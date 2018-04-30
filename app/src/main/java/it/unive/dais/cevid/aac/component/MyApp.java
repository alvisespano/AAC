package it.unive.dais.cevid.aac.component;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application{
    public MyApp() {
        // this method fires only once per application start.
        // getApplicationContext returns null here


        Log.i("main", "Constructor fired");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // this method fires once as well as constructor
        // but also application has context here
        MainActivity.setupColoredRegions(getBaseContext());
        Log.i("main", "onCreate fired");
    }
}

