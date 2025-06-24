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
package it.eng.parer.job.indiceAipFascicoli.utils;

import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ChiaveType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ContenutoType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.DettaglioUnitaDocumentarieType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.Fascicolo;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.IntestazioneType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ObjectFactory;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ProfiloArchivisticoType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ProfiloGeneraleType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ProfiloNormativoType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.ProfiloSpecificoType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.VersatoreType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.SoggettoProduttoreType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.UnitaDocumentariaType;
import it.eng.parer.aipFascicoli.xml.usprofascRespV2.UnitaDocumentarieType;
import it.eng.parer.entity.FasXmlFascicolo;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceMetaFascicoliHelper;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author DiLorenzo_F
 */
public class CreazioneIndiceMetaFascicoliUtilV2 {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceMetaFascicoliUtilV2.class);

    private CreazioneIndiceMetaFascicoliHelper cimfHelper;

    private ObjectStorageService objectStorageService;

    public CreazioneIndiceMetaFascicoliUtilV2() throws NamingException {
        // Recupera l'ejb per la lettura di informazioni, se possibile
        cimfHelper = (CreazioneIndiceMetaFascicoliHelper) new InitialContext()
                .lookup("java:module/CreazioneIndiceMetaFascicoliHelper");
        objectStorageService = (ObjectStorageService) new InitialContext().lookup("java:module/ObjectStorageService");
    }

    public Fascicolo generaIndiceMetaFascicoloV2(FasVVisFascicolo creaMeta, String cdVersioneXml)
            throws DatatypeConfigurationException {
        Fascicolo indiceMetaFascicoloV2 = new Fascicolo();
        popolaIndiceMetaFascicoloV2(indiceMetaFascicoloV2, creaMeta, cdVersioneXml);
        return indiceMetaFascicoloV2;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void popolaIndiceMetaFascicoloV2(Fascicolo indiceMetaFascicoloV2, FasVVisFascicolo creaMeta,
            String cdVersioneXml) throws DatatypeConfigurationException {

        ObjectFactory objFct = new ObjectFactory();

        /**
         * ************************************* VERSIONE PROFILO COMPLETO FASCICOLO *
         * *************************************
         */
        indiceMetaFascicoloV2.setVersioneProfiloCompletoFascicolo(cdVersioneXml);

        /**
         * ************** INTESTAZIONE * **************
         */
        // Versatore
        VersatoreType versatore = new VersatoreType();
        versatore.setAmbiente(creaMeta.getNmAmbiente());
        versatore.setEnte(creaMeta.getNmEnte());
        versatore.setStruttura(creaMeta.getNmStrut());
        versatore.setUserID(creaMeta.getNmUserid());

        // TODO: verificare con Righi
        // Soggetto Produttore
        SoggettoProduttoreType soggettoProduttore = new SoggettoProduttoreType();
        soggettoProduttore.setAmbiente(creaMeta.getNmAmbienteEnteConvenz());
        soggettoProduttore.setCodice(creaMeta.getCdEnteConvenz());
        soggettoProduttore.setDenominazione(creaMeta.getNmEnteConvenz());

        // Chiave
        ChiaveType chiave = new ChiaveType();
        chiave.setAnno(creaMeta.getAaFascicolo().intValue());
        chiave.setNumero(creaMeta.getCdKeyFascicolo());

        // Tipo Fascicolo
        String nmTipoFascicolo = creaMeta.getNmTipoFascicolo();

        IntestazioneType intestazione = new IntestazioneType();
        intestazione.setVersatore(versatore);
        intestazione.setSoggettoProduttore(soggettoProduttore);
        intestazione.setChiave(chiave);
        intestazione.setTipoFascicolo(nmTipoFascicolo);

        indiceMetaFascicoloV2.setIntestazione(intestazione);

        // MAC #32985
        // Ricavo la mappa dei profili fascicolo
        Map<String, String> xmls = objectStorageService.getObjectXmlFascicolo(creaMeta.getIdFascicolo().longValue());

        /**
         * ****************** PROFILO GENERALE * ******************
         */
        indiceMetaFascicoloV2
                .setProfiloGenerale(this.caricaProfiloGeneraleFascicolo(creaMeta.getIdFascicolo().longValue(), xmls));

        /**
         * ********************** PROFILO ARCHIVISTICO * **********************
         */
        ProfiloArchivisticoType profiloArchivistico = this
                .caricaProfiloArchivisticoFascicolo(creaMeta.getIdFascicolo().longValue(), xmls);
        if (profiloArchivistico != null) {
            indiceMetaFascicoloV2
                    .setProfiloArchivistico(objFct.createFascicoloProfiloArchivistico(profiloArchivistico));
        }

        /**
         * ******************* PROFILO NORMATIVO * *******************
         */
        ProfiloNormativoType profiloNormativo = this
                .caricaProfiloNormativoFascicolo(creaMeta.getIdFascicolo().longValue(), xmls);
        if (profiloNormativo != null) {
            indiceMetaFascicoloV2.setProfiloNormativo(objFct.createFascicoloProfiloNormativo(profiloNormativo));
        }

        /**
         * ******************* PROFILO SPECIFICO * *******************
         */
        ProfiloSpecificoType tmpProfiloSpecifico = this
                .caricaProfiloSpecificoFascicolo(creaMeta.getIdFascicolo().longValue(), xmls);
        if (tmpProfiloSpecifico != null && tmpProfiloSpecifico.getAny() != null
                && !tmpProfiloSpecifico.getAny().isEmpty()) {
            ProfiloSpecificoType o = new ProfiloSpecificoType();
            o.getAny().addAll(tmpProfiloSpecifico.getAny());
            o.setVersione(tmpProfiloSpecifico.getVersione());
            indiceMetaFascicoloV2.setProfiloSpecifico(objFct.createFascicoloProfiloSpecifico(o));
        }

        /**
         * ********************* UNITA' DOCUMENTARIE * *********************
         */
        UnitaDocumentarieType unitaDocumentarie = new UnitaDocumentarieType();

        /*
         * DETTAGLIO UNITA' DOCUMENTARIA
         */
        List<FasVLisUdInFasc> fasVLisUdInFascList = cimfHelper.getFasVLisUdInFasc(creaMeta.getIdFascicolo().longValue(),
                creaMeta.getIdUserIamVers().longValue());
        DettaglioUnitaDocumentarieType dettaglioUnitaDocumentarie = new DettaglioUnitaDocumentarieType();
        for (FasVLisUdInFasc fasVLisUdInFasc : fasVLisUdInFascList) {
            UnitaDocumentariaType unitaDocumentaria = new UnitaDocumentariaType();
            if (fasVLisUdInFasc.getCdRegistroKeyUnitaDoc() != null && fasVLisUdInFasc.getAaKeyUnitaDoc() != null
                    && fasVLisUdInFasc.getCdKeyUnitaDoc() != null) {
                unitaDocumentaria.setRegistro(fasVLisUdInFasc.getCdRegistroKeyUnitaDoc());
                unitaDocumentaria.setAnno(fasVLisUdInFasc.getAaKeyUnitaDoc().intValue());
                unitaDocumentaria.setNumero(fasVLisUdInFasc.getCdKeyUnitaDoc());
                dettaglioUnitaDocumentarie.getUnitaDocumentaria().add(unitaDocumentaria);
            }
        }
        unitaDocumentarie.setDettaglioUnitaDocumentarie(dettaglioUnitaDocumentarie);

        if (creaMeta.getNiUnitaDoc() != null) {
            unitaDocumentarie.setNumeroUnitaDocumentarie(creaMeta.getNiUnitaDoc().intValue());
        }

        /**
         * *********** FASCICOLI * ***********
         */
        // NON GESTITO AL MOMENTO
        /**
         * *********** CONTENUTO * ***********
         */
        ContenutoType contenuto = new ContenutoType();
        contenuto.setUnitaDocumentarie(unitaDocumentarie);

        indiceMetaFascicoloV2.setContenuto(contenuto);
    }

    private ProfiloGeneraleType caricaProfiloGeneraleFascicolo(long idFascicolo, Map<String, String> xmls) {
        ProfiloGeneraleType tmpProfiloGenerale = null;
        List<FasXmlFascicolo> lstXmlFascicolo = cimfHelper.leggiXmlVersamentiModelloXsdFascicolo(
                DecModelloXsdFascicolo.TiUsoModelloXsd.VERS,
                DecModelloXsdFascicolo.TiModelloXsd.PROFILO_GENERALE_FASCICOLO, idFascicolo);

        if (!lstXmlFascicolo.isEmpty()) {
            tmpProfiloGenerale = new ProfiloGeneraleType();

            try {
                // MAC #32985
                String blXml = lstXmlFascicolo.get(0).getBlXml();
                /*
                 * Se l'xml è vuoto
                 */
                if (blXml == null) {
                    // recupero oggetti da O.S. (se presenti)
                    if (!xmls.isEmpty()) {
                        blXml = xmls.get(DecModelloXsdFascicolo.TiModelloXsd.PROFILO_GENERALE_FASCICOLO.name());
                    }
                }
                // end MAC #32985

                tmpProfiloGenerale = JAXB.unmarshal(new StringReader(blXml), ProfiloGeneraleType.class);
                tmpProfiloGenerale.setVersione(lstXmlFascicolo.get(0).getDecModelloXsdFascicolo().getCdXsd());

            } catch (IllegalArgumentException ex) {
                log.error("Errore nel parsing dell'XML", ex);
                throw new RuntimeException("ERRORE nel parsing ", ex);
            }
        }

        return tmpProfiloGenerale;
    }

    private ProfiloArchivisticoType caricaProfiloArchivisticoFascicolo(long idFascicolo, Map<String, String> xmls) {
        ProfiloArchivisticoType tmpProfiloArchivistico = null;
        List<FasXmlFascicolo> lstXmlFascicolo = cimfHelper.leggiXmlVersamentiModelloXsdFascicolo(
                DecModelloXsdFascicolo.TiUsoModelloXsd.VERS,
                DecModelloXsdFascicolo.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO, idFascicolo);

        if (!lstXmlFascicolo.isEmpty()) {
            tmpProfiloArchivistico = new ProfiloArchivisticoType();

            // MAC #32985
            String blXml = lstXmlFascicolo.get(0).getBlXml();
            /*
             * Se l'xml è vuoto
             */
            if (blXml == null) {
                // recupero oggetti da O.S. (se presenti)
                if (!xmls.isEmpty()) {
                    blXml = xmls.get(DecModelloXsdFascicolo.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO.name());
                }
            }
            // end MAC #32985

            tmpProfiloArchivistico.setVersione(lstXmlFascicolo.get(0).getDecModelloXsdFascicolo().getCdXsd());

            try {
                Element el = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new ByteArrayInputStream(blXml.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
                tmpProfiloArchivistico.setAny(el);

            } catch (IllegalArgumentException | ParserConfigurationException | SAXException | IOException ex) {
                log.error("Errore nel parsing dell'XML", ex);
                throw new RuntimeException("ERRORE nel parsing ", ex);
            }
        }

        return tmpProfiloArchivistico;
    }

    private ProfiloNormativoType caricaProfiloNormativoFascicolo(long idFascicolo, Map<String, String> xmls) {
        ProfiloNormativoType tmpProfiloNormativo = null;
        List<FasXmlFascicolo> lstXmlFascicolo = cimfHelper.leggiXmlVersamentiModelloXsdFascicolo(
                DecModelloXsdFascicolo.TiUsoModelloXsd.VERS,
                DecModelloXsdFascicolo.TiModelloXsd.PROFILO_NORMATIVO_FASCICOLO, idFascicolo);

        if (!lstXmlFascicolo.isEmpty()) {
            tmpProfiloNormativo = new ProfiloNormativoType();

            // MAC #32985
            String blXml = lstXmlFascicolo.get(0).getBlXml();
            /*
             * Se l'xml è vuoto
             */
            if (blXml == null) {
                // recupero oggetti da O.S. (se presenti)
                if (!xmls.isEmpty()) {
                    blXml = xmls.get(DecModelloXsdFascicolo.TiModelloXsd.PROFILO_NORMATIVO_FASCICOLO.name());
                }
            }
            // end MAC #32985

            tmpProfiloNormativo.setVersione(lstXmlFascicolo.get(0).getDecModelloXsdFascicolo().getCdXsd());

            try {
                Element el = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new ByteArrayInputStream(blXml.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
                tmpProfiloNormativo.setAny(el);

            } catch (IllegalArgumentException | ParserConfigurationException | SAXException | IOException ex) {
                log.error("Errore nel parsing dell'XML", ex);
                throw new RuntimeException("ERRORE nel parsing ", ex);
            }
        }

        return tmpProfiloNormativo;
    }

    private ProfiloSpecificoType caricaProfiloSpecificoFascicolo(long idFascicolo, Map<String, String> xmls) {
        ProfiloSpecificoType tmpProfiloSpecifico = null;
        List<FasXmlFascicolo> lstXmlFascicolo = cimfHelper.leggiXmlVersamentiModelloXsdFascicolo(
                DecModelloXsdFascicolo.TiUsoModelloXsd.VERS,
                DecModelloXsdFascicolo.TiModelloXsd.PROFILO_SPECIFICO_FASCICOLO, idFascicolo);

        if (!lstXmlFascicolo.isEmpty()) {
            // MAC #32985
            String blXml = lstXmlFascicolo.get(0).getBlXml();
            /*
             * Se l'xml è vuoto
             */
            if (blXml == null) {
                // recupero oggetti da O.S. (se presenti)
                if (!xmls.isEmpty()) {
                    blXml = xmls.get(DecModelloXsdFascicolo.TiModelloXsd.PROFILO_SPECIFICO_FASCICOLO.name());
                }
            }
            // end MAC #32985

            DocumentBuilder db = null;
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
                // almost all XML entity attacks are prevented
                final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
                dbf.setFeature(FEATURE, true);
                dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

                dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                // ... and these as well, per Timothy Morgan's 2014 paper:
                // "XML Schema, DTD, and Entity Attacks" (see reference below)
                dbf.setXIncludeAware(false);
                dbf.setExpandEntityReferences(false);
                // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central mechanism that
                // will
                // help you safeguard XML processing. It instructs XML processors, such as parsers, validators,
                // and transformers, to try and process XML securely, and the FSP can be used as an alternative to
                // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
                // Exists from JDK6.
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                // ... and, per Timothy Morgan:
                // "If for some reason support for inline DOCTYPEs are a requirement, then
                // ensure the entity settings are disabled (as shown above) and beware that SSRF
                // attacks
                // (http://cwe.mitre.org/data/definitions/918.html) and denial
                // of service attacks (such as billion laughs or decompression bombs via "jar:")
                // are a risk."
                db = dbf.newDocumentBuilder();
                String blXmlProfiloSpec = blXml;
                byte[] xml = blXmlProfiloSpec.getBytes(StandardCharsets.UTF_8);
                InputSource is = new InputSource(new StringReader(new String(xml, StandardCharsets.UTF_8)));
                Document docxml = db.parse(is);
                XPath xPath = XPathFactory.newInstance().newXPath();
                String queryXml = "//ProfiloSpecifico/@versione";
                XPathExpression expr = xPath.compile(queryXml);
                String versioneProfiloSpec = expr.evaluate(docxml);
                queryXml = "//ProfiloSpecifico/*";
                expr = xPath.compile(queryXml);
                NodeList nodeList = (NodeList) expr.evaluate(docxml, XPathConstants.NODESET);

                tmpProfiloSpecifico = new ProfiloSpecificoType();
                tmpProfiloSpecifico.setVersione(versioneProfiloSpec);
                Document doc = db.newDocument();
                for (int idx = 0; idx < nodeList.getLength(); idx++) {
                    Node node = nodeList.item(idx);
                    String name = node.getNodeName();
                    String value = node.getTextContent();

                    Element el = doc.createElement(name);
                    el.insertBefore(doc.createTextNode(value != null ? value : ""), el.getLastChild());
                    tmpProfiloSpecifico.getAny().add(el);
                }

            } catch (IllegalArgumentException | ParserConfigurationException | XPathExpressionException | SAXException
                    | IOException ex) {
                log.error("Errore nel parsing dell'XML", ex);
                throw new RuntimeException("ERRORE nel parsing ", ex);
            }
        }

        return tmpProfiloSpecifico;
    }

}
