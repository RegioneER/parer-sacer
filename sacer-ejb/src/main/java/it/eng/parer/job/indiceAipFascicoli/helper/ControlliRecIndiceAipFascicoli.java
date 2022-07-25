package it.eng.parer.job.indiceAipFascicoli.helper;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.FasContenVerAipFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasSipVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.util.Calendar;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 * 
 */
@Stateless(mappedName = "ControlliRecIndiceAipFascicoli")
@LocalBean
public class ControlliRecIndiceAipFascicoli {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecIndiceAipFascicoli.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public RispostaControlli leggiXmlVersFascicoliAip(long idXmlVersFascicolo, long idFascicolo) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        log.debug("Lettura xml di versamento per AIP fascicolo - INIZIO");
        FasXmlVersFascicolo fasXmlVersFasc = entityManager.find(FasXmlVersFascicolo.class, idXmlVersFascicolo);

        if (fasXmlVersFasc != null) {
            rispostaControlli.setrObject(fasXmlVersFasc);
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr("666");
            rispostaControlli
                    .setDsErr("Errore interno: non sono stati eseguiti versamenti per il fascicolo id=" + idFascicolo);
        }

        log.debug("Lettura xml di versamento per AIP fascicolo - FINE");
        return rispostaControlli;
    }

    public RispostaControlli leggiXmlVersamentiAipDaFascicolo(long idFascicolo, String tiXmlVers) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        log.debug("Lettura xml di versamento per AIP fascicolo - INIZIO");
        /*
         * recupero xml del tipo passato relativo al versamento del fascicolo.
         */
        log.debug("Ricavo xml di versamento per il fascicolo id= {}", idFascicolo);
        String queryStr = "select t from FasXmlVersFascicolo t " + "where t.fasFascicolo.idFascicolo = :idFascicolo "
                + "and t.tiXmlVers = :tiXmlVers ";

        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiXmlVers", tiXmlVers);

        List<FasXmlVersFascicolo> xmlList = query.getResultList();
        if (xmlList.size() > 0) {
            rispostaControlli.setrObject(xmlList);
            rispostaControlli.setrBoolean(true);
        } else {
            rispostaControlli.setCodErr("666");
            rispostaControlli
                    .setDsErr("Errore interno: non sono stati eseguiti versamenti per il fascicolo id=" + idFascicolo);
        }

        log.debug("Lettura xml di versamento per AIP fascicolo - FINE");
        return rispostaControlli;
    }

    public RispostaControlli getVersioneSacer() {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        String appVersion = it.eng.spagoCore.configuration.ConfigSingleton.get_appVersion();
        rispostaControlli.setrString(appVersion);
        rispostaControlli.setrBoolean(true);
        return rispostaControlli;
    }

    public RispostaControlli getVersioniPrecedentiAIP(Long idFascicolo) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // Ricavo tutte le versioni precedenti dell'AIP
            String queryStr = "SELECT verAipFascicoloPrec "
                    + "FROM FasMetaVerAipFascicolo metaVerAipFascicoloPrec JOIN metaVerAipFascicoloPrec.fasVerAipFascicolo verAipFascicoloPrec "
                    + "WHERE verAipFascicoloPrec.fasFascicolo.idFascicolo = :idFascicolo "
                    + "AND metaVerAipFascicoloPrec.tiMeta = 'INDICE' "
                    + "ORDER BY verAipFascicoloPrec.pgVerAipFascicolo DESC ";

            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idFascicolo", idFascicolo);
            List<FasVerAipFascicolo> versioniPrecedenti = query.getResultList();
            rispostaControlli.setrObject(versioniPrecedenti);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione durante il recupero delle versioni precedenti dell'AIP " + e.getMessage()));
            log.error("Eccezione durante il recupero delle versioni precedenti dell'AIP", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getVersioneCorrenteMetaFascicolo(Long idVerAipFascicolo) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // Ricavo i metadati per la versione corrente in elaborazione
            String queryStr = "SELECT u FROM FasMetaVerAipFascicolo u "
                    + "WHERE u.fasVerAipFascicolo.idVerAipFascicolo = :idVerAipFascicolo "
                    + "AND u.tiMeta = 'FASCICOLO' ";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVerAipFascicolo", idVerAipFascicolo);
            List<FasMetaVerAipFascicolo> versioniCorrenti = query.getResultList();
            rispostaControlli.setrObject(versioniCorrenti);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione durante il recupero della versione corrente dei metadati " + e.getMessage()));
            log.error("Eccezione durante il recupero della versione corrente dei metadati", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getFasContenVerAipFascicolo(long idVerAipFascicolo) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT datoAipFasc " + "FROM FasContenVerAipFascicolo datoAipFasc "
                    + "WHERE datoAipFasc.fasVerAipFascicolo.idVerAipFascicolo = :idVerAipFascicolo ";

            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVerAipFascicolo", idVerAipFascicolo);
            List<FasContenVerAipFascicolo> lstDati = query.getResultList();
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAipFascicoli.getFasContenVerAipFascicolo " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il File durante i controlli per la creazione dell'indice AIP versione fascicoli",
                    e);
        }
        return rispostaControlli;
    }

    public RispostaControlli getFasSipVerAipFascicolo(long idVerAipFascicolo) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT sip " + "FROM FasSipVerAipFascicolo sip "
                    + "WHERE sip.fasVerAipFascicolo.idVerAipFascicolo = :idVerAipFascicolo "
                    + "AND sip.tiSip = 'VERSAMENTO_FASCICOLO' ";

            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVerAipFascicolo", idVerAipFascicolo);
            List<FasSipVerAipFascicolo> lstDati = query.getResultList();
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAipFascicoli.getFasSipVerAipFascicolo " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei sip riguardanti il File durante i controlli per la creazione dell'indice AIP versione fascicoli",
                    e);
        }
        return rispostaControlli;
    }

    // public RispostaControlli getDecModelloFileGroupFileMoreInfo(long idAmbiente) {
    // RispostaControlli rispostaControlli;
    // rispostaControlli = new RispostaControlli();
    // rispostaControlli.setrBoolean(false);
    // try {
    // String queryStr = "SELECT m "
    // + "FROM DecModelloXsdFascicolo m "
    // + "WHERE m.orgAmbiente.idAmbiente = :idAmbiente "
    // + "AND m.tiModelloXsd = 'FILE_GROUP_FILE_MORE_INFO' "
    // + "AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ";
    //
    // Query query = entityManager.createQuery(queryStr);
    // query.setParameter("idAmbiente", idAmbiente);
    // query.setParameter("filterDate", Calendar.getInstance().getTime());
    // List<DecModelloXsdFascicolo> lstDati = query.getResultList();
    // rispostaControlli.setrObject(lstDati);
    // rispostaControlli.setrBoolean(true);
    // } catch (Exception e) {
    // rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
    // rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, "Eccezione
    // ControlliRecIndiceAipFascicoli.getDecModelloFileGroupFileMoreInfo " + e.getMessage()));
    // log.error("Eccezione nella lettura dei dati riguardanti il modello XSD di tipo FILE_GROUP_FILE_MORE_INFO durante
    // i controlli per la creazione dell'indice AIP versione fascicoli " + e);
    // }
    // return rispostaControlli;
    // }

    public RispostaControlli getDecModelloSelfDescMoreInfo(long idAmbiente) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT m " + "FROM DecModelloXsdFascicolo m "
                    + "WHERE m.orgAmbiente.idAmbiente = :idAmbiente " + "AND m.tiModelloXsd = :tiModelloXsd "
                    + "AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ";

            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idAmbiente", idAmbiente);
            query.setParameter("tiModelloXsd", CostantiDB.TiModelloXsd.AIP_SELF_DESCRIPTION_MORE_INFO.name());
            query.setParameter("filterDate", Calendar.getInstance().getTime());
            List<DecModelloXsdFascicolo> lstDati = query.getResultList();
            rispostaControlli.setrObject(lstDati);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecIndiceAipFascicoli.getDecModelloSelfDescMoreInfo " + e.getMessage()));
            log.error(
                    "Eccezione nella lettura dei dati riguardanti il modello XSD di tipo AIP_SELF_DESCRIPTION_MORE_INFO durante i controlli per la creazione dell'indice AIP versione fascicoli",
                    e);
        }
        return rispostaControlli;
    }
}
