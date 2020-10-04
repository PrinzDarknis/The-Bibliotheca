package com.prinzdarknis.thebibliotheca.ui.SingleViews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.MainActivity;
import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.AdditionalInfo;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;
import com.prinzdarknis.thebibliotheca.ui.StackView;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoAndRelation extends Fragment {

    static final int REQUESTCODE = 666;

    public enum Typ {
        Series,
        Exemplar
    }

    private LayoutInflater inflater;
    private Context context;
    private Typ typ;
    private UUID mainID;

    private LinearLayout infoBox;
    private StackView infoList;
    private InfoListAdapter infoAdapter;

    private LinearLayout parentBox;
    private StackView parentList;
    private RelationListAdapter parentAdapter;

    private LinearLayout childBox;
    private StackView childList;
    private RelationListAdapter childAdapter;

    private LinearLayout spinnoffBox;
    private StackView spinnoffList;
    private RelationListAdapter spinnoffAdapter;

    public InfoAndRelation(Typ typ, UUID mainID) {
        this.typ = typ;
        this.mainID = mainID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.context = getContext();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_and_relation, container, false);

        infoBox = view.findViewById(R.id.infoBox);
        infoList = view.findViewById(R.id.infoList);

        parentBox = view.findViewById(R.id.parentBox);
        parentList = view.findViewById(R.id.parentList);

        childBox = view.findViewById(R.id.childBox);
        childList = view.findViewById(R.id.childList);

        spinnoffBox = view.findViewById(R.id.spinnoffBox);
        spinnoffList = view.findViewById(R.id.spinnoffList);

        //Load Data
        reloadInfos();
        reloadRelations();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.info_and_relation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_new_info:
                infoDialog(null);
                return true;
            case R.id.action_new_relation:
                relationDialog(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE) {
            if (resultCode == Activity.RESULT_OK) {
                reloadRelations();
            }
        }
    }

    private void reloadInfos() {
        ArrayList<AdditionalInfo> infos = ProgramLogic.getInstance().getInfos_Series(mainID);

        if (infos.size() > 0) {
            infoBox.setVisibility(View.VISIBLE);
            infoAdapter = new InfoListAdapter(infos, context);
            infoList.setAdapter(infoAdapter);

            //Events
            infoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    infoDialog((AdditionalInfo) infoAdapter.getItem(position));
                    return true;
                }
            });
        }
        else {
            infoBox.setVisibility(View.GONE);
        }
    }

    private void infoDialog(final AdditionalInfo info) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.dialog_infoTitle);
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        dialog.setView(dialogView);

        final EditText typInput = dialogView.findViewById(R.id.typInput);
        final EditText textInput = dialogView.findViewById(R.id.textInput);

        dialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Evaluation
                String newTyp = typInput.getText().toString();
                String newText = textInput.getText().toString();

                if (newTyp.equals("")) {
                    Toast.makeText(context, R.string.dialog_infoTyp_emptyInput, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newText.equals("")) {
                    Toast.makeText(context, R.string.dialog_infoText_emptyInput, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Changes
                ArrayList<AdditionalInfo> list;
                if (infoAdapter != null)
                    list = infoAdapter.getList();
                else
                    list = new ArrayList<AdditionalInfo>();

                if (info != null) {
                    info.typ = newTyp;
                    info.text = newText;
                }
                else {
                    list.add(new AdditionalInfo(newTyp, newText));
                }

                //Save
                switch (typ) {
                    case Series:
                        ProgramLogic.getInstance().writeInfos_Series(mainID, list);
                        break;
                    case Exemplar:
                        ProgramLogic.getInstance().writeInfos_Exemplar(mainID, list);
                        break;
                }

                reloadInfos();
            }
        });

        dialog.setNegativeButton(R.string.dialog_negativ, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (info != null) {
            typInput.setText(info.typ);
            textInput.setText(info.text);

            dialog.setNeutralButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder sureDialog = new AlertDialog.Builder(context);
                    sureDialog.setTitle(R.string.dialog_delete_title);

                    sureDialog.setPositiveButton(R.string.dialog_positiv, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (typ) {
                                case Series:
                                    ProgramLogic.getInstance().deleteInfos_Series(info.id);
                                    break;
                                case Exemplar:
                                    ProgramLogic.getInstance().deleteInfos_Exemplar(info.id);
                                    break;
                            }
                            reloadInfos();
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
        }

        dialog.show();
    }

    private void reloadRelations() {
        ArrayList<Relation> parents = null, childs = null, spinnoffs = null;

        switch (typ) {
            case Series:
                Series s = ProgramLogic.getInstance().getSingleSeries(mainID);
                if (s != null) {
                    parents = s.relationParents;
                    childs = s.relationChildren;
                    spinnoffs = s.spinnoffs;
                }
                break;
            case Exemplar:
                Exemplar e = ProgramLogic.getInstance().getSingleExemplar(mainID);
                if (e != null) {
                    parents = e.relationParents;
                    childs = e.relationChildren;
                    spinnoffs = e.spinnoffs;
                }
                break;
        }



        if (parents != null && parents.size() > 0) {
            parentBox.setVisibility(View.VISIBLE);
            parentAdapter = new RelationListAdapter(parents, context, mainID);
            parentList.setAdapter(parentAdapter);

            //Events
            parentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    relationDialog((Relation) parentAdapter.getItem(position));
                    return true;
                }
            });

            parentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    changePage(RelationListAdapter.extractUUID(view));
                }
            });
        }
        else {
            parentBox.setVisibility(View.GONE);
        }

        if (childs != null && childs.size() > 0) {
            childBox.setVisibility(View.VISIBLE);
            childAdapter = new RelationListAdapter(childs, context, mainID);
            childList.setAdapter(childAdapter);

            //Events
            childList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    relationDialog((Relation) childAdapter.getItem(position));
                    return true;
                }
            });

            childList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    changePage(RelationListAdapter.extractUUID(view));
                }
            });
        }
        else {
            childBox.setVisibility(View.GONE);
        }

        if (spinnoffs != null && spinnoffs.size() > 0) {
            spinnoffBox.setVisibility(View.VISIBLE);
            spinnoffAdapter = new RelationListAdapter(spinnoffs, context, mainID);
            spinnoffList.setAdapter(spinnoffAdapter);

            //Events
            spinnoffList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    relationDialog((Relation) spinnoffAdapter.getItem(position));
                    return true;
                }
            });

            spinnoffList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    changePage(RelationListAdapter.extractUUID(view));
                }
            });
        }
        else {
            spinnoffBox.setVisibility(View.GONE);
        }
    }

    private void relationDialog(Relation relation) {
        RelationEditActivity.Typ passTyp = null;
        switch (typ) {
            case Series:
                passTyp = RelationEditActivity.Typ.Series;
                break;
            case Exemplar:
                passTyp = RelationEditActivity.Typ.Exemplar;
                break;
        }
        Intent intent = RelationEditActivity.factory(context, relation, mainID, passTyp);
        startActivityForResult(intent, REQUESTCODE);
    }

    private void changePage(UUID id) {
        switch (typ) {
            case Series:
                Series series = ProgramLogic.getInstance().getSingleSeries(id);
                MainActivity.openSeries(series);
                break;
            case Exemplar:
                Exemplar exemplar = ProgramLogic.getInstance().getSingleExemplar(id);
                MainActivity.openExemplar(exemplar);
                break;
        }
    }
}
