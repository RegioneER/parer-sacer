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

package it.eng.parer.restArch.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.entity.AroAipRestituzioneArchivio;
import it.eng.parer.entity.AroRichiestaRa;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.restArch.dto.RicercaRichRestArchBean;
import it.eng.parer.viewEntity.AroVChkRaUd;
import it.eng.parer.viewEntity.AroVLisItemRa;
import it.eng.parer.viewEntity.AroVRicRichRa;
import it.eng.parer.viewEntity.OrgVRicOrganizRestArch;
import it.eng.parer.web.util.Constants;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class RestituzioneArchivioHelper extends GenericHelper {

    /**
     * Verifica l'esistenza di una richiesta di restituzione archivio per l'ente convenzionato
     * <code>idEnteConvenz</code>
     *
     * @param idEnteConvenz id ente convenzionato
     *
     * @return true se esiste
     */
    public boolean isRichRestArchExisting(BigDecimal idEnteConvenz) {
        Query query = getEntityManager().createQuery("SELECT COUNT(r) FROM AroRichiestaRa r "
                + "WHERE r.orgStrut.idEnteConvenz = :idEnteConvenz " + "AND r.tsFine IS NOT NULL "
                + "AND r.tiStato NOT IN (:tiStato)");
        query.setParameter("idEnteConvenz", idEnteConvenz);
        query.setParameter("tiStato",
                Arrays.asList(AroRichiestaTiStato.ANNULLATO, AroRichiestaTiStato.RESTITUITO));
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Verifica l'esistenza di una richiesta di restituzione archivio per l'ente convenzionato
     * <code>idEnteConvenz</code>
     *
     * @param idEnteConvenz id ente convenzionato
     *
     * @return true se esiste
     */
    public boolean isRichRestArchExistingRestituito(BigDecimal idEnteConvenz) {
        Query query = getEntityManager().createQuery("SELECT COUNT(r) FROM AroRichiestaRa r "
                + "WHERE r.orgStrut.idEnteConvenz = :idEnteConvenz " + "AND r.tsFine IS NOT NULL "
                + "AND r.tiStato = 'RESTITUITO' AND r.flSvuotaFtp = '1' ");
        query.setParameter("idEnteConvenz", idEnteConvenz);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Verifica l'esistenza di una richiesta di restituzione archivio per l'ente convenzionato e per
     * gli stati dati in input
     *
     * @param idEnteConvenz id ente convenzionato
     * @param tiStato       lista degli stati della richiesta di restituzione archivio
     *
     * @return true se esiste
     */
    public boolean isRichRestArchByStatoExisting(BigDecimal idEnteConvenz,
            List<AroRichiestaTiStato> tiStato) {
        Query query = getEntityManager().createQuery("SELECT COUNT(r) FROM AroRichiestaRa r "
                + "WHERE r.orgStrut.idEnteConvenz = :idEnteConvenz " + "AND r.tsFine IS NOT NULL "
                + "AND r.tiStato IN :tiStato");
        query.setParameter("idEnteConvenz", idEnteConvenz);
        query.setParameter("tiStato", tiStato);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    public List<Long> retrieveRichRestArchExpiredToProcess(BigDecimal idEnteConvenz) {
        Date systemDate = new Date();
        Query query = getEntityManager().createQuery("SELECT r.idRichiestaRa FROM AroRichiestaRa r "
                + "WHERE (r.tiStato != :tiStato " + "AND r.tsInizio + 1 < :systemDate "
                + "AND r.orgStrut.idEnteConvenz = :idEnteConvenz)");
        query.setParameter("tiStato", AroRichiestaTiStato.ANNULLATO);
        query.setParameter("systemDate", systemDate);
        query.setParameter("idEnteConvenz", idEnteConvenz);
        return query.getResultList();
    }

    /**
     * Seleziona le unità documentarie, le serie e i fascicoli appartenenti all'ente convenzionato
     * passato come parametro ritornandole sotto forma di insieme UdSerFascObj
     *
     * @param struttura entity OrgStrut
     *
     * @return lista oggetti di tipo {@link UdSerFascObj}
     */
    public List<UdSerFascObj> retrieveUdSerFascToProcess(OrgStrut struttura) {
        /*
         * select * from ARO_V_SEL_UD_SER_FASC_BY_ENTE v where id_rootstrut = 3323 order by
         * id_strut, id_unita_doc
         */
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.id.idUnitaDoc, u.id.idSerie, u.id.idFascicolo, u.tiEle "
                        + "FROM AroVSelUdSerFascByEnte u "
                        + "WHERE u.id.idRootstrut = :idRootstrut ");

        // TIP: fdilorenzo, DEFINISCE L'ORDINAMENTO CON CUI DEVONO ESSERE ELABORATI GLI OGGETTI (A
        // SUPPORTO DELLA LOGICA
        // DEFINITA IN ANALISI)
        queryStr.append("ORDER BY u.tiEle");

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("idRootstrut", BigDecimal.valueOf(struttura.getIdStrut()));

        List<Object[]> udSerFascObjectList = q.getResultList();
        List<UdSerFascObj> udSerFascObjSet = new ArrayList<>();

        for (Object[] udSerFascObject : udSerFascObjectList) {
            Constants.TipoEntitaSacer tipoEntitaSacer = (udSerFascObject[3].equals("01_UNI_DOC"))
                    ? Constants.TipoEntitaSacer.UNI_DOC
                    : (udSerFascObject[3].equals("02_SERIE")) ? Constants.TipoEntitaSacer.SER
                            : Constants.TipoEntitaSacer.FASC;
            BigDecimal id = (udSerFascObject[3].equals("01_UNI_DOC"))
                    ? (BigDecimal) udSerFascObject[0]
                    : (udSerFascObject[3].equals("02_SERIE")) ? (BigDecimal) udSerFascObject[1]
                            : (BigDecimal) udSerFascObject[2];
            udSerFascObjSet.add(new UdSerFascObj(id, tipoEntitaSacer));
        }

        return udSerFascObjSet;
    }

    // <editor-fold defaultstate="expand" desc="Query per funzioni online">
    public List<AroVRicRichRa> retrieveAroVRicRichRa(RicercaRichRestArchBean filtri,
            List<Long> idEnteConvenzList) {
        String clause = " WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicRichRa ("
                        + "r.idRichiestaRa, r.nmEnteConvenz, r.nmEnteStrut, r.idEnte, r.idStrut, r.idEnteConvenz, r.totali, r.estratti, r.errori, "
                        + "r.estrattiTotali, r.sumDim, r.maxDtEstrazione, r.tiStato, r.tsInizio)"
                        + " FROM AroVRicRichRa r ");
        if (idEnteConvenzList != null && !idEnteConvenzList.isEmpty()) {
            if (idEnteConvenzList.size() == 1) {
                queryStr.append(clause).append("r.idEnteConvenz = :idEnteConvenz ");
            } else {
                queryStr.append(clause).append("r.idEnteConvenz IN (:idEnteConvenz) ");
            }
            clause = " AND ";
        }
        if (!filtri.getTi_stato_rich_rest_arch_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_rest_arch_cor().size() == 1) {
                queryStr.append(clause).append("r.tiStato = :tiStatoRichRestArchCor ");
            } else {
                queryStr.append(clause).append("r.tiStato IN (:tiStatoRichRestArchCor) ");
            }
        }
        queryStr.append("ORDER BY r.tsInizio DESC");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idEnteConvenzList != null && !idEnteConvenzList.isEmpty()) {
            if (idEnteConvenzList.size() == 1) {
                query.setParameter("idEnteConvenz", BigDecimal.valueOf(idEnteConvenzList.get(0)));
            } else {
                query.setParameter("idEnteConvenz", bigDecimalFromLong(idEnteConvenzList));
            }
        }
        if (!filtri.getTi_stato_rich_rest_arch_cor().isEmpty()) {
            if (filtri.getTi_stato_rich_rest_arch_cor().size() == 1) {
                query.setParameter("tiStatoRichRestArchCor",
                        filtri.getTi_stato_rich_rest_arch_cor().get(0));
            } else {
                query.setParameter("tiStatoRichRestArchCor",
                        filtri.getTi_stato_rich_rest_arch_cor());
            }
        }

        return query.getResultList();
    }

    /**
     * Recupero l'elenco delle strutture associate alla richiesta <code>idRichiestaRa</code>
     *
     * @param idRichiestaRa l'identificativo della richiesta
     *
     * @return lista oggetti di tipo {@link AroVRicRichRa}
     */
    public List<AroVRicRichRa> retrieveAroVRicRichRa(BigDecimal idRichiestaRa) {
        Query query = getEntityManager()
                .createQuery("SELECT DISTINCT new it.eng.parer.viewEntity.AroVRicRichRa ("
                        + "r.idRichiestaRa, r.nmEnteConvenz, r.nmEnteStrut, r.idEnte, r.idStrut, r.idEnteConvenz, r.totali, r.estratti, r.errori, "
                        + "r.estrattiTotali, r.sumDim, r.maxDtEstrazione, r.tiStato, r.tsInizio)"
                        + " FROM AroVRicRichRa r " + "WHERE r.idRichiestaRa = :idRichiestaRa "
                        + "ORDER BY r.tsInizio");
        query.setParameter("idRichiestaRa", idRichiestaRa);
        return query.getResultList();
    }

    public List<Long> retrieveOrgEnteSiamList(RicercaRichRestArchBean filtri) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT enteSiam.idEnteSiam FROM SIOrgEnteSiam enteSiam, OrgStrut strut "
                        + "JOIN strut.orgEnte ente " + "JOIN ente.orgAmbiente ambiente "
                        + "WHERE strut.idEnteConvenz = enteSiam.idEnteSiam ");
        String andWord = "AND ";
        if (filtri.getId_strut() != null) {
            queryStr.append(andWord).append("strut.idStrut = :idStrut ");
            andWord = "AND ";
        }
        if (filtri.getId_ente() != null) {
            queryStr.append(andWord).append("ente.idEnte = :idEnte ");
            andWord = "AND ";
        }
        if (filtri.getId_ambiente() != null) {
            queryStr.append(andWord).append("ambiente.idAmbiente = :idAmbiente ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (filtri.getId_strut() != null) {
            query.setParameter("idStrut", longFromBigDecimal(filtri.getId_strut()));
        }
        if (filtri.getId_ente() != null) {
            query.setParameter("idEnte", longFromBigDecimal(filtri.getId_ente()));
        }
        if (filtri.getId_ambiente() != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(filtri.getId_ambiente()));
        }
        return query.getResultList();
    }

    public List<AroVLisItemRa> getAroVLisItemRa(BigDecimal idRichRestArch, BigDecimal idStrut) {
        Query query = getEntityManager()
                .createQuery("SELECT DISTINCT new it.eng.parer.viewEntity.AroVLisItemRa ("
                        + "a.idRichiestaRa, a.idStrut, a.anno, a.totUd, a.numAip, a.dimensione, a.numDocs, a.numErrori, "
                        + "a.numEstratti, a.avanzamento)"
                        + " FROM AroVLisItemRa a WHERE a.idRichiestaRa = :idRichRestArch AND a.idStrut = :idStrut ORDER BY a.anno");
        query.setParameter("idRichRestArch", idRichRestArch);
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }

    /**
     * Recupera l'elenco di item all'interno della struttura <code>idStrut</code> di una determinata
     * richiesta <code>idRichiestaRa</code>
     *
     * @param idRichRestArch l'identificativo della richiesta
     * @param idStrut        l'id della struttura
     *
     * @return lista oggetti di tipo {@link Object[]}
     */
    public List<Object[]> getAroVLisItemRaFm(BigDecimal idRichRestArch, BigDecimal idStrut) {
        Query query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT a.id_Richiesta, a.id_Strut, a.anno, a.tot_Ud, a.num_Aip, a.dimensione, a.num_Docs, a.num_Errori, "
                        + "a.num_Estratti, a.avanzamento"
                        + " FROM Aro_V_Lis_Item_Ra_Fm(:idRichRestArch, :idStrut) a ORDER BY a.anno");
        query.setParameter("idRichRestArch", idRichRestArch);
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }

    public List<AroVLisItemRa> getAroVLisItemRaFmList(BigDecimal idRichRestArch,
            BigDecimal idStrut) {
        List<Object[]> obList = getAroVLisItemRaFm(idRichRestArch, idStrut);
        return getAroVLisItemRaList(obList);
    }

    public List<AroVLisItemRa> getAroVLisItemRaList(List<Object[]> objList) {
        List<AroVLisItemRa> itemList = new ArrayList<>();
        for (Object[] obj : objList) {
            BigDecimal idRichiestaRa = (BigDecimal) obj[0];
            BigDecimal idStrut = (BigDecimal) obj[1];
            BigDecimal anno = (BigDecimal) obj[2];
            BigDecimal totUd = (BigDecimal) obj[3];
            BigDecimal numAip = (BigDecimal) obj[4];
            BigDecimal dimensione = (BigDecimal) obj[5];
            BigDecimal numDocs = (BigDecimal) obj[6];
            BigDecimal numErrori = (BigDecimal) obj[7];
            BigDecimal numEstratti = (BigDecimal) obj[8];
            BigDecimal avanzamento = (BigDecimal) obj[9];

            AroVLisItemRa item = new AroVLisItemRa(idRichiestaRa, idStrut, anno, totUd, numAip,
                    dimensione, numDocs, numErrori, numEstratti, avanzamento);
            itemList.add(item);
        }

        return itemList;
    }

    public Long countAroItemRichRestArch(BigDecimal idRichRestArch, TiStatoAroAipRa... tiStato) {
        List<TiStatoAroAipRa> statiList = Arrays.asList(tiStato);
        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(i) FROM AroAipRestituzioneArchivio i WHERE i.aroRichiestaRa.idRichiestaRa = :idRichRestArch ");
        if (!statiList.isEmpty()) {
            if (statiList.size() == 1) {
                queryStr.append("AND i.tiStato = :tiStatoItem ");
            } else {
                queryStr.append("AND i.tiStato IN (:tiStatoItem) ");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRichRestArch", longFromBigDecimal(idRichRestArch));
        if (!statiList.isEmpty()) {
            if (statiList.size() == 1) {
                query.setParameter("tiStatoItem", statiList.get(0));
            } else {
                query.setParameter("tiStatoItem", statiList);
            }
        }
        return (Long) query.getSingleResult();
    }
    // </editor-fold>

    /**
     * Recupera tutti gli errori sugli item della richiesta
     *
     * @param idRichRestArch l'id della richiesta
     *
     * @return lista oggetti di tipo {@link AroAipRestituzioneArchivio}
     */
    public List<AroAipRestituzioneArchivio> retrieveAroErrItemRestArch(long idRichRestArch) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT errItemRestArch FROM AroAipRestituzioneArchivio errItemRestArch "
                        + "WHERE errItemRestArch.aroRichiestaRa.idRichiestaRa = :idRichRestArch ");
        queryStr.append("AND errItemRestArch.tiStato = :tiErrItemRestArch ");

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("idRichRestArch", idRichRestArch);
        q.setParameter("tiErrItemRestArch", TiStatoAroAipRa.ERRORE);

        return q.getResultList();
    }

    /**
     * Ricavo la struttura che ha creato la richiesta, ovvero quella che ha definito il primo stato
     *
     * @param idRichRestArch l'id della richiesta
     *
     * @return l'id utente
     */
    public long getIdStrutFirstStateRich(BigDecimal idRichRestArch) {
        Query q = getEntityManager()
                .createQuery("SELECT richRestArch FROM AroRichiestaRa richRestArch "
                        + "WHERE richRestArch.idRichiestaRa = :idRichRestArch ");
        q.setParameter("idRichRestArch", idRichRestArch.longValue());
        List<AroRichiestaRa> list = q.getResultList();
        return list.get(0).getOrgStrut().getIdStrut();
    }

    public List<AroVChkRaUd> retrieveAroVChkRaUdList(BigDecimal idEnteConvenz) {
        Query q = getEntityManager().createQuery("SELECT vista FROM AroVChkRaUd vista "
                + "WHERE vista.aroVChkRaUdId.idEnteConvenz = :idEnteConvenz ");
        q.setParameter("idEnteConvenz", idEnteConvenz);
        List<AroVChkRaUd> list = q.getResultList();
        return list;
    }

    // MEV #32535
    public List<OrgVRicOrganizRestArch> retrieveOrgVRicOrganizRestArchList(
            BigDecimal idEnteConvenz) {
        Query q = getEntityManager().createQuery("SELECT vista FROM OrgVRicOrganizRestArch vista "
                + "WHERE vista.idEnteConvenz = :idEnteConvenz ");
        q.setParameter("idEnteConvenz", idEnteConvenz);
        List<OrgVRicOrganizRestArch> list = q.getResultList();
        return list;
    }

    public BigDecimal getIdEnteConvenzDaConsiderare(BigDecimal idOrganizIam) {
        Query q = getEntityManager()
                .createQuery("SELECT vista.idEnteConvenz FROM OrgVRicOrganizRestArch vista "
                        + "WHERE vista.idOrganizIam = :idOrganizIam ORDER BY vista.dtIniVal DESC");
        q.setParameter("idOrganizIam", idOrganizIam);
        List<BigDecimal> list = q.getResultList();
        return (BigDecimal) list.get(0);
    }

    // end MEV #32535
}
