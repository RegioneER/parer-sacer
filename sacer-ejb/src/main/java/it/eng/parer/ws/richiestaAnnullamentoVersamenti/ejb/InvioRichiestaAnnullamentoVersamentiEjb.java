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

package it.eng.parer.ws.richiestaAnnullamentoVersamenti.ejb;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.annulVers.ejb.AnnulVersEjb;
import it.eng.parer.annulVers.helper.AnnulVersHelper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.entity.AroItemRichAnnulVers;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStatoRichAnnulVers;
import it.eng.parer.entity.AroXmlRichAnnulVers;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.viewEntity.AroVLisItemRichAnnvrs;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.recupero.dto.ParametriParser;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.InvioRichiestaAnnullamentoVersamentiExt;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.RispostaWSInvioRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.utils.InvioRichiestaAnnullamentoVersamentiParser;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.esitoRichAnnullVers.CodiceEsitoType;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaType;
import it.eng.parer.ws.xml.esitoRichAnnullVers.VersamentoDaAnnullareType;
import it.eng.parer.ws.xml.richAnnullVers.TipoAnnullamentoType;
import it.eng.parer.ws.xml.richAnnullVers.TipoVersamentoType;
import it.eng.spagoLite.security.User;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "InvioRichiestaAnnullamentoVersamentiEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class InvioRichiestaAnnullamentoVersamentiEjb {

    private static final Logger log = LoggerFactory.getLogger(InvioRichiestaAnnullamentoVersamentiEjb.class);

    @Resource
    private SessionContext context;

    @EJB
    private InvioRichiestaAnnullamentoVersamentiHelper ravHelper;

    @EJB
    private ControlliWS controlliWS;

    @EJB
    private AnnulVersEjb avEjb;

    @EJB
    private AnnulVersHelper avHelper;

    @EJB
    private ElencoVersamentoHelper evHelper;

    public void esaminaRichiesteAnnullamentoVersamento(RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt) {
        EsitoRichiestaAnnullamentoVersamenti myEsito = rispostaWs.getEsitoRichiestaAnnullamentoVersamenti();

        if (ravExt.getUser() == null) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, "Errore: l'utente non è autenticato.");
            return;
        }

        try {
            log.info("Registro la richiesta {}",
                    ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getCodice());
            // Apro transazione e registro le richieste
            Long idRichiesta = context.getBusinessObject(InvioRichiestaAnnullamentoVersamentiEjb.class)
                    .registraRichieste(rispostaWs, ravExt);
            log.info("Richiesta registrata");
            // Chiusa transazione
            if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR && idRichiesta != null) {
                AroRichAnnulVers richAnnulVers = ravHelper.findById(AroRichAnnulVers.class, idRichiesta);
                AroStatoRichAnnulVers statoRichAnnulVers = ravHelper.findById(AroStatoRichAnnulVers.class,
                        richAnnulVers.getIdStatoRichAnnulVersCor());
                if (statoRichAnnulVers.getTiStatoRichAnnulVers().equals(CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
                    boolean flRichiestaPing = false;// default
                    // Apro nuova transazione per controlli ed evasione richiesta
                    // se flag presente su richiesta ....
                    if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isRichiestaDaPreIngest() != null) {
                        flRichiestaPing = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                                .isRichiestaDaPreIngest();
                    }
                    log.info("evasione richiesta annullamento ... ");
                    context.getBusinessObject(InvioRichiestaAnnullamentoVersamentiEjb.class)
                            .evasioneRichiestaAnnullamento(idRichiesta, ravExt.getUser().getIdUtente(),
                                    flRichiestaPing);
                    log.info("... evasa");
                    // Chiusa transazione
                    // Conto il numero di item presenti nella richiesta e quelli presenti nella richiesta con stato
                    // NON_ANNULLABILE
                    // Long numeroItems = avEjb.countItemsInRichAnnulVers(new
                    // BigDecimal(richAnnulVers.getIdRichAnnulVers()));
                    Long numeroItemsNonAnnullabili = avEjb.countItemsInRichAnnulVers(
                            new BigDecimal(richAnnulVers.getIdRichAnnulVers()),
                            CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                    CodiceEsitoType codiceEsito;
                    String codiceErrore = null;
                    String messaggioErrore = null;
                    if (numeroItemsNonAnnullabili == 0) {
                        codiceEsito = CodiceEsitoType.POSITIVO;
                    } else {
                        codiceEsito = CodiceEsitoType.WARNING;
                        // MEV#26446
                        codiceErrore = (richAnnulVers.getTiRichAnnulVers()
                                .equals(CostantiDB.TiRichAnnulVers.UNITA_DOC.name()))
                                        ? MessaggiWSBundle.RICH_ANN_VERS_012 : MessaggiWSBundle.RICH_ANN_VERS_023;
                        // end MEV#26446
                        messaggioErrore = MessaggiWSBundle.getString(codiceErrore);
                    }
                    rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta()
                            .setCodiceEsito(codiceEsito);
                    rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta()
                            .setCodiceErrore(codiceErrore);
                    rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta()
                            .setMessaggioErrore(messaggioErrore);
                }
                Long numeroItems = avEjb.countItemsInRichAnnulVers(new BigDecimal(richAnnulVers.getIdRichAnnulVers()));
                Long numeroItemsNonAnnullabili = avEjb.countItemsInRichAnnulVers(
                        new BigDecimal(richAnnulVers.getIdRichAnnulVers()),
                        CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                String xmlRisposta = generaRisposta(rispostaWs, ravExt, numeroItems, numeroItemsNonAnnullabili,
                        richAnnulVers);
                context.getBusinessObject(InvioRichiestaAnnullamentoVersamentiEjb.class).createAroXmlRichAnnulVers(
                        richAnnulVers, CostantiDB.TiXmlRichAnnulVers.RISPOSTA.name(), xmlRisposta,
                        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersioneXmlEsito());
            }
        } catch (Exception e) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella fase di generazione dell'XML di risposta del EJB "
                            + ExceptionUtils.getRootCauseMessage(e));
            log.error("Errore nella fase di generazione dell'XML di risposta del EJB ", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long registraRichieste(RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt) throws ParerUserError {
        Long idRichiesta = null;
        if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
            User user = ravExt.getUser();
            Long idStrut = ravExt.getIdStrut();
            String codice = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getCodice();
            String descrizione = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getDescrizione();
            String note = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getMotivazione();
            boolean flImmediata = false;
            boolean flForzaAnnul = false;
            boolean flRichiestaPing = false;
            // MEV#26446
            TipoVersamentoType tipoVersamento = TipoVersamentoType.UNITA_DOCUMENTARIA;
            // end MEV#26446

            // MEV 30721
            TipoAnnullamentoType tipoAnnullamento = TipoAnnullamentoType.ANNULLAMENTO_VERSAMENTO;

            // flImmediata è un parametro facoltativo che dunque, in caso non venga inviato, di default risulta essere
            // false
            if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isImmediata() != null) {
                flImmediata = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isImmediata();
            }

            if (ravExt.getModificatoriWS().contains(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING)) {

                // flForzaAnnul è un parametro facoltativo che dunque, in caso non venga inviato, di default risulta
                // essere false
                if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isForzaAnnullamento() != null) {
                    flForzaAnnul = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isForzaAnnullamento();
                }

                // flRichiestaPing è un parametro facoltativo che dunque, in caso non venga inviato, di default risulta
                // essere false
                if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isRichiestaDaPreIngest() != null) {
                    flRichiestaPing = ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                            .isRichiestaDaPreIngest();
                }
            }

            // MEV#26446
            if (ravExt.getModificatoriWS().contains(Costanti.ModificatoriWS.TAG_ANNUL_FASC)) {
                tipoVersamento = ravExt.getRichiestaAnnullamentoVersamenti().getVersamentiDaAnnullare()
                        .getVersamentoDaAnnullare().get(0).getTipoVersamento();
                // flImmediata è un parametro facoltativo ma, in caso non venga inviato, di default risulta essere
                // true quando il tipo versamento è FASCICOLO
                if (TipoVersamentoType.FASCICOLO.value().equals(tipoVersamento.value())
                        && ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isImmediata() == null) {
                    flImmediata = true;
                }
            }
            // end MEV#26446

            // MEV#26446
            String partMsg = (!TipoVersamentoType.UNITA_DOCUMENTARIA.value().equals(tipoVersamento.value()))
                    ? "fascicoli" : "unità documentarie";
            log.info(InvioRichiestaAnnullamentoVersamentiEjb.class.getSimpleName()
                    + " --- Apertura transazione richiesta annullamento versamenti " + partMsg);
            // end MEV#26446

            // Determino se esiste un'altra richiesta con stato corrente pari a INVIO_FALLITO, e in caso la cancello
            ravHelper.deleteRichiestaSePresente(ravExt.getIdStrut(),
                    ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getCodice());

            // MEV#26446
            String tiRichAnnulVers = (!TipoVersamentoType.UNITA_DOCUMENTARIA.value().equals(tipoVersamento.value()))
                    ? CostantiDB.TiRichAnnulVers.FASCICOLI.name() : CostantiDB.TiRichAnnulVers.UNITA_DOC.name();
            // end MEV#26446

            // MEV 30721
            String tiAnnulRichAnnulVers = "ANNULLAMENTO_VERSAMENTO";
            if (ravExt.getModificatoriWS().contains(Costanti.ModificatoriWS.TAG_ANNUL_TIPO_ANNUL)
                    && ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getTipoAnnullamento() != null) {
                if (!ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getTipoAnnullamento().value()
                        .equals(tipoAnnullamento.value())) {
                    tiAnnulRichAnnulVers = "CANCELLAZIONE";
                }
            }

            // Registro la richiesta di annullamento con i relativi item, eseguendo infine i controlli sulle unità
            // documentarie o sui fascicoli
            AroRichAnnulVers richAnnulVers = avEjb.insertRichAnnulVers(user.getIdUtente(), idStrut, codice, descrizione,
                    note, tiRichAnnulVers, ravExt.getDataElaborazione(), flImmediata, flForzaAnnul, flRichiestaPing,
                    tiAnnulRichAnnulVers, ravExt);

            boolean presentiDaAnnulInPing = false;
            for (AroItemRichAnnulVers aroItemRichAnnulVers : richAnnulVers.getAroItemRichAnnulVers()) {
                if (aroItemRichAnnulVers.getTiStatoItem()
                        .equals(CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())) {
                    presentiDaAnnulInPing = true;
                    break;
                }
            }

            // Se la richiesta proviene da PreIngest il sistema elimina tutti gli errori di tipo DA_ANNULLARE_IN_PING
            // per tutte le unità doc
            if (flRichiestaPing) {
                // PERFORMANCE ma se sono appena stati creati da insertRichAnnulVers non poteva evitare di farlo
                // basandosi proprio su
                // flRichiestaPing che ha come parametro di input ???
                avHelper.deleteAroErrRichAnnulVers(richAnnulVers.getIdRichAnnulVers(),
                        CostantiDB.TipoErrRichAnnulVers.DA_ANNULLARE_IN_PING.name());
            }

            // Conto il numero di item presenti nella richiesta e quelli presenti nella richiesta con stato
            // NON_ANNULLABILE
            // PERFORMANCE perché torniamo su DB
            Long numeroItems = avEjb.countItemsInRichAnnulVers(new BigDecimal(richAnnulVers.getIdRichAnnulVers()));
            // PERFORMANCE ancora su DB ?
            Long numeroItemsNonAnnullabili = avEjb.countItemsInRichAnnulVers(
                    new BigDecimal(richAnnulVers.getIdRichAnnulVers()),
                    CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());

            // Ora eseguo altri controlli post elaborazione
            String statoRichiesta = checkPostElaborazione(rispostaWs, numeroItems, numeroItemsNonAnnullabili,
                    flImmediata, presentiDaAnnulInPing, tiRichAnnulVers);

            // Registro lo STATO della richiesta
            avEjb.insertAroStatoRichAnnulVers(richAnnulVers, statoRichiesta, ravExt.getDataElaborazione(), null,
                    user.getIdUtente());
            log.info(InvioRichiestaAnnullamentoVersamentiEjb.class.getSimpleName()
                    + " --- Inserimento richiesta annullamento versamenti " + partMsg
                    + " completata con successo: inseriti " + numeroItems + " item di cui " + numeroItemsNonAnnullabili
                    + " con stato NON_ANNULLABILE ");
            log.info(InvioRichiestaAnnullamentoVersamentiEjb.class.getSimpleName()
                    + " --- Registro su DB gli XML di richiesta ed esito annullamento versamenti " + partMsg);
            // Registro l'XML ricevuto
            // MAC 31064
            AroXmlRichAnnulVers xmlRichAnnulVers = avEjb.createAroXmlRichAnnulVers(richAnnulVers,
                    CostantiDB.TiXmlRichAnnulVers.RICHIESTA.name(), ravExt.getXmlRichiesta(),
                    ravExt.getRichiestaAnnullamentoVersamenti().getVersioneXmlRichiesta());
            ravHelper.insertEntity(xmlRichAnnulVers, true);
            idRichiesta = richAnnulVers.getIdRichAnnulVers();
        }
        return idRichiesta;
    }

    /**
     * Controlli da eseguire successivamente all'elaborazione necessari a rilevare eventuali altri WARNING
     *
     * @param numeroItems
     *            numero item da elaborare
     * @param numeroItemsNonAnnullabili
     *            numero item non annullabili
     * @param flImmediata
     *            flag 1/0 (true/false)
     *
     * @return stato richiesta
     */
    private String checkPostElaborazione(RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs, Long numeroItems,
            Long numeroItemsNonAnnullabili, boolean flImmediata, boolean presentiDaAnnulInPing,
            String tiRichAnnulVers) {
        String statoRichiesta = null;
        CodiceEsitoType codiceEsito = null;
        String codiceErrore = null;
        String messaggioErrore = null;
        if (numeroItems.intValue() == numeroItemsNonAnnullabili.intValue()) {
            statoRichiesta = CostantiDB.StatoRichAnnulVers.INVIO_FALLITO.name();
            // Imposto esito e codice errore RICH_ANN_VERS_011 o RICH_ANN_VERS_022
            codiceEsito = CodiceEsitoType.NEGATIVO;
            // MEV#26446
            codiceErrore = (tiRichAnnulVers.equals(CostantiDB.TiRichAnnulVers.UNITA_DOC.name()))
                    ? MessaggiWSBundle.RICH_ANN_VERS_011 : MessaggiWSBundle.RICH_ANN_VERS_022;
            // end MEV#26446
            messaggioErrore = MessaggiWSBundle.getString(codiceErrore);
        } else {
            if (flImmediata) {
                if (presentiDaAnnulInPing) {
                    statoRichiesta = CostantiDB.StatoRichAnnulVers.INVIO_FALLITO.name();

                    codiceEsito = CodiceEsitoType.NEGATIVO;
                    codiceErrore = MessaggiWSBundle.RICH_ANN_VERS_013;
                    messaggioErrore = MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_013);
                } else {
                    statoRichiesta = CostantiDB.StatoRichAnnulVers.CHIUSA.name();
                }
            } else {
                statoRichiesta = CostantiDB.StatoRichAnnulVers.APERTA.name();
            }
        }

        if (codiceEsito != null) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta().setCodiceEsito(codiceEsito);
        }
        if (codiceErrore != null) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta().setCodiceErrore(codiceErrore);
        }
        if (messaggioErrore != null) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta()
                    .setMessaggioErrore(messaggioErrore);
        }
        return statoRichiesta;
    }

    private String generaRisposta(RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt, Long numeroItems, Long numeroItemsNonAnnullabili,
            AroRichAnnulVers richAnnulVers)
            throws IndexOutOfBoundsException, IOException, MarshalException, ValidationException, JAXBException {
        // Preparo la risposta
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta()
                .setNumeroVersamentiDaAnnullare(numeroItems != null ? BigInteger.valueOf(numeroItems) : null);
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta().setNumeroVersamentiNonAnnullabili(
                numeroItemsNonAnnullabili != null ? BigInteger.valueOf(numeroItemsNonAnnullabili) : null);
        // Determino gli item della richiesta
        List<AroVLisItemRichAnnvrs> itemList = ravHelper
                .getAroVLisItemRichAnnvrs(new BigDecimal(richAnnulVers.getIdRichAnnulVers()));
        if (!itemList.isEmpty()) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti()
                    .setVersamentiDaAnnullare(new EsitoRichiestaAnnullamentoVersamenti.VersamentiDaAnnullare());
            if (rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersioneXmlEsito().equals("1.4")) {
                it.eng.parer.ws.xml.esitoRichAnnullVers.TipoAnnullamentoType tiAnnullamento = richAnnulVers
                        .getTiAnnullamento()
                        .equals(it.eng.parer.ws.xml.esitoRichAnnullVers.TipoAnnullamentoType.CANCELLAZIONE.value())
                                ? it.eng.parer.ws.xml.esitoRichAnnullVers.TipoAnnullamentoType.CANCELLAZIONE
                                : it.eng.parer.ws.xml.esitoRichAnnullVers.TipoAnnullamentoType.ANNULLAMENTO_VERSAMENTO;
                rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta().setTipoAnnullamento(tiAnnullamento);
            }
            for (AroVLisItemRichAnnvrs item : itemList) {
                // Creo il record del versamento da annullare da dare in risposta
                VersamentoDaAnnullareType versamentoDaAnnullare = new VersamentoDaAnnullareType();
                // MEV#26446
                if (item.getTiItemRichAnnulVers().equals(CostantiDB.TipiEntitaSacer.UNI_DOC.name())) {
                    versamentoDaAnnullare.setTipoVersamento(
                            it.eng.parer.ws.xml.esitoRichAnnullVers.TipoVersamentoType.UNITA_DOCUMENTARIA);
                    //
                    versamentoDaAnnullare.setTipoRegistro(item.getCdRegistroKeyUnitaDoc());
                    versamentoDaAnnullare.setAnno(item.getAaKeyUnitaDoc().intValue());
                    versamentoDaAnnullare.setNumero(item.getCdKeyUnitaDoc());
                } else if (item.getTiItemRichAnnulVers().equals(CostantiDB.TipiEntitaSacer.FASC.name())) {
                    versamentoDaAnnullare
                            .setTipoVersamento(it.eng.parer.ws.xml.esitoRichAnnullVers.TipoVersamentoType.FASCICOLO);
                    //
                    versamentoDaAnnullare.setAnno(item.getAaFascicolo().intValue());
                    versamentoDaAnnullare.setNumero(item.getCdKeyFascicolo());
                }
                // end MEV#26446
                //
                if (rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersioneXmlEsito().equals("1.2")
                        || rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersioneXmlEsito().equals("1.3")
                        || rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersioneXmlEsito().equals("1.4")) {
                    versamentoDaAnnullare.setStato(item.getTiStatoItem());
                }
                versamentoDaAnnullare.setErroriRilevati(item.getDsListaErr());
                rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersamentiDaAnnullare()
                        .getVersamentoDaAnnullare().add(versamentoDaAnnullare);
            }
        }
        log.info(InvioRichiestaAnnullamentoVersamentiEjb.class.getSimpleName()
                + " --- Registro su DB gli XML di richiesta ed esito annullamento versamenti unità documentarie");
        // Registro l'XML di risposta
        String xmlRisposta = avEjb
                .marshallaEsitoRichiestaAnnullamentoVersamenti(rispostaWs.getEsitoRichiestaAnnullamentoVersamenti());
        return xmlRisposta;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void evasioneRichiestaAnnullamento(Long idRichAnnulVers, long idUserIam, boolean isFromPreingest)
            throws ParerUserError {
        log.debug("locking AroRichAnnulVers {} ...", idRichAnnulVers);
        AroRichAnnulVers richiestaAnnullamento = ravHelper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        log.debug("... locked");
        // Controlli item
        log.debug("controlloItemWsRichiestaAnnul - INIZIO");
        avEjb.controlloItemWsRichiestaAnnul(BigDecimal.valueOf(idRichAnnulVers), idUserIam, isFromPreingest);
        log.debug("controlloItemWsRichiestaAnnul - FINE");
        // Evasione richiesta
        log.debug("evasioneRichiestaAnnullamento - INIZIO");
        avEjb.evasioneRichiestaAnnullamento(richiestaAnnullamento);
        log.debug("evasioneRichiestaAnnullamento - FINE");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AroXmlRichAnnulVers createAroXmlRichAnnulVers(AroRichAnnulVers richAnnulVers, String tiXmlRichAnnulVers,
            String blXmlRichAnnulVers, String cdVersioneXml) {
        return avEjb.createAroXmlRichAnnulVers(richAnnulVers, tiXmlRichAnnulVers, blXmlRichAnnulVers, cdVersioneXml);
    }

    /**
     * Inizializza la risposta WS ed effettua contestualmente un controllo sulla versione impostata nella richiesta
     *
     * @param rispostaWs
     *            Risposta di cui inizializzare i campi
     * @param ravExt
     *            Richiesta esterna originale
     * @param now
     *            data da impostare come data dell'esito della risposta
     */
    public void initRispostaWs(RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt, Date now) {
        log.debug("Inizializzazione Risposta WS Invio Richiesta Annullamento Versamenti");

        /* Setto rispostaWS ed Esito in POSITIVO */
        rispostaWs.setSeverity(SeverityEnum.OK);
        rispostaWs.setErrorCode("");
        rispostaWs.setErrorMessage("");
        rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(new EsitoRichiestaAnnullamentoVersamenti());
        RispostaControlli rs = this.loadWsVersions(ravExt);
        // Se il controllo sulla versione è ha dato esito positivo metto a POSITIVO anche la risposta generale
        if (rs.isrBoolean()) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().setEsitoRichiesta(new EsitoRichiestaType());
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getEsitoRichiesta()
                    .setCodiceEsito(CodiceEsitoType.POSITIVO);
        } else {
            // qualcosa non va sulle versioni
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(rs.getCodErr(), rs.getDsErr());
        }
        // Imposto VersioneXmlEsito e Data richiesta odierna
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti()
                .setVersioneXmlEsito(ravExt.getDescrizione().getVersione(ravExt.getWsVersions()));
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti()
                .setVersioneXmlRichiesta(ravExt.getDescrizione().getVersione(ravExt.getWsVersions()));
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti()
                .setDataRichiesta(XmlDateUtility.dateToXMLGregorianCalendar(now));
        //
        ravExt.setParametriParser(new ParametriParser());
        ravExt.setDataElaborazione(now);
    }

    /**
     * Controlla che l'utente che attiva il servizio sia presente nella tabella IAM_USER, che sia attivo e che la sua
     * password sia corretta e non scaduta
     *
     * @param loginName
     *            login
     * @param password
     *            password
     * @param indirizzoIp
     *            indirizzo IP
     * @param rispostaWs
     *            bean RispostaWSInvioRichiestaAnnullamentoVersamenti
     * @param ravExt
     *            bean InvioRichiestaAnnullamentoVersamentiExt
     */
    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
            RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs, InvioRichiestaAnnullamentoVersamentiExt ravExt) {
        RispostaControlli tmpRispostaControlli = controlliWS.checkCredenziali(loginName, password, indirizzoIp,
                TipiWSPerControlli.ANNULLAMENTO);
        if (!tmpRispostaControlli.isrBoolean()) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(tmpRispostaControlli.getCodErr(), tmpRispostaControlli.getDsErr());
            // Popolo i tag da dare in risposta in questo caso
            // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(), ravExt.getDataElaborazione());
            // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
        } else {
            // Decoro l'ext
            ravExt.setNmUserid(loginName);
            ravExt.setUser((User) tmpRispostaControlli.getrObject());
        }
    }

    /**
     * Controlla che la versione XSD (cioè XML cioè WS) sia tra quelle consentite
     *
     * @param versione
     *            versione
     * @param rispostaWs
     *            bean RispostaWSInvioRichiestaAnnullamentoVersamenti
     * @param ravExt
     *            bean InvioRichiestaAnnullamentoVersamentiExt
     */
    public void verificaVersione(String versione, RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt) {
        EsitoRichiestaAnnullamentoVersamenti myEsito = rispostaWs.getEsitoRichiestaAnnullamentoVersamenti();
        RispostaControlli tmpRispostaControlli = null;
        tmpRispostaControlli = controlliWS.checkVersione(versione, ravExt.getDescrizione().getNomeWs(),
                ravExt.getWsVersions(), TipiWSPerControlli.ANNULLAMENTO);
        if (!tmpRispostaControlli.isrBoolean()) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(tmpRispostaControlli.getCodErr(), tmpRispostaControlli.getDsErr());
            // Popolo i tag da dare in risposta in questo caso
            // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(), ravExt.getDataElaborazione());
            // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
        } else {
            ravExt.checkVersioneRequest(versione);
            myEsito.setVersioneXmlEsito(ravExt.getVersioneCalc());
            myEsito.setVersioneXmlRichiesta(ravExt.getVersioneCalc());
        }
    }

    public void parseXML(String datiXml, RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs,
            InvioRichiestaAnnullamentoVersamentiExt ravExt) {

        if (ravExt.getUser() == null) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, "Errore: l'utente non è autenticato.");
            return;
        }

        try {
            InvioRichiestaAnnullamentoVersamentiParser ravParser = new InvioRichiestaAnnullamentoVersamentiParser(
                    rispostaWs);
            ravParser.parseXML(datiXml, ravExt);
        } catch (Exception e) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella fase di parsing dell'XML del EJB " + e.getMessage());
            log.error("Eccezione nella fase di parsing dell'XML del EJB ", e);
        }
    }

    protected RispostaControlli loadWsVersions(InvioRichiestaAnnullamentoVersamentiExt ext) {
        RispostaControlli rs = controlliWS.loadWsVersions(ext.getDescrizione());
        // if positive ...
        if (rs.isrBoolean()) {
            ext.setWsVersions((HashMap<String, String>) rs.getrObject());
        }
        return rs;
    }
}
