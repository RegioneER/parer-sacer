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

package it.eng.parer.objectstorage.ejb;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.parer.entity.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.inheritance.oop.AroXmlObjectStorage;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipOutputStream;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.Md5Utils;
import it.eng.spagoCore.util.UUIDMdcLogUtil;
import java.io.ByteArrayInputStream;

@Stateless(mappedName = "ObjectStorageService")
@LocalBean
public class ObjectStorageService {

    private final Logger log = LoggerFactory.getLogger(ObjectStorageService.class);

    private static final String PATTERN_FORMAT = "yyyyMMdd/HH/mm";

    private static final String STAGING_R = "READ_STAGING";
    private static final String COMPONENTI_R = "READ_COMPONENTI";
    private static final String SIP_R = "READ_SIP";
    // MEV#29089
    private static final String METADATI_AGG_MD_R = "READ_AGG_MD";
    private static final String SES_AGG_MD_ERR_KO_R = "READ_SESSIONI_AGG_MD_ERR_KO";
    // end MEV#29089
    // MEV#29090
    private static final String METADATI_FASC_R = "READ_FASCICOLI";
    private static final String SES_FASC_ERR_KO_R = "READ_SESSIONI_FASC_ERR_KO";
    // end MEV#29090
    private static final String REPORTVF_R = "READ_REPORTVF";
    // MEV#30395
    private static final String READ_INDICI_AIP = "READ_INDICI_AIP";
    private static final String WRITE_INDICI_AIP = "WRITE_INDICI_AIP";
    // end MEV#30395
    // MEV#30397
    private static final String READ_ELV_IX_AIP = "READ_ELV_IX_AIP";
    private static final String WRITE_ELV_IX_AIP = "WRITE_ELV_IX_AIP";
    // end MEV#30397
    // MEV#30398
    private static final String READ_INDICI_AIP_FASCICOLI = "READ_INDICI_AIP_FASCICOLI";
    private static final String WRITE_INDICI_AIP_FASCICOLI = "WRITE_INDICI_AIP_FASCICOLI";
    // end MEV#30398
    // MEV#30399
    private static final String READ_ELV_IX_AIP_FASCICOLI = "READ_ELV_IX_AIP_FASCICOLI";
    private static final String WRITE_ELV_IX_AIP_FASCICOLI = "WRITE_ELV_IX_AIP_FASCICOLI";
    // end MEV#30399
    private static final int BUFFER_SIZE = 10 * 1024 * 1024;

    // MEV#30400
    private static final String WRITE_INDICI_AIP_SERIE_UD = "WRITE_INDICI_AIP_SERIE_UD";
    private static final String READ_INDICI_AIP_SERIE_UD = "READ_INDICI_AIP_SERIE_UD";
    // end MEV#30400

    @EJB
    private SalvataggioBackendHelper salvataggioBackendHelper;

