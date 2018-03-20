package it.unive.dais.cevid.aac.AbstarctItemSearch.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.Activities.AITendersDetailsActivity;
import it.unive.dais.cevid.aac.AbstarctItemSearch.CompaniesTenders.utils.RecyclerItemClickListener;
import it.unive.dais.cevid.aac.AbstarctItemSearch.Expenditure.Activities.AIExpenditureDetailsActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.AbstarctItemSearch.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.AbstarctItemSearch.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;

/**
 * Created by gianmarcocallegher on 27/02/2018.
 */

public class AILayoutSetter {
    private boolean singleElement;
    private Activity activity;
    private View view;
    private RecyclerItemClickListener recyclerItemClickListener;

    public AILayoutSetter(Activity activity, View view, boolean singleElement) {
        this.activity = activity;
        this.view = view;
        this.singleElement = singleElement;
    }

    public AppaltiAdapter manageAppaltiCase(List<AppaltiParser.Data> appaltiList) {
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
        tv.setText(String.format(activity.getString(R.string.ai_result_appalti_format), sum, avg));

        return appaltiAdapter;
    }

    public SoldiPubbliciAdapter manageSoldiPubbliciCase(List<SoldipubbliciParser.Data> soldiPubbliciList, String year, int capite) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_exp);
        recyclerView.setLayoutManager(layoutManager);

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(soldiPubbliciList, year, capite);
        recyclerView.setAdapter(soldiPubbliciAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.removeOnItemTouchListener(recyclerItemClickListener);

        recyclerItemClickListener = new RecyclerItemClickListener(activity.getBaseContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(activity, AIExpenditureDetailsActivity.class);
                intent.putExtra(AIExpenditureDetailsActivity.DATA, soldiPubbliciList.get(position));
                intent.putExtra(AIExpenditureDetailsActivity.YEAR, year);
                intent.putExtra(AIExpenditureDetailsActivity.CAPITE, capite);

                activity.startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        });

        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        return soldiPubbliciAdapter;
    }

    public void manageCombineCase(List<SoldipubbliciParser.Data> soldiPubbliciList, List<AppaltiParser.Data> appaltiList, String year, int capite) {
        manageSoldiPubbliciCase(soldiPubbliciList, year, capite);
        manageAppaltiCase(appaltiList);
    }

    private View findViewById(int resource) {
        if (singleElement)
            return activity.findViewById(resource);
        else
            return view.findViewById(resource);
    }
}
