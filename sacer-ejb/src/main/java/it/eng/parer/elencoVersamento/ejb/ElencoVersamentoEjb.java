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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObj;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObjComparatorAnnoDtCreazione;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObjComparatorDtCreazione;
import it.eng.parer.elencoVersamento.validation.CriterioRaggrValidation;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.ElvUrnElencoVers;
import it.eng.parer.entity.inheritance.oop.ElvUdDocUpdDaElabElenco;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.ElvVChkAddDocAgg;
import it.eng.parer.viewEntity.ElvVChkAddDocAggNoEleCor;
import it.eng.parer.viewEntity.ElvVChkAddUpdUd;
import it.eng.parer.viewEntity.ElvVChkAddUpdUdNoEleCor;
import it.eng.parer.viewEntity.ElvVSelUdDocUpdByCrit;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.DeliveryModeJMS;
import it.eng.parer.web.util.Constants.TipoSessioneJMS;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.commons.collections4.iterators.IteratorChain;

/**
 *
 * @author Agati_D
 * @author DiLorenzo_F
 */
@SuppressWarnings({
        "rawtypes", "unchecked" })
@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ElencoVersamentoEjb {

    public static final String EXPIRATION_LOG = "{} {} ";
    public static final String LOG_CAE_AUMENTO_DI_SCADENZA = "CAE - Aumento di {} {}. Scadenza = {}";
    public static final String LOG_ERR_CONF_CRIT = "ATTENZIONE non è possibile aggiungere l'ud '{}' all'elenco. Possibile errore nella definizione del criterio";
    public static final String DS_ELENCO_SCADUTO = "Elenco scaduto";
    public static final String TIPO_ELENCO_VERS_DA_CHIUDERE = "ELENCO_VERS_DA_CHIUDERE";
    public static final String DS_SUPERO_MAX_COMPONENTI = "L'aggiunta di una unitÃ  documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti";
    public static final String LOG_ELENCO_VUOTO = "Lâ€™aggiunta di una unitÃ  documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti";

    Logger log = LoggerFactory.getLogger(ElencoVersamentoEjb.class);
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
    @Resource
    private SessionContext context;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @Inject
    private Validator validator;

    // MEV#27891
    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/ProducerConnectionFactoryNonXA")
    private ConnectionFactory connectionFactoryNonXA;
    @Resource(mappedName = "jms/ProducerConnectionFactoryUntransacted")
    private ConnectionFactory connectionFactoryUntransacted;
    @Resource(mappedName = "jms/queue/ElenchiDaElabQueue")
    private Queue queue;

    @EJB
    private ElencoVersamentoProducerEjb elencoVersamentoProducerEjb;
    // end MEV#27891

    public ElencoVersamentoEjb() {
        // serve ?
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildElencoVersamento(LogJob logJob) throws Exception {
        log.info("CAE - Creazione automatica elenchi versamento...");

        /* MEV 15631 PER OTTENERE I DATI PER REGISTRARE */
        // Recupero la lista degli ambienti
        List<OrgAmbiente> ambienti = elencoHelper.retrieveAmbienti();
        for (OrgAmbiente ambiente : ambienti) {
            BigDecimal idAmbiente = BigDecimal.valueOf(ambiente.getIdAmbiente());

            // Ricavo i parametri
            final String DATA_SCAD_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplicByAmb(
                    CostantiDB.ParametroAppl.DATA_SCAD_CHIUSURA_ELV_FISC, idAmbiente);
            final String NI_GG_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplicByAmb(
                    CostantiDB.ParametroAppl.NI_GG_CHIUSURA_ELV_FISC, idAmbiente);
            final String ORARIO_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplicByAmb(
                    CostantiDB.ParametroAppl.ORARIO_CHIUSURA_ELV_FISC, idAmbiente);
            final String ANNO_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplicByAmb(
                    CostantiDB.ParametroAppl.ANNO_CHIUSURA_ELV_FISC, idAmbiente);
            // Anno e Orario corrente
            Calendar calCorrente = Calendar.getInstance();
            int annoCorrente = calCorrente.get(Calendar.YEAR);
            int oraCorrente = calCorrente.get(Calendar.HOUR_OF_DAY);
            int minutoCorrente = calCorrente.get(Calendar.MINUTE);

            int niGg = Integer.parseInt(NI_GG_CHIUSURA_ELV_FISC);
            int anno = Integer.parseInt(ANNO_CHIUSURA_ELV_FISC);

            // Data scad
            String dataScadChiusuraElvFisc = DATA_SCAD_CHIUSURA_ELV_FISC.concat("_" + annoCorrente)
                    .replace("_", "/");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cal.setTime(sdf.parse(dataScadChiusuraElvFisc));
            Date dataScadChiusuraElvFiscDate = cal.getTime();
            // Data scad - gg
            Calendar calMenoGg = Calendar.getInstance();
            calMenoGg.setTime(sdf.parse(dataScadChiusuraElvFisc));
            calMenoGg.add(Calendar.DATE, -niGg);
            Date dataScadChiusuraElvFiscDateMenoGg = calMenoGg.getTime();

            Date dataCorrente = new Date();
            // Se data corrente rientra nel range di date determinato
            if (dataCorrente.after(dataScadChiusuraElvFiscDateMenoGg)
                    && dataCorrente.before(dataScadChiusuraElvFiscDate)) {
                String[] orarioChiusuraElvFisc = ORARIO_CHIUSURA_ELV_FISC.split("_");
                int oraChiusura = Integer.parseInt(orarioChiusuraElvFisc[0]);
                int minutoChiusura = Integer.parseInt(orarioChiusuraElvFisc[1]);

                if (oraCorrente >= oraChiusura && minutoCorrente >= minutoChiusura) {
                    // ricavo le strutture List<OrgStrut>
                    List<OrgStrut> struttureByAmb = elencoHelper.retrieveStruttureByAmb(idAmbiente);

                    for (OrgStrut strut : struttureByAmb) {
                        List<ElvElencoVer> elenchi = evEjb
                                .getElenchiFiscaliByStrutturaAperti(strut.getIdStrut(), anno);
                        for (ElvElencoVer elenco : elenchi) {
                            ElencoVersamentoEjb newElencoEjbRef1 = context
                                    .getBusinessObject(ElencoVersamentoEjb.class);
                            log.debug(
                                    "CAV - trovato elenco {} fiscale da settare stato = DA_CHIUDERE",
                                    elenco.getIdElencoVers());
                            newElencoEjbRef1.setDaChiudereFiscAtomic(
                                    ElencoEnums.MotivazioneChiusura.ELENCO_CHIUSURA_ANTICIP
                                            .message(),
                                    elenco.getIdElencoVers(), strut.getIdStrut(),
                                    logJob.getIdLogJob());
                        }
                    }
                }
            }
        }

        // MEV#27891
        final boolean flAbilitaElenchiBatch = Boolean
                .parseBoolean(configurationHelper.getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.FL_ABILITA_JOB_ELENCHI_BATCH));

        final int numMaxInCoda = Integer.parseInt(configurationHelper.getValoreParamApplicByApplic(
                CostantiDB.ParametroAppl.NUM_MAX_IN_CODA_ELENCHI_BATCH));

        // XA_TRANSACTED, SESSION_TRANSACTED, UNTRANSACTED
        final String JMS_SESSION_ELENCHI_BATCH = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.JMS_SESSION_ELENCHI_BATCH);

        TipoSessioneJMS jmsSession = null;
        if (JMS_SESSION_ELENCHI_BATCH.equals("XA_TRANSACTED")) {
            jmsSession = TipoSessioneJMS.XA_TRANSACTED;
        } else if (JMS_SESSION_ELENCHI_BATCH.equals("SESSION_TRANSACTED")) {
            jmsSession = TipoSessioneJMS.SESSION_TRANSACTED;
        } else if (JMS_SESSION_ELENCHI_BATCH.equals("UNTRANSACTED")) {
            jmsSession = TipoSessioneJMS.UNTRANSACTED;
        } else {
            jmsSession = TipoSessioneJMS.DISABLED;
        }

        // usata solo se SESSION_TRANSACTED
        final int jmsMsgChunk = Integer.parseInt(configurationHelper.getValoreParamApplicByApplic(
                CostantiDB.ParametroAppl.JMS_MSG_CHUNK_ELENCHI_BATCH));

        final String JMS_MSG_DELIVERY_ELENCHI_BATCH = configurationHelper
                .getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.JMS_MSG_DELIVERY_ELENCHI_BATCH);

        final DeliveryModeJMS jmsMsgDelivery = (JMS_MSG_DELIVERY_ELENCHI_BATCH.equals("PERSISTENT"))
                ? DeliveryModeJMS.PERSISTENT
                : DeliveryModeJMS.NON_PERSISTENT;
        // end MEV#27891

        // ricavo le strutture
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

        log.debug("numero strutture trovate = {}", strutture.size());
        for (OrgStrut struttura : strutture) {
            log.debug("CAE - processo struttura: {}", struttura.getIdStrut());
            // MEV#27891
            if (flAbilitaElenchiBatch) {
                // MAC#28020
                if (!jmsSession.equals(TipoSessioneJMS.DISABLED)) {
                    manageStrutBatchJms(struttura.getIdStrut(), numMaxInCoda, logJob, jmsSession,
                            jmsMsgChunk, jmsMsgDelivery);
                } else {
                    manageStrutBatch(struttura.getIdStrut(), numMaxInCoda, logJob,
                            numMaxInCoda < 0);
                }
                // end MAC#28020
            } else {
                manageStrut(struttura.getIdStrut(), logJob);
            }
            // end MEV#27891
        }

        jobHelper.writeLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(),
                ElencoEnums.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    // MAC#28020
    public void manageStrutBatch(final long idStruttura, final int numMax, final LogJob logJob,
            final boolean filteredByView) throws Exception {

        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // gestisco gli elenchi scaduti
        log.info("{} --- CAE - Struttura: id = '{}' nome = '{}'",
                JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), idStruttura,
                struttura.getNmStrut());
        elaboraElenchiScaduti(idStruttura, logJob.getIdLogJob());

        ElencoVersamentoEjb newEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);
        if (filteredByView) {
            newEjbRef1.manageBatchFilteredByView(struttura, numMax, logJob);
        } else {
            newEjbRef1.manageBatchToValidate(struttura, numMax, logJob);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageBatchFilteredByView(final OrgStrut struttura, final int numMax,
            final LogJob logJob) throws Exception {

        /*
         * determino tutti i criteri di raggruppamento appartenenti alla struttura versante
         * corrente, il cui intervallo (data istituzione - data soppressione) includa la data
         * corrente (con estremi compresi); i criteri sono selezionati in ordine di data istituzione
         */
        List<DecCriterioRaggr> criteriRaggrList = elencoHelper.retrieveCriterioByStrut(struttura,
                logJob.getDtRegLogJob());

        for (DecCriterioRaggr criterio : criteriRaggrList) {
            log.debug(
                    "{} --- CAE - Criterio della struttura '{}' trovato: nome criterio = '{}' (id = '{}')",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), struttura.getNmStrut(),
                    criterio.getNmCriterioRaggr(), criterio.getIdCriterioRaggr());

            boolean isTheFirst = true;

            /* Definisco numero elenchi creati in un giorno nullo */
            Long numElenchi = null;
            /*
             * Determino se per il criterio il numero massimo di elenchi che si puÃ² creare in un
             * giorno e' non nullo
             */
            if (criterio.getNiMaxElenchiByGg() != null) {
                long countElenchiNonAperti = elencoHelper.countElenchiGgByCritNonAperti(
                        new BigDecimal(criterio.getIdCriterioRaggr()));
                long countElenchiAperti = elencoHelper
                        .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                    continue;
                } else {
                    numElenchi = countElenchiNonAperti + countElenchiAperti;
                }
            }

            log.info("{} --- CAE - Elaborazione batch...",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());

            /*
             * Determino le Unità  Documentarie, i Documenti Aggiunti e gli Aggiornamenti Metadati
             * valide per il criterio e itero l'insieme
             */
            Iterator<ElvVSelUdDocUpdByCrit> i = elencoHelper
                    .streamUpdDocUdToProcess(criterio, numMax).iterator();
            try {
                // Itero l'insieme
                while (i.hasNext()) {
                    // Recupera l'elemento e sposta il cursore all'elemento successivo
                    ElvVSelUdDocUpdByCrit o = i.next();
                    // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"),
                    // mi serve per
                    // capire se è l'ultimo elemento
                    numElenchi = context.getBusinessObject(ElencoVersamentoEjb.class)
                            .manageUpdDocUdBatchFilteredByView(criterio.getIdCriterioRaggr(),
                                    struttura.getIdStrut(), logJob.getIdLogJob(), o, !i.hasNext(),
                                    isTheFirst, numElenchi);
                    if (numElenchi != null
                            && numElenchi > criterio.getNiMaxElenchiByGg().longValue()) {
                        // Passa al criterio successivo
                        break;
                    }

                    isTheFirst = false;
                }
            } catch (ParerInternalError ex) {
                log.warn(
                        "Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
            }

            log.info("{} --- CAE - Elaborazione batch... completata!",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageBatchToValidate(final OrgStrut struttura, final int numMax,
            final LogJob logJob) throws Exception {

        final Long countUdDaElab = elencoHelper.countUdDaElabToValidate(struttura.getIdStrut());
        final Long countDocDaElab = elencoHelper.countDocDaElabToValidate(struttura.getIdStrut());
        final Long countUpdDaElab = elencoHelper.countUpdDaElabToValidate(struttura.getIdStrut());

        int maxResultUd = (countUdDaElab > numMax) ? numMax : countUdDaElab.intValue();
        int maxResultDoc = (countDocDaElab > numMax) ? numMax : countDocDaElab.intValue();
        int maxResultUpd = (countUpdDaElab > numMax) ? numMax : countUpdDaElab.intValue();

        /*
         * determino tutti i criteri di raggruppamento appartenenti alla struttura versante
         * corrente, il cui intervallo (data istituzione - data soppressione) includa la data
         * corrente (con estremi compresi); i criteri sono selezionati in ordine di data istituzione
         */
        List<DecCriterioRaggr> criteriRaggrList = elencoHelper.retrieveCriterioByStrut(struttura,
                logJob.getDtRegLogJob());

        for (DecCriterioRaggr criterio : criteriRaggrList) {
            log.debug(
                    "{} --- CAE - Criterio della struttura '{}' trovato: nome criterio = '{}' (id = '{}')",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), struttura.getNmStrut(),
                    criterio.getNmCriterioRaggr(), criterio.getIdCriterioRaggr());

            Boolean isTheFirst = true;

            /* Definisco numero elenchi creati in un giorno nullo */
            Long numElenchi = null;

            /*
             * Determino se per il criterio il numero massimo di elenchi che si puÃ² creare in un
             * giorno e' non nullo
             */
            if (criterio.getNiMaxElenchiByGg() != null) {
                long countElenchiNonAperti = elencoHelper.countElenchiGgByCritNonAperti(
                        new BigDecimal(criterio.getIdCriterioRaggr()));
                long countElenchiAperti = elencoHelper
                        .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                    continue;
                } else {
                    numElenchi = countElenchiNonAperti + countElenchiAperti;
                }
            }

            log.info("{} --- CAE - Elaborazione batch...",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());

            /*
             * Determino le Unità  Documentarie, i Documenti Aggiunti e gli Aggiornamenti Metadati
             * valide per il criterio e itero l'insieme
             */
            IteratorChain<ElvUdDocUpdDaElabElenco> iteratorChain = new IteratorChain();

            if (maxResultUd > 0) {
                iteratorChain.addIterator(elencoHelper
                        .retrieveUdToValidate(struttura.getIdStrut(), maxResultUd).iterator());
            }

            if (maxResultDoc > 0) {
                iteratorChain.addIterator(elencoHelper
                        .retrieveDocToValidate(struttura.getIdStrut(), maxResultDoc).iterator());
            }

            if (maxResultUpd > 0) {
                iteratorChain.addIterator(elencoHelper
                        .retrieveUpdToValidate(struttura.getIdStrut(), maxResultUpd).iterator());
            }

            try {
                // Itero l'insieme
                while (iteratorChain.hasNext()) {
                    // Recupera l'elemento e sposta il cursore all'elemento successivo
                    ElvUdDocUpdDaElabElenco obj = iteratorChain.next();
                    // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"),
                    // mi serve per
                    // capire se è l'ultimo elemento
                    numElenchi = context.getBusinessObject(ElencoVersamentoEjb.class)
                            .manageUpdDocUdBatchToValidate(criterio, struttura.getIdStrut(),
                                    logJob.getIdLogJob(), obj, !iteratorChain.hasNext(), isTheFirst,
                                    numElenchi);
                    if (numElenchi != null
                            && numElenchi > criterio.getNiMaxElenchiByGg().longValue()) {
                        // Passa al criterio successivo
                        break;
                    }

                    isTheFirst = false;
                }
            } catch (ParerInternalError ex) {
                log.warn(
                        "Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
            }

            log.info("{} --- CAE - Elaborazione batch... completata!",
                    JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());
        }
    }
    // end MAC#28020

    // MEV#27891
    public void manageStrutBatchJms(final long idStruttura, final int numMax, final LogJob logJob,
            final TipoSessioneJMS jmsSession, final int jmsMsgChunk,
            final DeliveryModeJMS jmsMsgDelivery) throws Exception {

        Connection connection = null;
        Session session = null;
        MessageProducer messageProducer = null;

        try {

            switch (jmsSession) {
            case SESSION_TRANSACTED:
                connection = connectionFactoryNonXA.createConnection();
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
                break;
            case UNTRANSACTED:
                connection = connectionFactoryUntransacted.createConnection();
                session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
                break;
            case XA_TRANSACTED:
            default:
                connection = connectionFactory.createConnection();
                session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                break;
            }

            messageProducer = session.createProducer(queue);
            messageProducer
                    .setDeliveryMode((Constants.DeliveryModeJMS.PERSISTENT.equals(jmsMsgDelivery))
                            ? DeliveryMode.PERSISTENT
                            : DeliveryMode.NON_PERSISTENT);
            messageProducer.setPriority(0);

            ElencoVersamentoEjb newEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);
            newEjbRef1.sendBatchJms(connection, session, messageProducer, idStruttura, numMax,
                    logJob, jmsSession, jmsMsgChunk);

        } catch (JMSException ex) {
            throw new RuntimeException(String.format(
                    "Errore nell'invio dei messaggi con JMSXGroupID %s in coda", idStruttura));
        } finally {
            try {
                if (messageProducer != null) {
                    messageProducer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException ex) {
                log.error("Errore (trappato) JMS durante la chiusura delle risorse", ex);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendBatchJms(final Connection connection, final Session session,
            final MessageProducer messageProducer, final long idStruttura, final int numMax,
            final LogJob logJob, final TipoSessioneJMS jmsSession, final int jmsMsgChunk)
            throws Exception {

        log.info(
                "{} --- CAE - Invio dei messaggi in coda per tutti gli oggetti da processare con stato = NON_SELEZ_SCHED in modalità batch",
                JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS);

        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // gestisco gli elenchi scaduti
        log.info("{} --- CAE - Struttura: id = '{}' nome = '{}'",
                JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS, idStruttura, struttura.getNmStrut());
        elaboraElenchiScaduti(idStruttura, logJob.getIdLogJob());

        elencoVersamentoProducerEjb.sendBatchJmsMessageGrouping(connection, session,
                messageProducer, struttura, numMax, logJob, jmsSession, jmsMsgChunk);
    }
    // end MEV#27891

    public void manageStrut(long idStruttura, LogJob logJob) throws Exception {
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // gestisco gli elenchi scaduti
        log.info("CAE - Struttura: id = '{}' nome = '{}'", idStruttura, struttura.getNmStrut());
        elaboraElenchiScaduti(idStruttura, logJob.getIdLogJob());
        /*
         * determino tutti i criteri di raggruppamento appartenenti alla struttura versante
         * corrente, il cui intervallo (data istituzione - data soppressione) includa la data
         * corrente (con estremi compresi); i criteri sono selezionati in ordine di data istituzione
         */
        List<DecCriterioRaggr> criteriRaggr = elencoHelper.retrieveCriterioByStrut(struttura,
                logJob.getDtRegLogJob());
        for (DecCriterioRaggr criterio : criteriRaggr) {
            log.debug(
                    "CAE - Criterio della struttura '{}' trovato: nome criterio = '{}' (id = '{}')",
                    struttura.getNmStrut(), criterio.getNmCriterioRaggr(),
                    criterio.getIdCriterioRaggr());

            /* Definisco numero elenchi creati in un giorno nullo */
            Long numElenchi = null;
            /*
             * Determino se per il criterio il numero massimo di elenchi che si puÃ² creare in un
             * giorno e' non nullo
             */
            if (criterio.getNiMaxElenchiByGg() != null) {
                long countElenchiNonAperti = elencoHelper.countElenchiGgByCritNonAperti(
                        new BigDecimal(criterio.getIdCriterioRaggr()));
                long countElenchiAperti = elencoHelper
                        .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                    continue;
                } else {
                    numElenchi = countElenchiNonAperti + countElenchiAperti;
                }
            }

            Comparator<UpdDocUdObj> comp = new UpdDocUdObjComparatorDtCreazione();
            if (criterio.getAaKeyUnitaDoc() == null && criterio.getAaKeyUnitaDocDa() == null
                    && criterio.getAaKeyUnitaDocA() == null) {
                comp = new UpdDocUdObjComparatorAnnoDtCreazione();
            }

            /*
             * Determino le UnitÃ  Documentarie, i Documenti Aggiunti e gli Aggiornamenti Metadati
             * che soddisfano il criterio corrente
             */
            List<UpdDocUdObj> updDocUdObjectList = elencoHelper.retrieveUpdDocUdToProcess(criterio);
            log.debug("CAE - Trovati {} oggetti versati relativi al criterio '{}'",
                    updDocUdObjectList.size(), criterio.getNmCriterioRaggr());

            Collections.sort(updDocUdObjectList, comp);

            ElencoVersamentoEjb newElencoEjbRef1 = context
                    .getBusinessObject(ElencoVersamentoEjb.class);
            boolean isTheFirst = true;
            try {
                // Itero l'insieme
                Iterator<UpdDocUdObj> i = updDocUdObjectList.iterator();
                while (i.hasNext()) {
                    // Recupera l'elemento e sposta il cursore all'elemento successivo
                    UpdDocUdObj o = i.next();
                    // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"),
                    // mi serve per capire
                    // se Ã¨ l'ultimo elemento
                    numElenchi = newElencoEjbRef1.manageUpdDocUdObj(criterio.getIdCriterioRaggr(),
                            struttura.getIdStrut(), logJob.getIdLogJob(), o, !i.hasNext(),
                            isTheFirst, numElenchi);
                    if (numElenchi != null
                            && numElenchi > criterio.getNiMaxElenchiByGg().longValue()) {
                        // Passa al criterio successivo
                        break;
                    }
                    isTheFirst = false;
                }
            } catch (ParerInternalError ex) {
                log.warn(
                        "Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
            }
        }
        elaboraElenchiVuoti(idStruttura);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long manageUpdDocUdObj(long idCriterio, long idStruttura, Long idLogJob,
            UpdDocUdObj updDocUdObj, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        Long numElenchiTmp = null;
        switch (updDocUdObj.getTiEntitaSacer()) {
        case UNI_DOC:
            numElenchiTmp = manageUd(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(),
                    idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        case DOC:
            numElenchiTmp = manageDoc(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(),
                    idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        case UPD:
            numElenchiTmp = manageUpd(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(),
                    idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        }

        return numElenchiTmp;
    }

    // MAC#28020
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long manageUpdDocUdBatchFilteredByView(long idCriterio, long idStruttura, Long idLogJob,
            ElvVSelUdDocUpdByCrit updDocUdObj, boolean isTheLast, boolean isTheFirst,
            Long numElenchi) throws Exception {
        Long numElenchiTmp = null;

        switch (updDocUdObj.getTiEle()) {
        case "01_UNI_DOC":
            numElenchiTmp = manageUd(updDocUdObj.getElvVSelUdDocUpdByCritId().getIdUnitaDoc(),
                    updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura, idLogJob, isTheLast,
                    isTheFirst, numElenchi);
            break;
        case "02_DOC_AGG":
            numElenchiTmp = manageDoc(updDocUdObj.getElvVSelUdDocUpdByCritId().getIdDoc(),
                    updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura, idLogJob, isTheLast,
                    isTheFirst, numElenchi);
            break;
        case "03_UPD_UD":
            numElenchiTmp = manageUpd(updDocUdObj.getElvVSelUdDocUpdByCritId().getIdUpdUnitaDoc(),
                    updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura, idLogJob, isTheLast,
                    isTheFirst, numElenchi);
            break;
        }

        return numElenchiTmp;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long manageUpdDocUdBatchToValidate(final DecCriterioRaggr criterio, long idStruttura,
            Long idLogJob, ElvUdDocUpdDaElabElenco<?> updDocUdObj, boolean isTheLast,
            boolean isTheFirst, Long numElenchi) throws Exception {
        Long numElenchiTmp = null;

        Object obj = updDocUdObj.getUdDocUpdObj();
        if (obj instanceof AroUnitaDoc) {
            AroUnitaDoc ud = (AroUnitaDoc) obj;
            CriterioRaggrValidation<AroUnitaDoc> critRaggrValidation = new CriterioRaggrValidation<>(
                    criterio, ud, updDocUdObj.getAaKeyUnitaDoc(), updDocUdObj.getDtCreazione());
            Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                    .validate(critRaggrValidation);
            if (violations.isEmpty()) {
                numElenchiTmp = manageUd(new BigDecimal(ud.getIdUnitaDoc()),
                        updDocUdObj.getAaKeyUnitaDoc(), criterio.getIdCriterioRaggr(), idStruttura,
                        idLogJob, isTheLast, isTheFirst, numElenchi);
            }
        } else if (obj instanceof AroDoc) {
            AroDoc doc = (AroDoc) obj;
            CriterioRaggrValidation critRaggrValidation = new CriterioRaggrValidation(criterio, doc,
                    updDocUdObj.getAaKeyUnitaDoc(), updDocUdObj.getDtCreazione());
            Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                    .validate(critRaggrValidation);
            if (violations.isEmpty()) {
                numElenchiTmp = manageDoc(new BigDecimal(doc.getIdDoc()),
                        updDocUdObj.getAaKeyUnitaDoc(), criterio.getIdCriterioRaggr(), idStruttura,
                        idLogJob, isTheLast, isTheFirst, numElenchi);
            }
        } else if (obj instanceof AroUpdUnitaDoc) {
            AroUpdUnitaDoc upd = (AroUpdUnitaDoc) obj;
            CriterioRaggrValidation critRaggrValidation = new CriterioRaggrValidation(criterio, upd,
                    updDocUdObj.getAaKeyUnitaDoc(), updDocUdObj.getDtCreazione());
            Set<ConstraintViolation<CriterioRaggrValidation>> violations = validator
                    .validate(critRaggrValidation);
            if (violations.isEmpty()) {
                numElenchiTmp = manageUpd(new BigDecimal(upd.getIdUpdUnitaDoc()),
                        updDocUdObj.getAaKeyUnitaDoc(), criterio.getIdCriterioRaggr(), idStruttura,
                        idLogJob, isTheLast, isTheFirst, numElenchi);
            }
        }

        return numElenchiTmp;
    }
    // end MAC#28020

    // MEV#27169
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUdJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        this.manageUd(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst,
                numElenchi);
    }
    // end MEV#27169

    private Long manageUd(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroUnitaDoc ud = elencoHelper.retrieveUnitaDocById(udId.longValue());

        /* a) Prendo il LOCK esclusivo su ud */
        elencoHelper.lockUnitaDoc(ud);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il
                // numero massimo di elenchi
                // che si puÃ² creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo ud corrente all'elenco corrente */
        // calcolo il numero di documenti, il numero di componenti e la somma dei byte dei
        // componenti, della ud
        // corrente,
        // relativamente ai soli documenti con tipo creazione = VERSAMENTO_UNITA_DOC
        long numDocs = elencoHelper.countDocsInUnitaDocCustom(new BigDecimal(ud.getIdUnitaDoc()));
        Object[] numSizeArray = (Object[]) elencoHelper
                .numCompsAndSizeInUnitaDocCustom(new BigDecimal(ud.getIdUnitaDoc()));
        long numComps = (long) numSizeArray[0];
        BigDecimal sizeComps = (BigDecimal) numSizeArray[1];
        // ATTENZIONE: verifico se il numero di componenti della unitÃ  documentaria corrente,
        // Ã¨ inferiore o uguale al numero di componenti che l'elenco corrente puÃ² ancora includere
        // (tale numero Ã¨ definito dal numero massimo di componenti previsto dall'elenco a cui si
        // sottrae il numero di componenti derivanti da unitÃ  doc versate giÃ  incluse nell'elenco)
        boolean firstCheckUdOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
        if (firstCheckUdOk) { // l'ud sta nell'elenco: aggiungo.
            log.debug("aggiungo l'unita documentaria '{}' all'elenco", ud.getIdUnitaDoc());
            /* Aggiunta ud */
            addUnitaDocIntoElenco(ud, elenco, numDocs, numComps, sizeComps, struttura, logJob);
        } else if (elencoHelper.containsElenco(elenco)) {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco,
                    struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_SUPERO_MAX_COMPONENTI, tiStatoelenco, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si puÃ²
                // creare in un giorno
                // ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo
                // volume Ã¨
                // managed
            }
            // Aggiugo unitÃ  doc ad elenco sono dopo aver controllato se ci sta. Se non ci sta Ã¨
            // un problema di
            // configurazione del criterio
            boolean secondCheckUdOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
            if (secondCheckUdOk) { // l'ud sta nell'elenco: aggiugo.
                log.debug("aggiungo l'unita documentaria '{}' all'elenco", ud.getIdUnitaDoc());
                addUnitaDocIntoElenco(ud, elenco, numDocs, numComps, sizeComps, struttura, logJob);
            } else {
                log.warn(LOG_ERR_CONF_CRIT, ud.getIdUnitaDoc());
                throw new ParerInternalError(
                        "ATTENZIONE non Ã¨ possibile aggiungere l'ud '" + ud.getIdUnitaDoc()
                                + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        } else {
            log.warn(LOG_ERR_CONF_CRIT, ud.getIdUnitaDoc());
            throw new ParerInternalError("ATTENZIONE non Ã¨ possibile aggiungere l'ud '"
                    + ud.getIdUnitaDoc()
                    + "' all'elenco vuoto. Possibile errore nella definizione del criterio");
        }

        /* f) Verifico se l'elenco corrente Ã¨ scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_ELENCO_SCADUTO, tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente Ã¨ l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast
                && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    // MEV#27169
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageDocJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        this.manageDoc(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast,
                isTheFirst, numElenchi);
    }
    // end MEV#27169

    private Long manageDoc(BigDecimal idDoc, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroDoc doc = elencoHelper.retrieveDocById(idDoc.longValue());
        /* a) prendo LOCK escusivo su doc */
        elencoHelper.lockDoc(doc);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il
                // numero massimo di elenchi
                // che si puÃ² creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo doc corrente all'elenco corrente */
        // calcolo il numero di componenti e la somma dei byte dei componenti, del documento
        // corrente
        Object[] numSizeArray = (Object[]) elencoHelper
                .numCompsAndSizeInDoc(new BigDecimal(doc.getIdDoc()));
        long numComps = (long) numSizeArray[0];
        BigDecimal sizeComps = (BigDecimal) numSizeArray[1];
        // ATTENZIONE: verifico se il numero di componenti del documento corrente, Ã¨ inferiore o
        // uguale al numero di
        // componenti che l'elenco puÃ² ancora includere
        // (tale numero Ã¨ definito dal numero massimo di componenti previsto dal volume a cui si
        // sottrae
        // il numero di componenti derivanti da unitÃ  doc versate giÃ  incluse nell'elenco, il
        // numero di componenti
        // derivanti da documenti giÃ  inclusi nell'elenco
        // ed il numero di aggiornamento metadati per unitÃ  doc giÃ  inclusi nellâ€™elenco)
        boolean firstCheckDocOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
        if (firstCheckDocOk) { // il doc sta nell'elenco: aggiungo.
            log.debug("aggiungo documento '{}' all'elenco", doc.getIdDoc());
            /* Aggiunta doc */
            addDocIntoElenco(doc, elenco, numComps, sizeComps, struttura, logJob);
        } else if (elencoHelper.containsElenco(elenco)) {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco,
                    struttura, logJob);
            // EVO 19304

            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_SUPERO_MAX_COMPONENTI,
                    ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si puÃ²
                // creare in un giorno
                // ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo
                // volume Ã¨
                // managed
            }
            // Aggiungo il doc ad elenco solo dopo aver controllato se ci sta. Se non ci sta Ã¨ un
            // problema di
            // configurazione del criterio
            boolean secondCheckDocOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
            if (secondCheckDocOk) { // il doc sta nell'elenco: aggiungo.
                log.debug("aggiungo documento '{}' all'elenco", doc.getIdDoc());
                addDocIntoElenco(doc, elenco, numComps, sizeComps, struttura, logJob);
            } else {
                log.warn(LOG_ERR_CONF_CRIT, doc.getIdDoc());
                throw new ParerInternalError(
                        "ATTENZIONE non Ã¨ possibile aggiungere il doc '" + doc.getIdDoc()
                                + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        } else {
            log.warn(LOG_ERR_CONF_CRIT, doc.getIdDoc());
            throw new ParerInternalError("ATTENZIONE non Ã¨ possibile aggiungere il doc '"
                    + doc.getIdDoc()
                    + "' all'elenco vuoto. Possibile errore nella definizione del criterio");
        }

        /* f) Verifico se l'elenco corrente Ã¨ scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_ELENCO_SCADUTO, tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente Ã¨ l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast
                && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    // MEV#27169
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageUpdJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        this.manageUpd(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast,
                isTheFirst, numElenchi);
    }
    // end MEV#27169

    private Long manageUpd(BigDecimal idUpdUnitaDoc, BigDecimal aaKeyUnitaDoc, long idCriterio,
            long idStruttura, Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi)
            throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroUpdUnitaDoc upd = elencoHelper.retrieveUpdById(idUpdUnitaDoc.longValue());
        /* a) prendo LOCK escusivo su upd */
        elencoHelper.lockUpd(upd);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il
                // numero massimo di elenchi
                // che si puÃ² creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo upd corrente all'elenco corrente */
        // ATTENZIONE: verifico se il numero di componenti che l'elenco puÃ² ancora includere
        // (tale numero Ã¨ definito dal numero massimo di componenti previsto dal volume a cui si
        // sottrae
        // il numero di componenti derivanti da unitÃ  doc versate giÃ  incluse nell'elenco, il
        // numero di componenti
        // derivanti da documenti giÃ  inclusi nell'elenco
        // ed il numero di aggiornamento metadati per unitÃ  doc giÃ  inclusi nellâ€™elenco) e'
        // maggiore o uguale ad 1
        boolean firstCheckUpdOk = elencoHelper.checkFreeSpaceElenco(elenco, 1L);
        if (firstCheckUpdOk) { // la upd sta nell'elenco: aggiungo.
            log.debug("aggiungo aggiornamento metadati'{}' all'elenco", upd.getIdUpdUnitaDoc());
            /* Aggiunta upd */
            addUpdIntoElenco(upd, elenco, struttura, logJob);
        } else if (elencoHelper.containsElenco(elenco)) {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco,
                    struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_SUPERO_MAX_COMPONENTI, tiStatoelenco, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si puÃ²
                // creare in un giorno
                // ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo
                // volume Ã¨
                // managed
            }
            // Aggiungo la upd ad elenco solo dopo aver controllato se ci sta. Se non ci sta Ã¨ un
            // problema di
            // configurazione del criterio
            boolean secondCheckUpdOk = elencoHelper.checkFreeSpaceElenco(elenco, 1L);
            if (secondCheckUpdOk) { // la upd sta nell'elenco: aggiungo.
                log.debug("aggiungo aggiornamento metadati '{}' all'elenco",
                        upd.getIdUpdUnitaDoc());
                addUpdIntoElenco(upd, elenco, struttura, logJob);
            } else {
                log.warn(LOG_ERR_CONF_CRIT, upd.getIdUpdUnitaDoc());
                throw new ParerInternalError(
                        "ATTENZIONE non Ã¨ possibile aggiungere la upd '" + upd.getIdUpdUnitaDoc()
                                + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        } else {
            log.warn(LOG_ERR_CONF_CRIT, upd.getIdUpdUnitaDoc());
            throw new ParerInternalError("ATTENZIONE non Ã¨ possibile aggiungere la upd '"
                    + upd.getIdUpdUnitaDoc()
                    + "' all'elenco vuoto. Possibile errore nella definizione del criterio");
        }

        /* f) Verifico se l'elenco corrente Ã¨ scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    TIPO_ELENCO_VERS_DA_CHIUDERE, DS_ELENCO_SCADUTO, tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente Ã¨ l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast
                && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    public ElvElencoVer retrieveElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc,
            OrgStrut struttura, LogJob logJob, boolean isTheFirst) throws Exception {
        ElvElencoVer elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob,
                isTheFirst);
        if (elenco == null) {
            // non ci sono elenchi aperti quindi ne creo uno nuovo
            elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura);
        }
        return elenco;
    }

    public ElvElencoVer findOpenedElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc,
            OrgStrut struttura, LogJob logJob, boolean isTheFirst) throws Exception {
        ElvElencoVer elenco = null;
        // Recupero l'elenco aperto per il criterio corrente
        try {
            elenco = elencoHelper.retrieveElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo
            // elenco
            // Ã¨
            // managed
            BigDecimal numCompVers = elenco.getNiCompVersElenco();
            BigDecimal numCompAgg = elenco.getNiCompAggElenco();
            BigDecimal numUpdUd = elenco.getNiUpdUnitaDoc();
            int sommaCompAggCompVersUpd = numCompVers.intValue() + numCompAgg.intValue()
                    + numUpdUd.intValue();
            log.debug(
                    "CAE - Elenco aperto trovato: nome = {}; data di scadenza = {}; numero componenti versati = {};"
                            + " numero componenti aggiunti= {} ; numero aggiornamenti metadati= {}; per un totale di {}",
                    elenco.getNmElenco(), dateToString(elenco.getDtScadChius()),
                    elenco.getNiCompVersElenco(), elenco.getNiCompAggElenco(),
                    elenco.getNiUpdUnitaDoc(), sommaCompAggCompVersUpd);
            // Registro nel log solo se Ã¨ il primo elemento, non ogni volta che passo di qua
            if (isTheFirst && logJob != null) {
                elencoHelper.writeLogElencoVers(elenco, struttura,
                        ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_APERTO.name(), logJob);
            }
        } catch (ParerNoResultException ex) {
            elenco = null;
        }
        return elenco;
    }

    public ElvElencoVer createNewElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc,
            OrgStrut struttura) {
        ElvElencoVer elenco;
        log.debug("CAE - Nessun elenco aperto trovato. Ne creo uno nuovo");
        elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo volume Ã¨
        // managed
        return elenco;
    }

    private ElvElencoVer createElencoByCriterio(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc,
            OrgStrut struttura) {
        log.debug("CEC - Crea elenco da criterio");
        Date systemDate = new Date();
        ElvElencoVer elenco = new ElvElencoVer();
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.APERTO.name());
        // se il nome dell'elenco Ã¨ troppo lungo lo tronco per evitare problemi con il DB
        String nome = null;
        String descrizione = null;
        int nameLimit = 66;
        int descLimit = 250;
        boolean tuttiAnniKeyUnitaDocNulli = criterio.getAaKeyUnitaDoc() == null
                && criterio.getAaKeyUnitaDocDa() == null && criterio.getAaKeyUnitaDocA() == null;
        // Eventualmente tronca onde evitare problemi di scrittura sul DB per nomi troppo lunghi
        if (tuttiAnniKeyUnitaDocNulli) {
            if (criterio.getNmCriterioRaggr().length() > nameLimit) {
                nome = criterio.getNmCriterioRaggr().substring(0, nameLimit) + "_" + aaKeyUnitaDoc
                        + "_" + dateToString(systemDate);
            } else {
                nome = criterio.getNmCriterioRaggr() + "_" + aaKeyUnitaDoc + "_"
                        + dateToString(systemDate);
            }
            if (criterio.getDsCriterioRaggr().length() > descLimit) {
                descrizione = criterio.getDsCriterioRaggr().substring(0, descLimit) + "_"
                        + aaKeyUnitaDoc;
            } else {
                descrizione = criterio.getDsCriterioRaggr() + "_" + aaKeyUnitaDoc;
            }
        } else {
            nameLimit = 70;
            if (criterio.getNmCriterioRaggr().length() > nameLimit) {
                nome = criterio.getNmCriterioRaggr().substring(0, nameLimit) + "_"
                        + dateToString(systemDate);
            } else {
                nome = criterio.getNmCriterioRaggr() + "_" + dateToString(systemDate);
            }
            descrizione = criterio.getDsCriterioRaggr();
            elenco.setAaKeyUnitaDoc(aaKeyUnitaDoc);
        }

        elenco.setNmElenco(nome);
        elenco.setDsElenco(descrizione);

        elenco.setOrgStrut(struttura);
        elenco.setDtCreazioneElenco(systemDate);

        elenco.setNiMaxComp(criterio.getNiMaxComp());

        elenco.setTiScadChius(criterio.getTiScadChiusVolume());
        elenco.setNiTempoScadChius(criterio.getNiTempoScadChius());
        elenco.setTiTempoScadChius(criterio.getTiTempoScadChius());
        elenco.setDecCriterioRaggr(criterio);
        elenco.setNiSizeVersElenco(BigDecimal.ZERO);
        elenco.setNiUnitaDocVersElenco(BigDecimal.ZERO);
        elenco.setNiDocVersElenco(BigDecimal.ZERO);
        elenco.setNiCompVersElenco(BigDecimal.ZERO);
        elenco.setNiUpdUnitaDoc(BigDecimal.ZERO);
        elenco.setNiSizeAggElenco(BigDecimal.ZERO);
        elenco.setNiUnitaDocModElenco(BigDecimal.ZERO);
        elenco.setNiDocAggElenco(BigDecimal.ZERO);
        elenco.setNiCompAggElenco(BigDecimal.ZERO);

        // Calcola la data di scadenza dell'elenco
        Date expirationDate = calculateExpirationDate(elenco);
        elenco.setDtScadChius(expirationDate);
        // indicazione di elenco standard pari al valore specificato dal criterio
        elenco.setFlElencoStandard(criterio.getFlCriterioRaggrStandard());
        // indicazione di elenco fiscale pari al valore specificato dal criterio
        elenco.setFlElencoFisc(criterio.getFlCriterioRaggrFisc());

        // MEV 24534 Imposto subito lo stato di validazione a NO_INDICE per gestire la chiusura
        if (it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE
                .equals(criterio.getTiValidElenco())) {
            elenco.setTiValidElenco(
                    it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE);
        }

        /* Registro l'elenco creato nella coda degli elenchi da elaborare ELV_ELENCO_VERS_DA_ELAB */
        ElvElencoVersDaElab elencoVersDaElab = new ElvElencoVersDaElab();
        elencoVersDaElab.setElvElencoVer(elenco);
        elencoVersDaElab.setIdCriterioRaggr(new BigDecimal(criterio.getIdCriterioRaggr()));
        elencoVersDaElab.setIdStrut(new BigDecimal(struttura.getIdStrut()));
        elencoVersDaElab.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.APERTO.name());
        elencoVersDaElab.setTsStatoElenco(new Date());
        if (tuttiAnniKeyUnitaDocNulli) {
            elencoVersDaElab.setAaKeyUnitaDoc(aaKeyUnitaDoc);
        }

        List<ElvElencoVersDaElab> elencoVersDaElabList = new ArrayList<>();
        elencoVersDaElabList.add(elencoVersDaElab);
        elenco.setElvElencoVersDaElabs(elencoVersDaElabList);

        return elenco;
    }

    private void addUnitaDocIntoElenco(AroUnitaDoc ud, ElvElencoVer elenco, long numDocs,
            long numComp, BigDecimal sizeComps, OrgStrut struttura, LogJob logJob) {
        // MAC#28020
        gestisciSalvataggioElenco(elenco, struttura, logJob);
        // end MAC#28020
        log.debug("Num doc da agg: {}; num comp da agg: {}; sizeComps: {}", numDocs, numComp,
                sizeComps);
        // aggiorno l'elenco corrente, incrementando di 1 il numero delle unitÃ  doc versate incluse
        // nell'elenco
        elenco.setNiUnitaDocVersElenco(elenco.getNiUnitaDocVersElenco().add(BigDecimal.ONE));
        // aggiorno elenco incrementando il numero documenti, il numero componenti ed il numero di
        // byte versati
        elenco.setNiDocVersElenco(elenco.getNiDocVersElenco().add(new BigDecimal(numDocs)));
        elenco.setNiCompVersElenco(elenco.getNiCompVersElenco().add(new BigDecimal(numComp)));
        if (sizeComps == null) {
            sizeComps = BigDecimal.ZERO;
        }
        elenco.setNiSizeVersElenco(elenco.getNiSizeVersElenco().add(sizeComps));
        // aggiorno l'unitÃ  documentaria corrente, assegnando stato = IN_ELENCO_APERTO e
        // valorizzando la FK all'elenco
        // corrente
        ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_APERTO.name());
        ud.setElvElencoVer(elenco);
        log.debug(
                "aggiorno AroUnitaDoc idUnitaDoc={} imposto stato IN_ELENCO_APERTO e idElencoVers={}",
                ud.getIdUnitaDoc(), elenco.getIdElencoVers());
        elenco.getAroUnitaDocs().add(ud);
        // elimino l'unitÃ  documentaria corrente dalla coda delle unitÃ  documentarie da elaborare
        // per gli elenchi
        elencoHelper.deleteUdDocFromQueue(ud);
    }

    private void addDocIntoElenco(AroDoc doc, ElvElencoVer elenco, long numComps,
            BigDecimal sizeComps, OrgStrut struttura, LogJob logJob) {
        // MAC#28020
        boolean checkAddDocAgg = (!elencoHelper.containsElenco(elenco))
                ? checkAddDocAggNoEleCor(doc)
                : checkAddDocAgg(elenco, doc);
        // end MAC#28020
        /* (i), (ii) */
        if (checkAddDocAgg) {
            // MAC#28020
            gestisciSalvataggioElenco(elenco, struttura, logJob);
            // end MAC#28020
            log.debug("Num comps da agg: {}; sizeComps: {}", numComps, sizeComps);
            // aggiorno l'elenco, incrementando di 1 il numero documenti aggiunti dell'elenco
            elenco.setNiDocAggElenco(elenco.getNiDocAggElenco().add(BigDecimal.ONE));
            // aggiorno l'elenco, incrementando il numero componenti ed il numero di byte aggiunti
            // dell'elenco
            elenco.setNiCompAggElenco(elenco.getNiCompAggElenco().add(new BigDecimal(numComps)));
            // la dimensione puÃ² essere nulla quando inserisco un componente con tipo supporto =
            // metadati
            if (sizeComps == null) {
                sizeComps = BigDecimal.ZERO;
            }
            elenco.setNiSizeAggElenco(elenco.getNiSizeAggElenco().add(sizeComps));
            // aggiorno il documento, assegnando stato = IN_ELENCO_APERTO e valorizzando la FK
            // all'elenco corrente
            doc.setTiStatoDocElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_APERTO.name());
            doc.setElvElencoVer(elenco);
            elenco.getAroDocs().add(doc);
            // elimino il documento corrente dalla coda dei documenti da elaborare per gli elenchi
            elencoHelper.deleteDocFromQueue(doc);
        }
    }

    private void addUpdIntoElenco(AroUpdUnitaDoc upd, ElvElencoVer elenco, OrgStrut struttura,
            LogJob logJob) {
        // MAC#28020
        boolean checkAddUpdUd = (!elencoHelper.containsElenco(elenco)) ? checkAddUpdUdNoEleCor(upd)
                : checkAddUpdUd(elenco, upd);
        // end MAC#28020
        /* (ii), (iii), (iv) */
        if (checkAddUpdUd) {
            // MAC#28020
            gestisciSalvataggioElenco(elenco, struttura, logJob);
            // end MAC#28020
            // aggiorno l'elenco, incrementando di 1 il numero aggiornamenti metadati inclusi
            // nellâ€™elenco
            elenco.setNiUpdUnitaDoc(elenco.getNiUpdUnitaDoc().add(BigDecimal.ONE));
            // aggiorno l'aggiornamento metadati corrente, assegnando stato = IN_ELENCO_APERTO e
            // valorizzando la FK
            // all'elenco corrente
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_APERTO);
            upd.setElvElencoVer(elenco);
            elenco.getAroUpdUnitaDocs().add(upd);
            // elimino l'aggiornamento metadati corrente dalla coda degli aggiornamenti metadati da
            // elaborare per gli
            // elenchi
            elencoHelper.deleteUpdFromQueue(upd);
        }
    }

    public void elaboraElenchiScaduti(long idStruttura, long logJobId) throws Exception {
        log.debug("CAV - controllo se ci sono elenchi di versamento scaduti");
        // determino gli elenchi con stato APERTO appartenenti alla struttura corrente,
        // la cui scadenza di chiusura sia antecedente all'istante corrente
        List<Long> elenchiScadutiDaProcessare = elencoHelper
                .retrieveElenchiScadutiDaProcessare(idStruttura);
        ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);

        log.info("CAV - trovati {}  elenchi di versamento scaduti da settare stato = DA_CHIUDERE",
                elenchiScadutiDaProcessare.size());
        for (Long elencoId : elenchiScadutiDaProcessare) {
            log.debug("CAV - trovato elenco {} scaduto da settare stato = DA_CHIUDERE", elencoId);
            newElencoEjbRef1.setDaChiudereAtomic(
                    ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message(), elencoId, idStruttura,
                    logJobId);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaChiudereAtomic(String closeReason, Long idElenco, long idStruttura,
            long logJobId) {
        log.debug("CAV - setDaChiudereAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        LogJob logJob = elencoHelper.retrieveLogJobByid(logJobId);
        elencoHelper.writeLogElencoVers(elenco, struttura,
                ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_SCADUTO.name(), logJob);
        // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
        gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
        // MAC 26737
        ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                        : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                TIPO_ELENCO_VERS_DA_CHIUDERE, DS_ELENCO_SCADUTO, tiStatoelenco, null);
    }

    // MAC#28020
    /**
     * Nuovo metodo introdotto con la mac 28020: se l'elenco corrente Ã¨ stato creato da zero non Ã¨
     * "managed" perchÃ© non Ã¨ ancora stata chiamata la persist dell'elenco, quest'ultima verrÃ 
     * effettuata solo se l'inserimento della prima ud/doc/upd ha successo.
     *
     * @param elenco    elenco oggetto dell'inserimento
     * @param struttura struttura a cui l'elenco afferisce
     * @param logJob    tracciamento dell'operazione
     *
     * @throws Exception in caso di errore non gestito
     */
    private void gestisciSalvataggioElenco(ElvElencoVer elenco, OrgStrut struttura, LogJob logJob) {
        if (!elencoHelper.containsElenco(elenco)) {
            elencoHelper.getEntityManager().persist(elenco);
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    "CREAZIONE_ELENCO_VERS", null, ElvStatoElencoVer.TiStatoElenco.APERTO, null);

            elencoHelper.writeLogElencoVers(elenco, struttura,
                    ElencoEnums.OpTypeEnum.CREA_ELENCO.name(), logJob);

            log.debug("CVC - Creato nuovo elenco: nome = {}; data scadenza = {}",
                    elenco.getNmElenco(), dateToString(elenco.getDtScadChius()));
        }
    }
    // end MAC#28020

    /**
     * Nuovo metodo introdotto con la mev 24534: se l'elenco ha come tipo validazione
     * <em>NO_INDICE</em> non ne verrÃ  creato l'indice di versamento.
     *
     * @param closeReason ragione per cui viene chiuso l'elenco
     * @param elenco      elenco oggetto di chiusura
     * @param struttura   struttura a cui l'elenco afferisce
     * @param logJob      tracciamento dell'operazione
     *
     * @throws Exception in caso di errore non gestito
     */
    private void gestisciChiusuraElenco(String closeReason, ElvElencoVer elenco, OrgStrut struttura,
            LogJob logJob) {
        // MEV #24534 creazione automatica indice solo con tipo di validazione diversa da NO_INDICE
        if (it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                .equals(elenco.getTiValidElenco())) {
            setChiuso(closeReason, elenco, struttura, logJob);
        } else {
            setDaChiudere(closeReason, elenco, struttura, logJob);
        }
    }

    private void setChiuso(String closeReason, ElvElencoVer elenco, OrgStrut struttura,
            LogJob logJob) {
        log.debug("CAE - setChiuso...");

        // determina nome ente e struttura normalizzati e non
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Calcolo e persisto lo urn dell'elenco */
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);

        /*
         * Non aggiorno piÃ¹ l'elenco con il numero delle unitÃ  doc modificate incluse nell'elenco,
         * lo faccio nella addDocIntoElenco
         */
        // il sistema assegna all'elenco stato = CHIUSO sia nella tabella ELV_ELENCO_VERS, che nella
        // tabella
        // ELV_ELENCO_VERS_DA_ELAB
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        (elenco.getElvElencoVersDaElabs().get(0))
                .setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        log.debug("CAE - Elenco id = {} impostato con stato {}  per '{}'", elenco.getIdElencoVers(),
                ElencoEnums.ElencoStatusEnum.CHIUSO, closeReason);
        // il sistema definisce sull'elenco la data di chiusura ed il motivo di chiusura pari a
        // "Elenco scaduto"
        Date systemDate = new Date();
        elenco.setDtChius(systemDate);
        elenco.setDlMotivoChius(closeReason);
        // il sistema aggiorna l'elenco con il numero delle unitÃ  documentarie modificate incluse
        // nell'elenco a causa
        // di
        // documenti aggiunti
        // e/o di aggiornamenti metadati unitÃ  doc mediante la vista ELV_V_COUNT_UD_MODIF
        elenco.setNiUnitaDocModElenco(new BigDecimal(
                elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
        // il sistema assegna ad ogni unitÃ  documentaria appartenente all'elenco stato =
        // IN_ELENCO_CHIUSO
        List<AroUnitaDoc> udDocList = elencoHelper.retrieveUdDocsInElenco(elenco);
        for (AroUnitaDoc ud : udDocList) {
            ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
            log.debug("CAV - Assegnato alla ud '{}' lo stato {}", ud.getIdUnitaDoc(),
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_CHIUSO);
        }
        // il sistema assegna ad ogni documento appartenente all'elenco stato = IN_ELENCO_CHIUSO
        List<AroDoc> docList = elencoHelper.retrieveDocsInElenco(elenco);
        for (AroDoc doc : docList) {
            doc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_CHIUSO.name());
            log.debug("CAV - Assegnato al doc '{}' lo stato {}", doc.getIdDoc(),
                    ElencoEnums.DocStatusEnum.IN_ELENCO_CHIUSO);
        }
        // il sistema assegna ad ogni aggiornamento unitÃ  doc appartenente all'elenco stato =
        // IN_ELENCO_CHIUSO
        List<AroUpdUnitaDoc> updList = elencoHelper.retrieveUpdsInElenco(elenco);
        for (AroUpdUnitaDoc upd : updList) {
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
            log.debug("CAV - Assegnato alla upd '{}' lo stato {}", upd.getIdUpdUnitaDoc(),
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
        }
        // il sistema registra sul log delle operazioni
        elencoHelper.writeLogElencoVers(elenco, struttura,
                ElencoEnums.OpTypeEnum.CHIUSURA_ELENCO.name(), logJob);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaChiudereFiscAtomic(String closeReason, Long idElenco, long idStruttura,
            long logJobId) {
        log.debug("CAV - setDaChiudereAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        LogJob logJob = elencoHelper.retrieveLogJobByid(logJobId);
        elencoHelper.writeLogElencoVers(elenco, struttura,
                ElencoEnums.OpTypeEnum.SET_ELENCO_DA_CHIUDERE.name(), logJob);
        // MEV#26734
        gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
        // end MEV#26734
        // MAC 26737
        ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                        : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElenco),
                TIPO_ELENCO_VERS_DA_CHIUDERE,
                "Chiusura anticipata per scadenza termini conservazione fiscale.", tiStatoelenco,
                null);
    }

    private void setDaChiudere(String closeReason, ElvElencoVer elenco, OrgStrut struttura,
            LogJob logJob) {
        log.debug("CAE - setDaChiudere...");
        /*
         * Non aggiorno piÃ¹ l'elenco con il numero delle unitÃ  doc modificate incluse nell'elenco,
         * lo faccio nella addDocIntoElenco
         */
        // il sistema assegna all'elenco stato = DA_CHIUDERE sia nella tabella ELV_ELENCO_VERS, che
        // nella tabella
        // ELV_ELENCO_VERS_DA_ELAB
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        (elenco.getElvElencoVersDaElabs().get(0))
                .setTiStatoElenco(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        log.debug("CAE - Elenco id = {} settato con stato {} per '{}'", elenco.getIdElencoVers(),
                ElencoEnums.ElencoStatusEnum.DA_CHIUDERE, closeReason);
        // il sistema definisce sull'elenco la data di chiusura ed il motivo di chiusura pari a
        // "Elenco scaduto"
        Date systemDate = new Date();
        /* TODO DA TOGLIERE 19304 documento JobElencoVersamento1.15 pag. 15 */
        elenco.setDtChius(systemDate);
        elenco.setDlMotivoChius(closeReason);
        /* TODO DA TOGLIERE 19304 documento JobElencoVersamento1.15 pag. 15 */
        // il sistema aggiorna l'elenco con il numero delle unitÃ  documentarie modificate incluse
        // nell'elenco a causa
        // di
        // documenti aggiunti
        // e/o di aggiornamenti metadati unitÃ  doc mediante la vista ELV_V_COUNT_UD_MODIF
        elenco.setNiUnitaDocModElenco(new BigDecimal(
                elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
        // il sistema assegna ad ogni unitÃ  documentaria appartenente all'elenco stato =
        // IN_ELENCO_DA_CHIUDERE
        List<AroUnitaDoc> udDocList = elencoHelper.retrieveUdDocsInElenco(elenco);
        for (AroUnitaDoc ud : udDocList) {
            ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
            log.debug("CAV - Assegnato alla ud '{}' lo stato {}", ud.getIdUnitaDoc(),
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_DA_CHIUDERE);
        }
        // il sistema assegna ad ogni documento appartenente all'elenco stato =
        // IN_ELENCO_DA_CHIUDERE
        List<AroDoc> docList = elencoHelper.retrieveDocsInElenco(elenco);
        for (AroDoc doc : docList) {
            doc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
            log.debug("CAV - Assegnato al doc '{}' lo stato  {}", doc.getIdDoc(),
                    ElencoEnums.DocStatusEnum.IN_ELENCO_DA_CHIUDERE);
        }
        // il sistema assegna ad ogni aggiornamento unitÃ  doc appartenente allâ€™elenco stato =
        // IN_ELENCO_DA_CHIUDERE
        List<AroUpdUnitaDoc> updList = elencoHelper.retrieveUpdsInElenco(elenco);
        for (AroUpdUnitaDoc upd : updList) {
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE);
            log.debug("CAV - Assegnato alla upd '{}' lo stato {}", upd.getIdUpdUnitaDoc(),
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE);
        }
        // il sistema registra sul log delle operazioni
        elencoHelper.writeLogElencoVers(elenco, struttura,
                ElencoEnums.OpTypeEnum.SET_ELENCO_DA_CHIUDERE.name(), logJob);
    }

    private Date calculateExpirationDate(ElvElencoVer elenco) {
        Date expirationDate = null;
        Date creationDate = elenco.getDtCreazioneElenco();
        log.debug("CEC - Data di creazione {}", dateToString(creationDate));

        if (elenco.getTiScadChius() != null) {
            String tiScadChiusElenco = elenco.getTiScadChius();
            expirationDate = adjustElencoDateByTiScadChius(creationDate, tiScadChiusElenco);
        } else {
            String tiTempoScadChius = elenco.getTiTempoScadChius();
            BigDecimal niTempoScadChius = elenco.getNiTempoScadChius();
            expirationDate = adjustElencoDate(creationDate, tiTempoScadChius, niTempoScadChius,
                    ElencoEnums.ModeEnum.ADD.name());
        }
        log.debug("CVC - Data di scadenza {}", dateToString(expirationDate));
        return expirationDate;
    }

    private Date adjustElencoDateByTiScadChius(Date creationDate, String tiScadChiusVolume) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(creationDate);
        Date expirationDate = null;
        log.debug("Data di creazione {}", creationDate);
        if (ElencoEnums.ExpirationTypeEnum.GIORNALIERA.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del giorno di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);

            String newdate = dateformat.format(cal.getTime());
            log.debug(EXPIRATION_LOG, ElencoEnums.ExpirationTypeEnum.GIORNALIERA, newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.SETTIMANALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana di creazione
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_YEAR, days);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(EXPIRATION_LOG, ElencoEnums.ExpirationTypeEnum.SETTIMANALE, newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.QUINDICINALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana successiva a quella di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_WEEK, days);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            String newdate = dateformat.format(cal.getTime());
            log.debug(EXPIRATION_LOG, ElencoEnums.ExpirationTypeEnum.QUINDICINALE, newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.MENSILE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del mese di creazione
            int actualMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, actualMaximum);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(EXPIRATION_LOG, ElencoEnums.ExpirationTypeEnum.MENSILE, newdate);
        }
        expirationDate = cal.getTime();
        log.debug("CVC - Nuova data di scadenza {}", expirationDate);
        return expirationDate;
    }

    private Date adjustElencoDate(Date plainDate, String tiTempo, BigDecimal niTempo,
            String opType) {
        int tempo = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(plainDate);
        if (tiTempo != null && niTempo != null) {
            if (ElencoEnums.ModeEnum.ADD.name().equals(opType)) {
                tempo = niTempo.intValue();
            } else if (ElencoEnums.ModeEnum.SUB.name().equals(opType)) {
                tempo = -(niTempo.intValue());
            }
            if (ElencoEnums.TimeTypeEnum.MINUTI.name().equals(tiTempo)) {
                cal.add(Calendar.MINUTE, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_CAE_AUMENTO_DI_SCADENZA, tempo, ElencoEnums.TimeTypeEnum.MINUTI,
                        newdate);
            }
            if (ElencoEnums.TimeTypeEnum.ORE.name().equals(tiTempo)) {
                cal.add(Calendar.HOUR_OF_DAY, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_CAE_AUMENTO_DI_SCADENZA, tempo, ElencoEnums.TimeTypeEnum.ORE,
                        newdate);
            }
            if (ElencoEnums.TimeTypeEnum.GIORNI.name().equals(tiTempo)) {
                cal.add(Calendar.DAY_OF_WEEK, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug(LOG_CAE_AUMENTO_DI_SCADENZA, tempo, ElencoEnums.TimeTypeEnum.GIORNI,
                        newdate);
            }
        }
        return cal.getTime();
    }

    private String dateToString(Date date) {
        return dateformat.format(date);
    }

    // controllo se la data scadenza elenco Ã¨ <= della sysdate
    private boolean checkElencoExpired(ElvElencoVer elenco) {
        Date actualDate = new Date();
        log.debug(
                "CAV - Verifico se l'elenco '{}' con data scadenza {} Ã¨ scaduto all'istante corrente ({})",
                elenco.getNmElenco(), dateToString(elenco.getDtScadChius()),
                dateToString(actualDate));
        if (actualDate.after(elenco.getDtScadChius())) {
            log.debug("CAV - Elenco scaduto");
            return true;
        } else {
            log.debug("CAV - Elenco non scaduto");
            return false;
        }
    }

    private void manageLast(ElvElencoVer elenco, OrgStrut struttura, LogJob logJob) {
        if (elenco.getNiUnitaDocVersElenco().intValue() > 0
                || elenco.getNiDocAggElenco().intValue() > 0
                || elenco.getNiUpdUnitaDoc().intValue() > 0) {
            /*
             * Aggiorno l'elenco con il numero delle unitÃ  documentarie modificate incluse
             * nell'elenco a causa di documenti aggiunti e/o di aggiornamenti metadati unitÃ  doc
             * mediante la vista ELV_V_COUNT_UD_MODIF
             */
            elenco.setNiUnitaDocModElenco(new BigDecimal(
                    elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
            /*
             * Registro sul log delle operazioni che l'elenco rimane aperto (tipo operazione =
             * SET_ELENCO_APERTO) specificando il nome e l'id dell'elenco, e riferendo l'entrata del
             * log alla registrazione di log di inizio job di creazione automatica degli elenchi
             */
            if (logJob != null) {
                elencoHelper.writeLogElencoVers(elenco, struttura,
                        ElencoEnums.OpTypeEnum.SET_ELENCO_APERTO.name(), logJob);
            }
        }
    }

    public void elaboraElenchiVuoti(long idStruttura) {
        log.debug("CAV - controllo se ci sono elenchi di versamento vuoti");
        // determino gli elenchi vuoti con stato APERTO appartenenti alla struttura corrente
        List<Long> elenchiVuotiDaProcessare = elencoHelper
                .retrieveElenchiVuotiDaProcessare(idStruttura);
        ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);

        log.info("CAV - trovati {} elenchi di versamento vuoti da rimuovere",
                elenchiVuotiDaProcessare.size());
        for (Long elencoId : elenchiVuotiDaProcessare) {
            log.debug("CAV - trovato elenco {} vuoto da rimuovere", elencoId);
            newElencoEjbRef1.deleteElenchiAtomic(elencoId);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteElenchiAtomic(Long idElenco) {
        log.debug("CAV - deleteElenchiAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.deleteElvElencoVer(new BigDecimal(elenco.getIdElencoVers()));
    }

    /*
     * Metodo che controlla che: 1) la unitÃ  doc a cui appartiene il documento 2) i documenti
     * aggiunti dellâ€™unitÃ  doc a cui appartiene il documento versati prima del documento aggiunto
     * siano presenti nellâ€™elenco corrente oppure per essi sia definita la FK ad un altro elenco e
     * che tale elenco abbia stato INDICI_AIP_GENERATI o ELENCO_INDICI_AIP_CREATO o
     * ELENCO_INDICI_AIP_FIRMATO o ELENCO_INDICI_AIP_ERR_MARCA o COMPLETATO; se controllo fallisce,
     * lâ€™elemento viene scartato e si passa al successivo
     */
    private boolean checkAddDocAgg(ElvElencoVer elenco, AroDoc doc) {
        boolean isAddDocAggOk = false;

        ElvVChkAddDocAgg chkAddDocAgg = elencoHelper.retrieveElvVChkAddDocAggByIdDocAggByIdElenco(
                doc.getIdDoc(), elenco.getIdElencoVers());
        if ("1".equals(chkAddDocAgg.getFlAddDocUdOk())
                && "1".equals(chkAddDocAgg.getFlAllAddDocPrecOk())) {
            isAddDocAggOk = true;
        }
        return isAddDocAggOk;
    }

    // MAC#28020
    /*
     * Metodo che controlla che per: 1) la unitÃ  doc a cui appartiene il documento 2) i documenti
     * aggiunti dellâ€™unitÃ  doc a cui appartiene il documento versati prima del documento aggiunto
     * sia definita la FK ad un altro elenco e che tale elenco abbia stato INDICI_AIP_GENERATI o
     * ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMATO o ELENCO_INDICI_AIP_ERR_MARCA o
     * COMPLETATO; se controllo fallisce, lâ€™elemento viene scartato e si passa al successivo
     */
    private boolean checkAddDocAggNoEleCor(AroDoc doc) {
        boolean isAddDocAggOk = false;

        ElvVChkAddDocAggNoEleCor chkAddDocAggNoEleCor = elencoHelper
                .retrieveElvVChkAddDocAggNoEleCorByIdDoc(doc.getIdDoc());
        if ("1".equals(chkAddDocAggNoEleCor.getFlAddDocUdOk())
                && "1".equals(chkAddDocAggNoEleCor.getFlAllAddDocPrecOk())) {
            isAddDocAggOk = true;
        }
        return isAddDocAggOk;
    }
    // end MAC#28020

    /*
     * Metodo che controlla che: 1) la unitÃ  doc a cui appartiene l'aggiornamento 2) tutti i
     * documenti aggiunti presenti nell'aggiornamento 3) tutti gli aggiornamenti precedenti (con
     * progressivo minore di quello corrente) siano presenti nell'elenco corrente oppure per essi
     * sia definita la FK ad un altro elenco e che tale elenco abbia stato INDICI_AIP_GENERATI o
     * ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMATO o ELENCO_INDICI_AIP_ERR_MARCA o
     * COMPLETATO; se controllo fallisce, l'elemento viene scartato e si passa al successivo
     */
    private boolean checkAddUpdUd(ElvElencoVer elenco, AroUpdUnitaDoc upd) {
        boolean isAddUpdUdOk = false;

        ElvVChkAddUpdUd chkAddUpdUd = elencoHelper.retrieveElvVChkAddUpdUdByIdUpdUdByIdElenco(
                upd.getIdUpdUnitaDoc(), elenco.getIdElencoVers());
        if ("1".equals(chkAddUpdUd.getFlAddUpdUdOk())
                && "1".equals(chkAddUpdUd.getFlAllAddUpdDocOk())
                && "1".equals(chkAddUpdUd.getFlAllUpdPrecOk())) {
            isAddUpdUdOk = true;
        }
        return isAddUpdUdOk;
    }

    // MAC#28020
    /*
     * Metodo che controlla che per: 1) la unitÃ  doc a cui appartiene l'aggiornamento 2) tutti i
     * documenti aggiunti presenti nell'aggiornamento 3) tutti gli aggiornamenti precedenti (con
     * progressivo minore di quello corrente) sia definita la FK ad un altro elenco e che tale
     * elenco abbia stato INDICI_AIP_GENERATI o ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMATO
     * o ELENCO_INDICI_AIP_ERR_MARCA o COMPLETATO; se controllo fallisce, l'elemento viene scartato
     * e si passa al successivo
     */
    private boolean checkAddUpdUdNoEleCor(AroUpdUnitaDoc upd) {
        boolean isAddUpdUdOk = false;

        ElvVChkAddUpdUdNoEleCor chkAddUpdUdNoEleCor = elencoHelper
                .retrieveElvVChkAddUpdUdNoEleCorByIdUpdUd(upd.getIdUpdUnitaDoc());
        if ("1".equals(chkAddUpdUdNoEleCor.getFlAddUpdUdOk())
                && "1".equals(chkAddUpdUdNoEleCor.getFlAllAddUpdDocOk())
                && "1".equals(chkAddUpdUdNoEleCor.getFlAllUpdPrecOk())) {
            isAddUpdUdOk = true;
        }
        return isAddUpdUdOk;
    }
    // end MAC#28020

    public void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm) {
        log.debug("CAV - calcolaUrnElenco");
        // sistema (new URN)
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnte, nomeStruttura,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnElencoVers.TiUrnElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnteNorm,
                        nomeStrutturaNorm, sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnElencoVers.TiUrnElenco.NORMALIZZATO);
    }
}
