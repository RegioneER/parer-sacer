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

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerRuntimeException;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.UsrUser;
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
import it.eng.parer.serie.xml.serfileRespV2.MetadatiIntegratiFileType;
import it.eng.parer.serie.xml.serfileRespV2.UnitaDocumentariePresentiVolumeType;
import it.eng.parer.serie.xml.serprodResp.AmbienteType;
import it.eng.parer.serie.xml.serprodResp.EnteType;
import it.eng.parer.serie.xml.serprodResp.MetadatiIntegratiProducerType;
import it.eng.parer.serie.xml.serprodResp.StrutturaType;
import it.eng.parer.serie.xml.serselfdescRespV2.IndiceAIPType;
import it.eng.parer.serie.xml.serselfdescRespV2.MetadatiIntegratiSelfDescriptionType;
import it.eng.parer.serie.xml.servdcRespV2.AmbitiEContenutoType;
import it.eng.parer.serie.xml.servdcRespV2.ConsistenzaType;
import it.eng.parer.serie.xml.servdcRespV2.CriterioOrdinamentoType;
import it.eng.parer.serie.xml.servdcRespV2.CriterioSelezioneType;
import it.eng.parer.serie.xml.servdcRespV2.DefinizioneDatoSpecificoType;
import it.eng.parer.serie.xml.servdcRespV2.DefinizioniDatoSpecificoType;
import it.eng.parer.serie.xml.servdcRespV2.DescrizioneTipologieContenutoType;
import it.eng.parer.serie.xml.servdcRespV2.FiltriDatiSpecificiType;
import it.eng.parer.serie.xml.servdcRespV2.FiltroDatiSpecificiType;
import it.eng.parer.serie.xml.servdcRespV2.FrequenzaPeriodoType;
import it.eng.parer.serie.xml.servdcRespV2.LacunaType;
import it.eng.parer.serie.xml.servdcRespV2.MetadatiIntegratiVdCType;
import it.eng.parer.serie.xml.servdcRespV2.ModalitaAcquisizioneType;
import it.eng.parer.serie.xml.servdcRespV2.ModalitaSelezioneType;
import it.eng.parer.serie.xml.servdcRespV2.NotaType;
import it.eng.parer.serie.xml.servdcRespV2.NoteConservatoreType;
import it.eng.parer.serie.xml.servdcRespV2.NoteProduttoreType;
import it.eng.parer.serie.xml.servdcRespV2.NoteType;
import it.eng.parer.serie.xml.servdcRespV2.SelezioneType;
import it.eng.parer.serie.xml.servdcRespV2.SelezioneUnitaDocumentarieType;
import it.eng.parer.serie.xml.servdcRespV2.TempoConservazioneType;
import it.eng.parer.serie.xml.servdcRespV2.TipiDocumentoPrincipaleType;
import it.eng.parer.serie.xml.servdcRespV2.TipiDocumentoType;
import it.eng.parer.serie.xml.servdcRespV2.TipiRegistroType;
import it.eng.parer.serie.xml.servdcRespV2.TipiUnitaDocumentariaType;
import it.eng.parer.serie.xml.servdcRespV2.TipoDocumentoType;
import it.eng.parer.serie.xml.servdcRespV2.TipoRegistroType;
import it.eng.parer.serie.xml.servdcRespV2.TipoUnitaDocumentariaType;
import it.eng.parer.serie.xml.servdcRespV2.UnitaDocumentarieMancantiType;
import it.eng.parer.serie.xml.servdcRespV2.UnitaDocumentarieNonProdotteType;
import it.eng.parer.serie.xml.servdcRespV2.UnitaDocumentariePresentiType;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.AgentLegalPersonDto;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.usmainRespV2.Agent;
import it.eng.parer.ws.xml.usmainRespV2.AgentID;
import it.eng.parer.ws.xml.usmainRespV2.AgentName;
import it.eng.parer.ws.xml.usmainRespV2.CreatingApplication;
import it.eng.parer.ws.xml.usmainRespV2.EmbeddedMetadata;
import it.eng.parer.ws.xml.usmainRespV2.File;
import it.eng.parer.ws.xml.usmainRespV2.FileGroup;
import it.eng.parer.ws.xml.usmainRespV2.Hash;
import it.eng.parer.ws.xml.usmainRespV2.ID;
import it.eng.parer.ws.xml.usmainRespV2.MoreInfo;
import it.eng.parer.ws.xml.usmainRespV2.NameAndSurname;
import it.eng.parer.ws.xml.usmainRespV2.PIndex;
import it.eng.parer.ws.xml.usmainRespV2.PIndexID;
import it.eng.parer.ws.xml.usmainRespV2.PIndexSource;
import it.eng.parer.ws.xml.usmainRespV2.PVolume;
import it.eng.parer.ws.xml.usmainRespV2.PVolumeGroup;
import it.eng.parer.ws.xml.usmainRespV2.PVolumeSource;
import it.eng.parer.ws.xml.usmainRespV2.Process;
import it.eng.parer.ws.xml.usmainRespV2.RelevantDocument;
import it.eng.parer.ws.xml.usmainRespV2.SelfDescription;
import it.eng.parer.ws.xml.usmainRespV2.TimeInfo;
import it.eng.parer.ws.xml.usmainRespV2.TimeReference;

