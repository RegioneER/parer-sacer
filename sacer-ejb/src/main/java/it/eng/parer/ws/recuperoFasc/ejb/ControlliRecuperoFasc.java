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

package it.eng.parer.ws.recuperoFasc.ejb;

import static it.eng.parer.ws.recupero.ejb.ControlliRecupero.ERROR_CONTROLLI_LEGGI_XML;
import static it.eng.parer.ws.recupero.ejb.ControlliRecupero.ERROR_XML_VERSAMENTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasXmlFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recuperoFasc.dto.ContenutoRec;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliRecuperoFasc")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRecuperoFasc {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecuperoFasc.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private ObjectStorageService objectStorageService;

    @Resource
    EJBContext context;

    public RispostaControlli leggiFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	FasFascicolo tmpFasc = null;

	try {
	    tmpFasc = entityManager.find(FasFascicolo.class, idFascicolo);
	    rispostaControlli.setrObject(tmpFasc);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiFasc " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei fascicoli ", e);
	}
	return rispostaControlli;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli leggiContVerAipUdInFASCAIPV2(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroVerIndiceAipUd> lstVerAipUd = null;
	List<ContenutoRec> lstConten;

	try {
	    lstConten = new ArrayList<>();

	    String queryStr = "select v from FasContenVerAipFascicolo cont JOIN cont.aroVerIndiceAipUd v "
		    + "WHERE cont.fasVerAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
		    + "AND v.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
		    + "AND v.pgVerIndiceAip = (select MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d "
		    + "WHERE d.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = cont.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc)";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    lstVerAipUd = query.getResultList();
	    for (AroVerIndiceAipUd tmpVerAipUd : lstVerAipUd) {
		// urn
		String urnCompletoIniz = null;
		String urnCompleto;
		if (tmpVerAipUd.getAroUrnVerIndiceAipUds() != null
			&& !tmpVerAipUd.getAroUrnVerIndiceAipUds().isEmpty()) {
		    // Recupero lo urn NORMALIZZATO
		    AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
			    .find(tmpVerAipUd.getAroUrnVerIndiceAipUds(), new Predicate() {
				@Override
				public boolean evaluate(final Object object) {
				    return ((AroUrnVerIndiceAipUd) object).getTiUrn()
					    .equals(TiUrnVerIxAipUd.NORMALIZZATO);
				}
			    });
		    urnCompleto = urnVerIndiceAipUd.getDsUrn();
		} else {
		    urnCompleto = tmpVerAipUd.getDsUrn();
		}

		// urn iniz
		if (tmpVerAipUd.getAroUrnVerIndiceAipUds() != null
			&& !tmpVerAipUd.getAroUrnVerIndiceAipUds().isEmpty()) {
		    // Recupero lo urn INIZIALE
		    AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
			    .find(tmpVerAipUd.getAroUrnVerIndiceAipUds(), new Predicate() {
				@Override
				public boolean evaluate(final Object object) {
				    return ((AroUrnVerIndiceAipUd) object).getTiUrn()
					    .equals(TiUrnVerIxAipUd.INIZIALE);
				}
			    });
		    if (urnVerIndiceAipUd != null) {
			urnCompletoIniz = urnVerIndiceAipUd.getDsUrn();
		    }
		}

		ContenutoRec tmpCRec = new ContenutoRec(urnCompleto, urnCompletoIniz);
		// MEV#22921 Parametrizzazione servizi di recupero
		// tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
		// tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
		tmpCRec.setIdVerIndiceAipUd(tmpVerAipUd.getIdVerIndiceAip());
		// Gestisco il formato
		tmpCRec.setEstensioneFile(IOUtils.CONTENT_TYPE.ZIP.getFileExt());
		lstConten.add(tmpCRec);
	    }
	    // if (lstConten.size() > 0) {
	    // rispostaControlli.setrString(this.generaNomeFileAIPFasc(lstVerAipUd.get(0)
	    // .getFasContenVerAipFascicolos().get(0).getFasVerAipFascicolo().getFasFascicolo()));
	    // } else {
	    rispostaControlli.setrString(this.generaNomeFileAIPFasc(idFascicolo));
	    // }
	    rispostaControlli.setrObject(lstConten);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiContVerAipUdInFASCAIPV2 " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella delle versioni indici aip in FASC ",
		    e);
	}
	return rispostaControlli;
    }

    private String generaNomeFileAIPFasc(long idFasc) {
	FasFascicolo ff = entityManager.find(FasFascicolo.class, idFasc);
	return this.generaNomeFileAIPFasc(ff);
    }

    private String generaNomeFileAIPFasc(FasFascicolo ff) {
	String sistemaConservazione = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	CSVersatore versatore = this.getVersatoreFasc(ff, sistemaConservazione);
	CSChiaveFasc chiave = this.getChiaveFasc(ff);
	String tmpUrn = MessaggiWSFormat
		.formattaUrnAipFascicolo(MessaggiWSFormat.formattaBaseUrnFascicolo(
			MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
				Costanti.UrnFormatter.VERS_FMT_STRING),
			MessaggiWSFormat.formattaUrnPartFasc(chiave, true,
				Costanti.UrnFormatter.FASC_FMT_STRING)));
	return ContenutoRec.estraiNomeFileCompleto(tmpUrn);
    }

    public CSChiaveFasc getChiaveFasc(FasFascicolo ff) {
	CSChiaveFasc cscf = new CSChiaveFasc();
	cscf.setAnno(ff.getAaFascicolo().intValue());
	cscf.setNumero(ff.getCdKeyFascicolo());

	return cscf;
    }

    private CSVersatore getVersatoreFasc(FasFascicolo ff, String sistemaConservazione) {
	CSVersatore csv = new CSVersatore();
	csv.setStruttura(ff.getOrgStrut().getNmStrut());
	csv.setEnte(ff.getOrgStrut().getOrgEnte().getNmEnte());
	csv.setAmbiente(ff.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	// sistema (new URN)
	csv.setSistemaConservazione(sistemaConservazione);

	return csv;
    }

    // OK - caricaparametri
    public RispostaControlli leggiChiaveFascicolo(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	FasFascicolo tmpFasc = null;
	CSChiaveFasc chiaveFasc;

	try {
	    tmpFasc = entityManager.find(FasFascicolo.class, idFascicolo);
	    chiaveFasc = new CSChiaveFasc();
	    chiaveFasc.setAnno(tmpFasc.getAaFascicolo().intValue());
	    chiaveFasc.setNumero(tmpFasc.getCdKeyFascicolo());
	    rispostaControlli.setrObject(chiaveFasc);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiChiaveFascicolo " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei fascicoli ", e);
	}
	return rispostaControlli;
    }

    // OK - caricaparametri
    public RispostaControlli leggiVersatoreFascicolo(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	FasFascicolo tmpFasc = null;
	CSVersatore versatore;

	try {
	    tmpFasc = entityManager.find(FasFascicolo.class, idFascicolo);
	    versatore = new CSVersatore();
	    versatore.setStruttura(tmpFasc.getOrgStrut().getNmStrut());
	    versatore.setEnte(tmpFasc.getOrgStrut().getOrgEnte().getNmEnte());
	    versatore.setAmbiente(
		    tmpFasc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	    rispostaControlli.setrObject(versatore);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiVersatoreFascicolo " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei fascicoli ", e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<FasXmlVersFascicolo> lstXmlVersFascicolo = null;

	try {
	    String queryStr = "select xml from FasXmlVersFascicolo xml "
		    + "where xml.fasFascicolo.idFascicolo  = :idFascicolo "
		    + "order by xml.dtVersFascicolo ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    lstXmlVersFascicolo = query.getResultList();
	    rispostaControlli.setrObject(lstXmlVersFascicolo);
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

    public RispostaControlli leggiXMLProfiloFascList(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<FasXmlFascicolo> lstXmlFascicolo = null;

	try {
	    String queryStr = "select xml from FasXmlFascicolo xml "
		    + "join fetch xml.decModelloXsdFascicolo fasc "
		    + "where xml.fasFascicolo.idFascicolo  = :idFascicolo "
		    + "order by xml.dtVersFascicolo ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    lstXmlFascicolo = query.getResultList();
	    rispostaControlli.setrObject(lstXmlFascicolo);
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

    public RispostaControlli contaXMLIndiceAIPFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	try {
	    String queryStr = "select count(fas) from FasFileMetaVerAipFasc fas "
		    + "WHERE fas.fasMetaVerAipFascicolo.fasVerAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
		    + "and fas.fasMetaVerAipFascicolo.tiFormatoMeta = 'UNISYNCRO' ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    rispostaControlli.setrBoolean(true);
	    rispostaControlli.setrLong((Long) query.getSingleResult());
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecuperoFasc.contaXMLIndiceAIPFasc " + e.getMessage()));
	    log.error(
		    "Eccezione nella lettura della tabella dei file di versione indice AIP fascicolo ",
		    e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiContenVerAipFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<AroVerIndiceAipUd> lstAroVerIndiceAipUd = null;

	try {
	    String queryStr = "select v from FasContenVerAipFascicolo cont JOIN cont.aroVerIndiceAipUd v "
		    + "WHERE cont.fasVerAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
		    + "AND v.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
		    + "AND v.pgVerIndiceAip = (select MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d "
		    + "WHERE d.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = cont.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc)";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    lstAroVerIndiceAipUd = query.getResultList();
	    rispostaControlli.setrObject(lstAroVerIndiceAipUd);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecuperoFasc.leggiContenVerAipFasc " + e.getMessage()));
	    log.error(
		    "Eccezione nella lettura della tabella dei file di versione indice AIP fascicolo ",
		    e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIPFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<FasFileMetaVerAipFasc> listaFasFile = null;

	try {
	    String queryStr = "SELECT fas FROM FasFileMetaVerAipFasc fas "
		    + "JOIN FETCH fas.fasMetaVerAipFascicolo meta "
		    + "JOIN FETCH meta.fasVerAipFascicolo ver "
		    + "WHERE ver.fasFascicolo.idFascicolo = :idFascicolo "
		    + "AND meta.tiFormatoMeta = 'UNISYNCRO' " + "AND meta.tiMeta = 'INDICE' "
		    + "ORDER BY ver.pgVerAipFascicolo DESC";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    listaFasFile = query.getResultList();

	    rispostaControlli.setrObject(listaFasFile);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLIndiceAIPFasc " + e.getMessage()));
	    log.error(
		    "Eccezione nella lettura della tabella dei file di versione indice AIP fascicolo ",
		    e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiElvFileElencoVersFasc(long idFascicolo,
	    String tiFileElencoVersFasc, String tiFileElencoVersFasc2) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	ElvFileElencoVersFasc fileElencoVersFasc = null;

	try {
	    // MAC#25864
	    String queryStr = "SELECT fileElencoVersFasc FROM FasMetaVerAipFascicolo metaVerAipFasc, ElvFileElencoVersFasc fileElencoVersFasc "
		    + "WHERE metaVerAipFasc.fasVerAipFascicolo.pgVerAipFascicolo = (SELECT MAX(d.pgVerAipFascicolo) FROM FasVerAipFascicolo d WHERE d.idVerAipFascicolo = metaVerAipFasc.fasVerAipFascicolo.idVerAipFascicolo) "
		    + "AND fileElencoVersFasc.elvElencoVersFasc = metaVerAipFasc.fasVerAipFascicolo.elvElencoVersFasc "
		    + "AND metaVerAipFasc.fasVerAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
		    + "AND metaVerAipFasc.tiFormatoMeta = 'UNISYNCRO' "
		    + "AND fileElencoVersFasc.tiFileElencoVers = :tiFileElencoVersFasc ";
	    // end MAC#25864

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);
	    query.setParameter("tiFileElencoVersFasc", tiFileElencoVersFasc);

	    List<ElvFileElencoVersFasc> listaFileElencoVersFasc = query.getResultList();
	    if (!listaFileElencoVersFasc.isEmpty()) {
		// Se sto considerando la firma
		if (tiFileElencoVersFasc
			.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())) {
		    String queryStr2 = "SELECT fileElencoVersFasc FROM FasFascicolo fasc "
			    + "JOIN fasc.elvElencoVersFasc elencoVersFasc "
			    + "JOIN elencoVersFasc.elvFileElencoVersFasc fileElencoVersFasc "
			    + "WHERE fasc.idFascicolo = :idFascicolo "
			    + "AND fileElencoVersFasc.tiFileElencoVers = :tiFileElencoVersFasc ";

		    javax.persistence.Query query2 = entityManager.createQuery(queryStr2);
		    query2.setParameter("idFascicolo", idFascicolo);
		    query2.setParameter("tiFileElencoVersFasc", tiFileElencoVersFasc2);

		    List<ElvFileElencoVersFasc> listaFileElencoVersFasc2 = query.getResultList();
		    if (!listaFileElencoVersFasc2.isEmpty()) {
			fileElencoVersFasc = listaFileElencoVersFasc2.get(0);
		    }
		} // altrimenti è il caso della marca
		  // else if (tiFileElencoVers.equals(
		  // ElencoEnums.OpTypeEnum.MARCA_FIRMA_ELENCO_INDICI_AIP
		  // .name())) {
		  // fileElencoVers = listaFileElencoVers.get(0);
		  // }
	    }
	    rispostaControlli.setrObject(fileElencoVersFasc);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiElvFileElencoVersFasc " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei file elenco indici AIP fascicoli ",
		    e);
	}
	return rispostaControlli;
    }

    public void loadElvElencoVersFasc(ElvFileElencoVersFasc fileElencoVersFasc) {
	final String idParam = "idElencoVersFasc";
	TypedQuery<ElvElencoVersFasc> query = entityManager
		.createQuery("SELECT f FROM ElvElencoVersFasc f JOIN FETCH f.orgStrut s "
			+ "JOIN FETCH s.orgEnte e JOIN FETCH e.orgAmbiente a WHERE f.idElencoVersFasc = :"
			+ idParam, ElvElencoVersFasc.class);
	query.setParameter(idParam,
		fileElencoVersFasc.getElvElencoVersFasc().getIdElencoVersFasc());
	fileElencoVersFasc.setElvElencoVersFasc(query.getSingleResult());
    }

    // da RecuperoFascPrsr
    public RispostaControlli checkTipoFascicoloperStrut(long idStrut, String nmTipoFasc,
	    String descChiaveFasc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<DecTipoFascicolo> lstTipoFascs;

	try {
	    String queryStr = "select t from DecTipoFascicolo t "
		    + "where t.orgStrut.idStrut = :idStrutIn "
		    + "and t.nmTipoFascicolo = :nmTipoFascIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idStrutIn", idStrut);
	    query.setParameter("nmTipoFascIn", nmTipoFasc);

	    lstTipoFascs = query.getResultList();
	    if (lstTipoFascs.size() == 1) {
		rispostaControlli.setrLong(lstTipoFascs.get(0).getIdTipoFascicolo());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_001);
		rispostaControlli.setDsErr(MessaggiWSBundle
			.getString(MessaggiWSBundle.DOC_001_001, descChiaveFasc, nmTipoFasc)
			.replaceFirst("Documento", "Fascicolo"));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecuperoFasc.checkTipoFascicoloperStrut "
			    + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza del tipo fascicolo per la struttura ", e);
	}
	return rispostaControlli;
    }

    // da RecuperoFascPrsr
    public RispostaControlli checkTipoFascicoloinFASC(long idFascicolo, String nmTipoFasc,
	    String descChiaveFasc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<FasFascicolo> lstFasFascicolos;

	try {
	    String queryStr = "select t from FasFascicolo t "
		    + "where t.idFascicolo = :idFascicoloIn "
		    + "and t.decTipoFascicolo.nmTipoFascicolo = :nmTipoFascIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicoloIn", idFascicolo);
	    query.setParameter("nmTipoFascIn", nmTipoFasc);

	    lstFasFascicolos = query.getResultList();
	    if (!lstFasFascicolos.isEmpty()) {
		rispostaControlli.setrLong(
			lstFasFascicolos.get(0).getDecTipoFascicolo().getIdTipoFascicolo());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_004);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.DOC_001_004,
			nmTipoFasc, descChiaveFasc));
	    }
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecuperoFasc.checkTipoFascicoloinFASC " + e.getMessage()));
	    log.error("Eccezione nella verifica esistenza del tipo fascicolo nella Fascicolo ", e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiXMLMetadatiFasc(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<FasFileMetaVerAipFasc> listaFasFile = null;

	try {
	    String queryStr = "SELECT fas FROM FasFileMetaVerAipFasc fas "
		    + "JOIN FETCH fas.fasMetaVerAipFascicolo meta "
		    + "JOIN FETCH meta.fasVerAipFascicolo ver "
		    + "WHERE ver.fasFascicolo.idFascicolo = :idFascicolo "
		    + "AND meta.tiMeta = 'FASCICOLO' " + "ORDER BY ver.pgVerAipFascicolo DESC ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("idFascicolo", idFascicolo);

	    listaFasFile = query.getResultList();
	    rispostaControlli.setrObject(listaFasFile);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setrBoolean(false);
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliRecupero.leggiXMLMetadatiFasc " + e.getMessage()));
	    log.error(
		    "Eccezione nella lettura della tabella dei file di versione metadati fascicolo ",
		    e);
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
}
