/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.ejb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;

import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.entity.VrsPathDtVers;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerRuntimeException;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.monitoraggio.dto.DLQMsgInfo;
import it.eng.parer.ws.monitoraggio.dto.DLQMsgInfoExt;
import it.eng.parer.ws.monitoraggio.dto.RispostaWSStatusMonitor;
import it.eng.parer.ws.monitoraggio.dto.rmonitor.MonitorAltro;
import it.eng.parer.ws.monitoraggio.ejb.MonitorCoda.NomeCoda;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "AltriStatusMonitor")
@LocalBean
public class AltriStatusMonitor {

    private enum MonitorAltri {
        STATO_CONNESSIONE_ORCL, PRESENZA_ERRORI_CODA_JMS, PRESENZA_RECORD_INDICE_AIPUD_TOELAB, TPI_NI_FILE_PATH_ARK,
        TPI_ARCHIVIATA_ERR, TPI_DATA_NON_ARCHIVIATA
    }

    private enum MonitorSondeGenEsiti {
        OK, ERROR, WARNING
    }

    private static final Logger log = LoggerFactory.getLogger(AltriStatusMonitor.class);

    private static final String ERROR_SEPARATOR = ", ";
    private static final String JMS_SELECTOR = Costanti.JMSMsgProperties.MSG_K_APP + " = '"
            + Costanti.JMSMsgProperties.MSG_V_APP_SACER + "' OR " + Costanti.JMSMsgProperties.MSG_K_APP + " = '"
            + Costanti.JMSMsgProperties.MSG_V_APP_SACERWS + "'";

    @EJB
    ControlliMonitor controlliMonitor;
    @EJB
    MonitorCoda monitorCoda;
    @EJB
    ConfigurationHelper configurationHelper;

    public void calcolaStatoDatabase(List<MonitorAltro> listaMon) {
        MonitorAltro tmpAltro = new MonitorAltro();
        tmpAltro.setNome(MonitorAltri.STATO_CONNESSIONE_ORCL.name());
        if (controlliMonitor.controllaStatoDbOracle()) {
            tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
        } else {
            tmpAltro.setStato(MonitorSondeGenEsiti.ERROR.name() + "|Il database Oracle non è raggiungibile ");
            log.error("Errore: Il database Oracle non è raggiungibile");
        }
        listaMon.add(tmpAltro);
    }

    public void calcolaStatoCodaMorta(List<MonitorAltro> listaMon) {
        this.calcolaStatoCodaMorta(listaMon, JMS_SELECTOR);
    }

    private void calcolaStatoCodaMorta(List<MonitorAltro> listaMon, String messageSelector) {
        MonitorAltro tmpAltro = new MonitorAltro();
        // nome
        tmpAltro.setNome(MonitorAltri.PRESENZA_ERRORI_CODA_JMS.name());
        try {
            List<DLQMsgInfo> infoCodas = monitorCoda.retrieveMsgInQueue(NomeCoda.dmqQueue.name(), messageSelector);
            this.elabJmsMessage(tmpAltro, infoCodas);
        } catch (JMSException e) {
            log.error("Errore nel recupero delle informazioni della DLQ", e);
            throw new SacerRuntimeException("Errore nel recupero delle informazioni della DLQ",
                    SacerErrorCategory.INTERNAL_ERROR);
        }
        listaMon.add(tmpAltro);
    }

