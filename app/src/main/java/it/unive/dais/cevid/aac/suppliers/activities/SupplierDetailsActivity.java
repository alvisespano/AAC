package it.unive.dais.cevid.aac.suppliers.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.suppliers.parser.TenderParser;

public class SupplierDetailsActivity extends AppCompatActivity {
    public static final String TAG = "SupplierDetailsActivity";
    protected static final String BUNDLE_BID = "BID";
    private TenderParser.Data tender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_details);

        this.tender = (TenderParser.Data) getIntent().getSerializableExtra(BUNDLE_BID);

        setTextViews();
    }

    private void setTextViews(){
        setTextView(R.id.tender_title, tender.denominazione);
        setTextView(R.id.tender_title, tender.denominazione_lotto);
        setTextView(R.id.tender_plot, tender.id_lotto);
        setTextView(R.id.tender_ceiling, tender.importo_massimale + getString(R.string.euro_symbol));
    }

    private void setTextView(int resource, String text){
        TextView textView = (TextView) findViewById(resource);
        textView.setText(text);
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
