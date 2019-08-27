/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by babak on 6/19/15.
 */
public class GraphMerger {



    public static Graph merge(Graph a, Graph b) {
        if (a == null || a.isEmpty())
            return b;
        if (b == null || b.isEmpty())
            return a;
        return new GraphMerger(a, b).merge();
    }



    private final AddressTable addressTable;
    private final EdgeTable edgeTable;
    private final NodeTypeTable nodeTypeTable;
    private final NodeIdTable nodeIdTable;

    private final Graph out;

    private MergeProgress loProgress;
    private MergeProgress hiProgress;


    private short srcNodeType;
    private int srcNodeId;
    private boolean inbound;
    private short edgeType;
    private short dtnNodeType;
    private int dtnNodeId;


    public GraphMerger(Graph a, Graph b) {

        if (a.isEmpty() || b.isEmpty()) {
            throw new IllegalArgumentException(a.isEmpty() ? "empty a" : "empty b");
        }

        addressTable = new AddressTable(a.addressTable.size() + b.addressTable.size());
        edgeTable = new EdgeTable(a.edgeTable.size() + b.edgeTable.size());
        nodeTypeTable = new NodeTypeTable(a.nodeTypeTable.size() + b.nodeTypeTable.size());
        nodeIdTable = new NodeIdTable(a.nodeIdTable.size() + b.nodeIdTable.size());

        out = new Graph(addressTable, edgeTable, nodeTypeTable, nodeIdTable);

        loProgress = new MergeProgress(a);
        hiProgress = new MergeProgress(b);

        sortProgress();
    }


    private void sortProgress() {
        long comp = loProgress.compareTo(hiProgress);
        if (comp > 0) {
            MergeProgress temp = loProgress;
            loProgress = hiProgress;
            hiProgress = temp;
        } else if (comp == 0) {
            hiProgress.next();
        }
    }



    public Graph merge() {
        if (!addressTable.isEmpty())
            throw new IllegalStateException("already invoked");


        syncToProgress();

        addressTable.appendEntry(srcNodeType, srcNodeId, inbound, 0);
        edgeTable.appendEntry(edgeType, 0);
        nodeTypeTable.appendEntry(dtnNodeType, 0);
        nodeIdTable.appendEntry(dtnNodeId);

        // As far as the internal model is concerned, this is now a valid
        // graph. (It violates the API requirement that every edge has an inverse).
        // The cursor below would work..
        // MergeProgress cursor = new MergeProgress(out);

        advanceProgress();


        while (!loProgress.isAtEnd()) {



            long srcComp = compare(
                    srcNodeType, srcNodeId, loProgress.srcNodeType, loProgress.srcNodeId);
            if (srcComp != 0) {
                if (srcComp > 0) {
                    throw new IllegalStateException("corrupt graph");
                }

                addressTable.appendEntry(
                        loProgress.srcNodeType, loProgress.srcNodeId,
                        loProgress.inbound, edgeTable.size());
                edgeTable.appendEntry(loProgress.edgeType, nodeTypeTable.size());
                nodeTypeTable.appendEntry(loProgress.dtnNodeType, nodeIdTable.size());

            } else if (inbound != loProgress.inbound) {
                if (inbound)
                    throw new IllegalStateException("inbound " + inbound + "; " + loProgress);

                addressTable.setLastEdge(loProgress.inbound, edgeTable.size(), 1);
                edgeTable.appendEntry(loProgress.edgeType, nodeTypeTable.size());
                nodeTypeTable.appendEntry(loProgress.dtnNodeType, nodeIdTable.size());

            } else if (edgeType != loProgress.edgeType) {
                assertLessThan(edgeType, loProgress.edgeType, "edgeType, loProgress.edgeType");

                addressTable.incrLastEdgeCount(inbound);
                edgeTable.appendEntry(loProgress.edgeType, nodeTypeTable.size());
                nodeTypeTable.appendEntry(loProgress.dtnNodeType, nodeIdTable.size());

            } else if (dtnNodeType != loProgress.dtnNodeType) {
                assertLessThan(dtnNodeType, loProgress.dtnNodeType, "dtnNodeType, loProgress.dtnNodeType");

                edgeTable.incrLastNodeTypeCount();
                nodeTypeTable.appendEntry(loProgress.dtnNodeType, nodeIdTable.size());

            } else {
                assertLessThan(dtnNodeId, loProgress.dtnNodeId, "dtnNodeId, loProgress.dtnNodeId");

                nodeTypeTable.incrLastNodeIdCount();
            }


            nodeIdTable.appendEntry(loProgress.dtnNodeId);

            syncToProgress();
            advanceProgress();
        }

        return out;
    }


