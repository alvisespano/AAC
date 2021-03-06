package it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.abstarct_item_search.companies_tenders.utils.Company;
import it.unive.dais.cevid.aac.R;

/**
 * Created by Francesco on 09/01/2018.
 */

public class AITenderAdapter extends RecyclerView.Adapter<AITenderAdapter.Item> {

    List<Company.Tender> items;

    public AITenderAdapter(List<Company.Tender> data){
        this.items = data;
    }
    @Override
    public Item onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.element_ai_tender, parent, false);
        return new Item(itemView);
    }

    @Override
    public void onBindViewHolder(Item holder, int position) {
        Company.Tender tender = items.get(position);
        holder.cig.setText(tender.cig);
        holder.description.setText(tender.description);
        holder.cost.setText(tender.importo);
        holder.paid.setText(tender.liquidato);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Item extends RecyclerView.ViewHolder{
        TextView cig, description, cost, paid;
        public Item(View itemView){
            super(itemView);
            this.cig = (TextView) itemView.findViewById(R.id.ai_tender_cig);
            this.description = (TextView) itemView.findViewById(R.id.ai_tender_description);
            this.cost = (TextView) itemView.findViewById(R.id.ai_tender_cost);
            this.paid = (TextView) itemView.findViewById(R.id.ai_tender_paid);
        }
    }
}
