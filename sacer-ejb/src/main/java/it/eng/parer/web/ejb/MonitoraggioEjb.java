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

package it.eng.parer.web.ejb;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerUserError;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulTableBean;
import it.eng.parer.viewEntity.MonVCntUdAnnulAmb;
import it.eng.parer.viewEntity.MonVCntUdAnnulEnte;
import it.eng.parer.viewEntity.MonVCntUdAnnulStrut;
import it.eng.parer.viewEntity.MonVLisUniDocDaAnnul;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.helper.MonitoraggioSinteticoHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "MonitoraggioEjb")
@LocalBean
public class MonitoraggioEjb {

    @Resource
    SessionContext ctx;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @EJB
    private ObjectStorageService objectStorageService;

    @EJB
    private RecBlbOracle recBlbOracle;

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioSinteticoHelper")
    private MonitoraggioSinteticoHelper monitSintHelper;

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    public enum fieldSetToPopulate {
	UNITA_DOC_VERSATE, DOC_AGGIUNTI, AGGIORNAMENTI_METADATI, FASCICOLI, UNITA_DOC_VERS_FALLITI,
	DOC_AGGIUNTI_VERS_FALLITI, AGGIORNAMENTI_METADATI_FALLITI, FASCICOLI_VERS_FALLITI,
	UNITA_DOC_ANNUL
    }

    // Costanti per i nomi delle viste
    private static final String VIEW_ID_STRUT = "id_strut";
    private static final String VIEW_ID_ENTE = "id_ente";
    private static final String VIEW_ID_AMBIENTE = "id_ambiente";
    private static final String VIEW_ID_USER_IAM = "id_user_iam";

    public MonitoraggioEjb() {
	//
    }

    private static final Logger log = LoggerFactory.getLogger(MonitoraggioEjb.class);

