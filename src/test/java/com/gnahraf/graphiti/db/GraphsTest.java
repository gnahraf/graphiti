/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;


import com.gnahraf.graphiti.model.Cursor;
import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.datatypes.ShortInt;

import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by babak on 6/24/15.
 */
public class GraphsTest {

    @Test
    public void testMinimal() {
        final short srcNodeType = 1;
        final int srcNodeId = 5;
        final short edgeType = -3;
        final short dtnNodeType = 1;
        final int dtnNodeId = 6;

        GraphBuilder builder = new GraphBuilder();
        assertTrue(builder.isEmpty());

        builder.insertEdge(
                srcNodeType, srcNodeId,
                edgeType,
                dtnNodeType, dtnNodeId
        );

        Graph graph = builder.build();
        Cursor cursor = graph.newCursor();

        List<ShortInt> nodeIds = cursor.getQualifiedNodeIds();
        assertEquals(2, nodeIds.size());
        ShortInt srcNode = nodeIds.get(0);
        assertEquals(srcNodeType, srcNode.getType());
        assertEquals(srcNodeId, srcNode.getId());
        ShortInt dtnNode = nodeIds.get(1);
        assertEquals(dtnNodeType, dtnNode.getType());
        assertEquals(dtnNodeId, dtnNode.getId());


        NodeType srcType = new NodeType(srcNodeType);
        NodeType dtnType = new NodeType(dtnNodeType);
        EdgeType eType = new EdgeType(edgeType);

        cursor.moveTo(srcType, srcNodeId);
        assertSingularEdge(false, eType, dtnType, dtnNodeId, cursor);

        cursor.moveTo(dtnType, dtnNodeId);
        assertSingularEdge(true, eType, srcType, srcNodeId, cursor);

    }
    @Test
    public void testMinimal2() {
        final short srcNodeType = 2;
        final int srcNodeId = 5;
        final short edgeType = -3;
        final short dtnNodeType = 1;
        final int dtnNodeId = 6;

        GraphBuilder builder = new GraphBuilder();
        assertTrue(builder.isEmpty());

        builder.insertEdge(
                srcNodeType, srcNodeId,
                edgeType,
                dtnNodeType, dtnNodeId
        );

        Graph graph = builder.build();
        Cursor cursor = graph.newCursor();

        List<ShortInt> nodeIds = cursor.getQualifiedNodeIds();
        assertTrue(nodeIds.contains(new ShortInt(srcNodeType, srcNodeId)));
        assertTrue(nodeIds.contains(new ShortInt(dtnNodeType, dtnNodeId)));


        NodeType srcType = new NodeType(srcNodeType);
        NodeType dtnType = new NodeType(dtnNodeType);
        EdgeType eType = new EdgeType(edgeType);

        cursor.moveTo(srcType, srcNodeId);
        assertSingularEdge(false, eType, dtnType, dtnNodeId, cursor);

        cursor.moveTo(dtnType, dtnNodeId);
        assertSingularEdge(true, eType, srcType, srcNodeId, cursor);

    }

