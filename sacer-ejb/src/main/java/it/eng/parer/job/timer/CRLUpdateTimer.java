// package it.eng.parer.job.timer;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import it.eng.crypto.bean.ConfigBean;
// import it.eng.crypto.data.SignerUtil;
// import it.eng.crypto.exception.CryptoStorageException;
// import it.eng.parer.crypto.helper.CRLHelperLocal;
// import it.eng.parer.crypto.helper.ConfigHelperLocal;
// import it.eng.parer.jboss.timer.common.CronSchedule;
// import it.eng.parer.job.utils.JobConstants;
// import it.eng.parer.volume.utils.VolumeEnums.OpTypeEnum;
// import java.security.cert.X509CRL;
// import javax.ejb.EJB;
// import javax.ejb.LocalBean;
// import javax.ejb.Lock;
// import javax.ejb.LockType;
// import javax.ejb.ScheduleExpression;
// import javax.ejb.Singleton;
// import javax.ejb.Timeout;
// import javax.ejb.Timer;
// import javax.ejb.TimerConfig;
// import javax.ejb.TransactionAttribute;
// import javax.ejb.TransactionAttributeType;
// import org.apache.log4j.Logger;
//
/// **
// * Session Bean implementation class InvokeUpdate
// */
// @Singleton(mappedName = "CRLUpdateTimer")
// @LocalBean
// @Lock(LockType.READ)
// public class CRLUpdateTimer extends JobTimer {
//
// private Logger logger = Logger.getLogger(CRLUpdateTimer.class);
// @EJB
// private CRLUpdateTimer thisTimer;
// @EJB
// private ConfigHelperLocal configHelper;
// @EJB
// private CRLHelperLocal crlHelper;
//
// public CRLUpdateTimer() {
// super(JobConstants.JobEnum.SCARICO_CRL.name());
// logger.debug(CRLUpdateTimer.class.getName() + " creato");
// }
//
// @Override
// @Lock(LockType.WRITE)
// public void startSingleAction(String appplicationName) {
// boolean existTimer = false;
//
// for (Object obj : timerService.getTimers()) {
// Timer timer = (Timer) obj;
// String scheduled = (String) timer.getInfo();
// if (scheduled.equals(jobName)) {
// existTimer = true;
// }
// }
// if (!existTimer) {
// List<ConfigBean> crlConfig = null;
// try {
// crlConfig = configHelper.retriveAllConfig();
// } catch (CryptoStorageException e) {
// logger.error("Errore nel reperimento delle configurazioni CRL", e);
// }
// if (crlConfig != null) {
// timerService.createTimer(TIME_DURATION, jobName);
// }
// }
// }
//
// @Override
// @Lock(LockType.WRITE)
// public void startCronScheduled(CronSchedule sched,String appplicationName) {
// boolean existTimer = false;
// ScheduleExpression tmpScheduleExpression;
//
// for (Object obj : timerService.getTimers()) {
// Timer timer = (Timer) obj;
// String scheduled = (String) timer.getInfo();
// if (scheduled.equals(jobName)) {
// existTimer = true;
// }
// }
// if (!existTimer) {
// logger.info("Schedulazione: Ore: " + sched.getHour());
// logger.info("Schedulazione: Minuti: " + sched.getMinute());
// logger.info("Schedulazione: DOW: " + sched.getDayOfWeek());
// logger.info("Schedulazione: Mese: " + sched.getMonth());
// logger.info("Schedulazione: DOM: " + sched.getDayOfMonth());
//
// tmpScheduleExpression = new ScheduleExpression();
// tmpScheduleExpression.hour(sched.getHour());
// tmpScheduleExpression.minute(sched.getMinute());
// tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
// tmpScheduleExpression.month(sched.getMonth());
// tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
// logger.info("Lancio il timer CalcoloContenutoTimer...");
// timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
// }
// }
//
// @Override
// @Lock(LockType.WRITE)
// public void stop(String appplicationName) {
// for (Object obj : timerService.getTimers()) {
// Timer timer = (Timer) obj;
// String scheduled = (String) timer.getInfo();
// if (scheduled.equals(jobName)) {
// timer.cancel();
// }
// }
// }
//
// @Timeout
// public void doJob(Timer timer) {
// if (timer.getInfo().equals(jobName)) {
// thisTimer.startProcess(timer);
// }
// }
//
// @Override
// public void startProcess(Timer timer) {
// logger.info("CRL Update Job - Started");
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
// try {
// // TASK SCARICO CRL PROSSIME ALLA SCADENZA - Recupero tutti i
// // distribution point del sistema per le CA attive e con data di
// // scadenza successiva alla data odierna
// // Le configurazioni sono ordinate per subjectDN e numero d'ordine
// // del distribution point
// List<ConfigBean> crlConfig = configHelper.retriveAllConfig();
// if (crlConfig != null) {
// logger.debug("CRL Update Job - Trovate " + crlConfig.size() + " configurazioni");
// // Per ogni configurazione invio un messaggio
// Map<String, List<String>> map = new HashMap<String, List<String>>();
// for (ConfigBean config : crlConfig) {
// List<String> distrPoints = null;
// if ((distrPoints = map.get(config.getSubjectDN() + "|" + config.getKeyId())) != null) {
// distrPoints.add(config.getCrlURL());
// } else {
// distrPoints = new ArrayList<String>();
// distrPoints.add(config.getCrlURL());
// map.put(config.getSubjectDN() + "|" + config.getKeyId(), distrPoints);
// }
// }
// for (Map.Entry<String, List<String>> urls : map.entrySet()) {
// try {
// thisTimer.updateCRL(urls.getValue());
// logger.debug("Inviato update delle CRL per il subject " + urls.getKey());
//
// } catch (Exception e) {
// logger.error("Errore nell'update :" + urls.getKey(), e);
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name(),e.getMessage());
// }
// }
// }
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.FINE_SCHEDULAZIONE.name());
// } catch (CryptoStorageException e) {
// logger.error("Errore nel reperimento delle configurazioni CRL", e);
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name());
// }
//
// }
//
// @javax.ejb.Asynchronous
// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
// public void updateCRL(List<String> distributionPoints) {
// // In base all'url passato in ingresso recupero la CRL.
// X509CRL crl = SignerUtil.newInstance().getCrlByURL(distributionPoints);
//
// try {
// if (crl != null) {
// // la salvo sul db
// crlHelper.upsertCRL(crl);
// }
// } catch (CryptoStorageException e) {
// logger.error("Non è stato possibile salvare la CRL nel db", e);
// }
// }
// }
