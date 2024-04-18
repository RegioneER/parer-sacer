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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelper;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecModelloCampoInpUd;
import it.eng.parer.entity.DecModelloCampoOutSelUd;
import it.eng.parer.entity.DecModelloFiltroSelUdattb;
import it.eng.parer.entity.DecModelloFiltroTiDoc;
import it.eng.parer.entity.DecModelloOutSelUd;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecNotaModelloTipoSerie;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoNotaSerie;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecUsoModelloTipoSerie;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.dto.CreazioneModelloSerieBean;
import it.eng.parer.serie.helper.ModelliSerieHelper;
import it.eng.parer.serie.helper.TipoSerieHelper;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoInpUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoInpUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoInpUdTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloCampoOutSelUdTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroSelUdattbRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroSelUdattbTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroSelUdattbTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroTiDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloFiltroTiDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloOutSelUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloOutSelUdTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecNotaModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecNotaModelloTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloTipoSerieRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloTipoSerieTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoIFace.Values;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("rawtypes")
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ModelliSerieEjb {

    private static final Logger logger = LoggerFactory.getLogger(ModelliSerieEjb.class);

    @EJB
    private ModelliSerieHelper helper;
    @EJB
    private TipoSerieHelper tipoSerieHelper;
    @EJB
    private TipoUnitaDocHelper tipoUdHelper;
    @EJB
    private RegistroHelper registroHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private SacerLogEjb sacerLogEjb;

    /**
     * Ritorna il tableBean la lista di modelli abilitati per l'ambiente + tutti i modelli in uso per la struttura in
     * input
     *
     * @param idStrut
     *            id struttura
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * 
     * @return entity DecModelloTipoSerieTableBean
     */
    public DecModelloTipoSerieTableBean getDecModelloTipoSerieAllAbilitatiTableBean(BigDecimal idStrut,
            boolean filterValid) {
        DecModelloTipoSerieTableBean table = new DecModelloTipoSerieTableBean();
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
        List<DecModelloTipoSerie> allModelli = helper.retrieveDecModelloTipoSerie(
                strut.getOrgEnte().getOrgAmbiente().getIdAmbiente(), idStrut.longValue(), filterValid);
        List<DecModelloTipoSerie> usoModelli = helper.retrieveDecModelloTipoSerieFromDecUsoModello(idStrut,
                filterValid);
        if (!allModelli.isEmpty() || !usoModelli.isEmpty()) {
            try {

                Set<DecModelloTipoSerie> modelli = new HashSet<>();
                modelli.addAll(allModelli);
                modelli.addAll(usoModelli);

                for (DecModelloTipoSerie modello : modelli) {
                    DecModelloTipoSerieRowBean row = (DecModelloTipoSerieRowBean) Transform.entity2RowBean(modello);
                    row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
                    row.setString("nm_tipo_doc_dati_spec",
                            modello.getDecTipoDoc() != null ? modello.getDecTipoDoc().getNmTipoDoc() : null);
                    row.setString("nm_tipo_unita_doc_dati_spec", modello.getDecTipoUnitaDoc() != null
                            ? modello.getDecTipoUnitaDoc().getNmTipoUnitaDoc() : null);
                    table.add(row);
                }

                table.addSortingRule("nm_modello_tipo_serie", SortingRule.ASC);
                table.sort();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della lista di modelli abilitati per la struttura "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean la lista di modelli abilitati per l'ambiente
     *
     * @param idAmbiente
     *            id ambiente
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * 
     * @return entity bean DecModelloTipoSerieTableBean
     */
    public DecModelloTipoSerieTableBean getDecModelloTipoSerieAbilitatiAmbienteTableBean(BigDecimal idAmbiente,
            boolean filterValid) {
        DecModelloTipoSerieTableBean table = new DecModelloTipoSerieTableBean();
        List<DecModelloTipoSerie> allModelli = helper.retrieveDecModelloTipoSerie(idAmbiente.longValue(), null,
                filterValid);
        if (!allModelli.isEmpty()) {
            try {

                Set<DecModelloTipoSerie> modelli = new HashSet<>();
                modelli.addAll(allModelli);
                for (DecModelloTipoSerie modello : modelli) {
                    DecModelloTipoSerieRowBean row = (DecModelloTipoSerieRowBean) Transform.entity2RowBean(modello);
                    row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
                    row.setString("nm_tipo_doc_dati_spec",
                            modello.getDecTipoDoc() != null ? modello.getDecTipoDoc().getNmTipoDoc() : null);
                    row.setString("nm_tipo_unita_doc_dati_spec", modello.getDecTipoUnitaDoc() != null
                            ? modello.getDecTipoUnitaDoc().getNmTipoUnitaDoc() : null);
                    table.add(row);
                }

                table.addSortingRule("nm_modello_tipo_serie", SortingRule.ASC);
                table.sort();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della lista di modelli abilitati per l'ambiente "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Esegue i controlli relativi al modello di tipo di serie per il salvataggio di un registro
     *
     * @param idStrut
     *            la struttura relativa al salvataggio
     * @param idModelloTipoSerie
     *            il modello selezionato dall'utente
     * @param tipoSerie
     *            tipo serie scelto dall'utente
     * @param descrizioneTipoSerie
     *            descrizione tipo serie scelto dall'utente
     * @param codiceSerie
     *            codice serie scelto dall'utente
     * @param descrizioneSerie
     *            descrizione serie scelto dall'utente
     * @param idRegistroUnitaDoc
     *            id registro in modifica
     * @param idTipoUnitaDoc
     *            id tipo ud in modifica
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void checkModelloTipoUdRegistroPerSerie(BigDecimal idStrut, BigDecimal idModelloTipoSerie, String tipoSerie,
            String descrizioneTipoSerie, String codiceSerie, String descrizioneSerie, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) throws ParerUserError {
        DecModelloTipoSerie modello = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
        if (modello.getTiRglNmTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (StringUtils.isBlank(tipoSerie)) {
                throw new ParerUserError(
                        "\u00C8 necessario indicare la tipologia serie: il modello di tipo serie selezionato prevede che la tipologia serie sia 'EREDITA_DA_TIPO_UD_REG'");
            }
            DecTipoSerie decTipoSerie = tipoSerieHelper.getDecTipoSerieByName(tipoSerie, idStrut.longValue());
            if (decTipoSerie != null) {
                throw new ParerUserError(
                        "Nella struttura \u00E8 gi\u00E0 presente un tipo di serie con il campo Tipologia serie valorizzato con '"
                                + tipoSerie + "'");
            }
            if (checkTipoUdRegNmTipoSerieDaCreare(tipoSerie, idStrut, idRegistroUnitaDoc, idTipoUnitaDoc)) {
                throw new ParerUserError(
                        "Nella struttura \u00E8 gi\u00E0 presente un tipo di unit\u00E0 documentaria o un registro con il campo 'Tipologia serie' valorizzato con '"
                                + tipoSerie + "'");
            }
        }
        if (modello.getTiRglCdSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (StringUtils.isBlank(codiceSerie)) {
                throw new ParerUserError(
                        "\u00C8 necessario indicare il codice serie: il modello di tipo serie selezionato prevede che il codice serie sia 'EREDITA_DA_TIPO_UD_REG'");
            }
            if (!serieEjb.checkCdSerie(codiceSerie)) {
                throw new ParerUserError(
                        "Caratteri consentiti per il codice serie: lettere, numeri, punto, meno, underscore, due punti");
            }
            if (checkTipoUdRegCdSerieDaCreare(codiceSerie, idStrut, idRegistroUnitaDoc, idTipoUnitaDoc)) {
                throw new ParerUserError(
                        "Nella struttura \u00E8 gi\u00E0 presente un tipo di unit\u00E0 documentaria o un registro con il campo 'Codice serie' valorizzato con '"
                                + codiceSerie + "'");
            }
        }
        if (modello.getTiRglDsTipoSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (StringUtils.isBlank(descrizioneTipoSerie)) {
                throw new ParerUserError(
                        "\u00C8 necessario indicare la descrizione del tipo serie: il modello di tipo serie selezionato prevede che la descrizione del tipo serie sia 'EREDITA_DA_TIPO_UD_REG'");
            }
        }
        if (modello.getTiRglDsSerie().equals(CostantiDB.TipoRegolaModelloTipoSerie.EREDITA_DA_TIPO_UD_REG.name())) {
            if (StringUtils.isBlank(descrizioneSerie)) {
                throw new ParerUserError(
                        "\u00C8 necessario indicare la descrizione della serie: il modello di tipo serie selezionato prevede che la descrizione della serie sia 'EREDITA_DA_TIPO_UD_REG'");
            }
        }
    }

    /**
     * Verifica che il nome <code>nmTipoSerieDaCreare</code> indicato non sia già stato indicato come
     * "nm_tipo_serie_da_creare" su un'altra tipologia di ud o su un registro nella struttura data in input, escludendo
     * il registro o il tipo unità documentaria in modifica
     *
     * @param nmTipoSerieDaCreare
     *            nome tipo serie da creare
     * @param idStrut
     *            id struttura
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param idTipoUnitaDoc
     *            tipo unita doc
     * 
     * @return true se esiste
     */
    public boolean checkTipoUdRegNmTipoSerieDaCreare(String nmTipoSerieDaCreare, BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) {
        boolean regs = registroHelper.checkRegistroUnitaDocByCampoStringa("nmTipoSerieDaCreare", nmTipoSerieDaCreare,
                idStrut, idRegistroUnitaDoc);
        boolean tipiUd = tipoUdHelper.checkTipoUnitaDocByCampoStringa("nmTipoSerieDaCreare", nmTipoSerieDaCreare,
                idStrut, idTipoUnitaDoc);
        return (regs || tipiUd);
    }

    /**
     * Verifica che il nome <code>cdSerieDaCreare</code> indicato non sia già stato indicato come "cd_serie_da_creare"
     * su un'altra tipologia di ud o su un registro nella struttura data in input, escludendo il registro o il tipo
     * unità documentaria in modifica
     *
     * @param cdSerieDaCreare
     *            serie da creare
     * @param idStrut
     *            id struttura
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * 
     * @return true se esiste
     */
    public boolean checkTipoUdRegCdSerieDaCreare(String cdSerieDaCreare, BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) {
        boolean regs = registroHelper.checkRegistroUnitaDocByCampoStringa("cdSerieDaCreare", cdSerieDaCreare, idStrut,
                idRegistroUnitaDoc);
        boolean tipiUd = tipoUdHelper.checkTipoUnitaDocByCampoStringa("cdSerieDaCreare", cdSerieDaCreare, idStrut,
                idTipoUnitaDoc);
        return (regs || tipiUd);
    }

    /**
     * Ritorna il tableBean contenente la lista di elementi di descrizione del modello dato in input
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecNotaModelloTipoSerieTableBean getDecNotaModelloTipoSerieTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecNotaModelloTipoSerieTableBean table = new DecNotaModelloTipoSerieTableBean();
        List<DecNotaModelloTipoSerie> list = helper.retrieveDecNotaModelloTipoSerie(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                for (DecNotaModelloTipoSerie nota : list) {
                    DecNotaModelloTipoSerieRowBean row = (DecNotaModelloTipoSerieRowBean) Transform
                            .entity2RowBean(nota);
                    row.setString("nm_userid", nota.getIamUser().getNmUserid());
                    row.setString("cd_tipo_nota_serie", nota.getDecTipoNotaSerie().getCdTipoNotaSerie());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di elementi di descrizione dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il massimo progressivo creato per il modello di tipo serie e il tipo di nota dati in input
     *
     * @param idModelloTipoSerie
     *            id modello tipo serie
     * @param idTipoNotaSerie
     *            nota serie
     * 
     * @return il massimo progressivo
     */
    public BigDecimal getMaxPgNotaModelloTipoSerie(BigDecimal idModelloTipoSerie, BigDecimal idTipoNotaSerie) {
        BigDecimal pg = helper.getMaxPgDecNotaModelloSerie(idModelloTipoSerie, idTipoNotaSerie);
        return pg != null ? pg : BigDecimal.ZERO;
    }

    /**
     * Metodo di insert di una nota di modello di tipo serie
     *
     * @param param
     *            parametri per il loggin
     * @param idUtente
     *            id utente
     * @param idModelloTipoSerie
     *            id modello tipo serie
     * @param idTipoNotaSerie
     *            nota serie
     * @param pgNota
     *            progressivo nota
     * @param dsNota
     *            descrizione nota
     * @param dtNota
     *            data nota
     * 
     * @return BigDecimal pk nota
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveNotaModelloTipoSerie(LogParam param, long idUtente, BigDecimal idModelloTipoSerie,
            BigDecimal idTipoNotaSerie, BigDecimal pgNota, String dsNota, Date dtNota) throws ParerUserError {
        logger.debug("Eseguo il salvataggio della nota");
        BigDecimal id = null;
        try {
            DecModelloTipoSerie modello = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            if (modello.getDecNotaModelloTipoSeries() == null) {
                modello.setDecNotaModelloTipoSeries(new ArrayList<DecNotaModelloTipoSerie>());
            }
            DecNotaModelloTipoSerie nota = new DecNotaModelloTipoSerie();
            nota.setDecTipoNotaSerie(helper.findById(DecTipoNotaSerie.class, idTipoNotaSerie));
            nota.setIamUser(helper.findById(IamUser.class, idUtente));
            nota.setPgNotaTipoSerie(pgNota);
            nota.setDsNotaTipoSerie(dsNota);
            nota.setDtNotaTipoSerie(dtNota);

            modello.addDecNotaModelloTipoSery(nota);
            helper.insertEntity(nota, true);
            id = new BigDecimal(nota.getIdNotaModelloTipoSerie());
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(nota.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'elemento di descrizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
        return id;
    }

    /**
     * Metodo di update di una nota di modello di tipo serie
     *
     * @param param
     *            parametri per il logging
     * @param idNotaModelloTipoSerie
     *            id nota tipo serie
     * @param dsNota
     *            descrizione nota
     * @param idUtente
     *            id utente
     * @param dtNota
     *            data nota
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNotaModelloTipoSerie(LogParam param, BigDecimal idNotaModelloTipoSerie, String dsNota,
            long idUtente, Date dtNota) throws ParerUserError {
        logger.debug("Eseguo il salvataggio dell'elemento di descrizione");
        try {
            DecNotaModelloTipoSerie nota = helper.findById(DecNotaModelloTipoSerie.class, idNotaModelloTipoSerie);
            nota.setDsNotaTipoSerie(dsNota);
            nota.setIamUser(helper.findById(IamUser.class, idUtente));
            nota.setDtNotaTipoSerie(dtNota);
            helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(nota.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'elemento di descrizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Metodo di eliminazione di un elemento di descrizione del modello di tipo serie
     * 
     * @param param
     *            parametri per logging
     * @param idNotaModelloTipoSerie
     *            id nota modello serie
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteNotaModelloTipoSerie(LogParam param, BigDecimal idNotaModelloTipoSerie) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione dell'elemento di descrizione");
        try {
            DecNotaModelloTipoSerie nota = helper.findById(DecNotaModelloTipoSerie.class, idNotaModelloTipoSerie);
            helper.removeEntity(nota, true);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(nota.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());

        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione dell'elemento di descrizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di acquisizione del modello dato in input
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloCampoInpUdTableBean getDecModelloCampoInpUdTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        List<DecModelloCampoInpUd> list = helper.retrieveDecModelloCampoInpUd(idModelloTipoSerie);
        return transformDecModelloCampoInpUd(list);
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di acquisizione modificabili del modello dato in input e tipo
     * campo
     *
     * @param idModelloTipoSerie
     *            id modello
     * @param tiCampo
     *            tipo campo delle regole
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloCampoInpUdTableBean getDecModelloCampoInpUdTableBean(BigDecimal idModelloTipoSerie, String tiCampo)
            throws ParerUserError {
        List<DecModelloCampoInpUd> list = helper.retrieveDecModelloCampoInpUd(idModelloTipoSerie, tiCampo);
        DecModelloCampoInpUdTableBean table = transformDecModelloCampoInpUd(list);
        List<Object> toList = table.toList(DecModelloCampoInpUdTableDescriptor.COL_NM_CAMPO);
        for (CostantiDB.NomeCampo campo : CostantiDB.NomeCampo.getListaDatoProfiloIndividuazione()) {
            if (toList.contains(campo.name())) {
                DecModelloCampoInpUdRowBean row = table.getRow(toList.indexOf(campo.name()));
                row.setString("key_campo", campo.getDescrizione());
                row.setBigDecimal("num_ord_campo", new BigDecimal(campo.getNumeroOrdine()));
            } else {
                table.last();
                DecModelloCampoInpUdRowBean row = new DecModelloCampoInpUdRowBean();
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

    /**
     * Ritorna l'id della regola di acquisizione richiesta dato modello, tipo campo e nome del campo
     *
     * @param idModelloTipoSerie
     *            id modello serie
     * @param tiCampo
     *            tipo campo
     * @param nmCampo
     *            nome campo
     * 
     * @return BigDecimal pk modello campo in unita doc
     */
    public BigDecimal getIdModelloCampoInpUd(BigDecimal idModelloTipoSerie, String tiCampo, String nmCampo) {
        DecModelloCampoInpUd decModelloCampoInpUd = helper.getDecModelloCampoInpUd(idModelloTipoSerie, tiCampo,
                nmCampo);
        BigDecimal idModelloCampoInpUd = decModelloCampoInpUd != null
                ? new BigDecimal(decModelloCampoInpUd.getIdModelloCampoInpUd()) : null;
        return idModelloCampoInpUd;
    }

    /**
     * Esegue l'eliminazione della regole di acquisizione dato l'id della regola da eliminare
     *
     * @param param
     *            parametri per il logging
     * @param idModelloCampoInpUd
     *            id modello campo in unita doc
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloCampoInpUd(LogParam param, BigDecimal idModelloCampoInpUd) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione delle regole di acquisizione");
        try {
            DecModelloCampoInpUd regola = helper.findById(DecModelloCampoInpUd.class, idModelloCampoInpUd);
            helper.removeEntity(regola, true);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(regola.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione delle regole di acquisizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Esegue il salvataggio delle regole di acquisizione per il modello dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello serie
     * @param datiCompilati
     *            il tablebean con i dati provenienti dall'online a seguito di inserimenti/modifiche/cancellazioni
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecModelloCampoInpUd(LogParam param, BigDecimal idModelloTipoSerie,
            DecModelloCampoInpUdTableBean datiCompilati) throws ParerUserError {
        try {
            // "Normalizzo" i numeri d'ordine del tablebean dell'online
            normalizePgOrdCampo(datiCompilati);

            // Ricavo i "vecchi" valori su DB da confrontare poi con i nuovi dell'online
            DecModelloTipoSerie decModelloTipoSerie = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            List<DecModelloCampoInpUd> campiPreModifiche = decModelloTipoSerie.getDecModelloCampoInpUds();

            /* 1° - Elimino i campi non più presenti */
            for (DecModelloCampoInpUd campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (DecModelloCampoInpUdRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdModelloCampoInpUd() != null && campoPreModifica
                            .getIdModelloCampoInpUd() == datoCompilato.getIdModelloCampoInpUd().longValue()) {
                        campoPreModifica.setTiTrasformCampo(datoCompilato.getTiTrasformCampo());
                        isPresente = true;
                        break;
                    }
                }
                if (!isPresente) {
                    // se non è più presente, lo cancello
                    helper.removeEntity(campoPreModifica, true);
                }
            }

            /*
             * 2° - Sistemo i campi "modificati", ovvero quelli in cui ho scambiato pgOrdCampo tra due campi esistenti
             * oppure glielo ho cambiato con uno nuovo
             */
            ArrayList<DecModelloCampoInpUd> al = new ArrayList<>();
            for (DecModelloCampoInpUd campoPreModifica : campiPreModifiche) {
                for (DecModelloCampoInpUdRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdModelloCampoInpUd() != null && campoPreModifica
                            .getIdModelloCampoInpUd() == datoCompilato.getIdModelloCampoInpUd().longValue()) {
                        if (campoPreModifica.getPgOrdCampo().longValue() != datoCompilato.getPgOrdCampo().longValue()) {
                            DecModelloCampoInpUd campo = new DecModelloCampoInpUd();
                            campo.setNmCampo(campoPreModifica.getNmCampo());
                            campo.setDecModelloTipoSerie(decModelloTipoSerie);
                            campo.setPgOrdCampo(datoCompilato.getPgOrdCampo());
                            campo.setTiCampo(campoPreModifica.getTiCampo());
                            campo.setTiTrasformCampo(campoPreModifica.getTiTrasformCampo());
                            helper.removeEntity(campoPreModifica, true);
                            al.add(campo);
                            break;
                        }
                    }
                }
            }

            for (DecModelloCampoInpUd decModelloCampoInpUd : al) {
                helper.insertEntity(decModelloCampoInpUd, true);
            }

            /* 3° - Inserisco i nuovi campi */
            for (DecModelloCampoInpUdRowBean row : datiCompilati) {
                if (row.getIdModelloCampoInpUd() == null) {
                    DecModelloCampoInpUd campo = new DecModelloCampoInpUd();
                    campo.setNmCampo(row.getNmCampo());
                    campo.setDecModelloTipoSerie(decModelloTipoSerie);
                    campo.setPgOrdCampo(row.getPgOrdCampo());
                    campo.setTiCampo(row.getTiCampo());
                    campo.setTiTrasformCampo(row.getTiTrasformCampo());
                    helper.insertEntity(campo, true);
                }
            }

            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle regole di acquisizione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    private void normalizePgOrdCampo(DecModelloCampoInpUdTableBean tb) {
        long count = 0;
        for (DecModelloCampoInpUdRowBean rb : tb) {
            rb.setPgOrdCampo(new BigDecimal(++count));
        }
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di acquisizione sui dati specifici modificabili del modello
     * dato in input e tipo campo, aggiungendo a ogni riga le versioni del dato specifico
     *
     * @param idModelloTipoSerie
     *            id modello
     * @param idTipoEntita
     *            id tipo entita
     * @param tiCampo
     *            tipo campo delle regole
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloCampoInpUdTableBean getDecModelloCampoInpUdDatiSpecTableBean(BigDecimal idModelloTipoSerie,
            BigDecimal idTipoEntita, String tiCampo) throws ParerUserError {
        List<DecModelloCampoInpUd> list = helper.retrieveDecModelloCampoInpUd(idModelloTipoSerie, tiCampo);
        DecModelloCampoInpUdTableBean table = transformDecModelloCampoInpUd(list);
        List<Object> toList = table.toList(DecModelloCampoInpUdTableDescriptor.COL_NM_CAMPO);
        Constants.TipoEntitaSacer tipoEntitaSacer = tiCampo.equals(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name())
                ? Constants.TipoEntitaSacer.UNI_DOC : Constants.TipoEntitaSacer.DOC;
        List<DecAttribDatiSpec> decAttribDatiSpecs = tipoSerieHelper.getDecAttribDatiSpec(idTipoEntita,
                tipoEntitaSacer);

        for (DecAttribDatiSpec attrib : decAttribDatiSpecs) {
            String versioniXsd = tipoSerieHelper.getVersioniXsd(new BigDecimal(attrib.getIdAttribDatiSpec()),
                    idTipoEntita, tipoEntitaSacer);
            String name = attrib.getNmAttribDatiSpec();
            if (StringUtils.isNotBlank(versioniXsd)) {
                name += " " + versioniXsd;
            }
            if (!toList.contains(attrib.getNmAttribDatiSpec())) {
                table.last();
                DecModelloCampoInpUdRowBean row = new DecModelloCampoInpUdRowBean();
                row.setString("key_campo", name);
                row.setString("nm_campo", attrib.getNmAttribDatiSpec());
                row.setBigDecimal("pg_ord_campo", null);
                table.add(row);
            } else {
                DecModelloCampoInpUdRowBean row = table.getRow(toList.indexOf(attrib.getNmAttribDatiSpec()));
                row.setString("key_campo", name);
            }
        }

        table.addSortingRule("nm_campo", SortingRule.ASC);
        table.sort();
        return table;
    }

    private DecModelloCampoInpUdTableBean transformDecModelloCampoInpUd(List<DecModelloCampoInpUd> list)
            throws ParerUserError {
        DecModelloCampoInpUdTableBean table = new DecModelloCampoInpUdTableBean();
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecModelloCampoInpUdTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di regole di acquisizione dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di regole di rappresentazione del modello dato in input, ordinati come
     * segue: 1) KEY_UD_SERIE 2) DT_UD_SERIE 3) INFO_UD_SERIE 4) DS_KEY_ORD_UD_SERIE 5) PG_UD _SERIE
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloOutSelUdTableBean getDecModelloOutSelUdTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecModelloOutSelUdTableBean table = new DecModelloOutSelUdTableBean();
        // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con "delegate" sulla wiki)
        List<DecModelloOutSelUd> list = helper.retrieveDecModelloOutSelUd(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {

                // Ordino la lista nell'ordine indicato
                Collections.sort(list, new Comparator<DecModelloOutSelUd>() {
                    @Override
                    public int compare(DecModelloOutSelUd o1, DecModelloOutSelUd o2) {
                        CostantiDB.TipoDiRappresentazione tipoDiRappresentazione1 = CostantiDB.TipoDiRappresentazione
                                .byName(o1.getTiOut());
                        CostantiDB.TipoDiRappresentazione tipoDiRappresentazione2 = CostantiDB.TipoDiRappresentazione
                                .byName(o2.getTiOut());
                        return Integer.compare(tipoDiRappresentazione1.getNumeroOrdine(),
                                tipoDiRappresentazione2.getNumeroOrdine());
                    }
                });

                for (DecModelloOutSelUd out : list) {
                    DecModelloOutSelUdRowBean row = (DecModelloOutSelUdRowBean) Transform.entity2RowBean(out);
                    CostantiDB.TipoDiRappresentazione tipoDiRappresentazione = CostantiDB.TipoDiRappresentazione
                            .byName(row.getTiOut());
                    row.setString("desc_ti_out", tipoDiRappresentazione.toString());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di regole di rappresentazione dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di campi regola di rappresentazione modificabili del modello dato in
     * input e tipo campo
     *
     * @param idModelloOutSelUd
     *            id regola di rappresentazione, se null crea la lista completa di campi
     * @param tiCampo
     *            tipo campo delle regole
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloCampoOutSelUdTableBean getDecModelloCampoOutSelUdTableBean(BigDecimal idModelloOutSelUd,
            String tiCampo) throws ParerUserError {
        List<DecModelloCampoOutSelUd> list = (idModelloOutSelUd != null
                ? helper.retrieveDecModelloCampoOutSelUd(idModelloOutSelUd, tiCampo)
                : new ArrayList<DecModelloCampoOutSelUd>());
        DecModelloCampoOutSelUdTableBean table = transformDecModelloCampoOutSelUd(list);
        List<Object> toList = table.toList(DecModelloCampoOutSelUdTableDescriptor.COL_NM_CAMPO);
        for (CostantiDB.NomeCampo campo : CostantiDB.NomeCampo.getListaDatoProfilo()) {
            if (toList.contains(campo.name())) {
                DecModelloCampoOutSelUdRowBean row = table.getRow(toList.indexOf(campo.name()));
                row.setString("key_campo", campo.getDescrizione());
                row.setBigDecimal("fl_selezionato", BigDecimal.ONE);
                row.setBigDecimal("num_ord_campo", new BigDecimal(campo.getNumeroOrdine()));
            } else {
                table.last();
                DecModelloCampoOutSelUdRowBean row = new DecModelloCampoOutSelUdRowBean();
                row.setTiCampo(tiCampo);
                row.setNmCampo(campo.name());
                row.setString("key_campo", campo.getDescrizione());
                row.setBigDecimal("fl_selezionato", BigDecimal.ZERO);
                row.setBigDecimal("num_ord_campo", new BigDecimal(campo.getNumeroOrdine()));
                table.add(row);
            }
        }
        table.addSortingRule("num_ord_campo", SortingRule.ASC);
        table.sort();

        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di campi regola di rappresentazione sui dati specifici modificabili del
     * modello dato in input e tipo campo, aggiungendo a ogni riga le versioni del dato specifico
     *
     * @param idModelloOutSelUd
     *            id regola di rappresentazione, se null crea la lista completa di campi
     * @param idTipoEntita
     *            idTipoUnitaDoc o idTipoDoc in base a tiCampo
     * @param tiCampo
     *            tipo campo delle regole
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloCampoOutSelUdTableBean getDecModelloCampoOutSelUdTableBean(BigDecimal idModelloOutSelUd,
            BigDecimal idTipoEntita, String tiCampo) throws ParerUserError {
        List<DecModelloCampoOutSelUd> list = (idModelloOutSelUd != null
                ? helper.retrieveDecModelloCampoOutSelUd(idModelloOutSelUd, tiCampo)
                : new ArrayList<DecModelloCampoOutSelUd>());
        DecModelloCampoOutSelUdTableBean table = transformDecModelloCampoOutSelUd(list);
        List<Object> toList = table.toList(DecModelloCampoOutSelUdTableDescriptor.COL_NM_CAMPO);
        Constants.TipoEntitaSacer tipoEntitaSacer = tiCampo.equals(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name())
                ? Constants.TipoEntitaSacer.UNI_DOC : Constants.TipoEntitaSacer.DOC;
        List<DecAttribDatiSpec> decAttribDatiSpecs = tipoSerieHelper.getDecAttribDatiSpec(idTipoEntita,
                tipoEntitaSacer);

        for (DecAttribDatiSpec attrib : decAttribDatiSpecs) {
            String versioniXsd = tipoSerieHelper.getVersioniXsd(new BigDecimal(attrib.getIdAttribDatiSpec()),
                    idTipoEntita, tipoEntitaSacer);
            String name = attrib.getNmAttribDatiSpec();
            if (StringUtils.isNotBlank(versioniXsd)) {
                name += " " + versioniXsd;
            }
            if (!toList.contains(attrib.getNmAttribDatiSpec())) {
                table.last();
                DecModelloCampoOutSelUdRowBean row = new DecModelloCampoOutSelUdRowBean();
                row.setString("key_campo", name);
                row.setNmCampo(attrib.getNmAttribDatiSpec());
                row.setTiCampo(tiCampo);
                row.setBigDecimal("fl_selezionato", BigDecimal.ZERO);
                table.add(row);
            } else {
                DecModelloCampoOutSelUdRowBean row = table.getRow(toList.indexOf(attrib.getNmAttribDatiSpec()));
                row.setString("key_campo", name);
                row.setBigDecimal("fl_selezionato", BigDecimal.ONE);
            }
        }

        table.addSortingRule("nm_campo", SortingRule.ASC);
        table.sort();
        return table;
    }

    private DecModelloCampoOutSelUdTableBean transformDecModelloCampoOutSelUd(List<DecModelloCampoOutSelUd> list)
            throws ParerUserError {
        DecModelloCampoOutSelUdTableBean table = new DecModelloCampoOutSelUdTableBean();
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecModelloCampoOutSelUdTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di campi regola di rappresentazione dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Metodo di creazione nuovo record delle regole di rappresentazione
     *
     * @param param
     *            parametri per il logging
     * @param tiOut
     *            tipo output
     * @param dlFormatoOut
     *            formato
     * @param datiCompilati
     *            compilazione
     * @param idModelloTipoSerie
     *            id modello serie
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecModelloOutSelUd(LogParam param, String tiOut, String dlFormatoOut,
            DecModelloCampoOutSelUdTableBean datiCompilati, BigDecimal idModelloTipoSerie) throws ParerUserError {
        try {
            DecModelloTipoSerie decModelloTipoSerie = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            DecModelloOutSelUd decModelloOutSelUd = new DecModelloOutSelUd();
            decModelloOutSelUd.setTiOut(tiOut);
            decModelloOutSelUd.setDlFormatoOut(dlFormatoOut);
            decModelloOutSelUd.setDecModelloTipoSerie(decModelloTipoSerie);
            if (decModelloOutSelUd.getDecModelloCampoOutSelUds() == null) {
                decModelloOutSelUd.setDecModelloCampoOutSelUds(new ArrayList<DecModelloCampoOutSelUd>());
            }
            for (DecModelloCampoOutSelUdRowBean row : datiCompilati) {
                DecModelloCampoOutSelUd campo = new DecModelloCampoOutSelUd();
                campo.setNmCampo(row.getNmCampo());
                campo.setTiCampo(row.getTiCampo());
                campo.setTiTrasformCampo(row.getTiTrasformCampo());
                decModelloOutSelUd.addDecModelloCampoOutSelUd(campo);
            }
            helper.insertEntity(decModelloOutSelUd, false);
            helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle regole di rappresentazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Metodo di modifica record delle regole di rappresentazione
     *
     * @param param
     *            parametri per il logging
     * @param idModelloOutSelUd
     *            record da modificare
     * @param tiOut
     *            tipo output
     * @param dlFormatoOut
     *            formato
     * @param datiCompilati
     *            compilazione
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecModelloOutSelUd(LogParam param, BigDecimal idModelloOutSelUd, String tiOut, String dlFormatoOut,
            DecModelloCampoOutSelUdTableBean datiCompilati) throws ParerUserError {
        try {
            DecModelloOutSelUd decModelloOutSelUd = helper.findById(DecModelloOutSelUd.class, idModelloOutSelUd);
            decModelloOutSelUd.setTiOut(tiOut);
            decModelloOutSelUd.setDlFormatoOut(dlFormatoOut);

            // Recupero i campi da DB prima delle modifiche
            List<DecModelloCampoOutSelUd> campiPreModifiche = helper.retrieveDecModelloCampoOutSelUd(idModelloOutSelUd,
                    null);
            // List<DecModelloCampoOutSelUd> campiPreModifiche = decModelloOutSelUd.getDecModelloCampoOutSelUds();

            // Elimino i campi non più checkati
            for (DecModelloCampoOutSelUd campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (DecModelloCampoOutSelUdRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdModelloCampoOutSelUd() != null && campoPreModifica
                            .getIdModelloCampoOutSelUd() == datoCompilato.getIdModelloCampoOutSelUd().longValue()) {
                        isPresente = true;
                        break;
                    }
                }
                if (!isPresente) {
                    // se non è più presente, lo cancello
                    helper.removeEntity(campoPreModifica, true);
                    // helper.deleteDecModelloCampoOutSelUd(campoPreModifica.getIdModelloCampoOutSelUd());
                }
            }

            if (decModelloOutSelUd.getDecModelloCampoOutSelUds() == null) {
                decModelloOutSelUd.setDecModelloCampoOutSelUds(new ArrayList<DecModelloCampoOutSelUd>());
            }

            // Inserisco i nuovi (aventi idModelloCampoSelUd nullo)
            for (DecModelloCampoOutSelUdRowBean datoCompilato : datiCompilati) {
                if (datoCompilato.getIdModelloCampoOutSelUd() == null) {
                    DecModelloCampoOutSelUd campo = new DecModelloCampoOutSelUd();
                    campo.setNmCampo(datoCompilato.getNmCampo());
                    campo.setTiCampo(datoCompilato.getTiCampo());
                    campo.setTiTrasformCampo(datoCompilato.getTiTrasformCampo());
                    decModelloOutSelUd.addDecModelloCampoOutSelUd(campo);
                }
            }

            // helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(decModelloOutSelUd.getDecModelloTipoSerie().getIdModelloTipoSerie()),
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle regole di rappresentazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Esegue l'eliminazione della regola di rappresentazione data in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloOutSelUd
     *            id modello output
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloOutSelUd(LogParam param, BigDecimal idModelloOutSelUd) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione delle regole di rappresentazione");
        try {
            DecModelloOutSelUd regola = helper.findById(DecModelloOutSelUd.class, idModelloOutSelUd);
            helper.removeEntity(regola, true);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(regola.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione delle regole di rappresentazione ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Ritorna il tableBean contenente per il modello dato in input un unico rowBean che contiene la lista di regole di
     * filtraggio per i tipi documento principale
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloFiltroTiDocTableBean getDecModelloFiltroTiDocSingleRowTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecModelloFiltroTiDocTableBean table = new DecModelloFiltroTiDocTableBean();
        List<DecModelloFiltroTiDoc> list = helper.retrieveDecModelloFiltroTiDoc(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            DecModelloFiltroTiDocRowBean row = new DecModelloFiltroTiDocRowBean();
            row.setIdModelloTipoSerie(idModelloTipoSerie);
            StringBuilder nmTipoDocs = new StringBuilder();
            for (DecModelloFiltroTiDoc tiDoc : list) {
                nmTipoDocs.append(tiDoc.getNmTipoDoc()).append(";");
            }
            row.setNmTipoDoc(StringUtils.chop(nmTipoDocs.toString()));
            table.add(row);
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente per il modello dato in input la lista di regole di filtraggio per i tipi
     * documento principale
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloFiltroTiDocTableBean getDecModelloFiltroTiDocTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecModelloFiltroTiDocTableBean table = new DecModelloFiltroTiDocTableBean();
        List<DecModelloFiltroTiDoc> list = helper.retrieveDecModelloFiltroTiDoc(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecModelloFiltroTiDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di regole di filtraggio per i tipi documento dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di filtri su dati specifici del modello dato in input
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloFiltroSelUdattbTableBean getDecModelloFiltroSelUdattbTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecModelloFiltroSelUdattbTableBean table = new DecModelloFiltroSelUdattbTableBean();
        List<DecModelloFiltroSelUdattb> list = helper.retrieveDecModelloFiltroSelUdattb(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecModelloFiltroSelUdattbTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di filtri su dati specifici dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di filtri sui dati specifici modificabili del modello dato in input e
     * tipo campo, aggiungendo a ogni riga le versioni del dato specifico
     *
     * @param idModelloTipoSerie
     *            id modello
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param idTipoDoc
     *            id tipo documento
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloFiltroSelUdattbTableBean getDecModelloFiltroSelUdattbTableBean(BigDecimal idModelloTipoSerie,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc) throws ParerUserError {
        List<DecModelloFiltroSelUdattb> list = helper.retrieveDecModelloFiltroSelUdattb(idModelloTipoSerie);
        DecModelloFiltroSelUdattbTableBean table = transformDecModelloFiltroSelUdattb(list);
        List<Object> toList = table.toList(DecModelloFiltroSelUdattbTableDescriptor.COL_NM_FILTRO);
        List<DecAttribDatiSpec> decAttribDatiSpecsUnitaDoc = null;
        List<DecAttribDatiSpec> decAttribDatiSpecsDoc = null;
        String tipoUnitaDoc = null;
        String tipoDoc = null;
        if (idTipoUnitaDoc != null) {
            tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc).getNmTipoUnitaDoc();
            decAttribDatiSpecsUnitaDoc = tipoSerieHelper.getDecAttribDatiSpec(idTipoUnitaDoc,
                    Constants.TipoEntitaSacer.UNI_DOC);
        }
        if (idTipoDoc != null) {
            tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc).getNmTipoDoc();
            decAttribDatiSpecsDoc = tipoSerieHelper.getDecAttribDatiSpec(idTipoDoc, Constants.TipoEntitaSacer.DOC);
        }
        Set<String> attributeNames = new LinkedHashSet<>();
        if (decAttribDatiSpecsUnitaDoc != null) {
            String vers = "Tipo unit\u00E0 doc.: " + tipoUnitaDoc;
            for (DecAttribDatiSpec attrib : decAttribDatiSpecsUnitaDoc) {
                BaseTable versioni = new BaseTable();
                BaseRow versioneXsd = new BaseRow();
                String versioniXsd = tipoSerieHelper.getVersioniXsd(new BigDecimal(attrib.getIdAttribDatiSpec()),
                        idTipoUnitaDoc, Constants.TipoEntitaSacer.UNI_DOC);
                String tmp = vers;
                if (StringUtils.isNotBlank(versioniXsd)) {
                    tmp += " " + versioniXsd;
                }
                versioneXsd.setString("versione_xsd", tmp);
                versioneXsd.setString("ti_filtro", CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC.name());
                versioni.add(versioneXsd);
                if (attributeNames.add(attrib.getNmAttribDatiSpec())) {
                    if (!toList.contains(attrib.getNmAttribDatiSpec())) {
                        table.last();
                        DecModelloFiltroSelUdattbRowBean row = new DecModelloFiltroSelUdattbRowBean();
                        row.setObject(Values.SUB_LIST, versioni);
                        row.setString("nm_filtro", attrib.getNmAttribDatiSpec());
                        row.setString("ti_oper", null);
                        row.setString("dl_valore", null);
                        table.add(row);
                    } else {
                        DecModelloFiltroSelUdattbRowBean row = table
                                .getRow(toList.indexOf(attrib.getNmAttribDatiSpec()));
                        row.setObject(Values.SUB_LIST, versioni);
                    }
                }
            }
        }
        if (decAttribDatiSpecsDoc != null) {
            // Potrei avere attributi già popolati da db o aggiunti dai tipi ud, devo discernere la casistica mentre
            // scorro la lista
            List<String> udAttributeNames = new ArrayList<>(attributeNames);
            String vers = "Tipo doc.: " + tipoDoc;
            for (DecAttribDatiSpec attrib : decAttribDatiSpecsDoc) {
                BaseRow versioneXsd = new BaseRow();
                String versioniXsd = tipoSerieHelper.getVersioniXsd(new BigDecimal(attrib.getIdAttribDatiSpec()),
                        idTipoDoc, Constants.TipoEntitaSacer.DOC);
                String tmp = vers;
                if (StringUtils.isNotBlank(versioniXsd)) {
                    tmp += " " + versioniXsd;
                }
                versioneXsd.setString("versione_xsd", tmp);
                versioneXsd.setString("ti_filtro", CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC.name());

                // Se non sono presi né da db, né aggiunti da tipi ud
                if (!toList.contains(attrib.getNmAttribDatiSpec())
                        && attributeNames.add(attrib.getNmAttribDatiSpec())) {
                    BaseTable versioni = new BaseTable();
                    versioni.add(versioneXsd);
                    table.last();
                    DecModelloFiltroSelUdattbRowBean row = new DecModelloFiltroSelUdattbRowBean();
                    row.setObject(Values.SUB_LIST, versioni);
                    row.setString("nm_filtro", attrib.getNmAttribDatiSpec());
                    row.setString("ti_oper", null);
                    row.setString("dl_valore", null);
                    table.add(row);
                } else if (toList.contains(attrib.getNmAttribDatiSpec())) {
                    // Presi da db
                    DecModelloFiltroSelUdattbRowBean row = table.getRow(toList.indexOf(attrib.getNmAttribDatiSpec()));
                    if (row.getObject(Values.SUB_LIST) == null) {
                        row.setObject(Values.SUB_LIST, new BaseTable());
                    }
                    ((BaseTableInterface) row.getObject(Values.SUB_LIST)).add(versioneXsd);
                } else {
                    // Presi da tipi ud, sicuramente l'indice sarà la somma di quelli da db + l'indice degli attributi
                    // aggiunti dai tipi ud
                    int indexRow = (toList.size() + udAttributeNames.indexOf(attrib.getNmAttribDatiSpec())) - 1;
                    DecModelloFiltroSelUdattbRowBean row = table.getRow(indexRow);
                    if (row.getObject(Values.SUB_LIST) == null) {
                        row.setObject(Values.SUB_LIST, new BaseTable());
                    }
                    ((BaseTableInterface) row.getObject(Values.SUB_LIST)).add(versioneXsd);
                }
            }
        }

        table.addSortingRule("nm_filtro", SortingRule.ASC);
        table.sort();
        return table;
    }

    private DecModelloFiltroSelUdattbTableBean transformDecModelloFiltroSelUdattb(List<DecModelloFiltroSelUdattb> list)
            throws ParerUserError {
        DecModelloFiltroSelUdattbTableBean table = new DecModelloFiltroSelUdattbTableBean();
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecModelloFiltroSelUdattbTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di filtri sui dati specifici dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Esegue il salvataggio dei filtri dati specifici per il modello dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello serie
     * @param datiCompilati
     *            compilazione
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecModelloFiltroSelUdattb(LogParam param, BigDecimal idModelloTipoSerie,
            DecModelloFiltroSelUdattbTableBean datiCompilati) throws ParerUserError {
        try {
            DecModelloTipoSerie decModelloTipoSerie = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            // Ricavo i dati spec già presenti su DB
            List<DecModelloFiltroSelUdattb> campiPreModifiche = decModelloTipoSerie.getDecModelloFiltroSelUdattbs();

            // Elimino i campi non più presenti
            for (DecModelloFiltroSelUdattb campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (DecModelloFiltroSelUdattbRowBean datoCompilato : datiCompilati) {
                    if (datoCompilato.getIdModelloFiltroSelUdattb() != null && campoPreModifica
                            .getIdModelloFiltroSelUdattb() == datoCompilato.getIdModelloFiltroSelUdattb().longValue()) {
                        // Aggiorno cmq il valore. Il PersistenceContext capirà da solo se dovrà fare l'update o meno
                        campoPreModifica.setTiOper(datoCompilato.getTiOper());
                        campoPreModifica.setDlValore(datoCompilato.getDlValore());
                        helper.insertEntity(campoPreModifica, false);
                        isPresente = true;
                        break;
                    }
                }
                if (!isPresente) {
                    // se non è più presente, lo cancello
                    helper.removeEntity(campoPreModifica, false);
                }
            }

            for (DecModelloFiltroSelUdattbRowBean row : datiCompilati) {
                if (row.getIdModelloFiltroSelUdattb() == null) {
                    DecModelloFiltroSelUdattb filtro = new DecModelloFiltroSelUdattb();
                    filtro.setNmFiltro(row.getNmFiltro());
                    filtro.setDecModelloTipoSerie(decModelloTipoSerie);
                    filtro.setTiFiltro(row.getTiFiltro());
                    filtro.setTiOper(row.getTiOper());
                    filtro.setDlValore(row.getDlValore());
                    helper.insertEntity(filtro, false);
                }
            }
            helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dei filtri sui dati specifici ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Esegue l'eliminazione del filtro dati specifici dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloFiltroSelUdattb
     *            id modello filtro
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloFiltroSelUdattb(LogParam param, BigDecimal idModelloFiltroSelUdattb)
            throws ParerUserError {
        logger.debug("Eseguo l'eliminazione dei filtri sui dati specifici");
        try {
            DecModelloFiltroSelUdattb filtro = helper.findById(DecModelloFiltroSelUdattb.class,
                    idModelloFiltroSelUdattb);
            helper.removeEntity(filtro, true);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(filtro.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione dei filtri sui dati specifici ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Ritorna il tableBean contenente la lista di strutture associate al modello dato in input
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecUsoModelloTipoSerieTableBean getDecUsoModelloTipoSerieTableBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecUsoModelloTipoSerieTableBean table = new DecUsoModelloTipoSerieTableBean();
        List<DecUsoModelloTipoSerie> list = helper.retrieveDecUsoModelloTipoSerie(idModelloTipoSerie);
        if (list != null && !list.isEmpty()) {
            try {
                for (DecUsoModelloTipoSerie uso : list) {
                    OrgStrut strut = uso.getOrgStrut();
                    OrgEnte ente = strut.getOrgEnte();
                    String enteAmb = ente.getNmEnte() + " (" + ente.getOrgAmbiente().getNmAmbiente() + ")";

                    DecUsoModelloTipoSerieRowBean row = (DecUsoModelloTipoSerieRowBean) Transform.entity2RowBean(uso);
                    row.setString("nm_strut", strut.getNmStrut());
                    row.setString("ds_strut", strut.getDsStrut());
                    row.setString("ente_amb", enteAmb);

                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di elementi di descrizione dei modelli di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il rowBean del modello richiesto in input tramite id
     *
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @return il rowBean contenente i dati del modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloTipoSerieRowBean getDecModelloTipoSerieRowBean(BigDecimal idModelloTipoSerie)
            throws ParerUserError {
        DecModelloTipoSerie modello = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
        DecModelloTipoSerieRowBean row = null;
        try {
            row = (DecModelloTipoSerieRowBean) Transform.entity2RowBean(modello);
            row.setString("nm_tipo_doc_dati_spec",
                    modello.getDecTipoDoc() != null ? modello.getDecTipoDoc().getNmTipoDoc() : null);
            row.setString("nm_tipo_unita_doc_dati_spec",
                    modello.getDecTipoUnitaDoc() != null ? modello.getDecTipoUnitaDoc().getNmTipoUnitaDoc() : null);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            String msg = "Errore durante il recupero del modello di tipo serie di unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex);
            logger.error(msg, ex);
            throw new ParerUserError(msg);
        }
        return row;
    }

    /**
     * Ritorna il rowBean del modello richiesto in input tramite chiave unique
     *
     * @param nmModelloTipoSerie
     *            nome modello
     * @param idAmbiente
     *            id ambiente
     * 
     * @return il rowBean contenente i dati del modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloTipoSerieRowBean getDecModelloTipoSerieRowBean(String nmModelloTipoSerie, BigDecimal idAmbiente)
            throws ParerUserError {
        DecModelloTipoSerie modello = helper.getDecModelloTipoSerie(nmModelloTipoSerie, idAmbiente);
        DecModelloTipoSerieRowBean row = null;
        if (modello != null) {
            try {
                row = (DecModelloTipoSerieRowBean) Transform.entity2RowBean(modello);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero del modello di tipo serie di unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return row;
    }

    /**
     * Metodo di update di un modello di tipo serie
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello
     * @param creazioneBean
     *            bean CreazioneModelloSerieBean
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveModelloTipoSerie(LogParam param, BigDecimal idModelloTipoSerie,
            CreazioneModelloSerieBean creazioneBean) throws ParerUserError {
        logger.info("Eseguo il salvataggio delle modifiche sul modello di tipo serie");
        try {
            DecModelloTipoSerie modelloTipoSerie = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            modelloTipoSerie.setNmModelloTipoSerie(creazioneBean.getNm_modello_tipo_serie());
            setDatiModello(modelloTipoSerie, creazioneBean);
            helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
            logger.info("Salvataggio del modello completato");
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del modello di tipologia serie : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Eccezione imprevista durante il salvataggio del modello di tipologia serie");
        }
    }

    /**
     * Metodo di insert di un modello di tipo serie
     *
     * @param param
     *            parametri per il logging
     * @param creazioneBean
     *            bean CreazioneModelloSerieBean
     * 
     * @return id del nuovo modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveModelloTipoSerie(LogParam param, CreazioneModelloSerieBean creazioneBean) throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello di tipo serie");
        Long idModelloTipoSerie = null;
        try {
            OrgAmbiente ambiente = helper.findById(OrgAmbiente.class, creazioneBean.getId_ambiente());

            DecModelloTipoSerie modelloTipoSerie = new DecModelloTipoSerie();
            modelloTipoSerie.setOrgAmbiente(ambiente);
            modelloTipoSerie.setNmModelloTipoSerie(creazioneBean.getNm_modello_tipo_serie());
            setDatiModello(modelloTipoSerie, creazioneBean);
            helper.insertEntity(modelloTipoSerie, false);

            logger.info("Salvataggio del modello completato");
            idModelloTipoSerie = modelloTipoSerie.getIdModelloTipoSerie();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(idModelloTipoSerie), param.getNomePagina());
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del modello di tipologia serie : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Eccezione imprevista durante il salvataggio del modello di tipologia serie");
        }
        return idModelloTipoSerie;
    }

    private void setDatiModello(DecModelloTipoSerie modelloTipoSerie, CreazioneModelloSerieBean creazioneBean) {
        DecTipoUnitaDoc tipoUd = creazioneBean.getId_tipo_unita_doc_dati_spec() != null
                ? helper.findById(DecTipoUnitaDoc.class, creazioneBean.getId_tipo_unita_doc_dati_spec()) : null;
        DecTipoDoc tipoDoc = creazioneBean.getId_tipo_doc_dati_spec() != null
                ? helper.findById(DecTipoDoc.class, creazioneBean.getId_tipo_doc_dati_spec()) : null;

        modelloTipoSerie.setDsModelloTipoSerie(creazioneBean.getDs_modello_tipo_serie());
        modelloTipoSerie.setDtIstituz(creazioneBean.getDt_istituz());
        modelloTipoSerie.setDtSoppres(creazioneBean.getDt_soppres());
        modelloTipoSerie.setNmTipoSerieDaCreare(creazioneBean.getNm_tipo_serie_da_creare());
        modelloTipoSerie.setTiRglNmTipoSerie(creazioneBean.getTi_rgl_nm_tipo_serie());
        modelloTipoSerie.setDsTipoSerieDaCreare(creazioneBean.getDs_tipo_serie_da_creare());
        modelloTipoSerie.setTiRglDsTipoSerie(creazioneBean.getTi_rgl_ds_tipo_serie());
        modelloTipoSerie.setCdSerieDaCreare(creazioneBean.getCd_serie_da_creare());
        modelloTipoSerie.setTiRglCdSerie(creazioneBean.getTi_rgl_cd_serie());
        modelloTipoSerie.setDsSerieDaCreare(creazioneBean.getDs_serie_da_creare());
        modelloTipoSerie.setTiRglDsSerie(creazioneBean.getTi_rgl_ds_serie());
        modelloTipoSerie.setNiAnniConserv(creazioneBean.getNi_anni_conserv());
        modelloTipoSerie.setTiRglAnniConserv(creazioneBean.getTi_rgl_anni_conserv());
        modelloTipoSerie.setTiConservazioneSerie(creazioneBean.getTi_conservazione_serie());
        modelloTipoSerie.setTiRglConservazioneSerie(creazioneBean.getTi_rgl_conservazione_serie());
        modelloTipoSerie.setTiSelUd(creazioneBean.getTi_sel_ud());
        modelloTipoSerie.setNiAaSelUd(creazioneBean.getNi_aa_sel_ud());
        modelloTipoSerie.setNiAaSelUdSuc(creazioneBean.getNi_aa_sel_ud_suc());
        modelloTipoSerie.setNiUnitaDocVolume(creazioneBean.getNi_unita_doc_volume());
        modelloTipoSerie.setFlControlloConsistObblig(creazioneBean.getFl_controllo_consist_obblig());
        modelloTipoSerie.setFlCreaAutom(creazioneBean.getFl_crea_autom());
        modelloTipoSerie.setGgCreaAutom(creazioneBean.getGg_crea_autom());
        modelloTipoSerie.setAaIniCreaAutom(creazioneBean.getAa_ini_crea_autom());
        modelloTipoSerie.setAaFinCreaAutom(creazioneBean.getAa_fin_crea_autom());
        modelloTipoSerie.setTiRglRangeAnniCreaAutom(creazioneBean.getTi_rgl_range_anni_crea_autom());
        modelloTipoSerie.setNiMmCreaAutom(creazioneBean.getNi_mm_crea_autom());
        modelloTipoSerie.setDecTipoUnitaDoc(tipoUd);
        modelloTipoSerie.setDecTipoDoc(tipoDoc);
        modelloTipoSerie.setTiRglFiltroTiDoc(creazioneBean.getTi_rgl_filtro_ti_doc());
        modelloTipoSerie.setTiStatoVerSerieAutom(creazioneBean.getTi_stato_ver_serie_autom());
    }

    /**
     * Esegue l'eliminazione della associazione della struttura al modello data in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloTipoSerie(LogParam param, BigDecimal idModelloTipoSerie) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione di un modello");
        try {
            DecModelloTipoSerie modello = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
            helper.removeEntity(modello, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione del modello ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Metodo di salvataggio delle regole di filtraggio tipo documento principale
     *
     * @param param
     *            parametri per il loggin
     * @param idModelloTipoSerie
     *            id modello
     * @param nmTipiDocs
     *            tipi documento da inserire
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecModelloFiltroTiDoc(LogParam param, BigDecimal idModelloTipoSerie, List<String> nmTipiDocs)
            throws ParerUserError {
        try {
            DecModelloTipoSerie decModelloTipoSerie = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            List<DecModelloFiltroTiDoc> campiPreModifiche = helper.getDecModelloFiltroTiDoc(idModelloTipoSerie);

            /* 1° - Elimino i campi non più presenti */
            for (DecModelloFiltroTiDoc campoPreModifica : campiPreModifiche) {
                boolean isPresente = false;
                for (String nmTipoDoc : nmTipiDocs) {
                    if (campoPreModifica.getNmTipoDoc().equals(nmTipoDoc)) {
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
            for (String nmTipoDoc : nmTipiDocs) {
                if ((tipoSerieHelper.getDecModelloFiltroTiDoc(idModelloTipoSerie, nmTipoDoc)).size() == 0) {
                    DecModelloFiltroTiDoc filtro = new DecModelloFiltroTiDoc();
                    filtro.setDecModelloTipoSerie(decModelloTipoSerie);
                    filtro.setNmTipoDoc(nmTipoDoc);
                    tipoSerieHelper.insertEntity(filtro, true);
                }
            }
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle regole di filtraggio ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Esegue l'eliminazione delle regole di filtraggio per il modello dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloFiltroTiDoc(LogParam param, BigDecimal idModelloTipoSerie) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione delle regole di filtraggio");
        try {
            helper.deleteAllDecModelloFiltroTiDoc(idModelloTipoSerie);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione delle regole di filtraggio ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Metodo di inserimento record delle associazioni strutture al modello
     *
     * @param param
     *            parametri per il logging
     * @param idModelloTipoSerie
     *            id modello
     * @param strutTable
     *            entity OrgStrutTableBean
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecUsoModelloTipoSerie(LogParam param, BigDecimal idModelloTipoSerie, OrgStrutTableBean strutTable)
            throws ParerUserError {
        try {
            DecModelloTipoSerie modello = helper.findById(DecModelloTipoSerie.class, idModelloTipoSerie);
            for (OrgStrutRowBean row : strutTable) {
                if (helper.existDecUsoModelloTipoSerie(idModelloTipoSerie, row.getIdStrut())) {
                    throw new ParerUserError(
                            "La struttura " + row.getNmStrut() + " \u00E8 gi\u00E0 associata al modello");
                }
                OrgStrut strut = helper.findById(OrgStrut.class, row.getIdStrut());
                if (modello.getDecUsoModelloTipoSeries() == null) {
                    modello.setDecUsoModelloTipoSeries(new ArrayList<DecUsoModelloTipoSerie>());
                }
                DecUsoModelloTipoSerie uso = new DecUsoModelloTipoSerie();
                uso.setDecModelloTipoSerie(modello);
                uso.setOrgStrut(strut);
                modello.addDecUsoModelloTipoSery(uso);
            }
            helper.getEntityManager().flush();
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE, idModelloTipoSerie,
                    param.getNomePagina());
        } catch (ParerUserError e) {
            throw e;
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio delle associazioni delle strutture al modello di tipo serie ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Esegue l'eliminazione della associazione della struttura al modello data in input
     *
     * @param param
     *            parametri per il logging
     * @param idUsoModelloTipoSerie
     *            id modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecUsoModelloTipoSerie(LogParam param, BigDecimal idUsoModelloTipoSerie) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione della associazione della struttura al modello");
        try {
            DecUsoModelloTipoSerie uso = helper.findById(DecUsoModelloTipoSerie.class, idUsoModelloTipoSerie);
            helper.removeEntity(uso, true);
            // Inserito per loggare la foto del Modello tipo serie
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_SERIE,
                    new BigDecimal(uso.getDecModelloTipoSerie().getIdModelloTipoSerie()), param.getNomePagina());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione della associazione della struttura al modello ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

}
