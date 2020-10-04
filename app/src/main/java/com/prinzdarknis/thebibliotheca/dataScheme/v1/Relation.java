package com.prinzdarknis.thebibliotheca.dataScheme.v1;

import java.io.Serializable;
import java.util.UUID;

public class Relation implements Serializable {

    public Relation(int relationTyp, UUID father, UUID child) {
        this.relationTyp = relationTyp;
        this.father = father;
        this.child = child;
    }

    public int relationTyp;
    public UUID father;
    public String fatherName;
    public String fatherImage;
    public UUID child;
    public String childName;
    public String childImage;
}
