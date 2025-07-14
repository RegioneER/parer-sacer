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

package it.eng.parer.job.serie.ejb;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.serie.dto.CreazioneSerieBean;
import it.eng.parer.serie.dto.IntervalliSerieAutomBean;
import it.eng.parer.serie.dto.SerieAutomBean;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.serie.utils.FutureUtils;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author bonora_l
 */
@Stateless
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneSerieJob {

    private final Logger log = LoggerFactory.getLogger(CreazioneSerieJob.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private ConfigurationHelper configHelper;
    @EJB
    private RegistroEjb registroEjb;
    @EJB
    private UserHelper userHelper;

    public void creaSerie() throws ParerInternalError {

	log.info(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name()
		+ " --- ricerca i tipi serie per la creazione automatica");
	List<DecTipoSerie> tipiSerieAutom = serieHelper.getDecTipoSerieAutom();
	log.info(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name() + " --- Trovati "
		+ tipiSerieAutom.size() + " tipi serie");
	Map<String, BigDecimal> idVerSeries = new HashMap<>();
	// String nmUserId = configHelper.getValoreParamApplic("USERID_CREAZIONE_SERIE");
	// IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);
	Date now = Calendar.getInstance().getTime();
	for (DecTipoSerie tipoSerie : tipiSerieAutom) {
	    BigDecimal idTipoSerie = new BigDecimal(tipoSerie.getIdTipoSerie());
	    BigDecimal idStrut = new BigDecimal(tipoSerie.getOrgStrut().getIdStrut());
	    BigDecimal aaIniCreaAutom = tipoSerie.getAaIniCreaAutom();
	    BigDecimal aaFinCreaAutom = tipoSerie.getAaFinCreaAutom() != null
		    ? tipoSerie.getAaFinCreaAutom()
		    : new BigDecimal((Calendar.getInstance().get(Calendar.YEAR) - 1));
	    boolean afterGgCreaAutom = false;
	    try {
		int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
		SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_TYPE);
		df.parse(tipoSerie.getGgCreaAutom());
		Calendar cal = df.getCalendar();
		cal.set(Calendar.YEAR, annoCorrente);
		/*
		 * Se il giorno di creazione in automatico delle serie (gg_crea_autom con formato
		 * dd/mm) è successivo al giorno corrente e se aa_fin_crea_autom = anno corrente –
		 * 1, si sottrae 1 ad aa_fin_crea_autom
		 */
		if (cal.getTime().after(now)) {
		    afterGgCreaAutom = true;
		    if (aaFinCreaAutom.intValue() == (annoCorrente - 1)) {
			aaFinCreaAutom = new BigDecimal(annoCorrente - 2);
		    }
		}
	    } catch (ParseException ex) {
		log.error("Errore di parsing del giorno di creazione automatica "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new ParerInternalError(
			"Errore inaspettato nella lettura del giorno di creazione automatica");
	    }

	    SerSerie serie = serieHelper.getSerieAutom(aaIniCreaAutom, aaFinCreaAutom,
		    tipoSerie.getIdTipoSerie());
	    if (serie != null) {
		aaIniCreaAutom = serie.getAaSerie().add(BigDecimal.ONE);
	    }
	    log.info(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name()
		    + " --- Anno di inizio creazione serie per il tipo "
		    + tipoSerie.getNmTipoSerie() + ": " + aaIniCreaAutom.toPlainString());

	    BigDecimal niAnniConserv;
	    if (tipoSerie.getNiAnniConserv() != null) {
		niAnniConserv = tipoSerie.getNiAnniConserv();
	    } else {
		niAnniConserv = registroEjb.getMaxAnniConserv(idTipoSerie);
	    }
	    if (niAnniConserv == null) {
		throw new ParerInternalError(
			"Errore di configurazione del tipo serie: non sono definiti gli anni di conservazione n\u00E9 nel tipo serie, n\u00E9 nei registri associati");
	    }
	    for (int anno = aaIniCreaAutom.intValue(); anno <= aaFinCreaAutom.intValue(); anno++) {
		BigDecimal niMesiCreazioneSerie = tipoSerie.getNiMmCreaAutom();
		SerieAutomBean creaAutomBean;
		try {
		    creaAutomBean = serieEjb.generateIntervalliSerieAutom(niMesiCreazioneSerie,
			    tipoSerie.getCdSerieDefault(), tipoSerie.getDsSerieDefault(), anno,
			    tipoSerie.getTiSelUd());
		} catch (ParerUserError ex) {
		    throw new ParerInternalError(ex.getDescription());
		}

		log.info(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name()
			+ " --- Numero di serie da creare per l'anno " + anno + ": "
			+ creaAutomBean.getNumeroSerieDaCreare());
		for (int i = 0; i < creaAutomBean.getIntervalli().size(); i++) {
		    log.info("Serie numero: " + (i + 1));
		    IntervalliSerieAutomBean intervallo = creaAutomBean.getIntervalli().get(i);
		    log.info(intervallo.getCdSerie());

		    log.info("Verifico l'esistenza di una serie con questi parametri...");
		    CreazioneSerieBean serieBean = new CreazioneSerieBean(
			    CostantiDB.TipoCreazioneSerie.CALCOLO_AUTOMATICO.name(), idTipoSerie,
			    niAnniConserv, intervallo.getCdSerie(), intervallo.getDsSerie(),
			    new BigDecimal(anno), intervallo.getDtInizioSerie(),
			    intervallo.getDtFineSerie(), "Creazione automatica",
			    "Serie creata mediante CALCOLO_AUTOMATICO",
			    creaAutomBean.getTipoIntervallo(),
			    creaAutomBean.getTipoIntervallo() != null ? new BigDecimal(i + 1)
				    : null);
		    if (!serieEjb.isSerieExisting(idStrut, idTipoSerie, new BigDecimal(anno),
			    intervallo.getDtInizioSerie(), intervallo.getDtFineSerie(),
			    serieBean.getCdCompositoSerie())
			    && !serieEjb.isSerieExisting(idStrut, new BigDecimal(anno),
				    serieBean.getCdCompositoSerie())) {
			log.info("Serie non ancora esistente, la creo");
			if (tipoSerie.getTiSelUd()
				.equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
			    BigDecimal niAaSelUd = tipoSerie.getNiAaSelUd();
			    BigDecimal niAaSelUdSuc = tipoSerie.getNiAaSelUdSuc();
			    if (niAaSelUd == null) {
				niAaSelUd = BigDecimal.ZERO;
			    }
			    if (niAaSelUdSuc == null) {
				niAaSelUdSuc = BigDecimal.ZERO;
			    }
			    int fromIndex = serieBean.getAa_serie().subtract(niAaSelUd).intValue();
			    int toIndex = serieBean.getAa_serie().add(niAaSelUdSuc).intValue();
			    StringBuilder dsLista = new StringBuilder();
			    for (int index = fromIndex; index < (toIndex + 1); index++) {
				dsLista.append(String.valueOf(index)).append(",");
			    }
			    dsLista.deleteCharAt(dsLista.length() - 1);
			    serieBean.setDs_lista_anni_sel_serie(dsLista.toString());
			}
			// EVO#16486
			// calcola e verifica il codice normalizzato
			String cdSerieNormalized = MessaggiWSFormat
				.normalizingKey(serieBean.getCd_serie()); // base
			if (serieEjb.existsCdSerieNormalized(idStrut, serieBean.getAa_serie(),
				cdSerieNormalized)) {
			    // codice normalizzato già presente su sistema
			    throw new ParerInternalError(
				    "Il controllo univocità del codice normalizzato della serie "
					    + serieBean.getCd_serie() + " della struttura "
					    + idStrut + " , ha restituito errore");
			} else {
			    // cd serie normalized (se calcolato)
			    serieBean.setCd_serie_normaliz(cdSerieNormalized);
			}
			// end EVO#16486

			Long idVersione;
			try {
			    OrgStrut strut = serieHelper.findById(OrgStrut.class, idStrut);
			    // userCreazioneSerie = userHelper.findIamUser(nmUserId);
			    String nmUserId = configHelper.getValoreParamApplicByStrut(
				    CostantiDB.ParametroAppl.USERID_CREAZIONE_SERIE,
				    BigDecimal.valueOf(
					    strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()),
				    idStrut);
			    IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);
			    idVersione = serieEjb.saveSerie(userCreazioneSerie.getIdUserIam(),
				    idStrut, serieBean, null);
			    if (anno == aaFinCreaAutom.intValue() && !afterGgCreaAutom) {
				saveDecTipoSerieCreataAutom(i,
					creaAutomBean.getNumeroSerieDaCreare(), tipoSerie);
			    }
			} catch (ParerInternalError ex) {
			    throw ex;
			} catch (ParerUserError ex) {
			    String msg = ex.getDescription();
			    throw new ParerInternalError(msg);
			} catch (Exception ex) {
			    log.error("Errore inaspettato nel salvataggio della serie: "
				    + ExceptionUtils.getRootCauseMessage(ex), ex);
			    throw new ParerInternalError(
				    "Errore inaspettato nel salvataggio della serie: "
					    + ExceptionUtils.getRootCauseMessage(ex));
			}
			if (idVersione != null) {
			    String keyFuture = FutureUtils.buildKeyFuture(
				    CostantiDB.TipoCreazioneSerie.CALCOLO_AUTOMATICO.name(),
				    serieBean.getCd_serie(), serieBean.getAa_serie(), idStrut,
				    idVersione);
			    idVerSeries.put(keyFuture, new BigDecimal(idVersione));
			}
		    } else {
			log.info("Serie gi\u00E0 esistente, skip alla serie successiva");
			if (anno == aaFinCreaAutom.intValue() && !afterGgCreaAutom) {
			    try {
				saveDecTipoSerieCreataAutom(i,
					creaAutomBean.getNumeroSerieDaCreare(), tipoSerie);
			    } catch (ParerInternalError ex) {
				throw ex;
			    } catch (Exception ex) {
				log.error("Errore inaspettato nel salvataggio della serie: "
					+ ExceptionUtils.getRootCauseMessage(ex), ex);
				throw new ParerInternalError(
					"Errore inaspettato nel salvataggio della serie: "
						+ ExceptionUtils.getRootCauseMessage(ex));
			    }
			}
		    }
		}
	    }
	}
	if (!idVerSeries.isEmpty()) {
	    serieEjb.callCreazioneSerieAsync(idVerSeries);
	}

	log.info(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name()
		+ " --- Fine schedulazione job");
	jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(),
		JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    private void saveDecTipoSerieCreataAutom(int i, int numeroSerieDaCreare, DecTipoSerie tipoSerie)
	    throws ParerInternalError, ParseException {
	if (i == (numeroSerieDaCreare - 1)) {
	    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_TYPE);
	    df.parse(tipoSerie.getGgCreaAutom());
	    Calendar cal = df.getCalendar();
	    cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
	    serieEjb.saveDecTipoSerieCreataAutom(tipoSerie.getIdTipoSerie(), cal.getTime());
	}
    }
}
