/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.elencoVersamento.helper;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.utils.Strings;

import it.eng.paginator.util.HibernateUtils;
import it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco;
import it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare;
import it.eng.parer.elencoVersamento.utils.ComponenteInElenco;
import it.eng.parer.elencoVersamento.utils.DocUdObj;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.PayLoad;
import it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObj;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompIndiceAipDaElab;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUdIndiceAipDaElab;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.ElvDocAggDaElabElenco;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvLogElencoVer;
import it.eng.parer.entity.ElvStatoElencoVer;
import it.eng.parer.entity.ElvUdVersDaElabElenco;
import it.eng.parer.entity.ElvUpdUdDaElabElenco;
import it.eng.parer.entity.ElvUrnElencoVers;
import it.eng.parer.entity.HsmElencoSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.MonKeyTotalUd;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsSessioneVers;
import it.eng.parer.entity.VrsUrnXmlSessioneVers;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvUpdUdDaElabElenco.ElvUpdUdDaElabTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.HsmElencoSessioneFirma.TiEsitoFirmaElenco;
import it.eng.parer.entity.constraint.MonContaSesUpdUd;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers;
import it.eng.parer.entity.inheritance.oop.ElvUdDocUpdDaElabElenco;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.viewEntity.ElvVChkAddDocAgg;
import it.eng.parer.viewEntity.ElvVChkAddDocAggNoEleCor;
import it.eng.parer.viewEntity.ElvVChkAddUpdUd;
import it.eng.parer.viewEntity.ElvVChkAddUpdUdNoEleCor;
import it.eng.parer.viewEntity.ElvVChkUnaUdAnnul;
import it.eng.parer.viewEntity.ElvVLisAllUdByElenco;
import it.eng.parer.viewEntity.ElvVLisElencoDaMarcare;
import it.eng.parer.viewEntity.ElvVLisModifByUd;
import it.eng.parer.viewEntity.ElvVLisUdByStato;
import it.eng.parer.viewEntity.ElvVSelUdDocUpdByCrit;
import it.eng.parer.viewEntity.OrgVLisStrutPerEle;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Agati_D feat. Gilioli_P
 */
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Stateless(mappedName = "ElencoVersamentoHelper")
@LocalBean
public class ElencoVersamentoHelper extends GenericHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ElencoVersamentoHelper.class);
    public static final String JAVAX_PERSISTENCE_FETCHGRAPH = "javax.persistence.fetchgraph";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    @EJB
    private ObjectStorageService objectStorageService;

    public List<Long> retrieveElenchiScadutiDaProcessare(long idStrut) {
        Date systemDate = new Date();
        Query q = em.createQuery("SELECT elencoVers.idElencoVers "
                + "FROM ElvElencoVersDaElab elencoDaElab JOIN elencoDaElab.elvElencoVer elencoVers "
                + "WHERE (elencoDaElab.tiStatoElenco = 'APERTO' " + "AND elencoVers.dtScadChius < :systemDate "
                + "AND elencoDaElab.idStrut = :idStrut)");
        q.setParameter("systemDate", systemDate);
        q.setParameter("idStrut", bigDecimalFromLong(idStrut));
        return q.getResultList();
    }

    public List<Long> retrieveElenchiVuotiDaProcessare(long idStrut) {
        Query q = em.createQuery("SELECT elencoVers.idElencoVers "
                + "FROM ElvElencoVersDaElab elencoDaElab JOIN elencoDaElab.elvElencoVer elencoVers "
                + "WHERE (elencoDaElab.tiStatoElenco = 'APERTO' " + "AND elencoVers.niUnitaDocVersElenco = 0 "
                + "AND elencoVers.niDocAggElenco = 0 " + "AND elencoVers.niUpdUnitaDoc = 0 "
                + "AND elencoDaElab.idStrut = :idStrut)");
        q.setParameter("idStrut", bigDecimalFromLong(idStrut));
        return q.getResultList();
    }

    public List<OrgStrut> retrieveStrutture() {
        Query q = em.createQuery("SELECT s FROM OrgStrut s ORDER BY s.idStrut");
        return q.getResultList();
    }

    public List<OrgStrut> retrieveStruttureByAmb(BigDecimal idAmbiente) {
        Query q = em.createQuery("SELECT s FROM OrgStrut s " + "WHERE s.orgEnte.orgAmbiente.idAmbiente = :idAmbiente "
                + "ORDER BY s.idStrut");
        q.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        return q.getResultList();
    }

    public List<OrgAmbiente> retrieveAmbienti() {
        Query q = em.createQuery("SELECT a FROM OrgAmbiente a ORDER BY a.idAmbiente");
        return q.getResultList();
    }

    public List<OrgVLisStrutPerEle> retrieveStrutturePerEle() {
        Query q = em.createQuery("SELECT s FROM OrgVLisStrutPerEle s ORDER BY s.flPresenzaElencoFisc DESC, s.idStrut");
        return q.getResultList();
    }

    public List<DecCriterioRaggr> retrieveCriterioByStrut(OrgStrut struttura, Date jobStartDate) {
        StringBuilder queryStr = new StringBuilder("SELECT cr " + "FROM DecCriterioRaggr cr "
                + "JOIN FETCH cr.decCriterioFiltroMultiplos crm " + "WHERE cr.orgStrut = :struttura ");
        if (jobStartDate != null) {
            queryStr.append("AND cr.dtIstituz <= :jobStartDate " + "AND cr.dtSoppres > :jobStartDate ");
        }
        queryStr.append("ORDER BY cr.dtIstituz");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("struttura", struttura);
        if (jobStartDate != null) {
            q.setParameter("jobStartDate", jobStartDate);
        }
        return q.getResultList();
    }

    public ElvElencoVer retrieveElencoByCriterio(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc,
            OrgStrut struttura) throws ParerNoResultException {
        // Per un dato criterio ci può essere al massimo un elenco
        try {
            StringBuilder queryStr = new StringBuilder("SELECT elDaElab.elvElencoVer "
                    + "FROM ElvElencoVersDaElab elDaElab " + "WHERE elDaElab.idCriterioRaggr = :idCriterio "
                    + "AND elDaElab.idStrut = :idStruttura " + "AND elDaElab.tiStatoElenco = 'APERTO'");

            boolean tuttiAnniChiaveUdNulli = criterio.getAaKeyUnitaDoc() == null
                    && criterio.getAaKeyUnitaDocDa() == null && criterio.getAaKeyUnitaDocA() == null;

            if (tuttiAnniChiaveUdNulli) {
                queryStr.append(" AND elDaElab.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
            } else {
                queryStr.append(" AND elDaElab.aaKeyUnitaDoc IS NULL ");
            }

            Query q = em.createQuery(queryStr.toString());
            q.setParameter("idCriterio", BigDecimal.valueOf(criterio.getIdCriterioRaggr()));
            q.setParameter("idStruttura", BigDecimal.valueOf(struttura.getIdStrut()));
            if (tuttiAnniChiaveUdNulli) {
                q.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
            }
            return (ElvElencoVer) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new ParerNoResultException();
        }
    }

    /**
     * Seleziona le unità documentarie, i documenti aggiunti e gli aggiornamenti metadati che soddisfano il criterio di
     * raggruppamento passato come parametro ritornandole sotto forma di insieme UpdDocUdObj
     *
     * @param criterio
     *            raggruppamento
     *
     * @return lista oggetti di tipo {@link UpdDocUdObj}
     */
    public List<UpdDocUdObj> retrieveUpdDocUdToProcess(DecCriterioRaggr criterio) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.id.idUnitaDoc, u.id.idDoc, u.id.idUpdUnitaDoc, u.aaKeyUnitaDoc, u.dtCreazione, u.tiEle "
                        + "FROM ElvVSelUdDocUpdByCrit u " + "WHERE u.id.idCriterioRaggr = :idCriterio");

        if (criterio.getAaKeyUnitaDoc() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc >= :aaKeyUnitaDocDa ");
            queryStr.append(" AND u.aaKeyUnitaDoc <= :aaKeyUnitaDocA ");
        }
        // TIP: fdilorenzo, DEFINISCE L'ORDINAMENTO CON CUI DEVONO ESSERE ELABORATI GLI
        // OGGETTI VERSATI (A SUPPORTO
        // DELLA LOGICA DEFINITA IN ANALISI)
        queryStr.append(" ORDER BY u.tiEle");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idCriterio", BigDecimal.valueOf(criterio.getIdCriterioRaggr()));

        if (criterio.getAaKeyUnitaDoc() != null) {
            q.setParameter("aaKeyUnitaDoc", criterio.getAaKeyUnitaDoc());
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            if (criterio.getAaKeyUnitaDocDa() != null) {
                q.setParameter("aaKeyUnitaDocDa", criterio.getAaKeyUnitaDocDa());
            } else {
                q.setParameter("aaKeyUnitaDocDa", BigDecimal.valueOf(2000));
            }
            if (criterio.getAaKeyUnitaDocA() != null) {
                q.setParameter("aaKeyUnitaDocA", criterio.getAaKeyUnitaDocA());
            } else {
                q.setParameter("aaKeyUnitaDocA", BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            }
        }

        List<Object[]> updDocUdObjectList = q.getResultList();
        List<UpdDocUdObj> updDocUdObjSet = new ArrayList<>();

        for (Object[] updDocUdObject : updDocUdObjectList) {
            Constants.TipoEntitaSacer tipoEntitaSacer = (updDocUdObject[5].equals("01_UNI_DOC"))
                    ? Constants.TipoEntitaSacer.UNI_DOC : (updDocUdObject[5].equals("02_DOC_AGG"))
                            ? Constants.TipoEntitaSacer.DOC : Constants.TipoEntitaSacer.UPD;
            BigDecimal id = (updDocUdObject[5].equals("01_UNI_DOC")) ? (BigDecimal) updDocUdObject[0]
                    : (updDocUdObject[5].equals("02_DOC_AGG")) ? (BigDecimal) updDocUdObject[1]
                            : (BigDecimal) updDocUdObject[2];
            updDocUdObjSet.add(
                    new UpdDocUdObj(id, tipoEntitaSacer, (BigDecimal) updDocUdObject[3], (Date) updDocUdObject[4]));
        }

        return updDocUdObjSet;
    }

    /**
     *
     * /** Seleziona le unità documentarie che soddisfano il criterio di raggruppamento passato come parametro
     * ritornandole sotto forma di insieme DocUdObj
     *
     * @param criterio
     *            raggruppamento
     *
     * @return lista oggetti di tipo {@link DocUdObj}
     */
    public List<DocUdObj> retrieveUnitaDocToProcess(DecCriterioRaggr criterio) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.idUnitaDoc, u.aaKeyUnitaDoc, u.dtCreazione " + "FROM ElvVSelUdDocUpdByCrit u "
                        + "WHERE u.idCriterioRaggr = :idCriterio " + "AND u.tiEle = '01_UNI_DOC' ");

        if (criterio.getAaKeyUnitaDoc() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc >= :aaKeyUnitaDocDa ");
            queryStr.append(" AND u.aaKeyUnitaDoc <= :aaKeyUnitaDocA ");

        }

        queryStr.append(" ORDER BY u.dtCreazione");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idCriterio", criterio.getIdCriterioRaggr());

        if (criterio.getAaKeyUnitaDoc() != null) {
            q.setParameter("aaKeyUnitaDoc", criterio.getAaKeyUnitaDoc());
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            if (criterio.getAaKeyUnitaDocDa() != null) {
                q.setParameter("aaKeyUnitaDocDa", criterio.getAaKeyUnitaDocDa());
            } else {
                q.setParameter("aaKeyUnitaDocDa", 2000);
            }
            if (criterio.getAaKeyUnitaDocA() != null) {
                q.setParameter("aaKeyUnitaDocA", criterio.getAaKeyUnitaDocA());
            } else {
                q.setParameter("aaKeyUnitaDocA", Calendar.getInstance().get(Calendar.YEAR));
            }
        }

        List<Object[]> unitaDocObjectList = q.getResultList();
        List<DocUdObj> docUdObjSet = new ArrayList<>();

        for (Object[] unitaDocObject : unitaDocObjectList) {
            docUdObjSet.add(new DocUdObj((BigDecimal) unitaDocObject[0], Constants.TipoEntitaSacer.UNI_DOC,
                    (BigDecimal) unitaDocObject[1], (Date) unitaDocObject[2]));
        }

        return docUdObjSet;
    }

    public List<DocUdObj> retrieveDocToProcess(DecCriterioRaggr criterio) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.idDoc, u.aaKeyUnitaDoc, u.dtCreazione  " + "FROM ElvVSelUdDocUpdByCrit u "
                        + "WHERE u.idCriterioRaggr = :idCriterio " + "AND u.tiEle = '02_DOC_AGG' ");

        if (criterio.getAaKeyUnitaDoc() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc >= :aaKeyUnitaDocDa ");
            queryStr.append(" AND u.aaKeyUnitaDoc <= :aaKeyUnitaDocA ");
        }
        queryStr.append(" ORDER BY u.dtCreazione");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idCriterio", criterio.getIdCriterioRaggr());

        if (criterio.getAaKeyUnitaDoc() != null) {
            q.setParameter("aaKeyUnitaDoc", criterio.getAaKeyUnitaDoc());
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            if (criterio.getAaKeyUnitaDocDa() != null) {
                q.setParameter("aaKeyUnitaDocDa", criterio.getAaKeyUnitaDocDa());
            } else {
                q.setParameter("aaKeyUnitaDocDa", 2000);
            }
            if (criterio.getAaKeyUnitaDocA() != null) {
                q.setParameter("aaKeyUnitaDocA", criterio.getAaKeyUnitaDocA());
            } else {
                q.setParameter("aaKeyUnitaDocA", Calendar.getInstance().get(Calendar.YEAR));
            }
        }
        List<Object[]> docObjectList = q.getResultList();
        List<DocUdObj> docUdObjSet = new ArrayList<>();

        for (Object[] docObject : docObjectList) {
            docUdObjSet.add(new DocUdObj((BigDecimal) docObject[0], Constants.TipoEntitaSacer.DOC,
                    (BigDecimal) docObject[1], (Date) docObject[2]));
        }

        return docUdObjSet;
    }

    /**
     * @deprecated non viene usato da nessuno
     *
     * @param criterio
     *            criteri dell'unità documentaria
     *
     * @return lista di {@link DocUdObj}
     */
    @Deprecated
    public List<DocUdObj> retrieveUpdToProcess(DecCriterioRaggr criterio) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT u.idUpdUnitaDoc, u.aaKeyUnitaDoc, u.dtCreazione  " + "FROM ElvVSelUdDocUpdByCrit u "
                        + "WHERE u.idCriterioRaggr = :idCriterio " + "AND u.tiEle = '03_UPD_UD' ");

        if (criterio.getAaKeyUnitaDoc() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc >= :aaKeyUnitaDocDa ");
            queryStr.append(" AND u.aaKeyUnitaDoc <= :aaKeyUnitaDocA ");
        }
        queryStr.append(" ORDER BY u.dtCreazione, upd.pgUpdUnitaDoc");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idCriterio", criterio.getIdCriterioRaggr());

        if (criterio.getAaKeyUnitaDoc() != null) {
            q.setParameter("aaKeyUnitaDoc", criterio.getAaKeyUnitaDoc());
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            if (criterio.getAaKeyUnitaDocDa() != null) {
                q.setParameter("aaKeyUnitaDocDa", criterio.getAaKeyUnitaDocDa());
            } else {
                q.setParameter("aaKeyUnitaDocDa", 2000);
            }
            if (criterio.getAaKeyUnitaDocA() != null) {
                q.setParameter("aaKeyUnitaDocA", criterio.getAaKeyUnitaDocA());
            } else {
                q.setParameter("aaKeyUnitaDocA", Calendar.getInstance().get(Calendar.YEAR));
            }
        }
        List<Object[]> updObjectList = q.getResultList();
        List<DocUdObj> docUdObjSet = new ArrayList<>();

        for (Object[] updObject : updObjectList) {
            docUdObjSet.add(new DocUdObj((BigDecimal) updObject[0], Constants.TipoEntitaSacer.UPD,
                    (BigDecimal) updObject[1], (Date) updObject[2]));
        }

        return docUdObjSet;
    }

    // METODI RIFATTI DA PAOLO DOPO MODIFICHE LOGICA DI SANDRO
    // (AGGIUNGO RIFLESSIONE A GENNAIO 2017: STIAMO PARLANDO DI QUALCOSA FATTO CIRCA
    // 4 ANNI FA... PENSIERI,
    // IMPLEMENTAZIONI, OPERE E OMISSIONI ERANO STATE DETTATE DA DIVERSI FATTORI...
    // QUESTO COMMENTO VALE COME "PROMEMORIA" PER CHI UN DOMANI AVESSE DA "RIDIRE"
    // SU EVENTUALI TECNICHE DI SVILUPPO
    // CERTAMENTE MIGLIORABILI)
    private String buildClauseExists(String conjunctionWord, int entityNameSuffix, int indiceidattribds,
            String operatore, String filtro, String initialBracket, String from, String where, String entitaSacer,
            String and1, String and2) {
        StringBuilder clauseExists = new StringBuilder();
        clauseExists.append(conjunctionWord)
                .append(initialBracket + " exists (select ric_dati_spec" + entityNameSuffix + " from " + from
                        + " ric_dati_spec" + entityNameSuffix + " WHERE ric_dati_spec" + entityNameSuffix + where
                        + " ");
        clauseExists.append("and ric_dati_spec" + entityNameSuffix + and1 + indiceidattribds + " ");
        if (!and2.isEmpty()) {
            clauseExists.append("and ric_dati_spec" + entityNameSuffix + and2 + indiceidattribds + " ");
        }
        clauseExists.append("and ric_dati_spec" + entityNameSuffix + ".tiEntitaSacer = " + entitaSacer + " ");
        clauseExists.append("and UPPER(ric_dati_spec" + entityNameSuffix + ".dlValore) ");
        clauseExists.append(operatore);
        clauseExists.append(filtro);
        clauseExists.append(") ");
        return clauseExists.toString();
    }

    public ReturnParams buildQueryForDatiSpec(List<Object> datiSpecList) {
        ReturnParams retParams = new ReturnParams();
        StringBuilder queryStr = new StringBuilder();
        // UTILIZZO DEI DATI SPECIFICI
        String operatore = null;
        String filtro = null;
        int entityNameSuffix = 0;
        int indiceidattribds = 0;
        List<DatiSpecQueryParams> mappone = new ArrayList<>();
        List<DefinitoDaBean> listaDefinitoDa = new ArrayList<>();
        Set<String> insiemeTipiUnitaDoc = new HashSet<>();
        Set<String> insiemeTipiDoc = new HashSet<>();
        Set<String> insiemeSistemiMigrazUniDoc = new HashSet<>();
        Set<String> insiemeSistemiMigrazDoc = new HashSet<>();

        // Per ogni dato specifico
        for (Object datiSpecObj : datiSpecList) {
            if (datiSpecObj instanceof DecCriterioDatiSpecBean) {
                DecCriterioDatiSpecBean datiSpec = (DecCriterioDatiSpecBean) datiSpecObj;
                /*
                 * Se il filtro Ã¨ compilato, ricavo le informazioni che mi servono: aggiungo un elemento in
                 * ListaDefinitoDa e nel relativo insieme
                 */
                if ((StringUtils.isNotBlank(datiSpec.getTiOper()) && StringUtils.isNotBlank(datiSpec.getDlValore()))
                        || (datiSpec.getTiOper() != null
                                && datiSpec.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())
                                && StringUtils.isWhitespace(datiSpec.getDlValore()))
                        || (datiSpec.getTiOper() != null
                                && datiSpec.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())
                                && StringUtils.isWhitespace(datiSpec.getDlValore()))) {

                    // Ricavo la listaDefinitoDa di quel preciso dato specifico
                    List<DecCriterioAttribBean> decCriterioAttribList = datiSpec.getDecCriterioAttribs();

                    /*
                     * Scorro questa lista per andare ad inserire l'elemento nella lista principale, ovvero
                     * ListaDefinitoDa
                     */
                    for (DecCriterioAttribBean decCriterioAttrib : decCriterioAttribList) {
                        DefinitoDaBean definitoDa = new DefinitoDaBean();
                        definitoDa.setIdAttribDatiSpec(decCriterioAttrib.getIdAttribDatiSpec());
                        definitoDa.setTiEntitaSacer(decCriterioAttrib.getTiEntitaSacer());
                        definitoDa.setNmTipoDoc(decCriterioAttrib.getNmTipoDoc());
                        definitoDa.setNmTipoUnitaDoc(decCriterioAttrib.getNmTipoUnitaDoc());
                        definitoDa.setNmSistemaMigraz(decCriterioAttrib.getNmSistemaMigraz());
                        definitoDa.setNmAttribDatiSpec(datiSpec.getNmAttribDatiSpec());
                        definitoDa.setTiOper(datiSpec.getTiOper());
                        definitoDa.setDlValore(datiSpec.getDlValore());
                        listaDefinitoDa.add(definitoDa);
                        // Annoto quale elemento sto trattando inserendolo nel relativo insieme
                        // Caso UNI_DOC
                        if (definitoDa.getNmTipoUnitaDoc() != null) {
                            insiemeTipiUnitaDoc.add(definitoDa.getNmTipoUnitaDoc());
                        } // Caso DOC
                        else if (definitoDa.getNmTipoDoc() != null) {
                            insiemeTipiDoc.add(definitoDa.getNmTipoDoc());
                        } // Caso Sistema Migrazione con entitÃ  Sacer UNI_DOC
                        else if (definitoDa.getTiEntitaSacer().equals(Constants.TipoEntitaSacer.UNI_DOC.name())) {
                            insiemeSistemiMigrazUniDoc.add(definitoDa.getNmSistemaMigraz());
                        } // Caso Sistema Migrazione con entitÃ  Sacer DOC
                        else if (definitoDa.getTiEntitaSacer().equals(Constants.TipoEntitaSacer.DOC.name())) {
                            insiemeSistemiMigrazDoc.add(definitoDa.getNmSistemaMigraz());
                        }
                    }
                }
            }
        }

        ///////////////////////
        // COSTRUZIONE QUERY //
        ///////////////////////

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme tipi unitÃ  doc. appena creato
         */
        if (!insiemeTipiUnitaDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeTipiUnitaDoc.iterator();

            // Per ogni nm_tipo_unita_doc presente in insiemeTipiUnitaDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmTipoUnitaDoc = it.next();
                boolean firstTimeTipoUD = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmTipoUnitaDoc() != null
                            && definitoDa.getNmTipoUnitaDoc().equals(nmTipoUnitaDoc)) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeTipoUD) {
                            // (---1---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idUnitaDoc = u.id.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeTipoUD = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---2---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei TipiUnitÃ Doc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme tipi doc. appena creato
         */
        if (!insiemeTipiDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeTipiDoc.iterator();

            // Per ogni nm_tipo_doc presente in insiemeTipiDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmTipoDoc = it.next();
                boolean firstTimeTipoDoc = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmTipoDoc() != null && definitoDa.getNmTipoDoc().equals(nmTipoDoc)) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeTipoDoc) {
                            // (---3---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeTipoDoc = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---4---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpec";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".idAttribDatiSpec = :idattribdatispecin";
                            String and2 = "";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei TipiDoc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme sistemi di migrazione doc. UNI_DOC appena
         * creato
         */
        if (!insiemeSistemiMigrazUniDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeSistemiMigrazUniDoc.iterator();

            // Per ogni nm_sistema_migraz presente in insiemeSistemiMigrazUniDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmSisMigr = it.next();
                boolean firstTimeSisMigrTipoUD = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmSistemaMigraz() != null && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                            && definitoDa.getTiEntitaSacer().equals("UNI_DOC")) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeSisMigrTipoUD) {
                            // (---5---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeSisMigrTipoUD = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---6---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idUnitaDoc = u.idUnitaDoc";
                            String entitaSacer = "'UNI_DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei SistemiMigrazUniDoc
            queryStr.append(") ");
        }

        /*
         * Comincio a costruire la query con i dati presenti nell'insieme sistemi di migrazione doc. DOC appena creato
         */
        if (!insiemeSistemiMigrazDoc.isEmpty()) {
            boolean firstTimeDefinitoDa = true;
            Iterator<String> it = insiemeSistemiMigrazDoc.iterator();

            // Per ogni nm_sistema_migraz presente in insiemeSistemiMigrazUniDoc
            while (it.hasNext()) {
                if (firstTimeDefinitoDa) {
                    queryStr.append("AND ((");
                    firstTimeDefinitoDa = false;
                } else {
                    queryStr.append("OR (");
                }

                String conjunctionWord = "";
                String nmSisMigr = it.next();
                boolean firstTimeSisMigrTipoDoc = true;

                for (DefinitoDaBean definitoDa : listaDefinitoDa) {
                    if (definitoDa.getNmSistemaMigraz() != null && definitoDa.getNmSistemaMigraz().equals(nmSisMigr)
                            && definitoDa.getTiEntitaSacer().equals("DOC")) {
                        int j = mappone.size();
                        Object[] obj = translateFiltroToSql(definitoDa, j);
                        DatiSpecQueryParams datiSpecQueryParams = (DatiSpecQueryParams) obj[0];
                        operatore = (String) obj[1];
                        filtro = (String) obj[2];

                        if (firstTimeSisMigrTipoDoc) {
                            // (---7---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            firstTimeSisMigrTipoDoc = false;
                            entityNameSuffix++;
                            indiceidattribds++;
                        } else {
                            // (---8---) aggiungo il predicato alla query
                            String initialBracket = "";
                            String from = "AroVRicDatiSpecMigraz";
                            String where = ".idDoc = u.idDoc";
                            String entitaSacer = "'DOC'";
                            String and1 = ".nmSistemaMigraz = :nmsistemamigrazin";
                            String and2 = ".idAttribDatiSpec = :idattribdatispecin";
                            queryStr.append(buildClauseExists(conjunctionWord, entityNameSuffix, indiceidattribds,
                                    operatore, filtro, initialBracket, from, where, entitaSacer, and1, and2));
                            conjunctionWord = " AND ";
                            entityNameSuffix++;
                            indiceidattribds++;
                        }
                        mappone.add(datiSpecQueryParams);
                    } // END IF
                } // END FOR di ListaDefinitoDa
                queryStr.append(") ");
            } // END WHILE sull'insieme dei SistemiMigrazUniDoc
            queryStr.append(") ");
        }

        retParams.setMappone(mappone);
        retParams.setQuery(queryStr);
        return retParams;
    }

    public Object[] translateFiltroToSql(DefinitoDaBean definitoDa, int j) {
        String perc1 = "";
        String perc2 = "";
        String filtro = ":valorein" + j;
        String operatore = null;
        // Verifico in quale caso ricado
        if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.CONTIENE.name())) {
            operatore = " like ";
            perc1 = "%";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.INIZIA_PER.name())) {
            operatore = " like ";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.DIVERSO.name())) {
            operatore = " != ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.MAGGIORE.name())) {
            operatore = " > ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.MAGGIORE_UGUALE.name())) {
            operatore = " >= ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.MINORE.name())) {
            operatore = " < ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.MINORE_UGUALE.name())) {
            operatore = " <= ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NON_CONTIENE.name())) {
            operatore = " not like ";
            perc1 = "%";
            perc2 = "%";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())) {
            operatore = " is null ";
            filtro = "";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.UGUALE.name())) {
            operatore = " = ";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())) {
            operatore = " is not null ";
            filtro = "";
        } else if (definitoDa.getTiOper().equals(CostantiDB.TipoOperatoreDatiSpec.E_UNO_FRA.name())) {
            operatore = " IN ";
        }
        DatiSpecQueryParams datiSpecQueryParams = new DatiSpecQueryParams(definitoDa.getTiOper(),
                perc1 + definitoDa.getDlValore() + perc2);
        datiSpecQueryParams.add(definitoDa.getIdAttribDatiSpec());
        datiSpecQueryParams.addSM(definitoDa.getNmSistemaMigraz());
        Object[] obj = new Object[3];
        obj[0] = datiSpecQueryParams;
        obj[1] = operatore;
        obj[2] = filtro;
        return obj;
    }

    public void setNonElabSched(OrgStrut struttura, LogJob logJob) {
        /* Set ud non selezionate da schedulatore */
        // 1) aggiorno tutte le unità documentarie (tabella ARO_UNITA_DOC) presenti
        // nella tabella
        // ELV_UD_VERS_DA_ELAB_ELENCO
        // (che è filtrata mediante la struttura corrente e con data creazione inferiore
        // alla data di inizio della
        // creazione automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED), assegnando stato = NON_SELEZ_SCHED
        Query q1 = em.createQuery("UPDATE AroUnitaDoc ud SET ud.tiStatoUdElencoVers = :nonSelezSched "
                + "WHERE EXISTS (" + "SELECT udDaElab "
                + "FROM ElvUdVersDaElabElenco udDaElab JOIN udDaElab.aroUnitaDoc ud1 "
                + "WHERE ud1.idUnitaDoc = ud.idUnitaDoc " + "AND udDaElab.idStrut = :idStrut "
                + "AND udDaElab.dtCreazione < :startJobTime " + "AND udDaElab.tiStatoUdDaElab = :inAttesaSched)");
        q1.setParameter("startJobTime", logJob.getDtRegLogJob());
        q1.setParameter("idStrut", BigDecimal.valueOf(struttura.getIdStrut()));
        q1.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q1.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
        int updated1 = q1.executeUpdate();
        LOG.debug(
                "CAV - Trovate {} unità documentarie non schedulate relative alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated1, struttura.getNmStrut());

        // 2)aggiorno tutte le unità documentarie (tabella ELV_UD_VERS_DA_ELAB_ELENCO)
        // appartenenti alla struttura
        // corrente
        // e presenti nella coda da elaborare con data creazione inferiore alla data di
        // inizio della creazione
        // automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED, assegnando stato = NON_SELEZ_SCHED
        Query q2 = em.createQuery("UPDATE ElvUdVersDaElabElenco udDaElab SET udDaElab.tiStatoUdDaElab = :nonSelezSched "
                + "WHERE udDaElab.dtCreazione < :startJobTime " + "AND udDaElab.idStrut = :idStrut "
                + "AND udDaElab.tiStatoUdDaElab = :inAttesaSched");
        q2.setParameter("startJobTime", logJob.getDtRegLogJob());
        q2.setParameter("idStrut", BigDecimal.valueOf(struttura.getIdStrut()));
        q2.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q2.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
        int updated2 = q2.executeUpdate();
        LOG.debug(
                "CAV - Trovate nella coda di elaborazione {} unità documentarie non schedulate relative alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated2, struttura.getNmStrut());

        /* Set doc non selezionati da schedulatore */
        // 1)aggiorno tutti documenti (tabella ARO_DOC) presenti nella tabella
        // ELV_DOC_AGG_DA_ELAB_ELENCO
        // (che è filtrata mediante la struttura corrente e con data creazione inferiore
        // alla data di inizio della
        // creazione automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED), assegnando stato = NON_SELEZ_SCHED
        Query q3 = em.createQuery("UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = :nonSelezSched " + "WHERE EXISTS ("
                + "SELECT docDaElab " + "FROM ElvDocAggDaElabElenco docDaElab JOIN docDaElab.aroDoc doc1 "
                + "WHERE doc1.idDoc = doc.idDoc " + "AND docDaElab.idStrut = :idStrut "
                + "AND docDaElab.dtCreazione < :startJobTime " + "AND docDaElab.tiStatoDocDaElab = :inAttesaSched)");
        q3.setParameter("startJobTime", logJob.getDtRegLogJob());
        q3.setParameter("idStrut", BigDecimal.valueOf(struttura.getIdStrut()));
        q3.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q3.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
        int updated3 = q3.executeUpdate();
        LOG.debug("CAV - Trovati {} documenti non schedulati relativi alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated3, struttura.getNmStrut());
        // 2)aggiorno tutti i documenti (tabella ELV_DOC_DA_ELAB_ELENCO_AGG)
        // appartenenti alla struttura corrente
        // e presenti nella coda da elaborare con data creazione inferiore alla data di
        // inizio della creazione
        // automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED, assegnando stato = NON_SELEZ_SCHED
        Query q4 = em
                .createQuery("UPDATE ElvDocAggDaElabElenco docDaElab SET docDaElab.tiStatoDocDaElab = :nonSelezSched "
                        + "WHERE docDaElab.dtCreazione < :startJobTime " + "AND docDaElab.idStrut = :idStrut "
                        + "AND docDaElab.tiStatoDocDaElab = :inAttesaSched");
        q4.setParameter("startJobTime", logJob.getDtRegLogJob());
        q4.setParameter("idStrut", BigDecimal.valueOf(struttura.getIdStrut()));
        q4.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q4.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
        int updated4 = q4.executeUpdate();
        LOG.debug(
                "CAV - Trovati nella coda di elaborazione {} documenti non schedulati relative alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated4, struttura.getNmStrut());

        /* Set upd non selezionati da schedulatore */
        // 1)aggiorno tutti gli aggiornamenti metadati per unità doc (tabella
        // ARO_UPD_UNITA_DOC) presenti nella tabella
        // ELV_UPD_UD_DA_ELAB_ELENCO
        // (che è filtrata mediante la struttura corrente e con data creazione inferiore
        // alla data di inizio della
        // creazione automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED), assegnando stato = NON_SELEZ_SCHED
        Query q5 = em.createQuery("UPDATE AroUpdUnitaDoc upd SET upd.tiStatoUpdElencoVers = :nonSelezSched "
                + "WHERE EXISTS (" + "SELECT updDaElab "
                + "FROM ElvUpdUdDaElabElenco updDaElab JOIN updDaElab.aroUpdUnitaDoc upd1 "
                + "WHERE upd1.idUpdUnitaDoc = upd.idUpdUnitaDoc " + "AND updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.dtCreazione < :startJobTime "
                + "AND updDaElab.tiStatoUpdElencoVers = :inAttesaSched)");
        q5.setParameter("startJobTime", logJob.getDtRegLogJob());
        q5.setParameter("idStrut", struttura.getIdStrut());
        q5.setParameter("nonSelezSched", AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
        q5.setParameter("inAttesaSched", ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
        int updated5 = q5.executeUpdate();
        LOG.debug(
                "CAV - Trovati {} aggiornamenti metadati non schedulati relativi alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated5, struttura.getNmStrut());

        // 2) il sistema raggruppa gli aggiornamenti metadati per unità doc (tabella
        // ELV_UPD_UD_DA_ELAB_ELENCO)
        // appartenenti alla struttura corrente e con data creazione minore o uguale
        // alla data di inizio della creazione
        // automatica degli elenchi - 1 gg
        // e con stato = IN_ATTESA_SCHED; il raggruppamento e’ fatto per data creazione
        // (senza ora, minuti e secondi)
        // + identificatore della struttura, della sub struttura, del tipo unità doc,
        // del registro e del tipo documento
        // e per ogni gruppo si contano i record
        Query q5bis = em.createQuery("SELECT updDaElab.dtCreazione, updDaElab.orgStrut.idStrut, "
                + "updDaElab.orgSubStrut.idSubStrut, updDaElab.decTipoUnitaDoc.idTipoUnitaDoc, "
                + "updDaElab.decRegistroUnitaDoc.idRegistroUnitaDoc, updDaElab.decTipoDocPrinc.idTipoDoc, updDaElab.aaKeyUnitaDoc, COUNT(updDaElab) "
                + "FROM ElvUpdUdDaElabElenco updDaElab " + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.dtCreazione <= :startJobTime " + "AND updDaElab.tiStatoUpdElencoVers = :inAttesaSched "
                + "GROUP BY updDaElab.dtCreazione, updDaElab.orgStrut.idStrut, updDaElab.orgSubStrut.idSubStrut, "
                + "updDaElab.decTipoUnitaDoc.idTipoUnitaDoc, updDaElab.decRegistroUnitaDoc.idRegistroUnitaDoc, updDaElab.decTipoDocPrinc.idTipoDoc, updDaElab.aaKeyUnitaDoc ");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(logJob.getDtRegLogJob());
        calendar.add(Calendar.DATE, -1);

        q5bis.setParameter("startJobTime", calendar.getTime());
        q5bis.setParameter("idStrut", struttura.getIdStrut());
        q5bis.setParameter("inAttesaSched", ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
        List<Object[]> l = q5bis.getResultList();
        LOG.debug(
                "CAV - Trovati {} raggruppamenti di aggiornamenti metadati non schedulati relativi alla struttura '{}'",
                l.size(), struttura.getNmStrut());

        if (!l.isEmpty()) {
            for (Iterator<Object[]> iterator = l.iterator(); iterator.hasNext();) {
                Object[] ogg = iterator.next();
                Date dtRifConta = (Date) ogg[0];
                Long idStrut = (long) ogg[1];
                Long idSubStrut = (long) ogg[2];
                Long idTipoUnitaDoc = (long) ogg[3];
                Long idRegistroUnitaDoc = (long) ogg[4];
                Long idTipoDoc = (long) ogg[5];
                BigDecimal aaKeyUnitaDoc = (BigDecimal) ogg[6];
                Long conteggio = (long) ogg[7];

                // 3a) il sistema determina il record di MON_KEY_TOTAL_UD identificato da
                // identificatore della
                // struttura,
                // della sub struttura, del tipo unità doc, del registro e del tipo documento,
                // del gruppo corrente
                Query q5ter = em.createQuery("SELECT keyTotalUd FROM MonKeyTotalUd keyTotalUd "
                        + "WHERE keyTotalUd.orgStrut.idStrut = :idStrut "
                        + "AND keyTotalUd.orgSubStrut.idSubStrut = :idSubStrut "
                        + "AND keyTotalUd.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc "
                        + "AND keyTotalUd.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                        + "AND keyTotalUd.decTipoDocPrinc.idTipoDoc = :idTipoDoc "
                        + "AND keyTotalUd.aaKeyUnitaDoc = :aaKeyUnitaDoc ");

                q5ter.setParameter("idStrut", idStrut);
                q5ter.setParameter("idSubStrut", idSubStrut);
                q5ter.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
                q5ter.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
                q5ter.setParameter("idTipoDoc", idTipoDoc);
                q5ter.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);

                MonKeyTotalUd key = (MonKeyTotalUd) q5ter.getSingleResult();

                // 3b) il sistema aggiorna il record di MON_CONTA_SES_UPD_UD identificato
                // dall’identificatore della
                // chiave di totalizzazione,
                // dalla data del gruppo e dal tipo stato = IN_ATTESA_SCHED, sottraendo il
                // numero riportato dal gruppo
                Query q5quater = em.createQuery(
                        "UPDATE MonContaSesUpdUd conta SET conta.niSesUpdUd = conta.niSesUpdUd - :conteggio "
                                + "WHERE conta.monKeyTotalUd.idKeyTotalUd = :idKeyTotalUd "
                                + "AND conta.dtRifConta = :dtRifConta " + "AND conta.tiStatoUdpUd = :inAttesaSched ");
                q5quater.setParameter("conteggio", BigDecimal.valueOf(conteggio));
                q5quater.setParameter("dtRifConta", dtRifConta);
                q5quater.setParameter("idKeyTotalUd", key.getIdKeyTotalUd());
                q5quater.setParameter("inAttesaSched", MonContaSesUpdUd.TiStatoUdpUdMonContaSesUpdUd.IN_ATTESA_SCHED);

                q5quater.executeUpdate();

                // 3c) il sistema aggiorna il record di MON_CONTA_SES_UPD_UD identificato
                // dall’identificatore della
                // chiave di totalizzazione, dalla data del gruppo e dal tipo stato =
                // NON_SELEZ_SCHED, sommando il
                // numero riportato dal gruppo
                Query q5quintus = em.createQuery(
                        "UPDATE MonContaSesUpdUd conta SET conta.niSesUpdUd = conta.niSesUpdUd + :conteggio "
                                + "WHERE conta.monKeyTotalUd.idKeyTotalUd = :idKeyTotalUd "
                                + "AND conta.dtRifConta = :dtRifConta " + "AND conta.tiStatoUdpUd = :inAttesaSched ");
                q5quintus.setParameter("conteggio", BigDecimal.valueOf(conteggio));
                q5quintus.setParameter("dtRifConta", dtRifConta);
                q5quintus.setParameter("idKeyTotalUd", key.getIdKeyTotalUd());
                q5quintus.setParameter("inAttesaSched", MonContaSesUpdUd.TiStatoUdpUdMonContaSesUpdUd.NON_SELEZ_SCHED);

                q5quintus.executeUpdate();
            }
        }

        // 5)aggiorno tutti gli aggiornamenti metadati per unità doc (tabella
        // ELV_UPD_UD_DA_ELAB_ELENCO) appartenenti
        // alla struttura corrente
        // e presenti nella coda da elaborare con data creazione inferiore alla data di
        // inizio della creazione
        // automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED, assegnando stato = NON_SELEZ_SCHED
        Query q6 = em.createQuery(
                "UPDATE ElvUpdUdDaElabElenco updDaElab SET updDaElab.tiStatoUpdElencoVers = :nonSelezSched "
                        + "WHERE updDaElab.dtCreazione < :startJobTime " + "AND updDaElab.orgStrut.idStrut = :idStrut "
                        + "AND updDaElab.tiStatoUpdElencoVers = :inAttesaSched");
        q6.setParameter("startJobTime", logJob.getDtRegLogJob());
        q6.setParameter("idStrut", struttura.getIdStrut());
        q6.setParameter("nonSelezSched", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);
        q6.setParameter("inAttesaSched", ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
        int updated6 = q6.executeUpdate();
        LOG.debug("CAV - Trovati"
                + " nella coda di elaborazione {} aggiornamenti metadati non schedulati relative alla struttura '{}'. Assegno 'NON_ELAB_SCHED'",
                updated6, struttura.getNmStrut());
    }

    // MEV#27169
    public void setNonSelezSchedJms(OrgStrut struttura, BigDecimal id, Constants.TipoEntitaSacer tiEntitaSacer) {
        switch (tiEntitaSacer) {
        case UNI_DOC:
            /* Set ud non selezionata da schedulatore */
            // 1) aggiorno l'unità documentaria (tabella ARO_UNITA_DOC) presente nella
            // tabella
            // ELV_UD_VERS_DA_ELAB_ELENCO
            // (che è filtrata mediante id_unita_doc corrente e con stato =
            // IN_ATTESA_SCHED), assegnando stato =
            // NON_SELEZ_SCHED
            Query q1 = em.createQuery("UPDATE AroUnitaDoc ud SET ud.tiStatoUdElencoVers = :nonSelezSched "
                    + "WHERE EXISTS (" + "SELECT udDaElab "
                    + "FROM ElvUdVersDaElabElenco udDaElab JOIN udDaElab.aroUnitaDoc ud1 "
                    + "WHERE ud1.idUnitaDoc = ud.idUnitaDoc " + "AND udDaElab.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND udDaElab.tiStatoUdDaElab = :inAttesaSched)");
            q1.setParameter("idUnitaDoc", longFromBigDecimal(id));
            q1.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
            q1.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
            q1.executeUpdate();
            LOG.debug(String.format(
                    "Trovata unità documentaria %s non schedulata relativa alla struttura %s. Assegno 'NON_SELEZ_SCHED'",
                    id, struttura.getNmStrut()));

            // 2) aggiorno l'unità documentaria (tabella ELV_UD_VERS_DA_ELAB_ELENCO)
            // appartenente alla struttura
            // corrente
            // e presente nella coda da elaborare con stato = IN_ATTESA_SCHED, assegnando
            // stato = NON_SELEZ_SCHED
            Query q2 = em
                    .createQuery("UPDATE ElvUdVersDaElabElenco udDaElab SET udDaElab.tiStatoUdDaElab = :nonSelezSched "
                            + "WHERE udDaElab.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                            + "AND udDaElab.tiStatoUdDaElab = :inAttesaSched");
            q2.setParameter("idUnitaDoc", longFromBigDecimal(id));
            q2.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
            q2.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
            q2.executeUpdate();
            LOG.debug(String.format(
                    "Trovata nella coda di elaborazione l'unità documentaria %s non schedulata relativa alla struttura %s. Assegno 'NON_SELEZ_SCHED'",
                    id, struttura.getNmStrut()));
            break;
        case DOC:
            /* Set doc non selezionato da schedulatore */
            // 1)aggiorno il documento (tabella ARO_DOC) presente nella tabella
            // ELV_DOC_AGG_DA_ELAB_ELENCO
            // (che è filtrata mediante id_doc corrente e con stato = IN_ATTESA_SCHED),
            // assegnando stato =
            // NON_SELEZ_SCHED
            Query q3 = em
                    .createQuery("UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = :nonSelezSched " + "WHERE EXISTS ("
                            + "SELECT docDaElab " + "FROM ElvDocAggDaElabElenco docDaElab JOIN docDaElab.aroDoc doc1 "
                            + "WHERE doc1.idDoc = doc.idDoc " + "AND docDaElab.aroDoc.idDoc = :idDoc "
                            + "AND docDaElab.tiStatoDocDaElab = :inAttesaSched)");
            q3.setParameter("idDoc", longFromBigDecimal(id));
            q3.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
            q3.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
            q3.executeUpdate();
            LOG.debug(String.format(
                    "Trovato documento %s non schedulato relativo alla struttura %s. Assegno 'NON_SELEZ_SCHED'", id,
                    struttura.getNmStrut()));
            // 2)aggiorno il documento (tabella ELV_DOC_DA_ELAB_ELENCO_AGG) appartenente
            // alla struttura corrente
            // e presente nella coda da elaborare con stato = IN_ATTESA_SCHED, assegnando
            // stato = NON_SELEZ_SCHED
            Query q4 = em.createQuery(
                    "UPDATE ElvDocAggDaElabElenco docDaElab SET docDaElab.tiStatoDocDaElab = :nonSelezSched "
                            + "WHERE docDaElab.aroDoc.idDoc = :idDoc "
                            + "AND docDaElab.tiStatoDocDaElab = :inAttesaSched");
            q4.setParameter("idDoc", longFromBigDecimal(id));
            q4.setParameter("nonSelezSched", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
            q4.setParameter("inAttesaSched", ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name());
            q4.executeUpdate();
            LOG.debug(String.format(
                    "Trovato nella coda di elaborazione il documento %s non schedulato relativo alla struttura %s. Assegno 'NON_SELEZ_SCHED'",
                    id, struttura.getNmStrut()));
            break;
        case UPD:
            /* Set upd non selezionato da schedulatore */
            // 1)aggiorno l'aggiornamento metadati per unità doc (tabella ARO_UPD_UNITA_DOC)
            // presente nella tabella
            // ELV_UPD_UD_DA_ELAB_ELENCO
            // (che è filtrata mediante id_upd_unita_doc corrente e con stato =
            // IN_ATTESA_SCHED), assegnando stato =
            // NON_SELEZ_SCHED
            Query q5 = em.createQuery("UPDATE AroUpdUnitaDoc upd SET upd.tiStatoUpdElencoVers = :nonSelezSched "
                    + "WHERE EXISTS (" + "SELECT updDaElab "
                    + "FROM ElvUpdUdDaElabElenco updDaElab JOIN updDaElab.aroUpdUnitaDoc upd1 "
                    + "WHERE upd1.idUpdUnitaDoc = upd.idUpdUnitaDoc "
                    + "AND updDaElab.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc "
                    + "AND updDaElab.tiStatoUpdElencoVers = :inAttesaSched)");
            q5.setParameter("idUpdUnitaDoc", longFromBigDecimal(id));
            q5.setParameter("nonSelezSched", AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
            q5.setParameter("inAttesaSched", ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
            q5.executeUpdate();
            LOG.debug(String.format(
                    "Trovato aggiornamento metadati %s non schedulato relativo alla struttura %s. Assegno 'NON_SELEZ_SCHED'",
                    id, struttura.getNmStrut()));

            // 5)aggiorno l'aggiornamento metadati per unità doc (tabella
            // ELV_UPD_UD_DA_ELAB_ELENCO) appartenente
            // alla struttura corrente
            // e presente nella coda da elaborare con stato = IN_ATTESA_SCHED, assegnando
            // stato = NON_SELEZ_SCHED
            Query q6 = em.createQuery(
                    "UPDATE ElvUpdUdDaElabElenco updDaElab SET updDaElab.tiStatoUpdElencoVers = :nonSelezSched "
                            + "WHERE updDaElab.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc "
                            + "AND updDaElab.tiStatoUpdElencoVers = :inAttesaSched");
            q6.setParameter("idUpdUnitaDoc", longFromBigDecimal(id));
            q6.setParameter("nonSelezSched", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);
            q6.setParameter("inAttesaSched", ElvUpdUdDaElabTiStatoUpdElencoVers.IN_ATTESA_SCHED);
            q6.executeUpdate();
            LOG.debug(String.format(
                    "Trovato nella coda di elaborazione l'aggiornamento metadati %s non schedulato relativo alla struttura %s. Assegno 'NON_SELEZ_SCHED'",
                    id, struttura.getNmStrut()));
            break;
        }
    }
    // end MEV#27169

    /**
     * Conta il numero di documenti con tipo creazione uguale a "VERSAMENTO_UNITA_DOC" in una determinata unità
     * documentaria
     *
     * @param unitaDoc
     *            Unità documentaria della quale si vuole il numero di documenti
     *
     * @return Il numero di documenti
     */
    public long countDocsInUnitaDocCustom(BigDecimal unitaDoc) {
        String query = "SELECT count(doc) " + "FROM AroDoc doc " + "WHERE doc.aroUnitaDoc.idUnitaDoc = :unitaDoc "
                + "AND doc.tiCreazione = :TIPO_CREAZIONE";

        Query q = em.createQuery(query);
        q.setParameter("unitaDoc", longFromBigDecimal(unitaDoc));
        q.setParameter("TIPO_CREAZIONE", "VERSAMENTO_UNITA_DOC"); // TODO: inserire ENUM

        long numDocsInUd = ((Long) q.getSingleResult());
        LOG.debug("ADV - Trovati '{}' documenti all'interno dell'unità  documentale {}", numDocsInUd, unitaDoc);
        return numDocsInUd;
    }

    /**
     * Conta il numero di elenchi con stato diverso da APERTO creati con il criterio nel giorno
     *
     * @param idCrit
     *            criterio per il quale si vuole il numero di elenchi
     *
     * @return Il numero di elenchi
     */
    public long countElenchiGgByCritNonAperti(BigDecimal idCrit) {
        String query = "SELECT count(elenco) " + "FROM ElvElencoVer elenco "
                + "WHERE elenco.decCriterioRaggr.idCriterioRaggr = :idCrit " + "AND elenco.tiStatoElenco != 'APERTO' "
                + "AND TRUNC( elenco.dtCreazioneElenco) = TRUNC( :dataOdierna)";

        Query q = em.createQuery(query);
        q.setParameter("idCrit", longFromBigDecimal(idCrit));
        q.setParameter("dataOdierna", new Date());

        long numElenchiGg = ((Long) q.getSingleResult());
        LOG.debug("ADV - Trovati '{}' elenchi creati con il criterio {} nel giorno", numElenchiGg, idCrit);
        return numElenchiGg;
    }

    /**
     * Conta il numero di elenchi con stato APERTO creati con il criterio nel giorno
     *
     * @param idCrit
     *            criterio per il quale si vuole il numero di elenchi
     *
     * @return Il numero di elenchi
     */
    public long countElenchiGgByCritAperti(BigDecimal idCrit) {
        String query = "SELECT count(elenco) " + "FROM ElvElencoVer elenco "
                + "WHERE elenco.decCriterioRaggr.idCriterioRaggr = :idCrit " + "AND elenco.tiStatoElenco = 'APERTO' "
                + "AND TRUNC( elenco.dtCreazioneElenco) = TRUNC( :dataOdierna)";

        Query q = em.createQuery(query);
        q.setParameter("idCrit", longFromBigDecimal(idCrit));
        q.setParameter("dataOdierna", new Date());

        long numElenchiGg = ((Long) q.getSingleResult());
        LOG.debug("ADV - Trovati '{}' elenchi creati con il criterio {} nel giorno", numElenchiGg, idCrit);
        return numElenchiGg;
    }

    public List<AroCompDoc> retrieveCompsInDoc(AroDoc doc) {
        Query q = em.createQuery("SELECT comp " + "FROM AroDoc doc " + "JOIN doc.aroStrutDocs aroStrutDoc "
                + "JOIN aroStrutDoc.aroCompDocs comp " + "WHERE comp.aroCompDoc is null " + "AND doc.idDoc = :idDoc");
        q.setParameter("idDoc", doc.getIdDoc());
        return q.getResultList();
    }

    /**
     * Restituisce il numero dei componenti e la somma della dimensione dei componenti appartenenti all'unità
     * documentaria corrente con tipo creazione uguale a VERSAMENTO_UNITA_DOC
     *
     * @param unitaDocId
     *            id unita doc
     *
     * @return entity AroUnitaDoc
     */
    public Object numCompsAndSizeInUnitaDocCustom(BigDecimal unitaDocId) {
        Query q = em.createQuery("SELECT count(comp.idCompDoc), SUM(comp.niSizeFileCalc) "
                + "FROM AroUnitaDoc unitaDoc " + "JOIN unitaDoc.aroDocs doc " + "JOIN doc.aroStrutDocs strutDoc "
                + "JOIN strutDoc.aroCompDocs comp " + "WHERE doc.aroUnitaDoc.idUnitaDoc = :unitaDocId "
                + "AND doc.tiCreazione = :TIPO_CREAZIONE");
        q.setParameter("unitaDocId", longFromBigDecimal(unitaDocId));
        q.setParameter("TIPO_CREAZIONE", CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name());
        return q.getSingleResult();
    }

    /**
     * Restituisce il numero dei componenti e la somma della dimensione dei componenti appartenenti al documento
     * corrente
     *
     * @param docId
     *            id documento
     *
     * @return entity AroDoc
     */
    public Object numCompsAndSizeInDoc(BigDecimal docId) {
        Query q = em.createQuery("SELECT count(comp.idCompDoc), SUM(comp.niSizeFileCalc) " + "FROM AroDoc doc "
                + "JOIN doc.aroStrutDocs strutDoc " + "JOIN strutDoc.aroCompDocs comp " + "WHERE doc.idDoc = :docId");
        q.setParameter("docId", longFromBigDecimal(docId));
        return q.getSingleResult();
    }

    public void deleteUdDocFromQueue(AroUnitaDoc ud) {
        ElvUdVersDaElabElenco udVersDaElab;
        Query q = em.createQuery(
                "select udVersDaElab from ElvUdVersDaElabElenco udVersDaElab where udVersDaElab.aroUnitaDoc.idUnitaDoc = :idUnitaDoc");
        q.setParameter("idUnitaDoc", ud.getIdUnitaDoc());
        try {
            udVersDaElab = (ElvUdVersDaElabElenco) q.getSingleResult();
        } catch (NoResultException ex) {
            udVersDaElab = null;
        }
        if (udVersDaElab != null) {
            em.remove(udVersDaElab);
            LOG.debug("ADV - Eliminata unità documentaria con id = {} dalla coda di elaborazione", ud.getIdUnitaDoc());
        }

    }

    public void deleteDocFromQueue(AroDoc doc) {
        ElvDocAggDaElabElenco docAggDaElab;
        Query q = em.createQuery("select docAggDaElab " + "from ElvDocAggDaElabElenco docAggDaElab "
                + "where docAggDaElab.aroDoc.idDoc = :idDoc");
        q.setParameter("idDoc", doc.getIdDoc());
        try {
            docAggDaElab = (ElvDocAggDaElabElenco) q.getSingleResult();
        } catch (NoResultException ex) {
            docAggDaElab = null;
        }
        if (docAggDaElab != null) {
            em.remove(docAggDaElab);
            LOG.debug("ADV - Eliminato il documento con id = {} dalla coda di elaborazione", doc.getIdDoc());
        }
    }

    public void deleteUpdFromQueue(AroUpdUnitaDoc upd) {
        ElvUpdUdDaElabElenco updUdDaElab;
        Query q = em.createQuery("select updUdDaElab " + "from ElvUpdUdDaElabElenco updUdDaElab "
                + "where updUdDaElab.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc");
        q.setParameter("idUpdUnitaDoc", upd.getIdUpdUnitaDoc());
        try {
            updUdDaElab = (ElvUpdUdDaElabElenco) q.getSingleResult();
        } catch (NoResultException ex) {
            updUdDaElab = null;
        }
        if (updUdDaElab != null) {
            em.remove(updUdDaElab);
            LOG.debug("ADV - Eliminato l'aggiornamento metadati con id = {} dalla coda di elaborazione",
                    upd.getIdUpdUnitaDoc());
        }
    }

    public List<AroDoc> retrieveDocsInElenco(ElvElencoVer elenco) {
        return elenco.getAroDocs();
    }

    public List<AroUnitaDoc> retrieveUdDocsInElenco(ElvElencoVer elenco) {
        return elenco.getAroUnitaDocs();
    }

    public List<AroUpdUnitaDoc> retrieveUpdsInElenco(ElvElencoVer elenco) {
        return elenco.getAroUpdUnitaDocs();
    }

    public ElvElencoVer retrieveElencoById(Long idElenco) {
        return em.find(ElvElencoVer.class, idElenco);
    }

    public void writeLogElencoVers(ElvElencoVer elenco, OrgStrut struttura, Long user, String tipoOper, AroDoc doc,
            AroUnitaDoc unitaDoc) {
        writeLogElencoVers(elenco, struttura, user, tipoOper, doc, null, unitaDoc, null);
    }

    public void writeLogElencoVers(ElvElencoVer elenco, OrgStrut struttura, Long user, String tipoOper,
            AroUpdUnitaDoc upd, AroUnitaDoc unitaDoc) {
        writeLogElencoVers(elenco, struttura, user, tipoOper, null, upd, unitaDoc, null);
    }

    public void writeLogElencoVers(ElvElencoVer elenco, OrgStrut struttura, Long user, String tipoOper) {
        writeLogElencoVers(elenco, struttura, user, tipoOper, null, null, null, null);
    }

    public void writeLogElencoVers(ElvElencoVer elenco, OrgStrut struttura, String tipoOper, LogJob logJob) {
        writeLogElencoVers(elenco, struttura, null, tipoOper, null, null, null, logJob);
    }

    public void writeLogElencoVers(ElvElencoVer elenco, OrgStrut struttura, Long user, String tipoOper, AroDoc doc,
            AroUpdUnitaDoc upd, AroUnitaDoc unitaDoc, LogJob logJob) {
        ElvLogElencoVer logElenco = new ElvLogElencoVer();
        Date date = new Date();
        logElenco.setOrgStrut(struttura);
        logElenco.setTmOper(new Timestamp(date.getTime()));
        logElenco.setTiOper(tipoOper);
        if (user != null) {
            logElenco.setIamUser(em.find(IamUser.class, user));
        }
        logElenco.setIdElencoVers(new BigDecimal(elenco.getIdElencoVers()));
        logElenco.setNmElenco(elenco.getNmElenco());
        if (doc != null) {
            // TODO: controllare perche il campo sotto non c'è
            logElenco.setPgDoc(doc.getPgDoc());
            logElenco.setTiDoc(doc.getTiDoc());
        }
        if (upd != null) {
            // TODO: controllare perche il campo sotto non c'è
            logElenco.setPgUpdUnitaDoc(upd.getPgUpdUnitaDoc());
        }
        if (unitaDoc != null) {
            logElenco.setCdRegistroKeyUnitaDoc(unitaDoc.getCdRegistroKeyUnitaDoc());
            logElenco.setAaKeyUnitaDoc(unitaDoc.getAaKeyUnitaDoc());
            logElenco.setCdKeyUnitaDoc(unitaDoc.getCdKeyUnitaDoc());
        }
        if (logJob != null) {
            logElenco.setLogJob(logJob);
        }
        em.persist(logElenco);
        em.flush();
    }

    public OrgStrut retrieveOrgStrutByid(BigDecimal idStrut) {
        return em.find(OrgStrut.class, idStrut.longValue());
    }

    public LogJob retrieveLogJobByid(long idLogJob) {
        return em.find(LogJob.class, idLogJob);
    }

    // MEV#27169
    public DecCriterioRaggr retrieveDecCriterioRaggrByid(BigDecimal idCriterio) {
        return em.find(DecCriterioRaggr.class, idCriterio.longValue());
    }
    // end MEV#27169

    // MEV#27891
    /**
     * Elementi non selezionati da scheduler da processare inclusi nella coda degli elenchi da elaborare corrente sia
     * come ud versate sia come doc aggiunti sia come aggiornamenti metadati.
     *
     * @param idStrut
     *            idStrut
     * @param rowNum
     *            rowNum
     * 
     * @return List - Insieme di elementi ordinati
     */
    public List<PayLoad> retrieveUdDocUpdToVerify(long idStrut, int rowNum) {

        TypedQuery<PayLoad> q1 = em.createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.PayLoad"
                + "(udDaElab.aroUnitaDoc.idUnitaDoc, udDaElab.idStrut, 'UNI_DOC', udDaElab.tiStatoUdDaElab, udDaElab.aaKeyUnitaDoc, udDaElab.dtCreazione) "
                + "FROM ElvUdVersDaElabElenco udDaElab " + "WHERE udDaElab.idStrut = :idStrut "
                + "AND udDaElab.tiStatoUdDaElab =  :tiStato ", PayLoad.class);

        TypedQuery<PayLoad> q2 = em.createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.PayLoad"
                + "(docDaElab.aroDoc.idDoc, docDaElab.idStrut, 'DOC', docDaElab.tiStatoDocDaElab, docDaElab.aaKeyUnitaDoc, docDaElab.dtCreazione) "
                + "FROM ElvDocAggDaElabElenco docDaElab " + "WHERE docDaElab.idStrut = :idStrut "
                + "AND docDaElab.tiStatoDocDaElab =  :tiStato ", PayLoad.class);

        TypedQuery<PayLoad> q3 = em.createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.PayLoad"
                + "(updDaElab.aroUpdUnitaDoc.idUpdUnitaDoc, updDaElab.orgStrut.idStrut, 'UPD', updDaElab.tiStatoUpdElencoVers, updDaElab.aaKeyUnitaDoc, updDaElab.dtCreazione) "
                + "FROM ElvUpdUdDaElabElenco updDaElab " + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.tiStatoUpdElencoVers =  :tiStato ", PayLoad.class);

        q1.setParameter("idStrut", bigDecimalFromLong(idStrut));
        q1.setParameter("tiStato", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q2.setParameter("idStrut", bigDecimalFromLong(idStrut));
        q2.setParameter("tiStato", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        q3.setParameter("idStrut", idStrut);
        q3.setParameter("tiStato", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);

        List<PayLoad> list = new ArrayList<>();

        q1.setMaxResults(rowNum);
        List<PayLoad> l1 = q1.getResultList();
        list.addAll(l1);
        if (list.size() < rowNum) {
            q2.setMaxResults(rowNum - list.size());
            List<PayLoad> l2 = q2.getResultList();
            list.addAll(l2);
        }
        if (list.size() < rowNum) {
            q3.setMaxResults(rowNum - list.size());
            List<PayLoad> l3 = q3.getResultList();
            list.addAll(l3);
        }

        return list;

    }

    public Long countUdDaElabToValidate(long idStrut) {

        String queryStr = "SELECT COUNT(*) " + "FROM ElvUdVersDaElabElenco udDaElab "
                + "WHERE udDaElab.idStrut = :idStrut " + "AND udDaElab.tiStatoUdDaElab =  :tiStatoUdDaElab";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoUdDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return (Long) query.getSingleResult();
    }

    public Long countDocDaElabToValidate(long idStrut) {

        String queryStr = "SELECT COUNT(*) " + "FROM ElvDocAggDaElabElenco docDaElab "
                + "WHERE docDaElab.idStrut = :idStrut " + "AND docDaElab.tiStatoDocDaElab =  :tiStatoDocDaElab";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoDocDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return (Long) query.getSingleResult();
    }

    public Long countUpdDaElabToValidate(long idStrut) {

        String queryStr = "SELECT COUNT(*) " + "FROM ElvUpdUdDaElabElenco updDaElab "
                + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.tiStatoUpdElencoVers =  :tiStatoUpdElencoVers";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        query.setParameter("idStrut", idStrut);
        query.setParameter("tiStatoUpdElencoVers", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);

        return (Long) query.getSingleResult();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUdToValidate(long idStrut, int maxResult) {

        String queryStr = "SELECT udDaElab " + "FROM ElvUdVersDaElabElenco udDaElab "
                + "WHERE udDaElab.idStrut = :idStrut " + "AND udDaElab.tiStatoUdDaElab = :tiStatoUdDaElab "
                + "ORDER BY udDaElab.idUdVersDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        query.setMaxResults(maxResult);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoUdDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return query.getResultStream();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUdToValidateInParallel(long idStrut, int maxResult) {

        String queryStr = "SELECT udDaElab " + "FROM ElvUdVersDaElabElenco udDaElab "
                + "WHERE udDaElab.idStrut = :idStrut " + "AND udDaElab.tiStatoUdDaElab = :tiStatoUdDaElab "
                + "ORDER BY udDaElab.idUdVersDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvUdVersDaElabElenco> entityGraph = em.createEntityGraph(ElvUdVersDaElabElenco.class);
        entityGraph.addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoUdDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return query.getResultStream().limit(maxResult);
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUdToValidateInParallelLessThan1k(long idStrut, int maxResult) {

        // Get primary keys with LIMIT and OFFSET
        String queryStr = "SELECT udDaElab.idUdVersDaElabElenco " + "FROM ElvUdVersDaElabElenco udDaElab "
                + "WHERE udDaElab.idStrut = :idStrut " + "AND udDaElab.tiStatoUdDaElab = :tiStatoUdDaElab "
                + "ORDER BY udDaElab.idUdVersDaElabElenco";

        Query q1 = em.createQuery(queryStr);
        q1.setHint(QueryHints.HINT_READONLY, true);
        q1.setHint(QueryHints.HINT_CACHEABLE, true);
        q1.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        q1.setMaxResults(maxResult);

        q1.setParameter("idStrut", bigDecimalFromLong(idStrut));
        q1.setParameter("tiStatoUdDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        // Get entities with associations
        queryStr = "SELECT udDaElab " + "FROM ElvUdVersDaElabElenco udDaElab "
                + "WHERE udDaElab.idUdVersDaElabElenco IN (:ids)";

        Query q2 = em.createQuery(queryStr);
        q2.setHint(QueryHints.HINT_READONLY, true);
        q2.setHint(QueryHints.HINT_CACHEABLE, true);
        q2.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvUdVersDaElabElenco> entityGraph = em.createEntityGraph(ElvUdVersDaElabElenco.class);
        entityGraph.addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        q2.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        q2.setParameter("ids", q1.getResultStream().collect(toList()));

        return q2.getResultStream();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveDocToValidate(long idStrut, int maxResult) {

        String queryStr = "SELECT docDaElab " + "FROM ElvDocAggDaElabElenco docDaElab "
                + "WHERE docDaElab.idStrut = :idStrut " + "AND docDaElab.tiStatoDocDaElab = :tiStatoDocDaElab "
                + "ORDER BY docDaElab.idDocAggDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        query.setMaxResults(maxResult);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoDocDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return query.getResultStream();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveDocToValidateInParallel(long idStrut, int maxResult) {

        String queryStr = "SELECT docDaElab " + "FROM ElvDocAggDaElabElenco docDaElab "
                + "WHERE docDaElab.idStrut = :idStrut " + "AND docDaElab.tiStatoDocDaElab = :tiStatoDocDaElab "
                + "ORDER BY docDaElab.idDocAggDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvDocAggDaElabElenco> entityGraph = em.createEntityGraph(ElvDocAggDaElabElenco.class);
        entityGraph.addSubgraph("aroDoc").addAttributeNodes("aroUnitaDoc");
        entityGraph.addSubgraph("aroDoc").addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        query.setParameter("idStrut", bigDecimalFromLong(idStrut));
        query.setParameter("tiStatoDocDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        return query.getResultStream().limit(maxResult);
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveDocToValidateInParallelLessThan1k(long idStrut, int maxResult) {

        // Get primary keys with LIMIT and OFFSET
        String queryStr = "SELECT docDaElab.idDocAggDaElabElenco " + "FROM ElvDocAggDaElabElenco docDaElab "
                + "WHERE docDaElab.idStrut = :idStrut " + "AND docDaElab.tiStatoDocDaElab = :tiStatoDocDaElab "
                + "ORDER BY docDaElab.idDocAggDaElabElenco";

        Query q1 = em.createQuery(queryStr);
        q1.setHint(QueryHints.HINT_READONLY, true);
        q1.setHint(QueryHints.HINT_CACHEABLE, true);
        q1.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        q1.setMaxResults(maxResult);

        q1.setParameter("idStrut", bigDecimalFromLong(idStrut));
        q1.setParameter("tiStatoDocDaElab", ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());

        // Get entities with associations
        queryStr = "SELECT docDaElab " + "FROM ElvDocAggDaElabElenco docDaElab "
                + "WHERE docDaElab.idDocAggDaElabElenco IN (:ids)";

        Query q2 = em.createQuery(queryStr);
        q2.setHint(QueryHints.HINT_READONLY, true);
        q2.setHint(QueryHints.HINT_CACHEABLE, true);
        q2.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvDocAggDaElabElenco> entityGraph = em.createEntityGraph(ElvDocAggDaElabElenco.class);
        entityGraph.addSubgraph("aroDoc").addAttributeNodes("aroUnitaDoc");
        entityGraph.addSubgraph("aroDoc").addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        q2.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        q2.setParameter("ids", q1.getResultStream().collect(toList()));

        return q2.getResultStream();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUpdToValidate(long idStrut, int maxResult) {

        String queryStr = "SELECT updDaElab " + "FROM ElvUpdUdDaElabElenco updDaElab "
                + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.tiStatoUpdElencoVers =  :tiStatoUpdElencoVers "
                + "ORDER BY updDaElab.idUpdUdDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        query.setMaxResults(maxResult);

        query.setParameter("idStrut", idStrut);
        query.setParameter("tiStatoUpdElencoVers", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);

        return query.getResultStream();
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUpdToValidateInParallel(long idStrut, int maxResult) {

        String queryStr = "SELECT updDaElab " + "FROM ElvUpdUdDaElabElenco updDaElab "
                + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.tiStatoUpdElencoVers =  :tiStatoUpdElencoVers "
                + "ORDER BY updDaElab.idUpdUdDaElabElenco";

        Query query = em.createQuery(queryStr);
        query.setHint(QueryHints.HINT_READONLY, true);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvUpdUdDaElabElenco> entityGraph = em.createEntityGraph(ElvUpdUdDaElabElenco.class);
        entityGraph.addSubgraph("aroUpdUnitaDoc").addAttributeNodes("aroUnitaDoc");
        entityGraph.addSubgraph("aroUpdUnitaDoc").addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        query.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        query.setParameter("idStrut", idStrut);
        query.setParameter("tiStatoUpdElencoVers", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);

        return query.getResultStream().limit(maxResult);
    }

    public Stream<ElvUdDocUpdDaElabElenco> retrieveUpdToValidateInParallelLessThan1k(long idStrut, int maxResult) {

        // Get primary keys with LIMIT and OFFSET
        String queryStr = "SELECT updDaElab.idUpdUdDaElabElenco " + "FROM ElvUpdUdDaElabElenco updDaElab "
                + "WHERE updDaElab.orgStrut.idStrut = :idStrut "
                + "AND updDaElab.tiStatoUpdElencoVers =  :tiStatoUpdElencoVers "
                + "ORDER BY updDaElab.idUpdUdDaElabElenco";

        Query q1 = em.createQuery(queryStr);
        q1.setHint(QueryHints.HINT_READONLY, true);
        q1.setHint(QueryHints.HINT_CACHEABLE, true);
        q1.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));
        q1.setMaxResults(maxResult);

        q1.setParameter("idStrut", idStrut);
        q1.setParameter("tiStatoUpdElencoVers", ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);

        // Get entities with associations
        queryStr = "SELECT updDaElab " + "FROM ElvUpdUdDaElabElenco updDaElab "
                + "WHERE updDaElab.idUpdUdDaElabElenco IN (:ids)";

        Query q2 = em.createQuery(queryStr);
        q2.setHint(QueryHints.HINT_READONLY, true);
        q2.setHint(QueryHints.HINT_CACHEABLE, true);
        q2.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("1000"));

        EntityGraph<ElvUpdUdDaElabElenco> entityGraph = em.createEntityGraph(ElvUpdUdDaElabElenco.class);
        entityGraph.addSubgraph("aroUpdUnitaDoc").addAttributeNodes("aroUnitaDoc");
        entityGraph.addSubgraph("aroUpdUnitaDoc").addSubgraph("aroUnitaDoc").addAttributeNodes("aroDocs");
        q2.setHint(JAVAX_PERSISTENCE_FETCHGRAPH, entityGraph);

        q2.setParameter("ids", q1.getResultStream().collect(toList()));

        return q2.getResultStream();
    }

    /**
     * Stream delle unità documentarie, dei documenti aggiunti e degli aggiornamenti metadati che soddisfano il criterio
     * di raggruppamento passato come parametro
     *
     * @param criterio
     *            raggruppamento
     * @param maxResult
     *            rownum
     *
     * @return stream di oggetti di tipo {@link ElvVSelUdDocUpdByCrit}
     */
    public Stream<ElvVSelUdDocUpdByCrit> streamUpdDocUdToProcess(DecCriterioRaggr criterio, int maxResult) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM ElvVSelUdDocUpdByCrit u " + "WHERE u.id.idCriterioRaggr = :idCriterio");

        if (criterio.getAaKeyUnitaDoc() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            queryStr.append(" AND u.aaKeyUnitaDoc >= :aaKeyUnitaDocDa ");
            queryStr.append(" AND u.aaKeyUnitaDoc <= :aaKeyUnitaDocA ");
        }
        // tip: fdilorenzo, definisce l'ordinamento con cui devono essere elaborati gli oggetti versati (a supporto
        // della logica definita in analisi)
        queryStr.append(" ORDER BY u.tiEle");

        Query q = em.createQuery(queryStr.toString());
        q.setHint(QueryHints.HINT_READONLY, true);
        q.setHint(QueryHints.HINT_CACHEABLE, true);
        q.setHint(QueryHints.HINT_FETCH_SIZE, Integer.valueOf("100"));
        q.setParameter("idCriterio", BigDecimal.valueOf(criterio.getIdCriterioRaggr()));

        if (criterio.getAaKeyUnitaDoc() != null) {
            q.setParameter("aaKeyUnitaDoc", criterio.getAaKeyUnitaDoc());
        } else if (criterio.getAaKeyUnitaDocDa() != null || criterio.getAaKeyUnitaDocA() != null) {
            if (criterio.getAaKeyUnitaDocDa() != null) {
                q.setParameter("aaKeyUnitaDocDa", criterio.getAaKeyUnitaDocDa());
            } else {
                q.setParameter("aaKeyUnitaDocDa", BigDecimal.valueOf(2000));
            }
            if (criterio.getAaKeyUnitaDocA() != null) {
                q.setParameter("aaKeyUnitaDocA", criterio.getAaKeyUnitaDocA());
            } else {
                q.setParameter("aaKeyUnitaDocA", BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            }
        }

        return q.getResultStream();
    }
    // end MEV#27891

    public long retrieveUserIdByUsername(String username) {
        IamUser user;
        Query q = em.createQuery("select usr from IamUser usr where usr.nmUserid = :username");
        q.setParameter("username", username);
        try {
            user = (IamUser) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new EJBException("Errore nel reperimento dell'utente: non esiste l'utente '" + username + "'");
        }
        return user.getIdUserIam();
    }

    public AroUnitaDoc retrieveUnitaDocById(long idUnitaDoc) {
        return em.find(AroUnitaDoc.class, idUnitaDoc);
    }

    public AroUnitaDoc retrieveAndLockUnitaDocById(long idUnitaDoc) {
        return em.find(AroUnitaDoc.class, idUnitaDoc, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lockElenco(ElvElencoVer elenco) {
        em.lock(elenco, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lockUnitaDoc(AroUnitaDoc ud) {
        em.lock(ud, LockModeType.PESSIMISTIC_WRITE);
    }

    public AroDoc retrieveDocById(long idDoc) {
        return em.find(AroDoc.class, idDoc);
    }

    public AroUpdUnitaDoc retrieveUpdById(long idUpdUnitaDoc) {
        return em.find(AroUpdUnitaDoc.class, idUpdUnitaDoc);
    }

    public void lockDoc(AroDoc doc) {
        em.lock(doc, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lockUpd(AroUpdUnitaDoc upd) {
        em.lock(upd, LockModeType.PESSIMISTIC_WRITE);
    }

    public AroCompDoc retrieveCompDocById(long idCompDoc) {
        return em.find(AroCompDoc.class, idCompDoc);

    }

    public void flush() {
        em.flush();
    }

    public void detachAroDoc(AroDoc doc) {
        em.detach(doc);
    }

    // MAC#28020
    public boolean containsElenco(ElvElencoVer elenco) {
        return em.contains(elenco);
    }
    // end MAC#28020

    public DecCriterioRaggr retrieveCriterioByid(long idCriterio) {
        return em.find(DecCriterioRaggr.class, idCriterio);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atomicSetNonElabSched(OrgStrut struttura, LogJob logJob) {
        setNonElabSched(struttura, logJob);
    }

    /**
     * Registro in AroIndiceAipUdDaElab. Se il parametro "primo" è a true viene impostato pg_creazione_da_elab a 1. Alla
     * selec max + 1 altrimenti.
     *
     * @param unitaDoc
     *            - id unità documentaria
     * @param idElenco
     *            - id dell'elenco
     * @param dataCreazione
     *            - data di firma dell'elenco corrente
     * @param hasDocumentiAggiunti
     *            - true se ci sono documenti aggiunti
     *
     * @return entità AroIndiceAipUdDaElab
     */
    public AroIndiceAipUdDaElab registraInAroIndiceAipUdDaElab(AroUnitaDoc unitaDoc, long idElenco, Date dataCreazione,
            boolean hasDocumentiAggiunti) {
        AroIndiceAipUdDaElab aroIndiceAipUdDaElab = new AroIndiceAipUdDaElab();
        aroIndiceAipUdDaElab.setAroUnitaDoc(unitaDoc);
        aroIndiceAipUdDaElab.setDtCreazioneDaElab(dataCreazione);
        aroIndiceAipUdDaElab.setDsCausale(
                "Completamento dei controlli sul SIP preso in carico, attestato mediante validazione dell\'Elenco di versamento "
                        + idElenco);
        aroIndiceAipUdDaElab.setTiCreazione("ANTICIPATO");
        if (hasDocumentiAggiunti) {
            aroIndiceAipUdDaElab.setPgCreazioneDaElab(new BigDecimal(calcPgAroIndiceAipUdDaElab(unitaDoc)));
        } else {
            aroIndiceAipUdDaElab.setPgCreazioneDaElab(BigDecimal.ONE);
        }
        aroIndiceAipUdDaElab.setElvElencoVer(em.find(ElvElencoVer.class, idElenco));
        aroIndiceAipUdDaElab.setFlInCoda("0");
        em.persist(aroIndiceAipUdDaElab);
        return aroIndiceAipUdDaElab;
    }

    private long calcPgAroIndiceAipUdDaElab(AroUnitaDoc ud) {
        long retVal = 1;
        Query q = em.createQuery("select max(t.pgCreazioneDaElab) " + "from AroIndiceAipUdDaElab t "
                + "where t.aroUnitaDoc.idUnitaDoc = :idUd");
        q.setParameter("idUd", ud.getIdUnitaDoc());
        BigDecimal totale = (BigDecimal) q.getSingleResult();
        if (totale != null) {
            retVal = totale.longValue() + 1;
        }
        return retVal;
    }

    /**
     * Registra su AroCompIndiceAipDaElab.
     *
     * @param idCompDoc
     *            - id di AroCompDoc
     * @param aroIndiceAipUdDaElab
     *            - entity AroIndiceAipUdDaElab
     *
     * @return aroCompIndiceAipDaElab entità salvata
     */
    public AroCompIndiceAipDaElab registraInAroCompIndiceAipUdDaElab(long idCompDoc,
            AroIndiceAipUdDaElab aroIndiceAipUdDaElab) {
        AroCompIndiceAipDaElab aroCompIndiceAipDaElab = new AroCompIndiceAipDaElab();
        AroCompDoc aroCompDoc = em.find(AroCompDoc.class, idCompDoc);
        aroCompIndiceAipDaElab.setAroCompDoc(aroCompDoc);
        aroCompIndiceAipDaElab.setAroIndiceAipUdDaElab(aroIndiceAipUdDaElab);
        em.persist(aroCompIndiceAipDaElab);
        return aroCompIndiceAipDaElab;
    }

    /**
     * Registra su AroUpdUdIndiceAipDaElab.
     *
     * @param idUpdUnitaDoc
     *            - id di AroUpdUnitaDoc
     * @param aroIndiceAipUdDaElab
     *            - entity AroIndiceAipUdDaElab
     *
     * @return aroUpdUdIndiceAipDaElab entità salvata
     */
    public AroUpdUdIndiceAipDaElab registraInAroUpdUdIndiceAipUdDaElab(long idUpdUnitaDoc,
            AroIndiceAipUdDaElab aroIndiceAipUdDaElab) {
        AroUpdUdIndiceAipDaElab aroUpdUdIndiceAipDaElab = new AroUpdUdIndiceAipDaElab();
        AroUpdUnitaDoc aroUpdUnitaDoc = em.find(AroUpdUnitaDoc.class, idUpdUnitaDoc);
        aroUpdUdIndiceAipDaElab.setAroUpdUnitaDoc(aroUpdUnitaDoc);
        aroUpdUdIndiceAipDaElab.setAroIndiceAipUdDaElab(aroIndiceAipUdDaElab);
        em.persist(aroUpdUdIndiceAipDaElab);
        return aroUpdUdIndiceAipDaElab;
    }

    public boolean checkUdAnnullataByElenco(ElvElencoVer elenco) {
        boolean annullata = false;
        Query q = em.createQuery("select ud " + "from ElvVChkUnaUdAnnul ud " + "where ud.idElencoVers = :idElv");
        q.setParameter("idElv", BigDecimal.valueOf(elenco.getIdElencoVers()));
        ElvVChkUnaUdAnnul unaUdAnnul = (ElvVChkUnaUdAnnul) q.getSingleResult();
        if ("1".equals(unaUdAnnul.getFlUnaUdVersAnnul()) || "1".equals(unaUdAnnul.getFlUnaUdDocAggAnnul())
                || "1".equals(unaUdAnnul.getFlUnaUdUpdUdAnnul())) {
            annullata = true;
        }
        return annullata;
    }

    public boolean checkUdAnnullata(AroUnitaDoc ud) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean annullata = false;
        Date dataAnnullamento;
        Date defaultAnnullamento = sdf.parse("2444-12-31");
        Query q = em.createQuery("select ud.dtAnnul " + "from AroUnitaDoc ud " + "where ud.idUnitaDoc = :idUd");
        q.setParameter("idUd", ud.getIdUnitaDoc());
        dataAnnullamento = (java.util.Date) q.getSingleResult();
        if (dataAnnullamento.getTime() != defaultAnnullamento.getTime()) {
            annullata = true;
        }
        LOG.debug("Unità documentaria: '{}' annullata: {}", ud.getIdUnitaDoc(), annullata);
        return annullata;
    }

    public boolean checkDocAnnullato(AroDoc doc) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean annullato = false;
        Date dataAnnullamento;
        Date defaultAnnullamento = sdf.parse("2444-12-31");
        Query q = em.createQuery("select doc.dtAnnul " + "from AroDoc doc " + "where doc.idDoc = :idDoc");
        q.setParameter("idDoc", doc.getIdDoc());
        dataAnnullamento = (Date) q.getSingleResult();
        if (dataAnnullamento.getTime() != defaultAnnullamento.getTime()) {
            annullato = true;
        }
        LOG.debug("Documento: '{}' annullato: {}", doc.getIdDoc(), annullato);
        return annullato;
    }

    public boolean checkFreeSpaceElenco(ElvElencoVer elenco, long numComps) {
        boolean udOk = false;
        long freeSpace = elenco.getNiMaxComp().longValue() - (elenco.getNiCompVersElenco().longValue()
                + elenco.getNiCompAggElenco().intValue() + elenco.getNiUpdUnitaDoc().intValue());
        if (numComps <= freeSpace) {
            udOk = true;
        }
        LOG.debug(
                "Ok = {} Num comp da inserire: {}; spazio libero: {} --> NiMaxComp: {}, NiCompVersElenco: {}, NiCompAggElenco: {}, NiUpdUnitaDoc: {}",
                udOk, numComps, freeSpace, elenco.getNiMaxComp().intValue(), elenco.getNiCompVersElenco().intValue(),
                elenco.getNiCompAggElenco().intValue(), elenco.getNiUpdUnitaDoc().intValue());
        return udOk;
    }

    /**
     * Ottiene gli elenchi allo stato passato come parametro. Gli elenchi sono ordinati in modo che si elaborano prima i
     * fiscali, per anno del contenuto descending e per data di firma e poi i non fiscali.
     *
     * @param idStrut
     *            id della struttura
     * @param statoElenco
     *            stato dell'elenco {@link it.eng.parer.elencoVersamento.utils.ElencoEnums}
     *
     * @return lista di elenchi da elaborare.
     */
    public List<ElvElencoVersDaElab> retrieveElenchi(long idStrut, ElencoEnums.ElencoStatusEnum... statoElenco) {
        TypedQuery<ElvElencoVersDaElab> q = em.createQuery(
                "SELECT elencoDaElab " + "FROM ElvElencoVersDaElab elencoDaElab JOIN FETCH elencoDaElab.elvElencoVer "
                        + "WHERE elencoDaElab.tiStatoElenco IN (:statiElenco) " + "AND elencoDaElab.idStrut = :idStrut "
                        + "ORDER BY elencoDaElab.elvElencoVer.flElencoFisc DESC, " + "elencoDaElab.aaKeyUnitaDoc DESC, "
                        + "elencoDaElab.elvElencoVer.dtFirmaIndice",
                ElvElencoVersDaElab.class);
        List<String> statiList = Arrays.stream(statoElenco).map(Enum::name).collect(Collectors.toList());
        q.setParameter("statiElenco", statiList);
        q.setParameter("idStrut", bigDecimalFromLong(idStrut));

        return q.getResultList();
    }

    /**
     * Ottieni l'insieme delle unità documentarie appartenenti all'elenco. L'elenco contiene sia le unità versate sia i
     * documenti aggiunti sia gli aggiornamenti metadati il cui stato relativo all'elenco sia pari a quello passato in
     * input
     *
     * @param idElenco
     *            id dell'elenco validato.
     * @param stato
     *            valore stato
     * @param numGiorni
     *            numero giorni
     *
     * @return Set - insieme di id <strong>distinti</strong>
     */
    public List<BigDecimal> retrieveUdVersOrAggOrUpdInElencoValidate(long idElenco, String stato, int numGiorni) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numGiorni);
        Date dateToLookBefore = calendar.getTime();

        Query q = em.createQuery("SELECT DISTINCT e.idUnitaDoc FROM ElvVLisUdByStato e "
                + "WHERE e.idElencoVers = :idElenco AND e.tiStatoUd = :stato "
                + "AND (e.tsLastResetStato IS NULL OR e.tsLastResetStato < :dateToLookBefore) ");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        q.setParameter("stato", stato);
        q.setParameter("dateToLookBefore", dateToLookBefore);

        return q.getResultList();
    }

    /**
     * Ottieni l'insieme delle unità documentarie appartenenti all'elenco. L'elenco contiene sia le unità versate sia i
     * documenti aggiunti il cui stato relativo all'elenco sia pari a IN_ELENCO_VALIDATO
     *
     * @param idElenco
     *            id dell'elenco validato.
     *
     * @return Set - insieme di id <strong>distinti</strong>
     */
    public Collection<Long> retrieveUdVersOrAggInElencoValidate(long idElenco) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select ud.id_unita_doc from ARO_UNITA_DOC ud where ud.id_elenco_vers = <elenco corrente> and ud.dt_annul =
         * '31/12/2444' and ud.ti_stato_ud.elenco_vers = 'IN_ELENCO_VALIDATO' UNION -- serve per ottenere una sola volta
         * un id_unita_doc select distinct doc.id_unita_doc from ARO_DOC doc where doc.id_elenco_vers = <elenco
         * corrente> and doc.dt_annul = '31/12/2444' and doc.ti_stato_doc_elenco_vers = 'IN_ELENCO_VALIDATO'
         */
        // </editor-fold>
        TypedQuery<Long> q1 = em.createQuery(
                "SELECT ud.idUnitaDoc " + "FROM AroUnitaDoc ud " + " WHERE ud.elvElencoVer.idElencoVers = :idElenco "
                        + " AND ud.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                        + " AND ud.tiStatoUdElencoVers = 'IN_ELENCO_VALIDATO' ",
                Long.class);

        TypedQuery<Long> q2 = em.createQuery("SELECT DISTINCT doc.aroUnitaDoc.idUnitaDoc " + "FROM AroDoc doc "
                + " WHERE doc.elvElencoVer.idElencoVers = :idElenco "
                + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')"
                + " AND doc.tiStatoDocElencoVers = 'IN_ELENCO_VALIDATO' ", Long.class);
        q1.setParameter("idElenco", idElenco);
        q2.setParameter("idElenco", idElenco);
        List<Long> l1 = q1.getResultList();
        List<Long> l2 = q2.getResultList();
        Set<Long> hs = new HashSet<>();
        hs.addAll(l1);
        hs.addAll(l2);
        return hs;
    }

    /**
     * Componenti dell'unità doc corrente, inclusi nell'elenco di versamento corrente sia come ud versate sia come doc
     * aggiunti.
     *
     * @param idElenco
     *            idElenco
     * @param idUd
     *            idUnitaDoc
     *
     * @return Set - Insieme di elementi distint
     */
    public Collection<ComponenteDaVerificare> retrieveCompsToVerify(long idElenco, long idUd) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select comp.id_comp_doc, comp.FL_COMP_FIRMATO from ARO_UNITA_DOC ud join ARO_DOC doc on (doc.id_unita_doc =
         * ud.id_unita_doc and doc.ti_creazione = 'VERSAMENTO_UNITA_DOC') join ARO_STRUT_DOC strut_doc on
         * (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on (comp.id_strut_doc = strut_doc. id_strut_doc) where
         * ud.id_unita_doc = <unità doc corrente> and ud.id_elenco_vers = <elenco corrente> and doc.dt_annul =
         * '31/12/2444' and (comp.FL_COMP_FIRMATO = ‘0’ or (comp.FL_COMP_FIRMATO = ‘1’ and not exists (select * from
         * aro_firma_comp firma join aro_verif_firma_dt_vers verif on (verif.id_firma_comp = firma.id_firma_comp) where
         * firma.id_comp_doc = comp.id_comp_doc ) )) UNION select comp.id_comp_doc, comp.FL_COMP_FIRMATO from ARO_DOC
         * doc join ARO_STRUT_DOC strut_doc on (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on
         * (comp.id_strut_doc = strut_doc. id_strut_doc) where doc.id_unita_doc = <unità doc corrente> and
         * doc.id_elenco_vers = <elenco corrente> and doc.ti_creazione = 'AGGIUNTA_DOCUMENTO' and doc.dt_annul =
         * '31/12/2444' and (comp.FL_COMP_FIRMATO = ‘0’ or (comp.FL_COMP_FIRMATO = ‘1’ and not exists (select * from
         * aro_firma_comp firma join aro_verif_firma_dt_vers verif on (verif.id_firma_comp = firma.id_firma_comp) where
         * firma.id_comp_doc = comp.id_comp_doc ) ))
         */
        // </editor-fold>
        TypedQuery<ComponenteDaVerificare> q1 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare"
                        + "(comp.idCompDoc, ud.dtCreazione, comp.flCompFirmato) " + "FROM AroCompDoc comp "
                        + "JOIN comp.aroStrutDoc strutDoc " + "JOIN strutDoc.aroDoc doc " + "JOIN doc.aroUnitaDoc ud "
                        + "WHERE ud.idUnitaDoc = :idUd " + "AND doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
                        + "AND ud.elvElencoVer.idElencoVers = :idElenco "
                        + "AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') " + "AND ((comp.flCompFirmato = '0') "
                        + "OR (comp.flCompFirmato = '1' "
                        + "AND NOT EXISTS (SELECT verif FROM AroVerifFirmaDtVer verif "
                        + "JOIN verif.aroFirmaComp firma " + "WHERE firma.aroCompDoc.idCompDoc = comp.idCompDoc))) ",
                ComponenteDaVerificare.class);

        TypedQuery<ComponenteDaVerificare> q2 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare"
                        + "(comp.idCompDoc, doc.dtCreazione, comp.flCompFirmato)" + "FROM AroCompDoc comp "
                        + "JOIN comp.aroStrutDoc strutDoc " + "JOIN strutDoc.aroDoc doc "
                        + "WHERE doc.aroUnitaDoc.idUnitaDoc = :idUd " + "AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                        + "AND doc.elvElencoVer.idElencoVers = :idElenco "
                        + "AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') " + "AND ((comp.flCompFirmato = '0') "
                        + "OR (comp.flCompFirmato = '1' "
                        + "AND NOT EXISTS (SELECT verif FROM AroVerifFirmaDtVer verif "
                        + "JOIN verif.aroFirmaComp firma " + "WHERE firma.aroCompDoc.idCompDoc = comp.idCompDoc))) ",
                ComponenteDaVerificare.class);
        q1.setParameter("idElenco", idElenco);
        q1.setParameter("idUd", idUd);
        q2.setParameter("idElenco", idElenco);
        q2.setParameter("idUd", idUd);
        List<ComponenteDaVerificare> l1 = q1.getResultList();
        List<ComponenteDaVerificare> l2 = q2.getResultList();
        Set<ComponenteDaVerificare> hs = new HashSet<>();
        hs.addAll(l1);
        hs.addAll(l2);
        return hs;

    }

    /**
     * Insieme distinto di unità documentarie appartenenti all'elenco.
     *
     * @param idElencoVers
     *            id dell'elenco
     *
     * @return insieme di id Ud distinti.
     */
    public Set<UnitaDocumentariaInElenco> retrieveUdInElenco(long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select ud.id_unita_doc, '0' fl_solo_doc_aggiunti from ARO_UNITA_DOC ud where ud.id_elenco_vers = <elenco
         * corrente> and ud.dt_annul = '31/12/2444' and ud.ti_stato_ud_elenco_vers =
         * IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS UNION -- serve per ottenere una sola volta un id_unita_doc select
         * distinct doc.id_unita_doc, '1' fl_solo_doc_aggiunti from ARO_DOC doc where doc.id_elenco_vers = <elenco
         * corrente> and doc.dt_annul = '31/12/2444' and doc.ti_stato_doc_elenco_vers =
         * IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS and not exists (select * from ARO_UNITA_DOC ud_vers where
         * ud.id_elenco_vers = doc.id_elenco_vers and ud_vers.id_unita_doc = doc.id_unita_doc)
         */
        // </editor-fold>
        TypedQuery<UnitaDocumentariaInElenco> q1 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco (ud.idUnitaDoc, false) "
                        + " FROM AroUnitaDoc ud " + " WHERE ud.elvElencoVer.idElencoVers = :idElencoVers "
                        + " AND ud.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                        + " AND ud.tiStatoUdElencoVers = :firmeVerificate ",
                UnitaDocumentariaInElenco.class);
        q1.setParameter("idElencoVers", idElencoVers);
        q1.setParameter("firmeVerificate", ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name());

        TypedQuery<UnitaDocumentariaInElenco> q2 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco (doc.aroUnitaDoc.idUnitaDoc, true) "
                        + " FROM AroDoc doc " + " WHERE doc.elvElencoVer.idElencoVers = :idElencoVers "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                        + " AND doc.tiStatoDocElencoVers = :firmeVerificate " + " AND NOT EXISTS (  "
                        + "     SELECT udVers " + "       FROM AroUnitaDoc udVers "
                        + "       WHERE udVers.elvElencoVer.idElencoVers = doc.elvElencoVer.idElencoVers"
                        + "       AND  udVers.idUnitaDoc = doc.aroUnitaDoc.idUnitaDoc ) ",
                UnitaDocumentariaInElenco.class);
        q2.setParameter("idElencoVers", idElencoVers);
        q2.setParameter("firmeVerificate", ElencoEnums.DocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name());

        Set<UnitaDocumentariaInElenco> result = new HashSet<>();
        result.addAll(q1.getResultList());
        result.addAll(q2.getResultList());

        return result;
    }

    /**
     * Unità documentaria non annullata appartenente all’elenco, il cui stato relativo all'elenco sia pari a
     * IN_CODA_JMS_INDICE_AIP_DA_ELAB
     *
     * @param idElenco
     *            id dell'elenco
     * @param idUd
     *            id dell'unità documentaria
     *
     * @return insieme di id Ud distinti.
     */
    public ElvVLisUdByStato retrieveUdInElencoByStato(long idUd, long idElenco) {
        Query q = em.createQuery("SELECT DISTINCT e " + "FROM ElvVLisUdByStato e " + "WHERE e.idElencoVers = :idElenco "
                + "AND e.idUnitaDoc = :idUd " + "AND e.tiStatoUd = 'IN_CODA_JMS_INDICE_AIP_DA_ELAB' ");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        q.setParameter("idUd", bigDecimalFromLong(idUd));
        return (ElvVLisUdByStato) q.getSingleResult();
    }

    public boolean checkStatoElencoUdPerLeFasi(long idUd, long idElenco, String statoElencoUd) {
        boolean statiOk = false;
        Query q = em.createQuery("select e " + "from ElvVLisUdByStato e " + "where e.idElencoVers = :idElenco "
                + "and e.idUnitaDoc = :idUd");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        q.setParameter("idUd", bigDecimalFromLong(idUd));
        ElvVLisUdByStato udInElenco = (ElvVLisUdByStato) q.getSingleResult();
        if (statoElencoUd.equals(udInElenco.getTiStatoElencoVers())
                && statoElencoUd.equals(udInElenco.getTiStatoUd())) {
            statiOk = true;
        }
        return statiOk;
    }

    public boolean checkStatoAllUdInElencoPerLeFasi(long idElenco, String... stato) {
        boolean statiOk = false;
        Query q = em.createQuery("select count(e) " + "from ElvVLisUdByStato e " + "where e.idElencoVers = :idElenco "
                + "and not exists (select e1 from ElvVLisUdByStato e1 where e1.idElencoVers = e.idElencoVers and e1.tiStatoUd NOT IN (:stato))");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        q.setParameter("stato", Arrays.asList(stato));
        long numUdInElenco = ((Long) q.getSingleResult());
        if (numUdInElenco > 0L) {
            statiOk = true;
        }

        return statiOk;
    }

    // MEV#26288
    public boolean checkStatoAllUdInElencoPerVerificaFirmeDtVers(long idElenco, String... stato) {
        boolean statiOk = false;
        Query q = em.createQuery("select e " + "from ElvVLisUdByStato e " + "where e.idElencoVers = :idElenco "
                + "and e.tiStatoElencoVers = 'IN_CODA_JMS_VERIFICA_FIRME_DT_VERS' "
                + "and not exists (select e1 from ElvVLisUdByStato e1 where e1.idElencoVers = e.idElencoVers and e1.tiStatoUd NOT IN (:stato))");
        q.setParameter("idElenco", BigDecimal.valueOf(idElenco));
        q.setParameter("stato", Arrays.asList(stato));
        List<ElvVLisUdByStato> udInElencoList = q.getResultList();
        if (!udInElencoList.isEmpty()) {
            statiOk = true;
        }

        return statiOk;
    }

    public boolean checkStatoAllUdInElencoPerCodaIndiceAipDaElab(long idElenco, String... stato) {
        boolean statiOk = false;
        Query q = em.createQuery("select e " + "from ElvVLisUdByStato e " + "where e.idElencoVers = :idElenco "
                + "and e.tiStatoElencoVers = 'IN_CODA_JMS_INDICE_AIP_DA_ELAB' "
                + "and not exists (select e1 from ElvVLisUdByStato e1 where e1.idElencoVers = e.idElencoVers and e1.tiStatoUd NOT IN :stato)");
        q.setParameter("idElenco", HibernateUtils.bigDecimalFrom(idElenco));
        q.setParameter("stato", Arrays.asList(stato));
        List<ElvVLisUdByStato> udInElencoList = q.getResultList();
        if (!udInElencoList.isEmpty()) {
            statiOk = true;
        }

        return statiOk;
    }
    // end MEV#26288

    /**
     * (b) il sistema determina i componenti dell'unità doc presenti nell'elenco (sia quelli derivanti dal versamento
     * dell'unità doc, che quelli derivanti da aggiunta doc presenti nell'elenco)
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return insieme di componenti in elenco distinti
     */
    public Set<ComponenteInElenco> retrieveComponentiInElenco(long idUnitaDoc, long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select comp.id_comp_doc, comp.ds_urn_comp_calc from ARO_UNITA_DOC ud join ARO_DOC doc on (doc.id_unita_doc =
         * ud.id_unita_doc and doc.ti_creazione = 'VERSAMENTO_UNITA_DOC') join ARO_STRUT_DOC strut_doc on
         * (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on (comp.id_strut_doc = strut_doc. id_strut_doc) where
         * ud.id_unita_doc = <unità doc corrente> and ud.id_elenco_vers = <elenco corrente> and doc.dt_annul =
         * '31/12/2444'
         * 
         * UNION
         * 
         * select comp.id_comp_doc, comp.ds_urn_comp_calc from ARO_DOC doc join ARO_STRUT_DOC strut_doc on
         * (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on (comp.id_strut_doc = strut_doc. id_strut_doc) where
         * doc.id_unita_doc = <unità doc corrente> and doc.id_elenco_vers = <elenco corrente> and doc.ti_creazione =
         * 'AGGIUNTA_DOCUMENTO' and doc.dt_annul = '31/12/2444'
         */
        // </editor-fold>
        TypedQuery<ComponenteInElenco> q1 = em
                .createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteInElenco(comp.idCompDoc) "
                        + " FROM AroCompDoc comp JOIN comp.aroStrutDoc strut JOIN strut.aroDoc doc JOIN doc.aroUnitaDoc ud "
                        + " WHERE doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' " + " AND ud.idUnitaDoc = :idUnitaDoc "
                        + " AND ud.elvElencoVer.idElencoVers = :idElencoVers"
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') ", ComponenteInElenco.class);
        q1.setParameter("idUnitaDoc", idUnitaDoc);
        q1.setParameter("idElencoVers", idElencoVers);
        TypedQuery<ComponenteInElenco> q2 = em
                .createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteInElenco(comp.idCompDoc) "
                        + " FROM AroCompDoc comp JOIN comp.aroStrutDoc strut JOIN strut.aroDoc doc"
                        + " WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + " AND doc.elvElencoVer.idElencoVers = :idElencoVers"
                        + " AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')  ", ComponenteInElenco.class);

        q2.setParameter("idUnitaDoc", idUnitaDoc);
        q2.setParameter("idElencoVers", idElencoVers);

        Set<ComponenteInElenco> result = new HashSet<>();
        result.addAll(q1.getResultList());
        result.addAll(q2.getResultList());

        return result;
    }

    /**
     * il sistema determina gli aggiornamenti metadati dell'unità doc presenti nell'elenco
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return insieme di componenti in elenco distinti
     */
    public Set<AggiornamentoInElenco> retrieveAggiornamentiInElenco(long idUnitaDoc, long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select id_upd_unita_doc, pg_upd_unita_doc from ARO_UPD_UNITA_DOC upd join ARO_UNITA_DOC ud on
         * (ud.id_unita_doc = upd.id_unita_doc) where upd.id_unita_doc = <unità doc corrente> and ud.dt_annul =
         * ‘31/12/2444’ and upd.id_elenco_vers = <elenco corrente>
         */
        // </editor-fold>
        TypedQuery<AggiornamentoInElenco> query = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco(upd.idUpdUnitaDoc, upd.pgUpdUnitaDoc) "
                        + " FROM AroUpdUnitaDoc upd " + " WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + " AND upd.elvElencoVer.idElencoVers = :idElencoVers "
                        + " AND upd.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') ",
                AggiornamentoInElenco.class);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idElencoVers", idElencoVers);

        Set<AggiornamentoInElenco> result = new HashSet<>();
        result.addAll(query.getResultList());

        return result;
    }

    /**
     * Data minima dei documenti dell'unità documentaria nell'elenco.
     *
     * @param idUnitaDoc
     *            unità documentaria
     * @param idElencoVers
     *            elenco versamento
     *
     * @return data minima dei documenti in elenco oppure null
     */
    public Date getDataMinimaDocInElenco(long idUnitaDoc, long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select min (dt_creazione) from ARO_DOC doc where doc.id_unita_doc = <unità doc corrente> and
         * doc.id_elenco_vers = <elenco corrente> and doc.ti_creazione = 'AGGIUNTA_DOCUMENTO' and doc.dt_annul =
         * '31/12/2444'
         */
        // </editor-fold>
        TypedQuery<Date> query = em.createQuery("SELECT MIN(doc.dtCreazione) " + " FROM AroDoc doc "
                + " WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + " AND doc.elvElencoVer.idElencoVers = :idElencoVers " + " AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')", Date.class);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idElencoVers", idElencoVers);
        Date minDate = null;
        try {
            minDate = query.getSingleResult();
        } catch (NoResultException e) {
            LOG.debug("Nessun risultato per la data minima", e);
        }
        return minDate;
    }

    /**
     * (b) il sistema determina i documenti dell'unità doc, non in elenco o in un elenco diverso da quello corrente e
     * con stato diverso da IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI,
     * IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
     * IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO, versati in data inferiore alla data di creazione
     * minima dei documenti dell'unità doc presenti nell'elenco.
     *
     * @param idUnitaDocCorrente
     *            id unita doc corrente
     * @param dataCreazioneMinima
     *            data creazione minima
     * @param idElencoVersCorrente
     *            id elenco versamento corrente
     *
     * @return lista di elementi DISTINTI
     */
    public Set<Long> retrieveDocNonInElenco(long idUnitaDocCorrente, Date dataCreazioneMinima,
            long idElencoVersCorrente) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select doc.id_doc from ARO_UNITA_DOC ud join ARO_DOC doc on (doc.id_unita_doc = ud.id_unita_doc and
         * doc.ti_creazione = 'VERSAMENTO_UNITA_DOC') where ud.id_unita_doc = <unità doc corrente> and doc.dt_annul =
         * '31/12/2444' and ud.dt_creazione < <data creazione minima>
         * 
         * and (ud.id_elenco_vers is null or (ud.id_elenco_vers is not nulland ud.id_elenco_vers != <elenco corrente>)
         * and ud.ti_stato_ud_elenco_vers != IN_ELENCO_IN_CODA_INDICE_AIP))
         * 
         * UNION
         * 
         * select doc.id_doc from ARO_DOC doc where doc.id_unita_doc = <unità doc corrente> and doc.ti_creazione =
         * 'AGGIUNTA_DOCUMENTO' and doc.dt_annul = '31/12/2444' and doc.dt_creazione < <data creazione minima>
         * 
         * and (doc.id_elenco_vers is null or (doc.id_elenco_vers is not null and doc.id_elenco_vers != <elenco
         * corrente>) and doc.ti_stato_doc_elenco_vers != IN_ELENCO_IN_CODA_INDICE_AIP))
         */
        // </editor-fold>

        TypedQuery<Long> q1 = em.createQuery("SELECT doc.idDoc " + " FROM AroDoc doc JOIN doc.aroUnitaDoc ud"
                + " WHERE doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' " + " AND ud.idUnitaDoc = :idUnitaDocCorrente "
                + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                /*
                 * Non dobbiamo valutare la data minima per l'ud perché deve essere già entrata (e potrebbe essere la
                 * stessa dei documenti aggiunti) come accaduto nel caso della mac #11925
                 */
                // + " AND ud.dtCreazione < :dataCreazioneMinima"
                // l'UNITA DOCUMENTARIA non è associata ad un elenco oppure l'elenco è diverso
                // da quello attuale
                + " AND (ud.elvElencoVer IS NULL "
                + "      OR (ud.elvElencoVer IS NOT NULL AND ud.elvElencoVer.idElencoVers != :idElencoVersCorrente AND ud.tiStatoUdElencoVers NOT IN ('IN_ELENCO_IN_CODA_INDICE_AIP', 'IN_ELENCO_CON_INDICI_AIP_GENERATI', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA', 'IN_ELENCO_COMPLETATO'))"
                + "      ) ", Long.class);
        q1.setParameter("idUnitaDocCorrente", idUnitaDocCorrente);
        q1.setParameter("idElencoVersCorrente", idElencoVersCorrente);

        TypedQuery<Long> q2 = em.createQuery(
                "SELECT doc.idDoc " + " FROM AroDoc doc " + " WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDocCorrente "
                        + " AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                        + " AND doc.dtCreazione < :dataCreazioneMinima"
                        // il DOCUMENTO non è associato ad un elenco oppure l'elenco è diverso da quello
                        // attuale
                        + " AND ( doc.elvElencoVer IS NULL "
                        + "       OR (doc.elvElencoVer IS NOT NULL AND  doc.elvElencoVer.idElencoVers != :idElencoVersCorrente AND doc.tiStatoDocElencoVers NOT IN ('IN_ELENCO_IN_CODA_INDICE_AIP', 'IN_ELENCO_CON_INDICI_AIP_GENERATI', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA', 'IN_ELENCO_COMPLETATO')) "
                        + "     ) ",
                Long.class);
        q2.setParameter("idUnitaDocCorrente", idUnitaDocCorrente);
        q2.setParameter("dataCreazioneMinima", dataCreazioneMinima);
        q2.setParameter("idElencoVersCorrente", idElencoVersCorrente);

        Set<Long> result = new HashSet<>();
        result.addAll(q1.getResultList());
        result.addAll(q2.getResultList());

        return result;
    }

    /**
     * (c) Progressivo minimo degli aggiornamenti metadati dell'unità documentaria nell'elenco.
     *
     * @param idUnitaDoc
     *            unità documentaria
     * @param idElencoVers
     *            elenco versamento
     *
     * @return pg minimo degli aggiornamenti in elenco oppure null
     */
    public BigDecimal getPgMinimoUpdInElenco(long idUnitaDoc, long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select min (pg_upd_unita_doc) from ARO_UPD_UNITA_DOC upd join ARO_UNITA_DOC ud on (ud.id_unita_doc =
         * upd.id_unita_doc) where upd.id_unita_doc = <unità doc corrente> and upd.id_elenco_vers = <elenco corrente>
         * and upd.dt_annul = '31/12/2444'
         */
        // </editor-fold>
        TypedQuery<BigDecimal> query = em.createQuery("SELECT min(upd.pgUpdUnitaDoc) " + " FROM AroUpdUnitaDoc upd "
                + " WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + " AND upd.elvElencoVer.idElencoVers = :idElencoVers "
                + " AND upd.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')", BigDecimal.class);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idElencoVers", idElencoVers);
        BigDecimal minPg = null;
        try {
            minPg = query.getSingleResult();
        } catch (NoResultException e) {
            LOG.debug("Nessun risultato per il progressivo minimo", e);
        }
        return minPg;
    }

    /**
     * il sistema determina gli aggiornamenti dell'unità doc, non in elenco o in un elenco diverso da quello corrente e
     * con stato diverso da IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI,
     * IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
     * IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO; si verifica se tali aggiornamenti hanno
     * progressivo inferiore al progressivo minimo degli aggiornamenti in elenco.
     *
     * @param idUnitaDocCorrente
     *            id unita doc corrente
     * @param pgMinimoUpdInElenco
     *            progressivo aggiornamento in elenco
     * @param idElencoVersCorrente
     *            id elenco versamento corrente
     * @param tiStatoUpdElencoVers
     *            stato aggiornamento versamento elenco
     *
     * @return lista di elementi DISTINTI
     */
    public Set<Long> retrieveUpdNonInElenco(long idUnitaDocCorrente, BigDecimal pgMinimoUpdInElenco,
            long idElencoVersCorrente, AroUpdUDTiStatoUpdElencoVers... tiStatoUpdElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select id_upd_unita_doc from ARO_UPD_UNITA_DOC upd join ARO_UNITA_DOC ud on (ud.id_unita_doc =
         * upd.id_unita_doc) where upd.id_unita_doc = <unità doc corrente> and ud.dt_annul = ‘31/12/2444’ and
         * upd.pg_upd_unita_doc < <progressivo aggiornamento minimo>
         * 
         * and (upd.id_elenco_vers is null or (upd.id_elenco_vers is not null and upd.id_elenco_vers != <elenco
         * corrente> and upd.ti_stato_upd_elenco_vers not in (IN_ELENCO_IN_CODA_INDICE_AIP,
         * IN_ELENCO_CON_INDICI_AIP_GENERATI, N_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
         * IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO))
         */
        // </editor-fold>

        TypedQuery<Long> query = em.createQuery("SELECT upd.idUpdUnitaDoc "
                + " FROM AroUpdUnitaDoc upd JOIN upd.aroUnitaDoc ud" + " WHERE ud.idUnitaDoc = :idUnitaDocCorrente "
                + " AND upd.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                + " AND upd.pgUpdUnitaDoc < :pgMinimoUpdInElenco"
                // l'UNITA DOCUMENTARIA non è associata ad un elenco oppure l'elenco è diverso
                // da quello attuale
                + " AND (upd.elvElencoVer IS NULL "
                + "      OR (upd.elvElencoVer IS NOT NULL AND upd.elvElencoVer.idElencoVers != :idElencoVersCorrente AND upd.tiStatoUpdElencoVers NOT IN (:tiStatoUpdElencoVers))"
                + "      ) ", Long.class);
        query.setParameter("idUnitaDocCorrente", idUnitaDocCorrente);
        query.setParameter("pgMinimoUpdInElenco", pgMinimoUpdInElenco);
        query.setParameter("idElencoVersCorrente", idElencoVersCorrente);
        List<AroUpdUDTiStatoUpdElencoVers> statiList = Arrays.asList(tiStatoUpdElencoVers);
        query.setParameter("tiStatoUpdElencoVers", statiList);

        Set<Long> result = new HashSet<>();
        result.addAll(query.getResultList());

        return result;
    }

    /**
     * Componenti in stato 'IN_ELENCO_IN_CODA_INDICE_AIP'.
     *
     * @param idUnitaDoc
     *            id unità doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return Lista di elementi distinti (in base all'id componente)
     */
    public Set<ComponenteInElenco> retrieveCompInElenco(long idUnitaDoc, long idElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select comp.id_comp_doc, comp.ds_urn_comp_calc from ARO_DOC doc join ARO_STRUT_DOC strut_doc on
         * (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on (comp.id_strut_doc = strut_doc. id_strut_doc) where
         * doc.id_unita_doc = <unità doc corrente> and doc.id_elenco_vers = <elenco corrente> and doc.ti_creazione =
         * 'AGGIUNTA_DOCUMENTO' and doc.dt_annul = '31/12/2444'
         * 
         * UNION
         * 
         * select comp.id_comp_doc, comp.ds_urn_comp_calc from ARO_UNITA_DOC ud join ARO_DOC doc on (doc.id_unita_doc =
         * ud.id_unita_doc) join ARO_STRUT_DOC strut_doc on (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on
         * (comp.id_strut_doc = strut_doc. id_strut_doc) where ud.id_unita_doc = <unità doc corrente> and
         * doc.ti_creazione = 'VERSAMENTO_UNITA_DOC' and doc.dt_annul = '31/12/2444' and ud.ti_stato_ud_elenco_vers =
         * 'IN_ELENCO_IN_CODA_INDICE_AIP'
         * 
         * UNION
         * 
         * select comp.id_comp_doc, comp.ds_urn_comp_calc from ARO_UNITA_DOC ud join ARO_DOC doc on (doc.id_unita_doc =
         * ud.id_unita_doc) join ARO_STRUT_DOC strut_doc on (strut_doc.id_doc = doc.id_doc) join ARO_COMP_DOC comp on
         * (comp.id_strut_doc = strut_doc. id_strut_doc) where ud.id_unita_doc = <unità doc corrente> and
         * doc.ti_creazione = 'AGGIUNTA_DOCUMENTO' and doc.dt_annul = '31/12/2444' and doc.ti_stato_doc_elenco_vers =
         * 'IN_ELENCO_IN_CODA_INDICE_AIP'
         */
        // </editor-fold>

        TypedQuery<ComponenteInElenco> q1 = em
                .createQuery("SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteInElenco(comp.idCompDoc) "
                        + " FROM AroCompDoc comp JOIN comp.aroStrutDoc strut JOIN strut.aroDoc doc"
                        + " WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + " AND doc.elvElencoVer.idElencoVers = :idElencoVers"
                        + " AND doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')  ", ComponenteInElenco.class);
        q1.setParameter("idUnitaDoc", idUnitaDoc);
        q1.setParameter("idElencoVers", idElencoVers);

        TypedQuery<ComponenteInElenco> q2 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteInElenco(comp.idCompDoc) "
                        + " FROM AroCompDoc comp JOIN comp.aroStrutDoc strut JOIN strut.aroDoc doc JOIN doc.aroUnitaDoc ud "
                        + " WHERE doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' " + " AND ud.idUnitaDoc = :idUnitaDoc "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')  "
                        + " AND ud.tiStatoUdElencoVers IN ('IN_ELENCO_IN_CODA_INDICE_AIP', 'IN_ELENCO_CON_INDICI_AIP_GENERATI', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA', 'IN_ELENCO_COMPLETATO') ",
                ComponenteInElenco.class);
        q2.setParameter("idUnitaDoc", idUnitaDoc);

        TypedQuery<ComponenteInElenco> q3 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.ComponenteInElenco(comp.idCompDoc) "
                        + " FROM AroCompDoc comp JOIN comp.aroStrutDoc strut JOIN strut.aroDoc doc JOIN doc.aroUnitaDoc ud "
                        + " WHERE doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + " AND ud.idUnitaDoc = :idUnitaDoc "
                        + " AND doc.dtAnnul = to_date('31/12/2444','dd/mm/yyyy')  "
                        + " AND doc.tiStatoDocElencoVers IN ('IN_ELENCO_IN_CODA_INDICE_AIP', 'IN_ELENCO_CON_INDICI_AIP_GENERATI', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO', 'IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA', 'IN_ELENCO_COMPLETATO') ",
                ComponenteInElenco.class);
        q3.setParameter("idUnitaDoc", idUnitaDoc);

        Set<ComponenteInElenco> result = new HashSet<>();
        result.addAll(q1.getResultList());
        result.addAll(q2.getResultList());
        result.addAll(q3.getResultList());

        return result;
    }

    /**
     * Aggiornamenti in stato 'IN_ELENCO_IN_CODA_INDICE_AIP'.
     *
     * @param idUnitaDoc
     *            id unità doc
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoUpdElencoVers
     *            elenco stati aggiornamento
     *
     * @return Lista di elementi distinti (in base all'id aggiornamento)
     */
    public Set<AggiornamentoInElenco> retrieveUpdInElenco(long idUnitaDoc, long idElencoVers,
            AroUpdUDTiStatoUpdElencoVers... tiStatoUpdElencoVers) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select id_upd_unita_doc, pg_upd_unita_doc from ARO_UPD_UNITA_DOC upd join ARO_UNITA_DOC ud on
         * (ud.id_unita_doc = upd.id_unita_doc) where upd.id_unita_doc = <unità doc corrente> and ud.dt_annul =
         * ‘31/12/2444’ and upd.id_elenco_vers = <elenco corrente>
         * 
         * UNION
         * 
         * select id_upd_unita_doc, pg_upd_unita_doc from ARO_UPD_UNITA_DOC upd join ARO_UNITA_DOC ud on
         * (ud.id_unita_doc = upd.id_unita_doc) where upd.id_unita_doc = <unità doc corrente> and ud.dt_annul =
         * ‘31/12/2444’ and upd.ti_stato_upd_elenco_vers in (IN_ELENCO_IN_CODA_INDICE_AIP,
         * IN_ELENCO_CON_INDICI_AIP_GENERATI, N_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
         * IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO)
         */
        // </editor-fold>

        TypedQuery<AggiornamentoInElenco> q1 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco(upd.idUpdUnitaDoc, upd.pgUpdUnitaDoc) "
                        + " FROM AroUpdUnitaDoc upd " + " WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + " AND upd.elvElencoVer.idElencoVers = :idElencoVers"
                        + " AND upd.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') ",
                AggiornamentoInElenco.class);
        q1.setParameter("idUnitaDoc", idUnitaDoc);
        q1.setParameter("idElencoVers", idElencoVers);

        TypedQuery<AggiornamentoInElenco> q2 = em.createQuery(
                "SELECT NEW it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco(upd.idUpdUnitaDoc, upd.pgUpdUnitaDoc) "
                        + " FROM AroUpdUnitaDoc upd JOIN upd.aroUnitaDoc ud " + " WHERE ud.idUnitaDoc = :idUnitaDoc "
                        + " AND upd.dtAnnul = to_date('31/12/2444','dd/mm/yyyy') "
                        + " AND upd.tiStatoUpdElencoVers IN (:tiStatoUpdElencoVers) ",
                AggiornamentoInElenco.class);
        q2.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroUpdUDTiStatoUpdElencoVers> statiList = Arrays.asList(tiStatoUpdElencoVers);
        q2.setParameter("tiStatoUpdElencoVers", statiList);

        Set<AggiornamentoInElenco> result = new HashSet<>();
        result.addAll(q1.getResultList());
        result.addAll(q2.getResultList());

        return result;
    }

    public List<Long> retrieveIdElenchiDaElaborare(BigDecimal idStrut, String statoElenco) {
        Query q = em.createQuery(
                "SELECT elenco.idElencoVers " + "FROM ElvElencoVer elenco JOIN elenco.elvElencoVersDaElabs elDaElab "
                        + "WHERE elDaElab.idStrut = :idStrut " + "AND elDaElab.tiStatoElenco = :statoElenco "
                        + "ORDER BY elenco.dtCreazioneElenco");
        q.setParameter("idStrut", idStrut);
        q.setParameter("statoElenco", statoElenco);
        return q.getResultList();
    }

    public List<Long> retrieveIdElenchiDaValidare(BigDecimal idStrut, String statoElenco,
            String numMaxElenchiDaValidare) {
        Query q = em.createQuery("SELECT elenco.idElencoVers "
                + "FROM ElvElencoVer elenco JOIN elenco.elvElencoVersDaElabs elDaElab JOIN elenco.decCriterioRaggr crit "
                + "WHERE elDaElab.idStrut = :idStrut " + "AND elDaElab.tiStatoElenco = :statoElenco "
                + "AND ((elenco.tiModValidElenco IS NOT NULL AND elenco.tiValidElenco IS NOT NULL "
                + " AND elenco.tiModValidElenco = :tiModValidElenco AND elenco.tiValidElenco != :tiValidElenco) "
                + " OR ((elenco.tiModValidElenco IS NULL OR elenco.tiValidElenco IS NULL) "
                + " AND crit.tiModValidElenco = :tiModValidElencoCriterio AND crit.tiValidElenco != :tiValidElencoCriterio)) "
                + "ORDER BY elenco.dtCreazioneElenco");
        q.setParameter("idStrut", idStrut);
        q.setParameter("statoElenco", statoElenco);
        q.setParameter("tiModValidElenco", TiModValidElenco.AUTOMATICA);
        q.setParameter("tiValidElenco", TiValidElenco.FIRMA);
        q.setParameter("tiModValidElencoCriterio", TiModValidElencoCriterio.AUTOMATICA);
        q.setParameter("tiValidElencoCriterio", TiValidElencoCriterio.FIRMA);
        q.setFirstResult(0);
        q.setMaxResults(Integer.valueOf(numMaxElenchiDaValidare));
        return q.getResultList();
    }

    public List<BigDecimal> retrieveUdInElencoByElencoIdList(long idElencoVers) {
        String queryStr = "SELECT lisUdByElenco.id.idUnitaDoc FROM ElvVLisAllUdByElenco lisUdByElenco "
                + "WHERE lisUdByElenco.id.idElencoVers = :idElencoVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", bigDecimalFromLong(idElencoVers));
        return query.getResultList();
    }

    public ElvVLisAllUdByElenco retrieveElvVLisAllUdByElenco(long idElencoVers, long idUnitaDoc) {
        String queryStr = "SELECT lisUdByElenco FROM ElvVLisAllUdByElenco lisUdByElenco "
                + "WHERE lisUdByElenco.id.idElencoVers = :idElencoVers "
                + "AND lisUdByElenco.id.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", bigDecimalFromLong(idElencoVers));
        query.setParameter("idUnitaDoc", bigDecimalFromLong(idUnitaDoc));
        return (ElvVLisAllUdByElenco) query.getSingleResult();
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     *
     * @return entity AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public void deleteElencoVersDaElab(Long idElencoVersDaElab) {
        ElvElencoVersDaElab eevde = em.find(ElvElencoVersDaElab.class, idElencoVersDaElab);
        em.remove(eevde);
    }

    public ElvFileElencoVer storeFileIntoElenco(ElvElencoVer elenco, byte[] file, String fileType)
            throws NoSuchAlgorithmException, IOException {
        ElvFileElencoVer fileIndexElencoVers = new ElvFileElencoVer();
        fileIndexElencoVers.setBlFileElencoVers(file);
        fileIndexElencoVers.setTiFileElencoVers(fileType);
        fileIndexElencoVers.setElvElencoVer(elenco);
        fileIndexElencoVers.setIdStrut(new BigDecimal(elenco.getOrgStrut().getIdStrut()));
        fileIndexElencoVers.setDtCreazioneFile(new Date());
        // sha-256
        String hash = new HashCalculator().calculateHashSHAX(file, TipiHash.SHA_256).toHexBinary();
        fileIndexElencoVers.setDsHashFile(hash);
        fileIndexElencoVers.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
        fileIndexElencoVers.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
        fileIndexElencoVers.setCdVerXsdFile(ElencoEnums.ElencoInfo.VERSIONE_ELENCO.message());

        List<ElvFileElencoVer> fileIndexElencoConservList = elenco.getElvFileElencoVers();
        fileIndexElencoConservList.add(fileIndexElencoVers);
        elenco.setElvFileElencoVers(fileIndexElencoConservList);
        return fileIndexElencoVers;
    }

    /**
     * Restituisce il valore del numero di reset di stato per l'unità doc presente nell'elenco
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return il valore del numero di reset oppure 0 se questo ancora non esiste
     */
    public BigDecimal getNiResetStatoUnitaDocInElenco(long idUnitaDoc, long idElencoVers) {
        List<BigDecimal> resetList;
        String queryStr = "SELECT ud.niResetStato " + "FROM AroUnitaDoc ud " + "WHERE ud.idUnitaDoc = :idUnitaDoc "
                + "AND ud.elvElencoVer.idElencoVers = :idElencoVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);
        resetList = query.getResultList();
        if (resetList != null && !resetList.isEmpty() && resetList.get(0) != null) {
            return resetList.get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Restituisce il valore del numero di reset di stato per i documenti aggiunti appartenenti all'unità doc presenti
     * nell'elenco
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return il valore del numero di reset oppure 0 se questo ancora non esiste
     */
    public BigDecimal getNiResetStatoDocInElenco(long idUnitaDoc, long idElencoVers) {
        List<BigDecimal> resetList;
        String queryStr = "SELECT doc.niResetStato " + "FROM AroDoc doc "
                + "WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND doc.elvElencoVer.idElencoVers = :idElencoVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);
        resetList = query.getResultList();
        if (resetList != null && !resetList.isEmpty() && resetList.get(0) != null) {
            return resetList.get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Restituisce il valore del numero di reset di stato per gli aggiornamenti metadati relativi all'unità doc presenti
     * nell'elenco
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return il valore del numero di reset oppure 0 se questo ancora non esiste
     */
    public BigDecimal getNiResetStatoUpdInElenco(long idUnitaDoc, long idElencoVers) {
        List<BigDecimal> resetList;
        String queryStr = "SELECT upd " + "FROM AroUpdUnitaDoc upd " + "WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND upd.elvElencoVer.idElencoVers = :idElencoVers";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);
        resetList = query.getResultList();
        if (resetList != null && !resetList.isEmpty() && resetList.get(0) != null) {
            return resetList.get(0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Il sistema aggiorna l'unità doc presente nell'elenco, assegnando lo stato relativo all'elenco passato in input.
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     * @param stato
     *            stato dell'elenco : IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS nella prima parte del job
     *            IN_ELENCO_IN_CODA_INDICE_AIP nella seconda
     * @param tsStatoElencoVers
     *            timestamp stato elenco versamento
     * @param tsLastResetStato
     *            timestamp ultimo reset stato
     * @param niResetStato
     *            numero reset
     */
    public void aggiornaStatoUnitaDocInElenco(long idUnitaDoc, long idElencoVers, String stato, Date tsStatoElencoVers,
            Date tsLastResetStato, BigDecimal niResetStato) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select * from aro_unita_doc ud where ud.id_unita_doc = 121736 and ud.id_elenco_vers = 1322
         */
        // </editor-fold>
        TypedQuery<AroUnitaDoc> query = em.createQuery("SELECT ud " + "FROM AroUnitaDoc ud "
                + "WHERE ud.idUnitaDoc = :idUnitaDoc " + "AND ud.elvElencoVer.idElencoVers = :idElencoVers",
                AroUnitaDoc.class);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);

        // Dovrebbe essere sempre un risultato...
        for (AroUnitaDoc aroUnitaDoc : query.getResultList()) {
            aroUnitaDoc.setTiStatoUdElencoVers(stato);
            if (tsStatoElencoVers != null) {
                aroUnitaDoc.setTsStatoElencoVers(tsStatoElencoVers);
            }
            aroUnitaDoc.setTsLastResetStato(tsLastResetStato);
            aroUnitaDoc.setNiResetStato(niResetStato);
            em.persist(aroUnitaDoc);
        }

    }

    /**
     * Il sistema aggiorna i documenti aggiunti appartenenti all'unità doc presenti nell'elenco assegnando stato
     * relativo passato in input.
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     * @param stato
     *            stato dell'elenco : IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS nella prima parte del job
     *            IN_ELENCO_IN_CODA_INDICE_AIP nella seconda
     * @param tsStatoElencoVers
     *            timestamp stato elenco versamento
     * @param tsLastResetStato
     *            timestamp ultimo reset stato
     * @param niResetStato
     *            numero reset
     */
    public void aggiornaStatoDocInElenco(long idUnitaDoc, long idElencoVers, String stato, Date tsStatoElencoVers,
            Date tsLastResetStato, BigDecimal niResetStato) {
        // <editor-fold defaultstate="collapsed" desc="Query SQL da analisi">
        /*
         * select * from aro_doc doc where doc.id_unita_doc= 121623 and doc.id_elenco_vers = 1322
         */
        // </editor-fold>
        TypedQuery<AroDoc> query = em
                .createQuery("SELECT doc " + "FROM AroDoc doc " + "WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND doc.elvElencoVer.idElencoVers = :idElencoVers", AroDoc.class);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);

        for (AroDoc aroDoc : query.getResultList()) {
            aroDoc.setTiStatoDocElencoVers(stato);
            if (tsStatoElencoVers != null) {
                aroDoc.setTsStatoElencoVers(tsStatoElencoVers);
            }
            aroDoc.setTsLastResetStato(tsLastResetStato);
            aroDoc.setNiResetStato(niResetStato);
            em.persist(aroDoc);
        }
    }

    /**
     * Il sistema aggiorna gli aggiornamenti metadati relativi all'unità doc presenti nell'elenco assegnando stato
     * relativo passato in input.
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     * @param stato
     *            stato dell'elenco : IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS nella prima parte del job
     *            IN_ELENCO_IN_CODA_INDICE_AIP nella seconda
     * @param tsStatoElencoVers
     *            timestamp stato elenco versamento
     * @param tsLastResetStato
     *            timestamp ultimo reset stato
     * @param niResetStato
     *            numero reset
     */
    public void aggiornaStatoUpdInElenco(long idUnitaDoc, long idElencoVers, String stato, Date tsStatoElencoVers,
            Date tsLastResetStato, BigDecimal niResetStato) {
        TypedQuery<AroUpdUnitaDoc> query = em.createQuery(
                "SELECT upd " + "FROM AroUpdUnitaDoc upd " + "WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND upd.elvElencoVer.idElencoVers = :idElencoVers",
                AroUpdUnitaDoc.class);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);

        for (AroUpdUnitaDoc aroUpdUnitaDoc : query.getResultList()) {
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.valueOf(stato));
            if (tsStatoElencoVers != null) {
                aroUpdUnitaDoc.setTsStatoElencoVers(tsStatoElencoVers);
            }
            aroUpdUnitaDoc.setTsLastResetStato(tsLastResetStato);
            aroUpdUnitaDoc.setNiResetStato(niResetStato);
            em.persist(aroUpdUnitaDoc);
        }
    }

    /**
     * (2) Il sistema aggiorna elenco corrente (ELV_ELENCO_VERS) assegnando stato = FIRME_VERIFICATE_DT_VERS.
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param stato
     *            Stato delle elenco
     */
    public void aggiornaElencoCorrente(long idElencoVers, ElencoEnums.ElencoStatusEnum stato) {
        ElvElencoVer elencoCorrente = em.find(ElvElencoVer.class, idElencoVers);
        elencoCorrente.setTiStatoElenco(stato.name());
        em.persist(elencoCorrente);
    }

    /**
     * (3) Il sistema aggiorna elenco da elaborare corrente (ELV_ELENCO_VERS_DA_ELAB) assegnando stato =
     * FIRME_VERIFICATE_DT_VERS
     *
     * @param idElencoDaEleb
     *            id elenco da elaborare
     * @param stato
     *            enum ElencoStatusEnum
     */
    public void aggiornaElencoDaElabCorrente(long idElencoDaEleb, ElencoEnums.ElencoStatusEnum stato) {
        aggiornaElencoDaElabCorrente(idElencoDaEleb, stato, true);
    }

    /**
     * (3) Il sistema aggiorna elenco da elaborare corrente (ELV_ELENCO_VERS_DA_ELAB) assegnando stato =
     * FIRME_VERIFICATE_DT_VERS e se flag = true annulla il timestamp di assunzione stato
     *
     * @param idElencoDaEleb
     *            id elenco da elaborare
     * @param stato
     *            enum ElencoStatusEnum
     * @param annullaTimestamp
     *            true/false
     */
    public void aggiornaElencoDaElabCorrente(long idElencoDaEleb, ElencoEnums.ElencoStatusEnum stato,
            boolean annullaTimestamp) {
        ElvElencoVersDaElab elencoCorrenteDaElab = em.find(ElvElencoVersDaElab.class, idElencoDaEleb);
        elencoCorrenteDaElab.setTiStatoElenco(stato.name());
        if (annullaTimestamp) {
            elencoCorrenteDaElab.setTsStatoElenco(null);
        }
        em.persist(elencoCorrenteDaElab);
    }

    public ElvElencoVersDaElab retrieveElencoInQueue(ElvElencoVer elenco) {
        Query q = em.createQuery("SELECT elencoVersDaElab " + "FROM ElvElencoVersDaElab elencoVersDaElab "
                + "WHERE elencoVersDaElab.elvElencoVer.idElencoVers = :idElenco");
        q.setParameter("idElenco", elenco.getIdElencoVers());
        return (ElvElencoVersDaElab) q.getSingleResult();
    }

    public List<HsmElencoSessioneFirma> retrieveListaElencoInError(ElvElencoVer elenco, TiEsitoFirmaElenco esito) {
        Query q = em.createQuery("SELECT elencoInError " + "FROM HsmElencoSessioneFirma elencoInError "
                + "WHERE elencoInError.elvElencoVer.idElencoVers = :idElenco " + "AND elencoInError.tiEsito = :esito");
        q.setParameter("idElenco", elenco.getIdElencoVers());
        q.setParameter("esito", esito);
        return q.getResultList();
    }

    /**
     * Assegna lo stato status a tutte le unità documentarie presenti nell'elenco
     *
     * @param elenco
     *            entity ElvElencoVer
     * @param status
     *            stato
     */
    public void setUdsStatus(ElvElencoVer elenco, String status) {
        List<AroUnitaDoc> uds = retrieveUdDocsInElenco(elenco);
        for (AroUnitaDoc ud : uds) {
            ud.setTiStatoUdElencoVers(status);
            LOG.debug(" - Assegnato alla unità documentaria '{}' lo stato {}", ud.getIdUnitaDoc(), status);
        }
    }

    /**
     * Assegna lo stato status a tutti i documenti presenti nell'elenco
     *
     * @param elenco
     *            entity ElvElencoVer
     * @param status
     *            stato
     */
    public void setDocsStatus(ElvElencoVer elenco, String status) {
        List<AroDoc> docs = retrieveDocsInElenco(elenco);
        for (AroDoc doc : docs) {
            doc.setTiStatoDocElencoVers(status);
            LOG.debug(" - Assegnato al doc '{}' lo stato {}", doc.getIdDoc(), status);
        }
    }

    /**
     * Assegna lo stato status a tutti gli aggiornamenti presenti nell'elenco
     *
     * @param elenco
     *            entity ElvElencoVer
     * @param status
     *            stato
     */
    public void setUpdsStatus(ElvElencoVer elenco, AroUpdUDTiStatoUpdElencoVers status) {
        List<AroUpdUnitaDoc> upds = retrieveUpdsInElenco(elenco);
        for (AroUpdUnitaDoc upd : upds) {
            upd.setTiStatoUpdElencoVers(status);
            LOG.debug(" - Assegnata la upd '{}' lo stato {}", upd.getIdUpdUnitaDoc(), status);
        }
    }

    public void deleteElvElencoVer(BigDecimal idElencoVers) {
        em.remove(em.find(ElvElencoVer.class, idElencoVers.longValue()));
    }

    public void deleteElvElencoVersFasc(BigDecimal idElencoVersFasc) {
        em.remove(em.find(ElvElencoVersFasc.class, idElencoVersFasc.longValue()));
    }

    public void insertUdCodaUdDaElab(long idUnitaDoc, ElencoEnums.UdDocStatusEnum status) {
        ElvUdVersDaElabElenco udVersDaElab = new ElvUdVersDaElabElenco();
        AroUnitaDoc aroUnitaDoc = em.find(AroUnitaDoc.class, idUnitaDoc);
        udVersDaElab.setAroUnitaDoc(aroUnitaDoc);
        udVersDaElab.setIdStrut(new BigDecimal(aroUnitaDoc.getOrgStrut().getIdStrut()));
        udVersDaElab.setAaKeyUnitaDoc(aroUnitaDoc.getAaKeyUnitaDoc());
        udVersDaElab.setDtCreazione(aroUnitaDoc.getDtCreazione());
        udVersDaElab.setTiStatoUdDaElab(status.name());
        aroUnitaDoc.getElvUdVersDaElabElencos().add(udVersDaElab);
        em.persist(udVersDaElab);
        em.flush();
    }

    public void insertDocCodaDocDaElab(long idDoc, ElencoEnums.DocStatusEnum status) {
        ElvDocAggDaElabElenco docVersDaElab = new ElvDocAggDaElabElenco();
        AroDoc aroDoc = em.find(AroDoc.class, idDoc);
        docVersDaElab.setAroDoc(aroDoc);
        docVersDaElab.setIdStrut(aroDoc.getIdStrut());
        docVersDaElab.setAaKeyUnitaDoc(aroDoc.getAroUnitaDoc().getAaKeyUnitaDoc());
        docVersDaElab.setDtCreazione(aroDoc.getAroUnitaDoc().getDtCreazione());
        docVersDaElab.setTiStatoDocDaElab(status.name());
        aroDoc.getElvDocAggDaElabElencos().add(docVersDaElab);
        em.persist(docVersDaElab);
        em.flush();
    }

    public void insertUpdCodaUpdDaElab(long idUpdUnitaDoc, ElvUpdUdDaElabTiStatoUpdElencoVers status) {
        ElvUpdUdDaElabElenco updVersDaElab = new ElvUpdUdDaElabElenco();
        AroUpdUnitaDoc aroUpdUnitaDoc = em.find(AroUpdUnitaDoc.class, idUpdUnitaDoc);
        updVersDaElab.setAroUpdUnitaDoc(aroUpdUnitaDoc);
        updVersDaElab.setOrgStrut(aroUpdUnitaDoc.getOrgStrut());
        // MAC#22942
        // Identificatore della sub struttura pari al valore definito sull’unità doc in
        // aggiornamento
        updVersDaElab.setOrgSubStrut(aroUpdUnitaDoc.getAroUnitaDoc().getOrgSubStrut());
        // end MAC#22942
        updVersDaElab.setDecRegistroUnitaDoc(aroUpdUnitaDoc.getDecRegistroUnitaDoc());
        updVersDaElab.setAaKeyUnitaDoc(aroUpdUnitaDoc.getAroUnitaDoc().getAaKeyUnitaDoc());
        updVersDaElab.setDtCreazione(aroUpdUnitaDoc.getAroUnitaDoc().getDtCreazione());
        updVersDaElab.setDecTipoDocPrinc(aroUpdUnitaDoc.getDecTipoDocPrinc());
        updVersDaElab.setDecTipoUnitaDoc(aroUpdUnitaDoc.getDecTipoUnitaDoc());
        updVersDaElab.setTiStatoUpdElencoVers(status);
        aroUpdUnitaDoc.getElvUpdUdDaElabElencos().add(updVersDaElab);
        em.persist(updVersDaElab);
        em.flush();
    }

    public ElvElencoVersDaElab getElvElencoVersDaElabByIdElencoVers(long idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVersDaElab u " + "WHERE u.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        final List<ElvElencoVersDaElab> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            throw new NoResultException(
                    "Nessun ElvElencoVersDaElab trovato per elvElencoVer.idElencoVers " + idElencoVers);
        }
        return resultList.get(0);
    }

    public byte[] retrieveFileIndiceElenco(long idElencoVers, String tiFileElencoVers) {
        String queryStr = "SELECT u.blFileElencoVers FROM ElvFileElencoVer u "
                + "WHERE u.elvElencoVer.idElencoVers = :idElencoVers " + "AND u.tiFileElencoVers = :tiFileElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiFileElencoVers", tiFileElencoVers);
        List<ElvFileElencoVer> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return (byte[]) query.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<ElvFileElencoVer> retrieveFileIndiceElenco(long idElencoVers, String... tiFileElencoVers) {
        String queryStr = "SELECT new it.eng.parer.entity.ElvFileElencoVer(u.blFileElencoVers, u.cdVerXsdFile, u.tiFileElencoVers) FROM ElvFileElencoVer u "
                + "WHERE u.elvElencoVer.idElencoVers = :idElencoVers AND u.tiFileElencoVers IN (:tiFileElencoVers)";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiFileElencoVers", Arrays.asList(tiFileElencoVers));
        return query.getResultList();
    }

    public ElvFileElencoVer getFileIndiceElenco(long idElencoVers, String tiFileElencoVers) {
        String queryStr = "SELECT u FROM ElvFileElencoVer u " + "WHERE u.elvElencoVer.idElencoVers = :idElencoVers "
                + "AND u.tiFileElencoVers IN (:tiFileElencoVers)";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiFileElencoVers", Arrays.asList(tiFileElencoVers));
        List<ElvFileElencoVer> elencoList = query.getResultList();
        if (!elencoList.isEmpty()) {
            return elencoList.get(0);
        } else {
            return null;
        }
    }

    /*
     * restituisce la lista ElvUrnElencoVers per l'elenco passato
     */
    public List<ElvUrnElencoVers> retrieveUrnElencoVersList(long idElencoVers) {
        Query query = getEntityManager()
                .createQuery("SELECT a FROM ElvUrnElencoVers a WHERE a.elvElencoVers.idElencoVers = :idElencoVers");
        query.setParameter("idElencoVers", idElencoVers);
        return query.getResultList();
    }

    public List<AroDoc> retrieveDocVersList(AroUnitaDoc ud) {
        List<AroDoc> docsVers = new ArrayList<>();
        List<AroDoc> docsInUd = ud.getAroDocs();
        for (AroDoc docInUd : docsInUd) {
            if (docInUd.getElvElencoVer() == null
                    && docInUd.getTiCreazione().equals(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name())) {
                docsVers.add(docInUd);
            }
        }
        return docsVers;
    }

    public List<AroUnitaDoc> retrieveUdsWithDocAggInElenco(long idElenco) {
        Query q = em.createQuery("SELECT DISTINCT doc.aroUnitaDoc " + "FROM AroDoc doc "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElenco " + "AND doc.tiCreazione = :tiCreazione");
        q.setParameter("idElenco", idElenco);
        q.setParameter("tiCreazione", CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
        return q.getResultList();
    }

    public List<AroUnitaDoc> retrieveUdsModifDocAggUpdInElenco(long idElenco) {
        Query q = em.createQuery("SELECT DISTINCT ud.idUnitaDoc,e.dtVersMin "
                + "FROM ElvVLisUdModifElenco e, AroUnitaDoc ud " + "WHERE e.idElencoVers = :idElenco "
                + "AND e.idUnitaDoc = ud.idUnitaDoc " + "ORDER BY e.dtVersMin");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        List<AroUnitaDoc> uds = new ArrayList<>();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            Long idUnitaDoc = (Long) o[0];
            uds.add(em.find(AroUnitaDoc.class, idUnitaDoc));
        }
        return uds;
    }

    public List<AroDoc> retrieveDocAggList(AroUnitaDoc ud, long idElenco) {
        Query q = em.createQuery("SELECT doc " + "FROM AroDoc doc " + "WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND doc.elvElencoVer.idElencoVers = :idElenco " + "AND doc.tiCreazione = :tiCreazione");
        q.setParameter("idElenco", idElenco);
        q.setParameter("idUnitaDoc", ud.getIdUnitaDoc());
        q.setParameter("tiCreazione", CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
        return q.getResultList();
    }

    public List<ElvVLisModifByUd> retrieveDocAggUpdList(AroUnitaDoc ud, long idElenco) {
        Query q = em.createQuery("SELECT e " + "FROM ElvVLisModifByUd e " + "WHERE e.idUnitaDoc = :idUnitaDoc "
                + "AND e.idElencoVers = :idElenco " + "ORDER BY e.dtVers");
        q.setParameter("idElenco", bigDecimalFromLong(idElenco));
        q.setParameter("idUnitaDoc", BigDecimal.valueOf(ud.getIdUnitaDoc()));
        return q.getResultList();
    }

    /**
     * Restituisce il numero delle unità documentarie versate in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaUdVersate(Long idElencoVers) {
        String queryStr = "SELECT COUNT(ud) " + "FROM AroUnitaDoc ud "
                + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero dei fascicoli versati in elenco
     *
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     *
     * @return long risultato count
     */
    public long contaFascVersati(Long idElencoVersFasc) {
        String queryStr = "SELECT COUNT(fascicolo) " + "FROM FasFascicolo fascicolo "
                + "WHERE fascicolo.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero dei documenti versati in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaDocVersati(Long idElencoVers) {
        String queryStr = "SELECT COUNT(ud) " + "FROM AroDoc doc JOIN doc.aroUnitaDoc ud "
                + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers "
                + "AND doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero dei componenti versati in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return lista oggetti di tipo {@link AroCompDoc}
     */
    public Object[] contaCompVersati(Long idElencoVers) {
        String queryStr = "SELECT COUNT(comp),SUM(comp.niSizeFileCalc) "
                + "FROM AroCompDoc comp JOIN comp.aroStrutDoc strutDoc "
                + "JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc ud "
                + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers "
                + "AND doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        return (Object[]) query.getSingleResult();
    }

    /**
     * Restituisce il numero delle unità documentarie modificate a seguito dell'aggiunta di documenti in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaUdModificatePerDocAggiunti(Long idElencoVers) {
        String queryStr = "SELECT COUNT(DISTINCT ud.idUnitaDoc) " + "FROM AroDoc doc JOIN doc.aroUnitaDoc ud "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero delle unità documentarie modificate incluse nell'elenco a causa di documenti aggiunti e/o
     * di aggiornamenti metadati unità doc
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaUdModificatePerByDocAggiuntiByUpd(Long idElencoVers) {
        String queryStr = "SELECT e.niUnitaDocModElenco " + "FROM ElvVCountUdModif e "
                + "WHERE e.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", bigDecimalFromLong(idElencoVers));
        BigDecimal num = (BigDecimal) query.getSingleResult();
        return num != null ? num.longValue() : 0L;
    }

    /**
     * Restituisce il numero dei documenti aggiunti in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaDocAggiunti(Long idElencoVers) {
        String queryStr = "SELECT COUNT(doc) " + "FROM AroDoc doc "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero degli aggiornamenti in elenco
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return long risultato count
     */
    public long contaUpdUd(Long idElencoVers) {
        String queryStr = "SELECT COUNT(upd) " + "FROM AroUpdUnitaDoc upd "
                + "WHERE upd.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    /**
     * Restituisce il numero di componenti dei documenti aggiunti in elenco e la relativa dimensione in byte
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return lista oggetti di tipo {@link AroCompDoc}
     */
    public Object[] contaCompPerDocAggiunti(Long idElencoVers) {
        String queryStr = "SELECT COUNT(doc),SUM(comp.niSizeFileCalc) "
                + "FROM AroCompDoc comp JOIN comp.aroStrutDoc strutDoc JOIN strutDoc.aroDoc doc "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        return (Object[]) query.getSingleResult();
    }

    public List<Long> retrieveElenchiIndiciAipDaProcessare(String tiStatoElenco) {
        Query q = em.createQuery(
                "SELECT elencoVers.idElencoVers FROM ElvElencoVersDaElab elencoDaElab JOIN elencoDaElab.elvElencoVer elencoVers WHERE elencoDaElab.tiStatoElenco = :tiStatoElenco ORDER BY elencoDaElab.idStrut, elencoVers.dtCreazioneElenco");
        q.setParameter("tiStatoElenco", tiStatoElenco);
        return q.getResultList();
    }

    public List<ElvVLisElencoDaMarcare> retrieveElenchiIndiciAipDaMarcare(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, long idUserIam, List<String> tiGestElenco) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT elenchi FROM ElvVLisElencoDaMarcare elenchi WHERE elenchi.idUserIam = :idUserIam ");
        if (idStrut != null) {
            queryStr.append("AND ").append("elenchi.idStrut = :idStrut ");
        }
        if (idEnte != null) {
            queryStr.append("AND ").append("elenchi.idEnte = :idEnte ");
        }
        if (idAmbiente != null) {
            queryStr.append("AND ").append("elenchi.idAmbiente = :idAmbiente ");
        }
        if (tiGestElenco != null && !tiGestElenco.isEmpty()) {
            queryStr.append("AND ").append("elenchi.tiGestElenco IN(:tiGestElenco) ");
        }

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        if (idStrut != null) {
            q.setParameter("idStrut", idStrut);
        }
        if (idEnte != null) {
            q.setParameter("idEnte", idEnte);
        }
        if (idAmbiente != null) {
            q.setParameter("idAmbiente", idAmbiente);
        }
        if (tiGestElenco != null && !tiGestElenco.isEmpty()) {
            q.setParameter("tiGestElenco", tiGestElenco);
        }
        return q.getResultList();
    }

    /**
     * Ottieni l'insieme delle unità documentarie appartenenti all'elenco. L'elenco contiene sia le unità versate sia i
     * documenti aggiunti sia gli aggiornamenti metadati
     *
     * @param idElenco
     *            id dell'elenco validato.
     *
     * @return Set - insieme di id <strong>distinti</strong>
     */
    public Set<Long> retrieveUdVersOrAggInElenco(long idElenco) {
        TypedQuery<Long> q1 = em.createQuery(
                "SELECT ud.idUnitaDoc FROM AroUnitaDoc ud WHERE ud.elvElencoVer.idElencoVers = :idElenco ", Long.class);
        TypedQuery<Long> q2 = em.createQuery(
                "SELECT DISTINCT doc.aroUnitaDoc.idUnitaDoc FROM AroDoc doc WHERE doc.elvElencoVer.idElencoVers = :idElenco ",
                Long.class);
        TypedQuery<Long> q3 = em.createQuery(
                "SELECT DISTINCT upd.aroUnitaDoc.idUnitaDoc FROM AroUpdUnitaDoc upd WHERE upd.elvElencoVer.idElencoVers = :idElenco ",
                Long.class);
        q1.setParameter("idElenco", idElenco);
        q2.setParameter("idElenco", idElenco);
        q3.setParameter("idElenco", idElenco);
        List<Long> l1 = q1.getResultList();
        List<Long> l2 = q2.getResultList();
        List<Long> l3 = q3.getResultList();
        Set<Long> hs = new HashSet<>();
        hs.addAll(l1);
        hs.addAll(l2);
        hs.addAll(l3);
        return hs;
    }

    /**
     * Verifica se nell'elenco passato in ingresso esiste almeno un'ud (sia versata che per aggiunta documento)
     * annullata
     *
     * @param idElencoVers
     *            id elenco versamento
     *
     * @return true/false
     */
    public boolean existUdVersDocAggAnnullati(BigDecimal idElencoVers) {
        boolean result;
        String queryUdStr = "SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitadoc.elvElencoVer.idElencoVers = :idElencoVers "
                + "AND unitaDoc.tiStatoConservazione = 'ANNULLATA' ";
        Query queryUd = em.createQuery(queryUdStr);
        queryUd.setParameter("idElencoVers", idElencoVers.longValue());
        result = queryUd.getResultList().isEmpty();

        // Se non ho ud annullate per versamento, controllo per aggiunta documenti
        if (result) {
            String queryDocStr = "SELECT doc FROM AroDoc doc " + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers "
                    + "AND doc.aroUnitaDoc.tiStatoConservazione = 'ANNULLATA' ";
            Query queryDoc = em.createQuery(queryDocStr);
            queryDoc.setParameter("idElencoVers", idElencoVers.longValue());
            result = queryDoc.getResultList().isEmpty();
        }
        return !result;
    }

    public List<AroUnitaDoc> getUnitaDocVersateElenco(BigDecimal idElencoVers) {
        String queryUdStr = "SELECT unitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitadoc.elvElencoVer.idElencoVers = :idElencoVers ";
        Query queryUd = em.createQuery(queryUdStr);
        queryUd.setParameter("idElencoVers", idElencoVers.longValue());
        return queryUd.getResultList();
    }

    public List<AroDoc> getDocAggiuntiElenco(BigDecimal idElencoVers) {
        String queryUdStr = "SELECT doc FROM AroDoc doc WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ";
        Query queryUd = em.createQuery(queryUdStr);
        queryUd.setParameter("idElencoVers", idElencoVers.longValue());
        return queryUd.getResultList();
    }

    public List<AroUpdUnitaDoc> getUpdMetadatiElenco(BigDecimal idElencoVers) {
        String queryUdStr = "SELECT upd FROM AroUpdUnitaDoc upd WHERE upd.elvElencoVer.idElencoVers = :idElencoVers ";
        Query queryUd = em.createQuery(queryUdStr);
        queryUd.setParameter("idElencoVers", idElencoVers.longValue());
        return queryUd.getResultList();
    }

    public ElvVChkAddDocAgg retrieveElvVChkAddDocAggByIdDocAggByIdElenco(long idDoc, long idElencoVers) {
        try {
            Query query = getEntityManager().createNamedQuery("ElvVChkAddDocAgg.findByIdDocIdElenco",
                    ElvVChkAddDocAgg.class);
            query.setParameter("idDoc", bigDecimalFromLong(idDoc));
            query.setParameter("idElencoVersCor", bigDecimalFromLong(idElencoVers));
            return (ElvVChkAddDocAgg) query.getSingleResult();
        } catch (RuntimeException ex) {
            LOG.error("Errore nell'estrazione di ElvVChkAddDocAgg", ex);
            throw ex;
        }
    }

    // MAC#28020
    public ElvVChkAddDocAggNoEleCor retrieveElvVChkAddDocAggNoEleCorByIdDoc(long idDoc) {
        try {
            Query query = em.createNamedQuery("ElvVChkAddDocAggNoEleCor.findByIdDoc", ElvVChkAddDocAggNoEleCor.class);
            query.setParameter("idDoc", bigDecimalFromLong(idDoc));
            return (ElvVChkAddDocAggNoEleCor) query.getSingleResult();
        } catch (RuntimeException ex) {
            LOG.error("Errore nell'estrazione di ElvVChkAddDocAggNoEleCor", ex);
            throw ex;
        }
    }
    // end MAC#28020

    public ElvVChkAddUpdUd retrieveElvVChkAddUpdUdByIdUpdUdByIdElenco(long idUpdUnitaDoc, long idElencoVers) {
        try {
            Query query = em.createNamedQuery("ElvVChkAddUpdUd.findByIdUpdIdElenco", ElvVChkAddUpdUd.class);
            query.setParameter("idUpdUnitaDoc", bigDecimalFromLong(idUpdUnitaDoc));
            query.setParameter("idElencoVersCor", bigDecimalFromLong(idElencoVers));
            return (ElvVChkAddUpdUd) query.getSingleResult();
        } catch (RuntimeException ex) {
            LOG.error("Errore nell'estrazione di ElvVChkAddUpdUd", ex);
            throw ex;
        }
    }

    // MAC#28020
    public ElvVChkAddUpdUdNoEleCor retrieveElvVChkAddUpdUdNoEleCorByIdUpdUd(long idUpdUnitaDoc) {
        try {
            Query query = em.createNamedQuery("ElvVChkAddUpdUdNoEleCor.findByIdUpd", ElvVChkAddUpdUdNoEleCor.class);
            query.setParameter("idUpdUnitaDoc", bigDecimalFromLong(idUpdUnitaDoc));
            return (ElvVChkAddUpdUdNoEleCor) query.getSingleResult();
        } catch (RuntimeException ex) {
            LOG.error("Errore nell'estrazione di ElvVChkAddUpdUdNoEleCor", ex);
            throw ex;
        }
    }
    // end MAC#28020

    public List<SessioneVersamentoExt> leggiXmlVersamentiElencoDaUnitaDoc(long idUnitaDoc, String baseUrnUnitaDoc)
            throws ParerNoResultException {
        List<SessioneVersamentoExt> tmpSveList = new ArrayList<>();
        LOG.debug("Lettura xml di versamento - INIZIO");
        /*
         * recupero la sessione relativa al versamento originale dell'UD. per ora non ho bisogno di conoscerne l'elenco
         * dei documenti
         */
        LOG.debug("Ricavo la sessione di versamento per l'UD id={}", idUnitaDoc);
        String queryStr = "select t from VrsSessioneVers t " + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "and t.aroDoc is null " + "and t.tiStatoSessioneVers = 'CHIUSA_OK' "
                + "and t.tiSessioneVers = 'VERSAMENTO' ";

        javax.persistence.Query query = em.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);

        List<VrsSessioneVers> vsv = query.getResultList();
        if (!vsv.isEmpty()) {
            SessioneVersamentoExt sveVersamentoOrig = new SessioneVersamentoExt();
            sveVersamentoOrig.setIdUnitaDoc(vsv.get(0).getAroUnitaDoc().getIdUnitaDoc());
            sveVersamentoOrig.setIdSessioneVers(vsv.get(0).getIdSessioneVers());
            sveVersamentoOrig.setDataSessioneVers(vsv.get(0).getDtChiusura());
            sveVersamentoOrig.setTipoSessione(Constants.TipoSessione.valueOf(vsv.get(0).getTiSessioneVers()));
            //
            /*
             * ricavo i documenti XML relativi alla sessione di versamento individuata
             */
            queryStr = "select xml from VrsXmlDatiSessioneVers xml "
                    + "where xml.vrsDatiSessioneVers.vrsSessioneVers.idSessioneVers = :idSessioneVers "
                    + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' ";

            query = em.createQuery(queryStr);
            query.setParameter("idSessioneVers", sveVersamentoOrig.getIdSessioneVers());

            // Nota: dato che il tipo sessione è 'VERSAMENTO' si recuparano i metadata dell'unita documentaria (e non
            // del documento)
            // vedi : 't.tiSessioneVers = 'VERSAMENTO' nella query sopra
            Map<String, String> xmlVersamentoOs = objectStorageService
                    .getObjectSipUnitaDoc(sveVersamentoOrig.getIdUnitaDoc());

            List<VrsXmlDatiSessioneVers> vxdsv = query.getResultList();
            for (VrsXmlDatiSessioneVers xml : vxdsv) {
                SessioneVersamentoExt.DatiXml tmpDatiXml = new SessioneVersamentoExt().new DatiXml();
                tmpDatiXml.setTipoXmlDati(xml.getTiXmlDati());
                tmpDatiXml.setVersione(xml.getCdVersioneXml());
                // Il backend ORA è Object storage ma l'xml è già stato migrato? il controllo
                // sul null serve a questo
                if (!xmlVersamentoOs.isEmpty() && Strings.isNull(xml.getBlXml())) {
                    tmpDatiXml.setXml(xmlVersamentoOs.get(xml.getTiXmlDati()));
                } else {
                    tmpDatiXml.setXml(xml.getBlXml());
                }
                // EVO#16486
                // Recupero lo urn ORIGINALE dalla tabella VRS_URN_XML_SESSIONE_VERS (EVO#16486)
                VrsUrnXmlSessioneVers urnXmlSessioneVers = (VrsUrnXmlSessioneVers) CollectionUtils.find(
                        xml.getVrsUrnXmlSessioneVers(),
                        object -> ((VrsUrnXmlSessioneVers) object).getTiUrn().equals(TiUrnXmlSessioneVers.ORIGINALE));
                if (urnXmlSessioneVers != null) {
                    tmpDatiXml.setUrn(urnXmlSessioneVers.getDsUrn());
                } else {
                    // MEV#26219
                    switch (xml.getTiXmlDati()) {
                    case CostantiDB.TipiXmlDati.RICHIESTA:
                        // calcolo ORIGINALE
                        tmpDatiXml.setUrn(MessaggiWSFormat.formattaUrnIndiceSip(baseUrnUnitaDoc,
                                Costanti.UrnFormatter.URN_INDICE_SIP_V2));
                        break;
                    case CostantiDB.TipiXmlDati.RISPOSTA:
                        // calcolo ORIGINALE
                        tmpDatiXml.setUrn(MessaggiWSFormat.formattaUrnEsitoVers(baseUrnUnitaDoc,
                                Costanti.UrnFormatter.URN_ESITO_VERS_V2));
                        break;
                    case CostantiDB.TipiXmlDati.RAPP_VERS:
                        // calcolo ORIGINALE
                        tmpDatiXml.setUrn(MessaggiWSFormat.formattaUrnRappVers(baseUrnUnitaDoc,
                                Costanti.UrnFormatter.URN_RAPP_VERS_V2));
                        break;
                    case CostantiDB.TipiXmlDati.INDICE_FILE:
                        // calcolo ORIGINALE
                        tmpDatiXml.setUrn(MessaggiWSFormat.formattaUrnPiSip(baseUrnUnitaDoc,
                                Costanti.UrnFormatter.URN_PI_SIP_V2));
                        break;
                    default:
                        break;
                    }
                    // end MEV#26219
                }
                // end EVO#16486
                tmpDatiXml.setHash(xml.getDsHashXmlVers());
                tmpDatiXml.setAlgoritmo(xml.getDsAlgoHashXmlVers());
                tmpDatiXml.setEncoding(xml.getCdEncodingHashXmlVers());
                sveVersamentoOrig.getXmlDatiSessioneVers().add(tmpDatiXml);
            }
            tmpSveList.add(sveVersamentoOrig);
        } else {
            throw new ParerNoResultException(
                    "Errore interno: non sono stati eseguiti versamenti per l'UD " + idUnitaDoc);
        }

        LOG.debug("Lettura xml di versamento - FINE");
        return tmpSveList;
    }

    public List<ElvStatoElencoVer> retrieveStatiElencoByElencoVers(BigDecimal idElvElencoVers) {
        List<ElvStatoElencoVer> result = null;
        try {
            ElvElencoVer elenco = getEntityManager().find(ElvElencoVer.class, idElvElencoVers.longValue());
            Query query = getEntityManager().createQuery("SELECT e FROM ElvStatoElencoVer e "
                    + "WHERE e.elvElencoVer = :elvElencoVer ORDER BY e.tsStatoElencoVers ASC ");
            query.setParameter("elvElencoVer", elenco);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            LOG.error("Errore nell'estrazione degli stati", ex);
            throw ex;
        }
        return result;
    }

    public BigDecimal getPgStatoElencoVers(BigDecimal idElencoVers) {
        String queryStr = "SELECT statoElencoVers.pgStatoElencoVers " + "FROM ElvStatoElencoVer statoElencoVers "
                + "WHERE statoElencoVers.elvElencoVer.idElencoVers = :idElencoVers "
                + "ORDER BY statoElencoVers.pgStatoElencoVers DESC ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers.longValue());
        List<BigDecimal> risultato = query.getResultList();
        if (!risultato.isEmpty()) {
            return risultato.get(0);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getIdTiEveStatoElencoVers(String cdTiEveStatoElencoVers) {
        String queryStr = "SELECT tiEveStatoElencoVers.idTiEveStatoElencoVers FROM DecTiEveStatoElencoVers tiEveStatoElencoVers "
                + "WHERE tiEveStatoElencoVers.cdTiEveStatoElencoVers = :cdTiEveStatoElencoVers ";

        Query query = em.createQuery(queryStr);
        query.setParameter("cdTiEveStatoElencoVers", cdTiEveStatoElencoVers);
        List<Long> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return BigDecimal.valueOf(list.get(0));
        } else {
            return null;
        }
    }

    public List<ElvElencoVer> getElenchiFiscaliByStrutturaAperti(long idStrut, int anno) {
        Query query = em.createQuery("SELECT elenco FROM ElvElencoVer elenco " + "JOIN elenco.orgStrut strut "
                + "WHERE strut.idStrut = :idStrut " + "AND elenco.flElencoFisc = '1' "
                + "AND elenco.tiStatoElenco = 'APERTO' "
                + "AND EXISTS (SELECT unitaDoc.idUnitaDoc FROM AroUnitaDoc unitaDoc "
                + "WHERE unitaDoc.elvElencoVer = elenco " + "AND unitaDoc.aaKeyUnitaDoc = :anno) ");
        query.setParameter("anno", bigDecimalFromInteger(anno));
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }

    public boolean isStatoElencoCorrente(long idElencoVers,
            it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco tiStatoElenco) {
        Query query = em.createQuery("SELECT statoElenco FROM ElvStatoElencoVer statoElenco "
                + "JOIN statoElenco.elvElencoVer elencoVers " + "WHERE elencoVers.idElencoVers = :idElencoVers "
                + "AND statoElenco.tiStatoElenco = :tiStatoElenco "
                + "AND elencoVers.idStatoElencoVersCor = statoElenco.idStatoElencoVers ");
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);
        List<ElvStatoElencoVer> stati = query.getResultList();
        return stati != null && !stati.isEmpty();
    }

    // MAC#28509
    public List<ElvElencoVer> getElenchiFiscaliSoloDocAggMdByStrutturaAperti(long idStrut, int anno) {
        Query query = em.createQuery("SELECT elenco FROM ElvElencoVer elenco " + "JOIN elenco.orgStrut strut "
                + "WHERE strut.idStrut = :idStrut " + "AND elenco.flElencoFisc = '1' "
                + "AND elenco.tiStatoElenco = 'APERTO' " + "AND (EXISTS (SELECT doc FROM AroDoc doc "
                + "WHERE doc.tiCreazione = 'AGGIUNTA_DOCUMENTO' " + "AND doc.elvElencoVer = elenco "
                + "AND doc.aroUnitaDoc.aaKeyUnitaDoc = :anno " + "AND doc.aroUnitaDoc.elvElencoVer != elenco) "
                + "OR EXISTS (SELECT aggMd FROM AroUpdUnitaDoc aggMd " + "WHERE aggMd.elvElencoVer = elenco "
                + "AND aggMd.aroUnitaDoc.aaKeyUnitaDoc = :anno " + "AND aggMd.aroUnitaDoc.elvElencoVer != elenco))");
        query.setParameter("anno", bigDecimalFromInteger(anno));
        query.setParameter("idStrut", idStrut);
        return query.getResultList();
    }
    // end MAC#28509

}
