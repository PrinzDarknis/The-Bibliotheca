package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;

public class State implements Serializable {

    /**
     * Ereuge bestehenden Exemplar
     * @param name Name des State
     * @param id ID des State
     * @param color Farbe des State
     */
    public State(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public final int id;
    public final String name;
    public final String color;
}
