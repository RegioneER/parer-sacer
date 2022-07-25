package it.eng.parer.migrazioneObjectStorage.mdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.parer.blob.info.BlobInfo;
import it.eng.parer.blob.info.MigrazioneInfo;
import it.eng.parer.blob.info.PayLoad;
import it.eng.parer.blob.info.VerificaInfo;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompHashCalc;
import it.eng.parer.entity.AroCompObjectStorage;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.migrazioneObjectStorage.helper.ConsumerCodaHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@MessageDriven(name = "ConsumerCodaVerificatiMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/OggettiVerificatiQueue") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaVerificatiMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda verificati";

    Logger log = LoggerFactory.getLogger(ConsumerCodaVerificatiMdb.class);

    @Resource
    private MessageDrivenContext mdc;
    @EJB
    private ConsumerCodaHelper ccHelper;

    @Override
    public void onMessage(Message message) {

        try {
            log.debug(String.format("%s: inizio lavorazione messaggio", DESC_CONSUMER));
            TextMessage textMessage = (TextMessage) message;
            String pippo = textMessage.getText();
            log.debug(String.format("%s [JSON] %s", DESC_CONSUMER, pippo));
            ObjectMapper om = new ObjectMapper();
            PayLoad pl = om.readValue(pippo, PayLoad.class);
            BlobInfo blobboInfo = pl.getBlobInfo();
            MigrazioneInfo migranteInfo = pl.getMigrazioneInfo();
            VerificaInfo verInfo = pl.getVerificaInfo();

            // Estraggo le info che mi serviranno
            long idBlob = blobboInfo.getChiave().getIdBlob();
            long tsMigrato = migranteInfo.getTimeStamp();
            String tipoHashDaCalcolare = blobboInfo.getTipoHashDaCalcolare();
            String hash = verInfo.getHash();
            String hashEncoding = verInfo.getHashEncoding();
            long tsVerificato = verInfo.getTimeStamp();

            // Assumo i lock esclusivi
            OstMigrazFile migrazFile = ccHelper.getOstMigrazFileLocked("ARO_COMP_DOC", BigDecimal.valueOf(idBlob));
            AroCompDoc compDoc = ccHelper.findByIdWithLock(AroCompDoc.class, idBlob);
            // Se il file da migrare ha stato corrente MIGRAZ_IN_CORSO
            if (migrazFile.getTiStatoCor()
                    .equals(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name())) {
                //
                migrazFile.setTiStatoCor(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRATO.name());
                migrazFile.setTsRegStatoCor(new Date());
                migrazFile.setTsMigrato(new Date(tsMigrato));
                migrazFile.setTsVerificato(new Date(tsVerificato));
                migrazFile.setNiMigrazErr(BigDecimal.ZERO);
                // Registro in ARO_COMP_OBJECT_STORAGE
                registraAroCompObjectStorage(compDoc, migrazFile);
                // se tipoHashDaCalcolare non nullo (=calcolo richiesto)
                if (StringUtils.isNotBlank(tipoHashDaCalcolare)) {
                    // Registro in ARO_COMP_HASH_CALC se l'hash per l'algoritmo non è già definito
                    registraAroCompHashCalc(compDoc, tipoHashDaCalcolare, hash, hashEncoding);
                }
                // Elimino gli errori rilevati sul file da migrare nel corso di tentativi precedenti
                ccHelper.deleteOstMigrazFileErrList(migrazFile.getIdMigrazFile());
            }

            log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
        } catch (JMSException | IOException ex) {
            log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex), ex);
            mdc.setRollbackOnly();
        }
    }

    private void registraAroCompObjectStorage(AroCompDoc compDoc, OstMigrazFile migrazFile) {
        AroCompObjectStorage compObjectStorage = new AroCompObjectStorage();
        compObjectStorage.setAroCompDoc(compDoc);
        compObjectStorage.setCdKeyFile(migrazFile.getCdKeyFile());
        compObjectStorage.setNmBucket(migrazFile.getNmBucket());
        compObjectStorage.setNmTenant(migrazFile.getNmTenant());
        ccHelper.insertEntity(compObjectStorage, true);
    }

    private void registraAroCompHashCalc(AroCompDoc compDoc, String dsAlgoHashFile, String dsHashFile,
            String cdEncodingHashFile) {
        AroCompHashCalc compHashCalc = new AroCompHashCalc();
        compHashCalc.setAroCompDoc(compDoc);
        compHashCalc.setCdEncodingHashFile(cdEncodingHashFile);
        compHashCalc.setDsAlgoHashFile(dsAlgoHashFile);
        compHashCalc.setDsHashFile(dsHashFile);
        ccHelper.insertEntity(compHashCalc, true);
    }

}
