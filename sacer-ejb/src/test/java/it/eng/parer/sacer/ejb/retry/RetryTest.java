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

package it.eng.parer.sacer.ejb.retry;

import it.eng.parer.crypto.model.ParerTST;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.firma.crypto.verifica.CryptoErrorHandler;
import it.eng.parer.retry.ParerRetryConfiguration;
import it.eng.parer.retry.RestRetryInterceptor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Test della modalità di retry
 *
 * @author Snidero_L
 */
public class RetryTest {

    private static final int TIMEOUT = 10000;

    private static final String CRYPTO_LOCALE = "http://localhost:8091/";
    private static final String CRYPTO_SVIL_OKD = "https://verificafirma-crypto-parer-svil.parer-apps.ente.regione.emr.it/";
    private static final String CRYPTO_TEST_OKD = "https://verificafirma-crypto-parer-test.parer-apps.ente.regione.emr.it/";
    // private static final String CRYPTO_PROD_OCP =
    // "https://verificafirma-crypto-prod.apps.parerocp.ente.regione.emr.it/";

    private RestTemplate restTemplate;

    private String preferredEndpoint;

    public RetryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(TIMEOUT);
        clientHttpRequestFactory.setConnectTimeout(TIMEOUT);

        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setErrorHandler(new CryptoErrorHandler());

        preferredEndpoint = CRYPTO_TEST_OKD;

        List<URI> endpoints = new ArrayList<>();
        endpoints.add(URI.create(preferredEndpoint));
        endpoints.add(URI.create(CRYPTO_LOCALE));
        endpoints.add(URI.create(CRYPTO_SVIL_OKD));
        // endpoints.add(URI.create(CRYPTO_PROD_OCP));

        ParerRetryConfiguration retryClient = ParerRetryConfiguration.defaultInstance();

        restTemplate.getInterceptors().add(new RestRetryInterceptor(endpoints, retryClient));

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTST() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource("Ceci n'est pas un test".getBytes()) {
            @Override
            public String getFilename() {
                return "requestTst";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("description", "Richiesta TST");
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String endpoint = preferredEndpoint + "v1/tst";
        ParerTST result = restTemplate.postForObject(endpoint, requestEntity, ParerTST.class);
        Assert.assertNotNull(result.getTimeStampInfo());
    }

    @Test(expected = RestClientException.class)
    public void testTSTWithNoValidURL() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource("Ceci n'est pas un test".getBytes()) {
            @Override
            public String getFilename() {
                return "requestTst";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("description", "Richiesta TST");
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String preferredEndpoint = "http://localhost:8090/api/tst";

        List<URI> badEndpoints = new ArrayList<>();
        badEndpoints.add(URI.create("http://localhost:8091/"));
        badEndpoints.add(URI.create("http://localhost:8092/"));
        badEndpoints.add(URI.create("http://localhost:8093/"));
        badEndpoints.add(URI.create("http://localhost:8094/"));

        ParerRetryConfiguration retryClient = ParerRetryConfiguration.defaultInstance();
        restTemplate.getInterceptors().removeIf(i -> true);
        restTemplate.getInterceptors().add(new RestRetryInterceptor(badEndpoints, retryClient));

        restTemplate.postForObject(preferredEndpoint, requestEntity, ParerTST.class);
    }

    @Test
    public void testTSTWithSomeBadURI() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource("Ceci n'est pas un test".getBytes()) {
            @Override
            public String getFilename() {
                return "requestTst";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("description", "Richiesta TST");
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String preferredEndpoint = "http://localhost:8090/api/tst";

        List<URI> badEndpoints = new ArrayList<>();
        badEndpoints.add(URI.create("http://localhost:8091/"));
        badEndpoints.add(URI.create("Br0kenUr1"));
        badEndpoints.add(URI.create("http://localhost:8092/"));
        badEndpoints.add(URI.create("http://localhost:8093/"));
        badEndpoints.add(URI.create("//////////"));
        badEndpoints.add(URI.create("../../"));
        badEndpoints.add(URI.create(CRYPTO_SVIL_OKD));

        ParerRetryConfiguration retryClient = ParerRetryConfiguration.defaultInstance();
        restTemplate.getInterceptors().removeIf(i -> true);
        restTemplate.getInterceptors().add(new RestRetryInterceptor(badEndpoints, retryClient));

        ParerTST result = restTemplate.postForObject(preferredEndpoint, requestEntity, ParerTST.class);
        Assert.assertNotNull(result.getTimeStampInfo());
    }

    @Test(expected = CryptoParerException.class)
    public void testTSTWitoutMandatoryParameter() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        org.springframework.core.io.Resource resource = new ByteArrayResource("Ceci n'est pas un test".getBytes()) {
            @Override
            public String getFilename() {
                return "requestTst";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // body.add("description", "Richiesta TST");
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String preferredEndpoint = "http://localhost:8090/api/tst";

        List<URI> badEndpoints = new ArrayList<>();
        badEndpoints.add(URI.create("http://localhost:8091/"));
        badEndpoints.add(URI.create("Br0kenUr1"));
        badEndpoints.add(URI.create("http://localhost:8092/"));
        badEndpoints.add(URI.create("http://localhost:8093/"));
        badEndpoints.add(URI.create("//////////"));
        badEndpoints.add(URI.create("../../"));
        badEndpoints.add(URI.create(CRYPTO_SVIL_OKD));

        ParerRetryConfiguration retryClient = ParerRetryConfiguration.defaultInstance();
        restTemplate.getInterceptors().removeIf(i -> true);
        restTemplate.getInterceptors().add(new RestRetryInterceptor(badEndpoints, retryClient));

        ParerTST result = restTemplate.postForObject(preferredEndpoint, requestEntity, ParerTST.class);
        Assert.assertNotNull(result.getTimeStampInfo());
    }

}