    /**
     * Se esiste un attributo payloadType elaboro il messaggio secondo un template prestabilito Elabora quindi il numero
     * di KO
     * 
     * @param tmpAltro
     * @param infoCodas
     */
    private void elabJmsMessage(MonitorAltro tmpAltro, List<DLQMsgInfo> infoCodas) {
        // non esistono messaggi su DLQ
        if (infoCodas.isEmpty()) {
            tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
        } else {
            // preparo messaggio da inviare di ERROR
            StringBuilder koMsg = new StringBuilder();
            koMsg.append(MonitorSondeGenEsiti.ERROR.name() + "| messaggi rilevati in DLQ: ");

            // creazione messaggio raggruppando per payloadType/state
            Map<String, DLQMsgInfoExt> mapInfos = this.buildInfoCodaMap(infoCodas);
            // per ogni payloadType creo parte del messaggio da inviare al trapper Zabbix
            for (Iterator<String> it = mapInfos.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                DLQMsgInfoExt tmpInfoCoda = mapInfos.get(key);
                koMsg.append(tmpInfoCoda.getCountMsg() + " messaggi/o");
                if (StringUtils.isNotBlank(tmpInfoCoda.getPayloadType())) {
                    koMsg.append(" di tipo " + tmpInfoCoda.getPayloadType());
                }
                if (StringUtils.isNotBlank(tmpInfoCoda.getFromApplication())) {
                    koMsg.append(" da " + tmpInfoCoda.getFromApplication());
                }
                if (StringUtils.isNotBlank(tmpInfoCoda.getState())
                        && !tmpInfoCoda.getState().equals(MonitorCoda.PAYLOAD_NOSTATE)) {
                    koMsg.append(" con stato " + tmpInfoCoda.getState());
                }
                koMsg.append(ERROR_SEPARATOR);
            }
            // preparazione messaggio di errore (inviato a Zabbix)
            String msg = koMsg.toString().substring(0, koMsg.toString().length() - ERROR_SEPARATOR.length());// remove
            // char
            tmpAltro.setStato(msg);
            log.error(msg);
        }
    }

    private Map<String, DLQMsgInfoExt> buildInfoCodaMap(List<DLQMsgInfo> infoCodas) {
        Map<String, DLQMsgInfoExt> mapInfos = new HashMap<>();
        for (DLQMsgInfo info : infoCodas) {
            String key = this.elabMapInfoKey(info);
            if (mapInfos.containsKey(key)) {
                // estrae oggetto e aggiorna
                DLQMsgInfoExt tmpInfoCoda = mapInfos.get(key);
                tmpInfoCoda.incCountMsg(); // incremento contatore msg in DLQ
                mapInfos.put(key, tmpInfoCoda);
            } else {
                // init
                DLQMsgInfoExt tmpInfoCoda = new DLQMsgInfoExt();
                tmpInfoCoda.setPayloadType(info.getPayloadType());
                tmpInfoCoda.setFromApplication(info.getFromApplication());
                tmpInfoCoda.setState(info.getState());
                mapInfos.put(key, tmpInfoCoda);
            }
        }
        return mapInfos;
    }

    private String elabMapInfoKey(DLQMsgInfo info) {
        StringBuilder tmpKey = new StringBuilder();
        tmpKey.append(info.getPayloadType());
        tmpKey.append("_");
        tmpKey.append(info.getState());
        return tmpKey.toString();
    }

    public void calcolaStatoIndiceAipUdInCoda(RispostaWSStatusMonitor rispostaWs, List<MonitorAltro> tmpLstAltro) {
        MonitorAltro tmpAltro = new MonitorAltro();
        // nome
        tmpAltro.setNome(MonitorAltri.PRESENZA_RECORD_INDICE_AIPUD_TOELAB.name());
        // delta
        int delta = Integer.parseInt(configurationHelper.getValoreParamApplic(
                CostantiDB.TipoParametroAppl.PARERMT_INDICE_UD_DAELAB_NI_GG_MAX, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC));

        // calcolo date max
        DateTime dateTime = new DateTime(new Date());
        dateTime = dateTime.minusDays(delta);

        // query
        RispostaControlli rc = controlliMonitor.leggiStatoIndiceAipUdDaElab(new Date(dateTime.getMillis()), "1");
        // elab for message
        if (rc.isrBoolean()) {
            // add monitor (no SQL error)
            if (rc.getrLong() == 0) {
                // messaggio OK
                tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
            } else {
                // preparo messaggio da inviare di ERROR
                tmpAltro.setStato(MonitorSondeGenEsiti.ERROR.name() + "| rilevato/i " + rc.getrLong()
                        + " record di ARO_INDICE_AIP_UD_DA_ELAB da elaborare più vecchi di " + delta + " giorno/i");
            }
            tmpLstAltro.add(tmpAltro);
        } else {
            rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
        }
    }

