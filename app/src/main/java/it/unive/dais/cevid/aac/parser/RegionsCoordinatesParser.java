package it.unive.dais.cevid.aac.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
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

public class RegionsCoordinatesParser extends AsyncTask<String,Void,Boolean>{

    private static final String TAG = "RegCoordParser";


    private Context context;

    private JSONArray coordinates = null;

    private ArrayList<PolygonOptions> polygons = new ArrayList<PolygonOptions>();

    public RegionsCoordinatesParser(Context ctx)
    {
        this.context=ctx;
    }

    @NonNull
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


    private PolygonOptions createPolygon(JSONArray coord)
    {
        JSONArray regionCoordinates = coord;
        PolygonOptions rectOptions = new PolygonOptions();

        for(int i=0;i<regionCoordinates.length();i++)
        {
            try {
                JSONArray point= (JSONArray) regionCoordinates.get(i);
                double lat= (double) point.get(0);
                double lng= (double) point.get(1);
                //Log.d(TAG, "Maria lat "+lat);
                //Log.d(TAG, "Maria lng "+lng);
                rectOptions.add(new LatLng(lng,lat));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        //Log.d(TAG," Angelko coordonate din rectOptions : "+rectOptions.getPoints());
        return rectOptions;
    }


    public ArrayList<PolygonOptions> getPolygons() {
        return polygons;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try {
            //Log.d(TAG, "Angelko ++");
            InputStream in = context.getResources().openRawResource(R.raw.coord_regions);
            String input = convertStreamToString(in);
            JSONObject reader = new JSONObject(input);
            coordinates = reader.getJSONArray("coordinates");


            //Log.d("Angelko ", String.valueOf(coordinates));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<coordinates.length();i++)
        {

            try {
                JSONArray region= (JSONArray) coordinates.get(i);
                //Log.d(TAG," Angelko region : "+region);

                PolygonOptions newPolygon = new PolygonOptions();
                newPolygon = this.createPolygon(region);
                newPolygon.clickable(true);
                //Log.d(TAG," Angelko coordonate din newPolygon : "+newPolygon.getPoints());
                // Get back the mutable Polygon
                polygons.add(newPolygon);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);
    }


}