    /**
     * Effettua il lookup per stabilire come sia configurato il backend
     *
     * @param idTipoUnitaDoc
     *            id tipologia di unità documentaria
     * @param paramName
     *            nome del parametro
     *
     * @return Tipologia di backend. Al momento sono supportate
     *         {@link it.eng.parer.objectstorage.dto.BackendStorage.STORAGE_TYPE#BLOB} e
     *         {@link it.eng.parer.objectstorage.dto.BackendStorage.STORAGE_TYPE#OS}
     */
    public BackendStorage lookupBackend(long idTipoUnitaDoc, String paramName) {
        try {

            String tipoBackend = salvataggioBackendHelper.getBackendByParamName(idTipoUnitaDoc, paramName);
            return salvataggioBackendHelper.getBackend(tipoBackend);

        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    // MEV#30397
    /**
     * Effettua la lookup per stabilire come sia configurato il backend per gli elenchi indici aip
     *
     * @param idStrut
     *            id struttura
     *
     * @return tipologia di backend.
     */
    public BackendStorage lookupBackendElenchiIndiciAip(long idStrut) {
        try {
            String tipoBackend = salvataggioBackendHelper.getBackendElenchiIndiciAip(idStrut);

            // tipo backend
            return salvataggioBackendHelper.getBackend(tipoBackend);

        } catch (Exception e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#30397

    // MEV#30400
    /**
     * Effettua la lookup per stabilire come sia configurato il backend per gli indici aip delle serie di ud
     *
     * @param idStrut
     *            id struttura
     *
     * @return tipologia di backend.
     */
    public BackendStorage lookupBackendIndiciAipSerieUD(long idStrut) {
        try {
            String tipoBackend = salvataggioBackendHelper.getBackendIndiciAipSerieUD(idStrut);

            // tipo backend
            return salvataggioBackendHelper.getBackend(tipoBackend);

        } catch (Exception e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#30400

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati}
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipInStaging(long idSessioneVers) {
        try {
            VrsXmlDatiSesObjectStorageKo xmlVersamento = salvataggioBackendHelper.getLinkXmlDatiSesOs(idSessioneVers);
            if (!Objects.isNull(xmlVersamento)) {
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfiguration(xmlVersamento.getDecBackend().getNmBackend(), STAGING_R);
                ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                        xmlVersamento.getNmBucket(), xmlVersamento.getNmKeyFile());
                return unzip(object);
            } else {
                return Collections.emptyMap();
            }
        } catch (IOException | ObjectStorageException e) {
            log.error("Errore recupero idSessioneVers {}", idSessioneVers, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati} per l'unita documentaria
     *
     * @param idUnitaDoc
     *            id unita documentaria
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipUnitaDoc(long idUnitaDoc) {
        try {

            return getObjectSip(salvataggioBackendHelper.getLinkSipUnitaDocOs(idUnitaDoc));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati} per il documento
     *
     * @param idDoc
     *            id documento
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipDoc(long idDoc) {
        try {
            return getObjectSip(salvataggioBackendHelper.getLinkSipDocOs(idDoc));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectSip(AroXmlObjectStorage xmlObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(xmlObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(xmlObjectStorage.getDecBackend().getNmBackend(), SIP_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    xmlObjectStorage.getNmBucket(), xmlObjectStorage.getCdKeyFile());
            return unzip(object);
        } else {
            return Collections.emptyMap();
        }
    }

    // MEV#29089
    /**
     * Ottieni, in una mappa, la lista degli xml di versamento classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati} per l'aggiornamento metadati
     *
     * @param idUpdUnitaDoc
     *            id aggiornamento metadati
     *
     * @return mappa degli XML di versamento aggiornamento metadati
     */
    public Map<String, String> getObjectXmlVersAggMd(long idUpdUnitaDoc) {
        try {

            return getObjectSipAggMd(salvataggioBackendHelper.getLinkSipAggMdOs(idUpdUnitaDoc));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectSipAggMd(AroXmlUpdUdObjectStorage xmlUpdUdObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(xmlUpdUdObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    xmlUpdUdObjectStorage.getDecBackend().getNmBackend(), METADATI_AGG_MD_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    xmlUpdUdObjectStorage.getNmBucket(), xmlUpdUdObjectStorage.getCdKeyFile());
            return unzip(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml dei dati specifici aggiornati classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiUsoDatiSpec} per l'aggiornamento metadati
     *
     * @param idEntitaSacerUpd
     *            id aggiornamento metadati
     * @param tiEntitaSacerUpd
     *            tipo entità aggiornamento metadati
     *
     * @return mappa degli XML dei dati specifici aggiornati
     */
    public Map<String, String> getObjectXmlUpdDatiSpecAggMd(long idEntitaSacerUpd,
            TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacerUpd) {
        try {

            return getObjectUpdDatiSpecAggMd(
                    salvataggioBackendHelper.getLinkUpdDatiSpecAggMdOs(idEntitaSacerUpd, tiEntitaSacerUpd));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectUpdDatiSpecAggMd(AroUpdDatiSpecUdObjectStorage updDatiSpecObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(updDatiSpecObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    updDatiSpecObjectStorage.getDecBackend().getNmBackend(), METADATI_AGG_MD_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    updDatiSpecObjectStorage.getNmBucket(), updDatiSpecObjectStorage.getCdKeyFile());
            return unzipDatiSpecAggMd(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml dei dati specifici relativi ai metadati iniziali classificati nelle
     * tipologie definite qui {@link it.eng.parer.ws.utils.CostantiDB.TipiUsoDatiSpec} per l'aggiornamento metadati
     *
     * @param idEntitaSacerVersIni
     *            id versamento iniziale
     * @param tiEntitaSacerVersIni
     *            tipo entità versamento iniziale
     *
     * @return mappa degli XML dei dati specifici relativi ai metadati iniziali
     */
    public Map<String, String> getObjectXmlVersIniDatiSpecAggMd(long idEntitaSacerVersIni,
            TiEntitaSacerAroVersIniDatiSpec tiEntitaSacerVersIni) {
        try {

            return getObjectVersIniDatiSpecAggMd(
                    salvataggioBackendHelper.getLinkVersIniDatiSpecAggMdOs(idEntitaSacerVersIni, tiEntitaSacerVersIni));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectVersIniDatiSpecAggMd(
            AroVersIniDatiSpecObjectStorage versIniDatiSpecObjectStorage) throws ObjectStorageException, IOException {
        if (!Objects.isNull(versIniDatiSpecObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    versIniDatiSpecObjectStorage.getDecBackend().getNmBackend(), METADATI_AGG_MD_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    versIniDatiSpecObjectStorage.getNmBucket(), versIniDatiSpecObjectStorage.getCdKeyFile());
            return unzipDatiSpecAggMd(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento aggiornamento metadati fallito classificati nelle
     * tipologie definite qui {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati}
     *
     * @param idSesUpdUnitaDocKo
     *            id sessione di versamento aggiornamento metadati fallita
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipAggMdFallito(long idSesUpdUnitaDocKo) {
        try {
            VrsXmlSesUpdUdKoObjectStorage xmlVersUpdUdKo = salvataggioBackendHelper
                    .getLinkXmlSesAggMdKoOs(idSesUpdUnitaDocKo);
            if (!Objects.isNull(xmlVersUpdUdKo)) {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                        xmlVersUpdUdKo.getDecBackend().getNmBackend(), SES_AGG_MD_ERR_KO_R);
                ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                        xmlVersUpdUdKo.getNmBucket(), xmlVersUpdUdKo.getCdKeyFile());
                return unzip(object);
            } else {
                return Collections.emptyMap();
            }
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento aggiornamento metadati errato classificati nelle
     * tipologie definite qui {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati}
     *
     * @param idSesUpdUnitaDocErr
     *            id sessione di versamento aggiornamento metadati errata
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipAggMdErrato(long idSesUpdUnitaDocErr) {
        try {
            VrsXmlSesUpdUdErrObjectStorage xmlVersUpdUdErr = salvataggioBackendHelper
                    .getLinkXmlSesAggMdErrOs(idSesUpdUnitaDocErr);
            if (!Objects.isNull(xmlVersUpdUdErr)) {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                        xmlVersUpdUdErr.getDecBackend().getNmBackend(), SES_AGG_MD_ERR_KO_R);
                ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                        xmlVersUpdUdErr.getNmBucket(), xmlVersUpdUdErr.getCdKeyFile());
                return unzip(object);
            } else {
                return Collections.emptyMap();
            }
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> unzipDatiSpecAggMd(ResponseInputStream<GetObjectResponse> inputStream)
            throws IOException {
        // TipiXmlDati
        final String xml = ".xml";
        final Map<String, String> xmlDatiSpec = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream);) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String value = IOUtils.toString(zis, StandardCharsets.UTF_8);
                if (ze.getName().equals(CostantiDB.TipiUsoDatiSpec.VERS.name() + xml)) {
                    xmlDatiSpec.put(CostantiDB.TipiUsoDatiSpec.VERS.name(), value);
                } else if (ze.getName().equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name() + xml)) {
                    xmlDatiSpec.put(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name(), value);
                } else {
                    log.warn(
                            "Attenzione, l'entry con nome {} non è stata riconosciuta nel file zip dei Dati Specifici dell'aggiornamento metadati",
                            ze.getName());
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        return xmlDatiSpec;
    }
    // end MEV#29089

    // MEV#29090
    /**
     * Ottieni, in una mappa, la lista degli xml di versamento classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati} per il fascicolo
     *
     * @param idFascicolo
     *            id fascicolo
     *
     * @return mappa degli XML di versamento fascicolo
     */
    public Map<String, String> getObjectXmlVersFascicolo(long idFascicolo) {
        try {

            return getObjectSipFasc(salvataggioBackendHelper.getLinkSipFascicoloOs(idFascicolo));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectSipFasc(FasXmlVersFascObjectStorage xmlVersFascObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(xmlVersFascObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    xmlVersFascObjectStorage.getDecBackend().getNmBackend(), METADATI_FASC_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    xmlVersFascObjectStorage.getNmBucket(), xmlVersFascObjectStorage.getCdKeyFile());
            return unzip(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml dei profili classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TiModelloXsdProfilo} per il fascicolo
     *
     * @param idFascicolo
     *            id fascicolo
     *
     * @return mappa degli XML di profilo fascicolo
     */
    public Map<String, String> getObjectXmlFascicolo(long idFascicolo) {
        try {
            return getObjectMetaProfFasc(
                    (FasXmlFascObjectStorage) salvataggioBackendHelper.getLinkMetaProfFascicoloOs(idFascicolo));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> getObjectMetaProfFasc(FasXmlFascObjectStorage xmlFascObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(xmlFascObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    xmlFascObjectStorage.getDecBackend().getNmBackend(), METADATI_FASC_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    xmlFascObjectStorage.getNmBucket(), xmlFascObjectStorage.getCdKeyFile());
            return unzipProfiliFascicolo(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento fascicolo fallito classificati nelle tipologie definite
     * qui {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati}
     *
     * @param idSesFascicoloKo
     *            id sessione di versamento fascicolo fallita
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipFascFallito(long idSesFascicoloKo) {
        try {
            VrsXmlSesFascKoObjectStorage xmlVersFascKo = salvataggioBackendHelper
                    .getLinkXmlSesFascKoOs(idSesFascicoloKo);
            if (!Objects.isNull(xmlVersFascKo)) {
                ObjectStorageBackend config = salvataggioBackendHelper
                        .getObjectStorageConfiguration(xmlVersFascKo.getDecBackend().getNmBackend(), SES_FASC_ERR_KO_R);
                ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                        xmlVersFascKo.getNmBucket(), xmlVersFascKo.getCdKeyFile());
                return unzip(object);
            } else {
                return Collections.emptyMap();
            }
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni, in una mappa, la lista degli xml di versamento fascicolo errato classificati nelle tipologie definite
     * qui {@link it.eng.parer.ws.utils.CostantiDB.TipiXmlDati}
     *
     * @param idSesFascicoloErr
     *            id sessione di versamento fascicolo errata
     *
     * @return mappa degli XML
     */
    public Map<String, String> getObjectSipFascErrato(long idSesFascicoloErr) {
        try {
            VrsXmlSesFascErrObjectStorage xmlVersFascErr = salvataggioBackendHelper
                    .getLinkXmlSesFascErrOs(idSesFascicoloErr);
            if (!Objects.isNull(xmlVersFascErr)) {
                ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                        xmlVersFascErr.getDecBackend().getNmBackend(), SES_FASC_ERR_KO_R);
                ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                        xmlVersFascErr.getNmBucket(), xmlVersFascErr.getCdKeyFile());
                return unzip(object);
            } else {
                return Collections.emptyMap();
            }
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#29090

    /**
     * Ottieni lo stream del componente contenuto nell'object storage
     *
     * @param idCompDoc
     *            id del componente
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     */
    public void getObjectComponente(long idCompDoc, OutputStream outputStream) {
        try {
            AroCompObjectStorage link = salvataggioBackendHelper.getLinkCompDocOs(idCompDoc);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), COMPONENTI_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni lo stream del report di verifica contenuto nell'object storage
     *
     * @param idCompDoc
     *            id del componente
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     */
    public void getObjectReportvf(long idCompDoc, OutputStream outputStream) {
        try {
            FirReport link = salvataggioBackendHelper.getLinkReportVerificaFirma(idCompDoc);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), REPORTVF_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);

        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni lo stream del versamento fallito contenuto nell'object storage
     *
     * @param idFileSessione
     *            id file sessione
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     * @return true oggetto recuperato da bucket / false altrimenti
     *
     */
    public boolean getObjectComponenteInStaging(long idFileSessione, OutputStream outputStream) {
        boolean result = true;
        try {
            VrsFileSesObjectStorageKo link = salvataggioBackendHelper.getLinkVersamentoFallito(idFileSessione);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), STAGING_R);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getNmKeyFile());
            IOUtils.copyLarge(object, outputStream);

        } catch (IOException | ObjectStorageException e) {
            log.error("Errore recupero idFileSessione {}", idFileSessione, e);
            result = false;
        }

        return result;
    }

    // MEV#30395
    /**
     * Ottieni lo stream dell'indice aip contenuto nell'object storage
     *
     * @param idVerIndiceAip
     *            id della versione dell'indice aip
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     */
    public void getObjectIndiceAipUd(long idVerIndiceAip, OutputStream outputStream) {
        try {
            AroVerIndiceAipUdObjectStorage link = salvataggioBackendHelper.getLinkAroVerIndiceAipUdOs(idVerIndiceAip);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#30395

    // MEV#30397
    /**
     * Ottieni lo stream dell'elenco indici aip contenuto nell'object storage
     *
     * @param idFileElencoVers
     *            id del file dell'elenco indici aip
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     */
    public void getObjectElencoIndiciAip(long idFileElencoVers, OutputStream outputStream) {
        try {
            ElvFileElencoVersObjectStorage link = salvataggioBackendHelper.getLinkElvFileElencoVersOs(idFileElencoVers);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_ELV_IX_AIP);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni, sotto forma di byte array, il file dell'elenco indici aip contenuto nell'object storage
     *
     * @param idFileElencoVers
     *            id file elenco versamento
     *
     * @return file XML dell'elenco indici aip
     */
    public byte[] getObjectElencoIndiciAip(long idFileElencoVers) {
        try {

            return getObjectFileElencoIxAip(salvataggioBackendHelper.getLinkElvFileElencoVersOs(idFileElencoVers));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private byte[] getObjectFileElencoIxAip(ElvFileElencoVersObjectStorage fileElencoVersObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(fileElencoVersObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    fileElencoVersObjectStorage.getDecBackend().getNmBackend(), READ_ELV_IX_AIP);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    fileElencoVersObjectStorage.getNmBucket(), fileElencoVersObjectStorage.getCdKeyFile());
            return IOUtils.toByteArray(object);
        } else {
            return IOUtils.byteArray();
        }
    }
    // end MEV#30397

    /**
     * Controlla se il versamento fallito sia o meno stato registrato sull'object storage indipendemente dal valore del
     * parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idFileSessioneKo
     *            id file sessione
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isComponenteFallitoOnOs(long idFileSessioneKo) {
        try {
            return salvataggioBackendHelper.existFileSesObjectStorage(idFileSessioneKo);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Controlla se il componente sia o meno stato registrato sull'object storage indipendemente dal valore del
     * parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idCompDoc
     *            id componente documento
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isComponenteOnOs(long idCompDoc) {
        try {
            return salvataggioBackendHelper.existComponenteObjectStorage(idCompDoc);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * * Controlla se il report verifica firma sia o meno stato registrato sull'object storage indipendemente dal valore
     * del parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idCompDoc
     *            id componente documento
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isReportvfOnOsByIdCompDoc(long idCompDoc) {
        try {
            return salvataggioBackendHelper.existReportvfObjectStorage(idCompDoc);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> unzip(ResponseInputStream<GetObjectResponse> inputStream) throws IOException {
        // TipiXmlDati
        final String xml = ".xml";
        final Map<String, String> xmlVers = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream);) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String value = IOUtils.toString(zis, StandardCharsets.UTF_8);
                if (ze.getName().equals(CostantiDB.TipiXmlDati.RICHIESTA + xml)) {
                    xmlVers.put(CostantiDB.TipiXmlDati.RICHIESTA, value);
                } else if (ze.getName().equals(CostantiDB.TipiXmlDati.RISPOSTA + xml)) {
                    xmlVers.put(CostantiDB.TipiXmlDati.RISPOSTA, value);
                } else if (ze.getName().equals(CostantiDB.TipiXmlDati.RAPP_VERS + xml)) {
                    xmlVers.put(CostantiDB.TipiXmlDati.RAPP_VERS, value);
                } else if (ze.getName().equals(CostantiDB.TipiXmlDati.INDICE_FILE + xml)) {
                    xmlVers.put(CostantiDB.TipiXmlDati.INDICE_FILE, value);
                } else {
                    log.warn(
                            "Attenzione, l'entry con nome {} non è stata riconosciuta nel file zip dei SIP di versamento",
                            ze.getName());
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        return xmlVers;
    }

    private Map<String, String> unzipProfiliFascicolo(ResponseInputStream<GetObjectResponse> inputStream)
            throws IOException {
        // TipiXmlDati
        final String xml = ".xml";
        final Map<String, String> xmlProf = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream);) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String value = IOUtils.toString(zis, StandardCharsets.UTF_8);
                if (ze.getName().equals(CostantiDB.TiModelloXsdProfilo.PROFILO_GENERALE_FASCICOLO.name() + xml)) {
                    xmlProf.put(CostantiDB.TiModelloXsdProfilo.PROFILO_GENERALE_FASCICOLO.name(), value);
                } else if (ze.getName()
                        .equals(CostantiDB.TiModelloXsdProfilo.PROFILO_NORMATIVO_FASCICOLO.name() + xml)) {
                    xmlProf.put(CostantiDB.TiModelloXsdProfilo.PROFILO_NORMATIVO_FASCICOLO.name(), value);
                } else if (ze.getName()
                        .equals(CostantiDB.TiModelloXsdProfilo.PROFILO_ARCHIVISTICO_FASCICOLO.name() + xml)) {
                    xmlProf.put(CostantiDB.TiModelloXsdProfilo.PROFILO_ARCHIVISTICO_FASCICOLO.name(), value);
                } else if (ze.getName()
                        .equals(CostantiDB.TiModelloXsdProfilo.PROFILO_SPECIFICO_FASCICOLO.name() + xml)) {
                    xmlProf.put(CostantiDB.TiModelloXsdProfilo.PROFILO_SPECIFICO_FASCICOLO.name(), value);
                } else {
                    log.warn(
                            "Attenzione, l'entry con nome {} non è stata riconosciuta nel file zip dei profili del fascicolo",
                            ze.getName());
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        return xmlProf;
    }

    // MEV#30397
    /**
     * Salva il contenuto nel bucket degli Elenchi Indici AIP
     *
     * @param urn
     *            urn
     * @param nomeBackend
     *            backend configurato (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param contenuto
     *            byte array da salvare
     * @param idFileElencoVers
     *            id file elenco indice aip
     * @param idStrut
     *            id struttura
     *
     * @return risorsa su OS che identifica il contenuto caricato
     */
    public ObjectStorageResource createResourcesInElenchiIndiciAip(final String urn, String nomeBackend,
            byte[] contenuto, long idFileElencoVers, BigDecimal idStrut) {
        try {
            ObjectStorageBackend configuration = salvataggioBackendHelper.getObjectStorageConfiguration(nomeBackend,
                    WRITE_ELV_IX_AIP);

            // generate std tag
            Set<Tag> tags = new HashSet<>();

            // create key
            final String estensione = urn.toUpperCase().contains("MARCA") ? ".tsr" : ".xml.p7m";
            final String destKey = createRandomKey(urn) + estensione;

            // put on O.S. + save link
            try (ByteArrayInputStream bais = new ByteArrayInputStream(contenuto)) {

                ObjectStorageResource savedFile = salvataggioBackendHelper.putObject(bais, contenuto.length, destKey,
                        configuration, Optional.empty(), Optional.of(tags),
                        Optional.of(Md5Utils.md5AsBase64(contenuto)));
                log.debug("Salvato file {}/{}", savedFile.getBucket(), savedFile.getKey());
                // link
                salvataggioBackendHelper.saveObjectStorageLinkElencoIndiceAip(savedFile, nomeBackend, idFileElencoVers,
                        idStrut);
                return savedFile;
            }

        } catch (ObjectStorageException | IOException ex) {
            throw new EJBException(ex);
        }
    }

    /**
     * Crea una chiave utilizzando i seguenti elementi separati dal carattere <code>/</code>:
     * <ul>
     * <li>data in formato anno mese giorno (per esempio <strong>20221124</strong>)</li>
     * <li>ora a due cifre (per esempio <strong>14</strong>)</li>
     * <li>minuto a due cifre (per esempio <strong>05</strong>)</li>
     * <li>URN</li>
     * <li>UUID generato runtime <strong>28fd282d-fbe6-4528-bd28-2dfbe685286f</strong>) per ogni oggetto caricato</li>
     * </ul>
     *
     * Esempio di chiave completa:
     * <code>20221124/14/05/550e8400-e29b-41d4-a716-446655440000/28fd282d-fbe6-4528-bd28-2dfbe685286f</code>
     *
     * @return chiave dell'oggetto
     */
    private static String createRandomKey(final String urn) {

        String when = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault())
                .format(Instant.now());

        return when + "/" + urn + "/" + UUID.randomUUID().toString();
    }

    /**
     * Controlla se l'elenco indici aip sia o meno stato registrato sull'object storage indipendentemente dal valore del
     * parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idFileElencoVers
     *            id file elenco indice aip
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isElencoIndiciAipOnOs(long idFileElencoVers) {
        try {
            return salvataggioBackendHelper.existElencoIndiciAipObjectStorage(idFileElencoVers);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#30397

    // MEV#30395
    /**
     * Salva il contenuto nel bucket degli Indici AIP per l'unita documentaria
     *
     * @param nomeBackend
     *            backend configurato (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param contenuto
     *            contenuto da salvare
     * @param idVerIndiceAip
     *            id versione indice aip
     * @param idSubStrut
     *            id sotto struttura
     * @param aaKeyUnitaDoc
     *            anno chiave unità documentaria
     *
     * @return risorsa su OS che identifica il contenuto caricato
     */
    public ObjectStorageResource createResourcesInIndiciAipUnitaDoc(String nomeBackend, String contenuto,
            long idVerIndiceAip, BigDecimal idSubStrut, BigDecimal aaKeyUnitaDoc) {
        try {
            ObjectStorageBackend configuration = salvataggioBackendHelper.getObjectStorageConfiguration(nomeBackend,
                    WRITE_INDICI_AIP);

            // generate std tag
            Set<Tag> tags = new HashSet<>();

            final String destKey = salvataggioBackendHelper.generateKeyIndiceAip(idVerIndiceAip);

            // put on O.S.
            ObjectStorageResource savedFile = salvataggioBackendHelper.putObject(contenuto, destKey, configuration,
                    Optional.empty(), Optional.of(tags), Optional.of(calculateMd5AsBase64(contenuto)));

            log.debug("Salvato file {}/{}", savedFile.getBucket(), savedFile.getKey());
            // link
            salvataggioBackendHelper.saveObjectStorageLinkIndiceAipUd(savedFile, nomeBackend, idVerIndiceAip,
                    idSubStrut, aaKeyUnitaDoc);
            return savedFile;
        } catch (ObjectStorageException ex) {
            throw new EJBException(ex);
        }
    }

    /**
     * Calcola il message digest MD5 (base64 encoded) del dato da inviare via S3
     *
     * Nota: questa scelta deriva dal modello supportato dal vendor
     * (https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html) *
     *
     * @param str
     *            contenuto
     *
     * @return rappresentazione base64 del contenuto calcolato
     *
     * @throws NoSuchAlgorithmException
     *             errore generico
     * @throws IOException
     *             errore generico
     */
    private String calculateMd5AsBase64(String str) {
        return Md5Utils.md5AsBase64(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Controlla se l'indice aip sia o meno stato registrato sull'object storage indipendemente dal valore del parametro
     * (il pregresso potrebbe ancora essere su DB).
     *
     * @param idVerIndiceAip
     *            id versione indice aip
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isIndiceAipOnOs(long idVerIndiceAip) {
        try {
            return salvataggioBackendHelper.existIndiceAipObjectStorage(idVerIndiceAip);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * ###### NB: NON USARE SU O.S. NETAPP ######
     *
     * Ottieni gli attributi dell'oggetto dell'indice aip contenuto nell'object storage
     *
     * @param idVerIndiceAip
     *            id della versione dell'indice aip
     *
     * @return attributi dell'oggetto su O.s.
     */
    public GetObjectAttributesResponse getObjectAttributesIndiceAipUd(long idVerIndiceAip) {
        try {
            AroVerIndiceAipUdObjectStorage link = salvataggioBackendHelper.getLinkAroVerIndiceAipUdOs(idVerIndiceAip);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP);
            return salvataggioBackendHelper.getObjectAttributes(config, link.getNmBucket(), link.getCdKeyFile());
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni i metadati dell'oggetto dell'indice aip contenuto nell'object storage
     *
     * @param idVerIndiceAip
     *            id della versione dell'indice aip
     *
     * @return attributi dell'oggetto su O.s.
     */
    public HeadObjectResponse getObjectMetadataIndiceAipUd(long idVerIndiceAip) {
        try {
            AroVerIndiceAipUdObjectStorage link = salvataggioBackendHelper.getLinkAroVerIndiceAipUdOs(idVerIndiceAip);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP);
            return salvataggioBackendHelper.getObjectMetadata(config, link.getNmBucket(), link.getCdKeyFile());
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }
    // end MEV#30395

    // MEV #30398
    /**
     * Salva il file unico indice AIP fascicolo nel bucket degli indici AIP fascicoli
     *
     * @param urn
     *            urn normalizzato
     * @param nomeBackend
     *            backend configurato (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param xmlFiles
     *            files Xml da salvare (previa creazione file zip)
     * @param idVerAipFascicolo
     *            id versione aip fascicolo
     * @param idStrut
     *            id della struttura versante
     *
     * @return risorsa su OS che identifica il file caricato
     */
    public ObjectStorageResource createResourcesInIndiciAipFasc(final String urn, String nomeBackend,
            Map<String, String> xmlFiles, long idVerAipFascicolo, BigDecimal idStrut) {
        try {
            ObjectStorageBackend configuration = salvataggioBackendHelper.getObjectStorageConfiguration(nomeBackend,
                    WRITE_INDICI_AIP_FASCICOLI);
            // put on O.S.
            ObjectStorageResource savedFile = createSipXmlMapAndPutOnBucket(urn, xmlFiles, configuration,
                    SetUtils.emptySet());
            // link
            salvataggioBackendHelper.saveObjectStorageLinkIndiceAipFasc(savedFile, nomeBackend, idVerAipFascicolo,
                    idStrut);
            return savedFile;
        } catch (ObjectStorageException | IOException | NoSuchAlgorithmException ex) {
            throw new EJBException(ex);
        }
    }

    private ObjectStorageResource createSipXmlMapAndPutOnBucket(final String urn, Map<String, String> xmlFiles,
            ObjectStorageBackend configuration, Set<Tag> tags)
            throws IOException, ObjectStorageException, NoSuchAlgorithmException {
        ObjectStorageResource savedFile = null;
        Path tempZip = Files.createTempFile("aip_fasc-", ".zip");
        //
        try (InputStream is = Files.newInputStream(tempZip)) {
            // create key
            final String key;
            if (StringUtils.isBlank(urn)) {
                key = createRandomKey() + ".zip";
            } else {
                key = createRandomKey(urn) + ".zip";
            }
            // create zip file
            createZipFile(xmlFiles, tempZip);
            // put on O.S.
            savedFile = salvataggioBackendHelper.putObject(is, Files.size(tempZip), key, configuration,
                    Optional.empty(), Optional.of(tags), Optional.of(calculateFileBase64(tempZip)));
            log.debug("Salvato file {}/{}", savedFile.getBucket(), savedFile.getKey());
        } finally {
            if (tempZip != null) {
                Files.delete(tempZip);
            }
        }

        return savedFile;
    }

    /**
     * Crea una chiave utilizzando i seguenti elementi separati dal carattere <code>/</code>:
     * <ul>
     * <li>data in formato anno mese giorno (per esempio <strong>20221124</strong>)</li>
     * <li>ora a due cifre (per esempio <strong>14</strong>)</li>
     * <li>minuto a due cifre (per esempio <strong>05</strong>)</li>
     * <li>UUID sessione di versamento <strong>550e8400-e29b-41d4-a716-446655440000</strong>) recuperato dall'MDC ossia
     * dal Mapped Diagnostic Context (se non esiste viene generato comunque un UUID)</li>
     * <li>UUID generato runtime <strong>28fd282d-fbe6-4528-bd28-2dfbe685286f</strong>) per ogni oggetto caricato</li>
     * </ul>
     *
     * Esempio di chiave completa:
     * <code>20221124/14/05/550e8400-e29b-41d4-a716-446655440000/28fd282d-fbe6-4528-bd28-2dfbe685286f</code>
     *
     * @return chiave dell'oggetto
     */
    private static String createRandomKey() {

        String when = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneId.systemDefault())
                .format(Instant.now());

        return when + "/" + UUIDMdcLogUtil.getUuid() + "/" + UUID.randomUUID().toString();
    }

    /**
     * Crea i file zip contenente gli xml dell'indice AIP fascicolo.Possono essere di tipo:
     * <ul>
     * <li>{@link CostantiDB.TiMeta#FASCICOLO}, obbligatorio è l'XML dei metadati fascicolo</li>
     * <li>{@link CostantiDB.TiMeta#INDICE}, obbligatorio è l'XML dell'indice AIP fascicolo</li>
     * </ul>
     *
     *
     * @param xmlFiles
     *            mappa dei file delle tipologie indicate in descrizione.
     * @param zipFile
     *            file zip su cui salvare tutto
     *
     * @throws IOException
     *             in caso di errore
     */
    private void createZipFile(Map<String, String> xmlFiles, Path zipFile) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (Map.Entry<String, String> sipBlob : xmlFiles.entrySet()) {
                ZipEntry entry = new ZipEntry(sipBlob.getKey() + ".xml");
                out.putNextEntry(entry);
                out.write(sipBlob.getValue().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
        }
    }

    /**
     * Calcola il message digest MD5 (base64 encoded) del file da inviare via S3
     *
     * Nota: questa scelta deriva dal modello supportato dal vendor
     * (https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html) *
     *
     * @param path
     *            file
     *
     * @return rappresentazione base64 del contenuto calcolato
     *
     * @throws NoSuchAlgorithmException
     *             errore generico
     * @throws IOException
     *             errore generico
     */
    private String calculateFileBase64(Path resource) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int readed;
        MessageDigest digester = MessageDigest.getInstance(CostantiDB.TipiHash.MD5.descrivi());
        try (InputStream is = Files.newInputStream(resource)) {
            while ((readed = is.read(buffer)) != -1) {
                digester.update(buffer, 0, readed);
            }
        }
        return Base64.getEncoder().encodeToString(digester.digest());
    }

    /**
     * Ottieni, in una mappa, la lista degli xml classificati nelle tipologie definite qui
     * {@link it.eng.parer.ws.utils.CostantiDB.TiMeta} per l'indice AIP fascicolo
     *
     * @param idVerAipFascicolo
     *            id versione aip fascicolo
     *
     * @return mappa degli XML dell'indice AIP fascicolo
     */
    public Map<String, String> getObjectXmlIndiceAipFasc(long idVerAipFascicolo) {
        try {
            return getObjectIndiceAipFasc(salvataggioBackendHelper.getLinkIndiceAipFascOs(idVerAipFascicolo));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    public Map<String, String> getObjectIndiceAipFasc(
            FasFileMetaVerAipFascObjectStorage fileMetaVerAipFascObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(fileMetaVerAipFascObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    fileMetaVerAipFascObjectStorage.getDecBackend().getNmBackend(), READ_INDICI_AIP_FASCICOLI);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    fileMetaVerAipFascObjectStorage.getNmBucket(), fileMetaVerAipFascObjectStorage.getCdKeyFile());
            return unzipAipFascicolo(object);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Ottieni lo stream dell'indice aip fascicolo contenuto nell'object storage
     *
     * @param idVerAipFascicolo
     *            id della versione dell'indice aip fascicolo
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     */
    public void getObjectIndiceAipFasc(long idVerAipFascicolo, OutputStream outputStream) {
        try {
            FasFileMetaVerAipFascObjectStorage link = salvataggioBackendHelper
                    .getLinkFasFileMetaVerAipFascOs(idVerAipFascicolo);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP_FASCICOLI);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Effettua il lookup per stabilire come sia configurato il backend
     *
     * @param idAaTipoFascicolo
     *            id periodo tipologia fascicolo
     * @param paramName
     *            nome del parametro
     *
     * @return Tipologia di backend. Al momento sono supportate
     *         {@link it.eng.parer.objectstorage.dto.BackendStorage.STORAGE_TYPE#BLOB} e
     *         {@link it.eng.parer.objectstorage.dto.BackendStorage.STORAGE_TYPE#OS}
     */
    public BackendStorage lookupBackendFasc(long idAaTipoFascicolo, String paramName) {
        try {

            String tipoBackend = salvataggioBackendHelper.getBackendByParamNameFasc(idAaTipoFascicolo, paramName);
            return salvataggioBackendHelper.getBackend(tipoBackend);

        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Controlla se l'indice aip fascicolo sia o meno stato registrato sull'object storage indipendemente dal valore del
     * parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idVerAipFascicolo
     *            id versione indice aip fascicolo
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isIndiceAipFascicoloOnOs(long idVerAipFascicolo) {
        try {
            return salvataggioBackendHelper.existIndiceAipFascicoloObjectStorage(idVerAipFascicolo);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private Map<String, String> unzipAipFascicolo(ResponseInputStream<GetObjectResponse> inputStream)
            throws IOException {
        // TipiXmlDati
        final String xml = ".xml";
        final Map<String, String> xmlVers = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(inputStream);) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String value = IOUtils.toString(zis, StandardCharsets.UTF_8);
                if (ze.getName()
                        .equals(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.FASCICOLO.name() + xml)) {
                    xmlVers.put(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.FASCICOLO.name(), value);
                } else if (ze.getName()
                        .equals(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.INDICE.name() + xml)) {
                    xmlVers.put(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.INDICE.name(), value);
                } else {
                    log.warn(
                            "Attenzione, l'entry con nome {} non è stata riconosciuta nel file zip degli AIP fascicolo",
                            ze.getName());
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
        }
        return xmlVers;
    }

    // end MEV #30398

    // MEV#30399
    /**
     * Salva il contenuto nel bucket degli Elenchi Indici AIP
     *
     * @param urn
     *            urn
     * @param nomeBackend
     *            backend configurato (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param contenuto
     *            byte array da salvare
     * @param idFileElencoVersFasc
     *            id file elenco indice aip fascicoli
     * @param idStrut
     *            id struttura
     *
     * @return risorsa su OS che identifica il contenuto caricato
     */
    public ObjectStorageResource createResourcesInElenchiIndiciAipFasc(final String urn, String nomeBackend,
            byte[] contenuto, long idFileElencoVersFasc, BigDecimal idStrut) {
        try {
            ObjectStorageBackend configuration = salvataggioBackendHelper.getObjectStorageConfiguration(nomeBackend,
                    WRITE_ELV_IX_AIP_FASCICOLI);

            // generate std tag
            Set<Tag> tags = new HashSet<>();

            // create key
            final String estensione = urn.toUpperCase().contains("MARCA") ? ".tsr" : ".xml.p7m";
            final String destKey = createRandomKey(urn) + estensione;

            // put on O.S. + save link
            try (ByteArrayInputStream bais = new ByteArrayInputStream(contenuto)) {

                ObjectStorageResource savedFile = salvataggioBackendHelper.putObject(bais, contenuto.length, destKey,
                        configuration, Optional.empty(), Optional.of(tags),
                        Optional.of(Md5Utils.md5AsBase64(contenuto)));
                log.debug("Salvato file {}/{}", savedFile.getBucket(), savedFile.getKey());
                // link
                salvataggioBackendHelper.saveObjectStorageLinkElencoIndiceAipFasc(savedFile, nomeBackend,
                        idFileElencoVersFasc, idStrut);
                return savedFile;
            }

        } catch (ObjectStorageException | IOException ex) {
            throw new EJBException(ex);
        }
    }

    /**
     * Controlla se l'elenco indici aip fascicoli sia o meno stato registrato sull'object storage indipendentemente dal
     * valore del parametro (il pregresso potrebbe ancora essere su DB).
     *
     * @param idFileElencoVersFasc
     *            id file elenco indice aip fascicoli
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isElencoIndiciAipFascOnOs(long idFileElencoVersFasc) {
        try {
            return salvataggioBackendHelper.existElencoIndiciAipFascObjectStorage(idFileElencoVersFasc);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Effettua la lookup per stabilire come sia configurato il backend per gli elenchi indici aip fascicoli
     *
     * @param idStrut
     *            id struttura
     *
     * @return tipologia di backend.
     */
    public BackendStorage lookupBackendElenchiIndiciAipFasc(long idStrut) {
        try {
            String tipoBackend = salvataggioBackendHelper.getBackendElenchiIndiciAipFasc(idStrut);

            // tipo backend
            return salvataggioBackendHelper.getBackend(tipoBackend);

        } catch (Exception e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni lo stream dell'elenco indici aip fascicoli contenuto nell'object storage
     *
     * @param idFileElencoVersFasc
     *            id del file dell'elenco indici aip fascicoli
     * @param outputStream
     *            Stream su cui scrivere l'oggetto
     *
     */
    public void getObjectElencoIndiciAipFasc(long idFileElencoVersFasc, OutputStream outputStream) {
        try {
            ElvFileElencoVersFascObjectStorage link = salvataggioBackendHelper
                    .getLinkElvFileElencoVersFascOs(idFileElencoVersFasc);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_ELV_IX_AIP_FASCICOLI);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copyLarge(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni, sotto forma di byte array, il file dell'elenco indici aip fascicoli contenuto nell'object storage
     *
     * @param idFileElencoVersFasc
     *            id file elenco versamento fascicoli
     *
     * @return file XML dell'elenco indici aip fascicoli
     */
    public byte[] getObjectElencoIndiciAipFasc(long idFileElencoVersFasc) {
        try {

            return getObjectFileElencoIxAipFasc(
                    salvataggioBackendHelper.getLinkElvFileElencoVersFascOs(idFileElencoVersFasc));
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    private byte[] getObjectFileElencoIxAipFasc(ElvFileElencoVersFascObjectStorage fileElencoVersFascObjectStorage)
            throws ObjectStorageException, IOException {
        if (!Objects.isNull(fileElencoVersFascObjectStorage)) {
            ObjectStorageBackend config = salvataggioBackendHelper.getObjectStorageConfiguration(
                    fileElencoVersFascObjectStorage.getDecBackend().getNmBackend(), READ_ELV_IX_AIP_FASCICOLI);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    fileElencoVersFascObjectStorage.getNmBucket(), fileElencoVersFascObjectStorage.getCdKeyFile());
            return IOUtils.toByteArray(object);
        } else {
            return IOUtils.byteArray();
        }
    }

    // end MEV#30399

    // MEV#30400

    public ObjectStorageResource createResourcesInIndiciAipSerieUD(SerFileVerSerie serFileVerSerie, String nomeBackend,
            byte[] blob, long idVerSerie, BigDecimal idStrut, CSVersatore versatore, String codiceSerie,
            String versioneSerie) {
        try {
            ObjectStorageBackend configuration = salvataggioBackendHelper.getObjectStorageConfiguration(nomeBackend,
                    WRITE_INDICI_AIP_SERIE_UD);

            // generate std tag
            Set<Tag> tags = new HashSet<>();

            final String destKey = salvataggioBackendHelper.generateKeyIndiceAipSerieUD(serFileVerSerie, versatore,
                    codiceSerie, versioneSerie);

            // put on O.S.
            ObjectStorageResource savedFile = salvataggioBackendHelper.putObject(
                    new String(blob, StandardCharsets.UTF_8), destKey, configuration, Optional.empty(),
                    Optional.of(tags), Optional.of(calculateMd5AsBase64(new String(blob, StandardCharsets.UTF_8))));

            log.debug("Salvato file {}/{}", savedFile.getBucket(), savedFile.getKey());
            // link
            if (!salvataggioBackendHelper.existIndiceAipSerieUDObjectStorage(idVerSerie,
                    serFileVerSerie.getTiFileVerSerie())) {
                salvataggioBackendHelper.saveObjectStorageLinkIndiceAipSerieUd(savedFile, nomeBackend, idVerSerie,
                        idStrut, serFileVerSerie.getTiFileVerSerie());
            }
            return savedFile;
        } catch (ObjectStorageException ex) {

            throw new EJBException(ex);
        }
    }

    /**
     * Controlla se l'indice aip sia o meno stato registrato sull'object storage indipendemente dal valore del parametro
     * (il pregresso potrebbe ancora essere su DB).
     *
     * @param idVerSerie
     *            id versione indice aip
     *
     * @param tiFileVerSerie
     *            tipo file della versione dell'indice aip
     *
     * @return true se su O.s false altrimenti
     */
    public boolean isSerFileVerSerieUDOnOs(long idVerSerie, String tiFileVerSerie) {
        try {
            return salvataggioBackendHelper.existIndiceAipSerieUDObjectStorage(idVerSerie, tiFileVerSerie);
        } catch (ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    /**
     * Ottieni i metadati dell'oggetto dell'indice aip della serie ud contenuto nell'object storage
     *
     * @param idVerSerieUd
     *            id della versione dell'indice aip
     * @param tiFileVerSerie
     *            tipo file versione serie
     *
     * @return attributi dell'oggetto su O.s.
     */
    public HeadObjectResponse getObjectMetadataIndiceAipSerieUD(long idVerSerieUd, String tiFileVerSerie) {
        try {
            SerVerSerieObjectStorage link = salvataggioBackendHelper.getLinkSerVerSerieOs(idVerSerieUd, tiFileVerSerie);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP_SERIE_UD);
            return salvataggioBackendHelper.getObjectMetadata(config, link.getNmBucket(), link.getCdKeyFile());
        } catch (ObjectStorageException e) {

            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    public void getSerVerSerieObjectStorage(long idVerSerieUd, String tiFileVerSerie, OutputStream outputStream)
            throws ObjectStorageException {
        try {
            SerVerSerieObjectStorage link = salvataggioBackendHelper.getLinkSerVerSerieOs(idVerSerieUd, tiFileVerSerie);
            ObjectStorageBackend config = salvataggioBackendHelper
                    .getObjectStorageConfiguration(link.getDecBackend().getNmBackend(), READ_INDICI_AIP_SERIE_UD);
            ResponseInputStream<GetObjectResponse> object = salvataggioBackendHelper.getObject(config,
                    link.getNmBucket(), link.getCdKeyFile());
            IOUtils.copy(object, outputStream);
        } catch (IOException | ObjectStorageException e) {
            // EJB spec (14.2.2 in the EJB 3)
            throw new EJBException(e);
        }
    }

    // end MEV#30400

}
