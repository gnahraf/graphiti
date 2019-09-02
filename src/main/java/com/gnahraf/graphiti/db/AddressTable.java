/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import com.gnahraf.util.list.BaseList;
import com.gnahraf.util.mem.Buff;
import com.gnahraf.util.datatypes.ShortInt;
import com.gnahraf.util.datatypes.ShortIntId;
import com.gnahraf.util.mem.Table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static com.gnahraf.util.datatypes.Primitives.*;

/**
 * Node lookup table. A table of node [edge] addresses sorted by (qualified)
 * node IDs of the form <tt>(nodeType, nodeId)</tt>.
 *
 * <h3>Binary Format</h3>
 * <br/>
 * <pre>{@literal
 *
 * <ROW> : <N.TYPE><N.ID> <<E.IDX><E.COUNT>> <<E.IDX><E.COUNT>>
 *
 * }
 * </pre>
 * where <br/>
 * <pre>{@literal
 *
 * <N.TYPE>  : <SHORT>
 * <N.ID>    : <TRYTE>
 * <E.IDX>   : <TRYTE>
 * <E.COUNT> : <SHORT>
 *
 * }
 * </pre><br/>
 * 
 * The table is thus sorted by {@literal <N.TYPE><N.ID> } which can be considered a row's key:
 * the row's data are 2 other "columns", which are indexes into the edge table each comprising of a
 * range of row numbers in that table for both in-bound and out-bound edges.
 * 
 * <p/>
 * 
 * 
 * 
 * @see EdgeTable
 */
public class AddressTable extends Table {



	/**
	 * A row in the address table. Each row represents a node.
	 */
    public static class Address extends ShortIntId implements Comparable<Address> {

        private final int inEdgeRow;
        private final int inEdgeRowCount;
        private final int outEdgeRow;
        private final int outEdgeRowCount;

        public Address(
                short nodeType, int nodeId,
                int inEdgeRow, int inEdgeRowCount, int outEdgeRow, int outEdgeRowCount) {

            super(nodeType, nodeId);

            this.inEdgeRow = inEdgeRow;
            this.inEdgeRowCount = inEdgeRowCount;
            this.outEdgeRow = outEdgeRow;
            this.outEdgeRowCount = outEdgeRowCount;

            if (inEdgeRow < 0 || inEdgeRowCount < 0 || outEdgeRow < 0 || outEdgeRowCount < 0) {
                Integer[] args = {
                        (int) nodeType, nodeId,
                        inEdgeRow, inEdgeRowCount,
                        outEdgeRow, outEdgeRowCount
                };
                throw new IllegalArgumentException(Arrays.asList(args).toString());
            }
        }

        @Override
        public int compareTo(Address another) {
            return super.compareTo(another);
        }



        /**
         * Returns the number of row edge types in the specified direction for this node.
         * Note this is the number of edge <em>types</em>, which is likely fewer than the
         * number of edges. These can number at most 64k.
         * 
         * @param inbound the direction (<tt>false</tt> for out-bound)
         * 
         * @return the number of rows in the edge table, possibly zero. 
         * 
         * @see #getEdgeRow(boolean)
         */
        public int getEdgeRowCount(boolean inbound) {
            return inbound ? inEdgeRowCount : outEdgeRowCount;
        }

        /**
         * Returns the row number in the {@linkplain EdgeTable} for the first edge [type] in the
         * given direction.
         * 
         * @param inbound the direction (<tt>false</tt> for out-bound)
         * 
         * @return the row number in the edge table, or zero if there's no such edge
         * 
         * @see #getEdgeRowCount(boolean)
         */
        public int getEdgeRow(boolean inbound) {
            return inbound ? inEdgeRow : outEdgeRow;
        }
    }



    public final static int ROW_SIZE =
            Sizeof.SHORT +
            Sizeof.TRYTE +
            2 * (
            Sizeof.TRYTE +
            Sizeof.SHORT );


    private final List<ShortInt> nodeIds;



    public AddressTable(int initCapacity) {
        super(initCapacity);
        nodeIds = nodeIdView();
    }


    public AddressTable(Buff data, int size) {
        super(data, size);
        nodeIds = nodeIdView();
    }


    public Address getAddress(short nodeType, int nodeId) {
        int index = indexOf(nodeType, nodeId);
        if (index < 0)
            return null;

        // we skip over below, for o.w. index would be negative..
        // i.e. we already *know what we'll read in the commented out code

//        int offset = offset(index);
//        short type = data.getShort(offset);
//        offset += Sizeof.SHORT;
//        int id = data.getTryte(offset);
//        assert(type == nodeType && id == nodeId);
//        offset += Sizeof.TRYTE;

        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        
        int outEdgeRow = data.getTryte(offset);
        offset += Sizeof.TRYTE;
        int outEdgeRowCount = unsign( data.getShort(offset) );
        offset += Sizeof.SHORT;
        int inEdgeRow = data.getTryte(offset);
        offset += Sizeof.TRYTE;
        int inEdgeRowCount = unsign( data.getShort(offset) );

        return new Address(
                nodeType, nodeId,
                inEdgeRow, inEdgeRowCount,
                outEdgeRow, outEdgeRowCount);
    }


    public short getNodeType(int index) {
        int offset = offset(index);
        return data.getShort(offset);
    }


