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
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;

import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author sinatti_s
 */

@Stateless(mappedName = "AwsClientServices")
@LocalBean
public class AwsClientServices {

    private static final Logger log = LoggerFactory.getLogger(AwsClientServices.class);

    private AmazonS3 awsClient = null;

    @EJB
    private ConfigurationHelper configHelper;

    /**
     * Inizializzazione del client S3. TODO: necessario definire la modalità di connessione all'object storage in
     * termini di credenziali. L'indirizzo corretto dovrebbe essere https://s3.storagegrid.ente.regione.emr.it:8082
     */
    @PostConstruct
    private void init() {
        String storageAddress = configHelper.getValoreParamApplic(CostantiDB.ParametroAppl.OBJECT_STORAGE_ADDR, null,
                null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        log.info("Sto per effettuare il collegamento all'endpoint S3 [ " + storageAddress + "]");
        awsClient = AmazonS3Client.builder()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(storageAddress, Regions.US_EAST_1.name()))
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withPathStyleAccessEnabled(Boolean.TRUE).build();
    }

    /*
     * Client Shutdown
     */
    @PreDestroy
    private void destroy() {
        log.info("Shutdown endpoint S3...");
        if (awsClient != null) {
            awsClient.shutdown();
        }
    }

    /**
     * Recupero blob dell'oggetto via api (AWS) su object storage
     * 
     * @param tenant
     *            parte della chiave (tenant)
     * @param bucket
     *            parte della chiave (bucket)
     * @param key
     *            parte della chiave (chiave oggetto)
     * 
     * @return RispostaControlli dto con risposta operazione
     */
    public RispostaControlli getS3Object(String tenant, String bucket, String key) {
        RispostaControlli rc;
        rc = new RispostaControlli();
        rc.setrBoolean(false);

        try {
            log.debug("Ottengo oggetto identificato da [tenant=\"" + tenant + "\", bucket=\"" + bucket + "\", key=\""
                    + key + "\"]");
            S3Object s3Object = awsClient.getObject(bucket, key);
            rc.setrObject(s3Object);
            rc.setrBoolean(true);
        } catch (SdkClientException e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione RecObjectStorage.getS3Object " + e.getMessage()));
            log.error("Eccezione nel recupero object storage ", e);
        }

        return rc;
    }
}
