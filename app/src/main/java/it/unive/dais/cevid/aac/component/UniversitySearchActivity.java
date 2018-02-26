package it.unive.dais.cevid.aac.component;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import it.unive.dais.cevid.aac.fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.aac.util.CompanyComparator;
import it.unive.dais.cevid.datadroid.lib.parser.Parser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;

public class UniversitySearchActivity extends AppCompatActivity {
    private static final String TAG = "UniSearchActivity";

    public static final String UNIVERSITY_LIST = "UNI";
    private static final String BUNDLE_LIST = "LIST";
    private static final int FISCAL_CODE_LENGTH = 11;
    private static final int SEARCH_INPUT_MIN_LENGTH = 3;

    private UniversityItem universityItem;
    private SoldipubbliciParser soldiPubbliciParser;
    private AppaltiParser appaltiParser;
    private LinearLayout mainView;
    private String soldiPubbliciText = " ";
    private String appaltiText = " ";
    private boolean singleElement;

    private List<UniversityItem> universityItems;
    private Map<String, SoldipubbliciParser> codiceEnteSoldiPubbliciParserMap;
    private Map<String, AppaltiParser> codiceEnteAppaltiParserMap;

    // wrappers for parsers
    //

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(UNIVERSITY_LIST, (Serializable) universityItems);
//        saveParserState(savedInstanceState, appaltiParser);
//        saveParserState(savedInstanceState, soldiPubbliciParser);
        super.onSaveInstanceState(savedInstanceState);
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

        setContentView(R.layout.activity_university_search);

        mainView = (LinearLayout) findViewById(R.id.search_activity);
        mainView.requestFocus();

