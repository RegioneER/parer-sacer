package it.eng.parer.web.action;

import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.NoteRilascioAbstractAction;
import it.eng.parer.slite.gen.form.NoteRilascioForm;
import it.eng.parer.slite.gen.tablebean.SIAplNotaRilascioRowBean;
import it.eng.parer.slite.gen.tablebean.SIAplNotaRilascioTableBean;
import it.eng.parer.slite.gen.tablebean.SIAplNotaRilascioTableDescriptor;
import it.eng.parer.informazioni.noteRilascio.ejb.NoteRilascioEjb;
import it.eng.parer.web.util.Constants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
public class NoteRilascioAction extends NoteRilascioAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(NoteRilascioAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/NoteRilascioEjb")
    private NoteRilascioEjb noteRilascioEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            // Se carico il dettaglio della nota di rilascio
            if (lista.equals(getForm().getListaNoteRilascio().getName())) {
                // Imposto lista e dettaglio in modalitÃ  visualizzazione
                getForm().getListaNoteRilascio().setStatus(Status.view);
                // getForm().getListaNoteRilascio().setHideDeleteButton(true);
                getForm().getDettaglioNoteRilascio().setViewMode();

                // Carico le info del dettaglio NOTE DI RILASCIO
                if (!((SIAplNotaRilascioTableBean) getForm().getListaNoteRilascio().getTable()).isEmpty()) {
                    BigDecimal idNotaRilascio = ((SIAplNotaRilascioTableBean) getForm().getListaNoteRilascio()
                            .getTable()).getCurrentRow().getIdNotaRilascio();
                    SIAplNotaRilascioRowBean detailRow = noteRilascioEjb.getAplNotaRilascioRowBean(idNotaRilascio);
                    getForm().getDettaglioNoteRilascio().copyFromBean(detailRow);

                    Calendar today = GregorianCalendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    // if (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() == null ||
                    // (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() != null &&
                    // today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_fine_val().parse()) <= 0)
                    // && today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_ini_val().parse()) >= 0)
                    // {
                    getRequest().setAttribute("showNotaRilascio", true);
                    // } else {
                    // getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                    // }
                    // Carico da DB la lista delle note di rilascio precedenti associate all'applicazione
                    SIAplNotaRilascioTableBean noteRilascioPrecTableBean = noteRilascioEjb
                            .getAplNoteRilascioPrecTableBean(detailRow.getBigDecimal("id_applic"), idNotaRilascio,
                                    new Date(detailRow.getTimestamp("dt_versione").getTime()));
                    getForm().getNoteRilascioPrecedentiList().setTable(noteRilascioPrecTableBean);
                    getForm().getNoteRilascioPrecedentiList().getTable().setPageSize(10);
                    getForm().getNoteRilascioPrecedentiList().getTable()
                            .addSortingRule(SIAplNotaRilascioTableDescriptor.COL_DT_VERSIONE, SortingRule.DESC);
                    getForm().getNoteRilascioPrecedentiList().getTable().sort();
                    getForm().getNoteRilascioPrecedentiList().getTable().first();
                    // Disabilito i tasti sulla lista note di rilascio precedenti
                    getForm().getNoteRilascioPrecedentiList().setUserOperations(true, false, false, false);
                } else {
                    getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            if (getForm().getListaNoteRilascio().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_NOTE_RILASCIO);
            } else if (getTableName().equals(getForm().getNoteRilascioPrecedentiList().getName())) {
                redirectToNoteRilascioPage();
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.DETTAGLIO_NOTE_RILASCIO;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.DETTAGLIO_NOTE_RILASCIO)) {
                Calendar today = GregorianCalendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                // if (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() == null ||
                // (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() != null &&
                // today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_fine_val().parse()) <= 0)
                // && today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_ini_val().parse()) >= 0) {
                getRequest().setAttribute("showNotaRilascio", true);
                // } else {
                // getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                // }
                getSession().removeAttribute("isFromNotaRilascioPrec");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.NOTE_RILASCIO;
    }

    @Secure(action = "Menu.Informazioni.NoteRilascioVersCor")
    public void ricercaNoteRilascioPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Informazioni.NoteRilascioVersCor");

        getForm().getListaNoteRilascio().clear();
        getForm().getListaNoteRilascio().setStatus(Status.view);

        // Azzero in sessione l'attributo che mi tiene conto della lista delle note di rilascio precedenti
        getSession().removeAttribute("isFromNotaRilascioPrec");

        ricercaNoteRilascio(Constants.SACER);
    }

    /**
     * Ricerca note di rilascio in base all'applicazione passata in input
     *
     * @param nmApplic
     *            nome applicazione
     * 
     * @throws EMFError
     *             errore generico
     */
    public void ricercaNoteRilascio(String nmApplic) throws EMFError {

        SIAplNotaRilascioTableBean noteRilascioTableBean = noteRilascioEjb.getAplNoteRilascioTableBean(nmApplic);
        getForm().getListaNoteRilascio().setTable(noteRilascioTableBean);
        getForm().getListaNoteRilascio().getTable().first();
        getForm().getListaNoteRilascio().getTable().setPageSize(10);
        getForm().getListaNoteRilascio().setStatus(Status.view);
        getForm().getListaNoteRilascio().setHideDeleteButton(false);
        setTableName(getForm().getListaNoteRilascio().getName());
        loadDettaglio();
    }

    private void redirectToNoteRilascioPage() throws EMFError {
        NoteRilascioForm form = new NoteRilascioForm();
        form.getListaNoteRilascio()
                .setFilterValidRecords(getForm().getNoteRilascioPrecedentiList().isFilterValidRecords());
        form.getListaNoteRilascio().setUserOperations(true, false, false, false);
        getSession().setAttribute("isFromNotaRilascioPrec", true);
        redirectToPage(Application.Actions.NOTE_RILASCIO, form, form.getListaNoteRilascio().getName(),
                getForm().getNoteRilascioPrecedentiList().getTable(), getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }
}
