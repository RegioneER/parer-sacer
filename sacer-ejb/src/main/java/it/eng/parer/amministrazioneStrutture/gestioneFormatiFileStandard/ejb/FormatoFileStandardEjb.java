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

package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper.FormatoFileDocHelper;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecFormatoFileBusta;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecFormatoGruppoProprieta;
import it.eng.parer.entity.DecFormatoProprieta;
import it.eng.parer.entity.DecFormatoValutazione;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.FormatiForm;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileRowBean;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.base.BaseElements;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei formati file standard
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({
	TransactionInterceptor.class })
public class FormatoFileStandardEjb {

    private static final Logger logger = LoggerFactory.getLogger(FormatoFileStandardEjb.class);

    private static final String GESTITO = "GESTITO";
    private static final String IDONEO = "IDONEO";
    private static final String DEPRECATO = "DEPRECATO";

    @EJB
    private FormatoFileStandardHelper helper;
    @EJB
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB
    private FormatoFileDocHelper formatoFileDocHelper;

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardInList(Set<String> list,
	    BaseElements.Status status, BigDecimal idStrut) {
	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<Object[]> formati = helper.getDecFormatoFileStandardInList(list, status, idStrut);
	try {
	    if (!formati.isEmpty()) {
		DecFormatoFileStandardRowBean formatoRB;
		for (Object[] formato : formati) {
		    formatoRB = (DecFormatoFileStandardRowBean) Transform
			    .entity2RowBean(formato[0]);
		    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
		    Date dtIstituz = ((Date) formato[2]);
		    Date dtSoppres = ((Date) formato[3]);
		    if (dtIstituz.before(new Date()) && dtSoppres.after(new Date())) {
			formatoRB.setObject("fl_attivo", "1");
		    } else {
			formatoRB.setObject("fl_attivo", "0");
		    }
		    formatoTableBean.add(formatoRB);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new IllegalStateException(
		    "Errore inatteso nel recupero dei formati file standard");
	}
	return formatoTableBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardInListByName(
	    Collection<String> list) {

	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<Object[]> formati = helper.getDecFormatoFileStandardInListByName(list);
	try {
	    if (!formati.isEmpty()) {
		DecFormatoFileStandardRowBean formatoRB;
		for (Object[] formato : formati) {
		    formatoRB = (DecFormatoFileStandardRowBean) Transform
			    .entity2RowBean(formato[0]);
		    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
		    formatoRB.setString("cd_estensione_file_busta",
			    ((String) formato[1]).toUpperCase());
		    formatoTableBean.add(formatoRB);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return formatoTableBean;
    }

    public DecEstensioneFileTableBean getDecEstensioneFileListByName(Collection<String> list) {

	DecEstensioneFileTableBean formatoTableBean = new DecEstensioneFileTableBean();
	List<Object[]> formati = helper.getDecFormatoFileStandardInListByName(list);
	try {
	    if (!formati.isEmpty()) {
		DecEstensioneFileRowBean formatoRB;
		for (Object[] formato : formati) {
		    formatoRB = (DecEstensioneFileRowBean) Transform.entity2RowBean(formato[0]);
		    formatoRB.setString("cd_estensione_file", ((String) formato[1]).toUpperCase());
		    formatoRB.setString("cd_estensione_file_busta",
			    ((String) formato[1]).toUpperCase());
		    formatoTableBean.add(formatoRB);

		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return formatoTableBean;
    }

    public List<String> getFormatiFileStandardConcatenabili() {
	return helper.getFormatiFileStandardConcatenabili();
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardNotInList(
	    Collection<String> list, BaseElements.Status status) {

	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<Object[]> formati = helper.getDecFormatoFileStandardNotInList(list, status);

	try {
	    if (!formati.isEmpty()) {
		DecFormatoFileStandardRowBean formatoRB;
		Map<DecFormatoFileStandard, SortedSet<String>> formatiMap = new HashMap<>();
		for (Object[] formato : formati) {
		    if (formatiMap.containsKey((DecFormatoFileStandard) formato[0])) {
			SortedSet<String> estensioni = formatiMap
				.get((DecFormatoFileStandard) formato[0]);
			estensioni.add((String) formato[1]);
			formatiMap.put((DecFormatoFileStandard) formato[0], estensioni);
		    } else {
			SortedSet<String> estensione = new TreeSet<>();
			estensione.add((String) formato[1]);
			formatiMap.put((DecFormatoFileStandard) formato[0], estensione);
		    }
		}

		for (Map.Entry<DecFormatoFileStandard, SortedSet<String>> entry : formatiMap
			.entrySet()) {
		    formatoRB = (DecFormatoFileStandardRowBean) Transform
			    .entity2RowBean(entry.getKey());
		    SortedSet<String> estensioni = entry.getValue();
		    String estensioniString = "";
		    for (String estensione : estensioni) {
			if (estensioniString.equals("")) {
			    estensioniString = estensione;
			} else {
			    estensioniString = estensioniString + "; " + estensione;
			}
		    }
		    formatoRB.setString("cd_estensione_file", estensioniString);
		    formatoTableBean.add(formatoRB);
		}

		formatoTableBean.addSortingRule("nm_mimetype_file");
		formatoTableBean.sort();
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return formatoTableBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardNotInListCompresoStruttura(
	    Collection<String> list, BigDecimal idStrut, BaseElements.Status status) {

	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<Object[]> formati = helper.getDecFormatoFileStandardNotInList(list, status);

	try {
	    if (!formati.isEmpty()) {
		DecFormatoFileStandardRowBean formatoRB;
		Map<DecFormatoFileStandard, SortedSet<String>> formatiMap = new HashMap<>();
		for (Object[] formato : formati) {
		    if (formatiMap.containsKey((DecFormatoFileStandard) formato[0])) {
			SortedSet<String> estensioni = formatiMap
				.get((DecFormatoFileStandard) formato[0]);
			estensioni.add((String) formato[1]);
			formatiMap.put((DecFormatoFileStandard) formato[0], estensioni);
		    } else {
			SortedSet<String> estensione = new TreeSet<>();
			estensione.add((String) formato[1]);
			formatiMap.put((DecFormatoFileStandard) formato[0], estensione);
		    }
		}

		for (Map.Entry<DecFormatoFileStandard, SortedSet<String>> entry : formatiMap
			.entrySet()) {
		    formatoRB = (DecFormatoFileStandardRowBean) Transform
			    .entity2RowBean(entry.getKey());
		    SortedSet<String> estensioni = entry.getValue();
		    String estensioniString = "";
		    for (String estensione : estensioni) {
			if (estensioniString.equals("")) {
			    estensioniString = estensione;
			} else {
			    estensioniString = estensioniString + "; " + estensione;
			}
		    }
		    formatoRB.setString("cd_estensione_file", estensioniString);
		    formatoTableBean.add(formatoRB);
		}

		formatoTableBean.addSortingRule("nm_mimetype_file");
		formatoTableBean.sort();
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	}
	return formatoTableBean;
    }

    public String getDecFormatoFileStandardFromEstensioneFile(String cdEstensioneFile) {
	return helper.getDecFormatoFileStandardNameFromEstensioneFile(cdEstensioneFile);
    }

    public List<String> getDecFormatoFileStandardNameList(BigDecimal idFormatoFileDoc) {
	return helper.getDecFormatoFileStandardNameList(idFormatoFileDoc);
    }

    public DecEstensioneFileRowBean getDecEstensioneFileRowBean(BigDecimal idEstensioneFile,
	    BigDecimal idFormatoFileStandard) {
	return getDecEstensioneFile(idEstensioneFile, null, idFormatoFileStandard);
    }

    public DecEstensioneFileRowBean getDecEstensioneFileRowBean(String cdEstensioneFile,
	    BigDecimal idFormatoFileStandard) {
	return getDecEstensioneFile(BigDecimal.ZERO, cdEstensioneFile, idFormatoFileStandard);
    }

    public DecEstensioneFileRowBean getDecEstensioneFile(BigDecimal idEstensioneFile,
	    String nmEstensioneFile, BigDecimal idFormatoFileStandard) {
	DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();
	DecEstensioneFile estensioneFile = null;

	// FIXMEPLEASE Non capisco l'utilità di questi controlli
	if (idEstensioneFile == BigDecimal.ZERO && nmEstensioneFile != null) {
	    estensioneFile = helper.getDecEstensioneFileByName(nmEstensioneFile,
		    idFormatoFileStandard);
	}
	if (nmEstensioneFile == null && idEstensioneFile != BigDecimal.ZERO) {
	    estensioneFile = helper.findById(DecEstensioneFile.class, idEstensioneFile);
	}
	if (estensioneFile != null) {
	    try {
		estensioneFileRowBean = (DecEstensioneFileRowBean) Transform
			.entity2RowBean(estensioneFile);
	    } catch (Exception e) {
		logger.error(e.getMessage());
		throw new IllegalStateException(
			"Errore inatteso nel recupero dell'estensione file");
	    }
	}
	return estensioneFileRowBean;
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandardRowBean(
	    BigDecimal idFormatoFileStandard) {
	return getDecFormatoFileStandard(idFormatoFileStandard, null);
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandardRowBean(
	    String nmFormatoFileStandard) {
	return getDecFormatoFileStandard(BigDecimal.ZERO, nmFormatoFileStandard);
    }

    public DecFormatoFileStandardRowBean getDecFormatoFileStandard(BigDecimal idFormatoFileStandard,
	    String nmFormatoFileStandard) {
	DecFormatoFileStandard formatoFileStandard = null;
	DecFormatoFileStandardRowBean formatoFileStandardRowBean = null;
	if (idFormatoFileStandard != BigDecimal.ZERO && nmFormatoFileStandard == null) {
	    formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		    idFormatoFileStandard);
	}
	if (idFormatoFileStandard == BigDecimal.ZERO && nmFormatoFileStandard != null) {
	    formatoFileStandard = helper.getDecFormatoFileStandardByName(nmFormatoFileStandard);
	}

	if (formatoFileStandard != null) {
	    try {
		formatoFileStandardRowBean = (DecFormatoFileStandardRowBean) Transform
			.entity2RowBean(formatoFileStandard);
		formatoFileStandardRowBean.setObject(
			FormatiForm.FormatoFileStandard.ni_punteggio_totale,
			helper.calcolaValutazione(formatoFileStandard));
	    } catch (Exception e) {
		logger.error(e.getMessage());
		throw new IllegalStateException(
			"Errore inatteso nel recupero del formato file standard");
	    }
	}

	return formatoFileStandardRowBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardTableBean(
	    DecFormatoFileStandardRowBean fRowBean) {
	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<DecFormatoFileStandard> list = helper.retrieveDecFormatoFileStandardList(
		fRowBean != null ? fRowBean.getNmFormatoFileStandard() : null,
		fRowBean != null ? fRowBean.getNmMimetypeFile() : null);
	try {
	    if (!list.isEmpty()) {
		for (DecFormatoFileStandard entity : list) {
		    DecFormatoFileStandardRowBean row = new DecFormatoFileStandardRowBean();
		    row.entityToRowBean(entity);
		    List<DecEstensioneFile> estensioneFileList = helper
			    .retrieveDecEstensioneFileList(row.getIdFormatoFileStandard());
		    String cdEstensioneFile = "";
		    for (DecEstensioneFile estensioneFile : estensioneFileList) {
			cdEstensioneFile = cdEstensioneFile + estensioneFile.getCdEstensioneFile()
				+ ";";
		    }
		    row.setString("cd_estensione_file", cdEstensioneFile);
		    row.setObject(FormatiForm.FormatoFileStandardList.ni_punteggio_totale,
			    helper.calcolaValutazione(entity));
		    formatoTableBean.add(row);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}

	return formatoTableBean;
    }

    public DecFormatoFileStandardTableBean getDecFormatoFileStandardExcel() {
	DecFormatoFileStandardTableBean formatoTableBean = new DecFormatoFileStandardTableBean();
	List<DecFormatoFileStandard> list = helper.retrieveDecFormatoFileStandardList(null, null);
	try {
	    if (!list.isEmpty()) {
		for (DecFormatoFileStandard entity : list) {
		    DecFormatoFileStandardRowBean row = new DecFormatoFileStandardRowBean();
		    row.entityToRowBean(entity);
		    List<DecEstensioneFile> estensioneFileList = helper
			    .retrieveDecEstensioneFileList(row.getIdFormatoFileStandard());
		    String cdEstensioneFile = "";
		    for (DecEstensioneFile estensioneFile : estensioneFileList) {
			cdEstensioneFile = new StringBuilder(cdEstensioneFile)
				.append(estensioneFile.getCdEstensioneFile()).append(";")
				.toString();
		    }
		    row.setString("cd_estensione_file", cdEstensioneFile);
		    row.setObject(FormatiForm.FormatoFileStandardList.ni_punteggio_totale,
			    helper.calcolaValutazione(entity));
		    formatoTableBean.add(row);
		}
	    }

	    formatoTableBean.forEach(r -> {
		BigDecimal idFormato = r.getIdFormatoFileStandard();

		java.util.List<DecFormatoValutazione> valutazioneFormato = getFormatoValutazioneList(
			idFormato);
		for (DecFormatoValutazione row : valutazioneFormato) {
		    Long id = getIdFormatoGruppoProprieta(
			    row.getDecFormatoProprieta().getIdFormatoProprieta());

		    String nmField = null;
		    switch (id.intValue()) {
		    case 1:
			nmField = "natura";
			break;
		    case 2:
			nmField = "apertura";
			break;
		    case 3:
			nmField = "proprietario";
			break;
		    case 4:
			nmField = "estendibilita";
			break;
		    case 5:
			nmField = "livello_modello_metadati";
			break;
		    case 6:
			nmField = "robustezza";
			break;
		    case 7:
			nmField = "dipendenza_dal_dispositivo";
			break;
		    case 8:
			nmField = "compatibilita";
			break;
		    case 9:
			nmField = "contenuto";
			break;
		    default:
			break;
		    }

		    r.setBigDecimal(nmField, row.getNiPunteggio());
		}

	    });
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}

	return formatoTableBean;
    }

    public DecEstensioneFileTableBean getDecEstensioneFileTableBean(
	    DecEstensioneFileRowBean estensioneFileRowBean) {
	DecEstensioneFileTableBean estensioneFileTableBean = new DecEstensioneFileTableBean();
	List<DecEstensioneFile> list = helper
		.retrieveDecEstensioneFileList(estensioneFileRowBean.getIdFormatoFileStandard());
	try {
	    if (!list.isEmpty()) {
		estensioneFileTableBean = (DecEstensioneFileTableBean) Transform
			.entities2TableBean(list);
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}

	return estensioneFileTableBean;
    }

    public DecFormatoFileBustaRowBean getDecFormatoFileBustaRowBean(BigDecimal idFormatoFileBusta) {
	DecFormatoFileBustaRowBean formatoFileBustaRowBean = null;
	DecFormatoFileBusta formatoFileBusta = helper.findById(DecFormatoFileBusta.class,
		idFormatoFileBusta);
	if (formatoFileBusta != null) {
	    try {
		formatoFileBustaRowBean = (DecFormatoFileBustaRowBean) Transform
			.entity2RowBean(formatoFileBusta);
		String ds = (String) helper
			.getDsFormatoFileBusta(formatoFileBusta.getTiFormatoFirmaMarca());
		if (ds != null) {
		    formatoFileBustaRowBean.setString("ds_formato_firma_marca", ds);
		}
	    } catch (Exception e) {
		logger.error(e.getMessage());
	    }
	}
	return formatoFileBustaRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecFormatoFileStandard(DecFormatoFileStandardRowBean fRowBean) {
	/*
	 * Salvo il nuovo formatoFileStandard nella tabella che censisce tutti i formati del
	 * sistema, ovvero DEC_FORMATO_FILE_STANDARD
	 */
	DecFormatoFileStandard formatoFileStandard = (DecFormatoFileStandard) Transform
		.rowBean2Entity(fRowBean);
	helper.insertEntity(formatoFileStandard, true);
	fRowBean.setIdFormatoFileStandard(
		new BigDecimal(formatoFileStandard.getIdFormatoFileStandard()));
	String nmFormatoFileStandard = formatoFileStandard.getNmFormatoFileStandard();

	/*
	 * Salvo il formatoFileStandard per ogni struttura per ogni tipo componente a seconda del
	 * TIPO ESITO CONTROLLO del formatoFileStandard
	 */

	/* CASO FORMATO NON CONCATENABILE */
	if (formatoFileStandard.getFlFormatoConcat().equals("0")) {
	    // struttureList.forEach(struttura -> {
	    logger.debug("Inserisco il formato NON concatenabile " + nmFormatoFileStandard
		    + " per tutte le strutture");
	    salvaFormatoFileDocNative(formatoFileStandard.getIdFormatoFileStandard(),
		    formatoFileStandard.getNmFormatoFileStandard());

	    String tiEsitoContrFormato = formatoFileStandard.getTiEsitoContrFormato();
	    logger.debug("Inserisco i formati ammessi singoli sui tipi componente con flag "
		    + tiEsitoContrFormato + " uguale a true");
	    salvaFormatiAmmessiSingoloNative(getCampoFlagTipoComponente(tiEsitoContrFormato),
		    formatoFileStandard.getNmFormatoFileStandard());

	} else {
	    /* CASO FORMATO CONCATENABILE */
	    // Per ogni struttura oltre ad inserire il DEC_FORMATO_FILE_STANDARD e il singolo
	    // DEC_FORMATO_FILE_DOC,
	    // devo inserire tutte le concatenazioni sempre in DEC_FORMATO_FILE_DOC e
	    // DEC_USO_FORMATO_FILE_STANDARD
	    logger.debug("Inserisco il formato concatenabile " + nmFormatoFileStandard
		    + " per tutte le strutture");
	    salvaFormatoFileDocConcatenabileNative(formatoFileStandard.getIdFormatoFileStandard(),
		    formatoFileStandard.getNmFormatoFileStandard());
	    //
	    logger.debug("Inserisco il formato concatenabile " + nmFormatoFileStandard
		    + " per tutte le strutture concatenandolo con ogni altro formato tranne se stesso");
	    salvaFormatoConcatenabileNative(formatoFileStandard.getIdFormatoFileStandard(),
		    formatoFileStandard.getNmFormatoFileStandard());

	    String tiEsitoContrFormato = formatoFileStandard.getTiEsitoContrFormato();
	    logger.debug("Inserisco i formati ammessi sui tipi componente con flag "
		    + tiEsitoContrFormato + " uguale a true");

	    salvaFormatiAmmessiSingoloEConcatenatiNative(
		    getCampoFlagTipoComponente(tiEsitoContrFormato),
		    formatoFileStandard.getNmFormatoFileStandard());
	}

	// List<BigDecimal> idFormatoFileDocList = struttureHelper
	// .getIdFormatoFileDocList(formatoFileStandard.getIdFormatoFileStandard());
	// Set<BigDecimal> idFormatoFileDocSet = new HashSet<>(idFormatoFileDocList);
	// for (Long idFormatoFileDoc : idFormatoFileDocList) {
	// idFormatoFileDocSet.add(BigDecimal.valueOf(idFormatoFileDoc));
	// }
	// sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
	// param.getNomeUtente(),
	// param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO,
	// idFormatoFileDocSet,
	// param.getNomePagina());
    }

    private String getCampoFlagTipoComponente(String tiEsitoContrFormato) {
	String tipoFlag = "";
	switch (tiEsitoContrFormato) {
	case GESTITO:
	    tipoFlag = "fl_gestiti";
	    break;
	case IDONEO:
	    tipoFlag = "fl_idonei";
	    break;
	case DEPRECATO:
	    tipoFlag = "fl_deprecati";
	    break;
	default:
	    break;
	}
	return tipoFlag;
    }

    /**
     * Inserisce in DEC_FORMATO_FILE_DOC e in DEC_USO_FORMATO_FILE_STANDARD il nuovo singolo formato
     * NON CONCATENABILE per mantenere allineato il formato STANDARD a livello di ogni struttura
     *
     * @param idFormatoFileStandard l'id del nuovo formato inserito
     * @param nmFormatoFileStandard il nome del nuovo formato inserito
     *
     */
    public void salvaFormatoFileDocNative(long idFormatoFileStandard,
	    String nmFormatoFileStandard) {
	/* Inserisco in DEC_FORMATO_FILE_DOC e DEC_USO_FORMATO_FILE_STANDARD */
	formatoFileDocHelper.insertDecFormatoFileDocSingoloNative(idFormatoFileStandard,
		nmFormatoFileStandard);
    }

    public void salvaFormatoFileDocConcatenabileNative(long idFormatoFileStandard,
	    String nmFormatoFileStandard) {
	/* Inserisco in DEC_FORMATO_FILE_DOC e DEC_USO_FORMATO_FILE_STANDARD */
	formatoFileDocHelper.insertDecFormatoFileDocSingoloConcatenabileNative(
		idFormatoFileStandard, nmFormatoFileStandard);
    }

    public void salvaFormatoConcatenabileNative(long idFormatoFileStandard,
	    String nmFormatoFileStandard) {
	/* Inserisco in DEC_FORMATO_FILE_DOC e DEC_USO_FORMATO_FILE_STANDARD */
	formatoFileDocHelper.insertDecFormatoFileDocConcatenatiNative(idFormatoFileStandard,
		nmFormatoFileStandard);
    }

    public void salvaFormatiAmmessiSingoloNative(String tipoFlag, String nmFormatoFileStandard) {
	// Salvo il formato ammesso singolo
	formatoFileDocHelper.insertDecFormatoFileAmmessoSingoloNative(tipoFlag,
		nmFormatoFileStandard);
	// Salvo i formati ammessi concatenati al singolo
	formatoFileDocHelper.insertDecFormatoFileAmmessoConcatenatiFlagSpuntatoNative(tipoFlag,
		nmFormatoFileStandard + ".%");
    }

    public void salvaFormatiAmmessiSoloConcatenatiNative(String tipoFlag,
	    String nmFormatoFileStandard) {
	formatoFileDocHelper.insertDecFormatoFileAmmessoConcatenatiNative(tipoFlag,
		"%." + nmFormatoFileStandard);
    }

    public void salvaFormatiAmmessiSingoloEConcatenatiNative(String tipoFlag,
	    String nmFormatoFileStandard) {
	formatoFileDocHelper.insertDecFormatoFileAmmessoSingoloEConcatenatiNative(tipoFlag,
		nmFormatoFileStandard);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long insertDecEstensioneFile(DecEstensioneFileRowBean estensioneFileRowBean)
	    throws ParerUserError {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		estensioneFileRowBean.getIdFormatoFileStandard());
	if (formatoFileStandard.getDecEstensioneFiles() == null) {
	    formatoFileStandard.setDecEstensioneFiles(new ArrayList<>());
	}

	if (helper.getDecEstensioneFileByName(estensioneFileRowBean.getCdEstensioneFile(),
		null) != null) {
	    throw new ParerUserError("Estensione gi\u00E0 associata al formato specificato");
	}

	DecEstensioneFile estensioneFile = (DecEstensioneFile) Transform
		.rowBean2Entity(estensioneFileRowBean);
	estensioneFile.setDecFormatoFileStandard(formatoFileStandard);

	helper.insertEntity(estensioneFile, true);
	formatoFileStandard.getDecEstensioneFiles().add(estensioneFile);
	return estensioneFile.getIdEstensioneFile();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecEstensioneFile(BigDecimal idFormatoFileStandard,
	    BigDecimal idEstensioneFile, String cdEstensioneFileOld, String cdEstensioneFile)
	    throws ParerUserError {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		idFormatoFileStandard);
	if (formatoFileStandard.getDecEstensioneFiles() == null) {
	    formatoFileStandard.setDecEstensioneFiles(new ArrayList<>());
	}

	DecEstensioneFile estensioneFileDB = helper.findById(DecEstensioneFile.class,
		idEstensioneFile);

	DecEstensioneFile estensioneFileNewDB = helper.getDecEstensioneFileByName(cdEstensioneFile,
		null);

	if (estensioneFileDB != null && estensioneFileNewDB != null && estensioneFileDB
		.getIdEstensioneFile() != estensioneFileNewDB.getIdEstensioneFile()) {
	    throw new ParerUserError("Estensione gi\u00E0 associata ad un formato");
	}

	estensioneFileDB.setCdEstensioneFile(cdEstensioneFile);

	formatoFileStandard.getDecEstensioneFiles().add(estensioneFileDB);
    }

    /**
     * Gestione modifica natura del formatoFileStandard Se ho modificato ti_esito_contr_formato
     * (GESTITO, IDONEO, DEPRECATO) non essendoci controlli, devo solo modificare la visualizzazione
     * del formatoFileStandard a livello di tipo componente in base ai flag spuntati nel dettaglio
     * dello stesso
     *
     * @param idFormato l'id del formato file standard da aggiornare
     * @param fRowBean  il formato file standard con le modifiche online
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //
    public void updateDecFormatoFileStandard(BigDecimal idFormato,
	    DecFormatoFileStandardRowBean fRowBean) {
	/* Salvo le modifiche al formato file standard */
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		idFormato);// idFormato

	/* Ricavo gli eventuali valori che mi possono interessare */
	String tiEsitoContrFormatoOld = formatoFileStandard.getTiEsitoContrFormato();
	String tiEsitoContrFormatoNew = fRowBean.getTiEsitoContrFormato();
	String flFormatoConcatOld = formatoFileStandard.getFlFormatoConcat();
	String flFormatoConcatNew = fRowBean.getFlFormatoConcat();

	// Aggiornamento dei campi
	formatoFileStandard.setCdVersione(fRowBean.getCdVersione());// getCdVersione
	formatoFileStandard.setDsCopyright(fRowBean.getDsCopyright());
	formatoFileStandard.setDsFormatoFileStandard(fRowBean.getDsFormatoFileStandard());

	formatoFileStandard.setNmMimetypeFile(fRowBean.getNmMimetypeFile());
	formatoFileStandard.setNmFormatoFileStandard(fRowBean.getNmFormatoFileStandard());
	formatoFileStandard.setTiEsitoContrFormato(fRowBean.getTiEsitoContrFormato());
	formatoFileStandard.setFlFormatoConcat(fRowBean.getFlFormatoConcat());
	formatoFileStandard.setNtIdoneita(fRowBean.getNtIdoneita());
	formatoFileStandard.setDtValutazioneFormato(fRowBean.getDtValutazioneFormato());

	/////////////////////////////
	// TI ESITO CONTR FORMATO //
	/////////////////////////////
	/* Se ho MODIFICATO il TIPO ESITO CONTROLLO FORMATO del formatoFileStandard stesso */
	if (!tiEsitoContrFormatoOld.equals(tiEsitoContrFormatoNew)) { //
	    String tipoFlagOld = getCampoFlagTipoComponente(tiEsitoContrFormatoOld);
	    String tipoFlagNew = getCampoFlagTipoComponente(tiEsitoContrFormatoNew);
	    logger.debug("Cancello il formato ammesso "
		    + formatoFileStandard.getNmFormatoFileStandard()
		    + " come singolo e concatenato per tutti i tipi componente che non hanno il campo "
		    + tipoFlagNew + " a true");
	    formatoFileDocHelper.deleteOldTiEsitoContrFormato(tipoFlagOld, tipoFlagNew,
		    formatoFileStandard.getNmFormatoFileStandard());
	    logger.debug("Inserisco il formato ammesso "
		    + formatoFileStandard.getNmFormatoFileStandard()
		    + " come singolo e concatenato per tutti i tipi componente che hanno il campo "
		    + tipoFlagNew + " a true");
	    formatoFileDocHelper.insertNewTiEsitoContrFormato(tipoFlagOld, tipoFlagNew,
		    formatoFileStandard.getNmFormatoFileStandard());
	    //
	    //
	}
	//////////////////////////////////
	// FINE TI ESITO CONTR FORMATO //
	//////////////////////////////////
	////////////////////
	// CONCATENAZIONE //
	////////////////////
	/* Se ho modificato il flag concatenabile */
	if (!flFormatoConcatOld.equals(flFormatoConcatNew)) { //
	    /*
	     * Se ero CONCATENABILE e ora sono NON CONCATENABILE dovrò togliere le concatenazioni da
	     * DEC_FORMATO_FILE_DOC e in automatico, se ve ne sono, a cascata su
	     * DEC_FORMATO_FILE_AMMESSO
	     */
	    if (flFormatoConcatNew.equals("0")) {
		logger.debug("Cancello le concatenzioni del formato "
			+ formatoFileStandard.getNmFormatoFileStandard()
			+ " per tutte le strutture ");
		formatoFileDocHelper
			.deleteConcatenazioni(formatoFileStandard.getNmFormatoFileStandard());
	    } /*
	       * Se ero NON CONCATENABILE e ora sono CONCATENABILE dovrò aggiungere le
	       * concatenazioni in DEC_FORMATO_FILE_DOC e in automatico, se ve ne sono, a cascata in
	       * DEC_FORMATO_FILE_AMMESSO
	       */ else {//
		logger.debug("Inserisco il formato concatenabile "
			+ formatoFileStandard.getNmFormatoFileStandard()
			+ " per tutte le strutture concatenandolo con ogni altro formato tranne se stesso");
		salvaFormatoConcatenabileNative(formatoFileStandard.getIdFormatoFileStandard(),
			formatoFileStandard.getNmFormatoFileStandard());
		String tiEsitoContrFormato = formatoFileStandard.getTiEsitoContrFormato();
		logger.debug("Inserisco i formati ammessi sui tipi componente con flag "
			+ tiEsitoContrFormato + " uguale a true");
		salvaFormatiAmmessiSoloConcatenatiNative(
			getCampoFlagTipoComponente(tiEsitoContrFormato),
			formatoFileStandard.getNmFormatoFileStandard());
	    }
	}
    }

    public List<String> getFormatiConcatenatiDaInserireNelComponente(BigDecimal idTipoCompDoc,
	    String nmFormatoConcatenabile) {
	/*
	 * Recupero tutti i formati ammessi "singoli" sui quali creare le concatenazioni con
	 * formatoFileStandard "concatenabile" passato
	 */
	List<String> elencoNomiNuoviFormatiConcatenati = new ArrayList<>();
	DecFormatoFileDocTableBean formatoFileAmmessoTableBean = formatoFileDocEjb
		.getDecFormatoFileAmmessoTableBean(idTipoCompDoc);
	for (DecFormatoFileDocRowBean formatoAmmessoSulComponente : formatoFileAmmessoTableBean) {
	    /*
	     * Verifico per per ogni formatoFileStandard ammesso se è "singolo" (non già
	     * concatenato)
	     */
	    String[] nomeFormatoSplittato = formatoAmmessoSulComponente.getNmFormatoFileDoc()
		    .split("[.]");
	    List<String> nomeFormattatoSplittatoList = Arrays.asList(nomeFormatoSplittato);
	    if (nomeFormattatoSplittatoList.size() == 1) {
		elencoNomiNuoviFormatiConcatenati
			.add(nomeFormattatoSplittatoList.get(0) + "." + nmFormatoConcatenabile);
	    }
	}
	return elencoNomiNuoviFormatiConcatenati;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal insertDecFormatoFileBusta(DecFormatoFileBustaRowBean formatoFileBustaRowBean)
	    throws ParerUserError {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		formatoFileBustaRowBean.getIdFormatoFileStandard());

	if (formatoFileStandard.getDecFormatoFileBustas() == null) {
	    formatoFileStandard.setDecFormatoFileBustas(new ArrayList<>());
	}

	if (helper.getDecFormatoFileBustaByName(formatoFileBustaRowBean.getTiFormatoFirmaMarca(),
		formatoFileBustaRowBean.getIdFormatoFileStandard()) != null) {
	    throw new ParerUserError(
		    "Formato file busta gi\u00E0 associata al formato specificato");
	}

	DecFormatoFileBusta formatoFileBusta = new DecFormatoFileBusta();
	formatoFileBusta.setDecFormatoFileStandard(formatoFileStandard);
	formatoFileBusta.setTiFormatoFirmaMarca(formatoFileBustaRowBean.getTiFormatoFirmaMarca());
	helper.insertEntity(formatoFileBusta, true);
	formatoFileStandard.getDecFormatoFileBustas().add(formatoFileBusta);
	return BigDecimal.valueOf(formatoFileBusta.getIdFormatoFileBusta());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecFormatoFileBusta(BigDecimal idFormatoFileStandard,
	    BigDecimal idFormatoFileBusta, String tiFormatoOld, String tiFormato)
	    throws ParerUserError {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		idFormatoFileStandard);

	if (formatoFileStandard.getDecFormatoFileBustas() == null) {
	    formatoFileStandard.setDecFormatoFileBustas(new ArrayList<>());
	}

	DecFormatoFileBusta formatoFileBustaDB = helper.getDecFormatoFileBustaByName(tiFormato,
		idFormatoFileStandard);

	if (formatoFileBustaDB != null
		&& formatoFileBustaDB.getIdFormatoFileBusta() != idFormatoFileBusta.longValue()) {
	    throw new ParerUserError("Formato file busta gi\u00E0 associato al formato");
	}

	DecFormatoFileBusta formatoFileBustaDB2 = helper.findById(DecFormatoFileBusta.class,
		idFormatoFileBusta);
	// Aggiorno il formato
	formatoFileBustaDB2.setTiFormatoFirmaMarca(tiFormato);
    }

    public DecFormatoFileBustaTableBean getDecFormatoFileBustaTableBean(
	    BigDecimal idFormatoFileStandard) {
	DecFormatoFileBustaTableBean formatoFileBustaTableBean = new DecFormatoFileBustaTableBean();
	List<DecFormatoFileBusta> list = helper.getDecFormatoFileBustaList(idFormatoFileStandard);

	try {
	    if (!list.isEmpty()) {
		for (DecFormatoFileBusta busta : list) {
		    DecFormatoFileBustaRowBean rb = new DecFormatoFileBustaRowBean();
		    rb = (DecFormatoFileBustaRowBean) Transform.entity2RowBean(busta);
		    String ds = (String) helper.getDsFormatoFileBusta(rb.getTiFormatoFirmaMarca());
		    if (ds != null) {
			rb.setString("ds_formato_firma_marca", ds);
		    }
		    formatoFileBustaTableBean.add(rb);
		}
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage());
	    throw new IllegalStateException("Errore inatteso nel recupero dei formati file busta");
	}

	return formatoFileBustaTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecEstensioneFile(DecEstensioneFileRowBean estensioneFileRowBean) {
	DecEstensioneFile estensioneFile = helper.findById(DecEstensioneFile.class,
		estensioneFileRowBean.getIdEstensioneFile());
	helper.removeEntity(estensioneFile, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileBusta(DecFormatoFileBustaRowBean formatoFileBustaRowBean) {
	DecFormatoFileBusta formatoFileBusta = helper.findById(DecFormatoFileBusta.class,
		formatoFileBustaRowBean.getIdFormatoFileBusta());
	helper.removeEntity(formatoFileBusta, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecFormatoFileStandard(
	    DecFormatoFileStandardRowBean formatoFileStandardRowBean) throws ParerUserError {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		formatoFileStandardRowBean.getIdFormatoFileStandard());

	if (!formatoFileStandard.getAroCompDocs().isEmpty()) {
	    throw new ParerUserError(
		    "Impossibile eliminare il formato file standard: esiste almeno un componente versato con questo formato");
	}
	if (!formatoFileStandard.getAroBustaCrittogs().isEmpty()) {
	    throw new ParerUserError(
		    "Impossibile eliminare il formato file standard: esiste almeno una busta crittografata versata con questo formato");
	}
	if (!formatoFileStandard.getDecEstensioneFiles().isEmpty()) {
	    throw new ParerUserError(
		    "Impossibile eliminare il formato file standard: esiste almeno un'estensione associata al formato");
	}
	if (!formatoFileStandard.getDecFormatoFileBustas().isEmpty()) {
	    throw new ParerUserError(
		    "Impossibile eliminare il formato file standard: esiste almeno una busta associata al formato");
	}
	boolean isAmmesso = formatoFileDocEjb
		.isFormatoAmmesso(formatoFileStandard.getNmFormatoFileStandard());
	if (isAmmesso) {
	    throw new ParerUserError(
		    "Impossibile eliminare il formato file standard: esiste almeno un tipo componente associato al formato");
	}

	// Rimuovo i DEC_FORMATO_FILE_DOC
	formatoFileDocEjb.deleteFormatoFileDoc(formatoFileStandard.getNmFormatoFileStandard());
	// Rimuovo da DEC_FORMATO_FILE_STANDARD
	helper.removeEntity(formatoFileStandard, true);
    }

    @TransactionAttribute
    public BaseTable getValutazioneFormatiTableBean(BigDecimal idFormato) {
	Asserts.notNull(idFormato, "Impossibile ottenere le valutazioni senza un id formato");
	BaseTable baseTable = new BaseTable();
	List<DecFormatoGruppoProprieta> proprietaValutazione = helper
		.getAllDecFormatoGruppoProprieta();
	List<DecFormatoValutazione> valutazioni = helper
		.getValutazioniPerFormato(idFormato.longValue());

	for (DecFormatoGruppoProprieta g : proprietaValutazione) {
	    boolean valutazioneVuota = true;
	    for (DecFormatoValutazione v : valutazioni) {
		if (v.getDecFormatoProprieta().getDecFormatoGruppoProprieta()
			.getIdFormatoGruppoProprieta().equals(g.getIdFormatoGruppoProprieta())) {
		    valutazioneVuota = false;
		    BaseRow row = componiRigaValutazioneFormato(v);
		    baseTable.add(row);
		    break;
		}
	    }
	    if (valutazioneVuota) {
		baseTable.add(componiRigaValutazioneFormato(g));
	    }
	}

	return baseTable;
    }

    public static final String VALUTAZIONE_ID_GRP_PROP = "id_gruppo_proprieta";
    public static final String VALUTAZIONE_ID = "id_valutazione";
    public static final String VALUTAZIONE_NM_PROPRIETA = "nm_proprieta";

    private BaseRow componiRigaValutazioneFormato(DecFormatoGruppoProprieta gruppoProprieta) {
	BaseRow row = new BaseRow();
	row.setObject(VALUTAZIONE_ID_GRP_PROP, gruppoProprieta.getIdFormatoGruppoProprieta());
	row.setObject(VALUTAZIONE_ID, null);
	row.setObject(FormatiForm.ParametroValutazione.id_proprieta, null);
	row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta,
		gruppoProprieta.getNmFormatoGruppoProprieta());
	row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_proprieta,
		" - non impostato -");
	row.setBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio, null);
	return row;
    }

    private BaseRow componiRigaValutazioneFormato(DecFormatoValutazione valutazione) {
	BaseRow row = new BaseRow();
	row.setObject(VALUTAZIONE_ID_GRP_PROP, valutazione.getDecFormatoProprieta()
		.getDecFormatoGruppoProprieta().getIdFormatoGruppoProprieta());
	row.setObject(VALUTAZIONE_ID, valutazione.getIdFormatoValutazione());
	row.setObject(FormatiForm.ParametroValutazione.id_proprieta,
		valutazione.getDecFormatoProprieta().getIdFormatoProprieta());
	row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta,
		valutazione.getDecFormatoProprieta().getDecFormatoGruppoProprieta()
			.getNmFormatoGruppoProprieta());
	row.setString(FormatiForm.FormatoFileParametriValutazioneList.nm_formato_proprieta,
		valutazione.getDecFormatoProprieta().getNmFormatoProprieta());
	row.setBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio,
		valutazione.getNiPunteggio());
	row.setString(FormatiForm.FormatoFileParametriValutazioneList.nt_punteggio_valutazione,
		valutazione.getNtPunteggioValutazione());
	row.setBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.flg_interoperabilita,
		new BigDecimal(valutazione.getFlgInteroperabilita()));
	return row;
    }

    public BaseRow getParametroValutazioneRowBean(BaseRow parametroValutazioneCorrente) {
	BaseRow row = new BaseRow();
	row.setString(FormatiForm.ParametroValutazione.nm_formato_gruppo_proprieta,
		parametroValutazioneCorrente.getString(
			FormatiForm.FormatoFileParametriValutazioneList.nm_formato_gruppo_proprieta));
	row.setBigDecimal(FormatiForm.ParametroValutazione.ni_punteggio,
		parametroValutazioneCorrente.getBigDecimal(
			FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio));
	row.setString(FormatiForm.ParametroValutazione.nt_punteggio_valutazione,
		parametroValutazioneCorrente.getString(
			FormatiForm.FormatoFileParametriValutazioneList.nt_punteggio_valutazione));
	return row;
    }

    public BaseTable getProprietaPerGruppo(Long idGruppoProprieta) {
	List<DecFormatoProprieta> proprieta = helper
		.getDecFormatoProprietaByGruppo(idGruppoProprieta);
	BaseTable baseTable = new BaseTable();
	for (DecFormatoProprieta p : proprieta) {
	    BaseRow baseRow = new BaseRow();
	    baseRow.setBigDecimal(FormatiForm.ParametroValutazione.id_proprieta,
		    BigDecimal.valueOf(p.getIdFormatoProprieta()));
	    baseRow.setString(VALUTAZIONE_NM_PROPRIETA, p.getNmFormatoProprieta());
	    baseTable.add(baseRow);
	}
	return baseTable;
    }

    public DecFormatoValutazione updateDecFormatoValutazione(Long idValutazione, Long idProprieta,
	    BigDecimal punteggio, String ntPunteggioValutazione) {
	DecFormatoValutazione decFormatoValutazione = helper.findById(DecFormatoValutazione.class,
		idValutazione);
	Asserts.notNull(decFormatoValutazione, "Impossibile trovare un "
		+ DecFormatoValutazione.class.getSimpleName() + " con id " + idValutazione);
	DecFormatoProprieta decFormatoProprieta = helper.findById(DecFormatoProprieta.class,
		idProprieta);

	Asserts.notNull(decFormatoProprieta, "Impossibile trovare un "
		+ DecFormatoProprieta.class.getSimpleName() + " con id " + idProprieta);

	decFormatoValutazione.setNtPunteggioValutazione(ntPunteggioValutazione);
	decFormatoValutazione.setNiPunteggio(punteggio);
	decFormatoValutazione.setDecFormatoProprieta(decFormatoProprieta);
	// MEV#26039 - Miglioramenti della Valutazione idoneità formati alla conservazione
	decFormatoValutazione.setFlgInteroperabilita(0);
	return helper.mergeEntity(decFormatoValutazione);
    }

    public void insertDecFormatoValutazione(BigDecimal idFormatoFileStandard,
	    Long idFormatoProprieta, BigDecimal punteggio, String ntPunteggioValutazione) {
	Asserts.notNull(idFormatoFileStandard,
		"idFormatoFileStandard obbligatorio per inserire un nuovo DecFormatoValutazione");
	Asserts.notNull(idFormatoProprieta,
		"idFormatoProprieta obbligatorio per inserire un nuovo DecFormatoValutazione");
	Asserts.notNull(punteggio,
		"punteggio obbligatorio per inserire un nuovo DecFormatoValutazione");

	DecFormatoFileStandard formato = helper.findById(DecFormatoFileStandard.class,
		idFormatoFileStandard.longValue());
	Asserts.notNull(formato,
		"Non esiste nessun DecFormatoFileStandard con id " + idFormatoFileStandard);

	DecFormatoProprieta proprieta = helper.findById(DecFormatoProprieta.class,
		idFormatoProprieta);
	Asserts.notNull(proprieta,
		"Non esiste nessuna DecFormatoProprieta con id " + idFormatoProprieta);

	DecFormatoValutazione valutazione = new DecFormatoValutazione();
	valutazione.setDecFormatoFileStandard(formato);
	valutazione.setDecFormatoProprieta(proprieta);
	valutazione.setNiPunteggio(punteggio);
	valutazione.setNtPunteggioValutazione(ntPunteggioValutazione);
	// MEV#26039 - Miglioramenti della Valutazione idoneità formati alla conservazione
	valutazione.setFlgInteroperabilita(0);
	helper.insertEntity(valutazione, false);

    }

    public void deleteDecFormatoValutazione(Long idFormatoValutazione) {
	Asserts.notNull(idFormatoValutazione,
		"idFormatoValutazione obbligatorio per cancellare DecFormatoValutazione");
	DecFormatoValutazione daCancellare = helper.findById(DecFormatoValutazione.class,
		idFormatoValutazione);
	Asserts.notNull(daCancellare,
		"Non c'è nessun DecFormatoValutazione con id  " + idFormatoValutazione);
	helper.removeEntity(daCancellare, false);
    }

    public BigDecimal getPunteggioDefault(Long idFormatoProprieta) {
	Asserts.notNull(idFormatoProprieta,
		"idFormatoProprieta obbligatorio per recuperare la DecFormatoProprieta");
	DecFormatoProprieta proprieta = helper.findById(DecFormatoProprieta.class,
		idFormatoProprieta);
	Asserts.notNull(proprieta,
		"Non esiste nessuna DecFormatoProprieta con id " + idFormatoProprieta);
	return proprieta.getNiPunteggioDefault() == null ? null
		: BigDecimal.valueOf(proprieta.getNiPunteggioDefault());
    }

    public BigDecimal calcolaPunteggioInteroperabilita(BigDecimal idFormatoFileStandard) {
	Asserts.notNull(idFormatoFileStandard,
		"idFormatoFileStandard obbligatorio per poter calcolare il punteggio di interoperabilita");
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		idFormatoFileStandard.longValue());
	Asserts.notNull(formatoFileStandard,
		"Non esiste nessun DecFormatoFileStandard con id " + idFormatoFileStandard);
	return helper.calcolaValutazione(formatoFileStandard);
    }

    public BaseTable getMimetypeTableBean() {
	BaseTable tabella = new BaseTable();
	List<String> mimetypeList = helper.getMimetypeList();

	for (String mimetype : mimetypeList) {
	    BaseRow riga = new BaseRow();
	    riga.setString("nm_mimetype_file", mimetype);
	    tabella.add(riga);
	}

	return tabella;

    }

    public BaseTable getFormatoFileBustaTableBean() {
	BaseTable tabella = new BaseTable();
	List<Object[]> formatoFileBustaList = helper.getFormatoFileBustaList();

	for (Object[] formatoFileBusta : formatoFileBustaList) {
	    BaseRow riga = new BaseRow();
	    riga.setString("ti_formato_firma_marca", (String) formatoFileBusta[0]);
	    riga.setString("ds_formato_firma_marca", (String) formatoFileBusta[1]);
	    tabella.add(riga);
	}
	return tabella;
    }

    public String getDsFormatoFileBusta(String tiFormato) {
	Object dsFormato = helper.getDsFormatoFileBusta(tiFormato);
	if (dsFormato != null) {
	    return (String) dsFormato;
	} else
	    return null;
    }

    @TransactionAttribute
    public List<DecFormatoValutazione> getFormatoValutazioneList(BigDecimal idFormato) {
	DecFormatoFileStandard formatoFileStandard = helper.findById(DecFormatoFileStandard.class,
		idFormato);
	return helper.getValutazioniPerFormato(formatoFileStandard);
    }

    @TransactionAttribute
    public Long getIdFormatoGruppoProprieta(Long idFormatoProprieta) {
	DecFormatoProprieta p = helper.findById(DecFormatoProprieta.class, idFormatoProprieta);
	return p.getDecFormatoGruppoProprieta().getIdFormatoGruppoProprieta();
    }
}
