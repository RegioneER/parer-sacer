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
package it.eng.parer.scarto.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.bigDecimalFromLong;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroErrRichScartoVers;
import it.eng.parer.entity.AroRichScartoVers;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.viewEntity.AroVLisItemRichScarto;
import it.eng.parer.viewEntity.AroVLisStatoRichScarto;
import it.eng.parer.viewEntity.AroVRicRichScarto;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ScartoHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScartoHelper.class);

    /**
     * Verifica l'esistenza di una richiesta di scarto con codice <code>cdRichAnnulVers</code> per
     * la struttura <code>idStrut</code>
     *
     * @param cdRichScartoVers code richiesta scarto versamento
     * @param idStrut          id struttura
     *
     * @return true se esiste
     */
    public boolean isRichScartoVersExisting(String cdRichScartoVers, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(r) FROM AroRichScartoVers r WHERE r.cdRichScartoVers = :cdRichScartoVers AND r.orgStrut.idStrut = :idStrut");
        query.setParameter("cdRichScartoVers", cdRichScartoVers);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Ritorna la richiesta data l'unità documentaria come parametro se lo stato richiesta è APERTA
     * o CHIUSA
     *
     * @param idUnitaDoc       id unita doc
     * @param idRichScartoVers id della richiesta da escludere nel controllo in quanto contiene già
     *                         l'ud
     *
     * @return true se è presente
     */
    public AroRichScartoVers getAroRichScartoVersContainingUd(Long idUnitaDoc,
            Long idRichScartoVers) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT rich FROM AroItemRichScartoVers item JOIN item.aroRichScartoVers rich JOIN rich.aroStatoRichScartoVers stati WHERE ");
        if (idRichScartoVers != null) {
            queryStr.append("rich.idRichScartoVers != :idRichScartoVers").append(" AND ");
        }
        queryStr.append(
                "item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND stati.pgStatoRichScartoVers = (SELECT MAX(maxStati.pgStatoRichScartoVers) FROM AroStatoRichScartoVers maxStati WHERE maxStati.aroRichScartoVers.idRichScartoVers = rich.idRichScartoVers) AND stati.tiStatoRichScartoVers IN ('APERTA','CHIUSA') ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUnitaDoc", idUnitaDoc);
        if (idRichScartoVers != null) {
            query.setParameter("idRichScartoVers", idRichScartoVers);
        }
        List<AroRichScartoVers> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Conta i documenti versati (in versamento o aggiunta) di una ud in una specifica data
     *
     * @param idUnitaDoc  id unita doc
     * @param tiCreazione AGGIUNTA_DOCUMENTO o VERSAMENTO_UNITA_DOC
     * @param dtVers      data di versamento
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
        return (Long) query.getSingleResult();
    }

    /**
     * Ricavo il progressivo più alto tra tutti gli stati di una determinata richiesta
     *
     * @param idRichScartoVers l'id della richiesta di cui voglio conoscere il progressivo stato
     *                         maggiore
     *
     * @return il progressivo
     */
    public BigDecimal getUltimoProgressivoStatoRichiestaScarto(long idRichScartoVers) {
        Query q = getEntityManager().createQuery(
                "SELECT MAX(statoRichScartoVers.pgStatoRichScartoVers) FROM AroStatoRichScartoVers statoRichScartoVers "
                        + "WHERE statoRichScartoVers.aroRichScartoVers.idRichScartoVers = :idRichScartoVers ");
        q.setParameter("idRichScartoVers", idRichScartoVers);
        return (BigDecimal) q.getSingleResult() != null ? (BigDecimal) q.getSingleResult()
                : BigDecimal.ZERO;
    }

    public void updateStatoItemScartoList(Long idRichScartoVers, String tiStatoItemScarto) {
        Query q = getEntityManager().createQuery(
                "UPDATE AroItemRichScartoVers itemRichScartoVers SET itemRichScartoVers.tiStatoItemScarto = :tiStatoItemScarto "
                        + "WHERE itemRichScartoVers.aroRichScartoVers.idRichScartoVers = :idRichScartoVers AND itemRichScartoVers.tiStatoItemScarto = 'DA_SCARTARE'");
        q.setParameter("idRichScartoVers", idRichScartoVers);
        q.setParameter("tiStatoItemScarto", tiStatoItemScarto);
        q.executeUpdate();
    }

    // <editor-fold defaultstate="collapsed" desc="Query per funzioni online">
    public List<AroVRicRichScarto> retrieveAroVRicRichScarto(long idUser,
            RicercaRichScartoVersBean filtri) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicRichScarto ("
                        + "r.cdRichScartoVers,r.dsRichScartoVers,r.dtCreazioneRichScartoVers, r.flNonScartabile, "
                        + "r.idAmbiente,r.idEnte,r.id.idRichScartoVers,r.idStrut,r.id.idUserIam,r.niItem,r.niItemNonScartati,"
                        + "r.nmAmbiente,r.nmEnte,r.nmStrut,r.ntRichScartoVers,r.tiCreazioneRichScartoVers,r.tiStatoRichScartoVersCor)"
                        + " FROM AroVRicRichScarto r WHERE r.id.idUserIam = :idUserIam AND r.idAmbiente = :idAmbiente ");
        if (filtri.getId_ente() != null) {
            queryStr.append(clause).append("r.idEnte = :idEnte ");
        }
        if (filtri.getId_strut() != null) {
            queryStr.append(clause).append("r.idStrut = :idStrut ");
        }
        if (StringUtils.isNotBlank(filtri.getCd_rich_scarto_vers())) {
            queryStr.append(clause).append("UPPER(r.cdRichScartoVers) like :cdRichScartoVers ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_rich_scarto_vers())) {
            queryStr.append(clause).append("UPPER(r.dsRichScartoVers) like :dsRichScartoVers ");
        }
        if (StringUtils.isNotBlank(filtri.getNt_rich_scarto_vers())) {
            queryStr.append(clause).append("UPPER(r.ntRichScartoVers) like :ntRichScartoVers ");
        }
        if (!filtri.getTi_stato_rich_scarto_vers_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_scarto_vers_cor().size() == 1) {
                queryStr.append(clause)
                        .append("r.tiStatoRichScartoVersCor = :tiStatoRichScartoVersCor ");
            } else {
                queryStr.append(clause)
                        .append("r.tiStatoRichScartoVersCor IN (:tiStatoRichScartoVersCor) ");
            }
        }
        if (filtri.getDt_creazione_rich_scarto_vers_da() != null) {
            queryStr.append(clause).append("r.dtCreazioneRichScartoVers >= :dtInizio ");
        }
        if (filtri.getDt_creazione_rich_scarto_vers_a() != null) {
            queryStr.append(clause).append("r.dtCreazioneRichScartoVers <= :dtFine ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_non_scartabile())) {
            queryStr.append(clause).append("r.flNonScartabile = :flNonScartabile ");
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
        queryStr.append("ORDER BY r.dtCreazioneRichScartoVers");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", filtri.getId_ambiente());
        query.setParameter("idUserIam", bigDecimalFromLong(idUser));

        if (filtri.getId_ente() != null) {
            query.setParameter("idEnte", filtri.getId_ente());
        }
        if (filtri.getId_strut() != null) {
            query.setParameter("idStrut", filtri.getId_strut());
        }
        if (StringUtils.isNotBlank(filtri.getCd_rich_scarto_vers())) {
            query.setParameter("cdRichScartoVers",
                    "%" + filtri.getCd_rich_scarto_vers().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getDs_rich_scarto_vers())) {
            query.setParameter("dsRichScartoVers",
                    "%" + filtri.getDs_rich_scarto_vers().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(filtri.getNt_rich_scarto_vers())) {
            query.setParameter("ntRichScartoVers",
                    "%" + filtri.getNt_rich_scarto_vers().toUpperCase() + "%");
        }
        if (!filtri.getTi_stato_rich_scarto_vers_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_scarto_vers_cor().size() == 1) {
                query.setParameter("tiStatoRichScartoVersCor",
                        filtri.getTi_stato_rich_scarto_vers_cor().get(0));
            } else {
                query.setParameter("tiStatoRichScartoVersCor",
                        filtri.getTi_stato_rich_scarto_vers_cor());
            }
        }
        if (filtri.getDt_creazione_rich_scarto_vers_da() != null) {
            Date dtOriginale = filtri.getDt_creazione_rich_scarto_vers_da();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dtOriginale);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            query.setParameter("dtInizio", cal.getTime());
        }
        if (filtri.getDt_creazione_rich_scarto_vers_a() != null) {
            Date dtOriginale = filtri.getDt_creazione_rich_scarto_vers_a();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dtOriginale);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            query.setParameter("dtFine", cal.getTime());
        }
        if (StringUtils.isNotBlank(filtri.getFl_non_scartabile())) {
            query.setParameter("flNonScartabile", filtri.getFl_non_scartabile());
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
        return query.getResultList();
    }

    public List<AroVLisItemRichScarto> getAroVLisItemRichScarto(BigDecimal idRichScartoVers) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM AroVLisItemRichScarto a WHERE a.idRichScartoVers = :idRichScartoVers ORDER BY a.pgItemRichScartoVers");
        query.setParameter("idRichScartoVers", idRichScartoVers);
        return query.getResultList();
    }

    public List<AroVLisStatoRichScarto> getAroVLisStatoRichScarto(BigDecimal idRichScartoVers) {
        Query query = getEntityManager().createQuery(
                "SELECT a FROM AroVLisStatoRichScarto a WHERE a.idRichScartoVers = :idRichScartoVers ORDER BY a.pgStatoRichScartoVers");
        query.setParameter("idRichScartoVers", idRichScartoVers);
        return query.getResultList();
    }

    // </editor-fold>

    /**
     * Elimina tutti gli errori di un certo tipo sugli item della richiesta
     *
     * @param idRichScartoVers    l'id della richiesta
     * @param tiErrRichScartoVers il tipo di errore da eliminare
     */
    public void deleteAroErrRichScartoVers(long idRichScartoVers, String... tiErrRichScartoVers) {

        StringBuilder selectJpql = new StringBuilder();
        selectJpql.append("SELECT e FROM AroErrRichScartoVers e ");
        selectJpql.append(
                "WHERE e.aroItemRichScartoVers.aroRichScartoVers.idRichScartoVers = :idRichScartoVers ");

        boolean filterByTiErr = tiErrRichScartoVers != null && tiErrRichScartoVers.length > 0;

        if (filterByTiErr) {
            selectJpql.append("AND e.tiErr IN (:tiErrList) ");
        }

        String deleteJpql = "DELETE FROM AroErrRichScartoVers e WHERE e IN ("
                + selectJpql.toString() + ")";
        Query q = getEntityManager().createQuery(deleteJpql);

        q.setParameter("idRichScartoVers", idRichScartoVers);

        if (filterByTiErr) {
            q.setParameter("tiErrList", Arrays.asList(tiErrRichScartoVers));
        }

        q.executeUpdate();
    }

    /**
     * Restituisce true o false a seconda che un utente sia abilitato al tipo dato passato in input
     *
     * @param idUserIam        id user iam
     * @param idTipoDatoApplic id tipo dato applicativo
     * @param nmClasseTipoDato nome classe per tipo dato
     *
     * @return true/false
     */
    public boolean isUserAbilitatoToTipoDato(long idUserIam, long idTipoDatoApplic,
            String nmClasseTipoDato) {
        String queryStr = "SELECT COUNT(abilTipoDato) FROM IamAbilTipoDato abilTipoDato "
                + "JOIN abilTipoDato. iamAbilOrganiz abilOrganiz "
                + "JOIN abilOrganiz.iamUser user " + "WHERE user.idUserIam = :idUserIam "
                + "AND abilTipoDato.nmClasseTipoDato = :nmClasseTipoDato "
                + "AND abilTipoDato.idTipoDatoApplic = :idTipoDatoApplic ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUserIam", idUserIam);
        query.setParameter("idTipoDatoApplic", bigDecimalFromLong(idTipoDatoApplic));
        query.setParameter("nmClasseTipoDato", nmClasseTipoDato);
        return (Long) query.getSingleResult() > 0L;
    }

    /**
     * Restituisce l'elenco degli errori di un determinato item con una specifica gravità
     *
     * @param idItemRichScartoVers id richiesta scarto versamento
     * @param tiGravita            tipo gravita
     *
     * @return la lista di errori sull'item
     */
    public List<AroErrRichScartoVers> getAroErrRichScartoVersByGravity(long idItemRichScartoVers,
            String tiGravita) {
        Query query = getEntityManager().createQuery(
                "SELECT errRichScartoVers FROM AroErrRichScartoVers errRichScartoVers JOIN errRichScartoVers.aroItemRichScartoVers itemRichScartoVers "
                        + "WHERE itemRichScartoVers.idItemRichScartoVers = :idItemRichScartoVers AND errRichScartoVers.tiGravita = :tiGravita");
        query.setParameter("idItemRichScartoVers", idItemRichScartoVers);
        query.setParameter("tiGravita", tiGravita);
        return query.getResultList();
    }

    public boolean isUdScartata(long idUnitaDoc) {
        Query q = getEntityManager().createQuery("SELECT 1 FROM AroItemRichScartoVers item "
                + "WHERE item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND item.tiStatoItemScarto = 'SCARTATO' ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        return !q.getResultList().isEmpty();
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
     * Verifica se un'unità documentaria è già stata annullata.
     *
     * @param idUnitaDoc id dell'unità documentaria
     * @return true se l'UD è annullata
     */
    public boolean isUdAnnullata(long idUnitaDoc) {
        // Verifica se esiste un item di annullamento con stato ANNULLATO per questa UD
        Query q = getEntityManager().createQuery("SELECT 1 FROM AroItemRichAnnulVers item "
                + "WHERE item.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND item.tiStatoItem = 'ANNULLATO' ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        return !q.getResultList().isEmpty();
    }

}
