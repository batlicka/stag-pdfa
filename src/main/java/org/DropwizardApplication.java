package org;

import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
        final ApiResouce restApi = new ApiResouce(configuration.getUrlToVeraPDFrest(),configuration.getPathToRuleViolationExceptionFile());
        environment.jersey().register(restApi);
    }

}
