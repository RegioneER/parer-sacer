/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.job.validazioneFascicoli.helper;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.ElvVChkAllAipFascInCoda;
import it.eng.parer.viewEntity.ElvVChkSoloFascAnnul;
import it.eng.parer.viewEntity.FasVLisUdByFasc;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "ValidazioneFascicoliHelper")
@LocalBean
public class ValidazioneFascicoliHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(ValidazioneFascicoliHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public List<ElvElencoVersFascDaElab> getElvElencoVersFascDaElab(long idStrut, TiStatoElencoFascDaElab tiStato) {
        String queryStr = "SELECT elencoVersFascDaElab FROM ElvElencoVersFascDaElab elencoVersFascDaElab "
                + "JOIN elencoVersFascDaElab.elvElencoVersFasc elencoVersFasc, ElvStatoElencoVersFasc statoElencoVersFasc "
                + "WHERE elencoVersFascDaElab.tiStato = :tiStato " + "AND elencoVersFascDaElab.idStrut = :idStrut "
                + "AND elencoVersFasc.idStatoElencoVersFascCor = statoElencoVersFasc.idStatoElencoVersFasc "
                + "ORDER BY statoElencoVersFasc.tsStato ASC ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("tiStato", tiStato);
        return query.getResultList();
    }

    public List<FasFascicolo> getFascicoliInElencoNonAnnullati(long idElencoVersFasc) {
        String queryStr = "SELECT fascicolo FROM FasFascicolo fascicolo "
                + "JOIN fascicolo.elvElencoVersFasc elencoVersFasc "
                + "WHERE elencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND fascicolo.tiStatoFascElencoVers = :tiStatoFascElencoVers "
                + "AND fascicolo.tiStatoConservazione != :tiStatoConservazione "
                + "ORDER BY fascicolo.aaFascicolo, fascicolo.cdKeyFascicolo ASC ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiStatoConservazione", TiStatoConservazione.ANNULLATO);
        query.setParameter("tiStatoFascElencoVers", TiStatoFascElencoVers.IN_ELENCO_FIRMATO);

        return query.getResultList();
    }

    public boolean existsUdFascicoloByStatoCons(long idFascicolo, String tiStatoConservazione) {
        String queryStr = "SELECT fascicolo FROM FasFascicolo fascicolo "
                + "WHERE fascicolo.idFascicolo = :idFascicolo "
                + "AND EXISTS (SELECT unitaDoc FROM FasUnitaDocFascicolo unitaDocFascicolo "
                + "JOIN unitaDocFascicolo.aroUnitaDoc unitaDoc WHERE unitaDoc.tiStatoConservazione = :tiStatoConservazione "
                + "AND unitaDocFascicolo.fasFascicolo = fascicolo) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiStatoConservazione", tiStatoConservazione);
        return !query.getResultList().isEmpty();
    }

    public List<AroUnitaDoc> getUdFascicoloByStatoCons(long idFascicolo, String tiStatoConservazione) {
        String queryStr = "SELECT unitaDoc FROM FasUnitaDocFascicolo unitaDocFascicolo "
                + "JOIN unitaDocFascicolo.aroUnitaDoc unitaDoc " + "JOIN unitaDocFascicolo.fasFascicolo fascicolo "
                + "WHERE fascicolo.idFascicolo = :idFascicolo "
                + "AND unitaDoc.tiStatoConservazione = :tiStatoConservazione ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiStatoConservazione", tiStatoConservazione);
        return query.getResultList();
    }

    public Long getLastPgFascicoloCoda(long idFascicolo) {
        String queryStr = "SELECT COUNT(aipFascicoloDaElab) FROM FasAipFascicoloDaElab aipFascicoloDaElab "
                + "WHERE aipFascicoloDaElab.fasFascicolo.idFascicolo = :idFascicolo ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        return (Long) query.getSingleResult();
    }

    public boolean allUdFascicoloStatiConservazione(long idFascicolo, List<String> statiConservazione) {
        String queryStr = "SELECT COUNT(unitaDoc) FROM FasUnitaDocFascicolo unitaDocFascicolo "
                + "JOIN unitaDocFascicolo.aroUnitaDoc unitaDoc " + "JOIN unitaDocFascicolo.fasFascicolo fascicolo "
                + "WHERE fascicolo.idFascicolo = :idFascicolo "
                + "AND unitaDoc.tiStatoConservazione IN :statiConservazione ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("statiConservazione", statiConservazione);
        Long totFascStati = (Long) query.getSingleResult();

        String queryStr2 = "SELECT COUNT(unitaDoc) FROM FasUnitaDocFascicolo unitaDocFascicolo "
                + "JOIN unitaDocFascicolo.aroUnitaDoc unitaDoc " + "JOIN unitaDocFascicolo.fasFascicolo fascicolo "
                + "WHERE fascicolo.idFascicolo = :idFascicolo ";
        Query query2 = getEntityManager().createQuery(queryStr2);
        query2.setParameter("idFascicolo", idFascicolo);
        Long totFasc = (Long) query2.getSingleResult();

        return totFascStati == totFasc;
    }

    public void updateStatoConservazioneUdFascicolo(long idFascicolo, List<String> statiOld, String statoNew) {
        Query q = getEntityManager()
                .createQuery("UPDATE AroUnitaDoc unitaDoc SET unitaDoc.tiStatoConservazione = :statoNew "
                        + "WHERE unitaDoc.tiStatoConservazione IN :statiOld "
                        + "AND unitaDoc.idUnitaDoc IN (SELECT unitaDoc1.idUnitaDoc FROM FasUnitaDocFascicolo unitaDocFascicolo "
                        + "JOIN unitaDocFascicolo.aroUnitaDoc unitaDoc1 "
                        + "JOIN unitaDocFascicolo.fasFascicolo fascicolo "
                        + "WHERE fascicolo.idFascicolo = :idFascicolo) ");
        q.setParameter("idFascicolo", idFascicolo);
        q.setParameter("statiOld", statiOld);
        q.setParameter("statoNew", statoNew);
        q.executeUpdate();
        getEntityManager().flush();
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return entity AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = (List<AroVerIndiceAipUd>) q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public boolean allFascicoliAnnullati(long idElencoVersFasc) {
        ElvVChkSoloFascAnnul chkSoloFascAnnul = getEntityManager().find(ElvVChkSoloFascAnnul.class,
                BigDecimal.valueOf(idElencoVersFasc));
        return chkSoloFascAnnul.getFlSoloFascAnnul().equals("1");
    }

    public boolean allAipFascInCoda(long idElencoVersFasc) {
        ElvVChkAllAipFascInCoda chkAllAipFascInCoda = getEntityManager().find(ElvVChkAllAipFascInCoda.class,
                BigDecimal.valueOf(idElencoVersFasc));
        return chkAllAipFascInCoda.getFlAllAipInCoda().equals("1");
    }

    public VolVolumeConserv getVolumeUnitaDocPerDataMarcatura(long idUnitaDoc) {
        Query q = getEntityManager()
                .createQuery("SELECT volumeConserv FROM VolAppartUnitaDocVolume appartUnitaDocVolume "
                        + "JOIN appartUnitaDocVolume.volVolumeConserv volumeConserv "
                        + "JOIN appartUnitaDocVolume.aroUnitaDoc unitaDoc " + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "ORDER BY volumeConserv.tmMarcaIndice DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<VolVolumeConserv> lista = (List<VolVolumeConserv>) q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<BigDecimal> getUdFascicoloByFascIdList(long idFascicolo) {
        String queryStr = "SELECT lisUdByFasc.idUnitaDoc FROM FasVLisUdByFasc lisUdByFasc "
                + "WHERE lisUdByFasc.idFascicolo = :idFascicolo";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        return query.getResultList();
    }

    public FasVLisUdByFasc getFasVLisUdByFasc(long idFascicolo, long idUnitaDoc) {
        String queryStr = "SELECT lisUdByFasc FROM FasVLisUdByFasc lisUdByFasc "
                + "WHERE lisUdByFasc.idFascicolo = :idFascicolo " + "AND lisUdByFasc.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        return (FasVLisUdByFasc) query.getSingleResult();
    }
}
