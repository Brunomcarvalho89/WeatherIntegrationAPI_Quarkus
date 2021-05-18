package com.resow.wiapi.infrastructure;

import com.resow.wiapi.application.ICurrentWeatherService;
import com.resow.wiapi.application.impl.RegisterCurrentWeatherDataService;
import com.resow.wiapi.domain.repository.ICurrentWeatherRepository;
import com.resow.wiapi.application.IRegisterCurrentWeatherDataService;
import com.resow.wiapi.application.impl.TemperatureMonitoringService;
import com.resow.wiapi.application.dto.assembler.ITemperaturesByCityLast30DaysAssemblerDTO;
import com.resow.wiapi.application.dto.assembler.impl.LocationToCollectByCitynameAssemblerDTO;
import com.resow.wiapi.application.dto.assembler.impl.TemperaturesByCityLast30DaysAssemblerDTO;
import com.resow.wiapi.domain.repository.ILocationToCollectRepository;
import com.resow.wiapi.application.dto.assembler.ILocationToCollectAssemblerDTO;
import com.resow.wiapi.domain.repository.ILocationToCollectWoeidRepository;
import com.resow.wiapi.application.ITemperatureMonitoringService;
import com.resow.wiapi.application.ZipcodeService;
import com.resow.wiapi.application.dto.assembler.IAllLatestTemperaturesAssemblerDTO;
import com.resow.wiapi.application.dto.assembler.impl.AllLatestTemperaturesAssemblerDTO;
import com.resow.wiapi.infrastructure.acl.viacep.gateway.ViaCepZipcodeConnection;
import com.resow.wiapi.infrastructure.acl.viacep.gateway.ZipcodeConnection;
import io.quarkus.arc.DefaultBean;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@Dependent
public class BeansDefinition {

    @Inject
    private ICurrentWeatherService currentWeatherService;

    @Inject
    private ICurrentWeatherRepository currentWeatherRepository;

    @Inject
    private ILocationToCollectRepository locationToCollectRepository;

    @Inject
    private ILocationToCollectWoeidRepository locationToCollectWoeidRepository;

    @Inject
    private ZipcodeService zipcodeService;

    @Inject
    private ViaCepZipcodeConnection viaCepZipcodeConnection;

    @Produces
    @DefaultBean
    IRegisterCurrentWeatherDataService registerCurrentWeatherDataService() {
        return new RegisterCurrentWeatherDataService(
                this.currentWeatherService,
                this.currentWeatherRepository,
                this.locationToCollectRepository);
    }

    @Produces
    @DefaultBean
    ITemperatureMonitoringService locationToCollectService() {
        return new TemperatureMonitoringService(
                this.locationToCollectRepository,
                this.locationToCollectWoeidRepository,
                this.currentWeatherRepository,
                this.locationToCollectByCitynameAssemblerDTO(),
                this.temperaturesByCityLast30DaysAssemblerDTO(),
                this.allLatestTemperaturesAssemblerDTO(),
                this.zipcodeService
        );
    }

    @Produces
    @DefaultBean
    ILocationToCollectAssemblerDTO locationToCollectByCitynameAssemblerDTO() {
        return new LocationToCollectByCitynameAssemblerDTO();
    }

    @Produces
    @DefaultBean
    ITemperaturesByCityLast30DaysAssemblerDTO temperaturesByCityLast30DaysAssemblerDTO() {
        return new TemperaturesByCityLast30DaysAssemblerDTO();
    }

    @Produces
    @DefaultBean
    IAllLatestTemperaturesAssemblerDTO allLatestTemperaturesAssemblerDTO() {
        return new AllLatestTemperaturesAssemblerDTO();
    }

    @Produces
    @DefaultBean
    ZipcodeConnection zipcodeConnection() {
        return viaCepZipcodeConnection;
    }
}
