package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by spano on 07/12/2017.
 */
public abstract class AbstractItem implements MapItem, Serializable {
    @NonNull
    private final String title, description, id, cf;
    private int capite;
    private final double latitude, longitude;
    @NonNull
    private List<URL> urls;

    public AbstractItem(@NonNull String id, @NonNull String title, @NonNull String cf, @NonNull String description,
                        double latitude, double longitude) {
        this.id = id;
        this.cf = cf;
        this.description = description;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getCf() {
        return cf;
    }

    @NonNull
    public abstract String getCodiceComparto();

    @NonNull
    public int getCapite() {
        return capite;
    }

    @NonNull
    public List<URL> getUrls() {
        return urls;
    }

    @Override
    public String toString(){
        return this.title;
    }

    public void setCapite(int capite) {
        this.capite = capite;
    }

    public void setUrls(List<URL> urls) {
        this.urls = urls;
    }
}
