package com.skcc.abstsm.vo;

/**
 * Created by Jinyoung on 2018-02-07.
 */

public class ImageInfo {
    private String OCRCode;
    private String DataTime;
    private double GPSLatitude;
    private String GPSLatitudeRef;
    private double GPSLongitude;
    private String GPSLongitudeRef;
    private int ImageLength;
    private int ImageWidth;
    private String Maker;
    private String Model;

    public ImageInfo(String OCRCode, String dataTime, double GPSLatitude, String GPSLatitudeRef, double GPSLongitude, String GPSLongitudeRef, int imageLength, int imageWidth, String maker, String model) {
        this.OCRCode = OCRCode;
        DataTime = dataTime;
        this.GPSLatitude = GPSLatitude;
        this.GPSLatitudeRef = GPSLatitudeRef;
        this.GPSLongitude = GPSLongitude;
        this.GPSLongitudeRef = GPSLongitudeRef;
        ImageLength = imageLength;
        ImageWidth = imageWidth;
        Maker = maker;
        Model = model;
    }

    public String getOCRCode() {
        return OCRCode;
    }

    public void setOCRCode(String OCRCode) {
        this.OCRCode = OCRCode;
    }

    public String getDataTime() {
        return DataTime;
    }

    public void setDataTime(String dataTime) {
        DataTime = dataTime;
    }

    public double getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(double GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public String getGPSLatitudeRef() {
        return GPSLatitudeRef;
    }

    public void setGPSLatitudeRef(String GPSLatitudeRef) {
        this.GPSLatitudeRef = GPSLatitudeRef;
    }

    public double getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(double GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public String getGPSLongitudeRef() {
        return GPSLongitudeRef;
    }

    public void setGPSLongitudeRef(String GPSLongitudeRef) {
        this.GPSLongitudeRef = GPSLongitudeRef;
    }

    public int getImageLength() {
        return ImageLength;
    }

    public void setImageLength(int imageLength) {
        ImageLength = imageLength;
    }

    public int getImageWidth() {
        return ImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        ImageWidth = imageWidth;
    }

    public String getMaker() {
        return Maker;
    }

    public void setMaker(String maker) {
        Maker = maker;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }
}
