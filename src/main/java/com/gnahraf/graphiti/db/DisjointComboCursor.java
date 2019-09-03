/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;

import com.gnahraf.graphiti.model.Cursor;
import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.datatypes.ShortInt;
import com.gnahraf.util.list.Lists;
import com.gnahraf.util.list.SortedListUnion;

import java.util.List;

/**
 * A combined cursor over 2 graphs with disjoint edges.
 */
class DisjointComboCursor extends Cursor {

    private Cursor a;
    private boolean aLoaded;
    private Cursor b;
    private boolean bLoaded;


    public DisjointComboCursor(Cursor a, Cursor b) {
        this.a = a;
        this.b = b;

        moveTo(a.getNodeType(), a.getNodeId());
    }

    @Override
    protected boolean loadVertex(NodeType nodeType, int nodeId) {
        boolean aOk = a.moveTo(nodeType, nodeId);
        boolean bOk = b.moveTo(nodeType, nodeId);
        if (aOk || bOk) {
            aLoaded = aOk;
            bLoaded = bOk;
            return true;
        } else
            return false;
    }

    @Override
    public List<EdgeType> getEdgeTypes(boolean inbound) {
        if (combined())
            return Lists.distinctUnion(a.getEdgeTypes(inbound), b.getEdgeTypes(inbound));
        else
            return loadedCursor().getEdgeTypes(inbound);
    }

    @Override
    public int getEdgeCount(boolean inbound) {
        int ac = aLoaded ? a.getEdgeCount(inbound) : 0;
        int bc = bLoaded ? b.getEdgeCount(inbound) : 0;
        return ac + bc;
    }

    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType) {
        int ac = aLoaded ? a.getEdgeCount(inbound, edgeType) : 0;
        int bc = bLoaded ? b.getEdgeCount(inbound, edgeType) : 0;
        return ac + bc;
    }

    @Override
    public List<NodeType> getNodeTypes(boolean inbound, EdgeType edgeType) {
        if (combined())
            return Lists.distinctUnion(
                    a.getNodeTypes(inbound, edgeType),
                    b.getNodeTypes(inbound, edgeType));
        else
            return loadedCursor().getNodeTypes(inbound, edgeType);

    }



    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        int ac = aLoaded ? a.getEdgeCount(inbound, edgeType, nodeType) : 0;
        int bc = bLoaded ? b.getEdgeCount(inbound, edgeType, nodeType) : 0;
        return ac + bc;
    }

    @Override
    public List<Integer> getNodeIds(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        if (combined())
            return SortedListUnion.asUnion(
                    a.getNodeIds(inbound, edgeType, nodeType),
                    b.getNodeIds(inbound, edgeType, nodeType));
        else
            return loadedCursor().getNodeIds(inbound, edgeType, nodeType);
    }

    @Override
    public List<ShortInt> getQualifiedNodeIds() {
        if (combined())
            return Lists.distinctUnion(a.getQualifiedNodeIds(), b.getQualifiedNodeIds());
        else
            return loadedCursor().getQualifiedNodeIds();
    }


    private Cursor loadedCursor() {
        return aLoaded ? a : b;
    }

    private boolean combined() {
        return aLoaded && bLoaded;
    }
}
