package it.eng.parer.web.helper;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.MonContaByStatoConservNew;
import it.eng.parer.entity.MonContaUdDocComp;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.entity.VrsDatiSessioneVers;
import it.eng.parer.entity.VrsFileSessione;
import it.eng.parer.entity.VrsSessioneVers;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriConsistenzaSacer;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriContenutoSacer;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriJobSchedulati;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriOperazioniVolumi;
import it.eng.parer.slite.gen.form.MonitoraggioForm.FiltriReplicaOrg;
import it.eng.parer.slite.gen.tablebean.VrsFileSessioneRowBean;
import it.eng.parer.slite.gen.tablebean.VrsFileSessioneTableBean;
import it.eng.parer.slite.gen.tablebean.VrsSessioneVersRowBean;
import it.eng.parer.slite.gen.tablebean.VrsSessioneVersTableBean;
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
import it.eng.parer.slite.gen.viewbean.LogVLisSchedRowBean;
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
import it.eng.parer.viewEntity.LogVLisSchedStrut;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.viewEntity.MonVLisDocNonVersIam;
import it.eng.parer.viewEntity.MonVLisOperVolIam;
import it.eng.parer.viewEntity.MonVLisSesRecup;
import it.eng.parer.viewEntity.MonVLisUdNonVersIam;
import it.eng.parer.viewEntity.MonVLisUniDocDaAnnul;
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
import it.eng.parer.web.util.Constants.TipoSessione;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class MonitoraggioHelper implements Serializable {

    private static final long serialVersionUID = -3416899035771856955L;

    Logger log = LoggerFactory.getLogger(MonitoraggioHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

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
     *
     * @throws EMFError
     *             errore generico
     */
    public AroVDocRangeDtTableBean contaDocNoUd(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione, count(doc) ni_doc " + "from IamAbilOrganiz iao, "
                        + "AroVDocRangeDt doc, OrgStrut strut " + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.tiStatoDoc in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED', 'IN_VOLUME_APERTO', 'IN_VOLUME_IN_ERRORE', 'IN_VOLUME_CHIUSO', 'IN_VOLUME_DA_CHIUDERE', 'IN_ATTESA_MEMORIZZAZIONE') "
                        + "and strut.idStrut = iao.idOrganizApplic " + "and doc.idStrut = iao.idOrganizApplic ");

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

        queryStr.append("group by doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione "
                + "order by doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     * @throws EMFError
     *             errore generico
     */
    public AroVDocTiUdRangeDtTableBean contaDocUd(long idUtente, BigDecimal idTipoUnitaDoc, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione, count(doc) ni_doc " + "from IamAbilOrganiz iao, "
                        + "AroVDocTiUdRangeDt doc, OrgStrut strut " + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.tiStatoDoc in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED', 'IN_VOLUME_APERTO', 'IN_VOLUME_IN_ERRORE', 'IN_VOLUME_CHIUSO', 'IN_VOLUME_DA_CHIUDERE', 'IN_ATTESA_MEMORIZZAZIONE') "
                        + "and doc.idStrut = iao.idOrganizApplic " + "and strut.idStrut = iao.idOrganizApplic "
                        + "and doc.idTipoUnitaDoc = :idTipo ");

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

        queryStr.append("group by doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione "
                + "order by doc.tiDoc, doc.tiStatoDoc, doc.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        query.setParameter("idTipo", idTipoUnitaDoc);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     *
     * @throws EMFError
     *             errore generico
     */
    public AroVDocVolRangeDtTableBean contaDocStatoVolNoUd(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select doc.tiDoc, doc.tiStatoVolumeConserv, doc.tiDtCreazione, count(doc) ni_doc_chiuso "
                        + "from IamAbilOrganiz iao, " + "AroVDocVolRangeDt doc, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.tiStatoDoc = 'IN_VOLUME_CHIUSO' "
                        + "and doc.tiStatoVolumeConserv in ('CHIUSO', 'FIRMATO', 'FIRMATO_NO_MARCA', 'DA_VERIFICARE') "
                        + "and strut.idStrut = iao.idOrganizApplic " + "and doc.idStrut = iao.idOrganizApplic ");

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

        queryStr.append("group by doc.tiDoc, doc.tiStatoVolumeConserv, doc.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     *
     * @throws EMFError
     *             errore generico
     */
    public AroVDocVolTiUdRangeDtTableBean contaDocStatoVolUd(long idUtente, BigDecimal idTipoUnitaDoc,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select doc.tiDoc, doc.tiStatoVolumeConserv, doc.tiDtCreazione, count(doc) ni_doc_chiuso "
                        + "from IamAbilOrganiz iao, " + "AroVDocVolTiUdRangeDt doc, OrgStrut strut  "
                        + "where iao.iamUser.idUserIam = :idUa "
                        + "and doc.tiDoc in ('PRINCIPALE', 'ALLEGATO', 'ANNESSO', 'ANNOTAZIONE') "
                        + "and doc.tiStatoDoc = 'IN_VOLUME_CHIUSO' "
                        + "and doc.tiStatoVolumeConserv in ('CHIUSO', 'FIRMATO', 'FIRMATO_NO_MARCA', 'DA_VERIFICARE') "
                        + " and strut.idStrut = iao.idOrganizApplic " + "and doc.idStrut = iao.idOrganizApplic "
                        + "and doc.idTipoUnitaDoc = :idTipo ");

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

        queryStr.append("group by doc.tiDoc, doc.tiStatoVolumeConserv, doc.tiDtCreazione ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        query.setParameter("idTipo", idTipoUnitaDoc);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     *
     * @throws EMFError
     *             errore generico
     */
    public VrsVSessioneVersRisoltaTableBean contaSessioniVersRisVer(long idUtente, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select ses.flSesRisolta, ses.tiDtCreazione, count(ses) ni_ses_vers, ses.flVerif, ses.flSesNonRisolub "
                        + "from IamAbilOrganiz iao, " + "VrsVSessioneVersRisolta ses, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa " + "and ses.tiSessioneVers = 'VERSAMENTO' "
                        + "and ses.tiStatoSessioneVers = 'CHIUSA_ERR' " + "and strut.idStrut = iao.idOrganizApplic "
                        + "and ses.idStrut = iao.idOrganizApplic ");

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

        queryStr.append("group by ses.flSesRisolta, ses.tiDtCreazione, ses.flVerif, ses.flSesNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     *
     * @throws EMFError
     *             errore generico
     */
    public VrsVSessioneAggRisoltaTableBean contaSessioniAggRisVer(long idUtente, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStruttura) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "select ses.flSesRisolta, ses.tiDtCreazione, count(ses) ni_ses_agg, ses.flVerif, ses.flSesNonRisolub "
                        + "from IamAbilOrganiz iao,  " + "VrsVSessioneAggRisolta ses, OrgStrut strut "
                        + "where iao.iamUser.idUserIam = :idUa " + "and ses.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
                        + "and ses.tiStatoSessioneVers = 'CHIUSA_ERR' " + "and strut.idStrut = iao.idOrganizApplic "
                        + "and ses.idStrut = iao.idOrganizApplic ");

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

        queryStr.append("group by ses.flSesRisolta, ses.tiDtCreazione, ses.flVerif, ses.flSesNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
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
     * @throws EMFError
     *             errore generico
     */
    public MonVRiepStrutIamTableBean getMonVRiepStrutIamViewBean(long idUtente, int maxResult, int idAmbiente)
            throws EMFError {
        String queryStr = "SELECT u FROM MonVRiepStrutIam u WHERE u.idUserIam = :idUtente AND u.idAmbiente = :idAmbiente "
                + "ORDER BY u.nmAmbiente, u.nmEnte, u.nmStrut";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUtente", idUtente);
        query.setParameter("idAmbiente", idAmbiente);
        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVRiepStrutIam> listaMon = query.getResultList();
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
     * @return entity bean MonVLisDocTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVLisUdVersTableBean getMonVLisDocViewBean(MonitoraggioFiltriListaDocBean filtri, int maxResult)
            throws EMFError {
        String whereWord = "WHERE ";
        // Modifica 16/05/2016 - utilizzo di table diverse in base a determinati filtri
        String table;
        if (StringUtils.isNotBlank(filtri.getTipoDoc()) && filtri.getTipoDoc().equals("1")) {
            if (StringUtils.isBlank(filtri.getStatoDoc())
                    || (!filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())
                            && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED.name())
                            && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name()))) {
                // Doc principale, stato doc in elenco nullo o != da IN_ATTESA_MEM, IN_ATTESA_SCHED, NON_SELEZ_SCHED
                table = "MonVLisUdVers";
            } else {
                // Doc principale, stato doc in elenco == a IN_ATTESA_MEM, IN_ATTESA_SCHED, NON_SELEZ_SCHED
                table = "MonVLisUdVersDaElab";
            }
        } else if (StringUtils.isBlank(filtri.getStatoDoc())
                || (!filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())
                        && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED.name())
                        && !filtri.getStatoDoc().equals(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name()))) {
            // Doc non principale, stato doc in elenco nullo o != da IN_ATTESA_MEM, IN_ATTESA_SCHED, NON_SELEZ_SCHED
            table = "MonVLisDocVers";
        } else if (filtri.getTipoCreazione().equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
            // Doc non principale, tipo creazione == AGGIUNTA_DOCUMENTO, stato doc in elenco == a IN_ATTESA_MEM,
            // IN_ATTESA_SCHED, NON_SELEZ_SCHED
            table = "MonVLisDocAggDaElab";
        } else {
            // Doc non principale, tipo creazione != AGGIUNTA_DOCUMENTO, stato doc in elenco == a IN_ATTESA_MEM,
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
        BigDecimal anno_range_da = filtri.getAaKeyUnitaDocDa();
        BigDecimal anno_range_a = filtri.getAaKeyUnitaDocA();
        String codice_range_da = filtri.getCdKeyUnitaDocDa();
        String codice_range_a = filtri.getCdKeyUnitaDocA();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro tipo doc (PRINCIPALE = 1, tutti i documenti = 0
        String tipoDoc = filtri.getTipoDoc();
        if (tipoDoc != null) {
            if (tipoDoc.equals("1")) {
                queryStr.append(whereWord).append("u.tiDoc = 'PRINCIPALE' ");
                whereWord = "AND ";
            }
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

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date data_orario_da = (filtri.getGiornoVersDaValidato() != null ? filtri.getGiornoVersDaValidato() : null);
        Date data_orario_a = (filtri.getGiornoVersAValidato() != null ? filtri.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
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

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List listaDoc = query.getResultList();

        MonVLisUdVersTableBean monTableBean = new MonVLisUdVersTableBean();

        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (int index = 0; index < listaDoc.size(); index++) {
                    Object record = listaDoc.get(index);
                    BaseRowInterface row = Transform.entity2RowBean(record);
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
     * @throws EMFError
     *             errore generico
     */
    public MonVLisVersErrIamTableBean getMonVLisVersErrIamViewBean(MonitoraggioFiltriListaVersFallitiBean filtriSes,
            int maxResult) throws EMFError {
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
        // Inserimento nella query del filtro tipo doc (PRINCIPALE = 1, tutti i documenti = 0
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

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date data_orario_da = (filtriSes.getGiornoVersDaValidato() != null ? filtriSes.getGiornoVersDaValidato()
                : null);
        Date data_orario_a = (filtriSes.getGiornoVersAValidato() != null ? filtriSes.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
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

        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in versione multiselect
        Set<String> registroSet = filtriSes.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN :setregistro)");
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

        // Inserimento nella query del filtro CHIAVE UNITA DOC range con registro in versione multiselect
        Set<String> registroRangeList = filtriSes.getRegistro_range();
        if (registroRangeList != null && !registroRangeList.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN :listaregistro)");
            whereWord = " AND ";
        }

        BigDecimal anno_range_da = filtriSes.getAnno_range_da();
        BigDecimal anno_range_a = filtriSes.getAnno_range_a();
        String codice_range_da = filtriSes.getNumero_range_da();
        String codice_range_a = filtriSes.getNumero_range_a();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        BigDecimal idUserIam = filtriSes.getIdUserIam();
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
            whereWord = "AND ";
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

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
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
     * @param filtriSes
     *            i filtri di ricerca riportati dalla pagina precedente
     *
     * @return MonVLisVersErrTableBean entity bean MonVLisVersErr
     *
     * @throws EMFError
     *             errore generico
     *
     * @deprecated Questo metodo non viene più utilizzato (vedi mac #14982). Nelle future release verrà eliminato.
     */
    public List<BigDecimal> getIdSessioneVersFalliti(MonitoraggioFiltriListaVersFallitiBean filtriSes) throws EMFError {

        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u.idSessioneVers FROM MonVLisVersErrIam u ");

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
        // Inserimento nella query del filtro tipo doc (PRINCIPALE = 1, tutti i documenti = 0
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

        String flSessioneErrVerif = filtriSes.getVerificato();
        if (flSessioneErrVerif != null) {
            queryStr.append(whereWord).append("u.flSessioneErrVerif = :flSessioneErrVerif ");
            whereWord = "AND ";
        }

        BigDecimal idUserIam = filtriSes.getIdUserIam();
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
            whereWord = "AND ";
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

        if (flSessioneErrVerif != null) {
            query.setParameter("flSessioneErrVerif", flSessioneErrVerif);
        }

        if (idUserIam != null) {
            query.setParameter("idUserIam", idUserIam);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<BigDecimal> listaIdSesVersFalliti = query.getResultList();

        return listaIdSesVersFalliti;
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
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisOperVolIam u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOV.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOV.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOV.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.dtOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOV.getTi_mod_oper().parse();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        BigDecimal idVolumeConserv = filtriOV.getId_volume_conserv().parse();
        if (idVolumeConserv != null) {
            queryStr.append(whereWord).append("u.idVolumeConserv = :idVolumeConserv ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        String flCreaVolume = filtriOV.getFl_oper_crea_volume().parse();
        String flRecuperaVolumeAperto = filtriOV.getFl_oper_recupera_volume_aperto().parse();
        String flAggiungiDocVolume = filtriOV.getFl_oper_aggiungi_doc_volume().parse();
        String flRecuperaVolumeScaduto = filtriOV.getFl_oper_recupera_volume_scaduto().parse();
        String flSetVolumeDaChiudere = filtriOV.getFl_oper_set_volume_da_chiudere().parse();
        String flSetVolumeAperto = filtriOV.getFl_oper_set_volume_aperto().parse();
        String flInizioCreaIndice = filtriOV.getFl_oper_inizio_crea_indice().parse();
        String flRecuperaVolumeInErrore = filtriOV.getFl_oper_recupera_volume_in_errore().parse();
        String flCreaIndiceVolume = filtriOV.getFl_oper_crea_indice_volume().parse();
        String flMarcaIndiceVolume = filtriOV.getFl_oper_marca_indice_volume().parse();
        String flSetVolumeInErrore = filtriOV.getFl_oper_set_volume_in_errore().parse();
        String flInizioVerifFirme = filtriOV.getFl_oper_inizio_verif_firme().parse();
        String flChiusuraVolume = filtriOV.getFl_oper_chiusura_volume().parse();
        String flErrVerifFirme = filtriOV.getFl_oper_err_verif_firme().parse();
        String flRimuoviDocVolume = filtriOV.getFl_oper_rimuovi_doc_volume().parse();
        String flEliminaVolume = filtriOV.getFl_oper_elimina_volume().parse();
        String flModificaVolume = filtriOV.getFl_oper_modifica_volume().parse();
        String flFirmaNoMarcaVolume = filtriOV.getFl_oper_firma_no_marca_volume().parse();
        String flFirmaVolume = filtriOV.getFl_oper_firma_volume().parse();

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
            whereWord = "OR ";
        }

        if (filtriOV.getTi_output().parse().equals("ANALITICO")) {
            // ordina per tipo operazione e data operazione
            queryStr.append(endWW).append(" ORDER BY u.tiOper, u.dtOper ");
        } else if (filtriOV.getTi_output().parse().equals("CRONOLOGICO")) {
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

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        }

        if (tiModOper != null) {
            query.setParameter("tiModOper", tiModOper);
        }

        if (idVolumeConserv != null) {
            query.setParameter("idVolumeConserv", idVolumeConserv);
        }

        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisOperVolIam> listaOperVol = query.getResultList();

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
            if (filtriOV.getId_ente().parse() == null) {
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
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM ElvVLisLogOper u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOE.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOE.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOE.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.tmOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOE.getTi_mod_oper().parse();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        BigDecimal idElencoVers = filtriOE.getId_elenco_vers().parse();
        if (idElencoVers != null) {
            queryStr.append(whereWord).append("u.idElencoVers = :idElencoVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        boolean flChiusuraElenco = filtriOE.getFl_oper_chiusura_elenco().isChecked();
        boolean flCreaElenco = filtriOE.getFl_oper_crea_elenco().isChecked();
        boolean flCreaIndiceElenco = filtriOE.getFl_oper_crea_indice_elenco().isChecked();
        boolean flDefNoteElencoChiuso = filtriOE.getFl_oper_def_note_elenco_chiuso().isChecked();
        boolean flDefNoteIndiceElenco = filtriOE.getFl_oper_def_note_indice_elenco().isChecked();
        boolean flEliminaElenco = filtriOE.getFl_oper_elimina_elenco().isChecked();
        boolean flFirmaElenco = filtriOE.getFl_oper_firma_elenco().isChecked();
        boolean flModElenco = filtriOE.getFl_oper_mod_elenco().isChecked();
        boolean flRecuperaElencoAperto = filtriOE.getFl_oper_recupera_elenco_aperto().isChecked();
        boolean flRecuperaElencoScaduto = filtriOE.getFl_oper_recupera_elenco_scaduto().isChecked();
        boolean flRimuoviDocElenco = filtriOE.getFl_oper_rimuovi_doc_elenco().isChecked();
        boolean flRimuoviUdElenco = filtriOE.getFl_oper_rimuovi_ud_elenco().isChecked();
        boolean flSetElencoAperto = filtriOE.getFl_oper_set_elenco_aperto().isChecked();
        boolean flSetElencoDaChiudere = filtriOE.getFl_oper_set_elenco_da_chiudere().isChecked();
        boolean flFirmaInCorso = filtriOE.getFl_oper_firma_in_corso().isChecked();
        boolean flFirmaInCorsoFallita = filtriOE.getFl_oper_firma_in_corso_fallita().isChecked();
        boolean flStartCreazioneElencoAip = filtriOE.getFl_oper_start_crea_elenco_indici_aip().isChecked();
        boolean flEndCreazioneElencoAip = filtriOE.getFl_oper_end_crea_elenco_indici_aip().isChecked();
        boolean flFirmaElencoAip = filtriOE.getFl_oper_firma_elenco_indici_aip().isChecked();
        boolean flFirmaElencoAipInCorso = filtriOE.getFl_oper_firma_elenco_indici_aip_in_corso().isChecked();
        boolean flFirmaElencoAipFallita = filtriOE.getFl_oper_firma_elenco_indici_aip_fallita().isChecked();
        boolean flMarcaElencoAip = filtriOE.getFl_oper_marca_elenco_indici_aip().isChecked();
        boolean flMarcaElencoAipFallita = filtriOE.getFl_oper_marca_elenco_indici_aip_fallita().isChecked();

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
            whereWord = "OR ";
        }

        if (filtriOE.getTi_output().parse().equals("ANALITICO")) {
            // ordina per tipo operazione e data operazione
            queryStr.append(endWW).append(" ORDER BY u.tiOper, u.tmOper ");
        } else if (filtriOE.getTi_output().parse().equals("CRONOLOGICO")) {
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

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da);
            query.setParameter("dataa", data_orario_a);
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
            if (filtriOE.getId_ente().parse() == null) {
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
        StringBuilder queryStr = new StringBuilder("SELECT u.tiOper, count(u) FROM MonVLisOperVolIam u ");
        String whereWord = "WHERE ";

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOV.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOV.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOV.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);
        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.dtOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOV.getTi_mod_oper().parse();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        String flCreaVolume = filtriOV.getFl_oper_crea_volume().parse();
        String flRecuperaVolumeAperto = filtriOV.getFl_oper_recupera_volume_aperto().parse();
        String flAggiungiDocVolume = filtriOV.getFl_oper_aggiungi_doc_volume().parse();
        String flRecuperaVolumeScaduto = filtriOV.getFl_oper_recupera_volume_scaduto().parse();
        String flSetVolumeDaChiudere = filtriOV.getFl_oper_set_volume_da_chiudere().parse();
        String flSetVolumeAperto = filtriOV.getFl_oper_set_volume_aperto().parse();
        String flInizioCreaIndice = filtriOV.getFl_oper_inizio_crea_indice().parse();
        String flRecuperaVolumeInErrore = filtriOV.getFl_oper_recupera_volume_in_errore().parse();
        String flCreaIndiceVolume = filtriOV.getFl_oper_crea_indice_volume().parse();
        String flMarcaIndiceVolume = filtriOV.getFl_oper_marca_indice_volume().parse();
        String flSetVolumeInErrore = filtriOV.getFl_oper_set_volume_in_errore().parse();
        String flInizioVerifFirme = filtriOV.getFl_oper_inizio_verif_firme().parse();
        String flChiusuraVolume = filtriOV.getFl_oper_chiusura_volume().parse();
        String flErrVerifFirme = filtriOV.getFl_oper_err_verif_firme().parse();
        String flRimuoviDocVolume = filtriOV.getFl_oper_rimuovi_doc_volume().parse();
        String flEliminaVolume = filtriOV.getFl_oper_elimina_volume().parse();
        String flModificaVolume = filtriOV.getFl_oper_modifica_volume().parse();
        String flFirmaNoMarcaVolume = filtriOV.getFl_oper_firma_no_marca_volume().parse();
        String flFirmaVolume = filtriOV.getFl_oper_firma_volume().parse();

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
            whereWord = "OR ";
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

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
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
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u.tiOper, count(u) FROM ElvVLisLogOper u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriOE.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriOE.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriOE.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.tmOper between :datada AND :dataa) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro modalità operazione
        String tiModOper = filtriOE.getTi_mod_oper().parse();
        if (tiModOper != null) {
            queryStr.append(whereWord).append("u.tiModOper = :tiModOper ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id volume
        BigDecimal idElencoVers = filtriOE.getId_elenco_vers().parse();
        if (idElencoVers != null) {
            queryStr.append(whereWord).append("u.idElencoVers = :idElencoVers ");
            whereWord = "AND ";
        }

        // Inserimento nella query dei filtri sul tipo operazione
        boolean flChiusuraElenco = filtriOE.getFl_oper_chiusura_elenco().isChecked();
        boolean flCreaElenco = filtriOE.getFl_oper_crea_elenco().isChecked();
        boolean flCreaIndiceElenco = filtriOE.getFl_oper_crea_indice_elenco().isChecked();
        boolean flDefNoteElencoChiuso = filtriOE.getFl_oper_def_note_elenco_chiuso().isChecked();
        boolean flDefNoteIndiceElenco = filtriOE.getFl_oper_def_note_indice_elenco().isChecked();
        boolean flEliminaElenco = filtriOE.getFl_oper_elimina_elenco().isChecked();
        boolean flFirmaElenco = filtriOE.getFl_oper_firma_elenco().isChecked();
        boolean flModElenco = filtriOE.getFl_oper_mod_elenco().isChecked();
        boolean flRecuperaElencoAperto = filtriOE.getFl_oper_recupera_elenco_aperto().isChecked();
        boolean flRecuperaElencoScaduto = filtriOE.getFl_oper_recupera_elenco_scaduto().isChecked();
        boolean flRimuoviDocElenco = filtriOE.getFl_oper_rimuovi_doc_elenco().isChecked();
        boolean flRimuoviUdElenco = filtriOE.getFl_oper_rimuovi_ud_elenco().isChecked();
        boolean flSetElencoAperto = filtriOE.getFl_oper_set_elenco_aperto().isChecked();
        boolean flSetElencoDaChiudere = filtriOE.getFl_oper_set_elenco_da_chiudere().isChecked();
        boolean flFirmaInCorso = filtriOE.getFl_oper_firma_in_corso().isChecked();
        boolean flFirmaInCorsoFallita = filtriOE.getFl_oper_firma_in_corso_fallita().isChecked();
        boolean flStartCreazioneElencoAip = filtriOE.getFl_oper_start_crea_elenco_indici_aip().isChecked();
        boolean flEndCreazioneElencoAip = filtriOE.getFl_oper_end_crea_elenco_indici_aip().isChecked();
        boolean flFirmaElencoAip = filtriOE.getFl_oper_firma_elenco_indici_aip().isChecked();
        boolean flFirmaElencoAipInCorso = filtriOE.getFl_oper_firma_elenco_indici_aip_in_corso().isChecked();
        boolean flFirmaElencoAipFallita = filtriOE.getFl_oper_firma_elenco_indici_aip_fallita().isChecked();
        boolean flMarcaElencoAip = filtriOE.getFl_oper_marca_elenco_indici_aip().isChecked();
        boolean flMarcaElencoAipFallita = filtriOE.getFl_oper_marca_elenco_indici_aip_fallita().isChecked();

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
            whereWord = "OR ";
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

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da);
            query.setParameter("dataa", data_orario_a);
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
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT mon.orgSubStrut.orgStrut.orgEnte.nmEnte, mon.orgSubStrut.orgStrut.nmStrut, "
                        + "mon.orgSubStrut.nmSubStrut, mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, "
                        + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, "
                        + "mon.decTipoUnitaDoc.nmTipoUnitaDoc, mon.decTipoDoc.nmTipoDoc, "
                        + "sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul), "
                        + "sum(mon.niDocVers) - sum(mon.niDocAnnulUd), "
                        + "sum(mon.niCompVers) - sum(mon.niCompAnnulUd), "
                        + "sum(mon.niSizeVers) - sum(mon.niSizeAnnulUd), "
                        + "sum(mon.niDocAgg), sum(mon.niCompAgg), sum(mon.niSizeAgg), "
                        + "sum(mon.niUnitaDocAnnul), sum(mon.niDocAnnulUd), sum(mon.niCompAnnulUd), sum(mon.niSizeAnnulUd) "
                        + "FROM MonContaUdDocComp mon, SIOrgEnteSiam enteConvenz " + "JOIN mon.orgSubStrut subStrut "
                        + "JOIN subStrut.orgStrut strut " + "JOIN strut.orgEnte ente "
                        + "JOIN ente.orgCategEnte categEnte " + "LEFT JOIN strut.orgCategStrut categStrut "
                        + "JOIN mon.decTipoUnitaDoc decTipoUD, DecVTreeCategTipoUd tree, IamAbilOrganiz iao "
                        + "WHERE iao.iamUser.idUserIam = :idUserIam "
                        + "AND strut.idEnteConvenz = enteConvenz.idEnteSiam "
                        + "AND tree.idCategTipoUnitaDoc = decTipoUD.decCategTipoUnitaDoc.idCategTipoUnitaDoc "
                        + "AND iao.idOrganizApplic = mon.orgSubStrut.orgStrut.idStrut ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriCS.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        // Inserimento nella query del filtro id ente
        List<BigDecimal> idEnteList = filtriCS.getId_ente().parse();
        if (!idEnteList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.orgEnte.idEnte IN :idEnteList ");
        }
        // Inserimento nella query del filtro id strut
        List<BigDecimal> idStrutList = filtriCS.getId_strut().parse();
        if (!idStrutList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut IN :idStrutList ");
        }
        // Inserimento nella query del filtro id sub strut
        List<BigDecimal> idSubStrutList = filtriCS.getId_sub_strut().parse();
        if (!idSubStrutList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.idSubStrut IN :idSubStrutList ");
        }
        // Inserimento nella query del filtro registro
        List<BigDecimal> idRegistroUnitaDocList = filtriCS.getId_registro_unita_doc().parse();
        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decRegistroUnitaDoc.idRegistroUnitaDoc IN :idRegistroUnitaDocList ");
        }
        BigDecimal aaKeyUnitaDoc = filtriCS.getAa_key_unita_doc().parse();
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("mon.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }
        // Inserimento nella query del filtro tipo unità documentaria
        List<BigDecimal> idTipoUnitaDocList = filtriCS.getId_tipo_unita_doc().parse();
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decTipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList ");
        }
        // Inserimento nella query del filtro tipo documento
        List<BigDecimal> idTipoDocList = filtriCS.getId_tipo_doc().parse();
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decTipoDoc.idTipoDoc IN :idTipoDocList ");
        }
        BigDecimal idCategTipoUnitaDoc = filtriCS.getId_categ_tipo_unita_doc().parse();
        BigDecimal idSottocategTipoUnitaDoc = filtriCS.getId_sottocateg_tipo_unita_doc().parse();
        String filtroCategoria = null;
        if (idCategTipoUnitaDoc != null) {
            if (idSottocategTipoUnitaDoc != null) {
                filtroCategoria = "/" + idCategTipoUnitaDoc + "/" + idSottocategTipoUnitaDoc;
            } else {
                filtroCategoria = "/" + idCategTipoUnitaDoc + "/%";
            }
            queryStr.append(whereWord).append("tree.dlIdCategTipoUnitaDoc LIKE :filtroCategoria ");
        }
        // Inserimento nella query del filtro data
        Date dataRifDa = filtriCS.getDt_rif_da().parse();
        Date dataRifA = filtriCS.getDt_rif_a().parse();
        if (dataRifDa != null && dataRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
        }
        // idAmbitoTerritList
        if (!idAmbitoTerritList.isEmpty()) {
            queryStr.append(whereWord).append("enteConvenz.idAmbitoTerrit IN :idAmbitoTerritList ");
        }

        // Inserimento nella query del filtro ambito categoria ente
        List<BigDecimal> idCategEnteList = filtriCS.getId_categ_ente().parse();
        if (!idCategEnteList.isEmpty()) {
            queryStr.append(whereWord).append("categEnte.idCategEnte IN :idCategEnteList ");
        }

        // Inserimento nella query del filtro ambito categoria struttura
        List<BigDecimal> idCategStrutList = filtriCS.getId_categ_strut().parse();
        if (!idCategStrutList.isEmpty()) {
            queryStr.append(whereWord).append("categStrut.idCategStrut IN :idCategStrutList ");
        }

        queryStr.append(
                "GROUP BY mon.orgSubStrut.orgStrut.orgEnte.nmEnte, mon.orgSubStrut.orgStrut.nmStrut, mon.orgSubStrut.nmSubStrut, mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, "
                        + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, mon.decTipoUnitaDoc.nmTipoUnitaDoc, mon.decTipoDoc.nmTipoDoc "
                        + "ORDER BY mon.orgSubStrut.orgStrut.orgEnte.nmEnte, mon.orgSubStrut.orgStrut.nmStrut, mon.orgSubStrut.nmSubStrut, mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, "
                        + "mon.aaKeyUnitaDoc, tree.dlPathCategTipoUnitaDoc, mon.decTipoUnitaDoc.nmTipoUnitaDoc, mon.decTipoDoc.nmTipoDoc ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (!idEnteList.isEmpty()) {
            query.setParameter("idEnteList", idEnteList);
        }

        if (!idStrutList.isEmpty()) {
            query.setParameter("idStrutList", idStrutList);
        }

        if (!idSubStrutList.isEmpty()) {
            query.setParameter("idSubStrutList", idSubStrutList);
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

        if (filtroCategoria != null) {
            query.setParameter("filtroCategoria", filtroCategoria);
        }

        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        if (!idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", idAmbitoTerritList);
        }

        if (!idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", idCategEnteList);
        }

        if (!idCategStrutList.isEmpty()) {
            query.setParameter("idCategStrutList", idCategStrutList);
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
     * @throws EMFError
     *             errore generico
     */
    public BaseTable getMonTotSacerForHomeTable(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            Date dtRifDa, Date dtRifA, long idUserIam) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, "
                + "mon.aaKeyUnitaDoc, decTipoUD.nmTipoUnitaDoc, "
                + "sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul), "
                + "sum(mon.niDocVers) - sum(mon.niDocAnnulUd), " + "sum(mon.niCompVers) - sum(mon.niCompAnnulUd), "
                + "sum(mon.niSizeVers) - sum(mon.niSizeAnnulUd), "
                + "sum(mon.niDocAgg), sum(mon.niCompAgg), sum(mon.niSizeAgg), " + "categ.cdCategTipoUnitaDoc "
                + "FROM MonContaUdDocComp mon " + "JOIN mon.orgSubStrut subStrut " + "JOIN subStrut.orgStrut strut "
                + "LEFT JOIN strut.orgCategStrut categStrut " + "JOIN mon.decTipoUnitaDoc decTipoUD "
                + "JOIN decTipoUD.decCategTipoUnitaDoc categ, IamAbilOrganiz iao "
                + "WHERE iao.iamUser.idUserIam = :idUserIam " + "AND iao.idOrganizApplic = strut.idStrut ");

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
                "GROUP BY mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, mon.aaKeyUnitaDoc, decTipoUD.nmTipoUnitaDoc, categ.cdCategTipoUnitaDoc "
                        + "HAVING (sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul)) > 0 "
                        + "ORDER BY mon.decRegistroUnitaDoc.cdRegistroUnitaDoc, mon.aaKeyUnitaDoc, mon.decTipoUnitaDoc.nmTipoUnitaDoc, categ.cdCategTipoUnitaDoc ");

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
            final String sql = "SELECT NEW it.eng.parer.web.dto.CounterResultBean(max (m.dtRifConta), sum(m.niDocVers) + sum(m.niDocAgg) - sum(m.niDocAnnulUd) ) from MonContaUdDocComp m";
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
        String queryStr = "SELECT u FROM VrsSessioneVers u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers.longValue());

        List<VrsSessioneVers> sessioneVers = query.getResultList();
        VrsSessioneVers record = sessioneVers.get(0);
        if (flSessioneErrVerif != null) {
            record.setFlSessioneErrVerif(flSessioneErrVerif);
        }
        if (flSessioneErrNonRisolub != null) {
            record.setFlSessioneErrNonRisolub(flSessioneErrNonRisolub);
        }
        // Se sto impostando i valori per passare da una sessione errata ad un versamento fallito
        if (nmAmbientePerCalcolo != null) {
            record.setNmAmbiente(nmAmbientePerCalcolo);
            record.setNmEnte(nmEntePerCalcolo);
            record.setNmStrut(nmStrutPerCalcolo);
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrutPerCalcolo.longValue());
            record.setOrgStrut(strut);
        }
        record.setCdRegistroKeyUnitaDoc(registroUD);
        record.setAaKeyUnitaDoc(annoUD);
        record.setCdKeyUnitaDoc(numUD);
        record.setCdKeyDocVers(chiaveDoc);

        try {
            entityManager.merge(record);
            entityManager.flush();
        } catch (RuntimeException re) {
            // logga l'errore e blocca tutto
            // log.fatal("Eccezione nella persistenza del " + re);
            log.error("Eccezione nella persistenza del  " + re);
        }
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
        String queryStr = "SELECT u FROM VrsSessioneVers u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers.longValue());

        List<VrsSessioneVers> sessioneVers = query.getResultList();
        VrsSessioneVers record = sessioneVers.get(0);
        record.setFlSessioneErrVerif(flSessioneErrVerif);

        try {
            entityManager.merge(record);
            entityManager.flush();
        } catch (RuntimeException re) {
            // logga l'errore e blocca tutto
            // log.fatal("Eccezione nella persistenza del " + re);
            log.error("Eccezione nella persistenza del  " + re);
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
        VrsSessioneVers record = entityManager.getReference(VrsSessioneVers.class, idSessioneVers.longValue());
        if (flSessioneErrVerif != null) {
            record.setFlSessioneErrVerif(flSessioneErrVerif);
        }
        record.setFlSessioneErrNonRisolub(flSessioneErrNonRisolub);
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
     * @throws EMFError
     *             errore generico
     */
    public VrsSessioneVersTableBean getSessioniErrateListTB(String flVerificato, int maxResults) throws EMFError {
        StringBuilder queryStr = new StringBuilder("select ses, err.dsErr, err.cdErr "
                + "from VrsSessioneVers ses join " + "ses.vrsDatiSessioneVers datoSes join "
                + "datoSes.vrsErrSessioneVers err " + "where ses.orgStrut is null ");

        // Inserimento nella query del filtro flVerificato
        if (flVerificato != null) {
            queryStr.append("and ses.flSessioneErrVerif = :flVerificato ");
        }
        queryStr.append("order by ses.dtChiusura desc ");
        Query query = entityManager.createQuery(queryStr.toString());
        if (flVerificato != null) {
            query.setParameter("flVerificato", flVerificato);
        }
        query.setMaxResults(maxResults);
        List<Object[]> sessioniErrate = query.getResultList();
        VrsSessioneVersTableBean sessioniErrateTableBean = new VrsSessioneVersTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : sessioniErrate) {
                VrsSessioneVersRowBean rowBean = (VrsSessioneVersRowBean) Transform.entity2RowBean(row[0]);

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

                StringBuilder chiave_ud = new StringBuilder();
                if (rowBean.getCdRegistroKeyUnitaDoc() != null) {
                    chiave_ud.append(rowBean.getCdRegistroKeyUnitaDoc());
                    if (rowBean.getAaKeyUnitaDoc() != null) {
                        chiave_ud.append(" - ").append(rowBean.getAaKeyUnitaDoc());
                        if (rowBean.getCdKeyUnitaDoc() != null) {
                            chiave_ud.append(" - ").append(rowBean.getCdKeyUnitaDoc());
                        }
                    }
                }

                rowBean.setString("chiave_ud", chiave_ud.toString());
                rowBean.setString("ds_err", (String) row[1]);
                rowBean.setString("cd_err", (String) row[2]);

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
     * @throws EMFError
     *             errore generico
     */
    public MonVVisVersErrIamRowBean getMonVVisVersErrIamRowBean(BigDecimal idSessioneVers) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisVersErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        MonVVisVersErrIamRowBean versErr = null;
        List<MonVVisVersErrIam> versErrList = query.getResultList();
        try {
            if (versErrList != null && !versErrList.isEmpty()) {
                versErr = (MonVVisVersErrIamRowBean) Transform.entity2RowBean(versErrList.get(0));
            }
        } catch (Exception e) {
            log.error("Errore nel recupero del dettaglio del versamento fallito " + e.getMessage(), e);
        }
        return versErr;
    }

    /**
     * Ricavo la lista dei "Versamenti unità documentarie falliti" Di questi ricavo quelli con chiave nulla
     *
     * @param filtriSes
     *            bean filtro MonitoraggioFiltriListaVersFallitiBean
     *
     * @return lista errori di tipo Object[]
     *
     * @throws EMFError
     *             errore generico
     */
    public List<Object[]> getSessioniSenzaChiave(MonitoraggioFiltriListaVersFallitiBean filtriSes) throws EMFError {

        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT u.idSessioneVers, v.blXml FROM MonVLisVersErrIam u, VrsXmlDatiSessioneVers v "
                        + "WHERE u.idDatiSessioneVers = v.vrsDatiSessioneVers.idDatiSessioneVers ");

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

        Date data_orario_da = (filtriSes.getGiornoVersDaValidato() != null ? filtriSes.getGiornoVersDaValidato()
                : null);
        Date data_orario_a = (filtriSes.getGiornoVersAValidato() != null ? filtriSes.getGiornoVersAValidato() : null);

        String flSessioneErrVerif = filtriSes.getVerificato();
        if (flSessioneErrVerif != null) {
            queryStr.append(whereWord).append("u.flSessioneErrVerif = :flSessioneErrVerif ");
            whereWord = "AND ";
        }

        String flNonRisolub = filtriSes.getNonRisolubile();
        if (flNonRisolub != null) {
            queryStr.append(whereWord).append("u.flSessioneErrNonRisolub = :flSessioneErrNonRisolub ");
            whereWord = "AND ";
        }

        // Gestione filtri codice errore
        String classeErrore = filtriSes.getClasseErrore();
        String sottoClasseErrore = filtriSes.getSottoClasseErrore();
        String codiceErrore = filtriSes.getCodiceErrore();

        if (codiceErrore != null) {
            queryStr.append(whereWord).append("u.cdErr = :cdErr ");
            whereWord = "AND ";
        } else if (sottoClasseErrore != null || classeErrore != null) {
            queryStr.append(whereWord).append("u.cdErr LIKE :cdErr ");
            whereWord = "AND ";
        }

        BigDecimal idUserIam = filtriSes.getIdUserIam();
        if (idUserIam != null) {
            queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
            whereWord = "AND ";
        }

        queryStr.append(whereWord).append("v.tiXmlDati = 'RICHIESTA' ");

        if (tipoSes != null) {
            if (tipoSes.equals(TipoSessione.VERSAMENTO.name())) {
                queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc is null " + "AND u.cdKeyUnitaDoc is null "
                        + "AND u.aaKeyUnitaDoc is null ");
            } else if (tipoSes.equals(TipoSessione.AGGIUNGI_DOCUMENTO.name())) {
                queryStr.append(whereWord).append("((u.cdRegistroKeyUnitaDoc is null " + "AND u.cdKeyUnitaDoc is null "
                        + "AND u.aaKeyUnitaDoc is null) " + "OR u.cdKeyDocVers is null) ");
            }
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

        if (flNonRisolub != null) {
            query.setParameter("flSessioneErrNonRisolub", flNonRisolub);
        }

        if (periodoVers != null) {
            if (!periodoVers.equals("TUTTI")) {
                query.setParameter("datada", dataDBda.getTime(), TemporalType.TIMESTAMP);
            }
            query.setParameter("dataa", dataDBa.getTime(), TemporalType.TIMESTAMP);
        }

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        }

        if (flSessioneErrVerif != null) {
            query.setParameter("flSessioneErrVerif", flSessioneErrVerif);
        }

        if (codiceErrore != null) {
            query.setParameter("cdErr", codiceErrore);
        } else if (sottoClasseErrore != null) {
            query.setParameter("cdErr", sottoClasseErrore + '%');
        } else if (classeErrore != null) {
            query.setParameter("cdErr", classeErrore + '%');
        }

        if (idUserIam != null) {
            query.setParameter("idUserIam", idUserIam);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<Object[]> listaVersErr = query.getResultList();

        return listaVersErr;
    }

    /**
     * Restituisce il rowbean contenente i dati del dettaglio di una Sessione Errata
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity bean MonVVisSesErrRowBean
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVVisSesErrIamRowBean getMonVVisSesErrIamRowBean(BigDecimal idSessioneVers) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisSesErrIam u WHERE u.idSessioneVers = :idSessioneVers";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSessioneVers", idSessioneVers);
        MonVVisSesErrIamRowBean sesErr = null;
        List<MonVVisSesErrIam> sesErrList = query.getResultList();
        try {
            if (sesErrList != null && !sesErrList.isEmpty()) {
                sesErr = (MonVVisSesErrIamRowBean) Transform.entity2RowBean(sesErrList.get(0));
            }
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

    /**
     * Restituisce un teablebean rappresentante la lista file nel dettaglio di un Versamento Fallito
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity bean VrsFileSessioneTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public VrsFileSessioneTableBean getFileListTableBean(BigDecimal idSessioneVers) throws EMFError {
        Query query = entityManager
                .createQuery("select ses.idSessioneVers, fileSes.pgFileSessione, fileSes.nmFileSessione, "
                        + "CONCAT('vers_', ses.idSessioneVers, '_file_', fileSes.pgFileSessione, '_id_', fileSes.nmFileSessione) "
                        + "from VrsSessioneVers ses JOIN " + "ses.vrsDatiSessioneVers datiSes JOIN "
                        + "datiSes.vrsFileSessiones fileSes " + "where ses.idSessioneVers = :idSessioneVers "
                        + "order by fileSes.pgFileSessione ");
        query.setParameter("idSessioneVers", idSessioneVers);

        List<Object[]> listaFile = query.getResultList();
        VrsFileSessioneTableBean fileSessioneTableBean = new VrsFileSessioneTableBean();
        try {
            // trasformo la lista di entity (risultante della query) in un tablebean
            for (Object[] row : listaFile) {
                VrsFileSessioneRowBean rowBean = new VrsFileSessioneRowBean();
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
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return lista ogetti di tipo BlobObject
     *
     * @throws EMFError
     *             errore generico
     */
    public List<BlobObject> getBlobboByteList(BigDecimal idSessioneVers) throws EMFError {
        // creo la lista che conterrà i blobbi dei file da restituire
        List<BlobObject> listaBlobbiFile = new ArrayList<BlobObject>();

        // ricavo i blobbi
        VrsSessioneVers sessione = entityManager.find(VrsSessioneVers.class, idSessioneVers.longValue());
        for (int i = 0; i < sessione.getVrsDatiSessioneVers().size(); i++) {
            VrsDatiSessioneVers datiSes = sessione.getVrsDatiSessioneVers().get(i);
            for (int j = 0; j < datiSes.getVrsFileSessiones().size(); j++) {
                VrsFileSessione fileSes = datiSes.getVrsFileSessiones().get(j);
                BlobObject bo = new BlobObject(fileSes.getIdFileSessione(), "vers_" + sessione.getIdSessioneVers()
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
    public LogVLisSchedTableBean getLogVLisSchedViewBean(FiltriJobSchedulati filtriJS, Date[] dateValidate)
            throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSched u ");
        // Inserimento nella query del filtro nome job
        String nomeJob = filtriJS.getNm_job().parse();
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
            whereWord = "AND ";
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            query.setParameter("nmJob", nomeJob);
        }
        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<LogVLisSched> listaSched = query.getResultList();
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

    public LogVLisSchedStrutTableBean getLogVLisSchedStrutViewBean(FiltriJobSchedulati filtriJS, Date[] dateValidate)
            throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM LogVLisSchedStrut u ");
        // Inserimento nella query del filtro nome job
        String nomeJob = filtriJS.getNm_job().parse();
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro struttura
        BigDecimal idStrut = filtriJS.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        Date data_orario_da = (dateValidate != null ? dateValidate[0] : null);
        Date data_orario_a = (dateValidate != null ? dateValidate[1] : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
            whereWord = "AND ";
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (nomeJob != null) {
            query.setParameter("nmJob", nomeJob);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
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

    /**
     * Restituisce un rowbean contenente le informazioni sull'ultima schedulazione di un determinato job
     *
     * @param nomeJob
     *            nome del job
     *
     * @return entity bean LogVVisLastSchedRowBean
     *
     * @throws EMFError
     *             errore generico
     */
    public LogVVisLastSchedRowBean getLogVVisLastSchedRowBean(String nomeJob) throws EMFError {
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
        String queryStr = "SELECT u FROM LogVVisLastSched u WHERE u.nmJob = '" + nomeJob + "'";
        Query query = entityManager.createQuery(queryStr);
        return query.getResultList();
    }

    public OrgAmbiente getAmbiente(String nmAmbiente) {
        String queryStr = "SELECT u FROM OrgAmbiente u WHERE u.nmAmbiente = :nmambientein ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmambientein", nmAmbiente);
        if (query.getResultList().size() > 0) {
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
        query.setParameter("idstrutturain", idStruttura.longValue());
        Long res = (Long) query.getSingleResult();
        return new BigDecimal(res);
    }

    public List<Object[]> contaVersFallitiDistintiUD(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(u), u.idStrut, u.flVerif, u.flNonRisolub " + "FROM IamAbilOrganiz iao, "
                        + "VrsVUdNonVer u, OrgStrut strut " + "WHERE u.idStrut = iao.idOrganizApplic "
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

        queryStr.append("GROUP BY u.idStrut, u.flVerif, u.flNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> res = query.getResultList();
        return res;
    }

    public List<Object[]> contaVersFallitiDistintiDoc(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStruttura) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(u), u.idStrut, u.flVerif, u.flNonRisolub " + "FROM IamAbilOrganiz iao, "
                        + "VrsVDocNonVer u, OrgStrut strut " + "WHERE u.idStrut = iao.idOrganizApplic "
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
        queryStr.append("GROUP BY u.idStrut, u.flVerif, u.flNonRisolub ");

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idUa", idUtente);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStruttura != null) {
            query.setParameter("idStrut", idStruttura);
        }

        List<Object[]> res = query.getResultList();
        return res;
    }

    public MonVLisUdNonVersIamTableBean getMonVLisUdNonVersIamViewBean(
            MonitoraggioFiltriListaVersFallitiDistintiDocBean filtri, int maxResult) {
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
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
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

        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in versione multiselect
        Set<String> registroSet = filtri.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN :setregistro)");
            whereWord = " AND ";
        }

        BigDecimal anno = filtri.getAnno();
        String codice = filtri.getNumero();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro CHIAVE UNITA DOC range con registro in versione multiselect
        BigDecimal anno_range_da = filtri.getAnno_range_da();
        BigDecimal anno_range_a = filtri.getAnno_range_a();
        String codice_range_da = filtri.getNumero_range_da();
        String codice_range_a = filtri.getNumero_range_a();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        if (StringUtils.isNotBlank(filtri.getClasseErrore()) || StringUtils.isNotBlank(filtri.getSottoClasseErrore())
                || StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            queryStr.append(whereWord).append(" EXISTS (" + "SELECT err " + "FROM VrsErrSessioneVers err "
                    + "JOIN err.vrsDatiSessioneVers dati " + "JOIN dati.vrsSessioneVers ses "
                    + "WHERE ses.tiSessioneVers = 'VERSAMENTO' " + "AND ses.tiStatoSessioneVers = 'CHIUSA_ERR' "
                    + "AND ses.orgStrut.idStrut = u.idStrut "
                    + "AND ses.cdRegistroKeyUnitaDoc = u.cdRegistroKeyUnitaDoc "
                    + "AND ses.aaKeyUnitaDoc = u.aaKeyUnitaDoc " + "AND ses.cdKeyUnitaDoc = u.cdKeyUnitaDoc " + "AND ");
            if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
                queryStr.append("err.cdErr = :codErr");
            } else if (StringUtils.isNotBlank(filtri.getClasseErrore())
                    || StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
                queryStr.append("err.cdErr like :codErr");
            }
            queryStr.append(" )");
            whereWord = " AND ";
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

        if (registroSet != null && !registroSet.isEmpty()) {
            query.setParameter("setregistro", registroSet);
        }

        if (anno != null) {
            query.setParameter("annoin", anno);
        }

        if (codice != null) {
            query.setParameter("codicein", codice);
        }

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        query.setParameter("idUserIam", filtri.getIdUserIam());
        if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            query.setParameter("codErr", filtri.getCodiceErrore());
        } else if (StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
            query.setParameter("codErr", filtri.getSottoClasseErrore() + "%");
        } else if (StringUtils.isNotBlank(filtri.getClasseErrore())) {
            query.setParameter("codErr", filtri.getClasseErrore() + "%");
        }

        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<MonVLisUdNonVersIam> listaUdNonVers = query.getResultList();

        MonVLisUdNonVersIamTableBean monTableBean = new MonVLisUdNonVersIamTableBean();

        try {
            if (listaUdNonVers != null && !listaUdNonVers.isEmpty()) {
                monTableBean = (MonVLisUdNonVersIamTableBean) Transform.entities2TableBean(listaUdNonVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Struttura per presentarlo a video come unico campo dall'unione di ambiente, ente e
         * struttura
         */
        for (MonVLisUdNonVersIamRowBean row : monTableBean) {
            row.setString("struttura", row.getNmAmbiente() + ", " + row.getNmEnte() + ", " + row.getNmStrut());
            row.setString("chiave_ud",
                    row.getCdRegistroKeyUnitaDoc() + " - " + row.getAaKeyUnitaDoc() + " - " + row.getCdKeyUnitaDoc());
        }

        return monTableBean;
    }

    public MonVLisDocNonVersIamTableBean getMonVLisDocNonVersIamViewBean(
            MonitoraggioFiltriListaVersFallitiDistintiDocBean filtri, int maxResult) {
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
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
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

        // Inserimento nella query del filtro CHIAVE UNITA DOC singola con registro in versione multiselect
        Set<String> registroSet = filtri.getRegistro();
        if (registroSet != null && !registroSet.isEmpty()) {
            queryStr.append(whereWord).append("(u.cdRegistroKeyUnitaDoc IN :setregistro)");
            whereWord = " AND ";
        }

        BigDecimal anno = filtri.getAnno();
        String codice = filtri.getNumero();

        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = " AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = " AND ";
        }

        BigDecimal anno_range_da = filtri.getAnno_range_da();
        BigDecimal anno_range_a = filtri.getAnno_range_a();
        String codice_range_da = filtri.getNumero_range_da();
        String codice_range_a = filtri.getNumero_range_a();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        queryStr.append(whereWord).append("u.idUserIam = :idUserIam ");
        if (StringUtils.isNotBlank(filtri.getClasseErrore()) || StringUtils.isNotBlank(filtri.getSottoClasseErrore())
                || StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            queryStr.append(whereWord)
                    .append(" EXISTS (" + "SELECT err " + "FROM VrsErrSessioneVers err "
                            + "JOIN err.vrsDatiSessioneVers dati " + "JOIN dati.vrsSessioneVers ses "
                            + "WHERE ses.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
                            + "AND ses.tiStatoSessioneVers = 'CHIUSA_ERR' " + "AND ses.orgStrut.idStrut = u.idStrut "
                            + "AND ses.cdRegistroKeyUnitaDoc = u.cdRegistroKeyUnitaDoc "
                            + "AND ses.aaKeyUnitaDoc = u.aaKeyUnitaDoc " + "AND ses.cdKeyUnitaDoc = u.cdKeyUnitaDoc "
                            + "AND ses.cdKeyDocVers = u.cdKeyDocVers " + "AND ");
            if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
                queryStr.append("err.cdErr = :codErr");
            } else if (StringUtils.isNotBlank(filtri.getClasseErrore())
                    || StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
                queryStr.append("err.cdErr like :codErr");
            }
            queryStr.append(" )");
            whereWord = " AND ";
        }

        // Ordina per ambiente, ente, struttura e chiave di ordinamento
        queryStr.append("ORDER BY u.nmAmbiente, u.nmEnte, u.nmStrut, u.dsKeyOrd, u.cdKeyDocVers");

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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        query.setParameter("idUserIam", filtri.getIdUserIam());
        if (StringUtils.isNotBlank(filtri.getCodiceErrore())) {
            query.setParameter("codErr", filtri.getCodiceErrore());
        } else if (StringUtils.isNotBlank(filtri.getSottoClasseErrore())) {
            query.setParameter("codErr", filtri.getSottoClasseErrore() + "%");
        } else if (StringUtils.isNotBlank(filtri.getClasseErrore())) {
            query.setParameter("codErr", filtri.getClasseErrore() + "%");
        }

        query.setMaxResults(maxResult);

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
            row.setString("struttura", row.getNmAmbiente() + ", " + row.getNmEnte() + ", " + row.getNmStrut());
            row.setString("chiave_ud",
                    row.getCdRegistroKeyUnitaDoc() + " - " + row.getAaKeyUnitaDoc() + " - " + row.getCdKeyUnitaDoc());
        }

        return monTableBean;
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
     * @throws EMFError
     *             errore generico
     */
    public MonVVisUdNonVersRowBean getMonVVisUdNonVersRowBean(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisUdNonVers u " + "WHERE u.idStrut = :idstrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc " + "AND u.aaKeyUnitaDoc = :aakeyunitadoc "
                + "AND u.cdKeyUnitaDoc = :cdkeyunitadoc";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);
        MonVVisUdNonVersRowBean nonVers = null;
        List<MonVVisUdNonVers> listaNonVers = query.getResultList();
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

    public MonVVisDocNonVersRowBean getMonVVisDocNonVersRowBean(BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdKeyDocVers) throws EMFError {
        String queryStr = "SELECT u FROM MonVVisDocNonVers u " + "WHERE u.idStrut = :idstrut "
                + "AND u.cdRegistroKeyUnitaDoc = :cdregistrokeyunitadoc " + "AND u.aaKeyUnitaDoc = :aakeyunitadoc "
                + "AND u.cdKeyUnitaDoc = :cdkeyunitadoc " + "AND u.cdKeyDocVers = :cdkeydocvers ";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("cdregistrokeyunitadoc", cdRegistroKeyUnitaDoc);
        query.setParameter("aakeyunitadoc", aaKeyUnitaDoc);
        query.setParameter("cdkeyunitadoc", cdKeyUnitaDoc);
        query.setParameter("cdkeydocvers", cdKeyDocVers);

        MonVVisDocNonVersRowBean nonVers = null;
        List<MonVVisDocNonVers> listaNonVers = query.getResultList();
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

    public MonVLisVersDocNonVersTableBean getMonVLisVersDocNonVersViewBean(BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdKeyDocVers,
            String tipoVers) throws EMFError {
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
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String tipoVers)
            throws EMFError {
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
            Long idUserIam) throws EMFError {
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
            whereWord = "AND ";
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
            query.setParameter("idUserIam", idUserIam);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List listaRegistri = query.getResultList();
        BaseTable tabellaRegistri = new BaseTable();

        for (int i = 0; i < listaRegistri.size(); i++) {
            BaseRow rigaRegistri = new BaseRow();
            rigaRegistri.setString("registro", (String) listaRegistri.get(i));
            tabellaRegistri.add(rigaRegistri);
        }

        return tabellaRegistri;
    }

    public BaseTable getRegistriFromTotaleMonVLisUdNonVers(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            Long idUserIam) throws EMFError {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.cdRegistroKeyUnitaDoc FROM MonVLisUdNonVersIam u ");

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
            whereWord = "AND ";
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
            query.setParameter("idUserIam", idUserIam);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List listaRegistri = query.getResultList();
        BaseTable tabellaRegistri = new BaseTable();

        for (int i = 0; i < listaRegistri.size(); i++) {
            BaseRow rigaRegistri = new BaseRow();
            rigaRegistri.setString("registro", (String) listaRegistri.get(i));
            tabellaRegistri.add(rigaRegistri);
        }

        return tabellaRegistri;
    }

    public Object[] getXmlsSesErr(BigDecimal idSessioneVers) throws EMFError {
        Object[] xmls = new Object[2];
        String queryStr = "SELECT u FROM MonVVisSesErr u WHERE u.idSessioneVers = :idSessioneVers";
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

    public Object[] getXmlsVersErr(BigDecimal idSessioneVers) throws EMFError {
        Object[] xmls = new Object[2];
        String queryStr = "SELECT u FROM MonVVisVersErr u WHERE u.idSessioneVers = :idSessioneVers";
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
                    queryStr.append(whereCondition).append("sesRecup.cdRegistroKeyUnitaDoc IN :registri ");
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
            whereCondition = "AND ";
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
                    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
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
                    // rowBean.setString("nm_userid2", entity.getIamUser().getNmUserid());
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
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM IamVLisOrganizDaReplic u ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtri.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtri.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtri.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        String tiOper = filtri.getTi_oper_replic().parse();
        if (tiOper != null) {
            queryStr.append(whereWord).append("u.tiOperReplic = :tiOper ");
            whereWord = "AND ";
        }
        // Inserimento nella query del filtro id tipo unita doc
        String tiStato = filtri.getTi_stato_replic().parse();
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

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<IamVLisOrganizDaReplic> listaIam = query.getResultList();

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
     *
     * @return entity bean MonVLisDocTableBean
     *
     * @throws EMFError
     *             errore generico
     */
    public List<MonVLisUniDocDaAnnul> getMonVLisUniDocDaAnnulViewBean(long idUtente,
            MonitoraggioFiltriListaDocBean filtri, int maxResult) throws EMFError {
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
        BigDecimal anno_range_da = filtri.getAaKeyUnitaDocDa();
        BigDecimal anno_range_a = filtri.getAaKeyUnitaDocA();
        String codice_range_da = filtri.getCdKeyUnitaDocDa();
        String codice_range_a = filtri.getCdKeyUnitaDocA();

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
            whereWord = "AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        // Ricavo le date per eventuale inserimento nella query del filtro giorno versamento
        Date data_orario_da = (filtri.getGiornoVersDaValidato() != null ? filtri.getGiornoVersDaValidato() : null);
        Date data_orario_a = (filtri.getGiornoVersAValidato() != null ? filtri.getGiornoVersAValidato() : null);

        // Inserimento nella query del filtro data già impostato con data e ora
        if ((data_orario_da != null) && (data_orario_a != null)) {
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
        query.setParameter("user", idUtente);

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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        if (data_orario_da != null && data_orario_a != null) {
            query.setParameter("datada", data_orario_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_orario_a, TemporalType.TIMESTAMP);
        }

        if (statoAnnul != null) {
            query.setParameter("statoAnnul", statoAnnul);
        }

        query.setMaxResults(maxResult);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public BaseRow getTotaliUdDocComp(FiltriContenutoSacer filtriCS, List<BigDecimal> idAmbitoTerritList,
            long idUserIam) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul) AS num_ud, "
                        + "sum(mon.niDocVers) + sum(mon.niDocAgg) - sum(mon.niDocAnnulUd) AS num_doc, "
                        + "sum(mon.niCompVers) + sum(mon.niCompAgg) - sum(mon.niCompAnnulUd) AS num_comp, "
                        + "sum(mon.niSizeVers) + sum(mon.niSizeAgg) - sum(mon.niSizeAnnulUd) AS dim_bytes "
                        + "FROM MonContaUdDocComp mon, SIOrgEnteSiam enteConvenz " + "JOIN mon.orgSubStrut subStrut "
                        + "JOIN subStrut.orgStrut strut " + "JOIN strut.orgEnte ente "
                        + "JOIN ente.orgCategEnte categEnte " + "LEFT JOIN strut.orgCategStrut categStrut "
                        + "JOIN mon.decTipoUnitaDoc decTipoUD, DecVTreeCategTipoUd tree, IamAbilOrganiz iao "
                        + "WHERE iao.iamUser.idUserIam = :idUserIam "
                        + "AND strut.idEnteConvenz = enteConvenz.idEnteSiam "
                        + "AND tree.idCategTipoUnitaDoc = decTipoUD.decCategTipoUnitaDoc.idCategTipoUnitaDoc "
                        + "AND iao.idOrganizApplic = mon.orgSubStrut.orgStrut.idStrut ");

        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriCS.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        // Inserimento nella query del filtro id ente
        List<BigDecimal> idEnteList = filtriCS.getId_ente().parse();
        if (!idEnteList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.orgEnte.idEnte IN :idEnteList ");
        }
        // Inserimento nella query del filtro id strut
        List<BigDecimal> idStrutList = filtriCS.getId_strut().parse();
        if (!idStrutList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut IN :idStrutList ");
        }
        // Inserimento nella query del filtro id sub strut
        List<BigDecimal> idSubStrutList = filtriCS.getId_sub_strut().parse();
        if (!idSubStrutList.isEmpty()) {
            queryStr.append(whereWord).append("mon.orgSubStrut.idSubStrut IN :idSubStrutList ");
        }
        // Inserimento nella query del filtro registro
        List<BigDecimal> idRegistroUnitaDocList = filtriCS.getId_registro_unita_doc().parse();
        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decRegistroUnitaDoc.idRegistroUnitaDoc IN :idRegistroUnitaDocList ");
        }
        BigDecimal aaKeyUnitaDoc = filtriCS.getAa_key_unita_doc().parse();
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("mon.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }
        // Inserimento nella query del filtro tipo unità documentaria
        List<BigDecimal> idTipoUnitaDocList = filtriCS.getId_tipo_unita_doc().parse();
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decTipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList ");
        }
        // Inserimento nella query del filtro tipo documento
        List<BigDecimal> idTipoDocList = filtriCS.getId_tipo_doc().parse();
        if (!idTipoDocList.isEmpty()) {
            queryStr.append(whereWord).append("mon.decTipoDoc.idTipoDoc IN :idTipoDocList ");
        }
        BigDecimal idCategTipoUnitaDoc = filtriCS.getId_categ_tipo_unita_doc().parse();
        BigDecimal idSottocategTipoUnitaDoc = filtriCS.getId_sottocateg_tipo_unita_doc().parse();
        String filtroCategoria = null;
        if (idCategTipoUnitaDoc != null) {
            if (idSottocategTipoUnitaDoc != null) {
                filtroCategoria = "/" + idCategTipoUnitaDoc + "/" + idSottocategTipoUnitaDoc;
            } else {
                filtroCategoria = "/" + idCategTipoUnitaDoc + "/%";
            }
            queryStr.append(whereWord).append("tree.dlIdCategTipoUnitaDoc LIKE :filtroCategoria ");
        }

        // Inserimento nella query del filtro data
        Date dataRifDa = filtriCS.getDt_rif_da().parse();
        Date dataRifA = filtriCS.getDt_rif_a().parse();
        if (dataRifDa != null && dataRifA != null) {
            queryStr.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
        }

        if (!idAmbitoTerritList.isEmpty()) {
            queryStr.append(whereWord).append("enteConvenz.idAmbitoTerrit IN :idAmbitoTerritList ");
        }

        // Inserimento nella query del filtro ambito categoria ente
        List<BigDecimal> idCategEnteList = filtriCS.getId_categ_ente().parse();
        if (!idCategEnteList.isEmpty()) {
            queryStr.append(whereWord).append("categEnte.idCategEnte IN :idCategEnteList ");
        }

        // Inserimento nella query del filtro ambito categoria struttura
        List<BigDecimal> idCategStrutList = filtriCS.getId_categ_strut().parse();
        if (!idCategStrutList.isEmpty()) {
            queryStr.append(whereWord).append("categStrut.idCategStrut IN :idCategStrutList ");
        }

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (!idEnteList.isEmpty()) {
            query.setParameter("idEnteList", idEnteList);
        }

        if (!idStrutList.isEmpty()) {
            query.setParameter("idStrutList", idStrutList);
        }

        if (!idSubStrutList.isEmpty()) {
            query.setParameter("idSubStrutList", idSubStrutList);
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

        if (filtroCategoria != null) {
            query.setParameter("filtroCategoria", filtroCategoria);
        }

        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        if (!idAmbitoTerritList.isEmpty()) {
            query.setParameter("idAmbitoTerritList", idAmbitoTerritList);
        }

        if (!idCategEnteList.isEmpty()) {
            query.setParameter("idCategEnteList", idCategEnteList);
        }

        if (!idCategStrutList.isEmpty()) {
            query.setParameter("idCategStrutList", idCategStrutList);
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
            Date dtRifA, long idUserIam) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT sum(mon.niUnitaDocVers) - sum(mon.niUnitaDocAnnul) AS num_ud, "
                        + "sum(mon.niDocVers) + sum(mon.niDocAgg) - sum(mon.niDocAnnulUd) AS num_doc, "
                        + "sum(mon.niCompVers) + sum(mon.niCompAgg) - sum(mon.niCompAnnulUd) AS num_comp, "
                        + "sum(mon.niSizeVers) + sum(mon.niSizeAgg) - sum(mon.niSizeAnnulUd) AS dim_bytes "
                        + "FROM MonContaUdDocComp mon " + "JOIN mon.orgSubStrut subStrut "
                        + "JOIN subStrut.orgStrut strut " + "LEFT JOIN strut.orgCategStrut categStrut "
                        + "JOIN mon.decTipoUnitaDoc decTipoUD "
                        + "JOIN decTipoUD.decCategTipoUnitaDoc categ, IamAbilOrganiz iao "
                        + "WHERE iao.iamUser.idUserIam = :idUserIam "
                        + "AND iao.idOrganizApplic = mon.orgSubStrut.orgStrut.idStrut ");

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
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
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

    private static Calendar getDicembre2011() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private static Calendar getTodayWithoutHour() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Object[] getListaTotaliConsistenzaComp(FiltriConsistenzaSacer filtri) throws EMFError {
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String confrontoConteggiNativeQuery = "SELECT id_strut, amb.nm_ambiente, "
                + "ente.nm_ente, tmp.nm_strut, tmp.ni_comp_vers, tmp.ni_comp_aip_generato, "
                + "tmp.ni_comp_aip_in_aggiorn, tmp.ni_comp_presa_in_carico, tmp.ni_comp_in_volume, tmp.ni_comp_annul, "
                + "(tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) delta_versati_gestiti "
                + "FROM (SELECT id_strut, nm_strut, ";

        String parte2 = " (SELECT nvl(sum (ni_comp_vers + ni_comp_agg), 0) "
                + "FROM sacer.MON_CONTA_UD_DOC_COMP conta_ud "
                + "WHERE conta_ud.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') "
                + " AND conta_ud.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') AND conta_ud.id_strut = strut.id_strut "
                + ") ni_comp_vers, " + "     (select nvl(sum(ni_comp_aip_generato), 0)"
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_generato, " + "      "
                + "     (select nvl(sum(ni_comp_aip_in_aggiorn), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_in_aggiorn, " + "      "
                + "     (select nvl(sum(ni_comp_presa_in_carico), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_presa_in_carico, " + "      "
                + "     (select nvl(sum(ni_comp_in_volume), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_in_volume, " + "      "
                + "     (select nvl(sum(ni_comp_annul), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
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
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String confrontoConteggiNativeQuery = "SELECT id_strut, amb.nm_ambiente, "
                + "ente.nm_ente, tmp.nm_strut, tmp.ni_comp_vers, tmp.ni_comp_aip_generato, "
                + "tmp.ni_comp_aip_in_aggiorn, tmp.ni_comp_presa_in_carico, tmp.ni_comp_in_volume, tmp.ni_comp_annul, "
                + "(tmp.ni_comp_vers - (tmp.ni_comp_aip_generato + tmp.ni_comp_aip_in_aggiorn "
                + "+ tmp.ni_comp_presa_in_carico + tmp.ni_comp_in_volume + tmp.ni_comp_annul)) delta_versati_gestiti "
                + "FROM (SELECT id_strut, nm_strut, ";

        String parte2 = " (SELECT nvl(sum (ni_comp_vers + ni_comp_agg), 0) "
                + "FROM sacer.MON_CONTA_UD_DOC_COMP conta_ud "
                + "WHERE conta_ud.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') "
                + " AND conta_ud.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') AND conta_ud.id_strut = strut.id_strut "
                + ") ni_comp_vers, " + "     (select nvl(sum(ni_comp_aip_generato), 0)"
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_generato,"
                + "     (select nvl(sum(ni_comp_aip_in_aggiorn), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_aip_in_aggiorn, " + "      "
                + "     (select nvl(sum(ni_comp_presa_in_carico), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_presa_in_carico, " + "      "
                + "     (select nvl(sum(ni_comp_in_volume), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
                + "      where conta_stato.dt_rif_conta >= TO_DATE(?1, 'dd-mm-yyyy') AND conta_stato.dt_rif_conta <= TO_DATE(?2, 'dd-mm-yyyy') "
                + "      and conta_stato.id_strut = strut.id_strut " + "      ) ni_comp_in_volume, " + "      "
                + "     (select nvl(sum(ni_comp_annul), 0) "
                + "      from sacer.MON_CONTA_BY_STATO_CONSERV_NEW conta_stato "
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
        // start.add(Calendar.DATE, 1);
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
        String errore = "";
        int i = 0;

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
                    i++;
                    errore = errore + valDa + " <br>";
                }
                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return errore;
    }

    public String getListaTotaliConsistenzaCompDiffDayByDay2(FiltriConsistenzaSacer filtriCC) throws EMFError {
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String whereWord = " WHERE ";
        StringBuilder queryMonContaUdDocComp = new StringBuilder("SELECT mon FROM MonContaUdDocComp mon ");
        // Inserimento nella query del filtro id ambiente
        BigDecimal idAmbiente = filtriCC.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryMonContaUdDocComp.append(whereWord)
                    .append("mon.orgSubStrut.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro id ente
        BigDecimal idEnte = filtriCC.getId_ente().parse();
        if (idEnte != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.orgSubStrut.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro id strut
        BigDecimal idStrut = filtriCC.getId_strut().parse();
        if (idStrut != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro data
        Date dataRifDa = filtriCC.getDt_rif_da().parse();
        Date dataRifA = filtriCC.getDt_rif_a().parse();
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }

        queryMonContaUdDocComp.append("ORDER BY mon.dtRifConta DESC ");

        Query query = entityManager.createQuery(queryMonContaUdDocComp.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaUdDocComp> listaTotSacer = query.getResultList();
        Map<String, String> mappa1 = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // mappa1
        for (MonContaUdDocComp elemento : listaTotSacer) {
            BigDecimal somma = elemento.getNiCompVers().add(elemento.getNiCompAgg());
            String chiave = "" + elemento.getDtRifConta() + elemento.getIdStrut() + elemento.getAaKeyUnitaDoc()
                    + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc()
                    // + elemento.getDecTipoDoc().getIdTipoDoc()
                    + somma;
            mappa1.put(chiave, df.format(elemento.getDtRifConta()));
        }

        whereWord = " WHERE ";
        StringBuilder queryMonContaByStatoConserv = new StringBuilder("SELECT mon FROM MonContaByStatoConservNew mon ");
        // Inserimento nella query del filtro id ambiente
        if (idAmbiente != null) {
            queryMonContaByStatoConserv.append(whereWord)
                    .append("mon.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro id ente
        if (idEnte != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }

        // Inserimento nella query del filtro data
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }

        queryMonContaByStatoConserv.append("ORDER BY mon.dtRifConta DESC ");

        Query query2 = entityManager.createQuery(queryMonContaByStatoConserv.toString());
        if (idAmbiente != null) {
            query2.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query2.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query2.setParameter("idStrut", idStrut);
        }

        if (dataRifDa != null && dataRifA != null) {
            query2.setParameter("dataRifDa", dataRifDa);
            query2.setParameter("dataRifA", dataRifA);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaByStatoConservNew> listaTotByStatoConserv = query2.getResultList();
        Map<String, String> mappa2 = new HashMap<>();

        for (MonContaByStatoConservNew elemento : listaTotByStatoConserv) {
            BigDecimal somma = elemento.getNiCompAipGenerato().add(elemento.getNiCompAipInAggiorn())
                    .add(elemento.getNiCompAnnul()).add(elemento.getNiCompInVolume())
                    .add(elemento.getNiCompPresaInCarico());
            String chiave = "" + elemento.getDtRifConta() + elemento.getIdStrut() + elemento.getAaKeyUnitaDoc()
                    + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc()
                    // + elemento.getDecTipoDoc().getIdTipoDoc()
                    + somma;
            mappa2.put(chiave, df.format(elemento.getDtRifConta()));
        }

        String dateConErrori = "";
        MapDifference<String, String> diff = Maps.difference(mappa1, mappa2);
        Set<String> keysOnlyInSource = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInTarget = diff.entriesOnlyOnRight().keySet();
        SortedSet<String> totale = new TreeSet<>();

        for (String chiave : keysOnlyInSource) {
            totale.add(mappa1.get(chiave));
        }

        for (String chiave : keysOnlyInTarget) {
            totale.add(mappa2.get(chiave));
        }

        try {
            for (String data : totale) {
                Date date1 = df.parse(data);
                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String data1 = df1.format(date1);
                dateConErrori = dateConErrori + data1 + " <br>";
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }

        return dateConErrori;
    }

    public String getListaTotaliConsistenzaCompDiffDayByDayLink(BigDecimal idStrut, Date dataRifDa, Date dataRifA)
            throws EMFError {
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String whereWord = " WHERE ";
        StringBuilder queryMonContaUdDocComp = new StringBuilder("SELECT mon FROM MonContaUdDocComp mon ");
        if (idStrut != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaUdDocComp.append("ORDER BY mon.dtRifConta DESC ");

        Query query = entityManager.createQuery(queryMonContaUdDocComp.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        // query.setParameter("idUserIam", idUserIam);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaUdDocComp> listaTotSacer = query.getResultList();
        Map<String, String> mappa1 = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (MonContaUdDocComp elemento : listaTotSacer) {
            BigDecimal somma = elemento.getNiCompVers().add(elemento.getNiCompAgg());
            String chiave = "" + elemento.getDtRifConta() + elemento.getIdStrut() + elemento.getAaKeyUnitaDoc()
                    + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc()
                    // + elemento.getDecTipoDoc().getIdTipoDoc()
                    + somma;
            mappa1.put(chiave, df.format(elemento.getDtRifConta()));
        }

        whereWord = " WHERE ";
        StringBuilder queryMonContaByStatoConserv = new StringBuilder("SELECT mon FROM MonContaByStatoConservNew mon ");
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro data
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaByStatoConserv.append("ORDER BY mon.dtRifConta DESC ");

        Query query2 = entityManager.createQuery(queryMonContaByStatoConserv.toString());

        if (idStrut != null) {
            query2.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query2.setParameter("dataRifDa", dataRifDa);
            query2.setParameter("dataRifA", dataRifA);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaByStatoConservNew> listaTotByStatoConserv = query2.getResultList();
        Map<String, String> mappa2 = new HashMap<>();

        for (MonContaByStatoConservNew elemento : listaTotByStatoConserv) {
            BigDecimal somma = elemento.getNiCompAipGenerato().add(elemento.getNiCompAipInAggiorn())
                    .add(elemento.getNiCompAnnul()).add(elemento.getNiCompInVolume())
                    .add(elemento.getNiCompPresaInCarico());
            String chiave = "" + elemento.getDtRifConta() + elemento.getIdStrut() + elemento.getAaKeyUnitaDoc()
                    + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc()
                    // + elemento.getDecTipoDoc().getIdTipoDoc()
                    + somma;
            mappa2.put(chiave, df.format(elemento.getDtRifConta()));
        }

        String dateConErrori = "";
        MapDifference<String, String> diff = Maps.difference(mappa1, mappa2);
        Set<String> keysOnlyInSource = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInTarget = diff.entriesOnlyOnRight().keySet();
        SortedSet<String> totale = new TreeSet<>();

        for (String chiave : keysOnlyInSource) {
            totale.add(mappa1.get(chiave));
        }

        for (String chiave : keysOnlyInTarget) {
            totale.add(mappa2.get(chiave));
        }

        try {
            for (String data : totale) {
                Date date1 = df.parse(data);
                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String data1 = df1.format(date1);
                dateConErrori = dateConErrori + data1 + " <br>";
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }
        return dateConErrori;
    }

    public BaseTable getListaTotaliConsistenzaCompDiffDayByDayLink2(BigDecimal idStrut, Date dataRifDa, Date dataRifA)
            throws EMFError {
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String whereWord = " WHERE ";
        StringBuilder queryMonContaUdDocComp = new StringBuilder("SELECT mon FROM MonContaUdDocComp mon ");
        if (idStrut != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaUdDocComp.append("ORDER BY mon.dtRifConta DESC ");

        Query query = entityManager.createQuery(queryMonContaUdDocComp.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        // query.setParameter("idUserIam", idUserIam);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaUdDocComp> listaTotSacer = query.getResultList();
        Map<String, String> mappa1 = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (MonContaUdDocComp elemento : listaTotSacer) {
            BigDecimal somma = elemento.getNiCompVers().add(elemento.getNiCompAgg());
            String chiave = "" + elemento.getDtRifConta() + ";" + elemento.getIdStrut() + ";"
                    + elemento.getAaKeyUnitaDoc() + ";" + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + ";" + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc() + ";"
                    // + elemento.getDecTipoDoc().getIdTipoDoc() + ";"
                    + somma;
            mappa1.put(chiave, df.format(elemento.getDtRifConta()));
        }

        whereWord = " WHERE ";
        StringBuilder queryMonContaByStatoConserv = new StringBuilder("SELECT mon FROM MonContaByStatoConservNew mon ");
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro data
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaByStatoConserv.append("ORDER BY mon.dtRifConta DESC ");

        Query query2 = entityManager.createQuery(queryMonContaByStatoConserv.toString());

        if (idStrut != null) {
            query2.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query2.setParameter("dataRifDa", dataRifDa);
            query2.setParameter("dataRifA", dataRifA);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaByStatoConservNew> listaTotByStatoConserv = query2.getResultList();
        Map<String, String> mappa2 = new HashMap<>();

        // NB: per gestire il caso di elementi annullati qui non andrebbero sommati
        // se non vengono presi in considerazione sopra in MON_CONTA_UD_DOC_COMP
        // nella query precedente che tiene conto solo dei versati e aggiunti
        for (MonContaByStatoConservNew elemento : listaTotByStatoConserv) {
            BigDecimal somma = elemento.getNiCompAipGenerato().add(elemento.getNiCompAipInAggiorn())
                    .add(elemento.getNiCompAnnul()).add(elemento.getNiCompInVolume())
                    .add(elemento.getNiCompPresaInCarico());
            String chiave = "" + elemento.getDtRifConta() + ";" + elemento.getIdStrut() + ";"
                    + elemento.getAaKeyUnitaDoc() + ";" + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + ";" + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc() + ";"
                    // + elemento.getDecTipoDoc().getIdTipoDoc() + ";"
                    + somma;
            mappa2.put(chiave, df.format(elemento.getDtRifConta()));
        }

        String dateConErrori = "";
        MapDifference<String, String> diff = Maps.difference(mappa1, mappa2);
        Set<String> keysOnlyInSource = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInTarget = diff.entriesOnlyOnRight().keySet();
        SortedSet<String> totale = new TreeSet<>();
        Map<String, String> mappaTotale = new HashMap<>();
        mappaTotale.putAll(mappa1);
        mappaTotale.putAll(mappa2);

        for (String chiave : keysOnlyInSource) {
            totale.add(mappa1.get(chiave));
        }
        //
        // for (String chiave : keysOnlyInTarget) {
        // totale.add(mappa2.get(chiave));
        // }

        try {// totale
            for (String data : totale) {
                Date date1 = df.parse(data);
                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String data1 = df1.format(date1);
                dateConErrori = dateConErrori + data1 + " <br>";
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }

        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        // for (String chiaveMappaTotale : mappaTotale.keySet()) {
        // try {
        // String[] totali = chiaveMappaTotale.split(";");
        // String dataP = mappaTotale.get(chiaveMappaTotale);
        // Date date1 = df.parse(dataP);
        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // String parsedDateString = dateFormat.format(date1);
        // Date parsedDate = dateFormat.parse(parsedDateString);
        // // Date parsedDate2 = dateFormat.parse(totali[10]);
        // Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        // // Timestamp timestamp2 = new java.sql.Timestamp(parsedDate2.getTime());
        // riga.setObject("data_creazione", timestamp);
        // // riga.setBigDecimal("tot_comp_versati_in_archivio", new BigDecimal(totali[1]));
        // riga.setBigDecimal("aa_key_unita_doc", new BigDecimal(totali[2]));
        // riga.setBigDecimal("id_strut", new BigDecimal(totali[1]));
        // riga.setBigDecimal("id_tipo_unita_doc", new BigDecimal(totali[4]));
        // riga.setBigDecimal("id_tipo_doc", new BigDecimal(totali[5]));
        // riga.setBigDecimal("id_registro_unita_doc", new BigDecimal(totali[3]));
        // riga.setBigDecimal("differenza", new BigDecimal(totali[6]));
        // // riga.setBigDecimal("id_sub_strut", new BigDecimal( totali[9]));
        // // riga.setObject("dt_rif_conta", timestamp2);
        // // riga.setBigDecimal("tot_comp_versa_contati", new BigDecimal( totali[11]));
        //
        // tabella.add(riga);
        // } catch (Exception e) {
        // log.error(e.getMessage());
        // }
        // }
        for (String chiaveMappaTotale : keysOnlyInSource) {
            try {
                String[] totali = chiaveMappaTotale.split(";");
                String dataP = mappaTotale.get(chiaveMappaTotale);
                Date date1 = df.parse(dataP);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String parsedDateString = dateFormat.format(date1);
                Date parsedDate = dateFormat.parse(parsedDateString);
                // Date parsedDate2 = dateFormat.parse(totali[10]);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                // Timestamp timestamp2 = new java.sql.Timestamp(parsedDate2.getTime());
                riga.setObject("data_creazione", timestamp);
                // riga.setBigDecimal("tot_comp_versati_in_archivio", new BigDecimal(totali[1]));
                riga.setBigDecimal("aa_key_unita_doc", new BigDecimal(totali[2]));
                riga.setBigDecimal("id_strut", new BigDecimal(totali[1]));
                riga.setBigDecimal("id_tipo_unita_doc", new BigDecimal(totali[4]));
                // riga.setBigDecimal("id_tipo_doc", new BigDecimal(totali[5]));
                riga.setBigDecimal("id_registro_unita_doc", new BigDecimal(totali[3]));
                riga.setBigDecimal("differenza", new BigDecimal(totali[5]));
                // riga.setBigDecimal("id_sub_strut", new BigDecimal( totali[9]));
                // riga.setObject("dt_rif_conta", timestamp2);
                // riga.setBigDecimal("tot_comp_versa_contati", new BigDecimal( totali[11]));

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            // rigaMod.setString("nm_sub_strut", entityManager
            // .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            // rigaMod.setString("nm_tipo_doc", entityManager
            // .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }
        ////
        return tabella;
        // return dateConErrori;dateConErrori
    }

    public BaseTable getListaDifferenzaConsistenzaComp(BigDecimal idStrut, Date dtRifContaDa, Date dtRifContaA)
            throws EMFError {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT a.*, "
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO-b.TOT_COMP_VERSA_CONTATI AS differenza, " + "  b.id_strut, "
                + "  b.id_sub_strut, " + "  b.dt_rif_conta, " + "  b.tot_comp_versa_contati "
                + "  FROM (  SELECT /*+ parallel (12) */ " + "  TRUNC (doc.dt_creazione)     AS data_creazione, "
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
                + "            FROM mon_conta_ud_doc_comp " + "            WHERE id_strut = ?1  "
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
                riga.setObject("data_creazione", (Timestamp) totali[0]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[1]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[2]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[6]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[7]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[9]);
                riga.setObject("dt_rif_conta", (Timestamp) totali[10]);
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

    public BaseTable getListaTotaliConsistenzaCompDiffDayByDayAnnull(BigDecimal idStrut, Date dataRifDa, Date dataRifA)
            throws EMFError {
        /* Query per confronto totali tra MON_CONTA_UD_DOC_COMP e MON_CONTA_BY_STATO_CONSERV_NEW */
        String whereWord = " WHERE ";
        StringBuilder queryMonContaUdDocComp = new StringBuilder("SELECT mon FROM MonContaUdDocComp mon ");
        if (idStrut != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.orgSubStrut.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaUdDocComp.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaUdDocComp.append("ORDER BY mon.dtRifConta DESC ");

        Query query = entityManager.createQuery(queryMonContaUdDocComp.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query.setParameter("dataRifDa", dataRifDa);
            query.setParameter("dataRifA", dataRifA);
        }

        // query.setParameter("idUserIam", idUserIam);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaUdDocComp> listaTotSacer = query.getResultList();
        Map<String, String> mappa1 = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (MonContaUdDocComp elemento : listaTotSacer) {
            BigDecimal somma = elemento.getNiCompAnnulUd();
            String chiave = "" + elemento.getDtRifConta() + ";" + elemento.getIdStrut() + ";"
                    + elemento.getAaKeyUnitaDoc() + ";" + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + ";" + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc() + ";" + somma;
            mappa1.put(chiave, df.format(elemento.getDtRifConta()));
        }

        whereWord = " WHERE ";
        StringBuilder queryMonContaByStatoConserv = new StringBuilder("SELECT mon FROM MonContaByStatoConservNew mon ");
        // Inserimento nella query del filtro id strut
        if (idStrut != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.orgStrut.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        // Inserimento nella query del filtro data
        if (dataRifDa != null && dataRifA != null) {
            queryMonContaByStatoConserv.append(whereWord).append("mon.dtRifConta BETWEEN :dataRifDa AND :dataRifA ");
            whereWord = " AND ";
        }
        queryMonContaByStatoConserv.append("ORDER BY mon.dtRifConta DESC ");

        Query query2 = entityManager.createQuery(queryMonContaByStatoConserv.toString());

        if (idStrut != null) {
            query2.setParameter("idStrut", idStrut);
        }
        if (dataRifDa != null && dataRifA != null) {
            query2.setParameter("dataRifDa", dataRifDa);
            query2.setParameter("dataRifA", dataRifA);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "OBJECT"
        List<MonContaByStatoConservNew> listaTotByStatoConserv = query2.getResultList();
        Map<String, String> mappa2 = new HashMap<>();

        for (MonContaByStatoConservNew elemento : listaTotByStatoConserv) {
            BigDecimal somma = elemento.getNiCompAnnul();
            String chiave = "" + elemento.getDtRifConta() + ";" + elemento.getIdStrut() + ";"
                    + elemento.getAaKeyUnitaDoc() + ";" + elemento.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()
                    + ";" + elemento.getDecTipoUnitaDoc().getIdTipoUnitaDoc() + ";" + somma;
            mappa2.put(chiave, df.format(elemento.getDtRifConta()));
        }

        String dateConErrori = "";
        MapDifference<String, String> diff = Maps.difference(mappa1, mappa2);
        Set<String> keysOnlyInSource = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInTarget = diff.entriesOnlyOnRight().keySet();
        SortedSet<String> totale = new TreeSet<>();
        Map<String, String> mappaTotale = new HashMap<>();
        mappaTotale.putAll(mappa1);
        mappaTotale.putAll(mappa2);

        for (String chiave : keysOnlyInSource) {
            totale.add(mappa1.get(chiave));
        }
        //
        // for (String chiave : keysOnlyInTarget) {
        // totale.add(mappa2.get(chiave));
        // }

        try {
            for (String data : totale) {
                Date date1 = df.parse(data);
                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                String data1 = df1.format(date1);
                dateConErrori = dateConErrori + data1 + " <br>";
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }

        BaseTable tabella = new BaseTable();
        BaseRow riga = new BaseRow();

        // for (String chiaveMappaTotale : mappaTotale.keySet()) {
        // try {
        // String[] totali = chiaveMappaTotale.split(";");
        // String dataP = mappaTotale.get(chiaveMappaTotale);
        // Date date1 = df.parse(dataP);
        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // String parsedDateString = dateFormat.format(date1);
        // Date parsedDate = dateFormat.parse(parsedDateString);
        // // Date parsedDate2 = dateFormat.parse(totali[10]);
        // Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        // // Timestamp timestamp2 = new java.sql.Timestamp(parsedDate2.getTime());
        // riga.setObject("data_creazione", timestamp);
        // // riga.setBigDecimal("tot_comp_versati_in_archivio", new BigDecimal(totali[1]));
        // riga.setBigDecimal("aa_key_unita_doc", new BigDecimal(totali[2]));
        // riga.setBigDecimal("id_strut", new BigDecimal(totali[1]));
        // riga.setBigDecimal("id_tipo_unita_doc", new BigDecimal(totali[4]));
        // riga.setBigDecimal("id_tipo_doc", new BigDecimal(totali[5]));
        // riga.setBigDecimal("id_registro_unita_doc", new BigDecimal(totali[3]));
        // riga.setBigDecimal("differenza", new BigDecimal(totali[6]));
        // // riga.setBigDecimal("id_sub_strut", new BigDecimal( totali[9]));
        // // riga.setObject("dt_rif_conta", timestamp2);
        // // riga.setBigDecimal("tot_comp_versa_contati", new BigDecimal( totali[11]));
        //
        // tabella.add(riga);
        // } catch (Exception e) {
        // log.error(e.getMessage());
        // }
        // }
        for (String chiaveMappaTotale : keysOnlyInSource) {
            try {
                String[] totali = chiaveMappaTotale.split(";");
                String dataP = mappaTotale.get(chiaveMappaTotale);
                Date date1 = df.parse(dataP);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String parsedDateString = dateFormat.format(date1);
                Date parsedDate = dateFormat.parse(parsedDateString);
                // Date parsedDate2 = dateFormat.parse(totali[10]);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                // Timestamp timestamp2 = new java.sql.Timestamp(parsedDate2.getTime());
                riga.setObject("data_creazione", timestamp);
                // riga.setBigDecimal("tot_comp_versati_in_archivio", new BigDecimal(totali[1]));
                riga.setBigDecimal("aa_key_unita_doc", new BigDecimal(totali[2]));
                riga.setBigDecimal("id_strut", new BigDecimal(totali[1]));
                riga.setBigDecimal("id_tipo_unita_doc", new BigDecimal(totali[4]));
                // riga.setBigDecimal("id_tipo_doc", new BigDecimal(totali[5]));
                riga.setBigDecimal("id_registro_unita_doc", new BigDecimal(totali[3]));
                riga.setBigDecimal("differenza", new BigDecimal(totali[5]));
                // riga.setBigDecimal("id_sub_strut", new BigDecimal( totali[9]));
                // riga.setObject("dt_rif_conta", timestamp2);
                // riga.setBigDecimal("tot_comp_versa_contati", new BigDecimal( totali[11]));

                tabella.add(riga);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        for (BaseRow rigaMod : tabella) {
            rigaMod.setString("nm_strut",
                    entityManager.find(OrgStrut.class, rigaMod.getBigDecimal("id_strut").longValue()).getNmStrut());
            // rigaMod.setString("nm_sub_strut", entityManager
            // .find(OrgSubStrut.class, rigaMod.getBigDecimal("id_sub_strut").longValue()).getNmSubStrut());
            // rigaMod.setString("nm_tipo_doc", entityManager
            // .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
            rigaMod.setString("nm_tipo_unita_doc",
                    entityManager.find(DecTipoUnitaDoc.class, rigaMod.getBigDecimal("id_tipo_unita_doc").longValue())
                            .getNmTipoUnitaDoc());
            rigaMod.setString("cd_registro_unita_doc",
                    entityManager
                            .find(DecRegistroUnitaDoc.class, rigaMod.getBigDecimal("id_registro_unita_doc").longValue())
                            .getCdRegistroUnitaDoc());
        }
        // tabella
        return tabella;

        // return dateConErrori;
    }

    public BaseTable getListaDifferenzaConsistenzaVsCalcoloSacer(BigDecimal idStrut, Date dtRifContaDa,
            Date dtRifContaA) throws EMFError {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza," + "  b.id_strut,"
                + "  b.dt_rif_conta," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_aip_in_aggiorn+ni_comp_aip_generato+ni_comp_presa_in_carico+ni_comp_in_volume+ni_comp_annul)    AS TOT_COMP_VERSATI_IN_ARCHIVIO,"
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   id_tipo_doc_princ," + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_conta_by_stato_conserv_new"
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
                + "              FROM mon_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
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
                riga.setObject("data_creazione", (Timestamp) totali[1]);
                riga.setBigDecimal("tot_comp_versati_in_archivio", (BigDecimal) totali[2]);
                riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) totali[3]);
                riga.setBigDecimal("id_strut", (BigDecimal) totali[0]);
                riga.setBigDecimal("id_tipo_unita_doc", (BigDecimal) totali[4]);
                riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
                riga.setBigDecimal("id_registro_unita_doc", (BigDecimal) totali[6]);
                riga.setBigDecimal("differenza", (BigDecimal) totali[8]);
                riga.setBigDecimal("id_sub_strut", (BigDecimal) totali[7]);
                riga.setObject("dt_rif_conta", (Timestamp) totali[1]);
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
            Date dtRifContaA) throws EMFError {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza," + "  b.id_strut,"
                + "  b.dt_rif_conta," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   SUM (ni_comp_aip_in_aggiorn+ni_comp_aip_generato+ni_comp_presa_in_carico+ni_comp_in_volume+ni_comp_annul)    AS TOT_COMP_VERSATI_IN_ARCHIVIO,"
                // + " COUNT (1) AS tot_comp_versati_in_archivio, "
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc,"
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_conta_by_stato_conserv_new"
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
                + "              FROM mon_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
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
                // riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
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
            // rigaMod.setString("nm_tipo_doc", entityManager
            // .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
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

    public BaseTable getListaDifferenzaConsistenzaVsCalcoloSacerConTipoDoc(BigDecimal idStrut, Date dtRifContaDa,
            Date dtRifContaA) throws EMFError {
        /* Query per evidenziare differenze conteggio consistenza */
        String differenzaNativeQuery = "SELECT" + "  a.*,"
                + "  a.TOT_COMP_VERSATI_IN_ARCHIVIO - b.TOT_COMP_VERSA_CONTATI AS differenza," + "  b.id_strut,"
                + "  b.dt_rif_conta," + "  b.tot_comp_versa_contati"
                + "    FROM (   SELECT /*+ parallel (12) */ id_strut," + "                   dt_rif_conta,"
                + "                   COUNT (1)                    AS tot_comp_versati_in_archivio, "
                + "                   aa_key_unita_doc," + "                   id_tipo_unita_doc, id_tipo_doc_princ, "
                + "                   " + "                   id_REGISTRO_UNITA_DOC,"
                + "                   id_sub_strut   " + "              FROM mon_conta_by_stato_conserv_new"
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
                + "              FROM mon_conta_ud_doc_comp" + "             WHERE id_strut = ?1 "
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
                // riga.setBigDecimal("id_tipo_doc", (BigDecimal) totali[5]);
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
            // rigaMod.setString("nm_tipo_doc", entityManager
            // .find(DecTipoDoc.class, rigaMod.getBigDecimal("id_tipo_doc").longValue()).getNmTipoDoc());
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

}
