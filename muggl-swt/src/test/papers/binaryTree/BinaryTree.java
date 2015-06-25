package test.papers.binaryTree;

/**
 * 
 */
public class BinaryTree {
	protected Node root;
	
	/**
	 * 
	 *
	 * @param value
	 * @return TODO
	 */
	public boolean find(long value) {
		if (this.root == null) {
			return false;
		}
		
		return null != this.root.find(value);
	}
	
	/**
	 * 
	 *
	 * @param value
	 */
	public void insert(long value) {
		if (this.root == null) {
			this.root = new Node(value);
		} else {
			this.root.insert(value);
		}
	}
	
	/**
	 * 
	 *
	 * @param value
	 */
	public void delete(long value) {
		if (this.root == null) {
			/*
			 * Convention: No exception will be thrown if a value is not in the tree.
			 */
			return;
		}
		
		if (this.root.value == value) {
			if (this.root.leftChild == null && this.root.rightChild == null) {
				this.root = null;
			} else if (this.root.leftChild == null || this.root.rightChild == null) {
				 if (this.root.leftChild == null) {
					 this.root = this.root.rightChild;
				 } else {
					 this.root = this.root.leftChild;
				 }
			} else {
				Node symmetricSuccessor = this.root.getSymmetricSuccessor(this.root);
				
				symmetricSuccessor.leftChild = this.root.leftChild;
				if (this.root.rightChild == symmetricSuccessor) {
					// Avoid a cycle.
					symmetricSuccessor.rightChild = null;
				} else {
					symmetricSuccessor.rightChild = this.root.rightChild;
				}
				this.root = symmetricSuccessor;
			}
		} else if (value < this.root.value) {
			// Convention: No exception will be thrown if a value is not in the tree.
			if (this.root.getLeftChild() != null)
				this.root.leftChild.delete(value, this.root, true);
		} else {
			// Convention: No exception will be thrown if a value is not in the tree.
			if (this.root.rightChild != null)
				this.root.rightChild.delete(value, this.root, false);
		}
	}
	
	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (this.root == null) {
			return "";
		}
		return this.root.toString(String.valueOf(this.root.getHighestValue()).length() + 2);
	}
	
	/**
	 * 
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		BinaryTree tree = new BinaryTree();
		tree.insert(4L);
		tree.insert(2L);
		tree.insert(6L);
		tree.insert(1L);
		tree.insert(1L);
		tree.insert(7L);
		tree.insert(5L);
		tree.toString();
		tree.insert(3L);
		
		BinaryTree tree2 = new BinaryTree();
		tree2.insert(400000L);
		tree2.insert(200000L);
		tree2.insert(600000L);
		tree2.insert(100000L);
		tree2.insert(100000L);
		tree2.insert(700000L);
		tree2.insert(500000L);
		tree2.toString();
		tree2.insert(300000L);
		
		
		tree = new BinaryTree();
		tree.insert(1L);
		tree.insert(4L);
		tree.toString();
		tree.insert(6L);
		tree.insert(2L);
		tree.insert(8L);
		tree.insert(3L);
		tree.insert(4L);
		tree.insert(5L);
		tree.insert(9L);
		tree.insert(7L);
		tree.insert(10L);
		tree.toString();
		boolean found = tree.find(4L);
		tree.delete(4L);
		found = tree.find(4L);
		tree.delete(1L);
		tree.delete(9L);
		tree.delete(8L);
		tree.insert(1L);
		tree.delete(3L);
		tree.insert(4L);
		if (found) System.exit(0);
	}
	
}
