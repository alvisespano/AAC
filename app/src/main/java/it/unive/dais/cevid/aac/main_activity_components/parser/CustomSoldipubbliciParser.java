package it.unive.dais.cevid.aac.main_activity_components.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by spano on 04/01/2018.
 */
public class CustomSoldipubbliciParser extends AbstractAsyncParser<CustomSoldipubbliciParser.Data, PercentProgressStepper> {

    public CustomSoldipubbliciParser(@Nullable ProgressBarManager pbm) {
        super(pbm);
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        Request request = new Request.Builder()
                .url("http://soldipubblici.gov.it/it/chi/search/%20")
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();

        try {
            return parseJSON(new OkHttpClient().newCall(request).execute().body().string());
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    protected List<Data> parseJSON(String data) throws JSONException {
        List<Data> r = new ArrayList<>();
        JSONArray ja = new JSONArray(data);
        for (int i = 0; i < ja.length(); i++) {
            JSONObject j = ja.getJSONObject(i);
            Data d = new Data();
            d.ripartizione_geografica = j.getString("ripartizione_geografica");
            d.descrizione_provincia = j.getString("descrizione_provincia");
            d.data_ingresso_siope = j.getString("data_ingresso_siope");
            d.codice_sottocomparto = j.getString("codice_sottocomparto");
            d.codice_provincia = j.getString("codice_provincia");
            d.descrizione_regione = j.getString("descrizione_regione");
            d.numero_abitanti = j.getString("numero_abitanti");
            d.descrizione_ente = j.getString("descrizione_ente");
            d.descrizione_ente_not_stemmed = j.getString("descrizione_ente_not_stemmed");
            d.codice_regione = j.getString("codice_regione");
            d.codice_fiscale = j.getString("codice_fiscale");
            d.codice_comparto = j.getString("codice_comparto");
            d.codice_comune = j.getString("codice_comune");
            d.codice_ente = j.getString("codice_ente");
            d.data_uscita_siope = j.getString("data_uscita_siope");
            d._version_ = j.getString("_version_");
            r.add(d);
        }
        return r;
    }

    public static class Data implements Serializable {
        public String ripartizione_geografica;
        public String descrizione_provincia;
        public String data_ingresso_siope;
        public String codice_sottocomparto;
        public String codice_provincia;
        public String descrizione_regione;
        public String numero_abitanti;
        public String descrizione_ente;
        public String descrizione_ente_not_stemmed;
        public String codice_regione;
        public String codice_fiscale;
        public String codice_comparto;
        public String codice_comune;
        public String codice_ente;
        public String data_uscita_siope;
        public String _version_;
    }

}
