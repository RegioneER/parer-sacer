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

package it.eng.parer.serie.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCampoInpUd;
import it.eng.parer.entity.DecCampoOutSelUd;
import it.eng.parer.entity.DecFiltroSelUd;
import it.eng.parer.entity.DecFiltroSelUdDato;
import it.eng.parer.entity.DecOutSelUd;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerContenutoVerSerie;
import it.eng.parer.entity.SerFileInputVerSerie;
import it.eng.parer.entity.SerFileVerSerie;
import it.eng.parer.entity.SerIxVolVerSerie;
import it.eng.parer.entity.SerLacunaConsistVerSerie;
import it.eng.parer.entity.SerQueryContenutoVerSerie;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.entity.SerStatoSerie;
import it.eng.parer.entity.SerStatoVerSerie;
import it.eng.parer.entity.SerUrnFileVerSerie;
import it.eng.parer.entity.SerUrnIxVolVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.entity.constraint.SerUrnFileVerSerie.TiUrnFileVerSerie;
import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie.TiUrnIxVolVerSerie;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.serie.dto.RicercaSerieBean;
import it.eng.parer.serie.dto.RicercaUdAppartBean;
import it.eng.parer.slite.gen.viewbean.SerVLisErrFileSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartVolSerieTableBean;
import it.eng.parer.viewEntity.ResultVCalcoloSerieUd;
import it.eng.parer.viewEntity.SerVBucoNumerazioneUd;
import it.eng.parer.viewEntity.SerVJobContenutoBloccato;
import it.eng.parer.viewEntity.SerVJobVerSerieBloccato;
import it.eng.parer.viewEntity.SerVLisErrContenSerieUd;
import it.eng.parer.viewEntity.SerVLisErrFileSerieUd;
import it.eng.parer.viewEntity.SerVLisNotaSerie;
import it.eng.parer.viewEntity.SerVLisSerDaValidare;
import it.eng.parer.viewEntity.SerVLisStatoSerie;
import it.eng.parer.viewEntity.SerVLisUdAppartSerie;
import it.eng.parer.viewEntity.SerVLisUdErrFileInput;
import it.eng.parer.viewEntity.SerVLisUdNovers;
import it.eng.parer.viewEntity.SerVLisVerSeriePrec;
import it.eng.parer.viewEntity.SerVLisVolSerieUd;
import it.eng.parer.viewEntity.SerVRicConsistSerieUd;
import it.eng.parer.viewEntity.SerVRicSerieUd;
import it.eng.parer.viewEntity.SerVRicSerieUdUsr;
import it.eng.parer.viewEntity.SerVSelUdNovers;
import it.eng.parer.viewEntity.SerVSelUdNoversBuco;
import it.eng.parer.viewEntity.SerVVisContenutoSerieUd;
import it.eng.parer.viewEntity.SerVVisSerieUd;
import it.eng.parer.viewEntity.SerVVisVolVerSerieUd;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings({
        "rawtypes", "unchecked" })
@Stateless
@LocalBean
public class SerieHelper extends GenericHelper {

    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    private LazyListHelper lazyListHelper;
    private static final Logger log = LoggerFactory.getLogger(SerieHelper.class);

    public SerContenutoVerSerie getLockSerContenutoVerSerie(Long idVerSerie,
            String tiContenutoVerSerie) {
        SerContenutoVerSerie contenuto = null;
        try {
            Query query = getEntityManager().createQuery(
                    "SELECT cv FROM SerContenutoVerSerie cv WHERE cv.serVerSerie.idVerSerie = :idVerSerie AND cv.tiContenutoVerSerie = :tiContenutoVerSerie");
            query.setParameter("idVerSerie", idVerSerie);
            query.setParameter("tiContenutoVerSerie", tiContenutoVerSerie);
            contenuto = (SerContenutoVerSerie) query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
        } catch (LockTimeoutException lte) {
            log.info("CREAZIONE SERIE --- Impossibile acquisire il lock sul contenuto", lte);
        }
        return contenuto;
    }

    public SerContenutoVerSerie getSerContenutoVerSerie(Long idVerSerie,
            String tiContenutoVerSerie) {
        SerContenutoVerSerie contenuto = null;
        Query query = getEntityManager().createQuery(
                "SELECT cv FROM SerContenutoVerSerie cv WHERE cv.serVerSerie.idVerSerie = :idVerSerie AND cv.tiContenutoVerSerie = :tiContenutoVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiContenutoVerSerie", tiContenutoVerSerie);
        List<SerContenutoVerSerie> list = query.getResultList();
        if (!list.isEmpty()) {
            contenuto = list.get(0);
        }
        return contenuto;
    }

