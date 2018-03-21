package it.unive.dais.cevid.aac.abstarctItemSearch.comparison.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.abstarctItemSearch.comparison.activities.AIComparisonResultActivity;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private AIComparisonResultActivity parentActivity;
    private List<Fragment> registeredFragments = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this.numOfTabs = numOfTabs;
        parentActivity = (AIComparisonResultActivity) context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position != 0) {
            ComparsionFragment comparsionFragment = new ComparsionFragment();
            Bundle bundle = new Bundle();
            String codiceEnte = (String) parentActivity.getPositionCodiceEnteMap().get(position);

            bundle.putSerializable(AIComparisonResultActivity.LIST_SOLDIPUBBLICI, (Serializable) parentActivity.getCodiceEnteExpenditureMap().get(codiceEnte));
            bundle.putSerializable(AIComparisonResultActivity.LIST_APPALTI, (Serializable) parentActivity.getCodiceEnteTendersMap().get(codiceEnte));
            bundle.putInt(ComparsionFragment.CAPITE, (Integer) parentActivity.getPositionCapiteMap().get(position));

            comparsionFragment.setArguments(bundle);

            return comparsionFragment;
        }
        else {
            return new LeadboardFragment();
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.add(fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(object);
        super.destroyItem(container, position, object);
    }
}
