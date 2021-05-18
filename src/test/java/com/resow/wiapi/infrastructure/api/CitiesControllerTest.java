package com.resow.wiapi.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resow.wiapi.application.dto.TemperaturesByCitynameLast30DaysDTO;
import com.resow.wiapi.domain.repository.ICurrentWeatherRepository;
import com.resow.wiapi.domain.repository.ILocationToCollectRepository;
import com.resow.wiapi.domain.repository.ILocationToCollectWoeidRepository;
import com.resow.wiapi.infrastructure.acl.viacep.gateway.ViaCepZipcodeGateway;
import com.resow.wiapi.resources.RepositoryResources;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 *
 * @author bruno
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CitiesControllerTest {

    private MockServerClient mockServerClient;

    @ConfigProperty(name = "url.viacep")
    private String url;

    @Inject
    private ViaCepZipcodeGateway viaCepZipcodeGateway;

    @Inject
    private ILocationToCollectRepository locationToCollectRepository;

    @Inject
    private ICurrentWeatherRepository currentWeatherRepository;

    @Inject
    private ILocationToCollectWoeidRepository locationToCollectWoeidRepository;

    @BeforeEach
    public void init() {
        this.mockServerClient = new ClientAndServer(1080);
    }

    @AfterEach
    public void destroy() {
        this.mockServerClient.stop();
    }

    @Test
    @Order(1)
    public void testRegisterByCityname() {

        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/cityname/Petrópolis, RJ")
                    .then()
                    .statusCode(200);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/cityname/Petrópolis, RJ")
                    .then()
                    .statusCode(500);
        });
    }

    @Test
    @Order(2)
    public void testRegisterByWoeid() {
        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/woeid/1234?cityname=Cityname, RJ")
                    .then()
                    .statusCode(200);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/woeid/1234?cityname=Cityname, RJ")
                    .then()
                    .statusCode(500);
        });
    }

    @Test
    @Order(3)
    public void testRegisterByZipCode() {

        Assertions.assertDoesNotThrow(() -> {

            final String cep = "25555003";

            URI URI = new URI(String.format("/viacep/%s", cep));

            String returnBody = "{\"cep\": \"01001-000\",\"logradouro\": \"Praça da Sé\",\"complemento\": \"lado ímpar\",\"bairro\": \"Sé\",\"localidade\": \"São Paulo\",\"uf\": \"SP\",\"ibge\": \"3550308\",\"gia\": \"1004\",\"ddd\": \"11\",\"siafi\": \"7107\"}";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath(URI.getPath()))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400"))
                                    .withBody(returnBody)
                                    .withDelay(new Delay(TimeUnit.SECONDS, 1))
                    );

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/cep/" + cep)
                    .then()
                    .statusCode(200);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().post("/cities/cep/" + cep)
                    .then()
                    .statusCode(500);
        });
    }

    @Test
    @Order(4)
    public void testFindAll() {

        Assertions.assertDoesNotThrow(() -> {

            RepositoryResources repositoryResources = new RepositoryResources();
            repositoryResources.fillDatabase(this.locationToCollectRepository, this.currentWeatherRepository, this.locationToCollectWoeidRepository);

            Response responseReturn = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/")
                    .then()
                    .statusCode(200)
                    .extract().response();

            List<HashMap> readValue = new ObjectMapper().readValue(responseReturn.asString(), List.class);
            Assertions.assertTrue(readValue.size() > 0);
        });
    }

    @Test
    @Order(5)
    public void testFindAllTemperatures() {

        Assertions.assertDoesNotThrow(() -> {

            Response responseReturn = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/temperatures?page=0&max-results=2")
                    .then()
                    .statusCode(200)
                    .extract().response();

            List<HashMap> readValue = new ObjectMapper().readValue(responseReturn.asString(), List.class);
            Assertions.assertTrue(readValue.size() == 2);
        });
    }

    @Test
    @Order(6)
    public void testFindAllTemperaturesByCityname() {

        Assertions.assertDoesNotThrow(() -> {

            Response responseReturn = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/cityname/cityname1/temperatures")
                    .then()
                    .statusCode(200)
                    .extract().response();

            TemperaturesByCitynameLast30DaysDTO temperaturesByCityname = new ObjectMapper().readValue(responseReturn.asString(), TemperaturesByCitynameLast30DaysDTO.class);
            Assertions.assertTrue(temperaturesByCityname.getTemperatures().size() == 2);
        });
    }

    @Test
    @Order(7)
    public void testFindAllTemperaturesByWoeid() {

        Assertions.assertDoesNotThrow(() -> {

            Response responseReturn = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/woeid/5461/temperatures")
                    .then()
                    .statusCode(200)
                    .extract().response();

            TemperaturesByCitynameLast30DaysDTO temperaturesByCityname = new ObjectMapper().readValue(responseReturn.asString(), TemperaturesByCitynameLast30DaysDTO.class);
            Assertions.assertEquals("cityname4-woeid", temperaturesByCityname.getCity());
            Assertions.assertTrue(temperaturesByCityname.getTemperatures().size() == 2);
        });
    }

    @Test
    @Order(8)
    public void testDeregisterCityByCityname() {

        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().delete("/cities/cityname/Petrópolis, RJ")
                    .then()
                    .statusCode(200);
        });
    }

    @Test
    @Order(9)
    public void testDeleteTemperaturesByCityname() {

        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().delete("/cities/cityname/cityname1/temperatures")
                    .then()
                    .statusCode(200);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/cityname/cityname1/temperatures")
                    .then()
                    .statusCode(204);
        });
    }

    @Test
    @Order(10)
    public void testDeleteTemperaturesByWoeid() {

        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().delete("/cities/woeid/5461/temperatures")
                    .then()
                    .statusCode(200);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().get("/cities/woeid/5461/temperatures")
                    .then()
                    .statusCode(204);
        });
    }

    @Test
    @Order(12)
    public void testDeregisterCityByWoeid() {

        Assertions.assertDoesNotThrow(() -> {

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when().delete("/cities/woeid/5461")
                    .then()
                    .statusCode(200);
        });
    }
}
