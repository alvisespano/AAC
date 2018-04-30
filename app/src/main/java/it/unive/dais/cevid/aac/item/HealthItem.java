package it.unive.dais.cevid.aac.item;


import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import it.unive.dais.cevid.aac.parser.HealthParser;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;



public class HealthItem implements MapItem, Serializable {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String capoluogo;



    public HealthItem(@NonNull HealthParser.Data data) {
        this.id = data.id;
        this.name = data.name;
        this.latitude = data.latitude;
        this.longitude = data.longitude;
        this.capoluogo = data.capoluogo;
    }

    public HealthItem(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    @NonNull
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() throws Exception {
        return id;
    }

    @Override
    public String getDescription() throws Exception {
        return null;
    }

    public String getCapoluogo() {
        return capoluogo;
    }

    public void setCapoluogo(String capoluogo) {
        this.capoluogo = capoluogo;
    }

    public String toString(){
        return this.getName();
    }




}