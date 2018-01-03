package it.unive.dais.cevid.aac.parser;

/**
 * Created by fbusolin on 15/11/17.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.sync.Pool;
import it.unive.dais.cevid.datadroid.lib.util.PercentProgressStepper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author fbusolin
 */
public class ParticipantParser extends ParserWithProgressBar<ParticipantParser.Data, PercentProgressStepper, ParticipantParser.Actual> {

    public ParticipantParser(@NonNull String iva, @NonNull Pool<ProgressBar> pb) {
        super(new Actual(iva), pb);
    }

    public static class Actual extends AbstractAsyncParser<Data, PercentProgressStepper> {
        private final static String TAG = "ParticipantParser";
        private final static String single = "%27";
        private final static String pair = "%22";
        private final static String space = "%20";
        private final static String res2015 = "f2fcfe51-fe2b-4e5b-a730-659e24aa2b1d";
        private final static String res2016 = "996e869f-d3a3-4938-bd87-38d8d688860a";
        private final static String res2017 = "1ffc9410-9d28-47c6-b729-efc4de4e3287";
        private final String iva;

        public Actual(String iva) {
            this.iva = iva;
        }

        @NonNull
        public List<Data> parse() throws IOException {
            OkHttpClient client = new OkHttpClient();
            List<Data> returnList = new ArrayList<>();
            Request request2015 = new Request.Builder()
                    .url(this.buildURL(res2015))
                    .addHeader("Content-Type", "application/content-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Accept", "Application/json")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .build();
            Request request2016 = new Request.Builder()
                    .url(this.buildURL(res2016))
                    .addHeader("Content-Type", "application/content-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Accept", "Application/json")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .build();
            Request request2017 = new Request.Builder()
                    .url(this.buildURL(res2017))
                    .addHeader("Content-Type", "application/content-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Accept", "Application/json")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .build();
            try {
                returnList.addAll(parseJSON(client.newCall(request2015).execute().body().string()));
                returnList.addAll(parseJSON(client.newCall(request2016).execute().body().string()));
                returnList.addAll(parseJSON(client.newCall(request2017).execute().body().string()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnList;
        }

        private String buildURL(String resource) {
            return "http://dati.consip.it/api/action/datastore_search_sql?"
                    + "sql=SELECT%20*%20"
                    + "FROM" + space + pair + resource + pair + space
                    + "WHERE%20%22Partita_Iva%22LIKE%20" + single + iva + single;

        /*SELECT *
        * FROM ...
        * WHERE "Partita_Iva" LIKE '[iva]'*/
        }

        private List<Data> parseJSON(@Nullable String string) throws JSONException {
            List<Data> r = new ArrayList<>();
            JSONObject jo = new JSONObject(string);
            JSONObject result = jo.getJSONObject("result");
            JSONArray array = result.getJSONArray("records");
            PercentProgressStepper prog = new PercentProgressStepper(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Data d = new Data();
                d.esito = obj.optString("Esito_Partecipazione");
                d.forma_partecipazione = obj.optString("Forma_Partecipazione");
                d.data_aggiudicazione = obj.optString("Data_Aggiudicazione/Abilitazione");
                d.id_lotto = obj.optString("Identificativo_Lotto");
                d.full_text = obj.optString("_full_text");
                d.nome_iniziativa = obj.optString("#Denominazione_Iniziativa");
                d.tipo_strumento = obj.optString("Tipo_Strumento");
                d.rag_sociale = obj.optString("Ragione_Sociale");
                d.prog_partecipante = obj.optString("Progressivo_Partecipante");
                d.nome_partecipante = obj.optString("Denominazione_Partecipazione");
                d.nome_lotto = obj.optString("Denominazione_Lotto");
                d.piva = obj.optString("Partita_Iva");
                d.capogruppo = obj.optString("Flag_Capogruppo");
                d.id_download = obj.optString("id");

                r.add(d);
                prog.step();
                publishProgress(prog);
            }
            return r;
        }
    }

    public static class Data implements Serializable {
        public String esito,
                forma_partecipazione,
                data_aggiudicazione,
                id_lotto,
                full_text,
                nome_iniziativa,
                tipo_strumento,
                rag_sociale,
                prog_partecipante,
                nome_partecipante,
                nome_lotto,
                piva,
                capogruppo,
                id_download;
    }

}
