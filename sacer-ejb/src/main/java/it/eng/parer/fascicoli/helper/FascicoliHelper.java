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

package it.eng.parer.fascicoli.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecVoceTitol;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasEventoFascicolo;
import it.eng.parer.entity.FasEventoSog;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.entity.FasValoreAttribFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.MonContaFascicoli;
import it.eng.parer.entity.MonContaFascicoliKo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsFascicoloKo;
import it.eng.parer.entity.VrsSesFascicoloErr;
import it.eng.parer.entity.VrsSesFascicoloKo;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloTableBean;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVRicFascicoli;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.viewEntity.MonVChkFascByAmb;
import it.eng.parer.viewEntity.MonVChkFascByEnte;
import it.eng.parer.viewEntity.MonVChkFascByStrut;
import it.eng.parer.viewEntity.MonVChkFascByTiFasc;
import it.eng.parer.viewEntity.MonVChkFascKoByAmb;
import it.eng.parer.viewEntity.MonVChkFascKoByEnte;
import it.eng.parer.viewEntity.MonVChkFascKoByStrut;
import it.eng.parer.viewEntity.MonVChkFascKoByTiFasc;
import it.eng.parer.viewEntity.MonVCntFascByAmb;
import it.eng.parer.viewEntity.MonVCntFascByEnte;
import it.eng.parer.viewEntity.MonVCntFascByStrut;
import it.eng.parer.viewEntity.MonVCntFascByTiFasc;
import it.eng.parer.viewEntity.MonVCntFascKoByAmb;
import it.eng.parer.viewEntity.MonVCntFascKoByEnte;
import it.eng.parer.viewEntity.MonVCntFascKoByStrut;
import it.eng.parer.viewEntity.MonVCntFascKoByTiFasc;
import it.eng.parer.viewEntity.MonVLisFasc;
import it.eng.parer.viewEntity.MonVLisFascDaElab;
import it.eng.parer.viewEntity.MonVLisFascKo;
import it.eng.parer.viewEntity.MonVLisFascKoByErr;
import it.eng.parer.viewEntity.VrsVUpdFascicoloKo;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

/**
 * @author Moretti_Lu and Iacolucci_M
 */
@SuppressWarnings({
	"rawtypes", "unchecked" })
@Stateless
@LocalBean
public class FascicoliHelper extends GenericHelper {
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;

    private static final Logger logger = LoggerFactory.getLogger(FascicoliHelper.class);

    /**
     * Ricerca dei fascicoli filtrandoli per Struttura di appartenenza e abilitazioni dell'utente
     * corrente
     *
     * @param filtri  filtro ricerca fascicoli
     * @param idStrut id struttura
     * @param userId  id utente
     *
     * @return lista elemnti di tipo FasVRicFascicoli
     */
    public List<Object[]> retrieveFascicoli(RicercaFascicoliBean filtri, BigDecimal idStrut,
	    long userId) {
	return retrieveFascicoli(idStrut, userId, new FiltriRicercaFascicoli(filtri));
    }

    /**
     * Ricerca dei fascicoli annullati filtrandoli per Struttura di appartenenza, abilitazioni
     * dell'utente corrente e stato di conservazione ANNULLATO
     *
     * @param filtri  filtri per la ricerca dei fascicoli
     * @param idStrut id della struttura
     * @param userId  id dell'utente
     *
     * @return Lista di {@link FasVRicFascicoli}
     */
    public List<FasVRicFascicoli> retrieveFascicoliAnnullati(RicercaFascicoliBean filtri,
	    BigDecimal idStrut, long userId) {
	return retrieveFascicoliAnnullati(new FiltriRicercaFascicoli(filtri), idStrut, userId);
    }

