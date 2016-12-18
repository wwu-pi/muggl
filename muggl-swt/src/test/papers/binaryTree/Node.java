package test.papers.binaryTree;

/**
 * 
 */
class Node {
	protected Node leftChild;
	protected Node rightChild;
	long value;
	
	/**
	 * 
	 * @param value
	 */
	public Node(long value) {
		this.value = value;
		this.leftChild = null;
		this.rightChild = null;
	}
	
	/**
	 * 
	 *
	 * @return TODO
	 */
	public Node getLeftChild() {
		return this.leftChild;
	}
	
	/**
	 * 
	 *
	 * @return TODO
	 */
	public Node getRightChild() {
		return this.rightChild;
	}
	
	/**
	 * 
	 *
	 * @param value
	 * @return TODO
	 */
	public Node find(long value) {
		if (this.value == value) {
			return this;
		}
		
		if (this.value < value) {
			if (this.rightChild == null) {
				return null;
			}
			return this.rightChild.find(value);
		}
		
		if (this.leftChild == null) {
			return null;
		}
		return this.leftChild.find(value);
	}
	
	/**
	 * 
	 *
	 * @param value
	 */
	public void insert(long value) {
		if (this.value == value) {
			/*
			 * Convention: It is impossible to insert a value twice. But no exception will be
			 * thrown.
			 */
			return;
		}
		
		if (this.value < value) {
			if (this.rightChild == null) {
				this.rightChild = new Node(value);
			} else {
				this.rightChild.insert(value);
			}
		} else {
			if (this.leftChild == null) {
				this.leftChild = new Node(value);
			} else {
				this.leftChild.insert(value);
			}
		}
	}
	
	/**
	 * 
	 *
	 * @param value
	 * @param parent
	 * @param isLeft
	 */
	public void delete(long value, Node parent, boolean isLeft) {
		if (this.value == value) {
			// Distinguish between three possible cases.
			if (this.leftChild == null && this.rightChild == null) {
				if (isLeft) {
					parent.leftChild = null;
				} else {
					parent.rightChild = null;
				}
			} else if (this.leftChild == null || this.rightChild == null) {
				Node child;
				if (this.leftChild == null) {
					child = this.rightChild;
				} else {
					child = this.leftChild;
				}
				if (isLeft) {
					parent.leftChild = child;
				} else {
					parent.rightChild = child;
				}
			} else {
				Node symmetricSuccessor = getSymmetricSuccessor(this);
				if (isLeft) {
					parent.leftChild = symmetricSuccessor;
				} else {
					parent.rightChild = symmetricSuccessor;
				}

				symmetricSuccessor.leftChild = this.leftChild;
				if (this.rightChild == symmetricSuccessor) {
					// Avoid a cycle.
					symmetricSuccessor.rightChild = null;
				} else {
					symmetricSuccessor.rightChild = this.rightChild;
				}
			}
		} else {
			if (this.value < value) {
				if (this.rightChild == null) {
					/*
					 * Convention: No exception will be thrown if a value is not in the tree.
					 */
					return;
				}
				this.rightChild.delete(value, this, false);
			} else {
				if (this.leftChild == null) {
					/*
					 * Convention: No exception will be thrown if a value is not in the tree.
					 */
					return;
				}
				this.leftChild.delete(value, this, true);
			}
		}
	}
	
	/**
	 * 
	 *
	 * @param node
	 * @return bla
	 */
	Node getSymmetricSuccessor(Node node) {
		node = node.rightChild;
		Node lastNode = null;
		while (node.leftChild != null) {
			lastNode = node;
			node = node.leftChild;
		}
		
		// Found it.
		if (lastNode != null) {
			lastNode.leftChild = node.rightChild;
		}
		
		// Return it.
		return node;
	}
	
	/**
	 * 
	 *
	 * @param currentLevel
	 * @return TODO
	 */
	public long maximumDeepness(long currentLevel) {
		long newLevel = currentLevel;
		if (this.leftChild != null) {
			newLevel = this.leftChild.maximumDeepness(currentLevel + 1);
		}
		if (this.rightChild != null) {
			long newLevel2 = this.rightChild.maximumDeepness(currentLevel + 1);
			if (newLevel2 > newLevel) {
				newLevel = newLevel2;
			}
		}
		
		return newLevel;
	}
	
