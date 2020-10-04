package com.prinzdarknis.thebibliotheca.managment;

import android.graphics.Bitmap;

import com.prinzdarknis.thebibliotheca.dataScheme.v1.AdditionalInfo;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.State;
import com.prinzdarknis.thebibliotheca.database.IDatabase;
import com.prinzdarknis.thebibliotheca.imageManager.IImageManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Hauptlogik des Programmes.
 */
public class ProgramLogic {

    private static ProgramLogic instance;

    private IImageManager imageManager;
    private IDatabase database;

    private Library activeLibrary;

    //Singleton
    /**
     * Inizialisiert den Siniglton. (Interfaces für Plattformunabhänigkeit)
     * @param imageManager Manager für die Bilder
     * @param database Datenbankverbindung
     */
    public static void initialize(IImageManager imageManager, IDatabase database) {
        synchronized (ProgramLogic.class) {
            if (instance == null) {
                instance = new ProgramLogic(imageManager, database);
            }
        }
    }

    /**
     * Zugriff auf die aktuelle Instanz.
     * @return die aktuelle Instanz. Null, wenn noch nicht initialisiert.
     */
    public static ProgramLogic getInstance() {
        return instance;
    }

    private ProgramLogic (IImageManager imageManager, IDatabase database) {
        this.imageManager = imageManager;
        this.database = database;

        //load first Library
        ArrayList<Library> temp = database.getLibraries();
        if (temp.size() > 0)
            activeLibrary = temp.get(0);
    }

    //Management
    public void deleteDatabase() {
        database.deleteDB();
        activeLibrary = null;
    }
    public void newDatabase(IDatabase database) {
        this.database = database;
    }
    public void loadSampleData(IImageManager.Callback callback) {
        imageManager.createSampleData(callback);
        database.createSampleData();
    }

    //Libraries
    public ArrayList<Library> getLibraries() {
        return database.getLibraries();
    }
    public void createSaveLibrary(Library library) {
        database.createSaveLibrary(library);
    }
    public void deleteLibrary(Library library) {
        ArrayList<Series> seriesList = database.getSeries(activeLibrary.id);
        for (Series s : seriesList)
            imageManager.deleteImage(s.image);

        ArrayList<Exemplar> exemplarList = database.getExemplars(activeLibrary.id);
        for (Exemplar e : exemplarList)
            imageManager.deleteImage(e.image);

        database.deleteLibrary(library.id);
    }

    //Active Library
    public void setActiveLibrary(Library library) {
        activeLibrary = library;
    }
    public String getActiveLibraryName() {
        if (activeLibrary != null)
            return activeLibrary.name;
        else
            return null;
    }
    public UUID getActiveLibraryID() {
        if (activeLibrary != null)
            return activeLibrary.id;
        else
            return null;
    }

    //Series
    public ArrayList<Series> getSeries() {
        if (activeLibrary == null)
            return new ArrayList<Series>();

        return database.getSeries(activeLibrary.id);
    }
    public Series getSingleSeries(@NotNull UUID series) {
        return database.getSingleSeries(series);
    }
    public void createSaveSeries(Series series) {
        database.createSaveSeries(series);
    }
    public void deleteSeries(@NotNull UUID id, @NotNull boolean inclExemplar) {
        String[] s = database.getSeriesNameAndImage(id);
        imageManager.deleteImage(s[1]);

        if (inclExemplar) {
            ArrayList<Exemplar> exemplarList = database.getExemplars(activeLibrary.id, id);
            for (Exemplar e : exemplarList)
                imageManager.deleteImage(e.image);
        }

        database.deleteSeries(id, inclExemplar);
    }

    //Exemplar
    public ArrayList<Exemplar> getExemplars() {
        if (activeLibrary == null)
            return new ArrayList<Exemplar>();

        return database.getExemplars(activeLibrary.id);
    }
    public ArrayList<Exemplar> getExemplars(UUID series) {
        if (activeLibrary == null)
            return new ArrayList<Exemplar>();

        return database.getExemplars(activeLibrary.id, series);
    }
    public Exemplar getSingleExemplar(@NotNull UUID exemplar) {
        return database.getSingleExemplar(exemplar);
    }
    public void createSaveExemplar(Exemplar exemplar) {
        database.createSaveExemplar(exemplar);
    }
    public void deleteExemplar(@NotNull UUID id) {
        String[] s = database.getExemplarNameAndImage(id);
        imageManager.deleteImage(s[1]);
        database.deleteExemplar(id);
    }

