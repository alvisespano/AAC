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
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

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

        //prova soldipubblici

        SoldipubbliciParser.Data sppd = new SoldipubbliciParser.Data();

        sppd.descrizione_codice = "desc";
        sppd.codice_siope = "13";
        sppd.anno = "2018";
        sppd.codice_gestionale = "AAA";
        sppd.idtable = "true";
        sppd.data_di_fine_validita = "2019";
        sppd.cod_ente = "codente";
        sppd.imp_uscite_att = "1.0";
        sppd.periodo = "anni";
        sppd.ricerca = "ricerca";
        sppd.descrizione_ente = "descente";
        sppd.importo_2017 = "10";
        sppd.importo_2016 = "10";
        sppd.importo_2015 = "10";
        sppd.importo_2014 = "10";
        sppd.importo_2013 = "10";

        List<SoldipubbliciParser.Data> l1 = new ArrayList<SoldipubbliciParser.Data>();

        l1.add(sppd);

        for (SoldipubbliciParser.Data x : l1)
            eel.add(new EntitieExpenditure(x, "2016"));

        //fine prova soldipubblici

        //prova appalti

        AppaltiParser.Data apd = new AppaltiParser.Data();

        apd.cig = "14";
        apd.proponente = "proponente";
        apd.codiceFiscaleProp = "cfprop";
        apd.oggetto = "ogg";
        apd.sceltac = "sceltac";
        apd.aggiudicatario = "agg";
        apd.codiceFiscaleAgg = "cfagg";
        apd.importo = "10";
        apd.importoSommeLiquidate = "10";
        apd.dataInizio = "inizio";
        apd.dataFine = "fine";

        List<AppaltiParser.Data> l2 = new ArrayList<>();
        l2.add(apd);

        //fine prova appalti

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(eel, "1");
        recyclerViewExpenditure.setAdapter(soldiPubbliciAdapter);
        recyclerViewExpenditure.setVisibility(View.VISIBLE);

        AppaltiAdapter appaltiAdapter = new AppaltiAdapter(l2, 1.0);
        recyclerViewTenders.setAdapter(appaltiAdapter);
        recyclerViewTenders.setVisibility(View.VISIBLE);

        LinearLayout linearLayoutTenders = (LinearLayout) view.findViewById(R.id.sum_tenders);
        linearLayoutTenders.setVisibility(View.VISIBLE);

        TextView tv = (TextView) view.findViewById(R.id.sum_exp);
        tv.setText(String.format(getString(R.string.university_result_appalti_format), 1.0, 1.0));

        return view;
    }

    /*public static class Data implements Serializable {
        public String descrizione_codice;
        public String codice_siope;
        public String descrizione_ente;
        public String ricerca;
        public String idtable;
        public String cod_ente;
        public String anno;
        public String periodo;
        public String codice_gestionale;
        public String imp_uscite_att;
        public String data_di_fine_validita;
        public String importo_2013;
        public String importo_2014;
        public String importo_2015;
        public String importo_2016;
        public String importo_2017;

    }*/

    /*public static class Data implements Serializable{
        public String cig;
        public String proponente;
        public String codiceFiscaleProp;
        public String oggetto;
        public String sceltac;
        public String aggiudicatario;
        public String codiceFiscaleAgg;
        public String importo;
        public String importoSommeLiquidate;
        public String dataInizio;
        public String dataFine;
    }*/

}
