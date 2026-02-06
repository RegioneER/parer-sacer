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

package it.eng.parer.web.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroWarnUpdUnitaDoc;
import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecControlloWs;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.MonContaSesUpdUdKo;
import it.eng.parer.entity.VrsErrSesUpdUnitaDocErr;
import it.eng.parer.entity.VrsErrSesUpdUnitaDocKo;
import it.eng.parer.entity.VrsSesUpdUnitaDocKo;
import it.eng.parer.entity.VrsUpdUnitaDocKo;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.SacerRuntimeException;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocErrRowBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocErrTableBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocKoRowBean;
import it.eng.parer.slite.gen.tablebean.VrsErrSesUpdUnitaDocKoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdCompUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdDocUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdKoRisoltiTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUpdUdErrRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUpdUdErrTableBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUdUpdKoRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUpdUdErrRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUpdUdKoRowBean;
import it.eng.parer.viewEntity.AroVLisUpdCompUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdDocUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdKoRisolti;
import it.eng.parer.viewEntity.AroVVisUpdUnitaDoc;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.viewEntity.MonVLisUdUpdKoInterface;
import it.eng.parer.viewEntity.MonVLisUpdUdErr;
import it.eng.parer.viewEntity.MonVLisUpdUdInterface;
import it.eng.parer.viewEntity.MonVLisUpdUdKoInterface;
import it.eng.parer.viewEntity.MonVVisUdUpdKo;
import it.eng.parer.viewEntity.MonVVisUpdUdErr;
import it.eng.parer.viewEntity.MonVVisUpdUdKo;
import it.eng.parer.viewEntity.VrsVModifUpdUdKo;
import it.eng.parer.web.helper.MonitoraggioAggMetaHelper;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "MonitoraggioAggMetaEjb")
@LocalBean
public class MonitoraggioAggMetaEjb {

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioAggMetaHelper")
    private MonitoraggioAggMetaHelper monitoraggioAggMetaHelper;

    private static final Logger logger = LoggerFactory.getLogger(MonitoraggioAggMetaEjb.class);

    public enum fieldSetToPopulate {

        AGG_METADATI, AGG_METADATI_ATTESA_SCHED, AGG_METADATI_NON_SELEZ_SCHED, FALLITI_NON_RISOLTI,
        NON_RISOLUBILI, VERIFICATI, NON_VERIFICATI, AGG_METADATI_B30, AGG_METADATI_ATTESA_SCHED_B30,
        AGG_METADATI_NON_SELEZ_SCHED_B30, FALLITI_NON_RISOLTI_B30, NON_RISOLUBILI_B30,
        VERIFICATI_B30, NON_VERIFICATI_B30
    }

    public enum queryDaUtilizzare {

        MON_CHK_UPD_UD_COR_BY_AMB, MON_CHK_UPD_UD_COR_BY_ENTE, MON_CHK_UPD_UD_COR_BY_STRUT,
        MON_CNT_UPD_UD_NOCOR_BY_AMB, MON_CNT_UPD_UD_COR_BY_AMB, MON_CHK_UPD_UD_KO_COR_BY_AMB,
        MON_CNT_UPD_UD_KO_NOCOR_BY_AMB, MON_CNT_UPD_UD_KO_COR_BY_AMB
    }

    public enum StatoGenerazioneIndiceAip {
        IN_ATTESA_SCHED, NON_SELEZ_SCHED, IN_ELENCO_APERTO, IN_ELENCO_CHIUSO, IN_ELENCO_DA_CHIUDERE,
        IN_ELENCO_VALIDATO, IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI,
        IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA,
        IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO, IN_ELENCO_COMPLETATO
    }

    public enum Stato {
        NON_RISOLUBILE, VERIFICATO, NON_VERIFICATO
    }

    public BaseRow calcolaRiepilogoAggMeta(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
            BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
        Map<String, Object> risultati;
        BaseRow campi = new BaseRow();

        if (idStrut != null) {
            risultati = monitoraggioAggMetaHelper.getTotali("STRUTTURA", idUser, idAmbiente, idEnte,
                    idStrut, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc,
                    idRegistroUnitaDoc, idTipoDocPrinc);
        } else if (idEnte != null) {
            risultati = monitoraggioAggMetaHelper.getTotali("ENTE", idUser, idAmbiente, idEnte,
                    null, null, null, null, null, null, null);
        } else if (idAmbiente != null) {
            risultati = monitoraggioAggMetaHelper.getTotali("AMBIENTE", idUser, idAmbiente, null,
                    null, null, null, null, null, null, null);
        } else {
            throw new RuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaRiepilogoAggMeta() dell'ejb!");
        }

        resetCounters(campi);
        assignValueFields(campi, risultati);

        return campi;
    }

    public BaseRow calcolaRiepilogoAggMetaDataCorrente(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
            BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
        Map<String, Object> risultati;
        BaseRow campi = new BaseRow();

        if (idStrut != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliDataCorrente("STRUTTURA", idUser,
                    idAmbiente, idEnte, idStrut, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA,
                    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
        } else if (idEnte != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliDataCorrente("ENTE", idUser, idAmbiente,
                    idEnte, null, null, null, null, null, null, null);
        } else if (idAmbiente != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliDataCorrente("AMBIENTE", idUser,
                    idAmbiente, null, null, null, null, null, null, null, null);
        } else {
            throw new SacerRuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaRiepilogoAggMetaDataCorrente() dell'ejb!",
                    SacerErrorCategory.INTERNAL_ERROR);
        }

        resetCountersAggiornamentiMetadatiDataCorrente(campi);
        assignValueFieldsDataCorrente(campi, risultati);

        return campi;
    }

