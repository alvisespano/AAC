package it.unive.dais.cevid.aac.component;

import android.content.Intent;
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

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.CompanyAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.aac.util.RecyclerItemClickListener;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AIDetailsActivity extends AppCompatActivity {
    public static ArrayList<Company> appalti;
    public static ArrayList<SoldipubbliciParser.Data> spese;
    private RecyclerItemClickListener activeListener;

    public enum Mode {
        SPESE,
        APPALTI;
    }

    private Mode currentMode;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentMode = Mode.APPALTI;
        setContentView(R.layout.activity_ai_details);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_view_uni_details);
        v.setLayoutManager(layoutManager);
        if (appalti == null) appalti = new ArrayList<>();
        if (spese == null) spese = new ArrayList<>();
        setVisualizationMode(currentMode, appalti, v);
    }

    private void setVisualizationMode(Mode mode, ArrayList data, RecyclerView view) {
        switch (mode) {
            case APPALTI:
                ArrayList<Company> companies = (ArrayList<Company>) data;
                CompanyAdapter adapter = new CompanyAdapter(this, companies);
                view.setAdapter(adapter);
                view.removeOnItemTouchListener(activeListener);
                activeListener = new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Company company = companies.get(position);
                        Intent intent = new Intent(AIDetailsActivity.this, AICompanyDetailsActivity.class);
                        AICompanyDetailsActivity.setItems(company.tenders);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                });
                view.addOnItemTouchListener(activeListener);
                break;
            case SPESE:
                ArrayList<SoldipubbliciParser.Data> expenditures = (ArrayList<SoldipubbliciParser.Data>) data;
                List<EntitieExpenditure> el = new ArrayList<>();
                for (SoldipubbliciParser.Data x : expenditures)
                    el.add(new EntitieExpenditure(x, "2016"));
                SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(el, "1");
                view.setAdapter(soldiPubbliciAdapter);
                view.removeOnItemTouchListener(activeListener);
                activeListener = new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                });
                view.addOnItemTouchListener(activeListener);
                break;
        }
    }

    public static void setAppalti(List<Company> values) {
        appalti = new ArrayList<>(values);
    }

    public static void setSpese(List<SoldipubbliciParser.Data> data) {
        spese = new ArrayList<>(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_details, menu);
        setActiveItems(menu,currentMode);
        this.optionsMenu = menu;
        return true;
    }

    private void setActiveItems(Menu menu, Mode mode) {
        MenuItem money = menu.findItem(R.id.menu_details_money);
        MenuItem noMoney = menu.findItem(R.id.menu_details_no_money);
        switch(mode){
            case SPESE:
                money.setEnabled(true);
                money.setVisible(true);
                noMoney.setEnabled(true);
                noMoney.setVisible(true);
                break;
            case APPALTI:
                money.setEnabled(false);
                money.setVisible(false);
                noMoney.setEnabled(false);
                noMoney.setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView view = (RecyclerView) findViewById(R.id.list_view_uni_details);
        view.invalidate();
        switch (item.getItemId()) {
            case R.id.menu_details_swap:
                switchCurrentMode();
                if (currentMode == Mode.SPESE) {
                    setVisualizationMode(currentMode, spese, view);
                } else {
                    setVisualizationMode(currentMode, appalti, view);
                }
                setActiveItems(optionsMenu,currentMode);
                return true;
            case R.id.menu_details_money:
                if (currentMode == Mode.SPESE) {
                    setVisualizationMode(currentMode, filterExpenditure(spese, false), view);
                }
                setActiveItems(optionsMenu,currentMode);
                return true;
            case R.id.menu_details_no_money:
                if (currentMode == Mode.SPESE) {
                    setVisualizationMode(currentMode, filterExpenditure(spese, true), view);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private ArrayList filterExpenditure(ArrayList<SoldipubbliciParser.Data> data, boolean zero) {
        ArrayList<SoldipubbliciParser.Data> result = new ArrayList<>();
        for (SoldipubbliciParser.Data d : data) {
            if ((d.importo_2016.equals("0")) && (zero)) {
                result.add(d);
            } else if ((!d.importo_2016.equals("0")) && (!zero)) {
                result.add(d);
            }
        }
        return result;
    }

    private void switchCurrentMode() {
        if (currentMode == Mode.SPESE) currentMode = Mode.APPALTI;
        else currentMode = Mode.SPESE;
    }
}
