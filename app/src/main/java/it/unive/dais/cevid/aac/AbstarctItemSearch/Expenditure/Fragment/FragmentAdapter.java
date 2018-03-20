package it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
;import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gianmarcocallegher on 15/11/17.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private List<Fragment> registeredFragments = new ArrayList<>();
    private int capite;

    public FragmentAdapter(FragmentManager fm, int NumOfTabs, int capite) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.capite = capite;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return setReturnFragment("2017");
            case 1:
                return setReturnFragment("2016");
            case 2:
                return setReturnFragment("2015");
            case 3:
                return setReturnFragment("2014");
            case 4:
                return setReturnFragment("2013");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    private YearFragment setReturnFragment(String year){
        Bundle bundle = new Bundle();
        YearFragment yearFragment = new YearFragment();

        bundle.putString(YearFragment.YEAR, year);
        bundle.putInt(YearFragment.CAPITE, capite);

        yearFragment.setArguments(bundle);

        return yearFragment;
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.add(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }*/
}