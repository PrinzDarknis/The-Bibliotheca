package com.prinzdarknis.thebibliotheca.database.v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.prinzdarknis.thebibliotheca.dataScheme.v1.AdditionalInfo;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.State;
import com.prinzdarknis.thebibliotheca.database.IDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class DBAndroid extends SQLiteOpenHelper implements IDatabase {
    public static final String TAG = DBAndroid.class.getSimpleName();
    public static final String DATABASE_NAME = "bibliotheca.db";
    public static final int VERSION = 1;

    public static final int DEFAULTSTATE = 2;

    private static DBAndroid instance;
    private static Context context;
    private HashMap<Integer, State> states = null;
    private HashMap<Integer, String> realtionTyps = null;

    //Singleton
    public static void initialize(Context context) {
        synchronized (DBAndroid.class) {
            if (instance == null) {
                DBAndroid.context = context;
                instance = new DBAndroid(context);
            }
        }
    }

    public static DBAndroid getInstance() {
        return instance;
    }

    private DBAndroid(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //SQLiteOpenHelper
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Read Schema
        String sql = "";
        try {
            InputStream input = context.getAssets().open("DBSchema V1.sql");
            Scanner scanner = new Scanner(input);
            sql = scanner.useDelimiter("\\A").next(); // \\A = input beginn => until next input = entire File
            input.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not read DB Schema", e);
            e.printStackTrace();
        }

        //Create Database
        String[] commands = sql.split(";");
        for (String command : commands) {
            db.execSQL(command);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    //Management
    @Override
    public void deleteDB() {
        synchronized (DBAndroid.class) {
            context.deleteDatabase(DATABASE_NAME);
            instance = null;
        }
    }

    @Override
    public void createSampleData() {
        SQLiteDatabase db = getWritableDatabase();

        //Read SQL
        String sql = "";
        try {
            InputStream input = context.getAssets().open("Testdata v1.sql");
            Scanner scanner = new Scanner(input);
            sql = scanner.useDelimiter("\\A").next(); // \\A = input beginn => until next input = entire File
            input.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not read DB Schema", e);
            e.printStackTrace();
        }

        //write Database
        String[] commands = sql.split(";");
        for (String command : commands) {
            db.execSQL(command);
        }
    }

    //Libraries
    @Override
    public ArrayList<Library> getLibraries() {
        ArrayList<Library> arr = new ArrayList<Library>();

        SQLiteDatabase db = getReadableDatabase();

       Cursor cursor = db.query(
               LIBRARY.TABLE,
               new String[] {LIBRARY.ID, LIBRARY.NAME},
               null,
               null,
               null,
               null,
               LIBRARY.NAME
       );

       if (cursor.moveToFirst()) {
           do {
               Library l = new Library(
                       cursor.getString(cursor.getColumnIndex(LIBRARY.NAME)),
                       UUID.fromString(cursor.getString(cursor.getColumnIndex(LIBRARY.ID)))
               );
               setLibraryStatistics(l);
               arr.add(l);
           } while (cursor.moveToNext());
       }
       cursor.close();

        return arr;
    }

    @Override
    public void createSaveLibrary(@NotNull Library library) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIBRARY.ID, library.id.toString());
        values.put(LIBRARY.NAME, library.name);

        db.replace(LIBRARY.TABLE, null, values);
    }

    @Override
    public void deleteLibrary(@NotNull UUID id) {
        ArrayList<Exemplar> exemplars = getExemplars(id);
        for (Exemplar exemplar : exemplars) {
            deleteExemplar(exemplar.id);
        }

        ArrayList<Series> series = getSeries(id);
        for (Series serie : series) {
            deleteSeries(serie.id, false);
        }

        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                LIBRARY.TABLE,
                LIBRARY.ID + " = ?",
                new String[] { id.toString() }
        );
    }

    //Series
    @Override
    public ArrayList<Series> getSeries(@NotNull UUID library) {
        ArrayList<Series> arr = new ArrayList<Series>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                SERIES.TABLE,
                new String[] {SERIES.ID, SERIES.NAME, SERIES.INFOTEXT, SERIES.IMAGE, SERIES.LIBRARY},
                SERIES.LIBRARY + " = ?",
                new String[] { library.toString() },
                null,
                null,
                SERIES.NAME
        );

        if (cursor.moveToFirst()) {
            do {
                Series s = cursorToSeries(cursor);
                arr.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return arr;
    }

    @Override
    public Series getSingleSeries(@NotNull UUID series) {
        Series s = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                SERIES.TABLE,
                new String[] {SERIES.ID, SERIES.NAME, SERIES.INFOTEXT, SERIES.IMAGE, SERIES.LIBRARY},
                SERIES.ID + " = ?",
                new String[] { series.toString() },
                null,
                null,
                SERIES.NAME
        );

        if (cursor.moveToFirst()) {
            s = cursorToSeries(cursor);
        }
        cursor.close();

        return s;
    }

    @Override
    public void createSaveSeries(@NotNull Series series) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SERIES.ID, series.id.toString());
        values.put(SERIES.NAME, series.name);
        values.put(SERIES.INFOTEXT, series.infoText);
        values.put(SERIES.IMAGE, series.image);
        values.put(SERIES.LIBRARY, series.library.toString());

        db.replace(SERIES.TABLE, null, values);

        writeInfos(series.id, InfoTable.SeriesAdditionalInfo, series.additionalInfos);

        for (Relation r : series.relationParents)
            writeRelation(RelationTable.RelationSeries, r);
        for (Relation r : series.relationChildren)
            writeRelation(RelationTable.RelationSeries, r);
        for (Relation r : series.spinnoffs)
            writeRelation(RelationTable.RelationSeries, r);
    }

    @Override
    public void deleteSeries(@NotNull UUID id, @NotNull boolean inclExemplar) {
        SQLiteDatabase db = getWritableDatabase();

        //Exemplar
        if (inclExemplar) {
            db.delete(EXEMPLAR.TABLE,
                    EXEMPLAR.SERIES + " like ?",
                    new String[] { id.toString() }
            );
        }
        else {
            ContentValues values = new ContentValues();
            values.putNull(EXEMPLAR.SERIES);
            db.update(EXEMPLAR.TABLE,
                    values,
                    EXEMPLAR.SERIES + " = ?",
                    new String[] { id.toString() }
            );
        }

        //AdditionalInfo
        db.delete(InfoTable.SeriesAdditionalInfo.name(),
                ADDITIONALINFO.SERIES + " = ?",
                new String[] { id.toString() }
        );

        //Relations
        db.delete(RelationTable.RelationSeries.name(),
                RELATION_SERIES_EXEMPLAR.FATHER + " = ? OR " + RELATION_SERIES_EXEMPLAR.CHILD + " = ?",
                new String[] {
                        id.toString(),
                        id.toString()
                }
        );

        //Series
        db.delete(SERIES.TABLE,
                SERIES.ID + " = ?",
                new String[] {
                        id.toString()
                }
        );
    }

    @Override
    public String[] getSeriesNameAndImage(@NotNull UUID series) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                SERIES.TABLE,
                new String[] { SERIES.NAME, SERIES.IMAGE },
                SERIES.ID + " = ?",
                new String[] { series.toString() },
                null,
                null,
                SERIES.NAME
        );

        if (cursor.moveToFirst()) {
            return new String[] {
                    cursor.getString(cursor.getColumnIndex(SERIES.NAME)),
                    cursor.getString(cursor.getColumnIndex(SERIES.IMAGE))
            };
        }

        return new String[] {"",""};
    }

    //Exemplar
    @Override
    public ArrayList<Exemplar> getExemplars(@NotNull UUID library) {
        return getExemplars(library, null);
    }

    @Override
    public ArrayList<Exemplar> getExemplars(@NotNull UUID library, UUID series) {
        if (states == null)
            getStates();

        ArrayList<Exemplar> arr = new ArrayList<Exemplar>();
        String where = EXEMPLAR.LIBRARY + " = ?";
        String[] whereArgs = new String[] { library.toString() };
        if (series != null) {
            where += " AND " + EXEMPLAR.SERIES + " like ?";
            whereArgs = new String[] { library.toString(), series.toString() };
        }
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                EXEMPLAR.TABLE,
                new String[] {EXEMPLAR.ID, EXEMPLAR.NAME, EXEMPLAR.INFOTEXT, EXEMPLAR.IMAGE, EXEMPLAR.STATE, EXEMPLAR.SERIES, EXEMPLAR.LIBRARY},
                where,
                whereArgs,
                null,
                null,
                EXEMPLAR.NAME
        );

        if (cursor.moveToFirst()) {
            do {
                Exemplar e = cursorToExemplar(cursor);

                arr.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return arr;
    }

    @Override
    public Exemplar getSingleExemplar(@NotNull UUID exemplar) {
        Exemplar e = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                EXEMPLAR.TABLE,
                new String[] {EXEMPLAR.ID, EXEMPLAR.NAME, EXEMPLAR.INFOTEXT, EXEMPLAR.IMAGE, EXEMPLAR.STATE, EXEMPLAR.SERIES, EXEMPLAR.LIBRARY},
                EXEMPLAR.ID + " = ?",
                new String[] { exemplar.toString() },
                null,
                null,
                EXEMPLAR.NAME
        );

        if (cursor.moveToFirst()) {
            e = cursorToExemplar(cursor);
        }
        cursor.close();

        return e;
    }

    @Override
    public void createSaveExemplar(@NotNull Exemplar exemplar) {
        int state = exemplar.state != null ? exemplar.state.id : DEFAULTSTATE;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EXEMPLAR.ID, exemplar.id.toString());
        values.put(EXEMPLAR.NAME, exemplar.name);
        values.put(EXEMPLAR.INFOTEXT, exemplar.infoText);
        values.put(EXEMPLAR.IMAGE, exemplar.image);
        values.put(EXEMPLAR.STATE, state);
        values.put(EXEMPLAR.LIBRARY, exemplar.library.toString());
        if (exemplar.series != null)
            values.put(EXEMPLAR.SERIES, exemplar.series.toString());

        db.replace(EXEMPLAR.TABLE, null, values);

        writeInfos(exemplar.id, InfoTable.ExemplarAdditionalInfo, exemplar.additionalInfos);

        for (Relation r : exemplar.relationParents)
            writeRelation(RelationTable.RelationExemplar, r);
        for (Relation r : exemplar.relationChildren)
            writeRelation(RelationTable.RelationExemplar, r);
        for (Relation r : exemplar.spinnoffs)
            writeRelation(RelationTable.RelationExemplar, r);
    }

    @Override
    public void deleteExemplar(@NotNull UUID id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EXEMPLAR.TABLE,
                EXEMPLAR.ID + " = ?",
                new String[] {
                        id.toString()
                }
        );
    }

    @Override
    public String[] getExemplarNameAndImage(@NotNull UUID exemplar) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                EXEMPLAR.TABLE,
                new String[] { EXEMPLAR.NAME, EXEMPLAR.IMAGE },
                EXEMPLAR.ID + " = ?",
                new String[] { exemplar.toString() },
                null,
                null,
                EXEMPLAR.NAME
        );

        if (cursor.moveToFirst()) {
            return new String[] {
                    cursor.getString(cursor.getColumnIndex(EXEMPLAR.NAME)),
                    cursor.getString(cursor.getColumnIndex(EXEMPLAR.IMAGE))
            };
        }

        return new String[] {"",""};
    }

    //Infos
    @Override
    public ArrayList<AdditionalInfo> getInfos(@NotNull UUID series_exemplar, @NotNull InfoTable table) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AdditionalInfo> infos = new ArrayList<AdditionalInfo>();

        String refColum = table == InfoTable.SeriesAdditionalInfo ? ADDITIONALINFO.SERIES : ADDITIONALINFO.EXEMPLAR;

        Cursor cursor = db.query(
                table.name(),
                new String[] {ADDITIONALINFO.ID, ADDITIONALINFO.TYP, ADDITIONALINFO.TEXT},
                refColum + " = ?",
                new String[] { series_exemplar.toString() },
                null,
                null,
                ADDITIONALINFO.TYP
        );

        if(cursor.moveToFirst()) {
            do {
                AdditionalInfo a = new AdditionalInfo(
                        cursor.getString(cursor.getColumnIndex(ADDITIONALINFO.TYP)),
                        cursor.getString(cursor.getColumnIndex(ADDITIONALINFO.TEXT)),
                        UUID.fromString(cursor.getString(cursor.getColumnIndex(ADDITIONALINFO.ID)))
                );
                infos.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return infos;
    }

    @Override
    public void writeInfos(@NotNull UUID series_exemplar, @NotNull InfoTable table, @NotNull ArrayList<AdditionalInfo> infos) {
        SQLiteDatabase db = getWritableDatabase();

        //get Old Infos
        HashMap<UUID, Boolean> old = new HashMap<UUID, Boolean>();
        for (AdditionalInfo a: getInfos(series_exemplar, table)) {
            old.put(a.id, true);
        }

        //write
        for (AdditionalInfo a : infos) {
            ContentValues values = new ContentValues();
            values.put(ADDITIONALINFO.ID, a.id.toString());
            values.put(ADDITIONALINFO.TEXT, a.text);
            values.put(ADDITIONALINFO.TYP, a.typ);
            values.put(table == InfoTable.SeriesAdditionalInfo ? ADDITIONALINFO.SERIES : ADDITIONALINFO.EXEMPLAR, series_exemplar.toString());

            long i = db.replace (table.name(), null, values);

            if (old.containsKey(a.id)) {
                old.replace(a.id, false);
            }
        }

        //delete unused
        for (Map.Entry<UUID, Boolean> e : old.entrySet()) {
            if (e.getValue()) {
                deleteInfo(e.getKey(), table);
            }
        }
    }

    @Override
    public void deleteInfo(@NotNull UUID info, @NotNull InfoTable table) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(table.name(), ADDITIONALINFO.ID + " = ?", new String[] { info.toString() });
    }

    //Relations
    @Override
    public ArrayList<Relation> getRelations(@NotNull UUID series_exemplar, @NotNull RelationTable table) {
        ArrayList<Relation> relations = new ArrayList<Relation>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                table.name(),
                new String[] {RELATION_SERIES_EXEMPLAR.FATHER, RELATION_SERIES_EXEMPLAR.CHILD, RELATION_SERIES_EXEMPLAR.RELATION},
                RELATION_SERIES_EXEMPLAR.FATHER + " = ? OR " + RELATION_SERIES_EXEMPLAR.CHILD + " = ?",
                new String[] { series_exemplar.toString(), series_exemplar.toString() },
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                UUID father = UUID.fromString(cursor.getString(cursor.getColumnIndex(RELATION_SERIES_EXEMPLAR.FATHER)));
                UUID child = UUID.fromString(cursor.getString(cursor.getColumnIndex(RELATION_SERIES_EXEMPLAR.CHILD)));
                Relation r = new Relation(
                        cursor.getInt(cursor.getColumnIndex(RELATION_SERIES_EXEMPLAR.RELATION)),
                        father,
                        child
                );

                String[] fatherArr = null, childArr = null;
                switch (table) {
                    case RelationSeries:
                        fatherArr = getSeriesNameAndImage(father);
                        childArr = getSeriesNameAndImage(child);
                        break;
                    case RelationExemplar:
                        fatherArr = getExemplarNameAndImage(father);
                        childArr = getExemplarNameAndImage(child);
                        break;
                }

                r.fatherName = fatherArr[0];
                r.fatherImage = fatherArr[1];
                r.childName = childArr[0];
                r.childImage = childArr[1];
                relations.add(r);
            } while (cursor.moveToNext());
        }

        return relations;
    }

    @Override
    public void writeRelation(@NotNull RelationTable table, @NotNull Relation relation) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RELATION_SERIES_EXEMPLAR.FATHER, relation.father.toString());
        values.put(RELATION_SERIES_EXEMPLAR.CHILD, relation.child.toString());
        values.put(RELATION_SERIES_EXEMPLAR.RELATION, relation.relationTyp);

        db.replace(table.name(), null, values);
    }

    @Override
    public void deleteRelation(@NotNull RelationTable table, @NotNull Relation relation) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                        table.name(),
                        RELATION_SERIES_EXEMPLAR.FATHER + " = ? AND " + RELATION_SERIES_EXEMPLAR.CHILD + " = ?",
                        new String[] {relation.father.toString(), relation.child.toString()}
                );
    }

    //RelationTyps
    @Override
    public ArrayList<Map.Entry<Integer, String>> getRelationTyps() {

        synchronized (DBAndroid.class) {
            if (realtionTyps == null) {
                realtionTyps = new HashMap<Integer, String>();
                SQLiteDatabase db = getReadableDatabase();

                Cursor cursor = db.query(
                        RELATION.TABLE,
                        new String[] {RELATION.ID, RELATION.NAME},
                        null,
                        null,
                        null,
                        null,
                        RELATION.ID
                );

                if (cursor.moveToFirst()) {
                    do {
                        realtionTyps.put(
                                cursor.getInt(cursor.getColumnIndex(RELATION.ID)),
                                cursor.getString(cursor.getColumnIndex(RELATION.NAME))
                        );
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }
        }

        return new ArrayList<Map.Entry<Integer, String>>(realtionTyps.entrySet());
    }

    @Override
    public String getRelationTypName(@NotNull int id) {
        if (realtionTyps == null)
            getRelationTyps();

        return realtionTyps.get(id);
    }

    //State
    @Override
    public ArrayList<State> getStates() {
        synchronized (DBAndroid.class) {
            if(states == null) {
                states = new HashMap<Integer, State>();
                SQLiteDatabase db = getReadableDatabase();

                Cursor cursor = db.query(
                        STATE.TABLE,
                        new String[] {STATE.ID, STATE.NAME, STATE.COLOR},
                        null,
                        null,
                        null,
                        null,
                        STATE.NAME
                );

                if(cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(STATE.ID));
                        State s = new State(
                                id,
                                cursor.getString(cursor.getColumnIndex(STATE.NAME)),
                                cursor.getString(cursor.getColumnIndex(STATE.COLOR))
                        );
                        states.put(id, s);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }

        ArrayList<State> arr = new ArrayList<State>(states.values());
        arr.sort(new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.id - o2.id;
            }
        });
        return arr;
    }

    @Override
    public State getDefaultState() {
        synchronized (DBAndroid.class) {
            if(states == null) {
                getStates();
            }

            return states.get(DEFAULTSTATE);
        }
    }

    //Interne Helper
    private void setLibraryStatistics(Library library) {
        SQLiteDatabase db = getReadableDatabase();
        //zwei Queries, da SQLite keinen FULL JOIN unterstützt.

        //Series
        String sql =    "select Library, count(*) as count from 'Series' where Library like ? group by Library";
        Cursor cursor = db.rawQuery(sql, new String[] { library.id.toString() });
        if (cursor.moveToFirst()) {
            library.series_count = cursor.getInt(1);
        }

        //Exemplar
        sql =    "select Library, count(*) as count from 'Exemplar' where Library like ? group by Library";
        cursor = db.rawQuery(sql, new String[] { library.id.toString() });
        if (cursor.moveToFirst()) {
            library.exemplar_count = cursor.getInt(1);
        }
    }

    private Series cursorToSeries(Cursor cursor) {
        Series s = new Series(
                UUID.fromString(cursor.getString(cursor.getColumnIndex(SERIES.LIBRARY))),
                cursor.getString(cursor.getColumnIndex(SERIES.NAME)),
                UUID.fromString(cursor.getString(cursor.getColumnIndex(SERIES.ID)))
        );
        s.infoText = cursor.getString(cursor.getColumnIndex(SERIES.INFOTEXT));
        s.image = cursor.getString(cursor.getColumnIndex(SERIES.IMAGE));

        //AdditionalInfo
        s.additionalInfos = getInfos(s.id, InfoTable.SeriesAdditionalInfo);

        //Relations
        ArrayList<Relation> relations = getRelations(s.id, RelationTable.RelationSeries);
        for (Relation r: relations) {
            if (r.relationTyp == -2)
                //Spinnoff
                s.spinnoffs.add(r);
            else {
                //Nachfolger
                if (r.father.equals(s.id))
                    s.relationChildren.add(r);
                else
                    s.relationParents.add(r);
            }
        }
        return s;
    }

    private Exemplar cursorToExemplar(Cursor cursor) {
        Exemplar e = new Exemplar(
                UUID.fromString(cursor.getString(cursor.getColumnIndex(EXEMPLAR.LIBRARY))),
                cursor.getString(cursor.getColumnIndex(EXEMPLAR.NAME)),
                UUID.fromString(cursor.getString(cursor.getColumnIndex(EXEMPLAR.ID)))
        );
        e.infoText = cursor.getString(cursor.getColumnIndex(EXEMPLAR.INFOTEXT));
        e.image = cursor.getString(cursor.getColumnIndex(EXEMPLAR.IMAGE));
        e.state = states.get(cursor.getInt(cursor.getColumnIndex(EXEMPLAR.STATE)));
        String series_str = cursor.getString(cursor.getColumnIndex(EXEMPLAR.SERIES));
        if (series_str != null)
            e.series = UUID.fromString(series_str);

        //AdditionalInfo
        e.additionalInfos = getInfos(e.id, InfoTable.ExemplarAdditionalInfo);

        //Relations
        ArrayList<Relation> relations = getRelations(e.id, RelationTable.RelationExemplar);
        for (Relation r: relations) {
            if (r.relationTyp == -2)
                //Spinnoff
                e.spinnoffs.add(r);
            else {
                //Nachfolger
                if (r.father.equals(e.id))
                    e.relationChildren.add(r);
                else
                    e.relationParents.add(r);
            }
        }

        return e;
    }

    //Table- and Colum-Names
    private static class LIBRARY {
        public static final String TABLE = "Library";
        public static final String ID = "ID";
        public static final String NAME = "Name";
    }

    private static class SERIES {
        public static final String TABLE = "Series";
        public static final String ID = "ID";
        public static final String NAME = "Name";
        public static final String INFOTEXT = "Infotext";
        public static final String IMAGE = "Image";
        public static final String LIBRARY = "Library";
    }

    private static class EXEMPLAR {
        public static final String TABLE = "Exemplar";
        public static final String ID = "ID";
        public static final String NAME = "Name";
        public static final String INFOTEXT = "Infotext";
        public static final String IMAGE = "Image";
        public static final String SERIES = "Series";
        public static final String LIBRARY = "Library";
        public static final String STATE = "State";
    }

    private static class ADDITIONALINFO {
        public static final String ID = "ID";
        public static final String SERIES = "Series";
        public static final String EXEMPLAR = "Exemplar";
        public static final String TYP = "Typ";
        public static final String TEXT = "Text";
    }

    private static class STATE {
        public static final String TABLE = "State";
        public static final String ID = "ID";
        public static final String NAME = "Name";
        public static final String COLOR = "Color";
    }

    private static class RELATION {
        public static final String TABLE = "Relation";
        public static final String ID = "ID";
        public static final String NAME = "Name";
    }

    private static class RELATION_SERIES_EXEMPLAR {
        public static final String FATHER = "Father";
        public static final String CHILD = "Child";
        public static final String RELATION = "Relation";
    }
}
