package it.eng.parer.web.ejb;

import it.eng.parer.slite.gen.form.MonitoraggioSinteticoForm;
import it.eng.parer.slite.gen.viewbean.MonVChkCntAggRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntDocNonversRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntDocRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntUdAnnulRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntUdNonversRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntUdRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntVersRowBean;
import it.eng.parer.viewEntity.MonVChkAggAmb;
import it.eng.parer.viewEntity.MonVChkAggEnte;
import it.eng.parer.viewEntity.MonVChkAggStrut;
import it.eng.parer.viewEntity.MonVChkDocAmb;
import it.eng.parer.viewEntity.MonVChkDocEnte;
import it.eng.parer.viewEntity.MonVChkDocNonversAmb;
import it.eng.parer.viewEntity.MonVChkDocNonversEnte;
import it.eng.parer.viewEntity.MonVChkDocNonversStrut;
import it.eng.parer.viewEntity.MonVChkDocStrut;
import it.eng.parer.viewEntity.MonVChkDocTipoUd;
import it.eng.parer.viewEntity.MonVChkUdAmb;
import it.eng.parer.viewEntity.MonVChkUdAnnulAmb;
import it.eng.parer.viewEntity.MonVChkUdAnnulEnte;
import it.eng.parer.viewEntity.MonVChkUdAnnulStrut;
import it.eng.parer.viewEntity.MonVChkUdAnnulTipoUd;
import it.eng.parer.viewEntity.MonVChkUdEnte;
import it.eng.parer.viewEntity.MonVChkUdNonversAmb;
import it.eng.parer.viewEntity.MonVChkUdNonversEnte;
import it.eng.parer.viewEntity.MonVChkUdNonversStrut;
import it.eng.parer.viewEntity.MonVChkUdStrut;
import it.eng.parer.viewEntity.MonVChkUdTipoUd;
import it.eng.parer.viewEntity.MonVChkVersAmb;
import it.eng.parer.viewEntity.MonVChkVersEnte;
import it.eng.parer.viewEntity.MonVChkVersStrut;
import it.eng.parer.viewEntity.MonVCntAggAmb;
import it.eng.parer.viewEntity.MonVCntAggAmbB30;
import it.eng.parer.viewEntity.MonVCntAggEnte;
import it.eng.parer.viewEntity.MonVCntAggEnteB30;
import it.eng.parer.viewEntity.MonVCntAggStrut;
import it.eng.parer.viewEntity.MonVCntAggStrutB30;
import it.eng.parer.viewEntity.MonVCntDocAmb;
import it.eng.parer.viewEntity.MonVCntDocAmbB30;
import it.eng.parer.viewEntity.MonVCntDocEnte;
import it.eng.parer.viewEntity.MonVCntDocEnteB30;
import it.eng.parer.viewEntity.MonVCntDocNonversAmb;
import it.eng.parer.viewEntity.MonVCntDocNonversEnte;
import it.eng.parer.viewEntity.MonVCntDocNonversStrut;
import it.eng.parer.viewEntity.MonVCntDocStrut;
import it.eng.parer.viewEntity.MonVCntDocStrutB30;
import it.eng.parer.viewEntity.MonVCntDocTipoUd;
import it.eng.parer.viewEntity.MonVCntDocTipoUdB30;
import it.eng.parer.viewEntity.MonVCntUdAmb;
import it.eng.parer.viewEntity.MonVCntUdAmbB30;
import it.eng.parer.viewEntity.MonVCntUdAnnulAmb;
import it.eng.parer.viewEntity.MonVCntUdAnnulEnte;
import it.eng.parer.viewEntity.MonVCntUdAnnulStrut;
import it.eng.parer.viewEntity.MonVCntUdAnnulTipoUd;
import it.eng.parer.viewEntity.MonVCntUdEnte;
import it.eng.parer.viewEntity.MonVCntUdEnteB30;
import it.eng.parer.viewEntity.MonVCntUdNonversAmb;
import it.eng.parer.viewEntity.MonVCntUdNonversEnte;
import it.eng.parer.viewEntity.MonVCntUdNonversStrut;
import it.eng.parer.viewEntity.MonVCntUdStrut;
import it.eng.parer.viewEntity.MonVCntUdStrutB30;
import it.eng.parer.viewEntity.MonVCntUdTipoUd;
import it.eng.parer.viewEntity.MonVCntUdTipoUdB30;
import it.eng.parer.viewEntity.MonVCntVersAmb;
import it.eng.parer.viewEntity.MonVCntVersAmbB30;
import it.eng.parer.viewEntity.MonVCntVersEnte;
import it.eng.parer.viewEntity.MonVCntVersEnteB30;
import it.eng.parer.viewEntity.MonVCntVersStrut;
import it.eng.parer.viewEntity.MonVCntVersStrutB30;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.helper.MonitoraggioSinteticoHelper;
import it.eng.spagoLite.db.base.BaseRowInterface;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "MonitoraggioSinteticoEjb")
@LocalBean
public class MonitoraggioSinteticoEjb {

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioSinteticoHelper")
    private MonitoraggioSinteticoHelper monitSintHelper;

    private static final Logger logger = LoggerFactory.getLogger(MonitoraggioSinteticoEjb.class);

    public enum fieldSetToPopulate {

        UNITA_DOC_VERSATE, DOC_AGGIUNTI, VERS_FALLITI, AGGIUNTE_DOC_FALLITE, UNITA_DOC_VERSATE_B30, DOC_AGGIUNTI_B30,
        VERS_FALLITI_B30, AGGIUNTE_DOC_FALLITE_B30, UNITA_DOC_VERS_FALLITI, DOC_AGGIUNTI_VERS_FALLITI, UNITA_DOC_ANNUL
    }

