package it.unive.dais.cevid.aac.fragment;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.AIResultActivity;
import it.unive.dais.cevid.aac.util.URALayoutSetter;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class TabFragment extends Fragment {
    private static final String TAG = "AITabFragment";
    private static final String SOLDIPUBBLICI_MODE = "SOLDI_PUBBLICI";
    private static final String APPALTI_MODE = "APPALTI";
    private static final String COMBINE_MODE = "COMBINE";

    private List<SoldipubbliciParser.Data> soldiPubbliciList;
    private List<AppaltiParser.Data> appaltiList;
    private View view;

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
            soldiPubbliciList = (List<SoldipubbliciParser.Data>) savedInstanceState.getSerializable(AIResultActivity.LIST_SOLDIPUBBLICI);
            appaltiList = (List<AppaltiParser.Data>) savedInstanceState.getSerializable(AIResultActivity.LIST_APPALTI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(AIResultActivity.LIST_SOLDIPUBBLICI, (Serializable) soldiPubbliciList);
        outState.putString(AIResultActivity.LIST_APPALTI, String.valueOf(appaltiList));
    }

    public void onCreate(Bundle fragmentBundle) {
        super.onCreate(fragmentBundle);
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Serializable es = bundle.getSerializable(AIResultActivity.LIST_SOLDIPUBBLICI);
            Serializable ts = bundle.getSerializable(AIResultActivity.LIST_APPALTI);

            soldiPubbliciList = new ArrayList<>();
            appaltiList = new ArrayList<>();

            soldiPubbliciList.addAll((List<SoldipubbliciParser.Data>) es);
            appaltiList.addAll((List<AppaltiParser.Data>) ts);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_ai_result, container, false);

        Mode mode = Mode.getMode(soldiPubbliciList, appaltiList);

        URALayoutSetter uraLayoutSetter = new URALayoutSetter((Activity) inflater.getContext(), view, false);

        if (mode == Mode.SOLDI_PUBBLICI) {
            uraLayoutSetter.manageSoldiPubbliciCase(soldiPubbliciList);
        }
        if (mode == Mode.APPALTI) {
            uraLayoutSetter.manageAppaltiCase(appaltiList);
        }
        if (mode == Mode.COMBINE) {
            uraLayoutSetter.manageCombineCase(soldiPubbliciList, appaltiList);
        } else throw new UnexpectedException(TAG);

        return view;
    }
}
