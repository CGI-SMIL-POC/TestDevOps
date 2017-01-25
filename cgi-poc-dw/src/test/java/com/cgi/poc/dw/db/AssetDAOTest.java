package com.cgi.poc.dw.db;

import com.cgi.poc.dw.core.Asset;
import com.cgi.poc.dw.core.User;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import org.hibernate.context.internal.ManagedSessionContext;
import org.junit.Test;
import static org.junit.Assert.*;

public class AssetDAOTest extends DAOTest {

    /**
     * System under test.
     */
    private AssetDAO sut;

    /**
     * Initializations before each test method.
     *
     * @throws LiquibaseException if something is wrong with Liquibase.
     */
    @Override
    public void setUp() throws LiquibaseException {
        liquibase.update("TEST");
        session = SESSION_FACTORY.openSession();
        sut = new AssetDAO(SESSION_FACTORY);
        tx = null;
    }

    /**
     * Cleanup after each test method.
     *
     * @throws DatabaseException if there is an error with database access.
     * @throws LockException if two clients try to apply migrations
     * simultaneously.
     */
    @Override
    public void tearDown() throws DatabaseException, LockException {
        liquibase.dropAll();
    }

    /**
     * Test of findByUserId method, of class AssetDAO.
     */
    @Test
    public void testFindByUserId() {
        List<Asset> assets = null;
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //Do something here with UserDAO
            assets = sut.findByUserId(1);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }
        assertNotNull(assets);
        assertFalse(assets.isEmpty());
    }

    /**
     * Test of findById method, of class AssetDAO.
     */
    @Test
    public void testFindById() {
        String expectedUrl = "https://github.com/cgi/CgiPocDw";
        String expectedDescription = "Repo for this project";
        // An expectedId of a user added by a migration
        int userId = 1;
        // A generated expectedId of a asset
        Integer bmId;
        Optional<Asset> optional;
        Asset asset;

        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //Add a asset
            session
                    .createSQLQuery(
                            "insert into assets "
                            + "values(null, :url, :description, :userId)"
                    )
                    .setString("url", expectedUrl)
                    .setString("description", expectedDescription)
                    .setInteger("userId", userId)
                    .executeUpdate();

            BigInteger result = (BigInteger) session
                    .createSQLQuery(
                            "select id from assets "
                            + "where url = :url "
                            + "and description = :description "
                            + "and user_id = :userId"
                    )
                    .setString("url", expectedUrl)
                    .setString("description", expectedDescription)
                    .setInteger("userId", userId)
                    .uniqueResult();

            bmId = result.intValue();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(bmId);
        //Reopen session
        session = SESSION_FACTORY.openSession();
        tx = null;

        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //Look for a asset
            optional = sut.findById(bmId);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        asset = optional.get();
        assertEquals(expectedUrl, asset.getUrl());
    }

    /**
     * Test of save method, of class AssetDAO.
     */
    @Test
    public void testSave() {
        String expectedUrl = "https://github.com/cgi/CgiPocDw";
        String actualUrl;
        String expectedDescription = "Repo for this project";
        // An expectedId of a user added by a migration
        int userId = 1;
        Integer bmID;
        Asset addedAsset = new Asset(expectedUrl, expectedDescription);
        UserDAO userDAO = new UserDAO(SESSION_FACTORY);

        // Add a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //obtain a user
            User user = userDAO.findById(userId).get();
            addedAsset.setUser(user);
            //Save Asset
            bmID = sut.save(addedAsset).getId();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(bmID);
        //Reopen session
        session = SESSION_FACTORY.openSession();
        tx = null;

        // Extract the asset;
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            actualUrl = (String) session
                    .createSQLQuery(
                            "select url from assets "
                            + "where id = :id"
                    )
                    .setInteger("id", bmID)
                    .uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(actualUrl);
        assertFalse(actualUrl.isEmpty());
        assertEquals(expectedUrl, actualUrl);
    }

    /**
     * Test of delete method, of class AssetDAO.
     */
    @Test
    public void testDelete() {
        String expectedUrl = "https://github.com/cgi/CgiPocDw";
        String actualUrl;
        String expectedDescription = "Repo for this project";
        // An expectedId of a user added by a migration
        int userId = 1;
        // A generated expectedId of a asset
        Integer bmId;

        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //Add a asset
            session
                    .createSQLQuery(
                            "insert into assets "
                            + "values(null, :url, :description, :userId)"
                    )
                    .setString("url", expectedUrl)
                    .setString("description", expectedDescription)
                    .setInteger("userId", userId)
                    .executeUpdate();

            BigInteger result = (BigInteger) session
                    .createSQLQuery(
                            "select id from assets "
                            + "where url = :url "
                            + "and description = :description "
                            + "and user_id = :userId"
                    )
                    .setString("url", expectedUrl)
                    .setString("description", expectedDescription)
                    .setInteger("userId", userId)
                    .uniqueResult();

            bmId = result.intValue();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(bmId);
        //Reopen session
        session = SESSION_FACTORY.openSession();
        tx = null;

        //delete a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            //Delete a asset
            sut.delete(bmId);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        //Reopen session
        session = SESSION_FACTORY.openSession();
        tx = null;

        //look for a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            actualUrl = (String) session
                    .createSQLQuery(
                            "select url from assets "
                            + "where id = :id"
                    )
                    .setInteger("id", bmId)
                    .uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNull(actualUrl);
    }

    /**
     * Test findByIdAndUserId() method
     */
    @Test
    public void findByIdAndUserIdOk() {
        Optional<Asset> optional;
        final int expectedId = 1;
        final int expectedUserId = 1;
        //look for a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            optional = sut.findByIdAndUserId(expectedId, expectedUserId);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(expectedId, optional.get().getId().intValue());
    }

    /**
     * Test findByIdAndUserId() method
     */
    @Test
    public void findByIdAndUserIdWrongUserId() {
        Optional<Asset> optional;
        final int expectedId = 1;
        final int expectedUserId = 2;
        //look for a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            optional = sut.findByIdAndUserId(expectedId, expectedUserId);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    /**
     * Test findByIdAndUserId() method
     */
    @Test
    public void findByIdAndUserIdWrongId() {
        Optional<Asset> optional;
        final int expectedId = 109678;
        final int expectedUserId = 1;
        //look for a asset
        try {
            ManagedSessionContext.bind(session);
            tx = session.beginTransaction();

            optional = sut.findByIdAndUserId(expectedId, expectedUserId);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            ManagedSessionContext.unbind(SESSION_FACTORY);
            session.close();
        }

        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }
}
