package it.eng.parer.fascicoli.helper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AplSistemaMigraz;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.entity.MonContaFascicoli;
import it.eng.parer.entity.MonContaFascicoliKo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsFascicoloKo;
import it.eng.parer.entity.VrsSesFascicoloErr;
import it.eng.parer.entity.VrsSesFascicoloKo;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloTableBean;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVRicFascicoli;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.viewEntity.MonVChkFascByAmb;
import it.eng.parer.viewEntity.MonVChkFascByEnte;
import it.eng.parer.viewEntity.MonVChkFascByStrut;
import it.eng.parer.viewEntity.MonVChkFascByTiFasc;
import it.eng.parer.viewEntity.MonVChkFascKoByAmb;
import it.eng.parer.viewEntity.MonVChkFascKoByEnte;
import it.eng.parer.viewEntity.MonVChkFascKoByStrut;
import it.eng.parer.viewEntity.MonVChkFascKoByTiFasc;
import it.eng.parer.viewEntity.MonVCntFascByAmb;
import it.eng.parer.viewEntity.MonVCntFascByEnte;
import it.eng.parer.viewEntity.MonVCntFascByStrut;
import it.eng.parer.viewEntity.MonVCntFascByTiFasc;
import it.eng.parer.viewEntity.MonVCntFascKoByAmb;
import it.eng.parer.viewEntity.MonVCntFascKoByEnte;
import it.eng.parer.viewEntity.MonVCntFascKoByStrut;
import it.eng.parer.viewEntity.MonVCntFascKoByTiFasc;
import it.eng.parer.viewEntity.MonVLisFasc;
import it.eng.parer.viewEntity.MonVLisFascDaElab;
import it.eng.parer.viewEntity.MonVLisFascKo;
import it.eng.parer.viewEntity.MonVLisFascKoByErr;
import it.eng.parer.viewEntity.OrgVChkPartitionFascByAa;
import it.eng.parer.viewEntity.VrsVUpdFascicoloKo;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Moretti_Lu and Iacolucci_M
 */
