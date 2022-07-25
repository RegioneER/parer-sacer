/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.objectstorage.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;

import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;

@Stateless(mappedName = "ReportvfAwsClientServices")
@LocalBean
public class ReportvfAwsClientServices {

    private static final Logger LOG = LoggerFactory.getLogger(ReportvfAwsClientServices.class);

    private AmazonS3 awsClient = null;

    @EJB
    private ConfigurationHelper configHelper;

    private String bucketName;

    /*
     * Inizializzazione del client S3.
     *
     */
    @PostConstruct
    private void init() {
        // address
        String storageAddress = configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.OBJECT_STORAGE_ADDR, null,
                null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        // aws credentials
        final String reportvfAccessKeyId = configHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.REPORTVF_S3_ACCESS_KEY_ID, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        final String reportvfSecretKeyId = configHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.REPORTVF_S3_SECRET_KEY, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        // recupero le system properties
        final String accessKeyId = System.getProperty(reportvfAccessKeyId);
        final String secretKey = System.getProperty(reportvfSecretKeyId);
        // create basic credentials
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);

        LOG.info("Sto per effettuare il collegamento all'endpoint S3 [{}]", storageAddress);
        awsClient = AmazonS3Client.builder()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(storageAddress, Regions.US_EAST_1.name()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withPathStyleAccessEnabled(Boolean.TRUE)
                .build();

        // bucket
        bucketName = configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.BUCKET_REPORT_VERIFICAFIRMA, null, null,
                null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);

    }

    /*
     * Client Shutdown
     */
    @PreDestroy
    private void destroy() {
        LOG.info("Shutdown endpoint S3...");
        if (awsClient != null) {
            awsClient.shutdown();
        }
    }

    /**
     * Recupero blob dell'oggetto via api (AWS) su object storage
     * 
     * @param bucket
     *            parte della chiave (bucket)
     * @param key
     *            parte della chiave (chiave oggetto)
     * 
     * @return RispostaControlli dto con risposta operazione
     */
    public RispostaControlli getS3Object(String bucket, String key) {
        RispostaControlli rc;
        rc = new RispostaControlli();
        rc.setrBoolean(false);

        try {
            LOG.debug("Ottengo oggetto identificato da [bucket={}, key={}]", bucket, key);
            S3Object s3Object = awsClient.getObject(bucket, key);
            rc.setrObject(s3Object);
            rc.setrBoolean(true);
        } catch (SdkClientException e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ReportvfAwsClientServices.getS3Object " + e.getMessage()));
            LOG.error("Eccezione nel recupero object storage ", e);
        }

        return rc;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
