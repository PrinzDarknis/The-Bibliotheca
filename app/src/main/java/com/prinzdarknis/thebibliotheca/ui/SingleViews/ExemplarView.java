package com.prinzdarknis.thebibliotheca.ui.SingleViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.State;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;
import com.prinzdarknis.thebibliotheca.ui.Overviews.ExemplarListAdapter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExemplarView extends Fragment {

    public static final String ARG_TITLE = "dynamicTitle";
    public static final String ARG_EXEMPLAR = "exemplar";

    private static final int PICK_IMAGE = 56;

    TextView title;
    TextView state;
    ImageView image;
    EditText infoText;

    ConstraintLayout seriesBox;
    ImageView seriesImage;
    TextView seriesName;

    Button okButton;
    Button cancleButton;
    private String oldInfoText = "";

    Exemplar exemplar;
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
        View view = inflater.inflate(R.layout.fragment_exemplar_view, container, false);

        title = view.findViewById(R.id.title);
        image = view.findViewById(R.id.image);
        infoText = view.findViewById(R.id.infoText);
        state = view.findViewById(R.id.state);

        seriesBox = view.findViewById(R.id.seriesBox);
        seriesImage = view.findViewById(R.id.seriesImage);
        seriesName = view.findViewById(R.id.seriesName);

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
            exemplar = (Exemplar) args.getSerializable(ARG_EXEMPLAR);

            title.setText(exemplar.name);
            infoText.setText(exemplar.infoText);
            oldInfoText = exemplar.infoText;
            imageName = exemplar.image;

            Bitmap bm = ProgramLogic.getInstance().getImage(exemplar);
            if (bm != null)
                image.setImageBitmap(bm);
            else
                image.setImageResource(R.drawable.no_image);

            if (exemplar.state == null)
                exemplar.state = ProgramLogic.getInstance().getDefaultState();

            visualState();
            visualSeries();
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

        state.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeState();
                return true;
            }
        });

        seriesBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                changeSeries();
                return true;
            }
        });

        seriesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exemplar.series != null) {
                    Series s = ProgramLogic.getInstance().getSingleSeries(exemplar.series);
                    MainActivity.openSeries(s);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        InfoAndRelation infoFragment = new InfoAndRelation(InfoAndRelation.Typ.Exemplar, exemplar.id);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.inner_fragment_container, infoFragment).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.change_associated_series, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_changeSeries:
                changeSeries();
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
                if (ProgramLogic.getInstance().saveImage(exemplar, bm)) {
                    imageName = exemplar.image;
                    saveExemplar();
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
        saveExemplar();
        oldInfoText = exemplar.infoText;
        setInfoTextEditable(false);
    }

    public void saveExemplar() {
        State newState = exemplar.state; //save befor reload
        UUID newSeries = exemplar.series;

        //Load old from Database (evtl. new Infos and Relations)
        exemplar = ProgramLogic.getInstance().getSingleExemplar(exemplar.id);

        exemplar.name = title.getText().toString();
        exemplar.infoText = infoText.getText().toString();
        exemplar.image = imageName;
        exemplar.state = newState;
        exemplar.series = newSeries;

        ProgramLogic.getInstance().createSaveExemplar(exemplar);
    }

    public void changeTitle() {
        // Dialog for Name-Input
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.dialog_changeExemplarTitle);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(exemplar.name);
        dialog.setView(input);

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (!text.equals("")) {
                    title.setText(text);
                    saveExemplar();
                }
                else {
                    Toast.makeText(getContext(), R.string.dialog_newExemplar_emptyInput, Toast.LENGTH_SHORT).show();
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

    public void changeSeries() {
        //getSeries
        final HashMap<String, UUID> seriesMap = new HashMap<String, UUID>();
        for (Series s : ProgramLogic.getInstance().getSeries()) {
            seriesMap.put(s.name, s.id);
        }

        //Adapter
        String[] temp = Arrays.copyOf(seriesMap.keySet().toArray(), seriesMap.size(), String[].class);
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, temp);

        //Dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.dialog_changeAssociatedSeries);


        final AutoCompleteTextView input = new AutoCompleteTextView(getContext());
        input.setAdapter(titleAdapter);
        input.setText(seriesName.getText().toString(), false);
        dialog.setView(input);

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (seriesMap.containsKey(text)) {
                    exemplar.series = seriesMap.get(text);
                    saveExemplar();
                    visualSeries();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.activoty_relationedit_unknowntitle, text), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNeutralButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exemplar.series = null;
                saveExemplar();
                visualSeries();
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

    public void changeState() {
        final ArrayList<State> states = ProgramLogic.getInstance().getStates();

        PopupMenu selector = new PopupMenu(getContext(), state);
        for (int index = 0; index < states.size(); index++)
            selector.getMenu().add(0, index, states.get(index).id, states.get(index).name);

        selector.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                exemplar.state = states.get(item.getItemId());
                visualState();
                saveExemplar();
                return true;
            }
        });

        selector.show();
    }

    public void visualState() {
        state.setText(exemplar.state.name);
        state.setTextColor(ExemplarListAdapter.getStateColor(exemplar.state.color));
    }

    public void visualSeries() {
        if (exemplar.series != null) {
            String[] temp = ProgramLogic.getInstance().getSeriesNameAndImage(exemplar.series);
            seriesName.setText(temp[0]);

            Bitmap bm = ProgramLogic.getInstance().getImageByName(temp[1]);
            if (bm != null)
                seriesImage.setImageBitmap(bm);
            else
                seriesImage.setImageResource(R.drawable.no_image);

            seriesBox.setVisibility(View.VISIBLE);
        }
        else {
            seriesBox.setVisibility(View.GONE);
            seriesName.setText("");
        }
    }

}
