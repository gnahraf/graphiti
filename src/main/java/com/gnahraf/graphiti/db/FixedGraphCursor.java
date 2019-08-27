/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import com.gnahraf.graphiti.db.AddressTable.Address;
import com.gnahraf.graphiti.model.Cursor;
import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.datatypes.ShortInt;


import java.util.Collections;
import java.util.List;

/**
 * Created by babak on 6/18/15.
 */
public class FixedGraphCursor extends Cursor {


    private final AddressTable addressTable;
    private final EdgeTable edgeTable;
    private final NodeTypeTable nodeTypeTable;
    private final NodeIdTable nodeIdTable;


    private Address activeNode;

    public FixedGraphCursor(
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

        if (addressTable.size() == 0)
            throw new IllegalArgumentException("empty addressTable");

        ShortInt first = addressTable.getNodeIds().get(0);
        moveTo(new NodeType(first.getType()), first.getId());
    }


    @Override
    protected boolean loadVertex(NodeType nodeType, int nodeId) {
        Address address = addressTable.getAddress(nodeType.getId(), nodeId);
        if (address == null)
            return false;

        this.activeNode = address;
        return true;
    }

    @Override
    public List<EdgeType> getEdgeTypes(boolean inbound) {
        int count = activeNode.getEdgeRowCount(inbound);
        if (count == 0)
            return Collections.emptyList();
        int row = activeNode.getEdgeRow(inbound);
        return edgeTable.asTypeList().subList(row, row + count);
    }

    @Override
    public int getEdgeCount(boolean inbound) {
        int edgeTypeCount = activeNode.getEdgeRowCount(inbound);
        if (edgeTypeCount == 0)
            return 0;
        int edgeTypeRow = activeNode.getEdgeRow(inbound);
        int lastEdgeTypeRow = edgeTypeRow + edgeTypeCount;
        int count = 0;
        for (; edgeTypeRow < lastEdgeTypeRow; ++edgeTypeRow) {

            int nodeTypeRow = edgeTable.getNodeTypeRow(edgeTypeRow);
            int lastNodeTypeRow = nodeTypeRow + edgeTable.getNodeTypeCount(edgeTypeRow);

            for (; nodeTypeRow < lastNodeTypeRow; ++nodeTypeRow)
                count += nodeTypeTable.getNodeIdCount(nodeTypeRow);
        }
        return count;
    }

    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType) {
        int edgeRow = edgeRow(inbound, edgeType);
        if (edgeRow == -1)
            return 0;
        int count = 0;
        int nodeTypeRow = edgeTable.getNodeTypeRow(edgeRow);
        int lastNodeTypeRow = nodeTypeRow + edgeTable.getNodeTypeCount(edgeRow);
        for (; nodeTypeRow < lastNodeTypeRow; ++nodeTypeRow)
            count += nodeTypeTable.getNodeIdCount(nodeTypeRow);
        return count;
    }

    @Override
    public List<NodeType> getNodeTypes(boolean inbound, EdgeType edgeType) {
        int edgeRow = edgeRow(inbound, edgeType);
        if (edgeRow == -1)
            return Collections.emptyList();

        int nodeTypeRow = edgeTable.getNodeTypeRow(edgeRow);
        int nodeTypeCount = edgeTable.getNodeTypeCount(edgeRow);
        return nodeTypeTable.asTypeList().subList(nodeTypeRow, nodeTypeRow + nodeTypeCount);
    }


    private int edgeRow(boolean inbound, EdgeType edgeType) {
        int edgeTypeCount = activeNode.getEdgeRowCount(inbound);
        if (edgeTypeCount == 0)
            return -1;
        int edgeRow = activeNode.getEdgeRow(inbound);
        int lastEdgeRow = edgeRow + edgeTypeCount;
        if (edgeTypeCount <= 128) {
            while (edgeRow < lastEdgeRow) {
                short type = edgeTable.getEdgeType(edgeRow);
                if (type < edgeType.getId()) {
                    ++edgeRow;
                    continue;
                }
                if (type > edgeType.getId())
                    edgeRow = lastEdgeRow;
                break;
            }
            if (edgeRow == lastEdgeRow)
                return -1;
        } else {
            List<EdgeType> edgeTypes =
                    edgeTable.asTypeList().subList(edgeRow, lastEdgeRow);
            int relativeIndex = Collections.binarySearch(edgeTypes, edgeType);
            if (relativeIndex < 0)
                return -1;
            edgeRow += relativeIndex;
        }
        return edgeRow;
    }


    private int nodeTypeRow(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        int edgeRow = edgeRow(inbound, edgeType);
        return nodeTypeRow(edgeRow, nodeType);
    }


    private int nodeTypeRow(int edgeRow, NodeType nodeType) {
        if (edgeRow == -1)
            return -1;
        int nodeTypeRow = edgeTable.getNodeTypeRow(edgeRow);
        int nodeTypeCount = edgeTable.getNodeTypeCount(edgeRow);
        int lastNodeTypeRow = nodeTypeRow + nodeTypeCount;

        if (nodeTypeCount <= 128) {
            while (nodeTypeRow < lastNodeTypeRow) {
                short type = nodeTypeTable.getNodeType(nodeTypeRow);
                if (type < nodeType.getId()) {
                    ++nodeTypeRow;
                    continue;
                }
                if (type > nodeType.getId())
                    nodeTypeRow = lastNodeTypeRow;
                break;
            }
            if (nodeTypeRow == lastNodeTypeRow)
                return -1;
        } else {
            List<NodeType> nodeTypes =
                    nodeTypeTable.asTypeList().subList(nodeTypeRow, lastNodeTypeRow);
            int relativeIndex = Collections.binarySearch(nodeTypes, nodeType);
            if (relativeIndex < 0)
                return -1;
            nodeTypeRow += relativeIndex;
        }
        return nodeTypeRow;
    }


    @Override
    public int getEdgeCount(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        int nodeTypeRow = nodeTypeRow(inbound, edgeType, nodeType);
        if (nodeTypeRow == -1)
            return 0;
        return nodeTypeTable.getNodeIdCount(nodeTypeRow);
    }


    @Override
    public List<Integer> getNodeIds(boolean inbound, EdgeType edgeType, NodeType nodeType) {
        int nodeTypeRow = nodeTypeRow(inbound, edgeType, nodeType);
        if (nodeTypeRow == -1)
            return Collections.emptyList();
        int nodeIdRow = nodeTypeTable.getNodeIdRow(nodeTypeRow);
        int nodeIdCount = nodeTypeTable.getNodeIdCount(nodeTypeRow);
        return nodeIdTable.asList().subList(nodeIdRow, nodeIdRow + nodeIdCount);
    }


    @Override
    public List<ShortInt> getNodeIds() {
        return addressTable.getNodeIds();
    }
}
