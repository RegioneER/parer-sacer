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

package it.eng.parer.elencoVersamento.ejb;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.OpTypeEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class IndiceElencoVersJobEjb {

    Logger log = LoggerFactory.getLogger(IndiceElencoVersJobEjb.class);
    @EJB
    private IndiceElencoVersXsdEjb indiceEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private GenericHelper genericHelper;

    @Resource
    private SessionContext context;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildIndex(LogJob logJob)
	    throws ParerNoResultException, NoSuchAlgorithmException, IOException {
	log.info("Creazione automatica indici...");
	List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

	for (OrgStrut struttura : strutture) {
	    manageStrut(struttura.getIdStrut(), logJob.getIdLogJob());
	}
	jobHelper.writeLogJob(OpTypeEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(),
		OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    private void manageStrut(long idStruttura, long idLogJob)
	    throws ParerNoResultException, NoSuchAlgorithmException, IOException {
	log.debug("manageStrut");
	BigDecimal idStrut = new BigDecimal(idStruttura);
	/*
	 * Determino gli elenchi appartenenti alla struttura corrente, con stato DA_CHIUDERE
	 * (tabella ELV_ELENCO_VERS_DA_ELAB)
	 */
	List<Long> elenchiDaChiudere = elencoHelper.retrieveIdElenchiDaElaborare(idStrut,
		ElencoStatusEnum.DA_CHIUDERE.name());
	log.info("struttura id {}: trovati {} elenchi DA_CHIUDERE da processare", idStrut,
		elenchiDaChiudere.size());

	IndiceElencoVersJobEjb indiceElencoVersEjbRef1 = context
		.getBusinessObject(IndiceElencoVersJobEjb.class);
	for (Long idElenchi : elenchiDaChiudere) {
	    indiceElencoVersEjbRef1.manageIndexAtomic(idElenchi, idStruttura, idLogJob);
	}

	/*
	 * Determino gli elenchi appartenenti alla struttura corrente, con stato CHIUSO (tabella
	 * ELV_ELENCO_VERS_DA_ELAB), il cui elenco preveda tipo validazione = NO_FIRMA o NO_INDICE e
	 * modalità di validazione = AUTOMATICA. Se i valori nell'elenco sono nulli leggo i valori
	 * dal criterio di raggruppamento.
	 */
	final String numMaxElenchiDaValidare = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_ELENCHI_DA_VALIDARE);
	List<Long> elenchiDaValidare = new ArrayList<>();
	if (Integer.valueOf(numMaxElenchiDaValidare) > 0) {
	    elenchiDaValidare = elencoHelper.retrieveIdElenchiDaValidare(idStrut,
		    ElencoStatusEnum.CHIUSO.name(), numMaxElenchiDaValidare);
	}
	log.info("struttura id {}: trovati {} elenchi CHIUSO da validare", idStrut,
		elenchiDaValidare.size());

	IndiceElencoVersJobEjb indiceElencoVersEjbRef2 = context
		.getBusinessObject(IndiceElencoVersJobEjb.class);
	for (Long idElenchi : elenchiDaValidare) {
	    indiceElencoVersEjbRef2.manageValidAtomic(idElenchi, idStruttura, idLogJob);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageIndexAtomic(long idElenco, long idStruttura, long idLogJob)
	    throws ParerNoResultException, NoSuchAlgorithmException, IOException {
	log.debug("manageIndexAtomic - idElenco {} idStruttura {}", idElenco, idStruttura);
	LogJob logJob = elencoHelper.retrieveLogJobByid(idLogJob);
	OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
	ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
	elencoHelper.lockElenco(elenco);
	elencoHelper.writeLogElencoVers(elenco, struttura, OpTypeEnum.CREA_INDICE_ELENCO.name(),
		logJob);
	manageIndex(elenco, struttura, logJob);
    }

    private void manageIndex(ElvElencoVer elenco, OrgStrut struttura, LogJob logJob)
	    throws ParerNoResultException, NoSuchAlgorithmException, IOException {
	// EVO#16486
	// Determina le unità doc appartenenti all'elenco corrente (quelle versate, quelle per
	// aggiunta documento e
	// quelle per aggiornamento metadati)
	log.debug("manageIndex");
	// determina nome ente e struttura normalizzati e non
	OrgEnte ente = struttura.getOrgEnte();
	String nomeStruttura = struttura.getNmStrut();
	String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
	String nomeEnte = ente.getNmEnte();
	String nomeEnteNorm = ente.getCdEnteNormaliz();
	// Calcolo e persisto lo urn dell'elenco */
	calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
	// end EVO#16486
	buildIndexFile(elenco);
	// v) assegno all'elenco stato = CHIUSO e lo lascio nella coda degli elenchi da elaborare
	// assegnando stato =
	// CHIUSO
	elenco.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());
	ElvElencoVersDaElab elencoVersDaElab = elencoHelper.retrieveElencoInQueue(elenco);
	elencoVersDaElab.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());
	elencoVersDaElab.setTsStatoElenco(new Date());
	// vi) assegno ad ogni unità doc appartenente all'elenco stato = IN_ELENCO_CHIUSO
	elencoHelper.setUdsStatus(elenco, UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
	// vii) assegno ad ogni documento appartenente all'elenco stato = IN_ELENCO_CHIUSO
	elencoHelper.setDocsStatus(elenco, UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
	// viii) assegno ad ogni aggiornamento per unità doc appartenente all'elenco stato =
	// IN_ELENCO_CHIUSO
	elencoHelper.setUpdsStatus(elenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
	elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
		OpTypeEnum.CHIUSURA_ELENCO.name(), logJob);
	evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
		"CREAZIONE_INDICE_ELENCO_VERS", null, ElvStatoElencoVer.TiStatoElenco.CHIUSO, null);
    }

    private void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura,
	    String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm) {
	log.debug("calcolaUrnElenco");
	// sistema (new URN)
	String sistema = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	// salvo ORIGINALE
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	urnHelper.salvaUrnElvElencoVers(elenco,
		MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnte, nomeStruttura,
			sdf.format(elenco.getDtCreazioneElenco()),
			Long.toString(elenco.getIdElencoVers())),
		TiUrnElenco.ORIGINALE);
	// salvo NORMALIZZATO
	urnHelper.salvaUrnElvElencoVers(elenco,
		MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnteNorm,
			nomeStrutturaNorm, sdf.format(elenco.getDtCreazioneElenco()),
			Long.toString(elenco.getIdElencoVers())),
		TiUrnElenco.NORMALIZZATO);
    }

    private void buildIndexFile(ElvElencoVer elenco)
	    throws ParerNoResultException, NoSuchAlgorithmException, IOException {
	byte[] indexFile = null;
	log.debug("buildIndexFile");
	// creo il file indice_conservazione.xml
	log.info("creazione indice per elenco id '{}' appartenente alla struttura '{}'",
		elenco.getIdElencoVers(), elenco.getOrgStrut().getIdStrut());
	indexFile = indiceEjb.createIndex(elenco, false);
	// registro il file indice_conservazione.xml (in ELV_FILE_ELENCO_VERS)
	ElvFileElencoVer elvFileElencoVers = elencoHelper.storeFileIntoElenco(elenco, indexFile,
		FileTypeEnum.INDICE.name());
	// EVO#16486
	/* Calcolo e persisto lo urn dell'indice dell'elenco */
	CSVersatore csv = new CSVersatore();
	csv.setStruttura(elenco.getOrgStrut().getNmStrut());
	csv.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
	csv.setAmbiente(elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	// sistema (new URN)
	String sistemaConservazione = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	csv.setSistemaConservazione(sistemaConservazione);
	// calcolo parte urn ORIGINALE
	String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(csv);
	// calcolo parte urn NORMALIZZATO
	String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true,
		Costanti.UrnFormatter.VERS_FMT_STRING);
	// salvo ORIGINALE
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	log.debug("buildIndexFile - salvo URN");
	urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
		MessaggiWSFormat.formattaUrnElencoIndice(tmpUrn,
			sdf.format(elenco.getDtCreazioneElenco()),
			Long.toString(elenco.getIdElencoVers())),
		ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
	// salvo NORMALIZZATO
	urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
		MessaggiWSFormat.formattaUrnElencoIndice(tmpUrnNorm,
			sdf.format(elenco.getDtCreazioneElenco()),
			Long.toString(elenco.getIdElencoVers())),
		ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
	// end EVO#16486

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageValidAtomic(long idElenco, long idStruttura, long idLogJob) {
	log.debug("manageValidAtomic");
	LogJob logJob = elencoHelper.retrieveLogJobByid(idLogJob);
	OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
	ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
	elencoHelper.lockElenco(elenco);

	// Controllo se almeno una unità doc appartenente all'elenco e' annullata
	boolean annullata = elencoHelper.checkUdAnnullataByElenco(elenco);
	if (!annullata) { // ud non annullata
	    manageValid(elenco);
	    elencoHelper.writeLogElencoVers(elenco, struttura, OpTypeEnum.VALIDAZIONE_ELENCO.name(),
		    logJob);
	    evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
		    "VALIDA_INDICE_ELENCO_VERS",
		    "Validazione indice elenco in cui non sono presenti unità documentarie annullate",
		    ElvStatoElencoVer.TiStatoElenco.VALIDATO, null);
	} else {
	    log.debug(
		    "manageValidAtomic - chiamo evEjb.deleteElenco(idElenco) per elenco {} e struttura {}",
		    idElenco, idStruttura);
	    evEjb.deleteElenco(idElenco);
	}
    }

    private void manageValid(ElvElencoVer elenco) {
	// iv) aggiorno l'elenco valorizzando la data di firma, il tipo di validazione e la modalità
	// con i valori
	// definiti dall'elenco.
	log.debug("manageValid");
	elenco.setDtFirmaIndice(new Date());
	/* TODO 19304 DA TOGLIERE elenchiversamento1.15 pag. 27 */
	// se i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento
	if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
	    elenco.setTiValidElenco(
		    TiValidElenco.valueOf(elenco.getDecCriterioRaggr().getTiValidElenco().name()));
	    elenco.setTiModValidElenco(TiModValidElenco
		    .valueOf(elenco.getDecCriterioRaggr().getTiModValidElenco().name()));
	}
	// v) assegno all'elenco stato = VALIDATO e lo lascio nella coda degli elenchi da elaborare
	// assegnando stato =
	// validato
	elenco.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
	ElvElencoVersDaElab elencoVersDaElab = elencoHelper.retrieveElencoInQueue(elenco);
	elencoVersDaElab.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
	elencoVersDaElab.setTsStatoElenco(new Date());
	// vi) assegno ad ogni unità doc appartenente all'elenco stato = IN_ELENCO_VALIDATO
	elencoHelper.setUdsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
	// vii) assegno ad ogni documento appartenente all'elenco stato = IN_ELENCO_VALIDATO
	elencoHelper.setDocsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
	// viii) assegno ad ogni aggiornamento per unità doc appartenente all'elenco stato =
	// IN_ELENCO_VALIDATO
	elencoHelper.setUpdsStatus(elenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO);
    }
}
