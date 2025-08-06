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
package it.eng.parer.ws.monitoraggio.ejb;

import it.eng.parer.entity.DecJob;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliMonitor")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class ControlliMonitor {

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;

    private static final Logger log = LoggerFactory.getLogger(ControlliMonitor.class);

    public final static String WS_MONITORAGGIO_STATUS = "WS_MONITORAGGIO_STATUS";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private ConfigurationHelper configurationHelper;

    public RispostaControlli leggiUltimaChiamataWS() {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<Date> lstDate = null;

	try {
	    String queryStr = "select max(lj.dtRegLogJob) " + "from LogJob lj "
		    + "where lj.nmJob = :nmJob " + "and lj.tiRegLogJob = :tiRegLogJob ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmJob", WS_MONITORAGGIO_STATUS);
	    query.setParameter("tiRegLogJob", JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
	    lstDate = query.getResultList();
	    if (lstDate != null && lstDate.size() > 0 && lstDate.get(0) != null) {
		rispostaControlli.setrDate(lstDate.get(0));
	    } else {
		rispostaControlli.setrDate(this.sottraiUnGiorno(new Date()));
	    }
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.leggiUltimaChiamataWS " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei log dei job", e);
	}
	return rispostaControlli;
    }

    public RispostaControlli leggiElencoJob() {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<DecJob> lstJobs = null;

	try {
	    String queryStr = "select t from DecJob t " + "where t.tiSchedJob = 'STANDARD' "
		    + "or t.tiSchedJob = 'NO_TIMER'";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    lstJobs = query.getResultList();
	    rispostaControlli.setrObject(lstJobs);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.leggiElencoJob " + e.getMessage()));
	    log.error("Eccezione nella lettura  della tabella dei job", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli recNuovaEsecuzioneTimer(String jobName) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);

	try {
	    // Date tmpdaDate =
	    // timerManager.getNextElaboration(JobConstants.JobEnum.valueOf(jobName));
	    Date tmpdaDate = jbossTimerEjb.getDataProssimaAttivazione(jobName);

	    rispostaControlli.setrDate(tmpdaDate);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.recNuovaEsecuzioneTimer " + e.getMessage()));
	    log.error("Eccezione nell'accesso al manager dei job", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli leggiUltimaRegistrazione(String jobName) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	List<LogJob> lstJob;
	List<Date> lstDate = null;
	try {
	    // leggo la data dell'ultimo inizio di attività del job
	    String queryStr = "select max(lj.dtRegLogJob) " + "from LogJob lj "
		    + "where lj.nmJob = :nmJob " + "and lj.tiRegLogJob = :tiRegLogJob ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmJob", jobName);
	    query.setParameter("tiRegLogJob", JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
	    lstDate = query.getResultList();
	    if (lstDate != null && lstDate.size() > 0 && lstDate.get(0) != null) {
		Date dataInizio = lstDate.get(0);
		// cerco la data fine job (o errore nel job)
		// successiva o contemporanea all'inizio
		queryStr = "select j from LogJob j " + "where j.dtRegLogJob >= :dataInizio "
			+ "and (j.tiRegLogJob = :tiRegLogJob1 "
			+ "or j.tiRegLogJob = :tiRegLogJob2) " + "and j.nmJob = :nmJob ";

		query = entityManager.createQuery(queryStr);
		query.setParameter("nmJob", jobName);
		query.setParameter("tiRegLogJob1",
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
		query.setParameter("tiRegLogJob2", JobConstants.OpTypeEnum.ERRORE.name());
		query.setParameter("dataInizio", dataInizio);
		lstJob = query.getResultList();
		if (lstJob != null && lstJob.size() > 0) {
		    // se l'ho trovata, rendo la data e il tipo di schedulazione
		    rispostaControlli.setrDate(lstJob.get(0).getDtRegLogJob());
		    rispostaControlli.setrObject(
			    JobConstants.OpTypeEnum.valueOf(lstJob.get(0).getTiRegLogJob()));
		} else {
		    // altrimenti, rendo la data dell'inizio schedulazione (il job è in corso)
		    rispostaControlli.setrDate(dataInizio);
		    rispostaControlli.setrObject(JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE);
		}
	    } else {
		// se il job non ha mai "girato", rendo una condizione
		// di job terminato correttamente, che non
		// produrrà allarmi in sede di valutazione del
		// monitoraggio
		rispostaControlli.setrDate(new Date(0));
		rispostaControlli.setrObject(JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE);
	    }
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.leggiUltimaRegistrazione " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei log dei job", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli leggiAllarmiInIntervallo(String jobName, Date dataInizio,
	    Date ultimaAttivitaDelJob) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	Long numAllarmi;

	try {
	    String queryStr = "select count(j) from LogJob j "
		    + "where j.dtRegLogJob >= :dataInizio " + "and j.dtRegLogJob < :ultimaAttivita "
		    + "and j.tiRegLogJob = :tiRegLogJob " + "and j.nmJob = :nmJob ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmJob", jobName);
	    query.setParameter("tiRegLogJob", JobConstants.OpTypeEnum.ERRORE.name());
	    query.setParameter("dataInizio", dataInizio);
	    query.setParameter("ultimaAttivita", ultimaAttivitaDelJob);
	    numAllarmi = (Long) query.getSingleResult();
	    rispostaControlli.setrLong(numAllarmi);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.leggiAllarmiInIntervallo " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella dei log dei job", e);
	}

	return rispostaControlli;
    }

    public boolean controllaStatoDbOracle() {
	boolean resp = false;
	try {
	    String queryStr = "select 1 from dual";
	    javax.persistence.Query query = entityManager.createNativeQuery(queryStr);
	    BigDecimal r = (BigDecimal) query.getSingleResult();
	    if (r.longValue() == 1L) {
		resp = true;
	    }
	} catch (Exception e) {
	    log.error("Problema nella connessione al db Oracle: ", e);
	}
	return resp;
    }

    public Date sottraiUnGiorno(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	cal.add(Calendar.DAY_OF_MONTH, -1);
	return cal.getTime();
    }

    public RispostaControlli leggiStatoIndiceAipUdDaElab(Date dateMax, String flagInCoda) {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	long count = 0;

	try {
	    String queryStr = "select count(a) from AroIndiceAipUdDaElab a "
		    + "where a.flInCoda = :flInCoda " + "and a.tsInCoda <= :tsInCodaMax ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("flInCoda", flagInCoda);// in coda
	    query.setParameter("tsInCodaMax", dateMax);
	    count = (Long) query.getSingleResult();
	    rispostaControlli.setrLong(count);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.leggiStatoMessaggiIndiceAipUdDaElab "
			    + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella indice aip ud da elab", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli verificaNiFilePathArk() {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	rispostaControlli.setrLong(0);

	try {
	    // date

	    // MAC#27666
	    // DateFormat dateFormat = new SimpleDateFormat(Costanti.TPI_DATA_PATH_FMT_STRING);
	    DateTimeFormatter dateFormat = DateTimeFormatter
		    .ofPattern(Costanti.TPI_DATA_PATH_FMT_STRING);
	    // end MAC#27666
	    String dataInizioParam = configurationHelper.getValoreParamApplicByApplic(
		    CostantiDB.ParametroAppl.TPI_DATA_INIZIO_CONTROLLO_NUM_FILE_ARK);
	    // MAC#27666
	    LocalDate dataInizio = LocalDate.from(dateFormat.parse(dataInizioParam));
	    // end MAC#27666

	    String queryStr = "select v from VrsPathDtVers v "
		    + "where v.vrsDtVers.dtVers < CURRENT_DATE and v.vrsDtVers.dtVers >= :dtVers "
		    // la successiva condition garantisce (per logica applicativa) che
		    // v.vrsDtVers.flArk = TRUE (è implicito)
		    + "and v.vrsDtVers.tiStatoDtVers = :tiStatoDtVers "
		    // se flag ark è true devo controllare entrambi i contatori
		    + "and (v.vrsDtVers.flArkSecondario = :flArkSecondario and (v.niFilePath <> v.niFilePathArk or v.niFilePath <> v.niFilePathArkSecondario) "
		    + "or v.vrsDtVers.flArkSecondario is null and v.vrsDtVers.flFileNoArk = :flFileNoArk and v.niFilePath <> v.niFilePathArk)";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("dtVers", dataInizio);
	    query.setParameter("tiStatoDtVers", JobConstants.ArkStatusEnum.ARCHIVIATA.name());
	    query.setParameter("flArkSecondario", CostantiDB.Flag.TRUE);
	    query.setParameter("flFileNoArk", CostantiDB.Flag.FALSE);

	    List<VrsDtVers> result = (List<VrsDtVers>) query.getResultList();
	    rispostaControlli.setrLong(result.size());
	    rispostaControlli.setrObject(result);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.verificaNiFilePathArk " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella data versamento ", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli verificaIfExistStatoArchErr() {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	rispostaControlli.setrLong(0);

	try {
	    String queryStr = "select v from VrsDtVers v "
		    + "where v.tiStatoDtVers = :tiStatoDtVers and v.dtVers < CURRENT_DATE ";

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("tiStatoDtVers", JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name());
	    List<VrsDtVers> result = (List<VrsDtVers>) query.getResultList();
	    rispostaControlli.setrLong(result.size());
	    rispostaControlli.setrObject(result);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.verificaPresenzaStatoArchErr " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella data versamento ", e);
	}

	return rispostaControlli;
    }

    public RispostaControlli verificaDataNotArk() {
	RispostaControlli rispostaControlli;
	rispostaControlli = new RispostaControlli();
	rispostaControlli.setrBoolean(false);
	rispostaControlli.setrLong(0);

	try {
	    String queryStr = "select v1 from VrsDtVers v1 "
		    + "where v1.tiStatoDtVers in :tiStatoDtVersRegDaArk "
		    + "and v1.dtVers < CURRENT_DATE and exists ("
		    + "select v2 from VrsDtVers v2 where v2.tiStatoDtVers in :tiStatoDtVersArkArkErr and v2.dtVers >= v1.dtVers "
		    + ")";

	    List<String> tiStatoDtVersRegDaArk = Arrays.asList(
		    JobConstants.ArkStatusEnum.REGISTRATA.name(),
		    JobConstants.ArkStatusEnum.DA_ARCHIVIARE.name());
	    List<String> tiStatoDtVersArkArkErr = Arrays.asList(
		    JobConstants.ArkStatusEnum.ARCHIVIATA.name(),
		    JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name());

	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("tiStatoDtVersRegDaArk", tiStatoDtVersRegDaArk);
	    query.setParameter("tiStatoDtVersArkArkErr", tiStatoDtVersArkArkErr);

	    List<VrsDtVers> result = (List<VrsDtVers>) query.getResultList();
	    rispostaControlli.setrLong(result.size());
	    rispostaControlli.setrObject(result);
	    rispostaControlli.setrBoolean(true);
	} catch (Exception e) {
	    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
	    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
		    "Eccezione ControlliMonitor.verificaPresenzaStatoArchErr " + e.getMessage()));
	    log.error("Eccezione nella lettura della tabella data versamento ", e);
	}

	return rispostaControlli;
    }
}
