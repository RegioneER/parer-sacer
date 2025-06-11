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

package it.eng.parer.job.calcoloContenutoSacer.ejb;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.MonContaUdDocComp;
import it.eng.parer.entity.MonTipoUnitaDocUserVers;
import it.eng.parer.grantedEntity.OrgServizioErog;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.viewEntity.OrgVServSistVersDaErog;
import it.eng.parer.viewEntity.OrgVServTiServDaErog;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({ "unchecked" })
@Stateless(mappedName = "CalcoloContenutoSacerHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoSacerHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    Logger log = LoggerFactory.getLogger(CalcoloContenutoSacerHelper.class);

    private static final String VERS_UD = "select new it.eng.parer.entity.MonContaUdDocComp("
            + " TRUNC(docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " NVL(count(ud.idOrgStrut), 0), 'UD_VERS' ) " + "from AroDoc docPrinc, OrgStrut ostrut "
            + "join docPrinc.aroUnitaDoc ud " + "where docPrinc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
            + "and ostrut.idStrut = docPrinc.idStrut " + "and docPrinc.tiDoc = 'PRINCIPALE' "
            + "and docPrinc.dtCreazione >= :dataDa " + "and docPrinc.dtCreazione < :dataA "
            + "group by TRUNC(docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc,  docPrinc.idDecTipoDoc";
    private static final String VERS_DOC = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut,  ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " NVL(count(ud.idOrgStrut), 0), 'DOC_VERS' )" + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut "
            + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa " + "and doc.dtCreazione < :dataA "
            + "group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";
    private static final String VERS_COMP = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  NVL(count(ud.idOrgStrut), 0),  NVL( sum(comp.niSizeFileCalc), 0), " + " 'COMP_VERS' )"
            + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc " + "join comp.aroStrutDoc strut "
            + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa " + "and doc.dtCreazione < :dataA "
            + "group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";
    private static final String AGG_COMP = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  NVL(count(ud.idOrgStrut), 0),  NVL( sum(comp.niSizeFileCalc), 0), " + " 'COMP_AGG' )"
            + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc " + "join comp.aroStrutDoc strut "
            + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
            + " and ostrut.idStrut = doc.idStrut " + " and doc.dtCreazione >= :dataDa "
            + " and doc.dtCreazione < :dataA "
            + " group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";

    /* Nuove query per il calcolo delle ud, doc e comp annullati */
    public String getAnnullQuery(String nvl, String join) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',docPrinc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul " + "JOIN itemAnnul.aroRichAnnulVers richAnnul "
                + "JOIN richAnnul.orgStrut strut " + "JOIN itemAnnul.aroUnitaDoc unitaDoc "
                + "JOIN unitaDoc.orgSubStrut subStrut " + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND docPrinc.dtCreazione >= :dataDa and docPrinc.dtCreazione < :dataA " + where
                + "GROUP BY FUNCTION('trunc',docPrinc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "docPrinc.decTipoDoc.idTipoDoc ";
    }

    public String getAnnullQueryDoc(String nvl, String join, String where) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',doc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul " + "JOIN itemAnnul.aroRichAnnulVers richAnnul "
                + "JOIN richAnnul.orgStrut strut " + "JOIN itemAnnul.aroUnitaDoc unitaDoc "
                + "JOIN unitaDoc.orgSubStrut subStrut " + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
                + where + "GROUP BY FUNCTION('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "docPrinc.decTipoDoc.idTipoDoc ";
    }

    public String getAnnullQueryComp(String nvl, String join, String where) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',doc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + " unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul " + "JOIN itemAnnul.aroRichAnnulVers richAnnul "
                + "JOIN richAnnul.orgStrut strut " + "JOIN itemAnnul.aroUnitaDoc unitaDoc "
                + "JOIN unitaDoc.orgSubStrut subStrut " + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
                + where + "GROUP BY FUNCTION('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "docPrinc.decTipoDoc.idTipoDoc ";
    }

    private static final String joinDoc = ", AroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 ";
    private static final String joinComp = ", AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 ";
    private static final String where = " AND unitaDoc2 = unitaDoc ";
    private static final String nvlUd = "FUNCTION('nvl', count(unitaDoc), 0), 'UD_ANNULL') ";
    private static final String nvlDoc = "FUNCTION('nvl', count(doc), 0), 'DOC_ANNULL') ";
    private static final String nvlComp = "FUNCTION('nvl', count(compDoc), 0), FUNCTION('nvl', sum(compDoc.niSizeFileCalc), 0), 'COMP_ANNULL') ";

    private static final String AGG_DOC = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut,  ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " NVL(count(ud.idOrgStrut), 0), 'DOC_AGG' )" + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut "
            + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
            + " AND ostrut.idStrut = doc.idStrut " + " AND doc.dtCreazione >= :dataDa "
            + " AND doc.dtCreazione < :dataA "
            + " group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";

    public Calendar getDataInizioCalcolo() {
        String queryString = "SELECT MAX(u.dtRifConta) FROM MonContaUdDocComp u ";
        Query query = entityManager.createQuery(queryString);
        Date d = (Date) query.getSingleResult();
        Calendar cal = Calendar.getInstance();
        if (d != null) {
            cal.setTime(d);
            cal.add(Calendar.DATE, 1);
        } else {
            // Imposto la data all'1 dicembre 2011
            cal.set(Calendar.YEAR, 2011);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal;
    }

    public Calendar getUltimaDataEsecuzioneOkCCS() {
        String queryString = "SELECT MAX(u.dtRegLogJob) FROM LogJob u WHERE u.nmJob = 'CALCOLO_CONTENUTO_SACER' AND u.tiRegLogJob = 'FINE_SCHEDULAZIONE' ";
        Query query = entityManager.createQuery(queryString);
        Date d = (Date) query.getSingleResult();
        Calendar cal = Calendar.getInstance();
        if (d != null) {
            cal.setTime(d);
        } else {
            // Imposto la data all'1 dicembre 2011
            cal.set(Calendar.YEAR, 2011);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno(Date dataCalcoloDa) {
        Date dataCalcoloA = DateUtils.addDays(dataCalcoloDa, 1);

        /* Gestione versamenti e aggiunta documenti */
        final Map<MonContaUdDocComp, MonContaUdDocComp> res = new HashMap<>();//
        List<MonContaUdDocComp> resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, VERS_UD);
        addOrSetContToResult(resParziale, res, MonContaUdDocComp.TipoConteggio.UD_VERS);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, VERS_DOC);//
        addOrSetContToResult(resParziale, res, MonContaUdDocComp.TipoConteggio.DOC_VERS);//
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, VERS_COMP);
        addOrSetContToResult(resParziale, res, MonContaUdDocComp.TipoConteggio.COMP_VERS);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, AGG_DOC);
        addOrSetContToResult(resParziale, res, MonContaUdDocComp.TipoConteggio.DOC_AGG);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, AGG_COMP);
        addOrSetContToResult(resParziale, res, MonContaUdDocComp.TipoConteggio.COMP_AGG);
        for (Map.Entry<MonContaUdDocComp, MonContaUdDocComp> rec : res.entrySet()) {
            entityManager.persist(rec.getValue());
        }

        /* Gestione degli eventuali annullamenti */
        final Map<MonContaUdDocComp, MonContaUdDocComp> resAnnul = new HashMap<>();//
        List<MonContaUdDocComp> resParzialeAnnul = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA,
                getAnnullQuery(nvlUd, ""));
        addOrSetContToResult(resParzialeAnnul, resAnnul, MonContaUdDocComp.TipoConteggio.UD_ANNULL);
        resParzialeAnnul = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, getAnnullQueryDoc(nvlDoc, joinDoc, where));
        addOrSetContToResult(resParzialeAnnul, resAnnul, MonContaUdDocComp.TipoConteggio.DOC_ANNULL);
        resParzialeAnnul = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA,
                getAnnullQueryComp(nvlComp, joinComp, where));
        addOrSetContToResult(resParzialeAnnul, resAnnul, MonContaUdDocComp.TipoConteggio.COMP_ANNULL);

        for (Map.Entry<MonContaUdDocComp, MonContaUdDocComp> recordAnnul : resAnnul.entrySet()) {
            MonContaUdDocComp contaAnnul = recordAnnul.getValue();
            /* Trova il record e "aggiorna" gli annullamenti */
            MonContaUdDocComp contaDaAgg = getMonContaUdDocComp(contaAnnul.getDtRifConta(), contaAnnul.getIdStrut(),
                    contaAnnul.getIdOrgSubStrut(), contaAnnul.getAaKeyUnitaDoc(), contaAnnul.getIdDecRegistroUnitaDoc(),
                    contaAnnul.getIdDecTipoUnitaDoc(), contaAnnul.getIdDecTipoDoc());
            if (contaDaAgg != null) {
                contaDaAgg.setNiUnitaDocAnnul(contaDaAgg.getNiUnitaDocAnnul().add(contaAnnul.getNiUnitaDocAnnul()));
                contaDaAgg.setNiDocAnnulUd(contaDaAgg.getNiDocAnnulUd().add(contaAnnul.getNiDocAnnulUd()));
                contaDaAgg.setNiCompAnnulUd(contaDaAgg.getNiCompAnnulUd().add(contaAnnul.getNiCompAnnulUd()));
                contaDaAgg.setNiSizeAnnulUd(contaDaAgg.getNiSizeAnnulUd().add(contaAnnul.getNiSizeAnnulUd()));
            }
        }

        /*
         * Registra nella tabella MON_TIPO_UNITA_DOC_USER_VERS il totale delle unità documentarie versate per tipo ud ed
         * utente versatore nel giorno considerato
         */
        Query q = entityManager.createQuery("SELECT TRUNC( ud.dtCreazione), ud.idDecTipoUnitaDoc, "
                + "ud.iamUser.idUserIam, NVL(count(ud.idOrgStrut), 0) " + "FROM AroUnitaDoc ud "
                + "WHERE ud.dtCreazione >= :dataDa " + "AND ud.dtCreazione < :dataA "
                + "GROUP BY TRUNC(ud.dtCreazione), ud.idDecTipoUnitaDoc, ud.iamUser.idUserIam ");

        q.setParameter("dataDa", dataCalcoloDa);
        q.setParameter("dataA", dataCalcoloA);
        List<Object[]> objArrList = q.getResultList();

        for (Object[] objArr : objArrList) {
            insertMonTipoUnitaDocUserVers(Long.class.cast(objArr[1]), Long.class.cast(objArr[2]),
                    Date.class.cast(objArr[0]), Long.class.cast(objArr[3]));
        }
    }

    public void setDtErog(LogParam param) {
        log.debug("{} - setDtErog", this.getClass().getSimpleName());
        List<Long> idServizioErogList = getOrgServizioErogs();
        log.debug("{} - servizioErogList ha {} record", this.getClass().getSimpleName(), idServizioErogList.size());
        // Scorro i servizi erogati per ricavare la data di erogazione
        for (Long idServizioErog : idServizioErogList) {
            log.debug("{} ---- cerco  OrgVCalcDtErog idServizioErogato {}", this.getClass().getSimpleName(),
                    idServizioErog);
            final TypedQuery<Date> query = entityManager.createQuery(
                    "SELECT o.dtErog FROM OrgVCalcDtErog o WHERE o.idServizioErogato=:idServizioErogato", Date.class);
            query.setParameter("idServizioErogato", BigDecimal.valueOf(idServizioErog));
            Date dtErog = query.getSingleResult();
            log.debug("{} ---- {} ", this.getClass().getSimpleName(), dtErog != null ? "PRESENTE" : "ASSENTE");
            if (dtErog != null) {
                log.debug("{} ---- imposto DtErog su OrgServizioErog", this.getClass().getSimpleName());
                OrgServizioErog servizioErog = entityManager.find(OrgServizioErog.class, idServizioErog);
                servizioErog.setDtErog(dtErog);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_ENTE_CONVENZIONATO,
                        BigDecimal.valueOf(servizioErog.getOrgAccordoEnte().getSiOrgEnteConvenz().getIdEnteSiam()),
                        param.getNomePagina());
            }
        }
        entityManager.flush();
    }

    private void addOrSetContToResult(List<MonContaUdDocComp> cont, Map<MonContaUdDocComp, MonContaUdDocComp> res,
            MonContaUdDocComp.TipoConteggio tipoConteggio) {
        MonContaUdDocComp temp;
        for (MonContaUdDocComp i : cont) {
            if ((temp = res.get(i)) == null) {
                res.put(i, i);
            } else {
                switch (tipoConteggio) {
                case UD_VERS:
                    temp.setNiUnitaDocVers(i.getNiUnitaDocVers());
                    break;
                case UD_ANNULL:
                    temp.setNiUnitaDocAnnul(i.getNiUnitaDocAnnul());
                    break;
                case DOC_VERS:
                    temp.setNiDocVers(i.getNiDocVers());
                    break;
                case DOC_ANNULL:
                    temp.setNiDocAnnulUd(i.getNiDocAnnulUd());
                    break;
                case DOC_AGG:
                    temp.setNiDocAgg(i.getNiDocAgg());
                    break;
                case COMP_VERS:
                    temp.setNiCompVers(i.getNiCompVers());
                    temp.setNiSizeVers(i.getNiSizeVers());
                    break;
                case COMP_AGG:
                    temp.setNiCompAgg(i.getNiCompAgg());
                    temp.setNiSizeAgg(i.getNiSizeAgg());
                    break;
                case COMP_ANNULL:
                    temp.setNiCompAnnulUd(i.getNiCompAnnulUd());
                    temp.setNiSizeAnnulUd(i.getNiSizeAnnulUd());
                    break;
                default:
                    break;
                }
            }
        }
    }

    private List<MonContaUdDocComp> executeQueryCalcolo(Date dataCalcoloDa, Date dataCalcoloA, String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        return query.getResultList();
    }

    public void insertMonTipoUnitaDocUserVers(Long idTipoUnitaDoc, Long idUserIam, Date dtRifConta,
            Long niUnitaDocVers) {
        MonTipoUnitaDocUserVers tipoUnitaDocUserVers = new MonTipoUnitaDocUserVers();
        tipoUnitaDocUserVers.setDecTipoUnitaDoc(entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc));
        tipoUnitaDocUserVers.setIamUser(entityManager.find(IamUser.class, idUserIam));
        tipoUnitaDocUserVers.setDtRifConta(dtRifConta);
        tipoUnitaDocUserVers.setNiUnitaDocVers(BigDecimal.valueOf(niUnitaDocVers));
        entityManager.persist(tipoUnitaDocUserVers);
    }

    /**
     * Restituisce gli id tipi unità documentaria per quel tipo servizio per le strutture passate come parametro. Null
     * se non sono presenti record.
     *
     * @param idTipoServizio
     *            tipologia di servizio
     * @param idStrutList
     *            lista delle strutture
     * @param tiClasseTipoServizio
     *            Tipo di servizio
     *
     * @return Lista degli id delle unità doc
     */
    public List<Long> getIdTipiUnitaDocByStrutAndTipoServizio(BigDecimal idTipoServizio, List<BigDecimal> idStrutList,
            CostantiDB.TiClasseTipoServizio tiClasseTipoServizio) {

        String queryStr = String.format(
                "SELECT tipoUnitaDoc.idTipoUnitaDoc FROM DecTipoUnitaDoc tipoUnitaDoc "
                        + "WHERE tipoUnitaDoc.orgStrut.idStrut IN (:idStrut) "
                        + "AND tipoUnitaDoc.%s.idTipoServizio = :idTipoServizio ",
                tiClasseTipoServizio.equals(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE) ? "orgTipoServizio"
                        : "orgTipoServizioAttiv");

        List<Long> ids = idStrutList.stream().map(BigDecimal::longValue).collect(Collectors.toList());
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", ids);
        query.setParameter("idTipoServizio", longFromBigDecimal(idTipoServizio));
        return query.getResultList();
    }

    public Date getMinimumDtRifConta(List<Long> idTipoUnitaDocList) {
        if (!idTipoUnitaDocList.isEmpty()) {
            String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon "
                    + "WHERE mon.decTipoUnitaDoc.idTipoUnitaDoc IN (:idTipoUnitaDocList) "
                    + "ORDER BY mon.dtRifConta ASC";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
            List<Date> lista = query.getResultList();
            if (!lista.isEmpty()) {
                return lista.get(0);
            }
        }
        return null;
    }

    public Date getMinimumDtRifContaBySistVers(List<Long> idTipoUnitaDocList, BigDecimal idSistemaVersante) {
        if (!idTipoUnitaDocList.isEmpty()) {
            String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon " + "JOIN mon.iamUser iamUser "
                    + "WHERE mon.decTipoUnitaDoc.idTipoUnitaDoc IN (:idTipoUnitaDocList) "
                    + "AND EXISTS (SELECT usrUser FROM UsrUser usrUser "
                    + "WHERE usrUser.aplSistemaVersante.idSistemaVersante = :idSistemaVersante AND usrUser.idUserIam = iamUser.idUserIam) "
                    + "ORDER BY mon.dtRifConta ASC";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
            query.setParameter("idSistemaVersante", longFromBigDecimal(idSistemaVersante));
            List<Date> lista = query.getResultList();
            if (!lista.isEmpty()) {
                return lista.get(0);
            }
        }
        return null;
    }

    /**
     * Restituisce i sistemi versanti associati al tipo unità documentaria.
     *
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     *
     * @return lista oggetti di tipo {@link BigDecimal}
     */
    public List<BigDecimal> getAplSistemiVersantiSeparatiPerTipoUd(BigDecimal idTipoUnitaDoc) {
        Query q = entityManager.createQuery("SELECT dec.id.idSistemaVersante FROM DecVLisSisVersByTipoUd dec "
                + "WHERE dec.id.idTipoUnitaDoc = :idTipoUnitaDoc ");
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return q.getResultList();
    }

    public List<OrgVServSistVersDaErog> getOrgVServSistVersDaErog() {
        // Ricavo la lista dei record (sistemi versanti) aventi dtErog nulla
        String queryStr = "SELECT servSistVersDaErog FROM OrgVServSistVersDaErog servSistVersDaErog "
                + "WHERE servSistVersDaErog.dtErog IS NULL ";
        Query query = entityManager.createQuery(queryStr);
        return query.getResultList();
    }

    public List<OrgVServTiServDaErog> getOrgVServTiServDaErog() {
        // Ricavo la lista dei record (servizi erogati) aventi dtErog nulla
        String queryStr = "SELECT servTiServDaErog FROM OrgVServTiServDaErog servTiServDaErog "
                + "WHERE servTiServDaErog.dtErog IS NULL ";
        Query query = entityManager.createQuery(queryStr);
        return query.getResultList();
    }

    public List<Long> getOrgServizioErogs() {
        // Ricavo la lista dei record (servizi erogati) aventi dtErog nulla
        String queryStr = "SELECT servizioErog.idServizioErogato FROM OrgServizioErog servizioErog "
                + "WHERE servizioErog.dtErog IS NULL AND EXISTS (SELECT 1 FROM OrgVCalcDtErog orgVCalcDtErog WHERE orgVCalcDtErog.idServizioErogato = servizioErog.idServizioErogato)";
        log.debug("{} - getOrgServizioErogs {}", this.getClass().getSimpleName(), queryStr);
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        return query.getResultList();
    }

    public MonContaUdDocComp getMonContaUdDocComp(Date dtRifConta, BigDecimal idStrut, Long idSubStrut,
            BigDecimal aaKeyUnitaDoc, Long idRegistroUnitaDoc, Long idTipoUnitaDoc, Long idTipoDocPrinc) {
        Query q = entityManager.createQuery("SELECT conta FROM MonContaUdDocComp conta "
                + "WHERE conta.dtRifConta = :dtRifConta " + "AND conta.idStrut = :idStrut "
                + "AND conta.idOrgSubStrut = :idSubStrut " + "AND conta.aaKeyUnitaDoc= :aaKeyUnitaDoc "
                + "AND conta.idDecRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "AND conta.idDecTipoUnitaDoc = :idTipoUnitaDoc " + "AND conta.idDecTipoDoc = :idTipoDocPrinc ");
        q.setParameter("dtRifConta", dtRifConta);
        q.setParameter("idStrut", idStrut);
        q.setParameter("idSubStrut", idSubStrut);
        q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        q.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        q.setParameter("idTipoDocPrinc", idTipoDocPrinc);
        List<MonContaUdDocComp> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;

    }
}
