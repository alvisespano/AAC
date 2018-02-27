package it.unive.dais.cevid.aac.fragment;

import android.app.Activity;
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
import it.unive.dais.cevid.aac.component.UniversityResultActivity;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.aac.util.URALayoutSetter;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

/**
 * Created by gianmarcocallegher on 19/02/2018.
 */

public class TabFragment extends Fragment {
    private static final String TAG = "URATabFragment";

    private static List<SoldipubbliciParser.Data> soldiPubbliciList;
    private static List<AppaltiParser.Data> appaltiList;
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

    private enum Mode {
        APPALTI,
        SOLDI_PUBBLICI,
        COMBINE;

        public static TabFragment.Mode getMode() {
            if ((soldiPubbliciList != null && !soldiPubbliciList.isEmpty()) &&
                    appaltiList.isEmpty() || appaltiList == null)
                return SOLDI_PUBBLICI;
            if ((appaltiList != null && !appaltiList.isEmpty()) &&
                    soldiPubbliciList.isEmpty() || soldiPubbliciList == null)
                return APPALTI;
            return COMBINE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_university_result, container, false);

        URALayoutSetter uraLayoutSetter = new URALayoutSetter((Activity) inflater.getContext(), view, false);
        if (Mode.getMode() == Mode.SOLDI_PUBBLICI) {
            uraLayoutSetter.manageSoldiPubbliciCase(soldiPubbliciList);
        }
        if (Mode.getMode() == Mode.APPALTI) {
            uraLayoutSetter.manageAppaltiCase(appaltiList);
        }
        if (Mode.getMode() == Mode.COMBINE) {
            uraLayoutSetter.manageCombineCase(soldiPubbliciList, appaltiList);
        } else throw new UnexpectedException(TAG);

        return view;
    }
}
