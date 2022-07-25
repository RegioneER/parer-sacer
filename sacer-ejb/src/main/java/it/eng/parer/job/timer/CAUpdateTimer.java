// package it.eng.parer.job.timer;
//
// import java.security.cert.X509CRL;
// import java.security.cert.X509Certificate;
// import java.util.List;
// import java.util.Map;
// import it.eng.crypto.bean.ConfigBean;
// import it.eng.crypto.data.SignerUtil;
// import it.eng.crypto.exception.CryptoSignerException;
// import it.eng.crypto.exception.CryptoStorageException;
// import it.eng.parer.crypto.helper.CAHelperLocal;
// import it.eng.parer.crypto.helper.CRLHelperLocal;
// import it.eng.parer.crypto.helper.ConfigHelperLocal;
// import it.eng.parer.jboss.timer.common.CronSchedule;
// import it.eng.parer.job.utils.JobConstants;
// import it.eng.parer.volume.utils.VolumeEnums.OpTypeEnum;
// import java.math.BigDecimal;
// import java.security.cert.CertificateException;
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
// import org.apache.commons.io.IOUtils;
// import org.apache.log4j.Logger;
// import org.bouncycastle.asn1.ASN1InputStream;
//
/// **
// * Session Bean implementation class InvokeUpdate
// */
// @Singleton(mappedName = "CAUpdateTimer")
// @LocalBean
// @Lock(LockType.READ)
// public class CAUpdateTimer extends JobTimer {
//
// private Logger logger = Logger.getLogger(CAUpdateTimer.class);
// @EJB
// private CAUpdateTimer thisTimer;
// @EJB
// private CAHelperLocal caHelper;
// @EJB
// private ConfigHelperLocal configHelper;
// @EJB
// private CRLHelperLocal crlHelper;
//
// public CAUpdateTimer() {
// super(JobConstants.JobEnum.SCARICO_CA.name());
// logger.debug(CAUpdateTimer.class.getName() + " creato");
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
// timerService.createTimer(TIME_DURATION, jobName);
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
// logger.info("Lancio il timer CAUpdateTimer...");
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
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
//
// try {
// // TASK UPDATE CERTIFICATI CA e CRL - Scarico tutti i
// // certificati dal CNIPA, le CRL e creo le configurazioni nel
// // DB.
// Map<String, X509Certificate> qualifiedCertificate =
// SignerUtil.newInstance().getQualifiedPrincipalsAndX509Certificates();
// logger.info("Trovati " + qualifiedCertificate.size() + " certificati dal CNIPA");
// for (X509Certificate certificate : qualifiedCertificate.values()) {
// thisTimer.updateCA(certificate);
// }
// jobHelper.writeLogJob(jobName, OpTypeEnum.FINE_SCHEDULAZIONE.name());
// } catch (CryptoSignerException e) {
// logger.error("Errore nello scarico dei certificati CA dal CNIPA", e);
// jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name());
// }
//
// }
//
// @javax.ejb.Asynchronous
// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
// public void updateCA(X509Certificate cert) {
// ASN1InputStream aIn = null;
// try {
// caHelper.insertCA(cert);
//
// logger.debug("SubjectDN della CA: " + cert.getSubjectX500Principal().getName());
// boolean isCAActive = false;
// try {
// cert.checkValidity();
// isCAActive = true;
// } catch (CertificateException e) {
// isCAActive = false;
// }
//
// // Salvo l'url di recupero CRL
// List<String> urls = null;
// X509CRL crl = null;
// // Scarico la CRL
// try {
// urls = SignerUtil.newInstance().getURLCrlDistributionPoint(cert);
// crl = SignerUtil.newInstance().getCrlByURL(urls);
// } catch (CryptoSignerException e) {
// logger.warn("Non è stato possibile recuperare la CRL dal distribution point - Subject Key ID: "+
// SignerUtil.getSubjectKeyId(cert));
// }
// if (isCAActive) {
// // Creo una nuova configurazione
// int i = 1;
// for (String url : urls) {
// ConfigBean config = new ConfigBean();
// config.setCrlURL(url);
// config.setNiOrdUrlDistribCrl(new BigDecimal(i));
// config.setSubjectDN(cert.getSubjectX500Principal().getName());
// config.setKeyId(SignerUtil.getSubjectKeyId(cert));
// configHelper.upsertConfig(config);
// i++;
// }
// }
//
// if (crl != null) {
// // la salvo sul db
// crlHelper.upsertCRL(crl);
// }
//
// } catch (CryptoStorageException e) {
// logger.error("Warning update CA!", e);
// } catch (Exception e) {
// logger.error("Errore nell'aggionamento delle CA", e);
// } finally {
// IOUtils.closeQuietly(aIn);
// }
// }
// }
