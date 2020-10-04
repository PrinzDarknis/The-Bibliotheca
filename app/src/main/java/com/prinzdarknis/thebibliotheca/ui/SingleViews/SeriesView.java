package com.prinzdarknis.thebibliotheca.ui.SingleViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesView extends Fragment {

    public static final String ARG_TITLE = "dynamicTitle";
    public static final String ARG_SERIES = "series";

    private static final int PICK_IMAGE = 55;

    TextView title;
    ImageView image;
    EditText infoText;

    Button okButton;
    Button cancleButton;
    private String oldInfoText = "";

    Series series;
    private String imageName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_series_view, container, false);

        title = view.findViewById(R.id.title);
        image = view.findViewById(R.id.image);
        infoText = view.findViewById(R.id.infoText);

        //Buttons for infoText
        okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoTextEditOK();
            }
        });
        cancleButton = view.findViewById(R.id.cancleButton);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoTextEditCancle();
            }
        });
        setInfoTextEditable(false);

        Bundle args = getArguments();
        if (args != null) {
            series = (Series) args.getSerializable(ARG_SERIES);

            title.setText(series.name);
            infoText.setText(series.infoText);
            oldInfoText = series.infoText;
            imageName = series.image;

            Bitmap bm = ProgramLogic.getInstance().getImage(series);
            if (bm != null)
                image.setImageBitmap(bm);
            else
                image.setImageResource(R.drawable.no_image);
        }
        else {
            throw new IllegalArgumentException("no Arguments given.");
        }

        //onLongClick for Edit
        infoText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setInfoTextEditable(true);
                return true;
            }
        });

        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeTitle();
                return true;
            }
        });

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeImage();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        InfoAndRelation infoFragment = new InfoAndRelation(InfoAndRelation.Typ.Series, series.id);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.inner_fragment_container, infoFragment).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.show_exemplars, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_show:
                MainActivity.openExemplarsOfSeries(series);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                if (ProgramLogic.getInstance().saveImage(series, bm)) {
                    imageName = series.image;
                    saveSeries();
                    image.setImageBitmap(bm);
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), R.string.error_cantGetImage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setInfoTextEditable(boolean value) {
        //Disable but Clickable
        infoText.setCursorVisible(value);
        infoText.setFocusableInTouchMode(value);
        infoText.setFocusable(value);

        if (value) {
            okButton.setVisibility(View.VISIBLE);
            cancleButton.setVisibility(View.VISIBLE);
            infoText.setAlpha(1f);
        }
        else {
            okButton.setVisibility(View.GONE);
            cancleButton.setVisibility(View.GONE);
            infoText.setAlpha(0.5f);
        }
    }

    public void infoTextEditCancle() {
        infoText.setText(oldInfoText);
        setInfoTextEditable(false);
    }

    public void infoTextEditOK() {
        saveSeries();
        oldInfoText = series.infoText;
        setInfoTextEditable(false);
    }

    public void saveSeries() {
        //Load old from Database (evtl. new Infos and Relations)
        series = ProgramLogic.getInstance().getSingleSeries(series.id);

        series.name = title.getText().toString();
        series.infoText = infoText.getText().toString();
        series.image = imageName;

        ProgramLogic.getInstance().createSaveSeries(series);
    }

    public void changeTitle() {
        // Dialog for Name-Input
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.dialog_changeSeriesTitle);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(series.name);
        dialog.setView(input);

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (!text.equals("")) {
                    title.setText(text);
                    saveSeries();
                }
                else {
                    Toast.makeText(getContext(), R.string.dialog_newSeries_emptyInput, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton(R.string.dialog_negativ, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void changeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_selectPickture)), PICK_IMAGE);
    }
}