    /**
     * Dato un tablebean contenente una list di sessioni, ne modifica i flag "Verificato" e
     * "NonRisolubile"
     *
     * @param verificatiNonRisolubiliModificati lista verificati
     * @param idSessioneHS                      id sessione HS
     * @param idSessioneHSNoRis                 id sessione HR non risolta
     * @param tb                                bean AbstractBaseTable
     *
     * @return idSesModificate, il set di id delle sessioni modificate (flag "Verificato" o "Non
     *         risolubile"
     *
     * @throws ParerUserError errore generico
     */
    public Set<BigDecimal> aggiornaSessioni(Set<Integer> verificatiNonRisolubiliModificati,
	    Set<BigDecimal> idSessioneHS, Set<BigDecimal> idSessioneHSNoRis,
	    AbstractBaseTable<?> tb) throws ParerUserError {
	Set<BigDecimal> idSesModificate = new HashSet<>();
	try {
	    /* Scorro i flag (Verificato o Non risolubile) modificati */
	    for (Integer index : verificatiNonRisolubiliModificati) {
		BigDecimal idSesErr = tb.getRow(index).getBigDecimal("id_sessione_vers");
		idSesModificate.add(idSesErr);

		// Se ho impostato a "1" il flag "verificato"
		if (idSessioneHS.contains(idSesErr)) {
		    // Se ho impostato a "1" il flag "non risolubile"
		    if (idSessioneHSNoRis.contains(idSesErr)) {
			monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "1");
		    } else {
			monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "0");
		    }
		} else {
		    // Metti il flag flNonRisolubile a "null" visto che è l'unica opzione consentita
		    monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "0", null);
		}
	    }
	} catch (Exception e) {
	    /*
	     * Il rollback va settato visto che sono in modalità cmt in modo tale da gestire le
	     * eccezioni non di tipo RuntimeException (che vengono gestite automaticamente)
	     */
	    ctx.setRollbackOnly();
	    log.error(e.getMessage());
	    throw new ParerUserError(
		    "Attenzione: l'operazione non è stata eseguita perchè si è verificato un errore a runtime durante il salvataggio dei flag");
	}
	return idSesModificate;
    }

    public MonVLisUniDocDaAnnulTableBean getMonVLisUniDocDaAnnul(long idUtente,
	    MonitoraggioForm.FiltriDocumentiAnnullati filtri, int maxResult) throws EMFError {

	final BigDecimal idEnte = filtri.getId_ente().parse();
	MonitoraggioFiltriListaDocBean filtriListaDoc = new MonitoraggioFiltriListaDocBean();
	filtriListaDoc.setIdAmbiente(filtri.getId_ambiente().parse());
	filtriListaDoc.setIdEnte(idEnte);
	filtriListaDoc.setIdStrut(filtri.getId_strut().parse());
	filtriListaDoc.setIdTipoUnitaDoc(filtri.getId_tipo_unita_doc().parse());
	filtriListaDoc.setCdRegistroKeyUnitaDoc(filtri.getCd_registro_key_unita_doc().parse());
	filtriListaDoc.setAaKeyUnitaDoc(filtri.getAa_key_unita_doc().parse());
	filtriListaDoc.setAaKeyUnitaDocDa(filtri.getAa_key_unita_doc_da().parse());
	filtriListaDoc.setAaKeyUnitaDocA(filtri.getAa_key_unita_doc_a().parse());
	filtriListaDoc.setCdKeyUnitaDoc(filtri.getCd_key_unita_doc().parse());
	filtriListaDoc.setCdKeyUnitaDocDa(filtri.getCd_key_unita_doc_da().parse());
	filtriListaDoc.setCdKeyUnitaDocA(filtri.getCd_key_unita_doc_a().parse());

	filtriListaDoc.setGiornoAnnulDaValidato(filtri.getGiorno_annul_da_validato().parse());
	filtriListaDoc.setGiornoAnnulAValidato(filtri.getGiorno_annul_a_validato().parse());

	filtriListaDoc.setIdTipoDoc(filtri.getId_tipo_doc().parse());
	filtriListaDoc.setStatoDoc(filtri.getTi_stato_annul().parse());
	filtriListaDoc.setIdUserIam(new BigDecimal(idUtente));

	return monitoraggioHelper.getMonVLisUniDocDaAnnulViewBean(idUtente, filtriListaDoc,
		maxResult, list -> getMonVLisUniDocDaAnnulTableBeanFrom(list, idEnte));
    }

    private MonVLisUniDocDaAnnulTableBean getMonVLisUniDocDaAnnulTableBeanFrom(
	    List<MonVLisUniDocDaAnnul> listaDoc, BigDecimal idEnte) {
	MonVLisUniDocDaAnnulTableBean monTableBean = new MonVLisUniDocDaAnnulTableBean();
	try {
	    if (listaDoc != null && !listaDoc.isEmpty()) {
		monTableBean = (MonVLisUniDocDaAnnulTableBean) Transform
			.entities2TableBean(listaDoc);
	    }
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}

	/*
	 * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche
	 * con ambiente ed ente
	 */
	for (MonVLisUniDocDaAnnulRowBean row : monTableBean) {
	    if (idEnte == null) {
		row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
			+ (row.getNmStrut() != null ? row.getNmStrut() : ""));
	    }
	    if (!row.getTiDoc().equals("PRINCIPALE")) {
		row.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
	    }
	}
	return monTableBean;
    }

    /**
     * Conta il numero di componenti con errore di versamento. Se la configurazione del backend di
     * staging è object storage ottengo il valore da lì. Se, però, i versamenti falliti sono ancora
     * su blob li conto dal blob. Non può accadere che siano in entrambi i luoghi.
     *
     * @param idFileSessioneKo id file sesssione
     *
     * @return numero di componenti con errore di versamento
     */
    public long contaComponentiErroreVersamento(long idFileSessioneKo) {

	long nComponenti = 0L;
	boolean isVersamentoFallitoOnOs = objectStorageService
		.isComponenteFallitoOnOs(idFileSessioneKo);
	if (isVersamentoFallitoOnOs) {
	    nComponenti += 1;
	}

	nComponenti += recBlbOracle.contaBlobErroriVers(idFileSessioneKo);
	return nComponenti;

    }

    /**
     * Salva lo stream del componente versato in errore. Il componente può essere presente sul
     * bucket di staging (se il backend configurato è object storage) oppure sul database.
     * <strong>Nota bene:</strong> Essendo la configurazione del backend di staging a livello di
     * applicazione, il componente si può trovare <em>ancora</em> sul DB nonostante il parametro
     * dica il contrario.
     *
     * @param idFileSessioneKo id file sessione
     * @param out              stream su cui viene scritto il componente.
     *
     * @return true OutputStream aggiornato con la copia dell'oggetto / false OutputStream non
     *         aggiornato, file non restituito per errore
     */
    public boolean salvaStreamComponenteDaErroreVersamento(long idFileSessioneKo,
	    OutputStream out) {

	boolean isVersamentoFallitoOnOs = objectStorageService
		.isComponenteFallitoOnOs(idFileSessioneKo);
	//
	if (isVersamentoFallitoOnOs) {
	    //
	    return objectStorageService.getObjectComponenteInStaging(idFileSessioneKo, out);
	} else { // file è su DB
	    return recBlbOracle.recuperaBlobCompSuStream(idFileSessioneKo, out,
		    RecBlbOracle.TabellaBlob.ERRORI_VERS, null);
	}
    }

    public LinkedHashMap<String, BaseRowInterface> ricercaMonitoraggioStruttura(long idUtente,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura) {
	LinkedHashMap<String, BaseRowInterface> map = new LinkedHashMap<>();

	// Calcola versamenti
	BaseRow versamenti = calcolaRiepilogoVersamenti(idUtente, idAmbiente, idEnte, idStruttura);
	map.put("RiepilogoVersamentiMonitoraggioStruttura", versamenti);

	// Calcola versamenti falliti
	BaseRow versamentiFalliti = calcolaRiepilogoVersamentiFalliti(idUtente, idAmbiente, idEnte,
		idStruttura);
	map.put("RiepilogoVersamentiFallitiMonitoraggioStruttura", versamentiFalliti);

	// Calcola annullamenti
	BaseRow annullamenti = calcolaRiepilogoAnnullamenti(idUtente, idAmbiente, idEnte,
		idStruttura);
	map.put("RiepilogoAnnullamentiMonitoraggioStruttura", annullamenti);

	return map;
    }

    private Object buildQueryChk(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
	    BigDecimal idStrut, fieldSetToPopulate fieldSetToBuild) {
	return buildQueryChk(idUtente, idAmbiente, idEnte, idStrut, fieldSetToBuild, false);
    }

    private Object buildQueryChk(long idUtente, BigDecimal idAmbiente, BigDecimal idEnte,
	    BigDecimal idStrut, fieldSetToPopulate fieldSetToBuild, boolean isBefore30) {

	String view = null;
	String paramString = null;
	BigDecimal param1 = null;
	Long param2 = null;
	String select = null;
	String group_by = null;

	if (idStrut != null) {
	    switch (fieldSetToBuild) {
	    case UNITA_DOC_VERSATE:
		if (isBefore30) {
		    view = "MonVCntUdStrutB30";
		    paramString = VIEW_ID_STRUT
			    + " = :param1 AND monVCntUdStrutB30Id.tiStatoUd = 'TOTALE'";
		} else {
		    view = "MonVCntUdStrut";
		    paramString = VIEW_ID_STRUT
			    + " = :param1 AND monVCntUdStrutId.tiStatoUd = 'TOTALE'";
		}
		break;
	    case DOC_AGGIUNTI:
		if (isBefore30) {
		    view = "MonVCntDocStrutB30";
		    paramString = VIEW_ID_STRUT
			    + " = :param1 AND monVCntDocStrutB30Id.tiStatoDoc = 'TOTALE'";
		} else {
		    view = "MonVCntDocStrut";
		    paramString = VIEW_ID_STRUT
			    + " = :param1 AND monVCntDocStrutId.tiStatoDoc = 'TOTALE'";
		}
		break;
	    case AGGIORNAMENTI_METADATI:
		view = "MonVCntUpdUdStrut";
		paramString = VIEW_ID_STRUT + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUpdUdStrutId.tiStatoUpdUd = 'TOTALE'";
		param2 = idUtente;
		break;
	    case FASCICOLI:
		view = "MonVCntFascByStrut";
		paramString = VIEW_ID_STRUT + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntFascByStrutId.tiStatoFasc = 'TOTALE'";
		param2 = idUtente;
		break;
	    case UNITA_DOC_VERS_FALLITI:
		view = "MonVCntUdNonversStrut";
		paramString = VIEW_ID_STRUT
			+ " = :param1 AND monVCntUdNonversStrutId.tiStatoUdNonvers = 'NO_VERIF'";
		break;
	    case DOC_AGGIUNTI_VERS_FALLITI:
		view = "MonVCntDocNonversStrut";
		paramString = VIEW_ID_STRUT
			+ " = :param1 AND monVCntDocNonversStrutId.tiStatoDocNonvers = 'NO_VERIF'";
		break;
	    case AGGIORNAMENTI_METADATI_FALLITI:
		view = "MonVCntUdUpdKoStrut";
		paramString = VIEW_ID_STRUT + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUdUpdKoStrutId.tiStatoSesUpdKo = 'NON_VERIFICATO'";
		param2 = idUtente;
		break;
	    case FASCICOLI_VERS_FALLITI:
		view = "MonVCntFascKoByStrut";
		paramString = "view.monVCntFascKoByStrutId.idStrut = :param1 AND view.monVCntFascKoByStrutId.idUserIam = :param2 AND view.monVCntFascKoByStrutId.tiStatoFascKo = 'NON_VERIFICATO'";
		select = "view.monVCntFascKoByStrutId.idStrut, view.monVCntFascKoByStrutId.idUserIam, view.monVCntFascKoByStrutId.tiStatoFascKo, sum(view.niFascKo) as ni_fasc_ko ";
		group_by = " view.monVCntFascKoByStrutId.idStrut, view.monVCntFascKoByStrutId.idUserIam, view.monVCntFascKoByStrutId.tiStatoFascKo ";
		param1 = idStrut;
		param2 = idUtente;
		break;
	    case UNITA_DOC_ANNUL:
		view = "MonVCntUdAnnulStrut";
		// Rimuovi il prefisso "view." dalla paramString per essere coerente con gli altri
		// casi
		paramString = "view.monVCntUdAnnulStrutId.idStrut = :param1";
		// Mantieni il prefisso "view." solo nelle clausole select e group_by
		select = "view.monVCntUdAnnulStrutId.idStrut, view.monVCntUdAnnulStrutId.tiDtCreazione, sum(view.niAnnul) as ni_annul ";
		group_by = " view.monVCntUdAnnulStrutId.idStrut, view.monVCntUdAnnulStrutId.tiDtCreazione ";
		break;
	    default:
		throw new IllegalArgumentException(
			"Errore inaspettato nei parametri di calcolo riepilogo struttura");
	    }
	    param1 = idStrut;
	} else if (idEnte != null) {
	    switch (fieldSetToBuild) {
	    case UNITA_DOC_VERSATE:
		if (isBefore30) {
		    view = "MonVCntUdEnteB30";
		    paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntUdEnteB30Id.tiStatoUd = 'TOTALE'";
		} else {
		    view = "MonVCntUdEnte";
		    paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntUdEnteId.tiStatoUd = 'TOTALE'";
		}
		break;
	    case DOC_AGGIUNTI:
		if (isBefore30) {
		    view = "MonVCntDocEnteB30";
		    paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntDocEnteB30Id.tiStatoDoc = 'TOTALE'";
		} else {
		    view = "MonVCntDocEnte";
		    paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntDocEnteId.tiStatoDoc = 'TOTALE'";
		}
		break;
	    case AGGIORNAMENTI_METADATI:
		view = "MonVCntUpdUdEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUpdUdEnteId.tiStatoUpdUd = 'TOTALE'";
		break;
	    case FASCICOLI:
		view = "MonVCntFascByEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntFascByEnteId.tiStatoFasc = 'TOTALE'";
		break;
	    case UNITA_DOC_VERS_FALLITI:
		view = "MonVCntUdNonversEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUdNonversEnteId.tiStatoUdNonvers = 'NO_VERIF'";
		break;
	    case DOC_AGGIUNTI_VERS_FALLITI:
		view = "MonVCntDocNonversEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntDocNonversEnteId.tiStatoDocNonvers = 'NO_VERIF'";
		break;
	    case AGGIORNAMENTI_METADATI_FALLITI:
		view = "MonVCntUdUpdKoEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUdUpdKoEnteId.tiStatoSesUpdKo = 'NON_VERIFICATO'";
		break;
	    case FASCICOLI_VERS_FALLITI:
		view = "MonVCntFascKoByEnte";
		paramString = VIEW_ID_ENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntFascKoByEnteId.tiStatoFascKo = 'NON_VERIFICATO'";
		break;
	    case UNITA_DOC_ANNUL:
		view = "MonVCntUdAnnulEnte";
		// Correggi anche per Ente
		paramString = "view.monVCntUdAnnulEnteId.idEnte = :param1 AND view.monVCntUdAnnulEnteId.idUserIam = :param2";
		select = "view.monVCntUdAnnulEnteId.idEnte, view.monVCntUdAnnulEnteId.tiDtCreazione, sum(view.niAnnul) as ni_annul";
		group_by = "view.monVCntUdAnnulEnteId.idEnte, view.monVCntUdAnnulEnteId.tiDtCreazione";
		break;
	    default:
		throw new IllegalArgumentException(
			"Errore inaspettato nei parametri di calcolo riepilogo struttura");
	    }
	    param1 = idEnte;
	    param2 = idUtente;
	} else if (idAmbiente != null) {
	    switch (fieldSetToBuild) {
	    case UNITA_DOC_VERSATE:
		if (isBefore30) {
		    view = "MonVCntUdAmbB30";
		    paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntUdAmbB30Id.tiStatoUd = 'TOTALE'";
		} else {
		    view = "MonVCntUdAmb";
		    paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntUdAmbId.tiStatoUd = 'TOTALE'";
		}
		break;
	    case DOC_AGGIUNTI:
		if (isBefore30) {
		    view = "MonVCntDocAmbB30";
		    paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntDocAmbB30Id.tiStatoDoc = 'TOTALE'";
		} else {
		    view = "MonVCntDocAmb";
		    paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			    + " = :param2 AND monVCntDocAmbId.tiStatoDoc = 'TOTALE'";
		}
		break;
	    case AGGIORNAMENTI_METADATI:
		view = "MonVCntUpdUdAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUpdUdAmbId.tiStatoUpdUd = 'TOTALE'";
		break;
	    case FASCICOLI:
		view = "MonVCntFascByAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntFascByAmbId.tiStatoFasc = 'TOTALE'";
		break;
	    case UNITA_DOC_VERS_FALLITI:
		view = "MonVCntUdNonversAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUdNonversAmbId.tiStatoUdNonvers = 'NO_VERIF'";
		break;
	    case DOC_AGGIUNTI_VERS_FALLITI:
		view = "MonVCntDocNonversAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntDocNonversAmbId.tiStatoDocNonvers = 'NO_VERIF'";
		break;
	    case AGGIORNAMENTI_METADATI_FALLITI:
		view = "MonVCntUdUpdKoAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntUdUpdKoAmbId.tiStatoSesUpdKo = 'NON_VERIFICATO'";
		break;
	    case FASCICOLI_VERS_FALLITI:
		view = "MonVCntFascKoByAmb";
		paramString = VIEW_ID_AMBIENTE + " = :param1 AND " + VIEW_ID_USER_IAM
			+ " = :param2 AND monVCntFascKoByAmbId.tiStatoFascKo = 'NON_VERIFICATO'";
		break;
	    case UNITA_DOC_ANNUL:
		view = "MonVCntUdAnnulAmb";
		// Correggi anche per Ambiente
		paramString = "view.monVCntUdAnnulAmbId.idAmbiente = :param1 AND view.monVCntUdAnnulAmbId.idUserIam = :param2";
		select = "view.monVCntUdAnnulAmbId.idAmbiente, view.monVCntUdAnnulAmbId.tiDtCreazione, sum(view.niAnnul) as ni_annul";
		group_by = "view.monVCntUdAnnulAmbId.idAmbiente, view.monVCntUdAnnulAmbId.tiDtCreazione";
		break;
	    default:
		throw new IllegalArgumentException(
			"Errore inaspettato nei parametri di calcolo riepilogo struttura");
	    }
	    param1 = idAmbiente;
	    param2 = idUtente;
	} else {
	    throw new IllegalArgumentException(
		    "Errore inaspettato nei parametri di calcolo riepilogo struttura");
	}

	Object obj = monitoraggioHelper.getMonVCnt(view, paramString, param1, param2, select,
		group_by);

	return obj;
    }

    private BaseRow calcolaRiepilogoVersamenti(long idUtente, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStruttura) {
	BaseRow row = new BaseRow();

	// Unità documentarie versate oggi
	Object objUd = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.UNITA_DOC_VERSATE, false);
	row.setBigDecimal("ni_ud_corr", getValue(objUd, "ni_ud_corr", "getNiUd"));
	row.setBigDecimal("ni_ud_30gg", getValue(objUd, "ni_ud_30gg", "getNiUd"));

	// Unità documentarie versate prima di 30 giorni
	Object objUdB30gg = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.UNITA_DOC_VERSATE, true);
	row.setBigDecimal("ni_ud_b30gg", getValue(objUdB30gg, "ni_ud_b30gg", "getNiUd"));

	// Documenti aggiunti oggi
	Object objDoc = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.DOC_AGGIUNTI, false);
	row.setBigDecimal("ni_doc_corr", getValue(objDoc, "ni_doc_corr", "getNiDoc"));
	row.setBigDecimal("ni_doc_30gg", getValue(objDoc, "ni_doc_30gg", "getNiDoc"));

	// Documenti aggiunti prima di 30 giorni
	Object objDocB30gg = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.DOC_AGGIUNTI, true);
	row.setBigDecimal("ni_doc_b30gg", getValue(objDocB30gg, "ni_doc_b30gg", "getNiDoc"));

	// Aggiornamenti metadati (senza logica B30)
	Object objUpd = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.AGGIORNAMENTI_METADATI);
	row.setBigDecimal("ni_upd_corr", getValue(objUpd, "ni_upd_corr", "getNiUpdUd"));
	row.setBigDecimal("ni_upd_30gg", getValue(objUpd, "ni_upd_30gg", "getNiUpdUd"));
	row.setBigDecimal("ni_upd_b30gg", getValue(objUpd, "ni_upd_b30gg", "getNiUpdUd"));

	// Fascicoli (senza logica B30)
	Object objFasc = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.FASCICOLI);
	row.setBigDecimal("ni_fasc_corr", getValue(objFasc, "ni_fasc_corr", "getNiFasc"));
	row.setBigDecimal("ni_fasc_30gg", getValue(objFasc, "ni_fasc_30gg", "getNiFasc"));
	row.setBigDecimal("ni_fasc_b30gg", getValue(objFasc, "ni_fasc_b30gg", "getNiFasc"));

	return row;
    }

    private BaseRow calcolaRiepilogoVersamentiFalliti(long idUtente, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStruttura) {
	BaseRow row = new BaseRow();

	// Unità documentarie fallite
	Object objUdNonVers = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.UNITA_DOC_VERS_FALLITI);
	row.setBigDecimal("ni_ud_nonvers",
		getValue(objUdNonVers, "ni_ud_nonvers_noverif", "getNiUdNonvers"));

	// Documenti falliti
	Object objDocNonVers = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.DOC_AGGIUNTI_VERS_FALLITI);
	row.setBigDecimal("ni_doc_nonvers",
		getValue(objDocNonVers, "ni_doc_nonvers_noverif", "getNiDocNonvers"));

	// Aggiornamenti metadati falliti
	Object objUpdNonVers = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.AGGIORNAMENTI_METADATI_FALLITI);
	row.setBigDecimal("ni_upd_nonvers",
		getValue(objUpdNonVers, "ni_upd_nonvers_noverif", "getNiUpdUdKo"));

	// Fascicoli falliti
	Object objFascNonVers = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.FASCICOLI_VERS_FALLITI);
	row.setBigDecimal("ni_fasc_nonvers",
		getValue(objFascNonVers, "ni_fasc_nonvers_noverif", "getNiFascKo"));

	return row;
    }

    private BaseRow calcolaRiepilogoAnnullamenti(long idUtente, BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStruttura) {
	BaseRow row = new BaseRow();

	// Unità documentarie annullate
	Object objUdAnnul = buildQueryChk(idUtente, idAmbiente, idEnte, idStruttura,
		fieldSetToPopulate.UNITA_DOC_ANNUL);
	row.setBigDecimal("ni_ud_annul_corr",
		getValue(objUdAnnul, "ni_ud_annul_corr", "getNiAnnul"));
	row.setBigDecimal("ni_ud_annul_30gg",
		getValue(objUdAnnul, "ni_ud_annul_30gg", "getNiAnnul"));
	row.setBigDecimal("ni_ud_annul_b30gg",
		getValue(objUdAnnul, "ni_ud_annul_b30gg", "getNiAnnul"));

	return row;
    }

    private BigDecimal getValue(Object obj, String fieldName, String method) {
	try {
	    if (obj instanceof List) {
		List<?> list = (List<?>) obj;
		if (!list.isEmpty()) {
		    Object firstItem = list.get(0);

		    // Se è un Object[] (risultato di query aggregata)
		    if (firstItem instanceof Object[]) {
			// Gestione per query aggregate (Object[])
			if (fieldName.equals("ni_fasc_nonvers_noverif")) {
			    for (Object item : list) {
				Object[] row = (Object[]) item;
				BigDecimal value = (BigDecimal) row[3];
				return value != null ? value : BigDecimal.ZERO;
			    }
			} else {
			    for (Object item : list) {
				Object[] row = (Object[]) item;
				if (row.length >= 3) {
				    // row[0] = id, row[1] = tiDtCreazione, row[2] = somma
				    String tiDtCreazione = (String) row[1];
				    BigDecimal value = (BigDecimal) row[2];

				    if ((fieldName.contains("_corr")
					    && "OGGI".equals(tiDtCreazione))
					    || (fieldName.contains("_30gg")
						    && "30gg".equals(tiDtCreazione))
					    || (fieldName.contains("_b30gg")
						    && "before30gg".equals(tiDtCreazione))) {
					return value != null ? value : BigDecimal.ZERO;
				    }
				}
			    }
			}
			return BigDecimal.ZERO;
		    } else {
			// Gestione per entità JPA complete (logica esistente)
			Object entityToUse = null;
			for (Object entity : list) {
			    try {
				if (fieldName.equals("ni_ud_b30gg")
					|| fieldName.equals("ni_doc_b30gg")) {
				    entityToUse = entity;
				} else {
				    java.lang.reflect.Method getterId = entity.getClass().getMethod(
					    "get" + entity.getClass().getSimpleName() + "Id");
				    Object id = getterId.invoke(entity);
				    Object value = null;
				    try {
					java.lang.reflect.Method getter = id.getClass()
						.getMethod("getTiDtCreazione");
					value = getter.invoke(id);
				    } catch (NoSuchMethodException e) {
					// Campo non presente
				    }

				    if (value != null) {
					if ((fieldName.contains("_corr") && "OGGI".equals(value))
						|| (fieldName.contains("_30gg")
							&& "30gg".equals(value))
						|| (fieldName.contains("_b30gg")
							&& "before30gg".equals(value))) {
					    entityToUse = entity;
					    break;
					} else if (fieldName.contains("_nonvers")
						&& "NON_VERIFICATO".equals(value)) {
					    entityToUse = entity;
					    break;
					}
				    } else {
					entityToUse = entity;
				    }
				}
			    } catch (Exception e) {
				log.warn("Errore durante il controllo dell'entità: {}",
					e.getMessage());
			    }
			}

			if (entityToUse != null) {
			    try {
				java.lang.reflect.Method getter = entityToUse.getClass()
					.getMethod(method);
				Object value = getter.invoke(entityToUse);
				return value instanceof BigDecimal ? (BigDecimal) value
					: BigDecimal.ZERO;
			    } catch (NoSuchMethodException | IllegalAccessException
				    | InvocationTargetException e) {
				log.warn(
					"Campo '{}' non trovato nell'entità {}, ritorno BigDecimal.ZERO",
					fieldName, entityToUse.getClass().getSimpleName());
				return BigDecimal.ZERO;
			    }
			}
		    }
		}
	    }
	} catch (Exception e) {
	    log.error("Errore nell'estrazione del campo {}: {}", fieldName, e.getMessage());
	}

	log.warn("Impossibile estrarre il campo '{}', ritorno BigDecimal.ZERO", fieldName);
	return BigDecimal.ZERO;
    }
}