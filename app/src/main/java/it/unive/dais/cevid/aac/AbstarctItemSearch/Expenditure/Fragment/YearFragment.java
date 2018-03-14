package it.unive.dais.cevid.aac.AbstarctItem.Expenditure.Fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unive.dais.cevid.aac.AbstarctItem.util.AILayoutSetter;
import it.unive.dais.cevid.aac.AbstarctItem.Expenditure.Activities.AIExpenditureActivity;
import it.unive.dais.cevid.aac.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YearFragment extends Fragment {
    public static final String YEAR = "YEAR";
    private String year;

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            year = (String) bundle.getSerializable(YEAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);
        AILayoutSetter aiLayoutSetter = new AILayoutSetter((Activity) inflater.getContext(), view, false);
        aiLayoutSetter.manageSoldiPubbliciCase(AIExpenditureActivity.getSpeseEnte(), year);
        return view;
    }
}
