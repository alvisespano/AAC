package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;


public class UniversityResultActivity extends AppCompatActivity {
    private static final String TAG = "UniversityResultActivity";

    public static final String LIST_APPALTI = "LIST_APPALTI";
    public static final String LIST_SOLDIPUBBLICI = "LIST_SOLDIPUBBLICI";

    private enum Mode {
        APPALTI,
        SOLDI_PUBBLICI,
        COMBINE,
        UNKNOWN;

        public static Mode ofIntent(Intent i) {
            if (i.hasExtra(LIST_APPALTI) && i.hasExtra(LIST_SOLDIPUBBLICI)) return COMBINE;
            if (i.hasExtra(LIST_APPALTI)) return APPALTI;
            if (i.hasExtra(LIST_SOLDIPUBBLICI)) return SOLDI_PUBBLICI;
            return UNKNOWN; //throw new UnexpectedException("Unknown intent labels would lead to unsupported mode");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_result);

        Intent intent = getIntent();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        switch (Mode.ofIntent(intent)) {
            case APPALTI: {
                manageAppaltiCase(layoutManager, intent);
                break;
            }
            case SOLDI_PUBBLICI: {
                manageSoldiPubbliciCase(layoutManager, intent);
                break;
            }
            case COMBINE:{
                manageCombineCase(layoutManager, intent);
                break;
            }
            default: {
                Log.e("URA", "unknown mode");
            }
        }
    }

    private void manageAppaltiCase(RecyclerView.LayoutManager layoutManager, Intent intent) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_tenders);
        recyclerView.setLayoutManager(layoutManager);

        Serializable appaltiSerializableList = intent.getSerializableExtra(LIST_APPALTI);
        List<AppaltiParser.Data> appaltiList = (List<AppaltiParser.Data>) appaltiSerializableList;

        // TODO: calcolare la media ANCHE DEGLI ALTRI ENTI (università, in questo caso) per lo stesso tipo di fornitura
        double sum = DataManipulation.sumBy(appaltiList, x -> Double.parseDouble(x.importo));
        double avg = sum / appaltiList.size();

        AppaltiAdapter appaltiAdapter = new AppaltiAdapter(appaltiList, avg);
        recyclerView.setAdapter(appaltiAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.sum_tenders);
        linearLayout.setVisibility(View.VISIBLE);

        TextView tv = (TextView) findViewById(R.id.sum_exp);
        tv.setText(String.format(getString(R.string.university_result_appalti_format), sum, avg));
    }

    private void manageSoldiPubbliciCase(RecyclerView.LayoutManager layoutManager, Intent intent) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_exp);
        recyclerView.setLayoutManager(layoutManager);

        Serializable soldiPubbliciSerializableList = intent.getSerializableExtra(LIST_SOLDIPUBBLICI);
        List<SoldipubbliciParser.Data> soldiPubbliciList = (List<SoldipubbliciParser.Data>) soldiPubbliciSerializableList;

        List<EntitieExpenditure> entitieExpenditureListl = new ArrayList<>();

        for (SoldipubbliciParser.Data x : soldiPubbliciList)
            entitieExpenditureListl.add(new EntitieExpenditure(x, "2016"));

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(entitieExpenditureListl, "1");
        recyclerView.setAdapter(soldiPubbliciAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void manageCombineCase (RecyclerView.LayoutManager soldiPubbliciLayoutManager, Intent intent) {
        RecyclerView.LayoutManager appaltiLayoutManager = new LinearLayoutManager(this);

        manageSoldiPubbliciCase(soldiPubbliciLayoutManager, intent);
        manageAppaltiCase(appaltiLayoutManager, intent);
    }
}
