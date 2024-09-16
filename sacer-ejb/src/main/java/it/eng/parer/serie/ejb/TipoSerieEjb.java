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

package it.eng.parer.serie.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.helper.TipoDocumentoHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecCampoInpUd;
import it.eng.parer.entity.DecCampoOutSelUd;
import it.eng.parer.entity.DecFiltroSelUd;
import it.eng.parer.entity.DecFiltroSelUdAttb;
import it.eng.parer.entity.DecFiltroSelUdDato;
import it.eng.parer.entity.DecModelloCampoInpUd;
import it.eng.parer.entity.DecModelloCampoOutSelUd;
import it.eng.parer.entity.DecModelloFiltroSelUdattb;
import it.eng.parer.entity.DecModelloFiltroTiDoc;
import it.eng.parer.entity.DecModelloOutSelUd;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecNotaModelloTipoSerie;
import it.eng.parer.entity.DecNotaTipoSerie;
import it.eng.parer.entity.DecOutSelUd;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoNotaSerie;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.entity.DecXsdAttribDatiSpec;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.helper.TipoSerieHelper;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecCampoInpUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecCampoInpUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecCampoInpUdTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecCampoOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecCampoOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbRowBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbTableBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecNotaTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecNotaTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.web.dto.DecFiltroSelUdAttbBean;
import it.eng.parer.web.dto.DecFiltroSelUdDatoBean;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class TipoSerieEjb {

    @Resource
    private SessionContext context;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private TipoSerieHelper tipoSerieHelper;
    @EJB
    private UnitaDocumentarieHelper udHelper;
    @EJB
    private RegistroHelper registroHelper;
    @EJB
    private TipoDocumentoHelper tipoDocHelper;
    @EJB
    private TipoUnitaDocHelper tipoUnitaDocHelper;
    @EJB
    private DatiSpecificiHelper datiSpecificiHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private SacerLogEjb sacerLogEjb;

    private static final Logger log = LoggerFactory.getLogger(TipoSerieEjb.class);

    public enum XsdType {

        TIPO_DOC, TIPO_COMP_DOC, TIPO_UNITA_DOC
    }

    // FIXMEPLEASE: Correggere il metodo e le eccezioni
    public void insertDecTipoSerie(LogParam param, DecTipoSerieRowBean rowBean) throws ParerUserError {

        OrgStrut struttura = struttureHelper.findById(OrgStrut.class, rowBean.getIdStrut());
        if (struttura.getDecTipoSeries() == null) {
            struttura.setDecTipoSeries(new ArrayList<DecTipoSerie>());

        }
        DecTipoSerie decTipoSerie = tipoSerieHelper.getDecTipoSerieByName(rowBean.getNmTipoSerie(),
                struttura.getIdStrut());
        if (decTipoSerie != null) {

            throw new ParerUserError("Tipologia di serie gi\u00E0 esistente all'interno della struttura</br>");

        }
        DecTipoSerie row = (DecTipoSerie) Transform.rowBean2Entity(rowBean);
        row.setOrgStrut(struttura);
        tipoSerieHelper.insertEntity(row, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, new BigDecimal(row.getIdTipoSerie()),
                param.getNomePagina());

        struttura.getDecTipoSeries().add(row);
    }

    public void updateDecTipoSerie(LogParam param, BigDecimal idTipoSerie, DecTipoSerieRowBean rowBean,
            boolean campiModificati) throws ParerUserError {
        try {
            DecTipoSerie tipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie);
            tipoSerie.setAaIniCreaAutom(rowBean.getAaIniCreaAutom());
            tipoSerie.setAaFinCreaAutom(rowBean.getAaFinCreaAutom());
            tipoSerie.setCdSerieDefault(rowBean.getCdSerieDefault());
            tipoSerie.setDsSerieDefault(rowBean.getDsSerieDefault());
            tipoSerie.setDsTipoSerie(rowBean.getDsTipoSerie());
            tipoSerie.setDtIstituz(rowBean.getDtIstituz());
            tipoSerie.setDtSoppres(rowBean.getDtSoppres());
            tipoSerie.setFlControlloConsistObblig(rowBean.getFlControlloConsistObblig());
            tipoSerie.setFlCreaAutom(rowBean.getFlCreaAutom());
            tipoSerie.setFlTipoSeriePadre(rowBean.getFlTipoSeriePadre());
            tipoSerie.setGgCreaAutom(rowBean.getGgCreaAutom());
            tipoSerie.setNiAaSelUd(rowBean.getNiAaSelUd());
            tipoSerie.setNiAaSelUdSuc(rowBean.getNiAaSelUdSuc());
            tipoSerie.setNiAnniConserv(rowBean.getNiAnniConserv());
            tipoSerie.setNiMmCreaAutom(rowBean.getNiMmCreaAutom());
            tipoSerie.setNiUnitaDocVolume(rowBean.getNiUnitaDocVolume());
            tipoSerie.setNmTipoSerie(rowBean.getNmTipoSerie());
            tipoSerie.setTiConservazioneSerie(rowBean.getTiConservazioneSerie());
            tipoSerie.setTipoContenSerie(rowBean.getTipoContenSerie());
            tipoSerie.setTiSelUd(rowBean.getTiSelUd());
            tipoSerie.setTiStatoVerSerieAutom(rowBean.getTiStatoVerSerieAutom());
            if (rowBean.getIdTipoSeriePadre() != null) {
                DecTipoSerie decTipoSeriePadre = tipoSerieHelper.findById(DecTipoSerie.class,
                        rowBean.getIdTipoSeriePadre());
                tipoSerie.setDecTipoSeriePadre(decTipoSeriePadre);
            }
            // Alzo il flag solo se ho modificato almeno uno dei campi interessati
            if (campiModificati) {
                // Setto a true il flag fl_tipo_serie_upd
                serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
            }
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ParerUserError("Errore durante il salvataggio del tipo serie");
        }
    }

    public DecTipoSerieRowBean getDecTipoSerieRowBeanByNameAndIdStrut(String name, Long idsStrut)
            throws ParerUserError {

        DecTipoSerieRowBean rowBean = null;
        try {
            DecTipoSerie row = tipoSerieHelper.getDecTipoSerieByName(name, idsStrut);
            if (row != null) {
                rowBean = (DecTipoSerieRowBean) Transform.entity2RowBean(row);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error(ex.getMessage(), ex);
            throw new ParerUserError("Errore inaspettato nel recupero della tipologia serie per nome e struttura<br/>");
        }
        return rowBean;
    }

    public boolean isTipoSerieUsed(BigDecimal idTipoSerie, int numOfUses) {
        boolean result = false;

        result = tipoSerieHelper.checkIsTipoSerieUsed(idTipoSerie, numOfUses);

        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecTipoSerie(LogParam param, long idTipoSerie) throws ParerUserError {
        List<SerSerie> listaSerie = tipoSerieHelper.getSeriePerTipoSerie(new BigDecimal(idTipoSerie));
        if (listaSerie != null) {
            if (!listaSerie.isEmpty() && listaSerie.size() >= 1) {
                throw new ParerUserError(
                        "La tipologia \u00E8 gi\u00E0 utilizzata in una serie : eliminazione non consentita<br/>");
            } else {
                DecTipoSerie tipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie);
                String nmTipoSerie = tipoSerie.getNmTipoSerie();
                long idStrut = tipoSerie.getOrgStrut().getIdStrut();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                        new BigDecimal(tipoSerie.getIdTipoSerie()), param.getNomePagina());
                tipoSerieHelper.removeEntity(tipoSerie, true);
                log.info("Cancellazione tipo serie " + nmTipoSerie + " della struttura " + idStrut
                        + " avvenuta con successo!");
            }
        }
    }

    public void deleteDecTipoSerieUd(LogParam param, DecTipoSerieUdRowBean tipoSerieUdRowBean, BigDecimal idTipoSerie)
            throws ParerUserError {
        try {
            DecTipoSerieUd decTipoSerie = tipoSerieHelper.getDecTipoSerieUd(tipoSerieUdRowBean.getIdTipoSerie(),
                    tipoSerieUdRowBean.getIdRegistroUnitaDoc(), tipoSerieUdRowBean.getIdTipoUnitaDoc()).get(0);
            tipoSerieHelper.removeEntity(decTipoSerie, true);
            // Gestione fl_tipo_serie_mult in DecRegistroUnitaDoc
            if (!tipoSerieHelper.multipleDecRegistroUnitaDocInTipiSerie(tipoSerieUdRowBean.getIdRegistroUnitaDoc())) {
                DecRegistroUnitaDoc registroUnitaDoc = tipoSerieHelper.findById(DecRegistroUnitaDoc.class,
                        tipoSerieUdRowBean.getIdRegistroUnitaDoc());
                // Prendo il primo registro dalla lista risultato, sarà sempre quello
                registroUnitaDoc.setFlTipoSerieMult("0");
            }
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError(
                    "Errore durante la cancellazione di un'associazione registro-tipo ud del tipo serie");
        }
    }

    public DecRegistroUnitaDocTableBean getDecRegistroUnitaDocTableBeanForSerieByIdStrut(BigDecimal idStrut) {

        DecRegistroUnitaDocTableBean registroUnitaDocTableBean = new DecRegistroUnitaDocTableBean();

        List<DecRegistroUnitaDoc> list = tipoSerieHelper
                .getDecRegistroUnitaDocListPerSerieByIdStrut(idStrut.longValue());

        try {
            if (!list.isEmpty()) {
                for (DecRegistroUnitaDoc registro : list) {
                    DecRegistroUnitaDocRowBean registroRow = (DecRegistroUnitaDocRowBean) Transform
                            .entity2RowBean(registro);
                    if (registro.getDtIstituz().before(new Date()) && registro.getDtSoppres().after(new Date())) {
                        registroRow.setObject("fl_attivo", "1");
                    } else {
                        registroRow.setObject("fl_attivo", "0");
                    }
                    // List<BigDecimal> idRegistro = new ArrayList<>();
                    // idRegistro.add(registroRow.getIdRegistroUnitaDoc());
                    // List<DecVLisTiUniDocAms> tipoUnitaDocAssociati =
                    // tipoSerieHelper.getDecVLisTiUniDocAmsByStrutByRegistriList(idRegistro);
                    //
                    // StringBuilder nm_tipo_unita_doc = new StringBuilder();
                    // for (int index = 0; index < tipoUnitaDocAssociati.size(); index++) {
                    // DecVLisTiUniDocAms row = tipoUnitaDocAssociati.get(index);
                    // nm_tipo_unita_doc.append(row.getNmTipoUnitaDoc());
                    // if (index < (tipoUnitaDocAssociati.size() - 1)) {
                    // nm_tipo_unita_doc.append("; ");
                    // }
                    // }
                    //
                    // registroRow.setString("nm_tipo_unita_doc", nm_tipo_unita_doc.toString());
                    registroUnitaDocTableBean.add(registroRow);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }

        return registroUnitaDocTableBean;

    }

    public DecTipoSerieUdRowBean getDecTipoSerieUdRowBean(BigDecimal idTipoSerie, BigDecimal idRegistroNum,
            BigDecimal idTipoUnitaDocNum) {
        DecTipoSerieUdRowBean result = null;

        List<DecTipoSerieUd> decTipoSerieUdList = tipoSerieHelper.getDecTipoSerieUd(idTipoSerie, idRegistroNum,
                idTipoUnitaDocNum);

        if (decTipoSerieUdList != null && decTipoSerieUdList.size() > 0) {

            try {
                result = (DecTipoSerieUdRowBean) Transform.entity2RowBean(decTipoSerieUdList.get(0));
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getMessage(), ex);
            }

        }
        return result;
    }

    /**
     * Inserisce/modifica un record relativo all'associazione registro-tipoUd del Tipo Serie, ovvero nella tabella
     * DecTipoSerieUd
     *
     * @param param
     *            parametri per logging
     * @param idRegistro
     *            id registro
     * @param idTipoUnitaDocPerRegistro
     *            tipo unita doc per registro
     * @param idTipoSerie
     *            tipo serie
     * @param flSelUnitaDocAnnul
     *            flag 1/0 (true/false)
     * @param idTipoSerieUdDaMod
     *            id tipo esrie
     *
     * @throws ParerUserError
     *             errore generico
     *
     * @return Long pk
     */
    public Long insertRegistroTipoUnitaDocTipoSerie(LogParam param, String idRegistro,
            Set<String> idTipoUnitaDocPerRegistro, BigDecimal idTipoSerie, String flSelUnitaDocAnnul,
            BigDecimal idTipoSerieUdDaMod) throws ParerUserError {
        try {
            DecTipoSerieUd tipoSerieUd = null;
            BigDecimal idRegistroNum = new BigDecimal(idRegistro);
            DecTipoSerie tipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie.longValue());
            DecRegistroUnitaDoc decRegistroUnitaDoc = struttureHelper.findById(DecRegistroUnitaDoc.class,
                    idRegistroNum);
            if (idTipoSerieUdDaMod == null) {
                for (String idTipoUnitaDoc : idTipoUnitaDocPerRegistro) {
                    BigDecimal idTipoUnitaDocNum = new BigDecimal(idTipoUnitaDoc);
                    DecTipoUnitaDoc decTipoUnitaDoc = struttureHelper.findById(DecTipoUnitaDoc.class,
                            idTipoUnitaDocNum);
                    tipoSerieUd = new DecTipoSerieUd();
                    tipoSerieUd.setDecTipoSerie(tipoSerie);
                    tipoSerieUd.setDecRegistroUnitaDoc(decRegistroUnitaDoc);
                    tipoSerieUd.setDecTipoUnitaDoc(decTipoUnitaDoc);
                    tipoSerieUd.setFlSelUnitaDocAnnul(flSelUnitaDocAnnul);
                    tipoSerieHelper.insertEntity(tipoSerieUd, true);
                }
            } else {
                tipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUdDaMod);
                tipoSerieUd.setFlSelUnitaDocAnnul(flSelUnitaDocAnnul);
            }

            // Se sul tipo serie CORRENTE esiste più di un'associazione per quel registro,
            // setto a 1 il fl_tipo_serie_mult in DecRegistroUnitaDoc
            // if (existDecTipoSerieUdForRegistroAndTipoSerie(idRegistroNum, idTipoSerie)) {
            // decRegistroUnitaDoc.setFlTipoSerieMult("1");
            // }
            if (tipoSerieHelper.multipleDecRegistroUnitaDocInTipiSerie(idRegistroNum)) {
                decRegistroUnitaDoc.setFlTipoSerieMult("1");
            }

            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                    new BigDecimal(tipoSerieUd.getDecTipoSerie().getIdTipoSerie()), param.getNomePagina());
            return tipoSerieUd.getIdTipoSerieUd();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new ParerUserError("Errore durante la modifica dell'associazione registro-tipo ud del tipo serie");
        }
    }

    public DecFiltroSelUdTableBean getDecFiltroSelUdTableBean(BigDecimal idTipoSerie) {

        DecFiltroSelUdTableBean result = new DecFiltroSelUdTableBean();
        List<DecFiltroSelUd> lista = tipoSerieHelper.getDecFiltroSelUdList(idTipoSerie.longValue());
        if (lista != null && lista.size() > 0) {

            try {
                for (DecFiltroSelUd filtro : lista) {
                    DecFiltroSelUdRowBean row = (DecFiltroSelUdRowBean) Transform.entity2RowBean(filtro);
                    DecTipoDoc decTipoDoc = filtro.getDecTipoDoc();
                    if (decTipoDoc != null) {
                        row.setString("nm_tipo_doc", decTipoDoc.getNmTipoDoc());

                    }
                    result.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }

        }
        return result;

    }

    public DecFiltroSelUdTableBean getDecFiltroSelUdTableBean(BigDecimal idTipoSerie,
            CostantiDB.TipoFiltroSerieUd tiFiltro) {
        DecFiltroSelUdTableBean result = new DecFiltroSelUdTableBean();
        List<DecFiltroSelUd> lista = tipoSerieHelper.getDecFiltroSelUdList(idTipoSerie, tiFiltro);
        if (lista != null && lista.size() > 0) {
            try {
                for (DecFiltroSelUd filtro : lista) {
                    DecFiltroSelUdRowBean row = (DecFiltroSelUdRowBean) Transform.entity2RowBean(filtro);
                    DecTipoDoc decTipoDoc = filtro.getDecTipoDoc();
                    if (decTipoDoc != null) {
                        row.setString("nm_tipo_doc", decTipoDoc.getNmTipoDoc());
                    }
                    result.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Ricavo le regole di filtraggio di una determinata associazione "Registro-TipoUd" ed oltre a restituire il
     * tableBean relativo con i dati visualizzati in un'unica riga, creo anche un'apposita mappa contenente: K =
     * idTipoDoc; V = un'altra mappa (K = nmTipoDoc, V = idFiltroSelUd);
     *
     * N.B.: in caso di gestione di un ulteriore filtro oltre a TIPO_DOC_PRINC bisognerà aggiungere la gestione con un
     * nuovo "case"
     *
     * @param idTipoSerie
     *            id tipo serie
     *
     * @return array di tipo Object[]
     */
    public Object[] getDecFiltroSelUdAndMappaFiltri(BigDecimal idTipoSerie) {
        // Preparo gli oggetti da popolare
        Object[] risultato = new Object[2];
        DecFiltroSelUdTableBean result = new DecFiltroSelUdTableBean();
        Map<BigDecimal, Map<String, BigDecimal>> filtroMap = new HashMap<>();
        risultato[0] = result;
        risultato[1] = filtroMap;

        // Ricavo la lista delle regole di filtraggio
        List<DecFiltroSelUd> lista = tipoSerieHelper.getDecFiltroSelUdList(idTipoSerie.longValue());
        if (lista != null && lista.size() > 0) {
            try {
                // Costruisco l'unica riga da esporre
                DecFiltroSelUdRowBean row = new DecFiltroSelUdRowBean();
                for (DecFiltroSelUd filtro : lista) {
                    switch (filtro.getTiFiltro()) {
                    case "TIPO_DOC_PRINC":
                        // Tipo filtro in questo caso sarà sempre TIPO_DOC_PRINC
                        row.setTiFiltro(filtro.getTiFiltro());
                        // Costruisco la mappa (K=nm_tipo_doc, V=id_filtro_sel_ud)
                        DecTipoDoc decTipoDoc = filtro.getDecTipoDoc();
                        if (decTipoDoc != null) {
                            String nmTipoDocSeparatiDaVirgole = "";
                            String idTipoDocSeparatiDaVirgole = "";

                            // Aggiungo il filtro alla mappa
                            Map<String, BigDecimal> mappaInterna = new HashMap<>();
                            mappaInterna.put(filtro.getDecTipoDoc().getNmTipoDoc(),
                                    new BigDecimal(filtro.getIdFiltroSelUd()));
                            filtroMap.put(new BigDecimal(filtro.getDecTipoDoc().getIdTipoDoc()), mappaInterna);

                            // "Gestisco" il campo multiselect
                            if (row.getString("nm_tipo_doc_concatenati") != null) {
                                nmTipoDocSeparatiDaVirgole = row.getString("nm_tipo_doc_concatenati") + "; "
                                        + decTipoDoc.getNmTipoDoc();
                                idTipoDocSeparatiDaVirgole = row.getString("id_tipo_doc_concatenati") + ";"
                                        + decTipoDoc.getIdTipoDoc();
                            } else {
                                nmTipoDocSeparatiDaVirgole = decTipoDoc.getNmTipoDoc();
                                idTipoDocSeparatiDaVirgole = "" + decTipoDoc.getIdTipoDoc();
                            }
                            row.setString("nm_tipo_doc_concatenati", nmTipoDocSeparatiDaVirgole);
                            row.setString("id_tipo_doc_concatenati", idTipoDocSeparatiDaVirgole);
                        }
                        break;
                    }
                }

                result.add(row);
                risultato[0] = result;
                risultato[1] = filtroMap;
            } catch (Exception ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return risultato;
    }

    public DecTipoSerieTableBean getDecTipoSerieTableBean(BigDecimal idStrut, boolean filterValid) {
        DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
        List<DecTipoSerie> listaDecTipoSerie = tipoSerieHelper.retrieveDecTipoSerieList(idStrut, filterValid);
        if (listaDecTipoSerie != null && !listaDecTipoSerie.isEmpty()) {
            try {
                for (DecTipoSerie decTipoSerie : listaDecTipoSerie) {
                    DecTipoSerieRowBean row = (DecTipoSerieRowBean) Transform.entity2RowBean(decTipoSerie);
                    if (decTipoSerie.getDecTipoSeriePadre() != null) {
                        row.setString("nm_serie_padre   ", decTipoSerie.getDecTipoSeriePadre().getNmTipoSerie());
                    }
                    if (decTipoSerie.getNiAnniConserv() != null) {
                        if (decTipoSerie.getNiAnniConserv().compareTo(new BigDecimal("9999")) == 0) {
                            row.setString("ni_anni_conserv_string", "Illimitata");
                        } else {
                            row.setString("ni_anni_conserv_string", decTipoSerie.getNiAnniConserv().toString());
                        }
                    }
                    row.setString("nm_modello_tipo_serie", decTipoSerie.getDecModelloTipoSerie() != null
                            ? decTipoSerie.getDecModelloTipoSerie().getNmModelloTipoSerie() : "");
                    tableBean.add(row);
                }
            } catch (Exception ex) {
                log.error("Errore durante il recupero dei tipi serie " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tableBean;
    }

    public DecTipoSerieTableBean getDecTipoSerieTableBean(long idUser, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String isAttivo, String tipiSerieNoGenModello, BigDecimal idModelloTipoSerie) {
        DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
        List<DecTipoSerie> listaDecTipoSerie = tipoSerieHelper.getDecTipoSerie(idUser, idAmbiente, idEnte, idStrut,
                isAttivo, tipiSerieNoGenModello, idModelloTipoSerie);
        if (listaDecTipoSerie != null && !listaDecTipoSerie.isEmpty()) {
            tableBean = formattaDecTipoSerieTableBean(listaDecTipoSerie);
        }
        return tableBean;
    }

    public DecTipoSerieTableBean getDecTipoSeriePadrePerStrutturaTableBean(BigDecimal idStrut) {
        DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
        List<DecTipoSerie> retrieveDecTipoSerieList = tipoSerieHelper.retrieveDecTipoSerieList(idStrut, true, null,
                false);
        if (retrieveDecTipoSerieList != null && !retrieveDecTipoSerieList.isEmpty()) {
            try {
                tableBean = (DecTipoSerieTableBean) Transform.entities2TableBean(retrieveDecTipoSerieList);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero dei tipi serie padre " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);

            }
        }
        return tableBean;
    }

    public DecTipoSerieTableBean getDecTipoSerieDaCreareTableBean(BigDecimal idStrut, String tipoContenSerie,
            boolean filterValids) {
        DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
        List<DecTipoSerie> retrieveDecTipoSerieList = tipoSerieHelper.retrieveDecTipoSerieList(idStrut, false,
                tipoContenSerie, filterValids);
        if (retrieveDecTipoSerieList != null && !retrieveDecTipoSerieList.isEmpty()) {
            try {
                tableBean = (DecTipoSerieTableBean) Transform.entities2TableBean(retrieveDecTipoSerieList);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero dei tipi serie padre " + ExceptionUtils.getRootCauseMessage(ex),
                        ex);

            }
        }
        return tableBean;
    }

    private DecTipoSerieTableBean formattaDecTipoSerieTableBean(List<DecTipoSerie> listaDecTipoSerie) {
        DecTipoSerieTableBean tableBean = new DecTipoSerieTableBean();
        try {
            for (DecTipoSerie decTipoSerie : listaDecTipoSerie) {
                DecTipoSerieRowBean row = (DecTipoSerieRowBean) Transform.entity2RowBean(decTipoSerie);
                if (decTipoSerie.getDecTipoSeriePadre() != null) {
                    row.setString("nm_serie_padre", decTipoSerie.getDecTipoSeriePadre().getNmTipoSerie());
                }
                if (decTipoSerie.getNiAnniConserv() != null) {
                    if (decTipoSerie.getNiAnniConserv().compareTo(new BigDecimal("9999")) == 0) {
                        row.setString("ni_anni_conserv_string", "Illimitata");
                    } else {
                        row.setString("ni_anni_conserv_string", decTipoSerie.getNiAnniConserv().toString());
                    }
                }
                row.setString("nm_modello_tipo_serie", decTipoSerie.getDecModelloTipoSerie() != null
                        ? decTipoSerie.getDecModelloTipoSerie().getNmModelloTipoSerie() : "");
                tableBean.add(row);
            }
        } catch (Exception ex) {
            log.error("Errore durante il recupero dei tipi serie " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public DecTipoSerieRowBean getDecTipoSerieRowBean(BigDecimal idTipoSerie) {
        DecTipoSerieRowBean result = null;
        DecTipoSerie decTipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie);
        try {
            result = (DecTipoSerieRowBean) Transform.entity2RowBean(decTipoSerie);
            if (decTipoSerie.getDecTipoSeriePadre() != null) {
                result.setString("nm_serie_padre", decTipoSerie.getDecTipoSeriePadre().getNmTipoSerie());
            }
            if (decTipoSerie.getDecModelloTipoSerie() != null) {
                result.setString("nm_modello_tipo_serie",
                        decTipoSerie.getDecModelloTipoSerie().getNmModelloTipoSerie());
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero dei tipi serie " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }

        return result;
    }

    public Map<String, DecCampoInpUdTableBean> getMappaRegoleAcquisizionePerTipoSerie(BigDecimal idTipoSerie) {
        DecCampoInpUdTableBean decCampoInpUdTableBean;
        Map<String, DecCampoInpUdTableBean> mappa = new HashMap<>();
        CostantiDB.TipoCampo[] tipiCampo = CostantiDB.TipoCampo.getCampiOutSelUd();
        for (CostantiDB.TipoCampo tipoCampo : tipiCampo) {
            decCampoInpUdTableBean = getDecCampoInpUdTableBeanPerDecTipoSerie(idTipoSerie, tipoCampo.name());
            mappa.put(tipoCampo.name(), decCampoInpUdTableBean);
        }

        return mappa;

    }

    public DecCampoInpUdTableBean getRegoleAcquisizionePerTipoSerie(BigDecimal idTipoSerie) {
        DecCampoInpUdTableBean decCampoInpUdTableBean = new DecCampoInpUdTableBean();
        Map<String, DecCampoInpUdTableBean> mappa = getMappaRegoleAcquisizionePerTipoSerie(idTipoSerie);
        if (mappa != null && !mappa.isEmpty()) {

            Collection<DecCampoInpUdTableBean> values = mappa.values();
            for (DecCampoInpUdTableBean table : values) {
                for (DecCampoInpUdRowBean row : table) {
                    decCampoInpUdTableBean.add(row);
                }
            }
        }

        if (!decCampoInpUdTableBean.isEmpty()) {
            decCampoInpUdTableBean.addSortingRule("pg_ord_campo", SortingRule.ASC);
            decCampoInpUdTableBean.sort();
        }
        return decCampoInpUdTableBean;

    }

    public DecRegistroUnitaDocTableBean getRegistroUnitaDocTableBeanPerTipoSerie(BigDecimal idTipoSerie) {
        DecRegistroUnitaDocTableBean tableBean = new DecRegistroUnitaDocTableBean();
        StringBuilder value = null;
        List<DecRegistroUnitaDoc> listaDecRegistroUnitaDoc = tipoSerieHelper
                .getDecRegistroUnitaDocPerTipoSerie(idTipoSerie);
        if (listaDecRegistroUnitaDoc != null && !listaDecRegistroUnitaDoc.isEmpty()) {
            try {
                for (DecRegistroUnitaDoc registro : listaDecRegistroUnitaDoc) {
                    value = new StringBuilder(registro.getCdRegistroUnitaDoc()).append(" ")
                            .append(registro.getDsRegistroUnitaDoc());

                    DecRegistroUnitaDocRowBean rowBean = (DecRegistroUnitaDocRowBean) Transform
                            .entity2RowBean(registro);
                    rowBean.setObject("registro", value);
                    DecTipoUnitaDoc decTipoUnitaDoc = tipoSerieHelper
                            .getDecTipoUnitaDocByIdRegistroIdSerie(registro.getIdRegistroUnitaDoc(), idTipoSerie);
                    value = new StringBuilder(decTipoUnitaDoc.getNmTipoUnitaDoc()).append(" ")
                            .append(decTipoUnitaDoc.getDsTipoUnitaDoc());
                    rowBean.setObject("tipo_unita_doc", value);
                }
                tableBean = (DecRegistroUnitaDocTableBean) Transform.entities2TableBean(listaDecRegistroUnitaDoc);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero dei registri per tipi serie "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return tableBean;
    }

    public DecTipoSerieUdTableBean getDecTipoSerieUdTableBean(BigDecimal idTipoSerie) {
        DecTipoSerieUdTableBean result = new DecTipoSerieUdTableBean();
        List<DecTipoSerieUd> lista = tipoSerieHelper.getDecTipoSerieUd(idTipoSerie, null, null);
        if (lista != null && lista.size() > 0) {
            try {
                result = (DecTipoSerieUdTableBean) Transform.entities2TableBean(lista);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return result;
    }

    public DecTipoDocTableBean getDocumentiPrincipaliPerTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        List<DecTipoDoc> tipiDoc = tipoSerieHelper.getDecTipoDocPrincipalePerTipoUnitaDoc(idTipoUnitaDoc);
        DecTipoDocTableBean tableBean = new DecTipoDocTableBean();
        try {
            if (tipiDoc != null && !tipiDoc.isEmpty()) {
                tableBean = (DecTipoDocTableBean) Transform.entities2TableBean(tipiDoc);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero dei documenti Principali per TipoUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    /**
     * "Nuovo" metodo di inserimento regola di filtraggio: scorro la mappa passatami come parametro. Essendo un
     * inserimento, il valore di idFiltroSelUd sarà null (in quanto il record ancora non esiste).
     *
     * @param param
     *            parametri per il logging
     * @param rowBean
     *            il rowBean "particolare" della regola di filtraggio contenente gli unici elementi univoci vale a dire
     *            il tipoFiltro e l'idTipoSerieUD (associazione registro/tipoUd)
     * @param mappa
     *            contiene i dati del filtri che sto per inserire. K = idTipoDoc V = (K = nmTipoDoc, V = idFiltroSelUd)
     *            idFiltroSelUd in inserimento sarà nullo
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void insertRegolaDiFiltraggio2(LogParam param, DecFiltroSelUdRowBean rowBean,
            Map<BigDecimal, Map<String, BigDecimal>> mappa) throws ParerUserError {
        try {
            // Scorro la mappa con gli elementi da inserire
            Iterator it = mappa.entrySet().iterator();
            DecTipoSerieUd decTipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, rowBean.getIdTipoSerieUd());
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                BigDecimal idTipoDocPrinc = (BigDecimal) entry.getKey();
                // Creo l'entity da persistere
                DecFiltroSelUd entity = new DecFiltroSelUd();
                entity.setTiFiltro(rowBean.getTiFiltro());
                entity.setPgFiltro(BigDecimal.ONE);
                BigDecimal idTipoSerieUd = rowBean.getIdTipoSerieUd();
                decTipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUd);
                entity.setDecTipoSerieUd(decTipoSerieUd);
                CostantiDB.TipoFiltroSerieUd tiFiltro = CostantiDB.TipoFiltroSerieUd.byName(entity.getTiFiltro());
                BigDecimal maxPgPerTipoFiltro = tipoSerieHelper.getMaxPgPerTipoFiltro(tiFiltro, idTipoSerieUd);
                DecTipoDoc decTipoDocById = struttureHelper.findById(DecTipoDoc.class, idTipoDocPrinc);
                entity.setDecTipoDoc(decTipoDocById);
                maxPgPerTipoFiltro = maxPgPerTipoFiltro.add(BigDecimal.ONE, MathContext.UNLIMITED);
                entity.setPgFiltro(maxPgPerTipoFiltro);
                rowBean.setPgFiltro(maxPgPerTipoFiltro);
                // Persisto
                tipoSerieHelper.insertEntity(entity, true);
                // Aggiorno la mappa aggiungendo ora l'idFiltroSelUd del record appena creato!
                Map<String, BigDecimal> mappaInterna = (Map<String, BigDecimal>) entry.getValue();
                // "Scorro" l'unico valore presente
                for (Map.Entry entry2 : mappaInterna.entrySet()) {
                    entry2.setValue(new BigDecimal(entity.getIdFiltroSelUd()));
                }
            }
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                    new BigDecimal(decTipoSerieUd.getDecTipoSerie().getIdTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError(
                    "Errore durante l'inserimento di una regola di filtraggio del tipo serie del tipo serie");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateRegolaDiFiltraggio(LogParam param, DecFiltroSelUdRowBean decFiltroSelUdRowBean,
            DecOutSelUdTableBean decOutSelUdTableBean, BigDecimal idTipoSerie,
            Map<BigDecimal, Map<String, BigDecimal>> mappa) throws ParerUserError {
        try {
            /*
             * L'aggiornamento è diviso in CANCELLAZIONE DEGLI ELEMENTI ELIMINATI ed INSERIMENTO DEGLI ELEMENTI NUOVI.
             * Divido quindi la mappa in due sottomappe, una da dare in pasto alla cancellazione, l'altra da dare in
             * pasto all'inserimento
             */
            Map<BigDecimal, Map<String, BigDecimal>> mappaCancellazione = new HashMap<>();
            Map<BigDecimal, Map<String, BigDecimal>> mappaInserimento = new HashMap<>();
            Map<BigDecimal, Map<String, BigDecimal>> mappaPresentiDB = new HashMap<>();
            // Ricavo i filtri già salvati per questa associazione registro-tipoUd presenti su DB
            DecFiltroSelUdTableBean filtriSelUdTB = getDecFiltroSelUdTableBean(
                    decFiltroSelUdRowBean.getIdTipoSerieUd());
            // Creo la mappa dei filtri salvati su DB
            for (DecFiltroSelUdRowBean filtriSelUdRB : filtriSelUdTB) {
                Map<String, BigDecimal> mappaInternaDB = new HashMap<>();
                mappaInternaDB.put(filtriSelUdRB.getString("nm_tipo_doc"), filtriSelUdRB.getIdFiltroSelUd());
                mappaPresentiDB.put(filtriSelUdRB.getIdTipoDocPrinc(), mappaInternaDB);
            }

            ///////////////////
            // CANCELLAZIONE //
            ///////////////////
            /*
             * Controllo se la mia mappa online contiene ancora i valori presenti su DB Se non li contiene, significa
             * che sono stati eleminati, e dunque vanno eliminati da DB
             */
            Iterator<Entry<BigDecimal, Map<String, BigDecimal>>> itDB = mappaPresentiDB.entrySet().iterator();
            while (itDB.hasNext()) {
                Map.Entry entryDB = (Map.Entry) itDB.next();
                // Se la coppia nmTipoDoc-idFiltroSelUd non è più presente online...
                if (!mappa.containsValue((Map<String, BigDecimal>) entryDB.getValue())) {
                    // va messa da cancellare!
                    mappaCancellazione.put((BigDecimal) entryDB.getKey(), (Map<String, BigDecimal>) entryDB.getValue());
                }
            }
            // Passa FALSE per evitare di fare un log del tipo serie
            deleteDecFiltroSelUd2(param, decFiltroSelUdRowBean, decOutSelUdTableBean, idTipoSerie, mappaCancellazione,
                    false);

            /////////////////
            // INSERIMENTO //
            /////////////////
            // Ricavo gli elementi della mappa con idFiltroSelUd nullo
            Iterator it = mappa.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                BigDecimal idTipoDoc = (BigDecimal) entry.getKey();
                Map<String, BigDecimal> mappaInterna = (Map<String, BigDecimal>) entry.getValue();
                Iterator it2 = mappaInterna.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry entry2 = (Map.Entry) it2.next();
                    BigDecimal idFiltroSelUd = (BigDecimal) entry2.getValue();
                    if (idFiltroSelUd == null) {
                        mappaInserimento.put(idTipoDoc, mappaInterna);
                    }
                }
            }
            insertRegolaDiFiltraggio2(param, decFiltroSelUdRowBean, mappaInserimento);

            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
        } catch (ParerUserError ex) {
            log.error(ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante la modifica della regola di filtraggio del tipo tipo serie");
        }
    }

    /**
     * "Nuovo" metodo di eliminazione dell'unico record della lista "Regole di filtraggio" relativo al tipo filtro
     * "TIPO_DOC_PRINC". Viene eliminato il record dalla relativa tabella su DB (DEC_FILTRO_SEL_UD) e dei record aventi
     * i dati specifici in caso il filtro sia di TIPO_DOC_PRINC dalle tabelle su DB (DEC_FILTRO_SEL_UD_ATTB e
     * DEC_CAMPO_OUT_SEL_UD)
     *
     * @param param
     *            parametri per il logging
     * @param filtroSelUdRowBean
     *            l'unico record filtroSelUdRowBean
     * @param decOutSelUdTableBean
     *            il tableBean relativo alle regole di rappresentazione, coinvolte nel processo di cancellazione delle
     *            regole di filtraggio.
     * @param idTipoSerie
     *            id tipo serie
     * @param mappa
     *            contiene i dati del filtri che sto per cancellare K = idTipoDoc V = (K = nmTipoDoc, V =
     *            idFiltroSelUd).
     * @param effettuaLogging
     *            true/false
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFiltroSelUd2(LogParam param, DecFiltroSelUdRowBean filtroSelUdRowBean,
            DecOutSelUdTableBean decOutSelUdTableBean, BigDecimal idTipoSerie,
            Map<BigDecimal, Map<String, BigDecimal>> mappa, boolean effettuaLogging) throws ParerUserError {
        try {
            // Ricavo i dati unici dal rowBean, ovvero il tipoFiltro e l'id dell'associazione registro/tipoUd
            String tipoFiltro = filtroSelUdRowBean.getTiFiltro();
            BigDecimal idTipoSerieUd = filtroSelUdRowBean.getIdTipoSerieUd();

            // Scorro la mappa
            Iterator it = mappa.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                BigDecimal idTipoDoc = (BigDecimal) entry.getKey();
                Map<String, BigDecimal> mappaInterna = (Map<String, BigDecimal>) entry.getValue();

                // Scorro la mappa interna contenente nmTipoDoc e idFiltroSelUd
                Iterator it2 = mappaInterna.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry entry2 = (Map.Entry) it2.next();
                    BigDecimal idFiltroSelUd = (BigDecimal) entry2.getValue();
                    DecFiltroSelUd filtroSelUd = tipoSerieHelper.findById(DecFiltroSelUd.class, idFiltroSelUd);

                    tipoSerieHelper.removeEntity(filtroSelUd, true);

                    /* Se ho cancellato una regola di filtraggio inerente un TIPO_DOC_PRINC */
                    if (tipoFiltro.equals("TIPO_DOC_PRINC")) {
                        /* Elimino gli eventuali record dalle lista relativa ai FILTRI SU DATI SPECIFICI */
                        BigDecimal idTipoDocPrinc = idTipoDoc;
                        /* Recupero i dati specifici coinvolti */
                        DecAttribDatiSpecTableBean datiSpecTipoDocTB = udHelper
                                .getDecAttribDatiSpecTableBean(idTipoDocPrinc, Constants.TipoEntitaSacer.DOC);
                        List<String> nmAttribDatiSpecList = new ArrayList<>();
                        for (DecAttribDatiSpecRowBean datiSpecTipoDocRB : datiSpecTipoDocTB) {
                            nmAttribDatiSpecList.add(datiSpecTipoDocRB.getNmAttribDatiSpec());
                        }
                        /* Se sono presenti, cancello i dati specifici interessati */
                        if (!nmAttribDatiSpecList.isEmpty()) {
                            tipoSerieHelper.deleteDecFiltroSelUdAttbByIdTipoSerieENmAttribDatiSpecList(idTipoSerieUd,
                                    nmAttribDatiSpecList);
                        }

                        /*
                         * "Azzero" su DB i valori degli eventuali record dei dati specifici presenti nelle REGOLE DI
                         * RAPPRESENTAZIONE
                         */
                        for (DecOutSelUdRowBean decOutSelUdRowBean : decOutSelUdTableBean) {
                            tipoSerieHelper.deleteDecCampoOutSelUdsForUpdateDaRegoleFiltraggio(
                                    decOutSelUdRowBean.getIdOutSelUd(), CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC);
                        }
                    }
                }
            }
            tipoSerieHelper.getEntityManager().flush();
            if (effettuaLogging) {
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                        param.getNomePagina());
            }
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore durante la cancellazione della regola di filtraggio");
        }
    }

    public DecFiltroSelUdAttbTableBean getDecFiltroSelUdAttbList(BigDecimal idTipoSerieUd) {
        List<DecFiltroSelUdAttb> tipiDoc = tipoSerieHelper.getDecFiltroSelUdAttbList(idTipoSerieUd);
        DecFiltroSelUdAttbTableBean tableBean = new DecFiltroSelUdAttbTableBean();
        try {
            if (tipiDoc != null && !tipiDoc.isEmpty()) {
                tableBean = (DecFiltroSelUdAttbTableBean) Transform.entities2TableBean(tipiDoc);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero dei documenti Principali per TipoUnitaDoc "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public void saveFiltrIDatiSpecTipoSerieUd(LogParam param, Long idTipoSerieUd,
            DecFiltroSelUdAttbTableBean filtroSelUdDatiSpecificiCompilati,
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine, BigDecimal idTipoSerie) throws ParerUserError {
        try {
            // tipoSerieHelper.saveFiltrIDatiSpecTipoSerieUd(idTipoSerieUd, filtroSelUdDatiSpecificiCompilati,
            // listaDatiSpecOnLine);
            // tipoSerieHelper.getEntityManager().flush();

            DecTipoSerieUd tipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUd);
            // Ricavo i dati spec già presenti su DB
            List<DecFiltroSelUdAttb> campiPreModifiche = tipoSerieUd.getDecFiltroSelUdAttbs();

            // Elimino i campi non più presenti
            for (DecFiltroSelUdAttb campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (DecFiltroSelUdAttbRowBean datoCompilato : filtroSelUdDatiSpecificiCompilati) {
                    if (campoPreModifica.getNmAttribDatiSpec().equals(datoCompilato.getNmAttribDatiSpec())) {
                        // Aggiorno cmq il valore. Il PersistenceContext capirà da solo se dovrà fare l'update o meno
                        campoPreModifica.setTiOper(datoCompilato.getTiOper());
                        campoPreModifica.setDlValore(datoCompilato.getDlValore());
                        tipoSerieHelper.insertEntity(campoPreModifica, false);
                        isPresente = true;
                        break;
                    }
                }
                if (!isPresente) {
                    // se non è più presente, lo cancello
                    tipoSerieHelper.removeEntity(campoPreModifica, false);
                }
            }

            for (DecFiltroSelUdAttbRowBean row : filtroSelUdDatiSpecificiCompilati) {
                if (row.getIdFiltroSelUdAttb().compareTo(BigDecimal.ZERO) == 0) {
                    // Creo una lista di DecFiltroSelUdDato che verrà passata all'istanza di DecFiltroSelUdAttb
                    List<DecFiltroSelUdDato> listDecFiltroSelUdDato = new ArrayList();
                    DecFiltroSelUdAttb filtro = new DecFiltroSelUdAttb();
                    filtro.setTiOper(row.getTiOper());
                    filtro.setDlValore(row.getDlValore());
                    filtro.setNmAttribDatiSpec(row.getNmAttribDatiSpec());
                    // ASSOCIAZIONE PADRE-FIGLIO
                    for (DecFiltroSelUdAttbBean datoSpecBean : listaDatiSpecOnLine) {
                        if (datoSpecBean.getNmAttribDatiSpec().equals(row.getNmAttribDatiSpec())) {
                            List<DecFiltroSelUdDatoBean> dcabList = datoSpecBean.getDecFiltroSelUdDatos();
                            for (DecFiltroSelUdDatoBean dcab : dcabList) {
                                // ASSOCIAZIONE FIGLIO-PADRE
                                DecFiltroSelUdDato decFiltroSelUdDato = new DecFiltroSelUdDato();
                                decFiltroSelUdDato.setTiEntitaSacer(dcab.getTiEntitaSacer());
                                decFiltroSelUdDato.setNmTipoUnitaDoc(dcab.getNmTipoUnitaDoc());
                                decFiltroSelUdDato.setNmTipoDoc(dcab.getNmTipoDoc());
                                String dsVers = dcab.getDsListaVersioniXsd();
                                dsVers = dsVers.substring(5);
                                decFiltroSelUdDato.setDsListaVersioniXsd(dsVers);

                                Long id = dcab.getIdAttribDatiSpec().longValue();
                                DecAttribDatiSpec dads = tipoSerieHelper.findById(DecAttribDatiSpec.class, id);
                                decFiltroSelUdDato.setDecAttribDatiSpec(dads);
                                decFiltroSelUdDato.setDecFiltroSelUdAttb(filtro);
                                listDecFiltroSelUdDato.add(decFiltroSelUdDato);
                            }
                        }
                    }

                    // Setto la lista di DecFiltroSelUdDato alla proprietà dell'entity DecFiltroSelUdAttb
                    filtro.setDecFiltroSelUdDatos(listDecFiltroSelUdDato);
                    // Setto l'oggetto DecTipoSerieUd alla proprietà dell'entity DecFiltroSelUdAttb
                    filtro.setDecTipoSerieUd(tipoSerieUd);

                    tipoSerieHelper.insertEntity(filtro, false);
                }
            }
            tipoSerieHelper.getEntityManager().flush();

            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nel salvataggio dei filtri dati specifici del tipo serie");
        }
    }

    public DecOutSelUdTableBean getDecOutSelUdTableBean(BigDecimal idTipoSerieUd) {
        List<DecOutSelUd> lista = tipoSerieHelper.getDecOutSelUdPerTIpoSerieUd(idTipoSerieUd);
        DecOutSelUdTableBean result = new DecOutSelUdTableBean();

        for (DecOutSelUd out : lista) {
            try {
                DecOutSelUdRowBean row = (DecOutSelUdRowBean) Transform.entity2RowBean(out);
                CostantiDB.TipoDiRappresentazione tipoDiRappresentazione = CostantiDB.TipoDiRappresentazione
                        .byName(row.getTiOut());
                row.setString("desc_ti_out", tipoDiRappresentazione.toString());
                result.add(row);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return result;
    }

    public DecCampoOutSelUdTableBean getDecCampoOutSelUdTableBeanPerDecOutSelUd(BigDecimal idOutSelUd,
            String ti_campo) {

        List<DecCampoOutSelUd> lista = tipoSerieHelper.getDecCampoOutSelUdPerDecOutSelUd(idOutSelUd, ti_campo);
        DecCampoOutSelUdTableBean tableBean = new DecCampoOutSelUdTableBean();
        try {
            if (lista != null && !lista.isEmpty()) {
                tableBean = (DecCampoOutSelUdTableBean) Transform.entities2TableBean(lista);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero dei dati di profilo per tipoSerieUd "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;

    }

    public void insertDecOutSelUd(LogParam param, DecOutSelUdRowBean rowBean,
            Map<String, Map<String, String>> listaAttributiSelezionati, BigDecimal idTipoSerie) throws ParerUserError {
        try {
            DecOutSelUd decOutSelUd = null;
            BigDecimal idTipoSerieUd = rowBean.getIdTipoSerieUd();
            if (rowBean.getIdOutSelUd() == null || BigDecimal.ZERO.equals(rowBean.getIdOutSelUd())) {
                decOutSelUd = (DecOutSelUd) Transform.rowBean2Entity(rowBean);
                DecTipoSerieUd decTipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUd);
                if (decTipoSerieUd != null) {
                    decOutSelUd.setDecTipoSerieUd(decTipoSerieUd);
                }

                tipoSerieHelper.insertEntity(decOutSelUd, true);
                rowBean.setIdOutSelUd(new BigDecimal(decOutSelUd.getIdOutSelUd()));
                if (listaAttributiSelezionati != null && !listaAttributiSelezionati.isEmpty()) {
                    aggiungiCampiOutSelUd(listaAttributiSelezionati, decOutSelUd);
                }
            } else {
                manageAttributi(rowBean, listaAttributiSelezionati);
            }
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore durante il salvataggio della regola di rappresentazione del tipo serie");
        }
    }

    private void manageAttributi(DecOutSelUdRowBean rowBean, Map<String, Map<String, String>> listaAttributiSelezionati)
            throws Exception {
        BigDecimal idOutSelUd = rowBean.getIdOutSelUd();
        DecOutSelUd outSelUd = tipoSerieHelper.findById(DecOutSelUd.class, idOutSelUd);
        outSelUd.setDlFormatoOut(rowBean.getDlFormatoOut());

        DecCampoOutSelUdTableBean datiCompilati = new DecCampoOutSelUdTableBean();

        // Recupero i campi salvati su DB
        List<DecCampoOutSelUd> campiPreModifiche = tipoSerieHelper.getDecCampoOutSelUdPerDecOutSelUd(idOutSelUd, null);

        // va trasformata la mappa listaAttributiSelezionati in qualcosa di confrontabile (come datiCompilati)
        for (Map.Entry<String, Map<String, String>> entry : listaAttributiSelezionati.entrySet()) {
            String tiCampo = entry.getKey();
            Map<String, String> nmCampoMap = entry.getValue();
            for (Map.Entry<String, String> entry2 : nmCampoMap.entrySet()) {
                String nmCampo = entry2.getKey();
                String tiTrasformCampo = entry2.getValue();
                DecCampoOutSelUdRowBean datiCompilatiRB = new DecCampoOutSelUdRowBean();
                datiCompilatiRB.setNmCampo(nmCampo);
                datiCompilatiRB.setTiCampo(tiCampo);
                datiCompilatiRB.setIdOutSelUd(idOutSelUd);
                datiCompilatiRB.setTiTrasformCampo(tiTrasformCampo);
                datiCompilati.add(datiCompilatiRB);
            }
        }

        /* 1° - Elimino i campi non più presenti o modifico, in caso, il campo riferito al tipo trasformazione */
        for (DecCampoOutSelUd campoPreModifica : campiPreModifiche) {
            boolean isPresente = false;
            for (DecCampoOutSelUdRowBean datoCompilato : datiCompilati) {
                if (campoPreModifica.getNmCampo().equals(datoCompilato.getNmCampo()) && campoPreModifica
                        .getDecOutSelUd().getIdOutSelUd() == datoCompilato.getIdOutSelUd().longValue()
                        && campoPreModifica.getTiCampo().equals(datoCompilato.getTiCampo())) {
                    campoPreModifica.setTiTrasformCampo(datoCompilato.getTiTrasformCampo());
                    isPresente = true;
                    break;
                }
            }
            if (!isPresente) {
                // se non è più presente, lo cancello
                tipoSerieHelper.removeEntity(campoPreModifica, true);
            }
        }

        /* 2° - Inserisco i nuovi campi */
        for (DecCampoOutSelUdRowBean row : datiCompilati) {
            if ((tipoSerieHelper.getDecCampoOutSelUd(idOutSelUd, row.getNmCampo(), row.getTiCampo()).size()) == 0) {
                DecCampoOutSelUd campo = new DecCampoOutSelUd();
                campo.setNmCampo(row.getNmCampo());
                campo.setDecOutSelUd(outSelUd);
                campo.setNmCampo(row.getNmCampo());
                campo.setTiCampo(row.getTiCampo());
                campo.setTiTrasformCampo(row.getTiTrasformCampo());
                if (!row.getTiCampo().equals(CostantiDB.TipoCampo.DATO_PROFILO.name())) {
                    Constants.TipoEntitaSacer tipoEntitaSacer = row.getTiCampo()
                            .equals(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name()) ? Constants.TipoEntitaSacer.UNI_DOC
                                    : Constants.TipoEntitaSacer.DOC;
                    DecAttribDatiSpec datoSpecifico = recuperaDatoSpecifico(outSelUd, row.getNmCampo(),
                            tipoEntitaSacer);
                    campo.setDecAttribDatiSpec(datoSpecifico);
                } else {
                    campo.setDecAttribDatiSpec(null);
                }
                tipoSerieHelper.insertEntity(campo, true);
            }
        }

    }

    private void aggiungiCampiOutSelUd(Map<String, Map<String, String>> listaAttributiSelezionati,
            DecOutSelUd decOutSelUd) {
        /*
         * "","DATO_SPEC_UNI_DOC","DATO_SPEC_DOC_PRINC"
         *
         */
        List<DecCampoOutSelUd> listaCampiOutSelUd = new ArrayList<>();
        Map<String, String> mappaDatiProfilo = listaAttributiSelezionati.get("DATO_PROFILO");
        if (mappaDatiProfilo != null && !mappaDatiProfilo.isEmpty()) {
            Set<String> keySet = mappaDatiProfilo.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String datoProfilo = iterator.next();
                DecCampoOutSelUd campo = new DecCampoOutSelUd();
                campo.setDecOutSelUd(decOutSelUd);
                campo.setNmCampo(datoProfilo);
                campo.setTiCampo("DATO_PROFILO");
                String tiOut = decOutSelUd.getTiOut();
                if (CostantiDB.NomeCampo.NUMERO.name().equals(datoProfilo)
                        && CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name().equals(tiOut)) {

                    campo.setTiTrasformCampo(CostantiDB.TipoTrasformatore.OUT_PAD_CHAR.getTransformString());
                }

                listaCampiOutSelUd.add(campo);
            }

        }
        Map<String, String> datiSpecUniDoc = listaAttributiSelezionati.get("DATO_SPEC_UNI_DOC");
        if (datiSpecUniDoc != null && !datiSpecUniDoc.isEmpty()) {

            Set<String> keySet = datiSpecUniDoc.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String datoSpecUniDoc = iterator.next();
                String tiTrasformCampo = datiSpecUniDoc.get(datoSpecUniDoc);
                DecCampoOutSelUd campo = new DecCampoOutSelUd();
                campo.setDecOutSelUd(decOutSelUd);
                campo.setNmCampo(datoSpecUniDoc);
                campo.setTiCampo("DATO_SPEC_UNI_DOC");
                if (!StringUtils.isBlank(tiTrasformCampo) && tiTrasformCampo.equals("none")) {
                    tiTrasformCampo = null;
                }

                campo.setTiTrasformCampo(tiTrasformCampo);
                DecAttribDatiSpec datoSpecifico = recuperaDatoSpecifico(decOutSelUd, datoSpecUniDoc,
                        Constants.TipoEntitaSacer.UNI_DOC);
                campo.setDecAttribDatiSpec(datoSpecifico);
                listaCampiOutSelUd.add(campo);
            }

        }

        Map<String, String> datiSpecDoc = listaAttributiSelezionati.get("DATO_SPEC_DOC_PRINC");
        if (datiSpecDoc != null && !datiSpecDoc.isEmpty()) {

            Set<String> keySet = datiSpecDoc.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String datoSpecDoc = iterator.next();
                String tiTrasformCampo = datiSpecDoc.get(datoSpecDoc);
                DecCampoOutSelUd campo = new DecCampoOutSelUd();
                campo.setDecOutSelUd(decOutSelUd);
                campo.setNmCampo(datoSpecDoc);
                campo.setTiCampo("DATO_SPEC_DOC_PRINC");
                if (!StringUtils.isBlank(tiTrasformCampo) && tiTrasformCampo.equals("none")) {
                    tiTrasformCampo = null;
                }

                campo.setTiTrasformCampo(tiTrasformCampo);
                DecAttribDatiSpec datoSpecifico = recuperaDatoSpecifico(decOutSelUd, datoSpecDoc,
                        Constants.TipoEntitaSacer.DOC);
                campo.setDecAttribDatiSpec(datoSpecifico);
                listaCampiOutSelUd.add(campo);
            }

        }

        decOutSelUd.setDecCampoOutSelUds(listaCampiOutSelUd);
    }

    private DecAttribDatiSpec recuperaDatoSpecifico(DecOutSelUd decOutSelUd, String datoSpecUniDoc,
            Constants.TipoEntitaSacer tipoEntitaSacer) {
        DecAttribDatiSpec decAttribDatiSpec = null;
        if (tipoEntitaSacer.equals(Constants.TipoEntitaSacer.UNI_DOC)) {
            long idTipoSerieUd = decOutSelUd.getDecTipoSerieUd().getIdTipoSerieUd();
            DecTipoSerieUd decTipoSerieUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUd);

            long idTipoUnitaDoc = decTipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc();
            decAttribDatiSpec = tipoSerieHelper.getDecAttribDatiSpecByName(datoSpecUniDoc, null,
                    new BigDecimal(idTipoUnitaDoc), null);
        } else if (tipoEntitaSacer.equals(Constants.TipoEntitaSacer.DOC)) {
            long idTipoSerieUd = decOutSelUd.getDecTipoSerieUd().getIdTipoSerieUd();
            DecFiltroSelUdTableBean decFiltroSelUdTableBean = getDecFiltroSelUdTableBean(new BigDecimal(idTipoSerieUd));
            for (DecFiltroSelUdRowBean row : decFiltroSelUdTableBean) {
                String tiFiltro = row.getTiFiltro();
                if (CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name().equals(tiFiltro)) {

                    BigDecimal idTipoDocPrinc = row.getIdTipoDocPrinc();
                    DecAttribDatiSpec temp = tipoSerieHelper.getDecAttribDatiSpecByName(datoSpecUniDoc, idTipoDocPrinc,
                            null, null);
                    if (temp != null) {
                        decAttribDatiSpec = temp;
                        break;
                    }
                }
            }
        }

        return decAttribDatiSpec;

    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBean(BigDecimal idTipoEntita,
            Constants.TipoEntitaSacer tipoEntitaSacer) {
        return tipoSerieHelper.getDecAttribDatiSpecTableBean(idTipoEntita, tipoEntitaSacer);
    }

    /**
     * @deprecated
     *
     * @param idTipoSerie
     *            id del tipo serie
     * @param tipoEntitaSacer
     *            tipo di entità
     *
     * @return {@link DecAttribDatiSpecTableBean}
     */
    @Deprecated
    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBeanForTipoSerie(BigDecimal idTipoSerie,
            Constants.TipoEntitaSacer tipoEntitaSacer) {
        DecAttribDatiSpecTableBean result = new DecAttribDatiSpecTableBean();
        List<DecTipoSerieUd> listaTipoSerieUd = tipoSerieHelper.getDecTipoSerieUd(idTipoSerie, null, null);
        List<Long> listaId = null;

        if (listaTipoSerieUd != null && !listaTipoSerieUd.isEmpty()) {
            switch (tipoEntitaSacer) {

            case UNI_DOC:
                listaId = new ArrayList<>();
                for (DecTipoSerieUd tipoSerieUd : listaTipoSerieUd) {
                    long idTipoUnitaDoc = tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc();
                    listaId.add(idTipoUnitaDoc);
                }
                break;
            case DOC:
                listaId = new ArrayList<>();
                for (DecTipoSerieUd tipoSerieUd : listaTipoSerieUd) {
                    long idTipoSerieUd = tipoSerieUd.getIdTipoSerieUd();
                    List<DecFiltroSelUd> decFiltroSelUdList = tipoSerieHelper.getDecFiltroSelUdList(idTipoSerieUd);
                    for (DecFiltroSelUd filtroSelUd : decFiltroSelUdList) {
                        DecTipoDoc decTipoDoc = filtroSelUd.getDecTipoDoc();
                        long idTipoDoc = decTipoDoc.getIdTipoDoc();
                        listaId.add(idTipoDoc);
                    }
                }
                break;

            }
            result = tipoSerieHelper.getDecAttribDatiSpecTableBean(tipoEntitaSacer, listaId);
        }
        return result;
    }

    public DecAttribDatiSpecRowBean getDecAttribDatiSpecById(BigDecimal idAttribDatiSpec, String value) {
        DecAttribDatiSpecRowBean row = null;
        DecAttribDatiSpec decAttribDatiSpec = tipoSerieHelper.getDecAttribDatiSpecById(idAttribDatiSpec);
        if (decAttribDatiSpec != null) {

            try {
                row = (DecAttribDatiSpecRowBean) Transform.entity2RowBean(decAttribDatiSpec);
                row.setString("Ti_trasform_campo", value);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return row;
    }

    public int deleteDecOutSelUd(LogParam param, DecOutSelUdRowBean decOutSelUdRowBean, BigDecimal idTipoSerie)
            throws ParerUserError {
        try {
            BigDecimal idOutSelUd = decOutSelUdRowBean.getIdOutSelUd();
            int deleteDecOutSelUd = tipoSerieHelper.deleteDecOutSelUd(idOutSelUd);
            // Setto a true il flag fl_tipo_serie_upd
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
            return deleteDecOutSelUd;
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nella cancellazione della regola di rappresentazione del tipo serie");
        }
    }

    public boolean isDecOutSelUdPresent(BigDecimal idOutSelUd, String tiOut) {

        return tipoSerieHelper.isDecOutSelUdPresent(idOutSelUd, tiOut);

    }

    public BaseTableInterface getDecCampoInpDatiProfilo(BigDecimal idTipoSerie, String ti_campo,
            boolean soloSelezionati) {
        List<DecCampoInpUd> listaTiCampo = tipoSerieHelper.getDecCampoInpUdPerTipoSerie(idTipoSerie, ti_campo);
        BaseTableInterface tabella = new BaseTable();
        BaseTableInterface appoTable = new BaseTable();
        String key1 = "key_campo";
        String key2 = "nm_campo";
        for (CostantiDB.NomeCampo campo : CostantiDB.NomeCampo.getListaDatoProfiloIndividuazione()) {
            BaseRow row = new BaseRow();
            String name = campo.name();
            row.setString(key1, name);
            row.setString(key2, campo.getDescrizione());
            row.setBigDecimal("fl_selezionato", BigDecimal.ZERO);

            for (DecCampoInpUd decCampoInpUd : listaTiCampo) {
                if (decCampoInpUd.getNmCampo().equals(campo.name())) {
                    row.setBigDecimal("fl_selezionato", BigDecimal.ONE);
                    row.setString("pg_ord_campo", decCampoInpUd.getPgOrdCampo().toString());
                    if (soloSelezionati) {
                        appoTable.add(row);
                    }
                    break;
                }
            }
            tabella.add(row);
        }
        if (soloSelezionati) {
            return appoTable;
        } else {
            return tabella;
        }
    }

    public DecCampoInpUdTableBean getDecCampoInpUdTableBeanPerDecTipoSerie(BigDecimal idTipoSerie, String tipoCampo) {
        DecCampoInpUdTableBean decCampoInpUdTableBean = new DecCampoInpUdTableBean();
        List<DecCampoInpUd> lista = tipoSerieHelper.getDecCampoInpUdPerTipoSerie(idTipoSerie, tipoCampo);
        try {
            for (DecCampoInpUd decCampoInpUd : lista) {
                DecCampoInpUdRowBean newBean = (DecCampoInpUdRowBean) Transform.entity2RowBean(decCampoInpUd);
                if (!decCampoInpUd.getTiCampo().equals("DATO_PROFILO")) {
                    List<String> xsdPerCampoInpUdList = tipoSerieHelper
                            .getDecXsdDatiSpecByCampoInpUd(newBean.getIdCampoInpUd());

                    newBean.setObject("cd_versione_xsd", StringUtils.join(xsdPerCampoInpUdList, ", "));

                }
                decCampoInpUdTableBean.add(newBean);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore nel recupero delle regole di acquisizione per Tipologia serie : " + idTipoSerie, ex);
        }

        return decCampoInpUdTableBean;
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di acquisizione modificabili del tipo serie dato in input e
     * tipo campo
     *
     * @param idTipoSerie
     *            id tipo serie
     * @param tiCampo
     *            tipo campo delle regole
     *
     * @return il tableBean della lista
     *
     * @throws ParerUserError
     *             errore generico
     */
    public DecCampoInpUdTableBean getDecCampoInpUdTableBean(BigDecimal idTipoSerie, String tiCampo)
            throws ParerUserError {
        List<DecCampoInpUd> list = tipoSerieHelper.getDecCampoInpUdPerTipoSerie(idTipoSerie, tiCampo);
        DecCampoInpUdTableBean table = transformDecCampoInpUd(list);
        List<Object> toList = table.toList(DecCampoInpUdTableDescriptor.COL_NM_CAMPO);
        for (CostantiDB.NomeCampo campo : CostantiDB.NomeCampo.getListaDatoProfiloIndividuazione()) {
            if (toList.contains(campo.name())) {
                DecCampoInpUdRowBean row = table.getRow(toList.indexOf(campo.name()));
                row.setString("key_campo", campo.getDescrizione());
                row.setBigDecimal("num_ord_campo", new BigDecimal(campo.getNumeroOrdine()));
            } else {
                table.last();
                DecCampoInpUdRowBean row = new DecCampoInpUdRowBean();
                row.setString("nm_campo", campo.name());
                row.setString("key_campo", campo.getDescrizione());
                row.setBigDecimal("num_ord_campo", new BigDecimal(campo.getNumeroOrdine()));
                table.add(row);
            }
        }
        table.addSortingRule("num_ord_campo", SortingRule.ASC);
        table.sort();

        return table;
    }

    private DecCampoInpUdTableBean transformDecCampoInpUd(List<DecCampoInpUd> list) throws ParerUserError {
        DecCampoInpUdTableBean table = new DecCampoInpUdTableBean();
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecCampoInpUdTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di regole di acquisizione dei tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di acquisizione sui dati specifici modificabili del tipo serie
     * dato in input e tipo campo, aggiungendo a ogni riga le versioni del dato specifico
     *
     * @param idTipoSerie
     *            id modello
     * @param idTipoEntita
     *            id tipo entita
     * @param tiCampo
     *            tipo campo delle regole, DATO_SPEC_UNI_DOC o DATO_SPEC_DOC_PRINC
     *
     * @return il tableBean della lista
     *
     * @throws ParerUserError
     *             errore generico
     */
    public DecCampoInpUdTableBean getDecCampoInpUdDatiSpecTableBean(BigDecimal idTipoSerie, BigDecimal idTipoEntita,
            String tiCampo) throws ParerUserError {
        // Ricavo le regole di acquisizione (dati specifici) riferite al tipo unita doc o al tipo doc principale a
        // seconda del tipo campo in input
        List<DecCampoInpUd> list = tipoSerieHelper.getDecCampoInpUdPerTipoSerie(idTipoSerie, tiCampo);
        DecCampoInpUdTableBean table = transformDecCampoInpUd(list);
        // Converto il tablebean in una lista oggetti ordinata per nome campo
        List<Object> toList = table.toList(DecCampoInpUdTableDescriptor.COL_NM_CAMPO);
        Constants.TipoEntitaSacer tipoEntitaSacer = tiCampo.equals(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name())
                ? Constants.TipoEntitaSacer.UNI_DOC : Constants.TipoEntitaSacer.DOC;
        // Ricerco i dati specifici nella loro tabella, cercando i dati spec di un tipo ud o tipo, in base ai parametri
        // passati in input
        List<DecAttribDatiSpec> decAttribDatiSpecs = tipoSerieHelper.getDecAttribDatiSpec(idTipoEntita,
                tipoEntitaSacer);
        // Scorro tutti i dati specifici che dovranno essere "adattati" in DecCampoInpUD.
        // Verifico se il dato spec era presente in DecCampoInpUd: se presente, lo decoro, se non presente, lo mostro
        // vuoto
        for (DecAttribDatiSpec attrib : decAttribDatiSpecs) {
            String versioniXsd = tipoSerieHelper.getVersioniXsd(new BigDecimal(attrib.getIdAttribDatiSpec()),
                    idTipoEntita, tipoEntitaSacer);
            String name = attrib.getNmAttribDatiSpec();
            if (StringUtils.isNotBlank(versioniXsd)) {
                name += " " + versioniXsd;
            }
            if (!toList.contains(attrib.getNmAttribDatiSpec())) {
                table.last();
                DecCampoInpUdRowBean row = new DecCampoInpUdRowBean();
                row.setString("key_campo", name);
                row.setString("nm_campo", attrib.getNmAttribDatiSpec());
                row.setBigDecimal("pg_ord_campo", null);
                row.setBigDecimal("id_attrib_dati_spec", BigDecimal.valueOf(attrib.getIdAttribDatiSpec()));
                table.add(row);
            } else {
                DecCampoInpUdRowBean row = table.getRow(toList.indexOf(attrib.getNmAttribDatiSpec()));
                row.setString("key_campo", name);
                row.setBigDecimal("id_attrib_dati_spec", BigDecimal.valueOf(attrib.getIdAttribDatiSpec()));
            }
        }

        table.addSortingRule("nm_campo", SortingRule.ASC);
        table.sort();
        return table;
    }

    private void aggiungiCampiInpUd(Map<String, Map<String, String>> listaAttributiSelezionati,
            DecTipoSerie decTipoSerie) {
        /*
         * "","DATO_SPEC_UNI_DOC","DATO_SPEC_DOC_PRINC" private long idCampoInpUd; private String nmCampo; private
         * BigDecimal pgOrdCampo; private String tiCampo; private String tiTrasformCampo; private DecAttribDatiSpec
         * decAttribDatiSpec; private DecTipoSerie filtroSelUd;
         *
         */
        List<DecCampoInpUd> listaCampiInpUd = new ArrayList<>();
        Map<String, String> mappaDatiProfilo = listaAttributiSelezionati.get("DATO_PROFILO");
        if (mappaDatiProfilo != null && !mappaDatiProfilo.isEmpty()) {
            Set<String> keySet = mappaDatiProfilo.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {

                String datoProfilo = iterator.next();
                String value = mappaDatiProfilo.get(datoProfilo);
                // String[] values = value.split("[|]");
                DecCampoInpUd campo = new DecCampoInpUd();
                campo.setDecTipoSerie(decTipoSerie);
                campo.setNmCampo(datoProfilo);
                if (!StringUtils.isBlank(value)) {
                    BigDecimal pg_campo_ord = new BigDecimal(value);
                    campo.setPgOrdCampo(pg_campo_ord);

                }
                campo.setTiCampo("DATO_PROFILO");
                listaCampiInpUd.add(campo);
            }

        }
        Map<String, String> datiSpecUniDoc = listaAttributiSelezionati.get("DATO_SPEC_UNI_DOC");
        if (datiSpecUniDoc != null && !datiSpecUniDoc.isEmpty()) {

            Set<String> keySet = datiSpecUniDoc.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String datoSpecUniDoc = iterator.next();
                // Il value della mappa ï¿½ una concatenazione di valori :
                // {tiTRASFORM}|{pgORDCampo}
                // sono i valori inseriti dall'utente rispettivamente
                // nella combo del trasformatore e nel campo dell'ordine.
                String value = datiSpecUniDoc.get(datoSpecUniDoc);
                String[] values = value.split("[|]");
                String tiTrasformCampo = values[0];
                if (!StringUtils.isBlank(tiTrasformCampo) && tiTrasformCampo.equals("none")) {
                    tiTrasformCampo = null;
                }
                String pgOrdCampo = values[1];
                String idAttribDatiSpec = values[2];
                DecCampoInpUd campo = new DecCampoInpUd();
                campo.setDecTipoSerie(decTipoSerie);
                campo.setNmCampo(datoSpecUniDoc);
                campo.setTiCampo("DATO_SPEC_UNI_DOC");
                campo.setTiTrasformCampo(tiTrasformCampo);
                if (!StringUtils.isBlank(idAttribDatiSpec)) {
                    BigDecimal idAttr = new BigDecimal(idAttribDatiSpec);
                    DecAttribDatiSpec datoSpecifico = tipoSerieHelper.getDecAttribDatiSpecById(idAttr);
                    campo.setDecAttribDatiSpec(datoSpecifico);

                }
                if (!StringUtils.isBlank(pgOrdCampo)) {
                    BigDecimal pgOrd = new BigDecimal(pgOrdCampo);
                    campo.setPgOrdCampo(pgOrd);

                }
                listaCampiInpUd.add(campo);
            }

        }

        Map<String, String> datiSpecDoc = listaAttributiSelezionati.get("DATO_SPEC_DOC_PRINC");
        if (datiSpecDoc != null && !datiSpecDoc.isEmpty()) {

            Set<String> keySet = datiSpecDoc.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String datoSpecDoc = iterator.next();
                String value = datiSpecDoc.get(datoSpecDoc);
                String[] values = value.split("[|]");
                String tiTrasformCampo = values[0];
                if (!StringUtils.isBlank(tiTrasformCampo) && tiTrasformCampo.equals("none")) {
                    tiTrasformCampo = null;
                }
                String pgOrdCampo = values[1];
                String idAttribDatiSpec = values[2];
                DecCampoInpUd campo = new DecCampoInpUd();
                campo.setDecTipoSerie(decTipoSerie);
                campo.setNmCampo(datoSpecDoc);
                campo.setTiCampo("DATO_SPEC_DOC_PRINC");
                campo.setTiTrasformCampo(tiTrasformCampo);
                if (!StringUtils.isBlank(idAttribDatiSpec)) {
                    BigDecimal idAttr = new BigDecimal(idAttribDatiSpec);
                    DecAttribDatiSpec datoSpecifico = tipoSerieHelper.getDecAttribDatiSpecById(idAttr);
                    campo.setDecAttribDatiSpec(datoSpecifico);

                }
                if (!StringUtils.isBlank(pgOrdCampo)) {
                    BigDecimal pgOrd = new BigDecimal(pgOrdCampo);
                    campo.setPgOrdCampo(pgOrd);

                }
                listaCampiInpUd.add(campo);
            }

        }

        decTipoSerie.setDecCampoInpUds(listaCampiInpUd);
    }

    public Long getIdDecAttribDatiSpecByName(String nmAttribDatiSpec, BigDecimal idTipoDoc, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoCompDoc) {
        DecAttribDatiSpec decAttribDatiSpecByName = tipoSerieHelper.getDecAttribDatiSpecByName(nmAttribDatiSpec,
                idTipoDoc, idTipoUnitaDoc, idTipoCompDoc);
        return decAttribDatiSpecByName.getIdAttribDatiSpec();
    }

    public void insertDecCampiInpUdPerTipoSerie(LogParam param, DecTipoSerieRowBean tipoSerieRowBean,
            Map<String, Map<String, String>> listaAttributiSelezionati, boolean isMod) throws ParerUserError {
        try {
            BigDecimal idTipoSerie = tipoSerieRowBean.getIdTipoSerie();
            DecTipoSerie tipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie.longValue());
            tipoSerieHelper.deleteDecCampiInpUdPerTipoSerie(idTipoSerie);
            aggiungiCampiInpUd(listaAttributiSelezionati, tipoSerie);
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore durante il salvataggio delle regole di acquisizione");
        }
    }

    /**
     * Esegue il salvataggio delle regole di acquisizione per il tipo serie dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idTipoSerie
     *            id tipo serie
     * @param datiCompilati
     *            il tablebean con i dati provenienti dall'online a seguito di inserimenti/modifiche/cancellazioni
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecCampoInpUd(LogParam param, BigDecimal idTipoSerie, DecCampoInpUdTableBean datiCompilati)
            throws ParerUserError {
        try {
            // "Normalizzo" i numeri d'ordine del tablebean dell'online
            normalizePgOrdCampo(datiCompilati);

            // Ricavo i "vecchi" valori su DB da confrontare poi con i nuovi dell'online
            DecTipoSerie decTipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie);
            List<DecCampoInpUd> campiPreModifiche = decTipoSerie.getDecCampoInpUds();

            /* 1° - Elimino i campi non più presenti */
            for (DecCampoInpUd campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (DecCampoInpUdRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdCampoInpUd() != null
                            && campoPreModifica.getIdCampoInpUd() == datoCompilato.getIdCampoInpUd().longValue()) {
                        campoPreModifica.setTiTrasformCampo(datoCompilato.getTiTrasformCampo());
                        isPresente = true;
                        break;
                    }
                }
                if (!isPresente) {
                    // se non è più presente, lo cancello
                    tipoSerieHelper.removeEntity(campoPreModifica, true);
                }
            }

            /*
             * 2° - Sistemo i campi "modificati", ovvero quelli in cui ho scambiato pgOrdCampo tra due campi esistenti
             * oppure glielo ho cambiato con uno nuovo
             */
            ArrayList<DecCampoInpUd> al = new ArrayList();
            for (DecCampoInpUd campoPreModifica : campiPreModifiche) {
                for (DecCampoInpUdRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdCampoInpUd() != null
                            && campoPreModifica.getIdCampoInpUd() == datoCompilato.getIdCampoInpUd().longValue()) {
                        if (campoPreModifica.getPgOrdCampo().longValue() != datoCompilato.getPgOrdCampo().longValue()) {
                            DecCampoInpUd campo = new DecCampoInpUd();
                            campo.setNmCampo(campoPreModifica.getNmCampo());
                            campo.setDecTipoSerie(decTipoSerie);
                            campo.setPgOrdCampo(datoCompilato.getPgOrdCampo());
                            campo.setTiCampo(campoPreModifica.getTiCampo());
                            campo.setTiTrasformCampo(campoPreModifica.getTiTrasformCampo());
                            campo.setDecAttribDatiSpec((datoCompilato.getIdAttribDatiSpec() != null ? tipoSerieHelper
                                    .findById(DecAttribDatiSpec.class, datoCompilato.getIdAttribDatiSpec()) : null));
                            tipoSerieHelper.removeEntity(campoPreModifica, true);
                            al.add(campo);
                            break;
                        }
                    }
                }
            }

            for (DecCampoInpUd decCampoInpUd : al) {
                tipoSerieHelper.insertEntity(decCampoInpUd, true);
            }

            /* 3° - Inserisco i nuovi campi */
            for (DecCampoInpUdRowBean row : datiCompilati) {
                if (row.getIdCampoInpUd() == null) {
                    DecCampoInpUd campo = new DecCampoInpUd();
                    campo.setNmCampo(row.getNmCampo());
                    campo.setDecTipoSerie(decTipoSerie);
                    campo.setPgOrdCampo(row.getPgOrdCampo());
                    campo.setTiCampo(row.getTiCampo());
                    campo.setTiTrasformCampo(row.getTiTrasformCampo());
                    campo.setDecAttribDatiSpec((row.getIdAttribDatiSpec() != null
                            ? tipoSerieHelper.findById(DecAttribDatiSpec.class, row.getIdAttribDatiSpec()) : null));
                    tipoSerieHelper.insertEntity(campo, true);
                }
            }

            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle regole di acquisizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    private void normalizePgOrdCampo(DecCampoInpUdTableBean tb) {
        long count = 0;
        for (DecCampoInpUdRowBean rb : tb) {
            rb.setPgOrdCampo(new BigDecimal(++count));
        }
    }

    // FIXMEPLEASE gestire le transazioni
    public void deleteDecCampiInpUdPerTipoSerie(BigDecimal idTipoSerie) {
        tipoSerieHelper.deleteDecCampiInpUdPerTipoSerie(idTipoSerie);
    }

    public DecNotaTipoSerieTableBean getDecNoteTipoSerie(BigDecimal idTipoSerie) {
        DecNotaTipoSerieTableBean table = new DecNotaTipoSerieTableBean();

        List<DecNotaTipoSerie> lista = tipoSerieHelper.getDecNoteTipoSerie(idTipoSerie);
        try {
            for (DecNotaTipoSerie nota : lista) {
                IamUser iamUser = nota.getIamUser();
                // String dsNotaVerSerie = nota.getDsNotaVerSerie();
                // if(!StringUtils.isBlank(dsNotaVerSerie) && dsNotaVerSerie.length()>100){
                // dsNotaVerSerie=StringUtils.substring(dsNotaVerSerie, 0, 100);
                // nota.setDsNotaVerSerie(dsNotaVerSerie);
                //
                // }
                DecNotaTipoSerieRowBean row = (DecNotaTipoSerieRowBean) Transform.entity2RowBean(nota);
                row.setString("nm_userid", iamUser.getNmUserid());
                table.add(row);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore nel recupero delle note della tipo serie", ex);
        }
        return table;
    }

    // FIXMEPLEASE Gestire le transazioni
    public void insertDecNotaTipoSerie(LogParam param, DecNotaTipoSerieRowBean notaRow) {

        DecTipoSerie decTipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, notaRow.getIdTipoSerie());
        DecTipoNotaSerie tipoNotaSerie = tipoSerieHelper.findById(DecTipoNotaSerie.class, notaRow.getIdTipoNotaSerie());
        IamUser user = tipoSerieHelper.findById(IamUser.class, notaRow.getIdUserIam());
        DecNotaTipoSerie nota = new DecNotaTipoSerie();
        Timestamp dtNotaTipoSerie = notaRow.getDtNotaTipoSerie();
        String dsNotaTipoSerie = notaRow.getDsNotaTipoSerie();
        nota.setDtNotaTipoSerie(dtNotaTipoSerie);
        nota.setDsNotaTipoSerie(dsNotaTipoSerie);
        nota.setDecTipoSerie(decTipoSerie);
        nota.setIamUser(user);
        nota.setDecTipoNotaSerie(tipoNotaSerie);
        int maxPgPerNotatipoSerie = tipoSerieHelper.getMaxPgPerNotatipoSerie(notaRow.getIdTipoSerie(),
                notaRow.getIdTipoNotaSerie());
        nota.setPgNotaTipoSerie(new BigDecimal(maxPgPerNotatipoSerie));
        tipoSerieHelper.insertEntity(nota, true);
        notaRow.setIdNotaTipoSerie(new BigDecimal(nota.getIdNotaTipoSerie()));
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                new BigDecimal(decTipoSerie.getIdTipoSerie()), param.getNomePagina());
    }

    // FIXMEPLEASE Gestire le transazioni
    public Date updateDecNotaTipoSerie(LogParam param, BigDecimal idNotaTipoSerie, DecNotaTipoSerieRowBean notaRow) {
        DecNotaTipoSerie nota = tipoSerieHelper.findById(DecNotaTipoSerie.class, idNotaTipoSerie);
        nota.setDsNotaTipoSerie(notaRow.getDsNotaTipoSerie());
        IamUser user = tipoSerieHelper.findById(IamUser.class, notaRow.getIdUserIam());
        DecTipoNotaSerie tipoNotaSerie = tipoSerieHelper.findById(DecTipoNotaSerie.class, notaRow.getIdTipoNotaSerie());
        nota.setIamUser(user);
        nota.setDecTipoNotaSerie(tipoNotaSerie);
        // nota.setDsNotaVerSerie(notaRow.getDsNotaVerSerie());
        nota.setDtNotaTipoSerie(Calendar.getInstance().getTime());
        tipoSerieHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                new BigDecimal(nota.getDecTipoSerie().getIdTipoSerie()), param.getNomePagina());
        return nota.getDtNotaTipoSerie();
    }

    // FIXMEPLEASE Gestire le transazioni
    public int deleteDecNoteTipoSerie(LogParam param, BigDecimal idNotaTipoSerie) {
        DecNotaTipoSerie nota = tipoSerieHelper.getDecNotaTipoSerieById(idNotaTipoSerie);
        long idTipoSerie = nota.getDecTipoSerie().getIdTipoSerie();
        int ret = tipoSerieHelper.deleteDecNoteTipoSerieById(idNotaTipoSerie);
        tipoSerieHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, new BigDecimal(idTipoSerie),
                param.getNomePagina());
        return ret;
    }

    public DecNotaTipoSerieRowBean getDecNotaTipoSerieById(BigDecimal idNotaTipoSerie) {
        DecNotaTipoSerieRowBean row = null;
        DecNotaTipoSerie nota = tipoSerieHelper.findById(DecNotaTipoSerie.class, idNotaTipoSerie);
        try {
            row = (DecNotaTipoSerieRowBean) Transform.entity2RowBean(nota);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore nel recupero della nota della serie", ex);
        }
        return row;
    }

    public DecTipoSerieUdRowBean getDecTipoSerieUdById(BigDecimal idTipoSerieUd) {
        DecTipoSerieUdRowBean decTipoSerieUdRowBean = null;

        DecTipoSerieUd decTipoSeriaUd = tipoSerieHelper.findById(DecTipoSerieUd.class, idTipoSerieUd);

        try {
            decTipoSerieUdRowBean = (DecTipoSerieUdRowBean) Transform.entity2RowBean(decTipoSeriaUd);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore nel recupero del decTipoSeriaUd con id " + idTipoSerieUd.toPlainString(), ex);
        }

        return decTipoSerieUdRowBean;
    }

    public DecTipoNotaSerieTableBean getDecTipoNotaSerieTableBean(BigDecimal idTipoSerie) {

        DecTipoNotaSerieTableBean result = new DecTipoNotaSerieTableBean();

        List<DecTipoNotaSerie> decTipoNotaSerieList = null;
        List<DecTipoNotaSerie> decTipoNotaSerieNoFlMoltList = null;

        if (idTipoSerie != null) {
            decTipoNotaSerieList = tipoSerieHelper.getDecTipoNotaSerieList();
            decTipoNotaSerieNoFlMoltList = tipoSerieHelper.getDecTipoNotaSerieNoFlMoltList(idTipoSerie);
        } else {
            decTipoNotaSerieList = tipoSerieHelper.getAllDecTipoNotaSerieList();
        }
        if (decTipoNotaSerieList != null && !decTipoNotaSerieList.isEmpty()) {

            try {
                for (DecTipoNotaSerie decTipoNotaSerie : decTipoNotaSerieList) {
                    DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform
                            .entity2RowBean(decTipoNotaSerie);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaSerie(rowBean.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                    }
                    result.add(rowBean);
                }
                if (decTipoNotaSerieNoFlMoltList != null && !decTipoNotaSerieNoFlMoltList.isEmpty()) {
                    for (DecTipoNotaSerie tipoNota : decTipoNotaSerieNoFlMoltList) {
                        DecTipoNotaSerieRowBean row = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(tipoNota);
                        if (row.getFlObblig().equals("1")) {
                            row.setDsTipoNotaSerie(row.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                        }
                        result.add(row);
                    }
                }
                result.addSortingRule("ni_ord", SortingRule.ASC);
                result.sort();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore nel recupero della tabella dei tipo di nota sui tipi serie", ex);
            }

        }
        return result;
    }

    public DecTipoNotaSerieTableBean getDecTipoNotaSerieNotInVerSerieTableBean(BigDecimal idVerSerie) {
        DecTipoNotaSerieTableBean table = new DecTipoNotaSerieTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaSerie> decTipoNotaSerieList = tipoSerieHelper.getDecTipoNotaSerieList();
        List<DecTipoNotaSerie> listNotInVerSerie = tipoSerieHelper.getDecTipoNotaSerieNotInVerSerie(idVerSerie);
        try {
            if (decTipoNotaSerieList != null && !decTipoNotaSerieList.isEmpty()) {
                for (DecTipoNotaSerie row : decTipoNotaSerieList) {
                    DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaSerie(rowBean.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInVerSerie != null && !listNotInVerSerie.isEmpty()) {
                for (DecTipoNotaSerie row : listNotInVerSerie) {
                    DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaSerie(rowBean.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota della serie di unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public DecTipoNotaSerieTableBean getDecTipoNotaSerieNotInModelloTableBean(BigDecimal idModelloTipoSerie) {
        DecTipoNotaSerieTableBean table = new DecTipoNotaSerieTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaSerie> decTipoNotaSerieList = tipoSerieHelper.getDecTipoNotaSerieList();
        List<DecTipoNotaSerie> listNotInModelloSerie = tipoSerieHelper
                .getDecTipoNotaSerieNotInModelloSerie(idModelloTipoSerie);
        try {
            if (decTipoNotaSerieList != null && !decTipoNotaSerieList.isEmpty()) {
                for (DecTipoNotaSerie row : decTipoNotaSerieList) {
                    DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaSerie(rowBean.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInModelloSerie != null && !listNotInModelloSerie.isEmpty()) {
                for (DecTipoNotaSerie row : listNotInModelloSerie) {
                    DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaSerie(rowBean.getDsTipoNotaSerie() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota del modello di tipo serie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public DecTipoNotaSerieTableBean getSingleDecTipoNotaSerieTableBean(BigDecimal idTipoNotaSerie) {
        DecTipoNotaSerieTableBean table = new DecTipoNotaSerieTableBean();
        DecTipoNotaSerie nota = tipoSerieHelper.findById(DecTipoNotaSerie.class, idTipoNotaSerie);
        try {
            DecTipoNotaSerieRowBean rowBean = (DecTipoNotaSerieRowBean) Transform.entity2RowBean(nota);
            table.add(rowBean);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota della serie di unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public boolean getVersioniXsdPerTipoEntita(BigDecimal id, Constants.TipoEntitaSacer tipoEntitaSacer) {
        return tipoSerieHelper.getVersioniXsdPerTipoEntita(id, tipoEntitaSacer);
    }

    public String isFiltroSelUdByIdTipoDocPresent(BigDecimal idTipoDoc, BigDecimal idTipoSerieUd,
            BigDecimal idTipoDocOld) {
        String result = null;
        DecTipoDoc decTipoDoc = tipoSerieHelper.getDecTipoDocFromFiltroSelUdByIdTipoDoc(idTipoDoc, idTipoSerieUd);
        if (decTipoDoc != null && decTipoDoc.getIdTipoDoc() != idTipoDocOld.longValue()) {
            result = decTipoDoc.getNmTipoDoc();
        }
        return result;
    }

    public String getDecTipoDocFromFiltroSelUdByIdTipoDoc(BigDecimal idTipoDoc, BigDecimal idTipoSerieUd) {
        String result = null;
        DecTipoDoc decTipoDoc = tipoSerieHelper.getDecTipoDocFromFiltroSelUdByIdTipoDoc(idTipoDoc, idTipoSerieUd);
        if (decTipoDoc != null) {
            result = decTipoDoc.getNmTipoDoc();
        }

        return result;
    }

    public void deleteDecFiltroSelUdAttb(LogParam param, BigDecimal idFiltroSelUdAttb, BigDecimal idTipoSerie)
            throws ParerUserError {
        try {
            tipoSerieHelper.deleteDecFiltroSelUdAttb(idFiltroSelUdAttb);
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
            // Setto a true il flag fl_tipo_serie_upd
            serieEjb.setFlagContenutoVerSerieDaAggiornare(idTipoSerie);
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nella cancellazione del filtro dati specifici del tipo serie");
        }
    }

    public void deleteDecCampoInpUd(LogParam param, BigDecimal idCampoInpUd, BigDecimal idTipoSerie)
            throws ParerUserError {
        try {
            tipoSerieHelper.deleteDecCampoInpUd(idCampoInpUd);
            tipoSerieHelper.getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE, idTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            log.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nella cancellazione della regola di acquisizione del tipo serie");
        }
    }

    public boolean isTipoSerieModificabile(BigDecimal idTipoSerie) {
        return tipoSerieHelper.isTipoSerieModificabile(idTipoSerie);
    }

    public String isRegolaFiltraggioPresente(BigDecimal idTipoDoc, BigDecimal idTipoSerieUd, BigDecimal idTipoDocOld) {
        String result = null;
        DecTipoDoc decTipoDoc = tipoSerieHelper.getDecTipoDocFromFiltroSelUdByIdTipoDoc(idTipoDoc, idTipoSerieUd);
        if (decTipoDoc != null && decTipoDoc.getIdTipoDoc() != idTipoDocOld.longValue()) {
            result = decTipoDoc.getNmTipoDoc();
        }

        return result;
    }

    public Long[] getIdAmbienteEnteStrutturaTipoSerie(BigDecimal idTipoSerie) {
        Long[] ambEnteStrut = new Long[3];
        DecTipoSerie tipoSerie = tipoSerieHelper.findById(DecTipoSerie.class, idTipoSerie);
        ambEnteStrut[0] = tipoSerie.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();
        ambEnteStrut[1] = tipoSerie.getOrgStrut().getOrgEnte().getIdEnte();
        ambEnteStrut[2] = tipoSerie.getOrgStrut().getIdStrut();
        return ambEnteStrut;
    }

    /**
     *
     * Creazione automatica tipo serie standard, può essere basata su registro o su tipoUnitaDoc sulla base del flag
     * 'FlCreaTipoSerieStandard' presente in entrambe le entity
     *
     * Nel caso che la creazione sia basata sul registro, poi, viene creato in transazione un unico tipo di serie,
     * altrimenti (tipo unità doc) viene creato un tipo di serie per ogni associazione registro-tipo ud esistente per
     * quel tipo di unità documentaria
     *
     * @param param
     *            parametri per il logging
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     *
     * @return numero di tipi serie creati
     *
     * @throws ParerUserError
     *             errore generico
     */
    public int createTipoSerieStandardDaRegistroOTipoUd(LogParam param, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) throws ParerUserError {
        DecTipoUnitaDoc tipoUnitaDoc = tipoSerieHelper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        DecRegistroUnitaDoc registroUnitaDoc = tipoSerieHelper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        CostantiDB.TipoSerieCreaStandard creaStandard = checkTipoSerieStandardDaRegistroOTipoUd(registroUnitaDoc,
                tipoUnitaDoc);
        int tipiSerieCreati = 0;
        if (creaStandard != null) {
            switch (creaStandard) {
            case BASATA_SU_REGISTRO:
                // Verifico che non esista già il tipo serie che andrei a creare
                DecModelloTipoSerie modello = registroUnitaDoc.getDecModelloTipoSerie();
                // Qui esplodeva, tornava un modello nullo!
                if (modello != null) {
                    String nmTipoSerie = getNmTipoSerieForModello(modello, registroUnitaDoc, tipoUnitaDoc,
                            creaStandard.name(), null);
                    if (tipoSerieHelper.getDecTipoSerieByName(nmTipoSerie,
                            registroUnitaDoc.getOrgStrut().getIdStrut()) == null) {
                        context.getBusinessObject(TipoSerieEjb.class).createTipoSerieStandardDaRegistroWithTx(param,
                                modello, registroUnitaDoc, tipoUnitaDoc);
                        tipiSerieCreati++;
                    }
                }
                break;
            case BASATA_SU_TIPO_UNITA_DOC:
                tipiSerieCreati = context.getBusinessObject(TipoSerieEjb.class).createTipiSerieStandardDaTipiUd(param,
                        tipoUnitaDoc);
                break;
            }
        }
        return tipiSerieCreati;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int createTipoSerieStandardDaRegistroOTipoUdNewTx(LogParam param, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) throws ParerUserError {
        TipoSerieEjb me = context.getBusinessObject(TipoSerieEjb.class);
        return me.createTipoSerieStandardDaRegistroOTipoUd(param, idRegistroUnitaDoc, idTipoUnitaDoc);
    }

    public CostantiDB.TipoSerieCreaStandard checkTipoSerieStandardDaRegistroOTipoUd(BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) throws ParerUserError {
        DecTipoUnitaDoc tipoUnitaDoc = tipoSerieHelper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        DecRegistroUnitaDoc registroUnitaDoc = tipoSerieHelper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);

        return checkTipoSerieStandardDaRegistroOTipoUd(registroUnitaDoc, tipoUnitaDoc);
    }

    public CostantiDB.TipoSerieCreaStandard checkTipoSerieStandardDaRegistroOTipoUd(
            DecRegistroUnitaDoc registroUnitaDoc, DecTipoUnitaDoc tipoUnitaDoc) throws ParerUserError {
        CostantiDB.TipoSerieCreaStandard returnEnum = null;
        boolean regCreaTipoSerieStandard = registroUnitaDoc.getFlCreaTipoSerieStandard() != null
                ? registroUnitaDoc.getFlCreaTipoSerieStandard().equals("1") : false;
        boolean tipoUdCreaTipoSerieStandard = tipoUnitaDoc.getFlCreaTipoSerieStandard() != null
                ? tipoUnitaDoc.getFlCreaTipoSerieStandard().equals("1") : false;
        if (regCreaTipoSerieStandard && tipoUdCreaTipoSerieStandard) {
            throw new ParerUserError("Il registro " + registroUnitaDoc.getCdRegistroUnitaDoc()
                    + " e la tipologia di unit\u00E0 documentaria " + tipoUnitaDoc.getNmTipoUnitaDoc()
                    + " presentano entrambi \"Crea tipo serie standard\" selezionato: non \u00E8 possibile associare i due elementi");
        } else if (regCreaTipoSerieStandard) {
            // Creazione automatica tipo serie standard basata su registro
            returnEnum = CostantiDB.TipoSerieCreaStandard.BASATA_SU_REGISTRO;
        } else if (tipoUdCreaTipoSerieStandard) {
            // Creazione automatica tipo serie standard basata su tipo ud
            returnEnum = CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC;
        }
        return returnEnum;
    }

    /**
     * Creazione automatica tipo serie standard basata su tipo unità documentaria
     *
     * @param param
     *            parametri per il logging
     * @param tipoUnitaDoc
     *            tipo unita doc
     *
     * @return numero di tipi serie creati
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int createTipiSerieStandardDaTipiUd(LogParam param, DecTipoUnitaDoc tipoUnitaDoc) throws ParerUserError {
        int tipiSerieCreati = 0;
        try {
            DecModelloTipoSerie modello = tipoUnitaDoc.getDecModelloTipoSerie();
            if (modello != null) {
                for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : tipoUnitaDoc.getDecTipoUnitaDocAmmessos()) {
                    DecRegistroUnitaDoc registroUnitaDoc = tipoUnitaDocAmmesso.getDecRegistroUnitaDoc();
                    String nmTipoSerie = getNmTipoSerieForModello(modello, registroUnitaDoc, tipoUnitaDoc,
                            CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name(), null);
                    // if
                    // (modello.getTiRglNmTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name()))
                    // {
                    // nmTipoSerie = modello.getNmTipoSerieDaCreare()
                    // + (StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc()) ? " " +
                    // registroUnitaDoc.getCdRegistroUnitaDoc() : "");
                    // } else if
                    // (modello.getTiRglNmTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name()))
                    // {
                    // if (StringUtils.isNotBlank(registroUnitaDoc.getNmTipoSerieDaCreare())) {
                    // nmTipoSerie = registroUnitaDoc.getNmTipoSerieDaCreare();
                    // } else {
                    // nmTipoSerie = tipoUnitaDoc.getNmTipoSerieDaCreare() + " " +
                    // registroUnitaDoc.getCdRegistroUnitaDoc();
                    // }
                    // } else {
                    // throw new IllegalStateException("Errore inatteso nella creazione dei tipi serie");
                    // }

                    if (tipoSerieHelper.getDecTipoSerieByName(nmTipoSerie,
                            tipoUnitaDoc.getOrgStrut().getIdStrut()) == null) {
                        if (tipoUnitaDocAmmesso.getDecRegistroUnitaDoc().getFlCreaSerie().equals("1")) {
                            List<DecTipoUnitaDocAmmesso> tmpList = new ArrayList<>();
                            tmpList.add(tipoUnitaDocAmmesso);
                            createTipoSerieStandardDaRegistro(param, modello,
                                    tipoUnitaDocAmmesso.getDecRegistroUnitaDoc(), tipoUnitaDoc,
                                    CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name(), tmpList);
                            tipiSerieCreati++;
                        }
                    }
                }
            }
        } catch (ParerUserError e) {
            throw e;
        } catch (Exception e) {
            log.error("Errore imprevisto nella creazione del tipo di serie", e);
            throw new ParerUserError(
                    "Errore imprevisto nella creazione del tipo di serie: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return tipiSerieCreati;
    }

    /**
     * Creazione automatica tipo serie standard basata su registro
     *
     * @param param
     *            parametri per il logging
     * @param modelloTipoSerie
     *            modello tipo serie
     * @param registroUnitaDoc
     *            registro unita doc
     * @param tipoUnitaDoc
     *            tipo unita doc
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createTipoSerieStandardDaRegistroWithTx(LogParam param, DecModelloTipoSerie modelloTipoSerie,
            DecRegistroUnitaDoc registroUnitaDoc, DecTipoUnitaDoc tipoUnitaDoc) throws ParerUserError {
        try {
            createTipoSerieStandardDaRegistro(param, modelloTipoSerie, registroUnitaDoc, tipoUnitaDoc,
                    CostantiDB.TipoSerieCreaStandard.BASATA_SU_REGISTRO.name(),
                    registroUnitaDoc.getDecTipoUnitaDocAmmessos());
        } catch (ParerUserError e) {
            throw e;
        } catch (Exception e) {
            log.error("Errore imprevisto nella creazione del tipo di serie", e);
            throw new ParerUserError(
                    "Errore imprevisto nella creazione del tipo di serie: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public String getNmTipoSerieForModello(DecModelloTipoSerie modelloTipoSerie, DecRegistroUnitaDoc registroUnitaDoc,
            DecTipoUnitaDoc tipoUnitaDoc, String tipoSerieCreaStandard, String cdRegistroUnitaDocNew)
            throws ParerUserError {
        String nmTipoSerie;
        String registroSuffix = "";

        if (StringUtils.isNotBlank(cdRegistroUnitaDocNew)) {
            registroSuffix = " " + cdRegistroUnitaDocNew;
        } else if (StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) {
            registroSuffix = " " + registroUnitaDoc.getCdRegistroUnitaDoc();
        }
        if (modelloTipoSerie.getTiRglNmTipoSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            nmTipoSerie = modelloTipoSerie.getNmTipoSerieDaCreare()
                    + ((tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name()))
                            ? registroSuffix : "");
        } else if (modelloTipoSerie.getTiRglNmTipoSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
                if (StringUtils.isNotBlank(registroUnitaDoc.getNmTipoSerieDaCreare())) {
                    nmTipoSerie = registroUnitaDoc.getNmTipoSerieDaCreare();
                } else {
                    nmTipoSerie = tipoUnitaDoc.getNmTipoSerieDaCreare() + registroSuffix;
                }
            } else {
                nmTipoSerie = registroUnitaDoc.getNmTipoSerieDaCreare();
            }
        } else {
            throw new ParerUserError("Errore nella generazione del campo di tipologia serie");
        }
        return nmTipoSerie;
    }

    public String getDsTipoSerieForModello(DecModelloTipoSerie modelloTipoSerie, DecRegistroUnitaDoc registroUnitaDoc,
            DecTipoUnitaDoc tipoUnitaDoc, String tipoSerieCreaStandard, String cdRegistroUnitaDocNew)
            throws ParerUserError {
        String dsTipoSerie;
        String registroSuffix = "";
        if (StringUtils.isNotBlank(cdRegistroUnitaDocNew)) {
            registroSuffix = " " + cdRegistroUnitaDocNew;
        } else if (StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) {
            registroSuffix = " " + registroUnitaDoc.getCdRegistroUnitaDoc();
        }
        if (modelloTipoSerie.getTiRglDsTipoSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            dsTipoSerie = modelloTipoSerie.getDsTipoSerieDaCreare();
        } else if (modelloTipoSerie.getTiRglDsTipoSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
                if (StringUtils.isNotBlank(registroUnitaDoc.getDsTipoSerieDaCreare())) {
                    dsTipoSerie = registroUnitaDoc.getDsTipoSerieDaCreare();
                } else {
                    dsTipoSerie = tipoUnitaDoc.getDsTipoSerieDaCreare() + registroSuffix;
                }
            } else {
                dsTipoSerie = registroUnitaDoc.getDsTipoSerieDaCreare();
            }
        } else {
            throw new ParerUserError("Errore nella generazione del campo di descrizione tipo serie");
        }
        return dsTipoSerie;
    }

    public String getCdSerieForModello(DecModelloTipoSerie modelloTipoSerie, DecRegistroUnitaDoc registroUnitaDoc,
            DecTipoUnitaDoc tipoUnitaDoc, String tipoSerieCreaStandard, String cdRegistroUnitaDocNew)
            throws ParerUserError {
        String cdSerie;
        String registroSuffix = "";
        if (StringUtils.isNotBlank(cdRegistroUnitaDocNew)) {
            registroSuffix = "_" + cdRegistroUnitaDocNew;
        } else if (StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) {
            registroSuffix = "_" + registroUnitaDoc.getCdRegistroUnitaDoc();
        }

        if (modelloTipoSerie.getTiRglCdSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            cdSerie = modelloTipoSerie.getCdSerieDaCreare()
                    + ((tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name()))
                            ? registroSuffix : "");
        } else if (modelloTipoSerie.getTiRglCdSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
                if (StringUtils.isNotBlank(registroUnitaDoc.getCdSerieDaCreare())) {
                    cdSerie = registroUnitaDoc.getCdSerieDaCreare();
                } else {
                    cdSerie = tipoUnitaDoc.getCdSerieDaCreare() + registroSuffix;
                }
            } else {
                cdSerie = registroUnitaDoc.getCdSerieDaCreare();
            }
        } else {
            throw new ParerUserError("Errore nella generazione del campo di codice serie");
        }
        return cdSerie.replaceAll("\\s+", "_");
    }

    public String getDsSerieForModello(DecModelloTipoSerie modelloTipoSerie, DecRegistroUnitaDoc registroUnitaDoc,
            DecTipoUnitaDoc tipoUnitaDoc, String tipoSerieCreaStandard, String cdRegistroUnitaDocNew)
            throws ParerUserError {
        String dsSerie;
        String registroSuffix = "";
        if (StringUtils.isNotBlank(cdRegistroUnitaDocNew)) {
            registroSuffix = " " + cdRegistroUnitaDocNew;
        } else if (StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) {
            registroSuffix = " " + registroUnitaDoc.getCdRegistroUnitaDoc();
        }
        if (modelloTipoSerie.getTiRglDsSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            dsSerie = modelloTipoSerie.getDsSerieDaCreare();
        } else if (modelloTipoSerie.getTiRglDsSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
                if (StringUtils.isNotBlank(registroUnitaDoc.getDsSerieDaCreare())) {
                    dsSerie = registroUnitaDoc.getDsSerieDaCreare();
                } else {
                    dsSerie = tipoUnitaDoc.getDsSerieDaCreare() + registroSuffix;
                }
            } else {
                dsSerie = registroUnitaDoc.getDsSerieDaCreare();
            }
        } else {
            throw new ParerUserError("Errore nella generazione del campo di descrizione serie");
        }
        return dsSerie;
    }

    /**
     * Creazione automatica tipo serie standard, può essere basata su registro o su tipoUnitaDoc
     *
     * Nel primo caso viene eseguita una sola volta in transazione, nel secondo viene eseguita una volta per ogni
     * associazione registro-tipo ud esistente per quel tipo di unità documentaria
     *
     * @param param
     *            prametri per il logging
     * @param modelloTipoSerie
     *            modello
     * @param registroUnitaDoc
     *            registro unita doc
     * @param tipoUnitaDoc
     *            tipo unita doc
     * @param tipoSerieCreaStandard
     *            tipo serie
     * @param tipiAmmessi
     *            tipi ammessi
     *
     * @throws ParerUserError
     *             errore generico
     */
    private void createTipoSerieStandardDaRegistro(LogParam param, DecModelloTipoSerie modelloTipoSerie,
            DecRegistroUnitaDoc registroUnitaDoc, DecTipoUnitaDoc tipoUnitaDoc, String tipoSerieCreaStandard,
            List<DecTipoUnitaDocAmmesso> tipiAmmessi) throws ParerUserError {
        Date calIstituz = Calendar.getInstance().getTime();
        Calendar calSoppres = Calendar.getInstance();
        calSoppres.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        calSoppres.set(Calendar.MILLISECOND, 0);
        // Popolo il nuovo oggetto tipoSerie in base ai dati del modello
        DecTipoSerie tipoSerie = new DecTipoSerie();
        if (tipoSerie.getDecNotaTipoSeries() == null) {
            tipoSerie.setDecNotaTipoSeries(new ArrayList<DecNotaTipoSerie>());
        }
        if (tipoSerie.getDecTipoSerieUds() == null) {
            tipoSerie.setDecTipoSerieUds(new ArrayList<DecTipoSerieUd>());
        }
        if (tipoSerie.getDecCampoInpUds() == null) {
            tipoSerie.setDecCampoInpUds(new ArrayList<DecCampoInpUd>());
        }

        tipoSerie.setNmTipoSerie(getNmTipoSerieForModello(modelloTipoSerie, registroUnitaDoc, tipoUnitaDoc,
                tipoSerieCreaStandard, null));
        tipoSerie.setDsTipoSerie(getDsTipoSerieForModello(modelloTipoSerie, registroUnitaDoc, tipoUnitaDoc,
                tipoSerieCreaStandard, null));
        tipoSerie.setCdSerieDefault(
                getCdSerieForModello(modelloTipoSerie, registroUnitaDoc, tipoUnitaDoc, tipoSerieCreaStandard, null));
        tipoSerie.setDsSerieDefault(
                getDsSerieForModello(modelloTipoSerie, registroUnitaDoc, tipoUnitaDoc, tipoSerieCreaStandard, null));
        // if
        // (modelloTipoSerie.getTiRglNmTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name()))
        // {
        // tipoSerie.setNmTipoSerie(modelloTipoSerie.getNmTipoSerieDaCreare()
        // + ((tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name()) &&
        // StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) ? " " +
        // registroUnitaDoc.getCdRegistroUnitaDoc() : ""));
        // } else if
        // (modelloTipoSerie.getTiRglNmTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name()))
        // {
        // if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
        // if (StringUtils.isNotBlank(registroUnitaDoc.getNmTipoSerieDaCreare())) {
        // tipoSerie.setNmTipoSerie(registroUnitaDoc.getNmTipoSerieDaCreare());
        // } else {
        // tipoSerie.setNmTipoSerie(nmTipoSerieDaCreare + " " + registroUnitaDoc.getCdRegistroUnitaDoc());
        // }
        // } else {
        // tipoSerie.setNmTipoSerie(nmTipoSerieDaCreare);
        // }
        // }
        // if (modelloTipoSerie.getTiRglDsTipoSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
        // tipoSerie.setDsTipoSerie(modelloTipoSerie.getDsTipoSerieDaCreare());
        // } else if (modelloTipoSerie.getTiRglDsTipoSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
        // if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
        // if (StringUtils.isNotBlank(registroUnitaDoc.getDsTipoSerieDaCreare())) {
        // tipoSerie.setDsTipoSerie(registroUnitaDoc.getDsTipoSerieDaCreare());
        // } else {
        // tipoSerie.setDsTipoSerie(dsTipoSerieDaCreare + " " + registroUnitaDoc.getCdRegistroUnitaDoc());
        // }
        // } else {
        // tipoSerie.setDsTipoSerie(dsTipoSerieDaCreare);
        // }
        // }

        // if (modelloTipoSerie.getTiRglCdSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
        // tipoSerie.setCdSerieDefault(modelloTipoSerie.getCdSerieDaCreare()
        // + ((tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name()) &&
        // StringUtils.isNotBlank(registroUnitaDoc.getCdRegistroUnitaDoc())) ? "_" +
        // registroUnitaDoc.getCdRegistroUnitaDoc() : ""));
        // } else if (modelloTipoSerie.getTiRglCdSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
        // if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
        // if (StringUtils.isNotBlank(registroUnitaDoc.getCdSerieDaCreare())) {
        // tipoSerie.setCdSerieDefault(registroUnitaDoc.getCdSerieDaCreare());
        // } else {
        // tipoSerie.setCdSerieDefault(cdSerieDaCreare + "_" + registroUnitaDoc.getCdRegistroUnitaDoc());
        // }
        // } else {
        // tipoSerie.setCdSerieDefault(cdSerieDaCreare);
        // }
        // }
        // String substitute = tipoSerie.getCdSerieDefault().replaceAll("\\s+", "_");
        // tipoSerie.setCdSerieDefault(substitute);
        // if (modelloTipoSerie.getTiRglDsSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
        // tipoSerie.setDsSerieDefault(modelloTipoSerie.getDsSerieDaCreare());
        // } else if (modelloTipoSerie.getTiRglDsSerie()
        // .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
        // if (tipoSerieCreaStandard.equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
        // if (StringUtils.isNotBlank(registroUnitaDoc.getDsSerieDaCreare())) {
        // tipoSerie.setDsSerieDefault(registroUnitaDoc.getDsSerieDaCreare());
        // } else {
        // tipoSerie.setDsSerieDefault(dsSerieDaCreare + " " + registroUnitaDoc.getCdRegistroUnitaDoc());
        // }
        // } else {
        // tipoSerie.setDsSerieDefault(dsSerieDaCreare);
        // }
        // }
        if (modelloTipoSerie.getTiRglAnniConserv()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            tipoSerie.setNiAnniConserv(modelloTipoSerie.getNiAnniConserv());
        } else if (modelloTipoSerie.getTiRglAnniConserv()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_REG.name())) {
            tipoSerie.setNiAnniConserv(registroUnitaDoc.getNiAnniConserv());
        }

        if (modelloTipoSerie.getTiRglConservazioneSerie()
                .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
            tipoSerie.setTiConservazioneSerie(modelloTipoSerie.getTiConservazioneSerie());
        }

        if (modelloTipoSerie.getTiRglRangeAnniCreaAutom() != null) {
            if (modelloTipoSerie.getTiRglRangeAnniCreaAutom()
                    .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
                tipoSerie.setAaIniCreaAutom(modelloTipoSerie.getAaIniCreaAutom());
                tipoSerie.setAaFinCreaAutom(modelloTipoSerie.getAaFinCreaAutom());
            } else if (modelloTipoSerie.getTiRglRangeAnniCreaAutom()
                    .equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_REG.name())) {
                List<DecAaRegistroUnitaDoc> decAARegistroUnitaDocList = registroHelper
                        .getDecAARegistroUnitaDocList(new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()));
                if (decAARegistroUnitaDocList != null && !decAARegistroUnitaDocList.isEmpty()) {
                    tipoSerie.setAaIniCreaAutom(decAARegistroUnitaDocList.get(0).getAaMinRegistroUnitaDoc());
                }
                tipoSerie.setAaFinCreaAutom(null);
            }
        }

        tipoSerie.setDtIstituz(calIstituz);
        tipoSerie.setDtSoppres(calSoppres.getTime());
        tipoSerie.setFlTipoSeriePadre("0");
        tipoSerie.setDecModelloTipoSerie(modelloTipoSerie);
        tipoSerie.setTiCreaStandard(tipoSerieCreaStandard);
        tipoSerie.setTipoContenSerie(CostantiDB.TipoContenSerie.UNITA_DOC.name());
        tipoSerie.setTiSelUd(modelloTipoSerie.getTiSelUd());
        tipoSerie.setNiAaSelUd(modelloTipoSerie.getNiAaSelUd());
        tipoSerie.setNiAaSelUdSuc(modelloTipoSerie.getNiAaSelUdSuc());
        tipoSerie.setNiUnitaDocVolume(modelloTipoSerie.getNiUnitaDocVolume());
        tipoSerie.setFlControlloConsistObblig(modelloTipoSerie.getFlControlloConsistObblig());
        tipoSerie.setFlCreaAutom(modelloTipoSerie.getFlCreaAutom());
        tipoSerie.setGgCreaAutom(modelloTipoSerie.getGgCreaAutom());
        tipoSerie.setNiMmCreaAutom(modelloTipoSerie.getNiMmCreaAutom());
        tipoSerie.setOrgStrut(registroUnitaDoc.getOrgStrut());
        tipoSerie.setTiStatoVerSerieAutom(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name());
        // Creo eventuali campi note nel caso siano presenti nel modello
        for (DecNotaModelloTipoSerie notaModello : modelloTipoSerie.getDecNotaModelloTipoSeries()) {
            DecNotaTipoSerie notaTipoSerie = new DecNotaTipoSerie();
            notaTipoSerie.setDecTipoNotaSerie(notaModello.getDecTipoNotaSerie());
            notaTipoSerie.setPgNotaTipoSerie(notaModello.getPgNotaTipoSerie());
            notaTipoSerie.setDtNotaTipoSerie(calIstituz);
            notaTipoSerie.setDsNotaTipoSerie(notaModello.getDsNotaTipoSerie());
            notaTipoSerie.setIamUser(notaModello.getIamUser());
            tipoSerie.addDecNotaTipoSery(notaTipoSerie);
        }
        // Creo le associazioni registri-tipi ud
        Set<String> nmTipoDocs = new HashSet<>();
        for (DecTipoUnitaDocAmmesso tipoUdAmmesso : tipiAmmessi) {
            DecTipoSerieUd tipoSerieUd = new DecTipoSerieUd();
            tipoSerieUd.setFlSelUnitaDocAnnul("0");
            tipoSerieUd.setDecTipoUnitaDoc(tipoUdAmmesso.getDecTipoUnitaDoc());
            tipoSerieUd.setDecRegistroUnitaDoc(tipoUdAmmesso.getDecRegistroUnitaDoc());
            tipoSerie.addDecTipoSerieUd(tipoSerieUd);
            if (tipoSerieUd.getDecOutSelUds() == null) {
                tipoSerieUd.setDecOutSelUds(new ArrayList<DecOutSelUd>());
            }
            if (tipoSerieUd.getDecFiltroSelUds() == null) {
                tipoSerieUd.setDecFiltroSelUds(new ArrayList<DecFiltroSelUd>());
            }
            if (tipoSerieUd.getDecFiltroSelUdAttbs() == null) {
                tipoSerieUd.setDecFiltroSelUdAttbs(new ArrayList<DecFiltroSelUdAttb>());
            }
            // Creo le regole di filtraggio
            if (modelloTipoSerie.getTiRglFiltroTiDoc()
                    .equals(CostantiDB.TipoRegolaModelloTipoSerie.DEFINITO_NEL_MODELLO.name())) {
                if (modelloTipoSerie.getDecTipoDoc() != null
                        && !modelloTipoSerie.getDecModelloFiltroTiDocs().isEmpty()) {
                    throw new ParerUserError(
                            "Errore su modello: Sono specificati sia il tipo documento principale sia la lista di regole di filtraggio");
                } else if (modelloTipoSerie.getDecTipoDoc() != null) {
                    // Caso tipo documento principale
                    DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(
                            modelloTipoSerie.getDecTipoDoc().getNmTipoDoc(),
                            registroUnitaDoc.getOrgStrut().getIdStrut());
                    if (tipoDoc == null) {
                        throw new ParerUserError(
                                "Sul modello \u00E8 indicato un tipo documento non previsto nella struttura: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    } else if (!tipoDoc.getFlTipoDocPrincipale().equals("1")) {
                        throw new ParerUserError(
                                "Sul modello \u00E8 indicato un tipo documento non principale: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    } else if (tipoDocHelper.countDecTipoDocPrincipalePerTipoUnitaDoc(
                            tipoUdAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), tipoDoc.getIdTipoDoc()) == 0) {
                        throw new ParerUserError(
                                "Sul modello \u00E8 indicato un tipo documento non ammesso per la tipologia di unit\u00E0 documentaria: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    } else {
                        nmTipoDocs.add(tipoDoc.getNmTipoDoc());
                        createDecFiltroSelUd(tipoSerieUd, CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name(),
                                BigDecimal.ONE, tipoDoc);
                    }
                } else if (!modelloTipoSerie.getDecModelloFiltroTiDocs().isEmpty()) {
                    // Caso regole di filtraggio
                    BigDecimal pg = BigDecimal.ZERO;
                    for (DecModelloFiltroTiDoc reg : modelloTipoSerie.getDecModelloFiltroTiDocs()) {
                        pg = pg.add(BigDecimal.ONE);
                        DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(reg.getNmTipoDoc(),
                                registroUnitaDoc.getOrgStrut().getIdStrut());
                        if (tipoDoc == null) {
                            throw new ParerUserError(
                                    "Il modello di tipo serie prevede una regola di filtraggio su un nome di documento non previsto nella struttura. Impossibile procedere alla creazione del tipo serie "
                                            + tipoSerie.getNmTipoSerie());
                        } else if (!tipoDoc.getFlTipoDocPrincipale().equals("1")) {
                            throw new ParerUserError(
                                    "Sul modello \u00E8 indicata una regola di filtraggio su un tipo documento non principale: impossibile procedere alla creazione del tipo serie "
                                            + tipoSerie.getNmTipoSerie());
                        } else if (tipoDocHelper.countDecTipoDocPrincipalePerTipoUnitaDoc(
                                tipoUdAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), tipoDoc.getIdTipoDoc()) == 0) {
                            throw new ParerUserError(
                                    "Sul modello \u00E8 indicata una regola di filtraggio su un tipo documento non ammesso per la tipologia di unit\u00E0 documentaria: impossibile procedere alla creazione del tipo serie "
                                            + tipoSerie.getNmTipoSerie());
                        } else {
                            nmTipoDocs.add(tipoDoc.getNmTipoDoc());
                            createDecFiltroSelUd(tipoSerieUd, CostantiDB.TipoFiltroSerieUd.TIPO_DOC_PRINC.name(), pg,
                                    tipoDoc);
                        }
                    }
                }
            }
            // Creo i filtri sui dati specifici
            for (DecModelloFiltroSelUdattb attrib : modelloTipoSerie.getDecModelloFiltroSelUdattbs()) {
                CostantiDB.TipoCampo tipoFiltro = CostantiDB.TipoCampo.valueOf(attrib.getTiFiltro());
                DecAttribDatiSpec datoSpec;
                switch (tipoFiltro) {
                case DATO_SPEC_DOC_PRINC:
                    if (modelloTipoSerie.getDecTipoDoc() != null) {
                        nmTipoDocs.add(modelloTipoSerie.getDecTipoDoc().getNmTipoDoc());
                    } else {
                        for (DecModelloFiltroTiDoc reg : modelloTipoSerie.getDecModelloFiltroTiDocs()) {
                            DecTipoDoc tipoDoc = tipoDocHelper.getDecTipoDocByName(reg.getNmTipoDoc(),
                                    registroUnitaDoc.getOrgStrut().getIdStrut());
                            if (tipoDoc == null) {
                                throw new ParerUserError(
                                        "Il modello di tipo serie prevede una regola di filtraggio su un nome di documento non previsto nella struttura. Impossibile procedere alla creazione del tipo serie "
                                                + tipoSerie.getNmTipoSerie());
                            } else {
                                nmTipoDocs.add(tipoDoc.getNmTipoDoc());
                            }
                        }
                    }
                    for (String nmTipoDoc : nmTipoDocs) {
                        datoSpec = datiSpecificiHelper.getDecAttribDatiSpecByName(
                                registroUnitaDoc.getOrgStrut().getIdStrut(), attrib.getNmFiltro(),
                                CostantiDB.TipiEntitaSacer.DOC.name(), CostantiDB.TipiUsoDatiSpec.VERS.name(), null,
                                nmTipoDoc, null, null);
                        if (datoSpec != null) {
                            createDecFiltroSelUdAttb(tipoSerieUd, attrib, datoSpec,
                                    tipoUdAmmesso.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                        } else {
                            throw new ParerUserError(
                                    "Sul modello \u00E8 stato indicato come filtro su dati specifici un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                            + tipoSerie.getNmTipoSerie());
                        }
                    }
                    break;
                case DATO_SPEC_UNI_DOC:
                    datoSpec = datiSpecificiHelper.getDecAttribDatiSpecById(registroUnitaDoc.getOrgStrut().getIdStrut(),
                            attrib.getNmFiltro(), CostantiDB.TipiEntitaSacer.UNI_DOC.name(),
                            CostantiDB.TipiUsoDatiSpec.VERS.name(),
                            tipoUdAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), null, null, null);
                    if (datoSpec != null) {
                        createDecFiltroSelUdAttb(tipoSerieUd, attrib, datoSpec,
                                tipoUdAmmesso.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    } else {
                        throw new ParerUserError(
                                "Sul modello \u00E8 stato indicato come filtro su dati specifici un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    }
                    break;
                }
            }
            // Creo le regole di rappresentazione
            for (DecModelloOutSelUd modelloOutSelUd : modelloTipoSerie.getDecModelloOutSelUds()) {
                DecOutSelUd outSelUd = new DecOutSelUd();
                outSelUd.setDlFormatoOut(modelloOutSelUd.getDlFormatoOut());
                outSelUd.setTiOut(modelloOutSelUd.getTiOut());
                tipoSerieUd.addDecOutSelUd(outSelUd);
                if (outSelUd.getDecCampoOutSelUds() == null) {
                    outSelUd.setDecCampoOutSelUds(new ArrayList<DecCampoOutSelUd>());
                }

                for (DecModelloCampoOutSelUd modelloCampo : modelloOutSelUd.getDecModelloCampoOutSelUds()) {
                    DecCampoOutSelUd campoOutSelUd = new DecCampoOutSelUd();
                    campoOutSelUd.setTiCampo(modelloCampo.getTiCampo());
                    campoOutSelUd.setNmCampo(modelloCampo.getNmCampo());
                    campoOutSelUd.setTiTrasformCampo(modelloCampo.getTiTrasformCampo());

                    DecAttribDatiSpec datoSpec;
                    CostantiDB.TipoCampo tipoCampo = CostantiDB.TipoCampo.valueOf(modelloCampo.getTiCampo());
                    switch (tipoCampo) {
                    case DATO_SPEC_DOC_PRINC:
                        for (String nmTipoDoc : nmTipoDocs) {
                            datoSpec = datiSpecificiHelper.getDecAttribDatiSpecByName(
                                    registroUnitaDoc.getOrgStrut().getIdStrut(), modelloCampo.getNmCampo(),
                                    CostantiDB.TipiEntitaSacer.DOC.name(), CostantiDB.TipiUsoDatiSpec.VERS.name(), null,
                                    nmTipoDoc, null, null);
                            if (datoSpec != null) {
                                campoOutSelUd.setDecAttribDatiSpec(datoSpec);
                            } else {
                                throw new ParerUserError(
                                        "Sul modello \u00E8 stato indicato come regola di rappresentazione un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                                + tipoSerie.getNmTipoSerie());
                            }
                        }
                        break;
                    case DATO_SPEC_UNI_DOC:
                        datoSpec = datiSpecificiHelper.getDecAttribDatiSpecById(
                                registroUnitaDoc.getOrgStrut().getIdStrut(), modelloCampo.getNmCampo(),
                                CostantiDB.TipiEntitaSacer.UNI_DOC.name(), CostantiDB.TipiUsoDatiSpec.VERS.name(),
                                tipoUdAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), null, null, null);
                        if (datoSpec != null) {
                            campoOutSelUd.setDecAttribDatiSpec(datoSpec);
                        } else {
                            throw new ParerUserError(
                                    "Sul modello \u00E8 stato indicato come regola di rappresentazione un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                            + tipoSerie.getNmTipoSerie());
                        }
                        break;
                    }

                    outSelUd.addDecCampoOutSelUd(campoOutSelUd);
                }
            }
        }
        // Creo le regole di acquisizione
        for (DecModelloCampoInpUd modelloCampoInpUd : modelloTipoSerie.getDecModelloCampoInpUds()) {
            DecCampoInpUd campoInpUd = new DecCampoInpUd();
            campoInpUd.setNmCampo(modelloCampoInpUd.getNmCampo());
            campoInpUd.setPgOrdCampo(modelloCampoInpUd.getPgOrdCampo());
            campoInpUd.setTiCampo(modelloCampoInpUd.getTiCampo());
            campoInpUd.setTiTrasformCampo(modelloCampoInpUd.getTiTrasformCampo());
            DecAttribDatiSpec datoSpec;
            CostantiDB.TipoCampo tipoCampo = CostantiDB.TipoCampo.valueOf(modelloCampoInpUd.getTiCampo());
            switch (tipoCampo) {
            case DATO_SPEC_DOC_PRINC:
                if (nmTipoDocs.isEmpty()) {
                    throw new ParerUserError(
                            "Sul modello \u00E8 stato indicato come regola di acquisizione un dato specifico ma sul modello non \u00E8 stato indicato il filtro sui tipi di documento principale: impossibile procedere alla creazione del tipo serie "
                                    + tipoSerie.getNmTipoSerie());
                } else if (nmTipoDocs.size() > 1) {
                    throw new ParerUserError(
                            "Sul modello \u00E8 stato indicato come regola di acquisizione un dato specifico ma sul tipo serie \u00E8 presente pi\u00F9 di un documento principale definito come regola di filtraggio: impossibile procedere alla creazione del tipo serie "
                                    + tipoSerie.getNmTipoSerie());
                }
                for (String nmTipoDoc : nmTipoDocs) {
                    datoSpec = datiSpecificiHelper.getDecAttribDatiSpecByName(
                            registroUnitaDoc.getOrgStrut().getIdStrut(), modelloCampoInpUd.getNmCampo(),
                            CostantiDB.TipiEntitaSacer.DOC.name(), CostantiDB.TipiUsoDatiSpec.VERS.name(), null,
                            nmTipoDoc, null, null);
                    if (datoSpec != null) {
                        campoInpUd.setDecAttribDatiSpec(datoSpec);
                    } else {
                        throw new ParerUserError(
                                "Sul modello \u00E8 stato indicato come regola di rappresentazione un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    }
                }
                break;
            case DATO_SPEC_UNI_DOC:
                if (!tipiAmmessi.isEmpty()) {
                    if (tipiAmmessi.size() > 1) {
                        throw new ParerUserError(
                                "Sul modello \u00E8 stato indicato come regola di acquisizione un dato specifico ma sul tipo serie \u00E8 presente pi\u00F9 di un'associazione registro - tipo unit\u00E0 documentaria: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    }
                    datoSpec = datiSpecificiHelper.getDecAttribDatiSpecById(registroUnitaDoc.getOrgStrut().getIdStrut(),
                            modelloCampoInpUd.getNmCampo(), CostantiDB.TipiEntitaSacer.UNI_DOC.name(),
                            CostantiDB.TipiUsoDatiSpec.VERS.name(),
                            tipiAmmessi.get(0).getDecTipoUnitaDoc().getIdTipoUnitaDoc(), null, null, null);
                    if (datoSpec != null) {
                        campoInpUd.setDecAttribDatiSpec(datoSpec);
                    } else {
                        throw new ParerUserError(
                                "Sul modello \u00E8 stato indicato come regola di acquisizione un dato specifico non presente sulla struttura: impossibile procedere alla creazione del tipo serie "
                                        + tipoSerie.getNmTipoSerie());
                    }
                }
                break;
            }

            tipoSerie.addDecCampoInpUd(campoInpUd);
        }

        tipoSerieHelper.insertEntity(tipoSerie, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_SERIE,
                new BigDecimal(tipoSerie.getIdTipoSerie()), param.getNomePagina());

        // Se sul tipo serie CORRENTE esiste più di un'associazione per quel registro (che sia tramite DecTipoSerieUd o
        // DecTipoUnitaDocAmmesso non cambia),
        // setto a 1 il fl_tipo_serie_mult in DecRegistroUnitaDoc
        // List<DecTipoSerieUd> associazioniRegistroTipoUd = tipoSerie.getDecTipoSerieUds();
        // for (DecTipoSerieUd associazione : associazioniRegistroTipoUd) {
        // if (existDecTipoSerieUdForRegistroAndTipoSerie(new
        // BigDecimal(associazione.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()), new
        // BigDecimal(tipoSerie.getIdTipoSerie()))) {
        // associazione.getDecRegistroUnitaDoc().setFlTipoSerieMult("1");
        // }
        // }
        List<DecTipoSerieUd> associazioniRegistroTipoUd = tipoSerie.getDecTipoSerieUds();
        for (DecTipoSerieUd associazione : associazioniRegistroTipoUd) {
            if (tipoSerieHelper.multipleDecRegistroUnitaDocInTipiSerie(
                    new BigDecimal(associazione.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()))) {
                associazione.getDecRegistroUnitaDoc().setFlTipoSerieMult("1");
            }
        }
    }

    private void createDecFiltroSelUd(DecTipoSerieUd decTipoSerieUd, String tiFiltro, BigDecimal pgFiltro,
            DecTipoDoc decTipoDoc) {
        DecFiltroSelUd filtro = new DecFiltroSelUd();
        filtro.setTiFiltro(tiFiltro);
        filtro.setPgFiltro(pgFiltro);
        filtro.setDecTipoDoc(decTipoDoc);
        decTipoSerieUd.addDecFiltroSelUd(filtro);
    }

    private void createDecFiltroSelUdAttb(DecTipoSerieUd decTipoSerieUd, DecModelloFiltroSelUdattb attrib,
            DecAttribDatiSpec datoSpec, String nmTipoUdDoc) {
        DecFiltroSelUdAttb filtro = new DecFiltroSelUdAttb();
        filtro.setNmAttribDatiSpec(attrib.getNmFiltro());
        filtro.setTiOper(attrib.getTiOper());
        filtro.setDlValore(attrib.getDlValore());
        if (filtro.getDecFiltroSelUdDatos() == null) {
            filtro.setDecFiltroSelUdDatos(new ArrayList<DecFiltroSelUdDato>());
        }

        DecFiltroSelUdDato filtroDato = new DecFiltroSelUdDato();
        filtroDato.setDecAttribDatiSpec(datoSpec);
        filtroDato.setTiEntitaSacer(datoSpec.getTiEntitaSacer());
        if (datoSpec.getTiEntitaSacer().equals(CostantiDB.TipiEntitaSacer.UNI_DOC.name())) {
            filtroDato.setNmTipoUnitaDoc(nmTipoUdDoc);
        } else if (datoSpec.getTiEntitaSacer().equals(CostantiDB.TipiEntitaSacer.DOC.name())) {
            filtroDato.setNmTipoDoc(nmTipoUdDoc);
        }
        StringBuilder versioniXsd = new StringBuilder();
        for (DecXsdAttribDatiSpec xsdAttrib : datoSpec.getDecXsdAttribDatiSpecs()) {
            versioniXsd.append(xsdAttrib.getDecXsdDatiSpec().getCdVersioneXsd()).append(",");
        }
        filtroDato.setDsListaVersioniXsd(StringUtils.chop(versioniXsd.toString()));
        filtro.addDecFiltroSelUdDato(filtroDato);

        decTipoSerieUd.addDecFiltroSelUdAttb(filtro);
    }

    public DecTipoUnitaDocTableBean getDecTipoUnitaDocTableBeanFromTipoSerie(BigDecimal idStrut) {
        return getDecTipoUnitaDocTableBeanFromTipoSerie(idStrut, null);
    }

    public DecTipoUnitaDocTableBean getDecTipoUnitaDocTableBeanFromTipoSerie(BigDecimal idStrut,
            BigDecimal idTipoSerie) {
        DecTipoUnitaDocTableBean table = new DecTipoUnitaDocTableBean();
        List<DecTipoUnitaDoc> list = tipoUnitaDocHelper.retrieveDecTipoUnitaDocsFromTipoSerie(idStrut, idTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecTipoUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista tipologie ud abilitate "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
            }
        }
        return table;
    }

    public DecRegistroUnitaDocTableBean getDecRegistroUnitaDocTableBeanFromTipoSerie(BigDecimal idStrut) {
        return getDecRegistroUnitaDocTableBeanFromTipoSerie(idStrut, null);
    }

    public DecRegistroUnitaDocTableBean getDecRegistroUnitaDocTableBeanFromTipoSerie(BigDecimal idStrut,
            BigDecimal idTipoSerie) {
        DecRegistroUnitaDocTableBean table = new DecRegistroUnitaDocTableBean();
        List<DecRegistroUnitaDoc> list = registroHelper.retrieveDecRegistroUnitaDocsFromTipoSerie(idStrut, idTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecRegistroUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista registri abilitati "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
            }
        }
        return table;
    }

    public boolean existDecTipoSerieForIdModello(BigDecimal idModelloTipoSerie) {
        return tipoSerieHelper.countDecTipoSerie(idModelloTipoSerie) > 0L;
    }

    public boolean existDecTipoSerieUdForRegistro(BigDecimal idRegistroUnitaDoc) {
        return tipoSerieHelper.existDecTipoSerieUdForRegistro(idRegistroUnitaDoc);
    }

    // public boolean existDecTipoSerieUdForRegistroAndTipoSerie(BigDecimal idRegistroUnitaDoc, BigDecimal idTipoSerie)
    // {
    // return tipoSerieHelper.existDecTipoSerieUdForRegistroAndTipoSerie(idRegistroUnitaDoc, idTipoSerie);
    // }
    // public Long countDecTipoSerieUdForRegistroAndTipoSerie(BigDecimal idRegistroUnitaDoc, BigDecimal idTipoSerie) {
    // return tipoSerieHelper.countDecTipoSerieUdForRegistroAndTipoSerie(idRegistroUnitaDoc, idTipoSerie);
    // }
    public boolean multipleDecRegistroUnitaDocInTipiSerie(BigDecimal idRegistroUnitaDoc) {
        return tipoSerieHelper.multipleDecRegistroUnitaDocInTipiSerie(idRegistroUnitaDoc);
    }
}
