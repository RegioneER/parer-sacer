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

package it.eng.parer.async.helper;

import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.AroAipRaTipologiaOggetto;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Constants;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "CalcoloEstrazioneHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloEstrazioneHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(CalcoloEstrazioneHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private CalcoloEstrazioneHelper me;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public AroRichiestaRa writeAtomicAroRichiestaRa(Long idStrut, BigDecimal priorita) {
        Date date = new Date();
        AroRichiestaRa newRichiestaRaJob = new AroRichiestaRa();
        newRichiestaRaJob.setTsInizio(date);
        OrgStrut orgStrut = null;
        if (idStrut != null) {
            orgStrut = entityManager.find(OrgStrut.class, idStrut);
        }
        newRichiestaRaJob.setOrgStrut(orgStrut);
        newRichiestaRaJob.setTiStato(AroRichiestaTiStato.CALCOLO_AIP_IN_CORSO);
        newRichiestaRaJob.setPriorita(priorita);

        return entityManager.merge(newRichiestaRaJob);
    }

    public void writeAroAipRestituzioneArchivio(AroIndiceAipUd indiceAipUd, AroRichiestaRa richiestaRa,
            TiStatoAroAipRa tiStato, AroAipRaTipologiaOggetto tiTipologiaOggetto) {
        AroAipRestituzioneArchivio aipRestituzioneArch = new AroAipRestituzioneArchivio();
        aipRestituzioneArch.setAroIndiceAipUd(indiceAipUd);
        aipRestituzioneArch.setTiTipologiaOggetto(tiTipologiaOggetto);
        aipRestituzioneArch.setTiStato(tiStato);
        aipRestituzioneArch.setAroRichiestaRa(richiestaRa);

        entityManager.persist(aipRestituzioneArch);

        if (richiestaRa.getAroAipRestituzioneArchivios() == null) {
            richiestaRa.setAroAipRestituzioneArchivios(new ArrayList<>());
        }
        richiestaRa.getAroAipRestituzioneArchivios().add(aipRestituzioneArch);
    }

    public List<Long> retrieveAroUnitaDocList(BigDecimal idEnte) {
        StringBuilder queryStr = new StringBuilder("SELECT u.idUnitaDoc FROM AroUnitaDoc u ");
        String whereWord = "WHERE ";
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.idEnte = :idEnte ");
        }

        Query query = entityManager.createQuery(queryStr.toString());
        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<OrgEnte> retrieveEnti() {
        Query query = entityManager.createQuery("SELECT e FROM OrgEnte e ORDER BY e.idEnte");
        return query.getResultList();
    }

    public List<OrgStrut> retrieveStrutture() {
        Query q = entityManager.createQuery("SELECT s FROM OrgStrut s ORDER BY s.idStrut");
        return q.getResultList();
    }

    public List<OrgStrut> retrieveStrutture(BigDecimal idEnte) {
        Query q = entityManager
                .createQuery("SELECT s FROM OrgStrut s WHERE s.orgEnte.idEnte = :idEnte ORDER BY s.idStrut");
        q.setParameter("idEnte", longFromBigDecimal(idEnte));
        return q.getResultList();
    }

    public List<IamUser> retrieveIamUserByName(String nmUserid) {
        Query q = entityManager.createQuery("select iu from IamUser iu where iu.nmUserid = :nmUseridIn");
        q.setParameter("nmUseridIn", nmUserid);
        return q.getResultList();
    }

    public List<AroRichiestaRa> retrieveRichieste() {
        Query q = entityManager.createQuery("SELECT r FROM AroRichiestaRa r ORDER BY r.priorita DESC, r.tsFine ASC");
        return q.getResultList();
    }

    public OrgEnte retrieveOrgEnteById(BigDecimal idEnte) {
        return entityManager.find(OrgEnte.class, idEnte.longValue());
    }

    public SIOrgEnteSiam retrieveOrgEnteConvenzById(BigDecimal idEnteConvenz) {
        return entityManager.find(SIOrgEnteSiam.class, idEnteConvenz.longValue());
    }

    public OrgStrut retrieveOrgStrutById(BigDecimal idStrut) {
        return entityManager.find(OrgStrut.class, idStrut.longValue());
    }

    public AroUnitaDoc retrieveUnitaDocById(long idUnitaDoc) {
        return entityManager.find(AroUnitaDoc.class, idUnitaDoc);
    }

    public AroIndiceAipUd retrieveIndiceAipUdById(long idIndiceAipUd) {
        return entityManager.find(AroIndiceAipUd.class, idIndiceAipUd);
    }

    public AroRichiestaRa retrieveRichiestaById(Long idRichiestaRa) {
        return entityManager.find(AroRichiestaRa.class, idRichiestaRa);
    }

    public String getTipoSaveFile(BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = getEntityManager().find(DecTipoUnitaDoc.class, idTipoUnitaDoc.longValue());

        return tipoUnitaDoc.getTiSaveFile();
    }

    public AroAipRestituzioneArchivio retrieveAroAipRestituzioneArchivioById(Long idAipRestArchivio) {
        return entityManager.find(AroAipRestituzioneArchivio.class, idAipRestArchivio);
    }

    public LogJob retrieveLogJobById(long idLogJob) {
        return entityManager.find(LogJob.class, idLogJob);
    }

    public void lockUnitaDoc(AroUnitaDoc ud) {
        entityManager.lock(ud, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lockIndiceAipUd(AroIndiceAipUd indiceAipUd) {
        entityManager.lock(indiceAipUd, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<Long> retrieveRichiesteScaduteDaProcessare(long idStrut) {
        Date systemDate = new Date();
        Query q = entityManager.createQuery("SELECT richiestaRa.idRichiestaRa " + "FROM AroRichiestaRa richiestaRa "
                + "WHERE (richiestaRa.tiStato != :tiStato " + "AND richiestaRa.tsInizio + 1 < :systemDate "
                + "AND richiestaRa.orgStrut.idStrut = :idStrut)");
        q.setParameter("tiStato", AroRichiestaTiStato.ANNULLATO);
        q.setParameter("systemDate", systemDate);
        q.setParameter("idStrut", idStrut);
        return q.getResultList();
    }

    public List<Long> retrieveRichiesteEstrazioniInCorso(long idStrut) {
        Query q = entityManager.createQuery("SELECT richiestaRa.idRichiestaRa " + "FROM AroRichiestaRa richiestaRa "
                + "WHERE (richiestaRa.tiStato = :tiStato " + "AND richiestaRa.orgStrut.idStrut != :idStrut)");
        q.setParameter("tiStato", AroRichiestaTiStato.ESTRAZIONE_IN_CORSO);
        q.setParameter("idStrut", idStrut);
        return q.getResultList();
    }

    public boolean checkRichiestaInCoda(BigDecimal idEnteConvenz) {
        Query q = entityManager.createQuery(
                "SELECT rich " + "FROM AroRichiestaRa rich " + "WHERE rich.orgStrut.idEnteConvenz = :idEnteConvenz "
                        + "AND rich.tsFine IS NOT NULL " + "AND rich.tiStato != :tiStato");
        q.setParameter("idEnteConvenz", idEnteConvenz);
        q.setParameter("tiStato", AroRichiestaTiStato.ANNULLATO);
        return !q.getResultList().isEmpty();
    }

    /**
     * Seleziona le unità documentarie, le serie e i fascicoli appartenenti all'ente convenzionato passato come
     * parametro ritornandole sotto forma di insieme UdSerFascObj
     *
     * @param struttura
     *            entity OrgStrut
     * 
     * @return lista elementi di tipo UdSerFascObj
     */
    public List<UdSerFascObj> retrieveUdSerFascToProcess(OrgStrut struttura) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.id.idUnitaDoc, u.id.idSerie, u.id.idFascicolo, u.tiEle "
                        + "FROM AroVSelUdSerFascByEnte u " + "WHERE u.id.idRootstrut = :idRootstrut ");

        // TIP: fdilorenzo, DEFINISCE L'ORDINAMENTO CON CUI DEVONO ESSERE ELABORATI GLI OGGETTI (A SUPPORTO DELLA LOGICA
        // DEFINITA IN ANALISI)
        queryStr.append("ORDER BY u.tiEle");

        Query q = entityManager.createQuery(queryStr.toString());
        q.setParameter("idRootstrut", BigDecimal.valueOf(struttura.getIdStrut()));

        List<Object[]> udSerFascObjectList = q.getResultList();
        List<UdSerFascObj> udSerFascObjSet = new ArrayList<>();

        for (Object[] udSerFascObject : udSerFascObjectList) {
            Constants.TipoEntitaSacer tipoEntitaSacer = (udSerFascObject[3].equals("01_UNI_DOC"))
                    ? Constants.TipoEntitaSacer.UNI_DOC : (udSerFascObject[3].equals("02_SERIE"))
                            ? Constants.TipoEntitaSacer.SER : Constants.TipoEntitaSacer.FASC;
            BigDecimal id = (udSerFascObject[3].equals("01_UNI_DOC")) ? (BigDecimal) udSerFascObject[0]
                    : (udSerFascObject[3].equals("02_SERIE")) ? (BigDecimal) udSerFascObject[1]
                            : (BigDecimal) udSerFascObject[2];
            udSerFascObjSet.add(new UdSerFascObj(id, tipoEntitaSacer));
        }

        return udSerFascObjSet;
    }

    /**
     * Seleziona le unità documentarie, le serie e i fascicoli che soddisfano la richiesta di estrazione passata come
     * parametro ritornandole sotto forma di insieme UdSerFascObj
     *
     * @param richiesta
     *            entity AroRichiestaRa
     * @param maxUd2procRa
     *            numero massimo unita doc per processo
     * 
     * @return lista elementi di tipo UdSerFascObj
     */
    public List<UdSerFascObj> retrieveAipUdSerFascByRichiesta(AroRichiestaRa richiesta, int maxUd2procRa) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.idAipRestArchivio, u.tiTipologiaOggetto, u.aroIndiceAipUd.aroUnitaDoc.aaKeyUnitaDoc "
                        + "FROM AroAipRestituzioneArchivio u " + "WHERE u.aroRichiestaRa.idRichiestaRa = :idRichiesta "
                        + "AND u.tiStato = :tiStato ");

        // TIP: fdilorenzo, DEFINISCE L'ORDINAMENTO CON CUI DEVONO ESSERE ELABORATI GLI OGGETTI (A SUPPORTO DELLA LOGICA
        // DEFINITA IN ANALISI)
        queryStr.append("ORDER BY u.tiTipologiaOggetto, u.aroIndiceAipUd.aroUnitaDoc.aaKeyUnitaDoc");

        Query q = entityManager.createQuery(queryStr.toString());
        q.setParameter("idRichiesta", richiesta.getIdRichiestaRa());
        q.setParameter("tiStato", TiStatoAroAipRa.DA_ELABORARE);
        q.setFirstResult(0);
        q.setMaxResults(maxUd2procRa);
        List<Object[]> udSerFascObjectList = q.getResultList();
        List<UdSerFascObj> udSerFascObjSet = new ArrayList<>();

        for (Object[] udSerFascObject : udSerFascObjectList) {
            Constants.TipoEntitaSacer tipoEntitaSacer = (udSerFascObject[1].equals(AroAipRaTipologiaOggetto.UD))
                    ? Constants.TipoEntitaSacer.UNI_DOC : (udSerFascObject[1].equals(AroAipRaTipologiaOggetto.SERIE))
                            ? Constants.TipoEntitaSacer.SER : Constants.TipoEntitaSacer.FASC;
            Long id = (Long) udSerFascObject[0];
            udSerFascObjSet.add(new UdSerFascObj(BigDecimal.valueOf(id), tipoEntitaSacer));
        }

        return udSerFascObjSet;
    }

    /**
     * Ricava l'indice AIP che mi appresto ad estrarre
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return id indice aip
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    public AroIndiceAipUd retrieveIndiceAIPByIdUd(Long idUnitaDoc) throws ParerInternalError {
        String queryStr;
        Query query;
        AroIndiceAipUd aroIndiceAipUd;
        List<AroVerIndiceAipUd> aroVerIndiceAipUdList = null;
        try {

            queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
                    + "AND u.pgVerIndiceAip = (select MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d "
                    + "WHERE d.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDocD)";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setParameter("idUnitaDocD", idUnitaDoc);
            aroVerIndiceAipUdList = query.getResultList();
            if (aroVerIndiceAipUdList != null && !aroVerIndiceAipUdList.isEmpty()) {
                // Recupero il campo aroIndiceAipUd
                aroIndiceAipUd = aroVerIndiceAipUdList.get(0).getAroIndiceAipUd();
            } else {
                // Se non ho risultati significa che sono in una situazione incoerente
                aroIndiceAipUd = null;
            }
        } catch (NumberFormatException e) {
            throw new ParerInternalError(e);
        }
        return aroIndiceAipUd;
    }

    // EVO#20972
    /**
     * Ricava l'ultima versione dell'indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return entity versione indice aip
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    public AroVerIndiceAipUd retrieveLastVerIndiceAIPByIdUd(Long idUnitaDoc) throws ParerInternalError {
        String queryStr;
        Query query;
        AroVerIndiceAipUd aroVerIndiceAipUd;
        try {

            queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
                    + "AND u.pgVerIndiceAip = (select MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d "
                    + "WHERE d.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDocD)";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setParameter("idUnitaDocD", idUnitaDoc);
            aroVerIndiceAipUd = (AroVerIndiceAipUd) query.getSingleResult();
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero dell'ultima versione dell'indice AIP dell'ud {}",
                    e.getLocalizedMessage());
            throw new ParerInternalError(e);
        }
        return aroVerIndiceAipUd;
    }

    public List<AroRichiestaRa> retrieveRichiesteRaDaElab() {
        String queryStr = "SELECT rich FROM AroRichiestaRa rich " + "WHERE rich.tiStato IN (:tiStato) "
                + "ORDER BY rich.priorita DESC, rich.tsFine ASC";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("tiStato",
                Arrays.asList(AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE, AroRichiestaTiStato.ESTRAZIONE_IN_CORSO));

        return query.getResultList();
    }

    public List<AroRichiestaRa> retrieveRichiesteRaRestituite() {
        String queryStr = "SELECT rich FROM AroRichiestaRa rich WHERE rich.tiStato = :tiStato "
                + "AND rich.flSvuotaFtp = '1' ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("tiStato", AroRichiestaTiStato.RESTITUITO);
        return query.getResultList();
    }

    public List<AroRichiestaRa> retrieveRichiesteRaAnnullate() {
        String queryStr = "SELECT rich FROM AroRichiestaRa rich WHERE rich.tiStato = :tiStato ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("tiStato", AroRichiestaTiStato.ANNULLATO);
        return query.getResultList();
    }

    public boolean checkEstrazioneInCorso(BigDecimal idRichiestaRa) {
        String queryStr = "SELECT richiestaRa FROM AroRichiestaRa richiestaRa "
                + "WHERE richiestaRa.idRichiestaRa = :idRichiestaRa "
                + "AND EXISTS (SELECT aipRestArchivio FROM AroAipRestituzioneArchivio aipRestArchivio "
                + "WHERE aipRestArchivio.aroRichiestaRa.idRichiestaRa = richiestaRa.idRichiestaRa "
                + "AND aipRestArchivio.tiStato = :tiStato)";

        Query query = entityManager.createQuery(queryStr);

        query.setParameter("idRichiestaRa", longFromBigDecimal(idRichiestaRa));
        query.setParameter("tiStato", TiStatoAroAipRa.DA_ELABORARE);

        List<AroRichiestaRa> list = query.getResultList();
        return !list.isEmpty();
    }
}
