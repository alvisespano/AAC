package it.unive.dais.cevid.aac.component;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.CompanyAdapter;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.aac.util.CompanyComparator;

public class UniversityDetailsActivity extends AppCompatActivity {
    public static final String LIST_APPALTI ="LIST_APPALTI";
    private ArrayList<Company> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_details);
        items = (ArrayList<Company>) getIntent().getSerializableExtra(LIST_APPALTI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            items.sort(new CompanyComparator());
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_company);
        v.setLayoutManager(layoutManager);
        CompanyAdapter adapter = new CompanyAdapter(items);
        v.setAdapter(adapter);

    }
}
