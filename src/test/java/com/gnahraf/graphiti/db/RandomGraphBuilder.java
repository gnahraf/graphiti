/*
 * Copyright (c) 2015 Babak Farhang
 */

package com.gnahraf.graphiti.db;

import com.gnahraf.graphiti.model.EdgeType;
import com.gnahraf.graphiti.model.NodeType;
import com.gnahraf.util.datatypes.Primitives;
import com.gnahraf.util.Sampler;
import com.gnahraf.util.datatypes.Base;
import com.gnahraf.util.datatypes.ShortInt;
import com.gnahraf.util.datatypes.WeightedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by babak on 6/27/15.
 */
public class RandomGraphBuilder {


    static class EdgeDef extends Base {

        public final ShortInt src;
        public final EdgeType edgeType;
        public final ShortInt dtn;

        EdgeDef(NodeType srcType, int srcId, EdgeType edgeType, NodeType dtnType, int dtnId) {
            this(
                new ShortInt(srcType.getId(), srcId),
                edgeType,
                new ShortInt(dtnType.getId(), dtnId));
        }

        EdgeDef(ShortInt src, EdgeType edgeType, ShortInt dtn) {
            this.src = src;
            this.edgeType = edgeType;
            this.dtn = dtn;
        }


        NodeType srcType() {
            return new NodeType(src.getType());
        }

        NodeType dtnType() {
            return new NodeType(dtn.getType());
        }

        @Override
        protected boolean equalsImpl(Object o) {
            EdgeDef other = (EdgeDef) o;
            return
                src.equals(other.src) && edgeType.equals(other.edgeType) && dtn.equals(other.dtn);
        }
        @Override
        public int hashCode() {
            return src.hashCode() ^ edgeType.hashCode() ^ dtn.hashCode();
        }
        @Override
        public String toString() {
            return src + "=" + edgeType + "=>" + dtn;
        }
    }

    protected final Sampler<EdgeType> edgeTypeSampler = new Sampler<>();
    protected final Sampler<NodeType> nodeTypeSampler = new Sampler<>();

    protected final Sampler<Integer> edgeCountDistrib = new Sampler<>();

    protected final Sampler<ShortInt> existingNodes = new Sampler<>();

    protected final Random random = new Random(41);

    protected GraphBuilder builder = new GraphBuilder();

    private int distroRefreshPeriod = 5;
    private int sampleCount = 1000;

    private List<EdgeDef> expectedDefs = new ArrayList<>();







    public RandomGraphBuilder() {
        init();
    }

    protected void init() {
        edgeTypeSampler.setWeight(new EdgeType(1), 10);
        edgeTypeSampler.setWeight(new EdgeType(2), 3);
        edgeTypeSampler.setWeight(new EdgeType(3), 2);
        edgeTypeSampler.setWeight(new EdgeType(4), 1);
        edgeTypeSampler.setWeight(new EdgeType(5), 1);
        edgeTypeSampler.setWeight(new EdgeType(6), 1);
        edgeTypeSampler.setWeight(new EdgeType(7), 20);
        edgeTypeSampler.prepare(0);

        nodeTypeSampler.setWeight(new NodeType(1), 5);
        nodeTypeSampler.setWeight(new NodeType(2), 3);
        nodeTypeSampler.setWeight(new NodeType(3), 4);
        nodeTypeSampler.setWeight(new NodeType(4), 1);
        nodeTypeSampler.setWeight(new NodeType(5), 1);
        nodeTypeSampler.prepare(0);

        existingNodes.setWeight(new ShortInt(1, 23), 1);
        existingNodes.setWeight(new ShortInt(1, 25), 1);
        existingNodes.setWeight(new ShortInt(1, 27), 1);
        existingNodes.setWeight(new ShortInt(3, 2), 1);
        existingNodes.prepare(0);


        edgeCountDistrib.setWeight(1, 1);
        edgeCountDistrib.setWeight(3, 1);
        edgeCountDistrib.setWeight(3, 2);
        edgeCountDistrib.setWeight(4, 2);
        edgeCountDistrib.setWeight(5, 3);
        edgeCountDistrib.setWeight(6, 6);
        edgeCountDistrib.setWeight(7, 13);
        edgeCountDistrib.setWeight(8, 11);
        edgeCountDistrib.setWeight(9, 19);
        edgeCountDistrib.setWeight(10, 50);
        edgeCountDistrib.setWeight(11, 100);
        edgeCountDistrib.setWeight(12, 120);
        edgeCountDistrib.setWeight(13, 130);
        edgeCountDistrib.setWeight(14, 120);
        edgeCountDistrib.setWeight(15, 100);
        edgeCountDistrib.setWeight(16, 70);
        edgeCountDistrib.setWeight(17, 40);
        edgeCountDistrib.setWeight(18, 20);
        edgeCountDistrib.setWeight(19, 10);
        edgeCountDistrib.setWeight(20, 5);
        edgeCountDistrib.setWeight(21, 3);
        edgeCountDistrib.setWeight(22, 1);
        edgeCountDistrib.setWeight(23, 1);
        edgeCountDistrib.setWeight(24, 1);
        edgeCountDistrib.setWeight(25, 1);
        edgeCountDistrib.setWeight(26, 1);
        edgeCountDistrib.setWeight(27, 1);
        edgeCountDistrib.setWeight(28, 1);
        edgeCountDistrib.setWeight(29, 1);
        edgeCountDistrib.setWeight(30, 1);
        edgeCountDistrib.setWeight(31, 1);
        edgeCountDistrib.setWeight(32, 1);
        edgeCountDistrib.setWeight(33, 1);
        edgeCountDistrib.setWeight(34, 1);
        edgeCountDistrib.setWeight(35, 1);
        edgeCountDistrib.prepare(0);

    }


