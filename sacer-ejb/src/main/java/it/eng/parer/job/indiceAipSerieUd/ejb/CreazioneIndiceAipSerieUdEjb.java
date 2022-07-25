package it.eng.parer.job.indiceAipSerieUd.ejb;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipSerieUdEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
/**
 * Classe contenente il metodo richiamato dal job per la creazione indice AIP versione serie ud
 *
 */
public class CreazioneIndiceAipSerieUdEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipSerieUdEjb.class);
    @EJB
    private ElaborazioneRigaVersioneSerieUdDaElab elaborazioneRigaIndiceAip;
    @EJB
    private CreazioneIndiceAipSerieUdHelper ciaHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private JobHelper jobHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAipSerieUd() throws Exception {
        log.info(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- Inizio transazione di creazione indice");
        List<OrgStrut> strutture = serieHelper.retrieveStrutture();

        // Per ogni struttura versante
        for (OrgStrut struttura : strutture) {
            manageStrut(new BigDecimal(struttura.getIdStrut()), struttura.getNmStrut(),
                    struttura.getOrgEnte().getNmEnte(), struttura.getOrgEnte().getOrgAmbiente().getNmAmbiente());
        }

        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP Serie Ud */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.info(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                + "--- Creazione Indice Aip Versione Serie Ud --- - Chiusura transazione di creazione indice");
    }

    public void manageStrut(BigDecimal idStrut, String nmStrut, String nmEnte, String nmAmbiente) throws Exception {
        /*
         * Se il parametro VERIFICA_PARTIZIONI vale true, il sistema verifica che siano definite le partizioni per la
         * data corrente per i volumi delle serie (tipo partizione = FILE_VOL_SERIE) e per il file dell?indice AIP delle
         * serie (tipo partizione = FILE_SERIE) della struttura corrente
         */
        String verificaPartizioni = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        if (Boolean.parseBoolean(verificaPartizioni)) {
            if (struttureEjb.checkPartizioni(idStrut, new Date(), CostantiDB.TiPartition.FILE_VOL_SER.name())
                    .equals("0")) {
                throw new ParerUserError("La partizione di tipo FILE_VOL_SER per la data corrente e la struttura "
                        + nmAmbiente + "-" + nmEnte + "-" + nmStrut + " non è definita");
            } else if (struttureEjb.checkPartizioni(idStrut, new Date(), CostantiDB.TiPartition.FILE_SER.name())
                    .equals("0")) {
                throw new ParerUserError("La partizione di tipo FILE_SER per la data corrente e la struttura "
                        + nmAmbiente + "-" + nmEnte + "-" + nmStrut + " non è definita");
            }
        }

        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper.getValoreParamApplic("CREATING_APPLICATION_PRODUCER",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);

        /*
         * Il sistema determina le versioni correnti delle serie con stato corrente = VALIDATA della struttura versante
         */
        List<SerVerSerieDaElab> verSerieDaElabList = ciaHelper.getSerVerSerieDaElab(idStrut,
                CostantiDB.StatoVersioneSerieDaElab.VALIDATA.name());
        log.info(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- Ottenute " + verSerieDaElabList.size() + " "
                + "versioni serie da elaborare per la struttura " + idStrut);

        /* Per ogni VERSIONE serie recuperata */
        try {
            for (SerVerSerieDaElab verSerieDaElab : verSerieDaElabList) {
                elaborazioneRigaIndiceAip.gestisciIndiceAipSerieUdDaElab(verSerieDaElab, creatingApplicationProducer);
            }
            log.info(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                    + " --- Creazione Indice Aip Versione Serie Ud --- Elaborate " + verSerieDaElabList.size() + " "
                    + "versioni serie con successo per la struttura " + idStrut);
        } catch (Exception ex) {
            // log.fatal(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
            // + "--- Creazione Indice Aip Versione Serie Ud --- Errore: "
            // + ExceptionUtils.getRootCauseMessage(ex));
            log.error(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                    + "--- Creazione Indice Aip Versione Serie Ud --- Errore: "
                    + ExceptionUtils.getRootCauseMessage(ex));
            throw new ParerInternalError(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                    + "--- Creazione Indice Aip Versione Serie Ud --- Errore: "
                    + ExceptionUtils.getRootCauseMessage(ex));
        }
    }

}
