package org;

import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.api.SQLite;
import org.resources.ApiResouce;

public class DropwizardApplication extends Application<DropwizardConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropwizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dropwizard";
    }

    @Override
    public void initialize(final Bootstrap<DropwizardConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(final DropwizardConfiguration configuration,
                    final Environment environment) {
        SQLite databaseInstance = new SQLite();
        final ApiResouce restApi = new ApiResouce(databaseInstance, configuration.getStagpdfa() );
        environment.jersey().register(restApi);
    }

}
