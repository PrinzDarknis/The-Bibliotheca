package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

import java.util.ArrayList;

public class ExemplarListAdapter extends BaseAdapter {

    private ArrayList<Exemplar> list;
    private final LayoutInflater inflater;

    public ExemplarListAdapter(ArrayList<Exemplar> list, Context context) {
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
            convertView = inflater.inflate(R.layout.row_exemplar, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.infoText = convertView.findViewById(R.id.infoText);
            holder.state = convertView.findViewById(R.id.state);
            holder.image = convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        //write Values
        Exemplar e = list.get(position);
        holder.name.setText(e.name);
        holder.infoText.setText(e.infoText);

        Bitmap image = ProgramLogic.getInstance().getImage(e);
        if (image != null)
            holder.image.setImageBitmap(image);
        else
            holder.image.setImageResource(R.drawable.no_image);

        //State with colored Bullet point
        holder.state.setText("\u25CF " +e.state.name);
        holder.state.setTextColor(getStateColor(e.state.color));


        return convertView;
    }

    public static int getStateColor(String colorString) {
        try {
            return Color.parseColor(colorString);
        }
        catch (IllegalArgumentException _ex) {
            return Color.RED; //default
        }
    }

    static class ViewHolder {
        TextView name;
        TextView infoText;
        TextView state;
        ImageView image;
    }
}
