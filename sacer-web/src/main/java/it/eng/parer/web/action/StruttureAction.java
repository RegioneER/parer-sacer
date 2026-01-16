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
package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper.FormatoFileDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb.FormatoFileStandardEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb.SistemaMigrazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.SalvaStrutturaDto;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.CopiaStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb.StrutTitolariEjb;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroRichiestaRa;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.grantedEntity.SIOrgEnteConvenzOrg;
import it.eng.parer.restArch.ejb.RestituzioneArchivioEjb;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StruttureAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.EntiConvenzionatiForm;
import it.eng.parer.slite.gen.form.StrutDatiSpecForm;
import it.eng.parer.slite.gen.form.StrutFormatoFileForm;
import it.eng.parer.slite.gen.form.StrutSerieForm;
import it.eng.parer.slite.gen.form.StrutTipiFascicoloForm;
import it.eng.parer.slite.gen.form.StrutTipiForm;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm;
import it.eng.parer.slite.gen.form.StrutTitolariForm;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.form.StruttureForm.CheckDuplicaStruttura;
import it.eng.parer.slite.gen.form.StruttureForm.InsStruttura;
import it.eng.parer.slite.gen.form.StruttureForm.TipoRapprComp;
import it.eng.parer.slite.gen.form.StruttureForm.VisStrutture;
import it.eng.parer.slite.gen.form.SubStruttureForm;
import it.eng.parer.slite.gen.form.TrasformatoriForm;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaMigrazTableBean;
import it.eng.parer.slite.gen.tablebean.DecAaTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecCategTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecCategTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTitolRowBean;
import it.eng.parer.slite.gen.tablebean.DecTrasformTipoRapprRowBean;
import it.eng.parer.slite.gen.tablebean.DecTrasformTipoRapprTableBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbitoTerritRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbitoTerritTableBean;
import it.eng.parer.slite.gen.tablebean.OrgCategEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgCategEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgCategStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgCategStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgUsoSistemaMigrazRowBean;
import it.eng.parer.slite.gen.tablebean.OrgUsoSistemaMigrazTableBean;
import it.eng.parer.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.parer.slite.gen.viewbean.DecDocProcessoConservRowBean;
import it.eng.parer.slite.gen.viewbean.DecDocProcessoConservTableBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicEnteTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicStrutRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicStrutTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.xml.bind.JAXBException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

