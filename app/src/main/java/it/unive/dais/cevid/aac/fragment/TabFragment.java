package it.unive.dais.cevid.aac.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class TabFragment extends Fragment {

    private List<SoldipubbliciParser.Data> soldiPubbliciList;
    private List<AppaltiParser.Data> appaltiList;
    private View view;
    private static String mode;

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Serializable es = bundle.getSerializable("Expenditures");
            Serializable ts = bundle.getSerializable("Tenders");

            soldiPubbliciList = new ArrayList<>();
            appaltiList = new ArrayList<>();

            soldiPubbliciList.addAll((List<SoldipubbliciParser.Data>) es);
            appaltiList.addAll((List<AppaltiParser.Data>) ts);
        }
    }

    private void setMode() {
        if ((soldiPubbliciList != null && !soldiPubbliciList.isEmpty()) &&
                appaltiList.isEmpty() || appaltiList == null)
            mode = "SOLDI_PUBBLICI";
        if ((appaltiList != null && !appaltiList.isEmpty()) &&
                soldiPubbliciList.isEmpty() || soldiPubbliciList == null)
            mode = "APPALTI";
        else
            mode = "COMBINE";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_university_result, container, false);

        if (mode == "SOLDI_PUBBLICI")
            manageSoldiPubbliciCase(inflater);
        if (mode == "APPALTI")
            manageAppaltiCase(inflater);
        else
            manageCombineCase(inflater);

        return view;
    }

    private void manageAppaltiCase(LayoutInflater inflater) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_tenders);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: calcolare la media ANCHE DEGLI ALTRI ENTI (università, in questo caso) per lo stesso tipo di fornitura
        double sum = DataManipulation.sumBy(appaltiList, x -> Double.parseDouble(x.importo));
        double avg = sum / appaltiList.size();

        AppaltiAdapter appaltiAdapter = new AppaltiAdapter(appaltiList, avg);
        recyclerView.setAdapter(appaltiAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.sum_tenders);
        linearLayout.setVisibility(View.VISIBLE);

        TextView tv = (TextView) view.findViewById(R.id.sum_exp);
        tv.setText(String.format(getString(R.string.university_result_appalti_format), sum, avg));
    }

    private void manageSoldiPubbliciCase(LayoutInflater inflater) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_exp);

        recyclerView.setLayoutManager(layoutManager);

        List<EntitieExpenditure> entitieExpenditureList = new ArrayList<>();

        for (SoldipubbliciParser.Data x : soldiPubbliciList)
            entitieExpenditureList.add(new EntitieExpenditure(x, "2016"));

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(entitieExpenditureList, "1");
        recyclerView.setAdapter(soldiPubbliciAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void manageCombineCase(LayoutInflater inflater) {
        manageSoldiPubbliciCase(inflater);
        manageAppaltiCase(inflater);
    }
}
