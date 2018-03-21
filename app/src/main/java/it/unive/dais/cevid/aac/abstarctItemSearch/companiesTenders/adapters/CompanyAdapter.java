package it.unive.dais.cevid.aac.abstarctItemSearch.companiesTenders.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.abstarctItemSearch.companiesTenders.utils.Company;
import it.unive.dais.cevid.aac.R;

/**
 * Created by Francesco on 08/01/2018.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.Item> {

    private static final int AGGIUDICATARI_THRESHOLD = 10;
    private static final double DIRETTE_PERCENT_THRESHOLD = 0.20;
    private List<Company> dataList;
    private Context ctx;

    public CompanyAdapter(Context ctx, List<Company> dataList) {
        this.ctx = ctx;
        this.dataList = dataList;
    }

    @Override
    public Item onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_company, parent, false);
        return new Item(itemView);
    }

    @Override
    public void onBindViewHolder(Item holder, int position) {
        Company item = dataList.get(position);

        holder.name.setText(item.name);
        holder.iva.setText(item.fiscalCode);
        int dirette = 0;
        for (Company.Tender t : item.tenders) {
            if (t.sceltac.contains("AFFIDAMENTO DIRETTO")) dirette++;
        }
        final int aggiudicatari = item.getSize();
        holder.wins.setText(String.format(ctx.getString(R.string.company_adapter_wins_format), aggiudicatari, dirette));
        if (aggiudicatari > AGGIUDICATARI_THRESHOLD) {
            if (dirette > aggiudicatari * DIRETTE_PERCENT_THRESHOLD) {
                holder.wins.setBackgroundColor(Color.RED);
            }
            else {
                holder.wins.setBackgroundColor(Color.YELLOW);
            }
        }
        else {
            holder.wins.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        public TextView name, iva, wins, directWins;

        public Item(View itemView) {
            super(itemView);
            this.iva = (TextView) itemView.findViewById(R.id.company_iva);
            this.name = (TextView) itemView.findViewById(R.id.company_name);
            this.wins = (TextView) itemView.findViewById(R.id.company_wins);
        }
    }
}
