package it.unive.dais.cevid.aac.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.util.Company;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;

/**
 * Created by Francesco on 08/01/2018.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.Item> {


    private List<Company> dataList;

    public CompanyAdapter(List<Company> dataList) {
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
        holder.wins.setText(""+item.getSize());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        public TextView name,iva,wins;
        public Item(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.company_name);
            this.iva = (TextView) itemView.findViewById(R.id.company_iva);
            this.wins = (TextView) itemView.findViewById(R.id.company_wins);
        }
    }
}
