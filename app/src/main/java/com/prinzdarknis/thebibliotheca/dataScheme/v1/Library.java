package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;
import java.util.UUID;

/**
 * Repräsentation einer Bibliothek in Java
 */
public class Library implements Serializable {

    /**
     * Erzeuge neue Bibliothek (neue ID wird erzeugt)
     * @param name Name der Bibliothek
     */
    public Library(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    /**
     * Ereuge bestehende Bibliothek
     * @param name Name der Bibliothek
     * @param id ID der Bibliothek
     */
    public Library(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public final UUID id;
    public String name;

    //Statistics
    public int series_count;
    public int exemplar_count;
}
