package it.eng.parer.elencoVersFascicoli.ejb;

import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.ElvVCreaIxElencoFasc;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class IndiceElencoVersFascJobEjb {

    Logger log = LoggerFactory.getLogger(IndiceElencoVersFascJobEjb.class);

    @EJB
    private IndiceElencoVersFascXsdEjb indiceEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @Resource
    private SessionContext context;

    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");

    public IndiceElencoVersFascJobEjb() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildIndex(LogJob logJob) throws Exception {
        log.info("Indice Elenco Versamento Fascicoli - Creazione automatica indici elenchi fascicoli...");
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

        // LinkedList<OrgStrut> strutture = new LinkedList<>();
        // strutture.add(elencoHelper.retrieveOrgStrutByid(new BigDecimal("3323")));
        // strutture.add(elencoHelper.retrieveOrgStrutByid(new BigDecimal("41")));
        for (OrgStrut struttura : strutture) {
            manageStrut(struttura.getIdStrut(), logJob.getIdLogJob());
        }
        jobHelper.writeLogJob(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(),
                ElencoEnums.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public void manageStrut(long idStruttura, long idLogJob) throws Exception {
        log.debug("Indice Elenco Versamento Fascicoli - manageStrut");
        BigDecimal idStrut = new BigDecimal(idStruttura);
        /*
         * Determino gli elenchi appartenenti alla struttura corrente, con stato DA_CHIUDERE (tabella
         * ELV_ELENCO_VERS_FASC_DA_ELAB)
         */
        List<Long> elenchiDaChiudere = elencoHelper.retrieveIdElenchiDaElaborare(idStrut,
                TiStatoElencoFascDaElab.DA_CHIUDERE);
        log.info("Indice Elenco Versamento Fascicoli - struttura id " + idStrut + ": trovati "
                + elenchiDaChiudere.size() + " elenchi DA_CHIUDERE da processare");

        /*
         * Se per la struttura versante la lista degli elenchi da chiudere non è vuota e se il parametro
         * VERIFICA_PARTIZIONI vale true
         */
        String verificaPartizioni = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        if (!elenchiDaChiudere.isEmpty() && Boolean.parseBoolean(verificaPartizioni)
                && struttureEjb.checkPartizioni(new BigDecimal(idStruttura), new Date(),
                        CostantiDB.TiPartition.FILE_ELENCO_VERS_FASC.name()).equals("0")
                && !struttureEjb.checkPartizioniDataCorDefinito(new BigDecimal(idStruttura))) {
            OrgStrut strut = elencoHelper.retrieveOrgStrutByid(idStrut);
            throw new ParerUserError("La partizione di tipo FILE_ELENCO_VERS_FASC per la data corrente e la struttura "
                    + strut.getOrgEnte().getOrgAmbiente().getNmAmbiente() + "-" + strut.getOrgEnte().getNmEnte() + "-"
                    + strut.getNmStrut() + " non è definita");
        }

        IndiceElencoVersFascJobEjb indiceElencoVersFascEjbRef1 = context
                .getBusinessObject(IndiceElencoVersFascJobEjb.class);
        for (Long idElenchi : elenchiDaChiudere) {
            indiceElencoVersFascEjbRef1.manageIndexAtomic(idElenchi, idStruttura, idLogJob);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageIndexAtomic(long idElenco, long idStruttura, long idLogJob) throws Exception {
        LogJob logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        ElvElencoVersFasc elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.lockElenco(elenco);
        // TODO: verificare, elencoHelper.writeLogElencoVers(elenco, struttura,
        // ElencoEnums.OpTypeEnum.CREA_INDICE_ELENCO.name(), logJob);
        manageIndex(elenco, struttura, logJob);
    }

    public void manageIndex(ElvElencoVersFasc elenco, OrgStrut struttura, LogJob logJob) throws Exception {
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();

        buildIndexFile(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        // registro un nuovo stato = CHIUSO e lo lascio nella coda degli elenchi da elaborare assegnando stato = CHIUSO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.CHIUSO);

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

        // aggiorno l’elenco da elaborare assegnando stato = CHIUSO
        ElvElencoVersFascDaElab elencoVersFascDaElab = elencoHelper.retrieveElencoInQueue(elenco);
        elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.CHIUSO);
        // il sistema aggiorna l’elenco specificando l’identificatore dello stato corrente
        Long idStatoElencoVersFasc = elencoHelper
                .retrieveStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.CHIUSO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));
        // registro un nuovo stato pari a IN_ELENCO_CHIUSO per ogni fascicolo appartenente all’elenco
        elencoHelper.setStatoFascicoloElenco(elenco, TiStatoFascElenco.IN_ELENCO_CHIUSO);
        // assegno ad ogni fascicolo appartenente all'elenco stato = IN_ELENCO_CHIUSO
        elencoHelper.setFasFascicoliStatus(elenco, TiStatoFascElencoVers.IN_ELENCO_CHIUSO);
        // TODO: verificare, elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
        // OpTypeEnum.CHIUSURA_ELENCO.name(), logJob);
    }

    public void buildIndexFile(ElvElencoVersFasc elenco, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm) throws Exception {
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        byte[] indexFile = null;
        // creo il file .xml
        log.info("Indice Elenco Versamento Fascicoli - creazione indice per elenco id '" + elenco.getIdElencoVersFasc()
                + "' appartenente alla struttura '" + elenco.getOrgStrut().getIdStrut() + "'");
        indexFile = indiceEjb.createIndex(elenco, false);
        // calcolo l'hash SHA-256 del file .xml
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile, TipiHash.SHA_256).toHexBinary();
        // costruisco l'urn dell'indice
        ElvVCreaIxElencoFasc creaIxElencoFasc = elencoHelper.findViewById(ElvVCreaIxElencoFasc.class,
                BigDecimal.valueOf(elenco.getIdElencoVersFasc()));
        // URN originale
        String urnXmlIndice = creaIxElencoFasc.getDsUrnIndiceElenco();
        // URN normalizzato
        String urnXmlIndiceNormaliz = creaIxElencoFasc.getDsUrnIndiceElencoNormaliz();
        // Registro il file Indice.xml (in ELV_FILE_ELENCO_VERS_FASC)
        // definendo l'hash dell'indice, l'algoritmo usato per il calcolo hash (=SHA-256),
        // l'encoding del hash (=hexBinary), la versione del XSD (=1.0) con cui è creato l'indice dell'elenco e
        // l'urn dell’indice “urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<id elenco>:Indice”
        elencoHelper.storeFileIntoElenco(elenco, indexFile, ElencoEnums.FileTypeEnum.INDICE_ELENCO.name(), new Date(),
                hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(), urnXmlIndice,
                urnXmlIndiceNormaliz, ElencoEnums.ElencoInfo.VERSIONE_ELENCO.message());
        // TODO: verificare, elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
        // OpTypeEnum.CREA_INDICE_ELENCO.name(), logJob);
    }

    public void calcolaUrnElenco(ElvElencoVersFasc elenco, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nomeSistema = configurationHelper.getValoreParamApplic("SISTEMA_CONSERVAZIONE", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        // Calcola URN originale
        String urnOriginale = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnte, nomeStruttura,
                sdf.format(elenco.getTsCreazioneElenco()), String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnElenco(urnOriginale);
        // Calcola URN normalizzato
        String urnNormalizzato = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnteNorm,
                nomeStrutturaNorm, sdf.format(elenco.getTsCreazioneElenco()),
                String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnNormalizElenco(urnNormalizzato);
    }

}
