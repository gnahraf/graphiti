/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;

import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.util.datatypes.ShortInt;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Builds a graph. Insertion operations are idempotent.
 *
 * @see #insertEdge(ShortInt, Short, ShortInt)
 */
public class GraphBuilder {

    private final boolean OUTBOUND = false;
    private final boolean INBOUND = !OUTBOUND;

    private final TreeMap<ShortInt, Edges> nodes = new TreeMap<>();


    private AddressTable addressTable;
    private EdgeTable edgeTable;
    private NodeTypeTable nodeTypeTable;
    private NodeIdTable nodeIdTable;


    private int insertionDups;
    private int insertions;

    public boolean insertEdge(
            short srcNodeType, int srcNodeId,
            short edgeType,
            short dtnNodeType, int dtnNodeId) {

        return insertEdge(
                new ShortInt(srcNodeType, srcNodeId),
                edgeType,
                new ShortInt(dtnNodeType, dtnNodeId));
    }


    public boolean isEmpty() {
        return nodes.isEmpty();
    }


    public Graph build() {
        if (isEmpty())
            throw new IllegalStateException("instance is empty");
        addressTable = new AddressTable(nodes.size());
        edgeTable = new EdgeTable(nodes.size() * 2);
        nodeTypeTable = new NodeTypeTable(nodes.size() * 2);
        nodeIdTable = new NodeIdTable(nodes.size() * 2);

        for (Map.Entry<ShortInt, Edges> nodeEntry : nodes.entrySet()) {
            ShortInt srcNode = nodeEntry.getKey();
            addressTable.appendEntry(srcNode.getType(), srcNode.getId());
            Edges edges = nodeEntry.getValue();
            writeEdges(edges, OUTBOUND);
            writeEdges(edges, INBOUND);
        }

        return new Graph(addressTable, edgeTable, nodeTypeTable, nodeIdTable);
    }


    private void writeEdges(Edges edges, boolean inbound) {
        if (!edges.hasEdgeMap(inbound))
            return;
        addressTable.setLastEdge(inbound, edgeTable.size(), edges.edgeTypeCount(inbound));
        for (Map.Entry<Short, SortedMap<Short, SortedSet<Integer>>> e :
                edges.rEdgeMap(inbound).entrySet()) {
            short edgeType = e.getKey();
            SortedMap<Short, SortedSet<Integer>> dtnNodes = e.getValue();
            edgeTable.appendEntry(edgeType, nodeTypeTable.size(), dtnNodes.size());
            for (Map.Entry<Short, SortedSet<Integer>> typedIds : dtnNodes.entrySet()) {
                short nodeType = typedIds.getKey();
                SortedSet<Integer> ids = typedIds.getValue();
                nodeTypeTable.appendEntry(nodeType, nodeIdTable.size(), ids.size());
                for (Integer id : ids)
                    nodeIdTable.appendEntry(id);
            }
        }
    }


    public boolean insertEdge(ShortInt src, EdgeType edgeType, ShortInt dtn) {
        return insertEdge(src, edgeType.getId(), dtn);
    }


    public boolean insertEdge(ShortInt src, Short edgeType, ShortInt dtn) {
        if (insertEdgeImpl(src, OUTBOUND, edgeType, dtn)) {
            insertEdgeImpl(dtn, INBOUND, edgeType, src);
            ++insertions;
            return true;
        } else {
            ++insertionDups;
            return false;
        }
    }


    public int getInsertionCount() {
        return insertions;
    }

    public int getInsertionDupCount() {
        return insertionDups;
    }


    private boolean insertEdgeImpl(ShortInt src, boolean inbound, Short edgeType, ShortInt dtn) {
        Edges srcEdges = nodes.get(src);
        if (srcEdges == null) {
            srcEdges = new Edges();
            nodes.put(src, srcEdges);
        }

        SortedMap<Short, SortedSet<Integer>> dtnData = srcEdges.wEdgeMap(inbound).get(edgeType);
        if (dtnData == null) {
            dtnData = new TreeMap<>();
            srcEdges.wEdgeMap(inbound).put(edgeType, dtnData);
        }

        Short nodeType = dtn.getType();
        SortedSet<Integer> nodeIds = dtnData.get(nodeType);
        if (nodeIds == null) {
            nodeIds = new TreeSet<>();
            dtnData.put(nodeType, nodeIds);
        }

        return nodeIds.add(dtn.getId());
    }
}



class Edges {

    TreeMap<Short, SortedMap<Short, SortedSet<Integer>>> out;
    TreeMap<Short, SortedMap<Short, SortedSet<Integer>>> in;

    TreeMap<Short, SortedMap<Short, SortedSet<Integer>>> wEdgeMap(boolean inbound) {
        TreeMap<Short, SortedMap<Short, SortedSet<Integer>>> map = inbound ? in : out;
        if (map == null) {
            map = new TreeMap<>();
            if (inbound)
                in = map;
            else
                out = map;
        }
        return map;
    }


    boolean hasEdgeMap(boolean inbound) {
        return rEdgeMap(inbound) != null;
    }

    int edgeTypeCount(boolean inbound) {
        return hasEdgeMap(inbound) ? rEdgeMap(inbound).size() : 0;
    }


    SortedMap<Short, SortedMap<Short, SortedSet<Integer>>> rEdgeMap(boolean inbound) {
        return inbound ? in : out;
    }

}




