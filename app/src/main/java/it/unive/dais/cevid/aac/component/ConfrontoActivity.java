package it.unive.dais.cevid.aac.component;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.UniversityItem;

/**
 * Created by gianmarcocallegher on 12/02/2018.
 */

public class ConfrontoActivity extends AppCompatActivity {

    private List l;

    private Map positionTitleMap;
    private String mode;
    private FragmentAdapter fragmentAdapter;

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
