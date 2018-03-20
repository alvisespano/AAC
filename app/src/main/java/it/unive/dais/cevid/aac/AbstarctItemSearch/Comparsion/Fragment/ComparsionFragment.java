package it.unive.dais.cevid.aac.AbstarctItemSearch.Comparsion.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.AbstarctItemSearch.Comparsion.Activities.AIComparsionResultActivity;
import it.unive.dais.cevid.aac.AbstarctItemSearch.util.AILayoutSetter;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class ComparsionFragment extends Fragment {
    private static final String TAG = "AITabFragment";
    public static final String CAPITE = "CAPITE";

    private List<SoldipubbliciParser.Data> soldiPubbliciList;
    private List<AppaltiParser.Data> appaltiList;
    private View view;
    private int capite;

    private enum  Mode {
        SOLDI_PUBBLICI,
        APPALTI,
        COMBINE;

        public static Mode getMode(List<SoldipubbliciParser.Data> soldiPubbliciList, List<AppaltiParser.Data> appaltiList) {
            if ((soldiPubbliciList != null && !soldiPubbliciList.isEmpty()) && (appaltiList == null || appaltiList.isEmpty()))
                return SOLDI_PUBBLICI;
            else if ((appaltiList != null && !appaltiList.isEmpty()) && soldiPubbliciList == null || soldiPubbliciList.isEmpty())
                return APPALTI;
            else
                return COMBINE;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            soldiPubbliciList = (List<SoldipubbliciParser.Data>) savedInstanceState.getSerializable(AIComparsionResultActivity.LIST_SOLDIPUBBLICI);
            appaltiList = (List<AppaltiParser.Data>) savedInstanceState.getSerializable(AIComparsionResultActivity.LIST_APPALTI);
            capite = savedInstanceState.getInt(CAPITE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(AIComparsionResultActivity.LIST_SOLDIPUBBLICI, (Serializable) soldiPubbliciList);
        outState.putSerializable(AIComparsionResultActivity.LIST_APPALTI, (Serializable) appaltiList);
    }

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Serializable es = bundle.getSerializable(AIComparsionResultActivity.LIST_SOLDIPUBBLICI);
            Serializable ts = bundle.getSerializable(AIComparsionResultActivity.LIST_APPALTI);

            soldiPubbliciList = new ArrayList<>();
            appaltiList = new ArrayList<>();

            soldiPubbliciList.addAll((List<SoldipubbliciParser.Data>) es);
            appaltiList.addAll((List<AppaltiParser.Data>) ts);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_ai_comparsion_result, container, false);

        Mode mode = Mode.getMode(soldiPubbliciList, appaltiList);

        AILayoutSetter aiLayoutSetter = new AILayoutSetter((Activity) inflater.getContext(), view, false);

        if (mode == Mode.SOLDI_PUBBLICI) {
            aiLayoutSetter.manageSoldiPubbliciCase(soldiPubbliciList, "2016", capite);
        }
        if (mode == Mode.APPALTI) {
            aiLayoutSetter.manageAppaltiCase(appaltiList);
        }
        if (mode == Mode.COMBINE) {
            aiLayoutSetter.manageCombineCase(soldiPubbliciList, appaltiList, "2016", capite);
        } else throw new UnexpectedException(TAG);

        return view;
    }
}