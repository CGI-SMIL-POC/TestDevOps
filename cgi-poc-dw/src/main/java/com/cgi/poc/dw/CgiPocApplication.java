package com.cgi.poc.dw;

import com.cgi.poc.dw.auth.DBAuthenticator;
import com.cgi.poc.dw.core.Asset;
import com.cgi.poc.dw.core.User;
import com.cgi.poc.dw.db.AssetDAO;
import com.cgi.poc.dw.db.UserDAO;
import com.cgi.poc.dw.resources.AssetsResource;
import com.cgi.poc.dw.resources.Authentication;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;

/**
 * Dropwizard Application class.
 *
 */
public class CgiPocApplication
        extends Application<CgiPocConfiguration> {

    /**
     * Create Hibernate bundle.
     */
    private final HibernateBundle<CgiPocConfiguration> hibernateBundle
            = new HibernateBundle<CgiPocConfiguration>(
                    User.class,
                    Asset.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(
                CgiPocConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    /**
     * Application's main method.
     *
     * @param args the main args
     * @throws Exception the main exception
     */
    public static void main(final String[] args) throws Exception {
        new CgiPocApplication().run(args);
    }

    /**
     * Method returns application name.
     *
     * @return the name of the application
     */
    @Override
    public String getName() {
        return "CGI-POC-DW";
    }

    /**
     * Initializations.
     *
     * @param bootstrap the initialize configuration
     */
    @Override
    public void initialize(
            final Bootstrap<CgiPocConfiguration> bootstrap) {
        /**
         * Adding Hibernate bundle.
         */
        bootstrap.addBundle(hibernateBundle);
        /**
         * Adding migrations bundle.
         */
        bootstrap.addBundle(
                new MigrationsBundle<CgiPocConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(
                    CgiPocConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final CgiPocConfiguration configuration,
            final Environment environment) {
        // Create DAOs.
        final UserDAO userDAO
                = new UserDAO(hibernateBundle.getSessionFactory());
        final AssetDAO assetDAO
                = new AssetDAO(hibernateBundle.getSessionFactory());

        // Create an authenticator which is using the backing database
        // to check credentials.
        final DBAuthenticator authenticator
                = new UnitOfWorkAwareProxyFactory(hibernateBundle)
                .create(DBAuthenticator.class,
                        new Class<?>[]{UserDAO.class, SessionFactory.class},
                        new Object[]{userDAO,
                            hibernateBundle.getSessionFactory()});

        // Register authenticator.
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(authenticator)
                .setAuthorizer(new Authorizer<User>() {
                    @Override
                    public boolean authorize(User principal, String role) {
                        return true;
                    }
                })
                .setRealm("SECURITY REALM")
                .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        //Necessary if @Auth is used to inject a custom Principal
        // type into your resource
        environment.jersey().register(
                new AuthValueFactoryProvider.Binder<>(User.class));

        // Register the Asset Resource.
        environment.jersey().register(new AssetsResource(assetDAO));
        
        // Register the Authentication.
        environment.jersey().register(new Authentication(authenticator));
    }

}
