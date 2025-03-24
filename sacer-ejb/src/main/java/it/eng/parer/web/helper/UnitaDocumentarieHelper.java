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

package it.eng.parer.web.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroLogStatoConservUd;
import it.eng.parer.entity.AroNotaUnitaDoc;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroWarnUpdUnitaDoc;
import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoNotaUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecVersioneWs;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsXmlModelloSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.DecModelloXsdUd.TiModelloXsdUd;
import it.eng.parer.entity.constraint.SIOrgEnteSiam.TiEnteConvenz;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice;
import it.eng.parer.slite.gen.tablebean.AroFileVerIndiceAipUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroLogStatoConservUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroLogStatoConservUdTableBean;
import it.eng.parer.slite.gen.tablebean.AroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdRowBean;
import it.eng.parer.slite.gen.tablebean.AroVerIndiceAipUdTableBean;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroWarnUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoNotaUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisArchivUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisCompDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDatiSpecTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisElvVerTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisFascTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisLinkUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisNotaUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdCompUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdDocUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisUpdKoRisoltiTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolCorTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisVolNoValDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisNotaUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUnitaDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUpdUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisArchivUnitaDoc;
import it.eng.parer.viewEntity.AroVLisCompDoc;
import it.eng.parer.viewEntity.AroVLisDoc;
import it.eng.parer.viewEntity.AroVLisElvVer;
import it.eng.parer.viewEntity.AroVLisFasc;
import it.eng.parer.viewEntity.AroVLisLinkUnitaDoc;
import it.eng.parer.viewEntity.AroVLisNotaUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdCompUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdDocUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdKoRisolti;
import it.eng.parer.viewEntity.AroVLisVolCor;
import it.eng.parer.viewEntity.AroVLisVolNoValDoc;
import it.eng.parer.viewEntity.AroVRicUnitaDoc;
import it.eng.parer.viewEntity.AroVVisDocIam;
import it.eng.parer.viewEntity.AroVVisNotaUnitaDoc;
import it.eng.parer.viewEntity.AroVVisUnitaDocIam;
import it.eng.parer.viewEntity.AroVVisUpdUnitaDoc;
import it.eng.parer.viewEntity.ElvVLisUpdUd;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.volume.utils.VolumeEnums.DocStatusEnum;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.SerieAppartenenzaRowBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

