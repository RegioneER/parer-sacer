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

package it.eng.parer.job.indiceAip.utils;

import static it.eng.parer.ws.utils.Costanti.UKNOWN_EXT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper.FormatoFileDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.async.utils.IOUtils;
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
import it.eng.parer.entity.AroVersIniUnitaDoc;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.VrsXmlModelloSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiUsoXsdAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiUsoXsdAroVersIniDatiSpec;
import it.eng.parer.entity.constraint.DecModelloXsdUd;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerRuntimeException;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.job.dto.SessioneVersamentoExt.DatiXml;
import it.eng.parer.job.indiceAip.helper.ControlliRecIndiceAip;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.viewEntity.AroVVisCompAip;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.AgentLegalPersonDto;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recupero.utils.RecuperoZipGen;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaSacer;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.CostantiDB.TipiUsoDatiSpec;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.usdocResp.MetadatiIntegratiDocType;
import it.eng.parer.ws.xml.usdocResp.ProfiloDocumentoType;
import it.eng.parer.ws.xml.usfileResp.MetadatiIntegratiFileType;
import it.eng.parer.ws.xml.usfileResp.TipoSupportoType;
import it.eng.parer.ws.xml.usmainRespV2.Agent;
import it.eng.parer.ws.xml.usmainRespV2.AgentID;
import it.eng.parer.ws.xml.usmainRespV2.AgentName;
import it.eng.parer.ws.xml.usmainRespV2.CreatingApplication;
import it.eng.parer.ws.xml.usmainRespV2.EmbeddedMetadata;
import it.eng.parer.ws.xml.usmainRespV2.File;
import it.eng.parer.ws.xml.usmainRespV2.FileGroup;
import it.eng.parer.ws.xml.usmainRespV2.Hash;
import it.eng.parer.ws.xml.usmainRespV2.ID;
import it.eng.parer.ws.xml.usmainRespV2.MoreInfo;
import it.eng.parer.ws.xml.usmainRespV2.NameAndSurname;
import it.eng.parer.ws.xml.usmainRespV2.PIndex;
import it.eng.parer.ws.xml.usmainRespV2.PIndexID;
import it.eng.parer.ws.xml.usmainRespV2.PIndexSource;
import it.eng.parer.ws.xml.usmainRespV2.PVolume;
import it.eng.parer.ws.xml.usmainRespV2.PVolumeGroup;
import it.eng.parer.ws.xml.usmainRespV2.PVolumeSource;
import it.eng.parer.ws.xml.usmainRespV2.Process;
import it.eng.parer.ws.xml.usmainRespV2.RelevantDocument;
import it.eng.parer.ws.xml.usmainRespV2.SelfDescription;
import it.eng.parer.ws.xml.usmainRespV2.TimeInfo;
import it.eng.parer.ws.xml.usmainRespV2.TimeReference;
import it.eng.parer.ws.xml.uspvolumeRespV2.CamiciaFascicoloType;
import it.eng.parer.ws.xml.uspvolumeRespV2.ChiaveType;
import it.eng.parer.ws.xml.uspvolumeRespV2.ComposizioneType;
import it.eng.parer.ws.xml.uspvolumeRespV2.DatiSpecificiTypePVolume;
import it.eng.parer.ws.xml.uspvolumeRespV2.DocumentoCollegatoType;
import it.eng.parer.ws.xml.uspvolumeRespV2.FascicoloType;
import it.eng.parer.ws.xml.uspvolumeRespV2.MetadatiIntegratiPdAType;
import it.eng.parer.ws.xml.uspvolumeRespV2.NotaType;
import it.eng.parer.ws.xml.uspvolumeRespV2.ProfiloArchivisticoType;
import it.eng.parer.ws.xml.uspvolumeRespV2.ProfiloNormativoType;
import it.eng.parer.ws.xml.uspvolumeRespV2.ProfiloUnitaDocumentariaType;
import it.eng.parer.ws.xml.uspvolumeRespV2.VersatoreType;
import it.eng.parer.ws.xml.usselfdescRespV2.IndiceAIPType;
import it.eng.parer.ws.xml.usselfdescRespV2.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.ws.xml.versReqStato.Recupero;

