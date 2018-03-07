package it.unive.dais.cevid.aac.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.component.AIResultActivity;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private AIResultActivity parentActivity;
    private List<Fragment> registeredFragments = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this.numOfTabs = numOfTabs;
        parentActivity = (AIResultActivity) context;
    }

    @Override
    public Fragment getItem(int position) {
        ComparsionFragment comparsionFragment = new ComparsionFragment();
        Bundle bundle = new Bundle();
        String codiceEnte = (String) parentActivity.getPositionCodiceEnteMap().get(position);

        bundle.putSerializable(AIResultActivity.LIST_SOLDIPUBBLICI, (Serializable) parentActivity.getCodiceEnteExpenditureMap().get(codiceEnte));
        bundle.putSerializable(AIResultActivity.LIST_APPALTI, (Serializable) parentActivity.getCodiceEnteTendersMap().get(codiceEnte));

        comparsionFragment.setArguments(bundle);

        return comparsionFragment;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.add(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }
}
