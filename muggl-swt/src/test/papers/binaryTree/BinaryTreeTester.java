package test.papers.binaryTree;

/**
 *
 */
public class BinaryTreeTester {

	/**
	 * 
	 *
	 * @param tree
	 * @param find
	 * @param insert
	 * @param delete
	 */
	public static void testAll(BinaryTree tree, long find, long insert, long delete) {
		testFind(tree, find);
		testInsert(tree, insert);
		testDelete(tree, delete);
	}
	
	/**
	 * 
	 *
	 * @param tree
	 * @param find
	 */
	public static void testFind(BinaryTree tree, long find) {
		tree.find(find);
	}
	
	/**
	 * 
	 *
	 * @param tree
	 * @param insert
	 */
	public static void testInsert(BinaryTree tree, long insert) {
		tree.insert(insert);
	}
	
	/**
	 * 
	 *
	 * @param tree
	 * @param delete
	 */
	public static void testDelete(BinaryTree tree, long delete) {
		tree.delete(delete);
	}
	
}
