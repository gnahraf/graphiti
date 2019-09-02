/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;

import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.list.BaseList;
import com.gnahraf.util.mem.Buff;
import com.gnahraf.util.datatypes.Primitives.Sizeof;
import com.gnahraf.util.mem.Table;

import java.util.List;

/**
 * Table of 2-byte {@linkplain NodeType}s referenced from the {@linkplain EdgeTable}.
 * Each row here references a range of row numbers in the {@linkplain NodeIdTable}.
 * 
 * <h4>Limits</h4>
 * 
 * Row numbers greater than 16,777,215 in the {@linkplain NodeIdTable} are not addressable
 * from here. This puts a silly, artificial cap on the number of edges on the entire graph.
 * Needs fixing.
 * 
 * <p/>
 * 
 * FIXME: Expand row size by 1 byte in order to allow specifying larger row numbers in
 * the {@linkplain NodeIdTable}. The maximum size of a Java byte array (2GB) will then be
 * the governing constraint on the number of possible edges in a graph. (Adding a byte here
 * shouldn't move the needle much on memory footprint.. should work out to way less than an
 * extra byte per edge.)
 */
public class NodeTypeTable extends Table {

    public final static int ROW_WIDTH = Sizeof.SHORT + Sizeof.TRYTE + Sizeof.TRYTE;


    private final List<NodeType> nodeTypes;



    public NodeTypeTable(int initCapacity) {
        super(initCapacity);
        nodeTypes = getNodeTypeView();
    }

    public NodeTypeTable(Buff data, int size) {
        super(data, size);
        nodeTypes = getNodeTypeView();
    }




    @Override
    protected final int itemSize() {
        return ROW_WIDTH;
    }




    public short getNodeType(int index) {
        int offset = offset(index);
        return data.getShort(offset);
    }


    public int getNodeIdRow(int index) {
        int offset = offset(index) + Sizeof.SHORT;
        return data.getTryte(offset);
    }


    public int getNodeIdCount(int index) {
        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        return data.getTryte(offset);
    }


    public List<NodeType> asTypeList() {
        return nodeTypes;
    }


    private List<NodeType> getNodeTypeView() {
        return new BaseList<NodeType>() {
            @Override
            protected NodeType getImpl(int location) {
                return new NodeType( getNodeType(location) );
            }
            @Override
            public int size() {
                return NodeTypeTable.this.size();
            }
        };
    }







    public void appendEntry(short nodeType, int nodeIdRow) {
        appendEntry(nodeType, nodeIdRow, 1);
    }


    public void appendEntry(short nodeType, int nodeIdRow, int nodeIdCount) {
        int index = size();
        ensureAvailable();

        int offset = offset(index);
        data.putShort(nodeType, offset);
        offset += Sizeof.SHORT;
        data.putTryte(nodeIdRow, offset);
        offset += Sizeof.TRYTE;
        data.putTryte(nodeIdCount, offset);

        incrSize();
    }



    public void incrLastNodeIdCount() {
        incrLastNodeIdCount(1);
    }


    public void incrLastNodeIdCount(int amount) {
        int index = size() - 1;

        if (index < 0)
            throw new IllegalStateException("empty address table");
        if (amount < 0)
            throw new IllegalArgumentException("negative amount: " + amount);

        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        int count = data.getTryte(offset) + amount;
        data.putTryte(count, offset);
    }
}
