package com.skcc.abstsm.vo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by dudwo on 2018-02-07.
 */
public class BTS implements ClusterItem {

    private String id;
    private String ssid;
    private double latitude;      //위도
    private double longitude;   //경도
    private int altitude;           //고도
    private LatLng location;
    private String streetAaddress;
    private String secondaryUnit;
    private String enrollDate;
    private String modifyDate;

    public BTS() {
        this.id = "";
        this.ssid = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.altitude = 0;
        this.location = new LatLng(latitude,longitude);
        this.streetAaddress = "";
        this.secondaryUnit = "";
        this.enrollDate = "";
        this.modifyDate = "";
    }

    public BTS(String id, String ssid, double latitude, double longitude, int altitude, String streetAaddress, String secondaryUnit, String enrollDate, String modifyDate) {
        this.id = id;
        this.ssid = ssid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.streetAaddress = streetAaddress;
        this.secondaryUnit = secondaryUnit;
        this.enrollDate = enrollDate;
        this.modifyDate = modifyDate;
        this.location = new LatLng(latitude,longitude);
    }

    public BTS(String id, String ssid, double latitude, double longitude, int altitude, LatLng location, String streetAaddress, String secondaryUnit, String enrollDate, String modifyDate) {
        this.id = id;
        this.ssid = ssid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.location = location;
        this.streetAaddress = streetAaddress;
        this.secondaryUnit = secondaryUnit;
        this.enrollDate = enrollDate;
        this.modifyDate = modifyDate;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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

    @Override
    public String toString() {
        return  "{\"id\":\"" + id + "\"," +
                "\"ssid\":\"" + ssid + "\"," +
                "\"latitude\":" + latitude + "," +
                "\"longitude\":" + longitude + "," +
                "\"altitude\":" + altitude + "," +
                "\"streetAddress\":\"" + streetAaddress + "\"," +
                "\"secondaryUnit\":\"" + secondaryUnit + "\"," +
                "\"enrollDate\":\"" + enrollDate + "\"," +
                "\"modifyDate\":\"" + modifyDate + "\"}";
    }
}