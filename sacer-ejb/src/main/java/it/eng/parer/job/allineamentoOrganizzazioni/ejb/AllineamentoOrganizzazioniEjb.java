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

package it.eng.parer.job.allineamentoOrganizzazioni.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.reporg.ListaTipiDato;
import it.eng.integriam.client.ws.reporg.ReplicaOrganizzazione;
import it.eng.integriam.client.ws.reporg.ReplicaOrganizzazioneRispostaAbstract;
import it.eng.integriam.client.ws.reporg.TipoDato;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.job.allineamentoOrganizzazioni.dto.ParametriInputOrganizzazioni;
import it.eng.parer.job.allineamentoOrganizzazioni.utils.CostantiReplicaOrg;
import it.eng.parer.job.allineamentoOrganizzazioni.utils.CostantiReplicaOrg.EsitoServizio;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "AllineamentoOrganizzazioniEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class AllineamentoOrganizzazioniEjb {

    Logger log = LoggerFactory.getLogger(AllineamentoOrganizzazioniEjb.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper coHelper;
    @EJB
    private AllineamentoOrganizzazioniHelper aoHelper;

    /**
     * Metodo chiamato per la duplicazione su IAM delle organizzazioni inserite, modificate o cancellate su SACER La
     * lista delle organizzazioni è recuperata dalla tabella IAM_ORGANIZ_DA_REPLIC
     *
     */
    public void allineamentoOrganizzazioni() {
        allineamentoOrganizzazioni(null);
    }

    /**
     * Metodo chiamato per la duplicazione su IAM delle organizzazioni inserite, modificate o cancellate su SACER
     *
     * @param organizList
     *            Lista deelle organizzazioni da replicare su IAM
     *
     * @return ritorna l'esito della lavorazione dell'ultimo elemento della lista passata comeparametro
     */
    public String allineamentoOrganizzazioni(List<IamOrganizDaReplic> organizList) {
        String result = EsitoServizio.OK.name();
        boolean arrivoDaOnLine = false;
        /*
         * Determino l'insieme delle registrazioni nel log delle organizzazioni da allineare con stato DA_REPLICARE,
         * REPLICA_IN_TIMEOUT o REPLICA_IN_ERRORE
         */
        if (organizList == null) {
            organizList = aoHelper.getIamOrganizDaReplic();
        } else {
            arrivoDaOnLine = true;
        }

        /* Mi tengo una variabile che mi dice se la replica è andata o meno a buon fine */
        boolean replicaOK = true;

        log.info("Replica Organizzazioni SACER - ottenute {} organizzazioni da replicare", organizList.size());

        /* Per ogni registrazione determinata */
        for (IamOrganizDaReplic organizDaReplic : organizList) {
            try {
                ParametriInputOrganizzazioni pa = getParametriInputOrganizzazione(organizDaReplic);
                ReplicaOrganizzazioneRispostaAbstract resp = new ReplicaOrganizzazioneRispostaAbstract() {
                };

                /* Se l'organizzazione è presente */
                ReplicaOrganizzazione client = IAMSoapClients.replicaOrganizzazioneClient(pa.getNmUserid(),
                        pa.getCdPsw(), pa.getUrlReplicaOrganizzazioni());
                // MEV #23814, configurazione timeout
                IAMSoapClients.changeRequestTimeout((BindingProvider) client, pa.getTimeout());

                if (client != null) {
                    /* PREPARAZIONE ATTIVAZIONE SERVIZIO */
                    log.info("Replica Organizzazioni SACER - Preparazione attivazione servizio per l'organizzazione {}",
                            organizDaReplic.getNmOrganiz());
                    if (organizDaReplic.getTiOperReplic().equals(ApplEnum.TiOperReplic.INS.name())
                            || organizDaReplic.getTiOperReplic().equals(ApplEnum.TiOperReplic.MOD.name())) {
                        if (pa.isOrgPresente()) {
                            GregorianCalendar c = new GregorianCalendar();
                            XMLGregorianCalendar dtIniValGreg = null;
                            XMLGregorianCalendar dtFineValGreg = null;
                            if (pa.getDtIniVal() != null) {
                                c.setTime(pa.getDtIniVal());
                                dtIniValGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                            }
                            if (pa.getDtFineVal() != null) {
                                c.setTime(pa.getDtFineVal());
                                dtFineValGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                            }

                            /* Se tipo operazione è INSERIMENTO */
                            if (organizDaReplic.getTiOperReplic().equals(ApplEnum.TiOperReplic.INS.name())) {
                                log.info("Replica Organizzazioni SACER - Chiamo il ws di Inserimento Organizzazione");
                                resp = client.inserimentoOrganizzazione(pa.getNmApplic(), pa.getIdOrganizApplic(),
                                        pa.getNmTipoOrganiz(), pa.getIdEnteConserv(), pa.getIdEnteGestore(),
                                        pa.getIdOrganizApplicPadre(), pa.getNmTipoOrganizPadre(), pa.getNmOrganiz(),
                                        pa.getDsOrganiz(), pa.getIdEnteConvenz(), dtIniValGreg, dtFineValGreg,
                                        pa.getListaTipiDato());
                            } /* Se tipo operazione è MODIFICA */ else {
                                log.info("Replica Organizzazioni SACER - Chiamo il ws di Modifica Organizzazione");
                                resp = client.modificaOrganizzazione(pa.getNmApplic(), pa.getIdOrganizApplic(),
                                        pa.getNmTipoOrganiz(), pa.getIdEnteConserv(), pa.getIdEnteGestore(),
                                        pa.getNmOrganiz(), pa.getDsOrganiz(), pa.getIdOrganizApplicPadre(),
                                        pa.getNmTipoOrganizPadre(), pa.getIdEnteConvenz(), dtIniValGreg, dtFineValGreg,
                                        pa.getListaTipiDato());
                            }
                        } else {
                            resp.setCdEsito(it.eng.integriam.client.ws.reporg.EsitoServizio.OK);
                        }
                    } /* Se tipo operazione è CANCELLAZIONE */ else {
                        log.info("Replica Organizzazioni SACER - Chiamo il ws di Cancellazione Organizzazione");
                        resp = client.cancellaOrganizzazione(pa.getNmApplic(), pa.getIdOrganizApplic(),
                                pa.getNmTipoOrganiz());
                    }

                    /* Il sistema verifica la risposta del servizio di replica organizzazione */
                    EsitoServizio esitoServizio = resp.getCdEsito().name()
                            .equals(CostantiReplicaOrg.EsitoServizio.OK.name()) ? CostantiReplicaOrg.EsitoServizio.OK
                                    : CostantiReplicaOrg.EsitoServizio.KO;
                    /* Scrivo l'esito della singola replica organizzazione */
                    aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(), esitoServizio,
                            resp.getCdErr(), resp.getDsErr());

                    String posNeg = esitoServizio.name().equals(CostantiReplicaOrg.EsitoServizio.OK.name()) ? "positiva"
                            : "negativa";
                    log.info("Replica Organizzazioni SACER - Risposta WS {} per l'organizzazione {}", posNeg,
                            organizDaReplic.getNmOrganiz());

                    if (!esitoServizio.name().equals(CostantiReplicaOrg.EsitoServizio.OK.name())
                            && !resp.getCdErr().equals(CostantiReplicaOrg.SERVIZI_ORG_002)) {
                        replicaOK = false;
                        result = CostantiReplicaOrg.EsitoServizio.KO.name();
                    }

                } else {
                    /* Se il client è null, ci sono stati problemi */
                    aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
                            CostantiReplicaOrg.EsitoServizio.KO, CostantiReplicaOrg.SERVIZI_ORG_001,
                            "Errore nella creazione del client per la chiamata al WS di ReplicaOrganizzazioni");
                    log.error("Replica Organizzazioni - Risposta WS negativa per l'organizzazione {}",
                            organizDaReplic.getNmOrganiz());
                    break;
                }

            } catch (SOAPFaultException e) {
                aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
                        CostantiReplicaOrg.EsitoServizio.KO, CostantiReplicaOrg.SERVIZI_ORG_007,
                        e.getFault().getFaultCode() + ": " + e.getFault().getFaultString());
                log.error("Replica Organizzazioni - Risposta WS negativa per l'organizzazione "
                        + organizDaReplic.getNmOrganiz() + " " + CostantiReplicaOrg.SERVIZI_ORG_007
                        + " - Utente che attiva il servizio non riconosciuto o non abilitato", e);
                replicaOK = false;
                break;
            } catch (WebServiceException e) {
                /* Se non risponde o si verifica qualche errore */
                aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
                        CostantiReplicaOrg.EsitoServizio.NO_RISPOSTA, CostantiReplicaOrg.REPLICA_ORG_001,
                        "Il servizio di replica organizzazione non risponde");
                log.error(
                        "Replica Organizzazioni - Risposta WS negativa per l'organizzazione {} {} - Il servizio di replica organizzazione non risponde",
                        organizDaReplic.getNmOrganiz(), CostantiReplicaOrg.REPLICA_ORG_001);
                replicaOK = false;
                break;
            } catch (Exception e) {
                aoHelper.writeEsitoIamOrganizDaReplic(organizDaReplic.getIdOrganizDaReplic(),
                        CostantiReplicaOrg.EsitoServizio.KO, CostantiReplicaOrg.REPLICA_ORG_001, e.getMessage());
                log.error("Replica Organizzazioni - Risposta WS negativa per l'organizzazione "
                        + organizDaReplic.getNmOrganiz() + " " + CostantiReplicaOrg.REPLICA_ORG_001, e);
                replicaOK = false;
                break;
            }
        } // End organizDaReplic

        /* Scrivo nel log del job l'esito finale */
        if (!arrivoDaOnLine) {
            if (replicaOK) {
                jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(),
                        JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
            } else {
                jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(),
                        JobConstants.OpTypeEnum.ERRORE.name(),
                        "Errore durante la chiamata al WS di replica organizzazione");
            }
        }

        return result;
    }

    /**
     * Costruisce il bean di parametri in input da mandare al WS
     *
     * @param organizDaReplic
     *
     * @return
     */
    private ParametriInputOrganizzazioni getParametriInputOrganizzazione(IamOrganizDaReplic organizDaReplic) {
        /*
         * Creo il bean contenente i parametri di input per il WS e lo popolo diversamente a seconda che sia
         * inserimento, modifica o cancellazione
         */
        ParametriInputOrganizzazioni parametriInputOrganizzazioni = new ParametriInputOrganizzazioni();
        parametriInputOrganizzazioni
                .setNmUserid(coHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_REPLICA_ORG));
        parametriInputOrganizzazioni
                .setCdPsw(coHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_REPLICA_ORG));
        parametriInputOrganizzazioni
                .setNmApplic(coHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        /* Ricavo l'url del ws */
        parametriInputOrganizzazioni.setUrlReplicaOrganizzazioni(
                coHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_REPLICA_ORG));

        /* Ricavo il timeout per la chiamata */
        String timeoutString = coHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TIMEOUT_REPLICA_ORG);
        // imposto il valore di timeout solo se è configurato, altrimenti utillizo il default
        if (timeoutString != null && timeoutString.matches("^[0-9]+$")) {
            int timeoutReplicaOrganizzazione = Integer.parseInt(timeoutString);
            parametriInputOrganizzazioni.setTimeout(timeoutReplicaOrganizzazione);
        } else {
            log.warn(
                    "Il valore personalizzato {} per il parametro TIMEOUT_REPLICA_ORG non è corretto. Utilizzo il valore predefinito",
                    timeoutString);
        }

        parametriInputOrganizzazioni.setIdOrganizApplic((organizDaReplic.getIdOrganizApplic().intValue()));
        parametriInputOrganizzazioni.setNmTipoOrganiz(organizDaReplic.getNmTipoOrganiz());
        parametriInputOrganizzazioni.setOrgPresente(false);

        /* Preparo le lista dei TIPI DATO dell'organizzazione */
        List<DecTipoUnitaDoc> listaTipiUD;
        List<DecRegistroUnitaDoc> listaRegistri;
        List<OrgSubStrut> listaSottoStrutture;
        List<DecTipoDoc> listaTipiDoc;
        List<DecTipoFascicolo> listaTipiFascicolo;
        List<Long> idStruts = new ArrayList<>();

        if (organizDaReplic.getTiOperReplic().equals(ApplEnum.TiOperReplic.INS.name())
                || organizDaReplic.getTiOperReplic().equals(ApplEnum.TiOperReplic.MOD.name())) {
            ListaTipiDato lista = new ListaTipiDato();
            /*
             * Devo recuperare l'organizzazione da passare al WS, se non c'è (esempio è stata cancellata da SACER) non
             * posso passare nulla
             */
            switch (organizDaReplic.getNmTipoOrganiz()) {
            case "AMBIENTE":
                OrgAmbiente ambiente = aoHelper.getOrgAmbiente(organizDaReplic.getIdOrganizApplic());
                if (ambiente != null) {
                    parametriInputOrganizzazioni.setIdOrganizApplicPadre(null);
                    parametriInputOrganizzazioni.setNmTipoOrganizPadre(null);
                    parametriInputOrganizzazioni.setNmOrganiz(ambiente.getNmAmbiente());
                    parametriInputOrganizzazioni.setDsOrganiz(ambiente.getDsAmbiente());
                    parametriInputOrganizzazioni.setIdEnteConserv(ambiente.getIdEnteConserv().intValue());
                    parametriInputOrganizzazioni.setIdEnteGestore(ambiente.getIdEnteGestore().intValue());
                    parametriInputOrganizzazioni.setOrgPresente(true);
                }
                break;
            case "ENTE":
                OrgEnte ente = aoHelper.getOrgEnte(organizDaReplic.getIdOrganizApplic());
                if (ente != null) {
                    parametriInputOrganizzazioni
                            .setIdOrganizApplicPadre(Math.toIntExact(ente.getOrgAmbiente().getIdAmbiente()));
                    parametriInputOrganizzazioni.setNmTipoOrganizPadre("AMBIENTE");
                    parametriInputOrganizzazioni.setNmOrganiz(ente.getNmEnte());
                    parametriInputOrganizzazioni.setDsOrganiz(ente.getDsEnte());
                    parametriInputOrganizzazioni.setIdEnteConserv(null);
                    parametriInputOrganizzazioni.setIdEnteGestore(null);
                    parametriInputOrganizzazioni.setOrgPresente(true);
                }
                break;
            case "STRUTTURA":
                OrgStrut strut = aoHelper.getOrgStrut(organizDaReplic.getIdOrganizApplic());
                if (strut != null) {
                    parametriInputOrganizzazioni
                            .setIdOrganizApplicPadre(Math.toIntExact(strut.getOrgEnte().getIdEnte()));
                    parametriInputOrganizzazioni.setNmTipoOrganizPadre("ENTE");
                    parametriInputOrganizzazioni.setNmOrganiz(strut.getNmStrut());
                    parametriInputOrganizzazioni.setDsOrganiz(strut.getDsStrut());
                    parametriInputOrganizzazioni.setIdEnteConserv(null);
                    parametriInputOrganizzazioni.setIdEnteGestore(null);
                    parametriInputOrganizzazioni.setOrgPresente(true);
                    idStruts.add(strut.getIdStrut());
                }
                break;
            default:
                log.warn("Nome tipo organizzazione {} non gestito.", organizDaReplic.getNmTipoOrganiz());
                break;
            }

            if (parametriInputOrganizzazioni.isOrgPresente() && !idStruts.isEmpty()) {
                listaTipiUD = aoHelper.getDecTipoUnitaDocList(idStruts);
                listaRegistri = aoHelper.getDecRegistroUnitaDocList(idStruts);
                listaSottoStrutture = aoHelper.getOrgSubStrutList(idStruts);
                listaTipiDoc = aoHelper.getDecTipoDocList(idStruts);
                listaTipiFascicolo = aoHelper.getDecTipoFascicoloList(idStruts);

                if (listaTipiUD != null) {
                    for (DecTipoUnitaDoc tipoUD : listaTipiUD) {
                        TipoDato tipoDato = new TipoDato();
                        tipoDato.setNmClasseTipoDato(Constants.TipoDato.TIPO_UNITA_DOC.name());
                        tipoDato.setIdTipoDatoApplic(Math.toIntExact(tipoUD.getIdTipoUnitaDoc()));
                        tipoDato.setNmTipoDato(tipoUD.getNmTipoUnitaDoc());
                        tipoDato.setDsTipoDato(tipoUD.getDsTipoUnitaDoc());
                        lista.getTipoDato().add(tipoDato);
                    }
                }

                if (listaRegistri != null) {
                    for (DecRegistroUnitaDoc registro : listaRegistri) {
                        TipoDato tipoDato = new TipoDato();
                        tipoDato.setNmClasseTipoDato(Constants.TipoDato.REGISTRO.name());
                        tipoDato.setIdTipoDatoApplic(Math.toIntExact(registro.getIdRegistroUnitaDoc()));
                        tipoDato.setNmTipoDato(registro.getCdRegistroUnitaDoc());
                        tipoDato.setDsTipoDato(registro.getDsRegistroUnitaDoc());
                        lista.getTipoDato().add(tipoDato);
                    }
                }

                if (listaSottoStrutture != null) {
                    for (OrgSubStrut sottoStruttura : listaSottoStrutture) {
                        TipoDato tipoDato = new TipoDato();
                        tipoDato.setNmClasseTipoDato(Constants.TipoDato.SUB_STRUTTURA.name());
                        tipoDato.setIdTipoDatoApplic(Math.toIntExact(sottoStruttura.getIdSubStrut()));
                        tipoDato.setNmTipoDato(sottoStruttura.getNmSubStrut());
                        tipoDato.setDsTipoDato(sottoStruttura.getDsSubStrut());
                        lista.getTipoDato().add(tipoDato);
                    }
                }

                if (listaTipiDoc != null) {
                    for (DecTipoDoc tipoDoc : listaTipiDoc) {
                        TipoDato tipoDato = new TipoDato();
                        tipoDato.setNmClasseTipoDato(Constants.TipoDato.TIPO_DOC.name());
                        tipoDato.setIdTipoDatoApplic(Math.toIntExact(tipoDoc.getIdTipoDoc()));
                        tipoDato.setNmTipoDato(tipoDoc.getNmTipoDoc());
                        tipoDato.setDsTipoDato(tipoDoc.getDsTipoDoc());
                        lista.getTipoDato().add(tipoDato);
                    }
                }

                if (listaTipiFascicolo != null) {
                    for (DecTipoFascicolo tipoFascicolo : listaTipiFascicolo) {
                        TipoDato tipoDato = new TipoDato();
                        tipoDato.setNmClasseTipoDato(Constants.TipoDato.TIPO_FASCICOLO.name());
                        tipoDato.setIdTipoDatoApplic(Math.toIntExact(tipoFascicolo.getIdTipoFascicolo()));
                        tipoDato.setNmTipoDato(tipoFascicolo.getNmTipoFascicolo());
                        tipoDato.setDsTipoDato(tipoFascicolo.getDsTipoFascicolo());
                        lista.getTipoDato().add(tipoDato);
                    }
                }

                parametriInputOrganizzazioni.setListaTipiDato(lista);
            } else {
                parametriInputOrganizzazioni.setListaTipiDato(new ListaTipiDato());
            }
            // Inserisco i parametri riguardanti l'ente convenzionato
            Map<String, Object> mappa = aoHelper.getEnteConvenzInfo((organizDaReplic.getIdOrganizApplic()));
            Integer idEnteConvenz = mappa.get("idEnteConvenz") != null
                    ? ((BigDecimal) mappa.get("idEnteConvenz")).intValue() : null;
            Date dtIniVal = mappa.get("dtIniVal") != null ? (Date) mappa.get("dtIniVal") : null;
            Date dtFineVal = mappa.get("dtFineVal") != null ? (Date) mappa.get("dtFineVal") : null;
            if (idEnteConvenz != null) {
                parametriInputOrganizzazioni.setIdEnteConvenz(idEnteConvenz);
            }
            if (dtIniVal != null) {
                parametriInputOrganizzazioni.setDtIniVal(dtIniVal);
            }
            if (dtFineVal != null) {
                parametriInputOrganizzazioni.setDtFineVal(dtFineVal);
            }
        }
        return parametriInputOrganizzazioni;
    }
}