    public BigDecimal getAmbiente(BigDecimal idEnte) {
        return monitoraggioHelper.getIdAmbiente(idEnte);
    }

    public BigDecimal getEnte(BigDecimal idStruttura) {
        return monitoraggioHelper.getIdEnte(idStruttura);
    }

    public LinkedHashMap<String, BaseRowInterface> calcolaRiepilogoSintetico(long idUtente, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc, Set<String> fieldsWithCnt) {
        LinkedHashMap<String, BaseRowInterface> map = new LinkedHashMap<>();

        // Caricamento dati unità doc versate
        MonVChkCntUdRowBean rowBeanUd;
        if (fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_VERSATE.name())
                || fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_VERSATE_B30.name())) {
            rowBeanUd = (MonVChkCntUdRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                    fieldSetToPopulate.UNITA_DOC_VERSATE,
                    fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_VERSATE.name()),
                    fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_VERSATE_B30.name()));
        } else {
            rowBeanUd = new MonVChkCntUdRowBean();
            rowBeanUd.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                    fieldSetToPopulate.UNITA_DOC_VERSATE));
        }

        map.put(MonitoraggioSinteticoForm.UnitaDocVersate.NAME, rowBeanUd);

        MonVChkCntDocRowBean rowBeanDoc;
        if (fieldsWithCnt.contains(fieldSetToPopulate.DOC_AGGIUNTI.name())
                || fieldsWithCnt.contains(fieldSetToPopulate.DOC_AGGIUNTI_B30.name())) {
            rowBeanDoc = (MonVChkCntDocRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                    fieldSetToPopulate.DOC_AGGIUNTI, fieldsWithCnt.contains(fieldSetToPopulate.DOC_AGGIUNTI.name()),
                    fieldsWithCnt.contains(fieldSetToPopulate.DOC_AGGIUNTI_B30.name()));
        } else {
            rowBeanDoc = new MonVChkCntDocRowBean();
            rowBeanDoc.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                    fieldSetToPopulate.DOC_AGGIUNTI));
        }
        map.put(MonitoraggioSinteticoForm.DocAggiunti.NAME, rowBeanDoc);

        MonVChkCntVersRowBean rowBeanVers = new MonVChkCntVersRowBean();
        MonVChkCntAggRowBean rowBeanAgg = new MonVChkCntAggRowBean();
        MonVChkCntUdNonversRowBean rowBeanUdNonVers = new MonVChkCntUdNonversRowBean();
        MonVChkCntDocNonversRowBean rowBeanDocNonVers = new MonVChkCntDocNonversRowBean();
        if (idTipoUnitaDoc == null) {

            if (fieldsWithCnt.contains(fieldSetToPopulate.VERS_FALLITI.name())
                    || fieldsWithCnt.contains(fieldSetToPopulate.VERS_FALLITI_B30.name())) {
                rowBeanVers = (MonVChkCntVersRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.VERS_FALLITI, fieldsWithCnt.contains(fieldSetToPopulate.VERS_FALLITI.name()),
                        fieldsWithCnt.contains(fieldSetToPopulate.VERS_FALLITI_B30.name()));
            } else {
                rowBeanVers.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.VERS_FALLITI));
            }

            if (fieldsWithCnt.contains(fieldSetToPopulate.AGGIUNTE_DOC_FALLITE.name())
                    || fieldsWithCnt.contains(fieldSetToPopulate.AGGIUNTE_DOC_FALLITE_B30.name())) {
                rowBeanAgg = (MonVChkCntAggRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.AGGIUNTE_DOC_FALLITE,
                        fieldsWithCnt.contains(fieldSetToPopulate.AGGIUNTE_DOC_FALLITE.name()),
                        fieldsWithCnt.contains(fieldSetToPopulate.AGGIUNTE_DOC_FALLITE_B30.name()));
            } else {
                rowBeanAgg.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.AGGIUNTE_DOC_FALLITE));
            }

            if (fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_VERS_FALLITI.name())) {
                rowBeanUdNonVers = (MonVChkCntUdNonversRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut,
                        idTipoUnitaDoc, fieldSetToPopulate.UNITA_DOC_VERS_FALLITI, false);
            } else {
                rowBeanUdNonVers.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.UNITA_DOC_VERS_FALLITI));
            }

            if (fieldsWithCnt.contains(fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI.name())) {
                rowBeanDocNonVers = (MonVChkCntDocNonversRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut,
                        idTipoUnitaDoc, fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI, false);
            } else {
                rowBeanDocNonVers.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                        fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI));
            }
        }
        map.put(MonitoraggioSinteticoForm.VersamentiFalliti.NAME, rowBeanVers);
        map.put(MonitoraggioSinteticoForm.AggiunteDocumentiFallite.NAME, rowBeanAgg);
        map.put(MonitoraggioSinteticoForm.UnitaDocDaVersFalliti.NAME, rowBeanUdNonVers);
        map.put(MonitoraggioSinteticoForm.DocDaVersFalliti.NAME, rowBeanDocNonVers);

        MonVChkCntUdAnnulRowBean rowBeanUdAnnul;
        if (fieldsWithCnt.contains(fieldSetToPopulate.UNITA_DOC_ANNUL.name())) {
            rowBeanUdAnnul = (MonVChkCntUdAnnulRowBean) calcolaTot(idUtente, idAmbiente, idEnte, idStrut,
                    idTipoUnitaDoc, fieldSetToPopulate.UNITA_DOC_ANNUL, false);
        } else {
            rowBeanUdAnnul = new MonVChkCntUdAnnulRowBean();
            rowBeanUdAnnul.entityToRowBean(buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                    fieldSetToPopulate.UNITA_DOC_ANNUL));
        }
        map.put(MonitoraggioSinteticoForm.UnitaDocAnnul.NAME, rowBeanUdAnnul);

        return map;
    }

    private Object buildQueryChk(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, fieldSetToPopulate fieldSetToBuild) throws IllegalArgumentException {
        String view = null;
        String paramString = null;
        BigDecimal param1 = null;
        Long param2 = null;
        if (idTipoUnitaDoc != null) {

            switch (fieldSetToBuild) {
            case UNITA_DOC_VERSATE:
                view = MonVChkUdTipoUd.class.getSimpleName();
                break;
            case DOC_AGGIUNTI:
                view = MonVChkDocTipoUd.class.getSimpleName();
                break;
            case UNITA_DOC_ANNUL:
                view = MonVChkUdAnnulTipoUd.class.getSimpleName();
                break;
            case VERS_FALLITI:
            case AGGIUNTE_DOC_FALLITE:
            case UNITA_DOC_VERS_FALLITI:
            case DOC_AGGIUNTI_VERS_FALLITI:
                // Caso inesistente - lancia eccezione
            default:
                throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo riepilogo sintetico");
            }

            paramString = "view.idTipoUnitaDoc = :param1";
            param1 = idTipoUnitaDoc;
        } else if (idStrut != null) {
            switch (fieldSetToBuild) {
            case UNITA_DOC_VERSATE:
                view = MonVChkUdStrut.class.getSimpleName();
                break;
            case DOC_AGGIUNTI:
                view = MonVChkDocStrut.class.getSimpleName();
                break;
            case VERS_FALLITI:
                view = MonVChkVersStrut.class.getSimpleName();
                break;
            case AGGIUNTE_DOC_FALLITE:
                view = MonVChkAggStrut.class.getSimpleName();
                break;
            case UNITA_DOC_VERS_FALLITI:
                view = MonVChkUdNonversStrut.class.getSimpleName();
                break;
            case DOC_AGGIUNTI_VERS_FALLITI:
                view = MonVChkDocNonversStrut.class.getSimpleName();
                break;
            case UNITA_DOC_ANNUL:
                view = MonVChkUdAnnulStrut.class.getSimpleName();
                break;
            default:
                throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo riepilogo sintetico");
            }
            paramString = "view.idStrut = :param1";
            param1 = idStrut;
        } else if (idEnte != null) {
            switch (fieldSetToBuild) {
            case UNITA_DOC_VERSATE:
                view = MonVChkUdEnte.class.getSimpleName();
                break;
            case DOC_AGGIUNTI:
                view = MonVChkDocEnte.class.getSimpleName();
                break;
            case VERS_FALLITI:
                view = MonVChkVersEnte.class.getSimpleName();
                break;
            case AGGIUNTE_DOC_FALLITE:
                view = MonVChkAggEnte.class.getSimpleName();
                break;
            case UNITA_DOC_VERS_FALLITI:
                view = MonVChkUdNonversEnte.class.getSimpleName();
                break;
            case DOC_AGGIUNTI_VERS_FALLITI:
                view = MonVChkDocNonversEnte.class.getSimpleName();
                break;
            case UNITA_DOC_ANNUL:
                view = MonVChkUdAnnulEnte.class.getSimpleName();
                break;
            default:
                throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo riepilogo sintetico");
            }
            paramString = "view.idEnte = :param1 AND view.idUserIam = :param2";
            param1 = idEnte;
            param2 = idUtente;
        } else if (idAmbiente != null) {
            switch (fieldSetToBuild) {
            case UNITA_DOC_VERSATE:
                view = MonVChkUdAmb.class.getSimpleName();
                break;
            case DOC_AGGIUNTI:
                view = MonVChkDocAmb.class.getSimpleName();
                break;
            case VERS_FALLITI:
                view = MonVChkVersAmb.class.getSimpleName();
                break;
            case AGGIUNTE_DOC_FALLITE:
                view = MonVChkAggAmb.class.getSimpleName();
                break;
            case UNITA_DOC_VERS_FALLITI:
                view = MonVChkUdNonversAmb.class.getSimpleName();
                break;
            case DOC_AGGIUNTI_VERS_FALLITI:
                view = MonVChkDocNonversAmb.class.getSimpleName();
                break;
            case UNITA_DOC_ANNUL:
                view = MonVChkUdAnnulAmb.class.getSimpleName();
                break;
            default:
                throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo riepilogo sintetico");
            }
            paramString = "view.idAmbiente = :param1 AND view.idUserIam = :param2";
            param1 = idAmbiente;
            param2 = idUtente;
        } else {
            throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo riepilogo sintetico");
        }
        Object obj = monitSintHelper.getMonVChk(view, paramString, param1, param2);
        return obj;
    }

    public BaseRowInterface calcolaTot(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, fieldSetToPopulate type, boolean tot30gg, boolean isBefore30) {
        BaseRowInterface rowBean = null;
        BaseRowInterface appo = null;
        if (isBefore30) {
            appo = calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc, type, isBefore30);
        }
        if (tot30gg) {
            rowBean = calcolaTot(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc, type, false);
            if (isBefore30) {
                switch (type) {
                case UNITA_DOC_VERSATE:
                    azzeraB30ConutersUd(rowBean);
                    rowBean.setObject("ni_ud_b30gg", appo.getBigDecimal("ni_ud_b30gg"));
                    rowBean.setObject("ni_ud_attesa_mem_b30gg", appo.getBigDecimal("ni_ud_attesa_mem_b30gg"));
                    rowBean.setObject("ni_ud_attesa_sched_b30gg", appo.getBigDecimal("ni_ud_attesa_sched_b30gg"));
                    rowBean.setObject("ni_ud_nosel_sched_b30gg", appo.getBigDecimal("ni_ud_nosel_sched_b30gg"));

                    break;
                case DOC_AGGIUNTI:
                    azzeraB30ConutersDoc(rowBean);
                    rowBean.setObject("ni_doc_b30gg", appo.getBigDecimal("ni_doc_b30gg"));
                    rowBean.setObject("ni_doc_attesa_mem_b30gg", appo.getBigDecimal("ni_doc_attesa_mem_b30gg"));
                    rowBean.setObject("ni_doc_attesa_sched_b30gg", appo.getBigDecimal("ni_doc_attesa_sched_b30gg"));
                    rowBean.setObject("ni_doc_nosel_sched_b30gg", appo.getBigDecimal("ni_doc_nosel_sched_b30gg"));

                    break;
                case VERS_FALLITI:
                    azzeraB30ConutersVers(rowBean);
                    rowBean.setObject("ni_vers_b30gg", appo.getBigDecimal("ni_vers_b30gg"));
                    rowBean.setObject("ni_vers_risolti_b30gg", appo.getBigDecimal("ni_vers_risolti_b30gg"));
                    rowBean.setObject("ni_vers_norisolub_b30gg", appo.getBigDecimal("ni_vers_norisolub_b30gg"));
                    rowBean.setObject("ni_vers_verif_b30gg", appo.getBigDecimal("ni_vers_verif_b30gg"));
                    rowBean.setObject("ni_vers_noverif_b30gg", appo.getBigDecimal("ni_vers_noverif_b30gg"));

                    break;
                case AGGIUNTE_DOC_FALLITE:
                    azzeraB30ConutersAgg(rowBean);
                    rowBean.setObject("ni_agg_b30gg", appo.getBigDecimal("ni_agg_b30gg"));
                    rowBean.setObject("ni_agg_risolti_b30gg", appo.getBigDecimal("ni_agg_risolti_b30gg"));
                    rowBean.setObject("ni_agg_norisolub_b30gg", appo.getBigDecimal("ni_agg_norisolub_b30gg"));
                    rowBean.setObject("ni_agg_verif_b30gg", appo.getBigDecimal("ni_agg_verif_b30gg"));
                    rowBean.setObject("ni_agg_noverif_b30gg", appo.getBigDecimal("ni_agg_noverif_b30gg"));
                    break;
                }

            }
        }

        return rowBean == null ? appo : rowBean;
    }

    public BaseRowInterface calcolaTot(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, fieldSetToPopulate type, boolean isBefore30) {
        Object obj = buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, idTipoUnitaDoc, type);
        BaseRowInterface rowBean = null;
        String view = null;
        String paramString = null;
        BigDecimal param1 = null;
        Long param2 = null;
        switch (type) {
        case UNITA_DOC_VERSATE:
            rowBean = new MonVChkCntUdRowBean();
            ((MonVChkCntUdRowBean) rowBean).entityToRowBean(obj);
            if (isBefore30) {
                azzeraB30ConutersUd(rowBean);
            } else {
                ((MonVChkCntUdRowBean) rowBean).resetCounters();
            }
            if (idTipoUnitaDoc != null) {
                view = isBefore30 ? MonVCntUdTipoUdB30.class.getSimpleName() : MonVCntUdTipoUd.class.getSimpleName();
            } else if (idStrut != null) {
                view = isBefore30 ? MonVCntUdStrutB30.class.getSimpleName() : MonVCntUdStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = isBefore30 ? MonVCntUdEnteB30.class.getSimpleName() : MonVCntUdEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = isBefore30 ? MonVCntUdAmbB30.class.getSimpleName() : MonVCntUdAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali unità documentarie versate");
            }
            break;
        case DOC_AGGIUNTI:
            rowBean = new MonVChkCntDocRowBean();
            ((MonVChkCntDocRowBean) rowBean).entityToRowBean(obj);
            if (isBefore30) {
                azzeraB30ConutersDoc(rowBean);
            } else {
                ((MonVChkCntDocRowBean) rowBean).resetCounters();

            }
            if (idTipoUnitaDoc != null) {
                view = isBefore30 ? MonVCntDocTipoUdB30.class.getSimpleName() : MonVCntDocTipoUd.class.getSimpleName();
            } else if (idStrut != null) {
                view = isBefore30 ? MonVCntDocStrutB30.class.getSimpleName() : MonVCntDocStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = isBefore30 ? MonVCntDocEnteB30.class.getSimpleName() : MonVCntDocEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = isBefore30 ? MonVCntDocAmbB30.class.getSimpleName() : MonVCntDocAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali documenti aggiunti");
            }
            break;
        case VERS_FALLITI:
            rowBean = new MonVChkCntVersRowBean();
            ((MonVChkCntVersRowBean) rowBean).entityToRowBean(obj);
            if (isBefore30) {
                azzeraB30ConutersVers(rowBean);
            } else {
                ((MonVChkCntVersRowBean) rowBean).resetCounters();

            }
            if (idTipoUnitaDoc != null) {
                // Errore, non può essere valorizzato
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali versamenti falliti");
            } else if (idStrut != null) {
                view = isBefore30 ? MonVCntVersStrutB30.class.getSimpleName() : MonVCntVersStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = isBefore30 ? MonVCntVersEnteB30.class.getSimpleName() : MonVCntVersEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = isBefore30 ? MonVCntVersAmbB30.class.getSimpleName() : MonVCntVersAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali versamenti falliti");
            }
            break;
        case AGGIUNTE_DOC_FALLITE:
            rowBean = new MonVChkCntAggRowBean();
            ((MonVChkCntAggRowBean) rowBean).entityToRowBean(obj);
            if (isBefore30) {
                azzeraB30ConutersAgg(rowBean);
            } else {
                ((MonVChkCntAggRowBean) rowBean).resetCounters();

            }
            if (idTipoUnitaDoc != null) {
                // Errore, non può essere valorizzato
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali aggiunte fallite");
            } else if (idStrut != null) {
                view = isBefore30 ? MonVCntAggStrutB30.class.getSimpleName() : MonVCntAggStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = isBefore30 ? MonVCntAggEnteB30.class.getSimpleName() : MonVCntAggEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = isBefore30 ? MonVCntAggAmbB30.class.getSimpleName() : MonVCntAggAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali aggiunte fallite");
            }
            break;
        case UNITA_DOC_VERS_FALLITI:
            rowBean = new MonVChkCntUdNonversRowBean();
            ((MonVChkCntUdNonversRowBean) rowBean).entityToRowBean(obj);
            ((MonVChkCntUdNonversRowBean) rowBean).resetCounters();

            if (idTipoUnitaDoc != null) {
                // Errore, non può essere valorizzato
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali unità documentarie derivanti da versamenti falliti");
            } else if (idStrut != null) {
                view = MonVCntUdNonversStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = MonVCntUdNonversEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = MonVCntUdNonversAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali unità documentarie derivanti da versamenti falliti");
            }
            break;
        case DOC_AGGIUNTI_VERS_FALLITI:
            rowBean = new MonVChkCntDocNonversRowBean();
            ((MonVChkCntDocNonversRowBean) rowBean).entityToRowBean(obj);
            ((MonVChkCntDocNonversRowBean) rowBean).resetCounters();

            if (idTipoUnitaDoc != null) {
                // Errore, non può essere valorizzato
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali documenti derivanti da aggiunte fallite");
            } else if (idStrut != null) {
                view = MonVCntDocNonversStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = MonVCntDocNonversEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = MonVCntDocNonversAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali documenti derivanti da aggiunte fallite");
            }
            break;
        case UNITA_DOC_ANNUL:
            rowBean = new MonVChkCntUdAnnulRowBean();
            ((MonVChkCntUdAnnulRowBean) rowBean).entityToRowBean(obj);
            ((MonVChkCntUdAnnulRowBean) rowBean).resetCounters();

            if (idTipoUnitaDoc != null) {
                view = MonVCntUdAnnulTipoUd.class.getSimpleName();
            } else if (idStrut != null) {
                view = MonVCntUdAnnulStrut.class.getSimpleName();
            } else if (idEnte != null) {
                view = MonVCntUdAnnulEnte.class.getSimpleName();
            } else if (idAmbiente != null) {
                view = MonVCntUdAnnulAmb.class.getSimpleName();
            } else {
                throw new IllegalArgumentException(
                        "Errore inaspettato nei parametri di calcolo totali unità documentarie annullate");
            }
            break;
        default:
            throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo totali");
        }
        if (idTipoUnitaDoc != null) {
            switch (type) {
            case UNITA_DOC_ANNUL:
                paramString = "view.idTipoUnitaDoc = :param1";
                param1 = idTipoUnitaDoc;
                break;
            default:
                paramString = "view.idTipoUnitaDoc = :param1 AND view.idStrut = :param2";
                param1 = idTipoUnitaDoc;
                param2 = idStrut.longValue();
                break;
            }
        } else if (idStrut != null) {
            paramString = "view.idStrut = :param1";
            param1 = idStrut;
        } else if (idEnte != null) {
            paramString = "view.idEnte = :param1 AND view.idUserIam = :param2";
            param1 = idEnte;
            param2 = idUtente;
        } else if (idAmbiente != null) {
            paramString = "view.idAmbiente = :param1 AND view.idUserIam = :param2";
            param1 = idAmbiente;
            param2 = idUtente;
        }

        List resultList = monitSintHelper.getMonVCnt(view, paramString, param1, param2);
        /*
         * Per ogni record della lista calcolo i totali necessari a completare il rowBean
         */
        BigDecimal totOggi = BigDecimal.ZERO;
        BigDecimal totTrentaGg = BigDecimal.ZERO;
        BigDecimal totB30Gg = BigDecimal.ZERO;
        for (Object row : resultList) {
            String tiDtCreazione = null;
            String tiStato = null;
            BigDecimal ni = null;
            switch (type) {
            case UNITA_DOC_VERSATE:
                rowBean = handleUdVersateRowBean(row, tiDtCreazione, tiStato, ni, rowBean);
                break;
            case DOC_AGGIUNTI:
                rowBean = handleDocAggiuntiRowBean(row, tiDtCreazione, tiStato, ni, rowBean);
                break;
            case VERS_FALLITI:
                rowBean = handleVersFallitiRowBean(row, tiDtCreazione, tiStato, ni, totOggi, rowBean, totTrentaGg,
                        totB30Gg);
                break;
            case AGGIUNTE_DOC_FALLITE:
                rowBean = handleAggFalliteRowBean(row, tiDtCreazione, tiStato, ni, totOggi, rowBean, totTrentaGg,
                        totB30Gg);
                break;
            case UNITA_DOC_VERS_FALLITI:
                rowBean = handleUdNonVersRowBean(row, tiStato, ni, totOggi, rowBean);
                break;
            case DOC_AGGIUNTI_VERS_FALLITI:
                rowBean = handleDocNonVersRowBean(row, tiStato, ni, totOggi, rowBean);
                break;
            case UNITA_DOC_ANNUL:
                rowBean = handleUdAnnulRowBean(row, tiStato, ni, totOggi, rowBean);
                break;
            default:
                throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo totali");
            }
        }

        return rowBean;
    }

    private BaseRowInterface handleUdVersateRowBean(Object row, String tiDtCreazione, String tiStato, BigDecimal ni,
            BaseRowInterface rowBean) throws IllegalArgumentException {
        if (row instanceof MonVCntUdTipoUd) {
            MonVCntUdTipoUd entity = (MonVCntUdTipoUd) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdStrut) {
            MonVCntUdStrut entity = (MonVCntUdStrut) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdEnte) {
            MonVCntUdEnte entity = (MonVCntUdEnte) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdAmb) {
            MonVCntUdAmb entity = (MonVCntUdAmb) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdTipoUdB30) {
            MonVCntUdTipoUdB30 entity = (MonVCntUdTipoUdB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdStrutB30) {
            MonVCntUdStrutB30 entity = (MonVCntUdStrutB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdEnteB30) {
            MonVCntUdEnteB30 entity = (MonVCntUdEnteB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else if (row instanceof MonVCntUdAmbB30) {
            MonVCntUdAmbB30 entity = (MonVCntUdAmbB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoUd();
            ni = entity.getNiUd();
        } else {
            throw new IllegalArgumentException(
                    "Errore inaspettato nei parametri di calcolo totali unità documentarie versate");
        }

        switch (tiDtCreazione) {
        case "OGGI":
            // totOggi = totOggi.add(ni);
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdAttesaMemCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdAttesaSchedCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdNoselSchedCorr(ni);
            }
            break;
        case "30gg":
            // totTrentaGg = totTrentaGg.add(ni);
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntUdRowBean) rowBean).setNiUd30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdAttesaMem30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdAttesaSched30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setNiUdNoselSched30gg(ni);
            }
            break;
        case "B30":
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntUdRowBean) rowBean).setObject("ni_ud_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntUdRowBean) rowBean).setObject("ni_ud_attesa_mem_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setObject("ni_ud_attesa_sched_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntUdRowBean) rowBean).setObject("ni_ud_nosel_sched_b30gg", ni);
            }
            break;
        }
        // ((MonVChkCntUdRowBean) rowBean).setNiUdCorr(((MonVChkCntUdRowBean) rowBean).getNiUdCorr().add(totOggi));
        // ((MonVChkCntUdRowBean) rowBean).setNiUd30gg(((MonVChkCntUdRowBean) rowBean).getNiUd30gg().add(totTrentaGg));
        return rowBean;
    }

    private BaseRowInterface handleDocAggiuntiRowBean(Object row, String tiDtCreazione, String tiStato, BigDecimal ni,
            BaseRowInterface rowBean) throws IllegalArgumentException {
        if (row instanceof MonVCntDocTipoUd) {
            MonVCntDocTipoUd entity = (MonVCntDocTipoUd) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocStrut) {
            MonVCntDocStrut entity = (MonVCntDocStrut) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocEnte) {
            MonVCntDocEnte entity = (MonVCntDocEnte) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocAmb) {
            MonVCntDocAmb entity = (MonVCntDocAmb) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocTipoUdB30) {
            MonVCntDocTipoUdB30 entity = (MonVCntDocTipoUdB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocStrutB30) {
            MonVCntDocStrutB30 entity = (MonVCntDocStrutB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocEnteB30) {
            MonVCntDocEnteB30 entity = (MonVCntDocEnteB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else if (row instanceof MonVCntDocAmbB30) {
            MonVCntDocAmbB30 entity = (MonVCntDocAmbB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoDoc();
            ni = entity.getNiDoc();
        } else {
            throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo totali documenti aggiunti");
        }

        switch (tiDtCreazione) {
        case "OGGI":
            // totOggi = totOggi.add(ni);
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocAttesaMemCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocAttesaSchedCorr(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocNoselSchedCorr(ni);
            }
            break;
        case "30gg":
            // totTrentaGg = totTrentaGg.add(ni);
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntDocRowBean) rowBean).setNiDoc30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocAttesaMem30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocAttesaSched30gg(ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setNiDocNoselSched30gg(ni);
            }
            break;
        case "B30":
            if (tiStato.equals("TOTALE")) {
                ((MonVChkCntDocRowBean) rowBean).setObject("ni_doc_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                ((MonVChkCntDocRowBean) rowBean).setObject("ni_doc_attesa_mem_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.IN_ATTESA_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setObject("ni_doc_attesa_sched_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.DocStatusEnum.NON_SELEZ_SCHED.name())) {
                ((MonVChkCntDocRowBean) rowBean).setObject("ni_doc_nosel_sched_b30gg", ni);
            }
            break;
        }
        // ((MonVChkCntDocRowBean) rowBean).setNiDocCorr(((MonVChkCntDocRowBean) rowBean).getNiDocCorr().add(totOggi));
        // ((MonVChkCntDocRowBean) rowBean).setNiDoc30gg(((MonVChkCntDocRowBean)
        // rowBean).getNiDoc30gg().add(totTrentaGg));
        return rowBean;
    }

    private BaseRowInterface handleVersFallitiRowBean(Object row, String tiDtCreazione, String tiStato, BigDecimal ni,
            BigDecimal totOggi, BaseRowInterface rowBean, BigDecimal totTrentaGg, BigDecimal totB30Gg) {
        if (row instanceof MonVCntVersStrut) {
            MonVCntVersStrut entity = (MonVCntVersStrut) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else if (row instanceof MonVCntVersEnte) {
            MonVCntVersEnte entity = (MonVCntVersEnte) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else if (row instanceof MonVCntVersAmb) {
            MonVCntVersAmb entity = (MonVCntVersAmb) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else if (row instanceof MonVCntVersStrutB30) {
            MonVCntVersStrutB30 entity = (MonVCntVersStrutB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else if (row instanceof MonVCntVersEnteB30) {
            MonVCntVersEnteB30 entity = (MonVCntVersEnteB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else if (row instanceof MonVCntVersAmbB30) {
            MonVCntVersAmbB30 entity = (MonVCntVersAmbB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiVers();
        } else {
            throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo totali versamenti falliti");
        }

        switch (tiDtCreazione) {
        case "OGGI":
            totOggi = totOggi.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersRisoltiCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersNorisolubCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersVerifCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersNoverifCorr(ni);
            }
            break;
        case "30gg":
            totTrentaGg = totTrentaGg.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersRisolti30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersNorisolub30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersVerif30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setNiVersNoverif30gg(ni);
            }
            break;
        case "B30":
            totB30Gg = totB30Gg.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntVersRowBean) rowBean).setObject("ni_vers_risolti_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntVersRowBean) rowBean).setObject("ni_vers_norisolub_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setObject("ni_vers_verif_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntVersRowBean) rowBean).setObject("ni_vers_noverif_b30gg", ni);
            }
            break;
        }
        BigDecimal niVersB30gg = rowBean.getBigDecimal("ni_vers_b30gg");
        if (niVersB30gg != null) {
            niVersB30gg = niVersB30gg.add(totB30Gg);
            rowBean.setObject("ni_vers_b30gg", niVersB30gg);
        } else {
            ((MonVChkCntVersRowBean) rowBean)
                    .setNiVersCorr(((MonVChkCntVersRowBean) rowBean).getNiVersCorr().add(totOggi));
            ((MonVChkCntVersRowBean) rowBean)
                    .setNiVers30gg(((MonVChkCntVersRowBean) rowBean).getNiVers30gg().add(totTrentaGg));
        }
        return rowBean;
    }

    private BaseRowInterface handleAggFalliteRowBean(Object row, String tiDtCreazione, String tiStato, BigDecimal ni,
            BigDecimal totOggi, BaseRowInterface rowBean, BigDecimal totTrentaGg, BigDecimal totB30gg) {
        if (row instanceof MonVCntAggStrut) {
            MonVCntAggStrut entity = (MonVCntAggStrut) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else if (row instanceof MonVCntAggEnte) {
            MonVCntAggEnte entity = (MonVCntAggEnte) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else if (row instanceof MonVCntAggAmb) {
            MonVCntAggAmb entity = (MonVCntAggAmb) row;
            tiDtCreazione = entity.getTiDtCreazione();
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else if (row instanceof MonVCntAggStrutB30) {
            MonVCntAggStrutB30 entity = (MonVCntAggStrutB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else if (row instanceof MonVCntAggEnteB30) {
            MonVCntAggEnteB30 entity = (MonVCntAggEnteB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else if (row instanceof MonVCntAggAmbB30) {
            MonVCntAggAmbB30 entity = (MonVCntAggAmbB30) row;
            tiDtCreazione = "B30";
            tiStato = entity.getTiStatoVers();
            ni = entity.getNiAgg();
        } else {
            throw new IllegalArgumentException("Errore inaspettato nei parametri di calcolo totali aggiunte fallite");
        }

        switch (tiDtCreazione) {
        case "OGGI":
            totOggi = totOggi.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggRisoltiCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggNorisolubCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggVerifCorr(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggNoverifCorr(ni);
            }
            break;
        case "30gg":
            totTrentaGg = totTrentaGg.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggRisolti30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggNorisolub30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggVerif30gg(ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setNiAggNoverif30gg(ni);
            }
            break;
        case "B30":
            totB30gg = totB30gg.add(ni);
            if (tiStato.equals(VolumeEnums.VersStatusEnum.RISOLTO.name())) {
                ((MonVChkCntAggRowBean) rowBean).setObject("ni_agg_risolti_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
                ((MonVChkCntAggRowBean) rowBean).setObject("ni_agg_norisolub_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setObject("ni_agg_verif_b30gg", ni);
            } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
                ((MonVChkCntAggRowBean) rowBean).setObject("ni_agg_noverif_b30gg", ni);
            }
            break;
        }
        BigDecimal niAggB30gg = rowBean.getBigDecimal("ni_agg_b30gg");
        if (niAggB30gg != null) {
            niAggB30gg = niAggB30gg.add(totB30gg);
            rowBean.setObject("ni_agg_b30gg", niAggB30gg);
        } else {
            ((MonVChkCntAggRowBean) rowBean).setNiAggCorr(((MonVChkCntAggRowBean) rowBean).getNiAggCorr().add(totOggi));
            ((MonVChkCntAggRowBean) rowBean)
                    .setNiAgg30gg(((MonVChkCntAggRowBean) rowBean).getNiAgg30gg().add(totTrentaGg));

        }
        return rowBean;
    }

    private BaseRowInterface handleUdNonVersRowBean(Object row, String tiStato, BigDecimal ni, BigDecimal totOggi,
            BaseRowInterface rowBean) {
        if (row instanceof MonVCntUdNonversStrut) {
            MonVCntUdNonversStrut entity = (MonVCntUdNonversStrut) row;
            tiStato = entity.getTiStatoUdNonvers();
            ni = entity.getNiUdNonvers();
        } else if (row instanceof MonVCntUdNonversEnte) {
            MonVCntUdNonversEnte entity = (MonVCntUdNonversEnte) row;
            tiStato = entity.getTiStatoUdNonvers();
            ni = entity.getNiUdNonvers();
        } else if (row instanceof MonVCntUdNonversAmb) {
            MonVCntUdNonversAmb entity = (MonVCntUdNonversAmb) row;
            tiStato = entity.getTiStatoUdNonvers();
            ni = entity.getNiUdNonvers();
        } else {
            throw new IllegalArgumentException(
                    "Errore inaspettato nei parametri di calcolo totali unità documentarie derivanti da versamenti falliti");
        }

        totOggi = totOggi.add(ni);
        if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
            ((MonVChkCntUdNonversRowBean) rowBean).setNiUdNonversNorisolub(ni);
        } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
            ((MonVChkCntUdNonversRowBean) rowBean).setNiUdNonversVerif(ni);
        } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
            ((MonVChkCntUdNonversRowBean) rowBean).setNiUdNonversNoverif(ni);
        }
        ((MonVChkCntUdNonversRowBean) rowBean)
                .setNiUdNonvers(((MonVChkCntUdNonversRowBean) rowBean).getNiUdNonvers().add(totOggi));
        return rowBean;
    }

    private BaseRowInterface handleDocNonVersRowBean(Object row, String tiStato, BigDecimal ni, BigDecimal totOggi,
            BaseRowInterface rowBean) {
        if (row instanceof MonVCntDocNonversStrut) {
            MonVCntDocNonversStrut entity = (MonVCntDocNonversStrut) row;
            tiStato = entity.getTiStatoDocNonvers();
            ni = entity.getNiDocNonvers();
        } else if (row instanceof MonVCntDocNonversEnte) {
            MonVCntDocNonversEnte entity = (MonVCntDocNonversEnte) row;
            tiStato = entity.getTiStatoDocNonvers();
            ni = entity.getNiDocNonvers();
        } else if (row instanceof MonVCntDocNonversAmb) {
            MonVCntDocNonversAmb entity = (MonVCntDocNonversAmb) row;
            tiStato = entity.getTiStatoDocNonvers();
            ni = entity.getNiDocNonvers();
        } else {
            throw new IllegalArgumentException(
                    "Errore inaspettato nei parametri di calcolo totali unità documentarie derivanti da versamenti falliti");
        }

        totOggi = totOggi.add(ni);
        if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_RISOLUB.name())) {
            ((MonVChkCntDocNonversRowBean) rowBean).setNiDocNonversNorisolub(ni);
        } else if (tiStato.equals(VolumeEnums.VersStatusEnum.VERIF.name())) {
            ((MonVChkCntDocNonversRowBean) rowBean).setNiDocNonversVerif(ni);
        } else if (tiStato.equals(VolumeEnums.VersStatusEnum.NO_VERIF.name())) {
            ((MonVChkCntDocNonversRowBean) rowBean).setNiDocNonversNoverif(ni);
        }
        ((MonVChkCntDocNonversRowBean) rowBean)
                .setNiDocNonvers(((MonVChkCntDocNonversRowBean) rowBean).getNiDocNonvers().add(totOggi));
        return rowBean;
    }

    private BaseRowInterface handleUdAnnulRowBean(Object row, String tiStato, BigDecimal ni, BigDecimal totOggi,
            BaseRowInterface rowBean) {
        if (row instanceof MonVCntUdAnnulTipoUd) {
            MonVCntUdAnnulTipoUd entity = (MonVCntUdAnnulTipoUd) row;
            tiStato = entity.getTiStatoAnnul();
            ni = entity.getNiAnnul();
        } else if (row instanceof MonVCntUdAnnulStrut) {
            MonVCntUdAnnulStrut entity = (MonVCntUdAnnulStrut) row;
            tiStato = entity.getTiStatoAnnul();
            ni = entity.getNiAnnul();
        } else if (row instanceof MonVCntUdAnnulEnte) {
            MonVCntUdAnnulEnte entity = (MonVCntUdAnnulEnte) row;
            tiStato = entity.getTiStatoAnnul();
            ni = entity.getNiAnnul();
        } else if (row instanceof MonVCntUdAnnulAmb) {
            MonVCntUdAnnulAmb entity = (MonVCntUdAnnulAmb) row;
            tiStato = entity.getTiStatoAnnul();
            ni = entity.getNiAnnul();
        } else {
            throw new IllegalArgumentException(
                    "Errore inaspettato nei parametri di calcolo totali unità documentarie derivanti da versamenti falliti");
        }

        totOggi = totOggi.add(ni);
        if (tiStato.equals(VolumeEnums.MonitAnnulStatusEnum.DA_FARE_PING.name())) {
            ((MonVChkCntUdAnnulRowBean) rowBean).setNiUdAnnulDafarePing(ni);
        } else if (tiStato.equals(VolumeEnums.MonitAnnulStatusEnum.DA_FARE_SACER.name())) {
            ((MonVChkCntUdAnnulRowBean) rowBean).setNiUdAnnulDafareSacer(ni);
        } else if (tiStato.equals(VolumeEnums.MonitAnnulStatusEnum.OK.name())) {
            ((MonVChkCntUdAnnulRowBean) rowBean).setNiUdAnnul(ni);
        }
        ((MonVChkCntUdAnnulRowBean) rowBean)
                .setNiUdAnnulTot(((MonVChkCntUdAnnulRowBean) rowBean).getNiUdAnnulTot().add(totOggi));
        return rowBean;
    }

    private void azzeraB30ConutersUd(BaseRowInterface rowBean) {
        rowBean.setObject("ni_ud_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_ud_attesa_mem_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_ud_attesa_sched_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_ud_nosel_sched_b30gg", BigDecimal.ZERO);

    }

    private void azzeraB30ConutersDoc(BaseRowInterface rowBean) {
        rowBean.setObject("ni_doc_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_doc_attesa_mem_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_doc_attesa_sched_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_doc_nosel_sched_b30gg", BigDecimal.ZERO);
    }

    private void azzeraB30ConutersVers(BaseRowInterface rowBean) {
        rowBean.setObject("ni_vers_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_vers_risolti_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_vers_norisolub_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_vers_verif_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_vers_noverif_b30gg", BigDecimal.ZERO);
    }

    private void azzeraB30ConutersAgg(BaseRowInterface rowBean) {
        rowBean.setObject("ni_agg_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_agg_risolti_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_agg_norisolub_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_agg_verif_b30gg", BigDecimal.ZERO);
        rowBean.setObject("ni_agg_noverif_b30gg", BigDecimal.ZERO);
    }

}
