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

package it.eng.parer.elencoVersFascicoli.helper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.DocTypeEnum;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.viewEntity.ElvVCreaIxElencoFasc;
import it.eng.parer.viewEntity.ElvVCreaLisFascElenco;
import it.eng.parer.viewEntity.VolVCntUdDocCompTipoUd;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
        "unchecked" })
@Stateless(mappedName = "IndiceElencoVersFascHelper")
@LocalBean
public class IndiceElencoVersFascHelper {
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    public List<AroUnitaDoc> retrieveFasFascicoliInVolume(VolVolumeConserv volume) {
        Query q = em.createQuery(
                "SELECT appUnitaDoc.aroUnitaDoc " + "FROM VolAppartUnitaDocVolume appUnitaDoc "
                        + "WHERE appUnitaDoc.volVolumeConserv.idVolumeConserv = :idVolume");
        q.setParameter("idVolume", volume.getIdVolumeConserv());
        return q.getResultList();
    }

    public List<VolVCntUdDocCompTipoUd> getContenutoSinteticoElenco(VolVolumeConserv volume) {
        String selectContenutoSinteticoElencoStm = "SELECT contSint "
                + "FROM VolVCntUdDocCompTipoUd contSint "
                + "WHERE contSint.idVolumeConserv = :idVolume";

        Query q = em.createQuery(selectContenutoSinteticoElencoStm);
        q.setParameter("idVolume", BigDecimal.valueOf(volume.getIdVolumeConserv()));
        return q.getResultList();
    }

    public String getTipologieDocumentoPrincipaleElv(ElvElencoVer elenco) {
        String selectTipologieDocPrincElvStm = "SELECT distinct tipoDoc.nmTipoDoc "
                + "FROM ElvElencoVer elenco " + "JOIN elenco.aroUnitaDocs uds "
                + "JOIN uds.aroDocs docs " + "JOIN docs.decTipoDoc tipoDoc "
                + "WHERE docs.tiDoc = :tipoDoc " + "AND elenco = :elenco";
        Query q = em.createQuery(selectTipologieDocPrincElvStm);
        q.setParameter("tipoDoc", DocTypeEnum.PRINCIPALE.name());
        q.setParameter("elenco", elenco);
        List<String> tipologieDocPrinc = q.getResultList();
        return convertListToString(tipologieDocPrinc);
    }

    public String getTipologieDocumentoPrincipaleUd(AroUnitaDoc ud) {
        String selectTipologieDocPrincUdStm = "SELECT tipoDoc.nmTipoDoc " + "FROM AroUnitaDoc ud "
                + "JOIN ud.aroDocs docs " + "JOIN docs.decTipoDoc tipoDoc "
                + "WHERE docs.tiDoc = :tipoDoc " + "AND ud.idUnitaDoc = :idUd";
        Query q = em.createQuery(selectTipologieDocPrincUdStm);
        q.setParameter("tipoDoc", DocTypeEnum.PRINCIPALE.name());
        q.setParameter("idUd", ud.getIdUnitaDoc());
        List<String> tipologieDocPrinc = q.getResultList();
        return convertListToString(tipologieDocPrinc);
    }

    public String getTipologieUnitaDocumentaria(ElvElencoVer elenco) {
        String selectTipologieUdStm = "SELECT distinct tipoUnitaDoc.nmTipoUnitaDoc "
                + "FROM ElvElencoVer elenco " + "JOIN elenco.aroUnitaDocs uds "
                + "JOIN uds.decTipoUnitaDoc tipoUnitaDoc " + "WHERE elenco = :elenco";
        Query q = em.createQuery(selectTipologieUdStm);
        q.setParameter("elenco", elenco);
        List<String> tipologieUd = q.getResultList();
        return convertListToString(tipologieUd);
    }

    public String getTipologieRegistro(ElvElencoVer elenco) {
        String selectTipologieRegistroStm = "SELECT distinct uds.cdRegistroKeyUnitaDoc "
                + "FROM ElvElencoVer elenco " + "JOIN elenco.aroUnitaDocs uds "
                + "WHERE elenco = :elenco";
        Query q = em.createQuery(selectTipologieRegistroStm);
        q.setParameter("elenco", elenco);
        List<String> tipologieRegistro = q.getResultList();
        return convertListToString(tipologieRegistro);
    }

