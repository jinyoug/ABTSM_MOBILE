package com.skcc.abstsm.vo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by dudwo on 2018-02-07.
 */
public class BTS implements ClusterItem {

    private String ssid;
    private double latitude;      //위도
    private double longitude;   //경도
    private int altitude;           //고도
    private LatLng location;
    private String streetAaddress;
    private String secondaryUnit;
    private String enrollDate;
    private String modifyDate;

    public BTS(String ssid, double latitude, double longitude, int altitude, String streetAaddress, String secondaryUnit, String enrollDate, String modifyDate) {
        this.ssid = ssid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.location = new LatLng(latitude, longitude);
        this.streetAaddress = streetAaddress;
        this.secondaryUnit = secondaryUnit;
        this.enrollDate = enrollDate;
        this.modifyDate = modifyDate;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public String getStreetAaddress() {
        return streetAaddress;
    }

    public void setStreetAaddress(String streetAaddress) {
        this.streetAaddress = streetAaddress;
    }

    public String getSecondaryUnit() {
        return secondaryUnit;
    }

    public void setSecondaryUnit(String secondaryUnit) {
        this.secondaryUnit = secondaryUnit;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}