package com.cgi.poc.dw.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 * http://stackoverflow.com/questions/36964923/cant-get-hibernateutil-work
 * http://stackoverflow.com/questions/33005348/hibernate-5-org-hibernate-mappingexception-unknown-entity
 *
 */
public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY;

    static {
        try {
            StandardServiceRegistry standardRegistry
                    = new StandardServiceRegistryBuilder().
                    configure("hibernate.cfg.xml")
                    .build();
            Metadata metadata
                    = new MetadataSources(standardRegistry)
                    .getMetadataBuilder().
                    build();
            SESSION_FACTORY = metadata
                    .getSessionFactoryBuilder()
                    .build();
        } catch (Throwable ex) {
            // Log the exception.
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Produces session factory from the hibernate.cfg.xml configuration file.
     *
     * @return Hibernate session factory.
     */
    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
