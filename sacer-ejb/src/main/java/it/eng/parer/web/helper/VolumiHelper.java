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

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogOper;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.slite.gen.form.VolumiForm.Filtri;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.viewbean.VolVRicVolumeRowBean;
import it.eng.parer.slite.gen.viewbean.VolVRicVolumeTableBean;
import it.eng.parer.viewEntity.VolVRicVolume;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

/**
 * Session Bean implementation class VolVolumeConservHelper Contiene i metodi (implementati di
 * VolVolumeConservHelperLocal), per la gestione della persistenza su DB per le operarazioni CRUD su
 * oggetti di VolVolumeConservTableBean ed VolVolumeConservRowBean
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class VolumiHelper {

    /**
     * Default constructor.
     */
    public VolumiHelper() {
	/**
	 * Default constructor.
	 */
    }

    private static final Logger log = LoggerFactory.getLogger(VolumiHelper.class.getName());
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    LazyListHelper lazyListHelper;

    // ///////////////////////////////////////////////////
    // Metodi aggiunti
    // ///////////////////////////////////////////////////
    public VolVRicVolumeTableBean getVolVRicVolumeTB(BigDecimal idStrut, int maxResults) {
	String queryStr = "SELECT u FROM VolVRicVolume u WHERE u.idStrutVolume = :idstrut";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idstrut", idStrut);
	query.setMaxResults(maxResults);
	List<VolVRicVolume> lista = query.getResultList();
	VolVRicVolumeTableBean tmpTableBean = new VolVRicVolumeTableBean();
	for (VolVRicVolume vol : lista) {
	    VolVRicVolumeRowBean tmpRowBean = new VolVRicVolumeRowBean();
	    tmpRowBean.setNmVolumeConserv(vol.getNmVolumeConserv());
	    tmpTableBean.add(tmpRowBean);
	}
	return tmpTableBean;
    }

    // Metodo che restituisce un viewbean con i record trovati in base
    // ai filtri di ricerca passati in ingresso
    public VolVRicVolumeTableBean getVolVRicVolumeViewBean(BigDecimal idStrut, Filtri filtri,
	    int maxResults) throws EMFError {
	return getVolVRicVolumeViewBeanPlainFilters(idStrut, maxResults,
		filtri.getTi_stato_volume_conserv().parse(), filtri.getId_volume_conserv().parse(),
		filtri.getNm_volume_conserv().parse(), filtri.getDs_volume_conserv().parse(),
		filtri.getCreato_man().parse(), filtri.getDt_creazione_da().parse(),
		filtri.getDt_creazione_a().parse(), filtri.getCd_registro_key_unita_doc().parse(),
		filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
		filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse(),
		filtri.getCd_key_unita_doc_da().parse(), filtri.getCd_key_unita_doc_a().parse(),
		filtri.getNm_criterio_raggr().parse(), filtri.getTi_presenza_firme().parse(),
		filtri.getTi_val_firme().parse(), filtri.getNt_volume_chiuso().parse(),
		filtri.getNt_indice_volume().parse());
    }

    public VolVRicVolumeTableBean getVolVRicVolumeViewBeanPlainFilters(BigDecimal idStrut,
	    int maxResults, String stato, BigDecimal idVolume, String nmVolumeConserv,
	    String dsVolumeConserv, String creatoMan, Timestamp dtCreazioneDa,
	    Timestamp dtCreazioneA, String registro, BigDecimal anno, String codice,
	    BigDecimal anno_range_da, BigDecimal anno_range_a, String codice_range_da,
	    String codice_range_a, String criterio, String presenza, String validita,
	    String ntVolumeChiuso, String ntIndiceVolume) {
	String whereWord = "and ";
	StringBuilder queryStr = new StringBuilder(
		"SELECT DISTINCT new it.eng.parer.viewEntity.VolVRicVolume"
			+ "(u.dtChius, u.dtCreazione, u.dtFirmaMarca, u.dtScadChius, u.idVolumeConserv, u.niCompVolume,"
			+ " u.niKbSize, u.niMaxComp, u.niMaxUnitaDoc, u.niTempoScadChius, u.niTempoScadChiusFirme,"
			+ " u.niUnitaDocVolume, u.nmCriterioRaggr, u.nmVolumeConserv, u.dsVolumeConserv, u.tiPresenzaFirme,"
			+ " u.tiScadChiusVolume, u.tiStatoVolumeConserv, u.tiTempoScadChius, u.tiTempoScadChiusFirme,"
			+ " u.tiValFirme, u.ntIndiceVolume, u.dlMotivoChius, u.cdVersioneIndice, u.ntVolumeChiuso, u.idStrutVolume) FROM VolVRicVolume u"
			+ " WHERE u.idStrutVolume = :idstrut ");

	// Inserimento nella query del filtro STATO
	if (stato != null) {
	    queryStr.append(whereWord).append("u.tiStatoVolumeConserv = :statoin ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro ID VOLUME
	if (idVolume != null) {
	    queryStr.append(whereWord).append("u.idVolumeConserv = :idvolume ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro NM_VOLUME_CONSERV
	if (nmVolumeConserv != null) {
	    queryStr.append(whereWord).append("UPPER(u.nmVolumeConserv) LIKE :nmVolumeConserv ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro DS_VOLUME_CONSERV
	if (dsVolumeConserv != null) {
	    queryStr.append(whereWord).append("UPPER(u.dsVolumeConserv) LIKE :dsVolumeConserv ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro CREATO_MAN
	if (creatoMan != null) {
	    if (creatoMan.equals("1")) {
		queryStr.append(whereWord).append("u.idCriterioRaggr IS NULL ");
	    } else if (creatoMan.equals("0")) {
		queryStr.append(whereWord).append("u.idCriterioRaggr IS NOT NULL ");
	    }
	    whereWord = "and ";
	}

	Date data_da = null;
	Date data_a = null;

	// Inserimento nella query del filtro DATA CREAZIONE DA - A
	if (dtCreazioneDa != null) {
	    data_da = new Date(dtCreazioneDa.getTime());
	    if (dtCreazioneA != null) {
		data_a = new Date(dtCreazioneA.getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data_a);
		calendar.add(Calendar.DATE, 1);
		data_a = calendar.getTime();
	    } else {
		data_a = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data_a);
		calendar.add(Calendar.DATE, 1);
		data_a = calendar.getTime();
	    }
	}

	if ((data_da != null) && (data_a != null)) {
	    queryStr.append(whereWord).append("(u.dtCreazione between :datada AND :dataa) ");
	    whereWord = "and ";
	}
	// Inserimento nella query del filtro CHIAVE DOCUMENTO

	if (StringUtils.isNotBlank(registro)) {
	    queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registroin ");
	    whereWord = "AND ";
	}

	if (anno != null) {
	    queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
	    whereWord = "and ";
	}

	if (codice != null) {
	    queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro CHIAVE UNITA DOC PER RANGE

	if (anno_range_da != null && anno_range_a != null) {
	    queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a ");
	    whereWord = "AND ";
	}

	if (codice_range_da != null && codice_range_a != null) {
	    codice_range_da = StringPadding.padString(codice_range_da, "0", 12,
		    StringPadding.PADDING_LEFT);
	    codice_range_a = StringPadding.padString(codice_range_a, "0", 12,
		    StringPadding.PADDING_LEFT);
	    queryStr.append(whereWord).append(
		    "LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
	    whereWord = "AND ";
	}

	// Inserimento nella query del filtro CRITERIO

	if (criterio != null) {
	    queryStr.append(whereWord).append("u.nmCriterioRaggr = :criterioin ");
	    whereWord = "and ";
	}
	// Inserimento nella query del filtro PRESENZA FIRME

	if (presenza != null) {
	    queryStr.append(whereWord).append("u.tiPresenzaFirme = :presenzain ");
	    whereWord = "and ";
	}
	// Inserimento nella query del filtro VALIDITA' FIRME

	if (validita != null) {
	    queryStr.append(whereWord).append("u.tiValFirme = :validitain ");
	    whereWord = "and ";
	}
	// Inserimento nella query del filtro NT VOLUME CHIUSO

	if (ntVolumeChiuso != null) {
	    queryStr.append(whereWord).append("UPPER(u.ntVolumeChiuso) LIKE :ntVolumeChiuso ");
	    whereWord = "and ";
	}

	// Inserimento nella query del filtro NT INDICE VOLUME

	if (ntIndiceVolume != null) {
	    queryStr.append(whereWord).append("UPPER(u.ntIndiceVolume) LIKE :ntIndiceVolume ");
	}

	// ordina per data creazione decrescente
	queryStr.append("ORDER BY u.dtCreazione DESC");

	// CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
	Query query = entityManager.createQuery(queryStr.toString());
	// non avendo passato alla query i parametri di ricerca, devo passarli ora

	query.setParameter("idstrut", idStrut);

	if (stato != null) {
	    query.setParameter("statoin", stato);
	}

	if (idVolume != null) {
	    query.setParameter("idvolume", idVolume);
	}

	if (nmVolumeConserv != null) {
	    query.setParameter("nmVolumeConserv", "%" + nmVolumeConserv.toUpperCase() + "%");
	}

	if (dsVolumeConserv != null) {
	    query.setParameter("dsVolumeConserv", "%" + dsVolumeConserv.toUpperCase() + "%");
	}

	if (data_da != null && data_a != null) {
	    query.setParameter("datada", data_da, TemporalType.DATE);
	    query.setParameter("dataa", data_a, TemporalType.DATE);
	}

	if (StringUtils.isNotBlank(registro)) {
	    query.setParameter("registroin", registro);
	}

	if (anno != null) {
	    query.setParameter("annoin", anno);
	}

	if (codice != null) {
	    query.setParameter("codicein", codice);
	}

	if (anno_range_da != null && anno_range_a != null) {
	    query.setParameter("annoin_da", anno_range_da);
	    query.setParameter("annoin_a", anno_range_a);
	}

	if (codice_range_da != null && codice_range_a != null) {
	    query.setParameter("codicein_da", codice_range_da);
	    query.setParameter("codicein_a", codice_range_a);
	}

	if (criterio != null) {
	    query.setParameter("criterioin", criterio);
	}

	if (presenza != null) {
	    query.setParameter("presenzain", presenza);
	}

	if (validita != null) {
	    query.setParameter("validitain", validita);
	}

	if (ntVolumeChiuso != null) {
	    query.setParameter("ntVolumeChiuso", "%" + ntVolumeChiuso.toUpperCase() + "%");
	}

	if (ntIndiceVolume != null) {
	    query.setParameter("ntIndiceVolume", "%" + ntIndiceVolume.toUpperCase() + "%");
	}

	query.setMaxResults(maxResults);

	return lazyListHelper.getTableBean(query, list -> getVolVRicVolumeTableBeanFrom(list),
		"u.idVolumeConserv");

    }

    private VolVRicVolumeTableBean getVolVRicVolumeTableBeanFrom(List<VolVRicVolume> listaVolumi) {
	VolVRicVolumeTableBean volumiTableBean = new VolVRicVolumeTableBean();

	try {
	    if (listaVolumi != null && !listaVolumi.isEmpty()) {
		volumiTableBean = (VolVRicVolumeTableBean) Transform
			.entities2TableBean(listaVolumi);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}

	return volumiTableBean;
    }

    public VolVRicVolumeRowBean findVolVRicVolume(BigDecimal idVol) {

	String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.VolVRicVolume(u.dtChius, u.dtCreazione, u.dtFirmaMarca, u.dtScadChius, u.idVolumeConserv, u.niCompVolume, u.niKbSize, u.niMaxComp, u.niMaxUnitaDoc, u.niTempoScadChius, u.niTempoScadChiusFirme, u.niUnitaDocVolume, u.nmCriterioRaggr, u.nmVolumeConserv, u.dsVolumeConserv, u.tiPresenzaFirme, u.tiScadChiusVolume, u.tiStatoVolumeConserv, u.tiTempoScadChius, u.tiTempoScadChiusFirme, u.tiValFirme, u.ntIndiceVolume, u.dlMotivoChius, u.cdVersioneIndice, u.ntVolumeChiuso, u.idStrutVolume) FROM VolVRicVolume u WHERE u.idVolumeConserv = :idvol ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idvol", idVol);

	VolVRicVolumeRowBean dettaglio = new VolVRicVolumeRowBean();
	List<VolVRicVolume> listaVolumi = query.getResultList();
	try {
	    if (listaVolumi != null && !listaVolumi.isEmpty()) {
		VolVRicVolume record = listaVolumi.get(0);
		dettaglio = (VolVRicVolumeRowBean) Transform.entity2RowBean(record);
	    }
	} catch (Exception e) {
	    log.error("Errore nel recupero del dettaglio del volume {}", e.getMessage(), e);
	}

	return dettaglio;
    }

    public void saveNomeDesNote(Long idUser, BigDecimal idVolume, String nomeVol, String descrVol,
	    String noteVol, String noteVolChiuso) {
	String queryStr = "SELECT u FROM VolVolumeConserv u WHERE u.idVolumeConserv = :idvol ";
	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idvol", longFromBigDecimal(idVolume));

	List<VolVolumeConserv> volumeConserv = query.getResultList();
	VolVolumeConserv record = volumeConserv.get(0);
	record.setNmVolumeConserv(nomeVol);
	record.setDsVolumeConserv(descrVol);
	record.setNtIndiceVolume(noteVol);
	record.setNtVolumeChiuso(noteVolChiuso);
	try {
	    entityManager.persist(record);
	    entityManager.flush();
	    writeLogOper(record, record.getOrgStrut(), idUser,
		    VolumeEnums.OpTypeEnum.MODIFICA_VOLUME.name());
	} catch (RuntimeException re) {
	    // logga l'errore e blocca tutto
	    log.error("Eccezione nella persistenza della tabella VolVolumeConserv", re);
	}
    }

    public IamUser retrieveUserById(long userId) {
	Query q = entityManager.createQuery("SELECT u FROM IamUser u WHERE u.idUserIam = :userId");
	q.setParameter("userId", userId);
	return (IamUser) q.getSingleResult();
    }

    public void writeLogOper(VolVolumeConserv volume, OrgStrut struttura, Long idUser,
	    String tipoOper) {
	LogOper logOper = new LogOper();
	Date date = new Date();
	logOper.setOrgStrut(struttura);
	IamUser user = retrieveUserById(idUser);
	logOper.setDtOper(date);
	logOper.setTiOper(tipoOper);
	logOper.setIdVolumeConserv(new BigDecimal(volume.getIdVolumeConserv()));
	logOper.setNmVolumeConserv(volume.getNmVolumeConserv());
	logOper.setIamUser(user);
	try {
	    entityManager.persist(logOper);
	    entityManager.flush();
	} catch (RuntimeException re) {
	    // logga l'errore e blocca tutto
	    log.error("Eccezione nella persistenza della tabella LogOper", re);
	}
    }

    public boolean existNomeVolume(String nome, BigDecimal idStruttura) {
	String queryStr = "SELECT u FROM VolVolumeConserv u WHERE u.orgStrut.idStrut = :idstrut and u.nmVolumeConserv = :nomecrit";

	Query query = entityManager.createQuery(queryStr);
	query.setParameter("idstrut", longFromBigDecimal(idStruttura));
	query.setParameter("nomecrit", nome);

	return !query.getResultList().isEmpty();
    }

    public OrgStrutRowBean getOrgStrutRowBean(BigDecimal idStrut) {
	OrgStrut strut = entityManager.find(OrgStrut.class, idStrut.longValue());
	OrgStrutRowBean rb = new OrgStrutRowBean();
	if (strut != null) {
	    try {
		rb = (OrgStrutRowBean) Transform.entity2RowBean(strut);
		rb.setString("nm_ambiente", strut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
		rb.setString("nm_ente", strut.getOrgEnte().getNmEnte());
	    } catch (Exception e) {
		log.error(e.getMessage(), e);
	    }
	}
	return rb;
    }
}
