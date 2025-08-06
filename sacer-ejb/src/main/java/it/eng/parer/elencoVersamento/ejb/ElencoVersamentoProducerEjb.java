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

package it.eng.parer.elencoVersamento.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.PayLoad;
import it.eng.parer.elencoVersamento.validation.CriterioRaggrValidation;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.inheritance.oop.ElvUdDocUpdDaElabElenco;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.Costanti;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.validation.Validator;
import org.apache.commons.collections4.iterators.IteratorChain;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class ElencoVersamentoProducerEjb {

    public static final String JMSX_GROUP_ID = "JMSXGroupID";
    public static final String TIPO_PAYLOAD = "tipoPayload";
    public static final String LOG_MESSAGE_SEND = "JmsProducer messaggio inviato con JMSXGroupID {}";
    public static final String LOG_MESSAGE_JSON = "JmsProducer [JSON] {}";
    Logger log = LoggerFactory.getLogger(ElencoVersamentoProducerEjb.class);

    @EJB
    private ElencoVersamentoHelper elencoHelper;

    @Inject
    private Validator validator;

    public ElencoVersamentoProducerEjb() {
	// serve?
    }

    @SuppressWarnings({
	    "rawtypes", "unchecked" })
    public void sendBatchJmsMessageGrouping(final Connection connection, final Session session,
	    final MessageProducer messageProducer, OrgStrut struttura, final int numMax,
	    final LogJob logJob, final Constants.TipoSessioneJMS jmsSession, final int jmsMsgChunk)
	    throws JMSException {

	// MAC#28020
	final Long countUdDaElab = elencoHelper.countUdDaElabToValidate(struttura.getIdStrut());
	final Long countDocDaElab = elencoHelper.countDocDaElabToValidate(struttura.getIdStrut());
	final Long countUpdDaElab = elencoHelper.countUpdDaElabToValidate(struttura.getIdStrut());

	int maxResultUd = (countUdDaElab > numMax) ? numMax : countUdDaElab.intValue();
	int maxResultDoc = (countDocDaElab > numMax) ? numMax : countDocDaElab.intValue();
	int maxResultUpd = (countUpdDaElab > numMax) ? numMax : countUpdDaElab.intValue();

	int size = maxResultUd + maxResultDoc + maxResultUpd;
	// end MAC#28020

	/*
	 * determino tutti i criteri di raggruppamento appartenenti alla struttura versante
	 * corrente, il cui intervallo (data istituzione - data soppressione) includa la data
	 * corrente (con estremi compresi); i criteri sono selezionati in ordine di data istituzione
	 */
	List<DecCriterioRaggr> criteriRaggrList = elencoHelper.retrieveCriterioByStrut(struttura,
		logJob.getDtRegLogJob());

	AtomicInteger totMessiInCoda = new AtomicInteger(0);
	final String jmsGroupId = struttura.getIdStrut().toString();

	for (DecCriterioRaggr criterio : criteriRaggrList) {

	    if (totMessiInCoda.get() < numMax) {

		AtomicBoolean isTheFirst = new AtomicBoolean(true);

		log.debug(
			"{} --- CAE - Invio del payload dei messaggi alla coda jms con elaborazione batch sequenziale...",
			JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());

		/*
		 * Determino le Unità  Documentarie, i Documenti Aggiunti e gli Aggiornamenti
		 * Metadati valide per il criterio e itero l'insieme
		 */
		IteratorChain<ElvUdDocUpdDaElabElenco> iteratorChain = new IteratorChain<>();

		if (maxResultUd > 0) {
		    iteratorChain.addIterator(elencoHelper
			    .retrieveUdToValidate(struttura.getIdStrut(), maxResultUd).iterator());
		}

		if (maxResultDoc > 0) {
		    iteratorChain.addIterator(
			    elencoHelper.retrieveDocToValidate(struttura.getIdStrut(), maxResultDoc)
				    .iterator());
		}

		if (maxResultUpd > 0) {
		    iteratorChain.addIterator(
			    elencoHelper.retrieveUpdToValidate(struttura.getIdStrut(), maxResultUpd)
				    .iterator());
		}

		StreamSupport
			.stream(Spliterators.spliterator(iteratorChain, size,
				Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL),
				false)
			.filter(obj -> validator
				.validate(
					new CriterioRaggrValidation(criterio, obj.getUdDocUpdObj(),
						obj.getAaKeyUnitaDoc(), obj.getDtCreazione()))
				.isEmpty())
			.forEach(obj -> {
			    Object validObj = obj.getUdDocUpdObj();
			    PayLoad pl = new PayLoad();
			    if (validObj instanceof AroUnitaDoc) {
				pl.setId(((AroUnitaDoc) validObj).getIdUnitaDoc());
				pl.setTipoEntitaSacer("UNI_DOC");
			    } else if (validObj instanceof AroDoc) {
				pl.setId(((AroDoc) validObj).getIdDoc());
				pl.setTipoEntitaSacer("DOC");
			    } else if (validObj instanceof AroUpdUnitaDoc) {
				pl.setId(((AroUpdUnitaDoc) validObj).getIdUpdUnitaDoc());
				pl.setTipoEntitaSacer("UPD");
			    }
			    pl.setStato(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
			    pl.setIdStrut(struttura.getIdStrut());
			    pl.setAaKeyUnitaDoc(obj.getAaKeyUnitaDoc().longValue());
			    pl.setDtCreazione(obj.getDtCreazione().getTime());
			    pl.setIdCriterio(criterio.getIdCriterioRaggr());
			    pl.setIdLogJob(logJob.getIdLogJob());
			    pl.setDtRegLogJob(logJob.getDtRegLogJob().getTime());
			    pl.setIsTheFirst(isTheFirst.get());
			    // TIP: invece di persistere il record di log "SET_ELENCO_APERTO" nella
			    // ELV_LOG_ELENCO_VERS
			    // per ogni messaggio, non lo scrivo affatto.
			    pl.setIsTheLast(false);

			    try {
				sendMessage(session, messageProducer, jmsGroupId, pl);

				if (jmsSession
					.equals(Constants.TipoSessioneJMS.SESSION_TRANSACTED)) {
				    commitChunk(session, totMessiInCoda.get(), jmsMsgChunk);
				}
			    } catch (JMSException ex) {
				throw new RuntimeException(String.format(
					"Errore nell'invio del messaggio con JMSXGroupID %s in coda",
					String.valueOf(jmsGroupId), ex));
			    } catch (JsonProcessingException ex) {
				throw new RuntimeException(
					"Errore nella serializzazione in JSON del messaggio per la coda",
					ex);
			    }

			    totMessiInCoda.set(totMessiInCoda.getAndIncrement());

			    isTheFirst.set(false);
			});

		if (jmsSession.equals(Constants.TipoSessioneJMS.SESSION_TRANSACTED)) {
		    commitLastChunk(session, totMessiInCoda.get(), jmsMsgChunk);
		}

		log.debug(
			"{} --- CAE - Invio del payload dei messaggi alla coda jms con elaborazione batch sequenziale... completato!",
			JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());
	    }
	}
    }

    public void sendMessage(final Session session, final MessageProducer messageProducer,
	    final String jmsGroupId, final PayLoad pl)
	    throws JMSException, JsonProcessingException {
	final TextMessage textMessage = session.createTextMessage();
	textMessage.setStringProperty(JMSX_GROUP_ID, jmsGroupId);
	// app selector
	textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Constants.SACER);
	textMessage.setStringProperty(TIPO_PAYLOAD, "CodaElenchiDaElab");
	ObjectMapper jsonMapper = new ObjectMapper();
	textMessage.setText(jsonMapper.writeValueAsString(pl));
	log.debug(String.format(LOG_MESSAGE_JSON, textMessage.getText()));
	messageProducer.send(textMessage);
	log.debug(String.format(LOG_MESSAGE_SEND, jmsGroupId));
    }

    public void commitChunk(final Session session, final Integer totMessiInCoda,
	    final int jmsMsgChunk) throws JMSException {
	if (totMessiInCoda != 0 && totMessiInCoda % jmsMsgChunk == 0) {
	    session.commit();
	}
    }

    public void commitLastChunk(final Session session, final Integer totMessiInCoda,
	    final int jmsMsgChunk) throws JMSException {
	if (totMessiInCoda != 0 && totMessiInCoda % jmsMsgChunk != 0) {
	    session.commit();
	}
    }
}
