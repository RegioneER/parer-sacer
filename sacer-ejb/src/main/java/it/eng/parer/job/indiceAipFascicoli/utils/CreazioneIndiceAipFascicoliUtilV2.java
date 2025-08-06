/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.job.indiceAipFascicoli.utils;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import it.eng.parer.aipFascicoli.xml.usmainRespV2.Agent;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.AgentID;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.AgentName;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.CreatingApplication;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.EmbeddedMetadata;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.ExternalMetadata;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.File;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.FileGroup;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.Hash;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.ID;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.MoreInfo;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.NameAndSurname;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PIndex;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PIndexID;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PVolume;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PVolumeGroup;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.PVolumeSource;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.Process;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.RelevantDocument;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.SelfDescription;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.TimeInfo;
import it.eng.parer.aipFascicoli.xml.usmainRespV2.TimeReference;
import it.eng.parer.aipFascicoli.xml.usselfdescRespV2.IndiceAIPType;
import it.eng.parer.aipFascicoli.xml.usselfdescRespV2.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper.TipoFascicoloHelper;
import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.FasContenVerAipFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasSipVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.job.indiceAipFascicoli.helper.ControlliRecIndiceAipFascicoli;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recuperoFasc.ejb.ControlliRecuperoFasc;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versFascicoli.ejb.ControlliFascicoli;
import it.eng.parer.xml.utils.XmlUtils;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
	"unchecked" })
public class CreazioneIndiceAipFascicoliUtilV2 {

    private static final Logger log = LoggerFactory
	    .getLogger(CreazioneIndiceAipFascicoliUtilV2.class);

    public static final String NON_DEFINITO = "Non Definito";
    private final String cdVersioneXml = "2.0";
    private final String hashFunction = TipiHash.SHA_256.descrivi();
    private final String schemeAttribute = Costanti.SchemeAttributes.SCHEME_LOCAL;

    private RispostaControlli rispostaControlli;
    private final ControlliRecIndiceAipFascicoli controlliRecIndiceAipFascicoli;
    private final XmlContextCache xmlContextCache;
    private final AmbientiHelper ambientiHelper;
    private final TipoFascicoloHelper tipoFascicoloHelper;
    private final ControlliRecuperoFasc controlliRecuperoFasc;

    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliFascicoli controlliFascicoli = null;

    public CreazioneIndiceAipFascicoliUtilV2() throws NamingException {
	rispostaControlli = new RispostaControlli();
	// Recupera l'ejb per la lettura di informazioni, se possibile
	controlliFascicoli = (ControlliFascicoli) new InitialContext()
		.lookup("java:module/ControlliFascicoli");
	controlliRecIndiceAipFascicoli = (ControlliRecIndiceAipFascicoli) new InitialContext()
		.lookup("java:module/ControlliRecIndiceAipFascicoli");
	xmlContextCache = (XmlContextCache) new InitialContext()
		.lookup("java:module/XmlContextCache");
	ambientiHelper = (AmbientiHelper) new InitialContext().lookup("java:module/AmbientiHelper");
	tipoFascicoloHelper = (TipoFascicoloHelper) new InitialContext()
		.lookup("java:module/TipoFascicoloHelper");
	controlliRecuperoFasc = (ControlliRecuperoFasc) new InitialContext()
		.lookup("java:module/ControlliRecuperoFasc");
    }

    private void setRispostaError() {
	log.error(
		"Creazione Indice AIP Fascicoli v2.0 - Errore nella creazione dell'istanza di conservazione UniSyncro (PIndex): {}",
		rispostaControlli.getDsErr());
	throw new RuntimeException(
		rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr());
    }

    /**
     * Riceve il fascicolo da elaborare e crea il PIndex
     *
     * @param verAipFascicolo             entity FasVerAipFascicolo
     * @param codiceVersione              codice versione
     * @param codiceVersioneMetadati      codice versione metadati
     * @param mappaAgenti                 mappa chiave/valore
     * @param sistemaConservazione        sistema conservazione
     * @param creatingApplicationProducer producer
     *
     * @return entity PIndex
     *
     * @throws ParerInternalError             errore generico
     * @throws DatatypeConfigurationException errore generico
     * @throws JAXBException                  errore generico
     */
    public PIndex generaIndiceAIPV2Strict(FasVerAipFascicolo verAipFascicolo, String codiceVersione,
	    String codiceVersioneMetadati, Map<String, String> mappaAgenti,
	    String sistemaConservazione, String creatingApplicationProducer)
	    throws ParerInternalError, DatatypeConfigurationException, JAXBException {
	PIndex istanzaUnisincro = new PIndex();
	popolaPIndex(istanzaUnisincro, verAipFascicolo.getIdVerAipFascicolo(),
		verAipFascicolo.getFasFascicolo(), codiceVersione, codiceVersioneMetadati,
		mappaAgenti, sistemaConservazione, creatingApplicationProducer, Boolean.TRUE);
	return istanzaUnisincro;
    }

