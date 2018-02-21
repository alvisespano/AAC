package it.unive.dais.cevid.aac.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;

import it.unive.dais.cevid.aac.component.ConfrontoActivity;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private ConfrontoActivity parentActivity;

    public FragmentAdapter(FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this.numOfTabs = numOfTabs;
        parentActivity = (ConfrontoActivity) context;
    }

    @Override
    public Fragment getItem(int position) {

        TabFragment tab = new TabFragment();
        Bundle bundle = new Bundle();

        for (Object i : parentActivity.getPositionCodiceEnteMap().keySet()) {
            if (position == (Integer) i) {
                bundle.putSerializable("Expenditures", (Serializable) parentActivity.getCodiceEnteExpenditureMap().
                        get(parentActivity.getPositionCodiceEnteMap().get(position)));
                bundle.putSerializable("Tenders", (Serializable) parentActivity.getCodiceEnteTendersMap().
                        get(parentActivity.getPositionCodiceEnteMap().get(position)));
                tab.setArguments(bundle);
            }

        }
        return tab;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
