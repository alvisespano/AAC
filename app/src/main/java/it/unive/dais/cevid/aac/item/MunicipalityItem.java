package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import java.net.URL;
import java.util.List;

/**
 * Created by fbusolin on 13/11/17.
 */

public class MunicipalityItem extends AbstractItem {
    private static final String CODICE_COMPARTO = "PRO";

    public MunicipalityItem(@NonNull String id, @NonNull String title, @NonNull String cf, @NonNull String description,
                            int capite, double latitude, double longitude) {
        super(id, title, cf, description, latitude, longitude);
        super.setCapite(capite);
    }

    @NonNull
    @Override
    public String getCodiceComparto() {
        return CODICE_COMPARTO;
    }

}
