package com.cgi.poc.dw;

import com.cgi.poc.dw.core.Asset;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.ClassRule;

/**
 * A class to test entire application.
 *
 */
public class IntegrationTest {

    /**
     * HTTP Status Code 422 Unprocessable Entity. Used to test validation.
     */
    private static final int UNPROCESSABLE_ENTITY_HTTP_RESPONSE_CODE = 422;
    /**
     * A path to test configuration file.
     */
    private static final String CONFIG_PATH
            = ResourceHelpers.resourceFilePath("test-config.yml");

    /**
     * Start the application before all test methods.
     */
    @ClassRule
    public static final DropwizardAppRule<CgiPocConfiguration> RULE
            = new DropwizardAppRule<>(
                    CgiPocApplication.class,
                    CONFIG_PATH);

    /**
     * Special class that automatically provides user credentials when a
     * resource method is accessed.
     */
    private static final HttpAuthenticationFeature FEATURE
            = HttpAuthenticationFeature.basic("cgi", "p@ssw0rd");

    /**
     * Base path to resources.
     */
    private static String target;

    /**
     * Path to asset resources.
     */
    private static final String ASSET_PATH
            = "/assets";

    /**
     * The path to the key store which is necessary for HTTPS support.
     */
    private static final String TRUST_STORE_FILE_NAME
            = "cgi-poc-dw.keystore";

    /**
     * The password of the key store.
     */
    private static final String TRUST_STORE_PASSWORD
            = "p@ssw0rd";

    /**
     * Jersey client to access resources.
     */
    private Client client;

    /**
     * Initialization method run before all test methods.
     */
    @BeforeClass
    public static void setUpClass() {
        target = String.format("https://localhost:%d",
                RULE.getLocalPort());

    }

    /**
     * Configuration method run before each test method.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        RULE.getApplication()
                .run("db", "migrate", "-i TEST", CONFIG_PATH);
        SslConfigurator configurator
                = SslConfigurator.newInstance();
        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                .trustStorePassword(TRUST_STORE_PASSWORD);
        SSLContext context = configurator.createSSLContext();
        client = ClientBuilder.newBuilder()
                .sslContext(context)
                .build();
    }

    /**
     * Do cleanup after each method.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        client.close();
        RULE.getApplication()
                .run("db",
                        "drop-all",
                        "--confirm-delete-everything",
                        CONFIG_PATH);
    }

    /**
     * Test getAssets() method.
     */
    @Test
    public void getAssetsUnauthorized() {
        Response response = client.target(target)
                .path(ASSET_PATH)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test getAssets() method.
     */
    @Test
    public void getAssetsOK() {
        client.register(FEATURE);
        List<Asset> response = client.target(target)
                .path(ASSET_PATH)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Asset>>() {
                });

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(2, response.size());
    }

    /**
     * Test getAsset() method.
     */
    @Test
    public void getAssetUnathorized() {
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test getAsset() method.
     */
    @Test
    public void getAssetNotFound() {
        client.register(FEATURE);
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("109678")
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test getAsset() method.
     */
    @Test
    public void getAssetOK() {
        client.register(FEATURE);
        String expectedDescription = "Dropwizard Getting Started";
        Asset response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);

        assertNotNull(response);
        assertEquals(expectedDescription, response.getDescription());
    }

    /**
     * Test add asset method.
     */
    @Test
    public void addAssetUnauthorised() {
        Response response = client.target(target)
                .path(ASSET_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(
                        new Asset("http://localhost:8080", "localhost"),
                        MediaType.APPLICATION_JSON));

        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test add asset method.
     */
    @Test
    public void addAssetInvalid() {
        client.register(FEATURE);
        Response response = client.target(target)
                .path(ASSET_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(
                        new Asset(null, ""),
                        MediaType.APPLICATION_JSON));

        assertNotNull(response);
        assertEquals(UNPROCESSABLE_ENTITY_HTTP_RESPONSE_CODE,
                response.getStatus()
        );
    }

    /**
     * Test add asset method.
     */
    @Test
    public void addAssetOK() {
        client.register(FEATURE);
        String expectedURL = "http://localhost:8080";
        Asset response = client.target(target)
                .path(ASSET_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(new Asset(expectedURL, "localhost"),
                        MediaType.APPLICATION_JSON))
                .readEntity(Asset.class);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(expectedURL, response.getUrl());
    }

    /**
     * Test delete asset method.
     */
    @Test
    public void deleteAssetUnauthorized() {
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test delete asset method.
     */
    @Test
    public void deleteAssetNotFound() {
        client.register(FEATURE);
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("109678")
                .request(MediaType.APPLICATION_JSON)
                .delete();

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test delete asset method.
     */
    @Test
    public void deleteAssetOk() {
        int expestedId = 1;
        client.register(FEATURE);
        Asset response = client.target(target)
                .path(ASSET_PATH)
                .path(String.valueOf(expestedId))
                .request(MediaType.APPLICATION_JSON)
                .delete(Asset.class);

        assertNotNull(response);
        assertEquals(expestedId, response.getId().intValue());
    }

    /**
     * Test modify asset method.
     */
    @Test
    public void modifyAssetUnauthorized() {
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(
                        new Asset("http://localhost:8080", "localhost"),
                        MediaType.APPLICATION_JSON));

        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test modify asset method.
     */
    @Test
    public void modifyAssetNotFound() {
        client.register(FEATURE);
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("109678")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(
                        new Asset("http://localhost:8080", "localhost"),
                        MediaType.APPLICATION_JSON));

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test modify asset method.
     */
    @Test
    public void modifyAssetInvalid() {
        String data = "UNPROCESSABLE_ENTITY";
        client.register(FEATURE);
        Response response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(data,
                        MediaType.APPLICATION_JSON));

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());
    }

    /**
     * Test modify asset method.
     */
    @Test
    public void modifyAssetOK() {
        String expectedURL
                = "https://github.com/cgi/DwAssets";
        Map<String, String> data = new HashMap<>();
        data.put("url", "https://github.com/cgi/DwAssets");
        client.register(FEATURE);
        Asset response = client.target(target)
                .path(ASSET_PATH)
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(data))
                .readEntity(Asset.class);

        assertNotNull(response);
        assertEquals(expectedURL, response.getUrl());
    }
}
