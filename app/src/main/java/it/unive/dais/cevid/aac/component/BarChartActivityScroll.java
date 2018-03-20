package it.unive.dais.cevid.aac.component;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.IncassiSanita;
import it.unive.dais.cevid.aac.util.DemoBase;

/**
 * Created by ddonaggio on 19/12/17.
 */


public class BarChartActivityScroll extends DemoBase implements OnChartValueSelectedListener {

    private BarChart mChart;
    private String regionId;
    private String titolo;
    private List<IncassiSanita.DataRegione> incassiSanitaRegione;
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.ITALY);
    private HashMap<Integer,String> mapDesc = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<IncassiSanita.DataRegione>>(){}.getType();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                regionId = "Null";
                titolo = "Null";
                incassiSanitaRegione = null;
            } else {
                regionId = extras.getString("regionId");
                titolo = extras.getString("titolo");
                incassiSanitaRegione = gson.fromJson(extras.getString("regionData"), listType);
            }
        } else {
            regionId = (String) savedInstanceState.getSerializable("regionId");
            titolo = (String) savedInstanceState.getSerializable("titolo");
            incassiSanitaRegione = gson.fromJson((JsonElement) savedInstanceState.getSerializable("regionData"), listType);
        }

        TextView regionName = (TextView) findViewById(R.id.regionName);
        regionName.setText(regionId.replace("IT-",""));

        mChart = (BarChart) findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("I valori nell'asse Y sono espressi in scala logaritmica");
        mChart.getDescription().setPosition(550,20);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);

        Legend l = mChart.getLegend();
        l.setEnabled(false);

        setData(10);
        mChart.setFitBars(true);
    }

    private void setData(int count) {

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        Set<Map.Entry<String, Float>> importi = IncassiSanita.getImportiFromDataRegioneAndTitolo(incassiSanitaRegione,titolo).entrySet();
        Iterator importiIter = importi.iterator();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        int i = 0;
        while (importiIter.hasNext()) {
            Map.Entry entry = (Map.Entry) importiIter.next();
            // yVals.add(new BarEntry(i, (float) entry.getValue()));
            yVals.add(new BarEntry(i, (float) Math.log((float) entry.getValue())));
            mapDesc.put(i,(String) entry.getKey());
            i++;
        }

        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setDrawValues(false);
        set.setBarBorderColor(Color.BLACK);
        set.setBarBorderWidth((float) 0.5);

        BarData data = new BarData(set);

        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(800);
    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null) {
            return;
        }  else {
            TextView description = (TextView) findViewById(R.id.description);
            TextView amount = (TextView) findViewById(R.id.amount);
            description.setText(mapDesc.get((int) e.getX()));
            double n = (float) e.getY();
            amount.setText("Totale: "+ numberFormat.format(Math.exp(n)));
        }

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);
        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}