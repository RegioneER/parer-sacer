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

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.DocStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm.FiltriElenchiVersamento;
import it.eng.parer.viewEntity.*;
import it.eng.parer.web.util.StringPadding;
import it.eng.spagoCore.error.EMFError;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static it.eng.parer.util.Utils.*;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ElenchiVersamentoHelper extends GenericHelper {

    /* Definizione variabili della classe ElenchiVersamentoHelper */
    @EJB
    private ElencoVersamentoHelper evHelper;

    private String getQueryRicElenchiVersByUd(String unitaDocFilter) {
	return "SELECT /*+ parallel */  " + "       e.id_elenco_vers, " + "       e.nm_elenco, "
		+ "       e.ds_elenco, " + "       e.ti_stato_elenco, "
		+ "       e.dt_creazione_elenco, " + "       e.dt_chius, "
		+ "       e.dt_firma_indice, " + "       e.ni_comp_vers_elenco, "
		+ "       e.ni_comp_agg_elenco, " + "       e.ni_size_vers_elenco, "
		+ "       e.ni_size_agg_elenco, " + "       e.nt_indice_elenco, "
		+ "       e.nt_elenco_chiuso, " + "       e.id_criterio_raggr, "
		+ "       e.dt_creazione_elenco_ix_aip, " + "       e.dt_firma_elenco_ix_aip, "
		+ "       e.fl_elenco_fisc, " + "       e.fl_elenco_standard, "
		+ "       e.ni_indici_aip, " + "       e.ti_valid_elenco, "
		+ "       crit.nm_criterio_raggr, "
		+ "       daelab.TS_STATO_ELENCO ts_Stato_Elenco_In_Coda_Jms, "
		+ "       e.id_strut    id_strut_ud, " + "       case "
		+ " when e.ti_gest_elenco is not null " + "then e.ti_gest_elenco " + " else "
		+ " case " + " when crit.ti_gest_elenco_criterio is not null "
		+ "       then crit.ti_gest_elenco_criterio " + " else " + "       case "
		+ " when e.fl_elenco_standard = '0' "
		+ "       then (select ds_valore_param_applic from APL_V_GETVAL_PARAM_BY_strut val where val.id_strut = strut.id_strut and NM_PARAM_APPLIC = 'TI_GEST_ELENCO_NOSTD')  "
		+ "       when e.fl_elenco_standard = '1' " + "       and e.fl_elenco_fisc = '0' "
		+ "       then (select ds_valore_param_applic from APL_V_GETVAL_PARAM_BY_strut val where val.id_strut = strut.id_strut and NM_PARAM_APPLIC = 'TI_GEST_ELENCO_STD_NOFISC')  "
		+ "       else (select ds_valore_param_applic from APL_V_GETVAL_PARAM_BY_strut val where val.id_strut = strut.id_strut and NM_PARAM_APPLIC = 'TI_GEST_ELENCO_STD_FISC') "
		+ "       end " + " end " + " end ti_gest_elenco, " + "       CASE "
		+ "           WHEN    (    e.ti_stato_elenco IN "
		+ "                            ('FIRMA_IN_CORSO', "
		+ "                             'VALIDATO', "
		+ "                             'FIRME_VERIFICATE_DT_VERS', "
		+ "                             'IN_CODA_INDICE_AIP', "
		+ "                             'INDICI_AIP_GENERATI', "
		+ "                             'ELENCO_INDICI_AIP_CREATO', "
		+ "                             'ELENCO_INDICI_AIP_FIRMA_IN_CORSO', "
		+ "                             'ELENCO_INDICI_AIP_FIRMATO', "
		+ "                             'ELENCO_INDICI_AIP_ERR_MARCA', "
		+ "                             'COMPLETATO') "
		+ "                    AND e.ti_valid_elenco = 'FIRMA') "
		+ "                OR crit.ti_valid_elenco = 'FIRMA' " + "           THEN "
		+ "               '1' " + "           ELSE " + "               '0' "
		+ "       END            fl_elenco_firmato, " + "       amb.id_ambiente, "
		+ "       amb.nm_ambiente, " + "       ente.id_ente, " + "       ente.nm_ente, "
		+ "       strut.id_strut, " + "       strut.nm_strut "
		+ "  FROM elv_elenco_vers  e " + "       JOIN " + "       ( select * from  ( "
		+ "                            SELECT a.id_elenco_Vers "
		+ "                            from aro_unita_doc a  "
		+ "                            WHERE a.ID_STRUT = :idStrut and a.id_elenco_vers is not null "
		+ unitaDocFilter + "  " + "                            union " + "  "
		+ "                            SELECT  b.id_elenco_Vers "
		+ "                            from aro_unita_doc a join aro_doc b on a.ID_UNITA_DOC = b.ID_UNITA_DOC and b.ID_STRUT= :idStrut "
		+ "                            WHERE a.ID_STRUT = :idStrut and a.id_elenco_vers is not null and b.ti_doc IN ('ALLEGATO', 'ANNESSO','ANNOTAZIONE') and b.ID_ELENCO_VERS is not null "
		+ unitaDocFilter + "  " + "                            union " + "  "
		+ "                            SELECT  c.id_elenco_Vers "
		+ "                            from aro_unita_doc a join aro_upd_unita_doc c on a.ID_UNITA_DOC = c.ID_UNITA_DOC and c.ID_STRUT = :idStrut "
		+ "                            WHERE a.ID_STRUT = :idStrut and a.id_elenco_vers is not null and c.ID_ELENCO_VERS is not null "
		+ unitaDocFilter + " ) " + "                   )  tab2 "
		+ "           ON e.id_elenco_Vers = tab2.id_elenco_Vers "
		+ "       LEFT JOIN ELV_ELENCO_VERS_DA_ELAB daelab "
		+ "           ON daelab.ID_ELENCO_VERS = tab2.id_elenco_Vers "
		+ "       JOIN DEC_CRITERIO_RAGGR crit "
		+ "           ON (crit.id_criterio_raggr = e.id_criterio_raggr) "
		+ "       JOIN ORG_STRUT strut ON (strut.id_strut = e.id_strut) "
		+ "       JOIN ORG_ENTE ente ON (ente.id_ente = strut.id_ente) "
		+ "       JOIN ORG_AMBIENTE amb ON (amb.id_ambiente = ente.id_ambiente) "
		+ "       JOIN IAM_ABIL_ORGANIZ abil_org  ON (abil_org.id_organiz_applic = :idStrut  AND abil_org.id_user_iam = :idUserIam) ";
    }

    private String getTiGestElencoCase() {
	return " CASE " + "            WHEN e.ti_gest_elenco IS NOT NULL THEN "
		+ "                e.ti_gest_elenco " + "            ELSE  "
		+ "                CASE  "
		+ "                    WHEN crit.ti_gest_elenco_criterio IS NOT NULL THEN  "
		+ "                            crit.ti_gest_elenco_criterio  "
		+ "                    ELSE  " + "                        CASE  "
		+ "                                    WHEN e.fl_elenco_standard = '0' THEN  "
		+ "                                        (  "
		+ "                                            SELECT  "
		+ "                                                ds_valore_param_applic  "
		+ "                                            FROM  "
		+ "                                                apl_v_getval_param_by_strut val  "
		+ "                                            WHERE  "
		+ "                                                    val.id_strut = strut.id_strut  "
		+ "                                                AND nm_param_applic = 'TI_GEST_ELENCO_NOSTD'  "
		+ "                                        )  "
		+ "                                    WHEN e.fl_elenco_standard = '1'  "
		+ "                                         AND e.fl_elenco_fisc = '0' THEN  "
		+ "                                        (  "
		+ "                                            SELECT  "
		+ "                                                ds_valore_param_applic  "
		+ "                                            FROM  "
		+ "                                                apl_v_getval_param_by_strut val  "
		+ "                                            WHERE  "
		+ "                                                    val.id_strut = strut.id_strut  "
		+ "                                                AND nm_param_applic = 'TI_GEST_ELENCO_STD_NOFISC'  "
		+ "                                        )  "
		+ "                                    ELSE  "
		+ "                                        (  "
		+ "                                            SELECT  "
		+ "                                                ds_valore_param_applic  "
		+ "                                            FROM  "
		+ "                                                apl_v_getval_param_by_strut val  "
		+ "                                            WHERE  "
		+ "                                                    val.id_strut = strut.id_strut  "
		+ "                                                AND nm_param_applic = 'TI_GEST_ELENCO_STD_FISC'  "
		+ "                                        )  " + "                        END  "
		+ "                END  " + "        END";
    }

    private String getFlElencoFirmatoCase() {
	return "CASE "
		+ "            WHEN ( e.ti_stato_elenco IN ( 'FIRMA_IN_CORSO', 'VALIDATO', 'FIRME_VERIFICATE_DT_VERS', 'IN_CODA_INDICE_AIP', 'INDICI_AIP_GENERATI' "
		+ "            , "
		+ "                                          'ELENCO_INDICI_AIP_CREATO', 'ELENCO_INDICI_AIP_FIRMA_IN_CORSO', 'ELENCO_INDICI_AIP_FIRMATO' "
		+ "                                          , 'ELENCO_INDICI_AIP_ERR_MARCA', 'COMPLETATO' ) "
		+ "                   AND e.ti_valid_elenco = 'FIRMA' ) "
		+ "                 OR crit.ti_valid_elenco = 'FIRMA' THEN "
		+ "                '1' " + "            ELSE " + "                '0' "
		+ "        END";
    }

    public List<ElvVRicElencoVers> retrieveElvVRicElencoVersList(long idUserIam,
	    FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
	return retrieveElvVRicElencoVersList(idUserIam, new Filtri(filtriElenchiVersamento));
    }

    public List<ElvVRicElencoVers> retrieveElvVRicElencoVersList(long idUserIam, Filtri filtri) {
	Query query = createElvVRicElencoVersQuery(
		"SELECT u FROM ElvVRicElencoVers u WHERE u.idUserIam = :idUserIam ", filtri);
	setElvVRicElencoCommonParameters(query, idUserIam, filtri);
	List<ElvVRicElencoVers> listaElenchiVersamento = query.getResultList();
	return listaElenchiVersamento;
    }

    public List<ElvVRicElencoVersByStato> retrieveElvVRicElencoVersByStatoList(long idUserIam,
	    FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
	return retrieveElvVRicElencoVersByStatoList(idUserIam, new Filtri(filtriElenchiVersamento));
    }

    public List<ElvVRicElencoVersByStato> retrieveElvVRicElencoVersByStatoList(long idUserIam,
	    Filtri filtri) {
	Query query = createElvVRicElencoVersQuery(
		"SELECT u FROM ElvVRicElencoVersByStato u WHERE u.idUserIam = :idUserIam ", filtri);

	setElvVRicElencoCommonParameters(query, idUserIam, filtri);

	BigDecimal hhStatoElencoInCodaJms = filtri.getHhStatoElencoInCodaJms();
	if (hhStatoElencoInCodaJms != null) {
	    query.setParameter("hhStatoElencoInCodaJms", hhStatoElencoInCodaJms);
	}

	return query.getResultList();
    }

    public List<ElvVRicElencoVersByUd> retrieveElvVRicElencoVersByUdList(long idUserIam,
	    FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
	if (filtriElenchiVersamento.getTi_stato_conservazione().parse().isEmpty()) {
	    return convertToElvVRicElencoVersByUd(retrieveElvVRicElencoVersByUdList(idUserIam,
		    new Filtri(filtriElenchiVersamento)));
	} else {
	    return retrieveElvVRicElencoVersByStatoConservazioneUdList(idUserIam,
		    new Filtri(filtriElenchiVersamento));
	}
    }

    private List<ElvVRicElencoVersByUd> retrieveElvVRicElencoVersByStatoConservazioneUdList(
	    long idUserIam, Filtri filtri) {
	Query query = createElvVRicElencoVersQuery(
		"SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoVersByUd "
			+ "(u.id.idElencoVers, u.nmElenco, u.dsElenco, u.tiStatoElenco, u.tiGestElenco, u.niCompAggElenco, u.niCompVersElenco, "
			+ "u.niSizeVersElenco, u.niSizeAggElenco, u.dtCreazioneElenco, u.dtChius, u.dtFirmaIndice, "
			+ "u.idCriterioRaggr, u.nmCriterioRaggr, u.nmAmbiente, u.nmEnte, u.nmStrut, "
			+ "u.flElencoFisc, u.flElencoStandard, u.flElencoFirmato, u.niIndiciAip, "
			+ "u.dtCreazioneElencoIxAip, u.dtFirmaElencoIxAip, u.tsStatoElencoInCodaJms) "
			+ "FROM ElvVRicElencoVersByUd u WHERE u.idUserIam = :idUserIam ",
		filtri);
	setElvVRicElencoCommonParameters(query, idUserIam, filtri);

	String cdRegistroKeyUnitaDoc = filtri.getCdRegistroKeyUnitaDoc();
	BigDecimal aaKeyUnitaDoc = filtri.getAaKeyUnitaDoc();
	String cdKeyUnitaDoc = filtri.getCdKeyUnitaDoc();
	BigDecimal aaKeyUnitaDocDa = filtri.getAaKeyUnitaDocDa();
	BigDecimal aaKeyUnitaDocA = filtri.getAaKeyUnitaDocA();
	String cdKeyUnitaDocDa = filtri.getCdKeyUnitaDocDa();
	String cdKeyUnitaDocA = filtri.getCdKeyUnitaDocA();
	if (StringUtils.isNotBlank(cdRegistroKeyUnitaDoc)) {
	    query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
	}
	if (aaKeyUnitaDoc != null) {
	    query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
	}
	if (cdKeyUnitaDoc != null) {
	    query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
	}
	if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
	    query.setParameter("aaKeyUnitaDocDa", aaKeyUnitaDocDa);
	    query.setParameter("aaKeyUnitaDocA", aaKeyUnitaDocA);
	}
	if (cdKeyUnitaDocDa != null && cdKeyUnitaDocA != null) {
	    cdKeyUnitaDocDa = StringPadding.padString(cdKeyUnitaDocDa, "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyUnitaDocA = StringPadding.padString(cdKeyUnitaDocA, "0", 12,
		    StringPadding.PADDING_LEFT);
	    query.setParameter("cdKeyUnitaDocDa", cdKeyUnitaDocDa);
	    query.setParameter("cdKeyUnitaDocA", cdKeyUnitaDocA);
	}

	return query.getResultList();
    }

    private List<ElvVRicElencoVersByUd> convertToElvVRicElencoVersByUd(List<Object[]> sourceList) {
	List<ElvVRicElencoVersByUd> listaRisultato = new ArrayList<>();
	for (Object[] source : sourceList) {

	    ElvVRicElencoVersByUd target = new ElvVRicElencoVersByUd();

	    // Create a new ElvVRicElencoVersByUdId
	    ElvVRicElencoVersByUdId id = new ElvVRicElencoVersByUdId();

	    // Set the ID
	    id.setIdElencoVers((BigDecimal) source[0]);

	    // Set the ID into the target class
	    target.setElvVRicElencoVersByUdId(id);

	    target.setNmElenco((String) source[1]);
	    target.setDsElenco((String) source[2]);
	    target.setTiStatoElenco((String) source[3]);
	    target.setDtCreazioneElenco((Date) source[4]);
	    target.setDtChius((Date) source[5]);
	    target.setDtFirmaIndice((Date) source[6]);
	    target.setNiCompVersElenco((BigDecimal) source[7]);
	    target.setNiCompAggElenco((BigDecimal) source[8]);
	    target.setNiSizeVersElenco((BigDecimal) source[9]);
	    target.setNiSizeAggElenco((BigDecimal) source[10]);
	    target.setNtIndiceElenco((String) source[11]);
	    target.setNtElencoChiuso((String) source[12]);
	    target.setIdCriterioRaggr((BigDecimal) source[13]);
	    target.setDtCreazioneElencoIxAip((Date) source[14]);
	    target.setDtFirmaElencoIxAip((Date) source[15]);
	    target.setFlElencoFisc(((Character) source[16]).toString());
	    target.setFlElencoStandard(((Character) source[17]).toString());
	    target.setNiIndiciAip((BigDecimal) source[18]);
	    target.setTiValidElenco((String) source[19]);
	    target.setNmCriterioRaggr((String) source[20]);
	    target.setTsStatoElencoInCodaJms((Date) source[21]);
	    target.setIdStrutUniDoc((BigDecimal) source[22]);
	    target.setTiGestElenco((String) source[23]);
	    target.setFlElencoFirmato(((Character) source[24]).toString());
	    target.setIdAmbiente((BigDecimal) source[25]);
	    target.setNmAmbiente((String) source[26]);
	    target.setIdEnte((BigDecimal) source[27]);
	    target.setNmEnte((String) source[28]);
	    target.setIdStrut((BigDecimal) source[29]);
	    target.setNmStrut((String) source[30]);
	    listaRisultato.add(target);
	}
	return listaRisultato;
    }

    public List<Object[]> retrieveElvVRicElencoVersByUdList(long idUserIam, Filtri filtri) {
	String queryBase = getQueryRicElenchiVersByUd(createFiltriUdCondition(filtri));
	Query query = createElvVRicElencoVersByUdNativeQuery(queryBase, filtri);

	setElvVpRicElencoVersByUdParameters(query, idUserIam, filtri);

	String cdRegistroKeyUnitaDoc = filtri.getCdRegistroKeyUnitaDoc();
	BigDecimal aaKeyUnitaDoc = filtri.getAaKeyUnitaDoc();
	String cdKeyUnitaDoc = filtri.getCdKeyUnitaDoc();
	BigDecimal aaKeyUnitaDocDa = filtri.getAaKeyUnitaDocDa();
	BigDecimal aaKeyUnitaDocA = filtri.getAaKeyUnitaDocA();
	String cdKeyUnitaDocDa = filtri.getCdKeyUnitaDocDa();
	String cdKeyUnitaDocA = filtri.getCdKeyUnitaDocA();
	if (StringUtils.isNotBlank(cdRegistroKeyUnitaDoc)) {
	    query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
	}
	if (aaKeyUnitaDoc != null) {
	    query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
	}
	if (cdKeyUnitaDoc != null) {
	    query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
	}
	if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
	    query.setParameter("aaKeyUnitaDocDa", aaKeyUnitaDocDa);
	    query.setParameter("aaKeyUnitaDocA", aaKeyUnitaDocA);
	}
	if (cdKeyUnitaDocDa != null && cdKeyUnitaDocA != null) {
	    cdKeyUnitaDocDa = StringPadding.padString(cdKeyUnitaDocDa, "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyUnitaDocA = StringPadding.padString(cdKeyUnitaDocA, "0", 12,
		    StringPadding.PADDING_LEFT);
	    query.setParameter("cdKeyUnitaDocDa", cdKeyUnitaDocDa);
	    query.setParameter("cdKeyUnitaDocA", cdKeyUnitaDocA);
	}

	/* ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "ELENCHI DI VERSAMENTO" */
	return (List<Object[]>) query.getResultList();
    }

    private String createFiltriUdCondition(Filtri filtri) {
	StringBuilder queryStr = new StringBuilder();
	String whereWord = " AND ";

	/* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA */
	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    queryStr.append(whereWord)
		    .append("a.cd_Registro_Key_Unita_Doc = :cdRegistroKeyUnitaDoc ");
	}

	if (filtri.getAaKeyUnitaDoc() != null) {
	    queryStr.append(whereWord).append("a.aa_Key_Unita_Doc = :aaKeyUnitaDoc ");
	}

	if (filtri.getCdKeyUnitaDoc() != null) {
	    queryStr.append(whereWord).append("a.cd_Key_Unita_Doc = :cdKeyUnitaDoc ");
	}

	/* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA per range */
	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    queryStr.append(whereWord)
		    .append("a.aa_Key_Unita_Doc BETWEEN :aaKeyUnitaDocDa AND :aaKeyUnitaDocA ");
	}

	if (filtri.getCdKeyUnitaDocDa() != null && filtri.getCdKeyUnitaDocA() != null) {
	    queryStr.append(whereWord).append(
		    "LPAD( a.cd_Key_Unita_Doc, 12, '0') BETWEEN :cdKeyUnitaDocDa AND :cdKeyUnitaDocA ");
	}

	return queryStr.toString();
    }

    private void setElvVRicElencoCommonParameters(Query query, long idUserIam, Filtri filtri) {
	/* Recupero i campi da assegnare come parametri alla query */
	Date dtCreazioneElencoDa = null;
	Date dtCreazioneElencoA = null;
	if (filtri.getCreazioneElencoDa() != null) {
	    dtCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
	    if (filtri.getCreazioneElencoA() != null) {
		dtCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    } else {
		dtCreazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    }
	}
	Date dtValidazioneElencoDa = null;
	Date dtValidazioneElencoA = null;
	if (filtri.getValidazioneElencoDa() != null) {
	    dtValidazioneElencoDa = new Date(filtri.getValidazioneElencoDa().getTime());
	    if (filtri.getValidazioneElencoA() != null) {
		dtValidazioneElencoA = new Date(filtri.getValidazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    } else {
		dtValidazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    }
	}

	/* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
	Date dtCreazioneElencoIxAipDa = null;
	Date dtCreazioneElencoIxAipA = null;
	if (filtri.getCreazioneElencoIxAipDa() != null) {
	    dtCreazioneElencoIxAipDa = new Date(filtri.getCreazioneElencoIxAipDa().getTime());
	    if (filtri.getCreazioneElencoIxAipA() != null) {
		dtCreazioneElencoIxAipA = new Date(filtri.getCreazioneElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    } else {
		dtCreazioneElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    }
	}
	/* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
	Date dtFirmaElencoIxAipDa = null;
	Date dtFirmaElencoIxAipA = null;
	if (filtri.getFirmaElencoIxAipDa() != null) {
	    dtFirmaElencoIxAipDa = new Date(filtri.getFirmaElencoIxAipDa().getTime());
	    if (filtri.getFirmaElencoIxAipA() != null) {
		dtFirmaElencoIxAipA = new Date(filtri.getFirmaElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    } else {
		dtFirmaElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    }
	}

	/* Passaggio dei valori dei parametri di ricerca */
	query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
	BigDecimal idAmbiente = filtri.getIdAmbiente();
	if (idAmbiente != null) {
	    query.setParameter("idAmbiente", idAmbiente);
	}

	BigDecimal idEnte = filtri.getIdEnte();
	if (idEnte != null) {
	    query.setParameter("idEnte", idEnte);
	}
	BigDecimal idStrut = filtri.getIdStrut();
	if (idStrut != null) {
	    query.setParameter("idStrut", idStrut);
	}

	if (filtri.getIdElencoVers() != null) {
	    query.setParameter("idElencoVers", filtri.getIdElencoVers());
	}

	if (filtri.getNmElenco() != null) {
	    query.setParameter("nmElenco", "%" + filtri.getNmElenco().toUpperCase() + "%");
	}

	if (filtri.getDsElenco() != null) {
	    query.setParameter("dsElenco", "%" + filtri.getDsElenco().toUpperCase() + "%");
	}

	if (!filtri.getTiStatoElenco().isEmpty()) {
	    if (filtri.getTiStatoElenco().contains("IN_CODA_JMS_GENERA_INDICE_AIP")) {
		filtri.getTiStatoElenco()
			.add("IN_CODA_JMS_GENERA_INDICE_AIP (IN_CODA_JMS_GENERA_INDICE_AIP)");
	    }
	    if (filtri.getTiStatoElenco().contains("IN_CODA_JMS_VERIFICA_FIRME_DT_VERS")) {
		filtri.getTiStatoElenco().add(
			"IN_CODA_JMS_VERIFICA_FIRME_DT_VERS (IN_CODA_JMS_VERIFICA_FIRME_DT_VERS)");
	    }
	    query.setParameter("tiStatoElenco", filtri.getTiStatoElenco());
	}

	if (filtri.getTiValidElenco() != null) {
	    query.setParameter("tiValidElenco", filtri.getTiValidElenco());
	}

	if (filtri.getTiModValidElenco() != null) {
	    query.setParameter("tiModValidElenco", filtri.getTiModValidElenco());
	}

	if (filtri.getTiGestElenco() != null) {
	    query.setParameter("tiGestElenco", filtri.getTiGestElenco());
	}

	if (dtCreazioneElencoDa != null && dtCreazioneElencoA != null) {
	    query.setParameter("dtCreazioneElencoDa", dtCreazioneElencoDa, TemporalType.DATE);
	    query.setParameter("dtCreazioneElencoA", dtCreazioneElencoA, TemporalType.DATE);
	}

	if (dtValidazioneElencoDa != null && dtValidazioneElencoA != null) {
	    query.setParameter("dtValidazioneElencoDa", dtValidazioneElencoDa, TemporalType.DATE);
	    query.setParameter("dtValidazioneElencoA", dtValidazioneElencoA, TemporalType.DATE);
	}

	if (filtri.getNtElencoChiuso() != null) {
	    query.setParameter("ntElencoChiuso",
		    "%" + filtri.getNtElencoChiuso().toUpperCase() + "%");
	}

	if (filtri.getNtIndiceElenco() != null) {
	    query.setParameter("ntIndiceElenco",
		    "%" + filtri.getNtIndiceElenco().toUpperCase() + "%");
	}
	if (filtri.getNmCriterioRaggr() != null) {
	    query.setParameter("nmCriterioRaggr", filtri.getNmCriterioRaggr());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFisc())) {
	    query.setParameter("flElencoFisc", filtri.getFlElencoFisc());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
	    query.setParameter("flElencoStandard", filtri.getFlElencoStandard());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFirmato())) {
	    query.setParameter("flElencoFirmato", filtri.getFlElencoFirmato());
	}
	if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
	    query.setParameter("dtCreazioneElencoIxAipDa", dtCreazioneElencoIxAipDa,
		    TemporalType.DATE);
	    query.setParameter("dtCreazioneElencoIxAipA", dtCreazioneElencoIxAipA,
		    TemporalType.DATE);
	}
	if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
	    query.setParameter("dtFirmaElencoIxAipDa", dtFirmaElencoIxAipDa, TemporalType.DATE);
	    query.setParameter("dtFirmaElencoIxAipA", dtFirmaElencoIxAipA, TemporalType.DATE);
	}

	if (!filtri.getTiStatoConservazione().isEmpty()) {
	    query.setParameter("tiStatoConservazione", filtri.getTiStatoConservazione());
	}
    }

    private void setElvVpRicElencoVersByUdParameters(Query query, long idUserIam, Filtri filtri) {
	/* Recupero i campi da assegnare come parametri alla query */
	Date dtCreazioneElencoDa = null;
	Date dtCreazioneElencoA = null;
	if (filtri.getCreazioneElencoDa() != null) {
	    dtCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
	    if (filtri.getCreazioneElencoA() != null) {
		dtCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    } else {
		dtCreazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    }
	}
	Date dtValidazioneElencoDa = null;
	Date dtValidazioneElencoA = null;
	if (filtri.getValidazioneElencoDa() != null) {
	    dtValidazioneElencoDa = new Date(filtri.getValidazioneElencoDa().getTime());
	    if (filtri.getValidazioneElencoA() != null) {
		dtValidazioneElencoA = new Date(filtri.getValidazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    } else {
		dtValidazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    }
	}

	/* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
	Date dtCreazioneElencoIxAipDa = null;
	Date dtCreazioneElencoIxAipA = null;
	if (filtri.getCreazioneElencoIxAipDa() != null) {
	    dtCreazioneElencoIxAipDa = new Date(filtri.getCreazioneElencoIxAipDa().getTime());
	    if (filtri.getCreazioneElencoIxAipA() != null) {
		dtCreazioneElencoIxAipA = new Date(filtri.getCreazioneElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    } else {
		dtCreazioneElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    }
	}
	/* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
	Date dtFirmaElencoIxAipDa = null;
	Date dtFirmaElencoIxAipA = null;
	if (filtri.getFirmaElencoIxAipDa() != null) {
	    dtFirmaElencoIxAipDa = new Date(filtri.getFirmaElencoIxAipDa().getTime());
	    if (filtri.getFirmaElencoIxAipA() != null) {
		dtFirmaElencoIxAipA = new Date(filtri.getFirmaElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    } else {
		dtFirmaElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    }
	}

	/* Passaggio dei valori dei parametri di ricerca */
	query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
	query.setParameter("idStrut", filtri.getIdStrut());

	if (filtri.getIdElencoVers() != null) {
	    query.setParameter("idElencoVers", filtri.getIdElencoVers());
	}

	if (filtri.getNmElenco() != null) {
	    query.setParameter("nmElenco", "%" + filtri.getNmElenco().toUpperCase() + "%");
	}

	if (filtri.getDsElenco() != null) {
	    query.setParameter("dsElenco", "%" + filtri.getDsElenco().toUpperCase() + "%");
	}

	if (!filtri.getTiStatoElenco().isEmpty()) {
	    if (filtri.getTiStatoElenco().contains("IN_CODA_JMS_GENERA_INDICE_AIP")) {
		filtri.getTiStatoElenco()
			.add("IN_CODA_JMS_GENERA_INDICE_AIP (IN_CODA_JMS_GENERA_INDICE_AIP)");
	    }
	    if (filtri.getTiStatoElenco().contains("IN_CODA_JMS_VERIFICA_FIRME_DT_VERS")) {
		filtri.getTiStatoElenco().add(
			"IN_CODA_JMS_VERIFICA_FIRME_DT_VERS (IN_CODA_JMS_VERIFICA_FIRME_DT_VERS)");
	    }
	    query.setParameter("tiStatoElenco", filtri.getTiStatoElenco());
	}

	if (filtri.getTiValidElenco() != null) {
	    query.setParameter("tiValidElenco", filtri.getTiValidElenco());
	}

	if (filtri.getTiModValidElenco() != null) {
	    query.setParameter("tiModValidElenco", filtri.getTiModValidElenco());
	}

	if (filtri.getTiGestElenco() != null) {
	    query.setParameter("tiGestElenco", filtri.getTiGestElenco());
	}

	if (dtCreazioneElencoDa != null && dtCreazioneElencoA != null) {
	    query.setParameter("dtCreazioneElencoDa", dtCreazioneElencoDa, TemporalType.DATE);
	    query.setParameter("dtCreazioneElencoA", dtCreazioneElencoA, TemporalType.DATE);
	}

	if (dtValidazioneElencoDa != null && dtValidazioneElencoA != null) {
	    query.setParameter("dtValidazioneElencoDa", dtValidazioneElencoDa, TemporalType.DATE);
	    query.setParameter("dtValidazioneElencoA", dtValidazioneElencoA, TemporalType.DATE);
	}

	if (filtri.getNtElencoChiuso() != null) {
	    query.setParameter("ntElencoChiuso",
		    "%" + filtri.getNtElencoChiuso().toUpperCase() + "%");
	}

	if (filtri.getNtIndiceElenco() != null) {
	    query.setParameter("ntIndiceElenco",
		    "%" + filtri.getNtIndiceElenco().toUpperCase() + "%");
	}
	if (filtri.getNmCriterioRaggr() != null) {
	    query.setParameter("nmCriterioRaggr", filtri.getNmCriterioRaggr());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFisc())) {
	    query.setParameter("flElencoFisc", filtri.getFlElencoFisc());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
	    query.setParameter("flElencoStandard", filtri.getFlElencoStandard());
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFirmato())) {
	    query.setParameter("flElencoFirmato", filtri.getFlElencoFirmato());
	}
	if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
	    query.setParameter("dtCreazioneElencoIxAipDa", dtCreazioneElencoIxAipDa,
		    TemporalType.DATE);
	    query.setParameter("dtCreazioneElencoIxAipA", dtCreazioneElencoIxAipA,
		    TemporalType.DATE);
	}
	if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
	    query.setParameter("dtFirmaElencoIxAipDa", dtFirmaElencoIxAipDa, TemporalType.DATE);
	    query.setParameter("dtFirmaElencoIxAipA", dtFirmaElencoIxAipA, TemporalType.DATE);
	}
    }

    private Query createElvVRicElencoVersQuery(String selectQuery, Filtri filtri) {
	String whereWord = "AND ";
	StringBuilder queryStr = new StringBuilder(selectQuery);
	/* Inserimento nella query del filtro ID_AMBIENTE */
	BigDecimal idAmbiente = filtri.getIdAmbiente();
	if (idAmbiente != null) {
	    queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
	}

	/* Inserimento nella query del filtro ID_ENTE */
	BigDecimal idEnte = filtri.getIdEnte();
	if (idEnte != null) {
	    queryStr.append(whereWord).append("u.idEnte = :idEnte ");
	}

	/* Inserimento nella query del filtro ID_STRUT */
	BigDecimal idStrut = filtri.getIdStrut();
	if (idStrut != null) {
	    queryStr.append(whereWord).append("u.idStrut = :idStrut ");
	}

	/* Inserimento nella query del filtro ID ELENCO VERS */
	if (filtri.getIdElencoVers() != null) {
	    queryStr.append(whereWord).append("u.id.idElencoVers = :idElencoVers ");
	}

	/* Inserimento nella query del filtro NM_ELENCO */
	if (filtri.getNmElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(u.nmElenco) LIKE :nmElenco ");
	}

	/* Inserimento nella query del filtro DS_ELENCO */
	if (filtri.getDsElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(u.dsElenco) LIKE :dsElenco ");
	}

	/* Inserimento nella query del filtro TI_STATO_ELENCO */
	if (!filtri.getTiStatoElenco().isEmpty()) {
	    queryStr.append(whereWord).append("u.tiStatoElenco IN (:tiStatoElenco) ");
	}

	/* Inserimento nella query del filtro TI_VALID_ELENCO */
	if (filtri.getTiValidElenco() != null) {
	    queryStr.append(whereWord).append("u.tiValidElenco = :tiValidElenco ");
	}

	/* Inserimento nella query del filtro TI_MOD_VALID_ELENCO */
	if (filtri.getTiModValidElenco() != null) {
	    queryStr.append(whereWord).append("u.tiModValidElenco = :tiModValidElenco ");
	}

	/* Inserimento nella query del filtro TI_GEST_ELENCO */
	if (filtri.getTiGestElenco() != null) {
	    queryStr.append(whereWord).append("u.tiGestElenco = :tiGestElenco ");
	}

	/* Inserimento nella query del filtro TI_STATO_CONSERVAZIONE */
	if (!filtri.getTiStatoConservazione().isEmpty()) {
	    queryStr.append(whereWord)
		    .append("u.aroUnitaDoc.tiStatoConservazione IN :tiStatoConservazione ");
	}

	/* Inserimento nella query del filtro DATA CREAZIONE DA - A */
	Date dtCreazioneElencoDa = null;
	Date dtCreazioneElencoA = null;
	if (filtri.getCreazioneElencoDa() != null) {
	    dtCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
	    if (filtri.getCreazioneElencoA() != null) {
		dtCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    } else {
		dtCreazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    }
	}

	if ((dtCreazioneElencoDa != null) && (dtCreazioneElencoA != null)) {
	    queryStr.append(whereWord).append(
		    "(u.dtCreazioneElenco between :dtCreazioneElencoDa AND :dtCreazioneElencoA) ");
	}

	/* Inserimento nella query del filtro DATA VALIDAZIONE DA - A */
	Date dtValidazioneElencoDa = null;
	Date dtValidazioneElencoA = null;
	if (filtri.getValidazioneElencoDa() != null) {
	    dtValidazioneElencoDa = new Date(filtri.getValidazioneElencoDa().getTime());
	    if (filtri.getValidazioneElencoA() != null) {
		dtValidazioneElencoA = new Date(filtri.getValidazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    } else {
		dtValidazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    }
	}

	if ((dtValidazioneElencoDa != null) && (dtValidazioneElencoA != null)) {
	    queryStr.append(whereWord).append(
		    "(u.dtFirmaIndice between :dtValidazioneElencoDa AND :dtValidazioneElencoA) ");
	}

	/* Inserimento nella query del filtro NT_ELENCO_CHIUSO */
	if (filtri.getNtElencoChiuso() != null) {
	    queryStr.append(whereWord).append("UPPER(u.ntElencoChiuso) LIKE :ntElencoChiuso ");
	}

	/* Inserimento nella query del filtro NT_INDICE_ELENCO */
	if (filtri.getNtIndiceElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(u.ntIndiceElenco) LIKE :ntIndiceElenco ");
	}

	/* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA */

	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
	    // /* Se ho inserito il registro, allora devo aggiungere anche il parametro
	    // id_strut_uni_doc */
	}

	if (filtri.getAaKeyUnitaDoc() != null) {
	    queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
	}

	if (filtri.getCdKeyUnitaDoc() != null) {
	    queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
	}

	/* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA per range */

	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    queryStr.append(whereWord)
		    .append("u.aaKeyUnitaDoc BETWEEN :aaKeyUnitaDocDa AND :aaKeyUnitaDocA ");
	}

	if (filtri.getCdKeyUnitaDocDa() != null && filtri.getCdKeyUnitaDocA() != null) {
	    queryStr.append(whereWord).append(
		    "LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :cdKeyUnitaDocDa AND :cdKeyUnitaDocA ");
	}

	/* Inserimento nella query del filtro NM_CRITERIO_RAGGR */
	if (filtri.getNmCriterioRaggr() != null) {
	    queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
	}

	if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
	    queryStr.append(whereWord).append("u.flElencoStandard = :flElencoStandard ");
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFisc())) {
	    queryStr.append(whereWord).append("u.flElencoFisc = :flElencoFisc ");
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFirmato())) {
	    queryStr.append(whereWord).append("u.flElencoFirmato = :flElencoFirmato ");
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoIndiciAipCreato())) {
	    if (filtri.getFlElencoIndiciAipCreato().equals("1")) {
		queryStr.append(whereWord).append("u.dtCreazioneElencoIxAip IS NOT NULL ");
	    } else {
		queryStr.append(whereWord).append("u.dtCreazioneElencoIxAip IS NULL ");
	    }
	}

	/* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
	Date dtCreazioneElencoIxAipDa = null;
	Date dtCreazioneElencoIxAipA = null;
	if (filtri.getCreazioneElencoIxAipDa() != null) {
	    dtCreazioneElencoIxAipDa = new Date(filtri.getCreazioneElencoIxAipDa().getTime());
	    if (filtri.getCreazioneElencoIxAipA() != null) {
		dtCreazioneElencoIxAipA = new Date(filtri.getCreazioneElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    } else {
		dtCreazioneElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    }
	}

	if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
	    queryStr.append(whereWord).append(
		    "(u.dtCreazioneElencoIxAip between :dtCreazioneElencoIxAipDa AND :dtCreazioneElencoIxAipA) ");
	}

	/* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
	Date dtFirmaElencoIxAipDa = null;
	Date dtFirmaElencoIxAipA = null;
	if (filtri.getFirmaElencoIxAipDa() != null) {
	    dtFirmaElencoIxAipDa = new Date(filtri.getFirmaElencoIxAipDa().getTime());
	    if (filtri.getFirmaElencoIxAipA() != null) {
		dtFirmaElencoIxAipA = new Date(filtri.getFirmaElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    } else {
		dtFirmaElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    }
	}

	if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
	    queryStr.append(whereWord).append(
		    "(u.dtFirmaElencoIxAip between :dtFirmaElencoIxAipDa AND :dtFirmaElencoIxAipA) ");
	}

	/* Inserimento nella query del filtro HH_STATO_ELENCO_IN_CODA_JMS */
	if (filtri.getHhStatoElencoInCodaJms() != null) {
	    queryStr.append(whereWord)
		    .append("u.hhStatoElencoInCodaJms >= :hhStatoElencoInCodaJms ");
	}

	/* Ordina per data creazione ascendente */
	queryStr.append(" ORDER BY u.dtCreazioneElenco ASC ");
	/* CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER */
	return getEntityManager().createQuery(queryStr.toString());
    }

    private Query createElvVRicElencoVersByUdNativeQuery(String selectQuery, Filtri filtri) {
	String whereWord = "WHERE ";
	StringBuilder queryStr = new StringBuilder(selectQuery);

	/* Inserimento nella query del filtro ID ELENCO VERS */
	if (filtri.getIdElencoVers() != null) {
	    queryStr.append(whereWord).append("e.id_Elenco_Vers = :idElencoVers ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro NM_ELENCO */
	if (filtri.getNmElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(e.nm_Elenco) LIKE :nmElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro DS_ELENCO */
	if (filtri.getDsElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(e.ds_Elenco) LIKE :dsElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro TI_STATO_ELENCO */
	if (!filtri.getTiStatoElenco().isEmpty()) {
	    queryStr.append(whereWord).append("e.ti_Stato_Elenco IN (:tiStatoElenco) ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro TI_VALID_ELENCO */
	if (filtri.getTiValidElenco() != null) {
	    queryStr.append(whereWord).append("e.ti_Valid_Elenco = :tiValidElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro TI_MOD_VALID_ELENCO */
	if (filtri.getTiModValidElenco() != null) {
	    queryStr.append(whereWord).append("e.ti_Mod_Valid_Elenco = :tiModValidElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro TI_GEST_ELENCO */
	if (filtri.getTiGestElenco() != null) {
	    queryStr.append(whereWord).append(getTiGestElencoCase()).append(" = :tiGestElenco ");
	    // queryStr.append(whereWord).append("e.ti_Gest_Elenco = :tiGestElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro DATA CREAZIONE DA - A */
	Date dtCreazioneElencoDa = null;
	Date dtCreazioneElencoA = null;
	if (filtri.getCreazioneElencoDa() != null) {
	    dtCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
	    if (filtri.getCreazioneElencoA() != null) {
		dtCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    } else {
		dtCreazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoA = calendar.getTime();
	    }
	}

	if ((dtCreazioneElencoDa != null) && (dtCreazioneElencoA != null)) {
	    queryStr.append(whereWord).append(
		    "(e.dt_Creazione_Elenco between :dtCreazioneElencoDa AND :dtCreazioneElencoA) ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro DATA VALIDAZIONE DA - A */
	Date dtValidazioneElencoDa = null;
	Date dtValidazioneElencoA = null;
	if (filtri.getValidazioneElencoDa() != null) {
	    dtValidazioneElencoDa = new Date(filtri.getValidazioneElencoDa().getTime());
	    if (filtri.getValidazioneElencoA() != null) {
		dtValidazioneElencoA = new Date(filtri.getValidazioneElencoA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    } else {
		dtValidazioneElencoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtValidazioneElencoA);
		calendar.add(Calendar.DATE, 1);
		dtValidazioneElencoA = calendar.getTime();
	    }
	}

	if ((dtValidazioneElencoDa != null) && (dtValidazioneElencoA != null)) {
	    queryStr.append(whereWord).append(
		    "(e.dt_Firma_Indice between :dtValidazioneElencoDa AND :dtValidazioneElencoA) ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro NT_ELENCO_CHIUSO */
	if (filtri.getNtElencoChiuso() != null) {
	    queryStr.append(whereWord).append("UPPER(e.nt_Elenco_Chiuso) LIKE :ntElencoChiuso ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro NT_INDICE_ELENCO */
	if (filtri.getNtIndiceElenco() != null) {
	    queryStr.append(whereWord).append("UPPER(e.nt_Indice_Elenco) LIKE :ntIndiceElenco ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro NM_CRITERIO_RAGGR */
	if (filtri.getNmCriterioRaggr() != null) {
	    queryStr.append(whereWord).append("crit.nm_Criterio_Raggr = :nmCriterioRaggr ");
	    whereWord = " AND ";
	}

	if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
	    queryStr.append(whereWord).append("e.fl_Elenco_Standard = :flElencoStandard ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFisc())) {
	    queryStr.append(whereWord).append("e.fl_Elenco_Fisc = :flElencoFisc ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoFirmato())) {
	    queryStr.append(whereWord).append(getFlElencoFirmatoCase())
		    .append(" = :flElencoFirmato ");
	    // queryStr.append(whereWord).append("e.fl_Elenco_Firmato = :flElencoFirmato ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlElencoIndiciAipCreato())) {
	    if (filtri.getFlElencoIndiciAipCreato().equals("1")) {
		queryStr.append(whereWord).append("e.dt_Creazione_Elenco_Ix_Aip IS NOT NULL ");
		whereWord = " AND ";
	    } else {
		queryStr.append(whereWord).append("e.dt_Creazione_Elenco_Ix_Aip IS NULL ");
		whereWord = " AND ";
	    }
	}

	/* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
	Date dtCreazioneElencoIxAipDa = null;
	Date dtCreazioneElencoIxAipA = null;
	if (filtri.getCreazioneElencoIxAipDa() != null) {
	    dtCreazioneElencoIxAipDa = new Date(filtri.getCreazioneElencoIxAipDa().getTime());
	    if (filtri.getCreazioneElencoIxAipA() != null) {
		dtCreazioneElencoIxAipA = new Date(filtri.getCreazioneElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    } else {
		dtCreazioneElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtCreazioneElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtCreazioneElencoIxAipA = calendar.getTime();
	    }
	}

	if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
	    queryStr.append(whereWord).append(
		    "(e.dt_Creazione_Elenco_Ix_Aip between :dtCreazioneElencoIxAipDa AND :dtCreazioneElencoIxAipA) ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
	Date dtFirmaElencoIxAipDa = null;
	Date dtFirmaElencoIxAipA = null;
	if (filtri.getFirmaElencoIxAipDa() != null) {
	    dtFirmaElencoIxAipDa = new Date(filtri.getFirmaElencoIxAipDa().getTime());
	    if (filtri.getFirmaElencoIxAipA() != null) {
		dtFirmaElencoIxAipA = new Date(filtri.getFirmaElencoIxAipA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    } else {
		dtFirmaElencoIxAipA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFirmaElencoIxAipA);
		calendar.add(Calendar.DATE, 1);
		dtFirmaElencoIxAipA = calendar.getTime();
	    }
	}

	if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
	    queryStr.append(whereWord).append(
		    "(e.dt_Firma_Elenco_Ix_Aip between :dtFirmaElencoIxAipDa AND :dtFirmaElencoIxAipA) ");
	    whereWord = " AND ";
	}

	/* Inserimento nella query del filtro HH_STATO_ELENCO_IN_CODA_JMS */
	if (filtri.getHhStatoElencoInCodaJms() != null) {
	    queryStr.append(whereWord)
		    .append("daelab.hh_Stato_Elenco_In_Coda_Jms >= :hhStatoElencoInCodaJms ");
	    whereWord = " AND ";
	}

	/* Ordina per data creazione ascendente */
	queryStr.append(" ORDER BY e.dt_Creazione_Elenco ASC ");
	/* CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER */
	return getEntityManager().createNativeQuery(queryStr.toString());
    }

    public List<ElvVLisElencoVersStato> getListaElenchiDaFirmare(BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStrut, BigDecimal idElencoVers, String note,
	    String flElencoFisc, List<String> tiGestElenco, Date[] dateCreazioneElencoValidate,
	    long idUserIam, String... statiElenco) {
	String queryStr = "SELECT u FROM ElvVLisElencoVersStato u "
		+ "WHERE u.tiStatoElenco IN (:statiElenco) AND u.idUserIam = :idUserIam ";

	if (idAmbiente != null) {
	    queryStr = queryStr.concat("AND u.idAmbiente = :idAmbiente ");
	}
	if (idEnte != null) {
	    queryStr = queryStr.concat("AND u.idEnte = :idEnte ");
	}
	if (idStrut != null) {
	    queryStr = queryStr.concat("AND u.idStrut = :idStrut ");
	}
	if (idElencoVers != null) {
	    queryStr = queryStr.concat("AND u.idElencoVers = :idElencoVers ");
	}
	if (note != null) {
	    queryStr = queryStr.concat("AND u.flNoteElenco = :note ");
	}
	if (flElencoFisc != null) {
	    queryStr = queryStr.concat("AND u.flElencoFisc = :flElencoFisc ");
	}
	if (tiGestElenco != null && !tiGestElenco.isEmpty()) {
	    queryStr = queryStr.concat("AND u.tiGestElenco IN (:tiGestElenco) ");
	}

	Date data_da = (dateCreazioneElencoValidate != null ? dateCreazioneElencoValidate[0]
		: null);
	Date data_a = (dateCreazioneElencoValidate != null ? dateCreazioneElencoValidate[1] : null);

	if ((data_da != null) && (data_a != null)) {
	    if (Arrays.asList(statiElenco).contains(ElencoEnums.ElencoStatusEnum.CHIUSO.name())) {
		queryStr = queryStr.concat(
			"AND (u.dtCreazioneElenco >= :datada AND u.dtCreazioneElenco <= :dataa) ");
	    } else {
		queryStr = queryStr.concat(
			"AND (u.dtCreazioneElencoIxAip >= :datada AND u.dtCreazioneElencoIxAip <= :dataa) ");
	    }
	}

	queryStr = queryStr.concat("ORDER BY u.dtChius ASC ");

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("statiElenco", Arrays.asList(statiElenco));
	query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
	if (idAmbiente != null) {
	    query.setParameter("idAmbiente", idAmbiente);
	}
	if (idEnte != null) {
	    query.setParameter("idEnte", idEnte);
	}
	if (idStrut != null) {
	    query.setParameter("idStrut", idStrut);
	}
	if (idElencoVers != null) {
	    query.setParameter("idElencoVers", idElencoVers);
	}
	if (note != null) {
	    query.setParameter("note", note);
	}
	if (flElencoFisc != null) {
	    query.setParameter("flElencoFisc", flElencoFisc);
	}
	if (tiGestElenco != null && !tiGestElenco.isEmpty()) {
	    query.setParameter("tiGestElenco", tiGestElenco);
	}
	if (data_da != null && data_a != null) {
	    query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
	    query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
	}
	return query.getResultList();
    }

    public List<ElvVLisElencoVersStato> getListaElenchiDaFirmare(List<BigDecimal> idElencoVersList,
	    Long idUserIam) {
	List<ElvVLisElencoVersStato> listaElenchiVersamento = null;
	if (idElencoVersList != null && !idElencoVersList.isEmpty() && idUserIam != null) {
	    String queryStr = "SELECT u FROM ElvVLisElencoVersStato u WHERE u.idElencoVers IN (:idElencoVersList) AND u.idUserIam = :idUserIam";
	    Query query = getEntityManager().createQuery(queryStr);
	    query.setParameter("idElencoVersList", idElencoVersList);
	    query.setParameter("idUserIam", new BigDecimal(idUserIam));
	    listaElenchiVersamento = query.getResultList();
	}
	return listaElenchiVersamento;
    }

    /*
     * Usato dal Job SIGILLO
     */
    public List<ElvVLisElencoVersStato> getListaElenchiDaFirmare(BigDecimal idAmbiente,
	    List<String> tiStatoElenco, List<String> tiGestElenco, final int numMaxElenchiSigillo) {

	// MEV#30960
	String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVLisElencoVersStato(u.idElencoVers, u.idStrut, u.nmStrut, u.idEnte, u.nmEnte, u.idAmbiente, u.nmAmbiente) "
		+ "FROM ElvVLisElencoVersStato u " + "WHERE u.idAmbiente = :idAmbiente "
		+ "AND u.tiStatoElenco IN (:tiStatoElenco) "
		+ "AND u.tiGestElenco IN (:tiGestElenco)";
	// end MEV#30960

	Query query = getEntityManager().createQuery(queryStr);
	// MEV#29968
	query.setMaxResults(numMaxElenchiSigillo);
	// end MEV#29968
	query.setParameter("idAmbiente", idAmbiente);
	query.setParameter("tiStatoElenco", tiStatoElenco);
	query.setParameter("tiGestElenco", tiGestElenco);

	return query.getResultList();
    }

    public long countElencIndiciAipInStates(long idUserIam, List<String> elencoStates) {
	Query query = getEntityManager().createQuery(
		"SELECT COUNT(el) FROM ElvVLisElencoVersStato el WHERE el.idUserIam = :idUser AND el.tiStatoElenco IN (:states) ");
	query.setParameter("idUser", bigDecimalFromLong(idUserIam));
	query.setParameter("states", elencoStates);

	return (Long) query.getSingleResult();
    }

    public boolean isElencoDeletable(BigDecimal idElencoVers) {
	String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
		+ "AND u.tiStatoElenco IN (:statoElencoDeletable) ";
	Query query = getEntityManager().createQuery(queryStr);
	ElencoStatusEnum[] elencoEnums = ElencoStatusEnum.getStatoElencoDeletable();
	List<String> elencoString = new ArrayList<>();
	for (ElencoStatusEnum elencoEnum : elencoEnums) {
	    elencoString.add(elencoEnum.name());
	}
	query.setParameter("idElencoVers", longFromBigDecimal(idElencoVers));
	query.setParameter("statoElencoDeletable", elencoString);
	return !query.getResultList().isEmpty();
    }

    public boolean isElencoClosable(BigDecimal idElencoVers) {
	String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
		+ "AND u.tiStatoElenco = :statoElencoClosable ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idElencoVers", longFromBigDecimal(idElencoVers));
	query.setParameter("statoElencoClosable", ElencoEnums.ElencoStatusEnum.APERTO.name());
	List<ElvElencoVer> elenchi = query.getResultList();
	if (elenchi.isEmpty()) {
	    return false;
	} else {
	    return !elenchi.get(0).getAroUnitaDocs().isEmpty()
		    || !elenchi.get(0).getAroDocs().isEmpty()
		    || !elenchi.get(0).getAroUpdUnitaDocs().isEmpty();
	}
    }

    public boolean isElencoValidable(BigDecimal idElencoVers) {
	String queryStr = "SELECT u FROM ElvElencoVer u JOIN u.decCriterioRaggr crit "
		+ "WHERE u.idElencoVers = :idElencoVers "
		+ "AND u.tiStatoElenco = :statoElencoValidable "
		+ "AND ((u.tiModValidElenco IS NOT NULL AND u.tiValidElenco IS NOT NULL "
		+ " AND u.tiModValidElenco = :tiModValidElenco AND u.tiValidElenco = :tiValidElenco) "
		+ " OR ((u.tiModValidElenco IS NULL OR u.tiValidElenco IS NULL) "
		+ " AND crit.tiModValidElenco = :tiModValidElencoCriterio AND crit.tiValidElenco = :tiValidElencoCriterio)) ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idElencoVers", longFromBigDecimal(idElencoVers));
	query.setParameter("statoElencoValidable", ElencoEnums.ElencoStatusEnum.CHIUSO.name());
	query.setParameter("tiModValidElenco", TiModValidElenco.AUTOMATICA);
	query.setParameter("tiValidElenco", TiValidElenco.NO_FIRMA);
	query.setParameter("tiModValidElencoCriterio", TiModValidElencoCriterio.AUTOMATICA);
	query.setParameter("tiValidElencoCriterio", TiValidElencoCriterio.NO_FIRMA);
	return !query.getResultList().isEmpty();
    }

    public boolean areUdDocDeletables(BigDecimal idElencoVers) {
	String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
		+ "AND u.tiStatoElenco IN (:statoElencoUdDocDeletables) ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idElencoVers", longFromBigDecimal(idElencoVers));
	List<String> statoElencoUdDocDeletables = new ArrayList<>();
	statoElencoUdDocDeletables.add(ElencoEnums.ElencoStatusEnum.APERTO.name());
	statoElencoUdDocDeletables.add(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
	query.setParameter("statoElencoUdDocDeletables", statoElencoUdDocDeletables);
	List<ElvElencoVer> elenchi = query.getResultList();
	if (elenchi.isEmpty()) {
	    return false;
	} else {
	    return !elenchi.get(0).getAroUnitaDocs().isEmpty()
		    || !elenchi.get(0).getAroDocs().isEmpty();
	}
    }

    public boolean areUpdDeletables(BigDecimal idElencoVers) {
	String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
		+ "AND u.tiStatoElenco IN (:statoElencoUpdDeletables) ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idElencoVers", longFromBigDecimal(idElencoVers));
	List<String> statoElencoUpdDeletables = new ArrayList<>();
	statoElencoUpdDeletables.add(ElencoEnums.ElencoStatusEnum.APERTO.name());
	statoElencoUpdDeletables.add(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
	query.setParameter("statoElencoUpdDeletables", statoElencoUpdDeletables);
	List<ElvElencoVer> elenchi = query.getResultList();
	if (elenchi.isEmpty()) {
	    return false;
	} else {
	    return !elenchi.get(0).getAroUpdUnitaDocs().isEmpty();
	}
    }

    public boolean areAllElenchiNonPresentiFirmati(List<BigDecimal> idElencoVersSelezionatiList,
	    Date dataChiusura, BigDecimal idStrut) {
	String queryStr = "SELECT COUNT(elab) FROM ElvElencoVersDaElab elab JOIN elab.elvElencoVer u "
		+ "WHERE u.dtChius < :dataChiusura "
		+ "AND elab.elvElencoVer.idElencoVers NOT IN (:idElencoVersSelezionatiList) "
		+ "AND elab.tiStatoElenco = 'CHIUSO' " + "AND elab.idStrut = :idStrut ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idElencoVersSelezionatiList",
		longListFrom(idElencoVersSelezionatiList));
	query.setParameter("dataChiusura", dataChiusura);
	query.setParameter("idStrut", idStrut);
	return (Long) query.getSingleResult() == 0;
    }

    public boolean existNomeElenco(String nmElenco, BigDecimal idStrut) {
	String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.orgStrut.idStrut = :idStrut "
		+ "AND u.nmElenco = :nmElenco ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmElenco", nmElenco);
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	return !query.getResultList().isEmpty();
    }

    public void saveNomeDesNote(Long idUserIam, BigDecimal idElencoVers, String nmElenco,
	    String dsElenco, String ntIndiceElenco, String ntElencoChiuso,
	    List<ElencoEnums.OpTypeEnum> operList) {
	ElvElencoVer elenco = getEntityManager().find(ElvElencoVer.class, idElencoVers.longValue());
	elenco.setNmElenco(nmElenco);
	elenco.setDsElenco(dsElenco);
	elenco.setNtIndiceElenco(ntIndiceElenco);
	elenco.setNtElencoChiuso(ntElencoChiuso);
	/* A seconda di cosa ho modificato, scrivo nel log */
	for (ElencoEnums.OpTypeEnum oper : operList) {
	    evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam, oper.name());
	}
    }

    public void removeAppartenenzaUdElenco(Long idUnitaDoc) {
	AroUnitaDoc unitaDoc = findById(AroUnitaDoc.class, idUnitaDoc);
	unitaDoc.setElvElencoVer(null);
	// MC#27493
	unitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.NON_SELEZ_SCHED.name());
	// end MC#27493
    }

    public void removeAppartenenzaDocElenco(Long idDoc) {
	AroDoc doc = findById(AroDoc.class, idDoc);
	doc.setElvElencoVer(null);
	// MC#27493
	doc.setTiStatoDocElencoVers(DocStatusEnum.NON_SELEZ_SCHED.name());
	// end MC#27493
    }

    static class Filtri {
	BigDecimal idElencoVers;
	String nmElenco;
	String dsElenco;
	List<String> tiStatoElenco;
	String tiValidElenco;
	String tiModValidElenco;
	String tiGestElenco;
	Timestamp creazioneElencoDa;
	Timestamp creazioneElencoA;
	Timestamp validazioneElencoDa;
	Timestamp validazioneElencoA;
	String ntElencoChiuso;
	String ntIndiceElenco;
	String cdRegistroKeyUnitaDoc;
	BigDecimal aaKeyUnitaDoc;
	String cdKeyUnitaDoc;
	BigDecimal aaKeyUnitaDocDa;
	BigDecimal aaKeyUnitaDocA;
	String cdKeyUnitaDocDa;
	String cdKeyUnitaDocA;
	List<String> tiStatoConservazione;
	String nmCriterioRaggr;
	String flElencoStandard;
	String flElencoFisc;
	String flElencoFirmato;
	String flElencoIndiciAipCreato;
	Timestamp creazioneElencoIxAipDa;
	Timestamp creazioneElencoIxAipA;
	Timestamp firmaElencoIxAipDa;
	Timestamp firmaElencoIxAipA;
	BigDecimal hhStatoElencoInCodaJms;
	private BigDecimal idAmbiente;
	private BigDecimal idEnte;
	private BigDecimal idStrut;

	Filtri() {

	}

	Filtri(FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
	    idElencoVers = filtriElenchiVersamento.getId_elenco_vers().parse();
	    nmElenco = filtriElenchiVersamento.getNm_elenco().parse();
	    dsElenco = filtriElenchiVersamento.getDs_elenco().parse();
	    tiStatoElenco = filtriElenchiVersamento.getTi_stato_elenco().parse();
	    tiValidElenco = filtriElenchiVersamento.getTi_valid_elenco().parse();
	    tiModValidElenco = filtriElenchiVersamento.getTi_mod_valid_elenco().parse();
	    tiGestElenco = filtriElenchiVersamento.getTi_gest_elenco().parse();
	    creazioneElencoDa = filtriElenchiVersamento.getDt_creazione_elenco_da().parse();
	    creazioneElencoA = filtriElenchiVersamento.getDt_creazione_elenco_a().parse();
	    validazioneElencoDa = filtriElenchiVersamento.getDt_validazione_elenco_da().parse();
	    validazioneElencoA = filtriElenchiVersamento.getDt_validazione_elenco_a().parse();
	    ntElencoChiuso = filtriElenchiVersamento.getNt_elenco_chiuso().parse();
	    ntIndiceElenco = filtriElenchiVersamento.getNt_indice_elenco().parse();
	    cdRegistroKeyUnitaDoc = filtriElenchiVersamento.getCd_registro_key_unita_doc().parse();
	    aaKeyUnitaDoc = filtriElenchiVersamento.getAa_key_unita_doc().parse();
	    cdKeyUnitaDoc = filtriElenchiVersamento.getCd_key_unita_doc().parse();
	    aaKeyUnitaDocDa = filtriElenchiVersamento.getAa_key_unita_doc_da().parse();
	    aaKeyUnitaDocA = filtriElenchiVersamento.getAa_key_unita_doc_a().parse();
	    cdKeyUnitaDocDa = filtriElenchiVersamento.getCd_key_unita_doc_da().parse();
	    cdKeyUnitaDocA = filtriElenchiVersamento.getCd_key_unita_doc_a().parse();
	    nmCriterioRaggr = filtriElenchiVersamento.getNm_criterio_raggr().parse();
	    flElencoStandard = filtriElenchiVersamento.getFl_elenco_standard().parse();
	    flElencoFisc = filtriElenchiVersamento.getFl_elenco_fisc().parse();
	    flElencoFirmato = filtriElenchiVersamento.getFl_elenco_firmato().parse();
	    flElencoIndiciAipCreato = filtriElenchiVersamento.getFl_elenco_indici_aip_creato()
		    .parse();
	    creazioneElencoIxAipDa = filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da()
		    .parse();
	    creazioneElencoIxAipA = filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a()
		    .parse();
	    firmaElencoIxAipDa = filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse();
	    firmaElencoIxAipA = filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse();
	    hhStatoElencoInCodaJms = filtriElenchiVersamento.getHh_stato_elenco_in_coda_jms()
		    .parse();
	    idAmbiente = filtriElenchiVersamento.getId_ambiente().parse();
	    idEnte = filtriElenchiVersamento.getId_ente().parse();
	    idStrut = filtriElenchiVersamento.getId_strut().parse();
	    tiStatoConservazione = filtriElenchiVersamento.getTi_stato_conservazione().parse();
	}

	public BigDecimal getIdElencoVers() {
	    return idElencoVers;
	}

	void setIdElencoVers(BigDecimal idElencoVers) {
	    this.idElencoVers = idElencoVers;
	}

	public String getNmElenco() {
	    return nmElenco;
	}

	void setNmElenco(String nmElenco) {
	    this.nmElenco = nmElenco;
	}

	public String getDsElenco() {
	    return dsElenco;
	}

	void setDsElenco(String dsElenco) {
	    this.dsElenco = dsElenco;
	}

	public List<String> getTiStatoElenco() {
	    return tiStatoElenco;
	}//

	void setTiStatoElenco(List<String> tiStatoElenco) {
	    this.tiStatoElenco = tiStatoElenco;
	}

	public String getTiValidElenco() {
	    return tiValidElenco;
	}

	public void setTiValidElenco(String tiValidElenco) {
	    this.tiValidElenco = tiValidElenco;
	}

	public String getTiModValidElenco() {
	    return tiModValidElenco;
	}

	public void setTiModValidElenco(String tiModValidElenco) {
	    this.tiModValidElenco = tiModValidElenco;
	}

	public String getTiGestElenco() {
	    return tiGestElenco;
	}

	void setTiGestElenco(String tiGestElenco) {
	    this.tiGestElenco = tiGestElenco;
	}

	public Timestamp getCreazioneElencoDa() {
	    return creazioneElencoDa;
	}

	void setCreazioneElencoDa(Timestamp creazioneElencoDa) {
	    this.creazioneElencoDa = creazioneElencoDa;
	}

	public Timestamp getCreazioneElencoA() {
	    return creazioneElencoA;
	}

	void setCreazioneElencoA(Timestamp creazioneElencoA) {
	    this.creazioneElencoA = creazioneElencoA;
	}

	public Timestamp getValidazioneElencoDa() {
	    return validazioneElencoDa;
	}

	void setValidazioneElencoDa(Timestamp validazioneElencoDa) {
	    this.validazioneElencoDa = validazioneElencoDa;
	}

	public Timestamp getValidazioneElencoA() {
	    return validazioneElencoA;
	}

	void setValidazioneElencoA(Timestamp validazioneElencoA) {
	    this.validazioneElencoA = validazioneElencoA;
	}

	public String getNtElencoChiuso() {
	    return ntElencoChiuso;
	}

	void setNtElencoChiuso(String ntElencoChiuso) {
	    this.ntElencoChiuso = ntElencoChiuso;
	}

	public String getNtIndiceElenco() {
	    return ntIndiceElenco;
	}

	void setNtIndiceElenco(String ntIndiceElenco) {
	    this.ntIndiceElenco = ntIndiceElenco;
	}

	public String getCdRegistroKeyUnitaDoc() {
	    return cdRegistroKeyUnitaDoc;
	}

	void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	    this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
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

	public List<String> getTiStatoConservazione() {
	    return tiStatoConservazione;
	}//

	void setTiStatoConservazione(List<String> tiStatoConservazione) {
	    this.tiStatoConservazione = tiStatoConservazione;
	}

	public String getNmCriterioRaggr() {
	    return nmCriterioRaggr;
	}

	void setNmCriterioRaggr(String nmCriterioRaggr) {
	    this.nmCriterioRaggr = nmCriterioRaggr;
	}

	public String getFlElencoStandard() {
	    return flElencoStandard;
	}

	void setFlElencoStandard(String flElencoStandard) {
	    this.flElencoStandard = flElencoStandard;
	}

	public String getFlElencoFisc() {
	    return flElencoFisc;
	}

	void setFlElencoFisc(String flElencoFisc) {
	    this.flElencoFisc = flElencoFisc;
	}

	public String getFlElencoFirmato() {
	    return flElencoFirmato;
	}

	void setFlElencoFirmato(String flElencoFirmato) {
	    this.flElencoFirmato = flElencoFirmato;
	}

	public String getFlElencoIndiciAipCreato() {
	    return flElencoIndiciAipCreato;
	}

	void setFlElencoIndiciAipCreato(String flElencoIndiciAipCreato) {
	    this.flElencoIndiciAipCreato = flElencoIndiciAipCreato;
	}

	public Timestamp getCreazioneElencoIxAipDa() {
	    return creazioneElencoIxAipDa;
	}

	void setCreazioneElencoIxAipDa(Timestamp creazioneElencoIxAipDa) {
	    this.creazioneElencoIxAipDa = creazioneElencoIxAipDa;
	}

	public Timestamp getCreazioneElencoIxAipA() {
	    return creazioneElencoIxAipA;
	}

	void setCreazioneElencoIxAipA(Timestamp creazioneElencoIxAipA) {
	    this.creazioneElencoIxAipA = creazioneElencoIxAipA;
	}

	public Timestamp getFirmaElencoIxAipDa() {
	    return firmaElencoIxAipDa;
	}

	void setFirmaElencoIxAipDa(Timestamp firmaElencoIxAipDa) {
	    this.firmaElencoIxAipDa = firmaElencoIxAipDa;
	}

	public Timestamp getFirmaElencoIxAipA() {
	    return firmaElencoIxAipA;
	}

	void setFirmaElencoIxAipA(Timestamp firmaElencoIxAipA) {
	    this.firmaElencoIxAipA = firmaElencoIxAipA;
	}

	public BigDecimal getHhStatoElencoInCodaJms() {
	    return hhStatoElencoInCodaJms;
	}

	void setHhStatoElencoInCodaJms(BigDecimal hhStatoElencoInCodaJms) {
	    this.hhStatoElencoInCodaJms = hhStatoElencoInCodaJms;
	}

	public BigDecimal getIdAmbiente() {
	    return idAmbiente;
	}

	void setIdAmbiente(BigDecimal idAmbiente) {
	    this.idAmbiente = idAmbiente;
	}

	public BigDecimal getIdEnte() {
	    return idEnte;
	}

	void setIdEnte(BigDecimal idEnte) {
	    this.idEnte = idEnte;
	}

	public BigDecimal getIdStrut() {
	    return idStrut;
	}

	void setIdStrut(BigDecimal idStrut) {
	    this.idStrut = idStrut;
	}
    }
}
