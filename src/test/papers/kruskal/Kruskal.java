package test.papers.kruskal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

@SuppressWarnings("all")
public class Kruskal {

	public static class Node {
		private int				number;
		private ArrayList<Edge>	edges;

		public Node(int number) {
			this.number = number;
			this.edges = new ArrayList<Edge>();
		}

		public int getNumber() {
			return this.number;
		}

		public ArrayList<Edge> getEdges() {
			return this.edges;
		}

		public void addEdge(Edge edge) {
			this.edges.add(edge);
		}
		
		public boolean hasEdgeWith(Node node) {
			for (Edge edge : this.edges)
			{
				if (edge.getNode1() == node || edge.getNode2() == node)
					return true;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Node " + this.number; 
		}

	}

	public static class Edge implements Comparable {
		private Node	node1;
		private Node	node2;
		private int		weight;
		private boolean	inMinimalSpanningTree;

		public Edge(Node node1, Node node2, int weight) {
			this.node1 = node1;
			this.node1.addEdge(this);
			this.node2 = node2;
			this.node2.addEdge(this);
			this.weight = weight;
			this.inMinimalSpanningTree = false;
		}

		public Node getNode1() {
			return this.node1;
		}

		public Node getNode2() {
			return this.node2;
		}

		public int getWeight() {
			return this.weight;
		}

		public boolean isInMinimalSpanningTree() {
			return this.inMinimalSpanningTree;
		}

		public void setInMinimalSpanningTree(boolean value) {
			this.inMinimalSpanningTree = value;
		}

		/**
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(Object o) {
			if (o instanceof Edge) return compareTo((Edge) o);
			return 0;
		}

		public int compareTo(Edge e) {
			if (this.weight < e.weight) return -1;
			if (this.weight > e.weight) return 1;
			return 0;
		}
		
		@Override
		public String toString() {
			String info = "";
			if (this.inMinimalSpanningTree) info = " (in minimal spanning tree)";
			return "Edge between " + this.node1.getNumber() + " and " + this.node2.getNumber() + " with weight " + this.weight + info; 
		}

	}

	public static Edge[] minimalSpanningTree(Edge[] edges) {
		// Put all edges into a PriorityQueue.
		PriorityQueue<Edge> queue = new PriorityQueue<Edge>();
		for (Edge edge : edges) {
			queue.add(edge);
		}
		int edgesInSpanningTree = 0;
		
		// Starting edge.
		Node startingNode = null;

		// Find the edges...
		while (queue.size() > 0) {
			// Get the next edge...
			Edge edge = queue.poll();
			// Add it to the graph.
			edge.setInMinimalSpanningTree(true);
			if (startingNode == null) {
				// It is the first edge at all.
				startingNode = edge.getNode1();
				edgesInSpanningTree++;				
			} else {
				// Check for circles.
				if (hasCircle(startingNode)) {
					// Adding the edge has lead to a circle. Remove it.
					edge.setInMinimalSpanningTree(false);
				} else {
					edgesInSpanningTree++;
				}
			}
		}

		// Initialize the array for the spanning edges.
		Edge[] spanningEdges = new Edge[edgesInSpanningTree];
		int b = 0;
		for (int a = 0; a < edges.length; a++)
		{
			if (edges[a].isInMinimalSpanningTree()) {
				spanningEdges[b] = edges[a];
				b++;
			}
		}

		// Finished.
		return spanningEdges;
	}

	private static boolean hasCircle(Node startingNode) {
		Iterator<Edge> edgesIterator = startingNode.getEdges().iterator();
		while (edgesIterator.hasNext()) {
			Edge edge = edgesIterator.next();
			if (edge.isInMinimalSpanningTree()) {
				if (edge.getNode1() == startingNode) {
					if (leadsTo(edge.getNode2(), startingNode, startingNode)) return true;
				} else {
					if (leadsTo(edge.getNode1(), startingNode, startingNode)) return true;
				}
			}		
		}
		return false;
	}
	
	private static boolean leadsTo(Node from, Node to, Node origin) {
		if (from == to) return true;
		Iterator<Edge> edgesIterator = from.getEdges().iterator();
		while (edgesIterator.hasNext()) {
			Edge edge = edgesIterator.next();
			if (edge.isInMinimalSpanningTree()) {
				if (edge.getNode1() == from) {
					if (edge.getNode2() != origin && leadsTo(edge.getNode2(), to, from)) return true;
				} else {
					if (edge.getNode1() != origin && leadsTo(edge.getNode1(), to, from)) return true;
				}
			}	
		}
		return false;
	}

	public static void main(String... args) {
		Node node1 = new Kruskal.Node(1);
		Node node2 = new Kruskal.Node(2);
		Node node3 = new Kruskal.Node(3);
		Node node4 = new Kruskal.Node(4);
		Node node5 = new Kruskal.Node(5);
		Node node6 = new Kruskal.Node(6);
		Node node7 = new Kruskal.Node(7);
		Edge edge1 = new Kruskal.Edge(node1, node2, 7);
		Edge edge2 = new Kruskal.Edge(node1, node4, 5);
		Edge edge3 = new Kruskal.Edge(node2, node3, 8);
		Edge edge4 = new Kruskal.Edge(node2, node4, 9);
		Edge edge5 = new Kruskal.Edge(node2, node5, 7);
		Edge edge6 = new Kruskal.Edge(node3, node5, 5);
		Edge edge7 = new Kruskal.Edge(node4, node5, 15);
		Edge edge8 = new Kruskal.Edge(node4, node6, 6);
		Edge edge9 = new Kruskal.Edge(node5, node6, 8);
		Edge edge10 = new Kruskal.Edge(node5, node7, 9);
		Edge edge11 = new Kruskal.Edge(node6, node7, 11);
		Edge[] edges = {edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11};
		Edge[] finalEdges = minimalSpanningTree(edges);
		for (int a = 0; a < finalEdges.length; a++)
		{
			System.out.println(finalEdges[a].toString());
		}
	}

}
