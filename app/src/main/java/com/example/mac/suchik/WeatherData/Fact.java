package com.example.mac.suchik.WeatherData;

import com.caverock.androidsvg.SVG;

public class Fact {
    private float temp;
    private float feels_like;
    private String icon;
    private String condition;
    private float wind_speed;
    private float wind_gust;
    private String wind_dir;
    private float pressure_mm;
    private float pressure_pa;
    private float humidity;
    private float uv_index;
    private float soil_temp;
    private float soil_moisture;
    private String daytime;
    private boolean polar;
    private String season;
    private float obs_time;
    private String source;
    private float prec_type;
    private float prec_strength;
    private float cloudness;
    private SVG imageIcon;

    public boolean isPolar() {
        return polar;
    }

    public SVG getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(SVG imageIcon) {
        this.imageIcon = imageIcon;
    }

    // Getter Methods

    public float getTemp() {
        return temp;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public String getIcon() {
        return icon;
    }

    public String getCondition() {
        return condition;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public float getWind_gust() {
        return wind_gust;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public float getPressure_mm() {
        return pressure_mm;
    }

    public float getPressure_pa() {
        return pressure_pa;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getUv_index() {
        return uv_index;
    }

    public float getSoil_temp() {
        return soil_temp;
    }

    public float getSoil_moisture() {
        return soil_moisture;
    }

    public String getDaytime() {
        return daytime;
    }

    public boolean getPolar() {
        return polar;
    }

    public String getSeason() {
        return season;
    }

    public float getObs_time() {
        return obs_time;
    }

    public String getSource() {
        return source;
    }

    public float getPrec_type() {
        return prec_type;
    }

    public float getPrec_strength() {
        return prec_strength;
    }

    public float getCloudness() {
        return cloudness;
    }

    // Setter Methods

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setFeels_like(float feels_like) {
        this.feels_like = feels_like;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public void setWind_gust(float wind_gust) {
        this.wind_gust = wind_gust;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public void setPressure_mm(float pressure_mm) {
        this.pressure_mm = pressure_mm;
    }

    public void setPressure_pa(float pressure_pa) {
        this.pressure_pa = pressure_pa;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setUv_index(float uv_index) {
        this.uv_index = uv_index;
    }

    public void setSoil_temp(float soil_temp) {
        this.soil_temp = soil_temp;
    }

    public void setSoil_moisture(float soil_moisture) {
        this.soil_moisture = soil_moisture;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    public void setPolar(boolean polar) {
        this.polar = polar;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setObs_time(float obs_time) {
        this.obs_time = obs_time;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setPrec_type(float prec_type) {
        this.prec_type = prec_type;
    }

    public void setPrec_strength(float prec_strength) {
        this.prec_strength = prec_strength;
    }

    public void setCloudness(float cloudness) {
        this.cloudness = cloudness;
    }
}
