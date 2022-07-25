package it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.helper.TipoRappresentazioneHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecImageTrasform;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTrasformTipoRappr;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.DecImageTrasformRowBean;
import it.eng.parer.slite.gen.tablebean.DecImageTrasformTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTrasformTipoRapprRowBean;
import it.eng.parer.slite.gen.tablebean.DecTrasformTipoRapprTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoLite.db.base.row.BaseRow;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione delle tipologie di rappresentazione
 *
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class TipoRappresentazioneEjb {

    private static final Logger logger = LoggerFactory.getLogger(TipoRappresentazioneEjb.class);
    @Resource
    private SessionContext context;
    @EJB
    private TipoRappresentazioneHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private ControlliSemantici controlliSemantici;

    public DecTipoRapprCompRowBean getDecTipoRapprCompRowBean(BigDecimal idTipoRapprComp, BigDecimal idStrut) {
        DecTipoRapprCompRowBean tipoRapprCompRowBean = getDecTipoRapprComp(idTipoRapprComp, null, idStrut);
        return tipoRapprCompRowBean;
    }

    public DecTipoRapprCompRowBean getDecTipoRapprCompRowBean(String nmTipoRapprComp, BigDecimal idStrut) {
        DecTipoRapprCompRowBean tipoRapprCompRowBean = getDecTipoRapprComp(BigDecimal.ZERO, nmTipoRapprComp, idStrut);
        return tipoRapprCompRowBean;
    }

    private DecTipoRapprCompRowBean getDecTipoRapprComp(BigDecimal idTipoRapprComp, String nmTipoRapprComp,
            BigDecimal idStrut) {

        DecTipoRapprComp tipoRapprComp = null;
        DecTipoRapprCompRowBean tipoRapprCompRowBean = null;

        if (idTipoRapprComp == BigDecimal.ZERO && nmTipoRapprComp != null) {
            tipoRapprComp = helper.getDecTipoRapprCompByName(nmTipoRapprComp, idStrut);

        }
        if (nmTipoRapprComp == null && idTipoRapprComp != BigDecimal.ZERO) {
            tipoRapprComp = helper.findById(DecTipoRapprComp.class, idTipoRapprComp);
        }

        if (tipoRapprComp != null) {
            try {

                tipoRapprCompRowBean = (DecTipoRapprCompRowBean) Transform.entity2RowBean(tipoRapprComp);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return tipoRapprCompRowBean;
    }

    public DecTipoRapprCompTableBean getDecTipoRapprCompTableBean(BigDecimal idStrut, boolean isFilterValid) {

        DecTipoRapprCompTableBean tipoRapprCompTableBean = new DecTipoRapprCompTableBean();
        List<DecTipoRapprComp> list = helper.retrieveDecRapprCompList(idStrut, isFilterValid);

        try {
            if (!list.isEmpty()) {
                for (DecTipoRapprComp tipoRappr : list) {
                    DecTipoRapprCompRowBean tipoRapprCompRow = (DecTipoRapprCompRowBean) Transform
                            .entity2RowBean(tipoRappr);
                    if (tipoRappr.getDtIstituz().before(new Date()) && tipoRappr.getDtSoppres().after(new Date())) {
                        tipoRapprCompRow.setObject("fl_attivo", "1");
                    } else {
                        tipoRapprCompRow.setObject("fl_attivo", "0");
                    }

                    tipoRapprCompTableBean.add(tipoRapprCompRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoRapprCompTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoRapprComp(LogParam param, DecTipoRapprCompRowBean tipoRapprCompRowBean)
            throws ParerUserError {
        DecTipoRapprComp tipoRapprComp = new DecTipoRapprComp();
        tipoRapprComp.setNmTipoRapprComp(tipoRapprCompRowBean.getNmTipoRapprComp());
        tipoRapprComp.setDsTipoRapprComp(tipoRapprCompRowBean.getDsTipoRapprComp());
        tipoRapprComp.setDtIstituz(tipoRapprCompRowBean.getDtIstituz());
        tipoRapprComp.setDtSoppres(tipoRapprCompRowBean.getDtSoppres());
        tipoRapprComp.setTiAlgoRappr(tipoRapprCompRowBean.getTiAlgoRappr());
        tipoRapprComp.setTiOutputRappr(tipoRapprCompRowBean.getTiOutputRappr());
        OrgStrut struttura = helper.findById(OrgStrut.class, tipoRapprCompRowBean.getIdStrut());
        tipoRapprComp.setOrgStrut(struttura);
        DecFormatoFileDoc formatoCont = helper.findById(DecFormatoFileDoc.class,
                tipoRapprCompRowBean.getIdFormatoContenuto());
        tipoRapprComp.setDecFormatoFileDocCont(formatoCont);
        DecFormatoFileDoc formatoConv = null;
        if (tipoRapprCompRowBean.getIdFormatoConvertit() != null) {
            formatoConv = helper.findById(DecFormatoFileDoc.class, tipoRapprCompRowBean.getIdFormatoConvertit());
        }
        tipoRapprComp.setDecFormatoFileDocConv(formatoConv);
        DecFormatoFileStandard formatoStandard = helper.findById(DecFormatoFileStandard.class,
                tipoRapprCompRowBean.getIdFormatoOutputRappr());
        tipoRapprComp.setDecFormatoFileStandard(formatoStandard);
        helper.insertEntity(tipoRapprComp, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                new BigDecimal(tipoRapprComp.getIdTipoRapprComp()), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoRapprComp(LogParam param, BigDecimal idTipoRapprComp,
            DecTipoRapprCompRowBean tipoRapprCompRowBean) throws ParerUserError {

        DecTipoRapprComp dbTipoRapprComp = helper.getDecTipoRapprCompByName(tipoRapprCompRowBean.getNmTipoRapprComp(),
                tipoRapprCompRowBean.getIdStrut());
        if (dbTipoRapprComp != null && dbTipoRapprComp.getIdTipoRapprComp() != idTipoRapprComp.longValue()) {
            throw new ParerUserError("Nome Tipo gi\u00E0 associato a questa struttura all'interno del database</br>");
        }

        DecTipoRapprComp tipoRapprComp = helper.findById(DecTipoRapprComp.class, idTipoRapprComp);
        tipoRapprComp.setNmTipoRapprComp(tipoRapprCompRowBean.getNmTipoRapprComp());
        tipoRapprComp.setDsTipoRapprComp(tipoRapprCompRowBean.getDsTipoRapprComp());
        tipoRapprComp.setDtIstituz(tipoRapprCompRowBean.getDtIstituz());
        tipoRapprComp.setDtSoppres(tipoRapprCompRowBean.getDtSoppres());
        tipoRapprComp.setTiAlgoRappr(tipoRapprCompRowBean.getTiAlgoRappr());
        tipoRapprComp.setTiOutputRappr(tipoRapprCompRowBean.getTiOutputRappr());
        DecFormatoFileDoc formatoCont = helper.findById(DecFormatoFileDoc.class,
                tipoRapprCompRowBean.getIdFormatoContenuto());
        tipoRapprComp.setDecFormatoFileDocCont(formatoCont);
        DecFormatoFileDoc formatoConv = null;
        if (tipoRapprCompRowBean.getIdFormatoConvertit() != null) {
            formatoConv = helper.findById(DecFormatoFileDoc.class, tipoRapprCompRowBean.getIdFormatoConvertit());
        }
        tipoRapprComp.setDecFormatoFileDocConv(formatoConv);
        DecFormatoFileStandard formatoStandard = helper.findById(DecFormatoFileStandard.class,
                tipoRapprCompRowBean.getIdFormatoOutputRappr());
        tipoRapprComp.setDecFormatoFileStandard(formatoStandard);

        helper.getEntityManager().flush();

        // Per il momento disattivato
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                new BigDecimal(tipoRapprComp.getIdTipoRapprComp()), param.getNomePagina());
    }

    public boolean checkRelation(BaseRow rowBean) {
        boolean isRelated = false;
        if (rowBean instanceof DecTipoRapprCompRowBean) {
            DecTipoRapprCompRowBean tipoRapprCompRowBean = (DecTipoRapprCompRowBean) rowBean;
            DecTipoRapprComp tipoRapprComp = helper.findById(DecTipoRapprComp.class,
                    tipoRapprCompRowBean.getIdTipoRapprComp());
            isRelated = helper.checkRelationsAreEmptyForDecTipoRapprComp(tipoRapprComp.getIdTipoRapprComp());

        }
        return isRelated;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecTipoRapprComp(LogParam param, long idTipoRapprComp) throws ParerUserError {
        DecTipoRapprComp tipoRapprComp = helper.findById(DecTipoRapprComp.class, idTipoRapprComp);
        String nmTipoRapprComp = tipoRapprComp.getNmTipoRapprComp();
        long idStrut = tipoRapprComp.getOrgStrut().getIdStrut();
        boolean existsRelations = helper.checkRelationsAreEmptyForDecTipoRapprComp(tipoRapprComp.getIdTipoRapprComp());
        if (existsRelations) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo rappresentazione: esiste almeno un componente associato</br>");
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
                SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE, new BigDecimal(idTipoRapprComp),
                param.getNomePagina());
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                new BigDecimal(idTipoRapprComp), param.getNomePagina());
        helper.removeEntity(tipoRapprComp, true);
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaOggettiDaLoggare, param.getNomePagina());
        logger.info("Cancellazione tipo unità documentaria " + nmTipoRapprComp + " della struttura " + idStrut
                + " avvenuta con successo!");
    }

    public DecTrasformTipoRapprTableBean getDecTrasformTipoRapprTableBean(
            DecTipoRapprCompRowBean decTipoRapprCompRowBean) {
        DecTrasformTipoRapprTableBean result = null;

        List<DecTrasformTipoRappr> lista = helper
                .retrieveDecTrasformTipoRapprList(decTipoRapprCompRowBean.getIdTipoRapprComp().longValue());
        if (lista != null && !lista.isEmpty()) {
            try {
                result = new DecTrasformTipoRapprTableBean();
                DecTrasformTipoRapprRowBean rowBean = null;
                for (DecTrasformTipoRappr row : lista) {
                    rowBean = (DecTrasformTipoRapprRowBean) Transform.entity2RowBean(row);
                    result.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        } else {
            result = new DecTrasformTipoRapprTableBean();
        }
        return result;
    }

    public DecTrasformTipoRapprRowBean getDecTrasformTipoRapprRowBean(BigDecimal idTrasformTipoRappr) {
        DecTrasformTipoRapprRowBean decTrasformTipoRapprRowBean = null;
        DecTrasformTipoRappr decTrasformTipoRappr = helper.findById(DecTrasformTipoRappr.class, idTrasformTipoRappr);
        if (decTrasformTipoRappr != null) {
            try {
                decTrasformTipoRapprRowBean = (DecTrasformTipoRapprRowBean) Transform
                        .entity2RowBean(decTrasformTipoRappr);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }

        }
        return decTrasformTipoRapprRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // FIXME: TESTARE
    public void updateTrasformTipoRappr(LogParam param, DecTrasformTipoRapprRowBean row) {
        BigDecimal idTrasformTipoRappr = row.getIdTrasformTipoRappr();
        DecTrasformTipoRappr decTrasformTipoRappr = helper.findById(DecTrasformTipoRappr.class, idTrasformTipoRappr);
        if (row.getBlFileTrasform() == null) {
            decTrasformTipoRappr.setNmTrasform(row.getNmTrasform());
            decTrasformTipoRappr.setCdVersioneTrasform(row.getCdVersioneTrasform());
        } else {
            decTrasformTipoRappr.setBlFileTrasform(row.getBlFileTrasform());
            decTrasformTipoRappr.setTiStatoFileTrasform(CostantiDB.StatoFileTrasform.MODIFICATO.name());
        }
        decTrasformTipoRappr.setDtLastModTrasform(new Date(System.currentTimeMillis()));
        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE,
                row.getIdTipoRapprComp(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // FIXME: TESTARE
    public void setDecTrasformTipoRapprStatusAtError(LogParam param, DecTrasformTipoRapprRowBean row,
            BigDecimal idTipoRapprComp) {
        BigDecimal idTrasformTipoRappr = row.getIdTrasformTipoRappr();
        DecTrasformTipoRappr decTrasformTipoRappr = helper.findById(DecTrasformTipoRappr.class, idTrasformTipoRappr);
        decTrasformTipoRappr.setTiStatoFileTrasform(CostantiDB.StatoFileTrasform.ERRATO.name());
        decTrasformTipoRappr.setDtLastModTrasform(new Date(System.currentTimeMillis()));
        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE, idTipoRapprComp,
                param.getNomePagina());
    }

    public DecImageTrasformTableBean getDecImageTrasformTableBean(BigDecimal idTrasformTipoRappr) {
        DecImageTrasformTableBean result = null;
        String root = null;
        DecTrasformTipoRappr dttr = helper.findById(DecTrasformTipoRappr.class, idTrasformTipoRappr);
        List<DecImageTrasform> lista = helper.retrieveDecImageTrasformList(idTrasformTipoRappr);
        if (lista != null && !lista.isEmpty()) {
            try {
                RispostaControlli rispostaControlli;
                HashMap<String, String> imgDefaults = null;
                rispostaControlli = controlliSemantici
                        .caricaDefaultDaDBParametriApplic(CostantiDB.TipoParametroAppl.IMAGE);
                if (rispostaControlli.isrBoolean() == false) {
                    // Messaggio di errore
                } else {
                    imgDefaults = (HashMap<String, String>) rispostaControlli.getrObject();
                    root = imgDefaults.get(CostantiDB.ParametroAppl.IMAGE_ROOT_IMAGE_TRASFORM);
                }
                BigDecimal idImageTrasform = new BigDecimal(lista.get(0).getIdImageTrasform());
                CSVersatore vers = helper.getCSVersatoreForImageTrasform(idImageTrasform);
                String pathImages = root + "/" + MessaggiWSFormat.formattaSubPathVersatoreArk(vers);
                String pathTrasformatore = root + "/" + MessaggiWSFormat.formattaSubPathVersatoreArk(vers);
                String prefix = dttr.getDecTipoRapprComp().getNmTipoRapprComp() + "-" + dttr.getNmTrasform() + "-"
                        + dttr.getCdVersioneTrasform() + "-";

                result = (DecImageTrasformTableBean) Transform.entities2TableBean(lista);
                Iterator<DecImageTrasformRowBean> itDecImageTrasform = result.iterator();
                while (itDecImageTrasform.hasNext()) {
                    DecImageTrasformRowBean img = itDecImageTrasform.next();
                    String nomeImg = prefix + img.getNmImageTrasform();
                    img.setNmCompletoImageTrasform(pathImages + "/" + nomeImg);
                    img.setTiPathTrasform(pathTrasformatore);
                }

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }

        } else {
            result = new DecImageTrasformTableBean();
        }

        return result;
    }

    public DecImageTrasformRowBean getDecImageTrasformRowBean(BigDecimal idImageTrasform) {
        DecImageTrasformRowBean decImageTrasformRowBean = null;
        String root = null;

        DecImageTrasform decImageTrasform = helper.findById(DecImageTrasform.class, idImageTrasform);
        if (decImageTrasform != null) {
            BigDecimal idTrasform = new BigDecimal(decImageTrasform.getDecTrasformTipoRappr().getIdTrasformTipoRappr());
            DecTrasformTipoRappr dttr = helper.findById(DecTrasformTipoRappr.class, idTrasform);
            try {
                RispostaControlli rispostaControlli;
                HashMap<String, String> imgDefaults = null;
                rispostaControlli = controlliSemantici
                        .caricaDefaultDaDBParametriApplic(CostantiDB.TipoParametroAppl.IMAGE);
                if (rispostaControlli.isrBoolean() == false) {
                    // Messaggio di errore
                } else {
                    imgDefaults = (HashMap<String, String>) rispostaControlli.getrObject();
                    root = imgDefaults.get(CostantiDB.ParametroAppl.IMAGE_ROOT_IMAGE_TRASFORM);
                }
                decImageTrasformRowBean = (DecImageTrasformRowBean) Transform.entity2RowBean(decImageTrasform);
                CSVersatore vers = helper.getCSVersatoreForImageTrasform(idImageTrasform);
                String pathImages = root + "/" + MessaggiWSFormat.formattaSubPathVersatoreArk(vers);
                String pathTrasformatore = root + "/" + MessaggiWSFormat.formattaSubPathVersatoreArk(vers);
                String nomeImg = dttr.getDecTipoRapprComp().getNmTipoRapprComp() + "-" + dttr.getNmTrasform() + "-"
                        + dttr.getCdVersioneTrasform() + "-" + decImageTrasform.getNmImageTrasform();
                decImageTrasformRowBean.setNmCompletoImageTrasform(pathImages + "/" + nomeImg);
                decImageTrasformRowBean.setTiPathTrasform(pathTrasformatore);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }

        }
        return decImageTrasformRowBean;
    }

    public boolean existDecImageTrasformByName(BigDecimal idTrasformTipoRappr, String nameImg) {
        DecImageTrasform decImageTrasform = helper.getDecImageTrasformByName(idTrasformTipoRappr, nameImg);
        return decImageTrasform != null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // FIXME: TESTARE
    public void updateImageTrasform(LogParam param, DecImageTrasformRowBean row, BigDecimal idTipoRapprComp) {
        BigDecimal idImageTrasform = row.getIdImageTrasform();
        DecImageTrasform decImageTrasform = helper.findById(DecImageTrasform.class, idImageTrasform);
        if (row.getBlImageTrasform() == null) {
            decImageTrasform.setNmImageTrasform(row.getNmImageTrasform());
        } else {
            decImageTrasform.setBlImageTrasform(row.getBlImageTrasform());
        }

        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE, idTipoRapprComp,
                param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // FIXME: TESTARE
    public void insertImageTrasform(LogParam param, DecImageTrasformRowBean decImageTrasformRowBean,
            BigDecimal idTipoRapprComp) {
        DecImageTrasform decImageTrasform = new DecImageTrasform();// (DecImageTrasform)
                                                                   // Transform.rowBean2Entity(decImageTrasformRowBean);
        decImageTrasform.setNmImageTrasform(decImageTrasformRowBean.getNmImageTrasform());
        DecTrasformTipoRappr trasform = helper.findById(DecTrasformTipoRappr.class,
                decImageTrasformRowBean.getIdTrasformTipoRappr());
        decImageTrasform.setDecTrasformTipoRappr(trasform);
        decImageTrasform.setBlImageTrasform(decImageTrasformRowBean.getBlImageTrasform());
        decImageTrasform.setDtLastModImageTrasform(new Date());
        helper.insertEntity(decImageTrasform, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE, idTipoRapprComp,
                param.getNomePagina());
        decImageTrasformRowBean.setIdImageTrasform(new BigDecimal(decImageTrasform.getIdImageTrasform()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // FIXME: TESTARE
    public void deleteDecImageTrasform(LogParam param, DecImageTrasformRowBean row, BigDecimal idTipoRapprComp) {
        BigDecimal idImageTrasform = row.getIdImageTrasform();
        DecImageTrasform decImageTrasform = helper.findById(DecImageTrasform.class, idImageTrasform);
        helper.removeEntity(decImageTrasform, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_RAPPRESENTAZIONE_COMPONENTE, idTipoRapprComp,
                param.getNomePagina());
    }

    public String getDownLoadNameForTrasformTipoRappr(BigDecimal idTrasformTipoRappr, String nmTrasform,
            String cdVersioneTrasform) {
        String name = null;
        CSVersatore csVersatoreForTrasform = helper.getCSVersatoreForTrasformTipoRappr(idTrasformTipoRappr);
        StringBuilder path = new StringBuilder(MessaggiWSFormat.formattaSubPathVersatoreArk(csVersatoreForTrasform));
        path.append("-");
        path.append(nmTrasform);
        path.append("-");
        path.append(cdVersioneTrasform);
        name = path.toString();

        return name;
        // return
    }

}