    public int getNodeId(int index) {
        int offset = offset(index) + Sizeof.SHORT;
        return data.getTryte(offset);
    }


    public int getEdgeRow(boolean inbound, int index) {
        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE;
        if (inbound)
            offset += Sizeof.TRYTE + Sizeof.SHORT;
        return data.getTryte(offset);
    }


    public int getEdgeRowCount(boolean inbound, int index) {
        int offset = edgeRowCountOffset(inbound, index);
        return unsign(data.getShort(offset));
    }


    private int edgeRowCountOffset(boolean inbound, int index) {
        int offset = offset(index) + Sizeof.SHORT + Sizeof.TRYTE + Sizeof.TRYTE;
        if (inbound)
            offset += Sizeof.SHORT + Sizeof.TRYTE;
        return offset;
    }


    public void appendEntry(
            short nodeType, int nodeId) {

        appendEntry(nodeType, nodeId, 0, 0, 0, 0);
    }


    public void appendEntry(
            short nodeType, int nodeId, boolean inbound, int edgeRow) {

        if (inbound)
            appendEntry(nodeType, nodeId, 0, 0, edgeRow, 1);
        else
            appendEntry(nodeType, nodeId, edgeRow, 1, 0, 0);
    }


    public void appendEntry(
            short nodeType, int nodeId,
            int outEdgeRow, int outEdgeRowCount,
            int inEdgeRow, int inEdgeRowCount) {

        int offset;
        {
            int index = size();

            // guard against out of sequence entries
            if (index > 0) {
                int end = index - 1;
                short lastNodeType = getNodeType(end);
                if (nodeType < lastNodeType)
                    failIllegalArgs("last node type is " + lastNodeType,
                            nodeType, nodeId,
                            outEdgeRow, outEdgeRowCount,
                            inEdgeRow, inEdgeRowCount);
                else if (nodeType == lastNodeType) {
                    int lastNodeId = getNodeId(end);
                    if (nodeId <= lastNodeId)
                        failIllegalArgs("last node is (" + lastNodeType + "," + lastNodeId + ")",
                                nodeType, nodeId,
                                outEdgeRow, outEdgeRowCount,
                                inEdgeRow, inEdgeRowCount);
                }
            }

            ensureAvailable();
            offset = offset(index);
        }

        data.putShort(nodeType, offset);
        offset += Sizeof.SHORT;
        data.putTryte(nodeId, offset);
        offset += Sizeof.TRYTE;
        data.putTryte(outEdgeRow, offset);
        offset += Sizeof.TRYTE;
        data.putShort(unsignedShort(outEdgeRowCount), offset);
        offset += Sizeof.SHORT;
        data.putTryte(inEdgeRow, offset);
        offset += Sizeof.TRYTE;
        data.putShort(unsignedShort(inEdgeRowCount), offset);

        incrSize();
    }



    public void incrLastEdgeCount(boolean inbound) {
        incrLastEdgeCount(inbound, 1);
    }


    public void setLastEdge(boolean inbound, int edgeRow, int edgeRowCount) {
        int offset = offset(lastIndex()) + Sizeof.SHORT + Sizeof.TRYTE;
        if (inbound)
            offset += Sizeof.TRYTE + Sizeof.SHORT;
        short usEdgeRowCount = unsignedShort(edgeRowCount);
        data.putTryte(edgeRow, offset);
        offset += Sizeof.TRYTE;
        data.putShort(usEdgeRowCount, offset);
    }


    public void incrLastEdgeCount(boolean inbound, int amount) {
        int index = size() - 1;

        if (index < 0)
            throw new IllegalStateException("empty address table");
        if (amount < 0)
            throw new IllegalArgumentException("negative amount: " + amount);
        if (!inbound && getEdgeRowCount(true, index) > 0)
            throw new IllegalStateException(
                    "attempt to increment outbound edge rows after inbound edge rows set");

        int count = getEdgeRowCount(inbound, index) + amount;
        if (count == amount)
            throw new IllegalStateException(
                    "attempt to increment edge count before edge row set");
        int offset = edgeRowCountOffset(inbound, index);
        data.putShort(unsignedShort(count), offset);
    }


    private void failIllegalArgs(
            String caption,
            short nodeType, int nodeId,
            int outEdgeRow, int outEdgeRowCount,
            int inEdgeRow, int inEdgeRowCount) {
        Object[] args = {
            nodeType, nodeId, outEdgeRow, outEdgeRowCount, inEdgeRow, inEdgeRowCount
        };
        String msg = Arrays.asList(args).toString();
        if (caption != null)
            msg = caption + ": " + msg;
        throw new IllegalArgumentException(msg);
    }


    @Override
    protected final int itemSize() {
        return ROW_SIZE;
    }


    protected int indexOf(short nodeType, int nodeId) {
        return Collections.binarySearch(nodeIds, new ShortInt(nodeType, nodeId));
    }



    public List<ShortInt> getNodeIds() {
        return nodeIds;
    }




    private List<ShortInt> nodeIdView() {

        return new BaseList<ShortInt>() {

            @Override
            protected ShortInt getImpl(int location) {
                int offset = offset(location);
                short nodeType = data.getShort(offset);
                offset += Sizeof.SHORT;
                int nodeId = data.getTryte(offset);
                return new ShortInt(nodeType, nodeId);
            }
            @Override
            public int size() {
                return AddressTable.this.size();
            }
        };
    }


}