	/**
	 * 
	 *
	 * @return TODO
	 */
	long getHighestValue() {
		Node node = this;
		while (node.getRightChild() != null)
			node = node.getRightChild();
		return node.value + 100;
	}
	
	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(String.valueOf(getHighestValue()).length() + 2);
	}

	/**
	 * 
	 *
	 * @param entryLength
	 * @return TODO
	 */
	public String toString(int entryLength) {
		long maximumDeepness = maximumDeepness(0);
		if (maximumDeepness > Integer.MAX_VALUE) {
			return "Tree is too large to be displayed";
		}
		
		int neededLevels = 1;
		if (maximumDeepness > 0) {
			neededLevels = 3 * (int) Math.pow(2, maximumDeepness - 1);
		}
		
		String[] levels = new String[neededLevels];
		for (int a = 0; a < levels.length; a++)
		{
			levels[a] = "";
		}
		
		toString(this, levels, 1, maximumDeepness, 0, entryLength);
		int indent =  3 * (int) Math.pow(2, maximumDeepness - 1) - 1;
		for (int a = 0; a < indent; a++)
		{
			levels[0] += " ";
		}
		levels[0] += "[" + adjustStringLength(String.valueOf(this.value), entryLength - 2) + "]";
		
		String finalString = "";
		for (int a = 0; a < levels.length; a++) {
			finalString += levels[a];
			if (a < levels.length - 1) {
				finalString += "\n";
			}
		}
		return finalString;
	}
	
	/**
	 * 
	 *
	 * @param node
	 * @param levels
	 * @param currentLevel
	 * @param maximumDeepness
	 */
	private void toString(Node node, String[] levels, long currentLevel, long maximumDeepness, int startAt, int entryLength) {
		if (currentLevel > maximumDeepness) {
			return;
		}
		
		int preIndent = 3 * (int) Math.pow(2, maximumDeepness - currentLevel - 1) - 1;
		if (currentLevel == maximumDeepness) {
			preIndent = 0;
		} else if (preIndent < 0) {
			preIndent = 0;
		}
		int betweenIndent = 3 * (int) Math.pow(2, maximumDeepness - currentLevel) - 1;
		if (currentLevel == maximumDeepness) {
			betweenIndent = 3;
		} else if (betweenIndent < 1) {
			betweenIndent = 1;
		}
		int stopAt = startAt + 1;

		startAt = 0;
		stopAt = 0;
		if (maximumDeepness > 0) {
			startAt = 3 * (int) Math.pow(2, maximumDeepness - 1);
			if (currentLevel > 0) {
				stopAt = startAt;
				stopAt -= 3 * (int) Math.pow(2, maximumDeepness - currentLevel) - 1;
			}
			startAt -= 3 * (int) Math.pow(2, maximumDeepness - currentLevel - 1);
			if (currentLevel == maximumDeepness) {
				startAt--;
			}
		}
		
		boolean noLeftChild = true;
		boolean noRightChild = true;
		if (node != null) {
			if (node.leftChild != null) {
				noLeftChild = false;
			}
			if (node.rightChild != null) {
				noRightChild = false;
			}
		}
		
		// Fill in the current values and trigger the recursive calls for lower levels.
		if (currentLevel < maximumDeepness) {
			for (int a = 0; a < preIndent; a++)
			{
				levels[startAt] += " ";
			}
			levels[startAt] += " ";
		}
		
		if (noLeftChild) {
			levels[startAt] += getEmptyString(entryLength);
			toString(null, levels, currentLevel + 1, maximumDeepness, startAt, entryLength);
		} else {
			levels[startAt] += "[" + adjustStringLength(String.valueOf(node.leftChild.value), entryLength - 2) + "]";
			toString(node.leftChild, levels, currentLevel + 1, maximumDeepness, startAt, entryLength);
		}
		
		for (int a = 0; a < betweenIndent; a++)
		{
			levels[startAt] += " ";
		}
		levels[startAt] += " ";
		
		if (noRightChild) {
			levels[startAt] += getEmptyString(entryLength);
			toString(null, levels, currentLevel + 1, maximumDeepness, startAt, entryLength);
		} else {
			levels[startAt] += "[" + adjustStringLength(String.valueOf(node.rightChild.value), entryLength - 2) + "]";
			toString(node.rightChild, levels, currentLevel + 1, maximumDeepness, startAt, entryLength);
		}
		
		for (int a = 0; a < preIndent; a++)
		{
			levels[startAt] += " ";
		}
		levels[startAt] += " ";
		
		// Fill in the slashes.
		for (int l = startAt - 1; l >= stopAt; l--)
		{
			preIndent++;
			betweenIndent -= 2;
			
			if (currentLevel == maximumDeepness) {
				levels[l] += " ";
			} else {
				for (int a = 0; a < preIndent; a++)
				{
					levels[l] += " ";
				}
				levels[l] += " ";
			}
			if (noLeftChild) {
				levels[l] += " ";
			} else {
				levels[l] += "/";
			}
			for (int a = 0; a < betweenIndent; a++)
			{
				levels[l] += " ";
			}
			levels[l] += " ";
			if (noRightChild) {
				levels[l] += " ";
			} else {
				levels[l] += "\\";
			}
			for (int a = 0; a < preIndent; a++)
			{
				levels[l] += " ";
			}
			levels[l] += " ";
		}
	}
	
	/**
	 * 
	 *
	 * @param length
	 * @return TODO
	 */
	private String getEmptyString(int length) {
		switch (length) {
			case  0: return "";
			case  1: return " ";
			case  2: return "  ";
			case  3: return "   ";
			case  4: return "    ";
			case  5: return "     ";
			case  6: return "      ";
			case  7: return "       ";
			case  8: return "        ";
			case  9: return "         ";
			case 10: return "          ";
			case 11: return "           ";
			case 12: return "            ";
			case 13: return "             ";
			case 14: return "              ";
			case 15: return "               ";
			case 16: return "                ";
			case 17: return "                 ";
			case 18: return "                  ";
			case 19: return "                   ";
			case 20: return "                    ";
			case 21: return "                     ";
		}
		
		StringBuilder builder = new StringBuilder();
		for (int a = 0; a < length; a++)
		{
			builder.append(" ");
		}
		return builder.toString();
	}
	
	/**
	 * 
	 *
	 * @param string
	 * @param length
	 * @return TODO
	 */
	private String adjustStringLength(String string, int length) {
		int stringLength = string.length();
		if (stringLength >= length) {
			// Do nothing.
			return string;
		}
		// Calculate the padding.
		int padding = (length - stringLength) / 2;
		
		String left;
		String right = getEmptyString(padding);
		
		// Probably, one padding further is needed for the left side.
		if (padding * 2 + stringLength < length) {
			left = getEmptyString(padding + 1);
		} else {
			left = getEmptyString(padding);
		}
		
		// Combine and return.
		return left + string + right;
	}
}
