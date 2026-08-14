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

package it.eng.parer.web.helper;

import it.eng.parer.datamart.dto.ConteggioStatoUdDto;
import it.eng.parer.datamart.dto.RichiestaDataMartDTO;
import it.eng.parer.viewEntity.AroVChkStatoCorRichSoftDelete;
import it.eng.parer.entity.DmUdDelDecodStatoInterno;
import it.eng.parer.entity.DmUdDelObjectStorage;
import it.eng.parer.entity.DmUdDelRecRefTab;
import it.eng.parer.entity.DmUdDelRichieste;
import it.eng.parer.entity.DmUdDelStatoRichiesta;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OrgEnte;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class DataMartHelper extends GenericHelper {

    public DataMartHelper() {
        /* Default constructor */
    }

    private static final Logger log = LoggerFactory.getLogger(DataMartHelper.class.getName());

    /**
     * Restituisce la lista degli enti che hanno almeno una unità documentaria nel datamart (tabella
     * DM_UD_DEL), ordinata per nome ente.
     *
     * @return lista di {@link OrgEnte} coinvolti nel datamart
     */
    public List<OrgEnte> getOrgEnteDataMartList() {
        String queryStr = "SELECT ente.* FROM Org_Ente ente "
                + "WHERE ente.id_Ente IN (SELECT distinct id_ente FROM DM_UD_DEL group by id_ente) "
                + "ORDER BY ente.nm_ente";
        Query q = getEntityManager().createNativeQuery(queryStr, OrgEnte.class);
        return (List<OrgEnte>) q.getResultList();
    }

    /**
     * Restituisce l'elenco aggregato delle richieste del datamart con il conteggio delle UD per
     * ogni combinazione di (id_richiesta, ti_mot_cancellazione, ti_stato_ud_cancellate), filtrando
     * in base ai parametri opzionali forniti.
     *
     * @param tiMotCancellazione    tipo di motivo di cancellazione (es. 'R', 'S', 'A'); se
     *                              {@code null} non viene applicato il filtro
     * @param tiStatoRichiesta      stato della richiesta; se {@code null} non viene applicato il
     *                              filtro
     * @param idEnte                identificativo dell'ente; se {@code null} non viene applicato il
     *                              filtro
     * @param idStrut               identificativo della struttura; se {@code null} non viene
     *                              applicato il filtro
     * @param cdRegistroKeyUnitaDoc codice registro dell'unità documentaria; se {@code null} non
     *                              viene applicato il filtro
     * @param aaKeyUnitaDoc         anno chiave dell'unità documentaria; se {@code null} non viene
     *                              applicato il filtro
     * @param cdKeyUnitaDoc         chiave dell'unità documentaria; se {@code null} non viene
     *                              applicato il filtro
     *
     * @return lista di array di oggetti con i dati aggregati
     */
    public List<Object[]> getRichiesteDataMartList(String tiMotCancellazione,
            String tiStatoRichiesta, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc) {
        String whereWord = " WHERE ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT dm.id_richiesta, dm.ti_mot_cancellazione, "
                        + "CASE dm.ti_mot_cancellazione " + "WHEN 'R' THEN 'Restituzione archivio' "
                        + "WHEN 'S' THEN 'Scarto' " + "WHEN 'A' THEN 'Annullamento ud' "
                        + "ELSE 'Non definita' " + "END AS ds_mot_cancellazione, "
                        + "dm.ti_stato_ud_cancellate, count(1) as tot_ud_ti_mod "
                        + "FROM DM_UD_DEL dm ");

        if (tiMotCancellazione != null) {
            queryStr.append(whereWord).append("dm.ti_mot_cancellazione = :tiMotCancellazione ");
            whereWord = " AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("dm.id_ente = :idEnte ");
            whereWord = " AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("dm.id_strut = :idStrut ");
            whereWord = " AND ";
        }
        if (cdRegistroKeyUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("dm.cd_registro_key_unita_doc = :cdRegistroKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.aa_key_unita_doc = :aaKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (cdKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.cd_key_unita_doc = :cdKeyUnitaDoc ");
            whereWord = " AND ";
        }

        queryStr.append(
                "GROUP BY dm.id_richiesta, dm.ti_mot_cancellazione,  CASE dm.ti_mot_cancellazione WHEN 'R' THEN 'Restituzione archivio' WHEN 'S' THEN 'Scarto' WHEN 'A' THEN 'Annullamento ud' ELSE 'Non definita' "
                        + "END, dm.ti_stato_ud_cancellate ");

        Query q = getEntityManager().createNativeQuery(queryStr.toString());

        if (tiMotCancellazione != null) {
            q.setParameter("tiMotCancellazione", tiMotCancellazione);
        }
        if (tiStatoRichiesta != null) {
            q.setParameter("tiStatoRichiesta", tiStatoRichiesta);
        }
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            q.setParameter("idStrut", idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            q.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            q.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }

        return (List<Object[]>) q.getResultList();
    }

    /**
     * Restituisce la lista delle richieste del datamart come DTO, con il conteggio delle UD
     * associate, applicando i filtri opzionali forniti.
     *
     * @param tiMotCancellazione    tipo di motivo di cancellazione (es. 'R', 'S', 'A'); se
     *                              {@code null} non viene applicato il filtro
     * @param tiStatoRichiesta      stato della richiesta; se {@code null} non viene applicato il
     *                              filtro
     * @param idEnte                identificativo dell'ente; se {@code null} non viene applicato il
     *                              filtro
     * @param idStrut               identificativo della struttura; se {@code null} non viene
     *                              applicato il filtro
     * @param cdRegistroKeyUnitaDoc codice registro dell'unità documentaria; se {@code null} non
     *                              viene applicato il filtro
     * @param aaKeyUnitaDoc         anno chiave dell'unità documentaria; se {@code null} non viene
     *                              applicato il filtro
     * @param cdKeyUnitaDoc         chiave dell'unità documentaria; se {@code null} non viene
     *                              applicato il filtro
     * @param dtCreazioneDa         data di creazione minima (inclusa); se {@code null} viene usato
     *                              il 1° gennaio 2000
     * @param dtCreazioneA          data di creazione massima (inclusa); se {@code null} viene usata
     *                              la data odierna
     *
     * @return lista di {@link RichiestaDataMartDTO}
     */
    public List<RichiestaDataMartDTO> getRichiesteDataMartDtoList(String tiMotCancellazione,
            String tiStatoRichiesta, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc,
            Date dtCreazioneDa, Date dtCreazioneA) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT NEW it.eng.parer.datamart.dto.RichiestaDataMartDTO( "
                        + "dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, "
                        + "dmRich.tiMotCancellazione, "
                        + "CASE dmRich.tiMotCancellazione WHEN 'R' THEN 'Restituzione archivio' WHEN 'S' THEN 'Scarto' WHEN 'A' THEN 'Annullamento ud' ELSE 'Non definita' END, "
                        + "dmRich.dtCreazione, dmRich.dtEvasione, dmRich.tiStatoRichiesta"
                        + ", COUNT(dmUd)) "
                        + "FROM DmUdDelRichieste dmRich JOIN dmRich.dmUdDels dmUd ");

        String whereWord = " WHERE ";

        if (tiMotCancellazione != null) {
            queryStr.append(whereWord).append("dmRich.tiMotCancellazione = :tiMotCancellazione ");
            whereWord = " AND ";
        }
        if (tiStatoRichiesta != null) {
            queryStr.append(whereWord).append("dmRich.tiStatoRichiesta = :tiStatoRichiesta ");
            whereWord = " AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("dmUd.idEnte = :idEnte ");
            whereWord = " AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("dmUd.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (cdRegistroKeyUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("dmUd.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dmUd.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (cdKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dmUd.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
            whereWord = " AND ";
        }
        Date[] dateNormalizzate = new Date[2];
        if (dtCreazioneDa != null || dtCreazioneA != null) {
            dateNormalizzate = normalizeDateForDataMart(dtCreazioneDa, dtCreazioneA);
            queryStr.append(whereWord).append(
                    "dmRich.dtCreazione >= :dtCreazioneDa AND dmRich.dtCreazione <= :dtCreazioneA ");
            whereWord = " AND ";
        }

        queryStr.append("GROUP BY dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, "
                + "dmRich.tiMotCancellazione, dmRich.tiStatoRichiesta, dmRich.dtCreazione, dmRich.dtEvasione ");

        TypedQuery<RichiestaDataMartDTO> q = getEntityManager().createQuery(queryStr.toString(),
                RichiestaDataMartDTO.class);

        if (tiMotCancellazione != null) {
            q.setParameter("tiMotCancellazione", tiMotCancellazione);
        }
        if (tiStatoRichiesta != null) {
            q.setParameter("tiStatoRichiesta", tiStatoRichiesta);
        }
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            q.setParameter("idStrut", idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            q.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            q.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }
        if (dtCreazioneDa != null || dtCreazioneA != null) {
            q.setParameter("dtCreazioneDa", dateNormalizzate[0]);
            q.setParameter("dtCreazioneA", dateNormalizzate[1]);
        }

        return q.getResultList();
    }

    /**
     * Recupera una singola richiesta del datamart come DTO, incluso il conteggio delle UD
     * associate.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     *
     * @return il {@link RichiestaDataMartDTO} corrispondente, oppure {@code null} se non trovato
     */
    public RichiestaDataMartDTO getRichiestaDataMart(BigDecimal idUdDelRichiesta) {

        String queryStr = "SELECT NEW it.eng.parer.datamart.dto.RichiestaDataMartDTO( "
                + "dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, "
                + "dmRich.tiMotCancellazione, "
                + "CASE dmRich.tiMotCancellazione WHEN 'R' THEN 'Restituzione archivio' WHEN 'S' THEN 'Scarto' WHEN 'A' THEN 'Annullamento ud' ELSE 'Non definita' END, "
                + "dmRich.dtCreazione, dmRich.dtEvasione, dmRich.tiStatoRichiesta, COUNT(dmUd)) "
                + "FROM DmUdDelRichieste dmRich JOIN dmRich.dmUdDels dmUd "
                + "WHERE dmRich.idUdDelRichiesta = :idUdDelRichiesta "
                + "GROUP BY dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, "
                + "dmRich.tiMotCancellazione, dmRich.tiStatoRichiesta, dmRich.dtCreazione, dmRich.dtEvasione ";

        TypedQuery<RichiestaDataMartDTO> q = getEntityManager().createQuery(queryStr,
                RichiestaDataMartDTO.class);
        q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());
        List<RichiestaDataMartDTO> lista = q.getResultList();
        if (lista.size() == 1) {
            return lista.get(0);
        }
        return null;
    }

    /**
     * Normalizza l'intervallo di date per le query del datamart: imposta l'ora di {@code data_da} a
     * 00:00:00.000 (default: 1° gennaio 2000) e l'ora di {@code data_a} a 23:59:59.999 (default:
     * data odierna).
     *
     * @param data_da data di inizio intervallo; se {@code null} viene impostata al 1° gennaio 2000
     * @param data_a  data di fine intervallo; se {@code null} viene impostata alla data odierna
     *
     * @return array di due date: {@code [data_da normalizzata, data_a normalizzata]}
     */
    private Date[] normalizeDateForDataMart(Date data_da, Date data_a) {
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
     * Costruisce e restituisce una {@link Query} JPA per recuperare le unità documentarie di una
     * richiesta filtrate per stato.
     *
     * @param idRichiesta         identificativo della richiesta (ID_RICHIESTA)
     * @param tiStatoUdCancellate stato delle UD da filtrare (es. 'DA_CANCELLARE', 'CANCELLABILE')
     *
     * @return la {@link Query} pronta per l'esecuzione
     */
    public Query getUdDataMartByStatoUdQuery(BigDecimal idRichiesta, String tiStatoUdCancellate) {
        String queryStr = "SELECT dm FROM DmUdDel dm WHERE dm.idRichiesta = :idRichiesta AND dm.tiStatoUdCancellate = :tiStatoUdCancellate ORDER BY dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idRichiesta", idRichiesta);
        q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);
        return q;
    }

    /**
     * Costruisce e restituisce una {@link Query} JPA per recuperare le unità documentarie del
     * datamart, applicando i filtri opzionali forniti e ordinando per ente, struttura, registro,
     * anno e chiave UD.
     *
     * @param tiMotCancellazione    tipo di motivo di cancellazione; se {@code null} ignorato
     * @param idEnte                identificativo dell'ente; se {@code null} ignorato
     * @param idStrut               identificativo della struttura; se {@code null} ignorato
     * @param cdRegistroKeyUnitaDoc codice registro UD; se {@code null} ignorato
     * @param aaKeyUnitaDoc         anno chiave UD; se {@code null} ignorato
     * @param cdKeyUnitaDoc         chiave UD; se {@code null} ignorato
     * @param idRichiesta           identificativo della richiesta; se {@code null} ignorato
     * @param tiStatoUdCancellate   stato della UD; se {@code null} ignorato
     *
     * @return la {@link Query} pronta per l'esecuzione
     */
    public Query getUdDataMartQuery(String tiMotCancellazione, BigDecimal idEnte,
            BigDecimal idStrut, String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, BigDecimal idRichiesta, String tiStatoUdCancellate) {
        String whereWord = " WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT dm FROM DmUdDel dm ");

        if (tiMotCancellazione != null) {
            queryStr.append(whereWord).append("dm.tiMotCancellazione = :tiMotCancellazione ");
            whereWord = " AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("dm.idEnte = :idEnte ");
            whereWord = " AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("dm.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (cdRegistroKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (cdKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (idRichiesta != null) {
            queryStr.append(whereWord).append("dm.idRichiesta = :idRichiesta ");
            whereWord = " AND ";
        }
        if (tiStatoUdCancellate != null) {
            queryStr.append(whereWord).append("dm.tiStatoUdCancellate = :tiStatoUdCancellate ");
            whereWord = " AND ";
        }

        queryStr.append(
                "ORDER BY dm.nmEnte, dm.nmStrut, dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc  ");

        Query q = getEntityManager().createQuery(queryStr.toString());

        if (tiMotCancellazione != null) {
            q.setParameter("tiMotCancellazione", tiMotCancellazione);
        }
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            q.setParameter("idStrut", idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            q.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            q.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }
        if (idRichiesta != null) {
            q.setParameter("idRichiesta", idRichiesta);
        }
        if (tiStatoUdCancellate != null) {
            q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);
        }

        return q;
    }

    /**
     * Costruisce e restituisce una {@link Query} JPA per recuperare le UD del datamart relative a
     * richieste di annullamento versamento (TI_MOT_CANCELLAZIONE = 'A'), filtrate per stato UD.
     *
     * @param idRichiesta         identificativo della richiesta
     * @param tiStatoUdCancellate stato delle UD da filtrare
     *
     * @return la {@link Query} pronta per l'esecuzione
     */
    public Query getDmUdDelAnnulVersQuery(BigDecimal idRichiesta, String tiStatoUdCancellate) {
        String queryStr = "SELECT dm FROM DmUdDel dm JOIN dm.dmUdDelRichieste dmRich "
                + "WHERE dmRich.idRichiesta = :idRichiesta AND dmRich.tiMotCancellazione = 'A' AND dm.tiStatoUdCancellate = :tiStatoUdCancellate "
                + "ORDER BY dm.nmEnte, dm.nmStrut, dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc ";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idRichiesta", idRichiesta);
        q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);

        return q;
    }

    /**
     * Costruisce e restituisce una {@link Query} JPA per recuperare le UD del datamart relative a
     * richieste di scarto versamento (TI_MOT_CANCELLAZIONE = 'S'), filtrate per stato UD.
     *
     * @param idRichiesta         identificativo della richiesta
     * @param tiStatoUdCancellate stato delle UD da filtrare
     *
     * @return la {@link Query} pronta per l'esecuzione
     */
    public Query getDmUdDelScartoVersQuery(BigDecimal idRichiesta, String tiStatoUdCancellate) {
        String queryStr = "SELECT dm FROM DmUdDel dm JOIN dm.dmUdDelRichieste dmRich "
                + "WHERE dmRich.idRichiesta = :idRichiesta AND dmRich.tiMotCancellazione = 'S' AND dm.tiStatoUdCancellate = :tiStatoUdCancellate "
                + "ORDER BY dm.nmEnte, dm.nmStrut, dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc ";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idRichiesta", idRichiesta);
        q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);

        return q;
    }

    /**
     * Costruisce e restituisce una {@link Query} JPA per recuperare le UD del datamart con filtri
     * multipli opzionali, incluso il collegamento alla tabella delle richieste.
     * <p>
     * Il parametro {@code tiStatoRichiesta} è mantenuto per retrocompatibilità ma viene ignorato,
     * in quanto lo stato si trova nella tabella storico {@code DmUdDelStatoRichiesta}.
     *
     * @param tiMotCancellazione    tipo di motivo di cancellazione; se {@code null} ignorato
     * @param tiStatoRichiesta      ignorato (mantenuto per retrocompatibilità)
     * @param idEnte                identificativo dell'ente; se {@code null} ignorato
     * @param idStrut               identificativo della struttura; se {@code null} ignorato
     * @param cdRegistroKeyUnitaDoc codice registro UD; se {@code null} ignorato
     * @param aaKeyUnitaDoc         anno chiave UD; se {@code null} ignorato
     * @param cdKeyUnitaDoc         chiave UD; se {@code null} ignorato
     * @param idUdDelRichiesta      PK della richiesta (ID_UD_DEL_RICHIESTA); se {@code null}
     *                              ignorato
     * @param tiStatoUdCancellate   stato della UD; se {@code null} ignorato
     *
     * @return la {@link Query} pronta per l'esecuzione
     */
    public Query getDmUdDelQuery(String tiMotCancellazione, String tiStatoRichiesta,
            BigDecimal idEnte, BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal idUdDelRichiesta,
            String tiStatoUdCancellate) {
        String whereWord = " WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT dm FROM DmUdDel dm ");

        if (tiMotCancellazione != null) {
            queryStr.append(whereWord).append("dm.tiMotCancellazione = :tiMotCancellazione ");
            whereWord = " AND ";
        }
        // tiStatoRichiesta è ora nella tabella storico DmUdDelStatoRichiesta, non in DmUdDel
        // il parametro viene ignorato per retrocompatibilità (sempre null nelle chiamate esistenti)
        if (idEnte != null) {
            queryStr.append(whereWord).append("dm.idEnte = :idEnte ");
            whereWord = " AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("dm.idStrut = :idStrut ");
            whereWord = " AND ";
        }
        if (cdRegistroKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (cdKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("dm.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
            whereWord = " AND ";
        }
        if (idUdDelRichiesta != null) {
            queryStr.append(whereWord)
                    .append("dm.dmUdDelRichieste.idUdDelRichiesta = :idUdDelRichiesta ");
            whereWord = " AND ";
        }
        if (tiStatoUdCancellate != null) {
            queryStr.append(whereWord).append("dm.tiStatoUdCancellate = :tiStatoUdCancellate ");
            whereWord = " AND ";
        }

        queryStr.append(
                "ORDER BY dm.nmEnte, dm.nmStrut, dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc, dm.dtVersamento  ");

        Query q = getEntityManager().createQuery(queryStr.toString());

        if (tiMotCancellazione != null) {
            q.setParameter("tiMotCancellazione", tiMotCancellazione);
        }
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            q.setParameter("idStrut", idStrut);
        }
        if (cdRegistroKeyUnitaDoc != null) {
            q.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            q.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }
        if (idUdDelRichiesta != null) {
            q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());
        }
        if (tiStatoUdCancellate != null) {
            q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);
        }

        return q;
    }

    /**
     * Popola il datamart per una richiesta di <strong>annullamento versamento</strong>: crea il
     * record master in {@code DM_UD_DEL_RICHIESTE} e inserisce massivamente le UD da cancellare in
     * {@code DM_UD_DEL} a partire dagli item annullati in {@code ARO_ITEM_RICH_ANNUL_VERS}.
     *
     * @param idRichiesta        identificativo della richiesta di annullamento versamento
     * @param cdRichiesta        codice della richiesta
     * @param tiMotCancellazione tipo motivo cancellazione (atteso {@code 'A'})
     * @param tiModDel           modalità di cancellazione
     *
     * @return numero di righe inserite in {@code DM_UD_DEL}
     */
    @Transactional
    public int populateDataMartUdCentroStellaAnnulVers(BigDecimal idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String tiModDel) {
        // --- PASSAGGIO 1: Creare e salvare la riga master in DM_UD_DEL_RICHIESTE ---
        Long idUdDelRichiesta = createDmUdDelRichieste(idRichiesta, cdRichiesta,
                CostantiDB.TiMotCancellazione.A.name(),
                CostantiDB.TiStatoRichiesta.DA_EVADERE.name(),
                CostantiDB.TiStatoInternoRich.INIZIALE.name(), tiModDel);

        int numRecordDmUdDel = 0;
        // ======================================================================
        // PASSAGGIO 2: Popolamento massivo della tabella figlia DM_UD_DEL per Annullamento
        // Versamenti
        // ======================================================================
        String insertParentSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL ( "
                + "    ID_UNITA_DOC, AA_KEY_UNITA_DOC, CD_KEY_UNITA_DOC, CD_REGISTRO_KEY_UNITA_DOC, DT_VERSAMENTO, ID_ENTE, NM_ENTE, ID_STRUT, NM_STRUT, ID_UD_DEL_RICHIESTA, TI_STATO_UD_CANCELLATE, DT_STATO_UD_CANCELLATE, FL_ANNUL) "
                + " SELECT DISTINCT "
                + "    item_rich.ID_UNITA_DOC, item_rich.AA_KEY_UNITA_DOC, item_rich.CD_KEY_UNITA_DOC, item_rich.CD_REGISTRO_KEY_UNITA_DOC, ud.DT_CREAZIONE, ente.ID_ENTE, ente.NM_ENTE, strut.ID_STRUT, strut.NM_STRUT, :idUdDelRichiesta, 'DA_CANCELLARE', SYSDATE, '1' "
                + "FROM ARO_ITEM_RICH_ANNUL_VERS item_rich "
                + "JOIN ARO_UNITA_DOC ud ON (ud.id_unita_doc = item_rich.id_unita_doc) "
                + "JOIN ORG_STRUT strut ON strut.ID_STRUT = ud.ID_STRUT "
                + "JOIN ORG_ENTE ente ON ente.ID_ENTE = strut.ID_ENTE "
                + "WHERE item_rich.ID_RICH_ANNUL_VERS = :idRichiesta AND item_rich.ti_stato_item = 'ANNULLATO' AND item_rich.ti_item_rich_annul_vers = 'UNI_DOC' ";

        // Esegui la query nativa, legando il valore di idRichiesta al parametro :idRichiesta
        numRecordDmUdDel = getEntityManager().createNativeQuery(insertParentSql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta)
                .setParameter("idRichiesta", idRichiesta).executeUpdate();

        return numRecordDmUdDel;

    }

    /**
     * Popola il datamart per una richiesta di <strong>restituzione archivio</strong>: crea il
     * record master in {@code DM_UD_DEL_RICHIESTE} e inserisce massivamente le UD da cancellare in
     * {@code DM_UD_DEL} a partire dalla vista {@code ARO_V_SEL_UD_SER_FASC_BY_ENTE_X_DM}.
     *
     * @param idRichiesta identificativo della richiesta di restituzione archivio
     * @param cdRichiesta codice della richiesta
     * @param tiModDel    modalità di cancellazione
     *
     * @return numero di righe inserite in {@code DM_UD_DEL}
     */
    //
    @Transactional
    public int populateDataMartUdCentroStellaRestArch(BigDecimal idRichiesta, String cdRichiesta,
            String tiModDel) {
        // --- PASSAGGIO 1: Creare e salvare la riga master in DM_UD_DEL_RICHIESTE ---
        Long idUdDelRichiesta = createDmUdDelRichieste(idRichiesta, cdRichiesta,
                CostantiDB.TiMotCancellazione.R.name(),
                CostantiDB.TiStatoRichiesta.DA_EVADERE.name(),
                CostantiDB.TiStatoInternoRich.INIZIALE.name(), tiModDel);

        int numRecordDmUdDel = 0;

        // Recupero l'id_strutroot
        String queryRootstrut = "SELECT u.orgStrut.idStrut FROM AroRichiestaRa u WHERE u.idRichiestaRa = :idRichiesta ";
        Long idRootstrut = (Long) getEntityManager().createQuery(queryRootstrut)
                .setParameter("idRichiesta", idRichiesta.longValue()).getSingleResult();

        // ======================================================================
        // PASSAGGIO 2: Popolamento massivo della tabella figlia DM_UD_DEL per Restituzione
        // Archivio
        // ======================================================================
        String insertParentSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL ( "
                + "    ID_UNITA_DOC, AA_KEY_UNITA_DOC, CD_KEY_UNITA_DOC, CD_REGISTRO_KEY_UNITA_DOC, DT_VERSAMENTO, ID_ENTE, NM_ENTE, ID_STRUT, NM_STRUT, ID_UD_DEL_RICHIESTA, TI_STATO_UD_CANCELLATE, DT_STATO_UD_CANCELLATE, FL_ANNUL) "
                + " SELECT DISTINCT "
                + "    ud.ID_UNITA_DOC, ud.AA_KEY_UNITA_DOC, ud.CD_KEY_UNITA_DOC, ud.CD_REGISTRO_KEY_UNITA_DOC, ud.DT_CREAZIONE, ente.ID_ENTE, ente.NM_ENTE, strut.ID_STRUT, strut.NM_STRUT, :idUdDelRichiesta, 'DA_CANCELLARE', SYSDATE, CASE WHEN ud.TI_ANNUL = 'ANNULLAMENTO' THEN '1' ELSE '0' END "
                + "FROM ARO_V_SEL_UD_SER_FASC_BY_ENTE_X_DM item_rich "
                + "JOIN ORG_STRUT strut ON strut.ID_STRUT = item_rich.ID_STRUT "
                + "JOIN ORG_ENTE ente ON ente.ID_ENTE = strut.ID_ENTE "
                + "JOIN ARO_UNITA_DOC ud ON item_rich.ID_STRUT = ud.ID_STRUT "
                + "WHERE item_rich.ID_ROOTSTRUT = :idRootstrut AND item_rich.ti_ele = '01_UNI_DOC' ";

        // Esegui la query nativa, legando il valore di idRichiesta al parametro :idRichiesta
        numRecordDmUdDel = getEntityManager().createNativeQuery(insertParentSql)
                .setParameter("idRootstrut", idRootstrut)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta).executeUpdate();

        return numRecordDmUdDel;

    }

    /**
     * Crea e persiste un nuovo record master in {@code DM_UD_DEL_RICHIESTE}, registra lo stato
     * interno iniziale nella tabella storico {@code DM_UD_DEL_STATO_RICHIESTA} e aggiorna il
     * puntatore {@code STATO_INTERNO_RICH_COR} nella riga appena creata.
     *
     * @param idRichiesta        identificativo della richiesta sorgente (es. di annullamento o
     *                           scarto)
     * @param cdRichiesta        codice della richiesta
     * @param tiMotCancellazione tipo motivo di cancellazione (es. 'A', 'S', 'R')
     * @param tiStatoRichiesta   stato iniziale della richiesta (es. 'DA_EVADERE')
     * @param tiStatoInternoRich codice stato interno iniziale (es. 'INIZIALE')
     * @param tiModDel           modalità di cancellazione
     *
     * @return la PK ({@code ID_UD_DEL_RICHIESTA}) della riga appena creata
     */
    public Long createDmUdDelRichieste(BigDecimal idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String tiStatoRichiesta, String tiStatoInternoRich,
            String tiModDel) {
        DmUdDelRichieste nuovaRichiesta = new DmUdDelRichieste();
        nuovaRichiesta.setIdRichiesta(idRichiesta);
        nuovaRichiesta.setCdRichiesta(cdRichiesta);
        nuovaRichiesta.setTiMotCancellazione(tiMotCancellazione);
        nuovaRichiesta.setTiStatoRichiesta(tiStatoRichiesta);
        nuovaRichiesta.setTiModDel(tiModDel);
        nuovaRichiesta.setDtCreazione(new Date());
        getEntityManager().persist(nuovaRichiesta);

        // Registra lo stato interno iniziale nella tabella storico (prima riga per questa
        // richiesta: PG = 1)
        DmUdDelDecodStatoInterno decodIniziale = getEntityManager()
                .createQuery(
                        "SELECT d FROM DmUdDelDecodStatoInterno d WHERE d.tiStatoInternoRich = :ti",
                        DmUdDelDecodStatoInterno.class)
                .setParameter("ti", tiStatoInternoRich).getSingleResult();
        DmUdDelStatoRichiesta statoIniziale = new DmUdDelStatoRichiesta();
        statoIniziale.setDmUdDelRichieste(nuovaRichiesta);
        statoIniziale.setDecodStatoInterno(decodIniziale);
        statoIniziale.setDtRegStato(new Date());
        statoIniziale.setPgStatoRich(BigDecimal.ONE);
        getEntityManager().persist(statoIniziale);
        getEntityManager().flush(); // garantisce che statoIniziale.idStatoUdDelRichiesta sia
                                    // valorizzato

        // Aggiorna il puntatore FK allo stato corrente
        nuovaRichiesta.setStatoInternoRichCor(statoIniziale);
        getEntityManager().merge(nuovaRichiesta);

        return nuovaRichiesta.getIdUdDelRichiesta();
    }

    /**
     * Popola il datamart per una richiesta di <strong>scarto versamento</strong>: crea il record
     * master in {@code DM_UD_DEL_RICHIESTE} e inserisce massivamente le UD da cancellare in
     * {@code DM_UD_DEL} a partire dagli item scartati in {@code ARO_ITEM_RICH_SCARTO_VERS}.
     *
     * @param idRichiesta        identificativo della richiesta di scarto versamento
     * @param cdRichiesta        codice della richiesta
     * @param tiMotCancellazione tipo motivo di cancellazione
     * @param tiModDel           modalità di cancellazione
     *
     * @return numero di righe inserite in {@code DM_UD_DEL}
     */
    @Transactional
    public int populateDataMartScartoUdCentroStella(long idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String tiModDel) {
        // --- PASSAGGIO 1: Creare e salvare la riga master in DM_UD_DEL_RICHIESTE con stato
        // iniziale ---
        Long idUdDelRichiesta = createDmUdDelRichieste(BigDecimal.valueOf(idRichiesta), cdRichiesta,
                tiMotCancellazione, CostantiDB.TiStatoRichiesta.DA_EVADERE.name(),
                CostantiDB.TiStatoInternoRich.INIZIALE.name(), tiModDel);

        // ID della richiesta corrente, da passare come parametro
        int numRecordDmUdDel = 0;

        // ======================================================================
        // PASSAGGIO 1: Popolamento massivo della tabella padre DM_UD_DEL per Annullamento
        // Versamenti
        // ======================================================================
        String insertParentSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL ( "
                + "    ID_UNITA_DOC, AA_KEY_UNITA_DOC, CD_KEY_UNITA_DOC, CD_REGISTRO_KEY_UNITA_DOC, DT_VERSAMENTO, ID_ENTE, NM_ENTE, ID_STRUT, NM_STRUT, ID_UD_DEL_RICHIESTA, TI_STATO_UD_CANCELLATE, DT_STATO_UD_CANCELLATE, FL_ANNUL) "
                + " SELECT DISTINCT "
                + "    item_rich.ID_UNITA_DOC, item_rich.AA_KEY_UNITA_DOC, item_rich.CD_KEY_UNITA_DOC, item_rich.CD_REGISTRO_KEY_UNITA_DOC, ud.DT_CREAZIONE, ente.ID_ENTE, ente.NM_ENTE, strut.ID_STRUT, strut.NM_STRUT, :idUdDelRichiesta, 'DA_CANCELLARE', SYSDATE, CASE WHEN ud.TI_ANNUL = 'ANNULLAMENTO' THEN '1' ELSE '0' END "
                + "FROM ARO_ITEM_RICH_SCARTO_VERS item_rich "
                + "JOIN ARO_UNITA_DOC ud ON (ud.id_unita_doc = item_rich.id_unita_doc) "
                + "JOIN ORG_STRUT strut ON strut.ID_STRUT = ud.ID_STRUT "
                + "JOIN ORG_ENTE ente ON ente.ID_ENTE = strut.ID_ENTE "
                + "WHERE item_rich.ID_RICH_SCARTO_VERS = :idRichiesta AND item_rich.ti_stato_item_scarto = 'SCARTATO' ";

        // Esegui la query nativa, legando il valore di idRichiesta al parametro :idRichiesta
        numRecordDmUdDel = getEntityManager().createNativeQuery(insertParentSql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta)
                .setParameter("idRichiesta", idRichiesta).executeUpdate();

        return numRecordDmUdDel;

    }

    /**
     * Popola le tabelle satellite del datamart ({@code DM_UD_DEL_FAS}, {@code DM_UD_DEL_SER},
     * {@code DM_UD_DEL_OBJECT_STORAGE}) con i dati di fascicoli, serie e object-storage associati
     * alle UD della richiesta indicata. Al termine aggiorna lo stato delle UD a
     * {@code CANCELLABILE}.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void populateDataMartUdSatelliti(long idUdDelRichiesta) {
        int numRecordDmUdDelFas = 0;
        int numRecordDmUdDelSer = 0;

        // NOTA: Con JPA, il COMMIT è gestito dall'annotazione @Transactional alla fine del metodo.
        // Se non si usa @Transactional, servirebbe un commit manuale qui, ma non è la prassi.
        // Tuttavia, per rendere visibili i dati al passaggio 2 NELLA STESSA TRANSAZIONE,
        // non serve un COMMIT intermedio. Il DB vede i dati non ancora committati.

        log.info("Avvio popolamento Data Mart Fascicoli per richiesta {}", idUdDelRichiesta);
        String insertFascicoliSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL_FAS ( "
                + "    ID_UNITA_DOC, " + "    AA_FASCICOLO, " + "    CD_KEY_FASCICOLO, "
                + "    DT_ANNULL " + ") " + "SELECT " + "    u.ID_UNITA_DOC, "
                + "    fasc.AA_FASCICOLO, " + "    fasc.CD_KEY_FASCICOLO, " + "    fasc.DT_ANNULL "
                + "FROM " + "    DM_UD_DEL u "
                + "    JOIN FAS_UNITA_DOC_FASCICOLO raccordo ON u.ID_UNITA_DOC = raccordo.ID_UNITA_DOC "
                + "    JOIN FAS_FASCICOLO fasc ON raccordo.ID_FASCICOLO = fasc.ID_FASCICOLO "
                + "WHERE " + "    u.ID_UD_DEL_RICHIESTA = :idUdDelRichiesta ";

        numRecordDmUdDelFas = getEntityManager().createNativeQuery(insertFascicoliSql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta).executeUpdate();
        log.info("Inseriti {} record in DM_UD_DEL_FAS", numRecordDmUdDelFas);

        log.info("Avvio popolamento Data Mart Serie per richiesta {}", idUdDelRichiesta);
        String insertSerieSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL_SER ( " + "    ID_UNITA_DOC, "
                + "    AA_SERIE, " + "    CD_COMPOSITO_SERIE, " + "    DT_ANNULL " + ") "
                + "SELECT " + "    u.ID_UNITA_DOC, " + "    serie_master.AA_SERIE, "
                + "    serie_master.CD_COMPOSITO_SERIE, " + "    serie_master.DT_ANNUL " + "FROM "
                + "    DM_UD_DEL u "
                + "    JOIN ARO_UD_APPART_VER_SERIE appart ON u.id_unita_doc = appart.id_unita_doc "
                + "    JOIN SER_CONTENUTO_VER_SERIE contenuto ON appart.id_contenuto_ver_serie = contenuto.id_contenuto_ver_serie "
                + "    JOIN SER_VER_SERIE versione ON contenuto.id_ver_serie = versione.id_ver_serie "
                + "    JOIN SER_SERIE serie_master ON versione.id_serie = serie_master.id_serie "
                + "WHERE " + "    u.ID_UD_DEL_RICHIESTA = :idUdDelRichiesta ";

        // L'esecuzione della query rimane identica
        numRecordDmUdDelSer = getEntityManager().createNativeQuery(insertSerieSql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta).executeUpdate();
        log.info("Inseriti {} record in DM_UD_DEL_SER", numRecordDmUdDelSer);

        log.info("Avvio popolamento Data Mart Object Storage per richiesta {}", idUdDelRichiesta);

        // 2. Recupera in un colpo solo tutti i metadati per le tabelle _OBJECT_STORAGE
        TypedQuery<DmUdDelRecRefTab> metadataQuery = getEntityManager().createQuery(
                "SELECT m FROM DmUdDelRecRefTab m WHERE m.idUnitaDoc IN (SELECT d.idUnitaDoc FROM DmUdDel d WHERE d.dmUdDelRichieste.idUdDelRichiesta = :idUdDelRichiesta)"
                        + " AND m.nmTab LIKE '%\\_OBJECT\\_STORAGE' ESCAPE '\\'",
                DmUdDelRecRefTab.class);
        metadataQuery.setParameter("idUdDelRichiesta", idUdDelRichiesta);
        List<DmUdDelRecRefTab> objectStorageMetadatas = metadataQuery.getResultList();

        List<DmUdDelObjectStorage> recordsToInsert = new ArrayList<>();

        // 3. Itera sui metadati ed esegui una SELECT per ogni record per recuperare i dati
        for (DmUdDelRecRefTab metadata : objectStorageMetadatas) {
            // Nota: NM_COLUMN_PK non viene usato perché ipotizziamo di conoscere
            // il nome della colonna PK nelle tabelle di storage, come da esempio.
            // Se fosse dinamico, andrebbe incluso nella query.
            String sql = String.format(
                    "SELECT NM_TENANT, NM_BUCKET, CD_KEY_FILE, ID_DEC_BACKEND FROM %s WHERE %s = :pkValue",
                    metadata.getNmTab(), metadata.getNmColumnPk() // Uso la colonna PK dinamica dal
            // metadato
            );

            Query nativeQuery = getEntityManager().createNativeQuery(sql);
            nativeQuery.setParameter("pkValue", metadata.getIdPkRecTab());

            try {
                Object[] result = (Object[]) nativeQuery.getSingleResult();
                recordsToInsert
                        .add(mapResultArrayToObjectStorage(result, metadata.getIdUnitaDoc()));
            } catch (NoResultException e) {
                log.warn("Nessun record trovato nella tabella {} con PK {}. Salto il record.",
                        metadata.getNmTab(), metadata.getIdPkRecTab());
            }
        }

        // 4. Inserimento massivo (batch insert) dei record raccolti
        if (!recordsToInsert.isEmpty()) {
            log.info("Inizio inserimento di {} record in DM_UD_DEL_OBJECT_STORAGE...",
                    recordsToInsert.size());
            final int batchSize = 50; // Deve corrispondere a hibernate.jdbc.batch_size
            for (int i = 0; i < recordsToInsert.size(); i++) {
                getEntityManager().persist(recordsToInsert.get(i));
                if (i > 0 && i % batchSize == 0) {
                    getEntityManager().flush(); // Sincronizza il batch con il DB
                    getEntityManager().clear(); // Svuota la cache per liberare memoria
                }
            }
        }

        log.info("Inseriti {} record in DM_UD_DEL_OBJECT_STORAGE", recordsToInsert.size());

        // Terminato il popolamento dei satelliti, aggiorno lo stato in DM_UD_DEL
        String updateCentroStella = "UPDATE DmUdDel d " + "SET d.tiStatoUdCancellate = :nuovoStato "
                + "WHERE d.dmUdDelRichieste.idUdDelRichiesta = :idUdDelRichiesta";
        // Per le query UPDATE/DELETE si usa la generica interfaccia Query
        Query query = getEntityManager().createQuery(updateCentroStella);

        query.setParameter("nuovoStato", "CANCELLABILE");
        query.setParameter("idUdDelRichiesta", idUdDelRichiesta);

        int righeAggiornate = query.executeUpdate();

        log.info("Aggiornate {} unità documentarie in DM_UD_DEL assegnando stato CANCELLABILE",
                righeAggiornate);

        log.info("Popolamento per richiesta {} completato con successo.", idUdDelRichiesta);
    }

    /**
     * Funzione helper per mappare un array di Object restituito da una query nativa in una nuova
     * istanza dell'entità DmUdDelObjectStorage.
     */
    private DmUdDelObjectStorage mapResultArrayToObjectStorage(Object[] result,
            BigDecimal idUnitaDoc) {
        DmUdDelObjectStorage udDelOs = new DmUdDelObjectStorage();
        udDelOs.setIdUnitaDoc(idUnitaDoc);
        udDelOs.setNmTenant((String) result[0]);
        udDelOs.setNmBucket((String) result[1]);
        udDelOs.setCdKeyFile((String) result[2]);
        udDelOs.setIdDecBackend((BigDecimal) result[3]);
        return udDelOs;
    }

    /**
     * Verifica se una data richiesta contiene unità documentarie con stato 'DA_CANCELLARE' in modo
     * efficiente, fermandosi alla prima occorrenza.
     *
     * @param idRichiesta L'ID della richiesta da controllare.
     *
     * @return {@code false} se esiste almeno un'unità con stato 'DA_CANCELLARE', {@code true}
     *         altrimenti.
     */
    public boolean existsUdDaCancellare(Long idRichiesta) {
        String queryStr = "SELECT 1 " + "FROM DmUdDel d " + "WHERE d.idRichiesta = :idRichiesta "
                + "  AND d.tiStatoUdCancellate = 'DA_CANCELLARE'";

        TypedQuery<Integer> query = getEntityManager().createQuery(queryStr, Integer.class);
        query.setParameter("idRichiesta", idRichiesta);
        query.setMaxResults(1);
        List<Integer> results = query.getResultList();
        return !results.isEmpty();
    }

    /**
     * Verifica se TUTTE le unità documentali di una data richiesta hanno lo stato 'DA_CANCELLARE'.
     *
     * @param idRichiesta L'ID della richiesta da controllare.
     *
     * @return {@code true} se tutte le unità hanno lo stato corretto (o se non ci sono unità),
     *         {@code false} se anche solo una ha uno stato diverso.
     */
    public boolean allUdDaCancellare(Long idRichiesta) {
        // Contiamo le righe che hanno uno stato DIVERSO da 'DA_CANCELLARE' o NULL.
        // Se questo conteggio è 0, allora tutte le righe esistenti sono conformi.
        String jpql = "SELECT COUNT(d) " + "FROM DmUdDel d " + "WHERE d.idRichiesta = :idRichiesta "
                + "  AND (d.tiStatoUdCancellate <> 'DA_CANCELLARE' OR d.tiStatoUdCancellate IS NULL)";

        TypedQuery<Long> query = getEntityManager().createQuery(jpql, Long.class);
        query.setParameter("idRichiesta", BigDecimal.valueOf(idRichiesta));

        Long conteggioNonConformi = query.getSingleResult();

        // Se il numero di righe "sbagliate" è zero, la condizione è soddisfatta.
        return conteggioNonConformi == 0;
    }

    /**
     * Esegue l'update dello stato delle singole UD a 'CANCELLABILE'. Deve essere chiamato
     * all'interno di una transazione esistente.
     *
     * @param idUdDelRichiesta L'ID della richiesta.
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY) // Assicura che sia sempre chiamato
    // dentro una TX
    public void aggiornaStatoUdaCancellabili(BigDecimal idUdDelRichiesta) {
        String sql = "UPDATE DM_UD_DEL SET TI_STATO_UD_CANCELLATE = 'CANCELLABILE' WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta AND TI_STATO_UD_CANCELLATE = 'DA_CANCELLARE'";
        getEntityManager().createNativeQuery(sql).setParameter("idUdDelRichiesta", idUdDelRichiesta)
                .executeUpdate();
    }

    /**
     * Aggiorna la data di stato ({@code DT_STATO_UD_CANCELLATE}) a {@code SYSDATE} per tutte le UD
     * con stato {@code CANCELLABILE} appartenenti alla richiesta indicata. Deve essere chiamato
     * all'interno di una transazione esistente.
     *
     * @param idUdDelRichiesta L'ID della richiesta.
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY) // Assicura che sia sempre chiamato
    // dentro una TX
    public void aggiornaDtStatoUdCancellabili(BigDecimal idUdDelRichiesta) {
        String sql = "UPDATE DM_UD_DEL SET DT_STATO_UD_CANCELLATE = SYSDATE WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta AND TI_STATO_UD_CANCELLATE = 'CANCELLABILE'";
        getEntityManager().createNativeQuery(sql).setParameter("idUdDelRichiesta", idUdDelRichiesta)
                .executeUpdate();
    }

    /**
     * Recupera i conteggi delle unità documentarie raggruppati per stato per una specifica
     * richiesta, usando JPA-QL con una Constructor Expression.
     *
     * @param idUdDelRichiesta L'ID della richiesta da cui estrarre i dati.
     *
     * @return Una lista di DTO (ConteggioStatoUdDto), che è molto più type-safe.
     */
    public List<ConteggioStatoUdDto> getUdCountsByStatoForRichiestaDtoJPA(
            BigDecimal idUdDelRichiesta) {

        String jpaQueryString = "SELECT NEW it.eng.parer.datamart.dto.ConteggioStatoUdDto("
                + "    dmRich.idUdDelRichiesta, dmRich.idRichiesta, "
                + "    dmRich.tiMotCancellazione, "
                + "    dm.idEnte, dm.nmEnte, dm.idStrut, dm.nmStrut, " //
                + "    dm.tiStatoUdCancellate, " + "    COUNT(dm.idUnitaDoc), "
                + "    SUM(CASE WHEN dm.flAnnul = '1' THEN 1L ELSE 0L END) " + ") " + "FROM "
                + "    DmUdDel dm JOIN dm.dmUdDelRichieste dmRich " + "WHERE "
                + "    dmRich.idUdDelRichiesta = :idUdDelRichiesta " + "GROUP BY "
                + "    dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.tiMotCancellazione, dm.idEnte, dm.nmEnte, dm.idStrut, dm.nmStrut, dm.tiStatoUdCancellate "
                + "ORDER BY " + "    dm.tiStatoUdCancellate";

        // Uso una TypedQuery per ottenere una lista tipizzata senza cast manuali.
        TypedQuery<ConteggioStatoUdDto> q = getEntityManager().createQuery(jpaQueryString,
                ConteggioStatoUdDto.class);
        q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());

        return q.getResultList();
    }

    /**
     * Cancella le richieste di soft-delete ({@code ARO_RICH_SOFT_DELETE}) associate agli item della
     * richiesta Sacer indicata e al tipo item specificato.
     *
     * @param idRichiesta          identificativo della richiesta Sacer
     * @param tiItemRichSoftDelete tipo item di soft-delete (es. 'ANNUL_VERS', 'SCARTO_VERS')
     */
    public void deleteAroRichSoftDelete(BigDecimal idRichiesta, String tiItemRichSoftDelete) {
        String queryStr = "DELETE FROM ARO_RICH_SOFT_DELETE WHERE ID_RICH_SOFT_DELETE IN "
                + "(SELECT item.id_rich_soft_delete FROM ARO_ITEM_RICH_SOFT_DELETE item "
                + "WHERE item.id_richiesta_sacer = :idRichiesta "
                + "and item.ti_item_rich_soft_delete = :tiItemRichSoftDelete) ";
        Query query = getEntityManager().createNativeQuery(queryStr);
        query.setParameter("idRichiesta", idRichiesta);
        query.setParameter("tiItemRichSoftDelete", tiItemRichSoftDelete);
        query.executeUpdate();
    }

    // =================================================================================
    // METODI PER LA CANCELLAZIONE FISICA
    // =================================================================================

    /**
     * Inserisce una nuova riga nella tabella storico stati DM_UD_DEL_STATO_RICHIESTA con FK verso
     * la tabella di decodifica DM_UD_DEL_DECOD_STATO_INTERNO.
     * <p>
     * PG_STATO_RICH è calcolato come MAX(PG_STATO_RICH)+1 per la richiesta corrente: i progressivi
     * partono da 1 e sono univoci nell'ambito della singola richiesta.
     */
    private DmUdDelStatoRichiesta inserisciStatoRich(long idUdDelRichiesta,
            String tiStatoInternoRich) {
        // Calcola il prossimo progressivo come MAX+1 per questa richiesta (scope locale)
        BigDecimal pg = (BigDecimal) getEntityManager().createNativeQuery(
                "SELECT NVL(MAX(PG_STATO_RICH), 0) + 1 FROM DM_UD_DEL_STATO_RICHIESTA WHERE ID_UD_DEL_RICHIESTA = :id")
                .setParameter("id", idUdDelRichiesta).getSingleResult();

        // Recupera il record di decodifica tramite il codice tecnico
        DmUdDelDecodStatoInterno decod = getEntityManager()
                .createQuery(
                        "SELECT d FROM DmUdDelDecodStatoInterno d WHERE d.tiStatoInternoRich = :ti",
                        DmUdDelDecodStatoInterno.class)
                .setParameter("ti", tiStatoInternoRich).getSingleResult();

        DmUdDelStatoRichiesta stato = new DmUdDelStatoRichiesta();
        stato.setDmUdDelRichieste(
                getEntityManager().getReference(DmUdDelRichieste.class, idUdDelRichiesta));
        stato.setDecodStatoInterno(decod);
        stato.setDtRegStato(new Date());
        stato.setPgStatoRich(pg);
        getEntityManager().persist(stato);
        getEntityManager().flush(); // garantisce che stato.idStatoUdDelRichiesta sia valorizzato

        // Aggiorna il puntatore FK allo stato corrente nella tabella principale
        getEntityManager()
                .createQuery("UPDATE DmUdDelRichieste r SET r.statoInternoRichCor = :stato "
                        + "WHERE r.idUdDelRichiesta = :id")
                .setParameter("stato", stato).setParameter("id", idUdDelRichiesta).executeUpdate();
        return stato;
    }

    /**
     * Registra il nuovo stato della richiesta nella tabella storico stati. Viene eseguito in una
     * NUOVA transazione per garantire che il commit sia immediato, rendendo lo stato visibile
     * subito dopo la chiamata, indipendentemente dalla transazione del chiamante.
     *
     * @param idRichiesta L'ID della richiesta (ID_RICHIESTA, non la PK della tabella).
     * @param nuovoStato  Il nuovo stato da impostare (es. 'DA_EVADERE', 'EVASA').
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void impostaStatoRichiesta(BigDecimal idRichiesta, String nuovoStato) {
        log.info("[TX-STATUS] Aggiornando stato per richiesta {} a '{}'", idRichiesta, nuovoStato);

        int updated = getEntityManager()
                .createQuery("UPDATE DmUdDelRichieste r SET r.tiStatoRichiesta = :stato "
                        + "WHERE r.idRichiesta = :idRichiesta")
                .setParameter("stato", nuovoStato).setParameter("idRichiesta", idRichiesta)
                .executeUpdate();
        if (updated == 0) {
            throw new EJBException("Nessuna richiesta trovata per ID_RICHIESTA: " + idRichiesta);
        }

        // Se lo stato è EVASA, aggiorna anche DT_EVASIONE nella tabella principale
        if (CostantiDB.TiStatoRichiesta.EVASA.name().equals(nuovoStato)) {
            getEntityManager().createQuery(
                    "UPDATE DmUdDelRichieste r SET r.dtEvasione = :now WHERE r.idRichiesta = :idRichiesta")
                    .setParameter("now", new Date()).setParameter("idRichiesta", idRichiesta)
                    .executeUpdate();
        }

        log.info("[TX-STATUS] Aggiornamento stato per richiesta {} completato.", idRichiesta);
    }

    /**
     * Aggiorna lo stato interno della richiesta inserendo una nuova riga nello storico e,
     * facoltativamente, salvando un messaggio di errore nel record principale.
     *
     * @param idUdDelRichiesta  la PK della richiesta (ID_UD_DEL_RICHIESTA)
     * @param nuovoStatoInterno il codice del nuovo stato interno (es. 'IN_CANCELLAZIONE_FISICA')
     * @param messaggioErrore   messaggio di errore da salvare in {@code DS_MESSAGGIO_ERRORE};
     *                          {@code null} se non applicabile
     *
     * @return il progressivo ({@code PG_STATO_RICH}) della riga di stato appena creata, oppure
     *         {@code null} se la creazione non è riuscita
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta,
            String nuovoStatoInterno, String messaggioErrore) {
        log.info("[TX-STATUS] Aggiornando stato INTERNO per richiesta {} a '{}'", idUdDelRichiesta,
                nuovoStatoInterno);

        DmUdDelStatoRichiesta stato = inserisciStatoRich(idUdDelRichiesta.longValue(),
                nuovoStatoInterno);

        // Il messaggio di errore resta nel record principale della richiesta
        if (messaggioErrore != null) {
            int updated = getEntityManager()
                    .createQuery("UPDATE DmUdDelRichieste r SET r.dsMessaggioErrore = :msg "
                            + "WHERE r.idUdDelRichiesta = :id")
                    .setParameter("msg", messaggioErrore)
                    .setParameter("id", idUdDelRichiesta.longValue()).executeUpdate();
            if (updated == 0) {
                throw new EJBException("Nessuna richiesta trovata per ID: " + idUdDelRichiesta);
            }
        }
        return stato.getPgStatoRich();
    }

    /**
     * Overload di {@link #impostaStatoInternoRichiesta(BigDecimal, String, String)} senza messaggio
     * di errore.
     *
     * @param idUdDelRichiesta  la PK della richiesta (ID_UD_DEL_RICHIESTA)
     * @param nuovoStatoInterno il codice del nuovo stato interno
     *
     * @return il progressivo ({@code PG_STATO_RICH}) della riga di stato appena creata, oppure
     *         {@code null} se la creazione non è riuscita
     */
    public BigDecimal impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta,
            String nuovoStatoInterno) {
        return impostaStatoInternoRichiesta(idUdDelRichiesta, nuovoStatoInterno, null);
    }

    /**
     * Restituisce il codice dello stato esterno ({@code TI_STATO_RICHIESTA}) della richiesta
     * indicata.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     *
     * @return il codice stato, oppure {@code 'DA_EVADERE'} se la richiesta non viene trovata
     */
    public String getStatoRichiesta(BigDecimal idUdDelRichiesta) {
        try {
            String jpql = "SELECT r.tiStatoRichiesta FROM DmUdDelRichieste r "
                    + "WHERE r.idUdDelRichiesta = :id";
            return (String) getEntityManager().createQuery(jpql)
                    .setParameter("id", idUdDelRichiesta.longValue()).getSingleResult();
        } catch (NoResultException e) {
            return CostantiDB.TiStatoRichiesta.DA_EVADERE.name();
        }
    }

    /**
     * Restituisce il codice dello stato interno corrente ({@code TI_STATO_INTERNO_RICH}) della
     * richiesta indicata, navigando la FK {@code STATO_INTERNO_RICH_COR}.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     *
     * @return il codice stato interno, oppure {@code 'NON_TROVATA'} se la richiesta non esiste o
     *         non ha uno stato corrente valorizzato
     */
    public String getStatoInternoRichiesta(BigDecimal idUdDelRichiesta) {
        try {
            String jpql = "SELECT r.statoInternoRichCor.decodStatoInterno.tiStatoInternoRich FROM DmUdDelRichieste r "
                    + "WHERE r.idUdDelRichiesta = :id AND r.statoInternoRichCor IS NOT NULL";
            return (String) getEntityManager().createQuery(jpql)
                    .setParameter("id", idUdDelRichiesta.longValue()).getSingleResult();
        } catch (NoResultException e) {
            return "NON_TROVATA";
        }
    }

    /**
     * Restituisce la mappa {@code TI_STATO_INTERNO_RICH -> DS_STATO_INTERNO_RICH} leggendo l'intera
     * tabella di decodifica {@code DM_UD_DEL_DECOD_STATO_INTERNO}. Utile per risolvere il codice
     * tecnico dello stato in una descrizione leggibile, ad esempio per popolare la colonna
     * {@code DS_STATO_INTERNO_RICH} nelle liste di visualizzazione.
     *
     * @return mappa non {@code null} con tutti gli stati interni disponibili; può essere vuota se
     *         la tabella di decodifica non contiene righe
     */
    public Map<String, String> getDecodificaStatiInterni() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = getEntityManager().createNativeQuery(
                "SELECT TI_STATO_INTERNO_RICH, DS_STATO_INTERNO_RICH FROM DM_UD_DEL_DECOD_STATO_INTERNO")
                .getResultList();
        Map<String, String> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (String) row[1]);
        }
        return map;
    }

    /**
     * Recupera in un'unica query la descrizione dello stato interno corrente per un insieme di
     * richieste.
     *
     * @param ids lista di {@code ID_UD_DEL_RICHIESTA} da interrogare
     *
     * @return mappa {@code ID_UD_DEL_RICHIESTA -> descrizione stato interno}; le richieste prive di
     *         stato corrente vengono escluse
     */
    public Map<Long, String> getStatiInterniRichieste(List<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        // Legge il codice stato e la sua descrizione dalla tabella di decodifica in un'unica query
        @SuppressWarnings("unchecked")
        List<Object[]> rows = getEntityManager().createNativeQuery(
                "SELECT r.ID_UD_DEL_RICHIESTA, NVL(d.DS_STATO_INTERNO_RICH, d.TI_STATO_INTERNO_RICH) "
                        + "FROM DM_UD_DEL_RICHIESTE r "
                        + "JOIN DM_UD_DEL_STATO_RICHIESTA s ON s.ID_STATO_UD_DEL_RICHIESTA = r.ID_STATO_INTERNO_RICH_COR "
                        + "JOIN DM_UD_DEL_DECOD_STATO_INTERNO d ON d.ID_DECOD_STATO_INTERNO = s.ID_DECOD_STATO_INTERNO "
                        + "WHERE r.ID_UD_DEL_RICHIESTA IN :ids AND r.ID_STATO_INTERNO_RICH_COR IS NOT NULL")
                .setParameter("ids", ids).getResultList();
        for (Object[] row : rows) {
            result.put(((Number) row[0]).longValue(), (String) row[1]);
        }
        return result;
    }

    /**
     * Restituisce la modalità di cancellazione ({@code TI_MOD_DEL}) della richiesta indicata.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     *
     * @return il valore di {@code TI_MOD_DEL}, oppure {@code 'COMPLETA'} se la richiesta non viene
     *         trovata
     */
    public String getTiModDelRichiesta(BigDecimal idUdDelRichiesta) {
        try {
            String sql = "SELECT TI_MOD_DEL FROM DM_UD_DEL_RICHIESTE WHERE ID_UD_DEL_RICHIESTA = :id";
            return (String) getEntityManager().createNativeQuery(sql)
                    .setParameter("id", idUdDelRichiesta).getSingleResult();
        } catch (NoResultException e) {
            return "COMPLETA";
        }
    }

    /**
     * CALCOLO AVANZAMENTO LOGICO: Calcola quante UD sono state elaborate da Kafka contando i record
     * distinti nel satellite DM_UD_DEL_REC_REF_TAB per una data richiesta.
     *
     * @param idRichiesta L'ID della richiesta.
     *
     * @return Il numero di UD elaborate.
     */
    public long getConteggioLogicoElaborati(BigDecimal idRichiesta) {
        // Questa query è la misura più affidabile per verificare l'avanzamento di Kafka.
        // Conta quante UD uniche della richiesta hanno almeno
        // un record nel satellite popolato da Kafka.
        String sql = "SELECT COUNT(DISTINCT d.ID_UNITA_DOC) " + "FROM DM_UD_DEL d "
                + "WHERE d.ID_RICHIESTA = :idRichiesta AND EXISTS ("
                + "  SELECT 1 FROM DM_UD_DEL_REC_REF_TAB r "
                + "  WHERE d.ID_UNITA_DOC = r.ID_UNITA_DOC" + ")";

        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idRichiesta", idRichiesta);
        Object result = q.getSingleResult();
        return (result instanceof Number) ? ((Number) result).longValue() : 0L;
    }

    /**
     * CALCOLO AVANZAMENTO FISICO (e Totale per entrambi): Calcola il numero totale di unità
     * documentarie associate a una data richiesta.
     *
     * @param idRichiesta L'ID della richiesta.
     *
     * @return Il numero totale di UD.
     */
    public long getConteggioTotaleUD(BigDecimal idRichiesta) {
        String sql = "SELECT COUNT(*) FROM DM_UD_DEL WHERE ID_RICHIESTA = :idRichiesta";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idRichiesta", idRichiesta);
        Object result = q.getSingleResult();
        return ((BigDecimal) result).longValue();
    }

    /**
     * CALCOLO AVANZAMENTO FISICO: Calcola il numero di unità documentarie che sono state cancellate
     * fisicamente (marcate come CANCELLATA_DB_SACER) per una data richiesta.
     *
     * @param idRichiesta L'ID della richiesta.
     *
     * @return Il numero di UD già cancellate.
     */
    public long getConteggioUDCancellate(BigDecimal idRichiesta) {
        String sql = "SELECT COUNT(*) FROM DM_UD_DEL WHERE ID_RICHIESTA = :idRichiesta AND TI_STATO_UD_CANCELLATE = 'CANCELLATA_DB_SACER'";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idRichiesta", idRichiesta);
        Object result = q.getSingleResult();
        return ((BigDecimal) result).longValue();
    }

    /**
     * Metodo di supporto per recuperare lo stato esterno dalla vista di Kafka
     *
     * @param idRichiesta          id richiesta per l'avvio del microservizio
     * @param tiItemRichSoftDelete tipo item
     *
     * @return il record con i flag che indicano lo stato della cancellazione logica
     */
    public AroVChkStatoCorRichSoftDelete checkRunMicroservizio(BigDecimal idRichiesta,
            String tiItemRichSoftDelete) {
        String queryStr = "SELECT chk FROM AroVChkStatoCorRichSoftDelete chk WHERE chk.idRichiestaSacer = :idRichiesta AND chk.tiItemRichSoftDelete = :tiItemRichSoftDelete";
        List<AroVChkStatoCorRichSoftDelete> results = getEntityManager()
                .createQuery(queryStr, AroVChkStatoCorRichSoftDelete.class)
                .setParameter("idRichiesta", idRichiesta)
                .setParameter("tiItemRichSoftDelete", tiItemRichSoftDelete).setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Esegue le operazioni di correzione sul database per permettere la ripresa di un processo di
     * cancellazione logica fallito in modo "ripristinabile". L'intera operazione è atomica.
     *
     * @param idRichiesta          L'ID della richiesta del datamart.
     * @param tiItemRichSoftDelete motivo cancellazione
     * @param idUserIam            id utente
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eseguiCorrezionePerRipresaLogica(BigDecimal idRichiesta,
            String tiItemRichSoftDelete, long idUserIam) {

        // PRE-REQUISITO: Trovare l'ID_RICH_SOFT_DELETE ---
        String idSoftDeleteNativeQueryStr = "SELECT ID_RICH_SOFT_DELETE FROM ARO_ITEM_RICH_SOFT_DELETE WHERE ID_RICHIESTA_SACER = :idRichiesta "
                + "AND TI_ITEM_RICH_SOFT_DELETE = :tiItemRichSoftDelete ";

        @SuppressWarnings("unchecked")
        List<BigDecimal> idRichSoftDeleteList = (List<BigDecimal>) getEntityManager()
                .createNativeQuery(idSoftDeleteNativeQueryStr)
                .setParameter("idRichiesta", idRichiesta)
                .setParameter("tiItemRichSoftDelete", tiItemRichSoftDelete).getResultList();

        if (idRichSoftDeleteList == null) {
            throw new IllegalStateException(
                    "Impossibile trovare un ID_RICH_SOFT_DELETE associato alla richiesta Sacer: "
                            + idRichiesta);
        }

        // Gestisco ogni singola richiesta di cancellazione logica
        for (BigDecimal idRichSoftDelete : idRichSoftDeleteList) {

            log.info("Eseguo correzione per ID_RICH_SOFT_DELETE: {}", idRichSoftDelete);

            // --- PASSO 1: UPDATE su ARO_ITEM_RICH_SOFT_DELETE ---
            log.info("Passo 1: Resetto gli item in errore a 'DA_ELABORARE'.");
            String updateItemsSql = "UPDATE ARO_ITEM_RICH_SOFT_DELETE " + "SET "
                    + "  DT_CLAIM = NULL, " + "  DT_FINE_ELAB = NULL, "
                    + "  CD_INSTANCE_ID = NULL, " + "  CD_ERR_MSG = NULL, "
                    + "  TI_STATO_ITEM = 'DA_ELABORARE' "
                    + "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete "
                    + "  AND TI_STATO_ITEM = 'ERRORE_ELABORAZIONE' ";

            int updatedItems = getEntityManager().createNativeQuery(updateItemsSql)
                    .setParameter("idRichSoftDelete", idRichSoftDelete).executeUpdate();
            log.info("Resettati {} item.", updatedItems);

            // --- PASSO 2: INSERT su ARO_STATO_RICH_SOFT_DELETE ---
            log.info("Passo 2: Inserisco un nuovo stato 'ACQUISITA'.");

            // Prima troviamo il PG_STATO_RICH_SOFT_DELETE massimo per incrementarlo
            String maxPgQueryStr = "SELECT MAX(PG_STATO_RICH_SOFT_DELETE) FROM ARO_STATO_RICH_SOFT_DELETE WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete";
            BigDecimal maxPg = (BigDecimal) getEntityManager().createNativeQuery(maxPgQueryStr)
                    .setParameter("idRichSoftDelete", idRichSoftDelete).getSingleResult();

            BigDecimal nuovoPg = (maxPg == null) ? BigDecimal.ONE : maxPg.add(BigDecimal.ONE);

            String insertStatoSql = "INSERT INTO ARO_STATO_RICH_SOFT_DELETE ("
                    + "  ID_STATO_RICH_SOFT_DELETE, ID_RICH_SOFT_DELETE, PG_STATO_RICH_SOFT_DELETE, "
                    + "  DT_REG_STATO_RICH_SOFT_DELETE, TI_STATO_RICH_SOFT_DELETE, ID_USER_IAM) "
                    + "VALUES (SARO_STATO_RICH_SOFT_DELETE.nextval, :idRichSoftDelete, :nuovoPg, SYSDATE, 'ACQUISITA', :idUserIam)";

            // Eseguo l'insert e poi recupero l'ID.
            getEntityManager().createNativeQuery(insertStatoSql)
                    .setParameter("idRichSoftDelete", idRichSoftDelete)
                    .setParameter("idUserIam", idUserIam).setParameter("nuovoPg", nuovoPg)
                    .executeUpdate();

            // Recuperiamo l'ID dello stato appena inserito
            String newIdStatoQueryStr = "SELECT ID_STATO_RICH_SOFT_DELETE FROM ARO_STATO_RICH_SOFT_DELETE "
                    + "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete AND PG_STATO_RICH_SOFT_DELETE = :nuovoPg";
            BigDecimal nuovoIdStato = (BigDecimal) getEntityManager()
                    .createNativeQuery(newIdStatoQueryStr)
                    .setParameter("idRichSoftDelete", idRichSoftDelete)
                    .setParameter("nuovoPg", nuovoPg).getSingleResult();
            log.info("Creato nuovo stato con ID: {}", nuovoIdStato);

            // --- PASSO 3: UPDATE su ARO_RICH_SOFT_DELETE ---
            log.info("Passo 3: Allineo la richiesta master con il nuovo stato.");
            String updateRichiestaSql = "UPDATE ARO_RICH_SOFT_DELETE "
                    + "SET ID_STATO_RICH_SOFT_DELETE_COR = :nuovoIdStato "
                    + "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete";

            getEntityManager().createNativeQuery(updateRichiestaSql)
                    .setParameter("nuovoIdStato", nuovoIdStato)
                    .setParameter("idRichSoftDelete", idRichSoftDelete).executeUpdate();
        }

        log.info("Correzione completata per la richiesta {}", idRichiesta);
    }

    /**
     * Verifica se per una data richiesta esistono ancora Unità Documentarie nello stato
     * 'DA_CANCELLARE' utilizzando la tabella DM_UD_DEL.
     *
     * @param idUdDelRichiesta L'ID della richiesta.
     * @return true se NON esistono UD da cancellare, false altrimenti.
     */
    public boolean isLavoroKafkaCompletato(BigDecimal idUdDelRichiesta) {
        String sql = "SELECT CASE WHEN EXISTS (" + "  SELECT 1 FROM SACER.DM_UD_DEL "
                + "  WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta AND TI_STATO_UD_CANCELLATE = 'DA_CANCELLARE'"
                + ") THEN 0 ELSE 1 END FROM DUAL";

        BigDecimal result = (BigDecimal) getEntityManager().createNativeQuery(sql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta).getSingleResult();

        // Se il risultato è 1, significa che non esistono ud DA_CANCELLARE e dunque il lavoro è
        // completato.
        return result.intValue() == 1;
    }

    /**
     * Reimposta lo stato di tutte le UD della richiesta a {@code DA_CANCELLARE}, consentendo la
     * ripresa di un processo di cancellazione interrotto.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     */
    public void updateDmUdDelDaCancellare(BigDecimal idUdDelRichiesta) {
        log.info("Resetto lo stato delle UD in 'DA_CANCELLARE'.");
        String updateUdsSql = "UPDATE DM_UD_DEL " + "SET TI_STATO_UD_CANCELLATE = 'DA_CANCELLARE' "
                + "WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta ";
        int updatedUds = getEntityManager().createNativeQuery(updateUdsSql)
                .setParameter("idUdDelRichiesta", idUdDelRichiesta).executeUpdate();
        log.info("Resettati {} item.", updatedUds);
    }

    // MEV #39896
    /**
     * MEV 39896 - Esegue la "foto" (snapshot) della richiesta di restituzione archivio copiando i
     * dati dalla vista ARO_V_RIC_RICH_RA alla tabella ARO_RIC_RICH_RA_FOTO.
     *
     * @param idRichiestaRa Identificativo della richiesta
     */
    public void insertAroRichRichRaFoto(BigDecimal idRichiestaRa) {
        StringBuilder sql = new StringBuilder();
        // Nota: Ometto la colonna ID_RIC_RICH_RA_FOTO perché essendo una Identity (ISEQ$$...)
        // verrà popolata automaticamente dal database.
        sql.append("INSERT INTO ARO_RIC_RICH_RA_FOTO (");
        sql.append("   ID_ENTE_CONVENZ, NM_ENTE_CONVENZ, NM_ENTE_STRUT, ID_RICHIESTA_RA, ");
        sql.append("   ID_AMBIENTE, ID_ENTE, ID_STRUT, TOTALI, ESTRATTI, ERRORI, ");
        sql.append(
                "   SUM_DIM, MAX_DT_ESTRAZIONE, TI_STATO, ESTRATTI_TOTALI, CD_ERRORE, TS_INIZIO");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("   v.ID_ENTE_CONVENZ, v.NM_ENTE_CONVENZ, v.NM_ENTE_STRUT, v.ID_RICHIESTA_RA, ");
        sql.append("   v.ID_AMBIENTE, v.ID_ENTE, v.ID_STRUT, v.TOTALI, v.ESTRATTI, v.ERRORI, ");
        sql.append(
                "   v.SUM_DIM, v.MAX_DT_ESTRAZIONE, v.TI_STATO, v.ESTRATTI_TOTALI, v.CD_ERRORE, v.TS_INIZIO ");
        sql.append("FROM ARO_V_RIC_RICH_RA v ");
        sql.append("WHERE v.ID_RICHIESTA_RA = :idRichiestaRa");

        Query query = getEntityManager().createNativeQuery(sql.toString());
        query.setParameter("idRichiestaRa", idRichiestaRa);

        int rows = query.executeUpdate();
        log.info("Inserita foto ARO_RIC_RICH_RA_FOTO per idRichiestaRa {}: {} righe copiate.",
                idRichiestaRa, rows);
    }

    /**
     * MEV 39896 - Esegue la "foto" (snapshot) degli item della richiesta di restituzione archivio
     * copiando i dati dalla vista ARO_V_LIS_ITEM_RA alla tabella ARO_LIS_ITEM_RA_FOTO.
     *
     * @param idRichiestaRa Identificativo della richiesta
     */
    public void insertAroLisItemRaFoto(BigDecimal idRichiestaRa) {
        StringBuilder sql = new StringBuilder();
        // Nota: Ometto la colonna ID_LIS_ITEM_RA_FOTO perché gestita automaticamente (Identity)
        sql.append("INSERT INTO ARO_LIS_ITEM_RA_FOTO (");
        sql.append("   ID_RICHIESTA_RA, ID_STRUT, ANNO, TOT_UD, NUM_AIP, ");
        sql.append("   DIMENSIONE, NUM_DOCS, NUM_ERRORI, NUM_ESTRATTI, AVANZAMENTO");
        sql.append(") ");
        sql.append("SELECT ");
        sql.append("   v.ID_RICHIESTA_RA, v.ID_STRUT, v.ANNO, v.TOT_UD, v.NUM_AIP, ");
        sql.append("   v.DIMENSIONE, v.NUM_DOCS, v.NUM_ERRORI, v.NUM_ESTRATTI, v.AVANZAMENTO ");
        sql.append("FROM ARO_V_LIS_ITEM_RA v ");
        sql.append("WHERE v.ID_RICHIESTA_RA = :idRichiestaRa");

        Query query = getEntityManager().createNativeQuery(sql.toString());
        query.setParameter("idRichiestaRa", idRichiestaRa);

        int rows = query.executeUpdate();
        log.info("Inserita foto ARO_LIS_ITEM_RA_FOTO per idRichiestaRa {}: {} righe copiate.",
                idRichiestaRa, rows);
    }

    // end MEV #39896

    // MEV #30416
    /**
     * Recupera l'elenco distinto di ID_STRUT associati a una richiesta nel datamart.
     *
     * @param idUdDelRichiesta id richiesta datamart
     * @return l'elenco di strutture associate alla richiesta
     */
    public List<BigDecimal> getIdStrutCoinvolti(BigDecimal idUdDelRichiesta) {
        String sql = "SELECT DISTINCT ID_STRUT FROM DM_UD_DEL WHERE ID_UD_DEL_RICHIESTA = :id";
        return getEntityManager().createNativeQuery(sql).setParameter("id", idUdDelRichiesta)
                .getResultList();
    }

    /**
     * Cancella un blocco (batch) di record da VRS_SESSIONE_VERS_KO.
     *
     * @param idStrutList elenco delle strutture per le quali cancellare le sessioni di versamento
     * @param batchSize   numero di riga da cancellare
     * @return numero di righe cancellate
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteVrsSessioneVersKoBatch(List<BigDecimal> idStrutList, int batchSize) {
        // Usiamo ROWNUM per limitare la transazione (Oracle)
        String sql = "DELETE FROM VRS_SESSIONE_VERS_KO WHERE ID_STRUT IN (:idStruts) AND ROWNUM <= :batchSize";
        return getEntityManager().createNativeQuery(sql).setParameter("idStruts", idStrutList)
                .setParameter("batchSize", batchSize).executeUpdate();
    }

    /**
     * Cancella un blocco (batch) di record da VRS_SESSIONE_VERS_KO_ELIMINATE.
     *
     * @param idStrutList elenco delle strutture per le quali cancellare le sessioni di versamento
     * @param batchSize   numero di riga da cancellare
     * @return numero di righe cancellate
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteVrsSessioneVersKoEliminateBatch(List<BigDecimal> idStrutList, int batchSize) {
        String sql = "DELETE FROM VRS_SESSIONE_VERS_KO_ELIMINATE WHERE ID_STRUT IN (:idStruts) AND ROWNUM <= :batchSize";
        return getEntityManager().createNativeQuery(sql).setParameter("idStruts", idStrutList)
                .setParameter("batchSize", batchSize).executeUpdate();
    }

    /**
     * Per il Watchdog: recupera le richieste bloccate in cancellazione fisica.
     *
     * @return la lista di richieste attive
     */
    public List<BigDecimal> getRichiesteFisicheAttive() {
        String queryStr = "SELECT DISTINCT r.idUdDelRichiesta FROM DmUdDelRichieste r "
                + "WHERE r.statoInternoRichCor IS NOT NULL "
                + "AND r.statoInternoRichCor.decodStatoInterno.tiStatoInternoRich IN ('IN_CODA_CANCELLAZIONE', 'IN_CANCELLAZIONE_FISICA')";
        List<Long> ids = getEntityManager().createQuery(queryStr, Long.class).getResultList();
        return ids.stream().map(BigDecimal::valueOf).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Cancella un blocco (batch) di record da VRS_DOC_NON_VERS.
     *
     * @param idStrutList lista delle strutture coinvolte
     * @param batchSize   numero di record da trattare
     * @return numero di record cancellati
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteVrsDocNonVersBatch(List<BigDecimal> idStrutList, int batchSize) {
        String sql = "DELETE FROM VRS_DOC_NON_VERS WHERE ID_STRUT IN (:idStruts) AND ROWNUM <= :batchSize";
        return getEntityManager().createNativeQuery(sql).setParameter("idStruts", idStrutList)
                .setParameter("batchSize", batchSize).executeUpdate();
    }

    /**
     * Cancella un blocco (batch) di record da VRS_UNITA_DOC_NON_VERS.
     *
     * @param idStrutList lista delle strutture coinvolte
     * @param batchSize   numero di record da trattare
     * @return numero di record cancellati
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteVrsUnitaDocNonVersBatch(List<BigDecimal> idStrutList, int batchSize) {
        String sql = "DELETE FROM VRS_UNITA_DOC_NON_VERS WHERE ID_STRUT IN (:idStruts) AND ROWNUM <= :batchSize";
        return getEntityManager().createNativeQuery(sql).setParameter("idStruts", idStrutList)
                .setParameter("batchSize", batchSize).executeUpdate();
    }

    // MEV 37227

    /**
     * Verifica che tutte le UD nella richiesta abbiano documenti (versamento e aggiunte) con
     * DT_CREAZIONE già elaborata dal JOB CalcoloContenutoSACER, cioè con data che sia minore o
     * uguale a MAX(DT_RIF_CONTA) in MON_CONTA_UD_DOC_COMP. Se MON_CONTA è vuota (JOB mai eseguito),
     * tutte le UD risultano non contate. Ogni elemento del risultato è Object[]:
     * [CD_REGISTRO_KEY_UNITA_DOC, AA_KEY_UNITA_DOC, CD_KEY_UNITA_DOC, ultima_doc_dt, ultimo_job_dt]
     *
     * @param idUdDelRichiesta id della richiesta
     * @return lista ud
     */
    public List<Object[]> getUdConDocNonAncoraConteggiati(BigDecimal idUdDelRichiesta) {
        // Controlla sia la data di versamento (DT_CREAZIONE documenti) sia la data di
        // annullamento (DT_REG_STATO_RICH_ANNUL_VERS con stato EVASA) rispetto all'ultima
        // data elaborata dal JOB CalcoloContenutoSACER.
        // Se una delle due è successiva al MAX(DT_RIF_CONTA), la cancellazione fisica
        // non può avvenire: il JOB non troverebbe più l'UD in ARO e perderebbe il conteggio.
        String sql = "SELECT ud.CD_REGISTRO_KEY_UNITA_DOC, " + "       ud.AA_KEY_UNITA_DOC, "
                + "       ud.CD_KEY_UNITA_DOC, " + "       GREATEST( "
                + "           TRUNC(MAX(doc.DT_CREAZIONE)), "
                + "           COALESCE(TRUNC(MAX(stato.DT_REG_STATO_RICH_ANNUL_VERS)), "
                + "                    TRUNC(MAX(doc.DT_CREAZIONE))) "
                + "       )                                                            AS ultima_data_rilevante, "
                + "       (SELECT MAX(m.DT_RIF_CONTA) FROM MON_CONTA_UD_DOC_COMP m)   AS ultimo_job_dt "
                + "FROM DM_UD_DEL dm "
                + "JOIN ARO_UNITA_DOC ud ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "JOIN ARO_DOC       doc ON doc.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "LEFT JOIN ARO_ITEM_RICH_ANNUL_VERS item "
                + "       ON item.ID_UNITA_DOC            = ud.ID_UNITA_DOC "
                + "      AND item.TI_ITEM_RICH_ANNUL_VERS  = 'UNI_DOC' "
                + "      AND item.TI_STATO_ITEM            = 'ANNULLATO' "
                + "LEFT JOIN ARO_RICH_ANNUL_VERS rich "
                + "       ON rich.ID_RICH_ANNUL_VERS = item.ID_RICH_ANNUL_VERS "
                + "LEFT JOIN ARO_STATO_RICH_ANNUL_VERS stato "
                + "       ON stato.ID_STATO_RICH_ANNUL_VERS = rich.ID_STATO_RICH_ANNUL_VERS_COR "
                + "      AND stato.TI_STATO_RICH_ANNUL_VERS = 'EVASA' "
                + "WHERE dm.ID_UD_DEL_RICHIESTA    = ? "
                + "  AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' " + "  AND ( "
                + "        (SELECT MAX(m.DT_RIF_CONTA) FROM MON_CONTA_UD_DOC_COMP m) IS NULL "
                + "        OR TRUNC(doc.DT_CREAZIONE) > "
                + "           (SELECT MAX(m.DT_RIF_CONTA) FROM MON_CONTA_UD_DOC_COMP m) "
                + "        OR TRUNC(stato.DT_REG_STATO_RICH_ANNUL_VERS) > "
                + "           (SELECT MAX(m.DT_RIF_CONTA) FROM MON_CONTA_UD_DOC_COMP m) "
                + "      ) "
                + "GROUP BY ud.CD_REGISTRO_KEY_UNITA_DOC, ud.AA_KEY_UNITA_DOC, ud.CD_KEY_UNITA_DOC "
                + "ORDER BY ultima_data_rilevante DESC, ud.CD_REGISTRO_KEY_UNITA_DOC";

        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter(1, idUdDelRichiesta);
        return q.getResultList();
    }

    /**
     * Crea uno snapshot dei delta contabili (versamenti, aggiunte documenti, annullamenti) nella
     * tabella {@code DM_UD_DEL_CONTA} per le UD con stato {@code CANCELLABILE} della richiesta
     * indicata. L'operazione è idempotente: elimina eventuali snapshot residui prima di reinserire.
     * <p>
     * I dati vengono aggregati in 8 blocchi UNION ALL che coprono: versamento UD, documenti
     * versati, componenti versati, aggiunta documenti, aggiunta componenti, UD annullate, documenti
     * annullati e componenti annullati.
     *
     * @param idUdDelRichiesta la PK della richiesta (ID_UD_DEL_RICHIESTA)
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void popolaSnapshotConteggiSacer(BigDecimal idUdDelRichiesta) {

        // Prima di tutto pulisco eventuali snapshot residui per queste UD (idempotenza)
        String sqlDelete = "DELETE FROM DM_UD_DEL_CONTA WHERE ID_UNITA_DOC IN "
                + "(SELECT ID_UNITA_DOC FROM DM_UD_DEL WHERE ID_UD_DEL_RICHIESTA = ?)";
        getEntityManager().createNativeQuery(sqlDelete).setParameter(1, idUdDelRichiesta)
                .executeUpdate();

        String nativeSql = "INSERT INTO DM_UD_DEL_CONTA ( "
                + "  ID_UNITA_DOC, DT_RIF_CONTA, ID_STRUT, ID_SUB_STRUT, AA_KEY_UNITA_DOC, "
                + "  ID_REGISTRO_UNITA_DOC, ID_TIPO_UNITA_DOC, ID_TIPO_DOC_PRINC, "
                + "  NI_UNITA_DOC_VERS, NI_DOC_VERS, NI_COMP_VERS, NI_SIZE_VERS, "
                + "  NI_DOC_AGG, NI_COMP_AGG, NI_SIZE_AGG, "
                + "  NI_UNITA_DOC_ANNUL, NI_DOC_ANNUL_UD, NI_COMP_ANNUL_UD, NI_SIZE_ANNUL_UD "
                + ") " + "SELECT t.* FROM ( " +

                // --- 1. VERSAMENTO UD ---
                "  SELECT ud.ID_UNITA_DOC as ID_UNITA_DOC, TRUNC(docPrinc.DT_CREAZIONE) as DT_RIF_CONTA, ud.ID_STRUT as ID_STRUT, "
                + "  ud.ID_SUB_STRUT as ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC as AA_KEY_UNITA_DOC, ud.ID_REGISTRO_UNITA_DOC as ID_REGISTRO_UNITA_DOC, "
                + "  ud.ID_TIPO_UNITA_DOC as ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC as ID_TIPO_DOC_PRINC, "
                + "  1 as NI_UNITA_DOC_VERS, 0 as NI_DOC_VERS, 0 as NI_COMP_VERS, 0 as NI_SIZE_VERS, "
                + "  0 as NI_DOC_AGG, 0 as NI_COMP_AGG, 0 as NI_SIZE_AGG, "
                + "  0 as NI_UNITA_DOC_ANNUL, 0 as NI_DOC_ANNUL_UD, 0 as NI_COMP_ANNUL_UD, 0 as NI_SIZE_ANNUL_UD "
                + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND docPrinc.TI_CREAZIONE = 'VERSAMENTO_UNITA_DOC' " + "  UNION ALL " +

                // --- 2. DOCUMENTI VERSATI ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 " + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND doc.TI_CREAZIONE = 'VERSAMENTO_UNITA_DOC' " + "  UNION ALL " +

                // --- 3. COMPONENTI VERSATI E SIZE ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 1, NVL(comp.NI_SIZE_FILE_CALC, 0), 0, 0, 0, 0, 0, 0, 0 "
                + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN ARO_STRUT_DOC strut ON doc.ID_DOC = strut.ID_DOC "
                + "  JOIN ARO_COMP_DOC comp ON strut.ID_STRUT_DOC = comp.ID_STRUT_DOC "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND doc.TI_CREAZIONE = 'VERSAMENTO_UNITA_DOC' " + "  UNION ALL " +

                // --- 4. AGGIUNTA DOCUMENTI ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 " + // <--- 1 in NI_DOC_AGG
                "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND doc.TI_CREAZIONE = 'AGGIUNTA_DOCUMENTO' " + "  UNION ALL " +

                // --- 5. AGGIUNTA COMPONENTI E SIZE (IL BLOCCO CHE MANCAVA!) ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 0, 0, 0, 1, NVL(comp.NI_SIZE_FILE_CALC, 0), 0, 0, 0, 0 " + // <--- 1 in
                                                                                      // NI_COMP_AGG
                                                                                      // e
                                                                                      // Size
                "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN ARO_STRUT_DOC strut ON doc.ID_DOC = strut.ID_DOC "
                + "  JOIN ARO_COMP_DOC comp ON strut.ID_STRUT_DOC = comp.ID_STRUT_DOC "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND doc.TI_CREAZIONE = 'AGGIUNTA_DOCUMENTO' " + "  UNION ALL " +

                // --- 6. UD ANNULLATE ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(docPrinc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 " + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_ITEM_RICH_ANNUL_VERS item ON ud.ID_UNITA_DOC = item.ID_UNITA_DOC "
                + "  JOIN ARO_RICH_ANNUL_VERS rich ON item.ID_RICH_ANNUL_VERS = rich.ID_RICH_ANNUL_VERS "
                + "  JOIN ARO_STATO_RICH_ANNUL_VERS statoAnnul ON rich.ID_STATO_RICH_ANNUL_VERS_COR = statoAnnul.ID_STATO_RICH_ANNUL_VERS "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND statoAnnul.TI_STATO_RICH_ANNUL_VERS = 'EVASA' AND item.TI_STATO_ITEM = 'ANNULLATO' "
                + "  UNION ALL " +

                // --- 7. DOCUMENTI ANNULLATI ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 " + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN ARO_ITEM_RICH_ANNUL_VERS item ON ud.ID_UNITA_DOC = item.ID_UNITA_DOC "
                + "  JOIN ARO_RICH_ANNUL_VERS rich ON item.ID_RICH_ANNUL_VERS = rich.ID_RICH_ANNUL_VERS "
                + "  JOIN ARO_STATO_RICH_ANNUL_VERS statoAnnul ON rich.ID_STATO_RICH_ANNUL_VERS_COR = statoAnnul.ID_STATO_RICH_ANNUL_VERS "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND statoAnnul.TI_STATO_RICH_ANNUL_VERS = 'EVASA' AND item.TI_STATO_ITEM = 'ANNULLATO' "
                + "  UNION ALL " +

                // --- 8. COMPONENTI ANNULLATI E SIZE ---
                "  SELECT ud.ID_UNITA_DOC, TRUNC(doc.DT_CREAZIONE), ud.ID_STRUT, ud.ID_SUB_STRUT, ud.AA_KEY_UNITA_DOC, "
                + "  ud.ID_REGISTRO_UNITA_DOC, ud.ID_TIPO_UNITA_DOC, docPrinc.ID_TIPO_DOC, "
                + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 1, NVL(comp.NI_SIZE_FILE_CALC, 0) "
                + "  FROM ARO_UNITA_DOC ud "
                + "  JOIN ARO_DOC docPrinc ON ud.ID_UNITA_DOC = docPrinc.ID_UNITA_DOC AND docPrinc.TI_DOC = 'PRINCIPALE' "
                + "  JOIN ARO_DOC doc ON ud.ID_UNITA_DOC = doc.ID_UNITA_DOC "
                + "  JOIN ARO_STRUT_DOC strut ON doc.ID_DOC = strut.ID_DOC "
                + "  JOIN ARO_COMP_DOC comp ON strut.ID_STRUT_DOC = comp.ID_STRUT_DOC "
                + "  JOIN ARO_ITEM_RICH_ANNUL_VERS item ON ud.ID_UNITA_DOC = item.ID_UNITA_DOC "
                + "  JOIN ARO_RICH_ANNUL_VERS rich ON item.ID_RICH_ANNUL_VERS = rich.ID_RICH_ANNUL_VERS "
                + "  JOIN ARO_STATO_RICH_ANNUL_VERS statoAnnul ON rich.ID_STATO_RICH_ANNUL_VERS_COR = statoAnnul.ID_STATO_RICH_ANNUL_VERS "
                + "  JOIN DM_UD_DEL dm ON dm.ID_UNITA_DOC = ud.ID_UNITA_DOC "
                + "  WHERE dm.ID_UD_DEL_RICHIESTA = ? AND dm.TI_STATO_UD_CANCELLATE = 'CANCELLABILE' "
                + "  AND statoAnnul.TI_STATO_RICH_ANNUL_VERS = 'EVASA' AND item.TI_STATO_ITEM = 'ANNULLATO' "
                + ") t";

        Query q = getEntityManager().createNativeQuery(nativeSql);
        for (int i = 1; i <= 8; i++) {
            q.setParameter(i, idUdDelRichiesta);
        }

        int insertedRecords = q.executeUpdate();
        log.info(
                "Salvato snapshot di {} delta contabili (inclusi annullamenti) per la richiesta {}",
                insertedRecords, idUdDelRichiesta);
    }

    /**
     * Recupera lo storico completo dei passaggi di stato interno per una richiesta, ordinato per
     * progressivo decrescente (dal più recente al più vecchio).
     *
     * @param idUdDelRichiesta la PK della richiesta
     *
     * @return la lista degli stati, ordinata per PG_STATO_RICH discendente
     */
    public List<DmUdDelStatoRichiesta> getStoricoStatiRichiesta(BigDecimal idUdDelRichiesta) {
        String queryStr = "SELECT s FROM DmUdDelStatoRichiesta s "
                + "WHERE s.dmUdDelRichieste.idUdDelRichiesta = :idUdDelRichiesta "
                + "ORDER BY s.pgStatoRich DESC";
        TypedQuery<DmUdDelStatoRichiesta> q = getEntityManager().createQuery(queryStr,
                DmUdDelStatoRichiesta.class);
        q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());
        return q.getResultList();
    }

    /**
     * Restituisce lo storico stati con la descrizione decodificata. Ogni Object[] contiene: [0]
     * DmUdDelStatoRichiesta, [1] DS_STATO_INTERNO_RICH (String, nullable)
     *
     * @param idUdDelRichiesta la PK della richiesta
     *
     * @return lista di oggetti
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getStoricoStatiRichiestaConDescrizione(BigDecimal idUdDelRichiesta) {
        String sql = "SELECT s.ID_STATO_UD_DEL_RICHIESTA, s.ID_UD_DEL_RICHIESTA, "
                + "d.TI_STATO_INTERNO_RICH, s.DT_REG_STATO, s.PG_STATO_RICH, "
                + "NVL(d.DS_STATO_INTERNO_RICH, d.TI_STATO_INTERNO_RICH) AS DS_STATO_INTERNO_RICH "
                + "FROM DM_UD_DEL_STATO_RICHIESTA s "
                + "JOIN DM_UD_DEL_DECOD_STATO_INTERNO d ON d.ID_DECOD_STATO_INTERNO = s.ID_DECOD_STATO_INTERNO "
                + "WHERE s.ID_UD_DEL_RICHIESTA = :id " + "ORDER BY s.PG_STATO_RICH DESC";
        return getEntityManager().createNativeQuery(sql)
                .setParameter("id", idUdDelRichiesta.longValue()).getResultList();
    }
}