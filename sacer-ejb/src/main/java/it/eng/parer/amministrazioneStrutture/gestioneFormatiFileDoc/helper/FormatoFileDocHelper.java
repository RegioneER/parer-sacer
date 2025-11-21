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

package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.DecFormatoFileAmmesso;
import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.helper.GenericHelper;

/**
 * Helper dei formati file doc per le strutture
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class FormatoFileDocHelper extends GenericHelper {

    /**
     * Recupera i formati file doc per la struttura, legati al formato file standard
     *
     * @param idStrut     struttura di riferimento
     * @param filterValid true se devono essere recuperati solo quelli validi in data odierna
     *
     * @return DecFormatoFileDocTableBean
     */
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT formatoFileDoc FROM DecUsoFormatoFileStandard uso "
                        + "JOIN uso.decFormatoFileDoc formatoFileDoc "
                        + "JOIN formatoFileDoc.orgStrut strut ");
        String clause = " WHERE ";

        if (idStrut != null) {
            queryStr.append(clause).append("strut.idStrut = :idStrut ");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause).append(
                    "formatoFileDoc.dtIstituz <= :filterDate AND formatoFileDoc.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut.longValue());
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();
    }

    /**
     * Recupera i formati file doc per la struttura
     *
     * @param idStrut struttura di riferimento
     *
     * @return DecFormatoFileDocTableBean
     */
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM DecFormatoFileDoc u WHERE u.orgStrut.idStrut = :idstrut");
        query.setParameter("idstrut", longFromBigDecimal(idStrut));
        return query.getResultList();
    }

    public DecFormatoFileDoc getDecFormatoFileDocByName(String nmFormatoFileDoc,
            BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc WHERE formatoFileDoc.nmFormatoFileDoc = :nmFormatoFileDoc");

        if (idStrut != null) {
            queryStr.append(" AND formatoFileDoc.orgStrut.idStrut=:idStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        query.setParameter("nmFormatoFileDoc", nmFormatoFileDoc);
        List<DecFormatoFileDoc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public DecFormatoFileAmmesso getDecFormatoFileAmmessoByName(String nmFormatoFileDoc,
            String nmTipoCompDoc, String nmTipoStrutDoc, BigDecimal idStrut) {
        String queryStr = "SELECT formatoFileAmmesso FROM DecFormatoFileAmmesso formatoFileAmmesso "
                + "WHERE formatoFileAmmesso.decFormatoFileDoc.nmFormatoFileDoc = :nmFormatoFileDoc "
                + "AND formatoFileAmmesso.decFormatoFileDoc.orgStrut.idStrut = :idStrut "
                + "AND formatoFileAmmesso.decTipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc "
                + "AND formatoFileAmmesso.decTipoCompDoc.decTipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc "
                + "AND formatoFileAmmesso.decTipoCompDoc.decTipoStrutDoc.orgStrut.idStrut = :idStrut ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
        query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        query.setParameter("nmFormatoFileDoc", nmFormatoFileDoc);
        List<DecFormatoFileAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<DecFormatoFileDoc> getDecFormatoFileDoc(BigDecimal idStrut) {
        String queryStr = "SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc WHERE formatoFileDoc.orgStrut.idStrut=:idStrut";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return query.getResultList();
    }

    public List<DecFormatoFileDoc> getDecFormatoFileDocListSingoliContenabili(BigDecimal idStrut) {
        String queryStr = "SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc "
                + "JOIN formatoFileDoc.decUsoFormatoFileStandards uso "
                + "JOIN uso.decFormatoFileStandard standard "
                + "WHERE formatoFileDoc.orgStrut.idStrut = :idStrut "
                + "AND standard.flFormatoConcat = '1' " + "AND uso.niOrdUso = '1' ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return query.getResultList();
    }

    public List<DecFormatoFileDoc> getDecFormatoFileDocList() {
        String queryStr = "SELECT formatoFileDoc FROM DecFormatoFileDoc formatoFileDoc";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();
    }

    public int removeUsoFormatoFileStandardByFormatoFDoc(BigDecimal idFormatoFileDoc) {
        String queryStr = "DELETE FROM DecUsoFormatoFileStandard uffs "
                + " WHERE  uffs.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", longFromBigDecimal(idFormatoFileDoc));
        return query.executeUpdate();
    }

    public BigDecimal getUsoFormatoFileStandardMaxNrOrder(BigDecimal idFormatoFileDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(usoFormatoFileStandard.niOrdUso) FROM DecUsoFormatoFileStandard usoFormatoFileStandard "
                        + "WHERE usoFormatoFileStandard.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc");
        query.setParameter("idFormatoFileDoc", longFromBigDecimal(idFormatoFileDoc));

        BigDecimal nrOrd = (BigDecimal) query.getSingleResult();
        if (nrOrd == null) {
            nrOrd = BigDecimal.ZERO;
        }

        return nrOrd;
    }

    public List<Object[]> getDecFormatoFileAmmessoList(BigDecimal idTipoCompDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formatoFileAmmesso.idFormatoFileAmmesso, formatoFileDoc FROM DecFormatoFileAmmesso formatoFileAmmesso JOIN formatoFileAmmesso.decFormatoFileDoc formatoFileDoc ");

        if (idTipoCompDoc != null) {
            queryStr.append(
                    "WHERE formatoFileAmmesso.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        }
        return query.getResultList();
    }

    public List<DecFormatoFileDoc> getDecFormatoFileAmmessoNotInList(Set<String> formati,
            BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formato FROM DecFormatoFileDoc formato WHERE formato.orgStrut.idStrut = :strut ");

        if (!formati.isEmpty()) {
            queryStr.append("AND UPPER(formato.nmFormatoFileDoc) NOT IN (:nmformati)");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("strut", longFromBigDecimal(idStrut));
        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formati);
        }
        return query.getResultList();
    }

    public List<DecFormatoFileDoc> getAllDecFormatoFileSuStrut(BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formato FROM DecFormatoFileDoc formato WHERE formato.orgStrut.idStrut = :strut ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("strut", longFromBigDecimal(idStrut));
        return query.getResultList();
    }

    /* Restituisce i formati file per la struttura e per i nomi dei formati passati */
    // FIXMEPLEASE: Metodo curioso dato che prende una lista a cui viene passato sempre un solo
    // elemento
    public List<DecFormatoFileDoc> retrieveDecFormatoFileDocList(BigDecimal idStrut,
            List<String> formatoFileDocList) {
        if (!formatoFileDocList.isEmpty()) {
            Query q = getEntityManager().createQuery("SELECT u FROM DecFormatoFileDoc u "
                    + "WHERE u.nmFormatoFileDoc IN (:formatoFileDocList) "
                    + "AND u.orgStrut.idStrut = :idStrut ");
            q.setParameter("formatoFileDocList", formatoFileDocList);
            q.setParameter("idStrut", longFromBigDecimal(idStrut));
            return q.getResultList();
        }
        return null;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDoc(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  "
                + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select p from AroCompDoc p where p.decFormatoFileDoc.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDocCont(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  "
                + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select q from DecTipoRapprComp q where q.decFormatoFileDocCont.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean checkRelationsAreEmptyForDecFormatoFileDocConv(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  "
                + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND NOT EXISTS (select q from DecTipoRapprComp q where q.decFormatoFileDocConv.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean existsFormatoSpecificoAmmesso(long idFormatoFileDoc) {
        boolean result = true;

        String queryStr = " select a from DecFormatoFileDoc a  "
                + " where a.idFormatoFileDoc=:idFormatoFileDoc "
                + " AND EXISTS (select q from DecFormatoFileAmmesso q where q.decFormatoFileDoc.idFormatoFileDoc=a.idFormatoFileDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        List<Object[]> list = query.getResultList();
        result = list.size() > 0;
        return result;
    }

    public void deleteDecFormatoFileDocList(BigDecimal idStrut, List<String> formatoFileDocList) {
        if (!formatoFileDocList.isEmpty()) {
            Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileDoc u "
                    + "WHERE u.nmFormatoFileDoc IN (:formatoFileDocList) "
                    + "AND u.orgStrut.idStrut = :idStrut ");
            q.setParameter("formatoFileDocList", formatoFileDocList);
            q.setParameter("idStrut", longFromBigDecimal(idStrut));
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    // TODO: Di questa query non è molto chiara l'utilità
    public List<DecFormatoFileDoc> retrieveFormatoFileDocNiOrdUsoTB(BigDecimal idStruttura) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT u FROM DecUsoFormatoFileStandard uffs JOIN uffs.decFormatoFileDoc u WHERE uffs.niOrdUso=1 AND u.orgStrut.idStrut = :idstrut ORDER BY u.nmFormatoFileDoc");
        query.setParameter("idstrut", longFromBigDecimal(idStruttura));
        return query.getResultList();
    }

    public String findNmFormatoFileDocById(Long idFormatoFileDoc) {
        TypedQuery<String> query = getEntityManager().createQuery(
                "SELECT d.nmFormatoFileDoc FROM DecFormatoFileDoc d WHERE d.idFormatoFileDoc=:idFormatoFileDoc",
                String.class);
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        return query.getSingleResult();
    }///

    public void deleteDecFormatoFileAmmesso(BigDecimal idTipoCompDoc, List<Long> idFormatoFileDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileAmmesso u "
                + "WHERE u.decFormatoFileDoc.idFormatoFileDoc IN (:idFormatoFileDoc) "
                + "AND u.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
        q.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        q.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<Long> getDecFormatoFileAmmessoGestiti(BigDecimal idTipoCompDoc) {
        Query q = getEntityManager().createQuery("SELECT formatoFileAmmesso.idFormatoFileAmmesso "
                + "FROM DecFormatoFileAmmesso formatoFileAmmesso "
                + "JOIN formatoFileAmmesso.decFormatoFileDoc formatoFileDoc "
                + "JOIN formatoFileDoc.decUsoFormatoFileStandards uso "
                + "JOIN uso.decFormatoFileStandard formatoFileStandard "
                + "WHERE formatoFileStandard.tiEsitoContrFormato = 'GESTITO' "
                + "AND formatoFileAmmesso.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
        q.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        return (List<Long>) q.getResultList();
    }

    public void deleteDecFormatoFileAmmesso(List<Long> idFormatoFileAmmesso) {
        Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileAmmesso u "
                + "WHERE u.idFormatoFileAmmesso IN (:idFormatoFileAmmesso) ");
        q.setParameter("idFormatoFileAmmesso", idFormatoFileAmmesso);
        q.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteDecFormatoFileDoc(List<Long> idFormatoFileDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM DecFormatoFileDoc u "
                + "WHERE u.idFormatoFileDoc IN (:idFormatoFileDoc) ");
        q.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        q.executeUpdate();
        getEntityManager().flush();
    }

    private static final String FORMATI_STRUTTURA_SPECIFICA = "INSERT /*+ append */ INTO DEC_FORMATO_FILE_DOC (ID_FORMATO_FILE_DOC,ID_STRUT,NM_FORMATO_FILE_DOC,DS_FORMATO_FILE_DOC,CD_VERSIONE,DT_ISTITUZ,DT_SOPPRES) "
            + "  (SELECT sdec_formato_file_doc.nextval, tmp.id_strut, tmp.nm_formato_file_doc, tmp.ds_formato_file_doc, tmp.cd_versione, tmp.dt_istituz, tmp.dt_soppres FROM( "
            + "SELECT :idStrut as id_strut, std.nm_formato_file_standard as nm_formato_file_doc, "
            + "std.ds_formato_file_standard as ds_formato_file_doc, std.cd_versione, to_date('01-GEN-11','DD-MON-RR')as dt_istituz,to_date('31-DIC-44','DD-MON-RR') as dt_soppres "
            + "FROM SACER.DEC_FORMATO_FILE_STANDARD std " + "UNION "
            + "SELECT :idStrut as id_strut, standard1.nm_formato_file_standard || '.' || standard2.nm_formato_file_standard as nm_formato_file_doc, "
            + "standard1.ds_formato_file_standard as ds_formato_file_doc, standard1.cd_versione, to_date('01-GEN-11','DD-MON-RR') as dt_istituz,to_date('31-DIC-44','DD-MON-RR') as dt_soppres "
            + "FROM SACER.DEC_FORMATO_FILE_STANDARD standard1, SACER.DEC_FORMATO_FILE_STANDARD standard2 "
            + "WHERE standard1.fl_formato_concat = '0' "
            + "AND standard2.fl_formato_concat = '1')tmp)";

    private static final String FORMATI_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI = "INSERT /*+ append */ INTO DEC_FORMATO_FILE_DOC (ID_FORMATO_FILE_DOC,ID_STRUT,NM_FORMATO_FILE_DOC,DS_FORMATO_FILE_DOC,CD_VERSIONE,DT_ISTITUZ,DT_SOPPRES) "
            + "  (SELECT sdec_formato_file_doc.nextval, tmp.id_strut, tmp.nm_formato_file_doc, tmp.ds_formato_file_doc, tmp.cd_versione, tmp.dt_istituz, tmp.dt_soppres FROM( "
            + "SELECT :idStrut as id_strut, std.nm_formato_file_standard as nm_formato_file_doc, "
            + "std.ds_formato_file_standard as ds_formato_file_doc, std.cd_versione, to_date('01-GEN-11','DD-MON-RR')as dt_istituz,to_date('31-DIC-44','DD-MON-RR') as dt_soppres "
            + "FROM SACER.DEC_FORMATO_FILE_STANDARD std " + "UNION "
            + "SELECT :idStrut as id_strut, standard1.nm_formato_file_standard || '.' || standard2.nm_formato_file_standard as nm_formato_file_doc, "
            + "standard1.ds_formato_file_standard as ds_formato_file_doc, standard1.cd_versione, to_date('01-GEN-11','DD-MON-RR') as dt_istituz,to_date('31-DIC-44','DD-MON-RR') as dt_soppres "
            + "FROM SACER.DEC_FORMATO_FILE_STANDARD standard1, SACER.DEC_FORMATO_FILE_STANDARD standard2 "
            + "WHERE standard1.fl_formato_concat = '0' "
            + "AND standard2.fl_formato_concat = '1')tmp WHERE NOT EXISTS (SELECT * FROM DEC_FORMATO_FILE_DOC formato2 WHERE formato2.nm_formato_file_doc = tmp.nm_formato_file_doc AND formato2.id_strut = tmp.id_strut)) ";

    private static final String USO_SINGOLO_STRUTTURA_SPECIFICA = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, "
            + "tab_a.id_formato_file_standard " + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + " CROSS JOIN ( " + "        SELECT " + "            * " + "        FROM "
            + "            dec_formato_file_standard     ) tab_a "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "and tab_a.nm_formato_file_standard = formatoFileDoc.nm_formato_file_doc";

    private static final String USO_SINGOLO_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, "
            + "tab_a.id_formato_file_standard " + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + " CROSS JOIN ( " + "        SELECT " + "            * " + "        FROM "
            + "            dec_formato_file_standard     ) tab_a "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "and tab_a.nm_formato_file_standard = formatoFileDoc.nm_formato_file_doc AND NOT EXISTS (SELECT * FROM DEC_USO_FORMATO_FILE_STANDARD uso2 WHERE uso2.id_formato_file_doc = formatoFileDoc.id_formato_file_doc AND uso2.ni_ord_uso = 1) ";

    private static final String USO_CONCAT1_STRUTTURA_SPECIFICA = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, "
            + "(select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, 1, INSTR(formatoFileDoc.nm_formato_file_doc, '.')-1)) as id_formato_file_standard "
            + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "AND (select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, 1, INSTR(formatoFileDoc.nm_formato_file_doc, '.')-1)) IS NOT NULL";

    private static final String USO_CONCAT1_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, "
            + "(select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, 1, INSTR(formatoFileDoc.nm_formato_file_doc, '.')-1)) as id_formato_file_standard "
            + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "AND (select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, 1, INSTR(formatoFileDoc.nm_formato_file_doc, '.')-1)) IS NOT NULL "
            + "AND NOT EXISTS (SELECT * FROM DEC_USO_FORMATO_FILE_STANDARD uso2 WHERE uso2.id_formato_file_doc = formatoFileDoc.id_formato_file_doc AND uso2.ni_ord_uso = 1) ";

    private static final String USO_CONCAT2_STRUTTURA_SPECIFICA = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'2' as ni_ord_uso, "
            + "(select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, INSTR(formatoFileDoc.nm_formato_file_doc, '.')+1)) as id_formato_file_standard "
            + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "AND formatoFileDoc.nm_formato_file_doc LIKE '%.%'";

    private static final String USO_CONCAT2_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'2' as ni_ord_uso, "
            + "(select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, INSTR(formatoFileDoc.nm_formato_file_doc, '.')+1)) as id_formato_file_standard "
            + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.id_strut = :idStrut "
            + "AND formatoFileDoc.nm_formato_file_doc LIKE '%.%' "
            + "AND NOT EXISTS (SELECT * FROM DEC_USO_FORMATO_FILE_STANDARD uso2 WHERE uso2.id_formato_file_doc = formatoFileDoc.id_formato_file_doc AND uso2.ni_ord_uso = 2) ";

    private static final String FORMATO_SINGOLO = "INSERT /*+ append */ INTO dec_formato_file_doc "
            + "    SELECT " + "        sdec_formato_file_doc.NEXTVAL, " + "        tab_a.id_strut, "
            + "        nm_formato_file_standard, " + "        ds_formato_file_standard, "
            + "        cd_versione, " + "        TO_DATE('01/01/2011', 'dd/mm/yyyy') dt_istituz, "
            + "        TO_DATE('31/12/2444', 'dd/mm/yyyy') dt_soppres " + "    FROM "
            + "             dec_formato_file_standard a " + "        CROSS JOIN ( "
            + "            SELECT DISTINCT " + "                id_strut " + "            FROM "
            + "                dec_formato_file_doc " + "        ) tab_a " + "    WHERE "
            + "        id_formato_file_standard = ?1 ";

    private static final String USO_SINGOLO = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, " + "?1 " + "FROM "
            + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.nm_Formato_File_Doc = ?2";

    private static final String FORMATO_SINGOLO_AGGIUNGI_CONCATENAZIONI = "INSERT /*+ append */ INTO DEC_FORMATO_FILE_DOC "
            + "            SELECT " + "            sdec_formato_file_doc.NEXTVAL, "
            + "            tab_a.id_strut,  " + "            ?1||'.'||nm_formato_file_standard, "
            + "            (select std.ds_formato_file_standard FROM dec_formato_file_standard std where std.nm_formato_file_standard = ?1) as ds_formato_file_standard, cd_versione, "
            + "            TO_DATE('01/01/2011', 'dd/mm/yyyy') dt_istituz, "
            + "            TO_DATE('31/12/2444', 'dd/mm/yyyy') dt_soppres FROM "
            + "            dec_formato_file_standard a "
            + "            CROSS JOIN ( SELECT DISTINCT " + "            id_strut "
            + "            FROM dec_formato_file_doc "
            + "            ) tab_a WHERE a.fl_formato_concat = 1";

    private static final String FORMATO_SINGOLO_USO_CONCAT2 = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "            SELECT " + "            SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "            formatoFileDoc.id_formato_file_doc, " + "            '2' as ni_ord_uso, "
            + "            (select id_formato_file_standard from dec_formato_file_standard "
            + "            where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, INSTR(formatoFileDoc.nm_formato_file_doc, '.')+1)) as id_formato_file_standard "
            + "            FROM dec_formato_file_doc formatoFileDoc "
            + "            WHERE formatoFileDoc.nm_Formato_File_Doc LIKE ?1";

    private static final String FORMATO_CONCATENATO = "INSERT /*+ append */ INTO dec_formato_file_doc (ID_FORMATO_FILE_DOC,ID_STRUT,NM_FORMATO_FILE_DOC,DS_FORMATO_FILE_DOC,CD_VERSIONE,DT_ISTITUZ,DT_SOPPRES) "
            + "SELECT " + "    sdec_formato_file_doc.NEXTVAL, " + "    tab_b.id_strut, "
            + "    da_concatenare " + "    || '.' " + "    || nm_formato_file_standard, "
            + "    (select std.ds_formato_file_standard FROM dec_formato_file_standard std where std.nm_formato_file_standard = da_concatenare) as ds_formato_file_standard, "
            + "    dec_formato_file_standard.cd_versione, "
            + "    TO_DATE('01/01/2011', 'dd/mm/yyyy') dt_istituz, "
            + "    TO_DATE('31/12/2444', 'dd/mm/yyyy') dt_soppres " + "FROM "
            + "         dec_formato_file_standard " + "    CROSS JOIN ( " + "        SELECT "
            + "            nm_formato_file_standard da_concatenare " + "        FROM "
            + "            dec_formato_file_standard " + "        WHERE "
            + "            fl_formato_concat = 0 " + "    ) tab_a " + "    CROSS JOIN ( "
            + "         SELECT DISTINCT " + "                id_strut " + "            FROM "
            + "                dec_formato_file_doc " + "    ) tab_b " + "WHERE "
            + "    nm_formato_file_standard = ?1 ";

    private static final String USO_CONCAT1 = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'1' as ni_ord_uso, "
            + "(select id_formato_file_standard from dec_formato_file_standard "
            + "where nm_formato_file_standard = SUBSTR(formatoFileDoc.nm_formato_file_doc, 1, INSTR(formatoFileDoc.nm_formato_file_doc, '.')-1)) as id_formato_file_standard "
            + "FROM " + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.nm_Formato_File_Doc LIKE ?1";

    private static final String USO_CONCAT2 = "INSERT /*+ append */ INTO dec_uso_formato_file_standard "
            + "SELECT " + "SDEC_USO_FORMATO_FILE_STANDARD.nextval, "
            + "formatoFileDoc.id_formato_file_doc, " + "'2' as ni_ord_uso, " + "?1 " + "FROM "
            + "dec_formato_file_doc formatoFileDoc "
            + "WHERE formatoFileDoc.nm_Formato_File_Doc LIKE ?2";
    //

    public String getAmmessiSingoliQuery(String tipoFlag) {
        return "INSERT /*+ append */ INTO dec_formato_file_ammesso " + "SELECT  "
                + "SDEC_FORMATO_FILE_AMMESSO.nextval,  " + "tab1.* FROM( " + " " + "SELECT "
                + "   tmp.id_tipo_comp_doc,  a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + tipoFlag
                + " = '1') tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc = ?1 " + "    AND b.ni_ord_uso = 1 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut)tab1 ";
    }

    public String getAmmessiConcatenatiQueryInsert(String tipoFlag) {

        return "INSERT /*+ append */ INTO dec_formato_file_ammesso " + "SELECT  "
                + "SDEC_FORMATO_FILE_AMMESSO.nextval,  " + "tab1.* FROM(SELECT "
                + "    tmp.id_tipo_comp_doc, a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + tipoFlag
                + " = '1') tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc LIKE ?1 " + "    AND b.ni_ord_uso = 2 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut)tab1";
    }

    public String getAmmessiConcatenatiFlagSpuntatoQueryInsert(String tipoFlag,
            String tiEsitoContrFormato) {

        return "INSERT /*+ append */ INTO dec_formato_file_ammesso " + "SELECT  "
                + "SDEC_FORMATO_FILE_AMMESSO.nextval,  " + "tab1.* FROM(SELECT "
                + "    tmp.id_tipo_comp_doc, a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + tipoFlag
                + " = '1') tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc LIKE ?1 " + "    AND b.ni_ord_uso = 2 "
                + "AND c.ti_esito_contr_formato = ?2 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut)tab1";
    }

    public String getAmmessiSingoloEConcatenatiQueryInsert(String tipoFlag) {

        return "INSERT /*+ append */ INTO dec_formato_file_ammesso " + "SELECT  "
                + "SDEC_FORMATO_FILE_AMMESSO.nextval,  " + "tab1.* FROM( " + " " + "SELECT "
                + "   tmp.id_tipo_comp_doc,  a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + tipoFlag
                + " = '1') tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc = ?1 " + "    AND b.ni_ord_uso = 1 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut " + "UNION ALL " + "SELECT "
                + "    tmp.id_tipo_comp_doc, a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + tipoFlag
                + " = '1') tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc LIKE ?2 " + "    AND b.ni_ord_uso = 2 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut)tab1";
    }

    public String getAmmessiSingoloEConcatenatiQueryInsertForUpdateTiControFormato(String oldFlag,
            String newFlag) {

        return "INSERT /*+ append */ INTO dec_formato_file_ammesso " + "SELECT  "
                + "SDEC_FORMATO_FILE_AMMESSO.nextval,  " + "tab1.* FROM( " + " " + "SELECT "
                + "   tmp.id_tipo_comp_doc,  a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + newFlag + " = '1' AND "
                + oldFlag + " = '0' ) tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc = ?1 " + "    AND b.ni_ord_uso = 1 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut " + "UNION ALL " + "SELECT "
                + "    tmp.id_tipo_comp_doc, a.id_formato_file_doc " + "FROM "
                + "         dec_formato_file_doc a "
                + "    JOIN dec_uso_formato_file_standard b ON a.id_formato_file_doc = b.id_formato_file_doc "
                + "    JOIN dec_formato_file_standard     c ON b.id_formato_file_standard = c.id_formato_file_standard "
                + "    CROSS JOIN (SELECT * FROM DEC_TIPO_COMP_DOC WHERE " + newFlag + " = '1' AND "
                + oldFlag + " = '0' ) tmp "
                + "    JOIN dec_tipo_strut_doc tipo_strut_doc on (tipo_strut_doc.id_tipo_strut_doc = tmp.id_tipo_strut_doc) "
                + "WHERE " + "    nm_formato_file_doc LIKE ?2 " + "    AND b.ni_ord_uso = 2 "
                + "    AND a.id_strut = tipo_strut_doc.id_strut)tab1"
                + " WHERE NOT EXISTS ( SELECT 1 FROM dec_formato_file_ammesso dfa "
                + " WHERE dfa.id_tipo_comp_doc = tab1.id_tipo_comp_doc "
                + " AND dfa.id_formato_file_doc = tab1.id_formato_file_doc) ";
    }

    public String getAmmessiSingoloEConcatenatiQueryDeleteForUpdateTiControFormato(String oldFlag,
            String newFlag) {

        return "delete from dec_formato_file_ammesso " + "where id_tipo_comp_doc IN "
                + "(select compDoc.id_tipo_comp_doc from dec_tipo_comp_doc compDoc " + "where "
                + oldFlag + " = 1 " + "AND " + newFlag + " = 0)" + "AND id_formato_file_doc IN "
                + "(select formatoFileDoc.id_formato_file_doc from dec_formato_file_doc formatoFileDoc where nm_formato_file_doc LIKE ?1 "
                + "UNION ALL select formatoFileDoc.id_formato_file_doc from dec_formato_file_doc formatoFileDoc where nm_formato_file_doc = ?2)";
    }

    public String getQueryDeleteFormatiAmmessi() {
        return "DELETE FROM DEC_FORMATO_FILE_AMMESSO WHERE id_formato_file_ammesso IN (SELECT formato_file_ammesso.id_formato_file_ammesso FROM  DEC_TIPO_COMP_DOC comp_doc "
                + "JOIN DEC_FORMATO_FILE_AMMESSO formato_file_ammesso ON (formato_file_ammesso.id_tipo_comp_doc = comp_doc.id_tipo_comp_doc) "
                + "JOIN DEC_FORMATO_FILE_DOC formato_file_doc ON (formato_file_doc.id_formato_file_doc = formato_file_ammesso.id_formato_file_doc) "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "WHERE formato_file_standard.ti_esito_contr_formato = :tiEsitoContrFormato "
                + "AND comp_doc.id_tipo_comp_doc = :idTipoCompDoc " + "AND uso.ni_ord_uso = 1 "
                + "AND formato_file_doc.nm_formato_file_doc = formato_file_standard.nm_formato_file_standard "
                + "UNION ALL "
                + "SELECT formato_file_ammesso.id_formato_file_ammesso FROM  DEC_TIPO_COMP_DOC comp_doc "
                + "JOIN DEC_FORMATO_FILE_AMMESSO formato_file_ammesso ON (formato_file_ammesso.id_tipo_comp_doc = comp_doc.id_tipo_comp_doc) "
                + "JOIN DEC_FORMATO_FILE_DOC formato_file_doc ON (formato_file_doc.id_formato_file_doc = formato_file_ammesso.id_formato_file_doc) "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "WHERE formato_file_standard.ti_esito_contr_formato = :tiEsitoContrFormato "
                //
                // Vince il ti esito del formato più esterno, per questo la condizione su ni_ord_uso
                // è fatta così
                + "AND comp_doc.id_tipo_comp_doc = :idTipoCompDoc AND uso.ni_ord_uso = (SELECT max(ni_ord_uso) from dec_uso_formato_file_standard uso2 where uso2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "AND formato_File_Doc.nm_formato_file_doc LIKE '%.%') ";
    }

    // BDCS
    //
    public String getQueryInsertFormatiAmmessi() {
        return "INSERT /*+ append */ INTO DEC_FORMATO_FILE_AMMESSO (ID_FORMATO_FILE_AMMESSO,ID_FORMATO_FILE_DOC,ID_TIPO_COMP_DOC)  "
                + "(SELECT sdec_formato_file_ammesso.nextval, tmp.id_formato_file_doc, :idTipoCompDoc FROM "
                + "(SELECT formato_file_doc.id_formato_file_doc FROM DEC_FORMATO_FILE_DOC formato_file_doc "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "WHERE formato_file_standard.ti_esito_contr_formato = :tiEsitoContrFormato "
                + "AND formato_file_doc.id_strut = :idStrut " + "AND uso.ni_ord_uso = 1 "
                + "AND formato_file_doc.nm_formato_file_doc = formato_file_standard.nm_formato_file_standard "
                + "UNION ALL "
                + "SELECT formato_file_doc.id_formato_file_doc FROM DEC_FORMATO_FILE_DOC formato_file_doc "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "WHERE formato_file_standard.ti_esito_contr_formato = :tiEsitoContrFormato "
                // Vince il ti esito del formato più interno, per questo la condizione su ni_ord_uso
                // è fatta così
                + "AND formato_file_doc.id_strut = :idStrut AND uso.ni_ord_uso = (SELECT min(ni_ord_uso) from dec_uso_formato_file_standard uso2 where uso2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "AND formato_File_Doc.nm_formato_file_doc LIKE '%.%') tmp "
                + "WHERE NOT EXISTS (SELECT * FROM DEC_FORMATO_FILE_AMMESSO ammesso WHERE ammesso.id_formato_file_doc = tmp.id_formato_file_doc AND ammesso.id_tipo_comp_doc = :idTipoCompDoc))";
    }

    //
    public String getQuerySelectFormatiAmmessi() { //
        return "select * from  " + "(select  "
                + "ammesso.id_formato_file_ammesso as id_formato_file_ammesso, " // getQuerySelectFormatiAmmessi
                + "formato_file_doc.nm_formato_file_doc as nm_formato_file_doc, "
                + "formato_file_standard.nm_mimetype_file as nm_mimetype_file, "
                + "formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,  "
                + "formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato, "
                + "(select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,  "
                + "(select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc), "
                + "formato_file_doc.cd_versione, " + "formato_file_doc.dt_istituz, "
                + "formato_file_doc.dt_soppres " + "from dec_formato_file_ammesso ammesso "
                + "join dec_formato_file_doc formato_file_doc on (formato_file_doc.id_formato_file_doc = ammesso.id_formato_file_doc) "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "where ammesso.id_tipo_comp_doc = :idTipoCompDoc " + "AND uso.ni_ord_uso = 1 "
                + "AND formato_file_doc.nm_formato_file_doc = formato_file_standard.nm_formato_file_standard "
                + "UNION ALL " + "select  "
                + "ammesso.id_formato_file_ammesso as id_formato_file_ammesso, "
                + "formato_file_doc.nm_formato_file_doc as nm_formato_file_doc, "
                //
                + "(select std2.nm_mimetype_file FROM dec_formato_file_standard std2 "
                + "where std2.nm_formato_file_standard = SUBSTR(formato_file_doc.nm_formato_file_doc, 1, INSTR(formato_file_doc.nm_formato_file_doc, '.')-1)) as nm_mimetype_file, "
                + "formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,  "
                + "formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato, "
                + "(select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,  "
                + "(select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as nm_formato_file_standard, "
                + "formato_file_doc.cd_versione, " + "formato_file_doc.dt_istituz, "
                + "formato_file_doc.dt_soppres " + "FROM DEC_FORMATO_FILE_AMMESSO ammesso "
                + "JOIN DEC_FORMATO_FILE_DOC formato_file_doc ON (formato_file_doc.id_formato_file_doc = ammesso.id_formato_file_doc) "
                + "JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard) "
                + "where ammesso.id_tipo_comp_doc = :idTipoCompDoc "
                + "AND uso.ni_ord_uso = (SELECT min(ni_ord_uso) from dec_uso_formato_file_standard uso2 where uso2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) "
                + "AND formato_File_Doc.nm_formato_file_doc LIKE '%.%') tmp "
                + "order by tmp.nm_mimetype_file, tmp.nm_formato_file_doc ";
    }

    private static final String joinNomeFormato = " JOIN DEC_ESTENSIONE_FILE estensione ON (formato_file_standard.id_formato_file_standard = estensione.id_formato_file_standard) ";
    private static final String whereNomeFormato = " AND (UPPER(formato_file_doc.nm_formato_file_doc) LIKE :nmFormato OR UPPER(estensione.cd_Estensione_File) LIKE :nmFormato) ";
    private static final String whereMimetypeFormato = " AND formato_File_Standard.nm_Mimetype_File = :nmMimetypeFile ";

    public String getQueryFormatiAmmissibiliDaAggiungere(String joinNomeFormato,
            String whereNomeFormato, String whereMimetypeFormato) {
        return "select tmp1.* from     (select  " + "formato_file_doc.id_strut as id_strut, "
                + "                 formato_file_doc.id_formato_file_doc as id_formato_file_doc,   "
                + "                 formato_file_doc.nm_formato_file_doc as nm_formato_file_doc,   "
                + "                 formato_file_standard.nm_mimetype_file as nm_mimetype_file,   "
                + "                 formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,    "
                + "                 formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato,   "
                + "                 (select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,    "
                + "                 (select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc),   "
                + "                 formato_file_doc.cd_versione,    formato_file_doc.dt_istituz,    formato_file_doc.dt_soppres   "
                + "                 from  "
                + "                 dec_formato_file_doc formato_file_doc "
                + "                 JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + "                 JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard)   "
                + joinNomeFormato + "                 where uso.ni_ord_uso = 1   "
                + whereNomeFormato + whereMimetypeFormato
                + "                 AND formato_file_doc.nm_formato_file_doc = formato_file_standard.nm_formato_file_standard   "
                + "                 UNION    select  "
                + "                 formato_file_doc.id_strut as id_strut, "
                + "                 formato_file_doc.id_formato_file_doc as id_formato_file_doc,   "
                + "                 formato_file_doc.nm_formato_file_doc as nm_formato_file_doc,   "
                + "                 "
                + "                 (select std2.nm_mimetype_file FROM dec_formato_file_standard std2   "
                + "                 where std2.nm_formato_file_standard = SUBSTR(formato_file_doc.nm_formato_file_doc, 1, INSTR(formato_file_doc.nm_formato_file_doc, '.')-1)) as nm_mimetype_file,   "
                + "                 formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,    "
                + "                 formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato,   "
                + "                 (select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,    "
                + "                 (select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as nm_formato_file_standard,   "
                + "                 formato_file_doc.cd_versione,    formato_file_doc.dt_istituz,    formato_file_doc.dt_soppres   "
                + "                 FROM DEC_FORMATO_FILE_DOC formato_file_doc "
                + "                 JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + "                 JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard)   "
                + joinNomeFormato
                + "                 where uso.ni_ord_uso = (SELECT min(ni_ord_uso) from dec_uso_formato_file_standard uso2 where uso2.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + whereNomeFormato + whereMimetypeFormato
                + "                 AND formato_File_Doc.nm_formato_file_doc LIKE '%.%') tmp1 "
                + "where tmp1.id_strut = :idStrut " + "and NOT EXISTS ( "
                + "select tmp.* from     (select      "
                + "                 ammesso.id_formato_file_ammesso as id_formato_file_ammesso,  "
                + "                 formato_file_doc.id_formato_file_doc as id_formato_file_doc,   "
                + "                 formato_file_doc.nm_formato_file_doc as nm_formato_file_doc,   "
                + "                 formato_file_standard.nm_mimetype_file as nm_mimetype_file,   "
                + "                 formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,    "
                + "                 formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato,   "
                + "                 (select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,    "
                + "                 (select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc),   "
                + "                 formato_file_doc.cd_versione,    formato_file_doc.dt_istituz,    formato_file_doc.dt_soppres   "
                + "                 from dec_formato_file_ammesso ammesso   "
                + "                 join dec_formato_file_doc formato_file_doc on (formato_file_doc.id_formato_file_doc = ammesso.id_formato_file_doc)   "
                + "                 JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + "                 JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard)   "
                + joinNomeFormato
                + "                 where ammesso.id_tipo_comp_doc = :idTipoCompDoc    AND uso.ni_ord_uso = 1   "
                + "                 AND formato_file_doc.nm_formato_file_doc = formato_file_standard.nm_formato_file_standard   "
                + whereNomeFormato + whereMimetypeFormato + "                 UNION    select      "
                + "                 ammesso.id_formato_file_ammesso as id_formato_file_ammesso,  "
                + "                 formato_file_doc.id_formato_file_doc as id_formato_file_doc,   "
                + "                 formato_file_doc.nm_formato_file_doc as nm_formato_file_doc,   "
                + "                 "
                + "                 (select std2.nm_mimetype_file FROM dec_formato_file_standard std2   "
                + "                 where std2.nm_formato_file_standard = SUBSTR(formato_file_doc.nm_formato_file_doc, 1, INSTR(formato_file_doc.nm_formato_file_doc, '.')-1)) as nm_mimetype_file,   "
                + "                 formato_file_doc.ds_formato_file_doc as ds_formato_file_doc,    "
                + "                 formato_file_standard.ti_esito_contr_formato as ti_esito_contr_formato,   "
                + "                 (select CASE WHEN sysdate BETWEEN ffd.dt_istituz AND ffd.dt_soppres then '1' else '0' end from dec_formato_file_doc ffd where ffd.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as fl_attivo,    "
                + "                 (select replace(ffd2.nm_formato_file_doc, '.', ', ') FROM DEC_FORMATO_FILE_DOC ffd2 where ffd2.id_formato_file_doc = formato_file_doc.id_formato_file_doc) as nm_formato_file_standard,   "
                + "                 formato_file_doc.cd_versione,    formato_file_doc.dt_istituz,    formato_file_doc.dt_soppres   "
                + "                 FROM DEC_FORMATO_FILE_AMMESSO ammesso   "
                + "                 JOIN DEC_FORMATO_FILE_DOC formato_file_doc ON (formato_file_doc.id_formato_file_doc = ammesso.id_formato_file_doc)   "
                + "                 JOIN DEC_USO_FORMATO_FILE_STANDARD uso ON (uso.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + "                 JOIN DEC_FORMATO_FILE_STANDARD formato_file_standard ON (formato_file_standard.id_formato_file_standard = uso.id_formato_file_standard)   "
                + joinNomeFormato
                + "                 where ammesso.id_tipo_comp_doc = :idTipoCompDoc   "
                + whereNomeFormato + whereMimetypeFormato
                + "                 AND uso.ni_ord_uso = (SELECT min(ni_ord_uso) from dec_uso_formato_file_standard uso2 where uso2.id_formato_file_doc = formato_file_doc.id_formato_file_doc)   "
                + "                 AND formato_File_Doc.nm_formato_file_doc LIKE '%.%') tmp "
                + "                 WHERE tmp1.id_formato_file_doc = tmp.id_formato_file_doc) ";
    }

    public void insertDecFormatoFileDocStrutturaSpecificaNative(long idStrut) {
        Query query = getEntityManager().createNativeQuery(FORMATI_STRUTTURA_SPECIFICA);
        query.setParameter("idStrut", idStrut);
        int i = query.executeUpdate();
        Query query2 = getEntityManager().createNativeQuery(USO_SINGOLO_STRUTTURA_SPECIFICA);
        query2.setParameter("idStrut", idStrut);
        int j = query2.executeUpdate();
        Query query3 = getEntityManager().createNativeQuery(USO_CONCAT1_STRUTTURA_SPECIFICA);
        query3.setParameter("idStrut", idStrut);
        int k = query3.executeUpdate();
        Query query4 = getEntityManager().createNativeQuery(USO_CONCAT2_STRUTTURA_SPECIFICA);
        query4.setParameter("idStrut", idStrut);
        int l = query4.executeUpdate();
        System.out.println(i + ", " + j + ", " + k + ", " + l);
        getEntityManager().flush();
    }

    public void insertDecFormatoFileDocStrutturaSpecificaNativeExcludedGiaPresenti(long idStrut) {
        Query query = getEntityManager()
                .createNativeQuery(FORMATI_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI);
        query.setParameter("idStrut", idStrut);
        query.executeUpdate();
        Query query2 = getEntityManager()
                .createNativeQuery(USO_SINGOLO_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI);
        query2.setParameter("idStrut", idStrut);
        query2.executeUpdate();
        Query query3 = getEntityManager()
                .createNativeQuery(USO_CONCAT1_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI);
        query3.setParameter("idStrut", idStrut);
        query3.executeUpdate();
        Query query4 = getEntityManager()
                .createNativeQuery(USO_CONCAT2_STRUTTURA_SPECIFICA_ESCLUDI_PRESENTI);
        query4.setParameter("idStrut", idStrut);
        query4.executeUpdate();
        getEntityManager().flush();
    }

    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // public void insertDecFormatoFileDocStrutturaSpecificaNativeNewTx(long idStrut) {
    // insertDecFormatoFileDocStrutturaSpecificaNative(idStrut);
    // }

    public void insertDecFormatoFileDocSingoloNative(long idFormatoFileStandard,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(FORMATO_SINGOLO);
        query.setParameter(1, idFormatoFileStandard);
        query.executeUpdate();
        Query query2 = getEntityManager().createNativeQuery(USO_SINGOLO);
        query2.setParameter(1, idFormatoFileStandard);
        query2.setParameter(2, nmFormatoFileStandard);
        query2.executeUpdate();
        Query query3 = getEntityManager()
                .createNativeQuery(FORMATO_SINGOLO_AGGIUNGI_CONCATENAZIONI);
        query3.setParameter(1, nmFormatoFileStandard);
        query3.executeUpdate();
        Query query4 = getEntityManager().createNativeQuery(USO_CONCAT1);
        query4.setParameter(1, nmFormatoFileStandard + ".%");
        query4.executeUpdate();
        Query query5 = getEntityManager().createNativeQuery(FORMATO_SINGOLO_USO_CONCAT2);
        query5.setParameter(1, nmFormatoFileStandard + ".%");
        query5.executeUpdate();
        getEntityManager().flush();
    }

    public void insertDecFormatoFileDocSingoloConcatenabileNative(long idFormatoFileStandard,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(FORMATO_SINGOLO);
        query.setParameter(1, idFormatoFileStandard);
        query.executeUpdate();
        Query query2 = getEntityManager().createNativeQuery(USO_SINGOLO);
        query2.setParameter(1, idFormatoFileStandard);
        query2.setParameter(2, nmFormatoFileStandard);
        query2.executeUpdate();
        getEntityManager().flush();
    }

    public void insertDecFormatoFileDocConcatenatiNative(long idFormatoFileStandard,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(FORMATO_CONCATENATO);
        query.setParameter(1, nmFormatoFileStandard);
        query.executeUpdate();
        Query query2 = getEntityManager().createNativeQuery(USO_CONCAT1);
        query2.setParameter(1, "%." + nmFormatoFileStandard);
        query2.executeUpdate();
        getEntityManager().flush();
        Query query3 = getEntityManager().createNativeQuery(USO_CONCAT2);
        query3.setParameter(1, idFormatoFileStandard);
        query3.setParameter(2, "%." + nmFormatoFileStandard);
        query3.executeUpdate();
        getEntityManager().flush();
    }

    public void insertDecFormatoFileAmmessoSingoloNative(String tipoFlag,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(getAmmessiSingoliQuery(tipoFlag));
        query.setParameter(1, nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void insertDecFormatoFileAmmessoConcatenatiNative(String tipoFlag,
            String nmFormatoFileStandard) {
        Query query = getEntityManager()
                .createNativeQuery(getAmmessiConcatenatiQueryInsert(tipoFlag));
        query.setParameter(1, nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void insertDecFormatoFileAmmessoConcatenatiFlagSpuntatoNative(String tipoFlag,
            String nmFormatoFileStandard) {
        String tiEsito = getTiEsitoTipoComponente(tipoFlag);
        Query query = getEntityManager()
                .createNativeQuery(getAmmessiConcatenatiFlagSpuntatoQueryInsert(tipoFlag, tiEsito));
        query.setParameter(1, nmFormatoFileStandard);
        query.setParameter(2, tiEsito);
        query.executeUpdate();
        getEntityManager().flush();
    }

    private String getTiEsitoTipoComponente(String tipoFlag) {
        String tiEsito = "";
        switch (tipoFlag) {
        case "fl_gestiti":
            tiEsito = "GESTITO";
            break;
        case "fl_idonei":
            tiEsito = "IDONEO";
            break;
        case "fl_deprecati":
            tiEsito = "DEPRECATO";
            break;
        default:
            break;
        }
        return tiEsito;
    }

    public void insertDecFormatoFileAmmessoSingoloEConcatenatiNative(String tipoFlag,
            String nmFormatoFileStandard) {
        Query query = getEntityManager()
                .createNativeQuery(getAmmessiSingoloEConcatenatiQueryInsert(tipoFlag));
        query.setParameter(1, nmFormatoFileStandard);
        query.setParameter(2, "%." + nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecTipoCompDoc> getDecTipoCompDocByFlagList(String tipo, String flag) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoCompDoc FROM DecTipoCompDoc tipoCompDoc ");

        switch (tipo) {
        case "GESTITI":
            queryStr.append("WHERE tipoCompDoc.flGestiti = :flag ");
            break;
        case "IDONEI":
            queryStr.append("WHERE tipoCompDoc.flIdonei = :flag ");
            break;
        case "DEPRECATI":
            queryStr.append("WHERE tipoCompDoc.flDeprecati = :flag ");
            break;
        default:
            break;
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("flag", flag);
        return query.getResultList();
    }

    public void deleteConcatenazioni(String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(
                "DELETE FROM DEC_FORMATO_FILE_DOC " + "WHERE nm_formato_file_doc LIKE ?1 ");
        query.setParameter(1, "%." + nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteOldTiEsitoContrFormato(String oldFlag, String newFlag,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(
                getAmmessiSingoloEConcatenatiQueryDeleteForUpdateTiControFormato(oldFlag, newFlag));
        query.setParameter(1, "%." + nmFormatoFileStandard);
        query.setParameter(2, nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void insertNewTiEsitoContrFormato(String oldFlag, String newFlag,
            String nmFormatoFileStandard) {
        Query query = getEntityManager().createNativeQuery(
                getAmmessiSingoloEConcatenatiQueryInsertForUpdateTiControFormato(oldFlag, newFlag));
        query.setParameter(1, nmFormatoFileStandard);
        query.setParameter(2, "%." + nmFormatoFileStandard);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public boolean isFormatoAmmesso(String nmFormatoFileStandard) {
        String nativeQuery = "SELECT " + "    CASE " + "        WHEN EXISTS ( "
                + "            SELECT" + "                1 " + "            FROM "
                + "                     dec_formato_file_ammesso formatofileammesso "
                + "                JOIN dec_formato_file_doc formatofiledoc ON ( formatofileammesso.id_formato_file_doc = formatofiledoc.id_formato_file_doc "
                + "                ) " + "            WHERE "
                + "                ( formatofiledoc.nm_formato_file_doc = :nmFormatoFileStandard "
                + "                  OR formatofiledoc.nm_formato_file_doc LIKE :nmFormatoFileStandardLike ) "
                + "        ) THEN " + "            '1' " + "        ELSE " + "            '0' "
                + "    END AS rec_exists " + "FROM " + "    dual";

        Query q = getEntityManager().createNativeQuery(nativeQuery);
        q.setParameter("nmFormatoFileStandard", nmFormatoFileStandard);
        q.setParameter("nmFormatoFileStandardLike", "%." + nmFormatoFileStandard);
        return ((Character) q.getSingleResult()).compareTo("1".charAt(0)) == 0;

    }

    public void deleteFormatoFileDoc(String nmFormatoFileStandard) {
        String nativeQuery = "delete from dec_formato_file_doc formatoFileDoc "
                + "WHERE formatoFileDoc.nm_formato_file_doc = :nmFormatoFileStandard or formatoFileDoc.nm_formato_file_doc LIKE :nmFormatoFileStandardLike";
        Query q = getEntityManager().createNativeQuery(nativeQuery);
        q.setParameter("nmFormatoFileStandard", nmFormatoFileStandard);
        q.setParameter("nmFormatoFileStandardLike", "%." + nmFormatoFileStandard);
        q.executeUpdate();
        getEntityManager().flush();

    }

    public List<DecFormatoFileDoc> getDecFormatoFileDocPersonalizzati(BigDecimal idStrut) {
        // Recupero i formati personalizzati per la struttura, ovvero quelli con una concatenazione
        // dal terzo livello in
        // su
        String queryStr = "SELECT DISTINCT formatoFileDoc FROM DecUsoFormatoFileStandard uso JOIN uso.decFormatoFileDoc formatoFileDoc "
                + "WHERE uso.niOrdUso >= 3 " + "AND formatoFileDoc.orgStrut.idStrut = :idStrut ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecFormatoFileDoc> formatiPersonalizzatiList = q.getResultList();
        return formatiPersonalizzatiList;
    }

    public List<Object[]> getDecFormatoFileDocPersonalizzati2(BigDecimal idStrut) {
        // Recupero i formati personalizzati per la struttura, ovvero quelli con una concatenazione
        // dal terzo livello in
        // su
        String queryStr = "SELECT DISTINCT formatoFileDoc.idFormatoFileDoc, formatoFileDoc.nmFormatoFileDoc, formatoFileStandard.nmMimetypeFile,"
                + "formatoFileStandard.nmFormatoFileStandard, formatoFileDoc.dsFormatoFileDoc, formatoFileStandard.tiEsitoContrFormato FROM DecUsoFormatoFileStandard uso "
                + "JOIN uso.decFormatoFileDoc formatoFileDoc "
                + "JOIN uso.decFormatoFileStandard formatoFileStandard "
                + "WHERE uso.niOrdUso >= 3 " + "AND formatoFileDoc.orgStrut.idStrut = :idStrut ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<Object[]> formatiPersonalizzatiList = q.getResultList();
        return formatiPersonalizzatiList;
    }

    public List<DecFormatoFileDoc> getDecFormatoFileDocPersonalizzati3(BigDecimal idStrut) {
        // Recupero i formati personalizzati per la struttura, ovvero quelli con una concatenazione
        // dal terzo livello in
        // su
        String queryStr = "SELECT DISTINCT formatoFileDoc FROM DecUsoFormatoFileStandard uso JOIN uso.decFormatoFileDoc formatoFileDoc "
                + "WHERE uso.niOrdUso >= 3 " + "AND formatoFileDoc.orgStrut.idStrut = :idStrut ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecFormatoFileDoc> formatiPersonalizzatiList = q.getResultList();
        return formatiPersonalizzatiList;
    }

    public List<DecFormatoFileDoc> getDecFormatoFileDocPersonalizzatiTipoComp(BigDecimal idStrut,
            BigDecimal idTipoCompDoc) {
        // Recupero i formati personalizzati per il tipo componente e di conseguenza per la
        // struttura, ovvero quelli con
        // una concatenazione dal terzo livello in
        // su
        String queryStr = "SELECT DISTINCT formatoFileDoc FROM DecUsoFormatoFileStandard uso "
                + "JOIN uso.decFormatoFileDoc formatoFileDoc " + "WHERE uso.niOrdUso >= 3 "
                + "AND formatoFileDoc.orgStrut.idStrut = :idStrut "
                + "AND formatoFileDoc.decFormatoFileAmmessos.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idStrut", longFromBigDecimal(idStrut));
        q.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        List<DecFormatoFileDoc> formatiPersonalizzatiList = q.getResultList();
        return formatiPersonalizzatiList;
    }

    public boolean existsFormatoPresenteInAltriTipiCompStrut(long idStrut, long idTipoCompDoc,
            String nmFormatoFileDoc) {
        String queryStr = "SELECT formatoFileAmmesso FROM DecFormatoFileAmmesso formatoFileAmmesso "
                + "JOIN formatoFileAmmesso.decFormatoFileDoc formatoFileDoc "
                + "JOIN formatoFileAmmesso.decTipoCompDoc tipoCompDoc "
                + "WHERE tipoCompDoc.idTipoCompDoc <> :idTipoCompDoc "
                + "AND formatoFileDoc.nmFormatoFileDoc = :nmFormatoFileDoc "
                + "AND formatoFileDoc.orgStrut.idStrut = :idStrut ";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("nmFormatoFileDoc", nmFormatoFileDoc);
        q.setParameter("idTipoCompDoc", idTipoCompDoc);
        q.setParameter("idStrut", idStrut);
        return !q.getResultList().isEmpty();
    }

    public void deleteFormatiAmmessi(BigDecimal idTipoCompDoc, String tiEsitoContrFormato) {
        Query query = getEntityManager().createNativeQuery(getQueryDeleteFormatiAmmessi());
        query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        query.setParameter("tiEsitoContrFormato", tiEsitoContrFormato);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public void insertFormatiAmmessi(long idStrut, BigDecimal idTipoCompDoc,
            String tiEsitoContrFormato) {
        Query query = getEntityManager().createNativeQuery(getQueryInsertFormatiAmmessi());
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        query.setParameter("tiEsitoContrFormato", tiEsitoContrFormato);
        query.executeUpdate();
        getEntityManager().flush();
    }

    public List<Object[]> selectFormatiAmmessi(BigDecimal idTipoCompDoc) {
        Query query = getEntityManager().createNativeQuery(getQuerySelectFormatiAmmessi());
        query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        return (List<Object[]>) query.getResultList();
    }

    public List<Object[]> getFormatiAmmissibiliDaAggiungere(BigDecimal idTipoCompDoc,
            BigDecimal idStrut, String nmFormatoFileStandard, String nmMimetypeFile) {
        String joinFormati = nmFormatoFileStandard != null ? joinNomeFormato : "";
        String whereFormati = nmFormatoFileStandard != null ? whereNomeFormato : "";
        String whereMimetype = nmMimetypeFile != null ? whereMimetypeFormato : "";

        Query query = getEntityManager().createNativeQuery(
                getQueryFormatiAmmissibiliDaAggiungere(joinFormati, whereFormati, whereMimetype));
        query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));

        if (StringUtils.isNotBlank(nmFormatoFileStandard)) {
            query.setParameter("nmFormato", "%" + nmFormatoFileStandard.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            query.setParameter("nmMimetypeFile", nmMimetypeFile);
        }
        return (List<Object[]>) query.getResultList();
    }

    public void bulkDeleteDecFormatoFileDoc(long idStrut) {
        String queryStr = "DELETE FROM DecFormatoFileDoc formatoFileDoc "
                + "WHERE formatoFileDoc.orgStrut.idStrut = :idStrut ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idStrut", idStrut);
        q.executeUpdate();
        getEntityManager().flush();
    }
}
