package com.cgi.poc.dw.resources;

import com.cgi.poc.dw.core.Asset;
import com.cgi.poc.dw.core.User;
import com.cgi.poc.dw.db.AssetDAO;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssetsResourceTest {

    /**
     * Test user id.
     */
    private static final int USER_ID = 1;
    /**
     * Test user name.
     */
    private static final String USERNAME = "Coda";
    /**
     * Test user password.
     */
    private static final String PASSWORD = "Hale";
    /**
     * The id of the expected asset.
     */
    private static final int ASSET_ID = 1;
    /**
     * The URL of the expected asset.
     */
    private static final String URL
            = "https://github.com/cgi/CgiPocDw";
    /**
     * Test user.
     */
    private static final User USER = new User(USERNAME, PASSWORD);

    /**
     * Mocks asset DAO for resource testing purposes.
     */
    private static final AssetDAO ASSET_DAO = mock(AssetDAO.class);

    /**
     * Special class that automatically provides user credentials when a
     * resource method is accessed.
     */
    private static final HttpAuthenticationFeature FEATURE
            = HttpAuthenticationFeature.basic(USERNAME, PASSWORD);

    /**
     * Basic authentication filter based on fake authenticator.
     */
    private static final BasicCredentialAuthFilter FILTER
            = new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(credentials -> Optional.of(USER))
            .setAuthorizer((principal, role) -> true)
            .setRealm("SECURITY REALM")
            .buildAuthFilter();

    /**
     * Instruction to spin up in-memory server to test resource classes.
     *
     */
    @ClassRule
    public static final ResourceTestRule RULE
            = ResourceTestRule
            .builder()
            .addProvider(new AuthDynamicFeature(FILTER))
            .addProvider(RolesAllowedDynamicFeature.class)
            .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .addResource(new AssetsResource(ASSET_DAO))
            .build();

    /**
     * A asset for testing purposes.
     */
    private Asset expectedAsset;
    /**
     * A list of assets for testing purposes.
     */
    private List<Asset> assets;

    /**
     * Methods that provides class-level initialization.
     */
    @BeforeClass
    public static void beforeClass() {
        USER.setId(USER_ID);
        // Enable authomatic authentication.
        RULE.getJerseyTest().client().register(FEATURE);
    }

    /**
     * Initialization before each method.
     */
    @Before
    public void setUp() {
        assets = new ArrayList<>();
        expectedAsset = new Asset(
                "https://bitbucket.org/cgi/cgi-poc-dw",
                "Old project version");
        expectedAsset.setId(2);
        assets.add(expectedAsset);

        expectedAsset = new Asset(URL, "The repository of this project");
        expectedAsset.setId(ASSET_ID);
        assets.add(expectedAsset);
    }

    /**
     * Clean up after each method.
     */
    @After
    public void dearDown() {
        reset(ASSET_DAO);
        assets.clear();
    }

    /**
     * Test of getAssets method, of class AssetsResource.
     */
    @Test
    public void testGetAssets() {
        // given
        when(ASSET_DAO.findByUserId(USER_ID))
                .thenReturn(Collections.unmodifiableList(assets));

        // when
        final List<Asset> response = RULE
                .getJerseyTest()
                .target("/assets")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Asset>>() {
                });

        //then
        verify(ASSET_DAO).findByUserId(USER_ID);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(assets.size(), response.size());
        assertTrue(response.containsAll(assets));
    }

    /**
     * Test of getAsset method, of class AssetsResource.
     */
    @Test
    public void testGetAssetFound() {
        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.of(expectedAsset));

        // when
        final Optional<Asset> response
                = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Optional<Asset>>() {
                });

        // then
        verify(ASSET_DAO).findByIdAndUserId(ASSET_ID, USER_ID);
        assertNotNull(response);
        assertTrue(response.isPresent());
        assertEquals(expectedAsset, response.get());
    }

    /**
     * Test of getAsset method, of class AssetsResource.
     */
    @Test(expected = NotFoundException.class)
    public void testGetAssetNotFound() {
        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.empty());

        // when
        final Optional<Asset> response
                = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Optional<Asset>>() {
                });

        // then
        verify(ASSET_DAO).findByIdAndUserId(ASSET_ID, USER_ID);
        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    /**
     * Test of addAsset method, of class AssetsResource.
     */
    @Test
    public void testAddAssetOK() {
        ArgumentCaptor<Asset> argumentCaptor
                = ArgumentCaptor.forClass(Asset.class);

        // given
        when(ASSET_DAO.save(any(Asset.class
        )))
                .thenReturn(expectedAsset);

        // when
        final Asset response
                = RULE
                .getJerseyTest()
                .target("/assets")
                .request(MediaType.APPLICATION_JSON)
                .post(
                        Entity.entity(
                                expectedAsset,
                                MediaType.APPLICATION_JSON),
                        Asset.class);
        // then
        assertNotNull(response);
        verify(ASSET_DAO)
                .save(argumentCaptor.capture());

        Asset value = argumentCaptor.getValue();
        assertNotNull(value);
        assertNotNull(value.getUser());
        assertEquals(value.getUser(), USER);

        assertEquals(expectedAsset, response);
    }

    /**
     * Test of addAsset method, of class AssetsResource.
     */
    @Test
    public void testAddAssetInvalid() {
        final Response response
                = RULE
                .getJerseyTest()
                .target("/assets")
                .request(MediaType.APPLICATION_JSON)
                .post(
                        Entity.json(
                                new Asset(null, null)));

        assertEquals(422, response.getStatus());
    }

    /**
     * Test of modifyAsset method, of class AssetsResource.
     */
    @Test
    public void testModifyAssetOK() {
        String expectedURL
                = "https://github.com/cgi/DwAssets";
        ArgumentCaptor<Asset> argumentCaptor
                = ArgumentCaptor.forClass(Asset.class);

        Asset assetWithModifications
                = new Asset(expectedURL, null);
        assetWithModifications.setId(109678);

        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.of(expectedAsset));
        when(ASSET_DAO.save(any(Asset.class)))
                .thenReturn(expectedAsset);

        // when
        Asset response = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(
                        assetWithModifications,
                        MediaType.APPLICATION_JSON),
                        Asset.class);

        // then
        assertNotNull(response);
        assertEquals(expectedURL, response.getUrl());
        assertEquals(expectedAsset.getDescription(),
                response.getDescription());
        assertEquals(expectedAsset.getUser(),
                response.getUser());

        verify(ASSET_DAO).save(argumentCaptor.capture());
        assertNotNull(argumentCaptor.getValue());
        assertEquals(expectedURL, argumentCaptor.getValue().getUrl());
        assertNotEquals(URL, argumentCaptor.getValue().getUrl());
        assertEquals(expectedAsset.getDescription(),
                argumentCaptor.getValue().getDescription());
        assertEquals(expectedAsset.getUser(),
                argumentCaptor.getValue().getUser());
        // Check that purgeMap was called.
        assertEquals(ASSET_ID,
                argumentCaptor.getValue().getId().intValue());
    }

    /**
     * Test of modifyAsset method, of class AssetsResource.
     */
    @Test(expected = NotFoundException.class)
    public void testModifyAssetNotFound() {
        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.empty());

        // when
        RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(
                        expectedAsset,
                        MediaType.APPLICATION_JSON),
                        Asset.class);

        // then
    }

    /**
     * Test of modifyAsset method, of class AssetsResource.
     */
    @Test
    public void testModifyAssetInvalid() {
        String expectedKey = "wrongKey";

        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.of(expectedAsset));

        // when
        Response response = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(expectedKey,
                        MediaType.APPLICATION_JSON));

        // then
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                response.getStatus());

        verify(ASSET_DAO, times(0)).save(any(Asset.class));
    }

    /**
     * Test of modifyAsset method, of class AssetsResource.
     */
    @Test(expected = ProcessingException.class)
    public void testModifyAssetInvalidException() {
        String expectedKey = "wrongKey";

        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.of(expectedAsset));

        // when
        Asset response = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(expectedKey,
                        MediaType.APPLICATION_JSON))
                .readEntity(Asset.class);

        // then
    }

    /**
     * Test of deleteAsset method, of class AssetsResource.
     */
    @Test
    public void testDeleteAssetOK() {
        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.of(expectedAsset));

        //when
        Asset response = RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .delete(Asset.class);

        //then
        assertNotNull(response);
        assertEquals(expectedAsset, response);

        verify(ASSET_DAO).delete(ASSET_ID);
    }

    /**
     * Test of deleteAsset method, of class AssetsResource.
     */
    @Test(expected = NotFoundException.class)
    public void testDeleteAssetNotFound() {
        // given
        when(ASSET_DAO.findByIdAndUserId(ASSET_ID, USER_ID))
                .thenReturn(Optional.empty());

        // when
        RULE
                .getJerseyTest()
                .target("/assets/" + ASSET_ID)
                .request(MediaType.APPLICATION_JSON)
                .delete(Asset.class);

        // then
    }

    /**
     * Test of purgeMap() method
     */
    @Test
    public void testPurgeMap() {
        String expectedKey = "url";
        AssetsResource sut = new AssetsResource(ASSET_DAO);
        Map<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put(expectedKey, "http://www.dropwizard.io/1.0.2/docs/");
        map.put("description", null);

        sut.purgeMap(map);

        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertTrue(map.containsKey(expectedKey));
    }
}
