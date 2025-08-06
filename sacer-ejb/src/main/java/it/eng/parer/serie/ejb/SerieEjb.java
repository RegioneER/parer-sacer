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

package it.eng.parer.serie.ejb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.persistence.LockTimeoutException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.ValidationException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

import it.eng.parer.common.signature.Digest;
import it.eng.parer.crypto.model.ParerTST;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecCampoInpUd;
import it.eng.parer.entity.DecCampoOutSelUd;
import it.eng.parer.entity.DecFiltroSelUd;
import it.eng.parer.entity.DecFiltroSelUdDato;
import it.eng.parer.entity.DecNotaTipoSerie;
import it.eng.parer.entity.DecOutSelUd;
import it.eng.parer.entity.DecTipoNotaSerie;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieCreataAutom;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.SerConsistVerSerie;
import it.eng.parer.entity.SerContenutoVerSerie;
import it.eng.parer.entity.SerErrContenutoVerSerie;
import it.eng.parer.entity.SerErrFileInput;
import it.eng.parer.entity.SerFileInputVerSerie;
import it.eng.parer.entity.SerFileVerSerie;
import it.eng.parer.entity.SerIxVolVerSerie;
import it.eng.parer.entity.SerLacunaConsistVerSerie;
import it.eng.parer.entity.SerNotaVerSerie;
import it.eng.parer.entity.SerQueryContenutoVerSerie;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.entity.SerStatoSerie;
import it.eng.parer.entity.SerStatoVerSerie;
import it.eng.parer.entity.SerUdErrFileInput;
import it.eng.parer.entity.SerUdNonVersErr;
import it.eng.parer.entity.SerUrnFileVerSerie;
import it.eng.parer.entity.SerUrnIxVolVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.entity.VrsUnitaDocNonVer;
import it.eng.parer.entity.constraint.SerUrnFileVerSerie.TiUrnFileVerSerie;
import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie.TiUrnIxVolVerSerie;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAip.ejb.ElaborazioneRigaIndiceAipDaElab;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.serie.dto.CampiInputBean;
import it.eng.parer.serie.dto.CreazioneSerieBean;
import it.eng.parer.serie.dto.IntervalliSerieAutomBean;
import it.eng.parer.serie.dto.RegistroTipoUnitaDoc;
import it.eng.parer.serie.dto.ResultInputSerieBean;
import it.eng.parer.serie.dto.RicercaSerieBean;
import it.eng.parer.serie.dto.RicercaUdAppartBean;
import it.eng.parer.serie.dto.SerieAutomBean;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.slite.gen.tablebean.DecTipoSerieUdTableBean;
import it.eng.parer.slite.gen.tablebean.SerLacunaConsistVerSerieTableBean;
import it.eng.parer.slite.gen.tablebean.SerSerieRowBean;
import it.eng.parer.slite.gen.viewbean.SerVLisErrContenSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisErrFileSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisNotaSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisSerDaValidareRowBean;
import it.eng.parer.slite.gen.viewbean.SerVLisSerDaValidareTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisStatoSerieRowBean;
import it.eng.parer.slite.gen.viewbean.SerVLisStatoSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartVolSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdErrFileInputTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdNoversTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisVerSeriePrecTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisVolSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVRicConsistSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVRicConsistSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdUsrRowBean;
import it.eng.parer.slite.gen.viewbean.SerVRicSerieUdUsrTableBean;
import it.eng.parer.slite.gen.viewbean.SerVVisConsistSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVVisContenutoSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVVisErrContenSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVVisErrFileSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVVisSerieUdRowBean;
import it.eng.parer.slite.gen.viewbean.SerVVisVolVerSerieUdRowBean;
import it.eng.parer.viewEntity.ResultVCalcoloSerieUd;
import it.eng.parer.viewEntity.SerVBucoNumerazioneUd;
import it.eng.parer.viewEntity.SerVCalcDimSerie;
import it.eng.parer.viewEntity.SerVChkConservazioneUd;
import it.eng.parer.viewEntity.SerVChkDefUdNovers;
import it.eng.parer.viewEntity.SerVChkDefUdNoversBuco;
import it.eng.parer.viewEntity.SerVChkNotaObblig;
import it.eng.parer.viewEntity.SerVChkSerieUdDaRigenera;
import it.eng.parer.viewEntity.SerVJobContenutoBloccato;
import it.eng.parer.viewEntity.SerVJobVerSerieBloccato;
import it.eng.parer.viewEntity.SerVLisErrContenSerieUd;
import it.eng.parer.viewEntity.SerVLisErrFileSerieUd;
import it.eng.parer.viewEntity.SerVLisNotaSerie;
import it.eng.parer.viewEntity.SerVLisSerDaValidare;
import it.eng.parer.viewEntity.SerVLisStatoSerie;
import it.eng.parer.viewEntity.SerVLisUdAppartSerie;
import it.eng.parer.viewEntity.SerVLisUdAppartVolSerie;
import it.eng.parer.viewEntity.SerVLisUdErrFileInput;
import it.eng.parer.viewEntity.SerVLisUdNovers;
import it.eng.parer.viewEntity.SerVLisVerSeriePrec;
import it.eng.parer.viewEntity.SerVLisVolSerieUd;
import it.eng.parer.viewEntity.SerVRicConsistSerieUd;
import it.eng.parer.viewEntity.SerVRicSerieUd;
import it.eng.parer.viewEntity.SerVRicSerieUdUsr;
import it.eng.parer.viewEntity.SerVSelUdNovers;
import it.eng.parer.viewEntity.SerVSelUdNoversBuco;
import it.eng.parer.viewEntity.SerVVisConsistSerieUd;
import it.eng.parer.viewEntity.SerVVisContenutoSerieUd;
import it.eng.parer.viewEntity.SerVVisErrContenSerieUd;
import it.eng.parer.viewEntity.SerVVisErrFileSerieUd;
import it.eng.parer.viewEntity.SerVVisSerieUd;
import it.eng.parer.viewEntity.SerVVisVolVerSerieUd;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.UnitaDocumentarieEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoCreazioneSerie;
import it.eng.parer.ws.utils.CostantiDB.TipoFileVerSerie;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.message.MessageBox;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class SerieEjb {

    private static final Logger logger = LoggerFactory.getLogger(SerieEjb.class);

    @Resource
    private SessionContext context;

    @EJB
    private SerieHelper helper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private CreazioneIndiceAipSerieUdHelper ciasudHelper;
    @EJB
    private CryptoInvoker cryptoInvoker;

    @EJB
    private ElaborazioneRigaIndiceAipDaElab elabIndiceAip;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;

    // MEV#30400
    @EJB
    private ObjectStorageService objectStorageService;

    private static final String LOG_SALVATAGGIO_OS = "Salvato l'indice aip della serie su Object storage nel bucket {} con chiave {}! ";

    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDocumento")
    private RecuperoDocumento recuperoDocumento;

    private static final String ECCEZIONE_RECUPERO_INDICE_AIP = "Errore non gestito nel recupero del file";
    // end MEV#30400

    // MEV #31162
    @EJB
    private UnitaDocumentarieEjb udEjb;
    // end MEV #31162

    public static final String CD_SERIE_REGEX = "[A-Za-z0-9\\.\\-\\_\\:]+";
    public static final Pattern CD_SERIE_PATTERN = Pattern.compile(CD_SERIE_REGEX);

    public boolean checkCdSerie(String cdSerie) {
	return CD_SERIE_PATTERN.matcher(cdSerie).matches();
    }

    public SerSerieRowBean getSerSerieRowBean(BigDecimal idTipoSerie, BigDecimal idStrut,
	    BigDecimal aaSerie) {
	SerSerie serie = helper.getSerSerie(idTipoSerie, idStrut, aaSerie);
	SerSerieRowBean row = null;
	if (serie != null) {
	    try {
		row = (SerSerieRowBean) Transform.entity2RowBean(serie);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero delle serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero delle serie");
	    }
	}
	return row;
    }

    // <editor-fold defaultstate="collapsed" desc="Verifica esistenza serie">
    public boolean isSerieExisting(BigDecimal idStrut, BigDecimal idTipoSerie, BigDecimal aaSerie,
	    Date dataInizio, Date dataFine) {
	Long count = helper.countSerie(idStrut, idTipoSerie, aaSerie, dataInizio, dataFine, null);
	return count > 0L;
    }

    public boolean isSerieExisting(BigDecimal idStrut, BigDecimal idTipoSerie, BigDecimal aaSerie,
	    Date dataInizio, Date dataFine, String cdSerie) {
	Long count = helper.countSerie(idStrut, idTipoSerie, aaSerie, dataInizio, dataFine,
		cdSerie);
	return count > 0L;
    }

    public boolean isSerieExisting(BigDecimal idStrut, BigDecimal idTipoSerie, BigDecimal aaSerie,
	    String cdSerie) {
	Long count = helper.countSerie(idStrut, idTipoSerie, aaSerie, null, null, cdSerie);
	return count > 0L;
    }

    public boolean isSerieExisting(BigDecimal idStrut, BigDecimal aaSerie, String cdSerie) {
	Long count = helper.countSerie(idStrut, null, aaSerie, null, null, cdSerie);
	return count > 0L;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvataggio serie">
    /**
     * Esegue il salvataggio in transazione del nuovo record serie, che verr\u00E0 poi utilizzato
     * dal metodo asincrono per eseguire il calcolo
     *
     * @param idUser    l'utente che ha richiamato la funzione di creazione
     * @param idStrut   la struttura per cui viene creata la serie
     * @param serieBean bean con i parametri di creazione della serie
     * @param file      upload caricato dall'utente
     *
     * @return l'id del record della versione
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveSerie(long idUser, BigDecimal idStrut, CreazioneSerieBean serieBean,
	    byte[] file) throws ParerUserError {
	OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
	DecTipoSerie tipoSerie = helper.findById(DecTipoSerie.class, serieBean.getNm_tipo_serie());
	IamUser user = helper.findById(IamUser.class, idUser);
	String codiceSeriePadre = null;
	if (StringUtils.isNotBlank(serieBean.getCd_serie_padre())) {
	    codiceSeriePadre = serieBean.getCd_serie_padre();
	} else if (StringUtils.isNotBlank(serieBean.getCd_serie_padre_da_creare())) {
	    codiceSeriePadre = serieBean.getCd_serie_padre_da_creare();
	}
	String codiceSerie = StringUtils.isNotBlank(codiceSeriePadre)
		? codiceSeriePadre + "/" + serieBean.getCd_serie()
		: serieBean.getCd_serie();
	if (isSerieExisting(idStrut, serieBean.getAa_serie(), codiceSerie)) {
	    throw new ParerUserError("Esiste gi\u00E0 una serie con lo stesso codice");
	}

	Calendar c = Calendar.getInstance();
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	c.set(Calendar.MILLISECOND, 0);
	c.set(Calendar.YEAR, 2444);
	c.set(Calendar.MONTH, Calendar.DECEMBER);
	c.set(Calendar.DATE, 31);
	Date defaultAnnullamento = c.getTime();

	logger.info(
		SerieEjb.class.getSimpleName() + " SALVATAGGIO SERIE - Salvataggio nuova serie");
	SerSerie serie = new SerSerie();
	serie.setAaSerie(serieBean.getAa_serie());
	serie.setCdCompositoSerie(codiceSerie);
	serie.setDecTipoSerie(tipoSerie);
	serie.setDsSerie(serieBean.getDs_serie());
	serie.setNiAnniConserv(serieBean.getNi_anni_conserv());
	serie.setOrgStrut(strut);
	serie.setDtAnnul(defaultAnnullamento);
	serie.setCdSerieNormaliz(serieBean.getCd_serie_normaliz());

	if (serie.getSerSeriePadres() == null) {
	    serie.setSerSeriePadres(new ArrayList<>());
	}
	if (serie.getSerVerSeries() == null) {
	    serie.setSerVerSeries(new ArrayList<>());
	}

	logger.info(SerieEjb.class.getSimpleName() + " SALVATAGGIO SERIE - Salvataggio versione");
	SerVerSerie verSerie = new SerVerSerie();
	verSerie.setPgVerSerie(BigDecimal.ONE);
	verSerie.setCdVerSerie("00.01");
	verSerie.setDtInizioSelSerie(serieBean.getDt_inizio_serie());
	verSerie.setDtFineSelSerie(serieBean.getDt_fine_serie());
	verSerie.setDtRegUnitaDocDa(serieBean.getDt_reg_unita_doc_da());
	verSerie.setDtRegUnitaDocA(serieBean.getDt_reg_unita_doc_a());
	verSerie.setDtCreazioneUnitaDocDa(serieBean.getDt_creazione_unita_doc_da());
	verSerie.setDtCreazioneUnitaDocA(serieBean.getDt_creazione_unita_doc_a());
	verSerie.setNiPeriodoSelSerie(serieBean.getNi_periodo_sel_serie());
	verSerie.setTiPeriodoSelSerie(serieBean.getTi_periodo_sel_serie());
	verSerie.setDsListaAnniSelSerie(serieBean.getDs_lista_anni_sel_serie());
	verSerie.setFlUpdAnnulUnitaDoc("0");
	serie.addSerVerSery(verSerie);
	if (verSerie.getSerContenutoVerSeries() == null) {
	    verSerie.setSerContenutoVerSeries(new ArrayList<>());
	}
	if (verSerie.getSerStatoVerSeries() == null) {
	    verSerie.setSerStatoVerSeries(new ArrayList<>());
	}
	if (verSerie.getSerFileInputVerSeries() == null) {
	    verSerie.setSerFileInputVerSeries(new ArrayList<>());
	}
	if (verSerie.getSerNotaVerSeries() == null) {
	    verSerie.setSerNotaVerSeries(new ArrayList<>());
	}

	boolean acquisizione = false;
	if (serieBean.getTi_creazione().equals(TipoCreazioneSerie.ACQUISIZIONE_FILE.name())) {
	    acquisizione = true;
	}

	Calendar now = Calendar.getInstance();

	logger.info(
		SerieEjb.class.getSimpleName() + " SALVATAGGIO SERIE - Salvataggio stato versione");
	SerStatoVerSerie statoVerSerie = new SerStatoVerSerie();
	statoVerSerie.setPgStatoVerSerie(BigDecimal.ONE);
	statoVerSerie.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.APERTURA_IN_CORSO.name());
	statoVerSerie.setDsNotaAzione(serieBean.getDs_nota_azione());
	statoVerSerie.setDsAzione(serieBean.getDs_azione());
	statoVerSerie.setIamUser(user);
	statoVerSerie.setDtRegStatoVerSerie(now.getTime());
	verSerie.addSerStatoVerSery(statoVerSerie);

	String tipoContenuto = acquisizione ? CostantiDB.TipoContenutoVerSerie.ACQUISITO.name()
		: CostantiDB.TipoContenutoVerSerie.CALCOLATO.name();
	context.getBusinessObject(SerieEjb.class).createSerContenutoVerSerie(verSerie,
		tipoContenuto, now.getTime());

	for (DecNotaTipoSerie notaTipoSerie : tipoSerie.getDecNotaTipoSeries()) {
	    SerNotaVerSerie notaSerie = new SerNotaVerSerie();
	    notaSerie.setDtNotaVerSerie(now.getTime());
	    notaSerie.setDecTipoNotaSerie(notaTipoSerie.getDecTipoNotaSerie());
	    notaSerie.setPgNotaVerSerie(notaTipoSerie.getPgNotaTipoSerie());
	    notaSerie.setDsNotaVerSerie(notaTipoSerie.getDsNotaTipoSerie());

	    user.addSerNotaVerSery(notaSerie);
	    verSerie.addSerNotaVerSery(notaSerie);
	}

	if (acquisizione) {
	    context.getBusinessObject(SerieEjb.class).createSerFileInputVerSerie(file,
		    serieBean.getCd_doc_file_input_ver_serie(),
		    serieBean.getDs_doc_file_input_ver_serie(), serieBean.getFl_fornito_ente(),
		    CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name(), user, verSerie);
	}

	if (StringUtils.isNotBlank(serieBean.getCd_serie_padre_da_creare())) {
	    if (tipoSerie.getDecTipoSeriePadre() == null) {
		throw new ParerUserError(
			"Non \u00E8 possibile creare una serie padre per un tipo serie senza tipo serie padre");
	    } else {
		logger.info(SerieEjb.class.getSimpleName()
			+ " SALVATAGGIO SERIE - Salvataggio padre da creare");
		DecTipoSerie tipoSeriePadre = tipoSerie.getDecTipoSeriePadre();
		SerSerie seriePadre = new SerSerie();
		seriePadre.setOrgStrut(strut);
		seriePadre.setAaSerie(serieBean.getAa_serie());
		seriePadre.setCdCompositoSerie(serieBean.getCd_serie_padre_da_creare());
		seriePadre.setDsSerie(serieBean.getDs_serie_padre_da_creare());
		seriePadre.setDecTipoSerie(tipoSeriePadre);
		seriePadre.setNiAnniConserv(serieBean.getNi_anni_conserv());

		serie.setSerSeriePadre(seriePadre);
		if (seriePadre.getSerSeriePadres() == null) {
		    seriePadre.setSerSeriePadres(new ArrayList<SerSerie>());
		}
		if (seriePadre.getSerVerSeries() == null) {
		    seriePadre.setSerVerSeries(new ArrayList<SerVerSerie>());
		}
		seriePadre.getSerSeriePadres().add(serie);

		SerVerSerie verSeriePadre = new SerVerSerie();
		verSeriePadre.setPgVerSerie(BigDecimal.ONE);
		verSeriePadre.setCdVerSerie("00.01");
		verSeriePadre.setSerSerie(seriePadre);
		if (verSeriePadre.getSerVerSeries() == null) {
		    verSeriePadre.setSerVerSeries(new ArrayList<SerVerSerie>());
		}
		if (verSeriePadre.getSerStatoVerSeries() == null) {
		    verSeriePadre.setSerStatoVerSeries(new ArrayList<SerStatoVerSerie>());
		}
		seriePadre.addSerVerSery(verSeriePadre);

		verSerie.setSerVerSerie(verSeriePadre);
		verSeriePadre.getSerVerSeries().add(verSerie);

		SerStatoVerSerie statoVerSeriePadre = new SerStatoVerSerie();
		statoVerSeriePadre.setPgStatoVerSerie(BigDecimal.ONE);
		statoVerSeriePadre
			.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.APERTURA_IN_CORSO.name());
		statoVerSeriePadre.setDsNotaAzione(
			"Serie 'padre' creata contestualmente alla creazione della serie "
				+ serie.getCdCompositoSerie());
		statoVerSeriePadre.setDsAzione("Creazione manuale");
		statoVerSeriePadre.setIamUser(user);

		statoVerSeriePadre.setDtRegStatoVerSerie(now.getTime());
		verSeriePadre.addSerStatoVerSery(statoVerSeriePadre);

		logger.info(SerieEjb.class.getSimpleName()
			+ " SALVATAGGIO SERIE - Eseguo la persist dal nuovo padre in cascade");
		helper.insertEntity(seriePadre, true);
		helper.insertEntity(statoVerSeriePadre, false);
		verSeriePadre.setIdStatoVerSerieCor(
			new BigDecimal(statoVerSeriePadre.getIdStatoVerSerie()));
	    }
	} else if (StringUtils.isNotBlank(serieBean.getCd_serie_padre())
		&& serieBean.getId_serie_padre() != null) {
	    logger.info(SerieEjb.class.getSimpleName()
		    + " SALVATAGGIO SERIE - Aggiunta relazione a padre gi\u00E0 esistente");
	    SerSerie seriePadre = helper.findById(SerSerie.class, serieBean.getId_serie_padre());
	    serie.setSerSeriePadre(seriePadre);
	    seriePadre.getSerSeriePadres().add(serie);

	    SerVerSerie lastVerSeriePadre = helper
		    .getLastSerVerSerie(serieBean.getId_serie_padre());
	    verSerie.setSerVerSerie(lastVerSeriePadre);
	    lastVerSeriePadre.getSerVerSeries().add(verSerie);

	    logger.info(SerieEjb.class.getSimpleName()
		    + " SALVATAGGIO SERIE - Eseguo la persist dal padre preesistente in cascade");
	} else {
	    logger.info(SerieEjb.class.getSimpleName()
		    + " SALVATAGGIO SERIE - Eseguo la persist direttamente sulla nuova serie");
	    helper.insertEntity(serie, true);
	}

	helper.insertEntity(statoVerSerie, false);
	verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	return verSerie.getIdVerSerie();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerFileInputVerSerie createSerFileInputVerSerie(byte[] file,
	    String cdDocFileInputVerSerie, String dsDocFileInputVerSerie, String flFornitoEnte,
	    String tiScopoFileInputVerSerie, IamUser user, SerVerSerie verSerie) {
	SerFileInputVerSerie fileInputVerSerie = new SerFileInputVerSerie();
	fileInputVerSerie.setBlFileInputVerSerie(new String(file, Charset.forName("UTF-8")));
	fileInputVerSerie.setTiScopoFileInputVerSerie(tiScopoFileInputVerSerie);
	fileInputVerSerie.setCdDocFileInputVerSerie(cdDocFileInputVerSerie);
	fileInputVerSerie.setDsDocFileInputVerSerie(dsDocFileInputVerSerie);
	fileInputVerSerie.setFlFornitoEnte(flFornitoEnte);

	user.addSerFileInputVerSery(fileInputVerSerie);
	verSerie.addSerFileInputVerSery(fileInputVerSerie);
	return fileInputVerSerie;
    }

    public void createSerContenutoVerSerie(SerVerSerie verSerie, String tipoContenuto, Date now) {
	SerContenutoVerSerie contenutoSerie = new SerContenutoVerSerie();
	contenutoSerie.setSerVerSerie(verSerie);
	contenutoSerie.setTiContenutoVerSerie(tipoContenuto);
	contenutoSerie.setTiStatoContenutoVerSerie(
		CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name());
	contenutoSerie.setDtStatoContenutoVerSerie(now);
	contenutoSerie.setNiUdContenutoVerSerie(BigDecimal.ZERO);
	contenutoSerie.setIdFirstUdAppartVerSerie(BigDecimal.ZERO);
	contenutoSerie.setIdLastUdAppartVerSerie(BigDecimal.ZERO);
	contenutoSerie.setFlTipoSerieUpd("0");
	verSerie.addSerContenutoVerSery(contenutoSerie);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione CREAZIONE SERIE - ASINCRONA">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> callCreazioneSerieAsync(long idUser, Long idVerSerie,
	    boolean acquisizione) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<String> future = null;
	try {
	    JobConstants.JobEnum tipoCreazione;
	    if (acquisizione) {
		tipoCreazione = JobConstants.JobEnum.INPUT_SERIE;
	    } else {
		tipoCreazione = JobConstants.JobEnum.CALCOLO_SERIE;
	    }
	    future = context.getBusinessObject(SerieEjb.class).creazioneSerieAsync(idUser,
		    tipoCreazione, idVerSerie);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return future;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Map<String, ?>> callCreazioneSerieAsync(Map<String, BigDecimal> idVerSeries) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<Map<String, ?>> futures = null;
	try {
	    futures = context.getBusinessObject(SerieEjb.class).creazioneSerieAsync(idVerSeries);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return futures;
    }

    @Asynchronous
    public Future<String> creazioneSerieAsync(long idUser, JobConstants.JobEnum tipoCreazione,
	    long idVerSerie) throws ParerInternalError {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.info(SerieEjb.class.getSimpleName() + " --- Richiesta LOCK su contenuto di tipo "
		    + tipoCreazione.name());
	    switch (tipoCreazione) {
	    case CALCOLO_SERIE:
		contenuto = helper.getLockSerContenutoVerSerie(idVerSerie,
			CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
		if (contenuto != null) {
		    jobHelper.writeAtomicLogJob(tipoCreazione.name(),
			    JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			    contenuto.getIdContenutoVerSerie(),
			    contenuto.getClass().getSimpleName());
		    context.getBusinessObject(SerieEjb.class).calcoloSerie(idUser, contenuto,
			    false);
		}
		break;
	    case INPUT_SERIE:
		contenuto = helper.getLockSerContenutoVerSerie(idVerSerie,
			CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
		if (contenuto != null) {
		    jobHelper.writeAtomicLogJob(tipoCreazione.name(),
			    JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			    contenuto.getIdContenutoVerSerie(),
			    contenuto.getClass().getSimpleName());
		    context.getBusinessObject(SerieEjb.class).inputSerie(idUser, contenuto);
		}
		break;
	    default:
		throw new IllegalArgumentException("Tipo creazione serie non valido");
	    }

	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(tipoCreazione.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per creazione serie");
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista durante la fase di creazione serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    error = true;
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(tipoCreazione.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}

	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return new AsyncResult<>(result);
    }

    @Asynchronous
    public Future<Map<String, ?>> creazioneSerieAsync(Map<String, BigDecimal> idVerSeries) {
	Map<String, String> futures = new HashMap<>();
	for (Entry<String, BigDecimal> idVerSerieEntry : idVerSeries.entrySet()) {
	    // Recupero il parametro di struttura USERID_CREAZIONE_SERIE
	    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerieEntry.getValue());
	    BigDecimal idStrut = BigDecimal
		    .valueOf(verSerie.getSerSerie().getOrgStrut().getIdStrut());
	    BigDecimal idAmbiente = BigDecimal.valueOf(verSerie.getSerSerie().getOrgStrut()
		    .getOrgEnte().getOrgAmbiente().getIdAmbiente());
	    String nmUserId = configurationHelper.getValoreParamApplicByStrut(
		    CostantiDB.ParametroAppl.USERID_CREAZIONE_SERIE, idAmbiente, idStrut);
	    IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);
	    long idUser = userCreazioneSerie.getIdUserIam();
	    String result = context.getBusinessObject(SerieEjb.class)
		    .calcoloContenutoSerieSingleTx(idVerSerieEntry.getValue(), idUser);
	    futures.put(idVerSerieEntry.getKey(), result);
	}
	return new AsyncResult<Map<String, ?>>(futures);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String calcoloContenutoSerieSingleTx(BigDecimal idVerSerieEntry, long idUser) {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Richiesta LOCK su contenuto CALCOLATO con id_ver_serie "
		    + idVerSerieEntry.toPlainString());
	    contenuto = helper.getLockSerContenutoVerSerie(idVerSerieEntry.longValue(),
		    CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_SERIE.name(),
			JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
		context.getBusinessObject(SerieEjb.class).calcoloSerie(idUser, contenuto, true);
		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_SERIE.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per creazione serie");
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di creazione serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_SERIE.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}
	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CALCOLO SERIE">
    public void calcoloSerie(long idUser, SerContenutoVerSerie contenuto,
	    boolean calcoloAutomatico) {
	SerVerSerie verSerie = contenuto.getSerVerSerie();
	SerSerie serSerie = verSerie.getSerSerie();
	DecTipoSerie tipoSerie = serSerie.getDecTipoSerie();
	BigDecimal annoSerie = serSerie.getAaSerie();
	List<DecTipoSerieUd> tipiSerieUd = helper.getDecTipoSerieUd(tipoSerie.getIdTipoSerie());
	logger.debug(SerieEjb.class.getSimpleName()
		+ " CALCOLO ASYNC SERIE --- Ottenuti i tipi di serie da calcolare");

	if (tipiSerieUd.isEmpty()) {
	    throw new IllegalStateException(
		    "Errore inatteso nel calcolo della serie, non esistono record di associazioni registri - tipologie di unit\u00E0 documentarie per eseguire il calcolo");
	}

	int counterUd = 0;
	int rounds = 0;
	for (DecTipoSerieUd tipoSerieUd : tipiSerieUd) {
	    rounds++;
	    // Preparazione query - ottengo in un qualche modo degli AroUnitaDoc
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " CALCOLO ASYNC SERIE --- Preparazione della query di selezione delle unit\u00E0 documentarie");
	    String query = this.wrapQuery(JobConstants.JobEnum.CALCOLO_SERIE, tipoSerieUd,
		    annoSerie, verSerie);

	    List<ResultVCalcoloSerieUd> tmpUds = context.getBusinessObject(SerieEjb.class)
		    .saveQueryAndExecute(query,
			    tipoSerieUd.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
			    tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), contenuto,
			    helper.getDecFiltroSelUdSelTables(tipoSerieUd.getIdTipoSerieUd()));
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " CALCOLO ASYNC SERIE --- Query eseguita - ottenute " + tmpUds.size()
		    + " unit\u00E0 documentarie");
	    if (!tmpUds.isEmpty()) {
		for (ResultVCalcoloSerieUd ud : tmpUds) {
		    context.getBusinessObject(SerieEjb.class).handleAroUnitaDocWithTransaction(
			    contenuto.getIdContenutoVerSerie(), ud);
		}
		counterUd += tmpUds.size();
	    }
	    context.getBusinessObject(SerieEjb.class).handleAroUdAppartVerSerie(idUser, contenuto,
		    counterUd, "Calcolo serie", calcoloAutomatico, (rounds == tipiSerieUd.size()));
	}
    }

    private String wrapQuery(JobConstants.JobEnum tipoCreazione, DecTipoSerieUd tipoSerieUd,
	    BigDecimal annoSerie, SerVerSerie verSerie) throws IllegalStateException {
	String query = context.getBusinessObject(SerieEjb.class).buildQuery(tipoCreazione,
		tipoSerieUd, annoSerie, verSerie);
	StringBuilder wrapQuery = new StringBuilder("SELECT tmp.id_unita_doc, tmp.dt_creazione, ");
	boolean dtUdSeriePresente = false;
	boolean dsKeyOrdPresente = false;
	int counter = 1;
	int keyOrdIndex = 0;
	if (query.contains(CostantiDB.TipoDiRappresentazione.KEY_UD_SERIE.name())) {
	    wrapQuery.append("tmp.key_ud_serie, ");
	    counter++;
	}
	if (query.contains(CostantiDB.TipoDiRappresentazione.DT_UD_SERIE.name())) {
	    wrapQuery.append("tmp.dt_ud_serie, ");
	    dtUdSeriePresente = true;
	    counter++;
	}
	if (query.contains(CostantiDB.TipoDiRappresentazione.INFO_UD_SERIE.name())) {
	    wrapQuery.append("tmp.info_ud_serie, ");
	    counter++;
	}
	if (query.contains(CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) {
	    wrapQuery.append("tmp.ds_key_ord_ud_serie, ");
	    dsKeyOrdPresente = true;
	    counter++;
	    keyOrdIndex = counter;
	}
	if (query.contains(CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name())) {
	    wrapQuery.append("tmp.pg_ud_serie, ");
	    counter++;
	}
	wrapQuery.deleteCharAt(wrapQuery.lastIndexOf(","));
	wrapQuery.append(" FROM ( ");
	wrapQuery.append(query).append(" ) tmp ");
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
	wrapQuery.append(" WHERE ").append("tmp.key_ud_serie IS NOT NULL").append(" AND ")
		.append("tmp.dt_ud_serie IS NOT NULL").append(" AND ")
		.append("tmp.info_ud_serie IS NOT NULL").append(" AND ")
		.append("tmp.ds_key_ord_ud_serie IS NOT NULL");
	if (dtUdSeriePresente && verSerie.getDtInizioSelSerie() != null
		&& verSerie.getDtFineSelSerie() != null) {
	    wrapQuery.append(" AND ").append("tmp.dt_ud_serie >= ").append("to_date('")
		    .append(df.format(verSerie.getDtInizioSelSerie())).append("', 'dd/mm/yyyy')")
		    .append(" AND ").append("tmp.dt_ud_serie <= ").append("to_date('")
		    .append(df.format(verSerie.getDtFineSelSerie())).append("', 'dd/mm/yyyy')");
	}

	if (dsKeyOrdPresente) {
	    wrapQuery.append(" ORDER BY ").append(keyOrdIndex);
	}
	return wrapQuery.toString();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleAroUnitaDocWithTransaction(Long idContenutoVerSerie,
	    ResultVCalcoloSerieUd result) {
	logger.debug(SerieEjb.class.getSimpleName()
		+ " CALCOLO ASYNC SERIE --- Registra l'appartenenza dell'ud al contenuto");
	context.getBusinessObject(SerieEjb.class).handleAroUnitaDoc(idContenutoVerSerie, result);
    }

    public void handleAroUnitaDoc(Long idContenutoVerSerie, ResultVCalcoloSerieUd result) {
	AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, result.getIdUnitaDoc());
	SerContenutoVerSerie contenuto = helper.findById(SerContenutoVerSerie.class,
		idContenutoVerSerie);
	// Registra l'appartenenza dell'ud al contenuto (AroUdAppartVerSerie)
	AroUdAppartVerSerie appart = new AroUdAppartVerSerie();
	appart.setAroUnitaDoc(ud);
	appart.setCdUdSerie(result.getKeyUdSerie());
	appart.setDtUdSerie(result.getDtUdSerie());
	appart.setInfoUdSerie(result.getInfoUdSerie());
	appart.setDsKeyOrdUdSerie(result.getDsKeyOrdUdSerie());
	appart.setPgUdSerie(result.getPgUdSerie());

	contenuto.addAroUdAppartVerSery(appart);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleAroUdAppartVerSerie(long idUser, SerContenutoVerSerie contenuto,
	    int counterUd, String azione, boolean calcoloAutomatico, boolean lastRound) {
	logger.debug(SerieEjb.class.getSimpleName() + azione
		+ " --- Aggiorna il contenuto della versione con i dati delle ud appartenenti ad essa");
	Long firstUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), true);
	Long lastUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), false);
	Date now = Calendar.getInstance().getTime();

	contenuto.setNiUdContenutoVerSerie(new BigDecimal(counterUd));
	contenuto.setIdFirstUdAppartVerSerie(
		firstUd != null ? new BigDecimal(firstUd) : BigDecimal.ZERO);
	contenuto.setIdLastUdAppartVerSerie(
		lastUd != null ? new BigDecimal(lastUd) : BigDecimal.ZERO);
	contenuto.setTiStatoContenutoVerSerie(CostantiDB.StatoContenutoVerSerie.CREATO.name());
	contenuto.setDtStatoContenutoVerSerie(now);

	SerVerSerie verSerie = contenuto.getSerVerSerie();
	SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());
	if (statoCorrente != null) {
	    if (!statoCorrente.getTiStatoVerSerie()
		    .equals(CostantiDB.StatoVersioneSerie.APERTA.name())) {
		logger.debug(SerieEjb.class.getSimpleName() + azione
			+ " --- Aggiorna lo stato della versione");
		SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
			.createSerStatoVerSerie(
				statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
				CostantiDB.StatoVersioneSerie.APERTA.name(), azione, null, idUser,
				now, verSerie.getIdVerSerie());

		logger.debug(SerieEjb.class.getSimpleName() + azione
			+ " --- Eseguo la persist del nuovo stato versione");
		helper.insertEntity(statoVerSerie, false);
		verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));
	    }
	    DecTipoSerie tipoSerie = verSerie.getSerSerie().getDecTipoSerie();
	    if (calcoloAutomatico && tipoSerie.getTiStatoVerSerieAutom() == null) {
		throw new IllegalStateException(
			"Impostato calcolo automatico per la creazione della serie senza aver definito lo stato della versione all'interno del tipo di serie");
	    } else if (calcoloAutomatico && tipoSerie.getTiStatoVerSerieAutom() != null
		    && (tipoSerie.getTiStatoVerSerieAutom()
			    .equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
			    || tipoSerie.getTiStatoVerSerieAutom()
				    .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
			    || tipoSerie.getTiStatoVerSerieAutom()
				    .equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name()))
		    && lastRound) {
		SerVerSerieDaElab verSerieDaElab = new SerVerSerieDaElab();
		verSerieDaElab.setDtRegStatoVerSerie(now);
		verSerieDaElab.setIdStrut(
			new BigDecimal(verSerie.getSerSerie().getOrgStrut().getIdStrut()));
		verSerieDaElab
			.setTiStatoVerSerie(CostantiDB.StatoVersioneSerieDaElab.APERTA.name());
		verSerieDaElab.setSerVerSerie(verSerie);
		logger.debug(SerieEjb.class.getSimpleName()
			+ " --- Eseguo la persist del nuovo verSerie da elab");
		helper.insertEntity(verSerieDaElab, false);
	    }
	} else {
	    throw new IllegalStateException(
		    "Errore inatteso in fase di salvataggio dello stato corrente della versione della serie");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerStatoVerSerie createSerStatoVerSerie(BigDecimal pgStatoVerSerie,
	    String tiStatoVerSerie, String azione, String nota, long idUser, Date now,
	    Long idVerSerie) {
	SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
	SerStatoVerSerie statoVerSerie = new SerStatoVerSerie();
	statoVerSerie.setSerVerSerie(verSerie);
	statoVerSerie.setPgStatoVerSerie(pgStatoVerSerie);
	statoVerSerie.setTiStatoVerSerie(tiStatoVerSerie);
	statoVerSerie.setDsAzione(azione);
	statoVerSerie.setIamUser(helper.findById(IamUser.class, idUser));
	statoVerSerie.setDtRegStatoVerSerie(now);
	statoVerSerie.setDsNotaAzione(nota);
	verSerie.addSerStatoVerSery(statoVerSerie);
	return statoVerSerie;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerStatoSerie createSerStatoSerie(BigDecimal pgStatoSerie, String tiStatoSerie,
	    String azione, String nota, long idUser, Date now, Long idSerie) {
	SerSerie serie = helper.findById(SerSerie.class, idSerie);
	SerStatoSerie statoSerie = new SerStatoSerie();
	statoSerie.setSerSerie(serie);
	statoSerie.setPgStatoSerie(pgStatoSerie);
	statoSerie.setTiStatoSerie(tiStatoSerie);
	statoSerie.setDsAzione(azione);
	statoSerie.setIamUser(helper.findById(IamUser.class, idUser));
	statoSerie.setDtRegStatoSerie(now);
	statoSerie.setDsNotaAzione(nota);
	serie.addSerStatoSery(statoSerie);
	return statoSerie;
    }

    public String buildQuery(JobConstants.JobEnum tipoCreazione, DecTipoSerieUd tipoSerieUd,
	    BigDecimal annoSerie, SerVerSerie verSerie) {
	BigDecimal counterRicDatiSpec = BigDecimal.ONE;
	List<DecOutSelUd> output = helper.getDecOutSelUd(tipoSerieUd.getIdTipoSerieUd());
	List<Long> selTables = helper.getDecFiltroSelUdSelTables(tipoSerieUd.getIdTipoSerieUd());
	String select = context.getBusinessObject(SerieEjb.class).buildSelectQuery(output,
		counterRicDatiSpec, annoSerie, verSerie.getSerSerie().getCdCompositoSerie());
	select = StringUtils.chomp(select, ", ");
	String from = context.getBusinessObject(SerieEjb.class).buildFromQuery(selTables,
		select.contains("tipoDocPrinc.nm_Tipo_Doc"));
	String where;
	StringBuilder whereBuilder = this.buildGenericWhere(tipoSerieUd, annoSerie, verSerie);
	switch (tipoCreazione) {
	case CALCOLO_SERIE:
	    List<DecFiltroSelUd> filtri = helper.getDecFiltroSelUd(tipoSerieUd.getIdTipoSerieUd());
	    List<DecFiltroSelUdDato> filtriUd = helper.getDecFiltroSelUdDato(
		    tipoSerieUd.getIdTipoSerieUd(),
		    tipoSerieUd.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
	    where = context.getBusinessObject(SerieEjb.class).buildWhereCalcoloSerieQuery(
		    whereBuilder, selTables, filtri, filtriUd, tipoSerieUd, annoSerie,
		    counterRicDatiSpec, verSerie);
	    break;
	case INPUT_SERIE:
	    List<DecCampoInpUd> filtriDatoProfilo = helper
		    .getDecCampoInpUdDatoProfilo(tipoSerieUd.getDecTipoSerie().getIdTipoSerie());
	    List<DecCampoInpUd> filtriDatiSpecUd = helper.getDecCampoInpUdDatiSpec(
		    tipoSerieUd.getDecTipoSerie().getIdTipoSerie(),
		    tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), null);
	    where = context.getBusinessObject(SerieEjb.class).buildWhereInputSerieQuery(
		    whereBuilder, selTables, filtriDatoProfilo, filtriDatiSpecUd, tipoSerieUd,
		    annoSerie, counterRicDatiSpec);
	    break;
	default:
	    throw new IllegalArgumentException("Tipo creazione serie non valido");
	}

	String query = select + from + where;
	return query;
    }

    public List<ResultVCalcoloSerieUd> saveQueryAndExecute(String query, Long idRegistroUnitaDoc,
	    Long idTipoUnitaDoc, SerContenutoVerSerie contenuto, List<Long> selTables) {
	context.getBusinessObject(SerieEjb.class).saveQuery(query, idRegistroUnitaDoc,
		idTipoUnitaDoc, contenuto.getIdContenutoVerSerie());
	return helper.executeQueryList(query);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveQuery(String query, Long idRegistroUnitaDoc, Long idTipoUnitaDoc,
	    Long idContenutoVerSerie) {
	SerContenutoVerSerie tmp = helper.findById(SerContenutoVerSerie.class, idContenutoVerSerie);
	SerQueryContenutoVerSerie queryCont = new SerQueryContenutoVerSerie();
	queryCont.setBlQuery(query);
	queryCont.setIdRegistroUnitaDoc(new BigDecimal(idRegistroUnitaDoc));
	queryCont.setIdTipoUnitaDoc(new BigDecimal(idTipoUnitaDoc));

	tmp.addSerQueryContenutoVerSery(queryCont);
    }

    public String buildSelectQuery(List<DecOutSelUd> output, BigDecimal counterRicDatiSpec,
	    BigDecimal annoSerie, String cdSerie) {
	StringBuilder select = new StringBuilder("SELECT ud.ID_UNITA_DOC, ud.DT_CREAZIONE, ");
	for (DecOutSelUd out : output) {
	    Map<String, String> campiItemSelect = new HashMap<>();
	    List<DecCampoOutSelUd> campi = helper.getDecCampoOutSelUd(out.getIdOutSelUd());
	    // Gestisco tutti i campi da inserire nella select
	    for (DecCampoOutSelUd campo : campi) {
		CostantiDB.TipoCampo tipoCampo = CostantiDB.TipoCampo.valueOf(campo.getTiCampo());
		long idAttrib;
		String valoreAttributo = "";
		String subQuery;
		String tableSuffix;
		// Eseguo le sostituzioni in base al tipo di campo
		switch (tipoCampo) {
		case DATO_PROFILO:
		    CostantiDB.NomeCampo nomeCampo = CostantiDB.NomeCampo
			    .valueOf(campo.getNmCampo());
		    switch (nomeCampo) {
		    case TIPO_UNITA_DOC:
			campiItemSelect.put(nomeCampo.name(), "tipoUd.nm_Tipo_Unita_Doc");
			break;
		    case REGISTRO:
			campiItemSelect.put(nomeCampo.name(), "ud.cd_Registro_Key_Unita_Doc");
			break;
		    case ANNO:
			campiItemSelect.put(nomeCampo.name(), "to_char(ud.aa_Key_Unita_Doc)");
			break;
		    case NUMERO:
			if (out.getTiOut()
				.equals(CostantiDB.TipoDiRappresentazione.PG_UD_SERIE.name())) {
			    campiItemSelect.put(nomeCampo.name(), "ud.pg_unita_doc");
			} else if (out.getTiOut().equals(
				CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) {
			    campiItemSelect.put(nomeCampo.name(), "lpad(ud.pg_unita_doc, 12, '0')");
			} else {
			    campiItemSelect.put(nomeCampo.name(), "ud.cd_Key_Unita_Doc");
			}
			break;
		    case DATA_REG:
			if (out.getTiOut().equals(
				CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) {
			    campiItemSelect.put(nomeCampo.name(),
				    "to_char(ud.dt_Reg_Unita_Doc, 'yyyy/mm/dd')");
			} else {
			    campiItemSelect.put(nomeCampo.name(),
				    "to_char(ud.dt_Reg_Unita_Doc, 'dd/mm/yyyy')");
			}
			break;
		    case DATA_VERS:
			if (out.getTiOut().equals(
				CostantiDB.TipoDiRappresentazione.DS_KEY_ORD_UD_SERIE.name())) {
			    campiItemSelect.put(nomeCampo.name(),
				    "to_char(ud.dt_Creazione, 'yyyy/mm/dd')");
			} else {
			    campiItemSelect.put(nomeCampo.name(),
				    "to_char(ud.dt_Creazione, 'dd/mm/yyyy')");
			}
			break;
		    case TIPO_DOC_PRINC:
			campiItemSelect.put(nomeCampo.name(), "tipoDocPrinc.nm_Tipo_Doc");
			break;
		    case OGGETTO:
			campiItemSelect.put(nomeCampo.name(), "ud.dl_Oggetto_Unita_Doc");
			break;
		    case KEY_ORD_UD:
			campiItemSelect.put(nomeCampo.name(), "ud.ds_key_ord");
			break;
		    case ANNO_SERIE:
			campiItemSelect.put(nomeCampo.name(), annoSerie.toPlainString());
			break;
		    case CODICE_SERIE:
			campiItemSelect.put(nomeCampo.name(), "'" + cdSerie + "'");
			break;
		    default:
			throw new AssertionError(nomeCampo.name());
		    }
		    break;
		case DATO_SPEC_DOC_PRINC:
		    tableSuffix = "_" + counterRicDatiSpec.toPlainString();
		    idAttrib = campo.getDecAttribDatiSpec().getIdAttribDatiSpec();
		    if (StringUtils.isNotBlank(campo.getTiTrasformCampo())) {
			if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_NO_SEP_CHAR
					.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,7,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,5,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			} else if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_ANY_SEP_CHAR
					.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,9,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,6,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			} else if (campo.getTiTrasformCampo().equals(
				CostantiDB.TipoTrasformatore.OUT_PAD_CHAR.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN lpad(ricDatiSpec" + tableSuffix
				    + ".dl_Valore, 12, '0') ELSE NULL END";
			} else if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_NO_SEP_TO_YEAR
					.getTransformString())
				|| campo.getTiTrasformCampo()
					.equals(CostantiDB.TipoTrasformatore.OUT_ANY_SEP_TO_YEAR
						.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			}
		    } else {
			valoreAttributo = "ricDatiSpec" + tableSuffix + ".dl_Valore";
		    }
		    subQuery = "(SELECT " + valoreAttributo
			    + " FROM Aro_V_Ric_Dati_Spec ricDatiSpec" + tableSuffix
			    + " WHERE ricDatiSpec" + tableSuffix + ".id_Doc = docPrinc.id_Doc "
			    + "AND ricDatiSpec" + tableSuffix + ".ti_Entita_Sacer = 'DOC' "
			    + "AND ricDatiSpec" + tableSuffix + ".id_Attrib_Dati_Spec = "
			    + String.valueOf(idAttrib) + ")";
		    campiItemSelect.put(campo.getNmCampo(), subQuery);
		    counterRicDatiSpec = counterRicDatiSpec.add(BigDecimal.ONE);
		    break;
		case DATO_SPEC_UNI_DOC:
		    tableSuffix = "_" + counterRicDatiSpec.toPlainString();
		    idAttrib = campo.getDecAttribDatiSpec().getIdAttribDatiSpec();
		    if (StringUtils.isNotBlank(campo.getTiTrasformCampo())) {
			if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_NO_SEP_CHAR
					.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,7,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,5,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			} else if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_ANY_SEP_CHAR
					.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,9,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,6,2) || '/' || SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			} else if (campo.getTiTrasformCampo().equals(
				CostantiDB.TipoTrasformatore.OUT_PAD_CHAR.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN lpad(ricDatiSpec" + tableSuffix
				    + ".dl_Valore, 12, '0') ELSE NULL END";
			} else if (campo.getTiTrasformCampo()
				.equals(CostantiDB.TipoTrasformatore.OUT_NO_SEP_TO_YEAR
					.getTransformString())
				|| campo.getTiTrasformCampo()
					.equals(CostantiDB.TipoTrasformatore.OUT_ANY_SEP_TO_YEAR
						.getTransformString())) {
			    valoreAttributo = "CASE WHEN ricDatiSpec" + tableSuffix
				    + ".dl_valore IS NOT NULL THEN SUBSTR(ricDatiSpec" + tableSuffix
				    + ".dl_Valore,1,4) ELSE NULL END";
			}
		    } else {
			valoreAttributo = "ricDatiSpec" + tableSuffix + ".dl_Valore";
		    }
		    subQuery = "(SELECT " + valoreAttributo
			    + " FROM Aro_V_Ric_Dati_Spec ricDatiSpec" + tableSuffix
			    + " WHERE ricDatiSpec" + tableSuffix
			    + ".id_Unita_Doc = ud.id_Unita_Doc " + "AND ricDatiSpec" + tableSuffix
			    + ".ti_Entita_Sacer = 'UNI_DOC' " + "AND ricDatiSpec" + tableSuffix
			    + ".id_Attrib_Dati_Spec = " + String.valueOf(idAttrib) + ")";
		    campiItemSelect.put(campo.getNmCampo(), subQuery);
		    counterRicDatiSpec = counterRicDatiSpec.add(BigDecimal.ONE);
		    break;
		}
	    }
	    // Eseguo le sostituzioni di testo per i campi che lo prevedono
	    String itemSelect = null;
	    String dlFormatoOut = StringUtils.replace(out.getDlFormatoOut(), "'", "''");
	    if (StringUtils.isNotBlank(dlFormatoOut)) {
		// Se il campo prevede sostituzioni di testo, ricerco i tag al suo interno
		Pattern tagPattern = Pattern.compile("<(\\w+[-\\w]*)>");
		Matcher m = tagPattern.matcher(dlFormatoOut);
		/*
		 * Mi costruisco tre liste: una contenente i tagname, una gli indici di inizio e una
		 * gli indici di fine del tagname. Costruendoli insieme, per lo stesso indice di
		 * lista mi riferisco sempre allo stesso tagname
		 */
		List<String> tagNames = new ArrayList<>();
		List<Integer> tagStarts = new ArrayList<>();
		List<Integer> tagEnds = new ArrayList<>();
		while (m.find()) {
		    String tagname = m.group(1);// tag
		    tagNames.add(tagname);
		    tagStarts.add(m.start());
		    tagEnds.add(m.end());
		}

		StringBuilder tmp = new StringBuilder();
		int currentLength = 0;
		// Per ogni tag eseguo la sostituzione e costruisco la stringa
		for (int index = 0; index < tagNames.size(); index++) {
		    String tagName = tagNames.get(index);
		    int start = tagStarts.get(index);
		    int end = tagEnds.get(index);

		    String substitute = campiItemSelect.get(tagName);
		    if (start == currentLength) {
			tmp.append(substitute).append("||");
		    } else {
			tmp.append("'").append(dlFormatoOut.substring(currentLength, start))
				.append("'||");
			tmp.append(substitute).append("||");
		    }
		    currentLength = end;
		}
		// Se esiste dell'altro testo finale, lo inserisco
		if (currentLength < dlFormatoOut.length()) {
		    tmp.append("'").append(dlFormatoOut.substring(currentLength)).append("'");
		}

		String outputDefinitivo = StringUtils.chomp(tmp.toString(), "||");
		if (StringUtils.isBlank(outputDefinitivo)) {
		    throw new IllegalStateException(
			    "Errore imprevisto in fase di sostituzione dei valori nel formato di output");
		}
		itemSelect = outputDefinitivo;
	    } else if (campiItemSelect.size() == 1) {
		for (Map.Entry<String, String> campo : campiItemSelect.entrySet()) {
		    itemSelect = campo.getValue();
		}
	    } else {
		throw new IllegalStateException("Errore imprevisto : l'output con id pari a "
			+ out.getIdOutSelUd() + " non prevede formattazione dei campi");
	    }

	    CostantiDB.TipoDiRappresentazione tipoRappr = CostantiDB.TipoDiRappresentazione
		    .valueOf(out.getTiOut());
	    switch (tipoRappr) {
	    case DS_KEY_ORD_UD_SERIE:
		select.append(itemSelect).append(" DS_KEY_ORD_UD_SERIE, ");
		break;
	    case DT_UD_SERIE:
		select.append("to_date( ").append(itemSelect)
			.append(" , 'dd/mm/yyyy') DT_UD_SERIE, ");
		break;
	    case INFO_UD_SERIE:
		select.append(itemSelect).append(" INFO_UD_SERIE, ");
		break;
	    case KEY_UD_SERIE:
		select.append(itemSelect).append(" KEY_UD_SERIE, ");
		break;
	    case PG_UD_SERIE:
		select.append("to_number( ").append(itemSelect).append(")")
			.append(" PG_UD_SERIE, ");
		break;
	    default:
		throw new AssertionError(tipoRappr.name());

	    }
	}

	return select.toString();
    }

    public String buildFromQuery(List<Long> selTables, boolean tipoDocPrincInSelect) {
	StringBuilder from = new StringBuilder(
		" FROM Aro_Unita_Doc ud JOIN Dec_Tipo_Unita_Doc tipoUd on (tipoUd.id_tipo_unita_doc = ud.id_tipo_unita_doc) ");
	if (!selTables.isEmpty()) {
	    String ids = "";
	    for (Long id : selTables) {
		ids += id + ",";
	    }
	    from.append("JOIN Aro_Doc docPrinc "
		    + "ON (docPrinc.id_unita_doc = ud.id_unita_doc AND docPrinc.ti_Doc = 'PRINCIPALE' AND docPrinc.id_Tipo_Doc IN (")
		    .append(StringUtils.chop(ids))
		    .append(")) JOIN dec_Tipo_Doc tipoDocPrinc ON (tipoDocPrinc.id_tipo_doc = docPrinc.id_tipo_doc)");
	} else if (tipoDocPrincInSelect) {
	    from.append(
		    "JOIN Aro_Doc docPrinc ON (docPrinc.id_unita_doc = ud.id_unita_doc AND docPrinc.ti_Doc = 'PRINCIPALE') JOIN dec_Tipo_Doc tipoDocPrinc ON (tipoDocPrinc.id_tipo_doc = docPrinc.id_tipo_doc)");
	}

	return from.toString();
    }

    private StringBuilder buildGenericWhere(DecTipoSerieUd tipoSerieUd, BigDecimal annoSerie,
	    SerVerSerie verSerie) {
	StringBuilder where = new StringBuilder(" WHERE ");
	final String andClause = " AND ";
	OrgStrut strut = tipoSerieUd.getDecTipoSerie().getOrgStrut();
	where.append("ud.id_strut = ").append(strut.getIdStrut()).append(andClause);
	if (strut.getOrgSubStruts().size() > 1) {
	    where.append("ud.id_sub_strut IN (");
	    for (OrgSubStrut subStrut : strut.getOrgSubStruts()) {
		where.append(subStrut.getIdSubStrut()).append(",");
	    }
	    where.deleteCharAt(where.length() - 1).append(")").append(andClause);
	} else {
	    where.append("ud.id_sub_strut = ");
	    for (OrgSubStrut subStrut : strut.getOrgSubStruts()) {
		where.append(subStrut.getIdSubStrut());
	    }
	    where.append(andClause);
	}
	where.append("ud.id_Registro_Unita_Doc = ")
		.append(tipoSerieUd.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc())
		.append(andClause).append("ud.id_Tipo_Unita_Doc = ")
		.append(tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc());

	if (tipoSerieUd.getFlSelUnitaDocAnnul().equals("0")) {
	    where.append(andClause).append("ud.dt_Annul = to_date('31/12/2444', 'dd/mm/yyyy')");
	}

	if (tipoSerieUd.getDecTipoSerie().getTiSelUd()
		.equals(CostantiDB.TipoSelUdTipiSerie.ANNO_KEY.name())) {
	    where.append(andClause).append("ud.aa_Key_Unita_Doc = ")
		    .append(annoSerie.toPlainString());
	} else if (tipoSerieUd.getDecTipoSerie().getTiSelUd()
		.equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())
		&& StringUtils.isNotBlank(verSerie.getDsListaAnniSelSerie())) {
	    where.append(andClause).append("ud.aa_Key_Unita_Doc IN (")
		    .append(verSerie.getDsListaAnniSelSerie()).append(")");
	}

	return where;
    }

    public String buildWhereCalcoloSerieQuery(StringBuilder where, List<Long> selTables,
	    List<DecFiltroSelUd> filtri, List<DecFiltroSelUdDato> filtriUd,
	    DecTipoSerieUd tipoSerieUd, BigDecimal annoSerie, BigDecimal counterRicDatiSpec,
	    SerVerSerie verSerie) {
	String andClause = " AND ";
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);

	if (verSerie.getDtCreazioneUnitaDocDa() != null
		&& verSerie.getDtCreazioneUnitaDocA() != null) {
	    where.append(andClause).append("ud.dt_Creazione >= ").append("to_date('")
		    .append(df.format(verSerie.getDtCreazioneUnitaDocDa()))
		    .append("', 'dd/mm/yyyy hh24:mi:ss')").append(andClause)
		    .append("ud.dt_Creazione <= ").append("to_date('")
		    .append(df.format(verSerie.getDtCreazioneUnitaDocA()))
		    .append("', 'dd/mm/yyyy hh24:mi:ss')");
	}
	if (verSerie.getDtRegUnitaDocDa() != null && verSerie.getDtRegUnitaDocA() != null) {
	    where.append(andClause).append("ud.dt_Reg_Unita_Doc >= ").append("to_date('")
		    .append(df.format(verSerie.getDtRegUnitaDocDa()))
		    .append("', 'dd/mm/yyyy hh24:mi:ss')").append(andClause)
		    .append("ud.dt_Reg_Unita_Doc <= ").append("to_date('")
		    .append(df.format(verSerie.getDtRegUnitaDocA()))
		    .append("', 'dd/mm/yyyy hh24:mi:ss')");
	}

	where.append(context.getBusinessObject(SerieEjb.class).buildDatiSpecCalcoloQuery(filtriUd,
		"UNI_DOC", counterRicDatiSpec));

	StringBuilder appendFiltri = new StringBuilder();
	int counterFiltri = 0;
	if (!selTables.isEmpty()) {
	    for (Long idTipoDoc : selTables) {
		List<DecFiltroSelUdDato> filtriDatiSpecDoc = helper
			.getDecFiltroSelUdDato(tipoSerieUd.getIdTipoSerieUd(), idTipoDoc);
		if (!filtriDatiSpecDoc.isEmpty()) {
		    if (counterFiltri == 0) {
			appendFiltri.append(andClause).append("((");
			andClause = " OR ";
		    } else {
			appendFiltri.append(andClause).append("(");
		    }
		    appendFiltri.append(
			    context.getBusinessObject(SerieEjb.class).buildDatiSpecCalcoloQuery(
				    filtriDatiSpecDoc, "DOC", counterRicDatiSpec));
		    appendFiltri.append(") ");
		    counterFiltri++;
		}
	    }
	    appendFiltri.append(") ");
	}
	if (counterFiltri > 0) {
	    where.append(appendFiltri.toString());
	}
	return where.toString();
    }

    public String buildWhereInputSerieQuery(StringBuilder where, List<Long> selTables,
	    List<DecCampoInpUd> filtriDatoProfilo, List<DecCampoInpUd> filtriDatiSpec,
	    DecTipoSerieUd tipoSerieUd, BigDecimal annoSerie, BigDecimal counterRicDatiSpec) {
	String andClause = " AND ";
	for (DecCampoInpUd campo : filtriDatoProfilo) {
	    CostantiDB.NomeCampo nomeCampo = CostantiDB.NomeCampo.valueOf(campo.getNmCampo());
	    switch (nomeCampo) {
	    case TIPO_UNITA_DOC:
		where.append(andClause).append("tipoUd.nm_Tipo_Unita_Doc = '<")
			.append(campo.getNmCampo()).append(">'");
		break;
	    case REGISTRO:
		where.append(andClause).append("ud.cd_Registro_Key_Unita_Doc = '<")
			.append(campo.getNmCampo()).append(">'");
		break;
	    case ANNO:
		where.append(andClause).append("ud.aa_Key_Unita_Doc = <").append(campo.getNmCampo())
			.append(">");
		break;
	    case NUMERO:
		where.append(andClause).append("ud.cd_Key_Unita_Doc = '<")
			.append(campo.getNmCampo()).append(">'");
		break;
	    case DATA_REG:
		where.append(andClause).append("ud.dt_Reg_Unita_Doc = to_date('<")
			.append(campo.getNmCampo()).append(">', 'dd/mm/yyyy')");
		break;
	    case DATA_VERS:
		where.append(andClause).append("ud.dt_Creazione = to_date('<")
			.append(campo.getNmCampo()).append(">', 'dd/mm/yyyy')");
		break;
	    case TIPO_DOC_PRINC:
		where.append(andClause).append("tipoDocPrinc.nm_Tipo_Doc = '<")
			.append(campo.getNmCampo()).append(">'");
		break;
	    case OGGETTO:
		where.append(andClause).append("ud.dl_Oggetto_Unita_Doc = '<")
			.append(campo.getNmCampo()).append(">'");
		break;
	    default:
		throw new AssertionError(nomeCampo.name());
	    }
	}

	where.append(context.getBusinessObject(SerieEjb.class)
		.buildDatiSpecInputQuery(filtriDatiSpec, "UNI_DOC", counterRicDatiSpec));

	StringBuilder appendFiltri = new StringBuilder();
	int counterFiltri = 0;
	if (!selTables.isEmpty()) {
	    for (Long idTipoDoc : selTables) {
		List<DecCampoInpUd> filtriDatiSpecDoc = helper.getDecCampoInpUdDatiSpec(
			tipoSerieUd.getDecTipoSerie().getIdTipoSerie(), null, idTipoDoc);
		if (!filtriDatiSpecDoc.isEmpty()) {
		    if (counterFiltri == 0) {
			appendFiltri.append(andClause).append("((");
			andClause = " OR ";
		    } else {
			appendFiltri.append(andClause).append("(");
		    }
		    appendFiltri.append(context.getBusinessObject(SerieEjb.class)
			    .buildDatiSpecInputQuery(filtriDatiSpecDoc, "DOC", counterRicDatiSpec));
		    appendFiltri.append(") ");
		    counterFiltri++;
		}
	    }
	    appendFiltri.append(") ");
	}
	if (counterFiltri > 0) {
	    where.append(appendFiltri.toString());
	}

	return where.toString();
    }

    public String buildDatiSpecCalcoloQuery(List<DecFiltroSelUdDato> filtriUd, String tiEntitaSacer,
	    BigDecimal counterRicDatiSpec) {
	StringBuilder datiSpec = new StringBuilder();
	for (DecFiltroSelUdDato filtroUd : filtriUd) {
	    String tiOperString = filtroUd.getDecFiltroSelUdAttb().getTiOper();
	    CostantiDB.TipoOperatoreDatiSpec tiOper = CostantiDB.TipoOperatoreDatiSpec
		    .valueOf(tiOperString);
	    String descValore = filtroUd.getDecFiltroSelUdAttb().getDlValore() != null
		    ? filtroUd.getDecFiltroSelUdAttb().getDlValore().toUpperCase()
		    : null;
	    Long idAttrib = filtroUd.getDecAttribDatiSpec() != null
		    ? filtroUd.getDecAttribDatiSpec().getIdAttribDatiSpec()
		    : null;
	    this.buildDatoSpecSubQuery(tiOper, descValore, idAttrib, counterRicDatiSpec,
		    tiEntitaSacer, datiSpec);
	    counterRicDatiSpec = counterRicDatiSpec.add(BigDecimal.ONE);
	}
	return datiSpec.toString();
    }

    public String buildDatiSpecInputQuery(List<DecCampoInpUd> filtriCampi, String tiEntitaSacer,
	    BigDecimal counterRicDatiSpec) {
	StringBuilder datiSpec = new StringBuilder();
	for (DecCampoInpUd campo : filtriCampi) {
	    CostantiDB.TipoOperatoreDatiSpec tiOper = CostantiDB.TipoOperatoreDatiSpec.UGUALE;
	    String descValore = "<" + campo.getNmCampo() + ">";
	    Long idAttrib = campo.getDecAttribDatiSpec() != null
		    ? campo.getDecAttribDatiSpec().getIdAttribDatiSpec()
		    : null;
	    this.buildDatoSpecSubQuery(tiOper, descValore, idAttrib, counterRicDatiSpec,
		    tiEntitaSacer, datiSpec);
	    counterRicDatiSpec = counterRicDatiSpec.add(BigDecimal.ONE);
	}
	return datiSpec.toString();
    }

    private void buildDatoSpecSubQuery(CostantiDB.TipoOperatoreDatiSpec tiOper, String descValore,
	    Long idAttrib, BigDecimal counterRicDatiSpec, String tiEntitaSacer,
	    StringBuilder datiSpec) throws AssertionError {
	String operatore;
	String filtro = "";
	switch (tiOper) {
	case CONTIENE:
	    operatore = " like ";
	    filtro = "'%" + descValore + "%'";
	    break;
	case INIZIA_PER:
	    operatore = " like ";
	    filtro = "'" + descValore + "%'";
	    break;
	case DIVERSO:
	    operatore = " != ";
	    filtro = "'" + descValore + "'";
	    break;
	case MAGGIORE:
	    operatore = " > ";
	    filtro = "'" + descValore + "'";
	    break;
	case MAGGIORE_UGUALE:
	    operatore = " >= ";
	    filtro = "'" + descValore + "'";
	    break;
	case MINORE:
	    operatore = " < ";
	    filtro = "'" + descValore + "'";
	    break;
	case MINORE_UGUALE:
	    operatore = " <= ";
	    filtro = "'" + descValore + "'";
	    break;
	case NON_CONTIENE:
	    operatore = " not like ";
	    filtro = "'%" + descValore + "%'";
	    break;
	case NULLO:
	    operatore = " is null ";
	    break;
	case UGUALE:
	    operatore = " = ";
	    filtro = "'" + descValore + "'";
	    break;
	case NON_NULLO:
	    operatore = " is not null ";
	    break;
	case E_UNO_FRA:
	    operatore = " IN ";
	    String[] valori = descValore.split(",");
	    StringBuilder filter = new StringBuilder();
	    for (String valore : valori) {
		filter.append("'").append(valore).append("'").append(",");
	    }

	    filtro = "(" + StringUtils.chop(filter.toString()) + ")";
	    break;
	default:
	    throw new AssertionError(tiOper.name());
	}

	String tableSuffix = "_" + counterRicDatiSpec.toPlainString();
	if (tiEntitaSacer.equals("UNI_DOC")) {
	    datiSpec.append(" AND EXISTS (SELECT ricDatiSpec" + tableSuffix
		    + ".* from Aro_V_Ric_Dati_Spec ricDatiSpec" + tableSuffix + " "
		    + "WHERE ricDatiSpec" + tableSuffix + ".id_Unita_Doc = ud.id_Unita_Doc "
		    + "AND ricDatiSpec" + tableSuffix + ".ti_Entita_Sacer = 'UNI_DOC' "
		    + "AND ricDatiSpec" + tableSuffix + ".id_Attrib_Dati_Spec = " + idAttrib + " "
		    + "AND UPPER(ricDatiSpec" + tableSuffix + ".dl_Valore)" + operatore + filtro
		    + ")");
	} else if (tiEntitaSacer.equals("DOC")) {
	    if (counterRicDatiSpec.intValue() > 1) {
		datiSpec.append(" AND ");
	    }
	    datiSpec.append("EXISTS (SELECT ricDatiSpec" + tableSuffix
		    + ".* from Aro_V_Ric_Dati_Spec ricDatiSpec" + tableSuffix + " "
		    + "WHERE ricDatiSpec" + tableSuffix + ".id_Doc = docPrinc.id_Doc "
		    + "AND ricDatiSpec" + tableSuffix + ".ti_Entita_Sacer = 'DOC' "
		    + "AND ricDatiSpec" + tableSuffix + ".id_Attrib_Dati_Spec = " + idAttrib + " "
		    + "AND UPPER(ricDatiSpec" + tableSuffix + ".dl_Valore)" + operatore + filtro
		    + ")");
	}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="INPUT SERIE">
    public void inputSerie(long idUser, SerContenutoVerSerie contenuto) throws IOException {
	logger.debug(SerieEjb.class.getSimpleName()
		+ " INPUT ASYNC SERIE --- Ricerco i tipi di serie da calcolare");
	SerVerSerie verSerie = contenuto.getSerVerSerie();
	SerSerie serSerie = verSerie.getSerSerie();
	BigDecimal annoSerie = serSerie.getAaSerie();
	DecTipoSerie tipoSerie = serSerie.getDecTipoSerie();
	SerFileInputVerSerie fileInput = helper.getSerFileInputVerSerie(verSerie.getIdVerSerie(),
		CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name());
	List<DecTipoSerieUd> tipiSerieUd = helper.getDecTipoSerieUd(tipoSerie.getIdTipoSerie());
	logger.debug(SerieEjb.class.getSimpleName()
		+ " INPUT ASYNC SERIE --- Ottenuti i tipi di serie da calcolare");
	Map<Long, String> queryMap = new HashMap<>();
	if (tipiSerieUd.isEmpty()) {
	    throw new IllegalStateException(
		    "Errore inatteso nel calcolo della serie, non esistono record di associazioni registri - tipologie di unit\u00E0 documentarie per eseguire il calcolo");
	}

	for (DecTipoSerieUd tipoSerieUd : tipiSerieUd) {
	    // Preparazione query - ottengo in un qualche modo degli AroUnitaDoc
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " INPUT ASYNC SERIE --- Preparazione della query di selezione delle unit\u00E0 documentarie per il registro-tipoUd "
		    + tipoSerieUd.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc() + "-"
		    + tipoSerieUd.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
	    String query = this.wrapQuery(JobConstants.JobEnum.INPUT_SERIE, tipoSerieUd, annoSerie,
		    verSerie);
	    logger.debug(
		    SerieEjb.class.getSimpleName() + " INPUT ASYNC SERIE --- Query compilata ");
	    context.getBusinessObject(SerieEjb.class).saveQuery(query,
		    tipoSerieUd.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
		    tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
		    contenuto.getIdContenutoVerSerie());
	    queryMap.put(tipoSerieUd.getIdTipoSerieUd(), query);
	}

	List<DecCampoInpUd> campi = helper.getDecCampoInpUd(tipoSerie.getIdTipoSerie());
	List<String> csvRawRecords = new ArrayList<>();
	/* Recupero il CSVReader */
	CsvReader csvReader = new CsvReader(
		new ByteArrayInputStream(fileInput.getBlFileInputVerSerie().getBytes()),
		Charset.forName("UTF-8"));
	csvReader.setSkipEmptyRecords(true);
	int counterUd = 0;
	try {
	    if (csvReader.readHeaders()) {
		logger.debug(SerieEjb.class.getSimpleName()
			+ " INPUT ASYNC SERIE --- Eseguo il parsing del file csv");
		int index = 0;
		while (csvReader.readRecord()) {
		    csvRawRecords.add(csvReader.getRawRecord());
		    Map<String, CampiInputBean> mappaCampi = new HashMap<>();
		    for (DecCampoInpUd campo : campi) {
			String valoreCampo = csvReader.get(campo.getNmCampo());
			CampiInputBean bean = new CampiInputBean();
			bean.setNmCampo(campo.getNmCampo());
			bean.setPgOrdCampo(campo.getPgOrdCampo());
			bean.setTiTransformCampo(campo.getTiTrasformCampo());
			bean.setVlCampoRecord(valoreCampo);
			bean.setTipoCampo(CostantiDB.TipoCampo.valueOf(campo.getTiCampo()));

			if (campo.getTiTrasformCampo() != null && campo.getTiTrasformCampo().equals(
				CostantiDB.TipoTrasformatore.IN_NO_SEP_CHAR.getTransformString())) {
			    final Pattern p = Pattern
				    .compile("([0-9]{1,2})[\\W]([0-9]{1,2})[\\W]([0-9]{4})");
			    Matcher m = p.matcher(valoreCampo);
			    if (m.matches()) {
				String day = m.group(1);
				String month = m.group(2);
				String year = m.group(3);
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.parseInt(year));
				cal.set(Calendar.MONTH, (Integer.parseInt(month)) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
				bean.setVlCampoTransform(df.format(cal.getTime()));
			    } else {
				// FIXME: ??? se non matcha??
			    }
			} else {
			    bean.setVlCampoTransform(valoreCampo);
			}

			mappaCampi.put(campo.getNmCampo(), bean);
		    }

		    logger.debug(SerieEjb.class.getSimpleName()
			    + " INPUT ASYNC SERIE --- Eseguito il parsing del record --- procedo alla sostituzione dei campi nelle query");

		    List<SerErrFileInput> erroriList = new ArrayList<>();
		    List<ResultVCalcoloSerieUd> totaleRisultati = new ArrayList<>();
		    ResultInputSerieBean result = new ResultInputSerieBean();
		    for (Map.Entry<Long, String> entry : queryMap.entrySet()) {
			boolean campiNonValorizzatiError = false;
			String query = entry.getValue();

			Pattern tagPattern = Pattern.compile("<(\\w+[-\\w]*)>");
			Matcher m = tagPattern.matcher(query);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
			    String tagname = m.group(1);// tag
			    CampiInputBean substitute = mappaCampi.get(tagname);
			    if (substitute != null) {
				String toReplace = substitute.getVlCampoTransform().replace("'",
					"''");
				toReplace = toReplace.replace("\\", "\\\\");
				if (substitute.getTipoCampo()
					.equals(CostantiDB.TipoCampo.DATO_SPEC_UNI_DOC)
					|| substitute.getTipoCampo()
						.equals(CostantiDB.TipoCampo.DATO_SPEC_DOC_PRINC)) {
				    toReplace = toReplace.toUpperCase();
				}
				m.appendReplacement(sb, toReplace);
			    } else {
				// Non esiste un campo di sostituzione
				campiNonValorizzatiError = true;
				this.createSerErrFileInput(
					CostantiDB.TipoErroreFileInputSerie.CAMPI_NON_VALORIZZATI
						.name(),
					new BigDecimal(index), csvReader.getRawRecord(),
					erroriList);
				logger.error(SerieEjb.class.getSimpleName()
					+ " INPUT ASYNC SERIE --- Errore inaspettato nella sostituzione dei parametri nella query: il tag "
					+ tagname + " non ha un parametro per sostituirlo");
				logger.info(SerieEjb.class.getSimpleName()
					+ " INPUT ASYNC SERIE --- Passo alla query successiva");
				break;
			    }
			}

			if (!campiNonValorizzatiError) {
			    m.appendTail(sb);
			    logger.debug(SerieEjb.class.getSimpleName()
				    + " INPUT ASYNC SERIE --- Campi sostituiti con successo, eseguo la query");
			    List<ResultVCalcoloSerieUd> resultQueryList = helper
				    .executeQueryListWithException(sb.toString());
			    if (resultQueryList != null) {
				totaleRisultati.addAll(resultQueryList);
			    } else {
				// risultato null - Mi sono beccato una SQLException
				this.createSerErrFileInput(
					CostantiDB.TipoErroreFileInputSerie.SQL_ERRATA.name(),
					new BigDecimal(index), csvReader.getRawRecord(),
					erroriList);
				logger.error(SerieEjb.class.getSimpleName()
					+ " INPUT ASYNC SERIE --- L'esecuzione della query ha causato un'eccezione");
				totaleRisultati = null;
				break;
			    }
			}
		    }

		    result.setIndexRecord(new BigDecimal(index + 1));
		    result.setRecord(csvRawRecords.get(index));
		    result.setResultQueryList(totaleRisultati);

		    counterUd += context.getBusinessObject(SerieEjb.class).handleQueryResult(
			    contenuto.getIdContenutoVerSerie(), fileInput.getIdFileInputVerSerie(),
			    result, erroriList);
		    index++;
		}
	    } else {
		throw new IllegalStateException(
			"Errore imprevisto nella lettura del file caricato in fase di creazione serie. Presumibilmente file non in formato csv.");
	    }
	} finally {
	    csvReader.close();
	}

	context.getBusinessObject(SerieEjb.class).handleAroUdAppartVerSerie(idUser, contenuto,
		counterUd, "Acquisizione serie", false, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int handleQueryResult(Long idContenutoVerSerie, long idFileInputVerSerie,
	    ResultInputSerieBean result, List<SerErrFileInput> erroriList) {
	SerFileInputVerSerie fileInputVerSerie = helper.findById(SerFileInputVerSerie.class,
		idFileInputVerSerie);
	SerContenutoVerSerie contenuto = helper.findById(SerContenutoVerSerie.class,
		idContenutoVerSerie);
	int counter = 0;
	if (result.getResultQueryList() != null) {
	    if (result.getResultQueryList().isEmpty()) {
		logger.info(SerieEjb.class.getSimpleName()
			+ " INPUT ASYNC SERIE --- Errore, nessuna UD");
		this.createSerErrFileInput(CostantiDB.TipoErroreFileInputSerie.NO_UD.name(),
			result.getIndexRecord(), result.getRecord(), erroriList);
	    } else if (result.getResultQueryList().size() > 1) {
		logger.info(SerieEjb.class.getSimpleName()
			+ " INPUT ASYNC SERIE --- Errore, troppe UD");
		SerErrFileInput errFileInput = this.createSerErrFileInput(
			CostantiDB.TipoErroreFileInputSerie.TROPPE_UD.name(),
			result.getIndexRecord(), result.getRecord(), erroriList);
		this.createSerUdErrFileInput(errFileInput, result.getResultQueryList());
	    } else {
		ResultVCalcoloSerieUd resultRecord = result.getResultQueryList().get(0);
		if (!helper.existUdInSerie(contenuto.getIdContenutoVerSerie(),
			resultRecord.getIdUnitaDoc())) {
		    counter++;
		    logger.info(SerieEjb.class.getSimpleName()
			    + " INPUT ASYNC SERIE --- Registra l'appartenenza dell'ud al contenuto");
		    context.getBusinessObject(SerieEjb.class).handleAroUnitaDoc(idContenutoVerSerie,
			    resultRecord);
		} else {
		    logger.info(SerieEjb.class.getSimpleName()
			    + " INPUT ASYNC SERIE --- Errore, UD doppia");
		    SerErrFileInput errFileInput = this.createSerErrFileInput(
			    CostantiDB.TipoErroreFileInputSerie.UD_DOPPIA.name(),
			    result.getIndexRecord(), result.getRecord(), erroriList);
		    this.createSerUdErrFileInput(errFileInput, result.getResultQueryList());
		}
	    }
	}
	logger.debug(SerieEjb.class.getSimpleName()
		+ " INPUT ASYNC SERIE --- Eseguo la persist di ogni errore riscontrato");
	for (SerErrFileInput errFileInput : erroriList) {
	    fileInputVerSerie.addSerErrFileInput(errFileInput);
	}
	return counter;
    }

    private SerErrFileInput createSerErrFileInput(String tiErrRec, BigDecimal indexRecord,
	    String recordString, List<SerErrFileInput> erroriList) {
	SerErrFileInput errFileInput = new SerErrFileInput();
	errFileInput.setTiErrRec(tiErrRec);
	errFileInput.setNiRecErr(indexRecord);
	errFileInput.setDsRecErr(recordString);
	if (errFileInput.getSerUdErrFileInputs() == null) {
	    errFileInput.setSerUdErrFileInputs(new ArrayList<SerUdErrFileInput>());
	}

	erroriList.add(errFileInput);
	return errFileInput;
    }

    private void createSerUdErrFileInput(SerErrFileInput errFileInput,
	    List<ResultVCalcoloSerieUd> udList) {
	for (ResultVCalcoloSerieUd ud : udList) {
	    SerUdErrFileInput udErrFileInput = new SerUdErrFileInput();
	    udErrFileInput.setAroUnitaDoc(helper.findById(AroUnitaDoc.class, ud.getIdUnitaDoc()));
	    udErrFileInput.setCdUdSerie(ud.getKeyUdSerie());
	    udErrFileInput.setDsKeyOrdUdSerie(ud.getDsKeyOrdUdSerie());
	    udErrFileInput.setDtUdSerie(ud.getDtUdSerie());
	    udErrFileInput.setInfoUdSerie(ud.getInfoUdSerie());
	    udErrFileInput.setPgUdSerie(ud.getPgUdSerie());

	    errFileInput.addSerUdErrFileInput(udErrFileInput);
	}
    }
    // </editor-fold>

    /*
     * *** RICERCA ONLINE ***
     */
    public SerVRicSerieUdTableBean getSerVRicSerieUdTableBean(long idUserIam,
	    RicercaSerieBean filtri) {
	SerVRicSerieUdTableBean table = new SerVRicSerieUdTableBean();
	List<SerVRicSerieUd> list = helper.getSerVRicSerieUd(idUserIam, filtri);
	if (list != null && !list.isEmpty()) {
	    try {
		for (SerVRicSerieUd record : list) {
		    SerVRicSerieUdRowBean row = (SerVRicSerieUdRowBean) Transform
			    .entity2RowBean(record);
		    row.setString("amb_ente_strut", record.getNmAmbiente() + " - "
			    + record.getNmEnte() + " - " + record.getNmStrut());
		    row.setString("ti_crea_nm_modello",
			    (StringUtils.isNotBlank(record.getTiCreaStandard())
				    ? record.getTiCreaStandard()
				    : "")
				    + (StringUtils.isNotBlank(record.getNmModelloTipoSerie())
					    ? " ( " + record.getNmModelloTipoSerie() + " )"
					    : ""));
		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della traccia delle versioni serie"
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    public SerVRicSerieUdUsrTableBean getSerVRicSerieUdUsrTableBean(long idUserIam,
	    RicercaSerieBean filtri) {
	SerVRicSerieUdUsrTableBean table = new SerVRicSerieUdUsrTableBean();
	List<SerVRicSerieUdUsr> list = helper.getSerVRicSerieUdUsr(idUserIam, filtri);
	if (list != null && !list.isEmpty()) {
	    try {
		for (SerVRicSerieUdUsr record : list) {
		    SerVRicSerieUdUsrRowBean row = (SerVRicSerieUdUsrRowBean) Transform
			    .entity2RowBean(record);
		    row.setString("amb_ente_strut", record.getNmAmbiente() + " - "
			    + record.getNmEnte() + " - " + record.getNmStrut());
		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero delle serie"
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    public SerVRicSerieUdTableBean getSerVRicSerieUdTableBean(List<BigDecimal> verSeries,
	    BigDecimal idStrut) {
	SerVRicSerieUdTableBean table = new SerVRicSerieUdTableBean();
	List<SerVRicSerieUd> list = helper.getSerVRicSerieUd(verSeries, idStrut);
	if (list != null && !list.isEmpty()) {
	    try {
		for (SerVRicSerieUd record : list) {
		    SerVRicSerieUdRowBean row = (SerVRicSerieUdRowBean) Transform
			    .entity2RowBean(record);
		    row.setString("amb_ente_strut", record.getNmAmbiente() + " - "
			    + record.getNmEnte() + " - " + record.getNmStrut());
		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della traccia delle versioni serie"
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    /*
     * *** DETTAGLIO ONLINE ***
     */
    public SerVVisSerieUdRowBean getSerVVisSerieUdRowBean(BigDecimal idVerSerie) {
	SerVVisSerieUd serie = helper.getSerVVisSerieUd(idVerSerie);
	SerVVisSerieUdRowBean row = null;
	if (serie != null) {
	    try {
		row = (SerVVisSerieUdRowBean) Transform.entity2RowBean(serie);

		// MEV#15967 - Attivazione della firma Xades e XadesT
		SerFileVerSerie fi = helper.getFileVerSerieByTipoFile(idVerSerie,
			CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO);
		if (fi != null) {
		    row.setString("ti_firma", fi.getTiFirma());
		}
		//

		XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();

		if (StringUtils.isNotBlank(row.getBlFileIxAip())) {

		    String xmlFormatted = formatter.prettyPrintWithDOM3LS(
			    new String(serie.getBlFileIxAip(), Charset.forName("UTF-8")));
		    row.setBlFileIxAip(xmlFormatted);
		} else {
		    // MEV#30400
		    try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// recupero documento blob vs obj storage
			// build dto per recupero
			RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
				Constants.TiEntitaSacerObjectStorage.INDICE_AIP_SERIE,
				row.getIdVerSerie().longValue(), baos,
				RecBlbOracle.TabellaBlob.SER_FILE_VER_SERIE,
				TipoFileVerSerie.IX_AIP_UNISINCRO.name());
			// recupero
			boolean esitoRecupero = recuperoDocumento
				.callRecuperoDocSuStream(csRecuperoDoc);
			if (!esitoRecupero) {
			    throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
			}
			String xmlIndice = formatter.prettyPrintWithDOM3LS(
				baos.toString(StandardCharsets.UTF_8.displayName()));
			row.setBlFileIxAip(xmlIndice);
		    } catch (IOException ex) {
			logger.error(
				"Errore durante il recupero dell'indice aip della serie con id = "
					+ row.getIdSerie() + " "
					+ ExceptionUtils.getRootCauseMessage(ex),
				ex);
		    }
		    // end MEV#30400
		}
		// EVO#16486
		// Per ogni urn, popolo i campi urn originale e urn normalizzato, ricavandoli da
		// SER_URN_FILE_VER_SERIE
		List<SerUrnFileVerSerie> urnFileVerSerieList = helper.getUrnFileVerSerieByTipiUrn(
			idVerSerie,
			Arrays.asList(TiUrnFileVerSerie.ORIGINALE, TiUrnFileVerSerie.NORMALIZZATO));
		// filtro la lista in base al tipo file ('MARCA_IX_AIP_UNISINCRO',
		// 'IX_AIP_UNISINCRO_FIRMATO',
		// 'IX_AIP_UNISINCRO')
		List<SerUrnFileVerSerie> urnFileVerSerieFilteredList = (List<SerUrnFileVerSerie>) CollectionUtils
			.select(urnFileVerSerieList, new Predicate() {
			    @Override
			    public boolean evaluate(final Object object) {
				return "MARCA_IX_AIP_UNISINCRO".equals(((SerUrnFileVerSerie) object)
					.getSerFileVerSerie().getTiFileVerSerie());
			    }
			});
		if (urnFileVerSerieFilteredList.isEmpty()) {
		    urnFileVerSerieFilteredList = (List<SerUrnFileVerSerie>) CollectionUtils
			    .select(urnFileVerSerieList, new Predicate() {
				@Override
				public boolean evaluate(final Object object) {
				    return "IX_AIP_UNISINCRO_FIRMATO"
					    .equals(((SerUrnFileVerSerie) object)
						    .getSerFileVerSerie().getTiFileVerSerie());
				}
			    });
		}
		if (urnFileVerSerieFilteredList.isEmpty()) {
		    urnFileVerSerieFilteredList = (List<SerUrnFileVerSerie>) CollectionUtils
			    .select(urnFileVerSerieList, new Predicate() {
				@Override
				public boolean evaluate(final Object object) {
				    return "IX_AIP_UNISINCRO".equals(((SerUrnFileVerSerie) object)
					    .getSerFileVerSerie().getTiFileVerSerie());
				}
			    });
		}
		for (SerUrnFileVerSerie urnFileVerSerie : urnFileVerSerieFilteredList) {
		    switch (urnFileVerSerie.getTiUrn()) {
		    case ORIGINALE:
			row.setString("ds_urn_aip_serie", urnFileVerSerie.getDsUrn());
			break;
		    case NORMALIZZATO:
			row.setString("ds_urn_normaliz_aip_serie", urnFileVerSerie.getDsUrn());
			break;
		    default:
			break;
		    }
		}
		// end EVO#16486
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero della serie");
	    }
	}
	return row;
    }

    public String[] getFlContenutoBloccato(String nmJob, BigDecimal idContenutoVerSerie) {
	SerVJobContenutoBloccato view = helper.getSerVJobContenutoBloccato(nmJob,
		idContenutoVerSerie);
	String[] result = null;
	if (view != null) {
	    result = new String[] {
		    view.getFlJobBloccato(), view.getDlMsgJobBloccato() };
	}
	return result;
    }

    public String[] getFlVersioneBloccata(String nmJob, BigDecimal idVerSerie) {
	SerVJobVerSerieBloccato view = helper.getSerVJobVerSerieBloccato(nmJob, idVerSerie);
	String[] result = null;
	if (view != null) {
	    result = new String[] {
		    view.getFlJobBloccato(), view.getDlMsgJobBloccato() };
	}
	return result;
    }

    public boolean checkCsvHeaders(byte[] fileByteArray, BigDecimal idTipoSerie)
	    throws IOException, ParerUserError {
	boolean result = true;
	if (idTipoSerie == null) {
	    throw new ParerUserError("Tipo serie non rilevato per l'acquisizione del file");
	}
	List<DecCampoInpUd> list = helper.getDecCampoInpUd(idTipoSerie.longValue());
	if (list.isEmpty()) {
	    throw new ParerUserError(
		    "Per il tipo serie non sono definite le regole di acquisizione file");
	}
	/* Recupero il CSVReader */
	CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray),
		Charset.forName("UTF-8"));
	csvReader.setSkipEmptyRecords(true);
	try {
	    if (csvReader.readHeaders()) {
		List<String> headers = Arrays.asList(csvReader.getHeaders());
		for (DecCampoInpUd campo : list) {
		    String nmCampo = campo.getNmCampo();
		    if (!headers.contains(nmCampo)) {
			result = false;
			break;
		    }
		}
	    }
	} finally {
	    csvReader.close();
	}
	return result;
    }

    public SerVLisNotaSerieTableBean getSerVLisNotaSerieTableBean(BigDecimal idVerSerie) {
	SerVLisNotaSerieTableBean table = new SerVLisNotaSerieTableBean();
	List<SerVLisNotaSerie> list = helper.getSerVLisNotaSerie(idVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisNotaSerieTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista note della serie di unit\u00e0 documentarie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVLisStatoSerieTableBean getSerVLisStatoSerieTableBean(BigDecimal idVerSerie) {
	SerVLisStatoSerieTableBean table = new SerVLisStatoSerieTableBean();
	List<SerVLisStatoSerie> list = helper.getSerVLisStatoSerie(idVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisStatoSerieTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista stati della serie di unit\u00e0 documentarie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVLisVerSeriePrecTableBean getSerVLisVerSeriePrecTableBean(BigDecimal idVerSerie) {
	SerVLisVerSeriePrecTableBean table = new SerVLisVerSeriePrecTableBean();
	List<SerVLisVerSeriePrec> list = helper.getSerVLisVerSeriePrec(idVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisVerSeriePrecTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista versioni della serie di unit\u00e0 documentarie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVLisVolSerieUdTableBean getSerVLisVolSerieUdTableBean(BigDecimal idVerSerie) {
	SerVLisVolSerieUdTableBean table = new SerVLisVolSerieUdTableBean();
	List<SerVLisVolSerieUd> list = helper.getSerVLisVolSerieUd(idVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisVolSerieUdTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista versioni della serie di unit\u00e0 documentarie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public boolean checkExistNoteObblig(BigDecimal idVerSerie) {
	SerVChkNotaObblig check = helper.findViewById(SerVChkNotaObblig.class, idVerSerie);
	return (check.getFlOkNoteObblig().equals("1"));
    }

    public boolean checkStatoConservazioneUdInContenEff(BigDecimal idVerSerie) {
	SerVChkConservazioneUd check = helper.findViewById(SerVChkConservazioneUd.class,
		idVerSerie);
	return (check.getFlOkStatoConservazione().equals("1"));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSerie(BigDecimal idSerie, BigDecimal idVerSerie, String cdComposito,
	    String descSerie, BigDecimal anniConserv, Date dtInizioSelSerie, Date dtFineSelSerie,
	    String dsListaAnniSelSerie, String tiPeriodoSelSerie, BigDecimal niPeriodoSelSerie)
	    throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    context.getBusinessObject(SerieEjb.class).checkStatoSerie(idVerSerie,
		    CostantiDB.StatoVersioneSerie.APERTA.name(),
		    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name());

	    serie.setCdCompositoSerie(cdComposito);
	    serie.setDsSerie(descSerie);
	    serie.setNiAnniConserv(anniConserv);

	    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
	    verSerie.setDtInizioSelSerie(dtInizioSelSerie);
	    verSerie.setDtFineSelSerie(dtFineSelSerie);
	    verSerie.setDsListaAnniSelSerie(dsListaAnniSelSerie);
	    verSerie.setTiPeriodoSelSerie(tiPeriodoSelSerie);
	    verSerie.setNiPeriodoSelSerie(niPeriodoSelSerie);
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteSerVerSerie(long idUser, BigDecimal idSerie, BigDecimal idVerSerie)
	    throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    SerVRicSerieUd view = helper.findViewById(SerVRicSerieUd.class, idVerSerie);
	    if (view != null) {
		SerVVisSerieUd vistaSerie = helper.findViewById(SerVVisSerieUd.class, idVerSerie);

		if (!view.getTiStatoVerSerie()
			.equals(CostantiDB.StatoVersioneSerie.APERTURA_IN_CORSO.name())
			&& !view.getTiStatoVerSerie()
				.equals(CostantiDB.StatoVersioneSerie.APERTA.name())
			&& !view.getTiStatoVerSerie()
				.equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
		    throw new ParerUserError(
			    "La serie non \u00E8 eliminabile perch\u00E9 lo stato \u00E8 diverso da APERTURA_IN_CORSO, APERTA e DA_CONTROLLARE");
		} else if (view.getTiStatoVerSerie()
			.equals(CostantiDB.StatoVersioneSerie.APERTA.name())
			|| view.getTiStatoVerSerie()
				.equals(CostantiDB.StatoVersioneSerie.APERTURA_IN_CORSO.name())) {
		    if (!context.getBusinessObject(SerieEjb.class).checkContenuto(idVerSerie, true,
			    true, false, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
			    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
			    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
			boolean throwErr = true;
			if (vistaSerie.getTiStatoContenutoCalc() != null) {
			    if (vistaSerie.getTiStatoContenutoCalc().equals(
				    CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.CALCOLO_SERIE.name(),
						vistaSerie.getIdContenutoCalc());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    } else if (vistaSerie.getTiStatoContenutoCalc().equals(
				    CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO
					    .name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD
							.name(),
						vistaSerie.getIdContenutoCalc());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    }
			}
			if (vistaSerie.getTiStatoContenutoAcq() != null) {
			    if (vistaSerie.getTiStatoContenutoAcq().equals(
				    CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.INPUT_SERIE.name(),
						vistaSerie.getIdContenutoAcq());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    } else if (vistaSerie.getTiStatoContenutoAcq().equals(
				    CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO
					    .name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD
							.name(),
						vistaSerie.getIdContenutoAcq());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    }
			}
			if (throwErr) {
			    throw new ParerUserError(
				    "La serie non \u00E8 eliminabile perch\u00E9 per almeno un suo contenuto \u00E8 in corso la creazione o il controllo");
			}
		    }
		} else if (view.getTiStatoVerSerie()
			.equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
		    if (!context.getBusinessObject(SerieEjb.class).checkContenuto(idVerSerie, false,
			    false, true, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
			    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
			boolean throwErr = true;
			if (vistaSerie.getTiStatoContenutoEff() != null) {
			    if (vistaSerie.getTiStatoContenutoEff().equals(
				    CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD
							.name(),
						vistaSerie.getIdContenutoEff());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    } else if (vistaSerie.getTiStatoContenutoEff().equals(
				    CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO
					    .name())) {
				String[] blocco = context.getBusinessObject(SerieEjb.class)
					.getFlContenutoBloccato(
						JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD
							.name(),
						vistaSerie.getIdContenutoEff());
				if (blocco != null) {
				    if (blocco[0].equals("1")) {
					throwErr = false;
				    }
				}
			    }
			}
			if (throwErr) {
			    throw new ParerUserError(
				    "La serie non \u00E8 eliminabile perch\u00E9 \u00E8 in corso la generazione del contenuto di tipo EFFETTIVO o il suo controllo");
			}
		    }
		}
	    } else {
		throw new ParerUserError(
			"La serie non \u00E8 eliminabile perch\u00E9 la versione visualizzata non \u00E8 quella corrente");
	    }
	    try {
		boolean deleteSerie = serie.getSerVerSeries().size() == 1;
		SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
		helper.removeEntity(verSerie, false);
		if (deleteSerie) {
		    helper.removeEntity(serie, true);
		} else {
		    SerStatoSerie statoSerie;
		    Date now = Calendar.getInstance().getTime();
		    logger.debug(
			    SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della serie");
		    SerStatoSerie lastStatoSerie = helper.getLastSerStatoSerie(serie.getIdSerie());
		    BigDecimal pg = lastStatoSerie != null
			    ? lastStatoSerie.getPgStatoSerie().add(BigDecimal.ONE)
			    : BigDecimal.ONE;
		    statoSerie = context.getBusinessObject(SerieEjb.class).createSerStatoSerie(pg,
			    CostantiDB.StatoConservazioneSerie.ANNULLATA.name(),
			    "Eliminazione della versione serie corrente",
			    "Eliminazione la versione corrente della serie", idUser, now,
			    serie.getIdSerie());
		    logger.debug(SerieEjb.class.getSimpleName()
			    + " --- Eseguo la persist del nuovo stato serie");
		    helper.insertEntity(statoSerie, false);
		    serie.setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));
		    serie.setDtAnnul(now);
		}
	    } catch (Exception e) {
		String messaggio = "Eccezione imprevista nell'eliminazione della versione serie: ";
		messaggio += ExceptionUtils.getRootCauseMessage(e);
		logger.error(messaggio, e);
		throw new ParerUserError(messaggio);
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    public SerVVisVolVerSerieUdRowBean getSerVVisVolSerieUdRowBean(BigDecimal idVolVerSerie) {
	SerVVisVolVerSerieUd volume = helper.getSerVVisVolVerSerieUd(idVolVerSerie);
	SerVVisVolVerSerieUdRowBean row = null;
	if (volume != null) {
	    try {
		row = (SerVVisVolVerSerieUdRowBean) Transform.entity2RowBean(volume);
		if (StringUtils.isNotBlank(row.getBlIxVol())) {
		    XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
		    String xmlFormatted = formatter.prettyPrintWithDOM3LS(row.getBlIxVol());
		    row.setBlIxVol(xmlFormatted);
		}
		// EVO#16486
		// Per ogni urn, popolo i campi urn originale e urn normalizzato, ricavandoli da
		// SER_URN_IX_VOL_VER_SERIE
		List<SerUrnIxVolVerSerie> urnIxVolVerSerieList = helper
			.getUrnIxVolVerSerieByTipiUrn(idVolVerSerie, Arrays.asList(
				TiUrnIxVolVerSerie.ORIGINALE, TiUrnIxVolVerSerie.NORMALIZZATO));
		for (SerUrnIxVolVerSerie urnIxVolVerSerie : urnIxVolVerSerieList) {
		    switch (urnIxVolVerSerie.getTiUrn()) {
		    case ORIGINALE:
			row.setString("ds_urn_aip_vol", urnIxVolVerSerie.getDsUrn());
			break;
		    case NORMALIZZATO:
			row.setString("ds_urn_normaliz_aip_vol", urnIxVolVerSerie.getDsUrn());
			break;
		    default:
			break;
		    }
		}
		// end EVO#16486
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero del volume serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero del volume serie");
	    }
	}
	return row;
    }

    /* DETTAGLIO CONTENUTO */
    public void cleanContenutoSerie(BigDecimal idSerie, BigDecimal idVerSerie, String tipoContenuto)
	    throws ParerUserError {
	cleanContenutoSerie(idSerie, idVerSerie, tipoContenuto, false, null, null, null, null,
		null);
    }

    public void cleanContenutoSerie(BigDecimal idSerie, BigDecimal idVerSerie,
	    String cdDocFileInputVerSerie, String dsDocFileInputVerSerie, String flFornitoEnte,
	    byte[] file, long idUser) throws ParerUserError {
	cleanContenutoSerie(idSerie, idVerSerie, CostantiDB.TipoContenutoVerSerie.ACQUISITO.name(),
		true, cdDocFileInputVerSerie, dsDocFileInputVerSerie, flFornitoEnte, file, idUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cleanContenutoSerie(BigDecimal idSerie, BigDecimal idVerSerie, String tipoContenuto,
	    boolean ricreaFileInput, String cdDocFileInputVerSerie, String dsDocFileInputVerSerie,
	    String flFornitoEnte, byte[] file, Long idUser) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    try {
		SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
		if (tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.ACQUISITO.name())
			&& ricreaFileInput) {
		    logger.info("Elimino i file di tipo ACQUISITO");
		    helper.deleteSerFileInputVerSerie(idVerSerie.longValue(),
			    CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name());
		    logger.info("Creo i file di tipo ACQUISITO");
		    IamUser user = helper.findById(IamUser.class, idUser);
		    context.getBusinessObject(SerieEjb.class).createSerFileInputVerSerie(file,
			    cdDocFileInputVerSerie, dsDocFileInputVerSerie, flFornitoEnte,
			    CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name(), user,
			    verSerie);
		}
		Date now = Calendar.getInstance().getTime();
		logger.info("Elimino i contenuti di tipo " + tipoContenuto);
		helper.deleteSerContenutoVerSerie(idVerSerie.longValue(), tipoContenuto);
		context.getBusinessObject(SerieEjb.class).createSerContenutoVerSerie(verSerie,
			tipoContenuto, now);
	    } catch (Exception e) {
		String messaggio = "Eccezione imprevista nell'inizializzazione del calcolo serie";
		messaggio += ExceptionUtils.getRootCauseMessage(e);
		logger.error(messaggio, e);
		throw new ParerUserError(messaggio);
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    public boolean checkVersione(MessageBox messageBox, BigDecimal idVerSerie,
	    String... statiVersione) {
	boolean result = true;
	try {
	    checkStatoSerie(idVerSerie, statiVersione);
	} catch (ParerUserError ex) {
	    handleParerUserError(messageBox, ex);
	    result = false;
	}
	return result;
    }

    public boolean checkVersione(BigDecimal idVerSerie, String... statiVersione) {
	return checkVersione(null, idVerSerie, statiVersione);
    }

    public void checkStatoSerie(BigDecimal idVerSerie, String... statiVersione)
	    throws ParerUserError {
	SerVRicSerieUd serie = helper.findViewById(SerVRicSerieUd.class, idVerSerie);
	if (serie != null) {
	    boolean errorStato = true;
	    StringBuilder errorMsg = new StringBuilder("La serie ha stato corrente diverso da ");
	    if (statiVersione != null) {
		for (String stato : statiVersione) {
		    errorMsg.append(stato).append(",");
		    if (serie.getTiStatoVerSerie().equals(stato)) {
			errorStato = false;
		    }
		}
		if (errorStato) {
		    throw new ParerUserError(StringUtils.chop(errorMsg.toString()));
		}
	    }
	} else {
	    throw new ParerUserError("La versione visualizzata non \u00E8 quella corrente");
	}
    }

    public boolean existContenuto(BigDecimal idVerSerie, String tipoContenuto) {
	boolean result = false;
	if (helper.getSerContenutoVerSerie(idVerSerie.longValue(), tipoContenuto) != null) {
	    result = true;
	}
	return result;
    }

    public boolean checkContenuto(MessageBox messageBox, BigDecimal idVerSerie,
	    boolean checkContenutoCalc, boolean checkContenutoAcq, boolean checkContenutoEff,
	    String... statoContenuto) {
	boolean result = true;
	try {
	    checkStatoContenutoSerie(idVerSerie, checkContenutoCalc, checkContenutoAcq,
		    checkContenutoEff, statoContenuto);
	} catch (ParerUserError ex) {
	    handleParerUserError(messageBox, ex);
	    result = false;
	}
	return result;
    }

    private void handleParerUserError(MessageBox messageBox, ParerUserError ex) {
	if (messageBox != null) {
	    messageBox.addError(ex.getDescription());
	}
	logger.error(ex.getDescription());
    }

    public boolean checkContenuto(BigDecimal idVerSerie, boolean checkContenutoCalc,
	    boolean checkContenutoAcq, boolean checkContenutoEff, String... statoContenuto) {
	return checkContenuto(null, idVerSerie, checkContenutoCalc, checkContenutoAcq,
		checkContenutoEff, statoContenuto);
    }

    public void checkStatoContenutoSerie(BigDecimal idVerSerie, boolean checkContenutoCalc,
	    boolean checkContenutoAcq, boolean checkContenutoEff, String... statoContenuto)
	    throws ParerUserError {
	SerVVisSerieUd serie = helper.findViewById(SerVVisSerieUd.class, idVerSerie);
	List<String> statiContenuto = Arrays.asList(statoContenuto);
	StringBuilder errorMsg = new StringBuilder();

	if ((checkContenutoCalc && checkContenutoAcq) || (checkContenutoCalc && checkContenutoEff)
		|| (checkContenutoAcq && checkContenutoEff)
		|| (checkContenutoCalc && checkContenutoAcq && checkContenutoEff)) {
	    errorMsg.append("Uno o pi\u00F9 contenuti tra quelli scelti hanno stato diverso da ");
	} else if (checkContenutoCalc) {
	    errorMsg.append("Il contenuto di tipo CALCOLATO ha stato diverso da ");
	} else if (checkContenutoAcq) {
	    errorMsg.append("Il contenuto di tipo ACQUISITO ha stato diverso da ");
	} else if (checkContenutoEff) {
	    errorMsg.append("Il contenuto di tipo EFFETTIVO ha stato diverso da ");
	}
	boolean errorCalc = true;
	boolean errorAcq = true;
	boolean errorEff = true;

	for (String stato : statiContenuto) {
	    errorMsg.append(stato).append(",");
	    if (checkContenutoCalc && ((serie.getTiStatoContenutoCalc() == null)
		    || (serie.getTiStatoContenutoCalc().equals(stato)))) {
		errorCalc = false;
	    }
	    if (checkContenutoAcq && ((serie.getTiStatoContenutoAcq() == null)
		    || (serie.getTiStatoContenutoAcq().equals(stato)))) {
		errorAcq = false;
	    }
	    if (checkContenutoEff && ((serie.getTiStatoContenutoEff() == null)
		    || (serie.getTiStatoContenutoEff().equals(stato)))) {
		errorEff = false;
	    }
	}
	String msg = StringUtils.chop(errorMsg.toString());

	if (checkContenutoCalc && errorCalc) {
	    throw new ParerUserError(msg);
	} else if (checkContenutoAcq && errorAcq) {
	    throw new ParerUserError(msg);
	} else if (checkContenutoEff && errorEff) {
	    throw new ParerUserError(msg);
	}

    }

    /* DETTAGLIO CONTENUTO */
    public SerVVisContenutoSerieUdRowBean getSerVVisContenutoSerieUdRowBean(
	    BigDecimal idContenutoVerSerie, String tipoContenuto) {
	SerVVisContenutoSerieUd contenuto = helper.findViewById(SerVVisContenutoSerieUd.class,
		idContenutoVerSerie);
	SerVVisContenutoSerieUdRowBean row = null;
	if (contenuto != null) {
	    try {
		row = (SerVVisContenutoSerieUdRowBean) Transform.entity2RowBean(contenuto);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero del contenuto serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException("Errore durante il recupero del contenuto serie");
	    }
	}
	return row;
    }

    public SerVLisUdAppartSerieTableBean getSerVLisUdAppartSerieTableBean(
	    BigDecimal idContenutoVerSerie, RicercaUdAppartBean parametri) {
	return helper.getSerVLisUdAppartSerie(idContenutoVerSerie, parametri,
		list -> getSerVLisUdAppartSerieTableBeanFrom(list), true);
    }

    public SerVLisUdAppartSerieTableBean getSerVLisUdAppartSerieTableBeanForDownload(
	    BigDecimal idContenutoVerSerie, RicercaUdAppartBean parametri) {
	return helper.getSerVLisUdAppartSerie(idContenutoVerSerie, parametri,
		list -> getSerVLisUdAppartSerieTableBeanFrom(list), false);
    }

    private SerVLisUdAppartSerieTableBean getSerVLisUdAppartSerieTableBeanFrom(
	    List<SerVLisUdAppartSerie> list) {
	SerVLisUdAppartSerieTableBean table = new SerVLisUdAppartSerieTableBean();
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisUdAppartSerieTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista unit\u00e0 documentarie appartenenti alla serie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVLisErrContenSerieUdTableBean getSerVLisErrContenSerieUdTableBean(
	    BigDecimal idContenutoVerSerie) {
	SerVLisErrContenSerieUdTableBean table = new SerVLisErrContenSerieUdTableBean();
	List<SerVLisErrContenSerieUd> list = helper.getSerVLisErrContenSerieUd(idContenutoVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisErrContenSerieUdTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della lista errori contenuto della serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    public SerVLisErrFileSerieUdTableBean getSerVLisErrFileSerieUdTableBean(BigDecimal idVerSerie,
	    String tiScopoFileInputVerSerie) {
	return helper.getSerVLisErrFileSerieUd(idVerSerie, tiScopoFileInputVerSerie,
		list -> getSerVLisErrFileSerieUdTableBeanFrom(list), true);
    }

    public SerVLisErrFileSerieUdTableBean getSerVLisErrFileSerieUdTableBeanForDownload(
	    BigDecimal idVerSerie, String tiScopoFileInputVerSerie) {
	return helper.getSerVLisErrFileSerieUd(idVerSerie, tiScopoFileInputVerSerie,
		list -> getSerVLisErrFileSerieUdTableBeanFrom(list), true);
    }

    private SerVLisErrFileSerieUdTableBean getSerVLisErrFileSerieUdTableBeanFrom(
	    List<SerVLisErrFileSerieUd> list) {
	SerVLisErrFileSerieUdTableBean table = new SerVLisErrFileSerieUdTableBean();
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisErrFileSerieUdTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della lista errori file input serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    public String getFileAcquisito(BigDecimal idVerSerie) throws ParerUserError {
	String file = null;
	try {
	    SerFileInputVerSerie fileInput = helper.getSerFileInputVerSerie(idVerSerie.longValue(),
		    CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name());
	    file = fileInput.getBlFileInputVerSerie();
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nell'ottenimento del file da scaricare";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return file;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void initControlloContenuto(BigDecimal idSerie, BigDecimal idVerSerie,
	    String tipoContenuto) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    checkStatoSerie(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
		    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name());
	    checkStatoContenutoSerie(idVerSerie,
		    tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.CALCOLATO.name()),
		    tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.ACQUISITO.name()),
		    tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name()),
		    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
		    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());

	    SerContenutoVerSerie contenuto = helper.getSerContenutoVerSerie(idVerSerie.longValue(),
		    tipoContenuto);
	    contenuto.setTiStatoContenutoVerSerie(
		    CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO.name());
	    contenuto.setDtStatoContenutoVerSerie(Calendar.getInstance().getTime());
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void riavvioControlloContenuto(BigDecimal idSerie, BigDecimal idVerSerie,
	    BigDecimal idContenuto, String tipoContenuto) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    SerContenutoVerSerie contenuto = helper
		    .getLockSerContenutoVerSerie(idVerSerie.longValue(), tipoContenuto);
	    if (contenuto != null) {
		if (idContenuto.longValue() != contenuto.getIdContenutoVerSerie()) {
		    throw new ParerUserError(
			    "Errore inaspettato nel riavvio del controllo contenuto");
		}
		String[] blocco = getFlContenutoBloccato(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(), idContenuto);
		if (blocco != null) {
		    if (blocco[0].equals("1")) {
			helper.deleteSerErrContenutoVerSerie(contenuto.getIdContenutoVerSerie());
			contenuto.setTiStatoContenutoVerSerie(
				CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO
					.name());
			contenuto.setDtStatoContenutoVerSerie(Calendar.getInstance().getTime());
		    } else {
			throw new ParerUserError("Il controllo del contenuto non \u00E8 bloccato");
		    }
		}
	    } else {
		throw new ParerUserError("Il contenuto \u00E8 in uso da parte di un altro utente");
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteAroUdAppartVerSerie(long idUser, BigDecimal idSerie,
	    BigDecimal idUdAppartVerSerie) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    try {
		AroUdAppartVerSerie ud = helper.findById(AroUdAppartVerSerie.class,
			idUdAppartVerSerie);
		SerContenutoVerSerie serContenutoVerSerie = ud.getSerContenutoVerSerie();
		String statoContenuto = serContenutoVerSerie.getTiStatoContenutoVerSerie();
		String tipoContenuto = serContenutoVerSerie.getTiContenutoVerSerie();
		SerVerSerie verSerie = serContenutoVerSerie.getSerVerSerie();

		logger.debug("Elimino l'ud dalla serie");
		logger.debug("Aggiorno il contenuto della serie");
		helper.removeEntity(ud, false);
		BigDecimal niUd = serContenutoVerSerie.getNiUdContenutoVerSerie()
			.subtract(BigDecimal.ONE);
		serContenutoVerSerie.setNiUdContenutoVerSerie(niUd);
		if (serContenutoVerSerie.getIdFirstUdAppartVerSerie()
			.longValue() == idUdAppartVerSerie.longValue()) {
		    Long firstUd = helper.getUdAppartVerSerie(
			    serContenutoVerSerie.getIdContenutoVerSerie(), true);
		    serContenutoVerSerie.setIdFirstUdAppartVerSerie(
			    firstUd != null ? new BigDecimal(firstUd) : BigDecimal.ZERO);
		}
		if (serContenutoVerSerie.getIdLastUdAppartVerSerie()
			.longValue() == idUdAppartVerSerie.longValue()) {
		    Long lastUd = helper.getUdAppartVerSerie(
			    serContenutoVerSerie.getIdContenutoVerSerie(), false);
		    serContenutoVerSerie.setIdLastUdAppartVerSerie(
			    lastUd != null ? new BigDecimal(lastUd) : BigDecimal.ZERO);
		}
		logger.debug("Aggiorno lo stato della serie se necessario");
		Date now = Calendar.getInstance().getTime();
		SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
			verSerie.getIdStatoVerSerieCor());
		if (statoCorrente != null
			&& tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())
			&& statoCorrente.getTiStatoVerSerie()
				.equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())) {
		    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
			    .createSerStatoVerSerie(
				    statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
				    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
				    "Eliminazione di unit\u00E0 documentarie da serie", null,
				    idUser, now, verSerie.getIdVerSerie());
		    helper.insertEntity(statoVerSerie, false);
		    verSerie.setIdStatoVerSerieCor(
			    new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

		    helper.deleteSerVerSerieDaElab(new BigDecimal(verSerie.getIdVerSerie()));
		}
		if (statoContenuto.equals(CostantiDB.StatoContenutoVerSerie.CREATO.name())
			|| statoContenuto.equals(
				CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
		    serContenutoVerSerie.setTiStatoContenutoVerSerie(
			    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
		}
	    } catch (Exception e) {
		String messaggio = "Eccezione imprevista nell'eliminazione del record ";
		messaggio += ExceptionUtils.getRootCauseMessage(e);
		logger.error(messaggio, e);
		throw new ParerUserError(messaggio);
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    public void createZipSerQueryContenutoVerSerie(BigDecimal idContenuto, ZipOutputStream out,
	    String prefixFile, String suffixFile) throws IOException {
	List<SerQueryContenutoVerSerie> serQueryContenutoVerSeries = helper
		.getSerQueryContenutoVerSerie(idContenuto);
	int counter = 1;
	byte[] data = new byte[1024];
	InputStream bis = null;
	for (SerQueryContenutoVerSerie query : serQueryContenutoVerSeries) {
	    try {
		bis = new ByteArrayInputStream(query.getBlQuery().getBytes());
		int count;
		out.putNextEntry(new ZipEntry(prefixFile + (counter++) + suffixFile));
		while ((count = bis.read(data, 0, 1024)) != -1) {
		    out.write(data, 0, count);
		}
		out.closeEntry();
	    } finally {
		IOUtils.closeQuietly(bis);
	    }
	}
    }

    public SerVVisConsistSerieUdRowBean getSerVVisConsistSerieUdRowBean(BigDecimal idConsist) {
	SerVVisConsistSerieUd contenuto = helper.findViewById(SerVVisConsistSerieUd.class,
		idConsist);
	SerVVisConsistSerieUdRowBean row = null;
	if (contenuto != null) {
	    try {
		row = (SerVVisConsistSerieUdRowBean) Transform.entity2RowBean(contenuto);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della consistenza attesa serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException(
			"Errore durante il recupero della consistenza attesa");
	    }
	}
	return row;
    }

    public SerLacunaConsistVerSerieTableBean getSerLacunaConsistVerSerieTableBean(
	    BigDecimal idConsist) {
	SerLacunaConsistVerSerieTableBean table = new SerLacunaConsistVerSerieTableBean();
	List<SerLacunaConsistVerSerie> list = helper.getSerLacunaConsistVerSerie(idConsist);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerLacunaConsistVerSerieTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero della lista lacune "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    /**
     * Verifica che le chiavi fornite nella consistenza attesa rispettino il formato
     * registro-anno-numero corrispondenti al tipo serie selezionato
     *
     * @param cdFirstUdAttesa codice prima unita doc in attesa
     * @param cdLastUdAttesa  codice ultima unita doc in attesa
     * @param idTipoSerie     id tipo serie
     * @param aaSerie         anno serie
     *
     * @return true/false
     */
    public boolean checkChiaveUdConsistenzaAttesa(String cdFirstUdAttesa, String cdLastUdAttesa,
	    BigDecimal idTipoSerie, BigDecimal aaSerie) {
	DecTipoSerie tipoSerie = helper.findById(DecTipoSerie.class, idTipoSerie);
	boolean firstUdAttesaCorretta = false;
	boolean lastUdAttesaCorretta = false;
	if (StringUtils.isNotBlank(cdFirstUdAttesa) && StringUtils.isNotBlank(cdLastUdAttesa)) {
	    for (DecTipoSerieUd tipoSerieUd : tipoSerie.getDecTipoSerieUds()) {
		String cdRegistroUnitaDoc = tipoSerieUd.getDecRegistroUnitaDoc()
			.getCdRegistroUnitaDoc();
		if (cdFirstUdAttesa.startsWith(cdRegistroUnitaDoc + "-")) {
		    String annoNumero = StringUtils.substringAfter(cdFirstUdAttesa,
			    cdRegistroUnitaDoc + "-");
		    if (StringUtils.isNotBlank(annoNumero)
			    && annoNumero.startsWith(aaSerie.toPlainString() + "-")) {
			firstUdAttesaCorretta = true;
		    }
		}

		if (cdLastUdAttesa.startsWith(cdRegistroUnitaDoc + "-")) {
		    String annoNumero = StringUtils.substringAfter(cdLastUdAttesa,
			    cdRegistroUnitaDoc + "-");
		    if (StringUtils.isNotBlank(annoNumero)
			    && annoNumero.startsWith(aaSerie.toPlainString() + "-")) {
			lastUdAttesaCorretta = true;
		    }
		}
		if (firstUdAttesaCorretta && lastUdAttesaCorretta) {
		    break;
		}
	    }
	}
	return (firstUdAttesaCorretta && lastUdAttesaCorretta);
    }

    /**
     * Metodo per salvare la consistenza attesa nel caso la modalità sia CHIAVE_UD
     *
     * @param idUser                   id utente
     * @param idConsistVerSerie        id versamento serie
     * @param idSerie                  id serie
     * @param idVerSerie               id versamento
     * @param niUdAttese               numero unita doc attese
     * @param cdDocConsist             code documento
     * @param dsDocConsist             descrizione documento
     * @param dtComunicConsistVerSerie data versamento serie
     * @param cdRegistroFirst          codice registro
     * @param aaUnitaDocFirst          anno unita doc
     * @param cdUnitaDocFirst          code anno unita doc
     * @param cdRegistroLast           registro
     * @param aaUnitaDocLast           anno unita doc
     * @param cdUnitaDocLast           numero unita doc
     *
     * @return BigDecimal pk
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveConsistenzaAttesa(long idUser, BigDecimal idConsistVerSerie,
	    BigDecimal idSerie, BigDecimal idVerSerie, BigDecimal niUdAttese, String cdDocConsist,
	    String dsDocConsist, Date dtComunicConsistVerSerie, String cdRegistroFirst,
	    BigDecimal aaUnitaDocFirst, String cdUnitaDocFirst, String cdRegistroLast,
	    BigDecimal aaUnitaDocLast, String cdUnitaDocLast) throws ParerUserError {
	return saveConsistenzaAttesa(idUser, idConsistVerSerie, idSerie, idVerSerie, niUdAttese,
		CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name(), null, null, cdDocConsist,
		dsDocConsist, dtComunicConsistVerSerie, cdRegistroFirst, aaUnitaDocFirst,
		cdUnitaDocFirst, cdRegistroLast, aaUnitaDocLast, cdUnitaDocLast);
    }

    /**
     * Metodo per salvare la consistenza attesa nel caso la modalità sia PROGRESSIVO o CODICE
     *
     * @param idUser                   id utente
     * @param idConsistVerSerie        id versamento serie
     * @param idSerie                  id serie
     * @param idVerSerie               id versamento serie
     * @param niUdAttese               numero unita doc attese
     * @param tiMod                    tipo modello
     * @param cdFirstUdAttesa          codice prima unita doc in attesa
     * @param cdLastUdAttesa           codice ultima unita doc in attesa
     * @param cdDocConsist             codice documento
     * @param dsDocConsist             descrizione documento
     * @param dtComunicConsistVerSerie data versamento serie
     *
     * @return BigDecimal pk
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveConsistenzaAttesa(long idUser, BigDecimal idConsistVerSerie,
	    BigDecimal idSerie, BigDecimal idVerSerie, BigDecimal niUdAttese, String tiMod,
	    String cdFirstUdAttesa, String cdLastUdAttesa, String cdDocConsist, String dsDocConsist,
	    Date dtComunicConsistVerSerie) throws ParerUserError {
	return saveConsistenzaAttesa(idUser, idConsistVerSerie, idSerie, idVerSerie, niUdAttese,
		tiMod, cdFirstUdAttesa, cdLastUdAttesa, cdDocConsist, dsDocConsist,
		dtComunicConsistVerSerie, null, null, null, null, null, null);
    }

    private BigDecimal saveConsistenzaAttesa(long idUser, BigDecimal idConsistVerSerie,
	    BigDecimal idSerie, BigDecimal idVerSerie, BigDecimal niUdAttese, String tiMod,
	    String cdFirstUdAttesa, String cdLastUdAttesa, String cdDocConsist, String dsDocConsist,
	    Date dtComunicConsistVerSerie, String cdRegistroFirst, BigDecimal aaUnitaDocFirst,
	    String cdUnitaDocFirst, String cdRegistroLast, BigDecimal aaUnitaDocLast,
	    String cdUnitaDocLast) throws ParerUserError {
	logger.debug("Eseguo il salvataggio della consistenza attesa");
	BigDecimal id = null;
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    logger.debug("Ottenuto lock sulla serie");
	    DecTipoSerie tipoSerie = serie.getDecTipoSerie();
	    if (checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
		    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
		    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
		    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
		SerVRicSerieUd view = helper.findViewById(SerVRicSerieUd.class, idVerSerie);
		if (checkContenuto(idVerSerie, (view.getTiStatoContenutoCalc() != null),
			(view.getTiStatoContenutoAcq() != null),
			(view.getTiStatoContenutoEff() != null),
			CostantiDB.StatoContenutoVerSerie.CREATO.name(),
			CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
			CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
		    if (tipoSerie.getFlControlloConsistObblig().equals("1") && niUdAttese == null) {
			throw new ParerUserError(
				"Il tipo serie prevede l'obbligatoriet\u00E0 del controllo di consistenza e, quindi, deve essere valorizzato il numero di unit\u00E0 documentarie attese");
		    }
		    if (tiMod != null && tiMod
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
			if (helper.countSerieWithoutPgSerieUd(tipoSerie.getIdTipoSerie()) > 0L) {
			    throw new ParerUserError(
				    "La 'Modalit\u00E0 definizione prima ed ultima unit\u00E0 documentaria' "
					    + "non pu\u00F2 valere PROGRESSIVO, perch\u00E9 il tipo di serie non definisce come calcolare il progressivo delle unit\u00E0 documentarie appartenenti alla serie");
			}
		    }
		    if (idConsistVerSerie != null && (tiMod == null
			    || tiMod.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CODICE.name()))) {
			logger.debug("Controllo in modifica sulle lacune");
			List<SerLacunaConsistVerSerie> serLacunaConsistVerSerie = helper
				.getSerLacunaConsistVerSerie(idConsistVerSerie,
					CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name(), null,
					null);
			if (!serLacunaConsistVerSerie.isEmpty()) {
			    throw new ParerUserError(
				    "Non \u00E8 possibile modificare la modalit\u00E0 di definizione prima ed ultima unit\u00E0 documentaria, perch\u00E9 sono presenti lacune definite con modalit\u00E0 RANGE_PROGRESSIVI");
			}
		    }
		    logger.debug("Controlli sui dati effettuati con successo");
		    SerConsistVerSerie consist;
		    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
		    String azione;
		    if (idConsistVerSerie != null) {
			consist = helper.findById(SerConsistVerSerie.class, idConsistVerSerie);

			azione = "Modifica consistenza attesa";
		    } else {
			consist = new SerConsistVerSerie();
			consist.setSerLacunaConsistVerSeries(
				new ArrayList<SerLacunaConsistVerSerie>());
			if (verSerie.getSerConsistVerSeries() == null) {
			    verSerie.setSerConsistVerSeries(new ArrayList<SerConsistVerSerie>());
			}
			verSerie.addSerConsistVerSery(consist);

			azione = "Inserimento consistenza";
		    }
		    consist.setDtComunicConsistVerSerie(dtComunicConsistVerSerie);
		    consist.setIamUser(helper.findById(IamUser.class, idUser));
		    consist.setNiUnitaDocAttese(niUdAttese);
		    consist.setTiModConsistFirstLast(tiMod);
		    if (tiMod != null) {
			if (tiMod.equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
			    setConsistValuesForTiModProgressivo(consist,
				    new BigDecimal(cdFirstUdAttesa),
				    new BigDecimal(cdLastUdAttesa));
			} else if (tiMod
				.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CODICE.name())) {
			    setConsistValuesForTiModCodice(consist, cdFirstUdAttesa,
				    cdLastUdAttesa);
			} else if (tiMod
				.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name())) {
			    setConsistValuesForTiModChiaveUd(consist, cdRegistroFirst,
				    aaUnitaDocFirst, cdUnitaDocFirst, cdRegistroLast,
				    aaUnitaDocLast, cdUnitaDocLast);
			}
		    } else {
			setConsistValuesForTiModEmpty(consist);
		    }
		    consist.setCdDocConsistVerSerie(cdDocConsist);
		    consist.setDsDocConsistVerSerie(dsDocConsist);

		    if (idConsistVerSerie == null) {
			helper.insertEntity(consist, true);
			id = new BigDecimal(consist.getIdConsistVerSerie());
		    } else {
			id = idConsistVerSerie;
		    }

		    handleStatoSerieDaConsistenzaAttesa(verSerie, azione, idUser);
		} else {
		    throw new ParerUserError(
			    "La consistenza attesa non pu\u00F2 essere modificata / inserita perch\u00E9 almeno un contenuto ha stato diverso da CREATO e CONTROLLATA_CONSIST e DA_CONTROLLARE_CONSIST");
		}
	    } else {
		throw new ParerUserError(
			"La consistenza attesa non pu\u00F2 essere modificata / inserita perch\u00E9 la versione della serie ha stato diverso da APERTA e DA_CONTROLLARE e CONTROLLATA");
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
	return id;
    }

    private void setConsistValuesForTiModProgressivo(SerConsistVerSerie consist,
	    BigDecimal niFirstUnitaDocAttesa, BigDecimal niLastUnitaDocAttesa) {
	setConsistValuesForTiMod(consist, niFirstUnitaDocAttesa, niLastUnitaDocAttesa, null, null,
		null, null, null, null, null, null);
    }

    private void setConsistValuesForTiModCodice(SerConsistVerSerie consist, String cdFirstUdAttesa,
	    String cdLastUdAttesa) {
	setConsistValuesForTiMod(consist, null, null, cdFirstUdAttesa, cdLastUdAttesa, null, null,
		null, null, null, null);
    }

    private void setConsistValuesForTiModChiaveUd(SerConsistVerSerie consist,
	    String cdRegistroFirst, BigDecimal aaUnitaDocFirst, String cdUnitaDocFirst,
	    String cdRegistroLast, BigDecimal aaUnitaDocLast, String cdUnitaDocLast) {
	setConsistValuesForTiMod(consist, null, null, null, null, cdRegistroFirst, aaUnitaDocFirst,
		cdUnitaDocFirst, cdRegistroLast, aaUnitaDocLast, cdUnitaDocLast);
    }

    private void setConsistValuesForTiModEmpty(SerConsistVerSerie consist) {
	setConsistValuesForTiMod(consist, null, null, null, null, null, null, null, null, null,
		null);
    }

    private void setConsistValuesForTiMod(SerConsistVerSerie consist,
	    BigDecimal niFirstUnitaDocAttesa, BigDecimal niLastUnitaDocAttesa,
	    String cdFirstUdAttesa, String cdLastUdAttesa, String cdRegistroFirst,
	    BigDecimal aaUnitaDocFirst, String cdUnitaDocFirst, String cdRegistroLast,
	    BigDecimal aaUnitaDocLast, String cdUnitaDocLast) {
	consist.setNiFirstUnitaDocAttesa(niFirstUnitaDocAttesa);
	consist.setNiLastUnitaDocAttesa(niLastUnitaDocAttesa);
	consist.setCdFirstUnitaDocAttesa(cdFirstUdAttesa);
	consist.setCdLastUnitaDocAttesa(cdLastUdAttesa);
	consist.setCdRegistroFirst(cdRegistroFirst);
	consist.setAaUnitaDocFirst(aaUnitaDocFirst);
	consist.setCdUnitaDocFirst(cdUnitaDocFirst);
	consist.setCdRegistroLast(cdRegistroLast);
	consist.setAaUnitaDocLast(aaUnitaDocLast);
	consist.setCdUnitaDocLast(cdUnitaDocLast);
    }

    private void handleStatoSerieDaConsistenzaAttesa(SerVerSerie verSerie, String azione,
	    long idUser) throws ParerUserError, IllegalStateException {
	SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());
	if (statoCorrente != null) {
	    if (statoCorrente.getTiStatoVerSerie()
		    .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
		    || statoCorrente.getTiStatoVerSerie()
			    .equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
		logger.debug(
			SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della versione");
		SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
			.createSerStatoVerSerie(
				statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
				CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(), azione, null,
				idUser, Calendar.getInstance().getTime(), verSerie.getIdVerSerie());
		logger.debug(SerieEjb.class.getSimpleName()
			+ " --- Eseguo la persist del nuovo stato versione");
		helper.insertEntity(statoVerSerie, false);
		verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));
	    }
	} else {
	    throw new ParerUserError(
		    "Errore inatteso in fase di salvataggio dello stato corrente della versione della serie");
	}

	SerContenutoVerSerie contenCalc = helper.getSerContenutoVerSerie(verSerie.getIdVerSerie(),
		CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
	SerContenutoVerSerie contenAcq = helper.getSerContenutoVerSerie(verSerie.getIdVerSerie(),
		CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
	SerContenutoVerSerie contenEff = helper.getSerContenutoVerSerie(verSerie.getIdVerSerie(),
		CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

	if (contenCalc != null && contenCalc.getTiStatoContenutoVerSerie()
		.equals(CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
	    contenCalc.setTiStatoContenutoVerSerie(
		    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
	}
	if (contenAcq != null && contenAcq.getTiStatoContenutoVerSerie()
		.equals(CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
	    contenAcq.setTiStatoContenutoVerSerie(
		    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
	}
	if (contenEff != null && contenEff.getTiStatoContenutoVerSerie()
		.equals(CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
	    contenEff.setTiStatoContenutoVerSerie(
		    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
	    helper.deleteSerVerSerieDaElab(new BigDecimal(verSerie.getIdVerSerie()));
	}
    }

    // <editor-fold defaultstate="collapsed" desc="Gestione GENERAZIONE EFFETTIVO - ASINCRONA">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> callGenerazioneEffettivoAsync(long idUser, long idVerSerie,
	    boolean fromCalc, boolean fromAcq) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<String> future = null;
	try {
	    future = context.getBusinessObject(SerieEjb.class).generazioneEffettivoAsync(idUser,
		    idVerSerie, fromCalc, fromAcq);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	return future;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Map<String, ?>> callGenerazioneEffettivoAsync(
	    Map<String, BigDecimal> idVerSeries) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<Map<String, ?>> futures = null;
	try {
	    futures = context.getBusinessObject(SerieEjb.class)
		    .generazioneEffettivoAsync(idVerSeries);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return futures;
    }

    @Asynchronous
    public Future<String> generazioneEffettivoAsync(long idUser, long idVerSerie, boolean fromCalc,
	    boolean fromAcq) throws ParerInternalError {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- Richiesta LOCK su contenuto di tipo EFFETTIVO");
	    contenuto = helper.getLockSerContenutoVerSerie(idVerSerie,
		    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
		context.getBusinessObject(SerieEjb.class).generazioneEffettivo(idUser, contenuto,
			fromCalc, fromAcq, false);
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per generazione contenuto effettivo");
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di generazione contenuto effettivo:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}

	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return new AsyncResult<>(result);
    }

    @Asynchronous
    public Future<Map<String, ?>> generazioneEffettivoAsync(Map<String, BigDecimal> idVerSeries) {
	Map<String, String> futures = new HashMap<>();
	for (Entry<String, BigDecimal> idVerSerieEntry : idVerSeries.entrySet()) {
	    // Recupero il parametro di struttura USERID_CREAZIONE_SERIE
	    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerieEntry.getValue());
	    BigDecimal idStrut = BigDecimal
		    .valueOf(verSerie.getSerSerie().getOrgStrut().getIdStrut());
	    BigDecimal idAmbiente = BigDecimal.valueOf(verSerie.getSerSerie().getOrgStrut()
		    .getOrgEnte().getOrgAmbiente().getIdAmbiente());
	    String nmUserId = configurationHelper.getValoreParamApplicByStrut(
		    CostantiDB.ParametroAppl.USERID_CREAZIONE_SERIE, idAmbiente, idStrut);
	    IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);
	    long idUser = userCreazioneSerie.getIdUserIam();
	    String result = context.getBusinessObject(SerieEjb.class)
		    .generazioneContenutoEffettivoSingleTx(idVerSerieEntry.getValue(), idUser);
	    futures.put(idVerSerieEntry.getKey(), result);
	}
	return new AsyncResult<Map<String, ?>>(futures);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GENERAZIONE EFFETTIVO">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String generazioneContenutoEffettivoSingleTx(BigDecimal idVerSerieEntry, long idUser) {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Richiesta LOCK su contenuto EFFETTIVO con id_ver_serie "
		    + idVerSerieEntry.toPlainString());
	    contenuto = helper.getLockSerContenutoVerSerie(idVerSerieEntry.longValue(),
		    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
		context.getBusinessObject(SerieEjb.class).generazioneEffettivo(idUser, contenuto,
			true, false, true);
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per generazione contenuto effettivo serie");
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di generazione contenuto effettivo serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}
	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void generazioneEffettivo(long idUser, SerContenutoVerSerie contenuto, boolean fromCalc,
	    boolean fromAcq, boolean calcoloAutomatico) {
	List<ResultVCalcoloSerieUd> udDaCreare = new ArrayList<>();
	long idVerSerie = contenuto.getSerVerSerie().getIdVerSerie();
	String azione = "Generazione contenuto effettivo mediante ";
	if (fromCalc && fromAcq) {
	    logger.info(
		    "Il contenuto deve essere generato da entrambi i contenuti - creo un'unica lista di elementi non duplicati");
	    udDaCreare = helper.getAroUdAppartVerSerie(idVerSerie,
		    CostantiDB.TipoContenutoVerSerie.CALCOLATO.name(),
		    CostantiDB.TipoContenutoVerSerie.ACQUISITO.name(), false);
	    azione = azione.concat("il contenuto di tipo CALCOLATO e quello di tipo ACQUISITO");
	} else if (fromCalc) {
	    logger.info("Ottengo le unit\u00E0 documentarie del contenuto calcolato");
	    udDaCreare = helper.getAroUdAppartVerSerie(idVerSerie,
		    CostantiDB.TipoContenutoVerSerie.CALCOLATO.name(), null, false);
	    azione = azione.concat("il contenuto di tipo CALCOLATO");
	} else if (fromAcq) {
	    logger.info("Ottengo le unit\u00E0 documentarie del contenuto acquisito");
	    udDaCreare = helper.getAroUdAppartVerSerie(idVerSerie,
		    CostantiDB.TipoContenutoVerSerie.ACQUISITO.name(), null, false);
	    azione = azione.concat("il contenuto di tipo ACQUISITO");
	}
	logger.info("Il nuovo contenuto conterr\u00E0 " + udDaCreare.size()
		+ " unit\u00E0 documentarie");
	int counterUd = 0;
	for (ResultVCalcoloSerieUd ud : udDaCreare) {
	    context.getBusinessObject(SerieEjb.class)
		    .handleAroUnitaDoc(contenuto.getIdContenutoVerSerie(), ud);
	}
	counterUd += udDaCreare.size();

	logger.debug(SerieEjb.class.getSimpleName()
		+ " --- Aggiorna il contenuto della versione con i dati delle ud appartenenti ad essa");
	Long firstUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), true);
	Long lastUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), false);
	Date now = Calendar.getInstance().getTime();

	contenuto.setNiUdContenutoVerSerie(new BigDecimal(counterUd));
	contenuto.setIdFirstUdAppartVerSerie(
		firstUd != null ? new BigDecimal(firstUd) : BigDecimal.ZERO);
	contenuto.setIdLastUdAppartVerSerie(
		lastUd != null ? new BigDecimal(lastUd) : BigDecimal.ZERO);
	contenuto.setTiStatoContenutoVerSerie(CostantiDB.StatoContenutoVerSerie.CREATO.name());
	contenuto.setDtStatoContenutoVerSerie(now);

	SerVerSerie verSerie = contenuto.getSerVerSerie();
	SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());
	logger.debug(SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della versione");
	SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		.createSerStatoVerSerie(statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
			CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(), azione, null, idUser,
			now, verSerie.getIdVerSerie());

	logger.debug(
		SerieEjb.class.getSimpleName() + " --- Eseguo la persist del nuovo stato versione");
	helper.insertEntity(statoVerSerie, false);
	verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	DecTipoSerie tipoSerie = verSerie.getSerSerie().getDecTipoSerie();
	if (calcoloAutomatico && tipoSerie.getTiStatoVerSerieAutom() == null) {
	    throw new IllegalStateException(
		    "Impostato calcolo automatico per la generazione del contenuto EFFETTIVO della serie senza aver definito lo stato della versione all'interno del tipo di serie");
	} else if (calcoloAutomatico && tipoSerie.getTiStatoVerSerieAutom() != null
		&& (tipoSerie.getTiStatoVerSerieAutom()
			.equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
			|| tipoSerie.getTiStatoVerSerieAutom()
				.equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name()))) {
	    SerVerSerieDaElab verSerieDaElab = helper
		    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
	    verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name());
	} else {
	    helper.deleteSerVerSerieDaElab(new BigDecimal(idVerSerie));
	}

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione CONTROLLO CONTENUTO - ASINCRONA">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> callControlloContenutoAsync(long idUser, long idVerSerie,
	    String tipoContenuto) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<String> future = null;
	try {
	    future = context.getBusinessObject(SerieEjb.class).controlloContenutoAsync(idUser,
		    idVerSerie, tipoContenuto);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	return future;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Map<String, ?>> callControlloContenutoAsync(Map<String, BigDecimal> idVerSeries) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<Map<String, ?>> futures = null;
	try {
	    futures = context.getBusinessObject(SerieEjb.class)
		    .controlloContenutoAsync(idVerSeries);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return futures;
    }

    @Asynchronous
    public Future<String> controlloContenutoAsync(long idUser, long idVerSerie,
	    String tipoContenuto) {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.info(SerieEjb.class.getSimpleName() + " --- Richiesta LOCK su contenuto di tipo "
		    + tipoContenuto);
	    contenuto = helper.getLockSerContenutoVerSerie(idVerSerie, tipoContenuto);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
		context.getBusinessObject(SerieEjb.class).controlloContenuto(idUser, contenuto,
			false);
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per controllo contenuto serie");
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di controllo contenuto serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}
	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return new AsyncResult<>(result);
    }

    @Asynchronous
    public Future<Map<String, ?>> controlloContenutoAsync(Map<String, BigDecimal> idVerSeries) {
	Map<String, String> futures = new HashMap<>();
	for (Entry<String, BigDecimal> idVerSerieEntry : idVerSeries.entrySet()) {
	    // Recupero il parametro di struttura USERID_CREAZIONE_SERIE
	    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerieEntry.getValue());
	    BigDecimal idStrut = BigDecimal
		    .valueOf(verSerie.getSerSerie().getOrgStrut().getIdStrut());
	    BigDecimal idAmbiente = BigDecimal.valueOf(verSerie.getSerSerie().getOrgStrut()
		    .getOrgEnte().getOrgAmbiente().getIdAmbiente());
	    String nmUserId = configurationHelper.getValoreParamApplicByStrut(
		    CostantiDB.ParametroAppl.USERID_CREAZIONE_SERIE, idAmbiente, idStrut);
	    IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);
	    long idUser = userCreazioneSerie.getIdUserIam();
	    String result = context.getBusinessObject(SerieEjb.class)
		    .controlloContenutoSingleTx(idVerSerieEntry.getValue(), idUser);
	    futures.put(idVerSerieEntry.getKey(), result);
	}
	return new AsyncResult<Map<String, ?>>(futures);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CONTROLLO CONTENUTO">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String controlloContenutoSingleTx(BigDecimal idVerSerieEntry, long idUser) {
	SerContenutoVerSerie contenuto = null;
	boolean error = false;
	try {
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Richiesta LOCK su contenuto EFFETTIVO con id_ver_serie "
		    + idVerSerieEntry.toPlainString());
	    contenuto = helper.getLockSerContenutoVerSerie(idVerSerieEntry.longValue(),
		    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
		context.getBusinessObject(SerieEjb.class).controlloContenuto(idUser, contenuto,
			true);
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- FINE chiamata asincrona per controllo contenuto serie");
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di controllo contenuto serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (contenuto != null) {
		jobHelper.writeAtomicLogJob(
			JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
			JobConstants.OpTypeEnum.ERRORE.name(), messaggio,
			contenuto.getIdContenutoVerSerie(), contenuto.getClass().getSimpleName());
	    }
	}
	String result;
	if (contenuto != null && !error) {
	    result = "OK";
	} else if (contenuto == null) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void controlloContenuto(long idUser, SerContenutoVerSerie contenuto,
	    boolean calcoloAutomatico) {
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Elimino i vecchi errori del contenuto");
	helper.deleteSerErrContenutoVerSerie(contenuto.getIdContenutoVerSerie());
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Elimino i vecchi errori del contenuto");
	int pg = 1;
	final String origineErrore = CostantiDB.TipoOrigineErroreContenuto.CONTROLLO.name();

	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Ricerca chiavi doppie");
	List<String> codiciDuplicati = helper
		.getAroUdAppartChiaveDoppia(contenuto.getIdContenutoVerSerie());
	if (!codiciDuplicati.isEmpty()) {
	    for (String codice : codiciDuplicati) {
		String dlErr = "Il codice " + codice
			+ " \u00E8 presente pi\u00F9 di una volta fra le unit\u00E0 documentarie appartenenti al contenuto";
		context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			contenuto.getIdContenutoVerSerie(), pg++,
			CostantiDB.TipoErroreContenuto.CHIAVE_DOPPIA.name(), dlErr,
			CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
	    }
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Ricerca chiavi doppie");
	SerVerSerie verSerie = contenuto.getSerVerSerie();

	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Ricerca ud non versate che potrebbero appartenere alla serie");
	BigDecimal idVerSerie = new BigDecimal(verSerie.getIdVerSerie());
	SerVChkDefUdNovers check = helper.findViewById(SerVChkDefUdNovers.class, idVerSerie);
	if (check.getFlDefUdNonVers().equals("1")) {
	    List<SerVSelUdNovers> udNoVers = helper.getSerVSelUdNovers(idVerSerie.longValue(),
		    verSerie.getSerSerie().getAaSerie());
	    if (!udNoVers.isEmpty()) {
		String dlErr = "Sono presenti unit\u00E0 documentarie non versate relative all'anno della serie, che potrebbero appartenere alla serie";
		SerErrContenutoVerSerie errContenuto = context.getBusinessObject(SerieEjb.class)
			.createSerErrContenutoVerSerie(contenuto.getIdContenutoVerSerie(), pg++,
				CostantiDB.TipoErroreContenuto.UD_NON_VERS.name(), dlErr,
				CostantiDB.TipoGravitaErrore.WARNING.name(), origineErrore);
		if (errContenuto.getSerUdNonVersErrs() == null) {
		    errContenuto.setSerUdNonVersErrs(new ArrayList<SerUdNonVersErr>());
		}
		for (SerVSelUdNovers ud : udNoVers) {
		    context.getBusinessObject(SerieEjb.class).createSerUdNonVersErr(errContenuto,
			    ud.getSerVSelUdNoversId().getIdUnitaDocNonVers());
		}
	    }
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Ricerca ud non versate che potrebbero appartenere alla serie");
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controlli sulla consistenza attesa");

	List<SerConsistVerSerie> serConsistVerSeries = verSerie.getSerConsistVerSeries();
	if (serConsistVerSeries.isEmpty()) {
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- Consistenza attesa NON DEFINITA");
	    DecTipoSerie tipoSerie = verSerie.getSerSerie().getDecTipoSerie();
	    String tipoGravita = CostantiDB.TipoGravitaErrore.WARNING.name();
	    String dlErr = "La consistenza attesa non \u00E8 definita ed il tipo serie non prevede l'obbligatoriet\u00E0 del controllo consistenza";
	    if (tipoSerie.getFlControlloConsistObblig().equals("1")) {
		tipoGravita = CostantiDB.TipoGravitaErrore.ERRORE.name();
		dlErr = "La consistenza attesa non \u00E8 definita ed il tipo serie prevede l'obbligatoriet\u00E0 del controllo consistenza";
	    }
	    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
		    contenuto.getIdContenutoVerSerie(), pg++,
		    CostantiDB.TipoErroreContenuto.CONSISTENZA_NON_DEFINITA.name(), dlErr,
		    tipoGravita, origineErrore);
	} else {
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo numero totale di ud nel contenuto");
	    SerConsistVerSerie consistenza = serConsistVerSeries.get(0);
	    BigDecimal consistUdAttese;
	    if ((consistUdAttese = consistenza.getNiUnitaDocAttese()) != null) {
		BigDecimal contenUd = contenuto.getNiUdContenutoVerSerie();
		if (!consistUdAttese.equals(contenUd)) {
		    String dlErr = "La consistenza attesa prevede "
			    + consistUdAttese.toPlainString() + " unit\u00E0 documentarie,"
			    + " mentre il contenuto ha " + contenUd.toPlainString()
			    + " unit\u00E0 documentarie";
		    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			    contenuto.getIdContenutoVerSerie(), pg++,
			    CostantiDB.TipoErroreContenuto.NUMERO_TOTALE.name(), dlErr,
			    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
		}
	    }
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo numero totale di ud nel contenuto");

	    List<ResultVCalcoloSerieUd> aroUdAppartVerSerie = helper.getAroUdAppartVerSerie(
		    idVerSerie.longValue(), contenuto.getTiContenutoVerSerie(), null, false);
	    if (!aroUdAppartVerSerie.isEmpty()) {
		String cdFirstAroUdAppartVerSerie = aroUdAppartVerSerie.get(0).getKeyUdSerie();
		String cdLastAroUdAppartVerSerie = aroUdAppartVerSerie
			.get(aroUdAppartVerSerie.size() - 1).getKeyUdSerie();
		BigDecimal firstIdUnitaDoc = aroUdAppartVerSerie.get(0).getIdUnitaDoc();
		BigDecimal lastIdUnitaDoc = aroUdAppartVerSerie.get(aroUdAppartVerSerie.size() - 1)
			.getIdUnitaDoc();
		if (consistenza.getTiModConsistFirstLast() != null) {
		    logger.info(SerieEjb.class.getSimpleName()
			    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controlli su chiave iniziale-finale delle ud");
		    logger.info(SerieEjb.class.getSimpleName()
			    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- MODALITA DI CONSISTENZA = "
			    + consistenza.getTiModConsistFirstLast());
		    if (consistenza.getTiModConsistFirstLast()
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.CODICE.name())) {

			String cdFirstUdAttesa;
			String cdLastUdAttesa;
			if ((cdFirstUdAttesa = consistenza.getCdFirstUnitaDocAttesa()) != null) {
			    if (!cdFirstUdAttesa.equals(cdFirstAroUdAppartVerSerie)) {
				String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia codice pari a '"
					+ cdFirstUdAttesa
					+ "', mentre il contenuto ha come prima unit\u00E0 documentaria quella con codice pari a '"
					+ cdFirstAroUdAppartVerSerie + "'";
				context.getBusinessObject(SerieEjb.class)
					.createSerErrContenutoVerSerie(
						contenuto.getIdContenutoVerSerie(), pg++,
						CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE
							.name(),
						dlErr, CostantiDB.TipoGravitaErrore.ERRORE.name(),
						origineErrore);
			    }
			}
			if ((cdLastUdAttesa = consistenza.getCdLastUnitaDocAttesa()) != null) {
			    if (!cdLastUdAttesa.equals(cdLastAroUdAppartVerSerie)) {
				String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia codice pari a '"
					+ cdLastUdAttesa
					+ "', mentre il contenuto ha come ultima unit\u00E0 documentaria quella con codice pari a '"
					+ cdLastAroUdAppartVerSerie + "'";
				context.getBusinessObject(SerieEjb.class)
					.createSerErrContenutoVerSerie(
						contenuto.getIdContenutoVerSerie(), pg++,
						CostantiDB.TipoErroreContenuto.CHIAVE_FINALE.name(),
						dlErr, CostantiDB.TipoGravitaErrore.ERRORE.name(),
						origineErrore);
			    }
			}
		    } else if (consistenza.getTiModConsistFirstLast()
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name())) {
			AroUnitaDoc firstUd = helper.findById(AroUnitaDoc.class, firstIdUnitaDoc);
			String firstChiave = firstUd.getCdRegistroKeyUnitaDoc() + "-"
				+ firstUd.getAaKeyUnitaDoc().toPlainString() + "-"
				+ firstUd.getCdKeyUnitaDoc();
			AroUnitaDoc lastUd = helper.findById(AroUnitaDoc.class, lastIdUnitaDoc);
			String lastChiave = lastUd.getCdRegistroKeyUnitaDoc() + "-"
				+ lastUd.getAaKeyUnitaDoc().toPlainString() + "-"
				+ lastUd.getCdKeyUnitaDoc();

			String cdFirstUdAttesa = (consistenza.getCdRegistroFirst() != null
				&& consistenza.getAaUnitaDocFirst() != null
				&& consistenza.getCdUnitaDocFirst() != null)
					? consistenza.getCdRegistroFirst() + "-"
						+ consistenza.getAaUnitaDocFirst().toPlainString()
						+ "-" + consistenza.getCdUnitaDocFirst()
					: null;

			String cdLastUdAttesa = (consistenza.getCdRegistroLast() != null
				&& consistenza.getAaUnitaDocLast() != null
				&& consistenza.getCdUnitaDocLast() != null)
					? consistenza.getCdRegistroLast() + "-"
						+ consistenza.getAaUnitaDocLast().toPlainString()
						+ "-" + consistenza.getCdUnitaDocLast()
					: null;

			if (cdFirstUdAttesa != null && !cdFirstUdAttesa.equals(firstChiave)) {
			    String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia chiave pari a '"
				    + cdFirstUdAttesa
				    + "', mentre il contenuto ha come prima unit\u00E0 documentaria quella con chiave pari a '"
				    + firstChiave + "'";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
			if (cdLastUdAttesa != null && !cdLastUdAttesa.equals(lastChiave)) {
			    String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia chiave pari a '"
				    + cdLastUdAttesa
				    + "', mentre il contenuto ha come ultima unit\u00E0 documentaria quella con chiave pari a '"
				    + lastChiave + "'";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_FINALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
		    } else if (consistenza.getTiModConsistFirstLast()
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
			List<ResultVCalcoloSerieUd> aroUdAppartVerSeriePgNull = helper
				.getAroUdAppartVerSerie(idVerSerie.longValue(),
					contenuto.getTiContenutoVerSerie(), null, true);
			for (ResultVCalcoloSerieUd result : aroUdAppartVerSeriePgNull) {
			    String dlErr = "Per l'unit\u00E0 documentaria " + result.getKeyUdSerie()
				    + " appartenente al contenuto, il progressivo \u00E8 nullo";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.PROGRESSIVO_NULLO.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
			List<BigDecimal> aroUdAppartVerSeriePgDoppio = helper
				.getAroUdAppartNumeroDoppio(contenuto.getIdContenutoVerSerie());
			for (BigDecimal pgDoppio : aroUdAppartVerSeriePgDoppio) {
			    String dlErr = "Il progressivo " + pgDoppio.toPlainString()
				    + " \u00E8 presente pi\u00F9 di una volta fra le unit\u00E0 documentarie appartenenti al contenuto";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.PROGRESSIVO_DOPPIO.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
			BigDecimal niFirstUdAttesa;
			BigDecimal niLastUdAttesa;
			BigDecimal niFirstAroUdAppartVerSerie = aroUdAppartVerSerie.get(0)
				.getPgUdSerie();
			BigDecimal niLastAroUdAppartVerSerie = aroUdAppartVerSerie
				.get(aroUdAppartVerSerie.size() - 1).getPgUdSerie();
			if ((niFirstUdAttesa = consistenza.getNiFirstUnitaDocAttesa()) != null) {
			    if (niFirstAroUdAppartVerSerie != null) {
				if (!niFirstUdAttesa.equals(niFirstAroUdAppartVerSerie)) {
				    if ((niFirstUdAttesa.compareTo(niFirstAroUdAppartVerSerie) < 0
					    && !existLacuna(
						    new BigDecimal(
							    consistenza.getIdConsistVerSerie()),
						    null, niFirstUdAttesa, niFirstUdAttesa))
					    || niFirstUdAttesa
						    .compareTo(niFirstAroUdAppartVerSerie) > 0) {
					String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia progressivo pari a '"
						+ niFirstUdAttesa
						+ "', mentre il contenuto ha come prima unit\u00E0 documentaria quella con progressivo pari a '"
						+ niFirstAroUdAppartVerSerie + "'";
					context.getBusinessObject(SerieEjb.class)
						.createSerErrContenutoVerSerie(
							contenuto.getIdContenutoVerSerie(), pg++,
							CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE
								.name(),
							dlErr,
							CostantiDB.TipoGravitaErrore.ERRORE.name(),
							origineErrore);
				    }
				}
			    } else {
				String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia progressivo pari a '"
					+ niFirstUdAttesa
					+ "', mentre il contenuto non ha definito il progressivo per la prima unit\u00E0 documentaria";
				context.getBusinessObject(SerieEjb.class)
					.createSerErrContenutoVerSerie(
						contenuto.getIdContenutoVerSerie(), pg++,
						CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE
							.name(),
						dlErr, CostantiDB.TipoGravitaErrore.ERRORE.name(),
						origineErrore);
			    }
			}
			if ((niLastUdAttesa = consistenza.getNiLastUnitaDocAttesa()) != null) {
			    if (niLastAroUdAppartVerSerie != null) {
				if (!niLastUdAttesa.equals(niLastAroUdAppartVerSerie)) {
				    if ((niLastUdAttesa.compareTo(niLastAroUdAppartVerSerie) > 0
					    && !existLacuna(
						    new BigDecimal(
							    consistenza.getIdConsistVerSerie()),
						    null, niLastUdAttesa, niLastUdAttesa))
					    || niLastUdAttesa
						    .compareTo(niLastAroUdAppartVerSerie) < 0) {
					String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia progressivo pari a '"
						+ niLastUdAttesa
						+ "', mentre il contenuto ha come ultima unit\u00E0 documentaria quella con progressivo pari a '"
						+ niLastAroUdAppartVerSerie + "'";
					context.getBusinessObject(SerieEjb.class)
						.createSerErrContenutoVerSerie(
							contenuto.getIdContenutoVerSerie(), pg++,
							CostantiDB.TipoErroreContenuto.CHIAVE_FINALE
								.name(),
							dlErr,
							CostantiDB.TipoGravitaErrore.ERRORE.name(),
							origineErrore);
				    }
				}
			    } else {
				String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia progressivo pari a '"
					+ niLastUdAttesa
					+ "', mentre il contenuto non ha definito il progressivo per l'ultima unit\u00E0 documentaria";
				context.getBusinessObject(SerieEjb.class)
					.createSerErrContenutoVerSerie(
						contenuto.getIdContenutoVerSerie(), pg++,
						CostantiDB.TipoErroreContenuto.CHIAVE_FINALE.name(),
						dlErr, CostantiDB.TipoGravitaErrore.ERRORE.name(),
						origineErrore);
			    }
			}
			logger.info(SerieEjb.class.getSimpleName()
				+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo buchi numerazione");
			List<SerVBucoNumerazioneUd> listaBuchiNum = helper
				.getSerVBucoNumerazioneUd(contenuto.getIdContenutoVerSerie());
			for (SerVBucoNumerazioneUd buco : listaBuchiNum) {
			    BigDecimal pgUdSerIniBuco = buco.getSerVBucoNumerazioneUdId()
				    .getPgUdSerIniBuco();
			    BigDecimal pgUdSerFinBuco = buco.getPgUdSerFinBuco();
			    List<SerLacunaConsistVerSerie> lacuneList = helper
				    .getSerLacunaConsistVerSerie(
					    new BigDecimal(consistenza.getIdConsistVerSerie()),
					    CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name(),
					    pgUdSerIniBuco, pgUdSerFinBuco);
			    if (lacuneList.isEmpty()) {
				String dlErr = "Le unit\u00E0 documentarie con progressivo compreso nell'intervallo "
					+ pgUdSerIniBuco.toPlainString() + "-"
					+ pgUdSerFinBuco.toPlainString()
					+ " non sono presenti nel contenuto e la loro assenza ";
				SerErrContenutoVerSerie serErrContenutoVerSerie;
				if (existLacuna(new BigDecimal(consistenza.getIdConsistVerSerie()),
					null, pgUdSerIniBuco, pgUdSerFinBuco)) {
				    /*
				     * Controlla se esiste almeno una lacuna (comunicata con
				     * modalita = RANGE_PROGRESSIVI) della consistenza attesa, il
				     * cui intervallo si sovrappone o e? incluso con quello del buco
				     * di numerazione
				     */
				    dlErr += "\u00E8 solo parzialmente giustificata da una (o pi\u00F9) lacune della consistenza attesa";
				    serErrContenutoVerSerie = context
					    .getBusinessObject(SerieEjb.class)
					    .createSerErrContenutoVerSerie(
						    contenuto.getIdContenutoVerSerie(), pg++,
						    CostantiDB.TipoErroreContenuto.BUCO_NUMERAZIONE
							    .name(),
						    dlErr,
						    CostantiDB.TipoGravitaErrore.ERRORE.name(),
						    origineErrore);
				} else {
				    dlErr += "non \u00E8 giustificata da una lacuna della consistenza attesa";
				    serErrContenutoVerSerie = context
					    .getBusinessObject(SerieEjb.class)
					    .createSerErrContenutoVerSerie(
						    contenuto.getIdContenutoVerSerie(), pg++,
						    CostantiDB.TipoErroreContenuto.BUCO_NUMERAZIONE
							    .name(),
						    dlErr,
						    CostantiDB.TipoGravitaErrore.ERRORE.name(),
						    origineErrore);
				}
				/*
				 * se il tipo di serie della serie del contenuto in input prevede la
				 * possibilit\u00E0 di selezionare le unit\u00E0 doc non versate a
				 * fronte di un buco di numerazione (vedi vista
				 * SER_V_CHK_DEF_UD_NOVERS_BUCO (cio\u00E8 che il campo in output di
				 * tipo KEY_UD_SERIE sia definito solo con i campi REGISTRO, ANNO e
				 * PROGRESSIVO di tipo DATO_PROFILO e che il registro abbia formato
				 * standard (cio\u00E8 preveda solo numeri))
				 */
				SerVChkDefUdNoversBuco checkBuchi = helper
					.findViewById(SerVChkDefUdNoversBuco.class, idVerSerie);
				if (checkBuchi != null
					&& checkBuchi.getFlDefUdNonVers().equals("1")) {
				    /*
				     * unit\u00E0 doc non versate relative ai registri previsti dal
				     * tipo serie ed all'anno della serie, il cui numero e' compreso
				     * nel buco di numerazione
				     */
				    List<SerVSelUdNoversBuco> udNonVersate = helper
					    .getSerVSelUdNoversBuco(idVerSerie, pgUdSerIniBuco,
						    pgUdSerFinBuco);
				    for (SerVSelUdNoversBuco ud : udNonVersate) {
					context.getBusinessObject(SerieEjb.class)
						.createSerUdNonVersErr(serErrContenutoVerSerie,
							ud.getSerVSelUdNoversBucoId()
								.getIdUnitaDocNonVers());
				    }
				}
			    }
			}
			logger.info(SerieEjb.class.getSimpleName()
				+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo buchi numerazione");

			List<SerLacunaConsistVerSerie> lacuneList = helper
				.getSerLacunaConsistVerSerie(
					new BigDecimal(consistenza.getIdConsistVerSerie()),
					CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name(), null,
					null);
			for (SerLacunaConsistVerSerie lacuna : lacuneList) {
			    Long count = helper.countAroUdAppartVerSerieInPgInterval(
				    contenuto.getIdContenutoVerSerie(), lacuna.getNiIniLacuna(),
				    lacuna.getNiFinLacuna());
			    if (count > 0L) {
				String dlErr = "La lacuna che definisce l'intervallo "
					+ lacuna.getNiIniLacuna().toPlainString() + "-"
					+ lacuna.getNiFinLacuna().toPlainString()
					+ " \u00E8 errata in quanto esiste almeno una unit\u00E0 documentaria appartenente al contenuto con progressivo incluso nell'intervallo";
				context.getBusinessObject(SerieEjb.class)
					.createSerErrContenutoVerSerie(
						contenuto.getIdContenutoVerSerie(), pg++,
						CostantiDB.TipoErroreContenuto.LACUNA_ERRATA.name(),
						dlErr, CostantiDB.TipoGravitaErrore.ERRORE.name(),
						origineErrore);
			    }
			}
		    }
		    logger.info(SerieEjb.class.getSimpleName()
			    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controlli su chiave iniziale-finale delle ud");
		}
	    } else {
		logger.info(SerieEjb.class.getSimpleName()
			+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- Non esistono ud definite in AroUdAppartVerSerie");
		if (consistenza.getTiModConsistFirstLast() != null) {
		    if (consistenza.getTiModConsistFirstLast()
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.CODICE.name())) {
			String cdFirstUdAttesa;
			String cdLastUdAttesa;
			if ((cdFirstUdAttesa = consistenza.getCdFirstUnitaDocAttesa()) != null) {
			    String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia codice pari a '"
				    + cdFirstUdAttesa
				    + "', mentre il contenuto non presenta alcuna unit\u00E0 documentaria";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
			if ((cdLastUdAttesa = consistenza.getCdLastUnitaDocAttesa()) != null) {
			    String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia codice pari a '"
				    + cdLastUdAttesa
				    + "', mentre il contenuto non presenta alcuna unit\u00E0 documentaria";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_FINALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
		    } else if (consistenza.getTiModConsistFirstLast()
			    .equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
			BigDecimal niFirstUdAttesa;
			BigDecimal niLastUdAttesa;
			if ((niFirstUdAttesa = consistenza.getNiFirstUnitaDocAttesa()) != null) {
			    String dlErr = "La consistenza attesa prevede che la prima unit\u00E0 documentaria abbia progressivo pari a '"
				    + niFirstUdAttesa
				    + "', mentre il contenuto non presenta alcuna unit\u00E0 documentaria";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_INIZIALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
			if ((niLastUdAttesa = consistenza.getNiLastUnitaDocAttesa()) != null) {
			    String dlErr = "La consistenza attesa prevede che l'ultima unit\u00E0 documentaria abbia progressivo pari a '"
				    + niLastUdAttesa
				    + "', mentre il contenuto non presenta alcuna unit\u00E0 documentaria";
			    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				    contenuto.getIdContenutoVerSerie(), pg++,
				    CostantiDB.TipoErroreContenuto.CHIAVE_FINALE.name(), dlErr,
				    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
			}
		    }
		}
	    }
	}

	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controlli sulla consistenza attesa");
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Verifica presenza di UD non selezionate nel contenuto CALCOLATO");
	Calendar cal = Calendar.getInstance();
	cal.set(2010, Calendar.DECEMBER, 1, 0, 0, 0);
	cal.set(Calendar.MILLISECOND, 0);
	Date dataRiferimento = cal.getTime();
	if (contenuto.getTiContenutoVerSerie()
		.equals(CostantiDB.TipoContenutoVerSerie.CALCOLATO.name())) {
	    List<SerQueryContenutoVerSerie> queries = helper.getSerQueryContenutoVerSerie(
		    new BigDecimal(contenuto.getIdContenutoVerSerie()));
	    pg = context.getBusinessObject(SerieEjb.class)
		    .controllaUdNonSelezionateInContenutoCalcolato(queries, contenuto, pg,
			    dataRiferimento, origineErrore);
	    SerStatoVerSerie statoVerSerie = helper.getLastSerStatoVerSerieWithStatus(
		    idVerSerie.longValue(), CostantiDB.StatoVersioneSerie.APERTA.name());
	    if (statoVerSerie != null) {
		Date dtRif = statoVerSerie.getDtRegStatoVerSerie();
		logger.info(SerieEjb.class.getSimpleName()
			+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo unit\u00E0 documentarie versate dopo calcolo");
		pg = context.getBusinessObject(SerieEjb.class).controllaUdVersateDopoCalcolo(
			queries, contenuto.getIdContenutoVerSerie(), contenuto, pg, dtRif,
			origineErrore);
		logger.info(SerieEjb.class.getSimpleName()
			+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo unit\u00E0 documentarie versate dopo calcolo");
	    }
	} else if (contenuto.getTiContenutoVerSerie()
		.equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
	    SerContenutoVerSerie contenutoCalcolato = helper.getSerContenutoVerSerie(
		    verSerie.getIdVerSerie(), CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
	    if (contenutoCalcolato != null) {
		List<SerQueryContenutoVerSerie> queries = helper.getSerQueryContenutoVerSerie(
			new BigDecimal(contenutoCalcolato.getIdContenutoVerSerie()));
		pg = context.getBusinessObject(SerieEjb.class)
			.controllaUdNonSelezionateInContenutoCalcolato(queries, contenuto, pg,
				dataRiferimento, origineErrore);
		SerStatoVerSerie statoVerSerie = helper.getLastSerStatoVerSerieWithStatus(
			idVerSerie.longValue(), CostantiDB.StatoVersioneSerie.APERTA.name());
		if (statoVerSerie != null) {
		    Date dtRif = statoVerSerie.getDtRegStatoVerSerie();
		    logger.info(SerieEjb.class.getSimpleName()
			    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo unit\u00E0 documentarie versate dopo calcolo");
		    pg = context.getBusinessObject(SerieEjb.class).controllaUdVersateDopoCalcolo(
			    queries, contenuto.getIdContenutoVerSerie(), contenutoCalcolato, pg,
			    dtRif, origineErrore);
		    logger.info(SerieEjb.class.getSimpleName()
			    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo unit\u00E0 documentarie versate dopo calcolo");
		}
	    }
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Verifica presenza di UD non selezionate nel contenuto CALCOLATO");
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Verifica presenza di UD annullate");
	pg = context.getBusinessObject(SerieEjb.class).controllaUdAnnullate(
		verSerie.getSerSerie().getDecTipoSerie().getIdTipoSerie(), contenuto,
		dataRiferimento, pg, origineErrore);
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Verifica presenza di UD annullate");
	if (StringUtils.isNotBlank(verSerie.getDsListaAnniSelSerie())
		&& verSerie.getDsListaAnniSelSerie().length() > 5) {
	    // se per la versione serie e' definita la lista degli anni di selezione delle unita doc
	    // e se tale lista
	    // contiene altri anni oltre a quello della serie
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Verifica anni di selezione ud");
	    String dsListaAnniSelSerie = verSerie.getDsListaAnniSelSerie()
		    .replace(verSerie.getSerSerie().getAaSerie().toPlainString(), "")
		    .replace(",,", ",");
	    String[] anni = StringUtils.split(dsListaAnniSelSerie, ",");
	    for (String anno : anni) {
		BigDecimal aaSerieUd = new BigDecimal(anno);
		Long count = helper.countSerVSelUdNovers(idVerSerie.longValue(), aaSerieUd);
		if (count > 0L) {
		    String dlErr = "Sono presenti unit\u00E0 documentarie non versate, relative all'anno "
			    + anno
			    + ", diverso da quello della serie, che potrebbero appartenere alla serie";
		    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			    contenuto.getIdContenutoVerSerie(), pg++,
			    CostantiDB.TipoErroreContenuto.UD_NON_VERS.name(), dlErr,
			    CostantiDB.TipoGravitaErrore.WARNING.name(), origineErrore);
		}
	    }
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Verifica anni di selezione ud");
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Verifica presenza di anomalie di ordinamento delle unit\u00E0 documentarie");
	AroUdAppartVerSerie first = helper.findById(AroUdAppartVerSerie.class,
		contenuto.getIdFirstUdAppartVerSerie());
	AroUdAppartVerSerie last = helper.findById(AroUdAppartVerSerie.class,
		contenuto.getIdLastUdAppartVerSerie());
	Date min = helper.getMinDtUnitaDocFromUdAppartVerSerie(contenuto.getIdContenutoVerSerie());
	Date max = helper.getMaxDtUnitaDocFromUdAppartVerSerie(contenuto.getIdContenutoVerSerie());

	if (first != null && last != null) {
	    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
	    logger.debug(SerieEjb.class.getSimpleName()
		    + "--- Controllo date ud: DATA MINIMA delle ud - " + df.format(min)
		    + ". DATA PRIMA UD - " + df.format(first.getDtUdSerie()));
	    if (first.getDtUdSerie().getTime() != min.getTime()) {
		String dlErr = "La prima unit\u00E0 documentaria ha data ("
			+ df.format(first.getDtUdSerie())
			+ ") superiore alla data minima delle unit\u00E0 documentarie appartenenti al contenuto ("
			+ df.format(min) + ")";
		context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			contenuto.getIdContenutoVerSerie(), pg++,
			CostantiDB.TipoErroreContenuto.ANOMALIA_ORDINAMENTO.name(), dlErr,
			CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
	    }
	    logger.debug(SerieEjb.class.getSimpleName()
		    + "--- Controllo date ud: DATA MASSIMA delle ud - " + df.format(max)
		    + ". DATA ULTIMA UD - " + df.format(last.getDtUdSerie()));
	    if (last.getDtUdSerie().getTime() != max.getTime()) {
		String dlErr = "L'ultima unit\u00E0 documentaria ha data ("
			+ df.format(last.getDtUdSerie())
			+ ") inferiore alla data massima delle unit\u00E0 documentarie appartenenti al contenuto ("
			+ df.format(max) + ")";
		context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			contenuto.getIdContenutoVerSerie(), pg++,
			CostantiDB.TipoErroreContenuto.ANOMALIA_ORDINAMENTO.name(), dlErr,
			CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
	    }
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Verifica presenza di anomalie di ordinamento delle unit\u00E0 documentarie");
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo buchi numerazione FUORI CONSISTENZA");
	DecTipoSerie tipoSerie = verSerie.getSerSerie().getDecTipoSerie();
	logger.debug(SerieEjb.class.getSimpleName()
		+ "--- Controllo se per ogni registro-tipoUd \u00E8 definito il progressivo e "
		+ "se la consistenza non esiste oppure esiste e non \u00E8 definita la modalita' di acquisizione oppure \u00E8 CODICE ");
	if (helper.countSerieWithoutPgSerieUd(tipoSerie.getIdTipoSerie()) == 0L
		&& (serConsistVerSeries.isEmpty()
			|| serConsistVerSeries.get(0).getTiModConsistFirstLast() == null
			|| serConsistVerSeries.get(0).getTiModConsistFirstLast()
				.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CODICE.name()))) {
	    logger.debug(SerieEjb.class.getSimpleName()
		    + "--- Verifico la presenza di buchi di numerazione, per ognuno creo un errore");
	    List<SerVBucoNumerazioneUd> listaBuchiNum = helper
		    .getSerVBucoNumerazioneUd(contenuto.getIdContenutoVerSerie());
	    for (SerVBucoNumerazioneUd buco : listaBuchiNum) {
		BigDecimal pgUdSerIniBuco = buco.getSerVBucoNumerazioneUdId().getPgUdSerIniBuco();
		BigDecimal pgUdSerFinBuco = buco.getPgUdSerFinBuco();
		String dlErr = "Le unit\u00E0 documentarie con progressivo compreso nell'intervallo "
			+ pgUdSerIniBuco.toPlainString() + "-" + pgUdSerFinBuco.toPlainString()
			+ " non sono presenti nel contenuto";
		SerErrContenutoVerSerie serErrContenutoVerSerie = context
			.getBusinessObject(SerieEjb.class)
			.createSerErrContenutoVerSerie(contenuto.getIdContenutoVerSerie(), pg++,
				CostantiDB.TipoErroreContenuto.BUCO_NUMERAZIONE.name(), dlErr,
				CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
		/*
		 * se il tipo di serie della serie del contenuto in input prevede la
		 * possibilit\u00E0 di selezionare le unit\u00E0 doc non versate a fronte di un buco
		 * di numerazione (vedi vista SER_V_CHK_DEF_UD_NOVERS_BUCO (cioe per tutte le coppie
		 * registro ? tipo unita doc sia definito PG_UD_SERIE mediante il solo NUMERO di
		 * tipo DATO_PROFILO e che ogni registro preveda, per ogni intervallo di anni, una
		 * sola parte che definisce il progressivo))
		 */
		logger.info(SerieEjb.class.getSimpleName()
			+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- START - Controllo unit\u00E0 documentarie non versate per buco di numerazione");
		SerVChkDefUdNoversBuco checkBuchi = helper
			.findViewById(SerVChkDefUdNoversBuco.class, idVerSerie);
		if (checkBuchi != null && checkBuchi.getFlDefUdNonVers().equals("1")) {
		    /*
		     * unit\u00E0 doc non versate relative ai registri previsti dal tipo serie ed
		     * all'anno della serie, il cui numero e' compreso nel buco di numerazione
		     */
		    List<SerVSelUdNoversBuco> udNonVersate = helper
			    .getSerVSelUdNoversBuco(idVerSerie, pgUdSerIniBuco, pgUdSerFinBuco);
		    for (SerVSelUdNoversBuco ud : udNonVersate) {
			context.getBusinessObject(SerieEjb.class).createSerUdNonVersErr(
				serErrContenutoVerSerie,
				ud.getSerVSelUdNoversBucoId().getIdUnitaDocNonVers());
		    }
		}
		logger.info(SerieEjb.class.getSimpleName()
			+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo unit\u00E0 documentarie non versate per buco di numerazione");
	    }
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_CONTENUTO_SERIE_UD --- END - Controllo buchi numerazione FUORI CONSISTENZA");

	Date now = Calendar.getInstance().getTime();
	contenuto.setTiStatoContenutoVerSerie(
		CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name());
	contenuto.setDtStatoContenutoVerSerie(now);

	if (contenuto.getTiContenutoVerSerie()
		.equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
	    SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		    verSerie.getIdStatoVerSerieCor());
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Aggiorna lo stato della versione");
	    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		    .createSerStatoVerSerie(statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
			    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
			    "Controllo contenuto serie di tipo "
				    + contenuto.getTiContenutoVerSerie(),
			    null, idUser, now, verSerie.getIdVerSerie());

	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Eseguo la persist del nuovo stato versione");
	    helper.insertEntity(statoVerSerie, false);
	    verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));
	    if (calcoloAutomatico) {
		if (helper.countAroUdAppartVerSerie(contenuto.getIdContenutoVerSerie()) == 0) {
		    // se il numero delle unita doc del contenuto di tipo EFFETTIVO e' pari a 0
		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Contenuto effettivo 'vuoto' - Aggiorna lo stato della versione");
		    SerStatoVerSerie statoVerSerieAutom = context.getBusinessObject(SerieEjb.class)
			    .createSerStatoVerSerie(
				    statoVerSerie.getPgStatoVerSerie().add(BigDecimal.ONE),
				    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
				    "Il numero delle unit\u00E0 documentarie del contenuto di tipo EFFETTIVO \u00E8 pari a zero",
				    null, idUser, now, verSerie.getIdVerSerie());

		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Eseguo la persist del nuovo stato versione");
		    helper.insertEntity(statoVerSerieAutom, false);
		    verSerie.setIdStatoVerSerieCor(
			    new BigDecimal(statoVerSerieAutom.getIdStatoVerSerie()));

		    helper.deleteSerVerSerieDaElab(idVerSerie);

		    contenuto.setTiStatoContenutoVerSerie(
			    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
		    contenuto.setDtStatoContenutoVerSerie(now);
		} else if ((helper.countSerErrContenutoVerSerie(contenuto.getIdContenutoVerSerie(),
			CostantiDB.TipoErroreContenuto.CHIAVE_DOPPIA.name(),
			CostantiDB.TipoErroreContenuto.UD_NON_VERS.name(),
			CostantiDB.TipoErroreContenuto.UD_ANNULLATA.name(),
			CostantiDB.TipoErroreContenuto.ANOMALIA_ORDINAMENTO.name(),
			CostantiDB.TipoErroreContenuto.UD_NON_SELEZIONATE.name(),
			CostantiDB.TipoErroreContenuto.UD_VERSATA_DOPO_CALCOLO.name()) > 0L)
			|| (helper.countSerErrContenutoVerSerie(
				new BigDecimal(contenuto.getIdContenutoVerSerie()),
				CostantiDB.TipoGravitaErrore.ERRORE.name(),
				CostantiDB.TipoErroreContenuto.CONSISTENZA_NON_DEFINITA.name(),
				null) > 0L)) {
		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Aggiorna lo stato della versione per presenza errori nella generazione automatica");
		    SerStatoVerSerie statoVerSerieAutom = context.getBusinessObject(SerieEjb.class)
			    .createSerStatoVerSerie(
				    statoVerSerie.getPgStatoVerSerie().add(BigDecimal.ONE),
				    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
				    "Il controllo contenuto serie di tipo "
					    + contenuto.getTiContenutoVerSerie()
					    + " ha rilevato almeno un errore di tipo CHIAVE_DOPPIA o UD_NON_VERS "
					    + "o CONSISTENZA_NON_DEFINITA o UD_ANNULLATA o ANOMALIA_ORDINAMENTO "
					    + "o UD_NON_SELEZIONATE o UD_VERSATA_DOPO_CALCOLO",
				    null, idUser, now, verSerie.getIdVerSerie());

		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Eseguo la persist del nuovo stato versione");
		    helper.insertEntity(statoVerSerieAutom, false);
		    verSerie.setIdStatoVerSerieCor(
			    new BigDecimal(statoVerSerieAutom.getIdStatoVerSerie()));

		    helper.deleteSerVerSerieDaElab(idVerSerie);

		    contenuto.setTiStatoContenutoVerSerie(
			    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
		    contenuto.setDtStatoContenutoVerSerie(now);
		} else if (tipoSerie.getTiStatoVerSerieAutom() == null) {
		    throw new IllegalStateException(
			    "Impostato calcolo automatico per il controllo della serie senza aver definito lo stato della versione all'interno del tipo di serie");
		} else if (tipoSerie.getTiStatoVerSerieAutom()
			.equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
		    SerVerSerieDaElab verSerieDaElab = helper
			    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
		    verSerieDaElab.setTiStatoVerSerie(
			    CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());

		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Aggiorna lo stato della versione per la validazione automatica");
		    SerStatoVerSerie statoVerSerieAutom = context.getBusinessObject(SerieEjb.class)
			    .createSerStatoVerSerie(
				    statoVerSerie.getPgStatoVerSerie().add(BigDecimal.ONE),
				    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(),
				    "La serie deve essere validata", null, idUser, now,
				    verSerie.getIdVerSerie());

		    logger.info(SerieEjb.class.getSimpleName()
			    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Eseguo la persist del nuovo stato versione");
		    helper.insertEntity(statoVerSerieAutom, false);
		    verSerie.setIdStatoVerSerieCor(
			    new BigDecimal(statoVerSerieAutom.getIdStatoVerSerie()));
		} else {
		    helper.deleteSerVerSerieDaElab(idVerSerie);
		}
	    } else {
		helper.deleteSerVerSerieDaElab(idVerSerie);
	    }
	}
    }

    public int controllaUdAnnullate(Long idTipoSerie, SerContenutoVerSerie contenuto,
	    Date dataRiferimento, int pg, final String origineErrore) throws IllegalStateException {
	List<DecTipoSerieUd> tipiSerieUd = helper.getDecTipoSerieUdNoSelUnitaDocAnnul(idTipoSerie);
	for (DecTipoSerieUd tipoSerieUd : tipiSerieUd) {
	    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
	    List<AroUdAppartVerSerie> aroUnitaDocs = helper
		    .getAroUnitaDocAnnullateInContenutoWithTipoSerieUd(
			    contenuto.getIdContenutoVerSerie(),
			    tipoSerieUd.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
			    tipoSerieUd.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), dataRiferimento);
	    for (AroUdAppartVerSerie udAppart : aroUnitaDocs) {
		AroUnitaDoc ud = udAppart.getAroUnitaDoc();
		String dlErr = "L'unit\u00E0 documentaria " + udAppart.getCdUdSerie() + " ( "
			+ ud.getCdRegistroKeyUnitaDoc() + "-"
			+ ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
			+ " ) \u00E8 stata annullata successivamente all'istante "
			+ df.format(dataRiferimento) + " e la coppia registro "
			+ tipoSerieUd.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc()
			+ " - tipo di unit\u00E0 documentaria "
			+ tipoSerieUd.getDecTipoUnitaDoc().getNmTipoUnitaDoc()
			+ ", previsti dal tipo di serie, "
			+ "non consente di selezionare unit\u00E0 documentarie annullate";
		context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			contenuto.getIdContenutoVerSerie(), pg++,
			CostantiDB.TipoErroreContenuto.UD_ANNULLATA.name(), dlErr,
			CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
	    }
	}
	return pg;
    }

    public int controllaContenutoEffettivoNonVuoto(SerContenutoVerSerie contenuto, int pg,
	    final String origineErrore) {
	if (helper.countAroUdAppartVerSerie(contenuto.getIdContenutoVerSerie()) == 0) {
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- CONTROLLA_CONTENUTO_SERIE_UD --- Contenuto effettivo 'vuoto'");
	    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
		    contenuto.getIdContenutoVerSerie(), pg++,
		    CostantiDB.TipoErroreContenuto.CONTENUTO_EFFETTIVO_VUOTO.name(),
		    "Il contenuto di tipo EFFETTIVO non contiene unit\u00E0 documentarie",
		    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
	}
	return pg;
    }

    public int controllaUdNonSelezionateInContenutoCalcolato(
	    List<SerQueryContenutoVerSerie> queries, SerContenutoVerSerie contenuto, int pg,
	    Date dataRiferimento, String origineErrore) throws IllegalStateException {
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
	for (SerQueryContenutoVerSerie queryRecord : queries) {
	    String query = queryRecord.getBlQuery();
	    int indexCrop = query.indexOf("WHERE tmp.");
	    if (indexCrop != -1) {
		String croppedQuery = query.substring(0, indexCrop);
		croppedQuery += "WHERE (tmp.key_ud_serie IS NULL OR tmp.dt_ud_serie IS NULL OR tmp.info_ud_serie IS NULL OR tmp.ds_key_ord_ud_serie IS NULL)";
		SerVerSerie verSerie = contenuto.getSerVerSerie();
		if (verSerie.getDtInizioSelSerie() != null
			&& verSerie.getDtFineSelSerie() != null) {
		    SimpleDateFormat queryDf = new SimpleDateFormat(
			    Constants.DATE_FORMAT_DATE_TYPE);
		    StringBuilder tmpBuilder = new StringBuilder(croppedQuery);
		    tmpBuilder.append(" AND ").append("tmp.dt_ud_serie >= ").append("to_date('")
			    .append(queryDf.format(verSerie.getDtInizioSelSerie()))
			    .append("', 'dd/mm/yyyy')").append(" AND ")
			    .append("tmp.dt_ud_serie <= ").append("to_date('")
			    .append(queryDf.format(verSerie.getDtFineSelSerie()))
			    .append("', 'dd/mm/yyyy')");
		    croppedQuery = tmpBuilder.toString();
		}
		List<ResultVCalcoloSerieUd> resultList = helper.executeQueryList(croppedQuery);
		for (ResultVCalcoloSerieUd result : resultList) {
		    BigDecimal idUnitaDoc = result.getIdUnitaDoc();
		    AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
		    if (ud.getDtCreazione().after(dataRiferimento)) {
			String dlErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc()
				+ "-" + ud.getAaKeyUnitaDoc().toPlainString() + "-"
				+ ud.getCdKeyUnitaDoc() + ", versata successivamente all'istante "
				+ df.format(dataRiferimento)
				+ ",  non \u00E8 stata selezionata nel contenuto di tipo "
				+ contenuto.getTiContenutoVerSerie() + " perch\u00E9 "
				+ "almeno una delle informazioni che la descrivono come appartenente alla serie \u00E8 nulla";
			context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
				contenuto.getIdContenutoVerSerie(), pg++,
				CostantiDB.TipoErroreContenuto.UD_NON_SELEZIONATE.name(), dlErr,
				CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
		    }
		}
	    }
	}
	return pg;
    }

    public int controllaUdVersateDopoCalcolo(List<SerQueryContenutoVerSerie> queries,
	    Long idContenutoVerSerie, SerContenutoVerSerie contenutoCalcolato, int pg,
	    Date dataRiferimento, final String origineErrore) throws IllegalStateException {
	SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
	SimpleDateFormat queryDf = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
	for (SerQueryContenutoVerSerie queryRecord : queries) {
	    String query = queryRecord.getBlQuery();
	    if (StringUtils.containsIgnoreCase(query, "ORDER BY")) {
		String choppedQuery = StringUtils.substringBeforeLast(query, "ORDER BY");
		String orderBy = StringUtils.substringAfterLast(query, "ORDER BY");
		query = choppedQuery + " and tmp.dt_creazione > to_date('"
			+ queryDf.format(dataRiferimento) + "', 'dd/mm/yyyy') ORDER BY" + orderBy;
	    } else {
		query += " and tmp.dt_creazione > to_date('" + queryDf.format(dataRiferimento)
			+ "', 'dd/mm/yyyy') ";
	    }

	    List<ResultVCalcoloSerieUd> resultList = helper.executeQueryList(query);
	    for (ResultVCalcoloSerieUd result : resultList) {
		BigDecimal idUnitaDoc = result.getIdUnitaDoc();
		AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
		if (!helper.existUdInSerie(contenutoCalcolato.getIdContenutoVerSerie(),
			idUnitaDoc)) {
		    String dlErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc()
			    + "-" + ud.getAaKeyUnitaDoc().toPlainString() + "-"
			    + ud.getCdKeyUnitaDoc()
			    + " non \u00E8 presente nel contenuto perch\u00E9 versata successivamente all'istante "
			    + df.format(dataRiferimento);
		    context.getBusinessObject(SerieEjb.class).createSerErrContenutoVerSerie(
			    idContenutoVerSerie, pg++,
			    CostantiDB.TipoErroreContenuto.UD_VERSATA_DOPO_CALCOLO.name(), dlErr,
			    CostantiDB.TipoGravitaErrore.ERRORE.name(), origineErrore);
		}
	    }
	}
	return pg;
    }

    public SerErrContenutoVerSerie createSerErrContenutoVerSerie(long idContenutoVerSerie,
	    int pgErr, String tiErr, String dlErr, String tiGravitaErr, String tiOrigineErr) {
	SerContenutoVerSerie contenuto = helper.findById(SerContenutoVerSerie.class,
		idContenutoVerSerie);
	if (contenuto.getSerErrContenutoVerSeries() == null) {
	    contenuto.setSerErrContenutoVerSeries(new ArrayList<SerErrContenutoVerSerie>());
	}
	SerErrContenutoVerSerie errContenuto = new SerErrContenutoVerSerie();
	errContenuto.setPgErr(new BigDecimal(pgErr));
	errContenuto.setTiErr(tiErr);
	errContenuto.setDlErr(dlErr);
	errContenuto.setTiGravitaErr(tiGravitaErr);
	errContenuto.setTiOrigineErr(tiOrigineErr);
	contenuto.addSerErrContenutoVerSery(errContenuto);

	return errContenuto;
    }

    public void createSerUdNonVersErr(SerErrContenutoVerSerie serErrContenuto,
	    BigDecimal idUnitaDocNonVers) {
	SerUdNonVersErr udNonVers = new SerUdNonVersErr();
	VrsUnitaDocNonVer udNonVer = helper.findById(VrsUnitaDocNonVer.class, idUnitaDocNonVers);
	udNonVers.setVrsUnitaDocNonVer(udNonVer);
	serErrContenuto.addSerUdNonVersErr(udNonVers);
    }
    // </editor-fold>

    public boolean existLacuna(BigDecimal idConsistVerSerie, BigDecimal idLacuna,
	    BigDecimal niIniLacuna, BigDecimal niFinLacuna) {
	Long result = helper.countSerLacunaConsistVerSerie(idConsistVerSerie, idLacuna,
		CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name(), niIniLacuna, niFinLacuna);
	return result > 0L;
    }

    public BigDecimal getMaxPgLacuna(BigDecimal idConsistVerSerie) {
	List<SerLacunaConsistVerSerie> list = helper.getSerLacunaConsistVerSerie(idConsistVerSerie);
	BigDecimal pg = BigDecimal.ZERO;
	if (list != null && !list.isEmpty()) {
	    SerLacunaConsistVerSerie last = list.get(list.size() - 1);
	    pg = last.getPgLacuna();
	}
	return pg;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveLacuna(long idUser, BigDecimal idConsistVerSerie, BigDecimal pg,
	    String tiLacuna, String tiModLacuna, BigDecimal niIniLacuna, BigDecimal niFinLacuna,
	    String dlLacuna, String dlNotaLacuna) throws ParerUserError {
	logger.debug("Eseguo il salvataggio della lacuna");
	BigDecimal id = null;
	try {
	    SerConsistVerSerie consistVerSerie = helper.findById(SerConsistVerSerie.class,
		    idConsistVerSerie);
	    if (consistVerSerie.getSerLacunaConsistVerSeries() == null) {
		consistVerSerie
			.setSerLacunaConsistVerSeries(new ArrayList<SerLacunaConsistVerSerie>());
	    }
	    SerLacunaConsistVerSerie lacuna = context.getBusinessObject(SerieEjb.class)
		    .createSerLacunaConsistVerSerie(pg, tiLacuna, tiModLacuna, niIniLacuna,
			    niFinLacuna, dlLacuna, dlNotaLacuna, consistVerSerie);
	    helper.insertEntity(lacuna, false);
	    id = new BigDecimal(lacuna.getIdLacunaConsistVerSerie());

	    handleStatoSerieDaConsistenzaAttesa(consistVerSerie.getSerVerSerie(),
		    "Modifica consistenza attesa", idUser);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio della lacuna ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return id;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerLacunaConsistVerSerie createSerLacunaConsistVerSerie(BigDecimal pg, String tiLacuna,
	    String tiModLacuna, BigDecimal niIniLacuna, BigDecimal niFinLacuna, String dlLacuna,
	    String dlNotaLacuna, SerConsistVerSerie consistVerSerie) {
	SerLacunaConsistVerSerie lacuna = new SerLacunaConsistVerSerie();
	lacuna.setPgLacuna(pg);
	lacuna.setTiLacuna(tiLacuna);
	lacuna.setTiModLacuna(tiModLacuna);
	lacuna.setNiIniLacuna(niIniLacuna);
	lacuna.setNiFinLacuna(niFinLacuna);
	lacuna.setDlLacuna(dlLacuna);
	lacuna.setDlNotaLacuna(dlNotaLacuna);
	consistVerSerie.addSerLacunaConsistVerSery(lacuna);
	return lacuna;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveLacuna(long idUser, BigDecimal idLacunaConsistVerSerie, String tiLacuna,
	    BigDecimal niIniLacuna, BigDecimal niFinLacuna, String dlLacuna, String dlNotaLacuna)
	    throws ParerUserError {
	logger.debug("Eseguo il salvataggio della lacuna");
	try {
	    SerLacunaConsistVerSerie lacuna = helper.findById(SerLacunaConsistVerSerie.class,
		    idLacunaConsistVerSerie);
	    lacuna.setTiLacuna(tiLacuna);
	    lacuna.setNiIniLacuna(niIniLacuna);
	    lacuna.setNiFinLacuna(niFinLacuna);
	    lacuna.setDlLacuna(dlLacuna);
	    lacuna.setDlNotaLacuna(dlNotaLacuna);

	    handleStatoSerieDaConsistenzaAttesa(lacuna.getSerConsistVerSerie().getSerVerSerie(),
		    "Modifica consistenza attesa", idUser);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio della lacuna ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteLacuna(long idUser, BigDecimal idLacunaConsistVerSerie)
	    throws ParerUserError {
	logger.debug("Eseguo l'eliminazione della lacuna");
	try {
	    SerLacunaConsistVerSerie lacuna = helper.findById(SerLacunaConsistVerSerie.class,
		    idLacunaConsistVerSerie);
	    handleStatoSerieDaConsistenzaAttesa(lacuna.getSerConsistVerSerie().getSerVerSerie(),
		    "Modifica consistenza attesa", idUser);

	    helper.removeEntity(lacuna, true);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nell'eliminazione della lacuna ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
    }

    public boolean checkErroriContenutoEffettivo(BigDecimal idContenutoVerSerie,
	    String tiGravitaErr) {
	Long count = helper.countSerErrContenutoVerSerie(idContenutoVerSerie, tiGravitaErr);
	return count > 0L;
    }

    public boolean checkErroriContenutoEffettivo(BigDecimal idContenutoVerSerie,
	    String tiGravitaErr, String tiErr, String tiOrigineErr) {
	Long count = helper.countSerErrContenutoVerSerie(idContenutoVerSerie, tiGravitaErr, tiErr,
		tiOrigineErr);
	return count > 0L;
    }

    public static final String AZIONE_SERIE_APERTA = "Riapertura della serie";
    public static final String AZIONE_SERIE_DA_VALIDARE = "Deve essere possibile validare la serie";
    public static final String AZIONE_SERIE_DA_CONTROLLARE = "La serie deve essere ricontrollata";
    public static final String AZIONE_SERIE_VALIDAZIONE_IN_CORSO = "La serie deve essere validata";
    public static final String AZIONE_SERIE_FORZA_VALIDAZIONE = "La serie deve essere validata anche se sono presenti errori sul contenuto EFFETTIVO rilevati nel corso della validazione precedentemente attivata";
    public static final String AZIONE_SERIE_ANNULLATA = "La serie deve essere annullata";
    public static final String AZIONE_SERIE_AGGIORNA = "Almeno una unit\u00E0 documentaria della serie deve essere modificata";

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cambiaStatoSerie(long idUser, BigDecimal idSerie, BigDecimal idVerSerie,
	    BigDecimal idContenutoVerSerie, String azione, String stato, String nota,
	    String statoSerieCorrente, String tipoOperazione) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    try {
		if (checkVersione(idVerSerie, statoSerieCorrente)) {
		    SerContenutoVerSerie contenutoEff = helper.findById(SerContenutoVerSerie.class,
			    idContenutoVerSerie);
		    if (contenutoEff != null) {
			if (!contenutoEff.getTiContenutoVerSerie()
				.equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
			    throw new IllegalArgumentException("Contenuto non di tipo EFFETTIVO");
			}
			if (idVerSerie != null && contenutoEff.getSerVerSerie()
				.getIdVerSerie() != idVerSerie.longValue()) {
			    throw new IllegalArgumentException(
				    "Versione passata non corrispondente alla versione del contenuto di tipo EFFETTIVO");
			}
			SerVerSerie verSerie = contenutoEff.getSerVerSerie();
			if (contenutoEff.getTiStatoContenutoVerSerie()
				.equals(CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO
					.name())) {
			    throw new ParerUserError(
				    "Il controllo del contenuto effettivo \u00E8 in corso");
			}
			List<AroUnitaDoc> udInSerie = null;
			if (stato.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())
				|| stato.equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
				|| stato.equals(CostantiDB.StatoVersioneSerie.ANNULLATA.name())) {
			    udInSerie = helper.getLockAroUnitaDoc(idContenutoVerSerie);
			}
			if (stato.equals(
				CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())) {
			    if (!checkExistNoteObblig(idVerSerie)) {
				throw new ParerUserError(
					"La serie non pu\u00F2 assumere stato = VALIDATA perch\u00E9 alcuni tipi di nota obbligatori non sono definiti");
			    }
			    if (!checkStatoConservazioneUdInContenEff(idVerSerie)) {
				throw new ParerUserError(
					"La serie non pu\u00F2 assumere stato = VALIDATA perch\u00E9 alcune unit\u00E0 documentarie hanno stato di conservazione diverso da AIP_GENERATO e da VERSAMENTO_IN_ARCHIVIO e da IN_ARCHIVIO e da IN_CUSTODIA e da IN_VOLUME_DI_CONSERVAZIONE (cio\u00E8 esistono unit\u00E0 documentarie con stato pari a AIP_DA_GENERARE o AIP_IN_AGGIORNAMENTO o PRESA_IN_CARICO)");
			    }
			    if (checkSerieDaRigenerare(idVerSerie)) {
				throw new ParerUserError(
					"La serie non pu\u00F2 assumere stato = VALIDATA perch\u00E9 il tipo serie \u00E8 stato modificato e, quindi, la serie deve essere nuovamente calcolata e/o acquisita");
			    }
			}

			SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
				verSerie.getIdStatoVerSerieCor());
			Date now = Calendar.getInstance().getTime();
			logger.info(SerieEjb.class.getSimpleName()
				+ " --- Aggiorna lo stato della versione");
			SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
				.createSerStatoVerSerie(
					statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
					stato, azione, nota, idUser, now, verSerie.getIdVerSerie());

			logger.info(SerieEjb.class.getSimpleName()
				+ " --- Eseguo la persist del nuovo stato versione");
			helper.insertEntity(statoVerSerie, false);
			verSerie.setIdStatoVerSerieCor(
				new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

			if (stato.equals(CostantiDB.StatoVersioneSerie.APERTA.name())) {
			    helper.deleteSerContenutoVerSerie(verSerie.getIdVerSerie(),
				    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
			    helper.deleteSerVerSerieDaElab(idVerSerie);
			}
			if (stato.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())
				&& udInSerie != null) {
			    // MEV #31162
			    List<Long> idUnitaDocList = helper
				    .getStatoConservazioneAroUnitaDocInContenuto(
					    idContenutoVerSerie.longValue(),
					    CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE
						    .name());
			    // end MEV #31162

			    helper.updateStatoConservazioneAroUnitaDocInContenuto(
				    idContenutoVerSerie.longValue(),
				    CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE
					    .name(),
				    CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE.name());

			    // MEV #31162
			    IamUser utente = helper.findById(IamUser.class, idUser);
			    String modalita = null;
			    String agente = null;
			    // MAC #38013: cambiato in VALIDA_SERIE sia per JOB che per online
			    String tipoEvento = Constants.VALIDA_SERIE;
			    agente = utente.getNmUserid();
			    modalita = Constants.FUNZIONALITA_ONLINE;

			    // Valida serie da JOB
			    // if (tipoOperazione.equals("VALIDA_SERIE")) {
			    // agente = Constants.JOB_VALIDA_SERIE;
			    // modalita = Constants.NM_AGENTE_JOB_SACER;
			    // }
			    // Valida serie da cambia stato online
			    // else if (tipoOperazione.equals("CAMBIA_STATO")) {
			    // agente = utente.getNmUserid();
			    // modalita = Constants.FUNZIONALITA_ONLINE;
			    // }
			    for (Long idUnitaDoc : idUnitaDocList) {
				udEjb.insertLogStatoConservUd(idUnitaDoc, agente, tipoEvento,
					CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE
						.name(),
					modalita);
			    }
			    // end MEV #31162

			    helper.deleteSerVerSerieDaElab(idVerSerie);
			}
			if (stato.equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
				&& udInSerie != null) {
			    contenutoEff.setTiStatoContenutoVerSerie(
				    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
					    .name());
			    contenutoEff
				    .setDtStatoContenutoVerSerie(Calendar.getInstance().getTime());

			    serie.setIdStatoSerieCor(null);
			    helper.deleteSerFileVerSerie(idVerSerie,
				    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name());
			    helper.deleteSerVolVerSerie(idVerSerie);
			    helper.deleteSerVerSerieDaElab(idVerSerie);

			    // MEV #31162
			    List<String> statiConservOld = new ArrayList<>();
			    statiConservOld.add(
				    CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO
					    .name());
			    List<Long> idUnitaDocList = helper
				    .getStatoConservazioneAroUnitaDocWithoutOtherSeries(idSerie,
					    verSerie.getIdVerSerie(), statiConservOld);
			    // end MEV #31162

			    helper.updateStatoConservazioneAroUnitaDocWithoutOtherSeries(idSerie,
				    verSerie.getIdVerSerie(),
				    CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO
					    .name(),
				    CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name());

			    // MEV #31162
			    IamUser utente = helper.findById(IamUser.class, idUser);
			    String agente = utente.getNmUserid();
			    String modalita = Constants.FUNZIONALITA_ONLINE;
			    String tipoEvento = Constants.VERIFICA_SERIE;
			    for (Long idUnitaDoc : idUnitaDocList) {
				udEjb.insertLogStatoConservUd(idUnitaDoc, agente, tipoEvento,
					CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(),
					modalita);
			    }
			    // end MEV #31162

			}
			if (stato.equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
			    SerVerSerieDaElab serVerSerieDaElab = helper
				    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
			    if (serVerSerieDaElab != null) {
				if (!serVerSerieDaElab.getTiStatoVerSerie().equals(
					CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name())) {
				    throw new IllegalArgumentException(
					    "Impossibile impostare lo stato DA_VALIDARE sulla versione. Versione gi\u00E0 da elaborare con stato "
						    + serVerSerieDaElab.getTiStatoVerSerie());
				}
			    } else {
				SerVerSerieDaElab verSerieDaElab = new SerVerSerieDaElab();
				verSerieDaElab.setDtRegStatoVerSerie(now);
				verSerieDaElab.setIdStrut(new BigDecimal(
					verSerie.getSerSerie().getOrgStrut().getIdStrut()));
				verSerieDaElab.setTiStatoVerSerie(
					CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());
				verSerieDaElab.setSerVerSerie(verSerie);
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Eseguo la persist del nuovo verSerie da elab");
				helper.insertEntity(verSerieDaElab, false);
			    }
			}
			if (stato.equals(CostantiDB.StatoVersioneSerie.ANNULLATA.name())) {
			    logger.info(SerieEjb.class.getSimpleName()
				    + " --- In caso di stato annullata devo creare il nuovo stato serie di annullamento");
			    String nuovoStatoSerie;
			    switch (azione) {
			    case AZIONE_SERIE_ANNULLATA:
				nuovoStatoSerie = CostantiDB.StatoConservazioneSerie.ANNULLATA
					.name();
				break;
			    case AZIONE_SERIE_AGGIORNA:
				nuovoStatoSerie = CostantiDB.StatoConservazioneSerie.AIP_DA_AGGIORNARE
					.name();
				break;
			    default:
				nuovoStatoSerie = null;
				break;
			    }

			    SerStatoSerie statoSerie;
			    logger.debug(SerieEjb.class.getSimpleName()
				    + " --- Aggiorna lo stato della serie");

			    SerStatoSerie lastStatoSerie = helper
				    .getLastSerStatoSerie(serie.getIdSerie());
			    BigDecimal pg = lastStatoSerie != null
				    ? lastStatoSerie.getPgStatoSerie().add(BigDecimal.ONE)
				    : BigDecimal.ONE;
			    statoSerie = context.getBusinessObject(SerieEjb.class)
				    .createSerStatoSerie(pg, nuovoStatoSerie, azione, nota, idUser,
					    now, serie.getIdSerie());
			    logger.debug(SerieEjb.class.getSimpleName()
				    + " --- Eseguo la persist del nuovo stato serie");
			    helper.insertEntity(statoSerie, false);
			    serie.setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));
			    SerVerSerie verSerieToUpdate = null;

			    logger.debug(SerieEjb.class.getSimpleName()
				    + " --- Elimino la versione da SerVerSerieDaElab se esiste");
			    SerVerSerieDaElab serVerSerieDaElabByIdVerSerie = helper
				    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
			    if (serVerSerieDaElabByIdVerSerie != null) {
				helper.removeEntity(serVerSerieDaElabByIdVerSerie, false);
			    }

			    switch (azione) {
			    case AZIONE_SERIE_ANNULLATA:
				serie.setDtAnnul(now);
				verSerieToUpdate = verSerie;
				break;
			    case AZIONE_SERIE_AGGIORNA:
				logger.info(SerieEjb.class.getSimpleName()
					+ " --- Lo stato AGGIORNA crea una nuova versione per la serie con codice aumentato come copia della precedente");
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Salvataggio nuova versione");
				String cdVerSerieOld = verSerie.getCdVerSerie();
				String[] cdVerSeriePartial = cdVerSerieOld.split("\\.");
				Integer intVerSerie = Integer.parseInt(cdVerSeriePartial[1]);
				intVerSerie++;
				String cdVerSerie = cdVerSeriePartial[0] + "."
					+ String.format("%02d", intVerSerie);

				SerVerSerie verSerieNew = context.getBusinessObject(SerieEjb.class)
					.createSerVerSerie(cdVerSerie,
						verSerie.getDsListaAnniSelSerie(),
						verSerie.getDtCreazioneUnitaDocA(),
						verSerie.getDtCreazioneUnitaDocDa(),
						verSerie.getDtFineSelSerie(),
						verSerie.getDtInizioSelSerie(),
						verSerie.getDtRegUnitaDocA(),
						verSerie.getDtRegUnitaDocDa(),
						verSerie.getNiPeriodoSelSerie(),
						verSerie.getPgVerSerie().add(BigDecimal.ONE),
						verSerie.getTiPeriodoSelSerie(), serie);
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Copia delle note della versione");
				for (SerNotaVerSerie oldNota : verSerie.getSerNotaVerSeries()) {
				    SerNotaVerSerie notaSerie = new SerNotaVerSerie();
				    notaSerie.setDtNotaVerSerie(now);
				    notaSerie.setDecTipoNotaSerie(oldNota.getDecTipoNotaSerie());
				    notaSerie.setPgNotaVerSerie(oldNota.getPgNotaVerSerie());
				    notaSerie.setDsNotaVerSerie(oldNota.getDsNotaVerSerie());
				    oldNota.getIamUser().addSerNotaVerSery(notaSerie);

				    verSerieNew.addSerNotaVerSery(notaSerie);
				}

				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Copia della consistenza della vecchia versione");
				for (SerConsistVerSerie oldConsist : verSerie
					.getSerConsistVerSeries()) {
				    SerConsistVerSerie consistNew = new SerConsistVerSerie();
				    consistNew.setSerLacunaConsistVerSeries(
					    new ArrayList<SerLacunaConsistVerSerie>());
				    if (verSerieNew.getSerConsistVerSeries() == null) {
					verSerieNew.setSerConsistVerSeries(
						new ArrayList<SerConsistVerSerie>());
				    }
				    verSerieNew.addSerConsistVerSery(consistNew);
				    consistNew.setIamUser(oldConsist.getIamUser());
				    consistNew
					    .setNiUnitaDocAttese(oldConsist.getNiUnitaDocAttese());
				    consistNew.setTiModConsistFirstLast(
					    oldConsist.getTiModConsistFirstLast());
				    consistNew.setNiFirstUnitaDocAttesa(
					    oldConsist.getNiFirstUnitaDocAttesa());
				    consistNew.setNiLastUnitaDocAttesa(
					    oldConsist.getNiLastUnitaDocAttesa());
				    consistNew.setCdFirstUnitaDocAttesa(
					    oldConsist.getCdFirstUnitaDocAttesa());
				    consistNew.setCdLastUnitaDocAttesa(
					    oldConsist.getCdLastUnitaDocAttesa());
				    consistNew.setCdDocConsistVerSerie(
					    oldConsist.getCdDocConsistVerSerie());
				    consistNew.setDsDocConsistVerSerie(
					    oldConsist.getDsDocConsistVerSerie());
				    consistNew.setDtComunicConsistVerSerie(
					    oldConsist.getDtComunicConsistVerSerie());
				    logger.debug(SerieEjb.class.getSimpleName()
					    + " --- Copia delle lacune della vecchia consistenza");
				    for (SerLacunaConsistVerSerie oldLacuna : oldConsist
					    .getSerLacunaConsistVerSeries()) {
					context.getBusinessObject(SerieEjb.class)
						.createSerLacunaConsistVerSerie(
							oldLacuna.getPgLacuna(),
							oldLacuna.getTiLacuna(),
							oldLacuna.getTiModLacuna(),
							oldLacuna.getNiIniLacuna(),
							oldLacuna.getNiFinLacuna(),
							oldLacuna.getDlLacuna(),
							oldLacuna.getDlNotaLacuna(), consistNew);
				    }
				}

				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Copia dei file input della vecchia versione");
				for (SerFileInputVerSerie oldFileInput : verSerie
					.getSerFileInputVerSeries()) {
				    SerFileInputVerSerie fileInput = context
					    .getBusinessObject(SerieEjb.class)
					    .createSerFileInputVerSerie(
						    oldFileInput.getBlFileInputVerSerie()
							    .getBytes("UTF-8"),
						    oldFileInput.getCdDocFileInputVerSerie(),
						    oldFileInput.getDsDocFileInputVerSerie(),
						    oldFileInput.getFlFornitoEnte(),
						    oldFileInput.getTiScopoFileInputVerSerie(),
						    oldFileInput.getIamUser(), verSerieNew);
				    if (fileInput.getSerErrFileInputs() == null) {
					fileInput.setSerErrFileInputs(
						new ArrayList<SerErrFileInput>());
				    }
				    logger.debug(SerieEjb.class.getSimpleName()
					    + " --- Copia degli errori dei file input");
				    for (SerErrFileInput oldErrFileInput : oldFileInput
					    .getSerErrFileInputs()) {
					SerErrFileInput errFileInput = new SerErrFileInput();
					errFileInput.setTiErrRec(oldErrFileInput.getTiErrRec());
					errFileInput.setNiRecErr(oldErrFileInput.getNiRecErr());
					errFileInput.setDsRecErr(oldErrFileInput.getDsRecErr());
					if (errFileInput.getSerUdErrFileInputs() == null) {
					    errFileInput.setSerUdErrFileInputs(
						    new ArrayList<SerUdErrFileInput>());
					}

					logger.debug(SerieEjb.class.getSimpleName()
						+ " --- Copia delle unit\u00E0 documentarie dell'errore");
					for (SerUdErrFileInput oldUdErrFileInput : oldErrFileInput
						.getSerUdErrFileInputs()) {
					    SerUdErrFileInput udErrFileInput = new SerUdErrFileInput();
					    udErrFileInput.setAroUnitaDoc(
						    oldUdErrFileInput.getAroUnitaDoc());
					    udErrFileInput
						    .setCdUdSerie(oldUdErrFileInput.getCdUdSerie());
					    udErrFileInput.setDsKeyOrdUdSerie(
						    oldUdErrFileInput.getDsKeyOrdUdSerie());
					    udErrFileInput
						    .setDtUdSerie(oldUdErrFileInput.getDtUdSerie());
					    udErrFileInput.setInfoUdSerie(
						    oldUdErrFileInput.getInfoUdSerie());
					    udErrFileInput
						    .setPgUdSerie(oldUdErrFileInput.getPgUdSerie());

					    errFileInput.addSerUdErrFileInput(udErrFileInput);
					}

					fileInput.addSerErrFileInput(errFileInput);
				    }
				}
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Copia dei contenuti della vecchia versione");
				for (SerContenutoVerSerie oldContenuto : verSerie
					.getSerContenutoVerSeries()) {
				    SerContenutoVerSerie contenutoSerie = context
					    .getBusinessObject(SerieEjb.class)
					    .copySerContenutoVerSerie(oldContenuto, now);
				    verSerieNew.addSerContenutoVerSery(contenutoSerie);
				}
				helper.insertEntity(verSerieNew, false);
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Salvataggio dello stato della nuova versione");
				SerStatoVerSerie statoVerSerieNew = context
					.getBusinessObject(SerieEjb.class)
					.createSerStatoVerSerie(BigDecimal.ONE,
						CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(),
						"Annullamento versione precedente", null, idUser,
						now, verSerieNew.getIdVerSerie());
				logger.debug(SerieEjb.class.getSimpleName() + azione
					+ " --- Eseguo la persist del nuovo stato versione");
				helper.insertEntity(statoVerSerieNew, false);
				verSerieNew.setIdStatoVerSerieCor(
					new BigDecimal(statoVerSerieNew.getIdStatoVerSerie()));
				verSerieToUpdate = verSerieNew;

				SerVerSerieDaElab verSerieDaElab = new SerVerSerieDaElab();
				verSerieDaElab.setDtRegStatoVerSerie(now);
				verSerieDaElab.setIdStrut(new BigDecimal(
					verSerieNew.getSerSerie().getOrgStrut().getIdStrut()));
				verSerieDaElab.setTiStatoVerSerie(
					CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());
				verSerieDaElab.setSerVerSerie(verSerieNew);
				logger.debug(SerieEjb.class.getSimpleName()
					+ " --- Eseguo la persist del nuovo verSerie da elab");
				helper.insertEntity(verSerieDaElab, false);
				break;
			    }
			    logger.debug(SerieEjb.class.getSimpleName()
				    + " --- Aggiorna le unit\u00E0 doc appartenenti al contenuto di tipo EFFETTIVO della nuova versione, assegnando stato = AIP_GENERATO, purch\u00E8 tali unit\u00E0 doc non appartengano al contenuto di tipo EFFETTIVO di altra serie con stato = VERSAMENTO_IN_ARCHIVIO o IN_ARCHIVIO o IN_CUSTODIA");

			    if (verSerieToUpdate != null) {
				// MEV #31162
				List<String> statiConservOld = new ArrayList<>();
				statiConservOld.add(
					CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO
						.name());
				statiConservOld.add(
					CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name());
				statiConservOld.add(
					CostantiDB.StatoConservazioneUnitaDoc.IN_CUSTODIA.name());
				List<Long> idUnitaDocList = helper
					.getStatoConservazioneAroUnitaDocWithoutOtherSeries(idSerie,
						verSerieToUpdate.getIdVerSerie(), statiConservOld);
				// end MEV #31162

				helper.updateStatoConservazioneAroUnitaDocWithoutOtherSeries(
					idSerie, verSerieToUpdate.getIdVerSerie(),
					CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(),
					Arrays.asList(
						CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO
							.name(),
						CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO
							.name(),
						CostantiDB.StatoConservazioneUnitaDoc.IN_CUSTODIA
							.name()));

				// MEV #31162
				IamUser utente = helper.findById(IamUser.class, idUser);
				for (Long idUnitaDoc : idUnitaDocList) {
				    udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(),
					    Constants.CAMBIA_STATO_SERIE_ANNUL,
					    CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO
						    .name(),
					    Constants.FUNZIONALITA_ONLINE);
				}
				// end MEV #31162
			    }
			}
		    } else {
			throw new IllegalArgumentException(
				"Impossibile caricare il contenuto di tipo EFFETTIVO");
		    }
		} else {
		    throw new ParerUserError("La serie ha cambiato stato");
		}
	    } catch (ParerUserError e) {
		throw e;
	    } catch (LockTimeoutException lte) {
		logger.error("CAMBIO STATO SERIE --- Impossibile acquisire il lock sul contenuto",
			lte);
		throw new ParerUserError(
			"Impossibile acquisire il lock sulle unit\u00E0 documentarie appartenenti al contenuto effettivo");
	    } catch (Exception e) {
		String messaggio = "Eccezione imprevista nella modifica dello stato della serie ";
		messaggio += ExceptionUtils.getRootCauseMessage(e);
		logger.error(messaggio, e);
		throw new ParerUserError(messaggio);
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerContenutoVerSerie copySerContenutoVerSerie(SerContenutoVerSerie oldContenuto,
	    Date now) {
	SerContenutoVerSerie contenutoSerie = new SerContenutoVerSerie();
	contenutoSerie.setTiContenutoVerSerie(oldContenuto.getTiContenutoVerSerie());
	contenutoSerie.setTiStatoContenutoVerSerie(oldContenuto.getTiStatoContenutoVerSerie());
	contenutoSerie.setDtStatoContenutoVerSerie(now);
	contenutoSerie.setNiUdContenutoVerSerie(oldContenuto.getNiUdContenutoVerSerie());
	contenutoSerie.setIdFirstUdAppartVerSerie(oldContenuto.getIdFirstUdAppartVerSerie());
	contenutoSerie.setIdLastUdAppartVerSerie(oldContenuto.getIdLastUdAppartVerSerie());
	contenutoSerie.setFlTipoSerieUpd(oldContenuto.getFlTipoSerieUpd());
	if (contenutoSerie.getAroUdAppartVerSeries() == null) {
	    contenutoSerie.setAroUdAppartVerSeries(new ArrayList<>());
	}
	if (contenutoSerie.getSerErrContenutoVerSeries() == null) {
	    contenutoSerie.setSerErrContenutoVerSeries(new ArrayList<>());
	}
	if (contenutoSerie.getSerQueryContenutoVerSeries() == null) {
	    contenutoSerie.setSerQueryContenutoVerSeries(new ArrayList<>());
	}

	for (AroUdAppartVerSerie oldAroUdAppartVerSerie : oldContenuto.getAroUdAppartVerSeries()) {
	    AroUdAppartVerSerie aroUdAppartVerSerie = new AroUdAppartVerSerie();
	    aroUdAppartVerSerie.setAroUnitaDoc(oldAroUdAppartVerSerie.getAroUnitaDoc());
	    aroUdAppartVerSerie.setCdUdSerie(oldAroUdAppartVerSerie.getCdUdSerie());
	    aroUdAppartVerSerie.setDsKeyOrdUdSerie(oldAroUdAppartVerSerie.getDsKeyOrdUdSerie());
	    aroUdAppartVerSerie.setDtUdSerie(oldAroUdAppartVerSerie.getDtUdSerie());
	    aroUdAppartVerSerie.setInfoUdSerie(oldAroUdAppartVerSerie.getInfoUdSerie());
	    aroUdAppartVerSerie.setPgUdSerie(oldAroUdAppartVerSerie.getPgUdSerie());

	    contenutoSerie.addAroUdAppartVerSery(aroUdAppartVerSerie);
	}
	for (SerQueryContenutoVerSerie oldQuery : oldContenuto.getSerQueryContenutoVerSeries()) {
	    SerQueryContenutoVerSerie query = new SerQueryContenutoVerSerie();
	    query.setBlQuery(oldQuery.getBlQuery());
	    query.setIdRegistroUnitaDoc(oldQuery.getIdRegistroUnitaDoc());
	    query.setIdTipoUnitaDoc(oldQuery.getIdTipoUnitaDoc());

	    contenutoSerie.addSerQueryContenutoVerSery(query);
	}
	for (SerErrContenutoVerSerie oldErrContenuto : oldContenuto.getSerErrContenutoVerSeries()) {
	    SerErrContenutoVerSerie errContenuto = new SerErrContenutoVerSerie();
	    errContenuto.setPgErr(oldErrContenuto.getPgErr());
	    errContenuto.setTiErr(oldErrContenuto.getTiErr());
	    errContenuto.setDlErr(oldErrContenuto.getDlErr());
	    errContenuto.setTiGravitaErr(oldErrContenuto.getTiGravitaErr());
	    errContenuto.setTiOrigineErr(oldErrContenuto.getTiOrigineErr());
	    if (errContenuto.getSerUdNonVersErrs() == null) {
		errContenuto.setSerUdNonVersErrs(new ArrayList<SerUdNonVersErr>());
	    }
	    for (SerUdNonVersErr oldSerUdNonVersErr : oldErrContenuto.getSerUdNonVersErrs()) {
		SerUdNonVersErr udNonVers = new SerUdNonVersErr();
		udNonVers.setVrsUnitaDocNonVer(oldSerUdNonVersErr.getVrsUnitaDocNonVer());
		errContenuto.addSerUdNonVersErr(udNonVers);
	    }

	    contenutoSerie.addSerErrContenutoVerSery(errContenuto);
	}

	return contenutoSerie;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SerVerSerie createSerVerSerie(String cdVerSerie, String dsListaAnniSelSerie,
	    Date dtCreazioneUnitaDocA, Date dtCreazioneUnitaDocDa, Date dtFineSelSerie,
	    Date dtInizioSelSerie, Date dtRegUnitaDocA, Date dtRegUnitaDocDa,
	    BigDecimal niPeriodoSelSerie, BigDecimal pgVerSerie, String tiPeriodoSelSerie,
	    SerSerie serie) {
	SerVerSerie verSerie = new SerVerSerie();
	verSerie.setPgVerSerie(pgVerSerie);
	verSerie.setCdVerSerie(cdVerSerie);
	verSerie.setDtInizioSelSerie(dtInizioSelSerie);
	verSerie.setDtFineSelSerie(dtFineSelSerie);
	verSerie.setDtRegUnitaDocDa(dtRegUnitaDocDa);
	verSerie.setDtRegUnitaDocA(dtRegUnitaDocA);
	verSerie.setDtCreazioneUnitaDocDa(dtCreazioneUnitaDocDa);
	verSerie.setDtCreazioneUnitaDocA(dtCreazioneUnitaDocA);
	verSerie.setNiPeriodoSelSerie(niPeriodoSelSerie);
	verSerie.setTiPeriodoSelSerie(tiPeriodoSelSerie);
	verSerie.setDsListaAnniSelSerie(dsListaAnniSelSerie);
	verSerie.setFlUpdAnnulUnitaDoc("0");
	serie.addSerVerSery(verSerie);
	if (verSerie.getSerContenutoVerSeries() == null) {
	    verSerie.setSerContenutoVerSeries(new ArrayList<SerContenutoVerSerie>());
	}
	if (verSerie.getSerConsistVerSeries() == null) {
	    verSerie.setSerConsistVerSeries(new ArrayList<SerConsistVerSerie>());
	}
	if (verSerie.getSerStatoVerSeries() == null) {
	    verSerie.setSerStatoVerSeries(new ArrayList<SerStatoVerSerie>());
	}
	if (verSerie.getSerFileInputVerSeries() == null) {
	    verSerie.setSerFileInputVerSeries(new ArrayList<SerFileInputVerSerie>());
	}
	if (verSerie.getSerNotaVerSeries() == null) {
	    verSerie.setSerNotaVerSeries(new ArrayList<SerNotaVerSerie>());
	}
	return verSerie;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveNota(long idUtente, BigDecimal idVerSerie, BigDecimal idTipoNotaSerie,
	    BigDecimal pgNota, String dsNota, Date dtNota) throws ParerUserError {
	logger.debug("Eseguo il salvataggio dell'elemento di descrizione");
	BigDecimal id = null;
	try {
	    SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
	    if (verSerie.getSerNotaVerSeries() == null) {
		verSerie.setSerNotaVerSeries(new ArrayList<SerNotaVerSerie>());
	    }
	    SerNotaVerSerie nota = new SerNotaVerSerie();
	    nota.setDecTipoNotaSerie(helper.findById(DecTipoNotaSerie.class, idTipoNotaSerie));
	    nota.setIamUser(helper.findById(IamUser.class, idUtente));
	    nota.setPgNotaVerSerie(pgNota);
	    nota.setDsNotaVerSerie(dsNota);
	    nota.setDtNotaVerSerie(dtNota);

	    verSerie.addSerNotaVerSery(nota);
	    helper.insertEntity(nota, true);
	    id = new BigDecimal(nota.getIdNotaVerSerie());
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio dell'elemento di descrizione ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return id;
    }

    public BigDecimal getMaxPgNota(BigDecimal idVerSerie, BigDecimal idTipoNotaSerie) {
	BigDecimal pg = helper.getMaxPgSerVLisNotaSerie(idVerSerie, idTipoNotaSerie);
	return pg != null ? pg : BigDecimal.ZERO;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNota(BigDecimal idNota, String dsNota, long idUtente, Date dtNota)
	    throws ParerUserError {
	logger.debug("Eseguo il salvataggio dell'elemento di descrizione");
	try {
	    SerNotaVerSerie nota = helper.findById(SerNotaVerSerie.class, idNota);
	    nota.setDsNotaVerSerie(dsNota);
	    nota.setIamUser(helper.findById(IamUser.class, idUtente));
	    nota.setDtNotaVerSerie(dtNota);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio dell'elemento di descrizione ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteNota(BigDecimal idNotaVerSerie) throws ParerUserError {
	logger.debug("Eseguo l'eliminazione dell'elemento di descrizione");
	try {
	    SerNotaVerSerie nota = helper.findById(SerNotaVerSerie.class, idNotaVerSerie);
	    helper.removeEntity(nota, true);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nell'eliminazione dell'elemento di descrizione ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveStato(BigDecimal idStato, String dsAzione, String dsNota)
	    throws ParerUserError {
	logger.debug("Eseguo il salvataggio dell'elemento di descrizione");
	try {
	    SerStatoVerSerie stato = helper.findById(SerStatoVerSerie.class, idStato);
	    stato.setDsAzione(dsAzione);
	    stato.setDsNotaAzione(dsNota);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio dello stato ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
    }

    public SerVLisStatoSerieRowBean getStato(BigDecimal idStato) throws ParerUserError {
	SerVLisStatoSerieRowBean statoRow;
	try {
	    SerVLisStatoSerie stato = helper.findViewById(SerVLisStatoSerie.class, idStato);
	    statoRow = (SerVLisStatoSerieRowBean) Transform.entity2RowBean(stato);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel caricamento dello stato ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return statoRow;
    }

    public SerVVisErrFileSerieUdRowBean getSerVVisErrFileSerieUdRowBean(BigDecimal idErrFileInput) {
	SerVVisErrFileSerieUd erroreFileInput = helper.findViewById(SerVVisErrFileSerieUd.class,
		idErrFileInput);
	SerVVisErrFileSerieUdRowBean row = null;
	if (erroreFileInput != null) {
	    try {
		row = (SerVVisErrFileSerieUdRowBean) Transform.entity2RowBean(erroreFileInput);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dell'errore file in input "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException(
			"Errore durante il recupero dell'errore file in input");
	    }
	}
	return row;
    }

    public SerVLisUdErrFileInputTableBean getSerVLisUdErrFileInputTableBean(
	    BigDecimal idErrFileInput) {
	SerVLisUdErrFileInputTableBean table = new SerVLisUdErrFileInputTableBean();
	List<SerVLisUdErrFileInput> list = helper.getSerVLisUdErrFileInput(idErrFileInput);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisUdErrFileInputTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista di unit\u00e0 documentarie di errore file in input "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVVisErrContenSerieUdRowBean getSerVVisErrContenSerieUdRowBean(
	    BigDecimal idErrContenutoVerSerie) {
	SerVVisErrContenSerieUd erroreContenuto = helper.findViewById(SerVVisErrContenSerieUd.class,
		idErrContenutoVerSerie);
	SerVVisErrContenSerieUdRowBean row = null;
	if (erroreContenuto != null) {
	    try {
		row = (SerVVisErrContenSerieUdRowBean) Transform.entity2RowBean(erroreContenuto);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dell'errore file in input "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException(
			"Errore durante il recupero dell'errore file in input");
	    }
	}
	return row;
    }

    public SerVLisUdNoversTableBean getSerVLisUdNoversTableBean(BigDecimal idErrContenutoVerSerie) {
	SerVLisUdNoversTableBean table = new SerVLisUdNoversTableBean();
	List<SerVLisUdNovers> list = helper.getSerVLisUdNovers(idErrContenutoVerSerie);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisUdNoversTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista di unit\u00e0 documentarie di errore contenuto "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    public SerVLisUdAppartVolSerieTableBean getSerVLisUdAppartVolSerieTableBean(
	    BigDecimal idVolVerSerie, RicercaUdAppartBean parametri) {
	return helper.getSerVLisUdAppartVolSerie(idVolVerSerie, parametri,
		list -> getSerVLisUdAppartVolSerieTableBeanFrom(list));
    }

    private SerVLisUdAppartVolSerieTableBean getSerVLisUdAppartVolSerieTableBeanFrom(
	    List<SerVLisUdAppartVolSerie> list) {
	SerVLisUdAppartVolSerieTableBean table = new SerVLisUdAppartVolSerieTableBean();
	if (list != null && !list.isEmpty()) {
	    try {
		table = (SerVLisUdAppartVolSerieTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error(
			"Errore durante il recupero della lista di unit\u00e0 documentarie di volume serie "
				+ ExceptionUtils.getRootCauseMessage(ex),
			ex);
	    }
	}
	return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecTipoSerieCreataAutom(long idTipoSerie, Date date) throws ParerInternalError {
	logger.info("Eseguo il salvataggio dell'esecuzione della creazione automatica della serie");
	try {
	    DecTipoSerie tipoSerie = helper.findById(DecTipoSerie.class, idTipoSerie);
	    if (tipoSerie.getDecTipoSerieCreataAutoms() == null) {
		tipoSerie.setDecTipoSerieCreataAutoms(new ArrayList<DecTipoSerieCreataAutom>());
	    }
	    DecTipoSerieCreataAutom autom = new DecTipoSerieCreataAutom();
	    autom.setDtCreaAutom(date);

	    tipoSerie.addDecTipoSerieCreataAutom(autom);
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nel salvataggio della creazione automatica della serie ";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerInternalError(messaggio);
	}
    }

    public SerieAutomBean generateIntervalliSerieAutom(BigDecimal niMesiCreazioneSerie,
	    String cdSerieDefault, String dsSerieDefault, int anno, String tiSelUd)
	    throws ParerUserError {
	SerieAutomBean bean = new SerieAutomBean(niMesiCreazioneSerie);
	Calendar cal = Calendar.getInstance();
	Date dtInizioSerie = null;
	Date dtFineSerie = null;
	if (niMesiCreazioneSerie == null
		&& tiSelUd.equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
	    cal.set(Calendar.YEAR, anno);
	    cal.set(Calendar.MONTH, Calendar.JANUARY);
	    cal.set(Calendar.DATE, 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);

	    dtInizioSerie = cal.getTime();

	    cal.set(Calendar.MONTH, Calendar.DECEMBER);
	    cal.set(Calendar.DATE, 31);
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    // cal.set(Calendar.MILLISECOND, 999);
	    dtFineSerie = cal.getTime();
	} else if (niMesiCreazioneSerie != null) {
	    cal.set(Calendar.YEAR, anno);
	    cal.set(Calendar.MONTH, Calendar.JANUARY);
	    cal.set(Calendar.DATE, 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);

	    if (niMesiCreazioneSerie.doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.DECADE
		    .getNumSerie().doubleValue()) {
		bean.setNumeroSerieDaCreare(36);
		bean.setNiGiorniCalcolo(new BigDecimal(10));
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.DECADE.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA
			    .getNumSerie().doubleValue()) {
		bean.setNumeroSerieDaCreare(24);
		bean.setNiGiorniCalcolo(new BigDecimal(15));
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.QUINDICINA.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.MESE.getNumSerie()
			    .doubleValue()) {
		bean.setNumeroSerieDaCreare(12);
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.MESE.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.getNumSerie()
			    .doubleValue()) {
		bean.setNumeroSerieDaCreare(6);
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.BIMESTRE.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE
			    .getNumSerie().doubleValue()) {
		bean.setNumeroSerieDaCreare(4);
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.TRIMESTRE.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE
			    .getNumSerie().doubleValue()) {
		bean.setNumeroSerieDaCreare(3);
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.QUADRIMESTRE.name());
	    } else if (niMesiCreazioneSerie
		    .doubleValue() == CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.getNumSerie()
			    .doubleValue()) {
		bean.setNumeroSerieDaCreare(2);
		bean.setTipoIntervallo(CostantiDB.IntervalliMeseCreazioneSerie.SEMESTRE.name());
	    } else {
		throw new ParerUserError(
			"Errore nella determinazione del numero mesi per cui creare la serie");
	    }
	}
	int numMesiIntervallo = 1;
	if (niMesiCreazioneSerie != null) {
	    if (niMesiCreazioneSerie.compareTo(BigDecimal.ONE) >= 0) {
		// Altre casistiche in cui l'unita di misura e' basata sul mese, e non su giorni
		numMesiIntervallo = 12 / bean.getNumeroSerieDaCreare();
	    }
	}
	for (int i = 0; i < bean.getNumeroSerieDaCreare(); i++) {
	    String appendCdSerie = "";
	    String appendDsSerie = "";
	    if (niMesiCreazioneSerie != null) {
		SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
		if (niMesiCreazioneSerie.compareTo(BigDecimal.ONE) < 0) {
		    // Casistica NUM_SERIE_36 e NUM_SERIE_24, in cui parliamo di decadi e quindicine
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    dtInizioSerie = cal.getTime();
		    int giornoMassimo = cal.getActualMaximum(Calendar.DATE);
		    int giornoAttuale = cal.get(Calendar.DATE)
			    + bean.getNiGiorniCalcolo().intValue();

		    if (giornoAttuale > giornoMassimo) {
			// Se arrivare a fine mese ci sono meno giorni di quelli per il calcolo,
			// arrivo a fine mese
			cal.roll(Calendar.DATE, (giornoMassimo - cal.get(Calendar.DATE)));
		    } else if ((giornoMassimo - giornoAttuale) < Math
			    .round((double) bean.getNiGiorniCalcolo().intValue() / 2)) {
			// Caso di scarto a fine mese (Es. sono al 20, devo arrivare al 31). Se lo
			// scarto per il calcolo
			// successivo e' minore di 5, allora arrivo a fine mese
			int calc = bean.getNiGiorniCalcolo().intValue()
				+ (giornoMassimo - giornoAttuale);
			cal.roll(Calendar.DATE, calc);
		    } else {
			cal.roll(Calendar.DATE, bean.getNiGiorniCalcolo().intValue() - 1);
		    }

		    cal.set(Calendar.HOUR_OF_DAY, 23);
		    cal.set(Calendar.MINUTE, 59);
		    cal.set(Calendar.SECOND, 59);
		    // cal.set(Calendar.MILLISECOND, 999);

		    dtFineSerie = cal.getTime();

		    cal.add(Calendar.DATE, 1);
		} else {
		    // Altre casistiche in cui l'unita di misura e' comunque basata sul mese
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    dtInizioSerie = cal.getTime();

		    cal.roll(Calendar.MONTH, numMesiIntervallo - 1);
		    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

		    cal.set(Calendar.HOUR_OF_DAY, 23);
		    cal.set(Calendar.MINUTE, 59);
		    cal.set(Calendar.SECOND, 59);
		    // cal.set(Calendar.MILLISECOND, 999);

		    dtFineSerie = cal.getTime();

		    cal.add(Calendar.DATE, 1);
		}
		appendCdSerie = "-" + anno + "-" + (i + 1);
		appendDsSerie = " da " + df.format(dtInizioSerie) + " a " + df.format(dtFineSerie)
			+ "(" + (i + 1) + " " + bean.getTipoIntervallo() + ")";
	    } else {
		appendCdSerie = "-" + anno;
	    }
	    String cdSerie = cdSerieDefault + appendCdSerie;
	    String dsSerie = dsSerieDefault + appendDsSerie;
	    IntervalliSerieAutomBean intervalBean = new IntervalliSerieAutomBean(dtInizioSerie,
		    dtFineSerie, cdSerie, dsSerie);
	    bean.getIntervalli().add(intervalBean);
	}

	return bean;
    }

    // <editor-fold defaultstate="collapsed" desc="Gestione VALIDAZIONE SERIE - ASINCRONA">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> callValidazioneSerieAsync(BigDecimal idVerSerie) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<String> future = null;
	try {
	    future = context.getBusinessObject(SerieEjb.class).validazioneSerieAsync(idVerSerie);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return future;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Map<String, ?>> callValidazioneSerieAsync(Map<String, BigDecimal> idVerSeries) {
	logger.info(SerieEjb.class.getSimpleName() + " --- Inizio chiamata asincrona...");
	Future<Map<String, ?>> futures = null;
	try {
	    futures = context.getBusinessObject(SerieEjb.class).validazioneSerieAsync(idVerSeries);
	} catch (Exception e) {
	    // INUTILI in quanto intercettati
	}
	logger.info(SerieEjb.class.getSimpleName() + " --- Fine chiamata asincrona...");
	return futures;
    }

    @Asynchronous
    public Future<String> validazioneSerieAsync(BigDecimal idVerSerie) {
	boolean error = false;
	boolean lock = false;
	try {
	    lock = context.getBusinessObject(SerieEjb.class).executeValidazioneSerie(idVerSerie);
	} catch (Exception e) {
	    error = true;
	    String messaggio = "Eccezione imprevista durante la fase di validazione serie:";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    if (idVerSerie != null) {
		// Per non finire in uno stato inconsistente, la versione viene riportata a un nuovo
		// stato DA_VALIDARE
		// mettendo FINE_SCHEDULAZIONE anziché ERRORE
		String azione = StringUtils.substringAfter(ExceptionUtils.getRootCauseMessage(e),
			":");
		context.getBusinessObject(SerieEjb.class).createStatoErroreValidazione(azione,
			idVerSerie.longValue());

		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), messaggio,
			idVerSerie.longValue(), SerVerSerie.class.getSimpleName());
	    }
	}
	String result;
	if (lock && !error) {
	    result = "OK";
	} else if (!lock) {
	    result = "NO_LOCK";
	} else {
	    result = "ERROR";
	}
	return new AsyncResult<>(result);
    }

    @Asynchronous
    public Future<Map<String, ?>> validazioneSerieAsync(Map<String, BigDecimal> idVerSeries) {
	Map<String, String> futures = new HashMap<>();
	for (Entry<String, BigDecimal> idVerSerieEntry : idVerSeries.entrySet()) {
	    boolean error = false;
	    boolean lock = false;
	    try {
		lock = context.getBusinessObject(SerieEjb.class)
			.executeValidazioneSerie(idVerSerieEntry.getValue());
	    } catch (Exception e) {
		error = true;
		String messaggio = "Eccezione imprevista durante la fase di validazione serie:";
		messaggio += ExceptionUtils.getRootCauseMessage(e);
		logger.error(messaggio, e);
		if (idVerSerieEntry.getValue() != null) {
		    // Per non finire in uno stato inconsistente, la versione viene riportata a un
		    // nuovo stato
		    // DA_VALIDARE mettendo FINE_SCHEDULAZIONE anziché ERRORE
		    String azione = StringUtils
			    .substringAfter(ExceptionUtils.getRootCauseMessage(e), ":");
		    context.getBusinessObject(SerieEjb.class).createStatoErroreValidazione(azione,
			    idVerSerieEntry.getValue().longValue());

		    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(),
			    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), messaggio,
			    idVerSerieEntry.getValue().longValue(),
			    SerVerSerie.class.getSimpleName());
		}
	    }
	    String result;
	    if (lock && !error) {
		result = "OK";
	    } else if (!lock) {
		result = "NO_LOCK";
	    } else {
		result = "ERROR";
	    }
	    futures.put(idVerSerieEntry.getKey(), result);
	}
	return new AsyncResult<Map<String, ?>>(futures);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="VALIDAZIONE SERIE">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean executeValidazioneSerie(BigDecimal idVerSerie) throws Exception {
	boolean lock = false;
	logger.info(SerieEjb.class.getSimpleName() + " --- Richiesta LOCK su versione id "
		+ idVerSerie.toPlainString());
	SerVerSerie verSerie = helper.findByIdWithLock(SerVerSerie.class, idVerSerie);
	if (verSerie != null) {
	    lock = true;
	    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(),
		    JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
		    verSerie.getIdVerSerie(), verSerie.getClass().getSimpleName());
	    // se per il contenuto di tipo EFFETTIVO non sono presenti errori rilevati con origine =
	    // VALIDAZIONE ->
	    // Controlla validazione
	    SerContenutoVerSerie contenutoEff = helper.getSerContenutoVerSerie(
		    idVerSerie.longValue(), CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	    boolean erroreControlli = false;
	    if (helper.countSerErrContenutoVerSerie(
		    new BigDecimal(contenutoEff.getIdContenutoVerSerie()), null, null,
		    CostantiDB.TipoOrigineErroreContenuto.VALIDAZIONE.name()) == 0L) {
		erroreControlli = context.getBusinessObject(SerieEjb.class)
			.controllaValidazione(verSerie);
	    }
	    if (!erroreControlli) {
		// Esegue la validazione
		context.getBusinessObject(SerieEjb.class).validazioneSerie(verSerie);
	    }
	    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(),
		    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null,
		    verSerie.getIdVerSerie(), verSerie.getClass().getSimpleName());
	}
	logger.info(SerieEjb.class.getSimpleName()
		+ " --- FINE chiamata asincrona per validazione serie");
	return lock;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean controllaValidazione(SerVerSerie verSerie) {
	SerContenutoVerSerie contenutoEff = helper.getLockSerContenutoVerSerie(
		verSerie.getIdVerSerie(), CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	SerContenutoVerSerie contenutoCalc = helper.getSerContenutoVerSerie(
		verSerie.getIdVerSerie(), CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
	int pg = helper.countSerErrContenutoVerSerie(
		new BigDecimal(contenutoEff.getIdContenutoVerSerie()), null, null, null).intValue()
		+ 1;
	int pgIniziale = pg;
	final String origineErrore = CostantiDB.TipoOrigineErroreContenuto.VALIDAZIONE.name();
	SerStatoVerSerie lastSerStatoVerSerieWithStatus = helper.getLastSerStatoVerSerieWithStatus(
		verSerie.getIdVerSerie(), CostantiDB.StatoVersioneSerie.CONTROLLATA.name());
	final Date dataRiferimento = lastSerStatoVerSerieWithStatus.getDtRegStatoVerSerie();

	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_VALIDAZIONE --- START - Verifica presenza di UD nel contenuto effettivo");
	pg = context.getBusinessObject(SerieEjb.class)
		.controllaContenutoEffettivoNonVuoto(contenutoEff, pg, origineErrore);
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_VALIDAZIONE --- END - Verifica presenza di UD nel contenuto effettivo");
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_VALIDAZIONE --- START - Verifica presenza di UD annullate");
	pg = context.getBusinessObject(SerieEjb.class).controllaUdAnnullate(
		verSerie.getSerSerie().getDecTipoSerie().getIdTipoSerie(), contenutoEff,
		dataRiferimento, pg, origineErrore);
	logger.info(SerieEjb.class.getSimpleName()
		+ "--- CONTROLLA_VALIDAZIONE --- END - Verifica presenza di UD annullate");
	if (contenutoCalc != null) {
	    List<SerQueryContenutoVerSerie> queries = helper.getSerQueryContenutoVerSerie(
		    new BigDecimal(contenutoCalc.getIdContenutoVerSerie()));
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_VALIDAZIONE --- START - Verifica presenza di UD non selezionate nel contenuto CALCOLATO");
	    pg = context.getBusinessObject(SerieEjb.class)
		    .controllaUdNonSelezionateInContenutoCalcolato(queries, contenutoEff, pg,
			    dataRiferimento, origineErrore);
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_VALIDAZIONE --- END - Verifica presenza di UD non selezionate nel contenuto CALCOLATO");
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_VALIDAZIONE --- START - Controllo unit\u00E0 documentarie versate dopo calcolo");
	    pg = context.getBusinessObject(SerieEjb.class).controllaUdVersateDopoCalcolo(queries,
		    contenutoEff.getIdContenutoVerSerie(), contenutoCalc, pg, dataRiferimento,
		    origineErrore);
	    logger.info(SerieEjb.class.getSimpleName()
		    + "--- CONTROLLA_VALIDAZIONE --- END - Controllo unit\u00E0 documentarie versate dopo calcolo");
	}
	boolean erroreValidazione = (pg - pgIniziale) > 0;
	if (erroreValidazione) {

	    // esiste almeno un errore con origine = VALIDAZIONE
	    SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		    verSerie.getIdStatoVerSerieCor());
	    SerStatoVerSerie statoValidazioneInCorso = helper.getLastSerStatoVerSerieWithStatus(
		    verSerie.getIdVerSerie(),
		    CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name());
	    Date now = Calendar.getInstance().getTime();
	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- CONTROLLA_VALIDAZIONE --- Aggiorna lo stato della versione");
	    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		    .createSerStatoVerSerie(statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
			    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(),
			    "La serie non pu\u00F2 essere validata perch\u00E9 "
				    + "\u00E8 stato rilevato almeno un errore nel corso della validazione",
			    null, statoValidazioneInCorso.getIamUser().getIdUserIam(), now,
			    verSerie.getIdVerSerie());

	    logger.info(SerieEjb.class.getSimpleName()
		    + " --- CONTROLLA_VALIDAZIONE --- Eseguo la persist del nuovo stato versione");
	    helper.insertEntity(statoVerSerie, false);
	    verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	    SerVerSerieDaElab verSerieDaElab = helper
		    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
	    if (verSerieDaElab != null) {
		verSerieDaElab
			.setTiStatoVerSerie(CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());
	    } else {
		verSerieDaElab = new SerVerSerieDaElab();
		verSerieDaElab.setDtRegStatoVerSerie(now);
		verSerieDaElab.setIdStrut(
			new BigDecimal(verSerie.getSerSerie().getOrgStrut().getIdStrut()));
		verSerieDaElab
			.setTiStatoVerSerie(CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());
		verSerieDaElab.setSerVerSerie(verSerie);
		logger.debug(SerieEjb.class.getSimpleName()
			+ " --- Eseguo la persist del nuovo verSerie da elab");
		helper.insertEntity(verSerieDaElab, false);
	    }
	}
	return erroreValidazione;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createStatoErroreValidazione(String azione, Long idVerSerie) {
	SerVerSerie verSerie = helper.findByIdWithLock(SerVerSerie.class, idVerSerie);
	SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());
	SerStatoVerSerie statoValidazioneInCorso = statoCorrente;
	if (!statoCorrente.getTiStatoVerSerie()
		.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())) {
	    statoValidazioneInCorso = helper.getLastSerStatoVerSerieWithStatus(
		    verSerie.getIdVerSerie(),
		    CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name());
	}
	logger.debug(SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della versione");
	final Date now = Calendar.getInstance().getTime();
	SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		.createSerStatoVerSerie(statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
			CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(), azione, null,
			statoValidazioneInCorso.getIamUser().getIdUserIam(), now,
			verSerie.getIdVerSerie());
	logger.debug(
		SerieEjb.class.getSimpleName() + " --- Eseguo la persist del nuovo stato versione");
	helper.insertEntity(statoVerSerie, false);

	verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	SerVerSerieDaElab verSerieDaElab = new SerVerSerieDaElab();
	verSerieDaElab.setDtRegStatoVerSerie(now);
	verSerieDaElab
		.setIdStrut(new BigDecimal(verSerie.getSerSerie().getOrgStrut().getIdStrut()));
	verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerieDaElab.DA_VALIDARE.name());
	verSerieDaElab.setSerVerSerie(verSerie);
	logger.debug(SerieEjb.class.getSimpleName()
		+ " --- Eseguo la persist del nuovo verSerie da elab");
	helper.insertEntity(verSerieDaElab, false);
    }

    public void validazioneSerie(SerVerSerie verSerie)
	    throws ParerInternalError, NamingException, ValidationException,
	    NoSuchAlgorithmException, IOException, MarshalException, JAXBException {
	logger.debug(SerieEjb.class.getSimpleName()
		+ " VALIDAZIONE ASYNC SERIE --- determina le unit\u00E0 doc appartenenti al contenuto di tipo EFFETTIVO della versione serie in input, con stato = AIP_DA_GENERARE");
	List<AroUdAppartVerSerie> aroUnitaDocInContenEff = helper.getAroUdAppartVerSerieInContenEff(
		verSerie.getIdVerSerie(),
		CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE.name());
	logger.debug(SerieEjb.class.getSimpleName() + " VALIDAZIONE ASYNC SERIE --- ottenute "
		+ aroUnitaDocInContenEff.size() + " unit\u00E0 doc");
	for (AroUdAppartVerSerie udVerSerie : aroUnitaDocInContenEff) {
	    long idSerie = verSerie.getSerSerie().getIdSerie();
	    long idUnitaDoc = udVerSerie.getAroUnitaDoc().getIdUnitaDoc();
	    context.getBusinessObject(SerieEjb.class).creaPrimoIndiceAipUniSincro(idSerie,
		    idUnitaDoc);
	}
	context.getBusinessObject(SerieEjb.class).updateSerieValidata(verSerie);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaPrimoIndiceAipUniSincro(Long idSerie, Long idUnitaDoc)
	    throws ParerInternalError, NamingException, ValidationException,
	    NoSuchAlgorithmException, IOException, MarshalException, JAXBException {
	logger.debug(SerieEjb.class.getSimpleName()
		+ " VALIDAZIONE ASYNC SERIE --- crea la prima versione dell'indice AIP dell'unit\u00E0 doc "
		+ idUnitaDoc + " in formato UNISINCRO");
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	AroUnitaDoc ud = helper.findByIdWithLock(AroUnitaDoc.class, idUnitaDoc);
	logger.debug(
		"Richiamo metodo di creazione prima versione dell'indice AIP dell'unita doc in formato UNISINCRO");
	// MEV#30395
	BigDecimal idAmbiente = BigDecimal
		.valueOf(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
	String sincroVersion = configurationHelper
		.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.UNISINCRO_VERSION, idAmbiente);
	if (!"2.0".equals(sincroVersion)) {
	    elabIndiceAip.gestisciIndiceAipOs(ud.getIdUnitaDoc(), Constants.JOB_VALIDA_SERIE);
	} else {
	    elabIndiceAip.gestisciIndiceAipV2Os(ud.getIdUnitaDoc(), Constants.JOB_VALIDA_SERIE);
	}
	// end MEV#30395
	// AGGIORNAMENTO STATO INCONSISTENTE
	// logger.debug("Indice AIP creato, imposto lo stato dell'ud a VERSAMENTO_IN_ARCHIVIO");
	// ud.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSerieValidata(SerVerSerie verSerie) {
	SerSerie serie = verSerie.getSerSerie();
	SerContenutoVerSerie contenEff = helper.getSerContenutoVerSerie(verSerie.getIdVerSerie(),
		CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

	// QUERY NATIVA PER L'UPDATE delle UD per le quali non sia valorizzato l'id dell'ultima
	// versione indice AIP
	helper.updateAroUdAppartVerSerieNoAip(contenEff.getIdContenutoVerSerie());
	// UPDATE: modifica tutte le unità doc appartenenti al contenuto di tipo EFFETTIVO della
	// versione serie,
	// con stato conservazione = AIP_GENERATO o AIP_FIRMATO, assegnando VERSAMENTO_IN_ARCHIVIO
	List<String> statiConservazioneDaCambiare = new ArrayList<>(
		Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(),
			CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name()));
	// MEV #31162
	List<Long> idUnitaDocList = helper.getStatoConservazioneAroUnitaDocInContenuto(
		contenEff.getIdContenutoVerSerie(), statiConservazioneDaCambiare);
	// end MEV #31162

	helper.updateStatoConservazioneAroUnitaDocInContenuto(contenEff.getIdContenutoVerSerie(),
		statiConservazioneDaCambiare,
		CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());

	// MEV #31162
	for (Long idUnitaDoc : idUnitaDocList) {
	    udEjb.insertLogStatoConservUd(idUnitaDoc, Constants.JOB_VALIDA_SERIE,
		    Constants.BLOCCO_AGGIORNAMENTI_AIP,
		    CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
		    Constants.NM_AGENTE_JOB_SACER);
	}
	// end MEV #31162

	Date now = Calendar.getInstance().getTime();
	SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());
	SerStatoVerSerie statoValidazioneInCorso = statoCorrente;
	if (!statoCorrente.getTiStatoVerSerie()
		.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())) {
	    statoValidazioneInCorso = helper.getLastSerStatoVerSerieWithStatus(
		    verSerie.getIdVerSerie(),
		    CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name());
	}
	logger.debug(SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della versione");
	SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		.createSerStatoVerSerie(statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
			CostantiDB.StatoVersioneSerie.VALIDATA.name(), "Validazione serie",
			statoValidazioneInCorso.getDsNotaAzione(),
			statoValidazioneInCorso.getIamUser().getIdUserIam(), now,
			verSerie.getIdVerSerie());

	logger.debug(
		SerieEjb.class.getSimpleName() + " --- Eseguo la persist del nuovo stato versione");
	helper.insertEntity(statoVerSerie, false);
	verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	SerStatoSerie statoSerie;
	logger.debug(SerieEjb.class.getSimpleName() + " --- Aggiorna lo stato della serie");

	SerStatoSerie lastStatoSerie = helper.getLastSerStatoSerie(serie.getIdSerie());
	BigDecimal pg = lastStatoSerie != null
		? lastStatoSerie.getPgStatoSerie().add(BigDecimal.ONE)
		: BigDecimal.ONE;
	if (serie.getIdStatoSerieCor() == null) {
	    statoSerie = context.getBusinessObject(SerieEjb.class).createSerStatoSerie(pg,
		    CostantiDB.StatoConservazioneSerie.PRESA_IN_CARICO.name(), "Validazione serie",
		    statoValidazioneInCorso.getDsNotaAzione(),
		    statoValidazioneInCorso.getIamUser().getIdUserIam(), now, serie.getIdSerie());
	} else {
	    statoSerie = context.getBusinessObject(SerieEjb.class).createSerStatoSerie(pg,
		    CostantiDB.StatoConservazioneSerie.AIP_IN_AGGIORNAMENTO.name(),
		    "Validazione serie", statoValidazioneInCorso.getDsNotaAzione(),
		    statoValidazioneInCorso.getIamUser().getIdUserIam(), now, serie.getIdSerie());
	}
	logger.debug(
		SerieEjb.class.getSimpleName() + " --- Eseguo la persist del nuovo stato serie");
	helper.insertEntity(statoSerie, false);
	serie.setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));

	logger.debug(SerieEjb.class.getSimpleName()
		+ " --- Aggiorno il record in verSerieDaElab relativo alla versione");
	SerVerSerieDaElab verSerieDaElab = helper
		.getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());

	if (verSerieDaElab != null) {
	    verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.VALIDATA.name());
	} else {
	    if (verSerie.getSerVerSerieDaElabs() == null) {
		verSerie.setSerVerSerieDaElabs(new ArrayList<SerVerSerieDaElab>());
	    }
	    verSerieDaElab = new SerVerSerieDaElab();
	    verSerieDaElab.setDtRegStatoVerSerie(now);
	    verSerieDaElab
		    .setIdStrut(new BigDecimal(verSerie.getSerSerie().getOrgStrut().getIdStrut()));
	    verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.VALIDATA.name());
	    verSerieDaElab.setSerVerSerie(verSerie);
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Eseguo la persist del nuovo verSerie da elab");
	    helper.insertEntity(verSerieDaElab, false);
	}
    }
    // </editor-fold>

    public BaseTable getSerieDaFirmareBeanList(BigDecimal idAmbiente, BigDecimal idEnte,
	    BigDecimal idStrut, CostantiDB.StatoVersioneSerie statoVersioneSerie) {
	List<Object[]> objList = helper.getSerVerSerieDaElabList(idAmbiente, idEnte, idStrut,
		statoVersioneSerie);
	BaseTable serieTable = new BaseTable();

	if (objList != null && !objList.isEmpty()) {
	    for (Object[] obj : objList) {
		BaseRow serieRow = new BaseRow();
		serieRow.setString("amb_ente_strut",
			(String) obj[0] + " - " + (String) obj[1] + " - " + (String) obj[2]);
		serieRow.setString("cd_composito_serie", (String) obj[3]);
		serieRow.setBigDecimal("aa_serie", (BigDecimal) obj[4]);
		serieRow.setString("ds_serie", (String) obj[5]);
		serieRow.setString("nm_tipo_serie", (String) obj[6]);
		serieRow.setString("cd_ver_serie", (String) obj[7]);
		/* todo: formattare le date? */
		if ((Date) obj[8] != null || (Date) obj[9] != null) {
		    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
		    String data1 = sdf.format((Date) obj[8]);
		    String data2 = sdf.format((Date) obj[9]);

		    serieRow.setString("range_date", data1 + " - " + data2);
		}
		serieRow.setBigDecimal("id_ver_serie", new BigDecimal((Long) obj[10]));
		serieRow.setBigDecimal("id_strut", new BigDecimal((Long) obj[11]));
		serieTable.add(serieRow);
	    }
	}
	return serieTable;
    }

    /**
     * Data una struttura in input, mi restituisce l'informazione se esistano o meno delle versione
     * serie di tipo FIRMATA_NO_MARCA
     *
     * @param idStrut id struttura
     *
     * @return true/false
     */
    public boolean existsFirmataNoMarca(BigDecimal idStrut) {
	return helper.existsFirmataNoMarca(idStrut);
    }

    /**
     * Recupera dalla tabella SER_FILE_VER_SERIE il file
     *
     * @param idVerSerie     id versamento serie
     * @param tiFileVerSerie entity TipoFileVerSerie
     *
     * @return byte[] blob file
     */
    public byte[] getSerFileVerSerieBlob(long idVerSerie, TipoFileVerSerie tiFileVerSerie) {
	byte[] result = null;
	ByteArrayOutputStream os = new ByteArrayOutputStream();

	// recupero documento blob vs obj storage
	// build dto per recupero
	RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
		Constants.TiEntitaSacerObjectStorage.INDICE_AIP_SERIE, idVerSerie, os,
		RecBlbOracle.TabellaBlob.SER_FILE_VER_SERIE, tiFileVerSerie.name());
	// recupero
	boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);

	if (esitoRecupero) {
	    result = os.toByteArray();
	}
	try {
	    os.flush();
	    os.close();
	} catch (IOException ex) {
	    String messaggio = "Eccezione imprevista nell'ottenimento del file da scaricare";
	    messaggio += ExceptionUtils.getRootCauseMessage(ex);
	    logger.error(messaggio, ex);
	}
	return result;
    }

    /**
     * Recupera dalla tabella SER_FILE_VER_SERIE il hash
     *
     * @param idVerSerie     id versamento serie
     * @param tiFileVerSerie entity TipoFileVerSerie
     *
     * @return byte[] hash file
     */
    public byte[] getSerFileVerSerieHash(long idVerSerie, TipoFileVerSerie tiFileVerSerie) {
	byte[] result = null;
	SerFileVerSerie fileVerSerie = helper.getSerFileVerSerie(idVerSerie, tiFileVerSerie.name());
	if (fileVerSerie != null) {
	    String hash = fileVerSerie.getDsHashFile();
	    if (StringUtils.isNotBlank(hash)) {
		result = Base64.decodeBase64(hash);
	    }
	}
	return result;
    }

    /**
     * Recupera l'hash dalla tabella SER_FILE_VER_SERIE calcolato con l'algoritmo specificato
     *
     * @param idVerSerie     id versamento serie
     * @param tiFileVerSerie entity TipoFileVerSerie
     * @param hashAlg        algorimento Hash
     *
     * @return byte[] hash in base64
     */
    public byte[] getSerFileVerSerieHash(long idVerSerie, TipoFileVerSerie tiFileVerSerie,
	    Digest.DigestAlgorithm hashAlg) {
	byte[] result = null;
	SerFileVerSerie fileVerSerie = helper.getSerFileVerSerie(idVerSerie, tiFileVerSerie.name());
	if (fileVerSerie != null) {
	    // Checks if the hash was calculated with the algorithm requested
	    if (fileVerSerie.getDsAlgoHashFile().equals(hashAlg.getValue())) {
		String hash = fileVerSerie.getDsHashFile();
		if (StringUtils.isNotBlank(hash)) {
		    result = Base64.decodeBase64(hash);
		}
	    }
	}
	return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeFirma(Long idVerSerie, byte[] fileFirmato, long idUtente,
	    ElencoEnums.TipoFirma tipoFirma) throws ParerUserError {
	try {
	    this.storeFirmaNoTransaction(idVerSerie, fileFirmato, idUtente, tipoFirma);
	} catch (Exception e) {
	    /*
	     * Perché richiamato da interfaccia -> si gestisce l'errore utente su front-end
	     */
	    throw new ParerUserError("Errore durante scrittura firma " + e.getMessage());
	}
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void storeFirmaMandatoryTransaction(Long idVerSerie, byte[] fileFirmato, long idUtente,
	    ElencoEnums.TipoFirma tipoFirma) throws Exception {
	this.storeFirmaNoTransaction(idVerSerie, fileFirmato, idUtente, tipoFirma);
    }

    private void storeFirmaNoTransaction(Long idVerSerie, byte[] fileFirmato, long idUtente,
	    ElencoEnums.TipoFirma tipoFirma) throws Exception {
	SerVerSerieDaElab verSerieDaElab = helper.getSerVerSerieDaElabByIdVerSerie(idVerSerie);
	/* LOCCO SER_SERIE E SER_VER_SERIE */
	SerVerSerie verSerie = helper.findByIdWithLock(SerVerSerie.class, idVerSerie);
	SerSerie serie = helper.findByIdWithLock(SerSerie.class,
		verSerie.getSerSerie().getIdSerie());
	/* LOCCO ARO_UD_APPART_VER_SERIE */
	List<AroUdAppartVerSerie> udAppartVerSerieList = helper.getLockAroUdAppartVerSerie(
		idVerSerie, CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
	/* Urn firma */
	String versioneSerie = verSerieDaElab.getSerVerSerie().getCdVerSerie();
	final String sistema = configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
	CSVersatore csVersatore = new CSVersatore();
	csVersatore.setAmbiente(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut()
		.getOrgEnte().getOrgAmbiente().getNmAmbiente());
	csVersatore.setEnte(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte()
		.getNmEnte());
	csVersatore.setStruttura(
		verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getNmStrut());
	// sistema (new URN)
	csVersatore.setSistemaConservazione(sistema);
	String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();
	// Ricavo lo stesso cdVerXsdFile del file unisincro
	String cdVerXsdFile = verSerieDaElab.getSerVerSerie().getSerFileVerSeries().get(0)
		.getCdVerXsdFile();

	// MEV#30400

	BackendStorage backendIndiciAip = objectStorageService.lookupBackendIndiciAipSerieUD(
		verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getIdStrut());

	boolean putOnOs = true;
	if (objectStorageService.isSerFileVerSerieUDOnOs(
		verSerieDaElab.getSerVerSerie().getIdVerSerie(),
		CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name())) {
	    String md5LocalContent = calculateMd5AsBase64(
		    new String(fileFirmato, StandardCharsets.UTF_8));
	    String eTagFromObjectMetadata = objectStorageService.getObjectMetadataIndiceAipSerieUD(
		    verSerieDaElab.getSerVerSerie().getIdVerSerie(),
		    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name()).eTag();

	    if (md5LocalContent.equals(eTagFromObjectMetadata)) {
		putOnOs = false;
	    }
	}

	/* Registra nella tabella SER_FILE_VER_SERIE */
	SerFileVerSerie serFileVerSerie = helper.storeFileIntoSerFileVerSerie(idVerSerie,
		CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name(), fileFirmato,
		cdVerXsdFile, verSerieDaElab.getIdStrut(), new Date(), putOnOs, tipoFirma);

	// EVO#16492
	/* Calcolo e persisto urn dell'indice AIP firmato della serie */
	helper.storeSerUrnFileVerSerieFir(serFileVerSerie, csVersatore, codiceSerie, versioneSerie);
	// end EVO#16492

	ObjectStorageResource indiceAipSuOS;

	if (putOnOs) {
	    indiceAipSuOS = objectStorageService.createResourcesInIndiciAipSerieUD(serFileVerSerie,
		    backendIndiciAip.getBackendName(), fileFirmato,
		    verSerieDaElab.getSerVerSerie().getIdVerSerie(), verSerieDaElab.getIdStrut(),
		    csVersatore, codiceSerie, versioneSerie);
	    logger.debug(LOG_SALVATAGGIO_OS, indiceAipSuOS.getBucket(), indiceAipSuOS.getKey());
	}

	// end MEV#30400

	/* Se il tipo di serie prevede tipo conservazione = IN_ARCHIVIO */
	if (verSerie.getSerSerie().getDecTipoSerie().getTiConservazioneSerie()
		.equals(CostantiDB.TipoConservazioneSerie.IN_ARCHIVIO.name())) {
	    /* Registra il nuovo stato di versione della serie */
	    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		    .createSerStatoVerSerie(
			    ciasudHelper.getUltimoProgressivoSerStatoVerSerie(idVerSerie).add(
				    BigDecimal.ONE),
			    CostantiDB.StatoVersioneSerie.FIRMATA.name(), "Firma indice AIP serie",
			    null, idUtente, new Date(), idVerSerie);

	    /* Aggiorna l'identificatore dello stato corrente della versione della serie */
	    helper.insertEntity(statoVerSerie, false);
	    verSerieDaElab.getSerVerSerie()
		    .setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	    /* Registra un nuovo stato della serie */
	    long idSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getIdSerie();
	    SerStatoSerie statoSerie = context.getBusinessObject(SerieEjb.class)
		    .createSerStatoSerie(
			    ciasudHelper.getUltimoProgressivoSerStatoSerie(idSerie)
				    .add(BigDecimal.ONE),
			    CostantiDB.StatoConservazioneSerie.IN_ARCHIVIO.name(),
			    "Firma indice AIP serie", null, idUtente, new Date(), idSerie);

	    /* Aggiorna l'identificatore dello stato corrente della serie */
	    helper.insertEntity(statoSerie, false);
	    verSerieDaElab.getSerVerSerie().getSerSerie()
		    .setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));

	    /* Aggiorna la versione serie da elaborare in FIRMATA */
	    verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.FIRMATA.name());

	    /*
	     * Aggiorna tutte le unit doc appartenenti al contenuto di tipo EFFETTIVO della versione
	     * serie con stato = AIP_GENERATO, assegnando stato conservazione = IN_ARCHIVIO
	     */
	    SerContenutoVerSerie contenutoVerSerie = helper.getSerContenutoVerSerie(idVerSerie,
		    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

	    // MEV #31162
	    List<Long> idUnitaDocList = helper.getStatoConservazioneAroUnitaDocInContenuto(
		    contenutoVerSerie.getIdContenutoVerSerie(),
		    CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
	    // end MEV #31162

	    helper.updateStatoConservazioneAroUnitaDocInContenuto(
		    contenutoVerSerie.getIdContenutoVerSerie(),
		    CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
		    CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name());

	    // MEV #31162
	    IamUser utente = helper.findById(IamUser.class, idUtente);
	    for (Long idUnitaDoc : idUnitaDocList) {
		udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(),
			Constants.FIRMA_SERIE,
			CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name(),
			Constants.FUNZIONALITA_ONLINE);
	    }
	    // end MEV #31162
	}

	/*
	 * Se il tipo di serie prevede tipo conservazione = FISCALE, aggiorna la versione serie da
	 * elaborare assegnando stato = FIRMATA_NO_MARCA
	 */
	if (verSerie.getSerSerie().getDecTipoSerie().getTiConservazioneSerie()
		.equals(CostantiDB.TipoConservazioneSerie.FISCALE.name())) {
	    /* Registra il nuovo stato di versione della serie */
	    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
		    .createSerStatoVerSerie(
			    ciasudHelper.getUltimoProgressivoSerStatoVerSerie(idVerSerie)
				    .add(BigDecimal.ONE),
			    CostantiDB.StatoVersioneSerie.FIRMATA_NO_MARCA.name(),
			    "Firma indice AIP serie senza apposizione marca", null, idUtente,
			    new Date(), idVerSerie);
	    /* Aggiorna l'identificatore dello stato corrente della versione della serie */
	    helper.insertEntity(statoVerSerie, false);
	    verSerieDaElab.getSerVerSerie()
		    .setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	    /* Aggiorna la versione serie da elaborare in FIRMATA_no_marca */
	    verSerieDaElab.setTiStatoVerSerie(
		    CostantiDB.StatoVersioneSerieDaElab.FIRMATA_NO_MARCA.name());
	}
    }

    // public int marcaturaIndici(long idUtente) throws ParerUserError {
    // /* Recupero la lista di tutte le versione serie con stato FIRMATO_NO_MARCA appartenente a
    // qualunque struttura */
    // byte[] marcaTemporale;
    // List<SerVerSerieDaElab> verSerieDaMarcareList = helper.getSerVerSerieDaElab(null,
    // CostantiDB.StatoVersioneSerieDaElab.FIRMATA_NO_MARCA.name());
    // /* Per ogni versione serie su cui acquisire la marca temporale per la firma */
    // int signed = 0;
    // for (SerVerSerieDaElab verSerieDaMarcare : verSerieDaMarcareList) {
    // /* Recupero il file originale */
    // byte[] fileVerSerieOriginale =
    // helper.retrieveFileVerSerie(verSerieDaMarcare.getSerVerSerie(),
    // CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name());
    // try {
    // /*
    // * Il metodo marcaFirma deve essere eseguito in modo atomico (in una nuova transazione
    // REQUIRES_NEW).
    // * Per fare cio' devo passare nuovamente dal container quindi utilizzo getBusinessObject per
    // ottenere un
    // * nuovo riferimento a SerieEjb
    // */
    // SerieEjb serieEjb1 = context.getBusinessObject(SerieEjb.class);
    // BigDecimal idStrut = verSerieDaMarcare.getIdStrut();
    // it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma = amministrazioneEjb
    // .getTipoFirmaPerStruttura(idStrut);
    // marcaTemporale = serieEjb1.marcaFirma(verSerieDaMarcare.getSerVerSerie().getIdVerSerie(),
    // fileVerSerieOriginale, verSerieDaMarcareList.size(), idUtente, tipoFirma);
    // } catch (Exception ex) {
    // throw new ParerUserError("Errore nella marcatura di una versione serie: marcate " + signed +
    // " su "
    // + verSerieDaMarcareList.size());
    // }
    //
    // if (marcaTemporale != null) {
    // signed++;
    // } else {
    // /* Problema nell'acquisizione della marcatura temporale */
    // throw new ParerUserError("Errore durante l'acquisizione della marca temporale");
    // }
    // }
    // return signed;
    // }

    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // public byte[] marcaFirma(long idVerSerie, byte[] fileVerSerie, int serieSize, long idUtente,
    // ElencoEnums.TipoFirma tipoFirma) throws Exception {
    // // TimeStampToken tsToken = null;
    // ParerTST tsToken = null;
    // byte[] marcaTemporale = null;
    // /*
    // * Devo recuperare la serie dal suo id e non utilizzare direttamente l'oggetto perché siamo in
    // una nuova
    // * transazione e utilizzando l'oggetto passato in input non avrei il riferimento agli oggetti
    // figli (la versione
    // * serie sarebbe detached e non avrei piu il collegamento al db).
    // */
    // SerVerSerieDaElab verSerieDaElab = helper.getSerVerSerieDaElabByIdVerSerie(idVerSerie);
    // List<AroUdAppartVerSerie> udAppartVerSerieList =
    // helper.getLockAroUdAppartVerSerie(idVerSerie,
    // CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
    // /* Richiedo la marca per il file firmato */
    // tsToken = cryptoInvoker.requestTST(fileVerSerie);
    // marcaTemporale = tsToken.getEncoded();
    // /* Verifico l'avvenuta acquisizione della marcatura temporale */
    // if (marcaTemporale != null) {
    // logger.info("Marca temporale valida");
    //
    // /* Urn marca */
    // String versioneSerie = verSerieDaElab.getSerVerSerie().getCdVerSerie();
    // final String sistema = configurationHelper
    // .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
    // CSVersatore csVersatore = new CSVersatore();
    // csVersatore.setAmbiente(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte()
    // .getOrgAmbiente().getNmAmbiente());
    // csVersatore.setEnte(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte().getNmEnte());
    // csVersatore.setStruttura(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getNmStrut());
    // // sistema (new URN)
    // csVersatore.setSistemaConservazione(sistema);
    // String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();
    // String cdVerXsdFile =
    // verSerieDaElab.getSerVerSerie().getSerFileVerSeries().get(0).getCdVerXsdFile();
    //
    // // MEV#30400
    //
    // BackendStorage backendIndiciAip = objectStorageService.lookupBackendIndiciAipSerieUD(
    // verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getIdStrut());
    //
    // boolean putOnOs = true;
    // if
    // (objectStorageService.isSerFileVerSerieUDOnOs(verSerieDaElab.getSerVerSerie().getIdVerSerie(),
    // CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO.name())) {
    // String md5LocalContent = calculateMd5AsBase64(new String(marcaTemporale,
    // StandardCharsets.UTF_8));
    // String eTagFromObjectMetadata = objectStorageService
    // .getObjectMetadataIndiceAipSerieUD(verSerieDaElab.getSerVerSerie().getIdVerSerie(),
    // CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO.name())
    // .eTag();
    //
    // if (md5LocalContent.equals(eTagFromObjectMetadata)) {
    // putOnOs = false;
    // }
    // }
    //
    // /* Registra nella tabella SER_FILE_VER_SERIE la marca */
    // SerFileVerSerie serFileVerSerie = helper.storeFileIntoSerFileVerSerie(idVerSerie,
    // CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO.name(), marcaTemporale, cdVerXsdFile,
    // verSerieDaElab.getIdStrut(), new Date(), putOnOs, tipoFirma);
    //
    // // EVO#16492
    // /* Calcolo e persisto urn dell'indice AIP marcato della serie */
    // helper.storeSerUrnFileVerSerieMarca(serFileVerSerie, csVersatore, codiceSerie,
    // versioneSerie);
    // // end EVO#16492
    //
    // ObjectStorageResource indiceAipSuOS;
    //
    // if (putOnOs) {
    // indiceAipSuOS = objectStorageService.createResourcesInIndiciAipSerieUD(serFileVerSerie,
    // backendIndiciAip.getBackendName(), marcaTemporale,
    // verSerieDaElab.getSerVerSerie().getIdVerSerie(), verSerieDaElab.getIdStrut(), csVersatore,
    // codiceSerie, versioneSerie);
    // logger.debug(LOG_SALVATAGGIO_OS, indiceAipSuOS.getBucket(), indiceAipSuOS.getKey());
    // }
    //
    // // end MEV#30400
    //
    // /* Registra il nuovo stato di versione della serie */
    // SerStatoVerSerie statoVerSerie =
    // context.getBusinessObject(SerieEjb.class).createSerStatoVerSerie(
    // ciasudHelper.getUltimoProgressivoSerStatoVerSerie(idVerSerie).add(BigDecimal.ONE),
    // CostantiDB.StatoVersioneSerie.FIRMATA.name(), "Apposizione marca indice AIP serie", null,
    // idUtente,
    // new Date(), idVerSerie);
    //
    // /* Aggiorna l'identificatore dello stato corrente della versione della serie */
    // helper.insertEntity(statoVerSerie, false);
    // verSerieDaElab.getSerVerSerie().setIdStatoVerSerieCor(new
    // BigDecimal(statoVerSerie.getIdStatoVerSerie()));
    //
    // /* Registra un nuovo stato della serie */
    // long idSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getIdSerie();
    // SerStatoSerie statoSerie = context.getBusinessObject(SerieEjb.class).createSerStatoSerie(
    // ciasudHelper.getUltimoProgressivoSerStatoSerie(idSerie).add(BigDecimal.ONE),
    // CostantiDB.StatoConservazioneSerie.IN_ARCHIVIO.name(), "Firma e marcatura indice AIP serie",
    // null,
    // idUtente, new Date(), idSerie);
    //
    // /* Aggiorna l'identificatore dello stato corrente della serie */
    // helper.insertEntity(statoSerie, false);
    // verSerieDaElab.getSerVerSerie().getSerSerie()
    // .setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));
    //
    // /* Aggiorna la versione serie da elaborare in FIRMATA */
    // verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.FIRMATA.name());
    //
    // /*
    // * Aggiorna tutte le unit doc appartenenti al contenuto di tipo EFFETTIVO della versione serie
    // con stato =
    // * AIP_GENERATO, assegnando stato conservazione = IN_ARCHIVIO
    // */
    // SerContenutoVerSerie contenutoVerSerie = helper.getSerContenutoVerSerie(idVerSerie,
    // CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
    //
    // // MEV #31162
    // List<Long> idUnitaDocList = helper.getStatoConservazioneAroUnitaDocInContenuto(
    // contenutoVerSerie.getIdContenutoVerSerie(),
    // CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
    // // end MEV #31162
    //
    // helper.updateStatoConservazioneAroUnitaDocInContenuto(contenutoVerSerie.getIdContenutoVerSerie(),
    // CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
    // CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name());
    //
    // // MEV #31162
    // IamUser utente = helper.findById(IamUser.class, idUtente);
    // for (Long idUnitaDoc : idUnitaDocList) {
    // udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(), Constants.MARCA_FIRMA_SERIE,
    // CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name(), Constants.FUNZIONALITA_ONLINE);
    // }
    // // end MEV #31162
    //
    // }
    // return marcaTemporale;
    // }

    // MEV#30400
    private String calculateMd5AsBase64(String str) {
	return java.util.Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    // end MEV#30400

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void riavvioValidazioneSerie(BigDecimal idSerie, BigDecimal idVerSerie)
	    throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    SerVerSerie verSerie = helper.findByIdWithLock(SerVerSerie.class, idVerSerie);
	    if (verSerie != null) {
		String[] blocco = getFlVersioneBloccata(
			JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(), idVerSerie);
		if (blocco != null) {
		    if (!blocco[0].equals("1")) {
			throw new ParerUserError("La validazione della serie non \u00E8 bloccata");
		    }
		}
	    } else {
		throw new ParerUserError(
			"La versione della serie \u00E8 in uso da parte di un altro utente");
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    public SerUrnFileVerSerie getUrnFileVerSerieNormalizzatoByIdVerSerieAndTiFile(
	    BigDecimal idVerSerie, String tiFile) {
	List<TiUrnFileVerSerie> list = new ArrayList<>();
	list.add(TiUrnFileVerSerie.NORMALIZZATO);
	return helper.getUrnFileVerSerie(idVerSerie, list, tiFile);
    }

    // MEV#15967 - Attivazione della firma Xades e XadesT
    public String getTipoFirmaFileVerSerie(BigDecimal idVerSerie, String tiFileVerSerie)
	    throws ParerUserError {
	String tipo = null;
	try {
	    SerFileVerSerie fileVerSerie = helper.getSerFileVerSerie(idVerSerie.longValue(),
		    tiFileVerSerie);
	    if (fileVerSerie != null) {
		tipo = fileVerSerie.getTiFirma();
	    }
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nell'ottenimento del tipo firma del file ver serie";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return tipo;
    }

    public String[] ambienteEnteStrutturaSerie(BigDecimal idSerie) {
	String[] ambEnteStrut = new String[3];
	SerSerie serie = helper.findById(SerSerie.class, idSerie);
	ambEnteStrut[0] = serie.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente();
	ambEnteStrut[1] = serie.getOrgStrut().getOrgEnte().getNmEnte();
	ambEnteStrut[2] = serie.getOrgStrut().getNmStrut();
	return ambEnteStrut;
    }

    public String[] getNameAndFileIndiceVolume(long idVolVerSerie) throws ParerUserError {
	String[] file = new String[2];
	try {
	    SerIxVolVerSerie fileIndiceVolume = helper.getSerIxVolVerSerie(idVolVerSerie);
	    // TODO: VERIFICARE DURANTE INTERVENTO SULL'ADEGUAMENTO DEI RECUPERI
	    file[0] = fileIndiceVolume.getSerUrnIxVolVerSeries().get(0).getDsUrn().substring(4)
		    .replaceAll(" ", "_");
	    // end TODO
	    file[1] = fileIndiceVolume.getBlIxVol();
	} catch (Exception e) {
	    String messaggio = "Eccezione imprevista nell'ottenimento del file da scaricare";
	    messaggio += ExceptionUtils.getRootCauseMessage(e);
	    logger.error(messaggio, e);
	    throw new ParerUserError(messaggio);
	}
	return file;
    }

    public void createZipPacchettoArk(BigDecimal idVerSerie, ZipOutputStream out,
	    String ixAIPFileName, String ixVolPrefix) throws IOException, ParerUserError {
	final String ixAipExt = ".xml.p7m";
	final String marcaIxAipExt = ".tsr";
	final String ixVolExt = ".xml";

	byte[] indiceAip = context.getBusinessObject(SerieEjb.class).getSerFileVerSerieBlob(
		idVerSerie.longValue(), CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO);
	byte[] marcaAip = context.getBusinessObject(SerieEjb.class).getSerFileVerSerieBlob(
		idVerSerie.longValue(), CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO);
	if (indiceAip == null) {
	    throw new ParerUserError(
		    "Errore inaspettato nell'esecuzione del download: indice AIP firmato non presente");
	}

	// MEV#15967 - Attivazione della firma Xades e XadesT
	SerFileVerSerie serFile = helper.getSerFileVerSerie(idVerSerie.longValueExact(),
		CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name());

	SerUrnFileVerSerie verIndice = context.getBusinessObject(SerieEjb.class)
		.getUrnFileVerSerieNormalizzatoByIdVerSerieAndTiFile(idVerSerie,
			CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name());
	String filename = null;

	if (verIndice != null) {
	    filename = verIndice.getDsUrn();
	}

	if (serFile.getTiFirma() != null && serFile.getTiFirma()
		.equals(it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.XADES.name())) {
	    addEntryToZip(out, indiceAip, filename + ixVolExt);
	} else {
	    addEntryToZip(out, indiceAip, ixAIPFileName + ixAipExt);
	}
	//

	if (marcaAip != null) {
	    addEntryToZip(out, marcaAip, ixAIPFileName + marcaIxAipExt);
	}

	List<SerIxVolVerSerie> ixVolumi = helper.getSerIxVolVerSerieList(idVerSerie.longValue());
	for (SerIxVolVerSerie ix : ixVolumi) {
	    String ixVolFileName = ixVolPrefix
		    + ix.getSerVolVerSerie().getPgVolVerSerie().toPlainString() + ixVolExt;
	    byte[] ixVol = ix.getBlIxVol().getBytes(StandardCharsets.UTF_8);
	    addEntryToZip(out, ixVol, ixVolFileName);
	}
    }

    private void addEntryToZip(ZipOutputStream out, byte[] file, String filename)
	    throws IOException {
	byte[] data = new byte[1024];
	InputStream bis = null;
	try {
	    bis = new ByteArrayInputStream(file);
	    int count;
	    out.putNextEntry(new ZipEntry(filename));
	    while ((count = bis.read(data, 0, 1024)) != -1) {
		out.write(data, 0, count);
	    }
	    out.closeEntry();
	} finally {
	    IOUtils.closeQuietly(bis);
	}
    }

    public void checkDecTipoSerieUd(BigDecimal idTipoSerie) throws ParerUserError {
	List<DecTipoSerieUd> decTipoSerieUds = helper.getDecTipoSerieUd(idTipoSerie.longValue());
	if (decTipoSerieUds.isEmpty()) {
	    throw new ParerUserError(
		    "Impossibile eseguire il calcolo della serie, non esistono associazioni registri - tipologie di unit\u00E0 documentarie per il tipo di serie");
	} else {
	    for (DecTipoSerieUd tipo : decTipoSerieUds) {
		// Controllo che esistano tutte le regole di rappresentazione obbligatorie
		if (tipo.getDecOutSelUds() == null || tipo.getDecOutSelUds().isEmpty()
			|| tipo.getDecOutSelUds().size() < 4) {
		    throw new ParerUserError(
			    "Impossibile eseguire il calcolo della serie, perch\u00E9 per almeno una associazione registro - tipologia unit\u00E0 documentaria non sono definite tutte le regole di acquisizione obbligatorie");
		}
	    }
	}
    }

    public boolean checkSerieDaRigenerare(BigDecimal idVerSerie) {
	SerVChkSerieUdDaRigenera check = helper.findViewById(SerVChkSerieUdDaRigenera.class,
		idVerSerie);
	return (check.getFlVerSerieDaRigenera().equals("1"));
    }

    public boolean checkSerieModificabili(BigDecimal idTipoSerie) {
	List<SerVerSerie> serie = helper.getVersioniSerieCorrentiByTipoSerieAndStato(idTipoSerie,
		CostantiDB.StatoVersioneSerie.APERTA.name(),
		CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name());
	return !serie.isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setFlagContenutoVerSerieDaAggiornare(BigDecimal idTipoSerie) {
	List<SerVerSerie> verSeries = helper.getVersioniSerieCorrentiByTipoSerieAndStato(
		idTipoSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
		CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
		CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
		CostantiDB.StatoVersioneSerie.DA_VALIDARE.name());

	for (SerVerSerie verSerie : verSeries) {
	    for (SerContenutoVerSerie contenuto : verSerie.getSerContenutoVerSeries()) {
		contenuto.setFlTipoSerieUpd("1");
	    }
	}

    }

    public SerVLisSerDaValidareTableBean getSerVLisSerDaValidareTableBean(Long idUserIam,
	    BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut) throws ParerUserError {
	List<SerVLisSerDaValidare> list = helper.getSerVLisSerDaValidare(idUserIam, idAmbiente,
		idEnte, idStrut);
	SerVLisSerDaValidareTableBean table = new SerVLisSerDaValidareTableBean();
	if (!list.isEmpty()) {
	    try {
		for (SerVLisSerDaValidare record : list) {
		    SerVLisSerDaValidareRowBean row = (SerVLisSerDaValidareRowBean) Transform
			    .entity2RowBean(record);
		    row.setString("amb_ente_strut", record.getNmAmbiente() + " - "
			    + record.getNmEnte() + " - " + record.getNmStrut());
		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero delle serie da validare "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new ParerUserError("Errore durante il recupero delle serie da validare");
	    }
	}
	return table;

    }

    public boolean existQueryContenutoVerSerie(BigDecimal idContenutoVerSerie,
	    BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) {
	Long result = helper.countSerQueryContenutoVerSerie(idContenutoVerSerie, idRegistroUnitaDoc,
		idTipoUnitaDoc);
	return result > 0L;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int aggiungiUnitaDocAlContenuto(long idUser, BigDecimal idContenutoVerSerie,
	    Map<RegistroTipoUnitaDoc, List<BigDecimal>> map) throws ParerUserError {
	SerContenutoVerSerie contenuto = helper.findByIdWithLock(SerContenutoVerSerie.class,
		idContenutoVerSerie);
	int countUd = 0;
	if (contenuto != null) {
	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Scorro la mappa di dati per ottenere le query necessarie");
	    for (Map.Entry<RegistroTipoUnitaDoc, List<BigDecimal>> entry : map.entrySet()) {
		final RegistroTipoUnitaDoc registroTipoUnitaDoc = entry.getKey();
		SerQueryContenutoVerSerie query = helper.getSerQueryContenutoVerSerie(
			contenuto.getIdContenutoVerSerie(),
			registroTipoUnitaDoc.getIdRegistroUnitaDoc(),
			registroTipoUnitaDoc.getIdTipoUnitaDoc());
		String redecoratedQuery = redecorateQuery(query.getBlQuery(), entry.getValue(),
			contenuto.getSerVerSerie().getDtInizioSelSerie(),
			contenuto.getSerVerSerie().getDtFineSelSerie());
		if (redecoratedQuery != null) {
		    List<ResultVCalcoloSerieUd> resultSet = helper
			    .executeQueryList(redecoratedQuery);
		    countUd += resultSet.size();
		    for (ResultVCalcoloSerieUd result : resultSet) {
			context.getBusinessObject(SerieEjb.class)
				.handleAroUnitaDoc(contenuto.getIdContenutoVerSerie(), result);
		    }
		}
	    }

	    logger.debug(SerieEjb.class.getSimpleName()
		    + " --- Aggiorna il contenuto della versione con i dati delle ud appartenenti ad essa");
	    Long firstUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), true);
	    Long lastUd = helper.getUdAppartVerSerie(contenuto.getIdContenutoVerSerie(), false);

	    contenuto.setNiUdContenutoVerSerie(new BigDecimal(
		    helper.countAroUdAppartVerSerie(idContenutoVerSerie.longValue())));
	    contenuto.setIdFirstUdAppartVerSerie(
		    firstUd != null ? new BigDecimal(firstUd) : BigDecimal.ZERO);
	    contenuto.setIdLastUdAppartVerSerie(
		    lastUd != null ? new BigDecimal(lastUd) : BigDecimal.ZERO);

	    Date now = Calendar.getInstance().getTime();
	    if (contenuto.getTiContenutoVerSerie()
		    .equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
		SerVerSerie verSerie = contenuto.getSerVerSerie();
		BigDecimal idStatoVerSerie = verSerie.getIdStatoVerSerieCor();
		SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
			idStatoVerSerie);
		if (statoCorrente.getTiStatoVerSerie()
			.equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())) {
		    SerStatoVerSerie statoVerSerie = context.getBusinessObject(SerieEjb.class)
			    .createSerStatoVerSerie(
				    statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
				    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
				    "Aggiunte unit\u00E0 documentarie alla serie", null, idUser,
				    now, verSerie.getIdVerSerie());
		    helper.insertEntity(statoVerSerie, false);
		    verSerie.setIdStatoVerSerieCor(
			    new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

		    helper.deleteSerVerSerieDaElab(new BigDecimal(verSerie.getIdVerSerie()));
		}
	    }
	    if (contenuto.getTiStatoContenutoVerSerie()
		    .equals(CostantiDB.StatoContenutoVerSerie.CREATO.name())
		    || contenuto.getTiStatoContenutoVerSerie()
			    .equals(CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
		contenuto.setTiStatoContenutoVerSerie(
			CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());
		contenuto.setDtStatoContenutoVerSerie(now);
	    }
	} else {
	    throw new ParerUserError("Impossibile ottenere il lock sul contenuto");
	}
	return countUd;
    }

    private String redecorateQuery(String blQuery, List<BigDecimal> idUnitaDocs,
	    Date dtInizioSelSerie, Date dtFineSelSerie) throws ParerUserError {
	String redecoratedQuery = null;
	if (!idUnitaDocs.isEmpty()) {
	    int indexCropTmp = blQuery.indexOf(") tmp ");
	    int indexSubstWhere = -1;
	    if (indexCropTmp != -1) {
		String croppedQuery = blQuery.substring(0, indexCropTmp);
		indexSubstWhere = croppedQuery.lastIndexOf("WHERE ud.id_strut");
		if (indexSubstWhere != -1) {
		    // Riscrivo la query: riscrivo la prima where
		    StringBuilder builder = new StringBuilder(
			    blQuery.substring(0, indexSubstWhere));
		    if (idUnitaDocs.size() > 1) {
			builder.append("WHERE ud.id_unita_doc IN (");
			for (BigDecimal idUnitaDoc : idUnitaDocs) {
			    builder.append(idUnitaDoc.toPlainString()).append(",");
			}
			builder.deleteCharAt(builder.length() - 1).append(")");
		    } else {
			builder.append("WHERE ud.id_unita_doc = ");
			for (BigDecimal idUnitaDoc : idUnitaDocs) {
			    builder.append(idUnitaDoc.toPlainString());
			}
		    }
		    // Riscrivo la seconda where
		    builder.append(") tmp WHERE ").append("tmp.key_ud_serie IS NOT NULL")
			    .append(" AND ").append("tmp.dt_ud_serie IS NOT NULL").append(" AND ")
			    .append("tmp.info_ud_serie IS NOT NULL").append(" AND ")
			    .append("tmp.ds_key_ord_ud_serie IS NOT NULL");
		    if (dtInizioSelSerie != null && dtFineSelSerie != null) {
			SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
			builder.append(" AND ").append("tmp.dt_ud_serie >= ").append("to_date('")
				.append(df.format(dtInizioSelSerie)).append("', 'dd/mm/yyyy')")
				.append(" AND ").append("tmp.dt_ud_serie <= ").append("to_date('")
				.append(df.format(dtFineSelSerie)).append("', 'dd/mm/yyyy')");
		    }
		    redecoratedQuery = builder.toString();
		}
	    }

	    if (indexCropTmp == -1 || indexSubstWhere == -1) {
		throw new ParerUserError(
			"Errore inatteso in aggiunta dell'unit\u00E0 documentaria: impossibile ricostruire la query di calcolo serie");
	    }
	}
	return redecoratedQuery;
    }

    public boolean existUdInContenutoSerie(BigDecimal idContenutoVerSerie, BigDecimal idUnitaDoc) {
	return helper.existUdInSerie(idContenutoVerSerie.longValue(), idUnitaDoc);
    }

    public DecTipoSerieUdTableBean getDecTipoSerieUdTableBean(BigDecimal idTipoSerie)
	    throws ParerUserError {
	DecTipoSerieUdTableBean table = new DecTipoSerieUdTableBean();
	List<DecTipoSerieUd> list = helper.getDecTipoSerieUd(idTipoSerie.longValue());
	if (list != null && !list.isEmpty()) {
	    try {
		table = (DecTipoSerieUdTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		String msg = "Errore durante il recupero della lista di associazioni dei tipi serie di unit\u00e0 documentarie "
			+ ExceptionUtils.getRootCauseMessage(ex);
		logger.error(msg, ex);
		throw new ParerUserError(msg);
	    }
	}
	return table;
    }

    /**
     * Ricerca serie consistenze attese
     *
     * @param filtri bean RicercaSerieBean
     *
     * @return entity bean SerVRicConsistSerieUdTableBean
     */
    public SerVRicConsistSerieUdTableBean getSerVRicConsistSerieUdTableBean(
	    RicercaSerieBean filtri) {
	SerVRicConsistSerieUdTableBean table = new SerVRicConsistSerieUdTableBean();
	List<SerVRicConsistSerieUd> list = helper.getSerVRicConsistSerieUd(filtri);
	if (list != null && !list.isEmpty()) {
	    try {
		for (SerVRicConsistSerieUd record : list) {
		    SerVRicConsistSerieUdRowBean row = (SerVRicConsistSerieUdRowBean) Transform
			    .entity2RowBean(record);
		    row.setString("showContenutoEffettivo", "Mostra contenuto effettivo");
		    row.setString("amb_ente_strut", record.getNmAmbiente() + " - "
			    + record.getNmEnte() + " - " + record.getNmStrut());
		    int lacuneMancanti = record.getNiLacuneMancanti().intValue();
		    int lacuneNonProdotte = record.getNiLacuneNonProdotte().intValue();
		    row.setString("fl_presenza_lacune",
			    ((lacuneMancanti > 0 || lacuneNonProdotte > 0) ? "1" : "0"));
		    table.add(row);
		}
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero delle serie "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
	    }
	}
	return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteSerConsistVerSerie(long idUser, BigDecimal idConsistVerSerie,
	    BigDecimal idVerSerie, BigDecimal idSerie) throws ParerUserError {
	SerSerie serie = helper.findByIdWithLock(SerSerie.class, idSerie);
	if (serie != null) {
	    if (context.getBusinessObject(SerieEjb.class).checkVersione(idVerSerie,
		    CostantiDB.StatoVersioneSerie.APERTA.name(),
		    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
		    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
		    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
		if (context.getBusinessObject(SerieEjb.class).checkContenuto(idVerSerie, true, true,
			true, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
			CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
			CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
		    try {
			SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
			SerConsistVerSerie consist = helper.findById(SerConsistVerSerie.class,
				idConsistVerSerie);
			helper.removeEntity(consist, true);

			// Se versione serie ha stato corrente = CONTROLLATA o DA_VALIDARE, la
			// versione serie assume
			// stato = DA_CONTROLLARE
			SerStatoVerSerie statoCorrente = helper.findById(SerStatoVerSerie.class,
				verSerie.getIdStatoVerSerieCor());
			if (statoCorrente.getTiStatoVerSerie()
				.equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
				|| statoCorrente.getTiStatoVerSerie()
					.equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
			    Date now = Calendar.getInstance().getTime();
			    logger.info(SerieEjb.class.getSimpleName()
				    + " --- Aggiorna lo stato della versione");
			    SerStatoVerSerie statoVerSerie = context
				    .getBusinessObject(SerieEjb.class).createSerStatoVerSerie(
					    statoCorrente.getPgStatoVerSerie().add(BigDecimal.ONE),
					    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
					    "Eliminazione consistenza attesa", null, idUser, now,
					    verSerie.getIdVerSerie());

			    logger.info(SerieEjb.class.getSimpleName()
				    + " --- Eseguo la persist del nuovo stato versione");
			    helper.insertEntity(statoVerSerie, false);
			    verSerie.setIdStatoVerSerieCor(
				    new BigDecimal(statoVerSerie.getIdStatoVerSerie()));
			}
			SerContenutoVerSerie contenCalc = helper.getSerContenutoVerSerie(
				verSerie.getIdVerSerie(),
				CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
			SerContenutoVerSerie contenAcq = helper.getSerContenutoVerSerie(
				verSerie.getIdVerSerie(),
				CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
			SerContenutoVerSerie contenEff = helper.getSerContenutoVerSerie(
				verSerie.getIdVerSerie(),
				CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

			if (contenCalc != null && contenCalc.getTiStatoContenutoVerSerie().equals(
				CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
			    contenCalc.setTiStatoContenutoVerSerie(
				    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
					    .name());
			}
			if (contenAcq != null && contenAcq.getTiStatoContenutoVerSerie().equals(
				CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
			    contenAcq.setTiStatoContenutoVerSerie(
				    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
					    .name());
			}
			if (contenEff != null && contenEff.getTiStatoContenutoVerSerie().equals(
				CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
			    contenEff.setTiStatoContenutoVerSerie(
				    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
					    .name());
			    helper.deleteSerVerSerieDaElab(
				    new BigDecimal(verSerie.getIdVerSerie()));
			}
		    } catch (Exception e) {
			String messaggio = "Eccezione imprevista nell'eliminazione della consistenza della serie: ";
			messaggio += ExceptionUtils.getRootCauseMessage(e);
			logger.error(messaggio, e);
			throw new ParerUserError(messaggio);
		    }
		} else {
		    throw new ParerUserError(
			    "La consistenza attesa non pu\u00F2 essere eliminata perch\u00E9 almeno un contenuto ha stato diverso da CREATO e CONTROLLATA_CONSIST e DA_CONTROLLARE_CONSIST");
		}
	    } else {
		throw new ParerUserError(
			"La consistenza attesa non pu\u00F2 essere eliminta perch\u00E9 la versione della serie ha stato diverso da APERTA e DA_CONTROLLARE e CONTROLLATA e DA_VALIDARE");
	    }
	} else {
	    throw new ParerUserError("La serie \u00E8 in uso da parte di un altro utente");
	}
    }

    public BigDecimal getDimensioneSerie(BigDecimal idContenutoEff) {
	SerVCalcDimSerie dim = helper.findViewById(SerVCalcDimSerie.class, idContenutoEff);
	return dim.getNiMbSizeContenutoEff();
    }

    public boolean existsCdSerieNormalized(BigDecimal idStrut, BigDecimal aaSerie,
	    String cdSerieNormaliz) {
	return helper.existsCdSerNormaliz(idStrut.longValue(), aaSerie, cdSerieNormaliz);
    }

}
