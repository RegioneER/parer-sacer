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

package it.eng.parer.migrazioneObjectStorage.helper;

import static it.eng.parer.util.Utils.bigDecimalFromInteger;
import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.eng.parer.entity.OrgSubPartition;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.entity.OstMigrazStrutMese;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.OstVChkUsoTb;
import it.eng.parer.viewEntity.OstVLisStrutMmBlob;
import it.eng.parer.viewEntity.OstVLisSubpartBlobByIstz;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "VerificaMigrazioneSubPartizioneHelper")
@LocalBean
public class VerificaMigrazioneSubPartizioniHelper extends GenericHelper {

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public List<OstMigrazSubPart> getOstMigrazSubPartList(List<String> tiStato, String niFile) {
	String queryStr = "SELECT migrazSubPart FROM OstStatoMigrazSubPart statoMigrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart migrazSubPart "
		+ "WHERE statoMigrazSubPart.tiStato IN (:tiStato) "
		+ "AND statoMigrazSubPart.idStatoMigrazSubPart = migrazSubPart.idStatoMigrazSubPartCor ";
	if (niFile != null) {
	    queryStr += String.format("AND migrazSubPart.%s > 0 ", niFile);
	}
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", tiStato);
	return query.getResultList();
    }

    public List<OstMigrazSubPart> getOstMigrazSubPartListByStateOrdered(List<String> tiStato,
	    int numeroJob) {
	String queryStr = "SELECT migrazSubPart FROM OstStatoMigrazSubPart statoMigrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart migrazSubPart "
		+ "WHERE statoMigrazSubPart.tiStato IN (:tiStato) "
		+ "AND statoMigrazSubPart.idStatoMigrazSubPart = migrazSubPart.idStatoMigrazSubPartCor "
		+ "AND migrazSubPart.niIstanzaJobProducer = :niIstanzaJobProducer "
		+ "AND migrazSubPart.niFileDaMigrare > 0 "
		+ "ORDER BY migrazSubPart.mmMax, migrazSubPart.niFileDaMigrare DESC, migrazSubPart.niFileMigrazInCorso";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", tiStato);
	query.setParameter("niIstanzaJobProducer", bigDecimalFromInteger(numeroJob));
	return query.getResultList();
    }

