package com.viscino.viscino.Models;

/**
 * Created by j on 16-10-2017.
 */

public class Product {

    private String id;
    private String Name;
    private String Url;
    private double Price;
    public Product(){
    }
    public Product(String id,String name,String url,double price){

        this.id=id;
        this.Name=name;
        this.Url=url;
        this.Price=price;
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
    public void setLat(double price) {
        this.Price =price;
    }
    public String getid() {
        return id;
    }
    public String getName() {
        return Name;
    }
    public String getUrl() {
        return Url;
    }
    public double getPrice() {
        return Price;
    }
}
