package it.unive.dais.cevid.aac.component;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.fragment.ColoredMapFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ColoredMapActivity extends AppCompatActivity{

    private static final String TAG = "ColoredMapActivity";

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private ColoredMapFragment mapFragment = new ColoredMapFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colored_map);
        Log.d(TAG, "Maria"+mapFragment);
        setContentFragment(R.id.content_frame, mapFragment);
        this.parseCoordinates();
    }

    private void setContentFragment(int container, @NonNull Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(container, fragment, fragment.getTag())
                .commit();

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

    public ArrayList parseCoordinates() {
        ArrayList <LatLng> coordinates = null;

        try {
            Log.d(TAG, "Angelko ++");
            InputStream in = getResources().openRawResource(R.raw.veneto);
            String inputVeneto = convertStreamToString(in);
            JSONObject reader = new JSONObject(inputVeneto);
            JSONArray geometries  = reader.getJSONArray("geometries");
            String g = geometries.get(0).toString();
            JSONObject first = new JSONObject(g);
            JSONArray coord  = first.getJSONArray("coordinates");
            Log.d("Angelko ", String.valueOf(coord));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return coordinates;
    }
}

