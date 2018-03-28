package it.unive.dais.cevid.aac.abstarct_item_search.comparison.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.unive.dais.cevid.aac.R;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.CYAN;
import static android.graphics.Color.MAGENTA;

/**
 * Created by gianmarcocallegher on 19/03/2018.
 */

public class LeadboardAdapter extends RecyclerView.Adapter<LeadboardAdapter.LeadboardItem> {
    private static final String TAG = "LeadboardAdapter";
    private List<LeadboardFragment.DifferenceElement> differenceList;
    private int i;

    public LeadboardAdapter(List<LeadboardFragment.DifferenceElement> differenceList) {
        this.differenceList = differenceList;
        i = 1;
    }

    @Override
    public LeadboardItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_leadboard, parent, false);
        return new LeadboardItem(itemView);
    }

    @Override
    public void onBindViewHolder(LeadboardItem holder, int position) {
        holder.description.setText(i + ". " + differenceList.get(position).description);
        holder.sum_expenditures.setText(String.format("%.2f€", differenceList.get(position).sumExpenditures));
        holder.sum_tenders.setText(String.format("%.2f€", differenceList.get(position).sumTenders));
        holder.difference.setText(String.format("%.2f€", differenceList.get(position).difference));

        holder.sum_expenditures.setTextColor(CYAN);
        holder.sum_tenders.setTextColor(MAGENTA);
        holder.difference.setTextColor(BLUE);

        i++;
    }

    @Override
    public int getItemCount() {
        return differenceList.size();
    }

    public class LeadboardItem extends RecyclerView.ViewHolder {
        private TextView description, sum_expenditures, sum_tenders, difference;

        public LeadboardItem(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description_ai);
            sum_expenditures = (TextView) itemView.findViewById(R.id.public_expenditures);
            sum_tenders = (TextView) itemView.findViewById(R.id.tenders_exp);
            difference = (TextView) itemView.findViewById(R.id.difference_exp);
        }
    }
}
