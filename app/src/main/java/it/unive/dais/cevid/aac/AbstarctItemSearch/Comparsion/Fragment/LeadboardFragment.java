package it.unive.dais.cevid.aac.AbstarctItemSearch.Comparsion.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.unive.dais.cevid.aac.AbstarctItemSearch.Comparsion.Activities.AIComparsionResultActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * Created by gianmarcocallegher on 19/03/2018.
 */

public class LeadboardFragment extends Fragment {
    private static final String TAG = "LeadboardFragment";
    private static List differenceExpList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ai_comparsion_result, container, false);

        setUpDifferenceExpList();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.leadboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        LeadboardAdapter leadboardAdapter = new LeadboardAdapter(differenceExpList);
        recyclerView.setAdapter(leadboardAdapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpDifferenceExpList() {
        differenceExpList = new ArrayList<DifferenceElement>();

        for (AbstractItem abstractItem : AIComparsionResultActivity.getAbstractItems()) {
            Double sumExpenditures = (AIComparsionResultActivity.getCodiceEnteExpenditureMap() != Collections.EMPTY_MAP)?
                    sumExpenditure(AIComparsionResultActivity.getCodiceEnteExpenditureMap().get(abstractItem.getId()))
                    : 0.0;
            Double sumTenders = (AIComparsionResultActivity.getCodiceEnteTendersMap() != Collections.EMPTY_MAP)?
                    sumTenders(AIComparsionResultActivity.getCodiceEnteTendersMap().get(abstractItem.getId()))
                    : 0.0;

            differenceExpList.add(new DifferenceElement(abstractItem.getDescription(), sumExpenditures, sumTenders));

            differenceExpList.sort(new DifferenceElementComparator());
        }
    }

    private Double sumExpenditure(List<SoldipubbliciParser.Data> expendituresList) {
        Double sum = 0.0;

        for (SoldipubbliciParser.Data d : expendituresList) {
            sum += Double.valueOf(d.importo_2016);
        }

        return sum;
    }

    private Double sumTenders(List<AppaltiParser.Data> tendersList) {
        Double sum = 0.0;

        for (AppaltiParser.Data d : tendersList) {
            sum += Double.valueOf(d.importo);
        }

        return sum;
    }

    public class DifferenceElement {
        String description;
        Double sumExpenditures, sumTenders, difference;

        public DifferenceElement(String description, Double sumExpenditures, Double sumTenders) {
            this.description = description;
            this.sumExpenditures = sumExpenditures;
            this.sumTenders = sumTenders;
            this.difference = sumTenders - sumExpenditures;
        }
    }

    private class DifferenceElementComparator implements Comparator<DifferenceElement> {

        @Override
        public int compare(DifferenceElement o1, DifferenceElement o2) {
            if (o1.difference < o2.difference)
                return -1;
            if (o1.difference < o2.difference)
                return 0;
            else
                return 1;
        }
    }
}