/**
 * Session Bean implementation class unitaDocumetarieHelper Contiene i metodi (implementati di
 * unitaDocumentarieHelperLocal), per la gestione della persistenza su DB per le operarazioni CRUD su oggetti di
 * UnitaDocumentarieTableBean ed UnitaDocumentarieRowBean
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class UnitaDocumentarieHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(UnitaDocumentarieHelper.class.getName());

    private static final String ID_VER_INDICE_AIP_PARAMETER = "idVerIndiceAip";

    private static final String RIC_UD_DATI_SPEC_BASE = "SELECT distinct ud.id_Unita_Doc, ud.aa_Key_Unita_Doc, ud.cd_Key_Unita_Doc, ud.cd_Registro_Key_Unita_Doc, ud.dt_Creazione, ud.dt_Reg_Unita_Doc, ud.fl_Unita_Doc_Firmato, "
            + "ud.ti_Esito_Verif_Firme, ud.ds_Msg_Esito_Verif_Firme, tipo_ud.nm_Tipo_Unita_Doc, ud.fl_Forza_Accettazione, ud.fl_Forza_Conservazione, ud.ds_Key_Ord, ud.ni_Alleg, ud.ni_Annessi, ud.ni_Annot, ud.ti_Stato_Conservazione, ";

    private static final String RIC_UD_DATI_SPEC_NM_TIPO_DOC_PRINC = "(SELECT tipo_doc_princ.nm_tipo_doc "
            + "              FROM sacer.ARO_DOC  doc_princ "
            + "                   JOIN sacer.DEC_TIPO_DOC tipo_doc_princ "
            + "                       ON (tipo_doc_princ.id_tipo_doc = doc_princ.id_tipo_doc) "
            + "             WHERE     " + "                   doc_princ.id_unita_doc = ud.id_unita_doc "
            + "                   AND doc_princ.ti_doc = 'PRINCIPALE' "
            + "                   and rownum = 1                   " + "                   ) nm_tipo_doc_princ, ";

    private static final String RIC_UD_DATI_SPEC_STATI_ELENCO_VERS = "(SELECT LISTAGG (tmp.ti_stato_elenco_vers, '; ') "
            + "              WITHIN GROUP (ORDER BY tmp.ti_stato_elenco_vers) "
            + "              FROM (SELECT ud_elenco.ti_stato_ud_elenco_vers    ti_stato_elenco_vers "
            + "                      FROM sacer.ARO_UNITA_DOC ud_elenco "
            + "                     WHERE ud_elenco.id_unita_doc = ud.id_unita_doc " + "                    UNION "
            + "                    SELECT "
            + "                           doc_elenco.ti_stato_doc_elenco_vers    ti_stato_elenco_vers "
            + "                      FROM sacer.ARO_DOC doc_elenco "
            + "                     WHERE     doc_elenco.id_unita_doc = ud.id_unita_doc "
            + "                           AND doc_elenco.ti_stato_doc_elenco_vers "
            + "                                   IS NOT NULL) tmp "
            + "                                   )       ds_lista_stati_elenco_vers ";

    private static final String RIC_UD_DATI_SPEC_BASE_FROM = "FROM sacer.aro_unita_doc ud "
            + "JOIN sacer.dec_tipo_unita_doc tipo_ud on (tipo_ud.id_tipo_unita_doc = ud.id_tipo_unita_doc) "
            + "JOIN sacer.aro_doc doc on (doc.id_unita_doc = ud.id_unita_doc) ";

    @EJB(mappedName = "java:app/Parer-ejb/VolumeHelper")
    private VolumeHelper volumeHelper;
    @EJB
    private UnitaDocumentarieHelper me;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    private LazyListHelper lazyListHelper;

    @EJB
    private ObjectStorageService objectStorageService;

    // Metodo che restituisce un viewbean con il record trovato in base
    // ai parametri
    public AroVRicUnitaDocRowBean getAroVRicUnitaDocRowBean(BigDecimal idUnitaDoc, BigDecimal idStruttura,
            String statoDoc) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM AroVRicUnitaDoc u ");
        if (idUnitaDoc != null) {
            queryStr.append(whereWord).append("u.id.idUnitaDoc = :idud");
            whereWord = " AND ";
        }
        if (idStruttura != null) {
            queryStr.append(whereWord).append("u.idStrutUnitaDoc = :idstrutud");
            whereWord = " AND ";
        }
        if (statoDoc != null) {
            queryStr.append(whereWord).append("u.tiStatoDoc = :statodoc");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // Passo i parametri di ricerca
        if (idUnitaDoc != null) {
            query.setParameter("idud", idUnitaDoc);
        }
        if (idStruttura != null) {
            query.setParameter("idstrutud", idStruttura);
        }
        if (statoDoc != null) {
            query.setParameter("statodoc", statoDoc);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVRicUnitaDoc> listaUD = query.getResultList();

        AroVRicUnitaDocRowBean rowBean = null;

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                rowBean = (AroVRicUnitaDocRowBean) Transform.entity2RowBean(listaUD.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return rowBean;
    }

    /**
     * Metodo che wrappa quello seguente in quanto bypassa l'intercettazione fatta nell'ejb-jar.xml per il metodo
     * getAroVRicUnitaDocRicSempliceViewBean() che viene limitato ad un numero massimo di records.
     *
     * @param filtri
     *            filtri di ricerca
     * @param idTipoUnitaDocList
     *            lista di id del tipo di unità documentaria
     * @param cdRegistroUnitaDocList
     *            lista di codici del registro dell' unità documentaria
     * @param idTipoDocList
     *            lista di id del tipo documento
     * @param dateAcquisizioneValidate
     *            array di date validate
     * @param dateUnitaDocValidate
     *            array di date relative alle unità documentarie
     * @param idStruttura
     *            id della struttura
     *
     * @return {@link AroVRicUnitaDocTableBean} table bean da usare per la UI
     *
     * @throws EMFError
     *             errore generico
     */
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicSempliceViewBeanNoLimit(FiltriUnitaDocumentarieSemplice filtri,
            List<BigDecimal> idTipoUnitaDocList, List<String> cdRegistroUnitaDocList, List<BigDecimal> idTipoDocList,
            Date[] dateAcquisizioneValidate, Date[] dateUnitaDocValidate, BigDecimal idStruttura) throws EMFError {

        return getAroVRicUnitaDocRicSempliceViewBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocList,
                idTipoDocList, dateAcquisizioneValidate, dateUnitaDocValidate, idStruttura,
                new FiltriUnitaDocumentarieSemplicePlain(filtri), -1, false);
    }

    /**
     * @param filtri
     *            filtri di ricerca
     * @param idTipoUnitaDocList
     *            lista di tipi di unità documentarie
     * @param cdRegistroUnitaDocList
     *            lista di codici di registro delle unità documentarie
     * @param idTipoDocList
     *            lista dei tipi di documento
     * @param dateAcquisizioneValidate
     *            array delle date di acquisizione
     * @param dateUnitaDocValidate
     *            array delle date delle unità documentarie
     * @param idStruttura
     *            id della struttura
     * @param maxResults
     *            numero massimo di risultati
     *
     * @return {@link AroVRicUnitaDocTableBean} table bena per la UI
     *
     * @throws EMFError
     *             errore generico
     */
    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicSempliceViewBean(FiltriUnitaDocumentarieSemplice filtri,
            List<BigDecimal> idTipoUnitaDocList, List<String> cdRegistroUnitaDocList, List<BigDecimal> idTipoDocList,
            Date[] dateAcquisizioneValidate, Date[] dateUnitaDocValidate, BigDecimal idStruttura, int maxResults)
            throws EMFError {
        return getAroVRicUnitaDocRicSempliceViewBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocList,
                idTipoDocList, dateAcquisizioneValidate, dateUnitaDocValidate, idStruttura,
                new FiltriUnitaDocumentarieSemplicePlain(filtri), maxResults, true);
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicSempliceViewBeanPlainFilter(
            List<BigDecimal> idTipoUnitaDocList, List<String> cdRegistroUnitaDocList, List<BigDecimal> idTipoDocList,
            Date[] dateAcquisizioneValidate, Date[] dateUnitaDocValidate, BigDecimal idStruttura,
            FiltriUnitaDocumentarieSemplicePlain filtri, int maxResults, boolean lazy) {
        String whereWord = "WHERE ";
        StringBuilder queryStrBuilder = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc "
                + "(u.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc, u.dtCreazione, u.dtRegUnitaDoc, "
                + "u.flUnitaDocFirmato, u.tiEsitoVerifFirme, u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione, "
                + "u.flForzaConservazione, u.dsKeyOrd, u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione) FROM AroVRicUnitaDoc u ");

        // Inserimento nella query del filtro Tipo Unità Doc
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStrBuilder.append(whereWord).append(" u.idTipoUnitaDoc IN (:tipoudin) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_UD
        String cdVersioneXsdUd = filtri.getCdVersioneXsdUd();
        if (cdVersioneXsdUd != null) {
            queryStrBuilder.append(whereWord).append(" u.cdVersioneXsdUd = :cdVersioneXsdUd ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Registro
        if (!cdRegistroUnitaDocList.isEmpty()) {
            queryStrBuilder.append(whereWord).append(" u.cdRegistroKeyUnitaDoc IN (:registrokeyunitadocin) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Doc
        if (!idTipoDocList.isEmpty()) {
            queryStrBuilder.append(whereWord).append(" u.idTipoDoc IN (:tipodocin) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_DOC
        String cdVersioneXsdDoc = filtri.getCdVersioneXsdDoc();
        if (cdVersioneXsdDoc != null) {
            queryStrBuilder.append(whereWord).append(" u.cdVersioneXsdDoc = :cdVersioneXsdDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC
        BigDecimal anno = filtri.getAnno();
        String codice = filtri.getCodice();

        if (anno != null) {
            queryStrBuilder.append(whereWord).append(" u.aaKeyUnitaDoc = :annoin ");
            whereWord = "AND ";
        }

        if (codice != null) {
            queryStrBuilder.append(whereWord).append(" u.cdKeyUnitaDoc = :codicein ");
            whereWord = "AND ";
        }

        String cdKeyDocVers = filtri.getCdKeyDocVers();
        if (cdKeyDocVers != null) {
            queryStrBuilder.append(whereWord).append(" UPPER(u.cdKeyDocVers) LIKE :cdKeyDocVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC PER RANGE
        BigDecimal annoRangeDa = filtri.getAnnoRangeDa();
        BigDecimal annoRangeA = filtri.getAnnoRangeA();
        String codiceRangeDa = filtri.getCodiceRangeDa();
        String codiceRangeA = filtri.getCodiceRangeA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStrBuilder.append(whereWord).append(" u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStrBuilder.append(whereWord)
                    .append(" LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        String codiceNumContiene = filtri.getCdKeyUnitaDocContiene();

        if (codiceNumContiene != null) {
            queryStrBuilder.append(whereWord).append(" UPPER(u.cdKeyUnitaDoc) LIKE :codicein_contiene ");
            whereWord = " AND ";
            codiceNumContiene = codiceNumContiene.toUpperCase();
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getPresenza();
        if (presenza != null) {
            queryStrBuilder.append(whereWord).append(" u.flUnitaDocFirmato = :presenzain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro ESITO FIRME VERS
        String esito = filtri.getEsito();
        if (esito != null) {
            queryStrBuilder.append(whereWord).append(" u.tiEsitoVerifFirme = :esitoin ");
            whereWord = "AND ";
        }

        Date dataDa = (getDateOrNull(dateAcquisizioneValidate, 0));
        Date dataA = (getDateOrNull(dateAcquisizioneValidate, 1));

        if ((dataDa != null) && (dataA != null)) {
            queryStrBuilder.append(whereWord).append(" (u.dtCreazione >= :datada AND u.dtCreazione <= :dataa) ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro ESISTE PROFILO NORMATIVO
        String profiloNorm = filtri.getProfiloNorm();
        if (profiloNorm != null) {
            queryStrBuilder.append(whereWord).append(" u.flEsisteProfiloNormativo = :profiloNorm ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA ACCETTAZIONE
        String forzaAcc = filtri.getForzaAcc();
        if (forzaAcc != null) {
            queryStrBuilder.append(whereWord).append(" u.flForzaAccettazione = :forzaaccin ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA CONSERVAZIONE
        String forzaConserva = filtri.getForzaConserva();
        if (forzaConserva != null) {
            queryStrBuilder.append(whereWord).append(" u.flForzaConservazione = :forzaconservain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA COLLEGAMENTO
        String forzaColleg = filtri.getForzaColleg();
        if (forzaColleg != null) {
            queryStrBuilder.append(whereWord).append(" u.flForzaCollegamento = :forzacollegin ");
            whereWord = "AND ";
        }
        // // Inserimento nella query del filtro FORZA HASH
        // String forzaHash = filtri.getForzaHash();
        // if (forzaHash != null) {
        // queryStrBuilder.append(whereWord).append("u.flForzaHash = :forzahashin ");
        // whereWord = "AND ";
        // }
        // // Inserimento nella query del filtro FORZA FORMATO NUMERO
        // String forzaFmtNumero = filtri.getForzaFmtNumero();
        // if (forzaFmtNumero != null) {
        // queryStrBuilder.append(whereWord).append("u.flForzaFmtNumero = :forzafmtnumeroin ");
        // whereWord = "AND ";
        // }
        // // Inserimento nella query del filtro FORZA FORMATO FILE
        // String forzaFmtFile = filtri.getForzaFmtFile();
        // if (forzaFmtFile != null) {
        // queryStrBuilder.append(whereWord).append("u.flForzaFmtFile = :forzafmtfilein ");
        // whereWord = "AND ";
        // }
        // Inserimento nella query del filtro CD_VERSIONE_WS
        String cdVersioneWs = filtri.getCdVersioneWs();
        if (cdVersioneWs != null) {
            queryStrBuilder.append(whereWord).append(" u.cdVersioneWs = :cdVersioneWs ");
            whereWord = "AND ";
        }

        String unitaDocAnnul = filtri.getUnitaDocAnnul();
        if (unitaDocAnnul != null) {
            queryStrBuilder.append(whereWord).append(" u.flUnitaDocAnnul = :unitaDocAnnul ");
            whereWord = "AND ";
        }
        String docAggiunti = filtri.getDocAggiunti();
        if (docAggiunti != null) {
            queryStrBuilder.append(whereWord).append(" u.flDocAggiunti = :docAggiunti ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_AGG_META
        String flAggMeta = filtri.getFlAggMeta();
        if (flAggMeta != null) {
            queryStrBuilder.append(whereWord).append(" u.flAggMeta = :flAggMeta ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_HASH_VERS
        String flHashVers = filtri.getFlHashVers();
        if (flHashVers != null) {
            queryStrBuilder.append(whereWord).append(" u.flHashVers = :flHashVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro STATO CONSERVAZIONE
        String statoConserva = filtri.getStatoConserva();
        if (statoConserva != null) {
            queryStrBuilder.append(whereWord).append(" u.tiStatoConservazione = :statoconservain ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro TI_STATO_UD_ELENCO_VERS
        String tiStatoUdElencoVers = filtri.getTiStatoUdElencoVers();
        if (tiStatoUdElencoVers != null) {
            queryStrBuilder.append(whereWord).append(
                    " (u.tiStatoUdElencoVers = :tiStatoUdElencoVers OR u.tiStatoDocElencoVers = :tiStatoUdElencoVers) ");
            whereWord = "AND ";
        }

        Date dataMetaDa = (getDateOrNull(dateUnitaDocValidate, 0));
        Date dataMetaA = (getDateOrNull(dateUnitaDocValidate, 1));

        if ((dataMetaDa != null) && (dataMetaA != null)) {
            queryStrBuilder.append(whereWord).append(" (u.dtRegUnitaDoc between :datametada AND :datametaa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro TI_DOC ("Elemento")
        String tiDoc = filtri.getTiDoc();
        if (tiDoc != null) {
            queryStrBuilder.append(whereWord).append(" u.tiDoc = :tiDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Oggetto
        String oggettoMeta = filtri.getOggettoMeta();
        if (oggettoMeta != null) {
            queryStrBuilder.append(whereWord).append(" UPPER(u.dlOggettoUnitaDoc) LIKE :dloggettounitadocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Autore documento
        String autoreDocMeta = filtri.getAutoreDocMeta();
        if (autoreDocMeta != null) {
            queryStrBuilder.append(whereWord).append(" UPPER(u.dsAutoreDoc) LIKE :dldocmetain ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Descrizione documento
        String descrizioneDocMeta = filtri.getDescrizioneDocMeta();
        if (descrizioneDocMeta != null) {
            queryStrBuilder.append(whereWord).append(" UPPER(u.dlDoc) LIKE :dsautoredocin ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Conservazione
        String tipoConservazione = filtri.getTipoConservazione();
        if (tipoConservazione != null) {
            queryStrBuilder.append(whereWord).append(" u.tiConservazione = :ticonservazionein ");
            whereWord = "AND ";
        }

        queryStrBuilder.append(whereWord).append(" u.idStrutUnitaDoc = :idstrutin ");
        whereWord = "AND ";

        List<BigDecimal> subStruts = filtri.getSubStruts();
        if (!subStruts.isEmpty()) {
            queryStrBuilder.append(whereWord).append(" u.idSubStrut IN (:subStruts) ");
        }

        // ordina per dsKeyDoc crescente
        queryStrBuilder.append(" ORDER BY u.dsKeyOrd");

        Query query = getEntityManager().createQuery(queryStrBuilder.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("tipoudin", idTipoUnitaDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (!cdRegistroUnitaDocList.isEmpty()) {
            query.setParameter("registrokeyunitadocin", cdRegistroUnitaDocList);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (cdKeyDocVers != null) {
            query.setParameter("cdKeyDocVers", "%" + cdKeyDocVers.toUpperCase() + "%");
        }

        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }

        if (codiceNumContiene != null) {
            query.setParameter("codicein_contiene", "%" + codiceNumContiene + "%");
        }

        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }

        if (esito != null) {
            query.setParameter("esitoin", esito);
        }

        if (dataDa != null && dataA != null) {
            query.setParameter("datada", dataDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataA, TemporalType.TIMESTAMP);
        }

        if (profiloNorm != null) {
            query.setParameter("profiloNorm", profiloNorm);
        }

        if (forzaAcc != null) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (forzaConserva != null) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (forzaColleg != null) {
            query.setParameter("forzacollegin", forzaColleg);
        }

        // if (forzaHash != null) {
        // query.setParameter("forzahashin", forzaHash);
        // }
        //
        // if (forzaFmtNumero != null) {
        // query.setParameter("forzafmtnumeroin", forzaFmtNumero);
        // }
        //
        // if (forzaFmtFile != null) {
        // query.setParameter("forzafmtfilein", forzaFmtFile);
        // }

        if (cdVersioneWs != null) {
            query.setParameter("cdVersioneWs", cdVersioneWs);
        }

        if (unitaDocAnnul != null) {
            query.setParameter("unitaDocAnnul", unitaDocAnnul);
        }

        if (docAggiunti != null) {
            query.setParameter("docAggiunti", docAggiunti);
        }

        if (flAggMeta != null) {
            query.setParameter("flAggMeta", flAggMeta);
        }

        if (flHashVers != null) {
            query.setParameter("flHashVers", flHashVers);
        }

        if (statoConserva != null) {
            query.setParameter("statoconservain", statoConserva);
        }

        if (tiStatoUdElencoVers != null) {
            query.setParameter("tiStatoUdElencoVers", tiStatoUdElencoVers);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("tipodocin", idTipoDocList);
        }

        if (cdVersioneXsdDoc != null) {
            query.setParameter("cdVersioneXsdDoc", cdVersioneXsdDoc);
        }

        if (dataMetaDa != null && dataMetaA != null) {
            query.setParameter("datametada", dataMetaDa, TemporalType.DATE);
            query.setParameter("datametaa", dataMetaA, TemporalType.DATE);
        }

        if (tiDoc != null) {
            query.setParameter("tiDoc", tiDoc);
        }

        if (oggettoMeta != null) {
            query.setParameter("dloggettounitadocin", "%" + oggettoMeta.toUpperCase() + "%");
        }

        if (autoreDocMeta != null) {
            query.setParameter("dldocmetain", "%" + autoreDocMeta.toUpperCase() + "%");
        }

        if (descrizioneDocMeta != null) {
            query.setParameter("dsautoredocin", "%" + descrizioneDocMeta.toUpperCase() + "%");
        }

        if (idStruttura != null) {
            query.setParameter("idstrutin", idStruttura);
        }

        if (tipoConservazione != null) {
            query.setParameter("ticonservazionein", tipoConservazione);
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        if (lazy) {
            return lazyListHelper.getTableBean(query, this::resultListToAroVRicUnitaDocRowBeans, "u.idUnitaDoc");
        } else {
            return resultListToAroVRicUnitaDocRowBeans(query.getResultList());
        }
    }

    private AroVRicUnitaDocTableBean resultListToAroVRicUnitaDocRowBeans(List<AroVRicUnitaDoc> listaUD) {
        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                udTableBean = (AroVRicUnitaDocTableBean) Transform.entities2TableBean(listaUD);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return udTableBean;
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAvanzataViewBeanNoLimit(FiltriUnitaDocumentarieAvanzata filtri,
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie filtriCollegamenti,
            UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie filtriFirmatari,
            UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie filtriComponenti,
            UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie filtriFascicoli, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateCreazioneCompValidate, BigDecimal idStruttura, boolean addButton)
            throws EMFError {
        final Date dateMetaDa = getDateOrNull(dateUnitaDocValidate, 0);
        final Date dateMetaA = getDateOrNull(dateUnitaDocValidate, 1);
        return getAroVRicUnitaDocRicAvanzataViewBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocSet,
                idTipoDocList, listaDatiSpecOnLine, dateAcquisizioneValidate, dateCreazioneCompValidate, idStruttura,
                addButton, new FiltriUnitaDocumentarieAvanzataPlain(filtri, dateMetaDa, dateMetaA),
                new FiltriCollegamentiUnitaDocumentariePlain(filtriCollegamenti),
                new FiltriFirmatariUnitaDocumentariePlain(filtriFirmatari),
                new FiltriComponentiUnitaDocumentariePlain(filtriComponenti),
                new FiltriFascicoliUnitaDocumentariePlain(filtriFascicoli), false);
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicDatiSpecViewBeanNoLimit(FiltriUnitaDocumentarieDatiSpec filtri,
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine, BigDecimal idStruttura, boolean addButton,
            boolean almenoUnTipoUdSelPerRicercaDatiSpec, boolean almenoUnTipoDocSelPerRicercaDatiSpec) throws EMFError {
        return getAroVRicUnitaDocRicDatiSpecViewBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocSet,
                idTipoDocList, listaDatiSpecOnLine, idStruttura, addButton,
                new FiltriUnitaDocumentarieDatiSpecPlain(filtri), false, almenoUnTipoUdSelPerRicercaDatiSpec,
                almenoUnTipoDocSelPerRicercaDatiSpec);
    }

    private Date getDateOrNull(Date[] dateUnitaDocValidate, int i) {
        return dateUnitaDocValidate != null ? dateUnitaDocValidate[i] : null;
    }

    /**
     * @param filtri
     *            filtri di ricerca
     * @param idTipoUnitaDocList
     *            tipi di unita documentarie
     * @param cdRegistroUnitaDocSet
     *            codici registro
     * @param idTipoDocList
     *            tipi di documenti
     * @param listaDatiSpecOnLine
     *            dati spec online
     * @param filtriCollegamenti
     *            filtri di ricerca collegamenti
     * @param filtriFirmatari
     *            filtri di ricerca firmatatio
     * @param filtriComponenti
     *            filtri di ricerca componenti
     * @param filtriFascicoli
     *            filtri di ricerca fascicoli
     * @param dateAcquisizioneValidate
     *            date di acquisizione
     * @param dateUnitaDocValidate
     *            date delle unità documentarie
     * @param dateCreazioneCompValidate
     *            date di creazione
     * @param idStruttura
     *            id della struttura
     * @param addButton
     *            true, aggiunge il bottone per la UI
     *
     * @return AroVRicUnitaDocTableBean table bean per la UI
     *
     * @throws EMFError
     *             errore generico
     */
    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAvanzataViewBean(FiltriUnitaDocumentarieAvanzata filtri,
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine,
            UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie filtriCollegamenti,
            UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie filtriFirmatari,
            UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie filtriComponenti,
            UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie filtriFascicoli, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateCreazioneCompValidate, BigDecimal idStruttura, boolean addButton)
            throws EMFError {
        final Date dateMetaDa = getDateOrNull(dateUnitaDocValidate, 0);
        final Date dateMetaA = getDateOrNull(dateUnitaDocValidate, 1);
        return getAroVRicUnitaDocRicAvanzataViewBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocSet,
                idTipoDocList, listaDatiSpecOnLine, dateAcquisizioneValidate, dateCreazioneCompValidate, idStruttura,
                addButton, new FiltriUnitaDocumentarieAvanzataPlain(filtri, dateMetaDa, dateMetaA),
                new FiltriCollegamentiUnitaDocumentariePlain(filtriCollegamenti),
                new FiltriFirmatariUnitaDocumentariePlain(filtriFirmatari),
                new FiltriComponentiUnitaDocumentariePlain(filtriComponenti),
                new FiltriFascicoliUnitaDocumentariePlain(filtriFascicoli), true);
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAvanzataViewBeanPlainFilter(
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine, Date[] dateAcquisizioneValidate,
            Date[] dateCreazioneCompValidate, BigDecimal idStruttura, boolean addButton,
            FiltriUnitaDocumentarieAvanzataPlain filtri, FiltriCollegamentiUnitaDocumentariePlain filtriCollegamenti,
            FiltriFirmatariUnitaDocumentariePlain filtriFirmatari,
            FiltriComponentiUnitaDocumentariePlain filtriComponenti,
            FiltriFascicoliUnitaDocumentariePlain filtriFascicoli, boolean lazy) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc "
                + "(u.id.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc,"
                + " u.dtCreazione, u.dtRegUnitaDoc, u.flUnitaDocFirmato, u.tiEsitoVerifFirme,"
                + " u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione,"
                + " u.flForzaConservazione, u.dsKeyOrd,"
                + " u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione)"
                + " FROM AroVRicUnitaDoc u ");

        // GESTIONE FILTRI COMPONENTI
        // Da eseguire prima di tutto per la costruzione dei join
        boolean joinFormato = false;
        boolean joinAppart = false;
        boolean joinFirma = false;
        boolean joinCertif = false;
        StringBuilder whereCompClause = new StringBuilder();
        if (filtriComponenti.getNmTipoStrutDoc() != null) {
            whereCompClause.append(" AND strutDoc.decTipoStrutDoc.idTipoStrutDoc = :tipoStrutDocIn ");
        }
        if (filtriComponenti.getNmTipoCompDoc() != null) {
            whereCompClause.append(" AND comp.decTipoCompDoc.idTipoCompDoc = :tipoCompDocIn ");
        }
        if (filtriComponenti.getDsNomeCompVers() != null) {
            whereCompClause.append(" AND UPPER(comp.dsNomeCompVers) LIKE :nomeCompVersIn ");
        }
        if (filtriComponenti.getDlUrnCompVers() != null) {
            whereCompClause.append(" AND UPPER(comp.dlUrnCompVers) LIKE :urnCompVersIn ");
        }
        if (filtriComponenti.getDsHashFileVers() != null) {
            whereCompClause.append(" AND comp.dsHashFileVers = :hashFileVersIn ");
        }
        if (filtriComponenti.getNmMimetypeFile() != null) {
            joinFormato = true;
            whereCompClause.append(" AND UPPER(fileStandard.nmMimetypeFile) LIKE :mimeTypeIn ");
        }
        if (filtriComponenti.getNmFormatoFileVers() != null) {
            whereCompClause.append(" AND comp.decFormatoFileDoc.idFormatoFileDoc = :formatoFileDocIn ");
        }

        // Inserimento nella query del filtro file size
        BigDecimal fileSizeDa = filtriComponenti.getNiSizeFileDa();
        BigDecimal fileSizeA = filtriComponenti.getNiSizeFileA();
        if (fileSizeDa == null && fileSizeA != null) {
            fileSizeDa = BigDecimal.ZERO;
        }
        if (fileSizeDa != null && fileSizeA != null) {
            whereCompClause.append(" AND comp.niSizeFileCalc between :filesizedain AND :filesizeain ");
        }
        if (filtriComponenti.getDsFormatoRapprCalc() != null) {
            whereCompClause.append(" AND comp.dsFormatoRapprCalc = :formatoRapprCalcIn ");
        }
        if (filtriComponenti.getDsFormatoRapprEstesoCalc() != null) {
            whereCompClause.append(" AND comp.dsFormatoRapprEstesoCalc = :formatoRapprCalcEstesoIn ");
        }

        if (filtriComponenti.getFlCompFirmato() != null) {
            if (filtriComponenti.getFlCompFirmato().equals("1")) {
                whereCompClause.append(" AND comp.flCompFirmato = :compFirmatoIn ");
            } else {
                whereCompClause.append(
                        " AND NOT EXISTS (SELECT comp2 FROM AroCompDoc comp2 JOIN comp2.aroStrutDoc strutDoc2 WHERE strutDoc2.aroDoc.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc AND comp2.flCompFirmato = :compFirmatoIn ) ");
            }
        }

        if (filtriComponenti.getFlRifTempVers() != null) {
            if (filtriComponenti.getFlRifTempVers().equals("1")) {
                whereCompClause.append(" AND comp.tmRifTempVers IS NOT NULL ");
            } else {
                whereCompClause.append(
                        " AND NOT EXISTS (SELECT comp3 FROM AroCompDoc comp3 JOIN comp3.aroStrutDoc strutDoc3 WHERE strutDoc3.aroDoc.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc AND comp3.tmRifTempVers IS NOT NULL ) ");
            }
        }
        if (filtriComponenti.getDsRifTempVers() != null) {
            whereCompClause.append(" AND UPPER(comp.dsRifTempVers) LIKE :dsRifTempVers ");
        }

        if (filtriComponenti.getTiEsitoContrConforme() != null) {
            joinFirma = true;
            whereCompClause.append(" AND firma.tiEsitoContrConforme = :esitoContrConformeIn ");
        }

        Date dataValDa = null;
        Date dataValA = null;
        // Inserimento nella query del filtro DATA SCADENZA DA - A
        if (filtriComponenti.getDtScadFirmaCompDa() != null) {
            dataValDa = new Date(filtriComponenti.getDtScadFirmaCompDa().getTime());
            if (filtriComponenti.getDtScadFirmaCompA() != null) {
                dataValA = new Date(filtriComponenti.getDtScadFirmaCompA().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            } else {
                dataValA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataValA);
                calendar.add(Calendar.DATE, 1);
                dataValA = calendar.getTime();
            }
        }

        if ((dataValDa != null) && (dataValA != null)) {
            joinFirma = true;
            joinCertif = true;
            whereCompClause.append(" AND certif.dtFinValCertifFirmatario between :datavalda AND :datavala ");
        }

        if (filtriComponenti.getTiEsitoContrFormatoFile() != null) {
            whereCompClause.append(" AND comp.tiEsitoContrFormatoFile = :esitoContrFormatoFileIn ");
        }
        if (filtriComponenti.getTiEsitoVerifFirma() != null) {
            whereCompClause.append(" AND firma.tiEsitoVerifFirma = :esitoVerifFirmeIn ");
            joinFirma = true;
        }
        if (filtriComponenti.getTiEsitoVerifFirmeChiuse() != null) {
            whereCompClause.append(" AND appartComp.tiEsitoVerifFirmeChius = :esitoVerifFirmeChiusIn ");
            joinAppart = true;
        }
        if (filtriComponenti.getDsHashFileCalc() != null) {
            whereCompClause.append(" AND comp.dsHashFileCalc = :hashFileCalcIn ");
        }
        if (filtriComponenti.getDsAlgoHashFileCalc() != null) {
            whereCompClause.append(" AND comp.dsAlgoHashFileCalc = :algoHashFileCalcIn ");
        }
        if (filtriComponenti.getCdEncodingHashFileCalc() != null) {
            whereCompClause.append(" AND comp.cdEncodingHashFileCalc = :encodingHashFileCalcIn ");
        }
        if (filtriComponenti.getDsUrnCompCalc() != null) {
            whereCompClause.append(" AND UPPER(comp.dsUrnCompCalc) LIKE :urnCompCalcIn ");
        }

        Date dataCompDa = (getDateOrNull(dateCreazioneCompValidate, 0));
        Date dataCompA = (getDateOrNull(dateCreazioneCompValidate, 1));

        if ((dataCompDa != null) && (dataCompA != null)) {
            whereCompClause.append(" AND strutDoc.aroDoc.dtCreazione between :dataCompDaIn AND :dataCompAIn ");
        }
        //
        if (filtriComponenti.getFlForzaAccettazioneComp() != null) {
            whereCompClause.append(" AND strutDoc.aroDoc.flForzaAccettazione = :flForzaAccIn ");
        }

        if (filtriComponenti.getFlForzaConservazioneComp() != null) {
            whereCompClause.append(" AND strutDoc.aroDoc.flForzaConservazione = :flForzaConsIn ");
        }

        if (filtriComponenti.getTiSupportoComp() != null) {
            whereCompClause.append(" AND comp.tiSupportoComp = :tiSupportoCompIn ");
        }

        // TODO condizione duplicata, la togliamo?
        if (filtriComponenti.getNmTipoRapprComp() != null) {
            whereCompClause.append(" AND comp.decTipoRapprComp.idTipoRapprComp = :idTipoRapprCompIn ");
        }

        if (filtriComponenti.getDsIdCompVers() != null) {
            whereCompClause.append(" AND UPPER(comp.dsIdCompVers) LIKE :idCompDocIn ");
        }

        // Se il filtro "Cd firmatario" è valorizzato
        if (StringUtils.isNotBlank(filtriFirmatari.getCdFirmatario())) {
            whereCompClause.append(" AND UPPER(firma.cdFirmatario) LIKE :cdFirmatario ");
            joinFirma = true;
        }
        // Se il filtro "Cognome firmatario" è valorizzato
        if (StringUtils.isNotBlank(filtriFirmatari.getNmCognomeFirmatario())) {
            whereCompClause.append(" AND UPPER(firma.nmCognomeFirmatario) LIKE :nmCognomeFirmatario ");
            joinFirma = true;
        }
        // Se il filtro "Nome firmatario" è valorizzato
        if (StringUtils.isNotBlank(filtriFirmatari.getNmFirmatario())) {
            whereCompClause.append(" AND UPPER(firma.nmFirmatario) LIKE :nmFirmatario ");
            joinFirma = true;
        }

        if (StringUtils.isNotBlank(whereCompClause.toString())) {
            queryStr.append(appendJoin(joinFormato, joinFirma, joinCertif, joinAppart));
            queryStr.append(whereCompClause.toString());
            whereWord = " AND ";
        }

        // FINE GESTIONE FILTRI COMPONENTI

        // Inserimento nella query del filtro Tipo Unità Doc versione multiselect
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append(" (u.idTipoUnitaDoc IN (:listtipoud)) ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_UD
        String cdVersioneXsdUd = filtri.getCdVersioneXsdUd();
        if (cdVersioneXsdUd != null) {
            queryStr.append(whereWord).append(" u.cdVersioneXsdUd = :cdVersioneXsdUd ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Registro
        if (!cdRegistroUnitaDocSet.isEmpty()) {
            queryStr.append(whereWord).append(" u.cdRegistroKeyUnitaDoc IN (:setregistro) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Documento
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append(" u.idTipoDoc IN (:listtipodoc) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CD_VERSIONE_XSD_DOC
        String cdVersioneXsdDoc = filtri.getCdVersioneXsdDoc();
        if (cdVersioneXsdDoc != null) {
            queryStr.append(whereWord).append(" u.cdVersioneXsdDoc = :cdVersioneXsdDoc ");
            whereWord = "AND ";
        }

        BigDecimal anno = filtri.getAaKeyUnitaDoc();
        String codice = filtri.getCdKeyUnitaDoc();

        if (anno != null) {
            queryStr.append(whereWord).append(" u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append(" u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        String cdKeyDocVers = filtri.getCdKeyDocVers();
        if (cdKeyDocVers != null) {
            queryStr.append(whereWord).append(" UPPER(u.cdKeyDocVers) LIKE :cdKeyDocVers ");
            whereWord = "AND ";
        }

        BigDecimal annoRangeDa = filtri.getAaKeyUnitaDocDa();
        BigDecimal annoRangeA = filtri.getAaKeyUnitaDocA();
        String codiceRangeDa = filtri.getCdKeyUnitaDocDa();
        String codiceRangeA = filtri.getCdKeyUnitaDocA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append(" (u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append(" LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        String codiceNumContiene = filtri.getCdKeyUnitaDocContiene();

        if (codiceNumContiene != null) {
            queryStr.append(whereWord).append(" UPPER(u.cdKeyUnitaDoc) LIKE :codicein_contiene ");
            whereWord = " AND ";
            codiceNumContiene = codiceNumContiene.toUpperCase();
        }

        // Inserimento nella query del filtro PRESENZA FIRME
        String presenza = filtri.getFlUnitaDocFirmato();
        if (presenza != null) {
            queryStr.append(whereWord).append(" u.flUnitaDocFirmato = :presenzain ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro ESITO FIRME VERS in versione multiselect
        List<String> esitoveriffirmeList = filtri.getTiEsitoVerifFirme();
        if (!esitoveriffirmeList.isEmpty()) {
            queryStr.append(whereWord).append(" (u.tiEsitoVerifFirme IN (:listaesitoveriffirme)) ");
            whereWord = " AND ";
        }

        Date dataDa = (getDateOrNull(dateAcquisizioneValidate, 0));
        Date dataA = (getDateOrNull(dateAcquisizioneValidate, 1));

        if ((dataDa != null) && (dataA != null)) {
            queryStr.append(whereWord).append(" (u.dtCreazione between :datada AND :dataa) ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro ESISTE PROFILO NORMATIVO
        String profiloNorm = filtri.getProfiloNorm();
        if (profiloNorm != null) {
            queryStr.append(whereWord).append(" u.flEsisteProfiloNormativo = :profiloNorm ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro FORZA ACCETTAZIONE
        String forzaAcc = filtri.getFlForzaAccettazione();
        if (forzaAcc != null) {
            queryStr.append(whereWord).append(" u.flForzaAccettazione = :forzaaccin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro FORZA CONSERVAZIONE
        String forzaConserva = filtri.getFlForzaConservazione();
        if (forzaConserva != null) {
            queryStr.append(whereWord).append(" u.flForzaConservazione = :forzaconservain ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro FORZA COLLEGAMENTO
        String forzaColleg = filtri.getFlForzaCollegamento();
        if (forzaColleg != null) {
            queryStr.append(whereWord).append(" u.flForzaCollegamento = :forzacollegin ");
            whereWord = "AND ";
        }

        // // Inserimento nella query del filtro FORZA HASH
        // String forzaHash = filtri.getForzaHash();
        // if (forzaHash != null) {
        // queryStr.append(whereWord).append("u.flForzaHash = :forzahashin ");
        // whereWord = "AND ";
        // }
        // // Inserimento nella query del filtro FORZA FORMATO NUMERO
        // String forzaFmtNumero = filtri.getForzaFmtNumero();
        // if (forzaFmtNumero != null) {
        // queryStr.append(whereWord).append("u.flForzaFmtNumero = :forzafmtnumeroin ");
        // whereWord = "AND ";
        // }
        // // Inserimento nella query del filtro FORZA FORMATO FILE
        // String forzaFmtFile = filtri.getForzaFmtFile();
        // if (forzaFmtFile != null) {
        // queryStr.append(whereWord).append("u.flForzaFmtFile = :forzafmtfilein ");
        // whereWord = "AND ";
        // }
        // Inserimento nella query del filtro CD_VERSIONE_WS
        String cdVersioneWs = filtri.getCdVersioneWs();
        if (cdVersioneWs != null) {
            queryStr.append(whereWord).append(" u.cdVersioneWs = :cdVersioneWs ");
            whereWord = "AND ";
        }

        String unitaDocAnnul = filtri.getFlUnitaDocAnnul();
        if (unitaDocAnnul != null) {
            queryStr.append(whereWord).append(" u.flUnitaDocAnnul = :unitaDocAnnul ");
            whereWord = "AND ";
        }
        String docAggiunti = filtri.getFlDocAggiunti();
        if (docAggiunti != null) {
            queryStr.append(whereWord).append(" u.flDocAggiunti = :docAggiunti ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_AGG_META
        String flAggMeta = filtri.getFlAggMeta();
        if (flAggMeta != null) {
            queryStr.append(whereWord).append(" u.flAggMeta = :flAggMeta ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro FL_HASH_VERS
        String flHashVers = filtri.getFlHashVers();
        if (flHashVers != null) {
            queryStr.append(whereWord).append(" u.flHashVers = :flHashVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro STATO CONSERVAZIONE
        String tiStatoConservazione = filtri.getTiStatoConservazione();
        if (tiStatoConservazione != null) {
            queryStr.append(whereWord).append(" u.tiStatoConservazione = :tiStatoConservazione ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro TI_STATO_UD_ELENCO_VERS
        String tiStatoUdElencoVers = filtri.getTiStatoUdElencoVers();
        if (tiStatoUdElencoVers != null) {
            queryStr.append(whereWord).append(
                    " (u.tiStatoUdElencoVers = :tiStatoUdElencoVers OR u.tiStatoDocElencoVers = :tiStatoUdElencoVers) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro NM_SISTEMA_MIGRAZ in versione multiselect
        List<String> sisMigrazList = filtri.getNmSistemaMigraz();
        if (!sisMigrazList.isEmpty()) {
            queryStr.append(whereWord).append(" (u.nmSistemaMigraz IN (:listasismigraz)) ");
            whereWord = " AND ";
        }

        if ((filtri.getDataMetaDa() != null) && (filtri.getDataMetaA() != null)) {
            queryStr.append(whereWord).append(" (u.dtRegUnitaDoc between :datametada AND :datametaa) ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro TI_DOC ("Elemento")
        String tiDoc = filtri.getTiDoc();
        if (tiDoc != null) {
            queryStr.append(whereWord).append(" u.tiDoc = :tiDoc ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro metadati Oggetto
        String oggettoMeta = filtri.getDlOggettoUnitaDoc();
        if (oggettoMeta != null) {
            queryStr.append(whereWord).append(" UPPER(u.dlOggettoUnitaDoc) LIKE :dloggettounitadocin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro metadati Autore documento
        String autoreDocMeta = filtri.getDsAutoreDoc();
        if (autoreDocMeta != null) {
            queryStr.append(whereWord).append(" UPPER(u.dsAutoreDoc) LIKE :dldocmetain ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro metadati Descrizione documento
        String descrizioneDocMeta = filtri.getDlDoc();
        if (descrizioneDocMeta != null) {
            queryStr.append(whereWord).append(" UPPER(u.dlDoc) LIKE :dsautoredocin ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro Tipo Conservazione
        String tipoConservazione = filtri.getTiConservazione();
        if (tipoConservazione != null) {
            queryStr.append(whereWord).append(" u.tiConservazione = :ticonservazionein ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append(" u.idStrutUnitaDoc = :idstrutin ");
        whereWord = " AND ";

        // UTILIZZO DEI DATI SPECIFICI
        ReturnParams params = volumeHelper.buildQueryForDatiSpec(listaDatiSpecOnLine);
        queryStr.append(params.getQuery());
        List<DatiSpecQueryParams> mappone = params.getMappone();

        String valoreClassif = null;
        BigDecimal aaFascicolo = null;
        String cdKeyFascicolo = null;
        BigDecimal aaFascRangeDa = null;
        BigDecimal aaFascRangeA = null;
        String cdFascRangeDa = null;
        String cdFascRangeA = null;
        BigDecimal tipoFascIn = null;
        Date dtApeFascRangeDa = null;
        Date dtApeFascRangeA = null;
        Date dtChiuFascRangeDa = null;
        Date dtChiuFascRangeA = null;
        String oggettoFasc = null;

        if (filtriFascicoli.isFiltriImpostati()) {
            queryStr.append(whereWord)
                    .append(" exists (select 1 from AroVLisFasc fasc_ud where fasc_ud.id.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Indice cassificazione" è valorizzato
            valoreClassif = filtriFascicoli.getCdCompositoVoceTitol();
            if (valoreClassif != null) {
                queryStr.append(whereWord).append(" UPPER(fasc_ud.cdCompositoVoceTitol) LIKE :valoreclassif ");
                whereWord = " AND ";
            }

            aaFascicolo = filtriFascicoli.getAaFascicolo();
            if (aaFascicolo != null) {
                queryStr.append(whereWord).append(" fasc_ud.aaFascicolo = :aafasc ");
                whereWord = " AND ";
            }

            cdKeyFascicolo = filtriFascicoli.getCdKeyFascicolo();
            if (cdKeyFascicolo != null) {
                queryStr.append(whereWord).append(" fasc_ud.cdKeyFascicolo = :cdfasc ");
                whereWord = " AND ";
            }

            aaFascRangeDa = filtriFascicoli.getAaFascicoloDa();
            aaFascRangeA = filtriFascicoli.getAaFascicoloA();
            if (aaFascRangeDa != null && aaFascRangeA != null) {
                queryStr.append(whereWord).append(" (fasc_ud.aaFascicolo BETWEEN :aafasc_da AND :aafasc_a) ");
                whereWord = " AND ";
            }

            cdFascRangeDa = filtriFascicoli.getCdKeyFascicoloDa();
            cdFascRangeA = filtriFascicoli.getCdKeyFascicoloA();
            if (cdFascRangeDa != null && cdFascRangeA != null) {
                cdFascRangeDa = StringPadding.padString(cdFascRangeDa, "0", 12, StringPadding.PADDING_LEFT);
                cdFascRangeA = StringPadding.padString(cdFascRangeA, "0", 12, StringPadding.PADDING_LEFT);
                queryStr.append(whereWord)
                        .append(" LPAD( fasc_ud.cdKeyFascicolo, 12, '0') BETWEEN :cdfasc_da AND :cdfasc_a ");
                whereWord = " AND ";
            }

            tipoFascIn = filtriFascicoli.getNmTipoFascicolo();
            if (tipoFascIn != null) {
                queryStr.append(whereWord).append(" fasc_ud.idTipoFascicolo = :tipofascin ");
                whereWord = " AND ";
            }

            dtApeFascRangeDa = filtriFascicoli.getDtApeFascicoloDa();
            dtApeFascRangeA = filtriFascicoli.getDtApeFascicoloA();
            if (dtApeFascRangeDa != null && dtApeFascRangeA != null) {
                queryStr.append(whereWord).append(" (fasc_ud.dtApeFascicolo BETWEEN :dtapefasc_da AND :dtapefasc_a) ");
                whereWord = " AND ";
            }

            dtChiuFascRangeDa = filtriFascicoli.getDtChiuFascicoloDa();
            dtChiuFascRangeA = filtriFascicoli.getDtChiuFascicoloA();
            if (dtChiuFascRangeDa != null && dtChiuFascRangeA != null) {
                queryStr.append(whereWord)
                        .append(" (fasc_ud.dtChiuFascicolo BETWEEN :dtchiufasc_da AND :dtchiufasc_a) ");
                whereWord = " AND ";
            }

            oggettoFasc = filtriFascicoli.getDsOggettoFascicolo();
            if (oggettoFasc != null) {
                queryStr.append(whereWord).append(" UPPER(fasc_ud.dsOggettoFascicolo) LIKE :dsoggfasc ");
                whereWord = " AND ";
            }

            queryStr.append(") ");
        }

        String valoreColl = null;
        String valoreColl2 = null;
        String valorereg = null;
        BigDecimal valoreanno = null;
        String valorenumero = null;

        // FILTRI SU COLLEGAMENTI
        // Se il filtro "Con collegamento" vale "Sì"
        if (filtriCollegamenti.getConCollegamento() != null && filtriCollegamenti.getConCollegamento().equals("1")) {
            queryStr.append(whereWord).append(
                    " exists (select 1 from AroLinkUnitaDoc link where link.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Collegamento risolto" vale "Sì"
            if (filtriCollegamenti.getCollegamentoRisolto() != null
                    && filtriCollegamenti.getCollegamentoRisolto().equals("1")) {
                queryStr.append(whereWord).append(" link.aroUnitaDocLink is not null ");
            }
            // Se il filtro "Collegamento risolto" vale "No"
            if (filtriCollegamenti.getCollegamentoRisolto() != null
                    && filtriCollegamenti.getCollegamentoRisolto().equals("0")) {
                queryStr.append(whereWord).append(" link.aroUnitaDocLink is null ");
            }
            // Se il filtro "Descr. collegamento" è valorizzato
            valoreColl = filtriCollegamenti.getDsLinkUnitaDoc();
            if (valoreColl != null) {
                queryStr.append(whereWord).append(" UPPER(link.dsLinkUnitaDoc) LIKE :valorecoll ");
            }
            // Se il filtro "Registro" è valorizzato
            valorereg = filtriCollegamenti.getCdRegistroKeyUnitaDocLink();
            if (valorereg != null && !valorereg.equals("")) {
                queryStr.append(whereWord).append(" link.cdRegistroKeyUnitaDocLink = :valorereg ");
            }
            // Se il filtro "Anno" è valorizzato
            valoreanno = filtriCollegamenti.getAaKeyUnitaDocLink();
            if (valoreanno != null) {
                queryStr.append(whereWord).append(" link.aaKeyUnitaDocLink = :valoreanno ");
            }
            // Se il filtro "Numero" è valorizzato
            valorenumero = filtriCollegamenti.getCdKeyUnitaDocLink();
            if (valorenumero != null) {
                queryStr.append(whereWord).append(" link.cdKeyUnitaDocLink = :valorenumero ");
            }
            queryStr.append(") ");
        }
        // Se il filtro "Con collegamento" vale "No"
        if (filtriCollegamenti.getConCollegamento() != null && filtriCollegamenti.getConCollegamento().equals("0")) {
            queryStr.append(whereWord).append(
                    " not exists (select 1 from AroLinkUnitaDoc link1 where link1.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");
            queryStr.append(") ");
        }
        // Se il filtro "E' oggetto di collegamento" vale "Sì"
        if (filtriCollegamenti.getIsOggettoCollegamento() != null
                && filtriCollegamenti.getIsOggettoCollegamento().equals("1")) {
            queryStr.append(whereWord).append(
                    " exists (select 1 from AroLinkUnitaDoc link2 where link2.aroUnitaDocLink.idUnitaDoc = u.idUnitaDoc ");

            // Se il filtro "Descr. collegamento oggetto" è valorizzato
            valoreColl2 = filtriCollegamenti.getDsLinkUnitaDocOggetto();
            if (valoreColl2 != null) {
                queryStr.append(whereWord).append(" UPPER(link2.dsLinkUnitaDoc) LIKE :valorecoll2 ");
            }
            queryStr.append(") ");
        }
        // Se il filtro "Con collegamento" vale "No"
        if (filtriCollegamenti.getIsOggettoCollegamento() != null
                && filtriCollegamenti.getIsOggettoCollegamento().equals("0")) {
            queryStr.append(whereWord).append(
                    " not exists (select 1 from AroLinkUnitaDoc link3 where link3.aroUnitaDocLink.idUnitaDoc = u.idUnitaDoc ");
            queryStr.append(") ");
        }

        if (filtri.getDsClassif() != null || filtri.getCdFascic() != null || filtri.getDsOggettoFascic() != null
                || filtri.getCdSottofascic() != null || filtri.getDsOggettoSottofascic() != null) {
            queryStr.append(whereWord)
                    .append(" (exists (select 1 from AroUnitaDoc ud where ud.idUnitaDoc = u.idUnitaDoc ");
            if (filtri.getDsClassif() != null) {
                queryStr.append(" and UPPER(ud.dsClassifPrinc) like :classif ");
            }
            if (filtri.getCdFascic() != null) {
                queryStr.append(" and UPPER(ud.cdFascicPrinc) like :fascic ");
            }
            if (filtri.getDsOggettoFascic() != null) {
                queryStr.append(" and UPPER(ud.dsOggettoFascicPrinc) like :oggFascic ");
            }
            if (filtri.getCdSottofascic() != null) {
                queryStr.append(" and UPPER(ud.cdSottofascicPrinc) like :sottoFascic ");
            }
            if (filtri.getDsOggettoSottofascic() != null) {
                queryStr.append(" and UPPER(ud.dsOggettoSottofascicPrinc) like :oggSottoFascic ");
            }

            queryStr.append(
                    ") or exists (select 1 from AroArchivSec arch_sec where arch_sec.aroUnitaDoc.idUnitaDoc = u.idUnitaDoc ");
            if (filtri.getDsClassif() != null) {
                queryStr.append(" and UPPER(arch_sec.dsClassif) like :classif ");
            }
            if (filtri.getCdFascic() != null) {
                queryStr.append(" and UPPER(arch_sec.cdFascic) like :fascic ");
            }
            if (filtri.getDsOggettoFascic() != null) {
                queryStr.append(" and UPPER(arch_sec.dsOggettoFascic) like :oggFascic ");
            }
            if (filtri.getCdSottofascic() != null) {
                queryStr.append(" and UPPER(arch_sec.cdSottofascic) like :sottoFascic ");
            }
            if (filtri.getDsOggettoSottofascic() != null) {
                queryStr.append(" and UPPER(arch_sec.dsOggettoSottofascic) like :oggSottoFascic ");
            }
            queryStr.append(")) ");
            whereWord = " AND ";
        }

        List<BigDecimal> subStruts = filtri.getNmSubStrut();
        if (!subStruts.isEmpty()) {
            queryStr.append(whereWord).append(" u.idSubStrut IN (:subStruts) ");
        }
        // ordina per dsKeyDoc crescente
        queryStr.append(" ORDER BY u.dsKeyOrd");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // non avendo passato alla query i parametri di ricerca, devo passarli ora

        if (filtriComponenti.getNmTipoStrutDoc() != null) {
            query.setParameter("tipoStrutDocIn", filtriComponenti.getNmTipoStrutDoc().longValue());
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("listtipoud", idTipoUnitaDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (cdVersioneXsdDoc != null) {
            query.setParameter("cdVersioneXsdDoc", cdVersioneXsdDoc);
        }

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            query.setParameter("setregistro", cdRegistroUnitaDocSet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (cdKeyDocVers != null) {
            query.setParameter("cdKeyDocVers", "%" + cdKeyDocVers.toUpperCase() + "%");
        }

        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }

        if (codiceNumContiene != null) {
            query.setParameter("codicein_contiene", "%" + codiceNumContiene + "%");
        }

        if (presenza != null) {
            query.setParameter("presenzain", presenza);
        }

        if (!esitoveriffirmeList.isEmpty()) {
            query.setParameter("listaesitoveriffirme", esitoveriffirmeList);
        }

        if (dataDa != null && dataA != null) {
            query.setParameter("datada", dataDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataA, TemporalType.TIMESTAMP);
        }
        if (profiloNorm != null) {
            query.setParameter("profiloNorm", profiloNorm);
        }
        if (forzaAcc != null) {
            query.setParameter("forzaaccin", forzaAcc);
        }

        if (forzaConserva != null) {
            query.setParameter("forzaconservain", forzaConserva);
        }

        if (forzaColleg != null) {
            query.setParameter("forzacollegin", forzaColleg);
        }

        // if (forzaHash != null) {
        // query.setParameter("forzahashin", forzaHash);
        // }
        //
        // if (forzaFmtNumero != null) {
        // query.setParameter("forzafmtnumeroin", forzaFmtNumero);
        // }
        //
        // if (forzaFmtFile != null) {
        // query.setParameter("forzafmtfilein", forzaFmtFile);
        // }

        if (cdVersioneWs != null) {
            query.setParameter("cdVersioneWs", cdVersioneWs);
        }

        if (unitaDocAnnul != null) {
            query.setParameter("unitaDocAnnul", unitaDocAnnul);
        }

        if (docAggiunti != null) {
            query.setParameter("docAggiunti", docAggiunti);
        }

        if (flAggMeta != null) {
            query.setParameter("flAggMeta", flAggMeta);
        }

        if (flHashVers != null) {
            query.setParameter("flHashVers", flHashVers);
        }

        if (tiStatoConservazione != null) {
            query.setParameter("tiStatoConservazione", tiStatoConservazione);
        }

        if (tiStatoUdElencoVers != null) {
            query.setParameter("tiStatoUdElencoVers", tiStatoUdElencoVers);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("listtipodoc", idTipoDocList);
        }

        if (cdVersioneXsdUd != null) {
            query.setParameter("cdVersioneXsdUd", cdVersioneXsdUd);
        }

        if (!sisMigrazList.isEmpty()) {
            query.setParameter("listasismigraz", sisMigrazList);
        }

        if (filtri.getDataMetaDa() != null && filtri.getDataMetaA() != null) {
            query.setParameter("datametada", filtri.getDataMetaDa(), TemporalType.DATE);
            query.setParameter("datametaa", filtri.getDataMetaA(), TemporalType.DATE);
        }

        if (tiDoc != null) {
            query.setParameter("tiDoc", tiDoc);
        }

        if (oggettoMeta != null) {
            query.setParameter("dloggettounitadocin", "%" + oggettoMeta.toUpperCase() + "%");
        }

        if (autoreDocMeta != null) {
            query.setParameter("dldocmetain", "%" + autoreDocMeta.toUpperCase() + "%");
        }

        if (descrizioneDocMeta != null) {
            query.setParameter("dsautoredocin", "%" + descrizioneDocMeta.toUpperCase() + "%");
        }

        if (idStruttura != null) {
            query.setParameter("idstrutin", idStruttura);
        }

        if (tipoConservazione != null) {
            query.setParameter("ticonservazionein", tipoConservazione);
        }

        int contaIdAttrib = 0;
        for (int i = 0; i < mappone.size(); i++) {
            DatiSpecQueryParams dsqp = mappone.get(i);

            if (!dsqp.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.E_UNO_FRA.name())) {
                if (StringUtils.isNotBlank(dsqp.getDlValore())) {
                    query.setParameter("valorein" + i, dsqp.getDlValore().toUpperCase());
                }
            } else {
                String[] inParams = dsqp.getDlValore().toUpperCase().trim().replaceAll("\\s*,\\s*", ",").split(",");
                query.setParameter("valorein" + i, Arrays.asList(inParams));
            }
            for (int j = 0; j < dsqp.getIdAttribDatiSpec().size(); j++) {
                query.setParameter("idattribdatispecin" + contaIdAttrib, dsqp.getIdAttribDatiSpec().get(j));
                if (dsqp.getNmSistemaMigraz().get(j) != null) {
                    query.setParameter("nmsistemamigrazin" + contaIdAttrib, dsqp.getNmSistemaMigraz().get(j));
                }
                contaIdAttrib++;
            }
        }

        if (valoreClassif != null) {
            query.setParameter("valoreclassif", "%" + valoreClassif.toUpperCase() + "%");
        }

        if (aaFascicolo != null) {
            query.setParameter("aafasc", aaFascicolo);
        }

        if (cdKeyFascicolo != null) {
            query.setParameter("cdfasc", cdKeyFascicolo);
        }

        if (aaFascRangeDa != null && aaFascRangeA != null) {
            query.setParameter("aafasc_da", aaFascRangeDa);
            query.setParameter("aafasc_a", aaFascRangeA);
        }

        if (cdFascRangeDa != null && cdFascRangeA != null) {
            query.setParameter("cdfasc_da", cdFascRangeDa);
            query.setParameter("cdfasc_a", cdFascRangeA);
        }

        if (tipoFascIn != null) {
            query.setParameter("tipofascin", tipoFascIn);
        }

        if (dtApeFascRangeDa != null && dtApeFascRangeA != null) {
            query.setParameter("dtapefasc_da", dtApeFascRangeDa);
            query.setParameter("dtapefasc_a", dtApeFascRangeA);
        }

        if (dtChiuFascRangeDa != null && dtChiuFascRangeA != null) {
            query.setParameter("dtchiufasc_da", dtChiuFascRangeDa);
            query.setParameter("dtchiufasc_a", dtChiuFascRangeA);
        }

        if (oggettoFasc != null) {
            query.setParameter("dsoggfasc", "%" + oggettoFasc.toUpperCase() + "%");
        }

        if (valoreColl != null) {
            query.setParameter("valorecoll", "%" + valoreColl.toUpperCase() + "%");
        }
        if (valoreColl2 != null) {
            query.setParameter("valorecoll2", "%" + valoreColl2.toUpperCase() + "%");
        }

        if (valorereg != null && !valorereg.equals("")) {
            query.setParameter("valorereg", valorereg);
        }
        if (valoreanno != null) {
            query.setParameter("valoreanno", valoreanno);
        }
        if (valorenumero != null) {
            query.setParameter("valorenumero", valorenumero);
        }

        if (filtri.getDsClassif() != null) {
            query.setParameter("classif", "%" + filtri.getDsClassif().toUpperCase() + "%");
        }
        if (filtri.getCdFascic() != null) {
            query.setParameter("fascic", "%" + filtri.getCdFascic().toUpperCase() + "%");
        }
        if (filtri.getDsOggettoFascic() != null) {
            query.setParameter("oggFascic", "%" + filtri.getDsOggettoFascic().toUpperCase() + "%");
        }
        if (filtri.getCdSottofascic() != null) {
            query.setParameter("sottoFascic", "%" + filtri.getCdSottofascic().toUpperCase() + "%");
        }
        if (filtri.getDsOggettoSottofascic() != null) {
            query.setParameter("oggSottoFascic", "%" + filtri.getDsOggettoSottofascic().toUpperCase() + "%");
        }

        // Parametri Filtri Componente
        if (filtriComponenti.getNmTipoStrutDoc() != null) {
            query.setParameter("tipoStrutDocIn", filtriComponenti.getNmTipoStrutDoc().longValue());
        }
        if (filtriComponenti.getNmTipoCompDoc() != null) {
            query.setParameter("tipoCompDocIn", filtriComponenti.getNmTipoCompDoc().longValue());
        }
        if (filtriComponenti.getDsNomeCompVers() != null) {
            query.setParameter("nomeCompVersIn", "%" + filtriComponenti.getDsNomeCompVers().toUpperCase() + "%");
        }
        if (filtriComponenti.getDlUrnCompVers() != null) {
            query.setParameter("urnCompVersIn", "%" + filtriComponenti.getDlUrnCompVers().toUpperCase() + "%");
        }
        if (filtriComponenti.getDsHashFileVers() != null) {
            query.setParameter("hashFileVersIn", filtriComponenti.getDsHashFileVers());
        }
        if (filtriComponenti.getNmMimetypeFile() != null) {
            query.setParameter("mimeTypeIn", "%" + filtriComponenti.getNmMimetypeFile().toUpperCase() + "%");
        }
        if (filtriComponenti.getNmFormatoFileVers() != null) {
            query.setParameter("formatoFileDocIn", filtriComponenti.getNmFormatoFileVers().longValue());
        }
        if (fileSizeDa != null && fileSizeA != null) {
            query.setParameter("filesizedain", fileSizeDa);
            query.setParameter("filesizeain", fileSizeA);
        }
        if (filtriComponenti.getDsFormatoRapprCalc() != null) {
            query.setParameter("formatoRapprCalcIn", filtriComponenti.getDsFormatoRapprCalc());
        }
        if (filtriComponenti.getDsFormatoRapprEstesoCalc() != null) {
            query.setParameter("formatoRapprCalcEstesoIn", filtriComponenti.getDsFormatoRapprEstesoCalc());
        }
        if (filtriComponenti.getFlCompFirmato() != null) {
            // Gli devo sempre passare 1
            query.setParameter("compFirmatoIn", "1");
        }
        if (filtriComponenti.getTiEsitoContrConforme() != null) {
            query.setParameter("esitoContrConformeIn", filtriComponenti.getTiEsitoContrConforme());
        }
        if (filtriComponenti.getDsRifTempVers() != null) {
            query.setParameter("dsRifTempVers", "%" + filtriComponenti.getDsRifTempVers().toUpperCase() + "%");
        }

        if ((dataValDa != null) && (dataValA != null)) {
            query.setParameter("datavalda", dataValDa);
            query.setParameter("datavala", dataValA);
        }

        if (filtriComponenti.getTiEsitoContrFormatoFile() != null) {
            query.setParameter("esitoContrFormatoFileIn", filtriComponenti.getTiEsitoContrFormatoFile());
        }
        if (filtriComponenti.getTiEsitoVerifFirma() != null) {
            query.setParameter("esitoVerifFirmeIn", filtriComponenti.getTiEsitoVerifFirma());
        }
        if (filtriComponenti.getTiEsitoVerifFirmeChiuse() != null) {
            query.setParameter("esitoVerifFirmeChiusIn", filtriComponenti.getTiEsitoVerifFirmeChiuse());
        }
        if (filtriComponenti.getDsHashFileCalc() != null) {
            query.setParameter("hashFileCalcIn", filtriComponenti.getDsHashFileCalc());
        }
        if (filtriComponenti.getDsAlgoHashFileCalc() != null) {
            query.setParameter("algoHashFileCalcIn", filtriComponenti.getDsAlgoHashFileCalc());
        }
        if (filtriComponenti.getCdEncodingHashFileCalc() != null) {
            query.setParameter("encodingHashFileCalcIn", filtriComponenti.getCdEncodingHashFileCalc());
        }
        if (filtriComponenti.getDsUrnCompCalc() != null) {
            query.setParameter("urnCompCalcIn", "%" + filtriComponenti.getDsUrnCompCalc().toUpperCase() + "%");
        }

        if ((dataCompDa != null) && (dataCompA != null)) {
            query.setParameter("dataCompDaIn", dataCompDa);
            query.setParameter("dataCompAIn", dataCompA);
        }

        if (filtriComponenti.getFlForzaAccettazioneComp() != null) {
            query.setParameter("flForzaAccIn", filtriComponenti.getFlForzaAccettazioneComp());
        }

        if (filtriComponenti.getFlForzaConservazioneComp() != null) {
            query.setParameter("flForzaConsIn", filtriComponenti.getFlForzaConservazioneComp());
        }

        if (filtriComponenti.getTiSupportoComp() != null) {
            query.setParameter("tiSupportoCompIn", filtriComponenti.getTiSupportoComp());
        }

        if (filtriComponenti.getNmTipoRapprComp() != null) {
            query.setParameter("idTipoRapprCompIn", filtriComponenti.getNmTipoRapprComp().longValue());
        }

        if (filtriComponenti.getDsIdCompVers() != null) {
            query.setParameter("idCompDocIn", "%" + filtriComponenti.getDsIdCompVers().toUpperCase() + "%");
        }

        if (filtriFirmatari.getCdFirmatario() != null) {
            query.setParameter("cdFirmatario", "%" + filtriFirmatari.getCdFirmatario().toUpperCase() + "%");
        }
        if (filtriFirmatari.getNmCognomeFirmatario() != null) {
            query.setParameter("nmCognomeFirmatario",
                    "%" + filtriFirmatari.getNmCognomeFirmatario().toUpperCase() + "%");
        }
        if (filtriFirmatari.getNmFirmatario() != null) {
            query.setParameter("nmFirmatario", "%" + filtriFirmatari.getNmFirmatario().toUpperCase() + "%");
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        if (lazy) {
            return lazyListHelper.getTableBean(query, getAroVRicUnitaDocTableBeanFromResultListFunction(addButton),
                    "u.idUnitaDoc");
        } else {
            return getAroVRicUnitaDocTableBeanFromResultList(query.getResultList(), addButton);
        }
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicDatiSpecViewBeanPlainFilter(
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            List<DecCriterioDatiSpecBean> listaDatiSpecOnLine, BigDecimal idStruttura, boolean addButton,
            FiltriUnitaDocumentarieDatiSpecPlain filtri, boolean lazy, boolean almenoUnTipoUnitaDocSePerRicercaDatiSpec,
            boolean almenoUnTipoDocSelPerRicercaDatiSpec) throws EMFError {
        String whereWord = " WHERE ";
        // Creo la parte iniziale della query di ricerca
        StringBuilder queryInvolucro = new StringBuilder(RIC_UD_DATI_SPEC_BASE + RIC_UD_DATI_SPEC_NM_TIPO_DOC_PRINC
                + RIC_UD_DATI_SPEC_STATI_ELENCO_VERS + RIC_UD_DATI_SPEC_BASE_FROM);

        StringBuilder queryWhereConditions = new StringBuilder();

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            // Inserimento nella query del filtro Registro
            queryWhereConditions.append(whereWord).append(" ud.cd_Registro_Key_Unita_Doc IN (:setregistro) ");
            whereWord = "AND ";
        }

        if (!idTipoDocList.isEmpty()) {
            // Inserimento nella query del filtro Tipo doc
            queryWhereConditions.append(whereWord).append(" doc.id_tipo_doc IN (:listtipodoc) ");
            whereWord = "AND ";
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            // Inserimento nella query del filtro Tipo doc
            queryWhereConditions.append(whereWord).append(" ud.id_tipo_unita_doc IN (:listtipoud) ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sui range di anno e numero
        BigDecimal annoRangeDa = filtri.getAaKeyUnitaDocDa();
        BigDecimal annoRangeA = filtri.getAaKeyUnitaDocA();
        String codiceRangeDa = filtri.getCdKeyUnitaDocDa();
        String codiceRangeA = filtri.getCdKeyUnitaDocA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryWhereConditions.append(whereWord).append(" (ud.aa_Key_Unita_Doc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryWhereConditions.append(whereWord)
                    .append(" LPAD( ud.cd_Key_Unita_Doc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro sottostruttura
        List<BigDecimal> subStruts = filtri.getNmSubStrut();
        if (!subStruts.isEmpty()) {
            queryWhereConditions.append(whereWord).append(" ud.id_Sub_Strut IN (:subStruts) ");
        }

        whereWord = " AND ";
        queryWhereConditions.append(whereWord).append(" ud.ti_annul is null ");

        // UTILIZZO DEI DATI SPECIFICI
        String cdVersioneXsdUd = filtri.getCdVersioneXsdUd();
        String cdVersioneXsdDoc = filtri.getCdVersioneXsdDoc();
        ReturnParams params = volumeHelper.buildConditionsForRicDatiSpec(listaDatiSpecOnLine, cdVersioneXsdUd,
                cdVersioneXsdDoc, annoRangeDa, annoRangeA, idStruttura);
        List<DatiSpecQueryParams> mappone = params.getMappone();

        // Aggrego le varie sottoquery
        queryInvolucro.append(queryWhereConditions);
        queryInvolucro.append(params.getQuery());

        // ordina per dsKeyDoc crescente
        queryInvolucro.append(" ORDER BY ud.ds_Key_Ord");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createNativeQuery(queryInvolucro.toString());

        // non avendo passato alla query i parametri di ricerca dei dati specifici, devo passarli ora
        int contaIdAttrib = 0;
        for (int i = 0; i < mappone.size(); i++) {
            DatiSpecQueryParams dsqp = mappone.get(i);

            if (!dsqp.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.E_UNO_FRA.name())) {
                if (StringUtils.isNotBlank(dsqp.getDlValore())) {
                    query.setParameter("valorein" + i, dsqp.getDlValore().toUpperCase());
                }
            } else {
                String[] inParams = dsqp.getDlValore().toUpperCase().trim().replaceAll("\\s*,\\s*", ",").split(",");
                query.setParameter("valorein" + i, Arrays.asList(inParams));
            }
            for (int j = 0; j < dsqp.getIdAttribDatiSpec().size(); j++) {
                query.setParameter("idattribdatispecin" + contaIdAttrib, dsqp.getIdAttribDatiSpec().get(j));
                if (dsqp.getNmSistemaMigraz().get(j) != null) {
                    query.setParameter("nmsistemamigrazin" + contaIdAttrib, dsqp.getNmSistemaMigraz().get(j));
                }
                contaIdAttrib++;
            }
        }

        // Passaggio parametri alla query
        if (!idTipoDocList.isEmpty()) {
            query.setParameter("listtipodoc", idTipoDocList);
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("listtipoud", idTipoUnitaDocList);
        }

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            query.setParameter("setregistro", cdRegistroUnitaDocSet);
        }

        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }

        if (!subStruts.isEmpty()) {
            query.setParameter("subStruts", subStruts);
        }

        // Mi faccio restituire uno stream (più performante) e lo converto in oggetti AroVRicUnitaDoc
        List<AroVRicUnitaDoc> aroVRicUnitaDocList = convertStreamToObjectList(query);

        if (lazy) {
            return lazyListHelper.getTableBean(query, getAroVRicUnitaDocTableBeanFromResultListFunction(addButton),
                    "u.idUnitaDoc");
        } else {
            // return getAroVRicUnitaDocTableBeanFromResultList(query.getResultList(), addButton);
            return getAroVRicUnitaDocTableBeanFromResultList(aroVRicUnitaDocList, addButton);
        }
    }

    public List<AroVRicUnitaDoc> convertStreamToObjectList(Query query) {
        try (Stream<Object> objList = query.getResultStream()) {
            return objList.map(obj -> {
                Object[] row = (Object[]) obj;
                return new AroVRicUnitaDoc((BigDecimal) row[0], // idUnitaDoc
                        (BigDecimal) row[1], // aaKeyUnitaDoc
                        (String) row[2], // cdKeyUnitaDoc
                        (String) row[3], // cdRegistroKeyUnitaDoc
                        (Date) row[4], // dtCreazione
                        (Date) row[5], // dtRegUnitaDoc
                        ((Character) row[6]).toString(), // flUnitaDocFirmato
                        (String) row[7], // tiEsitoVerifFirme
                        (String) row[8], // dsMsgEsitoVerifFirme
                        (String) row[9], // nmTipoUnitaDoc
                        ((Character) row[10]).toString(), // flForzaAccettazione
                        ((Character) row[11]).toString(), // flForzaConservazione
                        (String) row[12], // dsKeyOrd
                        (BigDecimal) row[13], // niAlleg
                        (BigDecimal) row[14], // niAnnessi
                        (BigDecimal) row[15], // niAnnot
                        (String) row[16], // tiStatoConservazione
                        (String) row[17], // nmTipoDocPrinc
                        (String) row[18] // dsListaStatiElencoVers
                );
            }).collect(Collectors.toList());
        }
    }

    @SuppressWarnings("rawtypes")
    private Function<List, AroVRicUnitaDocTableBean> getAroVRicUnitaDocTableBeanFromResultListFunction(
            boolean addButton) {
        return list -> getAroVRicUnitaDocTableBeanFromResultList(list, addButton);
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocTableBeanFromResultList(List<AroVRicUnitaDoc> listaUD,
            boolean addButton) {
        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                for (AroVRicUnitaDoc ud : listaUD) {
                    AroVRicUnitaDocRowBean rowBean = (AroVRicUnitaDocRowBean) Transform.entity2RowBean(ud);
                    // Se sto gestendo l'aggiunta di ud a una serie, aggiungo questo campo per
                    // inserire il bottone "+"
                    // sulla selectList
                    if (addButton) {
                        rowBean.setString("addSerie", "1");
                    }
                    udTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return udTableBean;
    }

    private String appendJoin(boolean formato, boolean firma, boolean certif, boolean appart) {
        StringBuilder join = new StringBuilder(", AroCompDoc comp JOIN comp.aroStrutDoc strutDoc ");
        StringBuilder where = new StringBuilder("WHERE strutDoc.aroDoc.idDoc = u.idDoc ");
        if (formato) {
            join.append(" JOIN comp.decFormatoFileStandard fileStandard ");
        }
        if (appart) {
            join.append(" JOIN comp.volAppartCompVolumes appartComp JOIN appartComp.volAppartDocVolume appartDoc ");
            where.append(" AND appartDoc.aroDoc.idDoc = u.idDoc AND appartDoc.flValida = '1' ");
        }
        if (firma) {
            join.append(" JOIN comp.aroFirmaComps firma ");
        }
        if (certif) {
            join.append(" JOIN firma.firCertifFirmatario certif ");
        }
        return join.append(where.toString()).toString();
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocsById(List<BigDecimal> ids) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicUnitaDoc (u.id.idUnitaDoc, u.aaKeyUnitaDoc, u.cdKeyUnitaDoc, u.cdRegistroKeyUnitaDoc, u.dtCreazione, u.dtRegUnitaDoc, u.flUnitaDocFirmato, u.tiEsitoVerifFirme, u.dsMsgEsitoVerifFirme, u.nmTipoUnitaDoc, u.flForzaAccettazione, u.flForzaConservazione, u.dsKeyOrd, u.niAlleg, u.niAnnessi, u.niAnnot, u.nmTipoDocPrinc, u.dsListaStatiElencoVers, u.tiStatoConservazione) "
                + "FROM AroVRicUnitaDoc u " + "WHERE u.id.idUnitaDoc IN (:ids) " + "ORDER BY u.dsKeyOrd";

        Query query = getEntityManager().createQuery(queryStr);
        if (!ids.isEmpty()) {
            query.setParameter("ids", ids);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVRicUnitaDoc> listaUd = query.getResultList();

        return resultListToAroVRicUnitaDocRowBeans(listaUd);
    }

    public AroVLisDocTableBean getAroVLisDocTableBeanByIdDoc(Set<BigDecimal> idDocSet) {
        String queryStr = "SELECT u FROM AroVLisDoc u " + "WHERE u.idDoc IN (:idDocSet) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idDocSet", idDocSet);
        List<AroVLisDoc> listaDoc = query.getResultList();
        return getAroVLisDocTableBeanFromResultList(listaDoc);
    }

    private AroVLisDocTableBean getAroVLisDocTableBeanFromResultList(List<AroVLisDoc> listaDoc) {
        AroVLisDocTableBean docTB = new AroVLisDocTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (AroVLisDoc row : listaDoc) {
                    AroVLisDocRowBean rowBean = (AroVLisDocRowBean) Transform.entity2RowBean(row);
                    if (!rowBean.getTiDoc().equals("PRINCIPALE")) {
                        rowBean.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
                    }
                    docTB.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docTB;
    }

    public ElvVLisUpdUdTableBean getElvVLisUpdUdTableBeanByIdUpd(Set<BigDecimal> idUpdUnitaDocSet) {
        String queryStr = "SELECT u FROM ElvVLisUpdUd u " + "WHERE u.idUpdUnitaDoc IN (:idUpdUnitaDocSet) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUpdUnitaDocSet", idUpdUnitaDocSet);
        List<ElvVLisUpdUd> listaUpd = query.getResultList();
        ElvVLisUpdUdTableBean updTB = new ElvVLisUpdUdTableBean();
        try {
            if (listaUpd != null && !listaUpd.isEmpty()) {
                for (ElvVLisUpdUd row : listaUpd) {
                    ElvVLisUpdUdRowBean rowBean = (ElvVLisUpdUdRowBean) Transform.entity2RowBean(row);
                    updTB.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTB;
    }

    // Metodo che restituisce un rowbean con il record trovato in base
    // al''id unità documentaria
    public AroVVisUnitaDocIamRowBean getAroVVisUnitaDocIamRowBean(BigDecimal idud) {
        AroVVisUnitaDocIam rec = getEntityManager().find(AroVVisUnitaDocIam.class, idud);
        AroVVisUnitaDocIamRowBean dettaglioUD = new AroVVisUnitaDocIamRowBean();
        try {
            if (rec != null) {
                dettaglioUD = (AroVVisUnitaDocIamRowBean) Transform.entity2RowBean(rec);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'unita' documentaria" + e.getMessage(), e);
        }

        // Calcolo il campo stato conservazione
        setStatiDocumentari(dettaglioUD);
        addXmlUniDocFromOStoAroVVisUnitaDocIamRowBean(dettaglioUD);
        return dettaglioUD;
    }

    public AroVLisDocTableBean getAroVLisDocTableBean(BigDecimal idud, String tipoDoc) {
        StringBuilder queryStrBuilder = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.AroVLisDoc(u.cdKeyDocVers, u.dlDoc, u.dsAutoreDoc, u.dtCreazione, u.flDocFirmato, u.idDoc, u.idUnitaDoc, u.nmTipoDoc, u.pgDoc, u.tiDoc, u.tiDocOrd, u.tiStatoElencoVers, u.tiEsitoVerifFirme) "
                        + " FROM AroVLisDoc u " + "WHERE u.idUnitaDoc = :idud ");

        if (tipoDoc != null) {
            queryStrBuilder.append(" and u.tiDoc = :tipodoc ");
        }

        queryStrBuilder.append("ORDER BY u.tiDocOrd, u.pgDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStrBuilder.toString());

        query.setParameter("idud", idud);
        if (tipoDoc != null) {
            query.setParameter("tipodoc", tipoDoc);
        }

        return lazyListHelper.getTableBean(query, this::getAroVLisDocTableBeanFromResultList, "idDoc");
    }

    public AroVVisDocIamTableBean getAroVVisDocAggIamTableBean(BigDecimal idud, Date dataCreazioneUd) {
        String queryStr = "SELECT u FROM AroVVisDocIam u " + "WHERE u.idUnitaDoc = :idud "
                + "AND u.dtCreazione > :datacreazioneud ";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idud", idud);
        query.setParameter("datacreazioneud", dataCreazioneUd);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> listaDoc = query.getResultList();

        AroVVisDocIamTableBean listaDocTableBean = new AroVVisDocIamTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                listaDocTableBean = (AroVVisDocIamTableBean) Transform.entities2TableBean(listaDoc);
                addXmlDocFromOStoAroVVisDocIamTableBean(listaDocTableBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDocTableBean;
    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento sia l'object storage (gestito dal parametro
     * <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param tabella
     *            AroVVisDocIamTableBean
     */
    private void addXmlDocFromOStoAroVVisDocIamTableBean(AroVVisDocIamTableBean tabella) {
        for (AroVVisDocIamRowBean riga : tabella) {
            addXmlDocFromOStoAroVVisDocIamRowBean(riga);
        }

    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento sia l'object storage (gestito dal parametro
     * <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            AroVVisDocIamRowBean
     */
    public void addXmlDocFromOStoAroVVisDocIamRowBean(AroVVisDocIamRowBean riga) {
        boolean xmlVuoti = riga.getBlXmlRappDoc() == null && riga.getBlXmlRichDoc() == null
                && riga.getBlXmlRispDoc() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (xmlVuoti) {
            Map<String, String> xmls = objectStorageService.getObjectSipDoc(riga.getIdDoc().longValue());
            /*
             * se recuperati oggetti da O.S.
             */
            if (!xmls.isEmpty()) {
                riga.setBlXmlRappDoc(xmls.get(CostantiDB.TipiXmlDati.RAPP_VERS));
                riga.setBlXmlRichDoc(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA));
                riga.setBlXmlRispDoc(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA));
            }
        }
    }

    /**
     * Nel caso in cui il backend di salvataggio degli XML di versamento sia l'object storage (gestito dal parametro
     * <strong>applicativo</strong>) si possono verificare 2 casi:
     * <ul>
     * <li>gli xml sono <em>ancora</em> sul DB perché non ancora migrati</li>
     * <li>gli xml sono effettivamente sull'object storage</li>
     * </ul>
     * Se si avvera il secondo caso li devo recuperare
     *
     * @param riga
     *            AroVVisUnitaDocIamRowBean
     */
    private void addXmlUniDocFromOStoAroVVisUnitaDocIamRowBean(AroVVisUnitaDocIamRowBean riga) {
        boolean xmlVuoti = riga.getBlXmlRappUd() == null && riga.getBlXmlRichUd() == null
                && riga.getBlXmlRispUd() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (xmlVuoti) {
            Map<String, String> xmls = objectStorageService.getObjectSipUnitaDoc(riga.getIdUnitaDoc().longValue());
            // recupero oggetti da O.S. (se presenti)
            if (!xmls.isEmpty()) {
                riga.setBlXmlRappUd(xmls.get(CostantiDB.TipiXmlDati.RAPP_VERS));
                riga.setBlXmlRichUd(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA));
                riga.setBlXmlRispUd(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA));
                riga.setBlXmlIndexUd(xmls.get(CostantiDB.TipiXmlDati.INDICE_FILE));
            }
        }

    }

    public AroVLisLinkUnitaDocTableBean getAroVLisLinkUnitaDocTableBean(BigDecimal idud, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisLinkUnitaDoc u WHERE u.id.idUnitaDoc = :idud ORDER BY u.cdKeyUnitaDocLink";
        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idud", idud);

        query.setMaxResults(maxResults);
        return lazyListHelper.getTableBean(query, this::getAroVLisLinkUnitaDocTableBeanFromResultList);
    }

    private AroVLisLinkUnitaDocTableBean getAroVLisLinkUnitaDocTableBeanFromResultList(
            List<AroVLisLinkUnitaDoc> listaLink) {
        AroVLisLinkUnitaDocTableBean listaLinkTableBean = new AroVLisLinkUnitaDocTableBean();
        try {
            if (listaLink != null && !listaLink.isEmpty()) {
                listaLinkTableBean = (AroVLisLinkUnitaDocTableBean) Transform.entities2TableBean(listaLink);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaLinkTableBean;
    }

    public AroVLisArchivUnitaDocTableBean getAroVLisArchivUnitaDocTableBean(BigDecimal idud, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisArchivUnitaDoc u WHERE u.idUnitaDoc = :idud ORDER BY u.dsClassif, u.cdFascic";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idud", idud);

        query.setMaxResults(maxResults);
        return lazyListHelper.getTableBean(query, this::resultListToAroVLisArchivUnitaDocTableBean);
    }

    private AroVLisArchivUnitaDocTableBean resultListToAroVLisArchivUnitaDocTableBean(
            List<AroVLisArchivUnitaDoc> listaArchiv) {
        AroVLisArchivUnitaDocTableBean listaArchivTableBean = new AroVLisArchivUnitaDocTableBean();
        try {
            if (listaArchiv != null && !listaArchiv.isEmpty()) {
                listaArchivTableBean = (AroVLisArchivUnitaDocTableBean) Transform.entities2TableBean(listaArchiv);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaArchivTableBean;
    }

    // Metodi per la visualizzazione/ricerca su DOCUMENTI
    public AroVVisDocIamRowBean getAroVVisDocIamRowBean(BigDecimal iddoc) {
        String queryStr = "SELECT u FROM AroVVisDocIam u WHERE u.idDoc = :iddoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> doc = query.getResultList();

        AroVVisDocIamRowBean docRowBean = new AroVVisDocIamRowBean();
        try {
            if (doc != null && !doc.isEmpty()) {
                docRowBean = (AroVVisDocIamRowBean) Transform.entity2RowBean(doc.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docRowBean;
    }

    // Metodi per la visualizzazione/ricerca su DOCUMENTI
    public AroVVisDocIamTableBean getAroVVisDocIamTableBean(BigDecimal iddoc) {
        String queryStr = "SELECT u FROM AroVVisDocIam u WHERE u.idDoc = :iddoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisDocIam> doc = query.getResultList();

        AroVVisDocIamTableBean docTableBean = new AroVVisDocIamTableBean();
        try {
            if (doc != null && !doc.isEmpty()) {
                docTableBean = (AroVVisDocIamTableBean) Transform.entities2TableBean(doc);
                addXmlDocFromOStoAroVVisDocIamTableBean(docTableBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return docTableBean;
    }

    // Metodi per la visualizzazione/ricerca su AGGIORNAMENTI METADATI
    public AroVVisUpdUnitaDocRowBean getAroVVisUpdUnitaDocRowBean(BigDecimal idupdunitadoc) {
        String queryStr = "SELECT u FROM AroVVisUpdUnitaDoc u WHERE u.idUpdUnitaDoc = :idupdunitadoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", idupdunitadoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisUpdUnitaDoc> upd = query.getResultList();

        AroVVisUpdUnitaDocRowBean updRowBean = new AroVVisUpdUnitaDocRowBean();
        try {
            if (upd != null && !upd.isEmpty()) {
                updRowBean = (AroVVisUpdUnitaDocRowBean) Transform.entity2RowBean(upd.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updRowBean;
    }

    // Metodi per la visualizzazione/ricerca su AGGIORNAMENTI METADATI
    public AroVVisUpdUnitaDocTableBean getAroVVisUpdUnitaDocTableBean(BigDecimal idupdunitadoc) {
        String queryStr = "SELECT u FROM AroVVisUpdUnitaDoc u WHERE u.idUpdUnitaDoc = :idupdunitadoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", idupdunitadoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisUpdUnitaDoc> upd = query.getResultList();

        AroVVisUpdUnitaDocTableBean updTableBean = new AroVVisUpdUnitaDocTableBean();
        try {
            if (upd != null && !upd.isEmpty()) {
                updTableBean = (AroVVisUpdUnitaDocTableBean) Transform.entities2TableBean(upd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTableBean;
    }

    public AroVLisCompDocTableBean getAroVLisCompDocTableBean(BigDecimal iddoc, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisCompDoc u WHERE u.idDoc = :iddoc ORDER BY u.niOrdCompDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisCompDoc> listaCompDoc = query.getResultList();

        AroVLisCompDocTableBean listaCompDocTableBean = new AroVLisCompDocTableBean();
        try {
            if (listaCompDoc != null && !listaCompDoc.isEmpty()) {
                listaCompDocTableBean = (AroVLisCompDocTableBean) Transform.entities2TableBean(listaCompDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaCompDocTableBean;
    }

    public AroVLisUpdDocUnitaDocTableBean getAroVLisUpdDocUnitaDocTableBean(BigDecimal idupd, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisUpdDocUnitaDoc u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tiDocOrd, u.pgDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdDocUnitaDoc> listaUpdDoc = query.getResultList();

        AroVLisUpdDocUnitaDocTableBean listaUpdDocUnitaDocTableBean = new AroVLisUpdDocUnitaDocTableBean();
        try {
            if (listaUpdDoc != null && !listaUpdDoc.isEmpty()) {
                listaUpdDocUnitaDocTableBean = (AroVLisUpdDocUnitaDocTableBean) Transform
                        .entities2TableBean(listaUpdDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listaUpdDocUnitaDocTableBean;
    }

    public AroVLisVolNoValDocTableBean getAroVLisVolNoValDocTableBean(BigDecimal iddoc, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisVolNoValDoc u WHERE u.id.idDoc = :iddoc ORDER BY u.dtCreazione DESC";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisVolNoValDoc> listaVolNoValDoc = query.getResultList();

        AroVLisVolNoValDocTableBean listaVolNoValDocTableBean = new AroVLisVolNoValDocTableBean();
        try {
            if (listaVolNoValDoc != null && !listaVolNoValDoc.isEmpty()) {
                listaVolNoValDocTableBean = (AroVLisVolNoValDocTableBean) Transform
                        .entities2TableBean(listaVolNoValDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaVolNoValDocTableBean;
    }

    public AroVLisUpdCompUnitaDocTableBean getAroVLisUpdCompUnitaDocTableBean(BigDecimal idupd, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisUpdCompUnitaDoc u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tiDocOrd, u.pgDoc, u.niOrdCompDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdCompUnitaDoc> listaUpdCompUnitaDoc = query.getResultList();

        AroVLisUpdCompUnitaDocTableBean listaUpdCompUnitaDocTableBean = new AroVLisUpdCompUnitaDocTableBean();
        try {
            if (listaUpdCompUnitaDoc != null && !listaUpdCompUnitaDoc.isEmpty()) {
                listaUpdCompUnitaDocTableBean = (AroVLisUpdCompUnitaDocTableBean) Transform
                        .entities2TableBean(listaUpdCompUnitaDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdCompUnitaDocTableBean;
    }

    public AroVLisUpdKoRisoltiTableBean getAroVLisUpdKoRisoltiTableBean(BigDecimal idupd, int maxResults) {
        String queryStr = "SELECT u FROM AroVLisUpdKoRisolti u WHERE u.idUpdUnitaDoc = :idupd ORDER BY u.tsIniSes";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", idupd);

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVLisUpdKoRisolti> listaUpdKoRisolti = query.getResultList();

        AroVLisUpdKoRisoltiTableBean listaUpdKoRisoltiTableBean = new AroVLisUpdKoRisoltiTableBean();
        try {
            if (listaUpdKoRisolti != null && !listaUpdKoRisolti.isEmpty()) {
                listaUpdKoRisoltiTableBean = (AroVLisUpdKoRisoltiTableBean) Transform
                        .entities2TableBean(listaUpdKoRisolti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdKoRisoltiTableBean;
    }

    public AroWarnUpdUnitaDocTableBean getAroWarnUpdUnitaDocTableBean(BigDecimal idupd, int maxResults) {
        String queryStr = "SELECT u FROM AroWarnUpdUnitaDoc u WHERE u.aroUpdUnitaDoc.idUpdUnitaDoc = :idupd ORDER BY u.pgWarn";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupd", longFromBigDecimal(idupd));

        query.setMaxResults(maxResults);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroWarnUpdUnitaDoc> listaUpdWarnUnitaDoc = query.getResultList();

        AroWarnUpdUnitaDocTableBean listaUpdWarnUnitaDocTableBean = new AroWarnUpdUnitaDocTableBean();
        try {
            if (listaUpdWarnUnitaDoc != null && !listaUpdWarnUnitaDoc.isEmpty()) {
                for (AroWarnUpdUnitaDoc updWarnUD : listaUpdWarnUnitaDoc) {
                    AroWarnUpdUnitaDocRowBean listaUpdWarnUnitaDocRowBean = (AroWarnUpdUnitaDocRowBean) Transform
                            .entity2RowBean(updWarnUD);
                    listaUpdWarnUnitaDocRowBean.setString("cd_err", updWarnUD.getDecErrSacer().getCdErr());
                    listaUpdWarnUnitaDocTableBean.add(listaUpdWarnUnitaDocRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaUpdWarnUnitaDocTableBean;
    }

    public String getVersioniXsd(BigDecimal idAttribDatiSpec, BigDecimal id, TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "dec.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "dec.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "dec.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format("SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd "
                + " JOIN decXsd.decXsdDatiSpec dec " + " JOIN decXsd.decAttribDatiSpec decAttrib "
                + " WHERE decAttrib.idAttribDatiSpec = :idattribdatispec " + " AND %s = :id", tmpTipoEntita);

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idattribdatispec", longFromBigDecimal(idAttribDatiSpec));
        query.setParameter("id", longFromBigDecimal(id));

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        return Utils.composeVersioniString(listaVersioni);
    }

    public String getVersioniXsdSisMigr(BigDecimal idAttribDatiSpec, String tipoEntitaSacer, String nmSistemaMigraz) {

        String queryStr = "SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd JOIN decXsd.decXsdDatiSpec dec "
                + " JOIN decXsd.decAttribDatiSpec decAttrib " + " WHERE decAttrib.idAttribDatiSpec = :idattribdatispec"
                + " AND dec.nmSistemaMigraz = :nmsistemamigrazin" + " AND dec.tiEntitaSacer = :tipoentitasacerin";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idattribdatispec", longFromBigDecimal(idAttribDatiSpec));
        query.setParameter("nmsistemamigrazin", nmSistemaMigraz);
        query.setParameter("tipoentitasacerin", tipoEntitaSacer);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        return Utils.composeVersioniString(listaVersioni);
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBean(BigDecimal id, TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;

        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format(
                "SELECT DISTINCT u.idAttribDatiSpec,v.niOrdAttrib FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                        + "WHERE %s = :id ORDER BY v.niOrdAttrib",
                tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("id", longFromBigDecimal(id));
        final List<Object[]> rawResults = query.getResultList();
        List<DecAttribDatiSpec> listaDatiSpec = rawResults.stream().map(rec -> {
            Long idDec = Long.class.cast(rec[0]);
            return findById(DecAttribDatiSpec.class, idDec);
        }).collect(Collectors.toList());

        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecSisMigrTableBean(String tipoSistemaMigrazione,
            BigDecimal idStrut) {

        String queryStr = "SELECT u FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                + "WHERE u.nmSistemaMigraz = :nmsistemamigrazin " + "AND u.orgStrut.idStrut = :idstrutin "
                + "AND (u.tiEntitaSacer = :unidocin " + "OR u.tiEntitaSacer = :docin) " + "ORDER BY v.niOrdAttrib";

        Query query = this.getEntityManager().createQuery(queryStr);

        query.setParameter("nmsistemamigrazin", tipoSistemaMigrazione);
        query.setParameter("idstrutin", longFromBigDecimal(idStrut));
        query.setParameter("unidocin", TipoEntitaSacer.UNI_DOC.name());
        query.setParameter("docin", TipoEntitaSacer.DOC.name());

        List<DecAttribDatiSpec> listaDatiSpec = query.getResultList();

        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    // Ricavo i componenti di tipo file di un determinato documento
    public AroVLisCompDocTableBean getAroVLisCompDocFileTableBean(BigDecimal iddoc) {
        String queryStr = "SELECT u FROM AroVLisCompDoc u WHERE u.idDoc = :iddoc and UPPER(u.tiSupportoComp) LIKE 'FILE' ORDER BY u.niOrdCompDoc";

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("iddoc", iddoc);

        List<AroVLisCompDoc> listaCompDoc = query.getResultList();

        AroVLisCompDocTableBean listaCompDocTableBean = new AroVLisCompDocTableBean();
        try {
            if (listaCompDoc != null && !listaCompDoc.isEmpty()) {
                listaCompDocTableBean = (AroVLisCompDocTableBean) Transform.entities2TableBean(listaCompDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaCompDocTableBean;
    }

    // Ricavo i componenti di tipo file di un determinato documento
    public AroVLisDatiSpecTableBean getAroVLisDatiSpecTableBean(BigDecimal identificativo,
            TipoEntitaSacer tipoEntitaSacer, String tipoDatiSpec, int maxResults) {
        String tmpTipoEntita = null;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.idUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.idDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.idCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String
                .format("SELECT u FROM AroVLisDatiSpec u " + "WHERE %s = :id " + "AND u.tiUsoXsd = :tipoDatiSpecIn "
                        + "AND u.tiEntitaSacer = :tipoEntitaSacerIn " + "ORDER BY u.niOrdAttrib", tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);

        query.setParameter("id", identificativo);
        query.setParameter("tipoDatiSpecIn", tipoDatiSpec);
        query.setParameter("tipoEntitaSacerIn", tipoEntitaSacer.name());

        query.setMaxResults(maxResults);

        return lazyListHelper.getTableBean(query, this::getAroVLisDatiSpecTableBeanFromResultList);
    }

    private AroVLisDatiSpecTableBean getAroVLisDatiSpecTableBeanFromResultList(List<?> listaDatiSpec) {
        AroVLisDatiSpecTableBean listaTableBean = new AroVLisDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaTableBean = (AroVLisDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaTableBean;
    }

    public String getUrnCalc(long idud) {
        AroUnitaDoc aud = getEntityManager().find(AroUnitaDoc.class, idud);
        return "urn_" + aud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente() + "_"
                + aud.getOrgStrut().getOrgEnte().getNmEnte() + "_" + aud.getOrgStrut().getNmStrut() + "_"
                + aud.getCdRegistroKeyUnitaDoc() + "-" + aud.getAaKeyUnitaDoc().toString() + "-"
                + aud.getCdKeyUnitaDoc();
    }

    public List<String> getStatiDoc(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT DISTINCT (u.tiStatoDoc) FROM AroDoc u WHERE u.aroUnitaDoc.idUnitaDoc = :idunitadoc";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idunitadoc", longFromBigDecimal(idUnitaDoc));

        return query.getResultList();
    }

    private void setStatiDocumentari(BaseRow riga) {
        BigDecimal idUnitaDoc = riga.getBigDecimal("id_unita_doc");
        riga.setObject("fl_doc_in_volume_aperto", "0");
        riga.setObject("fl_doc_in_volume_chiuso", "0");
        riga.setObject("fl_doc_in_volume_in_errore", "0");
        riga.setObject("fl_doc_in_volume_da_chiudere", "0");
        StringBuilder stati = new StringBuilder();
        for (String stato : this.getStatiDoc(idUnitaDoc)) {
            if (StringUtils.isNotBlank(stato)) {
                stati.append(stato).append(", ");
                if (stato.equals(DocStatusEnum.IN_VOLUME_APERTO.name())) {
                    riga.setObject("fl_doc_in_volume_aperto", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_CHIUSO.name())) {
                    riga.setObject("fl_doc_in_volume_chiuso", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_IN_ERRORE.name())) {
                    riga.setObject("fl_doc_in_volume_in_errore", "1");
                } else if (stato.equals(DocStatusEnum.IN_VOLUME_DA_CHIUDERE.name())) {
                    riga.setObject("fl_doc_in_volume_da_chiudere", "1");
                }
            }
        }
        String stato = StringUtils.removeEnd(stati.toString(), ", ");
        riga.setObject("ds_stato_unita_doc", stato);
    }

    public DecTipoDocRowBean getDecTipoDocRowBean(BigDecimal idTipoDoc) {
        DecTipoDoc tipoDoc = getEntityManager().find(DecTipoDoc.class, idTipoDoc.longValue());
        DecTipoDocRowBean row = new DecTipoDocRowBean();
        try {
            if (tipoDoc != null) {
                row = (DecTipoDocRowBean) Transform.entity2RowBean(tipoDoc);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del tipo documento: " + e.getMessage(), e);
        }

        return row;
    }

    public DecTipoUnitaDocRowBean getDecTipoUnitaDocRowBean(BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc.longValue());
        DecTipoUnitaDocRowBean row = new DecTipoUnitaDocRowBean();

        try {
            if (tipoUnitaDoc != null) {
                row = (DecTipoUnitaDocRowBean) Transform.entity2RowBean(tipoUnitaDoc);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del tipo unità documentaria: " + e.getMessage(), e);
        }
        return row;
    }

    public String getTipoSaveFile(BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc.longValue());
        return tipoUnitaDoc.getTiSaveFile();
    }

    public boolean isDocumentoAggiunto(BigDecimal idDoc) {
        AroDoc doc = getEntityManager().find(AroDoc.class, idDoc.longValue());
        return doc.getTiCreazione().equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
    }

    public AroVerIndiceAipUdTableBean getAroVerIndiceAipUdTableBean(BigDecimal idUnitaDoc, int maxResults) {
        String queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "ORDER BY u.pgVerIndiceAip DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        query.setMaxResults(maxResults);
        List<AroVerIndiceAipUd> listaIndici = query.getResultList();
        AroVerIndiceAipUdTableBean listaIndiciTableBean = new AroVerIndiceAipUdTableBean();
        try {
            if (listaIndici != null && !listaIndici.isEmpty()) {
                listaIndiciTableBean = (AroVerIndiceAipUdTableBean) Transform.entities2TableBean(listaIndici);
                for (AroVerIndiceAipUdRowBean indiceRowBean : listaIndiciTableBean) {
                    // MEV#25918
                    // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X)
                    // Scompatto il campo cdVerIndiceAip
                    String[] numbers = indiceRowBean.getCdVerIndiceAip().split("[.]");
                    int majorNumber = Integer.parseInt(numbers[0]);
                    String tiFormatoIndiceAip = (majorNumber > 0) ? "UNI SInCRO 2.0 (UNI 11386:2020)"
                            : "UNI SInCRO 1.0 (UNI 11386:2010)";
                    indiceRowBean.setString("ti_formato_indice_aip", tiFormatoIndiceAip);
                    // end MEV#25918
                    if (indiceRowBean.getBigDecimal("id_elenco_vers") != null) {
                        ElvElencoVer elenco = findById(ElvElencoVer.class,
                                indiceRowBean.getBigDecimal("id_elenco_vers"));
                        indiceRowBean.setString("ti_stato_elenco_vers", elenco.getTiStatoElenco());
                    }
                    // EVO#16486
                    if (indiceRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                        AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                                indiceRowBean.getBigDecimal("id_ver_indice_aip"));
                        if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                            // Recupero lo urn ORIGINALE
                            AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(
                                    indice.getAroUrnVerIndiceAipUds(),
                                    object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                            }
                            // Recupero lo urn NORMALIZZATO
                            urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                    object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.NORMALIZZATO));
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                            }
                            // Recupero lo urn INIZIALE
                            urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                    object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.INIZIALE));
                            if (urnVerIndiceAipUd != null) {
                                indiceRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                            }
                        } else {
                            indiceRowBean.setString("urn", indice.getDsUrn());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaIndiciTableBean;
    }

    // MEV#30395
    public AroVerIndiceAipUdRowBean getAroVerIndiceAipUdRowBean(BigDecimal idVerIndiceAip, int maxResults) {
        String queryStr = "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.idVerIndiceAip = :idVerIndiceAip ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter(ID_VER_INDICE_AIP_PARAMETER, longFromBigDecimal(idVerIndiceAip));
        query.setMaxResults(maxResults);
        List<AroVerIndiceAipUd> listaVerIndici = query.getResultList();
        AroVerIndiceAipUdRowBean verIndiceRowBean = new AroVerIndiceAipUdRowBean();
        try {
            if (listaVerIndici != null && !listaVerIndici.isEmpty()) {
                AroVerIndiceAipUd indice = listaVerIndici.get(0);
                verIndiceRowBean = (AroVerIndiceAipUdRowBean) Transform.entity2RowBean(indice);
                if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                    // Recupero lo urn ORIGINALE
                    AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                            object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                    if (urnVerIndiceAipUd != null) {
                        verIndiceRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                    }
                    // Recupero lo urn NORMALIZZATO
                    urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                            object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.NORMALIZZATO));
                    if (urnVerIndiceAipUd != null) {
                        verIndiceRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                    }
                    // Recupero lo urn INIZIALE
                    urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                            object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.INIZIALE));
                    if (urnVerIndiceAipUd != null) {
                        verIndiceRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                    }
                } else {
                    verIndiceRowBean.setString("urn", indice.getDsUrn());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return verIndiceRowBean;
    }
    // end MEV#30395

    public AroFileVerIndiceAipUdRowBean getAroFileVerIndiceAipUdRowBean(BigDecimal idVerIndiceAip, int maxResults) {
        String queryStr = "SELECT u FROM AroFileVerIndiceAipUd u "
                + "WHERE u.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter(ID_VER_INDICE_AIP_PARAMETER, longFromBigDecimal(idVerIndiceAip));
        query.setMaxResults(maxResults);
        List<AroFileVerIndiceAipUd> listaFileIndici = query.getResultList();
        AroFileVerIndiceAipUdRowBean fileIndiceRowBean = new AroFileVerIndiceAipUdRowBean();
        try {
            if (listaFileIndici != null && !listaFileIndici.isEmpty()) {
                fileIndiceRowBean = (AroFileVerIndiceAipUdRowBean) Transform.entity2RowBean(listaFileIndici.get(0));
                if (fileIndiceRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                    AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                            fileIndiceRowBean.getBigDecimal("id_ver_indice_aip"));
                    if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn NORMALIZZATO
                        urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.NORMALIZZATO));
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn INIZIALE
                        urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.INIZIALE));
                        if (urnVerIndiceAipUd != null) {
                            fileIndiceRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                        }
                    } else {
                        fileIndiceRowBean.setString("urn", indice.getDsUrn());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fileIndiceRowBean;
    }

    public AroVLisVolCorTableBean getAroVLisVolCorViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisVolCor u " + "WHERE u.id.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtCreazione DESC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisVolCor> listaVolumi = query.getResultList();
        AroVLisVolCorTableBean volumiTableBean = new AroVLisVolCorTableBean();
        try {
            if (listaVolumi != null && !listaVolumi.isEmpty()) {
                volumiTableBean = (AroVLisVolCorTableBean) Transform.entities2TableBean(listaVolumi);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return volumiTableBean;
    }

    public AroVLisElvVerTableBean getAroVLisElvVerViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisElvVer u " + "WHERE u.id.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtCreazioneElenco DESC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisElvVer> listaElenchi = query.getResultList();
        AroVLisElvVerTableBean elenchiTableBean = new AroVLisElvVerTableBean();
        try {
            if (listaElenchi != null && !listaElenchi.isEmpty()) {
                elenchiTableBean = (AroVLisElvVerTableBean) Transform.entities2TableBean(listaElenchi);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return elenchiTableBean;
    }

    public Long getLastIdUnitaDocAnnullata(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT u.idUnitaDoc FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc " + "AND u.dtAnnul != :dtAnnul " + "ORDER BY u.dtAnnul DESC ";
        List<Long> listaUdAnnullate;
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtAnnul", cal.getTime());
        listaUdAnnullate = query.getResultList();
        // Posso avere più di una ud annullata, dunque prendo l'ultima
        if (listaUdAnnullate != null && !listaUdAnnullate.isEmpty()) {
            return listaUdAnnullate.get(0);
        } else {
            return null;
        }
    }

    public Long getIdUnitaDocVersataNoAnnul(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT u.idUnitaDoc FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc " + "AND u.dtAnnul = :dtAnnul ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtAnnul", cal.getTime());
        List<Long> listaUdVersate = query.getResultList();
        if (listaUdVersate != null && !listaUdVersate.isEmpty()) {
            return listaUdVersate.get(0);
        } else {
            return null;
        }
    }

    /**
     * Controlla che l’unità documentaria identificata dalla struttura versante, registro, anno e numero esista
     * <p>
     * (NOTA: una unità doc può essere annullata più di una volta, per questo il conteggio può essere superiore a 1)
     *
     * @param idStrut
     *            id struttura
     * @param cdRegistroKeyUnitaDoc
     *            chiave registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * @param cdKeyUnitaDoc
     *            numero unita doc
     *
     * @return true/false
     */
    public boolean existAroUnitaDoc(BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc) {
        String queryStr = "SELECT COUNT(u) FROM AroUnitaDoc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND u.cdKeyUnitaDoc = :cdKeyUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        Long numUd = (Long) query.getSingleResult();
        return numUd > 0;
    }

    public BaseTable getSerieAppartenenzaList(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT verSerie.idVerSerie, serie.cdCompositoSerie, serie.aaSerie, serie.dsSerie, verSerie.cdVerSerie, statoVerSerie.tiStatoVerSerie, statoSerie.tiStatoSerie "
                + "FROM AroUdAppartVerSerie udAppartVerSerie "
                + "JOIN udAppartVerSerie.serContenutoVerSerie contenutoVerSerie "
                + "JOIN contenutoVerSerie.serVerSerie verSerie " + "JOIN verSerie.serSerie serie "
                + "JOIN verSerie.serStatoVerSeries statoVerSerie " + "JOIN serie.serStatoSeries statoSerie "
                + "WHERE udAppartVerSerie.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
                + "AND verSerie.pgVerSerie = (SELECT MAX(ver.pgVerSerie) FROM SerVerSerie ver WHERE ver.idVerSerie = verSerie.idVerSerie) "
                + "AND verSerie.idStatoVerSerieCor = statoVerSerie.idStatoVerSerie "
                + "AND serie.idStatoSerieCor = statoSerie.idStatoSerie " + "ORDER BY serie.aaSerie ASC ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        List<Object[]> listaObj = query.getResultList();
        BaseTable listaSerieAppartenenza = new BaseTable();

        try {
            if (listaObj != null && !listaObj.isEmpty()) {
                for (Object[] obj : listaObj) {
                    SerieAppartenenzaRowBean bean = new SerieAppartenenzaRowBean();
                    bean.setIdVerSerie(new BigDecimal(obj[0].toString()));
                    bean.setCdCompositoSerie((String) obj[1]);
                    bean.setAaSerie((BigDecimal) obj[2]);
                    bean.setDsSerie((String) obj[3]);
                    bean.setCdVerSerie((String) obj[4]);
                    bean.setTiStatoVerSerie((String) obj[5]);
                    bean.setTiStatoSerie((String) obj[6]);
                    listaSerieAppartenenza.add(bean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaSerieAppartenenza;
    }

    public AroVLisFascTableBean getFascicoliAppartenenzaViewBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.AroVLisFasc(a.id.idFascicolo, a.aaFascicolo, a.cdKeyFascicolo, a.nmTipoFascicolo, "
                + "a.dtApeFascicolo, a.dtChiuFascicolo, a.cdCompositoVoceTitol, a.niUnitaDoc, a.tsIniSes, "
                + "a.tiEsito, a.tiStatoFascElencoVers, a.tiStatoConservazione) " + "FROM AroVLisFasc a "
                + "WHERE a.id.idUnitaDoc = :idUnitaDoc " + "ORDER BY a.aaFascicolo, a.cdKeyFascicolo ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisFasc> listaFascAppartenenza = query.getResultList();
        AroVLisFascTableBean fascicoliTableBean = new AroVLisFascTableBean();
        try {
            if (listaFascAppartenenza != null && !listaFascAppartenenza.isEmpty()) {
                fascicoliTableBean = (AroVLisFascTableBean) Transform.entities2TableBean(listaFascAppartenenza);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return fascicoliTableBean;
    }

    public AroUpdUnitaDocTableBean getAggiornamentiMetadatiTableBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroUpdUnitaDoc u " + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.tsIniSes ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        List<AroUpdUnitaDoc> listaUpd = query.getResultList();
        AroUpdUnitaDocTableBean updTableBean = new AroUpdUnitaDocTableBean();
        try {
            if (listaUpd != null && !listaUpd.isEmpty()) {
                updTableBean = (AroUpdUnitaDocTableBean) Transform.entities2TableBean(listaUpd);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updTableBean;
    }

    // MEV#24597
    public AroVLisNotaUnitaDocTableBean getListaNoteUnitaDocumentarieTableBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc ORDER BY u.niOrd, u.pgNotaUnitaDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisNotaUnitaDoc> listaNote = query.getResultList();
        AroVLisNotaUnitaDocTableBean noteTableBean = new AroVLisNotaUnitaDocTableBean();
        try {
            if (listaNote != null && !listaNote.isEmpty()) {
                noteTableBean = (AroVLisNotaUnitaDocTableBean) Transform.entities2TableBean(listaNote);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteTableBean;
    }

    public List<AroVLisNotaUnitaDoc> getAroVLisNotaUnitaDoc(BigDecimal idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc ORDER BY u.niOrd, u.pgNotaUnitaDoc");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        return query.getResultList();
    }

    public BigDecimal getMaxPgNota(BigDecimal idUnitaDoc, BigDecimal idTipoNotaUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(u.pgNotaUnitaDoc) FROM AroVLisNotaUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc AND u.idTipoNotaUnitaDoc = :idTipoNotaUnitaDoc ");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idTipoNotaUnitaDoc", idTipoNotaUnitaDoc);
        BigDecimal pg = (BigDecimal) query.getSingleResult();
        return pg != null ? pg : BigDecimal.ZERO;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocList() {

        List<DecTipoNotaUnitaDoc> result = null;
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='1' order by tnud.cdTipoNotaUnitaDoc");
        result = query.getResultList();
        return result;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocListByIdUserIam(long idUtente) {
        UsrUser user = this.findById(UsrUser.class, idUtente);

        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = null;
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='1' order by tnud.cdTipoNotaUnitaDoc");
        decTipoNotaUnitaDocList = query.getResultList();

        if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
            if (TiEnteConvenz.PRODUTTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())
                    || TiEnteConvenz.GESTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())) {
                CollectionUtils.filter(decTipoNotaUnitaDocList,
                        object -> (object).getCdTipoNotaUnitaDoc().equals("NOTE_PRODUTTORE"));
            }
        }

        return decTipoNotaUnitaDocList;
    }

    public DecTipoNotaUnitaDocTableBean getDecTipoNotaUnitaDocNotInUnitaDocTableBean(long idUtente,
            BigDecimal idUnitaDoc) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = this.getDecTipoNotaUnitaDocListByIdUserIam(idUtente);
        List<DecTipoNotaUnitaDoc> listNotInUnitaDoc = this.getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam(idUtente,
                idUnitaDoc);
        try {
            if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : decTipoNotaUnitaDocList) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInUnitaDoc != null && !listNotInUnitaDoc.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : listNotInUnitaDoc) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInUnitaDoc(BigDecimal idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroUnitaDoc.idUnitaDoc = :idUnitaDoc)");
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        return query.getResultList();
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInUnitaDocByIdUserIam(long idUtente,
            BigDecimal idUnitaDoc) {
        UsrUser user = this.findById(UsrUser.class, idUtente);

        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroUnitaDoc.idUnitaDoc = :idUnitaDoc)");
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        List<DecTipoNotaUnitaDoc> listNotInUnitaDoc = query.getResultList();

        if (listNotInUnitaDoc != null && !listNotInUnitaDoc.isEmpty()) {
            if (TiEnteConvenz.PRODUTTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())
                    || TiEnteConvenz.GESTORE.equals(user.getSiOrgEnteSiam().getTiEnteConvenz())) {
                CollectionUtils.filter(listNotInUnitaDoc,
                        object -> (object).getCdTipoNotaUnitaDoc().equals("NOTE_PRODUTTORE"));
            }
        }

        return listNotInUnitaDoc;
    }

    public DecTipoNotaUnitaDocTableBean getDecTipoNotaUnitaDocNotInVerIndiceAipTableBean(BigDecimal idVerIndiceAip) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        table.addSortingRule("ni_ord", SortingRule.ASC);
        List<DecTipoNotaUnitaDoc> decTipoNotaUnitaDocList = this.getDecTipoNotaUnitaDocList();
        List<DecTipoNotaUnitaDoc> listNotInVerIndiceAip = this.getDecTipoNotaUnitaDocNotInVerIndiceAip(idVerIndiceAip);
        try {
            if (decTipoNotaUnitaDocList != null && !decTipoNotaUnitaDocList.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : decTipoNotaUnitaDocList) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            if (listNotInVerIndiceAip != null && !listNotInVerIndiceAip.isEmpty()) {
                for (DecTipoNotaUnitaDoc row : listNotInVerIndiceAip) {
                    DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(row);
                    if (rowBean.getFlObblig().equals("1")) {
                        rowBean.setDsTipoNotaUnitaDoc(rowBean.getDsTipoNotaUnitaDoc() + " (OBBLIGATORIO)");
                    }
                    table.add(rowBean);
                }
            }
            table.sort();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public List<DecTipoNotaUnitaDoc> getDecTipoNotaUnitaDocNotInVerIndiceAip(BigDecimal idVerIndiceAip) {
        Query query = getEntityManager().createQuery(
                "Select tnud from DecTipoNotaUnitaDoc tnud where tnud.flMolt='0' AND NOT EXISTS ( select n from AroNotaUnitaDoc n where n.decTipoNotaUnitaDoc.idTipoNotaUnitaDoc=tnud.idTipoNotaUnitaDoc and n.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip)");
        query.setParameter(ID_VER_INDICE_AIP_PARAMETER, longFromBigDecimal(idVerIndiceAip));
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveNota(long idUtente, BigDecimal idUnitaDoc, BigDecimal idTipoNotaUnitaDoc, BigDecimal pgNota,
            String dsNota, Date dtNota) throws ParerUserError {
        log.debug("Eseguo il salvataggio della nota");
        BigDecimal id = null;
        try {
            AroUnitaDoc ud = this.findById(AroUnitaDoc.class, idUnitaDoc);
            if (ud.getAroNotaUnitaDocs() == null) {
                ud.setAroNotaUnitaDocs(new ArrayList<>());
            }
            AroNotaUnitaDoc nota = new AroNotaUnitaDoc();
            nota.setDecTipoNotaUnitaDoc(this.findById(DecTipoNotaUnitaDoc.class, idTipoNotaUnitaDoc));
            nota.setIamUser(this.findById(IamUser.class, idUtente));
            nota.setPgNotaUnitaDoc(pgNota);
            nota.setDsNotaUnitaDoc(dsNota);
            nota.setDtNotaUnitaDoc(dtNota);

            ud.addAroNotaUnitaDoc(nota);
            this.insertEntity(nota, true);
            id = new BigDecimal(nota.getIdNotaUnitaDoc());
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
        return id;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNota(BigDecimal idNota, String dsNota, long idUtente, Date dtNota) throws ParerUserError {
        log.debug("Eseguo il salvataggio della nota");
        try {
            AroNotaUnitaDoc nota = this.findById(AroNotaUnitaDoc.class, idNota);
            nota.setDsNotaUnitaDoc(dsNota);
            nota.setIamUser(this.findById(IamUser.class, idUtente));
            nota.setDtNotaUnitaDoc(dtNota);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteNota(BigDecimal idNotaUnitaDoc) throws ParerUserError {
        log.debug("Eseguo l'eliminazione della nota");
        try {
            AroNotaUnitaDoc nota = this.findById(AroNotaUnitaDoc.class, idNotaUnitaDoc);
            this.removeEntity(nota, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione della nota ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    public AroVLisNotaUnitaDocTableBean getAroVLisNotaUnitaDocTableBean(BigDecimal idUnitaDoc) {
        AroVLisNotaUnitaDocTableBean table = new AroVLisNotaUnitaDocTableBean();
        List<AroVLisNotaUnitaDoc> list = this.getAroVLisNotaUnitaDoc(idUnitaDoc);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisNotaUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Errore durante il recupero della lista note delle unit\u00e0 documentarie "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    public DecTipoNotaUnitaDocTableBean getSingleDecTipoNotaUnitaDocTableBean(BigDecimal idTipoNotaUnitaDoc) {
        DecTipoNotaUnitaDocTableBean table = new DecTipoNotaUnitaDocTableBean();
        DecTipoNotaUnitaDoc nota = this.findById(DecTipoNotaUnitaDoc.class, idTipoNotaUnitaDoc);
        try {
            DecTipoNotaUnitaDocRowBean rowBean = (DecTipoNotaUnitaDocRowBean) Transform.entity2RowBean(nota);
            table.add(rowBean);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            log.error("Errore durante il recupero della lista tipi di nota delle unit\u00e0 documentarie "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    public AroVVisNotaUnitaDocRowBean getAroVVisNotaUnitaDocRowBean(BigDecimal idNotaUnitaDoc) {
        String queryStr = "SELECT u FROM AroVVisNotaUnitaDoc u WHERE u.idNotaUnitaDoc = :idNotaUnitaDoc";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idNotaUnitaDoc", idNotaUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<AroVVisNotaUnitaDoc> nota = query.getResultList();

        AroVVisNotaUnitaDocRowBean notaRowBean = new AroVVisNotaUnitaDocRowBean();
        try {
            if (nota != null && !nota.isEmpty()) {
                notaRowBean = (AroVVisNotaUnitaDocRowBean) Transform.entity2RowBean(nota.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return notaRowBean;
    }

    public AroVerIndiceAipUdRowBean retrieveVersioneIndiceAipUdById(long idVerIndiceAip) {
        AroVerIndiceAipUd verIndiceAipUd = getEntityManager().find(AroVerIndiceAipUd.class, idVerIndiceAip);

        AroVerIndiceAipUdRowBean verIndiceAipUdRowBean = new AroVerIndiceAipUdRowBean();
        try {
            if (verIndiceAipUd != null) {
                verIndiceAipUdRowBean = (AroVerIndiceAipUdRowBean) Transform.entity2RowBean(verIndiceAipUd);

                // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X)
                // Scompatto il campo cdVerIndiceAip
                String[] numbers = verIndiceAipUdRowBean.getCdVerIndiceAip().split("[.]");
                int majorNumber = Integer.parseInt(numbers[0]);
                String tiFormatoIndiceAip = (majorNumber > 0) ? "UNI SInCRO 2.0 (UNI 11386:2020)"
                        : "UNI SInCRO 1.0 (UNI 11386:2010)";
                verIndiceAipUdRowBean.setString("ti_formato_indice_aip", tiFormatoIndiceAip);
                // end MEV#25918
                if (verIndiceAipUdRowBean.getBigDecimal("id_elenco_vers") != null) {
                    ElvElencoVer elenco = findById(ElvElencoVer.class,
                            verIndiceAipUdRowBean.getBigDecimal("id_elenco_vers"));
                    verIndiceAipUdRowBean.setString("ti_stato_elenco_vers", elenco.getTiStatoElenco());
                }

                if (verIndiceAipUdRowBean.getBigDecimal("id_ver_indice_aip") != null) {
                    AroVerIndiceAipUd indice = findById(AroVerIndiceAipUd.class,
                            verIndiceAipUdRowBean.getBigDecimal("id_ver_indice_aip"));
                    if (indice.getAroUrnVerIndiceAipUds() != null && !indice.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn NORMALIZZATO
                        urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.NORMALIZZATO));
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn_normalizzato", urnVerIndiceAipUd.getDsUrn());
                        }
                        // Recupero lo urn INZIALE
                        urnVerIndiceAipUd = IterableUtils.find(indice.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.INIZIALE));
                        if (urnVerIndiceAipUd != null) {
                            verIndiceAipUdRowBean.setString("urn_iniziale", urnVerIndiceAipUd.getDsUrn());
                        }
                    } else {
                        verIndiceAipUdRowBean.setString("urn", indice.getDsUrn());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return verIndiceAipUdRowBean;
    }

    public boolean isUserAppartAllOk(long idUtente) {
        boolean isOk = false;

        UsrUser user = getEntityManager().find(UsrUser.class, idUtente);
        if (user == null) {
            throw new NoResultException("Impossibile trovare UsrUser con idUtente " + idUtente);
        }
        TiEnteConvenz tiEnteConvenz = user.getSiOrgEnteSiam().getTiEnteConvenz();
        if (TiEnteConvenz.AMMINISTRATORE.equals(tiEnteConvenz) || TiEnteConvenz.CONSERVATORE.equals(tiEnteConvenz)) {
            isOk = true;
        }

        return isOk;
    }
    // end MEV#24597

    public List<Object[]> progressivoEsistenteUnitaDocList(BigDecimal idStrut, BigDecimal idUnitaDoc,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, BigDecimal nuovoProgressivo) {
        String queryStr = "SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitaDoc.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc "
                + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc " + "AND unitaDoc.pgUnitaDoc = :nuovoProgressivo "
                + "AND unitaDoc.orgStrut.idStrut = :idStrut " + "AND unitaDoc.idUnitaDoc != :idUnitaDoc "
                + "AND unitaDoc.dtAnnul = :dtAnnul ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("idStrut", idStrut.longValue());
        query.setParameter("nuovoProgressivo", nuovoProgressivo);
        Calendar deadline = Calendar.getInstance();
        deadline.set(2444, 11, 31, 0, 0, 0);
        query.setParameter("dtAnnul", deadline.getTime());
        List<AroUnitaDoc> unitaDocList = query.getResultList();
        List<Object[]> regAnnoNumeroObjList = new ArrayList<>();
        if (!unitaDocList.isEmpty()) {
            for (AroUnitaDoc unitaDoc : unitaDocList) {
                regAnnoNumeroObjList.add(new Object[] { unitaDoc.getCdRegistroKeyUnitaDoc(),
                        unitaDoc.getAaKeyUnitaDoc(), unitaDoc.getCdKeyUnitaDoc() });
            }
        }
        return regAnnoNumeroObjList;
    }

    public void salvaAssegnaProgressivo(BigDecimal idUnitaDoc, BigDecimal pgUnitaDoc) {
        String queryStr = "UPDATE AroUnitaDoc unitaDoc SET unitaDoc.pgUnitaDoc = :pgUnitaDoc "
                + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        query.setParameter("pgUnitaDoc", pgUnitaDoc);
        query.executeUpdate();
    }

    public BigDecimal getPgUnitaDoc(BigDecimal idUnitaDoc) {
        return (getEntityManager().find(AroUnitaDoc.class, idUnitaDoc.longValue())).getPgUnitaDoc();
    }

    public boolean existAroUnitaDocsFromRegistro(BigDecimal idRegistroUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc WHERE aroUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc AND aroUnitaDoc.decRegistroUnitaDoc.orgStrut.idStrut = :idStrut ");
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 0L : false;
    }

    public boolean existAroUnitaDocsFromTipoUnita(BigDecimal idTipoUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc WHERE aroUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc AND aroUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrut");
        query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 0L : false;
    }

    public boolean existsRelationsWithUnitaDoc(long idTipoDato, Constants.TipoDato tipoDato) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(unitaDoc) FROM AroUnitaDoc unitaDoc ");
        switch (tipoDato) {
        case REGISTRO:
            queryStr.append("WHERE unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append("WHERE unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr = new StringBuilder("SELECT COUNT(unitaDoc) FROM AroDoc doc " + "JOIN doc.aroUnitaDoc unitaDoc "
                    + "WHERE doc.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        default:
            break;
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoDato", idTipoDato);
        return (Long) query.getSingleResult() > 0;
    }

    /*
     * Controlla se esieste un numeroNormalizzato nell'ambito della stessa struttura/anno/numero registro.
     */
    public boolean existsUdWithSameNormalizedNumber(long idRegistroUnitaDoc, BigDecimal annoRegistro,
            String numeroNormalizzato, String numeroNonNormalizzato) {
        Query query = getEntityManager().createQuery("SELECT  COUNT(aroUnitaDoc) FROM AroUnitaDoc aroUnitaDoc " + // "WHERE
        // aroUnitaDoc.orgStrut=:orgStrut
        // " +
                "WHERE   aroUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc=:idRegistroUnitaDoc "
                + "AND     aroUnitaDoc.aaKeyUnitaDoc=:annoRegistro " + // "AND
                // aroUnitaDoc.cdRegistroKeyUnitaDoc=:codiceRegistro
                // " +
                "AND     aroUnitaDoc.cdKeyUnitaDocNormaliz=:numeroNormalizzato "
                + "AND     aroUnitaDoc.cdKeyUnitaDoc!=:numeroNonNormalizzato ");
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("annoRegistro", annoRegistro);
        query.setParameter("numeroNormalizzato", numeroNormalizzato);
        query.setParameter("numeroNonNormalizzato", numeroNonNormalizzato);
        Long result = (Long) query.getSingleResult();
        return (result != null) ? result > 1L : false;
    }

    /*
     * Data una UD torna la lista dei suoi AroDoc ordinati per: - Tipo documento principale e poi tutti gli altri -
     * Tutti gli altri ordinati per Data Creazione e nella stessa data per tipo documento e nello stesso tipo per
     * progressivo
     */
    public List<AroDoc> getAndSetAroDocOrderedByTypeAndDateProg(AroUnitaDoc aroUnitaDoc) {
        List<AroDoc> listaDoc = aroUnitaDoc.getAroDocs();
        ArrayList<AroDoc> alDef = null;
        if (listaDoc != null) {

            AroDoc aroDocPrinc = null;
            ArrayList<AroDoc> alNew = new ArrayList<>();
            for (AroDoc aroDoc : listaDoc) {
                if (aroDoc.getTiDoc().equals("PRINCIPALE")) {
                    aroDocPrinc = aroDoc; // memorizza per dopo il doc PRINCIPALE
                } else {
                    alNew.add(aroDoc);
                }
            }
            // Ordina gli elementi tranne il PRINCIPALE...
            Collections.sort(alNew, (doc1, doc2) -> {
                int comparazionePerData = doc1.getDtCreazione().compareTo(doc2.getDtCreazione());
                if (comparazionePerData == 0) {
                    int comparazionePerTipo = doc1.getTiDoc().compareTo(doc2.getTiDoc());
                    if (comparazionePerTipo == 0) {
                        return doc1.getPgDoc().compareTo(doc2.getPgDoc());
                    } else {
                        return comparazionePerTipo;
                    }
                } else {
                    return comparazionePerData;
                }
            });
            // Mette il PRINCIPALE come primo...
            alDef = new ArrayList<>();
            if (aroDocPrinc != null) {
                alDef.add(aroDocPrinc);
            }
            for (AroDoc aroDocZ : alNew) {
                alDef.add(aroDocZ);
            }
            // E poi tutti gli altri già ordinati di seguito
            BigDecimal prog = BigDecimal.ONE;
            for (AroDoc aroDocx : alDef) {
                // assegno solo se non presente
                if (aroDocx.getNiOrdDoc() == null) {
                    aroDocx.setNiOrdDoc(prog);

                    getEntityManager().merge(aroDocx);
                }
                // incremento
                prog = prog.add(BigDecimal.ONE);
            }

        }
        return alDef;
    }

    /*
     * restituisce AroCompUrnCalc del tipo passato per il componente passato
     */
    public AroCompUrnCalc findAroCompUrnCalcByType(AroCompDoc aroCompDoc, TiUrn tiUrn) {
        Query query = getEntityManager()
                .createQuery("SELECT  a FROM AroCompUrnCalc a WHERE   a.aroCompDoc = :aroCompDoc AND a.tiUrn = :tiUrn");
        query.setParameter("aroCompDoc", aroCompDoc);
        query.setParameter("tiUrn", tiUrn);
        List<AroCompUrnCalc> l = query.getResultList();
        return (l != null && !l.isEmpty()) ? l.get(0) : null;
    }

    private String calcAndcheckCdKeyNormalized(AroUnitaDoc aroUnitaDoc, String cdKeyUnitaDocNormaliz) {
        if (this.existsUdWithSameNormalizedNumber(aroUnitaDoc.getIdDecRegistroUnitaDoc(),
                aroUnitaDoc.getAaKeyUnitaDoc(), cdKeyUnitaDocNormaliz, aroUnitaDoc.getCdKeyUnitaDoc())) {
            // retry with another one
            // aggiungere _ infondo e richiamare ricorsivamente lo stesso metodo fino a che
            // la condizione di uscita
            // restituisce l'urn part normalizzato corretto
            cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz.concat("_");
            return this.calcAndcheckCdKeyNormalized(aroUnitaDoc, cdKeyUnitaDocNormaliz);
        }
        // return
        return cdKeyUnitaDocNormaliz;
    }

    public AroCompUrnCalc verifyAndCreateCompUrnCalc(AroCompDoc aroCompDoc, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm, String nomeSistema, TiUrn tiUrn) {
        AroDoc aroDoc = aroCompDoc.getAroStrutDoc().getAroDoc();
        AroUnitaDoc aroUnitaDoc = aroDoc.getAroUnitaDoc();
        AroCompDoc aroCompDocPadre = aroCompDoc.getAroCompDoc();
        boolean needCheckSottoComp = true;
        // Calcola URN originale
        AroCompUrnCalc aroCompUrnCalc = this.findAroCompUrnCalcByType(aroCompDoc, tiUrn);
        if (aroCompUrnCalc == null) {
            aroCompUrnCalc = new AroCompUrnCalc();
            aroCompUrnCalc.setAroCompDoc(aroCompDoc);
            aroCompUrnCalc.setTiUrn(tiUrn);
            String tmpUrn = null;
            switch (tiUrn) {
            case ORIGINALE:
                tmpUrn = String.format("urn:%s:%s:%s:%s-%s-%s:", nomeSistema, nomeEnte, nomeStruttura,
                        aroUnitaDoc.getCdRegistroKeyUnitaDoc(), aroUnitaDoc.getAaKeyUnitaDoc().longValueExact(),
                        aroUnitaDoc.getCdKeyUnitaDoc());
                break;
            case NORMALIZZATO:
                tmpUrn = String.format("urn:%s:%s:%s:%s-%s-%s:", nomeSistema, nomeEnteNorm, nomeStrutturaNorm,
                        Utils.getNormalizedUDCode(aroUnitaDoc.getCdRegistroKeyUnitaDoc()),
                        aroUnitaDoc.getAaKeyUnitaDoc().longValueExact(),
                        StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())
                                ? Utils.getNormalizedUDCode(aroUnitaDoc.getCdKeyUnitaDoc())
                                : aroUnitaDoc.getCdKeyUnitaDocNormaliz());
                break;
            default:
                tmpUrn = aroCompDoc.getDsUrnCompCalc();
                needCheckSottoComp = false;
                break;
            }
            // more
            if (needCheckSottoComp) {
                if (aroCompDocPadre != null) {
                    // E' UN SOTTOCOMPONENTE
                    tmpUrn += String.format("%05d", aroDoc.getNiOrdDoc().longValueExact()) + ":"
                            + String.format("%05d", aroCompDocPadre.getNiOrdCompDoc().longValueExact()) + ":"
                            + String.format("%02d", aroCompDoc.getNiOrdCompDoc().longValueExact());
                } else {
                    // E' UN COMPONENTE
                    tmpUrn += "DOC" + String.format("%05d", aroDoc.getNiOrdDoc().longValueExact()) + ":"
                            + String.format("%05d", aroCompDoc.getNiOrdCompDoc().longValueExact());
                }
            }
            if (StringUtils.isNotBlank(tmpUrn)) {
                // finally set urn
                aroCompUrnCalc.setDsUrn(tmpUrn);
            }
        }

        return aroCompUrnCalc;
    }

    /**
     * Verifica e calcolo chiave UD Normalizzata e URN Componenti
     *
     * @param idAroUnitaDoc
     *            id unita doc
     * @param tiUrnToCalculate
     *            tipo urn da calcolare
     */
    public void normalizzaUDAndCalcUrnOrigNormalizComp(long idAroUnitaDoc, List<TiUrn> tiUrnToCalculate) {
        // lock UD
        AroUnitaDoc tmpAroUnitaDoc = me.findByIdWithLock(AroUnitaDoc.class, idAroUnitaDoc);

        OrgStrut tmpOrgStrut = tmpAroUnitaDoc.getOrgStrut();
        OrgEnte tmpOrgEnte = tmpOrgStrut.getOrgEnte();
        String nomeSistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // verifico cd_key
        // calcola e verifica la chiave normalizzata (se non trovato in precedenza)
        if (StringUtils.isBlank(tmpAroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
            String cdKeyNormalized = me.calcAndcheckCdKeyNormalized(tmpAroUnitaDoc,
                    Utils.getNormalizedUDCode(tmpAroUnitaDoc.getCdKeyUnitaDoc()));
            tmpAroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
        }
        // per ogni documento/componente se non esiste:
        // calcolo ni_ord_doc
        me.getAndSetAroDocOrderedByTypeAndDateProg(tmpAroUnitaDoc);
        // urn componente ORIGINALE/NORMALIZZATO
        // per ogni documento
        for (AroDoc tmpAroDoc : tmpAroUnitaDoc.getAroDocs()) {
            // update ud on doc
            tmpAroDoc.setAroUnitaDoc(tmpAroUnitaDoc);
            // per ogni componente
            for (Iterator<AroStrutDoc> it = tmpAroDoc.getAroStrutDocs().iterator(); it.hasNext();) {
                AroStrutDoc tmpAroStrutDoc = it.next();
                for (AroCompDoc tmpAroCompDoc : tmpAroStrutDoc.getAroCompDocs()) {
                    // per ogni TiUrn (tranne originale)
                    for (TiUrn tiUrn : tiUrnToCalculate) {
                        // calcolo URN
                        AroCompUrnCalc tmpAroCompUrnCalc = me.verifyAndCreateCompUrnCalc(tmpAroCompDoc,
                                tmpOrgStrut.getNmStrut(), tmpOrgStrut.getCdStrutNormaliz(), tmpOrgEnte.getNmEnte(),
                                tmpOrgEnte.getCdEnteNormaliz(), nomeSistema, tiUrn);
                        // new one
                        if (tmpAroCompDoc.getAroAroCompUrnCalcs() == null) {
                            tmpAroCompDoc.setAroAroCompUrnCalcs(new ArrayList<>());
                        }
                        // add element
                        tmpAroCompDoc.getAroAroCompUrnCalcs().add(tmpAroCompUrnCalc);
                    } // TiUrn
                }
            }
        }
    }

    /**
     * @param filtri
     *            filtri di ricerca
     * @param idTipoUnitaDocList
     *            tipi di unità documentarie
     * @param cdRegistroUnitaDocSet
     *            registri delle unità documentarie
     * @param idTipoDocList
     *            tipi di documenti
     * @param dateAcquisizioneValidate
     *            date di acquisizione
     * @param dateUnitaDocValidate
     *            date delle unità documentarie
     * @param dateAnnulValidate
     *            date di annullamento
     *
     * @return AroVRicUnitaDocTableBean table bean per la UI
     *
     * @throws EMFError
     *             errore generico7
     */
    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAnnullateTableBean(
            UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate filtri, List<BigDecimal> idTipoUnitaDocList,
            Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateAnnulValidate) throws EMFError {
        final Date dataCreazioneDa = getDateOrNull(dateAcquisizioneValidate, 0);
        final Date dataCreazioneA = getDateOrNull(dateAcquisizioneValidate, 1);
        final Date dataRegUnitaDocDa = getDateOrNull(dateUnitaDocValidate, 0);
        final Date dataRegUnitaDocA = getDateOrNull(dateUnitaDocValidate, 1);
        final Date dataAnnulDa = getDateOrNull(dateAnnulValidate, 0);
        final Date dataAnnulA = getDateOrNull(dateAnnulValidate, 1);
        return getAroVRicUnitaDocRicAnnullateTableBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocSet,
                idTipoDocList, new FiltriUnitaDocumentarieAnnullatePlain(filtri, dataCreazioneDa, dataCreazioneA,
                        dataRegUnitaDocDa, dataRegUnitaDocA, dataAnnulDa, dataAnnulA),
                true);
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAnnullateTableBeanNoLimit(
            UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate filtri, List<BigDecimal> idTipoUnitaDocList,
            Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList, Date[] dateAcquisizioneValidate,
            Date[] dateUnitaDocValidate, Date[] dateAnnulValidate) throws EMFError {
        final Date dataCreazioneDa = getDateOrNull(dateAcquisizioneValidate, 0);
        final Date dataCreazioneA = getDateOrNull(dateAcquisizioneValidate, 1);
        final Date dataRegUnitaDocDa = getDateOrNull(dateUnitaDocValidate, 0);
        final Date dataRegUnitaDocA = getDateOrNull(dateUnitaDocValidate, 1);
        final Date dataAnnulDa = getDateOrNull(dateAnnulValidate, 0);
        final Date dataAnnulA = getDateOrNull(dateAnnulValidate, 1);
        return getAroVRicUnitaDocRicAnnullateTableBeanPlainFilter(idTipoUnitaDocList, cdRegistroUnitaDocSet,
                idTipoDocList, new FiltriUnitaDocumentarieAnnullatePlain(filtri, dataCreazioneDa, dataCreazioneA,
                        dataRegUnitaDocDa, dataRegUnitaDocA, dataAnnulDa, dataAnnulA),
                false);
    }

    public AroVRicUnitaDocTableBean getAroVRicUnitaDocRicAnnullateTableBeanPlainFilter(
            List<BigDecimal> idTipoUnitaDocList, Set<String> cdRegistroUnitaDocSet, List<BigDecimal> idTipoDocList,
            FiltriUnitaDocumentarieAnnullatePlain filtriUnitaDocumentarieAnnullatePlain, boolean lazy) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u FROM AroUnitaDoc u LEFT JOIN u.aroDocs doc ");

        if (!StringUtils.isBlank(filtriUnitaDocumentarieAnnullatePlain.getTiAnnullamento())) {
            queryStr.append(
                    "INNER JOIN AroItemRichAnnulVers i ON i.aroUnitaDoc = u.idUnitaDoc JOIN AroRichAnnulVers r ON i.aroRichAnnulVers = r.idRichAnnulVers ");
        }

        // Inserimento nella query del filtro Tipo Unità Doc versione multiselect
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("(u.decTipoUnitaDoc.idTipoUnitaDoc IN (:listtipoud))");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro Registro
        if (!cdRegistroUnitaDocSet.isEmpty()) {
            queryStr.append(whereWord).append("u.decRegistroUnitaDoc.cdRegistroUnitaDoc IN (:setregistro) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro Tipo Documento
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("doc.decTipoDoc.idTipoDoc IN (:listtipodoc) ");
            whereWord = "AND ";
        }

        BigDecimal anno = filtriUnitaDocumentarieAnnullatePlain.getAaKeyUnitaDoc();
        String codice = filtriUnitaDocumentarieAnnullatePlain.getCdKeyUnitaDoc();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        BigDecimal annoRangeDa = filtriUnitaDocumentarieAnnullatePlain.getAaKeyUnitaDocDa();
        BigDecimal annoRangeA = filtriUnitaDocumentarieAnnullatePlain.getAaKeyUnitaDocA();
        String codiceRangeDa = filtriUnitaDocumentarieAnnullatePlain.getCdKeyUnitaDocDa();
        String codiceRangeA = filtriUnitaDocumentarieAnnullatePlain.getCdKeyUnitaDocA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        if ((filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneDa() != null)
                && (filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneA() != null)) {
            queryStr.append(whereWord).append("(u.dtCreazione between :datada AND :dataa) ");
            whereWord = " AND ";
        }

        if ((filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocDa() != null)
                && (filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocA() != null)) {
            queryStr.append(whereWord).append("(u.dtRegUnitaDoc between :dataregudda AND :datareguda) ");
            whereWord = " AND ";
        }

        if ((filtriUnitaDocumentarieAnnullatePlain.getDataAnnulDa() != null)
                && (filtriUnitaDocumentarieAnnullatePlain.getDataAnnulA() != null)) {
            queryStr.append(whereWord).append("(u.dtAnnul between :dataannulda AND :dataannula) ");
            whereWord = " AND ";
        }

        if (!StringUtils.isBlank(filtriUnitaDocumentarieAnnullatePlain.getTiAnnullamento())) {
            queryStr.append(whereWord).append("r.tiAnnullamento = :tiAnnullamento ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.tiAnnul = 'ANNULLAMENTO' ORDER BY u.dsKeyOrd ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        // non avendo passato alla query i parametri di ricerca, devo passarli ora

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("listtipoud", longListFrom(idTipoUnitaDocList));
        }

        if (!cdRegistroUnitaDocSet.isEmpty()) {
            query.setParameter("setregistro", cdRegistroUnitaDocSet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }

        if (filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneDa() != null
                && filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneA() != null) {
            query.setParameter("datada", filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneDa(),
                    TemporalType.TIMESTAMP);
            query.setParameter("dataa", filtriUnitaDocumentarieAnnullatePlain.getDataCreazioneA(),
                    TemporalType.TIMESTAMP);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("listtipodoc", longListFrom(idTipoDocList));
        }

        if (filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocDa() != null
                && filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocA() != null) {
            query.setParameter("dataregudda", filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocDa(),
                    TemporalType.DATE);
            query.setParameter("datareguda", filtriUnitaDocumentarieAnnullatePlain.getDataRegUnitaDocA(),
                    TemporalType.DATE);
        }

        if ((filtriUnitaDocumentarieAnnullatePlain.getDataAnnulDa() != null)
                && (filtriUnitaDocumentarieAnnullatePlain.getDataAnnulA() != null)) {
            query.setParameter("dataannulda", filtriUnitaDocumentarieAnnullatePlain.getDataAnnulDa());
            query.setParameter("dataannula", filtriUnitaDocumentarieAnnullatePlain.getDataAnnulA());
        }

        if (!StringUtils.isBlank(filtriUnitaDocumentarieAnnullatePlain.getTiAnnullamento())) {
            query.setParameter("tiAnnullamento", filtriUnitaDocumentarieAnnullatePlain.getTiAnnullamento());
        }
        if (lazy) {
            return lazyListHelper.getTableBean(query, this::aroUnitaDocsToTableBean);
        } else {
            return aroUnitaDocsToTableBean(query.getResultList());
        }
    }

    private AroVRicUnitaDocTableBean aroUnitaDocsToTableBean(List<AroUnitaDoc> listaUD) {
        AroVRicUnitaDocTableBean udTableBean = new AroVRicUnitaDocTableBean();

        try {
            if (listaUD != null && !listaUD.isEmpty()) {
                for (AroUnitaDoc ud : listaUD) {
                    getEntityManager().clear();
                    AroUnitaDocRowBean udRowBean = (AroUnitaDocRowBean) Transform.entity2RowBean(ud);
                    AroVRicUnitaDocRowBean rowBean = new AroVRicUnitaDocRowBean();
                    rowBean.copyFromBaseRow(udRowBean);
                    rowBean.setString("nm_tipo_unita_doc",
                            getNmTipoUnitaDoc(ud.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
                    // Ricavo il campo tipo doc principale
                    rowBean.setString("ti_annullamento", getTiAnnullamento(ud));
                    for (AroDoc doc : getAroDocs(ud)) {
                        if (doc.getTiDoc().equals("PRINCIPALE")) {
                            rowBean.setString("nm_tipo_doc_princ", getNmTipoDoc(doc.getIdDecTipoDoc()));
                            break;
                        }
                    }
                    // Ricavo il campo ds lista stati elenco vers
                    rowBean.setString("ds_lista_stati_elenco_vers", getDsListaStatiElencoVers(ud.getIdUnitaDoc()));

                    // Ricavo l'identificativo e il tipo della richiesta di annullamento con stato
                    // EVASA cui
                    // l'annullamento si riferisce
                    AroRichAnnulVers aroRichAnnulVers = getAroRichAnnulVers(ud.getIdUnitaDoc());
                    if (aroRichAnnulVers != null) {
                        rowBean.setBigDecimal("id_rich_annul_vers",
                                BigDecimal.valueOf(aroRichAnnulVers.getIdRichAnnulVers()));
                        rowBean.setString("ti_creazione_rich_annul_vers",
                                aroRichAnnulVers.getTiCreazioneRichAnnulVers());
                    }

                    udTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return udTableBean;
    }

    private List<AroDoc> getAroDocs(AroUnitaDoc ud) {
        TypedQuery<AroDoc> query = getEntityManager()
                .createQuery("SELECT a FROM AroDoc a WHERE a.aroUnitaDoc = :aroUnitaDoc", AroDoc.class);
        query.setParameter("aroUnitaDoc", ud);
        return query.getResultList();
    }

    private String getNmTipoUnitaDoc(Long idTipoUnitaDoc) {
        if (idTipoUnitaDoc != null) {
            DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc);
            if (tipoUnitaDoc != null) {
                return tipoUnitaDoc.getNmTipoUnitaDoc();
            }
        }
        return "";
    }

    public String getNmTipoDoc(Long idTipoDoc) {
        if (idTipoDoc != null) {
            TypedQuery<String> query = getEntityManager()
                    .createQuery("SELECT d.nmTipoDoc FROM DecTipoDoc d WHERE d.idTipoDoc=:idTipoDoc", String.class);
            query.setParameter("idTipoDoc", idTipoDoc);
            return query.getSingleResult();
        }
        return "";
    }

    public String getTiAnnullamento(AroUnitaDoc ud) {
        if (ud != null) {
            TypedQuery<String> query = getEntityManager().createQuery(
                    "SELECT a.tiAnnullamento FROM AroRichAnnulVers a JOIN AroItemRichAnnulVers i ON i.aroRichAnnulVers = a.idRichAnnulVers WHERE i.aroUnitaDoc = :aroUnitaDoc",
                    String.class);
            query.setParameter("aroUnitaDoc", ud);
            List<String> result = query.getResultList();
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        }
        return "";
    }

    private String getDsListaStatiElencoVers(long idUnitaDoc) {
        Query query = getEntityManager().createNativeQuery(
                "select " + "listagg (tmp.ti_stato_elenco_vers, '; ') within group (order by tmp.ti_stato_elenco_vers) "
                        + " from " + " (select ud_elenco.ti_stato_ud_elenco_vers ti_stato_elenco_vers "
                        + " from ARO_UNITA_DOC ud_elenco " + " where ud_elenco.id_unita_doc = ?1 " + " " + " UNION "
                        + " select distinct doc_elenco.ti_stato_doc_elenco_vers ti_stato_elenco_vers "
                        + " from ARO_DOC doc_elenco " + " where doc_elenco.id_unita_doc = ?1 "
                        + " and doc_elenco.ti_stato_doc_elenco_vers is not null " + ") tmp ");

        query.setParameter(1, idUnitaDoc);
        Object obj = query.getSingleResult();
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    private AroRichAnnulVers getAroRichAnnulVers(long idUnitaDoc) {
        String queryStr = "SELECT rich FROM AroItemRichAnnulVers item "
                + "JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati "
                + "WHERE item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) "
                + "AND stati.tiStatoRichAnnulVers = 'EVASA' ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroRichAnnulVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<AroNotaUnitaDoc> findAroNotaUnitaDocByIdUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        TypedQuery<AroNotaUnitaDoc> query = getEntityManager().createQuery(
                "SELECT n FROM AroNotaUnitaDoc n WHERE n.aroUnitaDoc = :aroUnitaDoc ", AroNotaUnitaDoc.class);
        query.setParameter("aroUnitaDoc", aroUnitaDoc);
        return query.getResultList();
    }

    public String findNmTipoUnitaDocById(Long idTipoUnitaDoc) {
        TypedQuery<String> query = getEntityManager().createQuery(
                "SELECT u.nmTipoUnitaDoc FROM DecTipoUnitaDoc u WHERE u.idTipoUnitaDoc=:idTipoUnitaDoc", String.class);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return query.getSingleResult();
    }

    static class FiltriUnitaDocumentarieSemplicePlain {

        private String cdVersioneXsdUd;
        private String cdVersioneXsdDoc;
        private BigDecimal anno;
        private String codice;
        private String cdKeyDocVers;
        private BigDecimal annoRangeDa;
        private BigDecimal annoRangeA;
        private String codiceRangeDa;
        private String codiceRangeA;
        private String presenza;
        private String esito;
        private String forzaAcc;
        private String forzaConserva;
        private String forzaColleg;
        // private String forzaHash;
        // private String forzaFmtNumero;
        // private String forzaFmtFile;
        private String cdVersioneWs;
        private String unitaDocAnnul;
        private String docAggiunti;
        private String flAggMeta;
        private String flHashVers;
        private String statoConserva;
        private String tiStatoUdElencoVers;
        private String tiDoc;
        private String oggettoMeta;
        private String autoreDocMeta;
        private String descrizioneDocMeta;
        private String tipoConservazione;
        private List<BigDecimal> subStruts;
        private String cdKeyUnitaDocContiene;
        private String profiloNorm;

        FiltriUnitaDocumentarieSemplicePlain() {

        }

        private FiltriUnitaDocumentarieSemplicePlain(FiltriUnitaDocumentarieSemplice filtri) throws EMFError {
            this.cdVersioneXsdUd = filtri.getCd_versione_xsd_ud().parse();
            this.cdVersioneXsdDoc = filtri.getCd_versione_xsd_doc().parse();
            this.anno = filtri.getAa_key_unita_doc().parse();
            this.codice = filtri.getCd_key_unita_doc().parse();
            this.cdKeyDocVers = filtri.getCd_key_doc_vers().parse();
            this.annoRangeDa = filtri.getAa_key_unita_doc_da().parse();
            this.annoRangeA = filtri.getAa_key_unita_doc_a().parse();
            this.codiceRangeDa = filtri.getCd_key_unita_doc_da().parse();
            this.codiceRangeA = filtri.getCd_key_unita_doc_a().parse();
            this.presenza = filtri.getFl_unita_doc_firmato().parse();
            this.esito = filtri.getTi_esito_verif_firme().parse();
            this.forzaAcc = filtri.getFl_forza_accettazione().parse();
            this.forzaConserva = filtri.getFl_forza_conservazione().parse();
            this.forzaColleg = filtri.getFl_forza_collegamento().parse();
            this.unitaDocAnnul = filtri.getFl_unita_doc_annul().parse();
            this.docAggiunti = filtri.getFl_doc_aggiunti().parse();
            this.flHashVers = filtri.getFl_hash_vers().parse();
            this.statoConserva = filtri.getTi_stato_conservazione().parse();
            this.tiStatoUdElencoVers = filtri.getTi_stato_ud_elenco_vers().parse();
            this.tiDoc = filtri.getTi_doc().parse();
            this.oggettoMeta = filtri.getDl_oggetto_unita_doc().parse();
            this.autoreDocMeta = filtri.getDs_autore_doc().parse();
            this.descrizioneDocMeta = filtri.getDl_doc().parse();
            this.tipoConservazione = filtri.getTi_conservazione().parse();
            this.subStruts = filtri.getNm_sub_strut().parse();
            this.cdKeyUnitaDocContiene = filtri.getCd_key_unita_doc_contiene().parse();
            this.profiloNorm = filtri.getFl_profilo_normativo().parse();
            // this.forzaHash = filtri.getFl_forza_hash().parse();
            // this.forzaFmtNumero = filtri.getFl_forza_fmt_numero().parse();
            // this.forzaFmtFile = filtri.getFl_forza_fmt_file().parse();
            this.cdVersioneWs = filtri.getCd_versione_ws().parse();
            this.flAggMeta = filtri.getFl_agg_meta().parse();
        }

        public String getCdVersioneXsdUd() {
            return cdVersioneXsdUd;
        }

        public String getCdVersioneXsdDoc() {
            return cdVersioneXsdDoc;
        }

        public BigDecimal getAnno() {
            return anno;
        }

        public String getCodice() {
            return codice;
        }

        public String getCdKeyDocVers() {
            return cdKeyDocVers;
        }

        public BigDecimal getAnnoRangeDa() {
            return annoRangeDa;
        }

        public BigDecimal getAnnoRangeA() {
            return annoRangeA;
        }

        public String getCodiceRangeDa() {
            return codiceRangeDa;
        }

        public String getCodiceRangeA() {
            return codiceRangeA;
        }

        public String getPresenza() {
            return presenza;
        }

        public String getEsito() {
            return esito;
        }

        public String getForzaAcc() {
            return forzaAcc;
        }

        public String getForzaConserva() {
            return forzaConserva;
        }

        public String getForzaColleg() {
            return forzaColleg;
        }

        public String getUnitaDocAnnul() {
            return unitaDocAnnul;
        }

        public String getDocAggiunti() {
            return docAggiunti;
        }

        public String getFlHashVers() {
            return flHashVers;
        }

        public String getStatoConserva() {
            return statoConserva;
        }

        public String getTiStatoUdElencoVers() {
            return tiStatoUdElencoVers;
        }

        public String getTiDoc() {
            return tiDoc;
        }

        public String getOggettoMeta() {
            return oggettoMeta;
        }

        public String getAutoreDocMeta() {
            return autoreDocMeta;
        }

        public String getDescrizioneDocMeta() {
            return descrizioneDocMeta;
        }

        public String getTipoConservazione() {
            return tipoConservazione;
        }

        public List<BigDecimal> getSubStruts() {
            return subStruts;
        }

        // public String getForzaHash() {
        // return forzaHash;
        // }
        //
        // public String getForzaFmtNumero() {
        // return forzaFmtNumero;
        // }
        //
        // public String getForzaFmtFile() {
        // return forzaFmtFile;
        // }

        public String getCdVersioneWs() {
            return cdVersioneWs;
        }

        void setCdVersioneXsdUd(String cdVersioneXsdUd) {
            this.cdVersioneXsdUd = cdVersioneXsdUd;
        }

        void setCdVersioneXsdDoc(String cdVersioneXsdDoc) {
            this.cdVersioneXsdDoc = cdVersioneXsdDoc;
        }

        void setAnno(BigDecimal anno) {
            this.anno = anno;
        }

        void setCodice(String codice) {
            this.codice = codice;
        }

        void setCdKeyDocVers(String cdKeyDocVers) {
            this.cdKeyDocVers = cdKeyDocVers;
        }

        void setAnnoRangeDa(BigDecimal annoRangeDa) {
            this.annoRangeDa = annoRangeDa;
        }

        void setAnnoRangeA(BigDecimal annoRangeA) {
            this.annoRangeA = annoRangeA;
        }

        void setCodiceRangeDa(String codiceRangeDa) {
            this.codiceRangeDa = codiceRangeDa;
        }

        void setCodiceRangeA(String codiceRangeA) {
            this.codiceRangeA = codiceRangeA;
        }

        void setPresenza(String presenza) {
            this.presenza = presenza;
        }

        void setEsito(String esito) {
            this.esito = esito;
        }

        void setForzaAcc(String forzaAcc) {
            this.forzaAcc = forzaAcc;
        }

        void setForzaConserva(String forzaConserva) {
            this.forzaConserva = forzaConserva;
        }

        void setForzaColleg(String forzaColleg) {
            this.forzaColleg = forzaColleg;
        }

        void setUnitaDocAnnul(String unitaDocAnnul) {
            this.unitaDocAnnul = unitaDocAnnul;
        }

        void setDocAggiunti(String docAggiunti) {
            this.docAggiunti = docAggiunti;
        }

        void setFlHashVers(String flHashVers) {
            this.flHashVers = flHashVers;
        }

        void setStatoConserva(String statoConserva) {
            this.statoConserva = statoConserva;
        }

        void setTiStatoUdElencoVers(String tiStatoUdElencoVers) {
            this.tiStatoUdElencoVers = tiStatoUdElencoVers;
        }

        void setTiDoc(String tiDoc) {
            this.tiDoc = tiDoc;
        }

        void setOggettoMeta(String oggettoMeta) {
            this.oggettoMeta = oggettoMeta;
        }

        void setAutoreDocMeta(String autoreDocMeta) {
            this.autoreDocMeta = autoreDocMeta;
        }

        void setDescrizioneDocMeta(String descrizioneDocMeta) {
            this.descrizioneDocMeta = descrizioneDocMeta;
        }

        void setTipoConservazione(String tipoConservazione) {
            this.tipoConservazione = tipoConservazione;
        }

        void setSubStruts(List<BigDecimal> subStruts) {
            this.subStruts = subStruts;
        }

        public String getCdKeyUnitaDocContiene() {
            return cdKeyUnitaDocContiene;
        }

        public void setCdKeyUnitaDocContiene(String cdKeyUnitaDocContiene) {
            this.cdKeyUnitaDocContiene = cdKeyUnitaDocContiene;
        }

        public String getProfiloNorm() {
            return this.profiloNorm;
        }

        public void setProfiloNorm(String profiloNorm) {
            this.profiloNorm = profiloNorm;
        }

        // public void setForzaHash(String forzaHash) {
        // this.forzaHash = forzaHash;
        // }
        //
        // public void setForzaFmtNumero(String forzaFmtNumero) {
        // this.forzaFmtNumero = forzaFmtNumero;
        // }
        //
        // public void setForzaFmtFile(String forzaFmtFile) {
        // this.forzaFmtFile = forzaFmtFile;
        // }

        public void setCdVersioneWs(String cdVersioneWs) {
            this.cdVersioneWs = cdVersioneWs;
        }

        public String getFlAggMeta() {
            return flAggMeta;
        }

        public void setFlAggMeta(String flAggMeta) {
            this.flAggMeta = flAggMeta;
        }

    }

    static class FiltriUnitaDocumentarieAnnullatePlain {

        private BigDecimal aaKeyUnitaDoc;
        private String cdKeyUnitaDoc;
        private BigDecimal aaKeyUnitaDocDa;
        private BigDecimal aaKeyUnitaDocA;
        private String cdKeyUnitaDocDa;
        private String cdKeyUnitaDocA;
        private Date dataCreazioneDa;
        private Date dataCreazioneA;
        private Date dataRegUnitaDocDa;
        private Date dataRegUnitaDocA;
        private Date dataAnnulDa;
        private Date dataAnnulA;
        private String tiAnnullamento;

        FiltriUnitaDocumentarieAnnullatePlain() {

        }

        private FiltriUnitaDocumentarieAnnullatePlain(UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate filtri,
                Date dataCreazioneDa, Date dataCreazioneA, Date dataRegUnitaDocDa, Date dataRegUnitaDocA,
                Date dataAnnulDa, Date dataAnnulA) throws EMFError {
            this.aaKeyUnitaDoc = filtri.getAa_key_unita_doc().parse();
            this.cdKeyUnitaDoc = filtri.getCd_key_unita_doc().parse();
            this.aaKeyUnitaDocDa = filtri.getAa_key_unita_doc_da().parse();
            this.aaKeyUnitaDocA = filtri.getAa_key_unita_doc_a().parse();
            this.cdKeyUnitaDocDa = filtri.getCd_key_unita_doc_da().parse();
            this.cdKeyUnitaDocA = filtri.getCd_key_unita_doc_a().parse();
            this.dataCreazioneDa = dataCreazioneDa;
            this.dataCreazioneA = dataCreazioneA;
            this.dataRegUnitaDocDa = dataRegUnitaDocDa;
            this.dataRegUnitaDocA = dataRegUnitaDocA;
            this.dataAnnulDa = dataAnnulDa;
            this.dataAnnulA = dataAnnulA;
            this.tiAnnullamento = filtri.getTi_annullamento().parse();
        }

        public BigDecimal getAaKeyUnitaDoc() {
            return aaKeyUnitaDoc;
        }

        public String getCdKeyUnitaDoc() {
            return cdKeyUnitaDoc;
        }

        public BigDecimal getAaKeyUnitaDocDa() {
            return aaKeyUnitaDocDa;
        }

        public BigDecimal getAaKeyUnitaDocA() {
            return aaKeyUnitaDocA;
        }

        public String getCdKeyUnitaDocDa() {
            return cdKeyUnitaDocDa;
        }

        public String getCdKeyUnitaDocA() {
            return cdKeyUnitaDocA;
        }

        public Date getDataCreazioneDa() {
            return dataCreazioneDa;
        }

        public Date getDataCreazioneA() {
            return dataCreazioneA;
        }

        public Date getDataRegUnitaDocDa() {
            return dataRegUnitaDocDa;
        }

        public Date getDataRegUnitaDocA() {
            return dataRegUnitaDocA;
        }

        public Date getDataAnnulDa() {
            return dataAnnulDa;
        }

        public Date getDataAnnulA() {
            return dataAnnulA;
        }

        public String getTiAnnullamento() {
            return tiAnnullamento;
        }

        void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
            this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        }

        void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
            this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        }

        void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
            this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
        }

        void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
            this.aaKeyUnitaDocA = aaKeyUnitaDocA;
        }

        void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
            this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
        }

        void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
            this.cdKeyUnitaDocA = cdKeyUnitaDocA;
        }

        void setDataCreazioneDa(Date dataCreazioneDa) {
            this.dataCreazioneDa = dataCreazioneDa;
        }

        void setDataCreazioneA(Date dataCreazioneA) {
            this.dataCreazioneA = dataCreazioneA;
        }

        void setDataRegUnitaDocDa(Date dataRegUnitaDocDa) {
            this.dataRegUnitaDocDa = dataRegUnitaDocDa;
        }

        void setDataRegUnitaDocA(Date dataRegUnitaDocA) {
            this.dataRegUnitaDocA = dataRegUnitaDocA;
        }

        void setDataAnnulDa(Date dataAnnulDa) {
            this.dataAnnulDa = dataAnnulDa;
        }

        void setDataAnnulA(Date dataAnnulA) {
            this.dataAnnulA = dataAnnulA;
        }

        void setTiAnnullamento(String tiAnnullamento) {
            this.tiAnnullamento = tiAnnullamento;
        }
    }

    static class FiltriUnitaDocumentarieAvanzataPlain {

        private String cdVersioneXsdUd;
        private String cdVersioneXsdDoc;
        private BigDecimal aaKeyUnitaDoc;
        private String cdKeyUnitaDoc;
        private String cdKeyDocVers;
        private BigDecimal aaKeyUnitaDocDa;
        private BigDecimal aaKeyUnitaDocA;
        private String cdKeyUnitaDocDa;
        private String cdKeyUnitaDocA;
        private String flUnitaDocFirmato;
        private List<String> tiEsitoVerifFirme;
        private String flForzaAccettazione;
        private String flForzaConservazione;
        private String flForzaCollegamento;
        // private String forzaHash;
        // private String forzaFmtNumero;
        // private String forzaFmtFile;
        private String cdVersioneWs;
        private String flUnitaDocAnnul;
        private String flDocAggiunti;//
        private String flAggMeta;
        private String flHashVers;
        private String tiStatoConservazione;
        private String tiStatoUdElencoVers;
        private List<String> nmSistemaMigraz;
        private Date dataMetaDa;
        private Date dataMetaA;
        private String tiDoc;
        private String dlOggettoUnitaDoc;
        private String dsAutoreDoc;
        private String dlDoc;
        private String tiConservazione;
        private String dsClassif;
        private String cdFascic;
        private String dsOggettoFascic;
        private String cdSottofascic;
        private String dsOggettoSottofascic;
        private List<BigDecimal> nmSubStrut;
        private String cdKeyUnitaDocContiene;
        private String profiloNorm;

        FiltriUnitaDocumentarieAvanzataPlain() {
        }

        FiltriUnitaDocumentarieAvanzataPlain(FiltriUnitaDocumentarieAvanzata filtri, Date dataMetaDa, Date dataMetaA)
                throws EMFError {
            this.dataMetaDa = dataMetaDa;
            this.dataMetaA = dataMetaA;
            this.cdVersioneXsdUd = filtri.getCd_versione_xsd_ud().parse();
            this.cdVersioneXsdDoc = filtri.getCd_versione_xsd_doc().parse();
            this.aaKeyUnitaDoc = filtri.getAa_key_unita_doc().parse();
            this.cdKeyUnitaDoc = filtri.getCd_key_unita_doc().parse();
            this.cdKeyUnitaDocContiene = filtri.getCd_key_unita_doc_contiene().parse();
            this.cdKeyDocVers = filtri.getCd_key_doc_vers().parse();
            this.aaKeyUnitaDocDa = filtri.getAa_key_unita_doc_da().parse();
            this.aaKeyUnitaDocA = filtri.getAa_key_unita_doc_a().parse();
            this.cdKeyUnitaDocDa = filtri.getCd_key_unita_doc_da().parse();
            this.cdKeyUnitaDocA = filtri.getCd_key_unita_doc_a().parse();
            this.flUnitaDocFirmato = filtri.getFl_unita_doc_firmato().parse();
            this.tiEsitoVerifFirme = filtri.getTi_esito_verif_firme().parse();
            this.flForzaAccettazione = filtri.getFl_forza_accettazione().parse();
            this.flForzaConservazione = filtri.getFl_forza_conservazione().parse();
            this.flForzaCollegamento = filtri.getFl_forza_collegamento().parse();
            this.flUnitaDocAnnul = filtri.getFl_unita_doc_annul().parse();
            this.flDocAggiunti = filtri.getFl_doc_aggiunti().parse();
            this.flHashVers = filtri.getFl_hash_vers().parse();
            this.tiStatoConservazione = filtri.getTi_stato_conservazione().parse();
            this.tiStatoUdElencoVers = filtri.getTi_stato_ud_elenco_vers().parse();
            this.nmSistemaMigraz = filtri.getNm_sistema_migraz().parse();
            this.tiDoc = filtri.getTi_doc().parse();
            this.dlOggettoUnitaDoc = filtri.getDl_oggetto_unita_doc().parse();
            this.dsAutoreDoc = filtri.getDs_autore_doc().parse();
            this.dlDoc = filtri.getDl_doc().parse();
            this.tiConservazione = filtri.getTi_conservazione().parse();
            this.dsClassif = filtri.getDs_classif().parse();
            this.cdFascic = filtri.getCd_fascic().parse();
            this.dsOggettoFascic = filtri.getDs_oggetto_fascic().parse();
            this.cdSottofascic = filtri.getCd_sottofascic().parse();
            this.dsOggettoSottofascic = filtri.getDs_oggetto_sottofascic().parse();
            this.nmSubStrut = filtri.getNm_sub_strut().parse();
            this.profiloNorm = filtri.getFl_profilo_normativo().parse();
            // this.forzaHash = filtri.getFl_forza_hash().parse();
            // this.forzaFmtNumero = filtri.getFl_forza_fmt_numero().parse();
            // this.forzaFmtFile = filtri.getFl_forza_fmt_file().parse();
            this.cdVersioneWs = filtri.getCd_versione_ws().parse();
            this.flAggMeta = filtri.getFl_agg_meta().parse();
        }

        FiltriUnitaDocumentarieAvanzataPlain(FiltriUnitaDocumentarieAvanzata filtri) {

        }

        public String getCdVersioneXsdUd() {
            return cdVersioneXsdUd;
        }

        void setCdVersioneXsdUd(String cdVersioneXsdUd) {
            this.cdVersioneXsdUd = cdVersioneXsdUd;
        }

        public String getCdVersioneXsdDoc() {
            return cdVersioneXsdDoc;
        }

        void setCdVersioneXsdDoc(String cdVersioneXsdDoc) {
            this.cdVersioneXsdDoc = cdVersioneXsdDoc;
        }

        public BigDecimal getAaKeyUnitaDoc() {
            return aaKeyUnitaDoc;
        }

        void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
            this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        }

        public String getCdKeyUnitaDoc() {
            return cdKeyUnitaDoc;
        }

        void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
            this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        }

        public String getCdKeyDocVers() {
            return cdKeyDocVers;
        }

        void setCdKeyDocVers(String cdKeyDocVers) {
            this.cdKeyDocVers = cdKeyDocVers;
        }

        public BigDecimal getAaKeyUnitaDocDa() {
            return aaKeyUnitaDocDa;
        }

        void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
            this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
        }

        public BigDecimal getAaKeyUnitaDocA() {
            return aaKeyUnitaDocA;
        }

        void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
            this.aaKeyUnitaDocA = aaKeyUnitaDocA;
        }

        public String getCdKeyUnitaDocDa() {
            return cdKeyUnitaDocDa;
        }

        void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
            this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
        }

        public String getCdKeyUnitaDocA() {
            return cdKeyUnitaDocA;
        }

        void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
            this.cdKeyUnitaDocA = cdKeyUnitaDocA;
        }

        public String getFlUnitaDocFirmato() {
            return flUnitaDocFirmato;
        }

        void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
            this.flUnitaDocFirmato = flUnitaDocFirmato;
        }

        public List<String> getTiEsitoVerifFirme() {
            return tiEsitoVerifFirme;
        }

        void setTiEsitoVerifFirme(List<String> tiEsitoVerifFirme) {
            this.tiEsitoVerifFirme = tiEsitoVerifFirme;
        }

        public String getFlForzaAccettazione() {
            return flForzaAccettazione;
        }

        void setFlForzaAccettazione(String flForzaAccettazione) {
            this.flForzaAccettazione = flForzaAccettazione;
        }

        public String getFlForzaConservazione() {
            return flForzaConservazione;
        }

        void setFlForzaConservazione(String flForzaConservazione) {
            this.flForzaConservazione = flForzaConservazione;
        }

        public String getFlForzaCollegamento() {
            return flForzaCollegamento;
        }

        void setFlForzaCollegamento(String flForzaCollegamento) {
            this.flForzaCollegamento = flForzaCollegamento;
        }

        public String getFlUnitaDocAnnul() {
            return flUnitaDocAnnul;
        }

        void setFlUnitaDocAnnul(String flUnitaDocAnnul) {
            this.flUnitaDocAnnul = flUnitaDocAnnul;
        }

        public String getFlDocAggiunti() {
            return flDocAggiunti;
        }

        void setFlDocAggiunti(String flDocAggiunti) {
            this.flDocAggiunti = flDocAggiunti;
        }

        public String getFlHashVers() {
            return flHashVers;
        }

        void setFlHashVers(String flHashVers) {
            this.flHashVers = flHashVers;
        }

        public String getTiStatoConservazione() {
            return tiStatoConservazione;
        }

        void setTiStatoConservazione(String tiStatoConservazione) {
            this.tiStatoConservazione = tiStatoConservazione;
        }

        public String getTiStatoUdElencoVers() {
            return tiStatoUdElencoVers;
        }

        void setTiStatoUdElencoVers(String tiStatoUdElencoVers) {
            this.tiStatoUdElencoVers = tiStatoUdElencoVers;
        }

        public List<String> getNmSistemaMigraz() {
            return nmSistemaMigraz;
        }

        void setNmSistemaMigraz(List<String> nmSistemaMigraz) {
            this.nmSistemaMigraz = nmSistemaMigraz;
        }

        public Date getDataMetaDa() {
            return dataMetaDa;
        }

        void setDataMetaDa(Date dataMetaDa) {
            this.dataMetaDa = dataMetaDa;
        }

        public Date getDataMetaA() {
            return dataMetaA;
        }

        void setDataMetaA(Date dataMetaA) {
            this.dataMetaA = dataMetaA;
        }

        public String getTiDoc() {
            return tiDoc;
        }

        void setTiDoc(String tiDoc) {
            this.tiDoc = tiDoc;
        }

        public String getDlOggettoUnitaDoc() {
            return dlOggettoUnitaDoc;
        }

        void setDlOggettoUnitaDoc(String dlOggettoUnitaDoc) {
            this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
        }

        public String getDsAutoreDoc() {
            return dsAutoreDoc;
        }

        void setDsAutoreDoc(String dsAutoreDoc) {
            this.dsAutoreDoc = dsAutoreDoc;
        }

        public String getDlDoc() {
            return dlDoc;
        }

        void setDlDoc(String dlDoc) {
            this.dlDoc = dlDoc;
        }

        public String getTiConservazione() {
            return tiConservazione;
        }

        void setTiConservazione(String tiConservazione) {
            this.tiConservazione = tiConservazione;
        }

        public String getDsClassif() {
            return dsClassif;
        }

        void setDsClassif(String dsClassif) {
            this.dsClassif = dsClassif;
        }

        public String getCdFascic() {
            return cdFascic;
        }

        void setCdFascic(String cdFascic) {
            this.cdFascic = cdFascic;
        }

        public String getDsOggettoFascic() {
            return dsOggettoFascic;
        }

        void setDsOggettoFascic(String dsOggettoFascic) {
            this.dsOggettoFascic = dsOggettoFascic;
        }

        public String getCdSottofascic() {
            return cdSottofascic;
        }

        void setCdSottofascic(String cdSottofascic) {
            this.cdSottofascic = cdSottofascic;
        }

        public String getDsOggettoSottofascic() {
            return dsOggettoSottofascic;
        }

        void setDsOggettoSottofascic(String dsOggettoSottofascic) {
            this.dsOggettoSottofascic = dsOggettoSottofascic;
        }

        public List<BigDecimal> getNmSubStrut() {
            return nmSubStrut;
        }

        void setNmSubStrut(List<BigDecimal> nmSubStrut) {
            this.nmSubStrut = nmSubStrut;
        }

        public String getCdKeyUnitaDocContiene() {
            return cdKeyUnitaDocContiene;
        }

        public void setCdKeyUnitaDocContiene(String cdKeyUnitaDocContiene) {
            this.cdKeyUnitaDocContiene = cdKeyUnitaDocContiene;
        }

        public String getProfiloNorm() {
            return this.profiloNorm;
        }

        public void setProfiloNorm(String profiloNorm) {
            this.profiloNorm = profiloNorm;
        }

        // public String getForzaHash() {
        // return forzaHash;
        // }
        //
        // public String getForzaFmtNumero() {
        // return forzaFmtNumero;
        // }
        //
        // public String getForzaFmtFile() {
        // return forzaFmtFile;
        // }

        public String getCdVersioneWs() {
            return cdVersioneWs;
        }

        // public void setForzaHash(String forzaHash) {
        // this.forzaHash = forzaHash;
        // }
        //
        // public void setForzaFmtNumero(String forzaFmtNumero) {
        // this.forzaFmtNumero = forzaFmtNumero;
        // }
        //
        // public void setForzaFmtFile(String forzaFmtFile) {
        // this.forzaFmtFile = forzaFmtFile;
        // }

        public void setCdVersioneWs(String cdVersioneWs) {
            this.cdVersioneWs = cdVersioneWs;
        }

        public String getFlAggMeta() {
            return flAggMeta;
        }

        public void setFlAggMeta(String flAggMeta) {
            this.flAggMeta = flAggMeta;
        }

    }

    static class FiltriUnitaDocumentarieDatiSpecPlain {

        private String cdVersioneXsdUd;
        private String cdVersioneXsdDoc;
        // private BigDecimal aaKeyUnitaDoc;
        // private String cdKeyUnitaDoc;
        private BigDecimal aaKeyUnitaDocDa;
        private BigDecimal aaKeyUnitaDocA;
        private String cdKeyUnitaDocDa;
        private String cdKeyUnitaDocA;
        private List<String> nmSistemaMigraz;
        private List<BigDecimal> nmSubStrut;
        // private String cdKeyUnitaDocContiene;

        FiltriUnitaDocumentarieDatiSpecPlain() {
        }

        FiltriUnitaDocumentarieDatiSpecPlain(FiltriUnitaDocumentarieDatiSpec filtri) throws EMFError {
            this.cdVersioneXsdUd = filtri.getCd_versione_xsd_ud().parse();
            this.cdVersioneXsdDoc = filtri.getCd_versione_xsd_doc().parse();
            /// this.aaKeyUnitaDoc = filtri.getAa_key_unita_doc().parse();
            // this.cdKeyUnitaDoc = filtri.getCd_key_unita_doc().parse();
            // this.cdKeyUnitaDocContiene = filtri.getCd_key_unita_doc_contiene().parse();
            this.aaKeyUnitaDocDa = filtri.getAa_key_unita_doc_da().parse();
            this.aaKeyUnitaDocA = filtri.getAa_key_unita_doc_a().parse();
            this.cdKeyUnitaDocDa = filtri.getCd_key_unita_doc_da().parse();
            this.cdKeyUnitaDocA = filtri.getCd_key_unita_doc_a().parse();
            // this.nmSistemaMigraz = filtri.getNm_sistema_migraz().parse();
            this.nmSubStrut = filtri.getNm_sub_strut().parse();
        }

        public String getCdVersioneXsdUd() {
            return cdVersioneXsdUd;
        }

        void setCdVersioneXsdUd(String cdVersioneXsdUd) {
            this.cdVersioneXsdUd = cdVersioneXsdUd;
        }

        public String getCdVersioneXsdDoc() {
            return cdVersioneXsdDoc;
        }

        void setCdVersioneXsdDoc(String cdVersioneXsdDoc) {
            this.cdVersioneXsdDoc = cdVersioneXsdDoc;
        }

        // public BigDecimal getAaKeyUnitaDoc() {
        // return aaKeyUnitaDoc;
        // }
        //
        // void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        // this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        // }
        //
        // public String getCdKeyUnitaDoc() {
        // return cdKeyUnitaDoc;
        // }
        //
        // void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        // this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        // }

        public BigDecimal getAaKeyUnitaDocDa() {
            return aaKeyUnitaDocDa;
        }

        void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
            this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
        }

        public BigDecimal getAaKeyUnitaDocA() {
            return aaKeyUnitaDocA;
        }

        void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
            this.aaKeyUnitaDocA = aaKeyUnitaDocA;
        }

        public String getCdKeyUnitaDocDa() {
            return cdKeyUnitaDocDa;
        }

        void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
            this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
        }

        public String getCdKeyUnitaDocA() {
            return cdKeyUnitaDocA;
        }

        void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
            this.cdKeyUnitaDocA = cdKeyUnitaDocA;
        }

        // public List<String> getNmSistemaMigraz() {
        // return nmSistemaMigraz;
        // }
        //
        // void setNmSistemaMigraz(List<String> nmSistemaMigraz) {
        // this.nmSistemaMigraz = nmSistemaMigraz;
        // }

        public List<BigDecimal> getNmSubStrut() {
            return nmSubStrut;
        }

        void setNmSubStrut(List<BigDecimal> nmSubStrut) {
            this.nmSubStrut = nmSubStrut;
        }

    }

    static class FiltriCollegamentiUnitaDocumentariePlain {

        private String conCollegamento;
        private String collegamentoRisolto;
        private String dsLinkUnitaDoc;
        private String cdRegistroKeyUnitaDocLink;
        private BigDecimal aaKeyUnitaDocLink;
        private String cdKeyUnitaDocLink;
        private String isOggettoCollegamento;
        private String dsLinkUnitaDocOggetto;

        FiltriCollegamentiUnitaDocumentariePlain() {

        }

        private FiltriCollegamentiUnitaDocumentariePlain(
                UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie filtriCollegamenti) throws EMFError {
            conCollegamento = filtriCollegamenti.getCon_collegamento().parse();
            collegamentoRisolto = filtriCollegamenti.getCollegamento_risolto().parse();
            dsLinkUnitaDoc = filtriCollegamenti.getDs_link_unita_doc().parse();
            cdRegistroKeyUnitaDocLink = filtriCollegamenti.getCd_registro_key_unita_doc_link().getDecodedValue();
            aaKeyUnitaDocLink = filtriCollegamenti.getAa_key_unita_doc_link().parse();
            cdKeyUnitaDocLink = filtriCollegamenti.getCd_key_unita_doc_link().parse();
            isOggettoCollegamento = filtriCollegamenti.getIs_oggetto_collegamento().parse();
            dsLinkUnitaDocOggetto = filtriCollegamenti.getDs_link_unita_doc_oggetto().parse();
        }

        public String getConCollegamento() {
            return conCollegamento;
        }

        void setConCollegamento(String conCollegamento) {
            this.conCollegamento = conCollegamento;
        }

        public String getCollegamentoRisolto() {
            return collegamentoRisolto;
        }

        void setCollegamentoRisolto(String collegamentoRisolto) {
            this.collegamentoRisolto = collegamentoRisolto;
        }

        public String getDsLinkUnitaDoc() {
            return dsLinkUnitaDoc;
        }

        void setDsLinkUnitaDoc(String dsLinkUnitaDoc) {
            this.dsLinkUnitaDoc = dsLinkUnitaDoc;
        }

        public String getCdRegistroKeyUnitaDocLink() {
            return cdRegistroKeyUnitaDocLink;
        }

        void setCdRegistroKeyUnitaDocLink(String cdRegistroKeyUnitaDocLink) {
            this.cdRegistroKeyUnitaDocLink = cdRegistroKeyUnitaDocLink;
        }

        public BigDecimal getAaKeyUnitaDocLink() {
            return aaKeyUnitaDocLink;
        }

        void setAaKeyUnitaDocLink(BigDecimal aaKeyUnitaDocLink) {
            this.aaKeyUnitaDocLink = aaKeyUnitaDocLink;
        }

        public String getCdKeyUnitaDocLink() {
            return cdKeyUnitaDocLink;
        }

        void setCdKeyUnitaDocLink(String cdKeyUnitaDocLink) {
            this.cdKeyUnitaDocLink = cdKeyUnitaDocLink;
        }

        public String getIsOggettoCollegamento() {
            return isOggettoCollegamento;
        }

        void setIsOggettoCollegamento(String isOggettoCollegamento) {
            this.isOggettoCollegamento = isOggettoCollegamento;
        }

        public String getDsLinkUnitaDocOggetto() {
            return dsLinkUnitaDocOggetto;
        }

        void setDsLinkUnitaDocOggetto(String dsLinkUnitaDocOggetto) {
            this.dsLinkUnitaDocOggetto = dsLinkUnitaDocOggetto;
        }
    }

    static class FiltriFirmatariUnitaDocumentariePlain {

        // private String conFirmatario;
        private String cdFirmatario;
        private String nmCognomeFirmatario;
        private String nmFirmatario;

        FiltriFirmatariUnitaDocumentariePlain() {

        }

        private FiltriFirmatariUnitaDocumentariePlain(
                UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie filtriFirmatari) throws EMFError {
            // conFirmatario = filtriFirmatari.getCon_firmatario().parse();
            cdFirmatario = filtriFirmatari.getCd_firmatario().parse();
            nmCognomeFirmatario = filtriFirmatari.getNm_cognome_firmatario().parse();
            nmFirmatario = filtriFirmatari.getNm_firmatario().parse();
        }

        // public String getConFirmatario() {
        // return conFirmatario;
        // }
        //
        // public void setConFirmatario(String conFirmatario) {
        // this.conFirmatario = conFirmatario;
        // }

        public String getCdFirmatario() {
            return cdFirmatario;
        }

        public void setCdFirmatario(String cdFirmatario) {
            this.cdFirmatario = cdFirmatario;
        }

        public String getNmCognomeFirmatario() {
            return nmCognomeFirmatario;
        }

        public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
            this.nmCognomeFirmatario = nmCognomeFirmatario;
        }

        public String getNmFirmatario() {
            return nmFirmatario;
        }

        public void setNmFirmatario(String nmFirmatario) {
            this.nmFirmatario = nmFirmatario;
        }

    }

    static class FiltriComponentiUnitaDocumentariePlain {

        private String tiEsitoContrFormatoFile;
        private String tiEsitoVerifFirma;
        private String tiEsitoVerifFirmeChiuse;
        private String dsHashFileCalc;
        private String dsAlgoHashFileCalc;
        private String cdEncodingHashFileCalc;
        private String dsUrnCompCalc;
        private String flForzaAccettazioneComp;
        private String flForzaConservazioneComp;
        private String tiSupportoComp;
        private BigDecimal nmTipoRapprComp;
        private String dsIdCompVers;
        private BigDecimal nmTipoStrutDoc;
        private BigDecimal nmTipoCompDoc;
        private String dsNomeCompVers;
        private String dlUrnCompVers;
        private String dsHashFileVers;
        private String nmMimetypeFile;
        private BigDecimal nmFormatoFileVers;
        private BigDecimal niSizeFileDa;
        private BigDecimal niSizeFileA;
        private String dsFormatoRapprCalc;
        private String dsFormatoRapprEstesoCalc;
        private String flCompFirmato;
        private String flRifTempVers;
        private String dsRifTempVers;
        private String tiEsitoContrConforme;
        private Timestamp dtScadFirmaCompDa;
        private Timestamp dtScadFirmaCompA;

        FiltriComponentiUnitaDocumentariePlain() {

        }

        private FiltriComponentiUnitaDocumentariePlain(
                UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie filtriComponenti) throws EMFError {
            tiEsitoContrFormatoFile = filtriComponenti.getTi_esito_contr_formato_file().parse();
            tiEsitoVerifFirma = filtriComponenti.getTi_esito_verif_firma().parse();
            tiEsitoVerifFirmeChiuse = filtriComponenti.getTi_esito_verif_firme_chius().parse();
            dsHashFileCalc = filtriComponenti.getDs_hash_file_calc().parse();
            dsAlgoHashFileCalc = filtriComponenti.getDs_algo_hash_file_calc().parse();
            cdEncodingHashFileCalc = filtriComponenti.getCd_encoding_hash_file_calc().parse();
            dsUrnCompCalc = filtriComponenti.getDs_urn_comp_calc().parse();
            flForzaAccettazioneComp = filtriComponenti.getFl_forza_accettazione_comp().parse();
            flForzaConservazioneComp = filtriComponenti.getFl_forza_conservazione_comp().parse();
            tiSupportoComp = filtriComponenti.getTi_supporto_comp().parse();
            nmTipoRapprComp = filtriComponenti.getNm_tipo_rappr_comp().parse();
            dsIdCompVers = filtriComponenti.getDs_id_comp_vers().parse();
            nmTipoStrutDoc = filtriComponenti.getNm_tipo_strut_doc().parse();
            nmTipoCompDoc = filtriComponenti.getNm_tipo_comp_doc().parse();
            dsNomeCompVers = filtriComponenti.getDs_nome_comp_vers().parse();
            dlUrnCompVers = filtriComponenti.getDl_urn_comp_vers().parse();
            dsHashFileVers = filtriComponenti.getDs_hash_file_vers().parse();
            nmMimetypeFile = filtriComponenti.getNm_mimetype_file().parse();
            nmFormatoFileVers = filtriComponenti.getNm_formato_file_vers().parse();
            niSizeFileDa = filtriComponenti.getNi_size_file_da().parse();
            niSizeFileA = filtriComponenti.getNi_size_file_a().parse();
            dsFormatoRapprCalc = filtriComponenti.getDs_formato_rappr_calc().parse();
            dsFormatoRapprEstesoCalc = filtriComponenti.getDs_formato_rappr_esteso_calc().parse();
            flCompFirmato = filtriComponenti.getFl_comp_firmato().parse();
            flRifTempVers = filtriComponenti.getFl_rif_temp_vers().parse();
            dsRifTempVers = filtriComponenti.getDs_rif_temp_vers().parse();
            tiEsitoContrConforme = filtriComponenti.getTi_esito_contr_conforme().parse();
            dtScadFirmaCompDa = filtriComponenti.getDt_scad_firma_comp_da().parse();
            dtScadFirmaCompA = filtriComponenti.getDt_scad_firma_comp_a().parse();
        }

        public String getTiEsitoContrFormatoFile() {
            return tiEsitoContrFormatoFile;
        }

        void setTiEsitoContrFormatoFile(String tiEsitoContrFormatoFile) {
            this.tiEsitoContrFormatoFile = tiEsitoContrFormatoFile;
        }

        public String getTiEsitoVerifFirma() {
            return tiEsitoVerifFirma;
        }

        void setTiEsitoVerifFirma(String tiEsitoVerifFirma) {
            this.tiEsitoVerifFirma = tiEsitoVerifFirma;
        }

        public String getTiEsitoVerifFirmeChiuse() {
            return tiEsitoVerifFirmeChiuse;
        }

        void setTiEsitoVerifFirmeChiuse(String tiEsitoVerifFirmeChiuse) {
            this.tiEsitoVerifFirmeChiuse = tiEsitoVerifFirmeChiuse;
        }

        public String getDsHashFileCalc() {
            return dsHashFileCalc;
        }

        void setDsHashFileCalc(String dsHashFileCalc) {
            this.dsHashFileCalc = dsHashFileCalc;
        }

        public String getDsAlgoHashFileCalc() {
            return dsAlgoHashFileCalc;
        }

        void setDsAlgoHashFileCalc(String dsAlgoHashFileCalc) {
            this.dsAlgoHashFileCalc = dsAlgoHashFileCalc;
        }

        public String getCdEncodingHashFileCalc() {
            return cdEncodingHashFileCalc;
        }

        void setCdEncodingHashFileCalc(String cdEncodingHashFileCalc) {
            this.cdEncodingHashFileCalc = cdEncodingHashFileCalc;
        }

        public String getDsUrnCompCalc() {
            return dsUrnCompCalc;
        }

        void setDsUrnCompCalc(String dsUrnCompCalc) {
            this.dsUrnCompCalc = dsUrnCompCalc;
        }

        public String getFlForzaAccettazioneComp() {
            return flForzaAccettazioneComp;
        }

        void setFlForzaAccettazioneComp(String flForzaAccettazioneComp) {
            this.flForzaAccettazioneComp = flForzaAccettazioneComp;
        }

        public String getFlForzaConservazioneComp() {
            return flForzaConservazioneComp;
        }

        void setFlForzaConservazioneComp(String flForzaConservazioneComp) {
            this.flForzaConservazioneComp = flForzaConservazioneComp;
        }

        public String getTiSupportoComp() {
            return tiSupportoComp;
        }

        void setTiSupportoComp(String tiSupportoComp) {
            this.tiSupportoComp = tiSupportoComp;
        }

        public BigDecimal getNmTipoRapprComp() {
            return nmTipoRapprComp;
        }

        void setNmTipoRapprComp(BigDecimal nmTipoRapprComp) {
            this.nmTipoRapprComp = nmTipoRapprComp;
        }

        public String getDsIdCompVers() {
            return dsIdCompVers;
        }

        void setDsIdCompVers(String dsIdCompVers) {
            this.dsIdCompVers = dsIdCompVers;
        }

        public BigDecimal getNmTipoStrutDoc() {
            return nmTipoStrutDoc;
        }

        void setNmTipoStrutDoc(BigDecimal nmTipoStrutDoc) {
            this.nmTipoStrutDoc = nmTipoStrutDoc;
        }

        public BigDecimal getNmTipoCompDoc() {
            return nmTipoCompDoc;
        }

        void setNmTipoCompDoc(BigDecimal nmTipoCompDoc) {
            this.nmTipoCompDoc = nmTipoCompDoc;
        }

        public String getDsNomeCompVers() {
            return dsNomeCompVers;
        }

        void setDsNomeCompVers(String dsNomeCompVers) {
            this.dsNomeCompVers = dsNomeCompVers;
        }

        public String getDlUrnCompVers() {
            return dlUrnCompVers;
        }

        void setDlUrnCompVers(String dlUrnCompVers) {
            this.dlUrnCompVers = dlUrnCompVers;
        }

        public String getDsHashFileVers() {
            return dsHashFileVers;
        }

        void setDsHashFileVers(String dsHashFileVers) {
            this.dsHashFileVers = dsHashFileVers;
        }

        public String getNmMimetypeFile() {
            return nmMimetypeFile;
        }

        void setNmMimetypeFile(String nmMimetypeFile) {
            this.nmMimetypeFile = nmMimetypeFile;
        }

        public BigDecimal getNmFormatoFileVers() {
            return nmFormatoFileVers;
        }

        void setNmFormatoFileVers(BigDecimal nmFormatoFileVers) {
            this.nmFormatoFileVers = nmFormatoFileVers;
        }

        public BigDecimal getNiSizeFileDa() {
            return niSizeFileDa;
        }

        void setNiSizeFileDa(BigDecimal niSizeFileDa) {
            this.niSizeFileDa = niSizeFileDa;
        }

        public BigDecimal getNiSizeFileA() {
            return niSizeFileA;
        }

        void setNiSizeFileA(BigDecimal niSizeFileA) {
            this.niSizeFileA = niSizeFileA;
        }

        public String getDsFormatoRapprCalc() {
            return dsFormatoRapprCalc;
        }

        void setDsFormatoRapprCalc(String dsFormatoRapprCalc) {
            this.dsFormatoRapprCalc = dsFormatoRapprCalc;
        }

        public String getDsFormatoRapprEstesoCalc() {
            return dsFormatoRapprEstesoCalc;
        }

        void setDsFormatoRapprEstesoCalc(String dsFormatoRapprEstesoCalc) {
            this.dsFormatoRapprEstesoCalc = dsFormatoRapprEstesoCalc;
        }

        public String getFlCompFirmato() {
            return flCompFirmato;
        }

        void setFlCompFirmato(String flCompFirmato) {
            this.flCompFirmato = flCompFirmato;
        }

        public String getFlRifTempVers() {
            return flRifTempVers;
        }

        void setFlRifTempVers(String flRifTempVers) {
            this.flRifTempVers = flRifTempVers;
        }

        public String getDsRifTempVers() {
            return dsRifTempVers;
        }

        void setDsRifTempVers(String dsRifTempVers) {
            this.dsRifTempVers = dsRifTempVers;
        }

        public String getTiEsitoContrConforme() {
            return tiEsitoContrConforme;
        }

        void setTiEsitoContrConforme(String tiEsitoContrConforme) {
            this.tiEsitoContrConforme = tiEsitoContrConforme;
        }

        public Timestamp getDtScadFirmaCompDa() {
            return dtScadFirmaCompDa;
        }

        void setDtScadFirmaCompDa(Timestamp dtScadFirmaCompDa) {
            this.dtScadFirmaCompDa = dtScadFirmaCompDa;
        }

        public Timestamp getDtScadFirmaCompA() {
            return dtScadFirmaCompA;
        }

        void setDtScadFirmaCompA(Timestamp dtScadFirmaCompA) {
            this.dtScadFirmaCompA = dtScadFirmaCompA;
        }
    }

    static class FiltriFascicoliUnitaDocumentariePlain {

        private String cdCompositoVoceTitol;
        private BigDecimal aaFascicolo;
        private String cdKeyFascicolo;
        private BigDecimal aaFascicoloDa;
        private BigDecimal aaFascicoloA;
        private String cdKeyFascicoloDa;
        private String cdKeyFascicoloA;
        private BigDecimal nmTipoFascicolo;
        private Timestamp dtApeFascicoloDa;
        private Timestamp dtApeFascicoloA;
        private Timestamp dtChiuFascicoloDa;
        private Timestamp dtChiuFascicoloA;
        private String dsOggettoFascicolo;

        FiltriFascicoliUnitaDocumentariePlain() {

        }

        private FiltriFascicoliUnitaDocumentariePlain(
                UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie filtriFascicoli) throws EMFError {
            cdCompositoVoceTitol = filtriFascicoli.getCd_composito_voce_titol().parse();
            aaFascicolo = filtriFascicoli.getAa_fascicolo().parse();
            cdKeyFascicolo = filtriFascicoli.getCd_key_fascicolo().parse();
            aaFascicoloDa = filtriFascicoli.getAa_fascicolo_da().parse();
            aaFascicoloA = filtriFascicoli.getAa_fascicolo_a().parse();
            cdKeyFascicoloDa = filtriFascicoli.getCd_key_fascicolo_da().parse();
            cdKeyFascicoloA = filtriFascicoli.getCd_key_fascicolo_a().parse();
            nmTipoFascicolo = filtriFascicoli.getNm_tipo_fascicolo().parse();
            dtApeFascicoloDa = filtriFascicoli.getDt_ape_fascicolo_da().parse();
            dtApeFascicoloA = filtriFascicoli.getDt_ape_fascicolo_a().parse();
            dtChiuFascicoloDa = filtriFascicoli.getDt_chiu_fascicolo_da().parse();
            dtChiuFascicoloA = filtriFascicoli.getDt_chiu_fascicolo_a().parse();
            dsOggettoFascicolo = filtriFascicoli.getDs_oggetto_fascicolo().parse();
        }

        public String getCdCompositoVoceTitol() {
            return cdCompositoVoceTitol;
        }

        void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
            this.cdCompositoVoceTitol = cdCompositoVoceTitol;
        }

        public BigDecimal getAaFascicolo() {
            return aaFascicolo;
        }

        void setAaFascicolo(BigDecimal aaFascicolo) {
            this.aaFascicolo = aaFascicolo;
        }

        public String getCdKeyFascicolo() {
            return cdKeyFascicolo;
        }

        void setCdKeyFascicolo(String cdKeyFascicolo) {
            this.cdKeyFascicolo = cdKeyFascicolo;
        }

        public BigDecimal getAaFascicoloDa() {
            return aaFascicoloDa;
        }

        void setAaFascicoloDa(BigDecimal aaFascicoloDa) {
            this.aaFascicoloDa = aaFascicoloDa;
        }

        public BigDecimal getAaFascicoloA() {
            return aaFascicoloA;
        }

        void setAaFascicoloA(BigDecimal aaFascicoloA) {
            this.aaFascicoloA = aaFascicoloA;
        }

        public String getCdKeyFascicoloDa() {
            return cdKeyFascicoloDa;
        }

        void setCdKeyFascicoloDa(String cdKeyFascicoloDa) {
            this.cdKeyFascicoloDa = cdKeyFascicoloDa;
        }

        public String getCdKeyFascicoloA() {
            return cdKeyFascicoloA;
        }

        void setCdKeyFascicoloA(String cdKeyFascicoloA) {
            this.cdKeyFascicoloA = cdKeyFascicoloA;
        }

        public BigDecimal getNmTipoFascicolo() {
            return nmTipoFascicolo;
        }

        void setNmTipoFascicolo(BigDecimal nmTipoFascicolo) {
            this.nmTipoFascicolo = nmTipoFascicolo;
        }

        public Timestamp getDtApeFascicoloDa() {
            return dtApeFascicoloDa;
        }

        void setDtApeFascicoloDa(Timestamp dtApeFascicoloDa) {
            this.dtApeFascicoloDa = dtApeFascicoloDa;
        }

        public Timestamp getDtApeFascicoloA() {
            return dtApeFascicoloA;
        }

        void setDtApeFascicoloA(Timestamp dtApeFascicoloA) {
            this.dtApeFascicoloA = dtApeFascicoloA;
        }

        public Timestamp getDtChiuFascicoloDa() {
            return dtChiuFascicoloDa;
        }

        void setDtChiuFascicoloDa(Timestamp dtChiuFascicoloDa) {
            this.dtChiuFascicoloDa = dtChiuFascicoloDa;
        }

        public Timestamp getDtChiuFascicoloA() {
            return dtChiuFascicoloA;
        }

        void setDtChiuFascicoloA(Timestamp dtChiuFascicoloA) {
            this.dtChiuFascicoloA = dtChiuFascicoloA;
        }

        public String getDsOggettoFascicolo() {
            return dsOggettoFascicolo;
        }

        void setDsOggettoFascicolo(String dsOggettoFascicolo) {
            this.dsOggettoFascicolo = dsOggettoFascicolo;
        }

        boolean isFiltriImpostati() {
            for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {
                try {
                    if (field.get(this) != null) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
            return false;
        }
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     *
     * @return entity AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public AroVDtVersMaxByUnitaDoc getAroVDtVersMaxByUd(long idUnitaDoc) {
        String queryStr = "SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", bigDecimalFromLong(idUnitaDoc));
        List<AroVDtVersMaxByUnitaDoc> lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    @Override
    // Voglio che siano caricati anche i dati lazy della OrgStrut
    public <T> T findById(Class<T> entityClass, Serializable id) {
        if (entityClass == AroUnitaDoc.class) {
            String queryStr = "SELECT ud FROM AroUnitaDoc ud JOIN FETCH ud.orgStrut st JOIN FETCH st.orgEnte et JOIN FETCH et.orgAmbiente WHERE ud.idUnitaDoc = :idUnitaDoc";
            Query query = getEntityManager().createQuery(queryStr, entityClass);
            query.setParameter("idUnitaDoc", id);
            return entityClass.cast(query.getSingleResult());
        } else {
            return super.findById(entityClass, id);
        }
    }

    public String getProfiloNormativo(String tiUsoModelloXsd, TiModelloXsdUd tiModelloXsdUd, long idUnitaDoc) {

        List<VrsXmlModelloSessioneVers> lstXmlModelloSessioneVers = null;

        try {

            String queryStr = "select xms from VrsXmlModelloSessioneVers xms "
                    + "join xms.decUsoModelloXsdUniDoc.decModelloXsdUd modello_xsd "
                    + "join xms.vrsDatiSessioneVers dati_ses " + "join dati_ses.vrsSessioneVers ses "
                    + "where modello_xsd.tiUsoModelloXsd = :tiUsoModelloXsd "
                    + "and modello_xsd.tiModelloXsd = :tiModelloXsdUd " + "and dati_ses.tiDatiSessioneVers = 'XML_DOC' "
                    + "and ses.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "and ses.aroDoc is null "
                    + "and ses.tiStatoSessioneVers = 'CHIUSA_OK' " + "and ses.tiSessioneVers = 'VERSAMENTO' ";

            javax.persistence.Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
            query.setParameter("tiModelloXsdUd", tiModelloXsdUd);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstXmlModelloSessioneVers = query.getResultList();
            if (lstXmlModelloSessioneVers != null && !lstXmlModelloSessioneVers.isEmpty()) {
                return lstXmlModelloSessioneVers.get(0).getBlXml();
            }

        } catch (Exception e) {
            log.error("Eccezione durante il recupero del profilo normativo ud " + e.getMessage(), e);
        }
        return null;
    }

    public List<DecVersioneWs> getDecVersioneWsList(String tiWs) {
        Query query = getEntityManager().createQuery("SELECT versioneWs FROM DecVersioneWs versioneWs "
                + "WHERE versioneWs.tiWs = :tiWs " + "ORDER BY versioneWs.cdVersioneWs");
        query.setParameter("tiWs", tiWs);
        return query.getResultList();
    }

    // MEV #31162
    public AroLogStatoConservUdTableBean getAroLogStatoConservUdTableBean(BigDecimal idUnitaDoc) {
        String queryStr = "SELECT u FROM AroLogStatoConservUd u WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ORDER BY u.dtStato DESC";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        List<AroLogStatoConservUd> listaLog = query.getResultList();
        AroLogStatoConservUdTableBean logTableBean = new AroLogStatoConservUdTableBean();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS"); // Formato desiderato
        try {
            for (AroLogStatoConservUd udLog : listaLog) {
                AroLogStatoConservUdRowBean logRowBean = new AroLogStatoConservUdRowBean();
                logRowBean = (AroLogStatoConservUdRowBean) Transform.entity2RowBean(udLog);
                String dtStatoString = sdf.format(udLog.getDtStato());
                logRowBean.setString("dt_stato_string", dtStatoString);
                logTableBean.add(logRowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return logTableBean;
    }

    // end MEV #31162
}
