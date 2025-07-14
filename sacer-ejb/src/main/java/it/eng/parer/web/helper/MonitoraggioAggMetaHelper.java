/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroWarnUpdUnitaDoc;
import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.MonContaSesUpdUdKo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsErrSesUpdUnitaDocErr;
import it.eng.parer.entity.VrsErrSesUpdUnitaDocKo;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerRuntimeException;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVLisUpdCompUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdDocUnitaDoc;
import it.eng.parer.viewEntity.AroVLisUpdKoRisolti;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.viewEntity.MonVLisUdUpdKo;
import it.eng.parer.viewEntity.MonVLisUdUpdKoByErr;
import it.eng.parer.viewEntity.MonVLisUdUpdKoInterface;
import it.eng.parer.viewEntity.MonVLisUpdUd;
import it.eng.parer.viewEntity.MonVLisUpdUdDaElab;
import it.eng.parer.viewEntity.MonVLisUpdUdErr;
import it.eng.parer.viewEntity.MonVLisUpdUdInterface;
import it.eng.parer.viewEntity.MonVLisUpdUdKo;
import it.eng.parer.viewEntity.MonVLisUpdUdKoByErr;
import it.eng.parer.viewEntity.MonVLisUpdUdKoInterface;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({
	"unchecked", "rawtypes" })
