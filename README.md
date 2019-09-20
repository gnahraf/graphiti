# graphiti

Java library for compact, in-memory representation of a large directed graph. This was developed originally for an Android app: some choices in hard limits stem from that application. In that application the graph data was persisted in (i.e. loaded from) Android's standard SQLite DB. The objective here is fast, read-only, indexed access.

# Data Model

* Nodes (vertices): Each node is identified by an integral 2-tuple (node-type, node-id)
* Edges: Nodes are connected by user-defined integral edge types. Edges are directed: that is, an edge type has a notion of source and destination. An edge then is of the form ((src-node-type, src-node-id), edge-type, (dtn-node-type, dtn-node-id))
* Navigation: Starting from a given node (node-type, node-id) it is possible to retrieve both edges in which this node is the source (outbound) or in which this node is the destination (inbound). For nodes with many edges, the retrieved edges may be qualified by edge-type, and then by node-type of the endpoint.

Node types are designed to convey a category of node: e.g. person, place, picture, email, etc.

The model was not designed to encode detailed information in actual edges; rather an edge type is designed to encode a _type of relationship_. So for example, although you could coerce encoding it there, this is not designed to encode the date the edge got created into the edge itself. (The way *this* author models dates is by encoding them as standalone date nodes connected by *verb* type edges such as *date-created*, or *date-modified*, etc. This strategy of annotating the graph with such meta nodes for worked well for me: your mileage may vary.)

The interface for walking the graph is https://github.com/gnahraf/graphiti/blob/master/src/main/java/com/gnahraf/graphiti/model/Cursor.java

To quickly jump to its use, see the GraphsTest unit test https://github.com/gnahraf/graphiti/blob/master/src/test/java/com/gnahraf/graphiti/db/GraphsTest.java 

# Implementation

This uses 4 fixed width binary tables. An address table links nodes (node-type, node-id) to 2 row ranges in the edge [type] table, one range for outbound, another for inbound. Each row in the edge [type] table in turn has an entry (row number + count) specifying a range of rows in the node type table; each row in the node type table specifies a range of entries in the node id table, and node id table is just a list of 3 byte (unsigned) integral values. (Why 3 bytes? Because without adding some indirection magic, we're still to limited 2^31 (max byte array length) / 4 (sizeof int) = 2^29; that is, a 4th byte only expands the *usable* address space by a factor of 32.) Rows in the address table are sorted by (node-type, node-id); rows in the other tables are sorted within the referenced ranges.

Objects and values are loaded lazily. So while the library returns random access collection views of its contents (e.g. java.util.List), there's little memory overhead in obtaining a reference to such a view. And because these lists are sorted (and can be binary searched), there's often little reason to traverse them an element at a time.

The primary objective was efficient read-only access. There's support for writing (merging graphs), but nothing for delete. The reason why there's merge-support is that the procedure for *building* graphs (cf https://github.com/gnahraf/graphiti/blob/master/src/main/java/com/gnahraf/graphiti/db/GraphBuilder.java ) itself is not memory effecient; the output graph *is*. So in order to construct a big graph, you construct smaller ones and then merge them.

### Example memory footprint

From unit test referenced above

> nodes: 199,796

> edges: 2,402,850 (*not* double counting inbound)

> avg edge-type count per node: 2.45

> avg node-type count per edge: 1.85

> avg node-id count per node-type per edge-type: 2.65

> size: 40MB

> avg bytes per edge: < 17





# Limits

The current implementation is bound by the following limits

* Node-type: unsigned 2-byte value
* Node-id: unsigned 3-byte value (0 to 16,777,215)
* Edge-type: unsigned 2-byte value
* Max number of edges: 357,913,941

That maximum number of edges limit is an artifact of the max length of byte arrays in Java
and integer based indexing. Scaling it a few fold more seems straightforward, if needed.

# Development Options

* Expand limits (or make tradeoffs configurable)
* Add delete capability

