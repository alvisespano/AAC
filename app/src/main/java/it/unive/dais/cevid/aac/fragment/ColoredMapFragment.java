package it.unive.dais.cevid.aac.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.item.HealthItem;
import it.unive.dais.cevid.aac.parser.RegionsCoordinatesParser;


public class ColoredMapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "ColoredMapFragment";

    protected GoogleMap mMap;
    private  JSONArray ids = new JSONArray();
    List<HealthItem> healthItemsList = new ArrayList<HealthItem>(MainActivity.getHealthItems() );
    Marker currentMarker = null;


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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        Log.d("HealthItemList : " , healthItemsList.toString());

        LatLng rome = new LatLng(41.89, 12.51);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 5.3F));

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
            //Log.d("iterator! ", it.next().getId());

            Log.d("Dimensiune: ", String.valueOf(healthItemsList.size()));
            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
                public void onPolygonClick(Polygon polygon){
                    double lat = 0, lng = 0;
                    String title = null;
                    //do whatever with polygon!
                    for (int k=0; k<healthItemsList.size(); k++) {
                        HealthItem element = healthItemsList.get(k);
                        //Log.d("id parsat!", element.getId());
                        Log.d("Cele doua comparate: ", polygon.getTag().toString()+"<>"+element.getId());
                        String t = polygon.getTag().toString();
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
                    Log.d("Informatii din marker", finalTitle+" "+finalLat+" "+finalLng);
                    if (currentMarker!=null) {
                        currentMarker.remove();
                        currentMarker=null;
                    }
                    if (currentMarker==null) {
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(finalLat, finalLng))
                                .title(finalTitle));
                        currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospitals));
                        currentMarker.showInfoWindow();
                    }
                    Log.d("Am dat click pe : ", String.valueOf(polygon.getTag()));
                }

            });

        }

    }



}