    private void assertLessThan(int less, int more, String msg) {
        if (less >= more)
            throw new IllegalStateException(less + " >= " + more + ": " + msg);
    }


    private void advanceProgress() {
        loProgress.next();
        sortProgress();
    }


    private void syncToProgress() {
        srcNodeType = loProgress.srcNodeType;
        srcNodeId = loProgress.srcNodeId;
        inbound = loProgress.inbound;
        edgeType = loProgress.edgeType;
        dtnNodeType = loProgress.dtnNodeType;
        dtnNodeId = loProgress.dtnNodeId;
    }



//    private short endSrcNodeType() {
//        int index = addressTable.size() - 1;
//        return addressTable.getNodeType(index);
//    }
//
//
//    private int endSrcNodeId() {
//        int index = addressTable.size() - 1;
//        return addressTable.getNodeId(index);
//    }




    static long compare(short aType, int aId, short bType, int bId) {
        long comp = aType;
        comp -= bType;
        if (comp != 0)
            return comp;
        comp = aId;
        comp -= bId;
        return comp;
    }

}



class MergeProgress {

    Graph g;

    int srcNodeIndex;
    int lastSrcNodeIndex;
    short srcNodeType;
    int srcNodeId;

    boolean inbound;

    int edgeIndex;
    int lastEdgeIndex;
    short edgeType;

    int dtnNodeTypeIndex;
    int lastDtnNodeTypeIndex;
    short dtnNodeType;

    int dtnNodeIdIndex;
    int lastDtnNodeIdIndex;
    int dtnNodeId;


    MergeProgress(MergeProgress copy) {
        copy(copy);
    }

    MergeProgress(Graph g) {
        this.g = g;
        this.lastSrcNodeIndex = g.addressTable.size() - 1;
        loadSrcNode();
    }


    void copy(MergeProgress other) {
        g = other.g;

        srcNodeIndex = other.srcNodeIndex;
        lastSrcNodeIndex = other.lastSrcNodeIndex;
        srcNodeType = other.srcNodeType;
        srcNodeId = other.srcNodeId;

        inbound = other.inbound;
        edgeIndex = other.edgeIndex;
        lastEdgeIndex = other.lastEdgeIndex;
        edgeType = other.edgeType;

        dtnNodeTypeIndex = other.dtnNodeTypeIndex;
        lastDtnNodeTypeIndex = other.lastDtnNodeTypeIndex;
        dtnNodeType = other.dtnNodeType;

        dtnNodeIdIndex = other.dtnNodeIdIndex;
        lastDtnNodeIdIndex = other.lastDtnNodeIdIndex;
        dtnNodeId = other.dtnNodeId;

        atEnd = other.atEnd;    // (for sanity)
    }

    void loadSrcNode() {
        loadSrcNode(true);
    }

    void loadSrcNode(boolean cascade) {
        srcNodeType = g.addressTable.getNodeType(srcNodeIndex);
        srcNodeId = g.addressTable.getNodeId(srcNodeIndex);
        // position to outbound, if any
        inbound = !hasEdges(false);

        if (cascade)
            loadEdgeDirection(true);
    }


    void loadEdgeDirection() {
        loadEdgeDirection(true);
    }

    void loadEdgeDirection(boolean cascade) {
        edgeIndex = g.addressTable.getEdgeRow(inbound, srcNodeIndex);
        lastEdgeIndex =
                edgeIndex + g.addressTable.getEdgeRowCount(inbound, srcNodeIndex) - 1;

        if (cascade)
            loadEdgeType(true);
    }