    public SerSerie getSerSerie(BigDecimal idTipoSerie, BigDecimal idStrut, BigDecimal aaSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerSerie s WHERE s.orgStrut.idStrut = :idStrut AND s.decTipoSerie.idTipoSerie = :idTipoSerie AND s.aaSerie = :aaSerie");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("aaSerie", aaSerie);
        List<SerSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Long countSerie(BigDecimal idStrut, BigDecimal idTipoSerie, BigDecimal aaSerie,
            Date dataInizio, Date dataFine, String cdSerie) {
        StringBuilder queryStr = new StringBuilder("SELECT count(serie) FROM ");
        if (dataInizio != null && dataFine != null) {
            queryStr.append(" SerVerSerie vers JOIN vers.serSerie serie ");
        } else {
            queryStr.append(" SerSerie serie ");
        }

        queryStr.append(
                " WHERE serie.orgStrut.idStrut = :idStrut AND serie.aaSerie = :aaSerie AND serie.dtAnnul = :defaultAnnul ");

        if (idTipoSerie != null) {
            queryStr.append(" AND serie.decTipoSerie.idTipoSerie = :idTipoSerie ");
        }

        if (dataInizio != null && dataFine != null) {
            queryStr.append(" AND vers.pgVerSerie = ("
                    + "SELECT MAX(versCorr.pgVerSerie) FROM SerVerSerie versCorr WHERE versCorr.serSerie.idSerie = serie.idSerie"
                    + ") "
                    + " AND ((vers.dtInizioSelSerie IS NULL AND vers.dtFineSelSerie IS NULL) OR "
                    + "(vers.dtInizioSelSerie <= :dataFine AND vers.dtFineSelSerie >= :dataInizio))");
        }
        if (StringUtils.isNotBlank(cdSerie)) {
            queryStr.append(" AND serie.cdCompositoSerie = :cdSerie");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("aaSerie", aaSerie);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());
        if (idTipoSerie != null) {
            query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        }
        if (dataInizio != null && dataFine != null) {
            query.setParameter("dataInizio", dataInizio);
            query.setParameter("dataFine", dataFine);
        }
        if (StringUtils.isNotBlank(cdSerie)) {
            query.setParameter("cdSerie", cdSerie);
        }
        return (Long) query.getSingleResult();
    }

    public SerVerSerie getLastSerVerSerie(BigDecimal idSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT v FROM SerVerSerie v WHERE v.serSerie.idSerie = :idSerie ORDER BY v.pgVerSerie DESC");
        query.setParameter("idSerie", longFromBigDecimal(idSerie));
        List<SerVerSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SerStatoVerSerie getLastSerStatoVerSerie(Long idVerSerie) {
        return getLastSerStatoVerSerieWithStatus(idVerSerie, null);
    }

    public SerStatoVerSerie getLastSerStatoVerSerieWithStatus(Long idVerSerie,
            String tiStatoVerSerie) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerStatoVerSerie s WHERE s.serVerSerie.idVerSerie = :idVerSerie");
        if (StringUtils.isNotBlank(tiStatoVerSerie)) {
            queryStr.append(" AND s.tiStatoVerSerie = :tiStatoVerSerie");
        }
        queryStr.append(" ORDER BY s.pgStatoVerSerie DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idVerSerie", idVerSerie);
        if (StringUtils.isNotBlank(tiStatoVerSerie)) {
            query.setParameter("tiStatoVerSerie", tiStatoVerSerie);
        }
        List<SerStatoVerSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SerStatoSerie getLastSerStatoSerie(Long idSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerStatoSerie s WHERE s.serSerie.idSerie = :idSerie ORDER BY s.pgStatoSerie DESC");
        query.setParameter("idSerie", idSerie);
        List<SerStatoSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SerFileInputVerSerie getSerFileInputVerSerie(Long idVerSerie,
            String tiScopoFileInputVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT f FROM SerFileInputVerSerie f WHERE f.serVerSerie.idVerSerie = :idVerSerie AND f.tiScopoFileInputVerSerie = :tiScopoFileInputVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiScopoFileInputVerSerie", tiScopoFileInputVerSerie);
        List<SerFileInputVerSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public List<DecTipoSerieUd> getDecTipoSerieUd(Long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecTipoSerieUd d WHERE d.decTipoSerie.idTipoSerie = :idTipoSerie ");
        query.setParameter("idTipoSerie", idTipoSerie);
        return query.getResultList();
    }

    public List<DecTipoSerieUd> getDecTipoSerieUdNoSelUnitaDocAnnul(Long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecTipoSerieUd d WHERE d.decTipoSerie.idTipoSerie = :idTipoSerie AND d.flSelUnitaDocAnnul = '0'");
        query.setParameter("idTipoSerie", idTipoSerie);
        return query.getResultList();
    }

    public List<DecOutSelUd> getDecOutSelUd(Long idTipoSerieUd) {
        Query query = getEntityManager().createQuery(
                "SELECT o FROM DecOutSelUd o WHERE o.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd");
        query.setParameter("idTipoSerieUd", idTipoSerieUd);
        return query.getResultList();
    }

    public List<Long> getDecFiltroSelUdSelTables(Long idTipoSerieUd) {
        Query query = getEntityManager().createQuery(
                "SELECT f.decTipoDoc.idTipoDoc FROM DecFiltroSelUd f WHERE f.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd AND f.tiFiltro = 'TIPO_DOC_PRINC'");
        query.setParameter("idTipoSerieUd", idTipoSerieUd);
        return query.getResultList();
    }

    public List<DecFiltroSelUd> getDecFiltroSelUd(Long idTipoSerieUd) {
        Query query = getEntityManager().createQuery(
                "SELECT f FROM DecFiltroSelUd f WHERE f.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd AND f.tiFiltro != 'TIPO_DOC_PRINC'");
        query.setParameter("idTipoSerieUd", idTipoSerieUd);
        return query.getResultList();
    }

    public List<DecFiltroSelUdDato> getDecFiltroSelUdDato(Long idTipoSerieUd,
            String nmTipoUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecFiltroSelUdDato d JOIN d.decFiltroSelUdAttb f WHERE f.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd AND d.tiEntitaSacer = 'UNI_DOC' AND d.nmTipoUnitaDoc = :nmTipoUnitaDoc");
        query.setParameter("idTipoSerieUd", idTipoSerieUd);
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        return query.getResultList();
    }

    public List<DecFiltroSelUdDato> getDecFiltroSelUdDato(Long idTipoSerieUd, Long idTipoDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecFiltroSelUdDato d JOIN d.decFiltroSelUdAttb f JOIN d.decAttribDatiSpec ds JOIN ds.decTipoDoc td WHERE td.idTipoDoc = :idTipoDoc AND ds.tiUsoAttrib = 'VERS' AND ds.tiEntitaSacer = 'DOC' AND d.tiEntitaSacer = 'DOC' AND f.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd");
        query.setParameter("idTipoSerieUd", idTipoSerieUd);
        query.setParameter("idTipoDoc", idTipoDoc);
        return query.getResultList();
    }

    public List<DecCampoOutSelUd> getDecCampoOutSelUd(Long idOutSelUd) {
        Query query = getEntityManager().createQuery(
                "SELECT c FROM DecCampoOutSelUd c WHERE c.decOutSelUd.idOutSelUd = :idOutSelUd");
        query.setParameter("idOutSelUd", idOutSelUd);
        return query.getResultList();
    }

    /**
     * @param queryString la query
     *
     * @return Lista di {@link ResultVCalcoloSerieUd}
     */
    public List<ResultVCalcoloSerieUd> executeQueryList(String queryString) {
        org.hibernate.query.Query query = getEntityManager().createNativeQuery(queryString)
                .unwrap(org.hibernate.query.Query.class);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> resultMap = query.list();
        return resultMap.stream().map(ResultVCalcoloSerieUd::new).collect(Collectors.toList());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ResultVCalcoloSerieUd> executeQueryListWithException(String queryString) {
        List<ResultVCalcoloSerieUd> result = null;
        try {
            result = executeQueryList(queryString);
        } catch (PersistenceException e) {
            log.error("Eccezione", e);
        }
        return result;
    }

    public Long getUdAppartVerSerie(Long idContenutoVerSerie, boolean first) {
        Query query = getEntityManager().createQuery(
                "SELECT a.idUdAppartVerSerie FROM AroUdAppartVerSerie a WHERE a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie ORDER BY a.dsKeyOrdUdSerie "
                        + (first ? "ASC" : "DESC"));
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        List<Long> list = query.getResultList();
        Long result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public Date getMinDtUnitaDocFromUdAppartVerSerie(Long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT MIN(a.dtUdSerie) FROM AroUdAppartVerSerie a WHERE a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        List<Date> list = query.getResultList();
        Date result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public Date getMaxDtUnitaDocFromUdAppartVerSerie(Long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(a.dtUdSerie) FROM AroUdAppartVerSerie a WHERE a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        List<Date> list = query.getResultList();
        Date result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public List<DecCampoInpUd> getDecCampoInpUd(Long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT c FROM DecCampoInpUd c WHERE c.decTipoSerie.idTipoSerie = :idTipoSerie ORDER BY c.pgOrdCampo");
        query.setParameter("idTipoSerie", idTipoSerie);
        return query.getResultList();
    }

    public List<DecCampoInpUd> getDecCampoInpUdDatoProfilo(Long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT c FROM DecCampoInpUd c WHERE c.decTipoSerie.idTipoSerie = :idTipoSerie AND c.tiCampo = 'DATO_PROFILO' ORDER BY c.pgOrdCampo");
        query.setParameter("idTipoSerie", idTipoSerie);
        return query.getResultList();
    }

    public List<DecCampoInpUd> getDecCampoInpUdDatiSpec(Long idTipoSerie, Long idTipoUnitaDoc,
            Long idTipoDoc) {
        String tiEntitaSacerEndingQuery = null;
        String tiCampo = null;
        if (idTipoUnitaDoc != null) {
            tiCampo = "DATO_SPEC_UNI_DOC";
            tiEntitaSacerEndingQuery = "'UNI_DOC' AND a.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc";
        } else if (idTipoDoc != null) {
            tiCampo = "DATO_SPEC_DOC_PRINC";
            tiEntitaSacerEndingQuery = "'DOC' AND a.decTipoDoc.idTipoDoc = :idTipoDoc";
        }

        Query query = getEntityManager().createQuery(
                "SELECT c FROM DecCampoInpUd c JOIN c.decAttribDatiSpec a WHERE c.decTipoSerie.idTipoSerie = :idTipoSerie AND c.tiCampo = '"
                        + tiCampo + "' AND a.tiUsoAttrib = 'VERS' AND a.tiEntitaSacer = "
                        + tiEntitaSacerEndingQuery + " ORDER BY c.pgOrdCampo");
        query.setParameter("idTipoSerie", idTipoSerie);
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        } else if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", idTipoDoc);
        }
        return query.getResultList();
    }

    public boolean existUdInSerie(Long idContenutoVerSerie, BigDecimal idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(a) FROM AroUdAppartVerSerie a WHERE a.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("idUnitaDoc", longFromBigDecimal(idUnitaDoc));
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    public SerVVisSerieUd getSerVVisSerieUd(BigDecimal idVerSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT s FROM SerVVisSerieUd s WHERE s.idVerSerie = :idVerSerie ");
        query.setParameter("idVerSerie", idVerSerie);
        List<SerVVisSerieUd> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SerVVisVolVerSerieUd getSerVVisVolVerSerieUd(BigDecimal idVolVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVVisVolVerSerieUd s WHERE s.idVolVerSerie = :idVolVerSerie ");
        query.setParameter("idVolVerSerie", idVolVerSerie);
        List<SerVVisVolVerSerieUd> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // EVO#16486
    public List<SerUrnFileVerSerie> getUrnFileVerSerieByTipiUrn(BigDecimal idVerSerie,
            List<TiUrnFileVerSerie> tiUrn) {
        Query query = getEntityManager().createQuery("SELECT s FROM SerUrnFileVerSerie s "
                + "JOIN s.serFileVerSerie fileVerSerie " + "JOIN fileVerSerie.serVerSerie verSerie "
                + "WHERE verSerie.idVerSerie = :idVerSerie " + "AND s.tiUrn IN :tiUrn");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.setParameter("tiUrn", tiUrn);
        return query.getResultList();
    }

    public SerUrnFileVerSerie getUrnFileVerSerie(BigDecimal idVerSerie,
            List<TiUrnFileVerSerie> tiUrn, String tiFile) {
        Query query = getEntityManager().createQuery("SELECT s FROM SerUrnFileVerSerie s "
                + "JOIN s.serFileVerSerie fileVerSerie " + "JOIN fileVerSerie.serVerSerie verSerie "
                + "WHERE verSerie.idVerSerie = :idVerSerie "
                + "AND s.tiUrn IN :tiUrn AND fileVerSerie.tiFileVerSerie = :tiFile");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.setParameter("tiUrn", tiUrn);
        query.setParameter("tiFile", tiFile);
        return (SerUrnFileVerSerie) query.getSingleResult();
    }

    public SerFileVerSerie getFileVerSerieByTipoFile(BigDecimal idVerSerie,
            CostantiDB.TipoFileVerSerie tipoFileVerSerie) {
        Query query = getEntityManager().createQuery("SELECT s FROM SerFileVerSerie s "
                + "WHERE s.serVerSerie.idVerSerie = :idVerSerie AND s.tiFileVerSerie IN :tiFileVerSerie");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.setParameter("tiFileVerSerie", tipoFileVerSerie.name());
        List<SerFileVerSerie> l = query.getResultList();
        return (l != null && !l.isEmpty()) ? l.get(0) : null;
    }

    public List<SerUrnIxVolVerSerie> getUrnIxVolVerSerieByTipiUrn(BigDecimal idVolVerSerie,
            List<TiUrnIxVolVerSerie> tiUrn) {
        Query query = getEntityManager().createQuery("SELECT s FROM SerUrnIxVolVerSerie s "
                + "JOIN s.serIxVolVerSerie ixVolVerSerie "
                + "JOIN ixVolVerSerie.serVolVerSerie volVerSerie "
                + "WHERE volVerSerie.idVolVerSerie = :idVolVerSerie " + "AND s.tiUrn IN :tiUrn");
        query.setParameter("idVolVerSerie", longFromBigDecimal(idVolVerSerie));
        query.setParameter("tiUrn", tiUrn);
        return query.getResultList();
    }

    public List<SerVRicSerieUd> getSerVRicSerieUd(List<BigDecimal> verSeries, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVRicSerieUd s WHERE s.idStrut = :idStrut AND s.idVerSerie IN (:verSeries) ORDER BY s.cdCompositoSerie, s.aaSerie");
        query.setParameter("verSeries", verSeries);
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }

    public List<SerVRicSerieUd> getSerVRicSerieUd(long idUserIam, RicercaSerieBean filtri) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerVRicSerieUd s, IamAbilOrganiz abilOrganiz WHERE s.idStrut = abilOrganiz.idOrganizApplic "
                        + "AND abilOrganiz.iamUser.idUserIam = :idUserIam AND s.idAmbiente = :idAmbiente");
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            queryStr.append(clause).append("s.cdCompositoSerie like :cdCompositoSerie ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            queryStr.append(clause).append("s.dsSerie like :dsSerie ");
        }
        if (!filtri.getTi_stato_cor_serie().isEmpty()) {
            if (filtri.getTi_stato_cor_serie().size() == 1) {
                queryStr.append(clause).append("s.tiStatoVerSerie = :tiStatoVerSerie ");
            } else {
                queryStr.append(clause).append("s.tiStatoVerSerie IN (:tiStatoVerSerie) ");
            }
        }
        if (!filtri.getTi_stato_conservazione().isEmpty()) {
            if (filtri.getTi_stato_conservazione().size() == 1) {
                queryStr.append(clause).append("s.tiStatoSerie = :tiStatoConservazione ");
            } else {
                queryStr.append(clause).append("s.tiStatoSerie IN (:tiStatoConservazione) ");
            }
        }
        if (filtri.getId_tipo_serie() != null) {
            queryStr.append(clause).append("s.idTipoSerie = :idTipoSerie ");
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            queryStr.append(clause).append("s.aaSerie BETWEEN :aaSerieDa AND :aaSerieA ");
        } else {
            queryStr.append(clause).append("s.aaSerie <= :aaSerieA ");
        }

        if (filtri.getDt_inizio_serie() != null) {
            queryStr.append(clause).append("s.dtInizioSelSerie >= :dtInizio ");
        }
        if (filtri.getDt_fine_serie() != null) {
            queryStr.append(clause).append("s.dtFineSelSerie <= :dtFine ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_da_rigenera())) {
            queryStr.append(clause).append("s.flDaRigenera = :flDaRigenera");
        }
        if (filtri.getId_ente() != null) {
            queryStr.append(clause).append("s.idEnte = :idEnte ");
        }
        if (filtri.getId_strut() != null) {
            queryStr.append(clause).append("s.idStrut = :idStrut ");
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_calc())) {
            queryStr.append(clause).append("s.tiStatoContenutoCalc = :tiStatoContenutoCalc");
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_calc())) {
            queryStr.append(clause).append("s.flErrContenutoCalc = :flErrContenutoCalc");
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_acq())) {
            queryStr.append(clause).append("s.tiStatoContenutoAcq = :tiStatoContenutoAcq");
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_file())) {
            queryStr.append(clause).append("s.flErrContenutoFile = :flErrContenutoFile");
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_acq())) {
            queryStr.append(clause).append("s.flErrContenutoAcq = :flErrContenutoAcq");
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_eff())) {
            queryStr.append(clause).append("s.tiStatoContenutoEff = :tiStatoContenutoEff");
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_eff())) {
            queryStr.append(clause).append("s.flErrContenutoEff = :flErrContenutoEff");
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_validazione())) {
            queryStr.append(clause).append("s.flErrValidazione = :flErrValidazione");
        }
        if (StringUtils.isNotBlank(filtri.getFl_elab_bloccata())) {
            if (filtri.getFl_elab_bloccata().equals("1")) {
                queryStr.append(clause).append("s.dsElabBloccata IS NOT NULL");
            } else {
                queryStr.append(clause).append("s.dsElabBloccata IS NULL");
            }
        }
        if (StringUtils.isNotBlank(filtri.getFl_presenza_consist_attesa())) {
            queryStr.append(clause).append("s.flPresenzaConsistAttesa = :flPresenzaConsistAttesa");
        }
        if (StringUtils.isNotBlank(filtri.getTi_crea_standard())) {
            queryStr.append(clause).append("s.tiCreaStandard = :tiCreaStandard");
        }
        if (filtri.getId_modello_tipo_serie() != null) {
            queryStr.append(clause).append("s.idModelloTipoSerie = :idModelloTipoSerie");
        }
        queryStr.append(" ORDER BY s.cdCompositoSerie, s.aaSerie");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", filtri.getId_ambiente());
        query.setParameter("idUserIam", idUserIam);
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            query.setParameter("cdCompositoSerie", '%' + filtri.getCd_composito_serie() + '%');
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            query.setParameter("dsSerie", '%' + filtri.getDs_serie() + '%');
        }
        if (!filtri.getTi_stato_cor_serie().isEmpty()) {
            if (filtri.getTi_stato_cor_serie().size() == 1) {
                query.setParameter("tiStatoVerSerie", filtri.getTi_stato_cor_serie().get(0));
            } else {
                query.setParameter("tiStatoVerSerie", filtri.getTi_stato_cor_serie());
            }
        }
        if (!filtri.getTi_stato_conservazione().isEmpty()) {
            if (filtri.getTi_stato_conservazione().size() == 1) {
                query.setParameter("tiStatoConservazione",
                        filtri.getTi_stato_conservazione().get(0));
            } else {
                query.setParameter("tiStatoConservazione", filtri.getTi_stato_conservazione());
            }
        }
        if (filtri.getId_tipo_serie() != null) {
            query.setParameter("idTipoSerie", filtri.getId_tipo_serie());
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            query.setParameter("aaSerieDa", filtri.getAa_serie_da());
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        } else {
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        }

