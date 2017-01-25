package com.cgi.poc.dw.core;

import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.Test;
import static org.junit.Assert.*;
public class AssetTest extends EntityTest {

    /**
     * Test of setUrl method, of class Asset.
     */
    @Test
    public void testSetUrlIsNull() {
        Asset asset = new Asset();
        asset.setUrl(null);

        Set<ConstraintViolation<Asset>> constraintViolations
                = validator.validate(asset);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of setUrl method, of class Asset.
     */
    @Test
    public void testSetUrlIsEmpty() {
        Asset asset = new Asset();
        asset.setUrl("");

        Set<ConstraintViolation<Asset>> constraintViolations
                = validator.validate(asset);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of equals method, of class Asset.
     */
    @Test
    public void testEqualsNull() {
        String expectedURL = "https://github.com/cgi/CgiPocDw";
        Asset asset = new Asset(
                expectedURL,
                "Project Repository URL");
        Asset other = null;

        assertFalse(asset.equals(other));
    }

    /**
     * Test of equals method, of class Asset.
     */
    @Test
    public void testEqualsSame() {
        String expectedURL = "https://github.com/cgi/CgiPocDw";
        Asset asset = new Asset(
                expectedURL,
                "Project Repository URL");
        Asset other = asset;

        assertTrue(asset.equals(other));
        assertEquals(asset.hashCode(), other.hashCode());
    }

    /**
     * Test of equals method, of class Asset.
     */
    @Test
    public void testEqualsUser() {
        String expectedURL = "https://github.com/cgi/CgiPocDw";
        Asset asset = new Asset(
                expectedURL,
                "Project Repository URL");

        assertFalse(asset.equals(new User()));
    }

    /**
     * Test of equals method, of class Asset.
     */
    @Test
    public void testEqualsOk() {
        String expectedURL = "https://github.com/cgi/CgiPocDw";
        Asset asset = new Asset(
                expectedURL,
                "Project Repository URL");
        Asset other = new Asset(
                expectedURL,
                "Project Repository URL");

        assertTrue(asset.equals(other));
        assertEquals(asset.hashCode(), other.hashCode());
    }

    /**
     * Test of equals method, of class Asset.
     */
    @Test
    public void testEqualsUsersNotEqual() {
        String expectedURL = "https://github.com/cgi/CgiPocDw";
        Asset asset = new Asset(
                expectedURL,
                "Project Repository URL");
        User u1 = new User();
        u1.setId(1);
        asset.setUser(u1);
        Asset other = new Asset(
                expectedURL,
                "Project Repository URL");
        User u2 = new User();
        u2.setId(2);
        other.setUser(u2);

        assertFalse(asset.equals(other));
        assertNotEquals(asset.hashCode(), other.hashCode());
    }

}
