/*
 * Copyright (c) 2015 Babak Farhang
 */
package com.gnahraf.graphiti.model;


import com.gnahraf.util.datatypes.ShortId;

/**
 * Identifies the type of an edge that connects two nodes.
 * Thus an edge type represents a type of relationship between 2
 * entities (nodes). Though the type of relationship represented is
 * generally asymetric, i.e. the edges are directed, we choose not
 * to encode the inverse relationship as a separate type; rather,
 * we are classifying here the type of the relationship itself. For example,
 * a father-son relationship encompasses son-father relationship. Or an
 * owner-car relationship encompasses a car-owner relationship. I.e. it's
 * about the relation<em>ship</em>, not the mathematical relation.
 */
public final class EdgeType extends ShortId implements Comparable<EdgeType> {

    public EdgeType(int typeId) {
        super(typeId);
    }

    public EdgeType(short typeId) {
        super(typeId);
    }


    @Override
    public int compareTo(EdgeType another) {
        return super.compareTo(another);
    }
}