    @Test
    public void testV() {
        final short edgeType = 3;
        Number[][] edges = {
            new Short[] {       -3,        1,},
            new Integer[] {     11,       12,},
            new Short[] { edgeType, edgeType,},
            new Short[] {        1,        1,},
            new Integer[] {     10,       10,},
        };

        GraphBuilder builder = new GraphBuilder();
        int count = edges[0].length;
        Set<ShortInt> expectedNodes = new HashSet<>();
        for (int i = 0; i < count; ++i) {
            ShortInt srcNode = new ShortInt(edges[0][i].shortValue(), edges[1][i].intValue());
            short eType = edges[2][i].shortValue();
            ShortInt dtnNode = new ShortInt(edges[3][i].shortValue(), edges[4][i].intValue());
            builder.insertEdge(
                    srcNode.getType(),
                    srcNode.getId(),
                    eType,
                    dtnNode.getType(),
                    dtnNode.getId());
            expectedNodes.add(srcNode);
            expectedNodes.add(dtnNode);
        }


        Graph graph = builder.build();

        Cursor cursor = graph.newCursor();
        List<ShortInt> nodes = cursor.getQualifiedNodeIds();
        for (ShortInt expected : expectedNodes) {
            assertTrue(nodes.contains(expected));
        }
        assertEquals(expectedNodes.size(), nodes.size());

        ShortInt nexus = new ShortInt(1, 10);
        cursor.moveTo(new NodeType(nexus.getType()), nexus.getId());

        assertEquals(0, cursor.getEdgeCount(false));
        assertEmpty(cursor.getEdgeTypes(false));

        assertEquals(2, cursor.getEdgeCount(true));
        assertEquals(1, cursor.getEdgeTypes(true).size());
        EdgeType e = cursor.getEdgeTypes(true).get(0);
        assertEquals(edgeType, e.getId());

        assertEquals(2, cursor.getNodeTypes(true, e).size());
        NodeType nT0 = new NodeType(edges[0][0].shortValue());
        NodeType nT1 = new NodeType(edges[0][1].shortValue());
        assertEquals(nT0, cursor.getNodeTypes(true, e).get(0));
        assertEquals(nT1, cursor.getNodeTypes(true, e).get(1));

        assertEquals(edges[1][0], cursor.getNodeIds(true, e, nT0).get(0).intValue());
        assertEquals(edges[1][1], cursor.getNodeIds(true, e, nT1).get(0).intValue());

        assertEmpty(cursor.getNodeIds(true, e, new NodeType(0)));
        assertEmpty(cursor.getNodeTypes(true, new EdgeType(0)));

        int nI0 = cursor.getNodeIds(true, e, nT0).get(0);
        int nI1 = cursor.getNodeIds(true, e, nT1).get(0);

        cursor.moveTo(nT0, nI0);
        assertSingularEdge(false, e, new NodeType(nexus.getType()), nexus.getId(), cursor);

        cursor.moveTo(nT1, nI1);
        assertSingularEdge(false, e, new NodeType(nexus.getType()), nexus.getId(), cursor);
    }




    @Test
    public void testAFew() {

        Number[][] edges = {
                new Short[] {    -3,  1,  1,  9,  9,  5,},
                new Integer[] {  11, 10, 12, 84, 84, 55,},
                new Short[] {    1,   1,  1,  2,  1,  3,},
                new Short[] {    1,   2,  1,  2,  1,  2,},
                new Integer[] { 10,  27, 10, 27, 10, 27,},
        };

        GraphBuilder builder = new GraphBuilder();
        final int count = edges[0].length;
        Set<ShortInt> expectedNodes = new HashSet<>();
        for (int i = 0; i < count; ++i) {
            ShortInt srcNode = new ShortInt(edges[0][i].shortValue(), edges[1][i].intValue());
            short eType = edges[2][i].shortValue();
            ShortInt dtnNode = new ShortInt(edges[3][i].shortValue(), edges[4][i].intValue());
            builder.insertEdge(
                    srcNode.getType(),
                    srcNode.getId(),
                    eType,
                    dtnNode.getType(),
                    dtnNode.getId());
            expectedNodes.add(srcNode);
            expectedNodes.add(dtnNode);
        }


        Graph graph = builder.build();

        Cursor cursor = graph.newCursor();
        List<ShortInt> nodes = cursor.getQualifiedNodeIds();
        for (ShortInt expected : expectedNodes) {
            assertTrue(nodes.contains(expected));
        }
        assertEquals(expectedNodes.size(), nodes.size());

        for (int i = 0; i < count; ++i) {
            NodeType srcType = new NodeType(edges[0][i].shortValue());
            int srcId = edges[1][i].intValue();
            EdgeType edgeType = new EdgeType(edges[2][i].shortValue());
            NodeType dtnType = new NodeType(edges[3][i].shortValue());
            int dtnId = edges[4][i].intValue();
            assertEdge(srcType, srcId, edgeType, dtnType, dtnId, cursor);
        }
    }





