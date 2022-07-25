package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAipFascicoli.elenchi.ElaborazioneElencoIndiceAipFascicoli;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.utils.CostantiDB;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "CreazioneIndiceAipFascicoliEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipFascicoliEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipFascicoliEjb.class);
    @EJB
    private CreazioneIndiceAipFascicoliHelper ciafHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaborazioneRigaVersioneFascicoliDaElab elaborazione;
    @EJB
    private ElaborazioneElencoIndiceAipFascicoli elabElencoIndiciAipFascicoli;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private StruttureHelper struttureHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAipFascicoli()
            throws ParerInternalError, NamingException, NoSuchAlgorithmException, Exception {
        /* Il sistema apre una nuova transazione */
        log.debug("Creazione Indice AIP Fascicoli - Inizio transazione di creazione indice");
        /* Reupero gli indici da elaborare */
        log.debug("Creazione Indice AIP Fascicoli - Recupero gli FasAipFascicoloDaElab ");
        List<Long> fascDaElabList = ciafHelper.getIndexFasAipFascicoloDaElab();
        log.info("Creazione Indice AIP Fascicoli - Ottenuti " + fascDaElabList.size() + " indici AIP da elaborare");

        /* Per ogni fascicolo presente nella coda */
        try {
            for (Long fascDaElab : fascDaElabList) {
                elaborazione.gestisciIndiceAipFascicoliDaElab(fascDaElab);
            }
        } catch (IOException | NamingException | NoSuchAlgorithmException ex) {
            // log.fatal("Creazione Indice AIP fascicoli - Errore: " + ex);
            log.error("Creazione Indice AIP fascicoli - Errore: " + ex);
            throw new ParerInternalError(ex);
        }

        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP fascicoli */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP Fascicoli - Chiusura transazione di creazione indice");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneElenchiIndiceAipFascicoli(long idLogJob) throws ParerInternalError, IOException,
            DatatypeConfigurationException, JAXBException, ParseException, NoSuchAlgorithmException {
        List<Long> idElenchi = elencoHelper.retrieveElenchiIndiciAipFascicoliDaProcessare();
        Set<Long> struttureVerificate = new HashSet<>();
        if (!idElenchi.isEmpty()) {
            for (Long idElenco : idElenchi) {
                ElvElencoVersFasc elenco = ciafHelper.findById(ElvElencoVersFasc.class, idElenco);
                if (struttureVerificate.add(elenco.getOrgStrut().getIdStrut())) {
                    String checkPartizioni = struttureHelper.checkPartizioni(
                            BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()), new Date(),
                            CostantiDB.TiPartition.FILE_ELENCO_VERS_FASC.name());
                    String flPartFileEleVrsFascOk = struttureHelper
                            .partitionFileEleVersFascOK(BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
                    if ("0".equals(checkPartizioni) && "0".equals(flPartFileEleVrsFascOk)) {
                        OrgStrut strut = elenco.getOrgStrut();
                        OrgEnte ente = strut.getOrgEnte();
                        OrgAmbiente ambiente = ente.getOrgAmbiente();
                        String strutComposita = ambiente.getNmAmbiente() + "-" + ente.getNmEnte() + "-"
                                + strut.getNmStrut();
                        throw new ParerInternalError("La partizione di tipo "
                                + CostantiDB.TiPartition.FILE_ELENCO_VERS_FASC.name()
                                + " per la data corrente e la struttura " + strutComposita + " non \u00E8 definita");
                    }
                }
                elabElencoIndiciAipFascicoli.creaElencoIndiciAIPFascicoli(idElenco, idLogJob);
            }
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione elenchi indici AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP Fascicoli - Chiusura transazione di creazione elenchi indici AIP Fascicoli");
    }

}
