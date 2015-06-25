package test.papers.kruskal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import test.papers.kruskal.Kruskal.Edge;
import test.papers.kruskal.Kruskal.Node;
import de.wwu.muggl.symbolic.generating.Generator;

/**
 * 
 * 
 * Last modified: 2008-09-30
 * 
 * @author Tim Majchrzak
 * @version 1.0.0
 */
public class EdgesGenerator implements Generator {
	// Fields for the settings.
	private int		numberOfRuns;
	private int		numberOfRunsYet;
	private int		minimumNumberOfNodes;
	private int		maximumNumberOfNodes;
	private int		minimumEdgeLength;
	private int		maximumEdgeLength;
	private double	averageNumberOfEdges;

	// Fields for the edge generation yet performed.

	/**
	 * 
	 */
	public EdgesGenerator() {
		this(100, 5, 20, 1, 15, 1.5);
	}

	/**
	 * 
	 * @param numberOfRuns
	 * @param minimumNumberOfNodes
	 * @param maximumNumberOfNodes
	 * @param minimumEdgeLength
	 * @param maximumEdgeLength
	 * @param averageNumberOfEdges
	 */
	public EdgesGenerator(int numberOfRuns, int minimumNumberOfNodes, int maximumNumberOfNodes,
			int minimumEdgeLength, int maximumEdgeLength, double averageNumberOfEdges) {
		// TODO: check limitations. Wrong number will lead to wrong graphs or infinite loops.
		this.numberOfRuns = numberOfRuns;
		this.numberOfRunsYet = 0;
		this.minimumNumberOfNodes = minimumNumberOfNodes;
		this.maximumNumberOfNodes = maximumNumberOfNodes;
		this.minimumEdgeLength = minimumEdgeLength;
		this.maximumEdgeLength = maximumEdgeLength;
		this.averageNumberOfEdges = averageNumberOfEdges;
	}

	/**
	 * Returns true as the Generator can be used along with a ChoicePoint.
	 * 
	 * @return true
	 */
	public boolean allowsChoicePoint() {
		return true;
	}

	/**
	 * Check if this generator can provide another object. It does if counting up and the last
	 * returned integer was not the maximum specified.
	 * 
	 * @return true, if this generator can provide another object, false otherwise.
	 */
	public boolean hasAnotherObject() {
		if (this.numberOfRunsYet < this.numberOfRuns) return true;
		return false;
	}

