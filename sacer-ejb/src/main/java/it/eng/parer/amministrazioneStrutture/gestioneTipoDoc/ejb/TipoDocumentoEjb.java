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

package it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper.SottoStruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.helper.TipoDocumentoHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.DecTipoStrutDocAmmesso;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.helper.ModelliSerieHelper;
import it.eng.parer.serie.helper.TipoSerieHelper;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoDoc;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei tipi di documento
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneTipoDoc}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({
	TransactionInterceptor.class })
public class TipoDocumentoEjb {

    private static final Logger logger = LoggerFactory.getLogger(TipoDocumentoEjb.class);
    @Resource
    private SessionContext context;
    @EJB
    private TipoDocumentoHelper helper;
    @EJB
    private TipoSerieHelper tipoSerieHelper;
    @EJB
    private DatiSpecificiHelper datiSpecHelper;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private CriteriRaggruppamentoEjb critEjb;
    @EJB
    private CriteriRaggrHelper crHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocHelper;
    @EJB
    private ModelliSerieHelper modelliSerieHelper;
    @EJB
    private SottoStruttureHelper sottoStruttureHelper;

    public DecTipoDocTableBean getTipiDocAbilitati(long idUtente, BigDecimal idStruttura) {
	DecTipoDocTableBean table = new DecTipoDocTableBean();
	List<DecTipoDoc> list = helper.getTipiDocAbilitati(idUtente, idStruttura);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (DecTipoDocTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		String msg = "Errore durante il recupero della lista tipi documento abilitati "
			+ ExceptionUtils.getRootCauseMessage(ex);
		logger.error(msg, ex);
	    }
	}
	return table;
    }

    public DecTipoDocTableBean getTipiDocPrincipaliAbilitati(long idUtente,
	    BigDecimal idStruttura) {
	DecTipoDocTableBean table = new DecTipoDocTableBean();
	List<DecTipoDoc> list = helper.getTipiDocPrincipaliAbilitati(idUtente, idStruttura);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (DecTipoDocTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		String msg = "Errore durante il recupero della lista tipi documento abilitati "
			+ ExceptionUtils.getRootCauseMessage(ex);
		logger.error(msg, ex);
	    }
	}
	return table;
    }

    public DecTipoDocRowBean getDecTipoDocRowBean(BigDecimal idTipoDoc, BigDecimal idStrut) {
	DecTipoDocRowBean tipoDocRowBean = getDecTipoDoc(idTipoDoc, null, idStrut);
	return tipoDocRowBean;
    }

    public DecTipoDocRowBean getDecTipoDocRowBean(String nmTipoDoc, BigDecimal idStrut) {
	DecTipoDocRowBean tipoDocRowBean = getDecTipoDoc(BigDecimal.ZERO, nmTipoDoc, idStrut);
	return tipoDocRowBean;
    }

    private DecTipoDocRowBean getDecTipoDoc(BigDecimal idTipoDoc, String nmTipoDoc,
	    BigDecimal idStrut) {

	DecTipoDocRowBean tipoDocRowBean = null;
	DecTipoDoc tipoDoc = null;

	if (idTipoDoc == BigDecimal.ZERO && nmTipoDoc != null) {
	    tipoDoc = helper.getDecTipoDocByName(nmTipoDoc, idStrut);
	}
	if (nmTipoDoc == null && idTipoDoc != BigDecimal.ZERO) {
	    tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);
	}

	if (tipoDoc != null) {
	    try {
		tipoDocRowBean = (DecTipoDocRowBean) Transform.entity2RowBean(tipoDoc);
	    } catch (Exception e) {
		logger.error(e.getMessage(), e);
	    }
	}

	return tipoDocRowBean;
    }

    /**
     * Ritorna il tableBean dei tipi documento per i parametri passati
     *
     * @param idStrut   id della struttura
     * @param flPrinc   flag principale
     * @param dtSoppres data soppressione
     *
     * @return DecTipoDocTableBean entity
     */
    public DecTipoDocTableBean getDecTipoDocTableBean(BigDecimal idStrut, boolean flPrinc,
	    Date dtSoppres) {
	DecTipoDocTableBean tipoDocTableBean = new DecTipoDocTableBean();
	List<DecTipoDoc> tipoDocList = helper.retrieveDecTipoDocList(idStrut, flPrinc, dtSoppres,
		false);
	if (tipoDocList != null && !tipoDocList.isEmpty()) {
	    try {
		tipoDocTableBean = (DecTipoDocTableBean) Transform.entities2TableBean(tipoDocList);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei tipi documento "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero dei tipi documento");
	    }
	}
	return tipoDocTableBean;
    }

    /**
     * Ritorna il tableBean dei tipi documento per i parametri passati
     *
     * @param idStrut       id della struttura
     * @param flPrinc       flag principale
     * @param isFilterValid true/false
     *
     * @return DecTipoDocTableBean entity
     */
    public DecTipoDocTableBean getDecTipoDocTableBean(BigDecimal idStrut, boolean flPrinc,
	    boolean isFilterValid) {
	DecTipoDocTableBean tipoDocTableBean = new DecTipoDocTableBean();
	List<DecTipoDoc> tipoDocList = helper.retrieveDecTipoDocList(idStrut, flPrinc, null,
		isFilterValid);
	if (tipoDocList != null && !tipoDocList.isEmpty()) {
	    try {
		tipoDocTableBean = (DecTipoDocTableBean) Transform.entities2TableBean(tipoDocList);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei tipi documento "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero dei tipi documento");
	    }
	}
	return tipoDocTableBean;
    }

    public DecTipoDocTableBean getDecTipoDocTableBean(BigDecimal idStrut, Date dtSoppres) {
	DecTipoDocTableBean tipoDocTableBean = new DecTipoDocTableBean();
	List<DecTipoDoc> tipoDocList = helper.retrieveDecTipoDocList(idStrut, false, dtSoppres,
		false);
	if (tipoDocList != null && !tipoDocList.isEmpty()) {
	    try {
		tipoDocTableBean = (DecTipoDocTableBean) Transform.entities2TableBean(tipoDocList);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei tipi documento "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero dei tipi documento");
	    }
	}
	return tipoDocTableBean;
    }

    /**
     * Ritorna il tableBean dei tipi documento per i parametri passati
     *
     * @param idStrut       id della struttura
     * @param isFilterValid true ricerca solo i tipi validi alla data odierna
     *
     * @return DecTipoDocTableBean entity
     */
    public DecTipoDocTableBean getDecTipoDocTableBean(BigDecimal idStrut, boolean isFilterValid) {

	DecTipoDocTableBean tipoDocTableBean = new DecTipoDocTableBean();
	List<DecTipoDoc> list = helper.retrieveDecTipoDocList(idStrut, false, null, isFilterValid);

	// Creo il campo relativo al criterio di raggruppamento standard
	Calendar calendar = GregorianCalendar.getInstance();
	int anno = calendar.get(Calendar.YEAR);

	try {
	    if (!list.isEmpty()) {
		for (DecTipoDoc tipo : list) {
		    DecTipoDocRowBean tipoDocRow = (DecTipoDocRowBean) Transform
			    .entity2RowBean(tipo);
		    if (tipo.getDtIstituz().before(new Date())
			    && tipo.getDtSoppres().after(new Date())) {
			tipoDocRow.setObject("fl_attivo", "1");
		    } else {
			tipoDocRow.setObject("fl_attivo", "0");
		    }

		    DecXsdDatiSpec lastXsd = datiSpecHelper.getLastDecXsdDatiSpecForTipoDoc(
			    tipo.getOrgStrut().getIdStrut(), tipo.getIdTipoDoc());
		    if (lastXsd != null) {
			tipoDocRow.setObject("cd_versione_xsd", lastXsd.getCdVersioneXsd());
		    }

		    String flagStandard = crHelper.getCriterioStandardPerTipoDatoAnno(
			    tipo.getIdTipoDoc(), TipoDato.TIPO_DOC);
		    tipoDocRow.setString("flag_criterio_standard", flagStandard);

		    tipoDocTableBean.add(tipoDocRow);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new IllegalStateException("Errore durante il recupero dei tipi documento");
	}

	return tipoDocTableBean;
    }

    public void insertDecTipoDoc(LogParam param, DecTipoDocRowBean tipoDocRowBean,
	    String criterioAutomTipoDoc) throws ParerUserError {
	TipoDocumentoEjb me = context.getBusinessObject(TipoDocumentoEjb.class);
	IamOrganizDaReplic replic = me.saveDecTipoDoc(param, null, tipoDocRowBean,
		StruttureEjb.TipoOper.INS, criterioAutomTipoDoc);
	if (replic != null) {
	    struttureEjb.replicateToIam(replic);
	}
    }

    public void updateDecTipoDoc(LogParam param, BigDecimal idTipoDoc,
	    DecTipoDocRowBean tipoDocRowBean, String criterioAutomTipoDoc) throws ParerUserError {
	TipoDocumentoEjb me = context.getBusinessObject(TipoDocumentoEjb.class);
	IamOrganizDaReplic replic = me.saveDecTipoDoc(param, idTipoDoc, tipoDocRowBean,
		StruttureEjb.TipoOper.MOD, criterioAutomTipoDoc);
	if (replic != null) {
	    struttureEjb.replicateToIam(replic);
	}
    }

    /* METODO NUOVO */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveDecTipoDoc(LogParam param, BigDecimal idTipoDoc,
	    DecTipoDocRowBean tipoDocRowBean, StruttureEjb.TipoOper tipoOper,
	    String criterioAutomTipoDoc) throws ParerUserError {
	OrgStrut struttura = helper.findById(OrgStrut.class, tipoDocRowBean.getIdStrut());
	DecTipoDoc tipoDoc = new DecTipoDoc();
	ApplEnum.TiOperReplic tiOper = null;
	boolean modificatiNomeDescrizione = false;

	/* INSERIMENTO TIPO DOCUMENTO */
	if (tipoOper.name().equals((StruttureEjb.TipoOper.INS.name()))) {
	    if (struttura.getDecTipoDocs() == null) {
		struttura.setDecTipoDocs(new ArrayList<DecTipoDoc>());
	    }

	    if (helper.getDecTipoDocByName((tipoDocRowBean.getNmTipoDoc()),
		    tipoDocRowBean.getIdStrut()) != null) {
		throw new ParerUserError(
			"Tipo Documento gi\u00E0 esistente all'interno della struttura</br>");
	    }

	    tipoDoc.setNmTipoDoc(tipoDocRowBean.getNmTipoDoc());
	    tipoDoc.setDsTipoDoc(tipoDocRowBean.getDsTipoDoc());
	    tipoDoc.setDsPeriodicitaVers(tipoDocRowBean.getDsPeriodicitaVers());
	    tipoDoc.setDtIstituz(tipoDocRowBean.getDtIstituz());
	    tipoDoc.setDtSoppres(tipoDocRowBean.getDtSoppres());
	    tipoDoc.setFlTipoDocPrincipale(tipoDocRowBean.getFlTipoDocPrincipale());
	    tipoDoc.setDlNoteTipoDoc(tipoDocRowBean.getDlNoteTipoDoc());
	    tipoDoc.setOrgStrut(struttura);

	    helper.insertEntity(tipoDoc, true);
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
		    new BigDecimal(tipoDoc.getIdTipoDoc()), param.getNomePagina());
	    struttura.getDecTipoDocs().add(tipoDoc);

	    tiOper = ApplEnum.TiOperReplic.MOD;
	    modificatiNomeDescrizione = true;
	} else /* MODIFICA TIPO DOCUMENTO */ if (tipoOper.name()
		.equals((StruttureEjb.TipoOper.MOD.name()))) {
	    DecTipoDoc dbTipoDoc = helper.getDecTipoDocByName(tipoDocRowBean.getNmTipoDoc(),
		    tipoDocRowBean.getIdStrut());

	    if (dbTipoDoc != null && dbTipoDoc.getIdTipoDoc() != idTipoDoc.longValue()) {
		throw new ParerUserError(
			"Nome Tipo Documento gi\u00E0 associato a questa struttura all'interno del database</br>");
	    }

	    /* Controllo se sono stati modificati nome e/o descrizione */
	    tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);

	    if (!tipoDoc.getNmTipoDoc().equals(tipoDocRowBean.getNmTipoDoc())
		    || !tipoDoc.getDsTipoDoc().equals(tipoDocRowBean.getDsTipoDoc())) {
		modificatiNomeDescrizione = true;
	    }

	    tipoDoc.setNmTipoDoc(tipoDocRowBean.getNmTipoDoc());
	    tipoDoc.setDsTipoDoc(tipoDocRowBean.getDsTipoDoc());
	    tipoDoc.setDsPeriodicitaVers(tipoDocRowBean.getDsPeriodicitaVers());
	    tipoDoc.setDtIstituz(tipoDocRowBean.getDtIstituz());
	    tipoDoc.setDtSoppres(tipoDocRowBean.getDtSoppres());
	    tipoDoc.setFlTipoDocPrincipale(tipoDocRowBean.getFlTipoDocPrincipale());
	    tipoDoc.setDlNoteTipoDoc(tipoDocRowBean.getDlNoteTipoDoc());

	    helper.getEntityManager().flush();
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO,
		    new BigDecimal(tipoDoc.getIdTipoDoc()), param.getNomePagina());

	    // Se il tipo doc non \u00E8 principale, le eventuali regole vanno cancellate
	    if (tipoDocRowBean.getFlTipoDocPrincipale() == null
		    || (tipoDocRowBean.getFlTipoDocPrincipale() != null
			    && tipoDocRowBean.getFlTipoDocPrincipale().equals("0"))) {
		sottoStruttureHelper.deleteRegole(idTipoDoc);
	    }
	    tiOper = ApplEnum.TiOperReplic.MOD;
	}

	// Se il flag relativo al criterio di raggruppamento \u00E8 stato impostato,
	// procedo alla creazione automatica
	if (criterioAutomTipoDoc != null && criterioAutomTipoDoc.equals("1")) {
	    DecVCreaCritRaggrTipoDoc creaCritTipoDoc = crHelper
		    .getDecVCreaCritRaggrTipoDoc(tipoDoc.getIdTipoDoc());
	    if (crHelper.existNomeCriterio(creaCritTipoDoc.getNmCriterioRaggr(),
		    new BigDecimal(struttura.getIdStrut()))) {
		throw new ParerUserError(
			"Attenzione: non \u00E8 possibile terminare l'operazione in quanto si richiedere la creazione "
				+ "in automatico di un criterio di raggruppamento il cui nome, per questa struttura, \u00E8 gi\u00E0 presente nel DB");
	    }
	    DecCriterioRaggr criterioSalvato = critEjb
		    .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoDoc(creaCritTipoDoc);
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
		    new BigDecimal(criterioSalvato.getIdCriterioRaggr()), param.getNomePagina());

	}
	/*
	 * Il Tipo Documento va replicato solo se appartiere ad una struttura non appartenente ad un
	 * ente di tipo template e sono stati modificati il nome e/o la descrizione
	 */
	IamOrganizDaReplic replic = null;
	if (modificatiNomeDescrizione && (struttura.getOrgEnte().getTipoDefTemplateEnte()
		.equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
		|| struttura.getOrgEnte().getTipoDefTemplateEnte()
			.equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))) {
	    replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, tiOper);
	}
	return replic;
    }

    public void deleteDecTipoDoc(LogParam param, long idTipoDoc) throws ParerUserError {
	TipoDocumentoEjb me = context.getBusinessObject(TipoDocumentoEjb.class);
	IamOrganizDaReplic replic = me.deleteTipoDocPuntuale(param, idTipoDoc);
	if (replic != null) {
	    struttureEjb.replicateToIam(replic);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamOrganizDaReplic deleteTipoDocFromStruttura(LogParam param, long idTipoDoc)
	    throws ParerUserError {
	return eseguiDeleteTipoDoc(param, idTipoDoc, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteTipoDocPuntuale(LogParam param, long idTipoDoc)
	    throws ParerUserError {
	return eseguiDeleteTipoDoc(param, idTipoDoc, false);
    }

    /**
     * Business method per la cancellazione di un tipo documento di una determinata struttura. Il
     * metodo esegue le seguenti istruzioni: 1) Controlla i legami con Tipologia Serie e Unità
     * documentarie; 2) Controllo i legami con criteri di raggruppamento e, se può, li elimina 3)
     * Esegue la cancellazione del Tipo Doc con cascade 4) Scrive il record dell'organizzazione da
     * replicare
     *
     * @param idTipoDoc             id tipo documento
     * @param isFromDeleteStruttura true/false
     *
     * @return il record dell'organizzazione da replicare
     *
     * @throws ParerUserError errore generico
     */
    private IamOrganizDaReplic eseguiDeleteTipoDoc(LogParam param, long idTipoDoc,
	    boolean isFromDeleteStruttura) throws ParerUserError {
	DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);
	String nmTipoDoc = tipoDoc.getNmTipoDoc();
	long idStrut = tipoDoc.getOrgStrut().getIdStrut();

	boolean existsRelationsWithTipiSerie = tipoSerieHelper
		.existsRelationsWithTipiSerie(tipoDoc.getIdTipoDoc(), Constants.TipoDato.TIPO_DOC);
	if (existsRelationsWithTipiSerie) {
	    throw new ParerUserError(
		    "Impossibile eliminare il tipo documento: esiste almeno una tipologia di serie che utilizza il tipo documento nelle regole di filtraggio</br>");
	}

	boolean existsRelationsWithUnitaDoc = unitaDocHelper
		.existsRelationsWithUnitaDoc(tipoDoc.getIdTipoDoc(), Constants.TipoDato.TIPO_DOC);
	if (existsRelationsWithUnitaDoc) {
	    throw new ParerUserError(
		    "Impossibile eliminare il tipo documento: esiste almeno un'unità documentaria versata con tale tipologia</br>");
	}

	boolean existsRelationsWithModello = modelliSerieHelper
		.existsRelationsWithModello(tipoDoc.getIdTipoDoc(), Constants.TipoDato.TIPO_DOC);
	if (existsRelationsWithModello) {
	    throw new ParerUserError(
		    "Il tipo di documento è definito come filtro su almeno un modello</br>");
	}

	List<ObjectsToLogBefore> listaBefore = sacerLogEjb.logBefore(
		param.getTransactionLogContext(), param.getNomeApplicazione(),
		param.getNomeUtente(), param.getNomeAzione(),
		SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, new BigDecimal(idTipoDoc),
		param.getNomePagina());
	List<ObjectsToLogBefore> listaBeforeDeletion = ObjectsToLogBefore
		.filterObjectsForDeletion(listaBefore);
	List<ObjectsToLogBefore> listaBeforeModifying = ObjectsToLogBefore
		.filterObjectsForModifying(listaBefore);
	/* In questo caso gli oggetti vengono fotografati prima perché spariranno completamente */
	sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(),
		param.getNomeUtente(), param.getNomeAzione(), listaBeforeDeletion,
		param.getNomePagina());

	List<DecCriterioFiltroMultiplo> criteriAssociati = struttureHelper
		.getRelationsWithCriteriRaggruppamento(tipoDoc.getIdTipoDoc(),
			Constants.TipoDato.TIPO_DOC);
	if (!criteriAssociati.isEmpty()) {
	    List<Long> criteriRaggrDaEliminare = new ArrayList<>();
	    for (DecCriterioFiltroMultiplo criterioAssociato : criteriAssociati) {
		long idCriterioRaggr = criterioAssociato.getDecCriterioRaggr().getIdCriterioRaggr();
		boolean existsRelationsWithElenchiVolumiForCriterioRaggruppamento = struttureHelper
			.existsRelationsWithElenchiVolumiForCriterioRaggruppamento(idCriterioRaggr);
		if (existsRelationsWithElenchiVolumiForCriterioRaggruppamento) {
		    throw new ParerUserError(
			    "Il criterio è collegato a dei volumi od a degli elenchi di versamento</br>");
		}
		criteriRaggrDaEliminare.add(idCriterioRaggr);
	    }
	    // Se sono arrivato fin qui, quindi non è scattata l'eccezione, posso eliminare i
	    // criteri
	    crHelper.bulkDeleteCriteriRaggr(criteriRaggrDaEliminare);
	}

	sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		param.getNomeUtente(), param.getNomeAzione(),
		SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, new BigDecimal(idTipoDoc),
		param.getNomePagina());
	helper.removeEntity(tipoDoc, true);
	/* Foto dopo eliminazione di eventuali disassociazioni */
	sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(),
		param.getNomeUtente(), param.getNomeAzione(), listaBeforeModifying,
		param.getNomePagina());
	OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
	/*
	 * Il Tipo Documento va replicato solo se appartiene ad una struttura che non appartenente
	 * ad un ente di tipo template
	 */
	IamOrganizDaReplic replic = null;
	if ((struttura.getOrgEnte().getTipoDefTemplateEnte()
		.equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
		|| struttura.getOrgEnte().getTipoDefTemplateEnte()
			.equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))
		&& !isFromDeleteStruttura) {
	    replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura,
		    ApplEnum.TiOperReplic.MOD);
	}
	logger.info("Cancellazione tipo documento " + nmTipoDoc + " della struttura " + idStrut
		+ " avvenuta con successo!");
	return replic;
    }

    public DecTipoDocTableBean getDocumentiPrincipali(BigDecimal idStrut) {
	List<DecTipoDoc> tipiDoc = helper.retrieveDecTipoDocList(idStrut, true, null, false);
	DecTipoDocTableBean tableBean = new DecTipoDocTableBean();
	try {
	    if (tipiDoc != null && !tipiDoc.isEmpty()) {
		tableBean = (DecTipoDocTableBean) Transform.entities2TableBean(tipiDoc);
	    }
	} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		| IllegalAccessException | IllegalArgumentException
		| InvocationTargetException ex) {
	    logger.error("Errore durante il recupero dei documenti "
		    + ExceptionUtils.getRootCauseMessage(ex), ex);
	}
	return tableBean;
    }

    public DecTipoStrutDocAmmessoTableBean getDecTipoStrutDocAmmessoTableBeanByIdTipoDoc(
	    BigDecimal idTipoDoc) {
	DecTipoStrutDocAmmessoTableBean tipoStrutDocAmmessoTableBean = new DecTipoStrutDocAmmessoTableBean();
	DecTipoStrutDocAmmessoRowBean tipoStrutDocAmmessoRowBean = new DecTipoStrutDocAmmessoRowBean();
	List<DecTipoStrutDocAmmesso> tipoStrutDocAmmessoList = helper
		.getDecTipoStrutDocAmmessoListByIdTipoDoc(idTipoDoc.longValue());
	if (tipoStrutDocAmmessoList != null && !tipoStrutDocAmmessoList.isEmpty()) {
	    try {
		for (DecTipoStrutDocAmmesso tipoStrutDocAmmesso : tipoStrutDocAmmessoList) {
		    tipoStrutDocAmmessoRowBean = (DecTipoStrutDocAmmessoRowBean) Transform
			    .entity2RowBean(tipoStrutDocAmmesso);
		    tipoStrutDocAmmessoRowBean.setString("nm_tipo_strut_doc",
			    tipoStrutDocAmmesso.getDecTipoStrutDoc().getNmTipoStrutDoc());
		    if (tipoStrutDocAmmesso.getDecTipoStrutDoc().getDtSoppres().after(new Date())) {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "1");
		    } else {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "0");
		    }
		    tipoStrutDocAmmessoTableBean.add(tipoStrutDocAmmessoRowBean);
		}
	    } catch (Exception ex) {
		logger.error("Errore durante il recupero dei tipi struttura documento ammessi "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return tipoStrutDocAmmessoTableBean;
    }

    public DecTipoStrutDocAmmessoTableBean getDecTipoStrutDocAmmessoTableBeanByIdTipoStrutDoc(
	    BigDecimal idTipoStrutDoc) {
	DecTipoStrutDocAmmessoTableBean tipoStrutDocAmmessoTableBean = new DecTipoStrutDocAmmessoTableBean();
	List<DecTipoStrutDocAmmesso> tipoStrutDocAmmessoList = helper
		.getDecTipoStrutDocAmmessoListByIdTipoStrutDoc(idTipoStrutDoc.longValue());
	if (tipoStrutDocAmmessoList != null && !tipoStrutDocAmmessoList.isEmpty()) {
	    try {
		for (DecTipoStrutDocAmmesso tipoStrutDocAmmesso : tipoStrutDocAmmessoList) {
		    DecTipoStrutDocAmmessoRowBean tipoStrutDocAmmessoRowBean = (DecTipoStrutDocAmmessoRowBean) Transform
			    .entity2RowBean(tipoStrutDocAmmesso);
		    tipoStrutDocAmmessoRowBean.setString("nm_tipo_doc",
			    tipoStrutDocAmmesso.getDecTipoDoc().getNmTipoDoc());
		    tipoStrutDocAmmessoRowBean.setString("ds_tipo_doc",
			    tipoStrutDocAmmesso.getDecTipoDoc().getDsTipoDoc());
		    if (tipoStrutDocAmmesso.getDecTipoDoc().getDtSoppres().after(new Date())) {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "1");
		    } else {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "0");
		    }
		    tipoStrutDocAmmessoTableBean.add(tipoStrutDocAmmessoRowBean);
		}
	    } catch (Exception ex) {
		logger.error("Errore durante il recupero dei tipi struttura documento ammessi "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return tipoStrutDocAmmessoTableBean;
    }

    public DecTipoStrutDocTableBean getDecTipoStrutDocTableBeanByIdStrut(BigDecimal idStrut,
	    Date data) {
	DecTipoStrutDocTableBean tipoStrutDocTableBean = new DecTipoStrutDocTableBean();
	List<DecTipoStrutDoc> tipoStrutDocList = helper
		.getDecTipoStrutDocListByIdStrut(idStrut.longValue(), data);
	if (tipoStrutDocList != null && !tipoStrutDocList.isEmpty()) {
	    try {
		tipoStrutDocTableBean = (DecTipoStrutDocTableBean) Transform
			.entities2TableBean(tipoStrutDocList);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei tipo struttura documento "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return tipoStrutDocTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoStrutDocAmmesso(LogParam param, BigDecimal idTipoDoc,
	    BigDecimal idTipoStrutDoc) throws ParerUserError {
	if (helper.getDecTipoStrutDocAmmessoByParentId(idTipoDoc.longValue(),
		idTipoStrutDoc.longValue()) != null) {
	    throw new ParerUserError("Associazione gi\u00E0 esistente</br>");
	}
	try {
	    DecTipoStrutDocAmmesso tipoStrutDocAmmesso = new DecTipoStrutDocAmmesso();
	    // Recupero il Tipo Doc
	    DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc.longValue());
	    // Recupero il Tipo Strut Doc
	    DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class,
		    idTipoStrutDoc.longValue());
	    tipoStrutDocAmmesso.setDecTipoDoc(tipoDoc);
	    tipoStrutDocAmmesso.setDecTipoStrutDoc(tipoStrutDoc);

	    helper.insertEntity(tipoStrutDocAmmesso, true);
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
		    param.getNomePagina());
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new ParerUserError("Errore inatteso durante il salvataggio del record");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoStrutDocAmmessoFromTipoDoc(LogParam param,
	    BigDecimal idTipoStrutDocAmmesso, BigDecimal idTipoDoc, BigDecimal idTipoStrutDoc)
	    throws ParerUserError {
	updateDecTipoStrutDocAmmesso(param, idTipoStrutDocAmmesso, idTipoDoc, idTipoStrutDoc,
		"Nome tipo struttura documento ammesso");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoStrutDocAmmessoFromTipoStrutDoc(LogParam param,
	    BigDecimal idTipoStrutDocAmmesso, BigDecimal idTipoDoc, BigDecimal idTipoStrutDoc)
	    throws ParerUserError {
	updateDecTipoStrutDocAmmesso(param, idTipoStrutDocAmmesso, idTipoDoc, idTipoStrutDoc,
		"Nome tipo documento ammesso");
    }

    private void updateDecTipoStrutDocAmmesso(LogParam param, BigDecimal idTipoStrutDocAmmesso,
	    BigDecimal idTipoDoc, BigDecimal idTipoStrutDoc, String origin) throws ParerUserError {
	DecTipoStrutDocAmmesso dbTipoStrutDocAmmesso = helper.getDecTipoStrutDocAmmessoByParentId(
		idTipoDoc.longValue(), idTipoStrutDoc.longValue());
	// verifico quale dei due tipi stavo modificando: se l'id resta null vuol dire che non ci
	// sono state modifiche
	if (dbTipoStrutDocAmmesso != null) {
	    /* Verifico se esiste gi\u00E0 la nuova relazione creata */
	    if (dbTipoStrutDocAmmesso.getIdTipoStrutDocAmmesso() != idTipoStrutDocAmmesso
		    .longValue()) {
		throw new ParerUserError(origin
			+ " gi\u00E0 associato a questo documento all'interno del database</br>");
	    }
	} else {
	    dbTipoStrutDocAmmesso = helper.findById(DecTipoStrutDocAmmesso.class,
		    idTipoStrutDocAmmesso);
	}
	boolean update = false;
	if (dbTipoStrutDocAmmesso.getDecTipoDoc().getIdTipoDoc() != idTipoDoc.longValue()
		|| dbTipoStrutDocAmmesso.getDecTipoStrutDoc().getIdTipoStrutDoc() != idTipoStrutDoc
			.longValue()) {
	    update = true;
	}

	if (update) {
	    // Recupero il Tipo Strut Doc
	    DecTipoStrutDoc tipoStrutDoc = helper.findById(DecTipoStrutDoc.class, idTipoStrutDoc);
	    // Recupero il Tipo Doc
	    DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, idTipoDoc);
	    // Se non ho lanciato l'eccezione prima allora ho preso il record corretto
	    dbTipoStrutDocAmmesso.setDecTipoDoc(tipoDoc);
	    dbTipoStrutDocAmmesso.setDecTipoStrutDoc(tipoStrutDoc);

	    helper.getEntityManager().flush();
	    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		    param.getNomeUtente(), param.getNomeAzione(),
		    SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc,
		    param.getNomePagina());
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoStrutDocAmmesso(LogParam param, BigDecimal idTipoStrutDocAmmesso,
	    BigDecimal idTipoDoc) {
	DecTipoStrutDocAmmesso tipoStrutDocAmmesso = helper.findById(DecTipoStrutDocAmmesso.class,
		idTipoStrutDocAmmesso);
	helper.removeEntity(tipoStrutDocAmmesso, true);
	sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
		param.getNomeUtente(), param.getNomeAzione(),
		SacerLogConstants.TIPO_OGGETTO_TIPO_DOCUMENTO, idTipoDoc, param.getNomePagina());
    }

    public DecTipoStrutDocAmmessoRowBean getDecTipoStrutDocAmmessoRowBean(BigDecimal idTipoDoc,
	    BigDecimal idTipoStrutDoc) {
	DecTipoStrutDocAmmesso tipoStrutDocAmmesso = helper.getDecTipoStrutDocAmmessoByParentId(
		idTipoDoc.longValue(), idTipoStrutDoc.longValue());
	DecTipoStrutDocAmmessoRowBean row = new DecTipoStrutDocAmmessoRowBean();
	try {
	    row = (DecTipoStrutDocAmmessoRowBean) Transform.entity2RowBean(tipoStrutDocAmmesso);
	} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		| IllegalAccessException | IllegalArgumentException
		| InvocationTargetException ex) {
	    logger.error("Errore durante il recupero del tipo struttura documento ammessa"
		    + ExceptionUtils.getRootCauseMessage(ex), ex);
	}
	return row;
    }

    public DecTipoStrutDocAmmessoTableBean getDecTipoDocAmmessoTableBeanByIdTipoStrutDoc(
	    BigDecimal idTipoStrutDoc) {
	DecTipoStrutDocAmmessoTableBean tipoStrutDocAmmessoTableBean = new DecTipoStrutDocAmmessoTableBean();
	List<DecTipoStrutDocAmmesso> tipoStrutDocAmmessoList = helper
		.getDecTipoStrutDocAmmessoListByIdTipoStrutDoc(idTipoStrutDoc.longValue());
	if (tipoStrutDocAmmessoList != null && !tipoStrutDocAmmessoList.isEmpty()) {
	    try {
		for (DecTipoStrutDocAmmesso tipoStrutDocAmmesso : tipoStrutDocAmmessoList) {
		    DecTipoStrutDocAmmessoRowBean tipoStrutDocAmmessoRowBean = (DecTipoStrutDocAmmessoRowBean) Transform
			    .entity2RowBean(tipoStrutDocAmmesso);
		    tipoStrutDocAmmessoRowBean.setString("nm_tipo_doc",
			    tipoStrutDocAmmesso.getDecTipoDoc().getNmTipoDoc());
		    if (tipoStrutDocAmmesso.getDecTipoStrutDoc().getDtSoppres().after(new Date())) {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "1");
		    } else {
			tipoStrutDocAmmessoRowBean.setString("dt_soppres", "0");
		    }
		    tipoStrutDocAmmessoTableBean.add(tipoStrutDocAmmessoRowBean);
		}
	    } catch (Exception ex) {
		logger.error("Errore durante il recupero dei tipi documento ammessi "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return tipoStrutDocAmmessoTableBean;
    }
}
