package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.aipFascicoli.xml.usprofascResp.Fascicolo;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceMetaFascicoliHelper;
import it.eng.parer.job.indiceAipFascicoli.utils.CreazioneIndiceMetaFascicoliUtil;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
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
import java.util.Date;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.xml.sax.SAXException;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceMetaFascicoli")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceMetaFascicoli {

    @EJB
    private CreazioneIndiceMetaFascicoliHelper cimfHelper;
    @EJB
    private XmlContextCache xmlContextCache;

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceMetaFascicoli.class);

    public void creaMetaVerFascicolo(Long idVerAipFascicolo, String codiceVersione, String sistemaConservazione)
            throws ParerInternalError, Exception {
        FasVerAipFascicolo verAipFascicolo = cimfHelper.findById(FasVerAipFascicolo.class, idVerAipFascicolo);
        FasFascicolo fascicolo = cimfHelper.findById(FasFascicolo.class,
                verAipFascicolo.getFasFascicolo().getIdFascicolo());
        long idAmbiente = verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();

        /*
         * Determino il modello xsd per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene, con il
         * tipo "FASCICOLO"
         */
        List<DecModelloXsdFascicolo> modelloAttivoList = cimfHelper.retrieveIdModelliFascicoloDaElaborare(idAmbiente);
        log.info("Creazione Indice AIP Fascicoli - ambiente id " + idAmbiente + ": trovati " + modelloAttivoList.size()
                + " modelli xsd attivi di tipo FASCICOLO da processare");

        /* Se per l'ambiente il modello XSD non viene trovato */
        if (modelloAttivoList.isEmpty()) {
            throw new ParerInternalError("Il modello di tipo FASCICOLO per la data corrente e l'ambiente "
                    + verAipFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente()
                    + " non è definito");
        }

        // TIP: qui mi aspetto sempre un modello e uno soltanto!!!
        for (DecModelloXsdFascicolo modello : modelloAttivoList) {
            manageIndex(verAipFascicolo.getIdVerAipFascicolo(), fascicolo, modello, codiceVersione,
                    sistemaConservazione);
        }
    }

    public void manageIndex(long idVerAipFascicolo, FasFascicolo fascicolo, DecModelloXsdFascicolo modello,
            String codiceVersione, String sistemaConservazione) throws Exception {
        log.info("Creazione Indice AIP Fascicoli - Inizio creazione XML fascicolo per la versione fascicolo "
                + idVerAipFascicolo);

        CSVersatore versatore = new CSVersatore();
        versatore.setSistemaConservazione(sistemaConservazione);
        versatore.setAmbiente(fascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(fascicolo.getOrgStrut().getOrgEnte().getNmEnte());
        versatore.setStruttura(fascicolo.getOrgStrut().getNmStrut());

        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(fascicolo.getAaFascicolo().intValue());
        chiaveFasc.setNumero(fascicolo.getCdKeyFascicolo());

        // Costruisco l'xml
        String indexFile = buildIndexFile(fascicolo, modello.getCdXsd());

        // Eseguo la validazione dell'xml prodotto con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO
        try {
            String xsd = modello.getBlXsd();
            XmlUtils.validateXml(xsd, indexFile);
            log.info("Documento validato con successo");
        } catch (SAXException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
        }

        // Calcolo l'hash SHA-256 del file .xml ed hexBinary
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile, TipiHash.SHA_256).toHexBinary();

        /*
         * Registro un record nella tabella FAS_META_VER_AIP_FASCICOLO definendo l'hash dell'indice, l'algoritmo usato
         * per il calcolo hash (=SHA-256) e l'encoding del hash (=hexBinary)
         */
        FasMetaVerAipFascicolo metaVerAipFascicolo = cimfHelper.registraFasMetaVerAipFascicolo(idVerAipFascicolo,
                hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(), codiceVersione,
                versatore, chiaveFasc);

        // Eseguo il salvataggio del clob del file nella tabella FAS_FILE_META_VER_AIP_FASC
        cimfHelper.registraFasFileMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(), indexFile,
                fascicolo.getOrgStrut(), new Date());

        /*
         * Inserisco un record nella tabella FAS_XSD_META_VER_AIP_FASC indicando il riferimento al modello di XSD
         * utilizzato per la costruzione del file Fascicolo.xml, assegnando come nm_xsd = FascicoloXSD-<versione> (dove
         * versione è il valore del codice versione (cd_xsd) del record recuperato per la costruzione dell’xml)
         */
        String nmXsd = "FascicoloXSD-" + modello.getCdXsd();
        cimfHelper.registraFasXsdMetaVerAipFasc(metaVerAipFascicolo.getIdMetaVerAipFascicolo(),
                modello.getIdModelloXsdFascicolo(), nmXsd);
    }

    public String buildIndexFile(FasFascicolo fascicolo, String cdXsd) throws Exception {
        log.info("Creazione Indice AIP Fascicoli - Creazione XML fascicolo id '" + fascicolo.getIdFascicolo()
                + "' appartenente all'ambiente id '"
                + fascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente() + "'");

        CreazioneIndiceMetaFascicoliUtil indiceMetaFascicoliUtil = new CreazioneIndiceMetaFascicoliUtil();

        FasVVisFascicolo creaMeta = cimfHelper.getFasVVisFascicolo(fascicolo.getIdFascicolo());

        Fascicolo indiceMetaFascicolo = indiceMetaFascicoliUtil.generaIndiceMetaFascicolo(creaMeta, cdXsd);

        StringWriter tmpWriter = marshallFascicolo(indiceMetaFascicolo);

        return tmpWriter.toString();
    }

    private StringWriter marshallFascicolo(Fascicolo indiceMetaFascicolo) throws JAXBException {
        /* Eseguo il marshalling degli oggetti creati in Fascicolo per salvarli */
        StringWriter tmpWriter = new StringWriter();
        Marshaller jaxbMarshaller = xmlContextCache.getCreaFascicoloCtx().createMarshaller();
        jaxbMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascProfSchema());
        jaxbMarshaller.marshal(indiceMetaFascicolo, tmpWriter);
        tmpWriter.flush();

        return tmpWriter;
    }

}
