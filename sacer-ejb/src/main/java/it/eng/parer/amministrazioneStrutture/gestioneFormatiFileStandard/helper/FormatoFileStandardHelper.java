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

package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper;

import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileAmmesso;
import it.eng.parer.entity.DecFormatoFileBusta;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecFormatoGruppoProprieta;
import it.eng.parer.entity.DecFormatoProprieta;
import it.eng.parer.entity.DecFormatoValutazione;
import it.eng.parer.helper.GenericHelper;
import it.eng.spagoLite.form.base.BaseElements;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper dei formati file standard dell'applicazione
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class FormatoFileStandardHelper extends GenericHelper {

    public List<Object[]> getDecFormatoFileStandardInList(Set<String> formati, BaseElements.Status status,
            BigDecimal idStrut) {

        Set<String> formatiUpperCase = new HashSet<>();

        for (String string : formati) {
            formatiUpperCase.add(string.toUpperCase());
        }

        StringBuilder queryStr = new StringBuilder(
                "SELECT formato,ext.cdEstensioneFile,formatofileDoc.dtIstituz, formatoFileDoc.dtSoppres  FROM DecEstensioneFile ext JOIN ext.decFormatoFileStandard formato "
                        + "JOIN formato.decUsoFormatoFileStandards usoFormato JOIN usoFormato.decFormatoFileDoc formatoFileDoc ");

        if (status.equals(BaseElements.Status.update)) {
            queryStr.append(" WHERE formato.flFormatoConcat = '1' ");
        }
        if (!formati.isEmpty()) {
            if (status.equals(BaseElements.Status.update)) {
                queryStr.append(" AND UPPER(formato.nmFormatoFileStandard) IN (:nmformati)");
            } else {
                queryStr.append(" WHERE UPPER(ext.cdEstensioneFile) IN (:nmformati) ");
            }
        }

        queryStr.append(" AND formato.nmFormatoFileStandard =  formatoFileDoc.nmFormatoFileDoc "
                + " AND formatoFileDoc.orgStrut.idStrut = :idStrut ");
        queryStr.append(" ORDER BY ext.cdEstensioneFile ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formatiUpperCase);
        }
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return query.getResultList();
    }

    public List<Object[]> getDecFormatoFileStandardInListByName(Collection<String> formati) {

        Set<String> formatiUpperCase = new HashSet<>();

        for (String string : formati) {
            formatiUpperCase.add(string.toUpperCase());
        }

        Query query = getEntityManager().createQuery(
                "SELECT formato,ext.cdEstensioneFile  FROM DecEstensioneFile ext JOIN ext.decFormatoFileStandard formato WHERE formato.flFormatoConcat = '1' AND UPPER(ext.cdEstensioneFile) IN (:nmformati) ");

        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formatiUpperCase);
        }

        return query.getResultList();
    }

    public List<Object[]> getDecFormatoFileStandardNotInList(Collection<String> formati, BaseElements.Status status) {

        Set<String> formatiUpperCase = new HashSet<>();

        for (String string : formati) {
            formatiUpperCase.add(string.toUpperCase());
        }

        StringBuilder queryStr = new StringBuilder(
                "SELECT formato,ext.cdEstensioneFile  FROM DecEstensioneFile ext JOIN ext.decFormatoFileStandard formato ");
        if (status.equals(BaseElements.Status.update)) {
            queryStr.append(" WHERE formato.flFormatoConcat = '1' ");
        }
        if (!formati.isEmpty()) {
            if (status.equals(BaseElements.Status.update)) {
                queryStr.append(" AND UPPER(formato.nmFormatoFileStandard) NOT IN (:nmformati)");
            } else {
                queryStr.append(" WHERE UPPER(formato.nmFormatoFileStandard) NOT IN (:nmformati) ");
            }
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formatiUpperCase);
        }
        return query.getResultList();
    }

    public DecFormatoFileStandard getDecFormatoFileStandardByName(String nmFormato) {
        Query query = getEntityManager().createQuery(
                "SELECT formato FROM DecFormatoFileStandard formato WHERE UPPER(formato.nmFormatoFileStandard) = :nmFormato");
        query.setParameter("nmFormato", nmFormato.toUpperCase());
        List<DecFormatoFileStandard> list = query.getResultList();
        DecFormatoFileStandard formato = null;
        if (!list.isEmpty()) {
            formato = list.get(0);
        }

        return formato;
    }

    public String getDecFormatoFileStandardNameFromEstensioneFile(String cdEstensioneFile) {
        Query query = getEntityManager().createQuery(
                "SELECT formatoFileStandard.nmFormatoFileStandard FROM DecEstensioneFile estensioneFile JOIN"
                        + " estensioneFile.decFormatoFileStandard formatoFileStandard WHERE UPPER(estensioneFile.cdEstensioneFile) = :cdEstensioneFile",
                String.class);
        query.setParameter("cdEstensioneFile", cdEstensioneFile.toUpperCase());

        return (String) query.getSingleResult();
    }

    public DecEstensioneFile getDecEstensioneFileByName(String cdEstensioneFile, BigDecimal idFormatoFileStandard) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT estensioneFile FROM DecEstensioneFile estensioneFile WHERE estensioneFile.cdEstensioneFile = :cdEstensioneFile");
        if (idFormatoFileStandard != null) {
            queryStr.append(" AND estensioneFile.decFormatoFileStandard.idFormatoFileStandard=:idFormatoFileStandard");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("cdEstensioneFile", cdEstensioneFile);
        if (idFormatoFileStandard != null) {
            query.setParameter("idFormatoFileStandard", longFromBigDecimal(idFormatoFileStandard));
        }

        List<DecEstensioneFile> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<DecFormatoFileStandard> retrieveDecFormatoFileStandardList(String nmFormatoFileStandard,
            String nmMimetypeFile) {
        String whereWord = " WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT formatoFileStandard FROM DecFormatoFileStandard formatoFileStandard "
                        + "LEFT JOIN formatoFileStandard.decEstensioneFiles estensioneFile ");

        if (StringUtils.isNotBlank(nmFormatoFileStandard)) {
            queryStr.append(whereWord).append(" (UPPER(formatoFileStandard.nmFormatoFileStandard) LIKE :nmformato "
                    + "OR UPPER(estensioneFile.cdEstensioneFile) LIKE :nmformato) ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            queryStr.append(whereWord).append(" formatoFileStandard.nmMimetypeFile = :nmMimetypeFile ");
        }
        queryStr.append("ORDER BY formatoFileStandard.nmFormatoFileStandard ASC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (StringUtils.isNotBlank(nmFormatoFileStandard)) {
            query.setParameter("nmformato", "%" + nmFormatoFileStandard.toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            query.setParameter("nmMimetypeFile", nmMimetypeFile);
        }
        return query.getResultList();
    }

    public List<DecEstensioneFile> retrieveDecEstensioneFileList(BigDecimal idFormatoFileStandard) {
        Query query = getEntityManager().createQuery(
                "SELECT estensioneFile FROM DecEstensioneFile estensioneFile WHERE estensioneFile.decFormatoFileStandard.idFormatoFileStandard=:idFormatoFileStandard ORDER BY estensioneFile.cdEstensioneFile");
        query.setParameter("idFormatoFileStandard", longFromBigDecimal(idFormatoFileStandard));
        return query.getResultList();

    }

    public DecFormatoFileBusta getDecFormatoFileBustaByName(String tiFormatoFirmaMarca,
            BigDecimal idFormatoFileStandard) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT formatoFileBusta FROM DecFormatoFileBusta formatoFileBusta WHERE formatoFileBusta.tiFormatoFirmaMarca = :tiFormatoFirmaMarca");
        if (idFormatoFileStandard != null) {
            queryStr.append(
                    " AND formatoFileBusta.decFormatoFileStandard.idFormatoFileStandard=:idFormatoFileStandard");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("tiFormatoFirmaMarca", tiFormatoFirmaMarca);

        if (idFormatoFileStandard != null) {
            query.setParameter("idFormatoFileStandard", longFromBigDecimal(idFormatoFileStandard));
        }

        List<DecFormatoFileBusta> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<DecFormatoFileBusta> getDecFormatoFileBustaList(BigDecimal idFormatoFileStandard) {
        Query query = getEntityManager().createQuery(
                "SELECT formatoFileBusta FROM DecFormatoFileBusta formatoFileBusta WHERE formatoFileBusta.decFormatoFileStandard.idFormatoFileStandard=:idFormatoFileStandard");
        query.setParameter("idFormatoFileStandard", longFromBigDecimal(idFormatoFileStandard));
        return query.getResultList();
    }

    public List<String> getDecFormatoFileStandardNameList(BigDecimal idFormatoFileDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT formatoFileStandard.nmFormatoFileStandard FROM DecUsoFormatoFileStandard usoFormatoFileStandard "
                        + "JOIN usoFormatoFileStandard.decFormatoFileStandard formatoFileStandard "
                        + "WHERE usoFormatoFileStandard.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc ");
        query.setParameter("idFormatoFileDoc", longFromBigDecimal(idFormatoFileDoc));
        return query.getResultList();
    }

    public List<DecFormatoGruppoProprieta> getAllDecFormatoGruppoProprieta() {
        /* recupero tutti i parametri previsti per la valutazione */
        return getEntityManager()
                .createQuery("SELECT g FROM DecFormatoGruppoProprieta g  ORDER BY g.nmFormatoGruppoProprieta")
                .getResultList();
    }

    public List<DecFormatoValutazione> getValutazioniPerFormato(Long idFormato) {
        /* cerco le valutazioni effettivamente presenti per questo formato */
        Query queryValutazioni = getEntityManager()
                .createQuery("SELECT v " + " FROM DecFormatoValutazione v JOIN v.decFormatoFileStandard formato "
                        + " WHERE formato.idFormatoFileStandard = :idFormatoFileStandard");
        queryValutazioni.setParameter("idFormatoFileStandard", idFormato);
        return queryValutazioni.getResultList();
    }

    public List<DecFormatoValutazione> getValutazioniPerFormato(DecFormatoFileStandard formato) {
        /* cerco le valutazioni effettivamente presenti per questo formato */
        Query queryValutazioni = getEntityManager().createQuery("SELECT v "
                + " FROM DecFormatoValutazione v JOIN v.decFormatoFileStandard formato " + " WHERE formato = :formato");
        queryValutazioni.setParameter("formato", formato);
        return queryValutazioni.getResultList();
    }

    public List<DecFormatoProprieta> getDecFormatoProprietaByGruppo(Long idGruppoProprieta) {
        Query queryValutazioni = getEntityManager()
                .createQuery("SELECT p  FROM DecFormatoProprieta p JOIN p.decFormatoGruppoProprieta g "
                        + " WHERE g.idFormatoGruppoProprieta = :idFormatoGruppoProprieta ORDER BY p.niPunteggioDefault, p.nmFormatoProprieta");
        queryValutazioni.setParameter("idFormatoGruppoProprieta", idGruppoProprieta);
        return queryValutazioni.getResultList();
    }

    public BigDecimal calcolaValutazione(DecFormatoFileStandard formato) {
        Query query = getEntityManager().createQuery("SELECT SUM(v.niPunteggio) as totale FROM DecFormatoValutazione v "
                + "JOIN v.decFormatoFileStandard formato " + "WHERE formato = :formato "
                + "AND v.flgInteroperabilita = 1");
        query.setParameter("formato", formato);
        return (BigDecimal) query.getSingleResult();
    }

    public List<String> getMimetypeList() {
        Query query = getEntityManager().createQuery("SELECT DISTINCT formatoFileStandard.nmMimetypeFile "
                + "FROM DecFormatoFileStandard formatoFileStandard " + "ORDER BY formatoFileStandard.nmMimetypeFile ");
        return (List<String>) query.getResultList();
    }//

    public List<DecFormatoFileStandard> getDecFormatoFileStandardList() {
        Query query = getEntityManager().createQuery("SELECT u FROM DecFormatoFileStandard u ");
        return (List<DecFormatoFileStandard>) query.getResultList();
    }

    public List<String> getFormatiFileStandardConcatenabili() {
        Query query = getEntityManager().createQuery(
                "SELECT u.nmFormatoFileStandard FROM DecFormatoFileStandard u WHERE u.flFormatoConcat = '1' ");
        return (List<String>) query.getResultList();
    }

    //
    public DecFormatoFileAmmesso getDecFormatoFileAmmesso(long idFormatoFileDoc, long idTipoCompDoc) {
        List<DecFormatoFileAmmesso> formatoFileAmmessoList = new ArrayList<>();
        Query query = getEntityManager()
                .createQuery("SELECT formatoFileAmmesso FROM DecFormatoFileAmmesso formatoFileAmmesso "
                        + "WHERE formatoFileAmmesso.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc "
                        + "AND formatoFileAmmesso.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc);
        query.setParameter("idTipoCompDoc", idTipoCompDoc);

        formatoFileAmmessoList = (List<DecFormatoFileAmmesso>) query.getResultList();
        if (!formatoFileAmmessoList.isEmpty()) {
            return formatoFileAmmessoList.get(0);
        }
        return null;
    }

    public List<Object[]> getFormatoFileBustaList() {
        Query query = getEntityManager().createQuery(
                "SELECT busta.tiFormato, busta.dsFormato FROM DecFmtFirBusta busta ORDER BY busta.tiFormato ");
        return (List<Object[]>) query.getResultList();
    }

    public Object getDsFormatoFileBusta(String tiFormato) {
        Query query = getEntityManager()
                .createQuery("SELECT busta.dsFormato FROM DecFmtFirBusta busta WHERE busta.tiFormato = :tiFormato ");
        query.setParameter("tiFormato", tiFormato);
        return query.getSingleResult();
    }
}
