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
import it.eng.parer.entity.DmUdDelObjectStorage;
import it.eng.parer.entity.DmUdDelRecRefTab;
import it.eng.parer.entity.DmUdDelRichieste;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OrgEnte;
import it.eng.parer.helper.GenericHelper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    public List<OrgEnte> getOrgEnteDataMartList() {
	String queryStr = "SELECT ente.* FROM Org_Ente ente "
		+ "WHERE ente.id_Ente IN (SELECT distinct id_ente FROM DM_UD_DEL group by id_ente) "
		+ "ORDER BY ente.nm_ente";
	Query q = getEntityManager().createNativeQuery(queryStr, OrgEnte.class);
	return (List<OrgEnte>) q.getResultList();
    }

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

    public List<RichiestaDataMartDTO> getRichiesteDataMartDtoList(String tiMotCancellazione,
	    String tiStatoRichiesta, BigDecimal idEnte, BigDecimal idStrut,
	    String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc,
	    Date dtCreazioneDa, Date dtCreazioneA) {

	// La SELECT ora usa il costruttore del DTO. L'ordine dei campi DEVE corrispondere.
	StringBuilder queryStr = new StringBuilder(
		"SELECT NEW it.eng.parer.datamart.dto.RichiestaDataMartDTO( "
			+ "dmRich.idUdDelRichiesta, " + "dmRich.idRichiesta, "
			+ "dmRich.cdRichiesta, " + "dmRich.tiMotCancellazione, "
			+ "CASE dmRich.tiMotCancellazione WHEN 'R' THEN 'Restituzione archivio' WHEN 'S' THEN 'Scarto' WHEN 'A' THEN 'Annullamento ud' ELSE 'Non definita' END, "
			+ "dmRich.dtCreazione, " + "dmRich.tiStatoRichiesta, " + "COUNT(dmUd) "
			+ ") " + "FROM DmUdDelRichieste dmRich JOIN dmRich.dmUdDels dmUd ");

	String whereWord = " WHERE ";

	// I filtri sono ora applicati agli alias corretti (dmRich o dmUd)
	if (tiMotCancellazione != null) {
	    queryStr.append(whereWord).append("dmRich.tiMotCancellazione = :tiMotCancellazione ");
	    whereWord = " AND ";
	}
	if (tiStatoRichiesta != null) {
	    queryStr.append(whereWord).append("dmRich.tiStatoRichiesta = :tiStatoRichiesta ");
	    whereWord = " AND ";
	}
	// Assumendo che questi campi siano nell'entità DmUdDel
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
	    // Il filtro sulla data si applica a dmRich (la richiesta)
	    queryStr.append(whereWord).append(
		    "dmRich.dtCreazione >= :dtCreazioneDa AND dmRich.dtCreazione <= :dtCreazioneA ");
	    whereWord = " AND ";
	}

	// Il GROUP BY si semplifica e usa i campi delle entità
	queryStr.append(
		"GROUP BY dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, dmRich.tiMotCancellazione, dmRich.dtCreazione, dmRich.tiStatoRichiesta ");

	// Usa TypedQuery per ottenere una lista del tipo corretto senza cast manuali
	TypedQuery<RichiestaDataMartDTO> q = getEntityManager().createQuery(queryStr.toString(),
		RichiestaDataMartDTO.class);

	// Impostazione dei parametri (identica a prima)
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

    public RichiestaDataMartDTO getRichiestaDataMart(BigDecimal idUdDelRichiesta) {

	// La SELECT ora usa il costruttore del DTO. L'ordine dei campi DEVE corrispondere.
	String queryStr = "SELECT NEW it.eng.parer.datamart.dto.RichiestaDataMartDTO( "
		+ "dmRich.idUdDelRichiesta, " + "dmRich.idRichiesta, " + "dmRich.cdRichiesta, "
		+ "dmRich.tiMotCancellazione, "
		+ "CASE dmRich.tiMotCancellazione WHEN 'R' THEN 'Restituzione archivio' WHEN 'S' THEN 'Scarto' WHEN 'A' THEN 'Annullamento ud' ELSE 'Non definita' END, "
		+ "dmRich.dtCreazione, " + "dmRich.tiStatoRichiesta, " + "COUNT(dmUd) " + ") "
		+ "FROM DmUdDelRichieste dmRich JOIN dmRich.dmUdDels dmUd WHERE dmRich.idUdDelRichiesta = :idUdDelRichiesta "
		+ "GROUP BY dmRich.idUdDelRichiesta, dmRich.idRichiesta, dmRich.cdRichiesta, dmRich.tiMotCancellazione, dmRich.dtCreazione, dmRich.tiStatoRichiesta ";

	// Usa TypedQuery per ottenere una lista del tipo corretto senza cast manuali
	TypedQuery<RichiestaDataMartDTO> q = getEntityManager().createQuery(queryStr,
		RichiestaDataMartDTO.class);

	q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());
	List<RichiestaDataMartDTO> lista = q.getResultList();
	if (lista.size() == 1) {
	    return lista.get(0);
	}
	return null;

    }

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

    public Query getUdDataMartByStatoUdQuery(BigDecimal idRichiesta, String tiStatoUdCancellate) {
	String queryStr = "SELECT dm FROM DmUdDel dm WHERE dm.idRichiesta = :idRichiesta AND dm.tiStatoUdCancellate = :tiStatoUdCancellate ORDER BY dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc ";
	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idRichiesta", idRichiesta);
	q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);
	return q;
    }

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

    public Query getDmUdDelAnnulVersQuery(BigDecimal idRichiesta, String tiStatoUdCancellate) {
	String queryStr = "SELECT dm FROM DmUdDel dm JOIN dm.dmUdDelRichieste dmRich "
		+ "WHERE dmRich.idRichiesta = :idRichiesta AND dmRich.tiMotCancellazione = 'A' AND dm.tiStatoUdCancellate = :tiStatoUdCancellate "
		+ "ORDER BY dm.nmEnte, dm.nmStrut, dm.cdRegistroKeyUnitaDoc, dm.aaKeyUnitaDoc, dm.cdKeyUnitaDoc ";

	Query q = getEntityManager().createQuery(queryStr);
	q.setParameter("idRichiesta", idRichiesta);
	q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);

	return q;
    }

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
	if (tiStatoRichiesta != null) {
	    queryStr.append(whereWord).append("dm.tiStatoRichiesta = :tiStatoRichiesta ");
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
	if (idUdDelRichiesta != null) {
	    q.setParameter("idUdDelRichiesta", idUdDelRichiesta.longValue());
	}
	if (tiStatoUdCancellate != null) {
	    q.setParameter("tiStatoUdCancellate", tiStatoUdCancellate);
	}

	return q;
    }

    @Transactional
    public int populateDataMartUdCentroStella(long idRichiesta, String cdRichiesta,
	    String tiMotCancellazione, String tiModDel) {
	// --- PASSAGGIO 1: Creare e salvare la riga master in DM_UD_DEL_RICHIESTE ---

	DmUdDelRichieste nuovaRichiesta = new DmUdDelRichieste();
	nuovaRichiesta.setIdRichiesta(BigDecimal.valueOf(idRichiesta));
	nuovaRichiesta.setCdRichiesta(cdRichiesta);
	nuovaRichiesta.setTiMotCancellazione(tiMotCancellazione);
	nuovaRichiesta.setTiStatoRichiesta("DA_EVADERE"); // Stato utente iniziale
	nuovaRichiesta.setTiStatoInternoRich("INIZIALE"); // Stato tecnico iniziale
	nuovaRichiesta.setTiModDel(tiModDel); // Stato tecnico iniziale
	nuovaRichiesta.setDtCreazione(new Date()); // Imposta la data corrente

	// Persisti l'entità. Dopo questa chiamata, JPA/Hibernate si occuperà
	// di eseguire l'INSERT e di popolare il campo ID con il valore generato dal DB.
	getEntityManager().persist(nuovaRichiesta);

	Long idUdDelRichiesta = nuovaRichiesta.getIdUdDelRichiesta();

	// ID della richiesta corrente, da passare come parametro
	int numRecordDmUdDel = 0;

	// ======================================================================
	// PASSAGGIO 1: Popolamento massivo della tabella padre DM_UD_DEL per Annullamento
	// Versamenti
	// ======================================================================
	String insertParentSql = "INSERT /*+ APPEND */ INTO DM_UD_DEL ( "
		+ "    ID_UNITA_DOC, AA_KEY_UNITA_DOC, CD_KEY_UNITA_DOC, CD_REGISTRO_KEY_UNITA_DOC, DT_VERSAMENTO, ID_ENTE, NM_ENTE, ID_STRUT, NM_STRUT, ID_UD_DEL_RICHIESTA, TI_STATO_UD_CANCELLATE, DT_STATO_UD_CANCELLATE) "
		+ " SELECT DISTINCT "
		+ "    item_rich.ID_UNITA_DOC, item_rich.AA_KEY_UNITA_DOC, item_rich.CD_KEY_UNITA_DOC, item_rich.CD_REGISTRO_KEY_UNITA_DOC, ud.DT_CREAZIONE, ente.ID_ENTE, ente.NM_ENTE, strut.ID_STRUT, strut.NM_STRUT, :idUdDelRichiesta, 'DA_CANCELLARE', SYSDATE "
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
     * Esegue l'update dello stato delle singole UD a 'CANCELLABILE'. Deve essere chiamato
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
		+ "    dm.tiStatoUdCancellate, " + "    COUNT(dm.idUnitaDoc) " + ") " + "FROM "
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

    public void deleteAroRichSoftDelete(BigDecimal idRichiesta, String tiItemRichSoftDelete) {
	String queryStr = "DELETE FROM ARO_RICH_SOFT_DELETE WHERE ID_RICH_SOFT_DELETE = "
		+ "(SELECT item.id_rich_soft_delete FROM ARO_STATO_RICH_SOFT_DELETE stato JOIN ARO_ITEM_RICH_SOFT_DELETE item on (item.id_rich_soft_delete = stato.id_rich_soft_delete) "
		+ "WHERE stato.ti_stato_rich_soft_delete = 'ERRORE' "
		+ "AND item.id_richiesta_sacer = :idRichiesta "
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
     * Aggiorna lo stato della richiesta master nella tabella DM_UD_DEL_RICHIESTE. Viene eseguito in
     * una NUOVA transazione per garantire che il commit sia immediato, rendendo lo stato visibile
     * subito dopo la chiamata, indipendentemente dalla transazione del chiamante.
     *
     * @param idRichiesta L'ID della richiesta da aggiornare.
     * @param nuovoStato  Il nuovo stato da impostare (es. 'INVIATA_A_MS', 'LOGICA_COMPLETATA').
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void impostaStatoRichiesta(BigDecimal idRichiesta, String nuovoStato) {
	log.info("[TX-STATUS] Aggiornando stato per richiesta {} a '{}'", idRichiesta, nuovoStato);

	String sqlRichiesta = "UPDATE DM_UD_DEL_RICHIESTE SET TI_STATO_RICHIESTA = :stato, DT_ULTIMO_AGGIORNAMENTO = SYSDATE WHERE ID_RICHIESTA = :idRichiesta";

	int updatedRows = getEntityManager().createNativeQuery(sqlRichiesta)
		.setParameter("stato", nuovoStato).setParameter("idRichiesta", idRichiesta)
		.executeUpdate();

	if (updatedRows == 0) {
	    // Questo è un errore grave, significa che stiamo cercando di aggiornare una richiesta
	    // che non esiste.
	    throw new EJBException(
		    "Nessuna richiesta master trovata da aggiornare per l'ID: " + idRichiesta);
	}

	log.info("[TX-STATUS] Aggiornamento stato per richiesta {} completato.", idRichiesta);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta, String nuovoStatoInterno,
	    String messaggioErrore) {
	log.info("[TX-STATUS] Aggiornando stato INTERNO per richiesta {} a '{}'", idUdDelRichiesta,
		nuovoStatoInterno);
	String sqlBase = "UPDATE DM_UD_DEL_RICHIESTE SET TI_STATO_INTERNO_RICH = :stato, DT_ULTIMO_AGGIORNAMENTO = SYSDATE ";
	if (messaggioErrore != null) {
	    sqlBase += ", DS_MESSAGGIO_ERRORE = :msg ";
	}
	String sqlRichiesta = sqlBase + " WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta";

	Query q = getEntityManager().createNativeQuery(sqlRichiesta);
	q.setParameter("stato", nuovoStatoInterno);
	q.setParameter("idUdDelRichiesta", idUdDelRichiesta);
	if (messaggioErrore != null) {
	    q.setParameter("msg", messaggioErrore);
	}

	if (q.executeUpdate() == 0) {
	    throw new EJBException(
		    "Nessuna richiesta master trovata da aggiornare per l'ID: " + idUdDelRichiesta);
	}
    }

    public void impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta,
	    String nuovoStatoInterno) {
	impostaStatoInternoRichiesta(idUdDelRichiesta, nuovoStatoInterno, null);
    }

    public String getStatoRichiesta(BigDecimal idUdDelRichiesta) {
	try {
	    String sql = "SELECT TI_STATO_RICHIESTA FROM DM_UD_DEL_RICHIESTE WHERE ID_UD_DEL_RICHIESTA = :id";
	    return (String) getEntityManager().createNativeQuery(sql)
		    .setParameter("id", idUdDelRichiesta).getSingleResult();
	} catch (NoResultException e) {
	    return "DA_EVADERE";
	}
    }

    public String getStatoInternoRichiesta(BigDecimal idUdDelRichiesta) {
	try {
	    String sql = "SELECT TI_STATO_INTERNO_RICH FROM DM_UD_DEL_RICHIESTE WHERE ID_UD_DEL_RICHIESTA = :id";
	    return (String) getEntityManager().createNativeQuery(sql)
		    .setParameter("id", idUdDelRichiesta).getSingleResult();
	} catch (NoResultException e) {
	    return "NON_TROVATA";
	}
    }

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
	// Questa query è la misura più affidabile del progresso di Kafka.
	// Conta quante UD uniche della nostra richiesta hanno almeno
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

	// --- PRE-REQUISITO: Trovare l'ID_RICH_SOFT_DELETE ---
	// Se non hai una relazione mappata, usa una query nativa come questa:
	String idSoftDeleteNativeQueryStr = "SELECT ID_RICH_SOFT_DELETE FROM ARO_ITEM_RICH_SOFT_DELETE WHERE ID_RICHIESTA_SACER = :idRichiesta "
		+ "AND TI_ITEM_RICH_SOFT_DELETE = :tiItemRichSoftDelete ";

	BigDecimal idRichSoftDelete = (BigDecimal) getEntityManager()
		.createNativeQuery(idSoftDeleteNativeQueryStr)
		.setParameter("idRichiesta", idRichiesta)
		.setParameter("tiItemRichSoftDelete", tiItemRichSoftDelete).getSingleResult();

	if (idRichSoftDelete == null) {
	    throw new IllegalStateException(
		    "Impossibile trovare un ID_RICH_SOFT_DELETE associato alla richiesta Sacer: "
			    + idRichiesta);
	}

	log.info("Eseguo correzione per ID_RICH_SOFT_DELETE: {}", idRichSoftDelete);

	// --- PASSO 1: UPDATE su ARO_ITEM_RICH_SOFT_DELETE ---
	log.info("Passo 1: Resetto gli item in errore a 'DA_ELABORARE'.");
	String updateItemsSql = "UPDATE ARO_ITEM_RICH_SOFT_DELETE " + "SET " + "  DT_CLAIM = NULL, "
		+ "  DT_FINE_ELAB = NULL, " + "  CD_INSTANCE_ID = NULL, " + "  CD_ERR_MSG = NULL, "
		+ "  TI_STATO_ITEM = 'DA_ELABORARE' "
		+ "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete "
		+ "  AND TI_STATO_ITEM = 'ERRORE_ELABORAZIONE'";

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
	// s
	// Ora eseguiamo l'INSERT
	// Nota: ID_USER_IAM è hardcodato a 1, cambialo se necessario.
	String insertStatoSql = "INSERT INTO ARO_STATO_RICH_SOFT_DELETE ("
		+ "  ID_STATO_RICH_SOFT_DELETE, ID_RICH_SOFT_DELETE, PG_STATO_RICH_SOFT_DELETE, "
		+ "  DT_REG_STATO_RICH_SOFT_DELETE, TI_STATO_RICH_SOFT_DELETE, ID_USER_IAM) "
		+ "VALUES (SARO_STATO_RICH_SOFT_DELETE.nextval, :idRichSoftDelete, :nuovoPg, SYSDATE, 'ACQUISITA', :idUserIam)";

	// Per ottenere l'ID appena inserito, abbiamo bisogno di JDBC o di una logica più complessa.
	// Per ora, eseguiamo l'insert e poi recuperiamo l'ID.
	getEntityManager().createNativeQuery(insertStatoSql)
		.setParameter("idRichSoftDelete", idRichSoftDelete)
		.setParameter("idUserIam", idUserIam).setParameter("nuovoPg", nuovoPg)
		.executeUpdate();

	// Recuperiamo l'ID dello stato appena inserito
	String newIdStatoQueryStr = "SELECT ID_STATO_RICH_SOFT_DELETE FROM ARO_STATO_RICH_SOFT_DELETE "
		+ "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete AND PG_STATO_RICH_SOFT_DELETE = :nuovoPg";
	BigDecimal nuovoIdStato = (BigDecimal) getEntityManager()
		.createNativeQuery(newIdStatoQueryStr)
		.setParameter("idRichSoftDelete", idRichSoftDelete).setParameter("nuovoPg", nuovoPg)
		.getSingleResult();
	log.info("Creato nuovo stato con ID: {}", nuovoIdStato);

	// --- PASSO 3: UPDATE su ARO_RICH_SOFT_DELETE ---
	log.info("Passo 3: Allineo la richiesta master con il nuovo stato.");
	String updateRichiestaSql = "UPDATE ARO_RICH_SOFT_DELETE "
		+ "SET ID_STATO_RICH_SOFT_DELETE_COR = :nuovoIdStato "
		+ "WHERE ID_RICH_SOFT_DELETE = :idRichSoftDelete";

	getEntityManager().createNativeQuery(updateRichiestaSql)
		.setParameter("nuovoIdStato", nuovoIdStato)
		.setParameter("idRichSoftDelete", idRichSoftDelete).executeUpdate();

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
	// Usiamo una query nativa con un trucco per restituire 1 se esiste almeno un record,
	// e 0 altrimenti. Questo è un modo standard per mappare EXISTS a un risultato numerico.
	String sql = "SELECT CASE WHEN EXISTS (" + "  SELECT 1 FROM SACER.DM_UD_DEL "
		+ "  WHERE ID_UD_DEL_RICHIESTA = :idUdDelRichiesta AND TI_STATO_UD_CANCELLATE = 'DA_CANCELLARE'"
		+ ") THEN 0 ELSE 1 END FROM DUAL";

	BigDecimal result = (BigDecimal) getEntityManager().createNativeQuery(sql)
		.setParameter("idUdDelRichiesta", idUdDelRichiesta).getSingleResult();

	// Se il risultato è 1, significa che non esistono ud DA_CANCELLARE e dunque il lavoro è
	// completato.
	return result.intValue() == 1;
    }
}