    public void calcolaTpiNiPathFileArk(RispostaWSStatusMonitor rispostaWs, List<MonitorAltro> tmpLstAltro) {
        MonitorAltro tmpAltro = new MonitorAltro();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        // nome
        tmpAltro.setNome(MonitorAltri.TPI_NI_FILE_PATH_ARK.name());
        // query
        RispostaControlli rc = controlliMonitor.verificaNiFilePathArk();
        // elab for message
        if (rc.isrBoolean()) {
            // add monitor (no SQL error)
            if (rc.getrLong() == 0) {
                // messaggio OK
                tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
            } else {
                // preparo messaggio da inviare di ERROR
                List<VrsPathDtVers> result = (List<VrsPathDtVers>) rc.getrObject();
                StringBuilder sb = new StringBuilder();
                sb.append(MonitorSondeGenEsiti.ERROR.name() + "| rilevato/i errore numero file archiviati: ");
                for (VrsPathDtVers pathDtVers : result) {
                    String date = dateFormat.format(pathDtVers.getVrsDtVers().getDtVers());
                    sb.append("Data Vers = " + date + ",");
                    sb.append("Path = " + pathDtVers.getDlPath() + ",");
                    sb.append("Nr. File = " + pathDtVers.getNiFilePath() + ",");
                    sb.append("Nr. File Path Ark = " + pathDtVers.getNiFilePathArk() + ",");
                    sb.append("Nr. File Path Ark Secondario = " + (pathDtVers.getNiFilePathArkSecondario() != null
                            ? pathDtVers.getNiFilePathArkSecondario() : "solo primario"));
                    sb.append(";");
                }
                // remove last char
                String stato = sb.substring(0, sb.length() - 1);
                tmpAltro.setStato(stato);
            }
            tmpLstAltro.add(tmpAltro);
        } else {
            rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
        }
    }

    public void calcolaTpiArkErr(RispostaWSStatusMonitor rispostaWs, List<MonitorAltro> tmpLstAltro) {
        MonitorAltro tmpAltro = new MonitorAltro();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        // nome
        tmpAltro.setNome(MonitorAltri.TPI_ARCHIVIATA_ERR.name());
        // query
        RispostaControlli rc = controlliMonitor.verificaIfExistStatoArchErr();
        // elab for message
        if (rc.isrBoolean()) {
            // add monitor (no SQL error)
            if (rc.getrLong() == 0) {
                // messaggio OK
                tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
            } else {
                // preparo messaggio da inviare di ERROR
                List<VrsDtVers> result = (List<VrsDtVers>) rc.getrObject();
                StringBuilder sb = new StringBuilder();
                sb.append(MonitorSondeGenEsiti.ERROR.name()
                        + "| rilevata/e la/e seguente/i data/e di versamento con stato di archivazione "
                        + JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name() + ": ");
                sb.append(result.stream().map(v -> dateFormat.format(v.getDtVers())).collect(Collectors.joining(", ")));
                tmpAltro.setStato(sb.toString());
            }
            tmpLstAltro.add(tmpAltro);
        } else {
            rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
        }
    }

    public void calcolaTpiDataNotArk(RispostaWSStatusMonitor rispostaWs, List<MonitorAltro> tmpLstAltro) {
        MonitorAltro tmpAltro = new MonitorAltro();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        // nome
        tmpAltro.setNome(MonitorAltri.TPI_DATA_NON_ARCHIVIATA.name());
        // query
        RispostaControlli rc = controlliMonitor.verificaDataNotArk();
        // elab for message
        if (rc.isrBoolean()) {
            // add monitor (no SQL error)
            if (rc.getrLong() == 0) {
                // messaggio OK
                tmpAltro.setStato(MonitorSondeGenEsiti.OK.name());
            } else {
                // preparo messaggio da inviare di ERROR
                List<VrsDtVers> result = (List<VrsDtVers>) rc.getrObject();
                StringBuilder sb = new StringBuilder();
                sb.append(MonitorSondeGenEsiti.ERROR.name()
                        + "| rilevata/e la/e seguente/i data/e di versamento in stato "
                        + JobConstants.ArkStatusEnum.REGISTRATA.name() + " / "
                        + JobConstants.ArkStatusEnum.DA_ARCHIVIARE.name()
                        + ", antecedente/i a date di versamento con stato "
                        + JobConstants.ArkStatusEnum.ARCHIVIATA.name() + " / "
                        + JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name() + ": ");
                sb.append(result.stream().map(v -> dateFormat.format(v.getDtVers())).collect(Collectors.joining(", ")));
                tmpAltro.setStato(sb.toString());
            }
            tmpLstAltro.add(tmpAltro);
        } else {
            rispostaWs.setEsitoWsError(rc.getCodErr(), rc.getDsErr());
        }
    }

}
