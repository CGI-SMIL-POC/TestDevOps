package com.cgi.poc.dw.auth;

import com.cgi.poc.dw.core.User;
import com.cgi.poc.dw.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;

/**
 * Class for authenticating users using backing database.
 *
 */
public class DBAuthenticator implements Authenticator<BasicCredentials, User> {

    /**
     * Reference to User DAO to check whether the user with credentials
     * specified exists in the application's backing database.
     */
    private final UserDAO userDAO;
    /**
     * Hibernate session factory; Necessary for the authenticate method to work,
     * which doesn't work as described in the documentation.
     */
    private final SessionFactory sessionFactory;
    /**
     * A helper class for password encryption; Thread-safe.
     */
    private final PasswordEncryptor passwordEncryptor
            = new BasicPasswordEncryptor();

    /**
     * A constructor to initialize DAO.
     *
     * @param userDAO The DAO for the User object necessary to look for users by
     * their credentials.
     * @param sessionFactory Hibernate session factory; temporary solution as
     * database authentication doesn't work as described in documentation.
     */
    public DBAuthenticator(final UserDAO userDAO,
            final SessionFactory sessionFactory) {
        this.userDAO = userDAO;
        this.sessionFactory = sessionFactory;
    }

    /**
     * Implementation of the authenticate method.
     *
     * @param credentials An instance of the BasicCredentials class containing
     * username and password.
     * @return An Optional containing the user characterized by credentials or
     * an empty optional otherwise.
     * @throws AuthenticationException throws an exception in the case of
     * authentication problems.
     */
    @UnitOfWork
    @Override
    public final Optional<User> authenticate(BasicCredentials credentials)
            throws AuthenticationException {
        Session session = sessionFactory.openSession();
        Optional<User> result;
        try {
            ManagedSessionContext.bind(session);

            result = userDAO.findByUsername(credentials.getUsername());

            if (!result.isPresent()) {
                return result;
            } else               {
                if (passwordEncryptor.checkPassword(
                        credentials.getPassword(),
                        result.get().getPassword())) {
                    return result;
                } else {
                    return Optional.empty();
                }
            }

        } catch (Exception e) {
            throw new AuthenticationException(e);
        } finally {
            ManagedSessionContext.unbind(sessionFactory);
            session.close();
        }

    }

}