    private void assertSingularEdge(
            boolean inbound, EdgeType eType, NodeType dtnType, int dtnNodeId,
            Cursor cursor) {

        List<EdgeType> edgeTypes = cursor.getEdgeTypes(inbound);
        assertEquals(1, edgeTypes.size());
        assertEquals(eType, edgeTypes.get(0));

        List<NodeType> dtnTypes = cursor.getNodeTypes(inbound, eType);
        assertEquals(1, dtnTypes.size());

        assertEquals(dtnType, dtnTypes.get(0));

        List<Integer> ids = cursor.getNodeIds(inbound, eType, dtnType);
        assertEquals(1, ids.size());
        assertEquals(dtnNodeId, ids.get(0).intValue());

        assertEquals(1, cursor.getEdgeCount(inbound, eType, dtnType));
        assertEquals(1, cursor.getEdgeCount(inbound, eType));
        assertEquals(1, cursor.getEdgeCount(inbound));

        assertEmpty(cursor.getNodeTypes(inbound, new EdgeType(0)));
        assertEmpty(cursor.getNodeIds(inbound, new EdgeType(0), dtnType));
        assertEmpty(cursor.getNodeIds(inbound, eType, new NodeType(0)));

        assertEmpty(cursor.getEdgeTypes(!inbound));
        assertEmpty(cursor.getNodeTypes(!inbound, new EdgeType(0)));
        assertEmpty(cursor.getNodeIds(!inbound, new EdgeType(0), dtnType));
        assertEmpty(cursor.getNodeIds(!inbound, eType, new NodeType(0)));


        assertEquals(0, cursor.getEdgeCount(!inbound, eType, dtnType));
        assertEquals(0, cursor.getEdgeCount(!inbound, eType));
        assertEquals(0, cursor.getEdgeCount(!inbound));
    }


    private void assertEmpty(Collection<?> bag) {
        assertTrue(bag.isEmpty());
    }


    static void assertEdge(RandomGraphBuilder.EdgeDef edgeDef, Cursor cursor) {
        assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
    }

    static void assertEdge(ShortInt src, EdgeType edgeType, ShortInt dtn, Cursor cursor) {
        NodeType srcType = new NodeType(src.getType());
        NodeType dtnType = new NodeType(dtn.getType());
        assertEdge(srcType, src.getId(), edgeType, dtnType, dtn.getId(), cursor);
    }


    static void assertEdge(
            NodeType srcType, int srcId,
            EdgeType edgeType,
            NodeType dtnType, int dtnId,
            Cursor cursor) {

        assertEdgeImpl(srcType, srcId, false, edgeType, dtnType, dtnId, cursor);
        assertEdgeImpl(dtnType, dtnId, true, edgeType, srcType, srcId, cursor);
    }

    private static void assertEdgeImpl(
            NodeType srcType, int srcId,
            boolean inbound, EdgeType edgeType,
            NodeType dtnType, int dtnId,
            Cursor cursor) {

        assertTrue(cursor.moveTo(srcType, srcId));
        assertTrue(
            Collections.binarySearch(cursor.getEdgeTypes(inbound), edgeType) >= 0);
        assertTrue(
                Collections.binarySearch(cursor.getNodeTypes(inbound, edgeType), dtnType) >= 0);
        assertTrue(
                Collections.binarySearch(cursor.getNodeIds(inbound, edgeType, dtnType), dtnId) >= 0);
    }






    @Test
    public void testRandomGraph() {
        RandomGraphBuilder builder = new RandomGraphBuilder();
        Graph graph = builder.generateGraph(100);
        Cursor cursor = graph.newCursor();

        List<RandomGraphBuilder.EdgeDef> sampleEdges = builder.getExpectedDefs();
        int i = 0;
        for (RandomGraphBuilder.EdgeDef edgeDef : sampleEdges) {
//            System.out.println(++i + ". " + edgeDef);
            assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
        }
    }



