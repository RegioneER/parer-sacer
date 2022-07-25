package it.eng.parer.job.calcoloConsistenza.ejb;

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogElabConsist;
import it.eng.parer.entity.MonContaByStatoConservNew;
import it.eng.parer.entity.MonContaByStatoConservNew.TipoConteggio;
import it.eng.parer.entity.MonTipoUnitaDocUserVers;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.TmpStrutCalcConsistNew;
import it.eng.parer.entity.TmpStrutCalcConsistNew.TiStatoElab;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CalcoloConsistenzaHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloConsistenzaHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    private static final String UD_AIP_GENERATO = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " FUNC('nvl',count(comp.idStrut), 0), 'UD_AIP_GENERATO' ) " + " FROM AroCompDoc comp "
            + " JOIN comp.aroStrutDoc strutDoc " + " JOIN strutDoc.aroDoc doc " + " JOIN doc.aroUnitaDoc ud, "
            + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 " + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('AIP_GENERATO', 'AIP_FIRMATO', 'VERSAMENTO_IN_ARCHIVIO', 'IN_ARCHIVIO', 'IN_CUSTODIA') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc " + " AND docPrinc.tiDoc = 'PRINCIPALE' "
            + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY FUNC('trunc', doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut,ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_PRESA_IN_CARICO = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " FUNC('nvl',count(comp.idStrut), 0), 'UD_AIP_NON_GENERATO_PRESA_IN_CARICO') " + " FROM AroCompDoc comp "
            + " JOIN comp.aroStrutDoc strutDoc " + " JOIN strutDoc.aroDoc doc " + " JOIN doc.aroUnitaDoc ud, "
            + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 " + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('PRESA_IN_CARICO') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY FUNC('trunc', doc.dtCreazione), ud.idOrgStrut,ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_AIP_IN_AGG = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, " + " FUNC('nvl',count(comp.idStrut), 0), "
            + " 'UD_AIP_NON_GENERATO_AIP_IN_AGG') " + " FROM AroCompDoc comp " + " JOIN comp.aroStrutDoc strutDoc "
            + " JOIN strutDoc.aroDoc doc " + " JOIN doc.aroUnitaDoc ud, "
            + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 " + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('AIP_IN_AGGIORNAMENTO') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY FUNC('trunc', doc.dtCreazione), ud.idOrgStrut,ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_IN_VOL_CONS = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, " + " FUNC('nvl',count(comp.idStrut), 0), "
            + " 'UD_AIP_NON_GENERATO_IN_VOLUME_CONS') " + " FROM AroCompDoc comp " + " JOIN comp.aroStrutDoc strutDoc "
            + " JOIN strutDoc.aroDoc doc " + " JOIN doc.aroUnitaDoc ud, "
            + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 " + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('IN_VOLUME_DI_CONSERVAZIONE', 'AIP_DA_GENERARE') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY FUNC('trunc', doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut,ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    /* Nuove query per il calcolo delle ud, doc e comp annullati */
    private String getAnnullQuery() {

        return "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
                + "FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, "
                + "FUNC('nvl', count(compDoc), 0), 'UD_ANNULL') FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich , AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 "
                + "WHERE unitaDoc.idOrgStrut = :idStrut AND statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                + "AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA AND unitaDoc2 = unitaDoc "
                + "AND richAnnul.tiRichAnnulVers = 'UNITA_DOC' "
                + "GROUP BY FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut,unitaDoc.aaKeyUnitaDoc, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc ";
    }

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA = "SELECT " + "trunc(t0.dt_creazione), "
            + "t3.id_strut, " + "t3.id_sub_strut, " + "t3.aa_key_unita_doc, " + "t3.id_registro_unita_doc, "
            + "t3.id_tipo_unita_doc, " + "t4.id_tipo_doc, " + "    COUNT(CASE WHEN t3.ti_stato_conservazione IN ( "
            + "       'AIP_GENERATO', 'AIP_FIRMATO', 'VERSAMENTO_IN_ARCHIVIO', 'IN_ARCHIVIO', 'IN_CUSTODIA') "
            + "     THEN 1 " + "    ELSE NULL " + "   END) NI_COMP_AIP_GENERATO, "
            + "    COUNT(CASE WHEN t3.ti_stato_conservazione IN ( " + "       'AIP_IN_AGGIORNAMENTO') "
            + "      THEN 1 " + "     ELSE NULL " + "    END) NI_COMP_AIP_IN_AGG, "
            + "    COUNT(CASE WHEN t3.ti_stato_conservazione IN ( " + "       'PRESA_IN_CARICO') " + "      THEN 1 "
            + "     ELSE NULL " + "    END) NI_COMP_PRESA_IN_CARICO, "
            + "     COUNT(CASE WHEN t3.ti_stato_conservazione IN ( "
            + "       'IN_VOLUME_DI_CONSERVAZIONE', 'AIP_DA_GENERARE') " + "      THEN 1 " + "     ELSE NULL "
            + "    END) NI_COMP_VOLUME " + "FROM " + "   sacer.aro_unita_doc   t5, " + "    sacer.aro_doc         t4, "
            + "    sacer.aro_unita_doc   t3, " + "    sacer.aro_comp_doc    t2, " + "    sacer.aro_strut_doc   t1, "
            + "    sacer.aro_doc         t0 " + "WHERE " + "    ( ( ( ( ( ( ( t3.id_strut = ?1 ) " + "        ) "
            + "              AND ( t0.dt_creazione >= ?2 ) ) " + "           AND ( t0.dt_creazione < ?3 ) ) "
            + "         AND t3.aa_key_unita_doc = ?4 " + "          AND ( t4.ti_doc = 'PRINCIPALE' ) ) "
            + "        AND ( t3.id_unita_doc = t5.id_unita_doc ) ) "
            + "      AND ( ( ( ( t1.id_strut_doc = t2.id_strut_doc ) "
            + "                AND ( t0.id_doc = t1.id_doc ) ) "
            + "              AND ( t3.id_unita_doc = t0.id_unita_doc ) ) "
            + "            AND ( t5.id_unita_doc = t4.id_unita_doc ) ) ) " + "GROUP BY "
            + "    trunc(t0.dt_creazione), " + "    t3.id_strut, " + "    t3.id_sub_strut, "
            + "    t3.aa_key_unita_doc, " + "    t3.id_registro_unita_doc, " + "    t3.id_tipo_unita_doc, "
            + "    t4.id_tipo_doc";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA2 = "SELECT dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW "
            + "WHERE id_sub_strut = ?1 AND aa_key_unita_doc = ?2 AND id_strut = ?3 AND (dt_creazione >= ?4 AND dt_creazione < ?5 )";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3 = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW "
            + "WHERE id_sub_strut = ?1 AND aa_key_unita_doc = ?2 AND id_strut = ?3 AND dt_creazione BETWEEN ?4 AND ?5 ";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3_WITH_EXCL = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW "
            + "WHERE dt_creazione NOT IN (?6) AND id_sub_strut = ?1 AND aa_key_unita_doc = ?2 AND dt_creazione BETWEEN ?4 AND ?5 AND id_strut = ?3 ";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4 = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW " + "WHERE dt_creazione BETWEEN ?1 AND ?2 ";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_SINGOLO_GIORNO = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW " + "WHERE id_sub_strut = ?1 AND dt_creazione = ?2 ";

    private String getAnnullQuery2() {

        return "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
                + "FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, "
                + "FUNC('nvl', count(compDoc), 0), 'UD_ANNULL') FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich , AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 "
                + "WHERE unitaDoc.idOrgStrut = :idStrut AND unitaDoc.idOrgSubStrut = :idSubStrut AND statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                + "AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA AND unitaDoc2 = unitaDoc "
                + "AND richAnnul.tiRichAnnulVers = 'UNITA_DOC' " + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "GROUP BY FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut,unitaDoc.aaKeyUnitaDoc, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc ";
    }

    private String getAnnullQuery3() {

        return "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
                + "FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, "
                + "FUNC('nvl', count(compDoc), 0), 'UD_ANNULL') FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich , AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 "
                + "WHERE unitaDoc.idOrgStrut = :idStrut AND statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                + "AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA AND unitaDoc2 = unitaDoc "
                + "AND richAnnul.tiRichAnnulVers = 'UNITA_DOC' "
                + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc AND subStrut.idSubStrut = :idSubStrut "
                + "GROUP BY FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut,unitaDoc.aaKeyUnitaDoc, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc ";
    }

    private String getAnnullQuery4() {

        return "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
                + "FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "FUNC('nvl', count(compDoc), 0), 'UD_ANNULL') FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut, "
                + "AroStatoRichAnnulVers statoCorRich , AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 "
                + "WHERE unitaDoc.idOrgStrut = :idStrut AND statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                + "AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA AND unitaDoc2 = unitaDoc "
                + "AND richAnnul.tiRichAnnulVers = 'UNITA_DOC' "
                + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc AND subStrut.idSubStrut = :idSubStrut "
                + "GROUP BY FUNC('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut,unitaDoc.aaKeyUnitaDoc, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc ";
    }

    // elaborazione
    // data
    /**
     * Ricava l'ultima data in cui è stata calcolata la consistenza, per ricavare le nuove date devo ricavare la data
     * MASSIMA di un'ultima esecuzione Eventuali esecuzioni KO in date precedenti verrebberro considerate al giro
     * successivo in quanto ancora presenti nella tabella TMP_CALC_CONSIST_NEW
     *
     * @return la data di inizio calcolo consistenza
     */
    public Calendar getDataUltimoCalcoloConsistenza() {
        String queryString = "SELECT MAX(u.dtRifConta) FROM TmpStrutCalcConsistNew u ";
        Query query = entityManager.createQuery(queryString);
        Date d = (Date) query.getSingleResult();
        Calendar cal = Calendar.getInstance();
        if (d == null) {
            // Imposto la data all'1 dicembre 2011
            cal.set(Calendar.YEAR, 2011);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.setTime(d);
        }
        return cal;
    }

    public Date get30Novembre2011() {
        Calendar cal = Calendar.getInstance();
        // Imposto la data al 30 novembre 2011
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DATE, 30);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Calendar get1Dicembre2011() {
        Calendar cal = Calendar.getInstance();
        // Imposto la data al 1 dicembre 2011
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Ricavo TUTTE le strutture e le salvo in tabella temporanea con data di riferimento controllando che non esista
     * già il record
     *
     * @param dtRif
     *            data di riferimento
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenza(Date dtRif) {
        /* Ricavo le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
        Query query1 = entityManager.createQuery("SELECT DISTINCT strut.idStrut FROM VrsSessioneVers sessioneVers "
                + "JOIN sessioneVers.orgStrut strut " + "WHERE sessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                + "AND FUNC('trunc', sessioneVers.dtChiusura) = :dtRif "
                + "AND NOT EXISTS (SELECT strut2.orgStrut.idStrut FROM TmpStrutCalcConsistNew strut2 WHERE strut2.orgStrut.idStrut = strut.idStrut AND strut2.dtRifConta = :dtRif ) ");

        query1.setParameter("dtRif", dtRif);
        List<Long> idStrutList;
        idStrutList = query1.getResultList();
        Set<Long> idStrutSet = new HashSet<>(idStrutList);

        Query query2 = entityManager
                .createQuery("SELECT DISTINCT unitaDoc.orgStrut.idStrut FROM AroDoc doc JOIN doc.aroUnitaDoc unitaDoc "
                        + "WHERE FUNC('trunc', doc.dtCreazione) = :dtRif "
                        + "AND NOT EXISTS (SELECT strut2.orgStrut.idStrut FROM TmpStrutCalcConsistNew strut2 WHERE strut2.orgStrut.idStrut = unitaDoc.orgStrut.idStrut AND strut2.dtRifConta = :dtRif ) ");

        query2.setParameter("dtRif", dtRif);
        List<Long> idStrut2List;
        idStrut2List = query2.getResultList();
        Set<Long> idStrut2Set = new HashSet<>(idStrut2List);

        idStrutSet.addAll(idStrut2Set);

        /* Inserisco le strutture, con il giorno specifico, nella tabella temporanea */
        for (Long idStrut : idStrutSet) {
            insertTmpStrutCalcConsist(idStrut, dtRif);
        }
        entityManager.flush();
    }

    /**
     * Ricavo TUTTE le strutture e le salvo in tabella temporanea con data di riferimento controllando che non esista
     * già il record
     *
     * @param dtRifConta
     *            data di riferimento
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiTutteStruttureDaConsiderarePerCalcoloConsistenza(Date dtRifConta) {
        /* Ricavo le strutture da considerare */
        Query query1 = entityManager.createQuery("SELECT strut.idStrut FROM OrgStrut strut "
                + "WHERE NOT EXISTS (SELECT strut2.orgStrut.idStrut FROM TmpStrutCalcConsistNew strut2 WHERE "
                + "strut2.orgStrut.idStrut = strut.idStrut AND strut2.dtRifConta = :dtRifConta ) ");
        query1.setParameter("dtRifConta", dtRifConta);
        // getResultList
        List<Long> idStrutList;
        idStrutList = query1.getResultList();

        /* Inserisco le strutture, con il giorno specifico, nella tabella temporanea */
        for (Long idStrut : idStrutList) {
            insertTmpStrutCalcConsist(idStrut, dtRifConta);
        }
        entityManager.flush();
    }

    /**
     * Inserimento JPA di un record nella tabella TMP_CALC_CONSIST_NEW
     *
     * @param idStrut
     *            parametro struttura da inserire
     * @param dtRifConta
     *            parametro data di rifemento da inserire
     */
    private void insertTmpStrutCalcConsist(long idStrut, Date dtRifConta) {
        TmpStrutCalcConsistNew strutCalcConsist = new TmpStrutCalcConsistNew();
        strutCalcConsist.setOrgStrut(entityManager.find(OrgStrut.class, idStrut));
        strutCalcConsist.setDtRifConta(dtRifConta);
        strutCalcConsist.setTiStatoElab(TiStatoElab.DA_ELABORARE.name());
        entityManager.persist(strutCalcConsist);
    }

    /**
     * Eliminazione JPA di un elemento dalla tabella TMP_CALC_CONSIST_NEW
     *
     * @param id
     *            l'identificativo del record da eliminare
     */
    public void deleteTmpStrutCalcConsist(long id) {
        TmpStrutCalcConsistNew strutCalcConsist = entityManager.find(TmpStrutCalcConsistNew.class, id);
        entityManager.remove(strutCalcConsist);
        entityManager.flush();
    }

    /**
     * Metodo di supporto per eliminazioni da TMP_STRUT_CALC_CONSIST_NEW prima di una determinata data
     *
     * @param id
     *            l'identificativo del record da eliminare
     * @param dtRifConta
     *            la data dalla quale partire, a ritroso, con l'eliminazione
     */
    public void deleteAllTmpStrutCalcConsistByStrutAndDate(long id, Date dtRifConta) {
        Query q = entityManager
                .createQuery("DELETE FROM TmpStrutCalcConsistNew strut " + "WHERE strut.orgStrut.idStrut = :idStrut "
                        + "AND strut.dtRifConta < :dtRifConta " + "AND strut.tiStatoElab = 'ELABORAZIONE_OK' ");
        q.setParameter("idStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.executeUpdate();
        entityManager.flush();
    }

    // id
    /**
     * Metodo di supporto per aggiornaramento stato in TMP_STRUT_CALC_CONSIST_NEW per una determinata data
     *
     * @param id
     *            l'identificativo del record da aggiornare
     * @param dtRifConta
     *            la data da considerare per l'aggiornamento
     */
    public void updateLastTmpStrutCalcConsistByStrutAndDate(long id, Date dtRifConta) {
        Query q = entityManager
                .createQuery("UPDATE TmpStrutCalcConsistNew strut SET strut.tiStatoElab = 'ELABORAZIONE_OK' "
                        + "WHERE strut.orgStrut.idStrut = :idStrut " + "AND strut.dtRifConta = :dtRifConta ");

        q.setParameter("idStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.executeUpdate();
        entityManager.flush();
    }

    /**
     * Metodo di supporto per eliminazioni da TMP_STRUT_CALC_CONSIST_NEW prima di una determinata data
     *
     * @param id
     *            l'identificativo del record da eliminare
     * @param dtRifConta
     *            la data dalla quale partire, a ritroso, con l'eliminazione
     */
    public void deleteAllTmpStrutCalcConsistByStrutAndDateSubStrut(long id, Date dtRifConta) {
        Query q = entityManager.createQuery(
                "DELETE FROM TmpStrutCalcConsistNew strut " + "WHERE strut.orgSubStrut.idSubStrut = :idSubStrut "
                        + "AND strut.dtRifConta < :dtRifConta " + "AND strut.tiStatoElab = 'ELABORAZIONE_OK' ");
        q.setParameter("idSubStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.executeUpdate();
        entityManager.flush();
    }

    // id
    /**
     * Metodo di supporto per aggiornaramento stato in TMP_STRUT_CALC_CONSIST_NEW per una determinata data
     *
     * @param id
     *            l'identificativo del record da aggiornare
     * @param dtRifConta
     *            la data da considerare per l'aggiornamento
     */
    public void updateLastTmpStrutCalcConsistByStrutAndDateSubStrut(long id, Date dtRifConta) {
        Query q = entityManager
                .createQuery("UPDATE TmpStrutCalcConsistNew strut SET strut.tiStatoElab = 'ELABORAZIONE_OK' "
                        + "WHERE strut.orgSubStrut.idSubStrut = :idSubStrut " + "AND strut.dtRifConta = :dtRifConta ");

        q.setParameter("idSubStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.executeUpdate();
        entityManager.flush();
    }

    /**
     * Metodo di supporto per eliminazioni da TMP_STRUT_CALC_CONSIST_NEW prima di una determinata data
     *
     * @param id
     *            l'identificativo del record da eliminare
     * @param dtRifConta
     *            la data dalla quale partire, a ritroso, con l'eliminazione
     * @param aaKeyUnitaDoc
     *            anno di riferimento dell'unità documentaria
     */
    public void deleteAllTmpStrutCalcConsistByStrutDaElab(long id, Date dtRifConta, BigDecimal aaKeyUnitaDoc) {
        Query q = entityManager.createQuery("DELETE FROM TmpStrutCalcConsistNew strut "
                + "WHERE strut.orgSubStrut.idSubStrut = :idSubStrut " + "AND strut.dtRifConta < :dtRifConta "
                + "AND strut.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_OK') "
                + "AND strut.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        q.setParameter("idSubStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        q.executeUpdate();
        entityManager.flush();
    }

    // id
    /**
     * Metodo di supporto per aggiornaramento stato in TMP_STRUT_CALC_CONSIST_NEW per una determinata data
     *
     * @param id
     *            l'identificativo del record da aggiornare
     * @param dtRifConta
     *            la data da considerare per l'aggiornamento
     * @param aaKeyUnitaDoc
     *            anno di riferimento dell'unità documentaria
     */
    public void updateLastTmpStrutCalcConsistByStrutDaElab(long id, Date dtRifConta, BigDecimal aaKeyUnitaDoc) {
        Query q = entityManager.createQuery(
                "UPDATE TmpStrutCalcConsistNew strut SET strut.tiStatoElab = 'ELABORAZIONE_OK', strut.dtExecJob = :dtExecJob "
                        + "WHERE strut.orgSubStrut.idSubStrut = :idSubStrut " + "AND strut.dtRifConta = :dtRifConta "
                        + "AND strut.aaKeyUnitaDoc = :aaKeyUnitaDoc ");

        q.setParameter("idSubStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        q.setParameter("dtExecJob", c.getTime());
        q.executeUpdate();
        entityManager.flush();
    }

    /**
     * Metodo di supporto per eliminazioni da TMP_STRUT_CALC_CONSIST_NEW prima di una determinata data
     *
     * @param id
     *            l'identificativo del record da eliminare
     * @param dtRifConta
     *            la data dalla quale partire, a ritroso, con l'eliminazione
     * @param aaKeyUnitaDoc
     *            anno di chiave dell'unità documentaria
     */
    public void deleteAllTmpStrutCalcConsistByStrutAndDate2(long id, Date dtRifConta, BigDecimal aaKeyUnitaDoc) {
        Query q = entityManager.createQuery("DELETE FROM TmpStrutCalcConsistNew strut "
                + "WHERE strut.orgStrut.idStrut = :idStrut " + "AND strut.dtRifConta < :dtRifConta "
                + "AND strut.tiStatoElab = 'ELABORAZIONE_OK' " + "AND strut.aaKeyUnitaDoc = :aaKeyUnitaDoc");

        q.setParameter("idStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        q.executeUpdate();
        entityManager.flush();
    }

    // id
    /**
     * Metodo di supporto per aggiornaramento stato in TMP_STRUT_CALC_CONSIST_NEW per una determinata data
     *
     * @param id
     *            l'identificativo del record da aggiornare
     * @param dtRifConta
     *            la data da considerare per l'aggiornamento
     * @param aaKeyUnitaDoc
     *            anno di chiave dell'unità documentaria
     */
    public void updateLastTmpStrutCalcConsistByStrutAndDate2(long id, Date dtRifConta, BigDecimal aaKeyUnitaDoc) {
        Query q = entityManager
                .createQuery("UPDATE TmpStrutCalcConsistNew strut SET strut.tiStatoElab = 'ELABORAZIONE_OK' "
                        + "WHERE strut.orgStrut.idStrut = :idStrut " + "AND strut.dtRifConta = :dtRifConta "
                        + "AND strut.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        q.setParameter("idStrut", id);
        q.setParameter("dtRifConta", dtRifConta);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        q.executeUpdate();
        entityManager.flush();
    }

    public TmpStrutCalcConsistNew getTmpStrutCalcConsist(long id) {
        return entityManager.find(TmpStrutCalcConsistNew.class, id);
    }

    /**
     * Recupera dalla tabella temporanea le strutture da elaborare per il calcolo consistenza che abbiano uno degli
     * stati DA_ELABORARE o ELABORAZIONE_OK
     *
     * @return la lista delle strutture, con relativo giorno, da elaborare
     */
    public List<TmpStrutCalcConsistNew> getStruttureDaElaborarePerCalcoloConsistenza() {
        String queryString = "SELECT u FROM TmpStrutCalcConsistNew u "
                + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') "
                + "ORDER BY u.orgStrut.idStrut, u.dtRifConta ASC ";
        Query query = entityManager.createQuery(queryString);
        return query.getResultList();
    }

    /**
     * Recupera dalla tabella temporanea le strutture da elaborare per il calcolo consistenza che abbiano uno degli
     * stati DA_ELABORARE o ELABORAZIONE_OK
     *
     * @param idStrut
     *            id della struttura
     * @param idSubStrut
     *            id della sottostruttura
     *
     * @return la lista delle strutture, con relativo giorno, da elaborare
     */
    public List<TmpStrutCalcConsistNew> getStrutturaDaElaborarePerCalcoloConsistenza(long idStrut, long idSubStrut) {
        String queryString = "SELECT u FROM TmpStrutCalcConsistNew u "
                + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') " + "AND u.orgStrut.idStrut = :idStrut "
                + "AND u.orgSubStrut.idSubStrut = :idSubStrut " + "ORDER BY u.orgStrut.idStrut, u.dtRifConta ASC ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idSubStrut", idSubStrut);
        return query.getResultList();
    }

    /**
     * Recupera dalla tabella temporanea le strutture da elaborare per il calcolo consistenza che abbiano uno degli
     * stati DA_ELABORARE o ELABORAZIONE_OK
     *
     * @param idSubStrut
     *            id della sottostruttura
     *
     * @return la lista delle strutture, con relativo giorno, da elaborare
     */
    public List<Object[]> getRecordMinMax(long idSubStrut) {
        String queryString = "SELECT MIN(u.dtRifConta), MAX(u.dtRifConta), u.aaKeyUnitaDoc "
                + "FROM TmpStrutCalcConsistNew u " + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') "
                + "AND u.orgSubStrut.idSubStrut = :idSubStrut " + "GROUP BY u.aaKeyUnitaDoc ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idSubStrut", idSubStrut);
        return query.getResultList();
    }

    public List<TmpStrutCalcConsistNew> getStruttureDaElaborarePerCalcoloConsistenza2(Date d1, Date d2) {
        String queryString = "SELECT u FROM TmpStrutCalcConsistNew u "
                + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') "
                + "AND u.dtRifConta BETWEEN :d1 AND :d2 " + "ORDER BY u.orgStrut.idStrut, u.dtRifConta ASC ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("d1", d1);
        query.setParameter("d2", d2);
        return query.getResultList();
    }

    /**
     * Metodo di prova per recuperare dalla tabella temporanea le strutture da elaborare per il calcolo consistenza che
     * abbiano uno degli stati DA_ELABORARE o ELABORAZIONE_OK impostando, cablata, una data da cui partire
     *
     * @return la lista delle strutture, con relativo giorno, da elaborare
     */
    public List<TmpStrutCalcConsistNew> getStruttureDaElaborarePerCalcoloConsistenzaDtRifContaCustom() {
        String queryString = "SELECT u FROM TmpStrutCalcConsistNew u "
                + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') " + "AND u.dtRifConta >= :dtRifConta "
                + "ORDER BY u.dtRifConta ASC ";
        Query query = entityManager.createQuery(queryString);
        Calendar c = Calendar.getInstance();
        c.set(2021, Calendar.JANUARY, 1, 0, 0, 0);
        query.setParameter("dtRifConta", c.getTime());
        return query.getResultList();
    }

    // strutCalcConsist
    /**
     * Calcola: - per un giorno specifico - per una struttura specifica i totali da inserire in
     * MON_CONTA_BY_STATO_CONSERV_NEW sulla base delle query
     *
     * @param strutCalcConsist
     *            il record della struttura da trattare contenente anche il riferimento alla data
     *
     * @throws IOException
     *             eccezione nell'inserimento dei totali
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno(TmpStrutCalcConsistNew strutCalcConsist) throws IOException {
        long idStrut = strutCalcConsist.getOrgStrut().getIdStrut();
        Date da = strutCalcConsist.getDtRifConta();
        Date a = DateUtils.addDays(da, 1);
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        List<MonContaByStatoConservNew> resParziale = executeQueryCalcolo(idStrut, da, a, UD_AIP_GENERATO);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_GENERATO);
        resParziale = executeQueryCalcolo(idStrut, da, a, UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        resParziale = executeQueryCalcolo(idStrut, da, a, UD_AIP_NON_GENERATO_AIP_IN_AGG);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_AIP_IN_AGG);
        resParziale = executeQueryCalcolo(idStrut, da, a, UD_AIP_NON_GENERATO_IN_VOL_CONS);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_IN_VOLUME_CONS);
        resParziale = executeQueryCalcolo(idStrut, da, a, getAnnullQuery());
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_ANNULL);

        // Inserisco nella tabella MON_CONTA_BY_STATO_CONSERV
        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }

        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutAndDate(strutCalcConsist.getOrgStrut().getIdStrut(),
                strutCalcConsist.getDtRifConta());
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutAndDate(strutCalcConsist.getOrgStrut().getIdStrut(),
                strutCalcConsist.getDtRifConta());

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno2(TmpStrutCalcConsistNew strutCalcConsist) {
        long idStrut = strutCalcConsist.getOrgStrut().getIdStrut();
        BigDecimal aaKeyUnitaDoc = strutCalcConsist.getAaKeyUnitaDoc();
        Date da = strutCalcConsist.getDtRifConta();
        Date a = DateUtils.addDays(da, 1);
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        List<MonContaByStatoConservNew> resParziale = executeNativeQueryCalcolo(idStrut, da, a, aaKeyUnitaDoc,
                UD_AIP_GENERATO_E_NON_GENERATO_NATIVA);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_GENERATO);
        resParziale = executeQueryCalcolo2(idStrut, da, a, aaKeyUnitaDoc, getAnnullQuery2());
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_ANNULL);

        // Inserisco nella tabella MON_CONTA_BY_STATO_CONSERV
        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }

        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutAndDate(strutCalcConsist.getOrgStrut().getIdStrut(),
                strutCalcConsist.getDtRifConta());
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutAndDate(strutCalcConsist.getOrgStrut().getIdStrut(),
                strutCalcConsist.getDtRifConta());

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno3(TmpStrutCalcConsistNew strutCalcConsist) {
        long idStrut = strutCalcConsist.getOrgStrut().getIdStrut();
        long idSubStrut = strutCalcConsist.getOrgSubStrut().getIdSubStrut();
        BigDecimal aaKeyUnitaDoc = strutCalcConsist.getAaKeyUnitaDoc();
        Date da = strutCalcConsist.getDtRifConta();
        Date a = DateUtils.addDays(da, 1);
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        List<MonContaByStatoConservNew> resParziale = executeNativeQueryCalcolo2(idStrut, da, a, aaKeyUnitaDoc,
                idSubStrut, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA2);
        addOrSetContToResultConsistenza2(resParziale, res, TipoConteggio.UD_AIP_GENERATO);
        resParziale = executeQueryCalcolo3(idStrut, da, a, aaKeyUnitaDoc, idSubStrut, getAnnullQuery4());
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_ANNULL);

        // Inserisco nella tabella MON_CONTA_BY_STATO_CONSERV
        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }

        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutAndDateSubStrut(strutCalcConsist.getOrgSubStrut().getIdSubStrut(),
                strutCalcConsist.getDtRifConta());
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutAndDateSubStrut(strutCalcConsist.getOrgSubStrut().getIdSubStrut(),
                strutCalcConsist.getDtRifConta());

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno4(TmpStrutCalcConsistNew strutCalcConsist) {
        long idStrut = strutCalcConsist.getOrgStrut().getIdStrut();
        long idSubStrut = strutCalcConsist.getOrgSubStrut().getIdSubStrut();
        BigDecimal aaKeyUnitaDoc = strutCalcConsist.getAaKeyUnitaDoc();
        Date da = strutCalcConsist.getDtRifConta();
        Date a = DateUtils.addDays(da, 1);
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        List<MonContaByStatoConservNew> resParziale = executeNativeQueryCalcolo3(idStrut, da, a, aaKeyUnitaDoc,
                idSubStrut, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3);
        addOrSetContToResultConsistenza3(resParziale, res);

        // Inserisco nella tabella MON_CONTA_BY_STATO_CONSERV
        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }

        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutAndDateSubStrut(strutCalcConsist.getOrgSubStrut().getIdSubStrut(),
                strutCalcConsist.getDtRifConta());
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutAndDateSubStrut(strutCalcConsist.getOrgSubStrut().getIdSubStrut(),
                strutCalcConsist.getDtRifConta());

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno5(Object[] dateMinMaxAaKey, long idStrut, long idSubStrut) {
        BigDecimal aaKeyUnitaDoc = (BigDecimal) dateMinMaxAaKey[2];
        Date dtMin = (Date) dateMinMaxAaKey[0];
        Date dtMax = (Date) dateMinMaxAaKey[1];
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        executeNativeQueryCalcolo3(idStrut, dtMin, dtMax, aaKeyUnitaDoc, idSubStrut,
                UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3);

        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutDaElab(idSubStrut, dtMax, aaKeyUnitaDoc);
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutDaElab(idSubStrut, dtMax, aaKeyUnitaDoc);

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno6(Object[] dateMinMaxAaKey, long idStrut, long idSubStrut) {
        // Prendo la data minima e massima (record da elaborare) tra i record di quella sotto struttura,
        // aa_key_unita_doc con stato DA_ELABORARE
        // SONO I DATI PASSATI COME PARAMETRI IN INPUT AL METODO
        // Sto considerando una determinata sottostruttura, in un determinato aaKeyUnitaDoc avendo le dt min e max
        BigDecimal aaKeyUnitaDoc = (BigDecimal) dateMinMaxAaKey[2];
        Date dtMin = (Date) dateMinMaxAaKey[0];
        Date dtMax = (Date) dateMinMaxAaKey[1];

        // Esiste per la specifica sotto-struttura e aaKeyUnitaDoc un record in TMP_STRUT
        // con stato ELABORAZIONE_OK? Se sì restituiscimi la data di ultima esecuzione
        Date dataMassimaElaborazioneOK = getDataMassimaElaborazioneOK(idSubStrut, aaKeyUnitaDoc);

        // 1) CASO SOTTOSTRUTTURA GIA' ESISTENTE: GESTIONE MODIFICHE
        if (dataMassimaElaborazioneOK != null) {
            // Controllo se all'interno del periodo ci sono annullamenti
            // Recupero le ud annullate nel periodo in questione: su di essere bisognerà imputare
            // i conteggi degli annullamenti di competenza
            Query q1 = entityManager.createQuery("SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                    + "WHERE unitaDoc.dtAnnul BETWEEN :dtAnnulStart AND :dtAnnulEnd "
                    + "AND unitaDoc.orgSubStrut.idSubStrut = :idSubStrut "
                    + "AND unitaDoc.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            q1.setParameter("dtAnnulStart", dtMin);
            q1.setParameter("dtAnnulEnd", dtMax);
            q1.setParameter("idSubStrut", idSubStrut);
            q1.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
            List<AroUnitaDoc> udAnnulList = (List<AroUnitaDoc>) q1.getResultList(); // unita doc annullate nel periodo

            // GESTIONE UD ANNULLATE
            // PER INSERIMENTO NUOVO O RICALCOLO GIORNATE PASSATE
            if (udAnnulList != null && !udAnnulList.isEmpty()) {
                for (AroUnitaDoc udAnnul : udAnnulList) {
                    // Se la data di annullamento è precedente all'ultima esecuzione, allora
                    // cancello e ricalcolo, ALTRIMENTI, essendo una data successiva,
                    // è come un calcolo nuovo senza dover distinguere
                    if (udAnnul.getDtCreazione().before(dataMassimaElaborazioneOK)) {
                        Set<Date> dateDaRicalcolare = new HashSet<>();
                        // Scorro ogni documento dell'ud annullata
                        for (AroDoc doc : udAnnul.getAroDocs()) {
                            // Recupero la data di creazione documento, che sarà quella da "ricalcolare"
                            Date dtCreazioneDoc = doc.getDtCreazione();
                            Calendar c = Calendar.getInstance();
                            c.setTime(dtCreazioneDoc);
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);
                            dateDaRicalcolare.add(c.getTime());
                        }

                        // Recupero i record di MON_CONTA_BY_STATO_CONSERV_NEW da cancellare
                        List<MonContaByStatoConservNew> contaList = getMonContaByStatoConservNew(idStrut, idSubStrut,
                                aaKeyUnitaDoc, dateDaRicalcolare);
                        // delete
                        for (MonContaByStatoConservNew conta : contaList) {
                            deleteMonConta(conta);
                        }

                        // Ricalcolo i dati
                        for (Date dataDaRicalcolare : dateDaRicalcolare) {
                            // inserimento
                            executeNativeQueryCalcolo3(idStrut, dataDaRicalcolare, dataDaRicalcolare, aaKeyUnitaDoc,
                                    idSubStrut, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3);
                        }

                    }
                    // esaurita la data inserita o ricalcolata, aggiorno TMP_STRUT
                    manageTmpStrutAfterMonConta(idSubStrut, dtMax, aaKeyUnitaDoc);
                }
            }

            executeNativeQueryCalcolo3(idStrut, dtMin, dtMax, aaKeyUnitaDoc, idSubStrut,
                    UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3);
            manageTmpStrutAfterMonConta(idSubStrut, dtMax, aaKeyUnitaDoc);

        } else {
            // 2) CASO "PRIMO GIRO": DI TUTTO SACER O DI SINGOLE SOTTOSTRUTTURE
            final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
            executeNativeQueryCalcolo3(idStrut, dtMin, dtMax, aaKeyUnitaDoc, idSubStrut,
                    UD_AIP_GENERATO_E_NON_GENERATO_NATIVA3);

            manageTmpStrutAfterMonConta(idSubStrut, dtMax, aaKeyUnitaDoc);
        }

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteMonConta(MonContaByStatoConservNew conta) {
        entityManager.remove(conta);
        entityManager.flush();
    }

    public void manageTmpStrutAfterMonConta(long idSubStrut, Date dtMax, BigDecimal aaKeyUnitaDoc) {
        // Elimino dalla tabella temporanea tutti i record precendenti per la struttura trattata,
        deleteAllTmpStrutCalcConsistByStrutDaElab(idSubStrut, dtMax, aaKeyUnitaDoc);
        // Cambiando stato al record della struttura considerata nella data trattata
        updateLastTmpStrutCalcConsistByStrutDaElab(idSubStrut, dtMax, aaKeyUnitaDoc);
    }

    public List<MonContaByStatoConservNew> getMonContaByStatoConservNew(long idStrut, long idSubStrut,
            BigDecimal aaKeyUnitaDoc, Set<Date> dtToManage) {
        // aa
        if (!dtToManage.isEmpty()) {
            String queryString = "SELECT conta FROM MonContaByStatoConservNew conta "
                    + "WHERE conta.orgStrut.idStrut = :idStrut " + "AND conta.orgSubStrut.idSubStrut = :idSubStrut "
                    + "AND conta.aaKeyUnitaDoc = :aaKeyUnitaDoc " + "AND conta.dtRifConta IN :dtRifConta ";
            Query query = entityManager.createQuery(queryString);
            query.setParameter("idSubStrut", idSubStrut);
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
            query.setParameter("idStrut", idStrut);

            List<Date> mainList = new ArrayList<>();
            mainList.addAll(dtToManage);
            query.setParameter("dtRifConta", mainList);

            return (List<MonContaByStatoConservNew>) query.getResultList();
        }
        return new ArrayList<>();
    }

    public List<MonContaByStatoConservNew> getMonContaByStatoConservNew2(Object[] dtToManage) {
        // aa
        /* TODO controllare senza truncate */
        String queryString = "SELECT conta FROM MonContaByStatoConservNew conta "
                + "WHERE conta.dtRifConta = :dtRifConta " + "AND conta.orgSubStrut.idSubStrut = :idSubStrut ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idSubStrut", (Long) dtToManage[0]);
        query.setParameter("dtRifConta", (Date) dtToManage[1]);

        return (List<MonContaByStatoConservNew>) query.getResultList();
    }

    public void getAnnulProva() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, 2011);
        start.set(Calendar.MONTH, Calendar.DECEMBER);
        start.set(Calendar.DATE, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, 2021);
        end.set(Calendar.MONTH, Calendar.OCTOBER);
        end.set(Calendar.DATE, 7);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        executeQueryCalcolo(3323, start.getTime(), end.getTime(), getAnnullQuery());
    }

    /**
     * Metodo di prova per inserimento in MON_CONTA_BY_STATO_CONSERV_NEW con date e struttura cablate
     *
     * @throws IOException
     *             eccezione nell'inserimento dei totali del metodo di prova
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiornoPROVA() throws IOException {
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        Calendar da = Calendar.getInstance();
        Calendar a = Calendar.getInstance();
        da.set(2011, Calendar.DECEMBER, 01, 0, 0, 0);
        a.set(2021, Calendar.OCTOBER, 13, 23, 59, 59);
        List<MonContaByStatoConservNew> resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(),
                UD_AIP_GENERATO);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_GENERATO);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(), UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(), UD_AIP_NON_GENERATO_AIP_IN_AGG);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_AIP_IN_AGG);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(), UD_AIP_NON_GENERATO_IN_VOL_CONS);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_NON_GENERATO_IN_VOLUME_CONS);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(), getAnnullQuery());
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_ANNULL);

        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }
    }

    private void addOrSetContToResultConsistenza(List<MonContaByStatoConservNew> cont,
            Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res, TipoConteggio tipoConteggio) {
        MonContaByStatoConservNew temp;
        for (MonContaByStatoConservNew i : cont) {
            if ((temp = res.get(i)) == null) {
                res.put(i, i);
            } else {
                switch (tipoConteggio) {
                case UD_AIP_GENERATO:
                    temp.setNiCompAipGenerato(temp.getNiCompAipGenerato().add(i.getNiCompAipGenerato()));
                    break;
                case UD_ANNULL:
                    temp.setNiCompAnnul(i.getNiCompAnnul());
                    break;
                case UD_AIP_NON_GENERATO_AIP_IN_AGG:
                    temp.setNiCompAipInAggiorn(temp.getNiCompAipInAggiorn().add(i.getNiCompAipInAggiorn()));
                    break;
                case UD_AIP_NON_GENERATO_IN_VOLUME_CONS:
                    temp.setNiCompInVolume(temp.getNiCompInVolume().add(i.getNiCompInVolume()));
                    break;
                case UD_AIP_NON_GENERATO_PRESA_IN_CARICO:
                    temp.setNiCompPresaInCarico(temp.getNiCompPresaInCarico().add(i.getNiCompPresaInCarico()));
                    break;
                }
            }
        }
    }

    private void addOrSetContToResultConsistenza2(List<MonContaByStatoConservNew> cont,
            Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res, TipoConteggio tipoConteggio) {
        MonContaByStatoConservNew temp;
        for (MonContaByStatoConservNew i : cont) {
            if ((temp = res.get(i)) == null) {
                res.put(i, i);
            } else {
                switch (tipoConteggio) {
                case UD_AIP_GENERATO:
                    temp.setNiCompAipGenerato(temp.getNiCompAipGenerato().add(i.getNiCompAipGenerato()));
                    temp.setNiCompAipInAggiorn(temp.getNiCompAipInAggiorn().add(i.getNiCompAipInAggiorn()));
                    temp.setNiCompInVolume(temp.getNiCompInVolume().add(i.getNiCompInVolume()));
                    temp.setNiCompPresaInCarico(temp.getNiCompPresaInCarico().add(i.getNiCompPresaInCarico()));
                    break;
                case UD_ANNULL:
                    temp.setNiCompAnnul(i.getNiCompAnnul());
                    break;
                }
            }
        }
    }

    private void addOrSetContToResultConsistenza3(List<MonContaByStatoConservNew> cont,
            Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res) {
        MonContaByStatoConservNew temp;
        for (MonContaByStatoConservNew i : cont) {
            if ((temp = res.get(i)) == null) {
                res.put(i, i);
            } else {
                temp.setNiCompAipGenerato(temp.getNiCompAipGenerato().add(i.getNiCompAipGenerato()));
                temp.setNiCompAipInAggiorn(temp.getNiCompAipInAggiorn().add(i.getNiCompAipInAggiorn()));
                temp.setNiCompInVolume(temp.getNiCompInVolume().add(i.getNiCompInVolume()));
                temp.setNiCompPresaInCarico(temp.getNiCompPresaInCarico().add(i.getNiCompPresaInCarico()));
                temp.setNiCompAnnul(i.getNiCompAnnul());
            }
        }
    }

    // queryString
    private List<MonContaByStatoConservNew> executeQueryCalcolo(long idStrut, Date dataCalcoloDa, Date dataCalcoloA,
            String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idStrut", idStrut);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        return query.getResultList();
    }// niUnitaDocVers
     // idUserIam

    private List<MonContaByStatoConservNew> executeQueryCalcolo2(long idStrut, Date dataCalcoloDa, Date dataCalcoloA,
            BigDecimal aaKeyUnitaDoc, String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idStrut", idStrut);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        return query.getResultList();
    }

    private List<MonContaByStatoConservNew> executeQueryCalcolo3(long idStrut, Date dataCalcoloDa, Date dataCalcoloA,
            BigDecimal aaKeyUnitaDoc, long idSubStrut, String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idStrut", idStrut);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("idSubStrut", idSubStrut);
        return query.getResultList();
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo(long idStrut, Date dataCalcoloDa,
            Date dataCalcoloA, BigDecimal aaKeyUnitaDoc, String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idStrut);
        query.setParameter(2, dataCalcoloDa);
        query.setParameter(3, dataCalcoloA);
        query.setParameter(4, aaKeyUnitaDoc.longValue());
        List<Object[]> objList = query.getResultList();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        for (Object[] obj : objList) {
            MonContaByStatoConservNew conserv = new MonContaByStatoConservNew();
            conserv.setDtRifConta((Date) obj[0]);
            conserv.setIdStrut((BigDecimal) obj[1]);
            conserv.setIdSubStrut((BigDecimal) obj[2]);
            conserv.setAaKeyUnitaDoc((BigDecimal) obj[3]);
            conserv.setIdDecRegistroUnitaDoc(((BigDecimal) obj[4]).longValue());
            conserv.setIdDecTipoUnitaDoc(((BigDecimal) obj[5]).longValue());
            conserv.setIdDecTipoDoc(((BigDecimal) obj[6]).longValue());
            conserv.setNiCompAipGenerato((BigDecimal) obj[7]);
            conserv.setNiCompAipInAggiorn((BigDecimal) obj[8]);
            conserv.setNiCompPresaInCarico((BigDecimal) obj[9]);
            conserv.setNiCompInVolume((BigDecimal) obj[10]);
            conserv.setNiCompAnnul(BigDecimal.ZERO);
            lista.add(conserv);
        }
        return lista;
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo2(long idStrut, Date dataCalcoloDa,
            Date dataCalcoloA, BigDecimal aaKeyUnitaDoc, long idSubStrut, String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idSubStrut);
        query.setParameter(4, dataCalcoloDa);
        query.setParameter(5, dataCalcoloA);
        query.setParameter(2, aaKeyUnitaDoc.longValue());
        query.setParameter(3, idStrut);
        List<Object[]> objList = query.getResultList();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        for (Object[] obj : objList) {
            MonContaByStatoConservNew conserv = new MonContaByStatoConservNew();
            conserv.setDtRifConta((Date) obj[0]);
            conserv.setIdStrut((BigDecimal) obj[1]);
            conserv.setIdSubStrut((BigDecimal) obj[2]);
            conserv.setAaKeyUnitaDoc((BigDecimal) obj[3]);
            conserv.setIdDecRegistroUnitaDoc(((BigDecimal) obj[4]).longValue());
            conserv.setIdDecTipoUnitaDoc(((BigDecimal) obj[5]).longValue());
            conserv.setIdDecTipoDoc(((BigDecimal) obj[6]).longValue());
            conserv.setNiCompAipGenerato((BigDecimal) obj[7]);
            conserv.setNiCompAipInAggiorn((BigDecimal) obj[8]);
            conserv.setNiCompPresaInCarico((BigDecimal) obj[9]);
            conserv.setNiCompInVolume((BigDecimal) obj[10]);
            conserv.setNiCompAnnul(BigDecimal.ZERO);
            lista.add(conserv);
        }
        return lista;
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo3(long idStrut, Date dataCalcoloDa,
            Date dataCalcoloA, BigDecimal aaKeyUnitaDoc, long idSubStrut, String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idSubStrut);
        query.setParameter(2, aaKeyUnitaDoc.longValue());
        query.setParameter(3, idStrut);
        query.setParameter(4, dataCalcoloDa);
        query.setParameter(5, dataCalcoloA);

        query.executeUpdate();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        return lista;
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo3WithExcl(long idStrut, Date dataCalcoloDa,
            Date dataCalcoloA, BigDecimal aaKeyUnitaDoc, long idSubStrut, String queryString,
            List<Date> dateToExclude) {

        String queryS = "Insert /*+ append */ into "
                + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, "
                + "aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
                + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico, "
                + "ni_comp_in_volume, ni_comp_annul) "
                + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, "
                + "id_registro_unita_doc, id_tipo_unita_doc, "
                + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico, ni_comp_volume, "
                + "ni_comp_annul " + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW "
                + "WHERE id_sub_strut = ?1 AND aa_key_unita_doc = ?2 AND dt_creazione BETWEEN ?4 AND ?5 AND id_strut = ?3 ";

        String stringaAggiunta = "";
        int i = 6;

        for (Date d : dateToExclude) {
            stringaAggiunta = stringaAggiunta + " AND dt_creazione <> ?" + i;
            i++;
        }
        queryS = queryS + stringaAggiunta;

        Query query = entityManager.createNativeQuery(queryS);
        query.setParameter(1, idSubStrut);
        query.setParameter(2, aaKeyUnitaDoc.longValue());
        query.setParameter(3, idStrut);
        query.setParameter(4, dataCalcoloDa);
        query.setParameter(5, dataCalcoloA);
        int a = 6;
        for (Date d : dateToExclude) {
            query.setParameter(a, d);
            a++;
        }

        query.executeUpdate();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        return lista;
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo4(Date dataCalcoloDa, Date dataCalcoloA,
            String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, dataCalcoloDa);
        query.setParameter(2, dataCalcoloA);

        query.executeUpdate();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        return lista;
    }

    private List<MonContaByStatoConservNew> executeNativeQueryCalcolo4(long idSubStrut, Date dataCalcolo,
            String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idSubStrut);
        query.setParameter(2, dataCalcolo);

        query.executeUpdate();

        List<MonContaByStatoConservNew> lista = new ArrayList<>();

        return lista;
    }

    public void insertMonTipoUnitaDocUserVers(Long idTipoUnitaDoc, Long idUserIam, Date dtRifConta,
            BigDecimal niUnitaDocVers) {
        MonTipoUnitaDocUserVers tipoUnitaDocUserVers = new MonTipoUnitaDocUserVers();
        tipoUnitaDocUserVers.setDecTipoUnitaDoc(entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc));
        tipoUnitaDocUserVers.setIamUser(entityManager.find(IamUser.class, idUserIam));
        tipoUnitaDocUserVers.setDtRifConta(dtRifConta);
        tipoUnitaDocUserVers.setNiUnitaDocVers(niUnitaDocVers);
        entityManager.persist(tipoUnitaDocUserVers);
        entityManager.flush();
    }

    /**
     * Ricavo le strutture che, in un dato giorno, hanno effettuato versamenti o annullamenti e le salvo in tabella
     * temporanea con data di riferimento controllando che non esista già il record
     *
     * @param firstTime
     *            true, se è il primo giro di calcolo
     * @param start
     *            data di inizio intervallo temporale
     * @param end
     *            data di fine intervallo temporale
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaOld(boolean firstTime, Date start, Date end) {

        if (firstTime) {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query1 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_creazione "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) " + "JOIN Aro_Doc doc "
                            + "on (doc.id_doc = strutDoc.id_doc) " + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) " + "where "
                            + "not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query1.executeUpdate();
            // ID_STRUT
            Query query2 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_annull " + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_annull "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) " + "JOIN Aro_Doc doc "
                            + "on (doc.id_doc = strutDoc.id_doc) " + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) " + "where doc.ti_Annul = 'ANNULLAMENTO' "
                            + "and not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query2.executeUpdate();

        } else {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query1 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_creazione "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) " + "JOIN Aro_Doc doc "
                            + "on (doc.id_doc = strutDoc.id_doc) " + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) " + "where "
                            + "not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "AND trunc(doc.dt_creazione) > ?1 AND trunc(doc.dt_creazione) <= ?2 "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query1.setParameter(1, start);
            query1.setParameter(2, end);
            query1.executeUpdate();

            Query query2 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_creazione "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) " + "JOIN Aro_Doc doc "
                            + "on (doc.id_doc = strutDoc.id_doc) " + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) " + "where doc.ti_Annul = 'ANNULLAMENTO' "
                            + "and not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "AND trunc(doc.dt_creazione) > ?1 AND trunc(doc.dt_creazione) <= ?2 "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query2.setParameter(1, start);
            query2.setParameter(2, end);
            query2.executeUpdate();
        }
        entityManager.flush();
    }

    /**
     * VERSIONE JPA SENZA CONSIDERARE AA_KEY_UNITA_DOC
     *
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se non si tratta del
     * primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime
     *            true, se è il primo giro di calcolo
     * @param start
     *            data di inizio intervallo temporale
     * @param end
     *            data di fine intervallo temporale
     * @param idStrut
     *            la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenza(boolean firstTime, Date start, Date end,
            long idStrut) {
        List<Object[]> objList;
        if (firstTime) {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query = entityManager
                    .createQuery("SELECT doc.idStrut, " + "'DA_ELABORARE', " + "FUNC ('TRUNC', doc.dtCreazione) "
                            + "FROM AroDoc doc " + "WHERE NOT EXISTS " + "(SELECT tmp.orgStrut.idStrut "
                            + "FROM TmpStrutCalcConsistNew tmp " + "WHERE tmp.orgStrut.idStrut = doc.idStrut "
                            + "AND tmp.dtRifConta = FUNC ('TRUNC', doc.dtCreazione)) " + "AND doc.idStrut = :idStrut "
                            + "GROUP BY doc.idStrut, FUNC ('TRUNC', doc.dtCreazione)");

            query.setParameter("idStrut", BigDecimal.valueOf(idStrut));
            objList = query.getResultList();
        } else {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query = entityManager.createQuery("SELECT doc.idStrut, " + "'DA_ELABORARE', "
                    + "FUNC ('TRUNC', doc.dtCreazione) " + "FROM AroDoc doc " + "WHERE NOT EXISTS "
                    + "(SELECT tmp.orgStrut.idStrut " + "FROM TmpStrutCalcConsistNew tmp "
                    + "WHERE tmp.orgStrut.idStrut = doc.idStrut "
                    + "AND tmp.dtRifConta = FUNC ('TRUNC', doc.dtCreazione)) " + "AND doc.idStrut = :idStrut "
                    + "AND FUNC ('TRUNC', doc.dtCreazione) > :start  AND FUNC ('TRUNC', doc.dtCreazione) <= :stop "
                    + "GROUP BY doc.idStrut, FUNC ('TRUNC', doc.dtCreazione)");

            query.setParameter("idStrut", BigDecimal.valueOf(idStrut));
            query.setParameter("start", start);
            query.setParameter("stop", end);
            objList = query.getResultList();
        }
        for (Object[] obj : objList) {
            TmpStrutCalcConsistNew calc = new TmpStrutCalcConsistNew();
            calc.setDtRifConta((Date) obj[2]);
            calc.setOrgStrut(entityManager.find(OrgStrut.class, ((BigDecimal) obj[0]).longValue()));
            calc.setTiStatoElab((String) obj[1]);
            entityManager.persist(calc);
        }
        entityManager.flush();
    }

    /**
     * VERSIONE QUERY NATIVE OTTIMIZZATE (AA_KEY_UNITA_DOC FITTIZIO)
     *
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se non si tratta del
     * primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime
     *            true, se è il primo giro di calcolo
     * @param start
     *            data di inizio intervallo temporale
     * @param end
     *            data di fine intervallo temporale
     * @param idStrut
     *            la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNative(boolean firstTime, Date start, Date end,
            long idStrut) {
        if (firstTime) {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query = entityManager.createNativeQuery("Insert into /*+ append */ "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO "
                    + "FROM " + "(SELECT doc.id_Strut AS id_Strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, 1900 AS ANNO " + "FROM Aro_Doc doc "
                    + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut " + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut " + "AND tmp.dt_Rif_Conta = trunc(doc.dt_Creazione)) "
                    + "AND id_Strut = ?1 " + "GROUP BY doc.id_Strut, TRUNC(doc.dt_Creazione))");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();
        } else {
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO "
                    + "FROM(SELECT doc.id_Strut AS id_Strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, 1900 AS ANNO " + "FROM Aro_Doc doc "
                    + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut " + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut " + "AND tmp.dt_Rif_Conta = TRUNC (doc.dt_Creazione)) "
                    + "AND id_Strut = ?1 " + "AND trunc(doc.dt_creazione) > ?2  AND trunc(doc.dt_creazione) <= ?3 "
                    + "GROUP BY doc.id_Strut, TRUNC (doc.dt_creazione))");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.executeUpdate();
        }

        entityManager.flush();
    }

    /**
     * VERSIONE QUERY NATIVE OTTIMIZZATE CON RECUPERO AA_KEY_UNITA_DOC E ID_SUB_STRUT
     *
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se non si tratta del
     * primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime
     *            true, se è il primo giro di calcolo
     * @param start
     *            data di inizio intervallo temporale
     * @param end
     *            data di fine intervallo temporale
     * @param idStrut
     *            la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNative2(boolean firstTime, Date start, Date end,
            long idStrut) {
        if (firstTime) {
            /* VERSIONE ARO_DOC/ARO_UNITA_DOC */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT doc.id_Strut AS id_strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, "
                    + "unita_doc.AA_KEY_UNITA_DOC AS ANNO, unita_doc.id_sub_Strut AS id_sub_strut "
                    + "FROM ARO_UNITA_DOC unita_doc " + "JOIN ARO_DOC doc "
                    + "ON (unita_doc.id_unita_doc = doc.id_unita_doc) " + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut AND tmp.id_sub_strut = unita_doc.id_sub_strut "
                    + "AND tmp.dt_Rif_Conta = trunc(doc.dt_Creazione)) " + "AND doc.id_Strut = ?1 "
                    + "GROUP BY doc.id_Strut, trunc(doc.dt_Creazione), unita_doc.AA_KEY_UNITA_DOC, unita_doc.id_sub_Strut)");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();

            Query queryAnnul = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, "
                    + "AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT VRS.id_Strut AS id_strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers) AS dt_creazione, "
                    + "VRS.AA_KEY_UNITA_DOC AS ANNO, sub_strut.id_sub_strut "
                    + "FROM sacer.ARO_RICH_ANNUL_VERS rich_annul " + "JOIN sacer.ARO_STATO_RICH_ANNUL_VERS stato_rich "
                    + "ON (stato_rich.id_stato_rich_annul_vers = " + "rich_annul.id_stato_rich_annul_vers_cor) "
                    + "JOIN sacer.ARO_ITEM_RICH_ANNUL_VERS VRS "
                    + "ON (vrs.id_rich_annul_vers = rich_annul.id_rich_annul_vers) " + "JOIN ORG_STRUT strut "
                    + "ON (strut.id_strut = VRS.ID_STRUT) JOIN ORG_SUB_STRUT sub_strut "
                    + "ON(strut.id_strut = sub_strut.id_strut) " + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM sacer.Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = VRS.id_Strut AND tmp.id_sub_strut = sub_strut.id_sub_strut "
                    + "AND tmp.dt_Rif_Conta = " + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers)) "
                    + "AND VRS.id_Strut = ?1 and rich_annul.id_Strut=?1 "
                    + "AND vrs.ti_item_rich_annul_vers = 'UNI_DOC' " + "AND vrs.ti_stato_item = 'ANNULLATO' "
                    + "AND stato_rich.ti_stato_rich_annul_vers = 'EVASA' " + "GROUP BY VRS.id_Strut, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers), "
                    + "VRS.AA_KEY_UNITA_DOC, sub_strut.id_sub_Strut)");

            queryAnnul.setParameter(1, BigDecimal.valueOf(idStrut));
            queryAnnul.executeUpdate();

        } else {
            /* VERSIONE ARO_DOC/ARO_UNITA_DOC */
            /* Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è stato un versamento */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT doc.id_Strut AS id_strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, "
                    + "unita_doc.AA_KEY_UNITA_DOC AS ANNO, unita_doc.id_sub_strut " + "FROM ARO_UNITA_DOC unita_doc "
                    + "JOIN ARO_DOC doc " + "ON (unita_doc.id_unita_doc = doc.id_unita_doc) " + "WHERE NOT EXISTS "
                    + "(SELECT tmp.id_Strut " + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut " + "AND tmp.id_sub_strut = unita_doc.id_sub_strut "
                    + "AND tmp.dt_Rif_Conta = TRUNC (doc.dt_creazione)) " + "AND doc.id_Strut = ?1 "
                    + "AND doc.DT_CREAZIONE >= ?2  AND doc.DT_CREAZIONE < ?3 "
                    + "GROUP BY doc.id_Strut, TRUNC (doc.dt_creazione), unita_doc.AA_KEY_UNITA_DOC, unita_doc.id_sub_strut)");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.executeUpdate();

            Query queryAnnul = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT VRS.id_Strut AS id_strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers) AS dt_creazione, "
                    + "VRS.AA_KEY_UNITA_DOC AS ANNO, sub_strut.id_sub_strut "
                    + "FROM sacer.ARO_RICH_ANNUL_VERS rich_annul " + "JOIN sacer.ARO_STATO_RICH_ANNUL_VERS stato_rich "
                    + "ON (stato_rich.id_stato_rich_annul_vers = " + "rich_annul.id_stato_rich_annul_vers_cor) "
                    + "JOIN sacer.ARO_ITEM_RICH_ANNUL_VERS VRS "
                    + "ON (vrs.id_rich_annul_vers = rich_annul.id_rich_annul_vers) " + "JOIN ORG_STRUT strut "
                    + "ON (strut.id_strut = VRS.ID_STRUT) " + "JOIN ORG_SUB_STRUT sub_strut "
                    + "ON(strut.id_strut = sub_strut.id_strut) " + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM sacer.Tmp_Strut_Calc_Consist_New tmp " + "WHERE tmp.id_Strut = VRS.id_Strut "
                    + "AND tmp.id_sub_strut = sub_strut.id_sub_strut " + "AND tmp.dt_Rif_Conta = "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers)) "
                    + "AND VRS.id_Strut = ?1 and rich_annul.id_Strut=?1 "
                    + "AND stato_rich.DT_REG_STATO_RICH_ANNUL_VERS >= ?2  AND stato_rich.DT_REG_STATO_RICH_ANNUL_VERS < ?3 "
                    + "AND vrs.ti_item_rich_annul_vers = 'UNI_DOC' " + "AND vrs.ti_stato_item = 'ANNULLATO' "
                    + "AND stato_rich.ti_stato_rich_annul_vers = 'EVASA' " + "GROUP BY VRS.id_Strut, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers), "
                    + "VRS.AA_KEY_UNITA_DOC, sub_strut.id_sub_strut)");

            queryAnnul.setParameter(1, BigDecimal.valueOf(idStrut));
            queryAnnul.setParameter(2, start);
            queryAnnul.setParameter(3, end);
            queryAnnul.executeUpdate();
        }
        entityManager.flush();
    }

    /**
     * VERSIONE QUERY NATIVE OTTIMIZZATE CON RECUPERO AA_KEY_UNITA_DOC E ID_SUB_STRUT
     *
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se non si tratta del
     * primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime
     *            true, se è il primo giro di calcolo
     * @param start
     *            data di inizio intervallo temporale
     * @param end
     *            data di fine intervallo temporale
     * @param idStrut
     *            la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista(boolean firstTime, Date start,
            Date end, long idStrut) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();
        } else {

            // per questa struttura considerata recuperare la data di ultimo conteggio (dt_rif_conta)
            // da TMP_STRUT
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 "
                    + "AND vista.DT_CREAZIONE >= ?2  AND vista.DT_CREAZIONE < ?3 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.executeUpdate();
        }
        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista2(boolean firstTime, Date start,
            Date end, long idStrut) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 ");
            // + "AND vista.DT_CREAZIONE < ?2 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            // query.setParameter(2, end);
            query.executeUpdate();
        } else {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 "
                    + "AND vista.DT_CREAZIONE > ?2  AND vista.DT_CREAZIONE < ?3 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.executeUpdate();
        }
        entityManager.flush();
    }

    public List<OrgStrut> getOrgStrutList() {
        return entityManager.createQuery("SELECT strut FROM OrgStrut strut ").getResultList();
    }

    public List<OrgSubStrut> getOrgSubStrutList() {
        return entityManager.createQuery("SELECT subStrut FROM OrgSubStrut subStrut ").getResultList();
    }

    public Date getMinDateStrutPerCalcolo(long idStrut) {
        Query q = entityManager.createQuery("SELECT MAX(tmp.dtExecJob) FROM TmpStrutCalcConsistNew tmp "
                + "WHERE tmp.orgStrut.idStrut = :idStrut " + "AND tmp.tiStatoElab = 'ELABORAZIONE_OK' ");
        q.setParameter("idStrut", idStrut);
        Date start = get30Novembre2011();
        if (q.getSingleResult() != null) {
            start = (Date) q.getSingleResult();
        }
        return start;
    }

    public Date getDataMassimaElaborazioneOK(long idSubStrut, BigDecimal aaKeyUnitaDoc) {
        Query q = entityManager.createQuery("SELECT MAX(tmp.dtExecJob) FROM TmpStrutCalcConsistNew tmp "
                + "WHERE tmp.orgSubStrut.idSubStrut = :idSubStrut " + "AND tmp.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + "AND tmp.tiStatoElab = 'ELABORAZIONE_OK' ");
        q.setParameter("idSubStrut", idSubStrut);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        if (q.getSingleResult() != null) {
            return (Date) q.getSingleResult();
        }
        return null;

    }

    public List<Object[]> getRecordMinMaxByAnno(long idSubStrut, BigDecimal aaKeyUnitaDoc) {
        String queryString = "SELECT MIN(u.dtRifConta), MAX(u.dtRifConta) " + "FROM TmpStrutCalcConsistNew u "
                + "WHERE u.tiStatoElab IN ('DA_ELABORARE', 'ELABORAZIONE_KO') "
                + "AND u.orgSubStrut.idSubStrut = :idSubStrut " + "AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idSubStrut", idSubStrut);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVistaByDoppioId(boolean firstTime,
            Date start, Date end, Long[] strutSubStrut) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 "
                    + "AND vista.id_sub_strut = ?3 " + "AND vista.DT_CREAZIONE < ?2 ");

            query.setParameter(1, BigDecimal.valueOf(strutSubStrut[0]));
            query.setParameter(2, end);
            query.setParameter(3, BigDecimal.valueOf(strutSubStrut[1]));
            query.executeUpdate();
        } else {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 "
                    + "AND vista.id_sub_strut = ?4 " + "AND vista.DT_CREAZIONE > ?2  AND vista.DT_CREAZIONE < ?3 ");

            query.setParameter(1, BigDecimal.valueOf(strutSubStrut[0]));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.setParameter(4, BigDecimal.valueOf(strutSubStrut[1]));
            query.executeUpdate();
        }
        entityManager.flush();
    }

    public List<Long[]> getIdStrutIdSubStrutList() {
        return (List<Long[]>) entityManager
                .createQuery("SELECT subStrut.orgStrut.idStrut, subStrut.idSubStrut FROM OrgSubStrut subStrut ")
                .getResultList();
    }

    public Date getStessaDataUltimaEsecuzione() {
        Query q = entityManager.createQuery("SELECT DISTINCT strut.dtExecJob FROM TmpStrutCalcConsistNew strut ");
        List<Date> lista = (List<Date>) q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            if (lista.size() == 1) {
                return lista.get(0);
            }
            return null;
        }
        return get30Novembre2011();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista2NoStrut(boolean firstTime,
            Date start, Date end) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista ");
            query.executeUpdate();
        } else {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista "
                    + "WHERE vista.DT_CREAZIONE > ?1  AND vista.DT_CREAZIONE < ?2 ");

            query.setParameter(1, start);
            query.setParameter(2, end);
            query.executeUpdate();
        }
        entityManager.flush();
    }

    /* TODO: GESTIRE IL CASO DI JOB SPENTO OLTRE UN MESE */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno7(boolean firstTime, Date dtRifContaDa, Date dtRifContaA) {

        // Controllo di non aver già eseguito i calcoli per questo giorno
        Calendar a = Calendar.getInstance();
        a.set(Calendar.HOUR_OF_DAY, 0);
        a.set(Calendar.MINUTE, 0);
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);
        // a.add(Calendar.DATE, -1);
        if (!dtRifContaA.equals(a.getTime())) {

            // Controllo se è il primo giro
            // Controllo se all'interno del periodo ci sono annullamenti
            // Recupero le ud annullate nel periodo in questione: su di essere bisognerà imputare
            // i conteggi degli annullamenti di competenza

            /* TODO FARE QUERY NATIVA CON PARALLEL */
            Query q1 = entityManager.createQuery("SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                    + "WHERE unitaDoc.dtAnnul BETWEEN :dtAnnulStart AND :dtAnnulEnd ");
            q1.setParameter("dtAnnulStart", dtRifContaDa);
            q1.setParameter("dtAnnulEnd", dtRifContaA);
            List<AroUnitaDoc> udAnnulList = (List<AroUnitaDoc>) q1.getResultList(); // unita doc annullate nel periodo

            Set<Object[]> dateDaRicalcolare = new HashSet<>();
            // Se nel periodo ci sono annullamenti, mi salvo SOLO le date da "ricalcolare"
            // ovvero quelle con dtAnnul antecedente alla data da cui riparto con l'elaborazione del JOB
            if (udAnnulList != null && !udAnnulList.isEmpty()) {
                for (AroUnitaDoc udAnnul : udAnnulList) {
                    // Se la data di annullamento è precedente all'ultima esecuzione, allora
                    // cancello e ricalcolo, ALTRIMENTI, essendo una data successiva,
                    // è come un calcolo nuovo senza dover distinguere
                    if (udAnnul.getDtCreazione().before(dtRifContaDa)) {

                        // Scorro ogni documento dell'ud annullata
                        for (AroDoc doc : udAnnul.getAroDocs()) {
                            // Recupero la data di creazione documento, che sarà quella da "ricalcolare"
                            Date dtCreazioneDoc = doc.getDtCreazione();
                            Calendar c = Calendar.getInstance();
                            c.setTime(dtCreazioneDoc);
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);
                            Object[] dataDaRicalcolare = new Object[2];
                            dataDaRicalcolare[0] = udAnnul.getOrgSubStrut().getIdSubStrut();
                            dataDaRicalcolare[1] = c.getTime();
                            dateDaRicalcolare.add(dataDaRicalcolare);
                        }
                    }
                }
            }

            // Calcolo tutto il periodo
            executeNativeQueryCalcolo4(dtRifContaDa, dtRifContaA, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4);

            if (!dateDaRicalcolare.isEmpty()) {
                // Ricalcolo nel periodo i giorni di annullamento
                // Recupero i record di MON_CONTA_BY_STATO_CONSERV_NEW da cancellare
                for (Object[] dataDaRicalcolare : dateDaRicalcolare) {
                    List<MonContaByStatoConservNew> contaList = getMonContaByStatoConservNew2(dataDaRicalcolare);
                    // delete
                    for (MonContaByStatoConservNew conta : contaList) {
                        deleteMonConta(conta);
                        executeNativeQueryCalcolo4((Long) dataDaRicalcolare[0], (Date) dataDaRicalcolare[1],
                                UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_SINGOLO_GIORNO);
                    }
                }
            }
        }

        if (dtRifContaDa.after(dtRifContaA)) {
            dtRifContaDa = dtRifContaA;
        }
        // Salvo il record del JOB in Log_Elab_Consist
        insertLogElabConsist(dtRifContaDa, dtRifContaA);

        entityManager.flush();
    }

    public void insertLogElabConsist(Date dtRifContaDa, Date dtRifContaA) {
        LogElabConsist elabConsist = new LogElabConsist();
        Calendar dtElabConsist = Calendar.getInstance();
        dtElabConsist.set(Calendar.HOUR_OF_DAY, 0);
        dtElabConsist.set(Calendar.MINUTE, 0);
        dtElabConsist.set(Calendar.SECOND, 0);
        dtElabConsist.set(Calendar.MILLISECOND, 0);
        elabConsist.setDtElabConsist(dtElabConsist.getTime());
        elabConsist.setDtRifContaDa(dtRifContaDa);
        elabConsist.setDtRifContaA(dtRifContaA);
        entityManager.persist(elabConsist);
    }

    public void updateDtExecJob(Calendar endJob) {
        Query q = entityManager.createNativeQuery(
                "update tmp_strut_calc_consist_new " + "SET dt_exec_job = ?1 WHERE ti_stato_elab = 'ELABORAZIONE_OK' ");
        q.setParameter(1, endJob.getTime());
        q.executeUpdate();
    }

    public Calendar getUltimaDtRifContaA() {
        String queryString = "SELECT MAX(u.dtRifContaA) FROM LogElabConsist u ";
        Query query = entityManager.createQuery(queryString);
        List<Date> d = (List<Date>) query.getResultList();
        Calendar cal = Calendar.getInstance();
        if (d.get(0) == null) {
            // Imposto la data all'1 dicembre 2011
            cal.set(Calendar.YEAR, 2011);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.setTime(d.get(0));
            cal.add(Calendar.DATE, 1);
        }
        return cal;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eseguiPrimoGiroByRange(Date da, Date a) {
        executeNativeQueryCalcolo4(da, a, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4);
        insertLogElabConsist(da, a);
    }

}
