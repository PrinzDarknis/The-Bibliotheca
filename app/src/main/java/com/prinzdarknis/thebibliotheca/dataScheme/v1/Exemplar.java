package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Exemplar implements Serializable {

    /**
     * Erzeuge neue Exemplar (neue ID wird erzeugt)
     * @param libary zugehörige Bibliothek
     * @param name Name der Exemplar
     */
    public Exemplar(UUID libary, String name) {
        this.library = libary;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    /**
     * Ereuge bestehende Exemplar
     * @param libary zugehörige Bibliothek
     * @param name Name der Exemplar
     * @param id ID der Exemplar
     */
    public Exemplar(UUID libary, String name, UUID id) {
        this.library = libary;
        this.name = name;
        this.id = id;
    }

    public final UUID id;
    public final UUID library;
    public UUID series;
    public String name;
    public String infoText;
    public String image;
    public State state;
    public ArrayList<AdditionalInfo> additionalInfos = new ArrayList<AdditionalInfo>();
    public ArrayList<Relation> relationParents = new ArrayList<Relation>();
    public ArrayList<Relation> relationChildren = new ArrayList<Relation>();
    public ArrayList<Relation> spinnoffs = new ArrayList<Relation>();
}