/**
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "CreazioneIndiceAipUtilV2")
@LocalBean
public class CreazioneIndiceAipUtilV2 {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipUtilV2.class);
    public static final String NON_DEFINITO = "Non Definito";
    private String hashFunction = TipiHash.SHA_256.descrivi();
    private String schemeAttribute = Costanti.SchemeAttributes.SCHEME_LOCAL;
    private RispostaControlli rispostaControlli;
    @EJB
    ControlliRecIndiceAip controlliRecIndiceAip;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    @EJB
    ControlliRecupero controlliRecupero;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    @EJB
    UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    AmbientiHelper ambientiHelper;
    @EJB
    UserHelper userHelper;
    @EJB
    FormatoFileDocHelper formatoFileDocHelper = null;
    @EJB
    private RecuperoZipGen zipGen;

    public CreazioneIndiceAipUtilV2() {
        rispostaControlli = new RispostaControlli();
    }

    private void setRispostaError() {
        log.error(
                "Creazione Indice AIP v2.0 - Errore nella creazione dell'istanza di conservazione UniSyncro (PIndex): {}",
                rispostaControlli.getDsErr());
        throw new SacerRuntimeException(rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr(),
                SacerErrorCategory.INTERNAL_ERROR);
    }

    /**
     * Riceve l'unità documentaria da elaborare e crea il PIndex
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
     * @return entity PIndex
     * 
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NamingException
     *             errore generico
     * @throws java.security.NoSuchAlgorithmException
     *             errore generico
     */
    public PIndex generaIndiceAIPV2(AroIndiceAipUdDaElab aro, String codiceVersione, String cdVersioneXSDIndiceAIP,
            String sistemaConservazione, Map<String, String> mappaAgenti, String creatingApplicationProducer)
            throws IOException, NamingException, NoSuchAlgorithmException {
        PIndex istanzaUnisincro = new PIndex();
        List<SessioneVersamentoExt> sessioniVersamentoList = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAip(aro.getIdIndiceAipDaElab());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            sessioniVersamentoList = (List<SessioneVersamentoExt>) rispostaControlli.getrObject();
        }

        popolaPIndex(istanzaUnisincro, aro.getAroUnitaDoc(), codiceVersione, sistemaConservazione,
                cdVersioneXSDIndiceAIP, sessioniVersamentoList, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    /**
     * Riceve l'unità documentaria e crea il PIndex
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
     * @return entity PIndex
     * 
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NamingException
     *             errore generico
     * @throws java.security.NoSuchAlgorithmException
     *             errore generico
     */
    public PIndex generaIndiceAIPV2(AroUnitaDoc aro, String codiceVersione, String cdVersioneXSDIndiceAIP,
            String sistemaConservazione, Map<String, String> mappaAgenti, String creatingApplicationProducer)
            throws IOException, NamingException, NoSuchAlgorithmException {
        PIndex istanzaUnisincro = new PIndex();
        List<SessioneVersamentoExt> sessioniVersamentoList = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAipDaUnitaDoc(aro.getIdUnitaDoc());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            sessioniVersamentoList = (List<SessioneVersamentoExt>) rispostaControlli.getrObject();
        }
        popolaPIndex(istanzaUnisincro, aro, codiceVersione, sistemaConservazione, cdVersioneXSDIndiceAIP,
                sessioniVersamentoList, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    private void popolaPIndex(PIndex pIndex, AroUnitaDoc aroUnitaDoc, String codiceVersione,
            String sistemaConservazione, String cdVersioneXSDIndiceAIP,
            List<SessioneVersamentoExt> sessioniVersamentoList, Map<String, String> mappaAgenti,
            String creatingApplicationProducer) throws IOException, NamingException, NoSuchAlgorithmException {
        AroUnitaDoc tmpAroUnitaDoc = null;
        AroVersIniUnitaDoc tmpAroVersIniUnitaDoc = null;
        AroUpdUnitaDoc tmpAroUpdUnitaDocPgMax = null;
        Date timeRef = new GregorianCalendar().getTime();

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
        SelfDescription selfDesc = new SelfDescription();
        /* ID */
        ID id = new ID();
        // EVO#16486
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave, true, Costanti.UrnFormatter.UD_FMT_STRING));
        // salvo NORMALIZZATO
        String urn = MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrnNorm, codiceVersione,
                Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2);
        id.setValue(urn);
        // end EVO#16486
        id.setScheme(schemeAttribute);
        selfDesc.setID(id);

        /* Creating Application */
        CreatingApplication applicazione = new CreatingApplication();
        applicazione.setName(StringUtils.capitalize(Constants.SACER.toLowerCase()));
        applicazione.setProducer(creatingApplicationProducer);
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVersioneSacer();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            applicazione.setVersion(rispostaControlli.getrString());
        }
        selfDesc.setCreatingApplication(applicazione);

        // MEV#20971
        /* PIndexSource */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVersioniPrecedentiAIP(idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVerIndiceAipUd> versioniPrecedentiSacer = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            List<PIndexSource> sorgenteArray = new ArrayList<>();
            for (int i = 0; i < versioniPrecedentiSacer.size(); i++) {
                PIndexSource sorgente = new PIndexSource();
                ID idPIndexSource = new ID();
                it.eng.parer.ws.xml.usmainRespV2.Hash hashPIndexSource = new it.eng.parer.ws.xml.usmainRespV2.Hash();
                // EVO#16486
                // Recupero urn di tipo ORIGINALE dell'Indice AIP dell'unità documentaria
                AroUrnVerIndiceAipUd aroUrnAipIndiceAipUdOrigPrec = (AroUrnVerIndiceAipUd) CollectionUtils.find(
                        versioniPrecedentiSacer.get(i).getAroUrnVerIndiceAipUds(),
                        object -> ((AroUrnVerIndiceAipUd) object).getTiUrn().equals(TiUrnVerIxAipUd.NORMALIZZATO));
                String urnIxAipNorm = aroUrnAipIndiceAipUdOrigPrec.getDsUrn();
                idPIndexSource.setValue(urnIxAipNorm);
                idPIndexSource.setScheme(schemeAttribute);
                // end EVO#16486
                sorgente.setID(idPIndexSource);
                /* Definisco la folder relativa al sistema di conservazione */
                String folder = IOUtils.getPath("/pindexsource", StringUtils.capitalize(Constants.SACER.toLowerCase()),
                        IOUtils.UNIX_FILE_SEPARATOR);
                /* Definisco il nome e l'estensione del file */
                String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(urnIxAipNorm, true),
                        IOUtils.CONTENT_TYPE.XML.getFileExt());
                /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                String pathPIndexSource = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                sorgente.setPath(pathPIndexSource);
                // MAC#18826
                hashPIndexSource.setValue(versioniPrecedentiSacer.get(i).getDsHashIndiceAip());
                hashPIndexSource.setCanonicalXML(Boolean.FALSE);
                hashPIndexSource.setHashFunction(versioniPrecedentiSacer.get(i).getDsAlgoHashIndiceAip());
                sorgente.setHash(hashPIndexSource);
                sorgente.setDerivation("onetoone");
                sorgenteArray.add(sorgente);
            }
            selfDesc.getPIndexSource().addAll(sorgenteArray);
        }

        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVersioniPrecedentiAIPExternal(idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVLisaipudSistemaMigraz> versioniPrecedentiExternal = (List<AroVLisaipudSistemaMigraz>) rispostaControlli
                    .getrObject();
            List<PIndexSource> sorgenteArray = new ArrayList<>();
            for (int i = 0; i < versioniPrecedentiExternal.size(); i++) {
                PIndexSource sorgente = new PIndexSource();
                ID idPIndexSource = new ID();
                it.eng.parer.ws.xml.usmainRespV2.Hash hashPIndexSource = new it.eng.parer.ws.xml.usmainRespV2.Hash();
                // Calcolo urn di tipo NORMALIZZATO dell'UD con l'indice unisincro di altri conservatori
                CSChiave csChiaveUDAIPExt = new CSChiave();
                csChiaveUDAIPExt.setAnno(versioniPrecedentiExternal.get(i).getAaKeyUnitaDocAip().longValue());
                csChiaveUDAIPExt.setNumero(versioniPrecedentiExternal.get(i).getCdKeyUnitaDocAip());
                csChiaveUDAIPExt.setTipoRegistro(versioniPrecedentiExternal.get(i).getCdRegistroKeyUnitaDocAip());
                CSVersatore csVersatoreUDAIPExt = new CSVersatore();
                csVersatoreUDAIPExt.setSistemaConservazione(sistemaConservazione); // TODO: VERIFICARE SISTEMA DI
                // CONSERVAZIONE
                csVersatoreUDAIPExt.setAmbiente(versioniPrecedentiExternal.get(i).getNmAmbiente());
                csVersatoreUDAIPExt.setEnte(versioniPrecedentiExternal.get(i).getNmEnte());
                csVersatoreUDAIPExt.setStruttura(versioniPrecedentiExternal.get(i).getNmStrut());
                String urnUDAIPExtNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreUDAIPExt, true,
                                Costanti.UrnFormatter.VERS_FMT_STRING),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiaveUDAIPExt, true,
                                Costanti.UrnFormatter.UD_FMT_STRING));
                idPIndexSource.setValue(urnUDAIPExtNorm);
                idPIndexSource.setScheme(schemeAttribute);
                sorgente.setID(idPIndexSource);
                /* Definisco la folder relativa al sistema di conservazione */
                String folder = IOUtils.getPath("/pindexsource", versioniPrecedentiExternal.get(i).getNmSistemaMigraz(),
                        IOUtils.UNIX_FILE_SEPARATOR);
                /* Definisco il nome e l'estensione del file */
                String fileName = IOUtils.getFilename(MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiaveUDAIPExt),
                        IOUtils.CONTENT_TYPE.ZIP.getFileExt());
                /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                String pathPIndexSource = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                sorgente.setPath(pathPIndexSource);
                // Calcola l'hash SHA-256 dello zip contenente i file dell'UD dell'Indice AIP unisincro di altro
                // conservatore
                hashPIndexSource.setValue(this.calcolaHashZipFileUd(idUnitaDoc,
                        versioniPrecedentiExternal.get(i).getCdRegistroKeyUnitaDocAip(),
                        versioniPrecedentiExternal.get(i).getAaKeyUnitaDocAip(),
                        versioniPrecedentiExternal.get(i).getCdKeyUnitaDocAip()));
                hashPIndexSource.setCanonicalXML(Boolean.FALSE);
                hashPIndexSource.setHashFunction(hashFunction);
                sorgente.setHash(hashPIndexSource);
                sorgente.setDerivation("onetomany");
                sorgenteArray.add(sorgente);
            }
            selfDesc.getPIndexSource().addAll(sorgenteArray);
        }

        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.getVolumiUnitaDocList(idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VolVolumeConserv> volumiConservList = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            List<PIndexSource> sorgenteArray = new ArrayList<>();
            for (int i = 0; i < volumiConservList.size(); i++) {
                PIndexSource sorgente = new PIndexSource();
                ID idPIndexSource = new ID();
                it.eng.parer.ws.xml.usmainRespV2.Hash hashPIndexSource = new it.eng.parer.ws.xml.usmainRespV2.Hash();
                // Calcolo urn di tipo NORMALIZZATO dell'indice del volume di conservazione
                CSVersatore csVersatoreVol = new CSVersatore();
                csVersatoreVol.setSistemaConservazione(sistemaConservazione);
                csVersatoreVol.setAmbiente(
                        volumiConservList.get(i).getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
                csVersatoreVol.setEnte(volumiConservList.get(i).getOrgStrut().getOrgEnte().getNmEnte());
                csVersatoreVol.setStruttura(volumiConservList.get(i).getOrgStrut().getNmStrut());
                String urnIxVolConsNorm = MessaggiWSFormat.formattaUrnIndiceVolumeConserv(
                        MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreVol, true,
                                Costanti.UrnFormatter.VERS_FMT_STRING),
                        Long.toString(volumiConservList.get(i).getIdVolumeConserv()));
                idPIndexSource.setValue(urnIxVolConsNorm);
                idPIndexSource.setScheme(schemeAttribute);
                sorgente.setID(idPIndexSource);
                /* Definisco la folder relativa al sistema di conservazione */
                String folder = IOUtils.getPath("/pindexsource", StringUtils.capitalize(Constants.SACER.toLowerCase()),
                        IOUtils.UNIX_FILE_SEPARATOR);
                /* Definisco il nome e l'estensione del file */
                String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(urnIxVolConsNorm, true),
                        IOUtils.CONTENT_TYPE.ZIP.getFileExt());
                /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                String pathPIndexSource = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                sorgente.setPath(pathPIndexSource);
                // Calcola l'hash SHA-256 dello zip contenente i file del Volume di Conservazione
                hashPIndexSource
                        .setValue(this.calcolaHashZipProveConservazione(volumiConservList.get(i).getIdVolumeConserv()));
                hashPIndexSource.setCanonicalXML(Boolean.FALSE);
                hashPIndexSource.setHashFunction(hashFunction);
                sorgente.setHash(hashPIndexSource);
                sorgente.setDerivation("onetomany");
                sorgenteArray.add(sorgente);
            }
            selfDesc.getPIndexSource().addAll(sorgenteArray);
        }
        // end MEV#20971

        /* More Info */
        MoreInfo moreInfoApplic = new MoreInfo();
        moreInfoApplic.setXmlSchema("/xmlschema/Unisincro_MoreInfoSelfDescription_v2.0.xsd");
        EmbeddedMetadata extraInfoDescGenerale = new EmbeddedMetadata();
        MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
        IndiceAIPType indiceAIP = new IndiceAIPType();
        indiceAIP.setDataCreazione(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        indiceAIP.setFormato("UNI SInCRO 2.0 (UNI 11386:2020)");
        indiceAIP.setVersioneIndiceAIP(codiceVersione);
        indiceAIP.setVersioneXSDIndiceAIP(cdVersioneXSDIndiceAIP);
        miSelfD.setIndiceAIP(indiceAIP);

        it.eng.parer.ws.xml.usselfdescRespV2.ObjectFactory objFct1 = new it.eng.parer.ws.xml.usselfdescRespV2.ObjectFactory();
        extraInfoDescGenerale.setAny(objFct1.createMetadatiIntegratiSelfDescription(miSelfD));
        moreInfoApplic.setEmbeddedMetadata(extraInfoDescGenerale);
        selfDesc.setMoreInfo(moreInfoApplic);
        pIndex.setSelfDescription(selfDesc);

        /*
         * *********** DECORO PVOLUME ***********
         */
        PVolume pVolume = new PVolume();
        /* ID */
        ID idPVolume = new ID();
        // EVO#16486
        String urnPartVersatoreNorm = MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        String urnPartChiaveUdNorm = MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave, true,
                Costanti.UrnFormatter.UD_FMT_STRING);
        // MEV#25872
        String urnAIPUD = MessaggiWSFormat.formattaUrnAipUdAip(urnPartVersatoreNorm, urnPartChiaveUdNorm);
        idPVolume.setValue(urnAIPUD);
        // end MEV#25872
        // end EVO#16486
        idPVolume.setScheme(schemeAttribute);
        pVolume.setID(idPVolume);
        pVolume.setLabel("Pacchetti di archiviazione (AIP) di un'Unità documentaria");
        String desc = "File relativi ai Documenti appartenenti alla medesima Unità documentaria e ai relativi Pacchetti di versamento (PdV). Ogni Documento e ogni PdV è aggregato in un FileGroup che contiene i File che lo compongono";
        pVolume.setDescription(desc);
        /* PVolumeSource */
        List<PVolumeSource> sorgenteArray = new ArrayList<>();
        for (int i = 0; i < selfDesc.getPIndexSource().size(); i++) {
            PVolumeSource sorgente = new PVolumeSource();
            /* ID */
            ID idPVolumeSource = new ID();
            String tmpPartUrnName = IOUtils.extractPartUrnName(selfDesc.getPIndexSource().get(i).getID().getValue());
            if (tmpPartUrnName.toUpperCase().startsWith("INDICEAIP-UD")) {
                idPVolumeSource.setValue(idPVolume.getValue());
            } else {
                idPVolumeSource.setValue(selfDesc.getPIndexSource().get(i).getID().getValue());
            }
            idPVolumeSource.setScheme(schemeAttribute);
            sorgente.setID(idPVolumeSource);
            /* PIndexID */
            PIndexID pIndexId = new PIndexID();
            pIndexId.setValue(selfDesc.getPIndexSource().get(i).getID().getValue());
            pIndexId.setScheme(schemeAttribute);
            sorgente.setPIndexID(pIndexId);
            sorgenteArray.add(sorgente);
        }
        pVolume.getPVolumeSource().addAll(sorgenteArray);

        /* PVolumeGroup */
        PVolumeGroup pVolumeGruppo = new PVolumeGroup();
        // ID
        ID idPVolumeGruppo = new ID();
        idPVolumeGruppo.setValue(tmpAroUnitaDoc.getCdRegistroKeyUnitaDoc());
        idPVolumeGruppo.setScheme(schemeAttribute);
        pVolumeGruppo.setID(idPVolumeGruppo);
        // Label
        String label = "Registro o repertorio";
        pVolumeGruppo.setLabel(label);
        // Description
        DecRegistroUnitaDoc reg = unitaDocumentarieHelper.findById(DecRegistroUnitaDoc.class,
                tmpAroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc());
        if (reg != null) {
            String dsReg = reg.getDsRegistroUnitaDoc();
            pVolumeGruppo.setDescription(dsReg);
        }
        pVolume.setPVolumeGroup(pVolumeGruppo);
        /* MoreInfo */
        MoreInfo moreInfoPVolume = new MoreInfo();
        moreInfoPVolume.setXmlSchema("/xmlschema/Unisincro_MoreInfoPVolume_v2.0.xsd");
        EmbeddedMetadata emdpvolume = new EmbeddedMetadata();
        MetadatiIntegratiPdAType mipda = new MetadatiIntegratiPdAType();
        OrgStrut orgStrut = ambientiHelper.findOrgStrutById(tmpAroUnitaDoc.getOrgStrut().getIdStrut());

        // metadati PDA
        this.popolaMetadatiIntegratiPdA(mipda, tmpAroUnitaDoc, tmpAroVersIniUnitaDoc, tmpAroUpdUnitaDocPgMax, orgStrut,
                sistemaConservazione);

        // Composizione
        ComposizioneType composizione = new ComposizioneType();
        mipda.setComposizione(composizione);
        it.eng.parer.ws.xml.uspvolumeRespV2.ObjectFactory objFct2 = new it.eng.parer.ws.xml.uspvolumeRespV2.ObjectFactory();
        emdpvolume.setAny(objFct2.createMetadatiIntegratiPdA(mipda));
        moreInfoPVolume.setEmbeddedMetadata(emdpvolume);
        pVolume.setMoreInfo(moreInfoPVolume);
        pIndex.setPVolume(pVolume);

        /*
         * ***************** NORMALIZZO UD E CALCOLO URN COMPONENTI *****************
         */
        unitaDocumentarieHelper.normalizzaUDAndCalcUrnOrigNormalizComp(tmpAroUnitaDoc.getIdUnitaDoc(),
                Arrays.asList(TiUrn.ORIGINALE, TiUrn.NORMALIZZATO));
        /*
         * ***************** DECORO FILEGROUP *****************
         */
        this.popolaFileGroupList(pIndex, composizione, sessioniVersamentoList, tmpAroUnitaDoc, tmpAroVersIniUnitaDoc,
                tmpAroUpdUnitaDocPgMax, csChiave, csVersatore);

        /*
         * *************** DECORO PROCESS ***************
         */
        SIOrgEnteSiam orgEnteConvenz = null;
        if (orgStrut.getIdEnteConvenz() != null) {
            orgEnteConvenz = unitaDocumentarieHelper.findById(SIOrgEnteSiam.class, orgStrut.getIdEnteConvenz());
        }
        BigDecimal idAmbiente = BigDecimal.valueOf(orgStrut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        Process processo = new Process();
        /* Primo Agent */
        // SUBMITTER
        Agent primoAgenteSubmitter = new Agent();
        // Submitter attributes
        primoAgenteSubmitter.setAgentType("legal person");
        // Submitter.AgentID
        AgentID primoAgenteID = new AgentID();
        // MAC#26215
        if (orgEnteConvenz != null) {
            primoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
        }
        // end MAC#26215
        primoAgenteSubmitter.getAgentID().add(primoAgenteID);
        // Submitter.AgentName
        AgentName primoAgenteNome = new AgentName();
        // Submitter.AgentName.FormalName
        if (orgEnteConvenz != null) {
            primoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
        } else {
            primoAgenteNome.setFormalName(orgStrut.getOrgEnte().getDsEnte());
        }
        primoAgenteSubmitter.setAgentName(primoAgenteNome);
        // Submitter.RelevantDocument
        // MEV#25903
        String[] submtrRelevantDocuments = mappaAgenti.get(AGENT_SUBMITTER_RELEVANTDOCUMENT).split("\\|");
        for (String relevantDocument : submtrRelevantDocuments) {
            RelevantDocument primoAgenteRelevantDocument = new RelevantDocument();
            primoAgenteRelevantDocument.setValue(relevantDocument);
            primoAgenteSubmitter.getRelevantDocument().add(primoAgenteRelevantDocument);
        }
        // end MEV#25903
        processo.setSubmitter(primoAgenteSubmitter);
        /* Secondo Agent */
        // HOLDER
        Process.Holder secondoAgenteHolder = new Process.Holder();
        // Holder attributes
        secondoAgenteHolder.setAgentType("legal person");
        secondoAgenteHolder.setHolderRole("soggetto produttore");
        // Holder.AgentID
        AgentID secondoAgenteID = new AgentID();
        // MAC#26215
        if (orgEnteConvenz != null) {
            secondoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
        }
        // end MAC#26215
        secondoAgenteHolder.getAgentID().add(secondoAgenteID);
        // Holder.AgentName
        AgentName secondoAgenteNome = new AgentName();
        // Holder.AgentName.FormalName
        if (orgEnteConvenz != null) {
            secondoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
        } else {
            secondoAgenteNome.setFormalName(orgStrut.getOrgEnte().getDsEnte());
        }
        secondoAgenteHolder.setAgentName(secondoAgenteNome);
        // Holder.RelevantDocument
        // MEV#25903
        String[] holderRelevantDocuments = mappaAgenti.get(AGENT_HOLDER_RELEVANTDOCUMENT).split("\\|");
        for (String relevantDocument : holderRelevantDocuments) {
            RelevantDocument secondoAgenteRelevantDocument = new RelevantDocument();
            secondoAgenteRelevantDocument.setValue(relevantDocument);
            secondoAgenteHolder.getRelevantDocument().add(secondoAgenteRelevantDocument);
        }
        // end MEV#25903
        processo.getHolder().add(secondoAgenteHolder);
        // LISTA AUTHORIZED SIGNER
        rispostaControlli.reset();
        rispostaControlli = controlliRecupero.leggiListaUserByHsmUsername(idAmbiente);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<UsrUser> authSignerList = (List<UsrUser>) rispostaControlli.getrObject();
            List<Process.AuthorizedSigner> authSignerArray = new ArrayList<>();
            for (int i = 0; i < authSignerList.size(); i++) {
                Process.AuthorizedSigner authorizedSigner = new Process.AuthorizedSigner();
                // AuthorizedSigner attributes:
                // AgentType
                authorizedSigner.setAgentType("natural person");
                // SignerRole
                rispostaControlli.reset();
                rispostaControlli = controlliRecupero.leggiRuoloAuthorizedSigner(authSignerList.get(i).getIdUserIam(),
                        idAmbiente);
                if (!rispostaControlli.isrBoolean()) {
                    setRispostaError();
                } else {
                    String signerRole = ((String[]) rispostaControlli.getrObject())[0];
                    authorizedSigner.setSignerRole(signerRole);
                }
                // AuthorizedSigner.AgentID
                AgentID agenteID = new AgentID();
                agenteID.setValue("TINIT-" + authSignerList.get(i).getCdFisc());
                authorizedSigner.getAgentID().add(agenteID);
                // AuthorizedSigner.AgentName
                AgentName agenteNome = new AgentName();
                // AuthorizedSigner.AgentName.NameAndSurname
                NameAndSurname nameAndSurname = new NameAndSurname();
                nameAndSurname.setFirstName(authSignerList.get(i).getNmNomeUser());
                nameAndSurname.setLastName(authSignerList.get(i).getNmCognomeUser());
                agenteNome.setNameAndSurname(nameAndSurname);
                authorizedSigner.setAgentName(agenteNome);
                // AuthorizedSigner.RelevantDocument
                RelevantDocument agenteRelevantDocument = new RelevantDocument();
                String relevantDocument = ((String[]) rispostaControlli.getrObject())[1];
                agenteRelevantDocument.setValue(relevantDocument);
                authorizedSigner.getRelevantDocument().add(agenteRelevantDocument);
                authSignerArray.add(authorizedSigner);
            }

            // MEV#27831 - Modifica creazione indice AIP in presenza di SIGILLO
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiAuthorizedSignerLegalPersons(idAmbiente);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                ArrayList<AgentLegalPersonDto> agentiLegal = (ArrayList<AgentLegalPersonDto>) rispostaControlli
                        .getrObject();
                // MAC#29103 - Risoluzione problema con parametro "AGENT_AUTHORIZED_SIGNER_ROLE_LEGAL_PERSON"
                // Se non ci sono agent non deve produrre il frammento di xml!
                if (!agentiLegal.isEmpty()) {
                    for (AgentLegalPersonDto dto : agentiLegal) {
                        Process.AuthorizedSigner authorizedSigner = new Process.AuthorizedSigner();
                        // AuthorizedSigner attributes:
                        // AgentType
                        authorizedSigner.setAgentType("legal person");
                        // SignerRole
                        authorizedSigner.setSignerRole(dto.getRuolo());
                        // AuthorizedSigner.AgentID
                        AgentID agenteID = new AgentID();
                        agenteID.setValue(dto.getId());
                        authorizedSigner.getAgentID().add(agenteID);
                        // AuthorizedSigner.AgentName
                        AgentName agentName = new AgentName();
                        // AuthorizedSigner.AgentName.NameAndSurname
                        agentName.setFormalName(dto.getNome());
                        authorizedSigner.setAgentName(agentName);
                        // AuthorizedSigner.RelevantDocument
                        RelevantDocument agenteRelevantDocument = new RelevantDocument();
                        agenteRelevantDocument.setValue(dto.getDocumentoRilevante());
                        authorizedSigner.getRelevantDocument().add(agenteRelevantDocument);
                        authSignerArray.add(authorizedSigner);
                    }
                }
            }
            // FINE - MEV#27831
            processo.getAuthorizedSigner().addAll(authSignerArray);
        }

        /* Time Reference */
        TimeReference tempo = new TimeReference();
        // TimeInfo
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setValue(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        timeInfo.setAttachedTimeStamp(false);
        tempo.setTimeInfo(timeInfo);
        processo.setTimeReference(tempo);
        /* Law And Regulations */
        String legge = "Linee Guida sulla formazione, gestione e conservazione dei documenti informatici (09-09-2020)";
        processo.setLawsAndRegulations(legge);
        pIndex.setProcess(processo);

        // PIndex attributes:
        pIndex.setLanguage("it");
        pIndex.setSincroVersion("2.0");
        pIndex.setUri("http://www.uni.com/U3011/sincro-v2/PIndex.xsd");
    }

    private void popolaMetadatiIntegratiPdA(MetadatiIntegratiPdAType mipda, AroUnitaDoc tmpAroUnitaDoc,
            AroVersIniUnitaDoc tmpAroVersIniUnitaDoc, AroUpdUnitaDoc tmpAroUpdUnitaDoc, OrgStrut orgStrut,
            String sistemaConservazione) {

        // Versatore
        VersatoreType versatore = new VersatoreType();
        versatore.setAmbiente(orgStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(orgStrut.getOrgEnte().getNmEnte());
        versatore.setStruttura(orgStrut.getNmStrut());
        versatore.setUserID(userHelper.findUserById(tmpAroUnitaDoc.getIamUser().getIdUserIam()).getNmUserid());
        mipda.setVersatore(versatore);
        // Chiave
        ChiaveType chiave = new ChiaveType();
        chiave.setRegistro(tmpAroUnitaDoc.getCdRegistroKeyUnitaDoc());
        chiave.setAnno(tmpAroUnitaDoc.getAaKeyUnitaDoc().toBigInteger());
        chiave.setNumero(tmpAroUnitaDoc.getCdKeyUnitaDoc());
        mipda.setChiave(chiave);

        // MEV#25872
        // UrnUD
        CSChiave csChiave = new CSChiave();
        csChiave.setAnno(chiave.getAnno().longValue());
        csChiave.setNumero(chiave.getNumero());
        csChiave.setTipoRegistro(chiave.getRegistro());

        CSVersatore csVersatore = new CSVersatore();
        csVersatore.setSistemaConservazione(sistemaConservazione);
        csVersatore.setAmbiente(versatore.getAmbiente());
        csVersatore.setEnte(versatore.getEnte());
        csVersatore.setStruttura(versatore.getStruttura());

        String urnPartVersatoreNorm = MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        String urnPartChiaveUdNorm = MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave, true,
                Costanti.UrnFormatter.UD_FMT_STRING);
        String urnUD = MessaggiWSFormat.formattaBaseUrnUnitaDoc(urnPartVersatoreNorm, urnPartChiaveUdNorm);
        mipda.setUrnUD(urnUD);
        // end MEV#25872

        // Data Acquisizione
        mipda.setDataAcquisizione(XmlDateUtility.dateToXMLGregorianCalendar(tmpAroUnitaDoc.getDtCreazione()));
        // TipologiaUnitaDocumentaria
        mipda.setTipologiaUnitaDocumentaria(unitaDocumentarieHelper
                .findNmTipoUnitaDocById(tmpAroUnitaDoc.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
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
        ProfiloArchivisticoType profiloArchivistico = new ProfiloArchivisticoType();
        popolaProfiloArchivistico(profiloArchivistico, tmpAroUnitaDoc);
        if (tmpAroVersIniUnitaDoc != null) {
            popolaProfiloArchivisticoVersIniUpd(profiloArchivistico, tmpAroVersIniUnitaDoc);
        }
        if (tmpAroUpdUnitaDoc != null) {
            popolaProfiloArchivisticoUpd(profiloArchivistico, tmpAroUpdUnitaDoc);
        }
        mipda.setProfiloArchivistico(profiloArchivistico);
        // MEV#26419
        // Profilo Normativo dell'UD
        mipda.setProfiloNormativo(this.caricaProfiloNormativoUd(tmpAroUnitaDoc.getIdUnitaDoc()));
        // end MEV#26419
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
            if (!tmpLstAroUDLink.isEmpty()) {
                DocumentoCollegatoType documentiCollegati = new DocumentoCollegatoType();
                for (AroLinkUnitaDoc tmpLinkUD : tmpLstAroUDLink) {
                    AroUnitaDoc aroUnitaDoc = unitaDocumentarieHelper.findById(AroUnitaDoc.class,
                            tmpLinkUD.getAroUnitaDoc().getIdUnitaDoc());
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
                    csVersatoreUDColl
                            .setAmbiente(aroUnitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
                    csVersatoreUDColl.setEnte(aroUnitaDoc.getOrgStrut().getOrgEnte().getNmEnte());
                    csVersatoreUDColl.setStruttura(aroUnitaDoc.getOrgStrut().getNmStrut());
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
                if (!tmpLstAroUDLinkVersIniUpd.isEmpty()) {
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
                if (!tmpLstAroUDLinkUpd.isEmpty()) {
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
        List<AroNotaUnitaDoc> tmpLstAroNotaUnitaDoc = unitaDocumentarieHelper
                .findAroNotaUnitaDocByIdUnitaDoc(tmpAroUnitaDoc);
        if (!tmpLstAroNotaUnitaDoc.isEmpty()) {
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
            if (!tmpLstAroArchivSecs.isEmpty()) {
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
            if (!tmpLstAroVersIniArchivSecs.isEmpty()) {
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
            if (!tmpLstAroUpdArchivSecs.isEmpty()) {
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

    // MEV#26419
    private ProfiloNormativoType caricaProfiloNormativoUd(long idUnitaDoc) {
        ProfiloNormativoType tmpProfiloNormativo = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiModelloXsdUnitaDoc(
                CostantiDB.TiUsoModelloXsd.VERS.name(), DecModelloXsdUd.TiModelloXsdUd.PROFILO_NORMATIVO_UNITA_DOC,
                idUnitaDoc);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VrsXmlModelloSessioneVers> lstXmlModelloSessioneVers = (List<VrsXmlModelloSessioneVers>) rispostaControlli
                    .getrObject();
            if (!lstXmlModelloSessioneVers.isEmpty()) {
                tmpProfiloNormativo = new ProfiloNormativoType();
                tmpProfiloNormativo.setVersione(
                        lstXmlModelloSessioneVers.get(0).getDecUsoModelloXsdUniDoc().getDecModelloXsdUd().getCdXsd());

                try {
                    Element el = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                            .parse(new ByteArrayInputStream(lstXmlModelloSessioneVers.get(0).getBlXml().getBytes()))
                            .getDocumentElement();
                    tmpProfiloNormativo.setAny(el);

                } catch (IllegalArgumentException | ParserConfigurationException | SAXException | IOException ex) {
                    log.error("Errore nel parsing dell'XML", ex);
                }
            }
        }

        return tmpProfiloNormativo;
    }
    // end MEV#26419

    private void popolaFileGroupList(PIndex pIndex, ComposizioneType composizione,
            List<SessioneVersamentoExt> sessioniVersamentoList, AroUnitaDoc tmpAroUnitaDoc,
            AroVersIniUnitaDoc tmpAroVersIniUnitaDoc, AroUpdUnitaDoc tmpAroUpdUnitaDoc, CSChiave csChiave,
            CSVersatore csVersatore) {

        long numAllegati = 0;
        long numAnnessi = 0;
        long numAnnotazioni = 0;

        Map<String, FileGroup> mappaGruppoFileSipUd = new LinkedHashMap<>();
        Map<String, FileGroup> mappaGruppoFileSipDocAgg = new LinkedHashMap<>();
        Map<String, FileGroup> mappaGruppoFileSipUpd = new LinkedHashMap<>();

        for (SessioneVersamentoExt sessioneVersamento : sessioniVersamentoList) {

            for (AroDoc aroDoc : sessioneVersamento.getDocumentiVersati()) {
                FileGroup gruppoFile = new FileGroup();
                // ID
                ID idGruppoFile = new ID();
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
                        MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
                                Costanti.UrnFormatter.VERS_FMT_STRING),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave, true, Costanti.UrnFormatter.UD_FMT_STRING),
                        urnPartDocumento);
                idGruppoFile.setValue("urn:" + tmpString);
                idGruppoFile.setScheme(schemeAttribute);
                gruppoFile.setID(idGruppoFile);
                // Label
                gruppoFile.setLabel(Costanti.FileGroupDocumento.getEnum(aroDoc.getTiDoc()).getLabel());
                // Description
                gruppoFile.setDescription(Costanti.FileGroupDocumento.getEnum(aroDoc.getTiDoc()).getDesc());
                // File
                rispostaControlli.reset();
                rispostaControlli = controlliRecIndiceAip.leggiComponentiDocumento(aroDoc);
                if (rispostaControlli.isrBoolean()) {
                    List<AroCompDoc> lstDatiF = (List<AroCompDoc>) rispostaControlli.getrObject();
                    // PER OGNI COMPONENTE DEL DOCUMENTO
                    for (AroCompDoc aroCompDoc : lstDatiF) {
                        // Elaboralo come Componente
                        File fileComp = popolaComponenteFile(aroCompDoc, tmpAroVersIniUnitaDoc, tmpAroUpdUnitaDoc, 0);
                        fileComp.setEncoding("binary");
                        gruppoFile.getFile().add(fileComp);
                        rispostaControlli.reset();
                        rispostaControlli = controlliRecIndiceAip.leggiSottoComponenti(aroCompDoc);
                        if (rispostaControlli.isrBoolean()) {
                            // Elaboralo come Sottocomponente
                            List<AroCompDoc> listaSottocomponenti = (List<AroCompDoc>) rispostaControlli.getrObject();
                            int pos = 1;
                            for (AroCompDoc sotCompDoc : listaSottocomponenti) {
                                File fileSotComp = popolaComponenteFile(sotCompDoc, tmpAroVersIniUnitaDoc,
                                        tmpAroUpdUnitaDoc, pos++);
                                fileSotComp.setEncoding("binary");
                                gruppoFile.getFile().add(fileSotComp);
                            }
                        }
                    }
                }
                // More Info
                MoreInfo moreInfoFileGruppo = new MoreInfo();
                moreInfoFileGruppo.setXmlSchema("/xmlschema/Unisincro_MoreInfoDoc_v1.1.xsd");
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
                EmbeddedMetadata emgf = new EmbeddedMetadata();
                it.eng.parer.ws.xml.usdocResp.ObjectFactory objFct3 = new it.eng.parer.ws.xml.usdocResp.ObjectFactory();
                emgf.setAny(objFct3.createMetadatiIntegratiDoc(mieid));
                moreInfoFileGruppo.setEmbeddedMetadata(emgf);
                gruppoFile.setMoreInfo(moreInfoFileGruppo);
                pIndex.getFileGroup().add(gruppoFile);
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
                    File tmpFileItem = new File();
                    // ID
                    ID tmpIdFileItem = new ID();
                    tmpIdFileItem.setValue(datiXml.getUrn());
                    tmpIdFileItem.setScheme(schemeAttribute);
                    tmpFileItem.setID(tmpIdFileItem);
                    // Format
                    tmpFileItem.setFormat(IOUtils.CONTENT_TYPE.XML.getContentType());
                    // Encoding
                    tmpFileItem.setEncoding("binary");
                    // Extension
                    tmpFileItem.setExtension(IOUtils.CONTENT_TYPE.XML.getFileExt());
                    // Path
                    /* Definisco la folder relativa al sistema di conservazione */
                    String tmpPath = (sessioneVersamento.getTipoSessione().equals(Constants.TipoSessione.VERSAMENTO))
                            ? datiXml.getUrn().replaceAll(IOUtils.extractPartUrnName(datiXml.getUrn()), "SIP-UD")
                            : datiXml.getUrn().replaceAll(IOUtils.extractPartUrnName(datiXml.getUrn()),
                                    "SIP-AGGIUNTA_DOC");
                    String path = (sessioneVersamento.getTipoSessione().equals(Constants.TipoSessione.VERSAMENTO))
                            ? IOUtils.extractPartUrnName(tmpPath, true) : MessaggiWSFormat
                                    .normalizingKey(tmpPath.substring(tmpPath.lastIndexOf(":DOC")).substring(1));
                    String folder = IOUtils.getPath("/sip", path, IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(datiXml.getUrn(), true),
                            IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                    String pathSip = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                    tmpFileItem.setPath(pathSip);
                    // Hash
                    Hash tmpHashFileItem = new Hash();
                    tmpHashFileItem.setValue(datiXml.getHash());
                    // MAC#25654
                    String function = datiXml.getAlgoritmo() != null ? datiXml.getAlgoritmo() : NON_DEFINITO;
                    tmpHashFileItem.setHashFunction(function);
                    // end MAC#25654
                    tmpFileItem.setHash(tmpHashFileItem);
                    //
                    if (sessioneVersamento.getTipoSessione().equals(Constants.TipoSessione.VERSAMENTO)) {
                        /* FILEGROUP DEI DOCUMENTI DEL PACCHETTO DI VERSAMENTO (SIP) DI UNITÀ DOCUMENTARIA */
                        FileGroup fileGroupSipUd = mappaGruppoFileSipUd.computeIfAbsent(
                                datiXml.getUrn().replaceAll(IOUtils.extractPartUrnName(datiXml.getUrn()), ""),
                                k -> getFileGroupSipUd(datiXml.getUrn()));
                        if (fileGroupSipUd != null) {
                            fileGroupSipUd.getFile().add(tmpFileItem);
                        }
                    } else if (sessioneVersamento.getTipoSessione().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO)) {
                        /* FILEGROUP DEI DOCUMENTI DEL PACCHETTO DI VERSAMENTO (SIP) DI AGGIUNTA DOCUMENTO */
                        FileGroup fileGroupSipDocAgg = mappaGruppoFileSipDocAgg.computeIfAbsent(
                                datiXml.getUrn().replaceAll(IOUtils.extractPartUrnName(datiXml.getUrn()), ""),
                                k -> getFileGroupSipDocAgg(datiXml.getUrn()));
                        if (fileGroupSipDocAgg != null) {
                            fileGroupSipDocAgg.getFile().add(tmpFileItem);
                        }
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

        if (mappaGruppoFileSipUd.size() > 0) {
            pIndex.getFileGroup().addAll(mappaGruppoFileSipUd.values());
        }
        if (mappaGruppoFileSipDocAgg.size() > 0) {
            pIndex.getFileGroup().addAll(mappaGruppoFileSipDocAgg.values());
        }

        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiXmlVersamentiAipUpdDaUnitaDoc(tmpAroUnitaDoc.getIdUnitaDoc());
        List<AroXmlUpdUnitaDoc> xmlupds = null;
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            xmlupds = (List<AroXmlUpdUnitaDoc>) rispostaControlli.getrObject();
            for (AroXmlUpdUnitaDoc xmlupd : xmlupds) {
                if (xmlupd.getDsUrnXml() != null) {
                    File tmpFileItem = new File();
                    // ID
                    ID tmpIdFileItem = new ID();
                    tmpIdFileItem.setValue(xmlupd.getDsUrnXml());
                    tmpIdFileItem.setScheme(schemeAttribute);
                    tmpFileItem.setID(tmpIdFileItem);
                    // Format
                    tmpFileItem.setFormat(IOUtils.CONTENT_TYPE.XML.getContentType());
                    // Encoding
                    tmpFileItem.setEncoding("binary");
                    // Extension
                    tmpFileItem.setExtension(IOUtils.CONTENT_TYPE.XML.getFileExt());
                    // Path
                    // MAC#25915
                    boolean replaceWithAggMd = MessaggiWSFormat.isUrnMatchesSafely(xmlupd.getDsUrnXml(),
                            Costanti.UrnFormatter.SPATH_AGG_MD_REGEXP);
                    String strReplacement = (replaceWithAggMd) ? "AGG_MD" : "AGGIORNAMENTO_UPD";
                    String matchReplacement = (replaceWithAggMd) ? ":AGG_MD" : ":UPD";
                    int beginIndexReplacement = (replaceWithAggMd) ? 6 : 3;
                    // end MAC#25915
                    /* Definisco la folder relativa al sistema di conservazione */
                    // urn:<...>:UPD00001:SIP-AGGIORNAMENTO_UPD || urn:<...>:AGG_MD00001:SIP-AGG_MD
                    String tmpPath = xmlupd.getDsUrnXml().replaceAll(IOUtils.extractPartUrnName(xmlupd.getDsUrnXml()),
                            "SIP-" + strReplacement);
                    String[] tmpPathSplit = tmpPath.substring(tmpPath.lastIndexOf(matchReplacement)).substring(1)
                            .split(":");
                    // SIP-AGGIORNAMENTO_UPD00001 || SIP-AGG_MD00001
                    String path = tmpPathSplit[1] + tmpPathSplit[0].substring(beginIndexReplacement);
                    String folder = IOUtils.getPath("/sip", path, IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(xmlupd.getDsUrnXml(), true),
                            IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                    String pathSip = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                    tmpFileItem.setPath(pathSip);
                    // Hash
                    Hash tmpHashFileItem = new Hash();
                    tmpHashFileItem.setValue(xmlupd.getDsHashXml());
                    // MAC#25654
                    String function = xmlupd.getDsAlgoHashXml() != null ? xmlupd.getDsAlgoHashXml() : NON_DEFINITO;
                    tmpHashFileItem.setHashFunction(function);
                    // end MAC#25654
                    tmpFileItem.setHash(tmpHashFileItem);
                    //
                    /* FILEGROUP DEI DOCUMENTI DEL PACCHETTO DI VERSAMENTO (SIP) DI AGGIORNAMENTO METADATI */
                    FileGroup fileGroupSipUpd = mappaGruppoFileSipUpd.computeIfAbsent(
                            xmlupd.getDsUrnXml().replaceAll(IOUtils.extractPartUrnName(xmlupd.getDsUrnXml()), ""),
                            k -> getFileGroupSipUpd(xmlupd.getDsUrnXml()));
                    if (fileGroupSipUpd != null) {
                        fileGroupSipUpd.getFile().add(tmpFileItem);
                    }
                }
            }
            //
            if (mappaGruppoFileSipUpd.size() > 0) {
                pIndex.getFileGroup().addAll(mappaGruppoFileSipUpd.values());
            }
        }
    }

    private FileGroup getFileGroupSipUd(String urn) {
        FileGroup fileGroupSipUd = new FileGroup();
        // ID
        ID idGruppoFileSipUd = new ID();
        idGruppoFileSipUd.setValue(urn.replaceAll(IOUtils.extractPartUrnName(urn), "SIP-UD"));
        idGruppoFileSipUd.setScheme(schemeAttribute);
        fileGroupSipUd.setID(idGruppoFileSipUd);
        fileGroupSipUd.setLabel("Pacchetto di versamento (SIP) di Unità Documentaria");
        fileGroupSipUd.setDescription(
                "File dei pacchetti di versamento che hanno originato il presente pacchetto di archiviazione: Indice del Pacchetto di versamento (IndiceSIP), Rapporto di versamento (RdV) ed Esito versamento (EdV)");

        return fileGroupSipUd;
    }

    private FileGroup getFileGroupSipDocAgg(String urn) {
        FileGroup fileGroupSipDocAgg = new FileGroup();
        // ID
        ID idGruppoFileSipDocAgg = new ID();
        idGruppoFileSipDocAgg.setValue(urn.replaceAll(IOUtils.extractPartUrnName(urn), "SIP-AGGIUNTA_DOC"));
        idGruppoFileSipDocAgg.setScheme(schemeAttribute);
        fileGroupSipDocAgg.setID(idGruppoFileSipDocAgg);
        fileGroupSipDocAgg.setLabel("Pacchetto di versamento (SIP) di Aggiunta Documento a Unità documentaria");
        fileGroupSipDocAgg.setDescription(
                "File dei pacchetti di versamento che hanno originato il presente pacchetto di archiviazione: Indice del Pacchetto di versamento (PdV), Rapporto di versamento (RdV) ed Esito versamento (EdV)");

        return fileGroupSipDocAgg;
    }

    private FileGroup getFileGroupSipUpd(String urn) {
        FileGroup fileGroupSipUpd = new FileGroup();
        // ID
        ID idGruppoFileSipUpd = new ID();
        // MAC#25915
        boolean replaceWithAggMd = MessaggiWSFormat.isUrnMatchesSafely(urn, Costanti.UrnFormatter.SPATH_AGG_MD_REGEXP);
        String strReplacement = (replaceWithAggMd) ? "AGG_MD" : "AGGIORNAMENTO_UPD";
        // end MAC#25915
        idGruppoFileSipUpd.setValue(urn.replaceAll(IOUtils.extractPartUrnName(urn), "SIP-" + strReplacement));
        idGruppoFileSipUpd.setScheme(schemeAttribute);
        fileGroupSipUpd.setID(idGruppoFileSipUpd);
        fileGroupSipUpd.setLabel("Pacchetto di versamento (SIP) di Aggiornamento metadati Unità documentaria ");
        fileGroupSipUpd.setDescription(
                "File dei pacchetti di versamento che hanno originato il presente pacchetto di archiviazione: Indice del Pacchetto di versamento (IndiceSIP) e Rapporto di versamento (RdV)");

        return fileGroupSipUpd;
    }

    /**
     * Decoro l'elemento More Info del DOCUMENTO di FileGroup
     */
    private void popolaMetadatiIntegratiExtraInfoDoc(MetadatiIntegratiDocType mieid, AroDoc arodoc,
            AroUnitaDoc tmpAroUnitaDoc) {
        // Chiave Documento
        mieid.setChiaveDocumento(arodoc.getCdKeyDocVers());
        // Tipo Documento
        mieid.setTipoDocumento(unitaDocumentarieHelper.getNmTipoDoc(arodoc.getDecTipoDoc().getIdTipoDoc()));
        // Elemento
        mieid.setElemento(arodoc.getTiDoc());
        // Data Acquisizione
        mieid.setDataAcquisizione(XmlDateUtility.dateToXMLGregorianCalendar(arodoc.getDtCreazione()));
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(arodoc.getDlDoc());
        profiloDocumento.setAutore(arodoc.getDsAutoreDoc());
        mieid.setProfiloDocumento(profiloDocumento);
        // Note documento
        mieid.setNoteDocumento(arodoc.getNtDoc());
        // Dati Specifici del Documento
        DatiSpecificiTypePVolume dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.VERS, TipiEntitaSacer.DOC,
                arodoc.getIdDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Sistama di migrazione
        mieid.setSistemaDiMigrazione(tmpAroUnitaDoc.getNmSistemaMigraz());
        // Dati Specifici migrazione del Documento
        dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.MIGRAZ, TipiEntitaSacer.DOC, arodoc.getIdDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }

    }

    /**
     * Decoro i dati aggiornabili dell'elemento More Info del DOCUMENTO di FileGroup da ARO_VERS_INI_DOC
     */
    private void aggiornaMetadatiIntegratiExtraInfoDocVersIniUpd(MetadatiIntegratiDocType mieid,
            AroVersIniDoc aroVersIniDoc) {
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(aroVersIniDoc.getDlDoc());
        profiloDocumento.setAutore(aroVersIniDoc.getDsAutoreDoc());
        mieid.setProfiloDocumento(profiloDocumento);
        // Dati Specifici del Documento
        DatiSpecificiTypePVolume dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.VERS,
                TiEntitaSacerAroVersIniDatiSpec.DOC, aroVersIniDoc.getIdVersIniDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Dati Specifici migrazione del Documento
        dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.MIGRAZ,
                TiEntitaSacerAroVersIniDatiSpec.DOC, aroVersIniDoc.getIdVersIniDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }
    }

    /**
     * Decoro i dati aggiornabili dell'elemento More Info del DOCUMENTO di FileGroup da ARO_UPD_DOC_UNITA_DOC
     */
    private void aggiornaMetadatiIntegratiExtraInfoDocUpd(MetadatiIntegratiDocType mieid,
            AroUpdDocUnitaDoc aroUpdDocUnitaDoc) {
        // Profilo Documento
        ProfiloDocumentoType profiloDocumento = new ProfiloDocumentoType();
        profiloDocumento.setDescrizione(aroUpdDocUnitaDoc.getDlDoc());
        profiloDocumento.setAutore(aroUpdDocUnitaDoc.getDsAutoreDoc());
        mieid.setProfiloDocumento(profiloDocumento);
        // Dati Specifici del Documento
        DatiSpecificiTypePVolume dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.VERS,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_DOC, aroUpdDocUnitaDoc.getIdUpdDocUnitaDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType o = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecifici(o);
        }
        // Dati Specifici migrazione del Documento
        dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_DOC, aroUpdDocUnitaDoc.getIdUpdDocUnitaDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usdocResp.DatiSpecificiType m = new it.eng.parer.ws.xml.usdocResp.DatiSpecificiType();
            m.getAny().addAll(dati.getAny());
            m.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            mieid.setDatiSpecificiMigrazione(m);
        }
    }

    /**
     * Popola i dati di MoreInfo del COMPONENTE di FileGroup
     */
    private void popolaMetadatiIntegratiExtraInfoFile(MetadatiIntegratiFileType meta, AroCompDoc aroCompDoc, int pos,
            AroVVisCompAip aroVVisCompAip) {
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
        it.eng.parer.ws.xml.uspvolumeRespV2.DatiSpecificiTypePVolume dati = this
                .caricaDatiSpecUniSincro(TipiUsoDatiSpec.VERS, TipiEntitaSacer.COMP, aroCompDoc.getIdCompDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincro(TipiUsoDatiSpec.MIGRAZ, TipiEntitaSacer.COMP, aroCompDoc.getIdCompDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }

    }

    /**
     * Popola i dati aggiornabili di MoreInfo del COMPONENTE di FileGroup da ARO_VERS_INI_COMP
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
        it.eng.parer.ws.xml.uspvolumeRespV2.DatiSpecificiTypePVolume dati = this.caricaDatiSpecUniSincroVersIniUpd(
                TiUsoXsdAroVersIniDatiSpec.VERS, TiEntitaSacerAroVersIniDatiSpec.COMP,
                aroVersIniComp.getIdVersIniComp());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec.MIGRAZ,
                TiEntitaSacerAroVersIniDatiSpec.COMP, aroVersIniComp.getIdVersIniComp());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }
    }

    /**
     * Popola i dati aggiornabili di MoreInfo del COMPONENTE di FileGroup da ARO_UPD_COMP_UNITA_DOC
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
        it.eng.parer.ws.xml.uspvolumeRespV2.DatiSpecificiTypePVolume dati = this.caricaDatiSpecUniSincroUpd(
                TiUsoXsdAroUpdDatiSpecUnitaDoc.VERS, TiEntitaAroUpdDatiSpecUnitaDoc.UPD_COMP,
                aroUpdCompUnitaDoc.getIdUpdCompUnitaDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile o = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
            o.getAny().addAll(dati.getAny());
            o.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecifici(o);
        }
        // Dati specifici di migrazione del Componente
        it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile oMigraz = new it.eng.parer.ws.xml.usfileResp.DatiSpecificiTypeFile();
        dati = this.caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ,
                TiEntitaAroUpdDatiSpecUnitaDoc.UPD_COMP, aroUpdCompUnitaDoc.getIdUpdCompUnitaDoc());
        if (dati != null && dati.getAny() != null && !dati.getAny().isEmpty()) {
            oMigraz.getAny().addAll(dati.getAny());
            oMigraz.setVersioneDatiSpecifici(dati.getVersioneDatiSpecifici());
            meta.setDatiSpecificiMigrazione(oMigraz);
        }
    }

    private DatiSpecificiTypePVolume caricaDatiSpecUniSincro(TipiUsoDatiSpec tipoUsoAttr,
            TipiEntitaSacer tipoEntitySacer, long idEntitySacer) {
        DatiSpecificiTypePVolume tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntity(tipoUsoAttr, tipoEntitySacer, idEntitySacer);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<Object[]> tmpDati = (List<Object[]>) rispostaControlli.getrObject();
            if (!tmpDati.isEmpty()) {
                tmpDatiSpecifici = new DatiSpecificiTypePVolume();
                tmpDatiSpecifici.setVersioneDatiSpecifici((tmpDati.get(0))[0].toString());

                DocumentBuilder db = null;
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
                    db = dbf.newDocumentBuilder();

                    Document doc = db.newDocument();
                    for (Object[] tmpArr : tmpDati) {
                        Element el = doc.createElement(tmpArr[1].toString());
                        el.insertBefore(doc.createTextNode(tmpArr[2] != null ? tmpArr[2].toString() : ""),
                                el.getLastChild());
                        tmpDatiSpecifici.getAny().add(el);
                    }
                } catch (IllegalArgumentException | ParserConfigurationException ex) {
                    log.error("Errore caricaDatiSpecUniSincro ", ex);
                }
            }
        }

        return tmpDatiSpecifici;
    }

    private DatiSpecificiTypePVolume caricaDatiSpecUniSincroVersIniUpd(TiUsoXsdAroVersIniDatiSpec tipoUsoAttr,
            TiEntitaSacerAroVersIniDatiSpec tipoEntitySacer, long idEntitySacer) {
        DatiSpecificiTypePVolume tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntityVersIniUpd(tipoUsoAttr, tipoEntitySacer,
                idEntitySacer);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroVersIniDatiSpec> tmpAroVersIniDatiSpec = (List<AroVersIniDatiSpec>) rispostaControlli.getrObject();
            if (!tmpAroVersIniDatiSpec.isEmpty()) {
                // TODO: DA CENTRALIZZARE LETTURA CLOB
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    String blXmlDatiSpec = tmpAroVersIniDatiSpec.get(0).getBlXmlDatiSpec();
                    byte[] xml = blXmlDatiSpec.getBytes(StandardCharsets.UTF_8);
                    InputSource is = new InputSource(new StringReader(new String(xml, StandardCharsets.UTF_8)));
                    Document docxml = db.parse(is);
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String partQueryXml = (TiUsoXsdAroVersIniDatiSpec.MIGRAZ.equals(tipoUsoAttr))
                            ? "DatiSpecificiMigrazione" : "DatiSpecifici";
                    String queryXml = "//" + partQueryXml + "/VersioneDatiSpecifici";
                    XPathExpression expr = xPath.compile(queryXml);
                    String versioneDatiSpecifici = expr.evaluate(docxml);
                    queryXml = "//" + partQueryXml + "/*[position()>1]";
                    expr = xPath.compile(queryXml);
                    NodeList nodeList = (NodeList) expr.evaluate(docxml, XPathConstants.NODESET);

                    tmpDatiSpecifici = new DatiSpecificiTypePVolume();
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

    private DatiSpecificiTypePVolume caricaDatiSpecUniSincroUpd(TiUsoXsdAroUpdDatiSpecUnitaDoc tipoUsoAttr,
            TiEntitaAroUpdDatiSpecUnitaDoc tipoEntitySacer, long idEntitySacerUpd) {
        DatiSpecificiTypePVolume tmpDatiSpecifici = null;
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAip.leggiDatiSpecEntityUpd(tipoUsoAttr, tipoEntitySacer,
                idEntitySacerUpd);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<AroUpdDatiSpecUnitaDoc> tmpAroUpdDatiSpecUnitaDoc = (List<AroUpdDatiSpecUnitaDoc>) rispostaControlli
                    .getrObject();
            if (!tmpAroUpdDatiSpecUnitaDoc.isEmpty()) {
                // TODO: DA CENTRALIZZARE LETTURA CLOB
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
                    // dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    String blXmlDatiSpec = tmpAroUpdDatiSpecUnitaDoc.get(0).getBlXmlDatiSpec();
                    byte[] xml = blXmlDatiSpec.getBytes(StandardCharsets.UTF_8);
                    InputSource is = new InputSource(new StringReader(new String(xml, StandardCharsets.UTF_8)));
                    Document docxml = db.parse(is);
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String partQueryXml = (TiUsoXsdAroUpdDatiSpecUnitaDoc.MIGRAZ.equals(tipoUsoAttr))
                            ? "DatiSpecificiMigrazione" : "DatiSpecifici";
                    String queryXml = "//" + partQueryXml + "/VersioneDatiSpecifici";
                    XPathExpression expr = xPath.compile(queryXml);
                    String versioneDatiSpecifici = expr.evaluate(docxml);
                    queryXml = "//" + partQueryXml + "/*[position()>1]";
                    expr = xPath.compile(queryXml);
                    NodeList nodeList = (NodeList) expr.evaluate(docxml, XPathConstants.NODESET);

                    tmpDatiSpecifici = new DatiSpecificiTypePVolume();
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
     */
    private File popolaComponenteFile(AroCompDoc aroCompDoc, AroVersIniUnitaDoc aroVersIniUnitaDoc,
            AroUpdUnitaDoc aroUpdUnitaDoc, int pos) {
        File fileComp = new File();
        // ID
        ID idFile = new ID();
        // urn normalizzato
        AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(aroCompDoc, TiUrn.NORMALIZZATO);
        String urnCompNorm = compUrnCalc.getDsUrn();
        idFile.setValue(urnCompNorm);
        idFile.setScheme(schemeAttribute);
        fileComp.setID(idFile);
        // Encoding
        fileComp.setEncoding("binary");
        // Extension
        String fileExt = null;
        String dsFormatoRapprCalc = aroCompDoc.getDsFormatoRapprCalc();
        if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
            fileExt = dsFormatoRapprCalc;
        } else {
            if (aroCompDoc.getDecFormatoFileDoc() != null) {
                fileExt = formatoFileDocHelper
                        .findNmFormatoFileDocById(aroCompDoc.getDecFormatoFileDoc().getIdFormatoFileDoc());
            } else {
                fileExt = UKNOWN_EXT;
            }
        }
        fileComp.setExtension(fileExt);
        // Path
        /* Definisco la folder relativa al sistema di conservazione */
        String folder = IOUtils.getPath("/", "file", IOUtils.UNIX_FILE_SEPARATOR);
        /* Definisco il nome e l'estensione del file */
        String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(urnCompNorm, true), fileExt);
        /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
        String pathFile = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
        fileComp.setPath(pathFile);
        // Hash
        Hash hash = new Hash();
        hash.setValue(aroCompDoc.getDsHashFileCalc());
        // MAC#25654
        String function = aroCompDoc.getDsAlgoHashFileCalc() != null ? aroCompDoc.getDsAlgoHashFileCalc()
                : NON_DEFINITO;
        hash.setHashFunction(function);
        // end MAC#25654
        hash.setCanonicalXML(Boolean.FALSE);
        fileComp.setHash(hash);
        // Format
        rispostaControlli = controlliRecIndiceAip.leggiComponenteDaVista(aroCompDoc.getIdCompDoc());
        AroVVisCompAip aroVVisCompAip = (AroVVisCompAip) rispostaControlli.getrObject();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            String mymetype = aroVVisCompAip.getNmMimetypeFile() != null ? aroVVisCompAip.getNmMimetypeFile()
                    : NON_DEFINITO;
            fileComp.setFormat(mymetype);
        }

        // More Info
        MoreInfo moreInfoFile = new MoreInfo();
        moreInfoFile.setXmlSchema("/xmlschema/Unisincro_MoreInfoFile_v1.1.xsd");
        EmbeddedMetadata emFile = new EmbeddedMetadata();
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
        it.eng.parer.ws.xml.usfileResp.ObjectFactory objFct4 = new it.eng.parer.ws.xml.usfileResp.ObjectFactory();
        emFile.setAny(objFct4.createMetadatiIntegratiFile(mdif));
        moreInfoFile.setEmbeddedMetadata(emFile);
        fileComp.setMoreInfo(moreInfoFile);
        return fileComp;
    }

    /**
     * Calcola l'hash SHA-256 dello zip contenente i file delle UD
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param cdRegistroKeyUnitaDoc
     *            codice registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * @param cdKeyUnitaDoc
     *            numero unita doc
     * 
     * @return hash calcolato
     * 
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NamingException
     *             errore generico
     * @throws java.security.NoSuchAlgorithmException
     *             errore generico
     */
    public String calcolaHashZipFileUd(long idUnitaDoc, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) throws IOException, NamingException, NoSuchAlgorithmException {

        RecuperoExt recupero = new RecuperoExt();
        recupero.setParametriRecupero(new ParametriRecupero());
        recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.UNI_DOC);
        recupero.getParametriRecupero().setIdUnitaDoc(idUnitaDoc);
        recupero.setTipoSalvataggioFile(CostantiDB.TipoSalvataggioFile.BLOB);
        recupero.setTpiAbilitato(false);

        Recupero recXml = new Recupero();
        recXml.setChiave(new it.eng.parer.ws.xml.versReqStato.ChiaveType());
        recXml.setVersione(Costanti.VERSIONE_XML_RECUP_UD);

        recXml.getChiave().setAnno(aaKeyUnitaDoc.toBigInteger());
        recXml.getChiave().setNumero(cdKeyUnitaDoc);
        recXml.getChiave().setTipoRegistro(cdRegistroKeyUnitaDoc);

        recupero.setStrutturaRecupero(recXml);

        java.io.File zippo = zipGen.getZip(System.getProperty("java.io.tmpdir"), recupero, true,
                new RispostaWSRecupero());
        try (FileInputStream is = (new FileInputStream(zippo))) {
            return new HashCalculator().calculateSHAX(is, TipiHash.SHA_256).toHexBinary();
        }
    }

    /**
     * Calcola l'hash SHA-256 dello zip contenente CRL, certificati e file di conservazione di un volume
     *
     * @param idVolume
     *            id volume
     * 
     * @return hash calcolato
     * 
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NamingException
     *             errore generico
     * @throws java.security.NoSuchAlgorithmException
     *             errore generico
     */
    public String calcolaHashZipProveConservazione(long idVolume)
            throws IOException, NamingException, NoSuchAlgorithmException {

        RecuperoExt recupero = new RecuperoExt();
        recupero.setParametriRecupero(new ParametriRecupero());
        recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.PROVE_CONSERV_AIPV2);
        recupero.getParametriRecupero().setIdVolume(idVolume);

        java.io.File zippo = zipGen.getZipProveCons(System.getProperty("java.io.tmpdir"), recupero,
                new RispostaWSRecupero());
        try (FileInputStream is = (new FileInputStream(zippo))) {
            return new HashCalculator().calculateSHAX(is, TipiHash.SHA_256).toHexBinary();
        }
    }
}
