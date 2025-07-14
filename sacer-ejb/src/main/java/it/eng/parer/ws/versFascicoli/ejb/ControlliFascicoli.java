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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.versFascicoli.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecLivelloTitol;
import it.eng.parer.entity.DecParteNumeroFascicolo;
import it.eng.parer.entity.DecTitol;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versFascicoli.dto.ConfigNumFasc;
import it.eng.parer.ws.versFascicoli.utils.KeyOrdFascUtility.TipiCalcolo;

/**
 *
 * @author fioravanti_f, sinatti_s
 */
@Stateless(mappedName = "ControlliFascicoli")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliFascicoli {

    private static final Logger log = LoggerFactory.getLogger(ControlliFascicoli.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public enum TipiGestioneFascAnnullati {

	CARICA, CONSIDERA_ASSENTE
    }

    final static String NOME_FASCICOLO_SCONOSCIUTO = "Tipo fascicolo sconosciuto";

    public RispostaControlli getDecAaTipoFascicolo(long idAaTipoFascicolo) {

	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrLong(-1);
	DecAaTipoFascicolo aaTipoFasc = null;

	try {
	    aaTipoFasc = entityManager.find(DecAaTipoFascicolo.class, idAaTipoFascicolo);
	    rispostaControlli.setrLong(0);
	    rispostaControlli.setrObject(aaTipoFasc);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliFascicoli.getDecAaTipoFascicolo: " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella di decodifica " + e);
	}
	return rispostaControlli;
    }

    public RispostaControlli getDecLvlVoceTitolWithNiLivello(long niLivello, long idStrutt,
	    Date dtApertura) {

	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	DecTitol decTitol = null;

	rispostaControlli = this.getDecTitolStrutt(idStrutt, dtApertura);

	if (rispostaControlli.getrLong() != -1) {
	    decTitol = (DecTitol) rispostaControlli.getrObject();
	    return getDecLvlVoceTitolWithNiLivello(niLivello, idStrutt, dtApertura,
		    decTitol.getIdTitol());
	} else {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666P);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666P,
		    "ControlliFascicoli.getDecLvlVoceTitolWithNiLivello.getDecTitolStrutt: Errore recupero del titolario"));
	    return rispostaControlli;
	}

    }

    public RispostaControlli getDecLvlVoceTitolWithNiLivello(long niLivello, long idStrutt,
	    Date dtApertura, long idTitol) {

	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	List<DecLivelloTitol> decLivelloTitols = null;

	try {
	    String queryStr = "select d from DecLivelloTitol d "
		    + "where d.decTitol.idTitol = :idTitol " + "AND d.niLivello = :niLivello ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr,
		    DecLivelloTitol.class);
	    query.setParameter("idTitol", idTitol);
	    query.setParameter("niLivello", niLivello);// livello successivo

	    decLivelloTitols = query.getResultList();

	    // se esiste verifico se ottenere un separatore per la parte successiva
	    if (decLivelloTitols.size() == 1) {
		// livello attuale
		rispostaControlli.setrObject(decLivelloTitols.get(0));
		rispostaControlli.setrBoolean(true);
	    }

	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliFascicoli.getDecLvlVoceTitolWithNiLivello: " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella di decodifica ", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli getDecTitolStrutt(long idStruttura, Date dtApertura) {

	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrLong(-1);
	List<DecTitol> decTitols = null;

	try {

	    String queryStr = "select d from DecTitol d " + "where d.orgStrut.idStrut = :idStrut  "
		    + " AND  d.dtIstituz <= :dtApertura  " + " AND  d.dtSoppres >= :dtApertura ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr, DecTitol.class);
	    query.setParameter("idStrut", idStruttura);
	    query.setParameter("dtApertura", dtApertura);

	    decTitols = query.getResultList();

	    if (decTitols.size() == 1) {
		rispostaControlli.setrObject(decTitols.get(0));
		rispostaControlli.setrLong(0);
	    }

	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliFascicoli.getDecTitol: " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica " + e);
	}
	return rispostaControlli;
    }

    @SuppressWarnings("unchecked")
    public RispostaControlli checkCdVoceDecVoceTitol(String cdVoceTitol,
	    String cdCompositoVoceTitol, long idStrutt, Date dtApertura) {

	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	DecTitol decTitol = null;
	List<DecVoceTitol> decVoceTitols = null;

	try {

	    rispostaControlli = this.getDecTitolStrutt(idStrutt, dtApertura);

	    if (rispostaControlli.getrLong() != -1) {
		decTitol = (DecTitol) rispostaControlli.getrObject();

		String queryStr = "select d from DecVoceTitol d "
			+ "where d.decTitol.idTitol = :idTitol "
			+ " AND upper(d.cdVoceTitol) = upper(:cdVoceTitol)  "
			+ " AND upper(d.cdCompositoVoceTitol) = upper(:cdCompositoVoceTitol)  "
			+ " AND d.dtIstituz <= :dtApertura " + " AND d.dtSoppres >= :dtApertura ";

		javax.persistence.Query query = entityManager.createQuery(queryStr,
			DecVoceTitol.class);
		query.setParameter("idTitol", decTitol.getIdTitol());
		query.setParameter("cdVoceTitol",
			StringEscapeUtils.escapeJava(cdVoceTitol.toUpperCase().trim()));// ripulisco
											// dagli
											// spazi
		// escaping (stessa gestione della fase di inserimento sulle voci vedi
		// StrutTitolariEjb.creaVoce)
		query.setParameter("cdCompositoVoceTitol",
			StringEscapeUtils.escapeJava(cdCompositoVoceTitol.toUpperCase().trim()));// ripulisco
												 // dagli
												 // spazi
		query.setParameter("dtApertura", dtApertura);

		decVoceTitols = query.getResultList();

		// se esiste verifico se ottenere un separatore per la parte successiva
		if (decVoceTitols.size() == 1) {
		    // restituisco l'oggetto
		    rispostaControlli.setrObject(decVoceTitols.get(0));
		    rispostaControlli.setrBoolean(true);
		} else {
		    return rispostaControlli;
		}

	    } else {
		return rispostaControlli;
	    }

	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "ControlliFascicoli.checkCdVoceTitolario: " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella di decodifica del titolario ", e);
	}

	return rispostaControlli;
    }

    // chiamata anche dal job, rende la configurazione per validare i numeri/chiave
    // in base all'anno del registro
    public ConfigNumFasc caricaPartiAANumero(long idAaNumeroFasc, long niCharPadParteClassif) {
	ConfigNumFasc tmpConfAnno = new ConfigNumFasc(idAaNumeroFasc);

	String queryStr = "select t from DecParteNumeroFascicolo t "
		+ "where t.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
		+ "order by t.niParteNumero";

	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("idAaTipoFascicolo", idAaNumeroFasc);
	List<DecParteNumeroFascicolo> tmpLstP = (List<DecParteNumeroFascicolo>) query
		.getResultList();

	for (DecParteNumeroFascicolo tmpParte : tmpLstP) {
	    ConfigNumFasc.ParteNumero tmpPRanno = tmpConfAnno.aggiungiParte();
	    tmpPRanno.setNumParte(tmpParte.getNiParteNumero().intValue());
	    tmpPRanno.setNomeParte(tmpParte.getNmParteNumero());
	    tmpPRanno.setMaxLen(
		    tmpParte.getNiMaxCharParte() != null ? tmpParte.getNiMaxCharParte().longValue()
			    : -1);
	    tmpPRanno.setMinLen(
		    tmpParte.getNiMinCharParte() != null ? tmpParte.getNiMinCharParte().longValue()
			    : 0);

	    //
	    tmpPRanno.setSeparatore(
		    (tmpParte.getTiCharSep() != null && tmpParte.getTiCharSep().isEmpty()) ? " "
			    : tmpParte.getTiCharSep());
	    /*
	     * se il separatore è una stringa non-nulla ma vuota, il valore viene letto come uno
	     * spazio. nel DB è memorizzato come CHAR(1), pad-dato -al salvataggio- da Oracle, e che
	     * al momento della lettura viene trim-ato da eclipselink. Quindi con questo sistema
	     * ricostruisco il valore originale se questo era uno spazio
	     */
	    //
	    // Nota: nel DB la variabile tiParte ha tre valori mutualmente esclusivi.
	    // in questo caso, vengono gestiti come 4 flag separati perché i test relativi
	    // vengono effettuati in parti diverse del codice
	    tmpPRanno.setMatchAnnoChiave(tmpParte.getTiParte() != null
		    && tmpParte.getTiParte().equals(ConfigNumFasc.TiParte.ANNO.name()));
	    tmpPRanno.setMatchClassif(tmpParte.getTiParte() != null
		    && tmpParte.getTiParte().equals(ConfigNumFasc.TiParte.CLASSIF.name()));
	    tmpPRanno.setUsaComeProgressivo(tmpParte.getTiParte() != null
		    && tmpParte.getTiParte().equals(ConfigNumFasc.TiParte.PROGR.name()));
	    tmpPRanno.setUsaComeSottoProgressivo(tmpParte.getTiParte() != null
		    && tmpParte.getTiParte().equals(ConfigNumFasc.TiParte.PROGSUB.name()));
	    tmpPRanno.setTipoCalcolo(TipiCalcolo.valueOf(tmpParte.getTiCharParte()));
	    tmpPRanno.setTiPadding(tmpParte.getTiPadParte() != null
		    ? ConfigNumFasc.TipiPadding.valueOf(tmpParte.getTiPadParte())
		    : ConfigNumFasc.TipiPadding.NESSUNO);
	    tmpPRanno.setNiPadParteClassif(niCharPadParteClassif);// padding classif
	    ConfigNumFasc.impostaValoriAccettabili(tmpPRanno, tmpParte.getDlValoriParte());
	}
	tmpConfAnno.ElaboraParti();
	return tmpConfAnno;
    }

    public RispostaControlli leggiFascicolo(long idFascicolo) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	FasFascicolo tmpFasFascicolo = null;

	try {
	    tmpFasFascicolo = entityManager.find(FasFascicolo.class, idFascicolo);
	    rispostaControlli.setrObject(tmpFasFascicolo);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliFascicoli.leggiFascicolo " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei fascicoli ", e);
	}
	return rispostaControlli;
    }
}
