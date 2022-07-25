package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.aipFascicoli.xml.usmainResp.IdCType;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceAipFascicoliUtil;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import it.eng.parer.xml.utils.XmlUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceAipVersioneFascicoli")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceAipVersioneFascicoli {

    @EJB
    private CreazioneIndiceAipFascicoliHelper ciafHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ConfigurationHelper confHelper;

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceAipVersioneFascicoli.class);

    /* Ricavo i valori degli Agent dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParam = Arrays.asList(AGENT_PRESERVER_FORMALNAME, AGENT_PRESERVER_TAXCODE,
            AGENT_PRESERVATION_MNGR_TAXCODE, AGENT_PRESERVATION_MNGR_LASTNAME, AGENT_PRESERVATION_MNGR_FIRSTNAME);

    public void creaIndiceAipVerFascicolo(Long idVerAipFascicolo, String codiceVersione, String sistemaConservazione,
            String creatingApplicationProducer) throws ParerInternalError, Exception {
        FasVerAipFascicolo verAipFascicolo = ciafHelper.findById(FasVerAipFascicolo.class, idVerAipFascicolo);
        long idAmbiente = verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();

        /*
         * Determino il modello xsd per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene, con il
         * tipo "AIP_UNISYNCRO"
         */
        List<DecModelloXsdFascicolo> modelloAttivoList = ciafHelper.retrieveIdModelliDaElaborare(idAmbiente,
                CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name());
        log.info("Creazione Indice AIP Fascicoli - ambiente id " + idAmbiente + ": trovati " + modelloAttivoList.size()
                + " modelli xsd attivi di tipo AIP_UNISYNCRO da processare");

        /* Se per l'ambiente il modello XSD non viene trovato */
        if (modelloAttivoList.isEmpty()) {
            throw new ParerInternalError("Il modello di tipo AIP_UNISYNCRO per la data corrente e l'ambiente "
                    + verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente()
                    + " non è definito");
        }

        // TIP: qui mi aspetto sempre un modello e uno soltanto!!!
        for (DecModelloXsdFascicolo modello : modelloAttivoList) {
            manageIndex(idAmbiente, verAipFascicolo, modello, codiceVersione, sistemaConservazione,
                    creatingApplicationProducer);
        }
    }

    public void manageIndex(long idAmbiente, FasVerAipFascicolo verAipFascicolo,
            DecModelloXsdFascicolo modelloUnisyncro, String codiceVersione, String sistemaConservazione,
            String creatingApplicationProducer) throws Exception {
        log.info("Creazione Indice AIP Fascicoli - Inizio creazione XML indice AIP per la versione fascicolo "
                + verAipFascicolo.getIdVerAipFascicolo());

        CSVersatore versatore = new CSVersatore();
        versatore.setSistemaConservazione(sistemaConservazione);
        versatore.setAmbiente(
                verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getNmEnte());
        versatore.setStruttura(verAipFascicolo.getFasFascicolo().getOrgStrut().getNmStrut());

        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(verAipFascicolo.getFasFascicolo().getAaFascicolo().intValue());
        chiaveFasc.setNumero(verAipFascicolo.getFasFascicolo().getCdKeyFascicolo());

        /**
         * Generazione indice aip versione fascicolo
         */
        // Map<String, String> mappaAgent = confHelper.getParamApplicMapValue(agentParam);
        BigDecimal idAmbienteFas = BigDecimal
                .valueOf(verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
        BigDecimal idStrutFas = BigDecimal.valueOf(verAipFascicolo.getFasFascicolo().getOrgStrut().getIdStrut());
        Map<String, String> mappaAgent = confHelper.getParamApplicMapValue(agentParam, idAmbienteFas, idStrutFas, null,
                null, CostantiDB.TipoAplVGetValAppart.STRUT);
        SIOrgEnteSiam enteSiam = ciafHelper.findById(SIOrgEnteSiam.class,
                verAipFascicolo.getFasFascicolo().getOrgStrut().getIdEnteConvenz());

        CreazioneIndiceAipFascicoliUtil indiceAipFascicoliUtil = new CreazioneIndiceAipFascicoliUtil();
        IdCType idc = indiceAipFascicoliUtil.generaIndiceAIP(verAipFascicolo, codiceVersione, mappaAgent,
                sistemaConservazione, enteSiam, creatingApplicationProducer);

        log.debug("Creazione Indice AIP Fascicoli - Eseguo il marshalling dell'idc");
        StringWriter indexFile = marshallIdC(idc);

        // Eseguo la validazione dell'xml prodotto con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO
        log.info("Eseguo validazione dell'xml con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO");
        try {
            String xsd = modelloUnisyncro.getBlXsd();
            XmlUtils.validateXml(xsd, indexFile.toString());
            log.info("Documento validato con successo");
        } catch (SAXException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
        }

        // Calcolo l'hash SHA-256 del file .xml ed hexBinary
        log.debug("Creazione Indice AIP Fascicoli - Calcolo l'hash");
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile.toString(), TipiHash.SHA_256)
                .toHexBinary();

        /*
         * Registro un record nella tabella FAS_META_VER_AIP_FASCICOLO definendo l'hash dell'indice, l'algoritmo usato
         * per il calcolo hash (=SHA-256) e l'encoding del hash (=hexBinary)
         */
        FasMetaVerAipFascicolo metaVerAipFascicolo = ciafHelper.registraFasMetaVerAipFascicolo(verAipFascicolo,
                hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(), codiceVersione,
                versatore, chiaveFasc);

        // Eseguo il salvataggio del clob del file nella tabella FAS_FILE_META_VER_AIP_FASC
        ciafHelper.registraFasFileMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(), indexFile.toString(),
                verAipFascicolo.getFasFascicolo().getOrgStrut(), new Date());

        /*
         * Inserisco i record nella tabella FAS_XSD_META_VER_AIP_FASC indicando il riferimento al modello di xsd
         * utilizzato per la costruzione delle more info self description e dell’indice aip
         */
        DecModelloXsdFascicolo modelloSelfD = ciafHelper
                .retrieveIdModelliDaElaborare(idAmbiente, CostantiDB.TiModelloXsd.AIP_SELF_DESCRIPTION_MORE_INFO.name())
                .get(0);
        // DecModelloXsdFascicolo modelloFileG = ciafHelper.retrieveIdModelliDaElaborare(idAmbiente,
        // CostantiDB.TiModelloXsd.FILE_GROUP_FILE_MORE_INFO.name()).get(0);
        ciafHelper.registraFasXsdMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
                modelloSelfD.getIdModelloXsdFascicolo(), "SelfDescription-" + modelloSelfD.getCdXsd());
        // ciafHelper.registraFasXsdMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
        // modelloFileG.getIdModelloXsdFascicolo(), "File-" + modelloFileG.getCdXsd());
        ciafHelper.registraFasXsdMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
                modelloUnisyncro.getIdModelloXsdFascicolo(), "Unisyncro-UNI_11386_2010");
    }

    private StringWriter marshallIdC(IdCType idc) throws JAXBException {
        it.eng.parer.aipFascicoli.xml.usmainResp.ObjectFactory objFct_IdCType = new it.eng.parer.aipFascicoli.xml.usmainResp.ObjectFactory();
        JAXBElement<IdCType> element_IdCType = objFct_IdCType.createIdC(idc);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getCreaIndiceAipFascicoloCtx().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascUniSincro());
        tmpMarshaller.marshal(element_IdCType, tmpWriter);
        return tmpWriter;
    }
}
