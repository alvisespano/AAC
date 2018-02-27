package it.unive.dais.cevid.aac.util;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;

/**
 * Created by gianmarcocallegher on 27/02/2018.
 */

public class URALayoutSetter {
    private boolean singleElement;
    private Activity activity;
    private View view;

    public URALayoutSetter(Activity activity, View view, boolean singleElement) {
        this.activity = activity;
        this.view = view;
        this.singleElement = singleElement;
    }

    public void manageAppaltiCase(List<AppaltiParser.Data> appaltiList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_tenders);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: calcolare la media ANCHE DEGLI ALTRI ENTI (università, in questo caso) per lo stesso tipo di fornitura
        double sum = DataManipulation.sumBy(appaltiList, x -> Double.parseDouble(x.importo));
        double avg = sum / appaltiList.size();

        AppaltiAdapter appaltiAdapter = new AppaltiAdapter(appaltiList, avg);
        recyclerView.setAdapter(appaltiAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.sum_tenders);
        linearLayout.setVisibility(View.VISIBLE);

        TextView tv = (TextView) findViewById(R.id.sum_exp);
        tv.setText(String.format(activity.getString(R.string.university_result_appalti_format), sum, avg));
    }

    public void manageSoldiPubbliciCase(List<SoldipubbliciParser.Data> soldiPubbliciList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_exp);

        recyclerView.setLayoutManager(layoutManager);

        List<EntitieExpenditure> entitieExpenditureList = new ArrayList<>();

        for (SoldipubbliciParser.Data x : soldiPubbliciList)
            entitieExpenditureList.add(new EntitieExpenditure(x, "2016"));

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(entitieExpenditureList, "1");
        recyclerView.setAdapter(soldiPubbliciAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void manageCombineCase(List<SoldipubbliciParser.Data> soldiPubbliciList, List<AppaltiParser.Data> appaltiList) {
        manageSoldiPubbliciCase(soldiPubbliciList);
        manageAppaltiCase(appaltiList);
    }

    private View findViewById(int resource) {
        if (singleElement)
            return activity.findViewById(resource);
        else
            return view.findViewById(resource);
    }
}
