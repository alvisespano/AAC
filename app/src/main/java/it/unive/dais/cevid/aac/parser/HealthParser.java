package it.unive.dais.cevid.aac.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.IncassiSanita;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

import it.unive.dais.cevid.aac.item.HealthItem;




/**
 * Created by Angelko on 3/2/2018.
 */

public class HealthParser extends AbstractAsyncParser<HealthParser.Data, PercentProgressStepper> implements Serializable{
    private static final String TAG = "HealthParser";

    public Context context;
    public HealthParser(@NonNull ProgressBarManager pbm, Context context) {
        super(pbm);
        this.context = context;
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        return parseRegions();
    }

    protected List<Data> parseRegions() {
        List<Data> r = new ArrayList<Data>();
        try {
        PercentProgressStepper prog = new PercentProgressStepper(1200); // aici sa adaugam si pasii pentru parsarea incassiSanita
        ProgressBarManager progman=null;
        //take data from geo_regioni
        InputStream regionsStream = context.getResources().openRawResource(R.raw.geo_regioni);
        CsvRowParser p = new CsvRowParser(new InputStreamReader(regionsStream), true, ",",progman);
        List<CsvRowParser.Row> rows = p.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            for (final CsvRowParser.Row riga : rows) {
                HealthParser.Data a = new HealthParser.Data();
                a.id = riga.get("ID");
                a.name = riga.get("NAME");
                a.latitude = Double.parseDouble(riga.get("LAT"));
                a.longitude = Double.parseDouble(riga.get("LON"));
                a.capoluogo = riga.get("CAPOLUOGO");
                r.add(a);
                prog.step();
                publishProgress(prog);
            }


        } catch (InterruptedException | ExecutionException | ParserException e) {
            e.printStackTrace();
        }
        return r;
    }


    public class Data {
        public String id,
                name, capoluogo;
        public double latitude, longitude;
        //public List<HealthItem.Expenditure> expenditureTitleList;
    }

}
