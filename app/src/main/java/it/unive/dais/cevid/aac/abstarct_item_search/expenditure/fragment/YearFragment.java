package it.unive.dais.cevid.aac.abstarct_item_search.expenditure.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.abstarct_item_search.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.abstarct_item_search.util.AILayoutSetter;
import it.unive.dais.cevid.aac.abstarct_item_search.expenditure.activities.AIExpenditureActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * A simple {@link Fragment} subclass.
 */
public class YearFragment extends Fragment {
    public static final String YEAR = "YEAR";
    public static final String CAPITE = "CAPITE";
    private String year;
    private int capite;
    private AILayoutSetter aiLayoutSetter;
    private SoldiPubbliciAdapter soldiPubbliciAdapter;

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            year = bundle.getString(YEAR);
            capite = bundle.getInt(CAPITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_year, container, false);

        aiLayoutSetter = new AILayoutSetter((Activity) inflater.getContext(), view, false);
        soldiPubbliciAdapter = aiLayoutSetter.manageSoldiPubbliciCase(AIExpenditureActivity.getSpeseEnte(), year, capite);

        return view;
    }

    public void onQueryTextChange (String query) {
        List<SoldipubbliciParser.Data> expenditureList = new ArrayList<>(AIExpenditureActivity.getSpeseEnte());

        if (query.matches("[0-9]+"))
            DataManipulation.filterByWords(expenditureList, query.split(" "), Soldipubblici_getCode, false);
        else
            DataManipulation.filterByWords(expenditureList, query.split(" "), Soldipubblici_getText, false);

        soldiPubbliciAdapter.setFilter(expenditureList);
    }

    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getText = x -> x.descrizione_codice;

    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getCode = x -> x.codice_siope;

}
