/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.job.codaIndiceAip.ejb;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
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

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.ElvVLisUdByStato;

/**
 *
 * @author DiLorenzo_F
 */
@MessageDriven(name = "ConsumerCodaIndiciAipDaElabMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/IndiciAIPUDDaElabQueue"),
        // MEV#29936
        @ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "1800")
        // end MEV#29936
})
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConsumerCodaIndiciAipDaElabMdb implements MessageListener {

    private static final String DESC_CONSUMER = "Consumer coda indici aip unità documentarie da elaborare";

    Logger log = LoggerFactory.getLogger(ConsumerCodaIndiciAipDaElabMdb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElaboraCodaIndiciAipDaElabEjb elaboraCodaIndiciAipDaElabEjb;
    @EJB
    private GenericHelper genericHelper;
    @Resource
    private MessageDrivenContext mdc;

    @Override
    public void onMessage(Message message) {

        try {
            log.debug(String.format("%s: inizio lavorazione messaggio", DESC_CONSUMER));
            TextMessage textMessage = (TextMessage) message;
            String statoElencoUd = textMessage.getStringProperty("statoElencoUd");
            String[] ids = textMessage.getText().split(",");
            long idElencoDaElab = Long.parseLong(ids[0]);
            long idUd = Long.parseLong(ids[1]);
            ElvElencoVersDaElab elencoDaElab = genericHelper.findById(ElvElencoVersDaElab.class,
                    new BigDecimal(idElencoDaElab));
            AroUnitaDoc ud = genericHelper.findById(AroUnitaDoc.class, new BigDecimal(idUd));
            if (elencoDaElab != null && ud != null) {
                // Modifica messa in sospeso perché forse bisognerà fare dei lock piu selettivi nella fase 2 !!
                // MAC #16385 - Job creazione indici AIP - stato elenco non coerente con stati UD (Lock sull'elenco)

                if (statoElencoUd.equals(ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB.name())) {
                    log.debug(String.format("Processo unità documentaria [%s] dell'elenco [%s] aventi stato [%s]", idUd,
                            elencoDaElab.getElvElencoVer().getIdElencoVers(), statoElencoUd));
                    // Verifico che l'unità doc appartenente all'elenco definiti nel payload del messaggio abbia stato =
                    // IN_CODA_JMS_INDICE_AIP_DA_ELAB e che l’elenco abbia stato IN_CODA_JMS_INDICE_AIP_DA_ELAB (vedi
                    // vista ELV_V_LIS_UD_BY_STATO)
                    if (evHelper.checkStatoElencoUdPerLeFasi(idUd, elencoDaElab.getElvElencoVer().getIdElencoVers(),
                            statoElencoUd)) {
                        // Recupero l'unità doc appartenente all'elenco il cui stato relativo all'elenco sia pari a
                        // IN_CODA_JMS_INDICE_AIP_DA_ELAB (serve per definire il motivo per cui l'unità documentaria è
                        // presente nell'elenco)
                        ElvVLisUdByStato unitaInElenco = evHelper.retrieveUdInElencoByStato(idUd,
                                elencoDaElab.getElvElencoVer().getIdElencoVers());
                        try {
                            elaboraCodaIndiciAipDaElabEjb.elaboraUDFase2(unitaInElenco, elencoDaElab);
                        } catch (ParerUserError ex) {
                            log.info(DESC_CONSUMER + " - " + ex.getMessage() + ", passo all'UD successiva");
                        }
                    }
                } else {
                    throw new EJBException(DESC_CONSUMER + ": errore, STATO non previsto!");
                }
            } else {
                log.debug(String.format(
                        "L'unità documentaria con ID %s o l'elenco da elaborare con ID %s per il messaggio con stato %s non esiste!",
                        idUd, idElencoDaElab, statoElencoUd));
            }
            log.debug(String.format("%s: fine lavorazione messaggio", DESC_CONSUMER));
        } catch (JMSException ex) {
            log.error("Errore nel consumer: JMSException " + ExceptionUtils.getRootCauseMessage(ex), ex);
            mdc.setRollbackOnly();
        }

    }
}