    void loadEdgeType() {
        loadEdgeType(true);
    }

    void loadEdgeType(boolean cascade) {
        edgeType = g.edgeTable.getEdgeType(edgeIndex);
        dtnNodeTypeIndex = g.edgeTable.getNodeTypeRow(edgeIndex);
        lastDtnNodeTypeIndex =
                dtnNodeTypeIndex + g.edgeTable.getNodeTypeCount(edgeIndex) - 1;

        if (cascade)
            loadDtnNodeType(true);
    }


    void loadDtnNodeType() {
        loadDtnNodeType(true);
    }

    void loadDtnNodeType(boolean cascade) {
        dtnNodeType = g.nodeTypeTable.getNodeType(dtnNodeTypeIndex);
        dtnNodeIdIndex = g.nodeTypeTable.getNodeIdRow(dtnNodeTypeIndex);
        lastDtnNodeIdIndex =
                dtnNodeIdIndex + g.nodeTypeTable.getNodeIdCount(dtnNodeTypeIndex) - 1;
        if (cascade)
            loadDtnNodeId();
    }

    void loadDtnNodeId() {
        dtnNodeId = g.nodeIdTable.getNodeId(dtnNodeIdIndex);
    }


    boolean next() {
        advanceDtnNodeId();
        return !atEnd;
    }
    private boolean atEnd;


    boolean isAtEnd() {
        return atEnd;
    }


    long compareTo(MergeProgress other) {
        if (atEnd || other.atEnd) {
            if (atEnd)
                return other.atEnd ? 0 : 1;
            else
                return -1;
        }

        long comp = GraphMerger.compare(srcNodeType, srcNodeId, other.srcNodeType, other.srcNodeId);
        if (comp != 0)
            return comp;
        if (inbound != other.inbound)
            return inbound ? 1 : -1;
        comp = ((int) edgeType) - other.edgeType;
        if (comp != 0)
            return comp;
        comp = GraphMerger.compare(dtnNodeType, dtnNodeId, other.dtnNodeType, other.dtnNodeId);
        return comp;
    }



    void advanceAfter(MergeProgress other) {
        // sanity check
        if (g == other.g)
            throw new IllegalArgumentException("cannot apply to same graph " + g);

        for (long comp = compareTo(other); comp <= 0; ) {
            if (comp == 0) {
                next();
                assert compareTo(other) > 0;
                break;
            }
            nudgeToAfter(other);
            comp = compareTo(other);
        }
    }


    private void nudgeToAfter(MergeProgress other) {
        // compare the srcNodes
        long comp = GraphMerger.compare(srcNodeType, srcNodeId, other.srcNodeType, other.srcNodeId);

        // if the src nodes don't match, advance this
        if (comp != 0) {
            assert comp < 0;
            advanceSrcNode(other);

        } else if (inbound != other.inbound) {
            assert other.inbound;
            advanceEdgeDirection();

        } else if (edgeType != other.edgeType) {
            assert edgeType < other.edgeType;
            advanceEdgeType(other);

        } else if (dtnNodeType != other.dtnNodeType) {
            assert dtnNodeType < other.dtnNodeType;
            advanceDtnNodeType(other);

        } else {
            assert dtnNodeId < other.dtnNodeId;
            advanceDtnNodeId(other);
        }
    }



    private void advanceSrcNode(MergeProgress other) {
        int index = g.addressTable.indexOf(other.srcNodeType, other.srcNodeId);

        if (index < 0 && (index = -index - 1) == g.addressTable.size()) {
            advanceToEnd();

        } else {
            assert index > srcNodeIndex;

            srcNodeIndex = index;
            loadSrcNode();
        }
    }


    private void advanceSrcNode() {
        if (srcNodeIndex == lastSrcNodeIndex) {
            advanceToEnd();
        } else {
            ++srcNodeIndex;
            loadSrcNode();
        }
    }


    private void advanceEdgeDirection() {
        if (inbound || !hasEdges(true)) {
            advanceSrcNode();
        } else {
            inbound = true;
            loadEdgeDirection();
        }
    }



