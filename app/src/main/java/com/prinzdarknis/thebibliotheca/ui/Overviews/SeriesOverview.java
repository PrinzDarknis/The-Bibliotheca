package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

public class SeriesOverview extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                dialog.setTitle(R.string.dialog_newSeries);

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setView(input);

                dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (!text.equals("")) {
                            Series series = new Series(ProgramLogic.getInstance().getActiveLibraryID(), text);
                            ProgramLogic.getInstance().createSaveSeries(series);
                            reloadList(); //Falls user über "Zurück-Taste" zurück kommt
                            MainActivity.openSeries(series);
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void reloadList() {
        adapter = new SeriesListAdapter(ProgramLogic.getInstance().getSeries(), getContext());
        listView.setAdapter(adapter);
    }

    @Override
    protected boolean longClick(Object listItem) {
        deleteSeries((Series) listItem);
        return true;
    }

    @Override
    protected void shortClick(Object listItem) {
        MainActivity.openSeries((Series)listItem);
    }

    public void deleteSeries(final Series series) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.dialog_deleteSeries, series.name));

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder sureDialog = new AlertDialog.Builder(getContext());
                sureDialog.setTitle(R.string.dialog_deleteSeries_incl);

                TextView detailsText = new TextView(getContext());
                detailsText.setText(R.string.dialog_deleteSeries_incl_Details);
                sureDialog.setView(detailsText);

                sureDialog.setPositiveButton(R.string.dialog_deleteSeries_incl_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgramLogic.getInstance().deleteSeries(series.id, true);
                        reloadList();
                    }
                });

                //Negativ und Neutral getauscht, wegen Reihenfolge bei Anzeige
                sureDialog.setNegativeButton(R.string.dialog_deleteSeries_incl_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgramLogic.getInstance().deleteSeries(series.id, false);
                        reloadList();
                    }
                });

                sureDialog.setNeutralButton(R.string.dialog_negativ, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.cancel();
                sureDialog.show();
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
