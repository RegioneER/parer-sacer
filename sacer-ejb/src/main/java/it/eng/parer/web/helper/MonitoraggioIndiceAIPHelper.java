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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.helper.GenericHelper;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class MonitoraggioIndiceAIPHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioIndiceAIPHelper.class);

    String query1 = "select ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers stato_elenchi, "
            + "stato_ele.ti_stato_elenco stato_elenco, " + "count (ele.id_elenco_vers) ni_elenchi_total, "
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
            + "								)) " + "							 "
            + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte " + "and strut.id_strut = :idStrut "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) " + " "
            + "group by ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers, stato_ele.ti_stato_elenco ";

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
            + "								)) " + "							 "
            + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers " + " " + " "
            + "group by amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut, strut.id_strut, ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers, amb.id_ambiente, ente.id_ente ";

    String query3 = "select  " + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
            + "ele.id_elenco_vers id_elenco_vers, " + "ele.nm_elenco, " + "ele.ds_elenco, " + "ele.fl_elenco_fisc, "
            + "ele.ni_unita_doc_vers_elenco + ele.ni_unita_doc_mod_elenco Ni_unita_doc_tot, "
            + "ele.ni_unita_doc_vers_elenco , " + "ele.ni_doc_agg_elenco, " + "ele.ni_upd_unita_doc, "
            + "stato_ele.ts_stato_elenco_vers, " + "amb.id_ambiente, " + "ente.id_ente, " + "strut.id_strut, "
            + "extract( DAY FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as gg_permanenza_stato, "
            + "extract( HOUR FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as hh_permanenza_stato, "
            + "extract( MINUTE FROM (current_timestamp - (stato_ele.ts_stato_elenco_vers))) as mm_permanenza_stato, "
            + "ele.ni_unita_doc_mod_elenco Ni_unita_doc_agg " + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "								 "
            + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte " + "and strut.id_strut = :idStrut "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers "
            + "and ele.fl_elenco_fisc = :flElencoFisc "
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
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' then :thenOne "
            + "end fl_verifica_firma_eseguita, " + " " + " " + "case "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ATTESA_SCHED','NON_SELEZ_SCHED','IN_ELENCO_APERTO','IN_ELENCO_DA_CHIUDERE','IN_ELENCO_CHIUSO','IN_ELENCO_VALIDATO', "
            + "'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS','IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS', 'IN_CODA_JMS_INDICE_AIP_DA_ELAB') "
            + "then 0 " + "when ud.ti_stato_ud_elenco_vers in ( 'IN_ELENCO_CON_INDICI_AIP_GENERATI',	 "
            + "'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA','IN_ELENCO_COMPLETATO') "
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_INDICE_AIP' then :thenTwo "
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
            + "								)) " + "								 "
            + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte " + "and strut.id_strut = :idStrut "
            + "and ud.cd_registro_key_unita_doc = :cdRegistroKeyUnitaDoc " + "and ud.aa_key_unita_doc = :aaKeyUnitaDoc "
            + "and ud.cd_key_unita_doc = :cdKeyUnitaDoc "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers "
            + "and ele.fl_elenco_fisc = :flElencoFisc " + "and ele.id_elenco_vers = :idElencoVers "
            + "and ud.ti_stato_ud_elenco_vers = :tiStatoUdElencoVers "
            + "order by gg_permanenza_stato, hh_permanenza_stato, mm_permanenza_stato desc";

    String query4UdAgg = "select  " + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
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
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' then :thenOne "
            + "end fl_verifica_firma_eseguita, " + " " + " " + "case "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ATTESA_SCHED','NON_SELEZ_SCHED','IN_ELENCO_APERTO','IN_ELENCO_DA_CHIUDERE','IN_ELENCO_CHIUSO','IN_ELENCO_VALIDATO', "
            + "'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS','IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS', 'IN_CODA_JMS_INDICE_AIP_DA_ELAB') "
            + "then 0 " + "when ud.ti_stato_ud_elenco_vers in ( 'IN_ELENCO_CON_INDICI_AIP_GENERATI',	 "
            + "'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA','IN_ELENCO_COMPLETATO') "
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_INDICE_AIP' then :thenTwo "
            + "end fl_indice_aip_creato " + " " + " " + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "join aro_unita_doc ud on (ud.id_strut = strut.id_strut)   "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "" + "	AND ud.id_unita_doc IN( "
            + "			select distinct id_unita_doc " + "                       from ((select doc.id_unita_doc "
            + "                             from ARO_DOC doc "
            + "	      where doc.id_elenco_vers = ele.id_elenco_vers ) " + "                            UNION "
            + "	   (select upd.id_unita_doc " + "               		from ARO_UPD_UNITA_DOC upd "
            + "                           where upd.id_elenco_vers = ele.id_elenco_vers )) tmp" + "	    ) "

            + "								 " + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte "
            + "and strut.id_strut = :idStrut " + "and ud.cd_registro_key_unita_doc = :cdRegistroKeyUnitaDoc "
            + "and ud.aa_key_unita_doc = :aaKeyUnitaDoc " + "and ud.cd_key_unita_doc = :cdKeyUnitaDoc "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers "
            + "and ele.fl_elenco_fisc = :flElencoFisc " + "and ele.id_elenco_vers = :idElencoVers "
            + "and ud.ti_stato_ud_elenco_vers = :tiStatoUdElencoVers "
            + "order by gg_permanenza_stato, hh_permanenza_stato, mm_permanenza_stato desc";

    String query4UdDocAgg = "select  "
            + "amb.nm_ambiente || ' - ' || ente.nm_ente || ' - ' || strut.nm_strut struttura , "
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
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' then :thenOne "
            + "end fl_verifica_firma_eseguita, " + " " + " " + "case "
            + "when ud.ti_stato_ud_elenco_vers in ('IN_ATTESA_SCHED','NON_SELEZ_SCHED','IN_ELENCO_APERTO','IN_ELENCO_DA_CHIUDERE','IN_ELENCO_CHIUSO','IN_ELENCO_VALIDATO', "
            + "'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS','IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS', 'IN_CODA_JMS_INDICE_AIP_DA_ELAB') "
            + "then 0 " + "when ud.ti_stato_ud_elenco_vers in ( 'IN_ELENCO_CON_INDICI_AIP_GENERATI',	 "
            + "'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA','IN_ELENCO_COMPLETATO') "
            + "then 1 " + "when ud.ti_stato_ud_elenco_vers  = 'IN_CODA_INDICE_AIP' then :thenTwo "
            + "end fl_indice_aip_creato, " + "doc.id_doc," + "doc.dl_doc " + " " + " "
            + "from DEC_TI_EVE_STATO_ELENCO_VERS ti_eve_stato_ele  "
            + "left join elv_stato_elenco_vers stato_ele on (ti_eve_stato_ele.id_ti_eve_stato_elenco_vers = stato_ele.id_ti_eve_stato_elenco_vers) "
            + "join elv_elenco_vers ele on (ele.id_elenco_vers = stato_ele.id_elenco_vers) "
            + "join org_strut strut on (strut.id_strut = ele.id_strut) "
            + "join org_ente ente on (ente.id_ente = strut.id_ente) "
            + "join org_ambiente amb on (amb.id_ambiente = ente.id_ambiente) "
            + "join aro_unita_doc ud on (ud.id_strut = strut.id_strut) "
            + "join aro_doc doc on (doc.id_unita_doc = ud.id_unita_doc) "
            + "where   (stato_ele.pg_stato_elenco_vers = (select max(stato_ele_max.pg_stato_elenco_vers) "
            + "								from elv_stato_elenco_vers stato_ele_max "
            + "								where stato_ele_max.id_elenco_vers = stato_ele.id_elenco_vers "
            + "								)) " + "" + "	AND doc.id_doc IN( " + "			select distinct id_doc "
            + "                       from ((select doc.id_doc " + "                             from ARO_DOC doc "
            + "	      where doc.id_elenco_vers = ele.id_elenco_vers ) ) tmp" + "	    ) "
            + "								 " + "and amb.id_ambiente = :idAmbiente " + "and ente.id_ente = :idEnte "
            + "and strut.id_strut = :idStrut " + "and ud.cd_registro_key_unita_doc = :cdRegistroKeyUnitaDoc "
            + "and ud.aa_key_unita_doc = :aaKeyUnitaDoc " + "and ud.cd_key_unita_doc = :cdKeyUnitaDoc "
            + "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) "
            + "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers "
            + "and ele.fl_elenco_fisc = :flElencoFisc " + "and ele.id_elenco_vers = :idElencoVers "
            + "and ud.ti_stato_ud_elenco_vers = :tiStatoUdElencoVers "
            + "order by gg_permanenza_stato, hh_permanenza_stato, mm_permanenza_stato desc";

    String queryUd = "(select count (firma.id_comp_doc) " + " " + "from aro_unita_doc ud1 " + "join aro_doc doc "
            + "	on (doc.id_unita_doc = ud1.id_unita_doc " + "	and doc.ti_creazione = 'VERSAMENTO_UNITA_DOC') "
            + "join aro_strut_doc strut_doc " + "	on (strut_doc.id_doc = doc.id_doc) " + "join aro_comp_doc comp "
            + "	on (comp.id_strut_doc = strut_doc.id_strut_doc) " + "join aro_firma_comp firma "
            + "	on (firma.id_comp_doc = comp.id_comp_doc) " + "	 " + "where ud1.id_unita_doc = :idUnitaDoc "
            + "and comp.fl_comp_firmato = '1' " + "and exists (select * "
            + "			from aro_verif_firma_dt_vers verif "
            + "			where verif.id_firma_comp = firma.id_firma_comp " + "			))";

    String queryUd2 = "(select count(*) " + "from ARO_INDICE_AIP_UD " + "where id_unita_doc = :idUnitaDoc )";

    public List<Object[]> getRiepilogo(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal nnGgStato) {
        List<Object[]> risultatiList = getRisultatiList(query1, idAmbiente, idEnte, idStrut, nnGgStato);
        return risultatiList;
    }

    public List<Object[]> getRisultatiList(String viewName, BigDecimal param1, BigDecimal param2, BigDecimal param3,
            BigDecimal param4) {
        String queryStr = viewName;
        if (param1 == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = :idAmbiente ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = :idEnte ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = :idStrut ", " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) ", " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter("idAmbiente", param1);
        }
        if (param2 != null) {
            query.setParameter("idEnte", param2);
        }
        if (param3 != null) {
            query.setParameter("idStrut", param3);
        }
        if (param4 != null) {
            query.setParameter("dataSottratta", param4);
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
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = :idAmbiente ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = :idEnte ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) ", " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers ", " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter("idAmbiente", param1);
        }
        if (param2 != null) {
            query.setParameter("idEnte", param2);
        }
        if (param3 != null) {
            query.setParameter("idStrut", param3);
        }
        if (param4 != null) {
            query.setParameter("cdTiEveStatoElencoVers", param4);
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
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = :idAmbiente ", " ");
        }
        if (param2 == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = :idEnte ", " ");
        }
        if (param3 == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = :idStrut ", " ");
        }
        if (param4 == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) ", " ");
        }
        if (param5 == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers ", " ");
        }
        if (param6 == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.fl_elenco_fisc = :flElencoFisc ", " ");
        }
        Query query = getEntityManager().createNativeQuery(queryStr);
        if (param1 != null) {
            query.setParameter("idAmbiente", param1);
        }
        if (param2 != null) {
            query.setParameter("idEnte", param2);
        }
        if (param3 != null) {
            query.setParameter("idStrut", param3);
        }
        if (param4 != null) {
            query.setParameter("dataSottratta", param4);
        }
        if (param5 != null) {
            query.setParameter("cdTiEveStatoElencoVers", param5);
        }
        if (param6 != null) {
            query.setParameter("flElencoFisc", param6);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<List<Object[]>> getRiepilogoUd(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal nnGgStato,
            String cdTiEveStatoElencoVers, String fiscali, BigDecimal idElencoVers, String tiStatoUdElencoVers) {
        List<Object[]> risultatiList = getRisultati4List(query4, idAmbiente, idEnte, idStrut, cdRegistroKeyUnitaDoc,
                aaKeyUnitaDoc, cdKeyUnitaDoc, tiStatoUdElencoVers, nnGgStato, cdTiEveStatoElencoVers, fiscali,
                idElencoVers, queryUd, queryUd2);

        List<Object[]> risultati1List = getRisultati4List(query4UdAgg, idAmbiente, idEnte, idStrut,
                cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, tiStatoUdElencoVers, nnGgStato,
                cdTiEveStatoElencoVers, fiscali, idElencoVers, queryUd, queryUd2);

        List<Object[]> risultati2List = getRisultati4List(query4UdDocAgg, idAmbiente, idEnte, idStrut,
                cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, tiStatoUdElencoVers, nnGgStato,
                cdTiEveStatoElencoVers, fiscali, idElencoVers, queryUd, queryUd2);

        List<List<Object[]>> output = new ArrayList<>();

        output.add(risultatiList);
        output.add(risultati1List);
        output.add(risultati2List);

        return output;
    }

    public List<Object[]> getRisultati4List(String query4, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String tiStatoUdElencoVers,
            BigDecimal nnGgStato, String cdTiEveStatoElencoVers, String fiscali, BigDecimal idElencoVers,
            String queryUd, String queryUd2) {
        String queryStr = query4;

        queryStr = StringUtils.replace(queryStr, ":thenOne ", queryUd);
        queryStr = StringUtils.replace(queryStr, ":idUnitaDoc ", "ud.id_unita_doc ");
        queryStr = StringUtils.replace(queryStr, ":thenTwo ", queryUd2);
        queryStr = StringUtils.replace(queryStr, ":idUnitaDoc ", "ud.id_unita_doc ");
        if (idAmbiente == null) {
            queryStr = StringUtils.replace(queryStr, "and amb.id_ambiente = :idAmbiente ", " ");
        }
        if (idEnte == null) {
            queryStr = StringUtils.replace(queryStr, "and ente.id_ente = :idEnte ", " ");
        }
        if (idStrut == null) {
            queryStr = StringUtils.replace(queryStr, "and strut.id_strut = :idStrut ", " ");
        }
        if (cdRegistroKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.cd_registro_key_unita_doc = :cdRegistroKeyUnitaDoc ", " ");
        }
        if (aaKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.aa_key_unita_doc = :aaKeyUnitaDoc ", " ");
        }
        if (cdKeyUnitaDoc == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.cd_key_unita_doc = :cdKeyUnitaDoc ", " ");
        }
        if (nnGgStato == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and stato_ele.ts_stato_elenco_vers >= to_date (sysdate - :dataSottratta) ", " ");
        }
        if (cdTiEveStatoElencoVers == null) {
            queryStr = StringUtils.replace(queryStr,
                    "and ti_eve_stato_ele.cd_ti_eve_stato_elenco_vers = :cdTiEveStatoElencoVers ", " ");
        }
        if (fiscali == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.fl_elenco_fisc = :flElencoFisc ", " ");
        }
        if (idElencoVers == null) {
            queryStr = StringUtils.replace(queryStr, "and ele.id_elenco_vers = :idElencoVers ", " ");
        }
        if (tiStatoUdElencoVers == null) {
            queryStr = StringUtils.replace(queryStr, "and ud.ti_stato_ud_elenco_vers = :tiStatoUdElencoVers ", " ");
        }

        Query query = getEntityManager().createNativeQuery(queryStr);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }
        if (nnGgStato != null) {
            query.setParameter("nnGgStato", nnGgStato);
        }
        if (cdTiEveStatoElencoVers != null) {
            query.setParameter("cdTiEveStatoElencoVers", cdTiEveStatoElencoVers);
        }
        if (fiscali != null) {
            query.setParameter("flElencoFisc", fiscali);
        }
        if (idElencoVers != null) {
            query.setParameter("idElencoVers", idElencoVers);
        }
        if (tiStatoUdElencoVers != null) {
            query.setParameter("tiStatoUdElencoVers", tiStatoUdElencoVers);
        }

        return (List<Object[]>) query.getResultList();
    }

    public List<Object[]> contaNumeroMessaggiInCoda() {
        Query q = getEntityManager().createQuery("SELECT COUNT(u), MIN(u.tsInCoda), MAX(u.tsInCoda) "
                + "FROM AroIndiceAipUdDaElab u " + "WHERE u.flInCoda = '1' ");
        return q.getResultList();
    }

}
