/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.util.list.BaseList;
import com.gnahraf.util.mem.Buff;
import com.gnahraf.util.datatypes.Primitives.Sizeof;
import com.gnahraf.util.mem.Table;

import java.util.List;

import static com.gnahraf.util.datatypes.Primitives.unsign;
import static com.gnahraf.util.datatypes.Primitives.unsignedShort;

/**
 * Table of edges referencing node types.
 */
public class EdgeTable extends Table {

    public final static int ROW_SIZE = Sizeof.SHORT + Sizeof.TRYTE + Sizeof.SHORT;


    private final List<EdgeType> edgeTypes;



    public EdgeTable(int initCapacity) {
        super(initCapacity);
        edgeTypes = getEdgeTypeView();
    }

    public EdgeTable(Buff data, int size) {
        super(data, size);
        edgeTypes = getEdgeTypeView();
    }





    @Override
    protected final int itemSize() {
        return ROW_SIZE;
    }


    public short getEdgeType(int index) {
        int offset = offset(index);
        return data.getShort(offset);
    }


    public int getNodeTypeRow(int index) {
        int offset = offset(index) + Sizeof.SHORT;
        return data.getTryte(offset);
    }


    public int getNodeTypeCount(int index) {
        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        return unsign(data.getShort(offset));
    }



    public List<EdgeType> asTypeList() {
        return edgeTypes;
    }


    public void appendEntry(short edgeType, int nodeTypeRow) {
        appendEntry(edgeType, nodeTypeRow, 1);
    }

    public void appendEntry(short edgeType, int nodeTypeRow, int nodeTypeCount) {
        ensureAvailable();
        int size = size();
        int offset = offset(size);
        data.putShort(edgeType, offset);
        offset += Sizeof.SHORT;
        data.putTryte(nodeTypeRow, offset);
        offset += Sizeof.TRYTE;
        data.putShort(unsignedShort(nodeTypeCount), offset);
        setSize(size + 1);
    }

    public void incrLastNodeTypeCount() {
        incrLastNodeTypeCount(1);
    }

    public void incrLastNodeTypeCount(int amount) {
        int index = size() - 1;

        if (index < 0)
            throw new IllegalStateException("empty edge table");
        if (amount < 0)
            throw new IllegalArgumentException("negative amount: " + amount);

        int count = getNodeTypeCount(index) + amount;
        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        data.putShort(unsignedShort(count), offset);

    }


    private List<EdgeType> getEdgeTypeView() {
        return new BaseList<EdgeType>() {
            @Override
            protected EdgeType getImpl(int location) {
                return new EdgeType(getEdgeType(location));
            }
            @Override
            public int size() {
                return EdgeTable.this.size();
            }
        };
    }

}
