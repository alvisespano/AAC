package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.SupplierItem;
import it.unive.dais.cevid.aac.parser.ParticipantParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

public class SupplierSearchActivity extends AppCompatActivity {
    public static final String TAG = "SupplierSearchActivity";
    public static String SUPPLIER_ITEM = "SUPPLY";
    private SupplierItem supplier;
    private View mainView;
    private ParticipantParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_search);

        checkSavedInstanceState(savedInstanceState);

        this.mainView = findViewById(R.id.supply_info_activity);

        ProgressBarManager sharedProgressBar = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_supplier_search));

        parser = new ParticipantParser(supplier.getPiva(), sharedProgressBar);
        parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setTextViews();

        setSuppliersExpandButton();
    }

    private void checkSavedInstanceState(Bundle savedInstanceState){
        // bundle restore
        if (savedInstanceState == null) {
            // crea l'activity da zero
            supplier = (SupplierItem) getIntent().getSerializableExtra(SUPPLIER_ITEM);
        } else {
            // ricrea l'activity deserializzando alcuni dati dal bundle
            supplier = (SupplierItem) savedInstanceState.getSerializable(SUPPLIER_ITEM);
        }
        //create activity
    }

    private void setTextViews(){
        setTextView(R.id.supply_title, supplier.getTitle());
        setTextView(R.id.supply_vat, supplier.getPiva());
        setTextView(R.id.supply_address, supplier.getAddress());
        setTextView(R.id.supply_type, supplier.getType());
    }

    private void setSuppliersExpandButton(){
        Button button = (Button) findViewById(R.id.button_supply_expand);
        button.setOnClickListener(v -> {
            setOnClickListenerSuppliersExpandButton();
        });
    }

    private void setOnClickListenerSuppliersExpandButton(){
        try {
            List<ParticipantParser.Data> data = parser.getAsyncTask().get();
            if (data.size() > 0) {
                Intent intent = new Intent(SupplierSearchActivity.this, SupplierResultActivity.class);
                intent.putExtra(SupplierResultActivity.BUNDLE_PARTECIPATIONS, new ArrayList<>(data));
                startActivity(intent);
            } else {
                alert(String.format("Trovati %d bandi attivati nel 2016 per %s", data.size(), supplier.getTitle()));
            }
        } catch (InterruptedException | ExecutionException e) {
            alert(String.format("Errore inatteso: %s. Riprovare.", e.getMessage()));
            Log.e(TAG, String.format("exception caught during parser %s", parser.getName()));
            e.printStackTrace();
        }
    }

    private void setTextView(int resource, String text){
        TextView textView = (TextView) findViewById(resource);
        textView.setText(text);
    }

    private void alert(String msg) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_SHORT).show();
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
