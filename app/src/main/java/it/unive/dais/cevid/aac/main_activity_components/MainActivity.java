package it.unive.dais.cevid.aac.main_activity_components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationSettingsStates;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.main_activity_components.parser.EnrolledParser;
import it.unive.dais.cevid.aac.main_activity_components.parser.GraduatedParser;
import it.unive.dais.cevid.aac.menu_activities.AboutActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.menu_activities.SettingsActivity;
import it.unive.dais.cevid.aac.main_activity_components.fragment.BaseFragment;
import it.unive.dais.cevid.aac.main_activity_components.fragment.ListFragment;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.suppliers.util.SupplierItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.aac.main_activity_components.fragment.MapFragment;
import it.unive.dais.cevid.aac.suppliers.parser.SupplierParser;
import it.unive.dais.cevid.aac.suppliers.parser.TendersLinkParser;
import it.unive.dais.cevid.datadroid.lib.parser.progress.Handle;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.parser.progress.PercentProgressStepper;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;

    private BaseFragment currentMapFragment = new MapFragment();
    private BottomNavigationView bottomNavigation;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ProgressBarManager progressBarManager;  // TODO: provare a mettere qui la findViewById e vedere se funziona
    private static Map<String, List<URL>> codiceEnteAppaltiURLMap = Collections.EMPTY_MAP;
    private static Map<String, Integer> universityCapiteMap = Collections.EMPTY_MAP;

    private static TendersLinkParser tendersLinkParser;
    private static GraduatedParser graduatedParser;
    private static EnrolledParser enrolledParser1516;
    private static EnrolledParser enrolledParser1617;

    public enum Mode {
        UNIVERSITY,
        MUNICIPALITY,
        SUPPLIER
    }

    @NonNull
    private final Collection<UniversityItem> universityItems = new ConcurrentLinkedQueue<>();
    @NonNull
    private final Collection<MunicipalityItem> municipalityItems = new ConcurrentLinkedQueue<>();
    @NonNull
    private final Collection<SupplierItem> supplierItems = new ConcurrentLinkedQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setContentFragment(R.id.content_frame, currentMapFragment);

        //progressBarManager = new ProgressBarManager(this, new ProgressBar[]{(ProgressBar) findViewById(R.id.progress_bar_main)});
        progressBarManager = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_main));
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        setUpItems();
    }

    private void launchParsers() {
        InputStreamReader tendersReader = new InputStreamReader(getResources().openRawResource(R.raw.tenders));
        tendersLinkParser = new TendersLinkParser(tendersReader, true, ",", progressBarManager);
        tendersLinkParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        InputStreamReader graduatedReader = new InputStreamReader(getResources().openRawResource(R.raw.laureati_16));
        graduatedParser = new GraduatedParser(graduatedReader, true, ";", progressBarManager);
        graduatedParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        enrolledParser1516 = launchEnrolledParser(R.raw.iscritti_15_16);
        enrolledParser1617 = launchEnrolledParser(R.raw.iscritti_16_17);

    }

    private EnrolledParser launchEnrolledParser(int resource) {
        InputStreamReader enrolledReader = new InputStreamReader(getResources().openRawResource(resource));
        EnrolledParser enrolledParser = new EnrolledParser(enrolledReader, true, ";", progressBarManager);
        enrolledParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return enrolledParser;
    }

    private void setContentFragment(int container, @NonNull BaseFragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(container, fragment, fragment.getTag())
                .commit();

    }

    private void changeActiveFragment(@NonNull BaseFragment fragment) {
        currentMapFragment = fragment;
        setContentFragment(R.id.content_frame, fragment);
    }

    //stub for change button onClick listener
    public void onChangeType() {
        switch (currentMapFragment.getType()) {
            case MAP:
                changeActiveFragment(new ListFragment());
                break;
            case LIST:
                changeActiveFragment(new MapFragment());
                break;
            default:
                break;
        }
    }

    private void setUpItems() {
        launchParsers();
        setUpMunicipalityItems();
        setUpSupplierItems();
        setUpUniversityItems();
    }

    private void setUpSupplierItems() {
        SupplierParser p = new SupplierParser(progressBarManager) {
            @NonNull
            @Override
            public List<Data> onPostParse(@NonNull List<Data> r) {
                for (SupplierParser.Data x : r) {
                    supplierItems.add(new SupplierItem(MainActivity.this, x));
                }
                return r;
            }

            @Override
            public void onPostExecute(@NonNull List<Data> r) {
                if (getCurrentMode() == Mode.SUPPLIER) currentMapFragment.redraw(Mode.SUPPLIER);
            }
        };
        p.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setUpUniversityItems() {

        //setUniversityCapite();

        //add Ca Foscari
        universityItems.add(new UniversityItem("000704968000000", "Università Ca' Foscari",
                "Università degli studi di Venezia", 45.437576, 12.3289554));

        //add Padova
        universityItems.add(new UniversityItem("000058546000000", "Università di Padova",
                "Università degli studi di Padova", 45.406766, 11.8774462));

        //add Trento
        universityItems.add(new UniversityItem("000067046000000", "Università di Trento",
                "Università degli studi di Trento", 46.0694828, 11.1188738));

    }


    private void setUpMunicipalityItems() {
        //add Roma
        municipalityItems.add(new MunicipalityItem("800000047", "Roma", "COMUNE DI ROMA", 2873494, 41.9102411, 12.3955688));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "location service connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "location service connection suspended");
        Toast.makeText(this, R.string.conn_suspended, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "location service connection lost");
        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permissions granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                } else {
                    Log.e(TAG, "permissions not granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                    Snackbar.make(this.findViewById(R.id.main_view), R.string.msg_permissions_not_granted, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_with_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.menu_button_swap:
                changeItemIcon(item);
                onChangeType();
                break;
        }
        return false;
    }

    private void changeItemIcon(MenuItem item) {
        switch (currentMapFragment.getType()) {
            case LIST:
                item.setIcon(R.drawable.ic_view_list);
                break;
            case MAP:
                item.setIcon(R.drawable.ic_view_map);
                break;
        }
    }

    /**
     * Quando arriva un Intent viene eseguito questo metodo.
     * Può essere esteso e modificato secondo le necessità.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // inserire codice qui
                        break;
                    case Activity.RESULT_CANCELED:
                        // o qui
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Applica le impostazioni (preferenze) della mappa ad ogni chiamata.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Mode mode = getModeByMenuItemId(item.getItemId());
        Log.d(TAG, String.format("entering mode %s", mode));
        currentMapFragment.redraw(mode);
        if (currentMapFragment instanceof MapFragment) {
            ((MapFragment) currentMapFragment).getSelectedMarkers().clear();
        }
        return true;
    }

    @SuppressLint("DefaultLocale")
    private static Mode getModeByMenuItemId(int id) {
        switch (id) {
            case R.id.menu_university:
                return Mode.UNIVERSITY;
            case R.id.menu_municipality:
                return Mode.MUNICIPALITY;
            case R.id.menu_suppliers:
                return Mode.SUPPLIER;
            default:
                throw new UnexpectedException(String.format("invalid menu item id: %d", id));
        }
    }

    public Mode getCurrentMode() {
        assert bottomNavigation != null;
        return getModeByMenuItemId(bottomNavigation.getSelectedItemId());
    }

    @NonNull
    public Collection<UniversityItem> getUniversityItems() {
        return universityItems;
    }

    @NonNull
    public Collection<SupplierItem> getSupplierItems() {
        return supplierItems;
    }

    @NonNull
    public Collection<MunicipalityItem> getMunicipalityItems() {
        return municipalityItems;
    }

    public static void setUpTendersLink() {
        List<TendersLinkParser.Data> dataList;

        try {
            dataList = tendersLinkParser.getAsyncTask().get();
            setUpCodiceEnteAppaltiURLMap(dataList);
        } catch (InterruptedException | ExecutionException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static void setUpCodiceEnteAppaltiURLMap(List<TendersLinkParser.Data> dataList) throws MalformedURLException {
        codiceEnteAppaltiURLMap = new HashMap<>();

        for (TendersLinkParser.Data d : dataList) {
            if (codiceEnteAppaltiURLMap.containsKey(d.codiceEnte)) {
                codiceEnteAppaltiURLMap.get(d.codiceEnte).add(new URL(d.url));
            }
            else {
                List urls = new ArrayList<URL>();
                urls.add(new URL(d.url));
                codiceEnteAppaltiURLMap.put(d.codiceEnte, urls);
            }
        }
    }

    private static Map<String, Integer> parseGraduated() {
        Map<String, Integer> graduatedMap = new HashMap<>();

        try {
            for (GraduatedParser.Data gd : graduatedParser.getAsyncTask().get()) {
                if (gd.nome_ateneo.contains("Foscari")) {
                    graduatedMap.put("000704968000000", Integer.parseInt(gd.laureati));
                }
                if (gd.nome_ateneo.contains("Padova")) {
                    graduatedMap.put("000058546000000", Integer.parseInt(gd.laureati));
                }
                if (gd.nome_ateneo.contains("Trento")) {
                    graduatedMap.put("000067046000000", Integer.parseInt(gd.laureati));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return graduatedMap;
    }

    public static void setUpUniversityCapite() {
        Map<String, Integer> enrolledMap = parseEnrolled();
        Map<String, Integer> graduatedMap = parseGraduated();

        universityCapiteMap = new HashMap<>();

        for (String codiceEnte : enrolledMap.keySet()) {
            universityCapiteMap.put(codiceEnte, enrolledMap.get(codiceEnte) - graduatedMap.get(codiceEnte));
        }
    }


    private static Map<String, Integer> parseEnrolled() {
        Map<String, Integer> enrolledMap1516 = parseEnrolled(enrolledParser1516);
        Map<String, Integer> enrolledMap1617 = parseEnrolled(enrolledParser1617);

        Map<String, Integer> enrolledMap = new HashMap<>();

        for (String codiceEnte : enrolledMap1516.keySet()) {
            enrolledMap.put(codiceEnte, enrolledMap1516.get(codiceEnte) + enrolledMap1617.get(codiceEnte));
        }

        return enrolledMap;
    }

    private static Map<String, Integer> parseEnrolled(EnrolledParser enrolledParser) {
        Map<String, Integer> enrolledMap = new HashMap<>();

        try {
            for (EnrolledParser.Data ed : enrolledParser.getAsyncTask().get()) {
                if (ed.nome_ateneo.contains("Foscari")) {
                    if (!enrolledMap.containsKey("000704968000000"))
                        enrolledMap.put("000704968000000", Integer.parseInt(ed.numero_studenti));
                    else
                        enrolledMap.put("000704968000000", enrolledMap.get("000704968000000") + Integer.parseInt(ed.numero_studenti));
                }
                if (ed.nome_ateneo.contains("Padova")) {
                    if (!enrolledMap.containsKey("000058546000000"))
                        enrolledMap.put("000058546000000", Integer.parseInt(ed.numero_studenti));
                    else
                        enrolledMap.put("000058546000000", enrolledMap.get("000058546000000") + Integer.parseInt(ed.numero_studenti));
                }
                if (ed.nome_ateneo.contains("Trento")) {
                    if (!enrolledMap.containsKey("000067046000000"))
                        enrolledMap.put("000067046000000", Integer.parseInt(ed.numero_studenti));
                    else
                        enrolledMap.put("000067046000000", enrolledMap.get("000067046000000") + Integer.parseInt(ed.numero_studenti));
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return enrolledMap;
    }

    public static Map<String, Integer> getUniversityCapiteMap() {
        return universityCapiteMap;
    }

    public static Map<String, List<URL>> getCodiceEnteAppaltiURLMap() {
        return codiceEnteAppaltiURLMap;
    }


    // test stuff
    //
    //

    private void testProgressStepper() {
        final int n1 = 10, n2 = 30, n3 = 5;
        PercentProgressStepper p1 = new PercentProgressStepper(n1);
        for (int i = 0; i < n1; ++i) {
            PercentProgressStepper p2 = p1.getSubProgressStepper(n2);
            for (int j = 0; j < n2; ++j) {
                p2.step();
                Log.d(TAG, String.format("test progress: %d%%", (int) (p2.getPercent() * 100.)));
            }
            p1.step();
            Log.d(TAG, String.format("test progress: %d%%", (int) (p1.getPercent() * 100.)));
        }
    }

    private void testProgressBarManager() {
        ProgressBarManager m = new ProgressBarManager(this, (ProgressBar) findViewById(R.id.progress_bar_main));

        runOnUiThread(() -> {
            Handle<ProgressBar> h1 = m.acquire();
            Handle<ProgressBar> h2 = m.acquire();
            h1.apply(b -> {
                b.setMax(100);
                return null;
            });
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                h1.apply(b -> {
                    b.setProgress(finalI);
                    return null;
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
