/*
 * Copyright (c) 2015 Babak Farhang
 */
package com.gnahraf.graphiti.db;


import com.gnahraf.graphiti.model.Cursor;
import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.datatypes.ShortInt;

import java.util.Collections;
import java.util.List;

/**
 * Created by babak on 8/9/15.
 */
public class EmptyCursor extends Cursor {

    public final static Cursor INSTANCE = new EmptyCursor();

    public EmptyCursor() {
        moveTo(new NodeType(0), 0);
    }

    @Override
    protected boolean loadVertex(NodeType nodeType, int nodeId) {
        return nodeType.getId() == 0 && nodeId == 0;
    }

    @Override
    public List<EdgeType> getEdgeTypes(boolean inbound) {
        return Collections.emptyList();
    }

    @Override
    public int getEdgeCount(boolean inbound) {
        return 0;
    }

    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType) {
        return 0;
    }

    @Override
    public List<NodeType> getNodeTypes(boolean inbound, EdgeType edgeType) {
        return Collections.emptyList();
    }

    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        return 0;
    }

    @Override
    public List<Integer> getNodeIds(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        return Collections.emptyList();
    }

    @Override
    public List<ShortInt> getQualifiedNodeIds() {
        return Collections.emptyList();
    }
}
