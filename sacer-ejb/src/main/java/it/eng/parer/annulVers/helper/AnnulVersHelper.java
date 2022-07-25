package it.eng.parer.annulVers.helper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.annulVers.dto.RicercaRichAnnulVersBean;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroErrRichAnnulVers;
import it.eng.parer.entity.AroItemRichAnnulVers;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStatoRichAnnulVers;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVLisItemRichAnnvrs;
import it.eng.parer.viewEntity.AroVLisStatoRichAnnvrs;
import it.eng.parer.viewEntity.AroVRicRichAnnvrs;
import it.eng.parer.viewEntity.ElvVLisElencoFascAnnul;
import it.eng.parer.viewEntity.ElvVLisElencoUdAnnul;
import it.eng.parer.viewEntity.ElvVLisFascAnnulByElenco;
import it.eng.parer.viewEntity.ElvVLisUdAnnulByElenco;
import it.eng.parer.viewEntity.VolVLisUdAnnulByVolume;
import it.eng.parer.viewEntity.VolVLisVolumeUdAnnul;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class AnnulVersHelper extends GenericHelper {

    /**
     * Verifica l'esistenza di una richiesta di annullamento con codice <code>cdRichAnnulVers</code> per la struttura
     * <code>idStrut</code>
     *
     * @param cdRichAnnulVers
     *            code richiesta annullamento versamento
     * @param idStrut
     *            id struttura
     * 
     * @return true se esiste
     */
    public boolean isRichAnnulVersExisting(String cdRichAnnulVers, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(r) FROM AroRichAnnulVers r WHERE r.cdRichAnnulVers = :cdRichAnnulVers AND r.orgStrut.idStrut = :idStrut");
        query.setParameter("cdRichAnnulVers", cdRichAnnulVers);
        query.setParameter("idStrut", idStrut);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Ritorna la richiesta data l'unità documentaria come parametro se lo stato richiesta è APERTA o CHIUSA
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idRichAnnulVers
     *            id della richiesta da escludere nel controllo in quanto contiene già l'ud
     * 
     * @return true se è presente
     */
    public AroRichAnnulVers getAroRichAnnulVersContainingUd(Long idUnitaDoc, Long idRichAnnulVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT rich FROM AroItemRichAnnulVers item JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati WHERE ");
        if (idRichAnnulVers != null) {
            queryStr.append("rich.idRichAnnulVers != :idRichAnnulVers").append(" AND ");
        }
        queryStr.append(
                "item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) AND stati.tiStatoRichAnnulVers IN ('APERTA','CHIUSA') ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUnitaDoc", idUnitaDoc);
        if (idRichAnnulVers != null) {
            query.setParameter("idRichAnnulVers", idRichAnnulVers);
        }
        List<AroRichAnnulVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ritorna la richiesta dato il fascicolo come parametro se lo stato richiesta è APERTA o CHIUSA
     *
     * @param idFascicolo
     *            id fascicolo
     * @param idRichAnnulVers
     *            id della richiesta da escludere nel controllo in quanto contiene già il fascicolo
     * 
     * @return true se è presente
     */
    public AroRichAnnulVers getAroRichAnnulVersContainingFasc(Long idFascicolo, Long idRichAnnulVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT rich FROM AroItemRichAnnulVers item JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati WHERE ");
        if (idRichAnnulVers != null) {
            queryStr.append("rich.idRichAnnulVers != :idRichAnnulVers").append(" AND ");
        }
        queryStr.append(
                "item.fasFascicolo.idFascicolo = :idFascicolo AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) AND stati.tiStatoRichAnnulVers IN ('APERTA','CHIUSA') ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idFascicolo", idFascicolo);
        if (idRichAnnulVers != null) {
            query.setParameter("idRichAnnulVers", idRichAnnulVers);
        }
        List<AroRichAnnulVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Conta i documenti versati (in versamento o aggiunta) di una ud in una specifica data
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param tiCreazione
     *            AGGIUNTA_DOCUMENTO o VERSAMENTO_UNITA_DOC
     * @param dtVers
     *            data di versamento
     * 
     * @return numero di documenti versati
     */
    public Long countDocAggListOnDtVers(Long idUnitaDoc, String tiCreazione, Date dtVers) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(doc) FROM AroDoc doc WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND doc.tiCreazione = :tiCreazione AND doc.dtCreazione between :dtVersFrom AND :dtVersTo");

        Calendar from = Calendar.getInstance();
        from.setTime(dtVers);
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);
        Calendar to = Calendar.getInstance();
        to.setTime(dtVers);
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);
        to.set(Calendar.MILLISECOND, 999);

        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("tiCreazione", tiCreazione); // CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name()
        query.setParameter("dtVersFrom", from.getTime());
        query.setParameter("dtVersTo", to.getTime());
        Long docs = (Long) query.getSingleResult();
        return docs;
    }

    /**
     * Verifica se l'unità documentaria e' stata versata mediante PreIngest (in questo caso per la sessione di
     * versamento dell'unità documentaria e' presente lo xml di tipo INDICE_FILE)
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return true se è versata da PreIngest
     */
    public boolean isUdFromPreIngest(Long idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(v) FROM VrsXmlDatiSessioneVers xmlDatiV JOIN xmlDatiV.vrsDatiSessioneVers datiV JOIN datiV.vrsSessioneVers v WHERE v.tiStatoSessioneVers = 'CHIUSA_OK' AND v.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND xmlDatiV.tiXmlDati = 'INDICE_FILE'");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Determino le richieste con stato CHIUSA per le quali non sia definito un item con stato DA_ANNULLARE_IN_PING
     * OPPURE le richieste con stato COMUNICATA A SACER, purché non immediate
     *
     * @return la lista con le richieste
     */
    public List<AroRichAnnulVers> getRichiesteAnnullamentoVersamentoDaElab() {
        String queryStr = "SELECT richAnnulVers FROM AroStatoRichAnnulVers statoRichAnnulVer "
                + "JOIN statoRichAnnulVer.aroRichAnnulVers richAnnulVers " + "WHERE richAnnulVers.flImmediata = '0' "
                + "AND statoRichAnnulVer.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = richAnnulVers.idRichAnnulVers) "
                + "AND (statoRichAnnulVer.tiStatoRichAnnulVers = 'CHIUSA' "
                + "AND NOT EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers "
                + "WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = richAnnulVers.idRichAnnulVers AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_PING')) "
                + "OR (statoRichAnnulVer.tiStatoRichAnnulVers = 'COMUNICATA_A_SACER') ";

        Query query = getEntityManager().createQuery(queryStr);
        List<AroRichAnnulVers> richiesteAnnulList = (List<AroRichAnnulVers>) query.getResultList();
        return richiesteAnnulList;
    }

    /**
     * Ricavo il progressivo più alto tra tutti gli stati di una determinata richiesta
     *
     * @param idRichAnnulVers
     *            l'id della richiesta di cui voglio conoscere il progressivo stato maggiore
     * 
     * @return il progressivo
     */
    public BigDecimal getUltimoProgressivoStatoRichiesta(long idRichAnnulVers) {
        Query q = getEntityManager().createQuery(
                "SELECT MAX(statoRichAnnulVers.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers statoRichAnnulVers "
                        + "WHERE statoRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        return (BigDecimal) q.getSingleResult() != null ? (BigDecimal) q.getSingleResult() : BigDecimal.ZERO;
    }

    /**
     * Ricavo il progressivo più alto tra tutti gli item di una determinata richiesta
     *
     * @param idRichAnnulVers
     *            l'id della richiesta di cui voglio conoscere il progressivo stato maggiore
     * 
     * @return il progressivo
     */
    public BigDecimal getUltimoProgressivoItemRichiesta(long idRichAnnulVers) {
        Query q = getEntityManager().createQuery(
                "SELECT MAX(itemRichAnnulVers.pgItemRichAnnulVers) FROM AroItemRichAnnulVers itemRichAnnulVers "
                        + "WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        return (BigDecimal) q.getSingleResult() != null ? (BigDecimal) q.getSingleResult() : BigDecimal.ZERO;
    }

    /**
     * Ricavo l'idUserIam dell'utente che ha definito lo stato corrente della richiesta
     *
     * @param idStatoRichAnnulVersCorr
     *            id stato richieata annullamento corrente
     * 
     * @return idUserIam id user IAM
     */
    public long getIdUserIamStatoCorrenteRichiesta(long idStatoRichAnnulVersCorr) {
        AroStatoRichAnnulVers statoRichAnnulVers = getEntityManager().find(AroStatoRichAnnulVers.class,
                idStatoRichAnnulVersCorr);
        return statoRichAnnulVers.getIamUser().getIdUserIam();
    }

    /**
     * Ritorna la lista dei volumi di conservazione a cui appartengono gli item (della richiesta corrente) di tipo
     * UNI_DOC con stato DA_ANNULLARE_IN_SACER
     *
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * 
     * @return lista oggetti di tipo {@link VolVLisVolumeUdAnnul}
     */
    public List<VolVLisVolumeUdAnnul> retrieveVolVLisVolumeUdAnnul(long idRichAnnulVers) {
        Query query = getEntityManager()
                .createQuery("SELECT vol FROM VolVLisVolumeUdAnnul vol WHERE vol.idRichAnnulVers = :idRichAnnulVers ");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<VolVLisVolumeUdAnnul> volumiConservsList = query.getResultList();
        return volumiConservsList;
    }

    public List<VolVLisUdAnnulByVolume> retrieveVolVLisUdAnnulByVolume(long idRichAnnulVers, long idVolumeConserv) {
        Query query = getEntityManager().createQuery(
                "SELECT vol FROM VolVLisUdAnnulByVolume vol WHERE vol.idRichAnnulVers = :idRichAnnulVers AND vol.idVolumeConserv = :idVolumeConserv");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("idVolumeConserv", idVolumeConserv);
        List<VolVLisUdAnnulByVolume> udAnnuls = query.getResultList();
        return udAnnuls;
    }

    // /**
    // * Ricavo gli item della richiesta corrente con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di
    // * inclusione in un elenco vale gli stati passati come parametro
    // *
    // * @param idRichAnnulVers, l'id della richiesta corrente
    // * @param tiStatoElencoVers, gli stati ud/fasc
    // * @param tiItemRichAnnulVers
    // * @return
    // */
    // public List<AroItemRichAnnulVers> getItem(long idRichAnnulVers, CostantiDB.TiItemRichAnnulVers
    // tiItemRichAnnulVers, String... tiStatoElencoVers) {
    // List<AroItemRichAnnulVers> itemList;
    // StringBuilder queryStr = new StringBuilder("SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers
    // ");
    //
    // if (tiItemRichAnnulVers.equals(CostantiDB.TiItemRichAnnulVers.UNI_DOC)) {
    // queryStr.append("JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc ");
    // } else if (tiItemRichAnnulVers.equals(CostantiDB.TiItemRichAnnulVers.FASC)) {
    // queryStr.append("JOIN itemRichAnnulVers.fasFascicolo fascicolo ");
    // }
    //
    // queryStr.append("JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
    // + "WHERE richAnnulVers.idRichAnnulVers = :idRichAnnulVers "
    // + "AND itemRichAnnulVers.tiItemRichAnnulVers = :tiItemRichAnnulVers "
    // + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ");
    //
    // if (tiStatoElencoVers != null && tiStatoElencoVers.length > 0) {
    // if (tiItemRichAnnulVers.equals(CostantiDB.TiItemRichAnnulVers.UNI_DOC)) {
    // if (tiStatoElencoVers.length > 1) {
    // queryStr.append("AND unitaDoc.tiStatoUdElencoVers IN :tiStatoElencoVers ");
    // } else {
    // queryStr.append("AND unitaDoc.tiStatoUdElencoVers = :tiStatoElencoVers ");
    // }
    // } else if (tiItemRichAnnulVers.equals(CostantiDB.TiItemRichAnnulVers.FASC)) {
    // if (tiStatoElencoVers.length > 1) {
    // queryStr.append("AND fascicolo.tiStatoFascElencoVers IN :tiStatoElencoVers ");
    // } else {
    // queryStr.append("AND fascicolo.tiStatoFascElencoVers = :tiStatoElencoVers ");
    // }
    // }
    // }
    //
    // Query query = getEntityManager().createQuery(queryStr.toString());
    // query.setParameter("idRichAnnulVers", idRichAnnulVers);
    // query.setParameter("tiItemRichAnnulVers", tiItemRichAnnulVers.name());
    // if (tiStatoElencoVers != null && tiStatoElencoVers.length > 0) {
    //
    // List<String> statiList = new ArrayList<>();
    // List<FasFascicolo.TiStatoFascElencoVers> statiListEnum = new ArrayList<>();
    //
    // if (tiItemRichAnnulVers.equals(CostantiDB.TiItemRichAnnulVers.UNI_DOC)) {
    // statiList = Arrays.asList(tiStatoElencoVers);
    // } else {
    // for (String tistato : tiStatoElencoVers) {
    // statiListEnum.add(FasFascicolo.TiStatoFascElencoVers.valueOf(tistato));
    // }
    // }
    //
    // //List<Enum> statiList = Arrays.asList(FasFascicolo.TiStatoFascElencoVers.valueOf(tiStatoElencoVers));
    // if (statiList.size() > 1) {
    // query.setParameter("tiStatoElencoVers", statiList);
    // } else {
    // query.setParameter("tiStatoElencoVers", statiList.get(0));
    // }
    //
    // if (statiListEnum.size() > 1) {
    // query.setParameter("tiStatoElencoVers", statiListEnum);
    // } else {
    // query.setParameter("tiStatoElencoVers", statiListEnum.get(0));
    // }
    // }
    // itemList = (List<AroItemRichAnnulVers>) query.getResultList();
    // return itemList;
    // }

    /**
     * Ricavo gli item della richiesta corrente di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER il cui stato relativo al
     * processo di inclusione in un elenco vale gli stati passati come parametro
     *
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * @param tiStatoUdElencoVers
     *            gli stati ud
     * 
     * @return litsa oggetti di tipo {@link AroItemRichAnnulVers}
     */
    public List<AroItemRichAnnulVers> getItem(long idRichAnnulVers, String... tiStatoUdElencoVers) {
        List<AroItemRichAnnulVers> itemList;
        StringBuilder queryStr = new StringBuilder(
                "SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers "
                        + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc "
                        + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
                        + "WHERE richAnnulVers.idRichAnnulVers = :idRichAnnulVers "
                        + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'UNI_DOC' "
                        + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ");
        if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.length > 0) {
            if (tiStatoUdElencoVers.length > 1) {
                queryStr.append("AND unitaDoc.tiStatoUdElencoVers IN :tiStatoUdElencoVers ");
            } else {
                queryStr.append("AND unitaDoc.tiStatoUdElencoVers = :tiStatoUdElencoVers ");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.length > 0) {
            List<String> statiList = Arrays.asList(tiStatoUdElencoVers);
            if (statiList.size() > 1) {
                query.setParameter("tiStatoUdElencoVers", statiList);
            } else {
                query.setParameter("tiStatoUdElencoVers", statiList.get(0));
            }
        }
        itemList = (List<AroItemRichAnnulVers>) query.getResultList();
        return itemList;
    }

    /**
     * Il sistema determina gli aggiornamenti unità doc degli item (della richiesta corrente) di tipo UNI_DOC con stato
     * DA_ANNULLARE_IN_SACER, il cui stato relativo al processo di inclusione in un elenco vale IN_ATTESA_SCHED,
     * NON_SELEZ_SCHED
     * 
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * @param tiStatoUdElencoVers
     *            gli stati ud
     * 
     * @return lista elementi di tipo AroUpdUnitaDoc
     */
    public List<AroUpdUnitaDoc> getUpdItem(long idRichAnnulVers, AroUpdUDTiStatoUpdElencoVers... tiStatoUdElencoVers) {
        List<AroUpdUnitaDoc> itemList;
        StringBuilder queryStr = new StringBuilder("SELECT updUnitaDocs FROM AroItemRichAnnulVers itemRichAnnulVers "
                + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.aroUpdUnitaDocs updUnitaDocs "
                + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
                + "WHERE richAnnulVers.idRichAnnulVers = :idRichAnnulVers "
                + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ");
        if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.length > 0) {
            if (tiStatoUdElencoVers.length > 1) {
                queryStr.append("AND updUnitaDocs.tiStatoUpdElencoVers IN :tiStatoUpdElencoVers ");
            } else {
                queryStr.append("AND updUnitaDocs.tiStatoUpdElencoVers = :tiStatoUpdElencoVers ");
            }
        }
        // order by last update
        // queryStr.append("ORDER BY updUnitaDocs.pgUpdUnitaDoc DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (tiStatoUdElencoVers != null && tiStatoUdElencoVers.length > 0) {
            List<AroUpdUDTiStatoUpdElencoVers> statiList = Arrays.asList(tiStatoUdElencoVers);
            if (statiList.size() > 1) {
                query.setParameter("tiStatoUpdElencoVers", statiList);
            } else {
                query.setParameter("tiStatoUpdElencoVers", statiList.get(0));
            }
        }
        itemList = (List<AroUpdUnitaDoc>) query.getResultList();
        return itemList;
    }

    /**
     * Ricavo gli item della richiesta corrente di tipo FASC con stato DA_ANNULLARE_IN_SACER il cui stato relativo al
     * processo di inclusione in un elenco vale gli stati passati come parametro
     *
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * @param tiStatoFascElencoVers
     *            gli stati fasc
     * 
     * @return lista elementi di tipo AroItemRichAnnulVers
     */
    public List<AroItemRichAnnulVers> getItemFasc(long idRichAnnulVers,
            TiStatoFascElencoVers... tiStatoFascElencoVers) {
        List<AroItemRichAnnulVers> itemList;
        StringBuilder queryStr = new StringBuilder(
                "SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers "
                        + "JOIN itemRichAnnulVers.fasFascicolo fascicolo "
                        + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
                        + "WHERE richAnnulVers.idRichAnnulVers = :idRichAnnulVers "
                        + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'FASC' "
                        + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ");
        if (tiStatoFascElencoVers != null && tiStatoFascElencoVers.length > 0) {
            if (tiStatoFascElencoVers.length > 1) {
                queryStr.append("AND fascicolo.tiStatoFascElencoVers IN :tiStatoFascElencoVers ");
            } else {
                queryStr.append("AND fascicolo.tiStatoFascElencoVers = :tiStatoFascElencoVers ");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (tiStatoFascElencoVers != null && tiStatoFascElencoVers.length > 0) {
            List<TiStatoFascElencoVers> statiList = Arrays.asList(tiStatoFascElencoVers);
            if (statiList.size() > 1) {
                query.setParameter("tiStatoFascElencoVers", statiList);
            } else {
                query.setParameter("tiStatoFascElencoVers", statiList.get(0));
            }
        }
        itemList = (List<AroItemRichAnnulVers>) query.getResultList();
        return itemList;
    }

    /**
     * Ricavo i documenti aggiunti degli item di tipo UNI_DOC (quindi sto considerando l'item essere un'unità doc e ne
     * cerco i documenti aggiunti) con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di inclusione in un
     * elenco vale i valori passati come parametro
     *
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * @param tiStatoDocElencoVers
     *            gli stati del doc
     * 
     * @return lista elementi di tipo AroDoc
     */
    public List<AroDoc> getDocAggiunti(long idRichAnnulVers, String... tiStatoDocElencoVers) {
        StringBuilder queryStr = new StringBuilder("SELECT doc FROM AroItemRichAnnulVers itemRichAnnulVers "
                + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc " + "JOIN unitaDoc.aroDocs doc "
                + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
                + "WHERE richAnnulVers.idRichAnnulVers = :idRichAnnulVers "
                + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'UNI_DOC' "
                + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
                + "AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' ");
        if (tiStatoDocElencoVers != null) {
            if (tiStatoDocElencoVers.length > 1) {
                queryStr.append("AND doc.tiStatoDocElencoVers IN :tiStatoDocElencoVers ");
            } else {
                queryStr.append("AND doc.tiStatoDocElencoVers = :tiStatoDocElencoVers ");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (tiStatoDocElencoVers != null) {
            List<String> statiList = Arrays.asList(tiStatoDocElencoVers);
            if (statiList.size() > 1) {
                query.setParameter("tiStatoDocElencoVers", statiList);
            } else {
                query.setParameter("tiStatoDocElencoVers", statiList.get(0));
            }
        }
        List<AroDoc> docList = (List<AroDoc>) query.getResultList();
        return docList;
    }

    // public List<ElvElencoVer> getElenchiItem(long idRichAnnulVers) {
    // String queryStr = "SELECT DISTINCT elencoVers FROM AroItemRichAnnulVers itemRichAnnulVers "
    // + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc "
    // + "JOIN unitaDoc.elvElencoVer elencoVers "
    // + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
    // + "WHERE unitaDoc.tiStatoUdElencoVers IN ('IN_ELENCO_CHIUSO', 'IN_ELENCO_FIRMATO') "
    // + "AND elencoVers.tiStatoElenco IN ('CHIUSO', 'FIRMATO') "
    // + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'UNI_DOC' "
    // + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
    // + "AND richAnnulVers.idRichAnnulVers = :idRichAnnulVers ";
    // Query query = getEntityManager().createQuery(queryStr);
    // query.setParameter("idRichAnnulVers", idRichAnnulVers);
    // List<ElvElencoVer> elenchiList = (List<ElvElencoVer>) query.getResultList();
    // return elenchiList;
    // }
    //
    // public List<ElvElencoVer> getElenchiDocAggiunti(long idRichAnnulVers) {
    // String queryStr = "SELECT DISTINCT elencoVers FROM AroItemRichAnnulVers itemRichAnnulVers "
    // + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc "
    // + "JOIN unitaDoc.aroDocs doc "
    // + "JOIN doc.elvElencoVer elencoVers "
    // + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
    // + "WHERE doc.tiStatoDocElencoVers IN ('IN_ELENCO_CHIUSO', 'IN_ELENCO_FIRMATO') "
    // + "AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
    // + "AND elencoVers.tiStatoElenco IN ('CHIUSO', 'FIRMATO') "
    // + "AND itemRichAnnulVers.tiItemRichAnnulVers = 'UNI_DOC' "
    // + "AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
    // + "AND richAnnulVers.idRichAnnulVers = :idRichAnnulVers ";
    // Query query = getEntityManager().createQuery(queryStr);
    // query.setParameter("idRichAnnulVers", idRichAnnulVers);
    // List<ElvElencoVer> elenchiList = (List<ElvElencoVer>) query.getResultList();
    // return elenchiList;
    // }
    /**
     * Ritorna la lista dei elenchi a cui appartengono gli item (della richiesta corrente) di tipo UNI_DOC con stato
     * DA_ANNULLARE_IN_SACER, il cui stato vale IN_ELENCO_CHIUSO o IN_ELENCO_VALIDATO unito agli elenchi a cui
     * appartengono i documenti aggiunti degli item di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER, il cui stato vale
     * IN_ELENCO_CHIUSO o IN_ELENCO_VALIDATO
     *
     * @param idRichAnnulVers
     *            l'id della richiesta corrente
     * 
     * @return lista elementi di tipo ElvVLisElencoUdAnnul
     */
    public List<ElvVLisElencoUdAnnul> retrieveElvVLisElencoUdAnnuls(long idRichAnnulVers) {
        Query query = getEntityManager()
                .createQuery("SELECT elv FROM ElvVLisElencoUdAnnul elv WHERE elv.idRichAnnulVers = :idRichAnnulVers ");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<ElvVLisElencoUdAnnul> elenchi = query.getResultList();
        return elenchi;
    }

    public List<ElvVLisUdAnnulByElenco> retrieveElvVLisUdAnnulByElenco(long idRichAnnulVers, long idElencoVers) {
        Query query = getEntityManager().createQuery(
                "SELECT elv FROM ElvVLisUdAnnulByElenco elv WHERE elv.idRichAnnulVers = :idRichAnnulVers AND elv.idElencoVers = :idElencoVers");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("idElencoVers", idElencoVers);
        List<ElvVLisUdAnnulByElenco> udAnnuls = query.getResultList();
        return udAnnuls;
    }

    public void updateUnitaDocumentarieItem(long idRichAnnulVers, Date dtAnnul, String tiAnnul,
            String tiStatoConservazione, String ntAnnul) {
        Query q = getEntityManager().createQuery(
                "UPDATE AroUnitaDoc unitaDoc SET unitaDoc.dtAnnul = :dtAnnul, unitaDoc.tiAnnul = :tiAnnul, unitaDoc.tiStatoConservazione = :tiStatoConservazione, unitaDoc.ntAnnul = :ntAnnul "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.aroUnitaDoc = unitaDoc AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ) ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.setParameter("dtAnnul", dtAnnul);
        q.setParameter("tiAnnul", tiAnnul);
        q.setParameter("tiStatoConservazione", tiStatoConservazione);
        q.setParameter("ntAnnul", ntAnnul);
        q.executeUpdate();
    }

    public void updateUpdUnitaDocumentarieItem(long idRichAnnulVers, Date dtAnnul, String ntAnnul) {
        Query q = getEntityManager().createQuery(
                "UPDATE AroUpdUnitaDoc updUnitaDoc SET updUnitaDoc.dtAnnul = :dtAnnul, updUnitaDoc.ntAnnul = :ntAnnul "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.aroUnitaDoc.aroUpdUnitaDocs = updUnitaDoc AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ) ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.setParameter("dtAnnul", dtAnnul);
        q.setParameter("ntAnnul", ntAnnul);
        q.executeUpdate();
    }

    public void updateCollegamentiUd(long idRichAnnulVers) {
        Query q = getEntityManager()
                .createQuery("UPDATE AroLinkUnitaDoc linkUnitaDoc SET linkUnitaDoc.aroUnitaDocLink = null "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.aroUnitaDoc = linkUnitaDoc.aroUnitaDoc AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER') ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.executeUpdate();
    }

    public void updateDocumentiUdItem(Long idRichAnnulVers, Date dtAnnul, String tiAnnul, String ntAnnul) {
        Query q = getEntityManager().createQuery(
                "UPDATE AroDoc doc SET doc.dtAnnul = :dtAnnul, doc.tiAnnul = :tiAnnul, doc.ntAnnul = :ntAnnul "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.aroUnitaDoc = doc.aroUnitaDoc AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER') ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.setParameter("dtAnnul", dtAnnul);
        q.setParameter("tiAnnul", tiAnnul);
        q.setParameter("ntAnnul", ntAnnul);
        q.executeUpdate();
    }

    public void updateStatoItemList(Long idRichAnnulVers, String tiStatoItem) {
        Query q = getEntityManager().createQuery(
                "UPDATE AroItemRichAnnulVers itemRichAnnulVers SET itemRichAnnulVers.tiStatoItem = :tiStatoItem "
                        + "WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER'");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.setParameter("tiStatoItem", tiStatoItem);
        q.executeUpdate();
    }

    public void deleteElvUdVersDaElabElenco(long idUnitaDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM ElvUdVersDaElabElenco udVersDaElabElenco "
                + "WHERE udVersDaElabElenco.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        q.executeUpdate();
    }

    public void deleteElvUpdUdDaElabElenco(long idUpdUnitaDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM ElvUpdUdDaElabElenco updUdDaElabElenco "
                + "WHERE updUdDaElabElenco.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ");
        q.setParameter("idUpdUnitaDoc", idUpdUnitaDoc);
        q.executeUpdate();
    }

    public void deleteElvDocAggDaElabElenco(long idDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM ElvDocAggDaElabElenco docAggDaElabElenco "
                + "WHERE docAggDaElabElenco.aroDoc.idDoc = :idDoc ");
        q.setParameter("idDoc", idDoc);
        q.executeUpdate();
    }

    public void deleteElvFascDaElabElenco(long idFascicolo) {
        Query q = getEntityManager().createQuery("DELETE FROM ElvFascDaElabElenco fascDaElabElenco "
                + "WHERE fascDaElabElenco.fasFascicolo.idFascicolo = :idFascicolo ");
        q.setParameter("idFascicolo", idFascicolo);
        q.executeUpdate();
    }

    // <editor-fold defaultstate="collapsed" desc="Query per funzioni online">
    public List<AroVRicRichAnnvrs> retrieveAroVRicRichAnnvrs(long idUser, RicercaRichAnnulVersBean filtri) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicRichAnnvrs ("
                + "r.cdRichAnnulVers,r.dsRichAnnulVers,r.dtCreazioneRichAnnulVers,r.flAnnulPing,r.flImmediata,r.flNonAnnul,"
                + "r.idAmbiente,r.idEnte,r.idRichAnnulVers,r.idStrut,r.idUserIam,r.niItem,r.niItemNonAnnul,"
                + "r.niItemPing,r.nmAmbiente,r.nmEnte,r.nmStrut,r.ntRichAnnulVers,r.tiCreazioneRichAnnulVers,r.tiStatoRichAnnulVersCor)"
                + " FROM AroVRicRichAnnvrs r WHERE r.idUserIam = :idUserIam AND r.idAmbiente = :idAmbiente ");
        if (filtri.getId_ente() != null) {
            queryStr.append(clause).append("r.idEnte = :idEnte ");
        }
        if (filtri.getId_strut() != null) {
            queryStr.append(clause).append("r.idStrut = :idStrut ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_rich_annul_vers())) {
            queryStr.append(clause).append("UPPER(r.cdRichAnnulVers) like :cdRichAnnulVers ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_rich_annul_vers())) {
            queryStr.append(clause).append("UPPER(r.dsRichAnnulVers) like :dsRichAnnulVers ");
        }
        if (StringUtils.isNotBlank(filtri.getNt_rich_annul_vers())) {
            queryStr.append(clause).append("UPPER(r.ntRichAnnulVers) like :ntRichAnnulVers ");
        }
        if (!filtri.getTi_stato_rich_annul_vers_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_annul_vers_cor().size() == 1) {
                queryStr.append(clause).append("r.tiStatoRichAnnulVersCor = :tiStatoRichAnnulVersCor ");
            } else {
                queryStr.append(clause).append("r.tiStatoRichAnnulVersCor IN :tiStatoRichAnnulVersCor ");
            }
        }
        if (filtri.getDt_creazione_rich_annul_vers_da() != null) {
            queryStr.append(clause).append("r.dtCreazioneRichAnnulVers >= :dtInizio ");
        }
        if (filtri.getDt_creazione_rich_annul_vers_a() != null) {
            queryStr.append(clause).append("r.dtCreazioneRichAnnulVers <= :dtFine ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_immediata())) {
            queryStr.append(clause).append("r.flImmediata = :flImmediata ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_annul_ping())) {
            queryStr.append(clause).append("r.flAnnulPing = :flAnnulPing ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_non_annul())) {
            queryStr.append(clause).append("r.flNonAnnul = :flNonAnnul ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_registro_key_unita_doc())) {
            queryStr.append(clause).append("r.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
        }
        if (filtri.getAa_key_unita_doc() != null) {
            queryStr.append(clause).append("r.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_unita_doc())) {
            queryStr.append(clause).append("r.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
        }
        if (filtri.getAa_fascicolo() != null) {
            queryStr.append(clause).append("r.aaFascicolo = :aaFascicolo ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            queryStr.append(clause).append("r.cdKeyFascicolo = :cdKeyFascicolo ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_rich_annul_vers())) {
            queryStr.append(clause).append("r.tiRichAnnulVers = :tiRichAnnulVers ");
        }
        queryStr.append("ORDER BY r.dtCreazioneRichAnnulVers");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", filtri.getId_ambiente());
        query.setParameter("idUserIam", idUser);

        if (filtri.getId_ente() != null) {
            query.setParameter("idEnte", filtri.getId_ente());
        }
        if (filtri.getId_strut() != null) {
            query.setParameter("idStrut", filtri.getId_strut());
        }
        if (StringUtils.isNotBlank(filtri.getCd_rich_annul_vers())) {
            query.setParameter("cdRichAnnulVers", "%" + filtri.getCd_rich_annul_vers().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getDs_rich_annul_vers())) {
            query.setParameter("dsRichAnnulVers", "%" + filtri.getDs_rich_annul_vers().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getNt_rich_annul_vers())) {
            query.setParameter("ntRichAnnulVers", "%" + filtri.getNt_rich_annul_vers().toUpperCase() + "%");
        }
        if (!filtri.getTi_stato_rich_annul_vers_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_annul_vers_cor().size() == 1) {
                query.setParameter("tiStatoRichAnnulVersCor", filtri.getTi_stato_rich_annul_vers_cor().get(0));
            } else {
                query.setParameter("tiStatoRichAnnulVersCor", filtri.getTi_stato_rich_annul_vers_cor());
            }
        }
        if (filtri.getDt_creazione_rich_annul_vers_da() != null) {
            query.setParameter("dtInizio", filtri.getDt_creazione_rich_annul_vers_da());
        }
        if (filtri.getDt_creazione_rich_annul_vers_a() != null) {
            query.setParameter("dtFine", filtri.getDt_creazione_rich_annul_vers_a());
        }
        if (StringUtils.isNotBlank(filtri.getFl_immediata())) {
            query.setParameter("flImmediata", filtri.getFl_immediata());
        }
        if (StringUtils.isNotBlank(filtri.getFl_annul_ping())) {
            query.setParameter("flAnnulPing", filtri.getFl_annul_ping());
        }
        if (StringUtils.isNotBlank(filtri.getFl_non_annul())) {
            query.setParameter("flNonAnnul", filtri.getFl_non_annul());
        }
        if (StringUtils.isNotBlank(filtri.getCd_registro_key_unita_doc())) {
            query.setParameter("cdRegistroKeyUnitaDoc", filtri.getCd_registro_key_unita_doc());
        }
        if (filtri.getAa_key_unita_doc() != null) {
            query.setParameter("aaKeyUnitaDoc", filtri.getAa_key_unita_doc());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_unita_doc())) {
            query.setParameter("cdKeyUnitaDoc", filtri.getCd_key_unita_doc());
        }
        if (filtri.getAa_fascicolo() != null) {
            query.setParameter("aaFascicolo", filtri.getAa_fascicolo());
        }
        if (StringUtils.isNotBlank(filtri.getCd_key_fascicolo())) {
            query.setParameter("cdKeyFascicolo", filtri.getCd_key_fascicolo());
        }
        if (StringUtils.isNotBlank(filtri.getTi_rich_annul_vers())) {
            query.setParameter("tiRichAnnulVers", filtri.getTi_rich_annul_vers());
        }
        return (List<AroVRicRichAnnvrs>) query.getResultList();
    }

    public List<AroVLisItemRichAnnvrs> getAroVLisItemRichAnnvrs(BigDecimal idRichAnnulVers) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM AroVLisItemRichAnnvrs a WHERE a.idRichAnnulVers = :idRichAnnulVers ORDER BY a.pgItemRichAnnulVers");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<AroVLisItemRichAnnvrs> list = query.getResultList();
        return list;
    }

    public List<AroVLisStatoRichAnnvrs> getAroVLisStatoRichAnnvrs(BigDecimal idRichAnnulVers) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM AroVLisStatoRichAnnvrs a WHERE a.idRichAnnulVers = :idRichAnnulVers ORDER BY a.pgStatoRichAnnulVers");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<AroVLisStatoRichAnnvrs> list = query.getResultList();
        return list;
    }

    public Long countAroItemRichAnnulVers(BigDecimal idRichAnnulVers, String... tiStato) {
        List<String> statiList = Arrays.asList(tiStato);
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(i) FROM AroItemRichAnnulVers i WHERE i.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers ");
        if (!statiList.isEmpty()) {
            if (statiList.size() == 1) {
                queryStr.append("AND i.tiStatoItem = :tiStatoItem ");
            } else {
                queryStr.append("AND i.tiStatoItem IN :tiStatoItem ");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (!statiList.isEmpty()) {
            if (statiList.size() == 1) {
                query.setParameter("tiStatoItem", statiList.get(0));
            } else {
                query.setParameter("tiStatoItem", statiList);
            }
        }
        Long count = (Long) query.getSingleResult();
        return count;
    }
    // </editor-fold>

    /**
     * Elimina tutti gli errori di un certo tipo sugli item della richiesta
     *
     * @param idRichAnnulVers
     *            l'id della richiesta
     * @param tiErrRichAnnulVers
     *            il tipo di errore da eliminare
     */
    public void deleteAroErrRichAnnulVers(long idRichAnnulVers, String... tiErrRichAnnulVers) {
        StringBuilder queryStr = new StringBuilder("DELETE FROM AroErrRichAnnulVers errRichAnnulVers "
                + "WHERE errRichAnnulVers.aroItemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers ");
        if (tiErrRichAnnulVers != null) {
            if (tiErrRichAnnulVers.length > 1) {
                queryStr.append("AND errRichAnnulVers.tiErr IN :tiErrRichAnnulVers ");
            } else {
                queryStr.append("AND errRichAnnulVers.tiErr = :tiErrRichAnnulVers ");
            }
        }
        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        if (tiErrRichAnnulVers != null) {
            List<String> asList = Arrays.asList(tiErrRichAnnulVers);
            if (asList.size() > 1) {
                q.setParameter("tiErrRichAnnulVers", asList);
            } else {
                q.setParameter("tiErrRichAnnulVers", asList.get(0));
            }
        }
        q.executeUpdate();
    }

    /**
     * Restituisce true o false a seconda che un utente sia abilitato al tipo dato passato in input
     *
     * @param idUserIam
     *            id user iam
     * @param idTipoDatoApplic
     *            id tipo dato applicativo
     * @param nmClasseTipoDato
     *            nome classe per tipo dato
     * 
     * @return true/false
     */
    public boolean isUserAbilitatoToTipoDato(long idUserIam, long idTipoDatoApplic, String nmClasseTipoDato) {
        String queryStr = "SELECT COUNT(abilTipoDato) FROM IamAbilTipoDato abilTipoDato "
                + "JOIN abilTipoDato. iamAbilOrganiz abilOrganiz " + "JOIN abilOrganiz.iamUser user "
                + "WHERE user.idUserIam = :idUserIam " + "AND abilTipoDato.nmClasseTipoDato = :nmClasseTipoDato "
                + "AND abilTipoDato.idTipoDatoApplic = :idTipoDatoApplic ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIam", idUserIam);
        query.setParameter("idTipoDatoApplic", idTipoDatoApplic);
        query.setParameter("nmClasseTipoDato", nmClasseTipoDato);
        return (Long) query.getSingleResult() > 0L;
    }

    /**
     * Restituisce l'elenco degli errori di un determinato item con una specifica gravità
     *
     * @param idItemRichAnnulVers
     *            id richiesta annullamento versamento
     * @param tiGravita
     *            tipo gravita
     * 
     * @return la lista di errori sull'item
     */
    public List<AroErrRichAnnulVers> getAroErrRichAnnulVersByGravity(long idItemRichAnnulVers, String tiGravita) {
        Query query = getEntityManager().createQuery(
                "SELECT errRichAnnulVers FROM AroErrRichAnnulVers errRichAnnulVers JOIN errRichAnnulVers.aroItemRichAnnulVers itemRichAnnulVers "
                        + "WHERE itemRichAnnulVers.idItemRichAnnulVers = :idItemRichAnnulVers AND errRichAnnulVers.tiGravita = :tiGravita");
        query.setParameter("idItemRichAnnulVers", idItemRichAnnulVers);
        query.setParameter("tiGravita", tiGravita);
        List<AroErrRichAnnulVers> lista = (List<AroErrRichAnnulVers>) query.getResultList();
        return lista;
    }

    public boolean isUdAnnullata(long idUnitaDoc) {
        Query q = getEntityManager().createQuery("SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc " + "AND unitaDoc.dtAnnul < :endOfTheParer ");
        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        q.setParameter("idUnitaDoc", idUnitaDoc);
        q.setParameter("endOfTheParer", c.getTime());
        return !q.getResultList().isEmpty();
    }

    public boolean isFascicoloAnnullato(long idFascicolo) {
        String queryStr = "SELECT fasFascicolo FROM FasFascicolo fasFascicolo "
                + "WHERE fasFascicolo.idFascicolo = :idFascicolo "
                + "AND fasFascicolo.tiStatoConservazione = :tiStatoConservazione ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiStatoConservazione", TiStatoConservazione.ANNULLATO);
        return !query.getResultList().isEmpty();
    }

    public DecTipoDoc getDecTipoDocPrincipale(long idUnitaDoc) {
        String queryStr = "SELECT tipoDoc FROM AroDoc doc " + "JOIN doc.aroUnitaDoc unitaDoc "
                + "JOIN doc.decTipoDoc tipoDoc " + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND doc.tiDoc = 'PRINCIPALE' ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idUnitaDoc", idUnitaDoc);
        return (DecTipoDoc) q.getSingleResult();
    }

    /**
     * Ricavo l'utente che ha creato la richiesta, ovvero colui che ha definito il primo stato
     *
     * @param idRichAnnulVers
     *            l'id della richiesta
     * 
     * @return l'id utente
     */
    public long getIdUserIamFirstStateRich(BigDecimal idRichAnnulVers) {
        Query q = getEntityManager()
                .createQuery("SELECT statoRichAnnulVers FROM AroStatoRichAnnulVers statoRichAnnulVers "
                        + "WHERE statoRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers "
                        + "ORDER BY statoRichAnnulVers.pgStatoRichAnnulVers ASC ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers.longValue());
        List<AroStatoRichAnnulVers> listaStati = (List<AroStatoRichAnnulVers>) q.getResultList();
        return listaStati.get(0).getIamUser().getIdUserIam();
    }

    public AroStatoRichAnnulVers getStatoRichiestaRecenteAnnulUd(List<Long> idUnitaDocList) {
        Query q = getEntityManager()
                .createQuery("SELECT statoRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers "
                        + "JOIN itemRichAnnulVers.aroUnitaDoc unitaDoc "
                        + "JOIN itemRichAnnulVers.aroRichAnnulVers richAnnulVers "
                        + "JOIN richAnnulVers.aroStatoRichAnnulVers statoRichAnnulVers "
                        + "WHERE unitaDoc.idUnitaDoc IN :idUnitaDocList "
                        + "AND statoRichAnnulVers.tiStatoRichAnnulVers = 'EVASA' "
                        + "ORDER BY statoRichAnnulVers.dtRegStatoRichAnnulVers DESC ");

        q.setParameter("idUnitaDocList", idUnitaDocList);
        List<AroStatoRichAnnulVers> listaStati = (List<AroStatoRichAnnulVers>) q.getResultList();
        return listaStati.get(0);
    }

    public List<ElvVLisElencoFascAnnul> retrieveElvVLisElencoFascAnnul(long idRichAnnulVers) {
        Query query = getEntityManager().createQuery(
                "SELECT elv FROM ElvVLisElencoFascAnnul elv WHERE elv.idRichAnnulVers = :idRichAnnulVers ");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        List<ElvVLisElencoFascAnnul> elenchi = query.getResultList();
        return elenchi;
    }

    public List<ElvVLisElencoFascAnnul> retrieveElvVLisElencoFascAnnul(long idRichAnnulVers, long idElencoVersFasc) {
        Query query = getEntityManager().createQuery(
                "SELECT elv FROM ElvVLisElencoFascAnnul elv WHERE elv.idRichAnnulVers = :idRichAnnulVers AND elv.idElencoVersFasc = :idElencoVersFasc");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        List<ElvVLisElencoFascAnnul> fasAnnuls = query.getResultList();
        return fasAnnuls;
    }

    public void updateFascicoliItem(long idRichAnnulVers, Date dtAnnull, TiStatoConservazione tiStatoConservazione,
            String ntAnnul) {
        Query q = getEntityManager().createQuery(
                "UPDATE FasFascicolo fascicolo SET fascicolo.dtAnnull = :dtAnnull, fascicolo.tiStatoConservazione = :tiStatoConservazione, fascicolo.ntAnnul = :ntAnnul "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.fasFascicolo = fascicolo AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER' ) ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.setParameter("dtAnnull", dtAnnull);
        q.setParameter("tiStatoConservazione", tiStatoConservazione);
        q.setParameter("ntAnnul", ntAnnul);
        q.executeUpdate();
    }

    public void updateCollegamentiFasc(long idRichAnnulVers) {
        Query q = getEntityManager()
                .createQuery("UPDATE FasLinkFascicolo linkFascicolo SET linkFascicolo.fasFascicoloLink = null "
                        + "WHERE EXISTS (SELECT itemRichAnnulVers FROM AroItemRichAnnulVers itemRichAnnulVers WHERE itemRichAnnulVers.aroRichAnnulVers.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers.fasFascicolo = linkFascicolo.fasFascicolo AND itemRichAnnulVers.tiStatoItem = 'DA_ANNULLARE_IN_SACER') ");
        q.setParameter("idRichAnnulVers", idRichAnnulVers);
        q.executeUpdate();
    }

    // MAC#22156
    public int updateStatoConsAipGeneratoAroUnitaDocWithoutOtherFascicolos(long idRichAnnulVers,
            List<String> statiUdExcluded) {
        Query query = getEntityManager()
                .createQuery("UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = 'AIP_GENERATO' "
                        + "WHERE ud.tiStatoConservazione IN :statiUdExcluded AND "
                        + "EXISTS (SELECT udFasc_1 FROM AroItemRichAnnulVers itemRichAnnulVers_1, FasUnitaDocFascicolo udFasc_1 "
                        + "JOIN itemRichAnnulVers_1.aroRichAnnulVers richAnnulVers_1 "
                        + "JOIN udFasc_1.fasFascicolo fasc_1 " + "JOIN udFasc_1.aroUnitaDoc ud_1 "
                        + "WHERE richAnnulVers_1.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers_1.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
                        + "AND itemRichAnnulVers_1.fasFascicolo = fasc_1 AND ud_1 = ud ) "
                        + "AND NOT EXISTS (SELECT udFasc_2 FROM AroItemRichAnnulVers itemRichAnnulVers_2, FasUnitaDocFascicolo udFasc_2 "
                        + "JOIN itemRichAnnulVers_2.aroRichAnnulVers richAnnulVers_2 "
                        + "JOIN udFasc_2.fasFascicolo fasc_2 " + "JOIN udFasc_2.aroUnitaDoc ud_2 "
                        + "WHERE richAnnulVers_2.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers_2.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
                        + "AND itemRichAnnulVers_2.fasFascicolo != fasc_2 AND ud_2 = ud "
                        + "AND fasc_2.dtAnnull = :defaultAnnull) "
                        + "AND EXISTS (SELECT eleDaElab FROM ElvElencoVersDaElab eleDaElab "
                        + "JOIN eleDaElab.elvElencoVer elenco " + "WHERE elenco = ud.elvElencoVer)");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("statiUdExcluded", statiUdExcluded);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnull", c.getTime());

        return query.executeUpdate();
    }

    public int updateStatoConsAipFirmatoAroUnitaDocWithoutOtherFascicolos(long idRichAnnulVers,
            List<String> statiUdExcluded) {
        Query query = getEntityManager()
                .createQuery("UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = 'AIP_FIRMATO' "
                        + "WHERE ud.tiStatoConservazione IN :statiUdExcluded AND "
                        + "EXISTS (SELECT udFasc_1 FROM AroItemRichAnnulVers itemRichAnnulVers_1, FasUnitaDocFascicolo udFasc_1 "
                        + "JOIN itemRichAnnulVers_1.aroRichAnnulVers richAnnulVers_1 "
                        + "JOIN udFasc_1.fasFascicolo fasc_1 " + "JOIN udFasc_1.aroUnitaDoc ud_1 "
                        + "WHERE richAnnulVers_1.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers_1.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
                        + "AND itemRichAnnulVers_1.fasFascicolo = fasc_1 AND ud_1 = ud ) "
                        + "AND NOT EXISTS (SELECT udFasc_2 FROM AroItemRichAnnulVers itemRichAnnulVers_2, FasUnitaDocFascicolo udFasc_2 "
                        + "JOIN itemRichAnnulVers_2.aroRichAnnulVers richAnnulVers_2 "
                        + "JOIN udFasc_2.fasFascicolo fasc_2 " + "JOIN udFasc_2.aroUnitaDoc ud_2 "
                        + "WHERE richAnnulVers_2.idRichAnnulVers = :idRichAnnulVers AND itemRichAnnulVers_2.tiStatoItem = 'DA_ANNULLARE_IN_SACER' "
                        + "AND itemRichAnnulVers_2.fasFascicolo != fasc_2 AND ud_2 = ud "
                        + "AND fasc_2.dtAnnull = :defaultAnnull )"
                        + "AND NOT EXISTS (SELECT eleDaElab FROM ElvElencoVersDaElab eleDaElab "
                        + "JOIN eleDaElab.elvElencoVer elenco " + "WHERE elenco = ud.elvElencoVer)");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("statiUdExcluded", statiUdExcluded);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnull", c.getTime());

        return query.executeUpdate();
    }
    // end MAC#22156

    public List<ElvVLisFascAnnulByElenco> retrieveElvVLisFascAnnulByElenco(long idRichAnnulVers,
            long idElencoVersFasc) {
        Query query = getEntityManager().createQuery(
                "SELECT elv FROM ElvVLisFascAnnulByElenco elv WHERE elv.idRichAnnulVers = :idRichAnnulVers AND elv.idElencoVersFasc = :idElencoVersFasc");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        List<ElvVLisFascAnnulByElenco> fascAnnuls = query.getResultList();
        return fascAnnuls;
    }

    public String getXmlRichAnnulVersByTipo(Long idRichAnnulVers, CostantiDB.TiXmlRichAnnulVers tiXmlRichAnnulVers) {
        Query query = getEntityManager().createQuery("SELECT xmlRich.blXmlRichAnnulVers "
                + "FROM AroXmlRichAnnulVers xmlRich " + "JOIN xmlRich.aroRichAnnulVers annulVers "
                + "WHERE annulVers.idRichAnnulVers = :idRichAnnulVers AND xmlRich.tiXmlRichAnnulVers = :tiXmlRichAnnulVers ");
        query.setParameter("idRichAnnulVers", idRichAnnulVers);
        query.setParameter("tiXmlRichAnnulVers", tiXmlRichAnnulVers.name());
        return (String) query.getSingleResult();
    }
}