    /*
     * Determina la lista delle migrazioni delle sub partizioni con stato corrente DA_ELIMINARE
     * ordinate su anno-mese massimo presente nella sub partizione (asc) + numero di file migrati
     * (desc).
     */
    public List<Object[]> getOstMigrazSubPartDaEliminareListOrdered() {
	String queryStr = "SELECT migrazSubPart, orgSubPartition FROM OstStatoMigrazSubPart statoMigrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart migrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart.orgSubPartition orgSubPartition "
		+ "WHERE statoMigrazSubPart.tiStato = :tiStato "
		+ "AND statoMigrazSubPart.idStatoMigrazSubPart = migrazSubPart.idStatoMigrazSubPartCor "
		+ "ORDER BY migrazSubPart.mmMax ASC, migrazSubPart.niFileMigrati DESC";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato",
		it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_ELIMINARE.name());
	return query.getResultList();
    }

    /**
     * Determina i file della sub partizione con stato tiStatoCor da un numero di giorni maggiore di
     * quanto definito dal parametro passato in input
     *
     * @param idMigrazSubPart    id migrazione sub partizione
     * @param tiStatoCor         tipo stato
     * @param numGgMigrazInCorso numero giorni migrazione in corso
     *
     * @return lista oggetti di tipo {@link OstMigrazFile}
     */
    public List<OstMigrazFile> getOstMigrazFileBeforeNumGiorni(long idMigrazSubPart,
	    String tiStatoCor, BigDecimal numGgMigrazInCorso) {
	// Ricavi la data odierna meno il numero di giorni passati in input
	Calendar c = Calendar.getInstance();
	c.set(Calendar.HOUR_OF_DAY, 23);
	c.set(Calendar.MINUTE, 59);
	c.set(Calendar.SECOND, 59);
	c.set(Calendar.MILLISECOND, 999);
	c.add(Calendar.DATE, -numGgMigrazInCorso.intValue());
	String queryStr = "SELECT migrazFile FROM OstMigrazFile migrazFile "
		+ "JOIN migrazFile.ostMigrazSubPart migrazSubPart "
		+ "WHERE migrazSubPart.idMigrazSubPart = :idMigrazSubPart "
		+ "AND migrazFile.tiStatoCor = :tiStatoCor " + "AND migrazFile.tsRegStatoCor < :c ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idMigrazSubPart", idMigrazSubPart);
	query.setParameter("tiStatoCor", tiStatoCor);
	query.setParameter("c", c.getTime());
	return query.getResultList();
    }

    public List<OstMigrazFile> getOstMigrazFileByStateAndSubPartitionWithLimit(String stato,
	    OstMigrazSubPart ostMigrazSubPart, long maxRecords) {
	String queryStr = "SELECT migrazFile FROM OstMigrazFile migrazFile "
		+ "WHERE migrazFile.tiStatoCor = :stato "
		+ "AND migrazFile.ostMigrazSubPart=:ostMigrazSubPart ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("stato", stato);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	query.setMaxResults((int) maxRecords);
	return query.getResultList();
    }

    public List<OstMigrazFile> getOstMigrazFilePerNumErrori(long idMigrazSubPart, String tiStatoCor,
	    BigDecimal numMaxErr) {
	String queryStr = "SELECT migrazFile FROM OstMigrazFile migrazFile "
		+ "JOIN migrazFile.ostMigrazSubPart migrazSubPart "
		+ "WHERE migrazSubPart.idMigrazSubPart = :idMigrazSubPart "
		+ "AND migrazFile.tiStatoCor = :tiStatoCor "
		+ "AND migrazFile.niMigrazErr < :numMaxErr ";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idMigrazSubPart", idMigrazSubPart);
	query.setParameter("tiStatoCor", tiStatoCor);
	query.setParameter("numMaxErr", numMaxErr);
	return query.getResultList();
    }

    public List<OstMigrazSubPart> getOstMigrazSubPartPerPartizStatoCorrList(String tipoPartizione,
	    String tiStatoCorrente, int numeroJob) {
	String queryStr = "SELECT migraz FROM OstMigrazSubPart migraz, OstStatoMigrazSubPart stato "
		+ "JOIN migraz.orgSubPartition sub " + "JOIN sub.orgPartition part "
		+ "WHERE migraz.idStatoMigrazSubPartCor=stato.idStatoMigrazSubPart "
		+ "AND stato.tiStato=:tiStato " + "AND part.tiPartition=:tiPartition "
		+ "AND migraz.niIstanzaJobPrepara=:numeroJob " + "ORDER BY migraz.mmMax ASC";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", tiStatoCorrente);
	query.setParameter("tiPartition", tipoPartizione);
	query.setParameter("numeroJob", bigDecimalFromInteger(numeroJob));
	return query.getResultList();
    }

    public List<String> getTableSpaceList(String tiStato) {
	List<String> nmTablespace;
	String queryStr = "SELECT DISTINCT migrazSubPart.nmTablespace FROM OstStatoMigrazSubPart statoMigrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart migrazSubPart "
		+ "WHERE statoMigrazSubPart.tiStato = :tiStato "
		+ "AND statoMigrazSubPart.idStatoMigrazSubPart = migrazSubPart.idStatoMigrazSubPartCor ";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("tiStato", tiStato);
	nmTablespace = query.getResultList();
	return nmTablespace;
    }

    /**
     * Verifica che TUTTE le partizioni che usano il tableSpace passato in input abbiano lo stato
     * passato in input
     *
     * @param nmTablespace nome table space
     * @param tiStato      tipo stato
     *
     * @return true/false risultato verifica
     */
    public boolean checkAllSubPartitionsWithTableSpaceAndState(String nmTablespace,
	    String tiStato) {
	// Recupero il numero di sub-partizioni che usano la tabella spazio e hanno stato diverso da
	// MIGRATA
	String queryStr2 = "SELECT migrazSubPart FROM OstStatoMigrazSubPart statoMigrazSubPart "
		+ "JOIN statoMigrazSubPart.ostMigrazSubPart migrazSubPart "
		+ "WHERE statoMigrazSubPart.tiStato != :tiStato "
		+ "AND migrazSubPart.nmTablespace = :nmTablespace "
		+ "AND statoMigrazSubPart.idStatoMigrazSubPart = migrazSubPart.idStatoMigrazSubPartCor ";
	Query query2 = entityManager.createQuery(queryStr2);
	query2.setParameter("nmTablespace", nmTablespace);
	query2.setParameter("tiStato", tiStato);
	query2.setMaxResults(1);
	return ((List<OstMigrazSubPart>) query2.getResultList()).isEmpty();
    }

    public List<OstMigrazSubPart> getOstMigrazSubPartByTablespace(String nmTablespace) {
	// Recupero il numero di sub-partizioni che usano la tabella spazio
	String queryStr = "SELECT migrazSubPart FROM OstMigrazSubPart migrazSubPart "
		+ "WHERE migrazSubPart.nmTablespace = :nmTablespace ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("nmTablespace", nmTablespace);
	return query.getResultList();
    }

    public List<BigDecimal> getOstVLisFileBlobIdByStrutMeseBetweenDate(BigDecimal idStrut,
	    Date data1, Date data2) {
	String queryStr = "SELECT ost.idCompDoc " + "FROM OstVLisFileBlobBystrumese ost "
		+ "WHERE ost.idStrut = :idStrut " + "AND ost.dtCreazione BETWEEN :data1 AND :data2";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idStrut", idStrut);
	query.setParameter("data1", data1);
	query.setParameter("data2", data2);
	return query.getResultList();
    }

    public List<OstMigrazStrutMese> getOstMigrazStrutMeseByOrgSubPartitionFlag(
	    OstMigrazSubPart ostMigrazSubPart, boolean flagFileAggiunti) {
	String queryString = "SELECT migraz "
		+ "FROM OstMigrazStrutMese migraz, OrgStrut strut, OstMigrazSubPart ostMigrazSubPart "
		+ "WHERE migraz.idStrut=strut.idStrut "
		+ "AND migraz.ostMigrazSubPart.idMigrazSubPart=ostMigrazSubPart.idMigrazSubPart "
		+ "AND migraz.ostMigrazSubPart=:ostMigrazSubPart "
		+ "AND migraz.flFileAggiunti=:flagFileAggiunti "
		+ "ORDER BY strut.nmStrut, migraz.mmVers ";

	Query query = entityManager.createQuery(queryString);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	query.setParameter("flagFileAggiunti", flagFileAggiunti ? "1" : "0");
	return query.getResultList();
    }

    public boolean existsNoMigrazFileWithTiCausaleNoMigraz(OstMigrazSubPart ostMigrazSubPart,
	    String tiCausaleNoMigraz) {
	String queryString = "SELECT no " + "FROM OstNoMigrazFile no "
		+ "WHERE no.ostMigrazSubPart=:ostMigrazSubPart "
		+ "AND no.tiCausaleNoMigraz=:tiCausaleNoMigraz";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	query.setParameter("tiCausaleNoMigraz", tiCausaleNoMigraz);
	query.setMaxResults(1);
	List<?> l = query.getResultList();
	return !l.isEmpty();
    }

    public List<OstVLisSubpartBlobByIstz> getSubPartitionByMesiAntecedenti(int numeroIstanzaJob) {
	String queryString = "SELECT vista FROM OstVLisSubpartBlobByIstz vista WHERE vista.niIstanzaJobPrepara=:numeroIstanzaJob "
		+ "ORDER BY vista.mmMax ASC, vista.numRows DESC";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("numeroIstanzaJob", bigDecimalFromInteger(numeroIstanzaJob));
	return query.getResultList();
    }

    /*
     * Conta tutti i file per tutte le partizioni che hanno lo stato definito in statoFile
     */
    public long getCountFileAllPartitionWithState(String statoFile) {
	String queryString = "SELECT  sum(mig.niFileMigrazInCorso) "
		+ "FROM    OstMigrazSubPart mig, OstStatoMigrazSubPart stato "
		+ "WHERE   mig.idStatoMigrazSubPartCor=stato.idStatoMigrazSubPart "
		+ "AND     stato.tiStato = :statoFile ";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("statoFile", statoFile);
	BigDecimal numero = (BigDecimal) query.getResultList().get(0);
	return numero == null ? 0L : numero.longValueExact();
    }

    public List<OstVLisStrutMmBlob> getStrutturaMesePerSubPartizione(BigDecimal idSubPartition) {
	String queryString = "select a from OstVLisStrutMmBlob a WHERE a.id.idSubPartition=:idSubPartition";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("idSubPartition", idSubPartition);
	return query.getResultList();
    }

    public List<OstMigrazSubPart> getOstMigrazSubPartBySubPartition(
	    OrgSubPartition orgSubPartition) {
	String queryString = "select a from OstMigrazSubPart a WHERE a.orgSubPartition=:orgSubPartition";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("orgSubPartition", orgSubPartition);
	return query.getResultList();
    }

    public boolean usoTbsOK(String nmTablespace) {
	String queryString = "SELECT chkUsoTb FROM OstVChkUsoTb chkUsoTb "
		+ "WHERE chkUsoTb.nmTablespace = :nmTablespace ";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("nmTablespace", nmTablespace);
	List<OstVChkUsoTb> chkList = query.getResultList();
	if (!chkList.isEmpty()) {
	    return chkList.get(0).getFlOk().equals("1");
	}
	return false;
    }

    public List<Object[]> getSommeFileMigrazConStati(List<String> listaStatiSubPartizioni) {
	String queryString = "SELECT  sum(mig.niFileDaMigrare), sum(mig.niFileMigrazInCorso), sum(mig.niFileMigrazInErrore) "
		+ "FROM    OstMigrazSubPart mig, OstStatoMigrazSubPart stato "
		+ "WHERE   stato.idStatoMigrazSubPart=mig.idStatoMigrazSubPartCor "
		+ "AND     stato.tiStato IN (:listaStatiSubPartizioni)";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("listaStatiSubPartizioni", listaStatiSubPartizioni);
	return query.getResultList();
    }

    /*
     * Torna tutte le subpartizioni in stato MIGRAZ_IN_CORSO con numero migraz in corso > 0
     */
    public List<OstMigrazSubPart> getSubPartitionsMigrazInCorsoErroreMoreThanZero() {
	String queryString = "SELECT  sub "
		+ "FROM    OstMigrazSubPart sub, OstStatoMigrazSubPart stato "
		+ "WHERE   sub.idStatoMigrazSubPartCor = stato.idStatoMigrazSubPart "
		+ "AND     stato.tiStato IN ( :stato1, :stato2 ) "
		+ "AND     sub.niFileMigrazInCorso > 0 ";
	Query query = entityManager.createQuery(queryString);
	query.setParameter("stato1",
		it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
			.name());
	query.setParameter("stato2",
		it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
			.name());
	return query.getResultList();
    }

    /*
     * Torna il numero di componenti con lo stato passato come parametro della subpartizione
     * passata.
     */

    public long getCountComponentsSubPartAndState(OstMigrazSubPart ostMigrazSubPart,
	    String tiStato) {
	ArrayList<String> al = new ArrayList<>();
	al.add(tiStato);
	return getCountComponentsSubPartAndState(ostMigrazSubPart, al);
    }

    public long getCountComponentsSubPartAndState(OstMigrazSubPart ostMigrazSubPart,
	    List<String> tiStato) {
	long ret = 0;
	String queryString = "SELECT  count(ost_file) " + "FROM    OstMigrazFile ost_file "
		+ "WHERE   ost_file.ostMigrazSubPart=:ostMigrazSubPart ";
	if (tiStato != null) {
	    queryString += "AND     ost_file.tiStatoCor IN (:tiStato)";
	}
	Query query = entityManager.createQuery(queryString);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	if (tiStato != null) {
	    query.setParameter("tiStato", tiStato);
	}
	List<Long> lista = query.getResultList();
	if (lista != null && !lista.isEmpty()) {
	    Object ogg = lista.get(0);
	    if (ogg != null) {
		ret = (Long) ogg;
	    }
	}
	return ret;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eliminaMigrazioneFileInNewTransaction(long id) {
	Query q = getEntityManager()
		.createQuery("DELETE FROM OstMigrazFile f WHERE f.idMigrazFile=:id");
	q.setParameter("id", id);
	q.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eliminaNoMigrazioneFileInNewTransaction(long id) {
	Query q = getEntityManager()
		.createQuery("DELETE FROM OstNoMigrazFile f WHERE f.idNoMigrazFile=:id");
	q.setParameter("id", id);
	q.executeUpdate();
    }

    /*
     * Torna solo gli ID delle OstMigrazFile per subpartizione
     */
    public List<Long> findOstMigrazFileIdByOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
	String qString = "SELECT  f.idMigrazFile FROM    OstMigrazFile f WHERE   f.ostMigrazSubPart=:ostMigrazSubPart";
	Query query = entityManager.createQuery(qString);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	return query.getResultList();
    }

    /*
     * Torna solo gli ID delle OstNoMigrazFile per subpartizione
     */
    public List<Long> findOstNoMigrazFileIdByOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
	String qString = "SELECT  f.idNoMigrazFile FROM    OstNoMigrazFile f WHERE   f.ostMigrazSubPart=:ostMigrazSubPart";
	Query query = entityManager.createQuery(qString);
	query.setParameter("ostMigrazSubPart", ostMigrazSubPart);
	return query.getResultList();
    }

    /*
     * Query nativa per estrarre gli id dei componenti
     */
    public List<BigDecimal> findIdAroContenutoCompByCodiceIdentificativo(
	    String codiceIdentificativo) {
	// Il codice identificativo non deve essere passato come stringa (delimitata dagli apici)ma
	// come costante ORACLE
	String qString = "select id_conten_comp " + "from aro_contenuto_comp subpartition ("
		+ codiceIdentificativo + ") conten "
		+ "where not exists (select * from aro_comp_object_storage comp_objsto "
		+ "		   where comp_objsto.id_comp_doc = conten.id_comp_strut_doc)";
	Query query = entityManager.createNativeQuery(qString);
	return query.getResultList();
    }

    /*
     * Query nativa per estrarre gli id dei componenti
     */
    public BigDecimal countAroContenutoCompByCodiceIdentificativo(
	    String codiceIdentificativoPartizione) {
	BigDecimal ret = null;
	// Il codice identificativo non deve essere passato come stringa (delimitata dagli apici)ma
	// come costante ORACLE
	String qString = "select count(*) " + "from aro_contenuto_comp subpartition ("
		+ codiceIdentificativoPartizione + ") conten ";
	Query query = entityManager.createNativeQuery(qString);
	List<Long> lista = query.getResultList();
	if (lista != null && !lista.isEmpty()) {
	    Object ogg = lista.get(0);
	    if (ogg != null) {
		ret = (BigDecimal) ogg;
	    }
	}
	return ret;
    }

    public OstMigrazSubPart findOstMigrazSubPartByOrgSubPartitionWithLock(
	    BigDecimal idSubPartition) {
	return findOstMigrazSubPartByOrgSubPartition(idSubPartition, true);
    }

    private OstMigrazSubPart findOstMigrazSubPartByOrgSubPartition(BigDecimal idSubPartition,
	    boolean withLock) {
	String qString = "SELECT o FROM OstMigrazSubPart o WHERE o.orgSubPartition.idSubPartition=:idSubPartition";
	Query query = entityManager.createQuery(qString);
	query.setParameter("idSubPartition", longFromBigDecimal(idSubPartition));
	if (withLock) {
	    query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
	}
	List<OstMigrazSubPart> l = query.getResultList();
	if (l.isEmpty()) {
	    return null;
	} else {
	    return l.iterator().next();
	}
    }

}
