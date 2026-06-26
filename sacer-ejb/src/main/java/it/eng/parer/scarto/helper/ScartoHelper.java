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
import it.eng.parer.entity.AroItemPropScartoVers;
import it.eng.parer.entity.AroPropScartoVers;
import it.eng.parer.entity.AroRichScartoVers;
import it.eng.parer.entity.AroStatoPropScartoVers;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.scarto.dto.FiltriRicercaUdScartoDto;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.viewEntity.AroVLisItemRichScarto;
import it.eng.parer.viewEntity.AroVLisStatoRichScarto;
import it.eng.parer.viewEntity.AroVRicPropScartoVers;
import it.eng.parer.viewEntity.AroVRicRichScarto;
import it.eng.parer.ws.utils.CostantiDB;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ScartoHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(ScartoHelper.class);

    // Condizione: C'è conflitto se c'è un registro E (Anni diversi OR Flag Illimitata diversi)
    private static final String SQL_COND_CONFLITTO = "(r.ID_REGISTRO_UNITA_DOC IS NOT NULL AND (COALESCE(t.NI_AA_CONSERV, -1) <> COALESCE(r.NI_AA_CONSERV, -1) OR COALESCE(t.FL_CONSERV_ILLIMITATA, '0') <> COALESCE(r.FL_CONSERV_ILLIMITATA, '0')))";

    // Messaggio: Compone la stringa es. "Conflitto regole - Tipo UD: 10 anni; Registro: illimitata.
    // "
    private static final String SQL_MSG_CONFLITTO = "CASE WHEN " + SQL_COND_CONFLITTO
            + " THEN 'Conflitto regole - Tipo UD: ' || "
            + "CASE WHEN COALESCE(t.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'illimitata' ELSE COALESCE(TO_CHAR(t.NI_AA_CONSERV), 'n.d.') || ' anni' END || "
            + "'; Registro: ' || "
            + "CASE WHEN COALESCE(r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'illimitata' ELSE COALESCE(TO_CHAR(r.NI_AA_CONSERV), 'n.d.') || ' anni' END || '. ' ELSE '' END ";

    public List<AroVRicPropScartoVers> getAroVRicPropScartoVersList(long idUserIam,
            BigDecimal idStrut, String cdPropScartoVers, Date dtCreazionePropScartoVersDa,
            Date dtCreazionePropScartoVersA, Date dtUltimaModPropScartoVersDa,
            Date dtUltimaModPropScartoVersA, String tiStatoPropScartoVersCor) {
        StringBuilder queryStr = new StringBuilder("SELECT prop FROM AroVRicPropScartoVers prop "
                + "WHERE prop.id.idUserIam = :idUserIam AND prop.idStrut = :idStrut ");

        if (StringUtils.isNotBlank(cdPropScartoVers))
            queryStr.append(" AND prop.cdPropScartoVers LIKE :cdPropScartoVers ");

        Date[] dateCreazionePropNormalizzate = new Date[2];
        if (dtCreazionePropScartoVersDa != null || dtCreazionePropScartoVersA != null) {
            dateCreazionePropNormalizzate = normalizeDateForScarto(dtCreazionePropScartoVersDa,
                    dtCreazionePropScartoVersA);
            queryStr.append(
                    " AND prop.dtCreazionePropScartoVers >= :dtCreazionePropScartoVersDa AND prop.dtCreazionePropScartoVers <= :dtCreazionePropScartoVersA ");
        }

        Date[] dateUltimaModPropNormalizzate = new Date[2];
        if (dtUltimaModPropScartoVersDa != null || dtUltimaModPropScartoVersA != null) {
            dateUltimaModPropNormalizzate = normalizeDateForScarto(dtUltimaModPropScartoVersDa,
                    dtUltimaModPropScartoVersA);
            queryStr.append(
                    " AND prop.dtUltimaModPropScartoVers >= :dtUltimaModPropScartoVersDa AND prop.dtUltimaModPropScartoVers <= :dtUltimaModPropScartoVersA ");
        }

        if (StringUtils.isNotBlank(tiStatoPropScartoVersCor))
            queryStr.append(" AND prop.tiStatoPropScartoVersCor = :tiStatoPropScartoVersCor ");

        queryStr.append(" ORDER BY prop.cdPropScartoVers DESC ");

        Query q = getEntityManager().createQuery(queryStr.toString());

        q.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        q.setParameter("idStrut", idStrut);

        if (StringUtils.isNotBlank(cdPropScartoVers))
            q.setParameter("cdPropScartoVers", "%" + cdPropScartoVers + "%");

        if (dtCreazionePropScartoVersDa != null || dtCreazionePropScartoVersA != null) {
            q.setParameter("dtCreazionePropScartoVersDa", dateCreazionePropNormalizzate[0]);
            q.setParameter("dtCreazionePropScartoVersA", dateCreazionePropNormalizzate[1]);
        }

        if (dtUltimaModPropScartoVersDa != null || dtUltimaModPropScartoVersA != null) {
            q.setParameter("dtUltimaModPropScartoVersDa", dateUltimaModPropNormalizzate[0]);
            q.setParameter("dtUltimaModPropScartoVersA", dateUltimaModPropNormalizzate[1]);
        }

        if (StringUtils.isNotBlank(tiStatoPropScartoVersCor))
            q.setParameter("tiStatoPropScartoVersCor", tiStatoPropScartoVersCor);

        return (List<AroVRicPropScartoVers>) q.getResultList();
    }

    /**
     * Esegue la query nativa per il report delle UD scartabili, filtrando dinamicamente in base ai
     * parametri impostati dall'utente.
     *
     * @param filtri         i filtri di ricerca
     * @param idPropCorrente proposta corrente
     * @return oggetto contenente i dati del report sulle ud
     */
    public List<Object[]> getReportUdScartoNative(FiltriRicercaUdScartoDto filtri,
            Long idPropCorrente) {

        // Definizione del "Conflitto"
        // C'è conflitto se l'UD ha un registro E i tempi o i flag illimitata discordano tra Tipo UD
        // e Registro.
        // Uso COALESCE con -1 per gestire in sicurezza i null matematici.
        String sqlCondConflitto = "(r.ID_REGISTRO_UNITA_DOC IS NOT NULL AND (COALESCE(t.NI_AA_CONSERV, -1) <> COALESCE(r.NI_AA_CONSERV, -1) OR COALESCE(t.FL_CONSERV_ILLIMITATA, '0') <> COALESCE(r.FL_CONSERV_ILLIMITATA, '0')))";

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                // INDICE 0: Tipologia UD
                .append("    t.NM_TIPO_UNITA_DOC AS tipologia_ud, ")

                // INDICE 1: IL TOTALE DELLE UD (Rimesso al suo posto corretto!)
                .append("    COUNT(u.ID_UNITA_DOC) as qta_totale, ")

                // INDICE 2: RAGGIUNTO (NON in altre proposte, NON in conflitto)
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NULL AND NOT ")
                .append(sqlCondConflitto)
                .append(" AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 1 ELSE 0 END) as qta_raggiunto, ")

                // INDICE 3: NON RAGGIUNTO
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NULL AND NOT ")
                .append(sqlCondConflitto)
                .append(" AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 1 ELSE 0 END) as qta_non_raggiunto, ")

                // INDICE 4: SENZA INDICAZIONE
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NULL AND NOT ")
                .append(sqlCondConflitto)
                .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 1 ELSE 0 END) as qta_senza_indicazione, ")

                // INDICE 5: ILLIMITATA
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NULL AND NOT ")
                .append(sqlCondConflitto)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 1 ELSE 0 END) as qta_illimitate, ")

                // INDICE 6: CONFLITTI
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NULL AND ")
                .append(sqlCondConflitto).append(" THEN 1 ELSE 0 END) as qta_conflitti, ")

                // INDICE 7: IN ALTRE PROPOSTE
                .append("    SUM(CASE WHEN altre.ID_UNITA_DOC IS NOT NULL THEN 1 ELSE 0 END) as qta_in_altre_prop ")

                .append("FROM ARO_UNITA_DOC u ")
                .append("JOIN DEC_TIPO_UNITA_DOC t ON u.ID_TIPO_UNITA_DOC = t.ID_TIPO_UNITA_DOC ")
                .append("LEFT JOIN DEC_REGISTRO_UNITA_DOC r ON u.ID_REGISTRO_UNITA_DOC = r.ID_REGISTRO_UNITA_DOC ")

                .append("LEFT JOIN ( ")
                .append("    SELECT DISTINCT ID_UNITA_DOC FROM ARO_ITEM_PROP_SCARTO_VERS ")
                .append("    WHERE ID_PROP_SCARTO_VERS <> :idPropCorrente AND TI_ITEM_PROP_SCARTO_VERS = 'UNI_DOC' ")
                .append(") altre ON u.ID_UNITA_DOC = altre.ID_UNITA_DOC ")

                .append("WHERE u.ID_STRUT = :idStrut ")
                .append("  AND u.TI_STATO_CONSERVAZIONE <> 'ANNULLATA' ");

        // --- APPLICAZIONE DINAMICA DEI FILTRI ---
        if (filtri.getRegistro() != null)
            sql.append(" AND u.ID_REGISTRO_UNITA_DOC = :registro ");
        if (filtri.getAnno() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC = :anno ");
        if (filtri.getAnnoDa() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC >= :annoDa ");
        if (filtri.getAnnoA() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC <= :annoA ");
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            sql.append(" AND u.CD_KEY_UNITA_DOC = :numeroUd ");
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            sql.append(" AND u.CD_KEY_UNITA_DOC >= :numeroDa ");
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            sql.append(" AND u.CD_KEY_UNITA_DOC <= :numeroA ");
        if (filtri.getDataUd() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) = :dataUd ");
        if (filtri.getDataUdDa() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) >= :dataUdDa ");
        if (filtri.getDataUdA() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) <= :dataUdA ");

        if (StringUtils.isNotBlank(filtri.getOggettoUd())) {
            sql.append(" AND UPPER(u.DL_OGGETTO_UNITA_DOC) LIKE :oggettoUd ");
        }
        if (filtri.getTipologiaUd() != null) {
            sql.append(" AND t.ID_TIPO_UNITA_DOC = :tipologiaUd ");
        }
        if (StringUtils.isNotBlank(filtri.getClassifica())) {
            sql.append(" AND u.DS_CLASSIF_PRINC = :classifica ");
        }
        if (filtri.getTempoConservazione() != null) {
            sql.append(" AND COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) = :tempoConservazione ");
        }
        if ("SI".equals(filtri.getIllimitato())) {
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' ");
        } else if ("NO".equals(filtri.getIllimitato())) {
            // Esclude quelle illimitate
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }

        // --- FILTRO: TEMPO SUPERATO ---
        if ("SI".equals(filtri.getTempoSuperato())) {
            // UD pronte per lo scarto
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        } else if ("NO".equals(filtri.getTempoSuperato())) {
            // UD "giovani", tempo non ancora raggiunto (escludendo sempre le illimitate e quelle
            // senza indicazione)
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }
        if (!filtri.isIncludiFascicoli()) {
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM FAS_UNITA_DOC_FASCICOLO relFasc WHERE relFasc.ID_UNITA_DOC = u.ID_UNITA_DOC) ");
        }
        if (!filtri.isIncludiSerie()) {
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM ARO_UD_APPART_VER_SERIE relSerie WHERE relSerie.ID_UNITA_DOC = u.ID_UNITA_DOC) ");
        }

        sql.append(" GROUP BY t.NM_TIPO_UNITA_DOC ORDER BY t.NM_TIPO_UNITA_DOC ASC ");

        Query q = getEntityManager().createNativeQuery(sql.toString());

        q.setParameter("idStrut", filtri.getIdStrut());
        q.setParameter("idPropCorrente", idPropCorrente); // Parametro fondamentale per escludere il
                                                          // carrello corrente dalle "altre
                                                          // proposte"

        if (filtri.getRegistro() != null)
            q.setParameter("registro", filtri.getRegistro());
        if (filtri.getAnno() != null)
            q.setParameter("anno", filtri.getAnno());
        if (filtri.getAnnoDa() != null)
            q.setParameter("annoDa", filtri.getAnnoDa());
        if (filtri.getAnnoA() != null)
            q.setParameter("annoA", filtri.getAnnoA());
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            q.setParameter("numeroUd", filtri.getNumeroUd());
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            q.setParameter("numeroDa", filtri.getNumeroDa());
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            q.setParameter("numeroA", filtri.getNumeroA());
        if (filtri.getDataUd() != null)
            q.setParameter("dataUd", filtri.getDataUd());
        if (filtri.getDataUdDa() != null)
            q.setParameter("dataUdDa", filtri.getDataUdDa());
        if (filtri.getDataUdA() != null)
            q.setParameter("dataUdA", filtri.getDataUdA());
        if (StringUtils.isNotBlank(filtri.getOggettoUd()))
            q.setParameter("oggettoUd", "%" + filtri.getOggettoUd().toUpperCase() + "%");
        if (filtri.getTipologiaUd() != null)
            q.setParameter("tipologiaUd", filtri.getTipologiaUd());
        if (StringUtils.isNotBlank(filtri.getClassifica()))
            q.setParameter("classifica", filtri.getClassifica());
        if (filtri.getTempoConservazione() != null)
            q.setParameter("tempoConservazione", filtri.getTempoConservazione());

        return q.getResultList();
    }

    private Date[] normalizeDateForScarto(Date data_da, Date data_a) {
        // Se data_da è null, impostalo al 1 gennaio 2000
        Calendar calDa = Calendar.getInstance();
        if (data_da == null) {
            calDa.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        } else {
            calDa.setTime(data_da);
            calDa.set(Calendar.HOUR_OF_DAY, 0);
            calDa.set(Calendar.MINUTE, 0);
            calDa.set(Calendar.SECOND, 0);
        }
        calDa.set(Calendar.MILLISECOND, 0);
        data_da = calDa.getTime();

        // Se data_a è null, impostalo alla data odierna
        Calendar calA = Calendar.getInstance();
        if (data_a == null) {
            calA.setTime(new Date());
        } else {
            calA.setTime(data_a);
        }
        calA.set(Calendar.HOUR_OF_DAY, 23);
        calA.set(Calendar.MINUTE, 59);
        calA.set(Calendar.SECOND, 59);
        calA.set(Calendar.MILLISECOND, 999);
        data_a = calA.getTime();

        return new Date[] {
                data_da, data_a };
    }

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

    /**
     * Esegue la INSERT JPA della testata della Proposta di Scarto
     *
     * @param proposta L'entity della proposta da salvare
     */
    public void insertPropostaScarto(AroPropScartoVers proposta) {
        getEntityManager().persist(proposta);
        getEntityManager().flush(); // Forza la insert su DB per generare la Primary Key
    }

    /**
     * Esegue la INSERT JPA dello storico stati della Proposta di Scarto
     *
     * @param stato L'entity dello stato da salvare
     */
    public void insertStatoPropostaScarto(AroStatoPropScartoVers stato) {
        getEntityManager().persist(stato);
        getEntityManager().flush(); // Forza la insert su DB per generare la Primary Key
    }

    /**
     * Calcola il prossimo progressivo per una proposta di scarto, dato l'anno e la struttura.
     *
     * @param idStrut id struttura
     * @param anno    anno
     * @return il numero del successivo progressivo
     */
    public BigDecimal getNextPgPropScartoVers(BigDecimal idStrut, int anno) {
        Query q = getEntityManager()
                .createQuery("SELECT MAX(p.pgPropScartoVers) FROM AroPropScartoVers p "
                        + "WHERE p.orgStrut.idStrut = :idStrut "
                        + "AND EXTRACT(YEAR FROM p.dtCreazione) = :anno"); // Usiamo dtCreazione per
                                                                           // compatibilità JPA

        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        q.setParameter("anno", anno);

        BigDecimal maxPg = (BigDecimal) q.getSingleResult();
        if (maxPg == null) {
            return BigDecimal.ONE; // Primo inserimento dell'anno
        }
        return maxPg.add(BigDecimal.ONE);
    }

    /**
     * Identificatore della colonna cliccata nel report UD.
     */
    public enum ColonnaReportUd {
        RAGGIUNTO, NON_RAGGIUNTO, SENZA_INDICAZIONE, ILLIMITATA, CONFLITTI, IN_ALTRE_PROPOSTE,
        TOTALE
    }

    /**
     * Estrae le singole UD appartenenti al "secchiello" cliccato dall'utente.
     *
     * @param filtri            filtri ricerca ud per proposta
     * @param tipologiaCliccata tipologia ud
     * @param colonnaCliccata   colonna cliccata
     * @param idPropCorrente    proposta ciorrente
     * @return lista delle ud sulla base della colonna cliccata
     */
    public List<Object[]> getListaUdScartoNative(FiltriRicercaUdScartoDto filtri,
            String tipologiaCliccata, ColonnaReportUd colonnaCliccata, BigDecimal idPropCorrente) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ").append("    u.ID_UNITA_DOC, ") // Indice 0
                .append("    t.NM_TIPO_UNITA_DOC as tipologia_ud, ") // Indice 1
                .append("    u.CD_REGISTRO_KEY_UNITA_DOC as registro, ") // Indice 2
                .append("    u.AA_KEY_UNITA_DOC as anno, ") // Indice 3
                .append("    u.CD_KEY_UNITA_DOC as numero, ") // Indice 4

                // COSTRUZIONE STRINGA ALERT CONCATENATA
                .append("    TRIM( ").append("        ").append(SQL_MSG_CONFLITTO).append(" || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'Tenuta illimitata. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'Tempo conservazione non raggiunto. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 'Senza indicazione. ' ELSE '' END ")
                .append("    ) as ds_alert_scarto, ") // Indice 5

                // PALLINO SCARTABILE (Verde/Rosso)
                .append("    CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'SI' ELSE 'NO' END as fl_scartabile ") // Indice
                                                                                                                                                                                                                                                    // 6

                .append("FROM ARO_UNITA_DOC u ")
                .append("JOIN DEC_TIPO_UNITA_DOC t ON u.ID_TIPO_UNITA_DOC = t.ID_TIPO_UNITA_DOC ")
                .append("LEFT JOIN DEC_REGISTRO_UNITA_DOC r ON u.ID_REGISTRO_UNITA_DOC = r.ID_REGISTRO_UNITA_DOC ")
                .append("LEFT JOIN FAS_UNITA_DOC_FASCICOLO rel_fasc ON u.ID_UNITA_DOC = rel_fasc.ID_UNITA_DOC ")
                .append("LEFT JOIN FAS_FASCICOLO fasc ON rel_fasc.ID_FASCICOLO = fasc.ID_FASCICOLO ")

                // JOIN ESCLUSIONE ALTRE PROPOSTE
                .append("LEFT JOIN ( ")
                .append("    SELECT DISTINCT ID_UNITA_DOC FROM ARO_ITEM_PROP_SCARTO_VERS ")
                .append("    WHERE ID_PROP_SCARTO_VERS <> :idPropCorrente AND TI_ITEM_PROP_SCARTO_VERS = 'UNI_DOC' ")
                .append(") altre ON u.ID_UNITA_DOC = altre.ID_UNITA_DOC ");

        // WHERE BASE E TIPOLOGIA CLICCATA
        sql.append("WHERE u.ID_STRUT = :idStrut ")
                .append("  AND u.TI_STATO_CONSERVAZIONE <> 'ANNULLATA' ")
                .append("  AND t.NM_TIPO_UNITA_DOC = :tipologiaCliccata ");

        // APPLICAZIONE FILTRI DELLA MASCHERA
        if (filtri.getRegistro() != null)
            sql.append(" AND u.ID_REGISTRO_UNITA_DOC = :registro ");
        if (filtri.getAnno() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC = :anno ");
        if (filtri.getAnnoDa() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC >= :annoDa ");
        if (filtri.getAnnoA() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC <= :annoA ");
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            sql.append(" AND u.CD_KEY_UNITA_DOC = :numeroUd ");
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            sql.append(" AND u.CD_KEY_UNITA_DOC >= :numeroDa ");
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            sql.append(" AND u.CD_KEY_UNITA_DOC <= :numeroA ");
        if (filtri.getDataUd() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) = :dataUd ");
        if (filtri.getDataUdDa() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) >= :dataUdDa ");
        if (filtri.getDataUdA() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) <= :dataUdA ");
        if (StringUtils.isNotBlank(filtri.getOggettoUd()))
            sql.append(" AND UPPER(u.DL_OGGETTO_UNITA_DOC) LIKE :oggettoUd ");
        if (filtri.getTipologiaUd() != null)
            sql.append(" AND t.ID_TIPO_UNITA_DOC = :idTipoUd ");
        if (StringUtils.isNotBlank(filtri.getClassifica()))
            sql.append(" AND u.DS_CLASSIF_PRINC = :classifica ");
        if (filtri.getTempoConservazione() != null)
            sql.append(" AND COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) = :tempoConservazione ");

        // Filtri "SI/NO"
        if ("SI".equals(filtri.getIllimitato())) {
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' ");
        } else if ("NO".equals(filtri.getIllimitato())) {
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }

        if ("SI".equals(filtri.getTempoSuperato())) {
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        } else if ("NO".equals(filtri.getTempoSuperato())) {
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }

        if (!filtri.isIncludiFascicoli())
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM FAS_UNITA_DOC_FASCICOLO relFasc WHERE relFasc.ID_UNITA_DOC = u.ID_UNITA_DOC) ");
        if (!filtri.isIncludiSerie())
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM ARO_UD_APPART_VER_SERIE relSerie WHERE relSerie.ID_UNITA_DOC = u.ID_UNITA_DOC) ");

        // ========================================================================
        // APPLICAZIONE "SECCHIELLO" CLICCATO (Con Mutua Esclusività Perfetta!)
        // ========================================================================
        switch (colonnaCliccata) {
        case RAGGIUNTO:
            sql.append(" AND altre.ID_UNITA_DOC IS NULL AND NOT ").append(SQL_COND_CONFLITTO)
                    .append(" AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
            break;
        case NON_RAGGIUNTO:
            sql.append(" AND altre.ID_UNITA_DOC IS NULL AND NOT ").append(SQL_COND_CONFLITTO)
                    .append(" AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
            break;
        case SENZA_INDICAZIONE:
            sql.append(" AND altre.ID_UNITA_DOC IS NULL AND NOT ").append(SQL_COND_CONFLITTO)
                    .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
            break;
        case ILLIMITATA:
            sql.append(" AND altre.ID_UNITA_DOC IS NULL AND NOT ").append(SQL_COND_CONFLITTO)
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' ");
            break;
        case CONFLITTI:
            sql.append(" AND altre.ID_UNITA_DOC IS NULL AND ").append(SQL_COND_CONFLITTO)
                    .append(" ");
            break;
        case TOTALE:
            break;
        case IN_ALTRE_PROPOSTE:
            // Questo caso di solito ha un metodo dedicato, ma lo lasciamo per completezza
            break;
        }

        sql.append(" ORDER BY u.AA_KEY_UNITA_DOC DESC, u.CD_KEY_UNITA_DOC DESC");

        // VALORIZZAZIONE PARAMETRI
        Query q = getEntityManager().createNativeQuery(sql.toString());

        q.setParameter("idStrut", filtri.getIdStrut());
        q.setParameter("idPropCorrente", idPropCorrente.longValue());
        q.setParameter("tipologiaCliccata", tipologiaCliccata);

        if (filtri.getRegistro() != null)
            q.setParameter("registro", filtri.getRegistro());
        if (filtri.getAnno() != null)
            q.setParameter("anno", filtri.getAnno());
        if (filtri.getAnnoDa() != null)
            q.setParameter("annoDa", filtri.getAnnoDa());
        if (filtri.getAnnoA() != null)
            q.setParameter("annoA", filtri.getAnnoA());
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            q.setParameter("numeroUd", filtri.getNumeroUd());
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            q.setParameter("numeroDa", filtri.getNumeroDa());
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            q.setParameter("numeroA", filtri.getNumeroA());
        if (filtri.getDataUd() != null)
            q.setParameter("dataUd", filtri.getDataUd());
        if (filtri.getDataUdDa() != null)
            q.setParameter("dataUdDa", filtri.getDataUdDa());
        if (filtri.getDataUdA() != null)
            q.setParameter("dataUdA", filtri.getDataUdA());
        if (StringUtils.isNotBlank(filtri.getOggettoUd()))
            q.setParameter("oggettoUd", "%" + filtri.getOggettoUd().toUpperCase() + "%");
        if (filtri.getTipologiaUd() != null)
            q.setParameter("idTipoUd", filtri.getTipologiaUd());
        if (StringUtils.isNotBlank(filtri.getClassifica()))
            q.setParameter("classifica", filtri.getClassifica());
        if (filtri.getTempoConservazione() != null)
            q.setParameter("tempoConservazione", filtri.getTempoConservazione());

        return q.getResultList();
    }

    /**
     * Estrae i dettagli delle UD presenti in ALTRE proposte di scarto (escludendo la proposta
     * corrente). Ritorna anche il codice della proposta madre in cui la UD è inserita, il calcolo
     * dell'alert e l'indicazione di scartabilità (SI/NO).
     *
     * @param filtri            DTO con i filtri di ricerca
     * @param tipologiaCliccata Tipologia UD cliccata nel report
     * @param idPropCorrente    ID della proposta attualmente aperta (da escludere)
     * @return Lista di array di Object (Risultato query nativa)
     */
    public List<Object[]> getDettaglioUdInAltreProposteNative(FiltriRicercaUdScartoDto filtri,
            String tipologiaCliccata, Long idPropCorrente) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ").append("    u.ID_UNITA_DOC, ") // Indice 0
                .append("    t.NM_TIPO_UNITA_DOC as tipologia_ud, ") // Indice 1
                .append("    u.CD_REGISTRO_KEY_UNITA_DOC as registro, ") // Indice 2
                .append("    u.AA_KEY_UNITA_DOC as anno, ") // Indice 3
                .append("    u.CD_KEY_UNITA_DOC as numero, ") // Indice 4

                // QUI C'E' LA COLONNA IN PIU' (Codice della proposta "Madre")
                .append("    (LPAD (TO_CHAR(prop_padre.pg_prop_scarto_vers), 10, '0') || '/' || TO_CHAR(prop_padre.aa_prop_scarto_vers)) as codice_proposta, ") // Indice
                                                                                                                                                                // 5

                .append("    TRIM( ").append("        ").append(SQL_MSG_CONFLITTO).append(" || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'Tenuta illimitata. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'Tempo conservazione non raggiunto. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 'Senza indicazione. ' ELSE '' END ")
                // .append(" CASE WHEN fasc.ID_FASCICOLO IS NOT NULL AND (fasc.FL_CONSERV_ILLIMITATA
                // = '1' OR (EXTRACT(YEAR FROM SYSDATE) - fasc.AA_FASCICOLO) < fasc.NI_AA_CONSERV)
                // THEN 'Inclusa in fascicolo non scartabile. ' ELSE '' END ")
                .append("    ) as ds_alert_scarto, ") // Indice 6

                .append("    CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'SI' ELSE 'NO' END as fl_scartabile ") // Indice
                                                                                                                                                                                                                                                    // 7

                .append("FROM ARO_UNITA_DOC u ")
                .append("JOIN DEC_TIPO_UNITA_DOC t ON u.ID_TIPO_UNITA_DOC = t.ID_TIPO_UNITA_DOC ")
                .append("LEFT JOIN DEC_REGISTRO_UNITA_DOC r ON u.ID_REGISTRO_UNITA_DOC = r.ID_REGISTRO_UNITA_DOC ")

                // JOIN PER RISALIRE ALLE ALTRE PROPOSTE
                .append("JOIN ARO_ITEM_PROP_SCARTO_VERS i_altre ON u.ID_UNITA_DOC = i_altre.ID_UNITA_DOC ")
                .append("JOIN ARO_PROP_SCARTO_VERS prop_padre ON i_altre.ID_PROP_SCARTO_VERS = prop_padre.ID_PROP_SCARTO_VERS ");

        // JOIN PER I FASCICOLI
        // .append("LEFT JOIN FAS_UNITA_DOC_FASCICOLO rel_fasc ON u.ID_UNITA_DOC =
        // rel_fasc.ID_UNITA_DOC ")
        // .append("LEFT JOIN FAS_FASCICOLO fasc ON rel_fasc.ID_FASCICOLO = fasc.ID_FASCICOLO ");

        // WHERE DI BASE
        sql.append("WHERE u.ID_STRUT = :idStrut ")
                .append("  AND u.TI_STATO_CONSERVAZIONE <> 'ANNULLATA' ")
                .append("  AND t.NM_TIPO_UNITA_DOC = :tipologiaCliccata ")
                .append("  AND i_altre.ID_PROP_SCARTO_VERS <> :idPropCorrente ") // Fondamentale:
                                                                                 // esclude la
                                                                                 // proposta a cui
                                                                                 // stiamo lavorando
                .append("  AND i_altre.TI_ITEM_PROP_SCARTO_VERS = 'UNI_DOC' ");

        // ==============================================================
        // APPLICAZIONE DINAMICA DEI FILTRI (Gli stessi del report)
        // ==============================================================
        if (filtri.getRegistro() != null)
            sql.append(" AND u.ID_REGISTRO_UNITA_DOC = :registro ");
        if (filtri.getAnno() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC = :anno ");
        if (filtri.getAnnoDa() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC >= :annoDa ");
        if (filtri.getAnnoA() != null)
            sql.append(" AND u.AA_KEY_UNITA_DOC <= :annoA ");
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            sql.append(" AND u.CD_KEY_UNITA_DOC = :numeroUd ");
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            sql.append(" AND u.CD_KEY_UNITA_DOC >= :numeroDa ");
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            sql.append(" AND u.CD_KEY_UNITA_DOC <= :numeroA ");
        if (filtri.getDataUd() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) = :dataUd ");
        if (filtri.getDataUdDa() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) >= :dataUdDa ");
        if (filtri.getDataUdA() != null)
            sql.append(" AND TRUNC(u.DT_REG_UNITA_DOC) <= :dataUdA ");
        if (StringUtils.isNotBlank(filtri.getOggettoUd()))
            sql.append(" AND UPPER(u.DL_OGGETTO_UNITA_DOC) LIKE :oggettoUd ");
        if (filtri.getTipologiaUd() != null)
            sql.append(" AND t.ID_TIPO_UNITA_DOC = :idTipoUd ");
        if (StringUtils.isNotBlank(filtri.getClassifica()))
            sql.append(" AND u.DS_CLASSIF_PRINC = :classifica ");
        if (filtri.getTempoConservazione() != null)
            sql.append(" AND COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) = :tempoConservazione ");

        if ("SI".equals(filtri.getIllimitato())) {
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' ");
        } else if ("NO".equals(filtri.getIllimitato())) {
            // Esclude quelle illimitate
            sql.append(
                    " AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }

        // --- FILTRO: TEMPO SUPERATO ---
        if ("SI".equals(filtri.getTempoSuperato())) {
            // UD pronte per lo scarto
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        } else if ("NO".equals(filtri.getTempoSuperato())) {
            // UD "giovani", tempo non ancora raggiunto (escludendo sempre le illimitate e quelle
            // senza indicazione)
            sql.append(
                    " AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) ")
                    .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' ");
        }

        // Esclusione/Inclusione da Fascicoli e Serie
        if (!filtri.isIncludiFascicoli())
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM FAS_UNITA_DOC_FASCICOLO relFasc WHERE relFasc.ID_UNITA_DOC = u.ID_UNITA_DOC) ");
        if (!filtri.isIncludiSerie())
            sql.append(
                    " AND NOT EXISTS (SELECT 1 FROM ARO_UD_APPART_VER_SERIE relSerie WHERE relSerie.ID_UNITA_DOC = u.ID_UNITA_DOC) ");

        // ORDINAMENTO
        sql.append(" ORDER BY u.AA_KEY_UNITA_DOC DESC, u.CD_KEY_UNITA_DOC DESC");

        // ==============================================================
        // VALORIZZAZIONE DEI PARAMETRI
        // ==============================================================
        Query q = getEntityManager().createNativeQuery(sql.toString());

        // Parametri Base
        q.setParameter("idStrut", filtri.getIdStrut());
        q.setParameter("tipologiaCliccata", tipologiaCliccata);
        q.setParameter("idPropCorrente", idPropCorrente);

        // Parametri Filtri
        if (filtri.getRegistro() != null)
            q.setParameter("registro", filtri.getRegistro());
        if (filtri.getAnno() != null)
            q.setParameter("anno", filtri.getAnno());
        if (filtri.getAnnoDa() != null)
            q.setParameter("annoDa", filtri.getAnnoDa());
        if (filtri.getAnnoA() != null)
            q.setParameter("annoA", filtri.getAnnoA());
        if (StringUtils.isNotBlank(filtri.getNumeroUd()))
            q.setParameter("numeroUd", filtri.getNumeroUd());
        if (StringUtils.isNotBlank(filtri.getNumeroDa()))
            q.setParameter("numeroDa", filtri.getNumeroDa());
        if (StringUtils.isNotBlank(filtri.getNumeroA()))
            q.setParameter("numeroA", filtri.getNumeroA());
        if (filtri.getDataUd() != null)
            q.setParameter("dataUd", filtri.getDataUd());
        if (filtri.getDataUdDa() != null)
            q.setParameter("dataUdDa", filtri.getDataUdDa());
        if (filtri.getDataUdA() != null)
            q.setParameter("dataUdA", filtri.getDataUdA());
        if (StringUtils.isNotBlank(filtri.getOggettoUd()))
            q.setParameter("oggettoUd", "%" + filtri.getOggettoUd().toUpperCase() + "%");
        if (filtri.getTipologiaUd() != null)
            q.setParameter("idTipoUd", filtri.getTipologiaUd());
        if (StringUtils.isNotBlank(filtri.getClassifica()))
            q.setParameter("classifica", filtri.getClassifica());
        if (filtri.getTempoConservazione() != null)
            q.setParameter("tempoConservazione", filtri.getTempoConservazione());

        return q.getResultList();
    }

    public List<AroItemPropScartoVers> getAroItemPropScartoVers(long idPropScartoVers,
            String tiItemPropScartoVers) {
        Query q = getEntityManager().createQuery(
                "SELECT itemProp FROM AroItemPropScartoVers itemProp WHERE itemProp.aroPropScartoVers.idPropScartoVers = :idPropScartoVers AND itemProp.tiItemPropScartoVers = :tiItemPropScartoVers ");
        q.setParameter("idPropScartoVers", idPropScartoVers);
        q.setParameter("tiItemPropScartoVers", tiItemPropScartoVers);

        return (List<AroItemPropScartoVers>) q.getResultList();

    }

    /**
     * Conta il numero di item di un certo tipo associati alla proposta
     *
     * @param idPropScartoVers     id proposta
     * @param tiItemPropScartoVers tipo item proposta
     * @return numero item per tipo
     */
    public Long countItemsByTipo(long idPropScartoVers, String tiItemPropScartoVers) {
        Query q = getEntityManager().createQuery("SELECT COUNT(i) FROM AroItemPropScartoVers i "
                + "WHERE i.aroPropScartoVers.idPropScartoVers = :idPropScartoVers "
                + "AND i.tiItemPropScartoVers = :tiItemPropScartoVers");
        q.setParameter("idPropScartoVers", idPropScartoVers);
        q.setParameter("tiItemPropScartoVers", tiItemPropScartoVers);
        return (Long) q.getSingleResult();
    }

    /**
     * Ritorna il valore massimo di PG_ITEM attualmente presente per una proposta. Serve per
     * assegnare un nuovo progressivo univoco agli item in fase di inserimento.
     *
     * @param idPropScartoVers id proposta
     * @return numero massimo progressivo item
     */
    public int getMaxPgItem(long idPropScartoVers) {
        Query q = getEntityManager().createQuery(
                "SELECT MAX(i.pgItem) FROM AroItemPropScartoVers i WHERE i.aroPropScartoVers.idPropScartoVers = :idPropScartoVers");
        q.setParameter("idPropScartoVers", idPropScartoVers);

        BigDecimal maxPg = (BigDecimal) q.getSingleResult();
        return (maxPg != null) ? maxPg.intValue() : 0;
    }

    /**
     * Ritorna la lista degli ID delle UD che sono già presenti nella proposta. Serve per filtrare a
     * monte gli inserimenti.
     *
     * @param idPropScartoVers  id proposta
     * @param idUdDaControllare id ud da controllare se già in proposta
     * @return lista ud già presenti in proposta
     */
    public List<Long> getUdGiaInProposta(Long idPropScartoVers, List<Long> idUdDaControllare) {
        if (idUdDaControllare == null || idUdDaControllare.isEmpty())
            return new ArrayList<>();

        Query q = getEntityManager()
                .createQuery("SELECT i.aroUnitaDoc.idUnitaDoc FROM AroItemPropScartoVers i "
                        + "WHERE i.aroPropScartoVers.idPropScartoVers = :idPropScartoVers "
                        + "AND i.tiItemPropScartoVers = 'UNI_DOC' "
                        + "AND i.aroUnitaDoc.idUnitaDoc IN (:idUdDaControllare)");

        q.setParameter("idPropScartoVers", idPropScartoVers);
        q.setParameter("idUdDaControllare", idUdDaControllare);
        return q.getResultList();
    }

    /**
     * Estrae le UD già salvate in una proposta ricalcolando dinamicamente gli Alert in base alla
     * data odierna e alle regole attuali di conservazione.
     *
     * @param idPropScartoVers id proposta
     * @return lista ud già salvate in proposta
     */
    public List<Object[]> getUdSalvateConAlertNative(Long idPropScartoVers) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ").append("    i.ID_ITEM_PROP_SCARTO_VERS, ") // Indice 0
                .append("    u.ID_UNITA_DOC, ") // Indice 1
                .append("    t.NM_TIPO_UNITA_DOC as tipologia_ud, ") // Indice 2 (Nuovo campo)
                .append("    u.CD_REGISTRO_KEY_UNITA_DOC as registro, ") // Indice 3
                .append("    u.AA_KEY_UNITA_DOC as anno, ") // Indice 4
                .append("    u.CD_KEY_UNITA_DOC as numero, ") // Indice 5

                .append("    TRIM( ").append("        ").append(SQL_MSG_CONFLITTO).append(" || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'Tenuta illimitata. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'Tempo conservazione non raggiunto. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 'Senza indicazione. ' ELSE '' END ")
                // .append(" CASE WHEN fasc.ID_FASCICOLO IS NOT NULL AND (fasc.FL_CONSERV_ILLIMITATA
                // = '1' OR (EXTRACT(YEAR FROM SYSDATE) - fasc.AA_FASCICOLO) < fasc.NI_AA_CONSERV)
                // THEN 'Inclusa in fascicolo non scartabile. ' ELSE '' END ")
                .append("    ) as ds_alert_scarto, ") // Indice 6

                .append("    CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'SI' ELSE 'NO' END as fl_scartabile ") // Indice
                                                                                                                                                                                                                                                    // 7

                .append("FROM ARO_ITEM_PROP_SCARTO_VERS i ")
                .append("JOIN ARO_UNITA_DOC u ON i.ID_UNITA_DOC = u.ID_UNITA_DOC ")
                .append("JOIN DEC_TIPO_UNITA_DOC t ON u.ID_TIPO_UNITA_DOC = t.ID_TIPO_UNITA_DOC ")
                .append("LEFT JOIN DEC_REGISTRO_UNITA_DOC r ON u.ID_REGISTRO_UNITA_DOC = r.ID_REGISTRO_UNITA_DOC ")
                .append("LEFT JOIN FAS_UNITA_DOC_FASCICOLO rel_fasc ON u.ID_UNITA_DOC = rel_fasc.ID_UNITA_DOC ")
                .append("LEFT JOIN FAS_FASCICOLO fasc ON rel_fasc.ID_FASCICOLO = fasc.ID_FASCICOLO ")

                .append("WHERE i.ID_PROP_SCARTO_VERS = :idProp ")
                .append("  AND i.TI_ITEM_PROP_SCARTO_VERS = 'UNI_DOC' ") // Sostituisci con
                                                                         // CostantiDB.TiItemPropScartoVers.UNI_DOC.name()
                                                                         // se preferisci

                .append("ORDER BY i.PG_ITEM ASC ");

        Query q = getEntityManager().createNativeQuery(sql.toString());
        q.setParameter("idProp", idPropScartoVers);

        return q.getResultList();
    }

    public boolean isPropostaDeletable(BigDecimal idPropScartoVers) {
        String queryStr = "SELECT stato FROM AroStatoPropScartoVers stato WHERE stato.aroPropScartoVers.idPropScartoVers = :idPropScartoVers "
                + "AND stato.pgStatoPropScartoVers = (SELECT MAX(maxStati.pgStatoPropScartoVers) FROM AroStatoPropScartoVers maxStati WHERE maxStati.aroPropScartoVers.idPropScartoVers = stato.aroPropScartoVers.idPropScartoVers) "
                + "AND stato.tiStatoPropScartoVers = :tiStatoPropScartoVers ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idPropScartoVers", longFromBigDecimal(idPropScartoVers));
        query.setParameter("tiStatoPropScartoVers", CostantiDB.TiStatoPropScartoVers.APERTA.name());
        return !query.getResultList().isEmpty();
    }

    public AroVRicPropScartoVers getAroVRicPropScartoVersById(BigDecimal idPropScartoVers,
            long idUserIam) {
        Query query = getEntityManager().createQuery(
                "SELECT prop FROM AroVRicPropScartoVers prop WHERE prop.aroVRicPropScartoVersId.idPropScartoVers = :idPropScartoVers AND prop.aroVRicPropScartoVersId.idUserIam = :idUserIam ");
        query.setParameter("idPropScartoVers", idPropScartoVers);
        query.setParameter("idUserIam", BigDecimal.valueOf(idUserIam));

        List<AroVRicPropScartoVers> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * Data una lista di ID Unità Documentarie, restituisce un Set contenente solo gli ID che sono
     * già presenti in QUALSIASI proposta di scarto nel database.
     *
     * @param idUdDaControllare id unita doc da controllare
     * @return set bonificato delle ud
     */
    public Set<Long> getUdGiaInAltreProposte(List<Long> idUdDaControllare) {
        Set<Long> udTrovate = new HashSet<>();

        if (idUdDaControllare == null || idUdDaControllare.isEmpty()) {
            return udTrovate;
        }

        // Partiziono la lista per evitare l'errore Oracle IN (> 1000)
        int batchSize = 900;
        for (int i = 0; i < idUdDaControllare.size(); i += batchSize) {
            List<Long> subList = idUdDaControllare.subList(i,
                    Math.min(i + batchSize, idUdDaControllare.size()));

            Query q = getEntityManager()
                    .createQuery("SELECT i.aroUnitaDoc.idUnitaDoc FROM AroItemPropScartoVers i "
                            + "WHERE i.tiItemPropScartoVers = 'UNI_DOC' "
                            + "AND i.aroUnitaDoc.idUnitaDoc IN :listaId");

            q.setParameter("listaId", subList);
            udTrovate.addAll(q.getResultList());
        }

        return udTrovate;
    }

    /**
     * Restituisce un Set contenente gli ID delle UD che sono già presenti in ALTRE proposte di
     * scarto.
     *
     * @param idPropScartoVersCorrente la proposta di scarto corrente
     * @param idUdDaControllare        la lista delle ud da controllare
     * @return l'insieme delle ud già presenti in altre proposte
     */
    public Set<Long> getUdGiaInAltreProposte(Long idPropScartoVersCorrente,
            List<Long> idUdDaControllare) {
        Set<Long> udTrovate = new HashSet<>();

        if (idUdDaControllare == null || idUdDaControllare.isEmpty()) {
            return udTrovate;
        }

        int batchSize = 900;
        for (int i = 0; i < idUdDaControllare.size(); i += batchSize) {
            List<Long> subList = idUdDaControllare.subList(i,
                    Math.min(i + batchSize, idUdDaControllare.size()));

            Query q = getEntityManager()
                    .createQuery("SELECT i.aroUnitaDoc.idUnitaDoc FROM AroItemPropScartoVers i "
                            + "WHERE i.tiItemPropScartoVers = 'UNI_DOC' "
                            + "AND i.aroPropScartoVers.idPropScartoVers <> :idPropCorrente " + // ESCLUDE
                                                                                               // LA
                                                                                               // PROPOSTA
                                                                                               // ATTUALE
                            "AND i.aroUnitaDoc.idUnitaDoc IN :listaId");

            q.setParameter("idPropCorrente", idPropScartoVersCorrente);
            q.setParameter("listaId", subList);

            udTrovate.addAll(q.getResultList());
        }

        return udTrovate;
    }

    /**
     * Esegue la cancellazione fisica (DELETE) massiva degli item UD dalla proposta.
     *
     * @param idProp L'id della proposta di scarto
     * @param idUds  La lista degli ID delle unità documentarie da rimuovere
     */
    public void deleteItemUdDaProposta(Long idProp, List<Long> idUds) {
        if (idUds == null || idUds.isEmpty()) {
            return;
        }

        // Partiziono la lista per evitare l'errore Oracle IN (> 1000)
        int batchSize = 900;
        for (int i = 0; i < idUds.size(); i += batchSize) {
            List<Long> subList = idUds.subList(i, Math.min(i + batchSize, idUds.size()));

            Query q = getEntityManager().createQuery("DELETE FROM AroItemPropScartoVers i "
                    + "WHERE i.aroPropScartoVers.idPropScartoVers = :idProp "
                    + "AND i.tiItemPropScartoVers = 'UNI_DOC' "
                    + "AND i.aroUnitaDoc.idUnitaDoc IN :idUds");

            q.setParameter("idProp", idProp);
            q.setParameter("idUds", subList);

            q.executeUpdate();
        }
    }

    /**
     * Estrae le UD già salvate in una proposta, applicando eventuali filtri di ricerca interni
     *
     * @param idPropScartoVers id della proposta
     * @param idTipoUd         tipo ud
     * @param idRegistro       registro
     * @param anno             anno
     * @param annoDa           anno da
     * @param annoA            anno a
     * @param numero           numero
     * @param numeroDa         numero da
     * @param numeroA          numero a
     * @param flScartabile     scartabile
     * @param dsAlertTesto     alert
     * @return lista delle ud
     */
    public List<Object[]> getUdSalvateConAlertNative(Long idPropScartoVers, BigDecimal idTipoUd,
            BigDecimal idRegistro, BigDecimal anno, BigDecimal annoDa, BigDecimal annoA,
            String numero, String numeroDa, String numeroA, String flScartabile,
            String dsAlertTesto) {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM ( ");

        sql.append("SELECT ").append("    i.ID_ITEM_PROP_SCARTO_VERS, ")
                .append("    u.ID_UNITA_DOC, ").append("    t.NM_TIPO_UNITA_DOC as tipologia_ud, ")
                .append("    u.CD_REGISTRO_KEY_UNITA_DOC as registro, ")
                .append("    u.AA_KEY_UNITA_DOC as anno, ")
                .append("    u.CD_KEY_UNITA_DOC as numero, ")

                .append("    TRIM( ").append("        ").append(SQL_MSG_CONFLITTO).append(" || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '1' THEN 'Tenuta illimitata. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) < COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'Tempo conservazione non raggiunto. ' ELSE '' END || ")
                .append("        CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND t.NI_AA_CONSERV IS NULL AND r.NI_AA_CONSERV IS NULL AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' THEN 'Senza indicazione. ' ELSE '' END ")
                // .append(" CASE WHEN fasc.ID_FASCICOLO IS NOT NULL AND (fasc.FL_CONSERV_ILLIMITATA
                // = '1' OR (EXTRACT(YEAR FROM SYSDATE) - fasc.AA_FASCICOLO) < fasc.NI_AA_CONSERV)
                // THEN 'Inclusa in fascicolo non scartabile. ' ELSE '' END ")
                .append("    ) as ds_alert_scarto, ")

                .append("    CASE WHEN NOT ").append(SQL_COND_CONFLITTO)
                .append(" AND COALESCE(t.FL_CONSERV_ILLIMITATA, r.FL_CONSERV_ILLIMITATA, '0') = '0' AND (EXTRACT(YEAR FROM SYSDATE) - u.AA_KEY_UNITA_DOC) >= COALESCE(t.NI_AA_CONSERV, r.NI_AA_CONSERV) THEN 'SI' ELSE 'NO' END as fl_scartabile, ")

                .append("    i.PG_ITEM ")

                .append("FROM ARO_ITEM_PROP_SCARTO_VERS i ")
                .append("JOIN ARO_UNITA_DOC u ON i.ID_UNITA_DOC = u.ID_UNITA_DOC ")
                .append("JOIN DEC_TIPO_UNITA_DOC t ON u.ID_TIPO_UNITA_DOC = t.ID_TIPO_UNITA_DOC ")
                .append("LEFT JOIN DEC_REGISTRO_UNITA_DOC r ON u.ID_REGISTRO_UNITA_DOC = r.ID_REGISTRO_UNITA_DOC ")
                .append("LEFT JOIN FAS_UNITA_DOC_FASCICOLO rel_fasc ON u.ID_UNITA_DOC = rel_fasc.ID_UNITA_DOC ")
                .append("LEFT JOIN FAS_FASCICOLO fasc ON rel_fasc.ID_FASCICOLO = fasc.ID_FASCICOLO ")

                .append("WHERE i.ID_PROP_SCARTO_VERS = :idProp ")
                .append("  AND i.TI_ITEM_PROP_SCARTO_VERS = 'UNI_DOC' ");

        if (idTipoUd != null)
            sql.append(" AND t.ID_TIPO_UNITA_DOC = :idTipoUd ");
        if (idRegistro != null)
            sql.append(" AND u.ID_REGISTRO_UNITA_DOC = :idRegistro ");
        // Anno: singolo ha precedenza sul range
        if (anno != null) {
            sql.append(" AND u.AA_KEY_UNITA_DOC = :anno ");
        } else {
            if (annoDa != null)
                sql.append(" AND u.AA_KEY_UNITA_DOC >= :annoDa ");
            if (annoA != null)
                sql.append(" AND u.AA_KEY_UNITA_DOC <= :annoA ");
        }
        // Numero: singolo ha precedenza sul range
        if (StringUtils.isNotBlank(numero)) {
            sql.append(" AND u.CD_KEY_UNITA_DOC = :numero ");
        } else {
            if (StringUtils.isNotBlank(numeroDa))
                sql.append(" AND u.CD_KEY_UNITA_DOC >= :numeroDa ");
            if (StringUtils.isNotBlank(numeroA))
                sql.append(" AND u.CD_KEY_UNITA_DOC <= :numeroA ");
        }

        sql.append(") result_set ");

        sql.append("WHERE 1=1 ");

        if (StringUtils.isNotBlank(flScartabile)) {
            sql.append(" AND result_set.fl_scartabile = :flScartabile ");
        }
        if (StringUtils.isNotBlank(dsAlertTesto)) {
            sql.append(" AND UPPER(result_set.ds_alert_scarto) LIKE UPPER(:dsAlert) ");
        }

        sql.append("ORDER BY result_set.PG_ITEM ASC ");

        Query q = getEntityManager().createNativeQuery(sql.toString());
        q.setParameter("idProp", idPropScartoVers);

        if (idTipoUd != null)
            q.setParameter("idTipoUd", idTipoUd.longValue());
        if (idRegistro != null)
            q.setParameter("idRegistro", idRegistro.longValue());
        if (anno != null) {
            q.setParameter("anno", anno);
        } else {
            if (annoDa != null)
                q.setParameter("annoDa", annoDa);
            if (annoA != null)
                q.setParameter("annoA", annoA);
        }
        if (StringUtils.isNotBlank(numero)) {
            q.setParameter("numero", numero);
        } else {
            if (StringUtils.isNotBlank(numeroDa))
                q.setParameter("numeroDa", numeroDa);
            if (StringUtils.isNotBlank(numeroA))
                q.setParameter("numeroA", numeroA);
        }
        if (StringUtils.isNotBlank(flScartabile))
            q.setParameter("flScartabile", flScartabile);
        if (StringUtils.isNotBlank(dsAlertTesto))
            q.setParameter("dsAlert", "%" + dsAlertTesto + "%");

        return q.getResultList();
    }

    /**
     * Cancella fisicamente tutti gli item (di qualunque tipo) di una proposta di scarto. Utilizzato
     * prima della cancellazione della proposta stessa quando lo stato è DA_AUTORIZZARE.
     *
     * @param idPropScartoVers L'id della proposta di scarto
     */
    public void deleteAllItemsDaProposta(Long idPropScartoVers) {
        getEntityManager()
                .createQuery("DELETE FROM AroItemPropScartoVers i "
                        + "WHERE i.aroPropScartoVers.idPropScartoVers = :idProp")
                .setParameter("idProp", idPropScartoVers)
                .executeUpdate();
    }

    /**
     * Cancella fisicamente tutti gli stati di una proposta di scarto. Utilizzato prima della
     * cancellazione della proposta stessa quando lo stato è DA_AUTORIZZARE.
     *
     * @param idPropScartoVers L'id della proposta di scarto
     */
    public void deleteAllStatiDaProposta(Long idPropScartoVers) {
        getEntityManager()
                .createQuery("DELETE FROM AroStatoPropScartoVers s "
                        + "WHERE s.aroPropScartoVers.idPropScartoVers = :idProp")
                .setParameter("idProp", idPropScartoVers)
                .executeUpdate();
    }
}