        ProgressBarManager progressBarManager = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_university_search));

        checkSavedInstanceState(savedInstanceState);

        if (singleElement) {
            manageSingleElement(progressBarManager);
        }
        else {
            manageMutipleElements(progressBarManager);
        }

        manageTendersButton();

        mainView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideKeyboard(v);
            }
        });

    }

    private void alert(String msg) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // crea l'activity da zero
            Serializable l = getIntent().getSerializableExtra(UNIVERSITY_LIST);
            universityItems = (List<UniversityItem>) l;

            if (universityItems.size() == 1) {
                singleElement = true;
                universityItem = universityItems.get(0);
            }
            else {
                singleElement = false;
            }
        } else {
            // ricrea l'activity deserializzando alcuni dati dal bundle
            universityItems = (List<UniversityItem>) savedInstanceState.getSerializable(UNIVERSITY_LIST);
        }
    }

    private void manageSingleElement(ProgressBarManager progressBarManager) {
        launchParsersSingleElement(progressBarManager);
        setSearchViewsSingleElement();
        manageCombineButton();
        setTitleSingleElement();
    }

    private void manageMutipleElements(ProgressBarManager progressBarManager) {
        launchParsersMultipleElements(progressBarManager);
        setSearchViewsMultipleElements();
        manageCombineButton();
        setTitleMultipleElements();
    }

    private Map<String, List<SoldipubbliciParser.Data>> populateCodiceEnteExpenditureMap(){
        Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap = new HashMap<>();
        SoldipubbliciParser soldiPubbliciParser;

        for (UniversityItem item : universityItems) {
            soldiPubbliciParser = codiceEnteSoldiPubbliciParserMap.get(item.getId());
            codiceEnteExpenditureMap.put(item.getId(), processQuery(soldiPubbliciParser, soldiPubbliciText, Soldipubblici_getText, Soldipubblici_getCode));
        }

        return codiceEnteExpenditureMap;
    }

    private Map<String, List<AppaltiParser.Data>> populateCodiceEnteTendersMap(){
        Map<String, List<AppaltiParser.Data>> codiceTendersMap = new HashMap<>();
        AppaltiParser appaltiParser;

        for (UniversityItem item : universityItems) {
            appaltiParser = codiceEnteAppaltiParserMap.get(item.getId());
            codiceTendersMap.put(item.getId(), processQuery(appaltiParser, appaltiText, Appalti_getText, Appalti_getCode));
        }

        return codiceTendersMap;
    }


    //Combine Button stuff

    private void manageCombineButton() {
        Button combineButton = (Button) findViewById(R.id.button_combine_data);
        combineButton.setOnClickListener(view -> {

            if (appaltiText.length() < SEARCH_INPUT_MIN_LENGTH && soldiPubbliciText.length() < SEARCH_INPUT_MIN_LENGTH)
                alert("Compilare entrambi i campi di testo con stringhe di almeno 3 caratteri " +
                        "ed assicurarsi che diano risultati per richiedere una ricerca combinata.");
            else {
                Intent intent = new Intent(UniversitySearchActivity.this, UniversityResultActivity.class);
                if (singleElement) {
                    manageCombineButtonSingleElement(intent);
                }
                else {
                    intent.putExtra(UniversityResultActivity.LIST_UNIVERSITY_ITEMS, (Serializable) universityItems);
                    manageCombineButtonMultipleElements();
                }
                startActivity(intent);
            }

        });
    }

    private void manageCombineButtonSingleElement(Intent intent) {
        List<AppaltiParser.Data> tendersFilteredList;
        List<SoldipubbliciParser.Data> expenditureFilteredList;

        tendersFilteredList = processQuery(appaltiParser, appaltiText, Appalti_getText, Appalti_getCode);
        expenditureFilteredList = processQuery(soldiPubbliciParser, soldiPubbliciText, Soldipubblici_getText, Soldipubblici_getCode);

        if (expenditureFilteredList != null && tendersFilteredList != null) {
            intent.putExtra(UniversityResultActivity.LIST_SOLDIPUBBLICI, (Serializable) expenditureFilteredList);
            intent.putExtra(UniversityResultActivity.LIST_APPALTI, (Serializable) tendersFilteredList);
        }
    }

    private void manageCombineButtonMultipleElements() {
        Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap = populateCodiceEnteExpenditureMap();
        Map<String, List<AppaltiParser.Data>> codiceEnteTendersMap = populateCodiceEnteTendersMap();

        UniversityResultActivity.setCodiceEnteExpenditureMap(codiceEnteExpenditureMap);
        UniversityResultActivity.setCodiceEnteTendersMap(codiceEnteTendersMap);
    }


    //Title stuff

    private void setTitleSingleElement() {
        TextView title = (TextView) findViewById(R.id.univeristy_name);
        title.setText(universityItem.getTitle());
    }

    private void setTitleMultipleElements(){
        TextView title = (TextView) findViewById(R.id.univeristy_name);

        String s = "Confronto: ";

        for (UniversityItem item : universityItems) {
            s += item.getTitle() + ", ";
        }

        title.setText(s);
    }


    //Parsers stuff

    private void launchParsersSingleElement(ProgressBarManager progressBarManager) {
        // TODO: salvare lo stato dei parser con un proxy serializzabile
        soldiPubbliciParser = new SoldipubbliciParser(universityItem.getCodiceComparto(), universityItem.getId(), progressBarManager);
        appaltiParser = new AppaltiParser(universityItem.getUrls(), progressBarManager);
        soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchParsersMultipleElements(ProgressBarManager progressBarManager) {
        codiceEnteSoldiPubbliciParserMap = new HashMap<>();
        codiceEnteAppaltiParserMap = new HashMap<>();

        for (UniversityItem item : universityItems) {
            SoldipubbliciParser soldiPubbliciParser = new SoldipubbliciParser(item.getCodiceComparto(), item.getId(), progressBarManager);
            AppaltiParser appaltiParser = new AppaltiParser(item.getUrls(), progressBarManager);

            codiceEnteSoldiPubbliciParserMap.put(item.getId(), soldiPubbliciParser);
            codiceEnteAppaltiParserMap.put(item.getId(), appaltiParser);

            soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    //SearchViews stuff

    private void setSearchViewsSingleElement() {
        SearchView appaltiSearch = initializeSearchView(R.id.search_tenders);
        SearchView soldiPubbliciSearch = initializeSearchView(R.id.search_exp);

        if (singleElement) {
            setSingleListenerSingleElement(appaltiSearch, appaltiParser, UniversityResultActivity.LIST_APPALTI,
                    Appalti_getText, Appalti_getCode, x -> {
                        UniversitySearchActivity.this.appaltiText = x;
                        return null;
                    });

            setSingleListenerSingleElement(soldiPubbliciSearch, soldiPubbliciParser, UniversityResultActivity.LIST_SOLDIPUBBLICI,
                    Soldipubblici_getText, Soldipubblici_getCode, x -> {
                        UniversitySearchActivity.this.soldiPubbliciText = x;
                        return null;
                    });
        }
    }

    private void setSearchViewsMultipleElements() {
        SearchView appaltiSearch = initializeSearchView(R.id.search_tenders);
        SearchView soldiPubbliciSearch = initializeSearchView(R.id.search_exp);

        setSingleListenerMultipleElements(appaltiSearch, UniversityResultActivity.LIST_APPALTI, x -> {
            UniversitySearchActivity.this.appaltiText = x;
            return null;
        });

        setSingleListenerMultipleElements(soldiPubbliciSearch, UniversityResultActivity.LIST_SOLDIPUBBLICI, x -> {
            UniversitySearchActivity.this.soldiPubbliciText = x;
            return null;
        });
    }

    private SearchView initializeSearchView(int resourceId) {
        SearchView searchView = (SearchView) findViewById(resourceId);
        searchView.onActionViewExpanded();

        return searchView;
    }

    private <T> void setSingleListenerSingleElement(final SearchView v, final AsyncParser<T, ?> parser, final String label,
                                                    final Function<T, String> getText, final Function<T, Integer> getCode, final Function<String, Void> setText) {
        v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Intent intent = new Intent(UniversitySearchActivity.this, UniversityResultActivity.class);

                List <T> l = processQuery(parser, text, getText, getCode);

                if (l != null && !l.isEmpty()) {
                    intent.putExtra(label, (Serializable) l);
                    startActivity(intent);
                }
                else
                    alert("La ricerca non ha prodotto alcun risultato. Provare con altri valori.");

                v.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setText.apply(newText);
                return false;
            }
        });
    }

    private void setSingleListenerMultipleElements(final SearchView v, final String label, final Function<String, Void> setText) {
        v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Intent intent = new Intent(UniversitySearchActivity.this, UniversityResultActivity.class);
                Map m;

                intent.putExtra(UniversityResultActivity.LIST_UNIVERSITY_ITEMS, (Serializable) universityItems);

                if (label == UniversityResultActivity.LIST_SOLDIPUBBLICI) {
                    m = populateCodiceEnteExpenditureMap();
                    UniversityResultActivity.setCodiceEnteExpenditureMap(m);
                }
                else {
                    m = populateCodiceEnteTendersMap();
                    UniversityResultActivity.setCodiceEnteTendersMap(m);
                }

                v.clearFocus();

                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setText.apply(newText);
                return false;
            }
        });
    }

    private <T> List<T> processQuery(AsyncParser<T, ?> parser, String text, Function<T, String> getText,
                                     Function<T, Integer> getCode) {
        try {
            List<T> l = new ArrayList<>(parser.getAsyncTask().get());   // clona la lista per poterla manipolare in sicurezza
            if (!text.isEmpty()) {
                if (text.matches("[0-9]+"))
                    DataManipulation.filterByCode(l, Integer.parseInt(text), getCode);
                else
                    DataManipulation.filterByWords(l, text.split(" "), getText, false);
                return l;
            }
        } catch (InterruptedException | ExecutionException e) {
            alert(String.format("Errore inatteso: %s. Riprovare.", e.getMessage()));
            Log.e(TAG, String.format("exception caught during parser %s", parser.getName()));
            e.printStackTrace();
        }
        return null;
    }


    //Tenders (Company) and Expenditure stuff

    private void setTendersUniversityDetailsActivity(Map<String, Company> stringCompanyMap) {
        ArrayList<Company> values = new ArrayList<>(stringCompanyMap.values());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            values.sort(new CompanyComparator());
        }

        UniversityDetailsActivity.setAppalti(values); // troppi dati, usiamo un campo statico
    }

    private void setExpenditureUniversityDetailsActivity() {
        List<SoldipubbliciParser.Data> spese = new ArrayList<>();
        try {
            spese = soldiPubbliciParser.getAsyncTask().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        UniversityDetailsActivity.setSpese(spese);
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

    private void manageTendersButton () {
        Button tendersButton = (Button) findViewById(R.id.button_view_tenders);
        if (singleElement) {
            tendersButton.setOnClickListener(v -> {
                Map<String, Company> stringCompanyMap = new HashMap<>();

                populateStringCompanyMap(stringCompanyMap);

                setTendersUniversityDetailsActivity(stringCompanyMap);
                setExpenditureUniversityDetailsActivity();

                Intent intent = new Intent(UniversitySearchActivity.this, UniversityDetailsActivity.class);
                startActivity(intent);
            });
        }
        else {
            tendersButton.setVisibility(View.INVISIBLE);
        }
    }


    //High order functions

    private static final Function<AppaltiParser.Data, String> Appalti_getText = x -> x.oggetto;

    private static final Function<AppaltiParser.Data, Integer> Appalti_getCode = x -> Integer.parseInt(x.cig);

    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getText = x -> x.descrizione_codice;

    private static final Function<SoldipubbliciParser.Data, Integer> Soldipubblici_getCode = x -> Integer.parseInt(x.codice_siope);

}
