package it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb;

import it.eng.parer.job.allineamentoEntiConvenzionati.ejb.AllineamentoEntiConvenzionatiEjb;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.grantedEntity.OrgAmbitoTerrit;
import it.eng.parer.entity.OrgCategEnte;
import it.eng.parer.entity.OrgCategStrut;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbitoTerritRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbitoTerritTableBean;
import it.eng.parer.slite.gen.tablebean.OrgCategEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgCategEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgCategStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicAmbienteTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicEnteRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicEnteTableBean;
import it.eng.parer.viewEntity.OrgVRicAmbiente;
import it.eng.parer.viewEntity.OrgVRicEnte;
import it.eng.parer.viewEntity.UsrVAbilAmbSacerXstrut;
import it.eng.parer.viewEntity.UsrVAbilEnteSacerXstrut;
import it.eng.parer.viewEntity.UsrVChkCreaAmbSacer;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.entity.IamEnteConvenzDaAllinea;
import it.eng.parer.entity.OrgStoricoEnteAmbiente;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.grantedEntity.SIOrgAccordoEnte;
import it.eng.parer.grantedEntity.SIOrgAmbienteEnteConvenz;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.SIOrgEnteConvenzOrg;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.grantedViewEntity.OrgVRicEnteConvenzByEsterno;
import it.eng.parer.grantedViewEntity.OrgVTreeAmbitoTerrit;
import it.eng.parer.grantedViewEntity.UsrVAbilAmbEnteConvenz;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.SIOrgEnteConvenzOrgRowBean;
import it.eng.parer.slite.gen.tablebean.SIOrgEnteConvenzOrgTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStoricoEnteAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStoricoEnteAmbienteTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicAmbienteRowBean;
import it.eng.parer.viewEntity.UsrVAbilAmbXente;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class AmbienteEjb {

    private static final Logger log = LoggerFactory.getLogger(AmbienteEjb.class);
    @EJB
    private AmbientiHelper ambienteHelper;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private ParamApplicHelper paramApplicHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private AllineamentoEntiConvenzionatiEjb aecEjb;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private UserHelper userHelper;

    @Resource
    private SessionContext context;

    /**
     *
     * Metodo per la costruzione di un table bean dalla tabella Org Ente
     *
     * @param enteRowBean
     *            row bean dati ente
     * @param idUtente
     *            id utente
     * 
     * @return OrgVRicEnteTableBean
     */
    public OrgVRicEnteTableBean getOrgEnteTableBean(OrgEnteRowBean enteRowBean, long idUtente) {
        OrgVRicEnteTableBean entiTableBean = new OrgVRicEnteTableBean();
        List<OrgVRicEnte> listaEnti = ambienteHelper.getEntiAbilitatiRicerca(idUtente, enteRowBean.getIdAmbiente(),
                enteRowBean.getNmEnte(), enteRowBean.getTipoDefTemplateEnte());

        try {
            if (!listaEnti.isEmpty()) {

                for (OrgVRicEnte orgEnte : listaEnti) {
                    OrgVRicEnteRowBean enteRow = (OrgVRicEnteRowBean) Transform.entity2RowBean(orgEnte);
                    OrgCategEnte categEnte = ambienteHelper.findById(OrgCategEnte.class, orgEnte.getIdCategEnte());
                    enteRow.setString("categoriaente", categEnte.getCdCategEnte());
                    entiTableBean.add().copyFromBaseRow(enteRow);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Errore inatteso nel recupero della lista di enti");
        }
        return entiTableBean;
    }

    public boolean existsEntiValidiAmbienteTableBean(BigDecimal idAmbiente) {
        List<OrgEnte> list = ambienteHelper.getEntiValidiAmbiente(idAmbiente);
        return !list.isEmpty();
    }

    public boolean existsUtentiAttiviAbilitatiAdAmbienteTableBean(BigDecimal idAmbiente) {
        List<UsrUser> list = ambienteHelper.getUtentiAttiviAbilitatiAdAmbiente(idAmbiente);
        return !list.isEmpty();
    }

    public boolean existsUtentiAttiviAbilitatiAdAmbienteTableBean2(BigDecimal idAmbiente) {
        List<UsrUser> list = ambienteHelper.getUtentiAttiviAbilitatiAdAmbiente2(idAmbiente);
        return !list.isEmpty();
    }

    public boolean existsUtentiAttiviAbilitatiAdEnteTableBean(BigDecimal idEnte) {
        List<UsrUser> list = ambienteHelper.getUtentiAttiviAbilitatiAdEnte(idEnte);
        return !list.isEmpty();
    }

    /**
     * Metodo che ritorna un rowBean ricercando nella tabella Org Ambiente in base o all'id o al nome Ritorna null se la
     * ricerca non ha successo
     *
     * @param idAmbiente
     *            id ambiente
     * @param nmAmbiente
     *            nome ambiente
     * 
     * @return OrgAmbienteRowBean
     */
    private OrgAmbienteRowBean getOrgAmbiente(BigDecimal idAmbiente, String nmAmbiente) {
        OrgAmbienteRowBean ambienteRowBean = null;
        OrgAmbiente ambiente = new OrgAmbiente();

        if (idAmbiente == BigDecimal.ZERO && nmAmbiente != null) {
            ambiente = ambienteHelper.getOrgAmbienteByName(nmAmbiente);
        } else if (idAmbiente != BigDecimal.ZERO && nmAmbiente == null) {
            ambiente = ambienteHelper.findById(OrgAmbiente.class, idAmbiente);
        }
        if (ambiente != null) {
            try {
                // trasformo la lista di entity (risultante della query) in un tablebean
                ambienteRowBean = (OrgAmbienteRowBean) Transform.entity2RowBean(ambiente);
                if (ambiente.getIdEnteGestore() != null) {
                    SIOrgEnteSiam enteGestore = ambienteHelper.findById(SIOrgEnteSiam.class,
                            ambiente.getIdEnteGestore());
                    ambienteRowBean.setBigDecimal("id_ente_gestore", BigDecimal.valueOf(enteGestore.getIdEnteSiam()));
                    ambienteRowBean.setString("nm_ente_gestore", enteGestore.getNmEnteSiam());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new IllegalStateException("Errore inatteso nel recupero dell'ente");
            }
        }

        return ambienteRowBean;

    }

    /**
     * Metodo che richiama la funzione di ricerca associata al parametro idAmbiente inserito
     *
     * @param idAmbiente
     *            id ambiente
     * 
     * @return ambienteRowBean il risultato della ricerca
     */
    public OrgAmbienteRowBean getOrgAmbienteRowBean(BigDecimal idAmbiente) {
        OrgAmbienteRowBean ambienteRowBean = getOrgAmbiente(idAmbiente, null);
        return ambienteRowBean;
    }

    /**
     * Metodo che richiama la funzione di ricerca associata al parametro nmAmbiente inserito
     *
     * @param nmAmbiente
     *            nome ambiente
     * 
     * @return ambienteRowBean il risultato della ricerca
     */
    public OrgAmbienteRowBean getOrgAmbienteRowBean(String nmAmbiente) {
        OrgAmbienteRowBean ambienteRowBean = getOrgAmbiente(BigDecimal.ZERO, nmAmbiente);
        return ambienteRowBean;
    }

    /**
     * Metodo che ritorna un rowBean ricercando nella tabella Org Ente in base o all'id o al nome Ritorna null se la
     * ricerca non ha buon fine
     *
     * @param idEnte
     *            id ente
     * @param nmEnte
     *            nome ente
     * 
     * @return enteRowBean il risultato della ricerca
     */
    private OrgEnteRowBean getOrgEnte(BigDecimal idEnte, String nmEnte, BigDecimal idAmbiente) {
        OrgEnteRowBean enteRowBean = null;
        OrgEnte orgEnte = null;
        if (idAmbiente != null && StringUtils.isNotBlank(nmEnte)) {
            orgEnte = ambienteHelper.getOrgEnteByName(nmEnte, idAmbiente);
        } else if (idEnte != null && idEnte != BigDecimal.ZERO) {
            orgEnte = ambienteHelper.findById(OrgEnte.class, idEnte);
        }
        if (orgEnte != null) {
            try {
                // trasformo la lista di entity (risultante della query) in un tablebean
                enteRowBean = (OrgEnteRowBean) Transform.entity2RowBean(orgEnte);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new IllegalStateException("Errore inatteso nel recupero dell'ente");
            }
        }
        return enteRowBean;

    }

    /**
     * Metodo che richiama la funzione di ricerca associata al parametro nmEnte inserito
     *
     * @param nmEnte
     *            nome ente
     * @param idAmbiente
     *            id ambiente
     * 
     * @return enteRowBean il risultato della ricerca
     */
    public OrgEnteRowBean getOrgEnteRowBean(String nmEnte, BigDecimal idAmbiente) {
        OrgEnteRowBean enteRowBean = getOrgEnte(BigDecimal.ZERO, nmEnte, idAmbiente);
        return enteRowBean;

    }

    /**
     * Metodo che richiama la funzione di ricerca associata al parametro idEnte inserito
     *
     * @param idEnte
     *            id ente
     * 
     * @return enteRowBean il risultato della ricerca
     */
    public OrgEnteRowBean getOrgEnteRowBean(BigDecimal idEnte) {
        OrgEnteRowBean enteRowBean = getOrgEnte(idEnte, null, null);
        return enteRowBean;

    }

    public void updateOrgAmbiente(BigDecimal idAmb, OrgAmbienteRowBean ambienteRowBean) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.saveAmbiente(idAmb, ambienteRowBean, true);
        struttureEjb.replicateToIam(replic);
    }

    public BigDecimal insertOrgAmbiente(OrgAmbienteRowBean ambienteRowBean) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.saveAmbiente(null, ambienteRowBean, false);
        struttureEjb.replicateToIam(replic);
        return replic.getIdOrganizApplic();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveAmbiente(BigDecimal idAmb, OrgAmbienteRowBean ambienteRowBean, boolean update)
            throws ParerUserError {
        OrgAmbiente ambiente;
        ApplEnum.TiOperReplic tiOper;
        if (update) {
            ambiente = ambienteHelper.findById(OrgAmbiente.class, idAmb);

            ambiente.setNmAmbiente(ambienteRowBean.getNmAmbiente());
            ambiente.setDsAmbiente(ambienteRowBean.getDsAmbiente());
            ambiente.setDsNote(ambienteRowBean.getDsNote());
            ambiente.setDtIniVal(ambienteRowBean.getDtIniVal());
            ambiente.setDtFinVal(ambienteRowBean.getDtFinVal());
            ambiente.setIdEnteConserv(ambienteRowBean.getIdEnteConserv());
            ambiente.setIdEnteGestore(ambienteRowBean.getIdEnteGestore());

            tiOper = ApplEnum.TiOperReplic.MOD;
        } else {
            if (ambienteHelper.getOrgAmbienteByName(ambienteRowBean.getNmAmbiente()) != null) {
                throw new ParerUserError("Nome ambiente gi\u00E0 presente nel database");
            }
            ambiente = (OrgAmbiente) Transform.rowBean2Entity(ambienteRowBean);
            ambienteHelper.insertEntity(ambiente, true);

            tiOper = ApplEnum.TiOperReplic.INS;
        }
        ambiente = ambienteHelper.getOrgAmbienteByName(ambienteRowBean.getNmAmbiente());
        ambienteRowBean.entityToRowBean(ambiente);
        /*
         * N.B.: La replica va fatta solo se la modifica ha modificato il nome o la descrizione dell'ambiente e in
         * questo caso non servono controlli perchè la modifica può SOLO modificare o nome o descrizione...
         */
        IamOrganizDaReplic replic = ambienteHelper.insertEntityIamOrganizDaReplic(ambiente, tiOper);
        return replic;
    }

    private void manageParametriPerAmbiente(AplParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, OrgAmbiente ambiente) {
        for (AplParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                AplValoreParamApplic parametro = ambienteHelper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                ambienteHelper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")
                    // MEV26587
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(Constants.OBFUSCATED_STRING)) {
                AplValoreParamApplic parametro = ambienteHelper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                amministrazioneEjb.insertAplValoreParamApplic(ambiente, null, null, null,
                        paramApplicRowBean.getBigDecimal("id_param_applic"), "AMBIENTE",
                        paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    private void manageParametriMultipliPerAmbiente(AplParamApplicTableBean paramApplicTableBean,
            OrgAmbiente ambiente) {
        for (AplParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Recupero i token dei valori relativi allo specifico parametro nell'online
            String[] tokens = paramApplicRowBean.getString("ds_valore_param_applic_multi").split("\\|");
            // Recupero i token dei valori relativi allo specifico parametro presenti su DB
            List<AplValParamApplicMulti> listaParametriMultipliDB = amministrazioneHelper.getAplValParamApplicMultiList(
                    paramApplicRowBean.getIdParamApplic(), BigDecimal.valueOf(ambiente.getIdAmbiente()));

            // Scorro i token presenti su DB e cancello quelli non più presenti
            for (AplValParamApplicMulti parametroMultiploDB : listaParametriMultipliDB) {
                boolean trovato = false;
                for (String token : tokens) {
                    if (parametroMultiploDB.getDsValoreParamApplic().equals(token)) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    // CANCELLA IL PARAMETRO DA DB
                    amministrazioneHelper.removeEntity(parametroMultiploDB, true);
                }
            }

            // Scorro i token presenti presenti nell'online e se non presenti li inserisco su DB
            for (String token : tokens) {
                if (token != null && !token.equals("")) {
                    AplValParamApplicMulti valParamApplicMulti = amministrazioneHelper.getAplValParamApplicMulti(
                            paramApplicRowBean.getIdParamApplic(), BigDecimal.valueOf(ambiente.getIdAmbiente()), token);
                    if (valParamApplicMulti == null) {
                        // INSERISCI IL PARAMETRO SU DB
                        amministrazioneEjb.insertAplValParamApplicMulti(
                                paramApplicRowBean.getBigDecimal("id_param_applic"), ambiente, token);
                    }
                }
            }
        }
    }

    public void updateOrgEnte(BigDecimal idEnte, OrgEnteRowBean enteRowBean) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.saveEnte(idEnte, enteRowBean, true);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    public void insertOrgEnte(OrgEnteRowBean enteRowBean) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.saveEnte(null, enteRowBean, false);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveEnte(BigDecimal idEnte, OrgEnteRowBean enteRowBean, boolean update)
            throws ParerUserError {
        OrgEnte orgEnte;
        OrgAmbiente orgAmbiente;
        ApplEnum.TiOperReplic tiOper;
        boolean modificatiNomeDescrizione = false;
        boolean modificatoAmbiente = false;

        if (update) {
            OrgEnte dbOrgEnte = ambienteHelper.getOrgEnteByName(enteRowBean.getNmEnte(), null);
            if (dbOrgEnte != null && dbOrgEnte.getIdEnte() != idEnte.longValue()) {
                throw new ParerUserError("Nome ente gi\u00E0 presente all'interno dell'intero sistema</br>");
            }

            if (enteRowBean.getTipoDefTemplateEnte().equals("TEMPLATE_DEF_ENTE")
                    || enteRowBean.getTipoDefTemplateEnte().equals("NO_TEMPLATE")) {
                if (enteRowBean.getCdEnteNormaliz() == null
                        || ambienteHelper.existsCdEnteNormaliz(enteRowBean.getCdEnteNormaliz(), idEnte)) {
                    throw new ParerUserError(
                            "Il nome normalizzato dell'ente non è stato indicato o non è univoco</br>");
                }
            }

            orgEnte = ambienteHelper.findById(OrgEnte.class, idEnte);
            long idAmbienteDB = orgEnte.getOrgAmbiente().getIdAmbiente();
            orgAmbiente = ambienteHelper.findById(OrgAmbiente.class, enteRowBean.getIdAmbiente());
            OrgCategEnte orgCategEnte = ambienteHelper.findById(OrgCategEnte.class, enteRowBean.getIdCategEnte());

            // Se ho modificato l'ambiente di appartenenza dell'ente potrei inserire lo storico
            if (idAmbienteDB != orgAmbiente.getIdAmbiente()) {
                modificatoAmbiente = true;
                // Eseguo la storicizzazione solo se ho modificato le date
                if (dbOrgEnte.getDtIniValAppartAmbiente().compareTo(enteRowBean.getDtIniValAppartAmbiente()) != 0
                        || dbOrgEnte.getDtFinValAppartAmbiente()
                                .compareTo(enteRowBean.getDtFinValAppartAmbiente()) != 0) {
                    OrgStoricoEnteAmbiente storicoEnteAmbiente = new OrgStoricoEnteAmbiente();
                    storicoEnteAmbiente.setOrgAmbiente(orgEnte.getOrgAmbiente());
                    storicoEnteAmbiente.setDtIniVal(orgEnte.getDtIniValAppartAmbiente());
                    Calendar dataFine = Calendar.getInstance();
                    dataFine.setTime(enteRowBean.getDtIniValAppartAmbiente());
                    dataFine.add(Calendar.DATE, -1);
                    if (orgEnte.getDtFinValAppartAmbiente().compareTo(enteRowBean.getDtIniValAppartAmbiente()) <= 0) {
                        storicoEnteAmbiente.setDtFinVal(orgEnte.getDtFinValAppartAmbiente());
                    } else {
                        storicoEnteAmbiente.setDtFinVal(dataFine.getTime());
                    }
                    storicoEnteAmbiente.setOrgEnte(orgEnte);
                    ambienteHelper.insertEntity(storicoEnteAmbiente, true);
                }
            }

            /* Controllo se sono stati modificati nome e/o descrizione */
            if (!orgEnte.getNmEnte().equals(enteRowBean.getNmEnte())
                    || !orgEnte.getDsEnte().equals(enteRowBean.getDsEnte())) {
                modificatiNomeDescrizione = true;
            }

            orgEnte.setNmEnte(enteRowBean.getNmEnte());
            orgEnte.setCdEnteNormaliz(enteRowBean.getCdEnteNormaliz());
            orgEnte.setDsEnte(enteRowBean.getDsEnte());
            orgEnte.setOrgCategEnte(orgCategEnte);
            orgEnte.setOrgAmbiente(orgAmbiente);
            orgEnte.setDtIniValAppartAmbiente(enteRowBean.getDtIniValAppartAmbiente());
            orgEnte.setDtFinValAppartAmbiente(enteRowBean.getDtFinValAppartAmbiente());
            orgEnte.setDtIniVal(enteRowBean.getDtIniVal());
            orgEnte.setDtFineVal(enteRowBean.getDtFineVal());

            tiOper = ApplEnum.TiOperReplic.MOD;
        } else {
            orgAmbiente = ambienteHelper.findById(OrgAmbiente.class, enteRowBean.getIdAmbiente());
            if (orgAmbiente.getOrgEntes() == null) {
                orgAmbiente.setOrgEntes(new ArrayList<OrgEnte>());
            }
            if (ambienteHelper.getOrgEnteByName(enteRowBean.getNmEnte(), null) != null) {
                throw new ParerUserError("Nome ente gi\u00E0 presente all'interno dell'intero sistema</br>");
            }

            if (enteRowBean.getTipoDefTemplateEnte().equals("TEMPLATE_DEF_ENTE")
                    || enteRowBean.getTipoDefTemplateEnte().equals("NO_TEMPLATE")) {
                if (enteRowBean.getCdEnteNormaliz() == null
                        || ambienteHelper.existsCdEnteNormaliz(enteRowBean.getCdEnteNormaliz(), null)) {
                    throw new ParerUserError(
                            "Il nome normalizzato dell'ente non è stato indicato o non è univoco</br>");
                }
            }

            orgEnte = (OrgEnte) Transform.rowBean2Entity(enteRowBean);
            orgEnte.setOrgAmbiente(orgAmbiente);

            ambienteHelper.insertEntity(orgEnte, true);
            orgAmbiente.getOrgEntes().add(orgEnte);
            tiOper = ApplEnum.TiOperReplic.INS;
            orgEnte = ambienteHelper.getOrgEnteByName(enteRowBean.getNmEnte(),
                    new BigDecimal(orgAmbiente.getIdAmbiente()));
            enteRowBean.entityToRowBean(orgEnte);
            /* Avendo fatto un inserimento, sicuramente nome e descrizione sono stati "modificati" */
            modificatiNomeDescrizione = true;
        }
        IamOrganizDaReplic replic = null;
        if (modificatiNomeDescrizione || modificatoAmbiente) {
            replic = ambienteHelper.insertEntityIamOrganizDaReplic(orgEnte, tiOper);
        }
        return replic;
    }

    private boolean checkSovrapposizioneDate(Date dtIniValAppartAmbienteDb, Date dtFinValAppartAmbienteDb,
            Date dtIniValAppartAmbiente, Date dtFinValAppartAmbiente) {
        boolean check = false;
        // Se la data di decorrenza o quella di scadenza accordo, ricadono all'interno di un intervallo già esistente
        if ((dtIniValAppartAmbienteDb.compareTo(dtIniValAppartAmbiente) <= 0
                && dtFinValAppartAmbienteDb.compareTo(dtIniValAppartAmbiente) >= 0)
                || (dtIniValAppartAmbienteDb.compareTo(dtFinValAppartAmbiente) <= 0
                        && dtFinValAppartAmbienteDb.compareTo(dtFinValAppartAmbiente) >= 0)
                // oppure se l'intevallo del nuovo accordo si sovrappone totalmente ad un intervallo già esistente
                || (dtIniValAppartAmbienteDb.compareTo(dtIniValAppartAmbiente) >= 0
                        && dtFinValAppartAmbienteDb.compareTo(dtFinValAppartAmbiente) <= 0)) {
            check = true;
        }
        return check;
    }

    public void deleteOrgAmbiente(BigDecimal idAmbiente) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.deleteAmbiente(idAmbiente);
        struttureEjb.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteAmbiente(BigDecimal idAmbiente) throws ParerUserError {
        OrgAmbiente ambiente = ambienteHelper.findById(OrgAmbiente.class, idAmbiente);
        if (!ambiente.getOrgEntes().isEmpty()) {
            throw new ParerUserError(
                    "Eliminazione ambiente non riuscita: esistono elementi ancora associati all'ambiente</br>");
        }
        ambienteHelper.removeEntity(ambiente, true);

        IamOrganizDaReplic replic = ambienteHelper.insertEntityIamOrganizDaReplic(ambiente, ApplEnum.TiOperReplic.CANC);
        return replic;
    }

    public void deleteOrgEnte(BigDecimal idEnte) throws ParerUserError {
        AmbienteEjb me = context.getBusinessObject(AmbienteEjb.class);
        IamOrganizDaReplic replic = me.deleteEnte(idEnte);
        struttureEjb.replicateToIam(replic);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteEnte(BigDecimal idEnte) throws ParerUserError {
        OrgEnte ente = ambienteHelper.findById(OrgEnte.class, idEnte);
        if (!ente.getOrgStruts().isEmpty()) {
            throw new ParerUserError(
                    "Eliminazione ente non riuscita: esistono strutture ancora associati all'ente</br>");
        }
        ambienteHelper.removeEntity(ente, true);

        IamOrganizDaReplic replic = ambienteHelper.insertEntityIamOrganizDaReplic(ente, ApplEnum.TiOperReplic.CANC);
        return replic;
    }

    public OrgAmbitoTerritTableBean getOrgAmbitoTerritTableBean(String tipo) {
        OrgAmbitoTerritTableBean tableBean = new OrgAmbitoTerritTableBean();
        List<OrgAmbitoTerrit> list = ambienteHelper.getOrgAmbitoTerritList(tipo);

        if (list != null) {
            try {
                tableBean = (OrgAmbitoTerritTableBean) Transform.entities2TableBean(list);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return tableBean;
    }

    public OrgAmbitoTerritTableBean getOrgAmbitoTerritChildTableBean(BigDecimal idAmbitoTerritoriale) {
        OrgAmbitoTerritTableBean tableBean = new OrgAmbitoTerritTableBean();
        List<OrgAmbitoTerrit> list = ambienteHelper.getOrgAmbitoTerritChildList(idAmbitoTerritoriale);
        if (list != null) {
            try {
                tableBean = (OrgAmbitoTerritTableBean) Transform.entities2TableBean(list);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return tableBean;
    }

    public OrgAmbitoTerritTableBean getOrgAmbitoTerritChildTableBean(List<BigDecimal> idPapiAmbitoTerritList) {
        OrgAmbitoTerritTableBean tableBean = new OrgAmbitoTerritTableBean();
        List<OrgAmbitoTerrit> list = ambienteHelper.getOrgAmbitoTerritChildList(idPapiAmbitoTerritList);
        if (list != null) {
            try {
                tableBean = (OrgAmbitoTerritTableBean) Transform.entities2TableBean(list);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return tableBean;
    }

    public OrgAmbitoTerritRowBean getOrgAmbitoTerritRowBean(String cdAmbitoTerritoriale) {
        OrgAmbitoTerritRowBean orgAmbitoTerritRowBean = new OrgAmbitoTerritRowBean();
        OrgAmbitoTerrit orgAmbitoTerrit = ambienteHelper.getOrgAmbitoTerritByCode(cdAmbitoTerritoriale);

        if (orgAmbitoTerrit != null) {
            try {
                orgAmbitoTerritRowBean = (OrgAmbitoTerritRowBean) Transform.entity2RowBean(orgAmbitoTerrit);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Eccezione", ex);
            }
        }
        return orgAmbitoTerritRowBean;
    }

    public OrgAmbitoTerritRowBean getOrgAmbitoTerritRowBean(BigDecimal idAmbitoTerritoriale) {
        OrgAmbitoTerritRowBean orgAmbitoTerritRowBean = new OrgAmbitoTerritRowBean();
        OrgAmbitoTerrit orgAmbitoTerrit = ambienteHelper.findById(OrgAmbitoTerrit.class, idAmbitoTerritoriale);
        if (orgAmbitoTerrit != null) {
            try {
                orgAmbitoTerritRowBean = (OrgAmbitoTerritRowBean) Transform.entity2RowBean(orgAmbitoTerrit);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Eccezione", ex);
            }
        }
        return orgAmbitoTerritRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void moveOrgAmbitoTerritorialeNode(BigDecimal nodeId, BigDecimal nodeDestId) throws ParerUserError {
        OrgAmbitoTerrit orgAmbitoTerritDB = ambienteHelper.findById(OrgAmbitoTerrit.class, nodeId);
        OrgAmbitoTerrit orgAmbitoTerritDest = ambienteHelper.findById(OrgAmbitoTerrit.class, nodeDestId);
        // se la profondità(nodi figli) del nodo di partenza è minore di quella del nodo di arrivo posso procedere
        if (orgAmbitoTerritDest != null) {
            if (deepChildNodes(orgAmbitoTerritDB) < totChildNodeLevels(orgAmbitoTerritDest)) {

                switch (orgAmbitoTerritDest.getTiAmbitoTerrit()) {
                // Forma Associata non può avere nodi figli
                case "FORMA_ASSOCIATA":
                    throw new ParerUserError(
                            "Impossibile inserire il nodo all'interno di un nodo marcato come \"FORMA ASSOCIATA\"");
                case "PROVINCIA":
                    changeTiAmbitoTerritoriale(orgAmbitoTerritDB, orgAmbitoTerritDest.getTiAmbitoTerrit());
                    break;
                case "REGIONE/STATO":
                    changeTiAmbitoTerritoriale(orgAmbitoTerritDB, orgAmbitoTerritDest.getTiAmbitoTerrit());
                    break;
                }

            } else {
                throw new ParerUserError("Il nodo destinazione non \u00E8 compatibile con il nodo selezionato");
            }
        } else {
            changeTiAmbitoTerritoriale(orgAmbitoTerritDB, "");
        }

        orgAmbitoTerritDB.setOrgAmbitoTerrit(orgAmbitoTerritDest);
    }

    private void changeTiAmbitoTerritoriale(OrgAmbitoTerrit orgAmbitoTerritDB, String tipoPadre) {
        switch (tipoPadre) {
        case "":
            orgAmbitoTerritDB.setTiAmbitoTerrit("REGIONE/STATO");
            for (OrgAmbitoTerrit orgAmbitoTerritChild : orgAmbitoTerritDB.getOrgAmbitoTerrits()) {
                changeTiAmbitoTerritoriale(orgAmbitoTerritChild, "REGIONE/STATO");
            }
            break;
        case "REGIONE/STATO":
            for (OrgAmbitoTerrit orgAmbitoTerritChild : orgAmbitoTerritDB.getOrgAmbitoTerrits()) {
                changeTiAmbitoTerritoriale(orgAmbitoTerritChild, "PROVINCIA");
            }
            orgAmbitoTerritDB.setTiAmbitoTerrit("PROVINCIA");
            break;
        case "PROVINCIA":
            orgAmbitoTerritDB.setTiAmbitoTerrit("FORMA_ASSOCIATA");
            break;
        }
    }

    private int deepChildNodes(OrgAmbitoTerrit orgAmbitoTerritoriale) {
        // se non ha nodi figli
        if (orgAmbitoTerritoriale.getOrgAmbitoTerrits().isEmpty()) {
            return 0;
        } else {

            for (OrgAmbitoTerrit row : orgAmbitoTerritoriale.getOrgAmbitoTerrits()) {
                // se un figlio ha figli a sua volta, la profondità è 3
                if (!row.getOrgAmbitoTerrits().isEmpty()) {
                    return 2;
                }
            }
            // se nessun nodo figlio ha figli a sua volta
            return 1;
        }
    }

    private int totChildNodeLevels(OrgAmbitoTerrit orgAmbitoTerritoriale) {

        switch (orgAmbitoTerritoriale.getTiAmbitoTerrit()) {
        case "REGIONE/STATO":
            return 2;
        case "PROVINCIA":
            return 1;
        default:
            return 0;
        }
    }

    public OrgCategEnteTableBean getOrgCategEnteTableBean(OrgCategEnteRowBean categEnteRowBean) {

        OrgCategEnteTableBean orgCategEnteTableBean = new OrgCategEnteTableBean();
        List<OrgCategEnte> list = null;
        if (categEnteRowBean != null) {
            list = ambienteHelper.getOrgCategEnteList(categEnteRowBean.getCdCategEnte(),
                    categEnteRowBean.getDsCategEnte());
        } else {
            list = ambienteHelper.getOrgCategEnteList(null, null);

        }
        if (list != null) {
            try {

                orgCategEnteTableBean = (OrgCategEnteTableBean) Transform.entities2TableBean(list);

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {

                log.error("Eccezione", ex);
            }
        }

        return orgCategEnteTableBean;
    }

    public OrgCategEnteRowBean getOrgCategEnteRowBean(BigDecimal idCategEnte) {
        return (getOrgCategEnte(idCategEnte, null));
    }

    private OrgCategEnteRowBean getOrgCategEnte(BigDecimal idCategEnte, String cdCategEnte) {
        OrgCategEnteRowBean categEnteRowBean = new OrgCategEnteRowBean();
        OrgCategEnte categEnte = new OrgCategEnte();

        if (idCategEnte != BigDecimal.ZERO && cdCategEnte == null) {
            categEnte = ambienteHelper.findById(OrgCategEnte.class, idCategEnte);
        } else if (idCategEnte == BigDecimal.ZERO && cdCategEnte != null) {
            categEnte = ambienteHelper.getOrgCategEnteByCd(cdCategEnte);
        }

        if (categEnte != null) {
            try {
                categEnteRowBean = (OrgCategEnteRowBean) Transform.entity2RowBean(categEnte);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                log.error("Eccezione", ex);
            }
        }

        return categEnteRowBean;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertOrgCategEnte(OrgCategEnteRowBean categEnteRowBean) throws ParerUserError {
        OrgCategEnte categEnteDB = ambienteHelper.getOrgCategEnteByCd(categEnteRowBean.getCdCategEnte());
        if (categEnteDB != null) {
            throw new ParerUserError("Codice categoria gi\u00E0 utilizzato nel database, scegliere altro codice ");
        }
        // MAC#24363
        categEnteDB = ambienteHelper.getOrgCategEnteByDesc(categEnteRowBean.getDsCategEnte());
        if (categEnteDB != null) {
            throw new ParerUserError(
                    "Descrizione categoria gi\u00E0 utilizzata nel database, scegliere altra descrizione ");
        }
        // end MAC#24363

        OrgCategEnte categEnte = (OrgCategEnte) Transform.rowBean2Entity(categEnteRowBean);
        ambienteHelper.insertEntity(categEnte, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateOrgCategEnte(BigDecimal idCategEnte, OrgCategEnteRowBean categEnteRowBean) throws ParerUserError {
        OrgCategEnte categEnteDB = ambienteHelper.getOrgCategEnteByCd(categEnteRowBean.getCdCategEnte());
        if (categEnteDB != null && categEnteDB.getIdCategEnte() != idCategEnte.longValue()) {
            throw new ParerUserError("Codice categoria gi\u00E0 utilizzato nel database, scegliere altro codice ");
        }
        // MAC#24363
        categEnteDB = ambienteHelper.getOrgCategEnteByDesc(categEnteRowBean.getDsCategEnte());
        if (categEnteDB != null && categEnteDB.getIdCategEnte() != idCategEnte.longValue()) {
            throw new ParerUserError(
                    "Descrizione categoria gi\u00E0 utilizzata nel database, scegliere altra descrizione ");
        }
        // end MAC#24363

        OrgCategEnte categEnte = ambienteHelper.findById(OrgCategEnte.class, idCategEnte);
        categEnte.setCdCategEnte(categEnteRowBean.getCdCategEnte());
        categEnte.setDsCategEnte(categEnteRowBean.getDsCategEnte());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteOrgCategEnte(OrgCategEnteRowBean orgCategEnteRowBean) throws ParerUserError {
        OrgCategEnte categEnteDB = ambienteHelper.findById(OrgCategEnte.class, orgCategEnteRowBean.getIdCategEnte());
        if (!categEnteDB.getOrgEntes().isEmpty()) {
            throw new ParerUserError("Categoria associata a enti. Operazione cancellata.");
        }
        ambienteHelper.removeEntity(categEnteDB, true);
    }

    public OrgCategStrutTableBean getOrgCategStrutTableBean() {
        OrgCategStrutTableBean orgCategStrutTableBean = new OrgCategStrutTableBean();
        List<OrgCategStrut> list = ambienteHelper.getOrgCategStrutList();
        if (list != null && !list.isEmpty()) {
            try {
                orgCategStrutTableBean = (OrgCategStrutTableBean) Transform.entities2TableBean(list);
            } catch (Exception ex) {
                log.error("Eccezione", ex);
            }
        }
        return orgCategStrutTableBean;
    }

    public boolean isCreaAmbienteActive(long idUser, String nmApplic) {
        UsrVChkCreaAmbSacer record = ambienteHelper.getUsrVChkCreaAmbSacer(idUser, nmApplic);
        return record.getFlCreaAmbiente().equals("1");
    }

    public BaseTableInterface getAmbientiAbilitatiPerEnte(long idUser, String nmApplic) {
        BaseTable table = new BaseTable();
        List<UsrVAbilAmbXente> ambientiAbilitati = ambienteHelper.getAmbientiAbilitatiPerEnte(idUser, nmApplic);
        for (UsrVAbilAmbXente ambiente : ambientiAbilitati) {
            BaseRow row = new BaseRow();
            row.setBigDecimal("id_ambiente", ambiente.getIdOrganizApplic());
            row.setString("nm_ambiente", ambiente.getNmOrganiz());
            row.setString("ds_ambiente", ambiente.getDsOrganiz());
            table.add(row);
        }

        return table;
    }

    public BaseTableInterface getAmbientiAbilitatiPerStrut(long idUser, String nmApplic) {
        BaseTable table = new BaseTable();
        List<UsrVAbilAmbSacerXstrut> ambientiAbilitati = ambienteHelper.getAmbientiAbilitatiPerStrut(idUser, nmApplic);
        for (UsrVAbilAmbSacerXstrut ambiente : ambientiAbilitati) {
            BaseRow row = new BaseRow();
            row.setBigDecimal("id_ambiente", ambiente.getIdOrganizApplic());
            row.setString("nm_ambiente", ambiente.getNmOrganiz());
            row.setString("ds_ambiente", ambiente.getDsOrganiz());
            table.add(row);
        }

        return table;
    }

    public BaseTableInterface getEntiAbilitatiPerStrut(long idUser, String nmApplic, String nmEnte,
            BigDecimal idAmbiente, List<String> tipoDefTemplateEnte) {
        BaseTable table = new BaseTable();
        List<UsrVAbilEnteSacerXstrut> entiAbilitati = ambienteHelper.getEntiAbilitatiPerStrut(idUser, nmApplic, nmEnte,
                idAmbiente, tipoDefTemplateEnte);
        for (UsrVAbilEnteSacerXstrut ente : entiAbilitati) {
            BaseRow row = new BaseRow();
            row.setBigDecimal("id_ente", ente.getIdOrganizApplic());
            row.setString("nm_ente", ente.getNmOrganiz());
            row.setString("ds_ente", ente.getDsOrganiz());
            row.setString("nmDs", ente.getNmOrganiz() + ", " + ente.getDsOrganiz());
            table.add(row);
        }

        return table;
    }

    public BaseTableInterface getEntiValidiAbilitatiPerStrut(long idUser, String nmApplic, String nmEnte,
            BigDecimal idAmbiente, List<String> tipoDefTemplateEnte) {
        Date currentDate = new Date();
        BaseTable table = new BaseTable();
        List<UsrVAbilEnteSacerXstrut> entiAbilitati = ambienteHelper.getEntiAbilitatiPerStrut(idUser, nmApplic, nmEnte,
                idAmbiente, tipoDefTemplateEnte);
        for (UsrVAbilEnteSacerXstrut enteAbil : entiAbilitati) {
            // MEV#20463: filtro gli enti validi alla data, (La validità di un ente e’ definita dall’intervallo “Data
            // inizio validità” e “Data fine validità”, estremi compresi)
            OrgEnte ente = ambienteHelper.findById(OrgEnte.class, enteAbil.getIdOrganizApplic().longValue());
            if (!ente.getDtIniVal().after(currentDate) && !ente.getDtFineVal().before(currentDate)) {
                BaseRow row = new BaseRow();
                row.setBigDecimal("id_ente", BigDecimal.valueOf(ente.getIdEnte()));
                row.setString("nm_ente", ente.getNmEnte());
                row.setString("ds_ente", ente.getDsEnte());
                row.setString("nmDs", ente.getNmEnte() + ", " + ente.getDsEnte());
                table.add(row);
            }
        }

        return table;
    }

    /**
     * Metodo per la costruzione di un table bean dalla tabella Org Ambiente
     *
     * @param idUser
     *            id utente abilitato
     * @param nmAmbiente
     *            nome ambiente da ricercare
     * 
     * @return tableBean corrispondente ai criteri di ricerca
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public OrgVRicAmbienteTableBean getAmbientiAbilitatiPerRicerca(long idUser, String nmAmbiente)
            throws ParerUserError {
        OrgVRicAmbienteTableBean ambientiTableBean = new OrgVRicAmbienteTableBean();
        List<OrgVRicAmbiente> listaAmbienti = ambienteHelper.getAmbientiAbilitatiRicerca(idUser, nmAmbiente);
        try {
            for (OrgVRicAmbiente ambiente : listaAmbienti) {
                // trasformo la lista in un tableBean
                OrgVRicAmbienteRowBean ambienteRowBean = new OrgVRicAmbienteRowBean();
                ambienteRowBean = (OrgVRicAmbienteRowBean) Transform.entity2RowBean(ambiente);
                ambienteRowBean.setString("nm_ente_conserv",
                        ambienteHelper.findById(SIOrgEnteSiam.class, ambiente.getIdEnteConverv()).getNmEnteSiam());
                ambienteRowBean.setString("nm_ente_gestore",
                        ambienteHelper.findById(SIOrgEnteSiam.class, ambiente.getIdEnteGestore()).getNmEnteSiam());
                // ambientiTableBean = (OrgVRicAmbienteTableBean) Transform.entities2TableBean(listaAmbienti);
                ambientiTableBean.add(ambienteRowBean);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli ambienti" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nel recupero degli ambienti");
        }
        return ambientiTableBean;
    }

    public OrgAmbienteTableBean getAmbientiAbilitati(long idUser) throws ParerUserError {
        OrgAmbienteTableBean ambientiTableBean = new OrgAmbienteTableBean();
        List<OrgAmbiente> listaAmbienti = ambienteHelper.retrieveOrgAmbienteFromAbil(idUser);
        try {
            if (!listaAmbienti.isEmpty()) {
                // trasformo la lista in un tableBean
                ambientiTableBean = (OrgAmbienteTableBean) Transform.entities2TableBean(listaAmbienti);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli ambienti" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new ParerUserError("Errore nel recupero degli ambienti");
        }
        return ambientiTableBean;
    }

    public OrgVRicEnteTableBean getEntiAbilitatiTemplate(long idUser, BigDecimal idAmbiente,
            String tipoDefTemplateEnte) {
        OrgVRicEnteTableBean entiTableBean = new OrgVRicEnteTableBean();
        List<OrgVRicEnte> listaEnti = ambienteHelper.getEntiAbilitatiRicerca(idUser, idAmbiente, null,
                tipoDefTemplateEnte);
        try {
            if (!listaEnti.isEmpty()) {
                // trasformo la lista in un tableBean
                entiTableBean = (OrgVRicEnteTableBean) Transform.entities2TableBean(listaEnti);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti");
        }
        return entiTableBean;
    }

    /**
     * Recupera l'ente in base all'ambiente (a sua volta recuperato in base alle abilitazioni)
     *
     * @param idUser
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param filterValid
     *            true/false
     * 
     * @return OrgEnteTableBean
     */
    public OrgEnteTableBean getEntiAbilitatiNoTemplate(long idUser, long idAmbiente, Boolean filterValid) {
        OrgEnteTableBean entiTableBean = new OrgEnteTableBean();
        List<OrgEnte> listaEnti = ambienteHelper.retrieveOrgEnteAbilNoTemplate(idUser, idAmbiente, filterValid);
        return transformOrgEnteList(listaEnti, entiTableBean);
    }

    /**
     * Recupera l'ente in base all'ambiente (a sua volta recuperato in base alle abilitazioni)
     *
     * @param idUser
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param filterValid
     *            true/false
     * 
     * @return OrgEnteTableBean
     */
    public OrgEnteTableBean getEntiAbilitati(long idUser, long idAmbiente, Boolean filterValid) {
        OrgEnteTableBean entiTableBean = new OrgEnteTableBean();
        List<OrgEnte> listaEnti = ambienteHelper.retrieveOrgEnteAbil(idUser, idAmbiente, null, null, filterValid,
                (String[]) null);
        return transformOrgEnteList(listaEnti, entiTableBean);
        // trasformo la lista in un tableBean
    }

    /**
     * Recupera l'ente in base all'ambiente, agli ambiti territoriali e alle categorie enti (a sua volta recuperato in
     * base alle abilitazioni)
     *
     * @param idUser
     *            id utente
     * @param idAmbiente
     *            id ambiente
     * @param idAmbitoTerritList
     *            id ambito territoriale (lista)
     * @param idCategEnteList
     *            id categoria ente (lista)
     * @param filterValid
     *            true/false
     * 
     * @return OrgEnteTableBean
     */
    public OrgEnteTableBean getEntiAbilitatiAmbitoCateg(long idUser, BigDecimal idAmbiente,
            List<BigDecimal> idAmbitoTerritList, List<BigDecimal> idCategEnteList, Boolean filterValid) {
        OrgEnteTableBean entiTableBean = new OrgEnteTableBean();
        List<OrgEnte> listaEnti = ambienteHelper.retrieveOrgEnteAbil(idUser, idAmbiente.longValue(), idAmbitoTerritList,
                idCategEnteList, filterValid);
        return transformOrgEnteList(listaEnti, entiTableBean);
    }

    private OrgEnteTableBean transformOrgEnteList(List<OrgEnte> listaEnti, OrgEnteTableBean entiTableBean)
            throws IllegalStateException {
        try {
            if (!listaEnti.isEmpty()) {
                // trasformo la lista in un tableBean
                entiTableBean = (OrgEnteTableBean) Transform.entities2TableBean(listaEnti);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti");
        }
        return entiTableBean;
    }

    public String getNmEnteConvenz(String nmApplic, String nmTipoOrganiz, BigDecimal idOrganizApplic) {
        return ambienteHelper.getNmEnteConvenz(nmApplic, nmTipoOrganiz, idOrganizApplic);
    }

    public BaseTable getSIOrgEnteSiamTableBean(BigDecimal idAmbienteEnteConvenz) {
        BaseTable entiTableBean = new BaseTable();
        List<SIOrgEnteSiam> listaEnti = ambienteHelper.retrieveSiOrgEnteConvenz(idAmbienteEnteConvenz);
        try {
            for (SIOrgEnteSiam ente : listaEnti) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_siam", new BigDecimal(ente.getIdEnteSiam()));
                riga.setString("nm_ente_siam", ente.getNmEnteSiam());
                entiTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti siam" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti siam");
        }
        return entiTableBean;
    }

    /**
     * Ricava il tablebean contenente gli enti convenzionati dell'ambiente ente passato in input
     *
     * @param idUserUamCor
     *            id utente corrente
     * @param idAmbienteEnteConvenz
     *            id ambiente convenzionato
     * 
     * @return BaseTable
     */
    public BaseTable getSIOrgEnteConvenzTableBean(long idUserUamCor, BigDecimal idAmbienteEnteConvenz) {
        BaseTable entiTableBean = new BaseTable();
        List<SIOrgEnteSiam> listaEnti = ambienteHelper.getEntiConvenzionatiAbilitati(idUserUamCor,
                idAmbienteEnteConvenz);
        try {
            for (SIOrgEnteSiam ente : listaEnti) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_siam", new BigDecimal(ente.getIdEnteSiam()));
                riga.setString("nm_ente_siam", ente.getNmEnteSiam());
                entiTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti siam" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti siam");
        }
        return entiTableBean;
    }

    /**
     * Ricava il tablebean contenente gli enti convenzionati validi (ovvero esiste almeno un accordo valido alla data
     * odierna) dell'ambiente ente passato in input
     *
     * @param idUserUamCor
     *            id utente corrente
     * @param idAmbienteEnteConvenz
     *            id ambiente convenzionato
     * 
     * @return BaseTable
     */
    public BaseTable getSIOrgEnteConvenzAccordoValidoTableBean(long idUserUamCor, BigDecimal idAmbienteEnteConvenz) {
        BaseTable entiTableBean = new BaseTable();
        List<SIOrgEnteSiam> listaEnti = ambienteHelper.getEntiConvenzionatiValidiAbilitati(idUserUamCor,
                idAmbienteEnteConvenz);
        try {
            for (SIOrgEnteSiam ente : listaEnti) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_siam", new BigDecimal(ente.getIdEnteSiam()));
                riga.setString("nm_ente_siam", ente.getNmEnteSiam());
                entiTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti siam" + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti siam");
        }
        return entiTableBean;
    }

    /**
     * Restituisce il rowbean contenente le date di inizio e fine validità dell'associazione ente siam/struttura. Il
     * sistema propone in automatico la data inizio validità dell’associazione (dt_ini_val) = data decorrenza accordo
     * valido e data fine validità associazione (dt_fine_val) = data fine validità ente siam
     *
     * @param idEnteConvenz
     *            l'id dell'ente convenzionato da recuperare su DB
     * 
     * @return il rowbean contenente il le date
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public BaseRow getDateAssociazioneEnteSiamStrutRowBean(BigDecimal idEnteConvenz) throws ParerUserError {
        BaseRow dateEnteConvenzStrutRowBean = null;
        SIOrgEnteSiam enteSiam = ambienteHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
        SIOrgAccordoEnte accordoEnte = ambienteHelper.retrieveOrgAccordoValidoEnteConvenz(idEnteConvenz);
        if (accordoEnte != null && enteSiam != null) {
            try {
                dateEnteConvenzStrutRowBean = new BaseRow();
                if (accordoEnte.getDtDecAccordo() != null) {
                    dateEnteConvenzStrutRowBean.setTimestamp("dt_ini_val",
                            new Timestamp(accordoEnte.getDtDecAccordo().getTime()));
                }
                if (enteSiam.getDtCessazione() != null) {
                    dateEnteConvenzStrutRowBean.setTimestamp("dt_fine_val",
                            new Timestamp(enteSiam.getDtCessazione().getTime()));
                }
            } catch (Exception ex) {
                String msg = "Errore durante il recupero delle date" + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return dateEnteConvenzStrutRowBean;
    }

    public BaseTable getUsrVAbilAmbEnteConvenzTableBean(BigDecimal idUserIam) {
        BaseTable abilAmbEnteConvenzTableBean = new BaseTable();
        List<UsrVAbilAmbEnteConvenz> abilAmbEnteConvenzList = ambienteHelper
                .retrieveAmbientiEntiConvenzAbilitati(idUserIam);
        if (!abilAmbEnteConvenzList.isEmpty()) {
            try {
                for (UsrVAbilAmbEnteConvenz abilAmbEnteConvenz : abilAmbEnteConvenzList) {
                    BaseRow riga = new BaseRow();
                    riga.setBigDecimal("id_ambiente_ente_convenz", abilAmbEnteConvenz.getIdAmbienteEnteConvenz());
                    riga.setString("nm_ambiente_ente_convenz", abilAmbEnteConvenz.getNmAmbienteEnteConvenz());
                    abilAmbEnteConvenzTableBean.add(riga);
                }
            } catch (Exception e) {
                log.error(
                        "Errore nel recupero degli ambienti enti convenzionati" + ExceptionUtils.getRootCauseMessage(e),
                        e);
                throw new IllegalStateException("Errore inatteso nel recupero degli ambienti enti convenzionati");
            }
        }
        return abilAmbEnteConvenzTableBean;
    }

    public SIOrgEnteConvenzOrgTableBean getSIOrgEnteConvenzOrgTableBean(BigDecimal idStrut) {
        SIOrgEnteConvenzOrgTableBean siEnteConvenzTableBean = new SIOrgEnteConvenzOrgTableBean();
        List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = ambienteHelper.retrieveSIOrgEnteConvenzOrg(idStrut);
        if (!siEnteConvenzOrgList.isEmpty()) {
            try {
                for (SIOrgEnteConvenzOrg orgEnteConvenzOrg : siEnteConvenzOrgList) {
                    SIOrgEnteConvenzOrgRowBean row = (SIOrgEnteConvenzOrgRowBean) Transform
                            .entity2RowBean(orgEnteConvenzOrg);
                    row.setBigDecimal("id_ambiente_ente_convenz", new BigDecimal(orgEnteConvenzOrg.getSiOrgEnteConvenz()
                            .getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()));
                    row.setString("nm_ambiente_ente_convenz", orgEnteConvenzOrg.getSiOrgEnteConvenz()
                            .getSiOrgAmbienteEnteConvenz().getNmAmbienteEnteConvenz());
                    row.setBigDecimal("id_ente_convenz",
                            new BigDecimal(orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam()));
                    row.setString("nm_ente_convenz", orgEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam());
                    // MEV#20767
                    BigDecimal idAmbitoTerrit = orgEnteConvenzOrg.getSiOrgEnteConvenz().getIdAmbitoTerrit();
                    if (idAmbitoTerrit != null) {
                        OrgVTreeAmbitoTerrit treeAmbitoTerrit = ambienteHelper.findViewById(OrgVTreeAmbitoTerrit.class,
                                idAmbitoTerrit);
                        row.setString("ds_tree_cd_ambito_territ", treeAmbitoTerrit.getDsTreeCdAmbitoTerrit());
                    }
                    // end MEV#20767
                    siEnteConvenzTableBean.add(row);
                }
            } catch (Exception e) {
                log.error("Errore nel recupero degli enti convenzionati per la struttura : "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException(
                        "Errore inatteso nel recupero degli enti convenzionati per la struttura");
            }
        }
        return siEnteConvenzTableBean;
    }

    public SIOrgEnteConvenzOrgRowBean getSIOrgEnteConvenzOrgRowBean(BigDecimal idEnteConvenzOrg) {
        SIOrgEnteConvenzOrgRowBean siEnteConvenzRowBean = new SIOrgEnteConvenzOrgRowBean();
        SIOrgEnteConvenzOrg siEnteConvenzOrg = ambienteHelper.findById(SIOrgEnteConvenzOrg.class, idEnteConvenzOrg);
        if (siEnteConvenzOrg != null) {
            try {
                siEnteConvenzRowBean = (SIOrgEnteConvenzOrgRowBean) Transform.entity2RowBean(siEnteConvenzOrg);
                siEnteConvenzRowBean.setBigDecimal("id_ambiente_ente_convenz", new BigDecimal(siEnteConvenzOrg
                        .getSiOrgEnteConvenz().getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()));
                siEnteConvenzRowBean.setString("nm_ambiente_ente_convenz", siEnteConvenzOrg.getSiOrgEnteConvenz()
                        .getSiOrgAmbienteEnteConvenz().getNmAmbienteEnteConvenz());
                siEnteConvenzRowBean.setBigDecimal("id_ente_convenz",
                        new BigDecimal(siEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam()));
                siEnteConvenzRowBean.setString("nm_ente_convenz",
                        siEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam());
            } catch (Exception e) {
                log.error("Errore nel recupero dell'associazione struttura - ente convenzionato: "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException(
                        "Errore inatteso nel recupero dell'associazione struttura - ente convenzionato");
            }
        }
        return siEnteConvenzRowBean;
    }

    public BaseTable getEntiGestoreAbilitatiTableBean(BigDecimal idUserIamCor, BigDecimal idAmbienteEnteConvenz) {
        BaseTable ricEnteConvenzTableBean = new BaseTable();
        List<OrgVRicEnteConvenzByEsterno> ricEnteConvenzList = ambienteHelper
                .getOrgVRicEnteConvenzByEstList(idUserIamCor, idAmbienteEnteConvenz, "PRODUTTORE");
        try {
            for (OrgVRicEnteConvenzByEsterno ricEnteConvenz : ricEnteConvenzList) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_gestore", ricEnteConvenz.getIdEnteConvenz());
                riga.setString("nm_ente_gestore", ricEnteConvenz.getNmEnteConvenz());
                ricEnteConvenzTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero degli enti gestori: " + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero degli enti gestori");
        }
        return ricEnteConvenzTableBean;
    }

    public BaseTable getEnteGestoreCorrenteTableBean(BigDecimal idAmbiente) {
        BaseTable ricEnteConvenzTableBean = new BaseTable();
        OrgAmbiente ambiente = ambienteHelper.findById(OrgAmbiente.class, idAmbiente);
        try {
            if (ambiente != null) {
                BaseRow riga = new BaseRow();
                riga.setBigDecimal("id_ente_gestore", ambiente.getIdEnteGestore());
                riga.setString("nm_ente_gestore",
                        ambienteHelper.findById(SIOrgEnteSiam.class, ambiente.getIdEnteGestore()).getNmEnteSiam());
                ricEnteConvenzTableBean.add(riga);
            }
        } catch (Exception e) {
            log.error("Errore nel recupero dell'ente gestore corrente: " + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore inatteso nel recupero del'ente gestore corrente");
        }
        return ricEnteConvenzTableBean;
    }

    public BaseRow getEnteConservatore(BigDecimal idEnteSiamGestore) {
        BaseRow riga = null;
        // Ricerco l’ente convenzionato avente id_ente_siam = id_ente_siam scelto come gestore
        // e da esso ricerco l'accordo valido alla data. Una volta trovato l'accordo, ricavo l'ente convenz conserv
        SIOrgEnteSiam enteConvenzConserv = ambienteHelper.getEnteConvenzConserv(idEnteSiamGestore);
        if (enteConvenzConserv != null) {
            riga = new BaseRow();
            riga.setBigDecimal("id_ente_siam", BigDecimal.valueOf(enteConvenzConserv.getIdEnteSiam()));
            riga.setString("nm_ente_siam", enteConvenzConserv.getNmEnteSiam());
        }
        return riga;
    }

    public BaseTable getEntiConservatori(long idUserIamCor, BigDecimal idEnteSiamGestore) {
        BaseTable tabella = new BaseTable();
        List<SIOrgEnteSiam> entiConvenzConserv = ambienteHelper.getEnteConvenzConservList(idUserIamCor,
                idEnteSiamGestore);
        for (SIOrgEnteSiam ente : entiConvenzConserv) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_ente_siam", BigDecimal.valueOf(ente.getIdEnteSiam()));
            riga.setString("nm_ente_siam", ente.getNmEnteSiam());
            tabella.add(riga);
        }
        return tabella;
    }

    public BigDecimal getIdAmbienteEnteConvenz(BigDecimal idEnteConvenz) {
        SIOrgAmbienteEnteConvenz ambienteEnteConvenz = ambienteHelper
                .getSIOrgAmbienteEnteConvenzByEnteConvenz(idEnteConvenz);
        return BigDecimal.valueOf(ambienteEnteConvenz.getIdAmbienteEnteConvenz());
    }

    private void updateOrgStrutWithMostRecenteEnteConvenz(OrgStrut strut) {
        List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = ambienteHelper
                .retrieveSIOrgEnteConvenzOrg(BigDecimal.valueOf(strut.getIdStrut()));
        strut.setIdEnteConvenz(BigDecimal.valueOf(siEnteConvenzOrgList.get(0).getSiOrgEnteConvenz().getIdEnteSiam()));
        strut.setDtIniVal(siEnteConvenzOrgList.get(0).getDtIniVal());
        strut.setDtFineVal(siEnteConvenzOrgList.get(0).getDtFineVal());
    }

    /**
     * Inserisce una nuova associazione struttura - ente convenzionato. In caso di problemi sull'allineamento ente
     * convenzionato (metodo alignsEnteConvenzToIam) viene eseguita rollback solo per l'allineamento e non
     * sull'inserimento associazione
     *
     * @param param
     *            parametro
     * @param idStrut
     *            id struttura
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     * 
     * @return BigDecimal
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public BigDecimal insertEnteConvenzOrg(LogParam param, BigDecimal idStrut, BigDecimal idEnteConvenz, Date dtIniVal,
            Date dtFineVal) throws ParerUserError {

        IamEnteConvenzDaAllinea enteConvenzDaAllinea = context.getBusinessObject(AmbienteEjb.class)
                .saveEnteConvenzOrg(param, idStrut, idEnteConvenz, dtIniVal, dtFineVal);

        if (enteConvenzDaAllinea != null) {
            List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList = new ArrayList<>();
            enteConvenzDaAllineaList.add(enteConvenzDaAllinea);
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            struttureEjb.alignsEnteConvenzToIam(enteConvenzDaAllineaList);
        }

        SIOrgEnteConvenzOrg enteSalvato = ambienteHelper.getSIOrgEnteConvenzOrg(idStrut, idEnteConvenz, dtIniVal);
        return BigDecimal.valueOf(enteSalvato.getIdEnteConvenzOrg());
    }

    /**
     * Aggiorna l'associazione struttura - ente convenzionato. In caso di problemi sull'allineamento ente convenzionato
     * (metodo alignsEnteConvenzToIam) viene eseguita rollback solo per l'allineamento e non sulla modifica associazione
     *
     * @param param
     *            parametro
     * @param idEnteConvenzOrg
     *            id ente convezionato su organizzazione
     * @param idEnteConvenz
     *            id ente convenzionato
     * @param idStrut
     *            id struttura
     * @param dtIniVal
     *            data inizio validita
     * @param dtFineVal
     *            data fine validita
     * 
     * @return BigDecimal
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public BigDecimal updateEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg, BigDecimal idEnteConvenz,
            BigDecimal idStrut, Date dtIniVal, Date dtFineVal) throws ParerUserError {

        List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList = context.getBusinessObject(AmbienteEjb.class)
                .saveEnteConvenzOrg(param, idEnteConvenzOrg, idStrut, idEnteConvenz, dtIniVal, dtFineVal);

        if (!enteConvenzDaAllineaList.isEmpty()) {
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            struttureEjb.alignsEnteConvenzToIam(enteConvenzDaAllineaList);
        }

        SIOrgEnteConvenzOrg enteSalvato = ambienteHelper.getSIOrgEnteConvenzOrg(idStrut, idEnteConvenz, dtIniVal);
        return BigDecimal.valueOf(enteSalvato.getIdEnteConvenzOrg());
    }

    /**
     * Business method per la cancellazione di una singola associazione struttura - ente convenzionato. passata come
     * parametro. Il metodo chiama a sua volta il metodo "deleteEnteConvenzOrgTx" all'interno dello stesso contesto
     * transazionale. In caso ci siano problemi, si rilancia l'eccezione e l'annotation
     *
     * @param param
     *            parametro
     * @param idEnteConvenzOrg
     *            Sul metodo procede ad eseguire il rollback. Se invece il metodo non genera eccezioni viene chiamata
     *            l'allineamento su Iam. In questo caso, dovesse verificarsi un errore, la rollback verrebbe gestita
     *            tramite ParerUserError (del metodo alignsEnteConvenzToIam) ed essendo stato un creato un nuovo
     *            contesto transazionale (REQUIRES_NEW) la rollback avrebbe effetto solo sulla replica (non voglio
     *            rollbackare tutto...)
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void deleteEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg) throws ParerUserError {
        IamEnteConvenzDaAllinea enteConvenzDaAllinea = context.getBusinessObject(AmbienteEjb.class)
                .deleteEnteConvenzOrgTx(param, idEnteConvenzOrg);

        if (enteConvenzDaAllinea != null) {
            List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList = new ArrayList<>();
            enteConvenzDaAllineaList.add(enteConvenzDaAllinea);
            // Chiamata al WS di ALLINEA_ENTE_CONVENZIONATO DI IAM su ente dell'associazione
            struttureEjb.alignsEnteConvenzToIam(enteConvenzDaAllineaList);
        }
    }

    /**
     * Inserimento di una nuova associazione struttura - ente convenzionato dati i parametri in input
     *
     * @param param
     *            parametro
     * @param idStrut
     *            id della struttura su cui eseguire l'associazione
     * @param idEnteConvenz
     *            id dell'ente convenzionato
     * @param dtIniVal
     *            data inizio validità
     * @param dtFineVal
     *            data fine validità
     * 
     * @return IamEnteConvenzDaAllinea
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamEnteConvenzDaAllinea saveEnteConvenzOrg(LogParam param, BigDecimal idStrut, BigDecimal idEnteConvenz,
            Date dtIniVal, Date dtFineVal) throws ParerUserError {
        log.debug("Eseguo l'inserimento di una nuova associazione struttura - ente convenzionato");
        IamEnteConvenzDaAllinea allinea = null;
        SIOrgEnteConvenzOrg siOrgEnteConvenzOrg;
        try {
            String nmApplic = paramApplicHelper.getApplicationName().getDsValoreParamApplic();
            if (struttureHelper.checkEsistenzaAssociazioneEnteConvenzStrutVers(nmApplic, idStrut, dtIniVal, dtFineVal,
                    null)) {
                throw new ParerUserError(
                        "Nel periodo indicato la struttura risulta gi\u00E0 associata ad un altro ente convenzionato: impossibile eseguire la modifica");
            }

            // MAC 26960
            // Controllo l'intervallo dell'associazione
            if (!struttureHelper.existsIntervalloValiditaPerAssociazione(idEnteConvenz, dtIniVal, dtFineVal)) {
                throw new ParerUserError(
                        "L’intervallo di validità dell'associazione non rientra nell'intervallo compreso tra "
                                + "la data di decorrenza accordo valido e la data di fine validità dell'ente");
            }
            // MAC 26960

            SIUsrOrganizIam organiz = ambienteHelper.getSIUsrOrganizIam(idStrut);

            // Recupero l'organizzazione su IAM, se esiste altrimenti mando in errore
            if (organiz == null) {
                throw new ParerUserError(
                        "Attenzione: la struttura non risulta replicata in IAM, è dunque impossibile procedere con l'operazione");
            }

            // Salvo l'associazione in SACER_IAM.ORG_ENTE_CONVENZ_ORG
            siOrgEnteConvenzOrg = new SIOrgEnteConvenzOrg();
            SIOrgEnteSiam enteConvenz = ambienteHelper.findById(SIOrgEnteSiam.class, idEnteConvenz);
            siOrgEnteConvenzOrg.setSiOrgEnteConvenz(enteConvenz);
            siOrgEnteConvenzOrg.setSiUsrOrganizIam(organiz);
            siOrgEnteConvenzOrg.setDtIniVal(dtIniVal);
            siOrgEnteConvenzOrg.setDtFineVal(dtFineVal);
            ambienteHelper.insertEntity(siOrgEnteConvenzOrg, false);

            // Salvo in ORG_STRUT l'ente convenzionato associato se è quello più recente
            OrgStrut strut = ambienteHelper.findById(OrgStrut.class, idStrut);
            List<SIOrgEnteConvenzOrg> siEnteConvenzOrgList = ambienteHelper.retrieveSIOrgEnteConvenzOrg(idStrut);
            strut.setIdEnteConvenz(
                    BigDecimal.valueOf(siEnteConvenzOrgList.get(0).getSiOrgEnteConvenz().getIdEnteSiam()));
            strut.setDtIniVal(siEnteConvenzOrgList.get(0).getDtIniVal());
            strut.setDtFineVal(siEnteConvenzOrgList.get(0).getDtFineVal());
            //
            ambienteHelper.getEntityManager().flush();

            // Inserito per loggare la foto della STRUTTURA modificata
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA, idStrut, param.getNomePagina());

            // Salvo il record dell'associazione da salvare in IAM
            allinea = struttureEjb.insertIamEnteConvenzDaAllinea(BigDecimal.valueOf(enteConvenz.getIdEnteSiam()),
                    enteConvenz.getNmEnteSiam());
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'ente convenzionato associato alla struttura ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
        return allinea;
    }

    /**
     * Modifica di una associazione struttura - ente convenzionato dati i parametri in input
     *
     * @param param
     *            parametro
     * @param idEnteConvenzOrg
     *            id dell'associazione da modificare
     * @param idStrut
     *            id della struttura su cui eseguire l'associazione
     * @param idEnteConvenz
     *            id dell'ente convenzionato
     * @param dtIniVal
     *            data inizio validità
     * @param dtFineVal
     *            data fine validità
     * 
     * @return L'oggetto IamOrganizDaReplic con cui eseguire la replica a SacerIam
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<IamEnteConvenzDaAllinea> saveEnteConvenzOrg(LogParam param, BigDecimal idEnteConvenzOrg,
            BigDecimal idStrut, BigDecimal idEnteConvenz, Date dtIniVal, Date dtFineVal) throws ParerUserError {
        log.debug("Eseguo il salvataggio dell'ente convenzionato");
        List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList = new ArrayList<>();
        SIOrgEnteConvenzOrg enteConvenzOrg = null;
        try {
            String nmApplic = paramApplicHelper.getApplicationName().getDsValoreParamApplic();

            // Recupero la "vecchia" associazione ed eventuale "nuovo" ente convenzionato associato
            enteConvenzOrg = ambienteHelper.findById(SIOrgEnteConvenzOrg.class, idEnteConvenzOrg);
            long idEnteConvenzOld = enteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam();
            String nmEnteConvenzOld = enteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam();
            Date dtIniValOld = enteConvenzOrg.getDtIniVal();
            Date dtFineValOld = enteConvenzOrg.getDtFineVal();

            // Controllo se si sovrappongono periodi, escludendo ovviamente quello che sto trattando
            if (struttureHelper.checkEsistenzaAssociazioneEnteConvenzStrutVers(nmApplic, idStrut, dtIniVal, dtFineVal,
                    idEnteConvenzOrg)) {
                throw new ParerUserError(
                        "Nel periodo indicato la struttura risulta gi\u00E0 associata ad un altro ente convenzionato: impossibile eseguire la modifica");
            }

            // MAC 26960
            if (!struttureHelper.existsPeriodoValiditaAssociazioneEnteConvenzStrutVersAccordi(idEnteConvenz, dtIniVal,
                    dtFineVal)) {
                throw new ParerUserError(
                        "L’intervallo di validità dell'associazione non rientra nell'intervallo compreso tra la data di decorrenza del primo accordo definito sull'ente e la data di fine validità dell'ente siam");
            }
            // MAC 26960

            OrgStrut strut = ambienteHelper.findById(OrgStrut.class, idStrut);

            // CASO 1: ho modificato ANCHE l'ente convenzionato
            if (idEnteConvenzOld != idEnteConvenz.longValue()) {

                // Recupero l'organizzazione su IAM se esiste, altrimenti mando in errore
                SIUsrOrganizIam organiz = ambienteHelper.getSIUsrOrganizIam(idStrut);
                if (organiz == null) {
                    throw new ParerUserError(
                            "Attenzione: la struttura non risulta replicata in IAM, è dunque impossibile procedere con l'operazione");
                }

                // Cancello l'associazione
                ambienteHelper.removeEntity(enteConvenzOrg, true);

                // Inserisco quella nuova
                enteConvenzOrg = new SIOrgEnteConvenzOrg();
                enteConvenzOrg.setSiOrgEnteConvenz(ambienteHelper.findById(SIOrgEnteSiam.class, idEnteConvenz));
                enteConvenzOrg.setSiUsrOrganizIam(organiz);
                enteConvenzOrg.setDtIniVal(dtIniVal);
                enteConvenzOrg.setDtFineVal(dtFineVal);
                ambienteHelper.insertEntity(enteConvenzOrg, true);

                // Salvo in ORG_STRUT l'ente convenzionato associato se è quello più recente
                updateOrgStrutWithMostRecenteEnteConvenz(strut);

                // Salvo gli enti convenzionati interessati da allineare in IAM
                enteConvenzDaAllineaList.add(struttureEjb
                        .insertIamEnteConvenzDaAllinea(BigDecimal.valueOf(idEnteConvenzOld), nmEnteConvenzOld));
                enteConvenzDaAllineaList.add(struttureEjb.insertIamEnteConvenzDaAllinea(idEnteConvenz,
                        enteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam()));

            } // CASO 2: ho modificato SOLO le date
            else if (dtIniValOld.compareTo(dtIniVal) != 0 || dtFineValOld.compareTo(dtFineVal) != 0) {
                // Modifico le date
                enteConvenzOrg.setDtIniVal(dtIniVal);
                enteConvenzOrg.setDtFineVal(dtFineVal);

                // Salvo in ORG_STRUT l'ente convenzionato associato più recente
                updateOrgStrutWithMostRecenteEnteConvenz(strut);

                enteConvenzDaAllineaList.add(struttureEjb
                        .insertIamEnteConvenzDaAllinea(BigDecimal.valueOf(idEnteConvenzOld), nmEnteConvenzOld));
            }

            ambienteHelper.getEntityManager().flush();

            // Inserito per loggare la foto della struttura modificata
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA, idStrut, param.getNomePagina());

            return enteConvenzDaAllineaList;
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dell'ente convenzionato associato alla struttura ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            log.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamEnteConvenzDaAllinea deleteEnteConvenzOrgTx(LogParam param, BigDecimal idEnteConvenzOrg)
            throws ParerUserError {
        log.debug("Eseguo la delete dell'ente convenzionato");
        SIOrgEnteConvenzOrg siOrgEnteConvenzOrg = ambienteHelper.findById(SIOrgEnteConvenzOrg.class, idEnteConvenzOrg);
        OrgStrut strut = ambienteHelper.findById(OrgStrut.class,
                siOrgEnteConvenzOrg.getSiUsrOrganizIam().getIdOrganizApplic());
        BigDecimal idEnteConvenz = BigDecimal.valueOf(siOrgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam());
        String nmEnteConvenz = siOrgEnteConvenzOrg.getSiOrgEnteConvenz().getNmEnteSiam();

        final String flEliminaEnte = ambienteHelper.checkOrgVChkServFattByStrut(
                siOrgEnteConvenzOrg.getSiOrgEnteConvenz().getIdEnteSiam(), strut.getIdStrut(),
                siOrgEnteConvenzOrg.getDtIniVal());
        if (flEliminaEnte.equals("1")) {
            // Cancello l'associazione
            ambienteHelper.removeEntity(siOrgEnteConvenzOrg, true);
            // Salvo in ORG_STRUT l'ente convenzionato associato più recente tra quelli rimasti
            updateOrgStrutWithMostRecenteEnteConvenz(strut);
        } else {
            throw new ParerUserError(
                    "Sull'ente convenzionato sono presenti dei servizi erogati gi\u00E0 fatturati: non \u00E8 possibile eseguire l'eliminazione dell'associazione");
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                BigDecimal.valueOf(siOrgEnteConvenzOrg.getSiUsrOrganizIam().getIdOrganizApplic()),
                param.getNomePagina());

        IamEnteConvenzDaAllinea enteConvenzDaAllinea = struttureEjb.insertIamEnteConvenzDaAllinea(idEnteConvenz,
                nmEnteConvenz);
        return enteConvenzDaAllinea;
    }

    public boolean showInsertButton(long idUserIam) {
        return userHelper.checkEnteConvenzionatoAppart(idUserIam);
    }

    public boolean checkDateAmbiente(BigDecimal idAmbiente, Date dtIniVal, Date dtFineVal) {
        return ambienteHelper.checkDateAmbiente(idAmbiente, dtIniVal, dtFineVal);
    }

    public boolean isAmbienteModificato(BigDecimal idAmbiente, BigDecimal idEnte, Date dtIniVal, Date dtFinVal)
            throws ParerUserError {
        OrgEnte enteDB = ambienteHelper.findById(OrgEnte.class, idEnte);
        long idAmbienteDB = enteDB.getOrgAmbiente().getIdAmbiente();
        // Se ho modificato l'ambiente di appartenenza dell'ente, verifico id ente gestore e id ente conservatore
        if (idAmbienteDB != idAmbiente.longValue()) {
            OrgAmbiente ambienteNuovo = ambienteHelper.findById(OrgAmbiente.class, idAmbiente);
            // Determinano gli enti convenzionati delle strutture appartenenti all’ente sacer
            for (OrgStrut strutDB : enteDB.getOrgStruts()) {
                if (strutDB.getIdEnteConvenz() != null) {
                    OrgVRicEnteConvenzByEsterno ricEnteConvenz = ambienteHelper
                            .findViewById(OrgVRicEnteConvenzByEsterno.class, strutDB.getIdEnteConvenz());
                    if ((ricEnteConvenz.getIdEnteGestore().compareTo(ambienteNuovo.getIdEnteGestore()) != 0)
                            || (ricEnteConvenz.getIdEnteConserv().compareTo(ambienteNuovo.getIdEnteConserv()) != 0)) {
                        throw new ParerUserError(
                                "Modifica non consentita: esiste almeno una struttura appartenente a un ente produttore sul cui accordo valido non è definito lo stesso ente gestore o lo stesso ente conservatore definiti sull’ambiente selezionato");
                    }
                }
            }

            // Controllo l'intervallo di appartenenza se si sovrappone
            if (ambienteHelper.checkIntervalloSuStorico(idEnte, dtIniVal, dtFinVal)) {
                throw new ParerUserError(
                        "Le date di inizio e di fine validità di appartenenza all’ambiente si sovrappongono a quelle definite su una precedente appartenenza");
            }

            if (enteDB.getDtIniValAppartAmbiente().after(dtIniVal)) {
                throw new ParerUserError(
                        "Le date di appartenenza dell’ente all’ambiente sono inferiori a quelle indicate sull’attuale ambiente");
            }

            return true;
        }
        return false;
    }

    public OrgStoricoEnteAmbienteTableBean getOrgStoricoEnteAmbienteTableBean(BigDecimal idEnte) {
        OrgStoricoEnteAmbienteTableBean tableBean = new OrgStoricoEnteAmbienteTableBean();
        List<OrgStoricoEnteAmbiente> list = ambienteHelper.getOrgStoricoEnteAmbienteList(idEnte);
        try {
            for (OrgStoricoEnteAmbiente storico : list) {
                OrgStoricoEnteAmbienteRowBean rowBean = new OrgStoricoEnteAmbienteRowBean();
                rowBean = (OrgStoricoEnteAmbienteRowBean) Transform.entity2RowBean(storico);
                rowBean.setString("nm_ambiente", storico.getOrgAmbiente().getNmAmbiente());
                tableBean.add(rowBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tableBean;
    }

    public int getNumStoricizzazioni(BigDecimal idEnte) {
        OrgEnte ente = ambienteHelper.findById(OrgEnte.class, idEnte);
        return ente.getOrgStoricoEnteAmbientes().size();
    }

    public void saveParametriAmbiente(AplParamApplicTableBean parametriAmministrazioneAmbiente,
            AplParamApplicTableBean parametriConservazioneAmbiente, AplParamApplicTableBean parametriGestioneAmbiente,
            AplParamApplicTableBean parametriMultipliAmbiente, BigDecimal idAmbiente) {
        OrgAmbiente ambiente = struttureHelper.findById(OrgAmbiente.class, idAmbiente);
        gestioneParametriAmbiente(parametriAmministrazioneAmbiente, parametriConservazioneAmbiente,
                parametriGestioneAmbiente, parametriMultipliAmbiente, ambiente);
    }

    private void gestioneParametriAmbiente(AplParamApplicTableBean parametriAmministrazioneAmbiente,
            AplParamApplicTableBean parametriConservazioneAmbiente, AplParamApplicTableBean parametriGestioneAmbiente,
            AplParamApplicTableBean parametriMultipliAmbiente, OrgAmbiente ambiente) {
        // Gestione parametri amministrazione
        manageParametriPerAmbiente(parametriAmministrazioneAmbiente, "ds_valore_param_applic_ambiente_amm", ambiente);
        // Gestione parametri conservazione
        manageParametriPerAmbiente(parametriConservazioneAmbiente, "ds_valore_param_applic_ambiente_cons", ambiente);
        // Gestione parametri gestione
        manageParametriPerAmbiente(parametriGestioneAmbiente, "ds_valore_param_applic_ambiente_gest", ambiente);
        // Gestione parametri multipli
        manageParametriMultipliPerAmbiente(parametriMultipliAmbiente, ambiente);
    }
}
