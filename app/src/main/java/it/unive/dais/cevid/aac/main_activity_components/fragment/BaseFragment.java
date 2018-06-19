package it.unive.dais.cevid.aac.main_activity_components.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;

import it.unive.dais.cevid.aac.main_activity_components.MainActivity;
import it.unive.dais.cevid.aac.suppliers.activities.SupplierSearchActivity;
import it.unive.dais.cevid.aac.abstarct_item_search.AISearchActivity;
import it.unive.dais.cevid.aac.item.AbstractItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract  class BaseFragment extends Fragment {
    public enum Type{
        MAP,
        LIST
    }
    private static final String TAG = "BaseFragment";

    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;
    /**
     * API per i servizi di localizzazione.
     */
    protected FusedLocationProviderClient fusedLocationClient;
    /**
     * Posizione corrente. Potrebbe essere null prima di essere calcolata la prima volta.
     */
    @Nullable
    protected LatLng currentPosition = null;

    @Nullable
    protected MainActivity parentActivity;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        parentActivity = (MainActivity) ctx;
    }

    protected void gpsCheck() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i(TAG, "All location settings are satisfied.");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute acquire.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }

    public void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requiring permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), loc -> {
                if (loc != null) {
                    currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                    Log.i(TAG, "current position updated");
                }
            });
        }
    }

    public abstract void redraw(MainActivity.Mode mode);

    public abstract Type getType();

    protected void manageAICase(MapItem markerTag) {
        Intent intent = new Intent(getContext(), AISearchActivity.class);
        intent.putExtra(AISearchActivity.ABSTRACT_ITEM, markerTag);

        MainActivity.setUpTendersLink();


        ((AbstractItem) markerTag).setUrls(MainActivity.getCodiceEnteAppaltiURLMap().get(((AbstractItem) markerTag).getId()));

        if (markerTag instanceof UniversityItem) {
            if (MainActivity.getUniversityCapiteMap() == Collections.EMPTY_MAP) {
                MainActivity.setUpUniversityCapite();
            }
            ((UniversityItem) markerTag).setCapite(MainActivity.getUniversityCapiteMap().get(((UniversityItem) markerTag).getId()));
        }

        startActivity(intent);
    }

    protected void manageSupplierItemCase(MapItem markerTag) {
        Intent intent = new Intent(getContext(), SupplierSearchActivity.class);
        intent.putExtra(SupplierSearchActivity.SUPPLIER_ITEM, markerTag);
        startActivity(intent);
    }
}
