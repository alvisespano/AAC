package it.unive.dais.cevid.aac.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unive.dais.cevid.aac.R;

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
        View view = inflater.inflate(R.layout.activity_confronto, container, false);

        return view;
    }
}
