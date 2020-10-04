package com.prinzdarknis.thebibliotheca.ui.Overviews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

public class Libraries extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void reloadList() {
        adapter = new LibraryListAdapter(ProgramLogic.getInstance().getLibraries(), getContext());
        listView.setAdapter(adapter);
    }

    @Override
    protected boolean longClick(Object listItem) {
        deleteLibrary((Library)listItem);
        return true;
    }

    @Override
    protected void shortClick(Object listItem) {
        selectLibrary((Library)listItem);
    }

    private void deleteLibrary(final Library library) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(getString(R.string.dialog_deleteLibrary, library.name));

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder sureDialog = new AlertDialog.Builder(getContext());
                sureDialog.setTitle(R.string.dialog_delete_title);

                TextView detailsText = new TextView(getContext());
                detailsText.setText(R.string.dialog_deleteLibrary_Details);
                sureDialog.setView(detailsText);

                sureDialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgramLogic.getInstance().deleteLibrary(library);
                        reloadList();
                    }
                });

                sureDialog.setNegativeButton(R.string.dialog_negativ, new DialogInterface.OnClickListener() {
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

    private void selectLibrary(Library library) {
        ProgramLogic.getInstance().setActiveLibrary(library);
        MainActivity.setBibNameInNav(library.name);
        Navigation.findNavController(this.getView()).navigate(R.id.nav_series);
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
                dialog.setTitle(R.string.dialog_newLibrary);

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog.setView(input);

                dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (!text.equals("")) {
                            ProgramLogic.getInstance().createSaveLibrary(
                                    new Library(text)
                            );
                            reloadList();
                        }
                        else {
                            Toast.makeText(getContext(), R.string.dialog_newLibrary_emptyInput, Toast.LENGTH_SHORT).show();
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
}
