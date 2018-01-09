package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.CompanyAdapter;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.aac.util.RecyclerItemClickListener;

public class UniversityDetailsActivity extends AppCompatActivity {
    public static ArrayList<Company> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_details);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_company);
        v.setLayoutManager(layoutManager);
        if(items == null) items = new ArrayList<>();
        CompanyAdapter adapter = new CompanyAdapter(items);
        v.setAdapter(adapter);
        v.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), v, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Company company = items.get(position);
                Intent intent = new Intent(UniversityDetailsActivity.this,UniversityCompanyDetailsActivity.class);
                UniversityCompanyDetailsActivity.setItems(company.tenders);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

    public static void setItems(List<Company> values){
        items = new ArrayList<>(values);
    }
}
