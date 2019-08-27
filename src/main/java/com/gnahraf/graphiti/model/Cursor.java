/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.model;


import com.gnahraf.util.datatypes.ShortInt;

import java.util.List;

/**
 * Cursor over a graph. Because there are very many objects in a graph,
 * representing each through an object would be inefficient. Instead, we
 * use a cursor that can walk the graph.
 * <p/>
 * Each conceptual node is represented by a <tt>(NodeType,int)</tt> tuple. At
 * any point in time, an instance of this class is positioned at one of these
 * nodes. The coordinates of the node the cursor is positioned on are identified with
 * the {@linkplain #getNodeType()} and {@linkplain #getNodeId()}.
 * <p/>
 *
 */
public abstract class Cursor {

    private NodeType nodeType;
    private int nodeId;

    /**
     * Positions the cursor to the specified node coordinates.
     *
     * @param nodeType
     *        the domain of the node's ID. Qualifies <tt>nodeId</tt>
     * @param nodeId
     *        the node's ID
     *
     * @return <tt>true</tt>, if the specified coordinate exists in this
     *         graph
     */
    public boolean moveTo(NodeType nodeType, int nodeId) {
        if (loadVertex(nodeType, nodeId)) {
            this.nodeType = nodeType;
            this.nodeId = nodeId;
            return true;
        }
        return false;
    }


    protected abstract boolean loadVertex(NodeType nodeType, int nodeId);


    public int getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }



    public abstract List<EdgeType> getEdgeTypes(boolean inbound);


    public abstract int getEdgeCount(boolean inbound);


    public abstract int getEdgeCount(boolean inbound, EdgeType edgeType);


    public abstract List<NodeType> getNodeTypes(boolean inbound, EdgeType edgeType);


    public abstract int getEdgeCount(boolean inbound, EdgeType edgeType, NodeType nodeType);


    public abstract List<Integer> getNodeIds(boolean inbound, EdgeType edgeType, NodeType nodeType);


    public abstract List<ShortInt> getNodeIds();

    public boolean isEmpty() {
        return getNodeIds().size() < 2;
    }
}
