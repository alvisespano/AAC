package it.unive.dais.cevid.aac.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.IncassiSanita;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

/**
 * Created by Angelko on 3/20/2018.
 */

public class SanitaParser extends AbstractAsyncParser<SanitaParser.Data, PercentProgressStepper> implements Serializable {
    private static final String TAG = "HealthParser";

    public Context context;
    public SanitaParser(@NonNull ProgressBarManager pbm, Context context) {
        super(pbm);
        this.context = context;
    }

    @NonNull
    @Override
    public List<SanitaParser.Data> parse() throws IOException {
        return parseSanita();
    }

    private List<Data> parseSanita() {
        List<SanitaParser.Data> r = new ArrayList<>();
        try {
            PercentProgressStepper prog = new PercentProgressStepper(1300);
            ProgressBarManager progman = null;
            InputStream incassiStream = context.getResources().openRawResource(R.raw.incassi_sanita);
            CsvRowParser pIncassi = new CsvRowParser(new InputStreamReader(incassiStream), true, ",", progman);
            List<CsvRowParser.Row> rows = pIncassi.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            for (final CsvRowParser.Row riga : rows) {
                SanitaParser.Data a = new SanitaParser.Data();
                a.id = riga.get("Id");
                a.nomeRegione = riga.get("Regione");
                a.titolo = riga.get("Titolo");
                a.codice = riga.get("Codice");
                a.descrizione = riga.get("Descrizione");
                a.importo = Float.parseFloat(riga.get("Importo"));
                r.add(a);
                prog.step();
                publishProgress(prog);
                //Log.d("Angelko", String.valueOf(a.importo));
            }
        } catch (InterruptedException | ExecutionException | ParserException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static class  Data {
        public String id,
                nomeRegione,
                titolo,
                codice,
                descrizione;
        public float importo;
    }
}