@Stateless
@LocalBean
public class FascicoliHelper extends GenericHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    private static final Logger logger = LoggerFactory.getLogger(FascicoliHelper.class);

    /**
     * Ricerca dei fascicoli filtrandoli per Struttura di appartenenza e abilitazioni dell'utente corrente
     * 
     * @param filtri
     *            filtro ricerca fascicoli
     * @param idStrut
     *            id struttura
     * @param userId
     *            id utente
     * 
     * @return lista elemnti di tipo FasVRicFascicoli
     */
    public List<FasVRicFascicoli> retrieveFascicoli(RicercaFascicoliBean filtri, BigDecimal idStrut, long userId) {
        List<FasVRicFascicoli> result = null;
        String andClause = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT f FROM FasVRicFascicoli f "),
                whereClauseStr = new StringBuilder("WHERE f.idStrut = :idStrut ");

        if (filtri.getAa_fascicolo() != null) {
            whereClauseStr.append(andClause).append("f.aaFascicolo = :aaFascicolo ");
        }
        if (filtri.getAa_fascicolo_da() != null && filtri.getAa_fascicolo_a() != null) {
            whereClauseStr.append(andClause).append("f.aaFascicolo BETWEEN :aaFascicolo_da AND :aaFascicolo_a ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            whereClauseStr.append(andClause).append("f.cdKeyFascicolo = :cdKeyFascicolo ");
        }
        String cdKeyFascicolo_da = null, cdKeyFascicolo_a = null;
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo_da())
                && StringUtils.isNotBlank(filtri.getCd_key_fascicolo_a())) {
            cdKeyFascicolo_da = StringPadding.padString(filtri.getCd_key_fascicolo_da(), "0", 12,
                    StringPadding.PADDING_LEFT);
            cdKeyFascicolo_a = StringPadding.padString(filtri.getCd_key_fascicolo_a(), "0", 12,
                    StringPadding.PADDING_LEFT);
            whereClauseStr.append(andClause).append(
                    "FUNC('lpad', f.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicolo_da AND :cdKeyFascicolo_a ");
        }
        if (filtri.getNm_tipo_fascicolo() != null) {
            whereClauseStr.append(andClause).append("f.idTipoFascicolo = :idTipoFascicolo ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_oggetto_fascicolo())) {
            whereClauseStr.append(andClause).append("UPPER(f.dsOggettoFascicolo) LIKE :dsOggettoFascicolo ");
        }
        if (filtri.getDt_ape_fasciolo_da() != null && filtri.getDt_ape_fasciolo_a() != null) {
            whereClauseStr.append(andClause)
                    .append("f.dtApeFascicolo BETWEEN :dtApeFascicolo_da AND :dtApeFascicolo_a ");
        }
        if (filtri.getDt_chiu_fasciolo_da() != null && filtri.getDt_chiu_fasciolo_a() != null) {
            whereClauseStr.append(andClause)
                    .append("f.dtChiuFascicolo BETWEEN :dtChiuFascicolo_da AND :dtChiuFascicolo_a ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_proc_ammin())) {
            whereClauseStr.append(andClause).append("UPPER(f.cdProcAmmin) LIKE :cdProcAmmin ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_proc_ammin())) {
            whereClauseStr.append(andClause).append("UPPER(f.dsProcAmmin) LIKE :dsProcAmmin ");
        }
        if (filtri.getNi_aa_conservazione() != null) {
            whereClauseStr.append(andClause).append("f.niAaConservazione = :niAaConservazione ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_livello_riserv())) {
            whereClauseStr.append(andClause).append("UPPER(f.cdLivelloRiserv) LIKE :cdLivelloRiserv ");
        }
        if (StringUtils.isNotBlank(filtri.getNm_sistema_versante())) {
            whereClauseStr.append(andClause).append("UPPER(f.nmSistemaVersante) LIKE :nmSistemaVersante ");
        }
        if (StringUtils.isNotBlank(filtri.getNm_userid())) {
            whereClauseStr.append(andClause).append("UPPER(f.nmUserid) LIKE :nmUserid ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_composito_voce_titol())) {
            whereClauseStr.append(andClause).append("UPPER(f.cdCompositoVoceTitol) LIKE :cdCompositoVoceTitol ");
        }
        if (filtri.getAa_fascicolo_padre() != null) {
            whereClauseStr.append(andClause).append("f.aaFascicoloPadre = :aaFascicoloPadre ");
        }
        if (filtri.getAa_fascicolo_padre_da() != null && filtri.getAa_fascicolo_padre_a() != null) {
            whereClauseStr.append(andClause)
                    .append("f.aaFascicoloPadre BETWEEN :aaFascicoloPadre_da AND :aaFascicoloPadre_a ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo_padre())) {
            whereClauseStr.append(andClause).append("f.cdKeyFascicoloPadre = :cdKeyFascicoloPadre ");
        }
        String cdKeyFascicolo_padre_da = null, cdKeyFascicolo_padre_a = null;
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo_padre_da())
                && StringUtils.isNotBlank(filtri.getCd_key_fascicolo_padre_a())) {
            cdKeyFascicolo_padre_da = StringPadding.padString(filtri.getCd_key_fascicolo_padre_da(), "0", 12,
                    StringPadding.PADDING_LEFT);
            cdKeyFascicolo_padre_a = StringPadding.padString(filtri.getCd_key_fascicolo_padre_a(), "0", 12,
                    StringPadding.PADDING_LEFT);
            whereClauseStr.append(andClause).append(
                    "FUNC('lpad', f.cdKeyFascicoloPadre, 12, '0') BETWEEN :cdKeyFascicoloPadre_da AND :cdKeyFascicoloPadre_a ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_oggetto_fascicolo_padre())) {
            whereClauseStr.append(andClause).append("UPPER(f.dsOggettoFascicoloPadre) LIKE :dsOggettoFascicoloPadre ");
        }
        boolean udFilter = false;
        if (StringUtils.isNotBlank(filtri.getCd_registro_key_unita_doc())) {
            whereClauseStr.append(andClause).append("aud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
            udFilter = true;
        }
        if (filtri.getAa_key_unita_doc() != null) {
            whereClauseStr.append(andClause).append("aud.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            udFilter = true;
        }
        if (filtri.getAa_key_unita_doc_da() != null && filtri.getAa_key_unita_doc_a() != null) {
            whereClauseStr.append(andClause)
                    .append("aud.aaKeyUnitaDoc BETWEEN :aaKeyUnitaDoc_da AND :aaKeyUnitaDoc_a ");
            udFilter = true;
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_unita_doc())) {
            whereClauseStr.append(andClause).append("aud.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
            udFilter = true;
        }
        String cdKeyUnitaDoc_da = null, cdKeyUnitaDoc_a = null;
        if (StringUtils.isNotBlank(filtri.getCd_key_unita_doc_da())
                && StringUtils.isNotBlank(filtri.getCd_key_unita_doc_a())) {
            cdKeyUnitaDoc_da = StringPadding.padString(filtri.getCd_key_fascicolo_padre_da(), "0", 12,
                    StringPadding.PADDING_LEFT);
            cdKeyUnitaDoc_a = StringPadding.padString(filtri.getCd_key_fascicolo_padre_a(), "0", 12,
                    StringPadding.PADDING_LEFT);
            whereClauseStr.append(andClause)
                    .append("FUNC('lpad', aud.cdKeyUnitaDoc, 12, '0') BETWEEN :cdKeyUnitaDoc_da AND :cdKeyUnitaDoc_a ");
            udFilter = true;
        }
        if (udFilter) {
            whereClauseStr.append(andClause).append("fud.fasFascicolo.idFascicolo = f.idFascicolo ");
            queryStr.append(", FasUnitaDocFascicolo fud JOIN fud.aroUnitaDoc aud ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_conservazione())) {
            whereClauseStr.append(andClause).append("f.tiConservazione = :tiConservazione ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_classif())) {
            whereClauseStr.append(andClause).append("f.flForzaContrClassif = :flForzaContrClassif ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_numero())) {
            whereClauseStr.append(andClause).append("f.flForzaContrNumero = :flForzaContrNumero ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_colleg())) {
            whereClauseStr.append(andClause).append("f.flForzaContrColleg = :flForzaContrColleg ");
        }

        if (filtri.getTs_vers_fascicolo_da() != null && filtri.getTs_vers_fascicolo_a() != null) {
            whereClauseStr.append(andClause)
                    .append("f.tsVersFascicolo BETWEEN :tsVersFascicolo_da AND :tsVersFascicolo_a ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_esito())) {
            whereClauseStr.append(andClause).append("f.tiEsito = :tiEsito ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_conservazione())) {
            whereClauseStr.append(andClause).append("f.tiStatoConservazione = :tiStatoConservazione ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_fasc_elenco_vers())) {
            whereClauseStr.append(andClause).append("f.tiStatoFascElencoVers = :tiStatoFascElencoVers ");
        }
        whereClauseStr.append(andClause).append("f.tiStatoConservazione != 'ANNULLATO' ");
        whereClauseStr.append(andClause).append("f.idUserIamCorrente = :userId");
        queryStr.append(whereClauseStr).append(" ORDER BY f.aaFascicolo, f.cdKeyFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        if (filtri.getAa_fascicolo() != null) {
            query.setParameter("aaFascicolo", filtri.getAa_fascicolo());
        }
        if (filtri.getAa_fascicolo_da() != null && filtri.getAa_fascicolo_a() != null) {
            query.setParameter("aaFascicolo_da", filtri.getAa_fascicolo_da());
            query.setParameter("aaFascicolo_a", filtri.getAa_fascicolo_a());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            query.setParameter("cdKeyFascicolo", filtri.getCd_key_fascicolo());
        }
        if (StringUtils.isNotBlank(cdKeyFascicolo_da) && StringUtils.isNotBlank(cdKeyFascicolo_a)) {
            query.setParameter("cdKeyFascicolo_da", cdKeyFascicolo_da);
            query.setParameter("cdKeyFascicolo_a", cdKeyFascicolo_a);
        }
        if (filtri.getNm_tipo_fascicolo() != null) {
            query.setParameter("idTipoFascicolo", filtri.getNm_tipo_fascicolo());
        }
        if (StringUtils.isNotBlank(filtri.getDs_oggetto_fascicolo())) {
            query.setParameter("dsOggettoFascicolo", "%" + filtri.getDs_oggetto_fascicolo().toUpperCase() + "%");
        }
        if (filtri.getDt_ape_fasciolo_da() != null && filtri.getDt_ape_fasciolo_a() != null) {
            query.setParameter("dtApeFascicolo_da", filtri.getDt_ape_fasciolo_da());
            query.setParameter("dtApeFascicolo_a", filtri.getDt_ape_fasciolo_a());
        }
        if (filtri.getDt_chiu_fasciolo_da() != null && filtri.getDt_chiu_fasciolo_a() != null) {
            query.setParameter("dtChiuFascicolo_da", filtri.getDt_chiu_fasciolo_da());
            query.setParameter("dtChiuFascicolo_a", filtri.getDt_chiu_fasciolo_a());
        }
        if (StringUtils.isNotBlank(filtri.getCd_proc_ammin())) {
            query.setParameter("cdProcAmmin", "%" + filtri.getCd_proc_ammin().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getDs_proc_ammin())) {
            query.setParameter("dsProcAmmin", "%" + filtri.getDs_proc_ammin().toUpperCase() + "%");
        }
        if (filtri.getNi_aa_conservazione() != null) {
            query.setParameter("niAaConservazione", filtri.getNi_aa_conservazione());
        }
        if (StringUtils.isNotBlank(filtri.getCd_livello_riserv())) {
            query.setParameter("cdLivelloRiserv", "%" + filtri.getCd_livello_riserv().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getNm_sistema_versante())) {
            query.setParameter("nmSistemaVersante", "%" + filtri.getNm_sistema_versante().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getNm_userid())) {
            query.setParameter("nmUserid", "%" + filtri.getNm_userid().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getCd_composito_voce_titol())) {
            query.setParameter("cdCompositoVoceTitol", "%" + filtri.getCd_composito_voce_titol().toUpperCase() + "%");
        }
        if (filtri.getAa_fascicolo_padre() != null) {
            query.setParameter("aaFascicoloPadre", filtri.getAa_fascicolo_padre());
        }
        if (filtri.getAa_fascicolo_padre_da() != null && filtri.getAa_fascicolo_padre_a() != null) {
            query.setParameter("aaFascicoloPadre_da", filtri.getAa_fascicolo_padre_da());
            query.setParameter("aaFascicoloPadre_a", filtri.getAa_fascicolo_padre_a());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo_padre())) {
            query.setParameter("cdKeyFascicoloPadre", filtri.getCd_key_fascicolo_padre());
        }
        if (StringUtils.isNotBlank(cdKeyFascicolo_padre_da) && StringUtils.isNotBlank(cdKeyFascicolo_padre_a)) {
            query.setParameter("cdKeyFascicoloPadre_da", cdKeyFascicolo_padre_da);
            query.setParameter("cdKeyFascicoloPadre_a", cdKeyFascicolo_padre_a);
        }
        if (StringUtils.isNotBlank(filtri.getDs_oggetto_fascicolo_padre())) {
            query.setParameter("dsOggettoFascicoloPadre",
                    "%" + filtri.getDs_oggetto_fascicolo_padre().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getCd_registro_key_unita_doc())) {
            query.setParameter("cdRegistroKeyUnitaDoc", filtri.getCd_registro_key_unita_doc());
        }
        if (filtri.getAa_key_unita_doc() != null) {
            query.setParameter("aaKeyUnitaDoc", filtri.getAa_key_unita_doc());
        }
        if (filtri.getAa_key_unita_doc_da() != null && filtri.getAa_key_unita_doc_a() != null) {
            query.setParameter("aaKeyUnitaDoc_da", filtri.getAa_key_unita_doc_da());
            query.setParameter("aaKeyUnitaDoc_a", filtri.getAa_key_unita_doc_a());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_unita_doc())) {
            query.setParameter("cdKeyUnitaDoc", filtri.getCd_key_unita_doc());
        }
        if (StringUtils.isNotBlank(cdKeyUnitaDoc_da) && StringUtils.isNotBlank(cdKeyUnitaDoc_a)) {
            query.setParameter("cdKeyUnitaDoc_da", cdKeyUnitaDoc_da);
            query.setParameter("cdKeyUnitaDoc_a", cdKeyUnitaDoc_a);
        }

        if (StringUtils.isNotBlank(filtri.getTi_conservazione())) {
            query.setParameter("tiConservazione", filtri.getTi_conservazione());
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_classif())) {
            query.setParameter("flForzaContrClassif", filtri.getFl_forza_contr_classif());
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_numero())) {
            query.setParameter("flForzaContrNumero", filtri.getFl_forza_contr_numero());
        }
        if (StringUtils.isNotBlank(filtri.getFl_forza_contr_colleg())) {
            query.setParameter("flForzaContrColleg", filtri.getFl_forza_contr_colleg());
        }

        if (filtri.getTs_vers_fascicolo_da() != null && filtri.getTs_vers_fascicolo_a() != null) {
            query.setParameter("tsVersFascicolo_da", filtri.getTs_vers_fascicolo_da());
            query.setParameter("tsVersFascicolo_a", filtri.getTs_vers_fascicolo_a());
        }
        if (StringUtils.isNotBlank(filtri.getTi_esito())) {
            query.setParameter("tiEsito", filtri.getTi_esito());
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_conservazione())) {
            query.setParameter("tiStatoConservazione", filtri.getTi_stato_conservazione());
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_fasc_elenco_vers())) {
            query.setParameter("tiStatoFascElencoVers", filtri.getTi_stato_fasc_elenco_vers());
        }
        query.setParameter("userId", userId);
        result = query.getResultList();
        return result;
    }

    /**
     * Ricerca dei fascicoli annullati filtrandoli per Struttura di appartenenza, abilitazioni dell'utente corrente e
     * stato di conservazione ANNULLATO
     * 
     * @param filtri
     *            filtro ricerca fascicoli
     * @param idStrut
     *            id struttura
     * @param userId
     *            id utente
     * 
     * @return lista elementi di tipo FasVRicFascicoli
     */
    public List<FasVRicFascicoli> retrieveFascicoliAnnullati(RicercaFascicoliBean filtri, BigDecimal idStrut,
            long userId) {
        List<FasVRicFascicoli> result = null;
        String andClause = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT f FROM FasVRicFascicoli f "),
                whereClauseStr = new StringBuilder("WHERE f.idStrut = :idStrut ");

        if (filtri.getAa_fascicolo() != null) {
            whereClauseStr.append(andClause).append("f.aaFascicolo = :aaFascicolo ");
        }
        if (filtri.getAa_fascicolo_da() != null && filtri.getAa_fascicolo_a() != null) {
            whereClauseStr.append(andClause).append("f.aaFascicolo BETWEEN :aaFascicolo_da AND :aaFascicolo_a ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            whereClauseStr.append(andClause).append("f.cdKeyFascicolo = :cdKeyFascicolo ");
        }
        String cdKeyFascicolo_da = null, cdKeyFascicolo_a = null;
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo_da())
                && StringUtils.isNotBlank(filtri.getCd_key_fascicolo_a())) {
            cdKeyFascicolo_da = StringPadding.padString(filtri.getCd_key_fascicolo_da(), "0", 12,
                    StringPadding.PADDING_LEFT);
            cdKeyFascicolo_a = StringPadding.padString(filtri.getCd_key_fascicolo_a(), "0", 12,
                    StringPadding.PADDING_LEFT);
            whereClauseStr.append(andClause).append(
                    "FUNC('lpad', f.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicolo_da AND :cdKeyFascicolo_a ");
        }
        if (filtri.getNm_tipo_fascicolo() != null) {
            whereClauseStr.append(andClause).append("f.idTipoFascicolo = :idTipoFascicolo ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_composito_voce_titol())) {
            whereClauseStr.append(andClause).append("UPPER(f.cdCompositoVoceTitol) LIKE :cdCompositoVoceTitol ");
        }
        whereClauseStr.append(andClause).append("f.tiStatoConservazione = 'ANNULLATO' ");
        whereClauseStr.append(andClause).append("f.idUserIamCorrente = :userId");
        queryStr.append(whereClauseStr).append(" ORDER BY f.aaFascicolo, f.cdKeyFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        if (filtri.getAa_fascicolo() != null) {
            query.setParameter("aaFascicolo", filtri.getAa_fascicolo());
        }
        if (filtri.getAa_fascicolo_da() != null && filtri.getAa_fascicolo_a() != null) {
            query.setParameter("aaFascicolo_da", filtri.getAa_fascicolo_da());
            query.setParameter("aaFascicolo_a", filtri.getAa_fascicolo_a());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            query.setParameter("cdKeyFascicolo", filtri.getCd_key_fascicolo());
        }
        if (StringUtils.isNotBlank(cdKeyFascicolo_da) && StringUtils.isNotBlank(cdKeyFascicolo_a)) {
            query.setParameter("cdKeyFascicolo_da", cdKeyFascicolo_da);
            query.setParameter("cdKeyFascicolo_a", cdKeyFascicolo_a);
        }
        if (filtri.getNm_tipo_fascicolo() != null) {
            query.setParameter("idTipoFascicolo", filtri.getNm_tipo_fascicolo());
        }
        if (StringUtils.isNotBlank(filtri.getCd_composito_voce_titol())) {
            query.setParameter("cdCompositoVoceTitol", "%" + filtri.getCd_composito_voce_titol().toUpperCase() + "%");
        }
        query.setParameter("userId", userId);
        result = query.getResultList();
        return result;
    }

    // FLAGS
    public List<MonVChkFascByAmb> retrieveMonFascicoliByAmbUser(BigDecimal idAmbiente, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascByAmb.findByAmbUser", MonVChkFascByAmb.class);
            query.setParameter("idAmbiente", idAmbiente);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per ambiente [{}] e utente [{}]", idAmbiente, idUser,
                    ex);
            throw ex;
        }
    }

    // CONTEGGI
    public List<MonVCntFascByAmb> retrieveCntMonFascicoliByAmbUser(BigDecimal idAmbiente, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascByAmb.findByAmbUser", MonVCntFascByAmb.class);
            query.setParameter("idAmbiente", idAmbiente);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per ambiente [{}] e utente [{}]", idAmbiente, idUser,
                    ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascByEnte> retrieveMonFascicoliByEnteUser(BigDecimal idEnte, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascByEnte.findByEnteUser",
                    MonVChkFascByEnte.class);
            query.setParameter("idEnte", idEnte);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per ente [{}] e utente [{}]", idEnte, idUser, ex);
            throw ex;
        }
    }

    // CONTEGGI
    public List<MonVCntFascByEnte> retrieveCntMonFascicoliByEnteUser(BigDecimal idEnte, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascByEnte.findByEnteUser",
                    MonVCntFascByEnte.class);
            query.setParameter("idEnte", idEnte);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per ente [{}] e utente [{}]", idEnte, idUser, ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascByStrut> retrieveMonFascicoliByStrutUser(BigDecimal idStrut, BigDecimal idUserIam) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascByStrut.findByStrutUser",
                    MonVChkFascByStrut.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("idUserIam", idUserIam);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per struttura [{}]", idStrut, ex);
            throw ex;
        }
    }

    // CONTEGGI
    public List<MonVCntFascByStrut> retrieveCntMonFascicoliByStrutUserId(BigDecimal idStrut, BigDecimal idUserIam) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascByStrut.findByStrutUserId",
                    MonVCntFascByStrut.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("idUserIam", idUserIam);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per struttura [{}]", idStrut, ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascByTiFasc> retrieveMonFascicoliByTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascByTiFasc.findByTiFasc",
                    MonVChkFascByTiFasc.class);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per tipo fascicolo [{}]", idTipoFascicolo, ex);
            throw ex;
        }
    }

    // CONTEGGIO
    public List<MonVCntFascByTiFasc> retrieveCntMonFascicoliByTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascByTiFasc.findByTiFasc",
                    MonVCntFascByTiFasc.class);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring per tipo fascicolo [{}]", idTipoFascicolo, ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascKoByAmb> retrieveMonFascicoliKoByAmbUser(BigDecimal idAmbiente, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascKoByAmb.findByAmbUser",
                    MonVChkFascKoByAmb.class);
            query.setParameter("idAmbiente", idAmbiente);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per ambiente [{}] e utente [{}]", idAmbiente, idUser,
                    ex);
            throw ex;
        }
    }

    // CONTEGGI
    public List<MonVCntFascKoByAmb> retrieveCntMonFascicoliKoByAmbUser(BigDecimal idAmbiente, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascKoByAmb.findByAmbUser",
                    MonVCntFascKoByAmb.class);
            query.setParameter("idAmbiente", idAmbiente);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per ambiente [{}] e utente [{}]", idAmbiente, idUser,
                    ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascKoByEnte> retrieveMonFascicoliKoByEnteUser(BigDecimal idEnte, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascKoByEnte.findByEnteUser",
                    MonVChkFascKoByEnte.class);
            query.setParameter("idEnte", idEnte);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per ente [{}] e utente [{}]", idEnte, idUser, ex);
            throw ex;
        }
    }

    // CONTEGGIO
    public List<MonVCntFascKoByEnte> retrieveCntMonFascicoliKoByEnteUser(BigDecimal idEnte, BigDecimal idUser) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascKoByEnte.findByEnteUser",
                    MonVCntFascKoByEnte.class);
            query.setParameter("idEnte", idEnte);
            query.setParameter("idUser", idUser);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per ente [{}] e utente [{}]", idEnte, idUser, ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascKoByStrut> retrieveMonFascicoliKoByStrutUser(BigDecimal idStrut, BigDecimal idUserIam) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascKoByStrut.findByStrutUser",
                    MonVChkFascKoByStrut.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("idUserIam", idUserIam);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per struttura [{}]", idStrut, ex);
            throw ex;
        }
    }

    // CONTEGGI
    public List<MonVCntFascKoByStrut> retrieveCntMonFascicoliKoByStrutUserId(BigDecimal idStrut, BigDecimal idUserIam) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascKoByStrut.findByStrutUserId",
                    MonVCntFascKoByStrut.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("idUserIam", idUserIam);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per struttura [{}] e utente [{}]", idStrut,
                    idUserIam, ex);
            throw ex;
        }
    }

    // FLAGS
    public List<MonVChkFascKoByTiFasc> retrieveMonFascicoliKoByTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVChkFascKoByTiFasc.findByTiFasc",
                    MonVChkFascKoByTiFasc.class);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per tipo fascicolo [{}]", idTipoFascicolo, ex);
            throw ex;
        }
    }

    // CONTEGGIO
    public List<MonVCntFascKoByTiFasc> retrieveCntMonFascicoliKoByTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonVCntFascKoByTiFasc.findByTiFasc",
                    MonVCntFascKoByTiFasc.class);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per tipo fascicolo [{}]", idTipoFascicolo, ex);
            throw ex;
        }
    }

    public List<VrsSesFascicoloKo> retrieveSessioniFalliteByIdFascKo(BigDecimal idFascicoloKo) {
        try {
            Query query = getEntityManager().createNamedQuery("VrsSesFascicoloKo.findByFascicoloKo",
                    VrsSesFascicoloKo.class);
            query.setParameter("idFascicoloKo", idFascicoloKo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del monitoring KO per ID tipo fascicolo [{}]", idFascicoloKo, ex);
            throw ex;
        }
    }

    public VrsSesFascicoloKo getSessioneFallitaByIdSess(BigDecimal idSesFascicoloKo) {
        VrsSesFascicoloKo ses = null;
        try {
            ses = findById(VrsSesFascicoloKo.class, idSesFascicoloKo);
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione della sessione errata con id [{}]", idSesFascicoloKo, ex);
            throw ex;
        }
        return ses;
    }

    public List retrieveVLisFascKo(BigDecimal idUser, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idTipoFascicolo, Date[] dateValidate, BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA,
            String rangeNumeroDa, String rangeNumeroA, String statoSessione, String cdClasseErr, String cdErr) {
        List result = null;
        Date d1 = null;
        Date d2 = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery cq = null;
            Root entity = null;
            if ((cdErr == null || cdErr.equals("")) && (cdClasseErr == null || cdClasseErr.equals(""))) {
                cq = cb.createQuery(MonVLisFascKo.class);
                entity = cq.from(MonVLisFascKo.class);
            } else {
                cq = cb.createQuery(MonVLisFascKoByErr.class);
                entity = cq.from(MonVLisFascKoByErr.class);
            }
            cq.select(entity);
            cq.distinct(true);
            // ORDER BY
            cq.orderBy(cb.desc(entity.get("tsIniFirstSes")));
            List<Predicate> condizioni = new ArrayList<Predicate>();
            condizioni.add(cb.equal(entity.get("idUserIam"), idUser));
            condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
            if ((idEnte != null) && (!idEnte.equals(""))) {
                condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
            }
            if ((idStrut != null) && (!idStrut.equals(""))) {
                condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
            }
            if ((idTipoFascicolo != null) && (!idTipoFascicolo.equals(""))) {
                condizioni.add(cb.equal(entity.get("idTipoFascicolo"), idTipoFascicolo));
            }
            if (dateValidate != null) {
                Calendar dataDa = Calendar.getInstance();
                dataDa.setTime(dateValidate[0]);
                dataDa.set(Calendar.MILLISECOND, 0);
                dataDa.set(Calendar.HOUR_OF_DAY, 0);
                dataDa.set(Calendar.MINUTE, 0);
                dataDa.set(Calendar.SECOND, 0);
                dataDa.set(Calendar.MILLISECOND, 0);
                d1 = dataDa.getTime();
                Calendar dataA = Calendar.getInstance();
                dataA.setTime(dateValidate[1]);
                dataA.set(Calendar.HOUR_OF_DAY, 0);
                dataA.set(Calendar.MINUTE, 0);
                dataA.set(Calendar.SECOND, 0);
                dataA.set(Calendar.MILLISECOND, 0);
                d2 = dataA.getTime();
                // Per forzare l'indice su ORACLE!
                // condizioni.add(cb.between(cb.function("TRUNC", Date.class, entity.get("tsIniFirstSes")),
                // d1,
                // d2 ));
                condizioni.add(cb.between(cb.function("TRUNC", Date.class, entity.get("tsIniLastSes")),
                        cb.function("TRUNC", Date.class,
                                cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
                                        cb.parameter(String.class, "f1"))),
                        cb.function("TRUNC", Date.class, cb.function("TO_DATE", String.class,
                                cb.parameter(String.class, "d2"), cb.parameter(String.class, "f2")))));
                // cb.function(cdErr, type, args)
                // condizioni.add(cb.between(entity.get("tsIniFirstSes"), dateValidate[0], dateValidate[1]));
            }
            if (statoSessione != null && (!statoSessione.equals(""))) {
                condizioni.add(cb.equal(entity.get("tiStatoFascicoloKo"), statoSessione));
            }
            if (cdErr != null && (!cdErr.equals(""))) {
                condizioni.add(cb.equal(entity.get("cdErrSacer"), cdErr));
                /*
                 * List<DecClasseErrSacer> l=this.retrieveClasseErrSacerByCodice(cdErr); if (l!=null&&l.size()>0) {
                 * condizioni.add(cb.equal(entity.get("idErrSacer"), l.get(0).getIdClasseErrSacer())); }
                 */
            }
            if (cdClasseErr != null && (!cdClasseErr.equals(""))) {
                condizioni.add(cb.equal(entity.get("cdClasseErrSacer"), cdClasseErr));
            }
            if (rangeAnnoA != null && (!rangeAnnoA.equals(""))) {
                condizioni.add(cb.between(entity.get("aaFascicolo"), rangeAnnoDa, rangeAnnoA));
            } else if (rangeAnnoDa != null && (!rangeAnnoDa.equals(""))) {
                condizioni.add(cb.equal(entity.get("aaFascicolo"), rangeAnnoDa));
            }
            if (rangeNumeroA != null && (!rangeNumeroA.equals(""))) {
                condizioni.add(cb.between(entity.get("cdKeyFascicolo"), rangeNumeroA, rangeNumeroA));
            } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
                condizioni.add(cb.equal(entity.get("cdKeyFascicolo"), rangeNumeroDa));
            }
            cq.where(condizioni.toArray(new Predicate[] {}));
            TypedQuery q = getEntityManager().createQuery(cq);
            if (dateValidate != null) {
                q.setParameter("d1", sf.format(d1));
                q.setParameter("d2", sf.format(d2));
                q.setParameter("f1", "DD/MM/YYYY");
                q.setParameter("f2", "DD/MM/YYYY");
            }

            result = q.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione Della lista fascicoli derivanti da versamenti falliti", ex);
            throw ex;
        }
        return result;
    }

    public List retrieveVLisFasc(BigDecimal idUser, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idTipoFascicolo, Date[] dateValidate, BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA,
            String rangeNumeroDa, String rangeNumeroA, String statoIndiceAip, Set<String> statiConservazione,
            String flSesFascicoloKo) {
        List result = null;
        Date d1 = null;
        Date d2 = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery cq = null;
            Root entity = null;
            if (statoIndiceAip != null
                    && (statoIndiceAip.equals(ElencoEnums.StatoGenerazioneIndiceAip.IN_ATTESA_SCHED.name())
                            || statoIndiceAip.equals(ElencoEnums.StatoGenerazioneIndiceAip.NON_SELEZ_SCHED.name()))) {
                cq = cb.createQuery(MonVLisFascDaElab.class);
                entity = cq.from(MonVLisFascDaElab.class);
            } else {
                cq = cb.createQuery(MonVLisFasc.class);
                entity = cq.from(MonVLisFasc.class);
            }
            cq.select(entity);
            // ORDER BY
            cq.orderBy(cb.desc(entity.get("tsVersFascicolo")));
            List<Predicate> condizioni = new ArrayList<Predicate>();
            condizioni.add(cb.equal(entity.get("idUserIam"), idUser));
            condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
            if (idEnte != null) {
                condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
            }
            if (idStrut != null) {
                condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
            }
            if (idTipoFascicolo != null) {
                condizioni.add(cb.equal(entity.get("idTipoFascicolo"), idTipoFascicolo));
            }
            if (dateValidate != null) {
                Calendar dataDa = Calendar.getInstance();
                dataDa.setTime(dateValidate[0]);
                dataDa.set(Calendar.MILLISECOND, 0);
                dataDa.set(Calendar.HOUR_OF_DAY, 0);
                dataDa.set(Calendar.MINUTE, 0);
                dataDa.set(Calendar.SECOND, 0);
                dataDa.set(Calendar.MILLISECOND, 0);
                d1 = dataDa.getTime();
                Calendar dataA = Calendar.getInstance();
                dataA.setTime(dateValidate[1]);
                dataA.set(Calendar.HOUR_OF_DAY, 0);
                dataA.set(Calendar.MINUTE, 0);
                dataA.set(Calendar.SECOND, 0);
                dataA.set(Calendar.MILLISECOND, 0);
                d2 = dataA.getTime();
                // Per forzare l'indice su ORACLE!
                // condizioni.add(cb.between(cb.function("TRUNC", Date.class, entity.get("tsVersFascicolo")), d1, d2));
                // condizioni.add(cb.between(entity.get("tsVersFascicolo"), dateValidate[0], dateValidate[1]));

                condizioni.add(cb.between(cb.function("TRUNC", Date.class, entity.get("tsVersFascicolo")),
                        cb.function("TRUNC", Date.class,
                                cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
                                        cb.parameter(String.class, "f1"))),
                        cb.function("TRUNC", Date.class, cb.function("TO_DATE", String.class,
                                cb.parameter(String.class, "d2"), cb.parameter(String.class, "f2")))));

            }
            if (statoIndiceAip != null && !statoIndiceAip.equals("")) {
                condizioni.add(cb.equal(entity.get("tiStatoFascElencoVers"), statoIndiceAip));
            }
            if (flSesFascicoloKo != null && !flSesFascicoloKo.equals("")) {
                condizioni.add(cb.equal(entity.get("flSesFascicoloKo"), flSesFascicoloKo));
            }
            if (rangeAnnoA != null && !rangeAnnoA.equals("")) {
                condizioni.add(cb.between(entity.get("aaFascicolo"), rangeAnnoDa, rangeAnnoA));
            } else if (rangeAnnoDa != null && !rangeAnnoDa.equals("")) {
                condizioni.add(cb.equal(entity.get("aaFascicolo"), rangeAnnoDa));
            }
            if (rangeNumeroA != null && !rangeNumeroA.equals("")) {
                condizioni.add(cb.between(entity.get("cdKeyFascicolo"), rangeNumeroA, rangeNumeroA));
            } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
                condizioni.add(cb.equal(entity.get("cdKeyFascicolo"), rangeNumeroDa));
            }
            if (statiConservazione != null && !statiConservazione.isEmpty()) {
                condizioni.add(cb.and(entity.get("tiStatoConservazione").in(statiConservazione)));
            }
            cq.where(condizioni.toArray(new Predicate[] {}));
            TypedQuery q = getEntityManager().createQuery(cq);
            if (dateValidate != null) {
                q.setParameter("d1", sf.format(d1));
                q.setParameter("d2", sf.format(d2));
                q.setParameter("f1", "DD/MM/YYYY");
                q.setParameter("f2", "DD/MM/YYYY");
            }
            result = q.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione Della lista fascicoli derivanti da versamenti falliti", ex);
            throw ex;
        }
        return result;
    }

    public void bulkDeleteCriteriRaggrFasc(List<Long> idCriterioRaggrFascList) {
        if (!idCriterioRaggrFascList.isEmpty()) {
            String queryStr = "DELETE FROM DecCriterioRaggrFasc criterioRaggrFasc "
                    + "WHERE criterioRaggrFasc.idCriterioRaggrFasc IN :idCriterioRaggrFascList";
            Query q = getEntityManager().createQuery(queryStr);
            q.setParameter("idCriterioRaggrFascList", idCriterioRaggrFascList);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    public void bulkUpdateCriteriRaggrFasc(List<Long> idCriterioRaggrFascList) {
        if (!idCriterioRaggrFascList.isEmpty()) {
            String queryStr = "UPDATE DecCriterioRaggrFasc criterioRaggrFasc SET criterioRaggrFasc.flFiltroTipoFascicolo = '0' "
                    + "WHERE criterioRaggrFasc.idCriterioRaggrFasc IN :idCriterioRaggrFascList";
            Query q = getEntityManager().createQuery(queryStr);
            q.setParameter("idCriterioRaggrFascList", idCriterioRaggrFascList);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    /**
     * Verifica l'esistenza di fascicoli versati del tipo e per il range di anni passati in ingresso.
     *
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * @param aaIniTipoFascicolo
     *            anno inizio tipo fascicolo
     * @param aaFinTipoFascicolo
     *            anno fine tipo fascicolo
     * 
     * @return true/false
     */
    public boolean existFascicoliVersatiPerTipoFascicolo(BigDecimal idTipoFascicolo, BigDecimal aaIniTipoFascicolo,
            BigDecimal aaFinTipoFascicolo) {
        if (idTipoFascicolo != null) {
            StringBuilder queryStr = new StringBuilder("SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo "
                    + "WHERE tipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                    + "AND EXISTS (SELECT fascicolo FROM FasFascicolo fascicolo WHERE fascicolo.decTipoFascicolo = tipoFascicolo ");
            if (aaIniTipoFascicolo != null && aaFinTipoFascicolo != null) {
                queryStr.append("AND fascicolo.aaFascicolo BETWEEN :aaIniTipoFascicolo AND :aaFinTipoFascicolo");
            }
            queryStr.append(")");
            Query query = getEntityManager().createQuery(queryStr.toString());
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            if (aaIniTipoFascicolo != null && aaFinTipoFascicolo != null) {
                query.setParameter("aaIniTipoFascicolo", aaIniTipoFascicolo);
                query.setParameter("aaFinTipoFascicolo", aaFinTipoFascicolo);
            }
            return !query.getResultList().isEmpty();
        } else {
            throw new IllegalArgumentException("Parametro idTipoFascicolo nullo");
        }
    }

    /**
     * Verifica l'esistenza di fascicoli versati del modello passato in ingresso.
     *
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * 
     * @return true/false
     */
    public boolean existFascicoliVersatiPerModelloFascicolo(BigDecimal idModelloXsdFascicolo,
            BigDecimal idTipoFascicolo) {
        if (idModelloXsdFascicolo != null) {
            String queryStr = "SELECT modelloFascicolo FROM DecModelloXsdFascicolo modelloFascicolo "
                    + "WHERE modelloFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo "
                    + "AND EXISTS (SELECT xmlFascicolo FROM FasXmlFascicolo xmlFascicolo "
                    + "JOIN xmlFascicolo.fasFascicolo.decTipoFascicolo tipoFascicolo "
                    + "WHERE xmlFascicolo.decModelloXsdFascicolo = modelloFascicolo "
                    + "AND tipoFascicolo.idTipoFascicolo = :idTipoFascicolo)";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);

            return !query.getResultList().isEmpty();
        } else {
            throw new IllegalArgumentException("Parametro idModelloXsdFascicolo nullo");
        }
    }

    /**
     * Verifica l'esistenza di fascicoli versati per la struttura passata in ingresso.
     *
     * @param idStrut
     *            id struttura
     * 
     * @return true/false
     */
    public boolean existFascicoliVersatiPerStruttura(BigDecimal idStrut) {
        if (idStrut != null) {
            String queryStr = "SELECT strut FROM OrgStrut strut WHERE strut.idStrut = :idStrut "
                    + "AND EXISTS (SELECT fascicolo FROM FasFascicolo fascicolo WHERE fascicolo.orgStrut = strut) ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idStrut", idStrut);
            return !query.getResultList().isEmpty();
        } else {
            throw new IllegalArgumentException("Parametro idStrut nullo");
        }
    }

    /**
     * Verifica l'esistenza di selezioni di criteri di raggruppamento fascicoli sul criterio del tipo passato in
     * ingresso.
     *
     * @param idCriterioRaggrFasc
     *            id criterio raggruppamento
     * @param tiSel
     *            tipo selettore
     * 
     * @return int dimensione
     */
    public int countSelCriteriRaggrFascPerTipo(BigDecimal idCriterioRaggrFasc, String tiSel) {
        List<DecSelCriterioRaggrFasc> result = retrieveSelCriterioRaggrFascicoli(idCriterioRaggrFasc, tiSel);
        return result.size();
    }

    public FasVVisFascicolo retrieveFasVVisFascicolo(long idFascicolo) {
        FasVVisFascicolo result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasVVisFascicolo.find", FasVVisFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = (FasVVisFascicolo) query.getSingleResult();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasVVisFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public FasVRicFascicoli retrieveFasVRicFascicoli(long idFascicolo) {
        FasVRicFascicoli result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasVRicFascicoli.findById", FasVRicFascicoli.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = (FasVRicFascicoli) query.getSingleResult();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasVRicFascicoli per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasVLisUdInFasc> retrieveFasVLisUdInFasc(long idFascicolo, long userId) {
        List<FasVLisUdInFasc> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasVLisUdInFasc.find", FasVLisUdInFasc.class);
            query.setParameter("idFascicolo", idFascicolo);
            query.setParameter("userId", userId);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasVLisUdInFasc per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<Object[]> findCountFascicoliVersatiNelGiorno(Date data) {
        List<Object[]> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasFascicolo.findCountFascicoliVersatiNelGiorno",
                    FasFascicolo.class);
            query.setParameter("data", data);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione dei conteggi fascicoli versati per la data [{}]", data, ex);
            throw ex;
        }
        return result;
    }

    public List<Object[]> findCountFascicoliNonVersatiNelGiorno(Date data) {
        List<Object[]> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("VrsFascicoloKo.findCountFascicoliNonVersatiNelGiorno",
                    VrsFascicoloKo.class);
            query.setParameter("data", data);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione dei conteggi fascicoli non versati per la data [{}]", data, ex);
            throw ex;
        }
        return result;
    }

    // Estrae i fascicoli NON ANNULLATI
    public List<FasFascicolo> retrieveFasFascicoloByStrutAnnoNumValid(OrgStrut strut, long anno, String numero) {
        List<FasFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasFascicolo.findByStrutAnnoNum", FasFascicolo.class);
            query.setParameter("orgStrut", strut);
            query.setParameter("aaFascicolo", anno);
            query.setParameter("cdKeyFascicolo", numero);
            Calendar c = Calendar.getInstance();
            c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            c.set(Calendar.MILLISECOND, 0);
            query.setParameter("dtAnnull", c.getTime());
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasFascicolo", ex);
            throw ex;
        }
        return result;
    }

    public List<VrsFascicoloKo> retrieveFasNonVersatoByStrutAnnoNum(OrgStrut strut, long anno, String numero,
            Boolean pessimisticLock) {
        List<VrsFascicoloKo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("VrsFascicoloKo.findByStrutAnnoNum",
                    VrsFascicoloKo.class);
            if (pessimisticLock) {
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            }
            query.setParameter("orgStrut", strut);
            query.setParameter("aaFascicolo", anno);
            query.setParameter("cdKeyFascicolo", numero);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle VrsFascicoloKo", ex);
            throw ex;
        }
        return result;
    }

    public List<FasAmminPartec> retrieveFasAmminPartec(long idFascicolo) {
        List<FasAmminPartec> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasAmminPartec.find", FasAmminPartec.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasAmminPartec per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasSogFascicolo> retrieveFasSogFascicolo(long idFascicolo) {
        List<FasSogFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasSogFascicolo.find", FasSogFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasSogFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasRespFascicolo> retrieveFasRespFascicolo(long idFascicolo) {
        List<FasRespFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasRespFascicolo.find", FasRespFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasRespFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasUniOrgRespFascicolo> retrieveFasUniOrgRespFascicolo(long idFascicolo) {
        List<FasUniOrgRespFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasUniOrgRespFascicolo.find",
                    FasUniOrgRespFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasUniOrgRespFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasLinkFascicolo> retrieveFasLinkFascicolo(long idFascicolo) {
        List<FasLinkFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasLinkFascicolo.find", FasLinkFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasLinkFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<FasLinkFascicolo> retrieveFasLinkFascicoloParent(long idFascicolo) {
        List<FasLinkFascicolo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("FasLinkFascicolo.findByIdFasLink",
                    FasLinkFascicolo.class);
            query.setParameter("idFascicolo", idFascicolo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle FasLinkFascicolo per il fascicolo [{}]", idFascicolo, ex);
            throw ex;
        }
        return result;
    }

    public List<ElvVRicElencoFascByFas> retrieveFasElvFascicolo(long idFascicolo) {
        List<ElvVRicElencoFascByFas> result = null;
        try {
            String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByFas(u.idElencoVersFasc, u.tiStato, u.aaFascicoloElenco, u.niFascVersElenco, "
                    + "u.dlMotivoChius, u.tsCreazioneElenco, u.dtChiusura, u.dtFirma, u.idCriterioRaggrFasc, "
                    + "u.nmCriterioRaggr, u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, "
                    + "u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo) " + "FROM ElvVRicElencoFascByFas u "
                    + "WHERE u.idFascicolo = :idFascicolo "
                    // Filtro per gestire l'eventuale presenza, a seguito di uno o più errori di firma, di più stati
                    // CHIUSO, in modo da considerare solo l'ultimo registrato
                    + "AND ((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT s.tsStato FROM ElvStatoElencoVersFasc s WHERE s.idStatoElencoVersFasc = u.idStatoElencoVersFascCor AND s.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)) "
                    // Filtro per gestire l'eventuale presenza, a seguito di più stati CHIUSO, di molteplici stati
                    // FIRMATO, in modo da considerare solo lo stato di chiusura registrato prima della firma
                    + "OR ((u.dtFirma IS NOT NULL) AND (u.dtChiusura = (SELECT MAX(s1.tsStato) FROM ElvStatoElencoVersFasc s1 WHERE s1.tiStato = :statoChiuso AND s1.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)))) "
                    + "ORDER BY u.tsCreazioneElenco ";

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idFascicolo", idFascicolo);
            query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle ElvVRicElencoFascByFas per il fascicolo [{}]", idFascicolo, ex);
        }
        return result;
    }

    public List<DecClasseErrSacer> retrieveClasseErrSacerByTipiUso(List<String> tipiUsoErr) {
        List<DecClasseErrSacer> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("DecClasseErrSacer.findByTipiUsoErr",
                    DecClasseErrSacer.class);
            query.setParameter("tipiUsoErr", tipiUsoErr);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle DecClasseErrSacer per il tipi uso [{}]", tipiUsoErr, ex);
            throw ex;
        }
        return result;
    }

    public List<DecClasseErrSacer> retrieveClasseErrSacerByCodice(String cdClasseErrSacer) {
        List<DecClasseErrSacer> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("DecClasseErrSacer.findByCodice",
                    DecClasseErrSacer.class);
            query.setParameter("cdClasseErrSacer", cdClasseErrSacer);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle DecClasseErrSacer per il cdClasseErrSacer [{}]",
                    cdClasseErrSacer, ex);
            throw ex;
        }
        return result;
    }

    public List<VrsVUpdFascicoloKo> retrieveVrsVUpdFascicoloKoByFascKo(long idFascicoloKo) {
        List<VrsVUpdFascicoloKo> result = null;
        try {
            Query query = getEntityManager().createNamedQuery("VrsVUpdFascicoloKo.findByIdFascKo",
                    VrsVUpdFascicoloKo.class);
            query.setParameter("idFascicoloKo", idFascicoloKo);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di VrsSesFascicoloKo", ex);
            throw ex;
        }
        return result;
    }

    public List<DecErrSacer> retrieveErrSacerByCodClasse(String codClasse) {
        List<DecErrSacer> result = null;

        try {
            Query query = getEntityManager().createNamedQuery("DecErrSacer.findByCodClasse", DecErrSacer.class);
            query.setParameter("codClasse", codClasse);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione delle DecErrSacer per il codice [{}]", codClasse, ex);
            throw ex;
        }
        return result;
    }

    public List<VrsSesFascicoloErr> retrieveSessFascErrate(Date dataDa, Date dataA, String statoSessione,
            String cdClasseErr, String cdErr) {
        List<VrsSesFascicoloErr> result = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery cq = null;
            cq = cb.createQuery(VrsSesFascicoloErr.class);
            Root entity = cq.from(VrsSesFascicoloErr.class);
            cq.select(entity);
            // ORDER BY
            cq.orderBy(cb.desc(entity.get("tsIniSes")));
            List<Predicate> condizioni = new ArrayList<Predicate>();
            if (dataDa != null && dataA != null) {
                condizioni.add(cb.between(entity.get("tsIniSes"), dataDa, dataA));
            }
            if (statoSessione != null && !statoSessione.equals("")) {
                condizioni.add(cb.equal(entity.get("tiStatoSes"), statoSessione));
            }
            if (cdErr != null && !cdErr.equals("")) {
                condizioni.add(cb.equal(entity.get("decErrSacer").get("cdErr"), cdErr));
            }
            if (cdClasseErr != null && !cdClasseErr.equals("")) {
                condizioni.add(cb.equal(entity.get("decErrSacer").get("decClasseErrSacer").get("cdClasseErrSacer"),
                        cdClasseErr));
            }
            cq.where(condizioni.toArray(new Predicate[] {}));
            TypedQuery q = getEntityManager().createQuery(cq);
            result = q.getResultList();
        } catch (RuntimeException ex) {
            logger.error(
                    "Errore nell'estrazione di VrsSesFascicoloErr con dataDa [{}], dataA [{}], statoSessione [{}], cdClasseErr [{}], cdErr [{}]",
                    dataDa, dataA, statoSessione, cdClasseErr, cdErr, ex);
            throw ex;
        }
        return result;
    }

    public VrsSesFascicoloErr retrieveDettSessFascErr(BigDecimal idSess) {
        try {
            return getEntityManager().find(VrsSesFascicoloErr.class, idSess.longValue());
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione del dettaglio sessione fascicolo errata, idSessione=[{}]", idSess, ex);
            throw ex;
        }
    }

    // CONTEGGIO FASCICOLI KO
    public List<MonContaFascicoliKo> retrieveMonContaFascicoliKoByChiaveTotaliz(Date dtRifConta, OrgStrut orgStrut,
            String tiStatoFascicoloKo, long aaFascicolo, DecTipoFascicolo decTipoFascicolo, boolean lockEsclusivo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonContaFascicoliKo.findByChiaveTotaliz",
                    MonContaFascicoliKo.class);
            if (lockEsclusivo) {
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            }
            query.setParameter("dtRifConta", dtRifConta);
            query.setParameter("orgStrut", orgStrut);
            query.setParameter("tiStatoFascicoloKo", tiStatoFascicoloKo);
            query.setParameter("aaFascicolo", aaFascicolo);
            query.setParameter("decTipoFascicolo", decTipoFascicolo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di MonContaFascicoliKo", ex);
            throw ex;
        }
    }

    public List<MonContaFascicoli> retrieveMonContaFascicoliByChiaveTotaliz(BigDecimal idStrut, Date dtRifConta,
            BigDecimal idTipoFascicolo, BigDecimal aaFascicolo, BigDecimal idUserIam) {
        try {
            Query query = getEntityManager().createNamedQuery("MonContaFascicoli.findByChiaveTotalizz",
                    MonContaFascicoli.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("dtRifConta", dtRifConta);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            query.setParameter("aaFascicolo", aaFascicolo);
            query.setParameter("idUserIam", idUserIam);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di MonContaFascicoli", ex);
            throw ex;
        }
    }

    public List<MonContaFascicoliKo> retrieveMonContaFascicoliNonVersByChiaveTotaliz(BigDecimal idStrut,
            Date dtRifConta, BigDecimal idTipoFascicolo, BigDecimal aaFascicolo, String tiStatoFascicoloKo) {
        try {
            Query query = getEntityManager().createNamedQuery("MonContaFascicoliKo.findByChiaveTotalizIds",
                    MonContaFascicoliKo.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("dtRifConta", dtRifConta);
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            query.setParameter("aaFascicolo", aaFascicolo);
            query.setParameter("tiStatoFascicoloKo", tiStatoFascicoloKo);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di MonContaFascicoliKo", ex);
            throw ex;
        }
    }

    public List<OrgVChkPartitionFascByAa> retrieveOrgVChkPartitionFascByAaByStrutAnno(BigDecimal idStrut,
            BigDecimal anno) {
        try {
            Query query = getEntityManager().createNamedQuery("OrgVChkPartitionFascByAa.findByStrutAnno",
                    OrgVChkPartitionFascByAa.class);
            query.setParameter("idStrut", idStrut);
            query.setParameter("anno", anno);
            return query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di OrgVChkPartitionFascByAa", ex);
            throw ex;
        }
    }

    public List<DecCriterioRaggrFasc> retrieveCriteriRaggrFascicoli(
            CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc, boolean filterValid)
            throws EMFError {
        List<DecCriterioRaggrFasc> result = null;

        try {

            StringBuilder queryStr = new StringBuilder(
                    "SELECT DISTINCT crf FROM DecCriterioRaggrFasc crf LEFT JOIN crf.decSelCriterioRaggrFascicoli scrf ");
            String whereWord = "WHERE ";
            /* Inserimento nella query del filtro ID_AMBIENTE */
            BigDecimal idAmbiente = filtriCriteriRaggrFasc.getId_ambiente().parse();
            if (idAmbiente != null) {
                queryStr.append(whereWord).append("crf.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
                whereWord = "AND ";
            }
            /* Inserimento nella query del filtro ID_ENTE */
            BigDecimal idEnte = filtriCriteriRaggrFasc.getId_ente().parse();
            if (idEnte != null) {
                queryStr.append(whereWord).append("crf.orgStrut.orgEnte.idEnte = :idEnte ");
                whereWord = "AND ";
            }
            /* Inserimento nella query del filtro ID_STRUT */
            BigDecimal idStrut = filtriCriteriRaggrFasc.getId_strut().parse();
            if (idStrut != null) {
                queryStr.append(whereWord).append("crf.orgStrut.idStrut = :idStrut ");
                whereWord = "AND ";
            }
            String nmCriterioRaggr = filtriCriteriRaggrFasc.getNm_criterio_raggr().parse();
            if (nmCriterioRaggr != null) {
                queryStr.append(whereWord).append("UPPER(crf.nmCriterioRaggr) LIKE :nmCriterioRaggr ");
                whereWord = "AND ";
            }
            String flCriterioRaggrStandard = filtriCriteriRaggrFasc.getFl_criterio_raggr_standard().parse();
            if (flCriterioRaggrStandard != null) {
                queryStr.append(whereWord).append("crf.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
                whereWord = "AND ";
            }
            BigDecimal idTipoFascicolo = filtriCriteriRaggrFasc.getId_tipo_fascicolo().parse();
            if (idTipoFascicolo != null) {
                queryStr.append(whereWord).append("scrf.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
                whereWord = "AND ";
            }
            String cdCompositoVoceTitol = filtriCriteriRaggrFasc.getCd_composito_voce_titol().parse();
            if (cdCompositoVoceTitol != null) {
                queryStr.append(whereWord).append("scrf.decVoceTitol.cdCompositoVoceTitol LIKE :cdCompositoVoceTitol ");
                whereWord = "AND ";
            }
            BigDecimal aaFascicolo = filtriCriteriRaggrFasc.getAa_fascicolo().parse();
            if (aaFascicolo != null) {
                queryStr.append(whereWord).append(
                        "(crf.aaFascicolo = :aaFascicolo OR (crf.aaFascicoloDa <= :aaFascicolo AND crf.aaFascicoloA >= :aaFascicolo)) ");
                whereWord = "AND ";
            }
            String criterioAttivo = filtriCriteriRaggrFasc.getCriterio_attivo().parse();
            if (criterioAttivo != null) {
                if (criterioAttivo.equals("1")) {
                    queryStr.append(whereWord).append("crf.dtSoppres >= :data AND crf.dtIstituz <= :data ");
                } else {
                    queryStr.append(whereWord).append("crf.dtSoppres < :data OR crf.dtIstituz > :data ");
                }
                whereWord = "AND ";
            }
            if (filterValid) {
                queryStr.append(whereWord).append("crf.dtIstituz <= :filterDate AND crf.dtSoppres >= :filterDate ");
            }

            queryStr.append("ORDER BY crf.nmCriterioRaggr ");

            Query query = getEntityManager().createQuery(queryStr.toString());

            if (idAmbiente != null) {
                query.setParameter("idAmbiente", idAmbiente);
            }
            if (idEnte != null) {
                query.setParameter("idEnte", idEnte);
            }
            if (idStrut != null) {
                query.setParameter("idStrut", idStrut);
            }
            if (nmCriterioRaggr != null) {
                query.setParameter("nmCriterioRaggr", "%" + nmCriterioRaggr.toUpperCase() + "%");
            }
            if (flCriterioRaggrStandard != null) {
                query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
            }
            if (aaFascicolo != null) {
                query.setParameter("aaFascicolo", aaFascicolo);
            }
            if (idTipoFascicolo != null) {
                query.setParameter("idTipoFascicolo", idTipoFascicolo);
            }
            if (cdCompositoVoceTitol != null) {
                query.setParameter("cdCompositoVoceTitol", "%" + cdCompositoVoceTitol.toUpperCase() + "%");
            }
            if (criterioAttivo != null) {
                Calendar dataOdierna = Calendar.getInstance();
                dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
                dataOdierna.set(Calendar.MINUTE, 0);
                dataOdierna.set(Calendar.SECOND, 0);
                dataOdierna.set(Calendar.MILLISECOND, 0);
                query.setParameter("data", dataOdierna.getTime());
            }
            if (filterValid) {
                Date now = Calendar.getInstance().getTime();
                query.setParameter("filterDate", now);
            }

            result = query.getResultList();

        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di DecCriterioRaggrFasc", ex);
            throw ex;
        }

        return result;
    }

    public List<DecSelCriterioRaggrFasc> retrieveSelCriterioRaggrFascicoli(BigDecimal idCriterioRaggrFasc,
            String tiSel) {
        List<DecSelCriterioRaggrFasc> result = null;

        try {
            String whereWord = " and ";
            StringBuilder queryStr = new StringBuilder("SELECT scrf FROM DecSelCriterioRaggrFasc scrf "
                    + "WHERE scrf.decCriterioRaggrFasc.idCriterioRaggrFasc = :idcrit ");
            if (tiSel != null) {
                queryStr.append(whereWord).append("scrf.tiSel = :filtro ");
            }
            queryStr.append("ORDER BY scrf.tiSel");
            Query query = getEntityManager().createQuery(queryStr.toString());
            query.setParameter("idcrit", idCriterioRaggrFasc);
            if (tiSel != null) {
                query.setParameter("filtro", tiSel);
            }

            result = query.getResultList();
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di DecSelCriterioRaggrFasc con idCriterioRaggrFasc [{}], tiSel [{}]",
                    idCriterioRaggrFasc, tiSel, ex);
            throw ex;
        }

        return result;
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFascById(BigDecimal idCriterioRaggrFasc) {
        DecCriterioRaggrFasc result = null;

        try {
            result = getEntityManager().find(DecCriterioRaggrFasc.class, idCriterioRaggrFasc.longValue());
        } catch (RuntimeException ex) {
            logger.error("Errore nell'estrazione di DecCriterioRaggrFasc con idCriterioRaggrFasc [{}]",
                    idCriterioRaggrFasc, ex);
            throw ex;
        }

        return result;
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFasc(BigDecimal idStrutCorrente, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggrFasc u ");
        String whereWord = "WHERE ";

        if (idStrutCorrente != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrutCorrente ");
            whereWord = "AND ";
        }

        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrutCorrente != null) {
            query.setParameter("idStrutCorrente", idStrutCorrente);
        }

        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        List<DecCriterioRaggrFasc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    // /**
    // * Restituisce il valore di niMaxFascicoliCriterio per la struttura passata in input. Se la struttura non è
    // * presente o viene passato come parametro un id nullo, viene restituito 0
    // *
    // * @param idStrut
    // * @return
    // */
    // public long getNumMaxFascDaStrutConfigFasc(BigDecimal idStrut) {
    // long numFasc = 0;
    // if (idStrut != null) {
    // String queryStr = "SELECT strutCfgFasc.niMaxFascicoliCriterio FROM OrgStrutConfigFascicolo strutCfgFasc WHERE
    // strutCfgFasc.orgStrut.idStrut = :idStrut ";
    // Query q = getEntityManager().createQuery(queryStr);
    // q.setParameter("idStrut", idStrut.longValue());
    // List<BigDecimal> numMaxList = (List<BigDecimal>) q.getResultList();
    // if (!numMaxList.isEmpty()) {
    // numFasc = numMaxList.get(0).longValue();
    // }
    // }
    // return numFasc;
    // }

    // /**
    // * Restituisce il valore di niGgScadCriterio per la struttura passata in input. Se la struttura non è
    // * presente o viene passato come parametro un id nullo, viene restituito 0
    // *
    // * @param idStrut
    // * @return
    // */
    // public long getNumGgScadChiusDaStrutConfigFasc(BigDecimal idStrut) {
    // long numFasc = 0;
    // if (idStrut != null) {
    // String queryStr = "SELECT strutCfgFasc.niGgScadCriterio FROM OrgStrutConfigFascicolo strutCfgFasc WHERE
    // strutCfgFasc.orgStrut.idStrut = :idStrut ";
    // Query q = getEntityManager().createQuery(queryStr);
    // q.setParameter("idStrut", idStrut.longValue());
    // List<BigDecimal> numMaxList = (List<BigDecimal>) q.getResultList();
    // if (!numMaxList.isEmpty()) {
    // numFasc = numMaxList.get(0).longValue();
    // }
    // }
    // return numFasc;
    // }

    public boolean existNomeCriterio(String nome, BigDecimal idStruttura) {
        String queryStr = "SELECT crf FROM DecCriterioRaggrFasc crf WHERE crf.orgStrut.idStrut = :idstrut and crf.nmCriterioRaggr = :nomecrit";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", idStruttura);
        query.setParameter("nomecrit", nome);

        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Long saveCritRaggrFasc(LogParam param, CreaCriterioRaggrFascicoli filtri, Object[] anniFascicoliValidati,
            BigDecimal idStruttura, String nome, String criterioStandard, List<BigDecimal> voceTitolList)
            throws EMFError, ParerUserError {
        DecCriterioRaggrFasc record = new DecCriterioRaggrFasc();
        if (nome != null) {
            // Se c'è il parametro nome, carico il criterio di raggruppamento fascicoli corrispondente
            String queryStr = "SELECT u FROM DecCriterioRaggrFasc u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idstrut", idStruttura);
            query.setParameter("nomecrit", nome);
            record = (DecCriterioRaggrFasc) query.getSingleResult();
        }

        if (record.getDecSelCriterioRaggrFascicoli() == null) {
            record.setDecSelCriterioRaggrFascicoli(new ArrayList<DecSelCriterioRaggrFasc>());
        }

        StringBuilder queryStr = new StringBuilder("SELECT u FROM OrgStrut u WHERE u.idStrut = :idstrut");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", idStruttura);
        record.setOrgStrut((OrgStrut) query.getSingleResult());

        // Setto i filtri multipli a 0 come default
        record.setFlFiltroTipoFascicolo("0");
        record.setFlFiltroSistemaMigraz("0");
        record.setFlFiltroVoceTitol("0");

        // Per ogni tipo fascicolo creo un record filtro multiplo
        queryStr = new StringBuilder("SELECT u FROM DecTipoFascicolo u ");
        if (filtri.getNm_tipo_fascicolo().parse() != null && filtri.getNm_tipo_fascicolo().parse().size() > 0) {
            queryStr.append("WHERE u.idTipoFascicolo in :idtipofascicolo");
            query = getEntityManager().createQuery(queryStr.toString());
            List<BigDecimal> asList = filtri.getNm_tipo_fascicolo().parse();
            query.setParameter("idtipofascicolo", asList);
            List<DecTipoFascicolo> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroTipoFascicolo("1");
                for (DecTipoFascicolo tipo : lista) {
                    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri sono già presenti prima
                    // di salvarli
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecSelCriterioRaggrFasc u "
                                + "WHERE u.decTipoFascicolo = :tipo and u.tiSel = :filtro "
                                + "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
                        query.setParameter("tipo", tipo);
                        query.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
                        query.setParameter("crit", record.getIdCriterioRaggrFasc());
                        if (query.getResultList().isEmpty()) {
                            saveSelCriterioRaggrFascTipoFasc(record, tipo,
                                    ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
                        }
                    } else {
                        saveSelCriterioRaggrFascTipoFasc(record, tipo,
                                ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
                            + "WHERE u.tiSel = :filtro " + "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit "
                            + "and u.decTipoFascicolo.idTipoFascicolo NOT IN :tipi");
                    q.setParameter("tipi", asList);
                    q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
                    q.setParameter("crit", record.getIdCriterioRaggrFasc());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
                        + "WHERE u.tiSel = :filtro and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
                q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
                q.setParameter("crit", record.getIdCriterioRaggrFasc());
                q.executeUpdate();
                getEntityManager().flush();
            }
        }

        // Per ogni voce di titolario creo un record filtro multiplo
        queryStr = new StringBuilder("SELECT u FROM DecVoceTitol u ");
        if (voceTitolList != null && voceTitolList.size() > 0) {
            queryStr.append("WHERE u.idVoceTitol in :idvocetitol");
            query = getEntityManager().createQuery(queryStr.toString());
            query.setParameter("idvocetitol", voceTitolList);
            List<DecVoceTitol> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroVoceTitol("1");
                for (DecVoceTitol voce : lista) {
                    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri sono già presenti prima
                    // di salvarli
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecSelCriterioRaggrFasc u "
                                + "WHERE u.decVoceTitol = :voce and u.tiSel = :filtro "
                                + "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
                        query.setParameter("voce", voce);
                        query.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
                        query.setParameter("crit", record.getIdCriterioRaggrFasc());
                        if (query.getResultList().isEmpty()) {
                            saveSelCriterioRaggrFascVoceTitol(record, voce,
                                    ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
                        }
                    } else {
                        saveSelCriterioRaggrFascVoceTitol(record, voce,
                                ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
                            + "WHERE u.tiSel = :filtro " + "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit "
                            + "and u.decVoceTitol.idVoceTitol NOT IN :voci");
                    q.setParameter("voci", voceTitolList);
                    q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
                    q.setParameter("crit", record.getIdCriterioRaggrFasc());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
                        + "WHERE u.tiSel = :filtro and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
                q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
                q.setParameter("crit", record.getIdCriterioRaggrFasc());
                q.executeUpdate();
                getEntityManager().flush();
            }
        }

        // TODO: verificare
        /*
         * queryStr = new
         * StringBuilder("SELECT DISTINCT v.nmSistemaMigraz FROM OrgUsoSistemaMigraz u JOIN u.aplSistemaMigraz v " +
         * "WHERE u.orgStrut.idStrut = :idStrutturain " + "AND v.nmSistemaMigraz is not null "); // Per ogni sistema di
         * migrazione creo un record filtro multiplo if (filtri.getNm_sistema_migraz().parse() != null &&
         * filtri.getNm_sistema_migraz().parse().size() > 0) {
         * queryStr.append("AND v.nmSistemaMigraz in :nmsistemamigraz"); query =
         * getEntityManager().createQuery(queryStr.toString()); List<String> asList =
         * filtri.getNm_sistema_migraz().parse(); query.setParameter("nmsistemamigraz", asList);
         * query.setParameter("idStrutturain", idStruttura); List<String> lista = query.getResultList(); if
         * (!lista.isEmpty()) { record.setFlFiltroSistemaMigraz("1"); for (String tipo : lista) { // Se sto modificando
         * il criterio, verifico se ho già inserito precedentemente il filtro, // Altrimenti lo salvo direttamente if
         * (nome != null) { query = getEntityManager().createQuery("SELECT u FROM DecSelCriterioRaggrFasc u " +
         * "WHERE u.idSistemaMigraz = :tipo " + "and u.tiSel = :filtro " +
         * "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit"); query.setParameter("tipo", tipo);
         * query.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.SISTEMA_MIGRAZ.name());
         * query.setParameter("crit", record.getIdCriterioRaggrFasc()); if (query.getResultList().isEmpty()) {
         * saveCritRaggrFiltroMultiploSisMigr(record, tipo,
         * ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name()); } } else {
         * saveCritRaggrFiltroMultiploSisMigr(record, tipo,
         * ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name()); } } if (nome != null) { // In caso di
         * modifica, potrei aver eliminato qualche filtro dalle multiselect, che non risulterebbero più presenti nella
         * lista // Eseguo perciò una bulk delete sui record non presenti nella lista Query q =
         * getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u " +
         * "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit " +
         * "and u.nmSistemaMigraz NOT IN :tipi"); q.setParameter("tipi", asList); q.setParameter("filtro",
         * ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name()); q.setParameter("crit",
         * record.getIdCriterioRaggr()); q.executeUpdate(); getEntityManager().flush(); } } } else { // Se sono in
         * modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente // Eseguo perciò una bulk
         * delete per eliminare quei record if (nome != null) { Query q =
         * getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u " +
         * "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
         * q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
         * q.setParameter("crit", record.getIdCriterioRaggr()); q.executeUpdate(); } }
         */

        record.setNmCriterioRaggr(filtri.getNm_criterio_raggr().parse());
        record.setDsCriterioRaggr(filtri.getDs_criterio_raggr().parse());
        record.setNiMaxFasc(filtri.getNi_max_fasc().parse());
        record.setTiScadChius(filtri.getTi_scad_chius().getValue());
        record.setNiTempoScadChius(filtri.getNi_tempo_scad_chius().parse());
        record.setTiTempoScadChius(filtri.getTi_tempo_scad_chius().getValue());
        record.setDtIstituz(filtri.getDt_istituz().parse());
        record.setDtSoppres(filtri.getDt_soppres().parse());
        record.setAaFascicolo(filtri.getAa_fascicolo().parse());
        Object annoDa = (anniFascicoliValidati != null && anniFascicoliValidati.length > 1 ? anniFascicoliValidati[0]
                : null);
        Object annoA = (anniFascicoliValidati != null && anniFascicoliValidati.length > 1 ? anniFascicoliValidati[1]
                : null);
        record.setAaFascicoloDa((BigDecimal) annoDa);
        record.setAaFascicoloA((BigDecimal) annoA);
        record.setTiConservazione(filtri.getTi_conservazione().parse());
        record.setNtCriterioRaggr(filtri.getNt_criterio_raggr().parse());
        record.setFlCriterioRaggrStandard(criterioStandard);

        try {
            getEntityManager().persist(record);
            getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC,
                    new BigDecimal(record.getIdCriterioRaggrFasc()), param.getNomePagina());
        } catch (RuntimeException re) {
            /// logga l'errore e blocca tutto
            logger.error("Eccezione nella persistenza del  " + re);
            throw new EMFError(EMFError.BLOCKING, re);
        }

        return record.getIdCriterioRaggrFasc();
    }

    // TODO: verificare
    private void saveSelCriterioRaggrFascSisMigr(DecCriterioRaggrFasc crit, AplSistemaMigraz sistemaMigraz,
            String tiSel) {
        DecSelCriterioRaggrFasc filtro = new DecSelCriterioRaggrFasc();
        filtro.setDecCriterioRaggrFasc(crit);
        filtro.setAplSistemaMigraz(sistemaMigraz);
        filtro.setTiSel(tiSel);
        crit.getDecSelCriterioRaggrFascicoli().add(filtro);
    }

    private void saveSelCriterioRaggrFascTipoFasc(DecCriterioRaggrFasc crit, DecTipoFascicolo tipoFascicolo,
            String tiSel) {
        DecSelCriterioRaggrFasc filtro = new DecSelCriterioRaggrFasc();
        filtro.setDecCriterioRaggrFasc(crit);
        filtro.setDecTipoFascicolo(tipoFascicolo);
        filtro.setTiSel(tiSel);
        crit.getDecSelCriterioRaggrFascicoli().add(filtro);
    }

    private void saveSelCriterioRaggrFascVoceTitol(DecCriterioRaggrFasc crit, DecVoceTitol voceTitol, String tiSel) {
        DecSelCriterioRaggrFasc filtro = new DecSelCriterioRaggrFasc();
        filtro.setDecCriterioRaggrFasc(crit);
        filtro.setDecVoceTitol(voceTitol);
        filtro.setTiSel(tiSel);
        crit.getDecSelCriterioRaggrFascicoli().add(filtro);
    }

    public boolean deleteCritRaggrFasc(LogParam param, BigDecimal idStrut, String nmCriterioRaggr)
            throws ParerUserError {
        boolean result = false;

        String queryStr = "SELECT u FROM DecCriterioRaggrFasc u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("nomecrit", nmCriterioRaggr);

        // Ottengo la entity del record da eliminare
        DecCriterioRaggrFasc row = (DecCriterioRaggrFasc) query.getSingleResult();
        if (row != null && row.getElvElencoVersFasc().isEmpty()) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC,
                    new BigDecimal(row.getIdCriterioRaggrFasc()), param.getNomePagina());
            // Rimuovo il record
            getEntityManager().remove(row);
            getEntityManager().flush();
            logger.info("Cancellazione criterio di raggruppamento fascicoli " + nmCriterioRaggr + " della struttura "
                    + idStrut + " avvenuta con successo!");
            result = true;
        } else {
            throw new ParerUserError("Errore nell'eliminazione del criterio " + row.getNmCriterioRaggr()
                    + ", il criterio è collegato a degli elenchi di versamento");
        }
        return result;
    }

    public boolean existElvElencoVersPerCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc) {
        String queryStr = "SELECT elencoVersFasc FROM ElvElencoVersFasc elencoVersFasc "
                + "WHERE EXISTS (SELECT criterioRaggrFasc FROM DecCriterioRaggrFasc criterioRaggrFasc WHERE criterioRaggrFasc.idCriterioRaggrFasc = :idCriterioRaggrFasc AND elencoVersFasc.decCriterioRaggrFasc = criterioRaggrFasc)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggrFasc", idCriterioRaggrFasc.longValue());
        return !query.getResultList().isEmpty();
    }

    /**
     * Recupera i tipi di fascicolo per le chiavi fascicolo
     *
     * @param idUtente
     *            id utente
     * @param idStruttura
     *            id struttura
     * 
     * @return lista entity di tipo DecTipoFascicolo
     */
    public List<DecTipoFascicolo> getTipiFascicoloAbilitati(long idUtente, BigDecimal idStruttura) {
        List<BigDecimal> idStrutList = new ArrayList<>();
        idStrutList.add(idStruttura);
        return getTipiFascicoloAbilitatiDaStrutturaList(idUtente, idStrutList);
    }

    public List<DecTipoFascicolo> getTipiFascicoloAbilitatiDaStrutturaList(long idUtente,
            List<BigDecimal> idStrutturaList) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM DecTipoFascicolo u , IamAbilTipoDato iatd WHERE iatd.idTipoDatoApplic = u.idTipoFascicolo ");
        queryStr.append(
                " AND iatd.nmClasseTipoDato = 'TIPO_FASCICOLO' AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
        if (!idStrutturaList.isEmpty()) {
            queryStr.append("AND u.orgStrut.idStrut IN :idStrutturaList ");
        }
        queryStr.append("ORDER BY u.nmTipoFascicolo");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (!idStrutturaList.isEmpty()) {
            query.setParameter("idStrutturaList", idStrutturaList);
        }
        List<DecTipoFascicolo> listaTipiFascicolo = query.getResultList();
        return listaTipiFascicolo;
    }

    public FasFascicoloTableBean getListaFasFascicoloElvViewBean(BigDecimal idElencoVersFasc,
            ElenchiVersFascicoliForm.FascicoliFiltri filtri, DecTipoFascicoloTableBean tmpTableBeanTipoFasc)
            throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT f FROM FasFascicolo f WHERE f.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ");

        // Inserimento nella query dei tipi fascicolo abilitati
        Set<BigDecimal> idTipoFascicoloSet = new HashSet<>();
        for (DecTipoFascicoloRowBean row : tmpTableBeanTipoFasc) {
            idTipoFascicoloSet.add(row.getIdTipoFascicolo());
        }
        if (idTipoFascicoloSet.isEmpty()) {
            idTipoFascicoloSet.add(new BigDecimal("0"));
        }
        queryStr.append(whereWord).append("f.decTipoFascicolo.idTipoFascicolo IN :idtipofascicoloin ");

        // Inserimento nella query del filtro CHIAVE FASCICOLO
        BigDecimal idTipoFascicolo = filtri.getId_tipo_fascicolo().parse();
        BigDecimal anno = filtri.getAa_fascicolo().parse();
        String codice = filtri.getCd_key_fascicolo().parse();
        BigDecimal anno_range_da = filtri.getAa_fascicolo_da().parse();
        BigDecimal anno_range_a = filtri.getAa_fascicolo_a().parse();
        String codice_range_da = filtri.getCd_key_fascicolo_da().parse();
        String codice_range_a = filtri.getCd_key_fascicolo_a().parse();

        if (idTipoFascicolo != null) {
            queryStr.append(whereWord).append("f.decTipoFascicolo.idTipoFascicolo = :idtipofascin ");
            whereWord = "AND ";
        }

        if (anno != null) {
            queryStr.append(whereWord).append("f.aaFascicolo = :annoin ");
            whereWord = "AND ";
        }

        if (codice != null) {
            queryStr.append(whereWord).append("f.cdKeyFascicolo = :codicein ");
            whereWord = "AND ";
        }

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(f.aaFascicolo BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = "AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', f.cdKeyFascicolo, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = "AND ";
        }

        Date data_val_versamento_da = null;
        Date data_val_versamento_a = null;
        Date data_val_ape_fascicolo_da = null;
        Date data_val_ape_fascicolo_a = null;
        Date data_val_chiu_fascicolo_da = null;
        Date data_val_chiu_fascicolo_a = null;

        // Inserimento nella query del filtro DATA VERSAMENTO FASCICOLO DA - A
        if (filtri.getTs_ini_ses_da().parse() != null) {
            data_val_versamento_da = new Date(filtri.getTs_ini_ses_da().parse().getTime());
            if (filtri.getTs_ini_ses_a().parse() != null) {
                data_val_versamento_a = new Date(filtri.getTs_ini_ses_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_versamento_a);
                calendar.add(Calendar.DATE, 1);
                data_val_versamento_a = calendar.getTime();
            } else {
                data_val_versamento_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_versamento_a);
                calendar.add(Calendar.DATE, 1);
                data_val_versamento_a = calendar.getTime();
            }
        }

        if ((data_val_versamento_da != null) && (data_val_versamento_a != null)) {
            queryStr.append(whereWord).append("(f.tsIniSes BETWEEN :tsIniSes_da AND :tsIniSes_a) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro DATA APERTURA FASCICOLO DA - A
        if (filtri.getDt_ape_fascicolo_da().parse() != null) {
            data_val_ape_fascicolo_da = new Date(filtri.getDt_ape_fascicolo_da().parse().getTime());
            if (filtri.getDt_ape_fascicolo_a().parse() != null) {
                data_val_ape_fascicolo_a = new Date(filtri.getDt_ape_fascicolo_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_ape_fascicolo_a);
                calendar.add(Calendar.DATE, 1);
                data_val_ape_fascicolo_a = calendar.getTime();
            } else {
                data_val_ape_fascicolo_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_ape_fascicolo_a);
                calendar.add(Calendar.DATE, 1);
                data_val_ape_fascicolo_a = calendar.getTime();
            }
        }

        if ((data_val_ape_fascicolo_da != null) && (data_val_ape_fascicolo_a != null)) {
            queryStr.append(whereWord).append("(f.dtApeFascicolo BETWEEN :dtApeFascicolo_da AND :dtApeFascicolo_a) ");
            whereWord = "AND ";
        }

        // Inserimento nella query del filtro DATA CHIUSURA FASCICOLO DA - A
        if (filtri.getDt_chiu_fascicolo_da().parse() != null) {
            data_val_chiu_fascicolo_da = new Date(filtri.getDt_chiu_fascicolo_da().parse().getTime());
            if (filtri.getDt_chiu_fascicolo_a().parse() != null) {
                data_val_chiu_fascicolo_a = new Date(filtri.getDt_chiu_fascicolo_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_chiu_fascicolo_a);
                calendar.add(Calendar.DATE, 1);
                data_val_chiu_fascicolo_a = calendar.getTime();
            } else {
                data_val_chiu_fascicolo_a = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(data_val_chiu_fascicolo_a);
                calendar.add(Calendar.DATE, 1);
                data_val_chiu_fascicolo_a = calendar.getTime();
            }
        }

        if ((data_val_chiu_fascicolo_da != null) && (data_val_chiu_fascicolo_a != null)) {
            queryStr.append(whereWord)
                    .append("(f.dtChiuFascicolo BETWEEN :dtChiuFascicolo_da AND :dtChiuFascicolo_a) ");
            whereWord = "AND ";
        }

        String cdCompositoVoceTitol = filtri.getCd_composito_voce_titol().parse();
        if (cdCompositoVoceTitol != null) {
            queryStr.append(whereWord).append("f.decVoceTitol.cdCompositoVoceTitol = :cdcompositovocetitolin ");
            whereWord = "AND ";
        }

        queryStr.append("ORDER BY f.dsOggettoFascicolo ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idElencoVersFasc", idElencoVersFasc);

        query.setParameter("idtipofascicoloin", idTipoFascicoloSet);

        if (idTipoFascicolo != null) {
            query.setParameter("idtipofascin", idTipoFascicolo);
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

        if (data_val_versamento_da != null && data_val_versamento_a != null) {
            query.setParameter("tsIniSes_da", data_val_versamento_da, TemporalType.DATE);
            query.setParameter("tsIniSes_a", data_val_versamento_a, TemporalType.DATE);
        }

        if (data_val_ape_fascicolo_da != null && data_val_ape_fascicolo_a != null) {
            query.setParameter("dtApeFascicolo_da", data_val_ape_fascicolo_da, TemporalType.DATE);
            query.setParameter("dtApeFascicolo_a", data_val_ape_fascicolo_a, TemporalType.DATE);
        }

        if (data_val_chiu_fascicolo_da != null && data_val_chiu_fascicolo_a != null) {
            query.setParameter("dtChiuFascicolo_da", data_val_chiu_fascicolo_da, TemporalType.DATE);
            query.setParameter("dtChiuFascicolo_a", data_val_chiu_fascicolo_a, TemporalType.DATE);
        }

        if (StringUtils.isNotBlank(filtri.getCd_composito_voce_titol().parse())) {
            // query.setParameter("cdcompositovocetitolin", "%" +
            // filtri.getCd_composito_voce_titol().parse().toUpperCase() + "%");
            query.setParameter("cdcompositovocetitolin", filtri.getCd_composito_voce_titol().parse().toUpperCase());
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<FasFascicolo> listaFascicoli = query.getResultList();

        FasFascicoloTableBean fascicoliTableBean = new FasFascicoloTableBean();
        try {
            if (listaFascicoli != null && !listaFascicoli.isEmpty()) {
                fascicoliTableBean = (FasFascicoloTableBean) Transform.entities2TableBean(listaFascicoli);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // setta il campo relativo alla checkbox select_fasc non ceccato
        for (int i = 0; i < fascicoliTableBean.size(); i++) {
            FasFascicoloRowBean row = fascicoliTableBean.getRow(i);
            row.setString("select_fasc", "0");
            DecTipoFascicolo tipoFascicolo = findById(DecTipoFascicolo.class, row.getIdTipoFascicolo());
            row.setString("nm_tipo_fascicolo", tipoFascicolo.getNmTipoFascicolo());
        }

        return fascicoliTableBean;
    }

    /**
     * Determina i fascicoli con stato nell’elenco = IN_ELENCO_IN_CODA_CREAZIONE_AIP o IN_ELENCO_CON_AIP_CREATO o
     * IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO o IN_ELENCO_COMPLETATO nel cui contenuto sono presenti le unità
     * documentarie corrispondenti agli item (della richiesta corrente) di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER
     *
     * @param idRichAnnulVers
     *            id della richiesta di annullamento
     * 
     * @return la lista dei fascicoli
     */
    public List<BigDecimal> retrieveFasVLisFascByRichann(long idRichAnnulVers) {
        Query query = getEntityManager().createQuery("SELECT fas.idFascicolo FROM FasVLisFascByRichannUd fas "
                + "WHERE fas.idRichAnnulVers = :idRichAnnulVers ");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<BigDecimal> fascicoli = query.getResultList();
        return fascicoli;
    }

    public FasFileMetaVerAipFasc getFasFileMetaVerAipFasc(long idFascicolo, String tiMeta) {
        Query query = getEntityManager()
                .createQuery("SELECT fileMetaVerAipFasc FROM FasFileMetaVerAipFasc fileMetaVerAipFasc "
                        + "JOIN fileMetaVerAipFasc.fasMetaVerAipFascicolo metaVerAipFascicolo "
                        + "JOIN metaVerAipFascicolo.fasVerAipFascicolo verAipFascicolo "
                        + "WHERE verAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
                        + "AND verAipFascicolo.pgVerAipFascicolo = (SELECT MAX(verAipFascicolo2.pgVerAipFascicolo) FROM FasVerAipFascicolo verAipFascicolo2 WHERE verAipFascicolo2.fasFascicolo.idFascicolo = :idFascicolo) "
                        + "AND metaVerAipFascicolo.tiMeta = :tiMeta ");
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiMeta", tiMeta);
        List<FasFileMetaVerAipFasc> meta = query.getResultList();
        if (!meta.isEmpty()) {
            return meta.get(0);
        }
        return null;
    }

    public Long getIdFascVersatoNoAnnul(BigDecimal idStrut, BigDecimal aaFascicolo, String cdKeyFascicolo) {
        String queryStr = "SELECT u.idFascicolo FROM FasFascicolo u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.aaFascicolo = :aaFascicolo " + "AND u.cdKeyFascicolo = :cdKeyFascicolo "
                + "AND u.dtAnnull = :dtAnnull ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("aaFascicolo", aaFascicolo);
        query.setParameter("cdKeyFascicolo", cdKeyFascicolo);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        query.setParameter("dtAnnull", cal.getTime());
        List<Long> listaUdVersate = (List<Long>) query.getResultList();
        if (listaUdVersate != null && !listaUdVersate.isEmpty()) {
            return listaUdVersate.get(0);
        } else {
            return null;
        }
    }

    /**
     * Controlla che il fascicolo identificato dalla struttura versante, anno e numero esista
     *
     *
     * @param idStrut
     *            id struttura
     * @param aaFascicolo
     *            anno fascicolo
     * @param cdKeyFascicolo
     *            numero fascicolo
     * 
     * @return true/false
     */
    public boolean existsFascicolo(BigDecimal idStrut, BigDecimal aaFascicolo, String cdKeyFascicolo) {
        String queryStr = "SELECT COUNT(u) FROM FasFascicolo u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.aaFascicolo = :aaFascicolo " + "AND u.cdKeyFascicolo = :cdKeyFascicolo ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("aaFascicolo", aaFascicolo);
        query.setParameter("cdKeyFascicolo", cdKeyFascicolo);
        Long numFasc = (Long) query.getSingleResult();
        return numFasc > 0;
    }

    public AroRichAnnulVers getAroRichAnnulVersFasc(long idFascicolo) {
        String queryStr = "SELECT rich FROM AroItemRichAnnulVers item "
                + "JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati "
                + "WHERE item.fasFascicolo.idFascicolo = :idFascicolo "
                + "AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) "
                + "AND stati.tiStatoRichAnnulVers = 'EVASA' ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        List<AroRichAnnulVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
