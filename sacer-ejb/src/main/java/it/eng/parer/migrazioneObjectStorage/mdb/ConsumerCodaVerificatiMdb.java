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

package it.eng.parer.migrazioneObjectStorage.mdb;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.blob.info.BlobInfo;
import it.eng.parer.blob.info.MigrazioneInfo;
import it.eng.parer.blob.info.PayLoad;
import it.eng.parer.blob.info.VerificaInfo;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompHashCalc;
import it.eng.parer.entity.AroCompObjectStorage;
import it.eng.parer.entity.DecBackend;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.migrazioneObjectStorage.helper.ConsumerCodaHelper;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;

/**
 *
 * @author Gilioli_P
 */
@MessageDriven(name = "ConsumerCodaVerificatiMdb", activationConfig = {
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/OggettiVerificatiQueue") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaVerificatiMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda verificati";
    private static final String BACKEND_OBJ_STO = "OBJECT_STORAGE_PRIMARIO";

    Logger log = LoggerFactory.getLogger(ConsumerCodaVerificatiMdb.class);

    @Resource
    private MessageDrivenContext mdc;
    @EJB
    private ConsumerCodaHelper ccHelper;
    @EJB
    private SalvataggioBackendHelper backendHelper;

    @Override
    public void onMessage(Message message) {

	try {
	    log.debug("{}: inizio lavorazione messaggio", DESC_CONSUMER);
	    TextMessage textMessage = (TextMessage) message;
	    String text = textMessage.getText();
	    log.debug("{} [JSON] {}", DESC_CONSUMER, text);
	    ObjectMapper om = new ObjectMapper();
	    PayLoad pl = om.readValue(text, PayLoad.class);
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
	    OstMigrazFile migrazFile = ccHelper.getOstMigrazFileLocked("ARO_COMP_DOC",
		    BigDecimal.valueOf(idBlob));
	    AroCompDoc compDoc = ccHelper.findByIdWithLock(AroCompDoc.class, idBlob);
	    // Se il file da migrare ha stato corrente MIGRAZ_IN_CORSO
	    if (migrazFile.getTiStatoCor()
		    .equals(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO
			    .name())) {
		//
		migrazFile.setTiStatoCor(
			it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRATO.name());
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

	    log.debug("{}: fine lavorazione messaggio", DESC_CONSUMER);
	} catch (JMSException | IOException ex) {
	    log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex),
		    ex);
	    mdc.setRollbackOnly();
	}
    }

    private void registraAroCompObjectStorage(AroCompDoc compDoc, OstMigrazFile migrazFile) {
	DecBackend backend = backendHelper.getBackendEntity(BACKEND_OBJ_STO);
	if (backend == null) {
	    throw new IllegalStateException(
		    "Non è stato configurato il DecBackend " + BACKEND_OBJ_STO);
	}
	AroCompObjectStorage compObjectStorage = new AroCompObjectStorage();
	compObjectStorage.setAroCompDoc(compDoc);
	compObjectStorage.setCdKeyFile(migrazFile.getCdKeyFile());
	compObjectStorage.setNmBucket(migrazFile.getNmBucket());
	compObjectStorage.setNmTenant(migrazFile.getNmTenant());
	compObjectStorage.setDecBackend(backend);
	ccHelper.insertEntity(compObjectStorage, true);
    }

    private void registraAroCompHashCalc(AroCompDoc compDoc, String dsAlgoHashFile,
	    String dsHashFile, String cdEncodingHashFile) {
	AroCompHashCalc compHashCalc = new AroCompHashCalc();
	compHashCalc.setAroCompDoc(compDoc);
	compHashCalc.setCdEncodingHashFile(cdEncodingHashFile);
	compHashCalc.setDsAlgoHashFile(dsAlgoHashFile);
	compHashCalc.setDsHashFile(dsHashFile);
	ccHelper.insertEntity(compHashCalc, true);
    }

}
