package it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.adapters.CompanyAdapter;
import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.utils.Company;
import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.utils.RecyclerItemClickListener;
import it.unive.dais.cevid.aac.abstarct_item_search.expenditure.activities.AIExpenditureDetailsActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.abstarct_item_search.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AITendersDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AITendersDetailsActivity";
    public static ArrayList<Company> appalti;
    private RecyclerItemClickListener activeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ai_tenders_details);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_view_ai_tenders_details);
        v.setLayoutManager(layoutManager);

        if (appalti == null)
            appalti = new ArrayList<>();

        manageAppalti(v, appalti);
    }

    public static void setAppalti(List<Company> values) {
        appalti = new ArrayList<>(values);
    }

    private void manageAppalti(RecyclerView view, ArrayList data) {
        ArrayList<Company> companies = (ArrayList<Company>) data;
        setAppaltiLayout(view, companies);
    }

    private void setAppaltiLayout(RecyclerView view, ArrayList<Company> companies) {
        CompanyAdapter adapter = new CompanyAdapter(this, companies);
        view.setAdapter(adapter);

        view.removeOnItemTouchListener(activeListener);

        activeListener = new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                manageAppaltiItemClick(companies, position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        });

        view.addOnItemTouchListener(activeListener);
    }

    private void manageAppaltiItemClick(ArrayList<Company> companies, int position) {
        Company company = companies.get(position);
        Intent intent = new Intent(AITendersDetailsActivity.this, AICompanyDetailsActivity.class);
        AICompanyDetailsActivity.setItems(company.tenders);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
}
