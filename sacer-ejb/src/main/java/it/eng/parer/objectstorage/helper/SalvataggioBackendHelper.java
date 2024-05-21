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

package it.eng.parer.objectstorage.helper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroCompObjectStorage;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdDatiSpecUdObjectStorage;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUdObjectStorage;
import it.eng.parer.entity.AroVersIniDatiSpecObjectStorage;
import it.eng.parer.entity.AroXmlDocObjectStorage;
import it.eng.parer.entity.AroXmlUnitaDocObjectStorage;
import it.eng.parer.entity.AroXmlUpdUdObjectStorage;
import it.eng.parer.entity.DecBackend;
import it.eng.parer.entity.DecConfigObjectStorage;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.FasXmlFascObjectStorage;
import it.eng.parer.entity.FasXmlVersFascObjectStorage;
import it.eng.parer.entity.FirReport;
import it.eng.parer.entity.VrsFileSesObjectStorageKo;
import it.eng.parer.entity.VrsXmlDatiSesObjectStorageKo;
import it.eng.parer.entity.VrsXmlSesUpdUdErrObjectStorage;
import it.eng.parer.entity.VrsXmlSesUpdUdKoObjectStorage;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec;
import it.eng.parer.entity.VrsXmlSesFascErrObjectStorage;
import it.eng.parer.entity.VrsXmlSesFascKoObjectStorage;
import it.eng.parer.entity.inheritance.oop.AroXmlObjectStorage;
import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageBackend;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.ejb.AwsClient;
import it.eng.parer.objectstorage.ejb.AwsPresigner;
import it.eng.parer.objectstorage.exceptions.ObjectStorageException;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.Costanti.AwsConstants;
import it.eng.parer.ws.utils.CostantiDB.ParametroAppl;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Stateless(mappedName = "SalvataggioBackendHelper")
@LocalBean
public class SalvataggioBackendHelper {

    private final Logger log = LoggerFactory.getLogger(SalvataggioBackendHelper.class);

    private static final String LOG_MESSAGE_NO_SAVED = "Impossibile salvare il link dell'oggetto su DB";
    private static final String ID_COMP_DOC = "idCompDoc";

    private static final String NOME_BACKEND_PARAMETER = "nomeBackend";

    @EJB
    protected SalvataggioBackendHelper me;

    @EJB
    protected ConfigurationHelper configurationHelper;

    @EJB
    protected AwsPresigner presigner;

    @EJB
    protected AwsClient s3Clients;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public enum BACKEND_VERSAMENTO {
        DATABASE, OBJECT_STORAGE
    }