    private void advanceEdgeType(MergeProgress other) {
        while (edgeIndex != lastEdgeIndex && edgeType < other.edgeType) {
            // Performance note: could speed up (but likely unnecessary)
            ++edgeIndex;
            loadEdgeType(false);
        }

        if (edgeType < other.edgeType) {
            advanceEdgeDirection();
        } else {
            // finish the cascade
            loadDtnNodeType();
        }
    }

    private void advanceEdgeType() {
        if (edgeIndex == lastEdgeIndex) {
            advanceEdgeDirection();
        } else {
            ++edgeIndex;
            loadEdgeType();
        }
    }



    private void advanceDtnNodeType(MergeProgress other) {
        while (dtnNodeTypeIndex != lastDtnNodeTypeIndex && dtnNodeType < other.dtnNodeType) {
            // Performance note: could speed up (but likely unnecessary)
            ++dtnNodeTypeIndex;
            loadDtnNodeType(false);
        }

        if (dtnNodeType < other.dtnNodeType) {
            advanceEdgeType();
        } else {
            // finish the cascade
            loadDtnNodeId();
        }
    }

    private void advanceDtnNodeType() {
        if (dtnNodeTypeIndex == lastDtnNodeTypeIndex) {
            advanceEdgeType();
        } else {
            ++dtnNodeTypeIndex;
            loadDtnNodeType();
        }
    }





    private void advanceDtnNodeId(MergeProgress other) {
        int remainingIds = lastDtnNodeIdIndex - dtnNodeIdIndex;
        if (remainingIds > 128 && other.dtnNodeId - dtnNodeId > 128) {
            List<Integer> nodeIds = g.nodeIdTable.asList();
            nodeIds = nodeIds.subList(dtnNodeIdIndex + 1, lastDtnNodeIdIndex + 1);
            int index = Collections.binarySearch(nodeIds, other.dtnNodeId);
            if (index < 0) {
                // index encodes the insertion point..
                index = -index - 1;
                // ..so the nodeId at index, if any, is > other.dtnNodeId
            } else {
                // the nodeId at index is == other.dtnNodeId, so we advance the index
                ++index;
            }
            dtnNodeIdIndex += index + 1;
            if (dtnNodeIdIndex > lastDtnNodeIdIndex) {
                advanceDtnNodeType();
            } else {
                loadDtnNodeId();
            }
        } else {
            while (dtnNodeIdIndex != lastDtnNodeIdIndex && dtnNodeId <= other.dtnNodeId) {
                ++dtnNodeIdIndex;
                loadDtnNodeId();
            }
            if (dtnNodeId <= other.dtnNodeId) {
                advanceDtnNodeType();
            }
        }
    }

    private void advanceDtnNodeId() {
        if (dtnNodeIdIndex == lastDtnNodeIdIndex) {
            advanceDtnNodeType();
        } else {
            ++dtnNodeIdIndex;
            loadDtnNodeId();
        }
    }




    private boolean hasEdges(boolean inbound) {
        int count = g.addressTable.getEdgeRowCount(inbound, srcNodeIndex);
        assert count >= 0;
        return count != 0;
    }



    private void advanceToEnd() {
        if (atEnd)
            return;

        atEnd = true;

        // inconsequential block below; for bookkeeping, only.
        // comment out later..
        srcNodeIndex = lastSrcNodeIndex;
        loadSrcNode(false);
        if (!inbound) {
            inbound = hasEdges(true);
        }
        loadEdgeDirection(false);
        edgeIndex = lastEdgeIndex;
        loadEdgeType(false);
        dtnNodeTypeIndex = lastDtnNodeTypeIndex;
        loadDtnNodeType(false);
        dtnNodeIdIndex = lastDtnNodeIdIndex;
        loadDtnNodeId();
    }


    @Override
    public String toString() {
        Object[] coordinates = {
                srcNodeType,
                srcNodeId,
                inbound,
                edgeType,
                dtnNodeType,
                dtnNodeId
        };
        return getClass().getSimpleName() + Arrays.asList(coordinates).toString();
    }


}






