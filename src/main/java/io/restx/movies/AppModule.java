package io.restx.movies;

import com.google.common.base.Predicates;
import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Provides;

import com.google.common.base.Charsets;
import restx.security.*;
import restx.factory.Module;
import javax.inject.Named;

import static java.util.Arrays.asList;

@Module
public class AppModule {
    @Provides
    public SignatureKey signatureKey() {
         return new SignatureKey("4909028380269522207 movie-buddy-restx movie-buddy-restx 0569578b-6972-429c-bc9f-422134cb35df".getBytes(Charsets.UTF_8));
    }

    @Provides
    @Named("restx.admin.password")
    public String restxAdminPassword() {
        return "admin";
    }

    @Provides
    public ConfigSupplier appConfigSupplier(ConfigLoader configLoader) {
        // Load settings.properties in io.restx.movies package as a set of config entries
        return configLoader.fromResource("io.restx.movies/settings");
    }

    @Provides
    public CredentialsStrategy credentialsStrategy() {
        return new BCryptCredentialsStrategy();
    }

    @Provides
    public CORSAuthorizer allowCrossOrigins() {
        return new StdCORSAuthorizer(
                Predicates.<CharSequence>alwaysTrue(),
                Predicates.<CharSequence>alwaysTrue(), asList("GET", "POST"));
    }
}
