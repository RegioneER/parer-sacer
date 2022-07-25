/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.ejb;

import it.eng.parer.aipFascicoli.xml.usprofascResp.Fascicolo;
import it.eng.parer.aipFascicoli.xml.usselfdescResp.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.elenco.xml.aip.ElencoIndiciAIP;
import it.eng.parer.elenco.xml.indice.Elencoversamento;
import it.eng.parer.titolario.xml.CreaTitolario;
import it.eng.parer.titolario.xml.ModificaTitolario;
import it.eng.parer.elencoFascicoli.xml.indice.ElencoversamentoFascicoli;
import it.eng.parer.serie.xml.indiceVolumeSerie.IndiceVolumeSerie;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.usmainResp.IdCType;
import it.eng.parer.ws.xml.usmainRespV2.PIndex;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStatoMM.IndiceMM;
import it.eng.parer.ws.xml.versRespStato.IndiceProveConservazione;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.parerxml.xsd.FileXSD;
import it.eng.parerxml.xsd.FileXSDUtil;
import it.eng.tpi.bean.EliminaCartellaArchiviataRisposta;
import it.eng.tpi.bean.RegistraCartellaRiArkRisposta;
import it.eng.tpi.bean.RetrieveFileUnitaDocRisposta;
import it.eng.tpi.bean.SchedulazioniJobTPIRisposta;
import it.eng.tpi.bean.StatoArchiviazioneCartellaRisposta;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Fioravanti_F Nota bene: la cache dei cointesti XML usata dai servizi relativi ai Fascicoli è implementata dal
 *         file XmlFascCache
 */
@Singleton
@LocalBean
@Startup
public class XmlContextCache {

    private static final Logger log = LoggerFactory.getLogger(XmlContextCache.class);

    JAXBContext versReqStatoCtx_Recupero = null;
    JAXBContext versRespStatoCtx_StatoConservazione = null;
    JAXBContext versRespStatoCtx_IndiceProveCons = null;
    JAXBContext versReqStatoMMCtx_Indice = null;
    Schema versReqStatoSchema;
    Schema versReqStatoMMSchema;

    JAXBContext richAnnVersCtx_RichiestaAnnullamentoVersamenti = null;
    JAXBContext esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti = null;
    Schema richAnnVersSchema;

    JAXBContext creazioneElencoVersamentoCtx_Elencoversamento = null;
    Schema creazioneElencoVersamentoSchema = null;

    JAXBContext creazioneIndiceVolumeSerieCtx_IndiceVolumeSerie = null;
    Schema creazioneIndiceVolumeSerieSchema = null;

    JAXBContext versRespUniSincroCtx_IdC_Ud = null;
    JAXBContext versRespUniSincroCtx_IdC_Serie = null;
    Schema versRespUniSincroSchema = null;

    // EVO#20972
    JAXBContext versRespUniSincroCtx_PIndex_Ud = null;
    Schema versRespUniSincroSchemaV2 = null;
    // end EVO#20972

    JAXBContext elencoIndiciAipCtx_ElencoIndiciAIP = null;

    JAXBContext tpiStatoArkCartellaCtx = null;
    JAXBContext tpiEliminaCartellaArkCtx = null;
    JAXBContext tpiRetrieveFileUnitaDocCtx = null;
    JAXBContext tpiRegistraCartellaRiArkCtx = null;
    JAXBContext tpiSchedulazioniJobTPICtx = null;

    JAXBContext creaTitolarioCtx = null;
    JAXBContext modificaTitolarioCtx = null;

    JAXBContext elencoversamentoFascicoliCtx = null;

    JAXBContext creaFascicoloCtx = null;
    JAXBContext selfDescMoreInfoCtx = null;
    JAXBContext creaIndiceAipFascicoloCtx = null;
    JAXBContext elencoIndiciAipFascicoliCtx = null;
    Schema aipFascProfSchema = null;
    Schema aipFascSelfDescSchema = null;
    Schema aipFascUniSincroSchema = null;

