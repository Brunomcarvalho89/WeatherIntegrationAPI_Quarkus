package com.resow.wiapi.infrastructure.acl.hgbrasilweather.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resow.wiapi.application.exceptions.GetDataException;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.IApiWeatherConnection;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.request.CurrentWeatherByCityNameRequest;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.request.CurrentWeatherByWOEIDRequest;
import com.resow.wiapi.infrastructure.acl.hgbrasilweather.response.CurrentWeatherResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@ApplicationScoped
@Named("ApiWeatherConnection")
public class ApiWeatherConnection implements IApiWeatherConnection {

    @ConfigProperty(name = "url.hgbrasil")
    private String url;

    @ConfigProperty(name = "key.hgbrasil", defaultValue = "")
    private String key;

    @Override
    public Optional<CurrentWeatherResponse> currentWeather(CurrentWeatherByWOEIDRequest request) {

        try {

            StringBuilder builder = new StringBuilder(this.url);
            String uri = builder.append("/weather")
                    .append("?key=" + this.key)
                    .append("&woeid=" + request.getWoeid())
                    .toString();

            HttpRequest httpRequest = HttpRequest
                    .newBuilder(new URI(uri))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (Objects.equals(response.statusCode(), Response.Status.OK.getStatusCode())) {

                CurrentWeatherResponse convertValue = new ObjectMapper().readValue(response.body(), CurrentWeatherResponse.class);

                return Optional.of(convertValue);
            }

            throw new GetDataException("Erro ao obter os dados da api STATUS_CODE: " + response.statusCode());
        } catch (Exception ex) {
            throw new GetDataException("Erro ao obter os dados da api: [MESSAGE: " + ex.getMessage() + " ]");
        }
    }

    @Override
    public Optional<CurrentWeatherResponse> currentWeather(CurrentWeatherByCityNameRequest request) {

        try {
            StringBuilder builder = new StringBuilder(this.url);
            String uri = builder.append("/weather")
                    .append("?key=" + this.key)
                    .append("&city_name=" + URLEncoder.encode(request.getCityName(), StandardCharsets.UTF_8.toString()))
                    .toString();

            HttpRequest httpRequest = HttpRequest
                    .newBuilder(new URI(uri))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (Objects.equals(response.statusCode(), Response.Status.OK.getStatusCode())) {

                CurrentWeatherResponse convertValue = new ObjectMapper().readValue(response.body(), CurrentWeatherResponse.class);

                return Optional.of(convertValue);
            }

            throw new GetDataException("Erro ao obter os dados da api STATUS_CODE: " + response.statusCode());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GetDataException("Erro ao obter os dados da api: [MESSAGE: " + ex.getMessage() + " ]");
        }
    }

}
