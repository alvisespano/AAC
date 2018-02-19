package it.unive.dais.cevid.aac.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class TabFragment extends Fragment {
    String name;

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_university_result, container, false);

        RecyclerView.LayoutManager expendituresLayoutManager = new LinearLayoutManager(inflater.getContext());
        RecyclerView.LayoutManager tendersLayoutManager = new LinearLayoutManager(inflater.getContext());

        RecyclerView recyclerViewExpenditure = (RecyclerView) view.findViewById(R.id.list_exp);
        RecyclerView recyclerViewTenders = (RecyclerView) view.findViewById(R.id.list_tenders);

        recyclerViewExpenditure.setLayoutManager(expendituresLayoutManager);
        recyclerViewTenders.setLayoutManager(tendersLayoutManager);

        List eel = new ArrayList<EntitieExpenditure>();

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(eel, "1");
        recyclerViewExpenditure.setAdapter(soldiPubbliciAdapter);
        recyclerViewExpenditure.setVisibility(View.VISIBLE);

        AppaltiAdapter appaltiAdapter = new AppaltiAdapter(new ArrayList<AppaltiParser.Data>(), 1.0);
        recyclerViewTenders.setAdapter(appaltiAdapter);
        recyclerViewTenders.setVisibility(View.VISIBLE);

        LinearLayout linearLayoutTenders = (LinearLayout) view.findViewById(R.id.sum_tenders);
        linearLayoutTenders.setVisibility(View.VISIBLE);

        TextView tv = (TextView) view.findViewById(R.id.sum_exp);
        tv.setText(String.format(getString(R.string.university_result_appalti_format), 1.0, 1.0));

        return view;
    }
}
