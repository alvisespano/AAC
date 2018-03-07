package it.unive.dais.cevid.aac.parser;

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
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

import static it.unive.dais.cevid.aac.item.IncassiSanita.*;

/**
 * Created by Angelko on 3/2/2018.
 */

public class HealthParser extends AbstractAsyncParser<HealthParser.Data, PercentProgressStepper> implements Serializable{
    private static final String TAG = "MainActivity";

    public HealthParser(@NonNull ProgressBarManager pbm) {
        super(pbm);
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        return parseRegions();
    }

    protected List<Data> parseRegions() {
        List<Data> r = new ArrayList();
        PercentProgressStepper prog = new PercentProgressStepper(20);
        HealthParser.Data a = new HealthParser.Data();
        a.id="IT-ABRUZZO"; a.name="Abruzzo"; a.latitude=42.354008; a.longitude=13.391992; a.capoluogo="L'Aquila"; r.add(a);
        HealthParser.Data d = new HealthParser.Data();
        d.id="IT-BASILICATA"; d.name=" Basilicata"; d.latitude=40.633333;d.longitude=15.8; d.capoluogo=" Potenza";r.add(d);
        HealthParser.Data b = new HealthParser.Data();
        b.id="IT-CALABRIA"; b.name=" Calabria"; b.latitude=38.114444; b.longitude=15.65; b.capoluogo=" Reggio Calabria";r.add(b);
        HealthParser.Data c = new HealthParser.Data();
        c.id="IT-CAMPANIA"; c.name=" Campania"; c.latitude=40.833333; c.longitude=14.25; c.capoluogo=" Napoli";r.add(c);
        HealthParser.Data e = new HealthParser.Data();
        e.id="IT-EMILIA-ROMAGNA"; e.name="Emilia Romagna"; e.latitude=44.510556; e.longitude=10.956944; e.capoluogo=" Bologna" ;r.add(e);
        HealthParser.Data f = new HealthParser.Data();
        f.id="IT-FRIULI-VENEZIA-GIULIA"; f.name="Friuli Venezia Giulia"; f.latitude=45.636111; f.longitude= 13.804167; f.capoluogo=" Trieste" ;r.add(f);
        HealthParser.Data g = new HealthParser.Data();
        g.id="IT-LAZIO";  g.name="Lazio"; g.latitude=41.893056; g.longitude=12.482778; g.capoluogo=" Roma" ;r.add(g);
        HealthParser.Data h = new HealthParser.Data();
        h.id="IT-LIGURIA";  h.name="Liguria"; h.latitude=44.407186; h.longitude=8.933983; h.capoluogo=" Genova";r.add(h);
        HealthParser.Data i = new HealthParser.Data();
        i.id="IT-LOMBARDIA";  i.name="Lombardia"; i.latitude=45.464161; i.longitude=9.190336; i.capoluogo=" Milano";r.add(i);
        HealthParser.Data j = new HealthParser.Data();
        j.id="IT-MARCHE";  j.name="Marche"; j.latitude=43.616944; j.longitude=13.516667; j.capoluogo=" Ancona";r.add(j);
        HealthParser.Data k = new HealthParser.Data();
        k.id="IT-MOLISE";  k.name="Molise"; k.latitude=41.566667; k.longitude=14.666667; k.capoluogo=" Campobasso";r.add(k);
        HealthParser.Data l = new HealthParser.Data();
        l.id="IT-PIEMONTE";  l.name="Piemonte"; l.latitude=45.066667; l.longitude=7.7; l.capoluogo=" Torino";r.add(l);
        HealthParser.Data m = new HealthParser.Data();
        m.id="IT-PUGLIA";  m.name="Puglia"; m.latitude=41.125278; m.longitude=16.866667; m.capoluogo=" Bari";r.add(m);
        HealthParser.Data n = new HealthParser.Data();
        n.id="IT-SARDEGNA";  n.name="Sardegna"; n.latitude=39.216667; n.longitude=9.116667; n.capoluogo=" Cagliari" ;r.add(n);
        HealthParser.Data o = new HealthParser.Data();
        o.id="IT-SICILIA";  o.name="Sicilia"; o.latitude=38.115658; o.longitude=13.361262; o.capoluogo=" Palermo" ;r.add(o);
        HealthParser.Data p = new HealthParser.Data();
        p.id="IT-TOSCANA";  p.name="Toscana"; p.latitude=43.771389; p.longitude=11.254167; p.capoluogo=" Firenze" ;r.add(p);
        HealthParser.Data q = new HealthParser.Data();
        q.id="IT-TRENTINO-ALTO-ADIGE";  q.name="Trentino Alto Adige"; q.latitude=46.066667; q.longitude=11.116667; q.capoluogo=" Trento";r.add(q);
        HealthParser.Data x = new HealthParser.Data();
        x.id="IT-UMBRIA";  x.name="Umbria"; x.latitude=43.1121; x.longitude=12.3888; x.capoluogo=" Perugia" ;r.add(x);
        HealthParser.Data aa = new HealthParser.Data();
        aa.id="IT-VALLE-DA-AOSTA";  aa.name="Valle d'Aosta"; aa.latitude=45.737222; aa.longitude=7.320556; aa.capoluogo=" Aosta" ;r.add(aa);
        HealthParser.Data bb = new HealthParser.Data();
        bb.id="IT-VENETO";  bb.name="Veneto"; bb.latitude=45.439722; bb.longitude= 12.331944; bb.capoluogo=" Venice";r.add(bb);
        for (int ii = 0; ii < 20; ii++) {
            prog.step();
            publishProgress(prog);
        }
        return r;
    }


    public class Data {
        public String id,
                name, capoluogo;
        public double latitude, longitude;
    }

}
