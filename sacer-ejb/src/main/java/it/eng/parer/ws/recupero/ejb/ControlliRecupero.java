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

package it.eng.parer.ws.recupero.ejb;

import static it.eng.paginator.util.HibernateUtils.bigDecimalFrom;
import static it.eng.parer.ws.utils.Costanti.UKNOWN_EXT;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.util.HibernateUtils;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroLogStatoConservUd;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.VolFileVolumeConserv;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.VrsSessioneVers;
import it.eng.parer.entity.VrsUrnXmlSessioneVers;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.AgentLegalPersonDto;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliRecupero")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRecupero {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecupero.class);
    public static final String ERROR_LETTURA_UD = "Eccezione nella lettura  della tabella delle unità doc ";
    public static final String ERROR_LETTURA_COMPONENTI = "Eccezione nella lettura della tabella dei componenti in UD ";
    public static final String ERROR_CONTROLLI_LEGGI_XML = "Eccezione ControlliRecupero.leggiXMLSessioneversUd ";
    public static final String ERROR_XML_VERSAMENTO = "Eccezione nella lettura  della tabella degli XML di versamento UD ";
    public static final String ERROR_FILE_V_INDICE_AIP = "Eccezione nella lettura della tabella dei file di versione indice AIP ";
    public static final String ERROR_VER_INDICE_AIP = "Eccezione nella lettura della tabella delle versioni dell'indice AIP ";
    public static final String JAVAX_PERSISTENCE_FETCHGRAPH = "javax.persistence.fetchgraph";
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private ControlliRecupero me;

    @Resource
    EJBContext context;

    public RispostaControlli leggiUnitaDoc(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	AroUnitaDoc tmpUnitaDoc;
	EntityGraph<AroUnitaDoc> entityGraph = entityManager.createEntityGraph(AroUnitaDoc.class);
	entityGraph.addAttributeNodes("decRegistroUnitaDoc");
	entityGraph.addAttributeNodes("iamUser");
	entityGraph.addAttributeNodes("decTipoUnitaDoc");
	entityGraph.addAttributeNodes("aroNotaUnitaDocs");
	entityGraph.addSubgraph("orgStrut").addSubgraph("orgEnte").addAttributeNodes("orgAmbiente");
	Map<String, Object> properties = new HashMap<>();
	properties.put(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
	try {
	    tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc, properties);
	    rispostaControlli.setrObject(tmpUnitaDoc);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiUnitaDoc " + e.getMessage()));
	    log.error(ERROR_LETTURA_UD, e);
	}
	return rispostaControlli;
    }

    // EVO#20972
    public RispostaControlli leggiVolumeConserv(long idVolumeConserv) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	VolVolumeConserv volume = null;

	try {
	    String queryStr = "select t  from VolVolumeConserv t "
		    + "where t.idVolumeConserv = :idVolumeConserv";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idVolumeConserv", idVolumeConserv);
	    volume = (VolVolumeConserv) query.getSingleResult();
	    rispostaControlli.setrObject(volume);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVolumeConserv " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei volumi di conservazione ", e);
	}
	return rispostaControlli;
    }
    // end EVO#20972

    // OK prove conservazione
    public RispostaControlli leggiVolumiUnitaDoc(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VolVolumeConserv> lstVolumi = null;

	try {
	    // String queryStr = "select t from VolAppartUnitaDocVolume audv "
	    // + "join FETCH audv.volVolumeConserv t JOIN FETCH t.orgStrut strut "
	    // + "where audv.aroUnitaDoc.idUnitaDoc = :idUnitaDoc";
	    // MAC 35187
	    String queryStr = "select t from VolVolumeConserv t "
		    + "JOIN FETCH t.volAppartUnitaDocVolumes audv " + "JOIN FETCH t.orgStrut strut "
		    + "JOIN FETCH strut.orgEnte ente " + "JOIN FETCH ente.orgAmbiente ambiente "
		    + "where audv.aroUnitaDoc.idUnitaDoc = :idUnitaDoc";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);
	    lstVolumi = query.getResultList();
	    rispostaControlli.setrObject(lstVolumi);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVolumiUnitaDoc " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei volumi unità doc", e);
	}
	return rispostaControlli;
    }

    /**
     *
     * @param idUnitaDoc          id unita doc
     * @param estraiFileIndiceAip se true considera solo i file dei componenti per i quali è stato
     *                            calcolato l'indice AIP
     *
     * @return istanza di RispostaControlli con i campi valorizzati come segue: se va in errore:
     *         rBoolean è false se ok: rBoolean è true rObject contiene una lista di ComponenteRec
     *         corrispondenti ai componenti ed rString contiene il nome da attribuire alla directory
     *         dello zip che conterrà l'unità doc
     */
    // OK
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli leggiCompFileInUD(long idUnitaDoc, boolean estraiFileIndiceAip) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    String queryStr = "";

	    if (estraiFileIndiceAip) {
		// MAC #34605
		queryStr = "select DISTINCT acd from AroCompDoc acd join acd.aroCompVerIndiceAipUds acviau "
			+ "where acviau.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc and acd.tiSupportoComp = 'FILE'";
		// end MAC #34605
	    } else {
		queryStr = "select acd from AroCompDoc acd "
			+ "where acd.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
			+ "and acd.tiSupportoComp = 'FILE'";
	    }

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = null;

		// Ricavo l'URN normalizzato o iniziale:
		// - se presente l'iniziale, prendo quello (significa che avevo creato un indice AIP
		// con la vecchia
		// nomenclatura)
		// - se non presente l'iniziale, prendo il normalizzato (nuovo urn)
		AroCompUrnCalc tmpUrnIniziale = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);

		AroCompUrnCalc compUrnCalc = tmpUrnIniziale != null ? tmpUrnIniziale
			: unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp,
				TiUrn.NORMALIZZATO);
		if (compUrnCalc != null) {
		    urnCompleto = compUrnCalc.getDsUrn();
		} else {
		    urnCompleto = tmpCmp.getDsUrnCompCalc();
		}

		// urn iniz (per Tivoli)
		compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp,
			TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		} else {
		    urnCompletoIniz = tmpCmp.getDsUrnCompCalc();
		}

		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		// MEV#22921 Parametrizzazione servizi di recupero
		tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
		tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli.setrString(this.generaNomeFileUD(
			lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileUD(idUnitaDoc));
	    }
	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInUD " + e.getMessage()));
	    log.error(ERROR_LETTURA_COMPONENTI, e);
	}
	return rispostaControlli;
    }

    // EVO#20972
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli leggiCompFileInUDAIPV2(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    // MAC#30890
	    String queryStr = "SELECT DISTINCT acd from AroCompDoc acd "
		    + "join acd.aroCompVerIndiceAipUds acviau "
		    + "where acviau.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "and acd.tiSupportoComp = 'FILE'";
	    // end MAC#30890

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = null;

		AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO);
		if (compUrnCalc != null) {
		    urnCompleto = compUrnCalc.getDsUrn();
		} else {
		    urnCompleto = tmpCmp.getDsUrnCompCalc();
		}

		// urn iniz
		compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp,
			TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		} else {
		    urnCompletoIniz = tmpCmp.getDsUrnCompCalc();
		}
		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		// MEV#22921 Parametrizzazione servizi di recupero
		tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
		tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli.setrString(this.generaNomeFileAIPV2(
			lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileAIPV2(idUnitaDoc));
	    }
	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInUDAIPV2 " + e.getMessage()));
	    log.error(ERROR_LETTURA_COMPONENTI, e);
	}
	return rispostaControlli;
    }
    // end EVO#20972

    public RispostaControlli leggiCompFileInUDVersamentoUd(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    String queryStr = "SELECT compDoc FROM AroCompDoc compDoc "
		    + "JOIN compDoc.aroStrutDoc strutDoc " + "JOIN strutDoc.aroDoc doc "
		    + "JOIN doc.aroUnitaDoc unitaDoc " + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "AND doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
		    + "AND compDoc.tiSupportoComp = 'FILE'";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
		// urn iniz
		AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		}
		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());

		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli.setrString(this.generaNomeFileUD(
			lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileUD(idUnitaDoc));
	    }
	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInUDVersamentoUd " + e.getMessage()));
	    log.error(ERROR_LETTURA_COMPONENTI, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiCompFileInUDByTipoDoc(long idUnitaDoc, long idTipoDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    String queryStr = "select acd from AroCompDoc acd "
		    + "where acd.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "and acd.aroStrutDoc.aroDoc.decTipoDoc.idTipoDoc = :idTipoDoc "
		    + "and acd.tiSupportoComp = 'FILE'";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);
	    query.setParameter("idTipoDoc", idTipoDoc);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
		// urn iniz
		AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		}
		// dto
		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		// MEV#22921 Parametrizzazione servizi di recupero
		tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
		tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli.setrString(this.generaNomeFileUD(
			lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc())
			+ this.generaSuffissoTipoDocUD(
				lstComponenti.get(0).getAroStrutDoc().getAroDoc().getDecTipoDoc()));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileUD(idUnitaDoc)
			+ this.generaSuffissoTipoDocUD(idTipoDoc));
	    }
	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInUDByTipoDoc " + e.getMessage()));
	    log.error(ERROR_LETTURA_COMPONENTI, e);
	}
	return rispostaControlli;
    }

    private String generaSuffissoTipoDocUD(long idTipoDoc) {
	DecTipoDoc dtd = entityManager.find(DecTipoDoc.class, idTipoDoc);
	return this.generaSuffissoTipoDocUD(dtd);
    }

    private String generaSuffissoTipoDocUD(DecTipoDoc dtd) {
	StringBuilder tmpString = new StringBuilder();
	tmpString.append("_");
	tmpString.append(dtd.getNmTipoDoc());
	return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    private String generaNomeFileUD(long idUd) {
	AroUnitaDoc aud = entityManager.find(AroUnitaDoc.class, idUd);
	return this.generaNomeFileUD(aud);
    }

    private String generaNomeFileUD(AroUnitaDoc aud) {
	StringBuilder tmpString = new StringBuilder();
	tmpString.append(aud.getCdRegistroKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getAaKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getCdKeyUnitaDoc());
	return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // EVO#20972
    private String generaNomeFileAIPV2(long idUd) {
	AroUnitaDoc aud = entityManager.find(AroUnitaDoc.class, idUd);
	return this.generaNomeFileAIPV2(aud);
    }

    private String generaNomeFileAIPV2(AroUnitaDoc aud) {
	String sistemaConservazione = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	CSVersatore versatore = this.getVersatoreUd(aud, sistemaConservazione);
	CSChiave chiave = this.getChiaveUd(aud);
	String tmpUrn = MessaggiWSFormat.formattaUrnAipUdAip(
		MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
			Costanti.UrnFormatter.VERS_FMT_STRING),
		MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true,
			Costanti.UrnFormatter.UD_FMT_STRING));
	return ComponenteRec.estraiNomeFileCompleto(tmpUrn);
    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
	CSChiave csc = new CSChiave();
	csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
	csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
	csc.setNumero(ud.getCdKeyUnitaDoc());

	return csc;
    }

    private CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
	CSVersatore csv = new CSVersatore();
	csv.setStruttura(ud.getOrgStrut().getNmStrut());
	csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
	csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	// sistema (new URN)
	csv.setSistemaConservazione(sistemaConservazione);

	return csv;
    }
    // end EVO#20972

    // OK
    public RispostaControlli leggiCompFileInDoc(long idDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    String queryStr = "select acd  from AroCompDoc acd "
		    + "where acd.aroStrutDoc.aroDoc.idDoc = :idDoc "
		    + "and acd.tiSupportoComp = 'FILE'";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idDoc", idDoc);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
		// urn iniz
		AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		}
		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli.setrString(
			this.generaNomeFileDOC(lstComponenti.get(0).getAroStrutDoc().getAroDoc()));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileDOC(idDoc));
	    }
	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInDoc " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei componenti in DOC ", e);
	}
	return rispostaControlli;
    }

    // OK
    private String generaNomeFileDOC(long idDoc) {
	AroDoc ad = entityManager.find(AroDoc.class, idDoc);
	return this.generaNomeFileDOC(ad);
    }

    // Nota MAC#24837 : ad.getNiOrdDoc() non dovrebbe mai essere null in quanto, ad ogni servizio
    // di recupero precede una logica di gestione del pregresso degli urn che quindi valorizza
    // l'attributo NiOrdDoc
    private String generaNomeFileDOC(AroDoc ad) {
	// MAC#24837
	String urnPartDocumento = (ad.getNiOrdDoc() != null)
		? MessaggiWSFormat.formattaUrnPartDocumento(Costanti.CategoriaDocumento.Documento,
			ad.getNiOrdDoc().intValue(), true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
			Costanti.UrnFormatter.PAD5DIGITS_FMT)
		: MessaggiWSFormat.formattaUrnPartDocumento(
			Costanti.CategoriaDocumento.getEnum(ad.getTiDoc()),
			ad.getPgDoc().intValue());
	// end MAC#24837
	StringBuilder tmpString = new StringBuilder();
	AroUnitaDoc aud = ad.getAroUnitaDoc();
	tmpString.append(aud.getCdRegistroKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getAaKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getCdKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(urnPartDocumento);
	return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // OK
    public RispostaControlli leggiCompFileInComp(long idComp) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroCompDoc> lstComponenti = null;
	List<ComponenteRec> lstComp;

	try {
	    lstComp = new ArrayList<>();

	    // è un'idiozia, visto che non mi renderà mai più di una riga, ma volevo
	    // scriverla
	    // in modo uniforme.
	    String queryStr = "select acd  from AroCompDoc acd "
		    + "where acd.idCompDoc = :idCompDoc " + "and acd.tiSupportoComp = 'FILE'";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idCompDoc", idComp);

	    lstComponenti = query.getResultList();
	    for (AroCompDoc tmpCmp : lstComponenti) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
		// urn iniz
		AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
			.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
		if (compUrnCalc != null) {
		    urnCompletoIniz = compUrnCalc.getDsUrn();
		}
		ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
		// MEV#22921 Parametrizzazione servizi di recupero
		tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
		tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
		tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
		// Gestisco il formato
		String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
		if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
		    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
		} else {
		    if (tmpCmp.getDecFormatoFileDoc() != null) {
			tmpCRec.setEstensioneFile(
				tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
		    } else {
			tmpCRec.setEstensioneFile(UKNOWN_EXT);
		    }
		}
		lstComp.add(tmpCRec);
	    }
	    if (!lstComp.isEmpty()) {
		rispostaControlli
			.setrString(this.generaNomeFileComp(lstComponenti.get(0), lstComp.get(0)));
	    } else {
		rispostaControlli.setrString(this.generaNomeFileComp(idComp));
	    }

	    rispostaControlli.setrObject(lstComp);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiCompFileInComp " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei componenti in COMP ", e);
	}
	return rispostaControlli;
    }

    private String generaNomeFileComp(long idComp) {
	AroCompDoc tmpCmp = entityManager.find(AroCompDoc.class, idComp);
	// urn
	String urnCompletoIniz = null;
	String urnCompleto = unitaDocumentarieHelper
		.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
	// urn iniz
	AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp,
		TiUrn.INIZIALE);
	if (compUrnCalc != null) {
	    urnCompletoIniz = compUrnCalc.getDsUrn();
	}
	// dto
	ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
	tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
	// Gestisco il formato
	String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
	if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
	    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
	} else {
	    if (tmpCmp.getDecFormatoFileDoc() != null) {
		tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
	    } else {
		tmpCRec.setEstensioneFile(UKNOWN_EXT);
	    }
	}
	return this.generaNomeFileComp(tmpCmp, tmpCRec);
    }

    // OK
    private String generaNomeFileComp(AroCompDoc acd, ComponenteRec comp) {
	StringBuilder tmpString = new StringBuilder();
	AroUnitaDoc aud = acd.getAroStrutDoc().getAroDoc().getAroUnitaDoc();
	tmpString.append(aud.getCdRegistroKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getAaKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(aud.getCdKeyUnitaDoc());
	tmpString.append("-");
	tmpString.append(comp.getNomeFileBreve());
	return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // OK - caricaparametri
    public RispostaControlli leggiChiaveUnitaDoc(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	AroUnitaDoc tmpUnitaDoc = null;
	CSChiave chiave;

	try {
	    tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
	    chiave = new CSChiave();
	    chiave.setTipoRegistro(tmpUnitaDoc.getCdRegistroKeyUnitaDoc());
	    chiave.setAnno(tmpUnitaDoc.getAaKeyUnitaDoc().longValue());
	    chiave.setNumero(tmpUnitaDoc.getCdKeyUnitaDoc());
	    rispostaControlli.setrObject(chiave);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiChiaveUnitaDoc " + e.getMessage()));
	    log.error(ERROR_LETTURA_UD, e);
	}
	return rispostaControlli;
    }

    // OK - caricaparametri
    public RispostaControlli leggiVersatoreUnitaDoc(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	AroUnitaDoc tmpUnitaDoc = null;
	CSVersatore versatore;

	try {
	    tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
	    versatore = new CSVersatore();
	    versatore.setStruttura(tmpUnitaDoc.getOrgStrut().getNmStrut());
	    versatore.setEnte(tmpUnitaDoc.getOrgStrut().getOrgEnte().getNmEnte());
	    versatore.setAmbiente(
		    tmpUnitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	    rispostaControlli.setrObject(versatore);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVersatoreUnitaDoc " + e.getMessage()));
	    log.error(ERROR_LETTURA_UD, e);
	}
	return rispostaControlli;
    }

    // MAC#30890
    public RispostaControlli leggiXmlSessioniVersamentiAip(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = new ArrayList<>();
	List<VrsSessioneVers> tmpVsvList = new ArrayList<>();

	log.debug("Lettura xml di versamento per AIP - INIZIO");

	/*
	 * recupero la sessione relativa al versamento originale dell'UD. per ora non ho bisogno di
	 * conoscerne l'elenco dei documenti
	 */
	log.debug("Ricavo la sessione di versamento per l'UD id={}", idUnitaDoc);
	String queryStr = "select t from VrsSessioneVers t "
		+ "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and t.aroDoc is null "
		+ "and t.tiStatoSessioneVers = 'CHIUSA_OK' "
		+ "and t.tiSessioneVers = 'VERSAMENTO' ";

	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("idUnitaDoc", idUnitaDoc);

	List<VrsSessioneVers> vsv = query.getResultList();
	if (!vsv.isEmpty()) {
	    tmpVsvList.add(vsv.get(0));
	    //
	    /*
	     * ricavo la lista dei documenti versati ed aggiunti riferiti da questa unità
	     * documentaria. La lista dovrebbe essere sempre non-vuota poichè deve contenere almeno
	     * i documenti versati nel versamento iniziale, che vengono sempre riportati.
	     */
	    queryStr = "SELECT DISTINCT ad from AroDoc ad " + "join ad.aroStrutDocs asd "
		    + "join asd.aroCompDocs acd " + "join acd.aroCompVerIndiceAipUds acviau "
		    + "where acviau.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn "
		    + "order by ad.dtCreazione, ad.niOrdDoc, ad.pgDoc ";
	    EntityGraph<AroDoc> entityGraph = entityManager.createEntityGraph(AroDoc.class);
	    entityGraph.addAttributeNodes("decTipoDoc");
	    query = entityManager.createQuery(queryStr);
	    query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);
	    query.setParameter("idUnitaDocIn", idUnitaDoc);
	    List<AroDoc> ads = query.getResultList();
	    for (AroDoc tmpAd : ads) {
		if (tmpAd.getTiCreazione()
			.equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
		    log.debug("Ricavo la sessione di versamento del documento aggiunto id={}",
			    tmpAd.getIdDoc());
		    /*
		     * per ogni doc trovato, ricavo la sessione di versamento e la accodo alla
		     * collection
		     */
		    queryStr = "select t from VrsSessioneVers t " + "where t.aroDoc.idDoc = :idDoc "
			    + "and t.tiStatoSessioneVers = 'CHIUSA_OK' ";
		    query = entityManager.createQuery(queryStr);
		    query.setParameter("idDoc", tmpAd.getIdDoc());
		    vsv = query.getResultList();
		    if (!vsv.isEmpty()) {
			tmpVsvList.add(vsv.get(0));
		    }
		}
	    }

	    /*
	     * ricavo i documenti XML relativi ad ogni sessione di versamento individuata
	     */
	    for (VrsSessioneVers tmpsvExt : tmpVsvList) {
		queryStr = "select xml from VrsXmlDatiSessioneVers xml "
			+ "where xml.vrsDatiSessioneVers.vrsSessioneVers.idSessioneVers = :idSessioneVers "
			+ "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' ";

		query = entityManager.createQuery(queryStr);
		query.setParameter("idSessioneVers", tmpsvExt.getIdSessioneVers());

		List<VrsXmlDatiSessioneVers> vxdsv = query.getResultList();

		lstDatiSessioneVerses.addAll(vxdsv);
	    }

	    rispostaControlli.setrObject(lstDatiSessioneVerses);
	    rispostaControlli.setrBoolean(true);
	} else {
	    rispostaControlli.setCodErr("666");
	    rispostaControlli.setDsErr(
		    "Errore interno: non sono stati eseguiti versamenti per l'UD " + idUnitaDoc);
	}

	log.debug("Lettura xml di versamento per AIP - FINE");
	return rispostaControlli;
    }
    // end MAC#30980

    public RispostaControlli leggiXMLSessioneversUd(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

	try {
	    String queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
		    + "where xml.vrsDatiSessioneVers.vrsSessioneVers.aroUnitaDoc.idUnitaDoc  = :idUnitaDoc "
		    + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
		    + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
		    + "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    lstDatiSessioneVerses = query.getResultList();
	    rispostaControlli.setrObject(lstDatiSessioneVerses);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ERROR_CONTROLLI_LEGGI_XML + e.getMessage()));
	    log.error(ERROR_XML_VERSAMENTO, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversDoc(long idDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

	String queryStr = null;

	try {
	    AroDoc tmpAroDoc = entityManager.find(AroDoc.class, idDoc);
	    if (tmpAroDoc.getTiCreazione()
		    .equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
		// ricavo i documenti XML relativi ad un versameto di aggiunta, relativo al
		// documento richiesto
		queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
			+ "where xml.vrsDatiSessioneVers.vrsSessioneVers.aroDoc.idDoc = :idDoc "
			+ "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
			+ "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
			+ "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
			+ "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";
	    } else {
		// ricavo i documenti XML relativi al versamento principale, a cui appartiene il
		// documento
		queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
			+ "join xml.vrsDatiSessioneVers.vrsSessioneVers.aroUnitaDoc.aroDocs ad "
			+ "where ad.idDoc = :idDoc "
			+ "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
			+ "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
			+ "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiSessioneVers = 'VERSAMENTO' "
			+ "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";
	    }

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idDoc", idDoc);

	    lstDatiSessioneVerses = query.getResultList();
	    rispostaControlli.setrObject(lstDatiSessioneVerses);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ERROR_CONTROLLI_LEGGI_XML + e.getMessage()));
	    log.error(ERROR_XML_VERSAMENTO, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversComp(long idComp) {
	AroCompDoc tmpAroCompDoc = entityManager.find(AroCompDoc.class, idComp);
	return this.leggiXMLSessioneversDoc(tmpAroCompDoc.getAroStrutDoc().getAroDoc().getIdDoc());
    }

    public RispostaControlli leggiXMLSessioneVersUdPrincipale(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses;

	try {
	    String queryStr = "SELECT xmlDatiSessioneVers FROM VrsXmlDatiSessioneVers xmlDatiSessioneVers "
		    + "JOIN xmlDatiSessioneVers.vrsDatiSessioneVers datiSessioneVers "
		    + "JOIN datiSessioneVers.vrsSessioneVers sessioneVers "
		    + "JOIN sessioneVers.aroUnitaDoc unitaDoc "
		    + "WHERE unitaDoc.idUnitaDoc  = :idUnitaDoc "
		    + "AND sessioneVers.tiSessioneVers = 'VERSAMENTO' "
		    + "AND sessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
		    + "AND datiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
		    + "AND xmlDatiSessioneVers.tiXmlDati IN ('RICHIESTA', 'INDICE_FILE', 'RISPOSTA') "
		    + "ORDER BY sessioneVers.dtChiusura ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    lstDatiSessioneVerses = query.getResultList();
	    rispostaControlli.setrObject(lstDatiSessioneVerses);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ERROR_CONTROLLI_LEGGI_XML + e.getMessage()));
	    log.error(ERROR_XML_VERSAMENTO, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneVersDocAggiunto(long idDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

	try {
	    String queryStr = "SELECT xmlDatiSessioneVers FROM VrsXmlDatiSessioneVers xmlDatiSessioneVers "
		    + "JOIN xmlDatiSessioneVers.vrsDatiSessioneVers datiSessioneVers "
		    + "JOIN datiSessioneVers.vrsSessioneVers sessioneVers "
		    + "JOIN sessioneVers.aroUnitaDoc unitaDoc " + "JOIN sessioneVers.aroDoc doc "
		    + "WHERE doc.idDoc = :idDoc "
		    + "AND sessioneVers.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
		    + "AND sessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
		    + "AND datiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
		    + "AND xmlDatiSessioneVers.tiXmlDati IN ('RICHIESTA', 'RISPOSTA') "
		    + "ORDER BY sessioneVers.dtChiusura ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idDoc", idDoc);

	    lstDatiSessioneVerses = query.getResultList();
	    rispostaControlli.setrObject(lstDatiSessioneVerses);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ERROR_CONTROLLI_LEGGI_XML + e.getMessage()));
	    log.error(ERROR_XML_VERSAMENTO, e);
	}
	return rispostaControlli;
    }

    // MAC#30890
    public RispostaControlli leggiXmlVersamentiAipUpdDaUnitaDoc(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroXmlUpdUnitaDoc> xmlupds = new ArrayList<>();

	try {
	    /*
	     * ricavo la lista degli aggiornamenti versati riferiti all'unita doc per l'aip corrente
	     */
	    String queryStr = "SELECT DISTINCT upd from AroUpdUdVerIndiceAipUd updvi "
		    + "join updvi.aroUpdUnitaDoc upd " + "join upd.aroUnitaDoc ud "
		    + "where ud.idUnitaDoc = :idUnitaDoc " + "order by upd.tsIniSes ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);
	    List<AroUpdUnitaDoc> upds = query.getResultList();
	    for (AroUpdUnitaDoc tmpUpd : upds) {
		queryStr = "select xml from AroXmlUpdUnitaDoc xml "
			+ "where xml.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ";

		query = entityManager.createQuery(queryStr);
		query.setParameter("idUpdUnitaDoc", tmpUpd.getIdUpdUnitaDoc());

		xmlupds.addAll(query.getResultList());
	    }
	    rispostaControlli.setrObject(xmlupds);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecIndiceAip.leggiXmlVersamentiAipUpdDaUnitaDoc "
			    + e.getMessage()));
	    log.error("Eccezione nella lettura xml di versamento aggiornamento metadati ", e);
	}
	return rispostaControlli;
    }
    // end MAC#30890

    // EVO#20972
    public RispostaControlli leggiXMLSessioneversUpd(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroXmlUpdUnitaDoc> xmlupds = new ArrayList<>();

	try {
	    /*
	     * ricavo la lista degli aggiornamenti versati riferiti all'unita doc corrente.
	     */
	    String queryStr = "SELECT DISTINCT upd FROM AroUpdUnitaDoc upd "
		    + "join upd.aroUnitaDoc ud " + "where ud.idUnitaDoc = :idUnitaDoc "
		    + "order by upd.tsIniSes ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    List<AroUpdUnitaDoc> upds = query.getResultList();
	    for (AroUpdUnitaDoc tmpUpd : upds) {
		queryStr = "select xml from AroXmlUpdUnitaDoc xml "
			+ "where xml.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ";

		query = entityManager.createQuery(queryStr);
		query.setParameter("idUpdUnitaDoc", tmpUpd.getIdUpdUnitaDoc());

		xmlupds.addAll(query.getResultList());
	    }
	    rispostaControlli.setrObject(xmlupds);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLSessioneversUpd " + e.getMessage()));
	    log.error("Eccezione nella lettura xml di versamento aggiornamento metadati ", e);
	}
	return rispostaControlli;
    }
    // end EVO#20972

    public RispostaControlli contaXMLIndiceAIP(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "select count(aro) from AroVerIndiceAipUd aro "
		    + "WHERE aro.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "and aro.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    rispostaControlli.setrBoolean(true);
	    rispostaControlli.setrLong((Long) query.getSingleResult());
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.contaXMLIndiceAIP " + e.getMessage()));
	    log.error(ERROR_FILE_V_INDICE_AIP, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIP(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "SELECT aro FROM AroFileVerIndiceAipUd aro JOIN FETCH aro.aroVerIndiceAipUd"
		    + " WHERE aro.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "and aro.aroVerIndiceAipUd.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    List<AroFileVerIndiceAipUd> listaAroFile = query.getResultList();
	    listaAroFile.stream().forEach(this::loadAroUrnVerIndiceAipUds);
	    rispostaControlli.setrObject(listaAroFile);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLIndiceAIP " + e.getMessage()));
	    log.error(ERROR_FILE_V_INDICE_AIP, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIPOs(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "SELECT aro FROM AroVerIndiceAipUd aro "
		    + "WHERE aro.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "AND aro.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    List<AroVerIndiceAipUd> listaAroVer = query.getResultList();
	    listaAroVer.stream().forEach(this::loadAroUrnVerIndiceAipUdsOs);
	    rispostaControlli.setrObject(listaAroVer);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLIndiceAIP " + e.getMessage()));
	    log.error(ERROR_FILE_V_INDICE_AIP, e);
	}
	return rispostaControlli;
    }

    // MEV#30395
    public RispostaControlli leggiXMLIndiceAIPV2Os(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroVerIndiceAipUd> listaAroVerIxAip = null;

	try {
	    String queryStr = "SELECT DISTINCT u FROM AroVerIndiceAipUd u JOIN FETCH u.aroUrnVerIndiceAipUds "
		    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
		    + "ORDER BY u.pgVerIndiceAip DESC ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    listaAroVerIxAip = query.getResultList();
	    rispostaControlli.setrObject(listaAroVerIxAip);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVerIndiceAIPV2 " + e.getMessage()));
	    log.error(ERROR_VER_INDICE_AIP, e);
	}
	return rispostaControlli;
    }
    // end MEV#30395

    // EVO#20972:MEV#20971
    public RispostaControlli leggiXMLIndiceAIPV2(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroFileVerIndiceAipUd> listaAroFile = null;

	try {
	    String queryStr = "SELECT DISTINCT aro FROM AroFileVerIndiceAipUd aro JOIN FETCH aro.aroVerIndiceAipUd u JOIN FETCH u.aroUrnVerIndiceAipUds "
		    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
		    + "ORDER BY u.pgVerIndiceAip DESC ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    listaAroFile = query.getResultList();
	    rispostaControlli.setrObject(listaAroFile);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLIndiceAIPV2 " + e.getMessage()));
	    log.error(ERROR_FILE_V_INDICE_AIP, e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIPExternal(Long idUnitaDoc) {
	RispostaControlli rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    // Ricavo tutte le versioni precedenti dell'AIP provenienti da altri conservatori
	    String queryStr = "SELECT u FROM AroVLisaipudSistemaMigraz u "
		    + "WHERE u.idUnitaDoc = :idUnitaDoc ";
	    Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", HibernateUtils.bigDecimalFrom(idUnitaDoc));
	    List<AroVLisaipudSistemaMigraz> versioniPrecedentiExternal = query.getResultList();
	    rispostaControlli.setrObject(versioniPrecedentiExternal);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLIndiceAIPExternal " + e.getMessage()));
	    log.error(
		    "Eccezione durante il recupero delle versioni precedenti dell'AIP provenienti da altri conservatori ",
		    e);
	}
	return rispostaControlli;
    }
    // end EVO#20972:MEV#20971

    public RispostaControlli leggiElvFileElencoVers(long idUnitaDoc, String tiFileElencoVers,
	    String tiFileElencoVers2) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	ElvFileElencoVer fileElencoVers = null;

	try {
	    // MAC#25864
	    String queryStr = "SELECT fileElencoVers FROM AroVLisElvVer elv, AroVerIndiceAipUd verIndiceAipUd, ElvFileElencoVer fileElencoVers "
		    + "WHERE verIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = elv.aroVLisElvVerId.idUnitaDoc "
		    + "AND verIndiceAipUd.pgVerIndiceAip = (SELECT MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d WHERE d.aroIndiceAipUd = verIndiceAipUd.aroIndiceAipUd) "
		    + "AND fileElencoVers.elvElencoVer = verIndiceAipUd.elvElencoVer "
		    + "AND elv.aroVLisElvVerId.idUnitaDoc = :idUnitaDoc "
		    + "AND fileElencoVers.tiFileElencoVers = :tiFileElencoVers ";
	    // end MAC#25864
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", bigDecimalFrom(idUnitaDoc));
	    query.setParameter("tiFileElencoVers", tiFileElencoVers);

	    List<ElvFileElencoVer> listaFileElencoVers = query.getResultList();
	    if (!listaFileElencoVers.isEmpty()) {
		// Se sto considerando la firma
		if (tiFileElencoVers.equals(
			it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.FIRMA_ELENCO_INDICI_AIP
				.name())) {
		    // begin: verificare se rimuovere questa query che non viene utilizzata
		    String queryStr2 = "SELECT fileElencoVers FROM AroUnitaDoc unitaDoc "
			    + "JOIN unitaDoc.elvElencoVer elencoVers "
			    + "JOIN elencoVers.elvFileElencoVers fileElencoVers "
			    + "WHERE unitadoc.idUnitaDoc = :idUnitaDoc "
			    + "AND fileElencoVers.tiFileElencoVers = :tiFileElencoVers ";

		    javax.persistence.Query query2 = entityManager.createQuery(queryStr2);
		    query2.setParameter("idUnitaDoc", idUnitaDoc);
		    query2.setParameter("tiFileElencoVers", tiFileElencoVers2);
		    // end: verificare se rimuovere questa query che non viene utilizzata

		    List<ElvFileElencoVer> listaFileElencoVers2 = query.getResultList();
		    if (!listaFileElencoVers2.isEmpty()) {
			fileElencoVers = listaFileElencoVers2.get(0);
		    }
		} // altrimenti è il caso della marca
		else if (tiFileElencoVers.equals(
			it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.MARCA_FIRMA_ELENCO_INDICI_AIP
				.name())) {
		    fileElencoVers = listaFileElencoVers.get(0);
		}
	    }
	    rispostaControlli.setrObject(fileElencoVers);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiElvFileElencoVers " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei file elenco indici AIP ", e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiAroUdAppartVerSerie(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "SELECT udAppartVerSerie "
		    + "FROM AroUdAppartVerSerie udAppartVerSerie "
		    + "JOIN udAppartVerSerie.serContenutoVerSerie contenutoVerSerie "
		    + "JOIN contenutoVerSerie.serVerSerie verSerie "
		    + "JOIN verSerie.serSerie serie "
		    + "JOIN verSerie.serStatoVerSeries statoVerSerie "
		    + "JOIN serie.serStatoSeries statoSerie "
		    + "WHERE udAppartVerSerie.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
		    + "AND contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
		    + "AND statoVerSerie.tiStatoVerSerie != 'ANNULLATA' "
		    + "AND verSerie.pgVerSerie = (SELECT MAX(ver.pgVerSerie) FROM SerVerSerie ver WHERE ver.idVerSerie = verSerie.idVerSerie) "
		    + "AND verSerie.idStatoVerSerieCor = statoVerSerie.idStatoVerSerie "
		    + "AND serie.idStatoSerieCor = statoSerie.idStatoSerie "
		    + "ORDER BY serie.aaSerie ASC ";

	    Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);
	    List<AroUdAppartVerSerie> uds = query.getResultList();
	    rispostaControlli.setrObject(uds);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiAroUdAppartVerSerie " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella delle appartenenze ud alla serie ", e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiFasUnitaDocFascicolo(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    String queryStr = "SELECT unitaDocFascicolo FROM FasUnitaDocFascicolo unitaDocFascicolo JOIN FETCH unitaDocFascicolo.fasFascicolo "
		    + "WHERE unitaDocFascicolo.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);

	    List<FasUnitaDocFascicolo> uds = query.getResultList();
	    rispostaControlli.setrObject(uds);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiFasUnitaDocFascicolo " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella delle appartenenze ud al fascicolo ",
		    e);
	}
	return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkIdDocumentoinUD(long idUnitaDoc, String idDocumento) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<AroDoc> lstAd;

	try {
	    String queryStr = "select t from AroDoc t "
		    + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn "
		    + "and t.cdKeyDocVers = :cdKeyDocVersIn " + "and t.dtAnnul > :dataDiOggiIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDocIn", idUnitaDoc);
	    query.setParameter("cdKeyDocVersIn", idDocumento);
	    query.setParameter("dataDiOggiIn", new Date());

	    lstAd = query.getResultList();
	    if (lstAd.size() == 1) {
		rispostaControlli.setrLong(lstAd.get(0).getIdDoc());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.DOC_010_001);
		rispostaControlli.setDsErr(
			MessaggiWSBundle.getString(MessaggiWSBundle.DOC_010_001, idDocumento));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.checkIdDocumentoinUD " + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza del documento da recuperare ", e);
	}
	return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkIdComponenteinDoc(long idDoc, long progressivo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<AroCompDoc> lstCompDocs;

	try {
	    String queryStr = "select t from AroCompDoc t "
		    + "where t.aroStrutDoc.aroDoc.idDoc = :idDocIn "
		    + "and t.niOrdCompDoc = :niOrdCompDocIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idDocIn", idDoc);
	    query.setParameter("niOrdCompDocIn", new BigDecimal(progressivo));

	    lstCompDocs = query.getResultList();
	    if (lstCompDocs.size() == 1) {
		rispostaControlli.setrLong(lstCompDocs.get(0).getIdCompDoc());
		if (lstCompDocs.get(0).getAroCompDoc() != null) {
		    // questo permette di capire se è un sottocomponente
		    rispostaControlli
			    .setrLongExtended(lstCompDocs.get(0).getAroCompDoc().getIdCompDoc());
		}
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.COMP_010_001);
		rispostaControlli.setDsErr(
			MessaggiWSBundle.getString(MessaggiWSBundle.COMP_010_001, progressivo));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.checkIdComponenteinDoc " + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza del componenente da recuperare ", e);
	}
	return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkTipoDocumentoperStrut(long idStrut, String nmTipoDoc,
	    String descChiaveUd) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<DecTipoDoc> lstTipoDocs;

	try {
	    String queryStr = "select t from DecTipoDoc t "
		    + "where t.orgStrut.idStrut = :idStrutIn " + "and t.nmTipoDoc = :nmTipoDocIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idStrutIn", idStrut);
	    query.setParameter("nmTipoDocIn", nmTipoDoc);

	    lstTipoDocs = query.getResultList();
	    if (lstTipoDocs.size() == 1) {
		rispostaControlli.setrLong(lstTipoDocs.get(0).getIdTipoDoc());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_001);
		rispostaControlli.setDsErr(MessaggiWSBundle
			.getString(MessaggiWSBundle.DOC_001_001, descChiaveUd, nmTipoDoc)
			.replaceFirst("Documento", "Unità Documentaria"));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.checkTipoDocumentoperStrut " + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza del tipo documento per la struttura ", e);
	}
	return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkTipoDocumentoinUD(long idUnitaDoc, String nmTipoDoc,
	    String descChiaveUd) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<AroDoc> lstAroDocs;

	try {
	    String queryStr = "select t from AroDoc t "
		    + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn "
		    + "and t.decTipoDoc.nmTipoDoc = :nmTipoDocIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDocIn", idUnitaDoc);
	    query.setParameter("nmTipoDocIn", nmTipoDoc);

	    lstAroDocs = query.getResultList();
	    if (!lstAroDocs.isEmpty()) {
		rispostaControlli.setrLong(lstAroDocs.get(0).getDecTipoDoc().getIdTipoDoc());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_004);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.DOC_001_004,
			nmTipoDoc, descChiaveUd));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.checkTipoDocumentoinUD " + e.getMessage()));
	    log.error(
		    "Eccezione nella verifica esistenza del tipo documento nella Unità Documentaria ",
		    e);
	}
	return rispostaControlli;
    }

    /**
     * Verifica esistenza chiave normalizzata
     *
     * @param versatore       nome versatore
     * @param chiave          chiave unita doc
     * @param idRegistro      id registro
     * @param idUnitaDoc      id unita doc
     * @param cdKeyNormalized chiave normalizzata unita doc
     *
     * @return RispostaControlli con risultato della verifica effettuata
     */
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli calcCdKeyNormAndUrnPreg(CSVersatore versatore, CSChiave chiave,
	    long idRegistro, long idUnitaDoc, String cdKeyNormalized) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    // recupero parametro DATA_INIZIO_CALC_NUOVI_URN
	    String dataInizioParam = configurationHelper.getValoreParamApplicByApplic(
		    CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN);
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date dtInizioCalcNuoviUrn = dateFormat.parse(dataInizioParam);

	    rispostaControlli = this.getDtMaxVersMaxByUd(idUnitaDoc);
	    if (rispostaControlli.getrLong() != -1) {
		// dtVersMax ottenuto da vista
		Date dtVersMax = rispostaControlli.getrDate();
		// controllo : dtVersMax <= dataInizioCalcNuoviUrn
		if ((dtVersMax.before(dtInizioCalcNuoviUrn)
			|| dtVersMax.equals(dtInizioCalcNuoviUrn))) {
		    // lock ud
		    Map<String, Object> properties = new HashMap<>();
		    properties.put("javax.persistence.lock.timeout", 25000);
		    AroUnitaDoc aroUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc,
			    LockModeType.PESSIMISTIC_WRITE, properties);
		    //
		    if (StringUtils.isBlank(cdKeyNormalized)) {
			// calcola e verifica la chiave normalizzata
			cdKeyNormalized = MessaggiWSFormat.normalizingKey(chiave.getNumero()); // base
			rispostaControlli = this.checkCdKeyNormalized(idRegistro, chiave,
				cdKeyNormalized);
			// 666 error
			if (rispostaControlli.getCodErr() != null) {
			    return rispostaControlli;
			}
			if (rispostaControlli.getrLong() == 0 /* esiste chiave normalizzata */) {
			    // Esiste key normalized
			    rispostaControlli.setCodErr(MessaggiWSBundle.UD_005_005);
			    rispostaControlli.setDsErr(
				    MessaggiWSBundle.getString(MessaggiWSBundle.UD_005_005,
					    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave)));
			    rispostaControlli.setrBoolean(false);
			    return rispostaControlli;
			} else {
			    // aggiorno cdKeyNormalized
			    aroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
			    entityManager.persist(aroUnitaDoc);
			}
		    }
		    /*
		     * URN pregressi
		     */
		    // componenti
		    urnHelper.scriviUrnCompPreg(aroUnitaDoc, versatore, chiave);
		    // sip ud
		    urnHelper.scriviUrnSipUdPreg(aroUnitaDoc, versatore, chiave);
		    // sip doc
		    urnHelper.scriviUrnSipDocAggPreg(aroUnitaDoc, versatore, chiave);
		    // sip upd
		    urnHelper.scriviUrnSipUpdPreg(aroUnitaDoc, versatore, chiave);

		    // indice AIP
		    AroVerIndiceAipUd aroVerIndiceAipUd = evHelper
			    .getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
		    if (aroVerIndiceAipUd != null
			    && !aroVerIndiceAipUd.getDtCreazione().after(dtInizioCalcNuoviUrn)) {
			// eseguo registra urn aip pregressi
			urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
		    }

		    // unlock
		    entityManager.flush();
		} // if dtInizioCalcNuoviUrn
	    } // if getDtMaxVersMaxByUd
	      // OK
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    context.setRollbackOnly();
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.calcCdKeyNormAndUrnPreg " + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza / aggiornamento URN normalizzato ", e);
	}
	return rispostaControlli;
    }

    private RispostaControlli getDtMaxVersMaxByUd(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrLong(-1); // entity non trovata
	AroVDtVersMaxByUnitaDoc aroVDtVersMaxByUnitaDoc = null;

	try {
	    Query query = entityManager.createQuery(
		    "SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ");
	    query.setParameter("idUnitaDoc", bigDecimalFrom(idUnitaDoc));
	    aroVDtVersMaxByUnitaDoc = (AroVDtVersMaxByUnitaDoc) query.getSingleResult();
	    if (aroVDtVersMaxByUnitaDoc == null) {
		rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666P);
		rispostaControlli
			.setDsErr("ControlliSemantici.getDtMaxVersMaxByUd: non presente per id "
				+ idUnitaDoc);
	    } else {
		rispostaControlli.setrLong(0);
		rispostaControlli.setrDate(aroVDtVersMaxByUnitaDoc.getDtVersMax());
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.getDtMaxVersMaxByUd: " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica", e);
	}
	return rispostaControlli;
    }

    private RispostaControlli checkCdKeyNormalized(long idRegistro, CSChiave key,
	    String cdKeyUnitaDocNormaliz) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrLong(-1);
	Long num;
	try {
	    String queryStr = "select count(ud) from AroUnitaDoc ud "
		    + "where ud.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistro "
		    + " and ud.aaKeyUnitaDoc = :aaKeyUnitaDoc "
		    + " and ud.cdKeyUnitaDoc != :cdKeyUnitaDoc "
		    + " and ud.cdKeyUnitaDocNormaliz = :cdKeyUnitaDocNormaliz ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idRegistro", idRegistro);
	    query.setParameter("aaKeyUnitaDoc", bigDecimalFrom(key.getAnno()));
	    query.setParameter("cdKeyUnitaDoc", key.getNumero());
	    query.setParameter("cdKeyUnitaDocNormaliz", cdKeyUnitaDocNormaliz);

	    num = (Long) query.getSingleResult();
	    if (num > 0) {
		// esiste chiave normalized
		rispostaControlli.setrLong(0);
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.checkCdKeyNormalized: " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica ", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli leggiFirReportIdsAndGenZipName(long idCompDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    // TOFIX: da verificare se nome file componente corretto
	    String baseNameFileComp = this.generaNomeFileComp(idCompDoc);

	    String queryStr = "select t.idFirReport from FirReport t "
		    + "where t.aroCompDoc.idCompDoc = :idCompDoc";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idCompDoc", idCompDoc);

	    List<Long> idFirReports = query.getResultList();
	    if (!idFirReports.isEmpty()) {
		rispostaControlli.setrString(baseNameFileComp.concat("_ReportVerificaFirma"));// TOFIX:
		// verifica
		// nome
		// zip
		// file
		rispostaControlli.setrObject(idFirReports);
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.COMP_006_003);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_006_003,
			baseNameFileComp));
	    }

	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiFirReportIdsAndGenZipName "
			    + e.getMessage()));
	    log.error(ERROR_LETTURA_COMPONENTI, e);
	}
	return rispostaControlli;
    }

    // EVO#20972
    /**
     * Restituisce la lista degli utenti applicativi configurati per la firma HSM dato l'ambiente
     *
     * @param idAmbiente id ambiente
     *
     * @return RispostaControlli con risultato del recupero effettuato
     */
    public RispostaControlli leggiListaUserByHsmUsername(BigDecimal idAmbiente) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<UsrUser> listaUser = new ArrayList<>();
	try {
	    AplParamApplic paramApplic = configurationHelper
		    .getParamApplic(CostantiDB.ParametroAppl.HSM_USERNAME);
	    // Ricavo le coppie di valori multipli del parametro HSM_USERNAME sull'ambiente
	    List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
		    .getAplValParamApplicMultiList(
			    BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
	    for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
		// Splitto la coppia: il primo valore sarà lo user applicativo, il secondo lo user
		// hsm
		String[] chiaveValore = valParamApplicMulti.getDsValoreParamApplic().split(",");
		// Recupero l'utente nella tabella, partendo da nmUserid IAM univoco
		UsrUser user = userHelper.findUsrUser(chiaveValore[0]);
		if (listaUser.stream().noneMatch(t -> t.getNmUserid().equals(user.getNmUserid()))) {
		    listaUser.add(user);
		}
	    }

	    rispostaControlli.setrObject(listaUser);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.leggiListaUserByHsmUsername: " + e.getMessage()));
	    log.error(
		    "Eccezione nella verifica esistenza/recupero utenti applicativi configurati per la firma HSM : ",
		    e);
	}

	return rispostaControlli;
    }

    /**
     * Restituisce il Ruolo per l'authorized signer associato all'utente passato in ingresso per
     * l'ambiente specificato
     *
     * @param idUserIamCor id user Iam corrente
     * @param idAmbiente   id ambiente
     *
     * @return RispostaControlli con risultato del recupero effettuato
     */
    public RispostaControlli leggiRuoloAuthorizedSigner(long idUserIamCor, BigDecimal idAmbiente) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    AplParamApplic paramApplic = configurationHelper
		    .getParamApplic(CostantiDB.ParametroAppl.AGENT_AUTHORIZED_SIGNER_ROLE);
	    UsrUser user = amministrazioneHelper.findById(UsrUser.class, idUserIamCor);
	    String[] authSignerRole = null;
	    // Ricavo le coppie di valori multipli del parametro AGENT_AUTHORIZED_SIGNER_ROLE
	    // sull'ambiente
	    List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
		    .getAplValParamApplicMultiList(
			    BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
	    MultiValuedMap<String, String[]> mappaAuthSignerRole = new ArrayListValuedHashMap<>();
	    // Per ogni agent gestito dal parametro AGENT_AUTHORIZED_SIGNER_ROLE
	    for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
		// Splitto la coppia: il primo valore sarà lo user applicativo, il secondo il ruolo
		// dell'authorized
		// signer e, se specificato, il valore del relevant document
		String[] chiaveValore = valParamApplicMulti.getDsValoreParamApplic().split(",");
		if (chiaveValore.length > 2) {
		    // MAC#25904
		    mappaAuthSignerRole.put(chiaveValore[0], new String[] {
			    chiaveValore[1], chiaveValore[2] });
		    // end MAC#25904
		} else {
		    mappaAuthSignerRole.put(chiaveValore[0], new String[] {
			    chiaveValore[1] });
		}
	    }
	    // Controllo se l'utente corrente è presente come user applicativo in una delle coppie
	    Collection<Map.Entry<String, String[]>> entriesASR = mappaAuthSignerRole.entries();
	    for (Map.Entry<String, String[]> entry : entriesASR) {
		String chiave = entry.getKey();
		if (chiave.equals(user.getNmUserid())) {
		    authSignerRole = entry.getValue();
		    if (authSignerRole[0].equalsIgnoreCase("PreservationManager")) {
			// Vince il ruolo "PreservationManager"
			break;
		    }
		}
	    }

	    rispostaControlli.setrObject(authSignerRole);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.leggiRuoloAuthorizedSigner: " + e.getMessage()));
	    log.error("Eccezione nel recupero del ruolo per l'authorized signer : ", e);
	}

	return rispostaControlli;
    }
    // end EVO#20972

    // MEV#27831 - Modifica creazione indice AIP in presenza di SIGILLO
    /**
     * Restituisce la lista di agenti legal person per l'ambiente specificato
     *
     * @param idAmbiente id ambiente
     *
     * @return RispostaControlli con risultato del recupero effettuato
     */
    public RispostaControlli leggiAuthorizedSignerLegalPersons(BigDecimal idAmbiente) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    AplParamApplic paramApplic = configurationHelper.getParamApplic(
		    CostantiDB.ParametroAppl.AGENT_AUTHORIZED_SIGNER_ROLE_LEGAL_PERSON);
	    // Ricavo i valori multipli del parametro AGENT_AUTHORIZED_SIGNER_ROLE sull'ambiente
	    List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
		    .getAplValParamApplicMultiList(
			    BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
	    // Per ogni agent gestito dal parametro AGENT_AUTHORIZED_SIGNER_ROLE
	    ArrayList<AgentLegalPersonDto> agenti = new ArrayList<>();
	    if (listaCoppieValoriHsmUsername.isEmpty()) {
		// mette a vero altrimenti esce e poi esplode senza mostrare il motivo
		rispostaControlli.setrBoolean(true);
	    } else {
		// MAC#29103 - Risoluzione problema con parametro
		// "AGENT_AUTHORIZED_SIGNER_ROLE_LEGAL_PERSON"
		// Oltre a testare lista.size() == 0 si testa che il singolo record non contenva
		// valore vuoto
		String parDescrizione = listaCoppieValoriHsmUsername.get(0)
			.getDsValoreParamApplic();
		if (listaCoppieValoriHsmUsername.size() == 1
			&& (parDescrizione == null || parDescrizione.trim().equals(""))) {
		    // mette a vero altrimenti esce e poi esplode senza mostrare il motivo
		    rispostaControlli.setrBoolean(true);
		} else {
		    for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
			// Splitto il parametro: il primo valore sarà lul nome dell'ente, il secondo
			// il ruolo,
			// il terzo il relevant document, il quarto l'agentID
			String[] valori = valParamApplicMulti.getDsValoreParamApplic().split(",");
			if (valori.length != 4) {
			    rispostaControlli.setrBoolean(false);
			    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
			    final String ERRORE_SUI_PARAMETRI = "ControlliRecupero.leggiAuthorizedSignerLegalPersons: Uno dei parametri multipli contiene meno di 4 valori sull'ambiente "
				    + idAmbiente;
			    rispostaControlli.setDsErr(MessaggiWSBundle
				    .getString(MessaggiWSBundle.ERR_666, ERRORE_SUI_PARAMETRI));
			    log.error(ERRORE_SUI_PARAMETRI);
			    break;
			} else {
			    AgentLegalPersonDto dto = new AgentLegalPersonDto();
			    agenti.add(dto);
			    dto.setNome(valori[0]);
			    dto.setRuolo(valori[1]);
			    dto.setDocumentoRilevante(valori[2]);
			    dto.setId(valori[3]);
			    rispostaControlli.setrBoolean(true);
			}
		    }
		}
	    }
	    rispostaControlli.setrObject(agenti);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.leggiAuthorizedSignerLegalPersons: " + e.getMessage()));
	    log.error("Eccezione nel recupero del ruolo per l'authorized signer legal person: ", e);
	}
	return rispostaControlli;
    }
    // End - MEV#27831

    private void loadAroUrnVerIndiceAipUds(AroFileVerIndiceAipUd aroFileVerIndiceAipUd) {
	if (aroFileVerIndiceAipUd.getAroVerIndiceAipUd() != null) {
	    final String PARAM_NAME = "aroVerIndiceAipUd";
	    TypedQuery<AroUrnVerIndiceAipUd> query = entityManager.createQuery(
		    "SELECT t FROM AroUrnVerIndiceAipUd t WHERE t.aroVerIndiceAipUd=:" + PARAM_NAME,
		    AroUrnVerIndiceAipUd.class);
	    query.setParameter(PARAM_NAME, aroFileVerIndiceAipUd.getAroVerIndiceAipUd());
	    aroFileVerIndiceAipUd.getAroVerIndiceAipUd()
		    .setAroUrnVerIndiceAipUds(query.getResultList());
	}
    }

    private void loadAroUrnVerIndiceAipUdsOs(AroVerIndiceAipUd aroVerIndiceAipUd) {
	if (aroVerIndiceAipUd != null) {
	    final String PARAM_NAME = "aroVerIndiceAipUd";
	    TypedQuery<AroUrnVerIndiceAipUd> query = entityManager.createQuery(
		    "SELECT t FROM AroUrnVerIndiceAipUd t WHERE t.aroVerIndiceAipUd=:" + PARAM_NAME,
		    AroUrnVerIndiceAipUd.class);
	    query.setParameter(PARAM_NAME, aroVerIndiceAipUd);
	    aroVerIndiceAipUd.setAroUrnVerIndiceAipUds(query.getResultList());
	}
    }

    public VrsUrnXmlSessioneVers findVrsUrnXmlSessioneVersByTiUrn(
	    VrsXmlDatiSessioneVers vrsXmlDatiSessioneVers,
	    it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers tiUrn) {
	final String vrsXmlDatiSessioneVersParam = "vrsXmlDatiSessioneVers";
	final String tiUrnParam = "tiUrn";
	TypedQuery<VrsUrnXmlSessioneVers> query = entityManager.createQuery(
		"SELECT v from VrsUrnXmlSessioneVers v " + " WHERE v.vrsXmlDatiSessioneVers=:"
			+ vrsXmlDatiSessioneVersParam + " AND v.tiUrn=:" + tiUrnParam,
		VrsUrnXmlSessioneVers.class);
	query.setParameter(vrsXmlDatiSessioneVersParam, vrsXmlDatiSessioneVers);
	query.setParameter(tiUrnParam, tiUrn);
	query.setMaxResults(1);
	query.setFirstResult(0);
	return query.getSingleResult();
    }

    public Constants.TipoSessione getTipoSessioneFrom(
	    VrsXmlDatiSessioneVers vrsXmlDatiSessioneVers) {
	final String param1 = "vrsXmlDatiSessioneVers";
	TypedQuery<String> query = entityManager.createQuery(
		"SELECT v.vrsSessioneVers.tiSessioneVers FROM "
			+ "VrsDatiSessioneVers v WHERE v.idDatiSessioneVers = :" + param1,
		String.class);
	query.setParameter(param1,
		vrsXmlDatiSessioneVers.getVrsDatiSessioneVers().getIdDatiSessioneVers());
	return Constants.TipoSessione.valueOf(query.getSingleResult());
    }

    public void loadElvElencoVer(ElvFileElencoVer fileElencoVers) {
	final String idParam = "idElencoVers";
	TypedQuery<ElvElencoVer> query = entityManager
		.createQuery("SELECT f FROM ElvElencoVer f JOIN FETCH f.orgStrut s "
			+ "JOIN FETCH s.orgEnte e JOIN FETCH e.orgAmbiente a WHERE f.idElencoVers=:"
			+ idParam, ElvElencoVer.class);
	query.setParameter(idParam, fileElencoVers.getElvElencoVer().getIdElencoVers());
	fileElencoVers.setElvElencoVer(query.getSingleResult());
    }

    public List<VolFileVolumeConserv> findVolFileVolumeConserv(VolVolumeConserv volume) {
	final String paramVolume = "volVolumeConserv";
	TypedQuery<VolFileVolumeConserv> query = entityManager
		.createQuery("SELECT v FROM VolFileVolumeConserv v " + "WHERE v.volVolumeConserv=:"
			+ paramVolume, VolFileVolumeConserv.class);
	query.setParameter(paramVolume, volume);
	return query.getResultList();
    }

    /**
     * Ottieni la lista degli id_sessione_vers distinti partendo dalla tabella
     * VRS_XML_DATI_SESSIONE_VERS.
     *
     * Questo filtro non viene effettuato direttamente all'interno dello stream perchè non è
     * possibile, con hibernate, accedere ai valori non chiave per le entity con lazy loading (vedi
     * https://parermine.regione.emilia-romagna.it/projects/parer/wiki/Eclipselink2Hibernate#Lazy-loading
     * )
     *
     * @param vrsXmlDatiSessioneVers lista di entità VrsXmlDatiSessioneVers
     *
     * @return lista di id sessione vers (distinti)
     */
    public RispostaControlli findAllSessVersByXmlDatiSessVers(
	    List<VrsXmlDatiSessioneVers> vrsXmlDatiSessioneVers) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	//
	try {
	    TypedQuery<VrsSessioneVers> query = entityManager
		    .createQuery("Select distinct vrs from VrsXmlDatiSessioneVers xml_dati "
			    + "join xml_dati.vrsDatiSessioneVers dati join dati.vrsSessioneVers vrs "
			    + "WHERE xml_dati in (:vrsXmlDatiSessioneVers)", VrsSessioneVers.class);
	    query.setParameter("vrsXmlDatiSessioneVers", vrsXmlDatiSessioneVers);
	    List<VrsSessioneVers> result = query.getResultList();
	    rispostaControlli.setrObject(result);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception ex) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.findSessVersByXmlDatiSessVers "
			    + ExceptionUtils.getRootCauseMessage(ex)));
	    log.error("Eccezione nella verifica esistenza sessione versamento da recuperare ", ex);
	}

	return rispostaControlli;
    }

    /**
     * Ottieni l'identificativo della sessione di versamento a partire dall'identificativo delle
     * VRS_XML_DATI_SESSIONE_VERS.
     *
     * @param vrsXmlDatiSessioneVers id VrsXmlDatiSessioneVers
     *
     * @return id sessione vers
     */
    public RispostaControlli findIdSessVersByXmlDatiSessVers(
	    VrsXmlDatiSessioneVers vrsXmlDatiSessioneVers) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	//
	try {
	    TypedQuery<Long> query = entityManager
		    .createQuery("Select vrs.idSessioneVers from VrsXmlDatiSessioneVers xml_dati "
			    + "join xml_dati.vrsDatiSessioneVers dati join dati.vrsSessioneVers vrs "
			    + "WHERE xml_dati = :vrsXmlDatiSessioneVers", Long.class);
	    query.setParameter("vrsXmlDatiSessioneVers", vrsXmlDatiSessioneVers);
	    Long result = query.getSingleResult();
	    rispostaControlli.setrBoolean(true);
	    rispostaControlli.setrLong(result);
	} catch (Exception ex) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.findSessVersByXmlDatiSessVers "
			    + ExceptionUtils.getRootCauseMessage(ex)));
	    log.error("Eccezione nella verifica esistenza sessione versamento da recuperare ", ex);
	}

	return rispostaControlli;
    }

    /**
     * Ottieni la lista degli id_upd_unita_doc distinti partendo dalla tabella
     * ARO_XML_UPD_UNITA_DOC.
     *
     * Questo filtro non viene effettuato direttamente all'interno dello stream perchè non è
     * possibile, con hibernate, accedere ai valori non chiave per le entity con lazy loading (vedi
     * https://parermine.regione.emilia-romagna.it/projects/parer/wiki/Eclipselink2Hibernate#Lazy-loading
     * )
     *
     * @param aroXmlUpdUnitaDocs lista di entità AroXmlUpdUnitaDoc
     *
     * @return lista di id upd unita doc (distinti)
     */
    public RispostaControlli findAllUpdUnitaDocByXmlUpdUnitaDoc(
	    List<AroXmlUpdUnitaDoc> aroXmlUpdUnitaDocs) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	//
	try {
	    TypedQuery<AroUpdUnitaDoc> query = entityManager
		    .createQuery(
			    "Select distinct upd_ud from AroXmlUpdUnitaDoc xml_upd_ud "
				    + "join xml_upd_ud.aroUpdUnitaDoc upd_ud "
				    + "WHERE xml_upd_ud in (:aroXmlUpdUnitaDocs)",
			    AroUpdUnitaDoc.class);
	    query.setParameter("aroXmlUpdUnitaDocs", aroXmlUpdUnitaDocs);
	    List<AroUpdUnitaDoc> result = query.getResultList();
	    rispostaControlli.setrObject(result);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception ex) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.findAllUpdUnitaDocByXmlUpdUnitaDoc "
			    + ExceptionUtils.getRootCauseMessage(ex)));
	    log.error(
		    "Eccezione nella verifica esistenza sessione versamento aggiornamento metadati da recuperare ",
		    ex);
	}

	return rispostaControlli;
    }

    /**
     * Ottieni l'identificativo della sessione di versamento aggiornamento metadati a partire
     * dall'identificativo delle ARO_XML_UPD_UNITA_DOC
     *
     * @param aroXmlUpdUnitaDoc id AroXmlUpdUnitaDoc
     *
     * @return id upd unita doc
     */
    public RispostaControlli findIdUpdUnitaDocByXmlUpdUnitaDoc(
	    AroXmlUpdUnitaDoc aroXmlUpdUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	//
	try {
	    TypedQuery<Long> query = entityManager
		    .createQuery("Select upd_ud.idUpdUnitaDoc from AroXmlUpdUnitaDoc xml_upd_ud "
			    + "join xml_upd_ud.aroUpdUnitaDoc upd_ud "
			    + "WHERE xml_upd_ud = :aroXmlUpdUnitaDoc", Long.class);
	    query.setParameter("aroXmlUpdUnitaDoc", aroXmlUpdUnitaDoc);
	    Long result = query.getSingleResult();
	    rispostaControlli.setrBoolean(true);
	    rispostaControlli.setrLong(result);
	} catch (Exception ex) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.findIdUpdUnitaDocByXmlUpdUnitaDoc "
			    + ExceptionUtils.getRootCauseMessage(ex)));
	    log.error(
		    "Eccezione nella verifica esistenza sessione versamento aggiornamento metadati da recuperare ",
		    ex);
	}

	return rispostaControlli;
    }

    public RispostaControlli leggiLogStatoConservazione(long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroLogStatoConservUd> aroLog = null;

	try {
	    String queryStr = "select t from AroLogStatoConservUd t "
		    + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDoc order by t.dtStato desc";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idUnitaDoc", idUnitaDoc);
	    aroLog = query.getResultList();
	    rispostaControlli.setrObject(aroLog);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVolumeConserv " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei volumi di conservazione ", e);
	}
	return rispostaControlli;
    }

}