@Stateless
@LocalBean
public class MonitoraggioAggMetaHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioAggMetaHelper.class);

    String MON_CHK_UPD_UD_COR_BY_AMB = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ambiente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	), " + "	tmp_upd_ud_da_elab_elenco as "
	    + "	(select upd_da_elab.ti_stato_upd_elenco_vers  "
	    + "	 from tmp_abil_key_ud abil_key_ud "
	    + "	 join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "		on ( upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "		and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "		and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "		and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "		and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "		and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " amb.id_ambiente, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_abil_key_ud abil_key_ud "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'IN_ATTESA_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_attesa_sched_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'NON_SELEZ_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_nosel_sched_cor "
	    + "from ORG_AMBIENTE amb " + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) "
	    + "where amb.id_ambiente = :due " + "and usr.id_user_iam = :uno ";

    String MON_CHK_UPD_UD_COR_BY_ENTE = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	), " + "	tmp_upd_ud_da_elab_elenco as "
	    + "	(select upd_da_elab.ti_stato_upd_elenco_vers  "
	    + "	 from tmp_abil_key_ud abil_key_ud "
	    + "	 join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "		on ( upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "		and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "		and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "		and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "		and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "		and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " ente.id_ente, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_abil_key_ud abil_key_ud "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'IN_ATTESA_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_attesa_sched_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'NON_SELEZ_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_nosel_sched_cor " + "from ORG_ENTE ente "
	    + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + "where ente.id_ente = :due "
	    + "and usr.id_user_iam = :uno ";

    String MON_CHK_UPD_UD_COR_BY_STRUT = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto	"
	    + "	 and id_tipo_unita_doc = :quattro	"
	    + "	 and id_registro_unita_doc = :cinque	" + "	 and id_tipo_doc_princ = :sei	"
	    + "	), " + "	tmp_upd_ud_da_elab_elenco as "
	    + "	(select upd_da_elab.ti_stato_upd_elenco_vers  "
	    + "	 from tmp_abil_key_ud abil_key_ud "
	    + "	 join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "		on ( upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "		and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "		and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "		and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "		and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "		and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " strut.id_strut, " + "case "
	    + "	when exists (select * from tmp_abil_key_ud " + "				) "
	    + "			then '1' " + "			else '0' " + "end fl_upd_cor, "
	    + "case " + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'IN_ATTESA_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_attesa_sched_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_upd_ud_da_elab_elenco upd_da_elab "
	    + "				where upd_da_elab.ti_stato_upd_elenco_vers = 'NON_SELEZ_SCHED' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_nosel_sched_cor " + "from ORG_STRUT strut "
	    + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + "where strut.id_strut = :due "
	    + "and usr.id_user_iam = :uno ";

    String MON_CNT_UPD_UD_NOCOR_BY_AMB = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ambiente, " + "	  id_strut, "
	    + "      aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ambiente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	) " + "select "
	    + " abil_key_ud.id_ambiente, " + " conta_upd_ud.ti_stato_udp_ud, " + " case "
	    + "	when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud.ni_ses_upd_ud) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join MON_CONTA_SES_UPD_UD conta_upd_ud "
	    + "	on (conta_upd_ud.id_key_total_ud = abil_key_ud.id_key_total_ud) "
	    + "group by abil_key_ud.id_ambiente,  " + "		 conta_upd_ud.ti_stato_udp_ud, "
	    + "		 case "
	    + "			when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CNT_UPD_UD_NOCOR_BY_ENTE = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ente, " + "	  id_strut, "
	    + "      aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	) " + "select " + " abil_key_ud.id_ente, "
	    + " conta_upd_ud.ti_stato_udp_ud, " + " case "
	    + "	when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud.ni_ses_upd_ud) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join MON_CONTA_SES_UPD_UD conta_upd_ud "
	    + "	on (conta_upd_ud.id_key_total_ud = abil_key_ud.id_key_total_ud) "
	    + "group by abil_key_ud.id_ente,  " + "		 conta_upd_ud.ti_stato_udp_ud, "
	    + "		 case "
	    + "			when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CNT_UPD_UD_NOCOR_BY_STRUT = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_strut, " + "      aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ " + "	 from MON_V_ABIL_KEY_TOTAL_UD "
	    + "	 where id_strut = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "	) " + "select " + " abil_key_ud.id_strut, "
	    + " abil_key_ud.aa_key_unita_doc, " + " abil_key_ud.id_tipo_unita_doc, "
	    + " abil_key_ud.id_registro_unita_doc, " + " abil_key_ud.id_tipo_doc_princ, "
	    + " conta_upd_ud.ti_stato_udp_ud, " + " case "
	    + "	when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud.ni_ses_upd_ud) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join MON_CONTA_SES_UPD_UD conta_upd_ud "
	    + "	on (conta_upd_ud.id_key_total_ud = abil_key_ud.id_key_total_ud) "
	    + "group by abil_key_ud.id_strut,  " + "		 abil_key_ud.aa_key_unita_doc, "
	    + "		 abil_key_ud.id_tipo_unita_doc, "
	    + "		 abil_key_ud.id_registro_unita_doc, "
	    + "		 abil_key_ud.id_tipo_doc_princ,	"
	    + "		 conta_upd_ud.ti_stato_udp_ud, " + "		 case "
	    + "			when conta_upd_ud.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CHK_UPD_UD_KO_COR_BY_AMB = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, "
	    + "	  dt_last_upd_ud_ko " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ambiente = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and dt_last_upd_ud_ko = trunc(sysdate) " + "	), "
	    + "	tmp_ses_upd_unita_ko as  " + "	(select ses_ko.ti_stato_ses_upd_ko "
	    + "	 from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "	 join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "		on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "		and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "		and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "		and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "		and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "		and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " amb.id_ambiente, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_norisolub_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_verif_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_noverif_cor " + "from ORG_AMBIENTE amb "
	    + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + "where amb.id_ambiente = :due "
	    + "and usr.id_user_iam = :uno ";

    String MON_CHK_UPD_UD_KO_COR_BY_ENTE = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, "
	    + "	  dt_last_upd_ud_ko " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ente = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and dt_last_upd_ud_ko = trunc(sysdate) " + "	), "
	    + "	tmp_ses_upd_unita_ko as  " + "	(select ses_ko.ti_stato_ses_upd_ko "
	    + "	 from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "	 join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "		on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "		and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "		and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "		and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "		and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "		and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " ente.id_ente, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_norisolub_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_verif_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_noverif_cor " + "from ORG_ENTE ente "
	    + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + "where ente.id_ente = :due "
	    + "and usr.id_user_iam = :uno ";

    String MON_CHK_UPD_UD_KO_COR_BY_STRUT = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud_ko "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud_ko = trunc(sysdate) "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "	 ), " + "	tmp_ses_upd_unita_ko as  "
	    + "	(select ses_ko.ti_stato_ses_upd_ko "
	    + "	 from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "	 join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "		on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "		and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "		and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "		and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "		and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "		and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) " + "	) "
	    + "select " + " usr.id_user_iam id_user_iam_cor, " + " strut.id_strut, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_norisolub_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_verif_cor, " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_upd_ko_noverif_cor " + "from ORG_STRUT strut "
	    + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + "where strut.id_strut = :due "
	    + "and usr.id_user_iam = :uno ";

    String MON_CNT_UPD_UD_KO_NOCOR_BY_AMB = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_ambiente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	) " + "select "
	    + " abil_key_ud_ko.id_ambiente, " + " conta_upd_ud_ko.ti_stato_udp_ud_ko, " + " case "
	    + "	when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud_ko.ni_ses_upd_ud_ko) ni_upd_ud_ko "
	    + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join MON_CONTA_SES_UPD_UD_KO conta_upd_ud_ko "
	    + "	on (conta_upd_ud_ko.id_key_total_ud_ko = abil_key_ud_ko.id_key_total_ud_ko "
	    + "	and conta_upd_ud_ko.ti_stato_udp_ud_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "group by abil_key_ud_ko.id_ambiente, conta_upd_ud_ko.ti_stato_udp_ud_ko, "
	    + "		 case "
	    + "			when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CNT_UPD_UD_KO_NOCOR_BY_ENTE = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_ente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	) " + "select "
	    + " abil_key_ud_ko.id_ente, " + " conta_upd_ud_ko.ti_stato_udp_ud_ko, " + " case "
	    + "	when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud_ko.ni_ses_upd_ud_ko) ni_upd_ud_ko "
	    + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join MON_CONTA_SES_UPD_UD_KO conta_upd_ud_ko "
	    + "	on (conta_upd_ud_ko.id_key_total_ud_ko = abil_key_ud_ko.id_key_total_ud_ko "
	    + "	and conta_upd_ud_ko.ti_stato_udp_ud_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "group by abil_key_ud_ko.id_ente, conta_upd_ud_ko.ti_stato_udp_ud_ko, "
	    + "		 case "
	    + "			when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CNT_UPD_UD_KO_NOCOR_BY_STRUT = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_strut = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "	) " + "select "
	    + " abil_key_ud_ko.id_strut, " + " abil_key_ud_ko.aa_key_unita_doc, "
	    + " abil_key_ud_ko.id_tipo_unita_doc, " + " abil_key_ud_ko.id_registro_unita_doc, "
	    + " abil_key_ud_ko.id_tipo_doc_princ, " + " conta_upd_ud_ko.ti_stato_udp_ud_ko, "
	    + " case "
	    + "	when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "		then '30gg' " + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) "
	    + "         then 'oggi' " + "		else 'before30gg' "
	    + " end ti_dt_creazione, " + " sum (conta_upd_ud_ko.ni_ses_upd_ud_ko) ni_upd_ud_ko "
	    + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join MON_CONTA_SES_UPD_UD_KO conta_upd_ud_ko "
	    + "	on (conta_upd_ud_ko.id_key_total_ud_ko = abil_key_ud_ko.id_key_total_ud_ko "
	    + "	and conta_upd_ud_ko.ti_stato_udp_ud_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "group by abil_key_ud_ko.id_strut,  " + "		 abil_key_ud_ko.aa_key_unita_doc, "
	    + "		 abil_key_ud_ko.id_tipo_unita_doc, "
	    + "		 abil_key_ud_ko.id_registro_unita_doc, "
	    + "		 abil_key_ud_ko.id_tipo_doc_princ, "
	    + "		 conta_upd_ud_ko.ti_stato_udp_ud_ko, " + "		 case "
	    + "			when conta_upd_ud_ko.dt_rif_conta between trunc (sysdate - 30) and trunc(sysdate - 1)  "
	    + "				then '30gg' "
	    + " when conta_upd_ud_ko.dt_rif_conta = trunc(sysdate) " + "         then 'oggi' "
	    + "				else 'before30gg' " + "		 end ";

    String MON_CNT_UPD_UD_COR_BY_AMB = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ambiente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	) " + "select " + " abil_key_ud.id_ambiente, " + " 'TOTALE' ti_stato_upd_ud, "
	    + " count(*) ni_upd_ud " + "from tmp_abil_key_ud abil_key_ud "
	    + "join ARO_UPD_UNITA_DOC upd " + "	on (upd.id_strut = abil_key_ud.id_strut "
	    + "	and upd.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd.ts_ini_ses) = abil_key_ud.dt_last_upd_ud) "
	    + "where upd.TI_STATO_UPD_ELENCO_VERS is not null "
	    + "group by abil_key_ud.id_ambiente, 'TOTALE' " + "UNION " + "select "
	    + " abil_key_ud.id_ambiente, "
	    + " upd_da_elab.ti_stato_upd_elenco_vers ti_stato_upd_ud, " + " count(*) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "	on (upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "	and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) "
	    + "group by abil_key_ud.id_ambiente, upd_da_elab.ti_stato_upd_elenco_vers ";

    String MON_CNT_UPD_UD_COR_BY_ENTE = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_ente = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	) " + "select " + " abil_key_ud.id_ente, " + " 'TOTALE' ti_stato_upd_ud, "
	    + " count(*) ni_upd_ud " + "from tmp_abil_key_ud abil_key_ud "
	    + "join ARO_UPD_UNITA_DOC upd " + "	on (upd.id_strut = abil_key_ud.id_strut "
	    + "	and upd.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd.ts_ini_ses) = abil_key_ud.dt_last_upd_ud) "
	    + "where upd.TI_STATO_UPD_ELENCO_VERS is not null "
	    + "group by abil_key_ud.id_ente, 'TOTALE' " + "UNION " + "select "
	    + " abil_key_ud.id_ente, " + " upd_da_elab.ti_stato_upd_elenco_vers ti_stato_upd_ud, "
	    + " count(*) ni_upd_ud " + "from tmp_abil_key_ud abil_key_ud "
	    + "join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "	on (upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "	and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) "
	    + "group by abil_key_ud.id_ente, upd_da_elab.ti_stato_upd_elenco_vers ";

    String MON_CNT_UPD_UD_COR_BY_STRUT = "with tmp_abil_key_ud as " + "	(select "
	    + "	  id_key_total_ud, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud = trunc(sysdate) "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "	) " + "select " + " abil_key_ud.id_strut, "
	    + " abil_key_ud.aa_key_unita_doc, " + " abil_key_ud.id_tipo_unita_doc, "
	    + " abil_key_ud.id_registro_unita_doc, " + " abil_key_ud.id_tipo_doc_princ, "
	    + " 'TOTALE' ti_stato_upd_ud, " + " count(*) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join ARO_UPD_UNITA_DOC upd "
	    + "	on (upd.id_strut = abil_key_ud.id_strut "
	    + "	and upd.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd.ts_ini_ses) = abil_key_ud.dt_last_upd_ud) "
	    + "where upd.TI_STATO_UPD_ELENCO_VERS is not null " + "group by abil_key_ud.id_strut,  "
	    + "		 abil_key_ud.aa_key_unita_doc,	"
	    + "		 abil_key_ud.id_tipo_unita_doc,	"
	    + "		 abil_key_ud.id_registro_unita_doc, "
	    + "		 abil_key_ud.id_tipo_doc_princ,		" + "		'TOTALE' "
	    + "UNION " + "select " + " abil_key_ud.id_strut, "
	    + " abil_key_ud.aa_key_unita_doc,		"
	    + " abil_key_ud.id_tipo_unita_doc,		"
	    + " abil_key_ud.id_registro_unita_doc,	"
	    + " abil_key_ud.id_tipo_doc_princ,		"
	    + " upd_da_elab.ti_stato_upd_elenco_vers ti_stato_upd_ud, " + " count(*) ni_upd_ud "
	    + "from tmp_abil_key_ud abil_key_ud " + "join ELV_UPD_UD_DA_ELAB_ELENCO upd_da_elab "
	    + "	on (upd_da_elab.id_strut = abil_key_ud.id_strut "
	    + "	and upd_da_elab.id_tipo_unita_doc = abil_key_ud.id_tipo_unita_doc "
	    + "	and upd_da_elab.id_registro_unita_doc = abil_key_ud.id_registro_unita_doc "
	    + "	and upd_da_elab.id_tipo_doc_princ = abil_key_ud.id_tipo_doc_princ "
	    + "	and upd_da_elab.aa_key_unita_doc = abil_key_ud.aa_key_unita_doc "
	    + "	and trunc(upd_da_elab.dt_creazione) = abil_key_ud.dt_last_upd_ud) "
	    + "group by abil_key_ud.id_strut,  " + "		 abil_key_ud.aa_key_unita_doc,	"
	    + "		 abil_key_ud.id_tipo_unita_doc,	"
	    + "		 abil_key_ud.id_registro_unita_doc, "
	    + "		 abil_key_ud.id_tipo_doc_princ,		"
	    + "		 upd_da_elab.ti_stato_upd_elenco_vers ";

    String MON_CNT_UPD_UD_KO_COR_BY_AMB = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, "
	    + "	  dt_last_upd_ud_ko " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ambiente = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and dt_last_upd_ud_ko = trunc(sysdate) " + "	) " + "select "
	    + " abil_key_ud_ko.id_ambiente, " + " ses_ko.ti_stato_ses_upd_ko, "
	    + " count(*) ni_upd_ud_ko " + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "	on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "	and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "	and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "	and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "	and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "	and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO') "
	    + "	and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) "
	    + "group by abil_key_ud_ko.id_ambiente, ses_ko.ti_stato_ses_upd_ko ";

    String MON_CNT_UPD_UD_KO_COR_BY_ENTE = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, "
	    + "	  dt_last_upd_ud_ko " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ente = :due " + "	 and id_user_iam_cor = :uno "
	    + "	 and dt_last_upd_ud_ko = trunc(sysdate) " + "	) " + "select "
	    + " abil_key_ud_ko.id_ente, " + " ses_ko.ti_stato_ses_upd_ko, "
	    + " count(*) ni_upd_ud_ko " + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "	on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "	and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "	and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "	and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "	and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "	and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO') "
	    + "	and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) "
	    + "group by abil_key_ud_ko.id_ente, ses_ko.ti_stato_ses_upd_ko ";

    String MON_CNT_UPD_UD_KO_COR_BY_STRUT = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "	  dt_last_upd_ud_ko "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + "	 and dt_last_upd_ud_ko = trunc(sysdate) "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "	) " + "select "
	    + " abil_key_ud_ko.id_strut, " + " abil_key_ud_ko.aa_key_unita_doc, "
	    + " abil_key_ud_ko.id_tipo_unita_doc, " + " abil_key_ud_ko.id_registro_unita_doc, "
	    + " abil_key_ud_ko.id_tipo_doc_princ,	" + " ses_ko.ti_stato_ses_upd_ko, "
	    + " count(*) ni_upd_ud_ko " + "from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "	on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "	and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "	and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "	and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "	and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "	and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO') "
	    + "	and trunc(ses_ko.ts_ini_ses) = abil_key_ud_ko.dt_last_upd_ud_ko) "
	    + "group by abil_key_ud_ko.id_strut,  "
	    + "		 abil_key_ud_ko.aa_key_unita_doc,	"
	    + "		 abil_key_ud_ko.id_tipo_unita_doc,	"
	    + "		 abil_key_ud_ko.id_registro_unita_doc,	"
	    + "		 abil_key_ud_ko.id_tipo_doc_princ,	"
	    + "		 ses_ko.ti_stato_ses_upd_ko ";

    // MEV#22438
    String MON_CHK_UD_UPD_KO_BY_AMB = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "       "
	    + "	  dt_last_upd_ud_ko " + "	   " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ambiente = :due " + "	 and id_user_iam_cor = :uno " + "	), "
	    + "       " + "    tmp_ses_upd_unita_ko as " + "    (select ses_ko.ti_stato_ses_upd_ko "
	    + "     from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "     join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "       on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "       and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "       and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "       and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "       and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc) " + "      "
	    + "     join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "	   on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + " " + "select " + " usr.id_user_iam id_user_iam_cor, " + " amb.id_ambiente, " + " "
	    + " " + "case " + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_norisolub, " + " " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_verif, " + " " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_noverif " + " "
	    + "from ORG_AMBIENTE amb " + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) "
	    + " " + "where amb.id_ambiente = :due " + "and usr.id_user_iam = :uno ";

    String MON_CHK_UD_UPD_KO_BY_ENTE = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "       "
	    + "	  dt_last_upd_ud_ko " + "	   " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ente = :due " + "	 and id_user_iam_cor = :uno " + "	), "
	    + "       " + "    tmp_ses_upd_unita_ko as " + "    (select ses_ko.ti_stato_ses_upd_ko "
	    + "     from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "     join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "       on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "       and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "       and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "       and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "       and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc) " + "      "
	    + "     join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "	   on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + "	 " + "select " + " usr.id_user_iam id_user_iam_cor, " + " ente.id_ente, " + " "
	    + " " + "case " + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_norisolub, " + " " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_verif, " + " " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_noverif " + " "
	    + "from ORG_ENTE ente " + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) " + " "
	    + "where ente.id_ente = :due " + "and usr.id_user_iam = :uno ";

    String MON_CHK_UD_UPD_KO_BY_STRUT = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "       " + "	  dt_last_upd_ud_ko " + "	   "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + "	  "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "      " + "	), " + "       "
	    + "    tmp_ses_upd_unita_ko as " + "    (select ses_ko.ti_stato_ses_upd_ko "
	    + "     from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "     join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "       on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "       and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "       and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "       and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "       and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc) " + "      "
	    + "     join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "	   on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + "	 " + "select " + " usr.id_user_iam id_user_iam_cor, " + " strut.id_strut, " + " "
	    + "case " + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_RISOLUBILE' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_norisolub, " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_verif, " + " " + "case "
	    + "	when exists (select *  "
	    + "				from tmp_ses_upd_unita_ko ses_ko "
	    + "				where ses_ko.ti_stato_ses_upd_ko = 'NON_VERIFICATO' "
	    + "				) " + "			then '1' "
	    + "			else '0' " + "end fl_ud_upd_ko_noverif " + " "
	    + "from ORG_STRUT strut " + "join IAM_USER usr " + "	on (usr.id_user_iam > 0) "
	    + " " + "where strut.id_strut = :due " + "and usr.id_user_iam = :uno";

    String MON_CNT_UD_UPD_KO_BY_AMB = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ambiente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "       "
	    + "	  dt_last_upd_ud_ko " + "	   " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ambiente = :due " + "	 and id_user_iam_cor = :uno " + "	), "
	    + "       " + "    tmp_upd_unita_doc_ko as " + "    (select distinct  "
	    + "       abil_key_ud_ko.id_ambiente,  " + "        " + "       upd_ud_ko.id_strut, "
	    + "       upd_ud_ko.cd_registro_key_unita_doc, " + "       upd_ud_ko.aa_key_unita_doc, "
	    + "       upd_ud_ko.cd_key_unita_doc, " + "       "
	    + "       ses_ko.ti_stato_ses_upd_ko " + "      from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "      join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "        on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "        and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "        and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "        and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "        and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "        and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "  " + "      join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "        on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + "     " + "select  " + " upd_ko.id_ambiente, " + " upd_ko.ti_stato_ses_upd_ko, "
	    + " count(*) ni_upd_ud_ko " + "  " + "from  tmp_upd_unita_doc_ko upd_ko   " + " "
	    + " group by upd_ko.id_ambiente, upd_ko.ti_stato_ses_upd_ko";

    String MON_CNT_UD_UPD_KO_BY_ENTE = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_ente, " + "	  id_strut, "
	    + "	  aa_key_unita_doc, " + "      id_tipo_unita_doc, "
	    + "      id_registro_unita_doc, " + "      id_tipo_doc_princ, " + "       "
	    + "	  dt_last_upd_ud_ko " + "	   " + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO "
	    + "	 where id_ente = :due " + "	 and id_user_iam_cor = :uno " + "	), "
	    + "       " + "    tmp_upd_unita_doc_ko as " + "    (select distinct  "
	    + "       abil_key_ud_ko.id_ente,  " + "        " + "       upd_ud_ko.id_strut, "
	    + "       upd_ud_ko.cd_registro_key_unita_doc, " + "       upd_ud_ko.aa_key_unita_doc, "
	    + "       upd_ud_ko.cd_key_unita_doc, " + "       "
	    + "       ses_ko.ti_stato_ses_upd_ko " + "      from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "      join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "        on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "        and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "        and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "        and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "        and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "        and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "  " + "      join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "        on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + " " + "select  " + " upd_ko.id_ente, " + " upd_ko.ti_stato_ses_upd_ko, "
	    + " count(*) ni_upd_ud_ko " + "  " + "from  tmp_upd_unita_doc_ko upd_ko   " + " "
	    + " group by upd_ko.id_ente, upd_ko.ti_stato_ses_upd_ko";

    String MON_CNT_UD_UPD_KO_BY_STRUT = "with tmp_abil_key_ud_ko as " + "	(select "
	    + "	  id_key_total_ud_ko, " + "	  id_strut, " + "	  aa_key_unita_doc, "
	    + "      id_tipo_unita_doc, " + "      id_registro_unita_doc, "
	    + "      id_tipo_doc_princ, " + "       " + "	  dt_last_upd_ud_ko " + "	   "
	    + "	 from MON_V_ABIL_KEY_TOTAL_UD_KO " + "	 where id_strut = :due "
	    + "	 and id_user_iam_cor = :uno " + " "
	    + "	 and aa_key_unita_doc BETWEEN :sette AND :otto "
	    + "	 and id_tipo_unita_doc = :quattro " + "	 and id_registro_unita_doc = :cinque "
	    + "	 and id_tipo_doc_princ = :sei " + "      " + "	), " + "       "
	    + "    tmp_upd_unita_doc_ko as " + "    (select distinct  " + "        "
	    + "       upd_ud_ko.id_strut, " + "       upd_ud_ko.cd_registro_key_unita_doc, "
	    + "       upd_ud_ko.aa_key_unita_doc as aa_key_ud, "
	    + "       upd_ud_ko.cd_key_unita_doc, " + "        "
	    + "       abil_key_ud_ko.aa_key_unita_doc, "
	    + "       abil_key_ud_ko.id_tipo_unita_doc, "
	    + "       abil_key_ud_ko.id_registro_unita_doc, "
	    + "       abil_key_ud_ko.id_tipo_doc_princ, " + "       "
	    + "       ses_ko.ti_stato_ses_upd_ko " + "      from tmp_abil_key_ud_ko abil_key_ud_ko "
	    + "      join VRS_SES_UPD_UNITA_DOC_KO ses_ko "
	    + "        on (ses_ko.id_strut = abil_key_ud_ko.id_strut "
	    + "        and ses_ko.id_tipo_unita_doc = abil_key_ud_ko.id_tipo_unita_doc "
	    + "        and ses_ko.id_registro_unita_doc = abil_key_ud_ko.id_registro_unita_doc "
	    + "        and ses_ko.id_tipo_doc_princ = abil_key_ud_ko.id_tipo_doc_princ "
	    + "        and ses_ko.aa_key_unita_doc = abil_key_ud_ko.aa_key_unita_doc "
	    + "        and ses_ko.ti_stato_ses_upd_ko in ('NON_RISOLUBILE', 'NON_VERIFICATO', 'VERIFICATO')) "
	    + "  " + "      join VRS_UPD_UNITA_DOC_KO upd_ud_ko "
	    + "        on (upd_ud_ko.id_upd_unita_doc_ko = ses_ko.id_upd_unita_doc_ko) " + "    ) "
	    + " " + "select " + " abil_key_ud_ko.id_strut, " + "   "
	    + " abil_key_ud_ko.aa_key_unita_doc, " + " abil_key_ud_ko.id_tipo_unita_doc, "
	    + " abil_key_ud_ko.id_registro_unita_doc, " + " abil_key_ud_ko.id_tipo_doc_princ, "
	    + "  " + " abil_key_ud_ko.ti_stato_ses_upd_ko, " + " count(*) ni_upd_ud_ko " + "  "
	    + "from tmp_upd_unita_doc_ko abil_key_ud_ko " + "	 "
	    + "group by abil_key_ud_ko.id_strut,  " + " "
	    + "		 abil_key_ud_ko.aa_key_unita_doc, "
	    + "		 abil_key_ud_ko.id_tipo_unita_doc, "
	    + "		 abil_key_ud_ko.id_registro_unita_doc, "
	    + "		 abil_key_ud_ko.id_tipo_doc_princ, " + "		  "
	    + "		 abil_key_ud_ko.ti_stato_ses_upd_ko";
    // end MEV#22438

    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Object> getTotali(String tipoEntita, BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
	    BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
	Map<String, Object> risultati = new HashMap<>();
	List<Object[]> checkCorResult = null;
	// List<Object[]> totCorResult = null;
	List<Object[]> totNoCorResult = null;
	List<Object[]> checkCorKoResult = null;
	List<Object[]> totCorKoResult = null;
	List<Object[]> totNoCorKoResult = null;
	// MEV#22438
	List<Object[]> checkUdUpdKoResult = null;
	List<Object[]> totUdUpdKoResult = null;
	// end MEV#22438
	if (tipoEntita.equals("AMBIENTE")) {
	    checkCorResult = getMonV(MON_CHK_UPD_UD_COR_BY_AMB, idUser, idAmbiente);
	    // totCorResult = getMonV(MON_CNT_UPD_UD_COR_BY_AMB, idUser, idAmbiente);
	    totNoCorResult = getMonV(MON_CNT_UPD_UD_NOCOR_BY_AMB, idUser, idAmbiente);
	    checkCorKoResult = getMonV(MON_CHK_UPD_UD_KO_COR_BY_AMB, idUser, idAmbiente);
	    totCorKoResult = getMonV(MON_CNT_UPD_UD_KO_COR_BY_AMB, idUser, idAmbiente);
	    totNoCorKoResult = getMonV(MON_CNT_UPD_UD_KO_NOCOR_BY_AMB, idUser, idAmbiente);
	    // MEV#22438
	    checkUdUpdKoResult = getMonV(MON_CHK_UD_UPD_KO_BY_AMB, idUser, idAmbiente);
	    totUdUpdKoResult = getMonV(MON_CNT_UD_UPD_KO_BY_AMB, idUser, idAmbiente);
	    // end MEV#22438
	} else if (tipoEntita.equals("ENTE")) {
	    checkCorResult = getMonV(MON_CHK_UPD_UD_COR_BY_ENTE, idUser, idEnte);
	    // totCorResult = getMonV(MON_CNT_UPD_UD_COR_BY_ENTE, idUser, idEnte);
	    totNoCorResult = getMonV(MON_CNT_UPD_UD_NOCOR_BY_ENTE, idUser, idEnte);
	    checkCorKoResult = getMonV(MON_CHK_UPD_UD_KO_COR_BY_ENTE, idUser, idEnte);
	    totCorKoResult = getMonV(MON_CNT_UPD_UD_KO_COR_BY_ENTE, idUser, idEnte);
	    totNoCorKoResult = getMonV(MON_CNT_UPD_UD_KO_NOCOR_BY_ENTE, idUser, idEnte);
	    // MEV#22438
	    checkUdUpdKoResult = getMonV(MON_CHK_UD_UPD_KO_BY_ENTE, idUser, idAmbiente);
	    totUdUpdKoResult = getMonV(MON_CNT_UD_UPD_KO_BY_ENTE, idUser, idEnte);
	    // end MEV#22438
	} else if (tipoEntita.equals("STRUTTURA")) {
	    List<Integer> anniList = null;
	    if (aaKeyUnitaDoc != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDoc.intValue());
		anniList.add(aaKeyUnitaDoc.intValue());
	    } else if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDocDa.intValue());
		anniList.add(aaKeyUnitaDocA.intValue());
	    }
	    checkCorResult = getMonVStrut(MON_CHK_UPD_UD_COR_BY_STRUT, idUser, idStrut, anniList,
		    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    // totCorResult = getMonVStrut(MON_CNT_UPD_UD_COR_BY_STRUT, idUser, idStrut, anniList,
	    // idTipoUnitaDoc,
	    // idRegistroUnitaDoc, idTipoDocPrinc);
	    totNoCorResult = getMonVStrut(MON_CNT_UPD_UD_NOCOR_BY_STRUT, idUser, idStrut, anniList,
		    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    checkCorKoResult = getMonVStrutKo(MON_CHK_UPD_UD_KO_COR_BY_STRUT, idUser, idStrut,
		    anniList, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    totCorKoResult = getMonVStrutKo(MON_CNT_UPD_UD_KO_COR_BY_STRUT, idUser, idStrut,
		    anniList, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    totNoCorKoResult = getMonVStrutKo(MON_CNT_UPD_UD_KO_NOCOR_BY_STRUT, idUser, idStrut,
		    anniList, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    // MEV#22438
	    checkUdUpdKoResult = getMonVStrutKo(MON_CHK_UD_UPD_KO_BY_STRUT, idUser, idStrut,
		    anniList, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    totUdUpdKoResult = getMonVStrutKo(MON_CNT_UD_UPD_KO_BY_STRUT, idUser, idStrut, anniList,
		    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	    // end MEV#22438
	}

	risultati.put("checkCorResult", checkCorResult);
	// risultati.put("totCorResult", totCorResult);
	risultati.put("totNoCorResult", totNoCorResult);
	risultati.put("checkCorKoResult", checkCorKoResult);
	risultati.put("totCorKoResult", totCorKoResult);
	risultati.put("totNoCorKoResult", totNoCorKoResult);
	// MEV#22438
	risultati.put("checkUdUpdKoResult", checkUdUpdKoResult);
	risultati.put("totUdUpdKoResult", totUdUpdKoResult);
	// end MEV#22438

	return risultati;
    }

    public Map<String, Object> getTotaliDataCorrente(String tipoEntita, BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
	    BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
	Map<String, Object> risultati = new HashMap<>();
	List<Object[]> totCorResult = null;
	if (tipoEntita.equals("AMBIENTE")) {
	    totCorResult = getMonV(MON_CNT_UPD_UD_COR_BY_AMB, idUser, idAmbiente);
	} else if (tipoEntita.equals("ENTE")) {
	    totCorResult = getMonV(MON_CNT_UPD_UD_COR_BY_ENTE, idUser, idEnte);
	} else if (tipoEntita.equals("STRUTTURA")) {
	    List<Integer> anniList = null;
	    if (aaKeyUnitaDoc != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDoc.intValue());
		anniList.add(aaKeyUnitaDoc.intValue());
	    } else if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDocDa.intValue());
		anniList.add(aaKeyUnitaDocA.intValue());
	    }
	    totCorResult = getMonVStrut(MON_CNT_UPD_UD_COR_BY_STRUT, idUser, idStrut, anniList,
		    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	}

	risultati.put("totCorResult", totCorResult);

	return risultati;
    }

    public Map<String, Object> getTotaliFalliti(String tipoEntita, BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
	    BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
	Map<String, Object> risultati = new HashMap<>();
	List<Object[]> totCorKoResult = null;
	if (tipoEntita.equals("AMBIENTE")) {
	    totCorKoResult = getMonV(MON_CNT_UPD_UD_KO_COR_BY_AMB, idUser, idAmbiente);
	} else if (tipoEntita.equals("ENTE")) {
	    totCorKoResult = getMonV(MON_CNT_UPD_UD_KO_COR_BY_ENTE, idUser, idEnte);
	} else if (tipoEntita.equals("STRUTTURA")) {
	    List<Integer> anniList = null;
	    if (aaKeyUnitaDoc != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDoc.intValue());
		anniList.add(aaKeyUnitaDoc.intValue());
	    } else if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDocDa.intValue());
		anniList.add(aaKeyUnitaDocA.intValue());
	    }
	    totCorKoResult = getMonVStrutKo(MON_CNT_UPD_UD_KO_COR_BY_STRUT, idUser, idStrut,
		    anniList, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	}

	risultati.put("totCorKoResult", totCorKoResult);

	return risultati;
    }

    // MEV#22438
    public Map<String, Object> getTotaliUdUpdFalliti(String tipoEntita, BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal aaKeyUnitaDoc,
	    BigDecimal aaKeyUnitaDocDa, BigDecimal aaKeyUnitaDocA, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDocPrinc) {
	Map<String, Object> risultati = new HashMap<>();
	List<Object[]> totUdUpdKoResult = null;
	if (tipoEntita.equals("AMBIENTE")) {
	    totUdUpdKoResult = getMonV(MON_CNT_UD_UPD_KO_BY_AMB, idUser, idAmbiente);
	} else if (tipoEntita.equals("ENTE")) {
	    totUdUpdKoResult = getMonV(MON_CNT_UD_UPD_KO_BY_ENTE, idUser, idEnte);
	} else if (tipoEntita.equals("STRUTTURA")) {
	    List<Integer> anniList = null;
	    if (aaKeyUnitaDoc != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDoc.intValue());
		anniList.add(aaKeyUnitaDoc.intValue());
	    } else if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
		anniList = new ArrayList<>();
		anniList.add(aaKeyUnitaDocDa.intValue());
		anniList.add(aaKeyUnitaDocA.intValue());
	    }
	    totUdUpdKoResult = getMonVStrutKo(MON_CNT_UD_UPD_KO_BY_STRUT, idUser, idStrut, anniList,
		    idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
	}

	risultati.put("totUdUpdKoResult", totUdUpdKoResult);

	return risultati;
    }
    // end MEV#22438

    public List<Object[]> getMonV(String viewName, BigDecimal param1, BigDecimal param2) {
	Query query = getEntityManager().createNativeQuery(viewName);
	query.setParameter("uno", param1);
	if (param2 != null) {
	    query.setParameter("due", param2);
	}
	return query.getResultList();
    }

    public List<Object[]> getMonVStrut(String viewName, BigDecimal param1, BigDecimal param2,
	    List<Integer> param3, BigDecimal param4, BigDecimal param5, BigDecimal param6) {
	String queryStr = viewName;
	if (param3 == null) {
	    queryStr = StringUtils.replace(queryStr,
		    "and aa_key_unita_doc BETWEEN :sette AND :otto", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud.aa_key_unita_doc,", " ");
	}
	if (param4 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_tipo_unita_doc = :quattro", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud.id_tipo_unita_doc,", " ");
	}
	if (param5 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_registro_unita_doc = :cinque", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud.id_registro_unita_doc,", " ");
	}
	if (param6 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_tipo_doc_princ = :sei", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud.id_tipo_doc_princ,", " ");
	}
	Query query = getEntityManager().createNativeQuery(queryStr);
	query.setParameter("uno", param1);
	if (param2 != null) {
	    query.setParameter("due", param2);
	}
	if (param3 != null) {
	    query.setParameter("sette", param3.get(0));
	    query.setParameter("otto", param3.get(1));
	}
	if (param4 != null) {
	    query.setParameter("quattro", param4);
	}
	if (param5 != null) {
	    query.setParameter("cinque", param5);
	}
	if (param6 != null) {
	    query.setParameter("sei", param6);
	}
	return query.getResultList();
    }

    // MEV#22438
    public List<Object[]> getMonVStrutKo(String viewName, BigDecimal param1, BigDecimal param2,
	    List<Integer> param3, BigDecimal param4, BigDecimal param5, BigDecimal param6) {
	String queryStr = viewName;
	if (param3 == null) {
	    queryStr = StringUtils.replace(queryStr,
		    "and aa_key_unita_doc BETWEEN :sette AND :otto", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud_ko.aa_key_unita_doc,", " ");
	}
	if (param4 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_tipo_unita_doc = :quattro", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud_ko.id_tipo_unita_doc,", " ");
	}
	if (param5 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_registro_unita_doc = :cinque", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud_ko.id_registro_unita_doc,", " ");
	}
	if (param6 == null) {
	    queryStr = StringUtils.replace(queryStr, "and id_tipo_doc_princ = :sei", " ");
	    queryStr = StringUtils.replace(queryStr, "abil_key_ud_ko.id_tipo_doc_princ,", " ");
	}
	Query query = getEntityManager().createNativeQuery(queryStr);
	query.setParameter("uno", param1);
	if (param2 != null) {
	    query.setParameter("due", param2);
	}
	if (param3 != null) {
	    query.setParameter("sette", param3.get(0));
	    query.setParameter("otto", param3.get(1));
	}
	if (param4 != null) {
	    query.setParameter("quattro", param4);
	}
	if (param5 != null) {
	    query.setParameter("cinque", param5);
	}
	if (param6 != null) {
	    query.setParameter("sei", param6);
	}
	return query.getResultList();
    }
    // end MEV#22438

    public List<MonVLisUpdUdInterface> retrieveVLisAggMeta(BigDecimal idUser, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
	    BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
	    String rangeNumeroA, Set<String> statoIndiceAip, String flSesUpdKoRisolti) {
	List<MonVLisUpdUdInterface> result = null;
	Date d1 = null;
	Date d2 = null;
	try {
	    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	    // Ricavo il builder
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    // Crea la query secondo l'entità da utilizzare
	    CriteriaQuery cq = null;
	    // Recupero l'entità principale
	    Root entity = null;
	    if ((statoIndiceAip.size() == 1 && (statoIndiceAip.contains("IN_ATTESA_SCHED")
		    || statoIndiceAip.contains("NON_SELEZ_SCHED")))
		    || (statoIndiceAip.size() == 2 && statoIndiceAip.contains("IN_ATTESA_SCHED")
			    && statoIndiceAip.contains("NON_SELEZ_SCHED"))) {
		cq = cb.createQuery(MonVLisUpdUdDaElab.class);
		entity = cq.from(MonVLisUpdUdDaElab.class);
	    } else {
		cq = cb.createQuery(MonVLisUpdUd.class);
		entity = cq.from(MonVLisUpdUd.class);
	    }
	    cq.select(entity);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsIniSes")));
	    // Condizioni di WHERE
	    List<Predicate> condizioni = new ArrayList<Predicate>();
	    condizioni.add(cb.equal(entity.get("idUserIamCor"), idUser));
	    condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
	    if (idEnte != null) {
		condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
	    }
	    if (idStrut != null) {
		condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
	    }
	    if (idTipoUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoUnitaDoc"), idTipoUnitaDoc));
	    }
	    if (idRegistroUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idRegistroUnitaDoc"), idRegistroUnitaDoc));
	    }
	    if (idTipoDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoDocPrinc"), idTipoDoc));
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
		Expression es1 = cb.function("TRUNC", Date.class, entity.get("tsIniSes"));
		Expression es2 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
				cb.parameter(String.class, "f1")));
		Expression es3 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d2"),
				cb.parameter(String.class, "f2")));
		condizioni.add(cb.between(es1, es2, es3));
	    }
	    if (statoIndiceAip != null && !statoIndiceAip.isEmpty()) {
		// condizioni.add(cb.in(entity.get("tiStatoUpdElencoVers"), statoIndiceAip));
		condizioni.add((entity.get("tiStatoUpdElencoVers").in(statoIndiceAip)));
		// CriteriaBuilder.In in = cb.in(entity.get("tiStatoUpdElencoVers"));
		// condizioni.add(in.in(statoIndiceAip));
	    }
	    if (flSesUpdKoRisolti != null && !flSesUpdKoRisolti.equals("")) {
		condizioni.add(cb.equal(entity.get("flSesUpdKoRisolti"), flSesUpdKoRisolti));
	    }
	    if (rangeAnnoA != null) {
		condizioni.add(cb.between(entity.get("aaKeyUnitaDoc"), rangeAnnoDa, rangeAnnoA));
	    } else if (rangeAnnoDa != null) {
		condizioni.add(cb.equal(entity.get("aaKeyUnitaDoc"), rangeAnnoDa));
	    }
	    if (rangeNumeroA != null && !rangeNumeroA.equals("")) {
		condizioni
			.add(cb.between(entity.get("cdKeyUnitaDoc"), rangeNumeroDa, rangeNumeroA));
	    } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
		condizioni.add(cb.equal(entity.get("cdKeyUnitaDoc"), rangeNumeroDa));
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
	    log.error("Errore nell'estrazione Della lista aggiornamenti metadati", ex);
	    throw ex;
	}
	return result;
    }

    public List<MonVLisUpdUdKoInterface> retrieveVLisAggMetaFalliti(BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
	    BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
	    String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
	    BigDecimal idErr) {
	List result = null;
	Date d1 = null;
	Date d2 = null;
	try {
	    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery cq = null;
	    Root entity = null;
	    if (idErr == null && idClasseErr == null) {
		cq = cb.createQuery(MonVLisUpdUdKo.class);
		entity = cq.from(MonVLisUpdUdKo.class);
	    } else {
		cq = cb.createQuery(MonVLisUpdUdKoByErr.class);
		entity = cq.from(MonVLisUpdUdKoByErr.class);
	    }
	    cq.select(entity);
	    cq.distinct(true);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsIniSes")));
	    List<Predicate> condizioni = new ArrayList<Predicate>();
	    condizioni.add(cb.equal(entity.get("idUserIamCor"), idUser));
	    condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
	    if (idEnte != null) {
		condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
	    }
	    if (idStrut != null) {
		condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
	    }
	    if (idTipoUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoUnitaDoc"), idTipoUnitaDoc));
	    }
	    if (idRegistroUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idRegistroUnitaDoc"), idRegistroUnitaDoc));
	    }
	    if (idTipoDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoDocPrinc"), idTipoDoc));
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
		Expression es1 = cb.function("TRUNC", Date.class, entity.get("tsIniSes"));
		Expression es2 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
				cb.parameter(String.class, "f1")));
		Expression es3 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d2"),
				cb.parameter(String.class, "f2")));
		condizioni.add(cb.between(es1, es2, es3));
	    }
	    if (statoSessione != null && !statoSessione.isEmpty()) {
		condizioni.add((entity.get("tiStatoSesUpdKo").in(statoSessione)));
	    }
	    if (idErr != null) {
		condizioni.add(cb.equal(entity.get("idErrSacer"), idErr));
	    }
	    if (idClasseErr != null) {
		condizioni.add(cb.equal(entity.get("idClasseErrSacer"), idClasseErr));
	    }
	    if (rangeAnnoA != null) {
		condizioni.add(cb.between(entity.get("aaKeyUnitaDoc"), rangeAnnoDa, rangeAnnoA));
	    } else if (rangeAnnoDa != null) {
		condizioni.add(cb.equal(entity.get("aaKeyUnitaDoc"), rangeAnnoDa));
	    }
	    if (rangeNumeroA != null && (!rangeNumeroA.equals(""))) {
		condizioni
			.add(cb.between(entity.get("cdKeyUnitaDoc"), rangeNumeroDa, rangeNumeroA));
	    } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
		condizioni.add(cb.equal(entity.get("cdKeyUnitaDoc"), rangeNumeroDa));
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
	    log.error("Errore nell'estrazione della lista aggiornamenti metadati falliti", ex);
	    throw ex;
	}
	return result;
    }

    public List<MonVLisUpdUdErr> retrieveVLisAggMetaErrati(Date[] dateValidate,
	    Set<String> tiStatoSes, BigDecimal idClasseErr, BigDecimal idErr) {
	List<MonVLisUpdUdErr> result = null;
	Date d1 = null;
	Date d2 = null;
	try {
	    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	    // Ricavo il builder
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    // Crea la query secondo l'entità da utilizzare
	    CriteriaQuery cq = null;
	    // Recupero l'entità principale
	    Root entity = null;
	    cq = cb.createQuery(MonVLisUpdUdErr.class);
	    entity = cq.from(MonVLisUpdUdErr.class);
	    cq.select(entity);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsIniSes")));
	    // Condizioni di WHERE
	    List<Predicate> condizioni = new ArrayList<Predicate>();

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
		Expression<Date> es1 = cb.function("TRUNC", Date.class, entity.get("tsIniSes"));
		Expression<Date> es2 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
				cb.parameter(String.class, "f1")));
		Expression<Date> es3 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d2"),
				cb.parameter(String.class, "f2")));
		condizioni.add(cb.between(es1, es2, es3));
	    }
	    if (tiStatoSes != null && (!tiStatoSes.isEmpty())) {
		CriteriaBuilder.In<String> inClause = cb.in(entity.get("tiStatoSes"));
		for (String stato : tiStatoSes) {
		    inClause.value(stato);
		}
		condizioni.add(inClause);
	    }
	    if (idErr != null) {
		condizioni.add(cb.equal(entity.get("idErrSacer"), idErr));
	    }
	    if (idClasseErr != null) {
		condizioni.add(cb.equal(entity.get("idClasseErrSacer"), idClasseErr));
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
	    log.error("Errore nell'estrazione della lista aggiornamenti metadati errati", ex);
	    throw ex;
	}
	return result;
    }

    // MEV#22438
    public List<MonVLisUdUpdKoInterface> retrieveVLisUnitaDocAggMetaFalliti(BigDecimal idUser,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoDoc, Date[] dateValidate,
	    BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
	    String rangeNumeroA, Set<String> statoSessione, BigDecimal idClasseErr,
	    BigDecimal idErr) {
	List result = null;
	Date d1 = null;
	Date d2 = null;
	try {
	    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery cq = null;
	    Root entity = null;
	    if (idErr == null && idClasseErr == null) {
		cq = cb.createQuery(MonVLisUdUpdKo.class);
		entity = cq.from(MonVLisUdUpdKo.class);
	    } else {
		cq = cb.createQuery(MonVLisUdUpdKoByErr.class);
		entity = cq.from(MonVLisUdUpdKoByErr.class);
	    }
	    cq.multiselect(entity.get("idUserIamCor"), entity.get("idAmbiente"),
		    entity.get("nmAmbiente"), entity.get("idEnte"), entity.get("nmEnte"),
		    entity.get("idStrut"), entity.get("nmStrut"), entity.get("idTipoUnitaDocLast"),
		    entity.get("nmTipoUnitaDocLast"), entity.get("cdRegistroKeyUnitaDoc"),
		    entity.get("idTipoDocPrincLast"), entity.get("nmTipoDocPrincLast"),
		    entity.get("aaKeyUnitaDoc"), entity.get("cdKeyUnitaDoc"),
		    entity.get("tiStatoUpdUdKo"), entity.get("tsIniLastSes"),
		    entity.get("idSesUpdUdKoLast"), entity.get("idUpdUnitaDocKo"),
		    entity.get("dsTsIniLastSes"), entity.get("dsEnteStrut"),
		    entity.get("dsUnitaDoc"), entity.get("dsErrPrincLast"),
		    entity.get("cdErrPrincLast"), entity.get("cdControlloWsPrincLast"));
	    cq.distinct(true);
	    List<Predicate> condizioni = new ArrayList<Predicate>();
	    condizioni.add(cb.equal(entity.get("idUserIamCor"), idUser));
	    condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
	    if ((idEnte != null)) {
		condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
	    }
	    if ((idStrut != null)) {
		condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
	    }
	    if (idTipoUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoUnitaDoc"), idTipoUnitaDoc));
	    }
	    if (idRegistroUnitaDoc != null) {
		condizioni.add(cb.equal(entity.get("idRegistroUnitaDoc"), idRegistroUnitaDoc));
	    }
	    if (idTipoDoc != null) {
		condizioni.add(cb.equal(entity.get("idTipoDocPrinc"), idTipoDoc));
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
		Expression<Date> es1 = cb.function("TRUNC", Date.class, entity.get("tsIniSes"));
		Expression<Date> es2 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d1"),
				cb.parameter(String.class, "f1")));
		Expression<Date> es3 = cb.function("TRUNC", Date.class,
			cb.function("TO_DATE", String.class, cb.parameter(String.class, "d2"),
				cb.parameter(String.class, "f2")));
		condizioni.add(cb.between(es1, es2, es3));
	    }
	    if (statoSessione != null && !statoSessione.isEmpty()) {
		condizioni.add((entity.get("tiStatoSesUpdKo").in(statoSessione)));
	    }
	    if (idErr != null) {
		condizioni.add(cb.equal(entity.get("idErrSacer"), idErr));
	    }
	    if (idClasseErr != null) {
		condizioni.add(cb.equal(entity.get("idClasseErrSacer"), idClasseErr));
	    }
	    if (rangeAnnoA != null) {
		condizioni.add(cb.between(entity.get("aaKeyUnitaDoc"), rangeAnnoDa, rangeAnnoA));
	    } else if (rangeAnnoDa != null) {
		condizioni.add(cb.equal(entity.get("aaKeyUnitaDoc"), rangeAnnoDa));
	    }
	    if (rangeNumeroA != null && (!rangeNumeroA.equals(""))) {
		condizioni
			.add(cb.between(entity.get("cdKeyUnitaDoc"), rangeNumeroDa, rangeNumeroA));
	    } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
		condizioni.add(cb.equal(entity.get("cdKeyUnitaDoc"), rangeNumeroDa));
	    }
	    cq.where(condizioni.toArray(new Predicate[] {}));
	    TypedQuery<?> q = getEntityManager().createQuery(cq);
	    if (dateValidate != null) {
		q.setParameter("d1", sf.format(d1));
		q.setParameter("d2", sf.format(d2));
		q.setParameter("f1", "DD/MM/YYYY");
		q.setParameter("f2", "DD/MM/YYYY");
	    }

	    result = q.getResultList();
	} catch (RuntimeException ex) {
	    throw new SacerRuntimeException(ex, SacerErrorCategory.INTERNAL_ERROR);
	}
	return result;
    }
    // end MEV#22438

    public List<DecClasseErrSacer> retrieveClasseErrSacerByTipiUso(List<String> tipiUsoErr) {
	List<DecClasseErrSacer> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("DecClasseErrSacer.findByTipiUsoErr",
		    DecClasseErrSacer.class);
	    query.setParameter("tipiUsoErr", tipiUsoErr);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    log.error("Errore nell'estrazione delle DecClasseErrSacer per il tipi uso [{}]", ex);
	    throw ex;
	}
	return result;
    }

    public List<DecErrSacer> retrieveErrSacerByCodClasse(String codClasse) {
	List<DecErrSacer> result = null;

	try {
	    Query query = getEntityManager().createNamedQuery("DecErrSacer.findByCodClasse",
		    DecErrSacer.class);
	    query.setParameter("codClasse", codClasse);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    log.error("Errore nell'estrazione delle DecErrSacer per il codice [{}]", ex);
	    throw ex;
	}
	return result;
    }

    public List<DecErrSacer> retrieveErrSacerByIdClasse(BigDecimal idClasse) {
	List<DecErrSacer> result = null;

	try {
	    Query query = getEntityManager().createNamedQuery("DecErrSacer.findByIdClasse",
		    DecErrSacer.class);
	    query.setParameter("idClasse", longFromBigDecimal(idClasse));
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    log.error("Errore nell'estrazione delle DecErrSacer per l'id [{}]", ex);
	    throw ex;
	}
	return result;
    }

    public List<VrsErrSesUpdUnitaDocKo> getVrsErrSesUpdUnitaDocKoList(
	    BigDecimal idSesUpdUnitaDocKo) {
	String queryStr = "SELECT u FROM VrsErrSesUpdUnitaDocKo u WHERE u.vrsSesUpdUnitaDocKo.idSesUpdUnitaDocKo = :idSesUpdUnitaDocKo ORDER BY u.pgErr";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idSesUpdUnitaDocKo", longFromBigDecimal(idSesUpdUnitaDocKo));
	List<VrsErrSesUpdUnitaDocKo> lista = query.getResultList();
	return lista;
    }

    public List<Long> getVrsSesUpdUnitaDocKoList(BigDecimal idUpdUnitaDocKo) {
	String queryStr = "SELECT u.idSesUpdUnitaDocKo FROM VrsSesUpdUnitaDocKo u WHERE u.vrsUpdUnitaDocKo.idUpdUnitaDocKo = :idUpdUnitaDocKo ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idUpdUnitaDocKo", longFromBigDecimal(idUpdUnitaDocKo));
	List<Long> lista = query.getResultList();
	return lista;
    }

    public List<VrsErrSesUpdUnitaDocKo> getVrsErrSesUpdUnitaDocKoList(
	    List<Long> idSesUpdUnitaDocKoList) {
	String queryStr = "SELECT u FROM VrsErrSesUpdUnitaDocKo u WHERE u.vrsSesUpdUnitaDocKo.idSesUpdUnitaDocKo IN :idSesUpdUnitaDocKoList "
		+ "ORDER BY u.tiErr, u.vrsSesUpdUnitaDocKo.tsFineSes ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idSesUpdUnitaDocKoList", idSesUpdUnitaDocKoList);
	List<VrsErrSesUpdUnitaDocKo> lista = query.getResultList();
	return lista;
    }

    public List<VrsErrSesUpdUnitaDocErr> getVrsErrSesUpdUnitaDocErrList(
	    BigDecimal idSesUpdUnitaDocErr) {
	String queryStr = "SELECT u FROM VrsErrSesUpdUnitaDocErr u WHERE u.vrsSesUpdUnitaDocErr.idSesUpdUnitaDocErr = :idSesUpdUnitaDocErr ORDER BY u.pgErr";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idSesUpdUnitaDocErr", longFromBigDecimal(idSesUpdUnitaDocErr));
	List<VrsErrSesUpdUnitaDocErr> lista = query.getResultList();
	return lista;
    }

    public List<AroVLisUpdDocUnitaDoc> getAroVLisUpdDocUnitaDocList(BigDecimal idUpdUnitaDoc) {
	String queryStr = "SELECT u FROM AroVLisUpdDocUnitaDoc u WHERE u.idUpdUnitaDoc = :idUpdUnitaDoc ORDER BY u.tiDocOrd, u.pgDoc";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
	List<AroVLisUpdDocUnitaDoc> lista = query.getResultList();
	return lista;
    }

    public List<AroVLisUpdCompUnitaDoc> getAroVLisUpdCompUnitaDocList(BigDecimal idUpdUnitaDoc) {
	String queryStr = "SELECT u FROM AroVLisUpdCompUnitaDoc u WHERE u.idUpdUnitaDoc = :idUpdUnitaDoc ORDER BY u.tiDocOrd, u.pgDoc, u.niOrdCompDoc";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
	List<AroVLisUpdCompUnitaDoc> lista = query.getResultList();
	return lista;
    }

    public List<AroVLisUpdKoRisolti> getAroVLisUpdKoRisoltiList(BigDecimal idUpdUnitaDoc) {
	String queryStr = "SELECT u FROM AroVLisUpdKoRisolti u WHERE u.idUpdUnitaDoc = :idUpdUnitaDoc ORDER BY u.tsIniSes DESC";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
	List<AroVLisUpdKoRisolti> lista = query.getResultList();
	return lista;
    }

    public List<AroWarnUpdUnitaDoc> getAroWarnUpdUnitaDocList(BigDecimal idUpdUnitaDoc) {
	String queryStr = "SELECT u FROM AroWarnUpdUnitaDoc u WHERE u.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ORDER BY u.pgWarn";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idUpdUnitaDoc", longFromBigDecimal(idUpdUnitaDoc));
	List<AroWarnUpdUnitaDoc> lista = query.getResultList();
	return lista;
    }

    public MonContaSesUpdUdKo retrieveMonContaSesUpdUdKo(Date dtRifConta, long idStrut,
	    BigDecimal aaKeyUnitaDoc, long idRegistroUnitaDoc, long idTipoUnitaDoc, long idTipoDoc,
	    String tiStatoUpdUdKo) {
	String queryStr = "SELECT contaSesUpdUdKo FROM MonContaSesUpdUdKo contaSesUpdUdKo "
		+ "WHERE contaSesUpdUdKo.dtRifConta = :dtRifConta "
		+ "AND contaSesUpdUdKo.monKeyTotalUdKo.orgStrut.idStrut = :idStrut "
		+ "AND contaSesUpdUdKo.monKeyTotalUdKo.aaKeyUnitaDoc = :aaKeyUnitaDoc "
		+ "AND contaSesUpdUdKo.monKeyTotalUdKo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc "
		+ "AND contaSesUpdUdKo.monKeyTotalUdKo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc "
		+ "AND contaSesUpdUdKo.monKeyTotalUdKo.decTipoDocPrinc.idTipoDoc = :idTipoDoc "
		+ "AND contaSesUpdUdKo.tiStatoUdpUdKo = :tiStatoUpdUdKo ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("dtRifConta", dtRifConta);
	query.setParameter("idStrut", idStrut);
	query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
	query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
	query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
	query.setParameter("idTipoDoc", idTipoDoc);
	query.setParameter("tiStatoUpdUdKo",
		it.eng.parer.entity.constraint.MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo
			.valueOf(tiStatoUpdUdKo));
	List<MonContaSesUpdUdKo> conta = (List<MonContaSesUpdUdKo>) query.getResultList();
	if (!conta.isEmpty() && conta.size() == 1) {
	    return conta.get(0);
	} else {
	    return null;
	}
    }

    public LogVVisLastSched getLogVVisLastSched(String nmJob) {
	String queryStr = "SELECT u FROM LogVVisLastSched u WHERE u.nmJob = :nmJob";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmJob", nmJob);
	List<LogVVisLastSched> listaLog = query.getResultList();
	if (!listaLog.isEmpty()) {
	    return listaLog.get(0);
	}
	return null;
    }

    public List<OrgStrut> getStruttureVersantiPerAggMeta() {
	List<OrgStrut> result = null;
	try {
	    String queryStr = "SELECT DISTINCT strut FROM OrgStrut strut, AplVGetvalParamByTiud getvalParam "
		    + "WHERE strut.idStrut = getvalParam.idStrut "
		    + "AND getvalParam.nmParamApplic = 'FL_ABILITA_UPD_META' "
		    + "AND getvalParam.dsValoreParamApplic = 'true' " + "ORDER BY strut.idStrut ";
	    Query query = getEntityManager().createQuery(queryStr);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    log.error("Errore nell'estrazione delle strutture versati", ex);
	    throw ex;
	}
	return result;
    }

    public int deleteMonContaSesUpdUd(long idStrut, Date data) {
	String queryStr = "DELETE FROM MonContaSesUpdUd c " + " WHERE c IN "
		+ " (SELECT contaSesUpdUd FROM MonContaSesUpdUd contaSesUpdUd WHERE contaSesUpdUd.monKeyTotalUd.orgStrut.idStrut = :idStrut "
		+ " AND contaSesUpdUd.dtRifConta = :data)";
	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idStrut", idStrut);
	q.setParameter("data", data);
	int i = q.executeUpdate();
	getEntityManager().flush();
	return i;
    }

    public int deleteMonContaSesUpdUdKo(long idStrut, Date data) {
	String queryStr = "DELETE FROM MonContaSesUpdUdKo cko "
		+ "WHERE cko in (SELECT contaSesUpdUdKo FROM MonContaSesUpdUdKo contaSesUpdUdKo "
		+ "WHERE contaSesUpdUdKo.monKeyTotalUdKo.orgStrut.idStrut = :idStrut "
		+ "AND contaSesUpdUdKo.dtRifConta = :data)";
	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idStrut", idStrut);
	q.setParameter("data", data);
	int i = q.executeUpdate();
	getEntityManager().flush();
	return i;
    }

    public List<Object[]> getAggMetaPerCalcoloContenuto(long idStrut, Date data) {
	String queryStr = "SELECT updUnitaDoc.iamUser.idUserIam, keyTotalUd.idKeyTotalUd, updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "updUnitaDoc.decTipoDocPrinc.idTipoDoc, updUnitaDoc.aaKeyUnitaDoc, updUnitaDoc.orgStrut.idStrut, keyTotalUd.orgSubStrut.idSubStrut, "
		+ "updUnitaDoc.tiStatoUpdElencoVers, COUNT(keyTotalUd.idKeyTotalUd) "
		+ "FROM AroUpdUnitaDoc updUnitaDoc, MonKeyTotalUd keyTotalUd "
		+ "WHERE updUnitaDoc.orgStrut.idStrut = keyTotalUd.orgStrut.idStrut "
		+ "AND updUnitaDoc.orgStrut.idStrut=:idStrut "
		+ "AND updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc=keyTotalUd.decTipoUnitaDoc.idTipoUnitaDoc "
		+ "AND updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc=keyTotalUd.decRegistroUnitaDoc.idRegistroUnitaDoc "
		+ "AND updUnitaDoc.decTipoDocPrinc.idTipoDoc=keyTotalUd.decTipoDocPrinc.idTipoDoc "
		+ "AND updUnitaDoc.aaKeyUnitaDoc=keyTotalUd.aaKeyUnitaDoc "
		+ "AND TRUNC(updUnitaDoc.tsIniSes) = TRUNC(:data) "
		+ "AND TRUNC(updUnitaDoc.tsIniSes) = TRUNC(keyTotalUd.dtLastUpdUd) "
		+ "AND updUnitaDoc.tiStatoUpdElencoVers IN (:valori) "
		+ "GROUP BY updUnitaDoc.iamUser.idUserIam,keyTotalUd.idKeyTotalUd, updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "updUnitaDoc.decTipoDocPrinc.idTipoDoc, updUnitaDoc.aaKeyUnitaDoc, updUnitaDoc.orgStrut.idStrut, keyTotalUd.orgSubStrut.idSubStrut, "
		+ "updUnitaDoc.tiStatoUpdElencoVers ";
	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idStrut", idStrut);
	q.setParameter("data", data);
	List<AroUpdUDTiStatoUpdElencoVers> valori = new ArrayList<AroUpdUDTiStatoUpdElencoVers>();
	valori.add(AroUpdUDTiStatoUpdElencoVers.IN_ATTESA_SCHED);
	valori.add(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
	q.setParameter("valori", valori);
	List<Object[]> lista = new ArrayList<>();
	lista.addAll(q.getResultList());

	String queryStr2 = "SELECT updUnitaDoc.iamUser.idUserIam, keyTotalUd.idKeyTotalUd, updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "updUnitaDoc.decTipoDocPrinc.idTipoDoc, updUnitaDoc.aaKeyUnitaDoc, updUnitaDoc.orgStrut.idStrut, keyTotalUd.orgSubStrut.idSubStrut, "
		+ "'TOTALE', COUNT(keyTotalUd.idKeyTotalUd) "
		+ "FROM AroUpdUnitaDoc updUnitaDoc, MonKeyTotalUd keyTotalUd "
		+ "WHERE updUnitaDoc.orgStrut.idStrut = keyTotalUd.orgStrut.idStrut "
		+ "AND updUnitaDoc.orgStrut.idStrut=:idStrut "
		+ "AND updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc=keyTotalUd.decTipoUnitaDoc.idTipoUnitaDoc "
		+ "AND updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc=keyTotalUd.decRegistroUnitaDoc.idRegistroUnitaDoc "
		+ "AND updUnitaDoc.decTipoDocPrinc.idTipoDoc=keyTotalUd.decTipoDocPrinc.idTipoDoc "
		+ "AND updUnitaDoc.aaKeyUnitaDoc=keyTotalUd.aaKeyUnitaDoc "
		+ "AND TRUNC( updUnitaDoc.tsIniSes) = TRUNC( :data) "
		+ "AND TRUNC( updUnitaDoc.tsIniSes) = TRUNC( keyTotalUd.dtLastUpdUd) "
		+ "GROUP BY updUnitaDoc.iamUser.idUserIam,keyTotalUd.idKeyTotalUd, updUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, updUnitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "updUnitaDoc.decTipoDocPrinc.idTipoDoc, updUnitaDoc.aaKeyUnitaDoc, updUnitaDoc.orgStrut.idStrut, keyTotalUd.orgSubStrut.idSubStrut, "
		+ "'TOTALE' ";
	Query q2 = getEntityManager().createQuery(queryStr2);
	q2.setParameter("idStrut", idStrut);
	q2.setParameter("data", data);
	lista.addAll(q2.getResultList());
	return lista;
    }

    /*
     * public List<Object[]> getAggMetaPerCalcoloContenuto2(long idStrut, Date data) { String
     * queryStr = "select " + "      key_tot.id_key_total_ud " + "	 upd.id_strut, " +
     * "	 ud.id_sub_strut, " + "	 upd.id_tipo_unita_doc, " +
     * "	 upd.id_registro_unita_doc, " + "	 upd.id_tipo_doc_princ, " +
     * "	 upd.aa_key_unita_doc, " + "	 upd.ti_stato_upd_elenco_vers ti_stato_upd_ud, " +
     * "	 trunc(upd.ts_ini_ses) dt_upd_ud, " + "	 count(*) ni_ses_upd_ud " +
     * "	from ARO_UPD_UNITA_DOC upd " + "	join ARO_UNITA_DOC ud " +
     * "		on (ud.id_unita_doc = upd.id_unita_doc " +
     * "		and ud.dt_annul = to_date('31/12/2444', 'dd/mm/yyyy')) " +
     * "join MON_KEY_TOTAL_UD key_tot " + "on (key_tot.id_strut = upd.id_strut " +
     * "and key_tot.id_sub_strut = ud.id_sub_strut " +
     * "and key_tot.id_tipo_unita_doc = upd.id_tipo_unita_doc  " +
     * "and key_tot.id_registro_unita_doc = upd.id_registro_unita_doc  " +
     * "and key_tot.id_tipo_doc_princ = upd.id_tipo_doc_princ  " +
     * "and key_tot.aa_key_unita_doc = upd.aa_key_unita_doc) " +
     * "	where upd.ti_stato_upd_elenco_vers in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED') " +
     * "	and trunc(upd.ts_ini_ses) <= trunc(:uno) " + " and upd.id_strut = :due " +
     * "	group by upd.id_strut, ud.id_sub_strut, upd.id_tipo_unita_doc, upd.id_registro_unita_doc, upd.id_tipo_doc_princ, "
     * +
     * "			 upd.aa_key_unita_doc, upd.ti_stato_upd_elenco_vers, trunc(upd.ts_ini_ses)"
     * ; Query q = getEntityManager().createNativeQuery(queryStr); q.setParameter(2, idStrut);
     * q.setParameter(1, data); // List<AroUpdUDTiStatoUpdElencoVers> valori = new
     * ArrayList<AroUpdUDTiStatoUpdElencoVers>(); //
     * valori.add(AroUpdUDTiStatoUpdElencoVers.IN_ATTESA_SCHED); //
     * valori.add(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED); // q.setParameter("valori",
     * valori); List<Object[]> lista = new ArrayList<>(); lista.addAll(q.getResultList());
     *
     * String queryStr2 = "select " + "      key_tot.id_key_total_ud " + "	 upd.id_strut, " +
     * "	 ud.id_sub_strut, " + "	 upd.id_tipo_unita_doc, " +
     * "	 upd.id_registro_unita_doc, " + "	 upd.id_tipo_doc_princ, " +
     * "	 upd.aa_key_unita_doc, " + "	 'TOTALE' ti_stato_upd_ud, " +
     * "	 trunc(upd.ts_ini_ses) dt_upd_ud, " + "	 count(*) ni_ses_upd_ud " +
     * "	from ARO_UPD_UNITA_DOC upd " + "	join ARO_UNITA_DOC ud " +
     * "		on (ud.id_unita_doc = upd.id_unita_doc " +
     * "		and ud.dt_annul = to_date('31/12/2444', 'dd/mm/yyyy')) " +
     * "join MON_KEY_TOTAL_UD key_tot " + "on (key_tot.id_strut = upd.id_strut " +
     * "and key_tot.id_sub_strut = ud.id_sub_strut " +
     * "and key_tot.id_tipo_unita_doc = upd.id_tipo_unita_doc  " +
     * "and key_tot.id_registro_unita_doc = upd.id_registro_unita_doc  " +
     * "and key_tot.id_tipo_doc_princ = upd.id_tipo_doc_princ  " +
     * "and key_tot.aa_key_unita_doc = upd.aa_key_unita_doc) " +
     * "	and trunc(upd.ts_ini_ses) <= trunc(:uno) " + " and upd.id_strut = :due " +
     * "	group by upd.id_strut, ud.id_sub_strut, upd.id_tipo_unita_doc, upd.id_registro_unita_doc, upd.id_tipo_doc_princ, "
     * + "			 upd.aa_key_unita_doc, 'TOTALE', trunc(upd.ts_ini_ses)"; Query q2 =
     * getEntityManager().createNativeQuery(queryStr2); q2.setParameter(2, idStrut);
     * q2.setParameter(1, data); // q2.setParameter("valori", valori);
     * lista.addAll(q2.getResultList()); return lista; }
     */

    public List<Object[]> getAggMetaPerCalcoloContenuto3(long idStrut, Date data) {
	String queryStr = "select " + " key_tot.id_key_total_ud, " + " tmp.ti_stato_upd_ud, "
		+ " tmp.dt_upd_ud, " + " tmp.ni_ses_upd_ud " + "from " + "(select "
		+ "	 upd.id_strut, " + "	 ud.id_sub_strut, " + "	 upd.id_tipo_unita_doc, "
		+ "	 upd.id_registro_unita_doc, " + "	 upd.id_tipo_doc_princ, "
		+ "	 upd.aa_key_unita_doc, "
		+ "	 upd.ti_stato_upd_elenco_vers ti_stato_upd_ud, "
		+ "	 trunc(upd.ts_ini_ses) dt_upd_ud, " + "	 count(*) ni_ses_upd_ud "
		+ "	from ARO_UPD_UNITA_DOC upd " + "	join ARO_UNITA_DOC ud "
		+ "		on (ud.id_unita_doc = upd.id_unita_doc "
		+ "		and ud.dt_annul = to_date('31/12/2444', 'dd/mm/yyyy')) "
		+ "	where upd.ti_stato_upd_elenco_vers in ('IN_ATTESA_SCHED', 'NON_SELEZ_SCHED') "
		+ "and trunc(upd.ts_ini_ses) = trunc(:uno) "
		+ "                and upd.id_strut = :due "
		+ "	group by upd.id_strut, ud.id_sub_strut, upd.id_tipo_unita_doc, upd.id_registro_unita_doc, upd.id_tipo_doc_princ, "
		+ "			 upd.aa_key_unita_doc, upd.ti_stato_upd_elenco_vers, trunc(upd.ts_ini_ses) "
		+ "	 " + "	UNION " + "	select " + "	 upd.id_strut, "
		+ "	 ud.id_sub_strut, " + "	 upd.id_tipo_unita_doc, "
		+ "	 upd.id_registro_unita_doc, " + "	 upd.id_tipo_doc_princ, "
		+ "	 upd.aa_key_unita_doc, " + "	 'TOTALE' ti_stato_upd_ud, "
		+ "	 trunc(upd.ts_ini_ses) dt_upd_ud, " + "	 count(*) ni_ses_upd_ud "
		+ "	from ARO_UPD_UNITA_DOC upd " + "	join ARO_UNITA_DOC ud "
		+ "		on (ud.id_unita_doc = upd.id_unita_doc "
		+ "		and ud.dt_annul = to_date('31/12/2444', 'dd/mm/yyyy')) "
		+ "and trunc(upd.ts_ini_ses) = trunc(:uno) "
		+ "                and upd.id_strut = :due "
		+ "	group by upd.id_strut, ud.id_sub_strut, upd.id_tipo_unita_doc, upd.id_registro_unita_doc, upd.id_tipo_doc_princ, "
		+ "			 upd.aa_key_unita_doc, 'TOTALE', trunc(upd.ts_ini_ses) "
		+ "	) tmp " + "	join MON_KEY_TOTAL_UD key_tot "
		+ "		on (key_tot.id_strut = tmp.id_strut "
		+ "		and key_tot.id_sub_strut = tmp.id_sub_strut "
		+ "		and key_tot.id_tipo_unita_doc = tmp.id_tipo_unita_doc  "
		+ "		and key_tot.id_registro_unita_doc = tmp.id_registro_unita_doc  "
		+ "		and key_tot.id_tipo_doc_princ = tmp.id_tipo_doc_princ  "
		+ "		and key_tot.aa_key_unita_doc = tmp.aa_key_unita_doc)";
	Query q = getEntityManager().createNativeQuery(queryStr);
	q.setParameter("due", idStrut);
	q.setParameter("uno", data);
	return q.getResultList();
    }

    public List<Object[]> getSesAggMetaPerCalcoloContenuto(long idStrut, Date data) {
	String queryStr = "SELECT sesUpdUnitaDocKo.tiStatoSesUpdKo, keyTotalUdKo.idKeyTotalUdKo, sesUpdUnitaDocKo.decTipoUnitaDoc.idTipoUnitaDoc, sesUpdUnitaDocKo.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "sesUpdUnitaDocKo.decTipoDocPrinc.idTipoDoc, sesUpdUnitaDocKo.aaKeyUnitaDoc, sesUpdUnitaDocKo.orgStrut.idStrut, "
		+ "sesUpdUnitaDocKo.tiStatoSesUpdKo, COUNT(keyTotalUdKo.idKeyTotalUdKo) "
		+ "FROM VrsSesUpdUnitaDocKo sesUpdUnitaDocKo, MonKeyTotalUdKo keyTotalUdKo "
		+ "WHERE sesUpdUnitaDocKo.orgStrut.idStrut = keyTotalUdKo.orgStrut.idStrut "
		+ "AND sesUpdUnitaDocKo.orgStrut.idStrut=:idStrut "
		+ "AND sesUpdUnitaDocKo.decTipoUnitaDoc.idTipoUnitaDoc=keyTotalUdKo.decTipoUnitaDoc.idTipoUnitaDoc "
		+ "AND sesUpdUnitaDocKo.decRegistroUnitaDoc.idRegistroUnitaDoc=keyTotalUdKo.decRegistroUnitaDoc.idRegistroUnitaDoc "
		+ "AND sesUpdUnitaDocKo.decTipoDocPrinc.idTipoDoc=keyTotalUdKo.decTipoDocPrinc.idTipoDoc "
		+ "AND sesUpdUnitaDocKo.aaKeyUnitaDoc=keyTotalUdKo.aaKeyUnitaDoc "
		+ "AND TRUNC( sesUpdUnitaDocKo.tsIniSes) = TRUNC( :data) "
		+ "AND TRUNC( sesUpdUnitaDocKo.tsIniSes) = TRUNC( keyTotalUdKo.dtLastUpdUdKo) "
		+ "GROUP BY sesUpdUnitaDocKo.tiStatoSesUpdKo,keyTotalUdKo.idKeyTotalUdKo, sesUpdUnitaDocKo.decTipoUnitaDoc.idTipoUnitaDoc, sesUpdUnitaDocKo.decRegistroUnitaDoc.idRegistroUnitaDoc, "
		+ "sesUpdUnitaDocKo.decTipoDocPrinc.idTipoDoc, sesUpdUnitaDocKo.aaKeyUnitaDoc, sesUpdUnitaDocKo.orgStrut.idStrut, "
		+ "sesUpdUnitaDocKo.tiStatoSesUpdKo ";
	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idStrut", idStrut);
	q.setParameter("data", data);
	return q.getResultList();
    }

    public List<Object[]> getSesAggMetaPerCalcoloContenuto3(long idStrut, Date data) {
	String queryStr = "select  " + " key_tot_ko.id_key_total_ud_ko, "
		+ " tmp.ti_stato_udp_ud_ko, " + " tmp.dt_upd_ud_ko, " + " tmp.ni_ses_upd_ud_ko, "
		+ " tmp.id_strut, " + " tmp.id_tipo_unita_doc, " + " tmp.id_registro_unita_doc, "
		+ " tmp.id_tipo_doc_princ, " + " tmp.aa_key_unita_doc " + "from " + "	(select "
		+ "	 upd_ko.id_strut, " + "	 upd_ko.id_tipo_unita_doc, "
		+ "	 upd_ko.id_registro_unita_doc, " + "	 upd_ko.id_tipo_doc_princ, "
		+ "	 upd_ko.aa_key_unita_doc, "
		+ "	 upd_ko.ti_stato_ses_upd_ko ti_stato_udp_ud_ko, "
		+ "	 trunc(upd_ko.ts_ini_ses) dt_upd_ud_ko, " + "	 count(*) ni_ses_upd_ud_ko "
		+ "	from VRS_SES_UPD_UNITA_DOC_KO upd_ko "
		+ "	where trunc(upd_ko.ts_ini_ses) = trunc(:due) "
		+ "                and upd_ko.id_strut = :uno "
		+ "	group by upd_ko.id_strut, upd_ko.id_tipo_unita_doc, upd_ko.id_registro_unita_doc, upd_ko.id_tipo_doc_princ, "
		+ "			 upd_ko.aa_key_unita_doc, upd_ko.ti_stato_ses_upd_ko, trunc(upd_ko.ts_ini_ses) "
		+ "	) tmp " + "	join MON_KEY_TOTAL_UD_KO key_tot_ko "
		+ "		on (key_tot_ko.id_strut = tmp.id_strut "
		+ "		and key_tot_ko.id_tipo_unita_doc = tmp.id_tipo_unita_doc  "
		+ "		and key_tot_ko.id_registro_unita_doc = tmp.id_registro_unita_doc  "
		+ "		and key_tot_ko.id_tipo_doc_princ = tmp.id_tipo_doc_princ  "
		+ "		and key_tot_ko.aa_key_unita_doc = tmp.aa_key_unita_doc)";
	Query q = getEntityManager().createNativeQuery(queryStr);
	q.setParameter("uno", idStrut);
	q.setParameter("due", data);
	return q.getResultList();
    }
}
