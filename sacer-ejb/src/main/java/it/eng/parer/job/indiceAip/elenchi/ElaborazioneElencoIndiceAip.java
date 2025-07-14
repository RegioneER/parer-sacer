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

package it.eng.parer.job.indiceAip.elenchi;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import it.eng.parer.elenco.xml.aip.ContenutoSinteticoType;
import it.eng.parer.elenco.xml.aip.DescrizioneElencoIndiciAIPType;
import it.eng.parer.elenco.xml.aip.ElencoIndiciAIP;
import it.eng.parer.elenco.xml.aip.ElencoVersamentoDiOrigineType;
import it.eng.parer.elenco.xml.aip.IndiceAIPType;
import it.eng.parer.elenco.xml.aip.VersatoreType;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAip.helper.CreazioneIndiceAipHelper;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.ElvVChkSoloUdAnnul;
import it.eng.parer.viewEntity.ElvVLisaipudUrndacalcByele;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.ejb.UnitaDocumentarieEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import static it.eng.parer.elencoVersamento.utils.ElencoEnums.GestioneElencoEnum.FIRMA;
import static it.eng.parer.elencoVersamento.utils.ElencoEnums.GestioneElencoEnum.MARCA_FIRMA;
import static it.eng.parer.elencoVersamento.utils.ElencoEnums.GestioneElencoEnum.SIGILLO;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class ElaborazioneElencoIndiceAip {

    @Resource
    private SessionContext context;
    @EJB
    private CreazioneIndiceAipHelper ciaHelper;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    // MEV #31162
    @EJB
    private UnitaDocumentarieEjb udEjb;
    // end MEV #31162

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void creaElencoIndiciAIP(long idElencoVers, long idLogJob) throws ParerInternalError,
	    IOException, JAXBException, NoSuchAlgorithmException, ParseException {
	ElvElencoVer elenco = ciaHelper.findByIdWithLock(ElvElencoVer.class, idElencoVers);
	LogJob logJob = ciaHelper.findById(LogJob.class, idLogJob);
	elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
		ElencoEnums.OpTypeEnum.START_CREA_ELENCO_INDICI_AIP.name(), logJob);
	String tiGestione;

	if (StringUtils.isNotBlank(elenco.getTiGestElenco())) {
	    tiGestione = elenco.getTiGestElenco();
	} else if (StringUtils.isNotBlank(elenco.getDecCriterioRaggr().getTiGestElencoCriterio())) {
	    tiGestione = elenco.getDecCriterioRaggr().getTiGestElencoCriterio();
	} else {
	    OrgAmbiente orgAmbiente = elenco.getOrgStrut().getOrgEnte().getOrgAmbiente();
	    boolean elencoStandard = elenco.getFlElencoStandard().equals("1");
	    boolean elencoFiscale = elenco.getFlElencoFisc().equals("1");
	    if (!elencoStandard && !elencoFiscale) {
		tiGestione = configurationHelper.getValoreParamApplicByStrut(
			CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD,
			BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
			BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
	    } else if (elencoFiscale) {
		tiGestione = configurationHelper.getValoreParamApplicByStrut(
			CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC,
			BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
			BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
	    } else {
		tiGestione = configurationHelper.getValoreParamApplicByStrut(
			CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC,
			BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
			BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
	    }
	}
	/*
	 * 11/12/2017 - Modifica analisi elenchi 1.5
	 */
	if (tiGestione.equals(ElencoEnums.GestioneElencoEnum.FIRMA.name())
		|| tiGestione.equals(ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name())) {
	    ElvVChkSoloUdAnnul udSoloAnnul = ciaHelper.findViewById(ElvVChkSoloUdAnnul.class,
		    BigDecimal.valueOf(idElencoVers));
	    if (udSoloAnnul.getFlSoloDocAnnul().equals("1")
		    && udSoloAnnul.getFlSoloUdAnnul().equals("1")
		    && udSoloAnnul.getFlSoloUpdUdAnnul().equals("1")) {
		tiGestione = ElencoEnums.GestioneElencoEnum.NO_FIRMA.name();
		String nota = elenco.getNtElencoChiuso();
		elenco.setNtElencoChiuso((StringUtils.isNotBlank(nota) ? nota + ";" : "")
			+ "L'elenco contiene solo versamenti annullati");
		elenco.setTiGestElenco(tiGestione);
	    }
	}

	ElencoEnums.GestioneElencoEnum tiGestioneEnum = ElencoEnums.GestioneElencoEnum
		.valueOf(tiGestione);
	ElvElencoVersDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
	switch (tiGestioneEnum) {
	case FIRMA:
	case MARCA_FIRMA:
	case SIGILLO:
	case MARCA_SIGILLO:
	    // EVO#16486
	    verificaUrnAipElenco(elenco.getIdElencoVers());
	    // end EVO#16486
	    ElencoIndiciAIP elencoIndiciAip = creaElencoIndiceAipMarshallObject(elenco);
	    String elencoIndiciAipString = marshallElenco(elencoIndiciAip);
	    String hash = new HashCalculator()
		    .calculateHashSHAX(elencoIndiciAipString, TipiHash.SHA_256).toHexBinary();

	    ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
	    fileElencoVers.setTiFileElencoVers(ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name());
	    fileElencoVers
		    .setBlFileElencoVers(elencoIndiciAipString.getBytes(StandardCharsets.UTF_8));
	    fileElencoVers.setIdStrut(BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
	    XMLGregorianCalendar cal = elencoIndiciAip.getDescrizioneElencoIndiciAIP()
		    .getDataCreazione();
	    fileElencoVers.setDtCreazioneFile(
		    cal != null ? XmlDateUtility.xmlGregorianCalendarToDate(cal) : null);
	    fileElencoVers.setDsHashFile(hash);
	    fileElencoVers.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
	    fileElencoVers.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
	    fileElencoVers.setCdVerXsdFile(Costanti.VERSIONE_ELENCO_INDICE_AIP);

	    if (elenco.getElvFileElencoVers() == null) {
		elenco.setElvFileElencoVers(new ArrayList<>());
	    }
	    elenco.addElvFileElencoVer(fileElencoVers);

	    // EVO#16486
	    /* Calcolo e persisto lo urn dell'indice dell'elenco indici AIP firmato */
	    CSVersatore versatore = new CSVersatore();
	    versatore.setAmbiente(
		    elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	    versatore.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
	    versatore.setStruttura(elenco.getOrgStrut().getNmStrut());
	    // sistema (new URN)
	    final String sistema = configurationHelper
		    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	    versatore.setSistemaConservazione(sistema);
	    // calcolo parte urn ORIGINALE
	    String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(versatore);
	    // calcolo parte urn NORMALIZZATO
	    String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
		    Costanti.UrnFormatter.VERS_FMT_STRING);
	    // salvo ORIGINALE
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
		    MessaggiWSFormat.formattaUrnElencoIndiciAIPNonFirmati(tmpUrn,
			    sdf.format(elenco.getDtCreazioneElenco()),
			    Long.toString(elenco.getIdElencoVers())),
		    ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
	    // salvo NORMALIZZATO
	    urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
		    MessaggiWSFormat.formattaUrnElencoIndiciAIPNonFirmati(tmpUrnNorm,
			    sdf.format(elenco.getDtCreazioneElenco()),
			    Long.toString(elenco.getIdElencoVers())),
		    ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
	    // end EVO#16486

	    elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name());
	    elencoDaElab
		    .setTiStatoElenco(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name());
	    elencoDaElab.setTsStatoElenco(new Date());

	    // EVO 19304 (ElenchiVersamento1.15 pag. 60)
	    evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
		    "CREAZIONE_ELENCO_INDICE_AIP",
		    "Almeno una unità documentaria nell’elenco è non annullata e la gestione elenco = FIRMA o MARCA_FIRMA",
		    ElvStatoElencoVer.TiStatoElenco.ELENCO_INDICI_AIP_CREATO, null);

	    elenco.setNiIndiciAip(BigDecimal.valueOf(
		    elencoIndiciAip.getContenutoSintetico().getNumeroIndiciAIP().longValue()));
	    elenco.setDtCreazioneElencoIxAip(fileElencoVers.getDtCreazioneFile());

	    ciaHelper.updateDocumentiElencoIndiceAIPStatoConservDiverso(elenco.getIdElencoVers(),
		    CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		    UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name(),
		    UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO.name());
	    ciaHelper.updateUnitaDocElencoIndiceAIPStatoConvervDiverso(elenco.getIdElencoVers(),
		    CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		    UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name(),
		    UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO.name());
	    ciaHelper.updateAggiornamentiElencoIndiceAIPStatoConservDiverso(
		    elenco.getIdElencoVers(),
		    CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI,
		    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO);
	    break;
	case NO_FIRMA:
	    if (evEjb.soloUdAnnul(BigDecimal.valueOf(idElencoVers))) {
		String nota = elenco.getNtElencoChiuso();
		elenco.setNtElencoChiuso((StringUtils.isNotBlank(nota) ? nota + ";" : "")
			+ "L'elenco contiene solo versamenti annullati");
	    }
	    context.getBusinessObject(ElaborazioneElencoIndiceAip.class)
		    .setElencoCompletatoTxReq(elenco.getIdElencoVers());
	    break;
	default:
	    throw new AssertionError(tiGestioneEnum.name() + " non supportato");
	}
	elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
		ElencoEnums.OpTypeEnum.END_CREA_ELENCO_INDICI_AIP.name(), logJob);
    }

    private ElencoIndiciAIP creaElencoIndiceAipMarshallObject(ElvElencoVer elenco) {
	ElencoIndiciAIP objElenco = new ElencoIndiciAIP();
	objElenco.setVersioneElencoIndiciAIP(Costanti.VERSIONE_ELENCO_INDICE_AIP);

	popolaVersatore(objElenco, elenco.getOrgStrut());
	popolaDescrizioniUrnElenco(objElenco, elenco);

	objElenco.setContenutoSintetico(new ContenutoSinteticoType());

	List<AroVerIndiceAipUd> aroVerIndiceAipUdNoAnnul = ciaHelper
		.retrieveAroVerIndiceAipUdOrdered(elenco.getIdElencoVers());
	objElenco.getContenutoSintetico()
		.setNumeroIndiciAIP(BigInteger.valueOf(aroVerIndiceAipUdNoAnnul.size()));

	popolaContenutoAnalitico(objElenco, aroVerIndiceAipUdNoAnnul);

	return objElenco;
    }

    private void popolaVersatore(ElencoIndiciAIP objElenco, OrgStrut strut) {
	OrgEnte ente = strut.getOrgEnte();
	OrgAmbiente ambiente = ente.getOrgAmbiente();

	objElenco.setVersatore(new VersatoreType());
	objElenco.getVersatore().setAmbiente(ambiente.getNmAmbiente());
	objElenco.getVersatore().setEnte(ente.getNmEnte());
	objElenco.getVersatore().setStruttura(strut.getNmStrut());
    }

    private void popolaDescrizioniUrnElenco(ElencoIndiciAIP objElenco, ElvElencoVer elenco) {
	objElenco.setDescrizioneElencoIndiciAIP(new DescrizioneElencoIndiciAIPType());
	// EVO#16486
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	CSVersatore versatore = new CSVersatore();
	versatore.setAmbiente(elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	versatore.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
	versatore.setStruttura(elenco.getOrgStrut().getNmStrut());
	// sistema (new URN)
	final String sistema = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	versatore.setSistemaConservazione(sistema);
	// calcolo parte urn ORIGINALE
	String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(versatore);
	// salvo ORIGINALE
	final String urnElencoIndiciAip = MessaggiWSFormat.formattaUrnElencoIndiciAIP(tmpUrn,
		sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers()));
	objElenco.getDescrizioneElencoIndiciAIP().setUrn(urnElencoIndiciAip);
	// end EVO#16486
	objElenco.getDescrizioneElencoIndiciAIP().setDataCreazione(
		XmlDateUtility.dateToXMLGregorianCalendar(Calendar.getInstance().getTime()));
	objElenco.setElencoVersamentoDiOrigine(new ElencoVersamentoDiOrigineType());
	objElenco.getElencoVersamentoDiOrigine()
		.setIdElenco(BigInteger.valueOf(elenco.getIdElencoVers()));
	objElenco.getElencoVersamentoDiOrigine().setNomeElenco(elenco.getNmElenco());
	// EVO#16486
	// calcolo urn ORIGINALE
	// urn:<sistema>:<nome ente>:<nome struttura>:ElencoVers-UD-<data creazione>-<id elenco>
	final String urnElencoVersamento = MessaggiWSFormat.formattaUrnElencoVersamento(sistema,
		versatore.getEnte(), versatore.getStruttura(),
		sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers()));
	// end EVO#16486
	objElenco.getElencoVersamentoDiOrigine().setUrnElenco(urnElencoVersamento);
    }

    private void popolaContenutoAnalitico(ElencoIndiciAIP objElenco,
	    List<AroVerIndiceAipUd> indiciAip) {
	objElenco.setContenutoAnalitico(new ElencoIndiciAIP.ContenutoAnalitico());
	for (AroVerIndiceAipUd aroVerIndiceAipUd : indiciAip) {
	    IndiceAIPType indice = new IndiceAIPType();
	    // EVO#16486
	    // Recupero lo urn ORIGINALE dalla tabella ARO_URN_VER_INDICE_AIP_UD
	    AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
		    .find(aroVerIndiceAipUd.getAroUrnVerIndiceAipUds(), new Predicate() {
			@Override
			public boolean evaluate(final Object object) {
			    return ((AroUrnVerIndiceAipUd) object).getTiUrn()
				    .equals(TiUrnVerIxAipUd.ORIGINALE);
			}
		    });
	    if (urnVerIndiceAipUd != null) {
		indice.setUrnIndiceAIP(urnVerIndiceAipUd.getDsUrn());
	    } else {
		indice.setUrnIndiceAIP(aroVerIndiceAipUd.getDsUrn());
	    }
	    // end EVO#16486
	    // MAC#18826
	    indice.setHashIndiceAIP(StringUtils.isNotBlank(aroVerIndiceAipUd.getDsHashIndiceAip())
		    ? aroVerIndiceAipUd.getDsHashIndiceAip()
		    : aroVerIndiceAipUd.getDsHashAip());

	    objElenco.getContenutoAnalitico().getIndiceAIP().add(indice);
	}
    }

    private String marshallElenco(ElencoIndiciAIP elencoIndiciAIP)
	    throws MarshalException, JAXBException {
	StringWriter tmpWriter = new StringWriter();
	Marshaller tmpMarshaller = xmlContextCache.getElencoIndiciAipCtx_ElencoIndiciAIP()
		.createMarshaller();
	tmpMarshaller.marshal(elencoIndiciAIP, tmpWriter);
	tmpWriter.flush();
	return tmpWriter.toString();
    }

    // <editor-fold desc="Questi metodi potrebbero essere unificati">
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setCompletato(ElvElencoVer elenco, List<String> statiUdDocDaCompletare,
	    long idUtente, ElencoEnums.GestioneElencoEnum tiGestioneEnum, String modalitaLog) {
	ElvElencoVersDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
	elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.COMPLETATO.name());
	// Elimino l'elenco da elaborare
	ciaHelper.removeEntity(elencoDaElab, true);
	//
	ciaHelper.updateDocumentiElencoIndiceAIP(elenco.getIdElencoVers(), statiUdDocDaCompletare,
		ElencoEnums.UdDocStatusEnum.IN_ELENCO_COMPLETATO.name());
	ciaHelper.updateUnitaDocElencoIndiceAIP(elenco.getIdElencoVers(), statiUdDocDaCompletare,
		ElencoEnums.UdDocStatusEnum.IN_ELENCO_COMPLETATO.name());
	ciaHelper.updateAggiornamentiElencoIndiceAIP(elenco.getIdElencoVers(),
		transformCollection(statiUdDocDaCompletare),
		AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_COMPLETATO);

	Set<Long> idUnitaDocSet = elencoHelper
		.retrieveUdVersOrAggInElenco(elenco.getIdElencoVers());
	for (Long idUnitaDoc : idUnitaDocSet) {
	    AroUnitaDoc ud = ciaHelper.findById(AroUnitaDoc.class, idUnitaDoc);
	    // MEV #31162
	    // Recupero il nome agente: utente o JOB del Sigillo
	    String agente = "";
	    if (modalitaLog.equals(Constants.FUNZIONALITA_ONLINE)) {
		IamUser utente = ciaHelper.findById(IamUser.class, idUtente);
		agente = utente.getNmUserid();
	    } else {
		agente = Constants.JOB_SIGILLO;
	    }
	    // Recupero se solo firma o firma e marca
	    String evento = (tiGestioneEnum.equals(FIRMA) || tiGestioneEnum.equals(SIGILLO))
		    ? Constants.FIRMA_ELENCO_INDICE_AIP_UD
		    : Constants.MARCA_ELENCO_INDICE_AIP_UD;
	    // end MEV #31162

	    // Lock su ud
	    elencoHelper.lockUnitaDoc(ud);
	    if (ud.getTiStatoConservazione()
		    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())) {
		ud.setTiStatoConservazione(
			CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name());
		// MEV #31162
		udEjb.insertLogStatoConservUd(ud.getIdUnitaDoc(), agente, evento,
			CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name(), modalitaLog);
		// end MEV #31162
	    } // MAC 34839
	    else if (ud.getTiStatoConservazione()
		    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name())) {
		// MEV #31162
		udEjb.insertLogStatoConservUd(ud.getIdUnitaDoc(), agente, evento,
			CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name(),
			modalitaLog);
		// end MEV #31162
	    }
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setElencoCompletatoTxReq(long idElencoVers) {
	setElencoCompletato(idElencoVers);
    }

    private void setElencoCompletato(long idElencoVers) {
	final ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElencoVers);
	ElvElencoVersDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
	elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.COMPLETATO.name());
	ciaHelper.removeEntity(elencoDaElab, true);

	// EVO 19304
	evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
		"CREAZIONE_ELENCO_INDICE_AIP",
		"Almeno una unità documentaria nell’elenco è non annullata e la gestione elenco = NO_FIRMA",
		ElvStatoElencoVer.TiStatoElenco.COMPLETATO, null);

	ciaHelper.updateDocumentiElencoIndiceAIPStatoConservDiverso(elenco.getIdElencoVers(),
		CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name(),
		UdDocStatusEnum.IN_ELENCO_COMPLETATO.name());
	ciaHelper.updateUnitaDocElencoIndiceAIPStatoConvervDiverso(elenco.getIdElencoVers(),
		CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name(),
		UdDocStatusEnum.IN_ELENCO_COMPLETATO.name());
	ciaHelper.updateAggiornamentiElencoIndiceAIPStatoConservDiverso(elenco.getIdElencoVers(),
		CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(),
		AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI,
		AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_COMPLETATO);
    }
    // </editor-fold>

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateDocumentiElencoIndiceAip(long idElencoVers, String tiStatoElenco) {
	ciaHelper.updateDocumentiElencoIndiceAIP(idElencoVers, tiStatoElenco);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateDocumentiElencoIndiceAIP(long idElencoVers,
	    List<String> tiStatoDocElencoVersOld, String tiStatoDocElencoVersNew) {
	ciaHelper.updateDocumentiElencoIndiceAIP(idElencoVers, tiStatoDocElencoVersOld,
		tiStatoDocElencoVersNew);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateAggiornamentiElencoIndiceAIP(long idElencoVers,
	    List<AroUpdUDTiStatoUpdElencoVers> tiStatoUpdElencoVersOld,
	    AroUpdUDTiStatoUpdElencoVers tiStatoUpdElencoVersNew) {
	ciaHelper.updateAggiornamentiElencoIndiceAIP(idElencoVers, tiStatoUpdElencoVersOld,
		tiStatoUpdElencoVersNew);
    }

    /**
     * @deprecated
     *
     * @param idElencoVers            identificativo dell'elenco di versamento
     * @param tiStatoConservazione    tipo di stato conservazione
     * @param tiStatoDocElencoVersCor tipo stato documento elenco veramento corrente
     * @param tiStatoDocElencoVersNew tipo stato documento elenco versamento nuovo
     */
    @Deprecated
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateDocumentiElencoIndiceAIPStatoConservDiverso(long idElencoVers,
	    String tiStatoConservazione, String tiStatoDocElencoVersCor,
	    String tiStatoDocElencoVersNew) {
	ciaHelper.updateDocumentiElencoIndiceAIPStatoConservDiverso(idElencoVers,
		tiStatoConservazione, tiStatoDocElencoVersCor, tiStatoDocElencoVersNew);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocElencoIndiceAip(long idElencoVers, String tiStatoElenco) {
	ciaHelper.updateUnitaDocElencoIndiceAIP(idElencoVers, tiStatoElenco);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocElencoIndiceAIP(long idElencoVers, List<String> tiStatoElencoOld,
	    String tiStatoElencoNew) {
	ciaHelper.updateUnitaDocElencoIndiceAIP(idElencoVers, tiStatoElencoOld, tiStatoElencoNew);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUnitaDocElencoIndiceAIPStatoConservDiverso(long idElencoVers,
	    String tiStatoConservazione, String tiStatoUdElencoVersCor, String tiStatoElencoNew) {
	ciaHelper.updateUnitaDocElencoIndiceAIPStatoConvervDiverso(idElencoVers,
		tiStatoConservazione, tiStatoUdElencoVersCor, tiStatoElencoNew);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateAggiornamentiElencoIndiceAIPStatoConservDiverso(long idElencoVers,
	    String tiStatoConservazione, AroUpdUDTiStatoUpdElencoVers tiStatoUpdElencoVersCor,
	    AroUpdUDTiStatoUpdElencoVers tiStatoElencoNew) {
	ciaHelper.updateAggiornamentiElencoIndiceAIPStatoConservDiverso(idElencoVers,
		tiStatoConservazione, tiStatoUpdElencoVersCor, tiStatoElencoNew);
    }

    private List<AroUpdUDTiStatoUpdElencoVers> transformCollection(List<String> stati) {
	return (List<AroUpdUDTiStatoUpdElencoVers>) CollectionUtils.collect(stati,
		new Transformer() {
		    @Override
		    public Object transform(Object input) {
			String stato = (String) input;
			return AroUpdUDTiStatoUpdElencoVers.valueOf(stato);
		    }
		});
    }

    // EVO#16486
    public void verificaUrnAipElenco(long idElencoVers) throws ParerInternalError, ParseException {
	String sistemaConservazione = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	ElvElencoVer elenco = ciaHelper.findById(ElvElencoVer.class, idElencoVers);

	DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
	String dataInizioParam = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN);
	Date dataInizio = dateFormat.parse(dataInizioParam);

	List<ElvVLisaipudUrndacalcByele> elvVLisUrndacalcList = ciaHelper
		.getElvVLisaipudUrndacalcByele(elenco.getIdElencoVers());
	for (ElvVLisaipudUrndacalcByele elvVLisUrndacalc : elvVLisUrndacalcList) {
	    // controllo : dtCreazione <= dataInizioCalcNuoviUrn
	    // controllo e calcolo URN normalizzato
	    if (!elvVLisUrndacalc.getDtCreazione().after(dataInizio)) {
		AroUnitaDoc aroUnitaDoc = ciaHelper.findById(AroUnitaDoc.class,
			elvVLisUrndacalc.getId().getIdUnitaDoc().longValue());
		CSVersatore versatore = this.getVersatoreUd(aroUnitaDoc, sistemaConservazione);
		CSChiave chiave = this.getChiaveUd(aroUnitaDoc);
		// Gestione KEY NORMALIZED / URN PREGRESSI
		this.sistemaUrnIndiceAipElenco(aroUnitaDoc, elvVLisUrndacalc.getDtVersMax(),
			dataInizio, versatore, chiave);
	    }
	}
    }

    public void sistemaUrnIndiceAipElenco(AroUnitaDoc aroUnitaDoc, Date dtVersMax, Date dataInizio,
	    CSVersatore versatore, CSChiave chiave) throws ParerInternalError {
	// 1. se il numero normalizzato sull’unità doc nel DB è nullo ->
	// il sistema aggiorna ARO_UNITA_DOC
	// controllo : dtVersMax <= dataInizioCalcNuoviUrn
	if (!dtVersMax.after(dataInizio)
		&& StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
	    // calcola e verifica la chiave normalizzata
	    String cdKeyNormalized = MessaggiWSFormat
		    .normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
	    if (urnHelper.existsCdKeyNormalized(
		    aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
		    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(),
		    cdKeyNormalized)) {
		// urn normalizzato già presente su sistema
		throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
			+ MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave) + " della struttura "
			+ versatore.getEnte() + "/" + versatore.getStruttura() + " è già presente");
	    } else {
		// cd key normalized (se calcolato)
		if (StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
		    aroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
		}
	    }
	}

	// 2. verifica pregresso
	// A. check data massima versamento recuperata in precedenza rispetto parametro
	// su db
	if (!dtVersMax.after(dataInizio)) {
	    // B. eseguo registra urn comp pregressi
	    urnHelper.scriviUrnCompPreg(aroUnitaDoc, versatore, chiave);
	    // C. eseguo registra urn sip pregressi
	    // C.1. eseguo registra urn sip pregressi ud
	    urnHelper.scriviUrnSipUdPreg(aroUnitaDoc, versatore, chiave);
	    // C.2. eseguo registra urn sip pregressi documenti aggiunti
	    urnHelper.scriviUrnSipDocAggPreg(aroUnitaDoc, versatore, chiave);
	    // C.3. eseguo registra urn pregressi upd
	    urnHelper.scriviUrnSipUpdPreg(aroUnitaDoc, versatore, chiave);
	}

	// calcolo e persisto urn aip ud
	urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);

    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
	CSChiave csc = new CSChiave();
	csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
	csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
	csc.setNumero(ud.getCdKeyUnitaDoc());

	return csc;
    }

    public CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
	CSVersatore csv = new CSVersatore();
	csv.setStruttura(ud.getOrgStrut().getNmStrut());
	csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
	csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	// sistema (new URN)
	csv.setSistemaConservazione(sistemaConservazione);

	return csv;
    }
    // end EVO#16486
}
