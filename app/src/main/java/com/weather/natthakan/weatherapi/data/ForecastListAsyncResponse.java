package com.weather.natthakan.weatherapi.data;

import com.weather.natthakan.weatherapi.model.Forecast;

import java.util.ArrayList;

/**
 * Created by natthakan on 2/3/2018 AD.
 */

public interface ForecastListAsyncResponse {
    void processFinished(ArrayList<Forecast> forecastArrayList);
}
