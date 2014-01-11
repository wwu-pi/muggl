package test.papers.binaryTree;

import de.wwu.muggl.symbolic.generating.Generator;

/**
 *
 */
public class BinaryTreeGenerator implements Generator {
	private int sizeStrategy;
	private long maximumRuns;
	private int treesPerSize;
	private long currentRuns;
	
	/**
	 * 
	 * @param sizeStrategy 0 - linear, 1 - n power of 2, 2 - exponential, 3 - 10 ^ n
	 * @param maximumRuns
	 * @param treesPerSize
	 */
	public BinaryTreeGenerator(int sizeStrategy, long maximumRuns, int treesPerSize) {
		this.sizeStrategy = sizeStrategy;
		this.maximumRuns = maximumRuns;
		this.treesPerSize = treesPerSize;
		this.currentRuns = 0L;
	}
	
	/**
	 * @see de.wwu.muggl.symbolic.generating.Generator#allowsChoicePoint()
	 */
	public boolean allowsChoicePoint() {
		return true;
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.Generator#hasAnotherObject()
	 */
	public boolean hasAnotherObject() {
		if (this.currentRuns < this.maximumRuns)
			return true;
		return false;
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.Generator#objectNeedsConversion()
	 */
	public boolean objectNeedsConversion() {
		return true;
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.Generator#provideObject()
	 */
	public Object provideObject() throws IllegalStateException {
		if (!hasAnotherObject())
			throw new IllegalStateException("No more objects available.");
		
		// On the first run, return the empty tree.
		if (this.currentRuns == 0l) {
			this.currentRuns++;
			return new BinaryTree();
		}
		
		// New generated tree.
		BinaryTree tree;
		
		// Determine the number of elements.
		long numberOfElements = ((this.currentRuns - 1) / this.treesPerSize) + 1;
		switch (this.sizeStrategy) {
			case 1:
				numberOfElements = (long) Math.pow(numberOfElements, 2);
				break;
			case 2:
				numberOfElements = (long) Math.pow(2, numberOfElements);
				break;
			case 3:
				numberOfElements = (long)Math.pow(10, numberOfElements);
				break;
			default:
				break;
		}
		
		// Generate a random tree.
		tree = generateTree(numberOfElements);
		
		// Return the tree.
		this.currentRuns++;
		return tree;
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.Generator#reset()
	 */
	public void reset() {
		this.currentRuns = 0;
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.GenericGenerator#getDescription()
	 */
	public String getDescription() {
		return "Generator that generates binary trees.";
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.GenericGenerator#getName()
	 */
	public String getName() {
		return "Binary Tree Generator";
	}
	
	/**
	 * 
	 *
	 * @param numberOfElements
	 * @return
	 */
	private BinaryTree generateTree(long numberOfElements) {
		return new GeneratedBinaryTree(generateNode(numberOfElements, 0L));
	}
	
	/**
	 * 
	 *
	 * @param numberOfElements
	 * @param startAt
	 * @return
	 */
	private Node generateNode(long numberOfElements, long startAt) {
		// Number for the new node.
		long nodeNumber = startAt;
		
		// Generate further nodes?
		numberOfElements--;
		Node leftChild = null;
		Node rightChild = null;
		if (numberOfElements > 0) {
			// Determine the number of nodes for each side.
			double random = Math.random();
			long numberOfElementsLeft = Math.round(random * numberOfElements);
			long numberOfElementsRight = Math.round(((1.0 - random) * numberOfElements));
			// Have all elements been distributed?
			if (numberOfElementsLeft + numberOfElementsRight != numberOfElements) {
				// Determine which side to decrease or increase.
				boolean changeSecond = false;
				if (Math.random() > 0.5) changeSecond = true;
				if (numberOfElementsLeft + numberOfElementsRight > numberOfElements) {
					if ((changeSecond && numberOfElementsRight > 0) || numberOfElementsLeft == 0) {
						numberOfElementsRight--;
					} else {
						numberOfElementsLeft--;
					}
				} else {
					if (changeSecond) {
						numberOfElementsRight++;
					} else {
						numberOfElementsLeft++;
					}
				}
			}
			// Generate the left and the right children.
			if (numberOfElementsLeft > 0) {
				leftChild = generateNode(numberOfElementsLeft, startAt);
				nodeNumber += numberOfElementsLeft; 
			}
			if (numberOfElementsRight > 0) {
				rightChild = generateNode(numberOfElementsRight, startAt + 1 + numberOfElementsLeft);
			}
		}
		
		// Generate the new node.
		GeneratedNode node = new GeneratedNode(nodeNumber);
		node.setLeftChild(leftChild);
		node.setRighChild(rightChild);
		
		// Return the node.
		return node;
	}
	
	/**
	 * 
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		BinaryTreeGenerator generator = new BinaryTreeGenerator(0, 20, 5);
		for (int a = 0; a < 20; a++)
		{
			BinaryTree tree = (BinaryTree) generator.provideObject();
			System.out.println(tree.toString() + "\n");
		}
	}

}
