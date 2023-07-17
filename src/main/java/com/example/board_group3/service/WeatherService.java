package com.example.board_group3.service;

import com.example.board_group3.dto.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private static final String API_KEY = "248272caccbe78177c150490bf086679";

    public WeatherResponse getWeather(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, WeatherResponse.class, city, API_KEY);
    }
}