package it.unive.dais.cevid.aac.AbstarctItem.Comparsion.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unive.dais.cevid.aac.AbstarctItem.util.AILayoutSetter;
import it.unive.dais.cevid.aac.AbstarctItem.Comparsion.Fragment.FragmentAdapter;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class AIResultActivity extends AppCompatActivity {
    private static final String TAG = "AIResultActivity";

    public static final String LIST_APPALTI = "LIST_APPALTI";
    public static final String LIST_SOLDIPUBBLICI = "LIST_SOLDIPUBBLICI";
    public static final String ABSTRACT_ITEMS = "ABSTRACT_ITEMS";

    private static Map<String, List<SoldipubbliciParser.Data>> codiceEnteExpenditureMap = Collections.EMPTY_MAP;
    private static Map<String, List<AppaltiParser.Data>> codiceEnteTendersMap = Collections.EMPTY_MAP;
    private Map<Integer, String> positionCodiceEnteMap;
    private FragmentAdapter fragmentAdapter;

    private enum Mode {
        APPALTI,
        SOLDI_PUBBLICI,
        COMBINE,
        MULTIPLE_ELEMENTS;

        public static Mode ofIntent(Intent i) {
            if (i.hasExtra(LIST_APPALTI) && i.hasExtra(LIST_SOLDIPUBBLICI)) return COMBINE;
            if (i.hasExtra(LIST_APPALTI)) return APPALTI;
            if (i.hasExtra(LIST_SOLDIPUBBLICI)) return SOLDI_PUBBLICI;
            return MULTIPLE_ELEMENTS; //throw new UnexpectedException("Unknown intent labels would lead to unsupported mode");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Mode mode = Mode.ofIntent(intent);

        if (mode == Mode.APPALTI || mode == Mode.SOLDI_PUBBLICI || mode == Mode.COMBINE) {
            setContentView(R.layout.activity_ai_result);
        } else {
            setContentView(R.layout.fragment_layout);
        }

        AILayoutSetter aiLayoutSetter = new AILayoutSetter(this, getCurrentFocus(), true);

        switch (mode) {
            case APPALTI: {
                aiLayoutSetter.manageAppaltiCase((List<AppaltiParser.Data>) intent.getSerializableExtra(LIST_APPALTI));
                break;
            }
            case SOLDI_PUBBLICI: {
                aiLayoutSetter.manageSoldiPubbliciCase((List<SoldipubbliciParser.Data>) intent.getSerializableExtra(LIST_SOLDIPUBBLICI), "2016");
                break;
            }
            case COMBINE: {
                aiLayoutSetter.manageCombineCase((List<SoldipubbliciParser.Data>) intent.getSerializableExtra(LIST_SOLDIPUBBLICI),
                        (List<AppaltiParser.Data>) intent.getSerializableExtra(LIST_APPALTI), "2016");
                break;
            }
            case MULTIPLE_ELEMENTS: {
                if (!((codiceEnteExpenditureMap == null || codiceEnteExpenditureMap.isEmpty()) &&
                        (codiceEnteTendersMap == null || codiceEnteTendersMap.isEmpty()))) {
                    manageMultipleElements(intent);
                }
            }
            default: {
                Log.e(TAG, "Unknown mode");
            }
        }

    }

    //Multiple Items stuff

    public static void setCodiceEnteExpenditureMap(Map<String, List<SoldipubbliciParser.Data>> values) {
        codiceEnteExpenditureMap = new HashMap<>(values);
    }

    public static void setCodiceEnteTendersMap(Map<String, List<AppaltiParser.Data>> values) {
        codiceEnteTendersMap = new HashMap<>(values);
    }

    public static Map<String, List<SoldipubbliciParser.Data>> getCodiceEnteExpenditureMap() {
        return codiceEnteExpenditureMap;
    }

    public static Map<String, List<AppaltiParser.Data>> getCodiceEnteTendersMap() {
        return codiceEnteTendersMap;
    }

    public Map getPositionCodiceEnteMap() {
        return positionCodiceEnteMap;
    }

    private void manageMultipleElements(Intent intent) {
        TabLayout tabLayout = setTabLayout(intent);
        setViewPager(tabLayout);
    }

    private TabLayout setTabLayout(Intent intent) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Serializable sl = intent.getSerializableExtra(ABSTRACT_ITEMS);
        List<AbstractItem> abstractItems = (List<AbstractItem>) sl;

        if (abstractItems.size() > 2)
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        positionCodiceEnteMap = new HashMap<>();

        int i = 0;

        for (AbstractItem abstractItem : abstractItems) {
            tabLayout.addTab(tabLayout.newTab().setText(abstractItem.getTitle()), i);
            positionCodiceEnteMap.put(i, abstractItem.getId());
            i++;
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        return tabLayout;
    }

    private void setViewPager(TabLayout tabLayout) {
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
}
