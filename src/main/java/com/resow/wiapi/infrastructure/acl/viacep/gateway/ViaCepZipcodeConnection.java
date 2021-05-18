package com.resow.wiapi.infrastructure.acl.viacep.gateway;

import com.resow.wiapi.application.exceptions.ZipCodeException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@ApplicationScoped
public class ViaCepZipcodeConnection implements ZipcodeConnection {

    private static final Logger LOG = Logger.getLogger(ViaCepZipcodeConnection.class.getName());

    @ConfigProperty(name = "url.viacep")
    private String url;

    @Override
    public Optional<String> getAddress(String cep) throws ZipCodeException {
        try {
            URI uri = new URI(String.format(this.url, cep));

            HttpRequest httpRequest = HttpRequest
                    .newBuilder(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (Objects.equals(response.statusCode(), Response.Status.BAD_REQUEST.getStatusCode())) {
                throw new ZipCodeException("Invalid format for the provided zip code.");
            }

            String body = response.body();
            if (body.contains("erro")) {
                throw new ZipCodeException("No data for the zip code.");
            }

            return Optional.of(body);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        return Optional.empty();
    }

}