    public ElvVCreaIxElencoFasc retrieveElvVCreaIxElencoFasc(ElvElencoVersFasc elenco) {
        String selectCreaIxElencoFascStm = "SELECT creaIxElencoFasc "
                + "FROM ElvVCreaIxElencoFasc creaIxElencoFasc "
                + "WHERE creaIxElencoFasc.idElencoVersFasc = :idElencoVersFasc";
        Query q = em.createQuery(selectCreaIxElencoFascStm);
        q.setParameter("idElencoVersFasc", BigDecimal.valueOf(elenco.getIdElencoVersFasc()));
        return (ElvVCreaIxElencoFasc) q.getSingleResult();
    }

    public ElvVCreaLisFascElenco retrieveElvVCreaLisFascElenco(FasFascicolo ff) {
        String selectCreaLisFascElencoStm = "SELECT creaLisFascElenco "
                + "FROM ElvVCreaLisFascElenco creaLisFascElenco "
                + "WHERE creaLisFascElenco.id.idFascicolo = :idFascicolo "
                + "ORDER BY creaLisFascElenco.tsIniSes";
        Query q = em.createQuery(selectCreaLisFascElencoStm);
        q.setParameter("idFascicolo", BigDecimal.valueOf(ff.getIdFascicolo()));
        return (ElvVCreaLisFascElenco) q.getSingleResult();
    }

    public String convertListToString(List<String> listToConvert) {
        return StringUtils.join(listToConvert.toArray(), ",");
    }

    public Map<String, Date> retrieveDateVersamento(ElvElencoVer elenco) {
        Object[] dateVersamentoUdVersate = (Object[]) retrieveDateVersamentoUdVersate(elenco);
        Object[] dateVersamentoDocAgg = (Object[]) retrieveDateVersamentoDocAgg(elenco);

        Date minUdVersate = (Date) dateVersamentoUdVersate[0];
        Date minDocAgg = (Date) dateVersamentoDocAgg[0];

        Date maxUdVersate = (Date) dateVersamentoUdVersate[1];
        Date maxDocAgg = (Date) dateVersamentoDocAgg[1];

        Date dataVersamentoIniziale = calcolaDataVersamentoIniziale(minUdVersate, minDocAgg);
        Date dataVersamentoFinale = calcolaDataVersamentoFinale(maxUdVersate, maxDocAgg);
        Map<String, Date> dateVersamento = new HashMap<>();
        dateVersamento.put("dataVersamentoIniziale", dataVersamentoIniziale);
        dateVersamento.put("dataVersamentoFinale", dataVersamentoFinale);
        return dateVersamento;
    }

    private Date calcolaDataVersamentoIniziale(Date minUdVersate, Date minDocAgg) {
        Date dataVersamentoIniziale = null;
        if (minUdVersate != null && minDocAgg != null) {
            if (minUdVersate.after(minDocAgg)) {
                dataVersamentoIniziale = minDocAgg;
            } else {
                dataVersamentoIniziale = minUdVersate;
            }
        }
        if (minUdVersate == null && minDocAgg != null) {
            dataVersamentoIniziale = minDocAgg;
        } else if (minUdVersate != null && minDocAgg == null) {
            dataVersamentoIniziale = minUdVersate;
        }
        // TODO: controllare se funziona e cosa mettere in caso di null ad entrambi. Possibile?
        return dataVersamentoIniziale;
    }

    private Date calcolaDataVersamentoFinale(Date maxUdVersate, Date maxDocAgg) {
        Date dataVersamentoFinale = null;
        if (maxUdVersate != null && maxDocAgg != null) {
            if (maxUdVersate.after(maxDocAgg)) {
                dataVersamentoFinale = maxUdVersate;
            } else {
                dataVersamentoFinale = maxDocAgg;
            }
        }
        if (maxUdVersate == null && maxDocAgg != null) {
            dataVersamentoFinale = maxDocAgg;
        } else if (maxUdVersate != null && maxDocAgg == null) {
            dataVersamentoFinale = maxUdVersate;
        }
        // TODO: controllare se funziona e cosa mettere in caso di null ad entrambi. Possibile?
        return dataVersamentoFinale;
    }

    public Object retrieveDateVersamentoUdVersate(ElvElencoVer elenco) {
        String selectStm = "SELECT MIN(ud.dtCreazione), MAX(ud.dtCreazione) "
                + "FROM AroUnitaDoc ud " + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers";
        Query q = em.createQuery(selectStm);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        return q.getSingleResult();
    }

    public Object retrieveDateVersamentoDocAgg(ElvElencoVer elenco) {
        String selectStm = "SELECT MIN(doc.dtCreazione), MAX(doc.dtCreazione) " + "FROM AroDoc doc "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers";
        Query q = em.createQuery(selectStm);
        q.setParameter("idElencoVers", elenco.getIdElencoVers());
        return q.getSingleResult();
    }
}
