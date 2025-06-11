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

package it.eng.parer.job.indiceAipSerieUd.ejb;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerFileVerSerie;
import it.eng.parer.entity.SerStatoSerie;
import it.eng.parer.entity.SerStatoVerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;
import it.eng.parer.job.indiceAipSerieUd.utils.CreazioneIndiceAipSerieUdUtil;
import it.eng.parer.job.indiceAipSerieUd.utils.CreazioneIndiceAipSerieUdUtilV2;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.ParamIamHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.usmainResp.IdCType;
import it.eng.parer.ws.xml.usmainRespV2.PIndex;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceAipVersioneSerieUd")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceAipVersioneSerieUd {

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceAipVersioneSerieUd.class);

    @EJB
    public CreazioneIndiceAipSerieUdHelper ciasudHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private ConfigurationHelper confHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ParamIamHelper paramIamHelper;
    @EJB
    CreazioneIndiceAipSerieUdUtilV2 creazioneIndiceAipSerieUdUtilV2;
    // MEV#30400
    @EJB
    private ObjectStorageService objectStorageService;

    private static final String LOG_SALVATAGGIO_OS = "Salvato l'indice aip della seri su Object storage nel bucket {} con chiave {}! ";
    // end MEV#30400

    /* Ricavo i valori degli Agent dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParam = Arrays.asList(AGENT_PRESERVER_FORMALNAME, AGENT_PRESERVER_TAXCODE,
            AGENT_PRESERVATION_MNGR_TAXCODE, AGENT_PRESERVATION_MNGR_LASTNAME, AGENT_PRESERVATION_MNGR_FIRSTNAME);

    /* Ricavo i valori degli Agent v2.0 dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParamV2 = Arrays.asList();

    /* Ricavo i valori degli Agent v2.0 dalla tabella IAM_PARAM_APPLIC */
    private static final List<String> agentParamIamV2 = Arrays.asList(AGENT_HOLDER_RELEVANTDOCUMENT,
            AGENT_SUBMITTER_RELEVANTDOCUMENT);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaIndiceAipSerieUd(long idVerSerieDaElab, String creatingApplicationProducer) throws NamingException,
            ValidationException, JAXBException, MarshalException, NoSuchAlgorithmException, IOException {

        SerVerSerieDaElab verSerieDaElab = serieHelper.findById(SerVerSerieDaElab.class, idVerSerieDaElab);
        String versioneSerie = verSerieDaElab.getSerVerSerie().getCdVerSerie();
        String codiceSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getCdCompositoSerie();

        OrgStrut orgStrut = serieHelper.findById(OrgStrut.class, verSerieDaElab.getIdStrut().longValue());
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(orgStrut.getNmStrut());
        csv.setEnte(orgStrut.getOrgEnte().getNmEnte());
        csv.setAmbiente(orgStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente());

        // MEV #27080
        BigDecimal idAmbiente = BigDecimal.valueOf(orgStrut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        String sincroVersion = confHelper.getValoreParamApplicByAmb(CostantiDB.ParametroAppl.UNISINCRO_VERSION,
                idAmbiente);
        // end MEV #27080

        // sistema (new URN)
        String sistemaConservazione = confHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        csv.setSistemaConservazione(sistemaConservazione);

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Inizio creazione XML indice AIP per la versione serie "
                + verSerieDaElab.getSerVerSerie().getIdVerSerie());

        CreazioneIndiceAipSerieUdUtil indiceAipSerieUdUtil = new CreazioneIndiceAipSerieUdUtil();

        /* Data registrazione (sysdate) */
        Date dataRegistrazione = new GregorianCalendar().getTime();

        /**
         * GENERAZIONE INDICE AIP VERSIONE SERIE UD
         */
        StringWriter tmpWriter = null;
        OrgStrut strut = serieHelper.findById(OrgStrut.class, verSerieDaElab.getIdStrut());

        List<String> agentParamList = (!"2.0".equals(sincroVersion)) ? agentParam : agentParamV2;
        Map<String, String> mappaAgenti = confHelper.getParamApplicMapValue(agentParamList,
                BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                BigDecimal.valueOf(strut.getIdStrut()), null, null, CostantiDB.TipoAplVGetValAppart.STRUT);

        if (!"2.0".equals(sincroVersion)) {

            mappaAgenti = confHelper.getParamApplicMapValue(agentParam,
                    BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                    verSerieDaElab.getIdStrut(), null, null, CostantiDB.TipoAplVGetValAppart.STRUT);

            IdCType idc = indiceAipSerieUdUtil.generaIndiceAIPSerieUd(verSerieDaElab, dataRegistrazione, mappaAgenti,
                    creatingApplicationProducer);

            tmpWriter = marshallIdC(idc);
        } else {
            SIOrgEnteSiam orgEnteConvenz = null;
            if (strut.getIdEnteConvenz() != null) {
                orgEnteConvenz = ciasudHelper.findById(SIOrgEnteSiam.class, strut.getIdEnteConvenz());
            }
            if (orgEnteConvenz != null && orgEnteConvenz.getSiOrgAmbienteEnteConvenz() != null) {
                mappaAgenti.putAll(paramIamHelper.getParamApplicMapValue(agentParamIamV2,
                        BigDecimal.valueOf(orgEnteConvenz.getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()),
                        BigDecimal.valueOf(orgEnteConvenz.getIdEnteSiam()),
                        CostantiDB.TipoIamVGetValAppart.ENTECONVENZ));
            } else {
                log.warn("orgEnteConvenz o il suo ambiente è nullo per la struttura con id: {}", strut.getIdStrut());
                // Gestisci il caso di errore secondo la logica applicativa
            }
            PIndex pindex = creazioneIndiceAipSerieUdUtilV2.generaIndiceAIPSerieUd(verSerieDaElab, dataRegistrazione,
                    mappaAgenti, creatingApplicationProducer);

            tmpWriter = marshallPIndex(pindex);
        }

        // MEV#30400

        BackendStorage backendIndiciAip = objectStorageService.lookupBackendIndiciAipSerieUD(orgStrut.getIdStrut());

        boolean putOnOs = true;
        if (objectStorageService.isSerFileVerSerieUDOnOs(verSerieDaElab.getSerVerSerie().getIdVerSerie(),
                CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name())) {
            String md5LocalContent = calculateMd5AsBase64(tmpWriter.toString());
            String eTagFromObjectMetadata = objectStorageService
                    .getObjectMetadataIndiceAipSerieUD(verSerieDaElab.getSerVerSerie().getIdVerSerie(),
                            CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name())
                    .eTag();

            if (md5LocalContent.equals(eTagFromObjectMetadata)) {
                putOnOs = false;
            }
        }

        /* Persisto il file dell'indice AIP versione serie UD */
        // DATO CHE L'URN DEL FILE è SU UNA TABELLA SEPARATA SONO OBBLIGATO A CREARE SEMPRE IL RECORD SULLA
        // SerFileVerSerie
        SerFileVerSerie serFileVerSerie = serieHelper.storeFileIntoSerFileVerSerie(
                verSerieDaElab.getSerVerSerie().getIdVerSerie(), CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name(),
                tmpWriter.toString().getBytes("UTF-8"), sincroVersion, verSerieDaElab.getIdStrut(), dataRegistrazione,
                putOnOs, null);

        // EVO#16492
        /* Calcolo e persisto urn dell'indice AIP della serie */
        serieHelper.storeSerUrnFileVerSerie(serFileVerSerie, csv, codiceSerie, versioneSerie);
        // end EVO#16492

        ObjectStorageResource indiceAipSuOS;

        if (putOnOs) {
            indiceAipSuOS = objectStorageService.createResourcesInIndiciAipSerieUD(serFileVerSerie,
                    backendIndiciAip.getBackendName(), tmpWriter.toString().getBytes("UTF-8"),
                    verSerieDaElab.getSerVerSerie().getIdVerSerie(), verSerieDaElab.getIdStrut(), csv, codiceSerie,
                    versioneSerie);
            log.debug(LOG_SALVATAGGIO_OS, indiceAipSuOS.getBucket(), indiceAipSuOS.getKey());
        }

        // end MEV#30400

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Fine creazione XML indice AIP per la versione serie "
                + verSerieDaElab.getSerVerSerie().getIdVerSerie());

        // Definisco la data di assunzione del nuovo stato pari a data corrente
        verSerieDaElab.setDtRegStatoVerSerie(dataRegistrazione);

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Registro il nuovo STATO della VERSIONE della SERIE: DA_FIRMARE");
        BigDecimal pgStatoVerSerie = ciasudHelper
                .getUltimoProgressivoSerStatoVerSerie(verSerieDaElab.getSerVerSerie().getIdVerSerie());
        Long idUserIam = userHelper.findIamUser(
                confHelper.getValoreParamApplicByStrut(CostantiDB.ParametroAppl.USERID_CREAZIONE_IX_AIP_SERIE,
                        idAmbiente, BigDecimal.valueOf(strut.getIdStrut())))
                .getIdUserIam();

        SerStatoVerSerie statoVerSerie = serieEjb.createSerStatoVerSerie(pgStatoVerSerie.add(BigDecimal.ONE),
                CostantiDB.StatoVersioneSerie.DA_FIRMARE.name(), "Creazione indice AIP serie", null, idUserIam,
                new Date(), verSerieDaElab.getSerVerSerie().getIdVerSerie());
        // Aggiorna l'identificatore dello stato corrente della versione
        // della serie assegnando l'identificatore dello stato inserito
        serieHelper.insertEntity(statoVerSerie, false);
        verSerieDaElab.getSerVerSerie().setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Registro il nuovo STATO della SERIE: AIP_GENERATO");
        Long idSerie = verSerieDaElab.getSerVerSerie().getSerSerie().getIdSerie();
        BigDecimal pgStatoSerie = ciasudHelper.getUltimoProgressivoSerStatoSerie(idSerie);
        SerStatoSerie statoSerie = serieEjb.createSerStatoSerie(pgStatoSerie.add(BigDecimal.ONE),
                CostantiDB.StatoConservazioneSerie.AIP_GENERATO.name(), "Creazione indice AIP serie", null, idUserIam,
                new Date(), idSerie);
        // Aggiorna l'identificatore dello stato corrente della serie
        // assegnando l'identificatore dello stato inserito
        serieHelper.insertEntity(statoSerie, false);
        verSerieDaElab.getSerVerSerie().getSerSerie().setIdStatoSerieCor(new BigDecimal(statoSerie.getIdStatoSerie()));

        log.info(ElaborazioneRigaIndiceAipVersioneSerieUd.class.getSimpleName()
                + " --- Creazione Indice Aip Versione Serie Ud --- "
                + "Aggiorno lo STATO della SERIE DA ELABORARE: DA_FIRMARE");
        // Aggiorno lo stato della versione serie da elaborare assegnando stato DA_FIRMARE
        verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name());
    }

    // MEV#30400
    private String calculateMd5AsBase64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    // end MEV#30400

    private StringWriter marshallIdC(IdCType idc) throws ValidationException, JAXBException, MarshalException {
        it.eng.parer.ws.xml.usmainResp.ObjectFactory objFct_IdCType = new it.eng.parer.ws.xml.usmainResp.ObjectFactory();
        JAXBElement<IdCType> element_IdCType = objFct_IdCType.createIdC(idc);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getVersRespUniSincroCtx_IdC_Serie().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfVersRespUniSincro());
        tmpMarshaller.marshal(element_IdCType, tmpWriter);
        return tmpWriter;
    }

    private StringWriter marshallPIndex(PIndex pindex) throws JAXBException {
        it.eng.parer.ws.xml.usmainRespV2.ObjectFactory objFctPIndex = new it.eng.parer.ws.xml.usmainRespV2.ObjectFactory();
        JAXBElement<PIndex> elementPIndex = objFctPIndex.createPIndex(pindex);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getVersRespUniSincroCtx_PIndex_Serie().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfVersRespUniSincroV2());
        tmpMarshaller.marshal(elementPIndex, tmpWriter);
        return tmpWriter;
    }

}