    public BaseRow calcolaRiepilogoAggMetaFalliti(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
            BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
        Map<String, Object> risultati;
        BaseRow campi = new BaseRow();

        if (idStrut != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliFalliti("STRUTTURA", idUser, idAmbiente,
                    idEnte, idStrut, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc,
                    idRegistroUnitaDoc, idTipoDocPrinc);
        } else if (idEnte != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliFalliti("ENTE", idUser, idAmbiente,
                    idEnte, null, null, null, null, null, null, null);
        } else if (idAmbiente != null) {
            risultati = monitoraggioAggMetaHelper.getTotaliFalliti("AMBIENTE", idUser, idAmbiente,
                    null, null, null, null, null, null, null, null);
        } else {
            throw new SacerRuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaRiepilogoAggMetaFalliti() dell'ejb!",
                    SacerErrorCategory.INTERNAL_ERROR);
        }

        resetCountersAggiornamentiMetadatiFallitiCorrente(campi);
        assignValueFieldsFalliti(campi, risultati);

        return campi;
    }

    // MEV#22438
    public BaseRow calcolaRiepilogoUnitaDocAggMetaFalliti(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
            BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
        Map<String, Object> risultati;
        BaseRow campi = new BaseRow();
        if (idStrut != null) {
            // "STRUTTURA"
            risultati = monitoraggioAggMetaHelper.getTotaliUdUpdFalliti("STRUTTURA", idUser,
                    idAmbiente, idEnte, idStrut, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA,
                    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
        } else if (idEnte != null) {
            // "ENTE"
            risultati = monitoraggioAggMetaHelper.getTotaliUdUpdFalliti("ENTE", idUser, idAmbiente,
                    idEnte, null, null, null, null, null, null, null);
        } else if (idAmbiente != null) {
            // "AMBIENTE"
            risultati = monitoraggioAggMetaHelper.getTotaliUdUpdFalliti("AMBIENTE", idUser,
                    idAmbiente, null, null, null, null, null, null, null, null);
        } else {
            throw new SacerRuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaRiepilogoUnitaDocAggMetaFalliti() dell'ejb!",
                    SacerErrorCategory.INTERNAL_ERROR);
        }

        resetCountersUnitaDocAggiornamentiMetadatiFalliti(campi);
        assignValueFieldsUdUpdFalliti(campi, risultati);

        return campi;
    }
    // end MEV#22438

    public void assignValueFields(BaseRow row, Map<String, Object> risultati) {
        List<Object[]> checkCorResult = (List<Object[]>) risultati.get("checkCorResult");

        List<Object[]> totNoCorResult = (List<Object[]>) risultati.get("totNoCorResult");
        List<Object[]> checkCorKoResult = (List<Object[]>) risultati.get("checkCorKoResult");

        List<Object[]> totNoCorKoResult = (List<Object[]>) risultati.get("totNoCorKoResult");

        // MEV#22438
        List<Object[]> checkUdUpdKoResult = (List<Object[]>) risultati.get("checkUdUpdKoResult");
        // end MEV#22438

        Object[] objCheckCorResult = checkCorResult.get(0);
        Object[] objCheckCorKoResult = checkCorKoResult.get(0);
        // MEV#22438
        Object[] objCheckUdUpdKoResult = checkUdUpdKoResult.get(0);
        // end MEV#22438

        /* FLAGS */
        row.setString("fl_upd_corr", String.valueOf(objCheckCorResult[2]));
        row.setString("fl_upd_attesa_sched_corr", String.valueOf(objCheckCorResult[3]));
        row.setString("fl_upd_nosel_sched_corr", String.valueOf(objCheckCorResult[4]));
        row.setString("fl_upd_ko_norisolub_corr", String.valueOf(objCheckCorKoResult[2]));
        row.setString("fl_upd_ko_verif_corr", String.valueOf(objCheckCorKoResult[3]));
        row.setString("fl_upd_ko_noverif_corr", String.valueOf(objCheckCorKoResult[4]));
        // MEV#22438
        row.setString("fl_ud_upd_ko_norisolub", String.valueOf(objCheckUdUpdKoResult[2]));
        row.setString("fl_ud_upd_ko_verif", String.valueOf(objCheckUdUpdKoResult[3]));
        row.setString("fl_ud_upd_ko_noverif", String.valueOf(objCheckUdUpdKoResult[4]));
        // end MEV#22438

        String flUpdKoTotaliCorr = "0";
        if (row.getString("fl_upd_ko_norisolub_corr").equals("1")
                || row.getString("fl_upd_ko_verif_corr").equals("1")
                || row.getString("fl_upd_ko_noverif_corr").equals("1")) {
            flUpdKoTotaliCorr = "1";
        }
        row.setString("fl_upd_ko_totali_corr", flUpdKoTotaliCorr);

        // MEV#22438
        String flUdUpdKoTotali = "0";
        if (row.getString("fl_ud_upd_ko_norisolub").equals("1")
                || row.getString("fl_ud_upd_ko_verif").equals("1")
                || row.getString("fl_ud_upd_ko_noverif").equals("1")) {
            flUdUpdKoTotali = "1";
        }
        row.setString("fl_ud_upd_ko_totali", flUdUpdKoTotali);
        // end MEV#22438

        /* TOTALI */
        for (Object[] objTotNoCorResult : totNoCorResult) {
            int l = objTotNoCorResult.length;
            String tiStatoUpdUd = (String) objTotNoCorResult[l - 3];
            String tiDtCreazione = (String) objTotNoCorResult[l - 2];
            BigDecimal niUpd = (BigDecimal) objTotNoCorResult[l - 1];

            if (tiStatoUpdUd.equals("TOTALE")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_30gg", row.getBigDecimal("ni_upd_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_b30gg", row.getBigDecimal("ni_upd_b30gg").add(niUpd));
                }
            } else if (tiStatoUpdUd.equals("IN_ATTESA_SCHED")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_attesa_sched_30gg",
                            row.getBigDecimal("ni_upd_attesa_sched_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_attesa_sched_b30gg",
                            row.getBigDecimal("ni_upd_attesa_sched_b30gg").add(niUpd));
                }
            } else if (tiStatoUpdUd.equals("NON_SELEZ_SCHED")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_nosel_sched_30gg",
                            row.getBigDecimal("ni_upd_nosel_sched_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_nosel_sched_b30gg",
                            row.getBigDecimal("ni_upd_nosel_sched_b30gg").add(niUpd));
                }
            }
        }

        for (Object[] objTotNoCorKoResult : totNoCorKoResult) {
            int l = objTotNoCorKoResult.length;

            String tiStatoUpdUdKo = (String) objTotNoCorKoResult[l - 3];
            String tiDtCreazione = (String) objTotNoCorKoResult[l - 2];
            BigDecimal niUpd = (BigDecimal) objTotNoCorKoResult[l - 1];

            if (tiStatoUpdUdKo.equals("NON_RISOLUBILE")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_ko_norisolub_30gg",
                            row.getBigDecimal("ni_upd_ko_norisolub_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_ko_norisolub_b30gg",
                            row.getBigDecimal("ni_upd_ko_norisolub_b30gg").add(niUpd));
                }
            } else if (tiStatoUpdUdKo.equals("NON_VERIFICATO")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_ko_noverif_30gg",
                            row.getBigDecimal("ni_upd_ko_noverif_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_ko_noverif_b30gg",
                            row.getBigDecimal("ni_upd_ko_noverif_b30gg").add(niUpd));
                }
            } else if (tiStatoUpdUdKo.equals("VERIFICATO")) {
                if (tiDtCreazione.equals("30gg")) {
                    row.setBigDecimal("ni_upd_ko_verif_30gg",
                            row.getBigDecimal("ni_upd_ko_verif_30gg").add(niUpd));
                } else if (tiDtCreazione.equals("before30gg")) {
                    row.setBigDecimal("ni_upd_ko_verif_b30gg",
                            row.getBigDecimal("ni_upd_ko_verif_b30gg").add(niUpd));
                }
            }
        }

        row.setBigDecimal("ni_upd_ko_totali_30gg",
                row.getBigDecimal("ni_upd_ko_norisolub_30gg")
                        .add(row.getBigDecimal("ni_upd_ko_noverif_30gg"))
                        .add(row.getBigDecimal("ni_upd_ko_verif_30gg")));
        row.setBigDecimal("ni_upd_ko_totali_b30gg",
                row.getBigDecimal("ni_upd_ko_norisolub_b30gg")
                        .add(row.getBigDecimal("ni_upd_ko_noverif_b30gg"))
                        .add(row.getBigDecimal("ni_upd_ko_verif_b30gg")));
    }

    public void assignValueFieldsDataCorrente(BaseRow row, Map<String, Object> risultati) {
        List<Object[]> totCorResult = (List<Object[]>) risultati.get("totCorResult");
        for (Object[] objTotCorResult : totCorResult) {
            int l = objTotCorResult.length;

            String tiStatoUpdUd = (String) objTotCorResult[l - 2];
            BigDecimal niUpd = (BigDecimal) objTotCorResult[l - 1];

            if (tiStatoUpdUd.equals("TOTALE")) {
                row.setBigDecimal("ni_upd_corr", row.getBigDecimal("ni_upd_corr").add(niUpd));
            } else if (tiStatoUpdUd.equals("IN_ATTESA_SCHED")) {
                row.setBigDecimal("ni_upd_attesa_sched_corr",
                        row.getBigDecimal("ni_upd_attesa_sched_corr").add(niUpd));
            } else if (tiStatoUpdUd.equals("NON_SELEZ_SCHED")) {
                row.setBigDecimal("ni_upd_nosel_sched_corr",
                        row.getBigDecimal("ni_upd_nosel_sched_corr").add(niUpd));
            }
        }
    }

    public void assignValueFieldsFalliti(BaseRow row, Map<String, Object> risultati) {
        List<Object[]> totCorKoResult = (List<Object[]>) risultati.get("totCorKoResult");
        for (Object[] objTotCorKoResult : totCorKoResult) {
            int l = objTotCorKoResult.length;

            String tiStatoUpdUdKo = (String) objTotCorKoResult[l - 2];
            BigDecimal niUpd = (BigDecimal) objTotCorKoResult[l - 1];

            if (tiStatoUpdUdKo.equals("NON_RISOLUBILE")) {
                row.setBigDecimal("ni_upd_ko_norisolub_corr",
                        row.getBigDecimal("ni_upd_ko_norisolub_corr").add(niUpd));
            } else if (tiStatoUpdUdKo.equals("NON_VERIFICATO")) {
                row.setBigDecimal("ni_upd_ko_noverif_corr",
                        row.getBigDecimal("ni_upd_ko_noverif_corr").add(niUpd));
            } else if (tiStatoUpdUdKo.equals("VERIFICATO")) {
                row.setBigDecimal("ni_upd_ko_verif_corr",
                        row.getBigDecimal("ni_upd_ko_verif_corr").add(niUpd));
            }
        }
        row.setBigDecimal("ni_upd_ko_totali_corr",
                row.getBigDecimal("ni_upd_ko_norisolub_corr")
                        .add(row.getBigDecimal("ni_upd_ko_noverif_corr"))
                        .add(row.getBigDecimal("ni_upd_ko_verif_corr")));
    }

    // MEV#22438
    public void assignValueFieldsUdUpdFalliti(BaseRow row, Map<String, Object> risultati) {
        List<Object[]> totUdUpdKoResult = (List<Object[]>) risultati.get("totUdUpdKoResult");
        for (Object[] objTotUdUpdKoResult : totUdUpdKoResult) {
            int l = objTotUdUpdKoResult.length;

            String tiStatoUpdUdKo = (String) objTotUdUpdKoResult[l - 2];
            BigDecimal niUpd = (BigDecimal) objTotUdUpdKoResult[l - 1];

            if (tiStatoUpdUdKo.equals("NON_RISOLUBILE")) {
                row.setBigDecimal("ni_ud_upd_ko_norisolub",
                        row.getBigDecimal("ni_ud_upd_ko_norisolub").add(niUpd));
            } else if (tiStatoUpdUdKo.equals("NON_VERIFICATO")) {
                row.setBigDecimal("ni_ud_upd_ko_noverif",
                        row.getBigDecimal("ni_ud_upd_ko_noverif").add(niUpd));
            } else if (tiStatoUpdUdKo.equals("VERIFICATO")) {
                row.setBigDecimal("ni_ud_upd_ko_verif",
                        row.getBigDecimal("ni_ud_upd_ko_verif").add(niUpd));
            }
        }
        row.setBigDecimal("ni_ud_upd_ko_totali",
                row.getBigDecimal("ni_ud_upd_ko_norisolub")
                        .add(row.getBigDecimal("ni_ud_upd_ko_noverif"))
                        .add(row.getBigDecimal("ni_ud_upd_ko_verif")));
    }
    // end MEV#22438

    public BaseTable ricercaAggMetaPerMonitoraggio(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoIndiceAip, String flSesUpdKoRisolti) {
        BaseTable t = new BaseTable();
        List<MonVLisUpdUdInterface> l = monitoraggioAggMetaHelper.retrieveVLisAggMeta(idUser,
                idAmbiente, idEnte, idStrut, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc,
                dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoIndiceAip,
                flSesUpdKoRisolti);

        for (MonVLisUpdUdInterface objInterface : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_upd_unita_doc", objInterface.getIdUpdUnitaDoc());
            r.setBigDecimal("id_unita_doc", objInterface.getIdUnitaDoc());
            r.setString("nm_ente", objInterface.getNmEnte());
            r.setString("nm_struttura", objInterface.getNmStrut());
            r.setString("ds_unita_doc", objInterface.getDsUnitaDoc());
            r.setBigDecimal("pg_upd_unita_doc", objInterface.getPgUpdUnitaDoc());
            r.setString("nm_tipo_unita_doc", objInterface.getNmTipoUnitaDoc());
            r.setString("nm_tipo_doc_princ", objInterface.getNmTipoDocPrinc());
            r.setTimestamp("ts_ini_ses", new Timestamp(objInterface.getTsIniSes().getTime()));
            r.setString("fl_forza_upd", objInterface.getFlForzaUpd());
            r.setString("nt_upd", objInterface.getNtUpd());
            r.setString("ti_stato_upd_elenco_vers", objInterface.getTiStatoUpdElencoVers());
            r.setString("fl_ses_upd_ko_risolti", objInterface.getFlSesUpdKoRisolti());
            r.setString("nm_ente_struttura",
                    objInterface.getNmEnte() + " / " + objInterface.getNmStrut());
            t.add(r);
        }
        return t;
    }

    public BaseTable ricercaAggMetaFallitiPerMonitoraggio(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
            BigDecimal idErr) {
        return ricercaAggMetaFallitiPerMonitoraggioInternal(idUser, idAmbiente, idEnte, idStrut,
                idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, idClasseErr, idErr);
    }

    public BaseTable ricercaAggMetaFallitiPerMonitoraggioErr(BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
            BigDecimal idErr) {
        return ricercaAggMetaFallitiPerMonitoraggioInternal(idUser, idAmbiente, idEnte, idStrut,
                idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, idClasseErr, idErr);
    }

    private BaseTable ricercaAggMetaFallitiPerMonitoraggioInternal(BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
            BigDecimal idErr) {
        BaseTable t = new BaseTable();
        List<MonVLisUpdUdKoInterface> l = monitoraggioAggMetaHelper.retrieveVLisAggMetaFalliti(
                idUser, idAmbiente, idEnte, idStrut, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc,
                dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione,
                idClasseErr, idErr);

        for (MonVLisUpdUdKoInterface objInterface : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_ses_upd_unita_doc_ko", objInterface.getIdSesUpdUnitaDocKo());
            r.setString("ds_ente_strut",
                    objInterface.getNmEnte() + " / " + objInterface.getNmStrut());
            r.setString("ds_unita_doc", objInterface.getDsUnitaDoc());
            r.setString("nm_struttura", objInterface.getNmStrut());
            r.setString("ds_unita_doc", objInterface.getDsUnitaDoc());
            r.setString("nm_tipo_unita_doc", objInterface.getNmTipoUnitaDoc());
            r.setString("nm_tipo_doc_princ", objInterface.getNmTipoDocPrinc());
            r.setTimestamp("ts_ini_ses", new Timestamp(objInterface.getTsIniSes().getTime()));
            r.setString("cd_ds_err_princ",
                    objInterface.getCdErrPrinc() + " / " + objInterface.getDsErrPrinc());
            r.setString("cd_controllo_ws_princ", objInterface.getCdControlloWsPrinc());
            r.setString("ti_stato_ses_upd_ko", objInterface.getTiStatoSesUpdKo());
            r.setString("scelto", "");
            t.add(r);
        }
        return t;
    }

    // MEV#22438
    public BaseTable ricercaUnitaDocAggMetaFallitiPerMonitoraggio(BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
            BigDecimal idErr) {
        return ricercaUnitaDocAggMetaFallitiPerMonitoraggioInternal(idUser, idAmbiente, idEnte,
                idStrut, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, idClasseErr, idErr);
    }

    private BaseTable ricercaUnitaDocAggMetaFallitiPerMonitoraggioInternal(BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
            String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
            BigDecimal idErr) {
        BaseTable t = new BaseTable();
        List<MonVLisUdUpdKoInterface> l = monitoraggioAggMetaHelper
                .retrieveVLisUnitaDocAggMetaFalliti(idUser, idAmbiente, idEnte, idStrut,
                        idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                        rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, idClasseErr, idErr);

        for (MonVLisUdUpdKoInterface objInterface : l) {
            BaseRow r = new BaseRow();
            r.setBigDecimal("id_upd_unita_doc_ko", objInterface.getIdUpdUnitaDocKo());
            r.setString("ds_ente_strut",
                    objInterface.getNmEnte() + " / " + objInterface.getNmStrut());
            r.setString("nm_struttura", objInterface.getNmStrut());
            r.setString("ds_unita_doc", objInterface.getDsUnitaDoc());
            r.setString("nm_tipo_unita_doc_last", objInterface.getNmTipoUnitaDocLast());
            r.setString("nm_tipo_doc_princ_last", objInterface.getNmTipoDocPrincLast());
            r.setTimestamp("ts_ini_last_ses",
                    new Timestamp(objInterface.getTsIniLastSes().getTime()));
            if (!"DIVERSI".equalsIgnoreCase(objInterface.getDsErrPrincLast())) {
                r.setString("cd_ds_err_princ_last", objInterface.getCdErrPrincLast() + " / "
                        + objInterface.getDsErrPrincLast());
            } else {
                r.setString("cd_ds_err_princ_last", objInterface.getDsErrPrincLast());
            }
            r.setString("cd_controllo_ws_princ_last", objInterface.getCdControlloWsPrincLast());
            r.setString("ti_stato_upd_ud_ko", objInterface.getTiStatoUpdUdKo());
            t.add(r);
        }
        return t;
    }
    // end MEV#22438

    public MonVLisUpdUdErrTableBean ricercaAggMetaErrati(Date[] dateValidate,
            Set<String> tiStatoIndice, BigDecimal idClasseErr, BigDecimal idErr) {
        MonVLisUpdUdErrTableBean t = new MonVLisUpdUdErrTableBean();
        List<MonVLisUpdUdErr> l = monitoraggioAggMetaHelper.retrieveVLisAggMetaErrati(dateValidate,
                tiStatoIndice, idClasseErr, idErr);
        Set<BigDecimal> id = new HashSet<>();

        try {
            for (MonVLisUpdUdErr updErr : l) {
                if (id.add(updErr.getIdSesUpdUnitaDocErr())) {
                    MonVLisUpdUdErrRowBean r = (MonVLisUpdUdErrRowBean) Transform
                            .entity2RowBean(updErr);
                    t.add(r);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero degli aggiornamenti metadati errati "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return t;
    }

    public DecodeMap getIdClasseErrSacerByTipiUsoDecodeMap(List<String> tipiUso) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecClasseErrSacer> l = monitoraggioAggMetaHelper
                .retrieveClasseErrSacerByTipiUso(tipiUso);
        for (DecClasseErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setBigDecimal("id_classe_err_sacer",
                    BigDecimal.valueOf(ogg.getIdClasseErrSacer()));
            riga.setString("cd_classe_err", ogg.getCdClasseErrSacer());
            riga.setString("ds_classe_err",
                    ogg.getCdClasseErrSacer() + " - " + ogg.getDsClasseErrSacer());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "id_classe_err_sacer", "ds_classe_err");
        return errori;
    }

    public DecodeMap getCdClasseErrSacerByTipiUsoDecodeMap(List<String> tipiUso) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecClasseErrSacer> l = monitoraggioAggMetaHelper
                .retrieveClasseErrSacerByTipiUso(tipiUso);
        for (DecClasseErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setString("cd_classe_err", ogg.getCdClasseErrSacer());
            riga.setString("ds_classe_err",
                    ogg.getCdClasseErrSacer() + " - " + ogg.getDsClasseErrSacer());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "cd_classe_err", "ds_classe_err");
        return errori;
    }

    public DecodeMap getIdClasseErrSacerDecodeMap(List<String> tipiUso) {
        return getIdClasseErrSacerByTipiUsoDecodeMap(tipiUso);
    }

    public DecodeMap getCdClasseErrSacerDecodeMap(List<String> tipiUso) {
        return getCdClasseErrSacerByTipiUsoDecodeMap(tipiUso);
    }

    public DecodeMap getErrSacerByCodClasseDecodeMap(String codClasse) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecErrSacer> l = monitoraggioAggMetaHelper.retrieveErrSacerByCodClasse(codClasse);
        for (DecErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setString("cd_err", ogg.getCdErr());
            riga.setString("ds_err", ogg.getCdErr() + " - " + ogg.getDsErr());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "cd_err", "ds_err");
        return errori;
    }

    public DecodeMap getErrSacerByIdClasseDecodeMap(BigDecimal idClasse) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecErrSacer> l = monitoraggioAggMetaHelper.retrieveErrSacerByIdClasse(idClasse);
        for (DecErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setBigDecimal("id_err_sacer", BigDecimal.valueOf(ogg.getIdErrSacer()));
            riga.setString("ds_err", ogg.getCdErr() + " - " + ogg.getDsErrFiltro());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "id_err_sacer", "ds_err");
        return errori;
    }

    public enum tipoUsoClasseErrore {
        GENERICO, VERS_UNITA_DOC
    }

    public void resetCounters(BaseRowInterface rowBean) {
        resetCountersAggiornamentiMetadati(rowBean);
        resetCountersAggiornamentiMetadatiDataCorrente(rowBean);
        resetCountersAggiornamentiMetadatiFalliti(rowBean);
        resetCountersAggiornamentiMetadatiFallitiCorrente(rowBean);
        // MEV#22438
        resetCountersUnitaDocAggiornamentiMetadatiFalliti(rowBean);
        // end MEV#22438
    }

    public void resetCountersAggiornamentiMetadati(BaseRowInterface rowBean) {
        rowBean.setBigDecimal("ni_upd_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_b30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_attesa_sched_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_attesa_sched_b30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_nosel_sched_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_nosel_sched_b30gg", BigDecimal.ZERO);
    }

    public void resetCountersAggiornamentiMetadatiDataCorrente(BaseRowInterface rowBean) {
        rowBean.setBigDecimal("ni_upd_corr", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_attesa_sched_corr", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_nosel_sched_corr", BigDecimal.ZERO);
    }

    public void resetCountersAggiornamentiMetadatiFalliti(BaseRowInterface rowBean) {
        rowBean.setBigDecimal("ni_upd_ko_norisolub_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_norisolub_b30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_verif_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_verif_b30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_noverif_30gg", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_noverif_b30gg", BigDecimal.ZERO);
    }

    public void resetCountersAggiornamentiMetadatiFallitiCorrente(BaseRowInterface rowBean) {
        rowBean.setBigDecimal("ni_upd_ko_totali_corr", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_norisolub_corr", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_verif_corr", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_upd_ko_noverif_corr", BigDecimal.ZERO);
    }

    // MEV#22438
    public void resetCountersUnitaDocAggiornamentiMetadatiFalliti(BaseRowInterface rowBean) {
        rowBean.setBigDecimal("ni_ud_upd_ko_totali", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_ud_upd_ko_norisolub", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_ud_upd_ko_verif", BigDecimal.ZERO);
        rowBean.setBigDecimal("ni_ud_upd_ko_noverif", BigDecimal.ZERO);
    }
    // end MEV#22438

    public MonVVisUpdUdKoRowBean getMonVVisUpdUdKoRowBean(BigDecimal idSesUpdUnitaDocKo) {
        MonVVisUpdUdKoRowBean rb = new MonVVisUpdUdKoRowBean();
        MonVVisUpdUdKo mon = monitoraggioAggMetaHelper.findViewById(MonVVisUpdUdKo.class,
                idSesUpdUnitaDocKo);
        try {
            rb = (MonVVisUpdUdKoRowBean) Transform.entity2RowBean(mon);
            rb.setString("cd_ds_err_princ",
                    mon.getCdErrPrinc() != null ? mon.getCdErrPrinc() + " - " + mon.getDsErrPrinc()
                            : "");
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della vista MonVVisUpdUdKo "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return rb;
    }

    // MEV#22438
    public MonVVisUdUpdKoRowBean getMonVVisUdUpdKoRowBean(BigDecimal idUpdUnitaDocKo) {
        MonVVisUdUpdKoRowBean rb = new MonVVisUdUpdKoRowBean();
        MonVVisUdUpdKo mon = monitoraggioAggMetaHelper.findViewById(MonVVisUdUpdKo.class,
                idUpdUnitaDocKo);
        try {
            rb = (MonVVisUdUpdKoRowBean) Transform.entity2RowBean(mon);
            rb.setString("cd_ds_err_princ",
                    mon.getCdErrPrinc() != null ? mon.getCdErrPrinc() + " - " + mon.getDsErrPrinc()
                            : "");
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della vista MonVVisUdUpdKo "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return rb;
    }
    // end MEV#22438

    public MonVVisUpdUdErrRowBean getMonVVisUpdUdErrRowBean(BigDecimal idSesUpdUnitaDocErr) {
        MonVVisUpdUdErrRowBean rb = new MonVVisUpdUdErrRowBean();
        MonVVisUpdUdErr mon = monitoraggioAggMetaHelper.findViewById(MonVVisUpdUdErr.class,
                idSesUpdUnitaDocErr);
        try {
            rb = (MonVVisUpdUdErrRowBean) Transform.entity2RowBean(mon);
            rb.setString("cd_ds_err_princ",
                    mon.getCdErrPrinc() != null ? mon.getCdErrPrinc() + " - " + mon.getDsErrPrinc()
                            : "");
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della vista MonVVisUpdUdErr "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return rb;
    }

    public AroVVisUpdUnitaDocRowBean getAroVVisUpdUnitaDocRowBean(BigDecimal idUpdUnitaDoc) {
        AroVVisUpdUnitaDocRowBean rb = new AroVVisUpdUnitaDocRowBean();
        AroVVisUpdUnitaDoc aro = monitoraggioAggMetaHelper.findViewById(AroVVisUpdUnitaDoc.class,
                idUpdUnitaDoc);
        try {
            rb = (AroVVisUpdUnitaDocRowBean) Transform.entity2RowBean(aro);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della vista AroVVisUpdUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return rb;
    }

    public AroVLisUpdDocUnitaDocTableBean getAroVLisUpdDocUnitaDocTableBean(
            BigDecimal idUpdUnitaDoc) {
        AroVLisUpdDocUnitaDocTableBean tb = new AroVLisUpdDocUnitaDocTableBean();
        List<AroVLisUpdDocUnitaDoc> list = monitoraggioAggMetaHelper
                .getAroVLisUpdDocUnitaDocList(idUpdUnitaDoc);
        try {
            if (list != null && !list.isEmpty()) {
                tb = (AroVLisUpdDocUnitaDocTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella AroVLisUpdDocUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public AroVLisUpdCompUnitaDocTableBean getAroVLisUpdCompUnitaDocTableBean(
            BigDecimal idUpdUnitaDoc) {
        AroVLisUpdCompUnitaDocTableBean tb = new AroVLisUpdCompUnitaDocTableBean();
        List<AroVLisUpdCompUnitaDoc> list = monitoraggioAggMetaHelper
                .getAroVLisUpdCompUnitaDocList(idUpdUnitaDoc);
        try {
            if (list != null && !list.isEmpty()) {
                tb = (AroVLisUpdCompUnitaDocTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella AroVLisUpdCompUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public AroVLisUpdKoRisoltiTableBean getAroVLisUpdKoRisoltiTableBean(BigDecimal idUpdUnitaDoc) {
        AroVLisUpdKoRisoltiTableBean tb = new AroVLisUpdKoRisoltiTableBean();
        List<AroVLisUpdKoRisolti> list = monitoraggioAggMetaHelper
                .getAroVLisUpdKoRisoltiList(idUpdUnitaDoc);
        try {
            if (list != null && !list.isEmpty()) {
                tb = (AroVLisUpdKoRisoltiTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella AroVLisUpdKoRisolti "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public AroWarnUpdUnitaDocTableBean getAroWarnUpdUnitaDocTableBean(BigDecimal idUpdUnitaDoc) {
        AroWarnUpdUnitaDocTableBean tb = new AroWarnUpdUnitaDocTableBean();
        List<AroWarnUpdUnitaDoc> list = monitoraggioAggMetaHelper
                .getAroWarnUpdUnitaDocList(idUpdUnitaDoc);
        try {
            if (list != null && !list.isEmpty()) {
                tb = (AroWarnUpdUnitaDocTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella AroWarnUpdUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public VrsErrSesUpdUnitaDocKoTableBean ricercaVersamentiErrSesUpdUnitaDocKo(
            BigDecimal idSesUpdUnitaDocKo) {
        VrsErrSesUpdUnitaDocKoTableBean tb = new VrsErrSesUpdUnitaDocKoTableBean();
        List<VrsErrSesUpdUnitaDocKo> list = monitoraggioAggMetaHelper
                .getVrsErrSesUpdUnitaDocKoList(idSesUpdUnitaDocKo);
        try {
            for (VrsErrSesUpdUnitaDocKo ses : list) {
                VrsErrSesUpdUnitaDocKoRowBean rb = (VrsErrSesUpdUnitaDocKoRowBean) Transform
                        .entity2RowBean(ses);
                rb.setString("cd_ds_err", ses.getDecErrSacer().getCdErr() + " - " + ses.getDsErr());
                rb.setString("cd_controllo_ws", ses.getDecControlloWs().getCdControlloWs());
                tb.add(rb);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella VrsErrSesUpdUnitaDocKo "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public List<Long> ricercaVrsSesUpdUnitaDocKo(BigDecimal idUpdUnitaDocKo) {
        // VrsSesUpdUnitaDocKoTableBean tb = new VrsSesUpdUnitaDocKoTableBean();
        return monitoraggioAggMetaHelper.getVrsSesUpdUnitaDocKoList(idUpdUnitaDocKo);

        // return tb;
    }

    public VrsErrSesUpdUnitaDocKoTableBean ricercaVersamentiErrSesUpdUnitaDocKo(
            List<Long> idSesUpdUnitaDocKoList) {
        VrsErrSesUpdUnitaDocKoTableBean tb = new VrsErrSesUpdUnitaDocKoTableBean();
        List<VrsErrSesUpdUnitaDocKo> list = monitoraggioAggMetaHelper
                .getVrsErrSesUpdUnitaDocKoList(idSesUpdUnitaDocKoList);
        try {
            for (VrsErrSesUpdUnitaDocKo ses : list) {
                VrsErrSesUpdUnitaDocKoRowBean rb = new VrsErrSesUpdUnitaDocKoRowBean();
                rb = (VrsErrSesUpdUnitaDocKoRowBean) Transform.entity2RowBean(ses);
                rb.setString("cd_ds_err", ses.getDecErrSacer().getCdErr() + " - " + ses.getDsErr());
                rb.setString("cd_controllo_ws", ses.getDecControlloWs().getCdControlloWs());
                tb.add(rb);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei dati della tabella VrsErrSesUpdUnitaDocKo "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tb;
    }

    public VrsErrSesUpdUnitaDocErrTableBean ricercaVersamentiErrSesUpdUnitaDocErr(
            BigDecimal idSesUpdUnitaDocErr) {
        VrsErrSesUpdUnitaDocErrTableBean tb = new VrsErrSesUpdUnitaDocErrTableBean();
        List<VrsErrSesUpdUnitaDocErr> list = monitoraggioAggMetaHelper
                .getVrsErrSesUpdUnitaDocErrList(idSesUpdUnitaDocErr);
        try {
            for (VrsErrSesUpdUnitaDocErr rec : list) {
                VrsErrSesUpdUnitaDocErrRowBean rb = (VrsErrSesUpdUnitaDocErrRowBean) Transform
                        .entity2RowBean(rec);
                rb.setString("cd_ds_err", rec.getDecErrSacer().getCdErr() + " - " + rec.getDsErr());
                rb.setString("cd_controllo_ws", rec.getDecControlloWs().getCdControlloWs());
                tb.add(rb);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            logger.error(
                    "Errore durante il recupero dei dati della tabella VrsErrSesUpdUnitaDocErr "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        }
        return tb;
    }

    public void salvaStatoAggiornamentoFallito(BigDecimal idSesUpdUnitaDocKo,
            String tiStatoPreModifica, String statoArrivo) throws ParerInternalError {
        // Recupero la sessione di errore aggiornamento metadati
        VrsSesUpdUnitaDocKo sesUpdUnitaDocKo = monitoraggioAggMetaHelper
                .findByIdWithLock(VrsSesUpdUnitaDocKo.class, idSesUpdUnitaDocKo);
        // Il sistema assume lock esclusivo sull’aggiornamento fallito
        VrsUpdUnitaDocKo updUnitaDocKo = monitoraggioAggMetaHelper.findByIdWithLock(
                VrsUpdUnitaDocKo.class,
                sesUpdUnitaDocKo.getVrsUpdUnitaDocKo().getIdUpdUnitaDocKo());

        // Verifico lo stato di partenza prima della modifica
        if (!tiStatoPreModifica.equals(sesUpdUnitaDocKo.getTiStatoSesUpdKo().name())) {
            throw new ParerInternalError("L’aggiornamento fallito " + idSesUpdUnitaDocKo
                    + " non ha stato = " + tiStatoPreModifica);
        }

        // Il sistema verifica se per la chiave di totalizzazione degli aggiornamenti falliti
        // (data di inizio aggiornamento fallito + l’identificatore della struttura + lo stato di
        // partenza della
        // modifica
        // + l’anno dell’unità doc + gli identificatori del registro, del tipo unità doc e del tipo
        // doc principale,
        // dell’aggiornamento fallito),
        // esiste un totale aggiornamenti in MON_CONTA_SES_UPD_UD_KO, assumendo lock esclusivo
        MonContaSesUpdUdKo contaSes = monitoraggioAggMetaHelper.retrieveMonContaSesUpdUdKo(
                sesUpdUnitaDocKo.getTsIniSes(), sesUpdUnitaDocKo.getOrgStrut().getIdStrut(),
                sesUpdUnitaDocKo.getAaKeyUnitaDoc(),
                sesUpdUnitaDocKo.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                sesUpdUnitaDocKo.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
                sesUpdUnitaDocKo.getDecTipoDocPrinc().getIdTipoDoc(),
                sesUpdUnitaDocKo.getTiStatoSesUpdKo().name());
        if (contaSes != null) {
            // Assume lock esclusivo
            MonContaSesUpdUdKo contaSesLockato = monitoraggioAggMetaHelper
                    .findByIdWithLock(MonContaSesUpdUdKo.class, contaSes.getIdContaSesUpdUdKo());
            // Aggiorna il numero di aggiornamenti falliti sottraendo 1
            contaSesLockato
                    .setNiSesUpdUdKo(contaSesLockato.getNiSesUpdUdKo().subtract(BigDecimal.ONE));
        }

        // Aggiorna lo stato
        sesUpdUnitaDocKo.setTiStatoSesUpdKo(
                it.eng.parer.entity.constraint.VrsSesUpdUnitaDocKo.TiStatoSesUpdKo
                        .valueOf(statoArrivo));

        // Il sistema aggiorna unità doc derivante da aggiornamenti falliti,
        // a cui e’ relativo l’aggiornamento fallito, assegnando lo stato dell’unità doc mediante i
        // valori restituito
        // dalla vista
        // VRS_V_MODIF_UPD_UD_KO a cui si accede mediante l’identificatore della struttura e la
        // chiave dell’unità doc
        VrsVModifUpdUdKo modifUpd = monitoraggioAggMetaHelper.findViewById(VrsVModifUpdUdKo.class,
                updUnitaDocKo.getIdUpdUnitaDocKo());
        updUnitaDocKo.setVrsSesUpdUnitaDocKoFirst(monitoraggioAggMetaHelper
                .findById(VrsSesUpdUnitaDocKo.class, modifUpd.getIdSesUpdUdKoFirst()));
        updUnitaDocKo.setVrsSesUpdUnitaDocKoLast(monitoraggioAggMetaHelper
                .findById(VrsSesUpdUnitaDocKo.class, modifUpd.getIdSesUpdUdKoLast()));
        updUnitaDocKo.setDecTipoUnitaDocLast(monitoraggioAggMetaHelper
                .findById(DecTipoUnitaDoc.class, modifUpd.getIdTipoUnitaDocLast()));
        if (modifUpd.getIdErrSacerPrinc() != null) {
            updUnitaDocKo.setDecErrSacerPrinc(monitoraggioAggMetaHelper.findById(DecErrSacer.class,
                    modifUpd.getIdErrSacerPrinc()));
        }
        if (modifUpd.getIdControlloWsPrinc() != null) {
            updUnitaDocKo.setDecControlloWsPrinc(monitoraggioAggMetaHelper
                    .findById(DecControlloWs.class, modifUpd.getIdControlloWsPrinc()));
        }
        updUnitaDocKo.setDecTipoDocPrincLast(monitoraggioAggMetaHelper.findById(DecTipoDoc.class,
                modifUpd.getIdTipoDocPrincLast()));
        updUnitaDocKo.setDecRegistroUnitaDocLast(monitoraggioAggMetaHelper
                .findById(DecRegistroUnitaDoc.class, modifUpd.getIdRegistroUnitaDocLast()));
        updUnitaDocKo.setTsIniFirstSes(modifUpd.getTsIniFirstSes());
        updUnitaDocKo.setTsIniLastSes(modifUpd.getTsIniLastSes());
        updUnitaDocKo.setDsErrPrinc(modifUpd.getDsErrPrinc());
        updUnitaDocKo
                .setTiStatoUdpUdKo(it.eng.parer.entity.constraint.VrsUpdUnitaDocKo.TiStatoUdpUdKo
                        .valueOf(modifUpd.getTiStatoUpdUdKo()));

        LogVVisLastSched visLastSched = monitoraggioAggMetaHelper
                .getLogVVisLastSched("CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI");
        // Se data di inizio aggiornamento fallito e’ minore dell’ultima data di attivazione del job
        // “CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI”
        if (sesUpdUnitaDocKo.getTsIniSes().before(visLastSched.getDtRegLogJobIni())) {
            // Il sistema verifica se per la chiave di totalizzazione degli aggiornamenti falliti
            // (data di inizio aggiornamento fallito + l’identificatore della struttura + lo stato
            // di arrivo della
            // modifica
            // + l’anno dell’unità doc + gli identificatori del registro, del tipo unità doc e del
            // tipo doc principale,
            // dell’aggiornamento fallito),
            // esiste un totale aggiornamenti in MON_CONTA_SES_UPD_UD_KO, assumendo lock esclusivo
            MonContaSesUpdUdKo contaSesArrivo = monitoraggioAggMetaHelper
                    .retrieveMonContaSesUpdUdKo(sesUpdUnitaDocKo.getTsIniSes(),
                            sesUpdUnitaDocKo.getOrgStrut().getIdStrut(),
                            sesUpdUnitaDocKo.getAaKeyUnitaDoc(),
                            sesUpdUnitaDocKo.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                            sesUpdUnitaDocKo.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
                            sesUpdUnitaDocKo.getDecTipoDocPrinc().getIdTipoDoc(), statoArrivo);
            if (contaSesArrivo != null) {
                // Assume lock esclusivo
                MonContaSesUpdUdKo contaSesLockato = monitoraggioAggMetaHelper.findByIdWithLock(
                        MonContaSesUpdUdKo.class, contaSesArrivo.getIdContaSesUpdUdKo());
                // Aggiorna il numero di aggiornamenti falliti sottraendo 1
                contaSesLockato
                        .setNiSesUpdUdKo(contaSesLockato.getNiSesUpdUdKo().add(BigDecimal.ONE));
            } else {
                MonContaSesUpdUdKo contaSesNuovo = new MonContaSesUpdUdKo();
                contaSesNuovo.setDtRifConta(sesUpdUnitaDocKo.getTsIniSes());
                contaSesNuovo.setTiStatoUdpUdKo(
                        it.eng.parer.entity.constraint.MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo
                                .valueOf(statoArrivo));
                if (contaSes != null) {
                    contaSesNuovo.setMonKeyTotalUdKo(contaSes.getMonKeyTotalUdKo());
                }
                contaSesNuovo.setNiSesUpdUdKo(BigDecimal.ONE);
                monitoraggioAggMetaHelper.insertEntity(contaSesNuovo, true);
            }
        }
    }
}
