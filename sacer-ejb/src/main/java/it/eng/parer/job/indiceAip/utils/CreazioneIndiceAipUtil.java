package it.eng.parer.job.indiceAip.utils;

import it.eng.parer.entity.AroArchivSec;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroLinkUnitaDoc;
import it.eng.parer.entity.AroNotaUnitaDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdArchivSec;
import it.eng.parer.entity.AroUpdCompUnitaDoc;
import it.eng.parer.entity.AroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.AroUpdDocUnitaDoc;
import it.eng.parer.entity.AroUpdLinkUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroVersIniArchivSec;
import it.eng.parer.entity.AroVersIniComp;
import it.eng.parer.entity.AroVersIniDatiSpec;
import it.eng.parer.entity.AroVersIniDoc;
import it.eng.parer.entity.AroVersIniLinkUnitaDoc;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiUsoXsdAroVersIniDatiSpec;
import it.eng.parer.entity.AroVersIniUnitaDoc;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.constraint.AroXmlUpdUnitaDoc.TiXmlUpdUnitaDoc;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiUsoXsdAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.job.dto.SessioneVersamentoExt.DatiXml;
import it.eng.parer.job.indiceAip.helper.ControlliRecIndiceAip;
import it.eng.parer.viewEntity.AroVVisCompAip;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaSacer;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.CostantiDB.TipiUsoDatiSpec;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.usdocResp.MetadatiIntegratiDocType;
import it.eng.parer.ws.xml.usdocResp.ProfiloDocumentoType;
import it.eng.parer.ws.xml.usfileResp.MetadatiIntegratiFileType;
import it.eng.parer.ws.xml.usfileResp.TipoSupportoType;
import it.eng.parer.ws.xml.usmainResp.AgentType;
import it.eng.parer.ws.xml.usmainResp.AgentNameType;
import it.eng.parer.ws.xml.usmainResp.AgentIDType;
import it.eng.parer.ws.xml.usmainResp.AttachedTimeStampType;
import it.eng.parer.ws.xml.usmainResp.CreatingApplicationType;
import it.eng.parer.ws.xml.usmainResp.DescriptionType;
import it.eng.parer.ws.xml.usmainResp.EmbeddedMetadataType;
import it.eng.parer.ws.xml.usmainResp.FileType;
import it.eng.parer.ws.xml.usmainResp.FileGroupType;
import it.eng.parer.ws.xml.usmainResp.HashType;
import it.eng.parer.ws.xml.usmainResp.IdentifierType;
import it.eng.parer.ws.xml.usmainResp.IdCType;
import it.eng.parer.ws.xml.usmainResp.LawAndRegulationsType;
import it.eng.parer.ws.xml.usmainResp.MoreInfoType;
import it.eng.parer.ws.xml.usmainResp.NameAndSurnameType;
import it.eng.parer.ws.xml.usmainResp.ProcessType;
import it.eng.parer.ws.xml.usmainResp.SelfDescriptionType;
import it.eng.parer.ws.xml.usmainResp.SourceIdCType;
import it.eng.parer.ws.xml.usmainResp.TimeReferenceType;
import it.eng.parer.ws.xml.usmainResp.VdCType;
import it.eng.parer.ws.xml.usmainResp.VdCGroupType;
import it.eng.parer.ws.xml.usselfdescResp.ContenutoType;
import it.eng.parer.ws.xml.usselfdescResp.ContenutoPacchettoArchiviazioneType;
import it.eng.parer.ws.xml.usselfdescResp.IndiceAIPType;
import it.eng.parer.ws.xml.usselfdescResp.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.ws.xml.usvdcResp.CamiciaFascicoloType;
import it.eng.parer.ws.xml.usvdcResp.ChiaveType;
import it.eng.parer.ws.xml.usvdcResp.ComposizioneType;
import it.eng.parer.ws.xml.usvdcResp.DatiSpecificiTypeVdC;
import it.eng.parer.ws.xml.usvdcResp.DocumentoCollegatoType;
import it.eng.parer.ws.xml.usvdcResp.FascicoloType;
import it.eng.parer.ws.xml.usvdcResp.MetadatiIntegratiPdAType;
import it.eng.parer.ws.xml.usvdcResp.NotaType;
import it.eng.parer.ws.xml.usvdcResp.ProfiloArchivisticoType;
import it.eng.parer.ws.xml.usvdcResp.ProfiloUnitaDocumentariaType;
import it.eng.parer.ws.xml.usvdcResp.VersatoreType;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Gilioli_P
 */
