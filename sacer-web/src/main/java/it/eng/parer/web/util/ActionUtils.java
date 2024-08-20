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

package it.eng.parer.web.util;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUdNonVersIamTableDescriptor;
import it.eng.parer.web.action.MonitoraggioAction;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.util.Assert;

public class ActionUtils {

    private static Logger logger = LoggerFactory.getLogger(ActionUtils.class.getName());

    private StruttureEjb struttureEjb;
    private AmbienteEjb ambienteEjb;
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    private ElenchiVersamentoEjb elenchiVersamentoEjb;

    public ActionUtils() {
        try {
            // Recupera l'ejb per la lettura di informazioni, se possibile
            struttureEjb = (StruttureEjb) new InitialContext().lookup("java:app/Parer-ejb/StruttureEjb");
            ambienteEjb = (AmbienteEjb) new InitialContext().lookup("java:app/Parer-ejb/AmbienteEjb");
            tipoUnitaDocEjb = (TipoUnitaDocEjb) new InitialContext().lookup("java:app/Parer-ejb/TipoUnitaDocEjb");
            elenchiVersamentoEjb = (ElenchiVersamentoEjb) new InitialContext()
                    .lookup("java:app/Parer-ejb/ElenchiVersamentoEjb");
        } catch (NamingException ex) {
            logger.error("Errore durante il recupero degli ejb", ex);
        }
    }

    public static int getQueryMaxResults(Map<String, String> config, String enumType) {
        int maxResults = 1;
        String value;
        if ((value = config.get(enumType)) != null) {
            maxResults = Integer.parseInt(value);
        } else {
            value = config.get(ActionEnums.Configuration.MAX_RESULT_STANDARD.name());
            if (value != null) {
                maxResults = Integer.parseInt(value);
            }
        }
        return maxResults;
    }