    @Test
    public void testBigRandomGraph() {
        RandomGraphBuilder builder = new RandomGraphBuilder();
        builder.setSampleCount(2);
        builder.generate(100);
        builder.setDistroRefreshPeriod(100);
        builder.setSampleCount(100);
        builder.generate(10000);
        builder.setDistroRefreshPeriod(25000);
        builder.setSampleCount(1000);
        builder.generate(250000);
        Graph graph = builder.build();
        Cursor cursor = graph.newCursor();

        List<RandomGraphBuilder.EdgeDef> sampleEdges = builder.getExpectedDefs();
        int i = 0;
        for (RandomGraphBuilder.EdgeDef edgeDef : sampleEdges) {
//            System.out.println(++i + ". " + edgeDef);
            assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
        }
    }


    @Test
    public void testMinimalMerge() {
        ShortInt srcA = new ShortInt(3, 1);
        EdgeType eA = new EdgeType(19);
        ShortInt dtnA = new ShortInt(2, 4);
        ShortInt srcB = new ShortInt(3, 7);
        EdgeType eB = new EdgeType(20);
        ShortInt dtnB = dtnA;
        Graph a, b;
        {
            GraphBuilder builder = new GraphBuilder();
            builder.insertEdge(srcA, eA, dtnA);
            a = builder.build();

            builder = new GraphBuilder();
            builder.insertEdge(srcB, eB, dtnB);
            b = builder.build();
        }
        GraphMerger merger = new GraphMerger(a, b);
        Graph c = merger.merge();

        Cursor cursor = c.newCursor();
        assertEdge(srcA, eA, dtnA, cursor);
        assertEdge(srcB, eB, dtnB, cursor);
    }


    @Test
    public void testMinimalMerge2() {
        ShortInt srcA = new ShortInt(3, 1);
        EdgeType eA = new EdgeType(19);
        ShortInt dtnA = new ShortInt(2, 4);

        ShortInt srcB = new ShortInt(3, 7);
        EdgeType eB = new EdgeType(20);
        ShortInt dtnB = dtnA;

        Graph a, b;
        {
            GraphBuilder builder = new GraphBuilder();
            builder.insertEdge(srcA, eA, dtnA);
            a = builder.build();

            builder = new GraphBuilder();
            builder.insertEdge(srcB, eB, dtnB);
            b = builder.build();
        }
        GraphMerger merger = new GraphMerger(b, a);
        Graph c = merger.merge();

        Cursor cursor = c.newCursor();
        assertEdge(srcA, eA, dtnA, cursor);
        assertEdge(srcB, eB, dtnB, cursor);
    }



    @Test
    public void testMinimalMerge3() {
        ShortInt srcA = new ShortInt(3, 1);
        EdgeType eA = new EdgeType(19);
        ShortInt dtnA = new ShortInt(2, 4);
        ShortInt srcB = new ShortInt(3, 7);
        EdgeType eB = new EdgeType(20);
        ShortInt dtnB = new ShortInt(1, 5);
        Graph a, b;
        {
            GraphBuilder builder = new GraphBuilder();
            builder.insertEdge(srcA, eA, dtnA);
            a = builder.build();

            builder = new GraphBuilder();
            builder.insertEdge(srcB, eB, dtnB);
            b = builder.build();
        }
        GraphMerger merger = new GraphMerger(b, a);
        Graph c = merger.merge();

        Cursor cursor = c.newCursor();
        assertEdge(srcA, eA, dtnA, cursor);
        assertEdge(srcB, eB, dtnB, cursor);
    }



