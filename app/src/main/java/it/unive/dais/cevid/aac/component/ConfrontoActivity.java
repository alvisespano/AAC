package it.unive.dais.cevid.aac.component;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * Created by gianmarcocallegher on 12/02/2018.
 */

public class ConfrontoActivity extends AppCompatActivity {

    private List l;

    private Map positionCodiceEnteMap;
    private String mode;
    private FragmentAdapter fragmentAdapter;
    private Map<String, SoldipubbliciParser> codiceEnteSoldiPubbliciParserMap;
    private Map<String, AppaltiParser> codiceEnteAppaltiParserMap;

    private static Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap;
    private static Map<String, List<AppaltiParser.Data>> codiceEnteTendersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);

        mode = getIntent().getStringExtra("Mode");
        Serializable sl = getIntent().getSerializableExtra("List");

        if (mode.equals("University")) {
            l = new ArrayList<UniversityItem>();
            l.addAll((List<UniversityItem>) sl);
        }
        else {
            l = new ArrayList<MunicipalityItem>();
            l.addAll((List<MunicipalityItem>) sl);
        }

        codiceEnteSoldiPubbliciParserMap = new HashMap<>();
        codiceEnteAppaltiParserMap = new HashMap<>();

        for (Object x : l) {
            SoldipubbliciParser soldiPubbliciParser =
                    new SoldipubbliciParser(((AbstractItem) x).getCodiceComparto(), ((AbstractItem) x).getId(), null);

            AppaltiParser appaltiParser = new AppaltiParser(((AbstractItem) x).getUrls(), null);

            codiceEnteSoldiPubbliciParserMap.put(((AbstractItem) x).getId(), soldiPubbliciParser);
            codiceEnteAppaltiParserMap.put(((AbstractItem) x).getId(), appaltiParser);

            soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        try {
            populateDataMap();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (l.size() > 2)
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int i = 0;

        positionCodiceEnteMap = new HashMap<Integer, String>();

        for (Object o : l) {
            if (mode.equals("University")) {
                tabLayout.addTab(tabLayout.newTab().setText(((UniversityItem) o).getTitle()), i);
                positionCodiceEnteMap.put(i, ((UniversityItem) o).getId());
            }
            else {
                tabLayout.addTab(tabLayout.newTab().setText(((MunicipalityItem) o).getTitle()), i);
                positionCodiceEnteMap.put(i, ((MunicipalityItem) o).getId());
            }

            i++;
        }

        //view pager stuff
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        fragmentAdapter = new FragmentAdapter(
                getSupportFragmentManager(),
                tabLayout.getTabCount(),
                this
        );

        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private <T> List<T> filterDataList(AsyncParser<T, ?> parser, String text, Function<T, String> getText) {
        try {
            List<T> l = new ArrayList<>(parser.getAsyncTask().get());
            DataManipulation.filterByWords(l, text.split(" "), getText, false);

            return l;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateDataMap() throws ExecutionException, InterruptedException {

        codiceEnteTendersMap = new HashMap<>();
        codiceEnteExpenditureMap = new HashMap<>();

        for (String codiceEnte : codiceEnteSoldiPubbliciParserMap.keySet()) {

            SoldipubbliciParser soldipubbliciParser = codiceEnteSoldiPubbliciParserMap.get(codiceEnte);
            AppaltiParser appaltiParser = codiceEnteAppaltiParserMap.get(codiceEnte);

            String query = "canc";

            List<SoldipubbliciParser.Data> expenditureList = filterDataList(soldipubbliciParser, query, Soldipubblici_getText);
            List<AppaltiParser.Data> tendersList = filterDataList(appaltiParser, query, Appalti_getText);

            codiceEnteExpenditureMap.put(codiceEnte, expenditureList);
            codiceEnteTendersMap.put(codiceEnte, tendersList);
        }
    }

    public Map getPositionCodiceEnteMap() {
        return positionCodiceEnteMap;
    }

    public Map<String, List<SoldipubbliciParser.Data>> getCodiceEnteExpenditureMap() {
        return codiceEnteExpenditureMap;
    }

    public Map<String, List<AppaltiParser.Data>> getCodiceEnteTendersMap() {
        return codiceEnteTendersMap;
    }

    private static final Function<AppaltiParser.Data, String> Appalti_getText = x -> x.oggetto;
    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getText = x -> x.descrizione_codice;
}
