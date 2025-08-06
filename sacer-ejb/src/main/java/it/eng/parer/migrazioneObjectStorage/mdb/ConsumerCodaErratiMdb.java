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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.parer.blob.info.BlobInfo;
import it.eng.parer.blob.info.ErroreInfo;
import it.eng.parer.blob.info.ErroreInfo.TipologiaErrore;
import it.eng.parer.blob.info.MigrazioneInfo;
import it.eng.parer.blob.info.PayLoad;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.entity.OstMigrazFileErr;
import it.eng.parer.migrazioneObjectStorage.helper.ConsumerCodaHelper;

/**
 *
 * @author Gilioli_P
 */
@MessageDriven(name = "ConsumerCodaErratiMdb", activationConfig = {
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/OggettiInErroreQueue") })
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaErratiMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda errati";

    Logger log = LoggerFactory.getLogger(ConsumerCodaErratiMdb.class);

    @Resource
    private MessageDrivenContext mdc;
    @EJB
    private ConsumerCodaHelper ccHelper;

    @Override
    public void onMessage(Message message) {

	try {
	    log.debug(String.format("%s: inizio lavorazione messaggio", DESC_CONSUMER));
	    TextMessage textMessage = (TextMessage) message;
	    String mex = textMessage.getText();
	    log.debug(String.format("%s [JSON] %s", DESC_CONSUMER, mex));
	    ObjectMapper om = new ObjectMapper();
	    PayLoad pl = om.readValue(mex, PayLoad.class);
	    BlobInfo blobboInfo = pl.getBlobInfo();
	    MigrazioneInfo migrazioneInfo = pl.getMigrazioneInfo();
	    ErroreInfo erroreInfo = pl.getErroreInfo();

	    // Estraggo le info che mi serviranno
	    Long idBlob = blobboInfo.getChiave().getIdBlob();
	    Long tsMigr = new Long(0);
	    if (migrazioneInfo != null) {
		tsMigr = migrazioneInfo.getTimeStamp();
	    }
	    TipologiaErrore tipologia = erroreInfo.getTipologia();
	    String codice = erroreInfo.getCodice();
	    String messaggio = erroreInfo.getMessaggio();
	    String hashCalcolato = erroreInfo.getHashCalcolato();
	    long timeStamp = erroreInfo.getTimeStamp();

	    // Assumo i lock esclusivi
	    OstMigrazFile migrazFile = ccHelper.getOstMigrazFileLocked("ARO_COMP_DOC",
		    BigDecimal.valueOf(idBlob));

	    // Se il file da migrare ha stato corrente MIGRAZ_IN_CORSO
	    if (migrazFile.getTiStatoCor()
		    .equals(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO
			    .name())) {
		//
		migrazFile.setTiStatoCor(
			it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_ERRORE
				.name());
		migrazFile.setTsRegStatoCor(new Date());
		if (tsMigr > 0L) {
		    migrazFile.setTsMigrato(new Date(tsMigr));
		}
		migrazFile.setNiMigrazErr(migrazFile.getNiMigrazErr().add(BigDecimal.ONE));

		// Registro in OST_MIGRAZ_FILE_ERR
		registraOstMigrazFileErr(migrazFile, codice, messaggio, hashCalcolato, timeStamp,
			tipologia.name());
	    }
	    log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
	} catch (JMSException | IOException ex) {
	    log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex),
		    ex);
	    mdc.setRollbackOnly();
	}
    }

    private void registraOstMigrazFileErr(OstMigrazFile migrazFile, String codice,
	    String descrizione, String hashCalc, long timeStamp, String tipologia) {
	OstMigrazFileErr migrazFileErr = new OstMigrazFileErr();
	migrazFileErr.setOstMigrazFile(migrazFile);
	migrazFileErr.setTsErr(new Date(timeStamp));
	migrazFileErr.setCdErr(codice);
	migrazFileErr.setDsErr(descrizione);
	migrazFileErr.setDsHashFileCalcMigraz(hashCalc);
	migrazFileErr.setTiErr(tipologia);
	ccHelper.insertEntity(migrazFileErr, true);
    }

}