    public Fields triggerAmbienteGenerico(Fields campi, long idUtente, Boolean filterValid) throws EMFError {

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox ambienteCombo = (ComboBox) campi.getComponent("id_ambiente");
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

        // Azzero i valori preimpostati delle varie combo
        enteCombo.setValue("");
        strutCombo.setValue("");

        BigDecimal idAmbiente = (!ambienteCombo.getValue().equals("") ? new BigDecimal(ambienteCombo.getValue())
                : null);
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(idUtente, idAmbiente.longValue(),
                    filterValid);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            enteCombo.setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto gi\u00e0 impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                enteCombo.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), strutCombo, idUtente, filterValid);
            } else {
                strutCombo.setDecodeMap(new DecodeMap());
            }
        } else {
            enteCombo.setDecodeMap(new DecodeMap());
            strutCombo.setDecodeMap(new DecodeMap());
        }
        return campi;
    }

    public Fields triggerEnteGenerico(Fields campi, long idUtente, Boolean filterValid) throws EMFError {

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

        strutCombo.setValue("");

        BigDecimal idEnte = (!enteCombo.getValue().equals("") ? new BigDecimal(enteCombo.getValue()) : null);
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(idUtente, idEnte, filterValid);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            strutCombo.setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto gi\u00e0 impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                strutCombo.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            strutCombo.setDecodeMap(new DecodeMap());
        }
        return campi;
    }

    public Fields triggerStrutGenerico(Fields campi, long idUtente) throws EMFError {
        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

        BigDecimal idStrut = (!strutCombo.getValue().equals("") ? new BigDecimal(strutCombo.getValue()) : null);
        if (idStrut != null) {
            // Ricavo il TableBean relativo ai tipi di unit\u00e0 doc dall'ente scelto
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(idUtente, idStrut);
            DecodeMap mappaUD = new DecodeMap();
            mappaUD.populatedMap(tmpTableBeanTUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");
        }
        return campi;
    }

    // /**
    // * Metodo utilizzato per controllare il valore nella combo ambiente quando
    // * questo è l'unico presente e settare di conseguenza la combo ente
    // *
    // * @param idAmbiente
    // * @param ente
    // * @param strut
    // * @param idUtente
    // * @throws EMFError errore generico
    // */
    // private void checkUniqueAmbienteInCombo(BigDecimal idAmbiente, ComboBox ente, ComboBox strut, long idUtente)
    // throws EMFError {
    // if (idAmbiente != null) {
    // // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
    // OrgEnteTableBean tmpTableBeanEnte = comboHelper.getEnteNoTemplateFromAmbiente(idUtente, idAmbiente.longValue());
    // DecodeMap mappaEnte = new DecodeMap();
    // mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
    //
    // ente.setDecodeMap(mappaEnte);
    //
    // // Se la combo ente ha un solo valore presente, lo imposto e faccio controllo su di essa
    // if (tmpTableBeanEnte.size() == 1) {
    // ente.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
    // checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), strut, idUtente);
    // } else {
    // strut.setDecodeMap(new DecodeMap());
    // }
    // }
    // }
    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo \u00e0 l'unico presente e settare di
     * conseguenza la combo struttura
     *
     * @param idEnte
     *            id ente
     * @param strut
     *            id struttura
     * @param idUtente
     *            id utente
     *
     * @throws EMFError
     *             errore generico
     */
    private void checkUniqueEnteInCombo(BigDecimal idEnte, ComboBox strut, long idUtente, Boolean filterValid)
            throws EMFError {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(idUtente, idEnte, filterValid);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");

            strut.setDecodeMap(mappaStrut);

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanStrut.size() == 1) {
                strut.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        }
    }

    /**
     * Inizializza le combo Ambiente/Ente/Struttura in base alle abilitazioni dell'utente, impostando già selezionata la
     * struttura eventualmente passata come parametro
     *
     * @param field
     *            campo {@link Fields}
     * @param idUtente
     *            id utente
     * @param idStrut
     *            id struttura
     * @param filterValid
     *            true/false
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void initGenericComboAmbienteEnteStruttura(Fields field, long idUtente, BigDecimal idStrut,
            Boolean filterValid) throws ParerUserError {
        // Azzero i filtri
        ((ComboBox) field.getComponent("id_ambiente")).reset();
        ((ComboBox) field.getComponent("id_ente")).reset();
        ((ComboBox) field.getComponent("id_strut")).reset();

        // Inizializzo le combo settando la struttura corrente
        OrgEnteTableBean tmpTableBeanEnte;
        OrgStrutTableBean tmpTableBeanStruttura;

        // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
        OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(idUtente);
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        ((ComboBox) field.getComponent("id_ambiente")).setDecodeMap(mappaAmbiente);

        // Ricavo i valori della combo ENTE
        DecodeMap mappaEnte = new DecodeMap();
        ((ComboBox) field.getComponent("id_ente")).setDecodeMap(mappaEnte);

        // Ricavo i valori della combo STRUTTURA
        DecodeMap mappaStrut = new DecodeMap();
        ((ComboBox) field.getComponent("id_strut")).setDecodeMap(mappaStrut);

        // Ricavo la struttura da selezionare nelle combo
        if (idStrut != null) {
            OrgStrutRowBean strut = elenchiVersamentoEjb.getOrgStrutRowBeanWithAmbienteEnte(idStrut);
            if (strut != null) {
                long idEnte = strut.getIdEnte().longValue();
                long idAmbiente = strut.getBigDecimal("id_ambiente").longValue();

                ((ComboBox) field.getComponent("id_ambiente")).setValue("" + idAmbiente);

                tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(idUtente, idAmbiente, filterValid);
                mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
                ((ComboBox) field.getComponent("id_ente")).setDecodeMap(mappaEnte);
                ((ComboBox) field.getComponent("id_ente")).setValue("" + idEnte);

                tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(idUtente, new BigDecimal(idEnte),
                        filterValid);
                mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
                ((ComboBox) field.getComponent("id_strut")).setDecodeMap(mappaStrut);
                ((ComboBox) field.getComponent("id_strut")).setValue("" + idStrut);
            }
        }
    }

    /**
     * Metodo statico che ritorna la data in formato stringa da impostare come valore agli oggetti di tipo input date Se
     * <code>inputDate</code> è null, ritorna la data di soppressione di default (31/12/2444)
     *
     * @param inputDate
     *            data da valutare
     *
     * @return la data in formato dd/MM/yyyy
     */
    public static String getStringDate(Date inputDate) {
        Calendar calendar = Calendar.getInstance();
        if (inputDate != null) {
            calendar.setTime(inputDate);
        } else {
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        Date date = calendar.getTime();
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);

        String dateString = formato.format(date);
        return dateString;
    }

    /**
     * Metodo statico che ritorna la data in formato stringa da impostare come valore agli oggetti di tipo input date Se
     * <code>inputDate</code> è null, ritorna la data di soppressione di default (31/12/2444)
     *
     * @param inputDate
     *            data da valutare
     *
     * @return la data in formato dd/MM/yyyy HH:mm
     */
    public static String getStringDateTime(Date inputDate) {
        Calendar calendar = Calendar.getInstance();
        if (inputDate != null) {
            calendar.setTime(inputDate);
        } else {
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        Date date = calendar.getTime();
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_HOUR_MINUTE_TYPE);

        String dateString = formato.format(date);
        return dateString;
    }

    public static JSONObject getConservUnlimitedTrigger(Fields<Field> fields) throws EMFError {
        ComboBox<String> conservUnlimited = ((ComboBox<String>) fields.getComponent("Conserv_unlimited"));
        Input<BigDecimal> niAnniConserv = ((Input<BigDecimal>) fields.getComponent("Ni_anni_conserv"));
        String unlimited = conservUnlimited.parse();
        BigDecimal anniConserv = niAnniConserv.parse();
        if (unlimited != null && unlimited.equals("1")) {
            niAnniConserv.setValue("9999");
        } else if (anniConserv != null && anniConserv.equals(new BigDecimal(9999))) {
            niAnniConserv.setValue(null);
        }
        return fields.asJSON();
    }

    /*
     * Metodo per consentire di effettuare un download completo di tutti i dati di una lista senza limitazioni. Il
     * parametro table viene passato anche se sarebbe stato ricavabile da fields perché questo metodo potrebbe essere
     * invocato anche se non si è popolata una lista a video.
     */
    public static void buildCsvString(it.eng.spagoLite.form.list.List<SingleValueField<?>> fields,
            BaseTableInterface<? extends BaseRowInterface> table, TableDescriptor tableDescriptor, File tmpFile)
            throws IOException {
        Assert.isNull(table.getLazyListInterface(),
                "Bisogna fornire una tabella con tutti i record caricati, non è possibile gestire la logica del LazyList durante l'estrazione su csv");
        final String csvSeparator = ";";
        final String quote = "\"";
        final String end = "\r\n";
        List<String> columns = new ArrayList<>();
        try (FileWriter fw = new FileWriter(tmpFile, true); BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            boolean isFirst = true;
            for (SingleValueField<?> field : fields.getComponentList()) {
                columns.add(field.getName().toLowerCase());
                if (isFirst) {
                    out.append(quote).append(field.getDescription()).append(quote);
                    isFirst = false;
                } else {
                    out.append(csvSeparator).append(quote).append(field.getDescription()).append(quote);
                }
            }
            out.append(end);
            Map<String, ColumnDescriptor> columnMap = tableDescriptor.getColumnMap();
            //

            /*
             * if (table.getLazyListBean() != null) { int max = (table.getLazyListBean() != null) ?
             * table.getLazyListBean().getMaxResult() : table.fullSize(); int count = table.fullSize(); double
             * quantiPassi = Math.ceil(count / max);
             *
             * for (int i = 0; i <= quantiPassi; i++) {
             *
             * int pageToOpen = (i * table.getLazyListBean().getMaxResult()) / table.getPageSize(); table =
             * action.getPaginator().goPage(table, i > 0 ? pageToOpen + 1 : pageToOpen); writeTable(table, columnMap,
             * columns, out); } } else {
             */
            if (fields.getTable() != null) {
                fields.getTable().setCurrentRowIndex(0);
            }
            writeTable(table, columnMap, columns, out);
            /* } */

        } catch (IOException e) {
            throw e;
        }
    }

    private static void writeTable(BaseTableInterface<? extends BaseRowInterface> table,
            Map<String, ColumnDescriptor> columnMap, List<String> columns, PrintWriter out) {
        final String csvSeparator = ";";
        final String quote = "\"";
        final String end = "\r\n";
        for (BaseRowInterface row : table) {
            boolean isFirst = true;
            for (String column : columns) {
                if (!isFirst) {
                    out.append(csvSeparator);
                } else {
                    isFirst = false;
                }
                ColumnDescriptor descriptor = columnMap.get(column);
                // if (descriptor != null) {
                switch (descriptor.getType()) {
                case Types.DECIMAL:
                    out.append(row.getBigDecimal(column) != null ? row.getBigDecimal(column).toPlainString() : "");
                    break;
                case Types.VARCHAR:
                    String str = (row.getString(column) == null) ? "" : row.getString(column);
                    out.append(quote).append(str).append(quote);
                    break;
                case Types.TIMESTAMP:
                    SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
                    // Date date = new Date(row.getTimestamp(column).getTime());
                    out.append((row.getTimestamp(column) != null)
                            ? df.format(new Date(row.getTimestamp(column).getTime())) : "");
                    break;
                }
            }
            // }
            out.append(end);
        }
    }

}
