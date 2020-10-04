package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;
import java.util.UUID;

/**
 * Wrapper für zusätzliche Infos
 */
public class AdditionalInfo implements Serializable {

    /**
     * Erzeuge neue Info (neue ID wird erzeugt)
     * @param typ Art der Information
     * @param text Information
     */
    public AdditionalInfo(String typ, String text) {
        this.typ = typ;
        this.text = text;
        this.id = UUID.randomUUID();
    }

    /**
     * Ereuge bestehende Info
     * @param typ Art der Information
     * @param text Information
     * @param id ID der Information
     */
    public AdditionalInfo(String typ, String text, UUID id) {
        this.typ = typ;
        this.text = text;
        this.id = id;
    }

    public final UUID id;
    public String typ;
    public String text;
}
