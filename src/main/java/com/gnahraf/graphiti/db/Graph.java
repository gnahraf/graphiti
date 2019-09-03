/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import com.gnahraf.graphiti.model.Cursor;

/**
 * Created by babak on 6/18/15.
 */
public class Graph {


    final AddressTable addressTable;
    final EdgeTable edgeTable;
    final NodeTypeTable nodeTypeTable;
    final NodeIdTable nodeIdTable;


    public Graph(
            AddressTable addressTable,
            EdgeTable edgeTable,
            NodeTypeTable nodeTypeTable,
            NodeIdTable nodeIdTable) {

        this.addressTable = addressTable;
        this.edgeTable = edgeTable;
        this.nodeTypeTable = nodeTypeTable;
        this.nodeIdTable = nodeIdTable;

        if (addressTable == null)
            throw new IllegalArgumentException("null addressTable");
        if (edgeTable == null)
            throw new IllegalArgumentException("null edgeTable");
        if (nodeTypeTable == null)
            throw new IllegalArgumentException("null nodeTypeTable");
        if (nodeIdTable == null)
            throw new IllegalArgumentException("null nodeIdTable");
    }


    public boolean isEmpty() {
        return addressTable.isEmpty();
    }



    public Cursor newCursor() {
        return new FixedGraphCursor(addressTable, edgeTable, nodeTypeTable, nodeIdTable);
    }



    public int getNodeCount() {
        return addressTable.size();
    }


    public int getEdgeCount() {
        return nodeIdTable.size();
    }


    public long getMemSize() {
        return
            addressTable.byteSize() +
            edgeTable.byteSize() +
            nodeTypeTable.byteSize() +
            nodeIdTable.byteSize();
    }



    public float getAvgEdgeTypeCountPerNode() {
        return ((float) edgeTable.size()) / (addressTable.size() * 2);
    }


    public float getAvgNodeTypeCountPerEdgeType() {
        return ((float) nodeTypeTable.size()) / edgeTable.size();
    }


    public float getAvgNodeIdCountPerNodeType() {
        return ((float) nodeIdTable.size() / nodeTypeTable.size());
    }
    
    
    public int unusedMem() {
    	return  addressTable.overhead() +
    			edgeTable.overhead() +
    			nodeTypeTable.overhead() +
    			nodeIdTable.overhead();
    }
    
    
    /**
     * Trims backing tables to minimium byte size.
     */
    public void trimMemToSize() {
    	addressTable.trimToSize();
    	edgeTable.trimToSize();
    	nodeTypeTable.trimToSize();
    	nodeIdTable.trimToSize();
    }




    public static Graph merge(Graph a, Graph b) {
        if (a == null && b == null)
            throw new IllegalArgumentException("Attempt merge 2 null graphs");
        else if (a == null)
            return b;
        else if (b == null)
            return a;
        else if (a.isEmpty())
            return b;
        if (b.isEmpty())
            return a;
        else
            return new GraphMerger(a, b).merge();
    }

}