    /**
     * FLAGS
     *
     * @param idAmbiente id ambiente
     * @param idUser     id utente
     *
     * @return Lista {@link MonVChkFascByAmb}
     */
    public List<MonVChkFascByAmb> retrieveMonFascicoliByAmbUser(BigDecimal idAmbiente,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascByAmb.findByAmbUser",
		    MonVChkFascByAmb.class);
	    query.setParameter("idAmbiente", idAmbiente);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per ambiente [{}] e utente [{}]",
		    idAmbiente, idUser, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGI
     *
     * @param idAmbiente id ambiente
     * @param idUser     id utente
     *
     * @return Lista {@link MonVCntFascByAmb}
     */
    public List<MonVCntFascByAmb> retrieveCntMonFascicoliByAmbUser(BigDecimal idAmbiente,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascByAmb.findByAmbUser",
		    MonVCntFascByAmb.class);
	    query.setParameter("idAmbiente", idAmbiente);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per ambiente [{}] e utente [{}]",
		    idAmbiente, idUser, ex);
	    throw ex;
	}
    }

    /**
     * FLAGS
     *
     * @param idEnte id ente
     * @param idUser id utente
     *
     * @return Lista {@link MonVChkFascByEnte}
     */
    public List<MonVChkFascByEnte> retrieveMonFascicoliByEnteUser(BigDecimal idEnte,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascByEnte.findByEnteUser",
		    MonVChkFascByEnte.class);
	    query.setParameter("idEnte", idEnte);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per ente [{}] e utente [{}]",
		    idEnte, idUser, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGI
     *
     * @param idEnte id ente
     * @param idUser id utente
     *
     * @return Lista {@link MonVCntFascByEnte}
     */
    public List<MonVCntFascByEnte> retrieveCntMonFascicoliByEnteUser(BigDecimal idEnte,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascByEnte.findByEnteUser",
		    MonVCntFascByEnte.class);
	    query.setParameter("idEnte", idEnte);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per ente [{}] e utente [{}]",
		    idEnte, idUser, ex);
	    throw ex;
	}
    }

    /**
     * FLAGS
     *
     * @param idStrut   id struttura
     * @param idUserIam id user
     *
     * @return Lista {@link MonVChkFascByStrut}
     */
    public List<MonVChkFascByStrut> retrieveMonFascicoliByStrutUser(BigDecimal idStrut,
	    BigDecimal idUserIam) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascByStrut.findByStrutUser",
		    MonVChkFascByStrut.class);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("idUserIam", idUserIam);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per struttura [{}]", idStrut, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGI
     *
     * @param idStrut   id struttura
     * @param idUserIam id utente
     *
     * @return Lista {@link MonVCntFascByStrut}
     */
    public List<MonVCntFascByStrut> retrieveCntMonFascicoliByStrutUserId(BigDecimal idStrut,
	    BigDecimal idUserIam) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonVCntFascByStrut.findByStrutUserId", MonVCntFascByStrut.class);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("idUserIam", idUserIam);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per struttura [{}]", idStrut, ex);
	    throw ex;
	}
    }

    /**
     * FLAGS
     *
     * @param idTipoFascicolo id tipo fascicolo
     *
     * @return Lista di {@link MonVChkFascByTiFasc}
     */
    public List<MonVChkFascByTiFasc> retrieveMonFascicoliByTipoFascicolo(
	    BigDecimal idTipoFascicolo) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascByTiFasc.findByTiFasc",
		    MonVChkFascByTiFasc.class);
	    query.setParameter("idTipoFascicolo", idTipoFascicolo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per tipo fascicolo [{}]",
		    idTipoFascicolo, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGIO
     *
     * @param idTipoFascicolo id tipo fascicolo
     *
     * @return Lista di {@link MonVCntFascByTiFasc}
     */
    public List<MonVCntFascByTiFasc> retrieveCntMonFascicoliByTipoFascicolo(
	    BigDecimal idTipoFascicolo) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascByTiFasc.findByTiFasc",
		    MonVCntFascByTiFasc.class);
	    query.setParameter("idTipoFascicolo", idTipoFascicolo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring per tipo fascicolo [{}]",
		    idTipoFascicolo, ex);
	    throw ex;
	}
    }

    /**
     * FLAGS
     *
     * @param idAmbiente id ambiente
     * @param idUser     id utente
     *
     * @return Lista di {@link MonVChkFascKoByAmb}
     */
    public List<MonVChkFascKoByAmb> retrieveMonFascicoliKoByAmbUser(BigDecimal idAmbiente,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascKoByAmb.findByAmbUser",
		    MonVChkFascKoByAmb.class);
	    query.setParameter("idAmbiente", idAmbiente);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per ambiente [{}] e utente [{}]",
		    idAmbiente, idUser, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGI
     *
     * @param idAmbiente id ambietne
     * @param idUser     id utente
     *
     * @return Lista di {@link MonVCntFascKoByAmb}
     */
    public List<MonVCntFascKoByAmb> retrieveCntMonFascicoliKoByAmbUser(BigDecimal idAmbiente,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascKoByAmb.findByAmbUser",
		    MonVCntFascKoByAmb.class);
	    query.setParameter("idAmbiente", idAmbiente);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per ambiente [{}] e utente [{}]",
		    idAmbiente, idUser, ex);
	    throw ex;
	}
    }

    /**
     * FLAGS
     *
     * @param idEnte id ente
     * @param idUser id utente
     *
     * @return Lista di {@link MonVChkFascKoByEnte}
     */
    public List<MonVChkFascKoByEnte> retrieveMonFascicoliKoByEnteUser(BigDecimal idEnte,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascKoByEnte.findByEnteUser",
		    MonVChkFascKoByEnte.class);
	    query.setParameter("idEnte", idEnte);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per ente [{}] e utente [{}]",
		    idEnte, idUser, ex);
	    throw ex;
	}
    }

    /**
     * CONTEGGIO
     *
     * @param idEnte id ente
     * @param idUser id utente
     *
     * @return Lista di {@link MonVCntFascKoByEnte}
     */
    public List<MonVCntFascKoByEnte> retrieveCntMonFascicoliKoByEnteUser(BigDecimal idEnte,
	    BigDecimal idUser) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascKoByEnte.findByEnteUser",
		    MonVCntFascKoByEnte.class);
	    query.setParameter("idEnte", idEnte);
	    query.setParameter("idUser", idUser);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per ente [{}] e utente [{}]",
		    idEnte, idUser, ex);
	    throw ex;
	}
    }

    // FLAGS
    public List<MonVChkFascKoByStrut> retrieveMonFascicoliKoByStrutUser(BigDecimal idStrut,
	    BigDecimal idUserIam) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonVChkFascKoByStrut.findByStrutUser", MonVChkFascKoByStrut.class);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("idUserIam", idUserIam);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per struttura [{}]", idStrut,
		    ex);
	    throw ex;
	}
    }

    // CONTEGGI
    public List<MonVCntFascKoByStrut> retrieveCntMonFascicoliKoByStrutUserId(BigDecimal idStrut,
	    BigDecimal idUserIam) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonVCntFascKoByStrut.findByStrutUserId", MonVCntFascKoByStrut.class);
	    query.setParameter("idStrut", idStrut);
	    query.setParameter("idUserIam", idUserIam);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione del monitoring KO per struttura [{}] e utente [{}]",
		    idStrut, idUserIam, ex);
	    throw ex;
	}
    }

    // FLAGS
    public List<MonVChkFascKoByTiFasc> retrieveMonFascicoliKoByTipoFascicolo(
	    BigDecimal idTipoFascicolo) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVChkFascKoByTiFasc.findByTiFasc",
		    MonVChkFascKoByTiFasc.class);
	    query.setParameter("idTipoFascicolo", idTipoFascicolo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per tipo fascicolo [{}]",
		    idTipoFascicolo, ex);
	    throw ex;
	}
    }

    // CONTEGGIO
    public List<MonVCntFascKoByTiFasc> retrieveCntMonFascicoliKoByTipoFascicolo(
	    BigDecimal idTipoFascicolo) {
	try {
	    Query query = getEntityManager().createNamedQuery("MonVCntFascKoByTiFasc.findByTiFasc",
		    MonVCntFascKoByTiFasc.class);
	    query.setParameter("idTipoFascicolo", idTipoFascicolo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per tipo fascicolo [{}]",
		    idTipoFascicolo, ex);
	    throw ex;
	}
    }

    public Query retrieveSessioniFalliteByIdFascKo(BigDecimal idFascicoloKo) {
	try {
	    Query query = getEntityManager().createNamedQuery("VrsSesFascicoloKo.findByFascicoloKo",
		    VrsSesFascicoloKo.class);
	    query.setParameter("idFascicoloKo", longFromBigDecimal(idFascicoloKo));
	    return query;
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione del monitoring KO per ID tipo fascicolo [{}]",
		    idFascicoloKo, ex);
	    throw ex;
	}
    }

    public VrsSesFascicoloKo getSessioneFallitaByIdSess(BigDecimal idSesFascicoloKo) {
	VrsSesFascicoloKo ses = null;
	try {
	    ses = findById(VrsSesFascicoloKo.class, idSesFascicoloKo);
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione della sessione errata con id [{}]",
		    idSesFascicoloKo, ex);
	    throw ex;
	}
	return ses;
    }

    public void bulkDeleteCriteriRaggrFasc(List<Long> idCriterioRaggrFascList) {
	if (!idCriterioRaggrFascList.isEmpty()) {
	    String queryStr = "DELETE FROM DecCriterioRaggrFasc criterioRaggrFasc "
		    + "WHERE criterioRaggrFasc.idCriterioRaggrFasc IN (:idCriterioRaggrFascList)";
	    Query q = getEntityManager().createQuery(queryStr);
	    q.setParameter("idCriterioRaggrFascList", idCriterioRaggrFascList);
	    q.executeUpdate();
	    getEntityManager().flush();
	}
    }

    public void bulkUpdateCriteriRaggrFasc(List<Long> idCriterioRaggrFascList) {
	if (!idCriterioRaggrFascList.isEmpty()) {
	    String queryStr = "UPDATE DecCriterioRaggrFasc criterioRaggrFasc SET criterioRaggrFasc.flFiltroTipoFascicolo = '0' "
		    + "WHERE criterioRaggrFasc.idCriterioRaggrFasc IN (:idCriterioRaggrFascList)";
	    Query q = getEntityManager().createQuery(queryStr);
	    q.setParameter("idCriterioRaggrFascList", idCriterioRaggrFascList);
	    q.executeUpdate();
	    getEntityManager().flush();
	}
    }

    /**
     * Verifica l'esistenza di fascicoli versati del tipo e per il range di anni passati in
     * ingresso.
     *
     * @param idTipoFascicolo    id tipo fascicolo
     * @param aaIniTipoFascicolo anno inizio tipo fascicolo
     * @param aaFinTipoFascicolo anno fine tipo fascicolo
     *
     * @return true/false
     */
    public boolean existFascicoliVersatiPerTipoFascicolo(BigDecimal idTipoFascicolo,
	    BigDecimal aaIniTipoFascicolo, BigDecimal aaFinTipoFascicolo) {
	if (idTipoFascicolo != null) {
	    StringBuilder queryStr = new StringBuilder(
		    "SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo "
			    + "WHERE tipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
			    + "AND EXISTS (SELECT fascicolo FROM FasFascicolo fascicolo WHERE fascicolo.decTipoFascicolo = tipoFascicolo ");
	    if (aaIniTipoFascicolo != null && aaFinTipoFascicolo != null) {
		queryStr.append(
			"AND fascicolo.aaFascicolo BETWEEN :aaIniTipoFascicolo AND :aaFinTipoFascicolo");
	    }
	    queryStr.append(")");
	    Query query = getEntityManager().createQuery(queryStr.toString());
	    query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
	    if (aaIniTipoFascicolo != null && aaFinTipoFascicolo != null) {
		query.setParameter("aaIniTipoFascicolo", aaIniTipoFascicolo);
		query.setParameter("aaFinTipoFascicolo", aaFinTipoFascicolo);
	    }
	    return !query.getResultList().isEmpty();
	} else {
	    throw new IllegalArgumentException("Parametro idTipoFascicolo nullo");
	}
    }

    /**
     * Verifica l'esistenza di fascicoli versati del modello passato in ingresso.
     *
     * @param idModelloXsdFascicolo id modello xsd fascicolo
     * @param idTipoFascicolo       id tipo fascicolo
     *
     * @return true/false
     */
    public boolean existFascicoliVersatiPerModelloFascicolo(BigDecimal idModelloXsdFascicolo,
	    BigDecimal idTipoFascicolo) {
	if (idModelloXsdFascicolo != null) {
	    String queryStr = "SELECT modelloFascicolo FROM DecModelloXsdFascicolo modelloFascicolo "
		    + "WHERE modelloFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo "
		    + "AND EXISTS (SELECT xmlFascicolo FROM FasXmlFascicolo xmlFascicolo "
		    + "JOIN xmlFascicolo.fasFascicolo.decTipoFascicolo tipoFascicolo "
		    + "WHERE xmlFascicolo.decModelloXsdFascicolo = modelloFascicolo "
		    + "AND tipoFascicolo.idTipoFascicolo = :idTipoFascicolo)";
	    Query query = getEntityManager().createQuery(queryStr);
	    query.setParameter("idModelloXsdFascicolo", longFromBigDecimal(idModelloXsdFascicolo));
	    query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));

	    return !query.getResultList().isEmpty();
	} else {
	    throw new IllegalArgumentException("Parametro idModelloXsdFascicolo nullo");
	}
    }

    /**
     * Verifica l'esistenza di fascicoli versati per la struttura passata in ingresso.
     *
     * @param idStrut id struttura
     *
     * @return true/false
     */
    public boolean existFascicoliVersatiPerStruttura(BigDecimal idStrut) {
	if (idStrut != null) {
	    String queryStr = "SELECT strut FROM OrgStrut strut WHERE strut.idStrut = :idStrut "
		    + "AND EXISTS (SELECT fascicolo FROM FasFascicolo fascicolo WHERE fascicolo.orgStrut = strut) ";
	    Query query = getEntityManager().createQuery(queryStr);
	    query.setParameter("idStrut", longFromBigDecimal(idStrut));
	    return !query.getResultList().isEmpty();
	} else {
	    throw new IllegalArgumentException("Parametro idStrut nullo");
	}
    }

    /**
     * Verifica l'esistenza di selezioni di criteri di raggruppamento fascicoli sul criterio del
     * tipo passato in ingresso.
     *
     * @param idCriterioRaggrFasc id criterio raggruppamento
     * @param tiSel               tipo selettore
     *
     * @return int dimensione
     */
    public int countSelCriteriRaggrFascPerTipo(BigDecimal idCriterioRaggrFasc, String tiSel) {
	List<DecSelCriterioRaggrFasc> result = retrieveSelCriterioRaggrFascicoli(
		idCriterioRaggrFasc, tiSel);
	return result.size();
    }

    public FasVVisFascicolo retrieveFasVVisFascicolo(long idFascicolo) {
	FasVVisFascicolo result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasVVisFascicolo.find",
		    FasVVisFascicolo.class);
	    query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
	    result = (FasVVisFascicolo) query.getSingleResult();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasVVisFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public FasVRicFascicoli retrieveFasVRicFascicoli(long idFascicolo) {
	FasVRicFascicoli result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasVRicFascicoli.findById",
		    FasVRicFascicoli.class);
	    query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
	    result = (FasVRicFascicoli) query.getSingleResult();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasVRicFascicoli per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasVLisUdInFasc> retrieveFasVLisUdInFasc(long idFascicolo, long userId) {
	List<FasVLisUdInFasc> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasVLisUdInFasc.find",
		    FasVLisUdInFasc.class);
	    query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
	    query.setParameter("userId", bigDecimalFromLong(userId));
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasVLisUdInFasc per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<Object[]> findCountFascicoliVersatiNelGiorno(Date data) {
	List<Object[]> result;
	try {
	    Query query = getEntityManager()
		    .createNamedQuery("FasFascicolo.findCountFascicoliVersatiNelGiorno");
	    query.setParameter("data", data);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione dei conteggi fascicoli versati per la data [{}]",
		    data, ex);
	    throw ex;
	}
	return result;
    }

    /**
     * Recupera i tipi di fascicolo per le chiavi fascicolo
     *
     * @param idUtente    id utente
     * @param idStruttura id struttura
     *
     * @return lista entity di tipo DecTipoFascicolo
     */
    public List<DecTipoFascicolo> getTipiFascicoloAbilitati(long idUtente, BigDecimal idStruttura) {
	List<BigDecimal> idStrutList = new ArrayList<>();
	idStrutList.add(idStruttura);
	return getTipiFascicoloAbilitatiDaStrutturaList(idUtente, idStrutList);
    }

    public List<Object[]> findCountFascicoliNonVersatiNelGiorno(Date data) {
	List<Object[]> result = null;
	try {
	    Query query = getEntityManager()
		    .createNamedQuery("VrsFascicoloKo.findCountFascicoliNonVersatiNelGiorno");
	    query.setParameter("data", data);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione dei conteggi fascicoli non versati per la data [{}]",
		    data, ex);
	    throw ex;
	}
	return result;
    }

    // Estrae i fascicoli NON ANNULLATI
    public List<FasFascicolo> retrieveFasFascicoloByStrutAnnoNumValid(OrgStrut strut, long anno,
	    String numero) {
	List<FasFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasFascicolo.findByStrutAnnoNum",
		    FasFascicolo.class);
	    query.setParameter("orgStrut", strut);
	    query.setParameter("aaFascicolo", bigDecimalFromLong(anno));
	    query.setParameter("cdKeyFascicolo", numero);
	    Calendar c = Calendar.getInstance();
	    c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    query.setParameter("dtAnnull", c.getTime());
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasFascicolo", ex);
	    throw ex;
	}
	return result;
    }

    public List<VrsFascicoloKo> retrieveFasNonVersatoByStrutAnnoNum(OrgStrut strut, long anno,
	    String numero, Boolean pessimisticLock) {
	List<VrsFascicoloKo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("VrsFascicoloKo.findByStrutAnnoNum",
		    VrsFascicoloKo.class);
	    if (Boolean.TRUE.equals(pessimisticLock)) {
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
	    }
	    query.setParameter("orgStrut", strut);
	    query.setParameter("aaFascicolo", bigDecimalFromLong(anno));
	    query.setParameter("cdKeyFascicolo", numero);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle VrsFascicoloKo", ex);
	    throw ex;
	}
	return result;
    }

    public List<FasAmminPartec> retrieveFasAmminPartec(long idFascicolo) {
	List<FasAmminPartec> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasAmminPartec.find",
		    FasAmminPartec.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasAmminPartec per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasSogFascicolo> retrieveFasSogFascicolo(long idFascicolo) {
	List<FasSogFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasSogFascicolo.find",
		    FasSogFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasSogFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasEventoFascicolo> retrieveFasEventoFascicolo(long idFascicolo) {
	List<FasEventoFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "FasEventoFascicolo.findEventiFascicolo", FasEventoFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasEventoFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasValoreAttribFascicolo> retrieveFasValoreAttribFascicolo(long idFascicolo,
	    long idModelloXsdFascicolo) {
	List<FasValoreAttribFascicolo> result = null;
	try {
	    Query query = getEntityManager()
		    .createQuery("SELECT valore FROM FasValoreAttribFascicolo valore "
			    + "JOIN valore.decUsoModelloXsdFasc uso "
			    + "JOIN uso.decModelloXsdFascicolo modello "
			    + "WHERE valore.fasFascicolo.idFascicolo = :idFascicolo "
			    + "AND modello.idModelloXsdFascicolo = :idModelloXsdFascicolo ");
	    query.setParameter("idFascicolo", idFascicolo);
	    query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle FasValoreAttribFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasEventoSog> retrieveFasEventoSog(long idSogFascicolo) {
	List<FasEventoSog> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasEventoSog.findEventiSoggetto",
		    FasEventoSog.class);
	    query.setParameter("idSogFascicolo", idSogFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasEventoSog per il soggetto fascicolo [{}]",
		    idSogFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public String getEventiSoggetto(long idSogFascicolo) {
	String result = null;
	try {
	    Query query = getEntityManager().createNativeQuery(
		    "SELECT LISTAGG(eventoSoggetto.ds_denom_evento, ', ') FROM Fas_Evento_Sog eventoSoggetto WHERE eventoSoggetto.id_Sog_Fascicolo = :idSogFascicolo ");
	    query.setParameter("idSogFascicolo", idSogFascicolo);
	    result = (String) query.getSingleResult();
	    if (result != null) {
		return result;
	    }
	    return "";
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle stringhe FasEventoSog per il soggetto fascicolo [{}]",
		    idSogFascicolo, ex);
	    throw ex;
	}
    }

    public String getFasCodIdeSog(long idSogFascicolo, String tiCodSog) {
	String result = null;
	try {
	    Query query = getEntityManager().createNativeQuery(
		    "SELECT LISTAGG(ide.cd_sog || ': ' || ide.nm_cod_sog, ', ') FROM sacer.Fas_Cod_Ide_Sog ide WHERE ide.id_Sog_Fascicolo = :idSogFascicolo "
			    + "AND ide.ti_cod_sog = :tiCodSog ");
	    query.setParameter("idSogFascicolo", idSogFascicolo);
	    query.setParameter("tiCodSog", tiCodSog);
	    result = (String) query.getSingleResult();
	    if (result != null) {
		return result;
	    }
	    return "";
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle stringhe FasEventoSog per il soggetto fascicolo [{}]",
		    idSogFascicolo, ex);
	    throw ex;
	}
    }

    public List<FasRespFascicolo> retrieveFasRespFascicolo(long idFascicolo) {
	List<FasRespFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasRespFascicolo.find",
		    FasRespFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasRespFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasUniOrgRespFascicolo> retrieveFasUniOrgRespFascicolo(long idFascicolo) {
	List<FasUniOrgRespFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasUniOrgRespFascicolo.find",
		    FasUniOrgRespFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle FasUniOrgRespFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasLinkFascicolo> retrieveFasLinkFascicolo(long idFascicolo) {
	List<FasLinkFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasLinkFascicolo.find",
		    FasLinkFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasLinkFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<FasLinkFascicolo> retrieveFasLinkFascicoloParent(long idFascicolo) {
	List<FasLinkFascicolo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("FasLinkFascicolo.findByIdFasLink",
		    FasLinkFascicolo.class);
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle FasLinkFascicolo per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public List<ElvVRicElencoFascByFas> retrieveFasElvFascicolo(long idFascicolo) {
	List<ElvVRicElencoFascByFas> result = null;
	try {
	    String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByFas(u.id.idElencoVersFasc, u.tiStato, u.aaFascicoloElenco, u.niFascVersElenco, "
		    + "u.dlMotivoChius, u.tsCreazioneElenco, u.dtChiusura, u.dtFirma, u.idCriterioRaggrFasc, "
		    + "u.nmCriterioRaggr, u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, "
		    + "u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo) "
		    + "FROM ElvVRicElencoFascByFas u " + "WHERE u.id.idFascicolo = :idFascicolo "
		    // Filtro per gestire l'eventuale presenza, a seguito di uno o più errori di
		    // firma, di più stati
		    // CHIUSO, in modo da considerare solo l'ultimo registrato
		    + "AND ((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT s.tsStato FROM ElvStatoElencoVersFasc s WHERE s.idStatoElencoVersFasc = u.idStatoElencoVersFascCor AND s.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)) "
		    // Filtro per gestire l'eventuale presenza, a seguito di più stati CHIUSO, di
		    // molteplici stati
		    // FIRMATO, in modo da considerare solo lo stato di chiusura registrato prima
		    // della firma
		    + "OR ((u.dtFirma IS NOT NULL) AND (u.dtChiusura = (SELECT MAX(s1.tsStato) FROM ElvStatoElencoVersFasc s1 WHERE s1.tiStato = :statoChiuso AND s1.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)))) "
		    + "ORDER BY u.tsCreazioneElenco ";

	    Query query = getEntityManager().createQuery(queryStr);
	    query.setParameter("idFascicolo", bigDecimalFromLong(idFascicolo));
	    query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle ElvVRicElencoFascByFas per il fascicolo [{}]",
		    idFascicolo, ex);
	}
	return result;
    }

    public List<DecClasseErrSacer> retrieveClasseErrSacerByTipiUso(List<String> tipiUsoErr) {
	List<DecClasseErrSacer> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("DecClasseErrSacer.findByTipiUsoErr",
		    DecClasseErrSacer.class);
	    query.setParameter("tipiUsoErr", tipiUsoErr);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle DecClasseErrSacer per il tipi uso [{}]",
		    tipiUsoErr, ex);
	    throw ex;
	}
	return result;
    }

    public List<DecClasseErrSacer> retrieveClasseErrSacerByCodice(String cdClasseErrSacer) {
	List<DecClasseErrSacer> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("DecClasseErrSacer.findByCodice",
		    DecClasseErrSacer.class);
	    query.setParameter("cdClasseErrSacer", cdClasseErrSacer);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione delle DecClasseErrSacer per il cdClasseErrSacer [{}]",
		    cdClasseErrSacer, ex);
	    throw ex;
	}
	return result;
    }

    public List<VrsVUpdFascicoloKo> retrieveVrsVUpdFascicoloKoByFascKo(long idFascicoloKo) {
	List<VrsVUpdFascicoloKo> result = null;
	try {
	    Query query = getEntityManager().createNamedQuery("VrsVUpdFascicoloKo.findByIdFascKo",
		    VrsVUpdFascicoloKo.class);
	    query.setParameter("idFascicoloKo", idFascicoloKo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione di VrsSesFascicoloKo", ex);
	    throw ex;
	}
	return result;
    }

    public List<DecErrSacer> retrieveErrSacerByCodClasse(String codClasse) {
	List<DecErrSacer> result = null;

	try {
	    Query query = getEntityManager().createNamedQuery("DecErrSacer.findByCodClasse",
		    DecErrSacer.class);
	    query.setParameter("codClasse", codClasse);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione delle DecErrSacer per il codice [{}]", codClasse,
		    ex);
	    throw ex;
	}
	return result;
    }

    public VrsSesFascicoloErr retrieveDettSessFascErr(BigDecimal idSess) {
	try {
	    return getEntityManager().find(VrsSesFascicoloErr.class, idSess.longValue());
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione del dettaglio sessione fascicolo errata, idSessione=[{}]",
		    idSess, ex);
	    throw ex;
	}
    }

    // CONTEGGIO FASCICOLI KO
    public List<MonContaFascicoliKo> retrieveMonContaFascicoliKoByChiaveTotaliz(Date dtRifConta,
	    OrgStrut orgStrut, String tiStatoFascicoloKo, long aaFascicolo,
	    DecTipoFascicolo decTipoFascicolo, boolean lockEsclusivo) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonContaFascicoliKo.findByChiaveTotaliz", MonContaFascicoliKo.class);
	    if (lockEsclusivo) {
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
	    }
	    query.setParameter("dtRifConta", dtRifConta);
	    query.setParameter("orgStrut", orgStrut);
	    query.setParameter("tiStatoFascicoloKo", tiStatoFascicoloKo);
	    query.setParameter("aaFascicolo", bigDecimalFromLong(aaFascicolo));
	    query.setParameter("decTipoFascicolo", decTipoFascicolo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione di MonContaFascicoliKo", ex);
	    throw ex;
	}
    }

    public List<MonContaFascicoli> retrieveMonContaFascicoliByChiaveTotaliz(BigDecimal idStrut,
	    Date dtRifConta, BigDecimal idTipoFascicolo, BigDecimal aaFascicolo,
	    BigDecimal idUserIam) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonContaFascicoli.findByChiaveTotalizz", MonContaFascicoli.class);
	    query.setParameter("idStrut", longFromBigDecimal(idStrut));
	    query.setParameter("dtRifConta", dtRifConta);
	    query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
	    query.setParameter("aaFascicolo", aaFascicolo);
	    query.setParameter("idUserIam", longFromBigDecimal(idUserIam));
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione di MonContaFascicoli", ex);
	    throw ex;
	}
    }

    public List<MonContaFascicoliKo> retrieveMonContaFascicoliNonVersByChiaveTotaliz(
	    BigDecimal idStrut, Date dtRifConta, BigDecimal idTipoFascicolo, BigDecimal aaFascicolo,
	    String tiStatoFascicoloKo) {
	try {
	    Query query = getEntityManager().createNamedQuery(
		    "MonContaFascicoliKo.findByChiaveTotalizIds", MonContaFascicoliKo.class);
	    query.setParameter("idStrut", longFromBigDecimal(idStrut));
	    query.setParameter("dtRifConta", dtRifConta);
	    query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
	    query.setParameter("aaFascicolo", aaFascicolo);
	    query.setParameter("tiStatoFascicoloKo", tiStatoFascicoloKo);
	    return query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione di MonContaFascicoliKo", ex);
	    throw ex;
	}
    }

    public List<DecCriterioRaggrFasc> retrieveCriteriRaggrFascicoli(
	    CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc,
	    boolean filterValid) throws EMFError {
	return retrieveCriteriRaggrFascicoli(filterValid,
		new FiltriCriteriRaggrFascicoliPlain(filtriCriteriRaggrFasc));
    }

    /**
     *
     * @param idCriterioRaggrFasc id criterio di raggruppamento fascicoli
     * @param tiSel               tipo sel
     *
     * @return Lista di {@link DecSelCriterioRaggrFasc}
     */
    public List<DecSelCriterioRaggrFasc> retrieveSelCriterioRaggrFascicoli(
	    BigDecimal idCriterioRaggrFasc, String tiSel) {
	List<DecSelCriterioRaggrFasc> result = null;

	try {
	    String whereWord = " and ";
	    StringBuilder queryStr = new StringBuilder(
		    "SELECT scrf FROM DecSelCriterioRaggrFasc scrf "
			    + "WHERE scrf.decCriterioRaggrFasc.idCriterioRaggrFasc = :idcrit ");
	    if (tiSel != null) {
		queryStr.append(whereWord).append("scrf.tiSel = :filtro ");
	    }
	    queryStr.append("ORDER BY scrf.tiSel");
	    Query query = getEntityManager().createQuery(queryStr.toString());
	    query.setParameter("idcrit", longFromBigDecimal(idCriterioRaggrFasc));
	    if (tiSel != null) {
		query.setParameter("filtro", tiSel);
	    }

	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione di DecSelCriterioRaggrFasc con idCriterioRaggrFasc [{}], tiSel [{}]",
		    idCriterioRaggrFasc, tiSel, ex);
	    throw ex;
	}

	return result;
    }

    public List<FasFascicolo> getSottofascicoli(long idFascicolo) {
	List<FasFascicolo> result = null;
	try {
	    Query query = getEntityManager().createQuery(
		    "SELECT sottofascicolo FROM FasFascicolo sottofascicolo WHERE sottofascicolo.fasFascicoloPadre.idFascicolo = :idFascicolo ");
	    query.setParameter("idFascicolo", idFascicolo);
	    result = query.getResultList();
	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione dei sottofascicoli per il fascicolo [{}]",
		    idFascicolo, ex);
	    throw ex;
	}
	return result;
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFascById(BigDecimal idCriterioRaggrFasc) {
	DecCriterioRaggrFasc result = null;

	try {
	    result = getEntityManager().find(DecCriterioRaggrFasc.class,
		    idCriterioRaggrFasc.longValue());
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione di DecCriterioRaggrFasc con idCriterioRaggrFasc [{}]",
		    idCriterioRaggrFasc, ex);
	    throw ex;
	}

	return result;
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFasc(BigDecimal idStrutCorrente,
	    String nmCriterioRaggr) {
	StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggrFasc u ");
	String whereWord = "WHERE ";

	if (idStrutCorrente != null) {
	    queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrutCorrente ");
	    whereWord = "AND ";
	}

	if (nmCriterioRaggr != null) {
	    queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
	}

	Query query = getEntityManager().createQuery(queryStr.toString());

	if (idStrutCorrente != null) {
	    query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
	}

	if (nmCriterioRaggr != null) {
	    query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
	}

	List<DecCriterioRaggrFasc> list = query.getResultList();

	if (list.isEmpty()) {
	    return null;
	}
	return list.get(0);
    }

    public boolean existNomeCriterio(String nome, BigDecimal idStruttura) {
	String queryStr = "SELECT crf FROM DecCriterioRaggrFasc crf WHERE crf.orgStrut.idStrut = :idstrut and crf.nmCriterioRaggr = :nomecrit";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idstrut", longFromBigDecimal(idStruttura));
	query.setParameter("nomecrit", nome);
	return !query.getResultList().isEmpty();
    }

    public Long saveCritRaggrFasc(LogParam param, CreaCriterioRaggrFascicoli filtri,
	    Object[] anniFascicoliValidati, BigDecimal idStruttura, String nome,
	    String criterioStandard, List<BigDecimal> voceTitolList) throws EMFError {
	DecCriterioRaggrFasc decCriterioRaggrFasc = new DecCriterioRaggrFasc();
	if (nome != null) {
	    // Se c'è il parametro nome, carico il criterio di raggruppamento fascicoli
	    // corrispondente
	    decCriterioRaggrFasc = getDecCriterioRaggrFascByStrutAndCriterio(idStruttura, nome);
	}

	if (decCriterioRaggrFasc.getDecSelCriterioRaggrFascicoli() == null) {
	    decCriterioRaggrFasc.setDecSelCriterioRaggrFascicoli(new ArrayList<>());
	}
	final OrgStrut orgStrut = getOrgStrut(idStruttura);
	decCriterioRaggrFasc.setOrgStrut(orgStrut);

	// Setto i filtri multipli a 0 come default
	decCriterioRaggrFasc.setFlFiltroTipoFascicolo("0");
	decCriterioRaggrFasc.setFlFiltroSistemaMigraz("0");
	decCriterioRaggrFasc.setFlFiltroVoceTitol("0");

	// Per ogni tipo fascicolo creo un record filtro multiplo
	if (filtri.getNm_tipo_fascicolo().parse() != null
		&& !filtri.getNm_tipo_fascicolo().parse().isEmpty()) {
	    StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoFascicolo u ");
	    queryStr.append("WHERE u.idTipoFascicolo in (:idtipofascicolo)");
	    List<BigDecimal> asList = filtri.getNm_tipo_fascicolo().parse();
	    List<DecTipoFascicolo> lista = getDecTipoFascicoloList(queryStr, asList);
	    if (!lista.isEmpty()) {
		decCriterioRaggrFasc.setFlFiltroTipoFascicolo("1");
		for (DecTipoFascicolo tipo : lista) {
		    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri
		    // sono già presenti prima
		    // di salvarli
		    if (nome != null) {
			final List<DecSelCriterioRaggrFasc> decSelCriterioRaggrFascList = getDecSelCriterioRaggrFascList(
				decCriterioRaggrFasc, tipo);
			if (decSelCriterioRaggrFascList.isEmpty()) {
			    saveSelCriterioRaggrFascTipoFasc(decCriterioRaggrFasc, tipo,
				    ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
			}
		    } else {
			saveSelCriterioRaggrFascTipoFasc(decCriterioRaggrFasc, tipo,
				ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
		    }
		}
		if (nome != null) {
		    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect,
		    // che non
		    // risulterebbero più presenti nella lista
		    // Eseguo perciò una bulk delete sui record non presenti nella lista
		    deleteDecSelCriterioRaggrFascNotInTipoFascicolo(decCriterioRaggrFasc, asList);
		}
	    }
	} else {
	    // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato
	    // precedentemente
	    // Eseguo perciò una bulk delete per eliminare quei record
	    if (nome != null) {
		deleteDecSelCriterioRaggrFascFiltroTipoFascicolo(decCriterioRaggrFasc);
	    }
	}

	// Per ogni voce di titolario creo un record filtro multiplo
	if (voceTitolList != null && !voceTitolList.isEmpty()) {
	    List<DecVoceTitol> lista = getDecVoceTitolList(voceTitolList);
	    if (!lista.isEmpty()) {
		decCriterioRaggrFasc.setFlFiltroVoceTitol("1");
		for (DecVoceTitol voce : lista) {
		    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri
		    // sono già presenti prima
		    // di salvarli
		    if (nome != null) {
			final List<DecSelCriterioRaggrFasc> decSelCriterioRaggrFascList = getDecSelCriterioRaggrFascList(
				decCriterioRaggrFasc, voce);
			if (decSelCriterioRaggrFascList.isEmpty()) {
			    saveSelCriterioRaggrFascVoceTitol(decCriterioRaggrFasc, voce,
				    ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
			}
		    } else {
			saveSelCriterioRaggrFascVoceTitol(decCriterioRaggrFasc, voce,
				ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
		    }
		}
		if (nome != null) {
		    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect,
		    // che non
		    // risulterebbero più presenti nella lista
		    // Eseguo perciò una bulk delete sui record non presenti nella lista
		    deleteDecSelCriterioRaggrFascNotInVoceTitolo(voceTitolList,
			    decCriterioRaggrFasc);
		}
	    }
	} else {
	    // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato
	    // precedentemente
	    // Eseguo perciò una bulk delete per eliminare quei record
	    if (nome != null) {
		deleteDecSelCriterioRaggrFascFiltroVoceTitolo(decCriterioRaggrFasc);
	    }
	}

	decCriterioRaggrFasc.setNmCriterioRaggr(filtri.getNm_criterio_raggr().parse());
	decCriterioRaggrFasc.setDsCriterioRaggr(filtri.getDs_criterio_raggr().parse());
	decCriterioRaggrFasc.setNiMaxFasc(filtri.getNi_max_fasc().parse());
	decCriterioRaggrFasc.setTiScadChius(filtri.getTi_scad_chius().getValue());
	decCriterioRaggrFasc.setNiTempoScadChius(filtri.getNi_tempo_scad_chius().parse());
	decCriterioRaggrFasc.setTiTempoScadChius(filtri.getTi_tempo_scad_chius().getValue());
	decCriterioRaggrFasc.setDtIstituz(filtri.getDt_istituz().parse());
	decCriterioRaggrFasc.setDtSoppres(filtri.getDt_soppres().parse());
	decCriterioRaggrFasc.setAaFascicolo(filtri.getAa_fascicolo().parse());
	Object annoDa = (anniFascicoliValidati != null && anniFascicoliValidati.length > 1
		? anniFascicoliValidati[0]
		: null);
	Object annoA = (anniFascicoliValidati != null && anniFascicoliValidati.length > 1
		? anniFascicoliValidati[1]
		: null);
	decCriterioRaggrFasc.setAaFascicoloDa((BigDecimal) annoDa);
	decCriterioRaggrFasc.setAaFascicoloA((BigDecimal) annoA);
	decCriterioRaggrFasc.setTiConservazione(filtri.getTi_conservazione().parse());
	decCriterioRaggrFasc.setNtCriterioRaggr(filtri.getNt_criterio_raggr().parse());
	decCriterioRaggrFasc.setFlCriterioRaggrStandard(criterioStandard);

	try {
	    getEntityManager().persist(decCriterioRaggrFasc);
	    getEntityManager().flush();
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC,
		    new BigDecimal(decCriterioRaggrFasc.getIdCriterioRaggrFasc()),
		    param.getNomePagina());
	} catch (RuntimeException re) {
	    /// logga l'errore e blocca tutto
	    logger.error("Eccezione nella persistenza", re);
	    throw new EMFError(EMFError.BLOCKING, re);
	}

	return decCriterioRaggrFasc.getIdCriterioRaggrFasc();
    }

    private void saveSelCriterioRaggrFascTipoFasc(DecCriterioRaggrFasc crit,
	    DecTipoFascicolo tipoFascicolo, String tiSel) {
	DecSelCriterioRaggrFasc filtro = new DecSelCriterioRaggrFasc();
	filtro.setDecCriterioRaggrFasc(crit);
	filtro.setDecTipoFascicolo(tipoFascicolo);
	filtro.setTiSel(tiSel);
	crit.getDecSelCriterioRaggrFascicoli().add(filtro);
    }

    private void saveSelCriterioRaggrFascVoceTitol(DecCriterioRaggrFasc crit,
	    DecVoceTitol voceTitol, String tiSel) {
	DecSelCriterioRaggrFasc filtro = new DecSelCriterioRaggrFasc();
	filtro.setDecCriterioRaggrFasc(crit);
	filtro.setDecVoceTitol(voceTitol);
	filtro.setTiSel(tiSel);
	crit.getDecSelCriterioRaggrFascicoli().add(filtro);
    }

    public boolean deleteCritRaggrFasc(LogParam param, BigDecimal idStrut, String nmCriterioRaggr)
	    throws ParerUserError {
	boolean result = false;

	DecCriterioRaggrFasc row = getDecCriterioRaggrFascByStrutAndCriterio(idStrut,
		nmCriterioRaggr);
	if (row != null && row.getElvElencoVersFasc().isEmpty()) {
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC,
		    new BigDecimal(row.getIdCriterioRaggrFasc()), param.getNomePagina());
	    // Rimuovo il record
	    getEntityManager().remove(row);
	    getEntityManager().flush();
	    logger.info(
		    "Cancellazione criterio di raggruppamento fascicoli {} della struttura {} avvenuta con successo!",
		    nmCriterioRaggr, idStrut);
	    result = true;
	} else {
	    throw new ParerUserError("Errore nell'eliminazione del criterio "
		    + (row != null ? row.getNmCriterioRaggr() : "-")
		    + ", il criterio è collegato a degli elenchi di versamento");
	}
	return result;
    }

    public boolean existElvElencoVersPerCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc) {
	String queryStr = "SELECT elencoVersFasc FROM ElvElencoVersFasc elencoVersFasc "
		+ "WHERE EXISTS (SELECT criterioRaggrFasc FROM DecCriterioRaggrFasc criterioRaggrFasc WHERE criterioRaggrFasc.idCriterioRaggrFasc = :idCriterioRaggrFasc AND elencoVersFasc.decCriterioRaggrFasc = criterioRaggrFasc)";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idCriterioRaggrFasc", longFromBigDecimal(idCriterioRaggrFasc));
	return !query.getResultList().isEmpty();
    }

    /**
     * Recupera i tipi di fascicolo per le chiavi fascicolo
     *
     * @param idUtente        id utente
     * @param idStrutturaList lista id struttura
     *
     * @return lista entity di tipo DecTipoFascicolo
     */
    public List<DecTipoFascicolo> getTipiFascicoloAbilitatiDaStrutturaList(long idUtente,
	    List<BigDecimal> idStrutturaList) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT u FROM DecTipoFascicolo u , IamAbilTipoDato iatd WHERE iatd.idTipoDatoApplic = u.idTipoFascicolo ");
	queryStr.append(
		" AND iatd.nmClasseTipoDato = 'TIPO_FASCICOLO' AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
	if (!idStrutturaList.isEmpty()) {
	    queryStr.append("AND u.orgStrut.idStrut IN (:idStrutturaList) ");
	}
	queryStr.append("ORDER BY u.nmTipoFascicolo");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idUtente", idUtente);
	if (!idStrutturaList.isEmpty()) {
	    query.setParameter("idStrutturaList", longListFrom(idStrutturaList));
	}
	return query.getResultList();
    }

    public FasFascicoloTableBean getListaFasFascicoloElvViewBean(BigDecimal idElencoVersFasc,
	    ElenchiVersFascicoliForm.FascicoliFiltri filtri,
	    DecTipoFascicoloTableBean tmpTableBeanTipoFasc) throws EMFError {
	return getListaFasFascicoloElvViewBean(idElencoVersFasc, tmpTableBeanTipoFasc,
		new FiltriElenchiVersFascicoli(filtri));
    }

    /**
     * Determina i fascicoli con stato nell’elenco = IN_ELENCO_IN_CODA_CREAZIONE_AIP o
     * IN_ELENCO_CON_AIP_CREATO o IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO o IN_ELENCO_COMPLETATO nel
     * cui contenuto sono presenti le unità documentarie corrispondenti agli item (della richiesta
     * corrente) di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER
     *
     * @param idRichAnnulVers id della richiesta di annullamento
     *
     * @return la lista dei fascicoli
     */
    public List<BigDecimal> retrieveFasVLisFascByRichann(long idRichAnnulVers) {
	Query query = getEntityManager()
		.createQuery("SELECT fas.id.idFascicolo FROM FasVLisFascByRichannUd fas "
			+ "WHERE fas.id.idRichAnnulVers = :idRichAnnulVers ");
	query.setParameter("idRichAnnulVers", bigDecimalFromLong(idRichAnnulVers));
	return query.getResultList();
    }

    public FasFileMetaVerAipFasc getFasFileMetaVerAipFasc(long idFascicolo, String tiMeta) {
	Query query = getEntityManager().createQuery(
		"SELECT fileMetaVerAipFasc FROM FasFileMetaVerAipFasc fileMetaVerAipFasc "
			+ "JOIN fileMetaVerAipFasc.fasMetaVerAipFascicolo metaVerAipFascicolo "
			+ "JOIN metaVerAipFascicolo.fasVerAipFascicolo verAipFascicolo "
			+ "WHERE verAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
			+ "AND verAipFascicolo.pgVerAipFascicolo = (SELECT MAX(verAipFascicolo2.pgVerAipFascicolo) FROM FasVerAipFascicolo verAipFascicolo2 WHERE verAipFascicolo2.fasFascicolo.idFascicolo = :idFascicolo) "
			+ "AND metaVerAipFascicolo.tiMeta = :tiMeta ");
	query.setParameter("idFascicolo", idFascicolo);
	query.setParameter("tiMeta", tiMeta);
	List<FasFileMetaVerAipFasc> meta = query.getResultList();
	if (!meta.isEmpty()) {
	    return meta.get(0);
	}
	return null;
    }

    public FasMetaVerAipFascicolo getFasMetaVerAipFascicolo(long idFascicolo, String tiMeta) {
	Query query = getEntityManager().createQuery(
		"SELECT metaVerAipFascicolo FROM FasMetaVerAipFascicolo metaVerAipFascicolo "
			+ "JOIN metaVerAipFascicolo.fasVerAipFascicolo verAipFascicolo "
			+ "WHERE verAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
			+ "AND verAipFascicolo.pgVerAipFascicolo = (SELECT MAX(verAipFascicolo2.pgVerAipFascicolo) FROM FasVerAipFascicolo verAipFascicolo2 WHERE verAipFascicolo2.fasFascicolo.idFascicolo = :idFascicolo) "
			+ "AND metaVerAipFascicolo.tiMeta = :tiMeta ");
	query.setParameter("idFascicolo", idFascicolo);
	query.setParameter("tiMeta", tiMeta);
	List<FasMetaVerAipFascicolo> meta = query.getResultList();
	if (!meta.isEmpty()) {
	    return meta.get(0);
	}
	return null;
    }

    public FasVerAipFascicolo getFasVerAipFascicolo(long idFascicolo) {
	Query query = getEntityManager()
		.createQuery("SELECT verAipFascicolo FROM FasVerAipFascicolo verAipFascicolo "
			+ "WHERE verAipFascicolo.fasFascicolo.idFascicolo = :idFascicolo "
			+ "AND verAipFascicolo.pgVerAipFascicolo = "
			+ "(SELECT MAX(verAipFascicolo2.pgVerAipFascicolo) "
			+ "FROM FasVerAipFascicolo verAipFascicolo2 "
			+ "WHERE verAipFascicolo2.fasFascicolo.idFascicolo = :idFascicolo) ");
	query.setParameter("idFascicolo", idFascicolo);
	List<FasVerAipFascicolo> ver = query.getResultList();
	if (!ver.isEmpty()) {
	    return ver.get(0);
	}
	return null;
    }

    public Long getIdFascVersatoNoAnnul(BigDecimal idStrut, BigDecimal aaFascicolo,
	    String cdKeyFascicolo) {
	String queryStr = "SELECT u.idFascicolo FROM FasFascicolo u "
		+ "WHERE u.orgStrut.idStrut = :idStrut " + "AND u.aaFascicolo = :aaFascicolo "
		+ "AND u.cdKeyFascicolo = :cdKeyFascicolo " + "AND u.dtAnnull = :dtAnnull ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	query.setParameter("aaFascicolo", aaFascicolo);
	query.setParameter("cdKeyFascicolo", cdKeyFascicolo);
	Calendar cal = Calendar.getInstance();
	cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
	cal.set(Calendar.MILLISECOND, 0);
	query.setParameter("dtAnnull", cal.getTime());
	List<Long> listaUdVersate = query.getResultList();
	if (listaUdVersate != null && !listaUdVersate.isEmpty()) {
	    return listaUdVersate.get(0);
	} else {
	    return null;
	}
    }

    /**
     * Controlla che il fascicolo identificato dalla struttura versante, anno e numero esista
     *
     *
     * @param idStrut        id struttura
     * @param aaFascicolo    anno fascicolo
     * @param cdKeyFascicolo numero fascicolo
     *
     * @return true/false
     */
    public boolean existsFascicolo(BigDecimal idStrut, BigDecimal aaFascicolo,
	    String cdKeyFascicolo) {
	String queryStr = "SELECT COUNT(u) FROM FasFascicolo u "
		+ "WHERE u.orgStrut.idStrut = :idStrut " + "AND u.aaFascicolo = :aaFascicolo "
		+ "AND u.cdKeyFascicolo = :cdKeyFascicolo ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	query.setParameter("aaFascicolo", aaFascicolo);
	query.setParameter("cdKeyFascicolo", cdKeyFascicolo);
	Long numFasc = (Long) query.getSingleResult();
	return numFasc > 0;
    }

    public AroRichAnnulVers getAroRichAnnulVersFasc(long idFascicolo) {
	String queryStr = "SELECT rich FROM AroItemRichAnnulVers item "
		+ "JOIN item.aroRichAnnulVers rich JOIN rich.aroStatoRichAnnulVers stati "
		+ "WHERE item.fasFascicolo.idFascicolo = :idFascicolo "
		+ "AND stati.pgStatoRichAnnulVers = (SELECT MAX(maxStati.pgStatoRichAnnulVers) FROM AroStatoRichAnnulVers maxStati WHERE maxStati.aroRichAnnulVers.idRichAnnulVers = rich.idRichAnnulVers) "
		+ "AND stati.tiStatoRichAnnulVers = 'EVASA' ";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idFascicolo", idFascicolo);
	List<AroRichAnnulVers> list = query.getResultList();
	if (list != null && !list.isEmpty()) {
	    return list.get(0);
	} else {
	    return null;
	}
    }

    public CriteriaQuery<VrsSesFascicoloErr> createSessFascErrateCriteriaQuery(Date dataDa,
	    Date dataA, String statoSessione, String cdClasseErr, String cdErr) {
	try {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<VrsSesFascicoloErr> cq = cb.createQuery(VrsSesFascicoloErr.class);
	    Root<VrsSesFascicoloErr> entity = cq.from(VrsSesFascicoloErr.class);
	    cq.select(entity);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsIniSes")));
	    List<Predicate> condizioni = new ArrayList<>();
	    if (dataDa != null && dataA != null) {
		condizioni.add(cb.between(entity.get("tsIniSes"), dataDa, dataA));
	    }
	    if (statoSessione != null && !statoSessione.equals("")) {
		condizioni.add(cb.equal(entity.get("tiStatoSes"), statoSessione));
	    }
	    if (cdErr != null && !cdErr.equals("")) {
		condizioni.add(cb.equal(entity.get("decErrSacer").get("cdErr"), cdErr));
	    }
	    if (cdClasseErr != null && !cdClasseErr.equals("")) {
		condizioni.add(cb.equal(
			entity.get("decErrSacer").get("decClasseErrSacer").get("cdClasseErrSacer"),
			cdClasseErr));
	    }
	    cq.where(condizioni.toArray(new Predicate[] {}));
	    return cq;
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nella creazione delle query per VrsSesFascicoloErr con dataDa [{}], dataA [{}], statoSessione [{}], cdClasseErr [{}], cdErr [{}]",
		    dataDa, dataA, statoSessione, cdClasseErr, cdErr, ex);
	    throw ex;
	}
    }

    /**
     * Ricerca dei fascicoli filtrandoli per Struttura di appartenenza e abilitazioni dell'utente
     * corrente
     *
     * @param idStrut id struttura
     * @param userId  id utente
     * @param filtri  filtri di ricerca fascicoli
     *
     * @return Lista di {@link FasVRicFascicoli}
     */
    public List<FasVRicFascicoli> retrieveFascicoli_old(BigDecimal idStrut, long userId,
	    FiltriRicercaFascicoli filtri) {
	List<FasVRicFascicoli> result = null;
	String andClause = "AND ";
	StringBuilder queryStr = new StringBuilder(
		"SELECT DISTINCT new it.eng.parer.viewEntity.FasVRicFascicoli(f.idFascicolo, "
			+ "f.aaFascicolo, f.cdKeyFascicolo, f.cdCompositoVoceTitol, f.nmTipoFascicolo, f.dtApeFascicolo, f.dtChiuFascicolo, "
			+ "f.tsVersFascicolo, f.niUnitaDoc, f.niAaConservazione, f.cdLivelloRiserv, f.flForzaContrClassif, "
			+ "f.flForzaContrNumero, f.flForzaContrColleg, f.tiStatoFascElencoVers, f.tiStatoConservazione) FROM FasVRicFascicoli f ");
	StringBuilder whereClauseStr = new StringBuilder("WHERE f.idStrut = :idStrut ");

	if (filtri.getAaFascicolo() != null) {
	    whereClauseStr.append(andClause).append("f.aaFascicolo = :aaFascicolo ");
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    whereClauseStr.append(andClause)
		    .append("f.aaFascicolo BETWEEN :aaFascicolo_da AND :aaFascicolo_a ");
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    whereClauseStr.append(andClause).append("f.cdKeyFascicolo = :cdKeyFascicolo ");
	}
	String cdKeyFascicoloDa = null;
	String cdKeyFascicoloA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyFascicoloA())) {
	    cdKeyFascicoloDa = StringPadding.padString(filtri.getCdKeyFascicoloDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyFascicoloA = StringPadding.padString(filtri.getCdKeyFascicoloA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    whereClauseStr.append(andClause).append(
		    "LPAD(f.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicolo_da AND :cdKeyFascicolo_a ");
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    whereClauseStr.append(andClause).append("f.idTipoFascicolo = :idTipoFascicolo ");
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    whereClauseStr.append(andClause).append("f.cdVersioneXsd = :cdXsd ");
	    whereClauseStr.append(andClause).append("f.tiModelloXsd = :tiModelloXsd ");
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    whereClauseStr.append(andClause).append("f.cdVersioneXsd IS NOT NULL ");
	    whereClauseStr.append(andClause).append("f.tiModelloXsd = :tiModelloXsd ");
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicolo())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.dsOggettoFascicolo) LIKE :dsOggettoFascicolo ");
	}
	if (filtri.getDtApeFascioloDa() != null && filtri.getDtApeFascioloA() != null) {
	    whereClauseStr.append(andClause)
		    .append("f.dtApeFascicolo BETWEEN :dtApeFascicolo_da AND :dtApeFascicolo_a ");
	}
	if (filtri.getDtChiuFascioloDa() != null && filtri.getDtChiuFascioloA() != null) {
	    whereClauseStr.append(andClause).append(
		    "f.dtChiuFascicolo BETWEEN :dtChiuFascicolo_da AND :dtChiuFascicolo_a ");
	}
	if (StringUtils.isNotBlank(filtri.getCdProcAmmin())) {
	    whereClauseStr.append(andClause).append("UPPER(f.cdProcAmmin) LIKE :cdProcAmmin ");
	}
	if (StringUtils.isNotBlank(filtri.getDsProcAmmin())) {
	    whereClauseStr.append(andClause).append("UPPER(f.dsProcAmmin) LIKE :dsProcAmmin ");
	}
	if (filtri.getNiAaConservazione() != null) {
	    whereClauseStr.append(andClause).append("f.niAaConservazione = :niAaConservazione ");
	}
	if (StringUtils.isNotBlank(filtri.getCdLivelloRiserv())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.cdLivelloRiserv) LIKE :cdLivelloRiserv ");
	}
	if (StringUtils.isNotBlank(filtri.getNmSistemaVersante())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.nmSistemaVersante) LIKE :nmSistemaVersante ");
	}
	if (StringUtils.isNotBlank(filtri.getNmUserid())) {
	    whereClauseStr.append(andClause).append("UPPER(f.nmUserid) LIKE :nmUserid ");
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.cdCompositoVoceTitol) LIKE :cdCompositoVoceTitol ");
	}
	if (filtri.getAaFascicoloPadre() != null) {
	    whereClauseStr.append(andClause).append("f.aaFascicoloPadre = :aaFascicoloPadre ");
	}
	if (filtri.getAaFascicoloPadreDa() != null && filtri.getAaFascicoloPadreA() != null) {
	    whereClauseStr.append(andClause).append(
		    "f.aaFascicoloPadre BETWEEN :aaFascicoloPadre_da AND :aaFascicoloPadre_a ");
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadre())) {
	    whereClauseStr.append(andClause)
		    .append("f.cdKeyFascicoloPadre = :cdKeyFascicoloPadre ");
	}
	String cdKeyFascicoloPadreDa = null;
	String cdKeyFascicoloPadreA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadreDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadreA())) {
	    cdKeyFascicoloPadreDa = StringPadding.padString(filtri.getCdKeyFascicoloPadreDa(), "0",
		    12, StringPadding.PADDING_LEFT);
	    cdKeyFascicoloPadreA = StringPadding.padString(filtri.getCdKeyFascicoloPadreA(), "0",
		    12, StringPadding.PADDING_LEFT);
	    whereClauseStr.append(andClause).append(
		    "LPAD( f.cdKeyFascicoloPadre, 12, '0') BETWEEN :cdKeyFascicoloPadre_da AND :cdKeyFascicoloPadre_a ");
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicoloPadre())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.dsOggettoFascicoloPadre) LIKE :dsOggettoFascicoloPadre ");
	}
	boolean udFilter = false;
	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    whereClauseStr.append(andClause)
		    .append("aud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
	    udFilter = true;
	}
	if (filtri.getAaKeyUnitaDoc() != null) {
	    whereClauseStr.append(andClause).append("aud.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
	    udFilter = true;
	}
	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    whereClauseStr.append(andClause)
		    .append("aud.aaKeyUnitaDoc BETWEEN :aaKeyUnitaDoc_da AND :aaKeyUnitaDoc_a ");
	    udFilter = true;
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDoc())) {
	    whereClauseStr.append(andClause).append("aud.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
	    udFilter = true;
	}
	String cdKeyUnitaDocDa = null;
	String cdKeyUnitaDocA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDocDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyUnitaDocA())) {
	    cdKeyUnitaDocDa = StringPadding.padString(filtri.getCdKeyFascicoloPadreDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyUnitaDocA = StringPadding.padString(filtri.getCdKeyFascicoloPadreA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    whereClauseStr.append(andClause).append(
		    "LPAD( aud.cdKeyUnitaDoc, 12, '0') BETWEEN :cdKeyUnitaDoc_da AND :cdKeyUnitaDoc_a ");
	    udFilter = true;
	}
	if (udFilter) {
	    whereClauseStr.append(andClause)
		    .append("fud.fasFascicolo.idFascicolo = f.idFascicolo ");
	    queryStr.append(", FasUnitaDocFascicolo fud JOIN fud.aroUnitaDoc aud ");
	}
	if (StringUtils.isNotBlank(filtri.getTiConservazione())) {
	    whereClauseStr.append(andClause).append("f.tiConservazione = :tiConservazione ");
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrClassif())) {
	    whereClauseStr.append(andClause)
		    .append("f.flForzaContrClassif = :flForzaContrClassif ");
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrNumero())) {
	    whereClauseStr.append(andClause).append("f.flForzaContrNumero = :flForzaContrNumero ");
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrColleg())) {
	    whereClauseStr.append(andClause).append("f.flForzaContrColleg = :flForzaContrColleg ");
	}
	if (StringUtils.isNotBlank(filtri.getCdVersioneWs())) {
	    whereClauseStr.append(andClause).append("f.cdVersioneXmlSip = :cdVersioneWs ");
	}

	if (filtri.getTsVersFascicoloDa() != null && filtri.getTsVersFascicoloA() != null) {
	    whereClauseStr.append(andClause).append(
		    "f.tsVersFascicolo BETWEEN :tsVersFascicolo_da AND :tsVersFascicolo_a ");
	}
	if (StringUtils.isNotBlank(filtri.getTiEsito())) {
	    whereClauseStr.append(andClause).append("f.tiEsito = :tiEsito ");
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoConservazione())) {
	    whereClauseStr.append(andClause)
		    .append("f.tiStatoConservazione = :tiStatoConservazione ");
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoFascElencoVers())) {
	    whereClauseStr.append(andClause)
		    .append("f.tiStatoFascElencoVers = :tiStatoFascElencoVers ");
	}
	whereClauseStr.append(andClause).append("f.tiStatoConservazione != 'ANNULLATO' ");
	whereClauseStr.append(andClause).append("f.idUserIamCorrente =:userId");
	queryStr.append(whereClauseStr).append(" ORDER BY f.aaFascicolo, f.cdKeyFascicolo ");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idStrut", idStrut);
	if (filtri.getAaFascicolo() != null) {
	    query.setParameter("aaFascicolo", filtri.getAaFascicolo());
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    query.setParameter("aaFascicolo_da", filtri.getAaFascicoloDa());
	    query.setParameter("aaFascicolo_a", filtri.getAaFascicoloA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    query.setParameter("cdKeyFascicolo", filtri.getCdKeyFascicolo());
	}
	if (StringUtils.isNotBlank(cdKeyFascicoloDa) && StringUtils.isNotBlank(cdKeyFascicoloA)) {
	    query.setParameter("cdKeyFascicolo_da", cdKeyFascicoloDa);
	    query.setParameter("cdKeyFascicolo_a", cdKeyFascicoloA);
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    query.setParameter("idTipoFascicolo", filtri.getNmTipoFascicolo());
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	    query.setParameter("cdXsd", filtri.getCdXsd());
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicolo())) {
	    query.setParameter("dsOggettoFascicolo",
		    "%" + filtri.getDsOggettoFascicolo().toUpperCase() + "%");
	}
	if (filtri.getDtApeFascioloDa() != null && filtri.getDtApeFascioloA() != null) {
	    query.setParameter("dtApeFascicolo_da", filtri.getDtApeFascioloDa());
	    query.setParameter("dtApeFascicolo_a", filtri.getDtApeFascioloA());
	}
	if (filtri.getDtChiuFascioloDa() != null && filtri.getDtChiuFascioloA() != null) {
	    query.setParameter("dtChiuFascicolo_da", filtri.getDtChiuFascioloDa());
	    query.setParameter("dtChiuFascicolo_a", filtri.getDtChiuFascioloA());
	}
	if (StringUtils.isNotBlank(filtri.getCdProcAmmin())) {
	    query.setParameter("cdProcAmmin", "%" + filtri.getCdProcAmmin().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getDsProcAmmin())) {
	    query.setParameter("dsProcAmmin", "%" + filtri.getDsProcAmmin().toUpperCase() + "%");
	}
	if (filtri.getNiAaConservazione() != null) {
	    query.setParameter("niAaConservazione", filtri.getNiAaConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getCdLivelloRiserv())) {
	    query.setParameter("cdLivelloRiserv",
		    "%" + filtri.getCdLivelloRiserv().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getNmSistemaVersante())) {
	    query.setParameter("nmSistemaVersante",
		    "%" + filtri.getNmSistemaVersante().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getNmUserid())) {
	    query.setParameter("nmUserid", "%" + filtri.getNmUserid().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    query.setParameter("cdCompositoVoceTitol",
		    "%" + filtri.getCdCompositoVoceTitol().toUpperCase() + "%");
	}
	if (filtri.getAaFascicoloPadre() != null) {
	    query.setParameter("aaFascicoloPadre", filtri.getAaFascicoloPadre());
	}
	if (filtri.getAaFascicoloPadreDa() != null && filtri.getAaFascicoloPadreA() != null) {
	    query.setParameter("aaFascicoloPadre_da", filtri.getAaFascicoloPadreDa());
	    query.setParameter("aaFascicoloPadre_a", filtri.getAaFascicoloPadreA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadre())) {
	    query.setParameter("cdKeyFascicoloPadre", filtri.getCdKeyFascicoloPadre());
	}
	if (StringUtils.isNotBlank(cdKeyFascicoloPadreDa)
		&& StringUtils.isNotBlank(cdKeyFascicoloPadreA)) {
	    query.setParameter("cdKeyFascicoloPadre_da", cdKeyFascicoloPadreDa);
	    query.setParameter("cdKeyFascicoloPadre_a", cdKeyFascicoloPadreA);
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicoloPadre())) {
	    query.setParameter("dsOggettoFascicoloPadre",
		    "%" + filtri.getDsOggettoFascicoloPadre().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    query.setParameter("cdRegistroKeyUnitaDoc", filtri.getCdRegistroKeyUnitaDoc());
	}
	if (filtri.getAaKeyUnitaDoc() != null) {
	    query.setParameter("aaKeyUnitaDoc", filtri.getAaKeyUnitaDoc());
	}
	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    query.setParameter("aaKeyUnitaDoc_da", filtri.getAaKeyUnitaDocDa());
	    query.setParameter("aaKeyUnitaDoc_a", filtri.getAaKeyUnitaDocA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDoc())) {
	    query.setParameter("cdKeyUnitaDoc", filtri.getCdKeyUnitaDoc());
	}
	if (StringUtils.isNotBlank(cdKeyUnitaDocDa) && StringUtils.isNotBlank(cdKeyUnitaDocA)) {
	    query.setParameter("cdKeyUnitaDoc_da", cdKeyUnitaDocDa);
	    query.setParameter("cdKeyUnitaDoc_a", cdKeyUnitaDocA);
	}

	if (StringUtils.isNotBlank(filtri.getTiConservazione())) {
	    query.setParameter("tiConservazione", filtri.getTiConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrClassif())) {
	    query.setParameter("flForzaContrClassif", filtri.getFlForzaContrClassif());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrNumero())) {
	    query.setParameter("flForzaContrNumero", filtri.getFlForzaContrNumero());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrColleg())) {
	    query.setParameter("flForzaContrColleg", filtri.getFlForzaContrColleg());
	}
	if (StringUtils.isNotBlank(filtri.getCdVersioneWs())) {
	    query.setParameter("cdVersioneWs", filtri.getCdVersioneWs());
	}

	if (filtri.getTsVersFascicoloDa() != null && filtri.getTsVersFascicoloA() != null) {
	    query.setParameter("tsVersFascicolo_da", filtri.getTsVersFascicoloDa());
	    query.setParameter("tsVersFascicolo_a", filtri.getTsVersFascicoloA());
	}
	if (StringUtils.isNotBlank(filtri.getTiEsito())) {
	    query.setParameter("tiEsito", filtri.getTiEsito());
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoConservazione())) {
	    query.setParameter("tiStatoConservazione", filtri.getTiStatoConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoFascElencoVers())) {
	    query.setParameter("tiStatoFascElencoVers", filtri.getTiStatoFascElencoVers());
	}
	if (StringUtils.isNotBlank(filtri.getCdVersioneWs())) {
	    query.setParameter("cdVersioneWs", filtri.getCdVersioneWs());
	}
	query.setParameter("userId", bigDecimalFromLong(userId));
	result = query.getResultList();
	return result;
    }

    /**
     * Ricerca dei fascicoli in stato diverso da ANNULLATO filtrandoli per Struttura di appartenenza
     * e abilitazioni dell'utente corrente
     *
     * @param idStrut id struttura
     * @param userId  id utente
     * @param filtri  filtri di ricerca fascicoli
     *
     * @return Lista di {@link FasVRicFascicoli}
     */
    public List<Object[]> retrieveFascicoli(BigDecimal idStrut, long userId,
	    FiltriRicercaFascicoli filtri) {
	List<Object[]> result = null;
	String whereWord = "WHERE ";
	StringBuilder whereString = new StringBuilder();
	StringBuilder queryStr = new StringBuilder("SELECT DISTINCT f.id_Fascicolo, "
		+ "f.aa_Fascicolo, f.cd_Key_Fascicolo, f.cd_Composito_Voce_Titol, f.nm_Tipo_Fascicolo, f.dt_Ape_Fascicolo, f.dt_Chiu_Fascicolo, "
		+ "f.ts_Vers_Fascicolo, f.ni_Unita_Doc, f.ni_Aa_Conservazione, f.cd_Livello_Riserv, f.fl_Forza_Contr_Classif, "
		+ "f.fl_Forza_Contr_Numero, f.fl_Forza_Contr_Colleg, f.ti_Stato_Fasc_Elenco_Vers, f.ti_Stato_Conservazione, f.fl_upd_annul_unita_doc,"
		+ "f.fl_upd_modif_unita_doc FROM Fm_Ric_Fasc(:idStrut, :userId) f ");

	if (filtri.getAaFascicolo() != null) {
	    whereString.append(whereWord).append("f.aa_Fascicolo = :aaFascicolo ");
	    whereWord = " AND ";
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    whereString.append(whereWord)
		    .append("f.aa_Fascicolo BETWEEN :aaFascicolo_da AND :aaFascicolo_a ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    whereString.append(whereWord).append("f.cd_Key_Fascicolo = :cdKeyFascicolo ");
	    whereWord = " AND ";
	}
	String cdKeyFascicoloDa = null;
	String cdKeyFascicoloA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyFascicoloA())) {
	    cdKeyFascicoloDa = StringPadding.padString(filtri.getCdKeyFascicoloDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyFascicoloA = StringPadding.padString(filtri.getCdKeyFascicoloA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    whereString.append(whereWord).append(
		    "LPAD(f.cd_Key_Fascicolo, 12, '0') BETWEEN :cdKeyFascicolo_da AND :cdKeyFascicolo_a ");
	    whereWord = " AND ";
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    whereString.append(whereWord).append("f.id_Tipo_Fascicolo = :idTipoFascicolo ");
	    whereWord = " AND ";
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    whereString.append(whereWord).append("f.cd_Versione_Xsd = :cdXsd ");
	    whereWord = " AND ";
	    whereString.append(whereWord).append("f.modello_Xsd = :tiModelloXsd ");
	    whereWord = " AND ";
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    whereString.append(whereWord).append("f.cd_Versione_Xsd IS NOT NULL ");
	    whereWord = " AND ";
	    whereString.append(whereWord).append("f.modello_Xsd = :tiModelloXsd ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicolo())) {
	    whereString.append(whereWord)
		    .append("UPPER(f.ds_Oggetto_Fascicolo) LIKE :dsOggettoFascicolo ");
	    whereWord = " AND ";
	}
	if (filtri.getDtApeFascioloDa() != null && filtri.getDtApeFascioloA() != null) {
	    whereString.append(whereWord)
		    .append("f.dt_Ape_Fascicolo BETWEEN :dtApeFascicolo_da AND :dtApeFascicolo_a ");
	    whereWord = " AND ";
	}
	if (filtri.getDtChiuFascioloDa() != null && filtri.getDtChiuFascioloA() != null) {
	    whereString.append(whereWord).append(
		    "f.dt_Chiu_Fascicolo BETWEEN :dtChiuFascicolo_da AND :dtChiuFascicolo_a ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdProcAmmin())) {
	    whereString.append(whereWord).append("UPPER(f.cd_Proc_Ammin) LIKE :cdProcAmmin ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getDsProcAmmin())) {
	    whereString.append(whereWord).append("UPPER(f.ds_Proc_Ammin) LIKE :dsProcAmmin ");
	    whereWord = " AND ";
	}
	if (filtri.getNiAaConservazione() != null) {
	    whereString.append(whereWord).append("f.ni_Aa_Conservazione = :niAaConservazione ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdLivelloRiserv())) {
	    whereString.append(whereWord)
		    .append("UPPER(f.cd_Livello_Riserv) LIKE :cdLivelloRiserv ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getNmSistemaVersante())) {
	    whereString.append(whereWord)
		    .append("UPPER(f.nm_Sistema_Versante) LIKE :nmSistemaVersante ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getNmUserid())) {
	    whereString.append(whereWord).append("UPPER(f.nm_Userid) LIKE :nmUserid ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    whereString.append(whereWord)
		    .append("UPPER(f.cd_Composito_Voce_Titol) LIKE :cdCompositoVoceTitol ");
	    whereWord = " AND ";
	}
	if (filtri.getAaFascicoloPadre() != null) {
	    whereString.append(whereWord).append("f.aa_Fascicolo_Padre = :aaFascicoloPadre ");
	    whereWord = " AND ";
	}
	if (filtri.getAaFascicoloPadreDa() != null && filtri.getAaFascicoloPadreA() != null) {
	    whereString.append(whereWord).append(
		    "f.aa_Fascicolo_Padre BETWEEN :aaFascicoloPadre_da AND :aaFascicoloPadre_a ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadre())) {
	    whereString.append(whereWord)
		    .append("f.cd_Key_Fascicolo_Padre = :cdKeyFascicoloPadre ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlUpdAnnulUnitaDoc())) {
	    whereString.append(whereWord).append("f.fl_upd_annul_unita_doc = :flUpdAnnulUnitaDoc ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlUpdModifUnitaDoc())) {
	    whereString.append(whereWord).append("f.fl_upd_modif_unita_doc = :flUpdModifUnitaDoc ");
	    whereWord = " AND ";
	}
	String cdKeyFascicoloPadreDa = null;
	String cdKeyFascicoloPadreA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadreDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadreA())) {
	    cdKeyFascicoloPadreDa = StringPadding.padString(filtri.getCdKeyFascicoloPadreDa(), "0",
		    12, StringPadding.PADDING_LEFT);
	    cdKeyFascicoloPadreA = StringPadding.padString(filtri.getCdKeyFascicoloPadreA(), "0",
		    12, StringPadding.PADDING_LEFT);
	    whereString.append(whereWord).append(
		    "LPAD( f.cd_Key_Fascicolo_Padre, 12, '0') BETWEEN :cdKeyFascicoloPadre_da AND :cdKeyFascicoloPadre_a ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicoloPadre())) {
	    whereString.append(whereWord)
		    .append("UPPER(f.ds_Oggetto_Fascicolo_Padre) LIKE :dsOggettoFascicoloPadre ");
	    whereWord = " AND ";
	}
	boolean udFilter = false;
	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    whereString.append(whereWord)
		    .append("aud.cd_Registro_Key_Unita_Doc = :cdRegistroKeyUnitaDoc ");
	    udFilter = true;
	    whereWord = " AND ";
	}
	if (filtri.getAaKeyUnitaDoc() != null) {
	    whereString.append(whereWord).append("aud.aa_Key_Unita_Doc = :aaKeyUnitaDoc ");
	    udFilter = true;
	    whereWord = " AND ";
	}
	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    whereString.append(whereWord)
		    .append("aud.aa_Key_Unita_Doc BETWEEN :aaKeyUnitaDoc_da AND :aaKeyUnitaDoc_a ");
	    udFilter = true;
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDoc())) {
	    whereString.append(whereWord).append("aud.cd_Key_Unita_Doc = :cdKeyUnitaDoc ");
	    udFilter = true;
	    whereWord = " AND ";
	}
	String cdKeyUnitaDocDa = null;
	String cdKeyUnitaDocA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDocDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyUnitaDocA())) {
	    cdKeyUnitaDocDa = StringPadding.padString(filtri.getCdKeyFascicoloPadreDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyUnitaDocA = StringPadding.padString(filtri.getCdKeyFascicoloPadreA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    whereString.append(whereWord).append(
		    "LPAD( aud.cd_Key_Unita_Doc, 12, '0') BETWEEN :cdKeyUnitaDoc_da AND :cdKeyUnitaDoc_a ");
	    udFilter = true;
	    whereWord = " AND ";
	}
	String addJoin = " ";
	if (udFilter) {
	    whereString.append(whereWord).append("fud.id_Fascicolo = f.id_Fascicolo ");
	    addJoin = ", Fas_Unita_Doc_Fascicolo fud JOIN Aro_Unita_Doc aud on (fud.id_unita_doc = aud.id_unita_doc) ";
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getTiConservazione())) {
	    whereString.append(whereWord).append("f.ti_Conservazione = :tiConservazione ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrClassif())) {
	    whereString.append(whereWord)
		    .append("f.fl_Forza_Contr_Classif = :flForzaContrClassif ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrNumero())) {
	    whereString.append(whereWord).append("f.fl_Forza_Contr_Numero = :flForzaContrNumero ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrColleg())) {
	    whereString.append(whereWord).append("f.fl_Forza_Contr_Colleg = :flForzaContrColleg ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getCdVersioneWs())) {
	    whereString.append(whereWord).append("f.cd_Versione_Xml_Sip = :cdVersioneWs ");
	    whereWord = " AND ";
	}

	if (filtri.getTsVersFascicoloDa() != null && filtri.getTsVersFascicoloA() != null) {
	    whereString.append(whereWord).append(
		    "f.ts_Vers_Fascicolo BETWEEN :tsVersFascicolo_da AND :tsVersFascicolo_a ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getTiEsito())) {
	    whereString.append(whereWord).append("f.ti_Esito = :tiEsito ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoConservazione())) {
	    whereString.append(whereWord)
		    .append("f.ti_Stato_Conservazione = :tiStatoConservazione ");
	    whereWord = " AND ";
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoFascElencoVers())) {
	    whereString.append(whereWord)
		    .append("f.ti_Stato_Fasc_Elenco_Vers = :tiStatoFascElencoVers ");
	    whereWord = " AND ";
	}
	// whereString.append(whereWord).append("f.tiStatoConservazione != 'ANNULLATO' ");
	// whereWord = " AND ";
	// whereString.append(whereWord).append("f.idUserIamCorrente =:userId");
	// whereWord = " AND ";
	queryStr.append(addJoin).append(whereString)
		.append(" ORDER BY f.aa_Fascicolo, f.cd_Key_Fascicolo ");
	Query query = getEntityManager().createNativeQuery(queryStr.toString());
	query.setParameter("idStrut", idStrut);
	if (filtri.getAaFascicolo() != null) {
	    query.setParameter("aaFascicolo", filtri.getAaFascicolo());
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    query.setParameter("aaFascicolo_da", filtri.getAaFascicoloDa());
	    query.setParameter("aaFascicolo_a", filtri.getAaFascicoloA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    query.setParameter("cdKeyFascicolo", filtri.getCdKeyFascicolo());
	}
	if (StringUtils.isNotBlank(cdKeyFascicoloDa) && StringUtils.isNotBlank(cdKeyFascicoloA)) {
	    query.setParameter("cdKeyFascicolo_da", cdKeyFascicoloDa);
	    query.setParameter("cdKeyFascicolo_a", cdKeyFascicoloA);
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    query.setParameter("idTipoFascicolo", filtri.getNmTipoFascicolo());
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	    query.setParameter("cdXsd", filtri.getCdXsd());
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicolo())) {
	    query.setParameter("dsOggettoFascicolo",
		    "%" + filtri.getDsOggettoFascicolo().toUpperCase() + "%");
	}
	if (filtri.getDtApeFascioloDa() != null && filtri.getDtApeFascioloA() != null) {
	    query.setParameter("dtApeFascicolo_da", filtri.getDtApeFascioloDa());
	    query.setParameter("dtApeFascicolo_a", filtri.getDtApeFascioloA());
	}
	if (filtri.getDtChiuFascioloDa() != null && filtri.getDtChiuFascioloA() != null) {
	    query.setParameter("dtChiuFascicolo_da", filtri.getDtChiuFascioloDa());
	    query.setParameter("dtChiuFascicolo_a", filtri.getDtChiuFascioloA());
	}
	if (StringUtils.isNotBlank(filtri.getCdProcAmmin())) {
	    query.setParameter("cdProcAmmin", "%" + filtri.getCdProcAmmin().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getDsProcAmmin())) {
	    query.setParameter("dsProcAmmin", "%" + filtri.getDsProcAmmin().toUpperCase() + "%");
	}
	if (filtri.getNiAaConservazione() != null) {
	    query.setParameter("niAaConservazione", filtri.getNiAaConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getCdLivelloRiserv())) {
	    query.setParameter("cdLivelloRiserv",
		    "%" + filtri.getCdLivelloRiserv().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getNmSistemaVersante())) {
	    query.setParameter("nmSistemaVersante",
		    "%" + filtri.getNmSistemaVersante().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getNmUserid())) {
	    query.setParameter("nmUserid", "%" + filtri.getNmUserid().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    query.setParameter("cdCompositoVoceTitol",
		    "%" + filtri.getCdCompositoVoceTitol().toUpperCase() + "%");
	}
	if (filtri.getAaFascicoloPadre() != null) {
	    query.setParameter("aaFascicoloPadre", filtri.getAaFascicoloPadre());
	}
	if (filtri.getAaFascicoloPadreDa() != null && filtri.getAaFascicoloPadreA() != null) {
	    query.setParameter("aaFascicoloPadre_da", filtri.getAaFascicoloPadreDa());
	    query.setParameter("aaFascicoloPadre_a", filtri.getAaFascicoloPadreA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloPadre())) {
	    query.setParameter("cdKeyFascicoloPadre", filtri.getCdKeyFascicoloPadre());
	}
	if (StringUtils.isNotBlank(cdKeyFascicoloPadreDa)
		&& StringUtils.isNotBlank(cdKeyFascicoloPadreA)) {
	    query.setParameter("cdKeyFascicoloPadre_da", cdKeyFascicoloPadreDa);
	    query.setParameter("cdKeyFascicoloPadre_a", cdKeyFascicoloPadreA);
	}
	if (StringUtils.isNotBlank(filtri.getDsOggettoFascicoloPadre())) {
	    query.setParameter("dsOggettoFascicoloPadre",
		    "%" + filtri.getDsOggettoFascicoloPadre().toUpperCase() + "%");
	}
	if (StringUtils.isNotBlank(filtri.getCdRegistroKeyUnitaDoc())) {
	    query.setParameter("cdRegistroKeyUnitaDoc", filtri.getCdRegistroKeyUnitaDoc());
	}
	if (filtri.getAaKeyUnitaDoc() != null) {
	    query.setParameter("aaKeyUnitaDoc", filtri.getAaKeyUnitaDoc());
	}
	if (filtri.getAaKeyUnitaDocDa() != null && filtri.getAaKeyUnitaDocA() != null) {
	    query.setParameter("aaKeyUnitaDoc_da", filtri.getAaKeyUnitaDocDa());
	    query.setParameter("aaKeyUnitaDoc_a", filtri.getAaKeyUnitaDocA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyUnitaDoc())) {
	    query.setParameter("cdKeyUnitaDoc", filtri.getCdKeyUnitaDoc());
	}
	if (StringUtils.isNotBlank(cdKeyUnitaDocDa) && StringUtils.isNotBlank(cdKeyUnitaDocA)) {
	    query.setParameter("cdKeyUnitaDoc_da", cdKeyUnitaDocDa);
	    query.setParameter("cdKeyUnitaDoc_a", cdKeyUnitaDocA);
	}

	if (StringUtils.isNotBlank(filtri.getTiConservazione())) {
	    query.setParameter("tiConservazione", filtri.getTiConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrClassif())) {
	    query.setParameter("flForzaContrClassif", filtri.getFlForzaContrClassif());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrNumero())) {
	    query.setParameter("flForzaContrNumero", filtri.getFlForzaContrNumero());
	}
	if (StringUtils.isNotBlank(filtri.getFlForzaContrColleg())) {
	    query.setParameter("flForzaContrColleg", filtri.getFlForzaContrColleg());
	}
	if (StringUtils.isNotBlank(filtri.getFlUpdAnnulUnitaDoc())) {
	    query.setParameter("flUpdAnnulUnitaDoc", filtri.getFlUpdAnnulUnitaDoc());
	}
	if (StringUtils.isNotBlank(filtri.getFlUpdModifUnitaDoc())) {
	    query.setParameter("flUpdModifUnitaDoc", filtri.getFlUpdModifUnitaDoc());
	}
	if (filtri.getTsVersFascicoloDa() != null && filtri.getTsVersFascicoloA() != null) {
	    query.setParameter("tsVersFascicolo_da", filtri.getTsVersFascicoloDa());
	    query.setParameter("tsVersFascicolo_a", filtri.getTsVersFascicoloA());
	}
	if (StringUtils.isNotBlank(filtri.getTiEsito())) {
	    query.setParameter("tiEsito", filtri.getTiEsito());
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoConservazione())) {
	    query.setParameter("tiStatoConservazione", filtri.getTiStatoConservazione());
	}
	if (StringUtils.isNotBlank(filtri.getTiStatoFascElencoVers())) {
	    query.setParameter("tiStatoFascElencoVers", filtri.getTiStatoFascElencoVers());
	}
	if (StringUtils.isNotBlank(filtri.getCdVersioneWs())) {
	    query.setParameter("cdVersioneWs", filtri.getCdVersioneWs());
	}
	query.setParameter("userId", bigDecimalFromLong(userId));
	result = query.getResultList();
	return result;
    }

    /**
     * Ricerca dei fascicoli annullati filtrandoli per Struttura di appartenenza, abilitazioni
     * dell'utente corrente e stato di conservazione ANNULLATO
     *
     * @param filtri  filtro ricerca fascicoli
     * @param idStrut id struttura
     * @param userId  id utente
     *
     * @return lista elementi di tipo FasVRicFascicoli
     */

    public List<FasVRicFascicoli> retrieveFascicoliAnnullati(FiltriRicercaFascicoli filtri,
	    BigDecimal idStrut, long userId) {
	List<FasVRicFascicoli> result = null;
	String andClause = "AND ";
	StringBuilder queryStr = new StringBuilder(
		"SELECT DISTINCT new it.eng.parer.viewEntity.FasVRicFascicoli(f.idFascicolo, "
			+ "f.aaFascicolo, f.cdKeyFascicolo, f.cdCompositoVoceTitol, f.nmTipoFascicolo, f.dtApeFascicolo, f.dtChiuFascicolo, "
			+ "f.tsVersFascicolo, f.niUnitaDoc, f.niAaConservazione, f.cdLivelloRiserv, f.flForzaContrClassif, "
			+ "f.flForzaContrNumero, f.flForzaContrColleg, f.tiStatoFascElencoVers, f.tiStatoConservazione) FROM FasVRicFascicoli f ");
	StringBuilder whereClauseStr = new StringBuilder("WHERE f.idStrut = :idStrut ");

	if (filtri.getAaFascicolo() != null) {
	    whereClauseStr.append(andClause).append("f.aaFascicolo = :aaFascicolo ");
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    whereClauseStr.append(andClause)
		    .append("f.aaFascicolo BETWEEN :aaFascicolo_da AND :aaFascicolo_a ");
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    whereClauseStr.append(andClause).append("f.cdKeyFascicolo = :cdKeyFascicolo ");
	}
	String cdKeyFascicoloDa = null;
	String cdKeyFascicoloA = null;
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicoloDa())
		&& StringUtils.isNotBlank(filtri.getCdKeyFascicoloA())) {
	    cdKeyFascicoloDa = StringPadding.padString(filtri.getCdKeyFascicoloDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    cdKeyFascicoloA = StringPadding.padString(filtri.getCdKeyFascicoloA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    whereClauseStr.append(andClause).append(
		    "LPAD(f.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicolo_da AND :cdKeyFascicolo_a ");
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    whereClauseStr.append(andClause).append("f.idTipoFascicolo = :idTipoFascicolo ");
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    whereClauseStr.append(andClause)
		    .append("UPPER(f.cdCompositoVoceTitol) LIKE :cdCompositoVoceTitol ");
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    whereClauseStr.append(andClause).append("f.cdVersioneXsd = :cdXsd ");
	    whereClauseStr.append(andClause).append("f.tiModelloXsd = :tiModelloXsd ");
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    whereClauseStr.append(andClause).append("f.cdVersioneXsd IS NOT NULL ");
	    whereClauseStr.append(andClause).append("f.tiModelloXsd = :tiModelloXsd ");
	}
	whereClauseStr.append(andClause).append("f.tiStatoConservazione = 'ANNULLATO' ");
	whereClauseStr.append(andClause).append("f.idUserIamCorrente = :userId");
	queryStr.append(whereClauseStr).append(" ORDER BY f.aaFascicolo, f.cdKeyFascicolo ");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idStrut", idStrut);
	if (filtri.getAaFascicolo() != null) {
	    query.setParameter("aaFascicolo", filtri.getAaFascicolo());
	}
	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    query.setParameter("aaFascicolo_da", filtri.getAaFascicoloDa());
	    query.setParameter("aaFascicolo_a", filtri.getAaFascicoloA());
	}
	if (StringUtils.isNotBlank(filtri.getCdKeyFascicolo())) {
	    query.setParameter("cdKeyFascicolo", filtri.getCdKeyFascicolo());
	}
	if (StringUtils.isNotBlank(cdKeyFascicoloDa) && StringUtils.isNotBlank(cdKeyFascicoloA)) {
	    query.setParameter("cdKeyFascicolo_da", cdKeyFascicoloDa);
	    query.setParameter("cdKeyFascicolo_a", cdKeyFascicoloA);
	}
	if (filtri.getNmTipoFascicolo() != null) {
	    query.setParameter("idTipoFascicolo", filtri.getNmTipoFascicolo());
	}
	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitol())) {
	    query.setParameter("cdCompositoVoceTitol",
		    "%" + filtri.getCdCompositoVoceTitol().toUpperCase() + "%");
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() != null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	    query.setParameter("cdXsd", filtri.getCdXsd());
	}
	if ((filtri.getTiModelloXsd() != null && !filtri.getTiModelloXsd().isEmpty())
		&& filtri.getCdXsd() == null) {
	    query.setParameter("tiModelloXsd", filtri.getTiModelloXsd());
	}
	query.setParameter("userId", bigDecimalFromLong(userId));
	result = query.getResultList();
	return result;
    }

    public CriteriaQuery retrieveVLisFascKoCriteriaQuery(BigDecimal idUser, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
	    BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
	    String rangeNumeroA, String statoSessione, String cdClasseErr, String cdErr) {
	try {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery cq = null;
	    Root entity = null;
	    if ((cdErr == null || cdErr.equals(""))
		    && (cdClasseErr == null || cdClasseErr.equals(""))) {
		cq = cb.createQuery(MonVLisFascKo.class);
		entity = cq.from(MonVLisFascKo.class);
	    } else {
		cq = cb.createQuery(MonVLisFascKoByErr.class);
		entity = cq.from(MonVLisFascKoByErr.class);
	    }
	    cq.select(entity);
	    cq.distinct(true);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsIniFirstSes")));
	    List<Predicate> condizioni = new ArrayList<>();
	    condizioni.add(cb.equal(entity.get("idUserIam"), idUser));
	    condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
	    if (idEnte != null) {
		condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
	    }
	    if (idStrut != null) {
		condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
	    }
	    if (idTipoFascicolo != null) {
		condizioni.add(cb.equal(entity.get("idTipoFascicolo"), idTipoFascicolo));
	    }
	    if (dateValidate != null) {
		LocalDateTime dataDa = dateValidate[0].toInstant().atZone(ZoneId.systemDefault())
			.toLocalDate().atTime(0, 0, 0, 0);
		LocalDateTime dataA = dateValidate[1].toInstant().atZone(ZoneId.systemDefault())
			.toLocalDate().atTime(0, 0, 0, 0);
		condizioni.add(
			cb.between(cb.function("TRUNC", Date.class, entity.get("tsIniLastSes")),
				cb.literal(Timestamp.valueOf(dataDa)),
				cb.literal(Timestamp.valueOf(dataA))));
	    }
	    if (statoSessione != null && (!statoSessione.equals(""))) {
		condizioni.add(cb.equal(entity.get("tiStatoFascicoloKo"), statoSessione));
	    }
	    if (cdErr != null && (!cdErr.equals(""))) {
		condizioni.add(cb.equal(entity.get("cdErrSacer"), cdErr));
	    }
	    if (cdClasseErr != null && (!cdClasseErr.equals(""))) {
		condizioni.add(cb.equal(entity.get("cdClasseErrSacer"), cdClasseErr));
	    }
	    if (rangeAnnoA != null) {
		condizioni.add(cb.between(entity.get("aaFascicolo"), rangeAnnoDa, rangeAnnoA));
	    } else if (rangeAnnoDa != null) {
		condizioni.add(cb.equal(entity.get("aaFascicolo"), rangeAnnoDa));
	    }
	    if (rangeNumeroA != null && (!rangeNumeroA.equals(""))) {
		condizioni
			.add(cb.between(entity.get("cdKeyFascicolo"), rangeNumeroA, rangeNumeroA));
	    } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
		condizioni.add(cb.equal(entity.get("cdKeyFascicolo"), rangeNumeroDa));
	    }
	    cq.where(condizioni.toArray(new Predicate[] {}));
	    return cq;
	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione Della lista fascicoli derivanti da versamenti falliti",
		    ex);
	    throw ex;
	}
    }

    public CriteriaQuery retrieveVLisFascCriteriaQuery(BigDecimal idUser, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
	    BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa,
	    String rangeNumeroA, String statoIndiceAip, Set<String> statiConservazione,
	    String flSesFascicoloKo) {
	try {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    Root entity = null;
	    CriteriaQuery cq = null;
	    if (statoIndiceAip != null && (statoIndiceAip
		    .equals(ElencoEnums.StatoGenerazioneIndiceAip.IN_ATTESA_SCHED.name())
		    || statoIndiceAip.equals(
			    ElencoEnums.StatoGenerazioneIndiceAip.NON_SELEZ_SCHED.name()))) {
		cq = cb.createQuery(MonVLisFascDaElab.class);
		entity = cq.from(MonVLisFascDaElab.class);
	    } else {
		cq = cb.createQuery(MonVLisFasc.class);
		entity = cq.from(MonVLisFasc.class);
	    }
	    cq.select(entity);
	    // ORDER BY
	    cq.orderBy(cb.desc(entity.get("tsVersFascicolo")));
	    List<Predicate> condizioni = new ArrayList<>();
	    condizioni.add(cb.equal(entity.get("idUserIam"), idUser));
	    condizioni.add(cb.equal(entity.get("idAmbiente"), idAmbiente));
	    if (idEnte != null) {
		condizioni.add(cb.equal(entity.get("idEnte"), idEnte));
	    }
	    if (idStrut != null) {
		condizioni.add(cb.equal(entity.get("idStrut"), idStrut));
	    }
	    if (idTipoFascicolo != null) {
		condizioni.add(cb.equal(entity.get("idTipoFascicolo"), idTipoFascicolo));
	    }
	    if (dateValidate != null) {
		LocalDateTime dataDa = dateValidate[0].toInstant().atZone(ZoneId.systemDefault())
			.toLocalDate().atTime(0, 0, 0, 0);
		LocalDateTime dataA = dateValidate[1].toInstant().atZone(ZoneId.systemDefault())
			.toLocalDate().atTime(0, 0, 0, 0);
		condizioni.add(
			cb.between(cb.function("TRUNC", Date.class, entity.get("tsVersFascicolo")),
				cb.literal(Timestamp.valueOf(dataDa)),
				cb.literal(Timestamp.valueOf(dataA))));
	    }
	    if (statoIndiceAip != null && !statoIndiceAip.equals("")) {
		condizioni.add(cb.equal(entity.get("tiStatoFascElencoVers"), statoIndiceAip));
	    }
	    if (flSesFascicoloKo != null && !flSesFascicoloKo.equals("")) {
		condizioni.add(cb.equal(entity.get("flSesFascicoloKo"), flSesFascicoloKo));
	    }
	    if (rangeAnnoA != null) {
		condizioni.add(cb.between(entity.get("aaFascicolo"), rangeAnnoDa, rangeAnnoA));
	    } else if (rangeAnnoDa != null) {
		condizioni.add(cb.equal(entity.get("aaFascicolo"), rangeAnnoDa));
	    }
	    if (rangeNumeroA != null && !rangeNumeroA.equals("")) {
		condizioni
			.add(cb.between(entity.get("cdKeyFascicolo"), rangeNumeroA, rangeNumeroA));
	    } else if (rangeNumeroDa != null && !rangeNumeroDa.equals("")) {
		condizioni.add(cb.equal(entity.get("cdKeyFascicolo"), rangeNumeroDa));
	    }
	    if (statiConservazione != null && !statiConservazione.isEmpty()) {
		condizioni.add(cb.and(entity.get("tiStatoConservazione").in(statiConservazione)));
	    }
	    cq.where(condizioni.toArray(new Predicate[] {}));
	    return cq;

	} catch (RuntimeException ex) {
	    logger.error(
		    "Errore nell'estrazione Della lista fascicoli derivanti da versamenti falliti",
		    ex);
	    throw ex;
	}
    }

    public void deleteDecSelCriterioRaggrFascNotInVoceTitolo(List<BigDecimal> voceTitolList,
	    DecCriterioRaggrFasc decCriterioRaggrFasc) {
	Query q = getEntityManager()
		.createQuery("DELETE FROM DecSelCriterioRaggrFasc u " + "WHERE u.tiSel = :filtro "
			+ "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit "
			+ "and u.decVoceTitol.idVoceTitol NOT IN (:voci)");
	q.setParameter("voci", longListFrom(voceTitolList));
	q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
	q.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	q.executeUpdate();
	getEntityManager().flush();
    }

    public List<DecSelCriterioRaggrFasc> getDecSelCriterioRaggrFascList(
	    DecCriterioRaggrFasc decCriterioRaggrFasc, DecVoceTitol voce) {
	Query query = getEntityManager().createQuery("SELECT u FROM DecSelCriterioRaggrFasc u "
		+ "WHERE u.decVoceTitol = :voce and u.tiSel = :filtro "
		+ "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
	query.setParameter("voce", voce);
	query.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
	query.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	return query.getResultList();
    }

    public List<DecVoceTitol> getDecVoceTitolList(List<BigDecimal> voceTitolList) {
	StringBuilder queryStr = new StringBuilder("SELECT u FROM DecVoceTitol u ");
	queryStr.append("WHERE u.idVoceTitol in (:idvocetitol)");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idvocetitol", longListFrom(voceTitolList));
	return query.getResultList();
    }

    public void deleteDecSelCriterioRaggrFascFiltroVoceTitolo(
	    DecCriterioRaggrFasc decCriterioRaggrFasc) {
	Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
		+ "WHERE u.tiSel = :filtro and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
	q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.VOCE_TITOL.name());
	q.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	q.executeUpdate();
	getEntityManager().flush();
    }

    public void deleteDecSelCriterioRaggrFascFiltroTipoFascicolo(
	    DecCriterioRaggrFasc decCriterioRaggrFasc) {
	Query q = getEntityManager().createQuery("DELETE FROM DecSelCriterioRaggrFasc u "
		+ "WHERE u.tiSel = :filtro and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
	q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
	q.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	q.executeUpdate();
	getEntityManager().flush();
    }

    public void deleteDecSelCriterioRaggrFascNotInTipoFascicolo(
	    DecCriterioRaggrFasc decCriterioRaggrFasc, List<BigDecimal> tipoFascicoloList) {
	Query q = getEntityManager()
		.createQuery("DELETE FROM DecSelCriterioRaggrFasc u " + "WHERE u.tiSel = :filtro "
			+ "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit "
			+ "and u.decTipoFascicolo.idTipoFascicolo NOT IN (:tipi)");
	q.setParameter("tipi", longListFrom(tipoFascicoloList));
	q.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
	q.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	q.executeUpdate();
	getEntityManager().flush();
    }

    public List<DecSelCriterioRaggrFasc> getDecSelCriterioRaggrFascList(
	    DecCriterioRaggrFasc decCriterioRaggrFasc, DecTipoFascicolo tipo) {
	Query query = getEntityManager().createQuery("SELECT u FROM DecSelCriterioRaggrFasc u "
		+ "WHERE u.decTipoFascicolo = :tipo and u.tiSel = :filtro "
		+ "and u.decCriterioRaggrFasc.idCriterioRaggrFasc = :crit");
	query.setParameter("tipo", tipo);
	query.setParameter("filtro", ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
	query.setParameter("crit", decCriterioRaggrFasc.getIdCriterioRaggrFasc());
	return query.getResultList();
    }

    public List<DecTipoFascicolo> getDecTipoFascicoloList(StringBuilder queryStr,
	    List<BigDecimal> asList) {
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idtipofascicolo", longListFrom(asList));
	return query.getResultList();
    }

    public OrgStrut getOrgStrut(BigDecimal idStruttura) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT u FROM OrgStrut u WHERE u.idStrut = :idstrut");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idstrut", longFromBigDecimal(idStruttura));
	return (OrgStrut) query.getSingleResult();
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFascByStrutAndCriterio(BigDecimal idStruttura,
	    String nmCriterioRaggr) {
	DecCriterioRaggrFasc decCriterioRaggrFasc;
	String queryStr = "SELECT u FROM DecCriterioRaggrFasc u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idstrut", longFromBigDecimal(idStruttura));
	query.setParameter("nomecrit", nmCriterioRaggr);
	decCriterioRaggrFasc = (DecCriterioRaggrFasc) query.getSingleResult();
	return decCriterioRaggrFasc;
    }

    public FasFascicoloTableBean getListaFasFascicoloElvViewBean(BigDecimal idElencoVersFasc,
	    DecTipoFascicoloTableBean tmpTableBeanTipoFasc, FiltriElenchiVersFascicoli filtri) {
	String whereWord = "AND ";
	StringBuilder queryStr = new StringBuilder(
		"SELECT f FROM FasFascicolo f WHERE f.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ");

	// Inserimento nella query dei tipi fascicolo abilitati
	Set<BigDecimal> idTipoFascicoloSet = new HashSet<>();
	for (DecTipoFascicoloRowBean row : tmpTableBeanTipoFasc) {
	    idTipoFascicoloSet.add(row.getIdTipoFascicolo());
	}
	if (idTipoFascicoloSet.isEmpty()) {
	    idTipoFascicoloSet.add(new BigDecimal("0"));
	}
	queryStr.append(whereWord)
		.append("f.decTipoFascicolo.idTipoFascicolo IN (:idtipofascicoloin) ");

	// Inserimento nella query del filtro CHIAVE FASCICOLO

	if (filtri.getIdTipoFascicolo() != null) {
	    queryStr.append(whereWord)
		    .append("f.decTipoFascicolo.idTipoFascicolo = :idtipofascin ");
	    whereWord = "AND ";
	}

	if (filtri.getAaFascicolo() != null) {
	    queryStr.append(whereWord).append("f.aaFascicolo = :annoin ");
	    whereWord = "AND ";
	}

	if (filtri.getCdKeyFascicolo() != null) {
	    queryStr.append(whereWord).append("f.cdKeyFascicolo = :codicein ");
	    whereWord = "AND ";
	}

	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    queryStr.append(whereWord).append("(f.aaFascicolo BETWEEN :annoin_da AND :annoin_a) ");
	    whereWord = "AND ";
	}
	String codiceRangeDa = filtri.getCdKeyFascicoloDa();
	String codiceRangeA = filtri.getCdKeyFascicoloA();
	if (codiceRangeDa != null && codiceRangeA != null) {
	    codiceRangeDa = StringPadding.padString(filtri.getCdKeyFascicoloDa(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    codiceRangeA = StringPadding.padString(filtri.getCdKeyFascicoloA(), "0", 12,
		    StringPadding.PADDING_LEFT);
	    queryStr.append(whereWord).append(
		    "LPAD( f.cdKeyFascicolo, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
	    whereWord = "AND ";
	}

	Date dataValVersamentoDa = null;
	Date dataValVersamentoA = null;
	Date dataValApeFascicoloDa = null;
	Date dataValApeFascicoloA = null;
	Date dataValChiuFascicoloDa = null;
	Date dataValChiuFascicoloA = null;

	// Inserimento nella query del filtro DATA VERSAMENTO FASCICOLO DA - A
	if (filtri.getTsIniSesDa() != null) {
	    dataValVersamentoDa = new Date(filtri.getTsIniSesDa().getTime());
	    if (filtri.getTsIniSesA() != null) {
		dataValVersamentoA = new Date(filtri.getTsIniSesA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValVersamentoA);
		calendar.add(Calendar.DATE, 1);
		dataValVersamentoA = calendar.getTime();
	    } else {
		dataValVersamentoA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValVersamentoA);
		calendar.add(Calendar.DATE, 1);
		dataValVersamentoA = calendar.getTime();
	    }
	}

	if ((dataValVersamentoDa != null) && (dataValVersamentoA != null)) {
	    queryStr.append(whereWord).append("(f.tsIniSes BETWEEN :tsIniSes_da AND :tsIniSes_a) ");
	    whereWord = "AND ";
	}

	// Inserimento nella query del filtro DATA APERTURA FASCICOLO DA - A
	if (filtri.getDtApeFascicoloDa() != null) {
	    dataValApeFascicoloDa = new Date(filtri.getDtApeFascicoloDa().getTime());
	    if (filtri.getDtApeFascicoloA() != null) {
		dataValApeFascicoloA = new Date(filtri.getDtApeFascicoloA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValApeFascicoloA);
		calendar.add(Calendar.DATE, 1);
		dataValApeFascicoloA = calendar.getTime();
	    } else {
		dataValApeFascicoloA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValApeFascicoloA);
		calendar.add(Calendar.DATE, 1);
		dataValApeFascicoloA = calendar.getTime();
	    }
	}

	if ((dataValApeFascicoloDa != null) && (dataValApeFascicoloA != null)) {
	    queryStr.append(whereWord)
		    .append("(f.dtApeFascicolo BETWEEN :dtApeFascicolo_da AND :dtApeFascicolo_a) ");
	    whereWord = "AND ";
	}

	// Inserimento nella query del filtro DATA CHIUSURA FASCICOLO DA - A
	if (filtri.getDtChiuFascicoloDa() != null) {
	    dataValChiuFascicoloDa = new Date(filtri.getDtChiuFascicoloDa().getTime());
	    if (filtri.getDtChiuFascicoloA() != null) {
		dataValChiuFascicoloA = new Date(filtri.getDtChiuFascicoloA().getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValChiuFascicoloA);
		calendar.add(Calendar.DATE, 1);
		dataValChiuFascicoloA = calendar.getTime();
	    } else {
		dataValChiuFascicoloA = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataValChiuFascicoloA);
		calendar.add(Calendar.DATE, 1);
		dataValChiuFascicoloA = calendar.getTime();
	    }
	}

	if ((dataValChiuFascicoloDa != null) && (dataValChiuFascicoloA != null)) {
	    queryStr.append(whereWord).append(
		    "(f.dtChiuFascicolo BETWEEN :dtChiuFascicolo_da AND :dtChiuFascicolo_a) ");
	    whereWord = "AND ";
	}

	if (filtri.getCdCompositoVoceTitolo() != null) {
	    queryStr.append(whereWord)
		    .append("f.decVoceTitol.cdCompositoVoceTitol = :cdcompositovocetitolin ");
	}

	queryStr.append("ORDER BY f.dsOggettoFascicolo ");

	// CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idElencoVersFasc", longFromBigDecimal(idElencoVersFasc));

	query.setParameter("idtipofascicoloin", longListFrom(idTipoFascicoloSet));

	if (filtri.getIdTipoFascicolo() != null) {
	    query.setParameter("idtipofascin", filtri.getIdTipoFascicolo().longValue());
	}

	if (filtri.getAaFascicolo() != null) {
	    query.setParameter("annoin", filtri.getAaFascicolo());
	}

	if (filtri.getCdKeyFascicolo() != null) {
	    query.setParameter("codicein", filtri.getCdKeyFascicolo());
	}

	if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
	    query.setParameter("annoin_da", filtri.getAaFascicoloDa());
	    query.setParameter("annoin_a", filtri.getAaFascicoloA());
	}

	if (codiceRangeDa != null && codiceRangeA != null) {
	    query.setParameter("codicein_da", codiceRangeDa);
	    query.setParameter("codicein_a", codiceRangeA);
	}

	if (dataValVersamentoDa != null && dataValVersamentoA != null) {
	    query.setParameter("tsIniSes_da", dataValVersamentoDa, TemporalType.DATE);
	    query.setParameter("tsIniSes_a", dataValVersamentoA, TemporalType.DATE);
	}

	if (dataValApeFascicoloDa != null && dataValApeFascicoloA != null) {
	    query.setParameter("dtApeFascicolo_da", dataValApeFascicoloDa, TemporalType.DATE);
	    query.setParameter("dtApeFascicolo_a", dataValApeFascicoloA, TemporalType.DATE);
	}

	if (dataValChiuFascicoloDa != null && dataValChiuFascicoloA != null) {
	    query.setParameter("dtChiuFascicolo_da", dataValChiuFascicoloDa, TemporalType.DATE);
	    query.setParameter("dtChiuFascicolo_a", dataValChiuFascicoloA, TemporalType.DATE);
	}

	if (StringUtils.isNotBlank(filtri.getCdCompositoVoceTitolo())) {
	    query.setParameter("cdcompositovocetitolin",
		    filtri.getCdCompositoVoceTitolo().toUpperCase());
	}

	// ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
	List<FasFascicolo> listaFascicoli = query.getResultList();

	FasFascicoloTableBean fascicoliTableBean = new FasFascicoloTableBean();
	try {
	    if (listaFascicoli != null && !listaFascicoli.isEmpty()) {
		fascicoliTableBean = (FasFascicoloTableBean) Transform
			.entities2TableBean(listaFascicoli);
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}

	// setta il campo relativo alla checkbox select_fasc non ceccato
	for (int i = 0; i < fascicoliTableBean.size(); i++) {
	    FasFascicoloRowBean row = fascicoliTableBean.getRow(i);
	    row.setString("select_fasc", "0");
	    DecTipoFascicolo tipoFascicolo = findById(DecTipoFascicolo.class,
		    row.getIdTipoFascicolo());
	    row.setString("nm_tipo_fascicolo", tipoFascicolo.getNmTipoFascicolo());
	}

	return fascicoliTableBean;
    }

    public List<DecCriterioRaggrFasc> retrieveCriteriRaggrFascicoli(boolean filterValid,
	    FiltriCriteriRaggrFascicoliPlain filtriCriteriRaggrFascicoliPlain) {
	List<DecCriterioRaggrFasc> result = null;

	try {

	    StringBuilder queryStr = new StringBuilder(
		    "SELECT DISTINCT crf FROM DecCriterioRaggrFasc crf LEFT JOIN crf.decSelCriterioRaggrFascicoli scrf ");
	    String whereWord = "WHERE ";
	    /* Inserimento nella query del filtro ID_AMBIENTE */
	    BigDecimal idAmbiente = filtriCriteriRaggrFascicoliPlain.getIdAmbiente();
	    if (idAmbiente != null) {
		queryStr.append(whereWord)
			.append("crf.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
		whereWord = "AND ";
	    }
	    /* Inserimento nella query del filtro ID_ENTE */
	    BigDecimal idEnte = filtriCriteriRaggrFascicoliPlain.getIdEnte();
	    if (idEnte != null) {
		queryStr.append(whereWord).append("crf.orgStrut.orgEnte.idEnte = :idEnte ");
		whereWord = "AND ";
	    }
	    /* Inserimento nella query del filtro ID_STRUT */
	    BigDecimal idStrut = filtriCriteriRaggrFascicoliPlain.getIdStrut();
	    if (idStrut != null) {
		queryStr.append(whereWord).append("crf.orgStrut.idStrut = :idStrut ");
		whereWord = "AND ";
	    }
	    String nmCriterioRaggr = filtriCriteriRaggrFascicoliPlain.getNmCriterioRaggr();
	    if (nmCriterioRaggr != null) {
		queryStr.append(whereWord)
			.append("UPPER(crf.nmCriterioRaggr) LIKE :nmCriterioRaggr ");
		whereWord = "AND ";
	    }
	    String flCriterioRaggrStandard = filtriCriteriRaggrFascicoliPlain
		    .getFlCriterioRaggrStandard();
	    if (flCriterioRaggrStandard != null) {
		queryStr.append(whereWord)
			.append("crf.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
		whereWord = "AND ";
	    }
	    BigDecimal idTipoFascicolo = filtriCriteriRaggrFascicoliPlain.getIdTipoFascicolo();
	    if (idTipoFascicolo != null) {
		queryStr.append(whereWord)
			.append("scrf.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
		whereWord = "AND ";
	    }
	    String cdCompositoVoceTitol = filtriCriteriRaggrFascicoliPlain
		    .getCdCompositoVoceTitol();
	    if (cdCompositoVoceTitol != null) {
		queryStr.append(whereWord).append(
			"scrf.decVoceTitol.cdCompositoVoceTitol LIKE :cdCompositoVoceTitol ");
		whereWord = "AND ";
	    }
	    BigDecimal aaFascicolo = filtriCriteriRaggrFascicoliPlain.getAaFascicolo();
	    if (aaFascicolo != null) {
		queryStr.append(whereWord).append(
			"(crf.aaFascicolo = :aaFascicolo OR (crf.aaFascicoloDa <= :aaFascicolo AND crf.aaFascicoloA >= :aaFascicolo)) ");
		whereWord = "AND ";
	    }
	    String criterioAttivo = filtriCriteriRaggrFascicoliPlain.getCriterioAttivo();
	    if (criterioAttivo != null) {
		if (criterioAttivo.equals("1")) {
		    queryStr.append(whereWord)
			    .append("crf.dtSoppres >= :data AND crf.dtIstituz <= :data ");
		} else {
		    queryStr.append(whereWord)
			    .append("crf.dtSoppres < :data OR crf.dtIstituz > :data ");
		}
		whereWord = "AND ";
	    }
	    if (filterValid) {
		queryStr.append(whereWord)
			.append("crf.dtIstituz <= :filterDate AND crf.dtSoppres >= :filterDate ");
	    }

	    queryStr.append("ORDER BY crf.nmCriterioRaggr ");

	    Query query = getEntityManager().createQuery(queryStr.toString());

	    if (idAmbiente != null) {
		query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
	    }
	    if (idEnte != null) {
		query.setParameter("idEnte", longFromBigDecimal(idEnte));
	    }
	    if (idStrut != null) {
		query.setParameter("idStrut", longFromBigDecimal(idStrut));
	    }
	    if (nmCriterioRaggr != null) {
		query.setParameter("nmCriterioRaggr", "%" + nmCriterioRaggr.toUpperCase() + "%");
	    }
	    if (flCriterioRaggrStandard != null) {
		query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
	    }
	    if (aaFascicolo != null) {
		query.setParameter("aaFascicolo", aaFascicolo);
	    }
	    if (idTipoFascicolo != null) {
		query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
	    }
	    if (cdCompositoVoceTitol != null) {
		query.setParameter("cdCompositoVoceTitol",
			"%" + cdCompositoVoceTitol.toUpperCase() + "%");
	    }
	    if (criterioAttivo != null) {
		Calendar dataOdierna = Calendar.getInstance();
		dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
		dataOdierna.set(Calendar.MINUTE, 0);
		dataOdierna.set(Calendar.SECOND, 0);
		dataOdierna.set(Calendar.MILLISECOND, 0);
		query.setParameter("data", dataOdierna.getTime());
	    }
	    if (filterValid) {
		Date now = Calendar.getInstance().getTime();
		query.setParameter("filterDate", now);
	    }

	    result = query.getResultList();

	} catch (RuntimeException ex) {
	    logger.error("Errore nell'estrazione di DecCriterioRaggrFasc", ex);
	    throw ex;
	}

	return result;
    }

    public static class FiltriElenchiVersFascicoli {
	BigDecimal idTipoFascicolo;
	BigDecimal aaFascicolo;
	String cdKeyFascicolo;
	BigDecimal aaFascicoloDa;
	BigDecimal aaFascicoloA;
	String cdKeyFascicoloDa;
	String cdKeyFascicoloA;
	Timestamp tsIniSesDa;
	Timestamp tsIniSesA;
	Timestamp dtApeFascicoloDa;
	Timestamp dtApeFascicoloA;
	Timestamp dtChiuFascicoloDa;
	Timestamp dtChiuFascicoloA;
	String cdCompositoVoceTitolo;

	FiltriElenchiVersFascicoli() {

	}

	FiltriElenchiVersFascicoli(ElenchiVersFascicoliForm.FascicoliFiltri filtri)
		throws EMFError {
	    idTipoFascicolo = filtri.getId_tipo_fascicolo().parse();
	    aaFascicolo = filtri.getAa_fascicolo().parse();
	    cdKeyFascicolo = filtri.getCd_key_fascicolo().parse();
	    aaFascicoloDa = filtri.getAa_fascicolo_da().parse();
	    aaFascicoloA = filtri.getAa_fascicolo_a().parse();
	    cdKeyFascicoloDa = filtri.getCd_key_fascicolo_da().parse();
	    cdKeyFascicoloA = filtri.getCd_key_fascicolo_a().parse();
	    tsIniSesDa = filtri.getTs_ini_ses_da().parse();
	    tsIniSesA = filtri.getTs_ini_ses_a().parse();
	    dtApeFascicoloDa = filtri.getDt_ape_fascicolo_da().parse();
	    dtApeFascicoloA = filtri.getDt_ape_fascicolo_a().parse();
	    dtChiuFascicoloDa = filtri.getDt_chiu_fascicolo_da().parse();
	    dtChiuFascicoloA = filtri.getDt_chiu_fascicolo_a().parse();
	    cdCompositoVoceTitolo = filtri.getCd_composito_voce_titol().parse();
	}

	public BigDecimal getIdTipoFascicolo() {
	    return idTipoFascicolo;
	}

	void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
	    this.idTipoFascicolo = idTipoFascicolo;
	}

	public BigDecimal getAaFascicolo() {
	    return aaFascicolo;
	}

	void setAaFascicolo(BigDecimal aaFascicolo) {
	    this.aaFascicolo = aaFascicolo;
	}

	public String getCdKeyFascicolo() {
	    return cdKeyFascicolo;
	}

	void setCdKeyFascicolo(String cdKeyFascicolo) {
	    this.cdKeyFascicolo = cdKeyFascicolo;
	}

	public BigDecimal getAaFascicoloDa() {
	    return aaFascicoloDa;
	}

	void setAaFascicoloDa(BigDecimal aaFascicoloDa) {
	    this.aaFascicoloDa = aaFascicoloDa;
	}

	public BigDecimal getAaFascicoloA() {
	    return aaFascicoloA;
	}

	void setAaFascicoloA(BigDecimal aaFascicoloA) {
	    this.aaFascicoloA = aaFascicoloA;
	}

	public String getCdKeyFascicoloDa() {
	    return cdKeyFascicoloDa;
	}

	void setCdKeyFascicoloDa(String cdKeyFascicoloDa) {
	    this.cdKeyFascicoloDa = cdKeyFascicoloDa;
	}

	public String getCdKeyFascicoloA() {
	    return cdKeyFascicoloA;
	}

	void setCdKeyFascicoloA(String cdKeyFascicoloA) {
	    this.cdKeyFascicoloA = cdKeyFascicoloA;
	}

	public Timestamp getTsIniSesDa() {
	    return tsIniSesDa;
	}

	void setTsIniSesDa(Timestamp tsIniSesDa) {
	    this.tsIniSesDa = tsIniSesDa;
	}

	public Timestamp getTsIniSesA() {
	    return tsIniSesA;
	}

	void setTsIniSesA(Timestamp tsIniSesA) {
	    this.tsIniSesA = tsIniSesA;
	}

	public Timestamp getDtApeFascicoloDa() {
	    return dtApeFascicoloDa;
	}

	void setDtApeFascicoloDa(Timestamp dtApeFascicoloDa) {
	    this.dtApeFascicoloDa = dtApeFascicoloDa;
	}

	public Timestamp getDtApeFascicoloA() {
	    return dtApeFascicoloA;
	}

	void setDtApeFascicoloA(Timestamp dtApeFascicoloA) {
	    this.dtApeFascicoloA = dtApeFascicoloA;
	}

	public Timestamp getDtChiuFascicoloDa() {
	    return dtChiuFascicoloDa;
	}

	void setDtChiuFascicoloDa(Timestamp dtChiuFascicoloDa) {
	    this.dtChiuFascicoloDa = dtChiuFascicoloDa;
	}

	public Timestamp getDtChiuFascicoloA() {
	    return dtChiuFascicoloA;
	}

	void setDtChiuFascicoloA(Timestamp dtChiuFascicoloA) {
	    this.dtChiuFascicoloA = dtChiuFascicoloA;
	}

	public String getCdCompositoVoceTitolo() {
	    return cdCompositoVoceTitolo;
	}

	void setCdCompositoVoceTitolo(String cdCompositoVoceTitolo) {
	    this.cdCompositoVoceTitolo = cdCompositoVoceTitolo;
	}

    }

    static class FiltriRicercaFascicoli {
	BigDecimal aaFascicolo;
	BigDecimal aaFascicoloDa;
	BigDecimal aaFascicoloA;
	String cdKeyFascicolo;
	String cdKeyFascicoloDa;
	String cdKeyFascicoloA;
	BigDecimal nmTipoFascicolo;
	String tiModelloXsd;
	String cdXsd;
	String dsOggettoFascicolo;
	Date dtApeFascioloDa;
	Date dtApeFascioloA;
	Date dtChiuFascioloDa;
	Date dtChiuFascioloA;
	String cdProcAmmin;
	String dsProcAmmin;
	BigDecimal niAaConservazione;
	String cdLivelloRiserv;
	String nmSistemaVersante;
	String nmUserid;
	String cdCompositoVoceTitol;
	BigDecimal aaFascicoloPadre;
	BigDecimal aaFascicoloPadreDa;
	BigDecimal aaFascicoloPadreA;
	String cdKeyFascicoloPadre;
	String cdKeyFascicoloPadreDa;
	String cdKeyFascicoloPadreA;
	String dsOggettoFascicoloPadre;
	String cdRegistroKeyUnitaDoc;
	BigDecimal aaKeyUnitaDoc;
	BigDecimal aaKeyUnitaDocDa;
	BigDecimal aaKeyUnitaDocA;
	String cdKeyUnitaDoc;
	String cdKeyUnitaDocDa;
	String cdKeyUnitaDocA;
	String tiConservazione;
	String flForzaContrClassif;
	String flForzaContrNumero;
	String flForzaContrColleg;
	String cdVersioneWs;
	Date tsVersFascicoloDa;
	Date tsVersFascicoloA;
	String tiEsito;
	String tiStatoConservazione;
	String tiStatoFascElencoVers;
	String flUpdAnnulUnitaDoc;
	String flUpdModifUnitaDoc;

	public FiltriRicercaFascicoli() {

	}

	public FiltriRicercaFascicoli(RicercaFascicoliBean filtri) {
	    aaFascicolo = filtri.getAa_fascicolo();
	    aaFascicoloDa = filtri.getAa_fascicolo_da();
	    aaFascicoloA = filtri.getAa_fascicolo_a();
	    cdKeyFascicolo = filtri.getCd_key_fascicolo();
	    cdKeyFascicoloDa = filtri.getCd_key_fascicolo_da();
	    cdKeyFascicoloA = filtri.getCd_key_fascicolo_a();
	    nmTipoFascicolo = filtri.getNm_tipo_fascicolo();
	    tiModelloXsd = filtri.getTi_modello_xsd();
	    cdXsd = filtri.getCd_xsd();
	    dsOggettoFascicolo = filtri.getDs_oggetto_fascicolo();
	    dtApeFascioloDa = filtri.getDt_ape_fasciolo_da();
	    dtApeFascioloA = filtri.getDt_ape_fasciolo_a();
	    dtChiuFascioloDa = filtri.getDt_chiu_fasciolo_da();
	    dtChiuFascioloA = filtri.getDt_chiu_fasciolo_a();
	    cdProcAmmin = filtri.getCd_proc_ammin();
	    dsProcAmmin = filtri.getDs_proc_ammin();
	    niAaConservazione = filtri.getNi_aa_conservazione();
	    cdLivelloRiserv = filtri.getCd_livello_riserv();
	    nmSistemaVersante = filtri.getNm_sistema_versante();
	    nmUserid = filtri.getNm_userid();
	    cdCompositoVoceTitol = filtri.getCd_composito_voce_titol();
	    aaFascicoloPadre = filtri.getAa_fascicolo_padre();
	    aaFascicoloPadreDa = filtri.getAa_fascicolo_padre_da();
	    aaFascicoloPadreA = filtri.getAa_fascicolo_padre_a();
	    cdKeyFascicoloPadre = filtri.getCd_key_fascicolo_padre();
	    cdKeyFascicoloPadreDa = filtri.getCd_key_fascicolo_padre_da();
	    cdKeyFascicoloPadreA = filtri.getCd_key_fascicolo_padre_a();
	    dsOggettoFascicoloPadre = filtri.getDs_oggetto_fascicolo_padre();
	    cdRegistroKeyUnitaDoc = filtri.getCd_registro_key_unita_doc();
	    aaKeyUnitaDoc = filtri.getAa_key_unita_doc();
	    aaKeyUnitaDocDa = filtri.getAa_key_unita_doc_da();
	    aaKeyUnitaDocA = filtri.getAa_key_unita_doc_a();
	    cdKeyUnitaDoc = filtri.getCd_key_unita_doc();
	    cdKeyUnitaDocDa = filtri.getCd_key_unita_doc_da();
	    cdKeyUnitaDocA = filtri.getCd_key_unita_doc_a();
	    tiConservazione = filtri.getTi_conservazione();
	    flForzaContrClassif = filtri.getFl_forza_contr_classif();
	    flForzaContrNumero = filtri.getFl_forza_contr_numero();
	    flForzaContrColleg = filtri.getFl_forza_contr_colleg();
	    tsVersFascicoloDa = filtri.getTs_vers_fascicolo_da();
	    tsVersFascicoloA = filtri.getTs_vers_fascicolo_a();
	    tiEsito = filtri.getTi_esito();
	    tiStatoConservazione = filtri.getTi_stato_conservazione();
	    tiStatoFascElencoVers = filtri.getTi_stato_fasc_elenco_vers();
	    cdVersioneWs = filtri.getCd_versione_ws();
	    flUpdAnnulUnitaDoc = filtri.getFlUpdAnnulUnitaDoc();
	    flUpdModifUnitaDoc = filtri.getFlUpdModifUnitaDoc();
	}

	public BigDecimal getAaFascicolo() {
	    return aaFascicolo;
	}

	void setAaFascicolo(BigDecimal aaFascicolo) {
	    this.aaFascicolo = aaFascicolo;
	}

	public BigDecimal getAaFascicoloDa() {
	    return aaFascicoloDa;
	}

	void setAaFascicoloDa(BigDecimal aaFascicoloDa) {
	    this.aaFascicoloDa = aaFascicoloDa;
	}

	public BigDecimal getAaFascicoloA() {
	    return aaFascicoloA;
	}

	void setAaFascicoloA(BigDecimal aaFascicoloA) {
	    this.aaFascicoloA = aaFascicoloA;
	}

	public String getCdKeyFascicolo() {
	    return cdKeyFascicolo;
	}

	void setCdKeyFascicolo(String cdKeyFascicolo) {
	    this.cdKeyFascicolo = cdKeyFascicolo;
	}

	public String getCdKeyFascicoloDa() {
	    return cdKeyFascicoloDa;
	}

	void setCdKeyFascicoloDa(String cdKeyFascicoloDa) {
	    this.cdKeyFascicoloDa = cdKeyFascicoloDa;
	}

	public String getCdKeyFascicoloA() {
	    return cdKeyFascicoloA;
	}

	void setCdKeyFascicoloA(String cdKeyFascicoloA) {
	    this.cdKeyFascicoloA = cdKeyFascicoloA;
	}

	public BigDecimal getNmTipoFascicolo() {
	    return nmTipoFascicolo;
	}

	void setNmTipoFascicolo(BigDecimal nmTipoFascicolo) {
	    this.nmTipoFascicolo = nmTipoFascicolo;
	}

	public String getTiModelloXsd() {
	    return tiModelloXsd;
	}

	void setTiModelloXsd(String tiModelloXsd) {
	    this.tiModelloXsd = tiModelloXsd;
	}

	public String getCdXsd() {
	    return cdXsd;
	}

	void setCdXsd(String cdXsd) {
	    this.cdXsd = cdXsd;
	}

	public String getDsOggettoFascicolo() {
	    return dsOggettoFascicolo;
	}

	void setDsOggettoFascicolo(String dsOggettoFascicolo) {
	    this.dsOggettoFascicolo = dsOggettoFascicolo;
	}

	public Date getDtApeFascioloDa() {
	    return dtApeFascioloDa;
	}

	void setDtApeFascioloDa(Date dtApeFascioloDa) {
	    this.dtApeFascioloDa = dtApeFascioloDa;
	}

	public Date getDtApeFascioloA() {
	    return dtApeFascioloA;
	}

	void setDtApeFascioloA(Date dtApeFascioloA) {
	    this.dtApeFascioloA = dtApeFascioloA;
	}

	public Date getDtChiuFascioloDa() {
	    return dtChiuFascioloDa;
	}

	void setDtChiuFascioloDa(Date dtChiuFascioloDa) {
	    this.dtChiuFascioloDa = dtChiuFascioloDa;
	}

	public Date getDtChiuFascioloA() {
	    return dtChiuFascioloA;
	}

	void setDtChiuFascioloA(Date dtChiuFascioloA) {
	    this.dtChiuFascioloA = dtChiuFascioloA;
	}

	public String getCdProcAmmin() {
	    return cdProcAmmin;
	}

	void setCdProcAmmin(String cdProcAmmin) {
	    this.cdProcAmmin = cdProcAmmin;
	}

	public String getDsProcAmmin() {
	    return dsProcAmmin;
	}

	void setDsProcAmmin(String dsProcAmmin) {
	    this.dsProcAmmin = dsProcAmmin;
	}

	public BigDecimal getNiAaConservazione() {
	    return niAaConservazione;
	}

	void setNiAaConservazione(BigDecimal niAaConservazione) {
	    this.niAaConservazione = niAaConservazione;
	}

	public String getCdLivelloRiserv() {
	    return cdLivelloRiserv;
	}

	void setCdLivelloRiserv(String cdLivelloRiserv) {
	    this.cdLivelloRiserv = cdLivelloRiserv;
	}

	public String getNmSistemaVersante() {
	    return nmSistemaVersante;
	}

	void setNmSistemaVersante(String nmSistemaVersante) {
	    this.nmSistemaVersante = nmSistemaVersante;
	}

	public String getNmUserid() {
	    return nmUserid;
	}

	void setNmUserid(String nmUserid) {
	    this.nmUserid = nmUserid;
	}

	public String getCdCompositoVoceTitol() {
	    return cdCompositoVoceTitol;
	}

	void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
	    this.cdCompositoVoceTitol = cdCompositoVoceTitol;
	}

	public BigDecimal getAaFascicoloPadre() {
	    return aaFascicoloPadre;
	}

	void setAaFascicoloPadre(BigDecimal aaFascicoloPadre) {
	    this.aaFascicoloPadre = aaFascicoloPadre;
	}

	public BigDecimal getAaFascicoloPadreDa() {
	    return aaFascicoloPadreDa;
	}

	void setAaFascicoloPadreDa(BigDecimal aaFascicoloPadreDa) {
	    this.aaFascicoloPadreDa = aaFascicoloPadreDa;
	}

	public BigDecimal getAaFascicoloPadreA() {
	    return aaFascicoloPadreA;
	}

	void setAaFascicoloPadreA(BigDecimal aaFascicoloPadreA) {
	    this.aaFascicoloPadreA = aaFascicoloPadreA;
	}

	public String getCdKeyFascicoloPadre() {
	    return cdKeyFascicoloPadre;
	}

	void setCdKeyFascicoloPadre(String cdKeyFascicoloPadre) {
	    this.cdKeyFascicoloPadre = cdKeyFascicoloPadre;
	}

	public String getCdKeyFascicoloPadreDa() {
	    return cdKeyFascicoloPadreDa;
	}

	void setCdKeyFascicoloPadreDa(String cdKeyFascicoloPadreDa) {
	    this.cdKeyFascicoloPadreDa = cdKeyFascicoloPadreDa;
	}

	public String getCdKeyFascicoloPadreA() {
	    return cdKeyFascicoloPadreA;
	}

	void setCdKeyFascicoloPadreA(String cdKeyFascicoloPadreA) {
	    this.cdKeyFascicoloPadreA = cdKeyFascicoloPadreA;
	}

	public String getDsOggettoFascicoloPadre() {
	    return dsOggettoFascicoloPadre;
	}

	void setDsOggettoFascicoloPadre(String dsOggettoFascicoloPadre) {
	    this.dsOggettoFascicoloPadre = dsOggettoFascicoloPadre;
	}

	public String getCdRegistroKeyUnitaDoc() {
	    return cdRegistroKeyUnitaDoc;
	}

	void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	    this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
	}

	public BigDecimal getAaKeyUnitaDoc() {
	    return aaKeyUnitaDoc;
	}

	void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	    this.aaKeyUnitaDoc = aaKeyUnitaDoc;
	}

	public BigDecimal getAaKeyUnitaDocDa() {
	    return aaKeyUnitaDocDa;
	}

	void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
	    this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
	}

	public BigDecimal getAaKeyUnitaDocA() {
	    return aaKeyUnitaDocA;
	}

	void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
	    this.aaKeyUnitaDocA = aaKeyUnitaDocA;
	}

	public String getCdKeyUnitaDoc() {
	    return cdKeyUnitaDoc;
	}

	void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	    this.cdKeyUnitaDoc = cdKeyUnitaDoc;
	}

	public String getCdKeyUnitaDocDa() {
	    return cdKeyUnitaDocDa;
	}

	void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
	    this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
	}

	public String getCdKeyUnitaDocA() {
	    return cdKeyUnitaDocA;
	}

	void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
	    this.cdKeyUnitaDocA = cdKeyUnitaDocA;
	}

	public String getTiConservazione() {
	    return tiConservazione;
	}

	void setTiConservazione(String tiConservazione) {
	    this.tiConservazione = tiConservazione;
	}

	public String getFlForzaContrClassif() {
	    return flForzaContrClassif;
	}

	void setFlForzaContrClassif(String flForzaContrClassif) {
	    this.flForzaContrClassif = flForzaContrClassif;
	}

	public String getFlForzaContrNumero() {
	    return flForzaContrNumero;
	}

	void setFlForzaContrNumero(String flForzaContrNumero) {
	    this.flForzaContrNumero = flForzaContrNumero;
	}

	public String getFlForzaContrColleg() {
	    return flForzaContrColleg;
	}

	void setFlForzaContrColleg(String flForzaContrColleg) {
	    this.flForzaContrColleg = flForzaContrColleg;
	}

	public Date getTsVersFascicoloDa() {
	    return tsVersFascicoloDa;
	}

	void setTsVersFascicoloDa(Date tsVersFascicoloDa) {
	    this.tsVersFascicoloDa = tsVersFascicoloDa;
	}

	public Date getTsVersFascicoloA() {
	    return tsVersFascicoloA;
	}

	void setTsVersFascicoloA(Date tsVersFascicoloA) {
	    this.tsVersFascicoloA = tsVersFascicoloA;
	}

	public String getTiEsito() {
	    return tiEsito;
	}

	void setTiEsito(String tiEsito) {
	    this.tiEsito = tiEsito;
	}

	public String getTiStatoConservazione() {
	    return tiStatoConservazione;
	}

	void setTiStatoConservazione(String tiStatoConservazione) {
	    this.tiStatoConservazione = tiStatoConservazione;
	}

	public String getTiStatoFascElencoVers() {
	    return tiStatoFascElencoVers;
	}

	void setTiStatoFascElencoVers(String tiStatoFascElencoVers) {
	    this.tiStatoFascElencoVers = tiStatoFascElencoVers;
	}

	public String getCdVersioneWs() {
	    return cdVersioneWs;
	}

	public void setCdVersioneWs(String cdVersioneWs) {
	    this.cdVersioneWs = cdVersioneWs;
	}

	public String getFlUpdAnnulUnitaDoc() {
	    return flUpdAnnulUnitaDoc;
	}

	void setFlUpdAnnulUnitaDoc(String flUpdAnnulUnitaDoc) {
	    this.flUpdAnnulUnitaDoc = flUpdAnnulUnitaDoc;
	}

	public String getFlUpdModifUnitaDoc() {
	    return flUpdModifUnitaDoc;
	}

	void setFlUpdModifUnitaDoc(String flUpdModifUnitaDoc) {
	    this.flUpdModifUnitaDoc = flUpdModifUnitaDoc;
	}

    }

    protected static class FiltriCriteriRaggrFascicoliPlain {
	private BigDecimal idAmbiente;
	private BigDecimal idEnte;
	private BigDecimal idStrut;
	private String nmCriterioRaggr;
	private String flCriterioRaggrStandard;
	private BigDecimal idTipoFascicolo;
	private String cdCompositoVoceTitol;
	private BigDecimal aaFascicolo;
	private String criterioAttivo;

	FiltriCriteriRaggrFascicoliPlain() {

	}

	protected FiltriCriteriRaggrFascicoliPlain(
		CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc)
		throws EMFError {
	    this.idAmbiente = filtriCriteriRaggrFasc.getId_ambiente().parse();
	    this.idEnte = filtriCriteriRaggrFasc.getId_ente().parse();
	    this.idStrut = filtriCriteriRaggrFasc.getId_strut().parse();
	    this.nmCriterioRaggr = filtriCriteriRaggrFasc.getNm_criterio_raggr().parse();
	    this.flCriterioRaggrStandard = filtriCriteriRaggrFasc.getFl_criterio_raggr_standard()
		    .parse();
	    this.idTipoFascicolo = filtriCriteriRaggrFasc.getId_tipo_fascicolo().parse();
	    this.cdCompositoVoceTitol = filtriCriteriRaggrFasc.getCd_composito_voce_titol().parse();
	    this.aaFascicolo = filtriCriteriRaggrFasc.getAa_fascicolo().parse();
	    this.criterioAttivo = filtriCriteriRaggrFasc.getCriterio_attivo().parse();
	}

	public BigDecimal getIdAmbiente() {
	    return idAmbiente;
	}

	public BigDecimal getIdEnte() {
	    return idEnte;
	}

	public BigDecimal getIdStrut() {
	    return idStrut;
	}

	public String getNmCriterioRaggr() {
	    return nmCriterioRaggr;
	}

	public String getFlCriterioRaggrStandard() {
	    return flCriterioRaggrStandard;
	}

	public BigDecimal getIdTipoFascicolo() {
	    return idTipoFascicolo;
	}

	public String getCdCompositoVoceTitol() {
	    return cdCompositoVoceTitol;
	}

	public BigDecimal getAaFascicolo() {
	    return aaFascicolo;
	}

	public String getCriterioAttivo() {
	    return criterioAttivo;
	}

	void setIdAmbiente(BigDecimal idAmbiente) {
	    this.idAmbiente = idAmbiente;
	}

	void setIdEnte(BigDecimal idEnte) {
	    this.idEnte = idEnte;
	}

	void setIdStrut(BigDecimal idStrut) {
	    this.idStrut = idStrut;
	}

	void setNmCriterioRaggr(String nmCriterioRaggr) {
	    this.nmCriterioRaggr = nmCriterioRaggr;
	}

	void setFlCriterioRaggrStandard(String flCriterioRaggrStandard) {
	    this.flCriterioRaggrStandard = flCriterioRaggrStandard;
	}

	void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
	    this.idTipoFascicolo = idTipoFascicolo;
	}

	void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
	    this.cdCompositoVoceTitol = cdCompositoVoceTitol;
	}

	void setAaFascicolo(BigDecimal aaFascicolo) {
	    this.aaFascicolo = aaFascicolo;
	}

	void setCriterioAttivo(String criterioAttivo) {
	    this.criterioAttivo = criterioAttivo;
	}
    }

    /**
     *
     * @return lista di elementi di tipo DecModelloXsdFascicolo
     */
    public List<DecModelloXsdFascicolo> getDecModelloXsdFascicoloDisp() {

	List<DecModelloXsdFascicolo> result = null;

	String queryStr = "SELECT modelloXsdFasc FROM DecModelloXsdFascicolo modelloXsdFasc WHERE modelloXsdFasc.idModelloXsdFascicolo IN ( "
		+ "SELECT usoModelloXsdFasc.decModelloXsdFascicolo.idModelloXsdFascicolo FROM DecUsoModelloXsdFasc usoModelloXsdFasc "
		+ "WHERE usoModelloXsdFasc.decAaTipoFascicolo.idAaTipoFascicolo IN (SELECT aaTipoFascicolo.idAaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
		+ "WHERE aaTipoFascicolo.aaFinTipoFascicolo >= :aaFinTipoFascicolo) ) ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("aaFinTipoFascicolo",
		BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

	result = query.getResultList();

	return result;

    }

    /**
     *
     * @param idTipoFascicolo id tipo fascicolo
     *
     * @return lista di Object timodelloxsd
     */
    public List<Object[]> getDecModelloXsdFascicoloByTipoFascicolo(long idTipoFascicolo) {

	List<Object[]> result = null;

	String queryStr = "SELECT min(modelloXsdFasc.idModelloXsdFascicolo) as idModelloXsdFascicolo, modelloXsdFasc.tiModelloXsd as tiModelloXsd FROM DecModelloXsdFascicolo modelloXsdFasc WHERE modelloXsdFasc.idModelloXsdFascicolo IN ( "
		+ "SELECT usoModelloXsdFasc.decModelloXsdFascicolo.idModelloXsdFascicolo FROM DecUsoModelloXsdFasc usoModelloXsdFasc "
		+ "WHERE usoModelloXsdFasc.decAaTipoFascicolo.idAaTipoFascicolo = (SELECT aaTipoFascicolo.idAaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
		+ "WHERE aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo and :aaFinTipoFascicolo BETWEEN aaTipoFascicolo.aaIniTipoFascicolo AND aaTipoFascicolo.aaFinTipoFascicolo) ) GROUP BY  modelloXsdFasc.tiModelloXsd ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idTipoFascicolo", idTipoFascicolo);
	query.setParameter("aaFinTipoFascicolo",
		BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

	result = query.getResultList();

	return result;

    }

    /**
     *
     * @param idTipoFascicolo id tipo fascicolo
     * @param tiModelloXsd    modello xsd
     *
     * @return lista di elementi di tipo DecModelloXsdFascicolo
     */
    public List<DecModelloXsdFascicolo> getDecModelloXsdFascicoloByTipoFascicoloAndIdModelloXsd(
	    long idTipoFascicolo, TiModelloXsd tiModelloXsd) {

	List<DecModelloXsdFascicolo> result = null;

	String queryStr = "SELECT modelloXsdFasc FROM DecModelloXsdFascicolo modelloXsdFasc WHERE modelloXsdFasc.idModelloXsdFascicolo IN ( "
		+ "SELECT usoModelloXsdFasc.decModelloXsdFascicolo.idModelloXsdFascicolo FROM DecUsoModelloXsdFasc usoModelloXsdFasc "
		+ "WHERE usoModelloXsdFasc.decAaTipoFascicolo.idAaTipoFascicolo = (SELECT aaTipoFascicolo.idAaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
		+ "WHERE aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo and :aaFinTipoFascicolo BETWEEN aaTipoFascicolo.aaIniTipoFascicolo AND aaTipoFascicolo.aaFinTipoFascicolo) "
		+ "AND usoModelloXsdFasc.decModelloXsdFascicolo.tiModelloXsd = :tiModelloXsd ) ";
	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("idTipoFascicolo", idTipoFascicolo);
	query.setParameter("aaFinTipoFascicolo",
		BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
	query.setParameter("tiModelloXsd", tiModelloXsd);

	result = query.getResultList();

	return result;

    }
}
