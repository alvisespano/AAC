package it.unive.dais.cevid.aac.abstarct_item_search;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.activities.AITendersDetailsActivity;
import it.unive.dais.cevid.aac.abstarct_item_search.comparison.activities.AIComparisonActivity;
import it.unive.dais.cevid.aac.abstarct_item_search.expenditure.activities.AIExpenditureActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.utils.Company;
import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.utils.CompanyComparator;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

public class AISearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "AISearchActivity";

    public static final String ABSTRACT_ITEM = "UNI";
    private static final String BUNDLE_LIST = "LIST";
    private static final int FISCAL_CODE_LENGTH = 11;

    private AbstractItem abstractItem;
    private SoldipubbliciParser soldiPubbliciParser;
    private AppaltiParser appaltiParser;

    private String[] iconNameArray;
    private TypedArray iconTypedArray;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ai_search);

        iconNameArray = getResources().getStringArray(R.array.iconNameArray);
        iconTypedArray = getResources().obtainTypedArray(R.array.iconArray);

        checkSavedInstanceState(savedInstanceState);

        setUpLayout();

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

    private void setUpLayout() {
        GridView gridView = (GridView) findViewById(R.id.dashboard_grid);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(this);
    }

    private void manageFunctionality() {
        ProgressBarManager progressBarManager =
                new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_ai_search));
        launchParsers(progressBarManager);
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

        AITendersDetailsActivity.setAppalti(values); // troppi dati, usiamo un campo statico
    }

    private void setExpenditureAIDetailsActivity() {
        List<SoldipubbliciParser.Data> spese = new ArrayList<>();

        try {
            spese = soldiPubbliciParser.getAsyncTask().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        AITendersDetailsActivity.setSpese(spese);
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

    private void manageComparsion() {
        try {
            Intent intent =  new Intent(AISearchActivity.this, AIComparisonActivity.class);

            intent.putExtra(AIComparisonActivity.ABSTRACT_ITEM, abstractItem);
            intent.putExtra(AIComparisonActivity.SINGLE_ELEMENT, true);

            AIComparisonActivity.setSoldiPubbliciList(soldiPubbliciParser.getAsyncTask().get());
            AIComparisonActivity.setAppaltiList(appaltiParser.getAsyncTask().get());

            startActivity(intent);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void manageCompanies() {
        Map<String, Company> stringCompanyMap = new HashMap<>();

        populateStringCompanyMap(stringCompanyMap);

        setTendersAIDetailsActivity(stringCompanyMap);
        setExpenditureAIDetailsActivity();

        Intent intent = new Intent(AISearchActivity.this, AITendersDetailsActivity.class);
        intent.putExtra(AITendersDetailsActivity.ABSTRACTITEM, abstractItem);
        startActivity(intent);
    }

    private void manageExpenditure() {
        Intent intent = new Intent(AISearchActivity.this, AIExpenditureActivity.class);
        intent.putExtra(AIExpenditureActivity.ABSTRACT_ITEM, abstractItem);
        try {
            AIExpenditureActivity.setSpeseEnte(soldiPubbliciParser.getAsyncTask().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void manageInfo() {
        Intent intent = new Intent(AISearchActivity.this, AIInfoActivity.class);
        intent.putExtra(AIInfoActivity.ABSTRACT_ITEM, abstractItem);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String nameClicked = iconNameArray[position];

        if(nameClicked.equals(getString(R.string.combine_icon))) {
            manageComparsion();
        }
        else if(nameClicked.equals(getString(R.string.expenditure_icon))) {
            manageExpenditure();
        }
        else if(nameClicked.equals(getString(R.string.companies_icon))) {
            manageCompanies();
        }
        else if (nameClicked.equals(getString(R.string.info_icon))){
            manageInfo();
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return iconNameArray.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        private class ViewHolder {
            public ImageView icon;
            public TextView text;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // Create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(R.layout.dashboard_icon, null);
                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.dashboard_icon_text);
                holder.icon = (ImageView) v.findViewById(R.id.dashboard_icon_img);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.icon.setImageResource(iconTypedArray.getResourceId(position, -1));
            holder.text.setText(iconNameArray[position]);

            return v;
        }
    }
}
