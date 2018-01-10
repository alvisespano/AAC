package it.unive.dais.cevid.aac.component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;

public class UniversityExpenditureDetailsActivity extends AppCompatActivity {

    public static final String DATA = "DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_expenditure_details);
        EntitieExpenditure expenditure = (EntitieExpenditure) getIntent().getSerializableExtra(DATA);
        ((TextView) findViewById(R.id.exp_siope)).setText(expenditure.getCodice_siope());
        ((TextView) findViewById(R.id.exp_cost)).setText(expenditure.getImporto());
        ((TextView) findViewById(R.id.exp_desc)).setText(expenditure.getDescrizione_codice());

    }
}
