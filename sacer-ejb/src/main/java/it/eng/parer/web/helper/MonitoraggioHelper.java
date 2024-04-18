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

import static it.eng.parer.helper.GenericHelper.bigDecimalFromInteger;
import static it.eng.parer.helper.GenericHelper.bigDecimalFromLong;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import it.eng.parer.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriConsistenzaSacer;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriContenutoSacer;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriJobSchedulati;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriOperazioniVolumi;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriReplicaOrg;
import it.eng.parer.slite.gen.tablebean.VrsFileSessioneKoRowBean;
import it.eng.parer.slite.gen.tablebean.VrsFileSessioneKoTableBean;
import it.eng.parer.slite.gen.tablebean.VrsSessioneVersKoRowBean;
import it.eng.parer.slite.gen.tablebean.VrsSessioneVersKoTableBean;
import it.eng.parer.slite.gen.viewbean.AroVDocRangeDtRowBean;
import it.eng.parer.slite.gen.viewbean.AroVDocRangeDtTableBean;
import it.eng.parer.slite.gen.viewbean.AroVDocTiUdRangeDtRowBean;
import it.eng.parer.slite.gen.viewbean.AroVDocTiUdRangeDtTableBean;
import it.eng.parer.slite.gen.viewbean.AroVDocVolRangeDtRowBean;
import it.eng.parer.slite.gen.viewbean.AroVDocVolRangeDtTableBean;
import it.eng.parer.slite.gen.viewbean.AroVDocVolTiUdRangeDtRowBean;
import it.eng.parer.slite.gen.viewbean.AroVDocVolTiUdRangeDtTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisLogOperRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisLogOperTableBean;
import it.eng.parer.slite.gen.viewbean.IamVLisOrganizDaReplicRowBean;
import it.eng.parer.slite.gen.viewbean.IamVLisOrganizDaReplicTableBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedHistRowBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedHistTableBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedRowBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedStrutHistRowBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedStrutHistTableBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedStrutRowBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedStrutTableBean;
import it.eng.parer.slite.gen.viewbean.LogVLisSchedTableBean;
import it.eng.parer.slite.gen.viewbean.LogVVisLastSchedRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisDocNonVersIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisDocNonVersIamTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisOperVolIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisOperVolIamTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisSesRecupRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisSesRecupTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUdNonVersIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUdNonVersIamTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUdVersTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisVersDocNonVersTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisVersErrIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisVersErrIamTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisVersUdNonVersTableBean;
import it.eng.parer.slite.gen.viewbean.MonVRiepStrutIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVRiepStrutIamTableBean;
import it.eng.parer.slite.gen.viewbean.MonVVisDocNonVersRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisSesErrIamRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisUdNonVersRowBean;
import it.eng.parer.slite.gen.viewbean.MonVVisVersErrIamRowBean;
import it.eng.parer.slite.gen.viewbean.VrsVSessioneAggRisoltaRowBean;
import it.eng.parer.slite.gen.viewbean.VrsVSessioneAggRisoltaTableBean;
import it.eng.parer.slite.gen.viewbean.VrsVSessioneVersRisoltaRowBean;
import it.eng.parer.slite.gen.viewbean.VrsVSessioneVersRisoltaTableBean;
import it.eng.parer.viewEntity.IamVLisOrganizDaReplic;
import it.eng.parer.viewEntity.LogVLisSched;
import it.eng.parer.viewEntity.LogVLisSchedHist;
import it.eng.parer.viewEntity.LogVLisSchedStrut;
import it.eng.parer.viewEntity.LogVLisSchedStrutHist;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.viewEntity.MonVLisDocNonVersIam;
import it.eng.parer.viewEntity.MonVLisOperVolIam;
import it.eng.parer.viewEntity.MonVLisSesRecup;
import it.eng.parer.viewEntity.MonVLisUdNonVersIam;
import it.eng.parer.viewEntity.MonVLisVersDocNonVers;
import it.eng.parer.viewEntity.MonVLisVersErrIam;
import it.eng.parer.viewEntity.MonVLisVersUdNonVers;
import it.eng.parer.viewEntity.MonVRiepStrutIam;
import it.eng.parer.viewEntity.MonVVisDocNonVers;
import it.eng.parer.viewEntity.MonVVisSesErrIam;
import it.eng.parer.viewEntity.MonVVisUdNonVers;
import it.eng.parer.viewEntity.MonVVisVersErrIam;
import it.eng.parer.web.dto.CounterResultBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiDistintiDocBean;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.base.table.BaseTable;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class MonitoraggioHelper implements Serializable {

    private static final long serialVersionUID = -3416899035771856955L;

    Logger log = LoggerFactory.getLogger(MonitoraggioHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    private LazyListHelper lazyListHelper;

    @EJB
    private ObjectStorageService objectStorageService;

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta i documenti raggruppandoli per
     * tipo, stato e data di creazione senza filtrare per tipo unità documentaria
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     *
     * @return AroVDocRangeDtTableBean, il tablebean contenente la lista di documenti
     */
    public AroVDocRangeDtTableBean contaDocNoUd(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione, count(doc) as ni_doc "
                        + "from IamAbilOrganiz iao, " + "AroVDocRangeDt doc, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.id.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.id.tiStatoDoc in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED', 'IN_VOLUME_APERTO', 'IN_VOLUME_IN_ERRORE', 'IN_VOLUME_CHIUSO', 'IN_VOLUME_DA_CHIUDERE', 'IN_ATTESA_MEMORIZZAZIONE') "
                        + "and strut.idStrut = iao.idOrganizApplic " + "and doc.id.idStrut = iao.idOrganizApplic ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("group by doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione "
                + "order by doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaDoc = query.getResultList();
        AroVDocRangeDtTableBean contaDocTableBean = new AroVDocRangeDtTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaDoc) {
                AroVDocRangeDtRowBean rowBean = new AroVDocRangeDtRowBean();
                rowBean.setTiDoc(row[0].toString());
                rowBean.setTiStatoDoc(row[1].toString());
                rowBean.setTiDtCreazione(row[2].toString());
                rowBean.setString("ni_doc", row[3].toString());
                contaDocTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaDocTableBean;
    }

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta i documenti raggruppandoli per
     * tipo, stato e data di creazione filtrando anche per tipo unità documentaria
     *
     * @param idUtente
     *            l'id associato ai permessi di un utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     *
     * @return AroVDocTiUdRangeDtTableBean, il tablebean contenente la lista di documenti
     *
     */
    public AroVDocTiUdRangeDtTableBean contaDocUd(long idUtente, BigDecimal idTipoUnitaDoc, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione, count(doc) as ni_doc "
                        + "from IamAbilOrganiz iao, " + "AroVDocTiUdRangeDt doc, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.id.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.id.tiStatoDoc in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED', 'IN_VOLUME_APERTO', 'IN_VOLUME_IN_ERRORE', 'IN_VOLUME_CHIUSO', 'IN_VOLUME_DA_CHIUDERE', 'IN_ATTESA_MEMORIZZAZIONE') "
                        + "and doc.id.idStrut = iao.idOrganizApplic " + "and strut.idStrut = iao.idOrganizApplic "
                        + "and doc.id.idTipoUnitaDoc = :idTipo ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("group by doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione "
                + "order by doc.id.tiDoc, doc.id.tiStatoDoc, doc.id.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        query.setParameter("idTipo", idTipoUnitaDoc);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaDoc = query.getResultList();
        AroVDocTiUdRangeDtTableBean contaDocTableBean = new AroVDocTiUdRangeDtTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaDoc) {
                AroVDocTiUdRangeDtRowBean rowBean = new AroVDocTiUdRangeDtRowBean();
                rowBean.setTiDoc(row[0].toString());
                rowBean.setTiStatoDoc(row[1].toString());
                rowBean.setTiDtCreazione(row[2].toString());
                rowBean.setString("ni_doc", row[3].toString());
                contaDocTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaDocTableBean;
    }

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta i documenti raggruppandoli per
     * tipo, stato del volume e data di creazione senza filtrare per tipo unità documentaria
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     *
     * @return AroVDocVolRangeDtTableBean, il tablebean contenente la lista di documenti
     */
    public AroVDocVolRangeDtTableBean contaDocStatoVolNoUd(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select doc.id.tiDoc, doc.id.tiStatoVolumeConserv, doc.id.tiDtCreazione, count(doc) as ni_doc_chiuso "
                        + "from IamAbilOrganiz iao, " + "AroVDocVolRangeDt doc, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.id.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.id.tiStatoDoc = 'IN_VOLUME_CHIUSO' "
                        + "and doc.id.tiStatoVolumeConserv in ('CHIUSO', 'FIRMATO', 'FIRMATO_NO_MARCA', 'DA_VERIFICARE') "
                        + "and strut.idStrut = iao.idOrganizApplic " + "and doc.id.idStrut = iao.idOrganizApplic ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("group by doc.id.tiDoc, doc.id.tiStatoVolumeConserv, doc.id.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaDoc = query.getResultList();
        AroVDocVolRangeDtTableBean contaDocTableBean = new AroVDocVolRangeDtTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaDoc) {
                AroVDocVolRangeDtRowBean rowBean = new AroVDocVolRangeDtRowBean();
                rowBean.setTiDoc(row[0].toString());
                rowBean.setTiStatoVolumeConserv(row[1].toString());
                rowBean.setTiDtCreazione(row[2].toString());
                rowBean.setString("ni_doc_chiuso", row[3].toString());
                contaDocTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaDocTableBean;
    }

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta i documenti raggruppandoli per
     * tipo, stato del volume e data di creazione filtrando per tipo unità documentaria
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     *
     * @return AroVDocVolTiUdRangeDtTableBean, il tablebean contenente la lista di documenti
     */
    public AroVDocVolTiUdRangeDtTableBean contaDocStatoVolUd(long idUtente, BigDecimal idTipoUnitaDoc,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select doc.id.tiDoc, doc.id.tiStatoVolumeConserv, doc.id.tiDtCreazione, count(doc) as ni_doc_chiuso "
                        + "from IamAbilOrganiz iao, " + "AroVDocVolTiUdRangeDt doc, OrgStrut strut  "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.id.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.id.tiStatoDoc = 'IN_VOLUME_CHIUSO' "
                        + "and doc.id.tiStatoVolumeConserv in ('CHIUSO', 'FIRMATO', 'FIRMATO_NO_MARCA', 'DA_VERIFICARE') "
                        + " and strut.idStrut = iao.idOrganizApplic " + "and doc.id.idStrut = iao.idOrganizApplic "
                        + "and doc.id.idTipoUnitaDoc = :idTipo ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente  = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic  = :idStrut ");
        }

        queryStr.append("group by doc.id.tiDoc, doc.id.tiStatoVolumeConserv, doc.id.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        query.setParameter("idTipo", idTipoUnitaDoc);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaDoc = query.getResultList();
        AroVDocVolTiUdRangeDtTableBean contaDocTableBean = new AroVDocVolTiUdRangeDtTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaDoc) {
                AroVDocVolTiUdRangeDtRowBean rowBean = new AroVDocVolTiUdRangeDtRowBean();
                rowBean.setTiDoc(row[0].toString());
                rowBean.setTiStatoVolumeConserv(row[1].toString());
                rowBean.setTiDtCreazione(row[2].toString());
                rowBean.setString("ni_doc_chiuso", row[3].toString());
                contaDocTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaDocTableBean;
    }

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta le sessioni di tipo VERSAMENTO
     * con indicazione di risolta e verificata
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     *
     * @return VrsVSessioneVersRisoltaTableBean, il tablebean contenente la lista di documenti
     */
    public VrsVSessioneVersRisoltaTableBean contaSessioniVersRisVer(long idUtente, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select ses.id.flSesRisolta, ses.id.tiDtCreazione, count(ses) as ni_ses_vers, ses.id.flVerif, ses.id.flSesNonRisolub "
                        + "from IamAbilOrganiz iao, " + "VrsVSessioneVersRisolta ses, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa " + "and ses.id.tiSessioneVers = 'VERSAMENTO' "
                        + "and strut.idStrut = iao.idOrganizApplic " + "and ses.idStrut = iao.idOrganizApplic ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("group by ses.id.flSesRisolta, ses.id.tiDtCreazione, ses.id.flVerif, ses.id.flSesNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaSessioni = query.getResultList();
        VrsVSessioneVersRisoltaTableBean contaSessioniTableBean = new VrsVSessioneVersRisoltaTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaSessioni) {
                VrsVSessioneVersRisoltaRowBean rowBean = new VrsVSessioneVersRisoltaRowBean();
                rowBean.setFlSesRisolta(row[0] != null ? row[0].toString() : null);
                rowBean.setTiDtCreazione(row[1] != null ? row[1].toString() : null);
                rowBean.setString("ni_ses_vers", row[2] != null ? row[2].toString() : "0");
                rowBean.setFlVerif(row[3] != null ? row[3].toString() : null);
                rowBean.setFlSesNonRisolub(row[4] != null ? row[4].toString() : null);
                contaSessioniTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaSessioniTableBean;
    }

    /**
     * Utilizzato per il calcolo dei totali nella pagina di Riepilogo Versamenti, conta le sessioni di tipo
     * AGGIUNGI_DOCUMENTO con indicazione di risolta e verificata
     *
     * @param idUtente
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStruttura
     *            id struttura
     *
     * @return VrsVSessioneAggRisoltaTableBean, il tablebean contenente la lista di documenti
     */
    public VrsVSessioneAggRisoltaTableBean contaSessioniAggRisVer(long idUtente, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "select ses.id.flSesRisolta, ses.id.tiDtCreazione, count(ses) as ni_ses_agg, ses.id.flVerif, ses.id.flSesNonRisolub "
                        + "from IamAbilOrganiz iao,  " + "VrsVSessioneAggRisolta ses, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa " + "and ses.id.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
                        + "and ses.id.tiStatoSessioneVers = 'CHIUSA_ERR' " + "and strut.idStrut = iao.idOrganizApplic "
                        + "and ses.id.idStrut = iao.idOrganizApplic ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("group by ses.id.flSesRisolta, ses.id.tiDtCreazione, ses.id.flVerif, ses.id.flSesNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> contaSessioni = query.getResultList();
        VrsVSessioneAggRisoltaTableBean contaSessioniTableBean = new VrsVSessioneAggRisoltaTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : contaSessioni) {
                VrsVSessioneAggRisoltaRowBean rowBean = new VrsVSessioneAggRisoltaRowBean();
                rowBean.setFlSesRisolta(row[0] != null ? row[0].toString() : null);
                rowBean.setTiDtCreazione(row[1] != null ? row[1].toString() : null);
                rowBean.setString("ni_ses_agg", row[2] != null ? row[2].toString() : "0");
                rowBean.setFlVerif(row[3] != null ? row[3].toString() : null);
                rowBean.setFlSesNonRisolub(row[4] != null ? row[4].toString() : null);
                contaSessioniTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contaSessioniTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina di Riepilogo Struttura
     *
     * @param idUtente
     *            l'id associato ai permessi di un utente
     * @param maxResult
     *            il numero di record da recuperare
     * @param idAmbiente
     *            id ambiente
     *
     * @return entity bean MonVRiepStrutTableBean
     *
     */
    public MonVRiepStrutIamTableBean getMonVRiepStrutIamViewBean(long idUtente, int maxResult, int idAmbiente) {
        String queryStr = "SELECT u FROM MonVRiepStrutIam u WHERE u.idUserIam = :idUtente AND u.idAmbiente = :idAmbiente "
                + "ORDER BY u.nmAmbiente, u.nmEnte, u.nmStrut";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUtente", bigDecimalFromLong(idUtente));
        query.setParameter("idAmbiente", bigDecimalFromInteger(idAmbiente));
        query.setMaxResults(maxResult);

        return lazyListHelper.getTableBean(query, this::getMonVRiepStrutIamTableBeanFrom);
    }

    private MonVRiepStrutIamTableBean getMonVRiepStrutIamTableBeanFrom(List<MonVRiepStrutIam> listaMon) {
        MonVRiepStrutIamTableBean monTableBean = new MonVRiepStrutIamTableBean();

        try {
            if (listaMon != null && !listaMon.isEmpty()) {
                monTableBean = (MonVRiepStrutIamTableBean) Transform.entities2TableBean(listaMon);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Ridefinisco il campo struttura affinchè contenga ambiente, ente e struttura
        for (MonVRiepStrutIamRowBean rb : monTableBean) {
            rb.setNmStrut(rb.getNmAmbiente() + ", " + rb.getNmEnte() + ", " + rb.getNmStrut());
        }
        return monTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Lista Documenti
     *
     * @param filtri
     *            i filtri di ricerca riportati dalla pagina precedente
     * @param maxResult
     *            risultato massimo
     *
     *
     *
     * @return entity bean MonVLisDocTableBean
     *
     */
    public MonVLisUdVersTableBean getMonVLisDocViewBean(final MonitoraggioFiltriListaDocBean filtri, int maxResult) {
        String whereWord = "WHERE ";
        // Modifica 16/05/2016 - utilizzo di tabelle diverse in base a determinati
        // filtri
        String table;
        if (StringUtils.isNotBlank(filtri.getTipoDoc()) && filtri.getTipoDoc().equals("1")) {
            if (StringUtils.isBlank(filtri.getStatoDoc())
                    || (!filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())
                            && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED.name())
                            && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name()))) {
                // Doc principale, stato doc in elenco nullo o != da IN_ATTESA_MEM,
                // IN_ATTESA_SCHED, NON_SELEZ_SCHED
                table = "MonVLisUdVers";
            } else {
                // Doc principale, stato doc in elenco == a IN_ATTESA_MEM, IN_ATTESA_SCHED,
                // NON_SELEZ_SCHED
                table = "MonVLisUdVersDaElab";
            }
        } else if (StringUtils.isBlank(filtri.getStatoDoc())
                || (!filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())
                        && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED.name())
                        && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name()))) {
            // Doc non principale, stato doc in elenco nullo o != da IN_ATTESA_MEM,
            // IN_ATTESA_SCHED, NON_SELEZ_SCHED
            table = "MonVLisDocVers";
        } else if (filtri.getTipoCreazione().equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
            // Doc non principale, tipo creazione == AGGIUNTA_DOCUMENTO, stato doc in elenco
            // == a IN_ATTESA_MEM,
            // IN_ATTESA_SCHED, NON_SELEZ_SCHED
            table = "MonVLisDocAggDaElab";
        } else {
            // Doc non principale, tipo creazione != AGGIUNTA_DOCUMENTO, stato doc in elenco
            // == a IN_ATTESA_MEM,
            // IN_ATTESA_SCHED, NON_SELEZ_SCHED
            table = "MonVLisDocVers";
        }

        StringBuilder queryStr = new StringBuilder("SELECT u FROM " + table + " u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtri.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtri.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtri.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        BigDecimal idTipoUnitaDoc = filtri.getIdTipoUnitaDoc();
        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord).append("u.idTipoUnitaDoc = :idTipoUnitaDoc ");
            whereWord = "AND ";
        }

        if (filtri.getIdTipoDoc() != null) {
            queryStr.append(whereWord).append("u.idTipoDoc = :idTipoDoc ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        String registro = filtri.getCdRegistroKeyUnitaDoc();
        // Inserimento nella query del filtro CHIAVE UNITA DOC
        BigDecimal anno = filtri.getAaKeyUnitaDoc();
        String codice = filtri.getCdKeyUnitaDoc();

        if (StringUtils.isNotBlank(registro)) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registro ");
            whereWord = "AND ";
        }

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro CHIAVE UNITA DOC PER RANGE
        BigDecimal annoRangeDa = filtri.getAaKeyUnitaDocDa();
        BigDecimal annoRangeA = filtri.getAaKeyUnitaDocA();
        String codiceRangeDa = filtri.getCdKeyUnitaDocDa();
        String codiceRangeA = filtri.getCdKeyUnitaDocA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro tipo doc (PRINCIPALE = 1, tutti i
        // documenti = 0
        String tipoDoc = filtri.getTipoDoc();
        if (tipoDoc != null && tipoDoc.equals("1")) {
            queryStr.append(whereWord).append("u.tiDoc = 'PRINCIPALE' ");
            whereWord = "AND ";
        }

        // GESTIONE PERIODO - GIORNO
        Calendar dataDBa = Calendar.getInstance();
        Calendar dataDBda = Calendar.getInstance();
        dataDBda.set(Calendar.HOUR_OF_DAY, 0);
        dataDBda.set(Calendar.MINUTE, 0);
        dataDBda.set(Calendar.SECOND, 0);
        dataDBda.set(Calendar.MILLISECOND, 0);
        dataDBa.set(Calendar.HOUR_OF_DAY, 23);
        dataDBa.set(Calendar.MINUTE, 59);
        dataDBa.set(Calendar.SECOND, 59);
        dataDBa.set(Calendar.MILLISECOND, 999);

        // Inserimento nella query del filtro periodo versamento
        String periodoVers = filtri.getPeriodoVers();
        if (periodoVers != null) {
            if (periodoVers.equals("ULTIMI7")) {
                dataDBda.add(Calendar.DATE, -6);
                queryStr.append(whereWord).append("u.dtCreazione between :datada AND :dataa ");
            } else if (periodoVers.equals("OGGI")) {
                queryStr.append(whereWord).append("u.dtCreazione between :datada AND :dataa ");
            } else {
                queryStr.append(whereWord).append("u.dtCreazione < :dataa ");
            }
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno
        // versamento
        Date dataOrarioDa = (filtri.getGiornoVersDaValidato() != null ? filtri.getGiornoVersDaValidato() : null);
        Date dataOrarioA = (filtri.getGiornoVersAValidato() != null ? filtri.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtCreazione between :datada AND :dataa ");
            whereWord = "AND ";
        }

        String statoDoc = filtri.getStatoDoc();
        if (statoDoc != null) {
            queryStr.append(whereWord).append("u.tiStatoDocElencoVers = :tiStatoDocElencoVers ");
            whereWord = "AND ";
        }

        BigDecimal idUtente = filtri.getIdUserIam();
        if (idUtente != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUtente ");
            whereWord = "AND ";
        }

        String tipoCreazione = filtri.getTipoCreazione();
        if (StringUtils.isNotBlank(tipoCreazione)) {
            queryStr.append(whereWord).append("u.tiCreazione = :tiCreazione ");
        }

        // ordina per data creazione descrescente
        queryStr.append("ORDER BY u.dtCreazione DESC, u.dsOrdDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        }

        if (filtri.getIdTipoDoc() != null) {
            query.setParameter("idTipoDoc", filtri.getIdTipoDoc());
        }

        if (registro != null) {
            query.setParameter("registro", registro);
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

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        if (statoDoc != null) {
            query.setParameter("tiStatoDocElencoVers", statoDoc);

        }

        if (idUtente != null) {
            query.setParameter("idUtente", idUtente);
        }

        if (StringUtils.isNotBlank(tipoCreazione)) {
            query.setParameter("tiCreazione", tipoCreazione);
        }

        query.setMaxResults(maxResult);

        return lazyListHelper.getTableBean(query, list -> getMonVLisUdVersTableBeanFrom(filtri, list));
    }

    private MonVLisUdVersTableBean getMonVLisUdVersTableBeanFrom(MonitoraggioFiltriListaDocBean filtri,
            List<?> listaDoc) {
        MonVLisUdVersTableBean monTableBean = new MonVLisUdVersTableBean();

        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (int index = 0; index < listaDoc.size(); index++) {
                    Object rec = listaDoc.get(index);
                    BaseRowInterface row = Transform.entity2RowBean(rec);
                    /*
                     * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche con
                     * ambiente ed ente
                     */
                    if (filtri.getIdEnte() == null) {
                        String nmEnte = row.getString("nm_ente");
                        String nmStrut = row.getString("nm_strut");
                        String editNmStrut = StringUtils.isNotBlank(nmEnte)
                                ? nmEnte + (StringUtils.isNotBlank(nmStrut) ? ", " + nmStrut : "")
                                : (StringUtils.isNotBlank(nmStrut) ? nmStrut : "");
                        row.setString("nm_strut", editNmStrut);
                    }
                    String tiDoc = row.getString("ti_doc");
                    if (StringUtils.isNotBlank(tiDoc) && !tiDoc.equals("PRINCIPALE")) {
                        row.setString("ti_doc", tiDoc + " " + row.getBigDecimal("pg_doc").toPlainString());
                    }
                    monTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return monTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Lista Versamenti Falliti
     *
     * @param filtriSes
     *            i filtri di ricerca riportati dalla pagina precedente
     * @param maxResult
     *            il massimo numero di elementi da visualizzare nella lista
     *
     * @return MonVLisVersErrTableBean entity bean MonVLisVersErr
     *
     */
    public MonVLisVersErrIamTableBean getMonVLisVersErrIamViewBean(MonitoraggioFiltriListaVersFallitiBean filtriSes,
            int maxResult) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisVersErrIam u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriSes.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriSes.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriSes.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro tipo doc (PRINCIPALE = 1, tutti i
        // documenti = 0
        String tipoSes = filtriSes.getTipoSes();
        if (tipoSes != null) {
            queryStr.append(whereWord).append("u.tiSessioneVers = :tipoSes ");
            whereWord = "AND ";
        }

        String flRisolto = filtriSes.getRisolto();
        if (flRisolto != null) {
            queryStr.append(whereWord).append("u.flRisolto = :flRisolto ");
            whereWord = "AND ";
        }

        // GESTIONE PERIODO - GIORNO
        Calendar dataDBa = Calendar.getInstance();
        Calendar dataDBda = Calendar.getInstance();
        dataDBda.set(Calendar.HOUR_OF_DAY, 0);
        dataDBda.set(Calendar.MINUTE, 0);
        dataDBda.set(Calendar.SECOND, 0);
        dataDBda.set(Calendar.MILLISECOND, 0);
        dataDBa.set(Calendar.HOUR_OF_DAY, 23);
        dataDBa.set(Calendar.MINUTE, 59);
        dataDBa.set(Calendar.SECOND, 59);
        dataDBa.set(Calendar.MILLISECOND, 999);

        // Inserimento nella query del filtro periodo versamento
        String periodoVers = filtriSes.getPeriodoVers();
        if (periodoVers != null) {
            if (periodoVers.equals("ULTIMI7")) {
                dataDBda.add(Calendar.DATE, -6);
                queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
            } else if (periodoVers.equals("OGGI")) {
                queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
            } else {
                queryStr.append(whereWord).append("u.dtChiusura < :dataa ");
            }
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno
        // versamento
        Date dataOrarioDa = (filtriSes.getGiornoVersDaValidato() != null ? filtriSes.getGiornoVersDaValidato() : null);
        Date dataOrarioA = (filtriSes.getGiornoVersAValidato() != null ? filtriSes.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtChiusura between :datada AND :dataa ");
            whereWord = "AND ";
        }

        String flSessioneErrVerif = filtriSes.getVerificato();
        if (flSessioneErrVerif != null) {
            queryStr.append(whereWord).append("u.flSessioneErrVerif = :flSessioneErrVerif ");
            whereWord = "AND ";
        }

        String flSessioneErrNonRisolub = filtriSes.getNonRisolubile();
        if (flSessioneErrNonRisolub != null) {
            queryStr.append(whereWord).append("u.flSessioneErrNonRisolub = :flSessioneErrNonRisolub ");
            whereWord = "AND ";
        }

        // Gestione filtri codice errore
        String classeErrore = filtriSes.getClasseErrore() != null ? filtriSes.getClasseErrore().replace("_", "-")
                : null;
        String sottoClasseErrore = filtriSes.getSottoClasseErrore() != null
                ? filtriSes.getSottoClasseErrore().replace("_", "-") : null;
        String codiceErrore = filtriSes.getCodiceErrore() != null ? filtriSes.getCodiceErrore().replace("_", "-")
                : null;

        if (codiceErrore != null) {
            queryStr.append(whereWord).append("u.cdErr = :cdErr ");
            whereWord = "AND ";
        } else if (sottoClasseErrore != null || classeErrore != null) {
            queryStr.append(whereWord).append("u.cdErr LIKE :cdErr ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in
        // versione multiselect
        Set<String> registroSet = filtriSes.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN (:setregistro))");
            whereWord = " AND ";
        }

        BigDecimal anno = filtriSes.getAnno();
        String codice = filtriSes.getNumero();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC range con registro in
        // versione multiselect
        Set<String> registroRangeList = filtriSes.getRegistro_range();
        if (registroRangeList != null && !registroRangeList.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN (:listaregistro))");
            whereWord = " AND ";
        }

        BigDecimal annoRangeDa = filtriSes.getAnno_range_da();
        BigDecimal annoRangeA = filtriSes.getAnno_range_a();
        String numeroRangeDa = filtriSes.getNumero_range_da();
        String numeroRangeA = filtriSes.getNumero_range_a();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (numeroRangeDa != null && numeroRangeA != null) {
            numeroRangeDa = StringPadding.padString(numeroRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            numeroRangeA = StringPadding.padString(numeroRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        BigDecimal idUserIam = filtriSes.getIdUserIam();
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        }

        // ordina per data chiusura descrescente
        queryStr.append("ORDER BY u.dtChiusura DESC");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (tipoSes != null) {
            query.setParameter("tipoSes", tipoSes);
        }

        if (flRisolto != null) {
            query.setParameter("flRisolto", flRisolto);
        }

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        if (flSessioneErrVerif != null) {
            query.setParameter("flSessioneErrVerif", flSessioneErrVerif);
        }

        if (flSessioneErrNonRisolub != null) {
            query.setParameter("flSessioneErrNonRisolub", flSessioneErrNonRisolub);
        }

        if (codiceErrore != null) {
            query.setParameter("cdErr", codiceErrore);
        } else if (sottoClasseErrore != null) {
            query.setParameter("cdErr", sottoClasseErrore + '%');
        } else if (classeErrore != null) {
            query.setParameter("cdErr", classeErrore + '%');
        }

        if (registroSet != null && !registroSet.isEmpty()) {
            query.setParameter("setregistro", registroSet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (registroRangeList != null && !registroRangeList.isEmpty()) {
            query.setParameter("listaregistro", registroRangeList);
        }

        if (annoRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annoRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }

        if (numeroRangeDa != null && numeroRangeA != null) {
            query.setParameter("codicein_da", numeroRangeDa);
            query.setParameter("codicein_a", numeroRangeA);
        }

        if (idUserIam != null) {
            query.setParameter("idUserIam", idUserIam);
        }

        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisVersErrIam> listaVersErr = query.getResultList();

        MonVLisVersErrIamTableBean monTableBean = new MonVLisVersErrIamTableBean();

        try {
            if (listaVersErr != null && !listaVersErr.isEmpty()) {
                monTableBean = (MonVLisVersErrIamTableBean) Transform.entities2TableBean(listaVersErr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche con ambiente ed ente
         */
        for (MonVLisVersErrIamRowBean row : monTableBean) {
            if (filtriSes.getIdEnte() == null) {
                row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
                        + (row.getNmStrut() != null ? row.getNmStrut() : ""));
            }
        }
        return monTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Esame Operazioni Volumi
     *
     * @param filtriOV
     *            i filtri di ricerca
     * @param dateValidate
     *            filtro data e ora già validato
     * @param maxResult
     *            numero di record da visualizzare alla volta
     *
     * @return entity bean MonVLisOperVolTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVLisOperVolIamTableBean getMonVLisOperVolIamViewBean(FiltriOperazioniVolumi filtriOV, Date[] dateValidate,
            int maxResult) throws EMFError {
        final Date dataOrarioDa = dateValidate != null ? dateValidate[0] : null;
        final Date dataOrarioA = dateValidate != null ? dateValidate[1] : null;
        return getMonVLisOperVolIamViewBeanPlainFilters(maxResult,
                new FiltriOperazioniVolumiPlain(filtriOV, dataOrarioDa, dataOrarioA));
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Esame Operazioni Volumi
     *
     * @param maxResult
     *            numero massimo di risultati
     * @param filtriOV
     *            filtri per la ricerca di Operazioni Volumi
     *
     * @return MonVLisOperVolTableBean il table bean da usare nella UI
     */
    public MonVLisOperVolIamTableBean getMonVLisOperVolIamViewBeanPlainFilters(int maxResult,
            FiltriOperazioniVolumiPlain filtriOV) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisOperVolIam u ");

        // Inserimento nella query del filtro id ambiente
        final BigDecimal idAmbiente = filtriOV.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        final BigDecimal idEnte = filtriOV.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        final BigDecimal idStrut = filtriOV.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        final Date dataOrarioDa = filtriOV.getDataOrarioDa();
        final Date dataOrarioA = filtriOV.getDataOrarioA();

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        final String tiModOper = filtriOV.getTiModOper();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        final BigDecimal idVolumeConserv = filtriOV.getIdVolumeConserv();
        if (idVolumeConserv != null) {
            queryStr.append(whereWord).append("u.idVolumeConserv = :idVolumeConserv ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        final String flCreaVolume = filtriOV.getFlCreaVolume();
        final String flRecuperaVolumeAperto = filtriOV.getFlRecuperaVolumeAperto();
        final String flAggiungiDocVolume = filtriOV.getFlAggiungiDocVolume();
        final String flRecuperaVolumeScaduto = filtriOV.getFlRecuperaVolumeScaduto();
        final String flSetVolumeDaChiudere = filtriOV.getFlSetVolumeDaChiudere();
        final String flSetVolumeAperto = filtriOV.getFlSetVolumeAperto();
        final String flInizioCreaIndice = filtriOV.getFlInizioCreaIndice();
        final String flRecuperaVolumeInErrore = filtriOV.getFlRecuperaVolumeInErrore();
        final String flCreaIndiceVolume = filtriOV.getFlCreaIndiceVolume();
        final String flMarcaIndiceVolume = filtriOV.getFlMarcaIndiceVolume();
        final String flSetVolumeInErrore = filtriOV.getFlSetVolumeInErrore();
        final String flInizioVerifFirme = filtriOV.getFlInizioVerifFirme();
        final String flChiusuraVolume = filtriOV.getFlChiusuraVolume();
        final String flErrVerifFirme = filtriOV.getFlErrVerifFirme();
        final String flRimuoviDocVolume = filtriOV.getFlRimuoviDocVolume();
        final String flEliminaVolume = filtriOV.getFlEliminaVolume();
        final String flModificaVolume = filtriOV.getFlModificaVolume();
        final String flFirmaNoMarcaVolume = filtriOV.getFlFirmaNoMarcaVolume();
        final String flFirmaVolume = filtriOV.getFlFirmaVolume();

        String endWW = "";
        if (flCreaVolume.equals("1") || flRecuperaVolumeAperto.equals("1") || flAggiungiDocVolume.equals("1")
                || flRecuperaVolumeScaduto.equals("1") || flSetVolumeDaChiudere.equals("1")
                || flSetVolumeAperto.equals("1") || flInizioCreaIndice.equals("1")
                || flRecuperaVolumeInErrore.equals("1") || flCreaIndiceVolume.equals("1")
                || flMarcaIndiceVolume.equals("1") || flSetVolumeInErrore.equals("1") || flInizioVerifFirme.equals("1")
                || flChiusuraVolume.equals("1") || flErrVerifFirme.equals("1") || flRimuoviDocVolume.equals("1")
                || flEliminaVolume.equals("1") || flModificaVolume.equals("1") || flFirmaNoMarcaVolume.equals("1")
                || flFirmaVolume.equals("1")) {
            whereWord = "AND (";
            endWW = ")";
        } else {
            // setto il valore '' per fare in modo che se non ho settato nessun flag
            // non trovi nulla
            queryStr.append(whereWord).append("u.tiOper = '' ");
        }

        if (flCreaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeAperto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_APERTO' ");
            whereWord = "OR ";
        }
        if (flAggiungiDocVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'AGGIUNGI_DOC_VOLUME' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeScaduto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_SCADUTO' ");
            whereWord = "OR ";
        }
        if (flSetVolumeDaChiudere.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_DA_CHIUDERE' ");
            whereWord = "OR ";
        }
        if (flSetVolumeAperto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_APERTO' ");
            whereWord = "OR ";
        }
        if (flInizioCreaIndice.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'INIZIO_CREA_INDICE' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeInErrore.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_IN_ERRORE' ");
            whereWord = "OR ";
        }
        if (flCreaIndiceVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_INDICE_VOLUME' ");
            whereWord = "OR ";
        }
        if (flMarcaIndiceVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_INDICE_VOLUME' ");
            whereWord = "OR ";
        }
        if (flSetVolumeInErrore.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_IN_ERRORE' ");
            whereWord = "OR ";
        }
        if (flInizioVerifFirme.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'INIZIO_VERIF_FIRME' ");
            whereWord = "OR ";
        }
        if (flChiusuraVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CHIUSURA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flErrVerifFirme.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'ERR_VERIF_FIRME' ");
            whereWord = "OR ";
        }
        if (flRimuoviDocVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_DOC_VOLUME' ");
            whereWord = "OR ";
        }
        if (flEliminaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'ELIMINA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flModificaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'MODIFICA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flFirmaNoMarcaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_NO_MARCA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flFirmaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_VOLUME' ");
        }

        if (filtriOV.getTiOutput().equals("ANALITICO")) {
            // ordina per tipo operazione e data operazione
            queryStr.append(endWW).append(" ORDER BY u.tiOper, u.dtOper ");
        } else if (filtriOV.getTiOutput().equals("CRONOLOGICO")) {
            // ordina per data operazione
            queryStr.append(endWW).append("ORDER BY u.dtOper ");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        if (tiModOper != null) {
            query.setParameter("tiModOper", tiModOper);
        }

        if (idVolumeConserv != null) {
            query.setParameter("idVolumeConserv", idVolumeConserv);
        }

        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return lazyListHelper.getTableBean(query, list -> getMonVLisOperVolIamTableBeanFrom(idEnte, list));
    }

    private MonVLisOperVolIamTableBean getMonVLisOperVolIamTableBeanFrom(BigDecimal idEnte,
            List<MonVLisOperVolIam> listaOperVol) {
        MonVLisOperVolIamTableBean monTableBean = new MonVLisOperVolIamTableBean();

        try {
            if (listaOperVol != null && !listaOperVol.isEmpty()) {
                monTableBean = (MonVLisOperVolIamTableBean) Transform.entities2TableBean(listaOperVol);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Setto in un unico campo del tablebean il nome e cognome del firmatario
        for (MonVLisOperVolIamRowBean row : monTableBean) {
            row.setString("firmatario", row.getNmCognomeFirmatario() + " " + row.getNmNomeFirmatario());

            /*
             * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche con ambiente ed
             * ente
             */
            if (idEnte == null) {
                row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
                        + (row.getNmStrut() != null ? row.getNmStrut() : ""));
            }
        }

        return monTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Esame Operazioni Elenchi di Versamento
     *
     * @param filtriOE
     *            i filtri di ricerca
     * @param dateValidate
     *            filtro data e ora già validato
     *
     * @return entity bean MonVLisOperVolTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public ElvVLisLogOperTableBean getElvVLisLogOperViewBean(
            MonitoraggioForm.FiltriOperazioniElenchiVersamento filtriOE, Date[] dateValidate) throws EMFError {
        final Date dataOrarioDa = dateValidate != null ? dateValidate[0] : null;
        final Date dataOrarioA = dateValidate != null ? dateValidate[1] : null;
        return getElvVLisLogOperViewBean(
                new FiltriOperazioniElenchiVersamentoPlain(filtriOE, dataOrarioDa, dataOrarioA));
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Esame Operazioni Elenchi di Versamento
     *
     * @param filtriOE
     *            i filtri di ricerca
     *
     * @return MonVLisOperVolTableBean il table bean da usare nella UI
     */
    public ElvVLisLogOperTableBean getElvVLisLogOperViewBean(FiltriOperazioniElenchiVersamentoPlain filtriOE) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM ElvVLisLogOper u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOE.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOE.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOE.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date dataOrarioDa = filtriOE.getDataOrarioDa();
        Date dataOrarioA = filtriOE.getDataOrarioA();

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.tmOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOE.getTiModOper();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        BigDecimal idElencoVers = filtriOE.getIdElencoVers();
        if (idElencoVers != null) {
            queryStr.append(whereWord).append("u.idElencoVers = :idElencoVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        boolean flChiusuraElenco = filtriOE.isFlChiusuraElenco();
        boolean flCreaElenco = filtriOE.isFlCreaElenco();
        boolean flCreaIndiceElenco = filtriOE.isFlCreaIndiceElenco();
        boolean flDefNoteElencoChiuso = filtriOE.isFlDefNoteElencoChiuso();
        boolean flDefNoteIndiceElenco = filtriOE.isFlDefNoteIndiceElenco();
        boolean flEliminaElenco = filtriOE.isFlEliminaElenco();
        boolean flFirmaElenco = filtriOE.isFlFirmaElenco();
        boolean flModElenco = filtriOE.isFlModElenco();
        boolean flRecuperaElencoAperto = filtriOE.isFlRecuperaElencoAperto();
        boolean flRecuperaElencoScaduto = filtriOE.isFlRecuperaElencoScaduto();
        boolean flRimuoviDocElenco = filtriOE.isFlRimuoviDocElenco();
        boolean flRimuoviUdElenco = filtriOE.isFlRimuoviUdElenco();
        boolean flSetElencoAperto = filtriOE.isFlSetElencoAperto();
        boolean flSetElencoDaChiudere = filtriOE.isFlSetElencoDaChiudere();
        boolean flFirmaInCorso = filtriOE.isFlFirmaInCorso();
        boolean flFirmaInCorsoFallita = filtriOE.isFlFirmaInCorsoFallita();
        boolean flStartCreazioneElencoAip = filtriOE.isFlStartCreazioneElencoAip();
        boolean flEndCreazioneElencoAip = filtriOE.isFlEndCreazioneElencoAip();
        boolean flFirmaElencoAip = filtriOE.isFlFirmaElencoAip();
        boolean flFirmaElencoAipInCorso = filtriOE.isFlFirmaElencoAipInCorso();
        boolean flFirmaElencoAipFallita = filtriOE.isFlFirmaElencoAipFallita();
        boolean flMarcaElencoAip = filtriOE.isFlMarcaElencoAip();
        boolean flMarcaElencoAipFallita = filtriOE.isFlMarcaElencoAipFallita();

        String endWW = "";
        if (flChiusuraElenco || flCreaElenco || flCreaIndiceElenco || flDefNoteElencoChiuso || flDefNoteIndiceElenco
                || flEliminaElenco || flFirmaElenco || flModElenco || flRecuperaElencoAperto || flRecuperaElencoScaduto
                || flRimuoviDocElenco || flRimuoviUdElenco || flSetElencoAperto || flSetElencoDaChiudere
                || flFirmaInCorso || flFirmaInCorsoFallita || flStartCreazioneElencoAip || flEndCreazioneElencoAip
                || flFirmaElencoAip || flFirmaElencoAipInCorso || flFirmaElencoAipFallita || flMarcaElencoAip
                || flMarcaElencoAipFallita) {
            whereWord = "AND (";
            endWW = ")";
        } else {
            // setto il valore '' per fare in modo che se non ho settato nessun flag
            // non trovi nulla
            queryStr.append(whereWord).append("u.tiOper = '' ");
        }

        if (flChiusuraElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CHIUSURA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flCreaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flCreaIndiceElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_INDICE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flDefNoteElencoChiuso) {
            queryStr.append(whereWord).append("u.tiOper = 'DEF_NOTE_ELENCO_CHIUSO' ");
            whereWord = "OR ";
        }
        if (flDefNoteIndiceElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'DEF_NOTE_INDICE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flEliminaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'ELIMINA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flFirmaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'VALIDAZIONE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flModElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'MOD_ELENCO' ");
            whereWord = "OR ";
        }
        if (flRecuperaElencoAperto) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_ELENCO_APERTO' ");
            whereWord = "OR ";
        }
        if (flRecuperaElencoScaduto) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_ELENCO_SCADUTO' ");
            whereWord = "OR ";
        }
        if (flRimuoviDocElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_DOC_ELENCO' ");
            whereWord = "OR ";
        }
        if (flRimuoviUdElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_UD_ELENCO' ");
            whereWord = "OR ";
        }
        if (flSetElencoAperto) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_ELENCO_APERTO' ");
            whereWord = "OR ";
        }
        if (flSetElencoDaChiudere) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_ELENCO_DA_CHIUDERE' ");
            whereWord = "OR ";
        }
        if (flFirmaInCorso) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_IN_CORSO' ");
            whereWord = "OR ";
        }
        if (flFirmaInCorsoFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_IN_CORSO_FALLITA' ");
            whereWord = "OR ";
        }
        if (flStartCreazioneElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'START_CREA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flEndCreazioneElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'END_CREA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAipInCorso) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP_IN_CORSO' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAipFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP_FALLITA' ");
            whereWord = "OR ";
        }
        if (flMarcaElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flMarcaElencoAipFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_ELENCO_INDICI_AIP_FALLITA' ");
        }

        final String tiOutput = filtriOE.getTiOutput();
        if (tiOutput.equals("ANALITICO")) {
            // ordina per tipo operazione e data operazione
            queryStr.append(endWW).append(" ORDER BY u.tiOper, u.tmOper ");
        } else if (tiOutput.equals("CRONOLOGICO")) {
            // ordina per data operazione
            queryStr.append(endWW).append("ORDER BY u.tmOper ");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa);
            query.setParameter("dataa", dataOrarioA);
        }

        if (tiModOper != null) {
            query.setParameter("tiModOper", tiModOper);
        }

        if (idElencoVers != null) {
            query.setParameter("idElencoVers", idElencoVers);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<ElvVLisLogOperTableBean> listaOperElenchi = query.getResultList();

        ElvVLisLogOperTableBean monTableBean = new ElvVLisLogOperTableBean();
        try {
            if (listaOperElenchi != null && !listaOperElenchi.isEmpty()) {
                monTableBean = (ElvVLisLogOperTableBean) Transform.entities2TableBean(listaOperElenchi);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        for (ElvVLisLogOperRowBean row : monTableBean) {
            /* Setto in un unico campo del tablebean il nome e cognome del validatore */
            row.setString("validatore", row.getNmCognomeFirmatario() + " " + row.getNmNomeFirmatario());

            /*
             * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche con ambiente ed
             * ente
             */
            if (filtriOE.getIdEnte() == null) {
                row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
                        + (row.getNmStrut() != null ? row.getNmStrut() : ""));
            }
        }
        return monTableBean;
    }

    /**
     * Recupera una lista con i dati da visualizzare nella pagina Esame Operazioni Volumi per l'output di tipo aggregato
     *
     * @param filtriOV
     *            i filtri di ricerca
     * @param dateValidate
     *            filtro data e ora già validato
     *
     * @return la lista contenente tipo operatore e quantità
     *
     * @throws EMFError
     *             errore generico
     */
    public BaseTable getMonVLisOperVolOutputAggregato(FiltriOperazioniVolumi filtriOV, Date[] dateValidate)
            throws EMFError {
        final Date dataOrarioDa = (dateValidate != null ? dateValidate[0] : null);
        final Date dataOrarioA = (dateValidate != null ? dateValidate[1] : null);
        return getMonVLisOperVolOutputAggregato(new FiltriOperazioniVolumiPlain(filtriOV, dataOrarioDa, dataOrarioA));
    }

    public BaseTable getMonVLisOperVolOutputAggregato(FiltriOperazioniVolumiPlain filtriOV) {
        StringBuilder queryStr = new StringBuilder("SELECT u.tiOper, count(u) FROM MonVLisOperVolIam u ");
        String whereWord = "WHERE ";

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOV.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOV.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOV.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date dataOrarioDa = filtriOV.getDataOrarioDa();
        Date dataOrarioA = filtriOV.getDataOrarioA();
        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOV.getTiModOper();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        String flCreaVolume = filtriOV.getFlCreaVolume();
        String flRecuperaVolumeAperto = filtriOV.getFlRecuperaVolumeAperto();
        String flAggiungiDocVolume = filtriOV.getFlAggiungiDocVolume();
        String flRecuperaVolumeScaduto = filtriOV.getFlRecuperaVolumeScaduto();
        String flSetVolumeDaChiudere = filtriOV.getFlSetVolumeDaChiudere();
        String flSetVolumeAperto = filtriOV.getFlSetVolumeAperto();
        String flInizioCreaIndice = filtriOV.getFlInizioCreaIndice();
        String flRecuperaVolumeInErrore = filtriOV.getFlRecuperaVolumeInErrore();
        String flCreaIndiceVolume = filtriOV.getFlCreaIndiceVolume();
        String flMarcaIndiceVolume = filtriOV.getFlMarcaIndiceVolume();
        String flSetVolumeInErrore = filtriOV.getFlSetVolumeInErrore();
        String flInizioVerifFirme = filtriOV.getFlInizioVerifFirme();
        String flChiusuraVolume = filtriOV.getFlChiusuraVolume();
        String flErrVerifFirme = filtriOV.getFlErrVerifFirme();
        String flRimuoviDocVolume = filtriOV.getFlRimuoviDocVolume();
        String flEliminaVolume = filtriOV.getFlEliminaVolume();
        String flModificaVolume = filtriOV.getFlModificaVolume();
        String flFirmaNoMarcaVolume = filtriOV.getFlFirmaNoMarcaVolume();
        String flFirmaVolume = filtriOV.getFlFirmaVolume();

        String endWW = "";
        if (flCreaVolume.equals("1") || flRecuperaVolumeAperto.equals("1") || flAggiungiDocVolume.equals("1")
                || flRecuperaVolumeScaduto.equals("1") || flSetVolumeDaChiudere.equals("1")
                || flSetVolumeAperto.equals("1") || flInizioCreaIndice.equals("1")
                || flRecuperaVolumeInErrore.equals("1") || flCreaIndiceVolume.equals("1")
                || flMarcaIndiceVolume.equals("1") || flSetVolumeInErrore.equals("1") || flInizioVerifFirme.equals("1")
                || flChiusuraVolume.equals("1") || flErrVerifFirme.equals("1") || flRimuoviDocVolume.equals("1")
                || flEliminaVolume.equals("1") || flModificaVolume.equals("1") || flFirmaNoMarcaVolume.equals("1")
                || flFirmaVolume.equals("1")) {
            whereWord = "AND (";
            endWW = ")";
        } else {
            /*
             * setto il valore '' per fare in modo che se non ho settato nessun flag non trovi nulla
             */
            queryStr.append(whereWord).append("u.tiOper = '' ");
        }

        if (flCreaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeAperto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_APERTO' ");
            whereWord = "OR ";
        }
        if (flAggiungiDocVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'AGGIUNGI_DOC_VOLUME' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeScaduto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_SCADUTO' ");
            whereWord = "OR ";
        }
        if (flSetVolumeDaChiudere.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_DA_CHIUDERE' ");
            whereWord = "OR ";
        }
        if (flSetVolumeAperto.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_APERTO' ");
            whereWord = "OR ";
        }
        if (flInizioCreaIndice.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'INIZIO_CREA_INDICE' ");
            whereWord = "OR ";
        }
        if (flRecuperaVolumeInErrore.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_VOLUME_IN_ERRORE' ");
            whereWord = "OR ";
        }
        if (flCreaIndiceVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_INDICE_VOLUME' ");
            whereWord = "OR ";
        }
        if (flMarcaIndiceVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_INDICE_VOLUME' ");
            whereWord = "OR ";
        }
        if (flSetVolumeInErrore.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_VOLUME_IN_ERRORE' ");
            whereWord = "OR ";
        }
        if (flInizioVerifFirme.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'INIZIO_VERIF_FIRME' ");
            whereWord = "OR ";
        }
        if (flChiusuraVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'CHIUSURA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flErrVerifFirme.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'ERR_VERIF_FIRME' ");
            whereWord = "OR ";
        }
        if (flRimuoviDocVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_DOC_VOLUME' ");
            whereWord = "OR ";
        }
        if (flEliminaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'ELIMINA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flModificaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'MODIFICA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flFirmaNoMarcaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_NO_MARCA_VOLUME' ");
            whereWord = "OR ";
        }
        if (flFirmaVolume.equals("1")) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_VOLUME' ");
        }

        queryStr.append(endWW).append("GROUP BY u.tiOper ORDER BY u.tiOper ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        if (tiModOper != null) {
            query.setParameter("tiModOper", tiModOper);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<Object[]> listaOperVol = query.getResultList();

        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();
        // Elaboro i totali
        for (int i = 0; i < listaOperVol.size(); i++) {
            Object[] obj = listaOperVol.get(i);
            riga.setString("ti_oper", (String) obj[0]);
            riga.setString("ni_oper", "" + obj[1]);
            tabella.add(riga);
        } // tabella

        return tabella;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Esame Operazioni Elenchi di Versamento
     *
     * @param filtriOE
     *            i filtri di ricerca
     * @param dateValidate
     *            filtro data e ora già validato
     *
     * @return entity bean MonVLisOperVolTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public BaseTable getElvVLisLogOperOutputAggregato(MonitoraggioForm.FiltriOperazioniElenchiVersamento filtriOE,
            Date[] dateValidate) throws EMFError {
        Date dataOrarioDa = (dateValidate != null ? dateValidate[0] : null);
        Date dataOrarioA = (dateValidate != null ? dateValidate[1] : null);
        return getElvVLisLogOperOutputAggregato(
                new FiltriOperazioniElenchiVersamentoPlain(filtriOE, dataOrarioDa, dataOrarioA));
    }

    public BaseTable getElvVLisLogOperOutputAggregato(FiltriOperazioniElenchiVersamentoPlain filtriOE) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u.tiOper, count(u) FROM ElvVLisLogOper u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOE.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOE.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOE.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date dataOrarioDa = filtriOE.getDataOrarioDa();
        Date dataOrarioA = filtriOE.getDataOrarioA();

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.tmOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOE.getTiModOper();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        BigDecimal idElencoVers = filtriOE.getIdElencoVers();
        if (idElencoVers != null) {
            queryStr.append(whereWord).append("u.idElencoVers = :idElencoVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        boolean flChiusuraElenco = filtriOE.isFlChiusuraElenco();
        boolean flCreaElenco = filtriOE.isFlCreaElenco();
        boolean flCreaIndiceElenco = filtriOE.isFlCreaIndiceElenco();
        boolean flDefNoteElencoChiuso = filtriOE.isFlDefNoteElencoChiuso();
        boolean flDefNoteIndiceElenco = filtriOE.isFlDefNoteIndiceElenco();
        boolean flEliminaElenco = filtriOE.isFlEliminaElenco();
        boolean flFirmaElenco = filtriOE.isFlFirmaElenco();
        boolean flModElenco = filtriOE.isFlModElenco();
        boolean flRecuperaElencoAperto = filtriOE.isFlRecuperaElencoAperto();
        boolean flRecuperaElencoScaduto = filtriOE.isFlRecuperaElencoScaduto();
        boolean flRimuoviDocElenco = filtriOE.isFlRimuoviDocElenco();
        boolean flRimuoviUdElenco = filtriOE.isFlRimuoviUdElenco();
        boolean flSetElencoAperto = filtriOE.isFlSetElencoAperto();
        boolean flSetElencoDaChiudere = filtriOE.isFlSetElencoDaChiudere();
        boolean flFirmaInCorso = filtriOE.isFlFirmaInCorso();
        boolean flFirmaInCorsoFallita = filtriOE.isFlFirmaInCorsoFallita();
        boolean flStartCreazioneElencoAip = filtriOE.isFlStartCreazioneElencoAip();
        boolean flEndCreazioneElencoAip = filtriOE.isFlEndCreazioneElencoAip();
        boolean flFirmaElencoAip = filtriOE.isFlFirmaElencoAip();
        boolean flFirmaElencoAipInCorso = filtriOE.isFlFirmaElencoAipInCorso();
        boolean flFirmaElencoAipFallita = filtriOE.isFlFirmaElencoAipFallita();
        boolean flMarcaElencoAip = filtriOE.isFlMarcaElencoAip();
        boolean flMarcaElencoAipFallita = filtriOE.isFlMarcaElencoAipFallita();

        String endWW = "";
        if (flChiusuraElenco || flCreaElenco || flCreaIndiceElenco || flDefNoteElencoChiuso || flDefNoteIndiceElenco
                || flEliminaElenco || flFirmaElenco || flModElenco || flRecuperaElencoAperto || flRecuperaElencoScaduto
                || flRimuoviDocElenco || flRimuoviUdElenco || flSetElencoAperto || flSetElencoDaChiudere
                || flFirmaInCorso || flFirmaInCorsoFallita || flStartCreazioneElencoAip || flEndCreazioneElencoAip
                || flFirmaElencoAip || flFirmaElencoAipInCorso || flFirmaElencoAipFallita || flMarcaElencoAip
                || flMarcaElencoAipFallita) {
            whereWord = "AND (";
            endWW = ")";
        } else {
            // setto il valore '' per fare in modo che se non ho settato nessun flag
            // non trovi nulla
            queryStr.append(whereWord).append("u.tiOper = '' ");
        }

        if (flChiusuraElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CHIUSURA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flCreaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flCreaIndiceElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'CREA_INDICE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flDefNoteElencoChiuso) {
            queryStr.append(whereWord).append("u.tiOper = 'DEF_NOTE_ELENCO_CHIUSO' ");
            whereWord = "OR ";
        }
        if (flDefNoteIndiceElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'DEF_NOTE_INDICE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flEliminaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'ELIMINA_ELENCO' ");
            whereWord = "OR ";
        }
        if (flFirmaElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'VALIDAZIONE_ELENCO' ");
            whereWord = "OR ";
        }
        if (flModElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'MOD_ELENCO' ");
            whereWord = "OR ";
        }
        if (flRecuperaElencoAperto) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_ELENCO_APERTO' ");
            whereWord = "OR ";
        }
        if (flRecuperaElencoScaduto) {
            queryStr.append(whereWord).append("u.tiOper = 'RECUPERA_ELENCO_SCADUTO' ");
            whereWord = "OR ";
        }
        if (flRimuoviDocElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_DOC_ELENCO' ");
            whereWord = "OR ";
        }
        if (flRimuoviUdElenco) {
            queryStr.append(whereWord).append("u.tiOper = 'RIMUOVI_UD_ELENCO' ");
            whereWord = "OR ";
        }
        if (flSetElencoAperto) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_ELENCO_APERTO' ");
            whereWord = "OR ";
        }
        if (flSetElencoDaChiudere) {
            queryStr.append(whereWord).append("u.tiOper = 'SET_ELENCO_DA_CHIUDERE' ");
            whereWord = "OR ";
        }
        if (flFirmaInCorso) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_IN_CORSO' ");
            whereWord = "OR ";
        }
        if (flFirmaInCorsoFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_IN_CORSO_FALLITA' ");
            whereWord = "OR ";
        }
        if (flStartCreazioneElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'START_CREA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flEndCreazioneElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'END_CREA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAipInCorso) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP_IN_CORSO' ");
            whereWord = "OR ";
        }
        if (flFirmaElencoAipFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'FIRMA_ELENCO_INDICI_AIP_FALLITA' ");
            whereWord = "OR ";
        }
        if (flMarcaElencoAip) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_ELENCO_INDICI_AIP' ");
            whereWord = "OR ";
        }
        if (flMarcaElencoAipFallita) {
            queryStr.append(whereWord).append("u.tiOper = 'MARCA_ELENCO_INDICI_AIP_FALLITA' ");
        }

        queryStr.append(endWW).append("GROUP BY u.tiOper ORDER BY u.tiOper ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa);
            query.setParameter("dataa", dataOrarioA);
        }

        if (tiModOper != null) {
            query.setParameter("tiModOper", tiModOper);
        }
        // idElencoVers
        if (idElencoVers != null) {
            query.setParameter("idElencoVers", idElencoVers);
        }

        List<Object[]> listaOperVol = query.getResultList();

        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();
        for (Object[] obj : listaOperVol) {
            riga.setString("ti_oper", (String) obj[0]);
            riga.setString("ni_oper", "" + obj[1]);
            tabella.add(riga);
        }
        return tabella;
    }

    /**
     * Restituisce la lista contenente i dati totali del contenuto SACER della pagina Esame Contenuto Sacer
     *
     * @param filtriCS
     *            bean filtro FiltriContenutoSacer
     * @param idAmbitoTerritList
     *            lista id ambienti territoriali
     * @param idUserIam
     *            id user Iam
     *
     * @return entity bean MonTotSacerRowBean
     *
     * @throws EMFError
     *             errore generico
     */
    public BaseTable getMonTotSacerTable(FiltriContenutoSacer filtriCS, List<BigDecimal> idAmbitoTerritList,
            long idUserIam) throws EMFError {
        return getMonTotSacerTable(idAmbitoTerritList, idUserIam, new FiltriContenutoSacerPlain(filtriCS));
    }

    /**
     * Restituisce la lista contenente i dati totali del contenuto SACER della pagina Esame Contenuto Sacer
     *
     * @param idAmbitoTerritList
     *            lista degli id di ambito territoriale
     * @param idUserIam
     *            id dello user
     * @param filtriCS
     *            filtri del contenuto Sacer
     *
     * @return MonTotSacerRowBean table bean da usare nella UI
     */
    public BaseTable getMonTotSacerTable(List<BigDecimal> idAmbitoTerritList, long idUserIam,
            FiltriContenutoSacerPlain filtriCS) {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT strut.orgEnte.nmEnte, strut.nmStrut, "
                + "subStrut.nmSubStrut, registroUnitaDoc.cdRegistroUnitaDoc, "
                + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, "
                + "tipoUnitaDoc.nmTipoUnitaDoc, tipoDoc.nmTipoDoc, "
                + "sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul), "
                + "sum(mon.niDocVers) - sum(mon.niDocAnnulUd), " + "sum(mon.niCompVers) - sum(mon.niCompAnnulUd), "
                + "sum(mon.niSizeVers) - sum(mon.niSizeAnnulUd), "
                + "sum(mon.niDocAgg), sum(mon.niCompAgg), sum(mon.niSizeAgg), "
                + "sum(mon.niUnitaDocAnnul), sum(mon.niDocAnnulUd), sum(mon.niCompAnnulUd), sum(mon.niSizeAnnulUd) "
                + "FROM MonVRicContaUdDocComp mon, " + "DecTipoUnitaDoc tipoUnitaDoc, "
                + "DecRegistroUnitaDoc registroUnitaDoc, " + "DecTipoDoc tipoDoc, " + "OrgSubStrut subStrut "
                + "JOIN subStrut.orgStrut strut " + "JOIN strut.orgEnte ente " + "JOIN ente.orgCategEnte categEnte "
                + "LEFT JOIN strut.orgCategStrut categStrut, "
                + "SIOrgEnteSiam enteConvenz, DecVTreeCategTipoUd tree, IamAbilOrganiz iao "
                + "WHERE iao.iamUser.idUserIam = :idUserIam " + "AND strut.idEnteConvenz = enteConvenz.idEnteSiam "
                + "AND tree.idCategTipoUnitaDoc = tipoUnitaDoc.decCategTipoUnitaDoc.idCategTipoUnitaDoc "
                + "AND iao.idOrganizApplic = strut.idStrut " + "AND mon.idTipoUnitaDoc = tipoUnitaDoc.idTipoUnitaDoc "
                + "AND mon.idSubStrut = subStrut.idSubStrut "
                + "AND mon.idRegistroUnitaDoc = registroUnitaDoc.idRegistroUnitaDoc "//
                + "AND mon.idTipoDocPrinc = tipoDoc.idTipoDoc ");

        // Inserimento nella query del filtro id ambiente
        List<BigDecimal> idAmbienteList = filtriCS.getIdAmbienteList();
        if (!idAmbienteList.isEmpty()) {
            queryStr.append(whereWord).append("strut.orgEnte.orgAmbiente.idAmbiente IN :idAmbienteList ");
        }
        // Inserimento nella query del filtro id ente
        List<BigDecimal> idEnteList = filtriCS.getIdEnteList();
        if (!idEnteList.isEmpty()) {
            queryStr.append(whereWord).append("strut.orgEnte.idEnte IN :idEnteList ");
        }
        // Inserimento nella query del filtro id strut
        List<BigDecimal> idStrutList = filtriCS.getIdStrutList();
        if (!idStrutList.isEmpty()) {
            queryStr.append(whereWord).append("strut.idStrut IN :idStrutList ");
        }
        // Inserimento nella query del filtro id sub strut
        List<BigDecimal> idSubStrutList = filtriCS.getIdSubStrutList();
        if (!idSubStrutList.isEmpty()) {
            queryStr.append(whereWord).append("subStrut.idSubStrut IN :idSubStrutList ");
        }
        // Inserimento nella query del filtro registro
        List<BigDecimal> idRegistroUnitaDocList = filtriCS.getIdRegistroUnitaDocList();
        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idRegistroUnitaDoc IN :idRegistroUnitaDocList ");
        }
        BigDecimal aaKeyUnitaDoc = filtriCS.getAaKeyUnitaDoc();
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("mon.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }
        // Inserimento nella query del filtro tipo unità documentaria
        List<BigDecimal> idTipoUnitaDocList = filtriCS.getIdTipoUnitaDocList();
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idTipoUnitaDoc IN :idTipoUnitaDocList ");
        }
        // Inserimento nella query del filtro tipo documento
        List<BigDecimal> idTipoDocList = filtriCS.getIdTipoDocList();
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idTipoDocPrinc IN :idTipoDocList ");
        }
        List<BigDecimal> idCategTipoUnitaDocList = filtriCS.getIdCategTipoUnitaDocList();
        List<BigDecimal> idSottocategTipoUnitaDocList = filtriCS.getIdSottocategTipoUnitaDocList();
        List<String> filtroCategoria = new ArrayList<>();
        int i = 0;
        for (BigDecimal idCateg : idCategTipoUnitaDocList) {
            i++;
            if (!idSottocategTipoUnitaDocList.isEmpty()) {
                for (BigDecimal idSotto : idSottocategTipoUnitaDocList) {
                    filtroCategoria.add("/" + idCateg + "/" + idSotto);
                }
            } else {
                filtroCategoria.add("/" + idCateg + "/%");
            }
        }

        if (!filtroCategoria.isEmpty()) {
            queryStr.append(whereWord).append("(");
            for (int j = 1; j <= filtroCategoria.size(); j++) {
                queryStr.append("tree.dlIdCategTipoUnitaDoc LIKE :filtro").append(j).append(" OR ");
            }
            queryStr.replace(queryStr.lastIndexOf(" OR "), queryStr.length(), ")");
        }

        // Inserimento nella query del filtro data
        Date dataRifDa = filtriCS.getDataRifDa();
        Date dataRifA = filtriCS.getDataRifA();
        if (dataRifDa != null && dataRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
        }
        // idAmbitoTerritList
        if (!idAmbitoTerritList.isEmpty()) {
            queryStr.append(whereWord).append("enteConvenz.idAmbitoTerrit IN :idAmbitoTerritList ");
        }

        // Inserimento nella query del filtro ambito categoria ente
        List<BigDecimal> idCategEnteList = filtriCS.getIdCategEnteList();
        if (!idCategEnteList.isEmpty()) {
            queryStr.append(whereWord).append("categEnte.idCategEnte IN :idCategEnteList ");
        }

        // Inserimento nella query del filtro ambito categoria struttura
        List<BigDecimal> idCategStrutList = filtriCS.getIdCategStrutList();
        if (!idCategStrutList.isEmpty()) {
            queryStr.append(whereWord).append("categStrut.idCategStrut IN :idCategStrutList ");
        }

        queryStr.append(
                "GROUP BY strut.orgEnte.nmEnte, strut.nmStrut, subStrut.nmSubStrut, registroUnitaDoc.cdRegistroUnitaDoc, "
                        + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, tipoUnitaDoc.nmTipoUnitaDoc, tipoDoc.nmTipoDoc "
                        + "ORDER BY strut.orgEnte.nmEnte, strut.nmStrut, subStrut.nmSubStrut, registroUnitaDoc.cdRegistroUnitaDoc, "
                        + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, tipoUnitaDoc.nmTipoUnitaDoc, tipoDoc.nmTipoDoc ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (!idAmbienteList.isEmpty()) {
            query.setParameter("idAmbienteList", GenericHelper.longListFrom(idAmbienteList));
        }

        if (!idEnteList.isEmpty()) {
            query.setParameter("idEnteList", GenericHelper.longListFrom(idEnteList));
        }

        if (!idStrutList.isEmpty()) {
            query.setParameter("idStrutList", GenericHelper.longListFrom(idStrutList));
        }

        if (!idSubStrutList.isEmpty()) {
            query.setParameter("idSubStrutList", GenericHelper.longListFrom(idSubStrutList));
        }

        if (!idRegistroUnitaDocList.isEmpty()) {
            query.setParameter("idRegistroUnitaDocList", idRegistroUnitaDocList);
        }

        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("idTipoDocList", idTipoDocList);
        }

        int j = 1;
        if (!filtroCategoria.isEmpty()) {
            for (String filtro : filtroCategoria) {
                query.setParameter("filtro" + j, filtro);
                j++;
            }
        }

        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        if (!idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", idAmbitoTerritList);
        }

        if (!idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", GenericHelper.longListFrom(idCategEnteList));
        }

        if (!idCategStrutList.isEmpty()) {
            query.setParameter("idCategStrutList", GenericHelper.longListFrom(idCategStrutList));
        }

        query.setParameter("idUserIam", idUserIam);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totSacer : listaTotSacer) {
            try {
                riga.setString("nm_ente", (String) totSacer[0]);
                riga.setString("nm_strut", (String) totSacer[1]);
                riga.setString("nm_sub_strut", (String) totSacer[2]);
                riga.setString("cd_registro_unita_doc", (String) totSacer[3]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totSacer[4]);
                riga.setString("cd_categ_tipo_unita_doc", (String) totSacer[5]);//
                riga.setString("nm_tipo_unita_doc", (String) totSacer[6]);
                riga.setString("nm_tipo_doc", (String) totSacer[7]);
                riga.setBigDecimal("ni_unita_doc_s", (BigDecimal) totSacer[8]);
                riga.setBigDecimal("ni_doc_s", (BigDecimal) totSacer[9]);
                riga.setBigDecimal("ni_comp_s", (BigDecimal) totSacer[10]);
                riga.setBigDecimal("ni_size_s", (BigDecimal) totSacer[11]);
                riga.setBigDecimal("ni_doc_a", (BigDecimal) totSacer[12]);
                riga.setBigDecimal("ni_comp_a", (BigDecimal) totSacer[13]);
                riga.setBigDecimal("ni_size_a", (BigDecimal) totSacer[14]);
                riga.setBigDecimal("ni_unita_doc_annull", (BigDecimal) totSacer[15]);
                riga.setBigDecimal("ni_doc_annull_ud", (BigDecimal) totSacer[16]);
                riga.setBigDecimal("ni_comp_annull_ud", (BigDecimal) totSacer[17]);
                riga.setBigDecimal("ni_size_annull_ud", (BigDecimal) totSacer[18]);
                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return tabella;
    }

    /**
     * Restituisce la lista contenente i dati totali del contenuto SACER per la Home
     *
     * @param idAmbiente
     *            id ambiente
     * @param idUserIam
     *            id user Iam
     * @param idEnte
     *            id ente
     * @param dtRifDa
     *            data riferimento da
     * @param idStrut
     *            id struttura
     * @param dtRifA
     *            data riferimento a
     *
     * @return entity bean MonTotSacerRowBean
     *
     */
    public BaseTable getMonTotSacerForHomeTable(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            Date dtRifDa, Date dtRifA, long idUserIam) {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT registroUnitaDoc.cdRegistroUnitaDoc, "
                + "mon.aaKeyUnitaDoc, tipoUnitaDoc.nmTipoUnitaDoc, "
                + "sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul), "
                + "sum(mon.niDocVers) - sum(mon.niDocAnnulUd), " + "sum(mon.niCompVers) - sum(mon.niCompAnnulUd), "
                + "sum(mon.niSizeVers) - sum(mon.niSizeAnnulUd), "
                + "sum(mon.niDocAgg), sum(mon.niCompAgg), sum(mon.niSizeAgg), " + "categ.cdCategTipoUnitaDoc "
                + "FROM MonVRicContaUdDocComp mon, OrgSubStrut subStrut " + "JOIN subStrut.orgStrut strut "
                + "LEFT JOIN strut.orgCategStrut categStrut, " + "DecTipoUnitaDoc tipoUnitaDoc "
                + "JOIN tipoUnitaDoc.decCategTipoUnitaDoc categ, IamAbilOrganiz iao, DecRegistroUnitaDoc registroUnitaDoc "
                + "WHERE iao.iamUser.idUserIam = :idUserIam " + "AND iao.idOrganizApplic = strut.idStrut "
                + "AND mon.idSubStrut = subStrut.idSubStrut " + "AND mon.idTipoUnitaDoc = tipoUnitaDoc.idTipoUnitaDoc "
                + "AND mon.idRegistroUnitaDoc = registroUnitaDoc.idRegistroUnitaDoc ");

        if (idAmbiente != null) {
            queryStr.append(whereWord).append("strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("strut.idStrut = :idStrut ");
        }
        if (dtRifDa != null && dtRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dtRifDa AND :dtRifA ");
        }

        queryStr.append(
                "GROUP BY registroUnitaDoc.cdRegistroUnitaDoc, mon.aaKeyUnitaDoc, tipoUnitaDoc.nmTipoUnitaDoc, categ.cdCategTipoUnitaDoc "
                        + "HAVING (sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul)) > 0 "
                        + "ORDER BY registroUnitaDoc.cdRegistroUnitaDoc, mon.aaKeyUnitaDoc, tipoUnitaDoc.nmTipoUnitaDoc, categ.cdCategTipoUnitaDoc ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }

        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }

        if (idStrut != null) {
            query.setParameter("idStrut", GenericHelper.longFromBigDecimal(idStrut));
        }

        if (dtRifDa != null && dtRifA != null) {
            query.setParameter("dtRifDa", dtRifDa);
            query.setParameter("dtRifA", dtRifA);
        }

        query.setParameter("idUserIam", idUserIam);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totSacer : listaTotSacer) {
            try {
                riga.setString("cd_registro_unita_doc", (String) totSacer[0]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totSacer[1]);
                riga.setString("nm_tipo_unita_doc", (String) totSacer[2]);
                riga.setBigDecimal("ni_unita_doc_s", (BigDecimal) totSacer[3]);
                riga.setBigDecimal("ni_doc_s", (BigDecimal) totSacer[4]);
                riga.setBigDecimal("ni_comp_s", (BigDecimal) totSacer[5]);
                riga.setBigDecimal("ni_size_s", (BigDecimal) totSacer[6]);
                riga.setBigDecimal("ni_doc_a", (BigDecimal) totSacer[7]);
                riga.setBigDecimal("ni_comp_a", (BigDecimal) totSacer[8]);
                riga.setBigDecimal("ni_size_a", (BigDecimal) totSacer[9]);
                riga.setString("cd_categ_tipo_unita_doc", (String) totSacer[10]);
                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return tabella;
    }

    /**
     * Totale dei documenti conservati da ParER alla data.
     *
     * Questa informazione viene esposta da un servizio RESTful pubblico. Verificare su <a href=
     * "https://poloarchivistico.regione.emilia-romagna.it/">https://poloarchivistico.regione.emilia-romagna.it</a>
     * sulla colonna di destra.
     *
     * @return Bean contentente la data ed il numero di documenti.
     *
     * @throws EMFError
     *             errore generico
     */
    public CounterResultBean getTotalMonTotSacer() throws EMFError {
        try {
            final String sql = "SELECT NEW it.eng.parer.web.dto.CounterResultBean(max (m.dtRifConta), sum(m.niDocVers) + sum(m.niDocAgg) - sum(m.niDocAnnulUd) ) from MonVRicContaUdDocComp m";
            TypedQuery<CounterResultBean> query = entityManager.createQuery(sql, CounterResultBean.class);
            return query.getSingleResult();
        } catch (Exception e) {
            log.error("Errore nel conteggio dei documenti alla data", e);
            throw new EMFError(EMFError.ERROR, e);
        }
    }

    /**
     * Salva nella tabella relativa alle sessioni di versamento le modifiche apportate nella pagina di Dettaglio
     * Versamento Fallito o Sessione Errata
     *
     * @param idSessioneVers
     *            id sessione versamento
     * @param flSessioneErrVerif
     *            flag 1/0 (true/false)
     * @param flSessioneErrNonRisolub
     *            flag 1/0 (true/false)
     * @param nmAmbientePerCalcolo
     *            nome ambiente
     * @param nmEntePerCalcolo
     *            nome ente
     * @param nmStrutPerCalcolo
     *            nome struttura
     * @param idStrutPerCalcolo
     *            id struttura
     * @param registroUD
     *            registro unita doc
     * @param annoUD
     *            anno unita doc
     * @param numUD
     *            numero unita doc
     * @param chiaveDoc
     *            chiave documento
     */
    public void salvaDettaglio(BigDecimal idSessioneVers, String flSessioneErrVerif, String flSessioneErrNonRisolub,
            String nmAmbientePerCalcolo, String nmEntePerCalcolo, String nmStrutPerCalcolo,
            BigDecimal idStrutPerCalcolo, String registroUD, BigDecimal annoUD, String numUD, String chiaveDoc) {
        VrsSessioneVersKo sessioneVers = getVrsSessioneVersKo(idSessioneVers);
        if (flSessioneErrVerif != null) {
            sessioneVers.setFlSessioneErrVerif(flSessioneErrVerif);
        }
        if (flSessioneErrNonRisolub != null) {
            sessioneVers.setFlSessioneErrNonRisolub(flSessioneErrNonRisolub);
        }
        // Se sto impostando i valori per passare da una sessione errata ad un
        // versamento fallito
        if (nmAmbientePerCalcolo != null) {
            sessioneVers.setNmAmbiente(nmAmbientePerCalcolo);
            sessioneVers.setNmEnte(nmEntePerCalcolo);
            sessioneVers.setNmStrut(nmStrutPerCalcolo);
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrutPerCalcolo.longValue());
            sessioneVers.setOrgStrut(strut);
        }
        sessioneVers.setCdRegistroKeyUnitaDoc(registroUD);
        sessioneVers.setAaKeyUnitaDoc(annoUD);
        sessioneVers.setCdKeyUnitaDoc(numUD);
        sessioneVers.setCdKeyDocVers(chiaveDoc);

        try {
            entityManager.merge(sessioneVers);
            entityManager.flush();
        } catch (RuntimeException re) {
            // logga l'errore e blocca tutto
            log.error("Eccezione nella persistenza del  ", re);
        }
    }

    public VrsSessioneVersKo getVrsSessioneVersKo(BigDecimal idSessioneVers) {
        return entityManager.find(VrsSessioneVersKo.class, idSessioneVers);
    }

    /**
     * Salva nella tabella relativa alle sessioni di versamento le modifiche apportate nella pagina al flag "verificato"
     * di Lista Versamenti Falliti o Sessioni Errate
     *
     * @param idSessioneVers
     *            id sessione versamento
     * @param flSessioneErrVerif
     *            flag 1/0 (true/false)
     */
    public void saveFlVerificati(BigDecimal idSessioneVers, String flSessioneErrVerif) {
        VrsSessioneVersKo sessioneVersKo = getVrsSessioneVersKo(idSessioneVers);
        sessioneVersKo.setFlSessioneErrVerif(flSessioneErrVerif);
        try {
            entityManager.merge(sessioneVersKo);
            entityManager.flush();
        } catch (RuntimeException re) {
            // logga l'errore e blocca tutto
            log.error("Eccezione nella persistenza del  ", re);
        }
    }

    /**
     * Salva nella tabella relativa alle sessioni di versamento le modifiche apportate nella pagina al flag "verificato"
     * e al flag "non risolubile" di Lista Versamenti Falliti
     *
     * @param idSessioneVers
     *            id sessione versamento
     * @param flSessioneErrVerif
     *            flag 1/0 (true/false)
     * @param flSessioneErrNonRisolub
     *            flag 1/0 (true/false)
     */
    public void saveFlVerificatiNonRisolubili(BigDecimal idSessioneVers, String flSessioneErrVerif,
            String flSessioneErrNonRisolub) {
        VrsSessioneVersKo rec = entityManager.getReference(VrsSessioneVersKo.class, idSessioneVers.longValue());
        if (flSessioneErrVerif != null) {
            rec.setFlSessioneErrVerif(flSessioneErrVerif);
        }
        rec.setFlSessioneErrNonRisolub(flSessioneErrNonRisolub);
    }

    /**
     * Restituisce il nome ambiente associato al suo identificativo in tabella
     *
     * @param idAmbiente
     *            id ambiente
     *
     * @return il nome ambiente
     */
    public String getNomeAmbienteFromId(BigDecimal idAmbiente) {
        OrgAmbiente amb = entityManager.find(OrgAmbiente.class, idAmbiente.longValue());
        return amb.getNmAmbiente();
    }

    /**
     * Restituisce il nome ente associato al suo identificativo in tabella
     *
     * @param idEnte
     *            id ente
     *
     * @return il nome ente
     */
    public String getNomeEnteFromId(BigDecimal idEnte) {
        OrgEnte ente = entityManager.find(OrgEnte.class, idEnte.longValue());
        return ente.getNmEnte();
    }

    /**
     * Restituisce il nome struttura associato al suo identificativo in tabella
     *
     * @param idStruttura
     *            id struttura
     *
     * @return il nome struttura
     */
    public String getNomeStrutturaFromId(BigDecimal idStruttura) {
        OrgStrut struttura = entityManager.find(OrgStrut.class, idStruttura.longValue());
        return struttura.getNmStrut();
    }

    /**
     * Restituisce il nome tipo unità documentaria associato al suo identificativo in tabella
     *
     * @param idTipoUD
     *            id tipo unita doc
     *
     * @return il nome tipo unità documentaria
     */
    public String getNomeTipoUDFromId(long idTipoUD) {
        DecTipoUnitaDoc ud = entityManager.find(DecTipoUnitaDoc.class, idTipoUD);
        return ud.getNmTipoUnitaDoc();
    }

    /**
     * Restituisce in un tablebean la lista delle Sessioni Errate dell'omonima pagina in base ai filtri di ricerca
     *
     * @param flVerificato
     *            l'unico filtro di ricerca utilizzato
     * @param maxResults
     *            numero massimo risultati
     *
     * @return bean entity VrsSessioneVersTableBean
     *
     */
    public VrsSessioneVersKoTableBean getSessioniErrateListTB(String flVerificato, int maxResults) {

        StringBuilder queryStr = new StringBuilder(
                "select v.idSessioneVers, v.dsErr, v.cdErr from VrsVLisSesErrate v ");

        // Inserimento nella query del filtro flVerificato
        if (flVerificato != null) {
            queryStr.append(" where v.flSessioneErrVerif = :flVerificato ");
        }
        queryStr.append(" order by v.dtChiusura desc ");
        Query query = entityManager.createQuery(queryStr.toString());
        if (flVerificato != null) {
            query.setParameter("flVerificato", flVerificato);
        }
        query.setMaxResults(maxResults);
        return lazyListHelper.getTableBean(query, list -> getVrsSessioneVersTableBeanFrom(list, entityManager));
    }

    private VrsSessioneVersKoTableBean getVrsSessioneVersTableBeanFrom(List<Object[]> sessioniErrate,
            EntityManager em) {
        VrsSessioneVersKoTableBean sessioniErrateTableBean = new VrsSessioneVersKoTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : sessioniErrate) {
                int i = 0;
                VrsSessioneVersKo bean = em.find(VrsSessioneVersKo.class, Long.class.cast(row[i++]));
                VrsSessioneVersKoRowBean rowBean = (VrsSessioneVersKoRowBean) Transform.entity2RowBean(bean);

                StringBuilder struttura = new StringBuilder();
                if (rowBean.getNmAmbiente() != null) {
                    struttura.append(rowBean.getNmAmbiente());
                    if (rowBean.getNmEnte() != null) {
                        struttura.append(", ").append(rowBean.getNmEnte());
                        if (rowBean.getNmStrut() != null) {
                            struttura.append(", ").append(rowBean.getNmStrut());
                        }
                    }
                }

                rowBean.setString("struttura", struttura.toString());

                StringBuilder chiaveUd = new StringBuilder();
                if (rowBean.getCdRegistroKeyUnitaDoc() != null) {
                    chiaveUd.append(rowBean.getCdRegistroKeyUnitaDoc());
                    if (rowBean.getAaKeyUnitaDoc() != null) {
                        chiaveUd.append(" - ").append(rowBean.getAaKeyUnitaDoc());
                        if (rowBean.getCdKeyUnitaDoc() != null) {
                            chiaveUd.append(" - ").append(rowBean.getCdKeyUnitaDoc());
                        }
                    }
                }

                rowBean.setString("chiave_ud", chiaveUd.toString());
                final Object dsErr = row[i++];
                rowBean.setString("ds_err", (String) dsErr);
                final Object cdErr = row[i++];
                rowBean.setString("cd_err", (String) cdErr);

                sessioniErrateTableBean.add(rowBean);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return sessioniErrateTableBean;
    }

    /**
     * Restituisce il rowbean contenente i dati del dettaglio di un Versamento Fallito
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity bean MonVVisVersErrRowBean
     *
     */
    public MonVVisVersErrIamRowBean getMonVVisVersErrIamRowBean(BigDecimal idSessioneVers) {
        String queryStr = "SELECT u FROM MonVVisVersErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        MonVVisVersErrIamRowBean versErr = null;
        List<MonVVisVersErrIam> versErrList = query.getResultList();
        try {
            if (versErrList != null && !versErrList.isEmpty()) {
                versErr = (MonVVisVersErrIamRowBean) Transform.entity2RowBean(versErrList.get(0));
            }
            aggiungiXmlDaObjectStorage(versErr);

        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio del versamento fallito " + e.getMessage(), e);
        }
        return versErr;
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
     *            MonVVisVersErrIamRowBean
     */
    private void aggiungiXmlDaObjectStorage(MonVVisVersErrIamRowBean riga) {
        boolean xmlVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * Se gli xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (riga.getIdSessioneVers() != null && xmlVuoti) {
            Map<String, String> xmls = objectStorageService.getObjectSipInStaging(riga.getIdSessioneVers().longValue());
            // recupero oggetti se presenti su O.S
            if (!xmls.isEmpty()) {
                riga.setBlXmlRich(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA));
                riga.setBlXmlRisp(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA));
                riga.setBlXmlIndex(xmls.get(CostantiDB.TipiXmlDati.INDICE_FILE));
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
     *            MonVVisSesErrIamRowBean
     */
    private void aggiungiXmlDaObjectStorage(MonVVisSesErrIamRowBean riga) {
        boolean xmlVuoti = riga.getBlXmlRich() == null && riga.getBlXmlRisp() == null;
        /*
         * il backend risulta essere Object storage quindi gli xml non sono nei rowbean già caricati in memoria. Se gli
         * xml non sono ancora stati migrati, però, sono ancora presenti sulle tabelle
         */
        if (riga.getIdSessioneVers() != null && xmlVuoti) {
            Map<String, String> xmls = objectStorageService.getObjectSipInStaging(riga.getIdSessioneVers().longValue());

            // recupero oggetti se presenti su O.S.
            if (!xmls.isEmpty()) {
                riga.setBlXmlRich(xmls.get(CostantiDB.TipiXmlDati.RICHIESTA));
                riga.setBlXmlRisp(xmls.get(CostantiDB.TipiXmlDati.RISPOSTA));
                riga.setBlXmlIndex(xmls.get(CostantiDB.TipiXmlDati.INDICE_FILE));
            }
        }

    }

    /**
     * Restituisce il rowbean contenente i dati del dettaglio di una Sessione Errata
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity bean MonVVisSesErrRowBean
     *
     */
    public MonVVisSesErrIamRowBean getMonVVisSesErrIamRowBean(BigDecimal idSessioneVers) {
        MonVVisSesErrIamRowBean sesErr = null;
        List<MonVVisSesErrIam> sesErrList = getMonVVisSesErrIam(idSessioneVers);
        try {
            if (sesErrList != null && !sesErrList.isEmpty()) {
                sesErr = (MonVVisSesErrIamRowBean) Transform.entity2RowBean(sesErrList.get(0));
            }
            aggiungiXmlDaObjectStorage(sesErr);

        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio della sessione fallita " + e.getMessage(), e);
        }

        // concateno alcuni campi per il front-end
        String ambiente = sesErr.getNmAmbiente() != null ? sesErr.getNmAmbiente() : "";
        String ente = sesErr.getNmEnte() != null ? ", " + sesErr.getNmEnte() : "";
        String strut = sesErr.getNmStrut() != null ? ", " + sesErr.getNmStrut() : "";
        sesErr.setString("struttura", ambiente + ente + strut);

        return sesErr;
    }

    public List<MonVVisSesErrIam> getMonVVisSesErrIam(BigDecimal idSessioneVers) {
        String queryStr = "SELECT u FROM MonVVisSesErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        return query.getResultList();
    }

    /**
     * Restituisce un teablebean rappresentante la lista file nel dettaglio di un Versamento Fallito
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity bean VrsFileSessioneKoTableBean
     *
     */
    public VrsFileSessioneKoTableBean getFileListTableBean(BigDecimal idSessioneVers) {
        Query query = entityManager
                .createQuery("select ses.idSessioneVersKo, fileSes.pgFileSessione, fileSes.nmFileSessione, "
                        + "CONCAT('vers_', ses.idSessioneVersKo, '_file_', fileSes.pgFileSessione, '_id_', fileSes.nmFileSessione) "
                        + "from VrsSessioneVersKo ses JOIN " + "ses.vrsDatiSessioneVersKos datiSes JOIN "
                        + "datiSes.vrsFileSessioneKos fileSes " + "where ses.idSessioneVersKo = :idSessioneVers "
                        + "order by fileSes.pgFileSessione ");
        query.setParameter("idSessioneVers", GenericHelper.longFromBigDecimal(idSessioneVers));

        return lazyListHelper.getTableBean(query, list -> getVrsFileSessioneKoTableBeanFrom(idSessioneVers, list));
    }

    private VrsFileSessioneKoTableBean getVrsFileSessioneKoTableBeanFrom(BigDecimal idSessioneVers,
            List<Object[]> listaFile) {
        VrsFileSessioneKoTableBean fileSessioneTableBean = new VrsFileSessioneKoTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : listaFile) {
                VrsFileSessioneKoRowBean rowBean = new VrsFileSessioneKoRowBean();
                rowBean.setBigDecimal("id_sessione_vers", idSessioneVers);
                rowBean.setPgFileSessione(new BigDecimal(row[1].toString()));
                rowBean.setNmFileSessione(row[2].toString());
                rowBean.setString("nm_file_vers", row[3].toString());
                fileSessioneTableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return fileSessioneTableBean;
    }

    /**
     * Restituisce una lista di oggetti blob (id, nome, contenuto in byte[]) che rappresentano i file fisici della lista
     * file nella pagina Dettaglio Versamento Fallito
     *
     * @param idSessioneVersKo
     *            id sessione di versamento
     *
     * @return lista ogetti di tipo BlobObject
     *
     */
    public List<BlobObject> getBlobboByteList(BigDecimal idSessioneVersKo) {
        // creo la lista che conterrà i blobbi dei file da restituire
        List<BlobObject> listaBlobbiFile = new ArrayList<>();

        // ricavo i blobbi
        VrsSessioneVersKo sessione = entityManager.find(VrsSessioneVersKo.class, idSessioneVersKo.longValue());
        for (int i = 0; i < sessione.getVrsDatiSessioneVersKos().size(); i++) {
            VrsDatiSessioneVersKo datiSes = sessione.getVrsDatiSessioneVersKos().get(i);
            for (int j = 0; j < datiSes.getVrsFileSessioneKos().size(); j++) {
                VrsFileSessioneKo fileSes = datiSes.getVrsFileSessioneKos().get(j);
                BlobObject bo = new BlobObject(fileSes.getIdFileSessioneKo(), "vers_" + sessione.getIdSessioneVersKo()
                        + "_file_" + fileSes.getPgFileSessione() + "_id_" + fileSes.getNmFileSessione(), null);
                listaBlobbiFile.add(bo);
            }
        }
        return listaBlobbiFile;
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param filtriJS
     *            job schedulati FiltriJobSchedulati
     * @param dateValidate
     *            date da verificare
     *
     * @return entity bean LogVLisSchedTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public AbstractBaseTable<?> getLogVLisSchedViewBean(FiltriJobSchedulati filtriJS, Date[] dateValidate)
            throws EMFError {
        AbstractBaseTable<?> listReturn;

        final Date dataOrarioDa = dateValidate != null ? dateValidate[0] : null;
        final Date dataOrarioA = dateValidate != null ? dateValidate[1] : null;
        if ("0".equals(filtriJS.getStorico().parse())) {
            listReturn = getLogVLisSchedViewBeanPlainFilters(filtriJS.getNm_job().parse(), dataOrarioDa, dataOrarioA);
        } else {
            listReturn = getLogVLisSchedHistViewBeanPlainFilters(filtriJS.getNm_job().parse(), dataOrarioDa,
                    dataOrarioA);
        }
        return listReturn;
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param nomeJob
     *            nome del job
     * @param dataOrarioDa
     *            data e ora di inizio
     * @param dataOrarioA
     *            data e ora di fine
     *
     * @return LogVLisSchedTableBean table bean per la UI
     */
    public LogVLisSchedTableBean getLogVLisSchedViewBeanPlainFilters(String nomeJob, Date dataOrarioDa,
            Date dataOrarioA) {

        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSched u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            if (nomeJob.equals("ALLINEAMENTO_LOG")) {
                nomeJob = "ALLINEAMENTO_LOG_SACER";
            } else if (nomeJob.equals("INIZIALIZZAZIONE_LOG")) {
                nomeJob = "INIZIALIZZAZIONE_LOG_SACER";
            }

            query.setParameter("nmJob", nomeJob);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }
        return lazyListHelper.getTableBean(query, this::getLogVLisSchedTableBeanFrom);
    }

    /**
     * Metodo che restituisce un viewbean con lo storico dei record trovati in base ai filtri di ricerca passati in
     * ingresso
     *
     * @param nomeJob
     *            nome del job
     * @param dataOrarioDa
     *            data e ora di inizio
     * @param dataOrarioA
     *            data e ora di fine
     *
     * @return LogVLisSchedHistTableBean table bean per la UI
     */
    public LogVLisSchedHistTableBean getLogVLisSchedHistViewBeanPlainFilters(String nomeJob, Date dataOrarioDa,
            Date dataOrarioA) {

        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSchedHist u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            if (nomeJob.equals("ALLINEAMENTO_LOG")) {
                nomeJob = "ALLINEAMENTO_LOG_SACER";
            } else if (nomeJob.equals("INIZIALIZZAZIONE_LOG")) {
                nomeJob = "INIZIALIZZAZIONE_LOG_SACER";
            }

            query.setParameter("nmJob", nomeJob);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }
        return lazyListHelper.getTableBean(query, this::getLogVLisSchedHistTableBeanFrom);
    }

    private LogVLisSchedTableBean getLogVLisSchedTableBeanFrom(List<LogVLisSched> listaSched) {
        LogVLisSchedTableBean schedTableBean = new LogVLisSchedTableBean();
        try {
            if (listaSched != null && !listaSched.isEmpty()) {
                schedTableBean = (LogVLisSchedTableBean) Transform.entities2TableBean(listaSched);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Creo un nuovo campo concatenandone altri già esistenti
        for (int i = 0; i < schedTableBean.size(); i++) {
            LogVLisSchedRowBean row = schedTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedTableBean;
    }

    private LogVLisSchedHistTableBean getLogVLisSchedHistTableBeanFrom(List<LogVLisSchedHist> listaSched) {
        LogVLisSchedHistTableBean schedTableBean = new LogVLisSchedHistTableBean();
        try {
            if (listaSched != null && !listaSched.isEmpty()) {
                schedTableBean = (LogVLisSchedHistTableBean) Transform.entities2TableBean(listaSched);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Creo un nuovo campo concatenandone altri già esistenti
        for (int i = 0; i < schedTableBean.size(); i++) {
            LogVLisSchedHistRowBean row = schedTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedTableBean;
    }

    public AbstractBaseTable<?> getLogVLisSchedStrutViewBean(FiltriJobSchedulati filtriJS, Date[] dateValidate)
            throws EMFError {

        AbstractBaseTable<?> listReturn;

        final Date dataOrarioDa = dateValidate != null ? dateValidate[0] : null;
        final Date dataOrarioA = dateValidate != null ? dateValidate[1] : null;

        if ("0".equals(filtriJS.getStorico().parse())) {
            listReturn = getLogVLisSchedStrutViewBean(filtriJS.getId_strut().parse(), filtriJS.getNm_job().parse(),
                    dataOrarioDa, dataOrarioA);
        } else {
            listReturn = getLogVLisSchedStrutHistViewBean(filtriJS.getId_strut().parse(), filtriJS.getNm_job().parse(),
                    dataOrarioDa, dataOrarioA);
        }
        return listReturn;
    }

    public LogVLisSchedStrutTableBean getLogVLisSchedStrutViewBean(BigDecimal idStrut, String nomeJob,
            Date dataOrarioDa, Date dataOrarioA) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSchedStrut u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro struttura
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            if (nomeJob.equals("ALLINEAMENTO_LOG")) {
                nomeJob = "ALLINEAMENTO_LOG_SACER";
            } else if (nomeJob.equals("INIZIALIZZAZIONE_LOG")) {
                nomeJob = "INIZIALIZZAZIONE_LOG_SACER";
            }

            query.setParameter("nmJob", nomeJob);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<LogVLisSchedStrut> listaSched = query.getResultList();
        LogVLisSchedStrutTableBean schedTableBean = new LogVLisSchedStrutTableBean();
        try {
            if (listaSched != null && !listaSched.isEmpty()) {
                schedTableBean = (LogVLisSchedStrutTableBean) Transform.entities2TableBean(listaSched);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Creo un nuovo campo concatenandone altri già esistenti
        for (int i = 0; i < schedTableBean.size(); i++) {
            LogVLisSchedStrutRowBean row = schedTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedTableBean;
    }

    public LogVLisSchedStrutHistTableBean getLogVLisSchedStrutHistViewBean(BigDecimal idStrut, String nomeJob,
            Date dataOrarioDa, Date dataOrarioA) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSchedStrutHist u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro struttura
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            if (nomeJob.equals("ALLINEAMENTO_LOG")) {
                nomeJob = "ALLINEAMENTO_LOG_SACER";
            } else if (nomeJob.equals("INIZIALIZZAZIONE_LOG")) {
                nomeJob = "INIZIALIZZAZIONE_LOG_SACER";
            }

            query.setParameter("nmJob", nomeJob);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<LogVLisSchedStrutHist> listaSchedHist = query.getResultList();
        LogVLisSchedStrutHistTableBean schedHistTableBean = new LogVLisSchedStrutHistTableBean();
        try {
            if (listaSchedHist != null && !listaSchedHist.isEmpty()) {
                schedHistTableBean = (LogVLisSchedStrutHistTableBean) Transform.entities2TableBean(listaSchedHist);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // Creo un nuovo campo concatenandone altri già esistenti
        for (int i = 0; i < schedHistTableBean.size(); i++) {
            LogVLisSchedStrutHistRowBean row = schedHistTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedHistTableBean;
    }

    /**
     * Restituisce un rowbean contenente le informazioni sull'ultima schedulazione di un determinato job
     *
     * @param nomeJob
     *            nome del job
     *
     * @return entity bean LogVVisLastSchedRowBean
     *
     */
    public LogVVisLastSchedRowBean getLogVVisLastSchedRowBean(String nomeJob) {
        List<LogVVisLastSched> listaLog = getLogVVisLastSched(nomeJob);
        LogVVisLastSchedRowBean logRowBean = new LogVVisLastSchedRowBean();
        try {
            if (listaLog != null && !listaLog.isEmpty()) {
                logRowBean = (LogVVisLastSchedRowBean) Transform.entity2RowBean(listaLog.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return logRowBean;
    }

    public List<LogVVisLastSched> getLogVVisLastSched(String nomeJob) {
        String queryStr = "SELECT u FROM LogVVisLastSched u WHERE u.nmJob = :nmJob";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmJob", nomeJob);
        return query.getResultList();
    }

    public OrgAmbiente getAmbiente(String nmAmbiente) {
        String queryStr = "SELECT u FROM OrgAmbiente u WHERE u.nmAmbiente = :nmambientein ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmambientein", nmAmbiente);
        if (!query.getResultList().isEmpty()) {
            return (OrgAmbiente) query.getResultList().get(0);
        } else {
            return null;
        }
    }

    public BigDecimal getIdAmbiente(BigDecimal idEnte) {
        String queryStr = "SELECT u.orgAmbiente.idAmbiente FROM OrgEnte u WHERE u.idEnte = :identein ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("identein", idEnte.longValue());
        Long res = (Long) query.getSingleResult();
        return new BigDecimal(res);
    }

    public BigDecimal getIdEnte(BigDecimal idStruttura) {
        String queryStr = "SELECT u.orgEnte.idEnte FROM OrgStrut u WHERE u.idStrut = :idstrutturain ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idstrutturain", GenericHelper.longFromBigDecimal(idStruttura));
        Long res = (Long) query.getSingleResult();
        return new BigDecimal(res);
    }

    public List<Object[]> contaVersFallitiDistintiUD(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(u), u.id.idStrut, u.id.flVerif, u.id.flNonRisolub " + "FROM IamAbilOrganiz iao, "
                        + "VrsVUdNonVer u, OrgStrut strut " + "WHERE u.id.idStrut = iao.idOrganizApplic "
                        + "AND strut.idStrut = iao.idOrganizApplic " + "AND iao.iamUser.idUserIam = :idUa ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }

        queryStr.append("GROUP BY u.id.idStrut, u.id.flVerif, u.id.flNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        return query.getResultList();
    }

    public List<Object[]> contaVersFallitiDistintiDoc(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(u), u.vrsVDocNonVerId.idStrut, u.vrsVDocNonVerId.flVerif, u.vrsVDocNonVerId.flNonRisolub "
                        + "FROM IamAbilOrganiz iao, " + "VrsVDocNonVer u, OrgStrut strut "
                        + "WHERE u.vrsVDocNonVerId.idStrut = iao.idOrganizApplic "
                        + "and strut.idStrut = iao.idOrganizApplic " + "AND iao.iamUser.idUserIam = :idUa ");

        // Inserimento nella query dei filtri
        if (idAmbiente != null) {
            queryStr.append("and strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append("and strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStruttura != null) {
            queryStr.append("and iao.idOrganizApplic = :idStrut ");
        }
        queryStr.append(
                "GROUP BY u.vrsVDocNonVerId.idStrut, u.vrsVDocNonVerId.flVerif, u.vrsVDocNonVerId.flNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        return query.getResultList();
    }

    /**
     * Restituisce il rowbean contenente i dati del dettaglio di un'unità documentaria
     *
     * @param idStrut
     *            id struttura
     * @param cdRegistroKeyUnitaDoc
     *            registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * @param cdKeyUnitaDoc
     *            numero unita doc
     *
     * @return entity bean MonVVisUdNonVersRowBean
     *
     */
    public MonVVisUdNonVersRowBean getMonVVisUdNonVersRowBean(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) {
        List<MonVVisUdNonVers> listaNonVers = getMonVVisUdNonVers(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                cdKeyUnitaDoc);
        MonVVisUdNonVersRowBean nonVers = null;
        try {
            if (listaNonVers != null && !listaNonVers.isEmpty()) {
                nonVers = (MonVVisUdNonVersRowBean) Transform.entity2RowBean(listaNonVers.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'unità documentaria non versata " + e.getMessage(), e);
        }

        // concateno alcuni campi per il front-end
        nonVers.setString("struttura",
                nonVers.getNmAmbiente() + ", " + nonVers.getNmEnte() + ", " + nonVers.getNmStrut());
        nonVers.setString("chiave_ud", nonVers.getCdRegistroKeyUnitaDoc() + " - " + nonVers.getAaKeyUnitaDoc() + " - "
                + nonVers.getCdKeyUnitaDoc());

        return nonVers;
    }

    public List<MonVVisUdNonVers> getMonVVisUdNonVers(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) {
        String queryStr = "SELECT u FROM MonVVisUdNonVers u " + "WHERE u.id.idStrut = :idstrut "
                + "AND u.id.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc "
                + "AND u.id.aaKeyUnitaDoc = :aakeyunitadoc " + "AND u.id.cdKeyUnitaDoc = :cdkeyunitadoc";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);
        return query.getResultList();
    }

    public MonVVisDocNonVersRowBean getMonVVisDocNonVersRowBean(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdKeyDocVers) {
        List<MonVVisDocNonVers> listaNonVers = getMonVVisDocNonVers(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                cdKeyUnitaDoc, cdKeyDocVers);
        MonVVisDocNonVersRowBean nonVers = null;
        try {
            if (listaNonVers != null && !listaNonVers.isEmpty()) {
                nonVers = (MonVVisDocNonVersRowBean) Transform.entity2RowBean(listaNonVers.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio dell'unità documentaria non versata " + e.getMessage(), e);
        }

        // concateno alcuni campi per il front-end
        nonVers.setString("struttura",
                nonVers.getNmAmbiente() + ", " + nonVers.getNmEnte() + ", " + nonVers.getNmStrut());
        nonVers.setString("chiave_ud", nonVers.getCdRegistroKeyUnitaDoc() + " - " + nonVers.getAaKeyUnitaDoc() + " - "
                + nonVers.getCdKeyUnitaDoc());

        return nonVers;
    }

    public List<MonVVisDocNonVers> getMonVVisDocNonVers(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdKeyDocVers) {
        String queryStr = "SELECT u FROM MonVVisDocNonVers u " + "WHERE u.id.idStrut = :idstrut "
                + "AND u.id.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc "
                + "AND u.id.aaKeyUnitaDoc = :aakeyunitadoc " + "AND u.id.cdKeyUnitaDoc = :cdkeyunitadoc "
                + "AND u.id.cdKeyDocVers = :cdkeydocvers ";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);
        query.setParameter("cdkeydocvers", cdKeyDocVers);

        return query.getResultList();
    }

    public MonVLisVersDocNonVersTableBean getMonVLisVersDocNonVersViewBean(BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdKeyDocVers) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisVersDocNonVers u "
                + "WHERE u.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc " + "AND u.aaKeyUnitaDoc = :aakeyunitadoc "
                + "AND u.cdKeyUnitaDoc = :cdkeyunitadoc " + "AND u.idStrut = :idstrut ");

        if (cdKeyDocVers != null) {
            queryStr.append("AND u.cdKeyDocVers = :cdkeydocvers ");
        }

        // Ordina per ambiente, ente, struttura e chiave di ordinamento
        queryStr.append("ORDER BY u.dtChiusura DESC");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);

        if (cdKeyDocVers != null) {
            query.setParameter("cdkeydocvers", cdKeyDocVers);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisVersDocNonVers> listaVersDocNonVers = query.getResultList();

        MonVLisVersDocNonVersTableBean monTableBean = new MonVLisVersDocNonVersTableBean();

        try {
            if (listaVersDocNonVers != null && !listaVersDocNonVers.isEmpty()) {
                monTableBean = (MonVLisVersDocNonVersTableBean) Transform.entities2TableBean(listaVersDocNonVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return monTableBean;
    }

    public MonVLisVersUdNonVersTableBean getMonVLisVersUdNonVersViewBean(BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) {
        // Ordina per ambiente, ente, struttura e chiave di ordinamento
        Query query = entityManager.createQuery("SELECT u FROM MonVLisVersUdNonVers u "
                + "WHERE u.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc " + "AND u.aaKeyUnitaDoc = :aakeyunitadoc "
                + "AND u.cdKeyUnitaDoc = :cdkeyunitadoc " + "AND u.idStrut = :idstrut ORDER BY u.dtChiusura DESC");

        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisVersUdNonVers> listaVersUdNonVers = query.getResultList();

        MonVLisVersUdNonVersTableBean monTableBean = new MonVLisVersUdNonVersTableBean();

        try {
            if (listaVersUdNonVers != null && !listaVersUdNonVers.isEmpty()) {
                monTableBean = (MonVLisVersUdNonVersTableBean) Transform.entities2TableBean(listaVersUdNonVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return monTableBean;
    }

    public BaseTable getRegistriFromTotaleMonVLisVersErr(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            Long idUserIam) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u.cdRegistroKeyUnitaDoc FROM MonVLisVersErrIam u ");

        // Inserimento nella query del filtro id ambiente
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        }

        // ordina per data chiusura descrescente
        queryStr.append("AND u.cdRegistroKeyUnitaDoc IS NOT NULL ORDER BY u.cdRegistroKeyUnitaDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (idUserIam != null) {
            query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaRegistri = query.getResultList();
        BaseTable tabellaRegistri = new BaseTable();

        for (int i = 0; i < listaRegistri.size(); i++) {
            BaseRow rigaRegistri = new BaseRow();
            rigaRegistri.setString("registro", listaRegistri.get(i));
            tabellaRegistri.add(rigaRegistri);
        }

        return tabellaRegistri;
    }

    public BaseTable getRegistriFromTotaleMonVLisUdNonVers(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            Long idUserIam) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.id.cdRegistroKeyUnitaDoc FROM MonVLisUdNonVersIam u ");

        // Inserimento nella query del filtro id ambiente
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.id.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        }

        // ordina per data chiusura descrescente
        queryStr.append("AND u.id.cdRegistroKeyUnitaDoc IS NOT NULL ORDER BY u.id.cdRegistroKeyUnitaDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (idUserIam != null) {
            query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaRegistri = query.getResultList();
        BaseTable tabellaRegistri = new BaseTable();

        for (int i = 0; i < listaRegistri.size(); i++) {
            BaseRow rigaRegistri = new BaseRow();
            rigaRegistri.setString("registro", listaRegistri.get(i));
            tabellaRegistri.add(rigaRegistri);
        }

        return tabellaRegistri;
    }

    public Object[] getXmlsSesErr(BigDecimal idSessioneVers) {
        Object[] xmls = new Object[2];
        String queryStr = "SELECT u FROM MonVVisSesErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        MonVVisSesErrIamRowBean sesErr = new MonVVisSesErrIamRowBean();
        List<MonVVisSesErrIam> sesErrList = query.getResultList();
        try {
            if (sesErrList != null && !sesErrList.isEmpty()) {
                sesErr = (MonVVisSesErrIamRowBean) Transform.entity2RowBean(sesErrList.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio della sessione fallita " + e.getMessage(), e);
        }

        byte[] xmlRich = sesErr.getBlXmlRich().getBytes();
        byte[] xmlRisp = sesErr.getBlXmlRisp().getBytes();
        xmls[0] = xmlRich;
        xmls[1] = xmlRisp;
        return xmls;
    }

    /**
     *
     * @param idSessioneVers
     *            id della sessione di versamento
     *
     * @return xml di richiesta e di risposta
     */
    public Object[] getXmlsVersErr(BigDecimal idSessioneVers) {
        Object[] xmls = new Object[2];
        String queryStr = "SELECT u FROM MonVVisVersErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        MonVVisVersErrIamRowBean sesErr = new MonVVisVersErrIamRowBean();
        List<MonVVisVersErrIam> versErrList = query.getResultList();
        try {
            if (versErrList != null && !versErrList.isEmpty()) {
                sesErr = (MonVVisVersErrIamRowBean) Transform.entity2RowBean(versErrList.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio del versamento fallito " + e.getMessage(), e);
        }

        byte[] xmlRich = sesErr.getBlXmlRich().getBytes();
        byte[] xmlRisp = sesErr.getBlXmlRisp().getBytes();
        xmls[0] = xmlRich;
        xmls[1] = xmlRisp;
        return xmls;
    }

    public MonVLisSesRecupTableBean getSessioniRecupero(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura, String nmUserid, Date[] dateAperture, Object[] chiavi, String tiStato,
            String tiSessione) {
        StringBuilder queryStr = new StringBuilder("SELECT sesRecup from MonVLisSesRecup sesRecup ");
        Date dateDa = dateAperture != null ? dateAperture[0] : null;
        Date dateA = dateAperture != null ? dateAperture[1] : null;
        String whereCondition = " WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereCondition).append("sesRecup.idAmbiente = :idAmbiente ");
            whereCondition = "AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereCondition).append("sesRecup.idEnte = :idEnte ");
            whereCondition = "AND ";
        }
        if (idStruttura != null) {
            queryStr.append(whereCondition).append("sesRecup.idStrut = :idStrut ");
            whereCondition = "AND ";
        }
        if (StringUtils.isNotEmpty(nmUserid)) {
            queryStr.append(whereCondition).append("sesRecup.nmUserid LIKE :nmUserid ");
            whereCondition = "AND ";
        }
        if (dateDa != null && dateA != null) {
            queryStr.append(whereCondition).append("sesRecup.dtApertura BETWEEN :dataDa AND :dataA ");
            whereCondition = "AND ";
        } else if (dateDa != null) {
            queryStr.append(whereCondition).append("sesRecup.dtApertura >= :dataDa ");
            whereCondition = "AND ";
        } else if (dateA != null) {
            queryStr.append(whereCondition).append("sesRecup.dtApertura <= :dataA ");
            whereCondition = "AND ";
        }
        List<String> registri = null;
        if (chiavi != null) {
            if (chiavi[0] != null) {
                registri = Arrays.asList((String[]) chiavi[0]);
                if (registri != null && registri.size() > 1) {
                    queryStr.append(whereCondition).append("sesRecup.cdRegistroKeyUnitaDoc IN (:registri) ");
                    whereCondition = "AND ";
                } else if (registri != null && !registri.isEmpty()) {
                    queryStr.append(whereCondition).append("sesRecup.cdRegistroKeyUnitaDoc = :registri ");
                    whereCondition = "AND ";
                }
            }
            if (chiavi[1] != null) {
                queryStr.append(whereCondition).append("sesRecup.aaKeyUnitaDoc = :anno ");
                whereCondition = "AND ";
            }
            if (chiavi[2] != null) {
                queryStr.append(whereCondition).append("sesRecup.cdKeyUnitaDoc = :numero ");
                whereCondition = "AND ";
            }
        }

        if (tiStato != null) {
            queryStr.append(whereCondition).append("sesRecup.tiStato = :tiStato ");
            whereCondition = "AND ";
        }
        if (tiSessione != null) {
            queryStr.append(whereCondition).append("sesRecup.tiSessione = :tiSessione ");
        }

        queryStr.append("ORDER BY sesRecup.dtApertura DESC");
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        // setParameters
        if (dateDa != null) {
            query.setParameter("dataDa", dateDa);
        }
        if (dateA != null) {
            query.setParameter("dataA", dateA);
        }

        if (chiavi != null) {
            if (chiavi[0] != null) {
                if (registri != null && registri.size() > 1) {
                    query.setParameter("registri", registri);
                } else if (registri != null && !registri.isEmpty()) {
                    query.setParameter("registri", registri.get(0));
                }
            }
            if (chiavi[1] != null) {
                query.setParameter("anno", chiavi[1]);
            }
            if (chiavi[2] != null) {
                query.setParameter("numero", chiavi[2]);
            }
        }
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }
        if (StringUtils.isNotBlank(nmUserid)) {
            query.setParameter("nmUserid", "%" + nmUserid + "%");
        }
        if (StringUtils.isNotBlank(tiStato)) {
            query.setParameter("tiStato", tiStato);
        }
        if (StringUtils.isNotBlank(tiSessione)) {
            query.setParameter("tiSessione", tiSessione);
        }
        List<MonVLisSesRecup> sesRecup = query.getResultList();
        MonVLisSesRecupTableBean sesRecupTableBean = new MonVLisSesRecupTableBean();
        try {
            if (sesRecup != null && !sesRecup.isEmpty()) {
                for (MonVLisSesRecup row : sesRecup) {
                    MonVLisSesRecupRowBean rowBean = (MonVLisSesRecupRowBean) Transform.entity2RowBean(row);
                    // MAC#27666
                    // SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE
                    DateTimeFormatter df = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATE_TYPE);
                    // end MAC#27666
                    StringBuilder builder = new StringBuilder();
                    RecSessioneRecup entity = entityManager.find(RecSessioneRecup.class,
                            row.getIdSessioneRecup().longValue());
                    int tmpIndex = 0;
                    for (RecDtVersRecup dtVersRec : entity.getRecDtVersRecups()) {
                        builder.append(df.format(dtVersRec.getDtVers())).append(" (")
                                .append(dtVersRec.getTiStatoDtVersRecup()).append(")");
                        if (tmpIndex < (entity.getRecDtVersRecups().size() - 1)) {
                            builder.append(" ");
                        }
                        tmpIndex++;
                    }
                    rowBean.setDsListaDtVers(builder.toString());
                    boolean downloadable = rowBean.getTiSessione()
                            .equals(JobConstants.TipoSessioniRecupEnum.DOWNLOAD.name())
                            && rowBean.getTiStato().equals(JobConstants.StatoSessioniRecupEnum.CHIUSO_OK.name());
                    rowBean.setFlDownloadable(downloadable ? "1" : "0");
                    sesRecupTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return sesRecupTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Visualizza Repliche Organizzazioni
     *
     * @param filtri
     *            i filtri di ricerca riportati dalla pagina precedente
     * @param maxResult
     *            numero massimo risultati
     *
     * @return entity bean IamVLisOrganizDaReplicTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public IamVLisOrganizDaReplicTableBean getIamVLisOrganizDaReplicTableBean(FiltriReplicaOrg filtri, int maxResult)
            throws EMFError {
        return getIamVLisOrganizDaReplicTableBeanPlainFilters(maxResult, filtri.getId_ambiente().parse(),
                filtri.getId_ente().parse(), filtri.getId_strut().parse(), filtri.getTi_oper_replic().parse(),
                filtri.getTi_stato_replic().parse());
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Visualizza Repliche Organizzazioni
     *
     * @param maxResult
     *            numero massimo di risultati
     * @param idAmbiente
     *            id dell'ambiente
     * @param idEnte
     *            id dell'ente
     * @param idStrut
     *            id della struttura
     * @param tiOper
     *            tipo operazione
     * @param tiStato
     *            tipo stato
     *
     * @return IamVLisOrganizDaReplicTableBean il table Bean da usare nella UI
     */
    public IamVLisOrganizDaReplicTableBean getIamVLisOrganizDaReplicTableBeanPlainFilters(int maxResult,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, String tiOper, String tiStato) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM IamVLisOrganizDaReplic u ");

        // Inserimento nella query del filtro id ambiente
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id ente
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        if (tiOper != null) {
            queryStr.append(whereWord).append("u.tiOperReplic = :tiOper ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        if (tiStato != null) {
            queryStr.append(whereWord).append("u.tiStatoReplic = :tiStato ");
        }

        // ordina per descrizione
        queryStr.append("ORDER BY u.dsOrdOrganiz");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (tiOper != null) {
            query.setParameter("tiOper", tiOper);
        }

        if (tiStato != null) {
            query.setParameter("tiStato", tiStato);
        }

        query.setMaxResults(maxResult);
        return lazyListHelper.getTableBean(query, this::getIamVLisOrganizDaReplicTableBeanFrom);
    }

    private IamVLisOrganizDaReplicTableBean getIamVLisOrganizDaReplicTableBeanFrom(
            List<IamVLisOrganizDaReplic> listaIam) {

        IamVLisOrganizDaReplicTableBean iamTableBean = new IamVLisOrganizDaReplicTableBean();

        try {
            if (listaIam != null && !listaIam.isEmpty()) {
                iamTableBean = (IamVLisOrganizDaReplicTableBean) Transform.entities2TableBean(listaIam);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Errore per presentarlo a video con codice, messaggio e data
         */
        for (IamVLisOrganizDaReplicRowBean row : iamTableBean) {
            if (row.getCdErr() != null) {
                row.setString("errore", row.getCdErr() + " - " + (row.getDsMsgErr() != null ? row.getDsMsgErr() : "")
                        + " del " + (row.getDtErr() != null ? row.getDtErr() : ""));
            }
        }

        return iamTableBean;
    }

    /**
     * Recupera il tablebean con i dati da visualizzare nella pagina Lista versamenti documenti annullati
     *
     * @param idUtente
     *            id utente
     * @param filtri
     *            i filtri di ricerca riportati dalla pagina precedente
     * @param maxResult
     *            numero massimo risultati
     * @param toTableBeanFunc
     *            funzione che data una List di record restituisce una {@link MonVLisUniDocDaAnnulTableBean}
     *
     *
     *
     * @return entity bean MonVLisDocTableBean
     *
     */
    public MonVLisUniDocDaAnnulTableBean getMonVLisUniDocDaAnnulViewBean(long idUtente,
            MonitoraggioFiltriListaDocBean filtri, int maxResult,
            Function<List, MonVLisUniDocDaAnnulTableBean> toTableBeanFunc) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisUniDocDaAnnul u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtri.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtri.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtri.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        BigDecimal idTipoUnitaDoc = filtri.getIdTipoUnitaDoc();
        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord).append("u.idTipoUnitaDoc = :idTipoUnitaDoc ");
            whereWord = "AND ";
        }

        if (filtri.getIdTipoDoc() != null) {
            queryStr.append(whereWord).append("u.idTipoDoc = :idTipoDoc ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        String registro = filtri.getCdRegistroKeyUnitaDoc();
        // Inserimento nella query del filtro CHIAVE UNITA DOC
        BigDecimal anno = filtri.getAaKeyUnitaDoc();
        String codice = filtri.getCdKeyUnitaDoc();

        if (StringUtils.isNotBlank(registro)) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registro ");
            whereWord = "AND ";
        }

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro CHIAVE UNITA DOC PER RANGE
        BigDecimal annoRangeDa = filtri.getAaKeyUnitaDocDa();
        BigDecimal annoRangeA = filtri.getAaKeyUnitaDocA();
        String codiceRangeDa = filtri.getCdKeyUnitaDocDa();
        String codiceRangeA = filtri.getCdKeyUnitaDocA();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append("LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno
        // versamento
        Date dataOrarioDa = (filtri.getGiornoVersDaValidato() != null ? filtri.getGiornoVersDaValidato() : null);
        Date dataOrarioA = (filtri.getGiornoVersAValidato() != null ? filtri.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtCreazione between :datada AND :dataa ");
            whereWord = "AND ";
        }

        String statoAnnul = filtri.getStatoDoc();
        if (statoAnnul != null) {
            queryStr.append(whereWord).append("u.tiStatoAnnul = :statoAnnul ");
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("u.idUserIam = :user ");

        // ordina per data creazione descrescente
        queryStr.append("ORDER BY u.dtCreazione DESC, u.dsOrdDoc");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        // non avendo passato alla query i parametri di ricerca, devo passarli ora
        query.setParameter("user", bigDecimalFromLong(idUtente));

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        }

        if (filtri.getIdTipoDoc() != null) {
            query.setParameter("idTipoDoc", filtri.getIdTipoDoc());
        }

        if (registro != null) {
            query.setParameter("registro", registro);
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

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter("datada", dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("dataa", dataOrarioA, TemporalType.TIMESTAMP);
        }

        if (statoAnnul != null) {
            query.setParameter("statoAnnul", statoAnnul);
        }

        query.setMaxResults(maxResult);

        return lazyListHelper.getTableBean(query, toTableBeanFunc, "idDoc");
    }

    public BaseRow getTotaliUdDocComp(FiltriContenutoSacer filtriCS, List<BigDecimal> idAmbitoTerritList,
            long idUserIam) throws EMFError {
        return getTotaliUdDocComp(idAmbitoTerritList, idUserIam, new FiltriContenutoSacerPlain(filtriCS));
    }

    public BaseRow getTotaliUdDocComp(List<BigDecimal> idAmbitoTerritList, long idUserIam,
            FiltriContenutoSacerPlain filtriCS) {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul) AS num_ud, "
                        + "sum(mon.niDocVers) + sum(mon.niDocAgg) - sum(mon.niDocAnnulUd) AS num_doc, "
                        + "sum(mon.niCompVers) + sum(mon.niCompAgg) - sum(mon.niCompAnnulUd) AS num_comp, "
                        + "sum(mon.niSizeVers) + sum(mon.niSizeAgg) - sum(mon.niSizeAnnulUd) AS dim_bytes "
                        + "FROM MonVRicContaUdDocComp mon, SIOrgEnteSiam enteConvenz, " + "OrgSubStrut subStrut "
                        + "JOIN subStrut.orgStrut strut " + "JOIN strut.orgEnte ente "
                        + "JOIN ente.orgCategEnte categEnte " + "LEFT JOIN strut.orgCategStrut categStrut, "
                        + "DecTipoUnitaDoc tipoUnitaDoc, DecVTreeCategTipoUd tree, IamAbilOrganiz iao "
                        + "WHERE iao.iamUser.idUserIam = :idUserIam "
                        + "AND strut.idEnteConvenz = enteConvenz.idEnteSiam "
                        + "AND tree.idCategTipoUnitaDoc = tipoUnitaDoc.decCategTipoUnitaDoc.idCategTipoUnitaDoc "
                        + "AND iao.idOrganizApplic = strut.idStrut " + "AND mon.idSubStrut = subStrut.idSubStrut "
                        + "AND mon.idTipoUnitaDoc = tipoUnitaDoc.idTipoUnitaDoc ");

        // Inserimento nella query del filtro id ambiente
        List<BigDecimal> idAmbienteList = filtriCS.getIdAmbienteList();
        if (idAmbienteList != null) {
            queryStr.append(whereWord).append("strut.orgEnte.orgAmbiente.idAmbiente IN (:idAmbienteList) ");
        }
        // Inserimento nella query del filtro id ente
        List<BigDecimal> idEnteList = filtriCS.getIdEnteList();
        if (!idEnteList.isEmpty()) {
            queryStr.append(whereWord).append("strut.orgEnte.idEnte IN (:idEnteList) ");
        }
        // Inserimento nella query del filtro id strut
        List<BigDecimal> idStrutList = filtriCS.getIdStrutList();
        if (!idStrutList.isEmpty()) {
            queryStr.append(whereWord).append("strut.idStrut IN (:idStrutList) ");
        }
        // Inserimento nella query del filtro id sub strut
        List<BigDecimal> idSubStrutList = filtriCS.getIdSubStrutList();
        if (!idSubStrutList.isEmpty()) {
            queryStr.append(whereWord).append("subStrut.idSubStrut IN (:idSubStrutList) ");
        }
        // Inserimento nella query del filtro registro
        List<BigDecimal> idRegistroUnitaDocList = filtriCS.getIdRegistroUnitaDocList();
        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idRegistroUnitaDoc IN (:idRegistroUnitaDocList) ");
        }
        BigDecimal aaKeyUnitaDoc = filtriCS.getAaKeyUnitaDoc();
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("mon.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }
        // Inserimento nella query del filtro tipo unità documentaria
        List<BigDecimal> idTipoUnitaDocList = filtriCS.getIdTipoUnitaDocList();
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idTipoUnitaDoc IN (:idTipoUnitaDocList) ");
        }
        // Inserimento nella query del filtro tipo documento
        List<BigDecimal> idTipoDocList = filtriCS.getIdTipoDocList();
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.idTipoDocPrinc IN (:idTipoDocList) ");
        }
        List<BigDecimal> idCategTipoUnitaDocList = filtriCS.getIdCategTipoUnitaDocList();
        List<BigDecimal> idSottocategTipoUnitaDocList = filtriCS.getIdSottocategTipoUnitaDocList();
        List<String> filtroCategoria = new ArrayList<>();
        int i = 0;
        for (BigDecimal idCateg : idCategTipoUnitaDocList) {
            i++;
            if (!idSottocategTipoUnitaDocList.isEmpty()) {
                for (BigDecimal idSotto : idSottocategTipoUnitaDocList) {
                    filtroCategoria.add("/" + idCateg + "/" + idSotto);
                }
            } else {
                filtroCategoria.add("/" + idCateg + "/%");
            }
        }

        if (!filtroCategoria.isEmpty()) {
            queryStr.append(whereWord).append("(");
            for (int j = 1; j <= filtroCategoria.size(); j++) {
                queryStr.append("tree.dlIdCategTipoUnitaDoc LIKE :filtro").append(j).append(" OR ");
            }
            queryStr.replace(queryStr.lastIndexOf(" OR "), queryStr.length(), ")");
        }

        // Inserimento nella query del filtro data
        Date dataRifDa = filtriCS.getDataRifDa();
        Date dataRifA = filtriCS.getDataRifA();
        if (dataRifDa != null && dataRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
        }

        if (!idAmbitoTerritList.isEmpty()) {
            queryStr.append(whereWord).append("enteConvenz.idAmbitoTerrit IN (:idAmbitoTerritList) ");
        }

        // Inserimento nella query del filtro ambito categoria ente
        List<BigDecimal> idCategEnteList = filtriCS.getIdCategEnteList();
        if (!idCategEnteList.isEmpty()) {
            queryStr.append(whereWord).append("categEnte.idCategEnte IN (:idCategEnteList) ");
        }

        // Inserimento nella query del filtro ambito categoria struttura
        List<BigDecimal> idCategStrutList = filtriCS.getIdCategStrutList();
        if (!idCategStrutList.isEmpty()) {
            queryStr.append(whereWord).append("categStrut.idCategStrut IN (:idCategStrutList) ");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbienteList != null) {
            query.setParameter("idAmbienteList", GenericHelper.longListFrom(idAmbienteList));
        }

        if (!idEnteList.isEmpty()) {
            query.setParameter("idEnteList", GenericHelper.longListFrom(idEnteList));
        }

        if (!idStrutList.isEmpty()) {
            query.setParameter("idStrutList", GenericHelper.longListFrom(idStrutList));
        }

        if (!idSubStrutList.isEmpty()) {
            query.setParameter("idSubStrutList", GenericHelper.longListFrom(idSubStrutList));
        }

        if (!idRegistroUnitaDocList.isEmpty()) {
            query.setParameter("idRegistroUnitaDocList", idRegistroUnitaDocList);
        }

        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }

        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
        }

        if (!idTipoDocList.isEmpty()) {
            query.setParameter("idTipoDocList", idTipoDocList);
        }

        int j = 1;
        if (!filtroCategoria.isEmpty()) {
            for (String filtro : filtroCategoria) {
                query.setParameter("filtro" + j, filtro);
                j++;
            }
        }

        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        if (!idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", GenericHelper.longListFrom(idAmbitoTerritList));
        }

        if (!idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", GenericHelper.longListFrom(idCategEnteList));
        }

        if (!idCategStrutList.isEmpty()) {
            query.setParameter("idCategStrutList", GenericHelper.longListFrom(idCategStrutList));
        }

        query.setParameter("idUserIam", idUserIam);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        Object[] totali = (Object[]) query.getSingleResult();
        BaseRow riga = new BaseRow();

        try {
            riga.setBigDecimal("num_ud", (BigDecimal) totali[0]);
            riga.setBigDecimal("num_doc", (BigDecimal) totali[1]);
            riga.setBigDecimal("num_comp", (BigDecimal) totali[2]);
            riga.setBigDecimal("dim_bytes", (BigDecimal) totali[3]);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return riga;
    }

    public BaseRow getTotaliUdDocCompForHome(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, Date dtRifDa,
            Date dtRifA, long idUserIam) {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul) AS num_ud, "
                        + "sum(mon.niDocVers) + sum(mon.niDocAgg) - sum(mon.niDocAnnulUd) AS num_doc, "
                        + "sum(mon.niCompVers) + sum(mon.niCompAgg) - sum(mon.niCompAnnulUd) AS num_comp, "
                        + "sum(mon.niSizeVers) + sum(mon.niSizeAgg) - sum(mon.niSizeAnnulUd) AS dim_bytes "
                        + "FROM MonVRicContaUdDocComp mon, " + "OrgSubStrut subStrut " + "JOIN subStrut.orgStrut strut "
                        + "LEFT JOIN strut.orgCategStrut categStrut, " + "DecTipoUnitaDoc tipoUnitaDoc "
                        + "JOIN tipoUnitaDoc.decCategTipoUnitaDoc categ, IamAbilOrganiz iao "
                        + "WHERE iao.iamUser.idUserIam = :idUserIam " + "AND iao.idOrganizApplic = strut.idStrut "
                        + "AND mon.idTipoUnitaDoc = tipoUnitaDoc.idTipoUnitaDoc "
                        + "AND mon.idSubStrut = subStrut.idSubStrut ");

        if (idAmbiente != null) {
            queryStr.append(whereWord).append("strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("strut.orgEnte.idEnte = :idEnte ");
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("strut.idStrut = :idStrut ");
        }

        if (dtRifDa != null && dtRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dtRifDa AND :dtRifA ");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", GenericHelper.longFromBigDecimal(idAmbiente));
        }

        if (idEnte != null) {
            query.setParameter("idEnte", GenericHelper.longFromBigDecimal(idEnte));
        }

        if (idStrut != null) {
            query.setParameter("idStrut", GenericHelper.longFromBigDecimal(idStrut));
        }

        if (dtRifDa != null && dtRifA != null) {
            query.setParameter("dtRifDa", dtRifDa);
            query.setParameter("dtRifA", dtRifA);
        }

        query.setParameter("idUserIam", idUserIam);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        Object[] totali = (Object[]) query.getSingleResult();
        BaseRow riga = new BaseRow();

        try {
            riga.setBigDecimal("num_ud", (BigDecimal) totali[0]);
            riga.setBigDecimal("num_doc", (BigDecimal) totali[1]);
            riga.setBigDecimal("num_comp", (BigDecimal) totali[2]);
            riga.setBigDecimal("dim_bytes", (BigDecimal) totali[3]);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return riga;
    }

    public Object[] getListaTotaliConsistenzaComp(FiltriConsistenzaSacer filtri) throws EMFError {
        /*
         * Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW
         */
        String confrontoConteggiNativeQuery = "SELECT id_strut, amb.nm_ambiente, "
                + "ente.nm_ente, tmp.nm_strut, tmp.ni_comp_vers, tmp.ni_comp_aip_generato, "
                + "tmp.ni_comp_aip_in_aggiorn, tmp.ni_comp_presa_in_carico, tmp.ni_comp_in_volume, tmp.ni_comp_annul, "
                + "(tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) delta_versati_gestiti "
                + "FROM (SELECT id_strut, nm_strut, ";

        String parte2 = " (SELECT nvl(sum (ni_comp_vers + ni_comp_agg), 0) "
                + "FROM sacer.MON_V_RIC_CONTA_UD_DOC_COMP conta_ud "
                + "WHERE conta_ud.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') "
                + " AND conta_ud.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') AND conta_ud.id_strut = strut.id_strut "
                + ") ni_comp_vers, " + "     (select nvl(sum(ni_comp_aip_generato), 0)"
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_generato, " + "      "
                + "     (select nvl(sum(ni_comp_aip_in_aggiorn), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_in_aggiorn, " + "      "
                + "     (select nvl(sum(ni_comp_presa_in_carico), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_presa_in_carico, " + "      "
                + "     (select nvl(sum(ni_comp_in_volume), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_in_volume, " + "      "
                + "     (select nvl(sum(ni_comp_annul), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_annul "
                + "    from org_strut strut " + " join sacer.org_ente entesub " + "on(entesub.id_ente = strut.id_ente) "
                + "    join sacer.org_ambiente ambientesub " + "on(ambientesub.id_ambiente = entesub.id_ambiente) "
                + "    where strut.fl_template = '0' ";

        int i = 3;
        int k = 3;
        if (filtri.getId_ambiente().parse() != null && !filtri.getId_ambiente().parse().isEmpty()) {
            String lista = "AND ambientesub.id_ambiente IN (";

            for (int j = 0; j < filtri.getId_ambiente().parse().size(); j++) {
                lista = lista + "?" + i + ",";
                i++;
            }
            lista = lista.substring(0, lista.length() - 1) + ")";

            parte2 = parte2 + lista;
        }

        confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " entesub.id_ente, " + parte2;

        if (filtri.getId_ente().parse() != null) {
            confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " AND entesub.id_ente = ?" + i++;
        }
        if (filtri.getId_strut().parse() != null) {
            confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " AND strut.id_strut = ?" + i++;
        }

        confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " ) tmp " + "join sacer.org_ente ente "
                + "    on (ente.id_ente = tmp.id_ente) " + "join sacer.org_ambiente amb "
                + "    on (amb.id_ambiente = ente.id_ambiente) ";

        if (filtri.getDifferenza_zero().parse() != null) {
            if (filtri.getDifferenza_zero().parse().equals("1")) {
                confrontoConteggiNativeQuery = confrontoConteggiNativeQuery
                        + " WHERE  (tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                        + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) != 0 ";
            } else {
                confrontoConteggiNativeQuery = confrontoConteggiNativeQuery
                        + " WHERE (tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                        + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) = 0 ";
            }
        }

        confrontoConteggiNativeQuery = confrontoConteggiNativeQuery
                + "order by  amb.nm_ambiente, ente.nm_ente, tmp.nm_strut ";

        Query query = entityManager.createNativeQuery(confrontoConteggiNativeQuery);

        String valDa = new SimpleDateFormat("dd-MM-yyyy").format(filtri.getDt_rif_da().parse());
        String valA = new SimpleDateFormat("dd-MM-yyyy").format(filtri.getDt_rif_a().parse());

        query.setParameter(1, valDa);
        query.setParameter(2, valA);

        List<BigDecimal> idAmbList = filtri.getId_ambiente().parse();
        for (BigDecimal idAmb : idAmbList) {
            query.setParameter(k, idAmb);
            k++;
        }

        if (filtri.getId_ente().parse() != null) {
            query.setParameter(k++, filtri.getId_ente().parse());
        }
        if (filtri.getId_strut().parse() != null) {
            query.setParameter(k++, filtri.getId_strut().parse());
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();
        Long totaleNiCompNeiSip = 0L;
        Long totaleNiCompNeiSipAnnul = 0L;
        Long totaleNiCompPresaInCarico = 0L;
        Long totaleNiCompAipInAggiorn = 0L;
        Long totaleNiCompAipGen = 0L;
        Long totaleNiCompInVolume = 0L;
        Long totaleNiCompDelta = 0L;

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setString("nm_ambiente", (String) totali[1]);
                riga.setString("nm_ente", (String) totali[2]);
                riga.setString("nm_strut", (String) totali[3]);
                riga.setBigDecimal("ni_comp_vers", (BigDecimal) totali[4]); // A
                riga.setBigDecimal("ni_comp_aip_generato", (BigDecimal) totali[5]);
                riga.setBigDecimal("ni_comp_aip_in_aggiorn", (BigDecimal) totali[6]);
                riga.setBigDecimal("ni_comp_presa_in_carico", (BigDecimal) totali[7]);
                riga.setBigDecimal("ni_comp_in_volume", (BigDecimal) totali[8]);
                riga.setBigDecimal("ni_comp_annul", (BigDecimal) totali[9]);
                BigDecimal deltaParte1 = riga.getBigDecimal("ni_comp_vers")
                        .subtract(riga.getBigDecimal("ni_comp_annul"));
                BigDecimal deltaParte2 = riga.getBigDecimal("ni_comp_presa_in_carico")
                        .add(riga.getBigDecimal("ni_comp_aip_in_aggiorn"))
                        .add(riga.getBigDecimal("ni_comp_aip_generato")).add(riga.getBigDecimal("ni_comp_in_volume"));
                riga.setBigDecimal("ni_comp_delta", deltaParte1.subtract(deltaParte2));
                riga.setBigDecimal("ni_comp_aip_generato_piu_annul",
                        ((BigDecimal) totali[5]).add((BigDecimal) totali[9]));
                tabella.add(riga);
                // Aggiungo i totali
                totaleNiCompAipGen = totaleNiCompAipGen + riga.getBigDecimal("ni_comp_aip_generato").longValue();
                totaleNiCompNeiSip = totaleNiCompNeiSip + riga.getBigDecimal("ni_comp_vers").longValue();
                totaleNiCompNeiSipAnnul = totaleNiCompNeiSipAnnul + riga.getBigDecimal("ni_comp_annul").longValue();
                totaleNiCompAipInAggiorn = totaleNiCompAipInAggiorn
                        + riga.getBigDecimal("ni_comp_aip_in_aggiorn").longValue();
                totaleNiCompInVolume = totaleNiCompInVolume + riga.getBigDecimal("ni_comp_in_volume").longValue();
                totaleNiCompPresaInCarico = totaleNiCompPresaInCarico
                        + riga.getBigDecimal("ni_comp_presa_in_carico").longValue();
                totaleNiCompDelta = totaleNiCompDelta + riga.getBigDecimal("ni_comp_delta").longValue();

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        // Aggiungo in fondo una riga relativa ai totali
        BaseRow rigaTotali = new BaseRow();
        rigaTotali.setBigDecimal("totale_ni_comp_aip_generato", new BigDecimal(totaleNiCompAipGen));
        rigaTotali.setBigDecimal("totale_ni_comp_aip_in_aggiorn", new BigDecimal(totaleNiCompAipInAggiorn));
        rigaTotali.setBigDecimal("totale_ni_comp_annul", new BigDecimal(totaleNiCompNeiSipAnnul));
        rigaTotali.setBigDecimal("totale_ni_comp_in_volume", new BigDecimal(totaleNiCompInVolume));
        rigaTotali.setBigDecimal("totale_ni_comp_presa_in_carico", new BigDecimal(totaleNiCompPresaInCarico));
        rigaTotali.setBigDecimal("totale_ni_comp_vers", new BigDecimal(totaleNiCompNeiSip));
        rigaTotali.setBigDecimal("totale_ni_comp_delta", new BigDecimal(totaleNiCompDelta));

        Object[] risultato = new Object[2];
        risultato[0] = tabella;
        risultato[1] = rigaTotali;

        return risultato;
    }

    public String getListaTotaliConsistenzaCompDiffDayByDay(FiltriConsistenzaSacer filtri, Calendar start)
            throws EMFError {
        /*
         * Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW
         */
        String confrontoConteggiNativeQuery = "SELECT id_strut, amb.nm_ambiente, "
                + "ente.nm_ente, tmp.nm_strut, tmp.ni_comp_vers, tmp.ni_comp_aip_generato, "
                + "tmp.ni_comp_aip_in_aggiorn, tmp.ni_comp_presa_in_carico, tmp.ni_comp_in_volume, tmp.ni_comp_annul, "
                + "(tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) delta_versati_gestiti "
                + "FROM (SELECT id_strut, nm_strut, ";

        String parte2 = " (SELECT nvl(sum (ni_comp_vers + ni_comp_agg), 0) "
                + "FROM sacer.MON_V_RIC_CONTA_UD_DOC_COMP conta_ud "
                + "WHERE conta_ud.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') "
                + " AND conta_ud.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') AND conta_ud.id_strut = strut.id_strut "
                + ") ni_comp_vers, " + "     (select nvl(sum(ni_comp_aip_generato), 0)"
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_generato,"
                + "     (select nvl(sum(ni_comp_aip_in_aggiorn), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_in_aggiorn, " + "      "
                + "     (select nvl(sum(ni_comp_presa_in_carico), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_presa_in_carico, " + "      "
                + "     (select nvl(sum(ni_comp_in_volume), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_in_volume, " + "      "
                + "     (select nvl(sum(ni_comp_annul), 0) "
                + "      from sacer.MON_V_RIC_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_annul "
                + "    from org_strut strut " + " join sacer.org_ente entesub " + "on(entesub.id_ente = strut.id_ente) "
                + "    join sacer.org_ambiente ambientesub " + "on(ambientesub.id_ambiente = entesub.id_ambiente) "
                + "    where strut.fl_template = '0' AND ambientesub.id_ambiente = ?3 ";

        confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " entesub.id_ente, " + parte2;

        if (filtri.getId_ente().parse() != null) {
            confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " AND entesub.id_ente = ?4 ";
        }
        if (filtri.getId_strut().parse() != null) {
            confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " AND strut.id_strut = ?5 ";
        }

        confrontoConteggiNativeQuery = confrontoConteggiNativeQuery + " ) tmp " + "join sacer.org_ente ente "
                + "    on (ente.id_ente = tmp.id_ente) " + "join sacer.org_ambiente amb "
                + "    on (amb.id_ambiente = ente.id_ambiente) " + "  "
                + "order by  amb.nm_ambiente, ente.nm_ente, tmp.nm_strut ";

        Query query = entityManager.createNativeQuery(confrontoConteggiNativeQuery);

        String valDa = new SimpleDateFormat("dd-MM-yyyy").format(start.getTime());
        String valA = new SimpleDateFormat("dd-MM-yyyy").format(start.getTime());

        query.setParameter(1, valDa);
        query.setParameter(2, valA);

        query.setParameter(3, filtri.getId_ambiente().parse());
        if (filtri.getId_ente().parse() != null) {
            query.setParameter(4, filtri.getId_ente().parse());
        }
        if (filtri.getId_strut().parse() != null) {
            query.setParameter(5, filtri.getId_strut().parse());
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();
        StringBuilder errore = new StringBuilder();

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setString("nm_ambiente", (String) totali[1]);
                riga.setString("nm_ente", (String) totali[2]);
                riga.setString("nm_strut", (String) totali[3]);
                riga.setBigDecimal("ni_comp_vers", (BigDecimal) totali[4]);
                riga.setBigDecimal("ni_comp_aip_generato", (BigDecimal) totali[5]);
                riga.setBigDecimal("ni_comp_aip_in_aggiorn", (BigDecimal) totali[6]);
                riga.setBigDecimal("ni_comp_presa_in_carico", (BigDecimal) totali[7]);
                riga.setBigDecimal("ni_comp_in_volume", (BigDecimal) totali[8]);
                riga.setBigDecimal("ni_comp_annul", (BigDecimal) totali[9]);
                BigDecimal deltaParte1 = riga.getBigDecimal("ni_comp_vers")
                        .subtract(riga.getBigDecimal("ni_comp_annul"));
                BigDecimal deltaParte2 = riga.getBigDecimal("ni_comp_presa_in_carico")
                        .add(riga.getBigDecimal("ni_comp_aip_in_aggiorn"))
                        .add(riga.getBigDecimal("ni_comp_aip_generato")).add(riga.getBigDecimal("ni_comp_in_volume"));
                riga.setBigDecimal("ni_comp_delta", deltaParte1.subtract(deltaParte2));
                riga.setBigDecimal("ni_comp_aip_generato_piu_annul",
                        ((BigDecimal) totali[5]).add((BigDecimal) totali[9]));
                if (riga.getBigDecimal("ni_comp_delta").compareTo(BigDecimal.ZERO) != 0) {
                    errore.append(valDa).append(" <br>");
                }
                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return errore.toString();
    }

    public BaseTable getListaDifferenzaConsistenzaComp(BigDecimal idStrut, Date dtRifContaDa, Date dtRifContaA) {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT a.*, "
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO-b.TOT_COMP_VERSA_CONTATI AS differenza, "
                + "  b.id_strut  as id_strut_b, " + "  b.id_sub_strut, " + "  b.dt_rif_conta, "
                + "  b.tot_comp_versa_contati " + "  FROM (  SELECT /*+ parallel (12) */ "
                + "  TRUNC (doc.dt_creazione)     AS data_creazione, "
                + "                   COUNT (1)                    AS tot_comp_versati_in_archivio, "
                + "                   ud.aa_key_unita_doc, " + "                   ud.id_strut, "
                + "                   ud.id_tipo_unita_doc, " + "                   doc_princ.id_tipo_doc, "
                + "                   ud.ID_REGISTRO_UNITA_DOC " + "              FROM aro_unita_doc ud "
                + "                   JOIN aro_doc doc_princ "
                + "                       ON (    doc_princ.id_unita_doc = ud.id_unita_doc "
                + "                           AND doc_princ.ti_doc = 'PRINCIPALE') "
                + "                   JOIN aro_doc doc ON (doc.id_unita_doc = ud.id_unita_doc) "
                + "                   JOIN aro_strut_doc strut_doc "
                + "                       ON (strut_doc.id_doc = doc.id_doc) "
                + "                   JOIN aro_comp_doc comp "
                + "                       ON (comp.id_strut_doc = strut_doc.id_strut_doc) "
                + "             WHERE ud.id_strut = ?1  "
                // + " AND ud.id_sub_strut = ?2 "
                // + " AND TRUNC (doc.dt_creazione) <= TO_DATE (?2, 'dd/mm/yyyy') "
                + "             AND TRUNC (doc.dt_creazione) BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "             GROUP BY TRUNC (doc.dt_creazione), " + "                   aa_key_unita_doc, "
                + "                   ud.id_strut, " + "                   ud.id_tipo_unita_doc, "
                + "                   doc_princ.id_tipo_doc, " + "                   ud.ID_REGISTRO_UNITA_DOC) a "
                + "         JOIN " + "         (  SELECT /*+ parallel (12) */ id_strut, "
                + "                   id_sub_strut, " + "                   dt_rif_conta, "
                + "                   SUM (ni_comp_vers + ni_comp_Agg)    AS tot_comp_versa_contati, "
                + "                   aa_key_unita_doc, " + "                   id_tipo_unita_doc, "
                + "                   id_tipo_doc_princ, " + "                   id_REGISTRO_UNITA_DOC "
                + "            FROM mon_v_ric_conta_ud_doc_comp " + "            WHERE id_strut = ?1  "
                // + " AND id_sub_strut = ?2 "
                // + " AND dt_rif_conta <= TO_DATE (?2, 'dd/mm/yyyy') "
                + "            AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "              GROUP BY id_strut, " + "                   id_sub_strut, "
                + "                   dt_rif_conta, " + "                   aa_key_unita_doc, "
                + "                   id_tipo_unita_doc, " + "                   id_tipo_doc_princ, "
                + "                   id_REGISTRO_UNITA_DOC) b " + "             ON     a.id_strut = b.id_strut "
                + "                AND a.aa_key_unita_doc = b.aa_key_unita_doc "
                + "                AND a.id_tipo_unita_doc = b.id_tipo_unita_doc "
                + "                AND a.data_creazione = b.dt_rif_conta "
                + "                and a.ID_TIPO_DOC=b.ID_TIPO_DOC_PRINC "
                + "                and a.ID_REGISTRO_UNITA_DOC = b.ID_REGISTRO_UNITA_DOC "
                + "WHERE (a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) <> 0 "
                + "ORDER BY a.data_creazione ";

        Query query = entityManager.createNativeQuery(differenzaNativeQuery);

        query.setParameter(1, idStrut.longValue());
        String dtRifContaToStringDa = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaDa);
        String dtRifContaToStringA = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaA);
        query.setParameter(2, dtRifContaToStringDa);
        query.setParameter(3, dtRifContaToStringA);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setObject("data_creazione", totali[0]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[1]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[2]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[6]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[7]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[9]);
                riga.setObject("dt_rif_conta", totali[10]);
                riga.setBigDecimal("tot_comp_versa_contati", (BigDecimal) totali[11]);

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            rigaMod.setString("nm_sub_strut", entityManager
                    .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            rigaMod.setString("nm_tipo_doc", entityManager
                    .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }

        return tabella;
    }

    public BaseTable getListaDifferenzaConsistenzaVsCalcoloSacer(BigDecimal idStrut, Date dtRifContaDa,
            Date dtRifContaA) {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza,"
                + "  b.id_strut as id_Strut_b," + "  b.dt_rif_conta as dt_rif_conta_b," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_aip_in_aggiorn+ni_comp_aip_generato+ni_comp_presa_in_carico+ni_comp_in_volume+ni_comp_annul)    AS TOT_COMP_VERSATI_IN_ARCHIVIO,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   id_tipo_doc_princ," + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_v_ric_conta_by_stato_conserv_new"
                + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   dt_rif_conta,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   id_tipo_doc_princ," + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut) a" + "                    JOIN"
                + "         (  SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_vers + ni_comp_Agg)    AS tot_comp_versa_contati,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   id_tipo_doc_princ," + "                   id_REGISTRO_UNITA_DOC"
                + "              FROM mon_v_ric_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   id_sub_strut,"
                + "                   dt_rif_conta," + "                   aa_key_unita_doc,"
                + "                   id_tipo_unita_doc," + "                   id_tipo_doc_princ,"
                + "                   id_REGISTRO_UNITA_DOC) b" + "             ON     a.id_strut = b.id_strut"
                + "                AND a.aa_key_unita_doc = b.aa_key_unita_doc"
                + "                AND a.id_tipo_unita_doc = b.id_tipo_unita_doc"
                + "                AND a.dt_rif_conta = b.dt_rif_conta"
                + "                and a.ID_TIPO_DOC_PRINC=b.ID_TIPO_DOC_PRINC"
                + "                and a.ID_REGISTRO_UNITA_DOC = b.ID_REGISTRO_UNITA_DOC"
                + "  WHERE (a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) <> 0 "
                + "ORDER BY a.dt_rif_conta ";

        Query query = entityManager.createNativeQuery(differenzaNativeQuery);

        query.setParameter(1, idStrut.longValue());
        String dtRifContaToStringDa = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaDa);
        String dtRifContaToStringA = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaA);
        query.setParameter(2, dtRifContaToStringDa);
        query.setParameter(3, dtRifContaToStringA);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setObject("data_creazione", totali[1]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[2]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[6]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[8]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[7]);
                riga.setObject("dt_rif_conta", totali[1]);
                riga.setBigDecimal("tot_comp_versa_contati", (BigDecimal) totali[11]);

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            rigaMod.setString("nm_sub_strut", entityManager
                    .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            rigaMod.setString("nm_tipo_doc", entityManager
                    .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }

        return tabella;
    }

    public BaseTable getListaDifferenzaConsistenzaVsCalcoloSacer2(BigDecimal idStrut, Date dtRifContaDa,
            Date dtRifContaA) {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza,"
                + "  b.id_strut as id_strut_b," + "  b.dt_rif_conta as dt_rif_conta_b," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_aip_in_aggiorn+ni_comp_aip_generato+ni_comp_presa_in_carico+ni_comp_in_volume+ni_comp_annul)    AS TOT_COMP_VERSATI_IN_ARCHIVIO,"
                // + " COUNT (1) AS tot_comp_versati_in_archivio, "
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_v_ric_conta_by_stato_conserv_new"
                + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   dt_rif_conta,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut) a" + "                    left JOIN"
                + "         (  SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_vers + ni_comp_Agg)    AS tot_comp_versa_contati,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC"
                + "              FROM mon_v_ric_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   id_sub_strut,"
                + "                   dt_rif_conta," + "                   aa_key_unita_doc,"
                + "                   id_tipo_unita_doc," + "                   "
                + "                   id_REGISTRO_UNITA_DOC) b" + "             ON     a.id_strut = b.id_strut"
                + "                AND a.aa_key_unita_doc = b.aa_key_unita_doc"
                + "                AND a.id_tipo_unita_doc = b.id_tipo_unita_doc"
                + "                AND a.dt_rif_conta = b.dt_rif_conta" + "                "
                + "                and a.ID_REGISTRO_UNITA_DOC = b.ID_REGISTRO_UNITA_DOC"
                + "  WHERE ((a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) <> 0 OR (a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) IS NULL) "
                + "ORDER BY a.dt_rif_conta ";

        Query query = entityManager.createNativeQuery(differenzaNativeQuery);

        query.setParameter(1, idStrut.longValue());
        String dtRifContaToStringDa = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaDa);
        String dtRifContaToStringA = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaA);
        query.setParameter(2, dtRifContaToStringDa);
        query.setParameter(3, dtRifContaToStringA);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setObject("data_creazione", (Timestamp) totali[1]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[2]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[7]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[6]);
                riga.setObject("dt_rif_conta", (Timestamp) totali[1]);
                riga.setBigDecimal("tot_comp_versa_contati", (BigDecimal) totali[10]);

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            if (rigaMod.getBigDecimal("id_sub_strut") != null) {
                rigaMod.setString("nm_sub_strut", entityManager
                        .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            }
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }

        return tabella;
    }//

    public Calendar getLastPositiveRunCalcoloContenutoSacer() {
        String queryStr = "SELECT logJob.dtRegLogJob FROM LogJob logJob "
                + "WHERE logJob.nmJob = 'CALCOLO_CONTENUTO_SACER' " + "AND logJob.tiRegLogJob = 'FINE_SCHEDULAZIONE' "
                + "ORDER BY logJob.dtRegLogJob DESC ";
        Query query = entityManager.createQuery(queryStr).setMaxResults(1);
        List<Date> data = query.getResultList();
        Calendar cal = Calendar.getInstance();
        if (!data.isEmpty()) {
            cal.setTime(data.get(0));
        } else {
            cal.set(2011, Calendar.DECEMBER, 1);
        }
        return cal;
    }

    // getListaDifferenzaConsistenzaVsCalcoloSacerConTipoDoc
    public BaseTable getListaDifferenzaConsistenzaVsCalcoloSacerConTipoDoc(BigDecimal idStrut, Date dtRifContaDa,
            Date dtRifContaA) {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza," + "  b.id_strut,"
                + "  b.dt_rif_conta," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   COUNT (1)                    AS tot_comp_versati_in_archivio, "
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc, id_tipo_doc_princ, "
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_v_ric_conta_by_stato_conserv_new"
                + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   dt_rif_conta,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc, id_tipo_doc_princ,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut) a" + "                    left JOIN"
                + "         (  SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_vers + ni_comp_Agg)    AS tot_comp_versa_contati,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC"
                + "              FROM mon_v_ric_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
                + "                   AND dt_rif_conta BETWEEN TO_DATE (?2, 'dd/mm/yyyy') AND TO_DATE (?3, 'dd/mm/yyyy') "
                + "          GROUP BY id_strut," + "                   id_sub_strut,"
                + "                   dt_rif_conta," + "                   aa_key_unita_doc,"
                + "                   id_tipo_unita_doc, id_tipo_doc_princ, " + "                   "
                + "                   id_REGISTRO_UNITA_DOC) b" + "             ON     a.id_strut = b.id_strut"
                + "                AND a.aa_key_unita_doc = b.aa_key_unita_doc"
                + "                AND a.id_tipo_unita_doc = b.id_tipo_unita_doc"
                + "                AND a.dt_rif_conta = b.dt_rif_conta "
                + "                and a.ID_TIPO_DOC_princ=b.ID_TIPO_DOC_PRINC " + "                "
                + "                and a.ID_REGISTRO_UNITA_DOC = b.ID_REGISTRO_UNITA_DOC"
                + "  WHERE ((a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) <> 0 OR (a.tot_comp_versati_in_archivio - b.tot_comp_versa_contati) IS NULL) "
                + "ORDER BY a.dt_rif_conta ";

        Query query = entityManager.createNativeQuery(differenzaNativeQuery);

        query.setParameter(1, idStrut.longValue());
        String dtRifContaToStringDa = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaDa);
        String dtRifContaToStringA = new SimpleDateFormat("dd/MM/yyyy").format(dtRifContaA);
        query.setParameter(2, dtRifContaToStringDa);
        query.setParameter(3, dtRifContaToStringA);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<Object[]> listaTotSacer = query.getResultList();
        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        for (Object[] totali : listaTotSacer) {
            try {
                riga.setObject("data_creazione", (Timestamp) totali[1]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[2]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[7]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[6]);
                riga.setObject("dt_rif_conta", (Timestamp) totali[1]);
                riga.setBigDecimal("tot_comp_versa_contati", (BigDecimal) totali[10]);

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            rigaMod.setString("nm_sub_strut", entityManager
                    .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }

        return tabella;
    }

    public MonVLisUdNonVersIamTableBean getMonVLisUdNonVersIamViewBeanScaricaContenuto(
            MonitoraggioFiltriListaVersFallitiDistintiDocBean filtri, Integer maxResult) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u FROM MonVLisUdNonVersIam u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtri.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtri.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtri.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.monVLisUdNonVersIamId.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        String flVerificato = filtri.getFlVerificato();
        if (flVerificato != null) {
            queryStr.append(whereWord).append("u.flVerif = :flverificato ");
            whereWord = "AND ";
        }

        String flNonRisolub = filtri.getFlNonRisolub();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flNonRisolub = :flnonrisolub ");
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date dataFirstOrarioDa = (filtri.getGiornoFirstVersDaValidato() != null ? filtri.getGiornoFirstVersDaValidato()
                : null);
        Date dataFirstOrarioA = (filtri.getGiornoFirstVersAValidato() != null ? filtri.getGiornoFirstVersAValidato()
                : null);

        Date dataLastOrarioDa = (filtri.getGiornoLastVersDaValidato() != null ? filtri.getGiornoLastVersDaValidato()
                : null);
        Date dataLastOrarioA = (filtri.getGiornoLastVersAValidato() != null ? filtri.getGiornoLastVersAValidato()
                : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataFirstOrarioDa != null) && (dataFirstOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtFirstSesErr between :datafirstda AND :datafirsta ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((dataLastOrarioDa != null) && (dataLastOrarioA != null)) {
            queryStr.append(whereWord).append("u.dtLastSesErr between :datalastda AND :datalasta ");
            whereWord = "AND ";
        }

        // // Inserimento nella query del filtro data già impostato con data e ora
        // if ((dataOrarioDa != null) && (dataOrarioA != null)) {
        // queryStr.append(whereWord).append("u.dtLastSesErr between :datada AND :dataa ");
        // whereWord = "AND ";
        // }
        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in versione multiselect
        Set<String> registroSet = filtri.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.monVLisUdNonVersIamId.cdRegistroKeyUnitaDoc IN :setregistro)");
            whereWord = " AND ";
        }

        BigDecimal anno = filtri.getAnno();
        String codice = filtri.getNumero();

        if (anno != null) {
            queryStr.append(whereWord).append("u.monVLisUdNonVersIamId.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.monVLisUdNonVersIamId.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC range con registro in
        // versione multiselect
        BigDecimal annoRangeDa = filtri.getAnno_range_da();
        BigDecimal annoRangeA = filtri.getAnno_range_a();
        String codiceRangeDa = filtri.getNumero_range_da();
        String codiceRangeA = filtri.getNumero_range_a();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord)
                    .append("(u.monVLisUdNonVersIamId.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append(
                    "FUNCTION('lpad', u.monVLisUdNonVersIamId.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        if (StringUtils.isNotBlank(filtri.getClasseErrore()) || StringUtils.isNotBlank(filtri.getSottoClasseErrore())
                || StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            queryStr.append(whereWord)
                    .append(" EXISTS (" + "SELECT err " + "FROM VrsErrSessioneVersKo err "
                            + "JOIN err.vrsDatiSessioneVersKo dati " + "JOIN dati.vrsSessioneVersKo ses "
                            + "WHERE ses.tiSessioneVers = 'VERSAMENTO' "
                            + "AND ses.orgStrut.idStrut = u.monVLisUdNonVersIamId.idStrut "
                            + "AND ses.cdRegistroKeyUnitaDoc = u.monVLisUdNonVersIamId.cdRegistroKeyUnitaDoc "
                            + "AND ses.aaKeyUnitaDoc = u.monVLisUdNonVersIamId.aaKeyUnitaDoc "
                            + "AND ses.cdKeyUnitaDoc = u.monVLisUdNonVersIamId.cdKeyUnitaDoc " + "AND ");
            if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
                queryStr.append("err.cdErr = :codErr");
            } else if (StringUtils.isNotBlank(filtri.getClasseErrore())
                    || StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
                queryStr.append("err.cdErr like :codErr");
            }
            queryStr.append(" )");
        }

        // Ordina per ambiente, ente, struttura e chiave di ordinamento
        queryStr.append("ORDER BY u.nmAmbiente, u.nmEnte, u.nmStrut, u.dsKeyOrd");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (flVerificato != null) {
            query.setParameter("flverificato", flVerificato);
        }

        if (flNonRisolub != null) {
            query.setParameter("flnonrisolub", flNonRisolub);
        }

        if (dataFirstOrarioDa != null && dataFirstOrarioA != null) {
            query.setParameter("datafirstda", dataFirstOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("datafirsta", dataFirstOrarioA, TemporalType.TIMESTAMP);
        }

        if (dataLastOrarioDa != null && dataLastOrarioA != null) {
            query.setParameter("datalastda", dataLastOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter("datalasta", dataLastOrarioA, TemporalType.TIMESTAMP);
        }

        if (registroSet != null && !registroSet.isEmpty()) {
            query.setParameter("setregistro", registroSet);
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

        query.setParameter("idUserIam", filtri.getIdUserIam());
        if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            query.setParameter("codErr", filtri.getCodiceErrore());
        } else if (StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
            query.setParameter("codErr", filtri.getSottoClasseErrore() + "%");
        } else if (StringUtils.isNotBlank(filtri.getClasseErrore())) {
            query.setParameter("codErr", filtri.getClasseErrore() + "%");
        }

        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisUdNonVersIam> listaUdNonVers = query.getResultList();

        MonVLisUdNonVersIamTableBean monTableBean = new MonVLisUdNonVersIamTableBean();

        try {
            for (MonVLisUdNonVersIam row : listaUdNonVers) {
                MonVLisUdNonVersIamRowBean rb = (MonVLisUdNonVersIamRowBean) Transform.entity2RowBean(row);
                rb.setString("nm_strut", row.getNmAmbiente() + ", " + row.getNmEnte() + ", " + row.getNmStrut());
                if (rb.getFlVerif() != null) {
                    rb.setFlVerif(rb.getFlVerif().equals("1") ? "SI" : "NO");
                }
                if (rb.getFlNonRisolub() != null) {
                    rb.setFlNonRisolub(rb.getFlNonRisolub().equals("1") ? "SI" : "NO");
                }
                monTableBean.add(rb);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return monTableBean;
    }

    public MonVLisDocNonVersIamTableBean getMonVLisDocNonVersIamViewBeanScaricaContenuto(
            MonitoraggioFiltriListaVersFallitiDistintiDocBean filtri, Integer maxResult) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u FROM MonVLisDocNonVersIam u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtri.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtri.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtri.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.monVLisDocNonVersIamId.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        String flVerificato = filtri.getFlVerificato();
        if (flVerificato != null) {
            queryStr.append(whereWord).append("u.flVerif = :flverificato ");
            whereWord = "AND ";
        }

        String flNonRisolub = filtri.getFlNonRisolub();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flNonRisolub = :flnonrisolub ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in
        // versione multiselect
        Set<String> registroSet = filtri.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.monVLisDocNonVersIamId.cdRegistroKeyUnitaDoc IN :setregistro)");
            whereWord = " AND ";
        }

        BigDecimal anno = filtri.getAnno();
        String codice = filtri.getNumero();

        if (anno != null) {
            queryStr.append(whereWord).append("u.monVLisDocNonVersIamId.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.monVLisDocNonVersIamId.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        BigDecimal annoRangeDa = filtri.getAnno_range_da();
        BigDecimal annoRangeA = filtri.getAnno_range_a();
        String codiceRangeDa = filtri.getNumero_range_da();
        String codiceRangeA = filtri.getNumero_range_a();

        if (annoRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord)
                    .append("(u.monVLisDocNonVersIamId.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12, StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append(
                    "FUNCTION('lpad', u.monVLisDocNonVersIamId.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        if (StringUtils.isNotBlank(filtri.getClasseErrore()) || StringUtils.isNotBlank(filtri.getSottoClasseErrore())
                || StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            queryStr.append(whereWord).append(" EXISTS (" + "SELECT err " + "FROM VrsErrSessioneVersKo err "
                    + "JOIN err.vrsDatiSessioneVersKo dati " + "JOIN dati.vrsSessioneVersKo ses "
                    + "WHERE ses.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' " + "AND ses.orgStrut.idStrut = u.idStrut "
                    + "AND ses.cdRegistroKeyUnitaDoc = u.monVLisDocNonVersIamId.cdRegistroKeyUnitaDoc "
                    + "AND ses.aaKeyUnitaDoc = u.monVLisDocNonVersIamId.aaKeyUnitaDoc "
                    + "AND ses.cdKeyUnitaDoc = u.monVLisDocNonVersIamId.cdKeyUnitaDoc "
                    + "AND ses.cdKeyDocVers = u.monVLisDocNonVersIamId.cdKeyDocVers " + "AND ");
            if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
                queryStr.append("err.cdErr = :codErr");
            } else if (StringUtils.isNotBlank(filtri.getClasseErrore())
                    || StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
                queryStr.append("err.cdErr like :codErr");
            }
            queryStr.append(" )");
        }

        // Ordina per ambiente, ente, struttura e chiave di ordinamento
        queryStr.append(
                "ORDER BY u.nmAmbiente, u.nmEnte, u.nmStrut, u.dsKeyOrd, u.monVLisDocNonVersIamId.cdKeyDocVers");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (flVerificato != null) {
            query.setParameter("flverificato", flVerificato);
        }

        if (flNonRisolub != null) {
            query.setParameter("flnonrisolub", flNonRisolub);
        }

        if (registroSet != null && !registroSet.isEmpty()) {
            query.setParameter("setregistro", registroSet);
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

        query.setParameter("idUserIam", filtri.getIdUserIam());
        if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            query.setParameter("codErr", filtri.getCodiceErrore());
        } else if (StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
            query.setParameter("codErr", filtri.getSottoClasseErrore() + "%");
        } else if (StringUtils.isNotBlank(filtri.getClasseErrore())) {
            query.setParameter("codErr", filtri.getClasseErrore() + "%");
        }

        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisDocNonVersIam> listaDocNonVers = query.getResultList();

        MonVLisDocNonVersIamTableBean monTableBean = new MonVLisDocNonVersIamTableBean();

        try {
            if (listaDocNonVers != null && !listaDocNonVers.isEmpty()) {
                monTableBean = (MonVLisDocNonVersIamTableBean) Transform.entities2TableBean(listaDocNonVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Struttura per presentarlo a video come unico campo dall'unione di ambiente, ente e
         * struttura
         */
        for (MonVLisDocNonVersIamRowBean row : monTableBean) {
            row.setString("nm_strut", row.getNmAmbiente() + ", " + row.getNmEnte() + ", " + row.getNmStrut());
            if (row.getFlVerif() != null) {
                row.setFlVerif(row.getFlVerif().equals("1") ? "SI" : "NO");
            }
            if (row.getFlNonRisolub() != null) {
                row.setFlNonRisolub(row.getFlNonRisolub().equals("1") ? "SI" : "NO");
            }
        }

        return monTableBean;
    }

    static class FiltriOperazioniVolumiPlain {

        private BigDecimal idAmbiente;
        private BigDecimal idEnte;
        private BigDecimal idStrut;
        private Date dataOrarioDa;
        private Date dataOrarioA;
        private String tiModOper;
        private BigDecimal idVolumeConserv;
        private String flCreaVolume;
        private String flRecuperaVolumeAperto;
        private String flAggiungiDocVolume;
        private String flRecuperaVolumeScaduto;
        private String flSetVolumeDaChiudere;
        private String flSetVolumeAperto;
        private String flInizioCreaIndice;
        private String flRecuperaVolumeInErrore;
        private String flCreaIndiceVolume;
        private String flMarcaIndiceVolume;
        private String flSetVolumeInErrore;
        private String flInizioVerifFirme;
        private String flChiusuraVolume;
        private String flErrVerifFirme;
        private String flRimuoviDocVolume;
        private String flEliminaVolume;
        private String flModificaVolume;
        private String flFirmaNoMarcaVolume;
        private String flFirmaVolume;
        private String tiOutput;

        private FiltriOperazioniVolumiPlain(FiltriOperazioniVolumi filtriOV, Date dataOrarioDa, Date dataOrarioA)
                throws EMFError {
            this.idAmbiente = filtriOV.getId_ambiente().parse();
            this.idEnte = filtriOV.getId_ente().parse();
            this.idStrut = filtriOV.getId_strut().parse();
            this.dataOrarioDa = dataOrarioDa;
            this.dataOrarioA = dataOrarioA;
            this.tiModOper = filtriOV.getTi_mod_oper().parse();
            this.idVolumeConserv = filtriOV.getId_volume_conserv().parse();
            this.flCreaVolume = filtriOV.getFl_oper_crea_volume().parse();
            this.flRecuperaVolumeAperto = filtriOV.getFl_oper_recupera_volume_aperto().parse();
            this.flAggiungiDocVolume = filtriOV.getFl_oper_aggiungi_doc_volume().parse();
            this.flRecuperaVolumeScaduto = filtriOV.getFl_oper_recupera_volume_scaduto().parse();
            this.flSetVolumeDaChiudere = filtriOV.getFl_oper_set_volume_da_chiudere().parse();
            this.flSetVolumeAperto = filtriOV.getFl_oper_set_volume_aperto().parse();
            this.flInizioCreaIndice = filtriOV.getFl_oper_inizio_crea_indice().parse();
            this.flRecuperaVolumeInErrore = filtriOV.getFl_oper_recupera_volume_in_errore().parse();
            this.flCreaIndiceVolume = filtriOV.getFl_oper_crea_indice_volume().parse();
            this.flMarcaIndiceVolume = filtriOV.getFl_oper_marca_indice_volume().parse();
            this.flSetVolumeInErrore = filtriOV.getFl_oper_set_volume_in_errore().parse();
            this.flInizioVerifFirme = filtriOV.getFl_oper_inizio_verif_firme().parse();
            this.flChiusuraVolume = filtriOV.getFl_oper_chiusura_volume().parse();
            this.flErrVerifFirme = filtriOV.getFl_oper_err_verif_firme().parse();
            this.flRimuoviDocVolume = filtriOV.getFl_oper_rimuovi_doc_volume().parse();
            this.flEliminaVolume = filtriOV.getFl_oper_elimina_volume().parse();
            this.flModificaVolume = filtriOV.getFl_oper_modifica_volume().parse();
            this.flFirmaNoMarcaVolume = filtriOV.getFl_oper_firma_no_marca_volume().parse();
            this.flFirmaVolume = filtriOV.getFl_oper_firma_volume().parse();
            this.tiOutput = filtriOV.getTi_output().parse();
        }

        public FiltriOperazioniVolumiPlain() {

        }

        public BigDecimal getIdAmbiente() {
            return idAmbiente;
        }

        public BigDecimal getIdEnte() {
            return idEnte;
        }

        public BigDecimal getIdStrut() {
            return idStrut;
        }

        public Date getDataOrarioDa() {
            return dataOrarioDa;
        }

        public Date getDataOrarioA() {
            return dataOrarioA;
        }

        public String getTiModOper() {
            return tiModOper;
        }

        public BigDecimal getIdVolumeConserv() {
            return idVolumeConserv;
        }

        public String getFlCreaVolume() {
            return flCreaVolume;
        }

        public String getFlRecuperaVolumeAperto() {
            return flRecuperaVolumeAperto;
        }

        public String getFlAggiungiDocVolume() {
            return flAggiungiDocVolume;
        }

        public String getFlRecuperaVolumeScaduto() {
            return flRecuperaVolumeScaduto;
        }

        public String getFlSetVolumeDaChiudere() {
            return flSetVolumeDaChiudere;
        }

        public String getFlSetVolumeAperto() {
            return flSetVolumeAperto;
        }

        public String getFlInizioCreaIndice() {
            return flInizioCreaIndice;
        }

        public String getFlRecuperaVolumeInErrore() {
            return flRecuperaVolumeInErrore;
        }

        public String getFlCreaIndiceVolume() {
            return flCreaIndiceVolume;
        }

        public String getFlMarcaIndiceVolume() {
            return flMarcaIndiceVolume;
        }

        public String getFlSetVolumeInErrore() {
            return flSetVolumeInErrore;
        }

        public String getFlInizioVerifFirme() {
            return flInizioVerifFirme;
        }

        public String getFlChiusuraVolume() {
            return flChiusuraVolume;
        }

        public String getFlErrVerifFirme() {
            return flErrVerifFirme;
        }

        public String getFlRimuoviDocVolume() {
            return flRimuoviDocVolume;
        }

        public String getFlEliminaVolume() {
            return flEliminaVolume;
        }

        public String getFlModificaVolume() {
            return flModificaVolume;
        }

        public String getFlFirmaNoMarcaVolume() {
            return flFirmaNoMarcaVolume;
        }

        public String getFlFirmaVolume() {
            return flFirmaVolume;
        }

        public String getTiOutput() {
            return tiOutput;
        }

        public void setIdAmbiente(BigDecimal idAmbiente) {
            this.idAmbiente = idAmbiente;
        }

        public void setIdEnte(BigDecimal idEnte) {
            this.idEnte = idEnte;
        }

        public void setIdStrut(BigDecimal idStrut) {
            this.idStrut = idStrut;
        }

        public void setDataOrarioDa(Date dataOrarioDa) {
            this.dataOrarioDa = dataOrarioDa;
        }

        public void setDataOrarioA(Date dataOrarioA) {
            this.dataOrarioA = dataOrarioA;
        }

        public void setTiModOper(String tiModOper) {
            this.tiModOper = tiModOper;
        }

        public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
            this.idVolumeConserv = idVolumeConserv;
        }

        public void setFlCreaVolume(String flCreaVolume) {
            this.flCreaVolume = flCreaVolume;
        }

        public void setFlRecuperaVolumeAperto(String flRecuperaVolumeAperto) {
            this.flRecuperaVolumeAperto = flRecuperaVolumeAperto;
        }

        public void setFlAggiungiDocVolume(String flAggiungiDocVolume) {
            this.flAggiungiDocVolume = flAggiungiDocVolume;
        }

        public void setFlRecuperaVolumeScaduto(String flRecuperaVolumeScaduto) {
            this.flRecuperaVolumeScaduto = flRecuperaVolumeScaduto;
        }

        public void setFlSetVolumeDaChiudere(String flSetVolumeDaChiudere) {
            this.flSetVolumeDaChiudere = flSetVolumeDaChiudere;
        }

        public void setFlSetVolumeAperto(String flSetVolumeAperto) {
            this.flSetVolumeAperto = flSetVolumeAperto;
        }

        public void setFlInizioCreaIndice(String flInizioCreaIndice) {
            this.flInizioCreaIndice = flInizioCreaIndice;
        }

        public void setFlRecuperaVolumeInErrore(String flRecuperaVolumeInErrore) {
            this.flRecuperaVolumeInErrore = flRecuperaVolumeInErrore;
        }

        public void setFlCreaIndiceVolume(String flCreaIndiceVolume) {
            this.flCreaIndiceVolume = flCreaIndiceVolume;
        }

        public void setFlMarcaIndiceVolume(String flMarcaIndiceVolume) {
            this.flMarcaIndiceVolume = flMarcaIndiceVolume;
        }

        public void setFlSetVolumeInErrore(String flSetVolumeInErrore) {
            this.flSetVolumeInErrore = flSetVolumeInErrore;
        }

        public void setFlInizioVerifFirme(String flInizioVerifFirme) {
            this.flInizioVerifFirme = flInizioVerifFirme;
        }

        public void setFlChiusuraVolume(String flChiusuraVolume) {
            this.flChiusuraVolume = flChiusuraVolume;
        }

        public void setFlErrVerifFirme(String flErrVerifFirme) {
            this.flErrVerifFirme = flErrVerifFirme;
        }

        public void setFlRimuoviDocVolume(String flRimuoviDocVolume) {
            this.flRimuoviDocVolume = flRimuoviDocVolume;
        }

        public void setFlEliminaVolume(String flEliminaVolume) {
            this.flEliminaVolume = flEliminaVolume;
        }

        public void setFlModificaVolume(String flModificaVolume) {
            this.flModificaVolume = flModificaVolume;
        }

        public void setFlFirmaNoMarcaVolume(String flFirmaNoMarcaVolume) {
            this.flFirmaNoMarcaVolume = flFirmaNoMarcaVolume;
        }

        public void setFlFirmaVolume(String flFirmaVolume) {
            this.flFirmaVolume = flFirmaVolume;
        }

        public void setTiOutput(String tiOutput) {
            this.tiOutput = tiOutput;
        }
    }

    static class FiltriOperazioniElenchiVersamentoPlain {

        private BigDecimal idEnte;
        private BigDecimal idStrut;
        private Date dataOrarioDa;
        private Date dataOrarioA;
        private BigDecimal idAmbiente;
        private String tiModOper;
        private BigDecimal idElencoVers;
        private boolean flChiusuraElenco;
        private boolean flCreaElenco;
        private boolean flCreaIndiceElenco;
        private boolean flDefNoteElencoChiuso;
        private boolean flDefNoteIndiceElenco;
        private boolean flEliminaElenco;
        private boolean flFirmaElenco;
        private boolean flModElenco;
        private boolean flRecuperaElencoAperto;
        private boolean flRecuperaElencoScaduto;
        private boolean flRimuoviDocElenco;
        private boolean flRimuoviUdElenco;
        private boolean flSetElencoAperto;
        private boolean flSetElencoDaChiudere;
        private boolean flFirmaInCorso;
        private boolean flFirmaInCorsoFallita;
        private boolean flStartCreazioneElencoAip;
        private boolean flEndCreazioneElencoAip;
        private boolean flFirmaElencoAip;
        private boolean flFirmaElencoAipInCorso;
        private boolean flFirmaElencoAipFallita;
        private boolean flMarcaElencoAip;
        private boolean flMarcaElencoAipFallita;
        private String tiOutput;

        /**
         * @param filtriOE,
         *            i filtri di ricerca
         * @param dataOrarioDa,
         *            filtro data e ora inizio già validato
         * @param dataOrarioA,
         *            filtro data e ora fine già validato
         */
        private FiltriOperazioniElenchiVersamentoPlain(MonitoraggioForm.FiltriOperazioniElenchiVersamento filtriOE,
                Date dataOrarioDa, Date dataOrarioA) throws EMFError {
            this.idEnte = filtriOE.getId_ente().parse();
            this.idStrut = filtriOE.getId_strut().parse();
            this.dataOrarioDa = dataOrarioDa;
            this.dataOrarioA = dataOrarioA;
            this.idAmbiente = filtriOE.getId_ambiente().parse();
            this.tiModOper = filtriOE.getTi_mod_oper().parse();
            this.idElencoVers = filtriOE.getId_elenco_vers().parse();
            this.flChiusuraElenco = filtriOE.getFl_oper_chiusura_elenco().isChecked();
            this.flCreaElenco = filtriOE.getFl_oper_crea_elenco().isChecked();
            this.flCreaIndiceElenco = filtriOE.getFl_oper_crea_indice_elenco().isChecked();
            this.flDefNoteElencoChiuso = filtriOE.getFl_oper_def_note_elenco_chiuso().isChecked();
            this.flDefNoteIndiceElenco = filtriOE.getFl_oper_def_note_indice_elenco().isChecked();
            this.flEliminaElenco = filtriOE.getFl_oper_elimina_elenco().isChecked();
            this.flFirmaElenco = filtriOE.getFl_oper_firma_elenco().isChecked();
            this.flModElenco = filtriOE.getFl_oper_mod_elenco().isChecked();
            this.flRecuperaElencoAperto = filtriOE.getFl_oper_recupera_elenco_aperto().isChecked();
            this.flRecuperaElencoScaduto = filtriOE.getFl_oper_recupera_elenco_scaduto().isChecked();
            this.flRimuoviDocElenco = filtriOE.getFl_oper_rimuovi_doc_elenco().isChecked();
            this.flRimuoviUdElenco = filtriOE.getFl_oper_rimuovi_ud_elenco().isChecked();
            this.flSetElencoAperto = filtriOE.getFl_oper_set_elenco_aperto().isChecked();
            this.flSetElencoDaChiudere = filtriOE.getFl_oper_set_elenco_da_chiudere().isChecked();
            this.flFirmaInCorso = filtriOE.getFl_oper_firma_in_corso().isChecked();
            this.flFirmaInCorsoFallita = filtriOE.getFl_oper_firma_in_corso_fallita().isChecked();
            this.flStartCreazioneElencoAip = filtriOE.getFl_oper_start_crea_elenco_indici_aip().isChecked();
            this.flEndCreazioneElencoAip = filtriOE.getFl_oper_end_crea_elenco_indici_aip().isChecked();
            this.flFirmaElencoAip = filtriOE.getFl_oper_firma_elenco_indici_aip().isChecked();
            this.flFirmaElencoAipInCorso = filtriOE.getFl_oper_firma_elenco_indici_aip_in_corso().isChecked();
            this.flFirmaElencoAipFallita = filtriOE.getFl_oper_firma_elenco_indici_aip_fallita().isChecked();
            this.flMarcaElencoAip = filtriOE.getFl_oper_marca_elenco_indici_aip().isChecked();
            this.flMarcaElencoAipFallita = filtriOE.getFl_oper_marca_elenco_indici_aip_fallita().isChecked();
            this.tiOutput = filtriOE.getTi_output().parse();
        }

        public FiltriOperazioniElenchiVersamentoPlain() {
        }

        public BigDecimal getIdEnte() {
            return idEnte;
        }

        public BigDecimal getIdStrut() {
            return idStrut;
        }

        public Date getDataOrarioDa() {
            return dataOrarioDa;
        }

        public Date getDataOrarioA() {
            return dataOrarioA;
        }

        public BigDecimal getIdAmbiente() {
            return idAmbiente;
        }

        public String getTiModOper() {
            return tiModOper;
        }

        public BigDecimal getIdElencoVers() {
            return idElencoVers;
        }

        public boolean isFlChiusuraElenco() {
            return flChiusuraElenco;
        }

        public boolean isFlCreaElenco() {
            return flCreaElenco;
        }

        public boolean isFlCreaIndiceElenco() {
            return flCreaIndiceElenco;
        }

        public boolean isFlDefNoteElencoChiuso() {
            return flDefNoteElencoChiuso;
        }

        public boolean isFlDefNoteIndiceElenco() {
            return flDefNoteIndiceElenco;
        }

        public boolean isFlEliminaElenco() {
            return flEliminaElenco;
        }

        public boolean isFlFirmaElenco() {
            return flFirmaElenco;
        }

        public boolean isFlModElenco() {
            return flModElenco;
        }

        public boolean isFlRecuperaElencoAperto() {
            return flRecuperaElencoAperto;
        }

        public boolean isFlRecuperaElencoScaduto() {
            return flRecuperaElencoScaduto;
        }

        public boolean isFlRimuoviDocElenco() {
            return flRimuoviDocElenco;
        }

        public boolean isFlRimuoviUdElenco() {
            return flRimuoviUdElenco;
        }

        public boolean isFlSetElencoAperto() {
            return flSetElencoAperto;
        }

        public boolean isFlSetElencoDaChiudere() {
            return flSetElencoDaChiudere;
        }

        public boolean isFlFirmaInCorso() {
            return flFirmaInCorso;
        }

        public boolean isFlFirmaInCorsoFallita() {
            return flFirmaInCorsoFallita;
        }

        public boolean isFlStartCreazioneElencoAip() {
            return flStartCreazioneElencoAip;
        }

        public boolean isFlEndCreazioneElencoAip() {
            return flEndCreazioneElencoAip;
        }

        public boolean isFlFirmaElencoAip() {
            return flFirmaElencoAip;
        }

        public boolean isFlFirmaElencoAipInCorso() {
            return flFirmaElencoAipInCorso;
        }

        public boolean isFlFirmaElencoAipFallita() {
            return flFirmaElencoAipFallita;
        }

        public boolean isFlMarcaElencoAip() {
            return flMarcaElencoAip;
        }

        public boolean isFlMarcaElencoAipFallita() {
            return flMarcaElencoAipFallita;
        }

        public String getTiOutput() {
            return tiOutput;
        }

        void setIdEnte(BigDecimal idEnte) {
            this.idEnte = idEnte;
        }

        void setIdStrut(BigDecimal idStrut) {
            this.idStrut = idStrut;
        }

        void setDataOrarioDa(Date dataOrarioDa) {
            this.dataOrarioDa = dataOrarioDa;
        }

        void setDataOrarioA(Date dataOrarioA) {
            this.dataOrarioA = dataOrarioA;
        }

        void setIdAmbiente(BigDecimal idAmbiente) {
            this.idAmbiente = idAmbiente;
        }

        void setTiModOper(String tiModOper) {
            this.tiModOper = tiModOper;
        }

        void setIdElencoVers(BigDecimal idElencoVers) {
            this.idElencoVers = idElencoVers;
        }

        void setFlChiusuraElenco(boolean flChiusuraElenco) {
            this.flChiusuraElenco = flChiusuraElenco;
        }

        void setFlCreaElenco(boolean flCreaElenco) {
            this.flCreaElenco = flCreaElenco;
        }

        void setFlCreaIndiceElenco(boolean flCreaIndiceElenco) {
            this.flCreaIndiceElenco = flCreaIndiceElenco;
        }

        void setFlDefNoteElencoChiuso(boolean flDefNoteElencoChiuso) {
            this.flDefNoteElencoChiuso = flDefNoteElencoChiuso;
        }

        void setFlDefNoteIndiceElenco(boolean flDefNoteIndiceElenco) {
            this.flDefNoteIndiceElenco = flDefNoteIndiceElenco;
        }

        void setFlEliminaElenco(boolean flEliminaElenco) {
            this.flEliminaElenco = flEliminaElenco;
        }

        void setFlFirmaElenco(boolean flFirmaElenco) {
            this.flFirmaElenco = flFirmaElenco;
        }

        void setFlModElenco(boolean flModElenco) {
            this.flModElenco = flModElenco;
        }

        void setFlRecuperaElencoAperto(boolean flRecuperaElencoAperto) {
            this.flRecuperaElencoAperto = flRecuperaElencoAperto;
        }

        void setFlRecuperaElencoScaduto(boolean flRecuperaElencoScaduto) {
            this.flRecuperaElencoScaduto = flRecuperaElencoScaduto;
        }

        void setFlRimuoviDocElenco(boolean flRimuoviDocElenco) {
            this.flRimuoviDocElenco = flRimuoviDocElenco;
        }

        void setFlRimuoviUdElenco(boolean flRimuoviUdElenco) {
            this.flRimuoviUdElenco = flRimuoviUdElenco;
        }

        void setFlSetElencoAperto(boolean flSetElencoAperto) {
            this.flSetElencoAperto = flSetElencoAperto;
        }

        void setFlSetElencoDaChiudere(boolean flSetElencoDaChiudere) {
            this.flSetElencoDaChiudere = flSetElencoDaChiudere;
        }

        void setFlFirmaInCorso(boolean flFirmaInCorso) {
            this.flFirmaInCorso = flFirmaInCorso;
        }

        void setFlFirmaInCorsoFallita(boolean flFirmaInCorsoFallita) {
            this.flFirmaInCorsoFallita = flFirmaInCorsoFallita;
        }

        void setFlStartCreazioneElencoAip(boolean flStartCreazioneElencoAip) {
            this.flStartCreazioneElencoAip = flStartCreazioneElencoAip;
        }

        void setFlEndCreazioneElencoAip(boolean flEndCreazioneElencoAip) {
            this.flEndCreazioneElencoAip = flEndCreazioneElencoAip;
        }

        void setFlFirmaElencoAip(boolean flFirmaElencoAip) {
            this.flFirmaElencoAip = flFirmaElencoAip;
        }

        void setFlFirmaElencoAipInCorso(boolean flFirmaElencoAipInCorso) {
            this.flFirmaElencoAipInCorso = flFirmaElencoAipInCorso;
        }

        void setFlFirmaElencoAipFallita(boolean flFirmaElencoAipFallita) {
            this.flFirmaElencoAipFallita = flFirmaElencoAipFallita;
        }

        void setFlMarcaElencoAip(boolean flMarcaElencoAip) {
            this.flMarcaElencoAip = flMarcaElencoAip;
        }

        void setFlMarcaElencoAipFallita(boolean flMarcaElencoAipFallita) {
            this.flMarcaElencoAipFallita = flMarcaElencoAipFallita;
        }

        void setTiOutput(String tiOutput) {
            this.tiOutput = tiOutput;
        }
    }

    static class FiltriContenutoSacerPlain {

        private List<BigDecimal> idAmbienteList;
        private List<BigDecimal> idEnteList;
        private List<BigDecimal> idStrutList;
        private List<BigDecimal> idSubStrutList;
        private List<BigDecimal> idRegistroUnitaDocList;
        private BigDecimal aaKeyUnitaDoc;
        private List<BigDecimal> idTipoUnitaDocList;
        private List<BigDecimal> idTipoDocList;
        private List<BigDecimal> idCategTipoUnitaDocList;
        private List<BigDecimal> idSottocategTipoUnitaDocList;
        private Timestamp dataRifDa;
        private Timestamp dataRifA;
        private List<BigDecimal> idCategEnteList;
        private List<BigDecimal> idCategStrutList;

        public FiltriContenutoSacerPlain() {

        }

        public FiltriContenutoSacerPlain(FiltriContenutoSacer filtriCS) throws EMFError {
            this.idAmbienteList = filtriCS.getId_ambiente().parse();
            this.idEnteList = filtriCS.getId_ente().parse();
            this.idStrutList = filtriCS.getId_strut().parse();
            this.idSubStrutList = filtriCS.getId_sub_strut().parse();
            this.idRegistroUnitaDocList = filtriCS.getId_registro_unita_doc().parse();
            this.aaKeyUnitaDoc = filtriCS.getAa_key_unita_doc().parse();
            this.idTipoUnitaDocList = filtriCS.getId_tipo_unita_doc().parse();
            this.idTipoDocList = filtriCS.getId_tipo_doc().parse();
            this.idCategTipoUnitaDocList = filtriCS.getId_categ_tipo_unita_doc().parse();
            this.idSottocategTipoUnitaDocList = filtriCS.getId_sottocateg_tipo_unita_doc().parse();
            this.dataRifDa = filtriCS.getDt_rif_da().parse();
            this.dataRifA = filtriCS.getDt_rif_a().parse();
            this.idCategEnteList = filtriCS.getId_categ_ente().parse();
            this.idCategStrutList = filtriCS.getId_categ_strut().parse();

        }

        public List<BigDecimal> getIdAmbienteList() {
            return idAmbienteList;
        }

        public List<BigDecimal> getIdEnteList() {
            return idEnteList;
        }

        public List<BigDecimal> getIdStrutList() {
            return idStrutList;
        }

        public List<BigDecimal> getIdSubStrutList() {
            return idSubStrutList;
        }

        public List<BigDecimal> getIdRegistroUnitaDocList() {
            return idRegistroUnitaDocList;
        }

        public BigDecimal getAaKeyUnitaDoc() {
            return aaKeyUnitaDoc;
        }

        public List<BigDecimal> getIdTipoUnitaDocList() {
            return idTipoUnitaDocList;
        }

        public List<BigDecimal> getIdTipoDocList() {
            return idTipoDocList;
        }

        public List<BigDecimal> getIdCategTipoUnitaDocList() {
            return idCategTipoUnitaDocList;
        }

        public List<BigDecimal> getIdSottocategTipoUnitaDocList() {
            return idSottocategTipoUnitaDocList;
        }

        public Timestamp getDataRifDa() {
            return dataRifDa;
        }

        public Timestamp getDataRifA() {
            return dataRifA;
        }

        public List<BigDecimal> getIdCategEnteList() {
            return idCategEnteList;
        }

        public List<BigDecimal> getIdCategStrutList() {
            return idCategStrutList;
        }

        void setIdAmbienteList(List<BigDecimal> idAmbienteList) {
            this.idAmbienteList = idAmbienteList;
        }

        void setIdEnteList(List<BigDecimal> idEnteList) {
            this.idEnteList = idEnteList;
        }

        void setIdStrutList(List<BigDecimal> idStrutList) {
            this.idStrutList = idStrutList;
        }

        void setIdSubStrutList(List<BigDecimal> idSubStrutList) {
            this.idSubStrutList = idSubStrutList;
        }

        void setIdRegistroUnitaDocList(List<BigDecimal> idRegistroUnitaDocList) {
            this.idRegistroUnitaDocList = idRegistroUnitaDocList;
        }

        void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
            this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        }

        void setIdTipoUnitaDocList(List<BigDecimal> idTipoUnitaDocList) {
            this.idTipoUnitaDocList = idTipoUnitaDocList;
        }

        void setIdTipoDocList(List<BigDecimal> idTipoDocList) {
            this.idTipoDocList = idTipoDocList;
        }

        void setIdCategTipoUnitaDocList(List<BigDecimal> idCategTipoUnitaDocList) {
            this.idCategTipoUnitaDocList = idCategTipoUnitaDocList;
        }

        void setIdSottocategTipoUnitaDocList(List<BigDecimal> idSottocategTipoUnitaDocList) {
            this.idSottocategTipoUnitaDocList = idSottocategTipoUnitaDocList;
        }

        void setDataRifDa(Timestamp dataRifDa) {
            this.dataRifDa = dataRifDa;
        }

        void setDataRifA(Timestamp dataRifA) {
            this.dataRifA = dataRifA;
        }

        void setIdCategEnteList(List<BigDecimal> idCategEnteList) {
            this.idCategEnteList = idCategEnteList;
        }

        void setIdCategStrutList(List<BigDecimal> idCategStrutList) {
            this.idCategStrutList = idCategStrutList;
        }
    }

}
