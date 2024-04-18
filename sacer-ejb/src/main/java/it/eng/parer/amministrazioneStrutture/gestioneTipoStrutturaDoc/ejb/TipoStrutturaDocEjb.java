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

package it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper.TipoStrutturaDocHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecFormatoFileAmmesso;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.entity.DecTipoRapprAmmesso;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecUsoFormatoFileStandard;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecFormatoFileStandard;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
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
import org.apache.commons.lang3.StringUtils;
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
    @EJB
    private FormatoFileDocEjb formatoFileDocEjb;

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
        // if (!idFormatoFileAmmessoToDeleteList.isEmpty()) {
        // helper.deleteDecFormatoFileAmmessoList(idFormatoFileAmmessoToDeleteList);
        // }

        for (BigDecimal id : idFormatoFileAmmessoToDeleteList) {
            deleteDecFormatoFileAmmesso(id);
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
        tipoCompDoc.setFlGestiti(tipoCompDocRowBean.getFlGestiti());
        tipoCompDoc.setFlIdonei(tipoCompDocRowBean.getFlIdonei());
        tipoCompDoc.setFlDeprecati(tipoCompDocRowBean.getFlDeprecati());

        helper.insertEntity(tipoCompDoc, true);

        /* In base ai flag spuntati, inserisco i formati ammessi */
        formatoFileDocEjb.gestisciFormatiAmmessi(BigDecimal.valueOf(tipoCompDoc.getIdTipoCompDoc()),
                tipoCompDocRowBean.getFlGestiti(), tipoCompDocRowBean.getFlIdonei(),
                tipoCompDocRowBean.getFlDeprecati());

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
                tipoCompDocRowBean.getIdTipoStrutDoc()); // tipoCompDocRowBean
        String flGestitiDB = dbTipoCompDoc.getFlGestiti();
        String flIdoneiDB = dbTipoCompDoc.getFlIdonei();
        String flDeprecatiDB = dbTipoCompDoc.getFlDeprecati();
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
        tipoCompDoc.setFlGestiti(tipoCompDocRowBean.getFlGestiti());
        tipoCompDoc.setFlIdonei(tipoCompDocRowBean.getFlIdonei());
        tipoCompDoc.setFlDeprecati(tipoCompDocRowBean.getFlDeprecati());
        //
        /*
         * "Allineo" i formati GESTITI per il tipo componente SOLO se il flag è stato modificato, altrimenti allineerei
         * errorenamente
         */
        if (!StringUtils.equals(flGestitiDB, tipoCompDocRowBean.getFlGestiti())) {
            if (tipoCompDocRowBean.getFlGestiti().equals("1")) {
                /* Cerca nei DEC_FORMATO_FILE_DOC se ci sono dei formati GESTITI e in caso aggiungili */
                formatoFileDocEjb.gestisciFormatiAmmessiGestiti(idTipoCompDoc);
            } else {
                formatoFileDocEjb.eliminaFormatiAmmessiGestiti(idTipoCompDoc);
            }
        }

        /*
         * "Allineo" i formati IDONEI per il tipo componente SOLO se il flag è stato modificato, altrimenti allineerei
         * errorenamente
         */
        if (!StringUtils.equals(flIdoneiDB, tipoCompDocRowBean.getFlIdonei())) {
            if (tipoCompDocRowBean.getFlIdonei().equals("1")) {
                /* Cerca nei DEC_FORMATO_FILE_DOC se ci sono dei formati IDONEI e in caso aggiungili */
                formatoFileDocEjb.gestisciFormatiAmmessiIdonei(idTipoCompDoc);
            } else {
                formatoFileDocEjb.eliminaFormatiAmmessiIdonei(idTipoCompDoc);
            }
        }

        /*
         * "Allineo" i formati DEPRECATI per il tipo componente SOLO se il flag è stato modificato, altrimenti
         * allineerei errorenamente
         */
        if (!StringUtils.equals(flDeprecatiDB, tipoCompDocRowBean.getFlDeprecati())) {
            if (tipoCompDocRowBean.getFlDeprecati().equals("1")) {
                /* Cerca nei DEC_FORMATO_FILE_DOC se ci sono dei formati DEPRECATI e in caso aggiungili */
                formatoFileDocEjb.gestisciFormatiAmmessiDeprecati(idTipoCompDoc);
            } else {
                formatoFileDocEjb.eliminaFormatiAmmessiDeprecati(idTipoCompDoc);
            }
        }

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
    public String deleteDecFormatoFileAmmesso(LogParam param, BigDecimal idFormatoFileAmmesso,
            BigDecimal idTipoStrutDoc, String tiEsitoContrFormato) {
        DecFormatoFileAmmesso formatoFileAmmesso = helper.findById(DecFormatoFileAmmesso.class, idFormatoFileAmmesso);
        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class,
                formatoFileAmmesso.getDecTipoCompDoc().getIdTipoCompDoc());
        String flGestitiPreDelete = tipoCompDoc.getFlGestiti();
        String flIdoneiPreDelete = tipoCompDoc.getFlIdonei();
        String flDeprecatiPreDelete = tipoCompDoc.getFlDeprecati();
        helper.removeEntity(formatoFileAmmesso, true);
        String messaggio = null;
        /*
         * La cancellazione di un formato, comporta sicuramente, nel caso fosse settato a 1, l'azzeramento del relativo
         * flag GESTITO, IDONEO o DEPRECATO
         */
        if (tiEsitoContrFormato.equals(DecFormatoFileStandard.TiEsitoControFormato.GESTITO.name())) {
            if (flGestitiPreDelete.equals("1")) {
                messaggio = "La cancellazione del formato ammesso ha comportato il settaggio a 'false' del flag GESTITI";
            }
            tipoCompDoc.setFlGestiti("0");
        } else if (tiEsitoContrFormato.equals(DecFormatoFileStandard.TiEsitoControFormato.IDONEO.name())) {
            if (flIdoneiPreDelete.equals("1")) {
                messaggio = "La cancellazione del formato ammesso ha comportato il settaggio a 'false' del flag IDONEI";
            }
            tipoCompDoc.setFlIdonei("0");
        } else if (tiEsitoContrFormato.equals(DecFormatoFileStandard.TiEsitoControFormato.DEPRECATO.name())) {
            if (flDeprecatiPreDelete.equals("1")) {
                messaggio = "La cancellazione del formato ammesso ha comportato il settaggio a 'false' del seguente DEPRECATI";
            }
            tipoCompDoc.setFlDeprecati("0");
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO, idTipoStrutDoc,
                param.getNomePagina());

        return messaggio;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileAmmesso(BigDecimal idFormatoFileAmmesso) {
        DecFormatoFileAmmesso formatoFileAmmesso = helper.findById(DecFormatoFileAmmesso.class, idFormatoFileAmmesso);
        helper.removeEntity(formatoFileAmmesso, true);
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

    public void updateFlagTipoComponente(BigDecimal idTipoCompDoc) {
        int gestitiTotali = 0;
        int idoneiTotali = 0;
        int deprecatiTotali = 0;

        // Recupero i formati ammessi per il tipo componente IN QUESTO MOMENTO
        // a seguito delle modifiche sto apportando
        DecFormatoFileDocTableBean formatoFileAmmessoTableBean = formatoFileDocEjb
                .getDecFormatoFileAmmessoTableBean(idTipoCompDoc);
        // Discerno tra totali GESTITI, IDONEI e DEPRECATI
        int totGestitiPreSalvataggio = 0;
        int totIdoneiPreSalvataggio = 0;
        int totDeprecatiPreSalvataggio = 0;

        for (DecFormatoFileDocRowBean formatoFileAmmessoRowBean : formatoFileAmmessoTableBean) {
            if (formatoFileAmmessoRowBean.getString("ti_esito_contr_formato") != null) {
                if (formatoFileAmmessoRowBean.getString("ti_esito_contr_formato")
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.GESTITO.name())) {
                    totGestitiPreSalvataggio++;
                }
                if (formatoFileAmmessoRowBean.getString("ti_esito_contr_formato")
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.IDONEO.name())) {
                    totIdoneiPreSalvataggio++;
                }
                if (formatoFileAmmessoRowBean.getString("ti_esito_contr_formato")
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.DEPRECATO.name())) {
                    totDeprecatiPreSalvataggio++;
                }
            }
        }

        /* Ricavo il numero massimo di formati possibili GESTITI, IDONEI e DEPRECATI */
        DecTipoCompDoc compDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        OrgStrut strut = compDoc.getDecTipoStrutDoc().getOrgStrut();// strut
        List<DecFormatoFileDoc> formatoFileDocList = strut.getDecFormatoFileDocs();
        /* Scorro tutti i DEC_FORMATO_FILE_DOC della struttura */
        for (DecFormatoFileDoc formatoFileDoc : formatoFileDocList) {
            /* Per ognuno verifica se è GESTITO e in caso lo inserisco come tale in DEC_FORMATO_FILE_AMMESSO */
            int lunghezza = formatoFileDoc.getDecUsoFormatoFileStandards().size();
            if (lunghezza > 0) {
                DecUsoFormatoFileStandard usoFormatoFileStandard = formatoFileDoc.getDecUsoFormatoFileStandards()
                        .get(formatoFileDoc.getDecUsoFormatoFileStandards().size() - 1);
                /* Se il formato è di tipo GESTITO */
                if (usoFormatoFileStandard.getDecFormatoFileStandard().getTiEsitoContrFormato()
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.GESTITO.name())) {
                    gestitiTotali++;
                }
                if (usoFormatoFileStandard.getDecFormatoFileStandard().getTiEsitoContrFormato()
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.IDONEO.name())) {
                    idoneiTotali++;
                }
                if (usoFormatoFileStandard.getDecFormatoFileStandard().getTiEsitoContrFormato()
                        .equals(DecFormatoFileStandard.TiEsitoControFormato.DEPRECATO.name())) {
                    deprecatiTotali++;
                }
            }
        }
        // compDoc poi cancella
        DecTipoCompDoc tipoCompDoc = helper.findById(DecTipoCompDoc.class, idTipoCompDoc);
        if (gestitiTotali == totGestitiPreSalvataggio) {
            tipoCompDoc.setFlGestiti("1");
        } else {
            tipoCompDoc.setFlGestiti("0");
        }
        if (idoneiTotali == totIdoneiPreSalvataggio) {
            tipoCompDoc.setFlIdonei("1");
        } else {
            tipoCompDoc.setFlIdonei("0");
        }
        if (deprecatiTotali == totDeprecatiPreSalvataggio) {
            tipoCompDoc.setFlDeprecati("1");
        } else {
            tipoCompDoc.setFlDeprecati("0");
        }

    }

}
