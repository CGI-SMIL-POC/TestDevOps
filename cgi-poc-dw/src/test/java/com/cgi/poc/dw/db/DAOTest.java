package com.cgi.poc.dw.db;

import java.sql.Connection;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * An abstract class which contains all the necessary initializations for
 * testing Dropwizard DAO classes. Intended to be a superclass for DAOs tests.
 *
 */
public abstract class DAOTest {

    /**
     * Hibernate session factory.
     */
    protected static final SessionFactory SESSION_FACTORY
            = HibernateUtil.getSessionFactory();
    /**
     * A handle to apply Liquibase DB refactorings programmatically.
     */
    protected static Liquibase liquibase = null;

    /**
     * Initializations before all test methods.
     * http://myjourneyonjava.blogspot.ca/2014/12/different-ways-to-get-connection-object.html
     *
     * @throws LiquibaseException if something is wrong with Liquibase.
     * @throws SQLException if there is an error with database access.
     */
    @BeforeClass
    public static void setUpClass() throws LiquibaseException, SQLException {
        final Session session = SESSION_FACTORY.openSession();
        final SessionImpl sessionImpl = (SessionImpl) session;
        final Connection connection = sessionImpl.connection();
        final Database database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(
                        new JdbcConnection(connection)
                );
        liquibase = new Liquibase(
                "migrations.xml",
                new ClassLoaderResourceAccessor(),
                database
        );
        session.close();
    }

    /**
     * Clean up after all test methods.
     */
    @AfterClass
    public static void tearDownClass() {
        //SESSION_FACTORY.close();
    }
    /**
     * Hibernate session.
     */
    protected Session session;
    /**
     * Hibernate transaction.
     */
    protected Transaction tx;

    /**
     * Initializations before each test method.
     *
     * @throws LiquibaseException if something is wrong with Liquibase.
     */
    @Before
    public abstract void setUp() throws LiquibaseException;

    /**
     * Cleanup after each test method.
     *
     * @throws DatabaseException if there is an error with database access.
     * @throws LockException if two clients try to apply migrations
     * simultaneously.
     */
    @After
    public abstract void tearDown() throws DatabaseException, LockException;

}