    /**
     * Riceve il fascicolo da elaborare e crea il PIndex (not strict)
     *
     * @param verAipFascicolo             entity FasVerAipFascicolo
     * @param codiceVersione              codice versione
     * @param codiceVersioneMetadati      codice versione metadati
     * @param mappaAgenti                 mappa chiave/valore
     * @param sistemaConservazione        sistema conservazione
     * @param creatingApplicationProducer producer
     *
     * @return entity PIndex
     *
     * @throws ParerInternalError             errore generico
     * @throws DatatypeConfigurationException errore generico
     * @throws JAXBException                  errore generico
     */
    public PIndex generaIndiceAIPV2NotStrict(FasVerAipFascicolo verAipFascicolo,
	    String codiceVersione, String codiceVersioneMetadati, Map<String, String> mappaAgenti,
	    String sistemaConservazione, String creatingApplicationProducer)
	    throws ParerInternalError, DatatypeConfigurationException, JAXBException {
	PIndex istanzaUnisincro = new PIndex();
	popolaPIndex(istanzaUnisincro, verAipFascicolo.getIdVerAipFascicolo(),
		verAipFascicolo.getFasFascicolo(), codiceVersione, codiceVersioneMetadati,
		mappaAgenti, sistemaConservazione, creatingApplicationProducer, Boolean.FALSE);
	return istanzaUnisincro;
    }

    /**
     * Riceve il fascicolo e crea il PIndex
     *
     * @param idVerAipFascicolo           id versamento fascicolo aip
     * @param fasc                        entity FasFascicolo
     * @param codiceVersione              codice versione
     * @param codiceVersioneMetadati      codice versione metadati
     * @param mappaAgenti                 mappa chiave/valore
     * @param sistemaConservazione        sistema conservazione
     * @param creatingApplicationProducer producer
     *
     * @return entity PIndex
     *
     * @throws ParerInternalError             errore generico
     * @throws DatatypeConfigurationException errore generico
     * @throws JAXBException                  errore generico
     */
    public PIndex generaIndiceAIPV2Strict(long idVerAipFascicolo, FasFascicolo fasc,
	    String codiceVersione, String codiceVersioneMetadati, Map<String, String> mappaAgenti,
	    String sistemaConservazione, String creatingApplicationProducer)
	    throws ParerInternalError, DatatypeConfigurationException, JAXBException {
	PIndex istanzaUnisincro = new PIndex();
	popolaPIndex(istanzaUnisincro, idVerAipFascicolo, fasc, codiceVersione,
		codiceVersioneMetadati, mappaAgenti, sistemaConservazione,
		creatingApplicationProducer, Boolean.TRUE);
	return istanzaUnisincro;
    }

    private void popolaPIndex(PIndex pIndex, long idVerAipFascicolo, FasFascicolo fasFascicolo,
	    String codiceVersione, String codiceVersioneMetadati, Map<String, String> mappaAgenti,
	    String sistemaConservazione, String creatingApplicationProducer, Boolean strictMode)
	    throws ParerInternalError, DatatypeConfigurationException, JAXBException {
	FasFascicolo tmpFasFascicolo = null;

	XMLGregorianCalendar timeRef = DatatypeFactory.newInstance()
		.newXMLGregorianCalendar(new GregorianCalendar());

	CSChiaveFasc csChiaveFasc = new CSChiaveFasc();
	csChiaveFasc.setAnno(fasFascicolo.getAaFascicolo().intValue());
	csChiaveFasc.setNumero(fasFascicolo.getCdKeyFascicolo());

	CSVersatore csVersatore = new CSVersatore();
	csVersatore.setSistemaConservazione(sistemaConservazione);
	csVersatore.setAmbiente(
		fasFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	csVersatore.setEnte(fasFascicolo.getOrgStrut().getOrgEnte().getNmEnte());
	csVersatore.setStruttura(fasFascicolo.getOrgStrut().getNmStrut());

	long idFascicolo = fasFascicolo.getIdFascicolo();

	// Recupero il fascicolo
	rispostaControlli.reset();
	rispostaControlli = controlliFascicoli.leggiFascicolo(idFascicolo);
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    tmpFasFascicolo = (FasFascicolo) rispostaControlli.getrObject();
	}

	/*
	 * ************************ DECORO SELFDESCRIPTION ************************
	 */
	SelfDescription selfDesc = new SelfDescription();
	/* ID */
	ID id = new ID();
	// calcolo parte urn NORMALIZZATO
	String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnFascicolo(
		MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
			Costanti.UrnFormatter.VERS_FMT_STRING),
		MessaggiWSFormat.formattaUrnPartFasc(csChiaveFasc, true,
			Costanti.UrnFormatter.FASC_FMT_STRING));
	// salvo NORMALIZZATO
	// MAC#28051
	// la versione dell'indice per il fascicolo può essere solo 1.0 e non 2.0
	String urn = MessaggiWSFormat.formattaUrnIndiceAipFascicoli(tmpUrnNorm, codiceVersione,
		Costanti.UrnFormatter.URN_INDICE_AIP_FASC_FMT_STRING_V2);
	// end MAC#28051
	id.setValue(urn);
	id.setScheme(schemeAttribute);
	selfDesc.setID(id);

