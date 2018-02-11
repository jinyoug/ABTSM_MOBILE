package com.skcc.abstsm.vo;

/**
 * Created by dlwog on 2018-02-06.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import com.skcc.abtsm.PhotoActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ExifStore {

    public String dateTime;
    Context mContext;
    private double lat;
    private double lon;

    public ExifStore(Context context){
        mContext = context;
    }



    public void MarkGeoTagImage(String imagePath, Location location)
    {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GpsInfo.latitudeRef(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,  Double.toString(location.getLongitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GpsInfo.longitudeRef(location.getLongitude()));


            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            exif.setAttribute(ExifInterface.TAG_DATETIME,fmt_Exif.format(new Date(location.getTime())));
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location readGeoTagImage(String imagePath)
    {
        Location loc = new Location("");
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            float [] latlong = new float[2] ;
            if(exif.getLatLong(latlong)){
                loc.setLatitude(latlong[0]);
                loc.setLongitude(latlong[1]);
            }
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            loc.setTime(fmt_Exif.parse(date).getTime());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return loc;
    }

    public void showExif(ExifInterface exif) {

        geoDegree gd = new geoDegree(exif);


        String myAttribute = "[Exif information] \n\n";

        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);

        //((PhotoActivity) mContext).metadataView.setText(myAttribute);
    }


    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }
    public String getDateTime(ExifInterface exif) {
        dateTime = getTagString(ExifInterface.TAG_DATETIME, exif);
        return dateTime;
    }
/*
    public double getLat(ExifInterface exif) {
        geoDegree gd = new geoDegree(exif);
        lat = gd.Latitude;
        return lat;
    }*/
    public double getLat(ExifInterface exif) {
        geoDegree gd = new geoDegree(exif);
        lat = gd.Latitude;
        return lat;
    }

    public double getLon(ExifInterface exif) {
        geoDegree gd = new geoDegree(exif);
        lon = gd.Longitude;
        return lon;
    }

}


