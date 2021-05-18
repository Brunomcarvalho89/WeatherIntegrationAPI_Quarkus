package com.resow.wiapi.infrastructure.acl.hgbrasilweather.response;

import com.resow.wiapi.domain.CurrentWeather;
import com.resow.wiapi.domain.LocationToCollect;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author brunomcarvalho89@gmail.com
 */
@ApplicationScoped
@Named("CurrentWeatherFromResponseFactory")
public class CurrentWeatherFromResponseFactory implements ICurrentWeatherFromResponseFactory {

    public CurrentWeather currentWeather(CurrentWeatherResponse currentWeatherResponse, LocationToCollect locationToCollect) {

        CurrentWeather currentWeather = new CurrentWeather(
                currentWeatherResponse
                        .getResults()
                        .getTemp(),
                currentWeatherResponse
                        .getResults()
                        .getDate(),
                locationToCollect);

        return currentWeather;
    }

}