	/* Creating Application */
	CreatingApplication applicazione = new CreatingApplication();
	applicazione.setName(StringUtils.capitalize(Constants.SACER.toLowerCase()));
	applicazione.setProducer(creatingApplicationProducer);
	rispostaControlli.reset();
	rispostaControlli = controlliRecIndiceAipFascicoli.getVersioneSacer();
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    applicazione.setVersion(rispostaControlli.getrString());
	}
	selfDesc.setCreatingApplication(applicazione);

	/* More Info */
	MoreInfo moreInfoApplic = new MoreInfo();
	moreInfoApplic.setXmlSchema("/xmlschema/Unisincro_MoreInfoSelfDescriptionFA_v2.0.xsd");
	EmbeddedMetadata extraInfoDescGenerale = new EmbeddedMetadata();
	MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
	this.popolaMetadatiIntegratiSelfDesc(tmpFasFascicolo, miSelfD, codiceVersione, timeRef);

	it.eng.parer.aipFascicoli.xml.usselfdescRespV2.ObjectFactory objFct1 = new it.eng.parer.aipFascicoli.xml.usselfdescRespV2.ObjectFactory();
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
	String urnPartVersatoreNorm = MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true,
		Costanti.UrnFormatter.VERS_FMT_STRING);
	String urnPartChiaveFascNorm = MessaggiWSFormat.formattaUrnPartFasc(csChiaveFasc, true,
		Costanti.UrnFormatter.FASC_FMT_STRING);
	String urnAIPFasc = MessaggiWSFormat.formattaUrnAipFascicolo(MessaggiWSFormat
		.formattaBaseUrnFascicolo(urnPartVersatoreNorm, urnPartChiaveFascNorm));
	idPVolume.setValue(urnAIPFasc);
	idPVolume.setScheme(schemeAttribute);
	pVolume.setID(idPVolume);
	pVolume.setLabel("Pacchetto di archiviazione (AIP) di un fascicolo");
	String desc = "File relativi ad un fascicolo, ai relativi Pacchetti di versamento (PdV), ai relativi metadati e al suo contenuto. Ogni PdV è aggregato in un FileGroup relativo ai SIP, le unità documentarie contenute sono aggregate in un filegroup, i sottofascicoli contenuti sono aggregati in un filegroup";
	pVolume.setDescription(desc);
	/* PVolumeSource */
	List<PVolumeSource> sorgenteArray = new ArrayList<>();
	for (int i = 0; i < selfDesc.getPIndexSource().size(); i++) {
	    PVolumeSource sorgente = new PVolumeSource();
	    /* ID */
	    ID idPVolumeSource = new ID();
	    String tmpPartUrnName = IOUtils
		    .extractPartUrnName(selfDesc.getPIndexSource().get(i).getID().getValue());
	    if (tmpPartUrnName.toUpperCase().startsWith("INDICEAIP-FA")) {
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
	DecTipoFascicolo decTipoFascicolo = tipoFascicoloHelper
		.findDecTipoFascicolo(tmpFasFascicolo.getDecTipoFascicolo().getIdTipoFascicolo());
	if (decTipoFascicolo != null) {
	    ID idPVolumeGruppo = new ID();
	    idPVolumeGruppo.setValue(decTipoFascicolo.getNmTipoFascicolo());
	    idPVolumeGruppo.setScheme(schemeAttribute);
	    pVolumeGruppo.setID(idPVolumeGruppo);
	    // Label
	    String label = "Tipo Fascicolo";
	    pVolumeGruppo.setLabel(label);
	    // Description
	    pVolumeGruppo.setDescription(decTipoFascicolo.getDsTipoFascicolo());
	}
	pVolume.setPVolumeGroup(pVolumeGruppo);

	/* MoreInfo */
	MoreInfo moreInfoPVolume = new MoreInfo();
	// MEV#29589
	if (!strictMode && codiceVersioneMetadati.equals(codiceVersione)) {
	    moreInfoPVolume.setXmlSchema("/xmlschema/ProfiloCompletoFascicolo_1.0.xsd");
	} else {
	    moreInfoPVolume.setXmlSchema("/xmlschema/Unisincro_MoreInfoPVolumeFA_v2.0.xsd");
	}
	// end MEV#29589
	ExternalMetadata extvolume = new ExternalMetadata();
	extvolume.setFormat(IOUtils.CONTENT_TYPE.XML.getContentType());
	extvolume.setEncoding("binary");
	extvolume.setExtension(IOUtils.CONTENT_TYPE.XML.getFileExt());
	// ID
	ID idExt = new ID();
	// MEV#29589
	String tmpUrnName = MessaggiWSFormat.formattaUrnAipMetaFascicolo(
		MessaggiWSFormat.formattaChiaveFascicolo(csVersatore, csChiaveFasc),
		codiceVersioneMetadati);
	// end MEV#29589
	idExt.setValue(MessaggiWSFormat.estraiNomeFileCompleto(tmpUrnName));
	idExt.setScheme(schemeAttribute);
	extvolume.setID(idExt);
	// Path
	String filename = IOUtils.getFilename(IOUtils.extractPartUrnName(tmpUrnName, true),
		IOUtils.CONTENT_TYPE.XML.getFileExt());
	String path = IOUtils.getPath("/metadati", filename, IOUtils.UNIX_FILE_SEPARATOR);
	extvolume.setPath(path);
	// Hash
	Hash hash = new Hash();
	rispostaControlli.reset();
	rispostaControlli = controlliRecIndiceAipFascicoli
		.getVersioneCorrenteMetaFascicolo(idVerAipFascicolo);
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    List<FasMetaVerAipFascicolo> versioneCorrente = (List<FasMetaVerAipFascicolo>) rispostaControlli
		    .getrObject();
	    FasMetaVerAipFascicolo fasMetaVerAipFasc = (FasMetaVerAipFascicolo) CollectionUtils
		    .find(versioneCorrente, new Predicate() {
			@Override
			public boolean evaluate(final Object object) {
			    return ((FasMetaVerAipFascicolo) object).getNmMeta().toUpperCase()
				    .equals("FASCICOLO");
			}
		    });

	    hash.setValue(fasMetaVerAipFasc.getDsHashFile());
	    String function = fasMetaVerAipFasc.getDsAlgoHashFile() != null
		    ? fasMetaVerAipFasc.getDsAlgoHashFile()
		    : NON_DEFINITO;
	    hash.setHashFunction(function);
	}
	extvolume.setHash(hash);
	moreInfoPVolume.setExternalMetadata(extvolume);
	pVolume.setMoreInfo(moreInfoPVolume);
	pIndex.setPVolume(pVolume);

	/*
	 * ************************** DECORO FILEGROUP DEGLI AIP DEL FASCICOLO
	 * ***************************
	 */
	rispostaControlli.reset();
	rispostaControlli = controlliRecIndiceAipFascicoli
		.getFasContenVerAipFascicolo(idVerAipFascicolo);
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    List<FasContenVerAipFascicolo> fasContenVerAipFascList = (List<FasContenVerAipFascicolo>) rispostaControlli
		    .getrObject();
	    popolaFileGroupAipUdList(pIndex, fasContenVerAipFascList);
	}

	/*
	 * ******************************* DECORO FILEGROUP DEI VERSAMENTI
	 * *******************************
	 */
	rispostaControlli.reset();
	rispostaControlli = controlliRecIndiceAipFascicoli
		.getFasSipVerAipFascicolo(idVerAipFascicolo);
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    List<FasSipVerAipFascicolo> fasSipVerAipFascList = (List<FasSipVerAipFascicolo>) rispostaControlli
		    .getrObject();
	    popolaFileGroupSipFascList(pIndex, fasSipVerAipFascList);
	}

	/*
	 * *************** DECORO PROCESS ***************
	 */
	SIOrgEnteSiam orgEnteConvenz = null;
	if (fasFascicolo.getOrgStrut().getIdEnteConvenz() != null) {
	    orgEnteConvenz = tipoFascicoloHelper.findById(SIOrgEnteSiam.class,
		    fasFascicolo.getOrgStrut().getIdEnteConvenz());
	}
	BigDecimal idAmbiente = BigDecimal
		.valueOf(fasFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
	Process processo = new Process();
	/* Primo Agent */
	// SUBMITTER
	Agent primoAgenteSubmitter = new Agent();
	// Submitter attributes
	primoAgenteSubmitter.setAgentType("legal person");
	// Submitter.AgentID
	AgentID primoAgenteID = new AgentID();
	if (orgEnteConvenz != null) {
	    primoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
	}
	primoAgenteSubmitter.getAgentID().add(primoAgenteID);
	// Submitter.AgentName
	AgentName primoAgenteNome = new AgentName();
	// Submitter.AgentName.FormalName
	if (orgEnteConvenz != null) {
	    primoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
	} else {
	    primoAgenteNome.setFormalName(fasFascicolo.getOrgStrut().getOrgEnte().getDsEnte());
	}
	primoAgenteSubmitter.setAgentName(primoAgenteNome);
	// Submitter.RelevantDocument
	String[] submtrRelevantDocuments = mappaAgenti.get(AGENT_SUBMITTER_RELEVANTDOCUMENT)
		.split("\\|");
	for (String relevantDocument : submtrRelevantDocuments) {
	    RelevantDocument primoAgenteRelevantDocument = new RelevantDocument();
	    primoAgenteRelevantDocument.setValue(relevantDocument);
	    primoAgenteSubmitter.getRelevantDocument().add(primoAgenteRelevantDocument);
	}
	processo.setSubmitter(primoAgenteSubmitter);
	/* Secondo Agent */
	// HOLDER
	Process.Holder secondoAgenteHolder = new Process.Holder();
	// Holder attributes
	secondoAgenteHolder.setAgentType("legal person");
	secondoAgenteHolder.setHolderRole("soggetto produttore");
	// Holder.AgentID
	AgentID secondoAgenteID = new AgentID();
	if (orgEnteConvenz != null) {
	    secondoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
	}
	secondoAgenteHolder.getAgentID().add(secondoAgenteID);
	// Holder.AgentName
	AgentName secondoAgenteNome = new AgentName();
	// Holder.AgentName.FormalName
	if (orgEnteConvenz != null) {
	    secondoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
	} else {
	    secondoAgenteNome.setFormalName(fasFascicolo.getOrgStrut().getOrgEnte().getDsEnte());
	}
	secondoAgenteHolder.setAgentName(secondoAgenteNome);
	// Holder.RelevantDocument
	String[] holderRelevantDocuments = mappaAgenti.get(AGENT_HOLDER_RELEVANTDOCUMENT)
		.split("\\|");
	for (String relevantDocument : holderRelevantDocuments) {
	    RelevantDocument secondoAgenteRelevantDocument = new RelevantDocument();
	    secondoAgenteRelevantDocument.setValue(relevantDocument);
	    secondoAgenteHolder.getRelevantDocument().add(secondoAgenteRelevantDocument);
	}
	processo.getHolder().add(secondoAgenteHolder);
	// LISTA AUTHORIZED SIGNER
	rispostaControlli.reset();
	rispostaControlli = controlliRecuperoFasc.leggiListaUserByHsmUsername(idAmbiente);
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
		rispostaControlli = controlliRecuperoFasc.leggiRuoloAuthorizedSigner(
			authSignerList.get(i).getIdUserIam(), idAmbiente);
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
	    processo.getAuthorizedSigner().addAll(authSignerArray);
	}

	/* Time Reference */
	TimeReference tempo = new TimeReference();
	// TimeInfo
	TimeInfo timeInfo = new TimeInfo();
	timeInfo.setValue(timeRef);
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

    private void popolaFileGroupAipUdList(PIndex pIndex,
	    List<FasContenVerAipFascicolo> fasContenVerAipFascList) {

	Map<String, FileGroup> mappaGruppoFileAipUd = new LinkedHashMap<>();

	for (FasContenVerAipFascicolo fasContenVerAipFasc : fasContenVerAipFascList) {
	    // Recupero urn di tipo NORMALIZZATO dell'Indice AIP dell'unità documentaria
	    AroUrnVerIndiceAipUd aroUrnAipIndiceAipUdNorm = (AroUrnVerIndiceAipUd) CollectionUtils
		    .find(fasContenVerAipFasc.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds(),
			    object -> ((AroUrnVerIndiceAipUd) object).getTiUrn().equals(
				    it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd.NORMALIZZATO));
	    if (aroUrnAipIndiceAipUdNorm != null) {
		File tmpFileItem = new File();
		// ID
		ID tmpIdFileItem = new ID();
		tmpIdFileItem.setValue(aroUrnAipIndiceAipUdNorm.getDsUrn().replaceAll(
			IOUtils.extractPartUrnName(aroUrnAipIndiceAipUdNorm.getDsUrn()), "AIP-UD"));
		tmpIdFileItem.setScheme(schemeAttribute);
		tmpFileItem.setID(tmpIdFileItem);
		// Format
		tmpFileItem.setFormat(IOUtils.CONTENT_TYPE.ZIP.getContentType());
		// Encoding
		tmpFileItem.setEncoding("binary");
		// Extension
		tmpFileItem.setExtension(IOUtils.CONTENT_TYPE.ZIP.getFileExt());
		// Path
		/* Definisco la folder relativa al sistema di conservazione */
		String tmpPath = aroUrnAipIndiceAipUdNorm.getDsUrn().replaceAll(
			IOUtils.extractPartUrnName(aroUrnAipIndiceAipUdNorm.getDsUrn()),
			"unitadocumentarie");
		String path = IOUtils.extractPartUrnName(tmpPath, true);
		String folder = IOUtils.getPath("/contenuto", path, IOUtils.UNIX_FILE_SEPARATOR);
		/* Definisco il nome e l'estensione del file */
		String fileName = IOUtils.getFilename(
			MessaggiWSFormat.estraiNomeFileCompleto(aroUrnAipIndiceAipUdNorm.getDsUrn()
				.replaceAll(IOUtils.extractPartUrnName(
					aroUrnAipIndiceAipUdNorm.getDsUrn()), "AIP-UD")),
			IOUtils.CONTENT_TYPE.ZIP.getFileExt());
		/*
		 * Definisco il percorso relativo del file rispetto alla posizione dell'indice di
		 * conservazione
		 */
		String pathZip = IOUtils.getAbsolutePath(folder, fileName,
			IOUtils.UNIX_FILE_SEPARATOR);
		tmpFileItem.setPath(pathZip);
		// Hash
		Hash tmpHashFileItem = new Hash();
		tmpHashFileItem.setValue(fasContenVerAipFasc.getAroVerIndiceAipUd().getDsHashAip());
		String function = fasContenVerAipFasc.getAroVerIndiceAipUd() != null
			? fasContenVerAipFasc.getAroVerIndiceAipUd().getDsAlgoHashAip()
			: NON_DEFINITO;
		tmpHashFileItem.setHashFunction(function);
		tmpFileItem.setHash(tmpHashFileItem);
		//
		/*
		 * FILEGROUP DEI DOCUMENTI DEL PACCHETTO DI ARCHIVIAZIONE (AIP) DELLE UNITA'
		 * DOCUMENTARIE
		 */
		FileGroup fileGroupAipUp = mappaGruppoFileAipUd
			.computeIfAbsent(
				fasContenVerAipFasc.getFasVerAipFascicolo().getDsUrnAipFascicolo()
					.replaceAll(IOUtils.extractPartUrnName(fasContenVerAipFasc
						.getFasVerAipFascicolo().getDsUrnAipFascicolo()),
						""),
				k -> getFileGroupAipUd(fasContenVerAipFasc.getFasVerAipFascicolo()
					.getDsUrnAipFascicolo()));
		if (fileGroupAipUp != null) {
		    fileGroupAipUp.getFile().add(tmpFileItem);
		}
	    }
	}

	if (mappaGruppoFileAipUd.size() > 0) {
	    pIndex.getFileGroup().addAll(mappaGruppoFileAipUd.values());
	}
    }

    private void popolaFileGroupSipFascList(PIndex pIndex,
	    List<FasSipVerAipFascicolo> fasSipVerAipFascList) {

	Map<String, FileGroup> mappaGruppoFileSipFasc = new LinkedHashMap<>();

	for (FasSipVerAipFascicolo fasSipVerAipFasc : fasSipVerAipFascList) {

	    List<FasXmlVersFascicolo> xmlVersFascicoloList = new ArrayList<>();
	    xmlVersFascicoloList.add(fasSipVerAipFasc.getFasXmlVersFascicoloRich());
	    xmlVersFascicoloList.add(fasSipVerAipFasc.getFasXmlVersFascicoloRisp());

	    for (FasXmlVersFascicolo fasXmlVersFascicolo : xmlVersFascicoloList) {
		if (fasXmlVersFascicolo.getDsUrnXmlVers() != null) {
		    File tmpFileItem = new File();
		    // ID
		    ID tmpIdFileItem = new ID();
		    tmpIdFileItem.setValue(fasXmlVersFascicolo.getDsUrnXmlVers());
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
		    String tmpPath = fasXmlVersFascicolo.getDsUrnXmlVers().replaceAll(
			    IOUtils.extractPartUrnName(fasXmlVersFascicolo.getDsUrnXmlVers()),
			    "SIP-FA");
		    String path = IOUtils.extractPartUrnName(tmpPath, true);
		    String folder = IOUtils.getPath("/sip", path, IOUtils.UNIX_FILE_SEPARATOR);
		    /* Definisco il nome e l'estensione del file */
		    String fileName = IOUtils.getFilename(
			    IOUtils.extractPartUrnName(fasXmlVersFascicolo.getDsUrnXmlVers(), true),
			    IOUtils.CONTENT_TYPE.XML.getFileExt());
		    /*
		     * Definisco il percorso relativo del file rispetto alla posizione dell'indice
		     * di conservazione
		     */
		    String pathSip = IOUtils.getAbsolutePath(folder, fileName,
			    IOUtils.UNIX_FILE_SEPARATOR);
		    tmpFileItem.setPath(pathSip);
		    // Hash
		    Hash tmpHashFileItem = new Hash();
		    tmpHashFileItem.setValue(fasXmlVersFascicolo.getDsHashXmlVers());
		    // MAC#25654
		    String function = fasXmlVersFascicolo.getDsAlgoHashXmlVers() != null
			    ? fasXmlVersFascicolo.getDsAlgoHashXmlVers()
			    : NON_DEFINITO;
		    tmpHashFileItem.setHashFunction(function);
		    // end MAC#25654
		    tmpFileItem.setHash(tmpHashFileItem);
		    //
		    /* FILEGROUP DEI DOCUMENTI DEL PACCHETTO DI VERSAMENTO (SIP) DEL FASCICOLO */
		    FileGroup fileGroupSipFasc = mappaGruppoFileSipFasc.computeIfAbsent(
			    fasXmlVersFascicolo.getDsUrnXmlVers()
				    .replaceAll(IOUtils.extractPartUrnName(
					    fasXmlVersFascicolo.getDsUrnXmlVers()), ""),
			    k -> getFileGroupSipFasc(fasXmlVersFascicolo.getDsUrnXmlVers()));
		    if (fileGroupSipFasc != null) {
			fileGroupSipFasc.getFile().add(tmpFileItem);
		    }
		}
	    }
	}

	if (mappaGruppoFileSipFasc.size() > 0) {
	    pIndex.getFileGroup().addAll(mappaGruppoFileSipFasc.values());
	}
    }

    private FileGroup getFileGroupAipUd(String urn) {
	FileGroup fileGroupAipUd = new FileGroup();
	// ID
	ID idGruppoFileAipUd = new ID();
	idGruppoFileAipUd.setValue(urn.replaceAll(IOUtils.extractPartUrnName(urn), "UDContenute"));
	idGruppoFileAipUd.setScheme(schemeAttribute);
	fileGroupAipUd.setID(idGruppoFileAipUd);
	fileGroupAipUd.setLabel("AIP Unità documentarie");
	fileGroupAipUd.setDescription(
		"Raggruppa tutti gli Aip delle unità documentarie contenute nel fascicolo");

	return fileGroupAipUd;
    }

    private FileGroup getFileGroupSipFasc(String urn) {
	FileGroup fileGroupSipFasc = new FileGroup();
	// ID
	ID idGruppoFileSipFasc = new ID();
	idGruppoFileSipFasc.setValue(urn.replaceAll(IOUtils.extractPartUrnName(urn), "SIP-FA"));
	idGruppoFileSipFasc.setScheme(schemeAttribute);
	fileGroupSipFasc.setID(idGruppoFileSipFasc);
	fileGroupSipFasc.setLabel("Pacchetto di versamento (SIP) di fascicolo");
	fileGroupSipFasc.setDescription(
		"File dei pacchetti di versamento che hanno originato il presente pacchetto di archiviazione: Indice del Pacchetto di versamento (IndiceSIP) e Rapporto versamento (EdV)");

	return fileGroupSipFasc;
    }

    private void popolaMetadatiIntegratiSelfDesc(FasFascicolo tmpFasFascicolo,
	    MetadatiIntegratiSelfDescriptionType miSelfD, String codiceVersione,
	    XMLGregorianCalendar timeRef) throws ParerInternalError, JAXBException {
	OrgStrut orgStrut = ambientiHelper
		.findOrgStrutById(tmpFasFascicolo.getOrgStrut().getIdStrut());
	long idAmbiente = orgStrut.getOrgEnte().getOrgAmbiente().getIdAmbiente();

	/*
	 * Determino il modello xsd attivo per l'ambiente di appartenenza della struttura a cui il
	 * fascicolo appartiene e per la versione del modello xsd corrispondente a quella del
	 * servizio di versamento fascicolo, con il tipo "AIP_SELF_DESCRIPTION_MORE_INFO"
	 */
	rispostaControlli.reset();
	rispostaControlli = controlliRecIndiceAipFascicoli
		.getDecModelloSelfDescMoreInfoV2(idAmbiente, cdVersioneXml);
	if (!rispostaControlli.isrBoolean()) {
	    setRispostaError();
	} else {
	    List<DecModelloXsdFascicolo> decModelloXsdFascList = (List<DecModelloXsdFascicolo>) rispostaControlli
		    .getrObject();
	    log.info("Creazione Indice AIP Fascicoli v2 - ambiente id {}: trovati {}"
		    + " modelli xsd attivi di tipo AIP_SELF_DESCRIPTION_MORE_INFO da processare",
		    idAmbiente, decModelloXsdFascList.size());

	    /* Se per l'ambiente il modello XSD non viene trovato */
	    if (decModelloXsdFascList.isEmpty()) {
		throw new ParerInternalError(
			"Il modello di tipo AIP_SELF_DESCRIPTION_MORE_INFO per la data corrente e l'ambiente "
				+ orgStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente()
				+ " non è definito per la versione " + cdVersioneXml
				+ " del servizio di versamento");
	    }

	    // Procedo nella costruzione della porzione di xml da inserire nel tag <MoreInfo>
	    // secondo l’xsd recuperato.
	    IndiceAIPType indiceAIP = new IndiceAIPType();
	    indiceAIP.setDataCreazione(timeRef);
	    indiceAIP.setFormato("UNI SInCRO 2.0 (UNI 11386:2020)");
	    indiceAIP.setVersioneIndiceAIP(codiceVersione);
	    indiceAIP.setVersioneXSDIndiceAIP("2.0");
	    miSelfD.setIndiceAIP(indiceAIP);

	    /*
	     * Eseguo il marshalling degli oggetti creati in MetadatiIntegratiSelfDescriptionType
	     * per salvarli
	     */
	    StringWriter tmpWriter = marshallMiSelfDesc(miSelfD);

	    // Eseguo la validazione dell'xml prodotto con l'xsd recuperato da
	    // DEC_MODELLO_XSD_FASCICOLO
	    try {
		String xsd = decModelloXsdFascList.get(0).getBlXsd();
		XmlUtils.validateXml(xsd, tmpWriter.toString());
		log.info("Documento validato con successo");
	    } catch (SAXException | IOException ex) {
		log.error(ex.getMessage(), ex);
		throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
	    }
	}
    }

    private StringWriter marshallMiSelfDesc(MetadatiIntegratiSelfDescriptionType miSelfD)
	    throws JAXBException {
	it.eng.parer.aipFascicoli.xml.usselfdescRespV2.ObjectFactory objFctMiSelfDescType = new it.eng.parer.aipFascicoli.xml.usselfdescRespV2.ObjectFactory();
	JAXBElement<MetadatiIntegratiSelfDescriptionType> elementMiSelfDescType = objFctMiSelfDescType
		.createMetadatiIntegratiSelfDescription(miSelfD);

	StringWriter tmpWriter = new StringWriter();
	Marshaller tmpMarshaller = xmlContextCache.getSelfDescMoreInfoCtxV2().createMarshaller();
	tmpMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascSelfDescSchemaV2());
	tmpMarshaller.marshal(elementMiSelfDescType, tmpWriter);
	return tmpWriter;
    }
}
