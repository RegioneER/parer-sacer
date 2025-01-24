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
package it.eng.parer.firma.crypto.verifica;

import java.io.File;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.eng.parer.crypto.model.CryptoSignedP7mUri;
import it.eng.parer.crypto.model.ParerCRL;
import it.eng.parer.crypto.model.ParerTSD;
import it.eng.parer.crypto.model.ParerTST;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.firma.crypto.helper.CryptoRestConfiguratorHelper;
import it.eng.parer.retry.ParerRetryConfiguration;
import it.eng.parer.retry.RestRetryInterceptor;

/**
 * Invoca la Cryptolibrary.
 *
 * @author Snidero_L
 */
@Stateless
public class CryptoInvoker {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoInvoker.class.getName());
    private static final String CRL_CTX = "/api/crl";
    private static final String TST_CTX = "/api/tst";
    private static final String TSD_CTX = "/api/tsd";
    private static final String UNSIGNED_P7M_CTX = "/api/unsigned-p7m";

    private static final Logger log = LoggerFactory.getLogger(CryptoInvoker.class);

    @EJB
    protected CryptoRestConfiguratorHelper restInvoker;

    private static final int BUFFERSIZE = 10 * 1024 * 1024; // 10 megabyte

    /**
     * Health check per il cluster.(Attualmente non è utilizzato)
     *
     * @param url
     *            lista URI
     *
     * @return true/false se servizio UP and running
     */
    public boolean isUp(String url) {
        boolean up = true;
        try {
            RestTemplate restClient = new RestTemplate();
            restClient.setErrorHandler(new CryptoErrorHandler());
            restClient.getForEntity(url, Object.class);
        } catch (Exception ex) {
            LOG.warn("Impossibile contattare " + url, ex);
            up = false;
        }
        return up;
    }

    /**
     * Ottieni la CRL a partire dal certificato del firmatario.
     *
     * @param blobFilePerFirma
     *            blob del firmatario
     *
     * @return Oggetto contente la CRL
     *
     * @throws CryptoParerException
     *             Errore (gestito)
     * @throws RestClientException
     *             Errore sullo strato REST
     */
    public ParerCRL retrieveCRL(byte[] blobFilePerFirma) {

        RestTemplate restTemplate = buildRestTemplateWithRetry();

        String baseUrl = restInvoker.preferredEndpoint();

        String endpoint = baseUrl + CRL_CTX;

        String certificatoFirmatarioBase64 = Base64.getUrlEncoder().encodeToString(blobFilePerFirma);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
                .queryParam("certifFirmatarioBase64UrlEncoded", certificatoFirmatarioBase64);

        String url = builder.build().toUriString();
        LOG.debug("retreive crl da {}", url);
        ResponseEntity<ParerCRL> crlEntity = restTemplate.getForEntity(url, ParerCRL.class);
        return crlEntity.getBody();
    }

    /**
     * Ottieni la CRL a partire dal DN e dal keyID della CA.
     *
     * @param dnCa
     *            DN della CA
     * @param keyId
     *            keyID della CA
     *
     * @return Oggetto contente la CRL
     *
     * @throws CryptoParerException
     *             Errore (gestito)
     * @throws RestClientException
     *             Errore sullo strato REST
     */
    public ParerCRL retrieveCRL(String dnCa, String keyId) {
        if (StringUtils.isBlank(dnCa) || StringUtils.isBlank(keyId)) {
            throw new IllegalArgumentException("Parametri dnCa/keyId vuoti.");
        }
        RestTemplate restTemplate = buildRestTemplateWithRetry();

        String baseUrl = restInvoker.preferredEndpoint();

        String endpoint = baseUrl + CRL_CTX;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint).path("/")
                .path(ParerCRL.calcolaUniqueId(dnCa, keyId));

        String url = builder.build().toUriString();
        LOG.debug("retreive crl da {}", url);
        ResponseEntity<ParerCRL> crlEntity = restTemplate.getForEntity(url, ParerCRL.class);
        return crlEntity.getBody();
    }

    /**
     * Restiuscire la CRL utilizzando la lista di url passati in input. <em>NOTA</em> NON restituisce NULL
     *
     * @param urls
     *            lista URI
     *
     * @return la CRL (se c'è)
     *
     * @throws CryptoParerException
     *             se non c'è la crl
     * @throws RestClientException
     *             eccezione generica
     */
    public ParerCRL addCrlByURL(List<String> urls) {
        RestTemplate restTemplate = buildRestTemplateWithRetry();

        String preferredEndPoint = restInvoker.preferredEndpoint();

        String endpoint = preferredEndPoint + CRL_CTX;

        LOG.debug("POST crl by url {}", endpoint);
        HttpEntity<List<String>> request = new HttpEntity<>(urls);
        return restTemplate.postForObject(endpoint, request, ParerCRL.class);

    }

    /**
     * Ottieni la marca temporale.
     *
     * @param fileVerSerie
     *            contenuto da marcare.
     *
     * @return Marca temporale per l'oggetto in input.
     */
    public ParerTST requestTST(byte[] fileVerSerie) {
        RestTemplate restTemplate = buildRestTemplateWithRetry();
        ParerTST result = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource(fileVerSerie) {
            @Override
            public String getFilename() {
                return "requestTst";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("description", "Richiesta TST");
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String preferredEndPoint = restInvoker.preferredEndpoint();

        String endpoint = preferredEndPoint + TST_CTX;
        LOG.debug("post per {}", endpoint);

        result = restTemplate.postForObject(endpoint, requestEntity, ParerTST.class);

        return result;
    }

    /**
     * Ottieni il file sbustato dal file .p7m passato.
     *
     * @param fileP7m
     *            contenuto da sbustare.
     *
     * @return Oggetto sbustato.
     */
    public byte[] retriveOriginalP7mFromFile(File fileP7m) {
        RestTemplate restTemplate = buildRestTemplateWithRetry();
        ResponseEntity<byte[]> result = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("signed-p7m", new FileSystemResource(fileP7m));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String preferredEndPoint = restInvoker.preferredEndpoint();

        String endpoint = preferredEndPoint + UNSIGNED_P7M_CTX;
        LOG.debug("post per {}", endpoint);

        result = restTemplate.postForEntity(endpoint, requestEntity, byte[].class);
        return result.getBody();
    }

    /**
     * Ottieni il file sbustato da URL del file .p7m passato.
     *
     * @param dto
     *            modello con URL e nome file (opzionale)
     *
     * @return Oggetto sbustato.
     */
    public byte[] retriveOriginalP7mFromURL(CryptoSignedP7mUri dto) {
        RestTemplate restTemplate = buildRestTemplateWithRetry();
        ResponseEntity<byte[]> result = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CryptoSignedP7mUri> entity = new HttpEntity<>(dto, headers);

        String preferredEndPoint = restInvoker.preferredEndpoint();

        String endpoint = preferredEndPoint + UNSIGNED_P7M_CTX;
        LOG.debug("post per {}", endpoint);

        result = restTemplate.postForEntity(endpoint, entity, byte[].class);
        return result.getBody();
    }

    /**
     * Ottieni l'oggetto marcato.
     *
     * @param fileVerSerie
     *            contenuto da marcare.
     *
     * @return Oggetto in input + marca temporale.
     */
    public ParerTSD generateTSD(byte[] fileVerSerie) {
        RestTemplate restTemplate = buildRestTemplateWithRetry();

        String baseUrl = restInvoker.preferredEndpoint();
        String endpoint = baseUrl + TSD_CTX;
        LOG.debug("post per  {}", endpoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource(fileVerSerie) {
            @Override
            public String getFilename() {
                return "requestTsd";
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("description", "Richiesta TST");
        body.add("file", resource);

        return restTemplate.postForObject(endpoint, body, ParerTSD.class);
    }

    /**
     * Crea il client per le chiamate rest relativo a questo bean
     *
     * @param timeout
     *            timeout in ms
     *
     * @return restTemplate di spring configurato per "crypto"
     */
    private RestTemplate buildRestTemplateWithRetry() {

        RestTemplate template = new RestTemplate();
        int timeout = restInvoker.clientTimeout();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(timeout);
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setConnectionRequestTimeout(timeout);

        template.setRequestFactory(clientHttpRequestFactory);
        template.setErrorHandler(new CryptoErrorHandler());

        List<String> endpoints = restInvoker.endPoints();
        List<URI> endpointsURI = endpoints.stream().map(e -> URI.create(e)).collect(Collectors.toList());

        ParerRetryConfiguration retryClient = restInvoker.retryClient();

        template.getInterceptors().add(new RestRetryInterceptor(endpointsURI, retryClient));

        return template;
    }

}
