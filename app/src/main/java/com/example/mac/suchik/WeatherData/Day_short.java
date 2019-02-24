package com.example.mac.suchik.WeatherData;

import com.caverock.androidsvg.SVG;

public class Day_short {
        private String _source;
        private float temp;
        private float temp_min;
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
        private float prec_mm;
        private float prec_prob;
        private float prec_type;
        private float prec_strength;
        private float cloudness;
        private SVG imageIcon;

        // Getter Methods

        public SVG getImageIcon() {
            return imageIcon;
        }

        public String get_source() {
            return _source;
        }

        public float getTemp() {
            return temp;
        }

        public float getTemp_min() {
            return temp_min;
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

        public float getPrec_mm() {
            return prec_mm;
        }

        public float getPrec_prob() {
            return prec_prob;
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

        public void setImageIcon(SVG imageIcon) {
        this.imageIcon = imageIcon;
    }

        public void set_source(String _source) {
            this._source = _source;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public void setTemp_min(float temp_min) {
            this.temp_min = temp_min;
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

        public void setPrec_mm(float prec_mm) {
            this.prec_mm = prec_mm;
        }

        public void setPrec_prob(float prec_prob) {
            this.prec_prob = prec_prob;
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
