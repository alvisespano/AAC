package it.unive.dais.cevid.aac.abstarct_item_search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * Created by Fonto on 11/09/17.
 */

public class SoldiPubbliciAdapter extends RecyclerView.Adapter<SoldiPubbliciAdapter.SoldiPubbliciItem> {
    private static final String TAG = "SoldiPubbliciAdapter";
    private int capite;
    private List<SoldipubbliciParser.Data> expenditureList;
    private String year;

    public SoldiPubbliciAdapter(List<SoldipubbliciParser.Data> expenditureList, String year, int capite) {
        this.expenditureList = expenditureList;
        this.year = year;
        this.capite = capite;
    }

    @Override
    public SoldiPubbliciItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_public_expenditure, parent, false);
        return new SoldiPubbliciItem(itemView);

    }

    @Override
    public void onBindViewHolder(SoldiPubbliciItem holder, int position) {
        Double importo = Double.parseDouble(getCostFromYear(expenditureList.get(position)));
        holder.voceSpesa.setText(expenditureList.get(position).descrizione_codice);
        holder.importo.setText(String.format("%.2f€", importo));
        holder.procapite.setText(String.format("%.2f€",Double.valueOf(importo) / capite));
    }

    @Override
    public int getItemCount() {
        return expenditureList.size();
    }


    public class SoldiPubbliciItem extends RecyclerView.ViewHolder {
        private TextView voceSpesa, importo, procapite;

        public SoldiPubbliciItem(View itemView) {
            super(itemView);
            voceSpesa = (TextView) itemView.findViewById(R.id.description_exp);
            importo = (TextView) itemView.findViewById(R.id.public_exp);
            procapite = (TextView) itemView.findViewById(R.id.pro_capite);
        }
    }

    public void setFilter (List<SoldipubbliciParser.Data> expenditureFilteredList) {
        expenditureList = expenditureFilteredList;
        notifyDataSetChanged();
    }

    private String getCostFromYear(SoldipubbliciParser.Data expenditure) {
        switch (year) {
            case "2017": return expenditure.importo_2017;
            case "2016": return expenditure.importo_2016;
            case "2015": return expenditure.importo_2015;
            case "2014": return expenditure.importo_2014;
            case "2013": return expenditure.importo_2013;
            default: return null;
        }
    }
}
