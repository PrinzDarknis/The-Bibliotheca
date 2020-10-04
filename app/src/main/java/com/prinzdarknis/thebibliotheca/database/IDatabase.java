package com.prinzdarknis.thebibliotheca.database;

import com.prinzdarknis.thebibliotheca.dataScheme.v1.AdditionalInfo;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Exemplar;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Library;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Relation;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.Series;
import com.prinzdarknis.thebibliotheca.dataScheme.v1.State;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public interface IDatabase {

    public void deleteDB();
    public void createSampleData();

    public ArrayList<Library> getLibraries();
    public void createSaveLibrary(@NotNull Library library);
    public void deleteLibrary(@NotNull UUID id);

    public ArrayList<Series> getSeries(@NotNull UUID library);
    public Series getSingleSeries(@NotNull UUID series);
    public void createSaveSeries(@NotNull Series series);
    public void deleteSeries(@NotNull UUID id, @NotNull boolean inclExemplar);

    public ArrayList<Exemplar> getExemplars(@NotNull UUID library);
    public ArrayList<Exemplar> getExemplars(@NotNull UUID library, UUID series);
    public Exemplar getSingleExemplar(@NotNull UUID exemplar);
    public void createSaveExemplar(@NotNull Exemplar exemplar);
    public void deleteExemplar(@NotNull UUID id);

    public ArrayList<AdditionalInfo> getInfos(@NotNull UUID series_exemplar, @NotNull InfoTable table);
    public void writeInfos(@NotNull UUID series_exemplar, @NotNull InfoTable table, @NotNull ArrayList<AdditionalInfo> infos);
    public void deleteInfo(@NotNull UUID info, InfoTable table);

    public ArrayList<Relation> getRelations(@NotNull UUID series_exemplar, @NotNull RelationTable table);
    public void writeRelation(@NotNull RelationTable table, @NotNull Relation relation);
    public void deleteRelation(@NotNull RelationTable table, @NotNull Relation relation);

    public ArrayList<Map.Entry<Integer, String>> getRelationTyps();
    public String getRelationTypName(@NotNull int id);

    public ArrayList<State> getStates();
    public State getDefaultState();

    public String[] getExemplarNameAndImage(@NotNull UUID exemplar);
    public String[] getSeriesNameAndImage(@NotNull UUID series);

    //Enums
    public enum RelationTable {
        RelationSeries,
        RelationExemplar
    }
    public enum InfoTable {
        SeriesAdditionalInfo,
        ExemplarAdditionalInfo
    }
}
