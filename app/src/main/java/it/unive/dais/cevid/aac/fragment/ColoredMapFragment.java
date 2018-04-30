package it.unive.dais.cevid.aac.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.item.HealthItem;
import it.unive.dais.cevid.aac.item.IncassiSanita;


public class ColoredMapFragment extends Fragment
        implements OnMapReadyCallback,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "ColoredMapFragment";

    Button listButton;

    protected GoogleMap mMap;

    private  JSONArray ids = new JSONArray();

    private List<HealthItem> healthItemsList = new ArrayList<HealthItem>(MainActivity.getHealthItems() );

    private Marker currentMarker = null;

    //we will hold here all the data from incassi_sanita.csv
    private IncassiSanita healthData = new IncassiSanita(MainActivity.getIncassiSanitaData());

    //selected item from the menu
    private String selectedItem = "TOTALE";

    //bins for the legend
    private ArrayList<Float> bin = null;

    //all the polygons
    private ArrayList<Polygon> allPolygons = new ArrayList<>();

    //string for dialog box
    private String textDialog = new String();

    //values for the dialog box
    private ArrayList<Float> arrValues = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_colored_map, container, false);
        MapView mapView = (MapView) mView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        return mView;
    }

    private void listDialogCreate() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(selectedItem);
        alertDialog.setMessage(textDialog);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    //private

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        //Log.d("HealthItemList : " , healthItemsList.toString());

        LatLng rome = new LatLng(41.89, 12.51);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 4.0F));

        //button to show list
        listButton = (Button) this.getView().findViewById(R.id.list_button);

        listButton.setOnClickListener(v -> listDialogCreate());

        // Spinner element
        Spinner spinner = (Spinner) this.getView().findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("TOTALE");
        categories.add("ENTRATE DERIVANTI DALLA PRESTAZIONE DI SERVIZI");
        categories.add("CONTRIBUTI E TRASFERIMENTI CORRENTI");
        categories.add("ENTRATE DERIVANTI DA ALIENAZIONI DI BENI");
        categories.add("CONTRIBUTI E TRASFERIMENTI IN C/CAPITALE");
        categories.add("OPERAZIONI FINANZIARIE");
        categories.add("INCASSI DA REGOLARIZZARE");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        try {
            InputStream in = getResources().openRawResource(R.raw.coord_regions);
            String input = convertStreamToString(in);
            JSONObject reader = new JSONObject(input);
            ids = reader.getJSONArray("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<PolygonOptions> regions;
        regions = MainActivity.getRegionsCoordinates();

        setLegendScale();

        for(int i=0;i<regions.size();i++)
        {
            String tag = null;
            try {
                 tag = (String) ids.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Polygon polygon = mMap.addPolygon(regions.get(i));
            polygon.setTag(tag);
            polygon.setClickable(true);
            allPolygons.add(polygon);

            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
                public void onPolygonClick(Polygon polygon){
                    double lat = 0, lng = 0;
                    String title = null;
                    String t = polygon.getTag().toString();

                    float total = 0;
                    if(selectedItem.equals("TOTALE"))
                        total = healthData.getTotalPerRegion(t);
                    else
                        total = healthData.getTotalPerRegionAndTitolo(t,selectedItem);

                    for (int k=0; k<healthItemsList.size(); k++) {
                        HealthItem element = healthItemsList.get(k);
                        if(t.equals(element.getId()))
                            {
                                lng = element.getLongitude();
                                lat = element.getLatitude();
                                title = element.getName();
                            }
                    }
                    double finalLat = lat;
                    double finalLng = lng;
                    String finalTitle = title;
                    String infoMarker;
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.ITALY);
                        infoMarker = finalTitle+" : "+numberFormat.format(total);
                    if (currentMarker!=null) {
                        currentMarker.remove();
                        currentMarker=null;
                    }
                    if (currentMarker==null) {
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(finalLat, finalLng))
                                .title(infoMarker));
                        currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospitals));
                        currentMarker.showInfoWindow();
                    }
                }
            });
        }
    }

    private ArrayList<Float> getSortedAmount(String amountName) throws JSONException {
        ArrayList<Float> temp = new ArrayList<>();
        float [] arr = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<ids.length();i++)
        {
            if(amountName.equals("TOTALE"))
                arr[i] = healthData.getTotalPerRegion((String) ids.get(i));
            else
                arr[i] = healthData.getTotalPerRegionAndTitolo((String) ids.get(i),amountName);
        }
        Arrays.sort(arr);
        arrValues.clear();
        for(int i=0;i<arr.length;i++) {
            arrValues.add(arr[i]);
        }
        temp.add(arr[4]);
        temp.add(arr[9]);
        temp.add(arr[14]);
        temp.add(arr[19]);
        return temp; //this are for the bins so we can split the colors for each region
    }

    private String makeTextForDialog() throws JSONException {
        String a,b ;
        ArrayList<String> names = new ArrayList<>();
        textDialog = "";
        for(int i=0;i<arrValues.size();i++)
        {
            b = String.valueOf(arrValues.get(i));
            for(int j=0;j<ids.length();j++) {
                if(selectedItem=="TOTALE")
                {
                    a = String.valueOf(healthData.getTotalPerRegion((String) ids.get(j)));
                    if (a.equals(b))
                    {
                        for(HealthItem h : healthItemsList)
                        {
                            if(h.getId().equals(ids.get(j)))
                            {
                                names.add(h.getName());
                            }
                        }
                    }
                }
                else {
                    a = String.valueOf(healthData.getTotalPerRegionAndTitolo((String) ids.get(j), selectedItem));
                    if (a.equals(b))
                    {
                        for(HealthItem h : healthItemsList)
                        {
                            if(h.getId().equals(ids.get(j)))
                            {
                                names.add(h.getName());
                            }
                        }
                    }
                }
            }
        }
        for(int i=arrValues.size()-1;i>=0;i--)
        {
            String line = names.get(i)+" : "+arrValues.get(i);
            textDialog += line;
            textDialog +="\n";
        }

        return textDialog;
    }

    private String makeAmountForLegend(float amount)
    {
        String temp = null;
        if(amount>=100000000)
        {
            amount /= 1000000000;
            temp = "B";
        }
        else if(amount<100000000&&amount>=1000000)
            {
                amount /= 1000000;
                temp = "M";
            }
            else if(amount<1000000)
            {
                amount /= 1000;
                temp = "K";
            }
        String t = String.format("%.1f",amount);
        temp = t+temp;
        return temp;
    }

    private void setLegendScale()
    {
        try {
            bin = getSortedAmount(selectedItem);
            makeTextForDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView t1 = (TextView) this.getView().findViewById(R.id.textView1);
        TextView t2 = (TextView) this.getView().findViewById(R.id.textView2);
        TextView t3 = (TextView) this.getView().findViewById(R.id.textView3);
        TextView t4 = (TextView) this.getView().findViewById(R.id.textView4);
        t1.setText(makeAmountForLegend(bin.get(0)));
        t2.setText(makeAmountForLegend(bin.get(1)));
        t3.setText(makeAmountForLegend(bin.get(2)));
        t4.setText(makeAmountForLegend(bin.get(3)));
    }

    private void changePolygonsColor() {
        int color = 0;
        int strokeColor = 0;
        for (int i = 0; i < allPolygons.size(); i++) {
            String tag = null;
            tag = (String) allPolygons.get(i).getTag();
            float polygonAmount = 0;
            if (selectedItem.equals("TOTALE"))
                polygonAmount = healthData.getTotalPerRegion(tag);
            else
                polygonAmount = healthData.getTotalPerRegionAndTitolo(tag, selectedItem);
                if (polygonAmount < bin.get(0)) {
                    color = Color.argb(145, 255, 229, 153);
                    strokeColor = Color.rgb(255, 229, 153);
                    } else if (polygonAmount >= bin.get(0) && polygonAmount < bin.get(1)) {
                        color = Color.argb(145, 255, 153, 0);
                        strokeColor = Color.rgb(255, 153, 0);
                        } else if (polygonAmount >= bin.get(1) && polygonAmount < bin.get(2)) {
                                color = Color.argb(145, 255, 0, 0);
                                strokeColor = Color.rgb(255, 0, 0);
                                } else {
                                    color = Color.argb(145, 204, 0, 0);
                                    strokeColor = Color.rgb(204, 0, 0);
                                }
            allPolygons.get(i).setFillColor(color);
            allPolygons.get(i).setStrokeColor(strokeColor);
            allPolygons.get(i).setStrokeWidth(8);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //remove marker from last selected item
        if (currentMarker!=null) {
            currentMarker.remove();
            currentMarker=null;
        }
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        selectedItem = item;
        setLegendScale();
        changePolygonsColor();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
