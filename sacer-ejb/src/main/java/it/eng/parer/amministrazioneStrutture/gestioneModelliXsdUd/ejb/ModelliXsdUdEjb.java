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

package it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.helper.ModelliXsdUdHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecModelloXsdUd;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecUsoModelloXsdDoc;
import it.eng.parer.entity.DecUsoModelloXsdUniDoc;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.ModelliUDForm.FiltriModelliXsdUd;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdUniDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdUniDocTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaSacer;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class ModelliXsdUdEjb {

    private static final Logger logger = LoggerFactory.getLogger(ModelliXsdUdEjb.class);

    private static final String PARERUSERERR_MDLXSD_PREFIX = "Errore su Modello XSD: ";
    private static final String PARERUSERERR_USOMDLXSD_PREFIX = "Errore su Versione XSD profilo ammesso: ";

    @Resource
    private SessionContext context;
    @EJB
    private ModelliXsdUdHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;

    /**
     * Resitutisce la lista dei modelli xsd associati all'unità documentaria
     *
     * @param idTipoUniDoc
     *            id tipo unita documentaria
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     *
     * @return tabella con i modelli
     */
    public DecUsoModelloXsdUniDocTableBean getDecModelliXsdUdInUsoOnUniDoc(BigDecimal idTipoUniDoc,
            String tiUsoModelloXsd) {
        List<?> result = helper.retrieveDecModelliXsdUdListByTiEntitaInUso(idTipoUniDoc, TipiEntitaSacer.UNI_DOC,
                tiUsoModelloXsd, false);
        return usoModelloXsdUniDoc2TableBean(result);
    }

    /**
     * Resitutisce la lista dei modelli xsd associati al tipo document
     *
     * @param idTipoDoc
     *            id tipo documento
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     *
     * @return tabella con i modelli
     */
    public DecUsoModelloXsdDocTableBean getDecModelliXsdUdInUsoOnDoc(BigDecimal idTipoDoc, String tiUsoModelloXsd) {
        List<?> result = helper.retrieveDecModelliXsdUdListByTiEntitaInUso(idTipoDoc, TipiEntitaSacer.DOC,
                tiUsoModelloXsd, false);
        return usoModelloXsdDoc2TableBean(result);
    }

    /**
     * Resitutisce il dettaglio di un modello a partire dall'Id del modello e dall'id del tipo unità documentaria
     *
     * @param idTipoUniDoc
     *            id tipo unità doc
     * @param idModelloXsdUd
     *            id modello xsd
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     * @param cdXsd
     *            versione modello xsd
     *
     * @return row bean
     */
    public DecUsoModelloXsdUniDocRowBean getDecModelloXsdUdInUsoOnUniDoc(BigDecimal idTipoUniDoc,
            BigDecimal idModelloXsdUd, String tiUsoModelloXsd, String cdXsd) {
        List<?> result = helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(idModelloXsdUd, idTipoUniDoc,
                TipiEntitaSacer.UNI_DOC, tiUsoModelloXsd, cdXsd, StringUtils.EMPTY, false);
        return usoModelloXsdUdUniDoc2RowBean((DecUsoModelloXsdUniDoc) result.get(0));
    }

    /**
     * @param idTipoDoc
     *            id tipo documento
     * @param idModelloXsdUd
     *            id modello xsd
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     * @param cdXsd
     *            versione modello xsd
     *
     * @return row bean
     */
    public DecUsoModelloXsdDocRowBean getDecModelloXsdUdInUsoOnDoc(BigDecimal idTipoDoc, BigDecimal idModelloXsdUd,
            String tiUsoModelloXsd, String cdXsd) {
        List<?> result = helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(idModelloXsdUd, idTipoDoc,
                TipiEntitaSacer.DOC, tiUsoModelloXsd, cdXsd, StringUtils.EMPTY, false);
        return usoModelloXsdUdDoc2RowBean((DecUsoModelloXsdDoc) result.get(0));
    }

    private DecModelloXsdUdRowBean modelloXsdUd2RowBean(List<DecModelloXsdUd> modelliXsdUd) {
        DecModelloXsdUdRowBean row = null;
        try {
            row = (DecModelloXsdUdRowBean) Transform.entity2RowBean(modelliXsdUd.get(0));
            row.setString("nm_ambiente", modelliXsdUd.get(0).getOrgAmbiente().getNmAmbiente());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | IndexOutOfBoundsException ex) {
            logger.error("Errore durante conversione DecModelloXsdUd to DecModelloXsdUdRowBean {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return row;
    }

    /*
     * Uso modello to rowbean
     *
     */
    private DecUsoModelloXsdUniDocRowBean usoModelloXsdUdUniDoc2RowBean(DecUsoModelloXsdUniDoc usoModelloXsdUniDoc) {
        DecUsoModelloXsdUniDocRowBean row = null;
        try {
            row = (DecUsoModelloXsdUniDocRowBean) Transform.entity2RowBean(usoModelloXsdUniDoc);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | IndexOutOfBoundsException ex) {
            logger.error("Errore durante conversione DecModelloXsdUd to DecModelloXsdUdRowBean {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return row;
    }

    private DecUsoModelloXsdDocRowBean usoModelloXsdUdDoc2RowBean(DecUsoModelloXsdDoc usoModelloXsdUniDoc) {
        DecUsoModelloXsdDocRowBean row = null;
        try {
            row = (DecUsoModelloXsdDocRowBean) Transform.entity2RowBean(usoModelloXsdUniDoc);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | IndexOutOfBoundsException ex) {
            logger.error("Errore durante conversione DecModelloXsdUd to DecModelloXsdUdRowBean {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return row;
    }

    /**
     * Resitutisce la rappresentazione tabellare filtrando per id ambiente e uso
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiUsoModello
     *            tipo uso modello
     *
     * @return table bean
     */
    public DecModelloXsdUdTableBean getDecModelloXsdUdInUsoByIdAmbAndTiUso(BigDecimal idAmbiente, String tiUsoModello) {
        List<DecModelloXsdUd> modelloXsdUd = helper.retrieveDecModelliXsdUd4Amb(idAmbiente, tiUsoModello, false);
        return modelloXsdUd2TableBean(modelloXsdUd);
    }

    /**
     * Resitutisce la rappresentazione tabellare filtrando per tipo modello e uso
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModello
     *            tipo modello
     * @param tiUsoModello
     *            tipo uso modello
     *
     * @return table bean
     */
    public DecModelloXsdUdTableBean getDecModelloXsdUdInUso4AmbAndTiModelloXsd(BigDecimal idAmbiente, String tiModello,
            String tiUsoModello) {
        List<DecModelloXsdUd> modelloXsdUd = helper.retrieveDecModelliXsdUd4AmbAndTiModelloXsd(idAmbiente, tiModello,
                tiUsoModello, false);
        return modelloXsdUd2TableBean(modelloXsdUd);
    }

    /**
     * Resitutisce una mappa chiave/valore per liste a selezione
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiUsoModello
     *            tipo uso modello
     * @param key
     *            chiave mappa
     *
     * @return decoded map
     */
    public DecodeMap getTiModelloXsdInUsoByIdAmbTiUso(BigDecimal idAmbiente, String tiUsoModello, final String key) {
        // table
        DecModelloXsdUdTableBean modelloXsdUdTableBean = getDecModelloXsdUdInUsoByIdAmbAndTiUso(idAmbiente,
                tiUsoModello);
        modelloXsdUdTableBean.addSortingRule(key, SortingRule.ASC);
        modelloXsdUdTableBean.sort();
        // decode map
        DecodeMap mpTiModelloXsd = new DecodeMap();
        mpTiModelloXsd.populatedMap(modelloXsdUdTableBean, key, key);
        return mpTiModelloXsd;
    }

    /**
     * Resitutisce una mappa chiave/valore per liste a selezione
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModello
     *            tipo modello
     * @param tiUsoModello
     *            tipo uso modello
     * @param key
     *            chiave mappa
     *
     * @return decoded map
     */
    public DecodeMap getCdXsdInUsoByIdAmbTiModUso(BigDecimal idAmbiente, String tiModello, String tiUsoModello,
            final String key) {
        // table
        DecModelloXsdUdTableBean modelloXsdUdTableBean = getDecModelloXsdUdInUso4AmbAndTiModelloXsd(idAmbiente,
                tiModello, tiUsoModello);
        modelloXsdUdTableBean.addSortingRule(key, SortingRule.ASC);
        modelloXsdUdTableBean.sort();
        // decode map
        DecodeMap mpCdXsd = new DecodeMap();
        mpCdXsd.populatedMap(modelloXsdUdTableBean, key, key);
        return mpCdXsd;
    }

    private DecUsoModelloXsdUniDocTableBean usoModelloXsdUniDoc2TableBean(List<?> usoModelliXsdUd) {
        DecUsoModelloXsdUniDocTableBean table = new DecUsoModelloXsdUniDocTableBean();
        if (!usoModelliXsdUd.isEmpty()) {
            try {
                table = (DecUsoModelloXsdUniDocTableBean) Transform.entities2TableBean(usoModelliXsdUd);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante conversione DecUsoModelloXsdUniDoc to DecUsoModelloXsdUniDocTableBean {}",
                        ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    private DecUsoModelloXsdDocTableBean usoModelloXsdDoc2TableBean(List<?> usoModelliXsdUd) {
        DecUsoModelloXsdDocTableBean table = new DecUsoModelloXsdDocTableBean();
        if (!usoModelliXsdUd.isEmpty()) {
            try {
                table = (DecUsoModelloXsdDocTableBean) Transform.entities2TableBean(usoModelliXsdUd);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante conversione DecUsoModelloXsdDoc to DecUsoModelloXsdDocTableBean {}",
                        ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    private DecModelloXsdUdTableBean modelloXsdUd2TableBean(List<DecModelloXsdUd> modelliXsdUd) {
        DecModelloXsdUdTableBean table = new DecModelloXsdUdTableBean();
        if (!modelliXsdUd.isEmpty()) {
            try {
                table = (DecModelloXsdUdTableBean) Transform.entities2TableBean(modelliXsdUd);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante conversione DecModelloXsdUd to DecModelloXsdUdTableBean {}",
                        ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Inserimento della relazione (uso modello)
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveUsoModelloXsdUniDoc(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean)
            throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello xsd ud ammesso");
        try {
            // recupero del modello
            List<DecModelloXsdUd> modelliXsdUd = helper.retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(
                    modelloXsdUdRowBean.getIdAmbiente(), modelloXsdUdRowBean.getTiModelloXsd(),
                    modelloXsdUdRowBean.getTiUsoModelloXsd(), modelloXsdUdRowBean.getCdXsd(), false);

            if (modelliXsdUd.isEmpty()) {
                throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX + "Eccezione imprevista durante il salvataggio");
            }
            //
            DecModelloXsdUd modelloXsdUd = modelliXsdUd.get(0);

            modelloXsdUd.setDecUsoModelloXsdUniDocs(new ArrayList<>());
            DecUsoModelloXsdUniDoc usoModelloXsdUniDoc = new DecUsoModelloXsdUniDoc();
            usoModelloXsdUniDoc.setFlStandard(modelloXsdUdRowBean.getFlStandard());
            usoModelloXsdUniDoc.setDtIstituz(modelloXsdUdRowBean.getDtUsoIstituz());
            usoModelloXsdUniDoc.setDtSoppres(modelloXsdUdRowBean.getDtUsoSoppres());
            usoModelloXsdUniDoc.setDecModelloXsdUd(modelloXsdUd);
            usoModelloXsdUniDoc
                    .setDecTipoUnitaDoc(helper.findById(DecTipoUnitaDoc.class, modelloXsdUdRowBean.getIdTipoUniDoc()));
            helper.insertEntity(usoModelloXsdUniDoc, false);
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                    modelloXsdUdRowBean.getIdTipoUniDoc(), param.getNomePagina());

            logger.info("Salvataggio del modello xsd ud ammesso");
        } catch (Exception ex) {
            logger.error(PARERUSERERR_USOMDLXSD_PREFIX + "Errore imprevisto durante il salvataggio : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX + "Eccezione imprevista durante il salvataggio");
        }
    }

    /**
     * Inserimento della relazione (uso modello)
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveUsoModelloXsdDoc(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean) throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello xsd ud ammesso");
        try {
            // recupero del modello
            List<DecModelloXsdUd> modelliXsdUd = helper.retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(
                    modelloXsdUdRowBean.getIdAmbiente(), modelloXsdUdRowBean.getTiModelloXsd(),
                    modelloXsdUdRowBean.getTiUsoModelloXsd(), modelloXsdUdRowBean.getCdXsd(), false);

            if (modelliXsdUd.isEmpty()) {
                throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX + "Eccezione imprevista durante il salvataggio");
            }
            //
            DecModelloXsdUd modelloXsdUd = modelliXsdUd.get(0);

            modelloXsdUd.setDecUsoModelloXsdDocs(new ArrayList<>());
            DecUsoModelloXsdDoc usoModelloXsdDoc = new DecUsoModelloXsdDoc();
            usoModelloXsdDoc.setFlStandard(modelloXsdUdRowBean.getFlStandard());
            usoModelloXsdDoc.setDtIstituz(modelloXsdUdRowBean.getDtUsoIstituz());
            usoModelloXsdDoc.setDtSoppres(modelloXsdUdRowBean.getDtUsoSoppres());
            usoModelloXsdDoc.setDecModelloXsdUd(modelloXsdUd);
            usoModelloXsdDoc.setDecTipoDoc(helper.findById(DecTipoDoc.class, modelloXsdUdRowBean.getIdTipoDoc()));
            helper.insertEntity(usoModelloXsdDoc, false);
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
                    modelloXsdUdRowBean.getIdTipoDoc(), param.getNomePagina());

            logger.info("Salvataggio del modello xsd ud ammesso");
        } catch (Exception ex) {
            logger.error(PARERUSERERR_USOMDLXSD_PREFIX + "Errore imprevisto durante il salvataggio : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX + "Eccezione imprevista durante il salvataggio");
        }
    }

    /**
     * Verifica se per il tipo entita sacer (UNI_DOC) esiste una relazione con il modello xsd ud
     *
     * @param idTipoUniDoc
     *            id tipo unità doc
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     * @param cdXsd
     *            versione modello xsd
     *
     * @return true = se esiste relazione, false = altrimenti
     */
    public boolean existDecUsoModelloXsdUdOnUniDoc(BigDecimal idTipoUniDoc, String tiUsoModelloXsd, String cdXsd) {
        return !getDecUsoModelloXsdUd(TipiEntitaSacer.UNI_DOC, idTipoUniDoc, null, tiUsoModelloXsd, cdXsd,
                StringUtils.EMPTY).isEmpty();
    }

    /**
     * Verifica se per il tipo entita sacer (DOC) esiste una relazione con il modello xsd ud
     *
     * @param idTipoDoc
     *            id tipo doc
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     * @param cdXsd
     *            versione modello xsd
     *
     * @return true = se esiste relazione, false = altrimenti
     */
    public boolean existDecUsoModelloXsdUdOnDoc(BigDecimal idTipoDoc, String tiUsoModelloXsd, String cdXsd) {
        return !getDecUsoModelloXsdUd(TipiEntitaSacer.DOC, null, idTipoDoc, tiUsoModelloXsd, cdXsd, StringUtils.EMPTY)
                .isEmpty();

    }

    /**
     * Verifica se per il tipo entita sacer (UNI_DOC) esiste un modello standard in uso che non sia idModelloXsdUd
     * (lambda filter)
     *
     * @param idTipoUniDoc
     *            id tipo uni doc
     * @param idModelloXsdUd
     *            id modello xsd
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     *
     * @return true = se esiste relazione standard, false = altrimenti
     */
    public boolean existDecUsoModelloXsdUdOnUniDocStandard(BigDecimal idTipoUniDoc, BigDecimal idModelloXsdUd,
            String tiUsoModelloXsd) {
        List<DecUsoModelloXsdUniDoc> result = getDecUsoModelloXsdUd(TipiEntitaSacer.UNI_DOC, idTipoUniDoc, null,
                tiUsoModelloXsd, StringUtils.EMPTY, CostantiDB.Flag.TRUE);
        return result.stream()
                .filter(u -> idModelloXsdUd != null
                        && u.getDecModelloXsdUd().getIdModelloXsdUd().longValue() != idModelloXsdUd.longValue()
                        || idModelloXsdUd == null)
                .count() != 0;
    }

    /**
     * Verifica se per il tipo entita sacer (DOC) esiste un modello standard in uso che non sia idModelloXsdUd (lambda
     * filter)
     *
     * @param idTipoDoc
     *            id tipo doc
     * @param idModelloXsdUd
     *            id modello xsd
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     *
     * @return true = se esiste relazione standard, false = altrimenti
     */
    public boolean existDecUsoModelloXsdUdOnDocStandard(BigDecimal idTipoDoc, BigDecimal idModelloXsdUd,
            String tiUsoModelloXsd) {
        List<DecUsoModelloXsdDoc> result = getDecUsoModelloXsdUd(TipiEntitaSacer.DOC, null, idTipoDoc, tiUsoModelloXsd,
                StringUtils.EMPTY, CostantiDB.Flag.TRUE);
        return result.stream()
                .filter(u -> idModelloXsdUd != null
                        && u.getDecModelloXsdUd().getIdModelloXsdUd().longValue() != idModelloXsdUd.longValue()
                        || idModelloXsdUd == null)
                .count() != 0;
    }

    private List getDecUsoModelloXsdUd(TipiEntitaSacer tiEntitaSacer, BigDecimal idTipoUniDoc, BigDecimal idTipoDoc,
            String tiUsoModelloXsd, String cdXsd, String flStandard) {
        if (idTipoUniDoc != null) {
            return helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(null, idTipoUniDoc, tiEntitaSacer,
                    tiUsoModelloXsd, cdXsd, flStandard, false);
        } else if (idTipoDoc != null) {
            return helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(null, idTipoDoc, tiEntitaSacer, tiUsoModelloXsd,
                    cdXsd, flStandard, false);
        }
        return new ArrayList<>(); // empty list
    }

    /**
     * Restituisce il row bean del modello
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     * @param tiUsoModelloXsd
     *            tipo uso modello xsd
     * @param cdXsd
     *            versione modello xsd
     * @param filterValid
     *            true se nel periodo di valità / false altrimenti
     *
     * @return row bean
     */
    public DecModelloXsdUdRowBean getDecModelloXsdUd(BigDecimal idAmbiente, String tiModelloXsd, String tiUsoModelloXsd,
            String cdXsd, boolean filterValid) {
        List<DecModelloXsdUd> modelliXsdUd = helper.retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(idAmbiente,
                tiModelloXsd, tiUsoModelloXsd, cdXsd, filterValid);
        return modelloXsdUd2RowBean(modelliXsdUd);
    }

    /**
     * Restituisce il row bean del modello
     *
     * @param idModelloXsdUd
     *            id modello xsd ud
     *
     * @return row bean
     */
    public DecModelloXsdUdRowBean getDecModelloXsdUd(BigDecimal idModelloXsdUd) {
        DecModelloXsdUd modelloXsdUd = helper.findById(DecModelloXsdUd.class, idModelloXsdUd);
        return modelloXsdUd2RowBean(Arrays.asList(modelloXsdUd));
    }

    /**
     * Aggiornamento uso modello xsd documento
     *
     * @param param
     *            parametro logging
     * @param modelloXsdUdRowBean
     *            row bean del modello
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecModelloXsdUdInUsoUniDoc(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean)
            throws ParerUserError {
        List<?> result = helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(modelloXsdUdRowBean.getIdModelloXsdUd(),
                modelloXsdUdRowBean.getIdTipoUniDoc(), TipiEntitaSacer.UNI_DOC,
                modelloXsdUdRowBean.getTiUsoModelloXsd(), modelloXsdUdRowBean.getCdXsd(), StringUtils.EMPTY, false);
        if (!result.isEmpty()) {
            DecUsoModelloXsdUniDoc usoModelloXsdUniDoc = (DecUsoModelloXsdUniDoc) result.get(0);
            usoModelloXsdUniDoc.setFlStandard(modelloXsdUdRowBean.getFlStandard());
            usoModelloXsdUniDoc.setDtSoppres(modelloXsdUdRowBean.getDtUsoSoppres());
            helper.getEntityManager().flush();
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                    modelloXsdUdRowBean.getIdTipoUniDoc(), param.getNomePagina());
        } else {
            throw new ParerUserError(
                    PARERUSERERR_USOMDLXSD_PREFIX + "Errore su modifica, elemento non recuperato correttamente</br>");
        }
    }

    /**
     * Aggiornamento uso modello xsd documento
     *
     * @param param
     *            parametro logging
     * @param modelloXsdUdRowBean
     *            row bean del modello
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecModelloXsdUdInUsoDoc(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean)
            throws ParerUserError {
        List<?> result = helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(modelloXsdUdRowBean.getIdModelloXsdUd(),
                modelloXsdUdRowBean.getIdTipoDoc(), TipiEntitaSacer.DOC, modelloXsdUdRowBean.getTiUsoModelloXsd(),
                modelloXsdUdRowBean.getCdXsd(), StringUtils.EMPTY, false);

        if (!result.isEmpty()) {
            DecUsoModelloXsdUniDoc usoModelloXsdDoc = (DecUsoModelloXsdUniDoc) result.get(0);
            usoModelloXsdDoc.setFlStandard(modelloXsdUdRowBean.getFlStandard());
            usoModelloXsdDoc.setDtSoppres(modelloXsdUdRowBean.getDtUsoSoppres());
            helper.getEntityManager().flush();

            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
                    modelloXsdUdRowBean.getIdTipoDoc(), param.getNomePagina());
        } else {
            throw new ParerUserError(
                    PARERUSERERR_USOMDLXSD_PREFIX + "Errore su modifica, elemento non recuperato correttamente</br>");
        }
    }

    /**
     * Cancellazione con controllo dell'uso modello xsd per UNI_DOC
     *
     * @param param
     *            log param
     * @param idStrut
     *            id struttura
     * @param idTipoUniDoc
     *            id topo unita documentaria
     * @param idUsoModelloXsdUd
     *            id uso modello xsd
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecUsoModelloXsdUniDoc(LogParam param, BigDecimal idStrut, BigDecimal idTipoUniDoc,
            BigDecimal idUsoModelloXsdUd) throws ParerUserError {
        deleteDecUsoModelloXsd(param, idStrut, idTipoUniDoc, null, idUsoModelloXsdUd);
    }

    /**
     * Cancellazione con controllo dell'uso modello xsd per DOC
     *
     * @param param
     *            log param
     * @param idStrut
     *            id struttura
     * @param idTipoDoc
     *            id tipo documento
     * @param idUsoModelloXsdUd
     *            id uso modello
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecUsoModelloXsdDoc(LogParam param, BigDecimal idStrut, BigDecimal idTipoDoc,
            BigDecimal idUsoModelloXsdUd) throws ParerUserError {
        deleteDecUsoModelloXsd(param, idStrut, null, idTipoDoc, idUsoModelloXsdUd);
    }

    private void deleteDecUsoModelloXsd(LogParam param, BigDecimal idStrut, BigDecimal idTipoUniDoc,
            BigDecimal idTipoDoc, BigDecimal idUsoModelloXsdUd) throws ParerUserError {

        if (idTipoUniDoc != null) {
            DecUsoModelloXsdUniDoc usoModelloXsdUniDoc = helper.findById(DecUsoModelloXsdUniDoc.class,
                    idUsoModelloXsdUd);

            if (usoModelloXsdUniDoc != null) {
                if (helper.decUsoModelloXsdUdInUseOnVrs(idStrut, idUsoModelloXsdUd, TipiEntitaSacer.UNI_DOC)) {
                    throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX
                            + "Eliminazione non consentita in quanto esiste almeno un elemento associato</br>");
                }
                helper.removeEntity(usoModelloXsdUniDoc, true);
                // log evento
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUniDoc,
                        param.getNomePagina());
            } else {
                throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX
                        + "Errore su elminazione, elemento non recuperato correttamente</br>");
            }
        } else if (idTipoDoc != null) {
            DecUsoModelloXsdDoc usoModelloXsdDoc = helper.findById(DecUsoModelloXsdDoc.class, idUsoModelloXsdUd);

            if (usoModelloXsdDoc != null) {
                if (helper.decUsoModelloXsdUdInUseOnVrs(idStrut, idUsoModelloXsdUd, TipiEntitaSacer.DOC)) {
                    throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX
                            + "Impossibile eliminare versione xsd modello ammesso, esiste almeno un elemento associato ad esso</br>");
                }
                helper.removeEntity(usoModelloXsdDoc, true);
                // log evento
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
                        param.getNomePagina());
            } else {
                throw new ParerUserError(PARERUSERERR_USOMDLXSD_PREFIX
                        + "Errore su elminazione, elemento non recuperato correttamente</br>");
            }
        }

    }

    /**
     * Verifica se il modello in uso nel tipo UNI_DOC
     *
     * @param idStrut
     *            id struttura
     * @param idUsoModelloXsdUd
     *            id uso modello
     *
     * @return true se esiste almeno una sessione di versamento / false altrimenti
     */
    public boolean isUsoModelloXsdUdUniDocInUse(BigDecimal idStrut, BigDecimal idUsoModelloXsdUd) {
        return helper.decUsoModelloXsdUdInUseOnVrs(idStrut, idUsoModelloXsdUd, TipiEntitaSacer.UNI_DOC);
    }

    /**
     * Verifica se il modello in uso nel tipo DOC
     *
     * @param idStrut
     *            id struttura
     * @param idUsoModelloXsdUd
     *            id uso modello xsd
     *
     * @return true se esiste almeno una sessione di versamento / false altrimenti
     */
    public boolean isUsoModelloXsdUdDocInUse(BigDecimal idStrut, BigDecimal idUsoModelloXsdUd) {
        return helper.decUsoModelloXsdUdInUseOnVrs(idStrut, idUsoModelloXsdUd, TipiEntitaSacer.DOC);
    }

    /**
     * Verifica se il modello in uso nel tipo UNI_DOC
     *
     * @param idModelloXsdUd
     *            id modello
     *
     * @return true se esiste almeno una sessione di versamento / false altrimenti
     */
    public boolean isModelloXsdUdInUse(BigDecimal idModelloXsdUd) {
        return !helper.decModelloXsdUdInUseOnVrs(idModelloXsdUd);
    }

    /**
     * Disattivazione uso modello xsd nel tipo UNI_DOC
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @return row table aggiornato
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DecUsoModelloXsdUniDocTableBean deactivateDecModelloXsdUdInUsoUniDoc(LogParam param,
            DecModelloXsdUdRowBean modelloXsdUdRowBean) throws ParerUserError {
        //
        DecUsoModelloXsdUniDoc usoModelloXsdUniDoc = helper.findById(DecUsoModelloXsdUniDoc.class,
                modelloXsdUdRowBean.getIdUsoModelloXsdUniDoc());

        if (usoModelloXsdUniDoc != null) {
            usoModelloXsdUniDoc.setDtSoppres(Calendar.getInstance().getTime());
            helper.getEntityManager().flush();

            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                    modelloXsdUdRowBean.getIdTipoUniDoc(), param.getNomePagina());
        } else {
            throw new ParerUserError(
                    PARERUSERERR_USOMDLXSD_PREFIX + "Errore su disattivazione, elemento non trovato</br>");
        }
        // refresh table
        List<?> result = helper.retrieveDecModelliXsdUdListByTiEntitaInUso(modelloXsdUdRowBean.getIdTipoUniDoc(),
                TipiEntitaSacer.UNI_DOC, modelloXsdUdRowBean.getTiUsoModelloXsd(), false);
        return usoModelloXsdUniDoc2TableBean(result);
    }

    /**
     * Disattivazione uso modello xsd nel tipo DOC
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @return row table aggiornato
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DecUsoModelloXsdDocTableBean deactivateDecModelloXsdUdInUsoDoc(LogParam param,
            DecModelloXsdUdRowBean modelloXsdUdRowBean) throws ParerUserError {

        //
        DecUsoModelloXsdDoc usoModelloXsdDoc = helper.findById(DecUsoModelloXsdDoc.class,
                modelloXsdUdRowBean.getIdUsoModelloXsdDoc());

        if (usoModelloXsdDoc != null) {
            //
            usoModelloXsdDoc.setDtSoppres(Calendar.getInstance().getTime());
            helper.getEntityManager().flush();
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
                    modelloXsdUdRowBean.getIdTipoDoc(), param.getNomePagina());
        } else {
            throw new ParerUserError(
                    PARERUSERERR_USOMDLXSD_PREFIX + "Errore su disattivazione, elemento non trovato</br>");
        }
        // refresh table
        List<?> result = helper.retrieveDecModelliXsdUdListByTiEntitaInUso(modelloXsdUdRowBean.getIdTipoDoc(),
                TipiEntitaSacer.DOC, modelloXsdUdRowBean.getTiUsoModelloXsd(), false);
        return usoModelloXsdDoc2TableBean(result);
    }

    /**
     * Verifica se il modello è in uso
     *
     * @param idModelloXsdUd
     *            id modello xsd ud
     *
     * @return true = se esiste relazione, false = altrimenti
     */
    public boolean existDecUsoModelloXsdUdAtMostOnce(BigDecimal idModelloXsdUd) {
        return helper.existDecModelliXsdUdListInUso(idModelloXsdUd, false);

    }

    /**
     * Restituisce il table bean dei modelli xsd ud
     *
     * @param filtriModelliXsdUd
     *            filtro modelli xsd
     * @param idAmbientiToFind
     *            lista con id ambiente
     * @param tiUsoModelloXsd
     *            tipo uso modello
     * @param filterValid
     *            true se nel periodo di valità / false altrimenti
     *
     * @return table bean
     *
     * @throws EMFError
     *             eccezione generico
     */
    public DecModelloXsdUdTableBean findDecModelloXsdUd(FiltriModelliXsdUd filtriModelliXsdUd,
            List<BigDecimal> idAmbientiToFind, String tiUsoModelloXsd, boolean filterValid) throws EMFError {
        Date today = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        // table
        DecModelloXsdUdTableBean table = new DecModelloXsdUdTableBean();

        List<DecModelloXsdUd> result = helper.findDecModelliXsdUdList(filtriModelliXsdUd, idAmbientiToFind,
                tiUsoModelloXsd, filterValid);

        if (!result.isEmpty()) {
            Set<DecModelloXsdUd> modelli = new HashSet<>();
            modelli.addAll(result);
            for (DecModelloXsdUd modello : modelli) {
                DecModelloXsdUdRowBean row = modelloXsdUd2RowBean(Arrays.asList(modello));
                if (row != null) {
                    row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
                    if ((modello.getDtIstituz().equals(today) || modello.getDtIstituz().before(today))
                            && modello.getDtSoppres().after(today)) {
                        row.setString("fl_attivo", CostantiDB.Flag.TRUE);
                    } else {
                        row.setString("fl_attivo", CostantiDB.Flag.FALSE);
                    }
                    table.add(row);
                }
            }
        }
        return table;
    }

    /**
     * Salvataggio modello xsd
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @return pk del modello
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveModelloXsdUd(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean) throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello xsd");
        Long idModelloXsdUd = null;
        try {
            DecModelloXsdUd modelloXsdUd = (DecModelloXsdUd) Transform.rowBean2Entity(modelloXsdUdRowBean);
            helper.insertEntity(modelloXsdUd, true);

            logger.info("Salvataggio del modello xsd");
            idModelloXsdUd = modelloXsdUd.getIdModelloXsdUd();
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_UD, new BigDecimal(idModelloXsdUd),
                    param.getNomePagina());
        } catch (Exception ex) {
            logger.error(PARERUSERERR_MDLXSD_PREFIX + "Errore imprevisto durante il salvataggio del modello xsd {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    PARERUSERERR_MDLXSD_PREFIX + "Eccezione imprevista durante il salvataggio del modello xsd");
        }
        return idModelloXsdUd;
    }

    /**
     * Aggiornamento modello xsd
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateModelloXsdUd(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean) {
        DecModelloXsdUd modelliXsdUd = helper.findById(DecModelloXsdUd.class, modelloXsdUdRowBean.getIdModelloXsdUd());

        if (StringUtils.isNotBlank(modelloXsdUdRowBean.getBlXsd())) {
            modelliXsdUd.setBlXsd(modelloXsdUdRowBean.getBlXsd());
        }
        modelliXsdUd.setDtSoppres(modelloXsdUdRowBean.getDtSoppres());
        modelliXsdUd.setDsXsd(modelloXsdUdRowBean.getDsXsd());
        modelliXsdUd.setFlDefault(modelloXsdUdRowBean.getFlDefault());

        helper.getEntityManager().flush();
        // log evento
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_UD,
                modelloXsdUdRowBean.getIdModelloXsdUd(), param.getNomePagina());
    }

    /**
     * Cancellazione modello xsd
     *
     * @param param
     *            log param
     * @param modelloXsdUdRowBean
     *            row bean
     *
     * @throws ParerUserError
     *             eccezione generica
     */
    public void deleteDecModelloXsdUd(LogParam param, DecModelloXsdUdRowBean modelloXsdUdRowBean)
            throws ParerUserError {
        logger.debug("Eseguo l'eliminazione di un modello xsd");
        try {
            DecModelloXsdUd modelliXsdUd = helper.findById(DecModelloXsdUd.class,
                    modelloXsdUdRowBean.getIdModelloXsdUd());
            // log evento
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_UD,
                    modelloXsdUdRowBean.getIdModelloXsdUd(), param.getNomePagina());
            helper.removeEntity(modelliXsdUd, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione del modello xsd ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }

    }

    /**
     * Verifica se il modello in uso nel tipo UNI_DOC
     *
     * @param idAmbiente
     *            id ambiente
     * @param idModelloXsdUd
     *            id modello xsd
     * @param tiModelloXsd
     *            tipo modello
     * @param tiUsoModelloXsd
     *            tipo uso modello
     *
     * @return true se esiste modello standard previsto per l'ambiente
     */
    public boolean existAnotherDecModelloXsdStd(BigDecimal idAmbiente, BigDecimal idModelloXsdUd, String tiModelloXsd,
            String tiUsoModelloXsd) {
        List<DecModelloXsdUd> result = helper.retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd(idAmbiente, tiModelloXsd,
                tiUsoModelloXsd, CostantiDB.Flag.TRUE, false);
        return result.stream()
                .filter(m -> idModelloXsdUd != null && m.getIdModelloXsdUd().longValue() != idModelloXsdUd.longValue()
                        || idModelloXsdUd == null)
                .count() != 0;
    }

    /**
     * Verifica se il modello in uso nel tipo UNI_DOC
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello
     * @param tiUsoModelloXsd
     *            tipo uso modello
     *
     * @return true se esiste modello standard previsto per l'ambiente
     *
     */
    public Optional<DecModelloXsdUdRowBean> getDefaultDecModelloXsdUd(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd) {
        List<DecModelloXsdUd> result = helper.retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd(idAmbiente, tiModelloXsd,
                tiUsoModelloXsd, CostantiDB.Flag.TRUE, false);
        // only one element (more than one not possible or error!)
        if (result.isEmpty() || result.size() > 1) {
            return Optional.empty();
        }
        return Optional.of(modelloXsdUd2RowBean(Arrays.asList(result.get(0))));
    }
}
