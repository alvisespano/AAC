package it.unive.dais.cevid.aac.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.component.MunicipalitySearchActivity;
import it.unive.dais.cevid.aac.component.PieChartActivity;
import it.unive.dais.cevid.aac.component.SupplierSearchActivity;
import it.unive.dais.cevid.aac.component.UniversitySearchActivity;
import it.unive.dais.cevid.aac.item.HealthItem;
import it.unive.dais.cevid.aac.item.IncassiSanita;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;

    public ListFragment() {
        // Requires empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        assert parentActivity != null;
        listView = (ListView) rootView.findViewById(R.id.fragment_list_view);
        listView.setOnItemClickListener(this);
        ((SwipeRefreshLayout) rootView.findViewById(R.id.refresh)).setOnRefreshListener(this);
        redraw(parentActivity.getCurrentMode());
        return rootView;

    }

    @Override
    public void redraw(MainActivity.Mode mode) {
        assert parentActivity != null;
        Collection<? extends MapItem> c = null;

        switch (mode) {
            case SUPPLIER:
                c = parentActivity.getSupplierItems();
                break;
            case UNIVERSITY:
                c = parentActivity.getUniversityItems();
                break;
            case MUNICIPALITY:
                c = parentActivity.getMunicipalityItems();
                break;
            case HEALTH:
                c = parentActivity.getHealthItems();
                break;
        }
        List<MapItem> r = new ArrayList<>();
        r.addAll(c);
        listView.setAdapter(new ArrayAdapter<MapItem>(getContext(), R.layout.list_fragment, R.id.list_object, r));
    }

    @Override
    public Type getType() {
        return Type.LIST;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MapItem item = (MapItem) parent.getItemAtPosition(position);
        Intent intent;
        assert parentActivity != null;
        switch (parentActivity.getCurrentMode()) {
            case MUNICIPALITY:
                MunicipalityItem municipalityItem = (MunicipalityItem) item;
                intent = new Intent(getContext(), MunicipalitySearchActivity.class);
                intent.putExtra(MunicipalitySearchActivity.CODICE_ENTE, municipalityItem.getId());
                intent.putExtra(MunicipalitySearchActivity.CODICE_COMPARTO, municipalityItem.getCodiceComparto());
                intent.putExtra(MunicipalitySearchActivity.MUNICIPALITY_ITEM, municipalityItem);
                startActivity(intent);
                break;
            case UNIVERSITY:
                intent = new Intent(getContext(), UniversitySearchActivity.class);
                intent.putExtra(UniversitySearchActivity.UNIVERSITY_LIST, item);
                startActivity(intent);
                break;
            case SUPPLIER:
                intent = new Intent(getContext(), SupplierSearchActivity.class);
                intent.putExtra(SupplierSearchActivity.SUPPLIER_ITEM, item);
                startActivity(intent);
                break;
            case HEALTH:
                HealthItem healthItem = (HealthItem) item;
                IncassiSanita incassiSanita = new IncassiSanita(parentActivity.getIncassiSanitaData());
                Gson gson = new Gson();
                List<IncassiSanita.DataRegione> regionData = incassiSanita.getDataByIdRegione(healthItem.getId());
                intent = new Intent(getContext(), PieChartActivity.class);
                intent.putExtra("regionId", healthItem.getId());
                intent.putExtra("regionData", gson.toJson(regionData));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        assert parentActivity != null;
        SwipeRefreshLayout refreshView = (SwipeRefreshLayout) parentActivity.findViewById(R.id.refresh);
        redraw(parentActivity.getCurrentMode());
        refreshView.setRefreshing(false);
    }
}