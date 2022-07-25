package it.eng.parer.web.helper;

import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm.CreaCriterioRaggr;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploTableBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.viewEntity.DecVRicCriterioRaggr;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.DecVCreaCritRaggrRegistro;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoDoc;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoUd;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggrHelper extends GenericHelper {

    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    private static final Logger log = LoggerFactory.getLogger(CriteriRaggrHelper.class.getName());

    public Long saveCritRaggr(LogParam param, CreaCriterioRaggr filtri, Date[] dateCreazioneValidate,
            BigDecimal idStruttura, String nome, String criterioStandard) throws EMFError, ParerUserError {
        DecCriterioRaggr record = new DecCriterioRaggr();
        if (nome != null) {
            // Se c'è il parametro nome, carico il criterio di raggruppamento corrispondente
            String queryStr = "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idstrut", idStruttura);
            query.setParameter("nomecrit", nome);
            record = (DecCriterioRaggr) query.getSingleResult();
        }

        if (record.getDecCriterioFiltroMultiplos() == null) {
            record.setDecCriterioFiltroMultiplos(new ArrayList<DecCriterioFiltroMultiplo>());
        }

        StringBuilder queryStr = new StringBuilder("SELECT u FROM OrgStrut u WHERE u.idStrut = :idstrut");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", idStruttura);
        record.setOrgStrut((OrgStrut) query.getSingleResult());

        // Setto i filtri multipli a 0 come default
        record.setFlFiltroTipoUnitaDoc("0");
        record.setFlFiltroTipoDoc("0");
        record.setFlFiltroSistemaMigraz("0");
        record.setFlFiltroRegistroKey("0");
        record.setFlFiltroRangeRegistroKey("0");
        record.setFlFiltroTiEsitoVerifFirme("0");

        // Per ogni tipo unità doc creo un record filtro multiplo
        queryStr = new StringBuilder("SELECT u FROM DecTipoUnitaDoc u ");
        if (filtri.getNm_tipo_unita_doc().parse() != null && filtri.getNm_tipo_unita_doc().parse().size() > 0) {
            queryStr.append("WHERE u.idTipoUnitaDoc in :idtipoud");
            query = getEntityManager().createQuery(queryStr.toString());
            List<BigDecimal> asList = filtri.getNm_tipo_unita_doc().parse();
            query.setParameter("idtipoud", asList);
            List<DecTipoUnitaDoc> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroTipoUnitaDoc("1");
                for (DecTipoUnitaDoc tipo : lista) {
                    // Se siamo nel caso di modifica di un criterio, devo verificare se i filtri sono già presenti prima
                    // di salvarli
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                                + "WHERE u.decTipoUnitaDoc = :tipo and u.tiFiltroMultiplo = :filtro "
                                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
                        query.setParameter("tipo", tipo);
                        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                        query.setParameter("crit", record.getIdCriterioRaggr());
                        if (query.getResultList().isEmpty()) {
                            saveCritRaggrFiltroMultiplo(record, null, null, null, tipo, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(record, null, null, null, tipo, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                            + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                            + "and u.decTipoUnitaDoc.idTipoUnitaDoc NOT IN :tipi");
                    q.setParameter("tipi", asList);
                    q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                    q.setParameter("crit", record.getIdCriterioRaggr());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();
                getEntityManager().flush();
            }
        }

        // Per ogni tipo doc creo un record filtro multiplo
        queryStr = new StringBuilder("SELECT u FROM DecTipoDoc u ");
        if (filtri.getNm_tipo_doc().parse() != null && filtri.getNm_tipo_doc().parse().size() > 0) {
            queryStr.append("WHERE u.idTipoDoc in :idtipodoc");
            query = getEntityManager().createQuery(queryStr.toString());
            List<BigDecimal> asList = filtri.getNm_tipo_doc().parse();
            query.setParameter("idtipodoc", asList);
            List<DecTipoDoc> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroTipoDoc("1");
                for (DecTipoDoc tipo : lista) {
                    // Se sto modificando il criterio, verifico se ho già inserito precedentemente il filtro,
                    // Altrimenti lo salvo direttamente
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                                + "WHERE u.decTipoDoc = :tipo and u.tiFiltroMultiplo = :filtro "
                                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
                        query.setParameter("tipo", tipo);
                        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                        query.setParameter("crit", record.getIdCriterioRaggr());
                        if (query.getResultList().isEmpty()) {
                            saveCritRaggrFiltroMultiplo(record, null, null, tipo, null, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(record, null, null, tipo, null, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                            + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                            + "and u.decTipoDoc.idTipoDoc NOT IN :tipi");
                    q.setParameter("tipi", asList);
                    q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                    q.setParameter("crit", record.getIdCriterioRaggr());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();

                getEntityManager().flush();
            }
        }

        queryStr = new StringBuilder(
                "SELECT DISTINCT v.nmSistemaMigraz FROM OrgUsoSistemaMigraz u JOIN u.aplSistemaMigraz v "
                        + "WHERE u.orgStrut.idStrut = :idStrutturain " + "AND v.nmSistemaMigraz is not null ");
        // Per ogni sistema di migrazione creo un record filtro multiplo
        if (filtri.getNm_sistema_migraz().parse() != null && filtri.getNm_sistema_migraz().parse().size() > 0) {
            queryStr.append("AND v.nmSistemaMigraz in :nmsistemamigraz");
            query = getEntityManager().createQuery(queryStr.toString());
            List<String> asList = filtri.getNm_sistema_migraz().parse();
            query.setParameter("nmsistemamigraz", asList);
            query.setParameter("idStrutturain", idStruttura);
            List<String> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroSistemaMigraz("1");
                for (String tipo : lista) {
                    // Se sto modificando il criterio, verifico se ho già inserito precedentemente il filtro,
                    // Altrimenti lo salvo direttamente
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                                + "WHERE u.nmSistemaMigraz = :tipo " + "and u.tiFiltroMultiplo = :filtro "
                                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
                        query.setParameter("tipo", tipo);
                        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                        query.setParameter("crit", record.getIdCriterioRaggr());
                        if (query.getResultList().isEmpty()) {
                            saveCritRaggrFiltroMultiploSisMigr(record, tipo,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiploSisMigr(record, tipo,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                            + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                            + "and u.nmSistemaMigraz NOT IN :tipi");
                    q.setParameter("tipi", asList);
                    q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                    q.setParameter("crit", record.getIdCriterioRaggr());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
            // Eseguo perciò una bulk delete per eliminare quei record
            if (nome != null) {
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();
            }
        }

        // Per ogni registro creo un record filtro multiplo
        queryStr = new StringBuilder("SELECT u FROM DecRegistroUnitaDoc u ");
        if (filtri.getCd_registro_key_unita_doc().parse() != null
                && filtri.getCd_registro_key_unita_doc().parse().size() > 0) {
            queryStr.append("WHERE u.idRegistroUnitaDoc in :idreg");
            query = getEntityManager().createQuery(queryStr.toString());
            List<BigDecimal> asList = filtri.getCd_registro_key_unita_doc().parse();
            query.setParameter("idreg", asList);
            List<DecRegistroUnitaDoc> lista = query.getResultList();
            if (!lista.isEmpty()) {
                record.setFlFiltroRegistroKey("1");
                for (DecRegistroUnitaDoc reg : lista) {
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                                + "WHERE u.decRegistroUnitaDoc = :reg " + "and u.tiFiltroMultiplo = :filtro "
                                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
                        query.setParameter("reg", reg);
                        query.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                        query.setParameter("crit", record.getIdCriterioRaggr());
                        if (query.getResultList().isEmpty()) {
                            saveCritRaggrFiltroMultiplo(record, reg, null, null, null, null,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(record, reg, null, null, null, null,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                    }
                }
                if (nome != null) {
                    // In caso di modifica, potrei aver eliminato qualche filtro dalle multiselect, che non
                    // risulterebbero più presenti nella lista
                    // Eseguo perciò una bulk delete sui record non presenti nella lista
                    Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                            + "WHERE u.tiFiltroMultiplo = :filtro " + "and u.decCriterioRaggr.idCriterioRaggr = :crit "
                            + "and u.decRegistroUnitaDoc.idRegistroUnitaDoc NOT IN :regs");
                    q.setParameter("regs", asList);
                    q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                    q.setParameter("crit", record.getIdCriterioRaggr());
                    q.executeUpdate();
                    getEntityManager().flush();
                }
            }
        } else {
            if (nome != null) {
                // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
                // Eseguo perciò una bulk delete per eliminare quei record
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();
                getEntityManager().flush();
            }
        }

        // Per ogni tipo esito verifica firme creo un record filtro multiplo
        if (filtri.getTi_esito_verif_firme().parse() != null && filtri.getTi_esito_verif_firme().parse().size() > 0) {
            List<String> asList = filtri.getTi_esito_verif_firme().parse();
            if (!asList.isEmpty()) {
                record.setFlFiltroTiEsitoVerifFirme("1");
                for (Object tipo : asList) {
                    if (nome != null) {
                        query = getEntityManager().createQuery("SELECT u FROM DecCriterioFiltroMultiplo u "
                                + "WHERE u.tiEsitoVerifFirme = :tipo " + "and u.tiFiltroMultiplo = :filtro "
                                + "and u.decCriterioRaggr.idCriterioRaggr = :crit");
                        query.setParameter("tipo", (String) tipo);
                        query.setParameter("filtro",
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                        query.setParameter("crit", record.getIdCriterioRaggr());
                        if (query.getResultList().isEmpty()) {
                            saveCritRaggrFiltroMultiplo(record, null, null, null, null, (String) tipo,
                                    ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                        }
                    } else {
                        saveCritRaggrFiltroMultiplo(record, null, null, null, null, (String) tipo,
                                ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                    }
                }
                // In caso di modifica, l'utente potrebbe aver eliminato qualche filtro dalle multiselect, che non
                // risulterebbero più presenti nella lista
                // Eseguo perciò una bulk delete sui record non presenti nella lista
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit and u.tiEsitoVerifFirme NOT IN :esiti");
                q.setParameter("esiti", asList);
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();
                getEntityManager().flush();
            }
        } else {
            if (nome != null) {
                // Se sono in modifica, potrei avere eliminato tutti i filtri che avevo creato precedentemente
                // Eseguo perciò una bulk delete per eliminare quei record
                Query q = getEntityManager().createQuery("DELETE FROM DecCriterioFiltroMultiplo u "
                        + "WHERE u.tiFiltroMultiplo = :filtro and u.decCriterioRaggr.idCriterioRaggr = :crit");
                q.setParameter("filtro", ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
                q.setParameter("crit", record.getIdCriterioRaggr());
                q.executeUpdate();
                getEntityManager().flush();
            }
        }

        record.setNmCriterioRaggr(filtri.getNm_criterio_raggr().parse());
        record.setDsCriterioRaggr(filtri.getDs_criterio_raggr().parse());
        record.setNiMaxComp(filtri.getNi_max_comp().parse());
        record.setNiMaxElenchiByGg(filtri.getNi_max_elenchi_by_gg().parse());
        record.setTiScadChiusVolume(filtri.getTi_scad_chius_volume().getValue());
        record.setNiTempoScadChius(filtri.getNi_tempo_scad_chius().parse());
        record.setTiTempoScadChius(filtri.getTi_tempo_scad_chius().getValue());
        record.setDtIstituz(filtri.getDt_istituz().parse());
        record.setDtSoppres(filtri.getDt_soppres().parse());
        record.setCdKeyUnitaDoc(filtri.getCd_key_unita_doc().parse());
        record.setCdKeyUnitaDocDa(filtri.getCd_key_unita_doc_da().parse());
        record.setCdKeyUnitaDocA(filtri.getCd_key_unita_doc_a().parse());
        record.setAaKeyUnitaDoc(filtri.getAa_key_unita_doc().parse());
        record.setAaKeyUnitaDocDa(filtri.getAa_key_unita_doc_da().parse());
        record.setAaKeyUnitaDocA(filtri.getAa_key_unita_doc_a().parse());
        record.setFlUnitaDocFirmato(filtri.getFl_unita_doc_firmato().parse());
        Date dataDa = (dateCreazioneValidate != null ? dateCreazioneValidate[0] : null);
        Date dataA = (dateCreazioneValidate != null ? dateCreazioneValidate[1] : null);
        record.setDtCreazioneUnitaDocDa(dataDa);
        record.setDtCreazioneUnitaDocA(dataA);
        record.setFlForzaAccettazione(filtri.getFl_forza_accettazione().parse());
        record.setFlForzaConservazione(filtri.getFl_forza_conservazione().parse());
        record.setTiConservazione(filtri.getTi_conservazione().parse());
        record.setDtRegUnitaDocDa(filtri.getDt_reg_unita_doc_da().parse());
        record.setDtRegUnitaDocA(filtri.getDt_reg_unita_doc_a().parse());
        record.setDlOggettoUnitaDoc(filtri.getDl_oggetto_unita_doc().parse());
        record.setDlDoc(filtri.getDl_doc().parse());
        record.setDsAutoreDoc(filtri.getDs_autore_doc().parse());
        record.setNtCriterioRaggr(filtri.getNt_criterio_raggr().parse());
        record.setFlCriterioRaggrStandard(criterioStandard);
        // Salvo di default come non fiscale, poi i successivi controlli stabiliranno l'esatto valore
        record.setFlCriterioRaggrFisc("0");
        record.setTiGestElencoCriterio(filtri.getTi_gest_elenco_criterio().parse());
        record.setTiValidElenco(TiValidElencoCriterio.valueOf(filtri.getTi_valid_elenco().parse()));
        record.setTiModValidElenco(TiModValidElencoCriterio.valueOf(filtri.getTi_mod_valid_elenco().parse()));

        try {
            getEntityManager().persist(record);
            getEntityManager().flush();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                    new BigDecimal(record.getIdCriterioRaggr()), param.getNomePagina());
        } catch (RuntimeException re) {
            /// logga l'errore e blocca tutto
            // log.fatal("Eccezione nella persistenza del " + re);
            log.error("Eccezione nella persistenza del  " + re);
            throw new EMFError(EMFError.BLOCKING, re);
        }

        // Gestione del flag fiscale
        String flCriterioRaggrFiscMessage = getFlCriterioRaggrFiscMessage(new BigDecimal(record.getIdCriterioRaggr()));
        if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.FISCALE.getDescrizione())) {
            record.setFlCriterioRaggrFisc("1");
        } else if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.NON_FISCALE.getDescrizione())) {
            record.setFlCriterioRaggrFisc("0");
        } else {
            throw new ParerUserError(
                    "Il criterio è errato perchè è standard ed il tipo di unità documentaria usato dal criterio "
                            + "è associata a registri fiscali ed a registri non fiscali");
        }

        return record.getIdCriterioRaggr();
    }

    private void saveCritRaggrFiltroMultiploSisMigr(DecCriterioRaggr crit, String nmSistemaMigraz,
            String tiFiltroMultiplo) {
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(crit);
        filtro.setNmSistemaMigraz(nmSistemaMigraz);
        filtro.setTiFiltroMultiplo(tiFiltroMultiplo);
        crit.getDecCriterioFiltroMultiplos().add(filtro);
    }

    private void saveCritRaggrFiltroMultiplo(DecCriterioRaggr crit, DecRegistroUnitaDoc reg,
            DecRegistroUnitaDoc regRange, DecTipoDoc tipoDoc, DecTipoUnitaDoc tipoUD, String tiEsitoVerifFirme,
            String tiFiltroMultiplo) {
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(crit);
        filtro.setDecRegistroRangeUnitaDoc(regRange);
        filtro.setDecRegistroUnitaDoc(reg);
        filtro.setDecTipoDoc(tipoDoc);
        filtro.setDecTipoUnitaDoc(tipoUD);
        filtro.setTiEsitoVerifFirme(tiEsitoVerifFirme);
        filtro.setTiFiltroMultiplo(tiFiltroMultiplo);
        crit.getDecCriterioFiltroMultiplos().add(filtro);
    }

    public boolean existNomeCriterio(String nome, BigDecimal idStruttura) {
        String queryStr = "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", idStruttura);
        query.setParameter("nomecrit", nome);

        if (query.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggr(CriteriRaggruppamentoForm.FiltriCriteriRaggr filtriCriteri)
            throws EMFError {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u ");
        String whereWord = "WHERE ";
        /* Inserimento nella query del filtro ID_AMBIENTE */
        BigDecimal idAmbiente = filtriCriteri.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        /* Inserimento nella query del filtro ID_ENTE */
        BigDecimal idEnte = filtriCriteri.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        /* Inserimento nella query del filtro ID_STRUT */
        BigDecimal idStrut = filtriCriteri.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        String nmCriterioRaggr = filtriCriteri.getNm_criterio_raggr().parse();
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("UPPER(u.nmCriterioRaggr) LIKE :nmCriterioRaggr ");
            whereWord = "AND ";
        }
        String flCriterioRaggrStandard = filtriCriteri.getFl_criterio_raggr_standard().parse();
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("u.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
            whereWord = "AND ";
        }
        String flCriterioRaggrFisc = filtriCriteri.getFl_criterio_raggr_fisc().parse();
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("u.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
            whereWord = "AND ";
        }
        String tiValidElenco = filtriCriteri.getTi_valid_elenco().parse();
        if (tiValidElenco != null) {
            queryStr.append(whereWord).append("u.tiValidElenco = :tiValidElenco ");
            whereWord = "AND ";
        }
        String tiModValidElenco = filtriCriteri.getTi_mod_valid_elenco().parse();
        if (tiModValidElenco != null) {
            queryStr.append(whereWord).append("u.tiModValidElenco = :tiModValidElenco ");
            whereWord = "AND ";
        }
        String tiGestElencoCriterio = filtriCriteri.getTi_gest_elenco_criterio().parse();
        if (tiGestElencoCriterio != null) {
            queryStr.append(whereWord).append("u.tiGestElencoCriterio = :tiGestElencoCriterio ");
            whereWord = "AND ";
        }
        BigDecimal idRegistroUnitaDoc = filtriCriteri.getId_registro_unita_doc().parse();
        if (idRegistroUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "(u.idRegistroUnitaDoc = :idRegistroUnitaDoc OR u.idRegistroRangeUnitaDoc = :idRegistroUnitaDoc) ");
            whereWord = "AND ";
        }
        BigDecimal idTipoUnitaDoc = filtriCriteri.getId_tipo_unita_doc().parse();
        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord).append("u.idTipoUnitaDoc = :idTipoUnitaDoc ");
            whereWord = "AND ";
        }
        BigDecimal idTipoDoc = filtriCriteri.getId_tipo_doc().parse();
        if (idTipoDoc != null) {
            queryStr.append(whereWord).append("u.idTipoDoc = :idTipoDoc ");
            whereWord = "AND ";
        }
        BigDecimal aaKeyUnitaDoc = filtriCriteri.getAa_key_unita_doc().parse();
        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "(u.aaKeyUnitaDoc = :aaKeyUnitaDoc OR (u.aaKeyUnitaDocDa <= :aaKeyUnitaDoc AND u.aaKeyUnitaDocA >= :aaKeyUnitaDoc)) ");
            whereWord = "AND ";
        }
        String criterioAttivo = filtriCriteri.getCriterio_attivo().parse();
        if (criterioAttivo != null) {
            if (criterioAttivo.equals("1")) {
                queryStr.append(whereWord).append("u.dtSoppres >= :data AND u.dtIstituz <= :data ");
            } else {
                queryStr.append(whereWord).append("u.dtSoppres < :data OR u.dtIstituz > :data ");
            }
            whereWord = "AND ";
        }

        queryStr.append("ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", "%" + nmCriterioRaggr.toUpperCase() + "%");
        }
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        if (tiValidElenco != null) {
            query.setParameter("tiValidElenco", tiValidElenco);
        }
        if (tiModValidElenco != null) {
            query.setParameter("tiModValidElenco", tiModValidElenco);
        }
        if (tiGestElencoCriterio != null) {
            query.setParameter("tiGestElencoCriterio", tiGestElencoCriterio);
        }
        if (idRegistroUnitaDoc != null) {
            query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        }
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        }
        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", idTipoDoc);
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (criterioAttivo != null) {
            Calendar dataOdierna = Calendar.getInstance();
            dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
            dataOdierna.set(Calendar.MINUTE, 0);
            dataOdierna.set(Calendar.SECOND, 0);
            dataOdierna.set(Calendar.MILLISECOND, 0);
            query.setParameter("data", dataOdierna.getTime());
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        DecVRicCriterioRaggrTableBean critRaggrTableBean = new DecVRicCriterioRaggrTableBean();
        DecVRicCriterioRaggrRowBean critRaggrRowBean;

        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                for (DecVRicCriterioRaggr scriteriato : listaCritRaggr) {
                    critRaggrRowBean = (DecVRicCriterioRaggrRowBean) Transform.entity2RowBean(scriteriato);
                    critRaggrRowBean.setString("nm_ente_nm_strut",
                            scriteriato.getNmEnte() + " - " + scriteriato.getNmStrut());
                    String cdRegistro = null;
                    String aaUnitaDoc = null;
                    if (scriteriato.getCdRegistroUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroUnitaDoc();
                    } else if (scriteriato.getCdRegistroRangeUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroRangeUnitaDoc();
                    }
                    critRaggrRowBean.setString("cd_registro", cdRegistro);
                    if (scriteriato.getAaKeyUnitaDoc() != null) {
                        aaUnitaDoc = "" + scriteriato.getAaKeyUnitaDoc();
                    } else if (scriteriato.getAaKeyUnitaDocDa() != null) {
                        aaUnitaDoc = scriteriato.getAaKeyUnitaDocDa() + " - " + scriteriato.getAaKeyUnitaDocA();
                    }
                    critRaggrRowBean.setString("aa_unita_doc", aaUnitaDoc);
                    critRaggrTableBean.add(critRaggrRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRaggrTableBean;
    }

    public List<DecCriterioRaggr> retrieveDecCriterioRaggrList(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggr u ");
        String whereWord = "WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr");
        }
        queryStr.append(" ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCritRaggr;
    }

    public List<DecCriterioRaggr> retrieveDecCriterioRaggrList(BigDecimal idStruttura) {
        return retrieveDecCriterioRaggrList(null, null, idStruttura, null);
    }

    public boolean deleteDecCriterioRaggr(LogParam param, BigDecimal idStrut, String nmCriterioRaggr)
            throws ParerUserError {
        boolean result = false;

        String queryStr = "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut and u.nmCriterioRaggr = :nomecrit";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idstrut", idStrut);
        query.setParameter("nomecrit", nmCriterioRaggr);

        // Ottengo la entity del record da eliminare
        DecCriterioRaggr row = (DecCriterioRaggr) query.getSingleResult();
        if (row != null && row.getVolVolumeConservs().isEmpty() && row.getElvElencoVers().isEmpty()) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                    new BigDecimal(row.getIdCriterioRaggr()), param.getNomePagina());
            // Rimuovo il record
            getEntityManager().remove(row);
            getEntityManager().flush();
            log.info("Cancellazione criterio di raggruppamento " + nmCriterioRaggr + " della struttura " + idStrut
                    + " avvenuta con successo!");
            result = true;
        } else {
            throw new ParerUserError("Errore nell'eliminazione del criterio " + row.getNmCriterioRaggr()
                    + ", il criterio è collegato a dei volumi od a degli elenchi di versamento");
        }
        return result;
    }

    public DecCriterioFiltroMultiploTableBean getCriteriRaggrFiltri(BigDecimal idCriterioRaggr,
            String tiFiltroMultiplo) {
        String whereWord = " and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT f FROM DecCriterioFiltroMultiplo f " + "WHERE f.decCriterioRaggr.idCriterioRaggr = :idcrit ");
        if (tiFiltroMultiplo != null) {
            queryStr.append(whereWord).append("f.tiFiltroMultiplo = :filtro ");
        }
        queryStr.append("ORDER BY f.tiFiltroMultiplo");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idcrit", idCriterioRaggr);
        if (tiFiltroMultiplo != null) {
            query.setParameter("filtro", tiFiltroMultiplo);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioFiltroMultiplo> listaFiltriCritRaggr = query.getResultList();
        DecCriterioFiltroMultiploTableBean listaFiltriTableBean = new DecCriterioFiltroMultiploTableBean();
        try {
            if (listaFiltriCritRaggr != null && !listaFiltriCritRaggr.isEmpty()) {
                listaFiltriTableBean = (DecCriterioFiltroMultiploTableBean) Transform
                        .entities2TableBean(listaFiltriCritRaggr);
                if (tiFiltroMultiplo.equals(ApplEnum.TipoFiltroMultiploCriteriRaggr.RANGE_REGISTRO_UNI_DOC.name())) {
                    for (DecCriterioFiltroMultiplo record : listaFiltriCritRaggr) {
                        for (DecCriterioFiltroMultiploRowBean row : listaFiltriTableBean) {
                            if (record.getIdCriterioFiltroMult() == row.getIdCriterioFiltroMult().longValue()) {
                                row.setIdRegistroRangeUnitaDoc(
                                        new BigDecimal(record.getDecRegistroRangeUnitaDoc().getIdRegistroUnitaDoc()));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaFiltriTableBean;
    }

    public DecCriterioRaggrTableBean getCriteriRaggrbyId(BigDecimal id, BigDecimal idStruttura) {
        String whereWord = " and ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT u FROM DecCriterioRaggr u WHERE u.orgStrut.idStrut = :idstrut");
        if (id != null) {
            queryStr.append(whereWord).append("u.idCriterioRaggr = :id");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idstrut", idStruttura);
        if (id != null) {
            query.setParameter("id", id);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecCriterioRaggr> listaCritRaggr = query.getResultList();
        DecCriterioRaggrTableBean listaCritRaggrTableBean = new DecCriterioRaggrTableBean();
        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                listaCritRaggrTableBean = (DecCriterioRaggrTableBean) Transform.entities2TableBean(listaCritRaggr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // riformatto la data da presentare a video
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < listaCritRaggrTableBean.size(); i++) {
            DecCriterioRaggrRowBean row = listaCritRaggrTableBean.getRow(i);
            if (row.getDtCreazioneUnitaDocDa() != null) {
                Date dataDa = row.getDtCreazioneUnitaDocDa();
                c.setTime(dataDa);
                int oreDa = c.get(Calendar.HOUR_OF_DAY);
                int minutiDa = c.get(Calendar.MINUTE);
                row.setBigDecimal("ore_dt_creazione_unita_doc_da", new BigDecimal(oreDa));
                row.setBigDecimal("minuti_dt_creazione_unita_doc_da", new BigDecimal(minutiDa));
            }
            if (row.getDtCreazioneUnitaDocA() != null) {
                Date dataA = row.getDtCreazioneUnitaDocA();
                c.setTime(dataA);
                int oreA = c.get(Calendar.HOUR_OF_DAY);
                int minutiA = c.get(Calendar.MINUTE);

                row.setBigDecimal("ore_dt_creazione_unita_doc_a", new BigDecimal(oreA));
                row.setBigDecimal("minuti_dt_creazione_unita_doc_a", new BigDecimal(minutiA));
            }
        }

        return listaCritRaggrTableBean;
    }

    public OrgStrutRowBean getOrgStrutById(BigDecimal idStrut) {
        OrgStrutRowBean strutRB = new OrgStrutRowBean();
        try {
            strutRB = (OrgStrutRowBean) Transform
                    .entity2RowBean(getEntityManager().find(OrgStrut.class, idStrut.longValue()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return strutRB;
    }

    public DecCriterioRaggrRowBean getDecCriterioRaggrById(BigDecimal idCriterioRaggr) {
        DecCriterioRaggrRowBean critRB = new DecCriterioRaggrRowBean();
        try {
            DecCriterioRaggr criterioRaggr = getEntityManager().find(DecCriterioRaggr.class,
                    idCriterioRaggr.longValue());
            if (criterioRaggr != null) {
                // Info criterio
                critRB = (DecCriterioRaggrRowBean) Transform.entity2RowBean(criterioRaggr);
                // Info ambiente
                String tiGestElencoNoStd = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_NOSTD",
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
                String tiGestElencoStdFisc = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_FISC",
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
                String tiGestElencoStdNofisc = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_NOFISC",
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                        BigDecimal.valueOf(criterioRaggr.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
                critRB.setString("ti_gest_elenco_std_nofisc", tiGestElencoStdNofisc);
                critRB.setString("ti_gest_elenco_nostd", tiGestElencoNoStd);
                critRB.setString("ti_gest_elenco_std_fisc", tiGestElencoStdFisc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRB;
    }

    public DecCriterioRaggr getDecCriterioRaggr(BigDecimal idStrutCorrente, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggr u ");
        String whereWord = "WHERE ";

        if (idStrutCorrente != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrutCorrente ");
            whereWord = "AND ";
        }

        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
            whereWord = "AND ";
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrutCorrente != null) {
            query.setParameter("idStrutCorrente", idStrutCorrente);
        }

        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        List<DecCriterioRaggr> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Verifico se i tipi documento passati in ricerca sono tutti principali
     *
     * @param idTipiDocumentoList
     *            lista id tipo documento
     * 
     * @return true/false
     */
    public boolean areAllTipiDocPrincipali(List<BigDecimal> idTipiDocumentoList) {
        String queryStr = "SELECT u FROM DecTipoDoc u " + "WHERE u.flTipoDocPrincipale = '1' ";

        if (idTipiDocumentoList != null && !idTipiDocumentoList.isEmpty()) {
            queryStr = queryStr + "AND u.idTipoDoc IN :idTipiDocList ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idTipiDocumentoList != null && !idTipiDocumentoList.isEmpty()) {
            query.setParameter("idTipiDocList", idTipiDocumentoList);
        }
        List<DecTipoDoc> listaTipiDoc = query.getResultList();
        return listaTipiDoc.size() == idTipiDocumentoList.size();
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromAmministrazioneStruttura(BigDecimal idStrut,
            List<BigDecimal> idRegistroUnitaDocList, List<BigDecimal> idTipoUnitaDocList,
            List<BigDecimal> idTipoDocList) throws EMFError {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut ");

        if (!idRegistroUnitaDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.idRegistroUnitaDoc IN :idRegistroUnitaDocList ");
        }
        if (!idTipoUnitaDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.idTipoUnitaDoc IN :idTipoUnitaDocList ");
        }
        if (!idTipoDocList.isEmpty()) {
            queryStr = queryStr.append("AND u.idTipoDoc IN :idTipoDocList ");
        }

        queryStr.append("ORDER BY u.nmCriterioRaggr ASC ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);

        if (!idRegistroUnitaDocList.isEmpty()) {
            query.setParameter("idRegistroUnitaDocList", idRegistroUnitaDocList);
        }
        if (!idTipoUnitaDocList.isEmpty()) {
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
        }
        if (!idTipoDocList.isEmpty()) {
            query.setParameter("idTipoDocList", idTipoDocList);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCriteriToTableBean(listaCritRaggr);
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromStruttura(BigDecimal idStrut, boolean filterValid)
            throws EMFError {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut ";
        if (filterValid) {
            queryStr += " AND u.dtIstituz <= :filterDate AND u.dtSoppres >= :filterDate ";
        }
        queryStr += "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCriteriToTableBean(listaCritRaggr);
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromRegistroNoRange(BigDecimal idStrut,
            BigDecimal idRegistroUnitaDoc) throws EMFError {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCriteriToTableBean(listaCritRaggr);
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromTipoUnitaDoc(BigDecimal idStrut, BigDecimal idTipoUnitaDoc)
            throws EMFError {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.idTipoUnitaDoc = :idTipoUnitaDoc " + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCriteriToTableBean(listaCritRaggr);
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrFromTipoDoc(BigDecimal idStrut, BigDecimal idTipoDoc)
            throws EMFError {
        String queryStr = "SELECT DISTINCT new it.eng.parer.viewEntity.DecVRicCriterioRaggr "
                + "(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.idStrut, u.nmStrut, u.idCriterioRaggr, u.nmCriterioRaggr, u.nmTipoUnitaDoc, "
                + "u.cdRegistroUnitaDoc, u.cdRegistroRangeUnitaDoc, u.nmTipoDoc, u.flCriterioRaggrStandard, u.flCriterioRaggrFisc, u.tiValidElenco, u.tiModValidElenco, "
                + "u.tiGestElencoCriterio, u.aaKeyUnitaDoc, u.aaKeyUnitaDocDa, u.aaKeyUnitaDocA, u.niMaxComp, u.dsScadChius, u.dtIstituz, u.dtSoppres) FROM DecVRicCriterioRaggr u "
                + "WHERE u.idStrut = :idStrut AND u.idTipoDoc = :idTipoDoc " + "ORDER BY u.nmCriterioRaggr ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoDoc", idTipoDoc);
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<DecVRicCriterioRaggr> listaCritRaggr = query.getResultList();
        return listaCriteriToTableBean(listaCritRaggr);
    }

    private DecVRicCriterioRaggrTableBean listaCriteriToTableBean(List<DecVRicCriterioRaggr> listaCritRaggr) {

        DecVRicCriterioRaggrTableBean critRaggrTableBean = new DecVRicCriterioRaggrTableBean();
        DecVRicCriterioRaggrRowBean critRaggrRowBean;

        try {
            if (listaCritRaggr != null && !listaCritRaggr.isEmpty()) {
                for (DecVRicCriterioRaggr scriteriato : listaCritRaggr) {
                    critRaggrRowBean = (DecVRicCriterioRaggrRowBean) Transform.entity2RowBean(scriteriato);
                    critRaggrRowBean.setString("nm_ente_nm_strut",
                            scriteriato.getNmEnte() + " - " + scriteriato.getNmStrut());
                    String cdRegistro = null;
                    String aaUnitaDoc = null;
                    if (scriteriato.getCdRegistroUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroUnitaDoc();
                    } else if (scriteriato.getCdRegistroRangeUnitaDoc() != null) {
                        cdRegistro = scriteriato.getCdRegistroRangeUnitaDoc();
                    }
                    critRaggrRowBean.setString("cd_registro", cdRegistro);
                    if (scriteriato.getAaKeyUnitaDoc() != null) {
                        aaUnitaDoc = "" + scriteriato.getAaKeyUnitaDoc();
                    } else if (scriteriato.getAaKeyUnitaDocDa() != null) {
                        aaUnitaDoc = scriteriato.getAaKeyUnitaDocDa() + " - " + scriteriato.getAaKeyUnitaDocA();
                    }

                    critRaggrRowBean.setString("aa_unita_doc", aaUnitaDoc);
                    critRaggrTableBean.add(critRaggrRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return critRaggrTableBean;

    }

    public boolean existElvElencoVersPerCriterioRaggr(BigDecimal idCriterioRaggr) {
        String queryStr = "SELECT elencoVers FROM ElvElencoVer elencoVers "
                + "WHERE EXISTS (SELECT criterioRaggr FROM DecCriterioRaggr criterioRaggr WHERE criterioRaggr.idCriterioRaggr = :idCriterioRaggr AND elencoVers.decCriterioRaggr = criterioRaggr)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggr", idCriterioRaggr.longValue());
        return !query.getResultList().isEmpty();
    }

    public void bulkDeleteCriteriRaggr(List<Long> idCriterioRaggrList) {
        if (!idCriterioRaggrList.isEmpty()) {
            String queryStr = "DELETE FROM DecCriterioRaggr criterioRaggr "
                    + "WHERE criterioRaggr.idCriterioRaggr IN :idCriterioRaggrList";
            Query q = getEntityManager().createQuery(queryStr);
            q.setParameter("idCriterioRaggrList", idCriterioRaggrList);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    // /**
    // * Restituisce il valore di numMaxCompCriterioRaggr per la struttura passata in input. Se la struttura non è
    // * presente o viene passato come parametro un id nullo, viene restituito 0
    // *
    // * @param idStrut
    // * @return
    // */
    // public long getNumMaxCompDaStruttura(BigDecimal idStrut) {
    // long numComp = 0;
    // if (idStrut != null) {
    // String queryStr = "SELECT strut.numMaxCompCriterioRaggr FROM OrgStrut strut WHERE strut.idStrut = :idStrut ";
    // Query q = getEntityManager().createQuery(queryStr);
    // q.setParameter("idStrut", idStrut.longValue());
    // List<BigDecimal> numMaxList = (List<BigDecimal>) q.getResultList();
    // if (!numMaxList.isEmpty()) {
    // numComp = numMaxList.get(0).longValue();
    // }
    // }
    // return numComp;
    // }

    /**
     * Recupera i criteri di raggruppamento che hanno come campo: - l'anno passato come parametro e - il registro
     * passato come parametro oppure un tipo ud legato al registro passato come parametro
     *
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * 
     * @return lista oggetti di tipo {@link DecCriterioRaggr}
     */
    public List<DecCriterioRaggr> getDecCriterioRaggrRegistroOTipiUdAssociatiList(BigDecimal idRegistroUnitaDoc,
            int aaKeyUnitaDoc) {
        Query query = getEntityManager()
                .createQuery("SELECT criterioRaggr FROM DecCriterioFiltroMultiplo criterioFiltroMultiplo "
                        + "JOIN criterioFiltroMultiplo.decCriterioRaggr criterioRaggr "
                        + "JOIN criterioFiltroMultiplo.decRegistroUnitaDoc registro "
                        // Prendo o il registro passato come parametro o un tipo ud legato al registro passato come
                        // parametro
                        + "WHERE (registro.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                        + "OR EXISTS (SELECT tipoUnitaDoc FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso "
                        + "             JOIN tipoUnitaDocAmmesso.decTipoUnitaDoc tipoUnitaDoc "
                        + "             WHERE tipoUnitaDocAmmesso.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc)) "
                        //
                        + "AND criterioRaggr.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                        + "AND criterioRaggr.flCriterioRaggrStandard = '1' ");
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        return query.getResultList();
    }

    /**
     * Restituisce una Stringa con i seguenti possibili valori - 1 = ESISTE UN SOLO Criterio Stardard ATTIVO - 0 =
     * ESISTONO ASSOCIAZIONI ad almeno un criterio non standard - 2 = non è associata a criteri di raggruppamento (né
     * standard né non standard) o se tutti i criteri cui è associata sono disattivi alla data
     *
     * NB. Il valore 2 è utilizzato al posto di NULL perché il framework lo gestisce (per altri casi, correttamente)
     * come se fosse il valore FALSE
     *
     * @param idTipoDato
     *            id del tipo dato
     * @param tipoDato
     *            registro, tipo ud oppure tipo doc
     * 
     * @return String criterio
     */
    public String getCriterioStandardPerTipoDatoAnno(long idTipoDato, TipoDato tipoDato) {
        String queryStr = "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio " + "WHERE criterio.flCriterioRaggrStandard = :flagStandard "
                + "AND criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ";
        Date now = Calendar.getInstance().getTime();

        switch (tipoDato) {
        case REGISTRO:
            queryStr = queryStr
                    + "AND (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ";
            break;
        case TIPO_UNITA_DOC:
            queryStr = queryStr + "AND multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ";
            break;
        case TIPO_DOC:
            queryStr = queryStr + "AND multiplo.decTipoDoc.idTipoDoc = :idTipoDato ";
            break;
        }

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoDato", idTipoDato);
        query.setParameter("flagStandard", "1");
        query.setParameter("data", now);

        List<DecCriterioRaggr> listStandard = query.getResultList();
        query.setParameter("idTipoDato", idTipoDato);
        query.setParameter("flagStandard", "0");
        query.setParameter("data", now);
        List<DecCriterioRaggr> listNotStandard = query.getResultList();

        String risultato = "2";
        if (!listStandard.isEmpty() && listStandard.size() == 1 && listNotStandard.isEmpty()) {
            risultato = "1";
        } else {
            if (!listStandard.isEmpty() && !listNotStandard.isEmpty()) {
                risultato = "0";
            }
        }
        return risultato;
    }

    public boolean existsCriterioPerTipoDato(Long idTipoDato, TipoDato tipoDato, String flCriterioRaggrStandard,
            String flCriterioRaggrFisc) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(criterio) FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio "
                + "WHERE criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ");
        String whereWord = " AND ";
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
            whereWord = " AND ";
        }
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
            whereWord = " AND ";
        }

        switch (tipoDato) {
        case REGISTRO:
            queryStr.append(whereWord).append(
                    " (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        query.setParameter("idTipoDato", idTipoDato);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("data", now);
        return (Long) query.getSingleResult() > 0;
    }

    public List<DecCriterioRaggr> getCriteriPerTipoDato(Long idTipoDato, TipoDato tipoDato,
            String flCriterioRaggrStandard, String flCriterioRaggrFisc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo " + "JOIN multiplo.decCriterioRaggr criterio "
                        + "WHERE criterio.dtSoppres >= :data AND criterio.dtIstituz <= :data ");
        String whereWord = " AND ";
        if (flCriterioRaggrStandard != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrStandard = :flCriterioRaggrStandard ");
            whereWord = " AND ";
        }
        if (flCriterioRaggrFisc != null) {
            queryStr.append(whereWord).append("criterio.flCriterioRaggrFisc = :flCriterioRaggrFisc ");
            whereWord = " AND ";
        }

        switch (tipoDato) {
        case REGISTRO:
            queryStr.append(whereWord).append(
                    " (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idTipoDato) ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr.append(whereWord).append(" multiplo.decTipoDoc.idTipoDoc = :idTipoDato ");
            break;
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (flCriterioRaggrStandard != null) {
            query.setParameter("flCriterioRaggrStandard", flCriterioRaggrStandard);
        }
        if (flCriterioRaggrFisc != null) {
            query.setParameter("flCriterioRaggrFisc", flCriterioRaggrFisc);
        }
        query.setParameter("idTipoDato", idTipoDato);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("data", now);
        List<DecCriterioRaggr> criteri = (List<DecCriterioRaggr>) query.getResultList();
        return criteri;
    }

    public List<DecCriterioRaggr> getCriteriPerAssociazioneRegistroTipoUd(Long idRegistroUnitaDoc,
            Long idTipoUnitaDoc) {
        String queryStr = "SELECT criterio FROM DecCriterioFiltroMultiplo multiplo "
                + "JOIN multiplo.decCriterioRaggr criterio "
                + "WHERE (multiplo.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc OR multiplo.decRegistroRangeUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc) "
                + "AND multiplo.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        List<DecCriterioRaggr> criteri = (List<DecCriterioRaggr>) query.getResultList();
        return criteri;
    }

    public DecVCreaCritRaggrRegistro getDecVCreaCritRaggrRegistro(Long idRegistroUnitaDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrRegistro u "
                + "WHERE u.idRegistroUnitaDoc = :idRegistroUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        List<DecVCreaCritRaggrRegistro> list = query.getResultList();
        return list.get(0);
    }

    public DecVCreaCritRaggrTipoUd getDecVCreaCritRaggrTipoUd(Long idTipoUnitaDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrTipoUd u " + "WHERE u.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        List<DecVCreaCritRaggrTipoUd> list = query.getResultList();
        return list.get(0);
    }

    public DecVCreaCritRaggrTipoDoc getDecVCreaCritRaggrTipoDoc(Long idTipoDoc) {
        String queryStr = "SELECT u FROM DecVCreaCritRaggrTipoDoc u " + "WHERE u.idTipoDoc = :idTipoDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoDoc", idTipoDoc);
        List<DecVCreaCritRaggrTipoDoc> list = query.getResultList();
        return list.get(0);
    }

    /**
     * Verifico se i registri passati in input sono tutti fiscali
     *
     * @param idRegistroKeyUnitaDocList
     *            id registro chiave unita doc
     * 
     * @return true/false
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public boolean areAllRegistriFiscali(List<BigDecimal> idRegistroKeyUnitaDocList) throws ParerUserError {
        if (!idRegistroKeyUnitaDocList.isEmpty()) {
            String queryStr = "SELECT COUNT(registroUnitaDoc) FROM DecRegistroUnitaDoc registroUnitaDoc "
                    + "WHERE registroUnitaDoc.flRegistroFisc = '1' "
                    + "AND registroUnitaDoc.idRegistroUnitaDoc IN :idRegistroKeyUnitaDocList ";

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idRegistroKeyUnitaDocList", idRegistroKeyUnitaDocList);
            Long numRegistriFiscali = (Long) query.getSingleResult();
            return numRegistriFiscali == idRegistroKeyUnitaDocList.size();
        } else {
            throw new ParerUserError("Errore: lista registri fiscali da controllare vuota!");
        }
    }

    /**
     * Verifico se i tipi ud passati in input hanno associati solo registri fiscali
     *
     * @param idTipoUnitaDocList
     *            lista id tipo unita doc
     * 
     * @return true/false
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public boolean areAllRegistriAssociatiFiscali(List<BigDecimal> idTipoUnitaDocList) throws ParerUserError {
        if (!idTipoUnitaDocList.isEmpty()) {
            String queryStr = "SELECT DISTINCT registroUnitaDoc FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso "
                    + "JOIN tipoUnitaDocAmmesso.decTipoUnitaDoc tipoUnitaDoc "
                    + "JOIN tipoUnitaDocAmmesso.decRegistroUnitaDoc registroUnitaDoc "
                    + "WHERE tipoUnitaDoc.idTipoUnitaDoc IN :idTipoUnitaDocList ";
            // Una volta recuperati tutti i registri di tutti i tipi ud
            // (metto in DISTINCT per evitare di avere registri ripetuti associati a tipi ud diverse)
            // li scorro per verificare se sono tutti fiscali. Se la lista da controllare è vuota, è come se fosse false
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idTipoUnitaDocList", idTipoUnitaDocList);
            List<DecRegistroUnitaDoc> registroUnitaDocList = (List<DecRegistroUnitaDoc>) query.getResultList();
            int countRegAssFisc = 0;
            for (DecRegistroUnitaDoc registroUnitaDoc : registroUnitaDocList) {
                if (registroUnitaDoc.getFlRegistroFisc().equals("1")) {
                    countRegAssFisc++;
                }
            }
            if (!registroUnitaDocList.isEmpty()) {
                return countRegAssFisc == registroUnitaDocList.size();
            } else {
                return false;
            }
        } else {
            throw new ParerUserError("Errore: lista tipi ud da controllare vuota!");
        }
    }

    public String getFlCriterioRaggrFiscMessage(BigDecimal idCriterioRaggr) {
        String queryStr = "SELECT calcCriterioFisc.dsMessaggio FROM DecVCalcCriterioFisc calcCriterioFisc "
                + "WHERE calcCriterioFisc.idCriterioRaggr = :idCriterioRaggr ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idCriterioRaggr", idCriterioRaggr);
        List<String> list = query.getResultList();
        return list.get(0);
    }

    public List<String> getCriteriNonCoerenti(BigDecimal idTipoUnitaDoc) {
        String queryStr = "SELECT chkCriteriByTipoUd.dsCriterioNonCoerente FROM DecVChkCriteriByTipoUd chkCriteriByTipoUd "
                + "WHERE chkCriteriByTipoUd.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        List<String> list = (List<String>) query.getResultList();
        return list;
    }
}
