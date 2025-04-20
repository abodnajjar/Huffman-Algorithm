package application;

public class BinaryTree {

	char ch;
	String huffCode;
	BinaryTree left;
	BinaryTree right;

	public BinaryTree() {
		ch = '\0';
		huffCode = "";
		left = right = null;

	}

	public BinaryTree(char ch, String huffCode) {
		this.ch = ch;
		this.huffCode = huffCode;
		left = right = null;
	}

	public static BinaryTree addElt(BinaryTree tree, String st, int index, char ch) {
		if (index < st.length()) {
			// If the current bit of huffman code is 0, traverse left in the tree
			if (st.charAt(index) == '0') {
				// If the left child is null, create a new node
				if (tree.left == null)
					tree.left = new BinaryTree();
				// Recursively add the element in the left subtree
				tree.left = addElt(tree.left, st, index + 1, ch);
			} else { // If the current bit of huffman code is '1', traverse right in the tree
				if (tree.right == null)
					tree.right = new BinaryTree();
				tree.right = addElt(tree.right, st, index + 1, ch);
			}
			return tree;
		} else {
			tree.ch = ch; // Set the character in the current node when the end of the code is reached
			return tree;
		}
	}
	
}
