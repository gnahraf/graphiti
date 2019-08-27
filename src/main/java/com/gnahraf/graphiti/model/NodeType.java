/*
 * Copyright (c) 2015 Babak Farhang
 */
package com.gnahraf.graphiti.model;

import com.gnahraf.util.datatypes.ShortId;

/**
 * Identifies the type or class of a node. For example,
 * this may represent a particular table, which when combined
 * with an integral ID, specifies a particular row in that table.
 */
public final class NodeType extends ShortId implements Comparable<NodeType> {

    public NodeType(int typeId) {
        super(typeId);
    }

    public NodeType(short typeId) {
        super(typeId);
    }


    @Override
    public int compareTo(NodeType another) {
        return super.compareTo(another);
    }
}
