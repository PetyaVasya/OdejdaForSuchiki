package com.example.mac.suchik;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CitySave {
    HashMap<String, String[]> cityPos;
    List<String> cities;

    public HashMap<String, String[]> getcityPos() {
        return cityPos;
    }

    public void setcityPos(HashMap<String, String[]> cityPos) {
        this.cityPos = cityPos;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }
}
