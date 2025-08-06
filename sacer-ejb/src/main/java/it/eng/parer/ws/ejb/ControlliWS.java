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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.ejb;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.IamAbilTipoDato;
import it.eng.parer.entity.IamUser;
import it.eng.parer.idpjaas.logutils.LogDto;
import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.CostantiDB.TipoParametroAppl;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.VerificaVersione;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.WSLoginHandler;
import it.eng.spagoLite.security.exception.AuthWSException;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliWS")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliWS {

    private static final String ECCEZIONE_AUTENTICAZIONE = "Eccezione nella fase di autenticazione del EJB ";
    @EJB
    WsIdpLogger idpLogger;

    @EJB
    ControlliSemantici controlliSemantici;

    private static final Logger log = LoggerFactory.getLogger(ControlliWS.class);
    //
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public RispostaControlli checkVersione(String versione, String versioniWsKey,
	    Map<String, String> mapWsVersion, TipiWSPerControlli tipows) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	if (versione == null || versione.isEmpty()) {
	    switch (tipows) {
	    case VERSAMENTO_RECUPERO:
		// EVO#13993
	    case VERSAMENTO_RECUPERO_FASC:
		// end EVO#13993
	    case ANNULLAMENTO:
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_010);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_010));
		break;
	    default:
		break;
	    }

	    return rispostaControlli;
	}

	List<String> versioniWs = VerificaVersione.getWsVersionList(versioniWsKey, mapWsVersion);
	if (versioniWs.isEmpty()) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.UD_018_001);
	    rispostaControlli.setDsErr(
		    MessaggiWSBundle.getString(MessaggiWSBundle.UD_018_001, versioniWsKey));
	    return rispostaControlli;
	}

	for (String tmpString : versioniWs) {
	    if (versione.equals(tmpString)) {
		rispostaControlli.setrBoolean(true);
	    }
	}

	if (!rispostaControlli.isrBoolean()) {
	    switch (tipows) {
	    case VERSAMENTO_RECUPERO:
		// EVO#13993
	    case VERSAMENTO_RECUPERO_FASC:
		// end EVO#13993
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_011);
		rispostaControlli.setDsErr(
			MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_011, versione));
		break;
	    case ANNULLAMENTO:
		rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_003);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			MessaggiWSBundle.RICH_ANN_VERS_003, StringUtils.join(versioniWs, ",")));
		break;
	    default:
		break;
	    }
	}

	return rispostaControlli;
    }

    public RispostaControlli checkCredenziali(String loginName, String password, String indirizzoIP,
	    TipiWSPerControlli tipows) {
	return checkCredenziali(loginName, password, indirizzoIP, tipows, null, false);
    }

    public RispostaControlli checkCredenziali(String loginName, String password, String indirizzoIP,
	    TipiWSPerControlli tipows, boolean isOAuth2) {
	return checkCredenziali(loginName, password, indirizzoIP, tipows, null, isOAuth2);
    }

    public RispostaControlli checkCredenziali(String loginName, String password, String indirizzoIP,
	    TipiWSPerControlli tipows, String certCommonName) {
	return checkCredenziali(loginName, password, indirizzoIP, tipows, certCommonName, false);
    }

    public RispostaControlli checkCredenziali(String loginName, String password, String indirizzoIP,
	    TipiWSPerControlli tipows, String certCommonName, boolean isOAuth2) {
	User utente = null;
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	log.info("Indirizzo IP del chiamante - access: ws - IP: {}", indirizzoIP);

	if ((loginName == null || loginName.isEmpty())
		&& (certCommonName == null || certCommonName.isEmpty())) {
	    switch (tipows) {
	    case VERSAMENTO_RECUPERO:
		// EVO#13993
	    case VERSAMENTO_RECUPERO_FASC:
		// end EVO#13993
	    case ANNULLAMENTO:
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_004);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_004));
		break;
	    default:
		break;
	    }
	    return rispostaControlli;
	}

	// preparazione del log del login
	LogDto tmpLogDto = new LogDto();
	tmpLogDto.setNmAttore("Sacer WS");
	/* logga il login name oppure il CM del certificato se passato */
	if (certCommonName != null && !certCommonName.isEmpty()) {
	    tmpLogDto.setNmUser(certCommonName);
	} else {
	    tmpLogDto.setNmUser(loginName);
	}
	tmpLogDto.setCdIndIpClient(indirizzoIP);
	tmpLogDto.setTsEvento(new Date());
	// nota, non imposto l'indirizzo del server, verrà letto dal singleton da WsIdpLogger
	// Determina il loginName finale che sarà il common name oppure il normale loginName
	String loginNameFinale = (certCommonName != null && !certCommonName.isEmpty())
		? certCommonName
		: loginName;
	try {
	    if (certCommonName != null && !certCommonName.isEmpty()) {
		WSLoginHandler.login(certCommonName, entityManager);
	    } else {
		WSLoginHandler.login(loginName, password, indirizzoIP, entityManager, isOAuth2);
	    }
	    // se l'autenticazione riesce, non va in eccezione.
	    // passo quindi a leggere i dati dell'utente dal db
	    IamUser iamUser;
	    String queryStr = "select iu from IamUser iu where iu.nmUserid = :nmUseridIn";
	    Query query = entityManager.createQuery(queryStr, IamUser.class);
	    /* La query viene fatta per login name oppure se presente per Common Name! */
	    query.setParameter("nmUseridIn", loginNameFinale);
	    iamUser = (IamUser) query.getSingleResult();
	    //
	    utente = new User();
	    utente.setUsername(loginNameFinale);
	    utente.setIdUtente(iamUser.getIdUserIam());
	    // log della corretta autenticazione
	    tmpLogDto.setTipoEvento(LogDto.TipiEvento.LOGIN_OK);
	    tmpLogDto.setDsEvento("WS, login OK");
	    //
	    rispostaControlli.setrObject(utente);
	    rispostaControlli.setrBoolean(true);
	} catch (AuthWSException e) {
	    log.warn(
		    "ERRORE DI AUTENTICAZIONE WS. Applicazione: SACER Utente: {} Tipo errore: {} Indirizzo IP: {} Descrizione: {}",
		    loginNameFinale, e.getCodiceErrore().name(), indirizzoIP,
		    e.getDescrizioneErrore());
	    switch (tipows) {
	    case VERSAMENTO_RECUPERO:
		// EVO#13993
	    case VERSAMENTO_RECUPERO_FASC:
		// end EVO#13993
		if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_SCADUTO)) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_006);
		    rispostaControlli.setDsErr(MessaggiWSBundle
			    .getString(MessaggiWSBundle.UD_001_006, loginNameFinale));
		} else if (e.getCodiceErrore()
			.equals(AuthWSException.CodiceErrore.UTENTE_NON_ATTIVO)) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_007);
		    rispostaControlli.setDsErr(MessaggiWSBundle
			    .getString(MessaggiWSBundle.UD_001_007, loginNameFinale));
		} else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.LOGIN_FALLITO)) {
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_012);
		    rispostaControlli.setDsErr(MessaggiWSBundle
			    .getString(MessaggiWSBundle.UD_001_012, e.getDescrizioneErrore()));
		}
		break;
	    case ANNULLAMENTO:
		rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_001);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_001));
		break;
	    default:
		break;
	    }
	    //
	    // log dell'errore di autenticazione; ripeto la sequenza di if per chiarezza.
	    // Per altro nel caso sia stato invocato il ws di annullamento, la distnizione
	    // del tipo di errore non l'ho ancora eseguita.
	    //
	    if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_SCADUTO)) {
		tmpLogDto.setTipoEvento(LogDto.TipiEvento.EXPIRED);
		tmpLogDto.setDsEvento("WS, " + e.getDescrizioneErrore());
	    } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.UTENTE_NON_ATTIVO)) {
		tmpLogDto.setTipoEvento(LogDto.TipiEvento.LOCKED);
		tmpLogDto.setDsEvento("WS, " + e.getDescrizioneErrore());
	    } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.LOGIN_FALLITO)
		    && StringUtils.isNotBlank(password)) {
		// se l'autenticazione fallisce, devo capire se è stato sbagliata la password oppure
		// non esiste l'utente. Provo a caricarlo e verifico la cosa.
		String queryStr = "select count(iu) from IamUser iu where iu.nmUserid = :nmUseridIn";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("nmUseridIn", loginNameFinale);
		long tmpNumUtenti = (Long) query.getSingleResult();
		if (tmpNumUtenti > 0) {
		    tmpLogDto.setTipoEvento(LogDto.TipiEvento.BAD_PASS);
		    tmpLogDto.setDsEvento("WS, bad password");
		} else {
		    tmpLogDto.setTipoEvento(LogDto.TipiEvento.BAD_USER);
		    tmpLogDto.setDsEvento("WS, utente sconosciuto");
		}
	    } else if (e.getCodiceErrore().equals(AuthWSException.CodiceErrore.LOGIN_FALLITO)
		    && StringUtils.isBlank(password)) {
		tmpLogDto.setTipoEvento(LogDto.TipiEvento.BAD_PASS);
		tmpLogDto.setDsEvento("WS, null password");
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ECCEZIONE_AUTENTICAZIONE + e.getMessage()));
	    log.error(ECCEZIONE_AUTENTICAZIONE, e);
	}

	// scrittura log
	idpLogger.scriviLog(tmpLogDto, password);
	//
	return rispostaControlli;
    }

    // MAC#29602
    public RispostaControlli checkTipoDatoUdIamUserOrganizzazione(String descKey, User utente,
	    Long idUnitaDoc, String classeTipoDato) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	AroUnitaDoc unitaDoc = null;
	List<IamAbilTipoDato> iamAbilTipoDatos = null;

	try {
	    unitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
	    String queryStr = "select t from IamAbilTipoDato t "
		    + "where t.iamAbilOrganiz.iamUser.idUserIam = :idUserIamIn "
		    + "and t.iamAbilOrganiz.idOrganizApplic = :idOrganizApplicIn "
		    + "and t.idTipoDatoApplic = :idTipoDatoApplicIn "
		    + "and t.nmClasseTipoDato = :nmClasseTipoDatoIn ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr,
		    IamAbilTipoDato.class);
	    query.setParameter("idUserIamIn", utente.getIdUtente());
	    query.setParameter("idOrganizApplicIn", utente.getIdOrganizzazioneFoglia());
	    switch (classeTipoDato) {
	    case "REGISTRO":
		query.setParameter("idTipoDatoApplicIn",
			new BigDecimal(unitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()));
		break;
	    case "TIPO_UNITA_DOC":
		query.setParameter("idTipoDatoApplicIn",
			new BigDecimal(unitaDoc.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
		break;
	    case "SUB_STRUTTURA":
		query.setParameter("idTipoDatoApplicIn",
			new BigDecimal(unitaDoc.getOrgSubStrut().getIdSubStrut()));
		break;
	    default:
		break;
	    }
	    query.setParameter("nmClasseTipoDatoIn", classeTipoDato);
	    iamAbilTipoDatos = query.getResultList();

	    // ottengo un risultato -> abilitato al tipo dato
	    if (iamAbilTipoDatos.size() == 1) {
		rispostaControlli.setrLong(iamAbilTipoDatos.get(0).getIdAbilTipoDato());
		rispostaControlli.setrBoolean(true);
	    } else {
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_019_001);
		switch (classeTipoDato) {
		case "REGISTRO":
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.UD_019_001, descKey, String.format("'REGISTRO': %s",
				    unitaDoc.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc())));
		    break;
		case "TIPO_UNITA_DOC":
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_019_001,
				    descKey, String.format("'TIPO_UNITA_DOC': %s",
					    unitaDoc.getDecTipoUnitaDoc().getNmTipoUnitaDoc())));
		    break;
		case "SUB_STRUTTURA":
		    rispostaControlli
			    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_019_001,
				    descKey, String.format("'SUB_STRUTTURA': %s",
					    unitaDoc.getOrgSubStrut().getNmSubStrut())));
		    break;
		default:
		    break;
		}

	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.checkTipoDatoUdIamUserOrganizzazione: " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica ", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli checkTipoDatoDocsIamUserOrganizzazioneByUd(String descKey, User utente,
	    Long idUnitaDoc) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();

	AroUnitaDoc unitaDoc = null;
	List<IamAbilTipoDato> iamAbilTipoDatos = null;

	try {
	    unitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);

	    for (AroDoc doc : unitaDoc.getAroDocs()) {
		String queryStr = "select t from IamAbilTipoDato t "
			+ "where t.iamAbilOrganiz.iamUser.idUserIam = :idUserIamIn "
			+ "and t.iamAbilOrganiz.idOrganizApplic = :idOrganizApplicIn "
			+ "and t.idTipoDatoApplic = :idTipoDatoApplicIn "
			+ "and t.nmClasseTipoDato = :nmClasseTipoDatoIn ";
		javax.persistence.Query query = entityManager.createQuery(queryStr,
			IamAbilTipoDato.class);
		query.setParameter("idUserIamIn", utente.getIdUtente());
		query.setParameter("idOrganizApplicIn", utente.getIdOrganizzazioneFoglia());
		query.setParameter("idTipoDatoApplicIn",
			new BigDecimal(doc.getDecTipoDoc().getIdTipoDoc()));
		query.setParameter("nmClasseTipoDatoIn", "TIPO_DOC");
		iamAbilTipoDatos = query.getResultList();

		// ottengo un risultato -> abilitato al tipo dato
		if (iamAbilTipoDatos.size() == 1) {
		    rispostaControlli.setrLong(iamAbilTipoDatos.get(0).getIdAbilTipoDato());
		    rispostaControlli.setrBoolean(true);
		} else {
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_019_001);
		    rispostaControlli.setDsErr(MessaggiWSBundle.getString(
			    MessaggiWSBundle.UD_019_001, descKey,
			    String.format("'TIPO_DOC': %s", doc.getDecTipoDoc().getNmTipoDoc())));
		    rispostaControlli.setrBoolean(false);
		    break;
		}
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliRecupero.checkTipoDatoDocsIamUserOrganizzazioneByUd: "
			    + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica ", e);
	}

	return rispostaControlli;
    }
    // end MAC#29602

    public RispostaControlli checkUtente(String loginName) {
	User utente = null;
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    IamUser iamUser;
	    String queryStr = "select iu from IamUser iu where iu.nmUserid = :nmUseridIn";
	    Query query = entityManager.createQuery(queryStr, IamUser.class);
	    query.setParameter("nmUseridIn", loginName);
	    List<IamUser> tmpUsers = query.getResultList();
	    if (tmpUsers != null && !tmpUsers.isEmpty()) {
		iamUser = tmpUsers.get(0);

		if (!iamUser.getFlAttivo().equals("1")) {
		    // UTENTE_NON_ATTIVO
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_007);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_007, loginName));
		    return rispostaControlli;
		}
		if (iamUser.getDtScadPsw().before(new Date())) {
		    // UTENTE_SCADUTO
		    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_006);
		    rispostaControlli.setDsErr(
			    MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_006, loginName));
		    return rispostaControlli;
		}
		//
		utente = new User();
		utente.setUsername(loginName);
		utente.setIdUtente(iamUser.getIdUserIam());
		rispostaControlli.setrObject(utente);
		rispostaControlli.setrBoolean(true);
	    } else {
		// LOGIN_FALLITO
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_012);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_012,
			String.format("l'utente %s non è censito nel sistema", loginName)));
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ECCEZIONE_AUTENTICAZIONE + e.getMessage()));
	    log.error(ECCEZIONE_AUTENTICAZIONE, e);
	}

	return rispostaControlli;
    }

    public RispostaControlli checkAuthWS(User utente, IWSDesc descrizione,
	    TipiWSPerControlli tipows) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	boolean checkOrgVersAuth = false;
	long numAbil = 0;
	Integer tmpIdOrganizz = utente.getIdOrganizzazioneFoglia() != null
		? utente.getIdOrganizzazioneFoglia().intValue()
		: null;
	try {
	    WSLoginHandler.checkAuthz(utente.getUsername(), tmpIdOrganizz, descrizione.getNomeWs(),
		    entityManager);
	    rispostaControlli.setrBoolean(true);
	} catch (AuthWSException ex) {
	    checkOrgVersAuth = true;
	    switch (tipows) {
	    case VERSAMENTO_RECUPERO:
		// EVO#13993
	    case VERSAMENTO_RECUPERO_FASC:
		// end EVO#13993
		// L''utente {0} non è abilitato entro la struttura versante
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_009);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_009,
			utente.getUsername()));
		break;
	    case ANNULLAMENTO:
		rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_008);
		rispostaControlli
			.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_008));
		break;
	    default:
		break;
	    }
	} catch (Exception ex) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Errore nella verifica delle autorizzazioni utente  " + ex.getMessage()));
	    log.error("Errore nella verifica delle autorizzazioni utente ", ex);
	}

	if (checkOrgVersAuth) {
	    try {
		String queryStr = "select count(t) from IamAbilOrganiz t where "
			+ "t.iamUser.idUserIam = :idUserIamIn "
			+ "and t.idOrganizApplic = :idOrganizApplicIn";
		javax.persistence.Query query = entityManager.createQuery(queryStr);
		query.setParameter("idUserIamIn", utente.getIdUtente());
		query.setParameter("idOrganizApplicIn", BigDecimal.valueOf(tmpIdOrganizz));
		numAbil = (Long) query.getSingleResult();
		if (numAbil > 0) {
		    switch (tipows) {
		    case VERSAMENTO_RECUPERO:
			// EVO#13993
		    case VERSAMENTO_RECUPERO_FASC:
			// end EVO#13993
			// L'utente 0 non è autorizzato alla funzione 1
			rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_008);
			rispostaControlli
				.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_008,
					utente.getUsername(), descrizione.getNomeWs()));
			break;
		    case ANNULLAMENTO:
			rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_008);
			rispostaControlli.setDsErr(
				MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_008));
			break;
		    default:
			break;
		    }
		}
	    } catch (Exception ex) {
		rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
			"Errore nella verifica delle autorizzazioni utente  " + ex.getMessage()));
		log.error("Errore nella verifica delle autorizzazioni utente ", ex);
	    }
	}
	return rispostaControlli;
    }

    public RispostaControlli checkAuthWSNoOrg(User utente, IWSDesc descrizione) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    String querString = "select count(iu) from IamUser iu " + "JOIN iu.iamAbilOrganizs iao "
		    + "JOIN iao.iamAutorServs ias  " + "WHERE iu.nmUserid = :nmUserid  "
		    + "AND ias.nmServizioWeb = :servizioWeb";
	    Query query = entityManager.createQuery(querString);
	    query.setParameter("nmUserid", utente.getUsername());
	    query.setParameter("servizioWeb", descrizione.getNomeWs());
	    long num = (long) query.getSingleResult();
	    if (num > 0) {
		rispostaControlli.setrBoolean(true);
	    } else {
		// L'utente 0 non è autorizzato alla funzione 1
		rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_008);
		rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_008,
			utente.getUsername(), descrizione.getNomeWs()));
	    }
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    ECCEZIONE_AUTENTICAZIONE + e.getMessage()));
	    log.error(ECCEZIONE_AUTENTICAZIONE, e);
	}

	return rispostaControlli;
    }

    public RispostaControlli loadWsVersions(IWSDesc desc) {
	RispostaControlli rs = controlliSemantici
		.caricaDefaultDaDBParametriApplic(TipoParametroAppl.VERSIONI_WS);
	// if positive ...
	if (rs.isrBoolean()) {
	    HashMap<String, String> wsVersions = (HashMap<String, String>) rs.getrObject();
	    // verify if my version exits
	    if (VerificaVersione.getWsVersionList(desc.getNomeWs(), wsVersions).isEmpty()) {
		rs.setrBoolean(false);
		rs.setCodErr(MessaggiWSBundle.UD_018_001);
		rs.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_018_001,
			VerificaVersione.elabWsKey(desc.getNomeWs())));
	    }
	}
	return rs;
    }
}
