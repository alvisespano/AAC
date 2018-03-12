package it.unive.dais.cevid.aac.component;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.aac.util.CompanyComparator;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

public class AISearchActivity extends AppCompatActivity {
    private static final String TAG = "AISearchActivity";

    public static final String ABSTRACT_ITEM = "UNI";
    private static final String BUNDLE_LIST = "LIST";
    private static final int FISCAL_CODE_LENGTH = 11;

    private AbstractItem abstractItem;
    private SoldipubbliciParser soldiPubbliciParser;
    private AppaltiParser appaltiParser;
    private LinearLayout mainView;

    // wrappers for parsers
    //

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(ABSTRACT_ITEM, abstractItem);
//        saveParserState(savedInstanceState, appaltiParser);
//        saveParserState(savedInstanceState, soldiPubbliciParser);
    }

    // TODO: finire di implementare il salvataggio di stato dell'activity e dei parser
    private <T> void saveParserState(Bundle savedInstanceState, AsyncParser<T, ?> parser) {
        try {
            AsyncTask<Void, ?, List<T>> p = parser.getAsyncTask();
            switch (p.getStatus()) {
                case FINISHED:
                    savedInstanceState.putSerializable(BUNDLE_LIST, new ArrayList<T>(p.get()));
                    break;
                default:
                    break;
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, String.format("parser %s failed", parser.getClass().getSimpleName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainView = (LinearLayout) findViewById(R.id.search_activity);
        mainView.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ai_search);

        mainView = (LinearLayout) findViewById(R.id.search_activity);
        mainView.requestFocus();

        checkSavedInstanceState(savedInstanceState);

        manageFunctionality();
    }

    private void checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // crea l'activity da zero
            Serializable si = getIntent().getSerializableExtra(ABSTRACT_ITEM);
            abstractItem = (AbstractItem) si;

        }else {
            // ricrea l'activity deserializzando alcuni dati dal bundle
            abstractItem = (AbstractItem) savedInstanceState.getSerializable(ABSTRACT_ITEM);
        }
    }

    private void manageFunctionality() {
        ProgressBarManager progressBarManager =
                new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_ai_search));
        launchParsers(progressBarManager);
        manageSearchButton();
        manageTendersButton();
        manageExpenditureButton();
        setTitle();
    }


    //Title stuff

    private void setTitle() {
        TextView title = (TextView) findViewById(R.id.ai_name);
        title.setText(abstractItem.getTitle());
    }

    //Parsers stuff

    private void launchParsers(ProgressBarManager progressBarManager) {
        // TODO: salvare lo stato dei parser con un proxy serializzabile
        soldiPubbliciParser = new SoldipubbliciParser(abstractItem.getCodiceComparto(), abstractItem.getId(), progressBarManager);
        appaltiParser = new AppaltiParser(abstractItem.getUrls(), progressBarManager);
        soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //Tenders (Company) and Expenditure stuff

    private void setTendersAIDetailsActivity(Map<String, Company> stringCompanyMap) {
        ArrayList<Company> values = new ArrayList<>(stringCompanyMap.values());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            values.sort(new CompanyComparator());
        }

        AIDetailsActivity.setAppalti(values); // troppi dati, usiamo un campo statico
    }

    private void setExpenditureAIDetailsActivity() {
        List<SoldipubbliciParser.Data> spese = new ArrayList<>();
        try {
            spese = soldiPubbliciParser.getAsyncTask().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        AIDetailsActivity.setSpese(spese);
        startActivity(new Intent(AISearchActivity.this, AIExpenditureActivity.class));
    }

    private void populateStringCompanyMap(Map<String, Company> stringCompanyMap) {
        try {
            List<AppaltiParser.Data> appalti = appaltiParser.getAsyncTask().get();
            for (AppaltiParser.Data appalto : appalti) {
                String cfAgg = appalto.codiceFiscaleAgg;
                if (cfAgg.length() == FISCAL_CODE_LENGTH) { //TODO: segnalare i dati incompleti/errati magari
                    if (!stringCompanyMap.containsKey(cfAgg)) {
                        stringCompanyMap.put(cfAgg, new Company(cfAgg, appalto.aggiudicatario));
                    }
                    stringCompanyMap.get(cfAgg).addAppalto(appalto);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    //Button stuff

    private void manageSearchButton() {
        Button searchButton = (Button) findViewById(R.id.button_combine_data);
        searchButton.setOnClickListener(v -> {
            try {
                Intent intent =  new Intent(AISearchActivity.this, AIComparsionActivity.class);

                intent.putExtra(AIComparsionActivity.ABSTRACT_ITEMS, abstractItem);
                intent.putExtra(AIComparsionActivity.SINGLE_ELEMENT, true);

                AIComparsionActivity.setSoldiPubbliciList(soldiPubbliciParser.getAsyncTask().get());
                AIComparsionActivity.setAppaltiList(appaltiParser.getAsyncTask().get());

                startActivity(intent);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private void manageTendersButton () {
        Button tendersButton = (Button) findViewById(R.id.button_view_tenders);
        tendersButton.setVisibility(View.VISIBLE);

        tendersButton.setOnClickListener(v -> {
            Map<String, Company> stringCompanyMap = new HashMap<>();

            populateStringCompanyMap(stringCompanyMap);

            setTendersAIDetailsActivity(stringCompanyMap);
            setExpenditureAIDetailsActivity();

            Intent intent = new Intent(AISearchActivity.this, AIDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void manageExpenditureButton() {
        Button expenditureButton = (Button) findViewById(R.id.button_expenditure);
        expenditureButton.setVisibility(View.VISIBLE);
        expenditureButton.setOnClickListener(view -> {
            Intent intent = new Intent(AISearchActivity.this, AIExpenditureActivity.class);
            try {
                AIExpenditureActivity.setSpeseEnte(soldiPubbliciParser.getAsyncTask().get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        });
    }
}
