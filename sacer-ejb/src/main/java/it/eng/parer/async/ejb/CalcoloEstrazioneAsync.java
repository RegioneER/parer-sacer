/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.async.ejb;

import it.eng.parer.async.helper.CalcoloEstrazioneHelper;
import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.entity.AroIndiceAipUd;
import it.eng.parer.entity.AroRichiestaRa;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.AroAipRaTipologiaOggetto;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.viewEntity.AroVChkRaUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "CalcoloEstrazioneAsync")
@LocalBean
@Interceptors({ it.eng.parer.async.aop.LockInterceptor.class, it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloEstrazioneAsync {

    Logger log = LoggerFactory.getLogger(CalcoloEstrazioneAsync.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");

    @Resource
    private SessionContext context;

    @EJB
    private CalcoloEstrazioneHelper calcoloHelper;

    @EJB
    private ConfigurationHelper configurationHelper;

    public boolean eseguiRichiestaAsync() {
        try {
            /* Determino la directory root $ROOT_FOLDER_EC_RA */
            String rootFolderEcRaPath = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.ROOT_FOLDER_EC_RA);
            // String rootFolderEcRaPath = "/tmp";

            log.info("Creazione automatica richieste di estrazione...");
            // ricavo le strutture
            List<OrgStrut> strutture = calcoloHelper.retrieveStrutture();
            log.debug("numero strutture trovate = " + strutture.size());

            CalcoloEstrazioneAsync newCalcoloEstrazioneAsyncRef1 = context
                    .getBusinessObject(CalcoloEstrazioneAsync.class);
            for (OrgStrut struttura : strutture) {
                // per il TEST: creo una richiesta per una struttura prima dell'esecuzione del JOB
                if (struttura.getIdStrut() == 3323) {
                    log.debug("processo struttura: " + struttura.getIdStrut());
                    newCalcoloEstrazioneAsyncRef1.manageRichiestaByStrut(struttura.getIdStrut(), rootFolderEcRaPath);
                }
            }

            return true;

        } catch (Exception e) {
            // INUTILI in quanto intercettati
        }

        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageRichiestaByStrut(long idStrut, String rootFolderEcRaPath) throws Exception {
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(new BigDecimal(idStrut));
        // ricavo l'ente convenzionato
        SIOrgEnteSiam enteConvenz = calcoloHelper.retrieveOrgEnteConvenzById(struttura.getIdEnteConvenz());
        /* Percorso della directory $NM_ENTE_CONVENZIONATO, figlia di $ROOT_FOLDER_EC_RA */
        String childFolderEcRaPath = IOUtils.getPath(rootFolderEcRaPath,
                enteConvenz.getNmEnteSiam().toUpperCase().replaceAll(" ", "_"));
        /* Controllo la directory */
        if (!IOUtils.isFileReady(childFolderEcRaPath)) {
            log.debug(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi in lettura/scrittura");
            throw new ParerInternalError(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi in lettura/scrittura");
        }
        /* Controllo se esistono richieste di estrazioni appartenenti all'ente convenzionati della struttura corrente */
        if (calcoloHelper.checkRichiestaInCoda(struttura.getIdEnteConvenz())) {
            log.debug("Estrazione già in coda per l'ente convenzionato della struttura '" + struttura.getNmStrut());
            /*
             * Ritorno un messaggio di errore che dice che l’estrazione è in coda (non dovrebbe mai succedere in quanto
             * la richiesta dovrebbe essere inibita da UI, doppio controllo)
             */
            throw new ParerInternalError(
                    "Estrazione già in coda per l'ente convenzionato della struttura '" + struttura.getNmStrut());
        }
        // gestisco le richieste scadute
        log.info("Struttura: id ='" + idStrut + "' nome = '" + struttura.getNmStrut() + "'");
        elaboraRichiesteScadute(idStrut);

        AroRichiestaRa newRichiestaRa = generaRichiestaRa(idStrut, rootFolderEcRaPath, BigDecimal.ONE);
        if (!newRichiestaRa.getTiStato().equals(AroRichiestaTiStato.ANNULLATO)) {
            // Lancio funzione asincrona calcolo AIP da estrarre
            CalcoloEstrazioneAsync newCalcoloEstrazioneAsyncRef1 = context
                    .getBusinessObject(CalcoloEstrazioneAsync.class);
            newCalcoloEstrazioneAsyncRef1.creaRichiestaEstrazioneAsync(newRichiestaRa.getIdRichiestaRa(), idStrut);
        }
    }

    public AroRichiestaRa generaRichiestaRa(long idStrut, String rootFolderEcRaPath, BigDecimal priorita)
            throws Exception {
        String cdErrore = "";
        // ricavo la struttura
        OrgStrut struttura = calcoloHelper.findById(OrgStrut.class, idStrut);
        // Il sistema logga l’inizio di una nuova richiesta di calcolo estrazione AIP sulla tabella ARO_RICHIESTA_RA
        AroRichiestaRa richiestaRa = calcoloHelper.writeAtomicAroRichiestaRa(idStrut, priorita);
        // Vista per verificare che tutte le ud, le serie e i fascicoli appartenenti all'ente convenzionato in capo alla
        // struttura abbiano i requisiti
        AroVChkRaUd chkRaUdView = calcoloHelper.findViewById(AroVChkRaUd.class, struttura.getIdEnteConvenz());
        if (chkRaUdView.getFlUdNonInElenco().equals("1")) {
            log.debug("Ud fuori da elenco di versamento - annullo la richiesta");
            cdErrore = "Ud fuori da elenco di versamento";
        } else if (chkRaUdView.getFlElencoNonCompletato().equals("1")) {
            log.debug("Versamento ud non completato - annullo la richiesta");
            cdErrore = "Versamento ud non completato";
        } else if (chkRaUdView.getFlElencoNonFirmato().equals("1")) {
            log.debug("Ud in elenco non firmato - annullo la richiesta");
            cdErrore = "Ud in elenco non firmato";
        } else if (chkRaUdView.getFlAipNonFirmato().equals("1")) {
            log.debug("Aip in elenco non firmato o non in archivio - annullo la richiesta");
            cdErrore = "Aip in elenco non firmato o non in archivio";
        }

        if (!cdErrore.isEmpty()) {
            setAnnullata(cdErrore, richiestaRa, struttura, "RICHIESTA ANNULLATA");
            calcoloHelper.mergeEntity(richiestaRa);
        }

        return richiestaRa;
    }

    @Asynchronous
    public void creaRichiestaEstrazioneAsync(long idRichiestaRa, long idStrut) throws Exception {
        boolean isAnnullata = false;

        AroRichiestaRa richiestaRa = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(new BigDecimal(idStrut));

        /* Determino le Unità Documentarie, Serie e Fascicoli di appartenenza dell'ente */
        List<UdSerFascObj> udSerFascObjectList = calcoloHelper.retrieveUdSerFascToProcess(struttura);
        log.debug("Trovati " + udSerFascObjectList.size() + " oggetti relativi alla struttura '"
                + struttura.getNmStrut() + "'");

        CalcoloEstrazioneAsync newCalcoloEstrazioneAsyncRef1 = context.getBusinessObject(CalcoloEstrazioneAsync.class);
        boolean isTheFirst = true;
        try {
            // Itero l'insieme
            Iterator<UdSerFascObj> i = udSerFascObjectList.iterator();
            while (i.hasNext()) {
                // Recupera l'elemento e sposta il cursore all'elemento successivo
                UdSerFascObj o = (UdSerFascObj) i.next();
                // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"), mi serve per capire se
                // è l'ultimo elemento
                newCalcoloEstrazioneAsyncRef1.manageUdSerFascObjFase1(richiestaRa.getIdRichiestaRa(), o, !i.hasNext(),
                        isTheFirst);
                /* Verifico se la richiesta corrente è stata annullata */
                isAnnullata = newCalcoloEstrazioneAsyncRef1.checkRichiestaAnnullata(richiestaRa.getIdRichiestaRa());
                if (isAnnullata) {
                    break;
                }
                isTheFirst = false;
            }
        } catch (ParerInternalError ex) {
            log.warn("Attenzione: possibile errore nell'elaborazione della richiesta");
        }

        if (!isAnnullata) {
            // Assegno alla richiesta stato = IN_ATTESA_ESTRAZIONE nella tabella ARO_RICHIESTA_RA definendo anche la
            // data di fine
            log.debug("Richiesta id = " + richiestaRa.getIdRichiestaRa() + " settata con stato "
                    + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name());
            newCalcoloEstrazioneAsyncRef1.setStatoRichiestaRaAtomicByStato(richiestaRa.getIdRichiestaRa(),
                    AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUdSerFascObjFase1(long idRichiestaRa, UdSerFascObj udSerFascObj, boolean isTheLast,
            boolean isTheFirst) throws Exception {

        switch (udSerFascObj.getTiEntitaSacer()) {
        case UNI_DOC:
            manageUdFase1(udSerFascObj.getId(), idRichiestaRa, isTheLast, isTheFirst);
            break;
        case SER:
            // manageSerFase1(udSerFascObj.getId(), idEnte, isTheLast, isTheFirst);
            break;
        case FASC:
            // manageFascFase1(udSerFascObj.getId(), idEnte, isTheLast, isTheFirst);
            break;
        }
    }

    public void manageUdFase1(BigDecimal idUd, long idRichiestaRa, boolean isTheLast, boolean isTheFirst)
            throws Exception {
        AroRichiestaRa richiestaRa = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        AroUnitaDoc ud = calcoloHelper.retrieveUnitaDocById(idUd.longValue());
        AroIndiceAipUd aroIndiceAipUd = calcoloHelper.retrieveIndiceAIPByIdUd(ud.getIdUnitaDoc()); // TODO: si potrebbe
                                                                                                   // mettere nella
                                                                                                   // vista

        /* Prendo il LOCK esclusivo su ud */
        calcoloHelper.lockUnitaDoc(ud);

        /*
         * Aggiungo l'indice aip unità documentaria corrente, assegnando stato = DA_ELABORARE e valorizzando la FK alla
         * richiesta corrente
         */
        calcoloHelper.writeAroAipRestituzioneArchivio(aroIndiceAipUd, richiestaRa, TiStatoAroAipRa.DA_ELABORARE,
                AroAipRaTipologiaOggetto.UD);

        /* Se l'elemento corrente è l'ultimo */
        // if (isTheLast) {
        // manageLast(struttura);
        // }
    }

    public void elaboraRichiesteScadute(long idStrut) throws Exception {
        log.debug("Controllo se ci sono richieste di estrazione scadute");
        // determino le richieste con stato diverso da ANNULLATO appartenenti all'ente convenzionato della struttura
        // corrente,
        // la cui occorrenza sulla ARO_RICHIESTA_RA sia con dt_fine == null e dt_inizio + 24h sia antecedente
        // all'istante corrente
        List<Long> richiesteScaduteDaProcessare = calcoloHelper.retrieveRichiesteScaduteDaProcessare(idStrut);
        CalcoloEstrazioneAsync newCalcoloEstrazioneAsyncRef1 = context.getBusinessObject(CalcoloEstrazioneAsync.class);

        log.info("trovate " + richiesteScaduteDaProcessare.size()
                + " richieste di estrazione scadute da settare in stato = ANNULLATO");
        for (Long richiestaId : richiesteScaduteDaProcessare) {
            log.debug("trovata richiesta " + richiestaId + " scaduta da settare in stato = ANNULLATO");
            newCalcoloEstrazioneAsyncRef1.setDaAnnullareAtomic("", richiestaId, idStrut, "RICHIESTA SCADUTA");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaAnnullareAtomic(String cdErrore, Long idRichiestaRa, long idStrut, String annullReason)
            throws Exception {
        log.debug("setDaAnnullareAtomic...");
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(new BigDecimal(idStrut));

        setAnnullata(cdErrore, richiesta, struttura, annullReason);
    }

    private void setAnnullata(String cdErrore, AroRichiestaRa richiesta, OrgStrut struttura, String annullReason)
            throws Exception {
        log.debug("setAnnullato...");
        // il sistema assegna alla richiesta stato = ANNULLATO nella tabella ARO_RICHIESTA_RA
        richiesta.setTiStato(AroRichiestaTiStato.ANNULLATO);
        log.debug("Richiesta id = " + richiesta.getIdRichiestaRa() + " settata con stato "
                + AroRichiestaTiStato.ANNULLATO.name() + " per '" + annullReason + "'");
        // il sistema definisce sulla richiesta la data di fine ed il motivo di annullamento pari a "Richiesta scaduta"
        Date systemDate = new Date();
        richiesta.setTsFine(systemDate);
        richiesta.setCdErrore(cdErrore);
        richiesta.setNote(annullReason);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setStatoRichiestaRaAtomicByStato(long idRichiestaRa, AroRichiestaTiStato tiStato) throws Exception {
        log.debug("setStatoRichiestaRaByStatoAtomic...");
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        setStatoRichiestaRaByStato(richiesta, tiStato);
    }

    private void setStatoRichiestaRaByStato(AroRichiestaRa richiesta, AroRichiestaTiStato tiStato) throws Exception {
        log.debug("setStatoRichiestaRaByStato...");
        Date systemDate = new Date();
        richiesta.setTsFine(systemDate);
        richiesta.setTiStato(tiStato);
    }

    // controllo se la richiesta è stata annullata
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean checkRichiestaAnnullata(long idRichiestaRa) {
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        Date actualDate = new Date();
        log.debug("Verifico se la richiesta '" + richiesta.getIdRichiestaRa() + "' con data inizio "
                + dateToString(richiesta.getTsInizio()) + " risulta annullata all'istante corrente ("
                + dateToString(actualDate) + ")");
        if (richiesta.getTiStato().equals(AroRichiestaTiStato.ANNULLATO)) {
            log.debug("Richiesta annullata");
            return true;
        } else {
            log.debug("Richiesta non annullata");
            return false;
        }
    }

    private String dateToString(Date date) {
        return dateFormat.format(date);
    }

}
