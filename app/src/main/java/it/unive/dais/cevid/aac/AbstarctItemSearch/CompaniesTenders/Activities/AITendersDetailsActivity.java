package it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.Activities;

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

import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.Adapters.CompanyAdapter;
import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.utils.Company;
import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.utils.RecyclerItemClickListener;
import it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Activities.AIExpenditureDetailsActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.AbstarctItemSearch.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AITendersDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AITendersDetailsActivity";
    public static final String ABSTRACTITEM = "ABSTRACTITEM";
    public static ArrayList<Company> appalti;
    public static ArrayList<SoldipubbliciParser.Data> spese;
    private RecyclerItemClickListener activeListener;

    private AbstractItem abstractItem;

    public enum Mode {
        SPESE,
        APPALTI
    }

    private enum ZeroMode{
        TRUE,
        FALSE,
        UNDEFINED
    }

    private Mode currentMode;
    private ZeroMode currentZeroMode;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentMode = Mode.APPALTI;
        currentZeroMode = ZeroMode.UNDEFINED;

        abstractItem = (AbstractItem) getIntent().getSerializableExtra(ABSTRACTITEM);

        setContentView(R.layout.activity_ai_tenders_details);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView v = (RecyclerView) findViewById(R.id.list_view_ai_details);
        v.setLayoutManager(layoutManager);

        if (appalti == null) appalti = new ArrayList<>();
        if (spese == null) spese = new ArrayList<>();

        setVisualizationMode(currentMode, appalti, v);
    }

    private void setVisualizationMode(Mode mode, ArrayList data, RecyclerView view) {
        switch (mode) {
            case APPALTI:
                manageAppaltiCase(view, data);
                break;
            case SPESE:
                manageSpeseCase(view, data);
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
        inflater.inflate(R.menu.menu_options_companies_details, menu);
        setActiveItems(menu, currentMode);

        menu.findItem(R.id.menu_details_swap).setVisible(true);

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
        RecyclerView view = (RecyclerView) findViewById(R.id.list_view_ai_details);
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
                    currentZeroMode = ZeroMode.FALSE;
                    setVisualizationMode(currentMode, filterExpenditure(spese, false), view);
                }

                setActiveItems(optionsMenu,currentMode);

                return true;

            case R.id.menu_details_no_money:
                if (currentMode == Mode.SPESE) {
                    currentZeroMode = ZeroMode.TRUE;
                    setVisualizationMode(currentMode, filterExpenditure(spese, true), view);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<SoldipubbliciParser.Data> filterExpenditure(ArrayList<SoldipubbliciParser.Data> data, boolean zero) {
        ArrayList<SoldipubbliciParser.Data> result = new ArrayList<>();

        for (SoldipubbliciParser.Data d : data) {
            if ((d.importo_2016.equals("0.0")) && (zero)) {
                result.add(d);
            } else if ((!d.importo_2016.equals("0.0")) && (!zero)) {
                result.add(d);
            }
        }
        return result;
    }

    private void switchCurrentMode() {
        if (currentMode == Mode.SPESE) currentMode = Mode.APPALTI;
        else currentMode = Mode.SPESE;
    }

    private void manageAppaltiCase(RecyclerView view, ArrayList data) {
        ArrayList<Company> companies = (ArrayList<Company>) data;
        setAppaltiLayout(view, companies);
    }

    private void manageSpeseCase(RecyclerView view, ArrayList data) {
        ArrayList<SoldipubbliciParser.Data> expenditures = (ArrayList<SoldipubbliciParser.Data>) data;

        setSpeseLayout(view, expenditures);
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

    private void setSpeseLayout(RecyclerView view, List<SoldipubbliciParser.Data> el) {
        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(el, "2016", abstractItem.getCapite());
        view.setAdapter(soldiPubbliciAdapter);
        view.removeOnItemTouchListener(activeListener);

        activeListener = new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AITendersDetailsActivity.this, AIExpenditureDetailsActivity.class);
                if (currentZeroMode == ZeroMode.UNDEFINED)
                    intent.putExtra(AIExpenditureDetailsActivity.DATA, spese.get(position));
                else {
                    List<SoldipubbliciParser.Data> filteredExpenditure;
                    if (currentZeroMode == ZeroMode.FALSE) {
                        filteredExpenditure = filterExpenditure(spese, false);
                        intent.putExtra(AIExpenditureDetailsActivity.DATA, filteredExpenditure.get(position));
                    } else {
                        filteredExpenditure = filterExpenditure(spese, true);
                        intent.putExtra(AIExpenditureDetailsActivity.DATA, filteredExpenditure.get(position));
                    }
                }
                intent.putExtra(AIExpenditureDetailsActivity.YEAR, "2016");
                intent.putExtra(AIExpenditureDetailsActivity.CAPITE, abstractItem.getCapite());

                startActivity(intent);
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
