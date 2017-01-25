package com.cgi.poc.dw.auth;

import com.cgi.poc.dw.core.User;
import com.cgi.poc.dw.db.UserDAO;
import io.dropwizard.auth.basic.BasicCredentials;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * A class used to test DB-based basic authentication methods.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DBAuthenticatorTest {

    /**
     * Username for a test user.
     */
    private static final String USERNAME = "Coda";
    /**
     * Password for a test user.
     */
    private static final String PASSWORD = "HALE";
    /**
     * Encoded password.
     */
    private static final String HASHED_PASSWORD
            = "WR1sXzZRJFSBurV6itaZRrgN/m+sKedr";
    /**
     * A test user.
     */
    private static final User USER = new User(USERNAME, HASHED_PASSWORD);
    /**
     * User DAO mock.
     */
    @Mock
    private UserDAO USER_DAO;
    /**
     * Hibernate session factory.
     */
    @Mock
    private SessionFactory sf;
    /**
     * Hibernate session.
     */
    @Mock
    private Session session;
    /**
     * System under test, an authenticator class in this case.
     */
    private DBAuthenticator sut;

    /**
     * A method to initialize SUT before each test.
     */
    @Before
    public void setUp() {
        sut = new DBAuthenticator(USER_DAO, sf);
    }

    /**
     * Test of authenticate method, of class DBAuthenticator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAuthenticateOk() throws Exception {
        // given
        when(USER_DAO.findByUsername(USERNAME))
                .thenReturn(Optional.of(USER));
        when(sf.openSession()).thenReturn(session);

        // when
        Optional<User> optional
                = sut.authenticate(new BasicCredentials(USERNAME, PASSWORD));

        // then
        verify(USER_DAO).findByUsername(USERNAME);
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(USERNAME, optional.get().getUsername());
    }

    /**
     * Test of authenticate method, of class DBAuthenticator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAuthenticateFailure() throws Exception {
        // given
        when(USER_DAO.findByUsername(USERNAME))
                .thenReturn(Optional.empty());
        when(sf.openSession()).thenReturn(session);

        // when
        Optional<User> optional
                = sut.authenticate(new BasicCredentials(USERNAME, PASSWORD));

        // then
        verify(USER_DAO).findByUsername(USERNAME);
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    /**
     * Test of authenticate method, of class DBAuthenticator.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAuthenticateWrongPassword() throws Exception {
        // given
        when(USER_DAO.findByUsername(USERNAME))
                .thenReturn(Optional.of(USER));
        when(sf.openSession()).thenReturn(session);

        // when
        Optional<User> optional
                = sut.authenticate(new BasicCredentials(USERNAME, "p@ssw0rd"));

        // then
        verify(USER_DAO).findByUsername(USERNAME);
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }
}
