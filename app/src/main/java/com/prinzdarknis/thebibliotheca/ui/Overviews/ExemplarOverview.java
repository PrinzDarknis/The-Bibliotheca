package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

import java.util.UUID;

public class ExemplarOverview extends ListFragment {

    public static final String ARG_TITLE = "dynamicTitle";
    public static final String ARG_SERIES_ID = "id";

    private UUID filterSeries = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Arguments
        Bundle args = getArguments();
        if (args != null) {
            filterSeries = (UUID) args.getSerializable(ARG_SERIES_ID);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.overwiew, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_new:
                // Dialog for Name-Input
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(R.string.dialog_newExemplar);

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setView(input);

                dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (!text.equals("")) {
                            Exemplar exemplar = new Exemplar(ProgramLogic.getInstance().getActiveLibraryID(), text);
                            ProgramLogic.getInstance().createSaveExemplar(exemplar);
                            reloadList(); //Falls user über "Zurück-Taste" zurück kommt
                            MainActivity.openExemplar(exemplar);
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void reloadList() {
        adapter = new ExemplarListAdapter(ProgramLogic.getInstance().getExemplars(filterSeries), getContext());
        listView.setAdapter(adapter);
    }

    @Override
    protected boolean longClick(Object listItem) {
        deleteExemplar((Exemplar) listItem);
        return true;
    }

    @Override
    protected void shortClick(Object listItem) {
        MainActivity.openExemplar((Exemplar) listItem);
    }

    public void deleteExemplar(final Exemplar exemplar) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.dialog_deleteExemplar, exemplar.name));

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgramLogic.getInstance().deleteExemplar(exemplar.id);
                reloadList();
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
}
