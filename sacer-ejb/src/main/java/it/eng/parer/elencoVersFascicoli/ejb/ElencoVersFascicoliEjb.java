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

package it.eng.parer.elencoVersFascicoli.ejb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObj;
import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObjComparatorAnnoTsVersFascicolo;
import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObjComparatorTsVersFascicolo;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.util.Constants;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElencoVersFascicoliEjb {

    public static final String LOG_EXPIRATION_DATE = "{} {}";
    public static final String LOG_AUMENTO_TEMPO = "Elenco Versamento Fascicoli - Aumento di {} {}. Scadenza = {}";
    Logger log = LoggerFactory.getLogger(ElencoVersFascicoliEjb.class);
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
    @Resource
    private SessionContext context;

    public ElencoVersFascicoliEjb() {
        // EMPTY
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildElencoVersFascicoli(LogJob logJob) throws Exception {
        log.info("Elenco Versamento Fascicoli - Creazione automatica elenchi versamento fascicoli...");
        // ricavo le strutture
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

        log.debug("Elenco Versamento Fascicoli - numero strutture trovate = {} ", strutture.size());
        for (OrgStrut struttura : strutture) {
            log.debug("Elenco Versamento Fascicoli - processo struttura: {}", struttura.getIdStrut());
            manageStrut(struttura.getIdStrut(), logJob);
        }

        jobHelper.writeLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(),
                ElencoEnums.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public void manageStrut(long idStruttura, LogJob logJob) throws Exception {
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // gestisco gli elenchi scaduti
        log.info("Elenco Versamento Fascicoli - Struttura: id ='{}' nome = '{}'", idStruttura, struttura.getNmStrut());
        elaboraElenchiScaduti(idStruttura, logJob.getIdLogJob());
        /*
         * determino tutti i criteri di raggruppamento fascicoli appartenenti alla struttura versante corrente, il cui
         * intervallo (data istituzione - data soppressione) includa la data corrente (con estremi compresi); i criteri
         * sono selezionati in ordine di data istituzione
         */
        List<DecCriterioRaggrFasc> criteriRaggrFasc = elencoHelper.retrieveCriterioByStrut(struttura,
                logJob.getDtRegLogJob());
        for (DecCriterioRaggrFasc criterio : criteriRaggrFasc) {
            log.debug(
                    "Elenco Versamento Fascicoli - Criterio della struttura '{}' trovato: nome criterio = '{}' (id = '{}')",
                    struttura.getNmStrut(), criterio.getNmCriterioRaggr(), criterio.getIdCriterioRaggrFasc());

            Comparator<FasFascicoloObj> comp = new FasFascicoloObjComparatorTsVersFascicolo();
            if (criterio.getAaFascicolo() == null && criterio.getAaFascicoloDa() == null
                    && criterio.getAaFascicoloA() == null) {
                comp = new FasFascicoloObjComparatorAnnoTsVersFascicolo();
            }

            /* Determino i fascicoli che soddisfano il criterio corrente */
            List<FasFascicoloObj> fasFascicoliObjectList = elencoHelper.retrieveFascicoliToProcess(criterio);
            log.debug("Elenco Versamento Fascicoli - Trovati {} fascicoli versati relativi al criterio '{}'",
                    fasFascicoliObjectList.size(), criterio.getNmCriterioRaggr());

            Collections.sort(fasFascicoliObjectList, comp);

            ElencoVersFascicoliEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersFascicoliEjb.class);
            boolean isTheFirst = true;
            try {
                // Itero l'insieme
                Iterator<FasFascicoloObj> i = fasFascicoliObjectList.iterator();
                while (i.hasNext()) {
                    // Recupera l'elemento e sposta il cursore all'elemento successivo
                    FasFascicoloObj o = i.next();
                    // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"), mi serve per capire
                    // se è l'ultimo elemento
                    newElencoEjbRef1.manageFasFascicoloObj(criterio.getIdCriterioRaggrFasc(), struttura.getIdStrut(),
                            logJob.getIdLogJob(), o, !i.hasNext(), isTheFirst);
                    isTheFirst = false;
                }
            } catch (ParerInternalError ex) {
                log.warn("Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
            }
        }

        /* Cambio stato ai fascicoli della struttura corrente, non selezionati dai criteri */
        elencoHelper.atomicSetNonElabSched(struttura, logJob);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageFasFascicoloObj(long idCriterio, long idStruttura, long idLogJob, FasFascicoloObj fasFascicoloObj,
            boolean isTheLast, boolean isTheFirst) throws Exception {
        if (fasFascicoloObj.getTiEntitaSacer() == Constants.TipoEntitaSacer.FASC) { // fascicolo
            manageFasFascicolo(fasFascicoloObj.getId(), fasFascicoloObj.getAaFascicolo(), idCriterio, idStruttura,
                    isTheFirst);
        }
    }

    public void manageFasFascicolo(BigDecimal fascicoloId, BigDecimal aaFascicolo, long idCriterio, long idStruttura,
            boolean isTheFirst) throws Exception {
        boolean isExpired = false;
        ElvElencoVersFasc elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        DecCriterioRaggrFasc criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        FasFascicolo ff = elencoHelper.retrieveFasFascicoloById(fascicoloId.longValue());
        // Controllo se il fascicolo è annullato
        boolean annullato = elencoHelper.checkFascicoloAnnullato(ff);
        if (!annullato) { // fascicolo non annullato
            /* a) Prendo il LOCK esclusivo su ff */
            elencoHelper.lockFasFascicolo(ff);
            /* b) Definisco elenco corrente */
            elenco = retrieveElenco(criterio, aaFascicolo, struttura, isTheFirst);
            /* d) Aggiungo fascicolo corrente all'elenco corrente */
            // ATTENZIONE: verifico se il numero di fascicoli versati presenti nell’elenco
            // e’ minore del numero massimo di fascicoli versati previsti dall’elenco
            // (tale numero è definito dal criterio usato)
            boolean firstCheckFascOk = elencoHelper.checkFreeSpaceElenco(elenco);
            if (firstCheckFascOk) { // il fascicolo sta nell'elenco: aggiungo.
                log.debug("Elenco Versamento Fascicoli - aggiungo il fascicolo '{}' all'elenco", ff.getIdFascicolo());
                /* Aggiunta fascicolo */
                addFascicoloIntoElenco(ff, elenco);
            } else {
                /* Chiusura elenco esaurito */
                setDaChiudere(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco);
                /* Creazione elenco per criterio */
                elenco = createElencoByCriterio(criterio, aaFascicolo, struttura); // questo volume è managed
                // Aggiugo fascicolo ad elenco solo dopo aver controllato se ci sta. Se non ci sta è un problema di
                // configurazione del criterio
                boolean secondCheckFascOk = elencoHelper.checkFreeSpaceElenco(elenco);
                if (secondCheckFascOk) { // il fascicolo sta nell'elenco: aggiungo.
                    log.debug("Elenco Versamento Fascicoli - aggiungo il fascicolo '{}' all'elenco",
                            ff.getIdFascicolo());
                    addFascicoloIntoElenco(ff, elenco);
                } else {
                    log.warn(
                            "Elenco Versamento Fascicoli - ATTENZIONE non è possibile aggiungere il fascicolo '{}' all'elenco. Possibile errore nella definizione del criterio",
                            ff.getIdFascicolo());
                    throw new ParerInternalError("ATTENZIONE non è possibile aggiungere il fascicolo '"
                            + ff.getIdFascicolo() + "' all'elenco. Possibile errore nella definizione del criterio");
                }
            }
            /* f) TODO: verificare, Verifico se l'elenco corrente è scaduto */
            isExpired = checkElencoExpired(elenco);
            if (isExpired) {
                // vedere se dare a closeReason scope piu ampio
                String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
                setDaChiudere(closeReason, elenco);
            }
        }
    }

    public ElvElencoVersFasc retrieveElenco(DecCriterioRaggrFasc criterio, BigDecimal aaFascicolo, OrgStrut struttura,
            boolean isTheFirst) {
        ElvElencoVersFasc elenco = findOpenedElenco(criterio, aaFascicolo, struttura, isTheFirst);
        if (elenco == null) {
            // non ci sono elenchi aperti quindi ne creo uno nuovo
            elenco = createNewElenco(criterio, aaFascicolo, struttura);
        }
        return elenco;
    }

    public ElvElencoVersFasc findOpenedElenco(DecCriterioRaggrFasc criterio, BigDecimal aaFascicolo, OrgStrut struttura,
            boolean isTheFirst) {
        ElvElencoVersFasc elenco = null;
        // Recupero l'elenco aperto per il criterio corrente
        try {
            elenco = elencoHelper.retrieveElencoByCriterio(criterio, aaFascicolo, struttura); // questo elenco è managed
            log.debug(
                    "Elenco Versamento Fascicoli - Elenco aperto trovato: id ={}; data di scadenza ={}; totale fascicoli versati = {}",
                    elenco.getIdElencoVersFasc(), dateToString(elenco.getDtScadChius()), elenco.getNiFascVersElenco());
            // Registro nel log solo se è il primo elemento, non ogni volta che passo di qua
            if (isTheFirst) {
                // TODO: verificare
                // elencoHelper.writeLogElencoVers(elenco, struttura,
                // ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_APERTO.name(), logJob
            }
        } catch (ParerNoResultException ex) {
            elenco = null;
        }
        return elenco;
    }

    public ElvElencoVersFasc createNewElenco(DecCriterioRaggrFasc criterio, BigDecimal aaFascicolo,
            OrgStrut struttura) {
        ElvElencoVersFasc elenco;
        log.debug("Elenco Versamento Fascicoli - Nessun elenco aperto trovato. Ne creo uno nuovo");
        elenco = createElencoByCriterio(criterio, aaFascicolo, struttura); // questo volume è managed
        return elenco;
    }

    private ElvElencoVersFasc createElencoByCriterio(DecCriterioRaggrFasc criterio, BigDecimal aaFascicolo,
            OrgStrut struttura) {
        log.debug("Elenco Versamento Fascicoli - Crea elenco da criterio");

        ElvElencoVersFasc elenco = new ElvElencoVersFasc();
        Date systemDate = new Date();

        boolean tuttiAnniFascicoloNulli = criterio.getAaFascicolo() == null && criterio.getAaFascicoloDa() == null
                && criterio.getAaFascicoloA() == null;
        if (tuttiAnniFascicoloNulli) {
            elenco.setAaFascicolo(aaFascicolo);
        }
        elenco.setOrgStrut(struttura);
        elenco.setTsCreazioneElenco(systemDate);
        elenco.setNiMaxFascCrit(criterio.getNiMaxFasc());
        elenco.setTiScadChiusCrit(criterio.getTiScadChius());
        elenco.setNiTempoScadChiusCrit(criterio.getNiTempoScadChius());
        elenco.setTiTempoScadChiusCrit(criterio.getTiTempoScadChius());
        elenco.setDecCriterioRaggrFasc(criterio);
        elenco.setNiFascVersElenco(BigDecimal.ZERO);

        // Calcola la data di scadenza dell'elenco
        Date expirationDate = calculateExpirationDate(elenco);
        elenco.setDtScadChius(expirationDate);
        // indicazione di elenco standard pari al valore specificato dal criterio
        elenco.setFlElencoStandard(criterio.getFlCriterioRaggrStandard());

        elencoHelper.getEntityManager().persist(elenco);

        /* Registro un record in ELV_STATO_ELENCO_VERS_FASC */
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(systemDate);
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.APERTO);
        elencoHelper.getEntityManager().persist(statoElencoVersFasc);

        List<ElvStatoElencoVersFasc> statoElencoVersFascList = new ArrayList<>();
        statoElencoVersFascList.add(statoElencoVersFasc);
        elenco.setElvStatoElencoVersFascicoli(statoElencoVersFascList);

        /* Registro l'elenco creato nella coda degli elenchi da elaborare ELV_ELENCO_VERS_FASC_DA_ELAB */
        ElvElencoVersFascDaElab elencoVersFascDaElab = new ElvElencoVersFascDaElab();
        elencoVersFascDaElab.setElvElencoVersFasc(elenco);
        elencoVersFascDaElab.setIdCriterioRaggrFasc(new BigDecimal(criterio.getIdCriterioRaggrFasc()));
        elencoVersFascDaElab.setIdStrut(new BigDecimal(struttura.getIdStrut()));
        elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.APERTO);
        if (tuttiAnniFascicoloNulli) {
            elencoVersFascDaElab.setAaFascicolo(aaFascicolo);
        }
        elencoHelper.getEntityManager().persist(elencoVersFascDaElab);

        List<ElvElencoVersFascDaElab> elencoVersFascDaElabList = new ArrayList<>();
        elencoVersFascDaElabList.add(elencoVersFascDaElab);
        elenco.setElvElencoVersFascDaElabs(elencoVersFascDaElabList);

        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = elencoHelper
                .retrieveStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.APERTO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        // TODO: verificare log
        log.debug("Elenco Versamento Fascicoli - Creato nuovo elenco: id = {}; data scadenza = {}",
                elenco.getIdElencoVersFasc(), dateToString(elenco.getDtScadChius()));
        return elenco;
    }

    private void addFascicoloIntoElenco(FasFascicolo ff, ElvElencoVersFasc elenco) {
        // aggiorno l'elenco corrente, incrementando di 1 il numero dei fascicoli versati inclusi nell'elenco
        elenco.setNiFascVersElenco(elenco.getNiFascVersElenco().add(BigDecimal.ONE));
        // registro un nuovo stato pari a IN_ELENCO_APERTO per il fascicolo corrente in FAS_STATO_FASCICOLO_ELENCO
        FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
        statoFascicoloElenco.setFasFascicolo(ff);
        statoFascicoloElenco.setTsStato(new Date());
        statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_APERTO);

        List<FasStatoFascicoloElenco> statoFascicoloElencoList = new ArrayList<>();
        statoFascicoloElencoList.add(statoFascicoloElenco);
        ff.setFasStatoFascicoloElencos(statoFascicoloElencoList);
        // aggiorno il fascicolo corrente, assegnando stato = IN_ELENCO_APERTO e valorizzando la FK all'elenco corrente
        ff.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_APERTO);
        ff.setElvElencoVersFasc(elenco);
        elenco.getFasFascicoli().add(ff);
        // elimino il fascicolo corrente dalla coda dei fascicoli da elaborare per gli elenchi
        elencoHelper.deleteFasFascicoloFromQueue(ff);
    }

    public void elaboraElenchiScaduti(long idStruttura, long logJobId) throws Exception {
        log.debug("Elenco Versamento Fascicoli - controllo se ci sono elenchi di versamento fascicoli scaduti");
        // determino gli elenchi con stato APERTO appartenenti alla struttura corrente,
        // la cui scadenza di chiusura sia antecedente all'istante corrente
        List<Long> elenchiDaProcessare = elencoHelper.retrieveElenchiDaProcessare(idStruttura);
        ElencoVersFascicoliEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersFascicoliEjb.class);

        log.info(
                "Elenco Versamento Fascicoli - trovati {} elenchi di versamento fascicoli scaduti da settare stato = DA_CHIUDERE",
                elenchiDaProcessare.size());
        for (Long elencoId : elenchiDaProcessare) {
            log.debug("Elenco Versamento Fascicoli - trovato elenco {} scaduto da settare stato = DA_CHIUDERE",
                    elencoId);
            newElencoEjbRef1.setDaChiudereAtomic(ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message(), elencoId,
                    idStruttura, logJobId);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaChiudereAtomic(String closeReason, Long idElenco, long idStruttura, long logJobId) {
        log.debug("Elenco Versamento Fascicoli - setDaChiudereAtomic...");
        ElvElencoVersFasc elenco = elencoHelper.retrieveElencoById(idElenco);
        setDaChiudere(closeReason, elenco);
    }

    private void setDaChiudere(String closeReason, ElvElencoVersFasc elenco) {
        log.debug("Elenco Versamento Fascicoli - setDaChiudere...");
        // il sistema registra un nuovo stato = DA_CHIUDERE nella tabella ELV_STATO_ELENCO_VERS_FASC
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.DA_CHIUDERE);

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        // il sistema assegna lo stato nella tabella ELV_ELENCO_VERS_FASC_DA_ELAB
        (elenco.getElvElencoVersFascDaElabs().get(0)).setTiStato(TiStatoElencoFascDaElab.DA_CHIUDERE);

        // il sistema aggiorna l’elenco specificando l’identificatore dello stato corrente
        Long idStatoElencoVersFasc = elencoHelper
                .retrieveStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.DA_CHIUDERE)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        log.debug("Elenco Versamento Fascicoli - Elenco id = {} settato con stato {} per '{}'",
                elenco.getIdElencoVersFasc(), TiStatoElencoFasc.DA_CHIUDERE.name(), closeReason);

        // il sistema definisce sull'elenco il motivo di chiusura pari a "Elenco scaduto"
        elenco.setDlMotivoChius(closeReason);

        List<FasFascicolo> fasFascicoloList = elencoHelper.retrieveFasFascicoliInElenco(elenco);
        for (FasFascicolo ff : fasFascicoloList) {
            // il sistema registra un nuovo stato pari a IN_ELENCO_DA_CHIUDERE per il fascicolo corrente in
            // FAS_STATO_FASCICOLO_ELENCO
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(ff);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_DA_CHIUDERE);

            ff.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
            // il sistema assegna ad ogni fascicolo appartenente all'elenco stato = IN_ELENCO_DA_CHIUDERE
            ff.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_DA_CHIUDERE);
            log.debug("Elenco Versamento Fascicoli - Assegnato a fascicolo '{}' lo stato {}", ff.getIdFascicolo(),
                    TiStatoFascElencoVers.IN_ELENCO_DA_CHIUDERE.name());
        }
    }

    private Date calculateExpirationDate(ElvElencoVersFasc elenco) {
        Date expirationDate = null;
        Date creationDate = elenco.getTsCreazioneElenco();
        log.debug("Elenco Versamento Fascicoli - Data di creazione {}", dateToString(creationDate));

        if (elenco.getTiScadChiusCrit() != null) {
            String tiScadChiusElenco = elenco.getTiScadChiusCrit();
            expirationDate = adjustElencoDateByTiScadChius(creationDate, tiScadChiusElenco);
        } else {
            String tiTempoScadChius = elenco.getTiTempoScadChiusCrit();
            BigDecimal niTempoScadChius = elenco.getNiTempoScadChiusCrit();
            expirationDate = adjustElencoDate(creationDate, tiTempoScadChius, niTempoScadChius,
                    ElencoEnums.ModeEnum.ADD.name());
        }
        log.debug("Elenco Versamento Fascicoli - Data di scadenza {}", dateToString(expirationDate));
        return expirationDate;
    }

    private Date adjustElencoDateByTiScadChius(Date creationDate, String tiScadChiusVolume) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(creationDate);
        Date expirationDate = null;
        log.debug("Elenco Versamento Fascicoli - Data di creazione {}", creationDate);
        if (ElencoEnums.ExpirationTypeEnum.GIORNALIERA.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del giorno di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);

            String newdate = dateformat.format(cal.getTime());
            log.debug(LOG_EXPIRATION_DATE, ElencoEnums.ExpirationTypeEnum.GIORNALIERA.name(), newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.SETTIMANALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana di creazione
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_YEAR, days);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(LOG_EXPIRATION_DATE, ElencoEnums.ExpirationTypeEnum.SETTIMANALE.name(), newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.QUINDICINALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana successiva a quella di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_WEEK, days);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            String newdate = dateformat.format(cal.getTime());
            log.debug(LOG_EXPIRATION_DATE, ElencoEnums.ExpirationTypeEnum.QUINDICINALE.name(), newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.MENSILE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del mese di creazione
            int actualMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, actualMaximum);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(LOG_EXPIRATION_DATE, ElencoEnums.ExpirationTypeEnum.MENSILE.name(), newdate);
        }
        expirationDate = cal.getTime();
        log.debug("Elenco Versamento Fascicoli - Nuova data di scadenza {}", expirationDate);
        return expirationDate;
    }

    private Date adjustElencoDate(Date plainDate, String tiTempo, BigDecimal niTempo, String opType) {
        int tempo = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(plainDate);
        if (tiTempo != null && niTempo != null) {
            if (ElencoEnums.ModeEnum.ADD.name().equals(opType)) {
                tempo = niTempo.intValue();
            } else if (ElencoEnums.ModeEnum.SUB.name().equals(opType)) {
                tempo = -(niTempo.intValue());
            }
            if (ElencoEnums.TimeTypeEnum.MINUTI.name().equals(tiTempo)) {
                cal.add(Calendar.MINUTE, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_AUMENTO_TEMPO, tempo, ElencoEnums.TimeTypeEnum.MINUTI.name(), newdate);
            }
            if (ElencoEnums.TimeTypeEnum.ORE.name().equals(tiTempo)) {
                cal.add(Calendar.HOUR_OF_DAY, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_AUMENTO_TEMPO, tempo, ElencoEnums.TimeTypeEnum.ORE.name(), newdate);
            }
            if (ElencoEnums.TimeTypeEnum.GIORNI.name().equals(tiTempo)) {
                cal.add(Calendar.DAY_OF_WEEK, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_AUMENTO_TEMPO, tempo, ElencoEnums.TimeTypeEnum.GIORNI.name(), newdate);
            }
        }
        return cal.getTime();
    }

    private String dateToString(Date date) {
        return dateformat.format(date);
    }

    // controllo se la data scadenza elenco è <= della sysdate
    private boolean checkElencoExpired(ElvElencoVersFasc elenco) {
        Date actualDate = new Date();
        log.debug(
                "Elenco Versamento Fascicoli - Verifico se l'elenco '{}' con data scadenza {} è scaduto all'istante corrente ({})",
                elenco.getIdElencoVersFasc(), dateToString(elenco.getDtScadChius()), dateToString(actualDate));
        if (actualDate.after(elenco.getDtScadChius())) {
            log.debug("Elenco Versamento Fascicoli - Elenco scaduto");
            return true;
        } else {
            log.debug("Elenco Versamento Fascicoli - Elenco non scaduto");
            return false;
        }
    }

}
