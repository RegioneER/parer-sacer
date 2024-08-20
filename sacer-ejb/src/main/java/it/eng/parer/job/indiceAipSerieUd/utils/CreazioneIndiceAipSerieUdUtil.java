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

package it.eng.parer.job.indiceAipSerieUd.utils;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.job.indiceAipSerieUd.dto.FileQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.SelfDescriptionQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_10_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_11_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_1_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_2_3_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_4_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_5_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_6_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_7_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_8_Bean;
import it.eng.parer.job.indiceAipSerieUd.dto.VdCQuery_9_Bean;
import it.eng.parer.job.indiceAipSerieUd.helper.ControlliIndiceAipSerieUd;
import it.eng.parer.serie.xml.serfileResp.MetadatiIntegratiFileType;
import it.eng.parer.serie.xml.serfileResp.UnitaDocumentariePresentiVolumeType;
import it.eng.parer.serie.xml.serprodResp.AmbienteType;
import it.eng.parer.serie.xml.serprodResp.EnteType;
import it.eng.parer.serie.xml.serprodResp.MetadatiIntegratiProducerType;
import it.eng.parer.serie.xml.serprodResp.StrutturaType;
import it.eng.parer.serie.xml.serselfdescResp.IndiceAIPType;
import it.eng.parer.serie.xml.serselfdescResp.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.serie.xml.servdcResp.AmbitiEContenutoType;
import it.eng.parer.serie.xml.servdcResp.ConsistenzaType;
import it.eng.parer.serie.xml.servdcResp.CriterioOrdinamentoType;
import it.eng.parer.serie.xml.servdcResp.CriterioSelezioneType;
import it.eng.parer.serie.xml.servdcResp.DefinizioneDatoSpecificoType;
import it.eng.parer.serie.xml.servdcResp.DefinizioniDatoSpecificoType;
import it.eng.parer.serie.xml.servdcResp.DescrizioneTipologieContenutoType;
import it.eng.parer.serie.xml.servdcResp.FiltriDatiSpecificiType;
import it.eng.parer.serie.xml.servdcResp.FiltroDatiSpecificiType;
import it.eng.parer.serie.xml.servdcResp.FrequenzaPeriodoType;
import it.eng.parer.serie.xml.servdcResp.LacunaType;
import it.eng.parer.serie.xml.servdcResp.MetadatiIntegratiVdCType;
import it.eng.parer.serie.xml.servdcResp.ModalitaAcquisizioneType;
import it.eng.parer.serie.xml.servdcResp.ModalitaSelezioneType;
import it.eng.parer.serie.xml.servdcResp.NotaType;
import it.eng.parer.serie.xml.servdcResp.NoteConservatoreType;
import it.eng.parer.serie.xml.servdcResp.NoteProduttoreType;
import it.eng.parer.serie.xml.servdcResp.NoteType;
import it.eng.parer.serie.xml.servdcResp.SelezioneType;
import it.eng.parer.serie.xml.servdcResp.SelezioneUnitaDocumentarieType;
import it.eng.parer.serie.xml.servdcResp.TempoConservazioneType;
import it.eng.parer.serie.xml.servdcResp.TipiDocumentoPrincipaleType;
import it.eng.parer.serie.xml.servdcResp.TipiDocumentoType;
import it.eng.parer.serie.xml.servdcResp.TipiRegistroType;
import it.eng.parer.serie.xml.servdcResp.TipiUnitaDocumentariaType;
import it.eng.parer.serie.xml.servdcResp.TipoDocumentoType;
import it.eng.parer.serie.xml.servdcResp.TipoRegistroType;
import it.eng.parer.serie.xml.servdcResp.TipoUnitaDocumentariaType;
import it.eng.parer.serie.xml.servdcResp.UnitaDocumentarieMancantiType;
import it.eng.parer.serie.xml.servdcResp.UnitaDocumentarieNonProdotteType;
import it.eng.parer.serie.xml.servdcResp.UnitaDocumentariePresentiType;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.usmainResp.AgentIDType;
import it.eng.parer.ws.xml.usmainResp.AgentNameType;
import it.eng.parer.ws.xml.usmainResp.AgentType;
import it.eng.parer.ws.xml.usmainResp.AttachedTimeStampType;
import it.eng.parer.ws.xml.usmainResp.CreatingApplicationType;
import it.eng.parer.ws.xml.usmainResp.DescriptionType;
import it.eng.parer.ws.xml.usmainResp.EmbeddedMetadataType;
import it.eng.parer.ws.xml.usmainResp.FileGroupType;
import it.eng.parer.ws.xml.usmainResp.FileType;
import it.eng.parer.ws.xml.usmainResp.HashType;
import it.eng.parer.ws.xml.usmainResp.IdCType;
import it.eng.parer.ws.xml.usmainResp.IdentifierType;
import it.eng.parer.ws.xml.usmainResp.LawAndRegulationsType;
import it.eng.parer.ws.xml.usmainResp.MoreInfoType;
import it.eng.parer.ws.xml.usmainResp.NameAndSurnameType;
import it.eng.parer.ws.xml.usmainResp.ProcessType;
import it.eng.parer.ws.xml.usmainResp.SelfDescriptionType;
import it.eng.parer.ws.xml.usmainResp.SourceIdCType;
import it.eng.parer.ws.xml.usmainResp.TimeReferenceType;
import it.eng.parer.ws.xml.usmainResp.VdCGroupType;
import it.eng.parer.ws.xml.usmainResp.VdCType;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings({ "unchecked" })
public class CreazioneIndiceAipSerieUdUtil {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipSerieUdUtil.class);
    private RispostaControlli rispostaControlli;
    private ControlliIndiceAipSerieUd controlliIndiceAipSerieUd;
    private ConfigurationHelper configurationHelper;
    private final String hashFunction = TipiHash.SHA_256.descrivi();

    public CreazioneIndiceAipSerieUdUtil() throws NamingException {
        rispostaControlli = new RispostaControlli();
        // Recupera l'ejb per la lettura di informazioni, se possibile
        controlliIndiceAipSerieUd = (ControlliIndiceAipSerieUd) new InitialContext()
                .lookup("java:module/ControlliIndiceAipSerieUd");
        configurationHelper = (ConfigurationHelper) new InitialContext().lookup("java:module/ConfigurationHelper");
    }

    private void setRispostaError() {
        log.error(
                "{} --- Creazione Indice Aip Versione Serie Ud --- {} Errore nella creazione dell'istanza di conservazione UniSyncro (IdC): ",
                CreazioneIndiceAipSerieUdUtil.class.getSimpleName(), rispostaControlli.getDsErr());
        throw new RuntimeException(rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr());
    }

    /**
     * Riceve la versione serie da elaborare e crea l'IdC
     *
     * @param verSerieDaElab
     *            entity SerVerSerieDaElab
     * @param timeRef
     *            timestamp riferimento
     * @param mappaAgenti
     *            mappa chiave/valore
     * @param creatingApplicationProducer
     *            producer
     *
     * @return entity IdCType
     */
    public IdCType generaIndiceAIPSerieUd(SerVerSerieDaElab verSerieDaElab, Date timeRef,
            Map<String, String> mappaAgenti, String creatingApplicationProducer) {
        IdCType istanzaUnisincro = new IdCType();
        popolaIdC(istanzaUnisincro, verSerieDaElab, timeRef, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    /**
     * Decora le classi generate da JAXB per la creazione dell'indice AIP
     *
     * @param idc
     *            entity IdCType
     * @param verSerieDaElab
     *            entity SerVerSerieDaElab
     * @param timeRef
     *            timestamp riferimento
     * @param mappaAgenti
     *            mappa chiave/valore
     */
    private void popolaIdC(IdCType idc, SerVerSerieDaElab verSerieDaElab, Date timeRef, Map<String, String> mappaAgenti,
            String creatingApplicationProducer) {

        long idVerSerie = verSerieDaElab.getSerVerSerie().getIdVerSerie();
        String versioneSerie = verSerieDaElab.getSerVerSerie().getCdVerSerie();
        String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();
        String descrizioneSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getDsSerie();
        final String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

        CSVersatore csVersatore = new CSVersatore();
        csVersatore.setAmbiente(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte()
                .getOrgAmbiente().getNmAmbiente());
        csVersatore.setEnte(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte().getNmEnte());
        csVersatore.setStruttura(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getNmStrut());
        // sistema (new URN)
        csVersatore.setSistemaConservazione(sistema);

        /*
         * ************************ DECORO SELFDESCRIPTION ************************
         */
        SelfDescriptionType selfie = new SelfDescriptionType();
        /* ID */
        IdentifierType id = new IdentifierType();
        // EVO#16492
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);
        // salvo NORMALIZZATO
        id.setValue(MessaggiWSFormat.formattaUrnIndiceAIPSerieUDFir(tmpUrnNorm, versioneSerie));
        // end EVO#16492
        selfie.setID(id);

        /* Creating Application */
        CreatingApplicationType creatingApp = new CreatingApplicationType();
        creatingApp.setName("Sacer");
        creatingApp.setProducer(creatingApplicationProducer);
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVersioneSacer();
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            creatingApp.setVersion(rispostaControlli.getrString());
        }
        selfie.setCreatingApplication(creatingApp);

        /* Source IdC */
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getSelfDescriptionQuery1Data(idVerSerie);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<SelfDescriptionQuery_1_Bean> selfDescriptionQuery1BeanList = (List<SelfDescriptionQuery_1_Bean>) rispostaControlli
                    .getrObject();
            if (selfDescriptionQuery1BeanList != null && !selfDescriptionQuery1BeanList.isEmpty()) {
                for (SelfDescriptionQuery_1_Bean selfDescriptionQuery1Bean : selfDescriptionQuery1BeanList) {
                    SourceIdCType sorgente = new SourceIdCType();
                    // ID
                    IdentifierType idSourceIdc = new IdentifierType();
                    // EVO#16492
                    idSourceIdc.setValue(selfDescriptionQuery1Bean.getDsUrnFilePrec());
                    // end EVO#16492
                    sorgente.setID(idSourceIdc);
                    // Hash
                    HashType hashSourceIdc = new HashType();
                    hashSourceIdc.setValue(selfDescriptionQuery1Bean.getDsHashFilePrec());
                    hashSourceIdc.setFunction(hashFunction);
                    sorgente.setHash(hashSourceIdc);
                    selfie.getSourceIdC().add(sorgente);
                }
            }
        }

        /* More Info */
        MoreInfoType moreInfoApplic = new MoreInfoType();
        moreInfoApplic.setXMLScheme("IndiceAIPSerieUd_MoreInfo_SelfDescription_v1.0.xsd");
        EmbeddedMetadataType extraInfoDescGenerale = new EmbeddedMetadataType();
        MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
        IndiceAIPType indiceAIP = new IndiceAIPType();
        indiceAIP.setVersioneIndiceAIP(versioneSerie);
        indiceAIP.setDataCreazione(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        indiceAIP.setFormato("UNI SInCRO (UNI 11386:2010)");
        indiceAIP.setVersioneXSDIndiceAIP("1.0");
        miSelfD.setIndiceAIP(indiceAIP);
        it.eng.parer.serie.xml.serselfdescResp.ObjectFactory objFct1 = new it.eng.parer.serie.xml.serselfdescResp.ObjectFactory();
        extraInfoDescGenerale.setAny(objFct1.createMetadatiIntegratiSelfDescription(miSelfD));
        moreInfoApplic.setEmbeddedMetadata(extraInfoDescGenerale);
        selfie.setMoreInfo(moreInfoApplic);

        /*
         * *********** DECORO VDC ***********
         */
        VdCType vdc = new VdCType();
        /* ID */
        IdentifierType idVdc = new IdentifierType();
        // EVO#16492
        String urnSerie = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);
        idVdc.setValue(urnSerie);
        // end EVO#16492
        vdc.setID(idVdc);
        /* VdCGroup */
        VdCGroupType vdcGruppo = new VdCGroupType();
        // Label
        String label = "Serie documentaria";
        vdcGruppo.setLabel(label);
        // ID
        IdentifierType idVdcGruppo = new IdentifierType();
        idVdcGruppo.setValue(codiceSerie);
        vdcGruppo.setID(idVdcGruppo);
        // DescriptionType
        DescriptionType desc = new DescriptionType();
        desc.setValue(descrizioneSerie);
        vdcGruppo.setDescription(desc);
        vdc.setVdCGroup(vdcGruppo);

        /* MoreInfo */
        MoreInfoType moreInfoVdc = new MoreInfoType();
        moreInfoVdc.setXMLScheme("IndiceAIPSerie_MoreInfo_VdC_v1.0.xsd");
        EmbeddedMetadataType emdvdc = new EmbeddedMetadataType();
        MetadatiIntegratiVdCType mivdc = new MetadatiIntegratiVdCType();
        this.popolaMetadatiIntegratiVdC(mivdc, idVerSerie);
        it.eng.parer.serie.xml.servdcResp.ObjectFactory objFct2 = new it.eng.parer.serie.xml.servdcResp.ObjectFactory();
        emdvdc.setAny(objFct2.createMetadatiIntegratiVdC(mivdc));
        moreInfoVdc.setEmbeddedMetadata(emdvdc);
        vdc.setMoreInfo(moreInfoVdc);

        /*
         * ***************** DECORO FILEGROUP *****************
         */
        FileGroupType fileGroup = new FileGroupType();
        /* Label */
        fileGroup.setLabel("Volume");

        /* File */
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getFileQuery1Data(idVerSerie);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FileQuery_1_Bean> fileQuery1BeanList = (List<FileQuery_1_Bean>) rispostaControlli.getrObject();
            for (FileQuery_1_Bean fileQuery1Bean : fileQuery1BeanList) {
                FileType file = new FileType();
                file.setEncoding(null);
                file.setFormat("application/xml");
                // ID
                IdentifierType tmpIdFileItem = new IdentifierType();
                tmpIdFileItem.setScheme("local");
                // EVO#16492
                tmpIdFileItem.setValue(fileQuery1Bean.getDsUrnIxVol());
                // end EVO#16492
                file.setID(tmpIdFileItem);
                // Hash
                HashType tmpHashFileItem = new HashType();
                tmpHashFileItem.setValue(fileQuery1Bean.getDsHashIxVol());
                tmpHashFileItem.setFunction(hashFunction);
                file.setHash(tmpHashFileItem);

                /* MoreInfo */
                MoreInfoType moreInfoFile = new MoreInfoType();
                moreInfoFile.setXMLScheme("IndiceAIPSerie_MoreInfo_File_v1.0.xsd");
                EmbeddedMetadataType emdfile = new EmbeddedMetadataType();
                MetadatiIntegratiFileType mifile = new MetadatiIntegratiFileType();
                this.popolaMetadatiIntegratiFile(mifile, fileQuery1Bean);
                it.eng.parer.serie.xml.serfileResp.ObjectFactory objFct3 = new it.eng.parer.serie.xml.serfileResp.ObjectFactory();
                emdfile.setAny(objFct3.createMetadatiIntegratiFile(mifile));
                moreInfoFile.setEmbeddedMetadata(emdfile);
                file.setMoreInfo(moreInfoFile);

                fileGroup.getFile().add(file);
            }
        }

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
        primoAgenteNome.setFormalName(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getDsStrut());
        primoAgente.setAgentName(primoAgenteNome);
        MoreInfoType moreInfoProc = new MoreInfoType();
        moreInfoProc.setXMLScheme("IndiceAIPSerie_MoreInfo_Producer_v1.0.xsd");
        EmbeddedMetadataType emdproc = new EmbeddedMetadataType();
        MetadatiIntegratiProducerType mip = new MetadatiIntegratiProducerType();
        this.popolaMetadatiIntegratiProducer(mip, verSerieDaElab);
        it.eng.parer.serie.xml.serprodResp.ObjectFactory objFct4 = new it.eng.parer.serie.xml.serprodResp.ObjectFactory();
        emdproc.setAny(objFct4.createMetadatiIntegratiProducer(mip));
        moreInfoProc.setEmbeddedMetadata(emdproc);
        primoAgente.setMoreInfo(moreInfoProc);
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
        AgentType agenteManager = new AgentType();
        agenteManager.setType("person");
        agenteManager.setRole("PreservationManager");
        AgentNameType agenteManagerNome = new AgentNameType();
        NameAndSurnameType nomeECognomeManager = new NameAndSurnameType();
        nomeECognomeManager.setLastName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_LASTNAME));
        nomeECognomeManager.setFirstName(mappaAgenti.get(AGENT_PRESERVATION_MNGR_FIRSTNAME));
        agenteManagerNome.setNameAndSurname(nomeECognomeManager);
        agenteManager.setAgentName(agenteManagerNome);
        // Agent_ID
        AgentIDType soggettoID = new AgentIDType();
        soggettoID.setValue(mappaAgenti.get(AGENT_PRESERVATION_MNGR_TAXCODE));
        soggettoID.setScheme("TaxCode");
        agenteManager.getAgentID().add(soggettoID);
        processo.getAgent().add(agenteManager);
        /* Time Reference */
        TimeReferenceType tempo = new TimeReferenceType();
        AttachedTimeStampType attachedTimeStamp = new AttachedTimeStampType();
        attachedTimeStamp.setNormal(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        tempo.setAttachedTimeStamp(attachedTimeStamp);
        processo.setTimeReference(tempo);
        /* Law And Regulations */
        String legge = "DPCM 3 dicembre 2013 - Regole tecniche in materia di conservazione (GU n.59 del 12-3-2014)";
        LawAndRegulationsType lar = new LawAndRegulationsType();
        lar.setValue(legge);
        processo.setLawAndRegulations(lar);

        // Set di tutti i tag
        idc.setSelfDescription(selfie);
        idc.setVdC(vdc);
        idc.getFileGroup().add(fileGroup);
        idc.setProcess(processo);
    }

    private void popolaMetadatiIntegratiVdC(MetadatiIntegratiVdCType mivdc, long idVerSerie) {
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery1Data(idVerSerie);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_1_Bean> vdcQuery1BeanList = (List<VdCQuery_1_Bean>) rispostaControlli.getrObject();
            if (vdcQuery1BeanList != null && !vdcQuery1BeanList.isEmpty()) {
                VdCQuery_1_Bean vdcQuery1Bean = vdcQuery1BeanList.get(0);

                /*
                 * ************* ACQUISIZIONE *************
                 */
                popolaAcquisizione(mivdc, vdcQuery1Bean);

                /*
                 * ******************* AMBITI E CONTENUTO *******************
                 */
                popolaAmbitiEContenuto(mivdc, idVerSerie, vdcQuery1Bean);

                /*
                 * ********************* CRITERIO ORDINAMENTO *********************
                 */
                popolaCriterioOrdinamento(mivdc, vdcQuery1Bean);

                /*
                 * ******************** TEMPO CONSERVAZIONE ********************
                 */
                popolaTempoConservazione(mivdc, vdcQuery1Bean);

                /*
                 * ***** NOTE *****
                 */
                popolaNote(mivdc, vdcQuery1Bean);
            }
        }
    }

    private void popolaAcquisizione(MetadatiIntegratiVdCType mivdc, VdCQuery_1_Bean vdcQuery1Bean) {
        // Da non definire se il tipo nota ACQUISIZIONE_INFO non e? definito per la versione serie corrente
        if (vdcQuery1Bean.getDsNotaAcqInfo() != null) {
            ModalitaAcquisizioneType acquisizione = new ModalitaAcquisizioneType();
            acquisizione.setNota(getNota(vdcQuery1Bean.getDsAutoreAcqInfo(), vdcQuery1Bean.getDtNotaAcqInfo(),
                    vdcQuery1Bean.getDsNotaAcqInfo()));
            mivdc.setModalitaAcquisizione(acquisizione);
        }
    }

    private void popolaAmbitiEContenuto(MetadatiIntegratiVdCType mivdc, long idVerSerie,
            VdCQuery_1_Bean vdcQuery1Bean) {

        AmbitiEContenutoType ambitiContenuto = new AmbitiEContenutoType();

        /* Nota */
        // Da non definire se il tipo nota AMBITI_CONTENUTO non e? definito per la versione serie corrente
        if (vdcQuery1Bean.getDsNotaAmbConten() != null) {
            ambitiContenuto.setNota(getNota(vdcQuery1Bean.getDsAutoreAmbConten(), vdcQuery1Bean.getDtNotaAmbConten(),
                    vdcQuery1Bean.getDsNotaAmbConten()));
        }

        /* Consistenza */
        ambitiContenuto.setConsistenza(getConsistenza(idVerSerie, vdcQuery1Bean));

        /* SelezioneUnitaDocumentarie */
        ambitiContenuto.setSelezioneUnitaDocumentarie(getSelezioneUnitaDocumentarie(idVerSerie, vdcQuery1Bean));

        /* Descrizione Tipologie Contenuto */
        ambitiContenuto.setDescrizioneTipologieContenuto(getDescrizioneTipologieContenuto(vdcQuery1Bean));

        mivdc.setAmbitiEContenuto(ambitiContenuto);
    }

    private void popolaCriterioOrdinamento(MetadatiIntegratiVdCType mivdc, VdCQuery_1_Bean vdcQuery1Bean) {
        // N.B. da non definire se il tipo nota CRITERI_ORDINAMENTO non e? definito per la versione serie corrente
        if (vdcQuery1Bean.getDsNotaCritOrd() != null) {
            CriterioOrdinamentoType criterioOrdinamento = new CriterioOrdinamentoType();
            NotaType nota = new NotaType();
            nota.setAutore(vdcQuery1Bean.getDsAutoreCritOrd());
            nota.setData(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtNotaCritOrd()));
            nota.setValue(vdcQuery1Bean.getDsNotaCritOrd());
            criterioOrdinamento.setNota(nota);
            mivdc.setCriterioOrdinamento(criterioOrdinamento);
        }
    }

    private void popolaTempoConservazione(MetadatiIntegratiVdCType mivdc, VdCQuery_1_Bean vdcQuery1Bean) {
        TempoConservazioneType tempoConservazione = new TempoConservazioneType();
        // Tipo conservazione
        tempoConservazione.setTipoConservazione(vdcQuery1Bean.getTiConserv());

        if (vdcQuery1Bean.getNiAnniConserv() != null && vdcQuery1Bean.getNiAnniConserv().longValue() != 9999) {
            // Anni conservazione
            tempoConservazione.setAnniConservazione(vdcQuery1Bean.getNiAnniConserv().toBigInteger());
            // Data scarto
            DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            tempoConservazione.setDataScarto(df.format(vdcQuery1Bean.getDtScarto()));
        }
        // Note conservazione (opzionale)
        if (vdcQuery1Bean.getDsAutoreConserv() != null) {
            NotaType nota = new NotaType();
            nota.setAutore(vdcQuery1Bean.getDsAutoreConserv());
            nota.setData(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtNotaConserv()));
            nota.setValue(vdcQuery1Bean.getDsNotaConserv());
            tempoConservazione.setNota(nota);
        }
        mivdc.setTempoConservazione(tempoConservazione);
    }

    private void popolaNote(MetadatiIntegratiVdCType mivdc, VdCQuery_1_Bean vdcQuery1Bean) {
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery11Data(vdcQuery1Bean.getIdVerSerie());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_11_Bean> vdcQuery11BeanList = (List<VdCQuery_11_Bean>) rispostaControlli.getrObject();
            if (vdcQuery11BeanList != null && !vdcQuery11BeanList.isEmpty()) {
                NoteType note = new NoteType();
                NoteConservatoreType noteCons = new NoteConservatoreType();
                NoteProduttoreType noteProd = new NoteProduttoreType();

                for (VdCQuery_11_Bean vdcQuery11Bean : vdcQuery11BeanList) {
                    NotaType notaNote = getNota(vdcQuery11Bean.getDsAutore(), vdcQuery11Bean.getDtNota(),
                            vdcQuery11Bean.getDsNota());

                    if (vdcQuery11Bean.getCdTipoNotaSerie().equals(CostantiDB.TipoNotaSerie.NOTE_CONSERVATORE.name())) {
                        noteCons.getNota().add(notaNote);
                    } else if (vdcQuery11Bean.getCdTipoNotaSerie()
                            .equals(CostantiDB.TipoNotaSerie.NOTE_PRODUTTORE.name())) {
                        noteProd.getNota().add(notaNote);
                    }
                }

                if (!noteCons.getNota().isEmpty()) {
                    note.setNoteConservatore(noteCons);
                }

                if (!noteProd.getNota().isEmpty()) {
                    note.setNoteProduttore(noteProd);
                }
                mivdc.setNote(note);
            }
        }
    }

    private NotaType getNota(String autore, Date data, String contenuto) {
        NotaType nota = new NotaType();
        nota.setAutore(autore);
        nota.setData(XmlDateUtility.dateToXMLGregorianCalendar(data));
        nota.setValue(contenuto);
        return nota;
    }

    private ConsistenzaType getConsistenza(long idVerSerie, VdCQuery_1_Bean vdcQuery1Bean) {

        ConsistenzaType consistenza = new ConsistenzaType();

        // Unità documentarie presenti
        UnitaDocumentariePresentiType udp = new UnitaDocumentariePresentiType();
        udp.setNumeroUnitaDocumentarie(vdcQuery1Bean.getNiUnitaDoc().toBigInteger());
        UnitaDocumentariePresentiType.PrimaUnitaDocumentaria primaUd = new UnitaDocumentariePresentiType.PrimaUnitaDocumentaria();
        primaUd.setCodice(vdcQuery1Bean.getCdFirstUnitaDoc());
        primaUd.setData(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtFirstUnitaDoc()));
        UnitaDocumentariePresentiType.UltimaUnitaDocumentaria ultimaUd = new UnitaDocumentariePresentiType.UltimaUnitaDocumentaria();
        ultimaUd.setCodice(vdcQuery1Bean.getCdLastUnitaDoc());
        ultimaUd.setData(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtLastUnitaDoc()));
        udp.setPrimaUnitaDocumentaria(primaUd);
        udp.setUltimaUnitaDocumentaria(ultimaUd);
        consistenza.setUnitaDocumentariePresenti(udp);

        // Unità documentarie mancanti
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery23Data(idVerSerie,
                CostantiDB.TipoLacuna.MANCANTI.name());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_2_3_Bean> vdcQuery23BeanList = (List<VdCQuery_2_3_Bean>) rispostaControlli.getrObject();
            if (vdcQuery23BeanList != null && !vdcQuery23BeanList.isEmpty()) {
                UnitaDocumentarieMancantiType udm = new UnitaDocumentarieMancantiType();

                for (VdCQuery_2_3_Bean vdCQuery23Bean : vdcQuery23BeanList) {
                    // Aggiungo le lacune
                    udm.getLacuna()
                            .add(getLacuna(vdCQuery23Bean.getTiModLacuna(), vdCQuery23Bean.getDlLacuna(),
                                    vdCQuery23Bean.getNiIniLacuna(), vdCQuery23Bean.getNiFinLacuna(),
                                    vdCQuery23Bean.getDlNotaLacuna()));
                }
                consistenza.setUnitaDocumentarieMancanti(udm);
            }
        }
        // Unità documentarie non prodotte
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery23Data(idVerSerie,
                CostantiDB.TipoLacuna.NON_PRODOTTE.name());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_2_3_Bean> vdcQuery23BeanList = (List<VdCQuery_2_3_Bean>) rispostaControlli.getrObject();
            if (vdcQuery23BeanList != null && !vdcQuery23BeanList.isEmpty()) {
                UnitaDocumentarieNonProdotteType udnp = new UnitaDocumentarieNonProdotteType();

                for (VdCQuery_2_3_Bean vdCQuery23Bean : vdcQuery23BeanList) {
                    // Aggiungo le lacune
                    udnp.getLacuna()
                            .add(getLacuna(vdCQuery23Bean.getTiModLacuna(), vdCQuery23Bean.getDlLacuna(),
                                    vdCQuery23Bean.getNiIniLacuna(), vdCQuery23Bean.getNiFinLacuna(),
                                    vdCQuery23Bean.getDlNotaLacuna()));
                }
                consistenza.setUnitaDocumentarieNonProdotte(udnp);
            }
        }

        return consistenza;
    }

    private LacunaType getLacuna(String tiModLacuna, String dlLacuna, BigDecimal niIniLacuna, BigDecimal niFinLacuna,
            String dlNotaLacuna) {
        LacunaType lacuna = new LacunaType();

        if (tiModLacuna.equals(CostantiDB.TipoModLacuna.DESCRIZIONE.name())) {
            lacuna.setDescrizioneLacuna(dlLacuna);
        } else if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())) {
            lacuna.setProgressivoInizialeLacuna(BigInteger.valueOf(niIniLacuna.longValue()));
            lacuna.setProgressivoFinaleLacuna(BigInteger.valueOf(niFinLacuna.longValue()));
        }

        if (dlNotaLacuna != null) {
            lacuna.setNoteLacuna(dlNotaLacuna);
        }
        return lacuna;
    }

    private SelezioneUnitaDocumentarieType getSelezioneUnitaDocumentarie(long idVerSerie,
            VdCQuery_1_Bean vdcQuery1Bean) {
        SelezioneUnitaDocumentarieType sud = new SelezioneUnitaDocumentarieType();

        /* Modalità selezione */
        ModalitaSelezioneType ms = new ModalitaSelezioneType();

        // Selezione
        ms.setSelezione(SelezioneType.fromValue(vdcQuery1Bean.getTiSelezioneUd()));

        switch (ms.getSelezione()) {
        case ANNUALE:
            // Anno selezione
            ms.setAnnoSelezione(vdcQuery1Bean.getAaSelezioneUd().toBigInteger());
            break;
        case INFRA_ANNUALE:
            // Periodo selezione
            ms.setPeriodoSelezione(vdcQuery1Bean.getNiPeriodoSelSerie().toBigInteger());
            // Frequenza periodo selezione
            ms.setFrequenzaPeriodoSelezione(FrequenzaPeriodoType.fromValue(vdcQuery1Bean.getTiPeriodoSelSerie()));
            break;
        case RANGE_DATE:
            // Data inizio selezione
            ms.setDataInizioSelezione(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtInizioSelSerie()));
            // Data fine selezione
            ms.setDataFineSelezione(XmlDateUtility.dateToXMLGregorianCalendar(vdcQuery1Bean.getDtFineSelSerie()));
            break;
        }
        sud.setModalitaSelezione(ms);

        /* Criterio selezione */
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery4Data(idVerSerie);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_4_Bean> vdcQuery4BeanList = (List<VdCQuery_4_Bean>) rispostaControlli.getrObject();
            for (VdCQuery_4_Bean vdcQuery4Bean : vdcQuery4BeanList) {
                CriterioSelezioneType cs = new CriterioSelezioneType();
                // Tipo registro
                cs.setTipoRegistro(vdcQuery4Bean.getCdRegistroUnitaDoc());
                // Tipo unità documentaria
                cs.setTipoUnitaDocumentaria(vdcQuery4Bean.getNmTipoUnitaDoc());

                // Tipi documento principali
                rispostaControlli.reset();
                rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery5Data(vdcQuery4Bean.getIdTipoSerieUd());
                if (!rispostaControlli.isrBoolean()) {
                    setRispostaError();
                } else {
                    List<VdCQuery_5_Bean> vdcQuery5BeanList = (List<VdCQuery_5_Bean>) rispostaControlli.getrObject();
                    if (vdcQuery5BeanList != null && !vdcQuery5BeanList.isEmpty()) {
                        TipiDocumentoPrincipaleType tdp = new TipiDocumentoPrincipaleType();
                        for (VdCQuery_5_Bean vdcQuery5Bean : vdcQuery5BeanList) {
                            tdp.getTipoDocumentoPrincipale().add(vdcQuery5Bean.getNmTipoDocPrinc());
                        }
                        cs.setTipiDocumentoPrincipale(tdp);
                    }
                }

                // Filtri dati specifici
                rispostaControlli.reset();
                rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery6Data(vdcQuery4Bean.getIdTipoSerieUd());
                if (!rispostaControlli.isrBoolean()) {
                    setRispostaError();
                } else {
                    List<VdCQuery_6_Bean> vdcQuery6BeanList = (List<VdCQuery_6_Bean>) rispostaControlli.getrObject();
                    if (vdcQuery6BeanList != null && !vdcQuery6BeanList.isEmpty()) {
                        FiltriDatiSpecificiType filtrids = new FiltriDatiSpecificiType();
                        for (VdCQuery_6_Bean vdcQuery6Bean : vdcQuery6BeanList) {
                            // Filtro dati specifici
                            FiltroDatiSpecificiType filtrods = new FiltroDatiSpecificiType();
                            // Dato specifico
                            filtrods.setDatoSpecifico(vdcQuery6Bean.getNmAttribDatiSpec());
                            // Operatore
                            filtrods.setOperatore(vdcQuery6Bean.getTiOper());
                            // Valore
                            filtrods.setValore(vdcQuery6Bean.getDlValore());

                            // Definizioni Dato Specifico
                            rispostaControlli.reset();
                            rispostaControlli = controlliIndiceAipSerieUd
                                    .getVdCQuery7Data(vdcQuery6Bean.getIdFiltroSelUdAttb());
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaError();
                            } else {
                                List<VdCQuery_7_Bean> vdcQuery7BeanList = (List<VdCQuery_7_Bean>) rispostaControlli
                                        .getrObject();
                                DefinizioniDatoSpecificoType dids = new DefinizioniDatoSpecificoType();
                                for (VdCQuery_7_Bean vdcQuery7Bean : vdcQuery7BeanList) {
                                    DefinizioneDatoSpecificoType dds = new DefinizioneDatoSpecificoType();
                                    if (vdcQuery7Bean.getTiEntitaSacer()
                                            .equals(CostantiDB.TipiEntitaSacer.UNI_DOC.name())) {
                                        // Tipo unità documentaria
                                        dds.setTipoUnitaDocumentaria(vdcQuery7Bean.getNmTipoUnitaDoc());
                                    } else if (vdcQuery7Bean.getTiEntitaSacer()
                                            .equals(CostantiDB.TipiEntitaSacer.DOC.name())) {
                                        dds.setTipoDocumentoPrincipale(vdcQuery7Bean.getNmTipoDoc());
                                    }
                                    // Lista versioni XSD
                                    dds.setListaVersioniXsd(vdcQuery7Bean.getDsListaVersioniXsd());

                                    dids.getDefinizioneDatoSpecifico().add(dds);
                                }
                                filtrods.setDefinizioniDatoSpecifico(dids);
                            }
                            filtrids.getFiltroDatiSpecifici().add(filtrods);
                        }
                        cs.setFiltriDatiSpecifici(filtrids);

                    }
                }
                sud.getCriterioSelezione().add(cs);
            } // end for Criterio Selezione
        } // else Criterio Selezione

        return sud;
    }

    private DescrizioneTipologieContenutoType getDescrizioneTipologieContenuto(VdCQuery_1_Bean vdcQuery1Bean) {
        DescrizioneTipologieContenutoType dtc = new DescrizioneTipologieContenutoType();

        // Tipi Registro
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery8Data(vdcQuery1Bean.getIdVerSerie());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_8_Bean> vdcQuery8BeanList = (List<VdCQuery_8_Bean>) rispostaControlli.getrObject();
            TipiRegistroType tipiReg = new TipiRegistroType();
            for (VdCQuery_8_Bean vdcQuery8Bean : vdcQuery8BeanList) {
                // Tipo registro
                TipoRegistroType tipoReg = new TipoRegistroType();
                tipoReg.setCodiceTipoRegistro(vdcQuery8Bean.getCdRegistroUnitaDoc());
                tipoReg.setDescrizioneTipoRegistro(vdcQuery8Bean.getDsRegistroUnitaDoc());
                tipiReg.getTipoRegistro().add(tipoReg);
            }
            dtc.setTipiRegistro(tipiReg);
        }

        // Tipi Unità Documentaria
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery9Data(vdcQuery1Bean.getIdVerSerie());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_9_Bean> vdcQuery9BeanList = (List<VdCQuery_9_Bean>) rispostaControlli.getrObject();
            TipiUnitaDocumentariaType tipiUd = new TipiUnitaDocumentariaType();
            for (VdCQuery_9_Bean vdcQuery9Bean : vdcQuery9BeanList) {
                // Tipo Unità Documentaria
                TipoUnitaDocumentariaType tipoUd = new TipoUnitaDocumentariaType();
                tipoUd.setNomeTipoUnitaDocumentaria(vdcQuery9Bean.getNmTipoUnitaDoc());
                tipoUd.setDescrizioneTipoUnitaDocumentaria(vdcQuery9Bean.getDsTipoUnitaDoc());
                tipiUd.getTipoUnitaDocumentaria().add(tipoUd);
            }
            dtc.setTipiUnitaDocumentaria(tipiUd);
        }

        // Tipi Documento
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getVdCQuery10Data(vdcQuery1Bean.getIdVerSerie());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<VdCQuery_10_Bean> vdcQuery10BeanList = (List<VdCQuery_10_Bean>) rispostaControlli.getrObject();
            TipiDocumentoType tipiDoc = new TipiDocumentoType();
            for (VdCQuery_10_Bean vdcQuery10Bean : vdcQuery10BeanList) {
                // Tipo Documento
                TipoDocumentoType tipoDoc = new TipoDocumentoType();
                tipoDoc.setNomeTipoDocumento(vdcQuery10Bean.getNmTipoDoc());
                tipoDoc.setDescrizioneTipoDocumento(vdcQuery10Bean.getDsTipoDoc());
                tipiDoc.getTipoDocumento().add(tipoDoc);
            }
            dtc.setTipiDocumento(tipiDoc);
        }
        return dtc;
    }

    private void popolaMetadatiIntegratiFile(MetadatiIntegratiFileType mifile, FileQuery_1_Bean fileQueryBean) {
        // Numero unità documentarie volume
        mifile.setNumeroUnitaDocumentarieVolume(fileQueryBean.getNiUnitaDocVol().toBigInteger());
        // Unità documentarie presenti volume
        UnitaDocumentariePresentiVolumeType udpv = new UnitaDocumentariePresentiVolumeType();
        UnitaDocumentariePresentiVolumeType.PrimaUnitaDocumentaria primaUnitaDocumentaria = new UnitaDocumentariePresentiVolumeType.PrimaUnitaDocumentaria();
        primaUnitaDocumentaria.setCodice(fileQueryBean.getCdFirstUnitaDocVol());
        primaUnitaDocumentaria
                .setData(XmlDateUtility.dateToXMLGregorianCalendar(fileQueryBean.getDtFirstUnitaDocVol()));
        udpv.setPrimaUnitaDocumentaria(primaUnitaDocumentaria);
        UnitaDocumentariePresentiVolumeType.UltimaUnitaDocumentaria ultimaUnitaDocumentaria = new UnitaDocumentariePresentiVolumeType.UltimaUnitaDocumentaria();
        ultimaUnitaDocumentaria.setCodice(fileQueryBean.getCdLastUnitaDocVol());
        ultimaUnitaDocumentaria
                .setData(XmlDateUtility.dateToXMLGregorianCalendar(fileQueryBean.getDtLastUnitaDocVol()));
        udpv.setUltimaUnitaDocumentaria(ultimaUnitaDocumentaria);
        mifile.setUnitaDocumentariePresentiVolume(udpv);
    }

    private void popolaMetadatiIntegratiProducer(MetadatiIntegratiProducerType mip, SerVerSerieDaElab verSerieDaElab) {
        AmbienteType ambiente = new AmbienteType();
        ambiente.setNomeAmbiente(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte()
                .getOrgAmbiente().getNmAmbiente());
        ambiente.setDescrizioneAmbiente(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte()
                .getOrgAmbiente().getDsAmbiente());
        mip.setAmbiente(ambiente);
        EnteType ente = new EnteType();
        ente.setNomeEnte(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte().getNmEnte());
        ente.setDescrizioneEnte(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getOrgEnte().getDsEnte());
        mip.setEnte(ente);
        StrutturaType struttura = new StrutturaType();
        struttura.setNomeStruttura(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getNmStrut());
        struttura.setDescrizioneStruttura(verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut().getDsStrut());
        mip.setStruttura(struttura);
    }
}