public class CreazioneIndiceAipUtil {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipUtil.class);
    private RispostaControlli rispostaControlli;
    private ControlliRecIndiceAip controlliRecIndiceAip;

    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliRecupero controlliRecupero = null;

    UnitaDocumentarieHelper unitaDocumentarieHelper = null;

    public CreazioneIndiceAipUtil() throws NamingException {
        rispostaControlli = new RispostaControlli();
        // Recupera l'ejb per la lettura di informazioni, se possibile
        controlliRecupero = (ControlliRecupero) new InitialContext().lookup("java:module/ControlliRecupero");
        controlliRecIndiceAip = (ControlliRecIndiceAip) new InitialContext()
                .lookup("java:module/ControlliRecIndiceAip");
        unitaDocumentarieHelper = (UnitaDocumentarieHelper) new InitialContext()
                .lookup("java:module/UnitaDocumentarieHelper");
    }

    private void setRispostaError() {
        // log.fatal("Creazione Indice AIP - Errore nella creazione dell'istanza di conservazione UniSyncro (IdC): "
        // + rispostaControlli.getDsErr());
        log.error("Creazione Indice AIP - Errore nella creazione dell'istanza di conservazione UniSyncro (IdC): "
                + rispostaControlli.getDsErr());
        throw new RuntimeException(rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr());
    }

    /**
     * Riceve l'unità documentaria da elaborare e crea l'IdC
     *
     * @param aro
     *            entity AroIndiceAipUdDaElab
     * @param codiceVersione
     *            codice versione
     * @param cdVersioneXSDIndiceAIP
     *            codice versione xsd
     * @param sistemaConservazione
     *            sistema conservazione
     * @param mappaAgenti
     *            mappa chiave/valore
     * @param creatingApplicationProducer
     *            producer
     * 
     * @return entity IdCType
     */
    public IdCType generaIndiceAIP(AroIndiceAipUdDaElab aro, String codiceVersione, String cdVersioneXSDIndiceAIP,
            String sistemaConservazione, Map<String, String> mappaAgenti, String creatingApplicationProducer) {
        IdCType istanzaUnisincro = new IdCType();
        List<SessioneVersamentoExt> sessioniVersamentoList = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAip(aro.getIdIndiceAipDaElab());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            sessioniVersamentoList = (List<SessioneVersamentoExt>) rispostaControlli.getrObject();
        }

        popolaIdC(istanzaUnisincro, aro.getAroUnitaDoc(), codiceVersione, sistemaConservazione, cdVersioneXSDIndiceAIP,
                sessioniVersamentoList, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    /**
     * Riceve l'unità documentaria e crea l'IdC
     *
     * @param aro
     *            entity AroUnitaDoc
     * @param codiceVersione
     *            codice versione
     * @param cdVersioneXSDIndiceAIP
     *            codice versione xsd
     * @param sistemaConservazione
     *            sistema conservazione
     * @param mappaAgenti
     *            mappa chiave/valore
     * @param creatingApplicationProducer
     *            producer
     * 
     * @return entity IdCType
     */
    public IdCType generaIndiceAIP(AroUnitaDoc aro, String codiceVersione, String cdVersioneXSDIndiceAIP,
            String sistemaConservazione, Map<String, String> mappaAgenti, String creatingApplicationProducer) {
        IdCType istanzaUnisincro = new IdCType();
        List<SessioneVersamentoExt> sessioniVersamentoList = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAipDaUnitaDoc(aro.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            sessioniVersamentoList = (List<SessioneVersamentoExt>) rispostaControlli.getrObject();
        }
        popolaIdC(istanzaUnisincro, aro, codiceVersione, sistemaConservazione, cdVersioneXSDIndiceAIP,
                sessioniVersamentoList, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    private void popolaIdC(IdCType idc, AroUnitaDoc aroUnitaDoc, String codiceVersione, String sistemaConservazione,
            String cdVersioneXSDIndiceAIP, List<SessioneVersamentoExt> sessioniVersamentoList,
            Map<String, String> mappaAgenti, String creatingApplicationProducer) {
        AroUnitaDoc tmpAroUnitaDoc = null;
        AroVersIniUnitaDoc tmpAroVersIniUnitaDoc = null;
        AroUpdUnitaDoc tmpAroUpdUnitaDocPgMax = null;
        Date timeRef = new GregorianCalendar().getTime();
        boolean isFileGroupNotEmpty = false;

        CSChiave csChiave = new CSChiave();
        csChiave.setAnno(aroUnitaDoc.getAaKeyUnitaDoc().longValue());
        csChiave.setNumero(aroUnitaDoc.getCdKeyUnitaDoc());
        csChiave.setTipoRegistro(aroUnitaDoc.getCdRegistroKeyUnitaDoc());

        CSVersatore csVersatore = new CSVersatore();
        csVersatore.setSistemaConservazione(sistemaConservazione);
        csVersatore.setAmbiente(aroUnitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        csVersatore.setEnte(aroUnitaDoc.getOrgStrut().getOrgEnte().getNmEnte());
        csVersatore.setStruttura(aroUnitaDoc.getOrgStrut().getNmStrut());

        long idUnitaDoc = aroUnitaDoc.getIdUnitaDoc();

        // Recupero l'unità documentaria
        rispostaControlli.reset();
        rispostaControlli = controlliRecupero.leggiUnitaDoc(idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            tmpAroUnitaDoc = (AroUnitaDoc) rispostaControlli.getrObject();
        }

        // Recupero il versamento iniziale degli aggiornamenti metadati all'unità documentaria
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiAroVersIniUnitaDoc(tmpAroUnitaDoc.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            tmpAroVersIniUnitaDoc = (AroVersIniUnitaDoc) rispostaControlli.getrObject();
        }

        // Recupero l'aggiornamento avente progressivo maggiore riferito all'unità documentaria
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiVersamentiAipUpdPgMaxInCoda(tmpAroUnitaDoc.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            tmpAroUpdUnitaDocPgMax = (AroUpdUnitaDoc) rispostaControlli.getrObject();
        }

        /*
         * ************************ DECORO SELFDESCRIPTION ************************
         */
        SelfDescriptionType selfie = new SelfDescriptionType();
        /* ID */
        IdentifierType id = new IdentifierType();
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(csVersatore),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave));
        // calcolo ORIGINALE
        String urn = MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrn, codiceVersione,
                Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2);
        id.setValue(urn);
        selfie.setID(id);

        /* Creating Application */
        CreatingApplicationType applicazione = new CreatingApplicationType();
        applicazione.setName("Sacer");
        applicazione.setProducer(creatingApplicationProducer);
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVersioneSacer();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            applicazione.setVersion(rispostaControlli.getrString());
        }
        selfie.setCreatingApplication(applicazione);

        /* Source IdC */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVersioniPrecedentiAIP(idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVerIndiceAipUd> versioniPrecedenti = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            List<SourceIdCType> sorgenteArray = new ArrayList<>();
            for (int i = 0; i < versioniPrecedenti.size(); i++) {
                SourceIdCType sorgente = new SourceIdCType();
                IdentifierType idSourceIdc = new IdentifierType();
                it.eng.parer.ws.xml.usmainResp.HashType hashSourceIdc = new it.eng.parer.ws.xml.usmainResp.HashType();
                AroUrnVerIndiceAipUd aroUrnAipIndiceAipUdOrigPrec = (AroUrnVerIndiceAipUd) CollectionUtils
                        .find(versioniPrecedenti.get(i).getAroUrnVerIndiceAipUds(), new Predicate() {
                            @Override
                            public boolean evaluate(final Object object) {
                                return ((AroUrnVerIndiceAipUd) object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE);
                            }
                        });
                idSourceIdc.setValue(aroUrnAipIndiceAipUdOrigPrec.getDsUrn());
                sorgente.setID(idSourceIdc);
                // MAC#18826
                // MAC#25224
                hashSourceIdc.setValue(versioniPrecedenti.get(i).getDsHashIndiceAip());
                // MAC#25654
                String function = versioniPrecedenti.get(i).getDsAlgoHashIndiceAip() != null
                        ? versioniPrecedenti.get(i).getDsAlgoHashIndiceAip() : "Non Definito";
                hashSourceIdc.setFunction(function);
                // end MAC#25654
                // end MAC#25224
                sorgente.setHash(hashSourceIdc);
                sorgenteArray.add(sorgente);
            }
            selfie.getSourceIdC().addAll(sorgenteArray);
        }

        /* More Info */
        MoreInfoType moreInfoApplic = new MoreInfoType();
        moreInfoApplic.setXMLScheme("Unisincro_MoreInfoSelfDescription_v1.1.xsd");
        EmbeddedMetadataType extraInfoDescGenerale = new EmbeddedMetadataType();
        MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
        IndiceAIPType indiceAIP = new IndiceAIPType();
        indiceAIP.setDataCreazione(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        indiceAIP.setFormato("UNI SInCRO 1.0 (UNI 11386:2010)");
        indiceAIP.setVersioneIndiceAIP(codiceVersione);
        indiceAIP.setVersioneXSDIndiceAIP(cdVersioneXSDIndiceAIP);
        miSelfD.setIndiceAIP(indiceAIP);

        // Contenuto Pacchetto Archiviazione
        ContenutoPacchettoArchiviazioneType contenutoPacchettoArchiviazione = new ContenutoPacchettoArchiviazioneType();
        //
        List<ContenutoType> contenutoEsito = new ArrayList<>();
        List<ContenutoType> contenutoRapporto = new ArrayList<>();
        List<ContenutoType> contenutoSIP = new ArrayList<>();
        List<ContenutoType> contenutoPISIP = new ArrayList<>();

        for (SessioneVersamentoExt sessioneVersamento : sessioniVersamentoList) {
            String descrizionePart = "dell'Unità documentaria";
            if (sessioneVersamento.getTipoSessione() == Constants.TipoSessione.AGGIUNGI_DOCUMENTO) {
                descrizionePart = "dei documenti aggiunti all'Unità documentaria";
            }
            descrizionePart = descrizionePart.concat(" ".concat(csChiave.toString()));
            for (DatiXml datiXml : sessioneVersamento.getXmlDatiSessioneVers()) {
                // Setto il tag Urn
                if (datiXml.getUrn() != null && !datiXml.getUrn().isEmpty()) {
                    ContenutoType contenuto = new ContenutoType();
                    contenuto.setUrn(datiXml.getUrn());
                    // Setto il tag Descrizione
                    switch (datiXml.getTipoXmlDati()) {
                    case CostantiDB.TipiXmlDati.RICHIESTA:
                        contenuto.setDescrizione("Indice SIP " + descrizionePart);
                        contenutoSIP.add(contenuto);
                        break;
                    case CostantiDB.TipiXmlDati.RISPOSTA:
                        contenuto.setDescrizione("Esito versamento del SIP " + descrizionePart);
                        contenutoEsito.add(contenuto);
                        break;
                    case CostantiDB.TipiXmlDati.RAPP_VERS:
                        contenuto.setDescrizione("Rapporto di versamento  " + descrizionePart);
                        contenutoRapporto.add(contenuto);
                        break;
                    case CostantiDB.TipiXmlDati.INDICE_FILE:
                        contenuto.setDescrizione(" Informazioni sull'impacchettamento "
                                + "(PI - Packaging Information) del SIP " + "dell'unità documentaria ");
                        contenutoPISIP.add(contenuto);
                        break;
                    default:
                        break;
                    }
                }
                // Setto il tag Descrizione
            }
        }

        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAipUpdDaUnitaDoc(idUnitaDoc);
        List<AroXmlUpdUnitaDoc> xmlupds = null;
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            xmlupds = (List<AroXmlUpdUnitaDoc>) rispostaControlli.getrObject();
            String descrizionePart = "dell'aggiornamento all'Unità documentaria";
            descrizionePart = descrizionePart.concat(" ".concat(csChiave.toString()));
            for (AroXmlUpdUnitaDoc xmlupd : xmlupds) {
                // Setto il tag Urn
                ContenutoType contenuto = new ContenutoType();
                contenuto.setUrn(xmlupd.getDsUrnXml());
                // Setto il tag Descrizione
                if (TiXmlUpdUnitaDoc.RICHIESTA.equals(xmlupd.getTiXmlUpdUnitaDoc())) {
                    contenuto.setDescrizione(
                            ("Indice SIP " + descrizionePart + " " + xmlupd.getAroUpdUnitaDoc().getNtUpd()).trim());
                    contenutoSIP.add(contenuto);
                } else if (TiXmlUpdUnitaDoc.RISPOSTA.equals(xmlupd.getTiXmlUpdUnitaDoc())) {
                    contenuto.setDescrizione("Rapporto di versamento  " + descrizionePart);
                    contenutoRapporto.add(contenuto);
                }
            }
        }

        for (ContenutoType contenuto : contenutoSIP) {
            contenutoPacchettoArchiviazione.getContenuto().add(contenuto);
        }
        for (ContenutoType contenuto : contenutoPISIP) {
            contenutoPacchettoArchiviazione.getContenuto().add(contenuto);
        }
        for (ContenutoType contenuto : contenutoRapporto) {
            contenutoPacchettoArchiviazione.getContenuto().add(contenuto);
        }
        for (ContenutoType contenuto : contenutoEsito) {
            contenutoPacchettoArchiviazione.getContenuto().add(contenuto);
        }

        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getPrecedentiVersioniIndiceAip(idUnitaDoc);
        List<AroVerIndiceAipUd> lstVerIndice = null;
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            lstVerIndice = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            if (lstVerIndice != null) {
                for (AroVerIndiceAipUd precVersione : lstVerIndice) {
                    ContenutoType contenuto = new ContenutoType();
                    AroUrnVerIndiceAipUd aroUrnAipIndiceAipUdOrigPrec = (AroUrnVerIndiceAipUd) CollectionUtils
                            .find(precVersione.getAroUrnVerIndiceAipUds(), new Predicate() {
                                @Override
                                public boolean evaluate(final Object object) {
                                    return ((AroUrnVerIndiceAipUd) object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE);
                                }
                            });
                    contenuto.setUrn(aroUrnAipIndiceAipUdOrigPrec.getDsUrn());
                    contenuto.setDescrizione("Precedente versione dell'indice AIP");
                    contenutoPacchettoArchiviazione.getContenuto().add(contenuto);
                }
            }
        }
        miSelfD.setContenutoPacchettoArchiviazione(contenutoPacchettoArchiviazione);
        it.eng.parer.ws.xml.usselfdescResp.ObjectFactory objFct_1 = new it.eng.parer.ws.xml.usselfdescResp.ObjectFactory();
        extraInfoDescGenerale.setAny(objFct_1.createMetadatiIntegratiSelfDescription(miSelfD));
        moreInfoApplic.setEmbeddedMetadata(extraInfoDescGenerale);
        selfie.setMoreInfo(moreInfoApplic);
        idc.setSelfDescription(selfie);

        /*
         * *********** DECORO VDC ***********
         */
        VdCType vdc = new VdCType();
        /* ID */
        IdentifierType idVdc = new IdentifierType();
        String urnPartVersatoreNorm = MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        String urnPartChiaveUdNorm = MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave, true,
                Costanti.UrnFormatter.UD_FMT_STRING);
        String urnUD = MessaggiWSFormat.formattaBaseUrnUnitaDoc(urnPartVersatoreNorm, urnPartChiaveUdNorm);
        idVdc.setValue(urnUD);
        vdc.setID(idVdc);
        /* VdCGroup */
        VdCGroupType vdcGruppo = new VdCGroupType();
        // Label
        String label = "Registro";
        vdcGruppo.setLabel(label);
        // ID
        IdentifierType idVdcGruppo = new IdentifierType();
        idVdcGruppo.setValue(tmpAroUnitaDoc.getCdRegistroKeyUnitaDoc());
        vdcGruppo.setID(idVdcGruppo);
        // Description
        DecRegistroUnitaDoc reg = tmpAroUnitaDoc.getDecRegistroUnitaDoc();
        if (reg != null) {
            String dsReg = reg.getDsRegistroUnitaDoc();
            DescriptionType desc = new DescriptionType();
            desc.setValue(dsReg);
            vdcGruppo.setDescription(desc);
        }
        vdc.setVdCGroup(vdcGruppo);
        /* MoreInfo */
        MoreInfoType moreInfoVdc = new MoreInfoType();
        moreInfoVdc.setXMLScheme("Unisincro_MoreInfoVdC_v1.1.xsd");
        EmbeddedMetadataType emdvdc = new EmbeddedMetadataType();
        MetadatiIntegratiPdAType mipda = new MetadatiIntegratiPdAType();

        // metadati PDA
        this.popolaMetadatiIntegratiPdA(mipda, tmpAroUnitaDoc, tmpAroVersIniUnitaDoc, tmpAroUpdUnitaDocPgMax,
                codiceVersione, sistemaConservazione);

        // Composizione
        ComposizioneType composizione = new ComposizioneType();
        mipda.setComposizione(composizione);
        it.eng.parer.ws.xml.usvdcResp.ObjectFactory objFct_2 = new it.eng.parer.ws.xml.usvdcResp.ObjectFactory();
        emdvdc.setAny(objFct_2.createMetadatiIntegratiPdA(mipda));
        moreInfoVdc.setEmbeddedMetadata(emdvdc);
        vdc.setMoreInfo(moreInfoVdc);
        idc.setVdC(vdc);

        /*
         * ***************** NORMALIZZO UD E CALCOLO URN COMPONENTI *****************
         */
        unitaDocumentarieHelper.normalizzaUDAndCalcUrnOrigNormalizComp(tmpAroUnitaDoc.getIdUnitaDoc(),
                Arrays.asList(TiUrn.ORIGINALE, TiUrn.NORMALIZZATO));
        /*
         * ***************** DECORO FILEGROUP *****************
         */
        this.popolaFileGroupList(idc, composizione, sessioniVersamentoList, tmpAroUnitaDoc, xmlupds,
                tmpAroVersIniUnitaDoc, tmpAroUpdUnitaDocPgMax, csChiave, csVersatore);

        ////////////////

        /* FILEGROUP DELLE PRECEDENTI VERSIONI DELL'INDICE AIP */
        if (lstVerIndice != null) {
            isFileGroupNotEmpty = false;
            FileGroupType fileGroupPrecVersAip = new FileGroupType();
            /* Label */
            fileGroupPrecVersAip.setLabel("Precedenti versioni dell'Indice AIP dell'Unità documentaria");
            /* File */
            for (AroVerIndiceAipUd precVersIndiceAip : lstVerIndice) {

                AroUrnVerIndiceAipUd aroUrnAipIndiceAipUdOrigPrec = (AroUrnVerIndiceAipUd) CollectionUtils
                        .find(precVersIndiceAip.getAroUrnVerIndiceAipUds(), new Predicate() {
                            @Override
                            public boolean evaluate(final Object object) {
                                return ((AroUrnVerIndiceAipUd) object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE);
                            }
                        });
                // end EVO#16486
                if (aroUrnAipIndiceAipUdOrigPrec.getDsUrn() != null) {
                    FileType filePrecVersAip = new FileType();
                    filePrecVersAip.setEncoding(null);
                    filePrecVersAip.setFormat("application/xml");
                    // ID
                    IdentifierType idFileVersPrec = new IdentifierType();
                    idFileVersPrec.setValue(aroUrnAipIndiceAipUdOrigPrec.getDsUrn());
                    filePrecVersAip.setID(idFileVersPrec);
                    // Hash
                    HashType hash = new HashType();
                    // MAC#18826
                    // MAC#25224
                    hash.setValue(precVersIndiceAip.getDsHashIndiceAip());
                    // MAC#25654
                    String function = precVersIndiceAip.getDsAlgoHashIndiceAip() != null
                            ? precVersIndiceAip.getDsAlgoHashIndiceAip() : "Non Definito";
                    hash.setFunction(function);
                    // end MAC#25654
                    // end MAC#25224
                    filePrecVersAip.setHash(hash);
                    fileGroupPrecVersAip.getFile().add(filePrecVersAip);
                    isFileGroupNotEmpty = true;

                }

            }
            if (isFileGroupNotEmpty) {
                idc.getFileGroup().add(fileGroupPrecVersAip);
            }
        }

        /*
         * *************** DECORO PROCESS ***************
         */
        ProcessType processo = new ProcessType();
        /* Primo Agent */
        AgentType primoAgente = new AgentType();
        primoAgente.setType("organization");
        primoAgente.setRole("OtherRole");
        primoAgente.setOtherRole("Producer");
        AgentNameType primoAgenteNome = new AgentNameType();
        primoAgenteNome.setFormalName(tmpAroUnitaDoc.getOrgStrut().getOrgEnte().getDsEnte());
        primoAgente.setAgentName(primoAgenteNome);
        processo.getAgent().add(primoAgente);
        /* Secondo Agent */
        AgentType secondoAgente = new AgentType();
        secondoAgente.setType("organization");
        secondoAgente.setRole("OtherRole");
        secondoAgente.setOtherRole("Preserver");
        AgentNameType secondoAgenteNome = new AgentNameType();
        secondoAgenteNome.setFormalName(mappaAgenti.get(AGENT_PRESERVER_FORMALNAME));
        secondoAgente.setAgentName(secondoAgenteNome);
        AgentIDType secondoAgenteID = new AgentIDType();
        secondoAgenteID.setValue(mappaAgenti.get(AGENT_PRESERVER_TAXCODE));
        secondoAgenteID.setScheme("TaxCode");
        secondoAgente.getAgentID().add(secondoAgenteID);
        processo.getAgent().add(secondoAgente);
        /* Terzo Agent */
        AgentType agenteGB = new AgentType();
        agenteGB.setType("person");
        agenteGB.setRole("PreservationManager");
        AgentNameType agenteGBNome = new AgentNameType();
        NameAndSurnameType nomeEcognome = new NameAndSurnameType();
        nomeEcognome.setLastName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_LASTNAME));
        nomeEcognome.setFirstName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_FIRSTNAME));
        agenteGBNome.setNameAndSurname(nomeEcognome);
        agenteGB.setAgentName(agenteGBNome);
        // Agent_ID
        AgentIDType soggettoID = new AgentIDType();
        soggettoID.setValue(mappaAgenti.get(AGENT_PRESERVATION_MNGR_TAXCODE));
        soggettoID.setScheme("TaxCode");
        agenteGB.getAgentID().add(soggettoID);
        processo.getAgent().add(agenteGB);
        /* Time Reference */
        TimeReferenceType tempo = new TimeReferenceType();
        AttachedTimeStampType attachedTimeStamp = new AttachedTimeStampType();
        attachedTimeStamp.setNormal(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        tempo.setAttachedTimeStamp(attachedTimeStamp);
        processo.setTimeReference(tempo);
        /* Law And Regulations */
        String legge = "DPCM 3 dicembre 2013 - Regole tecniche in materia di conservazione (GU n.59 del 12-3-2014)";
        LawAndRegulationsType lar = new LawAndRegulationsType();
        lar.setValue(legge);
        processo.setLawAndRegulations(lar);
        idc.setProcess(processo);
    }

    private void popolaMetadatiIntegratiPdA(MetadatiIntegratiPdAType mipda, AroUnitaDoc tmpAroUnitaDoc,
            AroVersIniUnitaDoc tmpAroVersIniUnitaDoc, AroUpdUnitaDoc tmpAroUpdUnitaDoc, String codiceVersione,
            String sistemaConservazione) {

        // Versatore
        VersatoreType versatore = new VersatoreType();
        versatore.setAmbiente(tmpAroUnitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(tmpAroUnitaDoc.getOrgStrut().getOrgEnte().getNmEnte());
        versatore.setStruttura(tmpAroUnitaDoc.getOrgStrut().getNmStrut());
        versatore.setUserID(tmpAroUnitaDoc.getIamUser().getNmUserid());
        mipda.setVersatore(versatore);
        // Chiave
        ChiaveType chiave = new ChiaveType();
        chiave.setRegistro(tmpAroUnitaDoc.getCdRegistroKeyUnitaDoc());
        chiave.setAnno(tmpAroUnitaDoc.getAaKeyUnitaDoc().toBigInteger());
        chiave.setNumero(tmpAroUnitaDoc.getCdKeyUnitaDoc());
        mipda.setChiave(chiave);
        // Data Acquisizione
        mipda.setDataAcquisizione(XmlDateUtility.dateToXMLGregorianCalendar(tmpAroUnitaDoc.getDtCreazione()));
        // TipologiaUnitaDocumentaria
        mipda.setTipologiaUnitaDocumentaria(tmpAroUnitaDoc.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
        // ProfiloUnitaDocumentaria
        ProfiloUnitaDocumentariaType profiloUD = new ProfiloUnitaDocumentariaType();
        profiloUD.setOggetto(tmpAroUnitaDoc.getDlOggettoUnitaDoc());
        if (tmpAroVersIniUnitaDoc != null) {
            profiloUD.setOggetto(tmpAroVersIniUnitaDoc.getDlOggettoUnitaDoc());
        }
        if (tmpAroUpdUnitaDoc != null) {
            profiloUD.setOggetto(tmpAroUpdUnitaDoc.getDlOggettoUnitaDoc());
        }
        profiloUD.setData(tmpAroUnitaDoc.getDtRegUnitaDoc() != null
                ? XmlDateUtility.dateToXMLGregorianCalendar(tmpAroUnitaDoc.getDtRegUnitaDoc()) : null);
        if (tmpAroVersIniUnitaDoc != null) {
            profiloUD.setData(tmpAroVersIniUnitaDoc.getDtRegUnitaDoc() != null
                    ? XmlDateUtility.dateToXMLGregorianCalendar(tmpAroVersIniUnitaDoc.getDtRegUnitaDoc()) : null);
        }
        if (tmpAroUpdUnitaDoc != null) {
            profiloUD.setData(tmpAroUpdUnitaDoc.getDtRegUnitaDoc() != null
                    ? XmlDateUtility.dateToXMLGregorianCalendar(tmpAroUpdUnitaDoc.getDtRegUnitaDoc()) : null);
        }
        mipda.setProfiloUnitaDocumentaria(profiloUD);
        // Profilo Archivistico
        ProfiloArchivisticoType profilo = new ProfiloArchivisticoType();
        popolaProfiloArchivistico(profilo, tmpAroUnitaDoc);
        if (tmpAroVersIniUnitaDoc != null) {
            popolaProfiloArchivisticoVersIniUpd(profilo, tmpAroVersIniUnitaDoc);
        }
        if (tmpAroUpdUnitaDoc != null) {
            popolaProfiloArchivisticoUpd(profilo, tmpAroUpdUnitaDoc);
        }
        mipda.setProfiloArchivistico(profilo);
        // Note Unità documentaria
        mipda.setNoteUnitaDocumentaria(tmpAroUnitaDoc.getNtUnitaDoc());
        // Dati Specifici dell'UD
        mipda.setDatiSpecifici(this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.VERS, TipiEntitaSacer.UNI_DOC,
                tmpAroUnitaDoc.getIdUnitaDoc()));
        if (tmpAroVersIniUnitaDoc != null) {
            mipda.setDatiSpecifici(this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.VERS,
                    TiEntitaSacerAroVersIniDatiSpec.UNI_DOC, tmpAroVersIniUnitaDoc.getIdVersIniUnitaDoc()));
        }
        if (tmpAroUpdUnitaDoc != null) {
            mipda.setDatiSpecifici(this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.VERS,
                    TiEntitaAroUpdDatiSpecUnitaDoc.UPD_UNI_DOC, tmpAroUpdUnitaDoc.getIdUpdUnitaDoc()));
        }
        // Sistema di migrazione
        mipda.setSistemaDiMigrazione(tmpAroUnitaDoc.getNmSistemaMigraz());
        // Dati Specifici migrazione dell'UD
        mipda.setDatiSpecificiMigrazione(this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.MIGRAZ, TipiEntitaSacer.UNI_DOC,
                tmpAroUnitaDoc.getIdUnitaDoc()));
        if (tmpAroVersIniUnitaDoc != null) {
            mipda.setDatiSpecificiMigrazione(this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.MIGRAZ,
                    TiEntitaSacerAroVersIniDatiSpec.UNI_DOC, tmpAroVersIniUnitaDoc.getIdVersIniUnitaDoc()));
        }
        if (tmpAroUpdUnitaDoc != null) {
            mipda.setDatiSpecificiMigrazione(this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ,
                    TiEntitaAroUpdDatiSpecUnitaDoc.UPD_UNI_DOC, tmpAroUpdUnitaDoc.getIdUpdUnitaDoc()));
        }

        // Documenti collegati
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiUDocColleg(tmpAroUnitaDoc.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroLinkUnitaDoc> tmpLstAroUDLink = (List<AroLinkUnitaDoc>) rispostaControlli.getrObject();
            if (tmpLstAroUDLink.size() > 0) {
                DocumentoCollegatoType documentiCollegati = new DocumentoCollegatoType();
                for (AroLinkUnitaDoc tmpLinkUD : tmpLstAroUDLink) {
                    DocumentoCollegatoType.DocumentoCollegato tmpDocumentoCollegato = new DocumentoCollegatoType.DocumentoCollegato();
                    tmpDocumentoCollegato.setChiaveCollegamento(new ChiaveType());
                    tmpDocumentoCollegato.getChiaveCollegamento().setRegistro(tmpLinkUD.getCdRegistroKeyUnitaDocLink());
                    tmpDocumentoCollegato.getChiaveCollegamento()
                            .setAnno(tmpLinkUD.getAaKeyUnitaDocLink().toBigInteger());
                    tmpDocumentoCollegato.getChiaveCollegamento().setNumero(tmpLinkUD.getCdKeyUnitaDocLink());
                    CSChiave csChiaveUDColl = new CSChiave();
                    csChiaveUDColl.setAnno(tmpLinkUD.getAaKeyUnitaDocLink().longValue());
                    csChiaveUDColl.setNumero(tmpLinkUD.getCdKeyUnitaDocLink());
                    csChiaveUDColl.setTipoRegistro(tmpLinkUD.getCdRegistroKeyUnitaDocLink());
                    CSVersatore csVersatoreUDColl = new CSVersatore();
                    csVersatoreUDColl.setSistemaConservazione(sistemaConservazione);
                    csVersatoreUDColl.setAmbiente(
                            tmpLinkUD.getAroUnitaDoc().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
                    csVersatoreUDColl.setEnte(tmpLinkUD.getAroUnitaDoc().getOrgStrut().getOrgEnte().getNmEnte());
                    csVersatoreUDColl.setStruttura(tmpLinkUD.getAroUnitaDoc().getOrgStrut().getNmStrut());
                    String urnUDLink = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                            MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreUDColl),
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiaveUDColl));
                    tmpDocumentoCollegato.setUrnUDCollegata(urnUDLink);
                    tmpDocumentoCollegato.setDescrizioneCollegamento(tmpLinkUD.getDsLinkUnitaDoc());
                    documentiCollegati.getDocumentoCollegato().add(tmpDocumentoCollegato);
                }
                mipda.setDocumentiCollegati(documentiCollegati);
            }
        }
        if (tmpAroVersIniUnitaDoc != null) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecIndiceAip.leggiUDocCollegVersIniUpd(tmpAroUnitaDoc.getIdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                List<AroVersIniLinkUnitaDoc> tmpLstAroUDLinkVersIniUpd = (List<AroVersIniLinkUnitaDoc>) rispostaControlli
                        .getrObject();
                if (tmpLstAroUDLinkVersIniUpd.size() > 0) {
                    DocumentoCollegatoType documentiCollegati = new DocumentoCollegatoType();
                    for (AroVersIniLinkUnitaDoc tmpLinkUDVersIniUpd : tmpLstAroUDLinkVersIniUpd) {
                        DocumentoCollegatoType.DocumentoCollegato tmpDocumentoCollegato = new DocumentoCollegatoType.DocumentoCollegato();
                        tmpDocumentoCollegato.setChiaveCollegamento(new ChiaveType());
                        tmpDocumentoCollegato.getChiaveCollegamento()
                                .setRegistro(tmpLinkUDVersIniUpd.getCdRegistroKeyUnitaDocLink());
                        tmpDocumentoCollegato.getChiaveCollegamento()
                                .setAnno(tmpLinkUDVersIniUpd.getAaKeyUnitaDocLink().toBigInteger());
                        tmpDocumentoCollegato.getChiaveCollegamento()
                                .setNumero(tmpLinkUDVersIniUpd.getCdKeyUnitaDocLink());
                        CSChiave csChiaveUDColl = new CSChiave();
                        csChiaveUDColl.setAnno(tmpLinkUDVersIniUpd.getAaKeyUnitaDocLink().longValue());
                        csChiaveUDColl.setNumero(tmpLinkUDVersIniUpd.getCdKeyUnitaDocLink());
                        csChiaveUDColl.setTipoRegistro(tmpLinkUDVersIniUpd.getCdRegistroKeyUnitaDocLink());
                        CSVersatore csVersatoreUDColl = new CSVersatore();
                        csVersatoreUDColl.setSistemaConservazione(sistemaConservazione);
                        csVersatoreUDColl.setAmbiente(tmpLinkUDVersIniUpd.getAroVersIniUnitaDoc().getAroUnitaDoc()
                                .getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
                        csVersatoreUDColl.setEnte(tmpLinkUDVersIniUpd.getAroVersIniUnitaDoc().getAroUnitaDoc()
                                .getOrgStrut().getOrgEnte().getNmEnte());
                        csVersatoreUDColl.setStruttura(tmpLinkUDVersIniUpd.getAroVersIniUnitaDoc().getAroUnitaDoc()
                                .getOrgStrut().getNmStrut());
                        String urnUDLink = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                                MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreUDColl),
                                MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiaveUDColl));
                        tmpDocumentoCollegato.setUrnUDCollegata(urnUDLink);
                        tmpDocumentoCollegato.setDescrizioneCollegamento(tmpLinkUDVersIniUpd.getDsLinkUnitaDoc());
                        documentiCollegati.getDocumentoCollegato().add(tmpDocumentoCollegato);
                    }
                    mipda.setDocumentiCollegati(documentiCollegati);
                }
            }
        }
        if (tmpAroUpdUnitaDoc != null) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecIndiceAip.leggiUDocCollegUpd(tmpAroUpdUnitaDoc.getIdUpdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                List<AroUpdLinkUnitaDoc> tmpLstAroUDLinkUpd = (List<AroUpdLinkUnitaDoc>) rispostaControlli.getrObject();
                if (tmpLstAroUDLinkUpd.size() > 0) {
                    DocumentoCollegatoType documentiCollegati = new DocumentoCollegatoType();
                    for (AroUpdLinkUnitaDoc tmpLinkUDUpd : tmpLstAroUDLinkUpd) {
                        DocumentoCollegatoType.DocumentoCollegato tmpDocumentoCollegato = new DocumentoCollegatoType.DocumentoCollegato();
                        tmpDocumentoCollegato.setChiaveCollegamento(new ChiaveType());
                        tmpDocumentoCollegato.getChiaveCollegamento()
                                .setRegistro(tmpLinkUDUpd.getCdRegistroKeyUnitaDocLink());
                        tmpDocumentoCollegato.getChiaveCollegamento()
                                .setAnno(tmpLinkUDUpd.getAaKeyUnitaDocLink().toBigInteger());
                        tmpDocumentoCollegato.getChiaveCollegamento().setNumero(tmpLinkUDUpd.getCdKeyUnitaDocLink());
                        CSChiave csChiaveUDColl = new CSChiave();
                        csChiaveUDColl.setAnno(tmpLinkUDUpd.getAaKeyUnitaDocLink().longValue());
                        csChiaveUDColl.setNumero(tmpLinkUDUpd.getCdKeyUnitaDocLink());
                        csChiaveUDColl.setTipoRegistro(tmpLinkUDUpd.getCdRegistroKeyUnitaDocLink());
                        CSVersatore csVersatoreUDColl = new CSVersatore();
                        csVersatoreUDColl.setSistemaConservazione(sistemaConservazione);
                        csVersatoreUDColl.setAmbiente(tmpLinkUDUpd.getAroUpdUnitaDoc().getOrgStrut().getOrgEnte()
                                .getOrgAmbiente().getNmAmbiente());
                        csVersatoreUDColl
                                .setEnte(tmpLinkUDUpd.getAroUpdUnitaDoc().getOrgStrut().getOrgEnte().getNmEnte());
                        csVersatoreUDColl.setStruttura(tmpLinkUDUpd.getAroUpdUnitaDoc().getOrgStrut().getNmStrut());
                        String urnUDLink = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                                MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreUDColl),
                                MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiaveUDColl));
                        tmpDocumentoCollegato.setUrnUDCollegata(urnUDLink);
                        tmpDocumentoCollegato.setDescrizioneCollegamento(tmpLinkUDUpd.getDsLinkUnitaDoc());
                        documentiCollegati.getDocumentoCollegato().add(tmpDocumentoCollegato);
                    }
                    mipda.setDocumentiCollegati(documentiCollegati);
                }
            }
        }

        // Note
        List<AroNotaUnitaDoc> tmpLstAroNotaUnitaDoc = (List<AroNotaUnitaDoc>) tmpAroUnitaDoc.getAroNotaUnitaDocs();
        if (tmpLstAroNotaUnitaDoc.size() > 0) {
            NotaType note = new NotaType();
            for (AroNotaUnitaDoc tmpNotaUnitaDoc : tmpLstAroNotaUnitaDoc) {
                NotaType.NotaUnitaDocumentaria tmpNota = new NotaType.NotaUnitaDocumentaria();
                tmpNota.setTipoNota(tmpNotaUnitaDoc.getDecTipoNotaUnitaDoc().getDsTipoNotaUnitaDoc());
                tmpNota.setId(String.valueOf(tmpNotaUnitaDoc.getIdNotaUnitaDoc()));
                tmpNota.setNota(tmpNotaUnitaDoc.getDsNotaUnitaDoc());
                tmpNota.setDataRegistrazione(
                        XmlDateUtility.dateToXMLGregorianCalendar(tmpNotaUnitaDoc.getDtNotaUnitaDoc()));
                tmpNota.setUtente(tmpNotaUnitaDoc.getIamUser().getNmUserid());
                note.getNotaUnitaDocumentaria().add(tmpNota);
            }
            mipda.setNote(note);
        }
    }

    private void popolaProfiloArchivistico(ProfiloArchivisticoType profilo, AroUnitaDoc tmpAroUnitaDoc) {
        // Popolo il fascicolo principale con relativo sottofascicolo
        CamiciaFascicoloType principale = new CamiciaFascicoloType();
        profilo.setFascicoloPrincipale(principale);
        if (StringUtils.isNotEmpty(tmpAroUnitaDoc.getDsClassifPrinc())
                || StringUtils.isNotEmpty(tmpAroUnitaDoc.getCdFascicPrinc())
                || StringUtils.isNotEmpty(tmpAroUnitaDoc.getDsOggettoFascicPrinc())
                || StringUtils.isNotEmpty(tmpAroUnitaDoc.getCdSottofascicPrinc())
                || StringUtils.isNotEmpty(tmpAroUnitaDoc.getDsOggettoSottofascicPrinc())) {
            principale.setClassifica(tmpAroUnitaDoc.getDsClassifPrinc());
            if (StringUtils.isNotEmpty(tmpAroUnitaDoc.getCdFascicPrinc())
                    || StringUtils.isNotEmpty(tmpAroUnitaDoc.getDsOggettoFascicPrinc())) {
                FascicoloType fascicolo = new FascicoloType();
                fascicolo.setIdentificativo(tmpAroUnitaDoc.getCdFascicPrinc());
                fascicolo.setOggetto(tmpAroUnitaDoc.getDsOggettoFascicPrinc());
                principale.setFascicolo(fascicolo);
            }
            if (StringUtils.isNotEmpty(tmpAroUnitaDoc.getCdSottofascicPrinc())
                    || StringUtils.isNotEmpty(tmpAroUnitaDoc.getDsOggettoSottofascicPrinc())) {
                FascicoloType sottofascicolo = new FascicoloType();
                sottofascicolo.setIdentificativo(tmpAroUnitaDoc.getCdSottofascicPrinc());
                sottofascicolo.setOggetto(tmpAroUnitaDoc.getDsOggettoSottofascicPrinc());
                principale.setSottoFascicolo(sottofascicolo);
            }

        }

        // Popolo i fascicoli secondari
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiFascicoliSec(tmpAroUnitaDoc.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroArchivSec> tmpLstAroArchivSecs = (List<AroArchivSec>) rispostaControlli.getrObject();
            if (tmpLstAroArchivSecs.size() > 0) {
                ProfiloArchivisticoType.FascicoliSecondari fascicoli = new ProfiloArchivisticoType.FascicoliSecondari();
                profilo.setFascicoliSecondari(fascicoli);
                for (AroArchivSec tmpArchivSec : tmpLstAroArchivSecs) {
                    if (StringUtils.isNotEmpty(tmpArchivSec.getDsClassif())
                            || StringUtils.isNotEmpty(tmpArchivSec.getCdFascic())
                            || StringUtils.isNotEmpty(tmpArchivSec.getDsOggettoFascic())
                            || StringUtils.isNotEmpty(tmpArchivSec.getCdSottofascic())
                            || StringUtils.isNotEmpty(tmpArchivSec.getDsOggettoSottofascic())) {
                        CamiciaFascicoloType secondario = new CamiciaFascicoloType();
                        secondario.setClassifica(tmpArchivSec.getDsClassif());
                        if (StringUtils.isNotEmpty(tmpArchivSec.getCdFascic())
                                || StringUtils.isNotEmpty(tmpArchivSec.getDsOggettoFascic())) {
                            FascicoloType fascicoloSec = new FascicoloType();
                            fascicoloSec.setIdentificativo(tmpArchivSec.getCdFascic());
                            fascicoloSec.setOggetto(tmpArchivSec.getDsOggettoFascic());
                            secondario.setFascicolo(fascicoloSec);
                        }

                        if (StringUtils.isNotEmpty(tmpArchivSec.getCdSottofascic())
                                || StringUtils.isNotEmpty(tmpArchivSec.getDsOggettoSottofascic())) {
                            FascicoloType sottofascicloSec = new FascicoloType();
                            sottofascicloSec.setIdentificativo(tmpArchivSec.getCdSottofascic());
                            sottofascicloSec.setOggetto(tmpArchivSec.getDsOggettoSottofascic());
                            secondario.setSottoFascicolo(sottofascicloSec);
                        }
                        fascicoli.getFascicoloSecondario().add(secondario);
                    }
                }
            }
        }
    }

    private void popolaProfiloArchivisticoVersIniUpd(ProfiloArchivisticoType profilo,
            AroVersIniUnitaDoc tmpVersIniUnitaDoc) {
        // Popolo il fascicolo principale con relativo sottofascicolo
        CamiciaFascicoloType principale = new CamiciaFascicoloType();
        profilo.setFascicoloPrincipale(principale);
        if (StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getDsClassifPrinc())
                || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getCdFascicPrinc())
                || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getDsOggettoFascicPrinc())
                || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getCdSottofascicPrinc())
                || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getDsOggettoSottofascicPrinc())) {
            principale.setClassifica(tmpVersIniUnitaDoc.getDsClassifPrinc());
            if (StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getCdFascicPrinc())
                    || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getDsOggettoFascicPrinc())) {
                FascicoloType fascicolo = new FascicoloType();
                fascicolo.setIdentificativo(tmpVersIniUnitaDoc.getCdFascicPrinc());
                fascicolo.setOggetto(tmpVersIniUnitaDoc.getDsOggettoFascicPrinc());
                principale.setFascicolo(fascicolo);
            }
            if (StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getCdSottofascicPrinc())
                    || StringUtils.isNotEmpty(tmpVersIniUnitaDoc.getDsOggettoSottofascicPrinc())) {
                FascicoloType sottofascicolo = new FascicoloType();
                sottofascicolo.setIdentificativo(tmpVersIniUnitaDoc.getCdSottofascicPrinc());
                sottofascicolo.setOggetto(tmpVersIniUnitaDoc.getDsOggettoSottofascicPrinc());
                principale.setSottoFascicolo(sottofascicolo);
            }

        }

        // Popolo i fascicoli secondari
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip
                .leggiFascicoliSecVersIniUnitaDoc(tmpVersIniUnitaDoc.getIdVersIniUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVersIniArchivSec> tmpLstAroVersIniArchivSecs = (List<AroVersIniArchivSec>) rispostaControlli
                    .getrObject();
            if (tmpLstAroVersIniArchivSecs.size() > 0) {
                ProfiloArchivisticoType.FascicoliSecondari fascicoli = new ProfiloArchivisticoType.FascicoliSecondari();
                profilo.setFascicoliSecondari(fascicoli);
                for (AroVersIniArchivSec tmpVersIniArchivSec : tmpLstAroVersIniArchivSecs) {
                    if (StringUtils.isNotEmpty(tmpVersIniArchivSec.getDsClassif())
                            || StringUtils.isNotEmpty(tmpVersIniArchivSec.getCdFascic())
                            || StringUtils.isNotEmpty(tmpVersIniArchivSec.getDsOggettoFascic())
                            || StringUtils.isNotEmpty(tmpVersIniArchivSec.getCdSottofascic())
                            || StringUtils.isNotEmpty(tmpVersIniArchivSec.getDsOggettoSottofascic())) {
                        CamiciaFascicoloType secondario = new CamiciaFascicoloType();
                        secondario.setClassifica(tmpVersIniArchivSec.getDsClassif());
                        if (StringUtils.isNotEmpty(tmpVersIniArchivSec.getCdFascic())
                                || StringUtils.isNotEmpty(tmpVersIniArchivSec.getDsOggettoFascic())) {
                            FascicoloType fascicoloSec = new FascicoloType();
                            fascicoloSec.setIdentificativo(tmpVersIniArchivSec.getCdFascic());
                            fascicoloSec.setOggetto(tmpVersIniArchivSec.getDsOggettoFascic());
                            secondario.setFascicolo(fascicoloSec);
                        }

                        if (StringUtils.isNotEmpty(tmpVersIniArchivSec.getCdSottofascic())
                                || StringUtils.isNotEmpty(tmpVersIniArchivSec.getDsOggettoSottofascic())) {
                            FascicoloType sottofascicloSec = new FascicoloType();
                            sottofascicloSec.setIdentificativo(tmpVersIniArchivSec.getCdSottofascic());
                            sottofascicloSec.setOggetto(tmpVersIniArchivSec.getDsOggettoSottofascic());
                            secondario.setSottoFascicolo(sottofascicloSec);
                        }
                        fascicoli.getFascicoloSecondario().add(secondario);
                    }
                }
            }
        }

    }

    private void popolaProfiloArchivisticoUpd(ProfiloArchivisticoType profilo, AroUpdUnitaDoc tmpUpdUnitaDoc) {
        // Popolo il fascicolo principale con relativo sottofascicolo
        CamiciaFascicoloType principale = new CamiciaFascicoloType();
        profilo.setFascicoloPrincipale(principale);
        if (StringUtils.isNotEmpty(tmpUpdUnitaDoc.getDsClassifPrinc())
                || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getCdFascicPrinc())
                || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getDsOggettoFascicPrinc())
                || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getCdSottofascicPrinc())
                || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getDsOggettoSottofascicPrinc())) {
            principale.setClassifica(tmpUpdUnitaDoc.getDsClassifPrinc());
            if (StringUtils.isNotEmpty(tmpUpdUnitaDoc.getCdFascicPrinc())
                    || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getDsOggettoFascicPrinc())) {
                FascicoloType fascicolo = new FascicoloType();
                fascicolo.setIdentificativo(tmpUpdUnitaDoc.getCdFascicPrinc());
                fascicolo.setOggetto(tmpUpdUnitaDoc.getDsOggettoFascicPrinc());
                principale.setFascicolo(fascicolo);
            }
            if (StringUtils.isNotEmpty(tmpUpdUnitaDoc.getCdSottofascicPrinc())
                    || StringUtils.isNotEmpty(tmpUpdUnitaDoc.getDsOggettoSottofascicPrinc())) {
                FascicoloType sottofascicolo = new FascicoloType();
                sottofascicolo.setIdentificativo(tmpUpdUnitaDoc.getCdSottofascicPrinc());
                sottofascicolo.setOggetto(tmpUpdUnitaDoc.getDsOggettoSottofascicPrinc());
                principale.setSottoFascicolo(sottofascicolo);
            }

        }

        // Popolo i fascicoli secondari
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiFascicoliSecUpd(tmpUpdUnitaDoc.getIdUpdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroUpdArchivSec> tmpLstAroUpdArchivSecs = (List<AroUpdArchivSec>) rispostaControlli.getrObject();
            if (tmpLstAroUpdArchivSecs.size() > 0) {
                ProfiloArchivisticoType.FascicoliSecondari fascicoli = new ProfiloArchivisticoType.FascicoliSecondari();
                profilo.setFascicoliSecondari(fascicoli);
                for (AroUpdArchivSec tmpUpdArchivSec : tmpLstAroUpdArchivSecs) {
                    if (StringUtils.isNotEmpty(tmpUpdArchivSec.getDsClassif())
                            || StringUtils.isNotEmpty(tmpUpdArchivSec.getCdFascic())
                            || StringUtils.isNotEmpty(tmpUpdArchivSec.getDsOggettoFascic())
                            || StringUtils.isNotEmpty(tmpUpdArchivSec.getCdSottofascic())
                            || StringUtils.isNotEmpty(tmpUpdArchivSec.getDsOggettoSottofascic())) {
                        CamiciaFascicoloType secondario = new CamiciaFascicoloType();
                        secondario.setClassifica(tmpUpdArchivSec.getDsClassif());
                        if (StringUtils.isNotEmpty(tmpUpdArchivSec.getCdFascic())
                                || StringUtils.isNotEmpty(tmpUpdArchivSec.getDsOggettoFascic())) {
                            FascicoloType fascicoloSec = new FascicoloType();
                            fascicoloSec.setIdentificativo(tmpUpdArchivSec.getCdFascic());
                            fascicoloSec.setOggetto(tmpUpdArchivSec.getDsOggettoFascic());
                            secondario.setFascicolo(fascicoloSec);
                        }

                        if (StringUtils.isNotEmpty(tmpUpdArchivSec.getCdSottofascic())
                                || StringUtils.isNotEmpty(tmpUpdArchivSec.getDsOggettoSottofascic())) {
                            FascicoloType sottofascicloSec = new FascicoloType();
                            sottofascicloSec.setIdentificativo(tmpUpdArchivSec.getCdSottofascic());
                            sottofascicloSec.setOggetto(tmpUpdArchivSec.getDsOggettoSottofascic());
                            secondario.setSottoFascicolo(sottofascicloSec);
                        }
                        fascicoli.getFascicoloSecondario().add(secondario);
                    }
                }
            }
        }

    }

    private void popolaFileGroupList(IdCType idc, ComposizioneType composizione,
            List<SessioneVersamentoExt> sessioniVersamentoList, AroUnitaDoc tmpAroUnitaDoc,
            List<AroXmlUpdUnitaDoc> xmlupdList, AroVersIniUnitaDoc tmpAroVersIniUnitaDoc,
            AroUpdUnitaDoc tmpAroUpdUnitaDoc, CSChiave csChiave, CSVersatore csVersatore) {

        long numAllegati = 0;
        long numAnnessi = 0;
        long numAnnotazioni = 0;

        /* FILEGROUP DEI DOCUMENTI DELL'XML DI VERSAMENTO */
        FileGroupType fileGroupDocVers = new FileGroupType();
        fileGroupDocVers.setLabel("Indici SIP dell'Unità documentaria e degli eventuali Documenti aggiunti");
        /* FILEGROUP DEI DOCUMENTI DELL'XML DI ESITO VERSAMENTO */
        FileGroupType fileGroupEsitoVers = new FileGroupType();
        fileGroupEsitoVers.setLabel("Esito versamento dell'unità documentaria e degli eventuali Documenti aggiunti");
        /* FILEGROUP DEI DOCUMENTI DELL'XML DI RAPPORTO VERSAMENTO */
        FileGroupType fileGroupRappVers = new FileGroupType();
        fileGroupRappVers
                .setLabel("Rapporto di versamento dell'Unità documentaria e degli eventuali Documenti aggiunti");

        for (SessioneVersamentoExt sessioneVersamento : sessioniVersamentoList) {

            for (AroDoc aroDoc : sessioneVersamento.getDocumentiVersati()) {
                FileGroupType gruppoFile = new FileGroupType();
                // Label
                // MAC#23680
                String urnPartDocumento = (aroDoc.getNiOrdDoc() != null)
                        // EVO#16486
                        ? MessaggiWSFormat.formattaUrnPartDocumento(Costanti.CategoriaDocumento.Documento,
                                aroDoc.getNiOrdDoc().intValue(), true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                                Costanti.UrnFormatter.PAD5DIGITS_FMT)
                        // end EVO#16486
                        : MessaggiWSFormat.formattaUrnPartDocumento(
                                Costanti.CategoriaDocumento.getEnum(aroDoc.getTiDoc()), aroDoc.getPgDoc().intValue());
                // end MAC#23680
                String tmpString = MessaggiWSFormat.formattaBaseUrnDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(csVersatore),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave), urnPartDocumento);
                gruppoFile.setLabel("urn:" + tmpString);
                // File
                rispostaControlli.reset();
                rispostaControlli = controlliRecIndiceAip.leggiComponentiDocumento(aroDoc);
                if (rispostaControlli.isrBoolean()) {
                    List<AroCompDoc> lstDatiF = (List<AroCompDoc>) rispostaControlli.getrObject();
                    // PER OGNI COMPONENTE DEL DOCUMENTO
                    for (AroCompDoc aroCompDoc : lstDatiF) {
                        // Elaboralo come Componente
                        FileType fileComp = popolaComponenteFile(aroCompDoc, tmpAroVersIniUnitaDoc, tmpAroUpdUnitaDoc,
                                0);
                        fileComp.setEncoding(null);
                        gruppoFile.getFile().add(fileComp);
                        rispostaControlli.reset();
                        rispostaControlli = controlliRecIndiceAip.leggiSottoComponenti(aroCompDoc);
                        if (rispostaControlli.isrBoolean()) {
                            // Elaboralo come Sottocomponente
                            List<AroCompDoc> listaSottocomponenti = (List<AroCompDoc>) rispostaControlli.getrObject();
                            int pos = 1;
                            for (AroCompDoc sotCompDoc : listaSottocomponenti) {
                                FileType fileSotComp = popolaComponenteFile(sotCompDoc, tmpAroVersIniUnitaDoc,
                                        tmpAroUpdUnitaDoc, pos++);
                                fileSotComp.setEncoding(null);
                                gruppoFile.getFile().add(fileSotComp);
                            }
                        }
                    }
                }
                // More Info
                MoreInfoType moreInfoFileGruppo = new MoreInfoType();
                moreInfoFileGruppo.setXMLScheme("Unisincro_MoreInfoDoc_v1.1.xsd");
                MetadatiIntegratiDocType mieid = new MetadatiIntegratiDocType();
                popolaMetadatiIntegratiExtraInfoDoc(mieid, aroDoc, tmpAroUnitaDoc);
                if (tmpAroVersIniUnitaDoc != null) {
                    rispostaControlli.reset();
                    rispostaControlli = controlliRecIndiceAip.leggiDocumentoDaVersIniUpd(
                            tmpAroVersIniUnitaDoc.getIdVersIniUnitaDoc(), aroDoc.getIdDoc());
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaError();
                    } else {
                        AroVersIniDoc aroVersIniDoc = (AroVersIniDoc) rispostaControlli.getrObject();
                        if (aroVersIniDoc != null) {
                            aggiornaMetadatiIntegratiExtraInfoDocVersIniUpd(mieid, aroVersIniDoc);
                        }
                    }
                }
                if (tmpAroUpdUnitaDoc != null) {
                    rispostaControlli.reset();
                    rispostaControlli = controlliRecIndiceAip.leggiDocumentoDaUpd(tmpAroUpdUnitaDoc.getIdUpdUnitaDoc(),
                            aroDoc.getIdDoc());
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaError();
                    } else {
                        AroUpdDocUnitaDoc aroUpdDocUnitaDoc = (AroUpdDocUnitaDoc) rispostaControlli.getrObject();
                        if (aroUpdDocUnitaDoc != null) {
                            aggiornaMetadatiIntegratiExtraInfoDocUpd(mieid, aroUpdDocUnitaDoc);
                        }
                    }
                }
                EmbeddedMetadataType emgf = new EmbeddedMetadataType();
                it.eng.parer.ws.xml.usdocResp.ObjectFactory objFct_3 = new it.eng.parer.ws.xml.usdocResp.ObjectFactory();
                emgf.setAny(objFct_3.createMetadatiIntegratiDoc(mieid));
                moreInfoFileGruppo.setEmbeddedMetadata(emgf);
                gruppoFile.setMoreInfo(moreInfoFileGruppo);
                idc.getFileGroup().add(gruppoFile);
                //
                if (aroDoc.getTiDoc().equals(CostantiDB.TipoDocumento.ALLEGATO)) {
                    numAllegati++;
                } else if (aroDoc.getTiDoc().equals(CostantiDB.TipoDocumento.ANNESSO)) {
                    numAnnessi++;
                } else if (aroDoc.getTiDoc().equals(CostantiDB.TipoDocumento.ANNOTAZIONE)) {
                    numAnnotazioni++;
                }
            }

            for (DatiXml datiXml : sessioneVersamento.getXmlDatiSessioneVers()) {
                if (datiXml.getUrn() != null) {
                    FileType tmpFileItem = new FileType();
                    tmpFileItem.setEncoding(null);
                    tmpFileItem.setFormat("application/xml");
                    // ID
                    IdentifierType tmpIdFileItem = new IdentifierType();
                    tmpIdFileItem.setValue(datiXml.getUrn());
                    tmpFileItem.setID(tmpIdFileItem);
                    // Hash
                    HashType tmpHashFileItem = new HashType();
                    tmpHashFileItem.setValue(datiXml.getHash());
                    // MAC#25654
                    String function = datiXml.getAlgoritmo() != null ? datiXml.getAlgoritmo() : "Non Definito";
                    tmpHashFileItem.setFunction(function);
                    // end MAC#25654
                    tmpFileItem.setHash(tmpHashFileItem);

                    if (datiXml.getTipoXmlDati().equals(CostantiDB.TipiXmlDati.RICHIESTA)
                            || datiXml.getTipoXmlDati().equals(CostantiDB.TipiXmlDati.INDICE_FILE)) {
                        fileGroupDocVers.getFile().add(tmpFileItem);
                    } else if (datiXml.getTipoXmlDati().equals(CostantiDB.TipiXmlDati.RISPOSTA)) {
                        fileGroupEsitoVers.getFile().add(tmpFileItem);
                    } else if (datiXml.getTipoXmlDati().equals(CostantiDB.TipiXmlDati.RAPP_VERS)) {
                        fileGroupRappVers.getFile().add(tmpFileItem);
                    }
                }
            }
        }

        // aggiorno la composizione con i valori calcolati,
        // relativi al numero di documenti inseriti nell'indice AIP
        composizione.setNumeroAllegati(BigInteger.valueOf(numAllegati));
        composizione.setNumeroAnnessi(BigInteger.valueOf(numAnnessi));
        composizione.setNumeroAnnotazioni(BigInteger.valueOf(numAnnotazioni));
        //

        if (fileGroupDocVers.getFile().size() > 0) {
            idc.getFileGroup().add(fileGroupDocVers);
        }
        if (fileGroupRappVers.getFile().size() > 0) {
            idc.getFileGroup().add(fileGroupRappVers);
        }
        if (fileGroupEsitoVers.getFile().size() > 0) {
            idc.getFileGroup().add(fileGroupEsitoVers);
        }

        if (xmlupdList != null) {
            /* FILEGROUP DEGLI AGGIORNAMENTI METADATI DELL'XML DI VERSAMENTO */
            FileGroupType fileGroupUpdVers = new FileGroupType();
            fileGroupUpdVers.setLabel("Indici SIP dell'Unità documentaria e degli eventuali Aggiornamenti metadati");
            /* FILEGROUP DEGLI AGGIORNAMENTI METADATI DELL'XML DI RAPPORTO VERSAMENTO */
            FileGroupType fileGroupUpdRappVers = new FileGroupType();
            fileGroupUpdRappVers.setLabel(
                    "Rapporto di versamento dell'Unità documentaria e degli eventuali Aggiornamenti metadati");

            for (AroXmlUpdUnitaDoc xmlupd : xmlupdList) {
                if (xmlupd.getDsUrnXml() != null) {
                    FileType tmpFileItem = new FileType();
                    tmpFileItem.setEncoding(null);
                    tmpFileItem.setFormat("application/xml");
                    // ID
                    IdentifierType tmpIdFileItem = new IdentifierType();
                    tmpIdFileItem.setValue(xmlupd.getDsUrnXml());
                    tmpFileItem.setID(tmpIdFileItem);
                    // Hash
                    HashType tmpHashFileItem = new HashType();
                    tmpHashFileItem.setValue(xmlupd.getDsHashXml());
                    // MAC#25654
                    String function = xmlupd.getDsAlgoHashXml() != null ? xmlupd.getDsAlgoHashXml() : "Non Definito";
                    tmpHashFileItem.setFunction(function);
                    // end MAC#25654
                    tmpFileItem.setHash(tmpHashFileItem);

                    if (xmlupd.getTiXmlUpdUnitaDoc().equals(TiXmlUpdUnitaDoc.RICHIESTA)) {
                        fileGroupUpdVers.getFile().add(tmpFileItem);
                    } else if (xmlupd.getTiXmlUpdUnitaDoc().equals(TiXmlUpdUnitaDoc.RISPOSTA)) {
                        fileGroupUpdRappVers.getFile().add(tmpFileItem);
                    }
                }
            }

            if (fileGroupUpdVers.getFile().size() > 0) {
                idc.getFileGroup().add(fileGroupUpdVers);
            }
            if (fileGroupUpdRappVers.getFile().size() > 0) {
                idc.getFileGroup().add(fileGroupUpdRappVers);
            }
        }
    }

    /**
     * Decoro l'elemento More Info del DOCUMENTO di FileGroup
     *
     */
    private void popolaMetadatiIntegratiExtraInfoDoc(MetadatiIntegratiDocType mieid, AroDoc arodoc,
            AroUnitaDoc tmpAroUnitaDoc) {
        // Chiave Documento
        mieid.setChiaveDocumento(arodoc.getCdKeyDocVers());
        // Tipo Documento
        mieid.setTipoDocumento(arodoc.getDecTipoDoc().getNmTipoDoc());
        // Elemento
        mieid.setElemento(arodoc.getTiDoc());
        // Data Acquisizione
        mieid.setDataAcquisizione(XmlDateUtility.dateToXMLGregorianCalendar(arodoc.getDtCreazione()));
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(arodoc.getDlDoc());
        profiloDocumento.setAutore(arodoc.getDsAutoreDoc());
        // profiloDocumento.setStrutturaDocumento("");
        mieid.setProfiloDocumento(profiloDocumento);
        // Note documento
        mieid.setNoteDocumento(arodoc.getNtDoc());
        // Dati Specifici del Documento
        DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.VERS, TipiEntitaSacer.DOC,
                arodoc.getIdDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Sistama di migrazione
        mieid.setSistemaDiMigrazione(tmpAroUnitaDoc.getNmSistemaMigraz());
        // Dati Specifici migrazione del Documento
        dati = null;
        dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.MIGRAZ, TipiEntitaSacer.DOC, arodoc.getIdDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }
        // Stato Conservazione
        String statoDoc = arodoc.getTiStatoDoc();
        if (statoDoc != null && statoDoc.length() > 0) {
            // *****************************
            // righe commentate per cambio XSD
            // TODO: pulire questo codice: questa è una patch
            // it.eng.parer.ws.xml.usdocResp.types.StatoConservazioneType statoMeta =
            // it.eng.parer.ws.xml.usdocResp.types.StatoConservazioneType.fromValue(statoDoc);
            // mieid.setStatoConservazione(statoMeta);
            // *****************************
        }
    }

    /**
     * Decoro i dati aggiornabili dell'elemento More Info del DOCUMENTO di FileGroup da ARO_VERS_INI_DOC
     *
     */
    private void aggiornaMetadatiIntegratiExtraInfoDocVersIniUpd(MetadatiIntegratiDocType mieid,
            AroVersIniDoc aroVersIniDoc) {
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(aroVersIniDoc.getDlDoc());
        profiloDocumento.setAutore(aroVersIniDoc.getDsAutoreDoc());
        // profiloDocumento.setStrutturaDocumento("");
        mieid.setProfiloDocumento(profiloDocumento);
        // Dati Specifici del Documento
        DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.VERS,
                TiEntitaSacerAroVersIniDatiSpec.DOC, aroVersIniDoc.getIdVersIniDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Dati Specifici migrazione del Documento
        dati = null;
        dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.MIGRAZ,
                TiEntitaSacerAroVersIniDatiSpec.DOC, aroVersIniDoc.getIdVersIniDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }
    }

    /**
     * Decoro i dati aggiornabili dell'elemento More Info del DOCUMENTO di FileGroup da ARO_UPD_DOC_UNITA_DOC
     *
     */
    private void aggiornaMetadatiIntegratiExtraInfoDocUpd(MetadatiIntegratiDocType mieid,
            AroUpdDocUnitaDoc aroUpdDocUnitaDoc) {
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(aroUpdDocUnitaDoc.getDlDoc());
        profiloDocumento.setAutore(aroUpdDocUnitaDoc.getDsAutoreDoc());
        // profiloDocumento.setStrutturaDocumento("");
        mieid.setProfiloDocumento(profiloDocumento);
        // Dati Specifici del Documento
        DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.VERS,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_DOC, aroUpdDocUnitaDoc.getIdUpdDocUnitaDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Dati Specifici migrazione del Documento
        dati = null;
        dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_DOC, aroUpdDocUnitaDoc.getIdUpdDocUnitaDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }
    }

    /**
     * Popola i dati di MoreInfo del COMPONENTE di FileGroup
     *
     */
    private void popolaMetadatiIntegratiExtraInfoFile(MetadatiIntegratiFileType meta, AroCompDoc aroCompDoc, int pos,
            AroVVisCompAip aroVVisCompAip) {

        aroVVisCompAip = (AroVVisCompAip) rispostaControlli.getrObject();
        // Urn
        meta.setUrn(aroVVisCompAip.getDsUrnCompCalc());
        // DimensioneFile
        if (aroVVisCompAip.getNiSizeFileCalc() != null) {
            meta.setDimensioneFile(aroVVisCompAip.getNiSizeFileCalc().toBigInteger());
        } else {
            meta.setDimensioneFile(BigInteger.ZERO);
        }
        // Svolto Controllo Formato Verifica Firma
        if (aroVVisCompAip.getFlNoCalcFmtVerifFirme() != null) {
            meta.setSvoltoControlloFormatoVerificaFirma(aroVVisCompAip.getFlNoCalcFmtVerifFirme().equals("1"));
        }
        // Firmato
        if (aroVVisCompAip.getFlCompFirmato() != null) {
            meta.setFirmato(aroVVisCompAip.getFlCompFirmato().equals("1"));
        }
        // Esito Verifiche Firme
        meta.setEsitoVerificheFirme(aroVVisCompAip.getTiEsitoVerifFirme());
        // Messaggio Esito Verifiche Firme
        meta.setMessaggioEsitoVerificheFirme(aroVVisCompAip.getDsMsgEsitoVerifFirme());
        // Svolto Controllo Calcolo Hash
        if (aroVVisCompAip.getFlNoCalcHashFile() != null) {
            meta.setSvoltoControlloCalcoloHash(aroVVisCompAip.getFlNoCalcHashFile().equals("1"));
        }
        // Hash
        if (aroVVisCompAip.getDsHashFileCalc() != null) {
            meta.setHash(aroVVisCompAip.getDsHashFileCalc());
        }
        // Algoritmo Hash
        meta.setAlgoritmoHash(aroVVisCompAip.getDsAlgoHashFileCalc());
        // Encoding
        meta.setEncoding(aroVVisCompAip.getCdEncodingHashFileCalc());
        // Formato Rappresentazione
        meta.setFormatoRappresentazione(aroVVisCompAip.getDsFormatoRapprCalc());
        // Descrizione Formato
        meta.setDescrizioneFormato(aroVVisCompAip.getDsFormatoRapprFileDoc());
        // Formato Rappresentazione Esteso
        meta.setFormatoRappresentazioneEsteso(aroVVisCompAip.getDsFormatoRapprEstesoCalc());
        // Formato Componente Sbustato
        meta.setFormatoComponenteSbustato(aroVVisCompAip.getNmFormatoCalc());
        // Esito Controllo Formato
        meta.setEsitoControlloFormato(aroVVisCompAip.getTiEsitoContrFormatoFile());
        // Messaggio Esito Controllo Formato
        meta.setMessaggioEsitoControlloFormato(aroVVisCompAip.getDsMsgEsitoContrFormato());
        // Tipo Componente
        meta.setTipoComponente(aroVVisCompAip.getNmTipoCompDoc());
        // Tipo Supporto
        meta.setTipoSupporto(TipoSupportoType.valueOf(aroVVisCompAip.getTiSupportoComp()));
        // Tipo Rappresentazione
        meta.setTipoRappresentazione(aroVVisCompAip.getNmTipoRapprComp());
        // Utilizzo Data Firma Per Riferimento Temporale
        if (aroVVisCompAip.getFlRifTempDataFirmaVers() != null) {
            meta.setUtilizzoDataFirmaPerRifTemp(aroVVisCompAip.getFlRifTempDataFirmaVers().equals("1"));
        }
        // Riferimento Temporale
        meta.setRiferimentoTemporale(aroVVisCompAip.getTmRifTempVers() != null
                ? XmlDateUtility.dateToXMLGregorianCalendar(aroVVisCompAip.getTmRifTempVers()) : null);
        // Descrizione Riferimento Temporale
        meta.setDescrizioneRiferimentoTemporale(aroVVisCompAip.getDsRifTempVers());
        // Ordine Presentazione
        AroCompDoc padre = aroCompDoc.getAroCompDoc();
        boolean isThisASottoComponente = padre != null;
        if (isThisASottoComponente) {
            meta.setOrdinePresentazione(padre.getNiOrdCompDoc().toString() + "-" + pos);
        } else {
            meta.setOrdinePresentazione(aroCompDoc.getNiOrdCompDoc().toString());
        }
        // Numero Componente
        meta.setNumeroComponente(aroVVisCompAip.getNiOrdCompDoc().toBigInteger());
        // Urn Versato
        meta.setUrnVersato(aroVVisCompAip.getDlUrnCompVers());
        // Nome Componente
        meta.setNomeComponente(aroVVisCompAip.getDsNomeCompVers());
        // Formato Versato
        meta.setFormatoVersato(aroVVisCompAip.getNmFormatoVers());
        // Mimetype
        meta.setMimetype(aroVVisCompAip.getNmMimetypeFile());
        // Hash Versato
        meta.setHashVersato(aroVVisCompAip.getDsHashFileVers());
        // ID Componente
        meta.setIDComponente(aroVVisCompAip.getDsIdCompVers());
        // Dati specifici del Componente
        it.eng.parer.ws.xml.usvdcResp.DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.VERS,
                TipiEntitaSacer.COMP, aroCompDoc.getIdCompDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        dati = null;
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.MIGRAZ, TipiEntitaSacer.COMP, aroCompDoc.getIdCompDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }

    }

    /**
     * Popola i dati aggiornabili di MoreInfo del COMPONENTE di FileGroup da ARO_VERS_INI_COMP
     *
     */
    private void aggiornaMetadatiIntegratiExtraInfoFileVersIniUpd(MetadatiIntegratiFileType meta,
            AroVersIniComp aroVersIniComp) {
        // Urn Versato
        meta.setUrnVersato(aroVersIniComp.getDlUrnCompVers());
        // Nome Componente
        meta.setNomeComponente(aroVersIniComp.getDsNomeCompVers());
        // ID Componente
        meta.setIDComponente(aroVersIniComp.getDsIdCompVers());
        // Dati specifici del Componente
        it.eng.parer.ws.xml.usvdcResp.DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincroVersIniUpd(
                TiUsoXsdAroVersIniDatiSpec.VERS, TiEntitaSacerAroVersIniDatiSpec.COMP,
                aroVersIniComp.getIdVersIniComp());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        dati = null;
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.MIGRAZ,
                TiEntitaSacerAroVersIniDatiSpec.COMP, aroVersIniComp.getIdVersIniComp());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }
    }

    /**
     * Popola i dati aggiornabili di MoreInfo del COMPONENTE di FileGroup da ARO_UPD_COMP_UNITA_DOC
     *
     */
    private void aggiornaMetadatiIntegratiExtraInfoFileUpd(MetadatiIntegratiFileType meta,
            AroUpdCompUnitaDoc aroUpdCompUnitaDoc) {
        // Urn Versato
        meta.setUrnVersato(aroUpdCompUnitaDoc.getDlUrnCompVers());
        // Nome Componente
        meta.setNomeComponente(aroUpdCompUnitaDoc.getDsNomeCompVers());
        // ID Componente
        meta.setIDComponente(aroUpdCompUnitaDoc.getDsIdCompVers());
        // Dati specifici del Componente
        it.eng.parer.ws.xml.usvdcResp.DatiSpecificiTypeVdC dati = this.caricaDatiSpecUniSincroUpd(
                TiUsoXsdAroUpdDatiSpecUnitaDoc.VERS, TiEntitaAroUpdDatiSpecUnitaDoc.UPD_COMP,
                aroUpdCompUnitaDoc.getIdUpdCompUnitaDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        dati = null;
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_COMP, aroUpdCompUnitaDoc.getIdUpdCompUnitaDoc());
        if (dati != null && dati.getAny() != null && dati.getAny().size() > 0) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }
    }

    private DatiSpecificiTypeVdC caricaDatiSpecUniSincro(TipiUsoDatiSpec tipoUsoAttr, TipiEntitaSacer tipoEntitySacer,
            long idEntitySacer) {
        DatiSpecificiTypeVdC tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntity(tipoUsoAttr, tipoEntitySacer, idEntitySacer);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<Object[]> tmpDati = (List<Object[]>) rispostaControlli.getrObject();
            if (!tmpDati.isEmpty()) {
                tmpDatiSpecifici = new DatiSpecificiTypeVdC();
                tmpDatiSpecifici.setVersioneDatiSpecifici(((Object[]) tmpDati.get(0))[0].toString());

                DocumentBuilder db = null;
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    db = dbf.newDocumentBuilder();
                } catch (Exception e) {
                }

                Document doc = db.newDocument();
                for (Object[] tmpArr : tmpDati) {
                    Element el = doc.createElement(tmpArr[1].toString());
                    el.insertBefore(doc.createTextNode(tmpArr[2] != null ? tmpArr[2].toString() : ""),
                            el.getLastChild());
                    tmpDatiSpecifici.getAny().add(el);
                }
            }
        }

        return tmpDatiSpecifici;
    }

    private DatiSpecificiTypeVdC caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec tipoUsoAttr,
            TiEntitaSacerAroVersIniDatiSpec tipoEntitySacer, long idEntitySacer) {
        DatiSpecificiTypeVdC tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntityVersIniUpd(tipoUsoAttr, tipoEntitySacer,
                idEntitySacer);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVersIniDatiSpec> tmpDati = (List<AroVersIniDatiSpec>) rispostaControlli.getrObject();
            if (!tmpDati.isEmpty()) {
                // TODO: DA CENTRALIZZARE LETTURA CLOB
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    String blXmlDatiSpec = tmpDati.get(0).getBlXmlDatiSpec();
                    byte[] xml = blXmlDatiSpec.getBytes("UTF-8");
                    InputSource is = new InputSource(new StringReader(new String(xml, "UTF-8")));
                    Document docxml = db.parse(is);
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String queryXml = "//DatiSpecifici/VersioneDatiSpecifici";
                    XPathExpression expr = xPath.compile(queryXml);
                    String versioneDatiSpecifici = expr.evaluate(docxml);
                    queryXml = "//DatiSpecifici/*[position()>1]";
                    expr = xPath.compile(queryXml);
                    NodeList nodeList = (NodeList) expr.evaluate(docxml, XPathConstants.NODESET);

                    tmpDatiSpecifici = new DatiSpecificiTypeVdC();
                    tmpDatiSpecifici.setVersioneDatiSpecifici(versioneDatiSpecifici);
                    Document doc = db.newDocument();
                    for (int idx = 0; idx < nodeList.getLength(); idx++) {
                        Node node = nodeList.item(idx);
                        String name = node.getNodeName();
                        String value = node.getTextContent();

                        Element el = doc.createElement(name);
                        el.insertBefore(doc.createTextNode(value != null ? value : ""), el.getLastChild());
                        tmpDatiSpecifici.getAny().add(el);
                    }
                } catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException
                        | SAXException ex) {
                    log.error("ERRORE nel parsing ", ex);
                    throw new RuntimeException("ERRORE nel parsing ", ex);
                }
            }
        }

        return tmpDatiSpecifici;
    }

    private DatiSpecificiTypeVdC caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc tipoUsoAttr,
            TiEntitaAroUpdDatiSpecUnitaDoc tipoEntitySacer, long idEntitySacerUpd) {
        DatiSpecificiTypeVdC tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntityUpd(tipoUsoAttr, tipoEntitySacer,
                idEntitySacerUpd);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroUpdDatiSpecUnitaDoc> tmpDati = (List<AroUpdDatiSpecUnitaDoc>) rispostaControlli.getrObject();
            if (!tmpDati.isEmpty()) {
                // TODO: DA CENTRALIZZARE LETTURA CLOB
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    String blXmlDatiSpec = tmpDati.get(0).getBlXmlDatiSpec();
                    byte[] xml = blXmlDatiSpec.getBytes("UTF-8");
                    InputSource is = new InputSource(new StringReader(new String(xml, "UTF-8")));
                    Document docxml = db.parse(is);
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String queryXml = "//DatiSpecifici/VersioneDatiSpecifici";
                    XPathExpression expr = xPath.compile(queryXml);
                    String versioneDatiSpecifici = expr.evaluate(docxml);
                    queryXml = "//DatiSpecifici/*[position()>1]";
                    expr = xPath.compile(queryXml);
                    NodeList nodeList = (NodeList) expr.evaluate(docxml, XPathConstants.NODESET);

                    tmpDatiSpecifici = new DatiSpecificiTypeVdC();
                    tmpDatiSpecifici.setVersioneDatiSpecifici(versioneDatiSpecifici);
                    Document doc = db.newDocument();
                    for (int idx = 0; idx < nodeList.getLength(); idx++) {
                        Node node = nodeList.item(idx);
                        String name = node.getNodeName();
                        String value = node.getTextContent();

                        Element el = doc.createElement(name);
                        el.insertBefore(doc.createTextNode(value != null ? value : ""), el.getLastChild());
                        tmpDatiSpecifici.getAny().add(el);
                    }
                } catch (IOException | ParserConfigurationException | XPathExpressionException | DOMException
                        | SAXException ex) {
                    log.error("ERRORE nel parsing ", ex);
                    throw new RuntimeException("ERRORE nel parsing ", ex);
                }
            }
        }

        return tmpDatiSpecifici;
    }

    /**
     * Decora l'elemento File di FileGroup
     *
     */
    private FileType popolaComponenteFile(AroCompDoc aroCompDoc, AroVersIniUnitaDoc aroVersIniUnitaDoc,
            AroUpdUnitaDoc aroUpdUnitaDoc, int pos) {
        FileType fileComp = new FileType();
        // ID
        IdentifierType idFile = new IdentifierType();
        // String idFileContent = aroCompDoc.getDsUrnCompCalc().replace(' ', '_');
        AroCompUrnCalc urn = unitaDocumentarieHelper.findAroCompUrnCalcByType(aroCompDoc, TiUrn.NORMALIZZATO);
        String idFileContent = urn.getDsUrn();
        idFile.setValue(idFileContent);
        fileComp.setID(idFile);
        // Hash
        HashType hash = new HashType();
        hash.setValue(aroCompDoc.getDsHashFileCalc());
        // MAC#25654
        String function = aroCompDoc.getDsAlgoHashFileCalc() != null ? aroCompDoc.getDsAlgoHashFileCalc()
                : "Non Definito";
        hash.setFunction(function);
        // end MAC#25654
        fileComp.setHash(hash);
        rispostaControlli = controlliRecIndiceAip.leggiComponenteDaVista(aroCompDoc.getIdCompDoc());
        AroVVisCompAip aroVVisCompAip = (AroVVisCompAip) rispostaControlli.getrObject();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            String mymetype = aroVVisCompAip.getNmMimetypeFile() != null ? aroVVisCompAip.getNmMimetypeFile()
                    : "Non Definito";
            fileComp.setFormat(mymetype);
        }

        // MAC#25856
        /*
         * // Previous Hash String dsHashFileVers = aroCompDoc.getDsHashFileVers(); if (dsHashFileVers != null &&
         * !dsHashFileVers.isEmpty()) { PreviousHashType pHash = new PreviousHashType(); // MAC#25654 function =
         * aroCompDoc.getDsAlgoHashFileVers() != null ? aroCompDoc.getDsAlgoHashFileVers() : "Non Definito";
         * pHash.setFunction(function); // end MAC#25654 pHash.setRelatedIdC("Non_definito");
         * pHash.setValue(aroCompDoc.getDsHashFileVers()); fileComp.setPreviousHash(pHash); } // end MAC#25856
         */

        // More Info
        MoreInfoType moreInfoFile = new MoreInfoType();
        moreInfoFile.setXMLScheme("Unisincro_MoreInfoFile_v1.1.xsd");
        EmbeddedMetadataType emFile = new EmbeddedMetadataType();
        moreInfoFile.setEmbeddedMetadata(emFile);
        MetadatiIntegratiFileType mdif = new MetadatiIntegratiFileType();
        popolaMetadatiIntegratiExtraInfoFile(mdif, aroCompDoc, pos, aroVVisCompAip);
        if (aroVersIniUnitaDoc != null) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecIndiceAip
                    .leggiComponenteDaVersIniUpd(aroVersIniUnitaDoc.getIdVersIniUnitaDoc(), aroCompDoc.getIdCompDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                AroVersIniComp aroVersIniComp = (AroVersIniComp) rispostaControlli.getrObject();
                if (aroVersIniComp != null) {
                    aggiornaMetadatiIntegratiExtraInfoFileVersIniUpd(mdif, aroVersIniComp);
                }
            }
        }
        if (aroUpdUnitaDoc != null) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecIndiceAip.leggiComponenteDaUpd(aroUpdUnitaDoc.getIdUpdUnitaDoc(),
                    aroCompDoc.getIdCompDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                AroUpdCompUnitaDoc aroUpdCompUnitaDoc = (AroUpdCompUnitaDoc) rispostaControlli.getrObject();
                if (aroUpdCompUnitaDoc != null) {
                    aggiornaMetadatiIntegratiExtraInfoFileUpd(mdif, aroUpdCompUnitaDoc);
                }
            }
        }
        it.eng.parer.ws.xml.usfileResp.ObjectFactory objFct_4 = new it.eng.parer.ws.xml.usfileResp.ObjectFactory();
        emFile.setAny(objFct_4.createMetadatiIntegratiFile(mdif));
        moreInfoFile.setEmbeddedMetadata(emFile);
        fileComp.setMoreInfo(moreInfoFile);
        return fileComp;
    }

}
