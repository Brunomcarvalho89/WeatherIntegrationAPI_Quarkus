package com.resow.wiapi.infrastructure.acl;

import com.resow.wiapi.infrastructure.acl.hgbrasilweather.IApiWeatherConnection;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.request.CurrentWeatherByCityNameRequest;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.request.CurrentWeatherByWOEIDRequest;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.response.CurrentWeatherResponse;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@QuarkusTest
public class ApiWeatherConnectionByCityNameTest {

    @Inject
    private IApiWeatherConnection apiWeatherConnection;

    @ConfigProperty(name = "url.hgbrasil")
    private String url;

    @ConfigProperty(name = "key.hgbrasil", defaultValue = "")
    private String key;

    private MockServerClient mockServerClient;

    @BeforeEach
    public void init() {
        this.mockServerClient = new ClientAndServer(1080);
    }

    @AfterEach
    public void destroy() {
        this.mockServerClient.stop();
    }

    @Test
    public void testCurrentWeatherByCityNameRequest() {

        Assertions.assertDoesNotThrow(() -> {

            final String cityname = "petropolis,rj";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath("/hgbrasilteste/weather")
                                    .withQueryStringParameters(
                                            new Parameter("key", this.key),
                                            new Parameter("city_name", cityname)
                                    ),
                            Times.exactly(1))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400")
                                    ).withBody("{\"by\":\"city_name\",\"valid_key\":true,\"results\":{\"temp\":24,\"date\":\"12/01/2021\",\"time\":\"21:29\",\"condition_code\":\"26\",\"description\":\"Tempo nublado\",\"currently\":\"noite\",\"cid\":\"\",\"city\":\"Campinas, SP\",\"img_id\":\"26n\",\"humidity\":87,\"wind_speedy\":\"4 km/h\",\"sunrise\":\"5:34 am\",\"sunset\":\"6:59 pm\",\"condition_slug\":\"cloud\",\"city_name\":\"Campinas\",\"forecast\":[{\"date\":\"12/01\",\"weekday\":\"Ter\",\"max\":28,\"min\":21,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"13/01\",\"weekday\":\"Qua\",\"max\":29,\"min\":21,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"14/01\",\"weekday\":\"Qui\",\"max\":30,\"min\":21,\"description\":\"Tempestades isoladas\",\"condition\":\"storm\"},{\"date\":\"15/01\",\"weekday\":\"Sex\",\"max\":28,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"16/01\",\"weekday\":\"Sáb\",\"max\":27,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"17/01\",\"weekday\":\"Dom\",\"max\":28,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"18/01\",\"weekday\":\"Seg\",\"max\":27,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"19/01\",\"weekday\":\"Ter\",\"max\":30,\"min\":20,\"description\":\"Tempo nublado\",\"condition\":\"cloud\"},{\"date\":\"20/01\",\"weekday\":\"Qua\",\"max\":29,\"min\":20,\"description\":\"Parcialmente nublado\",\"condition\":\"cloudly_day\"},{\"date\":\"21/01\",\"weekday\":\"Qui\",\"max\":30,\"min\":22,\"description\":\"Parcialmente nublado\",\"condition\":\"cloudly_day\"}]},\"execution_time\":0.0,\"from_cache\":true}", Charset.forName("UTF-8"))
                                    .withDelay(new Delay(TimeUnit.SECONDS, 1))
                    );

            Request request = new CurrentWeatherByCityNameRequest(cityname);

            CurrentWeatherResponse currentWeather = this.apiWeatherConnection.currentWeather(request).orElse(null);

            Assertions.assertTrue(Objects.nonNull(currentWeather));
            Assertions.assertEquals(24, currentWeather.getResults().getTemp());
        });
    }

    @Test
    public void CurrentWeatherByWOEIDRequest() {

        Assertions.assertDoesNotThrow(() -> {

            final String woeid = "4321";

            this.mockServerClient
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath("/hgbrasilteste/weather")
                                    .withQueryStringParameters(
                                            new Parameter("key", this.key),
                                            new Parameter("woeid", woeid)
                                    ))
                    .respond(
                            HttpResponse.response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "application/json; charset=utf-8"),
                                            new Header("Cache-Control", "public, max-age=86400")
                                    ).withBody("{\"by\":\"woeid\",\"valid_key\":false,\"results\":{\"temp\":25,\"date\":\"13/01/2021\",\"time\":\"10:51\",\"condition_code\":\"28\",\"description\":\"Tempo nublado\",\"currently\":\"dia\",\"cid\":\"\",\"city\":\"São Paulo, SP\",\"img_id\":\"28\",\"humidity\":73,\"wind_speedy\":\"2.06 km/h\",\"sunrise\":\"05:31 am\",\"sunset\":\"06:58 pm\",\"condition_slug\":\"cloudly_day\",\"city_name\":\"São Paulo\",\"forecast\":[{\"date\":\"13/01\",\"weekday\":\"Qua\",\"max\":26,\"min\":21,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"14/01\",\"weekday\":\"Qui\",\"max\":25,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"15/01\",\"weekday\":\"Sex\",\"max\":26,\"min\":18,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"16/01\",\"weekday\":\"Sáb\",\"max\":27,\"min\":19,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"17/01\",\"weekday\":\"Dom\",\"max\":27,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"18/01\",\"weekday\":\"Seg\",\"max\":27,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"19/01\",\"weekday\":\"Ter\",\"max\":28,\"min\":20,\"description\":\"Tempestades isoladas\",\"condition\":\"storm\"},{\"date\":\"20/01\",\"weekday\":\"Qua\",\"max\":28,\"min\":21,\"description\":\"Tempo nublado\",\"condition\":\"cloud\"},{\"date\":\"21/01\",\"weekday\":\"Qui\",\"max\":23,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"},{\"date\":\"22/01\",\"weekday\":\"Sex\",\"max\":23,\"min\":20,\"description\":\"Tempestades\",\"condition\":\"storm\"}]},\"execution_time\":0.0,\"from_cache\":true}", Charset.forName("UTF-8")));

            Request request = new CurrentWeatherByWOEIDRequest(woeid);

            Optional<CurrentWeatherResponse> currentWeather = this.apiWeatherConnection.currentWeather(request);

            Assertions.assertTrue(currentWeather.isPresent());
        });
    }
}