public class StruttureAction extends StruttureAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(StruttureAction.class.getName());

    private static final String ERRORE_CARICAMENTO_STRUTTURA = "Errore durante il caricamento della struttura";
    private static final String WARN_VALORE_STRUTTURA_ASSENTE = "Valore sulla struttura non presente: nessuna cancellazione effettuata";
    private static final String ECCEZIONE_IMPORT_TIPO_UD = "Eccezione nell'import del tipo unitÃ  documentaria";
    private static final String ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_SUCCESSIVA_FINE = "Errore di compilazione form: la data di inizio validitÃ  Ã¨ successiva a quella di fine validitÃ </br>";
    private static final String ERRORE_COMPILAZIONE_DATA_FINE_VALIDITA_ASSENTE = "Errore di compilazione form: data fine validitÃ  non inserita</br>";
    private static final String ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_ASSENTE = "Errore di compilazione form: data inizio validitÃ  non inserita</br>";
    private static final String ERRORE_COMPILAZIONE_ENTE_CONVENZIONATO_ASSENTE = "Errore di compilazione form: ente convenzionato non inserito</br>";
    private static final String ERRORE_COMPILAZIONE_AMBIENTE_ASSENTE = "Errore di compilazione form: ambiente ente convenzionato non inserito</br>";
    private static final String ERRORE_COMPILAZIONE_STRUTTURA_ASSENTE = "Errore di compilazione form: Nome struttura non inserito</br>";
    private static final String ERRORE_SELEZIONE_ENTE_RIFERIMENTO = "Selezionare un ente di riferimento</br>";
    private static final String ECCEZIONE_GENERICA = "Eccezione";
    private static final String PARAMETER_ID_AMBIENTE = "idAmbiente";
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocHelper")
    private FormatoFileDocHelper formatoFileDocHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoSerieEjb")
    private TipoSerieEjb tipoSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StrutTitolariEjb")
    private StrutTitolariEjb titolariEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SpringTikaSingleton")
    private SpringTikaSingleton singleton;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggrHelper")
    private CriteriRaggrHelper crHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SistemaMigrazioneEjb")
    private SistemaMigrazioneEjb sysMigrazioneEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoRappresentazioneEjb")
    private TipoRappresentazioneEjb tipoRapprEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggruppamentoEjb")
    private CriteriRaggruppamentoEjb critRaggrEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileStandardEjb")
    private FormatoFileStandardEjb formatiStandardEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RestituzioneArchivioEjb")
    private RestituzioneArchivioEjb restArchEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecificiEjb;

    // Pattern per l'inserimento del nome struttura conforme al set di caratteri ammessi
    private static final String NOME_STRUT = "^[A-Za-z0-9_][A-Za-z0-9\\. _-]*$";
    private static final Pattern strutPattern = Pattern.compile(NOME_STRUT);

    private static final String SES_ATTRIB_SALVATAGGIO = "salvataggio";
    private static final String SES_ATTRIB_VALORI_SALVATAGGIO = "valoriSalvataggio";

    private enum TipoSalvataggio {

        STRUTTURA, DUPLICA_STANDARD, DUPLICA_NON_STANDARD, IMPORTA_STANDARD, IMPORTA_NON_STANDARD
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.CREA_STRUTTURA;
    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart) {
            // Wizard di inserimento parametri in Dettaglio Struttura
            if (getLastPublisher().equals(Application.Publisher.IMPORTA_PARAMETRI_WIZARD)) {
                int size100Mb = 100 * WebConstants.FILESIZE * WebConstants.FILESIZE;
                try {
                    String[] a = getForm().getImportaParametri().postMultipart(getRequest(),
                            size100Mb);
                    if (a != null) {
                        String operationMethod = a[0];
                        String[] navigationParams = Arrays.copyOfRange(a, 1, a.length);

                        Method method = StruttureAction.class.getMethod(operationMethod,
                                String[].class);
                        method.invoke(this, (Object) navigationParams);
                    }

                } catch (FileUploadException | NoSuchMethodException | SecurityException
                        | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException ex) {
                    log.error("Errore nell'invocazione del metodo di navigazione wizard :"
                            + ExceptionUtils.getRootCauseMessage(ex), ex);
                    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
                    goBack();
                }
            } // Pagina di Importa Struttura
            else if (getLastPublisher().equals(Application.Publisher.IMPORTA_STRUTTURA)) {
                int size100Mb = 100 * WebConstants.FILESIZE * WebConstants.FILESIZE;
                try {
                    String[] a = getForm().getInsStruttura().postMultipart(getRequest(), size100Mb);
                    if (a != null) {
                        String operationMethod = a[0];
                        Method method = StruttureAction.class.getMethod(operationMethod);
                        method.invoke(this);
                    }

                } catch (FileUploadException | NoSuchMethodException | SecurityException
                        | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException ex) {
                    log.error("Errore nella procedura di importa struttura :"
                            + ExceptionUtils.getRootCauseMessage(ex), ex);
                    getMessageBox().addError("Errore nella procedura di importa struttura");
                    goBack();
                }
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUTTURE;
    }

    @Override
    public void initOnClick() throws EMFError {
    }

    /**
     *
     * Caricamento dei dati del record scelto attraverso i tasti della lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {

        try {
            String lista = getTableName();

            String action = getNavigationEvent();

            if (lista != null && (action != null && !action.equals(NE_DETTAGLIO_INSERT))) {
                String categorieEntiListName = getForm().getCategorieEntiList().getName();
                if (lista.equals(categorieEntiListName)
                        && (getForm().getCategorieEntiList().getTable() != null)
                        && (getForm().getCategorieEntiList().getTable().size() > 0)) {

                    getForm().getCategorieEnti().setViewMode();
                    getForm().getCategorieEntiList().setStatus(Status.view);

                    BigDecimal idCategEnte = ((OrgCategEnteRowBean) getForm().getCategorieEntiList()
                            .getTable().getCurrentRow()).getIdCategEnte();

                    OrgCategEnteRowBean categEnteRowBean = ambienteEjb
                            .getOrgCategEnteRowBean(idCategEnte);
                    getForm().getCategorieEnti().copyFromBean(categEnteRowBean);

                } else if (lista.equals(getForm().getStruttureList().getName())
                        && (getForm().getStruttureList().getTable() != null)
                        && (getForm().getStruttureList().getTable().size() > 0)) {

                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    loadStruttura(idStrut);

                } else if (lista.equals(getForm().getTipoRapprCompList().getName())
                        && (getForm().getTipoRapprCompList().getTable() != null)
                        && (getForm().getTipoRapprCompList().getTable().size() > 0)) {

                    getForm().getTipoRapprComp().setViewMode();
                    getForm().getTipoRapprCompList().setStatus(Status.view);

                    getForm().getInsStruttura().getNm_strut().setHidden(false);
                    getForm().getInsStruttura().getDs_strut().setHidden(false);
                    getForm().getInsStruttura().getStruttura()
                            .setValue(getForm().getInsStruttura().getNm_strut().parse() + " - "
                                    + getForm().getInsStruttura().getDs_strut().parse());
                    getForm().getInsStruttura().getId_ente().setHidden(false);
                    BigDecimal idTipoRapprComp = ((DecTipoRapprCompRowBean) getForm()
                            .getTipoRapprCompList().getTable().getCurrentRow())
                            .getIdTipoRapprComp();

                    DecTipoRapprCompRowBean tipoRapprCompRowBean = tipoRapprEjb
                            .getDecTipoRapprCompRowBean(idTipoRapprComp, null);
                    getForm().getTipoRapprComp().copyFromBean(tipoRapprCompRowBean);

                    DecodeMap mappaAlgoritmoRappr = ComboGetter.getMappaTiAlgoRappr();
                    DecodeMap mappaTipoOutputRappr = ComboGetter.getMappaTiOutputRappr();
                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
                            .getDecFormatoFileDocNiOrdUsoTableBean(idStrut);
                    DecodeMap mappaFormatoFile = new DecodeMap();
                    mappaFormatoFile.populatedMap(tmpTableBeanFormatoFileDoc, "id_formato_file_doc",
                            "nm_formato_file_doc");
                    getForm().getTipoRapprComp().getId_formato_contenuto()
                            .setDecodeMap(mappaFormatoFile);
                    getForm().getTipoRapprComp().getId_formato_convertit()
                            .setDecodeMap(mappaFormatoFile);
                    DecFormatoFileStandardTableBean tmpTableBeanFfs = formatiStandardEjb
                            .getDecFormatoFileStandardTableBean(null);
                    mappaFormatoFile = new DecodeMap();
                    mappaFormatoFile.populatedMap(tmpTableBeanFfs, "id_formato_file_standard",
                            "nm_formato_file_standard");
                    getForm().getTipoRapprComp().getId_formato_output_rappr()
                            .setDecodeMap(mappaFormatoFile);
                    getForm().getTipoRapprComp().getTi_algo_rappr()
                            .setDecodeMap(mappaAlgoritmoRappr);
                    getForm().getTipoRapprComp().getTi_output_rappr()
                            .setDecodeMap(mappaTipoOutputRappr);
                    reloadTipoRapprCompLists(idTipoRapprComp);
                    if (!action.equals(NE_DETTAGLIO_VIEW)) {
                        getForm().getTipoRapprComp().getLogEventiTipoRapprComp().setEditMode();
                    }
                } else if (lista.equals(getForm().getCategorieStruttureList().getName())
                        && (getForm().getCategorieStruttureList().getTable() != null)
                        && (getForm().getCategorieStruttureList().getTable().size() > 0)) {

                    getForm().getCategorieStrutture().setViewMode();
                    getForm().getCategorieStruttureList().setStatus(Status.view);
                    BigDecimal idCategStrut = ((OrgCategStrutRowBean) getForm()
                            .getCategorieStruttureList().getTable().getCurrentRow())
                            .getIdCategStrut();
                    OrgCategStrutRowBean categStrutRowBean = struttureEjb
                            .getOrgCategStrutRowBean(idCategStrut);
                    getForm().getCategorieStrutture().copyFromBean(categStrutRowBean);
                } else if (lista
                        .equals(getForm().getTipoCompAmmessoDaTipoRapprCompList().getName())) {
                    getForm().getTipoCompAmmessoDaTipoRapprComp().setViewMode();
                    getForm().getTipoCompAmmessoDaTipoRapprComp().setStatus(Status.view);
                    getForm().getTipoCompAmmessoDaTipoRapprCompList().setStatus(Status.view);
                    // Popolo le due combo e setto il valore selezionato
                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    // Id tipo struttura documento
                    DecTipoStrutDocTableBean table = tipoDocEjb
                            .getDecTipoStrutDocTableBeanByIdStrut(idStrut, new Date());
                    DecodeMap mappaTipoStrutDoc = new DecodeMap();
                    mappaTipoStrutDoc.populatedMap(table, "id_tipo_strut_doc", "nm_tipo_strut_doc");
                    getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc()
                            .setDecodeMap(mappaTipoStrutDoc);
                    BigDecimal idTipoStrutDoc = ((DecTipoRapprAmmessoTableBean) getForm()
                            .getTipoCompAmmessoDaTipoRapprCompList().getTable()).getCurrentRow()
                            .getBigDecimal("id_tipo_strut_doc");
                    getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc()
                            .setValue("" + idTipoStrutDoc);
                    // Id tipo componente ammesso
                    DecTipoCompDocTableBean tipoCompDoc = tipoStrutDocEjb
                            .getDecTipoCompDocTableBean(idStrut, new Date(), idTipoStrutDoc);
                    DecodeMap mappaTipoCompDoc = new DecodeMap();
                    mappaTipoCompDoc.populatedMap(tipoCompDoc, "id_tipo_comp_doc",
                            "nm_tipo_comp_doc");
                    getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc()
                            .setDecodeMap(mappaTipoCompDoc);
                    BigDecimal idTipoCompDocAmmesso = ((DecTipoRapprAmmessoTableBean) getForm()
                            .getTipoCompAmmessoDaTipoRapprCompList().getTable()).getCurrentRow()
                            .getBigDecimal("id_tipo_comp_doc");
                    getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc()
                            .setValue("" + idTipoCompDocAmmesso);
                } else if (lista.equals(getForm().getSistemiMigrazioneList().getName())) {
                    getForm().getXsdMigrStrutTab()
                            .setCurrentTab(getForm().getXsdMigrStrutTab().getXsdMigrTipoUnitaDoc());
                    getForm().getGestioneXsdMigrazione().reset();

                    getForm().getInsStruttura().getNm_strut().setViewMode();
                    getForm().getInsStruttura().getNm_strut().setHidden(false);
                    getForm().getInsStruttura().getDs_strut().setViewMode();
                    getForm().getInsStruttura().getDs_strut().setHidden(false);
                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    OrgUsoSistemaMigrazRowBean uso = (OrgUsoSistemaMigrazRowBean) getForm()
                            .getSistemiMigrazioneList().getTable().getCurrentRow();

                    // inizializzazione combo
                    OrgUsoSistemaMigrazTableBean orgUsoTableBean = new OrgUsoSistemaMigrazTableBean();
                    orgUsoTableBean.add(uso);
                    getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                            .setDecodeMap(DecodeMap.Factory.newInstance(orgUsoTableBean,
                                    "id_sistema_migraz", "nm_sistema_migraz"));
                    getForm().getGestioneXsdMigrazione().getId_strut()
                            .setValue(idStrut.toPlainString());

                    getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                            .setValue(uso.getIdSistemaMigraz().toPlainString());
                    String nmSistemaMigraz = getForm().getGestioneXsdMigrazione()
                            .getNm_sistema_migraz().getDecodedValue();
                    getForm().getGestioneXsdMigrazione().getNm_sistema_migraz().setViewMode();
                    getForm().getGestioneXsdMigrazione().setStatus(Status.view);

                    DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb
                            .getDecXsdDatiSpecTableBean(idStrut,
                                    CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                                    CostantiDB.TipiEntitaSacer.UNI_DOC.name(), nmSistemaMigraz);
                    getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                    getForm().getXsdDatiSpecList().getTable().first();
                    getForm().getXsdDatiSpecList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    if ("1".equals(getForm().getInsStruttura().getFl_cessato().parse())) {
                        getForm().getXsdDatiSpecList().setUserOperations(true, false, false, false);
                    } else {
                        getForm().getXsdDatiSpecList().setUserOperations(true, true, true, true);
                    }
                } else if (lista.equals(getForm().getDocumentiProcessoConservList().getName())) {
                    initDocumentoProcessoConservDetail();
                    DecDocProcessoConservRowBean currentRow = (DecDocProcessoConservRowBean) getForm()
                            .getDocumentiProcessoConservList().getTable().getCurrentRow();
                    loadDettaglioDocumentoProcessoConserv(currentRow.getIdDocProcessoConserv());
                }
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getMessage());
        }
    }

    /**
     * Metodo richiamato dal tasto "annulla" nella NavBar
     *
     * @throws EMFError errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {

        String publisher = getLastPublisher();

        /*
         * La procedura esce dalla funzione di modifica/inserimento? tolgo l'asterisco dalle
         * descrizioni dei campi
         */
        String cdDescription = getForm().getCategorieEnti().getCd_categ_ente().getDescription();
        String dsDescription = getForm().getCategorieEnti().getDs_categ_ente().getDescription();
        cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                : cdDescription);
        dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                : dsDescription);
        getForm().getCategorieEnti().getCd_categ_ente().setDescription(cdDescription);
        getForm().getCategorieEnti().getDs_categ_ente().setDescription(dsDescription);

        cdDescription = getForm().getCategorieStrutture().getCd_categ_strut().getDescription();
        dsDescription = getForm().getCategorieStrutture().getDs_categ_strut().getDescription();
        cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                : cdDescription);
        dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                : dsDescription);
        getForm().getCategorieStrutture().getCd_categ_strut().setDescription(cdDescription);
        getForm().getCategorieStrutture().getDs_categ_strut().setDescription(dsDescription);

        if (publisher.equals(Application.Publisher.CREA_STRUTTURA)
                && getForm().getInsStruttura().getStatus().toString().equals("insert")) {
            goBackTo(Application.Publisher.STRUTTURA_RICERCA);
        } else if (publisher.equals(Application.Publisher.CREA_STRUTTURA)
                && getForm().getInsStruttura().getStatus().toString().equals("update")) {
            loadDettaglio();
        } else if (publisher.equals(Application.Publisher.TIPO_RAPPR_COMP_DETAIL)
                && getForm().getTipoRapprComp().getStatus() != null
                && getForm().getTipoRapprComp().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.CATEGORIE_STRUTTURE_DETAIL)
                && getForm().getCategorieStrutture().getStatus() != null
                && getForm().getCategorieStrutture().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.CATEGORIE_ENTI_DETAIL)
                && getForm().getCategorieEnti().getStatus() != null
                && getForm().getCategorieEnti().getStatus().toString().equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP)
                && getForm().getTipoCompAmmessoDaTipoRapprComp().getStatus() != null
                && getForm().getTipoCompAmmessoDaTipoRapprComp().getStatus().toString()
                        .equals("insert")) {
            goBack();
        } else if (publisher.equals(Application.Publisher.PARAMETRI_STRUTTURA)
                && getForm().getInsStruttura().getStatus() != null) {
            ricercaParametriStrutturaButton();
            getForm().getInsStruttura().setStatus(Status.view);
            setViewModeListeParametri();
            forwardToPublisher(publisher);
        } else {
            loadDettaglio();
        }
    }

    /**
     * Metodo richiamato dal tasto "Salva" della NavBar
     *
     * @throws EMFError errore generico
     */
    @Override
    public void saveDettaglio() throws EMFError {
        String cdDescription;
        String dsDescription;
        String publisher = getLastPublisher();
        StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
        switch (publisher) {
        case Application.Publisher.CREA_STRUTTURA:
            TipoSalvataggio tipoSalvataggio = (TipoSalvataggio) getSession()
                    .getAttribute(SES_ATTRIB_SALVATAGGIO);
            switch (tipoSalvataggio) {
            case STRUTTURA:
                salvaStruttura();
                break;
            case DUPLICA_NON_STANDARD:
                salvaStrutturaGestioneNonStandard(Application.Publisher.DUPLICA_STRUTTURA,
                        SpagoliteLogUtil.getButtonActionName(form, form.getCheckDuplicaStruttura(),
                                form.getCheckDuplicaStruttura().getConfermaSceltaDup().getName()),
                        TipoSalvataggio.DUPLICA_NON_STANDARD);
                break;
            case IMPORTA_NON_STANDARD:
                salvaStrutturaGestioneNonStandard(Application.Publisher.IMPORTA_STRUTTURA,
                        SpagoliteLogUtil.getButtonActionName(form, form.getInsStruttura(),
                                form.getInsStruttura().getConfermaImportaStruttura().getName()),
                        TipoSalvataggio.IMPORTA_NON_STANDARD);
                break;
            case DUPLICA_STANDARD:
            case IMPORTA_STANDARD:
                salvaStrutturaGestioneStandard();
                break;
            }
            break;
        case Application.Publisher.TIPO_RAPPR_COMP_DETAIL:
            salvaTipoRapprComp();
            break;
        case Application.Publisher.CATEGORIE_STRUTTURE_DETAIL:
            salvaCategStrut();
            cdDescription = getForm().getCategorieStrutture().getCd_categ_strut().getDescription();
            dsDescription = getForm().getCategorieStrutture().getDs_categ_strut().getDescription();
            cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                    : cdDescription);
            dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                    : dsDescription);
            getForm().getCategorieStrutture().getCd_categ_strut().setDescription(cdDescription);
            getForm().getCategorieStrutture().getDs_categ_strut().setDescription(dsDescription);
            break;
        case Application.Publisher.CATEGORIE_ENTI_DETAIL:
            salvaCategEnti();
            cdDescription = getForm().getCategorieEnti().getCd_categ_ente().getDescription();
            dsDescription = getForm().getCategorieEnti().getDs_categ_ente().getDescription();
            cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                    : cdDescription);
            dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                    : dsDescription);
            getForm().getCategorieEnti().getCd_categ_ente().setDescription(cdDescription);
            getForm().getCategorieEnti().getDs_categ_ente().setDescription(dsDescription);
            break;
        case Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP:
            salvaTipoCompAmmesso();
            break;
        case Application.Publisher.XSD_MIGR_STRUT:
            salvaUsoSistemaMigraz();
            break;
        case Application.Publisher.PARAMETRI_STRUTTURA:
            salvaParametriStruttura();
            break;
        }
    }

    /**
     * Metodo per il salvataggio e la modifica di un'entitÃ  "Struttura"
     *
     * @throws EMFError errore generico
     */
    private void salvaStruttura() throws EMFError {
        getMessageBox().clear();

        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
        InsStruttura struttura = getForm().getInsStruttura();
        struttura.post(getRequest());

        if (struttura.validate(getMessageBox())) {

            // Controllo nome struttura
            if (struttura.getNm_strut().parse() == null) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_STRUTTURA_ASSENTE);
            } else {
                // Controllo che il nome struttura rispetti
                Matcher m = strutPattern.matcher(struttura.getNm_strut().parse());
                if (!m.matches()) {
                    getMessageBox().addError(
                            "Errore di compilazione form: Nome struttura contenente caratteri non permessi");
                }
            }

            if (struttura.getDs_strut().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: Descrizione struttura non inserito</br>");
            }

            // Controllo su selezione ente
            if (getForm().getInsStruttura().getId_ente_rif().parse() == null) {
                getMessageBox().addError(ERRORE_SELEZIONE_ENTE_RIFERIMENTO);
            }

            // Controllo sulle date di validità della struttura
            if (struttura.getDt_ini_val_strut().parse()
                    .after(struttura.getDt_fine_val_strut().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: la data di inizio validità  struttura è successiva a quella di fine validità</br>");
            }

            if (!struttura.getStatus().equals(Status.update)) {
                if ((struttura.getId_ambiente_ente_convenz().parse() == null)) {
                    getMessageBox().addError(ERRORE_COMPILAZIONE_AMBIENTE_ASSENTE);
                }
                if ((struttura.getId_ente_convenz().parse() == null)) {
                    getMessageBox().addError(ERRORE_COMPILAZIONE_ENTE_CONVENZIONATO_ASSENTE);
                }
                if ((struttura.getDt_ini_val().parse() == null)) {
                    getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_ASSENTE);
                }
                if ((struttura.getDt_fine_val().parse() == null)) {
                    getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_FINE_VALIDITA_ASSENTE);
                }

                if (struttura.getDt_fine_val().parse() != null
                        && struttura.getDt_ini_val().parse() != null) {
                    // Controllo sulle date dell'ente convenzionato
                    if (struttura.getDt_ini_val().parse()
                            .after(struttura.getDt_fine_val().parse())) {
                        getMessageBox()
                                .addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_SUCCESSIVA_FINE);
                    }
                }
            }

            if (struttura.getFl_archivio_restituito().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: Archivio restituito non inserito</br>");
            }

            struttura.copyToBean(strutRowBean);

            // Ciclo per inserire nel rowbean i flag
            Map<String, String[]> map = getRequest().getParameterMap();
            Iterator<Entry<String, String[]>> iterator = map.entrySet().iterator();
            boolean isModifica = struttura.getStatus().equals(Status.update);
            if (isModifica) {
                while (iterator.hasNext()) {
                    Entry<String, String[]> next = iterator.next();
                    String key = next.getKey();
                    if (key.contains("Fl_") && !"fl_template".equalsIgnoreCase(key)
                            && !"fl_archivio_restituito".equalsIgnoreCase(key)
                            && !"fl_cessato".equalsIgnoreCase(key) && !key.contains("trigger")) {
                        ((CheckBox<String>) struttura.getComponent(key)).setChecked(true);
                        strutRowBean.setObject(key, "1");
                    }
                }
            } else {
                while (iterator.hasNext()) {
                    Entry<String, String[]> next = iterator.next();
                    String key = next.getKey();
                    if (key.contains("Fl_") && !"fl_archivio_restituito".equalsIgnoreCase(key)
                            && !"fl_cessato".equalsIgnoreCase(key) && !key.contains("trigger")) {
                        ((CheckBox<String>) struttura.getComponent(key)).setChecked(true);
                        strutRowBean.setObject(key, "1");
                    }
                }
            }
            BigDecimal idEnte = isModifica
                    ? ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                            .getBigDecimal("id_ente")
                    : struttura.getId_ente_rif().parse();
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(idEnte);

            // Controllo caratteristica "Template"
            if ("1".equals(strutRowBean.getFlTemplate()) && (ente.getTipoDefTemplateEnte()
                    .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))) {
                getMessageBox().addError(
                        "La struttura è di tipo template e deve appartenere ad un ente di tipo template</br>");
            }

            if (!"1".equals(strutRowBean.getFlTemplate()) && (ente.getTipoDefTemplateEnte()
                    .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE.name()))) {
                getMessageBox().addError(
                        "La struttura non è di tipo template e deve appartenere ad un ente non template o ad un ente con strutture template definite specificatamente per l'ente</br>");
            }

            // Controllo valorizzazione parametri fascicolo in caso di flag gestione fascicoli
            // settato
            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic().equals("FL_GEST_FASCICOLI")) {
                    if ("1".equals(row.getString("ds_valore_param_applic_strut_cons"))) {
                        getMessageBox().addError(
                                "Dal momento che si intende gestire i fascicoli è necessario inserire dei valori nei parametri relativi</br>");
                    }
                }
            }

            /////////////////
            // SALVATAGGIO //
            /////////////////
            if (getMessageBox().isEmpty()) {
                try {
                    // Esegue controllo sul numero componenti struttura e procede con il salvataggio
                    // Codice aggiutnivo per il logging
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(
                                    CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    checkNumCompAndSalvaStruttura(param, struttura, strutRowBean,
                            TipoSalvataggio.STRUTTURA, null, null, null);
                } catch (EMFError e) {
                    getSession().setAttribute("id_struttura_lavorato", null);
                    getMessageBox().addError(e.getMessage());
                } catch (ParerUserError ne) {
                    getSession().setAttribute("id_struttura_lavorato", null);
                    getMessageBox().addError(ne.getDescription());
                }
            }
        }
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    /**
     * Esegue il controllo sul campo "Numero massimo componenti" prima di consentire la prosecuzione
     * della procedura di salvataggio, switchando tra i vari tipo di salvataggio.
     *
     * @param struttura
     * @param strutRowBean
     * @param tipoSalvataggio
     *
     * @throws EMFError             errore generico
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws ParerUserError
     */
    private void checkNumCompAndSalvaStruttura(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean, TipoSalvataggio tipoSalvataggio,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura,
            AplParamApplicTableBean parametriGestioneStruttura) throws EMFError, ParerUserError {
        switchTipoSalvataggio(param, struttura, strutRowBean, tipoSalvataggio,
                parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                parametriGestioneStruttura);
    }

    @Override
    public void confermaSalvataggioStruttura() {
        try {
            if (getSession().getAttribute("salvataggioAttributes") != null) {
                TipoSalvataggio tipoSalvataggio = (TipoSalvataggio) getSession()
                        .getAttribute(SES_ATTRIB_SALVATAGGIO);
                Object[] sa = (Object[]) getSession().getAttribute("salvataggioAttributes");
                /*
                 * Codice aggiuntivo per il logging...
                 */
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                String determinaPublisher = null;
                String azione = null;
                switch (tipoSalvataggio) {
                case STRUTTURA:
                    determinaPublisher = SpagoliteLogUtil.getPageName(this);
                    azione = null; // DA DEFINIRE da dove puo' arrivare
                    break;

                case DUPLICA_NON_STANDARD:
                case DUPLICA_STANDARD:
                    determinaPublisher = Application.Publisher.DUPLICA_STRUTTURA;
                    azione = SpagoliteLogUtil.getButtonActionName(form,
                            form.getCheckDuplicaStruttura(),
                            form.getCheckDuplicaStruttura().getConfermaSceltaDup().getName());
                    break;

                case IMPORTA_STANDARD:
                case IMPORTA_NON_STANDARD:
                    determinaPublisher = Application.Publisher.IMPORTA_STRUTTURA;
                    azione = SpagoliteLogUtil.getButtonActionName(form, form.getInsStruttura(),
                            form.getInsStruttura().getConfermaImportaStruttura().getName());
                    break;
                }

                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), determinaPublisher, azione);
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                switchTipoSalvataggio(param, (InsStruttura) sa[0], (OrgStrutRowBean) sa[1],
                        tipoSalvataggio, (AplParamApplicTableBean) sa[2],
                        (AplParamApplicTableBean) sa[3], (AplParamApplicTableBean) sa[4]);
            }
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            postLoad();
        } catch (ParerUserError e) {
            String[] errorParts = e.getDescription().split(";");
            customizeErrorMessage(errorParts);
            forwardToPublisher(getLastPublisher());
        } catch (EMFError ex) {
            getMessageBox().addError(ex.getMessage());
            forwardToPublisher(getLastPublisher());
        }
    }

    private void customizeErrorMessage(String[] errorParts) {
        int startIndex = 0;
        if (errorParts[0].equals("TIPISERIE")) {
            startIndex = 1;
            getMessageBox().setViewMode(ViewMode.plain);
            getMessageBox().addWarning("Operazione eseguita con successo. Attenzione: <br>");
        } else {
            getMessageBox().addWarning("Attenzione: <br>");
            getMessageBox().setViewMode(ViewMode.alert);
        }
        for (int i = startIndex; i < errorParts.length; i++) {
            getMessageBox().addWarning(errorParts[i] + "<br>");
        }
    }

    @Override
    public void annullaSalvataggioStruttura() {
        // Nascondo i bottoni con javascript disattivato
        getForm().getSalvaStrutturaCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("salvataggioAttributes");
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    private void switchTipoSalvataggio(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean, TipoSalvataggio tipoSalvataggio,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura,
            AplParamApplicTableBean parametriGestioneStruttura) throws EMFError, ParerUserError {
        switch (tipoSalvataggio) {
        case STRUTTURA:
            // logging non implementato
            eseguiSalvataggioStruttura(param, struttura, strutRowBean);
            break;
        case DUPLICA_NON_STANDARD:
            eseguiDuplicaNonStandard(param, struttura, strutRowBean,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                    parametriGestioneStruttura);
            break;
        case DUPLICA_STANDARD:
        case IMPORTA_STANDARD:
            eseguiDuplicaImportaStandard(param, struttura, strutRowBean, tipoSalvataggio,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                    parametriGestioneStruttura);
            break;
        case IMPORTA_NON_STANDARD:
            eseguiImportaNonStandard(param, struttura, strutRowBean,
                    parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                    parametriGestioneStruttura);
            break;
        }
    }

    private void eseguiSalvataggioStruttura(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean) throws ParerUserError, EMFError {
        if (struttura.getStatus().equals(Status.update)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");
            OrgStrutRowBean beanFromDb = struttureEjb.getOrgStrutRowBean(idStrut, null);
            OrgEnteRowBean ente = struttureEjb
                    .getOrgEnteRowBean(struttura.getId_ente_rif().parse());
            OrgAmbienteRowBean ambiente = struttureEjb
                    .getOrgAmbienteRowBean(struttura.getId_ambiente_rif().parse());
            strutRowBean.setIdEnte(struttura.getId_ente_rif().parse());
            strutRowBean.setString("view_nm_ente", ente.getNmEnte());
            strutRowBean.setString("view_nm_amb", ambiente.getNmAmbiente());
            strutRowBean.setIdEnteConvenz(beanFromDb.getIdEnteConvenz());
            strutRowBean.setDtIniVal(beanFromDb.getDtIniVal());
            strutRowBean.setDtFineVal(beanFromDb.getDtFineVal());
            struttureEjb.updateOrgStruttura(param, idStrut, strutRowBean);
            loadlists(strutRowBean.getIdStrut(), true);
            // Workaround per evitare che si cancellino le checkbox visualizzate
            getForm().getInsStruttura().copyFromBean(strutRowBean);
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Update della struttura effettuato con successo"));
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            strutRowBean.setIdEnte(struttura.getId_ente_rif().parse());
            struttureEjb.insertOrgStruttura(param, strutRowBean, false);
            OrgStrutTableBean orgStrutTableBean = new OrgStrutTableBean();
            orgStrutTableBean.add(strutRowBean);
            getForm().getStruttureList().setTable(orgStrutTableBean);
            getForm().getStruttureList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getStruttureList().getTable().setCurrentRowIndex(0);
            loadlists(strutRowBean.getIdStrut(), true);
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Inserimento struttura effettuato con successo"));
        }
        setDettaglioStrutturaViewMode();
    }

    private void eseguiDuplicaNonStandard(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura,
            AplParamApplicTableBean parametriGestioneStruttura) throws EMFError, ParerUserError {
        if (struttura.getStatus().equals(Status.insert)) {
            strutRowBean.setIdEnte(struttura.getId_ente_rif().parse());
            try {
                SalvaStrutturaDto salva = (SalvaStrutturaDto) getSession()
                        .getAttribute(SES_ATTRIB_VALORI_SALVATAGGIO);
                CopiaStruttureEjb.OrgStrutCopyResult result = struttureEjb.copyOrgStrutRowBean(
                        param, strutRowBean,
                        ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                                .getBigDecimal("id_strut"),
                        salva, parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                        parametriGestioneStruttura);
                gestisciMessaggiInfo(result.getMex());
            } // Se checcio una ParerUserError in ogni caso ricarico il dettaglio
              // per altre eventuali eccezioni invece non deve ricaricare il dettaglio
            catch (ParerUserError e) {
                if (strutRowBean.getIdStrut() != null) {
                    reloadDettaglioPostDuplicaNonStandard(strutRowBean);
                }
                throw e;
            }
            reloadDettaglioPostDuplicaNonStandard(strutRowBean);
            getMessageBox().addInfo("Struttura duplicata con successo!");
        }
        setDettaglioStrutturaViewMode();
    }

    private void eseguiImportaNonStandard(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean, AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura,
            AplParamApplicTableBean parametriGestioneStruttura) throws EMFError, ParerUserError {
        if (getForm().getInsStruttura().getStatus().equals(Status.insert)) {
            try {
                strutRowBean.setIdEnte(struttura.getId_ente_rif().parse());
                SalvaStrutturaDto salva = (SalvaStrutturaDto) getSession()
                        .getAttribute(SES_ATTRIB_VALORI_SALVATAGGIO);
                CopiaStruttureEjb.OrgStrutCopyResult result = struttureEjb.insertOrgStrutImp(param,
                        strutRowBean, UUID.fromString(getSession().getAttribute("uuid").toString()),
                        salva, parametriAmministrazioneStruttura, parametriConservazioneStruttura,
                        parametriGestioneStruttura);
                gestisciMessaggiInfo(result.getMex());
            } catch (ParerUserError e) {
                reloadDettaglioPostImportaNonStandard(struttura, strutRowBean);
                throw e;
            }
            reloadDettaglioPostImportaNonStandard(struttura, strutRowBean);
            getMessageBox().addInfo("Struttura importata con successo!");
        }
        setDettaglioStrutturaViewMode();
    }

    private void reloadDettaglioPostDuplicaNonStandard(OrgStrutRowBean strutRowBean)
            throws EMFError {
        strutRowBean = struttureEjb.getOrgStrutRowBean(strutRowBean.getIdStrut(),
                strutRowBean.getIdEnte());
        OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        OrgAmbienteRowBean ambienteTemp = struttureEjb
                .getOrgAmbienteRowBean(enteTemp.getIdAmbiente());
        strutRowBean.setString("nm_ente",
                enteTemp.getNmEnte() + " - " + (ambienteTemp.getNmAmbiente()));
        OrgStrutTableBean orgStrutTableBean = new OrgStrutTableBean();
        orgStrutTableBean.add(strutRowBean);
        getForm().getStruttureList().setTable(orgStrutTableBean);
        getForm().getStruttureList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStruttureList().getTable().setCurrentRowIndex(0);
        loadlists(strutRowBean.getIdStrut(), true);
        setDettaglioStrutturaViewMode();
    }

    private void eseguiDuplicaImportaStandard(LogParam param, InsStruttura struttura,
            OrgStrutRowBean strutRowBean, TipoSalvataggio tipoSalvataggio,
            AplParamApplicTableBean parametriAmministrazioneStruttura,
            AplParamApplicTableBean parametriConservazioneStruttura,
            AplParamApplicTableBean parametriGestioneStruttura) throws EMFError, ParerUserError {
        boolean isFromImporta = tipoSalvataggio == TipoSalvataggio.IMPORTA_STANDARD;
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (getForm().getInsStruttura().getStatus().equals(Status.insert)) {
            try {
                SalvaStrutturaDto salva = (SalvaStrutturaDto) getSession()
                        .getAttribute(SES_ATTRIB_VALORI_SALVATAGGIO);
                if (isFromImporta) {
                    param.setNomePagina(Application.Publisher.IMPORTA_STRUTTURA);
                    StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                    param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(form,
                            form.getInsStruttura(),
                            form.getInsStruttura().getConfermaImportaStruttura().getName()));
                    strutRowBean.setFlTemplate("0");
                    if (getForm().getInsStruttura().getStatus().equals(Status.insert)) {
                        strutRowBean.setIdEnte(struttura.getId_ente_rif().parse());
                        CopiaStruttureEjb.OrgStrutCopyResult result = struttureEjb
                                .overwriteOrgStrutImp(param, strutRowBean,
                                        UUID.fromString(
                                                getSession().getAttribute("uuid").toString()),
                                        salva, parametriAmministrazioneStruttura,
                                        parametriConservazioneStruttura,
                                        parametriGestioneStruttura);
                        gestisciMessaggiInfo(result.getMex());
                        strutRowBean = struttureEjb.getOrgStrutRowBean(strutRowBean.getIdStrut(),
                                strutRowBean.getIdEnte());
                        OrgEnteRowBean enteTemp = struttureEjb
                                .getOrgEnteRowBean(strutRowBean.getIdEnte());
                        OrgAmbienteRowBean ambienteTemp = struttureEjb
                                .getOrgAmbienteRowBean(enteTemp.getIdAmbiente());
                        strutRowBean.setString("nm_ente",
                                enteTemp.getNmEnte() + " - " + (ambienteTemp.getNmAmbiente()));
                        getSession().removeAttribute("daImporta");
                        getMessageBox().addInfo("Struttura importata con successo!");
                    }
                } else {
                    param.setNomePagina(Application.Publisher.DUPLICA_STRUTTURA);
                    StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                    param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(form,
                            form.getCheckDuplicaStruttura(),
                            form.getCheckDuplicaStruttura().getConfermaSceltaDup().getName()));
                    BigDecimal idStrut = (BigDecimal) getSession().getAttribute("idStrutToCopy");
                    CopiaStruttureEjb.OrgStrutCopyResult result = struttureEjb.overwriteStrut(param,
                            idStrut, strutRowBean, salva, parametriAmministrazioneStruttura,
                            parametriConservazioneStruttura, parametriGestioneStruttura);
                    gestisciMessaggiInfo(result.getMex());
                    getMessageBox().addMessage(
                            new Message(MessageLevel.INF, "Nuova struttura salvata con successo"));
                }
                reloadDettaglioPostDuplicaImportaStandard(strutRowBean);
            } // Se checcio una ParerUserError in ogni caso ricarico il dettaglio
              // per altre eventuali eccezioni invece non deve ricaricare il dettaglio
            catch (ParerUserError e) {
                // Se è presente l'id strut, significa che l'importa/duplica è andato a buon fine e
                // quindi l'errore
                // riguarda la parte relativa ai tipi serie
                if (strutRowBean.getIdStrut() != null) {
                    setDettaglioStrutturaViewMode();
                }
                throw e;
            }
        }
        setDettaglioStrutturaViewMode();
    }

    private void reloadDettaglioPostDuplicaImportaStandard(OrgStrutRowBean strutRowBean)
            throws EMFError, ParerUserError {
        OrgStrutTableBean orgStrutTableBean = new OrgStrutTableBean();
        orgStrutTableBean.add(strutRowBean);
        getForm().getStruttureList().setTable(orgStrutTableBean);
        getForm().getStruttureList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStruttureList().getTable().setCurrentRowIndex(0);
        loadStruttura(strutRowBean.getIdStrut());
        loadlists(strutRowBean.getIdStrut(), true);
        setDettaglioStrutturaViewMode();
    }

    private void reloadDettaglioPostImportaNonStandard(InsStruttura struttura,
            OrgStrutRowBean strutRowBean) throws EMFError, ParerUserError {
        if (strutRowBean != null && strutRowBean.getIdStrut() != null) {
            strutRowBean = struttureEjb.getOrgStrutRowBean(strutRowBean.getIdStrut(),
                    strutRowBean.getIdEnte());
            OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
            OrgAmbienteRowBean ambienteTemp = struttureEjb
                    .getOrgAmbienteRowBean(enteTemp.getIdAmbiente());
            strutRowBean.setString("nm_ente",
                    enteTemp.getNmEnte() + " - " + (ambienteTemp.getNmAmbiente()));
            getForm().getInsStruttura().getNm_strut().setHidden(true);
            getForm().getInsStruttura().getDs_strut().setHidden(true);
            getForm().getInsStruttura().getId_ambiente_rif().setHidden(true);
            getForm().getInsStruttura().getId_ente_rif().setHidden(true);
            getForm().getInsStruttura().getView_nm_strut().setHidden(false);
            getForm().getInsStruttura().getView_nm_ente().setHidden(false);
            getForm().getInsStruttura().getView_nm_amb().setHidden(false);
            getForm().getInsStruttura().getView_nm_strut()
                    .setValue(strutRowBean.getNmStrut() + " (" + strutRowBean.getDsStrut() + ")");
            struttura.getView_nm_amb().setValue(ambienteTemp.getNmAmbiente());
            struttura.getView_nm_ente().setValue(enteTemp.getNmEnte());
            OrgStrutTableBean orgStrutTableBean = new OrgStrutTableBean();
            orgStrutTableBean.add(strutRowBean);
            getForm().getStruttureList().setTable(orgStrutTableBean);
            getForm().getStruttureList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getStruttureList().getTable().setCurrentRowIndex(0);
            loadStruttura(strutRowBean.getIdStrut());
            loadlists(strutRowBean.getIdStrut(), true);
        }
    }

    private void setDettaglioStrutturaViewMode() throws EMFError {
        getForm().getInsStruttura().setViewMode();
        getForm().getStruttureList().setStatus(Status.view);
        getForm().getInsStruttura().setStatus(Status.view);
        getForm().getVisStrutture().clear();
        getForm().getParametriAmministrazioneStrutturaList().getDs_valore_param_applic_strut_amm()
                .setViewMode();
        getForm().getParametriGestioneStrutturaList().setHideDeleteButton(false);
        getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                .setViewMode();
        getForm().getParametriConservazioneStrutturaList().setHideDeleteButton(false);
        getForm().getParametriConservazioneStrutturaList().getDs_valore_param_applic_strut_cons()
                .setViewMode();
        visualizzaBottoniListeDettaglioStruttura();
        getMessageBox().setViewMode(ViewMode.plain);
    }

    private void salvaStrutturaGestioneNonStandard(String publisherPerLogging,
            String buttonActionNamePerLogging, TipoSalvataggio tipoSalvataggio) throws EMFError {
        getMessageBox().clear();
        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
        InsStruttura struttura = getForm().getInsStruttura();
        struttura.post(getRequest());
        if (struttura.getNm_strut().parse() == null) {
            getMessageBox().addError(ERRORE_COMPILAZIONE_STRUTTURA_ASSENTE);
        }

        if (struttura.getDs_strut().parse() == null) {
            getMessageBox().addError(
                    "Errore di compilazione form: Descrizione struttura non inserito</br>");
        }

        // controllo su selezione ente
        if (getForm().getInsStruttura().getId_ente_rif().parse() == null
                && getForm().getStruttureList().getStatus().equals(Status.insert)) {
            getMessageBox().addError(ERRORE_SELEZIONE_ENTE_RIFERIMENTO);
        }
        if ((struttura.getId_ambiente_ente_convenz().parse() == null)) {
            getMessageBox().addError(ERRORE_COMPILAZIONE_AMBIENTE_ASSENTE);
        }
        if ((struttura.getId_ente_convenz().parse() == null)) {
            getMessageBox().addError(ERRORE_COMPILAZIONE_ENTE_CONVENZIONATO_ASSENTE);
        }
        if ((struttura.getDt_ini_val().parse() == null)) {
            getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_ASSENTE);
        }
        if ((struttura.getDt_fine_val().parse() == null)) {
            getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_FINE_VALIDITA_ASSENTE);
        }

        if (struttura.getDt_fine_val().parse() != null
                && struttura.getDt_ini_val().parse() == null) {
            // Controllo sulle date dell'ente convenzionato
            if (struttura.getDt_ini_val().parse().after(struttura.getDt_fine_val().parse())) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_SUCCESSIVA_FINE);
            }
        }
        // Controllo valorizzazione parametri fascicolo in caso di flag gestione fascicoli settato
        if (getForm().getParametriConservazioneStrutturaList().getTable() != null) {
            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic().equals("FL_GEST_FASCICOLI")) {
                    if (row.getString("ds_valore_param_applic_strut_cons").equals("1")) {
                        getMessageBox().addError(
                                "Dal momento che si intende gestire i fascicoli è necessario inserire dei valori nei parametri relativi</br>");
                    }
                }
            }
        }

        // Controllo valori possibili su struttura
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm()
                .getParametriGestioneStrutturaList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("strut", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (getMessageBox().isEmpty()) {
            struttura.copyToBean(strutRowBean);
            try {
                /* Codice aggiuntivo per il logging... */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), publisherPerLogging, buttonActionNamePerLogging);
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                checkNumCompAndSalvaStruttura(param, struttura, strutRowBean, tipoSalvataggio,
                        (AplParamApplicTableBean) getForm()
                                .getParametriAmministrazioneStrutturaList().getTable(),
                        (AplParamApplicTableBean) getForm().getParametriConservazioneStrutturaList()
                                .getTable(),
                        (AplParamApplicTableBean) getForm().getParametriGestioneStrutturaList()
                                .getTable());
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } catch (ParerUserError e) {
                String[] errorParts = e.getDescription().split(";");
                customizeErrorMessage(errorParts);
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } catch (Exception e) {
                getMessageBox()
                        .addError("Errore inaspettato: " + ExceptionUtils.getRootCauseMessage(e));
                forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }

    }

    private void salvaStrutturaGestioneStandard() throws EMFError {
        getMessageBox().clear();
        String daImporta = (String) getSession().getAttribute("daImporta");
        boolean isFromImporta = "Si".equalsIgnoreCase(daImporta);
        String toPublisher = getLastPublisher();
        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
        InsStruttura struttura = getForm().getInsStruttura();
        // Post dei valori a video e popolamento rowBean contenente i campi di struttura e parametri
        // fascicolo
        struttura.post(getRequest());

        struttura.copyToBean(strutRowBean);

        // Controllo completezza dati
        if (struttura.validate(getMessageBox())) {

            if (getForm().getInsStruttura().getId_ente_rif().parse() == null) {
                getMessageBox().addError(ERRORE_SELEZIONE_ENTE_RIFERIMENTO);
            }

            if (struttura.getNm_strut().parse() == null) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_STRUTTURA_ASSENTE);
            }

            if (struttura.getDs_strut().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: Descrizione struttura non inserito</br>");
            }
            if ((struttura.getId_ambiente_ente_convenz().parse() == null)) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_AMBIENTE_ASSENTE);
            }
            if ((struttura.getId_ente_convenz().parse() == null)) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_ENTE_CONVENZIONATO_ASSENTE);
            }
            if ((struttura.getDt_ini_val().parse() == null)) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_ASSENTE);
            }
            if ((struttura.getDt_fine_val().parse() == null)) {
                getMessageBox().addError(ERRORE_COMPILAZIONE_DATA_FINE_VALIDITA_ASSENTE);
            }
            if (struttura.getDt_fine_val().parse() != null
                    && struttura.getDt_ini_val().parse() == null) {
                // Controllo sulle date dell'ente convenzionato
                if (struttura.getDt_ini_val().parse().after(struttura.getDt_fine_val().parse())) {
                    getMessageBox()
                            .addError(ERRORE_COMPILAZIONE_DATA_INIZIO_VALIDITA_SUCCESSIVA_FINE);
                }
            }
        }

        // Controllo valori possibili su struttura
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm()
                .getParametriGestioneStrutturaList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("strut", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        /////////////////
        // SALVATAGGIO //
        /////////////////
        if (getMessageBox().isEmpty()) {
            try {
                LogParam param = SpagoliteLogUtil
                        .getLogParam(
                                configurationHelper.getValoreParamApplicByApplic(
                                        CostantiDB.ParametroAppl.NM_APPLIC),
                                getUser().getUsername());
                TipoSalvataggio tipoSalvataggio = isFromImporta ? TipoSalvataggio.IMPORTA_STANDARD
                        : TipoSalvataggio.DUPLICA_STANDARD;
                checkNumCompAndSalvaStruttura(param, struttura, strutRowBean, tipoSalvataggio,
                        (AplParamApplicTableBean) getForm()
                                .getParametriAmministrazioneStrutturaList().getTable(),
                        (AplParamApplicTableBean) getForm().getParametriConservazioneStrutturaList()
                                .getTable(),
                        (AplParamApplicTableBean) getForm().getParametriGestioneStrutturaList()
                                .getTable());
                forwardToPublisher(toPublisher);
            } catch (ParerUserError e) {
                String[] errorParts = e.getDescription().split(";");
                customizeErrorMessage(errorParts);
                forwardToPublisher(toPublisher);
            }
        } else {
            forwardToPublisher(toPublisher);
        }

    }

    /**
     * Metodo per il salvataggio o la modifica di un'entitÃ  DecTipoRapprComp
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoRapprComp() throws EMFError {
        getMessageBox().clear();

        DecTipoRapprCompRowBean tipoRapprCompRowBean = new DecTipoRapprCompRowBean();

        TipoRapprComp tipoRapprComp = getForm().getTipoRapprComp();
        tipoRapprComp.post(getRequest());

        if (tipoRapprComp.validate(getMessageBox())) {

            if (StringUtils.isBlank(tipoRapprComp.getNm_tipo_rappr_comp().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: tipo rappresentazione componente non inserito</br>");
            }
            if (StringUtils.isBlank(tipoRapprComp.getDs_tipo_rappr_comp().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: descrizione rappresentazione componente non inserita</br>");
            }
            if (tipoRapprComp.getDt_istituz().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: data di attivazione non inserita</br>");
            }
            if (tipoRapprComp.getId_formato_contenuto().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: formato del file contenuto non inserito</br>");
            }
            if (StringUtils.isBlank(tipoRapprComp.getTi_algo_rappr().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: algoritmo di rappresentazione non inserito</br>");
            }
            if (StringUtils.isBlank(tipoRapprComp.getTi_output_rappr().parse())) {
                getMessageBox()
                        .addError("Errore di compilazione form: tipo di output non inserito</br>");
            }
            if (tipoRapprComp.getId_formato_output_rappr().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione form: formato di output non inserito</br>");
            }
            if (tipoRapprComp.getDt_soppres().parse() == null) {
                tipoRapprComp.getDt_soppres().setValue(getDefaultDate());
            }
            if (tipoRapprComp.getDt_istituz().parse() != null
                    && tipoRapprComp.getDt_soppres().parse() != null && tipoRapprComp
                            .getDt_istituz().parse().after(tipoRapprComp.getDt_soppres().parse())) {
                getMessageBox().addError(
                        "Errore di compilazione form: data disattivazione precedente a data attivazione</br>");
            }
            try {
                if (getMessageBox().isEmpty()) {
                    tipoRapprComp.copyToBean(tipoRapprCompRowBean);

                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    tipoRapprCompRowBean.setIdStrut(idStrut);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(
                                    CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getTipoRapprComp().getStatus().equals(Status.insert)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        // VEDERE SE NON HO SBAGLIATO A TOGLIERE idTipoStrutDoc
                        tipoRapprEjb.insertDecTipoRapprComp(param, tipoRapprCompRowBean);
                        tipoRapprCompRowBean = tipoRapprEjb.getDecTipoRapprCompRowBean(
                                tipoRapprCompRowBean.getNmTipoRapprComp(), idStrut);
                        DecTipoRapprCompTableBean trcTable = new DecTipoRapprCompTableBean();
                        trcTable.add(tipoRapprCompRowBean);
                        getForm().getTipoRapprCompList().setTable(trcTable);
                        getForm().getTipoRapprCompList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getTipoRapprCompList().getTable().setCurrentRowIndex(0);

                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Nuovo Tipo Rappresentazione salvata con successo"));
                    } else if (getForm().getTipoRapprComp().getStatus().equals(Status.update)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        BigDecimal idTipoRapprComp = ((DecTipoRapprCompRowBean) getForm()
                                .getTipoRapprCompList().getTable().getCurrentRow())
                                .getIdTipoRapprComp();
                        // VEDERE SE NON HO SBAGLIATO A TOGLIERE idTipoStrutDoc
                        tipoRapprEjb.updateDecTipoRapprComp(param, idTipoRapprComp,
                                tipoRapprCompRowBean);
                        tipoRapprCompRowBean.setIdTipoRapprComp(idTipoRapprComp);
                        getForm().getTipoRapprCompList().getTable().setCurrentRowIndex(
                                getForm().getTipoRapprCompList().getTable().getCurrentRowIndex());
                    }
                    getForm().getTipoRapprComp().setViewMode();
                    getForm().getTipoRapprComp().setStatus(Status.view);
                    getForm().getTipoRapprCompList().setStatus(Status.view);
                    getMessageBox().setViewMode(ViewMode.plain);
                    reloadTipoRapprCompLists(tipoRapprCompRowBean.getIdTipoRapprComp());
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.TIPO_RAPPR_COMP_DETAIL);
    }

    private void salvaCategStrut() throws EMFError {
        getMessageBox().clear();
        getForm().getCategorieStrutture().post(getRequest());
        if (getForm().getCategorieStrutture().validate(getMessageBox())) {
            if (getForm().getCategorieStrutture().getCd_categ_strut().parse() == null) {
                getMessageBox().addError("Errore di compilazione: inserire codice categoria </br>");
            }
            if (getForm().getCategorieStrutture().getDs_categ_strut().parse() == null) {
                getMessageBox()
                        .addError("Errore di compilazione: inserire descrizione categoria </br>");
            }

            try {
                if (getMessageBox().isEmpty()) {

                    OrgCategStrutRowBean categStrutRowBean = new OrgCategStrutRowBean();
                    getForm().getCategorieStrutture().copyToBean(categStrutRowBean);

                    if (getForm().getCategorieStrutture().getStatus().equals(Status.insert)) {

                        struttureEjb.insertOrgCategStrut(categStrutRowBean);
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Nuova categoria strutture salvata con successo"));

                    } else if (getForm().getCategorieStrutture().getStatus()
                            .equals(Status.update)) {

                        BigDecimal idCategStrut = ((OrgCategStrutRowBean) getForm()
                                .getCategorieStruttureList().getTable().getCurrentRow())
                                .getIdCategStrut();
                        struttureEjb.updateOrgCategStrut(idCategStrut, categStrutRowBean);
                        getForm().getCategorieStruttureList().getTable()
                                .setCurrentRowIndex(getForm().getCategorieStruttureList().getTable()
                                        .getCurrentRowIndex());
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Update della categoria strutture effettuato con successo"));

                    }

                    getForm().getCategorieStrutture().setViewMode();
                    getForm().getCategorieStrutture().setStatus(Status.view);
                    getForm().getCategorieStruttureList().setStatus(Status.view);
                    getMessageBox().setViewMode(ViewMode.plain);
                }
                forwardToPublisher(Application.Publisher.CATEGORIE_STRUTTURE_DETAIL);
            } catch (ParerUserError ie) {
                getMessageBox().addError(ie.getDescription());
                forwardToPublisher(Application.Publisher.CATEGORIE_STRUTTURE_DETAIL);
            }
        }
    }

    private void salvaUsoSistemaMigraz() throws EMFError {
        getForm().getGestioneXsdMigrazione().postAndValidate(getRequest(), getMessageBox());
        if (!getMessageBox().hasError()) {
            try {
                BigDecimal idStrut = getForm().getGestioneXsdMigrazione().getId_strut().parse();
                BigDecimal idSistemaVersante = getForm().getGestioneXsdMigrazione()
                        .getNm_sistema_migraz().parse();

                sysMigrazioneEjb.insertOrgUsoSistemaMigraz(idStrut, idSistemaVersante);
                getMessageBox()
                        .addInfo("Sistema di migrazione per la struttura salvato con successo");
                getMessageBox().setViewMode(ViewMode.plain);

                getForm().getGestioneXsdMigrazione().getNm_sistema_migraz().setViewMode();
                getForm().getGestioneXsdMigrazione().setStatus(Status.view);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    private void salvaParametriStruttura() throws EMFError {
        getForm().getParametriAmministrazioneStrutturaList().post(getRequest());
        getForm().getParametriConservazioneStrutturaList().post(getRequest());
        getForm().getParametriGestioneStrutturaList().post(getRequest());
        getForm().getRicercaParametriStruttura().post(getRequest());

        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");

        List<String> funzione = getForm().getRicercaParametriStruttura().getFunzione().parse();

        // Controllo valori possibili su struttura
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneStrutturaList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm()
                .getParametriGestioneStrutturaList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("strut", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        /* Controllo possibile eliminazione parametri configurazione fascicolo */
        for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                .getParametriConservazioneStrutturaList().getTable()) {
            if (row.getNmParamApplic().equals("FL_GEST_FASCICOLI")) {
                if (!row.getString("ds_valore_param_applic_strut_cons").equals("true")) {
                    if (tipoFascicoloEjb.existsFascicoloVersatoPerStruttura(idStrut)) {
                        getMessageBox().addError(
                                "Impossibile eliminare i parametri configurazione fascicolo: esiste almeno un fascicolo versato");
                    }
                }
                break;
            }
        }

        if (!getMessageBox().hasError()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplicByApplic(
                            CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getToolbarSave(
                            getForm().getInsStruttura().getStatus().equals(Status.update)));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            struttureEjb.saveParametriStruttura(param, parametriAmministrazione,
                    parametriConservazione, parametriGestione, idStrut);
            getMessageBox().addInfo("Parametri struttura salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            ricercaParametriStrutturaButton();
            getForm().getInsStruttura().setStatus(Status.view);
            setViewModeListeParametri();
            try {
                // loadListeParametriStruttura(idStrut, null, false, false, false, false, true);
                loadListaParametriAmnministrazioneStruttura(idStrut, funzione, false, false,
                        getForm().getParametriAmministrazioneStrutturaList()
                                .isFilterValidRecords());
                loadListaParametriConservazioneStruttura(idStrut, funzione, false, false,
                        getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
                loadListaParametriGestioneStruttura(idStrut, funzione, false, false,
                        getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "Errore durante il ricaricamento dei parametri struttura a seguito del salvataggio degli stessi");
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        getSession().setAttribute("ActionMap", null);
        String lista = getTableName();
        String action = getNavigationEvent();
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && !getForm().getStruttureList().getTable().isEmpty()) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        boolean isUpdate = action.equals(NE_DETTAGLIO_INSERT);

        String cdDescription;
        String dsDescription;
        // Registro unita doc.: passa all'altra action
        if (getForm().getRegistroUnitaDocList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToRegistroPage();
        } // Tipologia unità doc: passa all'altra action
        else if (getForm().getTipoUnitaDocList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTipologiaUnitaDocPage();
        } // Tipo doc: passa all'altra action
        else if (getForm().getTipoDocList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTipoDocPage();
        } // Tipo fascicolo: passa all'altra action
        else if (getForm().getTipoFascicoloList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTipoFascicoloPage();
        } else if (getForm().getFormatoFileDocList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToFormatoFileDocPage();
        } else if (getForm().getTipoStrutDocList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTipoStrutDocPage();
        } else if (getForm().getCriteriRaggruppamentoList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToCreaCriterioRaggrPage();
        } else if (lista.equals(getForm().getSistemiMigrazioneList().getName())) {
            forwardToPublisher(Application.Publisher.XSD_MIGR_STRUT);
        } else if (getForm().getXsdDatiSpecList().getName().equals(lista)
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToXsdDatiSpecPage();
        } else if (lista.equals(getForm().getTitolariList().getName())
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTitolarioPage(action);
        } else if (lista.equals(getForm().getTipologieSerieList().getName())
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToTipologiaSeriaPage(action);
        } else if (getForm().getTrasformTipoRapprList().getName().equals(lista)) {
            TrasformatoriForm form = new TrasformatoriForm();
            if (idStrut == null) {
                idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                        .getCurrentRow()).getBigDecimal("id_strut");
            }
            form.getIdList().getId_strut().setValue(idStrut.toString());
            if (NE_DETTAGLIO_DELETE.equals(action)) {
                getSession().setAttribute("FromTrasformTipoRapprList", 1L);
            } else {
                getSession().removeAttribute("FromTrasformTipoRapprList");
            }
            BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
            form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
            form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
            OrgEnteRowBean enteTemp = struttureEjb
                    .getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
            form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
            form.getStrutRif().getStruttura()
                    .setValue(getForm().getInsStruttura().getNm_strut().parse() + " ("
                            + getForm().getInsStruttura().getDs_strut().parse() + ")");
            form.getStrutRif().getNm_strut()
                    .setValue(getForm().getInsStruttura().getNm_strut().parse());

            this.setInsertAction(false);
            this.setEditAction(false);
            this.setDeleteAction(false);
            form.getTrasformTipoRapprList().setFilterValidRecords(
                    getForm().getTrasformTipoRapprList().isFilterValidRecords());
            redirectToPage(Application.Actions.TRASFORMATORI, form,
                    form.getTrasformTipoRapprList().getName(),
                    getForm().getTrasformTipoRapprList().getTable(), getNavigationEvent());
        } else if (lista.equals(getForm().getSubStrutList().getName())
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToSubStrutPage(action);
        } else if (lista.equals(getForm().getEnteConvenzOrgList().getName())
                && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
            redirectToEnteConvenzPage(action);
        } else if (!action.equals(NE_DETTAGLIO_DELETE)) {
            if (lista.equals(getForm().getStruttureList().getName())
                    && (getForm().getStruttureList().getTable() != null)) {
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } else if (lista.equals(getForm().getDuplicaStruttureList().getName())
                    && (getForm().getDuplicaStruttureList().getTable() != null)) {
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } else if (getForm().getTipoRapprCompList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.TIPO_RAPPR_COMP_DETAIL);
            } else if (getForm().getTipoCompAmmessoDaTipoRapprCompList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP);
            } else if (getForm().getCategorieStruttureList().getName().equals(lista)) {
                cdDescription = getForm().getCategorieStrutture().getCd_categ_strut()
                        .getDescription();
                dsDescription = getForm().getCategorieStrutture().getDs_categ_strut()
                        .getDescription();
                if (isUpdate) {
                    cdDescription = (cdDescription.contains(("*")) ? cdDescription
                            : "*".concat(cdDescription));
                    dsDescription = (dsDescription.contains(("*")) ? dsDescription
                            : "*".concat(dsDescription));
                } else {
                    cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                            : cdDescription);
                    dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                            : dsDescription);
                }
                getForm().getCategorieStrutture().getCd_categ_strut().setDescription(cdDescription);
                getForm().getCategorieStrutture().getDs_categ_strut().setDescription(dsDescription);
                forwardToPublisher(Application.Publisher.CATEGORIE_STRUTTURE_DETAIL);
            } else if (getForm().getCategorieEntiList().getName().equals(lista)) {
                cdDescription = getForm().getCategorieEnti().getCd_categ_ente().getDescription();
                dsDescription = getForm().getCategorieEnti().getDs_categ_ente().getDescription();
                if (isUpdate) {
                    cdDescription = (cdDescription.contains(("*")) ? cdDescription
                            : "*".concat(cdDescription));
                    dsDescription = (dsDescription.contains(("*")) ? dsDescription
                            : "*".concat(dsDescription));
                } else {
                    cdDescription = (cdDescription.startsWith("*") ? cdDescription.substring(1)
                            : cdDescription);
                    dsDescription = (dsDescription.startsWith("*") ? dsDescription.substring(1)
                            : dsDescription);
                }
                getForm().getCategorieEnti().getCd_categ_ente().setDescription(cdDescription);
                getForm().getCategorieEnti().getDs_categ_ente().setDescription(dsDescription);
                forwardToPublisher(Application.Publisher.CATEGORIE_ENTI_DETAIL);
            } else if (lista.equals(getForm().getDocumentiProcessoConservList().getName())
                    && action.equals(ListAction.NE_DETTAGLIO_VIEW)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_DOCUMENTO_PROCESSO_CONSERV);
            }
        }
    }

    @Override
    public void updateInsStruttura() throws EMFError {
        if (getSession().getAttribute("provenienzaParametri") == null) {
            super.updateInsStruttura();
            getForm().getInsStruttura().setEditMode();
            BigDecimal idStrut = (BigDecimal) getSession().getAttribute("id_struttura_lavorato");
            OrgStrutRowBean orgStrutDaModificare = struttureEjb.getOrgStrutRowBean(idStrut, null);
            getForm().getInsStruttura().copyFromBean(orgStrutDaModificare);
            getForm().getInsStruttura().getInsStrutButton().setViewMode();
            getForm().getInsStruttura().getView_nm_amb().setViewMode();
            getForm().getInsStruttura().getView_nm_ente().setViewMode();
            getForm().getInsStruttura().getScaricaStruttura().setViewMode();
            getForm().getInsStruttura().getFl_archivio_restituito().setViewMode();
            getForm().getInsStruttura().getFl_cessato().setViewMode();
            getForm().getInsStruttura().getFl_template().setViewMode();
            getForm().getImportaParametri().getImportaParametriButton().setViewMode();
            getForm().getInsStruttura().getEliminaFormatiSpecifici().setViewMode();
            if (struttureEjb.hasAroUnitaDoc(idStrut)) {
                getForm().getInsStruttura().getNm_strut().setViewMode();
            }

            getForm().getInsStruttura().getId_ambiente_rif().setHidden(false);
            getForm().getInsStruttura().getId_ente_rif().setHidden(false);
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getDs_strut().setHidden(false);
            getForm().getInsStruttura().getView_nm_amb().setHidden(false);
            getForm().getInsStruttura().getView_nm_ente().setHidden(false);
            getForm().getInsStruttura().getFl_archivio_restituito().setHidden(false);
            getForm().getInsStruttura().getFl_cessato().setHidden(false);
            getForm().getInsStruttura().getFl_template().setHidden(false);
            getForm().getInsStruttura().getView_nm_strut().setHidden(true);
            getForm().getInsStruttura().getId_ente_rif().setDecodeMap(new DecodeMap());

            // Controllo valorizzazione parametri fascicolo in caso di flag gestione fascicoli
            // settato
            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic().equals("FL_ABILITA_CONTR_FMT_NUM")) {
                    row.setString("ds_valore_param_applic_strut_cons", "true");
                }
            }

            getForm().getInsStruttura().setStatus(Status.update);
            getForm().getStruttureList().setStatus(Status.update);
        } else {
            getForm().getInsStruttura().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenienzaParametri = (String) getSession()
                        .getAttribute("provenienzaParametri");
                try {
                    if (provenienzaParametri.equals("amministrazione")) {

                        setEditModeParametriAmministrazione();

                    } else if (provenienzaParametri.equals("conservazione")) {
                        setEditModeParametriConservazione();
                    } else if (provenienzaParametri.equals("gestione")) {
                        setEditModeParametriGestione();
                    }
                    forwardToPublisher(Application.Publisher.PARAMETRI_STRUTTURA);
                } catch (Exception ex) {
                    getMessageBox().addError("Errore durante il caricamento dei parametri");
                }
            }
        }
    }

    private void setEditModeParametriAmministrazione() {
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.update);
        getForm().getParametriConservazioneStrutturaList().setStatus(Status.update);
        getForm().getParametriGestioneStrutturaList().setStatus(Status.update);
        getForm().getParametriAmministrazioneStrutturaList().getDs_valore_param_applic_strut_amm()
                .setEditMode();
        getForm().getParametriConservazioneStrutturaList().getDs_valore_param_applic_strut_cons()
                .setEditMode();
        getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                .setEditMode();
    }

    private void setEditModeParametriConservazione() {
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getParametriConservazioneStrutturaList().setStatus(Status.update);
        getForm().getParametriGestioneStrutturaList().setStatus(Status.update);
        getForm().getParametriConservazioneStrutturaList().getDs_valore_param_applic_strut_cons()
                .setEditMode();
        getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                .setEditMode();
    }

    private void setEditModeParametriGestione() {
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getParametriGestioneStrutturaList().setStatus(Status.update);
        getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                .setEditMode();
    }

    /*
     * Questo metodo viene chiamato quaando nella form di dettaglio si punta ad un unico record
     * presente nella lista chiamante, quando ce ne sono piÃ¹ di uno viene chiamato il metodo
     * deleteStruttureList()
     */
    @Override
    public void deleteInsStruttura() throws EMFError {
        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
        BigDecimal idStrut = (BigDecimal) getSession().getAttribute("id_struttura_lavorato");
        if (idStrut != null && !BigDecimal.ZERO.equals(idStrut)) {
            strutRowBean.setIdStrut(idStrut);
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplicByApplic(
                                CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getToolbarDelete());
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                struttureEjb.deleteStruttura(param, idStrut.longValue());
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
            if (!getMessageBox().hasError()) {
                OrgStrutRowBean ricStrut = new OrgStrutRowBean();

                if (getForm().getVisStrutture().getId_ente().parse() != null) {
                    ricStrut.setIdEnte(getForm().getVisStrutture().getId_ente().parse());
                } else if (getForm().getVisStrutture().getId_ambiente().parse() != null) {
                    ricStrut.setBigDecimal("id_ambiente",
                            getForm().getVisStrutture().getId_ambiente().parse());
                }
                reloadStrutList(ricStrut);
                getMessageBox().addInfo("Struttura cancellata con successo");
                getMessageBox().setViewMode(ViewMode.plain);
                goToRicercaStrutture();
            }
        }
    }

    /**
     * Metodo richiamato con il tasto "Annulla" della NavBar
     *
     * @throws EMFError errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
        String lista = getTableName();
        String action = getNavigationEvent();
        ExecutionHistory lastExecutionHistory = SessionManager
                .getLastExecutionHistory(getSession());
        if (getForm().getStruttureList().getName().equals(lista) && NE_ELENCO.equals(action)
                && lastExecutionHistory.isAction()) {
            ricercaStruttura();
        } else if (getForm().getStruttureList().getName().equals(lista) && NE_ELENCO.equals(action)
                && !lastExecutionHistory.isAction()) {
            goBackTo(Application.Publisher.STRUTTURA_RICERCA);
        } else {
            if (getForm().getInsStruttura() != null) {
                getForm().getInsStruttura().setStatus(Status.view);
            }
            goBack();
        }
    }

    /**
     * Metodo richiamato dal tasto "Inserisci" all'interno della lista A seconda della lista
     * chiamante, visualizza la form di inserimento corrispondente
     *
     * @throws EMFError errore generico
     */
    @Override
    public void insertDettaglio() throws EMFError {

        String lista = getRequest().getParameter("table");

        if (lista.equals(getForm().getStruttureList().getName())) {
            getForm().getInsStruttura().setEditMode();
            getForm().getInsStruttura().clear();
            DecodeMap mappaAmbienti = new DecodeMap();
            BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                    getUser().getIdUtente(),
                    configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
            ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
            ambienteTableBean.sort();
            mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
            getForm().getInsStruttura().getId_ambiente_rif().setDecodeMap(mappaAmbienti);
            getForm().getInsStruttura().getId_ente_rif().setDecodeMap(new DecodeMap());

            try {
                // Object[] parametriObj = amministrazioneEjb.getAplParamApplicStruttura(null, null,
                // null, true);
                AplParamApplicTableBean parametriAmministrazione = amministrazioneEjb
                        .getAplParamApplicAmministrazioneStruttura(null, null, null, true);
                AplParamApplicTableBean parametriGestione = amministrazioneEjb
                        .getAplParamApplicGestioneStruttura(null, null, null, true);
                AplParamApplicTableBean parametriConservazione = amministrazioneEjb
                        .getAplParamApplicConservazioneStruttura(null, null, null, true);
                getForm().getParametriAmministrazioneSection().setLoadOpened(true);
                getForm().getParametriConservazioneSection().setLoadOpened(true);
                getForm().getParametriGestioneSection().setLoadOpened(true);
                getForm().getParametriAmministrazioneStrutturaList()
                        .setTable(parametriAmministrazione);
                getForm().getParametriAmministrazioneStrutturaList().getTable().setPageSize(300);
                getForm().getParametriAmministrazioneStrutturaList().getTable().first();
                getForm().getParametriGestioneStrutturaList().setTable(parametriGestione);
                getForm().getParametriGestioneStrutturaList().getTable().setPageSize(300);
                getForm().getParametriGestioneStrutturaList().getTable().first();
                getForm().getParametriConservazioneStrutturaList().setTable(parametriConservazione);
                getForm().getParametriConservazioneStrutturaList().getTable().setPageSize(300);
                getForm().getParametriConservazioneStrutturaList().getTable().first();
                getForm().getParametriAmministrazioneStrutturaList().setHideDeleteButton(true);
                getForm().getParametriGestioneStrutturaList().setHideDeleteButton(true);
                getForm().getParametriConservazioneStrutturaList().setHideDeleteButton(true);
                getForm().getParametriAmministrazioneStrutturaList()
                        .getDs_valore_param_applic_strut_amm().setEditMode();
                getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                        .setEditMode();
                getForm().getParametriConservazioneStrutturaList()
                        .getDs_valore_param_applic_strut_cons().setEditMode();
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }

            initStrutComboBox(null);

            // Inizializzo il campo relativo al numero massimo di componenti
            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic()
                        .equals(CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR)) {
                    row.setString("ds_valore_param_applic_strut_cons",
                            configurationHelper.getValoreParamApplicByApplic(
                                    CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR_WARN));
                    break;
                }
            }

            getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();

            getForm().getInsStruttura().getId_ambiente_rif().setHidden(false);
            getForm().getInsStruttura().getId_ente_rif().setHidden(false);
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getDs_strut().setHidden(false);
            getForm().getInsStruttura().getView_nm_amb().setHidden(true);
            getForm().getInsStruttura().getView_nm_ente().setHidden(true);
            getForm().getInsStruttura().getView_nm_strut().setHidden(true);

            // Precompilo le date
            Calendar data = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            Date today = data.getTime();
            String todayString = formatter.format(today);
            getForm().getInsStruttura().getDt_ini_val_strut().setValue(todayString);
            data.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            Date endWorld = data.getTime();
            String endWorldString = formatter.format(endWorld);
            getForm().getInsStruttura().getDt_fine_val_strut().setValue(endWorldString);
            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic().equals("FL_ABILITA_CONTR_FMT_NUM")) {
                    row.setString("ds_valore_param_applic_strut_cons", "true");
                }
            }
            getForm().getStrutFlags().setLoadOpened(true);

            getForm().getStruttureList().setStatus(Status.insert);
            getForm().getInsStruttura().setStatus(Status.insert);

            getSession().setAttribute(SES_ATTRIB_SALVATAGGIO, TipoSalvataggio.STRUTTURA);

        } else if (lista.equals(getForm().getTipoRapprCompList().getName())) {
            getForm().getTipoRapprComp().setEditMode();
            getForm().getTipoRapprComp().clear();
            BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
            getForm().getInsStruttura().getStruttura().setValue(
                    rowBean.getString("nm_strut") + " - " + rowBean.getString("ds_strut"));
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getDs_strut().setHidden(false);
            getForm().getInsStruttura().getId_ente().setHidden(false);

            Calendar data = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            Date today = data.getTime();
            String string = formatter.format(today);

            getForm().getTipoRapprComp().getDt_istituz().setValue(string);
            DecodeMap mappaAlgoritmoRappr = ComboGetter.getMappaTiAlgoRappr();
            DecodeMap mappaTipoOutputRappr = ComboGetter.getMappaTiOutputRappr();
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");
            DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
                    .getDecFormatoFileDocNiOrdUsoTableBean(idStrut);
            DecodeMap mappaFormatoFile = new DecodeMap();
            mappaFormatoFile.populatedMap(tmpTableBeanFormatoFileDoc, "id_formato_file_doc",
                    "nm_formato_file_doc");
            getForm().getTipoRapprComp().getId_formato_contenuto().setDecodeMap(mappaFormatoFile);
            getForm().getTipoRapprComp().getId_formato_convertit().setDecodeMap(mappaFormatoFile);
            DecFormatoFileStandardTableBean tmpTableBeanFfs = formatiStandardEjb
                    .getDecFormatoFileStandardTableBean(null);
            mappaFormatoFile = new DecodeMap();
            mappaFormatoFile.populatedMap(tmpTableBeanFfs, "id_formato_file_standard",
                    "nm_formato_file_standard");
            getForm().getTipoRapprComp().getId_formato_output_rappr()
                    .setDecodeMap(mappaFormatoFile);
            getForm().getTipoRapprComp().getTi_algo_rappr().setDecodeMap(mappaAlgoritmoRappr);
            getForm().getTipoRapprComp().getTi_output_rappr().setDecodeMap(mappaTipoOutputRappr);
            getForm().getTipoRapprComp().setStatus(Status.insert);
            getForm().getTipoRapprCompList().setStatus(Status.insert);
            getForm().getTipoRapprComp().getLogEventiTipoRapprComp().setViewMode();

        } else if (lista.equals(getForm().getCategorieStruttureList().getName())) {

            getForm().getCategorieStrutture().setEditMode();
            getForm().getCategorieStrutture().clear();

            getForm().getCategorieStrutture().setStatus(Status.insert);
            getForm().getCategorieStruttureList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getCategorieEntiList().getName())) {

            getForm().getCategorieEnti().setEditMode();
            getForm().getCategorieEnti().clear();

            getForm().getCategorieEnti().setStatus(Status.insert);
            getForm().getCategorieEntiList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getSubStrutList().getName())) {
            /*
             * Se l'azione è update o insert, verifico nel caso di struttura template che esista
             * solo la struttura di default e impongo che non se ne possano creare altre
             */
            boolean isTemplate = getForm().getInsStruttura().getFl_template().parse().equals("1");
            if (isTemplate) {
                getMessageBox().addError(
                        "La struttura \u00E8 di tipo template, impossibile creare nuove sottostrutture");
                forwardToPublisher(getLastPublisher());
            } else {
                redirectToSubStrutPage(NE_DETTAGLIO_INSERT);
            }
        } else if (lista.equals(getForm().getTitolariList().getName())) {
            getForm().getTitolarioCustomMessageButtonList().setEditMode();
            getRequest().setAttribute("customBox", true);
            getForm().getTitolariList().setStatus(Status.insert);
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
        } else if (lista.equals(getForm().getTipologieSerieList().getName())) {

            redirectToTipologiaSeriaPage(NE_DETTAGLIO_INSERT);

        } else if (lista.equals(getForm().getRegistroUnitaDocList().getName())) {
            redirectToRegistroPage();
        } else if (lista.equals(getForm().getTipoUnitaDocList().getName())) {
            redirectToTipologiaUnitaDocPage();
        } else if (lista.equals(getForm().getTipoDocList().getName())) {
            redirectToTipoDocPage();
        } else if (lista.equals(getForm().getTipoStrutDocList().getName())) {
            redirectToTipoStrutDocPage();
        } else if (lista.equals(getForm().getFormatoFileDocList().getName())) {
            redirectToFormatoFileDocPage();
        } else if (lista.equals(getForm().getCriteriRaggruppamentoList().getName())) {
            redirectToCreaCriterioRaggrPage();
        } else if (lista.equals(getForm().getXsdDatiSpecList().getName())) {
            redirectToXsdDatiSpecPage();
        } else if (lista.equals(getForm().getTipoCompAmmessoDaTipoRapprCompList().getName())) {
            // Lista "Tipo Rappresentazione Componente"
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");
            DecTipoStrutDocTableBean table = tipoDocEjb
                    .getDecTipoStrutDocTableBeanByIdStrut(idStrut, new Date());
            getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc().reset();

            DecodeMap mappaTipoStrutDoc = new DecodeMap();
            mappaTipoStrutDoc.populatedMap(table, "id_tipo_strut_doc", "nm_tipo_strut_doc");
            getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc()
                    .setDecodeMap(mappaTipoStrutDoc);
            /* Pulisco la bombo */
            getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc().clear();
            getForm().getTipoCompAmmessoDaTipoRapprComp().setEditMode();
            getForm().getTipoCompAmmessoDaTipoRapprComp().setStatus(Status.insert);
            getForm().getTipoCompAmmessoDaTipoRapprCompList().setStatus(Status.insert);
        } else if (lista.equals(getForm().getSistemiMigrazioneList().getName())) {
            getForm().getXsdMigrStrutTab()
                    .setCurrentTab(getForm().getXsdMigrStrutTab().getXsdMigrTipoUnitaDoc());
            getForm().getGestioneXsdMigrazione().reset();
            getForm().getGestioneXsdMigrazione().setEditMode();
            getForm().getGestioneXsdMigrazione().setStatus(Status.insert);

            getForm().getInsStruttura().getNm_strut().setViewMode();
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getDs_strut().setViewMode();
            getForm().getInsStruttura().getDs_strut().setHidden(false);
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");

            // inizializzazione combo
            AplSistemaMigrazTableBean aplSistemaMigrazTableBean = sysMigrazioneEjb
                    .getAplSistemaMigrazTableBean(idStrut);
            getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                    .setDecodeMap(DecodeMap.Factory.newInstance(aplSistemaMigrazTableBean,
                            "id_sistema_migraz", "nm_sistema_migraz"));
            getForm().getGestioneXsdMigrazione().getId_strut().setValue(idStrut.toPlainString());

            getForm().getXsdDatiSpecList().setTable(new DecXsdDatiSpecTableBean());
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            forwardToPublisher(Application.Publisher.XSD_MIGR_STRUT);
        } else if (lista.equals(getForm().getEnteConvenzOrgList().getName())) {
            redirectToEnteConvenzPage(NE_DETTAGLIO_INSERT);
        } else if (lista.equals(getForm().getTipoFascicoloList().getName())) {
            redirectToTipoFascicoloPage();
        }
    }

    /**
     * Metodo associato al link "Gestione Strutture" del menu Carica la form compilabile per la
     * ricerca
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.AmministrazioneStrutture.Strutture")
    public void ricercaStruttura() throws EMFError {

        try {
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.AmministrazioneStrutture.Strutture");

            getForm().getStruttureList().clear();
            getForm().getVisStrutture().setEditMode();
            getForm().getVisStrutture().getRicercaStrutturaButton().setEditMode();
            getForm().getVisStrutture().reset();

            initRicercaStruttureCombo();

            // Nascondo i bottoni di conferma/annulla creazione struttura template con javascript
            // disattivato
            getForm().getStruttureTemplateCreator().setViewMode();

            // ricarico la lista strutture
            OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
            strutRowBean.setNmStrut(getForm().getVisStrutture().getNm_strut().getValue());
            strutRowBean.setFlTemplate(getForm().getVisStrutture().getFl_template().getValue());
            strutRowBean.setString("fl_parametri_strut",
                    getForm().getVisStrutture().getFl_parametri_specifici().getValue());
            if (getForm().getVisStrutture().getId_ente().getValue() != null) {
                strutRowBean.setIdEnte(
                        new BigDecimal(getForm().getVisStrutture().getId_ente().getValue()));
            }
            getForm().getInsStruttura().getImportaStruttura().setEditMode();
            getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
            getForm().getImportaParametri().getImportaParametriDaRicercaButton().setEditMode();

            getForm().getStruttureList().getDupStruttura().setHidden(false);
            getForm().getStruttureList().getSostStruttura().setHidden(true);

            reloadStrutList(strutRowBean);

            getForm().getStruttureList().getDupStruttura().setHidden(false);
            getForm().getInsStruttura().getImportaStruttura().setHidden(false);
            getForm().getStruttureList().setHideInsertButton(false);

            getSession().removeAttribute("sostituzione");
            getSession().removeAttribute("daImporta");
            // Rimuovo l'attributo in sessione
            getSession().removeAttribute("struttureDaElaborarePerImportaParametri");

        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
            getMessageBox().setViewMode(ViewMode.plain);
        }
        forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
    }

    private void initRicercaStruttureCombo() throws ParerUserError {
        // Combo Ambiente
        DecodeMap mappaAmbienti = new DecodeMap();
        OrgAmbienteTableBean ambienteTableBean = ambienteEjb
                .getAmbientiAbilitati(getUser().getIdUtente());
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
        getForm().getVisStrutture().getId_ambiente().setDecodeMap(mappaAmbienti);

        Map<String, String> organizzazione = getUser().getOrganizzazioneMap();
        String ambienteSelezionato = organizzazione
                .get(WebConstants.Organizzazione.AMBIENTE.name());
        String enteSelezionato = organizzazione.get(WebConstants.Organizzazione.ENTE.name());
        String idAmbienteSelezionato = null;
        String idEnteSelezionato = null;
        if (ambienteSelezionato != null && !"".equals(ambienteSelezionato)) {
            Iterator<OrgAmbienteRowBean> it = ambienteTableBean.iterator();
            while (it.hasNext()) {
                OrgAmbienteRowBean orgAmbienteRowBean = it.next();
                String ambiente = orgAmbienteRowBean.getNmAmbiente();
                if (ambienteSelezionato.equals(ambiente)) {
                    idAmbienteSelezionato = orgAmbienteRowBean.getIdAmbiente().toString();
                    break;
                }
            }
        }

        if (idAmbienteSelezionato != null) {
            getForm().getVisStrutture().getId_ambiente().setValue(idAmbienteSelezionato);
        }

        // Combo Ente
        DecodeMap mappaEnti = new DecodeMap();
        // Ricavo i valori della combo ENTE
        OrgEnteTableBean orgEnteTableBean = ambienteEjb.getEntiAbilitati(getUser().getIdUtente(),
                Long.parseLong(idAmbienteSelezionato), Boolean.FALSE);
        orgEnteTableBean.addSortingRule("nm_ente", SortingRule.ASC);
        orgEnteTableBean.sort();
        for (OrgEnteRowBean row : orgEnteTableBean) {
            if (enteSelezionato.equals(row.getNmEnte())) {
                idEnteSelezionato = row.getIdEnte().toString();
            }
            row.setString("nmDs", row.getNmEnte() + ", " + row.getDsEnte());
        }

        mappaEnti.populatedMap(orgEnteTableBean, "id_ente", "nmDs");
        getForm().getVisStrutture().getId_ente().setDecodeMap(mappaEnti);
        if (idEnteSelezionato != null) {
            getForm().getVisStrutture().getId_ente().setValue(idEnteSelezionato);
        }

        // Combo flag
        getForm().getVisStrutture().getFl_template()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getVisStrutture().getFl_template().setValue("0");
        getForm().getVisStrutture().getFl_partiz()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getVisStrutture().getFl_parametri_specifici()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Combo ambito territoriale
        OrgAmbitoTerritTableBean regioniTable = ambienteEjb
                .getOrgAmbitoTerritTableBean("REGIONE/STATO");
        DecodeMap mappaRegione = new DecodeMap();
        mappaRegione.populatedMap(regioniTable, "id_ambito_territ", "cd_ambito_territ");
        getForm().getVisStrutture().getId_regione_stato().setDecodeMap(mappaRegione);

        // Azzero le combo provincia e forma associata
        getForm().getVisStrutture().getId_provincia().setDecodeMap(new DecodeMap());
        getForm().getVisStrutture().getId_forma_associata().setDecodeMap(new DecodeMap());

        // Combo categoria ente
        OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
        DecodeMap mappaCateg = new DecodeMap();
        mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
        getForm().getVisStrutture().getId_categ_ente().setDecodeMap(mappaCateg);

        // Combo ambiente ente convenzionati
        BaseTable ambienteEnteTable = ambienteEjb
                .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
        DecodeMap mappaAmbienteEnte = new DecodeMap();
        mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
                "nm_ambiente_ente_convenz");
        getForm().getVisStrutture().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);
    }

    @Override
    public void insStrutButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Metodo associato al bottone "VisStruttura" che raccoglie i dati dalla form di ricerca e li
     * utilizza per interrogare il DB
     *
     * @throws EMFError errore generico
     */
    // @Override
    public void visStrutturaButton() throws EMFError {

        VisStrutture strutturaForm;
        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();

        strutturaForm = getForm().getVisStrutture();
        strutturaForm.post(getRequest());
        strutturaForm.copyToBean(strutRowBean);

        getSession().setAttribute("idEnte", strutRowBean.getIdEnte());

        reloadStrutList(strutRowBean);
        forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);

    }

    /**
     * Metodo che inizializza le ComboBox presenti nella form associata a una struttura
     *
     * @throws EMFError errore generico
     */
    private void initStrutComboBox(BigDecimal idAmbienteEnteConvenz) {

        BaseTableInterface orgEnteTableBean = ambienteEjb.getEntiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC), null,
                null, null);
        DecodeMap mappaEnti = new DecodeMap();
        orgEnteTableBean.addSortingRule("nm_ente", SortingRule.ASC);
        orgEnteTableBean.sort();
        mappaEnti.populatedMap(orgEnteTableBean, "id_ente", "nm_ente");
        getForm().getInsStruttura().getId_ente().setDecodeMap(mappaEnti);

        // Pulizia filtri
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        BaseRow br2 = new BaseRow();
        BaseRow br3 = new BaseRow();

        bt.clear();
        // Inizializzazione mappa per tempo scadenza chiusura volume
        DecodeMap mappaTempoScadVol = new DecodeMap();
        br.setString("ti_scad_chius_volume", "GIORNALIERA");
        bt.add(br);
        br1.setString("ti_scad_chius_volume", "SETTIMANALE");
        bt.add(br1);
        br2.setString("ti_scad_chius_volume", "QUINDICINALE");
        bt.add(br2);
        br3.setString("ti_scad_chius_volume", "MENSILE");
        bt.add(br3);
        mappaTempoScadVol.populatedMap(bt, "ti_scad_chius_volume", "ti_scad_chius_volume");

        bt.clear();
        // Inizializzazione mappa per tempo scadenza chiusura
        DecodeMap mappaTempoScadChius = new DecodeMap();
        br.setString("ti_tempo_scad_chius", "MINUTI");
        bt.add(br);
        br1.setString("ti_tempo_scad_chius", "ORE");
        bt.add(br1);
        br2.setString("ti_tempo_scad_chius", "GIORNI");
        bt.add(br2);
        mappaTempoScadChius.populatedMap(bt, "ti_tempo_scad_chius", "ti_tempo_scad_chius");

        bt.clear();
        // Inizializzazione mappa per tempo scadenza chiusura firme
        DecodeMap mappaTempoScadChiusFirme = new DecodeMap();
        br.setString("ti_tempo_scad_chius_firme", "MINUTI");
        bt.add(br);
        br1.setString("ti_tempo_scad_chius_firme", "ORE");
        bt.add(br1);
        br2.setString("ti_tempo_scad_chius_firme", "GIORNI");
        bt.add(br2);
        mappaTempoScadChiusFirme.populatedMap(bt, "ti_tempo_scad_chius_firme",
                "ti_tempo_scad_chius_firme");

        bt.clear();
        // Inizializzazione mappa per i campi combo flag
        DecodeMap mappaFlag = new DecodeMap();
        br.setString("flag", "SI");
        br.setString("valore", "1");
        bt.add(br);
        br1.setString("flag", "NO");
        br1.setString("valore", "0");
        bt.add(br1);
        mappaFlag.populatedMap(bt, "valore", "flag");

        OrgCategStrutTableBean categTable = struttureEjb.getOrgCategStrutTableBean(null);
        DecodeMap mappaCateg = new DecodeMap();
        mappaCateg.populatedMap(categTable, "id_categ_strut", "cd_categ_strut");
        getForm().getInsStruttura().getId_categ_strut().setDecodeMap(mappaCateg);

        // Combo ambiente ente convenzionati
        BaseTable ambienteEnteTable = ambienteEjb
                .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
        DecodeMap mappaAmbienteEnte = new DecodeMap();
        mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
                "nm_ambiente_ente_convenz");
        getForm().getInsStruttura().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);

        // Ricavo il TableBean relativo agli enti convenzionati
        if (idAmbienteEnteConvenz != null) {
            BaseTable enteConvenzTableBean = ambienteEjb.getSIOrgEnteConvenzAccordoValidoTableBean(
                    getUser().getIdUtente(), idAmbienteEnteConvenz);
            DecodeMap mappaEntiConvenz = new DecodeMap();
            mappaEntiConvenz.populatedMap(enteConvenzTableBean, "id_ente_siam", "nm_ente_siam");
            getForm().getInsStruttura().getId_ente_convenz().setDecodeMap(mappaEntiConvenz);
        } else {
            getForm().getInsStruttura().getId_ente_convenz().setDecodeMap(new DecodeMap());
        }

        getForm().getInsStruttura().getFl_archivio_restituito()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getInsStruttura().getFl_archivio_restituito().setValue("0");
    }

    /**
     * Metodo che esegue la ricerca all'interno del Database di strutture corrispondenti ai
     * parametri passati in ingresso
     *
     * @param strutRowBean
     *
     * @throws EMFError errore generico
     */
    private void reloadStrutList(OrgStrutRowBean strutRowBean) throws EMFError {
        if (strutRowBean == null) {
            strutRowBean = new OrgStrutRowBean();
        }

        BigDecimal idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(
                strutRowBean.getBigDecimal("id_regione_stato"),
                strutRowBean.getBigDecimal("id_provincia"),
                strutRowBean.getBigDecimal("id_forma_associata"));

        OrgVRicStrutTableBean ricStrutTableBean = struttureEjb.getOrgVRicStrutTableBean(
                strutRowBean.getNmStrut(), strutRowBean.getIdEnte(),
                strutRowBean.getBigDecimal("id_ambiente"),
                strutRowBean.getFlTemplate() != null ? strutRowBean.getFlTemplate().equals("1")
                        : null,
                strutRowBean.getString("fl_partiz"), strutRowBean.getString("nm_sistema_versante"),
                idAmbitoTerrit, strutRowBean.getBigDecimal("id_categ_ente"),
                strutRowBean.getBigDecimal("id_ambiente_ente_convenz"),
                strutRowBean.getBigDecimal("id_ente_convenz"),
                strutRowBean.getString("fl_parametri_specifici"), getUser().getIdUtente());

        int inizio = 0;
        int pageSize = WebConstants.STRUTLIST_PAGE_SIZE;
        int paginaCorrente = 1;
        if (getForm().getStruttureList().getTable() != null) {
            inizio = getForm().getStruttureList().getTable().getFirstRowPageIndex();
            pageSize = (getForm().getStruttureList().getTable().getPageSize() == 1 ? 10
                    : getForm().getStruttureList().getTable().getPageSize());
            paginaCorrente = getForm().getStruttureList().getTable().getCurrentPageIndex();
        }

        getForm().getStruttureList().setTable(ricStrutTableBean);
        getForm().getStruttureList().getTable().setPageSize(pageSize);
        this.lazyLoadGoPage(getForm().getStruttureList(), paginaCorrente);
        getForm().getStruttureList().getTable().setCurrentRowIndex(inizio);

        if (getForm().getStruttureList().getStatus().equals(Status.update)) {
            getForm().getStruttureList().getTable().setCurrentRowIndex(
                    getForm().getStruttureList().getTable().getCurrentRowIndex());
        } else if (getForm().getStruttureList().getStatus().equals(Status.insert)) {
            getForm().getStruttureList().getTable().setCurrentRowIndex(
                    getForm().getStruttureList().getTable().getCurrentRowIndex());
        } else {
            getForm().getStruttureList().getTable().first();
        }

        // Conteggio strutture template
        getForm().getStruttureTemplate().getNum_strut_templ_disp()
                .setValue(struttureEjb.countOrgStrutTemplateRaggruppati(getUser().getIdUtente()));
        getForm().getStruttureTemplate().getNum_strut_templ_part()
                .setValue(struttureEjb.countOrgStrutTemplateWithCompletedPartitioningRaggruppati(
                        getUser().getIdUtente()));

        getForm().getInsStruttura().getImportaStruttura().setEditMode();
        getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
    }

    /**
     * Metodo che visualizza la form associata a OrgStruttura in status update
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateStruttureList() throws EMFError {

        getForm().getInsStruttura().setEditMode();
        getForm().getInsStruttura().getInsStrutButton().setViewMode();
        getForm().getInsStruttura().getView_nm_amb().setViewMode();
        getForm().getInsStruttura().getView_nm_ente().setViewMode();
        getForm().getInsStruttura().getScaricaStruttura().setViewMode();
        getForm().getImportaParametri().getImportaParametriButton().setViewMode();
        getForm().getInsStruttura().getEliminaFormatiSpecifici().setViewMode();
        getForm().getInsStruttura().getFl_cessato().setViewMode();
        getForm().getInsStruttura().getFl_template().setViewMode();
        if (getForm().getInsStruttura().getId_ente_convenz().parse() == null) {
            // Combo ambiente ente convenzionati
            BaseTable ambienteEnteTable = ambienteEjb
                    .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
            DecodeMap mappaAmbienteEnte = new DecodeMap();
            mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz",
                    "nm_ambiente_ente_convenz");
            getForm().getInsStruttura().getId_ambiente_ente_convenz()
                    .setDecodeMap(mappaAmbienteEnte);
        } else {
            getForm().getInsStruttura().getId_ambiente_ente_convenz().setViewMode();
        }
        if (struttureEjb.hasAroUnitaDoc(
                ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                        .getBigDecimal("id_strut"))) {
            getForm().getInsStruttura().getNm_strut().setViewMode();
        }

        getForm().getInsStruttura().getId_ambiente_rif().setHidden(false);
        getForm().getInsStruttura().getId_ente_rif().setHidden(false);
        getForm().getInsStruttura().getNm_strut().setHidden(false);
        getForm().getInsStruttura().getDs_strut().setHidden(false);
        getForm().getInsStruttura().getView_nm_amb().setHidden(false);
        getForm().getInsStruttura().getView_nm_ente().setHidden(false);
        getForm().getInsStruttura().getFl_archivio_restituito().setHidden(false);
        getForm().getInsStruttura().getFl_cessato().setHidden(false);
        getForm().getInsStruttura().getFl_template().setHidden(false);
        getForm().getInsStruttura().getView_nm_strut().setHidden(true);

        // Imposto aperte le sezioni riguardanti i parametri fascicolo
        getForm().getStrutFlags().setLoadOpened(true);
        getForm().getParametriUnitaDoc().setLoadOpened(true);
        getForm().getParametriFascicolo().setLoadOpened(true);

        for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                .getParametriConservazioneStrutturaList().getTable()) {
            if (row.getNmParamApplic().equals("FL_ABILITA_CONTR_FMT_NUM")) {
                row.setString("ds_valore_param_applic_strut_cons", "true");
            }
        }

        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getStruttureList().setStatus(Status.update);
        getSession().setAttribute(SES_ATTRIB_SALVATAGGIO, TipoSalvataggio.STRUTTURA);
    }

    @Override
    public void updateXsdDatiSpecList() throws EMFError {
        getForm().getXsdDatiSpec().setEditMode();
        getForm().getXsdDatiSpec().setStatus(Status.update);
        getForm().getXsdDatiSpecList().setStatus(Status.update);
    }

    /**
     * Metodo che visualizza la form associata a DecTipoRapprComp in status update
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateTipoRapprCompList() throws EMFError {
        getForm().getTipoRapprComp().setEditMode();
        getForm().getTipoRapprComp().getLogEventiTipoRapprComp().setViewMode();
        DecTipoRapprCompRowBean decTipoRapprCompRowBean = (DecTipoRapprCompRowBean) getForm()
                .getTipoRapprCompList().getTable().getCurrentRow();
        boolean existsVersamenti = tipoRapprEjb.checkRelation(decTipoRapprCompRowBean);
        if (existsVersamenti) {
            getForm().getTipoRapprComp().getNm_tipo_rappr_comp().setViewMode();
            getForm().getTipoRapprComp().getDt_istituz().setViewMode();
            getForm().getTipoRapprComp().getId_formato_contenuto().setViewMode();
            getForm().getTipoRapprComp().getId_formato_convertit().setViewMode();
            getForm().getTipoRapprComp().getId_formato_output_rappr().setViewMode();
            getForm().getTipoRapprComp().getTi_algo_rappr().setViewMode();
            getForm().getTipoRapprComp().getTi_output_rappr().setViewMode();
        }

        getForm().getTipoRapprComp().setStatus(Status.update);
        getForm().getTipoRapprCompList().setStatus(Status.update);
    }

    @Override
    public void updateCategorieStruttureList() throws EMFError {
        getForm().getCategorieStrutture().setEditMode();
        String cdDescription = getForm().getCategorieStrutture().getCd_categ_strut()
                .getDescription();
        String dsDescription = getForm().getCategorieStrutture().getDs_categ_strut()
                .getDescription();
        cdDescription = (cdDescription.contains(("*")) ? cdDescription : "*".concat(cdDescription));
        dsDescription = (dsDescription.contains(("*")) ? dsDescription : "*".concat(dsDescription));
        getForm().getCategorieStrutture().getCd_categ_strut().setDescription(cdDescription);
        getForm().getCategorieStrutture().getDs_categ_strut().setDescription(dsDescription);

        getForm().getCategorieStrutture().setStatus(Status.update);
        getForm().getCategorieStruttureList().setStatus(Status.update);
    }

    @Override
    public void updateCategorieEntiList() throws EMFError {
        getForm().getCategorieEnti().setEditMode();
        String cdDescription = getForm().getCategorieEnti().getCd_categ_ente().getDescription();
        String dsDescription = getForm().getCategorieEnti().getDs_categ_ente().getDescription();
        cdDescription = (cdDescription.contains(("*")) ? cdDescription : "*".concat(cdDescription));
        dsDescription = (dsDescription.contains(("*")) ? dsDescription : "*".concat(dsDescription));
        getForm().getCategorieEnti().getCd_categ_ente().setDescription(cdDescription);
        getForm().getCategorieEnti().getDs_categ_ente().setDescription(dsDescription);

        getForm().getCategorieEnti().setStatus(Status.update);
        getForm().getCategorieEntiList().setStatus(Status.update);
    }

    /**
     * Metodo che cancella dal DB la struttura selezionata attraverso la lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteStruttureList() throws EMFError {
        BaseRowInterface strutRowBean = getForm().getStruttureList().getTable().getCurrentRow();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.STRUTTURA_RICERCA.equalsIgnoreCase(param.getNomePagina())) {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getStruttureList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            struttureEjb.deleteStruttura(param, strutRowBean.getBigDecimal("id_strut").longValue());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (!getMessageBox().hasError()) {
            OrgStrutRowBean ricStrut = new OrgStrutRowBean();

            if (getForm().getVisStrutture().getId_ente().parse() != null) {
                ricStrut.setIdEnte(getForm().getVisStrutture().getId_ente().parse());
            } else if (getForm().getVisStrutture().getId_ambiente().parse() != null) {
                ricStrut.setBigDecimal("id_ambiente",
                        getForm().getVisStrutture().getId_ambiente().parse());
            }
            ricStrut.setFlTemplate(getForm().getVisStrutture().getFl_template().parse());
            ricStrut.setString("fl_partiz", getForm().getVisStrutture().getFl_partiz().parse());
            reloadStrutList(ricStrut);
            getMessageBox().addInfo("Struttura cancellata con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            goToRicercaStrutture();
        }
    }

    /**
     *
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteTipoRapprCompList() throws EMFError {
        getMessageBox().clear();
        String lastPublisher = getLastPublisher();
        DecTipoRapprCompRowBean tipoRapprCompRowBean = (DecTipoRapprCompRowBean) getForm()
                .getTipoRapprCompList().getTable().getCurrentRow();
        try {

            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (Application.Publisher.CREA_STRUTTURA.equalsIgnoreCase(param.getNomePagina())) {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
                        form.getTipoRapprCompList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoRapprEjb.deleteDecTipoRapprComp(param,
                    tipoRapprCompRowBean.getIdTipoRapprComp().longValue());
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Tipologia Rappresentazione eliminata con successo"));
            final BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");

            DecTipoRapprCompTableBean tipoRapprCompTableBean = tipoRapprEjb
                    .getDecTipoRapprCompTableBean(idStrut,
                            getForm().getTipoRapprCompList().isFilterValidRecords());
            getForm().getTipoRapprCompList().setTable(tipoRapprCompTableBean);
            getForm().getTipoRapprCompList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            getForm().getInsStruttura().getNm_strut().setHidden(true);
            getForm().getInsStruttura().getDs_strut().setHidden(true);
            getForm().getInsStruttura().getId_ente().setHidden(true);

            if (Application.Publisher.TIPO_RAPPR_COMP_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                forwardToPublisher(lastPublisher);
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            if (StringUtils.isNotBlank(lastPublisher)) {
                forwardToPublisher(lastPublisher);
            } else {
                goBack();
            }
        }
    }

    @Override
    public void deleteCategorieStruttureList() throws EMFError {
        getMessageBox().clear();

        OrgCategStrutRowBean orgCategStrutRowBean = (OrgCategStrutRowBean) getForm()
                .getCategorieStruttureList().getTable().getCurrentRow();

        try {
            struttureEjb.deleteOrgCategStrut(orgCategStrutRowBean);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (getMessageBox().isEmpty()) {
            orgCategStrutRowBean = new OrgCategStrutRowBean();
            getForm().getCategorieStrutture().copyToBean(orgCategStrutRowBean);

            OrgCategStrutTableBean orgCategStrutTableBean = struttureEjb
                    .getOrgCategStrutTableBean(orgCategStrutRowBean);

            getForm().getCategorieStruttureList().setTable(orgCategStrutTableBean);
            getForm().getCategorieStruttureList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getMessageBox().addInfo("Categoria Strutture cancellata con successo");

        }
        getMessageBox().setViewMode(ViewMode.plain);
    }

    @Override
    public void deleteCategorieEntiList() throws EMFError {
        getMessageBox().clear();

        OrgCategEnteRowBean orgCategEnteRowBean = (OrgCategEnteRowBean) getForm()
                .getCategorieEntiList().getTable().getCurrentRow();

        try {
            ambienteEjb.deleteOrgCategEnte(orgCategEnteRowBean);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (getMessageBox().isEmpty()) {
            orgCategEnteRowBean = new OrgCategEnteRowBean();
            getForm().getCategorieEnti().copyToBean(orgCategEnteRowBean);

            OrgCategEnteTableBean orgCategEnteTableBean = ambienteEjb
                    .getOrgCategEnteTableBean(orgCategEnteRowBean);

            getForm().getCategorieEntiList().setTable(orgCategEnteTableBean);
            getForm().getCategorieEntiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getMessageBox().addInfo("Categoria Enti cancellata con successo");

        }
        getMessageBox().setViewMode(ViewMode.plain);

    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        getSession().removeAttribute("elementoInserito");
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        try {

            /* NAVIGAZIONE RELOAD AFTER GO BACK CORRETTA */
            if (idStrut != null) {

                if (publisherName.equals(Application.Publisher.STRUTTURA_RICERCA)) {
                    OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
                    getForm().getVisStrutture().copyToBean(strutRowBean);

                    reloadStrutList(strutRowBean);

                    getForm().getInsStruttura().getImportaStruttura().setEditMode();
                    getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();

                } else if (publisherName.equals(Application.Publisher.CREA_STRUTTURA)) {
                    loadlists(idStrut, false);
                    getSession().setAttribute("lista", null);
                    getForm().getRegistriTab().setLoadOpened(true);
                    getForm().getTipoUdTab().setLoadOpened(true);
                    getForm().getTipoDocTab().setLoadOpened(true);
                    getForm().getStrutFlags().setLoadOpened(false);
                } else if (publisherName.equals(Application.Publisher.TIPO_RAPPR_COMP_DETAIL)) {
                    getForm().getInsStruttura().getDs_strut().setHidden(true);
                    getForm().getInsStruttura().getNm_strut().setHidden(true);
                    getForm().getInsStruttura().getId_ente().setHidden(true);
                    BigDecimal idTipoRapprComp = null;
                    if (!getForm().getTipoRapprCompList().getTable().isEmpty()) {
                        idTipoRapprComp = ((DecTipoRapprCompRowBean) getForm()
                                .getTipoRapprCompList().getTable().getCurrentRow())
                                .getIdTipoRapprComp();
                        reloadTipoRapprCompLists(idTipoRapprComp);
                    }
                } else if (publisherName
                        .equals(Application.Publisher.CATEGORIE_STRUTTURE_RICERCA)) {

                    getForm().getCategorieStrutture().setEditMode();
                    getForm().getCategorieStrutture().getRicercaCategorieStrutButton()
                            .setEditMode();

                    OrgCategStrutRowBean categStrutRowBean = new OrgCategStrutRowBean();
                    getForm().getCategorieStrutture().copyToBean(categStrutRowBean);
                    OrgCategStrutTableBean orgCategStrutTableBean = struttureEjb
                            .getOrgCategStrutTableBean(categStrutRowBean);

                    getForm().getCategorieStruttureList().setTable(orgCategStrutTableBean);
                    getForm().getCategorieStruttureList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getCategorieStruttureList().getTable().first();
                } else if (publisherName.equals(Application.Publisher.CATEGORIE_ENTI_RICERCA)) {

                    getForm().getCategorieEnti().setEditMode();
                    getForm().getCategorieEnti().getRicercaCategorieEnteButton().setEditMode();

                    OrgCategEnteRowBean categEnteRowBean = new OrgCategEnteRowBean();
                    getForm().getCategorieEnti().copyToBean(categEnteRowBean);
                    OrgCategEnteTableBean orgCategEnteTableBean = ambienteEjb
                            .getOrgCategEnteTableBean(categEnteRowBean);

                    getForm().getCategorieEntiList().setTable(orgCategEnteTableBean);
                    getForm().getCategorieEntiList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getCategorieEntiList().getTable().first();
                } else if (publisherName.equals(Application.Publisher.XSD_MIGR_STRUT)) {
                    String nmSistemaMigraz = getForm().getGestioneXsdMigrazione()
                            .getNm_sistema_migraz().getDecodedValue();
                    DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
                    if (getForm().getXsdMigrStrutTab().getCurrentTab()
                            .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoUnitaDoc())) {
                        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(idStrut,
                                CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                                CostantiDB.TipiEntitaSacer.UNI_DOC.name(), nmSistemaMigraz);
                    } else if (getForm().getXsdMigrStrutTab().getCurrentTab()
                            .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoDoc())) {
                        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(idStrut,
                                CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                                CostantiDB.TipiEntitaSacer.DOC.name(), nmSistemaMigraz);
                    } else if (getForm().getXsdMigrStrutTab().getCurrentTab()
                            .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoCompDoc())) {
                        xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(idStrut,
                                CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                                CostantiDB.TipiEntitaSacer.COMP.name(), nmSistemaMigraz);
                    }

                    getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                    getForm().getXsdDatiSpecList().getTable().first();
                    getForm().getXsdDatiSpecList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getXsdDatiSpecList().setStatus(Status.view);
                }
                postLoad();
            }
        } catch (EMFError ex) {
            log.error(ECCEZIONE_GENERICA, ex);
            getMessageBox().addError("Eccezione inattesa nel caricamento della pagina");
        }
    }

    @Override
    public void ricercaStrutturaButton() throws EMFError {
        VisStrutture strutturaForm = getForm().getVisStrutture();
        strutturaForm.post(getRequest());
        OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
        strutturaForm.copyToBean(strutRowBean);
        reloadStrutList(strutRowBean);
        if (getLastPublisher().equals(Application.Publisher.STRUTTURA_RICERCA)) {
            getForm().getInsStruttura().getImportaStruttura().setEditMode();
            forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
        } else if (getLastPublisher().equals(Application.Publisher.DUPLICA_STRUTTURA)) {
            getForm().getDuplicaStruttureList().setStatus(Status.view);
            forwardToPublisher(Application.Publisher.DUPLICA_STRUTTURA);
        }
    }

    public void duplicaStrutturaOperation() {
        // carico la maschera di compilazione con tutti i dati tranne ds e nm
        getForm().getCheckDuplicaStruttura().getCheck_sost_strut().setHidden(false);
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        getForm().getStruttureList().getTable().setCurrentRowIndex(nr);
        getSession().setAttribute("idStrutToCopy",
                ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                        .getBigDecimal("id_strut"));
        getForm().getCheckDuplicaStruttura().setEditMode();
        getForm().getInsStruttura().setViewMode();
        getForm().getInsStruttura().reset();
        getForm().getCheckDuplicaStruttura().clear();
        getForm().getCheckDuplicaStruttura().reset();
        setCheckInEditMode();
        if (((OrgVRicStrutRowBean) getForm().getStruttureList().getTable().getCurrentRow())
                .getFlTemplate().equals("1")) {
            getForm().getCheckDuplicaStruttura().getCheck_sost_strut().setHidden(true);
        }
        forwardToPublisher(Application.Publisher.DUPLICA_STRUTTURA);
    }

    public void duplicaFormatoOperation() throws EMFError {

        boolean isCessata = getForm().getInsStruttura().getFl_cessato().parse().equals("1");
        if (isCessata) {
            getMessageBox()
                    .addError("La struttura \u00E8 cessata, impossibile duplicare il formato");
            forwardToPublisher(getLastPublisher());
        } else {
            String riga = getRequest().getParameter("riga");
            Integer nr = Integer.parseInt(riga);

            getForm().getFormatoFileDocList().getTable().setCurrentRowIndex(nr);

            StrutFormatoFileForm form = new StrutFormatoFileForm();
            Integer row = getForm().getFormatoFileDocList().getTable().getCurrentRowIndex();

            StringBuilder string = new StringBuilder("?operation=duplicaFormato" + "&table="
                    + StrutFormatoFileForm.FormatoFileDocList.NAME + "&riga=" + row.toString());
            form.getFormatoFileDocList().setTable(getForm().getFormatoFileDocList().getTable());
            BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
            BigDecimal idStrut = rowBean.getBigDecimal("id_strut");

            form.getIdList().getId_strut().setValue(idStrut.toString());

            string.append("&idStrut=").append(idStrut);

            form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
            form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
            OrgEnteRowBean enteTemp = struttureEjb
                    .getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
            form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
            form.getStrutRif().getStruttura()
                    .setValue(getForm().getInsStruttura().getNm_strut().parse() + " ("
                            + getForm().getInsStruttura().getDs_strut().parse() + ")");

            // fisso a false tutte le autorizzazioni a procedere con le altre fasi della
            // listNavigationOnClick in questa
            // action
            this.setInsertAction(false);
            this.setEditAction(false);
            this.setDeleteAction(false);

            redirectToAction(Application.Actions.STRUT_FORMATO_FILE, string.toString(), form);

        }
    }

    @Override
    public JSONObject triggerVisStruttureId_enteOnTrigger() throws EMFError {
        getForm().getVisStrutture().post(getRequest());

        return getForm().getVisStrutture().asJSON();
    }

    @Override
    public JSONObject triggerVisStruttureId_ambienteOnTrigger() throws EMFError {
        getForm().getVisStrutture().post(getRequest());
        if (getForm().getVisStrutture().getId_ambiente().parse() != null) {
            DecodeMap mappaEnti = new DecodeMap();
            // Combo
            OrgEnteTableBean orgEnteTableBean = ambienteEjb.getEntiAbilitati(
                    getUser().getIdUtente(),
                    getForm().getVisStrutture().getId_ambiente().parse().longValue(),
                    Boolean.FALSE);
            orgEnteTableBean.addSortingRule("nm_ente", SortingRule.ASC);
            orgEnteTableBean.sort();
            for (OrgEnteRowBean row : orgEnteTableBean) {
                row.setString("nmDs", row.getNmEnte() + ", " + row.getDsEnte());
            }
            mappaEnti.populatedMap(orgEnteTableBean, "id_ente", "nmDs");
            getForm().getVisStrutture().getId_ente().setDecodeMap(mappaEnti);
        } else {
            getForm().getVisStrutture().getId_ente().setDecodeMap(new DecodeMap());
        }
        return getForm().getVisStrutture().asJSON();
    }

    @Override
    public JSONObject triggerInsStrutturaId_ambiente_rifOnTrigger() throws EMFError {
        getForm().getInsStruttura().post(getRequest());
        if (getForm().getInsStruttura().getId_ambiente_rif().parse() != null) {
            popolaEnteRif(null);
        } else {
            getForm().getInsStruttura().getId_ente_rif().setDecodeMap(new DecodeMap());
        }
        return getForm().getInsStruttura().asJSON();
    }

    private void popolaEnteRif(BigDecimal idEnte) throws EMFError {
        // A seconda di dove mi trovo (crea, duplica o importa) popolo
        // diversamente la combo ente
        List<String> tipoDefTemplateEnte = Arrays.asList(
                CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name(),
                CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name());
        TipoSalvataggio tipoSalvataggio = (TipoSalvataggio) getSession()
                .getAttribute(SES_ATTRIB_SALVATAGGIO);
        if (tipoSalvataggio != null && tipoSalvataggio.equals(TipoSalvataggio.STRUTTURA)) {
            tipoDefTemplateEnte = Arrays.asList(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name(),
                    CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name(),
                    CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE.name());

        }

        DecodeMap mappaEnti = new DecodeMap();
        BaseTableInterface orgEnteTableBean = ambienteEjb.getEntiValidiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC), null,
                getForm().getInsStruttura().getId_ambiente_rif().parse(), tipoDefTemplateEnte);
        orgEnteTableBean.addSortingRule("nm_ente", SortingRule.ASC);
        orgEnteTableBean.sort();

        mappaEnti.populatedMap(orgEnteTableBean, "id_ente", "nmDs");
        getForm().getInsStruttura().getId_ente_rif().setDecodeMap(mappaEnti);
        if (idEnte != null) {
            getForm().getInsStruttura().getId_ente_rif().setValue("" + idEnte);
        }
    }

    @Override
    public JSONObject triggerInsStrutturaId_ente_rifOnTrigger() throws EMFError {
        getForm().getInsStruttura().post(getRequest());
        return getForm().getInsStruttura().asJSON();
    }

    private String getDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2444, 11, 31, 0, 0, 0);

        Date dtSoppress = calendar.getTime();
        DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);

        return formato.format(dtSoppress);
    }

    @Override
    public void tabXsdMigrTipoDocOnClick() throws EMFError {

        getForm().getXsdMigrStrutTab()
                .setCurrentTab(getForm().getXsdMigrStrutTab().getXsdMigrTipoDoc());
        String nmSistemaMigraz = getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                .getDecodedValue();

        final BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(
                idStrut, CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                CostantiDB.TipiEntitaSacer.DOC.name(), nmSistemaMigraz);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.XSD_MIGR_STRUT);

    }

    @Override
    public void tabXsdMigrTipoUnitaDocOnClick() throws EMFError {
        getForm().getXsdMigrStrutTab()
                .setCurrentTab(getForm().getXsdMigrStrutTab().getXsdMigrTipoUnitaDoc());
        String nmSistemaMigraz = getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                .getDecodedValue();

        final BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(
                idStrut, CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                CostantiDB.TipiEntitaSacer.UNI_DOC.name(), nmSistemaMigraz);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getXsdDatiSpecList().setStatus(Status.update);

        forwardToPublisher(Application.Publisher.XSD_MIGR_STRUT);
    }

    @Override
    public void tabXsdMigrTipoCompDocOnClick() throws EMFError {
        getForm().getXsdMigrStrutTab()
                .setCurrentTab(getForm().getXsdMigrStrutTab().getXsdMigrTipoCompDoc());
        String nmSistemaMigraz = getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                .getDecodedValue();

        final BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(
                idStrut, CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                CostantiDB.TipiEntitaSacer.COMP.name(), nmSistemaMigraz);

        getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
        getForm().getXsdDatiSpecList().getTable().first();
        getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getXsdDatiSpecList().setStatus(Status.update);

        forwardToPublisher(Application.Publisher.XSD_MIGR_STRUT);
    }

    // MEV#21353
    @Override
    public void cessaStruttura() throws EMFError {
        InsStruttura struttura = getForm().getInsStruttura();
        struttura.post(getRequest());
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(this.getForm(),
                    this.getForm().getInsStruttura(),
                    this.getForm().getInsStruttura().getCessaStruttura().getName()));
            struttureEjb.cessazioneStruttura(param, idStrut.longValue());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (!getMessageBox().hasError()) {
            OrgStrutRowBean beanFromDb = struttureEjb.getOrgStrutRowBean(idStrut, null);
            OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
            struttura.copyToBean(strutRowBean);
            strutRowBean.setDtFineValStrut(beanFromDb.getDtFineValStrut());
            strutRowBean.setFlCessato(beanFromDb.getFlCessato());
            loadlists(idStrut, true);
            getForm().getInsStruttura().copyFromBean(strutRowBean);
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Cessazione della struttura effettuata con successo"));
            setDettaglioStrutturaViewMode();
        }
    }
    // end MEV#21353

    @Override
    public void scaricaStruttura() throws EMFError {
        BaseRowInterface row = getForm().getStruttureList().getTable().getCurrentRow();
        String ente = getForm().getInsStruttura().getView_nm_ente().parse();
        String filename = ente + "-" + row.getString("nm_strut");

        try {

            byte[] blob = struttureEjb.getOrgStrutXml(row);

            getResponse().setContentType("text/xml");
            getResponse().setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + ".xml");

            OutputStream out = getServletOutputStream();
            if (blob != null) {
                out.write(blob);
            }

            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Errore nella generazione dell'xml della struttura"));
            log.error("Errore nel marshalling", e);
        }
    }

    @Override
    public void importaStruttura() throws EMFError {
        getForm().getInsStruttura().setViewMode();
        getForm().getInsStruttura().clear();
        getForm().getInsStruttura().reset();
        setCheckInEditMode();
        getForm().getInsStruttura().getBl_xml_strut().setEditMode();
        getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
        getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
        getForm().getCheckDuplicaStruttura().clear();
        getForm().getCheckDuplicaStruttura().reset();
        initStrutComboBox(null);
        forwardToPublisher(Application.Publisher.IMPORTA_STRUTTURA);
    }

    private void setCheckInEditMode() {
        getForm().getInsStruttura().getCheck_dup_strut().setEditMode();
        getForm().getInsStruttura().getCheck_sost_strut().setEditMode();
        getForm().getInsStruttura().getCheck_includi_criteri().setEditMode();
        getForm().getInsStruttura().getCheck_includi_elementi_disattivi().setEditMode();
        getForm().getInsStruttura().getCheck_includi_formati().setEditMode();
        getForm().getInsStruttura().getCheck_includi_tipi_fascicolo().setEditMode();
        getForm().getInsStruttura().getCheck_mantieni_date_fine_validita().setEditMode();
        getForm().getInsStruttura().getCheck_includi_sistemi_migraz().setEditMode();
    }

    @Override
    public void confermaImportaStruttura() throws EMFError {

        getForm().getInsStruttura().setStatus(Status.insert);
        getForm().getStruttureList().setStatus(Status.insert);
        getMessageBox().clear();

        try {
            DecodeMap mappaAmbienti = new DecodeMap();
            BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                    getUser().getIdUtente(),
                    configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
            ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
            ambienteTableBean.sort();
            mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
            getForm().getInsStruttura().getId_ambiente_rif().setDecodeMap(mappaAmbienti);
            getForm().getInsStruttura().getId_ente_rif().setDecodeMap(new DecodeMap());

            getForm().getInsStruttura().setViewMode();

            getForm().getInsStruttura().getNm_strut().setEditMode();
            getForm().getInsStruttura().getCd_strut_normaliz().setEditMode();
            getForm().getInsStruttura().getDs_strut().setEditMode();
            getForm().getInsStruttura().getCd_ipa().setEditMode();
            getForm().getInsStruttura().getId_ambiente_rif().setEditMode();
            getForm().getInsStruttura().getId_ente_rif().setEditMode();
            getForm().getInsStruttura().getId_categ_strut().setEditMode();
            getForm().getInsStruttura().getDt_ini_val_strut().setEditMode();
            getForm().getInsStruttura().getDt_fine_val_strut().setEditMode();

            getForm().getInsStruttura().getId_ambiente_ente_convenz().setEditMode();
            getForm().getInsStruttura().getId_ente_convenz().setEditMode();
            getForm().getInsStruttura().getDt_ini_val().setEditMode();
            getForm().getInsStruttura().getDt_fine_val().setEditMode();
            getForm().getInsStruttura().getDl_note_strut().setEditMode();
            getForm().getInsStruttura().getNi_gg_scad_criterio().setEditMode();
            getForm().getInsStruttura().getNi_max_fascicoli_criterio().setEditMode();

            getForm().getInsStruttura().getDs_strut().setHidden(false);
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getCd_ipa().setHidden(false);
            getForm().getInsStruttura().getId_ambiente_rif().setHidden(false);
            getForm().getInsStruttura().getId_ente_rif().setHidden(false);
            getForm().getInsStruttura().getId_categ_strut().setHidden(false);

            // Apro le sezioni relative ai parametri
            getForm().getStrutFlags().setLoadOpened(true);
            getForm().getParametriUnitaDoc().setLoadOpened(true);
            getForm().getParametriFascicolo().setLoadOpened(true);
            importOrgStrutXml();

        } catch (JAXBException ex) {
            log.error(ECCEZIONE_GENERICA, ex);
        }
    }

    private SalvaStrutturaDto getSalvaStrutturaDto() throws EMFError {
        String checkIncludiCriteri = getForm().getInsStruttura().getCheck_includi_criteri().parse();
        String checkIncludiElementiDisattivi = getForm().getInsStruttura()
                .getCheck_includi_elementi_disattivi().parse();
        String checkIncludiFormati = getForm().getInsStruttura().getCheck_includi_formati().parse();
        String checkIncludiTipiFascicolo = getForm().getInsStruttura()
                .getCheck_includi_tipi_fascicolo().parse();
        String checkMantieniDateFineValidita = getForm().getInsStruttura()
                .getCheck_mantieni_date_fine_validita().parse();
        String checkIncludiSistemiMigraz = getForm().getInsStruttura()
                .getCheck_includi_sistemi_migraz().parse();
        SalvaStrutturaDto salva = new SalvaStrutturaDto();
        salva.setCheckIncludiCriteri(checkIncludiCriteri.equals("1"));
        salva.setCheckIncludiElementiDisattivi(checkIncludiElementiDisattivi.equals("1"));
        salva.setCheckIncludiFormati(checkIncludiFormati.equals("1"));
        salva.setCheckIncludiTipiFascicolo(checkIncludiTipiFascicolo.equals("1"));
        salva.setCheckMantieniDateFineValidita(checkMantieniDateFineValidita.equals("1"));
        salva.setCheckIncludiSistemiMigraz(checkIncludiSistemiMigraz.equals("1"));
        return salva;
    }

    private void importOrgStrutXml() throws EMFError, JAXBException {

        TipoSalvataggio salvataggioAttribute = TipoSalvataggio.IMPORTA_NON_STANDARD;
        getMessageBox().clear();

        try {
            // Struttura NON STANDARD
            String checkDupStrut = getForm().getInsStruttura().getCheck_dup_strut().parse();
            // Struttura STANDARD
            String checkSostStrut = getForm().getInsStruttura().getCheck_sost_strut().parse();
            SalvaStrutturaDto salva = getSalvaStrutturaDto();
            // Se ho selezionato il check di struttura STANDARD, controllo anche di avere strutture
            // template disponibili
            int numOfCheck = 0;
            if (checkDupStrut.equals("1")) {
                numOfCheck++;
            }
            if (checkSostStrut.equals("1")) {
                BigDecimal strutTemplateToSost = null;
                List<String> listaIdStruttureTemplate = getListaStrutTemplate();
                if (listaIdStruttureTemplate == null || listaIdStruttureTemplate.isEmpty()) {
                    getMessageBox().addError("Non sono disponibili strutture template ");
                    getForm().getInsStruttura().setViewMode();
                    setCheckInEditMode();
                    getForm().getInsStruttura().getBl_xml_strut().setEditMode();
                    getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
                    getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
                } else {
                    strutTemplateToSost = new BigDecimal(listaIdStruttureTemplate.get(0));
                    getSession().setAttribute("idStrutToCopy", strutTemplateToSost);
                    getSession().setAttribute("daImporta", "Si");
                    salvataggioAttribute = TipoSalvataggio.IMPORTA_STANDARD;
                    getForm().getInsStruttura().getFl_template().setViewMode();
                }
                numOfCheck++;
            }

            // Controllo di aver spuntato almeno uno dei 2 check
            switch (numOfCheck) {
            case 0:
                getMessageBox().addError("Selezionare almeno una funzione");
                getForm().getInsStruttura().setViewMode();
                getForm().getInsStruttura().clear();
                setCheckInEditMode();
                getForm().getInsStruttura().getBl_xml_strut().setEditMode();
                getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
                getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
                break;
            case 2:
                getMessageBox().addError("Selezionare solo una funzione");
                getForm().getInsStruttura().setViewMode();
                getForm().getInsStruttura().clear();
                setCheckInEditMode();
                getForm().getInsStruttura().getBl_xml_strut().setEditMode();
                getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
                getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
                break;
            default:
            }

            // Controllo di aver selezionato un file xml
            if (!getMessageBox().hasError()) {
                if (getForm().getInsStruttura().getBl_xml_strut().parse() == null) {
                    getMessageBox().addError("Nessun file selezionato");
                    getForm().getInsStruttura().setViewMode();
                    getForm().getInsStruttura().clear();
                    setCheckInEditMode();
                    getForm().getInsStruttura().getBl_xml_strut().setEditMode();
                    getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
                    getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
                }
            }

            // Se è tutto a posto, procedo ad elaborare il file
            if (!getMessageBox().hasError()) {
                byte[] fileByteArray = getForm().getInsStruttura().getBl_xml_strut().getFileBytes();
                String xmlString = new String(fileByteArray, StandardCharsets.UTF_8);

                String mime = singleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.XML_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/xml");
                }

                if (!getMessageBox().hasError()) {
                    UUID uuid = struttureEjb.importXmlOrgStrut(xmlString);
                    getSession().setAttribute("uuid", uuid);
                    getSession().setAttribute(SES_ATTRIB_SALVATAGGIO, salvataggioAttribute);
                    getSession().setAttribute(SES_ATTRIB_VALORI_SALVATAGGIO, salva);
                    // Creazione rowbean struttura con Gestione dei vari flag
                    OrgStrutRowBean strutRowBean = struttureEjb.strutToRowBean(uuid);
                    getForm().getInsStruttura().copyFromBean(strutRowBean);
                    try {
                        // Lista parametri
                        loadListeParametriStrutturaPerDupImp(strutRowBean, true, true,
                                struttureEjb.getStrutFromCache(uuid));
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getMessage());
                    }
                }

                getForm().getInsStruttura().getNm_strut().clear();
                getForm().getInsStruttura().getCd_strut_normaliz().clear();
                getForm().getInsStruttura().getDs_strut().clear();
                getForm().getInsStruttura().getCd_ipa().clear();
                getForm().getInsStruttura().getId_ambiente_rif().clear();
                getForm().getInsStruttura().getId_ambiente_ente_convenz().clear();
                getForm().getInsStruttura().getId_ente_convenz().clear();

                precompilaDateDuplicaImporta();
                getForm().getInsStruttura().setStatus(Status.insert);
                getForm().getStruttureList().setStatus(Status.insert);
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } else {
                // Se c'è qualche problem, resto qua
                forwardToPublisher(Application.Publisher.IMPORTA_STRUTTURA);
            }
        } catch (Exception ex) {
            log.error("Eccezione nell'upload dei file", ex);
            getMessageBox().addError("Eccezione nell'upload dei file", ex);
            getForm().getInsStruttura().setViewMode();
            getForm().getInsStruttura().clear();
            setCheckInEditMode();
            getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
            getForm().getInsStruttura().getConfermaImportaStruttura().setEditMode();
            forwardToPublisher(Application.Publisher.IMPORTA_STRUTTURA);
        }
    }

    private void loadlists(BigDecimal idStrut, boolean isFirst) throws EMFError {
        if (idStrut != null) {
            HashMap<String, Integer> indMap = new HashMap<>();

            if (!isFirst) {
                // Imposto gli indici di riga correnti da mantenere
                if (getForm().getRegistroUnitaDocList().getTable() != null) {
                    indMap.put("registroUnitaDoc",
                            getForm().getRegistroUnitaDocList().getTable().getCurrentRowIndex());
                    indMap.put("registroUnitaDocPS",
                            getForm().getRegistroUnitaDocList().getTable().getPageSize());
                }
                if (getForm().getTipoUnitaDocList().getTable() != null) {
                    indMap.put("tipoUnitaDoc",
                            getForm().getTipoUnitaDocList().getTable().getCurrentRowIndex());
                    indMap.put("tipoUnitaDocPS",
                            getForm().getTipoUnitaDocList().getTable().getPageSize());
                }
                if (getForm().getTipoDocList().getTable() != null) {
                    indMap.put("tipoDoc",
                            getForm().getTipoDocList().getTable().getCurrentRowIndex());
                    indMap.put("tipoDocPS", getForm().getTipoDocList().getTable().getPageSize());
                } //
                if (getForm().getTipoFascicoloList().getTable() != null) {
                    indMap.put("tipoFascicolo",
                            getForm().getTipoFascicoloList().getTable().getCurrentRowIndex());
                    indMap.put("tipoFascicoloPS",
                            getForm().getTipoFascicoloList().getTable().getPageSize());
                }
                if (getForm().getTipoStrutDocList().getTable() != null) {
                    indMap.put("tipoStrutDoc",
                            getForm().getTipoStrutDocList().getTable().getCurrentRowIndex());
                    indMap.put("tipoStrutDocPS",
                            getForm().getTipoStrutDocList().getTable().getPageSize());
                }
                if (getForm().getTipoRapprCompList().getTable() != null) {
                    indMap.put("tipoRapprComp",
                            getForm().getTipoRapprCompList().getTable().getCurrentRowIndex());
                    indMap.put("tipoRapprCompPS",
                            getForm().getTipoRapprCompList().getTable().getPageSize());
                }
                if (getForm().getCriteriRaggruppamentoList().getTable() != null) {
                    indMap.put("criteriRaggr", getForm().getCriteriRaggruppamentoList().getTable()
                            .getCurrentRowIndex());
                    indMap.put("criteriRaggrPS",
                            getForm().getCriteriRaggruppamentoList().getTable().getPageSize());
                }
                if (getForm().getTitolariList().getTable() != null) {
                    indMap.put("titolari",
                            getForm().getTitolariList().getTable().getCurrentRowIndex());
                    indMap.put("titolariPS", getForm().getTitolariList().getTable().getPageSize());
                }
                if (getForm().getSubStrutList().getTable() != null) {
                    indMap.put("subStrut",
                            getForm().getSubStrutList().getTable().getCurrentRowIndex());
                    indMap.put("subStrutPS", getForm().getSubStrutList().getTable().getPageSize());
                }
                if (getForm().getTipologieSerieList().getTable() != null) {
                    indMap.put("tipoSerie",
                            getForm().getTipologieSerieList().getTable().getCurrentRowIndex());
                    indMap.put("tipoSeriePS",
                            getForm().getTipologieSerieList().getTable().getPageSize());
                }
                if (getForm().getSistemiMigrazioneList().getTable() != null) {
                    indMap.put("sysMigr",
                            getForm().getSistemiMigrazioneList().getTable().getCurrentRowIndex());
                    indMap.put("sysMigrPS",
                            getForm().getSistemiMigrazioneList().getTable().getPageSize());
                }
                if (getForm().getEnteConvenzOrgList().getTable() != null) {
                    indMap.put("enteConvenz",
                            getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex());
                    indMap.put("enteConvenzPS",
                            getForm().getEnteConvenzOrgList().getTable().getPageSize());
                }
                if (getForm().getCorrispondenzePingList().getTable() != null) {
                    indMap.put("corrPing",
                            getForm().getCorrispondenzePingList().getTable().getCurrentRowIndex());
                    indMap.put("corrPingPS",
                            getForm().getCorrispondenzePingList().getTable().getPageSize());
                }
                if (getForm().getDocumentiProcessoConservList().getTable() != null) {
                    indMap.put("docProcCons", getForm().getDocumentiProcessoConservList().getTable()
                            .getCurrentRowIndex());
                    indMap.put("docProcConsPS",
                            getForm().getDocumentiProcessoConservList().getTable().getPageSize());
                }
            }

            // Lista registri
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getDecRegistroUnitaDocTableBean(idStrut,
                            getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
            registroUnitaDocTableBean.addSortingRule(
                    DecRegistroUnitaDocTableDescriptor.COL_CD_REGISTRO_UNITA_DOC, SortingRule.ASC);
            registroUnitaDocTableBean.sort();

            // Lista tipi Unità Documentaria
            DecTipoUnitaDocTableBean tipoUnitaTableBean = tipoUnitaDocEjb
                    .getDecTipoUnitaDocTableBean(idStrut,
                            getForm().getTipoUnitaDocList().isFilterValidRecords());

            getForm().getTipoUnitaDocList().setTable(tipoUnitaTableBean);
            tipoUnitaTableBean.addSortingRule(DecTipoUnitaDocTableDescriptor.COL_NM_TIPO_UNITA_DOC,
                    SortingRule.ASC);
            tipoUnitaTableBean.sort();

            // Lista tipi documento
            DecTipoDocTableBean tipoDocTableBean = tipoDocEjb.getDecTipoDocTableBean(idStrut,
                    getForm().getTipoDocList().isFilterValidRecords());
            getForm().getTipoDocList().setTable(tipoDocTableBean);
            tipoDocTableBean.addSortingRule(DecTipoDocTableDescriptor.COL_NM_TIPO_DOC,
                    SortingRule.ASC);
            tipoDocTableBean.sort();

            // Lista tipi fascicolo
            DecTipoFascicoloTableBean tipoFascicoloTableBean = tipoFascicoloEjb
                    .getDecTipoFascicoloList(idStrut,
                            getForm().getTipoFascicoloList().isFilterValidRecords());
            getForm().getTipoFascicoloList().setTable(tipoFascicoloTableBean);
            tipoFascicoloTableBean.addSortingRule(
                    DecTipoFascicoloTableDescriptor.COL_NM_TIPO_FASCICOLO, SortingRule.ASC);
            tipoFascicoloTableBean.sort();
            getForm().getTipoFascicoloTab().setLoadOpened(!tipoFascicoloTableBean.isEmpty());

            // Lista strutture documento
            DecTipoStrutDocTableBean tipoStrutDocTableBean = tipoStrutDocEjb
                    .getDecTipoStrutDocTableBean(idStrut,
                            getForm().getTipoStrutDocList().isFilterValidRecords());
            getForm().getTipoStrutDocList().setTable(tipoStrutDocTableBean);
            tipoStrutDocTableBean.addSortingRule(
                    DecTipoStrutDocTableDescriptor.COL_NM_TIPO_STRUT_DOC, SortingRule.ASC);
            tipoStrutDocTableBean.sort();

            // Lista tipologie rappresentazioni
            DecTipoRapprCompTableBean tipoRapprCompTableBean = tipoRapprEjb
                    .getDecTipoRapprCompTableBean(idStrut,
                            getForm().getTipoRapprCompList().isFilterValidRecords());

            getForm().getTipoRapprCompList().setTable(tipoRapprCompTableBean);
            tipoRapprCompTableBean.addSortingRule(
                    DecTipoRapprCompTableDescriptor.COL_NM_TIPO_RAPPR_COMP, SortingRule.ASC);
            tipoRapprCompTableBean.sort();

            // Lista criteri raggruppamento
            DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrByIdStrut(idStrut,
                    getForm().getCriteriRaggruppamentoList().isFilterValidRecords());
            criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
            criteri.sort();
            getForm().getCriteriRaggruppamentoList().setTable(criteri);
            getForm().getCriteriRaggruppamentoList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            // Lista titolari
            getForm().getTitolariList().setTable(titolariEjb.getDecTitolTableBean(idStrut,
                    getForm().getTitolariList().isFilterValidRecords()));
            getForm().getTitolariList().getTable().addSortingRule("dt_istituz", SortingRule.DESC);
            getForm().getTitolariList().getTable().sort();

            // Lista tipi Serie
            getForm().getTipologieSerieList().setTable(tipoSerieEjb.getDecTipoSerieTableBean(
                    idStrut, getForm().getTipologieSerieList().isFilterValidRecords()));
            getForm().getTipologieSerieList().getTable().addSortingRule("nm_tipo_serie",
                    SortingRule.ASC);
            getForm().getTipologieSerieList().getTable().sort();
            // Lista sottoStrutture
            getForm().getSubStrutList().setTable(subStrutEjb.getOrgSubStrutTableBean(idStrut));
            getForm().getSubStrutList().getTable().addSortingRule("nm_sub_strut", SortingRule.ASC);
            getForm().getSubStrutList().getTable().sort();
            // Lista sistemi di migrazione
            getForm().getSistemiMigrazioneList()
                    .setTable(sysMigrazioneEjb.getOrgUsoSistemaMigrazTableBean(idStrut));
            getForm().getSistemiMigrazioneList().getTable().addSortingRule("nm_sistema_migraz",
                    SortingRule.ASC);
            getForm().getSistemiMigrazioneList().getTable().sort();
            // Lista enti convenzionati
            getForm().getEnteConvenzOrgList()
                    .setTable(ambienteEjb.getSIOrgEnteConvenzOrgTableBean(idStrut));
            getForm().getEnteConvenzOrgList().getTable().addSortingRule("dt_ini_val",
                    SortingRule.DESC);
            getForm().getEnteConvenzOrgList().getTable().sort();

            SIOrgEnteConvenzOrg orgEnteConvenzOrg = ambienteEjb
                    .getOrgEnteConvenzOrgMostRecent(idStrut);

            if (orgEnteConvenzOrg != null) {
                try {
                    getForm().getDocumentiProcessoConservList()
                            .setTable(struttureEjb.getDecDocProcessoConservByEnteTableBean(
                                    new BigDecimal(orgEnteConvenzOrg.getSiOrgEnteConvenz()
                                            .getIdEnteSiam().longValue())));
                } catch (ParerUserError ex) {
                    java.util.logging.Logger.getLogger(StruttureAction.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }

            getForm().getDocumentiProcessoConservList().getTable().setPageSize(10);
            getForm().getDocumentiProcessoConservList().getTable().first();
            try {
                // Lista parametri
                // loadListeParametriStruttura(idStrut, null, false, false, false, false, true);
                loadListaParametriAmnministrazioneStruttura(idStrut, null, true, false, getForm()
                        .getParametriAmministrazioneStrutturaList().isFilterValidRecords());
                loadListaParametriConservazioneStruttura(idStrut, null, true, false,
                        getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
                loadListaParametriGestioneStruttura(idStrut, null, true, false,
                        getForm().getParametriGestioneStrutturaList().isFilterValidRecords());

            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getMessage());
            }

            getForm().getCorrispondenzePingList()
                    .setTable(struttureEjb.retrieveOrgVCorrPingList(idStrut));
            getForm().getCorrispondenzePingList().getTable().addSortingRule("nm_vers",
                    SortingRule.DESC);
            getForm().getCorrispondenzePingList().getTable().sort();

            getForm().getRegistroUnitaDocList().getTable().first();
            getForm().getTipoUnitaDocList().getTable().first();
            getForm().getTipoDocList().getTable().first();
            getForm().getTipoFascicoloList().getTable().first();
            getForm().getTipoStrutDocList().getTable().first();
            getForm().getTipoRapprCompList().getTable().first();
            getForm().getTitolariList().getTable().first();
            getForm().getSubStrutList().getTable().first();
            getForm().getTipologieSerieList().getTable().first();
            getForm().getSistemiMigrazioneList().getTable().first();
            getForm().getCorrispondenzePingList().getTable().first();
            getForm().getRegistroUnitaDocList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoUnitaDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoStrutDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipoRapprCompList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getCriteriRaggruppamentoList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTitolariList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getTipologieSerieList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getSubStrutList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getSistemiMigrazioneList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getCorrispondenzePingList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            // Apro le prime section
            getForm().getRegistriTab().setLoadOpened(true);
            getForm().getTipoUdTab().setLoadOpened(true);
            getForm().getTipoDocTab().setLoadOpened(true);

            getForm().getParametriAmministrazioneSection().setLoadOpened(false);
            getForm().getParametriConservazioneSection().setLoadOpened(false);
            getForm().getParametriGestioneSection().setLoadOpened(false);
            getForm().getCorrispondenzePingSection().setLoadOpened(false);

            if (!indMap.isEmpty()) {
                if (getForm().getRegistroUnitaDocList().getTable() != null
                        && indMap.containsKey("registroUnitaDoc")) {
                    getForm().getRegistroUnitaDocList().getTable()
                            .setCurrentRowIndex(indMap.get("registroUnitaDoc"));
                    getForm().getRegistroUnitaDocList().getTable()
                            .setPageSize(indMap.get("registroUnitaDocPS"));
                }
                if (getForm().getTipoUnitaDocList().getTable() != null
                        && indMap.containsKey("tipoUnitaDoc")) {
                    getForm().getTipoUnitaDocList().getTable()
                            .setCurrentRowIndex(indMap.get("tipoUnitaDoc"));
                    getForm().getTipoUnitaDocList().getTable()
                            .setPageSize(indMap.get("tipoUnitaDocPS"));
                }
                if (getForm().getTipoDocList().getTable() != null
                        && indMap.containsKey("tipoDoc")) {
                    getForm().getTipoDocList().getTable().setCurrentRowIndex(indMap.get("tipoDoc"));
                    getForm().getTipoDocList().getTable().setPageSize(indMap.get("tipoDocPS"));
                }
                if (getForm().getTipoFascicoloList().getTable() != null
                        && indMap.containsKey("tipoFascicolo")) {
                    getForm().getTipoFascicoloList().getTable()
                            .setCurrentRowIndex(indMap.get("tipoFascicolo"));
                    getForm().getTipoFascicoloList().getTable()
                            .setPageSize(indMap.get("tipoFascicoloPS"));
                }
                if (getForm().getTipoStrutDocList().getTable() != null
                        && indMap.containsKey("tipoStrutDoc")) {
                    getForm().getTipoStrutDocList().getTable()
                            .setCurrentRowIndex(indMap.get("tipoStrutDoc"));
                    getForm().getTipoStrutDocList().getTable()
                            .setPageSize(indMap.get("tipoStrutDocPS"));
                }
                if (getForm().getTipoRapprCompList().getTable() != null
                        && indMap.containsKey("tipoRapprComp")) {
                    getForm().getTipoRapprCompList().getTable()
                            .setCurrentRowIndex(indMap.get("tipoRapprComp"));
                    getForm().getTipoRapprCompList().getTable()
                            .setPageSize(indMap.get("tipoRapprCompPS"));
                }
                if (getForm().getCriteriRaggruppamentoList().getTable() != null
                        && indMap.containsKey("criteriRaggr")) {
                    getForm().getCriteriRaggruppamentoList().getTable()
                            .setCurrentRowIndex(indMap.get("criteriRaggr"));
                    getForm().getCriteriRaggruppamentoList().getTable()
                            .setPageSize(indMap.get("criteriRaggrPS"));
                }
                if (getForm().getTitolariList().getTable() != null
                        && indMap.containsKey("titolari")) {
                    getForm().getTitolariList().getTable()
                            .setCurrentRowIndex(indMap.get("titolari"));
                    getForm().getTitolariList().getTable().setPageSize(indMap.get("titolariPS"));
                }
                if (getForm().getTipologieSerieList().getTable() != null
                        && indMap.containsKey("tipoSerie")) {
                    getForm().getTipologieSerieList().getTable()
                            .setCurrentRowIndex(indMap.get("tipoSerie"));
                    getForm().getTipologieSerieList().getTable()
                            .setPageSize(indMap.get("tipoSeriePS"));
                }
                if (getForm().getSubStrutList().getTable() != null
                        && indMap.containsKey("subStrut")) {
                    getForm().getSubStrutList().getTable()
                            .setCurrentRowIndex(indMap.get("subStrut"));
                    getForm().getSubStrutList().getTable().setPageSize(indMap.get("subStrutPS"));
                }
                if (getForm().getSistemiMigrazioneList().getTable() != null
                        && indMap.containsKey("sysMigr")) {
                    getForm().getSistemiMigrazioneList().getTable()
                            .setCurrentRowIndex(indMap.get("sysMigr"));
                    getForm().getSistemiMigrazioneList().getTable()
                            .setPageSize(indMap.get("sysMigrPS"));
                }
                if (getForm().getEnteConvenzOrgList().getTable() != null
                        && indMap.containsKey("enteConvenz")) {
                    getForm().getEnteConvenzOrgList().getTable()
                            .setCurrentRowIndex(indMap.get("enteConvenz"));
                    getForm().getEnteConvenzOrgList().getTable()
                            .setPageSize(indMap.get("enteConvenzPS"));
                }
                if (getForm().getCorrispondenzePingList().getTable() != null
                        && indMap.containsKey("corrPing")) {
                    getForm().getCorrispondenzePingList().getTable()
                            .setCurrentRowIndex(indMap.get("corrPing"));
                    getForm().getCorrispondenzePingList().getTable()
                            .setPageSize(indMap.get("corrPingPS"));
                }
            }

            if ("1".equals(getForm().getInsStruttura().getFl_cessato().parse())) {
                getForm().getRegistroUnitaDocList().setUserOperations(true, false, false, false);
                getForm().getTipoUnitaDocList().setUserOperations(true, false, false, false);
                getForm().getTipoDocList().setUserOperations(true, false, false, false);
                getForm().getTipoFascicoloList().setUserOperations(true, false, false, false);
                getForm().getTipoStrutDocList().setUserOperations(true, false, false, false);
                getForm().getTipoRapprCompList().setUserOperations(true, false, false, false);
                getForm().getCriteriRaggruppamentoList().setUserOperations(true, false, false,
                        false);
                getForm().getTitolariList().setUserOperations(true, false, false, false);
                getForm().getTipologieSerieList().setUserOperations(true, false, false, false);
                getForm().getSubStrutList().setUserOperations(true, false, false, false);
                getForm().getSistemiMigrazioneList().setUserOperations(true, false, false, false);
                getForm().getEnteConvenzOrgList().setUserOperations(true, false, false, false);
                getForm().getCorrispondenzePingList().setUserOperations(true, false, false, false);
            } else {
                getForm().getRegistroUnitaDocList().setUserOperations(true, true, true, true);
                getForm().getTipoUnitaDocList().setUserOperations(true, true, true, true);
                getForm().getTipoDocList().setUserOperations(true, true, true, true);
                getForm().getTipoFascicoloList().setUserOperations(true, true, true, true);
                getForm().getTipoStrutDocList().setUserOperations(true, true, true, true);
                getForm().getTipoRapprCompList().setUserOperations(true, true, true, true);
                getForm().getCriteriRaggruppamentoList().setUserOperations(true, true, true, true);
                getForm().getTitolariList().setUserOperations(true, true, true, true);
                getForm().getTipologieSerieList().setUserOperations(true, true, true, true);
                getForm().getSubStrutList().setUserOperations(true, true, true, true);
                getForm().getSistemiMigrazioneList().setUserOperations(true, true, true, true);
                getForm().getEnteConvenzOrgList().setUserOperations(true, true, true, true);
                getForm().getCorrispondenzePingList().setUserOperations(true, true, true, true);
            }
        }
    }

    public void loadXsdTipoUd() throws EMFError {

        StrutDatiSpecForm form = new StrutDatiSpecForm();
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        getForm().getTipoUnitaDocList().getTable().setCurrentRowIndex(nr);
        /*
         * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che
         * porterà nella nuova action
         */
        BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
        BigDecimal idStrut = rowBean.getBigDecimal("id_strut");
        form.getIdList().getId_strut().setValue(idStrut.toString());

        StringBuilder string = new StringBuilder(
                "?operation=listNavigationOnClick&navigationEvent=dettaglioView&table="
                        + getForm().getXsdDatiSpecList().getName() + "&riga=" + nr);

        DecXsdDatiSpecRowBean xsdRow = datiSpecEjb.getLastDecXsdDatiSpecRowBean(idStrut,
                ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                        .getCurrentRow()).getIdTipoUnitaDoc(),
                null, null);
        DecXsdDatiSpecTableBean xsdTable = new DecXsdDatiSpecTableBean();
        xsdTable.add(xsdRow);
        xsdTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        form.getXsdDatiSpecList().setTable(xsdTable);

        string.append("&idTipoUnitaDoc=").append(((DecTipoUnitaDocRowBean) getForm()
                .getTipoUnitaDocList().getTable().getCurrentRow()).getIdTipoUnitaDoc().intValue());
        string.append("&cessato=").append(getForm().getInsStruttura().getFl_cessato().parse());
        form.getIdList().getId_tipo_unita_doc().setValue(((DecTipoUnitaDocRowBean) getForm()
                .getTipoUnitaDocList().getTable().getCurrentRow()).getIdTipoUnitaDoc().toString());

        getSession().setAttribute("lastPage", "tipoUnitaDoc");
        form.getTipoUdRif().getNm_tipo_unita_doc().setValue(((DecTipoUnitaDocRowBean) getForm()
                .getTipoUnitaDocList().getTable().getCurrentRow()).getNmTipoUnitaDoc());

        // form = form nuova
        form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
        OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
        form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " (" + getForm().getInsStruttura().getDs_strut().parse() + ")");

        this.setInsertAction(false);
        this.setEditAction(false);
        this.setDeleteAction(false);
        redirectToAction(Application.Actions.STRUT_DATI_SPEC, string.toString(), form);

    }

    public void loadXsdTipoDoc() throws EMFError {

        StrutDatiSpecForm form = new StrutDatiSpecForm();

        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        getForm().getTipoDocList().getTable().setCurrentRowIndex(nr);
        /*
         * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che
         * porterà nella nuova action
         */
        BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
        BigDecimal idStrut = rowBean.getBigDecimal("id_strut");
        form.getIdList().getId_strut().setValue(idStrut.toString());

        StringBuilder string = new StringBuilder(
                "?operation=listNavigationOnClick&navigationEvent=dettaglioView&table="
                        + getForm().getXsdDatiSpecList().getName() + "&riga=" + nr);

        DecXsdDatiSpecRowBean xsdRow = datiSpecEjb.getLastDecXsdDatiSpecRowBean(idStrut, null,
                ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                        .getIdTipoDoc(),
                null);
        DecXsdDatiSpecTableBean xsdTable = new DecXsdDatiSpecTableBean();
        xsdTable.add(xsdRow);
        xsdTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        form.getXsdDatiSpecList().setTable(xsdTable);

        string.append("&idTipoDoc=")
                .append(((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                        .getIdTipoDoc().intValue());
        string.append("&cessato=").append(getForm().getInsStruttura().getFl_cessato().parse());
        form.getIdList().getId_tipo_doc().setValue(
                ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                        .getIdTipoDoc().toString());

        getSession().setAttribute("lastPage", "tipoDoc");
        form.getTipoDocRif().getNm_tipo_doc().setValue(
                ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                        .getNmTipoDoc());

        // form = form nuova
        form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
        OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
        form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());

        this.setInsertAction(false);
        this.setEditAction(false);
        this.setDeleteAction(false);
        redirectToAction(Application.Actions.STRUT_DATI_SPEC, string.toString(), form);

    }

    @Override
    public void confermaSceltaDup() throws EMFError {

        getMessageBox().clear();
        CheckDuplicaStruttura check = getForm().getCheckDuplicaStruttura();
        check.post(getRequest());
        getForm().getInsStruttura().post(getRequest());
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");

        if (getRequest().getParameter("Check_dup_strut") != null
                && getRequest().getParameter("Check_sost_strut") != null) {
            getMessageBox().addError("Selezionare solo una funzione");
        } else if (getRequest().getParameter("Check_dup_strut") == null
                && getRequest().getParameter("Check_sost_strut") == null) {
            getMessageBox().addError("Selezionare una funzione");
        }
        if (getMessageBox().isEmpty()) {

            SalvaStrutturaDto salva = getSalvaStrutturaDto();

            initStrutComboBox(null);

            getForm().getInsStruttura().getId_ambiente_rif().setHidden(false);
            getForm().getInsStruttura().getId_ente_rif().setHidden(false);
            getForm().getInsStruttura().getId_ente().setHidden(false);
            getForm().getInsStruttura().getNm_strut().setHidden(false);
            getForm().getInsStruttura().getDs_strut().setHidden(false);
            getForm().getInsStruttura().getView_nm_amb().setHidden(true);
            getForm().getInsStruttura().getView_nm_ente().setHidden(true);
            getForm().getInsStruttura().getView_nm_strut().setHidden(true);

            OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut, null);
            getForm().getInsStruttura().copyFromBean(strutRowBean);

            getForm().getInsStruttura().setStatus(Status.insert);
            getForm().getInsStruttura().setEditMode();
            getForm().getInsStruttura().getDs_strut().clear();
            getForm().getInsStruttura().getNm_strut().clear();
            getForm().getInsStruttura().getCd_strut_normaliz().clear();
            getForm().getInsStruttura().getId_ente().clear();
            getForm().getInsStruttura().getId_ambiente_ente_convenz().clear();
            getForm().getInsStruttura().getId_ente_convenz().clear();

            // Apro le sezioni relative ai parametri
            getForm().getStrutFlags().setLoadOpened(true);
            getForm().getParametriUnitaDoc().setLoadOpened(true);
            getForm().getParametriFascicolo().setLoadOpened(true);

            getForm().getStruttureList().setStatus(Status.insert);

            try {
                // Lista parametri
                loadListeParametriStrutturaPerDupImp(strutRowBean, true, true, null);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getMessage());
            }

            DecodeMap mappaAmbienti = new DecodeMap();
            BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                    getUser().getIdUtente(),
                    configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
            ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
            ambienteTableBean.sort();
            mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
            getForm().getInsStruttura().getId_ambiente_rif().setDecodeMap(mappaAmbienti);
            getForm().getInsStruttura().getId_ente_rif().setDecodeMap(new DecodeMap());

            precompilaDateDuplicaImporta();
            getSession().setAttribute(SES_ATTRIB_VALORI_SALVATAGGIO, salva);

            if (getRequest().getParameter("Check_dup_strut") != null) {
                // Duplica Struttura NON STANDARD
                if (strutRowBean.getIdCategStrut() != null) {
                    getForm().getInsStruttura().getId_categ_strut()
                            .setValue(strutRowBean.getIdCategStrut().toString());
                }
                getSession().setAttribute(SES_ATTRIB_SALVATAGGIO,
                        TipoSalvataggio.DUPLICA_NON_STANDARD);
            } else {
                // Duplica Struttura STANDARD
                getSession().setAttribute("idStrutToCopy", idStrut);
                getSession().setAttribute(SES_ATTRIB_SALVATAGGIO, TipoSalvataggio.DUPLICA_STANDARD);
                getForm().getInsStruttura().getFl_template().setViewMode();
                getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
            }
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);

        } else {
            forwardToPublisher(Application.Publisher.DUPLICA_STRUTTURA);
        }

    }

    private void precompilaDateDuplicaImporta() {
        // Precompilo le date inizio e fine della struttura e dell'ente convenzionato
        Calendar data = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        Date today = data.getTime();
        String todayString = formatter.format(today);
        getForm().getInsStruttura().getDt_ini_val_strut().setValue(todayString);
        data.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        Date endWorld = data.getTime();
        String endWorldString = formatter.format(endWorld);
        getForm().getInsStruttura().getDt_fine_val_strut().setValue(endWorldString);
    }

    public void sostituisciStrutturaOperation() {

        getMessageBox().clear();
        // carico la maschera di compilazione con tutti i dati tranne ds e nm
        String riga = getRequest().getParameter("riga");
        Integer nr = Integer.parseInt(riga);
        getForm().getStruttureList().getTable().setCurrentRowIndex(nr);

        if (struttureEjb
                .isStrutUsedForVers(getForm().getStruttureList().getTable().getCurrentRow())) {
            getMessageBox().addError("Struttura utilizzata per versamenti, impossibile sostituire");
        }

        if (getMessageBox().isEmpty()) {
            getForm().getInsStruttura().setEditMode();
            getForm().getInsStruttura().setStatus(Status.insert);
            getForm().getStruttureList().setStatus(Status.insert);

            for (AplParamApplicRowBean row : (AplParamApplicTableBean) getForm()
                    .getParametriConservazioneStrutturaList().getTable()) {
                if (row.getNmParamApplic().equals("FL_ABILITA_CONTR_FMT_NUM")) {
                    row.setString("ds_valore_param_applic_strut_cons", "true");
                }
            }
            populateComboStrut(getForm().getStruttureList().getTable().getCurrentRow());
            getSession().setAttribute("idStrutToSub",
                    ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                            .getBigDecimal("id_strut"));
            getSession().setAttribute(SES_ATTRIB_SALVATAGGIO, TipoSalvataggio.DUPLICA_STANDARD);
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
        }
    }

    public void populateComboStrut(BaseRowInterface strutRowBean) {

        OrgEnteRowBean orgEnteRowBean = struttureEjb
                .getOrgEnteRowBean(strutRowBean.getBigDecimal("id_ente"));
        // Combo
        DecodeMap mappaEnti = new DecodeMap();
        BaseTableInterface orgEnteTableBean = ambienteEjb.getEntiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC), null,
                null, null);
        orgEnteTableBean.addSortingRule("nm_ente", SortingRule.ASC);
        orgEnteTableBean.sort();
        mappaEnti.populatedMap(orgEnteTableBean, "id_ente", "nm_ente");
        getForm().getInsStruttura().getId_ente_rif().setDecodeMap(mappaEnti);
        getForm().getInsStruttura().getId_ente_rif()
                .setValue(orgEnteRowBean.getIdEnte().toString());

        DecodeMap mappaAmbienti = new DecodeMap();
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
        getForm().getInsStruttura().getId_ambiente_rif().setDecodeMap(mappaAmbienti);

        getForm().getInsStruttura().getId_ambiente_rif()
                .setValue(orgEnteRowBean.getIdAmbiente().toString());
        getForm().getInsStruttura().getNm_strut().setValue(strutRowBean.getString("nm_strut"));
        getForm().getInsStruttura().getDs_strut().setValue(strutRowBean.getString("ds_strut"));
    }

    @Secure(action = "Menu.Amministrazione.GestioneAmbitoTerritoriale")
    public void gestioneAmbitoTerritoriale() {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneAmbitoTerritoriale");

        getForm().getAmbitoTerritoriale().setViewMode();

        DecodeMap tipiMap = new DecodeMap();
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();

        for (Enum<WebConstants.TipoAmbitoTerritoriale> tipo : WebConstants.TipoAmbitoTerritoriale
                .values()) {
            br.setString("ti_ambito_territ", tipo.name());
            bt.add(br);
        }
        tipiMap.populatedMap(bt, "ti_ambito_territ", "ti_ambito_territ");
        getForm().getAmbitoTerritoriale().getTi_ambito_territ().setDecodeMap(tipiMap);

        OrgAmbitoTerritTableBean table = ambienteEjb.getOrgAmbitoTerritTableBean(null);
        getForm().getGestAmbTree().setTable(table);

        forwardToPublisher(Application.Publisher.GESTIONE_AMBITO_TERRITORIALE);
    }

    @Secure(action = "Menu.Amministrazione.GestioneCategorieTipoUd")
    public void gestioneCategorieTipoUd() {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneCategorieTipoUd");

        getForm().getCategorieTipoUd().setViewMode();
        forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);

        reloadCategTipoUdTreeCombo();
    }

    @Override
    public JSONObject triggerAmbitoTerritorialeTi_ambito_territOnTrigger() throws EMFError {
        getForm().getAmbitoTerritoriale().post(getRequest());

        populateIdAmbitoTerritPadreComboBox(
                getForm().getAmbitoTerritoriale().getTi_ambito_territ().parse());

        return getForm().getAmbitoTerritoriale().asJSON();
    }

    public void createNode() {
        String publisher = getLastPublisher();
        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {
            getForm().getCategorieTipoUd().clear();
            getForm().getCategorieTipoUd().setEditMode();
            getForm().getCategorieTipoUd().setStatus(Status.insert);
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    public void confRemove() {
        getRequest().setAttribute("confRemove", true);
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    public void deleteNode() throws EMFError {

        String publisher = getLastPublisher();
        getMessageBox().clear();

        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {

            try {
                tipoUnitaDocEjb.deleteDecCategTipoUnitaDoc(
                        getForm().getCategorieTipoUd().getCd_categ_tipo_unita_doc().parse());
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }

            reloadCategTipoUdTreeCombo();

            getForm().getCategorieTipoUd().clear();
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    public void moveNode() {
        getMessageBox().clear();

        String publisher = getLastPublisher();

        BigDecimal nodeId = new BigDecimal(getRequest().getParameter("nodeId"));
        BigDecimal nodeDestId = null;
        // se l'idPadre è nullo, non lo inserisco
        if (!getRequest().getParameter("nodeDestId").equals("0")) {
            nodeDestId = new BigDecimal(getRequest().getParameter("nodeDestId"));
        }

        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {

            getForm().getCategorieTipoUd().setViewMode();
            try {
                tipoUnitaDocEjb.moveDecCategTipoUDNode(nodeId, nodeDestId);
            } catch (ParerUserError ie) {
                getMessageBox().addError(ie.getDescription());
            }

            reloadCategTipoUdTreeCombo();
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    public void loadNode() {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.GESTIONE_AMBITO_TERRITORIALE)) {

            getForm().getAmbitoTerritoriale().setViewMode();
            OrgAmbitoTerritRowBean orgAmbitoTerritRowBean = ambienteEjb
                    .getOrgAmbitoTerritRowBean(getRequest().getParameter("nodeName"));
            populateIdAmbitoTerritPadreComboBox(orgAmbitoTerritRowBean.getTiAmbitoTerrit());

            try {
                getForm().getAmbitoTerritoriale().copyFromBean(orgAmbitoTerritRowBean);
                if (orgAmbitoTerritRowBean.getTiAmbitoTerrit().equals("REGIONE/STATO")) {
                    getForm().getAmbitoTerritoriale().getTi_ambito_territ()
                            .setValue("REGIONE_STATO");
                }
            } catch (EMFError ex) {
                log.error(ECCEZIONE_GENERICA, ex);
                getForm().getAmbitoTerritoriale().clear();
            }
            getForm().getAmbitoTerritoriale().getId_to_update()
                    .setValue(orgAmbitoTerritRowBean.getIdAmbitoTerrit().toString());

            forwardToPublisher(Application.Publisher.GESTIONE_AMBITO_TERRITORIALE);

        } else if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {

            getForm().getCategorieTipoUd().setViewMode();
            DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean = tipoUnitaDocEjb
                    .getDecCategTipoUnitaDocRowBean(getRequest().getParameter("nodeName"));
            try {
                getForm().getCategorieTipoUd().copyFromBean(categTipoUnitaDocRowBean);
            } catch (EMFError ex) {
                log.error(ECCEZIONE_GENERICA, ex);
                getForm().getCategorieTipoUd().clear();
            }
            getForm().getCategorieTipoUd().getId_to_update()
                    .setValue(categTipoUnitaDocRowBean.getIdCategTipoUnitaDoc().toString());
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    @Override
    public void annulla() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {
            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        } else if (publisher.equals(Application.Publisher.CREA_STRUTTURA)) {
            // Nascondo i bottoni con javascript disattivato
            getForm().getTitolarioCustomMessageButtonList().setViewMode();
            forwardToPublisher(publisher);
        }
    }

    public void updateNode() throws EMFError {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD)) {

            if (getForm().getCategorieTipoUd().getId_to_update().parse() == null) {
                getMessageBox().addError("Selezionare un nodo");
            } else {
                getForm().getCategorieTipoUd().setEditMode();
                getForm().getCategorieTipoUd().setStatus(Status.update);
                getSession().setAttribute("idToUpdate",
                        getForm().getCategorieTipoUd().getId_to_update().parse());
            }

            forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
        }
    }

    public void populateIdAmbitoTerritPadreComboBox(String tipo) {
        OrgAmbitoTerritTableBean tableBean = new OrgAmbitoTerritTableBean();

        if (tipo != null) {
            if (tipo.equals(WebConstants.TipoAmbitoTerritoriale.PROVINCIA.name())) {
                tableBean = ambienteEjb.getOrgAmbitoTerritTableBean("REGIONE/STATO");
            }
            if (tipo.equals(WebConstants.TipoAmbitoTerritoriale.FORMA_ASSOCIATA.name())) {
                tableBean = ambienteEjb.getOrgAmbitoTerritTableBean("PROVINCIA");
            }
        }
        DecodeMap map = new DecodeMap();
        map.populatedMap(tableBean, "id_ambito_territ", "cd_ambito_territ");
        getForm().getAmbitoTerritoriale().getId_ambito_territ_padre().setDecodeMap(map);
    }

    @Override
    public void salvaCategorieTipoUd() throws EMFError {

        getMessageBox().clear();
        getForm().getCategorieTipoUd().post(getRequest());

        if (getForm().getCategorieTipoUd().validate(getMessageBox())) {
            if (getForm().getCategorieTipoUd().getCd_categ_tipo_unita_doc().parse() == null) {
                getMessageBox().addError("Inserire codice categoria</br>");
            }
            if (getForm().getCategorieTipoUd().getDs_categ_tipo_unita_doc().parse() == null) {
                getMessageBox().addError("Inserire descrizione categoria</br>");
            }

            try {
                if (getMessageBox().isEmpty()) {
                    DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean = new DecCategTipoUnitaDocRowBean();
                    getForm().getCategorieTipoUd().copyToBean(categTipoUnitaDocRowBean);
                    if (getForm().getCategorieTipoUd().getStatus().equals(Status.insert)) {
                        tipoUnitaDocEjb.insertDecCategTipoUnitaDoc(categTipoUnitaDocRowBean);
                    }
                    if (getForm().getCategorieTipoUd().getStatus().equals(Status.update)) {
                        BigDecimal idToUpdate = (BigDecimal) getSession()
                                .getAttribute("idToUpdate");
                        tipoUnitaDocEjb.updateDecCategTipoUnitaDoc(idToUpdate,
                                categTipoUnitaDocRowBean);
                    }
                    getForm().getCategorieTipoUd().setViewMode();
                    getForm().getCategorieTipoUd().clear();
                    reloadCategTipoUdTreeCombo();
                }

            } catch (ParerUserError ie) {
                getMessageBox().addError(ie.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.GESTIONE_CATEGORIE_TIPO_UD);
    }

    /**
     * Reload dell'albero delle categorie tipo ud e della combo nel dettaglio
     */
    public void reloadCategTipoUdTreeCombo() {
        DecCategTipoUnitaDocTableBean tableBean = tipoUnitaDocEjb
                .getDecCategTipoUnitaDocTableBean(false);
        getForm().getGestCatTiUdTree().setTable(tableBean);

        DecodeMap map = new DecodeMap();
        DecCategTipoUnitaDocTableBean tableCategFirstLevel = tipoUnitaDocEjb
                .getDecCategTipoUnitaDocTableBean(true);
        map.populatedMap(tableCategFirstLevel, "id_categ_tipo_unita_doc",
                "cd_categ_tipo_unita_doc");
        getForm().getCategorieTipoUd().getId_categ_tipo_unita_doc_padre().setDecodeMap(map);
    }

    @Secure(action = "Menu.Amministrazione.GestioneCategorieStrutture")
    public void ricercaCategorieStrutture() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneCategorieStrutture");
        getForm().getCategorieStrutture().clear();
        getForm().getCategorieStrutture().getRicercaCategorieStrutButton().setEditMode();
        getForm().getCategorieStrutture().setEditMode();
        forwardToPublisher(Application.Publisher.CATEGORIE_STRUTTURE_RICERCA);
    }

    @Override
    public void ricercaCategorieStrutButton() throws EMFError {

        getForm().getCategorieStrutture().post(getRequest());

        OrgCategStrutRowBean categStrutRowBean = new OrgCategStrutRowBean();
        getForm().getCategorieStrutture().copyToBean(categStrutRowBean);

        OrgCategStrutTableBean orgCategStrutTableBean = struttureEjb
                .getOrgCategStrutTableBean(categStrutRowBean);
        getForm().getCategorieStruttureList().setTable(orgCategStrutTableBean);
        getForm().getCategorieStruttureList().getTable()
                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getCategorieStruttureList().getTable().first();

        forwardToPublisher(Application.Publisher.CATEGORIE_STRUTTURE_RICERCA);
    }

    private void salvaCategEnti() throws EMFError {

        getMessageBox().clear();
        boolean possoSalvare = true;
        getForm().getCategorieEnti().post(getRequest());
        if (getForm().getCategorieEnti().validate(getMessageBox())) {
            if (getForm().getCategorieEnti().getCd_categ_ente().parse() == null) {
                getMessageBox().addError("Errore di compilazione: inserire codice categoria </br>");
                possoSalvare = false;
            }
            if (getForm().getCategorieEnti().getDs_categ_ente().parse() == null) {
                getMessageBox()
                        .addError("Errore di compilazione: inserire descrizione categoria </br>");
                possoSalvare = false;
            }

            try {
                if (possoSalvare) {

                    OrgCategEnteRowBean categEnteRowBean = new OrgCategEnteRowBean();
                    getForm().getCategorieEnti().copyToBean(categEnteRowBean);

                    if (getForm().getCategorieEnti().getStatus().equals(Status.insert)) {
                        ambienteEjb.insertOrgCategEnte(categEnteRowBean);
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Nuova categoria enti salvata con successo"));

                    } else if (getForm().getCategorieEnti().getStatus().equals(Status.update)) {

                        BigDecimal idCategEnte = ((OrgCategEnteRowBean) getForm()
                                .getCategorieEntiList().getTable().getCurrentRow())
                                .getIdCategEnte();
                        ambienteEjb.updateOrgCategEnte(idCategEnte, categEnteRowBean);
                        getForm().getCategorieEntiList().getTable().setCurrentRowIndex(
                                getForm().getCategorieEntiList().getTable().getCurrentRowIndex());
                        getMessageBox().addMessage(new Message(MessageLevel.INF,
                                "Update della categoria enti effettuato con successo"));

                    }

                    getForm().getCategorieEnti().setViewMode();
                    getForm().getCategorieEnti().setStatus(Status.view);
                    getForm().getCategorieEntiList().setStatus(Status.view);
                    getMessageBox().setViewMode(ViewMode.plain);

                }
                forwardToPublisher(Application.Publisher.CATEGORIE_ENTI_DETAIL);
            } catch (ParerUserError ie) {
                getMessageBox().addError(ie.getDescription());
                forwardToPublisher(Application.Publisher.CATEGORIE_ENTI_DETAIL);
            }
        }
    }

    @Secure(action = "Menu.Amministrazione.GestioneCategorieEnti")
    public void ricercaCategorieEnti() {

        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneCategorieEnti");

        getForm().getCategorieEnti().clear();
        getForm().getCategorieEnti().getRicercaCategorieEnteButton().setEditMode();
        getForm().getCategorieEnti().setEditMode();

        forwardToPublisher(Application.Publisher.CATEGORIE_ENTI_RICERCA);
    }

    /* CATEGORIE ENTI */
    @Override
    public void ricercaCategorieEnteButton() throws EMFError {

        getForm().getCategorieEnti().post(getRequest());

        OrgCategEnteRowBean categEnteRowBean = new OrgCategEnteRowBean();
        getForm().getCategorieEnti().copyToBean(categEnteRowBean);

        OrgCategEnteTableBean orgCategEnteTableBean = ambienteEjb
                .getOrgCategEnteTableBean(categEnteRowBean);
        getForm().getCategorieEntiList().setTable(orgCategEnteTableBean);
        getForm().getCategorieEntiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getCategorieEntiList().getTable().first();

        forwardToPublisher(Application.Publisher.CATEGORIE_ENTI_RICERCA);
    }

    private List<String> getListaStrutTemplate() {
        List<String> lista = null;
        OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(null, null, null,
                true);
        if (!strutTableBean.isEmpty()) {
            Iterator<OrgStrutRowBean> iter = strutTableBean.iterator();
            lista = new ArrayList<>();
            while (iter.hasNext()) {
                OrgStrutRowBean orgStrutRowBean = iter.next();
                lista.add(orgStrutRowBean.getIdStrut().toString());

            }
        }
        return lista;
    }

    private void goToRicercaStrutture() {
        getSession().setAttribute("id_struttura_lavorato", null);
        forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
    }

    @Override
    public void importazioneTitolario() throws EMFError {

        StrutTitolariForm form = new StrutTitolariForm();

        form.getTitolariList().setTable(getForm().getTitolariList().getTable());
        form.getTitolariList().setStatus(getForm().getTitolariList().getStatus());

        getForm().getTitolariList().setStatus(Status.view);

        // form = form nuova
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        form.getStrutRif().getId_strut().setValue(idStrut.toPlainString());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " - " + getForm().getInsStruttura().getDs_strut().parse());
        form.getStrutRif().getId_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());

        redirectToAction(Application.Actions.STRUT_TITOLARI, "?operation=loadImportaTitolario",
                form);
    }

    @Override
    public void inserimentoManualeTitolario() throws EMFError {
        StrutTitolariForm form = new StrutTitolariForm();

        form.getTitolariList().setTable(getForm().getTitolariList().getTable());
        form.getTitolariList().setStatus(getForm().getTitolariList().getStatus());

        getForm().getTitolariList().setStatus(Status.view);
        String updateRiga = "";
        if (form.getTitolariList().getStatus().equals(Status.update)) {
            int riga = getForm().getTitolariList().getTable().getCurrentRowIndex();
            updateRiga = "&riga=" + riga;
        }

        // form = form nuova
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        form.getStrutRif().getId_strut().setValue(idStrut.toPlainString());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " - " + getForm().getInsStruttura().getDs_strut().parse());
        form.getStrutRif().getId_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());

        redirectToAction(Application.Actions.STRUT_TITOLARI, "?operation=loadWizard" + updateRiga,
                form);
    }

    private void reloadTipoRapprCompLists(BigDecimal idTipoRapprComp) throws EMFError {

        getForm().getTipoRapprComp().setViewMode();
        getForm().getTipoRapprComp().setStatus(Status.view);
        getForm().getTipoRapprCompList().setStatus(Status.view);

        DecTipoRapprCompRowBean decTipoRapprCompRowBean = tipoRapprEjb
                .getDecTipoRapprCompRowBean(idTipoRapprComp, null);

        getForm().getTipoRapprComp().copyFromBean(decTipoRapprCompRowBean);

        /* Lista componenti */
        DecTipoRapprAmmessoTableBean tipoRapprAmmessoTableBean = tipoStrutDocEjb
                .getDecTipoRapprAmmessoTableBeanByIdTipoRapprComp(idTipoRapprComp);
        getForm().getTipoCompAmmessoDaTipoRapprCompList().setTable(tipoRapprAmmessoTableBean);
        getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable().first();
        getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable()
                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        /* Lista trasformatori */
        DecTrasformTipoRapprRowBean decTrasformTipoRapprRowBean = new DecTrasformTipoRapprRowBean();

        decTrasformTipoRapprRowBean.setIdTipoRapprComp(idTipoRapprComp);
        DecTrasformTipoRapprTableBean decTrasformTipoRapprTableBean = tipoRapprEjb
                .getDecTrasformTipoRapprTableBean(decTipoRapprCompRowBean);

        getForm().getTrasformTipoRapprList().setTable(decTrasformTipoRapprTableBean);
        getForm().getTrasformTipoRapprList().getTable().first();
        getForm().getTrasformTipoRapprList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipoRapprComp().getLogEventiTipoRapprComp().setEditMode();
        if ("1".equals(getForm().getInsStruttura().getFl_cessato().parse())) {
            getForm().getTipoCompAmmessoDaTipoRapprCompList().setUserOperations(true, false, false,
                    false);
            getForm().getTrasformTipoRapprList().setUserOperations(true, false, false, false);
        } else {
            getForm().getTipoCompAmmessoDaTipoRapprCompList().setUserOperations(true, true, true,
                    true);
            getForm().getTrasformTipoRapprList().setUserOperations(true, true, true, true);
        }
    }

    private void redirectToSubStrutPage(String action) throws EMFError {
        // Qualsiasi azione sia, la gestirà nell'action
        SubStruttureForm form = new SubStruttureForm();

        form.getSubStrutList().setTable(getForm().getSubStrutList().getTable());

        int riga = getForm().getSubStrutList().getTable().getCurrentRowIndex();

        // form = form nuova
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " - " + getForm().getInsStruttura().getDs_strut().parse());
        form.getStrutRif().getId_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());
        form.getStrutRif().getFl_template()
                .setValue(getForm().getInsStruttura().getFl_template().getValue());
        form.getStrutRif().getId_strut().setValue(
                ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                        .getBigDecimal("id_strut").toPlainString());

        redirectToAction(Application.Actions.SUB_STRUTTURE,
                "?operation=listNavigationOnClick&navigationEvent=" + action + "&table="
                        + form.getSubStrutList().getName() + "&riga=" + riga + "&cessato="
                        + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    @Override
    public void updateSubStrutList() throws EMFError {
        /*
         * Se l'azione è update o insert, verifico nel caso di struttura template che esista solo la
         * struttura di default e impongo che non se ne possano creare altre
         */
        boolean isTemplate = getForm().getInsStruttura().getFl_template().parse().equals("1");
        if (isTemplate) {
            getMessageBox().addError(
                    "La struttura \u00E8 di tipo template, impossibile modificare le sottostrutture");
            forwardToPublisher(getLastPublisher());
        } else {
            redirectToSubStrutPage(NE_DETTAGLIO_UPDATE);
        }
    }

    @Override
    public void deleteSubStrutList() throws EMFError {
        boolean isTemplate = getForm().getInsStruttura().getFl_template().parse().equals("1");
        if (isTemplate) {
            getMessageBox().addError(
                    "La struttura \u00E8 di tipo template, impossibile eliminare le sottostrutture");
            forwardToPublisher(getLastPublisher());
        } else {
            BigDecimal idInserito = (BigDecimal) getSession().getAttribute("elementoInserito");
            if (getForm().getSubStrutList().getTable().size() == 1 && idInserito == null) {
                getMessageBox().addError(
                        "\u00C8 obbligatoria la presenza di almeno una sottostruttura per ogni struttura");
                forwardToPublisher(getLastPublisher());
            } else {
                BigDecimal idSubStrut = idInserito != null ? idInserito
                        : ((OrgSubStrutRowBean) getForm().getSubStrutList().getTable()
                                .getCurrentRow()).getIdSubStrut();
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
                                    configHelper.getValoreParamApplicByApplic(
                                            CostantiDB.ParametroAppl.NM_APPLIC),
                                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(
                                    getForm(), getForm().getSubStrutList()));
                            param.setTransactionLogContext(
                                    sacerLogEjb.getNewTransactionLogContext());
                            subStrutEjb.deleteOrgSubStrut(param, idSubStrut.longValue(), false);
                            getMessageBox().addInfo("Sottostruttura eliminata con successo");
                            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                            getSession().removeAttribute("elementoInserito");
                            getForm().getVisStrutture().setEditMode();
                            initRicercaStruttureCombo();
                            forwardToPublisher(getLastPublisher());
                        } catch (ParerUserError ex) {
                            getMessageBox().addError(ex.getDescription());
                            forwardToPublisher(getLastPublisher());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTitolariList() throws EMFError {
        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable()
                .getCurrentRow();
        if (titolariEjb.isTitolarioChiuso(row.getIdTitol())) {
            getMessageBox().addError(
                    "Il titolario \u00E8 gi\u00E0 stato chiuso, non \u00E8 possibile modificarlo");
        } else {
            getForm().getTitolarioCustomMessageButtonList().setEditMode();
            getRequest().setAttribute("customBox", true);
            getForm().getTitolariList().setStatus(Status.update);
        }
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    @Override
    public void updateTipologieSerieList() throws EMFError {

        redirectToTipologiaSeriaPage(getNavigationEvent());
    }

    @Override
    public void deleteTipologieSerieList() throws EMFError {
        getMessageBox().clear();
        DecTipoSerieRowBean tipoSerieRowBean;
        tipoSerieRowBean = (DecTipoSerieRowBean) getForm().getTipologieSerieList().getTable()
                .getCurrentRow();
        int rowIndex = getForm().getTipologieSerieList().getTable().getCurrentRowIndex();
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getTipologieSerieList()));
            tipoSerieEjb.deleteDecTipoSerie(param, tipoSerieRowBean.getIdTipoSerie().longValue());
            getForm().getTipologieSerieList().getTable().remove(rowIndex);
            getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                    "Tipologia serie eliminata con successo"));
            forwardToPublisher(getLastPublisher());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    private void redirectToTitolarioPage(String action) throws EMFError {
        StrutTitolariForm form = new StrutTitolariForm();

        form.getTitolariList().setTable(getForm().getTitolariList().getTable());
        form.getTitolariList()
                .setFilterValidRecords(getForm().getTitolariList().isFilterValidRecords());

        int riga = getForm().getTitolariList().getTable().getCurrentRowIndex();

        // form = form nuova
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        form.getStrutRif().getId_strut().setValue(idStrut.toPlainString());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " - " + getForm().getInsStruttura().getDs_strut().parse());
        form.getStrutRif().getId_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());

        redirectToAction(Application.Actions.STRUT_TITOLARI,
                "?operation=listNavigationOnClick&navigationEvent=" + action + "&table="
                        + form.getTitolariList().getName() + "&riga=" + riga + "&cessato="
                        + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    private void redirectToTipologiaSeriaPage(String action) throws EMFError {
        StrutSerieForm form = new StrutSerieForm();
        BaseTableInterface<?> table = getForm().getTipologieSerieList().getTable();
        form.getTipologieSerieList().setTable(table);

        int riga = table.getCurrentRowIndex();

        // form = form nuova
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        form.getStrutRif().getId_strut().setValue(idStrut.toPlainString());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " - " + getForm().getInsStruttura().getDs_strut().parse());
        form.getStrutRif().getId_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());

        // Setto i valori delle combo ambiente/ente/struttura
        try {
            ActionUtils utile = new ActionUtils();
            utile.initGenericComboAmbienteEnteStruttura(form.getFiltriTipologieSerie(),
                    getUser().getIdUtente(), idStrut, Boolean.TRUE);
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel recupero delle strutture abilitate");
        }

        redirectToAction(Application.Actions.STRUT_SERIE,
                "?operation=listNavigationOnClick&navigationEvent=" + action + "&table="
                        + form.getTipologieSerieList().getName() + "&riga=" + riga + "&cessato="
                        + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    private StrutTipiForm prepareRedirectToStrutTipi() {
        StrutTipiForm form = new StrutTipiForm();
        BigDecimal idStrut = (getForm().getStruttureList().getTable().getCurrentRow())
                .getBigDecimal("id_strut");
        /* Mi porto dietro nella idList l'id della struttura cui faccio riferimento */
        form.getIdList().getId_strut().setValue(idStrut.toString());
        /* Recupero la struttura */
        BaseRowInterface strutBean = getForm().getStruttureList().getTable().getCurrentRow();
        /* Setto dei valori che potranno tornarmi utili in StrutTipi */
        form.getStrutRif().getNm_strut().setValue(strutBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(strutBean.getString("ds_strut"));
        OrgEnteRowBean enteBean = struttureEjb
                .getOrgEnteRowBean(strutBean.getBigDecimal("id_ente"));
        OrgAmbienteRowBean ambienteBean = struttureEjb
                .getOrgAmbienteRowBean(enteBean.getIdAmbiente());
        form.getStrutRif().getId_ente().setValue(enteBean.getNmEnte());
        form.getStrutRif().getStruttura().setValue(
                strutBean.getString("nm_strut") + " (" + strutBean.getString("ds_strut") + ")");
        form.getStrutRif().getNm_ambiente().setValue(ambienteBean.getNmAmbiente());
        return form;
    }

    private StrutTipiFascicoloForm prepareRedirectToStrutTipiFascicolo() {
        StrutTipiFascicoloForm form = new StrutTipiFascicoloForm();
        /* Recupero la struttura */
        BaseRowInterface strutBean = getForm().getStruttureList().getTable().getCurrentRow();
        /* Setto dei valori che potranno tornarmi utili in StrutTipiFascicolo */
        form.getStrutRif().getId_strut()
                .setValue(strutBean.getBigDecimal("id_strut").toPlainString());
        form.getStrutRif().getNm_strut().setValue(strutBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(strutBean.getString("ds_strut"));
        OrgEnteRowBean enteBean = struttureEjb
                .getOrgEnteRowBean(strutBean.getBigDecimal("id_ente"));
        OrgAmbienteRowBean ambienteBean = struttureEjb
                .getOrgAmbienteRowBean(enteBean.getIdAmbiente());
        form.getStrutRif().getId_ente().setValue(enteBean.getNmEnte());
        form.getStrutRif().getStruttura().setValue(
                strutBean.getString("nm_strut") + " (" + strutBean.getString("ds_strut") + ")");
        form.getStrutRif().getNm_ambiente().setValue(ambienteBean.getNmAmbiente());
        form.getStrutRif().getId_ambiente().setValue(ambienteBean.getIdAmbiente().toPlainString());
        return form;
    }

    private StrutTipoStrutForm prepareRedirectToStrutTipoStrut() throws EMFError {
        StrutTipoStrutForm form = new StrutTipoStrutForm();

        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");

        // salvo l'idStrut in modo da poterlo propagare più avanti se necessario
        form.getIdList().getId_strut().setValue(idStrut.toString());

        BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
        form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
        OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
        form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " (" + getForm().getInsStruttura().getDs_strut().parse() + ")");
        return form;
    }

    private StrutFormatoFileForm prepareRedirectToStrutFormatoFile() throws EMFError {
        StrutFormatoFileForm form = new StrutFormatoFileForm();

        BigDecimal idStrut = getForm().getStruttureList().getTable().getCurrentRow()
                .getBigDecimal("id_strut");

        // salvo l'idStrut in modo da poterlo propagare più avanti se necessario
        form.getIdList().getId_strut().setValue(idStrut.toString());

        BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();
        form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
        form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
        OrgEnteRowBean enteTemp = struttureEjb.getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
        form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " (" + getForm().getInsStruttura().getDs_strut().parse() + ")");
        return form;
    }

    private StrutDatiSpecForm prepareRedirectToStrutDatiSpec() throws EMFError {
        StrutDatiSpecForm form = new StrutDatiSpecForm();
        BigDecimal idStrut = getForm().getStruttureList().getTable().getCurrentRow()
                .getBigDecimal("id_strut");
        String nmSistemaMigraz = getForm().getGestioneXsdMigrazione().getNm_sistema_migraz()
                .getDecodedValue();
        if (StringUtils.isBlank(nmSistemaMigraz)) {
            getMessageBox().addError("Errore inatteso nel recupero del sistema di migrazione");
        }
        if (getMessageBox().isEmpty()) {

            Integer row = getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex();
            if (idStrut == null) {
                idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                        .getCurrentRow()).getBigDecimal("id_strut");
            }

            StringBuilder string = new StringBuilder(
                    "?operation=listNavigationOnClick&navigationEvent=" + getNavigationEvent()
                            + "&table=" + StrutDatiSpecForm.XsdDatiSpecList.NAME + "&riga="
                            + row.toString());
            form.getXsdDatiSpecList().setTable(getForm().getXsdDatiSpecList().getTable());

            // Non controllo il publisher da cui arrivo perchè sono sicuro di essere in quello
            // della
            // migrazione
            // controllo invece quale tabella di strutture ha richiamato la funzione, se quella
            // normale o quella di
            // duplica
            form.getIdList().getId_strut().setValue(idStrut.toString());
            form.getIdList().getNm_sys_migraz().setValue(nmSistemaMigraz);

            if (getForm().getXsdMigrStrutTab().getCurrentTab()
                    .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoDoc())) {
                form.getIdList().getNm_sacer_type().setValue("DOC");
                getSession().setAttribute("elementoRif", "Documento");

            } else if (getForm().getXsdMigrStrutTab().getCurrentTab()
                    .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoUnitaDoc())) {
                form.getIdList().getNm_sacer_type().setValue("UNI_DOC");
                getSession().setAttribute("elementoRif", "UD");

            } else if (getForm().getXsdMigrStrutTab().getCurrentTab()
                    .equals(getForm().getXsdMigrStrutTab().getXsdMigrTipoCompDoc())) {
                form.getIdList().getNm_sacer_type().setValue("COMP");
                getSession().setAttribute("elementoRif", "Componente");

            }
            getRequest().setAttribute("lastPage", "migraz");

            string.append("&idStrut=").append(idStrut);
            string.append("&cessato=").append(getForm().getInsStruttura().getFl_cessato().parse());
            // form = form nuova
            BaseRowInterface rowBean = getForm().getStruttureList().getTable().getCurrentRow();

            form.getStrutRif().getNm_strut().setValue(rowBean.getString("nm_strut"));
            form.getStrutRif().getDs_strut().setValue(rowBean.getString("ds_strut"));
            OrgEnteRowBean enteTemp = struttureEjb
                    .getOrgEnteRowBean(rowBean.getBigDecimal("id_ente"));
            form.getStrutRif().getId_ente().setValue(enteTemp.getNmEnte());
            form.getStrutRif().getStruttura().setValue(
                    rowBean.getString("nm_strut") + " (" + rowBean.getString("ds_strut") + ")");

            // fisso a false tutte le autorizzazioni a procedere con le altre fasi della
            // listNavigationOnClick in
            // questa action
            this.setInsertAction(false);
            this.setEditAction(false);
            this.setDeleteAction(false);
        }
        return form;
    }

    private void redirectToRegistroPage() throws EMFError {
        StrutTipiForm form = prepareRedirectToStrutTipi();
        form.getRegistroUnitaDocList()
                .setFilterValidRecords(getForm().getRegistroUnitaDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_TIPI, form,
                form.getRegistroUnitaDocList().getName(),
                getForm().getRegistroUnitaDocList().getTable(), getNavigationEvent());
    }

    private void redirectToTipologiaUnitaDocPage() throws EMFError {
        StrutTipiForm form = prepareRedirectToStrutTipi();
        /*
         * Se non è un tentativo d'inserimento di un nuovo Tipo unita documentaria mantengo il
         * valore dell'id in modo da poterlo riusare nella gestione del dettaglio
         */
        if (!NE_DETTAGLIO_INSERT.equals(getNavigationEvent())) {
            form.getIdList().getId_tipo_unita_doc()
                    .setValue(((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList().getTable()
                            .getCurrentRow()).getIdTipoUnitaDoc().toPlainString());
        } else {
            form.getIdList().getId_tipo_unita_doc().setValue(null);
        }
        form.getTipoUnitaDocList()
                .setFilterValidRecords(getForm().getTipoUnitaDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_TIPI, form, form.getTipoUnitaDocList().getName(),
                getForm().getTipoUnitaDocList().getTable(), getNavigationEvent());
    }

    private void redirectToTipoDocPage() throws EMFError {
        StrutTipiForm form = prepareRedirectToStrutTipi();

        /*
         * Se non è un tentativo d'inserimento di un nuovo Tipo unita documentaria mantengo il
         * valore dell'id in modo da poterlo riusare nella gestione del dettaglio
         */
        if (!NE_DETTAGLIO_INSERT.equals(getNavigationEvent())) {
            form.getIdList().getId_tipo_doc().setValue(
                    ((DecTipoDocRowBean) getForm().getTipoDocList().getTable().getCurrentRow())
                            .getIdTipoDoc().toPlainString());
        } else {
            form.getIdList().getId_tipo_doc().setValue(null);
        }
        form.getTipoDocList()
                .setFilterValidRecords(getForm().getTipoDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_TIPI, form, form.getTipoDocList().getName(),
                getForm().getTipoDocList().getTable(), getNavigationEvent());
    }

    private void redirectToTipoFascicoloPage() throws EMFError {
        StrutTipiFascicoloForm form = prepareRedirectToStrutTipiFascicolo();

        form.getTipoFascicoloList()
                .setFilterValidRecords(getForm().getTipoFascicoloList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_TIPI_FASCICOLO, form,
                form.getTipoFascicoloList().getName(), getForm().getTipoFascicoloList().getTable(),
                getNavigationEvent());
    }

    private void redirectToTipoStrutDocPage() throws EMFError {
        StrutTipoStrutForm form = prepareRedirectToStrutTipoStrut();
        form.getTipoStrutDocList()
                .setFilterValidRecords(getForm().getTipoStrutDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_TIPO_STRUT, form,
                form.getTipoStrutDocList().getName(), getForm().getTipoStrutDocList().getTable(),
                getNavigationEvent());
    }

    // Publisher
    private void redirectToFormatoFileDocPage() throws EMFError {
        StrutFormatoFileForm form = prepareRedirectToStrutFormatoFile();
        form.getFormatoFileDocList()
                .setFilterValidRecords(getForm().getFormatoFileDocList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_FORMATO_FILE, form,
                form.getFormatoFileDocList().getName(),
                getForm().getFormatoFileDocList().getTable(), getNavigationEvent());
    }

    private void redirectToCreaCriterioRaggrPage() throws EMFError {
        CriteriRaggruppamentoForm form = new CriteriRaggruppamentoForm();
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form
                .getComponent(form.getCriterioRaggrList().getName()))
                .setTable(getForm().getCriteriRaggruppamentoList().getTable());
        form.getIdFields().getId_strut().setValue(
                "" + ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                        .getBigDecimal("id_strut"));
        redirectToAction(Application.Actions.CRITERI_RAGGRUPPAMENTO,
                "?operation=listNavigationOnClick&navigationEvent=" + getNavigationEvent()
                        + "&table=" + form.getCriterioRaggrList().getName() + "&riga="
                        + getForm().getCriteriRaggruppamentoList().getTable().getCurrentRowIndex()
                        + "&cessato=" + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    private void redirectToXsdDatiSpecPage() throws EMFError {
        StrutDatiSpecForm form = prepareRedirectToStrutDatiSpec();

        form.getXsdDatiSpecList()
                .setFilterValidRecords(getForm().getXsdDatiSpecList().isFilterValidRecords());
        redirectToPage(Application.Actions.STRUT_DATI_SPEC, form,
                form.getXsdDatiSpecList().getName(), getForm().getXsdDatiSpecList().getTable(),
                getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
            BaseTableInterface<?> table, String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
                .setTable(table);
        redirectToAction(action,
                "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                        + listToPopulate + "&riga=" + table.getCurrentRowIndex() + "&cessato="
                        + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    @Override
    public void updateRegistroUnitaDocList() throws EMFError {
        redirectToRegistroPage();
    }

    @Override
    public void deleteRegistroUnitaDocList() throws EMFError {
        getMessageBox().clear();
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = (DecRegistroUnitaDocRowBean) getForm()
                .getRegistroUnitaDocList().getTable().getCurrentRow();
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getRegistroUnitaDocList()));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            registroEjb.deleteDecRegistroUnitaDoc(param,
                    registroUnitaDocRowBean.getIdRegistroUnitaDoc().longValue());
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Registro Unita' Documentaria eliminato con successo"));
            // Reload list
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getDecRegistroUnitaDocTableBean(idStrut,
                            getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
            getForm().getRegistroUnitaDocList().getTable().first();
            getForm().getRegistroUnitaDocList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            forwardToPublisher(getLastPublisher());

        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateTipoUnitaDocList() throws EMFError {
        redirectToTipologiaUnitaDocPage();
    }

    @Override
    public void deleteTipoUnitaDocList() throws EMFError {
        getMessageBox().clear();
        BigDecimal idTipoUnitaDoc = ((DecTipoUnitaDocRowBean) getForm().getTipoUnitaDocList()
                .getTable().getCurrentRow()).getIdTipoUnitaDoc();

        if (getMessageBox().isEmpty()) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                        getForm().getTipoUnitaDocList()));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                tipoUnitaDocEjb.deleteDecTipoUnitaDoc(param, idTipoUnitaDoc.longValue());
                getMessageBox().addMessage(new Message(MessageLevel.INF,
                        "Tipo Unita' Documentaria eliminato con successo"));
                BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                        .getCurrentRow()).getBigDecimal("id_strut");
                DecTipoUnitaDocTableBean tipoUnitaDocTableBean = tipoUnitaDocEjb
                        .getDecTipoUnitaDocTableBean(idStrut,
                                getForm().getTipoUnitaDocList().isFilterValidRecords());
                getForm().getTipoUnitaDocList().setTable(tipoUnitaDocTableBean);
                getForm().getTipoUnitaDocList().getTable().first();
                getForm().getTipoUnitaDocList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                forwardToPublisher(getLastPublisher());
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public void updateTipoDocList() throws EMFError {
        redirectToTipoDocPage();
    }

    @Override
    public void deleteTipoDocList() throws EMFError {
        getMessageBox().clear();
        DecTipoDocRowBean tipoDocRowBean = (DecTipoDocRowBean) getForm().getTipoDocList().getTable()
                .getCurrentRow();
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getTipoDocList()));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoDocEjb.deleteDecTipoDoc(param, tipoDocRowBean.getIdTipoDoc().longValue());
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Tipo Documento eliminato con successo"));
            // Reload list
            DecTipoDocTableBean tipoDocTableBean = tipoDocEjb.getDecTipoDocTableBean(idStrut,
                    getForm().getRegistroUnitaDocList().isFilterValidRecords());

            getForm().getTipoDocList().setTable(tipoDocTableBean);
            getForm().getTipoDocList().getTable().first();
            getForm().getTipoDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            forwardToPublisher(getLastPublisher());
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateTipoFascicoloList() throws EMFError {
        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList()
                .getTable()).getCurrentRow().getNmTipoFascicolo();
        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError(
                    "Attenzione: il tipo fascicolo sconosciuto non può essere modificato");
        } else {
            redirectToTipoFascicoloPage();
        }
    }

    @Override
    public void deleteTipoFascicoloList() throws EMFError {
        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList()
                .getTable()).getCurrentRow().getNmTipoFascicolo();
        BaseRowInterface currentRow = getForm().getTipoFascicoloList().getTable().getCurrentRow();
        BigDecimal idTipoFascicolo = currentRow.getBigDecimal("id_tipo_fascicolo");
        int riga = getForm().getTipoFascicoloList().getTable().getCurrentRowIndex();

        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError(
                    "Attenzione: il tipo fascicolo sconosciuto non pu\u00F2 essere eliminato");
        }

        if (!getMessageBox().hasError() && idTipoFascicolo != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(
                                    CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                            getForm().getTipoFascicoloList()));
                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecTipoFascicolo(param, idTipoFascicolo);
                    // Rimuovo la riga dalla lista online
                    getForm().getTipoFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Tipo fascicolo eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                    forwardToPublisher(getLastPublisher());
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public void updateTipoStrutDocList() throws EMFError {
        redirectToTipoStrutDocPage();
    }

    @Override
    public void deleteTipoStrutDocList() throws EMFError {
        getMessageBox().clear();

        DecTipoStrutDocRowBean tipoStrutDocRowBean;
        DecTipoCompDocRowBean tipoCompDocRowBean = new DecTipoCompDocRowBean();
        tipoStrutDocRowBean = (DecTipoStrutDocRowBean) getForm().getTipoStrutDocList().getTable()
                .getCurrentRow();

        tipoCompDocRowBean.setIdTipoStrutDoc(tipoCompDocRowBean.getIdTipoStrutDoc());
        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoStrutDocEjb.deleteDecTipoStrutDoc(param,
                    tipoStrutDocRowBean.getIdTipoStrutDoc().longValue());
            getMessageBox().addMessage(
                    new Message(MessageLevel.INF, "Struttura documento eliminata con successo"));
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");
            DecTipoStrutDocTableBean tipoStrutDocTableBean = tipoStrutDocEjb
                    .getDecTipoStrutDocTableBean(idStrut,
                            (getForm().getTipoStrutDocList().isFilterValidRecords() == null ? false
                                    : getForm().getTipoStrutDocList().isFilterValidRecords()));
            getForm().getTipoStrutDocList().setTable(tipoStrutDocTableBean);
            getForm().getTipoStrutDocList().getTable().first();
            getForm().getTipoStrutDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            forwardToPublisher(getLastPublisher());
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateFormatoFileDocList() throws EMFError {
        redirectToFormatoFileDocPage();
    }

    @Override
    public void deleteFormatoFileDocList() throws EMFError {
        DecFormatoFileDocRowBean formatoFileDocRowBean = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocList().getTable().getCurrentRow();
        if (getMessageBox().isEmpty()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getFormatoFileDocList()));
            try {
                formatoFileDocEjb.deleteDecFormatoFileDoc(param,
                        formatoFileDocRowBean.getIdFormatoFileDoc().longValue());
                getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                        "Formato ammesso eliminato con successo"));
                DecFormatoFileDocTableBean formatoFileDocTableBean = formatoFileDocEjb
                        .getDecFormatoFileDocTableBean(formatoFileDocRowBean.getIdStrut(),
                                getForm().getFormatoFileDocList().isFilterValidRecords());

                getForm().getFormatoFileDocList().setTable(formatoFileDocTableBean);
                getForm().getFormatoFileDocList().getTable().first();
                getForm().getFormatoFileDocList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                forwardToPublisher(getLastPublisher());

            } catch (ParerUserError e) {
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public void updateCriteriRaggruppamentoList() throws EMFError {
        redirectToCreaCriterioRaggrPage();
    }

    @Override
    public void deleteCriteriRaggruppamentoList() throws EMFError {
        try {
            DecVRicCriterioRaggrRowBean row = (DecVRicCriterioRaggrRowBean) getForm()
                    .getCriteriRaggruppamentoList().getTable().getCurrentRow();
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getCriteriRaggruppamentoList()));

            if (crHelper.deleteDecCriterioRaggr(param, row.getIdStrut(),
                    row.getNmCriterioRaggr())) {
                getMessageBox().addMessage(new Message(MessageLevel.INF,
                        "Criterio di raggruppamento eliminato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getVisStrutture().setEditMode();
                initRicercaStruttureCombo();
                forwardToPublisher(getLastPublisher());
            }
        } catch (ParerUserError ex) {
            forwardToPublisher(getLastPublisher());
        }
    }

    // DETTAGLIO
    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento,
     * ricavando i valori da una struttura impostata
     *
     * @param idStrut id struttura
     *
     * @return CriteriRaggruppamentoForm
     */
    public CriteriRaggruppamentoForm initComboAmbienteEnteStrutCreaCriteriRaggr(
            BigDecimal idStrut) {
        CriteriRaggruppamentoForm criteriForm = new CriteriRaggruppamentoForm();

        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

        // Inizializzo le combo settando la struttura corrente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        OrgEnteTableBean tmpTableBeanEnte = null;
        OrgStrutTableBean tmpTableBeanStruttura = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.FALSE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.FALSE);

        } catch (Exception ex) {
            log.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        criteriForm.getCreaCriterioRaggr().getId_ambiente().setDecodeMap(mappaAmbiente);
        criteriForm.getCreaCriterioRaggr().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        criteriForm.getCreaCriterioRaggr().getId_ente().setDecodeMap(mappaEnte);
        criteriForm.getCreaCriterioRaggr().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        criteriForm.getCreaCriterioRaggr().getId_strut().setDecodeMap(mappaStrut);
        criteriForm.getCreaCriterioRaggr().getId_strut().setValue(idStrut.toString());

        return criteriForm;
    }

    private void visualizzaBottoniListeDettaglioStruttura() throws EMFError {
        // Chiudo tutte le section ad eccezione di quella dei parametri
        getForm().getRegistriTab().setLoadOpened(true);
        getForm().getTipoUdTab().setLoadOpened(true);
        getForm().getTipoDocTab().setLoadOpened(true);
        getForm().getFormatoFileTab().setLoadOpened(false);
        getForm().getTipoStrutTab().setLoadOpened(false);
        getForm().getTipoRapprTab().setLoadOpened(false);
        getForm().getTitolariTab().setLoadOpened(false);
        getForm().getCriteriRaggruppamentoTab().setLoadOpened(false);
        getForm().getSubStrutTab().setLoadOpened(false);
        getForm().getSerieTab().setLoadOpened(false);
        getForm().getStrutFlags().setLoadOpened(false);
        // Rimetto visibile il bottone di esporta
        getForm().getInsStruttura().getScaricaStruttura().setEditMode();
        getForm().getInsStruttura().getScaricaStruttura().setDisableHourGlass(true);
        if ("1".equals(getForm().getInsStruttura().getFl_cessato().parse())) {
            getForm().getImportaParametri().getImportaParametriButton().setViewMode();
        } else {
            getForm().getImportaParametri().getImportaParametriButton().setEditMode();
        }
        // Bottone log eventi
        getForm().getInsStruttura().getLogEventi().setEditMode();

        getForm().getInsStruttura().getEliminaFormatiSpecifici().setEditMode();
    }

    @Override
    public void filterInactiveRecordsRegistroUnitaDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getRegistroUnitaDocList().getTable() != null) {
            rowIndex = getForm().getRegistroUnitaDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getRegistroUnitaDocList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista registri
        DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                .getDecRegistroUnitaDocTableBean(idStrut,
                        getForm().getRegistroUnitaDocList().isFilterValidRecords());

        getForm().getRegistroUnitaDocList().setTable(registroUnitaDocTableBean);
        registroUnitaDocTableBean.addSortingRule(
                DecRegistroUnitaDocTableDescriptor.COL_CD_REGISTRO_UNITA_DOC, SortingRule.ASC);
        registroUnitaDocTableBean.sort();

        getForm().getRegistroUnitaDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getRegistroUnitaDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoFascicoloList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoFascicoloList().getTable() != null) {
            rowIndex = getForm().getTipoFascicoloList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoFascicoloList().getTable().getPageSize();
        }
        // Lista fascicoli
        DecTipoFascicoloTableBean tipoFascicoloTableBean = null;
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        tipoFascicoloTableBean = tipoFascicoloEjb.getTipiFascicoloAbilitati(getUser().getIdUtente(),
                idStrut, getForm().getTipoFascicoloList().isFilterValidRecords());

        getForm().getTipoFascicoloList().setTable(tipoFascicoloTableBean);
        tipoFascicoloTableBean.addSortingRule(DecTipoFascicoloTableDescriptor.COL_NM_TIPO_FASCICOLO,
                SortingRule.ASC);
        tipoFascicoloTableBean.sort();

        getForm().getTipoFascicoloList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoFascicoloList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTitolariList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTitolariList().getTable() != null) {
            rowIndex = getForm().getTitolariList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTitolariList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista titolari
        getForm().getTitolariList().setTable(titolariEjb.getDecTitolTableBean(idStrut,
                getForm().getTitolariList().isFilterValidRecords()));
        getForm().getTitolariList().getTable().addSortingRule("dt_istituz", SortingRule.DESC);
        getForm().getTitolariList().getTable().sort();

        getForm().getTitolariList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTitolariList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoRapprCompList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoRapprCompList().getTable() != null) {
            rowIndex = getForm().getTipoRapprCompList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoRapprCompList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista tipologie rappresentazioni
        DecTipoRapprCompTableBean tipoRapprCompTableBean = tipoRapprEjb
                .getDecTipoRapprCompTableBean(idStrut,
                        getForm().getTipoRapprCompList().isFilterValidRecords());

        getForm().getTipoRapprCompList().setTable(tipoRapprCompTableBean);
        tipoRapprCompTableBean.addSortingRule(
                DecTipoRapprCompTableDescriptor.COL_NM_TIPO_RAPPR_COMP, SortingRule.ASC);
        tipoRapprCompTableBean.sort();

        getForm().getTipoRapprCompList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoRapprCompList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoStrutDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoStrutDocList().getTable() != null) {
            rowIndex = getForm().getTipoStrutDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoStrutDocList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista strutture documento
        DecTipoStrutDocTableBean tipoStrutDocTableBean = tipoStrutDocEjb
                .getDecTipoStrutDocTableBean(idStrut,
                        getForm().getTipoStrutDocList().isFilterValidRecords());

        getForm().getTipoStrutDocList().setTable(tipoStrutDocTableBean);
        tipoStrutDocTableBean.addSortingRule(DecTipoStrutDocTableDescriptor.COL_NM_TIPO_STRUT_DOC,
                SortingRule.ASC);
        tipoStrutDocTableBean.sort();

        getForm().getTipoStrutDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoStrutDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsFormatoFileDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getFormatoFileDocList().getTable() != null) {
            rowIndex = getForm().getFormatoFileDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getFormatoFileDocList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }

        // Lista formati versabili
        DecFormatoFileDocTableBean formatoFileDocTableBean = formatoFileDocEjb
                .getDecFormatoFileDocTableBean(idStrut,
                        getForm().getFormatoFileDocList().isFilterValidRecords());

        getForm().getFormatoFileDocList().setTable(formatoFileDocTableBean);
        getForm().getFormatoFileDocList().setStatus(Status.view);
        getForm().getFormatoFileDocList().getTable().addSortingRule(
                DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
        getForm().getFormatoFileDocList().getTable().sort();

        getForm().getFormatoFileDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getFormatoFileDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoDocList().getTable() != null) {
            rowIndex = getForm().getTipoDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoDocList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }

        // Lista tipi documento
        DecTipoDocTableBean tipoDocTableBean = tipoDocEjb.getDecTipoDocTableBean(idStrut,
                getForm().getTipoDocList().isFilterValidRecords());
        getForm().getTipoDocList().setTable(tipoDocTableBean);
        tipoDocTableBean.addSortingRule(DecTipoDocTableDescriptor.COL_NM_TIPO_DOC, SortingRule.ASC);
        tipoDocTableBean.sort();

        getForm().getTipoDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsTipoUnitaDocList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipoUnitaDocList().getTable() != null) {
            rowIndex = getForm().getTipoUnitaDocList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipoUnitaDocList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista tipi Unità Documentaria
        DecTipoUnitaDocTableBean tipoUnitaTableBean = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(
                idStrut, getForm().getTipoUnitaDocList().isFilterValidRecords());
        getForm().getTipoUnitaDocList().setTable(tipoUnitaTableBean);
        tipoUnitaTableBean.addSortingRule(DecTipoUnitaDocTableDescriptor.COL_NM_TIPO_UNITA_DOC,
                SortingRule.ASC);
        tipoUnitaTableBean.sort();

        getForm().getTipoUnitaDocList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipoUnitaDocList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsCriteriRaggruppamentoList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getCriteriRaggruppamentoList().getTable() != null) {
            rowIndex = getForm().getCriteriRaggruppamentoList().getTable().getCurrentRowIndex();
            pageSize = getForm().getCriteriRaggruppamentoList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista criteri raggruppamento
        DecVRicCriterioRaggrTableBean criteri = critRaggrEjb.getCriteriRaggrByIdStrut(idStrut,
                getForm().getCriteriRaggruppamentoList().isFilterValidRecords());
        criteri.addSortingRule("nm_criterio_raggr", SortingRule.ASC);
        criteri.sort();
        getForm().getCriteriRaggruppamentoList().setTable(criteri);
        getForm().getCriteriRaggruppamentoList().getTable()
                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        getForm().getCriteriRaggruppamentoList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getCriteriRaggruppamentoList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void deleteTipoCompAmmessoDaTipoRapprCompList() {

        try {
            /*
             * Codice aggiuntivo per il logging...
             */
            StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(
                    Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
                        form.getTipoCompAmmessoDaTipoRapprCompList()));
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            BigDecimal idTipoRapprAmmesso = ((DecTipoRapprAmmessoTableBean) getForm()
                    .getTipoCompAmmessoDaTipoRapprCompList().getTable()).getCurrentRow()
                    .getIdTipoRapprAmmesso();
            tipoStrutDocEjb.deleteDecTipoRapprAmmesso(param, idTipoRapprAmmesso,
                    idTipoRapprAmmesso);
            getMessageBox().addMessage(new Message(MessageLevel.INF,
                    "Tipo componente ammesso eliminato con successo"));
            BigDecimal idTipoRapprComp = getForm().getTipoRapprComp().getId_tipo_rappr_comp()
                    .parse();
            DecTipoRapprAmmessoTableBean tipoRapprAmmessoTableBean = tipoStrutDocEjb
                    .getDecTipoRapprAmmessoTableBeanByIdTipoRapprComp(idTipoRapprComp);

            getForm().getTipoCompAmmessoDaTipoRapprCompList().setTable(tipoRapprAmmessoTableBean);
            getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable().first();
            getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable()
                    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        } catch (Exception e) {
            getMessageBox().addError(e.getMessage());
        } finally {
            if (Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP
                    .equals(getLastPublisher())) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.TIPO_RAPPR_COMP_DETAIL);
            }
        }
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoCompAmmesso
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoCompAmmesso() throws EMFError {
        getForm().getTipoCompAmmessoDaTipoRapprComp().post(getRequest());
        if (getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc().parse() == null) {
            getMessageBox().addError(
                    "Errore di compilazione form: tipo struttura documento non inserito<br/>");
        }
        BigDecimal idTipoComp = getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc()
                .parse();
        if (idTipoComp == null) {
            getMessageBox()
                    .addError("Errore di compilazione form: tipo componente non inserito<br/>");
        }
        try {
            if (getMessageBox().isEmpty()) {

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                BigDecimal idTipoRapprComp = getForm().getTipoRapprComp().getId_tipo_rappr_comp()
                        .parse();
                if (getForm().getTipoCompAmmessoDaTipoRapprComp().getStatus()
                        .equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    tipoStrutDocEjb.insertDecTipoRapprAmmesso(param, idTipoComp, idTipoRapprComp);
                    getMessageBox().addMessage(new Message(MessageLevel.INF,
                            "Tipo componente inserito con successo!"));
                    /* Inserisco la riga in fondo alla tabella */
                    DecTipoRapprAmmessoRowBean row = tipoStrutDocEjb
                            .getDecTipoRapprAmmessoRowBean(idTipoComp, idTipoRapprComp);
                    getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable().last();
                    getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable().add(row);
                } else if (getForm().getTipoCompAmmessoDaTipoRapprComp().getStatus()
                        .equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    /*
                     * Ricavo il valore dell'idTipoStrutDoc già memorizzato su DB e di quello che
                     * sto modificando
                     */
                    BigDecimal idTipoRapprAmmessoDB = ((DecTipoRapprAmmessoRowBean) getForm()
                            .getTipoCompAmmessoDaTipoRapprCompList().getTable().getCurrentRow())
                            .getIdTipoRapprAmmesso();
                    tipoStrutDocEjb.updateDecTipoRapprAmmesso(param, idTipoRapprAmmessoDB,
                            idTipoComp, idTipoRapprComp);
                    getMessageBox().addMessage(new Message(MessageLevel.INF,
                            "Tipo componente modificato con successo!"));
                    /*
                     * Setto sulla lista (ancora da ricaricare) il nuovo valore di idTipoCompDoc, in
                     * maniera tale che in caso di immediata ri-modifica prenda su, dalla
                     * loadDettaglio, il valore corretto
                     */
                    ((DecTipoRapprAmmessoRowBean) getForm().getTipoCompAmmessoDaTipoRapprCompList()
                            .getTable().getCurrentRow()).setIdTipoCompDoc(idTipoComp);
                }
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getTipoCompAmmessoDaTipoRapprComp().setViewMode();
                getForm().getTipoCompAmmessoDaTipoRapprCompList().setViewMode();
                getForm().getTipoCompAmmessoDaTipoRapprComp().setStatus(Status.view);
                getForm().getTipoCompAmmessoDaTipoRapprCompList().setStatus(Status.view);
                getMessageBox().setViewMode(ViewMode.plain);
            }
            forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_RAPPR_COMP_TIPO_COMP);
        }

    }

    @Override
    public JSONObject triggerTipoCompAmmessoDaTipoRapprCompId_tipo_strut_docOnTrigger()
            throws EMFError {

        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc().post(getRequest());
        BigDecimal idTipoStrutDoc = getForm().getTipoCompAmmessoDaTipoRapprComp()
                .getId_tipo_strut_doc().parse();

        DecTipoCompDocTableBean tipoCompDoc = tipoStrutDocEjb.getDecTipoCompDocTableBean(idStrut,
                new Date(), idTipoStrutDoc);
        DecodeMap mappaTipoCompDoc = new DecodeMap();
        mappaTipoCompDoc.populatedMap(tipoCompDoc, "id_tipo_comp_doc", "nm_tipo_comp_doc");
        getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc()
                .setDecodeMap(mappaTipoCompDoc);

        return getForm().getTipoCompAmmessoDaTipoRapprComp().asJSON();

    }

    @Override
    public void updateTipoCompAmmessoDaTipoRapprCompList() throws EMFError {
        /* Imposto il valore nelle combo */
        getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_strut_doc().setEditMode();
        getForm().getTipoCompAmmessoDaTipoRapprComp().getId_tipo_comp_doc().setEditMode();
        getForm().getTipoCompAmmessoDaTipoRapprCompList().setStatus(Status.update);
        getForm().getTipoCompAmmessoDaTipoRapprComp().setStatus(Status.update);
    }

    public void loadDettaglioTipoCompDoc() throws EMFError {
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero il tipo strut doc
        DecTipoRapprAmmessoRowBean row = ((DecTipoRapprAmmessoTableBean) getForm()
                .getTipoCompAmmessoDaTipoRapprCompList().getTable()).getRow(numberRiga.intValue());
        BigDecimal idTipoCompDoc = row.getIdTipoCompDoc();
        DecTipoCompDocRowBean tipoCompDocRow = tipoStrutDocEjb
                .getDecTipoCompDocRowBean(idTipoCompDoc);
        // Setto la tabella dei tipi componente aggiungendo solo quella recuperata
        DecTipoCompDocTableBean tipoCompDocTable = new DecTipoCompDocTableBean();
        tipoCompDocTable.add(tipoCompDocRow);
        tipoCompDocTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getTipoCompAmmessoDaTipoRapprCompList().setTable(tipoCompDocTable);
        setTableName(getForm().getTipoCompAmmessoDaTipoRapprCompList().getName());
        setNavigationEvent(NE_DETTAGLIO_VIEW);
        redirectToTipoCompPage(row);
    }

    private void redirectToTipoCompPage(DecTipoRapprAmmessoRowBean row) throws EMFError {
        StrutTipoStrutForm form = prepareRedirectToStrutTipoStrut();
        form.getTipoStrutDoc().getNm_tipo_strut_doc().setValue(row.getString("nm_tipo_strut_doc"));
        form.getTipoStrutDoc().getDs_tipo_strut_doc().setValue(row.getString("ds_tipo_strut_doc"));
        redirectToPage(Application.Actions.STRUT_TIPO_STRUT, form,
                form.getTipoCompDocList().getName(),
                getForm().getTipoCompAmmessoDaTipoRapprCompList().getTable(), getNavigationEvent());
    }

    /**
     * Bottone per l'inserimento del file xml di configurazione: resetto l'oggetto wizard, imposto
     * il campo multipart editabile e forwordo alla pagina del wizard
     *
     * @throws EMFError errore generico
     */
    @Override
    public void importaParametriButton() throws EMFError {
        getForm().getInserimentoWizard().reset();
        getForm().getImportaParametri().setEditMode();
        forwardToPublisher(Application.Publisher.IMPORTA_PARAMETRI_WIZARD);
    }

    @Override
    public void importaParametriDaRicercaButton() throws EMFError {
        // Ricavo gli id delle strutture da trattare
        Map<BigDecimal, String> struttureDaElaborare = new HashMap<>();
        getForm().getStruttureList().post(getRequest());
        OrgVRicStrutTableBean strutTableBean = (OrgVRicStrutTableBean) getForm().getStruttureList()
                .getTable();
        for (OrgVRicStrutRowBean strutRowBean : strutTableBean) {
            if (strutRowBean.getString("fl_strutture_sel") != null
                    && strutRowBean.getString("fl_strutture_sel").equals("1")) {
                struttureDaElaborare.put(strutRowBean.getIdStrut(), strutRowBean.getNmStrut());
            }
        }
        if (!struttureDaElaborare.isEmpty()) {
            getSession().setAttribute("struttureDaElaborarePerImportaParametri",
                    struttureDaElaborare);
            importaParametriButton();
        } else {
            getMessageBox()
                    .addError("E' necessario selezionare le strutture ove eseguire l'importazione");
            forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
        }
    }

    /**
     * Primo step del wizard: - Carico il file xml
     *
     * @throws EMFError errore generico
     */
    @Override
    public void inserimentoWizardPasso1OnEnter() throws EMFError {
        getForm().getImportaParametri().setEditMode();
        forwardToPublisher(Application.Publisher.IMPORTA_PARAMETRI_WIZARD);
    }

    /**
     * Chiusura primo step: - Verifica se Ã¨ stato caricato il file XML, quindi carica il secondo
     * step, altrimenti mostra il messaggio di errore validazione
     *
     * @return true se i dati sono validati
     *
     * @throws EMFError errore generico
     */
    @Override
    public boolean inserimentoWizardPasso1OnExit() throws EMFError {
        return importaXmlDaWizard();
    }

    /**
     * Carica il secondo step: - carico la combo con le tipologie unitÃ  documentarie e la combo con
     * i tipi fascicolo
     *
     * @throws EMFError errore generico
     */
    @Override
    public void inserimentoWizardPasso2OnEnter() throws EMFError {
        UUID uuid = (UUID) getSession().getAttribute("uuid");
        // Setto i valori di tipologie unità documentarie ricavati dall'xml
        getForm().getImportaParametri().getNm_tipo_unita_doc()
                .setDecodeMap(getTipiUdDaXmlDecodeMap(uuid));
        getForm().getImportaParametri().getNm_tipo_unita_doc().clear();
        // Setto i valori di tipi fascicolo ricavati dall'xml
        getForm().getImportaParametri().getNm_tipo_fascicolo()
                .setDecodeMap(getTipiFascicoloDaXmlDecodeMap(uuid));
        getForm().getImportaParametri().getNm_tipo_fascicolo().clear();
        forwardToPublisher(Application.Publisher.IMPORTA_PARAMETRI_WIZARD);
    }

    /**
     * Chiusura secondo step - Passo direttamente al salvataggio
     *
     * @return true/false
     *
     * @throws EMFError errore generico
     */
    @Override
    public boolean inserimentoWizardPasso2OnExit() throws EMFError {
        forwardToPublisher(Application.Publisher.IMPORTA_PARAMETRI_WIZARD);
        // Esegui l'import
        return true;
    }

    /**
     * Metodo eseguito al salvataggio del wizard di importazione parametri di configurazione
     *
     * @return true in caso di salvataggio riuscito
     *
     * @throws EMFError errore generico
     */
    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        boolean result = true;
        if (getForm().getImportaParametri().validate(getMessageBox())) {
            try {
                if (getForm().getImportaParametri().getNm_tipo_unita_doc().parse() != null) {

                    String importareRegistri = getForm().getImportaParametri()
                            .getCheck_includi_registri().parse();
                    // Ricavo l'oggetto contenente l'XML con il tipo ud/tipi strut ud da importare
                    UUID uuid = (UUID) getSession().getAttribute("uuid");

                    /*
                     * Se il flag "Includi registri collegati" è true viene verificato se almeno un
                     * registro da importare cui il tipo Ud è collegato presenta il flag
                     * "fl_tipo_serie_mult" a 1 ed in caso lancio un messaggio di warning
                     */
                    boolean existRegistriDaImportareConFlTipoSerieMultAlzato = struttureEjb
                            .registriDaImportareConTipoSerieMult(uuid,
                                    getForm().getImportaParametri().getNm_tipo_unita_doc().parse());
                    if (importareRegistri.equals("1")
                            && existRegistriDaImportareConFlTipoSerieMultAlzato) {
                        getRequest().setAttribute("customBoxImportaParametri", true);
                        getSession().setAttribute(
                                "existRegistriDaImportareConFlTipoSerieMultAlzato",
                                existRegistriDaImportareConFlTipoSerieMultAlzato);
                        result = false;
                    } else {
                        // Eseguo l'import
                        eseguiImportaParametri(existRegistriDaImportareConFlTipoSerieMultAlzato);
                    }

                } else {
                    getMessageBox().addError("Selezionare un tipo unità documentaria");
                }
            } catch (EMFError e) {
                log.error(ECCEZIONE_IMPORT_TIPO_UD, e);
                getMessageBox().addError(
                        "Attenzione: errore durante l'import del Tipo Unità Documentaria");
                result = false;
            } catch (ParerUserError e) {
                String[] errorParts = e.getDescription().split(";");
                customizeErrorMessage(errorParts);
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            } catch (Exception e) {
                log.error(ECCEZIONE_IMPORT_TIPO_UD, e);
                getMessageBox().addError(ExceptionUtils.getRootCauseMessage(e));
                result = false;
            }
        }
        return result;
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        if (getSession().getAttribute("struttureDaElaborarePerImportaParametri") != null) {
            getSession().removeAttribute("struttureDaElaborarePerImportaParametri");
            forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
        } else {
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
        }
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.IMPORTA_PARAMETRI_WIZARD;
    }

    /**
     * Popola la combo Tipo Strut Unita Doc in base alla scelta di Tipo Unita Doc
     *
     * @return JSONObject
     *
     * @throws EMFError errore generico
     */
    @Override
    public JSONObject triggerImportaParametriNm_tipo_unita_docOnTrigger() throws EMFError {
        getForm().getImportaParametri().post(getRequest());
        List<String> nmTipoUnitaDocList = getForm().getImportaParametri().getNm_tipo_unita_doc()
                .parse();
        UUID uuid = (UUID) getSession().getAttribute("uuid");
        if (nmTipoUnitaDocList.size() == 1) {
            getForm().getImportaParametri().getNm_tipo_strut_unita_doc()
                    .setDecodeMap(getTipiStrutUdDaTipoUdDecodeMap(uuid, nmTipoUnitaDocList.get(0)));
        } else {
            getForm().getImportaParametri().getNm_tipo_strut_unita_doc()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getImportaParametri().asJSON();
    }

    private DecodeMap getTipiUdDaXmlDecodeMap(UUID uuid) {
        DecodeMap mappaTipiUd = new DecodeMap();
        DecTipoUnitaDocTableBean decTipoUnitaDocTableBean = struttureEjb
                .getTipiUdDaXmlImportato(uuid);
        mappaTipiUd.populatedMap(decTipoUnitaDocTableBean, "nm_tipo_unita_doc",
                "nm_tipo_unita_doc");
        return mappaTipiUd;
    }

    /**
     * Popola la combo Periodi tipo fascicolo in base alla scelta di Tipo Fascicolo
     *
     * @return JSONObject
     *
     * @throws EMFError errore generico
     */
    @Override
    public JSONObject triggerImportaParametriNm_tipo_fascicoloOnTrigger() throws EMFError {
        getForm().getImportaParametri().post(getRequest());
        List<String> nmTipoFascicoloList = getForm().getImportaParametri().getNm_tipo_fascicolo()
                .parse();
        UUID uuid = (UUID) getSession().getAttribute("uuid");
        if (nmTipoFascicoloList.size() == 1) {
            getForm().getImportaParametri().getAa_tipo_fascicolo().setDecodeMap(
                    getAaTipoFascicoloDaTipoFascicoloDecodeMap(uuid, nmTipoFascicoloList.get(0)));
        } else {
            getForm().getImportaParametri().getAa_tipo_fascicolo().setDecodeMap(new DecodeMap());
        }
        return getForm().getImportaParametri().asJSON();
    }

    private DecodeMap getTipiFascicoloDaXmlDecodeMap(UUID uuid) {
        DecodeMap mappaTipiFascicolo = new DecodeMap();
        DecTipoFascicoloTableBean tipoFascicoloTableBean = struttureEjb
                .getTipiFascicoloDaXmlImportato(uuid);
        mappaTipiFascicolo.populatedMap(tipoFascicoloTableBean, "nm_tipo_fascicolo",
                "nm_tipo_fascicolo");
        return mappaTipiFascicolo;
    }

    private DecodeMap getTipiStrutUdDaTipoUdDecodeMap(UUID uuid, String nmTipoUnitaDoc) {
        DecTipoStrutUnitaDocTableBean decTipoStrutUnitaDocTableBean = struttureEjb
                .getTipiStrutUdDaXmlImportato(uuid, nmTipoUnitaDoc);
        DecodeMap mappaTipiStrutUd = new DecodeMap();
        mappaTipiStrutUd.populatedMap(decTipoStrutUnitaDocTableBean, "nm_tipo_strut_unita_doc",
                "nm_tipo_strut_unita_doc");
        return mappaTipiStrutUd;
    }

    private DecodeMap getAaTipoFascicoloDaTipoFascicoloDecodeMap(UUID uuid,
            String nmTipoFascicolo) {
        DecAaTipoFascicoloTableBean aATipoFascicoloTableBean = struttureEjb
                .getAaTipoFascicoloDaXmlImportato(uuid, nmTipoFascicolo);
        DecodeMap mappaAaTipoFascicolo = new DecodeMap();
        mappaAaTipoFascicolo.populatedMap(aATipoFascicoloTableBean, "aa_ini_tipo_fascicolo",
                "descrizione_periodo");
        return mappaAaTipoFascicolo;
    }

    /**
     * Crea l'oggetto OrgStrut partendo dal file XML caricato e salvandolo nell'oggetto UUID
     *
     * @return true/false
     */
    private boolean importaXmlDaWizard() {
        boolean result = false;
        try {
            getForm().getImportaParametri().validate(getMessageBox());

            if (!getMessageBox().hasError()) {
                // controlli per il tipo acquisizione file
                if (getForm().getImportaParametri().getXml_parametri().parse() == null) {
                    getMessageBox().addError("Nessun file selezionato");
                }
            }

            byte[] fileByteArray = getForm().getImportaParametri().getXml_parametri()
                    .getFileBytes();
            String xmlString = new String(fileByteArray, StandardCharsets.UTF_8);

            if (!getMessageBox().hasError()) {
                String mime = singleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.XML_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/xml");
                }
            }

            if (!getMessageBox().hasError()) {
                UUID uuid = struttureEjb.importXmlOrgStrut(xmlString);
                getSession().setAttribute("uuid", uuid);
                result = true;
            }
        } catch (Exception ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        }
        return result;
    }

    public void confermaImportaParametri() throws EMFError {
        try {
            boolean existRegistriDaImportareConFlTipoSerieMultAlzato = false;
            if (getSession()
                    .getAttribute("existRegistriDaImportareConFlTipoSerieMultAlzato") != null) {
                existRegistriDaImportareConFlTipoSerieMultAlzato = (Boolean) getSession()
                        .getAttribute("existRegistriDaImportareConFlTipoSerieMultAlzato");
                getSession().removeAttribute("existRegistriDaImportareConFlTipoSerieMultAlzato");
            }
            // Eseguo l'import
            eseguiImportaParametri(existRegistriDaImportareConFlTipoSerieMultAlzato);

        } catch (ParerUserError e) {
            log.error(ECCEZIONE_IMPORT_TIPO_UD, e);
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.IMPORTA_PARAMETRI_WIZARD);
        }
    }

    private void eseguiImportaParametri(boolean existRegistriDaImportareConFlTipoSerieMultAlzato)
            throws ParerUserError, EMFError {
        /*
         * N.B. non c'è bisogno di fare post perchè viene già fatto nel process() per quanto
         * riguarda questo caso particolare di wizard multipart...
         */
        List<String> tipoUdDaImportareList = getForm().getImportaParametri().getNm_tipo_unita_doc()
                .parse();
        String tipoStrutUdDaImportare = getForm().getImportaParametri().getNm_tipo_strut_unita_doc()
                .parse();
        String importareCriteri = getForm().getImportaParametri().getCheck_includi_criteri()
                .parse();
        BigDecimal idStrutturaCorrente = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        String nmStrutturaCorrente = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getString("nm_strut");
        String importareRegistri = getForm().getImportaParametri().getCheck_includi_registri()
                .parse();
        String importareSistemiMigraz = getForm().getImportaParametri()
                .getCheck_includi_sistemi_migraz().parse();
        String importareFormatiComponente = getForm().getImportaParametri()
                .getCheck_includi_formati_componente().parse();

        List<String> tipoFascicoloDaImportareList = getForm().getImportaParametri()
                .getNm_tipo_fascicolo().parse();
        BigDecimal aaTipoFascicoloDaImportare = getForm().getImportaParametri()
                .getAa_tipo_fascicolo().parse();
        String sovrascriviPeriodi = getForm().getImportaParametri().getCheck_sovrascrivi_periodi()
                .parse();

        Map<BigDecimal, String> struttureDaElaborare = new HashMap<>();

        if (getSession().getAttribute("struttureDaElaborarePerImportaParametri") != null) {
            struttureDaElaborare = (HashMap) getSession()
                    .getAttribute("struttureDaElaborarePerImportaParametri");
        } else {
            struttureDaElaborare.put(idStrutturaCorrente, nmStrutturaCorrente);
        }

        // Ricavo l'oggetto contenente l'XML con il tipo ud/tipi strut ud da importare
        UUID uuid = (UUID) getSession().getAttribute("uuid");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getToolbarInsert());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        try {
            // Controllo che almeno un tipo ud o almeno un tipo fascicolo siano stati selezionati
            if (tipoUdDaImportareList.isEmpty() && tipoFascicoloDaImportareList.isEmpty()) {
                throw new ParerUserError(
                        "Attenzione: è necessario selezionare almeno un tipo ud o un tipo fascicolo");
            }

            for (String tipoUdDaImportare : tipoUdDaImportareList) {
                log.info("Importa parametri - Inizio importazione tipo ud {}", tipoUdDaImportare);
                // Eseguo l'import
                Object[] report = struttureEjb.eseguiImportTipoUd(param, struttureDaElaborare, uuid,
                        tipoUdDaImportare, tipoStrutUdDaImportare, importareRegistri,
                        importareCriteri, importareSistemiMigraz,
                        existRegistriDaImportareConFlTipoSerieMultAlzato,
                        importareFormatiComponente);
                log.info("Importa parametri - Fine importazione tipo ud {}", tipoUdDaImportare);

                // Preparo il report
                getMessageBox()
                        .addInfo("Esecuzione dell'importazione del tipo di unità documentaria "
                                + tipoUdDaImportare + "<br>");
                getMessageBox().addInfo("Numero delle strutture selezionate: "
                        + struttureDaElaborare.size() + "<br>");
                Set<String> strutErrorGenerico = (Set<String>) report[0];
                Set<String> strutErrorSuModello = (Set<String>) report[1];
                Set<String> strutErrorSuModelloXsdUd = (Set<String>) report[4];
                strutErrorGenerico.addAll(strutErrorSuModelloXsdUd);
                strutErrorGenerico.addAll(strutErrorSuModello);
                Map<String, String> strutErrorTipiSerie = (Map<String, String>) report[2];
                Set<String> strutErrorSisMigr = (Set<String>) report[3];
                Map<String, String> strutErrorSisVers = (Map<String, String>) report[5];

                getMessageBox()
                        .addInfo("Numero delle strutture su cui l'operazione è andata a buon fine: "
                                + (struttureDaElaborare.size()
                                        - (strutErrorGenerico.size() + strutErrorSisVers.size()))
                                + "<br>");
                getMessageBox()
                        .addInfo("Numero delle strutture su cui l'operazione è andata in errore: "
                                + (strutErrorGenerico.size() + strutErrorSisVers.size()) + "<br>");
                if (!strutErrorGenerico.isEmpty()) {
                    getMessageBox()
                            .addInfo("Strutture su cui l'operazione è andata in errore: <br>");
                    for (String strutErr : strutErrorGenerico) {
                        getMessageBox().addInfo("- " + strutErr + "<br>");
                    }
                }

                if (!strutErrorTipiSerie.isEmpty()) {
                    getMessageBox().addInfo(
                            "Strutture dove non è stato possibile eseguire la creazione dei tipi serie: <br>");
                    for (Map.Entry<String, String> strutErrTS : strutErrorTipiSerie.entrySet()) {
                        getMessageBox().addInfo("- " + strutErrTS.getKey() + "<br>");
                    }
                }

                if (!strutErrorSisMigr.isEmpty()) {
                    getMessageBox().addInfo(
                            "Attenzione: i seguenti sistemi di migrazione non sono stati importati in quanto non presenti a sistema: <br>");
                    for (String strutErr : strutErrorSisMigr) {
                        getMessageBox().addInfo("- " + strutErr + "<br>");
                    }
                }

                if (!strutErrorSisVers.isEmpty()) {
                    getMessageBox().addInfo(
                            "Strutture su cui l'operazione è andata in errore per sistema versante: <br>");
                    for (Map.Entry<String, String> strutErrSV : strutErrorSisVers.entrySet()) {
                        getMessageBox().addInfo(
                                strutErrSV.getKey() + " - " + strutErrSV.getValue() + "<br>");
                    }
                }
            }

            // TIPI FASCICOLO
            for (String tipoFascicoloDaImportare : tipoFascicoloDaImportareList) {
                log.info("Importa parametri - Inizio importazione tipo fascicolo {}",
                        tipoFascicoloDaImportare);

                // Controllo che le strutture appartengano tutte allo stesso ambiente SACER come
                // controllo per
                // l'importazione massiva dei tipi fascicolo
                String errore = struttureEjb.checkAppartenenzaAmbiente(struttureDaElaborare);

                // Controllo i modelli xsd periodo tipi fascicolo in base all'ambiente di questa
                // nuova struttura
                if (errore.equalsIgnoreCase("")) {
                    try {
                        struttureEjb.checkAndSetModelliXsdTipiFascicoloStrutturaImpParam(uuid,
                                idStrutturaCorrente, tipoFascicoloDaImportare);
                    } catch (ParerUserError e) {
                        String[] errorParts = e.getDescription().split(";");
                        customizeErrorMessage(errorParts);
                    }
                }

                if (errore.equals("") && !getMessageBox().hasWarning()) {
                    // Eseguo l'import dei tipi fascicolo
                    Object[] report = struttureEjb.eseguiImportTipoFascicolo(param,
                            struttureDaElaborare, uuid, tipoFascicoloDaImportare,
                            aaTipoFascicoloDaImportare, sovrascriviPeriodi);
                    Set<String> strutErrorGenerico = (Set<String>) report[0];
                    // Preparo il report
                    getMessageBox().addInfo("Esecuzione dell’importazione del tipo fascicolo "
                            + tipoFascicoloDaImportare + "<br>");
                    getMessageBox().addInfo("Numero delle strutture selezionate: "
                            + struttureDaElaborare.size() + "<br>");
                    getMessageBox().addInfo(
                            "Numero delle strutture su cui l'operazione è andata a buon fine: "
                                    + (struttureDaElaborare.size() - strutErrorGenerico.size())
                                    + "<br>");
                    getMessageBox().addInfo(
                            "Numero delle strutture su cui l'operazione è andata in errore: "
                                    + strutErrorGenerico.size() + "<br>");
                    if (!strutErrorGenerico.isEmpty()) {
                        getMessageBox()
                                .addInfo("Strutture su cui l'operazione è andata in errore: <br>");
                        for (String strutErr : strutErrorGenerico) {
                            getMessageBox().addInfo("- " + strutErr + "<br>");
                        }
                    }
                } else {
                    getMessageBox().addInfo(errore + "<br>");
                }
                log.info("Importa parametri - Fine importazione tipo fascicolo {}",
                        tipoFascicoloDaImportare);
            }

            getMessageBox().setViewMode(ViewMode.alert);
            // Se sono arrivato da ricerca strutture, al termine dell'import torno in ricerca
            // strutture
            if (getSession().getAttribute("struttureDaElaborarePerImportaParametri") != null) {
                forwardToPublisher(Application.Publisher.STRUTTURA_RICERCA);
            } else {
                // Altrimenti ricarico il dettaglio della struttura in cui mi trovavo
                loadStruttura(idStrutturaCorrente);
                postLoad();
                forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
            }

            // Rimuovo l'attributo in sessione
            getSession().removeAttribute("struttureDaElaborarePerImportaParametri");
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
    }

    private void loadStruttura(BigDecimal idStrut) throws EMFError, ParerUserError {
        getForm().getInsStruttura().setViewMode();
        getForm().getInsStruttura().getScaricaStruttura().setEditMode();
        getForm().getInsStruttura().getScaricaStruttura().setDisableHourGlass(true);
        if (getLastPublisher().equals(Application.Publisher.STRUTTURA_RICERCA)) {
            getForm().getInsStruttura().getImportaStruttura().setEditMode();
            getForm().getInsStruttura().getCreaStruttureTemplate().setEditMode();
        }
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut, null);

        if ("1".equals(strutRowBean.getFlCessato())) {
            getForm().getImportaParametri().getImportaParametriButton().setViewMode();
        } else {
            getForm().getImportaParametri().getImportaParametriButton().setEditMode();
        }
        OrgEnteRowBean orgEnteRowBean = ambienteEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        OrgAmbienteRowBean orgAmbienteRowBean = ambienteEjb
                .getOrgAmbienteRowBean(orgEnteRowBean.getIdAmbiente());
        initStrutComboBox(strutRowBean.getBigDecimal("id_ambiente_ente_convenz"));

        getForm().getInsStruttura().copyFromBean(strutRowBean);
        boolean isStrutturaTemplate = "1".equals(strutRowBean.getFlTemplate());
        getForm().getInsStruttura().getScaricaStruttura().setHidden(isStrutturaTemplate);
        getForm().getInsStruttura().getEliminaFormatiSpecifici().setEditMode();

        // Flag partizionamenti
        getForm().getInsStruttura().getPartiz_complet()
                .setValue(struttureEjb.partitionOK(strutRowBean.getIdStrut()));

        if (strutRowBean.getIdCategStrut() != null) {
            getForm().getInsStruttura().getId_categ_strut()
                    .setValue(strutRowBean.getIdCategStrut().toString());
        }

        getForm().getInsStruttura().getDs_strut().setHidden(true);
        getForm().getInsStruttura().getNm_strut().setHidden(true);
        getForm().getInsStruttura().getId_ambiente_rif().setHidden(true);
        getForm().getInsStruttura().getId_ente_rif().setHidden(true);

        getForm().getInsStruttura().getView_nm_strut().setHidden(false);
        getForm().getInsStruttura().getView_nm_ente().setHidden(false);
        getForm().getInsStruttura().getView_nm_amb().setHidden(false);

        getForm().getInsStruttura().getId_categ_strut().setHidden(false);

        getForm().getInsStruttura().getView_nm_strut()
                .setValue(strutRowBean.getNmStrut() + " (" + strutRowBean.getDsStrut() + ")");
        getForm().getInsStruttura().getView_nm_ente().setValue(orgEnteRowBean.getNmEnte());
        getForm().getInsStruttura().getView_nm_amb().setValue(orgAmbienteRowBean.getNmAmbiente());

        getForm().getInsStruttura().setStatus(Status.view);
        getForm().getStruttureList().setStatus(Status.view);

        loadlists(idStrut, true);

        DecodeMap mappaAmbienti = new DecodeMap();
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
        getForm().getInsStruttura().getId_ambiente_rif().setDecodeMap(mappaAmbienti);
        getForm().getInsStruttura().getId_ambiente_rif()
                .setValue("" + orgAmbienteRowBean.getIdAmbiente());
        popolaEnteRif(orgEnteRowBean.getIdEnte());

        // Chiudo le sezioni relative ai parametri
        getForm().getStrutFlags().setLoadOpened(false);
        getForm().getParametriUnitaDoc().setLoadOpened(false);
        getForm().getParametriFascicolo().setLoadOpened(false);

        // Parametri
        // loadListeParametriStruttura(idStrut, null, false, false, false, false, true);
        // loadListaParametriAmnministrazioneStruttura(idStrut, null, false, false,
        // getForm().getParametriAmministrazioneStrutturaList().isFilterValidRecords());
        //
        // loadListaParametriConservazioneStruttura(idStrut, null, false, false,
        // getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
        //
        // loadListaParametriGestioneStruttura(idStrut, null, false, true,
        // getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
        getSession().removeAttribute("provenienzaParametri");

        if ("1".equals(getForm().getInsStruttura().getFl_cessato().parse())) {
            getForm().getParametriStrutturaButtonList().getParametriAmministrazioneStrutturaButton()
                    .setViewMode();
            getForm().getParametriStrutturaButtonList().getParametriConservazioneStrutturaButton()
                    .setViewMode();
            getForm().getParametriStrutturaButtonList().getParametriGestioneStrutturaButton()
                    .setViewMode();
        } else {
            getForm().getParametriStrutturaButtonList().getParametriAmministrazioneStrutturaButton()
                    .setEditMode();
            getForm().getParametriStrutturaButtonList().getParametriConservazioneStrutturaButton()
                    .setEditMode();
            getForm().getParametriStrutturaButtonList().getParametriGestioneStrutturaButton()
                    .setEditMode();
        }
    }

    private void loadListeParametriStruttura(BigDecimal idStrut, List<String> funzione,
            boolean hideDeleteButtons, boolean editModeAmministrazione,
            boolean editModeConservazione, boolean editModeGestione, boolean filterValid)
            throws ParerUserError {
        BigDecimal idAmbiente = null;
        if (idStrut != null) {
            OrgStrutRowBean strut = struttureEjb.getOrgStrutRowBean(idStrut);
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(strut.getIdEnte());
            idAmbiente = ente.getIdAmbiente();
        }

        Object[] parametriObj = amministrazioneEjb.getAplParamApplicStruttura(idAmbiente, idStrut,
                funzione, filterValid);

        // MEV26587
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) parametriObj[0];
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) parametriObj[1];
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriAmministrazioneStrutturaList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneStrutturaList().getTable().first();
        getForm().getParametriGestioneStrutturaList().setTable(parametriGestione);
        getForm().getParametriGestioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriGestioneStrutturaList().getTable().first();
        getForm().getParametriConservazioneStrutturaList().setTable(parametriConservazione);
        getForm().getParametriConservazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriConservazioneStrutturaList().getTable().first();
        getForm().getParametriAmministrazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriGestioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriConservazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setEditMode();
            getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setViewMode();
            getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.view);
        }

        if (editModeConservazione) {
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setEditMode();
            getForm().getParametriConservazioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setViewMode();
            getForm().getParametriConservazioneStrutturaList().setStatus(Status.view);
        }

        if (editModeGestione) {
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setEditMode();
            getForm().getParametriGestioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setViewMode();
            getForm().getParametriGestioneStrutturaList().setStatus(Status.view);
        }
    }

    private void loadListaParametriAmnministrazioneStruttura(BigDecimal idStrut,
            List<String> funzione, boolean hideDeleteButtons, boolean editModeAmministrazione,
            boolean filterValid) throws ParerUserError {
        BigDecimal idAmbiente = null;
        if (idStrut != null) {
            OrgStrutRowBean strut = struttureEjb.getOrgStrutRowBean(idStrut);
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(strut.getIdEnte());
            idAmbiente = ente.getIdAmbiente();
        }

        // MEV26587
        AplParamApplicTableBean parametriAmministrazione = amministrazioneEjb
                .getAplParamApplicAmministrazioneStruttura(idAmbiente, idStrut, funzione,
                        filterValid);

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        getForm().getParametriAmministrazioneStrutturaList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneStrutturaList().getTable().first();
        getForm().getParametriAmministrazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setEditMode();
            getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setViewMode();
            getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.view);
        }
    }

    private void loadListaParametriConservazioneStruttura(BigDecimal idStrut, List<String> funzione,
            boolean hideDeleteButtons, boolean editModeConservazione, boolean filterValid)
            throws ParerUserError {
        BigDecimal idAmbiente = null;
        if (idStrut != null) {
            OrgStrutRowBean strut = struttureEjb.getOrgStrutRowBean(idStrut);
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(strut.getIdEnte());
            idAmbiente = ente.getIdAmbiente();
        }

        // MEV26587
        AplParamApplicTableBean parametriConservazione = amministrazioneEjb
                .getAplParamApplicConservazioneStruttura(idAmbiente, idStrut, funzione,
                        filterValid);

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriConservazioneStrutturaList().setTable(parametriConservazione);
        getForm().getParametriConservazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriConservazioneStrutturaList().getTable().first();
        getForm().getParametriConservazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        if (editModeConservazione) {
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setEditMode();
            getForm().getParametriConservazioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setViewMode();
            getForm().getParametriConservazioneStrutturaList().setStatus(Status.view);
        }
    }

    private void loadListaParametriGestioneStruttura(BigDecimal idStrut, List<String> funzione,
            boolean hideDeleteButtons, boolean editModeGestione, boolean filterValid)
            throws ParerUserError {
        BigDecimal idAmbiente = null;
        if (idStrut != null) {
            OrgStrutRowBean strut = struttureEjb.getOrgStrutRowBean(idStrut);
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(strut.getIdEnte());
            idAmbiente = ente.getIdAmbiente();
        }

        // MEV26587
        AplParamApplicTableBean parametriGestione = amministrazioneEjb
                .getAplParamApplicGestioneStruttura(idAmbiente, idStrut, funzione, filterValid);

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        getForm().getParametriGestioneStrutturaList().setTable(parametriGestione);
        getForm().getParametriGestioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriGestioneStrutturaList().getTable().first();
        getForm().getParametriGestioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        if (editModeGestione) {
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setEditMode();
            getForm().getParametriGestioneStrutturaList().setStatus(Status.update);
        } else {
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setViewMode();
            getForm().getParametriGestioneStrutturaList().setStatus(Status.view);
        }
    }

    /*
     * Carica le liste dei parametri a video dal Database oppure dall'entitÃ  orgStrut importata se
     * diversa da null.
     */
    private void loadListeParametriStrutturaPerDupImp(OrgStrutRowBean orgStrutRowBean,
            boolean hideDeleteButtons, boolean editMode, OrgStrut orgStrutImportata)
            throws ParerUserError {
        Object[] parametriObj = null;
        if (orgStrutImportata == null) {
            parametriObj = amministrazioneEjb
                    .getAplParamApplicRowBeansFromStruttura(orgStrutRowBean);
        } else {
            parametriObj = amministrazioneEjb
                    .getAplParamApplicRowBeansFromStrutturaImportata(orgStrutImportata);
        }
        getForm().getParametriAmministrazioneSection().setLoadOpened(true);
        getForm().getParametriConservazioneSection().setLoadOpened(true);
        getForm().getParametriGestioneSection().setLoadOpened(true);
        getForm().getParametriAmministrazioneStrutturaList()
                .setTable((AplParamApplicTableBean) parametriObj[0]);
        getForm().getParametriAmministrazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneStrutturaList().getTable().first();
        getForm().getParametriGestioneStrutturaList()
                .setTable((AplParamApplicTableBean) parametriObj[1]);
        getForm().getParametriGestioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriGestioneStrutturaList().getTable().first();
        getForm().getParametriConservazioneStrutturaList()
                .setTable((AplParamApplicTableBean) parametriObj[2]);
        getForm().getParametriConservazioneStrutturaList().getTable().setPageSize(300);
        getForm().getParametriConservazioneStrutturaList().getTable().first();
        getForm().getParametriAmministrazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriGestioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriConservazioneStrutturaList().setHideDeleteButton(hideDeleteButtons);
        if (editMode) {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setEditMode();
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setEditMode();
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setEditMode();
        } else {
            getForm().getParametriAmministrazioneStrutturaList()
                    .getDs_valore_param_applic_strut_amm().setViewMode();
            getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                    .setViewMode();
            getForm().getParametriConservazioneStrutturaList()
                    .getDs_valore_param_applic_strut_cons().setViewMode();
        }
    }

    /**
     * Al clic del bottone popolo una combo degli ambienti cui l'utente Ã¨ abilitato, setto
     * l'attributo che mi dice di visualizzare la finestrella ed infine redireziono alla pagina
     * stessa che deve essere ricaricata per recepire codeste direttive
     *
     * @throws EMFError errore generico
     */
    @Override
    public void creaStruttureTemplate() throws EMFError {
        getForm().getStruttureTemplateCreator().reset();
        getForm().getStruttureTemplateCreator().setEditMode();
        // Ricavo gli ambienti cui l'utente è abilitato
        DecodeMap mappaAmbienti = new DecodeMap();
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        mappaAmbienti.populatedMap(ambienteTableBean, "id_ambiente", "nm_ambiente");
        getForm().getStruttureTemplateCreator().getId_ambiente_strutture_template()
                .setDecodeMap(mappaAmbienti);
        /*
         * Ricarico la pagina in maniera tale che, col parametrozzo settato in request, a sto giro
         * mi venga visualizzata la finestrella
         */
        getRequest().setAttribute("customBoxStruttureTemplate", true);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void confermaCreazioneStruttureTemplate() throws EMFError {
        /*
         * Controllo la provenienza: via finestrella JavaScript o noJavaScript? N.B.: procedura
         * migliorabile facendo in modo di "settare" in request anche tramite finestrella JavaScript
         * il valore delle combo selezionate senza passarle come parametro come fatto di seguito...
         */
        String isFromAjax = getRequest().getParameter("isFromJavaScript");

        BigDecimal idAmbienteSelezionato = null;
        String nmAmbienteSelezionato = null;
        BigDecimal idEnteSelezionato = null;
        String nmEnteSelezionato = null;

        if (Boolean.parseBoolean(isFromAjax)) {
            idAmbienteSelezionato = !(getRequest().getParameter(PARAMETER_ID_AMBIENTE)).equals("")
                    ? new BigDecimal(getRequest().getParameter(PARAMETER_ID_AMBIENTE))
                    : null;
            nmAmbienteSelezionato = getRequest().getParameter("nmAmbiente");
            idEnteSelezionato = !(getRequest().getParameter("idEnte")).equals("")
                    ? new BigDecimal(getRequest().getParameter("idEnte"))
                    : null;
            nmEnteSelezionato = getRequest().getParameter("nmEnte");
        } else {
            getForm().getStruttureTemplateCreator().post(getRequest());
            idAmbienteSelezionato = getForm().getStruttureTemplateCreator()
                    .getId_ambiente_strutture_template().parse();
            nmAmbienteSelezionato = getForm().getStruttureTemplateCreator()
                    .getId_ambiente_strutture_template().getDecodedValue();
            idEnteSelezionato = getForm().getStruttureTemplateCreator()
                    .getId_ente_strutture_template().parse();
            nmEnteSelezionato = getForm().getStruttureTemplateCreator()
                    .getId_ente_strutture_template().getDecodedValue();
        }

        // Controllo innanzitutto di aver selezionato l'ambiente
        if (idAmbienteSelezionato != null) {

            BigDecimal idEntePerCreazioneStruttureTemplate = null;
            String nmEntePerCreazioneStruttureTemplate = null;

            // Se ho selezionato SOLO L'AMBIENTE, devo prendere un ente a caso tra quelli template
            // delle'ambiente
            // selezionato
            if (idEnteSelezionato == null) {
                OrgVRicEnteTableBean orgVRicEnteTemplateTableBean = ambienteEjb
                        .getEntiAbilitatiTemplate(getUser().getIdUtente(), idAmbienteSelezionato,
                                CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE.name());
                // Se NON esiste almeno un ente con tipo_def_template_ente = TEMPLATE_DEF_AMBIENTE
                // per l'ambiente
                // selezionato
                if (orgVRicEnteTemplateTableBean.isEmpty()) {
                    getMessageBox().addError(
                            "Per creare strutture template è necessario definire preventivamente un ente template nell'ambiente selezionato");
                    getRequest().setAttribute("customBoxStruttureTemplate", true);
                } else {
                    // Se invece ne esiste almeno uno, prendo il primo che trovo (come diceva quella
                    // pubblicità della
                    // FIAT del 2002, "adesso esco e vado col primo template che incontro!")
                    idEntePerCreazioneStruttureTemplate = orgVRicEnteTemplateTableBean.getRow(0)
                            .getIdEnte();
                    nmEntePerCreazioneStruttureTemplate = orgVRicEnteTemplateTableBean.getRow(0)
                            .getNmEnte();
                }
            }

            if (!getMessageBox().hasError()) {
                // Determino le strutture template disponibili già presenti per l'ambiente (ed
                // eventualmente ente)
                // selezionato/i
                int numStruttureTemplateGiaPresenti = (int) struttureEjb
                        .countOrgStrutTemplatePerAmbienteEnte(idAmbienteSelezionato.longValue(),
                                idEnteSelezionato != null ? idEnteSelezionato.longValue() : null,
                                idEnteSelezionato != null ? null
                                        : CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_AMBIENTE
                                                .name());

                int numMassimoStruttureTemplateCreabili = Integer
                        .parseInt(configurationHelper.getValoreParamApplicByApplic(
                                CostantiDB.ParametroAppl.NUM_MAX_STRUT_TEMPLATE));

                int numStruttureTemplateDaCreare = numMassimoStruttureTemplateCreabili
                        - numStruttureTemplateGiaPresenti;

                if (numStruttureTemplateDaCreare <= 0) {
                    getMessageBox().addError(
                            "Il numero di strutture template disponibili nell'ambiente/ente selezionato "
                                    + "è pari al numero massimo gestito dal sistema. "
                                    + "Prima di creare nuove strutture template "
                                    + "è necessario utilizzare quelle esistenti");
                    getRequest().setAttribute("customBoxStruttureTemplate", true);
                } else {
                    int[] progressivi = struttureEjb.getProgressiviPerCreazioneStruttureTemplate(
                            numStruttureTemplateDaCreare);

                    idEntePerCreazioneStruttureTemplate = idEnteSelezionato != null
                            ? idEnteSelezionato
                            : idEntePerCreazioneStruttureTemplate;
                    nmEntePerCreazioneStruttureTemplate = nmEnteSelezionato != null
                            ? nmEnteSelezionato
                            : nmEntePerCreazioneStruttureTemplate;

                    int progressivoTemplate = 0;
                    // Creo numStruttureTemplateDaCreare
                    while (numStruttureTemplateDaCreare != 0) {

                        OrgStrutRowBean struttura = new OrgStrutRowBean();
                        struttura.setNmStrut("Template " + progressivi[progressivoTemplate]);
                        struttura.setDsStrut("struttura template");
                        struttura.setIdEnte(idEntePerCreazioneStruttureTemplate);
                        struttura.setFlTemplate("1");
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);
                        struttura.setDtIniValStrut(new Timestamp(c.getTime().getTime()));
                        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
                        struttura.setDtFineValStrut(new Timestamp(c.getTime().getTime()));

                        try {
                            /*
                             * a questo metodo come LogParam viene passato null in quanto non serve
                             * perchÃ© non dovrÃ  loggare applicativamente la struttura in caso di
                             * Template.
                             */
                            struttureEjb.insertOrgStruttura(null, struttura, true);
                            progressivoTemplate++;

                        } catch (ParerUserError e) {
                            getMessageBox().addError(
                                    "Errore durante la creazione massiva di una o più strutture template");
                        }
                        numStruttureTemplateDaCreare--;
                    }

                    if (!getMessageBox().hasError()) {
                        // Conteggio strutture template
                        getForm().getStruttureTemplate().getNum_strut_templ_disp()
                                .setValue(struttureEjb
                                        .countOrgStrutTemplateRaggruppati(getUser().getIdUtente()));
                        getForm().getStruttureTemplate().getNum_strut_templ_part()
                                .setValue(struttureEjb
                                        .countOrgStrutTemplateWithCompletedPartitioningRaggruppati(
                                                getUser().getIdUtente()));
                        String messageInfo = "Create "
                                + (numMassimoStruttureTemplateCreabili
                                        - numStruttureTemplateGiaPresenti)
                                + " " + "strutture template nell'ambiente " + nmAmbienteSelezionato;
                        if (nmEntePerCreazioneStruttureTemplate != null
                                && !nmEntePerCreazioneStruttureTemplate.equals("")) {
                            messageInfo = messageInfo + " nell'ente "
                                    + nmEntePerCreazioneStruttureTemplate;
                        }
                        getMessageBox().addInfo(messageInfo);
                        getMessageBox().setViewMode(ViewMode.plain);
                        getForm().getStruttureTemplateCreator().setViewMode();
                    }
                }
            }
        } else {
            getMessageBox().addError("E' necessario selezionare un ambiente");
            getRequest().setAttribute("customBoxStruttureTemplate", true);
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void annullaCreazioneStruttureTemplate() throws EMFError {
        // Nascondo i bottoni con javascript disattivato
        getForm().getStruttureTemplateCreator().setViewMode();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void eliminaTuttiFormatiFileDoc() {
        List<String> formatiDaEliminare = new ArrayList<>();
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        DecFormatoFileDocTableBean formatoFileDocTB = (DecFormatoFileDocTableBean) getForm()
                .getFormatoFileDocList().getTable();
        for (DecFormatoFileDocRowBean formatoFileDocRB : formatoFileDocTB) {
            formatiDaEliminare.add(formatoFileDocRB.getNmFormatoFileDoc());
        }
        // Aggiunto per il logging
        StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getButtonActionName(form, form.getFormatoFileDocButtonList(), form
                        .getFormatoFileDocButtonList().getEliminaTuttiFormatiFileDoc().getName()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        List<String> formatiNonEliminati = formatoFileDocEjb.deleteDecFormatoFileDocList(param,
                idStrut, formatiDaEliminare);
        if (!formatiNonEliminati.isEmpty()) {
            getMessageBox().addWarning("I formati " + formatiNonEliminati.toString() + " "
                    + "non sono stati eliminati in quanto esiste almeno un elemento associato ad esso");
        } else {
            getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                    "Tutti i formati ammessi sono stati eliminati con successo"));
        }
        DecFormatoFileDocTableBean formatoFileDocTableBean = formatoFileDocEjb
                .getDecFormatoFileDocTableBean(idStrut,
                        getForm().getFormatoFileDocList().isFilterValidRecords());
        getForm().getFormatoFileDocList().setTable(formatoFileDocTableBean);
        getForm().getFormatoFileDocList().setStatus(Status.view);
        getForm().getFormatoFileDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getFormatoFileDocList().getTable().first();
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    public void triggerStruttureTemplateCreatorByJavaScript() {
        BigDecimal idAmbienteSelezionato = !(getRequest().getParameter(PARAMETER_ID_AMBIENTE))
                .equals("") ? new BigDecimal(getRequest().getParameter(PARAMETER_ID_AMBIENTE))
                        : null;
        getForm().getStruttureTemplateCreator().getId_ente_strutture_template()
                .setDecodeMap(new DecodeMap());
        if (idAmbienteSelezionato != null) {
            // Setto la combo degli enti
            setComboEntiAbilitatiTemplate(idAmbienteSelezionato);
        }
        // "Riseleziono" l'ambiente
        getForm().getStruttureTemplateCreator().getId_ambiente_strutture_template()
                .setValue("" + idAmbienteSelezionato);
        getRequest().setAttribute("customBoxStruttureTemplate", true);
        forwardToPublisher(getLastPublisher());
    }

    public void setComboEntiAbilitatiTemplate(BigDecimal idAmbiente) {
        OrgVRicEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiTemplate(
                getUser().getIdUtente(), idAmbiente,
                CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name());
        DecodeMap mappaEnti = new DecodeMap();
        mappaEnti.populatedMap(enteTableBean, "id_ente", "nm_ente");
        getForm().getStruttureTemplateCreator().getId_ente_strutture_template()
                .setDecodeMap(mappaEnti);
    }

    @Override
    public void ricaricaAmbiente() throws EMFError {
        getForm().getStruttureTemplateCreator().post(getRequest());
        BigDecimal idAmbiente = getForm().getStruttureTemplateCreator()
                .getId_ambiente_strutture_template().parse();
        if (idAmbiente != null) {
            setComboEntiAbilitatiTemplate(idAmbiente);
        } else {
            getMessageBox().addError("Selezionare un ambiente");
        }
        getRequest().setAttribute("customBoxStruttureTemplate", true);
        forwardToPublisher(getLastPublisher());
    }

    public void duplicaRegistroOperation() throws EMFError {
        boolean isCessata = getForm().getInsStruttura().getFl_cessato().parse().equals("1");
        if (isCessata) {
            getMessageBox()
                    .addError("La struttura \u00E8 cessata, impossibile duplicare il registro");
            forwardToPublisher(getLastPublisher());
        } else {
            // Essendo un metodo richiamato dall'elemento link, ricavo "a mano" la riga trattata
            String riga = getRequest().getParameter("riga");
            Integer numeroRiga = Integer.parseInt(riga);
            getForm().getRegistroUnitaDocList().getTable().setCurrentRowIndex(numeroRiga);
            // Siccome me devo spostÃ  nell'altra action (StrutTipiForm), la quale gestisce il
            // dettaglio del registro, mi
            // preparo per il trasloco
            StrutTipiForm form = prepareRedirectToStrutTipi();
            form.getRegistroUnitaDocList().setFilterValidRecords(
                    getForm().getRegistroUnitaDocList().isFilterValidRecords());
            // Inserisco la lista registri presente in StruttureAction nella lista registri di
            // StrutTipiAction
            ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form
                    .getComponent(form.getRegistroUnitaDocList().getName()))
                    .setTable(getForm().getRegistroUnitaDocList().getTable());
            // Redireziono alla action al metodo di duplicazione registro
            redirectToAction(Application.Actions.STRUT_TIPI,
                    "?operation=duplicaRegistroOperation&table=RegistroUnitaDocList&riga="
                            + numeroRiga,
                    form);
        }
    }

    @Override
    public void filterInactiveRecordsTipologieSerieList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getTipologieSerieList().getTable() != null) {
            rowIndex = getForm().getTipologieSerieList().getTable().getCurrentRowIndex();
            pageSize = getForm().getTipologieSerieList().getTable().getPageSize();
        }
        BigDecimal idStrut = null;
        if (getForm().getStruttureList().getTable() != null
                && getForm().getStruttureList().getTable().size() > 0) {
            idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                    .getBigDecimal("id_strut");
        }
        // Lista tipi Serie
        getForm().getTipologieSerieList().setTable(tipoSerieEjb.getDecTipoSerieTableBean(idStrut,
                getForm().getTipologieSerieList().isFilterValidRecords()));
        getForm().getTipologieSerieList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getTipologieSerieList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void logEventiTipoRapprComp() throws EMFError {
        DecTipoRapprCompRowBean bean = (DecTipoRapprCompRowBean) getForm().getTipoRapprCompList()
                .getTable().getCurrentRow();
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto()
                .setValue(SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE);
        form.getOggettoDetail().getIdOggetto().setValue(bean.getIdTipoRapprComp().toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void logEventi() throws EMFError {
        BaseRowInterface bean = getForm().getStruttureList().getTable().getCurrentRow();
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto()
                .setValue(SacerLogConstants.TIPO_OGGETTO_STRUTTURA);
        form.getOggettoDetail().getIdOggetto().setValue(bean.getBigDecimal("id_strut").toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();
        if (ogg instanceof StruttureForm) {
            StruttureForm form = (StruttureForm) ogg;
            if (form.getStruttureList().getStatus().equals(Status.view)) {
                form.getInsStruttura().getLogEventi().setEditMode();
            } else {
                form.getInsStruttura().getLogEventi().setViewMode();
            }

            // MEV#21353
            if (form.getStruttureList().getStatus().equals(Status.view)
                    || form.getStruttureList().getStatus().equals(Status.update)) {
                if (form.getStruttureList().getTable() != null
                        && !form.getStruttureList().getTable().isEmpty()) {
                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    BigDecimal idAmbiente = struttureEjb.getOrgAmbienteRowBeanByIdStrut(idStrut)
                            .getIdAmbiente();
                    // Se esiste una richiesta di restituzione archivio con stati ESTRATTO o
                    // VERIFICATO o RESTITUITO
                    // oppure il parametro Ã¨ valorizzato a true
                    String abilitaFlArkRestituitoNoRich = configurationHelper
                            .getValoreParamApplicByStrut(
                                    CostantiDB.ParametroAppl.ABILITA_FL_ARK_RESTITUITO_NO_RICH,
                                    idAmbiente, idStrut);
                    if (restArchEjb.checkRichRestArchByStatoExisting(idStrut,
                            Arrays.asList(AroRichiestaRa.AroRichiestaTiStato.ESTRATTO,
                                    AroRichiestaRa.AroRichiestaTiStato.VERIFICATO,
                                    AroRichiestaRa.AroRichiestaTiStato.RESTITUITO))
                            || Boolean.parseBoolean(abilitaFlArkRestituitoNoRich)) {
                        form.getInsStruttura().getFl_archivio_restituito().setHidden(false);
                        form.getInsStruttura().getFl_cessato().setHidden(false);
                    } else {
                        form.getInsStruttura().getFl_archivio_restituito().setHidden(true);
                        form.getInsStruttura().getFl_cessato().setHidden(true);
                    }
                } else {
                    form.getInsStruttura().getFl_archivio_restituito().setHidden(true);
                    form.getInsStruttura().getFl_cessato().setHidden(true);
                }
            } else {
                form.getInsStruttura().getFl_archivio_restituito().setHidden(true);
                form.getInsStruttura().getFl_cessato().setHidden(true);
            }

            if (getForm().getStruttureList().getStatus().equals(Status.view)) {
                if (form.getStruttureList().getTable() != null
                        && !form.getStruttureList().getTable().isEmpty()) {
                    BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                            .getCurrentRow()).getBigDecimal("id_strut");
                    OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut, null);
                    boolean isStrutturaTemplate = "1".equals(strutRowBean.getFlTemplate());
                    boolean isStrutturaCessata = "1".equals(strutRowBean.getFlCessato());
                    if (!isStrutturaTemplate && !isStrutturaCessata) {
                        getForm().getInsStruttura().getCessaStruttura().setEditMode();
                        getForm().getInsStruttura().getCessaStruttura().setHidden(false);
                    } else {
                        getForm().getInsStruttura().getCessaStruttura().setViewMode();
                        getForm().getInsStruttura().getCessaStruttura().setHidden(true);
                    }
                } else {
                    getForm().getInsStruttura().getCessaStruttura().setViewMode();
                    getForm().getInsStruttura().getCessaStruttura().setHidden(true);
                }
            } else {
                getForm().getInsStruttura().getCessaStruttura().setViewMode();
                getForm().getInsStruttura().getCessaStruttura().setHidden(true);
            }

            // Gestione visibilità pulsante download per documento processo conservazione
            if (getForm().getDocumentoProcessoConservDetail().getStatus() != null) {
                if (getForm().getDocumentoProcessoConservDetail().getStatus().equals(Status.view)) {
                    // Nascondo il campo per caricare il file
                    getForm().getDocumentoProcessoConservDetail().getBl_doc_processo_conserv()
                            .setHidden(true);

                    // Gestisco la visibilità del bottone di download del file
                    try {
                        if (getForm().getDocumentoProcessoConservDetail()
                                .getNm_file_doc_processo_conserv().parse() != null
                                && !getForm().getDocumentoProcessoConservDetail()
                                        .getNm_file_doc_processo_conserv().parse().isEmpty()) {
                            // Se c'è un file, visualizzo il bottone di download
                            getForm().getDocumentoProcessoConservDetail()
                                    .getDownloadFileDocProcesso().setHidden(false);
                            getForm().getDocumentoProcessoConservDetail()
                                    .getDownloadFileDocProcesso().setEditMode();
                        } else {
                            // Se non c'è un file, nascondo il bottone di download
                            getForm().getDocumentoProcessoConservDetail()
                                    .getDownloadFileDocProcesso().setHidden(true);
                            getForm().getDocumentoProcessoConservDetail()
                                    .getDownloadFileDocProcesso().setViewMode();
                        }
                    } catch (EMFError ex) {
                        java.util.logging.Logger.getLogger(StruttureAction.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public JSONObject triggerVisStruttureId_regione_statoOnTrigger() throws EMFError {
        getForm().getVisStrutture().post(getRequest());
        BigDecimal idRegioneStato = getForm().getVisStrutture().getId_regione_stato().parse();
        // Ricavo il TableBean relativo agli ambiti territoriali di secondo livello
        OrgAmbitoTerritTableBean ambito2LivelloTableBean = ambienteEjb
                .getOrgAmbitoTerritChildTableBean(idRegioneStato);
        DecodeMap mappaAmbitoTerrit2Livello = new DecodeMap();
        mappaAmbitoTerrit2Livello.populatedMap(ambito2LivelloTableBean, "id_ambito_territ",
                "cd_ambito_territ");
        getForm().getVisStrutture().getId_provincia().setDecodeMap(mappaAmbitoTerrit2Livello);
        getForm().getVisStrutture().getId_forma_associata().setDecodeMap(new DecodeMap());
        return getForm().getVisStrutture().asJSON();
    }

    @Override
    public JSONObject triggerVisStruttureId_provinciaOnTrigger() throws EMFError {
        getForm().getVisStrutture().post(getRequest());
        BigDecimal idProvincia = getForm().getVisStrutture().getId_provincia().parse();
        // Ricavo il TableBean relativo agli ambiti territoriali di terzo livello
        OrgAmbitoTerritTableBean ambito3LivelloTableBean = ambienteEjb
                .getOrgAmbitoTerritChildTableBean(idProvincia);
        DecodeMap mappaAmbitoTerrit3Livello = new DecodeMap();
        mappaAmbitoTerrit3Livello.populatedMap(ambito3LivelloTableBean, "id_ambito_territ",
                "cd_ambito_territ");
        getForm().getVisStrutture().getId_forma_associata().setDecodeMap(mappaAmbitoTerrit3Livello);
        return getForm().getVisStrutture().asJSON();
    }

    @Override
    public JSONObject triggerVisStruttureId_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getVisStrutture().post(getRequest());
        BigDecimal idAmbienteEnteConvenz = getForm().getVisStrutture().getId_ambiente_ente_convenz()
                .parse();
        // Ricavo il TableBean relativo agli enti convenzionati
        BaseTable enteConvenzTableBean = ambienteEjb
                .getSIOrgEnteConvenzTableBean(getUser().getIdUtente(), idAmbienteEnteConvenz);
        DecodeMap mappaEntiConvenz = new DecodeMap();
        mappaEntiConvenz.populatedMap(enteConvenzTableBean, "id_ente_siam", "nm_ente_siam");
        getForm().getVisStrutture().getId_ente_convenz().setDecodeMap(mappaEntiConvenz);
        return getForm().getVisStrutture().asJSON();
    }

    @Override
    public JSONObject triggerInsStrutturaId_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getInsStruttura().post(getRequest());
        BigDecimal idAmbienteEnteConvenz = getForm().getInsStruttura().getId_ambiente_ente_convenz()
                .parse();
        // Ricavo il TableBean relativo agli enti convenzionati
        BaseTable enteConvenzTableBean = ambienteEjb.getSIOrgEnteConvenzAccordoValidoTableBean(
                getUser().getIdUtente(), idAmbienteEnteConvenz);
        DecodeMap mappaEntiConvenz = new DecodeMap();
        mappaEntiConvenz.populatedMap(enteConvenzTableBean, "id_ente_siam", "nm_ente_siam");
        getForm().getInsStruttura().getId_ente_convenz().setDecodeMap(mappaEntiConvenz);
        return getForm().getInsStruttura().asJSON();
    }

    @Override
    public JSONObject triggerInsStrutturaId_ente_convenzOnTrigger() throws EMFError {
        getForm().getInsStruttura().post(getRequest());
        BigDecimal idEnteConvenz = getForm().getInsStruttura().getId_ente_convenz().parse();
        DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);

        try {
            BaseRow dateEnteSiamStrut = ambienteEjb
                    .getDateAssociazioneEnteSiamStrutRowBean(idEnteConvenz);
            if (dateEnteSiamStrut != null) {
                getForm().getInsStruttura().getDt_ini_val()
                        .setValue(formato.format(dateEnteSiamStrut.getTimestamp("dt_ini_val")));
                ambienteEjb.getOrgEnteRowBean(idEnteConvenz);
                getForm().getInsStruttura().getDt_fine_val()
                        .setValue(formato.format(dateEnteSiamStrut.getTimestamp("dt_fine_val")));
            } else {
                //
                getForm().getInsStruttura().getDt_ini_val()
                        .setValue(formato.format(Calendar.getInstance().getTime()));
                Calendar c = Calendar.getInstance();
                c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
                getForm().getInsStruttura().getDt_fine_val().setValue(formato.format(c.getTime()));
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        return getForm().getInsStruttura().asJSON();
    }

    @Override
    public void deleteEnteConvenzOrgList() throws EMFError {
        SIOrgEnteConvenzOrgRowBean row = (SIOrgEnteConvenzOrgRowBean) getForm()
                .getEnteConvenzOrgList().getTable().getCurrentRow();
        BigDecimal idEnteConvenzOrg = row.getIdEnteConvenzOrg();
        int riga = getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex();
        // Controllo che l'associazione non sia l'unica presente. In tal caso, l'eliminazione non Ã¨
        // consentita
        if (getForm().getEnteConvenzOrgList().getTable().size() == 1) {
            getMessageBox().addError(
                    "La struttura è associata ad un solo ente convenzionato. Non è possibile eseguire l'eliminazione");
        }

        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        if (!getMessageBox().hasError() && idEnteConvenzOrg != null) {
            try {
                ambienteEjb.deleteEnteConvenzOrg(param, idEnteConvenzOrg);
                getForm().getEnteConvenzOrgList().getTable().remove(riga);

                getMessageBox()
                        .addInfo("Associazione all'ente convenzionato eliminata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
                forwardToPublisher(getLastPublisher());
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void updateEnteConvenzOrgList() throws EMFError {
        redirectToEnteConvenzPage(NE_DETTAGLIO_UPDATE);
    }

    private void redirectToEnteConvenzPage(String action) throws EMFError {
        // Qualsiasi azione sia, la gestirÃ² nell'action
        EntiConvenzionatiForm form = new EntiConvenzionatiForm();
        form.getEnteConvenzOrgList().setTable(getForm().getEnteConvenzOrgList().getTable());
        int riga = getForm().getEnteConvenzOrgList().getTable().getCurrentRowIndex();
        // form = form nuova
        form.getStrutRif().getStruttura().setValue(getForm().getInsStruttura().getNm_strut().parse()
                + " ( " + getForm().getInsStruttura().getDs_strut().parse() + ")");
        form.getStrutRif().getNm_ente()
                .setValue(getForm().getInsStruttura().getId_ente().getDecodedValue());
        form.getStrutRif().getId_strut().setValue(
                ((BaseRowInterface) getForm().getStruttureList().getTable().getCurrentRow())
                        .getBigDecimal("id_strut").toPlainString());

        redirectToAction(Application.Actions.ENTI_CONVENZIONATI,
                "?operation=listNavigationOnClick&navigationEvent=" + action + "&table="
                        + form.getEnteConvenzOrgList().getName() + "&riga=" + riga + "&cessato="
                        + getForm().getInsStruttura().getFl_cessato().parse(),
                form);
    }

    public void loadStrutDaMenu() {
        StruttureForm form = new StruttureForm();
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        OrgStrutRowBean row = struttureEjb.getOrgStrutRowBean(idStrut);
        OrgStrutTableBean table = new OrgStrutTableBean();
        table.add(row);
        form.getStruttureList().setTable(table);
        setLastPublisher(Application.Publisher.CREA_STRUTTURA);
        setTableName(form.getStruttureList().getName());
        setForm(form);
        SessionManager.addPrevExecutionToHistory(getSession(), true, false);
        redirectToAction(Application.Actions.STRUTTURE,
                "?operation=listNavigationOnClick&navigationEvent=" + NE_DETTAGLIO_VIEW + "&table="
                        + form.getStruttureList().getName() + "&riga=0",
                form);
    }

    @Override
    public JSONObject triggerInsStrutturaNm_strutOnTrigger() throws EMFError {
        getForm().getInsStruttura().post(getRequest());
        String nmStrut = getForm().getInsStruttura().getNm_strut().parse();
        if (nmStrut != null) {
            String cdStrutNormaliz = Utils.getNormalizedUDCode(nmStrut);
            // MAC#18134 - Introdotto controllo di univocitÃ  sul codice normalizzato
            cdStrutNormaliz = struttureEjb.getCodStrutturaNormalizzatoUnivoco(cdStrutNormaliz);
            getForm().getInsStruttura().getCd_strut_normaliz().setValue(cdStrutNormaliz);
        }
        return getForm().getInsStruttura().asJSON();
    }

    /**
     * Elimina un parametro di amministrazione dalla lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteParametriAmministrazioneStrutturaList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm()
                .getParametriAmministrazioneStrutturaList().getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getParamApplicApplicationName(), getUser().getUsername(),
                SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriAmministrazioneStrutturaList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroStruttura(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning(WARN_VALORE_STRUTTURA_ASSENTE);
        }
        try {
            loadStruttura(getForm().getStruttureList().getTable().getCurrentRow()
                    .getBigDecimal("id_strut"));
        } catch (ParerUserError ex) {
            getMessageBox().addError(ERRORE_CARICAMENTO_STRUTTURA);
        }
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    /**
     * Elimina un parametro di conservazione dalla lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteParametriConservazioneStrutturaList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm()
                .getParametriConservazioneStrutturaList().getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getParamApplicApplicationName(), getUser().getUsername(),
                SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriConservazioneStrutturaList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroStruttura(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning(WARN_VALORE_STRUTTURA_ASSENTE);
        }
        try {
            loadStruttura(getForm().getStruttureList().getTable().getCurrentRow()
                    .getBigDecimal("id_strut"));
        } catch (ParerUserError ex) {
            getMessageBox().addError(ERRORE_CARICAMENTO_STRUTTURA);
        }
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    /**
     * Elimina un parametro di gestione dalla lista
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteParametriGestioneStrutturaList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm()
                .getParametriGestioneStrutturaList().getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getParamApplicApplicationName(), getUser().getUsername(),
                SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriGestioneStrutturaList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroStruttura(param, idValoreParamApplic)) {
                row.setString("ds_valore_param_applic_strut_gest", "");
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning(WARN_VALORE_STRUTTURA_ASSENTE);
        }
        try {
            loadStruttura(getForm().getStruttureList().getTable().getCurrentRow()
                    .getBigDecimal("id_strut"));
        } catch (ParerUserError ex) {
            getMessageBox().addError(ERRORE_CARICAMENTO_STRUTTURA);
        }
        forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
    }

    private void gestisciMessaggiInfo(List<String> messaggi) {
        if (!messaggi.isEmpty()) {
            for (String messaggio : messaggi) {
                getMessageBox().addInfo(messaggio);
            }
        }
    }

    @Override
    public void parametriAmministrazioneStrutturaButton() throws Throwable {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        // loadListeParametriStruttura(idStrut, null, false, true, true, true, filterValid);
        loadListaParametriAmnministrazioneStruttura(idStrut, null, false, true,
                getForm().getParametriAmministrazioneStrutturaList().isFilterValidRecords());
        loadListaParametriConservazioneStruttura(idStrut, null, false, true,
                getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
        loadListaParametriGestioneStruttura(idStrut, null, false, true,
                getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getRicercaParametriStruttura().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriStruttura().getFunzione().reset();
        getForm().getRicercaParametriStruttura().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "amministrazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_STRUTTURA);
    }

    @Override
    public void parametriConservazioneStrutturaButton() throws Throwable {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        // loadListeParametriStruttura(idStrut, null, false, false, true, true, filterValid);
        loadListaParametriAmnministrazioneStruttura(idStrut, null, false, false,
                getForm().getParametriAmministrazioneStrutturaList().isFilterValidRecords());
        loadListaParametriConservazioneStruttura(idStrut, null, false, true,
                getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
        loadListaParametriGestioneStruttura(idStrut, null, false, true,
                getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getRicercaParametriStruttura().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriStruttura().getFunzione().reset();
        getForm().getRicercaParametriStruttura().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "conservazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_STRUTTURA);
    }

    @Override
    public void parametriGestioneStrutturaButton() throws Throwable {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        // loadListeParametriStruttura(idStrut, null, false, false, false, true, filterValid);
        loadListaParametriAmnministrazioneStruttura(idStrut, null, false, false,
                getForm().getParametriAmministrazioneStrutturaList().isFilterValidRecords());
        loadListaParametriConservazioneStruttura(idStrut, null, false, false,
                getForm().getParametriConservazioneStrutturaList().isFilterValidRecords());
        loadListaParametriGestioneStruttura(idStrut, null, false, true,
                getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
        getForm().getInsStruttura().setStatus(Status.update);
        getForm().getRicercaParametriStruttura().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriStruttura().getFunzione().reset();
        getForm().getRicercaParametriStruttura().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "gestione");
        forwardToPublisher(Application.Publisher.PARAMETRI_STRUTTURA);
    }

    @Override
    public void ricercaParametriStrutturaButton() throws EMFError {
        getForm().getRicercaParametriStruttura().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriStruttura().getFunzione().parse();
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        try {
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession()
                        .getAttribute("provenienzaParametri");
                if (provenzienzaParametri.equals("amministrazione")) {
                    // loadListeParametriStruttura(idStrut, funzione, false, true, true, true,
                    // true);
                    loadListaParametriAmnministrazioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriAmministrazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriConservazioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriConservazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriGestioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
                    if (getForm().getInsStruttura().getStatus().equals(Status.update)) {
                        setEditModeParametriAmministrazione();
                    } else {
                        setViewModeListeParametri();
                    }
                } else if (provenzienzaParametri.equals("conservazione")) {
                    // loadListeParametriStruttura(idStrut, funzione, false, false, true, true,
                    // true);
                    loadListaParametriAmnministrazioneStruttura(idStrut, funzione, false, false,
                            getForm().getParametriAmministrazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriConservazioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriConservazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriGestioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
                    if (getForm().getInsStruttura().getStatus().equals(Status.update)) {
                        setEditModeParametriConservazione();
                    } else {
                        setViewModeListeParametri();
                    }
                } else if (provenzienzaParametri.equals("gestione")) {
                    // loadListeParametriStruttura(idStrut, funzione, false, false, false, true,
                    // true);
                    loadListaParametriAmnministrazioneStruttura(idStrut, funzione, false, false,
                            getForm().getParametriAmministrazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriConservazioneStruttura(idStrut, funzione, false, false,
                            getForm().getParametriConservazioneStrutturaList()
                                    .isFilterValidRecords());
                    loadListaParametriGestioneStruttura(idStrut, funzione, false, true,
                            getForm().getParametriGestioneStrutturaList().isFilterValidRecords());
                    if (getForm().getInsStruttura().getStatus().equals(Status.update)) {
                        setEditModeParametriGestione();
                    } else {
                        setViewModeListeParametri();
                    }
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri struttura");
        }
        forwardToPublisher(Application.Publisher.PARAMETRI_STRUTTURA);
    }

    private void setViewModeListeParametri() {
        getForm().getInsStruttura().setStatus(Status.view);
        getForm().getParametriAmministrazioneStrutturaList().setStatus(Status.view);
        getForm().getParametriConservazioneStrutturaList().setStatus(Status.view);
        getForm().getParametriGestioneStrutturaList().setStatus(Status.view);
        getForm().getParametriAmministrazioneStrutturaList().getDs_valore_param_applic_strut_amm()
                .setViewMode();
        getForm().getParametriConservazioneStrutturaList().getDs_valore_param_applic_strut_cons()
                .setViewMode();
        getForm().getParametriGestioneStrutturaList().getDs_valore_param_applic_strut_gest()
                .setViewMode();
    }

    private AplParamApplicTableBean obfuscatePasswordParamApplic(
            AplParamApplicTableBean paramApplicTableBean) {
        // MEV25687 - offusca le password
        Iterator<AplParamApplicRowBean> rowIt = paramApplicTableBean.iterator();
        while (rowIt.hasNext()) {
            AplParamApplicRowBean rowBean = rowIt.next();
            if (rowBean.getTiValoreParamApplic()
                    .equals(Constants.ComboValueParamentersType.PASSWORD.name())) {
                rowBean.setString("ds_valore_param_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_applic") != null) {
                    rowBean.setString("ds_valore_param_applic_applic", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente",
                            Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_strut_amm") != null) {
                    rowBean.setString("ds_valore_param_applic_strut_amm",
                            Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_strut_gest") != null) {
                    rowBean.setString("ds_valore_param_applic_strut_gest",
                            Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_strut_cons") != null) {
                    rowBean.setString("ds_valore_param_applic_strut_cons",
                            Constants.OBFUSCATED_STRING);
                }
            }
        }
        return paramApplicTableBean;
    }

    @Override
    public void eliminaFormatiSpecifici() throws EMFError {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        DecFormatoFileDocTableBean formatiPersonalizzatiTableBean = formatoFileDocEjb
                .getDecFormatoFileDocSpecifici(idStrut);
        getForm().getFormatoFileDocSpecificoList().setTable(formatiPersonalizzatiTableBean);
        getForm().getFormatoFileDocSpecificoList().getTable()
                .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
        getForm().getFormatoFileDocSpecificoList().getTable().addSortingRule(
                DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
        getForm().getFormatoFileDocSpecificoList().getTable().sort();
        getForm().getFormatoFileDocSpecificoList().getTable().first();

        forwardToPublisher(Application.Publisher.FORMATI_SPECIFICI_DETAIL);
    }

    @Override
    public void deleteFormatoFileDocSpecificoList() throws EMFError {
        DecFormatoFileDocRowBean formatoFileDocSpecificoRowBean = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocSpecificoList().getTable().getCurrentRow();
        int index = getForm().getFormatoFileDocSpecificoList().getTable().getCurrentRowIndex();
        BigDecimal idFormatoFileDoc = formatoFileDocSpecificoRowBean
                .getBigDecimal("id_formato_file_doc");
        if (formatoFileDocHelper
                .checkRelationsAreEmptyForDecFormatoFileDoc(idFormatoFileDoc.longValue())) {
            getMessageBox().addError(
                    "Impossibile eliminare il formato specifico: esiste almeno un componente versato con esso</br>");
            SessionManager.removeLastExecutionHistory(getSession());
        }
        if (!getMessageBox().hasError() && (formatoFileDocHelper
                .checkRelationsAreEmptyForDecFormatoFileDocConv(idFormatoFileDoc.longValue())
                || formatoFileDocHelper.checkRelationsAreEmptyForDecFormatoFileDocCont(
                        idFormatoFileDoc.longValue()))) {
            getMessageBox().addError(
                    "Impossibile eliminare il formato specifico: esiste almeno un tipo rappresentazione componente associato ad esso</br>");
            SessionManager.removeLastExecutionHistory(getSession());
        }

        try {
            if (!getMessageBox().hasError()) {
                if (!formatoFileDocHelper
                        .existsFormatoSpecificoAmmesso(idFormatoFileDoc.longValue())) {
                    eseguiCancellazioneFormatoSpecifico(idFormatoFileDoc, index);
                } else {
                    getRequest().setAttribute("customDeleteFormatiSpecifici", true);
                    String messaggio = "Attenzione: esiste almeno un tipo componente che annovera tra i suoi formati ammessi il formato specifico. "
                            + "Confermare la cancellazione?";
                    getRequest().setAttribute("messaggioDeleteFormatiSpecifici", messaggio);
                    getMessageBox().setViewMode(ViewMode.alert);
                }
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(getLastPublisher());

    }

    public void confermaCancFormatoSpecifico() {
        DecFormatoFileDocRowBean formatoFileDocSpecificoRowBean = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocSpecificoList().getTable().getCurrentRow();
        int index = getForm().getFormatoFileDocSpecificoList().getTable().getCurrentRowIndex();
        BigDecimal idFormatoFileDoc = formatoFileDocSpecificoRowBean
                .getBigDecimal("id_formato_file_doc");
        try {
            eseguiCancellazioneFormatoSpecifico(idFormatoFileDoc, index);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(getLastPublisher());
    }

    public void annullaCancFormatoSpecifico() {
        forwardToPublisher(getLastPublisher());
    }

    private void eseguiCancellazioneFormatoSpecifico(BigDecimal idFormatoFileDoc, int index)
            throws ParerUserError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplicByApplic(
                        CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                        getForm().getFormatoFileDocSpecificoList()));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        formatoFileDocEjb.deleteDecFormatoFileDocSpecifico(param, idFormatoFileDoc.longValue());
        getForm().getFormatoFileDocSpecificoList().getTable().remove(index);
        getMessageBox().addInfo("Formato specifico eliminato con successo!");
        SessionManager.removeLastExecutionHistory(getSession());
    }

    @Override
    public void filterInactiveRecordsParametriAmministrazioneStrutturaList() throws EMFError {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        boolean filterValid = getForm().getParametriAmministrazioneStrutturaList()
                .isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_STRUTTURA)) {
                loadListaParametriAmnministrazioneStruttura(idStrut, null, false, true,
                        filterValid);
            } else {
                loadListaParametriAmnministrazioneStruttura(idStrut, null, false, false,
                        filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(
                    "Errore durante il recupero dei parametri di amministrazione della struttura");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsParametriConservazioneStrutturaList() throws EMFError {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        boolean filterValid = getForm().getParametriConservazioneStrutturaList()
                .isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_STRUTTURA)) {
                loadListaParametriConservazioneStruttura(idStrut, null, false, true, filterValid);
            } else {
                loadListaParametriConservazioneStruttura(idStrut, null, false, false, filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(
                    "Errore durante il recupero dei parametri di conservazione della struttura");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsParametriGestioneStrutturaList() throws EMFError {
        BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                .getCurrentRow()).getBigDecimal("id_strut");
        boolean filterValid = getForm().getParametriGestioneStrutturaList().isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_STRUTTURA)) {
                loadListaParametriGestioneStruttura(idStrut, null, false, true, filterValid);
            } else {
                loadListaParametriGestioneStruttura(idStrut, null, false, false, filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(
                    "Errore durante il recupero dei parametri di gestione della struttura");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void deleteXsdDatiSpecList() throws EMFError {

        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                .getXsdDatiSpecList().getTable().getCurrentRow());
        getMessageBox().clear();
        Date dtSoppres = xsdDatiSpecRowBean.getDtSoppres();
        Date today = Calendar.getInstance().getTime();
        if (dtSoppres.compareTo(today) < 0) {
            getMessageBox().addError("Versione XSD gi\u00E0 disattivata in precedenza");
            forwardToPublisher(getLastPublisher());
        } else {
            // Il sistema controlla che tale attributo non sia associato a nessun tipo serie,
            // altrimenti da errore
            if (datiSpecificiEjb
                    .isXsdDatiSpecInUseInTipiSerie(xsdDatiSpecRowBean.getIdXsdDatiSpec())) {
                getMessageBox().addError(
                        "Almeno un attributo dell'xsd \u00E8 utilizzato da un tipo serie . L'eliminazione dell'xsd non \u00E8 consentita");
            }
            if (!getMessageBox().hasError()) {
                boolean isInUse = datiSpecificiEjb.isXsdDatiSpecInUse(xsdDatiSpecRowBean);
                boolean isInUseOnCampiRegole = datiSpecificiEjb.isXsdDatiSpecInUseOnCampi(
                        xsdDatiSpecRowBean.getIdXsdDatiSpec(), "DATO_SPEC_UNI_DOC",
                        "DATO_SPEC_DOC_PRINC");
                // se in uso non posso cancellare, ma posso disattivare
                if (isInUse || isInUseOnCampiRegole) {
                    if (StringUtils.isNotBlank(getLastPublisher())) {
                        // Mostra messaggio di disattivazione
                        getRequest().setAttribute("confermaDisattivazioneXsd", true);
                        forwardToPublisher(getLastPublisher());
                        SessionManager.removeLastExecutionHistory(getSession());
                    } else {
                        deleteXsd(xsdDatiSpecRowBean);
                    }
                } else {
                    deleteXsd(xsdDatiSpecRowBean);
                }
            } else {
                forwardToPublisher(getLastPublisher());
                SessionManager.removeLastExecutionHistory(getSession());
            }
        }
    }

    private void deleteXsd(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws EMFError {
        // se non in uso e ultimo in lista
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (Application.Publisher.XSD_DATI_SPEC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                    getForm().getXsdDatiSpecList()));
        }

        try {
            datiSpecificiEjb.delXsdDatiSpec(param, xsdDatiSpecRowBean);
            getMessageBox().addInfo("Eliminazione xsd avvenuta con successo");
        } catch (ParerUserError ex) {
            log.error(ex.getMessage(), ex);
            getMessageBox().addError("Errore inatteso nell'eliminazione del xsd");
        }
        if (!getMessageBox().hasError()) {
            DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(
                    xsdDatiSpecRowBean.getIdStrut(), CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                    xsdDatiSpecRowBean.getTiEntitaSacer(), xsdDatiSpecRowBean.getNmSistemaMigraz());

            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }
        forwardToPublisher(getLastPublisher());
        SessionManager.removeLastExecutionHistory(getSession());
    }

    public void confermaDisattivazione() throws EMFError {
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                .getXsdDatiSpecList().getTable().getCurrentRow());
        disattivaXsd(xsdDatiSpecRowBean.getIdXsdDatiSpec());
        if (!getMessageBox().hasError()) {
            DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(
                    xsdDatiSpecRowBean.getIdStrut(), CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(),
                    xsdDatiSpecRowBean.getTiEntitaSacer(), xsdDatiSpecRowBean.getNmSistemaMigraz());

            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        }
        forwardToPublisher(getLastPublisher());
    }

    private void disattivaXsd(BigDecimal idXsdDatiSpec) throws EMFError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        datiSpecificiEjb.deactivateXsdAndLog(param, idXsdDatiSpec);
    }

    @Override
    public void downloadFileDocProcesso() throws EMFError {
        downloadFileDocProcessoConserv();
    }

    public void downloadFileDocProcDaLista() throws EMFError {
        log.debug(">>>DOWNLOAD documento processo conservazione da Lista");
        setTableName(getForm().getDocumentiProcessoConservList().getName());
        setRiga(getRequest().getParameter("riga"));
        getForm().getDocumentiProcessoConservList().getTable()
                .setCurrentRowIndex(Integer.parseInt(getRiga()));
        DecDocProcessoConservRowBean row = (DecDocProcessoConservRowBean) getForm()
                .getDocumentiProcessoConservList().getTable().getCurrentRow();
        BigDecimal idDocProcessoConserv = row.getIdDocProcessoConserv();
        downloadFileCommonDocProcessoConserv(idDocProcessoConserv);
    }

    public void downloadFileDocProcessoConserv() throws EMFError {
        BigDecimal idDocProcessoConserv = getForm().getDocumentoProcessoConservDetail()
                .getId_doc_processo_conserv().parse();
        downloadFileCommonDocProcessoConserv(idDocProcessoConserv);
    }

    private void downloadFileCommonDocProcessoConserv(BigDecimal idDocProcessoConserv)
            throws EMFError {
        File tmpFile = null;
        FileOutputStream out = null;
        try {
            Object[] rec = struttureEjb.getFileDocumentoProcessoConserv(idDocProcessoConserv);
            // Controllo per scrupolo
            if (rec[1] == null) {
                getMessageBox().addError("Non c'\u00E8 alcun file da scaricare<br/>");
            } else {
                String nmFileDocProcessoConserv = (String) rec[0];
                byte[] blDocProcessoConserv = (byte[]) rec[1];
                String mimeType = (String) rec[2]; // Nuovo mimetype dal database

                tmpFile = new File(System.getProperty("java.io.tmpdir"), nmFileDocProcessoConserv);
                out = new FileOutputStream(tmpFile);
                IOUtils.write(blDocProcessoConserv, out);

                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                        tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                        tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));

                // Usa il mimetype corretto dal database, se disponibile
                String contentType = (mimeType != null && !mimeType.isEmpty()) ? mimeType
                        : "application/octet-stream";
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                        contentType);
            }
        } catch (Exception ex) {
            log.error("Errore in download documento processo conservazione "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download<br/>");
        } finally {
            IOUtils.closeQuietly(out);
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }

    }

    private StruttureForm initDocumentoProcessoConservDetail() throws EMFError {
        try {
            BigDecimal idStrut = ((BaseRowInterface) getForm().getStruttureList().getTable()
                    .getCurrentRow()).getBigDecimal("id_strut");
            String[] ambEnteStrut = struttureEjb.getAmbienteEnteStrutturaDesc(idStrut);
            SIOrgEnteConvenzOrg orgEnteConvenzOrg = ambienteEjb
                    .getOrgEnteConvenzOrgMostRecent(idStrut);
            getForm().getDocumentoProcessoConservDetail().getCd_registro_doc_processo_conserv()
                    .setDecodeMap(
                            DecodeMap.Factory.newInstance(
                                    registroEjb.getRegistriUnitaDocAbilitati(
                                            getUser().getIdUtente(), idStrut),
                                    "cd_registro", "cd_registro"));
            getForm().getDocumentoProcessoConservDetail().getId_organiz_iam()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            struttureEjb.getOrgEnteConvenzOrgTableBean(new BigDecimal(
                                    orgEnteConvenzOrg.getSiUsrOrganizIam().getIdOrganizApplic())),
                            "id_organiz_iam", "nm_organiz_strut_completo"));
            getForm().getDocumentoProcessoConservDetail().getId_tipo_doc_processo_conserv()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            struttureEjb.getDecTipoDocProcessoConservTableBean(idStrut),
                            "id_tipo_doc_processo_conserv", "nm_tipo_doc"));
            getForm().getEnteConvenzionatoDetail().getId_ente_siam()
                    .setValue(orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam().toString());
            getForm().getEnteConvenzionatoDetail().getNm_ente_siam()
                    .setValue(orgEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam());
            getForm().getEnteConvenzionatoDetail().getCd_ente_convenz()
                    .setValue(orgEnteConvenzOrg.getSiOrgEnteConvenz().getCdEnteConvenz());
            getForm().getEnteConvenzionatoDetail().getTi_cd_ente_convenz()
                    .setValue(orgEnteConvenzOrg.getSiOrgEnteConvenz().getTiCdEnteConvenz());

            // Carico i valori precompilati
            getForm().getDocumentoProcessoConservDetail().getEnte_doc_processo_conserv()
                    .setValue(ambEnteStrut[1]);
            getForm().getDocumentoProcessoConservDetail().getStruttura_doc_processo_conserv()
                    .setValue(ambEnteStrut[2]);

            // Calcolo del progressivo automatico (massimo esistente + 1)
            DecDocProcessoConservTableBean documentiTable = struttureEjb
                    .getDecDocProcessoConservByEnteTableBean(new BigDecimal(
                            orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam()));
            BigDecimal progressivoMassimo = BigDecimal.ZERO;

            if (documentiTable != null) {
                for (int i = 0; i < documentiTable.size(); i++) {
                    BaseRow row = documentiTable.getRow(i);
                    BigDecimal pgDocumento = row.getBigDecimal("pg_doc_processo_conserv");
                    if (pgDocumento != null && pgDocumento.compareTo(progressivoMassimo) > 0) {
                        progressivoMassimo = pgDocumento;
                    }
                }
            }

            // Imposto il progressivo come massimo + 1
            BigDecimal nuovoProgressivo = progressivoMassimo.add(BigDecimal.ONE);
            getForm().getDocumentoProcessoConservDetail().getPg_doc_processo_conserv()
                    .setValue(nuovoProgressivo.toString());
        } catch (ParerUserError ex) {
            java.util.logging.Logger.getLogger(StruttureAction.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return getForm();
    }

    private void loadDettaglioDocumentoProcessoConserv(BigDecimal idDocProcessoConserv)
            throws ParerUserError, EMFError {
        // Carico il dettaglio documento processo conservazione
        DecDocProcessoConservRowBean detailRow = struttureEjb
                .getDecDocProcessoConservRowBean(idDocProcessoConserv);

        getForm().getDocumentoProcessoConservDetail().copyFromBean(detailRow);

        getForm().getDocumentoProcessoConservDetail().setViewMode();
        getForm().getDocumentoProcessoConservDetail().setStatus(Status.view);
        getForm().getDocumentiProcessoConservList().setStatus(Status.view);
    }

    private void redirectToDocumentiProcessoConservPage() throws EMFError {
        if (!NE_DETTAGLIO_INSERT.equals(getNavigationEvent())) {
            try {
                // Non Ã¨ un tentativo di inserimento, quindi reindirizzo alla pagina di dettaglio
                // (non
                // si puÃ² inserire questa entitÃ  da Sacer, ma il controllo lo metto ugualmente per
                // eventuali sviluppi futuri)
                StruttureForm form = initDocumentoProcessoConservDetail();
                /*
                 * Se non Ã¨ un tentativo d'inserimento di un nuovo Tipo unita documentaria mantengo
                 * il valore dell'id in modo da poterlo riusare nella gestione del dettaglio
                 */
                DecDocProcessoConservRowBean currentRow = (DecDocProcessoConservRowBean) getForm()
                        .getDocumentiProcessoConservList().getTable().getCurrentRow();
                loadDettaglioDocumentoProcessoConserv(currentRow.getIdDocProcessoConserv());
                String riga = getRequest().getParameter("riga");
                Integer nr = Integer.parseInt(riga);
                redirectToAction(Application.Actions.STRUTTURE,
                        "?operation=listNavigationOnClick&navigationEvent=" + getNavigationEvent()
                                + "&table=" + form.getDocumentiProcessoConservList().getName()
                                + "&riga=" + nr,
                        form);
            } catch (ParerUserError ex) {
                java.util.logging.Logger.getLogger(StruttureAction.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    public void download() throws EMFError {
        log.debug(">>>DOWNLOAD");
        String filename = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean((String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la
                 * servlet per fare il download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse().setContentType(
                        StringUtils.isBlank(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name())
                                ? WebConstants.MIME_TYPE_GENERIC
                                : WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
                getResponse().setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename);

                FileInputStream inputStream = null;
                try {
                    getResponse().setHeader("Content-Length",
                            String.valueOf(fileToDownload.length()));
                    inputStream = new FileInputStream(fileToDownload);
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    log.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile) {
                    fileToDownload.delete();
                }
            } else {
                getMessageBox()
                        .addError("Errore durante il tentativo di download. File non trovato");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
            forwardToPublisher(getLastPublisher());
        }
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
    }

}
