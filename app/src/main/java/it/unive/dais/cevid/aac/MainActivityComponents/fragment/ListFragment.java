package it.unive.dais.cevid.aac.MainActivityComponents.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.MainActivityComponents.MainActivity;
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
        assert parentActivity != null;

        if ((parentActivity.getCurrentMode() == MainActivity.Mode.UNIVERSITY) ||
                (parentActivity.getCurrentMode() == MainActivity.Mode.MUNICIPALITY))
            manageAICase(item);
        if (parentActivity.getCurrentMode() == MainActivity.Mode.SUPPLIER)
                manageSupplierItemCase(item);
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public void onRefresh() {
        assert parentActivity != null;
        SwipeRefreshLayout refreshView = (SwipeRefreshLayout) parentActivity.findViewById(R.id.refresh);
        redraw(parentActivity.getCurrentMode());
        refreshView.setRefreshing(false);
    }
}
