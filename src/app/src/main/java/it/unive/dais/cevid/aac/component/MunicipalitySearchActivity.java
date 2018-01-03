package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.parser.MunicipalityParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.sync.RefCountedProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.PercentProgressStepper;

public class MunicipalitySearchActivity extends AppCompatActivity {
    public static final String MUNICIPALITY_ITEM = "MUNICIPALITY_ITEM";
    public static String CODICE_ENTE = "ENTE", CODICE_COMPARTO = "COMPARTO";

    @Nullable
    private MySoldipubbliciParser soldipubbliciParser;
    @Nullable
    private AppaltiParser appaltiParser;
    @Nullable
    private MunicipalityParser municipalityParser; // TODO: dobbiamo ancora usarlo ma intanto è un attributo di classe
    @Nullable
    private MunicipalityItem municipalityItem;
    @Nullable
    private RefCountedProgressBar progressBarPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_search);
        progressBarPool = new RefCountedProgressBar((ProgressBar) findViewById(R.id.progress_bar_comuni));
        municipalityItem = (MunicipalityItem) getIntent().getSerializableExtra(MUNICIPALITY_ITEM);

        final String ente = getIntent().getStringExtra(CODICE_ENTE);
        final String comparto = getIntent().getStringExtra(CODICE_COMPARTO);

        soldipubbliciParser = new MySoldipubbliciParser(comparto, ente);
        soldipubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        municipalityParser = new MunicipalityParser(new InputStreamReader(getResources().openRawResource(
                getResources().getIdentifier("comuni",
                        "raw", getPackageName()))));

        municipalityParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        appaltiParser = new AppaltiParser(municipalityItem.getUrls());
        appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Button btnBalance = (Button) findViewById(R.id.municipality_balance_button);
        Button btnTender = (Button) findViewById(R.id.municipality_tender_button);

        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBalance();
            }
        });

        btnTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTender();
            }
        });

        ((TextView) findViewById(R.id.municipality_title)).setText(municipalityItem.getTitle());
        ((TextView) findViewById(R.id.municipality_desc)).setText(municipalityItem.getDescription());
    }

    private void clickTender() {
        Intent intent = new Intent(MunicipalitySearchActivity.this, MunicipalityTenderActivity.class);
        try {
            assert appaltiParser != null;
            intent.putExtra("appalti_ente", (Serializable) appaltiParser.getAsyncTask().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    protected void clickBalance() {
        assert municipalityItem != null;
        String descrizione_ente = municipalityItem.getDescription(), numero_abitanti = municipalityItem.getCapite();
        List<EntitieExpenditure> spese_ente_2017 = new ArrayList<>();
        List<EntitieExpenditure> spese_ente_2016 = new ArrayList<>();
        List<EntitieExpenditure> spese_ente_2015 = new ArrayList<>();
        List<EntitieExpenditure> spese_ente_2014 = new ArrayList<>();
        List<EntitieExpenditure> spese_ente_2013 = new ArrayList<>();

        /** codice_comparto = findCodiceCompartoByDescrizioneEnte(descrizione_ente);
         codice_ente = findCodiceEnteByDescrizioneEnte(descrizione_ente);
         numero_abitanti = findNumeroAbitantiByDescrizioneEnte(descrizione_ente);*/

        try {
            assert soldipubbliciParser != null;
            List<SoldipubbliciParser.Data> l = new ArrayList<>(soldipubbliciParser.getAsyncTask().get());
            for (SoldipubbliciParser.Data x : l) {
                if (!(x.importo_2017).equals("0") && !(x.importo_2017).equals("null") && !(x.importo_2017).equals("")) {
                    spese_ente_2017.add(new EntitieExpenditure(x, "2017"));
                }
                if (!(x.importo_2016).equals("0") && !(x.importo_2016).equals("null") && !(x.importo_2016).equals("")) {
                    spese_ente_2016.add(new EntitieExpenditure(x, "2016"));
                }
                if (!(x.importo_2015).equals("0") && !(x.importo_2015).equals("null") && !(x.importo_2015).equals("")) {
                    spese_ente_2015.add(new EntitieExpenditure(x, "2015"));
                }
                if (!(x.importo_2014).equals("0") && !(x.importo_2014).equals("null") && !(x.importo_2014).equals("")) {
                    spese_ente_2014.add(new EntitieExpenditure(x, "2014"));
                }
                if (!(x.importo_2013).equals("0") && !(x.importo_2013).equals("null") && !(x.importo_2013).equals("")) {
                    spese_ente_2013.add(new EntitieExpenditure(x, "2013"));
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MunicipalitySearchActivity.this, MunicipalityResultActivity.class);
        intent.putExtra("numero_abitanti", numero_abitanti);
        intent.putExtra("descrizione_ente", descrizione_ente);
        intent.putExtra("spese_ente_2017", (Serializable) spese_ente_2017);
        intent.putExtra("spese_ente_2016", (Serializable) spese_ente_2016);
        intent.putExtra("spese_ente_2015", (Serializable) spese_ente_2015);
        intent.putExtra("spese_ente_2014", (Serializable) spese_ente_2014);
        intent.putExtra("spese_ente_2013", (Serializable) spese_ente_2013);
        startActivity(intent);
    }

    protected class MySoldipubbliciParser extends ParserWithProgressBar<SoldipubbliciParser.Data, PercentProgressStepper, SoldipubbliciParser> {
        public MySoldipubbliciParser(String codiceComparto, String id) {
            super(new SoldipubbliciParser(codiceComparto, id), progressBarPool);
        }
    }

}

