package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by Fonto on 04/09/17.
 */

public class UniversityItem extends AbstractItem implements MapItem, Serializable{
    private static final String CODICE_COMPARTO = "UNI";

    public UniversityItem(@NonNull String id, @NonNull String title, @NonNull String cf, @NonNull String description,
                          double latitude, double longitude) {
        super(id, title, cf, description, latitude, longitude);
    }

    @NonNull
    @Override
    public String getCodiceComparto() {
        return CODICE_COMPARTO;
    }

}