@SuppressWarnings("unchecked")
@Stateless(mappedName = "CreazioneIndiceAipSerieUdUtilV2")
@LocalBean
public class CreazioneIndiceAipSerieUdUtilV2 {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipSerieUdUtilV2.class);
    private RispostaControlli rispostaControlli;
    private final String hashFunction = TipiHash.SHA_256.descrivi();
    private final String schemeAttribute = Costanti.SchemeAttributes.SCHEME_LOCAL;
    @EJB
    ControlliIndiceAipSerieUd controlliIndiceAipSerieUd;
    @EJB
    ConfigurationHelper configurationHelper;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    @EJB
    ControlliRecupero controlliRecupero;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    @EJB
    UnitaDocumentarieHelper unitaDocumentarieHelper;

    public CreazioneIndiceAipSerieUdUtilV2() throws NamingException {
        rispostaControlli = new RispostaControlli();
    }

    private void setRispostaError() {
        log.error(
                "Creazione Indice AIP v2.0 - Errore nella creazione dell'istanza di conservazione UniSyncro (PIndex): {}",
                rispostaControlli.getDsErr());
        throw new SacerRuntimeException(rispostaControlli.getCodErr() + " - " + rispostaControlli.getDsErr(),
                SacerErrorCategory.INTERNAL_ERROR);
    }

    /**
     * Riceve la versione serie da elaborare e crea il PIndex
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
     * @return entity PIndex
     *
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NamingException
     *             errore generico
     * @throws java.security.NoSuchAlgorithmException
     *             errore generico
     */
    public PIndex generaIndiceAIPSerieUd(SerVerSerieDaElab verSerieDaElab, Date timeRef,
            Map<String, String> mappaAgenti, String creatingApplicationProducer)
            throws IOException, NamingException, NoSuchAlgorithmException {
        PIndex istanzaUnisincro = new PIndex();
        popolaPIndex(istanzaUnisincro, verSerieDaElab, timeRef, mappaAgenti, creatingApplicationProducer);
        return istanzaUnisincro;
    }

    /**
     * Decora le classi generate da JAXB per la creazione dell'indice AIP
     *
     * @param idc
     *            entity PIndex
     * @param verSerieDaElab
     *            entity SerVerSerieDaElab
     * @param timeRef
     *            timestamp riferimento
     * @param mappaAgenti
     *            mappa chiave/valore
     */
    private void popolaPIndex(PIndex idc, SerVerSerieDaElab verSerieDaElab, Date timeRef,
            Map<String, String> mappaAgenti, String creatingApplicationProducer)
            throws IOException, NamingException, NoSuchAlgorithmException {

        long idVerSerie = verSerieDaElab.getSerVerSerie().getIdVerSerie();

        // Scompatto il campo cdVerIndiceAip
        String[] numbers = verSerieDaElab.getSerVerSerie().getCdVerSerie().split("[.]");
        int minorNumber = Integer.parseInt(numbers[1]);
        String versioneSerie = Integer.parseInt(numbers[0]) == 0 ? "1." + Integer.toString(++minorNumber)
                : Integer.parseInt(numbers[0]) + "." + Integer.toString(++minorNumber);

        String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();
        String tipoSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getDecTipoSerie().getDsTipoSerie();
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
        SelfDescription selfie = new SelfDescription();
        /* ID */
        ID id = new ID();

        // EVO#16492
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);
        // salvo NORMALIZZATO
        id.setValue(MessaggiWSFormat.formattaUrnIndiceAIPSerieUDFir(tmpUrnNorm, versioneSerie));
        id.setScheme(schemeAttribute);
        // end EVO#16492
        selfie.setID(id);

        /* Creating Application */
        CreatingApplication creatingApp = new CreatingApplication();
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
                    PIndexSource sorgente = new PIndexSource();
                    // ID
                    ID idSourceIdc = new ID();
                    // EVO#16492
                    idSourceIdc.setValue(selfDescriptionQuery1Bean.getDsUrnFilePrec());
                    idSourceIdc.setScheme(schemeAttribute);
                    // end EVO#16492
                    sorgente.setID(idSourceIdc);
                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = IOUtils.getPath("/pindexsource",
                            StringUtils.capitalize(Constants.SACER.toLowerCase()), IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    String fileName = IOUtils.getFilename(
                            IOUtils.extractPartUrnName(selfDescriptionQuery1Bean.getDsUrnFilePrec(), true),
                            IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                    String pathPIndexSource = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                    sorgente.setPath(pathPIndexSource);
                    // Hash
                    it.eng.parer.ws.xml.usmainRespV2.Hash hashSourceIdc = new it.eng.parer.ws.xml.usmainRespV2.Hash();
                    hashSourceIdc.setValue(selfDescriptionQuery1Bean.getDsHashFilePrec());
                    hashSourceIdc.setHashFunction(hashFunction);
                    hashSourceIdc.setCanonicalXML(Boolean.FALSE);
                    sorgente.setHash(hashSourceIdc);
                    selfie.getPIndexSource().add(sorgente);
                }
            }
        }

        /* More Info */
        MoreInfo moreInfoApplic = new MoreInfo();
        moreInfoApplic.setXmlSchema("/xmlschema/Unisincro_MoreInfoSelfDescription_v2.0.xsd");
        EmbeddedMetadata extraInfoDescGenerale = new EmbeddedMetadata();
        MetadatiIntegratiSelfDescriptionType miSelfD = new MetadatiIntegratiSelfDescriptionType();
        IndiceAIPType indiceAIP = new IndiceAIPType();
        indiceAIP.setVersioneIndiceAIP(versioneSerie);
        indiceAIP.setDataCreazione(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        indiceAIP.setFormato("UNI SInCRO (UNI 11386:2020)");
        indiceAIP.setVersioneXSDIndiceAIP(Costanti.VERSIONE_XSD_INDICE_AIP_V2);
        miSelfD.setIndiceAIP(indiceAIP);
        it.eng.parer.serie.xml.serselfdescRespV2.ObjectFactory objFct1 = new it.eng.parer.serie.xml.serselfdescRespV2.ObjectFactory();
        extraInfoDescGenerale.setAny(objFct1.createMetadatiIntegratiSelfDescription(miSelfD));
        moreInfoApplic.setEmbeddedMetadata(extraInfoDescGenerale);
        selfie.setMoreInfo(moreInfoApplic);
        idc.setSelfDescription(selfie);

        /*
         * *********** DECORO VDC ***********
         */
        PVolume pVolume = new PVolume();
        /* ID */
        ID idpVolume = new ID();
        // EVO#16492
        String urnSerie = MessaggiWSFormat.formattaBaseUrnSerie(
                MessaggiWSFormat.formattaUrnPartVersatore(csVersatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                codiceSerie);
        idpVolume.setValue(urnSerie);
        idpVolume.setScheme(schemeAttribute);
        // end EVO#16492
        pVolume.setID(idpVolume);
        String labelpVol = "Pacchetto di archiviazione di una serie di Unità Documentarie";
        pVolume.setLabel(labelpVol);
        String descpVol = "Il pacchetto contiene l'elenco delle UD appartenenti alla serie suddiviso in uno o più Volumi";
        pVolume.setDescription(descpVol);

        /* PVolumeSource */
        List<PVolumeSource> sorgenteArray = new ArrayList<>();
        for (int i = 0; i < selfie.getPIndexSource().size(); i++) {
            PVolumeSource sorgente = new PVolumeSource();
            /* ID */
            ID idPVolumeSource = new ID();
            String tmpPartUrnName = IOUtils.extractPartUrnName(selfie.getPIndexSource().get(i).getID().getValue());
            if (tmpPartUrnName.toUpperCase().startsWith("INDICEAIP-UD")) {
                idPVolumeSource.setValue(idpVolume.getValue());
            } else {
                idPVolumeSource.setValue(selfie.getPIndexSource().get(i).getID().getValue());
            }
            idPVolumeSource.setScheme(schemeAttribute);
            sorgente.setID(idPVolumeSource);
            /* PIndexID */
            PIndexID pIndexId = new PIndexID();
            pIndexId.setValue(selfie.getPIndexSource().get(i).getID().getValue());
            pIndexId.setScheme(schemeAttribute);
            sorgente.setPIndexID(pIndexId);
            sorgenteArray.add(sorgente);
        }
        pVolume.getPVolumeSource().addAll(sorgenteArray);

        /* PVolumeGroup */
        PVolumeGroup pVolumeGruppo = new PVolumeGroup();

        // ID
        ID idPVolumeGruppo = new ID();
        idPVolumeGruppo.setValue(codiceSerie);
        idPVolumeGruppo.setScheme(schemeAttribute);
        pVolumeGruppo.setID(idPVolumeGruppo);
        // Label
        pVolumeGruppo.setLabel(tipoSerie); // MEV #27080 Qui va inserito il tipo serie
        // DescriptionType
        pVolumeGruppo.setDescription(descrizioneSerie);
        pVolume.setPVolumeGroup(pVolumeGruppo);

        /* MoreInfo */
        MoreInfo moreInfoVdc = new MoreInfo();
        moreInfoVdc.setXmlSchema("/xmlschema/Unisincro_MoreInfoPVolume_v2.0.xsd");
        EmbeddedMetadata emdvdc = new EmbeddedMetadata();
        MetadatiIntegratiVdCType mivdc = new MetadatiIntegratiVdCType();
        this.popolaMetadatiIntegratiVdC(mivdc, idVerSerie);
        it.eng.parer.serie.xml.servdcRespV2.ObjectFactory objFct2 = new it.eng.parer.serie.xml.servdcRespV2.ObjectFactory();
        emdvdc.setAny(objFct2.createMetadatiIntegratiVdC(mivdc));
        moreInfoVdc.setEmbeddedMetadata(emdvdc);
        pVolume.setMoreInfo(moreInfoVdc);
        idc.setPVolume(pVolume);

        /*
         * ***************** DECORO FILEGROUP *****************
         */
        FileGroup fileGroup = new FileGroup();
        /* File */
        rispostaControlli.reset();
        rispostaControlli = controlliIndiceAipSerieUd.getFileQuery1Data(idVerSerie);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<FileQuery_1_Bean> fileQuery1BeanList = (List<FileQuery_1_Bean>) rispostaControlli.getrObject();
            for (FileQuery_1_Bean fileQuery1Bean : fileQuery1BeanList) {
                File file = new File();
                file.setFormat(IOUtils.CONTENT_TYPE.XML.getContentType());
                file.setEncoding("binary");
                file.setExtension(IOUtils.CONTENT_TYPE.XML.getFileExt());
                // ID
                ID tmpIdFileItem = new ID();
                tmpIdFileItem.setScheme(schemeAttribute);
                // EVO#16492
                tmpIdFileItem.setValue(fileQuery1Bean.getDsUrnIxVol());
                // end EVO#16492
                file.setID(tmpIdFileItem);
                /* Definisco la folder */
                String folder = IOUtils.getPath("/file", StringUtils.capitalize(Constants.SACER.toLowerCase()),
                        IOUtils.UNIX_FILE_SEPARATOR);
                /* Definisco il nome e l'estensione del file */
                String fileName = IOUtils.getFilename(IOUtils.extractPartUrnName(fileQuery1Bean.getDsUrnIxVol(), true),
                        IOUtils.CONTENT_TYPE.XML.getFileExt());
                /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                String pathPIndexSource = IOUtils.getAbsolutePath(folder, fileName, IOUtils.UNIX_FILE_SEPARATOR);
                file.setPath(pathPIndexSource);
                // Hash
                Hash tmpHashFileItem = new Hash();

                tmpHashFileItem.setValue(fileQuery1Bean.getDsHashIxVol());
                tmpHashFileItem.setHashFunction(hashFunction);
                tmpHashFileItem.setCanonicalXML(Boolean.FALSE);
                file.setHash(tmpHashFileItem);

                /* MoreInfo */
                MoreInfo moreInfoFile = new MoreInfo();
                moreInfoFile.setXmlSchema("/xmlschema/Unisincro_MoreInfoFile_v1.1.xsd");
                EmbeddedMetadata emdfile = new EmbeddedMetadata();
                MetadatiIntegratiFileType mifile = new MetadatiIntegratiFileType();
                this.popolaMetadatiIntegratiFile(mifile, fileQuery1Bean);
                it.eng.parer.serie.xml.serfileRespV2.ObjectFactory objFct3 = new it.eng.parer.serie.xml.serfileRespV2.ObjectFactory();
                emdfile.setAny(objFct3.createMetadatiIntegratiFile(mifile));
                moreInfoFile.setEmbeddedMetadata(emdfile);
                file.setMoreInfo(moreInfoFile);
                fileGroup.setID(tmpIdFileItem);
                // Label
                String labelfgroup = "Elenco delle UD appartenenti alla Serie";
                fileGroup.setLabel(labelfgroup);
                // Description
                String descrfgroup = "L'elenco delle UD è suddiviso in uno o più Volumi, ognuno dei quali corrisponde a un elemento File. Per ogni Unità documentaria contenuta riporta l'urn e l'hash dell'indiceAIP della stessa";
                fileGroup.setDescription(descrfgroup);
                fileGroup.getFile().add(file);
                idc.getFileGroup().add(fileGroup);
            }
        }

        /*
         * *************** DECORO PROCESS ***************
         */
        SIOrgEnteSiam orgEnteConvenz = null;
        OrgStrut orgStrut = verSerieDaElab.getSerVerSerie().getSerSerie().getOrgStrut();
        if (orgStrut.getIdEnteConvenz() != null) {
            orgEnteConvenz = unitaDocumentarieHelper.findById(SIOrgEnteSiam.class, orgStrut.getIdEnteConvenz());
        }
        BigDecimal idAmbiente = BigDecimal.valueOf(orgStrut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        Process processo = new Process();
        /* Primo Agent */
        // SUBMITTER
        Agent primoAgenteSubmitter = new Agent();
        // Submitter attributes
        primoAgenteSubmitter.setAgentType("legal person");
        // Submitter.AgentID
        AgentID primoAgenteID = new AgentID();
        // MAC#26215
        if (orgEnteConvenz != null) {
            primoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
        }
        // end MAC#26215
        primoAgenteSubmitter.getAgentID().add(primoAgenteID);
        // Submitter.AgentName
        AgentName primoAgenteNome = new AgentName();
        // Submitter.AgentName.FormalName
        if (orgEnteConvenz != null) {
            primoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
        } else {
            primoAgenteNome.setFormalName(orgStrut.getOrgEnte().getDsEnte());
        }
        primoAgenteSubmitter.setAgentName(primoAgenteNome);
        // Submitter.RelevantDocument
        // MEV#25903
        String[] submtrRelevantDocuments = mappaAgenti.get(AGENT_SUBMITTER_RELEVANTDOCUMENT).split("\\|");
        for (String relevantDocument : submtrRelevantDocuments) {
            RelevantDocument primoAgenteRelevantDocument = new RelevantDocument();
            primoAgenteRelevantDocument.setValue(relevantDocument);
            primoAgenteSubmitter.getRelevantDocument().add(primoAgenteRelevantDocument);
        }
        // end MEV#25903
        processo.setSubmitter(primoAgenteSubmitter);

        /* Secondo Agent */
        // HOLDER
        Process.Holder secondoAgenteHolder = new Process.Holder();
        // Holder attributes
        secondoAgenteHolder.setAgentType("legal person");
        secondoAgenteHolder.setHolderRole("soggetto produttore");
        // Holder.AgentID
        AgentID secondoAgenteID = new AgentID();
        // MAC#26215
        if (orgEnteConvenz != null) {
            secondoAgenteID.setValue("VATIT-" + orgEnteConvenz.getCdFisc());
        }
        // end MAC#26215
        secondoAgenteHolder.getAgentID().add(secondoAgenteID);
        // Holder.AgentName
        AgentName secondoAgenteNome = new AgentName();
        // Holder.AgentName.FormalName
        if (orgEnteConvenz != null) {
            secondoAgenteNome.setFormalName(orgEnteConvenz.getNmEnteSiam());
        } else {
            secondoAgenteNome.setFormalName(orgStrut.getOrgEnte().getDsEnte());
        }
        secondoAgenteHolder.setAgentName(secondoAgenteNome);
        // Holder.RelevantDocument
        // MEV#25903
        String[] holderRelevantDocuments = mappaAgenti.get(AGENT_HOLDER_RELEVANTDOCUMENT).split("\\|");
        for (String relevantDocument : holderRelevantDocuments) {
            RelevantDocument secondoAgenteRelevantDocument = new RelevantDocument();
            secondoAgenteRelevantDocument.setValue(relevantDocument);
            secondoAgenteHolder.getRelevantDocument().add(secondoAgenteRelevantDocument);
        }
        // end MEV#25903
        processo.getHolder().add(secondoAgenteHolder);
        // LISTA AUTHORIZED SIGNER
        rispostaControlli.reset();
        rispostaControlli = controlliRecupero.leggiListaUserByHsmUsername(idAmbiente);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaError();
        } else {
            List<UsrUser> authSignerList = (List<UsrUser>) rispostaControlli.getrObject();
            List<Process.AuthorizedSigner> authSignerArray = new ArrayList<>();
            for (int i = 0; i < authSignerList.size(); i++) {
                Process.AuthorizedSigner authorizedSigner = new Process.AuthorizedSigner();
                // AuthorizedSigner attributes:
                // AgentType
                authorizedSigner.setAgentType("natural person");
                // SignerRole
                rispostaControlli.reset();
                rispostaControlli = controlliRecupero.leggiRuoloAuthorizedSigner(authSignerList.get(i).getIdUserIam(),
                        idAmbiente);
                if (!rispostaControlli.isrBoolean()) {
                    setRispostaError();
                } else {
                    String signerRole = ((String[]) rispostaControlli.getrObject())[0];
                    authorizedSigner.setSignerRole(signerRole);
                }
                // AuthorizedSigner.AgentID
                AgentID agenteID = new AgentID();
                agenteID.setValue("TINIT-" + authSignerList.get(i).getCdFisc());
                authorizedSigner.getAgentID().add(agenteID);
                // AuthorizedSigner.AgentName
                AgentName agenteNome = new AgentName();
                // AuthorizedSigner.AgentName.NameAndSurname
                NameAndSurname nameAndSurname = new NameAndSurname();
                nameAndSurname.setFirstName(authSignerList.get(i).getNmNomeUser());
                nameAndSurname.setLastName(authSignerList.get(i).getNmCognomeUser());
                agenteNome.setNameAndSurname(nameAndSurname);
                authorizedSigner.setAgentName(agenteNome);
                // AuthorizedSigner.RelevantDocument
                RelevantDocument agenteRelevantDocument = new RelevantDocument();
                String relevantDocument = ((String[]) rispostaControlli.getrObject())[1];
                agenteRelevantDocument.setValue(relevantDocument);
                authorizedSigner.getRelevantDocument().add(agenteRelevantDocument);
                authSignerArray.add(authorizedSigner);
            }

            // MEV#27831 - Modifica creazione indice AIP in presenza di SIGILLO
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiAuthorizedSignerLegalPersons(idAmbiente);
            if (!rispostaControlli.isrBoolean()) {
                setRispostaError();
            } else {
                ArrayList<AgentLegalPersonDto> agentiLegal = (ArrayList<AgentLegalPersonDto>) rispostaControlli
                        .getrObject();
                // MAC#29103 - Risoluzione problema con parametro "AGENT_AUTHORIZED_SIGNER_ROLE_LEGAL_PERSON"
                // Se non ci sono agent non deve produrre il frammento di xml!
                if (!agentiLegal.isEmpty()) {
                    for (AgentLegalPersonDto dto : agentiLegal) {
                        Process.AuthorizedSigner authorizedSigner = new Process.AuthorizedSigner();
                        // AuthorizedSigner attributes:
                        // AgentType
                        authorizedSigner.setAgentType("legal person");
                        // SignerRole
                        authorizedSigner.setSignerRole(dto.getRuolo());
                        // AuthorizedSigner.AgentID
                        AgentID agenteID = new AgentID();
                        agenteID.setValue(dto.getId());
                        authorizedSigner.getAgentID().add(agenteID);
                        // AuthorizedSigner.AgentName
                        AgentName agentName = new AgentName();
                        // AuthorizedSigner.AgentName.NameAndSurname
                        agentName.setFormalName(dto.getNome());
                        authorizedSigner.setAgentName(agentName);
                        // AuthorizedSigner.RelevantDocument
                        RelevantDocument agenteRelevantDocument = new RelevantDocument();
                        agenteRelevantDocument.setValue(dto.getDocumentoRilevante());
                        authorizedSigner.getRelevantDocument().add(agenteRelevantDocument);
                        authSignerArray.add(authorizedSigner);
                    }
                }
            }
            // FINE - MEV#27831
            processo.getAuthorizedSigner().addAll(authSignerArray);
        }

        /* Time Reference */
        TimeReference tempo = new TimeReference();
        // TimeInfo
        TimeInfo timeInfo = new TimeInfo();
        timeInfo.setValue(XmlDateUtility.dateToXMLGregorianCalendar(timeRef));
        timeInfo.setAttachedTimeStamp(false);
        tempo.setTimeInfo(timeInfo);
        processo.setTimeReference(tempo);
        /* Law And Regulations */
        String legge = "Linee Guida sulla formazione, gestione e conservazione dei documenti informatici (09-09-2020)";
        processo.setLawsAndRegulations(legge);
        idc.setProcess(processo);

        // PIndex attributes:
        idc.setLanguage("it");
        idc.setSincroVersion("2.0");
        idc.setUri("http://www.uni.com/U3011/sincro-v2/PIndex.xsd");
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
