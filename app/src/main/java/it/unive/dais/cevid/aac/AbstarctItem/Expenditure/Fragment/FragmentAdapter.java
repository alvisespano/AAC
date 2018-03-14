package it.unive.dais.cevid.aac.AbstarctItem.Expenditure.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
;

/**
 * Created by gianmarcocallegher on 15/11/17.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public FragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
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
        yearFragment.setArguments(bundle);

        return yearFragment;
    }
}