        if (filtri.getDt_inizio_serie() != null) {
            query.setParameter("dtInizio", filtri.getDt_inizio_serie());
        }
        if (filtri.getDt_fine_serie() != null) {
            query.setParameter("dtFine", filtri.getDt_fine_serie());
        }
        if (StringUtils.isNotBlank(filtri.getFl_da_rigenera())) {
            query.setParameter("flDaRigenera", filtri.getFl_da_rigenera());
        }
        if (filtri.getId_ente() != null) {
            query.setParameter("idEnte", filtri.getId_ente());
        }
        if (filtri.getId_strut() != null) {
            query.setParameter("idStrut", filtri.getId_strut());
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_calc())) {
            query.setParameter("tiStatoContenutoCalc", filtri.getTi_stato_contenuto_calc());
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_calc())) {
            query.setParameter("flErrContenutoCalc", filtri.getFl_err_contenuto_calc());
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_acq())) {
            query.setParameter("tiStatoContenutoAcq", filtri.getTi_stato_contenuto_acq());
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_file())) {
            query.setParameter("flErrContenutoFile", filtri.getFl_err_contenuto_file());
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_acq())) {
            query.setParameter("flErrContenutoAcq", filtri.getFl_err_contenuto_acq());
        }
        if (StringUtils.isNotBlank(filtri.getTi_stato_contenuto_eff())) {
            query.setParameter("tiStatoContenutoEff", filtri.getTi_stato_contenuto_eff());
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_contenuto_eff())) {
            query.setParameter("flErrContenutoEff", filtri.getFl_err_contenuto_eff());
        }
        if (StringUtils.isNotBlank(filtri.getFl_err_validazione())) {
            query.setParameter("flErrValidazione", filtri.getFl_err_validazione());
        }
        if (StringUtils.isNotBlank(filtri.getFl_presenza_consist_attesa())) {
            query.setParameter("flPresenzaConsistAttesa", filtri.getFl_presenza_consist_attesa());
        }
        if (StringUtils.isNotBlank(filtri.getTi_crea_standard())) {
            query.setParameter("tiCreaStandard", filtri.getTi_crea_standard());
        }
        if (filtri.getId_modello_tipo_serie() != null) {
            query.setParameter("idModelloTipoSerie", filtri.getId_modello_tipo_serie());
        }
        return query.getResultList();
    }

    public List<SerVRicSerieUdUsr> getSerVRicSerieUdUsr(long idUserIam, RicercaSerieBean filtri) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerVRicSerieUdUsr s, IamAbilOrganiz abilOrganiz WHERE s.idStrut = abilOrganiz.idOrganizApplic "
                        + "AND abilOrganiz.iamUser.idUserIam = :idUserIam AND s.idAmbiente = :idAmbiente");
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            queryStr.append(clause).append("UPPER(s.cdCompositoSerie) like :cdCompositoSerie ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            queryStr.append(clause).append("UPPER(s.dsSerie) like :dsSerie ");
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            queryStr.append(clause).append("s.aaSerie BETWEEN :aaSerieDa AND :aaSerieA ");
        } else {
            queryStr.append(clause).append("s.aaSerie <= :aaSerieA ");
        }
        if (filtri.getId_ente() != null) {
            queryStr.append(clause).append("s.idEnte = :idEnte ");
        }
        if (filtri.getId_strut() != null) {
            queryStr.append(clause).append("s.idStrut = :idStrut ");
        }
        if (filtri.getId_tipo_unita_doc() != null) {
            queryStr.append(clause).append("s.idTipoUnitaDoc = :idTipoUnitaDoc");
        }
        if (filtri.getId_registro_unita_doc() != null) {
            queryStr.append(clause).append("s.idRegistroUnitaDoc = :idRegistroUnitaDoc");
        }
        queryStr.append(" ORDER BY s.cdCompositoSerie, s.aaSerie");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", filtri.getId_ambiente());
        query.setParameter("idUserIam", idUserIam);
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            query.setParameter("cdCompositoSerie",
                    '%' + filtri.getCd_composito_serie().toUpperCase() + '%');
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            query.setParameter("dsSerie", '%' + filtri.getDs_serie().toUpperCase() + '%');
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            query.setParameter("aaSerieDa", filtri.getAa_serie_da());
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        } else {
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        }
        if (filtri.getId_ente() != null) {
            query.setParameter("idEnte", filtri.getId_ente());
        }
        if (filtri.getId_strut() != null) {
            query.setParameter("idStrut", filtri.getId_strut());
        }
        if (filtri.getId_tipo_unita_doc() != null) {
            query.setParameter("idTipoUnitaDoc", filtri.getId_tipo_unita_doc());
        }
        if (filtri.getId_registro_unita_doc() != null) {
            query.setParameter("idRegistroUnitaDoc", filtri.getId_registro_unita_doc());
        }
        return query.getResultList();
    }

    public SerVJobContenutoBloccato getSerVJobContenutoBloccato(String nmJob,
            BigDecimal idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVJobContenutoBloccato s WHERE s.id.idContenutoVerSerie = :idContenutoVerSerie AND s.id.nmJob = :nmJob");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("nmJob", nmJob);
        List<SerVJobContenutoBloccato> list = query.getResultList();
        SerVJobContenutoBloccato row = null;
        if (!list.isEmpty()) {
            row = list.get(0);
        }
        return row;
    }

    public SerVJobVerSerieBloccato getSerVJobVerSerieBloccato(String nmJob, BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVJobVerSerieBloccato s WHERE s.id.idVerSerie = :idVerSerie AND s.id.nmJob = :nmJob");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("nmJob", nmJob);
        List<SerVJobVerSerieBloccato> list = query.getResultList();
        SerVJobVerSerieBloccato row = null;
        if (!list.isEmpty()) {
            row = list.get(0);
        }
        return row;
    }

    public List<SerVLisNotaSerie> getSerVLisNotaSerie(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisNotaSerie s WHERE s.idVerSerie = :idVerSerie ORDER BY s.niOrd, s.pgNotaVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public BigDecimal getMaxPgSerVLisNotaSerie(BigDecimal idVerSerie, BigDecimal idTipoNotaSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT MAX(s.pgNotaVerSerie) FROM SerVLisNotaSerie s WHERE s.idVerSerie = :idVerSerie AND s.idTipoNotaSerie = :idTipoNotaSerie ");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("idTipoNotaSerie", idTipoNotaSerie);
        return (BigDecimal) query.getSingleResult();
    }

    public List<SerVLisStatoSerie> getSerVLisStatoSerie(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisStatoSerie s WHERE s.idVerSerie = :idVerSerie ORDER BY s.pgStatoVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public List<SerVLisVerSeriePrec> getSerVLisVerSeriePrec(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisVerSeriePrec s WHERE s.idVerSerieInput = :idVerSerie ORDER BY s.pgVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public List<SerVLisVolSerieUd> getSerVLisVolSerieUd(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisVolSerieUd s WHERE s.idVerSerie = :idVerSerie ORDER BY s.pgVolVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public SerVLisUdAppartVolSerieTableBean getSerVLisUdAppartVolSerie(BigDecimal idVolVerSerie,
            RicercaUdAppartBean parametri,
            Function<List, SerVLisUdAppartVolSerieTableBean> toTableBeanFunc) {
        StringBuilder builder = new StringBuilder(
                "SELECT s FROM SerVLisUdAppartVolSerie s WHERE s.idVolVerSerie = :idVolVerSerie ");
        String clause = " AND ";
        if (parametri != null) {
            if (StringUtils.isNotBlank(parametri.getCdUdSerie())) {
                builder.append(clause).append("UPPER(s.cdUdSerie) like :cdUdSerie");
            }
            if (StringUtils.isNotBlank(parametri.getInfoUdSerie())) {
                builder.append(clause).append("UPPER(s.infoUdSerie) like :infoUdSerie");
            }
            if (parametri.getDtUdSerieDa() != null || parametri.getDtUdSerieA() != null) {
                builder.append(clause).append("s.dtUdSerie BETWEEN :dataDa AND :dataA");
            }
            if (parametri.getPgUdSerieDa() != null || parametri.getPgUdSerieA() != null) {
                builder.append(clause).append("s.pgUdSerie BETWEEN :pgDa AND :pgA");
            }
        }
        builder.append(" ORDER BY s.dsKeyOrdUdSerie");
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idVolVerSerie", idVolVerSerie);
        if (parametri != null) {
            if (StringUtils.isNotBlank(parametri.getCdUdSerie())) {
                query.setParameter("cdUdSerie", "%" + parametri.getCdUdSerie().toUpperCase() + "%");
            }
            if (StringUtils.isNotBlank(parametri.getInfoUdSerie())) {
                query.setParameter("infoUdSerie",
                        "%" + parametri.getInfoUdSerie().toUpperCase() + "%");
            }
            if (parametri.getDtUdSerieDa() != null && parametri.getDtUdSerieA() != null) {
                query.setParameter("dataDa", parametri.getDtUdSerieDa());
                query.setParameter("dataA", parametri.getDtUdSerieA());
            }
            if (parametri.getPgUdSerieDa() != null && parametri.getPgUdSerieA() != null) {
                query.setParameter("pgDa", parametri.getPgUdSerieDa());
                query.setParameter("pgA", parametri.getPgUdSerieA());
            }
        }
        return lazyListHelper.getTableBean(query, toTableBeanFunc);
    }

    public void deleteSerContenutoVerSerie(long idVerSerie, String tiContenutoVerSerie) {
        Query q = getEntityManager().createQuery(
                "DELETE FROM SerContenutoVerSerie cv WHERE cv.serVerSerie.idVerSerie = :idVerSerie AND cv.tiContenutoVerSerie = :tiContenutoVerSerie");
        q.setParameter("idVerSerie", idVerSerie);
        q.setParameter("tiContenutoVerSerie", tiContenutoVerSerie);
        q.executeUpdate();
    }

    public void deleteSerErrContenutoVerSerie(long idContenutoVerSerie) {
        Query q = getEntityManager().createQuery(
                "DELETE FROM SerErrContenutoVerSerie err WHERE err.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        q.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        q.executeUpdate();
    }

    public void deleteSerFileInputVerSerie(long idVerSerie, String tiScopoFileInputVerSerie) {
        Query q = getEntityManager().createQuery(
                "DELETE FROM SerFileInputVerSerie fv WHERE fv.serVerSerie.idVerSerie = :idVerSerie AND fv.tiScopoFileInputVerSerie = :tiScopoFileInputVerSerie");
        q.setParameter("idVerSerie", idVerSerie);
        q.setParameter("tiScopoFileInputVerSerie", tiScopoFileInputVerSerie);
        q.executeUpdate();
    }

    public SerVVisContenutoSerieUd getSerVVisContenutoSerieUd(BigDecimal idVerSerie,
            String tipoContenuto) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVVisContenutoSerieUd s WHERE s.idVerSerie = :idVerSerie AND s.tiContenutoVerSerie = :tipoContenuto");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tipoContenuto", tipoContenuto);
        List<SerVVisContenutoSerieUd> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public SerVLisUdAppartSerieTableBean getSerVLisUdAppartSerie(BigDecimal idContenutoVerSerie,
            RicercaUdAppartBean parametri,
            Function<List, SerVLisUdAppartSerieTableBean> toTableBeanFunc, boolean lazy) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerVLisUdAppartSerie s WHERE s.idContenutoVerSerie = :idContenutoVerSerie");
        String clause = " AND ";

        if (parametri != null) {
            if (StringUtils.isNotBlank(parametri.getCdUdSerie())) {
                queryStr.append(clause).append("UPPER(s.cdUdSerie) like :cdUdSerie");
            }
            if (StringUtils.isNotBlank(parametri.getInfoUdSerie())) {
                queryStr.append(clause).append("UPPER(s.infoUdSerie) like :infoUdSerie");
            }
            if (parametri.getDtUdSerieDa() != null || parametri.getDtUdSerieA() != null) {
                queryStr.append(clause).append("s.dtUdSerie BETWEEN :dataDa AND :dataA");
            }
            if (parametri.getPgUdSerieDa() != null || parametri.getPgUdSerieA() != null) {
                queryStr.append(clause).append("s.pgUdSerie BETWEEN :pgDa AND :pgA");
            }
            if (StringUtils.isNotBlank(parametri.getTiStatoConservazione())) {
                queryStr.append(clause).append("s.tiStatoConservazione = :tiStatoConservazione");
            }
        }
        queryStr.append(" ORDER BY s.dsKeyOrdUdSerie");
        TypedQuery<SerVLisUdAppartSerie> query = getEntityManager().createQuery(queryStr.toString(),
                SerVLisUdAppartSerie.class);
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        if (parametri != null) {
            if (StringUtils.isNotBlank(parametri.getCdUdSerie())) {
                query.setParameter("cdUdSerie", "%" + parametri.getCdUdSerie().toUpperCase() + "%");
            }
            if (StringUtils.isNotBlank(parametri.getInfoUdSerie())) {
                query.setParameter("infoUdSerie",
                        "%" + parametri.getInfoUdSerie().toUpperCase() + "%");
            }
            if (parametri.getDtUdSerieDa() != null && parametri.getDtUdSerieA() != null) {
                query.setParameter("dataDa", parametri.getDtUdSerieDa());
                query.setParameter("dataA", parametri.getDtUdSerieA());
            }
            if (parametri.getPgUdSerieDa() != null && parametri.getPgUdSerieA() != null) {
                query.setParameter("pgDa", parametri.getPgUdSerieDa());
                query.setParameter("pgA", parametri.getPgUdSerieA());
            }
            if (StringUtils.isNotBlank(parametri.getTiStatoConservazione())) {
                query.setParameter("tiStatoConservazione", parametri.getTiStatoConservazione());
            }
        }
        if (lazy) {
            return lazyListHelper.getTableBean(query, toTableBeanFunc);
        } else {
            return toTableBeanFunc.apply(query.getResultList());
        }
    }

    public List<SerVLisErrContenSerieUd> getSerVLisErrContenSerieUd(
            BigDecimal idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisErrContenSerieUd s WHERE s.idContenutoVerSerie = :idContenutoVerSerie ORDER BY s.pgErr");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        return query.getResultList();
    }

    public SerVLisErrFileSerieUdTableBean getSerVLisErrFileSerieUd(BigDecimal idVerSerie,
            String tiScopoFileInputVerSerie,
            Function<List, SerVLisErrFileSerieUdTableBean> toTableBeanFunc, boolean lazy) {
        TypedQuery<SerVLisErrFileSerieUd> query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisErrFileSerieUd s WHERE s.idVerSerie = :idVerSerie AND s.tiScopoFileInputVerSerie = :tiScopoFileInputVerSerie ORDER BY s.niRecErr",
                SerVLisErrFileSerieUd.class);
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiScopoFileInputVerSerie", tiScopoFileInputVerSerie);
        if (lazy) {
            return lazyListHelper.getTableBean(query, toTableBeanFunc);
        } else {
            return toTableBeanFunc.apply(query.getResultList());
        }
    }

    public List<SerQueryContenutoVerSerie> getSerQueryContenutoVerSerie(
            BigDecimal idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerQueryContenutoVerSerie s WHERE s.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        query.setParameter("idContenutoVerSerie", longFromBigDecimal(idContenutoVerSerie));
        return query.getResultList();
    }

    public SerQueryContenutoVerSerie getSerQueryContenutoVerSerie(Long idContenutoVerSerie,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT q FROM SerQueryContenutoVerSerie q JOIN q.serContenutoVerSerie contenuto JOIN contenuto.serVerSerie verSerie, SerContenutoVerSerie contenInput WHERE verSerie.idVerSerie = contenInput.serVerSerie.idVerSerie AND contenInput.idContenutoVerSerie = :idContenutoVerSerie AND contenuto.tiContenutoVerSerie = 'CALCOLATO' AND q.idRegistroUnitaDoc = :idRegistroUnitaDoc AND q.idTipoUnitaDoc = :idTipoUnitaDoc");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return (SerQueryContenutoVerSerie) query.getSingleResult();
    }

    public List<SerLacunaConsistVerSerie> getSerLacunaConsistVerSerie(
            BigDecimal idConsistVerSerie) {
        return getSerLacunaConsistVerSerie(idConsistVerSerie, null, null, null);
    }

    public List<SerLacunaConsistVerSerie> getSerLacunaConsistVerSerie(BigDecimal idConsistVerSerie,
            String tiModLacuna, BigDecimal niIniLacuna, BigDecimal niFinLacuna) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerLacunaConsistVerSerie s WHERE s.serConsistVerSerie.idConsistVerSerie = :idConsistVerSerie ");
        if (StringUtils.isNotBlank(tiModLacuna)) {
            queryStr.append("AND s.tiModLacuna = :tiModLacuna ");
            if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())
                    && niIniLacuna != null && niFinLacuna != null) {
                queryStr.append(
                        "AND s.niIniLacuna = :niIniLacuna AND s.niFinLacuna = :niFinLacuna");
            }
        }
        queryStr.append(" ORDER BY s.pgLacuna");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idConsistVerSerie", longFromBigDecimal(idConsistVerSerie));
        if (StringUtils.isNotBlank(tiModLacuna)) {
            query.setParameter("tiModLacuna", tiModLacuna);
            if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())
                    && niIniLacuna != null && niFinLacuna != null) {
                query.setParameter("niIniLacuna", niIniLacuna);
                query.setParameter("niFinLacuna", niFinLacuna);
            }
        }
        return query.getResultList();
    }

    public Long countSerLacunaConsistVerSerie(BigDecimal idConsistVerSerie, BigDecimal idLacuna,
            String tiModLacuna, BigDecimal niIniLacuna, BigDecimal niFinLacuna) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(s) FROM SerLacunaConsistVerSerie s WHERE s.serConsistVerSerie.idConsistVerSerie = :idConsistVerSerie ");
        if (idLacuna != null) {
            queryStr.append("AND s.idLacunaConsistVerSerie != :idLacuna ");
        }
        if (StringUtils.isNotBlank(tiModLacuna)) {
            queryStr.append("AND s.tiModLacuna = :tiModLacuna ");
            if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())
                    && niIniLacuna != null && niFinLacuna != null) {
                queryStr.append(
                        "AND s.niIniLacuna <= :niFinLacuna AND s.niFinLacuna >= :niIniLacuna");
            }
        }
        queryStr.append(" ORDER BY s.pgLacuna");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idConsistVerSerie", longFromBigDecimal(idConsistVerSerie));
        if (idLacuna != null) {
            query.setParameter("idLacuna", longFromBigDecimal(idLacuna));
        }
        if (StringUtils.isNotBlank(tiModLacuna)) {
            query.setParameter("tiModLacuna", tiModLacuna);
            if (niIniLacuna != null && niFinLacuna != null) {
                query.setParameter("niIniLacuna", niIniLacuna);
                query.setParameter("niFinLacuna", niFinLacuna);
            }
        }
        return (Long) query.getSingleResult();
    }

    public List<ResultVCalcoloSerieUd> getAroUdAppartVerSerie(long idVerSerie, String tipoContenuto,
            String tipoContenuto2, boolean checkPgNullo) {
        StringBuilder queryStr = new StringBuilder("SELECT ");
        if (StringUtils.isNotBlank(tipoContenuto2)) {
            queryStr.append(" DISTINCT ");
        }
        queryStr.append(
                "new it.eng.parer.viewEntity.ResultVCalcoloSerieUd(a.aroUnitaDoc.idUnitaDoc, a.aroUnitaDoc.dtCreazione, a.cdUdSerie, a.dtUdSerie, a.infoUdSerie, a.dsKeyOrdUdSerie, a.pgUdSerie) FROM AroUdAppartVerSerie a JOIN a.serContenutoVerSerie contenuto WHERE contenuto.serVerSerie.idVerSerie = :idVerSerie AND ( contenuto.tiContenutoVerSerie = :tipoContenuto");
        if (StringUtils.isNotBlank(tipoContenuto2)) {
            queryStr.append(" OR  contenuto.tiContenutoVerSerie = :tipoContenuto2");
        }
        queryStr.append(" ) ");
        if (checkPgNullo) {
            queryStr.append(" AND a.pgUdSerie IS NULL");
        }
        queryStr.append(" ORDER BY a.dsKeyOrdUdSerie ASC");
        Query query = getEntityManager().createQuery(queryStr.toString(),
                ResultVCalcoloSerieUd.class);
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tipoContenuto", tipoContenuto);
        if (StringUtils.isNotBlank(tipoContenuto2)) {
            query.setParameter("tipoContenuto2", tipoContenuto2);
        }
        return query.getResultList();
    }

    public List<String> getAroUdAppartChiaveDoppia(long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT a.cdUdSerie FROM AroUdAppartVerSerie a WHERE a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie GROUP BY a.cdUdSerie HAVING COUNT(a.cdUdSerie)>1 ORDER BY a.cdUdSerie");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        return query.getResultList();
    }

    public List<BigDecimal> getAroUdAppartNumeroDoppio(long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT a.pgUdSerie FROM AroUdAppartVerSerie a WHERE a.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND a.pgUdSerie IS NOT NULL GROUP BY a.pgUdSerie HAVING COUNT(a.pgUdSerie)>1 ORDER BY a.pgUdSerie");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        return query.getResultList();
    }

    public Long countAroUdAppartVerSerieInPgInterval(long idContenutoVerSerie,
            BigDecimal pgUdSerieDa, BigDecimal pgUdSerieA) {
        Query query = getEntityManager().createQuery("SELECT COUNT (a) "
                + "FROM AroUdAppartVerSerie a JOIN a.serContenutoVerSerie contenuto "
                + "WHERE contenuto.idContenutoVerSerie = :idContenutoVerSerie "
                + "AND a.pgUdSerie BETWEEN :pgUdSerieDa AND :pgUdSerieA ORDER BY a.dsKeyOrdUdSerie ASC",
                Long.class);
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("pgUdSerieDa", pgUdSerieDa);
        query.setParameter("pgUdSerieA", pgUdSerieA);
        return (Long) query.getSingleResult();
    }

    public List<SerVSelUdNovers> getSerVSelUdNovers(long idVerSerie, BigDecimal aaSelUd) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVSelUdNovers s WHERE s.id.idVerSerie = :idVerSerie AND s.aaKeyUnitaDoc = :aaSelUd");
        query.setParameter("idVerSerie", bigDecimalFromLong(idVerSerie));
        query.setParameter("aaSelUd", aaSelUd);
        return query.getResultList();
    }

    public Long countSerVSelUdNovers(long idVerSerie, BigDecimal aaSelUd) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(s) FROM SerVSelUdNovers s WHERE s.id.idVerSerie = :idVerSerie AND s.aaKeyUnitaDoc = :aaSelUd");
        query.setParameter("idVerSerie", bigDecimalFromLong(idVerSerie));
        query.setParameter("aaSelUd", aaSelUd);
        return (Long) query.getSingleResult();
    }

    public List<SerVBucoNumerazioneUd> getSerVBucoNumerazioneUd(long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT s FROM SerVBucoNumerazioneUd s WHERE s.id.idContenutoVerSerie = :idContenutoVerSerie ORDER BY s.id.pgUdSerIniBuco");
        query.setParameter("idContenutoVerSerie", bigDecimalFromLong(idContenutoVerSerie));
        return query.getResultList();
    }

    public List<SerVSelUdNoversBuco> getSerVSelUdNoversBuco(BigDecimal idVerSerie,
            BigDecimal pgUdSerIniBuco, BigDecimal pgUdSerFinBuco) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVSelUdNoversBuco s WHERE s.id.idVerSerie = :idVerSerie AND s.pgUnitaDoc BETWEEN :pgIni AND :pgFin");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("pgIni", pgUdSerIniBuco);
        query.setParameter("pgFin", pgUdSerFinBuco);
        return query.getResultList();
    }

    public Long countSerErrContenutoVerSerie(BigDecimal idContenutoVerSerie, String tiGravitaErr) {
        return countSerErrContenutoVerSerie(idContenutoVerSerie, tiGravitaErr, null, null);
    }

    public Long countSerErrContenutoVerSerie(BigDecimal idContenutoVerSerie, String tiGravitaErr,
            String tiErr, String tiOrigineErr) {
        StringBuilder builder = new StringBuilder(
                "SELECT COUNT(e) FROM SerErrContenutoVerSerie e WHERE e.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie ");
        if (StringUtils.isNotBlank(tiGravitaErr)) {
            builder.append("AND e.tiGravitaErr = :tiGravitaErr ");
        }
        if (StringUtils.isNotBlank(tiErr)) {
            builder.append("AND e.tiErr = :tiErr ");
        }
        if (StringUtils.isNotBlank(tiOrigineErr)) {
            builder.append("AND e.tiOrigineErr = :tiOrigineErr ");
        }
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idContenutoVerSerie", longFromBigDecimal(idContenutoVerSerie));
        if (StringUtils.isNotBlank(tiGravitaErr)) {
            query.setParameter("tiGravitaErr", tiGravitaErr);
        }
        if (StringUtils.isNotBlank(tiErr)) {
            query.setParameter("tiErr", tiErr);
        }
        if (StringUtils.isNotBlank(tiOrigineErr)) {
            query.setParameter("tiOrigineErr", tiOrigineErr);
        }
        return (Long) query.getSingleResult();
    }

    public Long countSerErrContenutoVerSerie(long idContenutoVerSerie, String... tiErrs) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(e) FROM SerErrContenutoVerSerie e WHERE e.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND e.tiErr IN (:tiErrs)");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("tiErrs", Arrays.asList(tiErrs));
        return (Long) query.getSingleResult();
    }

    public List<SerVLisUdErrFileInput> getSerVLisUdErrFileInput(BigDecimal idErrFileInput) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisUdErrFileInput s WHERE s.idErrFileInput = :idErrFileInput ORDER BY s.dsKeyOrdUdSerie");
        query.setParameter("idErrFileInput", idErrFileInput);
        return query.getResultList();
    }

    public List<SerVLisUdNovers> getSerVLisUdNovers(BigDecimal idErrContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerVLisUdNovers s WHERE s.idErrContenutoVerSerie = :idErrContenutoVerSerie ORDER BY s.cdKeyUnitaDoc");
        query.setParameter("idErrContenutoVerSerie", idErrContenutoVerSerie);
        return query.getResultList();
    }

    public List<DecTipoSerie> getDecTipoSerieAutom() {

        Query query = getEntityManager()
                .createQuery("SELECT d FROM DecTipoSerie d WHERE d.flCreaAutom = '1' "
                        + "AND d.tipoContenSerie = 'UNITA_DOC' AND d.flTipoSeriePadre = '0' "
                        + "AND d.dtIstituz <= :filterDate AND d.dtSoppres >= :filterDate "
                        + "AND NOT EXISTS(SELECT serieCreataAutom FROM DecTipoSerieCreataAutom serieCreataAutom "
                        + "WHERE serieCreataAutom.decTipoSerie.idTipoSerie = d.idTipoSerie "
                        + "AND serieCreataAutom.dtCreaAutom = TO_DATE(CONCAT(d.ggCreaAutom,'/',TO_CHAR(CURRENT_DATE, 'yyyy')),'dd/mm/yyyy') )");
        Date now = Calendar.getInstance().getTime();
        query.setParameter("filterDate", now);
        return query.getResultList();
    }

    public SerSerie getSerieAutom(BigDecimal aaIniCreaAutom, BigDecimal aaFinCreaAutom,
            long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT s FROM SerSerie s WHERE s.aaSerie BETWEEN :aaIniCreaAutom AND :aaFinCreaAutom AND s.decTipoSerie.idTipoSerie = :idTipoSerie AND s.dtAnnul = :defaultAnnul ORDER BY s.aaSerie DESC");
        query.setParameter("aaIniCreaAutom", aaIniCreaAutom);
        query.setParameter("aaFinCreaAutom", aaFinCreaAutom);
        query.setParameter("idTipoSerie", idTipoSerie);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        List<SerSerie> list = query.getResultList();
        SerSerie result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }

        return result;
    }

    public Long countSerieWithoutPgSerieUd(Long idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(tipoSerieUd) FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie WHERE tipoSerie.idTipoSerie = :idTipoSerie AND NOT EXISTS (SELECT out FROM DecOutSelUd out WHERE out.decTipoSerieUd.idTipoSerieUd = tipoSerieUd.idTipoSerieUd AND out.tiOut = 'PG_UD_SERIE')");
        query.setParameter("idTipoSerie", idTipoSerie);
        return (Long) query.getSingleResult();
    }

    public List<Object[]> getSerVerSerieDaElabList(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, CostantiDB.StatoVersioneSerie tiStatoVerSerie) {
        String queryStr = "SELECT strut.orgEnte.orgAmbiente.nmAmbiente, strut.orgEnte.nmEnte, strut.nmStrut, "
                + "serie.cdCompositoSerie, serie.aaSerie, serie.dsSerie, tipoSerie.nmTipoSerie, verSerie.cdVerSerie, "
                + "verSerie.dtInizioSelSerie, verSerie.dtFineSelSerie, verSerie.idVerSerie, strut.idStrut "
                + "FROM SerVerSerieDaElab verSerieDaElab JOIN verSerieDaElab.serVerSerie verSerie "
                + "JOIN verSerie.serSerie serie JOIN serie.decTipoSerie tipoSerie JOIN tipoSerie.orgStrut strut ";

        String whereWord = "WHERE ";

        if (idAmbiente != null) {
            queryStr = queryStr.concat(whereWord)
                    .concat("strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }

        if (idEnte != null) {
            queryStr = queryStr.concat(whereWord).concat("strut.orgEnte.idEnte = :idEnte ");
            whereWord = "AND ";
        }

        if (idStrut != null) {
            queryStr = queryStr.concat(whereWord).concat("strut.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        if (tiStatoVerSerie != null) {
            queryStr = queryStr.concat(whereWord)
                    .concat("verSerieDaElab.tiStatoVerSerie = :tiStatoVerSerie ");
        }

        queryStr = queryStr.concat(
                "ORDER BY strut.orgEnte.orgAmbiente.nmAmbiente, strut.orgEnte.nmEnte, strut.nmStrut, serie.cdCompositoSerie ");

        Query query = getEntityManager().createQuery(queryStr);

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }

        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        if (tiStatoVerSerie != null) {
            query.setParameter("tiStatoVerSerie", tiStatoVerSerie.name());
        }
        return query.getResultList();
    }

    public boolean existsFirmataNoMarca(BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(verSerieDaElab) FROM SerVerSerieDaElab verSerieDaElab "
                        + "WHERE verSerieDaElab.tiStatoVerSerie = 'FIRMATA_NO_MARCA' ");
        if (idStrut != null) {
            queryStr.append("AND verSerieDaElab.serVerSerie.serSerie.orgStrut.idStrut = :idStrut");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        return (Long) query.getSingleResult() > 0;
    }

    public List<SerVerSerieDaElab> getSerVerSerieDaElab(BigDecimal idStrut,
            String tiStatoVerSerie) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT verSerieDaElab FROM SerVerSerieDaElab verSerieDaElab ");

        if (idStrut != null) {
            queryStr.append(whereWord)
                    .append("verSerieDaElab.serVerSerie.serSerie.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (tiStatoVerSerie != null) {
            queryStr.append(whereWord).append("verSerieDaElab.tiStatoVerSerie = :tiStatoVerSerie ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (tiStatoVerSerie != null) {
            query.setParameter("tiStatoVerSerie", tiStatoVerSerie);
        }
        return query.getResultList();
    }

    public List<AroUnitaDoc> getLockAroUnitaDoc(BigDecimal idContenutoVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT ud FROM AroUdAppartVerSerie udVerSerie JOIN udVerSerie.aroUnitaDoc ud WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie");
        query.setParameter("idContenutoVerSerie", longFromBigDecimal(idContenutoVerSerie));
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    public void deleteSerFileVerSerie(BigDecimal idVerSerie, String tiFileVerSerie) {
        Query query = getEntityManager().createQuery(
                "DELETE FROM SerFileVerSerie file WHERE file.serVerSerie.idVerSerie = :idVerSerie AND file.tiFileVerSerie = :tiFileVerSerie");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.setParameter("tiFileVerSerie", tiFileVerSerie);
        query.executeUpdate();
    }

    public void deleteSerVolVerSerie(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "DELETE FROM SerVolVerSerie vol WHERE vol.serVerSerie.idVerSerie = :idVerSerie");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.executeUpdate();
    }

    public void deleteSerVerSerieDaElab(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "DELETE FROM SerVerSerieDaElab verDaElab WHERE verDaElab.serVerSerie.idVerSerie = :idVerSerie");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        query.executeUpdate();
    }

    public int updateStatoConservazioneAroUnitaDocInContenuto(Long idContenutoVerSerie,
            String tiStatoConservazioneOld, String tiStatoConservazioneNew) {
        Query query = getEntityManager().createQuery(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = :tiStatoConservazioneNew WHERE ud.tiStatoConservazione = :tiStatoConservazioneOld AND EXISTS (SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND udVerSerie.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc)");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);
        query.setParameter("tiStatoConservazioneNew", tiStatoConservazioneNew);
        return query.executeUpdate();
    }

    // MEV #31162
    public List<Long> getStatoConservazioneAroUnitaDocInContenuto(Long idContenutoVerSerie,
            String tiStatoConservazioneOld) {
        Query query = getEntityManager().createQuery(
                "SELECT ud.idUnitaDoc FROM AroUnitaDoc ud WHERE ud.tiStatoConservazione = :tiStatoConservazioneOld AND EXISTS (SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND udVerSerie.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc)");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);
        return query.getResultList();
    }
    // end MEV #31162

    public int updateStatoConservazioneAroUnitaDocInContenuto(Long idContenutoVerSerie,
            List<String> tiStatoConservazioneOld, String tiStatoConservazioneNew) {
        Query query = getEntityManager().createQuery(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = :tiStatoConservazioneNew WHERE ud.tiStatoConservazione IN (:tiStatoConservazioneOld) AND EXISTS (SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND udVerSerie.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc)");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);
        query.setParameter("tiStatoConservazioneNew", tiStatoConservazioneNew);
        return query.executeUpdate();
    }

    // MEV #31162
    public List<Long> getStatoConservazioneAroUnitaDocInContenuto(Long idContenutoVerSerie,
            List<String> tiStatoConservazioneOld) {
        Query query = getEntityManager().createQuery(
                "SELECT ud.idUnitaDoc FROM AroUnitaDoc ud WHERE ud.tiStatoConservazione IN (:tiStatoConservazioneOld) AND EXISTS (SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND udVerSerie.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc)");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);
        return query.getResultList();
    }
    // end MEV #31162

    public int updateStatoConservazioneAroUnitaDocWithoutOtherSeries(BigDecimal idSerie,
            Long idVerSerie, String tiStatoConservazioneOld, String tiStatoConservazioneNew) {
        Query query = getEntityManager().createQuery(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = :tiStatoConservazioneNew "
                        + "WHERE ud.tiStatoConservazione = :tiStatoConservazioneOld AND "
                        + "EXISTS (SELECT udVerSerie_1 FROM AroUdAppartVerSerie udVerSerie_1 "
                        + "JOIN udVerSerie_1.serContenutoVerSerie contenuto_1 "
                        + "JOIN contenuto_1.serVerSerie verSerie_1 "
                        + "JOIN verSerie_1.serSerie serie_1 WHERE serie_1.idSerie = :idSerie "
                        + "AND verSerie_1.idVerSerie = :idVerSerie "
                        + "AND udVerSerie_1.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_1.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc )"
                        + "AND NOT EXISTS (SELECT udVerSerie_2 FROM AroUdAppartVerSerie udVerSerie_2 "
                        + "JOIN udVerSerie_2.serContenutoVerSerie contenuto_2 "
                        + "JOIN contenuto_2.serVerSerie verSerie_2 "
                        + "JOIN verSerie_2.serSerie serie_2 WHERE serie_2.idSerie != :idSerie "
                        + "AND udVerSerie_2.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_2.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc "
                        + "AND serie_2.dtAnnul = :defaultAnnul )");
        query.setParameter("idSerie", longFromBigDecimal(idSerie));
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);
        query.setParameter("tiStatoConservazioneNew", tiStatoConservazioneNew);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return query.executeUpdate();
    }

    public int updateStatoConservazioneAroUnitaDocWithoutOtherSeries(BigDecimal idSerie,
            Long idVerSerie, String tiStatoConservazioneNew, List<String> statiUdExcluded) {
        Query query = getEntityManager().createQuery(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = :tiStatoConservazioneNew "
                        + "WHERE ud.tiStatoConservazione IN (:statiUdExcluded) AND "
                        + "EXISTS (SELECT udVerSerie_1 FROM AroUdAppartVerSerie udVerSerie_1 "
                        + "JOIN udVerSerie_1.serContenutoVerSerie contenuto_1 "
                        + "JOIN contenuto_1.serVerSerie verSerie_1 "
                        + "JOIN verSerie_1.serSerie serie_1 WHERE serie_1.idSerie = :idSerie "
                        + "AND verSerie_1.idVerSerie = :idVerSerie "
                        + "AND udVerSerie_1.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_1.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc )"
                        + "AND NOT EXISTS (SELECT udVerSerie_2 FROM AroUdAppartVerSerie udVerSerie_2 "
                        + "JOIN udVerSerie_2.serContenutoVerSerie contenuto_2 "
                        + "JOIN contenuto_2.serVerSerie verSerie_2 "
                        + "JOIN verSerie_2.serSerie serie_2 WHERE serie_2.idSerie != :idSerie "
                        + "AND udVerSerie_2.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_2.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc "
                        + "AND serie_2.dtAnnul = :defaultAnnul )");
        query.setParameter("idSerie", longFromBigDecimal(idSerie));
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("statiUdExcluded", statiUdExcluded);
        query.setParameter("tiStatoConservazioneNew", tiStatoConservazioneNew);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return query.executeUpdate();
    }

    public List<Long> getStatoConservazioneAroUnitaDocWithoutOtherSeries(BigDecimal idSerie,
            Long idVerSerie, List<String> tiStatoConservazioneOld) {
        Query query = getEntityManager().createQuery("SELECT ud.idUnitaDoc FROM AroUnitaDoc ud "
                + "WHERE ud.tiStatoConservazione IN (:tiStatoConservazioneOld) AND "
                + "EXISTS (SELECT udVerSerie_1 FROM AroUdAppartVerSerie udVerSerie_1 "
                + "JOIN udVerSerie_1.serContenutoVerSerie contenuto_1 "
                + "JOIN contenuto_1.serVerSerie verSerie_1 "
                + "JOIN verSerie_1.serSerie serie_1 WHERE serie_1.idSerie = :idSerie "
                + "AND verSerie_1.idVerSerie = :idVerSerie "
                + "AND udVerSerie_1.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_1.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc )"
                + "AND NOT EXISTS (SELECT udVerSerie_2 FROM AroUdAppartVerSerie udVerSerie_2 "
                + "JOIN udVerSerie_2.serContenutoVerSerie contenuto_2 "
                + "JOIN contenuto_2.serVerSerie verSerie_2 "
                + "JOIN verSerie_2.serSerie serie_2 WHERE serie_2.idSerie != :idSerie "
                + "AND udVerSerie_2.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie_2.aroUnitaDoc.idUnitaDoc = ud.idUnitaDoc "
                + "AND serie_2.dtAnnul = :defaultAnnul )");
        query.setParameter("idSerie", longFromBigDecimal(idSerie));
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiStatoConservazioneOld", tiStatoConservazioneOld);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return query.getResultList();
    }

    public List<AroUdAppartVerSerie> getAroUdAppartVerSerieInContenEff(Long idVerSerie,
            String tiStatoConservazioneUd) {
        Query query = getEntityManager().createQuery(
                "SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie JOIN udVerSerie.aroUnitaDoc ud WHERE udVerSerie.serContenutoVerSerie.serVerSerie.idVerSerie = :idVerSerie AND udVerSerie.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND ud.tiStatoConservazione = :tiStatoConservazioneUd");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiStatoConservazioneUd", tiStatoConservazioneUd);
        return query.getResultList();
    }

    public List<AroUdAppartVerSerie> getAroUdAppartVerSerieNoAip(Long idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie WHERE udVerSerie.serContenutoVerSerie.serVerSerie.idVerSerie = :idVerSerie AND udVerSerie.serContenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' AND udVerSerie.idVerIndiceAipUd IS NULL");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public int updateAroUdAppartVerSerieNoAip(Long idContenutoEffettivo) {
        Query query = getEntityManager()
                .createNativeQuery("UPDATE ARO_UD_APPART_VER_SERIE udVerSerie "
                        + "SET id_ver_indice_aip_ud = (SELECT u.id_ver_indice_aip_last FROM aro_indice_aip_ud u "
                        + "WHERE u.id_unita_doc = udVerSerie.id_unita_doc AND u.ti_formato_indice_aip = 'UNISYNCRO') "
                        + "WHERE udVerSerie.id_contenuto_ver_serie = ?1 AND udVerSerie.id_ver_indice_aip_ud IS NULL");
        query.setParameter(1, idContenutoEffettivo);
        return query.executeUpdate();
    }

    public BigDecimal getIdVerIndiceAipLast(Long idUnitaDoc, String tiFormatoIndiceAip) {
        BigDecimal result;
        Query query = getEntityManager().createQuery(
                "SELECT u.idVerIndiceAipLast FROM AroIndiceAipUd u WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND u.tiFormatoIndiceAip = :tiFormatoIndiceAip");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("tiFormatoIndiceAip", tiFormatoIndiceAip);
        List<BigDecimal> results = query.getResultList();
        if (results.isEmpty()) {
            AroUnitaDoc ud = findById(AroUnitaDoc.class, idUnitaDoc);
            throw new IllegalStateException(
                    "La serie non pu\u00F2 essere validata perch\u00E9 per l'unit\u00E0 documentaria "
                            + (ud != null
                                    ? ud.getCdRegistroKeyUnitaDoc() + "-"
                                            + ud.getAaKeyUnitaDoc().toPlainString() + "-"
                                            + ud.getCdKeyUnitaDoc()
                                    : String.valueOf(idUnitaDoc))
                            + " l'indice AIP non \u00E8 definito ");
        } else {
            result = results.get(0);
        }
        return result;
    }

    /**
     *
     * @param idVerSerie     id versamento serie
     * @param tiFileVerSerie tipo serie
     * @param blFile         blob
     * @param cdVerXsdFile   codice versione xsd file
     * @param idStrut        id struttura
     * @param dtCreazione    data creazione
     * @param tipoFirma      tipo firma (XADES o CADES)
     *
     * @param putOnOs        parametro per decidere se salvare anche il blob
     *
     * @return entity SerFileVerSerie
     *
     * @throws IOException              errore generico di tipo IO
     * @throws NoSuchAlgorithmException Salva un record file calcolandone l'hash con SHA-256
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerFileVerSerie storeFileIntoSerFileVerSerie(long idVerSerie, String tiFileVerSerie,
            byte[] blFile, String cdVerXsdFile, BigDecimal idStrut, Date dtCreazione,
            boolean putOnOs, ElencoEnums.TipoFirma tipoFirma)
            throws NoSuchAlgorithmException, IOException {
        SerFileVerSerie fileVerSerie = new SerFileVerSerie();
        SerVerSerie verSerie = getEntityManager().find(SerVerSerie.class, idVerSerie);
        fileVerSerie.setSerVerSerie(verSerie);
        fileVerSerie.setTiFileVerSerie(tiFileVerSerie);

        if (!putOnOs) {
            fileVerSerie.setBlFile(blFile);
        }

        String hash = new HashCalculator().calculateHashSHAX(blFile, TipiHash.SHA_256)
                .toHexBinary();
        fileVerSerie.setDsHashFile(hash);
        fileVerSerie.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
        fileVerSerie.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
        fileVerSerie.setCdVerXsdFile(cdVerXsdFile);
        fileVerSerie.setIdStrut(idStrut);
        fileVerSerie.setDtCreazione(dtCreazione);
        fileVerSerie.setTiFirma(tipoFirma == null ? null : tipoFirma.name());
        OrgStrut strut = findById(OrgStrut.class, idStrut);
        fileVerSerie.setIdEnteConserv(strut.getOrgEnte().getOrgAmbiente().getIdEnteConserv());
        getEntityManager().persist(fileVerSerie);
        verSerie.getSerFileVerSeries().add(fileVerSerie);
        return fileVerSerie;
    }

    public SerVerSerieDaElab getSerVerSerieDaElabByIdVerSerie(long idVerSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT verSerieDaElab FROM SerVerSerieDaElab verSerieDaElab "
                        + "WHERE verSerieDaElab.serVerSerie.idVerSerie = :idVerSerie ");
        query.setParameter("idVerSerie", idVerSerie);
        List<SerVerSerieDaElab> list = query.getResultList();
        SerVerSerieDaElab result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public List<AroUdAppartVerSerie> getLockAroUdAppartVerSerie(long idVerSerie,
            String tiContenutoVerSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT udAppartVerSerie FROM AroUdAppartVerSerie udAppartVerSerie "
                        + "WHERE udAppartVerSerie.serContenutoVerSerie.serVerSerie.idVerSerie = :idVerSerie "
                        + "AND udAppartVerSerie.serContenutoVerSerie.tiContenutoVerSerie = :tiContenutoVerSerie ");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiContenutoVerSerie", tiContenutoVerSerie);
        return query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    public SerFileVerSerie getSerFileVerSerie(long idVerSerie, String tiFileVerSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT fileVerSerie FROM SerFileVerSerie fileVerSerie "
                        + "WHERE fileVerSerie.serVerSerie.idVerSerie = :idVerSerie "
                        + "AND fileVerSerie.tiFileVerSerie = :tiFileVerSerie ");
        query.setParameter("idVerSerie", idVerSerie);
        query.setParameter("tiFileVerSerie", tiFileVerSerie);
        SerFileVerSerie fileVerSerie = null;
        List<SerFileVerSerie> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            fileVerSerie = list.get(0);
        }

        return fileVerSerie;
    }

    public byte[] retrieveFileVerSerie(SerVerSerie verSerie, String fileType) {
        byte[] file = null;
        List<SerFileVerSerie> serieFiles = verSerie.getSerFileVerSeries();
        for (SerFileVerSerie serieFile : serieFiles) {
            if (fileType.equals(serieFile.getTiFileVerSerie())) {
                file = serieFile.getBlFile();
            }
        }
        return file;
    }

    public SerIxVolVerSerie getSerIxVolVerSerie(long idVolVerSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT ixVolVerSerie FROM SerIxVolVerSerie ixVolVerSerie "
                        + "WHERE ixVolVerSerie.serVolVerSerie.idVolVerSerie = :idVolVerSerie ");
        query.setParameter("idVolVerSerie", idVolVerSerie);
        return (SerIxVolVerSerie) query.getSingleResult();
    }

    public List<SerIxVolVerSerie> getSerIxVolVerSerieList(long idVerSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT ixVolVerSerie FROM SerIxVolVerSerie ixVolVerSerie WHERE ixVolVerSerie.serVolVerSerie.serVerSerie.idVerSerie = :idVerSerie");
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    public List<AroUdAppartVerSerie> getAroUnitaDocAnnullateInContenutoWithTipoSerieUd(
            long idContenutoVerSerie, long idRegistroUnitaDoc, long idTipoUnitaDoc,
            Date minDtAnnul) {
        Query query = getEntityManager().createQuery(
                "SELECT udVerSerie FROM AroUdAppartVerSerie udVerSerie JOIN udVerSerie.aroUnitaDoc ud WHERE udVerSerie.serContenutoVerSerie.idContenutoVerSerie = :idContenutoVerSerie AND ud.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc AND ud.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc AND ud.tiStatoConservazione = 'ANNULLATA' AND ud.dtAnnul >= :dtAnnul");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        query.setParameter("dtAnnul", minDtAnnul);
        return query.getResultList();
    }

    public List<SerVLisSerDaValidare> getSerVLisSerDaValidare(Long idUserIam, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerVLisSerDaValidare s WHERE s.id.idUserIam = :idUserIam ");
        String clause = " AND ";
        if (idAmbiente != null) {
            queryStr.append(clause).append("s.idAmbiente = :idAmbiente");
        }
        if (idEnte != null) {
            queryStr.append(clause).append("s.idEnte = :idEnte");
        }
        if (idStrut != null) {
            queryStr.append(clause).append("s.idStrut = :idStrut");
        }
        queryStr.append(" ORDER BY s.nmAmbiente, s.nmEnte, s.nmStrut, s.cdCompositoSerie");
        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        return query.getResultList();
    }

    public List<SerVerSerie> getVersioniSerieCorrentiByTipoSerieAndStato(BigDecimal idTipoSerie,
            String... statiSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT v FROM SerStatoVerSerie stato JOIN stato.serVerSerie v JOIN v.serSerie s "
                        + "WHERE s.decTipoSerie.idTipoSerie = :idTipoSerie "
                        + "AND v.pgVerSerie = (SELECT MAX(versCorr.pgVerSerie) FROM SerVerSerie versCorr WHERE versCorr.serSerie.idSerie = s.idSerie) "
                        + "AND stato.tiStatoVerSerie IN (:statiSerie) "
                        + "AND s.dtAnnul = :defaultAnnul");

        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        query.setParameter("statiSerie", Arrays.asList(statiSerie));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return query.getResultList();
    }

    public Long countSerQueryContenutoVerSerie(BigDecimal idContenutoVerSerie,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(q) FROM SerQueryContenutoVerSerie q JOIN q.serContenutoVerSerie contenuto JOIN contenuto.serVerSerie verSerie, SerContenutoVerSerie contenInput WHERE verSerie.idVerSerie = contenInput.serVerSerie.idVerSerie AND contenInput.idContenutoVerSerie = :idContenutoVerSerie AND contenuto.tiContenutoVerSerie = 'CALCOLATO' AND q.idRegistroUnitaDoc = :idRegistroUnitaDoc AND q.idTipoUnitaDoc = :idTipoUnitaDoc");
        query.setParameter("idContenutoVerSerie", longFromBigDecimal(idContenutoVerSerie));
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return (Long) query.getSingleResult();
    }

    public Long countAroUdAppartVerSerie(long idContenutoVerSerie) {
        Query query = getEntityManager().createQuery("SELECT COUNT (a) "
                + "FROM AroUdAppartVerSerie a JOIN a.serContenutoVerSerie contenuto "
                + "WHERE contenuto.idContenutoVerSerie = :idContenutoVerSerie "
                + "ORDER BY a.dsKeyOrdUdSerie ASC");
        query.setParameter("idContenutoVerSerie", idContenutoVerSerie);
        return (Long) query.getSingleResult();
    }

    public List<OrgStrut> retrieveStrutture() {
        Query q = getEntityManager().createQuery(
                "SELECT DISTINCT strut " + "FROM SerVerSerieDaElab verSerieDaElab, OrgStrut strut "
                        + "WHERE verSerieDaElab.idStrut = strut.idStrut "
                        + "AND verSerieDaElab.tiStatoVerSerie = 'VALIDATA' ");
        return q.getResultList();
    }

    public List<SerVRicConsistSerieUd> getSerVRicConsistSerieUd(RicercaSerieBean filtri) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT s FROM SerVRicConsistSerieUd s WHERE s.idAmbiente = :idAmbiente AND s.idEnte = :idEnte AND s.idStrut = :idStrut ");
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            queryStr.append(clause).append("s.cdCompositoSerie like :cdCompositoSerie ");
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            queryStr.append(clause).append("s.dsSerie like :dsSerie ");
        }
        if (filtri.getId_tipo_serie() != null) {
            queryStr.append(clause).append("s.idTipoSerie = :idTipoSerie ");
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            queryStr.append(clause).append("s.aaSerie BETWEEN :aaSerieDa AND :aaSerieA ");
        } else {
            queryStr.append(clause).append("s.aaSerie <= :aaSerieA ");
        }
        if (StringUtils.isNotBlank(filtri.getFl_presenza_consist_attesa())) {
            queryStr.append(clause).append("s.flPresenzaConsistAttesa = :flPresenzaConsistAttesa");
        }
        if (filtri.getId_tipo_unita_doc() != null) {
            queryStr.append(clause).append("s.idTipoUnitaDoc = :idTipoUnitaDoc");
        }
        if (filtri.getId_registro_unita_doc() != null) {
            queryStr.append(clause).append("s.idRegistroUnitaDoc = :idRegistroUnitaDoc");
        }

        queryStr.append(" ORDER BY s.cdCompositoSerie, s.aaSerie");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", filtri.getId_ambiente());
        query.setParameter("idEnte", filtri.getId_ente());
        query.setParameter("idStrut", filtri.getId_strut());
        if (StringUtils.isNotBlank(filtri.getCd_composito_serie())) {
            query.setParameter("cdCompositoSerie", '%' + filtri.getCd_composito_serie() + '%');
        }
        if (StringUtils.isNotBlank(filtri.getDs_serie())) {
            query.setParameter("dsSerie", '%' + filtri.getDs_serie() + '%');
        }
        if (filtri.getId_tipo_serie() != null) {
            query.setParameter("idTipoSerie", filtri.getId_tipo_serie());
        }
        if (filtri.getAa_serie_da() != null && filtri.getAa_serie_a() != null) {
            query.setParameter("aaSerieDa", filtri.getAa_serie_da());
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        } else {
            query.setParameter("aaSerieA", filtri.getAa_serie_a());
        }
        if (StringUtils.isNotBlank(filtri.getFl_presenza_consist_attesa())) {
            query.setParameter("flPresenzaConsistAttesa", filtri.getFl_presenza_consist_attesa());
        }
        if (filtri.getId_tipo_unita_doc() != null) {
            query.setParameter("idTipoUnitaDoc", filtri.getId_tipo_unita_doc());
        }
        if (filtri.getId_registro_unita_doc() != null) {
            query.setParameter("idRegistroUnitaDoc", filtri.getId_registro_unita_doc());
        }
        return query.getResultList();
    }

    public List<SerVerSerie> getVersioniSerieCorrentiByTipoSerie(long idTipoSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT v FROM SerVerSerie v JOIN v.serSerie s "
                        + "WHERE s.decTipoSerie.idTipoSerie = :idTipoSerie "
                        + "AND v.pgVerSerie = (SELECT MAX(versCorr.pgVerSerie) FROM SerVerSerie versCorr WHERE versCorr.serSerie.idSerie = s.idSerie) "
                        + "AND s.dtAnnul = :defaultAnnul "
                        + "ORDER BY s.cdCompositoSerie, s.aaSerie");

        query.setParameter("idTipoSerie", idTipoSerie);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return query.getResultList();
    }

    /**
     * Determina le versioni serie correnti con stato = VALIDATA o DA_FIRMARE o FIRMATA o
     * IN_CUSTODIA, nel cui contenuto di tipo EFFETTIVO siano presenti le unità documentarie degli
     * item (della richiesta corrente) di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER
     *
     * @param idRichAnnulVers id della richiesta di annullamento
     *
     * @return la lista delle versioni
     */
    public List<BigDecimal> retrieveSerVLisVerserByRichann(long idRichAnnulVers) {
        Query query = getEntityManager().createQuery(
                "SELECT ser.id.idVerSerie FROM SerVLisVerserByRichann ser WHERE ser.id.idRichAnnulVers = :idRichAnnulVers ");
        query.setParameter("idRichAnnulVers", bigDecimalFromLong(idRichAnnulVers));
        return query.getResultList();
    }

    public boolean existsCdSerNormaliz(long idStrut, BigDecimal aaSerie, String cdSerieNormaliz) {
        String queryStr = "select count(ser) from SerSerie ser "
                + "where ser.orgStrut.idStrut = :idStrut " + " and ser.aaSerie = :aaSerie "
                + " and ser.cdSerieNormaliz = :cdSerieNormaliz "
                + " and ser.dtAnnul = :defaultAnnul";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("aaSerie", aaSerie);
        query.setParameter("cdSerieNormaliz", cdSerieNormaliz);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);

        query.setParameter("defaultAnnul", c.getTime());

        return (Long) query.getSingleResult() > 0;
    }

    public void storeSerUrnFileVerSerie(SerFileVerSerie tmpSerFileVerSerie, CSVersatore versatore,
            String codiceSerie, String versioneSerie) {
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore), codiceSerie);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnSerie(MessaggiWSFormat
                .formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);

        // salvo ORIGINALE
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDNonFirmate(tmpUrn, versioneSerie),
                TiUrnFileVerSerie.ORIGINALE);
        // salvo NORMALIZZATO
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDNonFirmate(tmpUrnNorm, versioneSerie),
                TiUrnFileVerSerie.NORMALIZZATO);
    }

    public void storeSerUrnFileVerSerieFir(SerFileVerSerie tmpSerFileVerSerie,
            CSVersatore versatore, String codiceSerie, String versioneSerie) {
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore), codiceSerie);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnSerie(MessaggiWSFormat
                .formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);

        // salvo ORIGINALE
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDFir(tmpUrn, versioneSerie),
                TiUrnFileVerSerie.ORIGINALE);
        // salvo NORMALIZZATO
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDFir(tmpUrnNorm, versioneSerie),
                TiUrnFileVerSerie.NORMALIZZATO);
    }

    public void storeSerUrnFileVerSerieMarca(SerFileVerSerie tmpSerFileVerSerie,
            CSVersatore versatore, String codiceSerie, String versioneSerie) {
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore), codiceSerie);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnSerie(MessaggiWSFormat
                .formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);

        // salvo ORIGINALE
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDMarca(tmpUrn, versioneSerie),
                TiUrnFileVerSerie.ORIGINALE);
        // salvo NORMALIZZATO
        this.salvaUrnVerIxAipFileVerSerie(tmpSerFileVerSerie,
                MessaggiWSFormat.formattaUrnIndiceAIPSerieUDMarca(tmpUrnNorm, versioneSerie),
                TiUrnFileVerSerie.NORMALIZZATO);
    }

    private void salvaUrnVerIxAipFileVerSerie(SerFileVerSerie serFileVerSerie, String tmpUrn,
            TiUrnFileVerSerie tiUrn) {

        SerUrnFileVerSerie tmpSerUrnFileVerSerie = null;

        tmpSerUrnFileVerSerie = new SerUrnFileVerSerie();
        tmpSerUrnFileVerSerie.setDsUrn(tmpUrn);
        tmpSerUrnFileVerSerie.setTiUrn(tiUrn);
        tmpSerUrnFileVerSerie.setSerFileVerSerie(serFileVerSerie);

        // persist
        getEntityManager().persist(tmpSerUrnFileVerSerie);

        serFileVerSerie.getSerUrnFileVerSeries().add(tmpSerUrnFileVerSerie);
    }

    // MAC #39491
    /**
     * Esegue l'update massivo dello stato di conservazione per una lista di ID.
     *
     * @param ids        Lista di ID delle Unità Documentarie
     * @param nuovoStato Il nuovo stato da assegnare (es. IN_ARCHIVIO)
     * @return numero di righe aggiornate
     */
    public int updateStatoUdMassivo(List<Long> ids, String nuovoStato) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        Query query = getEntityManager()
                .createQuery("UPDATE AroUnitaDoc ud SET ud.tiStatoConservazione = :nuovoStato "
                        + "WHERE ud.idUnitaDoc IN :ids");
        query.setParameter("nuovoStato", nuovoStato);
        query.setParameter("ids", ids);

        return query.executeUpdate();
    }
    // end MAC #39491
}
