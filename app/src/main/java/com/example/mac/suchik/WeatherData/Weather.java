
package com.example.mac.suchik.WeatherData;


import android.graphics.Bitmap;

public class Weather {

    private Integer id;
    private String main;
    private String description;
    private String icon;
    private Bitmap imageIcon;
    private String strImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Bitmap getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(Bitmap imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getStrImage() {
        return strImage;
    }

    public void setStrImage(String strImage) {
        this.strImage = strImage;
    }
}
