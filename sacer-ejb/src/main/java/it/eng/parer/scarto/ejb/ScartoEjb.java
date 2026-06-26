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
package it.eng.parer.scarto.ejb;

import com.csvreader.CsvReader;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.annulVers.dto.UnitaDocBean;
import it.eng.parer.entity.*;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.viewEntity.*;
import it.eng.parer.web.ejb.DataMartEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.entity.constraint.AplValoreParamApplic;
import it.eng.parer.entity.dto.ReportScartoUdDTO;
import it.eng.parer.scarto.dto.FiltriRicercaUdScartoDto;
import it.eng.parer.scarto.helper.ScartoHelper;
import it.eng.parer.slite.gen.tablebean.AroPropScartoVersRowBean;
import static it.eng.parer.util.Utils.bigDecimalFromLong;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ScartoEjb {

    private static final Logger logger = LoggerFactory.getLogger(ScartoEjb.class);
    private static final String LOG_MESSAGE_SCARTO = "Scarto Versamenti Unit\u00E0 Documentarie --- ";

    @Resource
    private SessionContext context;
    @EJB
    private ScartoHelper helper;
    @EJB
    private UnitaDocumentarieHelper udHelper;
    // MEV #30725
    @EJB
    private DataMartEjb dataMartEjb;
    // end MEV #30725
    @EJB
    private ConfigurationHelper configurationHelper;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /**
     * Ricerca le Proposte di Scarto e mappa i risultati in AroVRicPropScartoVersTableBean
     *
     * @param idUserIam                  utente che effettua la ricerca
     * @param idStrut                    struttura
     * @param cdPropScartoVers           codice proposta
     * @param dtCreazionPropScartoVersDa data creazione proposta da
     * @param dtCreazionPropScartoVersA  data creazione proposta a
     * @param dtUltimaModScartoVersA     data ultima modifica proposta da
     * @param dtUltimaModScartoVersDa    data ultima modifica proposta a
     * @param tiStatoProp                stato proposta
     * @return AroVRicPropScartoVersTableBean popolato
     * @throws it.eng.spagoCore.error.EMFError eccezione
     */
    public AroVRicPropScartoVersTableBean ricercaProposteScarto(long idUserIam, BigDecimal idStrut,
            String cdPropScartoVers, Date dtCreazionPropScartoVersDa,
            Date dtCreazionPropScartoVersA, Date dtUltimaModScartoVersDa,
            Date dtUltimaModScartoVersA, String tiStatoProp) throws EMFError {

        AroVRicPropScartoVersTableBean proposteTableBean = new AroVRicPropScartoVersTableBean();
        List<AroVRicPropScartoVers> proposteList = helper.getAroVRicPropScartoVersList(idUserIam,
                idStrut, cdPropScartoVers, dtCreazionPropScartoVersDa, dtCreazionPropScartoVersA,
                dtUltimaModScartoVersDa, dtUltimaModScartoVersA, tiStatoProp);

        try {
            for (AroVRicPropScartoVers propScarto : proposteList) {
                AroVRicPropScartoVersRowBean propScartoVersRowBean = (AroVRicPropScartoVersRowBean) Transform
                        .entity2RowBean(propScarto);

                propScartoVersRowBean.setString("amb_ente_strut",
                        propScartoVersRowBean.getNmAmbiente() + " - "
                                + propScartoVersRowBean.getNmEnte() + " - "
                                + propScartoVersRowBean.getNmStrut());

                proposteTableBean.add(propScartoVersRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista delle proposte di scarto", e);
        }

        return proposteTableBean;
    }

    /**
     * Crea e salva la testata della Proposta di Scarto e ne inizializza lo stato ad APERTA.
     *
     * @param dsPropScartoVers Descrizione della proposta
     * @param ntPropScartoVers Eventuali note (può essere null)
     * @param idStrut          ID della struttura dell'utente
     * @param idUserIam        ID dell'utente loggato che crea la proposta
     * @return ID della proposta appena creata
     * @throws ParerUserError In caso di validazioni fallite (es. Codice già esistente)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long creaTestataPropostaScarto(String dsPropScartoVers, String ntPropScartoVers,
            BigDecimal idStrut, long idUserIam) throws ParerUserError {

        Date now = Calendar.getInstance().getTime();
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);

        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);

            // 1. Calcolo del nuovo progressivo
            BigDecimal nuovoPg = helper.getNextPgPropScartoVers(idStrut, annoCorrente);

            // 2. Creazione Entity Proposta (Testata)
            AroPropScartoVers proposta = new AroPropScartoVers();
            proposta.setOrgStrut(strut);
            proposta.setPgPropScartoVers(nuovoPg);
            // Non setto l'anno perché è una colonna virtuale calcolata dal DB
            proposta.setDsPropScartoVers(dsPropScartoVers);
            proposta.setNtPropScartoVers(ntPropScartoVers);
            proposta.setDtCreazione(now);
            proposta.setDtUltimaMod(now);

            // 3. Salvo la proposta su DB (genererà ID_PROP_SCARTO_VERS)
            helper.insertPropostaScarto(proposta);

            IamUser utente = helper.findById(IamUser.class, idUserIam);
            // 4. Creazione Entity Stato (imposto lo stato iniziale APERTA)
            AroStatoPropScartoVers stato = new AroStatoPropScartoVers();
            stato.setAroPropScartoVers(proposta);
            stato.setPgStatoPropScartoVers(BigDecimal.ONE); // È il primo stato
            stato.setTiStatoPropScartoVers(CostantiDB.TiStatoPropScartoVers.APERTA.name());
            stato.setDtRegStatoPropScartoVers(now);
            stato.setDsNotaPropScartoVers("Creazione proposta di scarto");
            stato.setIamUser(utente);

            // 5. Salvo lo stato su DB (genererà ID_STATO_PROP_SCARTO_VERS)
            helper.insertStatoPropostaScarto(stato);

            // 6. Aggiorno la proposta con l'ID dello stato corrente
            proposta.setIdStatoPropScartoVersCor(
                    BigDecimal.valueOf(stato.getIdStatoPropScartoVers()));

            return proposta.getIdPropScartoVers();
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante la creazione della proposta di scarto : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Errore imprevisto durante la creazione della proposta di scarto.");
        }
    }

    /**
     * Carica il dettaglio proposta di scarto versamenti dato l'id proposta
     *
     * @param idPropScartoVers id della proposta
     *
     * @return rowBean della vista
     */
    public AroPropScartoVersRowBean getAroPropScartoVersRowBean(BigDecimal idPropScartoVers) {
        AroPropScartoVers proposta = helper.findById(AroPropScartoVers.class, idPropScartoVers);
        AroPropScartoVersRowBean row = null;
        if (proposta != null) {
            try {
                row = (AroPropScartoVersRowBean) Transform.entity2RowBean(proposta);
                row.setBigDecimal("id_ambiente", BigDecimal.valueOf(
                        proposta.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()));
                row.setBigDecimal("id_ente",
                        BigDecimal.valueOf(proposta.getOrgStrut().getOrgEnte().getIdEnte()));
                row.setBigDecimal("id_strut",
                        BigDecimal.valueOf(proposta.getOrgStrut().getIdStrut()));
                Calendar cal = Calendar.getInstance();
                cal.setTime(proposta.getDtCreazione());
                int anno = cal.get(Calendar.YEAR);
                row.setString("cd_prop_scarto_vers", anno + "_" + row.getPgPropScartoVers());

                for (AroStatoPropScartoVers stato : proposta.getAroStatoPropScartoVers()) {
                    if (stato.getIdStatoPropScartoVers() == (proposta.getIdStatoPropScartoVersCor()
                            .longValue())) {
                        row.setString("ti_stato_prop_scarto_vers",
                                stato.getTiStatoPropScartoVers());
                        break;
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della proposta di scarto versamenti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della proposta di scarto versamenti");
            }
        }
        return row;
    }

    /**
     * Aggiorna i dati anagrafici di una Proposta di Scarto esistente.
     *
     * @param idPropScartoVers L'ID della proposta da aggiornare
     * @param dsPropScartoVers La nuova descrizione
     * @param ntPropScartoVers Le nuove note
     * @throws it.eng.parer.exception.ParerUserError errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiornaTestataPropostaScarto(BigDecimal idPropScartoVers, String dsPropScartoVers,
            String ntPropScartoVers) throws ParerUserError {
        try {
            // Recupero l'entità esistente dal DB
            AroPropScartoVers proposta = helper.findById(AroPropScartoVers.class, idPropScartoVers);

            if (proposta == null) {
                throw new ParerUserError("Errore: Impossibile trovare la proposta da aggiornare.");
            }

            // Aggiorno solo i campi consentiti
            proposta.setDsPropScartoVers(dsPropScartoVers);
            proposta.setNtPropScartoVers(ntPropScartoVers);
            // Aggiorno data ultima modifica
            proposta.setDtUltimaMod(Calendar.getInstance().getTime());

            helper.mergeEntity(proposta);

            logger.info("Aggiornata testata Proposta Scarto ID: {}", idPropScartoVers);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante l'aggiornamento della proposta di scarto: "
                    + ex.getMessage(), ex);
            throw new ParerUserError(
                    "Errore imprevisto durante l'aggiornamento della proposta di scarto.");
        }
    }

    /**
     * Avvia la richiesta di autorizzazione per una Proposta di Scarto: salva i dati del
     * provvedimento di richiesta e transita lo stato in DA_AUTORIZZARE.
     *
     * @param idPropScartoVers  ID della proposta da inviare ad autorizzazione
     * @param ntAutorita        Note/identificativo dell'autorità
     * @param cdRegistroRichAut Registro del provvedimento di richiesta
     * @param aaRichAut         Anno del provvedimento di richiesta
     * @param cdRichAut         Numero del provvedimento di richiesta
     * @param idUserIam         ID dell'utente che esegue l'operazione
     *
     * @throws ParerUserError se la proposta non è in stato APERTA o in caso di errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void avviaRichiestaAutorizzazione(BigDecimal idPropScartoVers, String ntAutorita,
            String cdRegistroRichAut, BigDecimal aaRichAut, String cdRichAut, long idUserIam)
            throws ParerUserError {
        try {
            AroPropScartoVers proposta = helper.findByIdWithLock(AroPropScartoVers.class,
                    idPropScartoVers);
            if (proposta == null) {
                throw new ParerUserError("Proposta di scarto non trovata.");
            }

            // Verifica stato corrente: deve essere APERTA
            AroStatoPropScartoVers statoCorrente = helper.findById(AroStatoPropScartoVers.class,
                    proposta.getIdStatoPropScartoVersCor());
            if (!CostantiDB.TiStatoPropScartoVers.APERTA.name()
                    .equals(statoCorrente.getTiStatoPropScartoVers())) {
                throw new ParerUserError(
                        "La proposta non è in stato APERTA: impossibile avviare la richiesta di autorizzazione.");
            }

            // Salva i dati della richiesta di autorizzazione sulla proposta
            proposta.setNtAutorita(ntAutorita);
            proposta.setCdRegistroRichAut(cdRegistroRichAut);
            proposta.setAaRichAut(aaRichAut);
            proposta.setCdRichAut(cdRichAut);
            proposta.setDtUltimaMod(Calendar.getInstance().getTime());

            // Crea il nuovo stato DA_AUTORIZZARE
            IamUser utente = helper.findById(IamUser.class, idUserIam);
            AroStatoPropScartoVers nuovoStato = new AroStatoPropScartoVers();
            nuovoStato.setAroPropScartoVers(proposta);
            nuovoStato.setPgStatoPropScartoVers(
                    statoCorrente.getPgStatoPropScartoVers().add(BigDecimal.ONE));
            nuovoStato.setTiStatoPropScartoVers(
                    CostantiDB.TiStatoPropScartoVers.DA_AUTORIZZARE.name());
            nuovoStato.setDtRegStatoPropScartoVers(Calendar.getInstance().getTime());
            nuovoStato.setDsNotaPropScartoVers("Richiesta di autorizzazione avviata");
            nuovoStato.setIamUser(utente);

            helper.insertStatoPropostaScarto(nuovoStato);
            proposta.setIdStatoPropScartoVersCor(
                    BigDecimal.valueOf(nuovoStato.getIdStatoPropScartoVers()));

            logger.info(
                    "Proposta Scarto ID: {} transitata in stato DA_AUTORIZZARE dall'utente ID: {}",
                    idPropScartoVers, idUserIam);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(
                    "Errore durante l'avvio della richiesta di autorizzazione: " + ex.getMessage(),
                    ex);
            throw new ParerUserError(
                    "Errore imprevisto durante l'avvio della richiesta di autorizzazione.");
        }
    }

    /**
     * Registra l'autorizzazione (risposta dell'autorità) su una Proposta di Scarto. Salva i dati
     * del provvedimento di risposta e transita lo stato in AUTORIZZATA. In caso di autorizzazione
     * PARZIALE la proposta rimane modificabile per la rimozione delle UD non autorizzate.
     *
     * @param idPropScartoVers      ID della proposta
     * @param cdRegistroRispAut     Registro di risposta
     * @param aaRispAut             Anno di risposta
     * @param cdRispAut             Numero di risposta
     * @param tiAutorizzazione      COMPLETA o PARZIALE
     * @param cdRegistroProvvScarto Registro del provvedimento di scarto
     * @param aaProvvScarto         Anno del provvedimento di scarto
     * @param idUserIam             ID dell'utente che esegue l'operazione
     * @param dsFirmatoDa           Firmatario del provvedimento di scarto
     * @param cdProvvScarto         Numero del provvedimento di scarto
     * @return Id della richiesta di scarto creata
     *
     * @throws ParerUserError se la proposta non è in stato DA_AUTORIZZARE o in caso di errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long registraAutorizzazione(BigDecimal idPropScartoVers, String cdRegistroRispAut,
            BigDecimal aaRispAut, String cdRispAut, String tiAutorizzazione,
            String cdRegistroProvvScarto, BigDecimal aaProvvScarto, String cdProvvScarto,
            String dsFirmatoDa, long idUserIam) throws ParerUserError {
        Long idRichScartoVers = null;
        try {
            AroPropScartoVers proposta = helper.findByIdWithLock(AroPropScartoVers.class,
                    idPropScartoVers);
            if (proposta == null) {
                throw new ParerUserError("Proposta di scarto non trovata.");
            }

            // Verifica stato corrente: deve essere DA_AUTORIZZARE
            AroStatoPropScartoVers statoCorrente = helper.findById(AroStatoPropScartoVers.class,
                    proposta.getIdStatoPropScartoVersCor());
            if (!CostantiDB.TiStatoPropScartoVers.DA_AUTORIZZARE.name()
                    .equals(statoCorrente.getTiStatoPropScartoVers())) {
                throw new ParerUserError(
                        "La proposta non è in stato DA_AUTORIZZARE: impossibile registrare l'autorizzazione.");
            }

            // Valida il tipo di autorizzazione
            CostantiDB.TiAutorizzazionePropScartoVers tiAut;
            try {
                tiAut = CostantiDB.TiAutorizzazionePropScartoVers.valueOf(tiAutorizzazione);
            } catch (IllegalArgumentException ex) {
                throw new ParerUserError(
                        "Tipo autorizzazione non valido: " + tiAutorizzazione);
            }

            // Per autorizzazione COMPLETA, pre-verifica unicità del codice richiesta PRIMA del
            // cambio di stato: se il codice esiste già la ParerUserError viene lanciata con la
            // proposta ancora in DA_AUTORIZZARE, evitando che finisca in AUTORIZZATA senza
            // richiesta.
            if (tiAut == CostantiDB.TiAutorizzazionePropScartoVers.COMPLETA) {
                Calendar calPre = Calendar.getInstance();
                calPre.setTime(proposta.getDtCreazione());
                String cdRichPrev = proposta.getPgPropScartoVers().toPlainString() + "/"
                        + calPre.get(Calendar.YEAR);
                if (helper.isRichScartoVersExisting(cdRichPrev,
                        BigDecimal.valueOf(proposta.getOrgStrut().getIdStrut()))) {
                    throw new ParerUserError(
                            "Impossibile registrare l'autorizzazione: esiste gi\u00e0 una richiesta "
                                    + "di scarto con codice '" + cdRichPrev
                                    + "' per questa struttura. Verificare se la proposta è già "
                                    + "stata autorizzata in precedenza.");
                }
            }

            // Salva i dati della risposta dell'autorità sulla proposta
            proposta.setCdRegistroRispAut(cdRegistroRispAut);
            proposta.setAaRispAut(aaRispAut);
            proposta.setCdRispAut(cdRispAut);
            proposta.setTiAutorizzazione(tiAut.name());
            // Salva i dati del provvedimento di scarto
            proposta.setCdRegistroProvvScarto(cdRegistroProvvScarto);
            proposta.setAaProvvScarto(aaProvvScarto);
            proposta.setCdProvvScarto(cdProvvScarto);
            proposta.setDsFirmatoDa(dsFirmatoDa);
            proposta.setDtUltimaMod(Calendar.getInstance().getTime());

            // Per autorizzazione PARZIALE lo stato torna ad APERTA per consentire la modifica
            // in sottrazione; per COMPLETA la proposta viene chiusa definitivamente.
            String nuovoStatoStr;
            String notaStato;
            if (tiAut == CostantiDB.TiAutorizzazionePropScartoVers.PARZIALE) {
                nuovoStatoStr = CostantiDB.TiStatoPropScartoVers.APERTA_REVISIONE.name();
                notaStato = "Autorizzazione PARZIALE registrata: rimuovere le UD non autorizzate e completare la revisione";
                // Snapshot del numero di UD presenti al momento dell'autorizzazione
                long niUd = helper.countItemsByTipo(idPropScartoVers.longValue(),
                        CostantiDB.TiItemPropScartoVers.UNI_DOC.name());
                proposta.setNiUdPreRevisione(BigDecimal.valueOf(niUd));
            } else {
                nuovoStatoStr = CostantiDB.TiStatoPropScartoVers.AUTORIZZATA.name();
                notaStato = "Autorizzazione COMPLETA registrata: proposta autorizzata";
            }

            IamUser utente = helper.findById(IamUser.class, idUserIam);
            AroStatoPropScartoVers nuovoStato = new AroStatoPropScartoVers();
            nuovoStato.setAroPropScartoVers(proposta);
            nuovoStato.setPgStatoPropScartoVers(
                    statoCorrente.getPgStatoPropScartoVers().add(BigDecimal.ONE));
            nuovoStato.setTiStatoPropScartoVers(nuovoStatoStr);
            nuovoStato.setDtRegStatoPropScartoVers(Calendar.getInstance().getTime());
            nuovoStato.setDsNotaPropScartoVers(notaStato);
            nuovoStato.setIamUser(utente);

            helper.insertStatoPropostaScarto(nuovoStato);
            proposta.setIdStatoPropScartoVersCor(
                    BigDecimal.valueOf(nuovoStato.getIdStatoPropScartoVers()));

            // Per autorizzazione COMPLETA crea subito la richiesta di scarto
            if (tiAut == CostantiDB.TiAutorizzazionePropScartoVers.COMPLETA) {
                idRichScartoVers = creaRichScartoVersFromProposta(proposta, utente,
                        Calendar.getInstance().getTime(), idUserIam);
            }

            logger.info(
                    "Proposta Scarto ID: {} autorizzazione {} registrata dall'utente ID: {}",
                    idPropScartoVers, tiAut.name(), idUserIam);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore durante la registrazione dell'autorizzazione: " + ex.getMessage(),
                    ex);
            throw new ParerUserError(
                    "Errore imprevisto durante la registrazione dell'autorizzazione.");
        }
        return idRichScartoVers;
    }

    /**
     * Completa la revisione di una Proposta di Scarto che si trova in stato APERTA_REVISIONE (dopo
     * autorizzazione PARZIALE). Transita lo stato in AUTORIZZATA senza richiedere ulteriori dati di
     * autorizzazione (già registrati in precedenza).
     *
     * @param idPropScartoVers ID della proposta
     * @param idUserIam        ID dell'utente che esegue l'operazione
     * @return Id della richiesta di scarto creata
     *
     * @throws ParerUserError se la proposta non è in stato APERTA_REVISIONE o in caso di errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long completaRevisionePropostaScarto(BigDecimal idPropScartoVers, long idUserIam)
            throws ParerUserError {
        Long idRichScartoVers = null;
        try {
            AroPropScartoVers proposta = helper.findByIdWithLock(AroPropScartoVers.class,
                    idPropScartoVers);
            if (proposta == null) {
                throw new ParerUserError("Proposta di scarto non trovata.");
            }

            AroStatoPropScartoVers statoCorrente = helper.findById(AroStatoPropScartoVers.class,
                    proposta.getIdStatoPropScartoVersCor());
            if (!CostantiDB.TiStatoPropScartoVers.APERTA_REVISIONE.name()
                    .equals(statoCorrente.getTiStatoPropScartoVers())) {
                throw new ParerUserError(
                        "La proposta non è in stato APERTA_REVISIONE: impossibile completare la revisione.");
            }

            // Verifica che sia stata rimossa almeno una UD rispetto allo snapshot pre-revisione
            BigDecimal niUdPreRevisione = proposta.getNiUdPreRevisione();
            if (niUdPreRevisione != null) {
                long niUdAttuali = helper.countItemsByTipo(idPropScartoVers.longValue(),
                        CostantiDB.TiItemPropScartoVers.UNI_DOC.name());
                if (niUdAttuali >= niUdPreRevisione.longValue()) {
                    throw new ParerUserError(
                            "Impossibile completare la revisione: l'autorizzazione era PARZIALE ma non è stata rimossa alcuna unità documentaria dalla proposta. "
                                    + "Rimuovere almeno una UD non autorizzata prima di procedere.");
                }
            }

            proposta.setDtUltimaMod(Calendar.getInstance().getTime());

            // Pre-verifica unicità del codice richiesta PRIMA del cambio di stato: se il codice
            // esiste già la ParerUserError viene lanciata con la proposta ancora in
            // APERTA_REVISIONE.
            Calendar calPre = Calendar.getInstance();
            calPre.setTime(proposta.getDtCreazione());
            String cdRichPrev = proposta.getPgPropScartoVers().toPlainString() + "/"
                    + calPre.get(Calendar.YEAR);
            if (helper.isRichScartoVersExisting(cdRichPrev,
                    BigDecimal.valueOf(proposta.getOrgStrut().getIdStrut()))) {
                throw new ParerUserError(
                        "Impossibile completare la revisione: esiste gi\u00e0 una richiesta "
                                + "di scarto con codice '" + cdRichPrev
                                + "' per questa struttura. Verificare se la proposta è già "
                                + "stata processata in precedenza.");
            }

            IamUser utente = helper.findById(IamUser.class, idUserIam);
            AroStatoPropScartoVers nuovoStato = new AroStatoPropScartoVers();
            nuovoStato.setAroPropScartoVers(proposta);
            nuovoStato.setPgStatoPropScartoVers(
                    statoCorrente.getPgStatoPropScartoVers().add(BigDecimal.ONE));
            nuovoStato.setTiStatoPropScartoVers(
                    CostantiDB.TiStatoPropScartoVers.AUTORIZZATA.name());
            nuovoStato.setDtRegStatoPropScartoVers(Calendar.getInstance().getTime());
            nuovoStato.setDsNotaPropScartoVers(
                    "Revisione completata: proposta autorizzata dopo rimozione UD non autorizzate");
            nuovoStato.setIamUser(utente);

            helper.insertStatoPropostaScarto(nuovoStato);
            proposta.setIdStatoPropScartoVersCor(
                    BigDecimal.valueOf(nuovoStato.getIdStatoPropScartoVers()));

            // Revisione completata → proposta AUTORIZZATA: crea la richiesta di scarto
            idRichScartoVers = creaRichScartoVersFromProposta(proposta, utente,
                    Calendar.getInstance().getTime(), idUserIam);

            logger.info(
                    "Proposta Scarto ID: {} revisione completata → AUTORIZZATA dall'utente ID: {}",
                    idPropScartoVers, idUserIam);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore durante il completamento della revisione: " + ex.getMessage(), ex);
            throw new ParerUserError("Errore imprevisto durante il completamento della revisione.");
        }
        return idRichScartoVers;
    }

    /**
     * Recupera i dati del Report delle UD per le Proposte di Scarto, mappando i risultati della
     * query nativa nel TableBean per la UI e calcolando i totali per il riepilogo.
     *
     * @param filtri         i filtri di ricerca
     * @param idPropCorrente proposta corrente
     * @return DTO contenente il TableBean, il totale UD e la stringa degli anni di riferimento
     */
    public ReportScartoUdDTO calcolaReportUdPerScarto(FiltriRicercaUdScartoDto filtri,
            Long idPropCorrente) {

        ReportScartoUdDTO resultDto = new ReportScartoUdDTO();
        BaseTable tableBean = new BaseTable();

        // 1. Inizializzo la mappa dei totali per colonna (Aggiunto CONFLITTI)
        Map<String, Long> totaliPerColonna = new HashMap<>();
        totaliPerColonna.put(Constants.TiAlertPropScarto.RAGGIUNTO.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.NON_RAGGIUNTO.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.SENZA_INDICAZIONE.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.ILLIMITATA.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.CONFLITTI.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.IN_ALTRE_PROPOSTE.name(), 0L);
        totaliPerColonna.put(Constants.TiAlertPropScarto.TOTALE.name(), 0L);

        // 2. Invoco l'Helper per la query Nativa
        List<Object[]> rawResults = helper.getReportUdScartoNative(filtri, idPropCorrente);

        int counterTotaleUd = 0;

        // 3. Ciclo i risultati nativi e popolo il TableBean e la Mappa
        if (rawResults != null && !rawResults.isEmpty()) {
            for (Object[] row : rawResults) {

                // --- ESTRAZIONE DATI SICURA ---
                // NOTA: Assicurati che questi indici rispecchino l'ordine esatto della SELECT
                // nell'Helper
                String valTipologia = (String) row[0];
                long valTotale = ((Number) row[1]).longValue(); // COUNT
                long valRaggiunto = ((Number) row[2]).longValue();
                long valNonRagg = ((Number) row[3]).longValue();
                long valSenzaInd = ((Number) row[4]).longValue();
                long valIllimitata = ((Number) row[5]).longValue();
                long valConflitti = ((Number) row[6]).longValue(); // <--- NUOVA
                long valInAltre = ((Number) row[7]).longValue(); // SCALATA DI 1

                // --- AGGIORNAMENTO TOTALI GENERALI (Per le intestazioni/pie' di pagina) ---
                counterTotaleUd += valTotale;

                totaliPerColonna.put(Constants.TiAlertPropScarto.TOTALE.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.TOTALE.name())
                                + valTotale);
                totaliPerColonna.put(Constants.TiAlertPropScarto.RAGGIUNTO.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.RAGGIUNTO.name())
                                + valRaggiunto);
                totaliPerColonna.put(Constants.TiAlertPropScarto.NON_RAGGIUNTO.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.NON_RAGGIUNTO.name())
                                + valNonRagg);
                totaliPerColonna.put(Constants.TiAlertPropScarto.SENZA_INDICAZIONE.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.SENZA_INDICAZIONE.name())
                                + valSenzaInd);
                totaliPerColonna.put(Constants.TiAlertPropScarto.ILLIMITATA.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.ILLIMITATA.name())
                                + valIllimitata);
                totaliPerColonna.put(Constants.TiAlertPropScarto.CONFLITTI.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.CONFLITTI.name())
                                + valConflitti); // <--- NUOVA
                totaliPerColonna.put(Constants.TiAlertPropScarto.IN_ALTRE_PROPOSTE.name(),
                        totaliPerColonna.get(Constants.TiAlertPropScarto.IN_ALTRE_PROPOSTE.name())
                                + valInAltre);

                // --- POPOLAMENTO ROW BEAN PER LA JSP ---
                BaseRow rowBean = new BaseRow();

                rowBean.setString("tipologia_ud", valTipologia);
                rowBean.setBigDecimal("qta_raggiunto", bigDecimalFromLong(valRaggiunto));
                rowBean.setBigDecimal("qta_non_raggiunto", bigDecimalFromLong(valNonRagg));
                rowBean.setBigDecimal("qta_senza_indicazione", bigDecimalFromLong(valSenzaInd));
                rowBean.setBigDecimal("qta_illimitate", bigDecimalFromLong(valIllimitata));
                rowBean.setBigDecimal("qta_conflitti", bigDecimalFromLong(valConflitti)); // <---
                                                                                          // NUOVA
                                                                                          // COLONNA
                                                                                          // XML
                rowBean.setBigDecimal("qta_in_altre_prop", bigDecimalFromLong(valInAltre));
                rowBean.setBigDecimal("qta_totale", bigDecimalFromLong(valTotale));

                tableBean.add(rowBean);
            }
        }

        // 4. Calcolo etichetta Anni di Riferimento in base ai filtri
        String labelAnni;

        if (filtri.getAnno() != null) {
            labelAnni = String.valueOf(filtri.getAnno());
        } else if (filtri.getAnnoDa() != null && filtri.getAnnoA() != null) {
            labelAnni = (filtri.getAnnoDa().equals(filtri.getAnnoA()))
                    ? String.valueOf(filtri.getAnnoDa())
                    : filtri.getAnnoDa() + " - " + filtri.getAnnoA();
        } else if (filtri.getAnnoDa() != null) {
            labelAnni = "Dal " + filtri.getAnnoDa();
        } else if (filtri.getAnnoA() != null) {
            labelAnni = "Fino al " + filtri.getAnnoA();
        } else {
            labelAnni = "Tutti gli anni";
        }

        // 5. Setto i risultati nel DTO
        resultDto.setTableBean(tableBean);
        resultDto.setTotaleUd(counterTotaleUd);
        resultDto.setAnniRiferimento(labelAnni);
        resultDto.setTotaliPerColonna(totaliPerColonna);

        return resultDto;
    }

    /**
     * Mappa i risultati della query delle UD in ALTRE proposte nel TableBean.
     *
     * @param filtri            filtri di ricerca ud per proposta
     * @param tipologiaCliccata tipologia ud
     * @param idPropCorrente    proposta corrente
     * @return lista ud in altre proposte
     */
    public BaseTable estraiListaUdInAltreProposte(FiltriRicercaUdScartoDto filtri,
            String tipologiaCliccata, Long idPropCorrente) {
        BaseTable tableBean = new BaseTable();
        List<Object[]> rawResults = helper.getDettaglioUdInAltreProposteNative(filtri,
                tipologiaCliccata, idPropCorrente);

        if (rawResults != null && !rawResults.isEmpty()) {
            for (Object[] row : rawResults) {
                BaseRow rowBean = new BaseRow();
                Long idUnitaDoc = ((Number) row[0]).longValue();

                rowBean.setBigDecimal("id_unita_doc", bigDecimalFromLong(idUnitaDoc));
                rowBean.setString("nm_tipo_unita_doc", (String) row[1]);
                rowBean.setString("cd_registro_key_unita_doc", (String) row[2]);
                if (row[3] != null) {
                    rowBean.setBigDecimal("aa_key_unita_doc",
                            bigDecimalFromLong(((Number) row[3]).longValue()));
                }
                rowBean.setString("cd_key_unita_doc", (String) row[4]);

                // Il codice della proposta
                rowBean.setString("cd_prop_scarto_vers", (String) row[5]);

                rowBean.setString("ds_alert_scarto", (String) row[6]);
                rowBean.setString("fl_scartabile", (String) row[7]);

                tableBean.add(rowBean);
            }
        }
        return tableBean;
    }

    /**
     * Recupera l'elenco di dettaglio delle UD relative a una specifica "cella" del report.
     *
     * @param filtri            filtri ricerca ud per proposta
     * @param tipologiaCliccata tipologia ud
     * @param colonnaCliccata   tipo colonna
     * @param idPropCorrente    id proposta corrente
     * @return lista ud per proposta
     */
    public BaseTable estraiListaUdPerScarto(FiltriRicercaUdScartoDto filtri,
            String tipologiaCliccata, ScartoHelper.ColonnaReportUd colonnaCliccata,
            BigDecimal idPropCorrente) {

        BaseTable tableBean = new BaseTable();
        List<Object[]> rawResults = helper.getListaUdScartoNative(filtri, tipologiaCliccata,
                colonnaCliccata, idPropCorrente);

        if (rawResults != null && !rawResults.isEmpty()) {
            for (Object[] row : rawResults) {
                BaseRow rowBean = new BaseRow();

                Long idUnitaDoc = ((Number) row[0]).longValue();

                rowBean.setBigDecimal("id_unita_doc", bigDecimalFromLong(idUnitaDoc));

                rowBean.setString("nm_tipo_unita_doc", (String) row[1]);
                rowBean.setString("cd_registro_key_unita_doc", (String) row[2]);

                if (row[3] != null) {
                    rowBean.setBigDecimal("aa_key_unita_doc",
                            bigDecimalFromLong(((Number) row[3]).longValue()));
                }

                rowBean.setString("cd_key_unita_doc", (String) row[4]);
                rowBean.setString("ds_alert_scarto", (String) row[5]);
                // Mappo il nuovo campo "SI/NO"
                rowBean.setString("fl_scartabile", (String) row[6]);

                tableBean.add(rowBean);
            }
        }
        return tableBean;
    }

    public BaseTable getUdItemPropScartoVersTableBean(BigDecimal idPropScartoVers) {
        BaseTable tableBean = new BaseTable();

        // Chiamo la nuova query nativa che fa le JOIN e calcola l'Alert
        List<Object[]> rawResults = helper.getUdSalvateConAlertNative(idPropScartoVers.longValue());

        if (rawResults != null && !rawResults.isEmpty()) {
            for (Object[] row : rawResults) {
                BaseRow rowBean = new BaseRow();

                // Mappatura Array:
                // [0] = ID_ITEM_PROP_SCARTO_VERS
                // [1] = ID_UNITA_DOC
                // [2] = TIPOLOGIA UD
                // [3] = REGISTRO
                // [4] = ANNO
                // [5] = NUMERO
                // [6] = ALERT (Ricalcolato in tempo reale!)
                Long idItem = ((Number) row[0]).longValue();
                Long idUnitaDoc = ((Number) row[1]).longValue();

                rowBean.setBigDecimal("id_item_prop_scarto_vers", bigDecimalFromLong(idItem));
                rowBean.setBigDecimal("id_unita_doc", bigDecimalFromLong(idUnitaDoc));
                rowBean.setString("nm_tipo_unita_doc", (String) row[2]);
                rowBean.setString("cd_registro_key_unita_doc", (String) row[3]);
                rowBean.setBigDecimal("aa_key_unita_doc",
                        bigDecimalFromLong(((Number) row[4]).longValue()));
                rowBean.setString("cd_key_unita_doc", (String) row[5]);

                // Popolo l'alert fresco di DB
                rowBean.setString("ds_alert_scarto", (String) row[6]);
                // Mappo il nuovo campo "SI/NO"
                rowBean.setString("fl_scartabile", (String) row[7]);

                tableBean.add(rowBean);
            }
        }
        return tableBean;
    }

    public boolean isPropostaDeletable(BigDecimal idPropScartoVers) {
        return helper.isPropostaDeletable(idPropScartoVers);
    }

    /**
     * Esegue l'eliminazione della proposta di annullamento versamenti
     *
     * @param idPropScartoVers id della proposta
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deletePropScartoVers(BigDecimal idPropScartoVers) throws ParerUserError {
        AroPropScartoVers propScartoVers = helper.findByIdWithLock(AroPropScartoVers.class,
                idPropScartoVers);
        // Verifica lo stato corrente della proposta
        AroStatoPropScartoVers statoCorrente = helper.findById(AroStatoPropScartoVers.class,
                propScartoVers.getIdStatoPropScartoVersCor());
        if (!statoCorrente.getTiStatoPropScartoVers()
                .equals(CostantiDB.TiStatoPropScartoVers.APERTA.name())) {
            throw new ParerUserError(
                    "La proposta non \u00E8 cancellabile perch\u00E9 ha stato corrente diverso da APERTA");
        }
        helper.removeEntity(propScartoVers, false);
    }

    /**
     * Annulla fisicamente una Proposta di Scarto che si trova in stato DA_AUTORIZZARE, rimuovendo
     * tutti gli item e cancellando la proposta dal DB. Da utilizzare quando l'autorità ha
     * comunicato che TUTTE le UD presenti in proposta non sono scartabili.
     *
     * @param idPropScartoVers ID della proposta da annullare
     * @param idUserIam        ID dell'utente che esegue l'operazione
     *
     * @throws ParerUserError se la proposta non è in stato DA_AUTORIZZARE o in caso di errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void annullaPropostaDaAutorizzare(BigDecimal idPropScartoVers, long idUserIam)
            throws ParerUserError {
        try {
            AroPropScartoVers proposta = helper.findByIdWithLock(AroPropScartoVers.class,
                    idPropScartoVers);
            if (proposta == null) {
                throw new ParerUserError("Proposta di scarto non trovata.");
            }

            // Verifica stato corrente: deve essere DA_AUTORIZZARE
            AroStatoPropScartoVers statoCorrente = helper.findById(AroStatoPropScartoVers.class,
                    proposta.getIdStatoPropScartoVersCor());
            if (!CostantiDB.TiStatoPropScartoVers.DA_AUTORIZZARE.name()
                    .equals(statoCorrente.getTiStatoPropScartoVers())) {
                throw new ParerUserError(
                        "La proposta non \u00e8 in stato DA_AUTORIZZARE: impossibile annullarla con questa funzione.");
            }

            // Prima azzeriamo il puntatore allo stato corrente per evitare vincoli FK
            proposta.setIdStatoPropScartoVersCor(null);
            helper.mergeEntity(proposta);
            // Flush necessario per rendere visibile la modifica prima delle DELETE
            entityManager.flush();

            // Rimuove tutti gli item dalla proposta (BULK DELETE)
            helper.deleteAllItemsDaProposta(idPropScartoVers.longValue());

            // Rimuove tutti gli stati della proposta (BULK DELETE)
            helper.deleteAllStatiDaProposta(idPropScartoVers.longValue());

            // Cancella fisicamente la proposta
            // Rileggo l'entità per averla managed dopo il flush
            AroPropScartoVers propostaManaged = helper.findById(AroPropScartoVers.class,
                    idPropScartoVers);
            if (propostaManaged != null) {
                helper.removeEntity(propostaManaged, false);
            }

            logger.info(
                    "Proposta Scarto ID: {} annullata (DA_AUTORIZZARE, nessuna UD scartabile) dall'utente ID: {}",
                    idPropScartoVers, idUserIam);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore durante l'annullamento della proposta: " + ex.getMessage(), ex);
            throw new ParerUserError("Errore imprevisto durante l'annullamento della proposta.");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long salvaItemUdProposta(BigDecimal idPropScartoVers, BaseTable listaItemAggiornata) {
        AroPropScartoVers proposta = helper.findById(AroPropScartoVers.class, idPropScartoVers);

        // Recupero gli item di tipo UNI_DOC attualmente salvati sul DB per questa proposta
        List<AroItemPropScartoVers> listaItemDB = helper.getAroItemPropScartoVers(
                idPropScartoVers.longValue(), CostantiDB.TiItemPropScartoVers.UNI_DOC.name());

        // LOGICA DELETE: Quali sono nel DB ma non più nella lista aggiornata?
        for (AroItemPropScartoVers itemDb : listaItemDB) {
            boolean trovato = false;
            for (BaseRow itemAggiornato : listaItemAggiornata) {
                if (itemAggiornato.getBigDecimal("id_item_prop_scarto_vers") != null
                        && itemAggiornato.getBigDecimal("id_item_prop_scarto_vers")
                                .longValue() == (itemDb.getIdItemPropScartoVers())) {
                    trovato = true;
                    break;
                }
            }
            if (!trovato) {
                helper.removeEntity(itemDb, false);
            }
        }

        // 3. LOGICA INSERT: Quali sono nuovi (ID null)?
        int progressivo = helper.getMaxPgItem(idPropScartoVers.longValue()) + 1;
        for (BaseRow itemRB : listaItemAggiornata) {
            if (itemRB.getBigDecimal("id_item_prop_scarto_vers") == null) {

                AroItemPropScartoVers item = new AroItemPropScartoVers();
                item.setAroPropScartoVers(proposta);
                item.setAroUnitaDoc(
                        helper.findById(AroUnitaDoc.class, itemRB.getBigDecimal("id_unita_doc")));
                item.setFasFascicolo(null);
                item.setTiItemPropScartoVers("UNI_DOC");
                item.setPgItem(BigDecimal.valueOf(progressivo++));
                item.setTiStatoItem("INSERITO_IN_PROPOSTA");
                helper.insertEntity(item, false);

            }
        }

        // Aggiorno la data di ultima modifica della proposta
        proposta.setDtUltimaMod(Calendar.getInstance().getTime());

        // Ritorno il nuovo totale da mostrare nella testata
        return helper.countItemsByTipo(idPropScartoVers.longValue(),
                CostantiDB.TiItemPropScartoVers.UNI_DOC.name());

    }

    public Set<Long> getUdGiaInAltreProposte(List<Long> idUdDaControllare) {
        return helper.getUdGiaInAltreProposte(idUdDaControllare);
    }

    public Set<Long> getUdGiaInAltreProposte(Long idPropScartoVersCorrente,
            List<Long> idUdDaControllare) {
        return helper.getUdGiaInAltreProposte(idPropScartoVersCorrente, idUdDaControllare);
    }

    /**
     * Aggiorna i contatori (UD, Fascicoli, Serie) sulla testata della proposta. Va chiamato dopo
     * ogni operazione di inserimento (INSERT in AroItemPropScartoVers).
     *
     * @param idPropScartoVers     proposta di scarto
     * @param tiItemPropScartoVers tipo item proposta
     * @return numero item in proposta
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long getNumeroItemProposta(BigDecimal idPropScartoVers, String tiItemPropScartoVers) {
        // Leggo i totali aggiornati dal DB
        long numItemInProposta = helper.countItemsByTipo(idPropScartoVers.longValue(),
                tiItemPropScartoVers);
        // Long fasc = helper.countItemsByTipo(idPropScartoVers, "FASC");
        // Long serie = helper.countItemsByTipo(idPropScartoVers, "SERIE");
        return numItemInProposta;
    }

    // <editor-fold defaultstate="collapsed" desc="Creazione richiesta annullamento
    // versamenti">
    /**
     * Verifica l'esistenza degli header REGISTRO, ANNO, NUMERO all'interno del file inviato
     *
     * @param fileByteArray file byte array
     *
     * @return true se sono presenti tutti gli header richiesti
     *
     * @throws IOException eccezione di tipo IO
     */
    public boolean checkCsvHeaders(byte[] fileByteArray) throws IOException {
        boolean result = true;
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray),
                StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        set.add(header.toUpperCase());
                    }
                }
                if (set.size() < 3) {
                    result = false;
                }
            }
        } finally {
            csvReader.close();
        }
        return result;
    }

    /**
     * Verifica l'esistenza di una precedente richiesta di scarto per codice richiesta
     *
     * @param cdRichScartoVers codice richiesta scarto
     * @param idStrut          id struttura
     *
     * @return true se esiste gi\u00E0 una richiesta di scarto con codice
     *         <code>cdRichScartoVers</code>
     */
    public boolean checkCdRichScartoVersExisting(String cdRichScartoVers, BigDecimal idStrut) {
        return helper.isRichScartoVersExisting(cdRichScartoVers, idStrut);
    }

    /**
     * Esegue il salvataggio in transazione del nuovo record di richiesta scarto versamento
     *
     * @param idUserIam        utente che crea la richiesta di scarto
     * @param cdRichScartoVers codice richiesta
     * @param dsRichScartoVers descrizione richiesta
     * @param ntRichScartoVers nota richiesta
     * @param fileByteArray    upload caricato dall'utente
     * @param idStrut          la struttura per cui viene creata la serie
     *
     * @return id richiesta
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveRichScartoVers(long idUserIam, String cdRichScartoVers, String dsRichScartoVers,
            String ntRichScartoVers, byte[] fileByteArray, BigDecimal idStrut)
            throws ParerUserError {
        logger.info("Eseguo il salvataggio della richiesta di scarto");
        Date now = Calendar.getInstance().getTime();
        Long idRich = null;
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
            IamUser user = helper.findById(IamUser.class, idUserIam);
            if (user.getAroStatoRichScartoVers() == null) {
                user.setAroStatoRichScartoVers(new ArrayList<>());
            }

            // Preparo la richiesta da registrare
            AroRichScartoVers rich = new AroRichScartoVers();
            rich.setCdRichScartoVers(cdRichScartoVers);
            rich.setDsRichScartoVers(dsRichScartoVers);
            rich.setNtRichScartoVers(ntRichScartoVers);
            rich.setDtCreazioneRichScartoVers(now);
            rich.setTiCreazioneRichScartoVers(
                    CostantiDB.TipoCreazioneRichScartoVers.UPLOAD_FILE.name());
            rich.setOrgStrut(strut);
            if (rich.getAroFileRichScartoVers() == null) {
                rich.setAroFileRichScartoVers(new ArrayList<>());
            }
            if (rich.getAroItemRichScartoVers() == null) {
                rich.setAroItemRichScartoVers(new ArrayList<>());
            }
            if (rich.getAroStatoRichScartoVers() == null) {
                rich.setAroStatoRichScartoVers(new ArrayList<>());
            }

            helper.insertEntity(rich, true);

            // Preparo lo stato da registrare
            String stato = (fileByteArray != null) ? CostantiDB.StatoRichScartoVers.CHIUSA.name()
                    : CostantiDB.StatoRichScartoVers.APERTA.name();
            AroStatoRichScartoVers statoRichScartoVers = context.getBusinessObject(ScartoEjb.class)
                    .createAroStatoRichScartoVers(rich, stato, now, null, user);

            // Se è specificato il file, devo preparare il file da registrare in
            // ARO_FILE_RICH_SCARTO_VERS
            if (fileByteArray != null) {
                AroFileRichScartoVers fileRich = new AroFileRichScartoVers();
                fileRich.setTiFile(CostantiDB.TipoFileRichScartoVers.FILE_UD_SCARTO.name());
                fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                rich.addAroFileRichScartoVers(fileRich);

                // Preparo gli item e gli eventuali errori in ARO_ITEM_RICH_SCARTO_VERS e
                // ARO_ERR_RICH_SCARTO_VERS
                context.getBusinessObject(ScartoEjb.class).handleCsvRecordsScarto(rich,
                        fileByteArray, idUserIam);
            }

            helper.insertEntity(statoRichScartoVers, true);

            // Aggiorno l’identificatore dello stato corrente della richiesta assegnando
            // l’identificatore dello stato
            // inserito
            rich.setIdStatoRichScartoVersCor(
                    new BigDecimal(statoRichScartoVers.getIdStatoRichScartoVers()));

            logger.info("Salvataggio della richiesta scarto completato");
            idRich = rich.getIdRichScartoVers();
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(
                    "Errore imprevisto durante il salvataggio della richiesta di scarto del versamento : "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di scarto del versamento");
        }
        return idRich;
    }

    /**
     * Legge il csv e crea gli oggetti AroItemRichScartoVers da salvare alla richiesta di scarto e
     * gli eventuali errori in AroErrRichScartoVers
     *
     * @param rich          la richiesta di scarto
     * @param fileByteArray il file csv in byte[]
     * @param idUserIam     id utente che ha creato la richiesta (ovvero colui che ha definito il
     *                      primo stato)
     *
     * @throws IOException    eccezione di tipo IO
     * @throws ParerUserError Errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleCsvRecordsScarto(AroRichScartoVers rich, byte[] fileByteArray, long idUserIam)
            throws IOException, ParerUserError {
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray),
                StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                logger.debug("Eseguo il parsing del file csv");
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                List<String> headersCorretti = new ArrayList<>();
                // Ottengo gli header corretti ignorando il case,
                // per cercare di produrre in lettura delle righe meno cicli possibili
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        headersCorretti.add(header);
                    }
                }
                int progressivoItem = 1;
                Set<UnitaDocBean> udsInRich = new HashSet<>();

                /*
                 * PER OGNI RECORD PRESENTE NEL FILE CSV
                 */
                while (csvReader.readRecord()) {
                    String registro = null;
                    String annoString = null;
                    String numero = null;
                    for (String header : headersCorretti) {
                        String valoreCampo = csvReader.get(header);
                        if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())) {
                            registro = valoreCampo;
                        } else if (header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())) {
                            annoString = valoreCampo;
                        } else if (header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                            numero = valoreCampo;
                        }
                    }

                    // Creo un record item per ogni riga del csv
                    BigDecimal anno = new BigDecimal(annoString);
                    BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

                    if (udsInRich.add(new UnitaDocBean(idStrut, registro, anno, numero))) {
                        // Ricavo l'ud
                        Long idUnitaDoc = udHelper.getIdUnitaDocVersata(idStrut, registro, anno,
                                numero);
                        // Verifico che l'ud non sia già stata scartata (ATTUALMENTE CONTROLLO IL
                        // RECORD IN eventuali altre AroItemRichScartoVers
                        // in quanto non ho un'informazione diretta su AroUnitaDoc) o già annullata
                        boolean udGiaScartata = false;
                        boolean udGiaAnnullata = false;

                        if (idUnitaDoc != null) {
                            udGiaScartata = helper.isUdScartata(idUnitaDoc);
                            udGiaAnnullata = helper.isUdAnnullata(idUnitaDoc);
                        }

                        // Se l'item non era già presente nella richiesta, preparo il record da
                        // registrare in ARO_ITEM_RICH_SCARTO_VERS
                        // Questo metodo imposta di default lo stato a NON_SCARTABILE
                        AroItemRichScartoVers item = createAroItemRichScartoVers(rich, registro,
                                anno, numero, progressivoItem);

                        logger.debug("Controlli item da scartare");
                        if (idUnitaDoc == null) {
                            // UD non esistente - creo diverso record di errore
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " non esiste";
                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else if (udGiaAnnullata) {
                            // CASO 2: UD esistente ma già annullata
                            // Deve avere errore ITEM_NON_ESISTE e restare NON_SCARTABILE
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " è già stata annullata";

                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else if (udGiaScartata) {
                            // CASO 3: UD esistente ma già scartata
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " \u00E8 gi\u00E0 stata scartata";
                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichScartoVers.ITEM_GIA_SCARTATO.name(),
                                    dsErr, CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else {
                            // CASO 4: UD esistente e valida per controlli successivi
                            AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
                            // Aggiungo l'UD all'item
                            item.setAroUnitaDoc(ud);

                            /*
                             * CONTROLLI ITEM DA SCARTARE Se questi controlli passano, lo stato
                             * verrà aggiornato a DA_SCARTARE altrimenti resterà NON_SCARTABILE
                             * (default)
                             */
                            controlloItemDaScartare(item, idUserIam);
                        }

                        progressivoItem++;
                    }
                } // Fine per ogni record del csv
                if (progressivoItem == 1) {
                    // Il file non conteneva nemmeno una riga di ud, a parte l'intestazione
                    throw new ParerUserError(
                            "Il file non contiene alcuna unit\u00E0 documentaria da scartare");
                }
            } else {
                throw new IllegalStateException(
                        "Errore imprevisto nella lettura del file caricato per la creazione della richiesta di scarto del versamento. Presumibilmente file non in formato csv.");
            }
        } finally {
            csvReader.close();
        }
    }

    /**
     * Controlli sull'item candidato ad essere scartato tenendo conto delle abilitazioni dell'utente
     * passato in ingresso
     *
     * @param item
     */
    private void controlloItemDaScartare(AroItemRichScartoVers item, long idUserIam) {
        int progressivoErr = 1;
        AroUnitaDoc ud = item.getAroUnitaDoc();

        // TODO DA VERIFICARE PER LO SCARTO
        // Controllo se esiste già un'altra richiesta, diversa da quella corrente, con
        // stato APERTA o CHIUSA che
        // contiene quella unita doc da scartare
        if (ud != null) {
            // Controllo se l'UD è già stata scartata in una precedente richiesta evasa
            if (helper.isUdScartata(ud.getIdUnitaDoc())) {
                String dsErr = "L'unità documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " è già stata scartata";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.ITEM_GIA_SCARTATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controllo se l'UD è già stata annullata
            if (helper.isUdAnnullata(ud.getIdUnitaDoc())) {
                String dsErr = "L'unità documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " è già stata annullata";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            AroRichScartoVers existingRich = helper.getAroRichScartoVersContainingUd(
                    ud.getIdUnitaDoc(), item.getAroRichScartoVers().getIdRichScartoVers());
            if (existingRich != null) {
                // UD gi\u00E0 presente in un'altra richiesta diversa da quella corrente
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " \u00E8 in corso di scarto nella richiesta "
                        + existingRich.getCdRichScartoVers();
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.ITEM_IN_CORSO_DI_SCARTO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al
            // TIPO_UNITA_DOC, in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam,
                    ud.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
                    Constants.TipoDato.TIPO_UNITA_DOC.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha tipo unit\u00E0 documentaria a cui l'utente non \u00E8 abilitato";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.TIPO_UNITA_DOC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al REGISTRO,
            // in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam,
                    ud.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    Constants.TipoDato.REGISTRO.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha registro a cui l'utente non \u00E8 abilitato";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.REGISTRO_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al TIPO_DOC
            // (tipo documento principale dell'ud), in caso negativo registro l'errore
            DecTipoDoc tipoDocPrinc = helper.getDecTipoDocPrincipale(ud.getIdUnitaDoc());
            if (!helper.isUserAbilitatoToTipoDato(idUserIam, tipoDocPrinc.getIdTipoDoc(),
                    Constants.TipoDato.TIPO_DOC.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha tipo documento principale a cui l'utente non \u00E8 abilitato";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.TIPO_DOC_PRINC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }
        }

        // Se per l'item corrente non sono presenti errori con gravità ERRORE
        if (helper.getAroErrRichScartoVersByGravity(item.getIdItemRichScartoVers(),
                CostantiDB.TipoGravitaErrore.ERRORE.name()).isEmpty()) {
            item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.DA_SCARTARE.name());
        }

    }

    private void createAroErrRichScartoVers(AroItemRichScartoVers item, BigDecimal pgErr,
            String tiErr, String dsErr, String tiGravitaErr) {
        AroErrRichScartoVers err = new AroErrRichScartoVers();
        err.setPgErr(pgErr);
        err.setTiErr(tiErr);
        err.setDsErr(dsErr);
        err.setTiGravita(tiGravitaErr);
        item.addAroErrRichScartoVers(err);
        helper.insertEntity(err, true);
    }

    private AroItemRichScartoVers createAroItemRichScartoVers(AroRichScartoVers rich,
            String registro, BigDecimal anno, String numero, int progressivo) {
        // Dovrei aver ottenuto tutti i campi necessari per creare il nuovo record
        AroItemRichScartoVers item = new AroItemRichScartoVers();
        item.setCdRegistroKeyUnitaDoc(registro);
        item.setAaKeyUnitaDoc(anno);
        item.setCdKeyUnitaDoc(numero);
        item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
        item.setPgItemRichScartoVers(new BigDecimal(progressivo));
        item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.NON_SCARTABILE.name());
        if (item.getAroErrRichScartoVers() == null) {
            item.setAroErrRichScartoVers(new ArrayList<>());
        }
        rich.addAroItemRichScartoVers(item);
        helper.insertEntity(item, true);
        return item;
    }

    /**
     * Crea automaticamente una richiesta di scarto (ARO_RICH_SCARTO_VERS) a partire da una proposta
     * che ha appena raggiunto lo stato AUTORIZZATA. Vengono trasferiti come item della richiesta
     * tutte le UD di tipo UNI_DOC presenti nella proposta. La richiesta viene creata in stato
     * CHIUSA (pronta per l'elaborazione da parte del job).
     *
     * @param proposta  la proposta autorizzata
     * @param utente    utente che ha completato l'autorizzazione
     * @param now       timestamp dell'operazione
     * @param idUserIam id utente (per il controllo abilitazioni)
     */
    private Long creaRichScartoVersFromProposta(AroPropScartoVers proposta, IamUser utente,
            Date now, long idUserIam) throws ParerUserError {
        Calendar cal = Calendar.getInstance();
        cal.setTime(proposta.getDtCreazione());
        int anno = cal.get(Calendar.YEAR);
        String cdRichScartoVers = proposta.getPgPropScartoVers().toPlainString() + "/" + anno;

        // Verifica unicità del codice: in caso di doppia sottomissione o errore applicativo
        // potrebbe essere richiesta la creazione di una richiesta con codice già esistente.
        if (helper.isRichScartoVersExisting(cdRichScartoVers,
                BigDecimal.valueOf(proposta.getOrgStrut().getIdStrut()))) {
            throw new ParerUserError("Impossibile creare la richiesta di scarto: esiste già una "
                    + "richiesta con codice '" + cdRichScartoVers + "' per questa struttura.");
        }

        AroRichScartoVers rich = new AroRichScartoVers();
        rich.setCdRichScartoVers(cdRichScartoVers);
        rich.setDsRichScartoVers(proposta.getDsPropScartoVers());
        rich.setNtRichScartoVers(proposta.getNtPropScartoVers());
        rich.setDtCreazioneRichScartoVers(now);
        rich.setTiCreazioneRichScartoVers(
                CostantiDB.TipoCreazioneRichScartoVers.DA_PROPOSTA.name());
        rich.setOrgStrut(proposta.getOrgStrut());
        rich.setAroItemRichScartoVers(new ArrayList<>());
        rich.setAroFileRichScartoVers(new ArrayList<>());
        rich.setAroStatoRichScartoVers(new ArrayList<>());

        helper.insertEntity(rich, true);

        // Crea un item della richiesta per ciascuna UD di tipo UNI_DOC in proposta
        int progressivo = 1;
        for (AroItemPropScartoVers itemProp : proposta.getAroItemPropScartoVers()) {
            if (!CostantiDB.TiItemPropScartoVers.UNI_DOC.name()
                    .equals(itemProp.getTiItemPropScartoVers())) {
                continue;
            }
            AroUnitaDoc ud = itemProp.getAroUnitaDoc();
            if (ud == null) {
                continue;
            }
            AroItemRichScartoVers item = new AroItemRichScartoVers();
            item.setCdRegistroKeyUnitaDoc(ud.getCdRegistroKeyUnitaDoc());
            item.setAaKeyUnitaDoc(ud.getAaKeyUnitaDoc());
            item.setCdKeyUnitaDoc(ud.getCdKeyUnitaDoc());
            item.setIdStrut(BigDecimal.valueOf(proposta.getOrgStrut().getIdStrut()));
            item.setPgItemRichScartoVers(new BigDecimal(progressivo++));
            item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.NON_SCARTABILE.name());
            item.setAroUnitaDoc(ud);
            rich.addAroItemRichScartoVers(item);
            helper.insertEntity(item, true);
        }

        // Stato iniziale CHIUSA: la richiesta è pronta per l'elaborazione del job
        AroStatoRichScartoVers statoRich = context.getBusinessObject(ScartoEjb.class)
                .createAroStatoRichScartoVers(rich,
                        CostantiDB.StatoRichScartoVers.CHIUSA.name(), now,
                        "Creata automaticamente da proposta di scarto " + cdRichScartoVers,
                        utente);
        helper.insertEntity(statoRich, true);
        rich.setIdStatoRichScartoVersCor(
                new BigDecimal(statoRich.getIdStatoRichScartoVers()));

        logger.info(
                "Richiesta di scarto {} creata automaticamente dalla proposta ID: {}",
                cdRichScartoVers, proposta.getIdPropScartoVers());
        return rich.getIdRichScartoVers();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Scarto versamenti">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraRichiestaScarto(long idRichScartoVers, long idUserIam, String modalita) {
        // Assumo lock esclusivo sulla richiesta
        AroRichScartoVers richiestaScarto = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        AroStatoRichScartoVers statoRichScartoVers = helper.findById(AroStatoRichScartoVers.class,
                richiestaScarto.getIdStatoRichScartoVersCor());
        boolean proseguiScarto = false;

        // Lock su item ud
        for (AroItemRichScartoVers itemRichScartoVers : richiestaScarto
                .getAroItemRichScartoVers()) {
            // Controllo che l'item esista per loccarlo
            if (itemRichScartoVers.getAroUnitaDoc() != null) {
                helper.findByIdWithLock(AroUnitaDoc.class,
                        itemRichScartoVers.getAroUnitaDoc().getIdUnitaDoc());
            }
        }

        // Se la richiesta ha stato corrente CHIUSA
        if (statoRichScartoVers.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.CHIUSA.name())) {
            proseguiScarto = true;
            logger.debug("{} --- Scarto Versamenti - Verifica della richiesta con stato CHIUSA",
                    ScartoEjb.class.getSimpleName());

            // Elimino tutti gli errori rilevati sugli item della richiesta, tranne quelli
            // di tipo ITEM_NON_ESISTE e ITEM_GIA_PRESENTE e ITEM_GIA_SCARTATO e ITEM_GIA_ANNULLATO
            helper.deleteAroErrRichScartoVers(idRichScartoVers,
                    CostantiDB.TipoErrRichScartoVers.getStatiControlloItem());

            // Controllo gli item
            List<AroItemRichScartoVers> itemRichScartoVersList = richiestaScarto
                    .getAroItemRichScartoVers();
            for (AroItemRichScartoVers item : itemRichScartoVersList) {
                // Assumo lock esclusivo sull'unità doc definita nell'item della richiesta
                item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.NON_SCARTABILE.name());
                /*
                 * CONTROLLA ITEM DA SCARTARE (senza cancellazione errori e assegnazione stato
                 * NON_SCARTABILE)
                 */
                controlloItemDaScartare(item, idUserIam);

            }
        }

        ////////////////////////
        // EVADO LA RICHIESTA //
        ////////////////////////
        // Se la richiesta era ancora chiusa (CONCORRENZA tra online e job)
        if (proseguiScarto) {
            context.getBusinessObject(ScartoEjb.class).evasioneRichiestaScarto(richiestaScarto,
                    idUserIam, modalita);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evasioneRichiestaScarto(AroRichScartoVers richiestaScarto, long idUserIam,
            String modalita) {
        richiestaScarto = entityManager.find(AroRichScartoVers.class,
                richiestaScarto.getIdRichScartoVers(), LockModeType.PESSIMISTIC_WRITE);
        evadiScartoVersamentiUnitaDoc(richiestaScarto, idUserIam, modalita);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evadiScartoVersamentiUnitaDoc(AroRichScartoVers richiestaScarto, long idUserIam,
            String modalita) {
        long idRichScartoVers = richiestaScarto.getIdRichScartoVers();
        logger.debug("{} Id richiesta scarto versamento {}", LOG_MESSAGE_SCARTO, idRichScartoVers);
        AroStatoRichScartoVers statoRichScartoVers = helper.findById(AroStatoRichScartoVers.class,
                richiestaScarto.getIdStatoRichScartoVersCor());
        logger.info("{} Evasione richiesta scarto ID: {}", LOG_MESSAGE_SCARTO, idRichScartoVers);
        logger.debug("{} Procedo ad evadere la richiesta di scarto avente id:{}",
                LOG_MESSAGE_SCARTO, idRichScartoVers);
        logger.debug("{} tiStatoRichScartoVers={} dtRegStatoRichScartoVers={}", LOG_MESSAGE_SCARTO,
                statoRichScartoVers.getTiStatoRichScartoVers(),
                statoRichScartoVers.getDtRegStatoRichScartoVers());
        // Definisco come data di scarto la data corrente
        Calendar cal = Calendar.getInstance();
        Date dataScarto = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
        String dtString = df.format(dataScarto);

        // TODO EVENTUALI OPERAZIONI DA EFFETTUARE SUGLI ELEMENTI COLLEGATI ALLE UD COME
        // NELL'ANNULLAMENTO
        logger.debug("{} Registro il nuovo stato della richiesta di scarto avente id: {}",
                LOG_MESSAGE_SCARTO, idRichScartoVers);
        // Registra il nuovo stato della richiesta di scarto
        TypedQuery<AroStatoRichScartoVers> query = entityManager.createQuery(
                "SELECT a FROM AroStatoRichScartoVers a WHERE a.aroRichScartoVers = :aroRichScartoVers",
                AroStatoRichScartoVers.class);
        query.setParameter("aroRichScartoVers", richiestaScarto);
        richiestaScarto.setAroStatoRichScartoVers(query.getResultList());
        AroStatoRichScartoVers statoRichScartoVersNew = context.getBusinessObject(ScartoEjb.class)
                .createAroStatoRichScartoVers(richiestaScarto,
                        CostantiDB.StatoRichScartoVers.EVASA.name(),
                        Calendar.getInstance().getTime(), null, statoRichScartoVers.getIamUser());
        helper.insertEntity(statoRichScartoVersNew, false);
        // Aggiorno l'identificatore dello stato corrente della richiesta assegnando
        // l'identificatore dello stato inserito
        richiestaScarto.setIdStatoRichScartoVersCor(
                new BigDecimal(statoRichScartoVersNew.getIdStatoRichScartoVers()));

        // Modifico gli item assegnando stato SCARTATO
        helper.updateStatoItemScartoList(idRichScartoVers,
                CostantiDB.StatoItemRichScartoVers.SCARTATO.name());

        // MEV #30725: registro nel centro stella del DataMart
        String tipoCancellazione = configurationHelper.getAplValoreParamApplic(
                CostantiDB.ParametroAppl.TI_CANCELLAZIONE_MS_UD_DEL,
                AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
        int totaliDataMart = dataMartEjb.insertUdDataMartScartoVersCentroStella(idRichScartoVers,
                richiestaScarto.getCdRichScartoVers(), CostantiDB.TiMotCancellazione.S.name(),
                tipoCancellazione);
        logger.info("Gestione DataMart Scarto Versamenti: inserite {} ud in DM_UD_DEL",
                totaliDataMart);
        // fine MEV #30725: registro nel DataMart

        logger.debug("{} - richiesta avente id: {} elaborata con successo!", LOG_MESSAGE_SCARTO,
                idRichScartoVers);
        logger.info("Fine evasione richiesta ID: {}", idRichScartoVers);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroStatoRichScartoVers createAroStatoRichScartoVers(AroRichScartoVers richScartoVers,
            String tiStatoRichScartoVers, Date dtRegStatoRichScartoVers,
            String dsNotaRichScartoVers, IamUser iamUser) {
        AroStatoRichScartoVers statoRichScartoVers = new AroStatoRichScartoVers();
        statoRichScartoVers.setPgStatoRichScartoVers(helper
                .getUltimoProgressivoStatoRichiestaScarto(richScartoVers.getIdRichScartoVers())
                .add(BigDecimal.ONE));
        statoRichScartoVers.setTiStatoRichScartoVers(tiStatoRichScartoVers);
        statoRichScartoVers.setDtRegStatoRichScartoVers(dtRegStatoRichScartoVers);
        statoRichScartoVers.setDsNotaRichScartoVers(dsNotaRichScartoVers);
        statoRichScartoVers.setIamUser(iamUser);
        richScartoVers.addAroStatoRichScartoVers(statoRichScartoVers);
        return statoRichScartoVers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroXmlRichAnnulVers createAroXmlRichAnnulVers(AroRichAnnulVers richAnnulVers,
            String tiXmlRichAnnulVers, String blXmlRichAnnulVers, String cdVersioneXml) {
        AroXmlRichAnnulVers xmlRichAnnulVers = new AroXmlRichAnnulVers();
        logger.info("Eseguo il salvataggio dell'xml " + tiXmlRichAnnulVers + " annullamento");
        xmlRichAnnulVers.setTiXmlRichAnnulVers(tiXmlRichAnnulVers);
        xmlRichAnnulVers.setBlXmlRichAnnulVers(blXmlRichAnnulVers);
        xmlRichAnnulVers.setCdVersioneXml(cdVersioneXml);
        richAnnulVers.addAroXmlRichAnnulVers(xmlRichAnnulVers);
        return xmlRichAnnulVers;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funzioni Online">
    /**
     * Ricerca online - Ritorna il tablebean di risultati dati i filtri richiesti
     *
     * @param idUser id utente che ha eseguito la ricerca
     * @param filtri parametri della richiesta
     *
     * @return tablebean
     */
    public AroVRicRichScartoTableBean getAroVRicRichScartoTableBean(long idUser,
            RicercaRichScartoVersBean filtri) {
        AroVRicRichScartoTableBean table = new AroVRicRichScartoTableBean();
        List<AroVRicRichScarto> list = helper.retrieveAroVRicRichScarto(idUser, filtri);
        if (list != null && !list.isEmpty()) {
            try {
                for (AroVRicRichScarto aroVRicRichScarto : list) {
                    AroVRicRichScartoRowBean row = (AroVRicRichScartoRowBean) Transform
                            .entity2RowBean(aroVRicRichScarto);
                    row.setString("amb_ente_strut",
                            aroVRicRichScarto.getNmAmbiente() + " - "
                                    + aroVRicRichScarto.getNmEnte() + " - "
                                    + aroVRicRichScarto.getNmStrut());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle richieste di scarto versamento "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Carica il dettaglio richiesta di scarto versamenti dato l'id richiesta
     *
     * @param idRichScartoVers id della richiesta
     *
     * @return rowBean della vista
     */
    public AroVVisRichScartoRowBean getAroVVisRichScartoRowBean(BigDecimal idRichScartoVers) {
        AroVVisRichScarto richiesta = helper.findViewById(AroVVisRichScarto.class,
                idRichScartoVers);
        AroVVisRichScartoRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisRichScartoRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della richiesta di scarto versamenti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della richiesta di scarto versamenti");
            }
        }
        return row;
    }

    public AroVLisItemRichScartoTableBean getAroVLisItemRichScartoTableBean(
            BigDecimal idRichScartoVers) {
        AroVLisItemRichScartoTableBean table = new AroVLisItemRichScartoTableBean();
        List<AroVLisItemRichScarto> list = helper.getAroVLisItemRichScarto(idRichScartoVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisItemRichScartoTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di versamenti della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di versamenti della richiesta di scartoversamenti");
            }
        }
        return table;
    }

    public AroVLisStatoRichScartoTableBean getAroVLisStatoRichScartoTableBean(
            BigDecimal idRichScartoVers) {
        AroVLisStatoRichScartoTableBean table = new AroVLisStatoRichScartoTableBean();
        List<AroVLisStatoRichScarto> list = helper.getAroVLisStatoRichScarto(idRichScartoVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisStatoRichScartoTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di stati della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di stati della richiesta di scarto versamenti");
            }
        }
        return table;
    }

    /**
     * Esegue la modifica della richiesta di scarto versamenti, aggiungendo le ud da un file
     * caricato
     *
     * @param idRichScartoVers id richiesta scarto
     * @param fileByteArray    array file in byte
     * @param idUserIam        id user Iam
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRichScartoVers(BigDecimal idRichScartoVers, byte[] fileByteArray,
            long idUserIam) throws ParerUserError {
        // try {
        // AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
        // idRichScartoVers);
        // richScartoVers.setTiCreazioneRichScartoVers(
        // CostantiDB.TipoCreazioneRichScartoVers.UPLOAD_FILE.name());
        // if (fileByteArray != null) {
        // AroFileRichScartoVers fileRich = new AroFileRichScartoVers();
        // fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_UD_SCARTO.name());
        // fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
        // richScartoVers.addAroFileRichAnnulVer(fileRich);
        //
        // context.getBusinessObject(ScartoEjb.class).handleCsvRecords(richScartoVers,
        // fileByteArray, idUserIam);
        // }
        // } catch (ParerUserError ex) {
        // throw ex;
        // } catch (Exception ex) {
        // logger.error(
        // "Errore imprevisto durante il salvataggio della richiesta di scarto del versamento : "
        // + ExceptionUtils.getRootCauseMessage(ex),
        // ex);
        // throw new ParerUserError(
        // "Eccezione imprevista durante il salvataggio della richiesta di scarto del versamento");
        // }
    }

    /**
     * Esegue l'eliminazione della richiesta di scarto versamenti
     *
     * @param idRichScartoVers id della richiesta
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteRichScartoVers(BigDecimal idRichScartoVers) throws ParerUserError {
        AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        // Verifica lo stato corrente della richiesta
        AroStatoRichScartoVers statoCorrente = helper.findById(AroStatoRichScartoVers.class,
                richScartoVers.getIdStatoRichScartoVersCor());
        if (!statoCorrente.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.APERTA.name())
                && !statoCorrente.getTiStatoRichScartoVers()
                        .equals(CostantiDB.StatoRichScartoVers.CHIUSA.name())) {
            throw new ParerUserError(
                    "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA o CHIUSA");
        }
        helper.removeEntity(richScartoVers, false);
    }

    /**
     * Esegue l'eliminazione di un item della richiesta di scarto versamenti
     *
     * @param idRichScartoVers     id della richiesta
     * @param idItemRichScartoVers id dell'item da eliminare
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteItemRichScartoVers(BigDecimal idRichScartoVers,
            BigDecimal idItemRichScartoVers) throws ParerUserError {
        AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        AroStatoRichScartoVers statoCorrente = helper.findById(AroStatoRichScartoVers.class,
                richScartoVers.getIdStatoRichScartoVersCor());
        // Verifica lo stato corrente della richiesta
        if (!statoCorrente.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.APERTA.name())) {
            throw new ParerUserError(
                    "Il versamento non \u00E8 eliminabile perch\u00E9 la richiesta ha stato corrente diverso da APERTA");
        }
        AroItemRichScartoVers item = helper.findById(AroItemRichScartoVers.class,
                idItemRichScartoVers);
        helper.removeEntity(item, false);
    }
    // </editor-fold>

    /**
     * Verifica che la richiesta abbia uno degli stati elencati
     *
     * @param idRichScartoVers id della richiesta
     * @param statiRichiesta   stati da verificare
     *
     * @return true se la richiesta ha stato uguale a uno di quelli in elenco
     */
    public boolean checkStatoRichiestaScarto(BigDecimal idRichScartoVers,
            String... statiRichiesta) {
        boolean result = false;

        AroRichScartoVers rich = helper.findById(AroRichScartoVers.class, idRichScartoVers);
        if (rich != null) {
            AroStatoRichScartoVers ultimoStato = helper.findById(AroStatoRichScartoVers.class,
                    rich.getIdStatoRichScartoVersCor());
            if (statiRichiesta != null) {
                for (String stato : statiRichiesta) {
                    if (ultimoStato.getTiStatoRichScartoVers().equals(stato)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public AroVVisStatoRichScartoRowBean geAroVVisStatoRichScartoRowBean(
            BigDecimal idStatoRichScarto) {
        AroVVisStatoRichScarto richiesta = helper.findViewById(AroVVisStatoRichScarto.class,
                idStatoRichScarto);
        AroVVisStatoRichScartoRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisStatoRichScartoRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero dello stato della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero dello stato della richiesta di scarto versamenti");
            }
        }
        return row;
    }

    public AroVVisStatoRichAnnvrsRowBean geAroVVisStatoRichAnnvrsRowBean(
            BigDecimal idStatoRichAnnvrs) {
        AroVVisStatoRichAnnvrs richiesta = helper.findViewById(AroVVisStatoRichAnnvrs.class,
                idStatoRichAnnvrs);
        AroVVisStatoRichAnnvrsRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisStatoRichAnnvrsRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero dello stato della richiesta di annullamento versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero dello stato della richiesta di annullamento versamenti");
            }
        }
        return row;
    }

    public AroVRicPropScartoVersRowBean getAroVRicPropScartoVersRowBean(BigDecimal idPropScartoVers,
            long idUserIam) {
        AroVRicPropScartoVers propScartoVers = helper.getAroVRicPropScartoVersById(idPropScartoVers,
                idUserIam);
        AroVRicPropScartoVersRowBean row = new AroVRicPropScartoVersRowBean();
        try {
            row = (AroVRicPropScartoVersRowBean) Transform.entity2RowBean(propScartoVers);
            row.setString("amb_ente_strut",
                    row.getNmAmbiente() + " - " + row.getNmEnte() + " - " + row.getNmStrut());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero della proposta di scarto"
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return row;
    }

    // EJB: Inserimento massivo
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void salvaItemUdInProposta(Long idProp, List<Long> idUds) {
        int pg = helper.getMaxPgItem(idProp) + 1;
        AroPropScartoVers prop = helper.findById(AroPropScartoVers.class, idProp);

        for (Long idUd : idUds) {
            AroItemPropScartoVers item = new AroItemPropScartoVers();
            item.setAroPropScartoVers(prop);
            item.setAroUnitaDoc(helper.findById(AroUnitaDoc.class, idUd));
            item.setTiItemPropScartoVers("UNI_DOC");
            item.setPgItem(BigDecimal.valueOf(pg++));
            item.setTiStatoItem("INSERITO_IN_PROPOSTA");
            helper.insertEntity(item, false);
        }
    }

    /**
     * Rimuove massivamente una lista di Unità Documentarie dalla proposta di scarto.
     *
     * @param idProp L'id della proposta
     * @param idUds  La lista di ID UD da rimuovere
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void rimuoviItemUdDaProposta(Long idProp, List<Long> idUds) {
        if (idUds == null || idUds.isEmpty()) {
            return;
        }

        // Delega all'Helper per l'esecuzione della query di DELETE
        helper.deleteItemUdDaProposta(idProp, idUds);

        logger.info("Rimosse {} UD dalla proposta ID: {}", idUds.size(), idProp);
    }

    /**
     * Estrae dal DB tutte le UD già salvate in una specifica proposta di scarto, calcolando al volo
     * lo stato di coerenza (Alert) e restituendole come BaseTable formattata per la griglia UI
     * (UdSelezionatePropScartoList).
     *
     * @param idPropScartoVers id proposta
     * @param idTipoUd         id tipo ud
     * @param idRegistro       id registro
     * @param anno             anno ud
     * @param annoDa           anno ud da
     * @param annoA            anno ud a
     * @param numero           numero ud
     * @param numeroDa         numero ud da
     * @param numeroA          numero ud a
     * @param flScartabile     flag scartabile
     * @param dsAlertTesto     testo alert
     * @return lista ud salvate in proposta
     */
    public BaseTable getListaUdSalvateInProposta(BigDecimal idPropScartoVers, BigDecimal idTipoUd,
            BigDecimal idRegistro, BigDecimal anno, BigDecimal annoDa, BigDecimal annoA,
            String numero, String numeroDa, String numeroA, String flScartabile,
            String dsAlertTesto) {
        BaseTable tableBean = new BaseTable();

        List<Object[]> rawResults = helper.getUdSalvateConAlertNative(idPropScartoVers.longValue(),
                idTipoUd, idRegistro, anno, annoDa, annoA, numero, numeroDa, numeroA, flScartabile,
                dsAlertTesto);

        if (rawResults != null && !rawResults.isEmpty()) {
            for (Object[] row : rawResults) {
                BaseRow rowBean = new BaseRow();

                // Mappatura Array restituito dalla query nativa:
                // [0] = ID_ITEM_PROP_SCARTO_VERS
                // [1] = ID_UNITA_DOC
                // [2] = TIPO_UD
                // [3] = REGISTRO
                // [4] = ANNO
                // [5] = NUMERO
                // [6] = ALERT (Ricalcolato in tempo reale)
                // [7] = FL_SCARTABILE (SI/NO per i pallini)
                Long idItem = ((Number) row[0]).longValue();
                Long idUnitaDoc = ((Number) row[1]).longValue();

                rowBean.setBigDecimal("id_item_prop_scarto_vers", bigDecimalFromLong(idItem));
                rowBean.setBigDecimal("id_unita_doc", bigDecimalFromLong(idUnitaDoc));
                rowBean.setString("nm_tipo_unita_doc", (String) row[2]);
                rowBean.setString("cd_registro_key_unita_doc", (String) row[3]);

                if (row[4] != null) {
                    rowBean.setBigDecimal("aa_key_unita_doc",
                            bigDecimalFromLong(((Number) row[4]).longValue()));
                }

                rowBean.setString("cd_key_unita_doc", (String) row[5]);
                rowBean.setString("ds_alert_scarto", (String) row[6]);
                rowBean.setString("fl_scartabile", (String) row[7]);

                tableBean.add(rowBean);
            }
        }

        return tableBean;
    }

    public long contaUdInProposta(Long idPropScartoVers) {
        // Chiama la COUNT su ARO_ITEM_PROP_SCARTO_VERS filtrata per 'UNI_DOC'
        return helper.countItemsByTipo(idPropScartoVers,
                CostantiDB.TiItemPropScartoVers.UNI_DOC.name());
    }

}
