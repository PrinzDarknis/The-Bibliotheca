package com.prinzdarknis.thebibliotheca.ui.SingleViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

import java.util.ArrayList;
import java.util.UUID;

public class RelationListAdapter extends BaseAdapter {

    private ArrayList<Relation> list;
    private final LayoutInflater inflater;
    private UUID mainID;

    public RelationListAdapter(ArrayList<Relation> list, Context context, UUID mainID) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.mainID = mainID;
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
            convertView = inflater.inflate(R.layout.row_relation, parent, false);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.image);
            holder.name = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        //write Values
        Relation r = list.get(position);
        String image;
        if (r.father.equals(mainID)) {
            //Child important
            holder.id = r.child;
            image = r.childImage;
            holder.name.setText(r.childName);
        }
        else {
            //Father important
            holder.id = r.father;
            image = r.fatherImage;
            holder.name.setText(r.fatherName);
        }

        Bitmap bm = ProgramLogic.getInstance().getImageByName(image);
        if (bm != null)
            holder.image.setImageBitmap(bm);
        else
            holder.image.setImageResource(R.drawable.no_image);

        return convertView;
    }

    public static UUID extractUUID(View view) {
        return ((RelationListAdapter.ViewHolder) view.getTag()).id;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
        UUID id;
    }
}
