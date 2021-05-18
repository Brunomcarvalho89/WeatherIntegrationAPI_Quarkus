package com.resow.wiapi.infrastructure.service.impl;

import com.resow.wiapi.infrastructure.service.IWeatherDataCollector;
import com.resow.wiapi.application.IRegisterCurrentWeatherDataService;
import io.quarkus.scheduler.Scheduled;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@ApplicationScoped
public class WeatherDataCollectorScheduled implements IWeatherDataCollector {

    private final IRegisterCurrentWeatherDataService currentWeatherDataService;

    @Inject
    public WeatherDataCollectorScheduled(IRegisterCurrentWeatherDataService currentWeatherDataService) {
        this.currentWeatherDataService = currentWeatherDataService;
    }

    @Override
    @Scheduled(cron = "0/10 * * * * ?")
    public void collect() {
        System.out.println("Collecting every 10 seconds");
        this.currentWeatherDataService.collectAll();
    }
}
