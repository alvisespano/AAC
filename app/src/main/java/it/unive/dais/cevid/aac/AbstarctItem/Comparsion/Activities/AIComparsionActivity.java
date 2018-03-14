package it.unive.dais.cevid.aac.AbstarctItem.Comparsion.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
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

import it.unive.dais.cevid.aac.AbstarctItem.AISearchActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.MainActivityComponents.MainActivity;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;

public class AIComparsionActivity extends AppCompatActivity {
    private static final String TAG = "AIComparsionActivity";

    public static final String ABSTRACT_ITEMS = "AI";
    //private static final String BUNDLE_LIST = "LIST";
    private static final int SEARCH_INPUT_MIN_LENGTH = 3;
    public static final String SINGLE_ELEMENT = "SINGLE_ELEMENT";

    private AbstractItem abstractItem;
    private LinearLayout mainView;
    private String soldiPubbliciText = " ";
    private String appaltiText = " ";
    private boolean singleElement;

    private static List<SoldipubbliciParser.Data> soldiPubbliciList;
    private static List<AppaltiParser.Data> appaltiList;

    private List<AbstractItem> abstractItems;
    private Map<String, SoldipubbliciParser> codiceEnteSoldiPubbliciParserMap;
    private Map<String, AppaltiParser> codiceEnteAppaltiParserMap;
    private SearchView soldiPubbliciSearch;
    private SearchView appaltiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_comparsion);

        mainView = (LinearLayout) findViewById(R.id.comparsion_activity);
        mainView.requestFocus();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProgressBarManager progressBarManager = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_ai_search));

        checkSavedInstanceState(savedInstanceState);

        appaltiSearch = initializeSearchView(R.id.search_tenders);
        soldiPubbliciSearch = initializeSearchView(R.id.search_exp);

        if (singleElement) {
            manageSingleElement();
        }
        else {
            manageMultipleElements(progressBarManager);
        }

        manageCombineButton();

        mainView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideKeyboard(v);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SINGLE_ELEMENT, singleElement);

        if (singleElement) {
            savedInstanceState.putSerializable(ABSTRACT_ITEMS, abstractItem);
        }
        else {
            savedInstanceState.putSerializable(ABSTRACT_ITEMS, (Serializable) abstractItems);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    private void checkSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            singleElement = getIntent().getBooleanExtra(SINGLE_ELEMENT, false);

            if (singleElement) {
                Serializable se = getIntent().getSerializableExtra(ABSTRACT_ITEMS);
                abstractItem = (AbstractItem) se;
            }
            else {
                Serializable l = getIntent().getSerializableExtra(ABSTRACT_ITEMS);
                abstractItems = (List<AbstractItem>) l;
            }
        }
        else {
            singleElement = savedInstanceState.getBoolean(SINGLE_ELEMENT);
            if (singleElement) {
                abstractItem = (AbstractItem) savedInstanceState.getSerializable(ABSTRACT_ITEMS);
            }
            else {
                abstractItems = (List<AbstractItem>) savedInstanceState.getSerializable(ABSTRACT_ITEMS);
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void manageSingleElement() {
        setSearchViewsSingleElement();
        setTitleSingleElement();
    }

    private void manageMultipleElements(ProgressBarManager progressBarManager) {
        launchParsersMultipleElements(progressBarManager);
        setSearchViewsMultipleElements();
        setTitleMultipleElements();
    }

    public static void setSoldiPubbliciList (List<SoldipubbliciParser.Data> values) {
        soldiPubbliciList = new ArrayList<>(values);
    }

    public static void setAppaltiList(List<AppaltiParser.Data> values) {
        appaltiList = new ArrayList<>(values);
    }

    //Parsers stuff

    private void launchParsersMultipleElements(ProgressBarManager progressBarManager) {
        codiceEnteSoldiPubbliciParserMap = new HashMap<>();
        codiceEnteAppaltiParserMap = new HashMap<>();

        for (AbstractItem item : abstractItems) {
            SoldipubbliciParser soldiPubbliciParser = new SoldipubbliciParser(item.getCodiceComparto(), item.getId(), progressBarManager);
            AppaltiParser appaltiParser = new AppaltiParser(item.getUrls(), progressBarManager);

            codiceEnteSoldiPubbliciParserMap.put(item.getId(), soldiPubbliciParser);
            codiceEnteAppaltiParserMap.put(item.getId(), appaltiParser);

            soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    //Title stuff

    private void setTitleSingleElement() {
        TextView title = (TextView) findViewById(R.id.ai_name);
        title.setText(abstractItem.getTitle());
    }

    private void setTitleMultipleElements(){
        TextView title = (TextView) findViewById(R.id.ai_name);

        String start = "Confronto: ";
        String s = start;

        for (AbstractItem item : abstractItems) {
            if (!s.equals(start))
                s += ", " + item.getTitle();
            else s += item.getTitle();
        }

        title.setText(s);
    }


    //SearchViews stuff

    private void setSearchViewsSingleElement() {
        setSingleListenerSingleElement(appaltiSearch, AIResultActivity.LIST_APPALTI,
                Appalti_getText, Appalti_getCode, x -> {
                    AIComparsionActivity.this.appaltiText = x;
                    return null;
                });

        setSingleListenerSingleElement(soldiPubbliciSearch, AIResultActivity.LIST_SOLDIPUBBLICI,
                Soldipubblici_getText, Soldipubblici_getCode, x -> {
                    AIComparsionActivity.this.soldiPubbliciText = x;
                    return null;
                });
    }

    private void setSearchViewsMultipleElements() {

        setSingleListenerMultipleElements(appaltiSearch, AIResultActivity.LIST_APPALTI, x -> {
            AIComparsionActivity.this.appaltiText = x;
            return null;
        });

        setSingleListenerMultipleElements(soldiPubbliciSearch, AIResultActivity.LIST_SOLDIPUBBLICI, x -> {
            AIComparsionActivity.this.soldiPubbliciText = x;
            return null;
        });
    }

    private SearchView initializeSearchView(int resourceId) {
        SearchView searchView = (SearchView) findViewById(resourceId);
        searchView.onActionViewExpanded();

        return searchView;
    }

    private <T> void setSingleListenerSingleElement(final SearchView v, final String label, final Function<T, String> getText,
                                                    final Function<T, Integer> getCode, final Function<String, Void> setText) {
        v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Intent intent = new Intent(AIComparsionActivity.this, AIResultActivity.class);

                List <T> l;

                if (label == AIResultActivity.LIST_SOLDIPUBBLICI)
                    l = processQuery((List<T>) soldiPubbliciList, text, getText, getCode);
                else
                    l = processQuery((List<T>) appaltiList, text, getText, getCode);

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
                Intent intent = new Intent(AIComparsionActivity.this, AIResultActivity.class);
                Map m;

                intent.putExtra(AIResultActivity.ABSTRACT_ITEMS, (Serializable) abstractItems);

                if (label == AIResultActivity.LIST_SOLDIPUBBLICI) {
                    try {
                        m = populateCodiceEnteExpenditureMap();
                        AIResultActivity.setCodiceEnteExpenditureMap(m);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        m = populateCodiceEnteTendersMap();
                        AIResultActivity.setCodiceEnteTendersMap(m);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
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

    private <T> List<T> processQuery(List<T> l, String text, Function<T, String> getText, Function<T, Integer> getCode) {
        if (!text.isEmpty()) {
            if (text.matches("[0-9]+"))
                DataManipulation.filterByCode(l, Integer.parseInt(text), getCode);
            else
                DataManipulation.filterByWords(l, text.split(" "), getText, false);
            return l;
        }

        return null;
    }


    //CombineButton stuff

    private void manageCombineButton() {
        Button combineButton = (Button) findViewById(R.id.button_combine_data);
        combineButton.setOnClickListener(view -> {

            if (appaltiText.length() < SEARCH_INPUT_MIN_LENGTH && soldiPubbliciText.length() < SEARCH_INPUT_MIN_LENGTH) {
                alert(getString(R.string.combine_button_alert));
            }
            else {
                if (singleElement) {
                    manageCombineButtonSingleElement();
                }
                else {
                    try {
                        manageCombineButtonMultipleElements();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void manageCombineButtonSingleElement() {
        List<AppaltiParser.Data> tendersFilteredList;
        List<SoldipubbliciParser.Data> expenditureFilteredList;

        expenditureFilteredList = processQuery(soldiPubbliciList, soldiPubbliciText, Soldipubblici_getText, Soldipubblici_getCode);
        tendersFilteredList = processQuery(appaltiList, appaltiText, Appalti_getText, Appalti_getCode);

        if (expenditureFilteredList != null && tendersFilteredList != null) {
            Intent intent = new Intent(AIComparsionActivity.this, AIResultActivity.class);

            intent.putExtra(AIResultActivity.LIST_SOLDIPUBBLICI, (Serializable) expenditureFilteredList);
            intent.putExtra(AIResultActivity.LIST_APPALTI, (Serializable) tendersFilteredList);

            startActivity(intent);
        }
        else {
            alert(getString(R.string.combine_button_single_element_alert));
        }
    }

    private void manageCombineButtonMultipleElements() throws ExecutionException, InterruptedException {
        Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap = populateCodiceEnteExpenditureMap();
        Map<String, List<AppaltiParser.Data>> codiceEnteTendersMap = populateCodiceEnteTendersMap();

        Intent intent = new Intent(AIComparsionActivity.this, AIResultActivity.class);

        intent.putExtra(AIResultActivity.ABSTRACT_ITEMS, (Serializable) abstractItems);
        AIResultActivity.setCodiceEnteExpenditureMap(codiceEnteExpenditureMap);
        AIResultActivity.setCodiceEnteTendersMap(codiceEnteTendersMap);

        intent.putExtra(AIResultActivity.ABSTRACT_ITEMS, (Serializable) abstractItems);

        startActivity(intent);
    }

    //utils method

    private Map<String, List<SoldipubbliciParser.Data>> populateCodiceEnteExpenditureMap() throws ExecutionException, InterruptedException {
        Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap = new HashMap<>();
        SoldipubbliciParser soldiPubbliciParser;

        for (AbstractItem item : abstractItems) {
            soldiPubbliciParser = codiceEnteSoldiPubbliciParserMap.get(item.getId());
            codiceEnteExpenditureMap.put(item.getId(), processQuery(soldiPubbliciParser.getAsyncTask().get(), soldiPubbliciText, Soldipubblici_getText, Soldipubblici_getCode));
        }

        return codiceEnteExpenditureMap;
    }

    private Map<String, List<AppaltiParser.Data>> populateCodiceEnteTendersMap() throws ExecutionException, InterruptedException {
        Map<String, List<AppaltiParser.Data>> codiceTendersMap = new HashMap<>();
        AppaltiParser appaltiParser;

        for (AbstractItem item : abstractItems) {
            appaltiParser = codiceEnteAppaltiParserMap.get(item.getId());
            codiceTendersMap.put(item.getId(), processQuery(appaltiParser.getAsyncTask().get(), appaltiText, Appalti_getText, Appalti_getCode));
        }

        return codiceTendersMap;
    }

    private void alert(String msg) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (singleElement) {
                    Intent intent = new Intent(AIComparsionActivity.this, AISearchActivity.class);
                    //intent.putExtra(AISearchActivity.ABSTRACT_ITEM, abstractItem);
                    startActivity(intent);
                }
                else {
                    startActivity(new Intent(AIComparsionActivity.this, MainActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //High order functions

    private static final Function<AppaltiParser.Data, String> Appalti_getText = x -> x.oggetto;

    private static final Function<AppaltiParser.Data, Integer> Appalti_getCode = x -> Integer.parseInt(x.cig);

    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getText = x -> x.descrizione_codice;

    private static final Function<SoldipubbliciParser.Data, Integer> Soldipubblici_getCode = x -> Integer.parseInt(x.codice_siope);

}
