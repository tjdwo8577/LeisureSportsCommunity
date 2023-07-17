package com.example.board_group3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class WeatherResponse {

    @JsonProperty("coord")
    private Coord coord;

    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("clouds")
    private Clouds clouds;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("name")
    private String name;

    @Getter
    @Setter
    @ToString
    public static class Coord {
        @JsonProperty("lon")
        private double lon;

        @JsonProperty("lat")
        private double lat;
    }

    @Getter
    @Setter
    @ToString
    public static class Weather {
        @JsonProperty("id")
        private int id;

        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;

        @JsonProperty("icon")
        private String icon;
    }

    @Getter
    @Setter
    @ToString
    public static class Main {
        @JsonProperty("temp")
        private double temp;

        @JsonProperty("feels_like")
        private double feelsLike;

        @JsonProperty("temp_min")
        private double tempMin;

        @JsonProperty("temp_max")
        private double tempMax;

        @JsonProperty("pressure")
        private int pressure;

        @JsonProperty("humidity")
        private int humidity;
    }

    @Getter
    @Setter
    @ToString
    public static class Wind {
        @JsonProperty("speed")
        private double speed;

        @JsonProperty("deg")
        private int deg;
    }

    @Getter
    @Setter
    @ToString
    public static class Clouds {
        @JsonProperty("all")
        private int all;
    }

    @Getter
    @Setter
    @ToString
    public static class Sys {
        @JsonProperty("country")
        private String country;

        @JsonProperty("sunrise")
        private long sunrise;

        @JsonProperty("sunset")
        private long sunset;
    }
}