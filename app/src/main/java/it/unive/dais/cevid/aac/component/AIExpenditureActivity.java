package it.unive.dais.cevid.aac.component;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.PagerAdapter;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AIExpenditureActivity extends AppCompatActivity {
    private static List<SoldipubbliciParser.Data> speseEnte;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_expenditure);

        TabLayout tabLayout = setTabLayout();
        setViewPager(tabLayout);
    }

    private TabLayout setTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("2017"));
        tabLayout.addTab(tabLayout.newTab().setText("2016"));
        tabLayout.addTab(tabLayout.newTab().setText("2015"));
        tabLayout.addTab(tabLayout.newTab().setText("2014"));
        tabLayout.addTab(tabLayout.newTab().setText("2013"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return tabLayout;
    }

    private void setViewPager(TabLayout tabLayout) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
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

    public static void setSpeseEnte(List<SoldipubbliciParser.Data> values) {
        speseEnte = new ArrayList<>(values);
    }

    public static List<SoldipubbliciParser.Data> getSpeseEnte() {
        return speseEnte;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_details, menu);

        menu.findItem(R.id.menu_details_swap).setVisible(true);

        this.optionsMenu = menu;

        return true;
    }
}
