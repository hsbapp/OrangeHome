package com.gmoonxs.www.orangehome;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by 威 on 2016/6/26.
 */
public class Weather {
    String cityName;
    String updateTime;
    String aqi;
    String quality;
    String wendu;
    String shidu;
    String pm2_5;
    String pm10;
    Calendar upadteTime;

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(String pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public Calendar getUpadteTime() {
        return upadteTime;
    }

    public void setUpadteTime(Calendar upadteTime) {
        this.upadteTime = upadteTime;
    }
}