    /**
     * Questo metodo crea una cache per i JAXBContext.
     *
     */
    @PostConstruct
    protected void initSingleton() {

        try {
            log.info("Inizializzazione singleton XMLContext...");

            tpiStatoArkCartellaCtx = JAXBContext.newInstance(StatoArchiviazioneCartellaRisposta.class);
            tpiEliminaCartellaArkCtx = JAXBContext.newInstance(EliminaCartellaArchiviataRisposta.class);
            tpiRegistraCartellaRiArkCtx = JAXBContext.newInstance(RegistraCartellaRiArkRisposta.class);
            tpiRetrieveFileUnitaDocCtx = JAXBContext.newInstance(RetrieveFileUnitaDocRisposta.class);
            tpiSchedulazioniJobTPICtx = JAXBContext.newInstance(SchedulazioniJobTPIRisposta.class);

            creaTitolarioCtx = JAXBContext.newInstance(CreaTitolario.class);
            modificaTitolarioCtx = JAXBContext.newInstance(ModificaTitolario.class);

            elencoversamentoFascicoliCtx = JAXBContext.newInstance(ElencoversamentoFascicoli.class);
        } catch (JAXBException ex) {
            // log.fatal("Inizializzazione singleton XMLContext fallita! ", ex);
            log.error("Inizializzazione singleton XMLContext fallita! ", ex);
            throw new RuntimeException(ex);
        }

        try {
            versReqStatoCtx_Recupero = JAXBContext.newInstance(Recupero.class);
            versRespStatoCtx_StatoConservazione = JAXBContext.newInstance(StatoConservazione.class);
            versRespStatoCtx_IndiceProveCons = JAXBContext.newInstance(IndiceProveConservazione.class);
            versReqStatoMMCtx_Indice = JAXBContext.newInstance(IndiceMM.class);
            richAnnVersCtx_RichiestaAnnullamentoVersamenti = JAXBContext
                    .newInstance(RichiestaAnnullamentoVersamenti.class);
            esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti = JAXBContext
                    .newInstance(EsitoRichiestaAnnullamentoVersamenti.class);
            creazioneElencoVersamentoCtx_Elencoversamento = JAXBContext.newInstance(Elencoversamento.class);
            creazioneIndiceVolumeSerieCtx_IndiceVolumeSerie = JAXBContext.newInstance(IndiceVolumeSerie.class);
            versRespUniSincroCtx_IdC_Serie = JAXBContext.newInstance(IdCType.class,
                    it.eng.parer.serie.xml.serselfdescResp.ObjectFactory.class,
                    it.eng.parer.serie.xml.servdcResp.ObjectFactory.class,
                    it.eng.parer.serie.xml.serfileResp.ObjectFactory.class,
                    it.eng.parer.serie.xml.serprodResp.ObjectFactory.class);
            versRespUniSincroCtx_IdC_Ud = JAXBContext.newInstance(IdCType.class,
                    it.eng.parer.ws.xml.usselfdescResp.ObjectFactory.class,
                    it.eng.parer.ws.xml.usvdcResp.ObjectFactory.class,
                    it.eng.parer.ws.xml.usfileResp.ObjectFactory.class,
                    it.eng.parer.ws.xml.usdocResp.ObjectFactory.class);
            // EVO#20972
            versRespUniSincroCtx_PIndex_Ud = JAXBContext.newInstance(PIndex.class,
                    it.eng.parer.ws.xml.usselfdescRespV2.ObjectFactory.class,
                    it.eng.parer.ws.xml.uspvolumeRespV2.ObjectFactory.class,
                    it.eng.parer.ws.xml.usfileResp.ObjectFactory.class,
                    it.eng.parer.ws.xml.usdocResp.ObjectFactory.class);
            // end EVO#20972
            elencoIndiciAipCtx_ElencoIndiciAIP = JAXBContext.newInstance(ElencoIndiciAIP.class);

            creaFascicoloCtx = JAXBContext.newInstance(Fascicolo.class,
                    it.eng.parer.aipFascicoli.xml.usprofascResp.ObjectFactory.class);
            selfDescMoreInfoCtx = JAXBContext.newInstance(MetadatiIntegratiSelfDescriptionType.class,
                    it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory.class);
            creaIndiceAipFascicoloCtx = JAXBContext.newInstance(it.eng.parer.aipFascicoli.xml.usmainResp.IdCType.class,
                    it.eng.parer.aipFascicoli.xml.usmainResp.ObjectFactory.class,
                    it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory.class,
                    it.eng.parer.aipFascicoli.xml.usprofascResp.ObjectFactory.class);
            elencoIndiciAipFascicoliCtx = JAXBContext
                    .newInstance(it.eng.parer.aipelencoFascicoli.xml.indice.ElencoIndiciAIP.class);

            SchemaFactory schemaFctry = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            versReqStatoSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.VERS_REQ_STATO_XSD));
            versReqStatoMMSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.VERS_REQ_STATO_MM_XSD));
            richAnnVersSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.ANNULLA_VERS_REQ_XSD));
            creazioneElencoVersamentoSchema = schemaFctry
                    .newSchema(FileXSDUtil.getURLFileXSD(FileXSD.CREAZIONE_ELENCO_VERSAMENTO_XSD));
            creazioneIndiceVolumeSerieSchema = schemaFctry
                    .newSchema(FileXSDUtil.getURLFileXSD(FileXSD.INDICE_VOLUME_SERIE_XSD));
            versRespUniSincroSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.UNISINCRO_2_XSD));
            // EVO#20972
            versRespUniSincroSchemaV2 = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.UNISINCRO_2_XSD_V2));
            // end EVO#20972

            aipFascProfSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.AIP_FASC_PROF_XSD));
            aipFascSelfDescSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.AIP_FASC_SELF_DESC_XSD));
            aipFascUniSincroSchema = schemaFctry.newSchema(FileXSDUtil.getURLFileXSD(FileXSD.AIP_FASC_UNISINCRO_2_XSD));

            log.info("Inizializzazione singleton XMLContext... completata.");
        } catch (JAXBException | SAXException ex) {
            // log.fatal("Inizializzazione singleton XMLContext fallita! ", ex);
            log.error("Inizializzazione singleton XMLContext fallita! ", ex);
            throw new RuntimeException(ex);
        }
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqStatoCtx_Recupero() {
        return versReqStatoCtx_Recupero;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersRespStatoCtx_StatoConservazione() {
        return versRespStatoCtx_StatoConservazione;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersRespStatoCtx_IndiceProveCons() {
        return versRespStatoCtx_IndiceProveCons;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersReqStatoMMCtx_Indice() {
        return versReqStatoMMCtx_Indice;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersReqStato() {
        return versReqStatoSchema;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersReqStatoMM() {
        return versReqStatoMMSchema;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getRichAnnVersCtx_RichiestaAnnullamentoVersamenti() {
        return richAnnVersCtx_RichiestaAnnullamentoVersamenti;
    }

    @Lock(LockType.READ)
    public JAXBContext getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti() {
        return esitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfRichAnnVers() {
        return richAnnVersSchema;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getCreazioneElencoVersamentoCtx_Elencoversamento() {
        return creazioneElencoVersamentoCtx_Elencoversamento;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfCreazioneElencoVersamento() {
        return creazioneElencoVersamentoSchema;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getCreazioneIndiceVolumeSerieCtx_IndiceVolumeSerie() {
        return creazioneIndiceVolumeSerieCtx_IndiceVolumeSerie;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfcreazioneIndiceVolumeSerie() {
        return creazioneIndiceVolumeSerieSchema;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getVersRespUniSincroCtx_IdC_Ud() {
        return versRespUniSincroCtx_IdC_Ud;
    }

    @Lock(LockType.READ)
    public JAXBContext getVersRespUniSincroCtx_IdC_Serie() {
        return versRespUniSincroCtx_IdC_Serie;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersRespUniSincro() {
        return versRespUniSincroSchema;
    }

    //

    // EVO#20972
    @Lock(LockType.READ)
    public JAXBContext getVersRespUniSincroCtx_PIndex_Ud() {
        return versRespUniSincroCtx_PIndex_Ud;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfVersRespUniSincroV2() {
        return versRespUniSincroSchemaV2;
    }
    // end EVO#20972

    //

    @Lock(LockType.READ)
    public Schema getSchemaOfAipFascProfSchema() {
        return aipFascProfSchema;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfAipFascSelfDescSchema() {
        return aipFascSelfDescSchema;
    }

    @Lock(LockType.READ)
    public Schema getSchemaOfAipFascUniSincro() {
        return aipFascUniSincroSchema;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getElencoIndiciAipCtx_ElencoIndiciAIP() {
        return elencoIndiciAipCtx_ElencoIndiciAIP;
    }

    //

    @Lock(LockType.READ)
    public JAXBContext getCreaFascicoloCtx() {
        return creaFascicoloCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getSelfDescMoreInfoCtx() {
        return selfDescMoreInfoCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getCreaIndiceAipFascicoloCtx() {
        return creaIndiceAipFascicoloCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getElencoIndiciAipFascicoliCtx() {
        return elencoIndiciAipFascicoliCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getTpiStatoArkCartellaCtx() {
        return tpiStatoArkCartellaCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getTpiEliminaCartellaArkCtx() {
        return tpiEliminaCartellaArkCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getTpiRetrieveFileUnitaDocCtx() {
        return tpiRetrieveFileUnitaDocCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getTpiRegistraCartellaRiArkCtx() {
        return tpiRegistraCartellaRiArkCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getTpiSchedulazioniJobTPICtx() {
        return tpiSchedulazioniJobTPICtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getCreaTitolarioCtx() {
        return creaTitolarioCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getModificaTitolarioCtx() {
        return modificaTitolarioCtx;
    }

    @Lock(LockType.READ)
    public JAXBContext getElencoversamentoFascicoliCtx() {
        return elencoversamentoFascicoliCtx;
    }
}