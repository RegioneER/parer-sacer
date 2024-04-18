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

package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.SubStruttureAbstractAction;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.slite.gen.tablebean.OrgRegolaValSubStrutTableBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Bonora_L
 */
public class SubStruttureAction extends SubStruttureAbstractAction {

    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (!getNavigationEvent().equals(ListAction.NE_DETTAGLIO_DELETE)
                && !getNavigationEvent().equals(ListAction.NE_DETTAGLIO_INSERT)) {
            if (getTableName().equals(getForm().getSubStrutList().getName())) {
                getForm().getSubStrut().copyFromBean(getForm().getSubStrutList().getTable().getCurrentRow());
                BigDecimal idSubStrut = getForm().getSubStrut().getId_sub_strut().parse();
                BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
                // Carico la lista con le regole sulla sottostruttura
                OrgRegolaValSubStrutTableBean regolaValSubStrutTableBean = subStrutEjb
                        .getOrgRegolaValSubStrutTableBean(idStrut, idSubStrut);
                getForm().getRegoleSubStrutList().setTable(regolaValSubStrutTableBean);
                getForm().getRegoleSubStrutList().getTable().setPageSize(10);
                getForm().getRegoleSubStrutList().getTable().first();

                String cessato = (String) getRequest().getParameter("cessato");
                if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                    getForm().getSubStrutList().setUserOperations(true, false, false, false);
                    getForm().getRegoleSubStrutList().setUserOperations(true, false, false, false);
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getForm().getSubStrutList().getStatus().equals(Status.insert)) {
            goBack();
        } else {
            getForm().getSubStrut().copyFromBean(getForm().getSubStrutList().getTable().getCurrentRow());
            getForm().getSubStrutList().setStatus(Status.view);
            getForm().getSubStrut().setStatus(Status.view);
            getForm().getSubStrut().setViewMode();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSubStrutList().getName())) {
            boolean isTemplate = getForm().getStrutRif().getFl_template().parse().equals("1");
            if (isTemplate) {
                getMessageBox()
                        .addError("La struttura \u00E8 di tipo template, impossibile creare nuove sottostrutture");
                forwardToPublisher(getDefaultPublsherName());
            } else {
                getForm().getSubStrut().reset();
                getForm().getSubStrutList().setStatus(Status.insert);
                getForm().getSubStrut().setStatus(Status.insert);
                getForm().getSubStrut().setEditMode();
            }
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getForm().getSubStrut().postAndValidate(getRequest(), getMessageBox())) {
            BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
            // Codice aggiuntivo per il logging
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (getForm().getSubStrutList().getStatus().equals(Status.insert)) {
                if (!subStrutEjb.existOrgSubStrut(getForm().getSubStrut().getNm_sub_strut().parse(), idStrut)) {
                    Long idSubStrut = null;
                    try {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        // idSubStrut = struttureEjb.saveSubStrut(getForm().getSubStrut().getNm_sub_strut().parse(),
                        // getForm().getSubStrut().getDs_sub_strut().parse(), idStrut);
                        idSubStrut = subStrutEjb.insertOrgSubStrut(param,
                                getForm().getSubStrut().getNm_sub_strut().parse(),
                                getForm().getSubStrut().getDs_sub_strut().parse(), idStrut);
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                    if (!getMessageBox().hasError()) {
                        if (idSubStrut != null) {
                            getForm().getSubStrut().getId_sub_strut().setValue(String.valueOf(idSubStrut));
                        }

                        // Gestisco la lista per avere la riga corrente
                        OrgSubStrutTableBean table = new OrgSubStrutTableBean();
                        OrgSubStrutRowBean row = new OrgSubStrutRowBean();
                        getForm().getSubStrut().copyToBean(row);
                        table.add(row);
                        table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        table.first();

                        getForm().getSubStrutList().setTable(table);
                        getSession().setAttribute("elementoInserito", row.getIdSubStrut());
                    }
                } else {
                    getMessageBox().addError("Sottostruttura gi\u00E0 presente nel sistema");
                }
            } else if (getForm().getSubStrutList().getStatus().equals(Status.update)) {

                OrgSubStrutRowBean row = (OrgSubStrutRowBean) getForm().getSubStrutList().getTable().getCurrentRow();
                BigDecimal idSubStrut = row.getIdSubStrut();
                if (idSubStrut == null) {
                    getMessageBox().addError(
                            "Errore inaspettato. Ritentare il caricamento e la modifica della sottostruttura");
                } else {
                    try {
                        String name = getForm().getSubStrut().getNm_sub_strut().isViewMode() ? null
                                : getForm().getSubStrut().getNm_sub_strut().parse();
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        // struttureEjb.saveSubStrut(idSubStrut, name,
                        // getForm().getSubStrut().getDs_sub_strut().parse());
                        subStrutEjb.updateOrgSubStrut(param, name, getForm().getSubStrut().getDs_sub_strut().parse(),
                                idSubStrut);
                        if (name != null) {
                            row.setNmSubStrut(name);
                        }
                        row.setDsSubStrut(getForm().getSubStrut().getDs_sub_strut().parse());
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
            if (!getMessageBox().hasError()) {
                getMessageBox().addInfo("Sottostruttura salvata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                getForm().getSubStrutList().setStatus(Status.view);
                getForm().getSubStrut().setStatus(Status.view);
                getForm().getSubStrut().setViewMode();
            }
            forwardToPublisher(getDefaultPublsherName());
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getTableName().equals(getForm().getSubStrutList().getName())) {
            getForm().getSubStrutList().setStatus(Status.view);
            getForm().getSubStrut().setStatus(Status.view);
            getForm().getSubStrut().setViewMode();
            forwardToPublisher(Application.Publisher.SUB_STRUT_DETAIL);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.SUB_STRUT_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SUB_STRUTTURE;
    }

    @Override
    public void updateSubStrutList() throws EMFError {
        boolean isTemplate = getForm().getStrutRif().getFl_template().parse().equals("1");
        if (isTemplate) {
            getMessageBox().addError("La struttura \u00E8 di tipo template, impossibile modificare le sottostrutture");
        } else {
            // Verifica presenza UD legate alla sottostruttura
            BigDecimal idSubStrut = ((OrgSubStrutRowBean) getForm().getSubStrutList().getTable().getCurrentRow())
                    .getIdSubStrut();
            if (idSubStrut == null) {
                getMessageBox()
                        .addError("Errore inaspettato. Ritentare il caricamento e la modifica della sottostruttura");
            } else {
                getForm().getSubStrutList().setStatus(Status.update);
                getForm().getSubStrut().setStatus(Status.update);
                if (subStrutEjb.existUdInSubStrut(idSubStrut)) {
                    getForm().getSubStrut().getNm_sub_strut().setViewMode();
                    getForm().getSubStrut().getDs_sub_strut().setEditMode();
                } else {
                    getForm().getSubStrut().setEditMode();
                }
            }
        }
        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public void deleteSubStrutList() throws EMFError {
        BigDecimal idInserito = (BigDecimal) getSession().getAttribute("elementoInserito");
        if (getForm().getSubStrutList().getTable().size() == 1 & idInserito == null) {
            getMessageBox().addError("\u00C8 obbligatoria la presenza di almeno una sottostruttura per ogni struttura");
            goBack();
        } else {
            boolean isTemplate = getForm().getStrutRif().getFl_template().parse().equals("1");
            if (isTemplate) {
                getMessageBox()
                        .addError("La struttura \u00E8 di tipo template, impossibile eliminare le sottostrutture");
            } else {
                BigDecimal idSubStrut = idInserito != null ? idInserito
                        : ((OrgSubStrutRowBean) getForm().getSubStrutList().getTable().getCurrentRow()).getIdSubStrut();
                if (idSubStrut == null) {
                    getMessageBox().addError(
                            "Errore inaspettato. Ritentare il caricamento e la modifica della sottostruttura");
                } else {
                    if (subStrutEjb.existUdInSubStrut(idSubStrut)) {
                        getMessageBox().addError(
                                "Impossibile eliminare la sottostruttura: esiste almeno un elemento associato ad essa");
                    } else {
                        try {
                            // Codice aggiuntivo per il logging
                            LogParam param = SpagoliteLogUtil.getLogParam(
                                    configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.SUB_STRUT_DETAIL)) {
                                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                            } else {
                                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                                param.setNomeAzione(
                                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getSubStrutList()));
                            }
                            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                            subStrutEjb.deleteOrgSubStrut(param, idSubStrut.longValue(), false);
                            getMessageBox().addInfo("Sottostruttura eliminata con successo");
                            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                            getSession().removeAttribute("elementoInserito");
                            goBackTo(Application.Publisher.CREA_STRUTTURA);
                        } catch (ParerUserError ex) {
                            getMessageBox().addError(ex.getDescription());
                        }
                    }
                }
            }
        }
    }

}
