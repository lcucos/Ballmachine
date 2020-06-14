package com.webproject;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.Map;

import org.apache.log4j.Level;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.sql.Template;

import com.webproject.auth.SimpleAuthenticator;
import com.webproject.auth.SimpleAuthorizer;
import com.webproject.core.User;
import com.webproject.db.UserDAO;
import com.webproject.pi.PiSystem;
import com.webproject.resources.ControllerResource;
import com.webproject.resources.ViewResource;
import com.webproject.resources.WebProjectExceptionMapper;
import com.webproject.views.HtmlPageResource;

public class WebProjectApplication extends Application<WebProjectConfiguration> {
    public static void main(String[] args) throws Exception {
        new WebProjectApplication().run(args) ;
    }

    private final HibernateBundle<WebProjectConfiguration> hibernateBundle =
            new HibernateBundle<WebProjectConfiguration>(User.class 
            											   ) {
                @Override
                public DataSourceFactory getDataSourceFactory(WebProjectConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "ballmachine";
    }

    @Override
    public void initialize(Bootstrap<WebProjectConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        //bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<WebProjectConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(WebProjectConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        //bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<WebProjectConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(WebProjectConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(WebProjectConfiguration configuration, Environment environment) {    	

        PiSystem piSystem = new PiSystem();

        //final Template template = configuration.buildTemplate();
        //environment.healthChecks().register("template", new TemplateHealthCheck(template));
        /*
        final UserDAO userDao = new UserDAO(hibernateBundle.getSessionFactory());
        User uberUser = new User(configuration.getUberUser(),configuration.getUberPassword(),"uber",null, "ADMIN");
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new SimpleAuthenticator(uberUser, userDao))
                .setAuthorizer(new SimpleAuthorizer())
                .setRealm("BallMachine")
                .buildAuthFilter()));
        */
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
        environment.jersey().register(new HtmlPageResource());
        environment.jersey().register(new ControllerResource(piSystem));
        environment.jersey().register(new WebProjectExceptionMapper());
        
		org.apache.log4j.LogManager.getLogger("org.eclipse.jetty").setLevel(Level.INFO);
        
        try {
			piSystem.init(configuration.getConfigFolder(), configuration.getPiVirtual());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