	/**
	 * Provide the next array of edges.
	 * 
	 * @return The next array of edges.
	 */
	public Edge[] provideObject() {
		// Check if there are more edges available.
		if (!hasAnotherObject())
			throw new IllegalStateException("There are no more elements available.");
		
		// Count up.
		this.numberOfRunsYet++;

		// First step: Determine the actual numbers.
		Random random = new Random();
		int numberOfNodes = random.nextInt(this.maximumNumberOfNodes + 1 - this.minimumNumberOfNodes)
				+ this.minimumNumberOfNodes;
		int numberOfEdges = (int) Math.ceil(numberOfNodes * this.averageNumberOfEdges);
		// Make sure the number of edges is not too high.
		if (numberOfEdges > ((numberOfNodes - 1) * numberOfNodes) / 2)
			numberOfEdges = ((numberOfNodes - 1) * numberOfNodes) / 2;
		int edgesBuilt = 0;
		
		// Second step: Generate nodes.
		Node[] nodes = new Node[numberOfNodes];
		for (int a = 0; a < numberOfNodes; a++)
		{
			nodes[a] = new Node(a + 1);
		}
		
		// Third step: Generate edges.
		Edge[] edges = new Edge[numberOfEdges];
		
		// First phase: Each node gets one edge.
		for (; edgesBuilt < numberOfNodes; edgesBuilt++)
		{
			// Determine the node that the edge leads to.
			Node edgeTo = null;
			while (edgeTo == null || edgeTo.getNumber() == edgesBuilt + 1 || edgeTo.hasEdgeWith(nodes[edgesBuilt]))
			{
				edgeTo = nodes[random.nextInt(numberOfNodes)];
			}
			
			// Generate a weight.
			int weight = random.nextInt(this.maximumEdgeLength + 1 - this.minimumEdgeLength)
					+ this.minimumEdgeLength;
			
			// Build the edge.
			edges[edgesBuilt] = new Edge(nodes[edgesBuilt], edgeTo, weight);
		}
		
		// Second phase: Distribute edges between unconnected sub-trees as long as there is only one tree.
		List<Integer> processingOrder = new ArrayList<Integer>();
		List<Integer> targetOrder = new ArrayList<Integer>();
		while (processingOrder.size() < numberOfNodes)
		{
			Integer integer = random.nextInt(numberOfNodes);
			if (!processingOrder.contains(integer))
				processingOrder.add(integer);
		}
		while (targetOrder.size() < numberOfNodes)
		{
			Integer integer = random.nextInt(numberOfNodes);
			if (!targetOrder.contains(integer))
				targetOrder.add(integer);
		}
		
		for (Integer integer1 : processingOrder)
		{
			for (Integer integer2 : targetOrder)
			{
				if (integer1 != integer2) {
					// Is there a connection?
					if (!leadsTo(nodes[integer1], nodes[integer2])) {
						// Generate one!
						int weight = random.nextInt(this.maximumEdgeLength + 1 - this.minimumEdgeLength)
							+ this.minimumEdgeLength;
						edges[edgesBuilt] = new Edge(nodes[integer1], nodes[integer2], weight);
						edgesBuilt++;
					}
				}
			}
		}
		
		// Third phase: Randomly generate more edges.
		for (; edgesBuilt < numberOfEdges; edgesBuilt++)
		{
			Node edgeFrom = nodes[random.nextInt(numberOfNodes)];
			Node edgeTo = null;
			while (edgeTo == null || edgeTo.getNumber() == edgesBuilt + 1 || edgeTo.hasEdgeWith(edgeFrom))
			{
				edgeFrom = nodes[random.nextInt(numberOfNodes)];
				edgeTo = nodes[random.nextInt(numberOfNodes)];
			}
			int weight = random.nextInt(this.maximumEdgeLength + 1 - this.minimumEdgeLength)
				+ this.minimumEdgeLength;
			edges[edgesBuilt] = new Edge(edgeFrom, edgeTo, weight);
		}
		
		// Finished. Return the edges.
		return edges;
	}
	
	/**
	 * Check if this generator supplies java objects that are no wrappers for primitive types. It
	 * supplies arrays of objects which needs to be converted to array references containing object
	 * references.
	 * 
	 * @return true.
	 */
	public boolean objectNeedsConversion() {
		return true;
	}
	
	private boolean leadsTo(Node from, Node to) {
		return leadsTo(from, to, new ArrayList<Node>());
	}
	
	/**
	 * 
	 *
	 * @param from
	 * @param to
	 * @param origin
	 * @return
	 */
	private boolean leadsTo(Node from, Node to, ArrayList<Node> hasSeen) {
		if (from == to) return true;
		if (hasSeen.contains(from)) return false;
		Iterator<Edge> edgesIterator = from.getEdges().iterator();
		while (edgesIterator.hasNext()) {
			Edge edge = edgesIterator.next();
			hasSeen.add(from);
			if (edge.getNode1() == from) {
				if (leadsTo(edge.getNode2(), to, hasSeen)) return true;
			} else {
				if (leadsTo(edge.getNode1(), to, hasSeen)) return true;
			}
			hasSeen.remove(from);
		}
		return false;
	}

	/**
	 * Reset this generator to the starting value.
	 */
	public void reset() {
		this.numberOfRunsYet = 0;
	}

	/**
	 * Get the name of the Generator.
	 * 
	 * @return "Integer increment generator".
	 */
	public String getName() {
		return "Kuskal edges generator";
	}

	/**
	 * Get a description of what the generator does and how it works.
	 * 
	 * @return A description of the generator.
	 */
	public String getDescription() {
		return ""; // TODO
	}

}
