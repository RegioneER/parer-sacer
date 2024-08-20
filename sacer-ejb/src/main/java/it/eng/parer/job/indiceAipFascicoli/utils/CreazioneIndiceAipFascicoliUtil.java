/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.job.indiceAipFascicoli.utils;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import it.eng.parer.aipFascicoli.xml.usmainResp.AgentIDType;
import it.eng.parer.aipFascicoli.xml.usmainResp.AgentNameType;
import it.eng.parer.aipFascicoli.xml.usmainResp.AgentType;
import it.eng.parer.aipFascicoli.xml.usmainResp.AttachedTimeStampType;
import it.eng.parer.aipFascicoli.xml.usmainResp.CreatingApplicationType;
import it.eng.parer.aipFascicoli.xml.usmainResp.DescriptionType;
import it.eng.parer.aipFascicoli.xml.usmainResp.EmbeddedMetadataType;
import it.eng.parer.aipFascicoli.xml.usmainResp.FileGroupType;
import it.eng.parer.aipFascicoli.xml.usmainResp.FileType;
import it.eng.parer.aipFascicoli.xml.usmainResp.HashType;
import it.eng.parer.aipFascicoli.xml.usmainResp.IdCType;
import it.eng.parer.aipFascicoli.xml.usmainResp.IdentifierType;
import it.eng.parer.aipFascicoli.xml.usmainResp.LawAndRegulationsType;
import it.eng.parer.aipFascicoli.xml.usmainResp.MoreInfoType;
import it.eng.parer.aipFascicoli.xml.usmainResp.NameAndSurnameType;
import it.eng.parer.aipFascicoli.xml.usmainResp.ProcessType;
import it.eng.parer.aipFascicoli.xml.usmainResp.SelfDescriptionType;
import it.eng.parer.aipFascicoli.xml.usmainResp.SourceIdCType;
import it.eng.parer.aipFascicoli.xml.usmainResp.TimeReferenceType;
import it.eng.parer.aipFascicoli.xml.usmainResp.VdCGroupType;
import it.eng.parer.aipFascicoli.xml.usmainResp.VdCType;
import it.eng.parer.aipFascicoli.xml.usselfdescResp.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper.TipoFascicoloHelper;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.FasContenVerAipFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasSipVerAipFascicolo;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.indiceAipFascicoli.helper.ControlliRecIndiceAipFascicoli;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versFascicoli.ejb.ControlliFascicoli;
import it.eng.parer.xml.utils.XmlUtils;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({ "unchecked" })
public class CreazioneIndiceAipFascicoliUtil {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipFascicoliUtil.class);
    private RispostaControlli rispostaControlli;
    private ControlliRecIndiceAipFascicoli controlliRecIndiceAipFascicoli;
    private XmlContextCache xmlContextCache;
    private AmbientiHelper ambientiHelper;
    private TipoFascicoloHelper tipoFascicoloHelper;
    private final String hashFunction = TipiHash.SHA_256.descrivi();

    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliFascicoli controlliFascicoli = null;

    public CreazioneIndiceAipFascicoliUtil() throws NamingException {
        rispostaControlli = new RispostaControlli();
        // Recupera l'ejb per la lettura di informazioni, se possibile
        controlliFascicoli = (ControlliFascicoli) new InitialContext().lookup("java:module/ControlliFascicoli");
        controlliRecIndiceAipFascicoli = (ControlliRecIndiceAipFascicoli) new InitialContext()
                .lookup("java:module/ControlliRecIndiceAipFascicoli");
        xmlContextCache = (XmlContextCache) new InitialContext().lookup("java:module/XmlContextCache");
        ambientiHelper = (AmbientiHelper) new InitialContext().lookup("java:module/AmbientiHelper");
        tipoFascicoloHelper = (TipoFascicoloHelper) new InitialContext().lookup("java:module/TipoFascicoloHelper");
    }

    private void setRispostaError() {
        log.error(
                "Creazione Indice AIP Fascicoli - Errore nella creazione dell'istanza di conservazione UniSyncro (IdC): {}",
                rispostaControlli.getDsErr());
        throw new RuntimeException(rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr());
    }

    /**
     * Riceve il fascicolo da elaborare e crea l'IdC
     *
     * @param verAipFascicolo
     *            entity FasVerAipFascicolo
     * @param codiceVersione
     *            codice versione
     * @param mappaAgenti
     *            mappa chiave/valore
     * @param sistemaConservazione
     *            sistema conservazione
     * @param enteSiam
     *            entity SIOrgEnteSiam
     * @param creatingApplicationProducer
     *            producer
     *
     * @return entity IdCType
     *
     * @throws ParerInternalError
     *             errore generico
     * @throws DatatypeConfigurationException
     *             errore generico
     * @throws JAXBException
     *             errore generico
     */
    public IdCType generaIndiceAIP(FasVerAipFascicolo verAipFascicolo, String codiceVersione,
            Map<String, String> mappaAgenti, String sistemaConservazione, SIOrgEnteSiam enteSiam,
            String creatingApplicationProducer)
            throws ParerInternalError, DatatypeConfigurationException, JAXBException {
        IdCType istanzaUnisincro = new IdCType();
        popolaIdC(istanzaUnisincro, verAipFascicolo.getIdVerAipFascicolo(), verAipFascicolo.getFasFascicolo(),
                codiceVersione, mappaAgenti, sistemaConservazione, enteSiam, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    /**
     * Riceve il fascicolo e crea l'IdC
     *
     * @param idVerAipFascicolo
     *            id versamento fascicolo aip
     * @param fasc
     *            entity FasFascicolo
     * @param codiceVersione
     *            codice versione
     * @param mappaAgenti
     *            mappa chiave/valore
     * @param sistemaConservazione
     *            sistema conservazione
     * @param enteSiam
     *            entity SIOrgEnteSiam
     * @param creatingApplicationProducer
     *            producer
     *
     * @return entity IdCType
     *
     * @throws ParerInternalError
     *             errore generico
     * @throws DatatypeConfigurationException
     *             errore generico
     * @throws JAXBException
     *             errore generico
     */
    public IdCType generaIndiceAIP(long idVerAipFascicolo, FasFascicolo fasc, String codiceVersione,
            Map<String, String> mappaAgenti, String sistemaConservazione, SIOrgEnteSiam enteSiam,
            String creatingApplicationProducer)
            throws ParerInternalError, DatatypeConfigurationException, JAXBException {
        IdCType istanzaUnisincro = new IdCType();
        popolaIdC(istanzaUnisincro, idVerAipFascicolo, fasc, codiceVersione, mappaAgenti, sistemaConservazione,
                enteSiam, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    private void popolaIdC(IdCType idc, long idVerAipFascicolo, FasFascicolo fasFascicolo, String codiceVersione,
            Map<String, String> mappaAgenti, String sistemaConservazione, SIOrgEnteSiam enteSiam,
            String creatingApplicationProducer)
            throws ParerInternalError, DatatypeConfigurationException, JAXBException {
        FasFascicolo tmpFasFascicolo = null;

        XMLGregorianCalendar timeRef = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());

        CSChiaveFasc csChiaveFasc = new CSChiaveFasc();
        csChiaveFasc.setAnno(fasFascicolo.getAaFascicolo().intValue());
        csChiaveFasc.setNumero(fasFascicolo.getCdKeyFascicolo());

        CSVersatore csVersatore = new CSVersatore();
        csVersatore.setSistemaConservazione(sistemaConservazione);
        csVersatore.setAmbiente(fasFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        csVersatore.setEnte(fasFascicolo.getOrgStrut().getOrgEnte().getNmEnte());
        csVersatore.setStruttura(fasFascicolo.getOrgStrut().getNmStrut());

        long idFascicolo = fasFascicolo.getIdFascicolo();

        // Recupero il fascicolo
        rispostaControlli.reset();
        rispostaControlli = controlliFascicoli.leggiFascicolo(idFascicolo);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            tmpFasFascicolo = (FasFascicolo) rispostaControlli.getrObject();
        }

        /*
         * ************************ DECORO SELFDESCRIPTION ************************
         */
        SelfDescriptionType selfie = new SelfDescriptionType();
        /* ID */
        IdentifierType id = new IdentifierType();
        // EVO#16486
        String urnIndiceAIP = MessaggiWSFormat.formattaUrnIndiceAipFascicoli(codiceVersione);
        // end EVO#16486
        id.setValue(MessaggiWSFormat.bonificaID(urnIndiceAIP));
        selfie.setID(id);

        /* Creating Application */
        CreatingApplicationType applicazione = new CreatingApplicationType();
        applicazione.setName("Sacer");
        applicazione.setProducer(creatingApplicationProducer);
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getVersioneSacer();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            applicazione.setVersion(rispostaControlli.getrString());
        }
        selfie.setCreatingApplication(applicazione);

        /* Source IdC */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getVersioniPrecedentiAIP(idFascicolo);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FasVerAipFascicolo> versioniPrecedenti = (List<FasVerAipFascicolo>) rispostaControlli.getrObject();
            SourceIdCType[] sorgenteArray = new SourceIdCType[versioniPrecedenti.size()];
            for (int i = 0; i < versioniPrecedenti.size(); i++) {
                SourceIdCType sorgente = new SourceIdCType();
                IdentifierType idSourceIdc = new IdentifierType();
                HashType hashSourceIdc = new HashType();
                // EVO#16486
                idSourceIdc.setValue(versioniPrecedenti.get(i).getDsUrnNormalizAipFascicolo());
                // end EVO#16486
                sorgente.setID(idSourceIdc);
                // recupero l'hash della precedente versione di AIP
                FasMetaVerAipFascicolo fasMetaVerAipFasc = IterableUtils.find(
                        versioniPrecedenti.get(i).getFasMetaVerAipFascicolos(),
                        object -> (object).getTiMeta().equals("INDICE"));
                hashSourceIdc.setValue(fasMetaVerAipFasc.getDsHashFile());
                hashSourceIdc.setFunction(hashFunction);
                sorgente.setHash(hashSourceIdc);
                sorgenteArray[i] = sorgente;
            }
            selfie.getSourceIdC().addAll(Arrays.asList(sorgenteArray)); // TODO: verificare addAll e ordinamento per
                                                                        // progressivo versione (ordine crescente)
        }

        /* More Info */
        MoreInfoType moreInfoApplic = new MoreInfoType();
        moreInfoApplic.setXMLScheme("Unisincro_MoreInfoSelfDescription_v1.0.xsd");
        EmbeddedMetadataType extraInfoDescGenerale = new EmbeddedMetadataType();
        MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
        this.popolaMetadatiIntegratiSelfDesc(tmpFasFascicolo, miSelfD, codiceVersione);
        it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory jaxbObjFactorySelfDes = new it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory();
        extraInfoDescGenerale.setAny(jaxbObjFactorySelfDes.createMetadatiIntegratiSelfDescription(miSelfD));
        moreInfoApplic.setEmbeddedMetadata(extraInfoDescGenerale);
        selfie.setMoreInfo(moreInfoApplic);
        idc.setSelfDescription(selfie);

        /*
         * *********** DECORO VDC ***********
         */
        VdCType vdc = new VdCType();
        /* ID */
        IdentifierType idVdc = new IdentifierType();
        String urn = MessaggiWSFormat
                .formattaUrnAipFascicolo(MessaggiWSFormat.formattaChiaveFascicolo(csVersatore, csChiaveFasc));
        idVdc.setValue(MessaggiWSFormat.bonificaID(urn));
        vdc.setID(idVdc);
        /* VdCGroup */
        VdCGroupType vdcGruppo = new VdCGroupType();
        // Label
        String label = "Tipo Fascicolo";
        vdcGruppo.setLabel(label);
        DecTipoFascicolo decTipoFascicolo = tipoFascicoloHelper
                .findDecTipoFascicolo(tmpFasFascicolo.getDecTipoFascicolo().getIdTipoFascicolo());
        if (decTipoFascicolo != null) {
            // ID
            IdentifierType idVdcGruppo = new IdentifierType();
            String nmTipoFascicolo = decTipoFascicolo.getNmTipoFascicolo();
            idVdcGruppo.setValue(nmTipoFascicolo);
            vdcGruppo.setID(idVdcGruppo);
            // Description
            DescriptionType desc = new DescriptionType();
            String dsTipoFascicolo = decTipoFascicolo.getDsTipoFascicolo();
            desc.setValue(dsTipoFascicolo);
            vdcGruppo.setDescription(desc);
        }
        vdc.setVdCGroup(vdcGruppo);

        /* MoreInfo */
        MoreInfoType moreInfoVdc = new MoreInfoType();
        moreInfoVdc.setXMLScheme("Unisincro_MoreInfoVdC_v1.1.xsd");
        FileType extvdc = new FileType();
        extvdc.setEncoding(null);
        extvdc.setFormat("application/xml");
        // ID
        IdentifierType idExt = new IdentifierType();
        idExt.setValue(MessaggiWSFormat.formattaUrnAipMetaFascicolo(
                MessaggiWSFormat.formattaChiaveFascicolo(csVersatore, csChiaveFasc), codiceVersione));
        extvdc.setID(idExt);
        // Path
        extvdc.setPath("\\METADATI\\");
        // Hash
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getVersioneCorrenteMetaFascicolo(idVerAipFascicolo);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FasMetaVerAipFascicolo> versioneCorrente = (List<FasMetaVerAipFascicolo>) rispostaControlli
                    .getrObject();
            FasMetaVerAipFascicolo fasMetaVerAipFasc = IterableUtils.find(versioneCorrente,
                    object -> (object).getNmMeta().equals("Fascicolo"));
            HashType hashExt = new HashType();
            hashExt.setValue(fasMetaVerAipFasc.getDsHashFile());
            hashExt.setFunction(hashFunction);
            extvdc.setHash(hashExt);
        }
        moreInfoVdc.setExternalMetadata(extvdc);
        vdc.setMoreInfo(moreInfoVdc);
        idc.setVdC(vdc);

        /*
         * **************************************** DECORO FILEGROUP DEGLI AIP DEL FASCICOLO
         * ****************************************
         */
        FileGroupType fileGroupAipUd = new FileGroupType();
        /* Label */
        fileGroupAipUd.setLabel("AIP Unità documentarie");
        /* File */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getFasContenVerAipFascicolo(idVerAipFascicolo);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FasContenVerAipFascicolo> fasContenVerAipFascList = (List<FasContenVerAipFascicolo>) rispostaControlli
                    .getrObject();
            for (FasContenVerAipFascicolo fasContenVerAipFasc : fasContenVerAipFascList) {
                FileType file = new FileType();
                file.setEncoding(null);
                file.setFormat("application/xml");
                // ID
                IdentifierType tmpIdFileItem = new IdentifierType();
                tmpIdFileItem.setScheme("local");
                CSChiave csChiaveUd = new CSChiave();
                csChiaveUd.setTipoRegistro(fasContenVerAipFasc.getAroVerIndiceAipUd().getAroIndiceAipUd()
                        .getAroUnitaDoc().getCdRegistroKeyUnitaDoc());
                csChiaveUd.setAnno(fasContenVerAipFasc.getAroVerIndiceAipUd().getAroIndiceAipUd().getAroUnitaDoc()
                        .getAaKeyUnitaDoc().longValue());
                csChiaveUd.setNumero(fasContenVerAipFasc.getAroVerIndiceAipUd().getAroIndiceAipUd().getAroUnitaDoc()
                        .getCdKeyUnitaDoc());
                // EVO#16486
                String urnUD = fasContenVerAipFasc.getNmConten();
                tmpIdFileItem.setValue(urnUD);
                // end EVO#16486
                file.setID(tmpIdFileItem);
                // Path
                file.setPath("\\DATI\\UNITADOCUMENTARIE\\");
                // Hash
                HashType tmpHashFileItem = new HashType();
                tmpHashFileItem.setValue(fasContenVerAipFasc.getAroVerIndiceAipUd().getDsHashAip());
                tmpHashFileItem.setFunction(hashFunction);
                file.setHash(tmpHashFileItem);

                fileGroupAipUd.getFile().add(file);
            }
        }
        idc.getFileGroup().add(fileGroupAipUd);

        /*
         * ******************************* DECORO FILEGROUP DEI VERSAMENTI *******************************
         */
        FileGroupType fileGroupVers = new FileGroupType();
        /* File */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getFasSipVerAipFascicolo(idVerAipFascicolo);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FasSipVerAipFascicolo> fasSipVerAipFascList = (List<FasSipVerAipFascicolo>) rispostaControlli
                    .getrObject();
            for (FasSipVerAipFascicolo fasSipVerAipFasc : fasSipVerAipFascList) {
                /* Label */
                fileGroupVers.setLabel("Versamenti_" + fasSipVerAipFasc.getNmSip());
                /* Creo un sottotag <File> riferito all’indice SIP */
                this.popolaIndiceSipFile(fasSipVerAipFasc.getFasXmlVersFascicoloRich().getIdXmlVersFascicolo(),
                        tmpFasFascicolo, fileGroupVers);
                /* Creo un sottotag <File> riferito al rapporto di versamento */
                this.popolaIndiceSipFile(fasSipVerAipFasc.getFasXmlVersFascicoloRisp().getIdXmlVersFascicolo(),
                        tmpFasFascicolo, fileGroupVers);
            }
        }
        idc.getFileGroup().add(fileGroupVers);

        /*
         * *************** DECORO PROCESS ***************
         */
        ProcessType processo = new ProcessType();
        /* Primo Agent */
        AgentType primoAgente = new AgentType();
        primoAgente.setType("organization");
        primoAgente.setRole("OtherRole");
        primoAgente.setOtherRole("Producer");
        AgentNameType primoAgenteNome = new AgentNameType();
        primoAgenteNome.setFormalName(enteSiam.getNmEnteSiam());
        primoAgente.setAgentName(primoAgenteNome);
        AgentIDType primoAgenteID = new AgentIDType();
        primoAgenteID.setValue(enteSiam.getNmEnteSiam());
        primoAgenteID.setScheme("TaxCode");
        primoAgente.getAgentID().add(primoAgenteID);
        processo.getAgent().add(primoAgente);
        /* Secondo Agent */
        AgentType secondoAgente = new AgentType();
        secondoAgente.setType("organization");
        secondoAgente.setRole("OtherRole");
        secondoAgente.setOtherRole("Preserver");
        AgentNameType secondoAgenteNome = new AgentNameType();
        secondoAgenteNome.setFormalName(mappaAgenti.get(AGENT_PRESERVER_FORMALNAME));
        secondoAgente.setAgentName(secondoAgenteNome);
        AgentIDType secondoAgenteID = new AgentIDType();
        secondoAgenteID.setValue(mappaAgenti.get(AGENT_PRESERVER_TAXCODE));
        secondoAgenteID.setScheme("TaxCode");
        secondoAgente.getAgentID().add(secondoAgenteID);
        processo.getAgent().add(secondoAgente);
        /* Terzo Agent */
        AgentType agenteGB = new AgentType();
        agenteGB.setType("person");
        agenteGB.setRole("OtherRole");
        agenteGB.setOtherRole("PreservationManager");
        AgentNameType agenteGBNome = new AgentNameType();
        NameAndSurnameType nomeEcognome = new NameAndSurnameType();
        nomeEcognome.setLastName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_LASTNAME));
        nomeEcognome.setFirstName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_FIRSTNAME));
        agenteGBNome.setNameAndSurname(nomeEcognome);
        agenteGB.setAgentName(agenteGBNome);
        // Agent_ID
        AgentIDType soggettoID = new AgentIDType();
        soggettoID.setValue(mappaAgenti.get(AGENT_PRESERVATION_MNGR_TAXCODE));
        soggettoID.setScheme("TaxCode");
        agenteGB.getAgentID().add(soggettoID);
        processo.getAgent().add(agenteGB);
        /* Time Reference */
        TimeReferenceType tempo = new TimeReferenceType();
        AttachedTimeStampType attachedTimeStamp = new AttachedTimeStampType();
        attachedTimeStamp.setNormal(timeRef);
        tempo.setAttachedTimeStamp(attachedTimeStamp);
        processo.setTimeReference(tempo);
        /* Law And Regulations */
        String legge = "DPCM 3 dicembre 2013 - Regole tecniche in materia di conservazione (GU n.59 del 12-3-2014)";
        LawAndRegulationsType lar = new LawAndRegulationsType();
        lar.setValue(legge);
        processo.setLawAndRegulations(lar);
        idc.setProcess(processo);
    }

    private void popolaMetadatiIntegratiSelfDesc(FasFascicolo tmpFasFascicolo,
            MetadatiIntegratiSelfDescriptionType miSelfD, String codiceVersione)
            throws ParerInternalError, JAXBException {
        OrgStrut orgStrut = ambientiHelper.findOrgStrutById(tmpFasFascicolo.getOrgStrut().getIdStrut());
        long idAmbiente = orgStrut.getOrgEnte().getOrgAmbiente().getIdAmbiente();

        /*
         * Determino il modello xsd per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene, con il
         * tipo "AIP_SELF_DESCRIPTION_MORE_INFO"
         */
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.getDecModelloSelfDescMoreInfo(idAmbiente);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<DecModelloXsdFascicolo> decModelloXsdFascList = (List<DecModelloXsdFascicolo>) rispostaControlli
                    .getrObject();
            log.info(
                    "Creazione Indice AIP Fascicoli - ambiente id {}: trovati {}"
                            + " modelli xsd attivi di tipo AIP_SELF_DESCRIPTION_MORE_INFO da processare",
                    idAmbiente, decModelloXsdFascList.size());

            /* Se per l'ambiente il modello XSD non viene trovato */
            if (decModelloXsdFascList.isEmpty()) {
                throw new ParerInternalError(
                        "Il modello di tipo AIP_SELF_DESCRIPTION_MORE_INFO per la data corrente e l'ambiente "
                                + orgStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente() + " non è definito");
            }

            // Procedo nella costruzione della porzione di xml da inserire nel tag <MoreInfo> secondo l’xsd recuperato.
            it.eng.parer.aipFascicoli.xml.usselfdescResp.IndiceAIPType indiceAIP = new it.eng.parer.aipFascicoli.xml.usselfdescResp.IndiceAIPType();
            indiceAIP.setVersioneXSDIndiceAIP("1.0");
            indiceAIP.setFormatoIndiceAIP("UNI SInCRO (UNI 11386:2010)");
            indiceAIP.setVersioneIndiceAIP(codiceVersione);
            miSelfD.setIndiceAIP(indiceAIP);

            /* Eseguo il marshalling degli oggetti creati in MetadatiIntegratiSelfDescriptionType per salvarli */
            StringWriter tmpWriter = marshallMiSelfDesc(miSelfD);

            // Eseguo la validazione dell'xml prodotto con l'xsd recuperato da DEC_MODELLO_XSD_FASCICOLO
            try {
                String xsd = decModelloXsdFascList.get(0).getBlXsd();
                XmlUtils.validateXml(xsd, tmpWriter.toString());
                log.info("Documento validato con successo");
            } catch (SAXException | IOException ex) {
                log.error(ex.getMessage(), ex);
                throw new ParerInternalError("Il file non rispetta l'XSD previsto per lo scambio");
            }
        }
    }

    private StringWriter marshallMiSelfDesc(MetadatiIntegratiSelfDescriptionType miSelfD) throws JAXBException {
        it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory objFctMiSelfDescType = new it.eng.parer.aipFascicoli.xml.usselfdescResp.ObjectFactory();
        JAXBElement<MetadatiIntegratiSelfDescriptionType> elementMiSelfDescType = objFctMiSelfDescType
                .createMetadatiIntegratiSelfDescription(miSelfD);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getSelfDescMoreInfoCtx().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfAipFascSelfDescSchema());
        tmpMarshaller.marshal(elementMiSelfDescType, tmpWriter);
        return tmpWriter;
    }

    private void popolaIndiceSipFile(Long idXmlVersFascicolo, FasFascicolo tmpFasFascicolo,
            FileGroupType fileGroupVers) {
        rispostaControlli.reset();
        rispostaControlli = controlliRecIndiceAipFascicoli.leggiXmlVersFascicoliAip(idXmlVersFascicolo,
                tmpFasFascicolo.getIdFascicolo());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            FasXmlVersFascicolo fasXmlVersFasc = (FasXmlVersFascicolo) rispostaControlli.getrObject();
            FileType file = new FileType();
            file.setEncoding(null);
            file.setFormat("application/xml");
            // ID
            IdentifierType tmpIdFileItem = new IdentifierType();
            tmpIdFileItem.setScheme("local");
            tmpIdFileItem.setValue(fasXmlVersFasc.getDsUrnXmlVers());
            file.setID(tmpIdFileItem);
            // Path
            file.setPath("\\VERSAMENTI\\" + fasXmlVersFasc.getIdXmlVersFascicolo() + "\\");
            // Hash
            HashType tmpHashFileItem = new HashType();
            tmpHashFileItem.setValue(fasXmlVersFasc.getDsAlgoHashXmlVers());
            tmpHashFileItem.setFunction(hashFunction);
            file.setHash(tmpHashFileItem);

            fileGroupVers.getFile().add(file);
        }
    }
}
