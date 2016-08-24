This is a program which implements Djikstra's Algorithm. It calculates the minimum travel cost between two cities with non-negative edge weights.

The inputs are:
Number of nodes
Number of edges
Motel prices at each city (node weight)
Gas prices for traveling between each city (edge weight)

Input Structure:
numNodes
numEdges
nodeNum price
nodeNum1 nodeNum2 price

The first node is always the start node, and the second node is always the end node.

Given this information, the program constructs an adjacency matrix, runs Djikstra's algorithm, then returns the minimum travel cost.
