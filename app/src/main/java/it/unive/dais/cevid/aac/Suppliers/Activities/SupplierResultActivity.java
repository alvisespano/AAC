package it.unive.dais.cevid.aac.Suppliers.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.Suppliers.Adapter.TenderAdapter;
import it.unive.dais.cevid.aac.Suppliers.parser.TenderParser;
import it.unive.dais.cevid.aac.Suppliers.parser.ParticipantParser;
import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.utils.RecyclerItemClickListener;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

public class SupplierResultActivity extends AppCompatActivity {
    public static final String TAG = "SupplierResultActivity";
    protected static final String BUNDLE_PARTECIPATIONS = "PARTS";
    private List<ParticipantParser.Data> tenders;
    private Map<ParticipantParser.Data, TenderParser.Data> map = new HashMap<>();
    private List<TenderParser> parsers = new ArrayList<>();
    private RecyclerView view;
    private ProgressBarManager progressBarPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_result);

        Intent intent = getIntent();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        view = (RecyclerView) findViewById(R.id.list_results);
        view.setLayoutManager(layoutManager);

        progressBarPool = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_supplier_result));

        tenders = (List<ParticipantParser.Data>) intent.getSerializableExtra(BUNDLE_PARTECIPATIONS);

        populateParsersList();

        setAdapter();
    }

    private void setAdapter(){
        TenderAdapter adapter = new TenderAdapter(tenders);
        view.setAdapter(adapter);
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        manageAdapterOnItemClick(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void manageAdapterOnItemClick(int position){
        try {
            List<TenderParser.Data> data = parsers.get(position).getAsyncTask().get();
            if (!(data == null || data.size() <= 0)) {
                TenderParser.Data tender = data.get(0);
                map.put(tenders.get(position), tender);
                Intent intent = new Intent(SupplierResultActivity.this, SupplierDetailsActivity.class);
                intent.putExtra(SupplierDetailsActivity.BUNDLE_BID, tender);
                startActivity(intent);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void populateParsersList(){
        for (ParticipantParser.Data p : tenders) {
            String lotto = p.id_lotto;
            TenderParser tenderParser = new TenderParser(lotto, progressBarPool);

            parsers.add(tenderParser);
            tenderParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
