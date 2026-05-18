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

package it.eng.parer.job.calcoloConsistenza.ejb;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogElabConsist;
import it.eng.parer.entity.MonContaByStatoConservNew;
import it.eng.parer.entity.MonContaByStatoConservNew.TipoConteggio;
import it.eng.parer.entity.MonTipoUnitaDocUserVers;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "CalcoloConsistenzaHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloConsistenzaHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    Logger log = LoggerFactory.getLogger(CalcoloConsistenzaHelper.class);

    private static final String UD_AIP_GENERATO = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " nvl(count(comp.idStrut), 0), 'UD_AIP_GENERATO' ) " + " FROM AroCompDoc comp "
            + " JOIN comp.aroStrutDoc strutDoc " + " JOIN strutDoc.aroDoc doc "
            + " JOIN doc.aroUnitaDoc ud, " + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 "
            + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('AIP_GENERATO', 'AIP_FIRMATO', 'VERSAMENTO_IN_ARCHIVIO', 'IN_ARCHIVIO', 'IN_CUSTODIA') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut,ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_PRESA_IN_CARICO = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " nvl(count(comp.idStrut), 0), 'UD_AIP_NON_GENERATO_PRESA_IN_CARICO') "
            + " FROM AroCompDoc comp " + " JOIN comp.aroStrutDoc strutDoc "
            + " JOIN strutDoc.aroDoc doc " + " JOIN doc.aroUnitaDoc ud, "
            + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 " + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('PRESA_IN_CARICO') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY trunc(doc.dtCreazione), ud.idOrgStrut,ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_AIP_IN_AGG = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, " + " nvl(count(comp.idStrut), 0), "
            + " 'UD_AIP_NON_GENERATO_AIP_IN_AGG') " + " FROM AroCompDoc comp "
            + " JOIN comp.aroStrutDoc strutDoc " + " JOIN strutDoc.aroDoc doc "
            + " JOIN doc.aroUnitaDoc ud, " + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 "
            + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('AIP_IN_AGGIORNAMENTO') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY trunc(doc.dtCreazione), ud.idOrgStrut,ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    private static final String UD_AIP_NON_GENERATO_IN_VOL_CONS = "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
            + " trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, " + " nvl(count(comp.idStrut), 0), "
            + " 'UD_AIP_NON_GENERATO_IN_VOLUME_CONS') " + " FROM AroCompDoc comp "
            + " JOIN comp.aroStrutDoc strutDoc " + " JOIN strutDoc.aroDoc doc "
            + " JOIN doc.aroUnitaDoc ud, " + " AroDoc docPrinc JOIN docPrinc.aroUnitaDoc ud2 "
            + " WHERE ud.idOrgStrut = :idStrut "
            + " AND ud.tiStatoConservazione IN ('IN_VOLUME_DI_CONSERVAZIONE', 'AIP_DA_GENERARE') "
            + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
            // + " AND ud.dtCreazione >= :dataDa and ud.dtCreazione < :dataA "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud.idUnitaDoc = ud2.idUnitaDoc "
            + " GROUP BY trunc(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut,ud.aaKeyUnitaDoc, ud.idDecRegistroUnitaDoc, "
            + " ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc ";

    /* Nuove query per il calcolo delle ud, doc e comp annullati */
    private String getAnnullQuery() {

        return "SELECT new it.eng.parer.entity.MonContaByStatoConservNew("
                + "trunc(doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, "
                + "nvl(count(compDoc), 0), 'UD_ANNULL') FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich , AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 "
                + "WHERE unitaDoc.idOrgStrut = :idStrut AND statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemAnnul.tiStatoItem = 'ANNULLATO' " + "AND docPrinc.tiDoc = 'PRINCIPALE' "
                + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                // + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                + "AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA AND unitaDoc2 = unitaDoc "
                + "AND richAnnul.tiRichAnnulVers = 'UNITA_DOC' "
                + "GROUP BY TRUNC(doc.dtCreazione), strut.idStrut, subStrut.idSubStrut,unitaDoc.aaKeyUnitaDoc, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc ";
    }

    //
    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4 = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW " + "WHERE dt_creazione BETWEEN ?1 AND ?2 ";

    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_LAST_180 = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180 (dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico, ni_comp_volume, ni_comp_annul) "
            + "SELECT dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW " + "WHERE dt_creazione BETWEEN ?1 AND ?2 ";

    private static final String MOVE_LAST_180_TO_MON_CONTA = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico, ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180 ";

    // Inserimento record nella tabella di appoggio
    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_APPOGGIO = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW_ANNUL (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT SMON_CONTA_BY_STATO_CONSERV_NEW.nextval, dt_creazione, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc, ni_comp_aip_generato, ni_comp_aip_in_agg, ni_comp_presa_in_carico,	ni_comp_volume, ni_comp_annul	"
            + "FROM MON_V_CONTA_BY_STATO_CONSERV_NEW "
            + "WHERE id_strut IN ?1 AND dt_creazione BETWEEN ?2 AND ?3 ";

    // Cancellazione record dalla tabella dei conteggi
    private static final String DELETE_UD_AIP_GENERATO_E_NON_GENERATO_PER_RICALCOLO_ANNUL = "Delete from MON_CONTA_BY_STATO_CONSERV_NEW "
            + "WHERE id_strut IN ?1 AND dt_rif_conta BETWEEN ?2 AND ?3 ";

    // "Trasferimento" record dalla tabella di appoggio a quella dei conteggi
    private static final String UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_RIPRISTINA = "Insert /*+ append */ into "
            + "MON_CONTA_BY_STATO_CONSERV_NEW (id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + "id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul) "
            + "SELECT id_conta_by_stato_conserv, dt_rif_conta, id_strut, id_sub_strut, aa_key_unita_doc, id_registro_unita_doc, id_tipo_unita_doc, "
            + " id_tipo_doc_princ, ni_comp_aip_generato, ni_comp_aip_in_aggiorn, ni_comp_presa_in_carico,	ni_comp_in_volume, ni_comp_annul	"
            + "FROM MON_CONTA_BY_STATO_CONSERV_NEW_ANNUL ";

    // Svuotamento tabella appoggio
    private static final String DELETE_MON_CONTA_BY_STATO_CONSERV_NEW_ANNULL = "Delete from MON_CONTA_BY_STATO_CONSERV_NEW_ANNUL ";

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

    public Calendar getData6Mesi() {
        Calendar cal = Calendar.getInstance();
        // Imposto la data 6 mesi antecedente alla data odierna
        cal.add(Calendar.DAY_OF_MONTH, -180);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteMonConta(MonContaByStatoConservNew conta) {
        entityManager.remove(conta);
        entityManager.flush();
    }

    public int deleteMonConta(Date dtRifContaDa) {
        Query query = entityManager.createQuery(
                "DELETE FROM MonContaByStatoConservNew conta WHERE conta.dtRifConta >= :dtRifConta ");
        query.setParameter("dtRifConta", dtRifContaDa);
        int numRecordCancellati = query.executeUpdate();
        return numRecordCancellati;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int truncateMonContaByStatoConservNewLast180() {
        Query query = entityManager
                .createNativeQuery("DELETE FROM MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180");
        int numRecordCancellati = query.executeUpdate();
        entityManager.flush();
        return numRecordCancellati;
    }

    public List<MonContaByStatoConservNew> getMonContaByStatoConservNew(long idStrut,
            long idSubStrut, BigDecimal aaKeyUnitaDoc, Set<Date> dtToManage) {
        // aa
        if (!dtToManage.isEmpty()) {
            String queryString = "SELECT conta FROM MonContaByStatoConservNew conta "
                    + "WHERE conta.orgStrut.idStrut = :idStrut "
                    + "AND conta.orgSubStrut.idSubStrut = :idSubStrut "
                    + "AND conta.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                    + "AND conta.dtRifConta IN :dtRifConta ";
            Query query = entityManager.createQuery(queryString);
            query.setParameter("idSubStrut", idSubStrut);
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
            query.setParameter("idStrut", idStrut);

            List<Date> mainList = new ArrayList<>();
            mainList.addAll(dtToManage);
            query.setParameter("dtRifConta", mainList);

            return query.getResultList();
        }
        return new ArrayList<>();
    }

    public List<MonContaByStatoConservNew> getMonContaByStatoConservNew2(Object[] dtToManage) {
        // aa
        /* TODO controllare senza truncate */
        String queryString = "SELECT conta FROM MonContaByStatoConservNew conta "
                + "WHERE conta.dtRifConta = :dtRifConta "
                + "AND conta.orgSubStrut.idSubStrut = :idSubStrut ";
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idSubStrut", (Long) dtToManage[0]);
        query.setParameter("dtRifConta", (Date) dtToManage[1]);

        return query.getResultList();
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
     * Metodo di prova per inserimento in MON_CONTA_BY_STATO_CONSERV_NEW con date e struttura
     * cablate
     *
     * @throws IOException eccezione nell'inserimento dei totali del metodo di prova
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiornoPROVA() throws IOException {
        final Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res = new HashMap<>();
        Calendar da = Calendar.getInstance();
        Calendar a = Calendar.getInstance();
        da.set(2011, Calendar.DECEMBER, 01, 0, 0, 0);
        a.set(2021, Calendar.OCTOBER, 13, 23, 59, 59);
        List<MonContaByStatoConservNew> resParziale = executeQueryCalcolo(3323, da.getTime(),
                a.getTime(), UD_AIP_GENERATO);
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_AIP_GENERATO);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(),
                UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        addOrSetContToResultConsistenza(resParziale, res,
                TipoConteggio.UD_AIP_NON_GENERATO_PRESA_IN_CARICO);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(),
                UD_AIP_NON_GENERATO_AIP_IN_AGG);
        addOrSetContToResultConsistenza(resParziale, res,
                TipoConteggio.UD_AIP_NON_GENERATO_AIP_IN_AGG);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(),
                UD_AIP_NON_GENERATO_IN_VOL_CONS);
        addOrSetContToResultConsistenza(resParziale, res,
                TipoConteggio.UD_AIP_NON_GENERATO_IN_VOLUME_CONS);
        resParziale = executeQueryCalcolo(3323, da.getTime(), a.getTime(), getAnnullQuery());
        addOrSetContToResultConsistenza(resParziale, res, TipoConteggio.UD_ANNULL);

        for (Map.Entry<MonContaByStatoConservNew, MonContaByStatoConservNew> r : res.entrySet()) {
            entityManager.persist(r.getValue());
        }
    }

    private void addOrSetContToResultConsistenza(List<MonContaByStatoConservNew> cont,
            Map<MonContaByStatoConservNew, MonContaByStatoConservNew> res,
            TipoConteggio tipoConteggio) {
        MonContaByStatoConservNew temp;
        for (MonContaByStatoConservNew i : cont) {
            if ((temp = res.get(i)) == null) {
                res.put(i, i);
            } else {
                switch (tipoConteggio) {
                case UD_AIP_GENERATO:
                    temp.setNiCompAipGenerato(
                            temp.getNiCompAipGenerato().add(i.getNiCompAipGenerato()));
                    break;
                case UD_ANNULL:
                    temp.setNiCompAnnul(i.getNiCompAnnul());
                    break;
                case UD_AIP_NON_GENERATO_AIP_IN_AGG:
                    temp.setNiCompAipInAggiorn(
                            temp.getNiCompAipInAggiorn().add(i.getNiCompAipInAggiorn()));
                    break;
                case UD_AIP_NON_GENERATO_IN_VOLUME_CONS:
                    temp.setNiCompInVolume(temp.getNiCompInVolume().add(i.getNiCompInVolume()));
                    break;
                case UD_AIP_NON_GENERATO_PRESA_IN_CARICO:
                    temp.setNiCompPresaInCarico(
                            temp.getNiCompPresaInCarico().add(i.getNiCompPresaInCarico()));
                    break;
                }
            }
        }
    }

    // queryString
    private List<MonContaByStatoConservNew> executeQueryCalcolo(long idStrut, Date dataCalcoloDa,
            Date dataCalcoloA, String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("idStrut", idStrut);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        return query.getResultList();
    }// niUnitaDocVers
     // idUserIam

    private void executeNativeQueryCalcolo4(Date dataCalcoloDa, Date dataCalcoloA,
            String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, dataCalcoloDa);
        query.setParameter(2, dataCalcoloA);

        query.executeUpdate();
    }

    private void executeNativeQueryCalcolo4(long idSubStrut, Date dataCalcolo, String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idSubStrut);
        query.setParameter(2, dataCalcolo);

        query.executeUpdate();
    }

    private void executeNativeQueryDeleteAndExecute(Set<Long> idStrut, Date dataCalcoloRipartenza,
            Date dataCalcoloA, String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter(1, idStrut);
        query.setParameter(2, dataCalcoloRipartenza);
        query.setParameter(3, dataCalcoloA);

        query.executeUpdate();
    }

    private void executeNativeQueryWithoutParameters(String queryString) {
        Query query = entityManager.createNativeQuery(queryString);
        query.executeUpdate();
    }

    public void insertMonTipoUnitaDocUserVers(Long idTipoUnitaDoc, Long idUserIam, Date dtRifConta,
            BigDecimal niUnitaDocVers) {
        MonTipoUnitaDocUserVers tipoUnitaDocUserVers = new MonTipoUnitaDocUserVers();
        tipoUnitaDocUserVers
                .setDecTipoUnitaDoc(entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc));
        tipoUnitaDocUserVers.setIamUser(entityManager.find(IamUser.class, idUserIam));
        tipoUnitaDocUserVers.setDtRifConta(dtRifConta);
        tipoUnitaDocUserVers.setNiUnitaDocVers(niUnitaDocVers);
        entityManager.persist(tipoUnitaDocUserVers);
        entityManager.flush();
    }

    /**
     * Ricavo le strutture che, in un dato giorno, hanno effettuato versamenti o annullamenti e le
     * salvo in tabella temporanea con data di riferimento controllando che non esista già il record
     *
     * @param firstTime true, se è il primo giro di calcolo
     * @param start     data di inizio intervallo temporale
     * @param end       data di fine intervallo temporale
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaOld(boolean firstTime,
            Date start, Date end) {

        if (firstTime) {
            /*
             * Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è
             * stato un versamento
             */
            Query query1 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_creazione "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) "
                            + "JOIN Aro_Doc doc " + "on (doc.id_doc = strutDoc.id_doc) "
                            + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) " + "where "
                            + "not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query1.executeUpdate();
            // ID_STRUT
            Query query2 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_annull "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_annull "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) "
                            + "JOIN Aro_Doc doc " + "on (doc.id_doc = strutDoc.id_doc) "
                            + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) "
                            + "where doc.ti_Annul = 'ANNULLAMENTO' "
                            + "and not exists (SELECT tmp.id_Strut FROM Tmp_Strut_Calc_Consist_New tmp WHERE tmp.id_Strut = "
                            + "doc.id_Strut AND tmp.dt_Rif_Conta = trunc(doc.dt_creazione)) "
                            + "group by doc.id_Strut, trunc(doc.dt_creazione)) ");

            query2.executeUpdate();

        } else {
            /*
             * Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è
             * stato un versamento
             */
            Query query1 = entityManager.createNativeQuery(
                    "Insert into TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                            + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione "
                            + "FROM "
                            + "(select doc.id_Strut as id_strut, 'DA_ELABORARE' as da_elaborare, trunc(doc.dt_creazione) as dt_creazione "
                            + "from Aro_Comp_Doc compDoc " + "JOIN Aro_Strut_Doc strutDoc "
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) "
                            + "JOIN Aro_Doc doc " + "on (doc.id_doc = strutDoc.id_doc) "
                            + "JOIN Aro_Unita_Doc unitaDoc "
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
                            + "on (strutDoc.id_strut_doc = compDoc.id_strut_doc) "
                            + "JOIN Aro_Doc doc " + "on (doc.id_doc = strutDoc.id_doc) "
                            + "JOIN Aro_Unita_Doc unitaDoc "
                            + "on (unitaDoc.id_unita_doc = doc.id_unita_doc) "
                            + "where doc.ti_Annul = 'ANNULLAMENTO' "
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
     * VERSIONE QUERY NATIVE OTTIMIZZATE (AA_KEY_UNITA_DOC FITTIZIO)
     *
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se
     * non si tratta del primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime true, se è il primo giro di calcolo
     * @param start     data di inizio intervallo temporale
     * @param end       data di fine intervallo temporale
     * @param idStrut   la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNative(boolean firstTime,
            Date start, Date end, long idStrut) {
        if (firstTime) {
            /*
             * Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è
             * stato un versamento
             */
            Query query = entityManager.createNativeQuery("Insert into /*+ append */ "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO "
                    + "FROM " + "(SELECT doc.id_Strut AS id_Strut, "
                    + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, 1900 AS ANNO "
                    + "FROM Aro_Doc doc " + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM Tmp_Strut_Calc_Consist_New tmp " + "WHERE tmp.id_Strut = doc.id_Strut "
                    + "AND tmp.dt_Rif_Conta = trunc(doc.dt_Creazione)) " + "AND id_Strut = ?1 "
                    + "GROUP BY doc.id_Strut, TRUNC(doc.dt_Creazione))");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();
        } else {
            /*
             * Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è
             * stato un versamento
             */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO "
                    + "FROM(SELECT doc.id_Strut AS id_Strut, " + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, 1900 AS ANNO "
                    + "FROM Aro_Doc doc " + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM Tmp_Strut_Calc_Consist_New tmp " + "WHERE tmp.id_Strut = doc.id_Strut "
                    + "AND tmp.dt_Rif_Conta = TRUNC (doc.dt_Creazione)) " + "AND id_Strut = ?1 "
                    + "AND trunc(doc.dt_creazione) > ?2  AND trunc(doc.dt_creazione) <= ?3 "
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
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se
     * non si tratta del primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime true, se è il primo giro di calcolo
     * @param start     data di inizio intervallo temporale
     * @param end       data di fine intervallo temporale
     * @param idStrut   la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNative2(boolean firstTime,
            Date start, Date end, long idStrut) {
        if (firstTime) {
            /* VERSIONE ARO_DOC/ARO_UNITA_DOC */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT doc.id_Strut AS id_strut, "
                    + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, "
                    + "unita_doc.AA_KEY_UNITA_DOC AS ANNO, unita_doc.id_sub_Strut AS id_sub_strut "
                    + "FROM ARO_UNITA_DOC unita_doc " + "JOIN ARO_DOC doc "
                    + "ON (unita_doc.id_unita_doc = doc.id_unita_doc) " + "WHERE NOT EXISTS "
                    + "(SELECT tmp.id_Strut " + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut AND tmp.id_sub_strut = unita_doc.id_sub_strut "
                    + "AND tmp.dt_Rif_Conta = trunc(doc.dt_Creazione)) " + "AND doc.id_Strut = ?1 "
                    + "GROUP BY doc.id_Strut, trunc(doc.dt_Creazione), unita_doc.AA_KEY_UNITA_DOC, unita_doc.id_sub_Strut)");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();

            Query queryAnnul = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, "
                    + "AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT VRS.id_Strut AS id_strut, "
                    + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers) AS dt_creazione, "
                    + "VRS.AA_KEY_UNITA_DOC AS ANNO, sub_strut.id_sub_strut "
                    + "FROM sacer.ARO_RICH_ANNUL_VERS rich_annul "
                    + "JOIN sacer.ARO_STATO_RICH_ANNUL_VERS stato_rich "
                    + "ON (stato_rich.id_stato_rich_annul_vers = "
                    + "rich_annul.id_stato_rich_annul_vers_cor) "
                    + "JOIN sacer.ARO_ITEM_RICH_ANNUL_VERS VRS "
                    + "ON (vrs.id_rich_annul_vers = rich_annul.id_rich_annul_vers) "
                    + "JOIN ORG_STRUT strut "
                    + "ON (strut.id_strut = VRS.ID_STRUT) JOIN ORG_SUB_STRUT sub_strut "
                    + "ON(strut.id_strut = sub_strut.id_strut) " + "WHERE NOT EXISTS "
                    + "(SELECT tmp.id_Strut " + "FROM sacer.Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = VRS.id_Strut AND tmp.id_sub_strut = sub_strut.id_sub_strut "
                    + "AND tmp.dt_Rif_Conta = "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers)) "
                    + "AND VRS.id_Strut = ?1 and rich_annul.id_Strut=?1 "
                    + "AND vrs.ti_item_rich_annul_vers = 'UNI_DOC' "
                    + "AND vrs.ti_stato_item = 'ANNULLATO' "
                    + "AND stato_rich.ti_stato_rich_annul_vers = 'EVASA' "
                    + "GROUP BY VRS.id_Strut, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers), "
                    + "VRS.AA_KEY_UNITA_DOC, sub_strut.id_sub_Strut)");

            queryAnnul.setParameter(1, BigDecimal.valueOf(idStrut));
            queryAnnul.executeUpdate();

        } else {
            /* VERSIONE ARO_DOC/ARO_UNITA_DOC */
            /*
             * Ricavo e inserisco le strutture da considerare per il giorno specifico in cui c'è
             * stato un versamento
             */
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM " + "(" + " SELECT doc.id_Strut AS id_strut, "
                    + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (doc.dt_Creazione) AS dt_creazione, "
                    + "unita_doc.AA_KEY_UNITA_DOC AS ANNO, unita_doc.id_sub_strut "
                    + "FROM ARO_UNITA_DOC unita_doc " + "JOIN ARO_DOC doc "
                    + "ON (unita_doc.id_unita_doc = doc.id_unita_doc) " + "WHERE NOT EXISTS "
                    + "(SELECT tmp.id_Strut " + "FROM Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = doc.id_Strut "
                    + "AND tmp.id_sub_strut = unita_doc.id_sub_strut "
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
                    + "FROM " + "(" + " SELECT VRS.id_Strut AS id_strut, "
                    + "'DA_ELABORARE' AS da_elaborare, "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers) AS dt_creazione, "
                    + "VRS.AA_KEY_UNITA_DOC AS ANNO, sub_strut.id_sub_strut "
                    + "FROM sacer.ARO_RICH_ANNUL_VERS rich_annul "
                    + "JOIN sacer.ARO_STATO_RICH_ANNUL_VERS stato_rich "
                    + "ON (stato_rich.id_stato_rich_annul_vers = "
                    + "rich_annul.id_stato_rich_annul_vers_cor) "
                    + "JOIN sacer.ARO_ITEM_RICH_ANNUL_VERS VRS "
                    + "ON (vrs.id_rich_annul_vers = rich_annul.id_rich_annul_vers) "
                    + "JOIN ORG_STRUT strut " + "ON (strut.id_strut = VRS.ID_STRUT) "
                    + "JOIN ORG_SUB_STRUT sub_strut " + "ON(strut.id_strut = sub_strut.id_strut) "
                    + "WHERE NOT EXISTS " + "(SELECT tmp.id_Strut "
                    + "FROM sacer.Tmp_Strut_Calc_Consist_New tmp "
                    + "WHERE tmp.id_Strut = VRS.id_Strut "
                    + "AND tmp.id_sub_strut = sub_strut.id_sub_strut " + "AND tmp.dt_Rif_Conta = "
                    + "TRUNC (stato_rich.dt_reg_stato_rich_annul_vers)) "
                    + "AND VRS.id_Strut = ?1 and rich_annul.id_Strut=?1 "
                    + "AND stato_rich.DT_REG_STATO_RICH_ANNUL_VERS >= ?2  AND stato_rich.DT_REG_STATO_RICH_ANNUL_VERS < ?3 "
                    + "AND vrs.ti_item_rich_annul_vers = 'UNI_DOC' "
                    + "AND vrs.ti_stato_item = 'ANNULLATO' "
                    + "AND stato_rich.ti_stato_rich_annul_vers = 'EVASA' "
                    + "GROUP BY VRS.id_Strut, "
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
     * Inserisco i record in TMP_STRUT_CALC_CONSIST_NEW passando in input l'intervallo temporale (se
     * non si tratta del primo giro che partirebbe dal 2011) per una determinata struttura
     *
     * @param firstTime true, se è il primo giro di calcolo
     * @param start     data di inizio intervallo temporale
     * @param end       data di fine intervallo temporale
     * @param idStrut   la struttura considerata
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista(
            boolean firstTime, Date start, Date end, long idStrut) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
            query.executeUpdate();
        } else {

            // per questa struttura considerata recuperare la data di ultimo conteggio
            // (dt_rif_conta)
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
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista2(
            boolean firstTime, Date start, Date end, long idStrut) {
        if (firstTime) {
            Query query = entityManager.createNativeQuery("Insert /*+ append */ into "
                    + "TMP_STRUT_CALC_CONSIST_NEW (ID_STRUT,TI_STATO_ELAB,ID_TMP_STRUT_CALC_CONSIST,DT_RIF_CONTA, AA_KEY_UNITA_DOC, ID_SUB_STRUT) "
                    + "SELECT ID_STRUT, da_elaborare, STMP_STRUT_CALC_CONSIST_NEW.nextval, dt_creazione, ANNO, id_sub_strut "
                    + "FROM TMP_V_STRUT_CALC_CONSIST_NEW vista " + "WHERE vista.id_strut = ?1 ");

            query.setParameter(1, BigDecimal.valueOf(idStrut));
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
        return entityManager.createQuery("SELECT subStrut FROM OrgSubStrut subStrut ")
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVistaByDoppioId(
            boolean firstTime, Date start, Date end, Long[] strutSubStrut) {
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
                    + "AND vista.id_sub_strut = ?4 "
                    + "AND vista.DT_CREAZIONE > ?2  AND vista.DT_CREAZIONE < ?3 ");

            query.setParameter(1, BigDecimal.valueOf(strutSubStrut[0]));
            query.setParameter(2, start);
            query.setParameter(3, end);
            query.setParameter(4, BigDecimal.valueOf(strutSubStrut[1]));
            query.executeUpdate();
        }
        entityManager.flush();
    }

    public List<Long[]> getIdStrutIdSubStrutList() {
        return entityManager.createQuery(
                "SELECT subStrut.orgStrut.idStrut, subStrut.idSubStrut FROM OrgSubStrut subStrut ")
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiungiStruttureDaConsiderarePerCalcoloConsistenzaQueryNativeVista2NoStrut(
            boolean firstTime, Date start, Date end) {
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
        Query q = entityManager.createNativeQuery("update tmp_strut_calc_consist_new "
                + "SET dt_exec_job = ?1 WHERE ti_stato_elab = 'ELABORAZIONE_OK' ");
        q.setParameter(1, endJob.getTime());
        q.executeUpdate();
    }

    public Calendar getUltimaDtRifContaA() {
        String queryString = "SELECT MAX(u.dtRifContaA) FROM LogElabConsist u ";
        Query query = entityManager.createQuery(queryString);
        List<Date> d = query.getResultList();
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

    public Calendar getUltimaDtElabConsist() {
        String queryString = "SELECT MAX(u.dtElabConsist) FROM LogElabConsist u ";
        Query query = entityManager.createQuery(queryString);
        List<Date> d = query.getResultList();
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
        }
        return cal;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eseguiPrimoGiroByRange(Date da, Date a) {
        executeNativeQueryCalcolo4(da, a, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4);
        insertLogElabConsist(da, a);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiornoOptimized(boolean firstTime, Date dtRifContaDa,
            Date dtRifContaA) throws ParseException {

        // Elimino i record da ricalcolare da MON_CONTA_BY_STATO_CONSERV_NEW
        int numRecordCancellati = deleteMonConta(dtRifContaDa);
        log.info(
                "{} - Cancellazione da MON_CONTA_BY_STATO_CONSERV_NEW: sono stati cancellati {} record",
                JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), numRecordCancellati);

        // Controllo di non aver già eseguito i calcoli per questo giorno
        Calendar a = Calendar.getInstance();
        a.set(Calendar.HOUR_OF_DAY, 0);
        a.set(Calendar.MINUTE, 0);
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);

        if (!dtRifContaA.equals(a.getTime())) {

            // Recupero se nel periodo in questione esistono dei record con dt_annul dove
            // dt_creazione < dt_annul:
            // mi interessano solo questi perché se l'annullamento avviene all'interno del periodo
            // considerato per il
            // calcolo lo stato sarà già aggiornato in fase di conteggio
            Query q1 = entityManager.createNativeQuery(
                    "SELECT /*+ parallel(16) */ id_strut, MIN(dt_creazione) FROM sacer.Aro_Unita_Doc unitaDoc "
                            + "WHERE unitaDoc.dt_annul BETWEEN :dtAnnulStart AND :dtAnnulEnd "
                            + "AND unitaDoc.dt_creazione < :dtAnnulStart group by id_strut ");
            q1.setParameter("dtAnnulStart", dtRifContaDa);
            q1.setParameter("dtAnnulEnd", dtRifContaA);
            List<Object[]> recordDaRicalcolareList = q1.getResultList();

            Set<Long> idStrutDaRicalcolare = new HashSet<>();
            Set<Date> dataDaCuiRicalcolareSet = new HashSet<>();

            // Se nel periodo ci sono annullamenti, mi salvo i record da "ricalcolare"
            // ovvero quelli con dtCreazione antecedente alla data di annul
            if (recordDaRicalcolareList != null && !recordDaRicalcolareList.isEmpty()) {
                for (int i = 0; i < recordDaRicalcolareList.size(); i++) {
                    dataDaCuiRicalcolareSet
                            .add((Date) (((Object[]) recordDaRicalcolareList.get(i))[1]));
                    idStrutDaRicalcolare
                            .add(((BigDecimal) (((Object[]) recordDaRicalcolareList.get(i))[0]))
                                    .longValue());
                }
            }

            /**
             * ************************ Calcolo tutto il periodo
             *************************
             */
            // executeNativeQueryCalcolo4(dtRifContaDa, dtRifContaA,
            // UD_AIP_GENERATO_E_NON_GENERATO_NATIVA4);
            // Riverso in MON_CONTA_BY_STATO_CONSERV_NEW i dati calcolati in
            // MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180
            moveDataInMonConta(dtRifContaDa, dtRifContaA);
            log.info(
                    "{} - Copio i dati da LAST_180 a MON_CONTA e ricalcolo eventuali annullamenti nel periodo considerato",
                    JobConstants.JobEnum.CALCOLO_CONSISTENZA.name());

            // Se sono presenti annullamenti con dt_creazione antecedente il periodo di calcolo,
            // "allineo"
            if (!idStrutDaRicalcolare.isEmpty()) {
                // Elimino l'orario dalla data di creazione minima recuperata
                Date dataDaDoveRipartire = Collections.min(dataDaCuiRicalcolareSet);
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dataDaDoveRipartire = formatter.parse(formatter.format(dataDaDoveRipartire));

                // Calcolo i dati e li inserisco in una tabella di "appoggio"
                executeNativeQueryDeleteAndExecute(idStrutDaRicalcolare, dataDaDoveRipartire,
                        dtRifContaA, UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_APPOGGIO);
                // Delete massiva delle sottostrutture
                executeNativeQueryDeleteAndExecute(idStrutDaRicalcolare, dataDaDoveRipartire,
                        dtRifContaA, DELETE_UD_AIP_GENERATO_E_NON_GENERATO_PER_RICALCOLO_ANNUL);
                // Ricalcolo massivo delle sottostrutture
                executeNativeQueryWithoutParameters(
                        UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_RIPRISTINA);
                // Svuoto la tabella di appoggio
                executeNativeQueryWithoutParameters(DELETE_MON_CONTA_BY_STATO_CONSERV_NEW_ANNULL);
            }

        }

        if (dtRifContaDa.after(dtRifContaA)) {
            dtRifContaDa = dtRifContaA;
        }
        // Salvo il record del JOB in Log_Elab_Consist
        insertLogElabConsist(dtRifContaDa, dtRifContaA);

        truncateMonContaByStatoConservNewLast180();
        log.info("{} - Svuoto MON_CONTA_BY_STATO_CONSERV_NEW_LAST_180",
                JobConstants.JobEnum.CALCOLO_CONSISTENZA.name());

        entityManager.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiornoLast180(Date dtRifContaDa, Date dtRifContaA)
            throws ParseException {

        // Controllo di non aver già eseguito i calcoli per questo giorno
        Calendar a = Calendar.getInstance();
        a.set(Calendar.HOUR_OF_DAY, 0);
        a.set(Calendar.MINUTE, 0);
        a.set(Calendar.SECOND, 0);
        a.set(Calendar.MILLISECOND, 0);

        if (!dtRifContaA.equals(a.getTime())) {
            executeNativeQueryCalcolo4(dtRifContaDa, dtRifContaA,
                    UD_AIP_GENERATO_E_NON_GENERATO_NATIVA_LAST_180);
            entityManager.flush();
        }
    }

    public int moveDataInMonConta(Date dtRifContaDa, Date dtRifContaA) {
        Query query = entityManager.createNativeQuery(MOVE_LAST_180_TO_MON_CONTA);
        int numRecordCopiati = query.executeUpdate();
        return numRecordCopiati;
    }

}
