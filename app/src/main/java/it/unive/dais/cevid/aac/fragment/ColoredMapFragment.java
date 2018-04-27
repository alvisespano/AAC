package it.unive.dais.cevid.aac.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.ColoredMapActivity;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.item.HealthItem;
import it.unive.dais.cevid.aac.item.IncassiSanita;


public class ColoredMapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "ColoredMapFragment";

    protected GoogleMap mMap;

    private  JSONArray ids = new JSONArray();

    List<HealthItem> healthItemsList = new ArrayList<HealthItem>(MainActivity.getHealthItems() );

    Marker currentMarker = null;

    //we will hold here all the data from incassi_sanita.csv
    IncassiSanita healthData = new IncassiSanita(MainActivity.getIncassiSanitaData());

    float maximumTotal = 0;

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


        ArrayList<Float> bin = null;
        try {
            bin = getSortedTotalAmounts();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView t1 = (TextView) this.getView().findViewById(R.id.textView1);
        TextView t2 = (TextView) this.getView().findViewById(R.id.textView2);
        TextView t3 = (TextView) this.getView().findViewById(R.id.textView3);
        TextView t4 = (TextView) this.getView().findViewById(R.id.textView4);
        t1.setText(String.valueOf(bin.get(0)));
        t2.setText(String.valueOf(bin.get(1)));
        t3.setText(String.valueOf(bin.get(2)));
        t4.setText(String.valueOf(bin.get(3)));

        int color = 0;
        for(int i=0;i<regions.size();i++)
        {
            String tag = null;
            try {
                 tag = (String) ids.get(i);
                float polygonAmount = healthData.getTotalPerRegion(tag);
                if(polygonAmount<bin.get(0))
                    color = Color.rgb(255,229,153);
                else if(polygonAmount>= bin.get(0) &&polygonAmount<bin.get(1))
                        color = Color.rgb(255,153,0);
                    else if(polygonAmount>=bin.get(1)&&polygonAmount<bin.get(2))
                            color = Color.rgb(255,0,0);
                        else
                            color = Color.rgb(204,0,0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Polygon polygon = mMap.addPolygon(regions.get(i));
            polygon.setTag(tag);
            polygon.setClickable(true);
            polygon.setFillColor(color);
            polygon.setStrokeColor(Color.LTGRAY);

            //Log.d("iterator! ", it.next().getId());

            Log.d("Dimensiune: ", String.valueOf(healthItemsList.size()));
            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
                public void onPolygonClick(Polygon polygon){
                    double lat = 0, lng = 0;
                    String title = null;
                    String t = polygon.getTag().toString();

                    float total = healthData.getTotalPerRegion(t);
                    Log.d(t," : "+String.valueOf(healthData.getTotalPerRegionAndTitolo(t,"ENTRATE DERIVANTI DALLA PRESTAZIONE DI SERVIZI")));


                    for (int k=0; k<healthItemsList.size(); k++) {
                        HealthItem element = healthItemsList.get(k);
                        //Log.d("id parsat!", element.getId());
                        //Log.d("Cele doua comparate: ", polygon.getTag().toString()+"<>"+element.getId());
                        if(t.equals(element.getId()))
                        {
                            Log.d("Am ajuns pe true! ","da!");
                            lng = element.getLongitude();
                            lat = element.getLatitude();
                            title = element.getName();
                        }
                    }
                    double finalLat = lat;
                    double finalLng = lng;
                    String finalTitle = title;
                    String infoMarker;
                        infoMarker = String.format("%s : %.2f", finalTitle, total);
                    //Log.d()
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

                    Log.d("Am dat click pe : ", String.valueOf(polygon.getTag()));


                }

            });

        }

    }

    private ArrayList<Float> getSortedTotalAmounts() throws JSONException {
        ArrayList<Float> temp = new ArrayList<>();
        float [] arr = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(int i=0;i<ids.length();i++)
        {
            arr[i] = healthData.getTotalPerRegion((String) ids.get(i));
        }
        Arrays.sort(arr);
        temp.add(arr[4]);
        temp.add(arr[9]);
        temp.add(arr[14]);
        temp.add(arr[19]);
        return temp;
    }


}
