package com.resow.wiapi.infrastructure.api.controller;

import java.util.stream.Collectors;
import com.resow.wiapi.application.dto.TemperaturesByCitynameLast30DaysDTO;
import com.resow.wiapi.domain.CurrentWeather;
import java.util.List;
import java.util.Optional;
import com.resow.wiapi.application.exceptions.CitynameMustBeInformedException;
import java.util.Objects;
import com.resow.wiapi.application.ITemperatureMonitoringService;
import com.resow.wiapi.application.dto.AllLatestTemperaturesDTO;
import com.resow.wiapi.application.dto.LocationToCollectByCitynameDTO;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author brunomcarvalho89@gmail.com
 *
 * http://localhost:8080/swagger-ui/index.html#/
 */
@Path("cities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CitiesController {

    @Inject
    private ITemperatureMonitoringService temperatureService;

    @GET
    @Path("")
    public Response findAll() {

        List<Response> collect = this.temperatureService
                .findAllCitiesForCollect()
                .stream()
                .map(item -> {

                    return Response.ok(item)
                            .links(
                                    Link.fromMethod(CitiesController.class, "findAllTemperaturesByCityname")
                                            .rel("temperatures")
                                            .build(item.getCityname()),
                                    Link.fromMethod(CitiesController.class, "deregisterCity")
                                            .rel("remove-city")
                                            .build(item.getCityname()),
                                    Link.fromMethod(CitiesController.class, "deleteTemperaturesByCityname")
                                            .rel("remove-temperatures-by-city")
                                            .build(item.getCityname())
                            ).build();
                })
                .collect(Collectors.toList());

        Link build = Link.fromMethod(CitiesController.class, "findAll").rel("self").build();

        return Response.ok(collect)
                .links(build)
                .build();
    }

    @GET
    @Path("temperatures")
    public Response findAllTemperatures(@QueryParam("page") int page, @QueryParam("max-results") int maxResults) {

        Optional<AllLatestTemperaturesDTO> findAllTheLatestTemperatures = this.temperatureService.findAllTheLatestTemperatures(page, maxResults);

        if (findAllTheLatestTemperatures.isEmpty()) {
            return Response
                    .noContent()
                    .build();
        }

        AllLatestTemperaturesDTO allLatestTemperaturesDTO = findAllTheLatestTemperatures.get();

        List<Response> collect = allLatestTemperaturesDTO
                .getTemperatures()
                .stream()
                .map(item -> {

                    return Response.ok(item)
                            .links(
                                    Link.fromMethod(CitiesController.class, "findAllTemperaturesByCityname")
                                            .rel("temperatures")
                                            .build(item.getCity()),
                                    Link.fromMethod(CitiesController.class, "deregisterCity")
                                            .rel("remove-city")
                                            .build(item.getCity()),
                                    Link.fromMethod(CitiesController.class, "deleteTemperaturesByCityname")
                                            .rel("remove-temperatures-by-city")
                                            .build(item.getCity())
                            ).build();
                })
                .collect(Collectors.toList());

        Link build = Link.fromMethod(CitiesController.class, "findAllTemperatures").rel("self").build(page + 1, maxResults);

        return Response.ok(collect)
                .links(build)
                .build();
    }

    @GET
    @Path("cityname/{city_name}/temperatures")
    public Response findAllTemperaturesByCityname(@PathParam("city_name") String cityname) {

        Optional<TemperaturesByCitynameLast30DaysDTO> assemble = this.temperatureService.temperaturesByCityname(CurrentWeather.Time.LAST_30_HOURS, cityname);

        if (assemble.isEmpty()) {
            return Response
                    .noContent()
                    .build();
        }

        return Response
                .ok(assemble.get())
                .build();
    }

    @GET
    @Path("woeid/{woeid}/temperatures")
    public Response findAllTemperaturesByWoeid(@PathParam("woeid") String woeid) {

        Optional<TemperaturesByCitynameLast30DaysDTO> assemble = this.temperatureService.temperaturesByWoeid(CurrentWeather.Time.LAST_30_HOURS, woeid);

        if (assemble.isEmpty()) {
            return Response
                    .noContent()
                    .build();
        }

        return Response
                .ok(assemble.get())
                .build();
    }

    @POST
    @Path("cityname/{city_name}")
    public Response registerByCityname(@PathParam("city_name") String cityname) {

        if (cityname.isEmpty() || Objects.isNull(cityname)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        Optional<LocationToCollectByCitynameDTO> registeredCity = this.temperatureService.registerByCityname(cityname);
        if (registeredCity.isPresent()) {

            LocationToCollectByCitynameDTO item = registeredCity.get();

            return Response.ok(item)
                    .links(
                            Link.fromMethod(CitiesController.class, "findAllTemperaturesByCityname")
                                    .rel("temperatures")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deregisterCity")
                                    .rel("remove-city")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deleteTemperaturesByCityname")
                                    .rel("remove-temperatures-by-city")
                                    .build(item.getCityname())
                    ).build();

        }

        return Response.serverError().build();
    }

    @POST
    @Path("woeid/{woeid}")
    public Response registerByWoeid(@PathParam("woeid") String woeid, @QueryParam("cityname") String cityname) {

        if (woeid.isEmpty() || Objects.isNull(woeid)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        Optional<LocationToCollectByCitynameDTO> registeredCity = this.temperatureService.registerByWoeid(cityname, woeid);
        if (registeredCity.isPresent()) {

            LocationToCollectByCitynameDTO item = registeredCity.get();

            return Response.ok(item)
                    .links(
                            Link.fromMethod(CitiesController.class, "findAllTemperaturesByCityname")
                                    .rel("temperatures")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deregisterCity")
                                    .rel("remove-city")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deleteTemperaturesByCityname")
                                    .rel("remove-temperatures-by-city")
                                    .build(item.getCityname()))
                    .build();
        }

        return Response.serverError().build();
    }

    @POST
    @Path("cep/{cep}")
    public Response registerByZipCode(@PathParam("cep") String cep) {

        Optional<LocationToCollectByCitynameDTO> registeredCity = this.temperatureService.registerByZipCode(cep);
        if (registeredCity.isPresent()) {

            LocationToCollectByCitynameDTO item = registeredCity.get();

            return Response.ok(item)
                    .links(
                            Link.fromMethod(CitiesController.class, "findAllTemperaturesByCityname")
                                    .rel("temperatures")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deregisterCity")
                                    .rel("remove-city")
                                    .build(item.getCityname()),
                            Link.fromMethod(CitiesController.class, "deleteTemperaturesByCityname")
                                    .rel("remove-temperatures-by-city")
                                    .build(item.getCityname()))
                    .build();
        }

        return Response.serverError().build();
    }

    @DELETE
    @Path("cityname/{city_name}")
    public Response deregisterCity(@PathParam("city_name") String cityname) {

        if (cityname.isEmpty() || Objects.isNull(cityname)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        this.temperatureService.deregisterByCityname(cityname);

        if (this.temperatureService
                .findAllCitiesForCollect()
                .stream()
                .filter(item -> item.getCityname().equalsIgnoreCase(cityname))
                .findFirst()
                .isPresent()) {

            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("woeid/{woeid}")
    public Response deregisterWoeid(@PathParam("woeid") String woeid) {

        if (woeid.isEmpty() || Objects.isNull(woeid)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        this.temperatureService.deregisterByWoeid(woeid);

        if (this.temperatureService.locationToCollectExistsByWoeid(woeid)) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("cityname/{city_name}/temperatures")
    public Response deleteTemperaturesByCityname(@PathParam("city_name") String cityname) {

        if (cityname.isEmpty() || Objects.isNull(cityname)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        this.temperatureService.clearTemperaturesByCityname(cityname);

        return Response.ok().build();
    }

    @DELETE
    @Path("woeid/{woeid}/temperatures")
    public Response deleteTemperaturesByWoeid(@PathParam("woeid") String woeid) {

        if (woeid.isEmpty() || Objects.isNull(woeid)) {
            throw new CitynameMustBeInformedException("Cityname must be informed.");
        }

        this.temperatureService.clearTemperaturesByWoeid(woeid);

        return Response.ok().build();
    }
}
