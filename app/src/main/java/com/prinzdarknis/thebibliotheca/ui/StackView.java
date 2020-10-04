package com.prinzdarknis.thebibliotheca.ui;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.prinzdarknis.thebibliotheca.ui.SingleViews.InfoListAdapter;

import java.util.ArrayList;


public class StackView extends LinearLayout {

    public int itemTopMargin = 8;

    private BaseAdapter adapter;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;

    private ArrayList<View> views = new ArrayList<View>();

    public StackView(Context context) {
        super(context);
    }

    public StackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StackView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        adapter.registerDataSetObserver(observer);
        redraw();
    }

    public void setOnItemLongClickListener(final AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;

        for (int i = 0; i < views.size(); i++) {
            final View child = views.get(i);
            final int pos = i;
            child.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClickListener.onItemLongClick(null, child, pos, 0); //parent can'T be Used, because can't extending AdapterView (no extend of multiple classes)
                }
            });
        }
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

        for (int i = 0; i < views.size(); i++) {
            final View child = views.get(i);
            final int pos = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, child, pos, 0); //parent can'T be Used, because can't extending AdapterView (no extend of multiple classes)
                }
            });
        }
    }

    public void redraw() {
        removeAllViews();
        views.clear();
        int marginPx = (int)dpToPx(itemTopMargin);

        if(adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                View child = adapter.getView(i, null, this);

                //Margin
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)child.getLayoutParams();
                p.setMargins(0, marginPx, 0, 0);

                addView(child);
                views.add(child);
            }
        }

        setOnItemLongClickListener(onItemLongClickListener);
        setOnItemClickListener(onItemClickListener);

        requestLayout();
    }


    private DataSetObserver observer = new DataSetObserver() {
        /**
         * This method is called when the entire data set has changed
         */
        @Override
        public void onChanged() {
            super.onChanged();
            redraw();
        }
    };

    public float dpToPx(float dp) {
        Resources r = getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

}
