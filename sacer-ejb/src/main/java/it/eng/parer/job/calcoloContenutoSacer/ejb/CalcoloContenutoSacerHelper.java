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
@SuppressWarnings({
        "unchecked" })
@Stateless(mappedName = "CalcoloContenutoSacerHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoSacerHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    Logger log = LoggerFactory.getLogger(CalcoloContenutoSacerHelper.class);

    private static final String VERS_UD = "select new it.eng.parer.entity.MonContaUdDocComp("
            + " TRUNC(docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " NVL(count(ud.idOrgStrut), 0), 'UD_VERS' ) "
            + "from AroDoc docPrinc, OrgStrut ostrut " + "join docPrinc.aroUnitaDoc ud "
            + "where docPrinc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
            + "and ostrut.idStrut = docPrinc.idStrut " + "and docPrinc.tiDoc = 'PRINCIPALE' "
            + "and docPrinc.dtCreazione >= :dataDa " + "and docPrinc.dtCreazione < :dataA "
            + "group by TRUNC(docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc,  docPrinc.idDecTipoDoc";
    private static final String VERS_DOC = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut,  ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " NVL(count(ud.idOrgStrut), 0), 'DOC_VERS' )"
            + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut " + "join doc.aroUnitaDoc ud "
            + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa "
            + "and doc.dtCreazione < :dataA "
            + "group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";
    private static final String VERS_COMP = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  NVL(count(ud.idOrgStrut), 0),  NVL( sum(comp.niSizeFileCalc), 0), "
            + " 'COMP_VERS' )" + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc "
            + "join comp.aroStrutDoc strut " + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud "
            + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa "
            + "and doc.dtCreazione < :dataA "
            + "group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";
    private static final String AGG_COMP = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  NVL(count(ud.idOrgStrut), 0),  NVL( sum(comp.niSizeFileCalc), 0), "
            + " 'COMP_AGG' )" + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc "
            + "join comp.aroStrutDoc strut " + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud "
            + "join docPrinc.aroUnitaDoc ud2 " + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
            + " and ostrut.idStrut = doc.idStrut " + " and doc.dtCreazione >= :dataDa "
            + " and doc.dtCreazione < :dataA "
            + " group by TRUNC(doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";

    /* Nuove query per il calcolo delle ud, doc e comp annullati */
    public String getAnnullQuery(String nvl, String join) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',docPrinc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, " + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemAnnul.tiStatoItem = 'ANNULLATO' " + "AND docPrinc.tiDoc = 'PRINCIPALE' "
                + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND docPrinc.dtCreazione >= :dataDa and docPrinc.dtCreazione < :dataA " +
                // where
                + "GROUP BY FUNCTION('trunc',docPrinc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "docPrinc.decTipoDoc.idTipoDoc ";
    }

    public String getAnnullQueryDoc(String nvl, String join, String where) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',doc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + "unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, " + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemAnnul.tiStatoItem = 'ANNULLATO' " + "AND docPrinc.tiDoc = 'PRINCIPALE' "
                + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
                + where
                + "GROUP BY FUNCTION('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
                + "unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "docPrinc.decTipoDoc.idTipoDoc ";
    }

    public String getAnnullQueryComp(String nvl, String join, String where) {
        return "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNCTION('trunc',doc.dtCreazione), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, "
                + " unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, " + nvl
                + "FROM AroItemRichAnnulVers itemAnnul "
                + "JOIN itemAnnul.aroRichAnnulVers richAnnul " + "JOIN richAnnul.orgStrut strut "
                + "JOIN itemAnnul.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.orgSubStrut subStrut "
                + "JOIN unitaDoc.aroDocs docPrinc, " + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemAnnul.tiStatoItem = 'ANNULLATO' " + "AND docPrinc.tiDoc = 'PRINCIPALE' "
                + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA "
                // + " AND doc.dtCreazione >= :dataDa and doc.dtCreazione < :dataA "
                + where
                + "GROUP BY FUNCTION('trunc',doc.dtCreazione), strut.idStrut, subStrut.idSubStrut, "
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
            + " NVL(count(ud.idOrgStrut), 0), 'DOC_AGG' )"
            + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut " + "join doc.aroUnitaDoc ud "
            + "join docPrinc.aroUnitaDoc ud2 " + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
            + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
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
        List<MonContaUdDocComp> resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA,
                VERS_UD);
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
        resParzialeAnnul = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA,
                getAnnullQueryDoc(nvlDoc, joinDoc, where));
        addOrSetContToResult(resParzialeAnnul, resAnnul,
                MonContaUdDocComp.TipoConteggio.DOC_ANNULL);
        resParzialeAnnul = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA,
                getAnnullQueryComp(nvlComp, joinComp, where));
        addOrSetContToResult(resParzialeAnnul, resAnnul,
                MonContaUdDocComp.TipoConteggio.COMP_ANNULL);

        for (Map.Entry<MonContaUdDocComp, MonContaUdDocComp> recordAnnul : resAnnul.entrySet()) {
            MonContaUdDocComp contaAnnul = recordAnnul.getValue();
            /* Trova il record e "aggiorna" gli annullamenti */
            MonContaUdDocComp contaDaAgg = getMonContaUdDocComp(contaAnnul.getDtRifConta(),
                    contaAnnul.getIdStrut(), contaAnnul.getIdOrgSubStrut(),
                    contaAnnul.getAaKeyUnitaDoc(), contaAnnul.getIdDecRegistroUnitaDoc(),
                    contaAnnul.getIdDecTipoUnitaDoc(), contaAnnul.getIdDecTipoDoc());
            if (contaDaAgg != null) {
                contaDaAgg.setNiUnitaDocAnnul(
                        contaDaAgg.getNiUnitaDocAnnul().add(contaAnnul.getNiUnitaDocAnnul()));
                contaDaAgg.setNiDocAnnulUd(
                        contaDaAgg.getNiDocAnnulUd().add(contaAnnul.getNiDocAnnulUd()));
                contaDaAgg.setNiCompAnnulUd(
                        contaDaAgg.getNiCompAnnulUd().add(contaAnnul.getNiCompAnnulUd()));
                contaDaAgg.setNiSizeAnnulUd(
                        contaDaAgg.getNiSizeAnnulUd().add(contaAnnul.getNiSizeAnnulUd()));
            }
        }

        /*
         * Registra nella tabella MON_TIPO_UNITA_DOC_USER_VERS il totale delle unità documentarie
         * versate per tipo ud ed utente versatore nel giorno considerato
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
        log.debug("{} - servizioErogList ha {} record", this.getClass().getSimpleName(),
                idServizioErogList.size());
        // Scorro i servizi erogati per ricavare la data di erogazione
        for (Long idServizioErog : idServizioErogList) {
            log.debug("{} ---- cerco  OrgVCalcDtErog idServizioErogato {}",
                    this.getClass().getSimpleName(), idServizioErog);
            final TypedQuery<Date> query = entityManager.createQuery(
                    "SELECT o.dtErog FROM OrgVCalcDtErog o WHERE o.idServizioErogato=:idServizioErogato",
                    Date.class);
            query.setParameter("idServizioErogato", BigDecimal.valueOf(idServizioErog));
            Date dtErog = query.getSingleResult();
            log.debug("{} ---- {} ", this.getClass().getSimpleName(),
                    dtErog != null ? "PRESENTE" : "ASSENTE");
            if (dtErog != null) {
                log.debug("{} ---- imposto DtErog su OrgServizioErog",
                        this.getClass().getSimpleName());
                OrgServizioErog servizioErog = entityManager.find(OrgServizioErog.class,
                        idServizioErog);
                servizioErog.setDtErog(dtErog);
                sacerLogEjb
                        .log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                                param.getNomeUtente(), param.getNomeAzione(),
                                SacerLogConstants.TIPO_OGGETTO_ENTE_CONVENZIONATO,
                                BigDecimal.valueOf(servizioErog.getOrgAccordoEnte()
                                        .getSiOrgEnteConvenz().getIdEnteSiam()),
                                param.getNomePagina());
            }
        }
        entityManager.flush();
    }

    private void addOrSetContToResult(List<MonContaUdDocComp> cont,
            Map<MonContaUdDocComp, MonContaUdDocComp> res,
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

    private List<MonContaUdDocComp> executeQueryCalcolo(Date dataCalcoloDa, Date dataCalcoloA,
            String queryString) {
        Query query = entityManager.createQuery(queryString);
        query.setParameter("dataDa", dataCalcoloDa);
        query.setParameter("dataA", dataCalcoloA);
        return query.getResultList();
    }

    public void insertMonTipoUnitaDocUserVers(Long idTipoUnitaDoc, Long idUserIam, Date dtRifConta,
            Long niUnitaDocVers) {
        MonTipoUnitaDocUserVers tipoUnitaDocUserVers = new MonTipoUnitaDocUserVers();
        tipoUnitaDocUserVers
                .setDecTipoUnitaDoc(entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc));
        tipoUnitaDocUserVers.setIamUser(entityManager.find(IamUser.class, idUserIam));
        tipoUnitaDocUserVers.setDtRifConta(dtRifConta);
        tipoUnitaDocUserVers.setNiUnitaDocVers(BigDecimal.valueOf(niUnitaDocVers));
        entityManager.persist(tipoUnitaDocUserVers);
    }

    /**
     * Elabora in modo atomico l'intera giornata di Calcolo Contenuto Sacer. Se uno step fallisce,
     * l'intera giornata viene rollbackata.
     *
     * @param dataCalcoloDa data da elaborare
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraGiornataContenutoSacer(Date dataCalcoloDa) {
        insertTotaliPerGiorno(dataCalcoloDa);
        gestisciCancellazioniFisiche(dataCalcoloDa);
        gestisciRestituzioniArchivioDelGiorno(dataCalcoloDa);
    }

    /**
     * Restituisce gli id tipi unità documentaria per quel tipo servizio per le strutture passate
     * come parametro. Null se non sono presenti record.
     *
     * @param idTipoServizio       tipologia di servizio
     * @param idStrutList          lista delle strutture
     * @param tiClasseTipoServizio Tipo di servizio
     *
     * @return Lista degli id delle unità doc
     */
    public List<Long> getIdTipiUnitaDocByStrutAndTipoServizio(BigDecimal idTipoServizio,
            List<BigDecimal> idStrutList, CostantiDB.TiClasseTipoServizio tiClasseTipoServizio) {

        String queryStr = String.format(
                "SELECT tipoUnitaDoc.idTipoUnitaDoc FROM DecTipoUnitaDoc tipoUnitaDoc "
                        + "WHERE tipoUnitaDoc.orgStrut.idStrut IN (:idStrut) "
                        + "AND tipoUnitaDoc.%s.idTipoServizio = :idTipoServizio ",
                tiClasseTipoServizio.equals(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE)
                        ? "orgTipoServizio"
                        : "orgTipoServizioAttiv");

        List<Long> ids = idStrutList.stream().map(BigDecimal::longValue)
                .collect(Collectors.toList());
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

    public Date getMinimumDtRifContaBySistVers(List<Long> idTipoUnitaDocList,
            BigDecimal idSistemaVersante) {
        if (!idTipoUnitaDocList.isEmpty()) {
            String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon "
                    + "JOIN mon.iamUser iamUser "
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
     * @param idTipoUnitaDoc id tipo unita doc
     *
     * @return lista oggetti di tipo {@link BigDecimal}
     */
    public List<BigDecimal> getAplSistemiVersantiSeparatiPerTipoUd(BigDecimal idTipoUnitaDoc) {
        Query q = entityManager
                .createQuery("SELECT dec.id.idSistemaVersante FROM DecVLisSisVersByTipoUd dec "
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

    public MonContaUdDocComp getMonContaUdDocComp(Date dtRifConta, BigDecimal idStrut,
            Long idSubStrut, BigDecimal aaKeyUnitaDoc, Long idRegistroUnitaDoc, Long idTipoUnitaDoc,
            Long idTipoDocPrinc) {
        Query q = entityManager.createQuery("SELECT conta FROM MonContaUdDocComp conta "
                + "WHERE conta.dtRifConta = :dtRifConta " + "AND conta.idStrut = :idStrut "
                + "AND conta.idOrgSubStrut = :idSubStrut "
                + "AND conta.aaKeyUnitaDoc= :aaKeyUnitaDoc "
                + "AND conta.idDecRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "AND conta.idDecTipoUnitaDoc = :idTipoUnitaDoc "
                + "AND conta.idDecTipoDoc = :idTipoDocPrinc ");
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

    // MEV #37227

    // METODO UNICO PER LE CANCELLAZIONI (Annullamento, Scarto)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciCancellazioniFisiche(Date dataCalcoloDa) {
        Date dataCalcoloA = org.apache.commons.lang3.time.DateUtils.addDays(dataCalcoloDa, 1);

        log.info(
                "Calcolo Contenuto Sacer - Avvio allineamento massivo contatori cancellazioni tra {} e {}",
                dataCalcoloDa, dataCalcoloA);

        // Verifica formale di quadratura prima dello storno: tutte le chiavi da spostare
        // devono esistere nella tabella attiva e con capienza sufficiente per il decremento.
        String aggSourceSql = "SELECT c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, "
                + "       c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC, "
                + "       SUM(c.NI_UNITA_DOC_VERS) as s_ud_vers, SUM(c.NI_DOC_VERS) as s_doc_vers, SUM(c.NI_COMP_VERS) as s_comp_vers, SUM(c.NI_SIZE_VERS) as s_size_vers, "
                + "       SUM(c.NI_DOC_AGG) as s_doc_agg, SUM(c.NI_COMP_AGG) as s_comp_agg, SUM(c.NI_SIZE_AGG) as s_size_agg, "
                + "       SUM(c.NI_UNITA_DOC_ANNUL) as s_ud_annul, SUM(c.NI_DOC_ANNUL_UD) as s_doc_annul, SUM(c.NI_COMP_ANNUL_UD) as s_comp_annul, SUM(c.NI_SIZE_ANNUL_UD) as s_size_annul "
                + "FROM DM_UD_DEL_CONTA c " + "JOIN DM_UD_DEL d ON c.ID_UNITA_DOC = d.ID_UNITA_DOC "
                + "WHERE d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "  AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA "
                + "GROUP BY c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC";

        String aggCountSql = "SELECT COUNT(1) FROM (" + aggSourceSql + ")";
        Query qAggCount = entityManager.createNativeQuery(aggCountSql);
        qAggCount.setParameter("dataDa", dataCalcoloDa);
        qAggCount.setParameter("dataA", dataCalcoloA);
        Number aggCount = (Number) qAggCount.getSingleResult();
        long totCombinazioni = aggCount != null ? aggCount.longValue() : 0L;

        String dmVolumeSql = "SELECT COUNT(1), COUNT(DISTINCT c.ID_UNITA_DOC) "
                + "FROM DM_UD_DEL_CONTA c " + "JOIN DM_UD_DEL d ON c.ID_UNITA_DOC = d.ID_UNITA_DOC "
                + "WHERE d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "  AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA";
        Query qDmVolume = entityManager.createNativeQuery(dmVolumeSql);
        qDmVolume.setParameter("dataDa", dataCalcoloDa);
        qDmVolume.setParameter("dataA", dataCalcoloA);
        Object[] dmVolume = (Object[]) qDmVolume.getSingleResult();
        long totRigheDm = dmVolume[0] != null ? ((Number) dmVolume[0]).longValue() : 0L;
        long totUdCoinvolte = dmVolume[1] != null ? ((Number) dmVolume[1]).longValue() : 0L;

        // Checksum dei volumi aggregati da spostare: usato nel log di apertura e chiusura
        // per verificare a colpo d'occhio la coerenza dell'operazione.
        String deltaCheckSql = "SELECT SUM(s_ud_vers), SUM(s_doc_vers), SUM(s_comp_vers), SUM(s_size_vers), "
                + "       SUM(s_doc_agg), SUM(s_comp_agg), SUM(s_size_agg), "
                + "       SUM(s_ud_annul), SUM(s_doc_annul), SUM(s_comp_annul), SUM(s_size_annul) "
                + "FROM (" + aggSourceSql + ")";
        Query qDeltaCheck = entityManager.createNativeQuery(deltaCheckSql);
        qDeltaCheck.setParameter("dataDa", dataCalcoloDa);
        qDeltaCheck.setParameter("dataA", dataCalcoloA);
        Object[] deltaCheck = (Object[]) qDeltaCheck.getSingleResult();
        long chkUdVers = deltaCheck[0] != null ? ((Number) deltaCheck[0]).longValue() : 0L;
        long chkDocVers = deltaCheck[1] != null ? ((Number) deltaCheck[1]).longValue() : 0L;
        long chkCompVers = deltaCheck[2] != null ? ((Number) deltaCheck[2]).longValue() : 0L;
        long chkSizeVers = deltaCheck[3] != null ? ((Number) deltaCheck[3]).longValue() : 0L;
        long chkDocAgg = deltaCheck[4] != null ? ((Number) deltaCheck[4]).longValue() : 0L;
        long chkCompAgg = deltaCheck[5] != null ? ((Number) deltaCheck[5]).longValue() : 0L;
        long chkSizeAgg = deltaCheck[6] != null ? ((Number) deltaCheck[6]).longValue() : 0L;
        long chkUdAnnul = deltaCheck[7] != null ? ((Number) deltaCheck[7]).longValue() : 0L;
        long chkDocAnnul = deltaCheck[8] != null ? ((Number) deltaCheck[8]).longValue() : 0L;
        long chkCompAnnul = deltaCheck[9] != null ? ((Number) deltaCheck[9]).longValue() : 0L;
        long chkSizeAnnul = deltaCheck[10] != null ? ((Number) deltaCheck[10]).longValue() : 0L;

        log.info(
                "Calcolo Contenuto Sacer - Sintesi input cancellazioni fisiche [giorno={}, combinazioni={}, righeDm={}, udCoinvolte={}, "
                        + "udVers={}, docVers={}, compVers={}, sizeVers={}, docAgg={}, compAgg={}, sizeAgg={}, "
                        + "udAnnul={}, docAnnul={}, compAnnul={}, sizeAnnul={}]",
                dataCalcoloDa, totCombinazioni, totRigheDm, totUdCoinvolte, chkUdVers, chkDocVers,
                chkCompVers, chkSizeVers, chkDocAgg, chkCompAgg, chkSizeAgg, chkUdAnnul,
                chkDocAnnul, chkCompAnnul, chkSizeAnnul);

        if (totCombinazioni == 0L) {
            log.info(
                    "Calcolo Contenuto Sacer - Nessuna combinazione da stornare per cancellazioni fisiche tra {} e {}",
                    dataCalcoloDa, dataCalcoloA);
            return;
        }

        String mismatchSql = "SELECT COUNT(1) FROM (" + aggSourceSql + ") agg "
                + "LEFT JOIN MON_CONTA_UD_DOC_COMP att "
                + "  ON att.DT_RIF_CONTA = agg.DT_RIF_CONTA AND att.ID_STRUT = agg.ID_STRUT AND att.ID_SUB_STRUT = agg.ID_SUB_STRUT "
                + " AND att.AA_KEY_UNITA_DOC = agg.AA_KEY_UNITA_DOC AND att.ID_REGISTRO_UNITA_DOC = agg.ID_REGISTRO_UNITA_DOC "
                + " AND att.ID_TIPO_UNITA_DOC = agg.ID_TIPO_UNITA_DOC AND att.ID_TIPO_DOC_PRINC = agg.ID_TIPO_DOC_PRINC "
                + "WHERE att.ID_CONTA_UD_DOC_COMP IS NULL "
                + "   OR NVL(att.NI_UNITA_DOC_VERS, 0) < NVL(agg.s_ud_vers, 0) "
                + "   OR NVL(att.NI_DOC_VERS, 0) < NVL(agg.s_doc_vers, 0) "
                + "   OR NVL(att.NI_COMP_VERS, 0) < NVL(agg.s_comp_vers, 0) "
                + "   OR NVL(att.NI_SIZE_VERS, 0) < NVL(agg.s_size_vers, 0) "
                + "   OR NVL(att.NI_DOC_AGG, 0) < NVL(agg.s_doc_agg, 0) "
                + "   OR NVL(att.NI_COMP_AGG, 0) < NVL(agg.s_comp_agg, 0) "
                + "   OR NVL(att.NI_SIZE_AGG, 0) < NVL(agg.s_size_agg, 0) "
                + "   OR NVL(att.NI_UNITA_DOC_ANNUL, 0) < NVL(agg.s_ud_annul, 0) "
                + "   OR NVL(att.NI_DOC_ANNUL_UD, 0) < NVL(agg.s_doc_annul, 0) "
                + "   OR NVL(att.NI_COMP_ANNUL_UD, 0) < NVL(agg.s_comp_annul, 0) "
                + "   OR NVL(att.NI_SIZE_ANNUL_UD, 0) < NVL(agg.s_size_annul, 0)";

        Query qMismatch = entityManager.createNativeQuery(mismatchSql);
        qMismatch.setParameter("dataDa", dataCalcoloDa);
        qMismatch.setParameter("dataA", dataCalcoloA);
        Number mismatchCount = (Number) qMismatch.getSingleResult();
        if (mismatchCount != null && mismatchCount.longValue() > 0L) {
            String mismatchSampleSql = "SELECT * FROM ("
                    + "  SELECT agg.DT_RIF_CONTA, agg.ID_STRUT, agg.ID_SUB_STRUT, agg.AA_KEY_UNITA_DOC, "
                    + "         agg.ID_REGISTRO_UNITA_DOC, agg.ID_TIPO_UNITA_DOC, agg.ID_TIPO_DOC_PRINC, "
                    + "         CASE "
                    + "           WHEN att.ID_CONTA_UD_DOC_COMP IS NULL THEN 'MISSING_ACTIVE_ROW' "
                    + "           WHEN NVL(att.NI_UNITA_DOC_VERS, 0) < NVL(agg.s_ud_vers, 0) THEN 'INSUFFICIENT_NI_UNITA_DOC_VERS' "
                    + "           WHEN NVL(att.NI_DOC_VERS, 0) < NVL(agg.s_doc_vers, 0) THEN 'INSUFFICIENT_NI_DOC_VERS' "
                    + "           WHEN NVL(att.NI_COMP_VERS, 0) < NVL(agg.s_comp_vers, 0) THEN 'INSUFFICIENT_NI_COMP_VERS' "
                    + "           WHEN NVL(att.NI_SIZE_VERS, 0) < NVL(agg.s_size_vers, 0) THEN 'INSUFFICIENT_NI_SIZE_VERS' "
                    + "           WHEN NVL(att.NI_DOC_AGG, 0) < NVL(agg.s_doc_agg, 0) THEN 'INSUFFICIENT_NI_DOC_AGG' "
                    + "           WHEN NVL(att.NI_COMP_AGG, 0) < NVL(agg.s_comp_agg, 0) THEN 'INSUFFICIENT_NI_COMP_AGG' "
                    + "           WHEN NVL(att.NI_SIZE_AGG, 0) < NVL(agg.s_size_agg, 0) THEN 'INSUFFICIENT_NI_SIZE_AGG' "
                    + "           WHEN NVL(att.NI_UNITA_DOC_ANNUL, 0) < NVL(agg.s_ud_annul, 0) THEN 'INSUFFICIENT_NI_UNITA_DOC_ANNUL' "
                    + "           WHEN NVL(att.NI_DOC_ANNUL_UD, 0) < NVL(agg.s_doc_annul, 0) THEN 'INSUFFICIENT_NI_DOC_ANNUL_UD' "
                    + "           WHEN NVL(att.NI_COMP_ANNUL_UD, 0) < NVL(agg.s_comp_annul, 0) THEN 'INSUFFICIENT_NI_COMP_ANNUL_UD' "
                    + "           WHEN NVL(att.NI_SIZE_ANNUL_UD, 0) < NVL(agg.s_size_annul, 0) THEN 'INSUFFICIENT_NI_SIZE_ANNUL_UD' "
                    + "           ELSE 'UNKNOWN' " + "         END as MOTIVO " + "  FROM ("
                    + aggSourceSql + ") agg " + "  LEFT JOIN MON_CONTA_UD_DOC_COMP att "
                    + "    ON att.DT_RIF_CONTA = agg.DT_RIF_CONTA AND att.ID_STRUT = agg.ID_STRUT AND att.ID_SUB_STRUT = agg.ID_SUB_STRUT "
                    + "   AND att.AA_KEY_UNITA_DOC = agg.AA_KEY_UNITA_DOC AND att.ID_REGISTRO_UNITA_DOC = agg.ID_REGISTRO_UNITA_DOC "
                    + "   AND att.ID_TIPO_UNITA_DOC = agg.ID_TIPO_UNITA_DOC AND att.ID_TIPO_DOC_PRINC = agg.ID_TIPO_DOC_PRINC "
                    + "  WHERE att.ID_CONTA_UD_DOC_COMP IS NULL "
                    + "     OR NVL(att.NI_UNITA_DOC_VERS, 0) < NVL(agg.s_ud_vers, 0) "
                    + "     OR NVL(att.NI_DOC_VERS, 0) < NVL(agg.s_doc_vers, 0) "
                    + "     OR NVL(att.NI_COMP_VERS, 0) < NVL(agg.s_comp_vers, 0) "
                    + "     OR NVL(att.NI_SIZE_VERS, 0) < NVL(agg.s_size_vers, 0) "
                    + "     OR NVL(att.NI_DOC_AGG, 0) < NVL(agg.s_doc_agg, 0) "
                    + "     OR NVL(att.NI_COMP_AGG, 0) < NVL(agg.s_comp_agg, 0) "
                    + "     OR NVL(att.NI_SIZE_AGG, 0) < NVL(agg.s_size_agg, 0) "
                    + "     OR NVL(att.NI_UNITA_DOC_ANNUL, 0) < NVL(agg.s_ud_annul, 0) "
                    + "     OR NVL(att.NI_DOC_ANNUL_UD, 0) < NVL(agg.s_doc_annul, 0) "
                    + "     OR NVL(att.NI_COMP_ANNUL_UD, 0) < NVL(agg.s_comp_annul, 0) "
                    + "     OR NVL(att.NI_SIZE_ANNUL_UD, 0) < NVL(agg.s_size_annul, 0) "
                    + ") WHERE ROWNUM <= 20";

            Query qMismatchSample = entityManager.createNativeQuery(mismatchSampleSql);
            qMismatchSample.setParameter("dataDa", dataCalcoloDa);
            qMismatchSample.setParameter("dataA", dataCalcoloA);
            List<Object[]> mismatchSample = qMismatchSample.getResultList();
            for (Object[] rec : mismatchSample) {
                log.error(
                        "Quadratura KO - chiave [DT_RIF_CONTA={}, ID_STRUT={}, ID_SUB_STRUT={}, AA_KEY_UNITA_DOC={}, ID_REGISTRO_UNITA_DOC={}, ID_TIPO_UNITA_DOC={}, ID_TIPO_DOC_PRINC={}] motivo={} ",
                        rec[0], rec[1], rec[2], rec[3], rec[4], rec[5], rec[6], rec[7]);
            }
            throw new IllegalStateException(
                    "Quadratura cancellazioni fisiche fallita: " + mismatchCount.longValue()
                            + " combinazioni non allineate tra delta DM e tabella attiva");
        }

        // 1. INCREMENTA LA TABELLA READ_ONLY (Storico Aggregato)
        String mergeReadOnlySql = "MERGE INTO MON_CONTA_UD_DOC_COMP_READ_ONLY ro " + "USING ("
                + "  SELECT c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, "
                + "         c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC, "
                + "         SUM(c.NI_UNITA_DOC_VERS) as s_ud_vers, SUM(c.NI_DOC_VERS) as s_doc_vers, SUM(c.NI_COMP_VERS) as s_comp_vers, SUM(c.NI_SIZE_VERS) as s_size_vers, "
                + "         SUM(c.NI_DOC_AGG) as s_doc_agg, SUM(c.NI_COMP_AGG) as s_comp_agg, SUM(c.NI_SIZE_AGG) as s_size_agg, "
                + "         SUM(c.NI_UNITA_DOC_ANNUL) as s_ud_annul, SUM(c.NI_DOC_ANNUL_UD) as s_doc_annul, SUM(c.NI_COMP_ANNUL_UD) as s_comp_annul, SUM(c.NI_SIZE_ANNUL_UD) as s_size_annul "
                + "  FROM DM_UD_DEL_CONTA c "
                + "  JOIN DM_UD_DEL d ON c.ID_UNITA_DOC = d.ID_UNITA_DOC "
                + "  WHERE d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "    AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA "
                + "  GROUP BY c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC "
                + ") agg "
                + "ON (ro.DT_RIF_CONTA = agg.DT_RIF_CONTA AND ro.ID_STRUT = agg.ID_STRUT AND ro.ID_SUB_STRUT = agg.ID_SUB_STRUT AND "
                + "    ro.AA_KEY_UNITA_DOC = agg.AA_KEY_UNITA_DOC AND ro.ID_REGISTRO_UNITA_DOC = agg.ID_REGISTRO_UNITA_DOC AND "
                + "    ro.ID_TIPO_UNITA_DOC = agg.ID_TIPO_UNITA_DOC AND ro.ID_TIPO_DOC_PRINC = agg.ID_TIPO_DOC_PRINC) "
                + "WHEN MATCHED THEN UPDATE SET "
                + "  ro.NI_UNITA_DOC_VERS = NVL(ro.NI_UNITA_DOC_VERS, 0) + agg.s_ud_vers, "
                + "  ro.NI_DOC_VERS = NVL(ro.NI_DOC_VERS, 0) + agg.s_doc_vers, "
                + "  ro.NI_COMP_VERS = NVL(ro.NI_COMP_VERS, 0) + agg.s_comp_vers, "
                + "  ro.NI_SIZE_VERS = NVL(ro.NI_SIZE_VERS, 0) + agg.s_size_vers, "
                + "  ro.NI_DOC_AGG = NVL(ro.NI_DOC_AGG, 0) + agg.s_doc_agg, "
                + "  ro.NI_COMP_AGG = NVL(ro.NI_COMP_AGG, 0) + agg.s_comp_agg, "
                + "  ro.NI_SIZE_AGG = NVL(ro.NI_SIZE_AGG, 0) + agg.s_size_agg, "
                + "  ro.NI_UNITA_DOC_ANNUL = NVL(ro.NI_UNITA_DOC_ANNUL, 0) + agg.s_ud_annul, "
                + "  ro.NI_DOC_ANNUL_UD = NVL(ro.NI_DOC_ANNUL_UD, 0) + agg.s_doc_annul, "
                + "  ro.NI_COMP_ANNUL_UD = NVL(ro.NI_COMP_ANNUL_UD, 0) + agg.s_comp_annul, "
                + "  ro.NI_SIZE_ANNUL_UD = NVL(ro.NI_SIZE_ANNUL_UD, 0) + agg.s_size_annul "
                + "WHEN NOT MATCHED THEN INSERT "
                + "  (ID_CONTA_UD_DOC_COMP, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, AA_KEY_UNITA_DOC, ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, "
                + "   NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, "
                + "   NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD) "
                + "  VALUES (SMON_CONTA_UD_DOC_COMP.NEXTVAL, agg.DT_RIF_CONTA, agg.ID_STRUT, agg.ID_SUB_STRUT, agg.AA_KEY_UNITA_DOC, agg.ID_REGISTRO_UNITA_DOC, agg.ID_TIPO_UNITA_DOC, agg.ID_TIPO_DOC_PRINC, "
                + "          agg.s_ud_vers, agg.s_doc_vers, agg.s_comp_vers, agg.s_size_vers, agg.s_doc_agg, agg.s_comp_agg, agg.s_size_agg, "
                + "          agg.s_ud_annul, agg.s_doc_annul, agg.s_comp_annul, agg.s_size_annul)";

        Query qMergeRO = entityManager.createNativeQuery(mergeReadOnlySql);
        qMergeRO.setParameter("dataDa", dataCalcoloDa);
        qMergeRO.setParameter("dataA", dataCalcoloA);
        int rowsRO = qMergeRO.executeUpdate();

        // 2. DECREMENTA LA TABELLA ATTIVA (Usa GREATEST per evitare numeri negativi)
        String updateAttivaSql = "MERGE INTO MON_CONTA_UD_DOC_COMP att " + "USING ("
                + "  SELECT c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, "
                + "         c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC, "
                + "         SUM(c.NI_UNITA_DOC_VERS) as s_ud_vers, SUM(c.NI_DOC_VERS) as s_doc_vers, SUM(c.NI_COMP_VERS) as s_comp_vers, SUM(c.NI_SIZE_VERS) as s_size_vers, "
                + "         SUM(c.NI_DOC_AGG) as s_doc_agg, SUM(c.NI_COMP_AGG) as s_comp_agg, SUM(c.NI_SIZE_AGG) as s_size_agg, "
                + "         SUM(c.NI_UNITA_DOC_ANNUL) as s_ud_annul, SUM(c.NI_DOC_ANNUL_UD) as s_doc_annul, SUM(c.NI_COMP_ANNUL_UD) as s_comp_annul, SUM(c.NI_SIZE_ANNUL_UD) as s_size_annul "
                + "  FROM DM_UD_DEL_CONTA c "
                + "  JOIN DM_UD_DEL d ON c.ID_UNITA_DOC = d.ID_UNITA_DOC "
                + "  WHERE d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "    AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA "
                + "  GROUP BY c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC "
                + ") agg "
                + "ON (att.DT_RIF_CONTA = agg.DT_RIF_CONTA AND att.ID_STRUT = agg.ID_STRUT AND att.ID_SUB_STRUT = agg.ID_SUB_STRUT AND "
                + "    att.AA_KEY_UNITA_DOC = agg.AA_KEY_UNITA_DOC AND att.ID_REGISTRO_UNITA_DOC = agg.ID_REGISTRO_UNITA_DOC AND "
                + "    att.ID_TIPO_UNITA_DOC = agg.ID_TIPO_UNITA_DOC AND att.ID_TIPO_DOC_PRINC = agg.ID_TIPO_DOC_PRINC) "
                + "WHEN MATCHED THEN UPDATE SET "
                + "  att.NI_UNITA_DOC_VERS = GREATEST(0, NVL(att.NI_UNITA_DOC_VERS, 0) - agg.s_ud_vers), "
                + "  att.NI_DOC_VERS = GREATEST(0, NVL(att.NI_DOC_VERS, 0) - agg.s_doc_vers), "
                + "  att.NI_COMP_VERS = GREATEST(0, NVL(att.NI_COMP_VERS, 0) - agg.s_comp_vers), "
                + "  att.NI_SIZE_VERS = GREATEST(0, NVL(att.NI_SIZE_VERS, 0) - agg.s_size_vers), "
                + "  att.NI_DOC_AGG = GREATEST(0, NVL(att.NI_DOC_AGG, 0) - agg.s_doc_agg), "
                + "  att.NI_COMP_AGG = GREATEST(0, NVL(att.NI_COMP_AGG, 0) - agg.s_comp_agg), "
                + "  att.NI_SIZE_AGG = GREATEST(0, NVL(att.NI_SIZE_AGG, 0) - agg.s_size_agg), "
                + "  att.NI_UNITA_DOC_ANNUL = GREATEST(0, NVL(att.NI_UNITA_DOC_ANNUL, 0) - agg.s_ud_annul), "
                + "  att.NI_DOC_ANNUL_UD = GREATEST(0, NVL(att.NI_DOC_ANNUL_UD, 0) - agg.s_doc_annul), "
                + "  att.NI_COMP_ANNUL_UD = GREATEST(0, NVL(att.NI_COMP_ANNUL_UD, 0) - agg.s_comp_annul), "
                + "  att.NI_SIZE_ANNUL_UD = GREATEST(0, NVL(att.NI_SIZE_ANNUL_UD, 0) - agg.s_size_annul)";

        Query qUpdateAttiva = entityManager.createNativeQuery(updateAttivaSql);
        qUpdateAttiva.setParameter("dataDa", dataCalcoloDa);
        qUpdateAttiva.setParameter("dataA", dataCalcoloA);
        int rowsAttiva = qUpdateAttiva.executeUpdate();

        if (rowsAttiva != totCombinazioni) {
            throw new IllegalStateException(
                    "Quadratura MERGE su tabella attiva non coerente. Combinazioni="
                            + totCombinazioni + ", righe aggiornate attiva=" + rowsAttiva);
        }
        if (rowsRO < totCombinazioni) {
            throw new IllegalStateException(
                    "Quadratura MERGE su tabella read only non coerente. Combinazioni="
                            + totCombinazioni + ", righe toccate read only=" + rowsRO);
        }

        // 3. PULIZIA ZERI DALLA TABELLA ATTIVA
        // Rimuove fisicamente i record della tabella attiva che sono stati portati a 0 dalle
        // sottrazioni.
        String cleanZerosSql = "DELETE FROM MON_CONTA_UD_DOC_COMP "
                + "WHERE NVL(NI_UNITA_DOC_VERS,0) = 0 AND NVL(NI_DOC_VERS,0) = 0 "
                + "  AND NVL(NI_COMP_VERS,0) = 0 AND NVL(NI_SIZE_VERS,0) = 0 "
                + "  AND NVL(NI_DOC_AGG,0) = 0 AND NVL(NI_COMP_AGG,0) = 0 AND NVL(NI_SIZE_AGG,0) = 0 "
                + "  AND NVL(NI_UNITA_DOC_ANNUL,0) = 0 AND NVL(NI_DOC_ANNUL_UD,0) = 0 "
                + "  AND NVL(NI_COMP_ANNUL_UD,0) = 0 AND NVL(NI_SIZE_ANNUL_UD,0) = 0";

        int rowsZeriCancellati = entityManager.createNativeQuery(cleanZerosSql).executeUpdate();

        // 4. PARACADUTE: SALVATAGGIO IN STORICO (Audit)
        // Spostiamo i delta elaborati oggi in una tabella di archivio storico prima di distruggerli
        String archiveSnapshotSql = "INSERT INTO DM_UD_DEL_CONTA_STORICO (ID_UNITA_DOC, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, AA_KEY_UNITA_DOC, "
                + "  ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, "
                + "  NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD, DT_ELABORAZIONE_JOB) "
                + "SELECT c.ID_UNITA_DOC, c.DT_RIF_CONTA, c.ID_STRUT, c.ID_SUB_STRUT, c.AA_KEY_UNITA_DOC, "
                + "  c.ID_REGISTRO_UNITA_DOC, c.ID_TIPO_UNITA_DOC, c.ID_TIPO_DOC_PRINC, c.NI_UNITA_DOC_VERS, c.NI_DOC_VERS, c.NI_COMP_VERS, c.NI_SIZE_VERS, "
                + "  c.NI_DOC_AGG, c.NI_COMP_AGG, c.NI_SIZE_AGG, c.NI_UNITA_DOC_ANNUL, c.NI_DOC_ANNUL_UD, c.NI_COMP_ANNUL_UD, c.NI_SIZE_ANNUL_UD, SYSDATE "
                + "FROM DM_UD_DEL_CONTA c " + "WHERE EXISTS ( " + "  SELECT 1 FROM DM_UD_DEL d "
                + "  WHERE d.ID_UNITA_DOC = c.ID_UNITA_DOC "
                + "    AND d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "    AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA "
                + ")";

        Query qArchive = entityManager.createNativeQuery(archiveSnapshotSql);
        qArchive.setParameter("dataDa", dataCalcoloDa);
        qArchive.setParameter("dataA", dataCalcoloA);
        int rowsArchived = qArchive.executeUpdate();

        // 5. PULIZIA SNAPSHOT (Svuotiamo la tabella di lavoro)
        String cleanSnapshotSql = "DELETE FROM DM_UD_DEL_CONTA c " + "WHERE EXISTS ( "
                + "  SELECT 1 FROM DM_UD_DEL d " + "  WHERE d.ID_UNITA_DOC = c.ID_UNITA_DOC "
                + "    AND d.TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER' "
                + "    AND d.DT_STATO_UD_CANCELLATE >= :dataDa AND d.DT_STATO_UD_CANCELLATE < :dataA "
                + ")";

        Query qClean = entityManager.createNativeQuery(cleanSnapshotSql);
        qClean.setParameter("dataDa", dataCalcoloDa);
        qClean.setParameter("dataA", dataCalcoloA);
        int rowsDeleted = qClean.executeUpdate();

        if (rowsArchived != rowsDeleted) {
            throw new IllegalStateException(
                    "Quadratura archive/delete DM_UD_DEL_CONTA non coerente. Archiviati="
                            + rowsArchived + ", cancellati=" + rowsDeleted);
        }

        log.info(
                "Spostamento conteggi concluso. Aggiornati {} aggr. in RO, {} in Attiva. Rimosse {} righe azzerate. "
                        + "Archiviati {} e puliti {} record di log storici. "
                        + "Sintesi [combinazioni={}, righeDm={}, udCoinvolte={}, "
                        + "udVers={}, docVers={}, compVers={}, sizeVers={}, docAgg={}, compAgg={}, sizeAgg={}, "
                        + "udAnnul={}, docAnnul={}, compAnnul={}, sizeAnnul={}].",
                rowsRO, rowsAttiva, rowsZeriCancellati, rowsArchived, rowsDeleted, totCombinazioni,
                totRigheDm, totUdCoinvolte, chkUdVers, chkDocVers, chkCompVers, chkSizeVers,
                chkDocAgg, chkCompAgg, chkSizeAgg, chkUdAnnul, chkDocAnnul, chkCompAnnul,
                chkSizeAnnul);
    }

    // METODO DEDICATO: GESTIONE SGANCIAMENTO INTERA STRUTTURA (Restituzione Archivio)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciRestituzioneArchivio(Long idStrut) {
        log.info(
                "Calcolo Contenuto Sacer - Inizio allineamento contatori per Restituzione Archivio. ID_STRUT: {}",
                idStrut);

        // 1. TRAVASO DIRETTO: Sposta tutto l'attivo della struttura nella READ_ONLY.
        // Se ci sono già righe per quelle combinazioni nello storico, le somma.
        String mergeSql = "MERGE INTO MON_CONTA_UD_DOC_COMP_READ_ONLY ro "
                + "USING (SELECT * FROM MON_CONTA_UD_DOC_COMP WHERE ID_STRUT = :idStrut) att "
                + "ON (ro.DT_RIF_CONTA = att.DT_RIF_CONTA AND ro.ID_STRUT = att.ID_STRUT AND ro.ID_SUB_STRUT = att.ID_SUB_STRUT "
                + "    AND ro.AA_KEY_UNITA_DOC = att.AA_KEY_UNITA_DOC AND ro.ID_REGISTRO_UNITA_DOC = att.ID_REGISTRO_UNITA_DOC "
                + "    AND ro.ID_TIPO_UNITA_DOC = att.ID_TIPO_UNITA_DOC AND ro.ID_TIPO_DOC_PRINC = att.ID_TIPO_DOC_PRINC) "
                + "WHEN MATCHED THEN UPDATE SET "
                + "    ro.NI_UNITA_DOC_VERS = NVL(ro.NI_UNITA_DOC_VERS, 0) + NVL(att.NI_UNITA_DOC_VERS, 0), "
                + "    ro.NI_DOC_VERS = NVL(ro.NI_DOC_VERS, 0) + NVL(att.NI_DOC_VERS, 0), "
                + "    ro.NI_COMP_VERS = NVL(ro.NI_COMP_VERS, 0) + NVL(att.NI_COMP_VERS, 0), "
                + "    ro.NI_SIZE_VERS = NVL(ro.NI_SIZE_VERS, 0) + NVL(att.NI_SIZE_VERS, 0), "
                + "    ro.NI_DOC_AGG = NVL(ro.NI_DOC_AGG, 0) + NVL(att.NI_DOC_AGG, 0), "
                + "    ro.NI_COMP_AGG = NVL(ro.NI_COMP_AGG, 0) + NVL(att.NI_COMP_AGG, 0), "
                + "    ro.NI_SIZE_AGG = NVL(ro.NI_SIZE_AGG, 0) + NVL(att.NI_SIZE_AGG, 0), "
                + "    ro.NI_UNITA_DOC_ANNUL = NVL(ro.NI_UNITA_DOC_ANNUL, 0) + NVL(att.NI_UNITA_DOC_ANNUL, 0), "
                + "    ro.NI_DOC_ANNUL_UD = NVL(ro.NI_DOC_ANNUL_UD, 0) + NVL(att.NI_DOC_ANNUL_UD, 0), "
                + "    ro.NI_COMP_ANNUL_UD = NVL(ro.NI_COMP_ANNUL_UD, 0) + NVL(att.NI_COMP_ANNUL_UD, 0), "
                + "    ro.NI_SIZE_ANNUL_UD = NVL(ro.NI_SIZE_ANNUL_UD, 0) + NVL(att.NI_SIZE_ANNUL_UD, 0) "
                + "WHEN NOT MATCHED THEN INSERT "
                + "    (ID_CONTA_UD_DOC_COMP, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, AA_KEY_UNITA_DOC, "
                + "     ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, "
                + "     NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, "
                + "     NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, "
                + "     NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD) "
                + "    VALUES (SMON_CONTA_UD_DOC_COMP.NEXTVAL, att.DT_RIF_CONTA, att.ID_STRUT, att.ID_SUB_STRUT, att.AA_KEY_UNITA_DOC, "
                + "            att.ID_REGISTRO_UNITA_DOC, att.ID_TIPO_UNITA_DOC, att.ID_TIPO_DOC_PRINC, "
                + "            NVL(att.NI_UNITA_DOC_VERS, 0), NVL(att.NI_DOC_VERS, 0), NVL(att.NI_COMP_VERS, 0), NVL(att.NI_SIZE_VERS, 0), "
                + "            NVL(att.NI_DOC_AGG, 0), NVL(att.NI_COMP_AGG, 0), NVL(att.NI_SIZE_AGG, 0), "
                + "            NVL(att.NI_UNITA_DOC_ANNUL, 0), NVL(att.NI_DOC_ANNUL_UD, 0), NVL(att.NI_COMP_ANNUL_UD, 0), NVL(att.NI_SIZE_ANNUL_UD, 0))";

        Query queryMerge = entityManager.createNativeQuery(mergeSql);
        queryMerge.setParameter("idStrut", idStrut);
        int mergedRows = queryMerge.executeUpdate();

        // 2. SCRITTURA LOG DI AUDIT (Aggregato)
        // Registriamo nello storico COSA abbiamo spostato, così la query di riconciliazione
        // quadrerà
        String logSql = "INSERT INTO DM_UD_DEL_CONTA_STORICO (ID_UNITA_DOC, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, "
                + "  AA_KEY_UNITA_DOC, ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, "
                + "  NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, "
                + "  NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD, DT_ELABORAZIONE_JOB) "
                + "SELECT 0, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, "
                + "  AA_KEY_UNITA_DOC, ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, "
                + "  NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, "
                + "  NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD, SYSDATE "
                + "FROM MON_CONTA_UD_DOC_COMP WHERE ID_STRUT = :idStrut";

        entityManager.createNativeQuery(logSql).setParameter("idStrut", idStrut).executeUpdate();

        // 3. PULIZIA: Dato che la struttura è bloccata, posso eliminare tutto l'attivo in totale
        // sicurezza.
        String deleteSql = "DELETE FROM MON_CONTA_UD_DOC_COMP WHERE ID_STRUT = :idStrut";
        Query queryDelete = entityManager.createNativeQuery(deleteSql);
        queryDelete.setParameter("idStrut", idStrut);
        int deletedRows = queryDelete.executeUpdate();

        log.info(
                "Restituzione Archivio completata per ID_STRUT {}. {} righe aggregate spostate nello Storico. {} righe cancellate dall'Attivo.",
                idStrut, mergedRows, deletedRows);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciRestituzioniArchivioDelGiorno(Date dataCalcoloDa) {
        Date dataCalcoloA = org.apache.commons.lang3.time.DateUtils.addDays(dataCalcoloDa, 1);

        // Cerco l'ID_STRUT collegato a una richiesta di RESTITUZIONE che ha terminato TUTTO l'iter
        // (stato EVASA) esattamente nel giorno in esame.
        String findStrutSql = "SELECT DISTINCT d.ID_STRUT " + "FROM DM_UD_DEL_RICHIESTE r "
                + "JOIN DM_UD_DEL d ON r.ID_UD_DEL_RICHIESTA = d.ID_UD_DEL_RICHIESTA "
                + "WHERE r.TI_MOT_CANCELLAZIONE = 'R' " + "  AND r.TI_STATO_RICHIESTA = 'EVASA' " + // <--
                                                                                                    // Controlla
                                                                                                    // che
                                                                                                    // TUTTA
                                                                                                    // la
                                                                                                    // richiesta
                                                                                                    // sia
                                                                                                    // finita
                "  AND r.DT_EVASIONE >= :dataDa AND r.DT_EVASIONE < :dataA";

        Query queryFind = entityManager.createNativeQuery(findStrutSql);
        queryFind.setParameter("dataDa", dataCalcoloDa);
        queryFind.setParameter("dataA", dataCalcoloA);

        List<Number> idStrutture = queryFind.getResultList();

        // Se non trova nulla, questo ciclo viene semplicemente saltato.
        for (Number idStrutNum : idStrutture) {
            Long idStrut = idStrutNum.longValue();
            gestisciRestituzioneArchivio(idStrut);
        }
    }

    // end MEV #37227

}
