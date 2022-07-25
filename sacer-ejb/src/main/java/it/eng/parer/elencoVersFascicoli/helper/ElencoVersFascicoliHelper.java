package it.eng.parer.elencoVersFascicoli.helper;

import it.eng.parer.elencoVersFascicoli.utils.FasFascicoloObj;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.ElvDocAggDaElabElenco;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvFascDaElabElenco;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.ElvLogElencoVer;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.HsmElencoFascSesFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvFascDaElabElenco.TiStatoFascDaElab;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.volume.utils.DatiSpecQueryParams;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DecCriterioAttribBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "ElencoVersFascicoliHelper")
@LocalBean
public class ElencoVersFascicoliHelper extends GenericHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ElencoVersFascicoliHelper.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    public List<Long> retrieveElenchiDaProcessare(long idStrut) {
        Date systemDate = new Date();
        Query q = em.createQuery("SELECT elencoVersFasc.idElencoVersFasc "
                + "FROM ElvElencoVersFascDaElab elencoDaElab JOIN elencoDaElab.elvElencoVersFasc elencoVersFasc "
                + "WHERE (elencoDaElab.tiStato = :statoAperto " + "AND elencoVersFasc.dtScadChius < :systemDate "
                + "AND elencoDaElab.idStrut = :idStrut)");
        q.setParameter("statoAperto", TiStatoElencoFascDaElab.APERTO);
        q.setParameter("systemDate", systemDate);
        q.setParameter("idStrut", idStrut);
        List<Long> elenchi = q.getResultList();
        return elenchi;
    }

    public List<OrgStrut> retrieveStrutture() {
        Query q = em.createQuery("SELECT s FROM OrgStrut s ORDER BY s.idStrut");
        List<OrgStrut> strutture = q.getResultList();
        return strutture;
    }

    public List<DecCriterioRaggrFasc> retrieveCriterioByStrut(OrgStrut struttura, Date jobStartDate)
            throws ParseException {
        Query q = em.createQuery("SELECT crf " + "FROM DecCriterioRaggrFasc crf " + "WHERE crf.orgStrut = :struttura "
                + "AND crf.dtIstituz <= :jobStartDate " + "AND crf.dtSoppres > :jobStartDate "
                + "ORDER BY crf.dtIstituz");
        q.setParameter("struttura", struttura);
        q.setParameter("jobStartDate", jobStartDate);
        List<DecCriterioRaggrFasc> criteriRaggrFasc = q.getResultList();
        return criteriRaggrFasc;
    }

    public ElvElencoVersFasc retrieveElencoByCriterio(DecCriterioRaggrFasc criterio, BigDecimal aaFascicolo,
            OrgStrut struttura) throws ParerNoResultException {
        // Per un dato criterio ci può essere al massimo un elenco
        try {
            StringBuilder queryStr = new StringBuilder("SELECT elDaElab.elvElencoVersFasc "
                    + "FROM ElvElencoVersFascDaElab elDaElab " + "WHERE elDaElab.idCriterioRaggrFasc = :idCriterio "
                    + "AND elDaElab.idStrut = :idStruttura " + "AND elDaElab.tiStato = :statoAperto");

            boolean tuttiAnniFascicoloNulli = criterio.getAaFascicolo() == null && criterio.getAaFascicoloDa() == null
                    && criterio.getAaFascicoloA() == null;

            if (tuttiAnniFascicoloNulli) {
                queryStr.append(" AND elDaElab.aaFascicolo = :aaFascicolo ");
            } else {
                queryStr.append(" AND elDaElab.aaFascicolo IS NULL ");
            }

            Query q = em.createQuery(queryStr.toString());
            q.setParameter("idCriterio", criterio.getIdCriterioRaggrFasc());
            q.setParameter("idStruttura", struttura.getIdStrut());
            q.setParameter("statoAperto", TiStatoElencoFascDaElab.APERTO);
            if (tuttiAnniFascicoloNulli) {
                q.setParameter("aaFascicolo", aaFascicolo);
            }
            ElvElencoVersFasc elenco = (ElvElencoVersFasc) q.getSingleResult();
            return elenco;
        } catch (NoResultException ex) {
            throw new ParerNoResultException();
        }
    }

    /**
     * Seleziona i fascicoli che soddisfano il criterio di raggruppamento fascicoli passato come parametro ritornandole
     * sotto forma di insieme FasFascicoloObj
     *
     * @param criterio
     *            raggruppamento fascicoli
     * 
     * @return lista oggetti di tipo {@link FasFascicoloObj}
     */
    public List<FasFascicoloObj> retrieveFascicoliToProcess(DecCriterioRaggrFasc criterio) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT u.idFascicolo, u.aaFascicolo, u.tsVersFascicolo "
                + "FROM ElvVSelFascVers u " + "WHERE u.idCriterioRaggrFasc = :idCriterio ");

        if (criterio.getAaFascicolo() != null) {
            queryStr.append(" AND u.aaFascicolo = :aaFascicolo ");
        } else if (criterio.getAaFascicoloDa() != null || criterio.getAaFascicoloA() != null) {
            // if (criterio.getAaFascicoloDa() != null) {
            queryStr.append(" AND u.aaFascicolo >= :aaFascicoloDa ");
            // }
            // if (criterio.getAaFascicoloA() != null) {
            queryStr.append(" AND u.aaFascicolo <= :aaFascicoloA ");
            // }
        }

        // TODO: se per il criterio i filtri aa_fascicolo oppure aa_fascicolo_da o aa_fascicolo_a sono valorizzati,
        // fascicoli sono selezionati in ordine di data creazione;
        // se per il criterio i filtri aa_fascicolo e aa_fascicolo_da e aa_fascicolo_a non sono valorizzati,
        // i fascicoli sono selezionati in ordine di anno della chiave del fascicolo + data creazione;
        queryStr.append(" ORDER BY u.tsVersFascicolo");

        Query q = em.createQuery(queryStr.toString());
        q.setParameter("idCriterio", criterio.getIdCriterioRaggrFasc());

        if (criterio.getAaFascicolo() != null) {
            q.setParameter("aaFascicolo", criterio.getAaFascicolo());
        } else if (criterio.getAaFascicoloDa() != null || criterio.getAaFascicoloA() != null) {
            if (criterio.getAaFascicoloDa() != null) {
                q.setParameter("aaFascicoloDa", criterio.getAaFascicoloDa());
            } else {
                q.setParameter("aaFascicoloDa", 2000);
            }
            if (criterio.getAaFascicoloA() != null) {
                q.setParameter("aaFascicoloA", criterio.getAaFascicoloA());
            } else {
                q.setParameter("aaFascicoloA", Calendar.getInstance().get(Calendar.YEAR));
            }
        }

        List<Object[]> fasFascicoloObjectList = (List<Object[]>) q.getResultList();
        List<FasFascicoloObj> fasFascicoloObjSet = new ArrayList<>();

        for (Object[] fasFascicoloObject : fasFascicoloObjectList) {
            fasFascicoloObjSet.add(new FasFascicoloObj((BigDecimal) fasFascicoloObject[0],
                    Constants.TipoEntitaSacer.FASC, (BigDecimal) fasFascicoloObject[1], (Date) fasFascicoloObject[2]));
        }

        return fasFascicoloObjSet;
    }

    // METODI RIFATTI DA PAOLO DOPO MODIFICHE LOGICA DI SANDRO
    // (AGGIUNGO RIFLESSIONE A GENNAIO 2017: STIAMO PARLANDO DI QUALCOSA FATTO CIRCA 4 ANNI FA... PENSIERI,
    // IMPLEMENTAZIONI, OPERE E OMISSIONI ERANO STATE DETTATE DA DIVERSI FATTORI...
    // QUESTO COMMENTO VALE COME "PROMEMORIA" PER CHI UN DOMANI AVESSE DA "RIDIRE" SU EVENTUALI TECNICHE DI SVILUPPO
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

    public ReturnParams buildQueryForDatiSpec(List datiSpecList) {
        ReturnParams retParams = new ReturnParams();
        StringBuilder queryStr = new StringBuilder();
        // UTILIZZO DEI DATI SPECIFICI
        String operatore = null;
        String filtro = null;
        int entityNameSuffix = 0;
        int indiceidattribds = 0;
        List<DatiSpecQueryParams> mappone = new ArrayList<>();
        List<DefinitoDaBean> listaDefinitoDa = new ArrayList<>();
        Set<String> insiemeTipiUnitaDoc = new HashSet();
        Set<String> insiemeTipiDoc = new HashSet();
        Set<String> insiemeSistemiMigrazUniDoc = new HashSet();
        Set<String> insiemeSistemiMigrazDoc = new HashSet();

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
                            String where = ".idUnitaDoc = u.idUnitaDoc";
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
        /* Set fascicoli non selezionati da schedulatore */
        // 1) registro un nuovo stato pari a NON_SELEZ_SCHED (tabella FAS_STATO_FASCICOLO_ELENCO) per tutti i fascicoli
        // appartenenti all’elenco
        // presenti nella tabella ELV_FASC_DA_ELAB_ELENCO (che e’ filtrata mediante la struttura corrente
        // e con data creazione inferiore alla data di inizio della creazione automatica degli elenchi e con stato =
        // IN_ATTESA_SCHED)
        List<FasFascicolo> fasFascicoloList = retrieveFascicoliInQueue(struttura, logJob);
        for (FasFascicolo ff : fasFascicoloList) {
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(ff);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.NON_SELEZ_SCHED);

            ff.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
        }
        LOG.debug("Elenco Versamento Fascicoli - Trovati nella coda di elaborazione " + fasFascicoloList.size()
                + " fascicoli non schedulati relativi alla struttura '" + struttura.getNmStrut()
                + "'. Registro nuovo stato 'NON_ELAB_SCHED'");

        // 2) aggiorno tutti i fascicoli (tabella FAS_FASCICOLO) presenti nella tabella ELV_FASC_DA_ELAB_ELENCO
        // (che è filtrata mediante la struttura corrente e con data creazione inferiore alla data di inizio della
        // creazione automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED), assegnando stato = NON_SELEZ_SCHED
        Query q1 = em.createQuery("UPDATE FasFascicolo ff SET ff.tiStatoFascElencoVers = :nonSelezSched "
                + "WHERE EXISTS (" + "SELECT ffDaElab "
                + "FROM ElvFascDaElabElenco ffDaElab JOIN ffDaElab.fasFascicolo ff1 " + "WHERE ff1 = ff "
                + "AND ffDaElab.idStrut = :idStrut " + "AND ffDaElab.tsVersFascicolo < :startJobTime "
                + "AND ffDaElab.tiStatoFascDaElab = :inAttesaSched)");
        q1.setParameter("startJobTime", logJob.getDtRegLogJob());
        q1.setParameter("idStrut", struttura.getIdStrut());
        q1.setParameter("nonSelezSched",
                it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers.NON_SELEZ_SCHED);
        q1.setParameter("inAttesaSched", TiStatoFascDaElab.IN_ATTESA_SCHED);
        int updated1 = q1.executeUpdate();
        LOG.debug("Elenco Versamento Fascicoli - Trovati " + updated1
                + " fascicoli non schedulati relativi alla struttura '" + struttura.getNmStrut()
                + "'. Assegno 'NON_ELAB_SCHED'");

        // 3) aggiorno tutti i fascicoli (tabella ELV_FASC_DA_ELAB_ELENCO) appartenenti alla struttura corrente
        // e presenti nella coda da elaborare con data creazione inferiore alla data di inizio della creazione
        // automatica degli elenchi
        // e con stato = IN_ATTESA_SCHED, assegnando stato = NON_SELEZ_SCHED
        Query q2 = em.createQuery("UPDATE ElvFascDaElabElenco ffDaElab SET ffDaElab.tiStatoFascDaElab = :nonSelezSched "
                + "WHERE ffDaElab.tsVersFascicolo < :startJobTime " + "AND ffDaElab.idStrut = :idStrut "
                + "AND ffDaElab.tiStatoFascDaElab = :inAttesaSched");
        q2.setParameter("startJobTime", logJob.getDtRegLogJob());
        q2.setParameter("idStrut", struttura.getIdStrut());
        q2.setParameter("nonSelezSched", TiStatoFascDaElab.NON_SELEZ_SCHED);
        q2.setParameter("inAttesaSched", TiStatoFascDaElab.IN_ATTESA_SCHED);
        int updated2 = q2.executeUpdate();
        LOG.debug("Elenco Versamento Fascicoli - Trovati nella coda di elaborazione " + updated2
                + " fascicoli non schedulati relativi alla struttura '" + struttura.getNmStrut()
                + "'. Assegno 'NON_ELAB_SCHED'");
    }

    public List<FasFascicolo> retrieveFascicoliInQueue(OrgStrut struttura, LogJob logJob) {
        List<FasFascicolo> fascicoliInQueue = null;
        Query q = em.createQuery("SELECT ff FROM FasFascicolo ff " + "WHERE EXISTS (" + "SELECT ffDaElab "
                + "FROM ElvFascDaElabElenco ffDaElab JOIN ffDaElab.fasFascicolo ff1 " + "WHERE ff1 = ff "
                + "AND ffDaElab.idStrut = :idStrut " + "AND ffDaElab.tsVersFascicolo < :startJobTime "
                + "AND ffDaElab.tiStatoFascDaElab = :inAttesaSched)");
        q.setParameter("idStrut", struttura.getIdStrut());
        q.setParameter("startJobTime", logJob.getDtRegLogJob());
        q.setParameter("inAttesaSched", TiStatoFascDaElab.IN_ATTESA_SCHED);
        fascicoliInQueue = q.getResultList();
        return fascicoliInQueue;
    }

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
        q.setParameter("unitaDoc", unitaDoc);
        q.setParameter("TIPO_CREAZIONE", "VERSAMENTO_UNITA_DOC"); // TODO: inserire ENUM

        long numDocsInUd = ((Long) q.getSingleResult()).longValue();
        LOG.debug("ADV - Trovati '" + numDocsInUd + "' documenti all'interno dell'unità  documentale " + unitaDoc);
        return numDocsInUd;
    }

    public List<AroCompDoc> retrieveCompsInDoc(AroDoc doc) {
        Query q = em.createQuery("SELECT comp " + "FROM AroDoc doc " + "JOIN doc.aroStrutDocs aroStrutDoc "
                + "JOIN aroStrutDoc.aroCompDocs comp " + "WHERE comp.aroCompDoc is null " + "AND doc.idDoc = :idDoc");
        q.setParameter("idDoc", doc.getIdDoc());
        List<AroCompDoc> comps = q.getResultList();
        return comps;
    }

    /**
     * Restituisce il numero dei componenti e la somma della dimensione dei componenti appartenenti all'unità
     * documentaria corrente con tipo creazione uguale a VERSAMENTO_UNITA_DOC
     *
     * @param unitaDocId
     *            id unita doc
     * 
     * @return Object entity AroUnitaDoc
     */
    public Object numCompsAndSizeInUnitaDocCustom(BigDecimal unitaDocId) {
        Query q = em.createQuery("SELECT count(comp.idCompDoc), SUM(comp.niSizeFileCalc) "
                + "FROM AroUnitaDoc unitaDoc " + "JOIN unitaDoc.aroDocs doc " + "JOIN doc.aroStrutDocs strutDoc "
                + "JOIN strutDoc.aroCompDocs comp " + "WHERE doc.aroUnitaDoc.idUnitaDoc = :unitaDocId "
                + "AND doc.tiCreazione = :TIPO_CREAZIONE");
        q.setParameter("unitaDocId", unitaDocId);
        q.setParameter("TIPO_CREAZIONE", "VERSAMENTO_UNITA_DOC"); // TODO: inserire ENUM
        Object result = q.getSingleResult();
        return result;
    }

    /**
     * Restituisce il numero dei componenti e la somma della dimensione dei componenti appartenenti al documento
     * corrente
     *
     * @param docId
     *            id documento
     * 
     * @return Object entity AroDoc
     */
    public Object numCompsAndSizeInDoc(BigDecimal docId) {
        Query q = em.createQuery("SELECT count(comp.idCompDoc), SUM(comp.niSizeFileCalc) " + "FROM AroDoc doc "
                + "JOIN doc.aroStrutDocs strutDoc " + "JOIN strutDoc.aroCompDocs comp " + "WHERE doc.idDoc = :docId");
        q.setParameter("docId", docId);
        Object result = q.getSingleResult();
        return result;
    }

    public void deleteFasFascicoloFromQueue(FasFascicolo ff) {
        ElvFascDaElabElenco fascDaElab;
        Query q = em.createQuery(
                "select fascDaElab from ElvFascDaElabElenco fascDaElab where fascDaElab.fasFascicolo.idFascicolo = :idFascicolo");
        q.setParameter("idFascicolo", ff.getIdFascicolo());
        try {
            fascDaElab = (ElvFascDaElabElenco) q.getSingleResult();
        } catch (NoResultException ex) {
            fascDaElab = null;
        }
        if (fascDaElab != null) {
            em.remove(fascDaElab);
            LOG.debug("ADV - Eliminato fascicolo con id = " + ff.getIdFascicolo() + " dalla coda di elaborazione");
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
            LOG.debug("ADV - Eliminato il documento con id = " + doc.getIdDoc() + " dalla coda di elaborazione");
        }
    }

    public List<FasFascicolo> retrieveFasFascicoliInElenco(ElvElencoVersFasc elenco) {
        List<FasFascicolo> fasFascicoliList = elenco.getFasFascicoli();
        return fasFascicoliList;
    }

    public ElvElencoVersFasc retrieveElencoById(Long idElenco) {
        ElvElencoVersFasc elenco = em.find(ElvElencoVersFasc.class, idElenco);
        return elenco;
    }

    public ElvStatoElencoVersFasc retrieveStatoElencoByIdElencoVersFascStato(Long idElencoVersFasc,
            TiStatoElencoFasc status) {
        ElvStatoElencoVersFasc elvStatoElencoVersFasc;
        Query q = em.createQuery(
                "select statoElenco from ElvStatoElencoVersFasc statoElenco where statoElenco.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc and statoElenco.tiStato = :tiStato");
        q.setParameter("idElencoVersFasc", idElencoVersFasc);
        q.setParameter("tiStato", status);
        try {
            elvStatoElencoVersFasc = (ElvStatoElencoVersFasc) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new EJBException("Errore nel reperimento dello stato elenco versamento fascicoli");
        }
        return elvStatoElencoVersFasc;
    }

    public void writeLogElencoVers(ElvElencoVersFasc elenco, OrgStrut struttura, long user, String tipoOper,
            FasFascicolo ff) {
        writeLogElencoVers(elenco, struttura, user, tipoOper, ff, null);
    }

    public void writeLogElencoVers(ElvElencoVersFasc elenco, OrgStrut struttura, long user, String tipoOper) {
        writeLogElencoVers(elenco, struttura, user, tipoOper, null, null);
    }

    public void writeLogElencoVers(ElvElencoVersFasc elenco, OrgStrut struttura, String tipoOper, LogJob logJob) {
        writeLogElencoVers(elenco, struttura, null, tipoOper, null, logJob);
    }

    // TODO: verificare perchè la tabella LOG è assente
    public void writeLogElencoVers(ElvElencoVersFasc elenco, OrgStrut struttura, Long user, String tipoOper,
            FasFascicolo ff, LogJob logJob) {
        ElvLogElencoVer logElenco = new ElvLogElencoVer();
        Date date = new Date();
        logElenco.setOrgStrut(struttura);
        logElenco.setTmOper(new Timestamp(date.getTime()));
        logElenco.setTiOper(tipoOper);
        if (user != null) {
            logElenco.setIamUser(em.find(IamUser.class, user));
        }
        /*
         * logElenco.setIdElencoVers(new BigDecimal(elenco.getIdElencoVersFasc()));
         * logElenco.setNmElenco(elenco.getNmElenco()); // TODO: verificare perche il campo non c'è if (ff != null) {
         * //TODO: controllare perche il campo sotto non c'è //logElenco.setIdDoc(new BigDecimal(doc.getIdDoc()));
         * logElenco.setPgDoc(doc.getPgDoc()); logElenco.setTiDoc(doc.getTiDoc()); } if (unitaDoc != null) {
         * logElenco.setCdRegistroKeyUnitaDoc(unitaDoc.getCdRegistroKeyUnitaDoc());
         * logElenco.setAaKeyUnitaDoc(unitaDoc.getAaKeyUnitaDoc());
         * logElenco.setCdKeyUnitaDoc(unitaDoc.getCdKeyUnitaDoc()); } if (logJob != null) { logElenco.setLogJob(logJob);
         * }
         */
        em.persist(logElenco);
        em.flush();
    }

    public OrgStrut retrieveOrgStrutByid(BigDecimal idStrut) {
        OrgStrut orgStrut = em.find(OrgStrut.class, idStrut.longValue());
        return orgStrut;
    }

    public LogJob retrieveLogJobByid(long idLogJob) {
        LogJob logJob = em.find(LogJob.class, idLogJob);
        return logJob;
    }

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

    public ElvElencoVersFasc writeNewElenco(ElvElencoVersFasc elenco) {
        elenco = em.merge(elenco);
        return elenco;
    }

    public ElvStatoElencoVersFasc writeNewStatoElenco(ElvStatoElencoVersFasc statoElenco) {
        statoElenco = em.merge(statoElenco);
        return statoElenco;
    }

    public FasFascicolo retrieveFasFascicoloById(long idFascicolo) {
        FasFascicolo fasFascicolo = em.find(FasFascicolo.class, idFascicolo);
        return fasFascicolo;
    }

    public AroUnitaDoc retrieveAndLockUnitaDocById(long idUnitaDoc) {
        AroUnitaDoc unitaDoc = em.find(AroUnitaDoc.class, idUnitaDoc, LockModeType.PESSIMISTIC_WRITE);
        return unitaDoc;
    }

    public void lockElenco(ElvElencoVersFasc elenco) {
        em.lock(elenco, LockModeType.PESSIMISTIC_WRITE);
    }

    public void lockFasFascicolo(FasFascicolo ff) {
        em.lock(ff, LockModeType.PESSIMISTIC_WRITE);
    }

    public AroDoc retrieveDocById(long idDoc) {
        AroDoc doc = em.find(AroDoc.class, idDoc);
        return doc;
    }

    public void lockDoc(AroDoc doc) {
        em.lock(doc, LockModeType.PESSIMISTIC_WRITE);
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

    public void detachAroCompDoc(AroCompDoc compDoc) {
        em.detach(compDoc);
    }

    public DecCriterioRaggrFasc retrieveCriterioByid(long idCriterio) {
        DecCriterioRaggrFasc criterio = em.find(DecCriterioRaggrFasc.class, idCriterio);
        return criterio;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atomicSetNonElabSched(OrgStrut struttura, LogJob logJob) {
        setNonElabSched(struttura, logJob);
    }

    /**
     * Il sistema aggiorna l’elenco specificando l’identificatore dello stato corrente passato in input.
     *
     * @param elenco
     *            - elenco corrente
     * @param idStatoElencoVersFasc
     *            stato elenco versamento corrente
     */
    public void aggiornaStatoInElencoCor(ElvElencoVersFasc elenco, BigDecimal idStatoElencoVersFasc) {
        elenco.setIdStatoElencoVersFascCor(idStatoElencoVersFasc);
        em.persist(elenco);
    }

    public boolean checkFascicoloAnnullato(FasFascicolo ff) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean annullato = false;
        Date dataAnnullamento;
        Date defaultAnnullamento = sdf.parse("2444-12-31");
        Query q = em.createQuery("select ff.dtAnnull " + "from FasFascicolo ff " + "where ff.idFascicolo = :idFf");
        q.setParameter("idFf", ff.getIdFascicolo());
        dataAnnullamento = (java.util.Date) q.getSingleResult();
        if (dataAnnullamento.getTime() != defaultAnnullamento.getTime()) {
            annullato = true;
        }
        LOG.debug("Fascicolo: '" + ff.getIdFascicolo() + "' annullato: " + annullato);
        return annullato;
    }

    public boolean checkFreeSpaceElenco(ElvElencoVersFasc elenco) {
        boolean fasFascicoliOk = false;
        if (elenco.getNiFascVersElenco().intValue() < elenco.getNiMaxFascCrit().intValue()) {
            fasFascicoliOk = true;
        }
        LOG.debug("Ok = " + fasFascicoliOk + " Num fascicoli inseriti: " + elenco.getNiFascVersElenco().intValue() + "/"
                + elenco.getNiMaxFascCrit().intValue());
        return fasFascicoliOk;
    }

    public List<Long> retrieveIdElenchiDaElaborare(BigDecimal idStrut, TiStatoElencoFascDaElab statoElenco) {
        Query q = em.createQuery("SELECT elenco.idElencoVersFasc "
                + "FROM ElvElencoVersFasc elenco JOIN elenco.elvElencoVersFascDaElabs elDaElab "
                + "WHERE elDaElab.idStrut = :idStrut " + "AND elDaElab.tiStato = :statoElenco "
                + "ORDER BY elenco.tsCreazioneElenco");
        q.setParameter("idStrut", idStrut);
        q.setParameter("statoElenco", statoElenco);
        List<Long> idElenchi = q.getResultList();
        return idElenchi;
    }

    public void deleteElencoVersFascDaElab(Long idElencoVersFascDaElab) {
        ElvElencoVersFascDaElab eevfde = em.find(ElvElencoVersFascDaElab.class, idElencoVersFascDaElab);
        em.remove(eevfde);
    }

    public void storeFileIntoElenco(ElvElencoVersFasc elenco, byte[] file, String fileType, Date dtCreazioneFile,
            String dsHashFile, String dsAlgoHashFile, String cdEncodingHashFile, String dsUrnFile,
            String dsUrnNormalizFile, String cdVerXsdFile) {
        ElvFileElencoVersFasc fileIndexElencoVersFasc = new ElvFileElencoVersFasc();
        fileIndexElencoVersFasc.setBlFileElencoVers(file);
        fileIndexElencoVersFasc.setTiFileElencoVers(fileType);
        fileIndexElencoVersFasc.setElvElencoVersFasc(elenco);
        fileIndexElencoVersFasc.setIdStrut(new BigDecimal(elenco.getOrgStrut().getIdStrut()));
        fileIndexElencoVersFasc.setDtCreazioneFile(dtCreazioneFile);
        fileIndexElencoVersFasc.setDsHashFile(dsHashFile);
        fileIndexElencoVersFasc.setDsAlgoHashFile(dsAlgoHashFile);
        fileIndexElencoVersFasc.setCdEncodingHashFile(cdEncodingHashFile);
        fileIndexElencoVersFasc.setDsUrnFile(dsUrnFile);
        fileIndexElencoVersFasc.setDsUrnNormalizFile(dsUrnNormalizFile);
        fileIndexElencoVersFasc.setCdVerXsdFile(cdVerXsdFile);
        List<ElvFileElencoVersFasc> fileIndexElencoConservList = elenco.getElvFileElencoVersFasc();
        fileIndexElencoConservList.add(fileIndexElencoVersFasc);
        elenco.setElvFileElencoVersFasc(fileIndexElencoConservList);
    }

    public ElvElencoVersFascDaElab retrieveElencoInQueue(ElvElencoVersFasc elenco) {
        ElvElencoVersFascDaElab elencoVersFascDaElab = null;
        Query q = em.createQuery("SELECT elencoVersFascDaElab " + "FROM ElvElencoVersFascDaElab elencoVersFascDaElab "
                + "WHERE elencoVersFascDaElab.elvElencoVersFasc.idElencoVersFasc = :idElenco");
        q.setParameter("idElenco", elenco.getIdElencoVersFasc());
        // try {
        elencoVersFascDaElab = (ElvElencoVersFascDaElab) q.getSingleResult();
        // } catch (NoResultException ex) {
        // return null;
        // }
        return elencoVersFascDaElab;
    }

    public List<HsmElencoFascSesFirma> retrieveListaElencoInError(ElvElencoVersFasc elenco,
            TiEsitoFirmaElencoFasc esito) {
        Query q = em.createQuery("SELECT elencoInError " + "FROM HsmElencoFascSesFirma elencoInError "
                + "WHERE elencoInError.elvElencoVersFasc.idElencoVersFasc = :idElenco "
                + "AND elencoInError.tiEsito = :esito");
        q.setParameter("idElenco", elenco.getIdElencoVersFasc());
        q.setParameter("esito", esito);
        List<HsmElencoFascSesFirma> hsmElencoFascSesFirma = q.getResultList();

        return hsmElencoFascSesFirma;
    }

    /**
     * Registra un nuovo stato status per tutti i fascicoli presenti nell'elenco
     *
     * @param elenco
     *            entity ElvElencoVersFasc
     * @param status
     *            entity TiStatoFascElenco
     */
    public void setStatoFascicoloElenco(ElvElencoVersFasc elenco, TiStatoFascElenco status) {
        List<FasFascicolo> fasFascicoli = retrieveFasFascicoliInElenco(elenco);
        for (FasFascicolo ff : fasFascicoli) {
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(ff);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(status);

            ff.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
            LOG.debug(" - Registrato per il fascicolo '" + ff.getIdFascicolo() + "' nuovo stato " + status.name());
        }
    }

    /**
     * Assegna lo stato status a tutti i fascicoli presenti nell'elenco
     *
     * @param elenco
     *            entity ElvElencoVersFasc
     * @param status
     *            entity TiStatoFascElencoVers
     */
    public void setFasFascicoliStatus(ElvElencoVersFasc elenco,
            it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers status) {
        List<FasFascicolo> fasFascicoli = retrieveFasFascicoliInElenco(elenco);
        for (FasFascicolo ff : fasFascicoli) {
            ff.setTiStatoFascElencoVers(status);
            LOG.debug(" - Assegnato al fascicolo '" + ff.getIdFascicolo() + "' lo stato " + status.name());
        }
    }

    public void deleteElvElencoVersFasc(BigDecimal idElencoVersFasc) {
        em.remove(em.find(ElvElencoVersFasc.class, idElencoVersFasc.longValue()));
        // em.flush();
    }

    public void insertFascCodaFascDaElab(long idFascicolo, TiStatoFascDaElab status) {
        ElvFascDaElabElenco fascVersDaElab = new ElvFascDaElabElenco();
        FasFascicolo fasFascicolo = em.find(FasFascicolo.class, idFascicolo);
        fascVersDaElab.setFasFascicolo(fasFascicolo);
        fascVersDaElab.setIdStrut(new BigDecimal(fasFascicolo.getOrgStrut().getIdStrut()));
        fascVersDaElab.setAaFascicolo(fasFascicolo.getAaFascicolo());
        fascVersDaElab.setTsVersFascicolo(fasFascicolo.getTsIniSes());
        fascVersDaElab.setTiStatoFascDaElab(status);
        fascVersDaElab.setIdTipoFascicolo(new BigDecimal(fasFascicolo.getDecTipoFascicolo().getIdTipoFascicolo()));
        fasFascicolo.getElvFascDaElabElencos().add(fascVersDaElab);
        em.persist(fascVersDaElab);
        em.flush();
    }

    public ElvStatoElencoVersFasc getStatoElencoByIdElencoVersFascStato(Long idElencoVersFasc,
            TiStatoElencoFasc status) {
        ElvStatoElencoVersFasc elvStatoElencoVersFasc;
        Query q = em.createQuery(
                "SELECT statoElenco FROM ElvStatoElencoVersFasc statoElenco WHERE statoElenco.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc AND statoElenco.tiStato = :tiStato");
        q.setParameter("idElencoVersFasc", idElencoVersFasc);
        q.setParameter("tiStato", status);
        try {
            elvStatoElencoVersFasc = (ElvStatoElencoVersFasc) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new EJBException("Errore nel reperimento dello stato elenco versamento fascicoli");
        }
        return elvStatoElencoVersFasc;
    }

    public ElvElencoVersFascDaElab getElvElencoVersFascDaElabByIdElencoVersFasc(long idElencoVersFasc) {
        String queryStr = "SELECT u FROM ElvElencoVersFascDaElab u "
                + "WHERE u.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        return (ElvElencoVersFascDaElab) query.getResultList().get(0);
    }

    public ElvFileElencoVersFasc retrieveElvFileElencoVersFasc(long idElencoVersFasc, String tiFileElencoVers) {
        Query query = getEntityManager().createNamedQuery("ElvFileElencoVersFasc.findByIdElencoTipoFile",
                ElvFileElencoVersFasc.class);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiFileElencoVers", tiFileElencoVers);
        List list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return (ElvFileElencoVersFasc) query.getResultList().get(0);
        } else {
            return null;
        }
    }

    public byte[] retrieveFileIndiceElenco(long idElencoVersFasc, String tiFileElencoVers) {
        String queryStr = "SELECT u.blFileElencoVers FROM ElvFileElencoVersFasc u "
                + "WHERE u.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND u.tiFileElencoVers = :tiFileElencoVers ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiFileElencoVers", tiFileElencoVers);
        List list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            return (byte[]) query.getResultList().get(0);
        } else {
            return null;
        }
    }

    public ElvFileElencoVersFasc getFileIndiceElenco(long idElencoVersFasc, String tiFileElencoVers) {
        String queryStr = "SELECT u FROM ElvFileElencoVersFasc u "
                + "WHERE u.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND u.tiFileElencoVers IN :tiFileElencoVers";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiFileElencoVers", Arrays.asList(tiFileElencoVers));
        List<ElvFileElencoVersFasc> elencoList = (List<ElvFileElencoVersFasc>) query.getResultList();
        if (!elencoList.isEmpty()) {
            return elencoList.get(0);
        } else {
            return null;
        }
    }

    public List<ElvFileElencoVersFasc> retrieveFileIndiceElenco(long idElencoVersFasc, String... tiFileElencoVers) {
        String queryStr = "SELECT new it.eng.parer.entity.ElvFileElencoVersFasc(u.blFileElencoVers, u.cdVerXsdFile, u.tiFileElencoVers) FROM ElvFileElencoVersFasc u "
                + "WHERE u.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc AND u.tiFileElencoVers IN :tiFileElencoVers";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiFileElencoVers", Arrays.asList(tiFileElencoVers));
        return query.getResultList();
    }

    /**
     * Restituisce il numero dei fascicoli versati in elenco
     *
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     * 
     * @return long risultato
     */
    public long contaFascVersati(Long idElencoVersFasc) {
        String queryStr = "SELECT COUNT(f) " + "FROM FasFascicolo f "
                + "WHERE f.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        Long num = (Long) query.getSingleResult();
        return num != null ? num : 0L;
    }

    public List<Long> retrieveElenchiIndiciAipFascicoliDaProcessare() {
        Query q = em.createQuery("SELECT elencoVersFasc.idElencoVersFasc "
                + "FROM ElvElencoVersFascDaElab elencoDaElab " + "JOIN elencoDaElab.elvElencoVersFasc elencoVersFasc "
                + "JOIN elencoVersFasc.elvStatoElencoVersFascicoli statoElencoVersoFasc "
                + "WHERE elencoDaElab.tiStato = :tiStato " + "AND statoElencoVersoFasc.tiStato = :tiStatoFirmato "
                + "ORDER BY statoElencoVersoFasc.tsStato ASC");
        q.setParameter("tiStato", TiStatoElencoFascDaElab.AIP_CREATI);
        q.setParameter("tiStatoFirmato", TiStatoElencoFasc.FIRMATO);
        List<Long> elenchi = q.getResultList();
        return elenchi;
    }

    /**
     * Verifica se nell'elenco passato in ingresso esiste almeno un fascicolo annullato
     *
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     * 
     * @return true/false
     */
    public boolean existFascVersAnnullati(BigDecimal idElencoVersFasc) {
        boolean result;
        String queryUdStr = "SELECT fasFascicolo FROM FasFascicolo fasFascicolo "
                + "WHERE fasFascicolo.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND fasFascicolo.tiStatoConservazione = :tiStatoConservazione ";
        Query queryUd = em.createQuery(queryUdStr);
        queryUd.setParameter("idElencoVersFasc", idElencoVersFasc.longValue());
        queryUd.setParameter("tiStatoConservazione", TiStatoConservazione.ANNULLATO);
        result = queryUd.getResultList().isEmpty();

        return !result;
    }

    public List<FasFascicolo> getFasFascicoloVersatiElenco(BigDecimal idElencoVersFasc) {
        String queryFfStr = "SELECT fasFascicolo FROM FasFascicolo fasFascicolo "
                + "WHERE fasFascicolo.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc ";
        Query queryFf = em.createQuery(queryFfStr);
        queryFf.setParameter("idElencoVersFasc", idElencoVersFasc.longValue());
        return (List<FasFascicolo>) queryFf.getResultList();
    }

    /**
     * Ottieni l'insieme dei fascicoli appartenenti all'elenco.
     *
     * @param idElenco
     *            id dell'elenco validato.
     * 
     * @return Set - insieme di id <strong>distinti</strong>
     */
    public Set<Long> retrieveFascVersInElenco(long idElenco) {
        TypedQuery<Long> q1 = em.createQuery(
                "SELECT ff.idFascicolo FROM FasFascicolo ff WHERE ff.elvElencoVersFasc.idElencoVersFasc = :idElenco ",
                Long.class);
        q1.setParameter("idElenco", idElenco);
        List<Long> l1 = q1.getResultList();
        Set<Long> hs = new HashSet<>();
        hs.addAll(l1);
        return hs;
    }

    /**
     * Ottieni l'insieme dei fascicoli appartenenti all'elenco. L'elenco contiene tutti i fascicoli il cui stato
     * relativo all'elenco sia pari a IN_ELENCO_CON_AIP_CREATO
     *
     * @param idElenco
     *            id dell'elenco validato.
     * 
     * @return Set - insieme di id <strong>distinti</strong>
     */
    public Set<Long> retrieveFascVersInElencoAipCreato(long idElenco) {
        TypedQuery<Long> q1 = em.createQuery("SELECT ff.idFascicolo " + "FROM FasFascicolo ff "
                + " WHERE ff.elvElencoVersFasc.idElencoVersFasc = :idElenco " + " AND ff.dtAnnull = {d '2444-12-31'} "
                + " AND ff.tiStatoFascElencoVers = :tiStatoFascElencoVers ", Long.class);

        q1.setParameter("idElenco", idElenco);
        q1.setParameter("tiStatoFascElencoVers", TiStatoFascElencoVers.IN_ELENCO_CON_AIP_CREATO);
        List<Long> l1 = q1.getResultList();

        Set<Long> hs = new HashSet<>();
        hs.addAll(l1);

        return hs;
    }

}
