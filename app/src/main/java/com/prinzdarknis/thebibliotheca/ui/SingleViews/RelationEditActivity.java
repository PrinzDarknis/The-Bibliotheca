package com.prinzdarknis.thebibliotheca.ui.SingleViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.prinzdarknis.thebibliotheca.R;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.managment.ProgramLogic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RelationEditActivity extends AppCompatActivity {

    final static String RELATION = "relation";
    final static String MAINID = "mainID";
    final static String TYP = "typ";

    private AutoCompleteTextView parentName;
    private AutoCompleteTextView childName;
    private Spinner relationSpinner;

    private Relation relation;
    private UUID mainID;
    private String mainName;
    private Typ typ;
    private HashMap<String, UUID> titleMap = new HashMap<String, UUID>();
    private HashMap<String, Integer> relationMap = new HashMap<String, Integer>();

    public static Intent factory(Context context, Relation relation, UUID mainID, Typ typ) {
        Intent i = new Intent(context, RelationEditActivity.class);
        i.putExtra(RELATION, relation);
        i.putExtra(MAINID, mainID);
        i.putExtra(TYP, typ);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_edit);

        parentName = findViewById(R.id.parentName);
        childName = findViewById(R.id.childName);
        relationSpinner = findViewById(R.id.relationSpinner);

        //Argument Typ
        Intent intent = getIntent();
        typ = (Typ) intent.getSerializableExtra(TYP);
        relation = (Relation) intent.getSerializableExtra(RELATION);
        mainID = (UUID) intent.getSerializableExtra(MAINID);
        String[] temp = null;

        //depending on Typ
        switch (typ) {
            case Series:
                temp = ProgramLogic.getInstance().getSeriesNameAndImage(mainID);
                for (Series s : ProgramLogic.getInstance().getSeries()) {
                    titleMap.put(s.name, s.id);
                }
                break;
            case Exemplar:
                temp = ProgramLogic.getInstance().getExemplarNameAndImage(mainID);
                for (Exemplar e : ProgramLogic.getInstance().getExemplars()) {
                    titleMap.put(e.name, e.id);
                }
                break;
        }
        mainName = temp[0];

        //Adapter
        temp = Arrays.copyOf(titleMap.keySet().toArray(), titleMap.size(), String[].class);
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
        parentName.setAdapter(titleAdapter);
        childName.setAdapter(titleAdapter);

        for (Map.Entry<Integer, String> p : ProgramLogic.getInstance().getRelations()) {
            relationMap.put(p.getValue(), p.getKey());
        }

        temp = Arrays.copyOf(relationMap.keySet().toArray(), relationMap.size(), String[].class);
        ArrayAdapter<String> relationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temp);
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationSpinner.setAdapter(relationAdapter);

        //old Values
        if (relation != null) {
            if (relation.child.equals(mainID))
                childName.setEnabled(false);
            else
                parentName.setEnabled(false);

            childName.setText(relation.childName, false);
            parentName.setText(relation.fatherName, false);

            //get Position of Relation in Spinner
            for (int i = 0; i < relationAdapter.getCount(); i++) {
                if (relationMap.get(relationAdapter.getItem(i)) == relation.relationTyp) {
                    relationSpinner.setSelection(i);
                    break;
                }
            }
        }
        else {
            childName.setText(mainName, false);
            childName.setEnabled(false);
            parentName.setText("", false);
            relationSpinner.setSelection(0);
        }
    }

    public void cancle(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        //Check
        String newChildName = childName.getText().toString();
        if (!titleMap.containsKey(newChildName)) {
            Toast.makeText(this, getString(R.string.activoty_relationedit_unknowntitle, newChildName), Toast.LENGTH_SHORT).show();
            return;
        }

        String newParentName = parentName.getText().toString();
        if (!titleMap.containsKey(newParentName)) {
            Toast.makeText(this, getString(R.string.activoty_relationedit_unknowntitle, newParentName), Toast.LENGTH_SHORT).show();
            return;
        }

        if (newChildName.equals(newParentName)) {
            Toast.makeText(this, R.string.activoty_relationedit_sametitle, Toast.LENGTH_SHORT).show();
            return;
        }

        //delete Old (no ID for Overwrite)
        if (relation != null) {
            switch (typ){
                case Series:
                    ProgramLogic.getInstance().deleteRelation_Series(relation);
                    break;
                case Exemplar:
                    ProgramLogic.getInstance().deleteRelation_Exemplar(relation);
                    break;
            }
        }

        relation = new Relation(
                relationMap.get((String)relationSpinner.getSelectedItem()),
                titleMap.get(newParentName),
                titleMap.get(newChildName)
        );
        switch (typ){
            case Series:
                ProgramLogic.getInstance().writeRelation_Series(relation);
                break;
            case Exemplar:
                ProgramLogic.getInstance().writeRelation_Exemplar(relation);
                break;
        }

        setResult(RESULT_OK);
        finish();
    }

    public void swap(View view) {
        String temp = parentName.getText().toString();
        parentName.setText(childName.getText().toString(), false);
        childName.setText(temp, false);

        //parentName.clearFocus();
        //childName.clearFocus();

        parentName.setEnabled(!parentName.isEnabled());
        childName.setEnabled(!childName.isEnabled());
    }

    enum Typ {
        Series,
        Exemplar
    }
}
