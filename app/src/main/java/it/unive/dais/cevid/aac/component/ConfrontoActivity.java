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
import java.util.concurrent.ThreadPoolExecutor;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

/**
 * Created by gianmarcocallegher on 12/02/2018.
 */

public class ConfrontoActivity extends AppCompatActivity {

    private List l;

    private Map positionTitleMap;
    private String mode;
    private FragmentAdapter fragmentAdapter;
    private Map<String, SoldipubbliciParser> codiceEnteSoldiPubbliciParserMap;
    private Map<String, AppaltiParser> codiceEnteAppaltiParserMap;

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
            SoldipubbliciParser soldipubbliciParser =
                    new SoldipubbliciParser(((AbstractItem) x).getId(), "UNI", null);

            AppaltiParser appaltiParser = new AppaltiParser(((AbstractItem) x).getUrls(), null);

            codiceEnteSoldiPubbliciParserMap.put(((AbstractItem) x).getId(), soldipubbliciParser);
            codiceEnteAppaltiParserMap.put(((AbstractItem) x).getId(), appaltiParser);

            soldipubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (l.size() > 2)
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int i = 0;

        positionTitleMap = new HashMap<Integer, String>();

        for (Object o : l) {
            if (mode.equals("University")) {
                tabLayout.addTab(tabLayout.newTab().setText(((UniversityItem) o).getTitle()), i);
                positionTitleMap.put(i, ((UniversityItem) o).getTitle());
            }
            else {
                tabLayout.addTab(tabLayout.newTab().setText(((MunicipalityItem) o).getTitle()), i);
                positionTitleMap.put(i, ((MunicipalityItem) o).getTitle());
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

    public Map getPositionTitleMap() {
        return positionTitleMap;
    }
}
