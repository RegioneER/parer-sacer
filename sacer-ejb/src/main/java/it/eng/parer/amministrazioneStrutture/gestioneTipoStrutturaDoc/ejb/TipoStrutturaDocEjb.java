package it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper.TipoStrutturaDocHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecFormatoFileAmmesso;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.entity.DecTipoRapprAmmesso;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocTableBean;
import it.eng.parer.web.util.Transform;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

/**
 * EJB di gestione delle tipologie di struttura documento
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class TipoStrutturaDocEjb {

    private static final Logger logger = LoggerFactory.getLogger(TipoStrutturaDocEjb.class);
    @Resource
    private SessionContext context;
    @EJB
    private TipoStrutturaDocHelper helper;
    @EJB
    private DatiSpecificiHelper datiSpecHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;

    public DecTipoStrutDocRowBean getDecTipoStrutDocRowBean(BigDecimal idTipoStrutDoc, BigDecimal idStrut) {
        DecTipoStrutDocRowBean tipoStrutDocRowBean = getDecTipoStrutDoc(idTipoStrutDoc, null, idStrut);
        return tipoStrutDocRowBean;
    }

    public DecTipoStrutDocRowBean getDecTipoStrutDocRowBean(String nmTipoStrutDoc, BigDecimal idStrut) {
        DecTipoStrutDocRowBean tipoStrutDocRowBean = getDecTipoStrutDoc(BigDecimal.ZERO, nmTipoStrutDoc, idStrut);
        return tipoStrutDocRowBean;
    }

    private DecTipoStrutDocRowBean getDecTipoStrutDoc(BigDecimal idTipoStrutDoc, String nmTipoStrutDoc,
            BigDecimal idStrut) {
        DecTipoStrutDocRowBean tipoStrutDocRowBean = null;
        DecTipoStrutDoc tipoStrutDoc = null;

        if (idTipoStrutDoc == BigDecimal.ZERO && nmTipoStrutDoc != null) {
            tipoStrutDoc = helper.getDecTipoStrutDocByName(nmTipoStrutDoc, idStrut);
        }
        if (nmTipoStrutDoc == null && idTipoStrutDoc != BigDecimal.ZERO) {
            tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, idTipoStrutDoc);
        }

        if (tipoStrutDoc != null) {
            try {
                tipoStrutDocRowBean = (DecTipoStrutDocRowBean) Transform.entity2RowBean(tipoStrutDoc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tipoStrutDocRowBean;
    }

    public DecTipoStrutDocTableBean getDecTipoStrutDocTableBean(BigDecimal idStrut, boolean isFilterValid) {

        DecTipoStrutDocTableBean tipoStrutDocTableBean = new DecTipoStrutDocTableBean();
        List<DecTipoStrutDoc> list = helper.getDecTipoStrutDocList(idStrut, isFilterValid);

        try {
            if (!list.isEmpty()) {
                for (DecTipoStrutDoc tipoStrut : list) {
                    DecTipoStrutDocRowBean tipoStrutDocRow = (DecTipoStrutDocRowBean) Transform
                            .entity2RowBean(tipoStrut);
                    if (tipoStrut.getDtIstituz().before(new Date()) && tipoStrut.getDtSoppres().after(new Date())) {
                        tipoStrutDocRow.setObject("fl_attivo", "1");
                    } else {
                        tipoStrutDocRow.setObject("fl_attivo", "0");
                    }

                    tipoStrutDocTableBean.add(tipoStrutDocRow);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoStrutDocTableBean;
    }

    public DecTipoCompDocRowBean getDecTipoCompDocRowBean(BigDecimal idTipoCompDoc) {

        DecTipoCompDocRowBean tipoCompDocRowBean = new DecTipoCompDocRowBean();
        tipoCompDocRowBean = getDecTipoCompDoc(idTipoCompDoc, null, null);
        return tipoCompDocRowBean;
    }

    public DecTipoCompDocRowBean getDecTipoCompDocRowBean(String nmTipoCompDoc, BigDecimal idTipoStrutDoc) {
        DecTipoCompDocRowBean tipoCompDocRowBean = getDecTipoCompDoc(BigDecimal.ZERO, nmTipoCompDoc, idTipoStrutDoc);
        return tipoCompDocRowBean;
    }

    public DecTipoCompDocRowBean getDecTipoCompDoc(BigDecimal idTipoCompDoc, String nmTipoCompDoc,
            BigDecimal idTipoStrutDoc) {

        DecTipoCompDocRowBean tipoCompDocRowBean = null;
        DecTipoCompDoc tipoCompDoc = null;

        if (idTipoCompDoc == BigDecimal.ZERO && nmTipoCompDoc != null && idTipoStrutDoc != null) {
            tipoCompDoc = helper.getDecTipoCompDocByName(nmTipoCompDoc, idTipoStrutDoc);
        }
        if (nmTipoCompDoc == null && idTipoCompDoc != BigDecimal.ZERO) {
            tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        }
        if (tipoCompDoc != null) {
            try {
                tipoCompDocRowBean = (DecTipoCompDocRowBean) Transform.entity2RowBean(tipoCompDoc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return tipoCompDocRowBean;
    }

    public DecTipoCompDocTableBean getDecTipoCompDocTableBean(BigDecimal idStrut, Date data,
            BigDecimal idTipoStrutDoc) {
        DecTipoCompDocTableBean tipoCompDocTableBean = new DecTipoCompDocTableBean();
        List<DecTipoCompDoc> tipoCompDocList = helper.getDecTipoCompDocList(idStrut.longValue(), data,
                idTipoStrutDoc.longValue());
        if (tipoCompDocList != null && !tipoCompDocList.isEmpty()) {
            try {
                tipoCompDocTableBean = (DecTipoCompDocTableBean) Transform.entities2TableBean(tipoCompDocList);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei tipi componente" + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
            }
        }
        return tipoCompDocTableBean;
    }

    public DecTipoCompDocTableBean getDecTipoCompDocTableBean(BigDecimal idTipoStrutDoc, boolean isFilterValid) {

        DecTipoCompDocTableBean tipoCompDocTableBean = new DecTipoCompDocTableBean();
        List<DecTipoCompDoc> list = helper.getDecTipoCompDocList(idTipoStrutDoc, isFilterValid);
        Long idStrut = null;
        try {
            if (!list.isEmpty()) {
                for (DecTipoCompDoc tipoComp : list) {
                    if (idStrut == null) {
                        idStrut = tipoComp.getDecTipoStrutDoc().getOrgStrut().getIdStrut();
                    }
                    DecTipoCompDocRowBean tipoCompDocRow = (DecTipoCompDocRowBean) Transform.entity2RowBean(tipoComp);
                    if (tipoComp.getDtIstituz().before(new Date()) && tipoComp.getDtSoppres().after(new Date())) {
                        tipoCompDocRow.setObject("fl_attivo", "1");
                    } else {
                        tipoCompDocRow.setObject("fl_attivo", "0");
                    }
                    DecXsdDatiSpec lastXsd = datiSpecHelper.getLastDecXsdDatiSpecForTipoCompDoc(idStrut,
                            tipoComp.getIdTipoCompDoc());
                    if (lastXsd != null) {
                        tipoCompDocRow.setObject("cd_versione_xsd", lastXsd.getCdVersioneXsd());
                    }
                    tipoCompDocTableBean.add(tipoCompDocRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoCompDocTableBean;
    }

    public DecTipoStrutUnitaDocTableBean getDecTipoStrutUnitaDocTableBean(BigDecimal idTipoUnitaDoc,
            boolean isFilterValid) {
        DecTipoStrutUnitaDocTableBean tipoStrutUnitaDocTableBean = new DecTipoStrutUnitaDocTableBean();
        List<DecTipoStrutUnitaDoc> list = helper.getDecTipoStrutUnitaDocList(idTipoUnitaDoc, isFilterValid);

        try {
            if (!list.isEmpty()) {
                for (DecTipoStrutUnitaDoc tipoStrutUnita : list) {
                    DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRow = (DecTipoStrutUnitaDocRowBean) Transform
                            .entity2RowBean(tipoStrutUnita);
                    if (tipoStrutUnita.getDtIstituz().before(new Date())
                            && tipoStrutUnita.getDtSoppres().after(new Date())) {
                        tipoStrutUnitaDocRow.setObject("fl_attivo", "1");
                    } else {
                        tipoStrutUnitaDocRow.setObject("fl_attivo", "0");
                    }

                    // // Creo il campo relativo ai periodi di validit\u00E0
                    // StringBuilder periodi = new StringBuilder();
                    // int numeroPeriodi = registro.getDecAaRegistroUnitaDocs().size();
                    // for (int index = 0; index < numeroPeriodi; index++) {
                    // DecAaRegistroUnitaDoc anno = registro.getDecAaRegistroUnitaDocs().get(index);
                    // periodi.append(anno.getAaMinRegistroUnitaDoc());
                    // if (anno.getAaMaxRegistroUnitaDoc() != null) {
                    // periodi.append(" - ").append(anno.getAaMaxRegistroUnitaDoc());
                    // }
                    // if (index < (numeroPeriodi - 1)) {
                    // periodi.append("; ");
                    // }
                    //
                    // }
                    String aaMin = tipoStrutUnitaDocRow.getAaMinTipoStrutUnitaDoc() != null
                            ? tipoStrutUnitaDocRow.getAaMinTipoStrutUnitaDoc().toString() : "";
                    String aaMax = tipoStrutUnitaDocRow.getAaMaxTipoStrutUnitaDoc() != null
                            ? " - " + tipoStrutUnitaDocRow.getAaMaxTipoStrutUnitaDoc().toString() : "";

                    tipoStrutUnitaDocRow.setString("periodo_validita", aaMin + aaMax);

                    tipoStrutUnitaDocTableBean.add(tipoStrutUnitaDocRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoStrutUnitaDocTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoStrutDoc(LogParam param, DecTipoStrutDocRowBean tipoStrutDocRowBean)
            throws ParerUserError {
        OrgStrut struttura = helper.findById(OrgStrut.class, tipoStrutDocRowBean.getIdStrut());
        if (helper.getDecTipoStrutDocByName(tipoStrutDocRowBean.getNmTipoStrutDoc(),
                tipoStrutDocRowBean.getIdStrut()) != null) {
            throw new ParerUserError("Tipo gi\u00E0 esistente all'interno della struttura</br>");
        }

        // FIXME: Eliminare rowBean2Entity
        DecTipoStrutDoc tipoStrutDoc = (DecTipoStrutDoc) Transform.rowBean2Entity(tipoStrutDocRowBean);
        tipoStrutDoc.setOrgStrut(struttura);

        helper.insertEntity(tipoStrutDoc, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                new BigDecimal(tipoStrutDoc.getIdTipoStrutDoc()), param.getNomePagina());

        tipoStrutDocRowBean.setIdTipoStrutDoc(new BigDecimal(tipoStrutDoc.getIdTipoStrutDoc()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoStrutDoc(LogParam param, BigDecimal idTipoStrutDoc,
            DecTipoStrutDocRowBean tipoStrutDocRowBean) throws ParerUserError {
        DecTipoStrutDoc dbTipoStrutDoc = helper.getDecTipoStrutDocByName(tipoStrutDocRowBean.getNmTipoStrutDoc(),
                tipoStrutDocRowBean.getIdStrut());

        if (dbTipoStrutDoc != null && dbTipoStrutDoc.getIdTipoStrutDoc() != idTipoStrutDoc.longValue()) {
            throw new ParerUserError(
                    "Nome Tipo Struttura gi\u00E0 associato a questa struttura all'interno del database</br>");
        }

        DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, idTipoStrutDoc);
        tipoStrutDoc.setDsTipoStrutDoc(tipoStrutDocRowBean.getDsTipoStrutDoc());
        tipoStrutDoc.setNmTipoStrutDoc(tipoStrutDocRowBean.getNmTipoStrutDoc());
        tipoStrutDoc.setDtIstituz(tipoStrutDocRowBean.getDtIstituz());
        tipoStrutDoc.setDtSoppres(tipoStrutDocRowBean.getDtSoppres());
        helper.getEntityManager().flush();

        // Potrebbe essere necessario modificarlo questo punto perché chiamato da qualche altra parte che potrebbe
        // incartarsi!!
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                param.getNomePagina());

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteAndInsertDecFormatoFileAmmesso(LogParam param, List<BigDecimal> idFormatoFileAmmessoToDeleteList,
            BigDecimal idTipoStrutDoc, BigDecimal idTipoCompDoc, List<BigDecimal> listaFormatiDaInserire) {
        if (!idFormatoFileAmmessoToDeleteList.isEmpty()) {
            helper.deleteDecFormatoFileAmmessoList(idFormatoFileAmmessoToDeleteList);
        }
        for (BigDecimal id : listaFormatiDaInserire) {
            insertDecFormatoFileAmmesso(idTipoCompDoc, id);
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                param.getNomePagina());
    }

    private void insertDecFormatoFileAmmesso(BigDecimal idTipoCompDoc, BigDecimal idFormatoFileDoc) {
        DecFormatoFileAmmesso formatoFileAmmesso = new DecFormatoFileAmmesso();

        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        if (tipoCompDoc.getDecFormatoFileAmmessos() == null) {
            tipoCompDoc.setDecFormatoFileAmmessos(new ArrayList<DecFormatoFileAmmesso>());
        }
        DecFormatoFileDoc formatoFileDoc = helper.findById(DecFormatoFileDoc.class, idFormatoFileDoc);
        if (formatoFileDoc.getDecFormatoFileAmmessos() == null) {
            formatoFileDoc.setDecFormatoFileAmmessos(new ArrayList<DecFormatoFileAmmesso>());
        }

        formatoFileAmmesso.setDecTipoCompDoc(tipoCompDoc);
        formatoFileAmmesso.setDecFormatoFileDoc(formatoFileDoc);

        helper.insertEntity(formatoFileAmmesso, true);
        // Aggiungo il formato file ammesso alle liste many-to-one
        tipoCompDoc.getDecFormatoFileAmmessos().add(formatoFileAmmesso);
        formatoFileDoc.getDecFormatoFileAmmessos().add(formatoFileAmmesso);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoCompDoc(LogParam param, DecTipoCompDocRowBean tipoCompDocRowBean) {
        DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, tipoCompDocRowBean.getIdTipoStrutDoc());
        DecTipoCompDoc tipoCompDoc = new DecTipoCompDoc();
        tipoCompDoc.setDecTipoStrutDoc(tipoStrutDoc);
        tipoCompDoc.setDsTipoCompDoc(tipoCompDocRowBean.getDsTipoCompDoc());
        tipoCompDoc.setNmTipoCompDoc(tipoCompDocRowBean.getNmTipoCompDoc());
        tipoCompDoc.setDtIstituz(tipoCompDocRowBean.getDtIstituz());
        tipoCompDoc.setDtSoppres(tipoCompDocRowBean.getDtSoppres());
        tipoCompDoc.setTiUsoCompDoc(tipoCompDocRowBean.getTiUsoCompDoc());

        helper.insertEntity(tipoCompDoc, true);
        // TEST: VERIFICARE LOGGING
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                tipoCompDocRowBean.getIdTipoStrutDoc(), param.getNomePagina());

        tipoCompDocRowBean.setIdTipoCompDoc(new BigDecimal(tipoCompDoc.getIdTipoCompDoc()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoCompDoc(LogParam param, BigDecimal idTipoCompDoc, DecTipoCompDocRowBean tipoCompDocRowBean)
            throws ParerUserError {
        DecTipoCompDoc dbTipoCompDoc = helper.getDecTipoCompDocByName(tipoCompDocRowBean.getNmTipoCompDoc(),
                tipoCompDocRowBean.getIdTipoStrutDoc());
        if (dbTipoCompDoc != null && dbTipoCompDoc.getIdTipoCompDoc() != idTipoCompDoc.longValue()) {
            throw new ParerUserError(
                    "Nome Tipo Componente gi\u00E0 associato a questa struttura all'interno del database</br>");
        }

        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, tipoCompDocRowBean.getIdTipoStrutDoc());

        tipoCompDoc.setDecTipoStrutDoc(tipoStrutDoc);
        tipoCompDoc.setNmTipoCompDoc(tipoCompDocRowBean.getNmTipoCompDoc());
        tipoCompDoc.setDsTipoCompDoc(tipoCompDocRowBean.getDsTipoCompDoc());
        tipoCompDoc.setTiUsoCompDoc(tipoCompDocRowBean.getTiUsoCompDoc());
        tipoCompDoc.setDtIstituz(tipoCompDocRowBean.getDtIstituz());
        tipoCompDoc.setDtSoppres(tipoCompDocRowBean.getDtSoppres());

        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                tipoCompDocRowBean.getIdTipoStrutDoc(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecTipoStrutDoc(LogParam param, long idTipoStrutDoc) throws ParerUserError {
        DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, idTipoStrutDoc);
        String nmTipoStrutDoc = tipoStrutDoc.getNmTipoStrutDoc();
        long idStrut = tipoStrutDoc.getOrgStrut().getIdStrut();
        boolean existsRelationsWithStrutDoc = helper.existsRelationsWithStrutDoc(tipoStrutDoc.getIdTipoStrutDoc());
        if (existsRelationsWithStrutDoc) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo struttura documento: esiste almeno un'unità documentaria versata con tale tipologia</br>");
        }
        // LOG BEFORE PER IL TIPO DOCUMENTO
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
                SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, new BigDecimal(idTipoStrutDoc),
                param.getNomePagina());
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                new BigDecimal(idTipoStrutDoc), param.getNomePagina());
        helper.removeEntity(tipoStrutDoc, true);
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaOggettiDaLoggare, param.getNomePagina());

        logger.info("Cancellazione tipo struttura documento " + nmTipoStrutDoc + " della struttura " + idStrut
                + " avvenuta con successo!");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoCompDoc(LogParam param, DecTipoCompDocRowBean tipoCompDocRowBean) throws ParerUserError {
        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, tipoCompDocRowBean.getIdTipoCompDoc());
        boolean hasManyRelationsEmpty = helper
                .checkManyRelationsAreEmptyForDecTipoCompDoc(tipoCompDoc.getIdTipoCompDoc());
        if (hasManyRelationsEmpty) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo componente documento: esiste almeno un elemento associato ad esso</br>");
        }
        helper.removeEntity(tipoCompDoc, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                tipoCompDocRowBean.getIdTipoStrutDoc(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileAmmesso(LogParam param, BigDecimal idFormatoFileAmmesso,
            BigDecimal idTipoStrutDoc) {
        DecFormatoFileAmmesso formatoFileAmmesso = helper.findById(DecFormatoFileAmmesso.class, idFormatoFileAmmesso);
        helper.removeEntity(formatoFileAmmesso, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                param.getNomePagina());
    }

    public DecTipoRapprAmmessoTableBean getDecTipoRapprAmmessoTableBeanByIdTipoCompDoc(BigDecimal idTipoCompDoc) {
        DecTipoRapprAmmessoTableBean tipoRapprCompTableBean = new DecTipoRapprAmmessoTableBean();
        DecTipoRapprAmmessoRowBean tipoRapprCompRowBean = new DecTipoRapprAmmessoRowBean();
        List<DecTipoRapprAmmesso> tipoRapprAmmessoTableBeanList = helper
                .retrieveDecTipoRapprAmmessoByIdTipoCompDoc(idTipoCompDoc.longValue());
        if (tipoRapprAmmessoTableBeanList != null && !tipoRapprAmmessoTableBeanList.isEmpty()) {
            try {
                for (DecTipoRapprAmmesso tipoRapprAmmesso : tipoRapprAmmessoTableBeanList) {
                    tipoRapprCompRowBean = (DecTipoRapprAmmessoRowBean) Transform.entity2RowBean(tipoRapprAmmesso);
                    tipoRapprCompRowBean.setString("nm_tipo_rappr_comp",
                            tipoRapprAmmesso.getDecTipoRapprComp().getNmTipoRapprComp());
                    tipoRapprCompRowBean.setString("ds_tipo_rappr_comp",
                            tipoRapprAmmesso.getDecTipoRapprComp().getDsTipoRapprComp());
                    // tipoRapprCompRowBean.setString("ds_tipo_doc",
                    // tipoRapprAmmesso.getDecTipoRapprComp().getDsTipoRapprComp());
                    if (tipoRapprAmmesso.getDecTipoRapprComp().getDtSoppres().after(new Date())) {
                        tipoRapprCompRowBean.setString("dt_soppres", "1");
                    } else {
                        tipoRapprCompRowBean.setString("dt_soppres", "0");
                    }
                    tipoRapprCompTableBean.add(tipoRapprCompRowBean);
                }
            } catch (Exception ex) {
                logger.error("Errore durante il recupero dei tipi rappresentazione ammessi "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tipoRapprCompTableBean;
    }

    public DecTipoRapprAmmessoTableBean getDecTipoRapprAmmessoTableBeanByIdTipoRapprComp(BigDecimal idTipoRapprComp) {
        DecTipoRapprAmmessoTableBean tipoRapprCompTableBean = new DecTipoRapprAmmessoTableBean();
        List<DecTipoRapprAmmesso> tipoRapprAmmessoTableBeanList = helper
                .retrieveDecTipoRapprAmmessoByIdTipoRapprComp(idTipoRapprComp.longValue());
        if (tipoRapprAmmessoTableBeanList != null && !tipoRapprAmmessoTableBeanList.isEmpty()) {
            try {
                for (DecTipoRapprAmmesso tipoRapprAmmesso : tipoRapprAmmessoTableBeanList) {
                    DecTipoRapprAmmessoRowBean tipoRapprCompRowBean = (DecTipoRapprAmmessoRowBean) Transform
                            .entity2RowBean(tipoRapprAmmesso);
                    tipoRapprCompRowBean.setBigDecimal("id_tipo_strut_doc", new BigDecimal(
                            tipoRapprAmmesso.getDecTipoCompDoc().getDecTipoStrutDoc().getIdTipoStrutDoc()));
                    tipoRapprCompRowBean.setString("nm_tipo_strut_doc",
                            tipoRapprAmmesso.getDecTipoCompDoc().getDecTipoStrutDoc().getNmTipoStrutDoc());
                    tipoRapprCompRowBean.setString("ds_tipo_strut_doc",
                            tipoRapprAmmesso.getDecTipoCompDoc().getDecTipoStrutDoc().getDsTipoStrutDoc());
                    tipoRapprCompRowBean.setString("nm_tipo_comp_doc",
                            tipoRapprAmmesso.getDecTipoCompDoc().getNmTipoCompDoc());
                    tipoRapprCompRowBean.setString("ds_tipo_comp_doc",
                            tipoRapprAmmesso.getDecTipoCompDoc().getDsTipoCompDoc());
                    if (tipoRapprAmmesso.getDecTipoRapprComp().getDtSoppres().after(new Date())) {
                        tipoRapprCompRowBean.setString("dt_soppres", "1");
                    } else {
                        tipoRapprCompRowBean.setString("dt_soppres", "0");
                    }
                    tipoRapprCompTableBean.add(tipoRapprCompRowBean);
                }
            } catch (Exception ex) {
                logger.error("Errore durante il recupero dei tipi rappresentazione ammessi "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tipoRapprCompTableBean;
    }

    public DecTipoRapprCompTableBean getDecTipoRapprCompTableBeanByIdStrut(BigDecimal idStrut, Date data) {
        DecTipoRapprCompTableBean tipoRapprCompTableBean = new DecTipoRapprCompTableBean();
        List<DecTipoRapprComp> tipoRapprCompList = helper.getDecTipoRapprCompListByIdStrut(idStrut.longValue(), data);
        if (tipoRapprCompList != null && !tipoRapprCompList.isEmpty()) {
            try {
                tipoRapprCompTableBean = (DecTipoRapprCompTableBean) Transform.entities2TableBean(tipoRapprCompList);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei tipi rappresentazione componente"
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tipoRapprCompTableBean;
    }

    public DecTipoRapprAmmessoRowBean getDecTipoRapprAmmessoRowBean(BigDecimal idTipoCompDoc,
            BigDecimal idTipoRapprComp) {
        DecTipoRapprAmmesso tipoRapprAmmesso = helper.getDecTipoRapprAmmessoByParentId(idTipoCompDoc.longValue(),
                idTipoRapprComp.longValue());
        DecTipoRapprAmmessoRowBean row = new DecTipoRapprAmmessoRowBean();
        try {
            row = (DecTipoRapprAmmessoRowBean) Transform.entity2RowBean(tipoRapprAmmesso);
            row.setBigDecimal("id_tipo_strut_doc",
                    new BigDecimal(tipoRapprAmmesso.getDecTipoCompDoc().getDecTipoStrutDoc().getIdTipoStrutDoc()));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero del tipo componente" + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return row;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoRapprAmmesso(LogParam param, BigDecimal idTipoCompDoc, BigDecimal idTipoRapprComp)
            throws ParerUserError {
        if (helper.getDecTipoRapprAmmessoByParentId(idTipoCompDoc.longValue(), idTipoRapprComp.longValue()) != null) {
            throw new ParerUserError("Associazione gi\u00E0 esistente</br>");
        }
        DecTipoRapprAmmesso tipoRapprAmmesso = new DecTipoRapprAmmesso();
        // Recupero il Tipo Comp Doc
        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        // Recupero il Tipo Rappresentazione Componente
        DecTipoRapprComp tipoRapprComp = helper.findById(DecTipoRapprComp.class, idTipoRapprComp);
        tipoRapprAmmesso.setDecTipoCompDoc(tipoCompDoc);
        tipoRapprAmmesso.setDecTipoRapprComp(tipoRapprComp);

        helper.insertEntity(tipoRapprAmmesso, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoRapprAmmesso(LogParam param, BigDecimal idTipoRapprAmmesso, BigDecimal idTipoCompDoc,
            BigDecimal idTipoRapprComp) throws ParerUserError {
        DecTipoRapprAmmesso dbTipoRapprAmmesso = helper.getDecTipoRapprAmmessoByParentId(idTipoCompDoc.longValue(),
                idTipoRapprComp.longValue());
        // verifico quale dei due tipi stavo modificando: se l'id resta null vuol dire che non ci sono state modifiche
        if (dbTipoRapprAmmesso != null) {
            /* Verifico se esiste gi\u00E0 la nuova relazione creata */
            if (dbTipoRapprAmmesso.getIdTipoRapprAmmesso() != idTipoRapprAmmesso.longValue()) {
                throw new ParerUserError(
                        "Nome tipo rappresentazione componente gi\u00E0 associato a questo documento all'interno del database</br>");
            }
        } else {
            dbTipoRapprAmmesso = helper.findById(DecTipoRapprAmmesso.class, idTipoRapprAmmesso);
        }
        boolean update = false;
        if (dbTipoRapprAmmesso.getDecTipoRapprComp().getIdTipoRapprComp() != idTipoRapprComp.longValue()
                || dbTipoRapprAmmesso.getDecTipoCompDoc().getIdTipoCompDoc() != idTipoCompDoc.longValue()) {
            update = true;
        }

        if (update) {
            // Recupero il Tipo Comp Doc
            DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
            // Recupero il Tipo rappresentazione
            DecTipoRapprComp tipoRapprComp = helper.findById(DecTipoRapprComp.class, idTipoRapprComp);
            dbTipoRapprAmmesso.setDecTipoCompDoc(tipoCompDoc);
            dbTipoRapprAmmesso.setDecTipoRapprComp(tipoRapprComp);

            helper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
                    new BigDecimal(tipoCompDoc.getDecTipoStrutDoc().getIdTipoStrutDoc()), param.getNomePagina());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoRapprAmmesso(LogParam param, BigDecimal idTipoRapprAmmesso, BigDecimal idTipoStrutDoc) {
        DecTipoRapprAmmesso tipoRapprAmmesso = helper.findById(DecTipoRapprAmmesso.class, idTipoRapprAmmesso);
        idTipoStrutDoc = new BigDecimal(tipoRapprAmmesso.getDecTipoCompDoc().getDecTipoStrutDoc().getIdTipoStrutDoc());
        helper.removeEntity(tipoRapprAmmesso, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                param.getNomePagina());
    }

}
