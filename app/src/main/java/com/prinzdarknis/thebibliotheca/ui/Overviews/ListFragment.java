package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.prinzdarknis.thebibliotheca.R;

public abstract class ListFragment extends Fragment {
    ListView listView;
    BaseAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simplelistview, container, false);

        listView = view.findViewById(R.id.listView);
        reloadList();

        //Listener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return longClick(adapter.getItem(position));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shortClick(adapter.getItem(position));
            }
        });

        return view;
    }

    protected abstract void reloadList();
    protected abstract boolean longClick(Object listItem);
    protected abstract void shortClick(Object listItem);
}
