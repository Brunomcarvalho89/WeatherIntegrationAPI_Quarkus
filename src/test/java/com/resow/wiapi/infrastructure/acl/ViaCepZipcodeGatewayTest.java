package com.resow.wiapi.infrastructure.acl;

import com.resow.wiapi.infrastructure.acl.viacep.gateway.ViaCepZipcodeGateway;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@QuarkusTest
public class ViaCepZipcodeGatewayTest {

    @ConfigProperty(name = "url.viacep")
    private String url;

    private ClientAndServer mockServerClient;

    @Inject
    private ViaCepZipcodeGateway viaCepZipcodeGateway;

    @BeforeEach
    public void init() {
        this.mockServerClient = new ClientAndServer(1080);
    }

    @AfterEach
    public void destroy() {
        this.mockServerClient.stop();
    }

    @Test
    public void testFindByCEP() {

        Assertions.assertDoesNotThrow(() -> {

            final String cep = "25564333";

            URI URI = new URI(String.format("/viacep/%s", cep));

            String returnBody = "{\"cep\": \"01001-000\",\"logradouro\": \"Praça da Sé\",\"complemento\": \"lado ímpar\",\"bairro\": \"Sé\",\"localidade\": \"São Paulo\",\"uf\": \"SP\",\"ibge\": \"3550308\",\"gia\": \"1004\",\"ddd\": \"11\",\"siafi\": \"7107\"}";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath(URI.toString()))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400")
                                    ).withBody(returnBody, Charset.forName("UTF-8"))
                    );

            String address = this.viaCepZipcodeGateway.addressByZipCode(cep).orElse(null);
            Assertions.assertTrue(Objects.nonNull(address));
            Assertions.assertEquals("São Paulo, SP", address);
        });

    }

    @Test
    public void testFindByCEPBadRequest() {

        Assertions.assertDoesNotThrow(() -> {

            final String cep = "25564333";

            URI URI = new URI(String.format("/viacep/%s", cep));

            String returnBody = "{\"cep\": \"01001-000\",\"logradouro\": \"Praça da Sé\",\"complemento\": \"lado ímpar\",\"bairro\": \"Sé\",\"localidade\": \"São Paulo\",\"uf\": \"SP\",\"ibge\": \"3550308\",\"gia\": \"1004\",\"ddd\": \"11\",\"siafi\": \"7107\"}";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath(URI.toString()),
                            Times.exactly(1))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(400)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400")
                                    ).withBody(returnBody, Charset.forName("UTF-8"))
                                    .withDelay(new Delay(TimeUnit.SECONDS, 1))
                    );

            String address = this.viaCepZipcodeGateway.addressByZipCode(cep).orElse(null);
            Assertions.assertTrue(Objects.isNull(address));
        });
    }

    @Test
    public void testFindByCEPStringErro() {

        Assertions.assertDoesNotThrow(() -> {

            final String cep = "25564333";

            URI URI = new URI(String.format("/viacep/%s", cep));

            String returnBody = "erro";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath(URI.toString()))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400")
                                    ).withBody(returnBody, Charset.forName("UTF-8"))
                    );

            String address = this.viaCepZipcodeGateway.addressByZipCode(cep).orElse(null);
            Assertions.assertTrue(Objects.isNull(address));
        });
    }
}
