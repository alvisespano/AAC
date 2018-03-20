package it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AIExpenditureActivity extends AppCompatActivity {
    private static final String TAG = "AIExpenditureActivity";
    public static final String ABSTRACT_ITEM = "ABSTRACT_ITEM";
    private static List<SoldipubbliciParser.Data> speseEnte;
    private FragmentAdapter adapter;
    private AbstractItem abstractItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_expenditure);

        abstractItem = (AbstractItem) getIntent().getSerializableExtra(ABSTRACT_ITEM);

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

        adapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), abstractItem.getCapite());
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
        inflater.inflate(R.menu.menu_expenditure, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem myMenuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) myMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.onQueryTextChange(newText.toLowerCase());

                return false;
            }
        });
        return true;
    }
}
