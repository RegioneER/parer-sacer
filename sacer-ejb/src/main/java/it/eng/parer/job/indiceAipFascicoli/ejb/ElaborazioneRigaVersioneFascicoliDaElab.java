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

package it.eng.parer.job.indiceAipFascicoli.ejb;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasAipFascicoloDaElab;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasStatoConservFascicolo;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.viewEntity.ElvVChkAllAipFascCreati;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceAipFascicoliDaElab")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaVersioneFascicoliDaElab {

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaVersioneFascicoliDaElab.class);
    @EJB
    private CreazioneIndiceAipFascicoliHelper ciafHelper;
    @EJB
    private ElaborazioneRigaIndiceMetaFascicoli elaborazioneMeta;
    @EJB
    private ElaborazioneRigaIndiceAipVersioneFascicoli elaborazioneAip;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    // MEV#30398
    @EJB
    private ObjectStorageService objectStorageService;
    // end MEV#30398

    // MEV#29589
    /*
     * Determino la modalità per effettuare la generazione dell'indice aip (default: FALSE)
     */
    private static final Boolean STRICT_MODE = Boolean.FALSE;
    /*
     * Determino la versione Unisincro di riferimento per la quale effettuare la generazione dell'indice aip (default:
     * v2.0)
     */
    private static final String UNISINCRO_V2_REF = "2.0";
    // end MEV#29589

    // MEV#26576
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciIndiceAipFascicoliDaElab(long idFascDaElab) throws Exception {
        FasAipFascicoloDaElab fascDaElab = ciafHelper.findFasAipFascicoloDaElab(idFascDaElab);
        // Se non trova il record esce e non fa nulla!
        if (fascDaElab != null) {
            BigDecimal idFascicolo = BigDecimal.valueOf(fascDaElab.getFasFascicolo().getIdFascicolo());
            FasXmlVersFascicolo xmlVersFascicolo = ciafHelper.getFasXmlVersFascicolo(idFascicolo.longValue(),
                    "RICHIESTA");
            gestisciIndiceAipFascicoliDaElab(fascDaElab, xmlVersFascicolo.getCdVersioneXml());
        }
    }
    // end MEV#26576

    public void gestisciIndiceAipFascicoliDaElab(FasAipFascicoloDaElab fascDaElab, String cdVersioneXml)
            throws Exception {

        // MEV #30398
        DecAaTipoFascicolo aaTipoFascicoloCorrente = ciafHelper.retrieveDecAaTipoFascicoloCorrente(
                fascDaElab.getFasFascicolo().getDecTipoFascicolo().getIdTipoFascicolo());
        BackendStorage backendIndiciAipFascicoli = objectStorageService.lookupBackendFasc(
                aaTipoFascicoloCorrente.getIdAaTipoFascicolo(), CostantiDB.ParametroAppl.BACKEND_INDICI_AIP_FASCICOLI);
        Map<String, String> indiciAipFascicoliBlob = new HashMap<>();
        // end MEV #30398

        String desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF;

        /* Recupero il fascicolo da elaborare */
        log.debug("{} - Elaboro il fascicolo {}", desJobMessage, fascDaElab.getFasFascicolo().getIdFascicolo());
        FasFascicolo fascicolo = ciafHelper.findById(FasFascicolo.class, fascDaElab.getFasFascicolo().getIdFascicolo());

        /* Lock esclusivo sul fascicolo */
        ciafHelper.lockFascicolo(fascicolo);

        /* Determino il progressivo di versione dell'indice AIP fascicolo e lo aumento di 1 */
        log.debug("{} - Ottengo il progressivo versione", desJobMessage);
        int progressivoVersione = ciafHelper.getProgressivoVersione(fascicolo.getIdFascicolo());
        progressivoVersione++;

        /* Determino il codice di versione dell'AIP fascicolo */
        String tiCreazione = fascDaElab.getTiCreazione();
        log.debug("{} - Ottengo il codice versione AIP", desJobMessage);

        /* Determino il codice di versione dell'AIP */
        String codiceVersione = ciafHelper.getVersioneAIP(fascicolo.getIdFascicolo(), tiCreazione);

        String codiceVersioneMetadati = ciafHelper.getVersioneMetadatiAIPV2(fascicolo.getIdFascicolo(), tiCreazione);

        /* Determino il sistema di conservazione */
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.CREATING_APPLICATION_PRODUCER);

        /* Persisto nelle varie tabelle di creazione dell'indice AIP fascicolo */
        log.debug("{} - Registro l'indice AIP", desJobMessage);
        FasVerAipFascicolo lastVer = ciafHelper.registraAIP(fascDaElab, progressivoVersione, codiceVersione,
                sistemaConservazione);

        /* Crea File fascicolo --> file FASCICOLO */
        log.debug("{} - Creo il file XML", desJobMessage);
        elaborazioneMeta.creaMetaVerFascicolo(lastVer.getIdVerAipFascicolo(), codiceVersioneMetadati,
                sistemaConservazione, cdVersioneXml, backendIndiciAipFascicoli, indiciAipFascicoliBlob);

        /* Crea indice AIP fascicolo --> file INDICE */
        log.debug("{} - Genero l'indice AIP", desJobMessage);
        elaborazioneAip.creaIndiceAipVerFascicolo(lastVer.getIdVerAipFascicolo(), codiceVersione,
                codiceVersioneMetadati, sistemaConservazione, creatingApplicationProducer, cdVersioneXml,
                backendIndiciAipFascicoli, indiciAipFascicoliBlob);

        // MEV #30398
        /*
         * Se backendIndiciAipFascicoli di tipo O.S. si effettua il salvataggio (con link su apposita entity)
         */
        if (backendIndiciAipFascicoli.isObjectStorage()) {
            // Creo gli oggetti per calcolare l'URN
            CSVersatore versatore = new CSVersatore();
            versatore.setSistemaConservazione(sistemaConservazione);
            versatore.setAmbiente(
                    fascDaElab.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
            versatore.setEnte(fascDaElab.getFasFascicolo().getOrgStrut().getOrgEnte().getNmEnte());
            versatore.setStruttura(fascDaElab.getFasFascicolo().getOrgStrut().getNmStrut());

            CSChiaveFasc chiaveFasc = new CSChiaveFasc();
            chiaveFasc.setAnno(fascDaElab.getFasFascicolo().getAaFascicolo().intValue());
            chiaveFasc.setNumero(fascDaElab.getFasFascicolo().getCdKeyFascicolo());

            // calculate normalized URN
            String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnFascicolo(
                    MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                    MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc, true, Costanti.UrnFormatter.FASC_FMT_STRING));

            ObjectStorageResource indiceAipFascSuOS = objectStorageService.createResourcesInIndiciAipFasc(tmpUrnNorm,
                    backendIndiciAipFascicoli.getBackendName(), indiciAipFascicoliBlob, lastVer.getIdVerAipFascicolo(),
                    BigDecimal.valueOf(fascDaElab.getFasFascicolo().getOrgStrut().getIdStrut()));
            log.debug("Salvati i file indice AIP fascicolo nel bucket {} con chiave {} ", indiceAipFascSuOS.getBucket(),
                    indiceAipFascSuOS.getKey());
        }
        // end MEV #30398

        // Aggiorno il fascicolo assegnando stato nell’elenco pari a IN_ELENCO_CON_AIP_CREATO
        if (fascicolo.getElvElencoVersFasc().getIdElencoVersFasc()
                .equals(fascDaElab.getElvElencoVersFasc().getIdElencoVersFasc())) {
            log.debug("{} - Aggiorno lo stato fascicolo a IN_ELENCO_CON_AIP_CREATO", desJobMessage);
            fascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_CON_AIP_CREATO);
        }

        // Registro un nuovo stato nell’elenco del fascicolo specificando stato = IN_ELENCO_CON_AIP_CREATO
        log.debug("{} - Registro un nuovo stato nell’elenco del fascicolo a IN_ELENCO_CON_AIP_CREATO", desJobMessage);
        FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
        statoFascicoloElenco.setFasFascicolo(fascicolo);
        statoFascicoloElenco.setTsStato(new Date());
        statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_CON_AIP_CREATO);

        fascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

        ciafHelper.insertEntity(statoFascicoloElenco, true);

        // Aggiorno il fascicolo assegnando stato di conservazione pari a AIP_GENERATO
        log.debug("{} - Aggiorno il fascicolo assegnando stato di conservazione pari a AIP_GENERATO", desJobMessage);
        fascicolo
                .setTiStatoConservazione(it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione.AIP_GENERATO);

        // Registro un nuovo stato di conservazione del fascicolo specificando stato = AIP_GENERATO
        log.debug("{} - Registro un nuovo stato di conservazione del fascicolo a AIP_GENERATO", desJobMessage);
        FasStatoConservFascicolo statoConservFascicolo = new FasStatoConservFascicolo();
        statoConservFascicolo.setFasFascicolo(fascicolo);
        statoConservFascicolo.setTsStato(new Date());
        statoConservFascicolo.setTiStatoConservazione(
                it.eng.parer.entity.constraint.FasStatoConservFascicolo.TiStatoConservazione.AIP_GENERATO);

        fascicolo.getFasStatoConservFascicoloElencos().add(statoConservFascicolo);

        ciafHelper.insertEntity(statoConservFascicolo, true);

        // Vista per verificare che tutti i fascicoli aggiunti appartenenti all'indice aip abbiano stato
        // IN_ELENCO_CON_AIP_CREATO
        ElvVChkAllAipFascCreati view = ciafHelper.findViewById(ElvVChkAllAipFascCreati.class,
                BigDecimal.valueOf(fascDaElab.getElvElencoVersFasc().getIdElencoVersFasc()));
        if (view.getFlAllAipCreati().equals("1")) {
            log.debug(
                    "{} - per l'elenco tutti i fascicoli hanno stato IN_ELENCO_CON_AIP_CREATO - registro un nuovo stato per l’elenco",
                    desJobMessage);

            // Registro un nuovo stato per l’elenco assegnando stato = AIP_CREATI
            ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
            statoElencoVersFasc.setElvElencoVersFasc(fascDaElab.getElvElencoVersFasc());
            statoElencoVersFasc.setTsStato(new Date());
            statoElencoVersFasc.setTiStato(TiStatoElencoFasc.AIP_CREATI);

            fascDaElab.getElvElencoVersFasc().getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

            /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
            Long idStatoElencoVersFasc = elencoHelper
                    .getStatoElencoByIdElencoVersFascStato(fascDaElab.getElvElencoVersFasc().getIdElencoVersFasc(),
                            TiStatoElencoFasc.AIP_CREATI)
                    .getIdStatoElencoVersFasc();
            fascDaElab.getElvElencoVersFasc().setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

            ElvElencoVersFascDaElab elencoDaElab = elencoHelper
                    .retrieveElencoInQueue(fascDaElab.getElvElencoVersFasc());
            elencoDaElab.setTiStato(TiStatoElencoFascDaElab.AIP_CREATI);
        }

        log.debug("{} - Elimino l'indice AIP dalla coda di elaborazione", desJobMessage);
        /* Elimino il record da quelli da elaborare */
        ciafHelper.eliminaIndiceAipDaElab(fascDaElab);
        log.debug("{} - Operazione di inserimento completata con successo", desJobMessage);
    }
}
