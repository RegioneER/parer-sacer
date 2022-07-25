package it.eng.parer.web.helper;

import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.helper.GenericHelper;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//IndiceA
/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class MonitoraggioIndiceAIPHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioIndiceAIPHelper.class);

    String query1 = "select ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers stato_elenchi, "
            + "count (ele.id_elenco_vers) ni_elenchi_total, "
            + "count (case when ele.fl_elenco_fisc = 1 then ele.id_elenco_vers else null end) as ni_elenchi_fisc , "
            + "count (case when ele.fl_elenco_fisc = 0 then ele.id_elenco_vers else null end) as ni_elenchi_no_fisc  "
            + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "							 " + "and amb.id_ambiente = ?1 "
            + "and ente.id_ente = ?2 " + "and strut.id_strut = ?3 "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?4) " + " "
            + "group by ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers ";

    String query2 = "select  " + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
            + "strut.id_strut idstruttura, " + "ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers stato_elenchi,  "
            + "amb.id_ambiente, ente.id_ente, " + "count (ele.id_elenco_vers) ni_elenchi_total, "
            + "count (case when ele.fl_elenco_fisc = 1 then ele.id_elenco_vers else null end) as ni_elenchi_fisc , "
            + "count (case when ele.fl_elenco_fisc = 0 then ele.id_elenco_vers else null end) as ni_elenchi_no_fisc "
            + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "							 " + "and amb.id_ambiente = ?1 "
            + "and ente.id_ente = ?2 " + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?3) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?4 " + " " + " "
            + "group by amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut, strut.id_strut, ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers, amb.id_ambiente, ente.id_ente ";

    String query3 = "select  " + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
            + "ele.id_elenco_vers id_elenco_vers, " + "ele.nm_elenco, " + "ele.ds_elenco, " + "ele.fl_elenco_fisc, "
            + "ele.ni_unita_doc_vers_elenco + ele.ni_unita_doc_mod_elenco Ni_unita_doc_tot, "
            + "ele.ni_unita_doc_vers_elenco , " + "ele.ni_doc_agg_elenco, " + "ele.ni_upd_unita_doc, "
            + "stato_ele.ts_stato_elenco_vers, " + "amb.id_ambiente, " + "ente.id_ente, " + "strut.id_strut, "
            + "extract( DAY FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as gg_permanenza_stato, "
            + "extract( HOUR FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as hh_permanenza_stato, "
            + "extract( MINUTE FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as mm_permanenza_stato "
            + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "								 " + "and amb.id_ambiente = ?1 "
            + "and ente.id_ente = ?2 " + "and strut.id_strut = ?3 "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?4) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?5 " + "and ele.fl_elenco_fisc = ?6 "
            + "order by gg_permanenza_stato, hh_permanenza_stato, mm_permanenza_stato desc";

    String query4 = "select  " + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
            + "ele.id_elenco_vers id_elenco_vers, " + "ele.nm_elenco  nm_elenco, " + "ele.ds_elenco ds_elenco, "
            + "ele.fl_elenco_fisc fl_elenco_fisc, " + "ud.id_unita_doc id_unita_doc, "
            + "ud.cd_registro_key_unita_doc cd_registro_key_unita_doc, " + "ud.aa_key_unita_doc aa_key_unita_doc, "
            + "ud.cd_key_unita_doc cd_key_unita_doc, " + "ud.ti_stato_ud_elenco_vers stato_ud, "
            + "ud.ts_last_reset_stato timestamp_stato, "
            + "extract( DAY FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as gg_permanenza_stato, "
            + "extract( HOUR FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as hh_permanenza_stato, "
            + "extract( MINUTE FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as mm_permanenza_stato, "
            + "case "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ATTESA_SCHED','NON_SELEZ_SCHED','IN_ELENCO_APERTO','IN_ELENCO_DA_CHIUDERE','IN_ELENCO_CHIUSO','IN_ELENCO_VALIDATO') "
            + "then 0 "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS','IN_CODA_JMS_INDICE_AIP_DA_ELAB','IN_ELENCO_IN_CODA_INDICE_AIP', 'IN_ELENCO_CON_INDICI_AIP_GENERATI',	 "
            + "'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA','IN_ELENCO_COMPLETATO') "
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' then ?1 "
            + "end fl_verifica_firma_eseguita, " + " " + " " + "case "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ATTESA_SCHED','NON_SELEZ_SCHED','IN_ELENCO_APERTO','IN_ELENCO_DA_CHIUDERE','IN_ELENCO_CHIUSO','IN_ELENCO_VALIDATO', "
            + "'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS','IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS', 'IN_CODA_JMS_INDICE_AIP_DA_ELAB') "
            + "then 0 " + "when ud.ti_stato_ud_elenco_vers in ( 'IN_ELENCO_CON_INDICI_AIP_GENERATI',	 "
            + "'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA','IN_ELENCO_COMPLETATO') "
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_INDICE_AIP' then ?2 "
            + "end fl_indice_aip_creato " + " " + " " + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "join aro_unita_doc ud on (ud.id_elenco_vers = ele.id_elenco_vers) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "								 " + "and amb.id_ambiente = ?3 "
            + "and ente.id_ente = ?4 " + "and strut.id_strut = ?5 " + "and ud.cd_registro_key_unita_doc = ?6 "
            + "and ud.aa_key_unita_doc = ?7 " + "and ud.cd_key_unita_doc = ?8 "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?9) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?10 " + "and ele.fl_elenco_fisc = ?11 "
            + "and ele.id_elenco_vers = ?12 " + "and ud.ti_stato_ud_elenco_vers = ?13 "
            + "order by gg_permanenza_stato, hh_permanenza_stato, mm_permanenza_stato desc";

    String queryUd = "(select count (firma.id_comp_doc) " + " " + "from aro_unita_doc ud1 " + "join aro_doc doc "
            + "	on (doc.id_unita_doc = ud1.id_unita_doc " + "	and doc.ti_creazione = 'VERSAMENTO_UNITA_DOC') "
            + "join aro_strut_doc strut_doc " + "	on (strut_doc.id_doc = doc.id_doc) " + "join aro_comp_doc comp "
            + "	on (comp.id_strut_doc = strut_doc.id_strut_doc) " + "join aro_firma_comp firma "
            + "	on (firma.id_comp_doc = comp.id_comp_doc) " + "	 " + "where ud1.id_unita_doc = ?14 "
            + "and comp.fl_comp_firmato = '1' " + "and exists (select * "
            + "			from aro_verif_firma_dt_vers verif "
            + "			where verif.id_firma_comp = firma.id_firma_comp " + "			))";

    String queryUd2 = "(select count(*) " + "from ARO_INDICE_AIP_UD " + "where id_unita_doc = ?14 )";

    public List<Object[]> getRiepilogo(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal nnGgStato) {
        List<Object[]> risultatiList = getRisultatiList(query1, idAmbiente, idEnte, idStrut, nnGgStato);
        return risultatiList;
    }

    public List<Object[]> getRisultatiList(String viewName, BigDecimal param1, BigDecimal param2, BigDecimal param3,
            BigDecimal param4) {
        String queryStr = viewName;
        if (param1 == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = ?1 ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = ?2 ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = ?3 ", " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr, "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?4) ",
                    " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter(1, param1);
        }
        if (param2 != null) {
            query.setParameter(2, param2);
        }
        if (param3 != null) {
            query.setParameter(3, param3);
        }
        if (param4 != null) {
            query.setParameter(4, param4);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<Object[]> getRiepilogoStrutture(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal nnGgStato,
            String cdTiEveStatoElencoVers) {
        List<Object[]> risultatiList = getRisultati2List(query2, idAmbiente, idEnte, nnGgStato, cdTiEveStatoElencoVers);
        return risultatiList;
    }

    public List<Object[]> getRisultati2List(String viewName, BigDecimal param1, BigDecimal param2, BigDecimal param3,
            String param4) {
        String queryStr = viewName;
        if (param1 == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = ?1 ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = ?2 ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr, "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?3) ",
                    " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr, "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?4 ", " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter(1, param1);
        }
        if (param2 != null) {
            query.setParameter(2, param2);
        }
        if (param3 != null) {
            query.setParameter(3, param3);
        }
        if (param4 != null) {
            query.setParameter(4, param4);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<String> getStatiElencoNoCompletato() {
        Query q = getEntityManager().createQuery("SELECT u.cdTiEveStatoElencoVers FROM DecTiEveStatoElencoVers u "
                + "WHERE u.cdTiEveStatoElencoVers != 'COMPLETATO' ORDER BY u.cdTiEveStatoElencoVers ");
        return (List<String>) q.getResultList();
    }

    public List<Object[]> getRiepilogoElenchi(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal nnGgStato, String cdTiEveStatoElencoVers, String fiscali) {
        List<Object[]> risultatiList = getRisultati3List(query3, idAmbiente, idEnte, idStrut, nnGgStato,
                cdTiEveStatoElencoVers, fiscali);
        return risultatiList;
    }

    public List<Object[]> getRisultati3List(String viewName, BigDecimal param1, BigDecimal param2, BigDecimal param3,
            BigDecimal param4, String param5, String param6) {
        String queryStr = viewName;
        if (param1 == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = ?1 ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = ?2 ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = ?3 ", " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr, "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?4) ",
                    " ");
        }
        if (param5 == null) {
            queryStr = StringUtils.replace(queryStr, "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?5 ", " ");
        }
        if (param6 == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.fl_elenco_fisc = ?6 ", " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter(1, param1);
        }
        if (param2 != null) {
            query.setParameter(2, param2);
        }
        if (param3 != null) {
            query.setParameter(3, param3);
        }
        if (param4 != null) {
            query.setParameter(4, param4);
        }
        if (param5 != null) {
            query.setParameter(5, param5);
        }
        if (param6 != null) {
            query.setParameter(6, param6);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<Object[]> getRiepilogoUd(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal nnGgStato,
            String cdTiEveStatoElencoVers, String fiscali, BigDecimal idElencoVers, String tiStatoUdElencoVers) {
        List<Object[]> risultatiList = getRisultati4List(query4, idAmbiente, idEnte, idStrut, cdRegistroKeyUnitaDoc,
                aaKeyUnitaDoc, cdKeyUnitaDoc, tiStatoUdElencoVers, nnGgStato, cdTiEveStatoElencoVers, fiscali,
                idElencoVers, queryUd, queryUd2);
        return risultatiList;
    }

    public List<Object[]> getRisultati4List(String query4, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String tiStatoUdElencoVers,
            BigDecimal nnGgStato, String cdTiEveStatoElencoVers, String fiscali, BigDecimal idElencoVers,
            String queryUd, String queryUd2) {
        String queryStr = query4;

        // if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.equals("IN_CODA_JMS_VERIFICA_FIRME_DT_VERS")) {
        queryStr = StringUtils.replace(queryStr, "?1 ", queryUd);
        queryStr = StringUtils.replace(queryStr, "?14 ", "ud.id_unita_doc ");
        // } else {
        // queryStr = StringUtils.replace(queryStr,
        // "when ud.ti_stato_ud_elenco_vers = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' then ?1 ", " ");
        //
        // }
        // if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.equals("IN_CODA_INDICE_AIP")) {
        queryStr = StringUtils.replace(queryStr, "?2 ", queryUd2);
        queryStr = StringUtils.replace(queryStr, "?14 ", "ud.id_unita_doc ");
        // } else {
        // queryStr = StringUtils.replace(queryStr, "when ud.ti_stato_ud_elenco_vers = 'IN_CODA_INDICE_AIP' then ?2 ",
        // " ");
        // }
        if (idAmbiente == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = ?3 ", " ");
        }
        if (idEnte == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = ?4 ", " ");
        }
        if (idStrut == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = ?5 ", " ");
        }
        if (cdRegistroKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.cd_registro_key_unita_doc = ?6 ", " ");
        }
        if (aaKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.aa_key_unita_doc = ?7 ", " ");
        }
        if (cdKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.cd_key_unita_doc = ?8 ", " ");
        }
        if (nnGgStato == null) {
            queryStr = StringUtils.replace(queryStr, "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - ?9) ",
                    " ");
        }
        if (cdTiEveStatoElencoVers == null) {
            queryStr = StringUtils.replace(queryStr, "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = ?10 ", " ");
        }
        if (fiscali == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.fl_elenco_fisc = ?11 ", " ");
        }
        if (idElencoVers == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.id_elenco_vers = ?12 ", " ");
        }
        if (tiStatoUdElencoVers == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.ti_stato_ud_elenco_vers = ?13 ", " ");
        }

        Query query = getEntityManager().createNativeQuery(queryStr);
        if (idAmbiente != null) {
            query.setParameter(3, idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter(4, idEnte);
        }
        if (idStrut != null) {
            query.setParameter(5, idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            query.setParameter(6, cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter(7, aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            query.setParameter(8, cdKeyUnitaDoc);
        }
        if (nnGgStato != null) {
            query.setParameter(9, nnGgStato);
        }
        if (cdTiEveStatoElencoVers != null) {
            query.setParameter(10, cdTiEveStatoElencoVers);
        }
        if (fiscali != null) {
            query.setParameter(11, fiscali);
        }
        if (idElencoVers != null) {
            query.setParameter(12, idElencoVers);
        }
        if (tiStatoUdElencoVers != null) {
            query.setParameter(13, tiStatoUdElencoVers);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<Object[]> contaNumeroMessaggiInCoda() {
        Query q = getEntityManager().createQuery("SELECT COUNT(u), MIN(u.tsInCoda), MAX(u.tsInCoda) "
                + "FROM AroIndiceAipUdDaElab u " + "WHERE u.flInCoda = '1' ");
        return q.getResultList();
    }

}
