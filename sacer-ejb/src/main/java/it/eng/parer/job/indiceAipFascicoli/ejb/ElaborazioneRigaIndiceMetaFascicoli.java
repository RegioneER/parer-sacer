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

package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceMetaFascicoliHelper;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceMetaFascicoliUtil;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceMetaFascicoliUtilV2;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.xml.utils.XmlUtils;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.xml.sax.SAXException;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceMetaFascicoli")
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceMetaFascicoli {

    @EJB
    private CreazioneIndiceMetaFascicoliHelper cimfHelper;
    @EJB
    private XmlContextCache xmlContextCache;

    // MEV#30398
    @EJB
    private ObjectStorageService objectStorageService;
    Map<String, String> indiciAipFascicoliBlob = new HashMap<>();
    // end MEV#30398

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceMetaFascicoli.class);

    // MEV#29589
    /*
     * Determino la modalità per effettuare la generazione dell'indice aip (default: FALSE)
     */
    private static final Boolean STRICT_MODE = Boolean.FALSE;
    /*
     * Determino la versione Unisincro di riferimento per la quale effettuare la generazione
     * dell'indice aip (default: v2.0)
     */
    private static final String UNISINCRO_V2_REF = "2.0";
    // end MEV#29589

    public void creaMetaVerFascicolo(Long idVerAipFascicolo, String codiceVersioneMetadati,
	    String sistemaConservazione, String cdVersioneXml,
	    BackendStorage backendIndiciAipFascicoli, Map<String, String> indiciAipFascicoliBlob)
	    throws ParerInternalError, Exception {

	String desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF;

	FasVerAipFascicolo verAipFascicolo = cimfHelper.findById(FasVerAipFascicolo.class,
		idVerAipFascicolo);
	FasFascicolo fascicolo = cimfHelper.findById(FasFascicolo.class,
		verAipFascicolo.getFasFascicolo().getIdFascicolo());
	long idAmbiente = verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte()
		.getOrgAmbiente().getIdAmbiente();

	/*
	 * Determino il modello xsd di tipo FASCICOLO attivo per l'ambiente di appartenenza della
	 * struttura a cui il fascicolo appartiene e per la versione del modello xsd corrispondente
	 * a quella del servizio di versamento fascicolo
	 */
	List<DecModelloXsdFascicolo> modelloAttivoList = cimfHelper
		.retrieveIdModelliFascicoloDaElaborareV2(idAmbiente, cdVersioneXml);
	log.info(
		"{} - ambiente id {}: trovati {} modelli xsd attivi di tipo FASCICOLO da processare",
		desJobMessage, idAmbiente, modelloAttivoList.size());

	// TIP: qui mi aspetto sempre un modello e uno soltanto!!!
	for (DecModelloXsdFascicolo modello : modelloAttivoList) {
	    manageIndex(verAipFascicolo.getIdVerAipFascicolo(), fascicolo, modello,
		    codiceVersioneMetadati, sistemaConservazione, cdVersioneXml,
		    backendIndiciAipFascicoli, indiciAipFascicoliBlob);
	}
    }

    public void manageIndex(long idVerAipFascicolo, FasFascicolo fascicolo,
	    DecModelloXsdFascicolo modello, String codiceVersioneMetadati,
	    String sistemaConservazione, String cdVersioneXml,
	    BackendStorage backendIndiciAipFascicoli, Map<String, String> indiciAipFascicoliBlob)
	    throws Exception {

	String desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF;

	log.info("{} - Inizio creazione XML fascicolo per la versione fascicolo {}", desJobMessage,
		idVerAipFascicolo);

	CSVersatore versatore = new CSVersatore();
	versatore.setSistemaConservazione(sistemaConservazione);
	versatore
		.setAmbiente(fascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
	versatore.setEnte(fascicolo.getOrgStrut().getOrgEnte().getNmEnte());
	versatore.setStruttura(fascicolo.getOrgStrut().getNmStrut());

	CSChiaveFasc chiaveFasc = new CSChiaveFasc();
	chiaveFasc.setAnno(fascicolo.getAaFascicolo().intValue());
	chiaveFasc.setNumero(fascicolo.getCdKeyFascicolo());

	// Costruisco l'xml
	String indexFile = buildIndexFile(fascicolo, cdVersioneXml);

	// Eseguo la validazione dell'xml prodotto con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO
	log.info(
		"{} - Eseguo validazione dell'xml con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO",
		desJobMessage);
	try {
	    String xsd = modello.getBlXsd();
	    XmlUtils.validateXml(xsd, indexFile);
	    log.info("{} - Documento validato con successo", desJobMessage);
	} catch (SAXException | IOException ex) {
	    log.error(ex.getMessage(), ex);
	    throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
	}

	// Calcolo l'hash SHA-256 del file .xml ed hexBinary
	String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile, TipiHash.SHA_256)
		.toHexBinary();

	/*
	 * Registro un record nella tabella FAS_META_VER_AIP_FASCICOLO definendo l'hash dell'indice,
	 * l'algoritmo usato per il calcolo hash (=SHA-256) e l'encoding del hash (=hexBinary)
	 */
	FasMetaVerAipFascicolo metaVerAipFascicolo = cimfHelper.registraFasMetaVerAipFascicolo(
		idVerAipFascicolo, hashXmlIndice, TipiHash.SHA_256.descrivi(),
		TipiEncBinari.HEX_BINARY.descrivi(), codiceVersioneMetadati, versatore, chiaveFasc);

	// MEV #30398
	// Eseguo il salvataggio del clob del file nella tabella FAS_FILE_META_VER_AIP_FASC
	cimfHelper.registraFasFileMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
		indexFile, fascicolo.getOrgStrut(), new Date(), backendIndiciAipFascicoli,
		indiciAipFascicoliBlob);
	// end MEV #30398

	/*
	 * Inserisco un record nella tabella FAS_XSD_META_VER_AIP_FASC indicando il riferimento al
	 * modello di XSD utilizzato per la costruzione del file Fascicolo.xml, assegnando come
	 * nm_xsd = MetadatiFascicolo-<Versione> (dove Versione è il valore corrispondente sia alla
	 * versione del servizio di versamento fascicolo (cdVersioneXml) che al valore del codice
	 * versione (cd_xsd) del record recuperato per la validazione.
	 */
	String nmXsd = "MetadatiFascicolo-" + cdVersioneXml;
	cimfHelper.registraFasXsdMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
		modello.getIdModelloXsdFascicolo(), nmXsd);
    }

    public String buildIndexFile(FasFascicolo fascicolo, String cdVersioneXml) throws Exception {

	String desJobMessage = "Creazione Indice AIP Fascicoli v" + UNISINCRO_V2_REF;

	log.info("{} - Creazione XML fascicolo id {} appartenente all'ambiente id {} ",
		desJobMessage, fascicolo.getIdFascicolo(),
		fascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());

	FasVVisFascicolo creaMeta = cimfHelper.getFasVVisFascicolo(fascicolo.getIdFascicolo());

	// MEV#26576
	StringWriter tmpWriter = null;
	log.debug("{} - Eseguo il marshalling del Fascicolo", desJobMessage);
	CreazioneIndiceMetaFascicoliUtilV2 indiceMetaFascicoliUtilV2 = new CreazioneIndiceMetaFascicoliUtilV2();
	it.eng.parer.aipFascicoli.xml.usprofascRespV2.Fascicolo indiceMetaFascicoloV2 = indiceMetaFascicoliUtilV2
		.generaIndiceMetaFascicoloV2(creaMeta, cdVersioneXml);

	tmpWriter = marshallFascicoloV2(indiceMetaFascicoloV2);
	// end MEV#26576

	return tmpWriter.toString();
    }

    private StringWriter marshallFascicolo(
	    it.eng.parer.aipFascicoli.xml.usprofascResp.Fascicolo indiceMetaFascicolo)
	    throws JAXBException {
	/* Eseguo il marshalling degli oggetti creati in Fascicolo per salvarli */
	StringWriter tmpWriter = new StringWriter();
	Marshaller jaxbMarshaller = xmlContextCache.getCreaFascicoloCtx().createMarshaller();
	jaxbMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascProfSchema());
	jaxbMarshaller.marshal(indiceMetaFascicolo, tmpWriter);
	tmpWriter.flush();

	return tmpWriter;
    }

    // MEV#26576
    private StringWriter marshallFascicoloV2(
	    it.eng.parer.aipFascicoli.xml.usprofascRespV2.Fascicolo indiceMetaFascicoloV2)
	    throws JAXBException {
	/* Eseguo il marshalling degli oggetti creati in Fascicolo per salvarli */
	StringWriter tmpWriter = new StringWriter();
	Marshaller jaxbMarshaller = xmlContextCache.getCreaFascicoloCtxV2().createMarshaller();
	jaxbMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascProfSchemaV2());
	jaxbMarshaller.marshal(indiceMetaFascicoloV2, tmpWriter);
	tmpWriter.flush();

	return tmpWriter;
    }
    // end MEV#26576
}
