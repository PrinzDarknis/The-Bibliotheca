package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;

import java.util.ArrayList;

public class LibraryListAdapter extends BaseAdapter {

    private ArrayList<Library> list;
    private final LayoutInflater inflater;

    public LibraryListAdapter(ArrayList<Library> list, Context context) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       //gibt es schon eine fertige Zeile mit UI-Elementen? weniger speicher
        ViewHolder holder;
        if (convertView == null) {
            //Zusammenbauen der Zeile
            convertView = inflater.inflate(R.layout.row_library, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.series_count = convertView.findViewById(R.id.series_count);
            holder.exemplar_count = convertView.findViewById(R.id.exemplar_count);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //write Values
        Library l = list.get(position);
        holder.name.setText(l.name);
        holder.series_count.setText(String.valueOf(l.series_count));
        holder.exemplar_count.setText(String.valueOf(l.exemplar_count));

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView series_count;
        TextView exemplar_count;
    }
}
