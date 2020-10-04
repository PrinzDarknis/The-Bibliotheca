package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Repräsentation einer Reihe/Serie in Java
 */
public class Series implements Serializable {

    /**
     * Erzeuge neue Serie (neue ID wird erzeugt)
     * @param libary zugehörige Bibliothek
     * @param name Name der Serie
     */
    public Series(UUID libary, String name) {
        this.library = libary;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    /**
     * Ereuge bestehende Serie
     * @param libary zugehörige Bibliothek
     * @param name Name der Serie
     * @param id ID der Serie
     */
    public Series(UUID libary, String name, UUID id) {
        this.library = libary;
        this.name = name;
        this.id = id;
    }

    public final UUID library;
    public final UUID id;
    public String name;
    public String infoText;
    public String image;
    public ArrayList<AdditionalInfo> additionalInfos = new ArrayList<AdditionalInfo>();
    public ArrayList<Relation> relationParents = new ArrayList<Relation>();
    public ArrayList<Relation> relationChildren = new ArrayList<Relation>();
    public ArrayList<Relation> spinnoffs = new ArrayList<Relation>();
}
