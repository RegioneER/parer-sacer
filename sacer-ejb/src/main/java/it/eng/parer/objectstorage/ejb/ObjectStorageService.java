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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroCompObjectStorage;
import it.eng.parer.entity.AroVerIndiceAipUdObjectStorage;
import it.eng.parer.entity.FasXmlFascObjectStorage;
import it.eng.parer.entity.FasXmlVersFascObjectStorage;
import it.eng.parer.entity.FirReport;
import it.eng.parer.entity.VrsFileSesObjectStorageKo;
import it.eng.parer.entity.VrsXmlDatiSesObjectStorageKo;
import it.eng.parer.entity.VrsXmlSesFascErrObjectStorage;
import it.eng.parer.entity.VrsXmlSesFascKoObjectStorage;
import it.eng.parer.entity.inheritance.oop.AroXmlObjectStorage;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.objectstorage.helper.SalvataggioBackendHelper;
import it.eng.parer.ws.utils.CostantiDB;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.Md5Utils;

@Stateless(mappedName = "ObjectStorageService")
@LocalBean
public class ObjectStorageService {

    private final Logger log = LoggerFactory.getLogger(ObjectStorageService.class);

    private static final String STAGING_R = "READ_STAGING";
    private static final String COMPONENTI_R = "READ_COMPONENTI";
    private static final String SIP_R = "READ_SIP";
    // MEV#29090
    private static final String METADATI_FASC_R = "READ_FASCICOLI";
    private static final String SES_FASC_ERR_KO_R = "READ_SESSIONI_FASC_ERR_KO";
    // end MEV#29090
    private static final String REPORTVF_R = "READ_REPORTVF";
    // MEV#30395
    private static final String READ_INDICI_AIP = "READ_INDICI_AIP";
    private static final String WRITE_INDICI_AIP = "WRITE_INDICI_AIP";
    // end MEV#30395

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

            return getObjectMetaProfFasc(salvataggioBackendHelper.getLinkMetaProfFascicoloOs(idFascicolo));
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
}
