package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasAipFascicoloDaElab;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasStatoConservFascicolo;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.viewEntity.ElvVChkAllAipFascCreati;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciIndiceAipFascicoliDaElab(long idFascDaElab)
            throws ParerInternalError, NamingException, NoSuchAlgorithmException, IOException, Exception {
        FasAipFascicoloDaElab fascDaElab = ciafHelper.findFasAipFascicoloDaElab(idFascDaElab);

        /* Recupero il fascicolo da elaborare */
        log.debug("Creazione Indice AIP Fascicoli - Elaboro il fascicolo "
                + fascDaElab.getFasFascicolo().getIdFascicolo());
        FasFascicolo fascicolo = ciafHelper.findById(FasFascicolo.class, fascDaElab.getFasFascicolo().getIdFascicolo());

        /* Lock esclusivo sul fascicolo */
        ciafHelper.lockFascicolo(fascicolo);

        /* Determino il progressivo di versione dell'indice AIP fascicolo e lo aumento di 1 */
        log.debug("Creazione Indice AIP Fascicoli - Ottengo il progressivo versione");
        int progressivoVersione = ciafHelper.getProgressivoVersione(fascicolo.getIdFascicolo());
        progressivoVersione++;

        /* Determino il codice di versione dell'AIP fascicolo */
        String tiCreazione = fascDaElab.getTiCreazione();
        log.debug("Creazione Indice AIP Fascicoli - Ottengo il progressivo versione AIP");
        String codiceVersione = ciafHelper.getVersioneAIP(fascicolo.getIdFascicolo(), tiCreazione);

        /* Determino il sistema di conservazione */
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);

        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper.getValoreParamApplic("CREATING_APPLICATION_PRODUCER",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);

        /* Persisto nelle varie tabelle di creazione dell'indice AIP fascicolo */
        log.debug("Creazione Indice AIP Fascicoli - Registro l'indice AIP");
        FasVerAipFascicolo lastVer = ciafHelper.registraAIP(fascDaElab, progressivoVersione, codiceVersione,
                sistemaConservazione);

        /* Crea File fascicolo */
        log.debug("Creazione Indice AIP Fascicoli - Creo il file XML");
        elaborazioneMeta.creaMetaVerFascicolo(lastVer.getIdVerAipFascicolo(), codiceVersione, sistemaConservazione);

        /* Crea indice AIP fascicolo */
        log.debug("Creazione Indice AIP Fascicoli - Genero l'indice AIP");
        elaborazioneAip.creaIndiceAipVerFascicolo(lastVer.getIdVerAipFascicolo(), codiceVersione, sistemaConservazione,
                creatingApplicationProducer);

        // Aggiorno il fascicolo assegnando stato nell’elenco pari a IN_ELENCO_CON_AIP_CREATO
        if (fascicolo.getElvElencoVersFasc().getIdElencoVersFasc() == fascDaElab.getElvElencoVersFasc()
                .getIdElencoVersFasc()) {
            log.debug("Creazione Indice AIP Fascicolo - Aggiorno lo stato fascicolo a IN_ELENCO_CON_AIP_CREATO");
            fascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_CON_AIP_CREATO);
        }

        // Registro un nuovo stato nell’elenco del fascicolo specificando stato = IN_ELENCO_CON_AIP_CREATO
        log.debug(
                "Creazione Indice AIP Fascicolo - Registro un nuovo stato nell’elenco del fascicolo a IN_ELENCO_CON_AIP_CREATO");
        FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
        statoFascicoloElenco.setFasFascicolo(fascicolo);
        statoFascicoloElenco.setTsStato(new Date());
        statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_CON_AIP_CREATO);

        fascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

        ciafHelper.insertEntity(statoFascicoloElenco, true);

        // Aggiorno il fascicolo assegnando stato di conservazione pari a AIP_GENERATO
        log.debug(
                "Creazione Indice AIP Fascicolo - Aggiorno il fascicolo assegnando stato di conservazione pari a AIP_GENERATO");
        fascicolo
                .setTiStatoConservazione(it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione.AIP_GENERATO);

        // Registro un nuovo stato di conservazione del fascicolo specificando stato = AIP_GENERATO
        log.debug(
                "Creazione Indice AIP Fascicolo - Registro un nuovo stato di conservazione del fascicolo a AIP_GENERATO");
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
                    "Creazione Indice AIP Fascicoli - per l'elenco tutti i fascicoli hanno stato IN_ELENCO_CON_AIP_CREATO - registro un nuovo stato per l’elenco");

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

        log.debug("Creazione Indice AIP Fascicoli - Elimino l'indice AIP dalla coda di elaborazione");
        /* Elimino il record da quelli da elaborare */
        ciafHelper.eliminaIndiceAipDaElab(fascDaElab);
        log.debug("Creazione Indice AIP Fascicoli - Operazione di inserimento completata con successo");
    }
}
