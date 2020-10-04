package com.prinzdarknis.thebibliotheca.database.v1;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.prinzdarknis.thebibliotheca.dataScheme.v1.AdditionalInfo;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.State;
import com.prinzdarknis.thebibliotheca.database.IDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class DBAndroidTest {
    private DBAndroid dbManager;
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DBAndroid.initialize(context);
        dbManager = DBAndroid.getInstance();

        dbManager.createSampleData();
    }

    @After
    public void cleadUp() {
        dbManager.deleteDB();
    }

    //Management
    @Test
    public void deleteDB() {
        dbManager.deleteDB();

        assertFalse(context.getDatabasePath(DBAndroid.DATABASE_NAME).exists());
    }

    //Libraries
    @Test
    public void getLibraries() {
        ArrayList<Library> libaries = dbManager.getLibraries();
        assertEquals(3, libaries.size());

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), libaries.get(0).id);
        assertEquals("Bücher", libaries.get(0).name);

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), libaries.get(1).id);
        assertEquals("DVD/Blu-rey", libaries.get(1).name);

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), libaries.get(2).id);
        assertEquals("Novel", libaries.get(2).name);
    }

    @Test
    public void createSaveLibrary() {
        Library l = new Library("C_Test");
        dbManager.createSaveLibrary(l);

        ArrayList<Library> libaries = dbManager.getLibraries();

        assertEquals(l.id, libaries.get(1).id);
        assertEquals("C_Test", libaries.get(1).name);
    }

    @Test
    public void deleteLibrary() {
        dbManager.deleteLibrary(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ArrayList<Library> libaries = dbManager.getLibraries();
        assertEquals(2, libaries.size());

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), libaries.get(0).id);
        assertEquals("DVD/Blu-rey", libaries.get(0).name);

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), libaries.get(1).id);
        assertEquals("Novel", libaries.get(1).name);
    }

    //Series
    @Test
    public void getSeries() {
        ArrayList<Series> series = dbManager.getSeries(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        assertEquals(2, series.size());

        //Daten + Relations
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000017"), series.get(0).id);
        assertEquals("Das Geheimnis von Askir", series.get(0).name);
        assertEquals("Askir ist eine Weltmetropole, das Zentrum der Welt von Richard Schwartz. Das Epos um die Abenteuer in Askir hat eine riesige Fangemeinde und hat bereits zahlreiche Phantastik-Preise eingestrichen.",
                series.get(0).infoText);
        assertEquals("00000000-0000-0000-0000-000000000017.jpg", series.get(0).image);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), series.get(0).library);
        assertEquals(0, series.get(0).additionalInfos.size());
        assertEquals(0, series.get(0).relationParents.size());
        assertEquals(1, series.get(0).relationChildren.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000017"), series.get(0).relationChildren.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000018"), series.get(0).relationChildren.get(0).child);
        assertEquals(0, series.get(0).spinnoffs.size());

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000018"), series.get(1).id);
        assertEquals("Die Götterkriege", series.get(1).name);
        assertEquals("", series.get(1).infoText);
        assertEquals("00000000-0000-0000-0000-000000000018.jpg", series.get(1).image);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), series.get(1).library);
        assertEquals(0, series.get(1).additionalInfos.size());
        assertEquals(1, series.get(1).relationParents.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000017"), series.get(1).relationParents.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000018"), series.get(1).relationParents.get(0).child);
        assertEquals(0, series.get(1).relationChildren.size());
        assertEquals(0, series.get(1).spinnoffs.size());

        //Additional Info
        series = dbManager.getSeries(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        assertEquals(3, series.size());

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), series.get(1).id);
        assertEquals(2, series.get(1).additionalInfos.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000025"), series.get(1).additionalInfos.get(0).id);
        assertEquals("Autor", series.get(1).additionalInfos.get(0).typ);
        assertEquals("Dojyomaru", series.get(1).additionalInfos.get(0).text);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000026"), series.get(1).additionalInfos.get(1).id);
        assertEquals("Verlag", series.get(1).additionalInfos.get(1).typ);
        assertEquals("Seven Seas Entertainment", series.get(1).additionalInfos.get(1).text);
    }

    @Test
    public void createSaveSeries() {
        //Insert
        Series s = new Series(UUID.fromString("00000000-0000-0000-0000-000000000003"), "C_testi");
        s.infoText = "Miau Miau";
        s.image = "bild.jpg";

        s.additionalInfos.add(new AdditionalInfo(
                "test_typ",
                "Test_text"
        ));

        Series father = new Series(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Z_fasther");
        Series child = new Series(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Z_child");
        Series spinnoff = new Series(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Z_spinnoff");

        s.relationParents.add(new Relation(-1, father.id, s.id));
        s.relationParents.add(new Relation(-1, s.id, child.id));
        s.spinnoffs.add(new Relation(-2, s.id, spinnoff.id));

        dbManager.createSaveSeries(father);
        dbManager.createSaveSeries(child);
        dbManager.createSaveSeries(spinnoff);
        dbManager.createSaveSeries(s);

        //Test
        ArrayList<Series> series = dbManager.getSeries(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        assertEquals(7, series.size());

        assertEquals(s.id, series.get(1).id);
        assertEquals("C_testi", series.get(1).name);
        assertEquals("Miau Miau", series.get(1).infoText);
        assertEquals("bild.jpg", series.get(1).image);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), series.get(1).library);

        assertEquals(1, series.get(1).additionalInfos.size());
        assertEquals("test_typ", series.get(1).additionalInfos.get(0).typ);
        assertEquals("Test_text", series.get(1).additionalInfos.get(0).text);

        assertEquals(1, series.get(1).relationParents.size());
        assertEquals(father.id, series.get(1).relationParents.get(0).father);
        assertEquals(s.id, series.get(1).relationParents.get(0).child);

        assertEquals(1, series.get(1).relationChildren.size());
        assertEquals(s.id, series.get(1).relationChildren.get(0).father);
        assertEquals(child.id, series.get(1).relationChildren.get(0).child);

        assertEquals(1, series.get(1).spinnoffs.size());
        assertEquals(s.id, series.get(1).spinnoffs.get(0).father);
        assertEquals(spinnoff.id, series.get(1).spinnoffs.get(0).child);
    }

    @Test
    public void deleteSeries() {
        //ohne Exemplare
        dbManager.deleteSeries(UUID.fromString("00000000-0000-0000-0000-000000000017"), false);

        ArrayList<Series> series = dbManager.getSeries(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(1, series.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000018"), series.get(0).id);

        ArrayList<Exemplar> examplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(6, examplars.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000019"), examplars.get(0).id);
        assertEquals("Askir: Die komplette Saga 1", examplars.get(0).name);
        assertEquals(null, examplars.get(0).series);

        //mit Exemplare
        dbManager.deleteSeries(UUID.fromString("00000000-0000-0000-0000-000000000018"), true);

        series = dbManager.getSeries(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        //assertEquals(0, series.size());

        examplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertEquals(3, examplars.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000019"), examplars.get(0).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000020"), examplars.get(1).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000021"), examplars.get(2).id);
    }

    //Exemplar
    @Test
    public void getExemplars() {
        ArrayList<Exemplar> exemplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        assertEquals(6, exemplars.size());

        //Variable
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000007"), exemplars.get(0).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), exemplars.get(0).library);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000004"), exemplars.get(0).series);
        assertEquals("How a Realist Hero Rebuilt the Kingdom Vol. 1", exemplars.get(0).name);
        assertEquals("", exemplars.get(0).infoText);
        assertEquals("00000000-0000-0000-0000-000000000007.jpg", exemplars.get(0).image);
        assertEquals(3, exemplars.get(0).state.id);

        //Info
        assertEquals(5, exemplars.get(0).additionalInfos.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000014"), exemplars.get(0).additionalInfos.get(0).id);

        //Parents/Childs
        assertEquals(1, exemplars.get(1).relationParents.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000007"), exemplars.get(1).relationParents.get(0).father);
        assertEquals(1, exemplars.get(1).relationChildren.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000009"), exemplars.get(1).relationChildren.get(0).child);
    }

    @Test
    public void getExemplars_Series() {
        ArrayList<Exemplar> exemplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000017"));

        assertEquals(3, exemplars.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000019"), exemplars.get(0).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000020"), exemplars.get(1).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000021"), exemplars.get(2).id);
    }

    @Test
    public void createSaveExemplar() {
        Exemplar e = new Exemplar(
                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                "Test Bookworm"
        );
        e.image = "Book.png";
        e.infoText = "";
        e.series = UUID.fromString("00000000-0000-0000-0000-000000000005");
        e.spinnoffs.add(new Relation(
                -2,
                UUID.fromString("00000000-0000-0000-0000-000000000010"),
                e.id
        ));
        dbManager.createSaveExemplar(e);

        //Test
        ArrayList<Exemplar> exemplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        assertEquals(7, exemplars.size());
        assertEquals(e.id, exemplars.get(6).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000003"), exemplars.get(6).library);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000005"), exemplars.get(6).series);
        assertEquals("Test Bookworm", exemplars.get(6).name);
        assertEquals("", exemplars.get(6).infoText);
        assertEquals("Book.png", exemplars.get(6).image);

        assertEquals(0, exemplars.get(6).relationParents.size());
        assertEquals(0, exemplars.get(6).relationChildren.size());
        assertEquals(1, exemplars.get(6).spinnoffs.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000010"), exemplars.get(6).spinnoffs.get(0).father);
    }

    @Test
    public void deleteExemplar() {
        dbManager.deleteExemplar(UUID.fromString("00000000-0000-0000-0000-000000000010"));

        ArrayList<Exemplar> exemplars = dbManager.getExemplars(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        assertEquals(5, exemplars.size());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000007"), exemplars.get(0).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000008"), exemplars.get(1).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000009"), exemplars.get(2).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000011"), exemplars.get(3).id);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000012"), exemplars.get(4).id);
    }

    //Infos
    @Test
    public void getInfos() {
        //Series
        ArrayList<AdditionalInfo> infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000004") , IDatabase.InfoTable.SeriesAdditionalInfo);
        assertEquals(2, infos.size());
        assertEquals("Autor", infos.get(0).typ);
        assertEquals("Dojyomaru", infos.get(0).text);
        assertEquals("Verlag", infos.get(1).typ);
        assertEquals("Seven Seas Entertainment", infos.get(1).text);

        //Exemplar
        infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000007") , IDatabase.InfoTable.ExemplarAdditionalInfo);
        assertEquals(5, infos.size());
        assertEquals("Autor", infos.get(0).typ);
        assertEquals("Dojyomaru", infos.get(0).text);
        assertEquals("Erscheinungsdatum", infos.get(1).typ);
        assertEquals("08.10.2018", infos.get(1).text);
        assertEquals("IBAN-10", infos.get(2).typ);
        assertEquals("1626929076", infos.get(2).text);
        assertEquals("IBAN-13", infos.get(3).typ);
        assertEquals("978-1626929074", infos.get(3).text);
        assertEquals("Verlag", infos.get(4).typ);
        assertEquals("Seven Seas Entertainment", infos.get(4).text);
    }

    @Test
    public void writeInfos() {
        //Series
        ArrayList<AdditionalInfo> infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000004"), IDatabase.InfoTable.SeriesAdditionalInfo);
        infos.add(new AdditionalInfo(
                "Test_typ",
                "Test_text"
        ));
        dbManager.writeInfos(UUID.fromString("00000000-0000-0000-0000-000000000004"), IDatabase.InfoTable.SeriesAdditionalInfo, infos);

        //Test
        infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000004"), IDatabase.InfoTable.SeriesAdditionalInfo);
        assertEquals(3, infos.size());
        assertEquals("Test_typ", infos.get(1).typ);
        assertEquals("Test_text", infos.get(1).text);

        //Exemplar
        infos = new ArrayList<AdditionalInfo>();
        infos.add(new AdditionalInfo(
                "Test_typ",
                "Test_text"
        ));
        dbManager.writeInfos(UUID.fromString("00000000-0000-0000-0000-000000000007"), IDatabase.InfoTable.ExemplarAdditionalInfo, infos);

        //Test
        infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000007"), IDatabase.InfoTable.ExemplarAdditionalInfo);
        assertEquals(1, infos.size());
        assertEquals("Test_typ", infos.get(0).typ);
        assertEquals("Test_text", infos.get(0).text);
    }

    @Test
    public void deleteInfos() {
        //Series
        dbManager.deleteInfo(UUID.fromString("00000000-0000-0000-0000-000000000025"), IDatabase.InfoTable.SeriesAdditionalInfo);

        ArrayList<AdditionalInfo> infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000004") , IDatabase.InfoTable.SeriesAdditionalInfo);
        assertEquals(1, infos.size());
        assertEquals("Verlag", infos.get(0).typ);
        assertEquals("Seven Seas Entertainment", infos.get(0).text);

        //Exemplar
        dbManager.deleteInfo(UUID.fromString("00000000-0000-0000-0000-000000000027"), IDatabase.InfoTable.ExemplarAdditionalInfo);

        infos = dbManager.getInfos(UUID.fromString("00000000-0000-0000-0000-000000000007") , IDatabase.InfoTable.ExemplarAdditionalInfo);
        assertEquals(4, infos.size());
        assertEquals("Autor", infos.get(0).typ);
        assertEquals("Dojyomaru", infos.get(0).text);
        assertEquals("Erscheinungsdatum", infos.get(1).typ);
        assertEquals("08.10.2018", infos.get(1).text);
        assertEquals("IBAN-10", infos.get(2).typ);
        assertEquals("1626929076", infos.get(2).text);
        assertEquals("Verlag", infos.get(3).typ);
        assertEquals("Seven Seas Entertainment", infos.get(3).text);
    }

    //Relations
    @Test
    public void getRelations() {
        //Series
        ArrayList<Relation> relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000017"), IDatabase.RelationTable.RelationSeries);
        assertEquals(1, relations.size());
        assertEquals(-1, relations.get(0).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000017"), relations.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000018"), relations.get(0).child);

        //Exemplar
        relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000008"), IDatabase.RelationTable.RelationExemplar);
        assertEquals(2, relations.size());
        assertEquals(-1, relations.get(0).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000007"), relations.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000008"), relations.get(0).child);
        assertEquals(-1, relations.get(1).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000008"), relations.get(1).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000009"), relations.get(1).child);
    }

    @Test
    public void writeRelation() {
        //Series
        Relation r = new Relation(
                -2,
                UUID.fromString("00000000-0000-0000-0000-000000000005"),
                UUID.fromString("00000000-0000-0000-0000-000000000006")
        );
        dbManager.writeRelation(IDatabase.RelationTable.RelationSeries, r);

        //Test
        ArrayList<Relation> relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000005"), IDatabase.RelationTable.RelationSeries);
        assertEquals(1, relations.size());
        assertEquals(-2, relations.get(0).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000005"), relations.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000006"), relations.get(0).child);

        //Exemplar
        r = new Relation(
                -2,
                UUID.fromString("00000000-0000-0000-0000-000000000011"),
                UUID.fromString("00000000-0000-0000-0000-000000000024")
        );
        dbManager.writeRelation(IDatabase.RelationTable.RelationExemplar, r);

        //Test
        relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000011"), IDatabase.RelationTable.RelationExemplar);
        assertEquals(3, relations.size());
        assertEquals(-2, relations.get(2).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000011"), relations.get(2).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000024"), relations.get(2).child);
    }

    @Test
    public void deleteRelation() {
        //Series
        dbManager.deleteRelation(IDatabase.RelationTable.RelationSeries, new Relation(
                -1,
                UUID.fromString("00000000-0000-0000-0000-000000000017"),
                UUID.fromString("00000000-0000-0000-0000-000000000018")
        ));

        //Test
        ArrayList<Relation> relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000017"), IDatabase.RelationTable.RelationSeries);
        assertEquals(0, relations.size());

        //Exemplar
        dbManager.deleteRelation(IDatabase.RelationTable.RelationExemplar, new Relation(
                -1,
                UUID.fromString("00000000-0000-0000-0000-000000000008"),
                UUID.fromString("00000000-0000-0000-0000-000000000009")
        ));

        //Test
        relations = dbManager.getRelations(UUID.fromString("00000000-0000-0000-0000-000000000009"), IDatabase.RelationTable.RelationExemplar);
        assertEquals(1, relations.size());
        assertEquals(-1, relations.get(0).relationTyp);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000009"), relations.get(0).father);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000010"), relations.get(0).child);
    }

    //RelationTyps
    @Test
    public void getRelationTypName() {
        ArrayList<Map.Entry<Integer, String>> typs = dbManager.getRelationTyps();

        assertEquals(2, typs.size());
        assertEquals(-1, (int)typs.get(0).getKey());
        assertEquals("Nachfolger", typs.get(0).getValue());
        assertEquals(-2, (int)typs.get(1).getKey());
        assertEquals("Spinoff", typs.get(1).getValue());
    }

    @Test
    public void getRelationTyp() {
        assertEquals("Spinoff", dbManager.getRelationTypName(-2));
    }

    //State
    @Test
    public void getStates() {
        ArrayList<State> states = dbManager.getStates();

        assertEquals(5, states.size());
        assertEquals(1 , states.get(0).id);
        assertEquals("Angekündigt", states.get(0).name);
        assertEquals("#9c9c9c", states.get(0).color);
        assertEquals(2 , states.get(1).id);
        assertEquals("Erhältlich", states.get(1).name);
        assertEquals("#c40000", states.get(1).color);
        assertEquals(3 , states.get(2).id);
        assertEquals("In Besitz", states.get(2).name);
        assertEquals("#18b300", states.get(2).color);
        assertEquals(4 , states.get(3).id);
        assertEquals("Vorbestellt", states.get(3).name);
        assertEquals("#00abb8", states.get(3).color);
        assertEquals(5 , states.get(4).id);
        assertEquals("Nicht Verfügbar", states.get(4).name);
        assertEquals("#000000", states.get(4).color);
    }
}