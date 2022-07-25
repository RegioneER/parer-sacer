package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper.FormatoFileDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecUsoFormatoFileStandard;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoFormatoFileStandardRowBean;
import it.eng.parer.web.util.Transform;

/**
 * EJB di gestione dei formati file doc
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class FormatoFileDocEjb {

    private static final Logger logger = LoggerFactory.getLogger(FormatoFileDocEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private FormatoFileDocHelper helper;
    @EJB
    private FormatoFileStandardHelper formatoFileStandardHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;

    public DecFormatoFileDocRowBean getDecFormatoFileDocRowBean(BigDecimal idFormatoFileDoc, BigDecimal idStrut) {
        return getDecFormatoFileDoc(idFormatoFileDoc, null, idStrut);
    }

    public DecFormatoFileDocRowBean getDecFormatoFileDocRowBean(String nmFormatoFileDoc, BigDecimal idStrut) {
        return getDecFormatoFileDoc(BigDecimal.ZERO, nmFormatoFileDoc, idStrut);
    }

    private DecFormatoFileDocRowBean getDecFormatoFileDoc(BigDecimal idFormatoFileDoc, String nmFormatoFileDoc,
            BigDecimal idStrut) {
        DecFormatoFileDocRowBean formatoFileDocRowBean = null;
        DecFormatoFileDoc formatoFileDoc = null;

        if (idFormatoFileDoc == BigDecimal.ZERO && nmFormatoFileDoc != null) {
            formatoFileDoc = helper.getDecFormatoFileDocByName(nmFormatoFileDoc, idStrut);
        }
        if (nmFormatoFileDoc == null && idFormatoFileDoc != BigDecimal.ZERO) {
            formatoFileDoc = helper.findById(DecFormatoFileDoc.class, idFormatoFileDoc);
        }

        if (formatoFileDoc != null) {
            try {
                formatoFileDocRowBean = (DecFormatoFileDocRowBean) Transform.entity2RowBean(formatoFileDoc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return formatoFileDocRowBean;
    }

    public DecFormatoFileDocTableBean getDecFormatoFileDocTableBean(BigDecimal idStrut) {
        DecFormatoFileDocTableBean table = new DecFormatoFileDocTableBean();
        List<DecFormatoFileDoc> list = helper.retrieveDecFormatoFileDocList(idStrut);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecFormatoFileDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore inatteso nel recupero dei formati per la struttura "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalArgumentException("Errore inatteso nel recupero dei formati per la struttura");
            }
        }
        return table;
    }

    public DecFormatoFileDocTableBean getDecFormatoFileDocTableBean(BigDecimal idStrut, boolean isFilterValid) {
        DecFormatoFileDocTableBean formatoFileDocTableBean = new DecFormatoFileDocTableBean();
        List<DecFormatoFileDoc> list = helper.retrieveDecFormatoFileDocList(idStrut, isFilterValid);

        try {
            if (!list.isEmpty()) {
                for (DecFormatoFileDoc formato : list) {
                    DecFormatoFileDocRowBean row = (DecFormatoFileDocRowBean) Transform.entity2RowBean(formato);
                    if (formato.getDtIstituz().before(new Date()) && formato.getDtSoppres().after(new Date())) {
                        row.setObject("fl_attivo", "1");
                    } else {
                        row.setObject("fl_attivo", "0");
                    }

                    // NOTA: Aggiunto il mimetype e altro... si può ottimizzare ma va la gestione dei formati
                    // è stata smandruppata diverse volte, bisognerebbe rivedere la logica con gli analisti
                    List<DecUsoFormatoFileStandard> usi = formato.getDecUsoFormatoFileStandards();
                    if (!usi.isEmpty()) {
                        if (usi.size() > 1) {
                            for (DecUsoFormatoFileStandard uso : usi) {
                                row.setString("ti_esito_contr_formato",
                                        uso.getDecFormatoFileStandard().getTiEsitoContrFormato());
                                row.setString("nm_formato_file_standard",
                                        uso.getDecFormatoFileStandard().getNmFormatoFileStandard());
                                row.setBigDecimal(StruttureForm.FormatoFileDocList.ni_punteggio_totale,
                                        formatoFileStandardHelper.calcolaValutazione(uso.getDecFormatoFileStandard()));
                                if (uso.getDecFormatoFileStandard().getFlFormatoConcat().equals("0")) {
                                    row.setString("nm_mimetype_file",
                                            uso.getDecFormatoFileStandard().getNmMimetypeFile());
                                    break;
                                }
                            }
                        } else {
                            row.setString("ti_esito_contr_formato",
                                    usi.get(0).getDecFormatoFileStandard().getTiEsitoContrFormato());
                            row.setString("nm_formato_file_standard",
                                    usi.get(0).getDecFormatoFileStandard().getNmFormatoFileStandard());
                            row.setString("nm_mimetype_file",
                                    usi.get(0).getDecFormatoFileStandard().getNmMimetypeFile());
                            row.setBigDecimal(StruttureForm.FormatoFileDocList.ni_punteggio_totale,
                                    formatoFileStandardHelper
                                            .calcolaValutazione(usi.get(0).getDecFormatoFileStandard()));

                        }

                        SortedSet<String> estensioni = new TreeSet<>();
                        for (DecUsoFormatoFileStandard uso : usi) {
                            for (DecEstensioneFile est : uso.getDecFormatoFileStandard().getDecEstensioneFiles()) {
                                estensioni.add(est.getCdEstensioneFile());
                            }
                        }

                        String estensioniString = "";
                        for (String estensione : estensioni) {
                            if (estensioniString.equals("")) {
                                estensioniString = estensione;
                            } else {
                                estensioniString = estensioniString + "; " + estensione;
                            }
                        }
                        row.setString("cd_estensione_file", estensioniString);
                    }

                    formatoFileDocTableBean.add(row);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        formatoFileDocTableBean.addSortingRule("nm_mimetype_file");
        formatoFileDocTableBean.sort();
        return formatoFileDocTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addFormatoFileDoc(LogParam param, DecFormatoFileDocRowBean formato,
            DecFormatoFileStandardTableBean decFormatoFileStandardTableBean, Boolean duplica)
            throws ParerUserError, ParerWarningException {
        DecFormatoFileDoc formatoFileDocDB = null;
        // Controllo di poter inserire il record: sia che sia un nuovo inserimento (insert o duplica), sia che sia una
        // modifica
        if ((formatoFileDocDB = helper.getDecFormatoFileDocByName(formato.getNmFormatoFileDoc(),
                formato.getIdStrut())) != null) {
            Calendar dtSoppres = Calendar.getInstance();
            Calendar dataOdierna = Calendar.getInstance();
            dtSoppres.setTime(formatoFileDocDB.getDtSoppres());
            dtSoppres.set(Calendar.HOUR_OF_DAY, 23);
            dtSoppres.set(Calendar.MINUTE, 59);
            dtSoppres.set(Calendar.SECOND, 59);
            dtSoppres.set(Calendar.MILLISECOND, 999);

            // Presente ed attivo
            if (dtSoppres.after(dataOdierna)) {
                // Inserimento/duplica (in quanto id nullo)
                if (formato.getIdFormatoFileDoc() == null) {
                    throw new ParerUserError("Attenzione: il formato " + formato.getNmFormatoFileDoc()
                            + " non può essere salvato in quanto già presente");
                } // modifica (in quanto id presente)
                else {
                    DecFormatoFileDoc ffd = helper.findById(DecFormatoFileDoc.class, formato.getIdFormatoFileDoc());
                    if (!ffd.getNmFormatoFileDoc().equals(formato.getNmFormatoFileDoc())) {
                        throw new ParerUserError("Attenzione: il formato " + formato.getNmFormatoFileDoc()
                                + " non può essere salvato in quanto già presente");
                    }
                }

            } // Presente e disattivo
            else {
                throw new ParerWarningException("Attenzione: il formato " + formato.getNmFormatoFileDoc()
                        + " è già presente tra i formati ammessi nella struttura ma non è attivo; si desidera attivarlo?",
                        formatoFileDocDB.getIdFormatoFileDoc());

            }
        }

        executeModificaFormato(param, formato, decFormatoFileStandardTableBean, duplica);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executeModificaFormato(LogParam param, DecFormatoFileDocRowBean formato,
            DecFormatoFileStandardTableBean decFormatoFileStandardTableBean, Boolean duplica) throws ParerUserError {
        try {
            String[] sNew = formato.getNmFormatoFileDoc().split("[.]");

            if (formato.getIdFormatoFileDoc() != null) {
                helper.removeUsoFormatoFileStandardByFormatoFDoc(formato.getIdFormatoFileDoc());
                updateDecFormatoFileDoc(formato.getIdFormatoFileDoc(), formato);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO,
                        formato.getIdFormatoFileDoc(), param.getNomePagina());
            } else {
                BigDecimal idDecFormatoFileDoc = insertDecFormatoFileDoc(formato);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, idDecFormatoFileDoc,
                        param.getNomePagina());
                formato.setIdFormatoFileDoc(idDecFormatoFileDoc);
            }
            if (formato.getIdFormatoFileDoc() != null && duplica != null) {
                int counter = 0;
                if (!duplica) {
                    counter = helper.getUsoFormatoFileStandardMaxNrOrder(formato.getIdFormatoFileDoc()).intValue();
                }

                for (String row : sNew) {
                    DecFormatoFileStandard ffs = formatoFileStandardHelper.getDecFormatoFileStandardByName(row);
                    DecUsoFormatoFileStandardRowBean usoFormato = new DecUsoFormatoFileStandardRowBean();
                    usoFormato.setIdFormatoFileDoc(formato.getIdFormatoFileDoc());
                    BigDecimal id = ffs != null ? BigDecimal.valueOf(ffs.getIdFormatoFileStandard()) : null;
                    usoFormato.setIdFormatoFileStandard(id);
                    usoFormato.setNiOrdUso(new BigDecimal(++counter));
                    insertDecUsoFormatoFileStandard(usoFormato);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ParerUserError("Errore nel salvataggio del formato file doc: " + ex.getMessage());
        }
    }

    private BigDecimal insertDecFormatoFileDoc(DecFormatoFileDocRowBean formatoFileDocRowBean) {

        DecFormatoFileDoc formatoFileDoc = new DecFormatoFileDoc();

        OrgStrut struttura = helper.findById(OrgStrut.class, formatoFileDocRowBean.getIdStrut());
        if (struttura.getDecFormatoFileDocs() == null) {
            struttura.setDecFormatoFileDocs(new ArrayList<>());
        }

        BigDecimal idDecFormatoFileDoc = null;
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        DecFormatoFileDoc formatoFileDocByName = helper.getDecFormatoFileDocByName(
                formatoFileDocRowBean.getNmFormatoFileDoc(), formatoFileDocRowBean.getIdStrut());
        // Controllo il formato
        if (formatoFileDocByName == null) {
            try {
                formatoFileDoc = (DecFormatoFileDoc) Transform.rowBean2Entity(formatoFileDocRowBean);
                formatoFileDoc.setOrgStrut(struttura);
                if (formatoFileDoc.getDecUsoFormatoFileStandards() == null) {
                    formatoFileDoc.setDecUsoFormatoFileStandards(new ArrayList<>());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            helper.insertEntity(formatoFileDoc, true);
            struttura.getDecFormatoFileDocs().add(formatoFileDoc);
            idDecFormatoFileDoc = new BigDecimal(formatoFileDoc.getIdFormatoFileDoc());
        }
        return idDecFormatoFileDoc;
    }

    private void insertDecUsoFormatoFileStandard(DecUsoFormatoFileStandardRowBean usoFormatoFileStandardRowBean) {

        DecUsoFormatoFileStandard usoFormatoFileStandard = new DecUsoFormatoFileStandard();

        DecFormatoFileDoc formatoFileDoc = helper.findById(DecFormatoFileDoc.class,
                usoFormatoFileStandardRowBean.getIdFormatoFileDoc());
        if (formatoFileDoc.getDecUsoFormatoFileStandards() == null) {
            formatoFileDoc.setDecUsoFormatoFileStandards(new ArrayList<>());
        }
        DecFormatoFileStandard formatoFileStandard = formatoFileStandardHelper.findById(DecFormatoFileStandard.class,
                usoFormatoFileStandardRowBean.getIdFormatoFileStandard());
        if (formatoFileStandard.getDecUsoFormatoFileStandards() == null) {
            formatoFileStandard.setDecUsoFormatoFileStandards(new ArrayList<>());
        }

        try {

            usoFormatoFileStandard = (DecUsoFormatoFileStandard) Transform
                    .rowBean2Entity(usoFormatoFileStandardRowBean);
            usoFormatoFileStandard.setDecFormatoFileStandard(formatoFileStandard);
            usoFormatoFileStandard.setDecFormatoFileDoc(formatoFileDoc);
            usoFormatoFileStandard.setNiOrdUso(usoFormatoFileStandardRowBean.getNiOrdUso());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        helper.insertEntity(usoFormatoFileStandard, true);

        formatoFileDoc.getDecUsoFormatoFileStandards().add(usoFormatoFileStandard);
        formatoFileStandard.getDecUsoFormatoFileStandards().add(usoFormatoFileStandard);
    }

    public void updateDecFormatoFileDoc(BigDecimal idFormatoFileDoc, DecFormatoFileDocRowBean formatoFileDocRowBean)
            throws ParerUserError {
        DecFormatoFileDoc dbFormatoFileDoc = helper.getDecFormatoFileDocByName(
                formatoFileDocRowBean.getNmFormatoFileDoc(), formatoFileDocRowBean.getIdStrut());
        if (dbFormatoFileDoc != null && dbFormatoFileDoc.getIdFormatoFileDoc() != idFormatoFileDoc.longValue()) {
            throw new ParerUserError(
                    "Nome Formato File gi\u00E0 associato a questa struttura all'interno del database</br>");
        }

        DecFormatoFileDoc formatoFileDoc = helper.findById(DecFormatoFileDoc.class, idFormatoFileDoc);
        formatoFileDoc.setDsFormatoFileDoc(formatoFileDocRowBean.getDsFormatoFileDoc());
        formatoFileDoc.setDtSoppres(formatoFileDocRowBean.getDtSoppres());

        helper.getEntityManager().flush();
    }

    public void activateDecFormatoFileDoc(LogParam param, Long idFormatoFileDoc) {
        DecFormatoFileDoc formatoFileDoc = helper.findById(DecFormatoFileDoc.class, idFormatoFileDoc);
        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        formatoFileDoc.setDtSoppres(c.getTime());
        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO,
                BigDecimal.valueOf(idFormatoFileDoc), param.getNomePagina());
    }

    public DecFormatoFileDocTableBean getDecFormatoFileAmmessoTableBean(BigDecimal idTipoCompDoc) {
        DecFormatoFileDocTableBean formatoTableBean = new DecFormatoFileDocTableBean();
        List<Object[]> formati = helper.getDecFormatoFileAmmessoList(idTipoCompDoc);
        try {
            if (!formati.isEmpty()) {
                DecFormatoFileDocRowBean formatoRB;
                for (Object[] formato : formati) {
                    formatoRB = (DecFormatoFileDocRowBean) Transform.entity2RowBean(formato[1]);
                    formatoRB.setBigDecimal("id_formato_file_ammesso", new BigDecimal((Long) formato[0]));

                    if (formatoRB.getDtIstituz().before(new Date()) && formatoRB.getDtSoppres().after(new Date())) {
                        formatoRB.setObject("fl_attivo", "1");
                    } else {
                        formatoRB.setObject("fl_attivo", "0");
                    }

                    // Aggiunto il mimetype
                    List<DecUsoFormatoFileStandard> usi = ((DecFormatoFileDoc) formato[1])
                            .getDecUsoFormatoFileStandards();

                    String nmFormatoFileStandard = "";
                    boolean putComma = false;
                    for (DecUsoFormatoFileStandard uso : usi) {
                        if (putComma) {
                            nmFormatoFileStandard = nmFormatoFileStandard + ", ";
                        }
                        nmFormatoFileStandard = nmFormatoFileStandard
                                + uso.getDecFormatoFileStandard().getNmFormatoFileStandard();
                        putComma = true;
                        formatoRB.setString("ti_esito_contr_formato",
                                uso.getDecFormatoFileStandard().getTiEsitoContrFormato());
                        if (uso.getDecFormatoFileStandard().getFlFormatoConcat().equals("0")) {
                            formatoRB.setString("nm_mimetype_file",
                                    uso.getDecFormatoFileStandard().getNmMimetypeFile());
                        }
                    }
                    formatoRB.setString("nm_formato_file_standard", nmFormatoFileStandard);

                    formatoTableBean.add(formatoRB);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        formatoTableBean.addSortingRule("nm_mimetype_file");
        formatoTableBean.sort();
        return formatoTableBean;
    }

    public DecFormatoFileDocTableBean getDecFormatoFileAmmessoNotInList(Set<String> list, BigDecimal idStrut) {
        DecFormatoFileDocTableBean formatoTableBean = new DecFormatoFileDocTableBean();
        List<DecFormatoFileDoc> formati = helper.getDecFormatoFileAmmessoNotInList(list, idStrut);

        try {
            if (!formati.isEmpty()) {

                for (DecFormatoFileDoc formato : formati) {
                    DecFormatoFileDocRowBean row = (DecFormatoFileDocRowBean) Transform.entity2RowBean(formato);
                    // Aggiunto il mimetype
                    List<DecUsoFormatoFileStandard> usi = formato.getDecUsoFormatoFileStandards();

                    String nmFormatoFileStandard = "";
                    boolean putComma = false;
                    for (DecUsoFormatoFileStandard uso : usi) {
                        if (putComma) {
                            nmFormatoFileStandard = nmFormatoFileStandard + ", ";
                        }
                        nmFormatoFileStandard = nmFormatoFileStandard
                                + uso.getDecFormatoFileStandard().getNmFormatoFileStandard();
                        putComma = true;
                        row.setString("ti_esito_contr_formato",
                                uso.getDecFormatoFileStandard().getTiEsitoContrFormato());
                        if (uso.getDecFormatoFileStandard().getFlFormatoConcat().equals("0")) {
                            row.setString("nm_mimetype_file", uso.getDecFormatoFileStandard().getNmMimetypeFile());
                        }
                    }
                    row.setString("nm_formato_file_standard", nmFormatoFileStandard);
                    formatoTableBean.add(row);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return formatoTableBean;
    }

    /**
     * Elimina i formati passati come parametro di input e restituisce la lista di quelli non eliminati in quanto
     * associati ad altri elementi e dunque non eliminabili
     *
     * @param param
     *            parametro per il logging
     * @param idStrut
     *            struttura
     * @param formatoFileDocList
     *            formato file (lista elementi)
     * 
     * @return resituisce lista di elementi di tipo String
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<String> deleteDecFormatoFileDocList(LogParam param, BigDecimal idStrut,
            List<String> formatoFileDocList) {
        List<String> formatiNonEliminati = new ArrayList<>();
        for (String nmFormatoFileDoc : formatoFileDocList) {
            DecFormatoFileDoc formatoFileDoc = null;

            formatoFileDoc = helper.getDecFormatoFileDocByName(nmFormatoFileDoc, idStrut);
            if (formatoFileDoc != null) {
                if (helper.checkRelationsAreEmptyForDecFormatoFileDoc(formatoFileDoc.getIdFormatoFileDoc())
                        || helper.checkRelationsAreEmptyForDecFormatoFileDocConv(formatoFileDoc.getIdFormatoFileDoc())
                        || helper
                                .checkRelationsAreEmptyForDecFormatoFileDocCont(formatoFileDoc.getIdFormatoFileDoc())) {
                    formatiNonEliminati.add(nmFormatoFileDoc);
                } else {
                    /*
                     * Questo array serve solo per contenere UN SOLO elemento perché si riutilizza il metodo di
                     * cancellazione che già lavora con gli array.
                     */
                    List<String> elimina = new ArrayList<>();
                    elimina.add(nmFormatoFileDoc);
                    /* Prende gli Id dei formati File Doc e li fotografa tutti */
                    List<DecFormatoFileDoc> decFmts = helper.retrieveDecFormatoFileDocList(idStrut, elimina);
                    BigDecimal idFormatoFile = null;
                    if (decFmts != null) {
                        idFormatoFile = new BigDecimal(decFmts.get(0).getIdFormatoFileDoc());
                    }
                    // LOG BEFORE PER IL FORMATO AMMESSO
                    /*
                     * Se il TransactionContext è già valorizzato usa quello altrimenti ne ottiene uno nuovo e lo
                     * valorizza su logParam per usi successivi nel caso in cui tutto dovesse essere loggato nello
                     * stesso contesto transazionale logico del logging.
                     */
                    if (!param.isTransactionActive()) {
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    }
                    List<ObjectsToLogBefore> listaOggettiDaLoggare = sacerLogEjb.logBefore(
                            param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                            param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, idFormatoFile,
                            param.getNomePagina());
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, idFormatoFile, param.getNomePagina());
                    helper.deleteDecFormatoFileDocList(idStrut, elimina);
                    sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(), listaOggettiDaLoggare, param.getNomePagina());
                }
            }
        }
        return formatiNonEliminati;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecFormatoFileDoc(LogParam param, long idFormatoFileDoc) throws ParerUserError {
        DecFormatoFileDoc formatoFileDoc = helper.findById(DecFormatoFileDoc.class, idFormatoFileDoc);
        String nmFormatoFileDoc = formatoFileDoc.getNmFormatoFileDoc();
        long idStrut = formatoFileDoc.getOrgStrut().getIdStrut();
        if (helper.checkRelationsAreEmptyForDecFormatoFileDoc(formatoFileDoc.getIdFormatoFileDoc())
                || helper.checkRelationsAreEmptyForDecFormatoFileDocConv(formatoFileDoc.getIdFormatoFileDoc())
                || helper.checkRelationsAreEmptyForDecFormatoFileDocCont(formatoFileDoc.getIdFormatoFileDoc())) {
            throw new ParerUserError(
                    "Impossibile eliminare il formato ammesso: esiste almeno un elemento associato ad esso</br>");
        }
        // LOG BEFORE PER IL TIPO STRUTTURA
        /*
         * Se il TransactionContext è già valorizzato usa quello altrimenti ne ottiene uno nuovo e lo valorizza su
         * logParam per usi successivi nel caso in cui tutto dovesse essere loggato nello stesso contesto transazionale
         * logico del logging.
         */
        if (!param.isTransactionActive()) {
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        }
        List<ObjectsToLogBefore> listaOggettiDaLoggare = sacerLogEjb.logBefore(param.getTransactionLogContext(),
                param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
                SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, new BigDecimal(idFormatoFileDoc),
                param.getNomePagina());
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO, new BigDecimal(idFormatoFileDoc),
                param.getNomePagina());
        helper.removeEntity(formatoFileDoc, true);
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaOggettiDaLoggare, param.getNomePagina());

        logger.info("Cancellazione formato file documento " + nmFormatoFileDoc + " della struttura " + idStrut
                + " avvenuta con successo!");
    }

    public boolean isDecFormatoFileDocInUse(DecFormatoFileDocRowBean formatoFileDocRowBean) {
        return helper
                .checkRelationsAreEmptyForDecFormatoFileDoc(formatoFileDocRowBean.getIdFormatoFileDoc().longValue());
    }

    public DecFormatoFileDocTableBean getDecFormatoFileDocNiOrdUsoTableBean(BigDecimal idStrut) {
        DecFormatoFileDocTableBean table = new DecFormatoFileDocTableBean();
        List<DecFormatoFileDoc> list = helper.retrieveFormatoFileDocNiOrdUsoTB(idStrut);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecFormatoFileDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore inatteso nel recupero dei formati per la struttura "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalArgumentException("Errore inatteso nel recupero dei formati per la struttura");
            }
        }
        return table;
    }
}