    public Graph generateGraph(int rounds) {
        generate(rounds);
        return build();
    }



    public RandomGraphBuilder generate(int nodes) {
        if (nodes < 1)
            throw new IllegalArgumentException("rounds: " + nodes);
        int edgeSamplingPeriod;
        {
            int totalWeightedCount = 0;
            for (WeightedItem<Integer> wc : edgeCountDistrib.weightedItems()) {
                totalWeightedCount += wc.getItem() * wc.getWeight();
            }
            int avgEdgeCount = totalWeightedCount / edgeCountDistrib.totalWeight();
            edgeSamplingPeriod = Math.max(1, nodes * avgEdgeCount / sampleCount);
        }
        for (int count = 0; count < nodes; ++count) {
            if (count % distroRefreshPeriod == 0)
                refreshDistribution();
            newNodeAndEdges(edgeSamplingPeriod);
        }
        return this;
    }


    public RandomGraphBuilder clearBuilder() {
        builder = new GraphBuilder();
        return this;
    }


    public Graph build() {
        return builder.build();
    }


    protected void newNodeAndEdges(int samplePeriod) {
        NodeType nodeType = nodeTypeSampler.next();
        nodeTypeSampler.incrWeight(nodeType);

        int nodeId = nextNodeId();
        ShortInt src = new ShortInt(nodeType.getId(), nodeId);

        for (int count = nextEdgeCount(); count-- > 0; ) {

            EdgeType edgeType = edgeTypeSampler.next();
            edgeTypeSampler.incrWeight(edgeType);

            ShortInt dtn = existingNodes.next();
            existingNodes.incrWeight(dtn);

            builder.insertEdge(src, edgeType, dtn);
            ++edgeCount;
            if (edgeCount % samplePeriod == 0)
                expectedDefs.add(new EdgeDef(src, edgeType, dtn));

        }
        existingNodes.incrWeight(src);
    }

    private int edgeCount;




    private void refreshDistribution() {
        nodeTypeSampler.prepareDistribution();
        edgeTypeSampler.prepareDistribution();
        existingNodes.prepareDistribution();
    }


    protected int nextEdgeCount() {
        int count = edgeCountDistrib.next();
        if (count >= existingNodes.getDistributionSize())
            count = existingNodes.getDistributionSize() - 1;
        return count;
    }



    protected int nextNodeId() {
        return random.nextInt(Primitives.MAX_TRYTE) + 1;
    }





    public int getDistroRefreshPeriod() {
        return distroRefreshPeriod;
    }

    public RandomGraphBuilder setDistroRefreshPeriod(int distroRefreshPeriod) {
        if (distroRefreshPeriod < 1)
            throw new IllegalArgumentException("distroRefreshPeriod: " + distroRefreshPeriod);
        this.distroRefreshPeriod = distroRefreshPeriod;
        return this;
    }

    public int getEdgeCount() {
        return edgeCount;
    }


    public int getSampleCount() {
        return sampleCount;
    }

    public RandomGraphBuilder setSampleCount(int sampleCount) {
        if (sampleCount < 1)
            throw new IllegalArgumentException("sampleCount: " + sampleCount);
        this.sampleCount = sampleCount;
        return this;
    }

    public List<EdgeDef> getExpectedDefs() {
        return expectedDefs;
    }
}
