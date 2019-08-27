# graphiti

Java library for compact, in-memory, representation of a large directed graph. This was developed originally for an Android app: some choices in hard limits stem from that application. 

# Data Model

* Nodes (vertices): Each node is identified by an integral 2-tuple (node-type, node-id)
* Edges: Nodes are connected by user-defined integral edge types. Edges are directed: that is, an edge type has a notion of source and destination. An edge then is of the form ((src-node-type, src-node-id), edge-type, (dtn-node-type, dtn-node-id))
* Navigation: Starting from a given node (node-type, node-id) it is possible to retrieve both edges in which this node is the source (outbound) or in which this node is the destination (inbound). For nodes with many edges, the retrieved edges may be qualified by edge-type, and then by node-type of the endpoint.

# Implementation

This uses fixed width binary tables. The primary objective was efficient read-only access. There's some support for writing (merging graphs), but nothing for delete.

Objects and values are loaded lazily. So while the library returns random access collection views of its contents (e.g. java.util.List), there's little memory overhead in obtaining a reference to such a view. And because these lists are sorted (and can be binary searched), there's often little reason to traverse them an element at a time.

# Limits

The current implementation is bound by the following limits

* Node-type: unsigned 2-byte value
* Node-id: unsigned 3-byte value (0 to 16,777,215)
* Edge-type: unsigned 2-byte value
* Max number of edges per node per edge-type: 64K
  (That is, [unrealistically] accounting for a multitude of edge types, each node can have a maximum of 64K x 64k = 4B edges.)

# Development Options

* Make hard limits configurable
* Add delete capability