    @Test
    public void testMinimalMerge4() {
        ShortInt srcA = new ShortInt(3, 1);
        EdgeType eA = new EdgeType(19);
        ShortInt dtnA = new ShortInt(2, 4);

        ShortInt srcA2 = new ShortInt(3, 11);
        EdgeType eA2 = eA;
        ShortInt dtnA2 = new ShortInt(3, 1);

        ShortInt srcB = new ShortInt(3, 7);
        EdgeType eB = new EdgeType(19);
        ShortInt dtnB = dtnA;

        ShortInt srcB2 = srcA2;
        EdgeType eB2 = eA2;
        ShortInt dtnB2 = dtnA2;


        Graph a, b;
        {
            GraphBuilder builder = new GraphBuilder();
            builder.insertEdge(srcA, eA, dtnA);
            builder.insertEdge(srcA2, eA2, dtnA2);
            a = builder.build();

            builder = new GraphBuilder();
            builder.insertEdge(srcB, eB, dtnB);
            builder.insertEdge(srcB2, eB2, dtnB2);
            b = builder.build();
        }
        GraphMerger merger = new GraphMerger(b, a);
        Graph c = merger.merge();

        Cursor cursor = c.newCursor();
        assertEdge(srcA, eA, dtnA, cursor);
        assertEdge(srcB, eB, dtnB, cursor);
    }


    @Test
    public void testTinyMerge() {
        RandomGraphBuilder builder = new RandomGraphBuilder();
        builder.setSampleCount(10);
        Graph a = builder.generateGraph(1);
        Graph b = builder.clearBuilder().generateGraph(1);

        Graph c = new GraphMerger(a, b).merge();

        Cursor cursor = c.newCursor();
        int i = 0;
        for (RandomGraphBuilder.EdgeDef edgeDef : builder.getExpectedDefs()) {
            System.out.println(++i + ". " + edgeDef);
            assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
        }

    }




    @Test
    public void testSmallMerge() {
        RandomGraphBuilder builder = new RandomGraphBuilder();
        builder.setSampleCount(10);
        builder.generate(10);
        builder.setDistroRefreshPeriod(10);
        builder.setSampleCount(100);
        builder.generate(1000);
        Graph a = builder.build();
        builder.clearBuilder();
        builder.setDistroRefreshPeriod(50).generate(1000);
        Graph b = builder.build();
        Graph c = new GraphMerger(a, b).merge();

        Cursor cursor = c.newCursor();
        
        for (RandomGraphBuilder.EdgeDef edgeDef : builder.getExpectedDefs()) {
//            System.out.println(++i + ". " + edgeDef);
            assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
        }

    }

    @Test
    public void testModerateMerge() {

        RandomGraphBuilder builder = new RandomGraphBuilder();
        builder.setSampleCount(2);
        builder.generate(100);
        builder.setDistroRefreshPeriod(100);
        builder.setSampleCount(100);
        builder.generate(10000);

        Graph a = builder.setDistroRefreshPeriod(10000).generateGraph(90000);
        Graph b = builder.clearBuilder().setDistroRefreshPeriod(30000).generateGraph(100000);
        Graph c = new GraphMerger(a, b).merge();

        Cursor cursor = c.newCursor();
        
        for (RandomGraphBuilder.EdgeDef edgeDef : builder.getExpectedDefs()) {
//            System.out.println(++i + ". " + edgeDef);
            assertEdge(edgeDef.src, edgeDef.edgeType, edgeDef.dtn, cursor);
        }

//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException ix) {
//            fail();
//        }
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        System.out.println("testModerateMerge");
        System.out.println("=================");
        System.out.println("nodes: " + formatter.format(c.getNodeCount()));
        System.out.println("edges: " + formatter.format(c.getEdgeCount() / 2) + " (sans inbound)");
        System.out.println("<e:n>: " + formatter.format(c.getAvgEdgeTypeCountPerNode()));
        System.out.println("<t:e>: " + formatter.format(c.getAvgNodeTypeCountPerEdgeType()));
        System.out.println("<i:t>: " + formatter.format(c.getAvgNodeIdCountPerNodeType()));
        System.out.println("bytes: " + formatter.format(c.getMemSize()));
        
        System.out.println();
        System.out.println("Testing graph mem footprint trimming..");
        System.out.println("======================================");
        System.out.println("overhead bytes: " + formatter.format(c.unusedMem()));
        c.trimMemToSize();
        System.out.println("bytes (post trim): " + formatter.format(c.getMemSize()));
        System.out.println("overhead  (post trim): " + formatter.format(c.unusedMem()));

    }





}

