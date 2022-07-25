package it.eng.parer.job.calcoloContenutoSacer.ejb;

import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.MonContaUdDocComp;
import it.eng.parer.entity.MonContaUdDocComp.TipoConteggio;
import it.eng.parer.entity.MonTipoUnitaDocUserVers;
import it.eng.parer.grantedEntity.OrgServizioErog;
import it.eng.parer.grantedViewEntity.OrgVCalcDtErog;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.viewEntity.OrgVServSistVersDaErog;
import it.eng.parer.viewEntity.OrgVServTiServDaErog;
import it.eng.parer.ws.utils.CostantiDB;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CalcoloContenutoSacerHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoSacerHelper {

    private static final Logger log = LoggerFactory.getLogger(CalcoloContenutoSacerHelper.class);
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    private static final String versUD = "select new it.eng.parer.entity.MonContaUdDocComp("
            + " FUNC('trunc',docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " FUNC('nvl',count(ud.idOrgStrut), 0), 'UD_VERS' ) " + "from AroDoc docPrinc, OrgStrut ostrut "
            + "join docPrinc.aroUnitaDoc ud " + "where docPrinc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
            + "and ostrut.idStrut = docPrinc.idStrut " + "and docPrinc.tiDoc = 'PRINCIPALE' "
            + "and docPrinc.dtCreazione >= :dataDa " + "and docPrinc.dtCreazione < :dataA "
            + "group by FUNC('trunc',docPrinc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc,  docPrinc.idDecTipoDoc";

    private static final String versDoc = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "FUNC('trunc',doc.dtCreazione), ud.idOrgStrut,  ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " FUNC('nvl',count(ud.idOrgStrut), 0), 'DOC_VERS' )"
            + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut " + "join doc.aroUnitaDoc ud "
            + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa " + "and doc.dtCreazione < :dataA "
            + "group by FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";

    private static final String versComp = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  FUNC('nvl',count(ud.idOrgStrut), 0),  FUNC('nvl', sum(comp.niSizeFileCalc), 0), " + " 'COMP_VERS' )"
            + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc " + "join comp.aroStrutDoc strut "
            + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' AND docPrinc.tiDoc = 'PRINCIPALE' AND ud = ud2 "
            + "and ostrut.idStrut = doc.idStrut " + "and doc.dtCreazione >= :dataDa " + "and doc.dtCreazione < :dataA "
            + "group by FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";
    private static final String aggComp = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + " ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + "  FUNC('nvl',count(ud.idOrgStrut), 0),  FUNC('nvl', sum(comp.niSizeFileCalc), 0), " + " 'COMP_AGG' )"
            + "from AroCompDoc comp, OrgStrut ostrut, AroDoc docPrinc " + "join comp.aroStrutDoc strut "
            + "join strut.aroDoc doc " + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
            + " and ostrut.idStrut = doc.idStrut " + " and doc.dtCreazione >= :dataDa "
            + " and doc.dtCreazione < :dataA "
            + " group by FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc";

    /* Nuove query per il calcolo delle ud, doc e comp annullati */
    private String getAnnullQuery(String nvl, String join, String where) {
        String genericAnnull = "SELECT new it.eng.parer.entity.MonContaUdDocComp("
                + "FUNC('trunc', statoCorRich.dtRegStatoRichAnnulVers), strut.idStrut, "
                + "subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc, "
                + nvl + "FROM AroItemRichAnnulVers itemAnnul " + "JOIN itemAnnul.aroRichAnnulVers richAnnul "
                + "JOIN richAnnul.orgStrut strut " + "JOIN itemAnnul.aroUnitaDoc unitaDoc "
                + "JOIN unitaDoc.orgSubStrut subStrut " + "JOIN unitaDoc.aroDocs docPrinc, "
                + "AroStatoRichAnnulVers statoCorRich " + join
                + "WHERE statoCorRich.idStatoRichAnnulVers = richAnnul.idStatoRichAnnulVersCor "
                + "AND itemAnnul.tiItemRichAnnulVers = 'UNI_DOC' " + "AND itemAnnul.tiStatoItem = 'ANNULLATO' "
                + "AND docPrinc.tiDoc = 'PRINCIPALE' " + "AND statoCorRich.tiStatoRichAnnulVers = 'EVASA' "
                + "AND statoCorRich.dtRegStatoRichAnnulVers >= :dataDa "
                + "AND statoCorRich.dtRegStatoRichAnnulVers < :dataA " + where
                + "GROUP BY FUNC('trunc', statoCorRich.dtRegStatoRichAnnulVers), strut.idStrut, subStrut.idSubStrut, unitaDoc.decRegistroUnitaDoc.idRegistroUnitaDoc, unitaDoc.aaKeyUnitaDoc, unitaDoc.decTipoUnitaDoc.idTipoUnitaDoc, docPrinc.decTipoDoc.idTipoDoc ";

        return genericAnnull;
    }

    private static final String joinDoc = ", AroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 ";
    private static final String joinComp = ", AroCompDoc compDoc JOIN compDoc.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc unitaDoc2 ";
    private static final String where = " AND unitaDoc2 = unitaDoc ";
    private static final String nvlUd = "FUNC('nvl', count(unitaDoc), 0), 'UD_ANNULL') ";
    private static final String nvlDoc = "FUNC('nvl', count(doc), 0), 'DOC_ANNULL') ";
    private static final String nvlComp = "FUNC('nvl', count(compDoc), 0), FUNC('nvl', sum(compDoc.niSizeFileCalc), 0), 'COMP_ANNULL') ";

    private static final String aggDoc = "select new it.eng.parer.entity.MonContaUdDocComp("
            + "FUNC('trunc',doc.dtCreazione), ud.idOrgStrut,  ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
            + "ud.aaKeyUnitaDoc, ud.idDecTipoUnitaDoc, docPrinc.idDecTipoDoc, "
            + " FUNC('nvl',count(ud.idOrgStrut), 0), 'DOC_AGG' )" + "from AroDoc doc, AroDoc docPrinc, OrgStrut ostrut "
            + "join doc.aroUnitaDoc ud " + "join docPrinc.aroUnitaDoc ud2 "
            + "where doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + " AND docPrinc.tiDoc = 'PRINCIPALE' " + " AND ud = ud2 "
            + " AND ostrut.idStrut = doc.idStrut " + " AND doc.dtCreazione >= :dataDa "
            + " AND doc.dtCreazione < :dataA "
            + " group by FUNC('trunc',doc.dtCreazione), ud.idOrgStrut, ud.idOrgSubStrut, ud.idDecRegistroUnitaDoc, "
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertTotaliPerGiorno(Date dataCalcoloDa) throws IOException {
        Date dataCalcoloA = DateUtils.addDays(dataCalcoloDa, 1);
        final Map<MonContaUdDocComp, MonContaUdDocComp> res = new HashMap<MonContaUdDocComp, MonContaUdDocComp>();
        List<MonContaUdDocComp> resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, versUD);
        addOrSetContToResult(resParziale, res, TipoConteggio.UD_VERS);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, versDoc);
        addOrSetContToResult(resParziale, res, TipoConteggio.DOC_VERS);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, versComp);
        addOrSetContToResult(resParziale, res, TipoConteggio.COMP_VERS);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, getAnnullQuery(nvlUd, "", ""));
        addOrSetContToResult(resParziale, res, TipoConteggio.UD_ANNULL);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, getAnnullQuery(nvlDoc, joinDoc, where));
        addOrSetContToResult(resParziale, res, TipoConteggio.DOC_ANNULL);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, getAnnullQuery(nvlComp, joinComp, where));
        addOrSetContToResult(resParziale, res, TipoConteggio.COMP_ANNULL);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, aggDoc);
        addOrSetContToResult(resParziale, res, TipoConteggio.DOC_AGG);
        resParziale = executeQueryCalcolo(dataCalcoloDa, dataCalcoloA, aggComp);
        addOrSetContToResult(resParziale, res, TipoConteggio.COMP_AGG);
        for (Map.Entry<MonContaUdDocComp, MonContaUdDocComp> record : res.entrySet()) {
            entityManager.persist(record.getValue());
        }

        /*
         * Registra nella tabella MON_TIPO_UNITA_DOC_USER_VERS il totale delle unità documentarie versate per tipo ud ed
         * utente versatore nel giorno considerato
         */
        Query q = entityManager.createQuery("SELECT FUNC('trunc', ud.dtCreazione), ud.idDecTipoUnitaDoc, "
                + "ud.iamUser.idUserIam, FUNC('nvl',count(ud.idOrgStrut), 0) " + "FROM AroUnitaDoc ud "
                + "WHERE ud.dtCreazione >= :dataDa " + "AND ud.dtCreazione < :dataA "
                + "GROUP BY FUNC('trunc',ud.dtCreazione), ud.idDecTipoUnitaDoc, ud.iamUser.idUserIam ");

        q.setParameter("dataDa", dataCalcoloDa);
        q.setParameter("dataA", dataCalcoloA);
        List<Object[]> objArrList = q.getResultList();

        for (Object[] objArr : objArrList) {
            insertMonTipoUnitaDocUserVers((Long) objArr[1], (Long) objArr[2], (Date) objArr[0], (BigDecimal) objArr[3]);
        }
    }

    public void setDtErogBySistVers(LogParam param) {
        // Ricavo la lista dei record (sistemi versanti) aventi dtErog nulla
        String queryStr = "SELECT servSistVersDaErog FROM OrgVServSistVersDaErog servSistVersDaErog "
                + "WHERE servSistVersDaErog.dtErog IS NULL ";
        Query query = entityManager.createQuery(queryStr);
        List<OrgVServSistVersDaErog> servSistVersDaErogList = (List<OrgVServSistVersDaErog>) query.getResultList();
        // Scorro i sistemi versanti per ricavare la data di erogazione
        for (OrgVServSistVersDaErog servSistVersDaErog : servSistVersDaErogList) {
            // Ricavo la lista degli id delle strutture, splittando le "," e togliendo gli spazi bianchi, per sicurezza
            // anche all'inizio e alla fine
            List<String> idStrutStringList = Arrays.asList(servSistVersDaErog.getListStrut().trim().split("\\s*,\\s*"));
            // Converto gli id string in id numerici
            List<BigDecimal> idStrutList = new ArrayList<>();
            for (String value : idStrutStringList) {
                idStrutList.add(new BigDecimal(value));
            }

            // Ricavo la lista di tipi ud con un determinato tipo servizio (ATTIVAZIONE) per tutte le strutture
            // considerate
            List<Long> listaIdTipiUd = getIdTipiUnitaDocByStrutAndTipoServizio(servSistVersDaErog.getIdTipoServizio(),
                    idStrutList, CostantiDB.TiClasseTipoServizio.ATTIVAZIONE_SISTEMA_VERSANTE);

            // Ricavo la data di erogazione "minima" (dtFirstVers)
            Date dtFirstVers = getMinimumDtRifContaBySistVers(listaIdTipiUd, servSistVersDaErog.getIdSistemaVersante());
            if (dtFirstVers != null) {
                // Eseguo l’update della tabella di IAM ORG_SERVIZIO_EROG
                updateIAMOrgServizioErog(param, servSistVersDaErog.getIdServizioErogato(),
                        servSistVersDaErog.getIdEnteConvenz(), dtFirstVers);
            }
        }
    }

    public void setDtErog(LogParam param) {
        // Ricavo la lista dei record (servizi erogati) aventi dtErog nulla
        String queryStr = "SELECT servizioErog FROM OrgServizioErog servizioErog "
                + "WHERE servizioErog.dtErog IS NULL ";
        Query query = entityManager.createQuery(queryStr);
        List<OrgServizioErog> servizioErogList = (List<OrgServizioErog>) query.getResultList();
        // Scorro i servizi erogati per ricavare la data di erogazione
        for (OrgServizioErog servizioErog : servizioErogList) {
            OrgVCalcDtErog calcDtErog = entityManager.find(OrgVCalcDtErog.class,
                    BigDecimal.valueOf(servizioErog.getIdServizioErogato()));

            if (calcDtErog != null) {
                // if (servizioErog.getOrgAccordoEnte().getDtDecAccordo().before(calcDtErog.getDtErog())
                // && servizioErog.getOrgAccordoEnte().getDtScadAccordo().after(calcDtErog.getDtErog())) {
                servizioErog.setDtErog(calcDtErog.getDtErog());
                entityManager.flush();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_ENTE_CONVENZIONATO,
                        BigDecimal.valueOf(servizioErog.getOrgAccordoEnte().getSiOrgEnteConvenz().getIdEnteSiam()),
                        param.getNomePagina());
                // }
            }
        }
    }

    public void setDtErogByTiServ(LogParam param) {
        // Ricavo la lista dei record (servizi erogati) aventi dtErog nulla
        String queryStr = "SELECT servTiServDaErog FROM OrgVServTiServDaErog servTiServDaErog "
                + "WHERE servTiServDaErog.dtErog IS NULL ";
        Query query = entityManager.createQuery(queryStr);
        List<OrgVServTiServDaErog> servTiServDaErogList = (List<OrgVServTiServDaErog>) query.getResultList();
        // Scorro i servizi erogati per ricavare la data di erogazione
        for (OrgVServTiServDaErog servTiServDaErog : servTiServDaErogList) {
            // Per ogni servizio erogato, ricavo la lista degli id delle strutture, splittando le "," e togliendo gli
            // spazi bianchi, per sicurezza anche all'inizio e alla fine
            List<String> idStrutStringList = Arrays.asList(servTiServDaErog.getListStrut().trim().split("\\s*,\\s*"));
            // Converto gli id string in id numerici
            List<BigDecimal> idStrutList = new ArrayList<>();
            for (String value : idStrutStringList) {
                idStrutList.add(new BigDecimal(value));
            }

            // Ricavo la lista di tipi ud con un determinato tipo servizio (CONSERVAZIONE) per tutte le strutture
            // considerate
            List<Long> listaIdTipiUd = getIdTipiUnitaDocByStrutAndTipoServizio(servTiServDaErog.getIdTipoServizio(),
                    idStrutList, CostantiDB.TiClasseTipoServizio.CONSERVAZIONE);

            // Ricavo la dtRifConta considerando tutti i tipiUd di tutte le strutture di un determinato tipo servizio
            Date dtRifConta = getMinimumDtRifConta(listaIdTipiUd);
            if (dtRifConta != null) {

                updateIAMOrgServizioErog(param, servTiServDaErog.getIdServizioErogato(),
                        servTiServDaErog.getIdEnteConvenz(), dtRifConta);
            }
        }
    }

    private void updateIAMOrgServizioErog(LogParam param, BigDecimal idServizioErogato, BigDecimal idEnteConvenzionato,
            Date dtErog) {
        Query q = entityManager.createQuery("UPDATE OrgServizioErog servizioErog SET servizioErog.dtErog = :dtErog "
                + "WHERE servizioErog.idServizioErogato = :idServizioErogato ");
        q.setParameter("idServizioErogato", idServizioErogato.longValue());
        q.setParameter("dtErog", dtErog);
        q.executeUpdate();
        entityManager.flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_ENTE_CONVENZIONATO, idEnteConvenzionato,
                param.getNomePagina());
    }

    private void addOrSetContToResult(List<MonContaUdDocComp> cont, Map<MonContaUdDocComp, MonContaUdDocComp> res,
            TipoConteggio tipoConteggio) {
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
     * Restituisce gli id tipi unità documentaria per quel tipo servizio per le strutture passate come parametro. Null
     * se non sono presenti record.
     *
     * @param idTipoServizio
     * 
     * @return List<Long>
     */
    private List<Long> getIdTipiUnitaDocByStrutAndTipoServizio(BigDecimal idTipoServizio, List<BigDecimal> idStrutList,
            CostantiDB.TiClasseTipoServizio tiClasseTipoServizio) {

        String queryStr = String.format(
                "SELECT tipoUnitaDoc.idTipoUnitaDoc FROM DecTipoUnitaDoc tipoUnitaDoc "
                        + "WHERE tipoUnitaDoc.orgStrut.idStrut IN :idStrut "
                        + "AND tipoUnitaDoc.%s.idTipoServizio = :idTipoServizio ",
                tiClasseTipoServizio.equals(CostantiDB.TiClasseTipoServizio.CONSERVAZIONE) ? "orgTipoServizio"
                        : "orgTipoServizioAttiv");

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrutList);
        query.setParameter("idTipoServizio", idTipoServizio.longValue());
        List<Long> lista = (List<Long>) query.getResultList();
        return lista;
    }

    private Date getMinimumDtRifConta(List<Long> idTipoUnitaDocList) {
        if (idTipoUnitaDocList.size() > 0) {
            String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon "
                    + "WHERE mon.decTipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList "
                    + "ORDER BY mon.dtRifConta ASC";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
            List<Date> lista = (List<Date>) query.getResultList();
            if (!lista.isEmpty()) {
                return lista.get(0);
            }
        }
        return null;
    }

    private Date getMinimumDtRifContaBySistVers(List<Long> idTipoUnitaDocList, BigDecimal idSistemaVersante) {
        if (idTipoUnitaDocList.size() > 0) {
            String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon " + "JOIN mon.iamUser iamUser "
                    + "WHERE mon.decTipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList "
                    + "AND EXISTS (SELECT usrUser FROM UsrUser usrUser "
                    + "WHERE usrUser.aplSistemaVersante.idSistemaVersante = :idSistemaVersante AND usrUser.idUserIam = iamUser.idUserIam) "
                    + "ORDER BY mon.dtRifConta ASC";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
            query.setParameter("idSistemaVersante", idSistemaVersante);
            List<Date> lista = (List<Date>) query.getResultList();
            if (!lista.isEmpty()) {
                return lista.get(0);
            }
        }
        return null;
    }

    private Date getMinimumDtRifContaBySistVers(Long idTipoUnitaDoc, BigDecimal idSistemaVersante) {
        String queryStr = "SELECT mon.dtRifConta FROM MonTipoUnitaDocUserVers mon " + "JOIN mon.iamUser iamUser "
                + "WHERE mon.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc "
                + "AND EXISTS (SELECT usrUser FROM UsrUser usrUser "
                + "WHERE usrUser.aplSistemaVersante.idSistemaVersante = :idSistemaVersante AND usrUser.idUserIam = iamUser.idUserIam) "
                + "ORDER BY mon.dtRifConta ASC";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        query.setParameter("idSistemaVersante", idSistemaVersante);
        List<Date> lista = (List<Date>) query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
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
        Query q = entityManager.createQuery("SELECT dec.idSistemaVersante FROM DecVLisSisVersByTipoUd dec "
                + "WHERE dec.idTipoUnitaDoc = :idTipoUnitaDoc ");
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return (List<BigDecimal>) q.getResultList();
    }
}
