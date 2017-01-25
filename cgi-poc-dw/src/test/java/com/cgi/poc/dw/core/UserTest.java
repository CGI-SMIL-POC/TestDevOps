package com.cgi.poc.dw.core;

import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * http://hibernate.org/validator/documentation/getting-started/
 */
public class UserTest extends EntityTest {

    /**
     * Test of setId method, of class User.
     */
    @Test(expected = NullPointerException.class)
    public void idIsNull() {
        User user = new User("Coda", "1");
        user.setId(null);
    }

    /**
     * Test of setId method, of class User.
     */
    @Test
    public void idIsOK() {
        User user = new User("Coda", "1");
        user.setId(1);
        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    /**
     * Test of the constructor of the User class.
     */
    @Test
    public void usernameIsNull() {
        User user = new User(null, "1");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of the constructor of the User class.
     */
    @Test
    public void passwordIsNull() {
        User user = new User("Coda", null);
        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of the constructor of the User class.
     */
    @Test
    public void constructorOK() {
        User user = new User("Coda", "1");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    /**
     * Test of setUsername method, of class User.
     */
    @Test
    public void testSetUsernameIsNull() {
        User user = new User("Coda", "1");
        user.setUsername(null);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of setUsername method, of class User.
     */
    @Test
    public void testSetUsernameIsEmpty() {
        User user = new User("Coda", "1");
        user.setUsername("");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of setUsername method, of class User.
     */
    @Test
    public void testSetUsernameIsOk() {
        User user = new User("Coda", "1");
        user.setUsername("Coda");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    /**
     * Test of setPassword method, of class User.
     */
    @Test
    public void testSetPasswordIsNull() {
        User user = new User("Coda", "1");
        user.setPassword(null);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_NOT_NULL, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of setPassword method, of class User.
     */
    @Test
    public void testSetPasswordIsEmpty() {
        User user = new User("Coda", "1");
        user.setPassword("");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertFalse(constraintViolations.isEmpty());
        assertEquals(ERROR_LENGTH, constraintViolations
                .iterator()
                .next()
                .getMessage());
    }

    /**
     * Test of setPassword method, of class User.
     */
    @Test
    public void testSetPasswordIsOk() {
        User user = new User("Coda", "1");
        user.setPassword("2");

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());
    }

    /**
     * Test of addAsset method, of class User.
     */
    @Test(expected = NullPointerException.class)
    public void testAddAsset() {
        User user = new User("Coda", "1");
        user.addAsset(null);
    }

    /**
     * Test of addAsset method, of class User.
     */
    @Test
    public void testAddAssetIsNull() {
        User user = new User("Coda", "1");
        user.addAsset(new Asset());
        int expectedId = 1;
        user.setId(expectedId);

        Set<ConstraintViolation<User>> constraintViolations
                = validator.validate(user);

        assertTrue(constraintViolations.isEmpty());

        Asset asset = user.getAssets().iterator().next();

        assertEquals(expectedId, asset.getUser().getId().intValue());
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsOtherIsNull() {
        User user = new User("Coda", "1");
        assertFalse(user.equals(null));
        assertNotEquals(user.hashCode(), Objects.hashCode(null));
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsOtherIsSame() {
        User user = new User("Coda", "1");
        assertTrue(user.equals(user));
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsOtherIsAsset() {
        User user = new User("Coda", "1");
        assertFalse(user.equals(new Asset()));
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsAnotherUser() {
        User user = new User("Coda", "1");
        User other = new User();
        assertFalse(user.equals(other));
        assertNotEquals(user.hashCode(), other.hashCode());
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsOk() {
        User user = new User("Coda", "1");
        int expectedId = 1;
        user.setId(expectedId);
        User other = new User("Coda", "1");
        other.setId(expectedId);
        assertTrue(user.equals(other));
        assertEquals(user.hashCode(), other.hashCode());
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsIdIsNull() {
        User user = new User("Coda", "1");
        User other = new User("Coda", "1");
        assertTrue(user.equals(other));
        assertEquals(user.hashCode(), other.hashCode());
    }

    /**
     * Test of equals method, of class User.
     */
    @Test
    public void testEqualsOtherIdIsNull() {
        User user = new User("Coda", "1");
        int expectedId = 1;
        user.setId(expectedId);
        User other = new User("Coda", "1");
        assertFalse(user.equals(other));
        assertNotEquals(user.hashCode(), other.hashCode());
    }
}