    /**
     * Ottieni la tipologia di backend per salvare i BLOB relativi al versamento sincrono
     *
     * @param idTipoUnitaDoc
     *            id della tipologia dell'UD
     * @param paramName
     *            nome del parametro
     *
     * @return Configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure DATABASE_PRIMARIO
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public String getBackendByParamName(long idTipoUnitaDoc, String paramName) throws ObjectStorageException {
        String backendDatiVersamento = null;
        try {
            return getParameter(idTipoUnitaDoc, paramName);

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message("Impossibile ottenere il parametro {0} con id tipo unita doc {1} e tipo creazione {2}",
                            backendDatiVersamento, idTipoUnitaDoc, paramName)
                    .cause(e).build();
        }
    }

    private String getParameter(long idTipoUnitaDoc, String parameterName) {
        DecTipoUnitaDoc tipoUd = entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        long idStrut = tipoUd.getOrgStrut().getIdStrut();

        long idAmbiente = tipoUd.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();

        return configurationHelper.getValoreParamApplicByTipoUd(parameterName, BigDecimal.valueOf(idAmbiente),
                BigDecimal.valueOf(idStrut), BigDecimal.valueOf(idTipoUnitaDoc));
    }

    public DecBackend getBackendEntity(String nomeBackend) {
        TypedQuery<DecBackend> query = entityManager
                .createQuery("Select d from DecBackend d where d.nmBackend = :nomeBackend", DecBackend.class);
        query.setParameter(NOME_BACKEND_PARAMETER, nomeBackend);
        return query.getSingleResult();
    }

    /**
     * Ottieni l'oggetto dall'object storage selezionato sotto-forma di InputStream.
     *
     * @param configuration
     *            configurazione per accedere all'object storage
     * @param bucket
     *            bucket
     * @param objectKey
     *            chiave
     *
     * @return InputStream dell'oggetto ottenuto
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ResponseInputStream<GetObjectResponse> getObject(ObjectStorageBackend configuration, String bucket,
            String objectKey) throws ObjectStorageException {
        try {
            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());

            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
            return s3SourceClient.getObject(getObjectRequest);

        } catch (AwsServiceException | SdkClientException e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile ottenere dal bucket {1} oggetto con chiave {2}",
                            configuration.getBackendName(), bucket, objectKey)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni le informazioni relative all'object storage su cui è memorizzato il SIP di versamento. L'oggetto che
     * corrisponde alla chiave inserita nella tabella è un file zip contenente tutti gli xml di versamento.
     *
     * @param idSessioneVers
     *            id sessione di versamento
     *
     * @return entity contenente le informazioni di bucket/chiave dello zip contenente gli xml di versamento.
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsXmlDatiSesObjectStorageKo getLinkXmlDatiSesOs(long idSessioneVers) throws ObjectStorageException {
        try {

            TypedQuery<VrsXmlDatiSesObjectStorageKo> query = entityManager.createQuery(
                    "Select xml from VrsXmlDatiSesObjectStorageKo xml where xml.datiSessioneVersKo.vrsSessioneVersKo.idSessioneVersKo = :idSessioneVers",
                    VrsXmlDatiSesObjectStorageKo.class);
            query.setParameter("idSessioneVers", idSessioneVers);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // no result (past data / needed guarantess working from the past)
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message(
                            "Errore durante il recupero da VrsXmlDatiSesObjectStorageKo per id dati sessione vers {0} ",
                            idSessioneVers)
                    .cause(e).build();
        }
    }

    /**
     * Ottieni il collegamento tra sip dell'unita documentaria e il suo bucket/chiave su OS.
     *
     * @param idUnitaDoc
     *            id unita documentaria
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroXmlObjectStorage getLinkSipUnitaDocOs(long idUnitaDoc) throws ObjectStorageException {
        try {
            return entityManager.find(AroXmlUnitaDocObjectStorage.class, idUnitaDoc);

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da AroXmlUnitaDocObjectStorage per id unita doc vers {0} ",
                            idUnitaDoc)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra sip del documento e il suo bucket/chiave su OS.
     *
     * @param idDoc
     *            id documento
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroXmlObjectStorage getLinkSipDocOs(long idDoc) throws ObjectStorageException {
        try {
            return entityManager.find(AroXmlDocObjectStorage.class, idDoc);

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da AroXmlUnitaDocObjectStorage per id doc vers {0} ", idDoc)
                    .cause(e).build();
        }

    }

    // MEV#29089
    /**
     * Ottieni il collegamento tra sip dell'aggiornamento metadati e il suo bucket/chiave su OS.
     *
     * @param idUpdUnitaDoc
     *            id aggiornamento metadati
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroXmlUpdUdObjectStorage getLinkSipAggMdOs(long idUpdUnitaDoc) throws ObjectStorageException {
        try {
            return entityManager.find(AroXmlUpdUdObjectStorage.class, idUpdUnitaDoc);

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message(
                            "Errore durante il recupero da AroXmlUpdUdObjectStorage per id aggiornamento metadati {0} ",
                            idUpdUnitaDoc)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra i dati specifici aggiornati dell'aggiornamento metadati e il suo bucket/chiave su OS.
     *
     * @param idEntitaSacerUpd
     *            id aggiornamento metadati
     * @param tiEntitaSacerUpd
     *            tipo entità aggiornamento metadati
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroUpdDatiSpecUdObjectStorage getLinkUpdDatiSpecAggMdOs(long idEntitaSacerUpd,
            TiEntitaAroUpdDatiSpecUnitaDoc tiEntitaSacerUpd) throws ObjectStorageException {
        try {
            String tmpTipoEntita = null;
            switch (tiEntitaSacerUpd) {
            case UPD_UNI_DOC:
                tmpTipoEntita = "xml.aroUpdUnitaDoc.idUpdUnitaDoc";
                break;
            case UPD_DOC:
                tmpTipoEntita = "xml.aroUpdDocUnitaDoc.idUpdDocUnitaDoc";
                break;
            case UPD_COMP:
                tmpTipoEntita = "xml.aroUpdCompUnitaDoc.idUpdCompUnitaDoc";
                break;
            }
            String queryStr = String.format(
                    "select xml " + "from AroUpdDatiSpecUdObjectStorage xml "
                            + "where xml.tiEntitaSacer = :tipoEntitySacer " + "and %s = :idEntitySacerUpd ",
                    tmpTipoEntita);

            TypedQuery<AroUpdDatiSpecUdObjectStorage> query = entityManager.createQuery(queryStr,
                    AroUpdDatiSpecUdObjectStorage.class);
            query.setParameter("tipoEntitySacer", tiEntitaSacerUpd);
            query.setParameter("idEntitySacerUpd", idEntitaSacerUpd);
            return query.getSingleResult();
        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da AroUpdDatiSpecUdObjectStorage per tipo entità aggiornamento metadati {0} con id aggiornamento metadati {1} ",
                    tiEntitaSacerUpd.name(), idEntitaSacerUpd).cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra i dati specifici relativi ai metadati iniziali dell'aggiornamento metadati e il suo
     * bucket/chiave su OS.
     *
     * @param idEntitaSacerVersIni
     *            id versamento iniziale
     * @param tiEntitaSacerVersIni
     *            tipo entità versamento iniziale
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroVersIniDatiSpecObjectStorage getLinkVersIniDatiSpecAggMdOs(long idEntitaSacerVersIni,
            TiEntitaSacerAroVersIniDatiSpec tiEntitaSacerVersIni) throws ObjectStorageException {
        try {
            String tmpTipoEntita = null;
            switch (tiEntitaSacerVersIni) {
            case UNI_DOC:
                tmpTipoEntita = "xml.aroVersIniUnitaDoc.idVersIniUnitaDoc";
                break;
            case DOC:
                tmpTipoEntita = "xml.aroVersIniDoc.idVersIniDoc";
                break;
            case COMP:
                tmpTipoEntita = "xml.aroVersIniComp.idVersIniComp";
                break;
            }
            String queryStr = String.format(
                    "select xml " + "from AroVersIniDatiSpecObjectStorage xml "
                            + "where xml.tiEntitaSacer = :tipoEntitySacer " + "and %s = :idEntitySacerVersIni ",
                    tmpTipoEntita);

            TypedQuery<AroVersIniDatiSpecObjectStorage> query = entityManager.createQuery(queryStr,
                    AroVersIniDatiSpecObjectStorage.class);
            query.setParameter("tipoEntitySacer", tiEntitaSacerVersIni);
            query.setParameter("idEntitySacerVersIni", idEntitaSacerVersIni);
            return query.getSingleResult();
        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da AroVersIniDatiSpecObjectStorage per tipo entità versamento iniziale {0} con id versamento iniziale {1} ",
                    tiEntitaSacerVersIni.name(), idEntitaSacerVersIni).cause(e).build();
        }

    }

    /**
     * Ottieni le informazioni relative all'object storage su cui è memorizzato il SIP di versamento aggiornamento
     * metadati fallito. L'oggetto che corrisponde alla chiave inserita nella tabella è un file zip contenente tutti gli
     * xml di versamento aggiornamento metadati fallito.
     *
     * @param idSesUpdUnitaDocKo
     *            id sessione di versamento aggiornamento metadati fallita
     *
     * @return entity contenente le informazioni di bucket/chiave dello zip contenente gli xml di versamento
     *         aggiornamento metadati fallito.
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsXmlSesUpdUdKoObjectStorage getLinkXmlSesAggMdKoOs(long idSesUpdUnitaDocKo) throws ObjectStorageException {
        try {

            TypedQuery<VrsXmlSesUpdUdKoObjectStorage> query = entityManager.createQuery(
                    "Select xml from VrsXmlSesUpdUdKoObjectStorage xml where xml.vrsSesUpdUnitaDocKo.idSesUpdUnitaDocKo = :idSesUpdUnitaDocKo",
                    VrsXmlSesUpdUdKoObjectStorage.class);
            query.setParameter("idSesUpdUnitaDocKo", idSesUpdUnitaDocKo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // no result (past data / needed guarantess working from the past)
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da VrsXmlSesUpdUdKoObjectStorage per id xml sessione aggiornamento metadati fallita {0} ",
                    idSesUpdUnitaDocKo).cause(e).build();
        }
    }

    /**
     * Ottieni le informazioni relative all'object storage su cui è memorizzato il SIP di versamento aggiornamento
     * metadati errato. L'oggetto che corrisponde alla chiave inserita nella tabella è un file zip contenente tutti gli
     * xml di versamento aggiornamento metadati errato.
     *
     * @param idSesUpdUnitaDocErr
     *            id sessione di versamento aggiornamento metadati errata
     *
     * @return entity contenente le informazioni di bucket/chiave dello zip contenente gli xml di versamento
     *         aggiornamento metadati errato.
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsXmlSesUpdUdErrObjectStorage getLinkXmlSesAggMdErrOs(long idSesUpdUnitaDocErr)
            throws ObjectStorageException {
        try {

            TypedQuery<VrsXmlSesUpdUdErrObjectStorage> query = entityManager.createQuery(
                    "Select xml from VrsXmlSesUpdUdErrObjectStorage xml where xml.vrsSesUpdUnitaDocErr.idSesUpdUnitaDocErr = :idSesUpdUnitaDocErr",
                    VrsXmlSesUpdUdErrObjectStorage.class);
            query.setParameter("idSesUpdUnitaDocErr", idSesUpdUnitaDocErr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // no result (past data / needed guarantess working from the past)
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da VrsXmlSesUpdUdErrObjectStorage per id xml sessione aggiornamento metadati errata {0} ",
                    idSesUpdUnitaDocErr).cause(e).build();
        }
    }
    // end MEV#29089

    // MEV#29090
    /**
     * Ottieni il collegamento tra sip del fascicolo e il suo bucket/chiave su OS.
     *
     * @param idFascicolo
     *            id fascicolo
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public FasXmlVersFascObjectStorage getLinkSipFascicoloOs(long idFascicolo) throws ObjectStorageException {
        try {
            return entityManager.find(FasXmlVersFascObjectStorage.class, idFascicolo);

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da FasXmlVersFascObjectStorage per id fascicolo {0} ",
                            idFascicolo)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra i metadati di profilo del fascicolo e il suo bucket/chiave su OS.
     *
     * @param idFascicolo
     *            id fascicolo
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public FasXmlFascObjectStorage getLinkMetaProfFascicoloOs(long idFascicolo) throws ObjectStorageException {
        try {
            return entityManager.find(FasXmlFascObjectStorage.class, idFascicolo);

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da FasXmlFascObjectStorage per id fascicolo {0} ", idFascicolo)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni le informazioni relative all'object storage su cui è memorizzato il SIP di versamento fascicolo fallito.
     * L'oggetto che corrisponde alla chiave inserita nella tabella è un file zip contenente tutti gli xml di versamento
     * fascicolo fallito.
     *
     * @param idSesFascicoloKo
     *            id sessione di versamento fascicolo fallita
     *
     * @return entity contenente le informazioni di bucket/chiave dello zip contenente gli xml di versamento fascicolo
     *         fallito.
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsXmlSesFascKoObjectStorage getLinkXmlSesFascKoOs(long idSesFascicoloKo) throws ObjectStorageException {
        try {

            TypedQuery<VrsXmlSesFascKoObjectStorage> query = entityManager.createQuery(
                    "Select xml from VrsXmlSesFascKoObjectStorage xml where xml.vrsSesFascicoloKo.idSesFascicoloKo = :idSesFascicoloKo",
                    VrsXmlSesFascKoObjectStorage.class);
            query.setParameter("idSesFascicoloKo", idSesFascicoloKo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // no result (past data / needed guarantess working from the past)
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da VrsXmlSesFascKoObjectStorage per id xml sessione fascicolo fallita {0} ",
                    idSesFascicoloKo).cause(e).build();
        }
    }

    /**
     * Ottieni le informazioni relative all'object storage su cui è memorizzato il SIP di versamento fascicolo errato.
     * L'oggetto che corrisponde alla chiave inserita nella tabella è un file zip contenente tutti gli xml di versamento
     * fascicolo errato.
     *
     * @param idSesFascicoloErr
     *            id sessione di versamento fascicolo errata
     *
     * @return entity contenente le informazioni di bucket/chiave dello zip contenente gli xml di versamento fascicolo
     *         errato.
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsXmlSesFascErrObjectStorage getLinkXmlSesFascErrOs(long idSesFascicoloErr) throws ObjectStorageException {
        try {

            TypedQuery<VrsXmlSesFascErrObjectStorage> query = entityManager.createQuery(
                    "Select xml from VrsXmlSesFascErrObjectStorage xml where xml.vrsSesFascicoloErr.idSesFascicoloErr = :idSesFascicoloErr",
                    VrsXmlSesFascErrObjectStorage.class);
            query.setParameter("idSesFascicoloErr", idSesFascicoloErr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // no result (past data / needed guarantess working from the past)
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da VrsXmlSesFascErrObjectStorage per id xml sessione fascicolo errata {0} ",
                    idSesFascicoloErr).cause(e).build();
        }
    }
    // end MEV#29090

    /**
     * Ottieni il collegamento tra il componente e il suo bucket/chiave su OS.
     *
     * @param idCompDoc
     *            id del componente
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroCompObjectStorage getLinkCompDocOs(long idCompDoc) throws ObjectStorageException {
        try {
            TypedQuery<AroCompObjectStorage> query = entityManager.createQuery(
                    "select t from AroCompObjectStorage t where t.aroCompDoc.idCompDoc = :idCompDoc ",
                    AroCompObjectStorage.class);
            query.setParameter(ID_COMP_DOC, idCompDoc);
            return query.getSingleResult();

        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da AroCompObjectStorage per id comp doc vers {0} ", idCompDoc)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra il report (identificato dal componente) ed il suo bucket/chiave su OS.
     *
     * @param idCompDoc
     *            id del componente
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public FirReport getLinkReportVerificaFirma(long idCompDoc) throws ObjectStorageException {
        try {
            TypedQuery<FirReport> query = entityManager.createQuery(
                    "select t from FirReport t where t.aroCompDoc.idCompDoc = :idCompDoc", FirReport.class);
            query.setParameter(ID_COMP_DOC, idCompDoc);
            return query.getSingleResult();

        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da AroCompObjectStorage per id comp doc vers {0}", idCompDoc)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra il versamento fallito (identifica dall'id file sessione) ed il suo bucket/chiave su
     * OS.
     *
     * @param idFileSessioneKo
     *            id file sessione
     * 
     * @return record contenete il link
     * 
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public VrsFileSesObjectStorageKo getLinkVersamentoFallito(long idFileSessioneKo) throws ObjectStorageException {
        try {
            TypedQuery<VrsFileSesObjectStorageKo> query = entityManager.createQuery(
                    "Select ses_os from VrsFileSesObjectStorageKo ses_os where ses_os.fileSessioneKo.idFileSessioneKo = :idFileSessioneKo",
                    VrsFileSesObjectStorageKo.class);
            query.setParameter("idFileSessioneKo", idFileSessioneKo);
            return query.getSingleResult();
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore durante il recupero da VrsFileSesObjectStorageKo per id file sessione {0} ",
                            idFileSessioneKo)
                    .cause(e).build();
        }
    }

    /**
     * Restitusce un boolean per la verifica del "link" verso object storage
     * 
     * @param idFileSessioneKo
     *            id sessione file
     * 
     * @return boolean true se effettivamente presente su object storage / false altrimenti
     * 
     * @throws ObjectStorageException
     *             eccezione generica
     */
    public boolean existFileSesObjectStorage(long idFileSessioneKo) throws ObjectStorageException {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "Select count(ses_os) from VrsFileSesObjectStorageKo ses_os where ses_os.fileSessioneKo.idFileSessioneKo = :idFileSessioneKo",
                    Long.class);
            query.setParameter("idFileSessioneKo", idFileSessioneKo);
            Long result = query.getSingleResult();
            return result.longValue() > 0;
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore verifica presenza VrsFileSesObjectStorageKo per id file sessione {0} ",
                            idFileSessioneKo)
                    .cause(e).build();
        }
    }

    /**
     * Restitusce un boolean per la verifica del "link" verso object storage
     *
     * @param idCompDoc
     *            id componente da verificare
     * 
     * @return boolean true se effettivamente presente su object storage / false altrimenti
     * 
     * @throws ObjectStorageException
     *             eccezione generica
     */
    public boolean existComponenteObjectStorage(long idCompDoc) throws ObjectStorageException {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "Select count(aro_comp_os) from AroCompObjectStorage aro_comp_os where aro_comp_os.aroCompDoc.idCompDoc = :idCompDoc",
                    Long.class);
            query.setParameter("idCompDoc", idCompDoc);
            Long result = query.getSingleResult();
            return result.longValue() > 0;
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore verifica presenza AroCompObjectStorage per id componente {0} ", idCompDoc).cause(e)
                    .build();
        }
    }

    /**
     * Restitusce un boolean per la verifica del "link" verso object storage
     * 
     * @param idCompDoc
     *            id componente da verificare
     * 
     * @return boolean true se effettivamente presente su object storage / false altrimenti
     * 
     * @throws ObjectStorageException
     *             eccezione generica
     */
    public boolean existReportvfObjectStorage(long idCompDoc) throws ObjectStorageException {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "Select count(f) from FirReport f  where f.aroCompDoc.idCompDoc = :idCompDoc and f.nmBucket is not null and f.cdKeyFile is not null",
                    Long.class);
            query.setParameter("idCompDoc", idCompDoc);
            Long result = query.getSingleResult();
            return result.longValue() > 0;
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore verifica presenza FirReport per id componente {0} ", idCompDoc).cause(e).build();
        }
    }

    /**
     * Ottieni la configurazione del backend a partire dal nome del backend
     *
     * @param nomeBackend
     *            per esempio "OBJECT_STORAGE_PRIMARIO"
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public BackendStorage getBackend(String nomeBackend) throws ObjectStorageException {
        try {

            DecBackend backend = me.getBackendEntity(nomeBackend);
            final BackendStorage.STORAGE_TYPE type = BackendStorage.STORAGE_TYPE.valueOf(backend.getNmTipoBackend());
            final String backendName = backend.getNmBackend();

            return new BackendStorage() {
                private static final long serialVersionUID = 5092016605462729859L;

                @Override
                public BackendStorage.STORAGE_TYPE getType() {
                    return type;
                }

                @Override
                public String getBackendName() {
                    return backendName;
                }
            };

        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw ObjectStorageException.builder().message("Impossibile ottenere le informazioni di backend").cause(e)
                    .build();
        }

    }

    private static final String BUCKET = "BUCKET";
    private static final String ACCESS_KEY_ID_SYS_PROP = "ACCESS_KEY_ID_SYS_PROP";
    private static final String SECRET_KEY_SYS_PROP = "SECRET_KEY_SYS_PROP";

    /**
     * Ottieni la configurazione per potersi collegare a quel bucket dell'Object Storage scelto.
     *
     * @param nomeBackend
     *            nome del backend <strong> di tipo DEC_BACKEND.NM_TIPO_BACKEND = 'OS' </strong>come censito su
     *            DEC_BACKEND (per esempio OBJECT_STORAGE_PRIMARIO)
     * @param tipoUsoOs
     *            ambito di utilizzo di questo backend (per esempio STAGING)
     *
     * @return Configurazione dell'Object Storage per quell'ambito
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ObjectStorageBackend getObjectStorageConfiguration(final String nomeBackend, final String tipoUsoOs)
            throws ObjectStorageException {
        TypedQuery<DecConfigObjectStorage> query = entityManager.createQuery(
                "Select c from DecConfigObjectStorage c where c.tiUsoConfigObjectStorage = :tipoUsoOs and c.decBackend.nmBackend = :nomeBackend order by c.nmConfigObjectStorage",
                DecConfigObjectStorage.class);
        query.setParameter("tipoUsoOs", tipoUsoOs).setParameter(NOME_BACKEND_PARAMETER, nomeBackend);
        List<DecConfigObjectStorage> resultList = query.getResultList();
        String bucket = null;
        String nomeSystemPropertyAccessKeyId = null;
        String nomeSystemPropertySecretKey = null;
        String storageAddress = null;

        for (DecConfigObjectStorage decConfigObjectStorage : resultList) {
            switch (decConfigObjectStorage.getNmConfigObjectStorage()) {
            case ACCESS_KEY_ID_SYS_PROP:
                nomeSystemPropertyAccessKeyId = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            case BUCKET:
                bucket = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            case SECRET_KEY_SYS_PROP:
                nomeSystemPropertySecretKey = decConfigObjectStorage.getDsValoreConfigObjectStorage();
                break;
            default:
                throw ObjectStorageException.builder()
                        .message("Impossibile stabilire la tiplogia del parametro per l'object storage").build();
            }
            // identico per tutti perché definito nella tabella padre
            storageAddress = decConfigObjectStorage.getDecBackend().getDlBackendUri();
        }
        if (StringUtils.isBlank(bucket) || StringUtils.isBlank(nomeSystemPropertyAccessKeyId)
                || StringUtils.isBlank(nomeSystemPropertySecretKey) || StringUtils.isBlank(storageAddress)) {
            throw ObjectStorageException.builder()
                    .message("Impossibile stabilire la tiplogia del parametro per l'object storage").build();
        }

        final String accessKeyId = System.getProperty(nomeSystemPropertyAccessKeyId);
        final String secretKey = System.getProperty(nomeSystemPropertySecretKey);
        final URI osURI = URI.create(storageAddress);
        final String stagingBucket = bucket;

        return new ObjectStorageBackend() {
            private static final long serialVersionUID = -7032516962480163852L;

            @Override
            public String getBackendName() {
                return nomeBackend;
            }

            @Override
            public URI getAddress() {
                return osURI;
            }

            @Override
            public String getBucket() {
                return stagingBucket;
            }

            @Override
            public String getAccessKeyId() {
                return accessKeyId;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }
        };

    }

    // MEV#30395
    /**
     * Salva i dati sull'object storage della configurazione identificandolo con la chiave passata come parametro.
     *
     * @param contenuto
     *            dati
     * @param key
     *            chiave dell'oggetto
     * @param configuration
     *            configurazione dell'object storage in cui aggiungere l'oggetto
     *
     * @return riferimento alla risorsa appena inserita
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ObjectStorageResource putObject(String contenuto, final String key, ObjectStorageBackend configuration)
            throws ObjectStorageException {
        checkConfiguration(configuration);
        try {
            return putObject(contenuto, key, configuration, Optional.empty(), Optional.empty(), Optional.empty());
        } catch (Exception e) {
            throw ObjectStorageException.builder()
                    .message("Impossibile salvare oggetto {0} sul bucket {1}", key, configuration.getBucket()).cause(e)
                    .build();
        }
    }

    /**
     * Salva i dati sull'object storage della configurazione identificandolo con la chiave passata come parametro.
     *
     * @param contenuto
     *            dati
     * @param key
     *            chiave dell'oggetto
     * @param configuration
     *            configurazione dell'object storage in cui aggiungere l'oggetto
     * @param metadata
     *            eventuali metadati (nel caso non vengano passati vengono utilizzati quelli predefiniti)
     * @param tags
     *            eventuali tag (nel caso non vengano passati non vengono apposti)
     * @param base64md5
     *            eventuale base64-encoded MD5 del dato per data integrity check
     *
     * @return riferimento alla risorsa appena inserita
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public ObjectStorageResource putObject(String contenuto, final String key, ObjectStorageBackend configuration,
            Optional<Map<String, String>> metadata, Optional<Set<Tag>> tags, Optional<String> base64md5)
            throws ObjectStorageException {

        checkConfiguration(configuration);

        final URI storageAddress = configuration.getAddress();
        final String accessKeyId = configuration.getAccessKeyId();
        final String secretKey = configuration.getSecretKey();
        final String bucket = configuration.getBucket();

        log.debug("Sto per inserire nell'os {} la chiave {} sul bucket {}", storageAddress, key, bucket);

        try {
            S3Client s3Client = s3Clients.getClient(storageAddress, accessKeyId, secretKey);

            PutObjectRequest.Builder putObjectBuilder = PutObjectRequest.builder().bucket(bucket).key(key);

            if (metadata.isPresent()) {
                putObjectBuilder.metadata(metadata.get());
            } else {
                putObjectBuilder.metadata(defaultMetadata());
            }
            if (tags.isPresent()) {
                putObjectBuilder.tagging(Tagging.builder().tagSet(tags.get()).build());
            }
            if (base64md5.isPresent()) {
                putObjectBuilder.contentMD5(base64md5.get());
            }

            PutObjectRequest objectRequest = putObjectBuilder.build();
            final long start = System.currentTimeMillis();
            PutObjectResponse response = s3Client.putObject(objectRequest,
                    RequestBody.fromString(contenuto, StandardCharsets.UTF_8));

            final long end = System.currentTimeMillis() - start;
            if (log.isDebugEnabled()) {
                log.debug("Salvato oggetto {} sul bucket {} con ETag {} in {} ms", key, bucket, response.eTag(), end);
            }
            final URL presignedUrl = presigner.getPresignedUrl(configuration, key);
            //
            final URI presignedURLasURI = presignedUrl.toURI();

            final String tenant = getDefaultTenant();

            return new ObjectStorageResource() {
                @Override
                public String getBucket() {
                    return bucket;
                }

                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public String getETag() {
                    return response.eTag();
                }

                @Override
                public String getExpiration() {
                    return response.expiration();
                }

                @Override
                public URI getPresignedURL() {
                    return presignedURLasURI;
                }

                @Override
                public String getTenant() {
                    return tenant;
                }
            };

        } catch (Exception e) {
            throw ObjectStorageException.builder().message("{0}: impossibile salvare oggetto {1} sul bucket {2}",
                    configuration.getBackendName(), key, configuration.getBucket()).cause(e).build();
        }
    }

    private Map<String, String> defaultMetadata() {

        Map<String, String> defaultMetadata = new HashMap<>();
        defaultMetadata.put(AwsConstants.MEATADATA_INGEST_NODE, System.getProperty("jboss.node.name"));
        defaultMetadata.put(AwsConstants.MEATADATA_INGEST_TIME,
                ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        return defaultMetadata;
    }

    /**
     * Effettua il salvataggio del collegamento tra la versione dell'indice aip dell'ud e la chiave sull'object storage
     *
     * @param object
     *            informazioni dell'oggetto salvato
     * @param nmBackend
     *            nome del backend (di tipo OS) su cui è stato salvato
     * @param idVerIndiceAip
     *            id versione indice aip
     * @param idSubStrut
     *            id sotto struttura
     * @param aaKeyUnitaDoc
     *            anno chiave unità documentaria
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public void saveObjectStorageLinkIndiceAipUd(ObjectStorageResource object, String nmBackend, long idVerIndiceAip,
            BigDecimal idSubStrut, BigDecimal aaKeyUnitaDoc) throws ObjectStorageException {
        try {
            DecBackend decBackend = me.getBackendEntity(nmBackend);
            AroVerIndiceAipUd aroVerIndiceAip = entityManager.find(AroVerIndiceAipUd.class, idVerIndiceAip);

            AroVerIndiceAipUdObjectStorage osLink = new AroVerIndiceAipUdObjectStorage();
            osLink.setAroVerIndiceAipUd(aroVerIndiceAip);

            osLink.setIdSubStrut(idSubStrut);
            osLink.setAaKeyUnitaDoc(aaKeyUnitaDoc);

            osLink.setCdKeyFile(object.getKey());
            osLink.setNmBucket(object.getBucket());
            osLink.setNmTenant(object.getTenant());

            osLink.setDecBackend(decBackend);
            entityManager.persist(osLink);

        } catch (Exception e) {
            throw ObjectStorageException.builder().message(LOG_MESSAGE_NO_SAVED).cause(e).build();
        }
    }

    /**
     * Genera la chiave dell'indice aip da salvare sull'object storage.
     *
     * @param idVerIndiceAip
     *            identificativo della versione dell'indice aip di cui salvare il contenuto
     *
     * @return chiave che verrà utilizzata sul bucket indici aip
     *
     * @throws ObjectStorageException
     *             in caso di errore.
     */
    public String generateKeyIndiceAip(long idVerIndiceAip) throws ObjectStorageException {
        try {

            AroVerIndiceAipUd verIndiceAipUd = entityManager.find(AroVerIndiceAipUd.class, idVerIndiceAip);

            // devo "pescare" l'UD passando dalla ARO_INDICE_AIP_UD
            AroUnitaDoc unitaDoc = verIndiceAipUd.getAroIndiceAipUd().getAroUnitaDoc();

            String nmStrutNorm = unitaDoc.getOrgStrut().getCdStrutNormaliz();

            String nmEnteNorm = unitaDoc.getOrgStrut().getOrgEnte().getCdEnteNormaliz();

            String cdRegistroNorm = unitaDoc.getDecRegistroUnitaDoc().getCdRegistroNormaliz();
            int anno = unitaDoc.getAaKeyUnitaDoc().intValue();

            return createKeyIndiciAip(nmEnteNorm, nmStrutNorm, cdRegistroNorm, anno, unitaDoc.getIdUnitaDoc(),
                    verIndiceAipUd.getPgVerIndiceAip());

        } catch (Exception e) {
            throw ObjectStorageException.builder().message("Impossibile generare la chiave del componente").cause(e)
                    .build();
        }
    }

    private String getDefaultTenant() {
        return configurationHelper.getValoreParamApplicByApplic(ParametroAppl.TENANT_OBJECT_STORAGE);

    }

    private String createKeyIndiciAip(String nmEnteNorm, String nmStrutNorm, String cdRegistroNorm, int anno,
            long idUnitaDoc, BigDecimal pgVerIndiceAip) {
        // Non serve a nulla
        String nmTenant = getDefaultTenant();

        return nmTenant + "/" + nmEnteNorm + "/" + nmStrutNorm + "/" + cdRegistroNorm + "/" + anno + "/" + idUnitaDoc
                + "/" + pgVerIndiceAip;
    }

    private static void checkConfiguration(ObjectStorageBackend configuration) throws ObjectStorageException {
        List<String> errors = new ArrayList<>();
        if (configuration.getAddress() == null) {
            errors.add("indirizzo object storage");
        }
        if (StringUtils.isBlank(configuration.getAccessKeyId())) {
            errors.add("access key Id");
        }
        if (StringUtils.isBlank(configuration.getSecretKey())) {
            errors.add("secret Key");
        }
        if (StringUtils.isBlank(configuration.getBucket())) {
            errors.add("nome bucket");
        }
        if (!errors.isEmpty()) {
            throw ObjectStorageException.builder()
                    .message("Parametri mancanti per il collegamento a object storage: {0}", String.join(",", errors))
                    .build();
        }

    }

    /**
     * Restitusce un boolean per la verifica del "link" verso object storage
     * 
     * @param idVerIndiceAip
     *            id versione indice aip
     * 
     * @return boolean true se effettivamente presente su object storage / false altrimenti
     * 
     * @throws ObjectStorageException
     *             eccezione generica
     */
    public boolean existIndiceAipObjectStorage(long idVerIndiceAip) throws ObjectStorageException {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "Select count(ix_aip_os) from AroVerIndiceAipUdObjectStorage ix_aip_os where ix_aip_os.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip",
                    Long.class);
            query.setParameter("idVerIndiceAip", idVerIndiceAip);
            Long result = query.getSingleResult();
            return result > 0;
        } catch (NonUniqueResultException e) {
            throw ObjectStorageException.builder()
                    .message("Errore verifica presenza AroVerIndiceAipUdObjectStorage per id versione indice aip {0} ",
                            idVerIndiceAip)
                    .cause(e).build();
        }
    }

    /**
     * Ottieni gli attributi dell'oggetto dall'object storage selezionato.
     *
     * @param configuration
     *            configurazione per accedere all'object storage
     * @param bucket
     *            bucket
     * @param objectKey
     *            chiave
     *
     * @return Attributi dell'oggetto ottenuto
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public GetObjectAttributesResponse getObjectAttributes(ObjectStorageBackend configuration, String bucket,
            String objectKey) throws ObjectStorageException {
        try {
            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());
            GetObjectAttributesRequest getObjectAttributesRequest = GetObjectAttributesRequest.builder().bucket(bucket)
                    .key(objectKey).build();
            return s3SourceClient.getObjectAttributes(getObjectAttributesRequest);
        } catch (AwsServiceException | SdkClientException e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile ottenere dal bucket {1} gli attributi dell'oggetto con chiave {2}",
                            configuration.getBackendName(), bucket, objectKey)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni i metadati dell'oggetto dall'object storage selezionato.
     *
     * @param configuration
     *            configurazione per accedere all'object storage
     * @param bucket
     *            bucket
     * @param objectKey
     *            chiave
     *
     * @return Metadati dell'oggetto ottenuto
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public HeadObjectResponse getObjectMetadata(ObjectStorageBackend configuration, String bucket, String objectKey)
            throws ObjectStorageException {
        try {
            S3Client s3SourceClient = s3Clients.getClient(configuration.getAddress(), configuration.getAccessKeyId(),
                    configuration.getSecretKey());
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucket).key(objectKey).build();
            return s3SourceClient.headObject(headObjectRequest);
        } catch (AwsServiceException | SdkClientException e) {
            throw ObjectStorageException.builder()
                    .message("{0}: impossibile ottenere dal bucket {1} i metadati dell'oggetto con chiave {2}",
                            configuration.getBackendName(), bucket, objectKey)
                    .cause(e).build();
        }

    }

    /**
     * Ottieni il collegamento tra l'indice aip dell'unita documentaria e il suo bucket/chiave su OS.
     *
     * @param idVerIndiceAip
     *            id versione indice aip
     *
     * @return record contenete il link
     *
     * @throws ObjectStorageException
     *             in caso di errore
     */
    public AroVerIndiceAipUdObjectStorage getLinkAroVerIndiceAipUdOs(long idVerIndiceAip)
            throws ObjectStorageException {
        try {
            TypedQuery<AroVerIndiceAipUdObjectStorage> query = entityManager.createQuery(
                    "Select ix_aip_os from AroVerIndiceAipUdObjectStorage ix_aip_os where ix_aip_os.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip",
                    AroVerIndiceAipUdObjectStorage.class);
            query.setParameter("idVerIndiceAip", idVerIndiceAip);
            return query.getSingleResult();

        } catch (IllegalArgumentException e) {
            throw ObjectStorageException.builder().message(
                    "Errore durante il recupero da AroVerIndiceAipUdObjectStorage per id versione indice aip  {0} ",
                    idVerIndiceAip).cause(e).build();
        }

    }
    // end MEV#30395
}
