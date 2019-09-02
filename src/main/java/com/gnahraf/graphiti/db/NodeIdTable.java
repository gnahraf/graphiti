/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;

import com.gnahraf.util.list.BaseList;
import com.gnahraf.util.mem.Buff;
import com.gnahraf.util.datatypes.Primitives;
import com.gnahraf.util.datatypes.Pinterval;
import com.gnahraf.util.mem.Table;

import java.util.List;

/**
 * Single-column table of 3-byte node IDs referenced from the {@linkplain NodeTypeTable}. The entries
 * here complete the specification of edges in our prefix encoded scheme.
 */
public class NodeIdTable extends Table {

    public final static int ROW_SIZE = Primitives.Sizeof.TRYTE;


    private final List<Integer> ids;


    public NodeIdTable(int initCapacity) {
        super(initCapacity);
        this.ids = idsView();
    }

    public NodeIdTable(Buff data, int size) {
        super(data, size);
        this.ids = idsView();
    }



    @Override
    protected final int itemSize() {
        return ROW_SIZE;
    }


    public int getNodeId(int index) {
        int offset = offset(index);
        return data.getTryte(offset);
    }



    public List<Integer> asList() {
        return ids;
    }


    private List<Integer> idsView() {
        return new BaseList<Integer>() {
            @Override
            public int size() {
                return NodeIdTable.this.size();
            }
            @Override
            protected Integer getImpl(int location) {
                return getNodeId(location);
            }
        };
    }



    public void appendEntry(int nodeId) {
        int index = size();
        ensureAvailable();

        int offset = offset(index);
        data.putTryte(nodeId, offset);
        incrSize();
    }


    public void appendEntries(NodeIdTable other, int row, int count) {
        Pinterval rows = new Pinterval(row, row + count + 1);
        copyFrom(other, rows, size());
    }
}
