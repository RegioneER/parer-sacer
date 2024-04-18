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

import it.eng.parer.aipFascicoli.xml.usmainResp.IdCType;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PIndex;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceAipFascicoliUtil;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceAipFascicoliUtilV2;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.ParamIamHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;
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
    @EJB
    private ParamIamHelper paramIamHelper;

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceAipVersioneFascicoli.class);

    /* Ricavo i valori degli Agent dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParam = Arrays.asList(AGENT_PRESERVER_FORMALNAME, AGENT_PRESERVER_TAXCODE,
            AGENT_PRESERVATION_MNGR_TAXCODE, AGENT_PRESERVATION_MNGR_LASTNAME, AGENT_PRESERVATION_MNGR_FIRSTNAME);

    /* Ricavo i valori degli Agent v2.0 dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParamV2 = Arrays.asList();

    /* Ricavo i valori degli Agent v2.0 dalla tabella IAM_PARAM_APPLIC */
    private static final List<String> agentParamIamV2 = Arrays.asList(AGENT_HOLDER_RELEVANTDOCUMENT,
            AGENT_SUBMITTER_RELEVANTDOCUMENT);

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
    /*
     * Determino le versioni del servizio di versamento fascicolo per le quali forzare la generazione dell'indice aip
     * conforme alla versione Unisincro di riferimento (default: v1.0 e v1.1)
     */
    private static final List<String> FORZA_VERSIONI_XML_NOT_STRICT = Arrays.asList("1.0", "1.1");
    //
    private static final String LOG_ERR_CONF_PROFILO = "Il modello di tipo AIP_UNISYNCRO per la data corrente e l'ambiente %s non è definito per la versione %s del servizio di versamento";
    private static final String LOG_ERR_CONF_PROFILO_NOT_STRICT = "Il modello di tipo AIP_UNISYNCRO per la data corrente e l'ambiente %s non è definito";
    // end MEV#29589

    public void creaIndiceAipVerFascicolo(Long idVerAipFascicolo, String codiceVersione, String codiceVersioneMetadati,
            String sistemaConservazione, String creatingApplicationProducer, String cdVersioneXml)
            throws ParerInternalError, Exception {

        // MEV#29589
        // Se la modalità strict non è attiva la logica forza la generazione dell'indice aip conforme alla versione
        // Unisincro specificata dalla costante UNISINCRO_V2_REF
        // per le versioni del servizio di versamento fascicolo specificate dalla costante FORZA_VERSIONI_XML_NOT_STRICT
        String desJobMessage = "";
        if (STRICT_MODE.equals(Boolean.FALSE) && FORZA_VERSIONI_XML_NOT_STRICT.contains(cdVersioneXml)
                && UNISINCRO_V2_REF.compareTo(cdVersioneXml) > 0) {
            desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF + " (not strict)";
        } else {
            // MEV#26576
            desJobMessage = "Creazione Indice AIP Fascicoli v" + cdVersioneXml;
            // end MEV#26576
        }
        // end MEV#29589

        FasVerAipFascicolo verAipFascicolo = ciafHelper.findById(FasVerAipFascicolo.class, idVerAipFascicolo);
        long idAmbiente = verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();

        // MEV#29589
        // Se la modalità strict non è attiva la logica forza la generazione dell'indice aip conforme alla versione
        // Unisincro specificata dalla costante UNISINCRO_V2_REF
        // per le versioni del servizio di versamento fascicolo specificate dalla costante FORZA_VERSIONI_XML_NOT_STRICT
        String usoVersioneXml = (STRICT_MODE.equals(Boolean.FALSE)
                && FORZA_VERSIONI_XML_NOT_STRICT.contains(cdVersioneXml)
                && UNISINCRO_V2_REF.compareTo(cdVersioneXml) > 0) ? UNISINCRO_V2_REF : cdVersioneXml;
        // end MEV#29589

        /*
         * Determino il modello xsd attivo per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene
         * e per la versione del modello xsd corrispondente a quella del servizio di versamento fascicolo o a quella
         * forzata (MEV#29589), con il tipo "AIP_UNISYNCRO"
         */
        List<DecModelloXsdFascicolo> modelloAttivoList = ciafHelper.retrieveIdModelliDaElaborareV2(idAmbiente,
                CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name(), usoVersioneXml);

        log.info("{} - ambiente id {}: trovati {} modelli xsd attivi di tipo AIP_UNISYNCRO da processare",
                desJobMessage, idAmbiente, modelloAttivoList.size());

        /* Se per l'ambiente il modello XSD non viene trovato */
        if (modelloAttivoList.isEmpty()) {
            // MEV#29589
            // Se la modalità strict non è attiva la logica forza la generazione dell'indice aip conforme alla versione
            // Unisincro specificata dalla costante UNISINCRO_V2_REF
            // per le versioni del servizio di versamento fascicolo specificate dalla costante
            // FORZA_VERSIONI_XML_NOT_STRICT
            String description = "";
            if (STRICT_MODE.equals(Boolean.FALSE) && FORZA_VERSIONI_XML_NOT_STRICT.contains(cdVersioneXml)
                    && UNISINCRO_V2_REF.compareTo(cdVersioneXml) > 0) {
                description = String.format(LOG_ERR_CONF_PROFILO_NOT_STRICT,
                        verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
            } else {
                description = String.format(LOG_ERR_CONF_PROFILO,
                        verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente(),
                        cdVersioneXml);
            }
            // end MEV#29589
            throw new ParerInternalError(description);
        }

        // TIP: qui mi aspetto sempre un modello e uno soltanto!!!
        for (DecModelloXsdFascicolo modello : modelloAttivoList) {
            manageIndex(idAmbiente, verAipFascicolo, modello, codiceVersione, codiceVersioneMetadati,
                    sistemaConservazione, creatingApplicationProducer, cdVersioneXml);
        }
    }

    public void manageIndex(long idAmbiente, FasVerAipFascicolo verAipFascicolo,
            DecModelloXsdFascicolo modelloUnisyncro, String codiceVersione, String codiceVersioneMetadati,
            String sistemaConservazione, String creatingApplicationProducer, String cdVersioneXml) throws Exception {

        // MEV#29589
        // Se la modalità strict non è attiva la logica forza la generazione dell'indice aip conforme alla versione
        // Unisincro specificata dalla costante UNISINCRO_V2_REF
        // per le versioni del servizio di versamento fascicolo specificate dalla costante FORZA_VERSIONI_XML_NOT_STRICT
        String desJobMessage = "";
        if (STRICT_MODE.equals(Boolean.FALSE) && FORZA_VERSIONI_XML_NOT_STRICT.contains(cdVersioneXml)
                && UNISINCRO_V2_REF.compareTo(cdVersioneXml) > 0) {
            desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF + " (not strict)";
        } else {
            // MEV#26576
            desJobMessage = "Creazione Indice AIP Fascicoli v" + cdVersioneXml;
            // end MEV#26576
        }
        // end MEV#29589

        log.info("{} - Inizio creazione XML indice AIP per la versione fascicolo {}", desJobMessage,
                verAipFascicolo.getIdVerAipFascicolo());

        CSVersatore versatore = new CSVersatore();
        versatore.setSistemaConservazione(sistemaConservazione);
        versatore.setAmbiente(
                verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getNmEnte());
        versatore.setStruttura(verAipFascicolo.getFasFascicolo().getOrgStrut().getNmStrut());

        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(verAipFascicolo.getFasFascicolo().getAaFascicolo().intValue());
        chiaveFasc.setNumero(verAipFascicolo.getFasFascicolo().getCdKeyFascicolo());

        // MEV#26576
        /**
         * Generazione indice aip versione fascicolo
         */
        BigDecimal idAmbienteFas = BigDecimal
                .valueOf(verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
        BigDecimal idStrutFas = BigDecimal.valueOf(verAipFascicolo.getFasFascicolo().getOrgStrut().getIdStrut());
        SIOrgEnteSiam enteSiam = ciafHelper.findById(SIOrgEnteSiam.class,
                verAipFascicolo.getFasFascicolo().getOrgStrut().getIdEnteConvenz());
        StringWriter indexFile = null;
        // MEV#29589
        // Se la modalità strict non è attiva la logica forza la generazione dell'indice aip conforme alla versione
        // Unisincro specificata dalla costante UNISINCRO_V2_REF
        // per le versioni del servizio di versamento fascicolo specificate dalla costante FORZA_VERSIONI_XML_NOT_STRICT
        if (STRICT_MODE.equals(Boolean.FALSE) && FORZA_VERSIONI_XML_NOT_STRICT.contains(cdVersioneXml)
                && UNISINCRO_V2_REF.compareTo(cdVersioneXml) > 0) {

            Map<String, String> mappaAgent = confHelper.getParamApplicMapValue(agentParamV2, idAmbienteFas, idStrutFas,
                    null, null, CostantiDB.TipoAplVGetValAppart.STRUT);

            mappaAgent.putAll(paramIamHelper.getParamApplicMapValue(agentParamIamV2,
                    BigDecimal.valueOf(enteSiam.getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()),
                    BigDecimal.valueOf(enteSiam.getIdEnteSiam()), CostantiDB.TipoIamVGetValAppart.ENTECONVENZ));

            CreazioneIndiceAipFascicoliUtilV2 indiceAipFascicoliUtilV2 = new CreazioneIndiceAipFascicoliUtilV2();
            PIndex pindex = indiceAipFascicoliUtilV2.generaIndiceAIPV2NotStrict(verAipFascicolo, codiceVersione,
                    codiceVersioneMetadati, mappaAgent, sistemaConservazione, creatingApplicationProducer);

            log.debug("{} - Eseguo il marshalling del pindex", desJobMessage);
            indexFile = marshallPIndex(pindex);
        }
        // end MEV#29589
        else if (!"2.0".equals(cdVersioneXml)) {

            Map<String, String> mappaAgent = confHelper.getParamApplicMapValue(agentParam, idAmbienteFas, idStrutFas,
                    null, null, CostantiDB.TipoAplVGetValAppart.STRUT);

            CreazioneIndiceAipFascicoliUtil indiceAipFascicoliUtil = new CreazioneIndiceAipFascicoliUtil();
            IdCType idc = indiceAipFascicoliUtil.generaIndiceAIP(verAipFascicolo, codiceVersione, mappaAgent,
                    sistemaConservazione, enteSiam, creatingApplicationProducer);

            log.debug("{} - Eseguo il marshalling dell'idc", desJobMessage);
            indexFile = marshallIdC(idc);
        } else {

            Map<String, String> mappaAgent = confHelper.getParamApplicMapValue(agentParamV2, idAmbienteFas, idStrutFas,
                    null, null, CostantiDB.TipoAplVGetValAppart.STRUT);

            mappaAgent.putAll(paramIamHelper.getParamApplicMapValue(agentParamIamV2,
                    BigDecimal.valueOf(enteSiam.getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()),
                    BigDecimal.valueOf(enteSiam.getIdEnteSiam()), CostantiDB.TipoIamVGetValAppart.ENTECONVENZ));

            CreazioneIndiceAipFascicoliUtilV2 indiceAipFascicoliUtilV2 = new CreazioneIndiceAipFascicoliUtilV2();
            PIndex pindex = indiceAipFascicoliUtilV2.generaIndiceAIPV2Strict(verAipFascicolo, codiceVersione,
                    codiceVersioneMetadati, mappaAgent, sistemaConservazione, creatingApplicationProducer);

            log.debug("{} - Eseguo il marshalling del pindex", desJobMessage);
            indexFile = marshallPIndex(pindex);
        }
        // end MEV#26576

        // Eseguo la validazione dell'xml prodotto con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO
        log.info("{} - Eseguo validazione dell'xml con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO", desJobMessage);
        try {
            String xsd = modelloUnisyncro.getBlXsd();
            XmlUtils.validateXml(xsd, indexFile.toString());
            log.info("{} - Documento validato con successo", desJobMessage);
        } catch (SAXException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
        }

        // Calcolo l'hash SHA-256 del file .xml ed hexBinary
        log.debug("{} - Calcolo l'hash", desJobMessage);
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

    // MEV#26576
    private StringWriter marshallPIndex(PIndex pindex) throws JAXBException {
        it.eng.parer.aipFascicoli.xml.usmainRespV2.ObjectFactory objFctPIndex = new it.eng.parer.aipFascicoli.xml.usmainRespV2.ObjectFactory();
        JAXBElement<PIndex> elementPIndex = objFctPIndex.createPIndex(pindex);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getCreaIndiceAipFascicoloCtxV2().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascUniSincroV2());
        tmpMarshaller.marshal(elementPIndex, tmpWriter);
        return tmpWriter;
    }
    // end MEV#26576
}
