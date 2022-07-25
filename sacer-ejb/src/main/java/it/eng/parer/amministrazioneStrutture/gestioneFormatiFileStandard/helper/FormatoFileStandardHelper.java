package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper;

import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileBusta;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecFormatoGruppoProprieta;
import it.eng.parer.entity.DecFormatoProprieta;
import it.eng.parer.entity.DecFormatoValutazione;
import it.eng.parer.helper.GenericHelper;
import it.eng.spagoLite.form.base.BaseElements;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper dei formati file standard dell'applicazione
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class FormatoFileStandardHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(FormatoFileStandardHelper.class);

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
                queryStr.append(" AND UPPER(formato.nmFormatoFileStandard) IN :nmformati");
            } else {
                queryStr.append(" WHERE UPPER(ext.cdEstensioneFile) IN :nmformati ");
            }
        }

        queryStr.append(" AND formato.nmFormatoFileStandard =  formatoFileDoc.nmFormatoFileDoc "
                + " AND formatoFileDoc.orgStrut.idStrut = :idStrut ");
        queryStr.append(" ORDER BY ext.cdEstensioneFile ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formatiUpperCase);
        }
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }

    public List<Object[]> getDecFormatoFileStandardInListByName(Collection<String> formati) {

        Set<String> formatiUpperCase = new HashSet<>();

        for (String string : formati) {
            formatiUpperCase.add(string.toUpperCase());
        }

        Query query = getEntityManager().createQuery(
                "SELECT formato,ext.cdEstensioneFile  FROM DecEstensioneFile ext JOIN ext.decFormatoFileStandard formato WHERE formato.flFormatoConcat = '1' AND UPPER(ext.cdEstensioneFile) IN :nmformati ");

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
            // if (status.equals(BaseElements.Status.update)) {
            // queryStr.append(" AND UPPER(ext.cdEstensioneFile) NOT IN :nmformati");
            // } else {
            // queryStr.append(" WHERE UPPER(ext.cdEstensioneFile) NOT IN :nmformati ");
            // }
            if (status.equals(BaseElements.Status.update)) {
                queryStr.append(" AND UPPER(formato.nmFormatoFileStandard) NOT IN :nmformati");
            } else {
                queryStr.append(" WHERE UPPER(formato.nmFormatoFileStandard) NOT IN :nmformati ");
            }
        }

        // queryStr.append(" ORDER BY ext.cdEstensioneFile ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (!formati.isEmpty()) {
            query.setParameter("nmformati", formatiUpperCase);
        }
        return query.getResultList();
    }

    // public BigDecimal getDecFormatoFileStandardIdFromEstensioneFile(String cdEstensioneFile) {
    // Query query = getEntityManager().createQuery(
    // "SELECT formatoFileStandard.idFormatoFileStandard FROM DecEstensioneFile estensioneFile JOIN"
    // + " estensioneFile.decFormatoFileStandard formatoFileStandard WHERE UPPER(estensioneFile.cdEstensioneFile) =
    // :cdEstensioneFile");
    // query.setParameter("cdEstensioneFile", cdEstensioneFile.toUpperCase());
    //
    // List<Long> list = query.getResultList();
    // Long id = list.get(0);
    //
    // return new BigDecimal(id);
    // }

    public DecFormatoFileStandard getDecFormatoFileStandardByName(String nmFormato) {
        Query query = getEntityManager().createQuery(
                "SELECT formato FROM DecFormatoFileStandard formato WHERE formato.nmFormatoFileStandard = :nmFormato");
        query.setParameter("nmFormato", nmFormato);
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
        String nmFormatoFileStandard = (String) query.getSingleResult();

        return nmFormatoFileStandard;
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
            query.setParameter("idFormatoFileStandard", idFormatoFileStandard);
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
                        + "LEFT JOIN formatoFileStandard.decEstensioneFiles estensioneFile "
        /* + "LEFT JOIN  formatoFileStandard.decFormatoValutaziones formatoValutazione " */);

        if (StringUtils.isNotBlank(nmFormatoFileStandard)) {
            queryStr.append(whereWord).append(" (UPPER(formatoFileStandard.nmFormatoFileStandard) LIKE :nmformato "
                    + "OR UPPER(estensioneFile.cdEstensioneFile) LIKE :nmformato) ");
            whereWord = " AND ";
        }
        if (StringUtils.isNotBlank(nmMimetypeFile)) {
            queryStr.append(whereWord).append(" formatoFileStandard.nmMimetypeFile = :nmMimetypeFile ");
        }
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
                "SELECT estensioneFile FROM DecEstensioneFile estensioneFile WHERE estensioneFile.decFormatoFileStandard.idFormatoFileStandard=:idFormatoFileStandard");
        query.setParameter("idFormatoFileStandard", idFormatoFileStandard);
        List<DecEstensioneFile> list = query.getResultList();
        return list;

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
            query.setParameter("idFormatoFileStandard", idFormatoFileStandard);
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
        query.setParameter("idFormatoFileStandard", idFormatoFileStandard);
        List<DecFormatoFileBusta> list = query.getResultList();
        return list;
    }

    public List<String> getDecFormatoFileStandardNameList(BigDecimal idFormatoFileDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT formatoFileStandard.nmFormatoFileStandard FROM DecUsoFormatoFileStandard usoFormatoFileStandard "
                        + "JOIN usoFormatoFileStandard.decFormatoFileStandard formatoFileStandard "
                        + "WHERE usoFormatoFileStandard.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc ");
        query.setParameter("idFormatoFileDoc", idFormatoFileDoc.longValue());
        List<String> list = (List<String>) query.getResultList();
        return list;
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

    public List<DecFormatoProprieta> getDecFormatoProprietaByGruppo(Long idGruppoProprieta) {
        Query queryValutazioni = getEntityManager()
                .createQuery("SELECT p  FROM DecFormatoProprieta p JOIN p.decFormatoGruppoProprieta g "
                        + " WHERE g.idFormatoGruppoProprieta = :idFormatoGruppoProprieta ORDER BY p.niPunteggioDefault, p.nmFormatoProprieta");
        queryValutazioni.setParameter("idFormatoGruppoProprieta", idGruppoProprieta);
        return queryValutazioni.getResultList();
    }

    public BigDecimal calcolaValutazione(DecFormatoFileStandard formato) {
        Query query = getEntityManager().createQuery(
                "SELECT SUM(v.niPunteggio) as totale FROM DecFormatoValutazione v JOIN v.decFormatoFileStandard formato "
                        + " WHERE formato = :formato");
        query.setParameter("formato", formato);
        return (BigDecimal) query.getSingleResult();
    }

    public List<String> getMimetypeList() {
        Query query = getEntityManager().createQuery("SELECT DISTINCT formatoFileStandard.nmMimetypeFile "
                + "FROM DecFormatoFileStandard formatoFileStandard " + "ORDER BY formatoFileStandard.nmMimetypeFile ");
        return (List<String>) query.getResultList();
    }

}
