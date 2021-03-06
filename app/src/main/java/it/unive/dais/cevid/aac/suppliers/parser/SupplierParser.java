package it.unive.dais.cevid.aac.suppliers.parser;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by fbusolin on 13/11/17.
 */

public class SupplierParser extends AbstractAsyncParser<SupplierParser.Data, PercentProgressStepper> implements Serializable {

    private static final String QUERY = "http://dati.consip.it/api/action/datastore_search_sql?" +
            "sql=SELECT%20*%20" +
            "FROM%20%22f476dccf-d60a-4301-b757-829b3e030ac6%22%20" +
            "ORDER%20BY%22Numero_Aggiudicazioni%22%20DESC%20LIMIT%20100";

    public SupplierParser(@NonNull ProgressBarManager pbm) {
        super(pbm);
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        Request request = new Request.Builder()
                .url(QUERY)
                .addHeader("Content-Type", "application/content-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "Application/json")
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
        JSONObject jo = new JSONObject(data);
        JSONObject result = jo.getJSONObject("result");
        JSONArray array = result.getJSONArray("records");
        PercentProgressStepper prog = new PercentProgressStepper(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Data d = new Data();
            d.id = obj.optString("id");
            d.n_abilitazioni = obj.optString("Numero_Abilitazioni");
            d.n_aggiudicati = obj.optString("Numero_Aggiudicazioni");
            d.forma_societaria = obj.optString("Forma_Societaria");
            d.indirizzo = obj.optString("Indirizzo_Sede_legale");
            d.piva = obj.optString("#Partita_Iva");
            d.provincia = obj.optString("Provincia_Sede_legale");
            d.n_transazioni = obj.optString("Numero_Transazioni");
            d.ragione_sociale = obj.optString("Ragione_Sociale");
            d.regione = obj.optString("Regione_Sede_legale");
            d.n_attivi = obj.optString("Numero_Contratti_Attivi");
            d.comune = obj.optString("Comune_Sede_legale");
            d.nazione = obj.optString("Nazione_Sede_legale");
            if (!Objects.equals(d.n_aggiudicati, "") && !Objects.equals(d.n_aggiudicati, "0")) {
                r.add(d);
            }
            prog.step();
            publishProgress(prog);
        }
        return r;
    }

    public static class Data implements Serializable {
        public String n_abilitazioni,
                n_aggiudicati,
                forma_societaria,
                indirizzo,
                piva,
                provincia,
                n_transazioni,
                ragione_sociale,
                regione,
                id,
                nazione,
                comune,
                n_attivi;
    }

}
