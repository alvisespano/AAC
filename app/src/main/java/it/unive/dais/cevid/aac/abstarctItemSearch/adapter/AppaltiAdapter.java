package it.unive.dais.cevid.aac.abstarctItemSearch.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;


public class AppaltiAdapter extends RecyclerView.Adapter<AppaltiAdapter.AppaltoItem> {

    private List<AppaltiParser.Data> dataList;
    @Nullable
    private final Double avg;

    public AppaltiAdapter(List<AppaltiParser.Data> dataList, Double avg) {
        this.dataList = dataList;
        this.avg = avg;
    }

    @Override
    public AppaltoItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_appalti, parent, false);
        return new AppaltoItem(itemView);
    }

    @Override
    public void onBindViewHolder(AppaltoItem holder, int position) {
        final AppaltiParser.Data x = dataList.get(position);
        holder.setImporto(x.importo);
        holder.setAggiudicatario(x.aggiudicatario);
        holder.setOggetto(x.oggetto);
        holder.setScelta(x.sceltac);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class AppaltoItem extends RecyclerView.ViewHolder {

        private TextView importo, oggetto, aggiudicatario, scelta;

        public AppaltoItem(View itemView) {
            super(itemView);
            importo = (TextView) itemView.findViewById(R.id.text_cost);
            oggetto = (TextView) itemView.findViewById(R.id.text_oggetto);
            aggiudicatario = (TextView) itemView.findViewById(R.id.text_supplier);
            scelta = (TextView) itemView.findViewById(R.id.text_selection);
        }

        public void setImporto(String s) {
            double x = Double.parseDouble(s);
            importo.setText(String.format("%.2f€", x));
            if (avg != null)
                importo.setBackgroundColor(x <= avg ? Color.GREEN : Color.RED);
        }

        public void setAggiudicatario(String s) {
            aggiudicatario.setText(s);
        }

        public void setOggetto(String s) {
            oggetto.setText(s);
        }

        public void setScelta(String s) {
            scelta.setText(s);
        }
    }
}
