package it.unive.dais.cevid.aac.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.ColoredMapActivity;


public class ColoredMapFragment extends Fragment implements OnMapReadyCallback{
    private static final String TAG = "ColoredMapFragment";

    protected GoogleMap mMap;


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

    public JSONArray parseCoordinates() {
        JSONArray coordinates = null;

        try {
            Log.d(TAG, "Angelko ++");
            InputStream in = getResources().openRawResource(R.raw.veneto);
            String inputVeneto = convertStreamToString(in);
            JSONObject reader = new JSONObject(inputVeneto);
            coordinates = reader.getJSONArray("Veneto");
            Log.d("Angelko ", String.valueOf(coordinates));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;


        LatLng rome = new LatLng(41.89, 12.51);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 5));

        JSONArray coordinates = this.parseCoordinates();

        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions();

        for(int i=0;i<coordinates.length();i++)
        {
            try {
                JSONArray point= (JSONArray) coordinates.get(i);
                double lat= (double) point.get(0);
                double lng= (double) point.get(1);
                Log.d(TAG, "Maria lat "+lat);
                Log.d(TAG, "Maria lng "+lng);
                rectOptions.add(new LatLng(lng,lat));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        rectOptions.fillColor(Color.BLUE);
        // Get back the mutable Polygon
        mMap.addPolygon(rectOptions);
    }

}
