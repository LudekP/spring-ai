package com.msx.springai.functions;

import com.msx.springai.model.WeatherRequest;
import com.msx.springai.model.WeatherResponse;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

public class WeatherServiceFunction implements Function<WeatherRequest, WeatherResponse> {

    public static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather";

    private final String apiNinjasKey;

    public WeatherServiceFunction(String apiNinjasKey) {
        this.apiNinjasKey = apiNinjasKey;
    }

    @Override
    public WeatherResponse apply(WeatherRequest request) {
        RestClient restClient = RestClient.builder()
                .baseUrl(WEATHER_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-Api-Key", apiNinjasKey);
                    httpHeaders.set("Accept", "application/json");
                    httpHeaders.set("Content-Type", "application/json");
                }).build();

        return restClient.get().uri(uriBuilder -> {
            System.out.println("Building URI for weather request: " + request);

            uriBuilder.queryParam("city", request.location());

            if (request.state() != null && !request.state().isBlank()) {
                uriBuilder.queryParam("state", request.state());
            }
            if (request.country() != null && !request.country().isBlank()) {
                uriBuilder.queryParam("country", request.country());
            }
            return uriBuilder.build();
        }).retrieve().body(WeatherResponse.class);
    }

}
