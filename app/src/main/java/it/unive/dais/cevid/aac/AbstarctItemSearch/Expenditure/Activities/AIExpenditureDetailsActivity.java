package it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AIExpenditureDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AIExpenditureDetailsActivity";
    public static final String CAPITE = "CAPITE";
    public static final String DATA = "DATA";
    public static final String YEAR = "YEAR";
    private SoldipubbliciParser.Data expenditure;
    private String year;
    private int capite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_expenditure_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        capite = getIntent().getIntExtra(CAPITE, 1);
        year = getIntent().getStringExtra(YEAR);
        expenditure = (SoldipubbliciParser.Data) getIntent().getSerializableExtra(DATA);

        setTextViews();
    }

    private void setTextViews() {
        ((TextView)findViewById(R.id.ai_exp_details_title)).setText(expenditure.descrizione_ente);
        ((TextView)findViewById(R.id.exp_siope_code)).setText(expenditure.codice_siope);
        ((TextView)findViewById(R.id.exp_desc)).setText(expenditure.descrizione_codice);
        ((TextView)findViewById(R.id.exp_cost)).setText(getCostFromYear());
        ((TextView)findViewById(R.id.exp_procapite)).setText(String.valueOf(Double.parseDouble(getCostFromYear()) / capite));
        ((TextView)findViewById(R.id.exp_end_validity)).setText(expenditure.data_di_fine_validita);
    }

    private String getCostFromYear() {
        switch (year) {
            case "2017": return expenditure.importo_2017;
            case "2016": return expenditure.importo_2016;
            case "2015": return expenditure.importo_2015;
            case "2014": return expenditure.importo_2014;
            case "2013": return expenditure.importo_2013;
            default: return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
