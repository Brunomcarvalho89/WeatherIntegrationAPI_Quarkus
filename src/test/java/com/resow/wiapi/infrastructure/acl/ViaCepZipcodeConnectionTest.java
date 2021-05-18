package com.resow.wiapi.infrastructure.acl;

import com.resow.wiapi.infrastructure.acl.viacep.gateway.ZipcodeConnection;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.charset.Charset;
import java.util.Objects;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@QuarkusTest
public class ViaCepZipcodeConnectionTest {

    @ConfigProperty(name = "url.viacep")
    private String url;

    private ClientAndServer mockServerClient;

    @Inject
    private ZipcodeConnection zipcodeConnection;

    @BeforeEach
    public void init() {
        this.mockServerClient = new ClientAndServer(1080);
    }

    @AfterEach
    public void destroy() {
        this.mockServerClient.stop();
    }

    @Test
    public void testFindByCityname() {

        Assertions.assertDoesNotThrow(() -> {

            final String cep = "25564333";

            String viaCepURL = String.format("/viacep/%s", cep);

            String returnBody = "{\"cep\": \"01001-000\",\"logradouro\": \"Praça da Sé\",\"complemento\": \"lado ímpar\",\"bairro\": \"Sé\",\"localidade\": \"São Paulo\",\"uf\": \"SP\",\"ibge\": \"3550308\",\"gia\": \"1004\",\"ddd\": \"11\",\"siafi\": \"7107\"}";

            this.mockServerClient
                    .when(
                            HttpRequest.request(viaCepURL)
                                    .withMethod("GET"))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withBody(returnBody, Charset.forName("UTF-8")));

            String address = this.zipcodeConnection.getAddress(cep).orElse(null);
            Assertions.assertTrue(Objects.nonNull(address));
            Assertions.assertEquals(returnBody, address);
        });
    }
}
