package it.unive.dais.cevid.aac.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.util.Company;

/**
 * Created by Francesco on 09/01/2018.
 */

public class UniversityTenderAdapter extends RecyclerView.Adapter<UniversityTenderAdapter.Item> {

    List<Company.Tender> items;

    public UniversityTenderAdapter(List<Company.Tender> data){
        this.items = data;
    }
    @Override
    public Item onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_university_tender, parent, false);
        return new Item(itemView);
    }

    @Override
    public void onBindViewHolder(Item holder, int position) {
        Company.Tender tender = items.get(position);
        holder.cig.setText(tender.cig);
        holder.cost.setText(tender.importo);
        holder.paid.setText(tender.liquidato);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Item extends RecyclerView.ViewHolder{
        TextView cig,cost,paid;
        public Item(View itemView){
            super(itemView);
            this.cig = (TextView) itemView.findViewById(R.id.university_tender_cig);
            this.cost = (TextView) itemView.findViewById(R.id.university_tender_cost);
            this.paid = (TextView) itemView.findViewById(R.id.university_tender_paid);
        }
    }
}
