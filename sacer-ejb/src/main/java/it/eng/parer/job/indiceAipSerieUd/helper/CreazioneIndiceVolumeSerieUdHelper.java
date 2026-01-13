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

package it.eng.parer.job.indiceAipSerieUd.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.SerIxVolVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.SerVolVerSerie;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.SerVCreaIxVolSerieUd;
import it.eng.parer.viewEntity.SerVLisUdAppartVolSerie;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 *
 * @author gilioli_p
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "CreazioneIndiceVolumeSerieUdHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceVolumeSerieUdHelper extends GenericHelper {

    @Resource
    private SessionContext context;

    public SerVCreaIxVolSerieUd getSerVCreaIxVolSerieUd(Long idVolVerSerie) {
        // serve per forzare l'aggiornamento anche della vista SerVCreaIxVolSerieUd che usa dati
        // persistiti in
        // precedenza
        getEntityManager().flush();
        return getEntityManager().find(SerVCreaIxVolSerieUd.class,
                BigDecimal.valueOf(idVolVerSerie));
    }

    public List<SerVLisUdAppartVolSerie> getSerVLisUdAppartVolSerie(Long idVolVerSerie) {
        Query query = getEntityManager().createQuery("SELECT u FROM SerVLisUdAppartVolSerie u "
                + "WHERE u.idVolVerSerie = :idVolVerSerie " + "ORDER BY u.dsKeyOrdUdSerie ASC ");
        query.setParameter("idVolVerSerie", bigDecimalFromLong(idVolVerSerie));
        return query.getResultList();
    }

    public SerVolVerSerie registraVolVerSerie(long idVerSerie) {
        SerVolVerSerie volVerSerie = new SerVolVerSerie();
        volVerSerie.setSerVerSerie(getEntityManager().find(SerVerSerie.class, idVerSerie));
        volVerSerie.setPgVolVerSerie(
                context.getBusinessObject(CreazioneIndiceVolumeSerieUdHelper.class)
                        .getUltimoProgressivoVolVerSerie(idVerSerie).add(BigDecimal.ONE));
        volVerSerie.setNiUnitaDocVol(BigDecimal.ZERO);
        getEntityManager().persist(volVerSerie);
        getEntityManager().flush();
        return volVerSerie;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerIxVolVerSerie registraSerIxVolVerSerie(long idVolVerSerie, String cdVerXsdIxVol,
            String xml, String hash, BigDecimal idStrut) {
        SerIxVolVerSerie ixVolVerSerie = new SerIxVolVerSerie();
        SerVolVerSerie volVerSerie = getEntityManager().find(SerVolVerSerie.class, idVolVerSerie);
        ixVolVerSerie.setSerVolVerSerie(volVerSerie);
        ixVolVerSerie.setBlIxVol(xml);
        ixVolVerSerie.setCdVerXsdIxVol(cdVerXsdIxVol);
        ixVolVerSerie.setDsHashIxVol(hash);
        ixVolVerSerie.setDsAlgoHashIxVol(TipiHash.SHA_256.descrivi());
        ixVolVerSerie.setCdEncodingHashIxVol(TipiEncBinari.HEX_BINARY.descrivi());
        ixVolVerSerie.setIdStrut(idStrut);
        ixVolVerSerie.setDtCreazione(new Date());
        getEntityManager().persist(ixVolVerSerie);
        getEntityManager().flush();
        if (volVerSerie.getSerIxVolVerSeries() == null) {
            volVerSerie.setSerIxVolVerSeries(new ArrayList<>());
        }
        volVerSerie.getSerIxVolVerSeries().add(ixVolVerSerie);
        return ixVolVerSerie;
    }

    /**
     * Ricava le ud appartenenti al contenuto di tipo EFFETTIVO per le quali la foreign key al
     * volume è nulla
     *
     * @param idVerSerie id versamento serie
     *
     * @return lista oggetti di tipo {@link AroUdAppartVerSerie}
     */
    public List<AroUdAppartVerSerie> getUdEffettiveSenzaVolume(Long idVerSerie) {
        String queryStr = "SELECT u FROM AroUdAppartVerSerie u "
                + "JOIN u.serContenutoVerSerie contenutoVerSerie "
                + "JOIN contenutoVerSerie.serVerSerie verSerie "
                + "WHERE contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
                + "AND u.serVolVerSerie IS NULL " + "AND verSerie.idVerSerie = :idVerSerie "
                + "ORDER BY u.dsKeyOrdUdSerie ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return query.getResultList();
    }

    /**
     * Recupera l'ultimo progressivo stato serie
     *
     * @param idVerSerie id versamento serie
     *
     * @return BigDecimal pk risultato
     */
    public BigDecimal getUltimoProgressivoVolVerSerie(Long idVerSerie) {
        String queryStr = "SELECT MAX(u.pgVolVerSerie) FROM SerVolVerSerie u "
                + "WHERE u.serVerSerie.idVerSerie = :idVerSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return (BigDecimal) query.getSingleResult() != null ? (BigDecimal) query.getSingleResult()
                : BigDecimal.ZERO;
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc id unita doc
     *
     * @return entity AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery("SELECT u FROM AroVerIndiceAipUd u "
                + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
                + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public AroVDtVersMaxByUnitaDoc getAroVDtVersMaxByUd(long idUnitaDoc) {
        String queryStr = "SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", BigDecimal.valueOf(idUnitaDoc));
        List<AroVDtVersMaxByUnitaDoc> lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }
}