    //Info
    public ArrayList<AdditionalInfo> getInfos_Exemplar(@NotNull UUID series_exemplar){
        return database.getInfos(series_exemplar, IDatabase.InfoTable.ExemplarAdditionalInfo);
    }
    public void writeInfos_Exemplar(@NotNull UUID series_exemplar, @NotNull ArrayList<AdditionalInfo> infos){
        database.writeInfos(series_exemplar, IDatabase.InfoTable.ExemplarAdditionalInfo, infos);
    }
    public void deleteInfos_Exemplar(@NotNull UUID info){
        database.deleteInfo(info, IDatabase.InfoTable.ExemplarAdditionalInfo);
    }

    public ArrayList<AdditionalInfo> getInfos_Series(@NotNull UUID series_exemplar){
        return database.getInfos(series_exemplar, IDatabase.InfoTable.SeriesAdditionalInfo);
    }
    public void writeInfos_Series(@NotNull UUID series_exemplar, @NotNull ArrayList<AdditionalInfo> infos){
        database.writeInfos(series_exemplar, IDatabase.InfoTable.SeriesAdditionalInfo, infos);
    }
    public void deleteInfos_Series(@NotNull UUID info){
        database.deleteInfo(info, IDatabase.InfoTable.SeriesAdditionalInfo);
    }

    //Relations
    public ArrayList<Map.Entry<Integer, String>> getRelations() {
        return database.getRelationTyps();
    }
    public void writeRelation_Series(@NotNull Relation relation) {
        database.writeRelation(IDatabase.RelationTable.RelationSeries, relation);
    }
    public void deleteRelation_Series(@NotNull Relation relation) {
        database.deleteRelation(IDatabase.RelationTable.RelationSeries, relation);
    }
    public void writeRelation_Exemplar(@NotNull Relation relation) {
        database.writeRelation(IDatabase.RelationTable.RelationExemplar, relation);
    }
    public void deleteRelation_Exemplar(@NotNull Relation relation) {
        database.deleteRelation(IDatabase.RelationTable.RelationExemplar, relation);
    }

    /**
     * Creates an Relation from Vather to Child.
     * Meaning: Father is in Realtionship to Child
     * @param father
     * @param relation
     * @param child
     * @return Null, if creation completed. Otherwise the error message;
     */
    public String createRelationSeries(Series father, int relation, Series child) {
        throw new IllegalStateException();
    }

    /**
     * Creates an Relation from Vather to Child.
     * Meaning: Father is in Realtionship to Child
     * @param father
     * @param relation
     * @param child
     * @return Null, if creation completed. Otherwise the error message;
     */
    public String createRelationExemplar(Exemplar father, int relation, Exemplar child) {
        throw new IllegalStateException();
    }

    //State
    public ArrayList<State> getStates(){
        return database.getStates();
    }
    public State getDefaultState() {
        return database.getDefaultState();
    }

    //Image
    public Bitmap getImage(Series series) {
        return imageManager.getImageByFileName(series.image);
    }
    public Bitmap getImage(Exemplar exemplar) {
        return imageManager.getImageByFileName(exemplar.image);
    }
    public Bitmap getImageByName(String filename) {
        return imageManager.getImageByFileName(filename);
    }

    public boolean saveImage(Series series, Bitmap image) {
        String filename = series.id.toString() + ".jpg";
        boolean erg = imageManager.saveImage(image, filename);

        if (erg)
            series.image = filename;

        return erg;
    }
    public boolean saveImage(Exemplar exemplar, Bitmap image) {
        String filename = exemplar.id.toString() + ".jpg";
        boolean erg = imageManager.saveImage(image, filename);

        if (erg)
            exemplar.image = filename;

        return erg;
    }

    //Name and Image
    public String[] getExemplarNameAndImage(@NotNull UUID exemplar){
        return database.getExemplarNameAndImage(exemplar);
    }
    public String[] getSeriesNameAndImage(@NotNull UUID series){
        return database.getSeriesNameAndImage(series);
    }
}
