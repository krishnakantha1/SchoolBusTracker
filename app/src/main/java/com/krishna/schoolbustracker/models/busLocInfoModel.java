package com.krishna.schoolbustracker.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class busLocInfoModel implements Serializable {
    String place,time;
    double lat,lng;
    public  busLocInfoModel(){
    }
    public busLocInfoModel(String place,String time,double lat,double lng){
        this.place=place;
        this.time=time;
        this.lat=lat;
        this.lng=lng;
    }
    public LatLng getPosition(){
        return new LatLng(lat,lng);
    }
    public String getPlace(){
        return place;
    }
    public String getTime(){
        return time;
    }


    public void setplace(String place) {
        this.place = place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public  void setLat(double lat){this.lat=lat;}

    public void setLng(double lng){this.lng=lng;}
}
