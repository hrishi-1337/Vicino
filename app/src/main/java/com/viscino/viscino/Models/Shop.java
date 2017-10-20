package com.viscino.viscino.Models;

/**
 * Created by j on 13-10-2017.
 */

public class Shop {

    private String id;
    private String Name;
    private String Url;
    private double Lat;
    private double Lng;
    public Shop(){
    }
    public Shop(String id,String name,String url,double lat,double lng){

        this.id=id;
        this.Name=name;
        this.Url=url;
        this.Lat=lat;
        this.Lng=lng;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.Name = name;
    }
    public void setUrl(String url) {
        this.Url = url;
    }
    public void setLat(double lat) {
        this.Lat=lat;
    }
    public void setLng(double lng) {
        this.Lng=lng;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return Name;
    }
    public String getUrl() {
        return Url;
    }
    public double getLat() {
        return Lat;
    }
    public double getLng() {
        return Lng;
    }
}