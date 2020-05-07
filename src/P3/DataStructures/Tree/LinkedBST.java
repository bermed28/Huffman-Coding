package P3.DataStructures.Tree;

import java.util.Iterator;

import P3.DataStructures.SortedList.SortedArrayList;
import P3.DataStructures.SortedList.SortedLinkedList;
import P3.DataStructures.SortedList.SortedList;
import P3.DataStructures.Tree.utils.Comparator;


/**
 * Linked Binary Search Tree implementation of the BST ADT.
 * @author Juan O. Lopez
 *
 * @param <K> Generic data type for the keys of the values being stored
 * @param <V> Generic data type for the values being stored
 */
public class LinkedBST<K extends Comparable<? super K>, 
					   V extends Comparable<? super V>> implements BinarySearchTree<K, V> {
	private int currentSize;
	private BTNode<K, V> root;
	private Comparator<K> comparator;

	public LinkedBST(Comparator<K> comparator) {
		if (comparator == null)
			throw new IllegalArgumentException("Comparator cannot be null.");
		this.comparator = comparator;
		currentSize = 0;
		root = null;
	}

	@Override
	public Iterator<V> iterator() {
		return getValues().iterator();
	}

	@Override
	public void add(K key, V value) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null.");
		if (value == null)
			throw new IllegalArgumentException("Value cannot be null.");

		if (root == null)
			root = new BTNode<>(key, value);
		else
			addAux(key, value, root);

		currentSize++;
	}
	
	private void addAux(K key, V value, BTNode<K, V> N) {
		int comparison = comparator.compare(key, N.getKey());
		if (comparison < 0) {
			if (N.getLeftChild() == null)
				N.setLeftChild(new BTNode<K, V>(key, value, N));
			else
				addAux(key, value, N.getLeftChild());
		}
		else {
			if (N.getRightChild() == null)
				N.setRightChild(new BTNode<K, V>(key, value, N));
			else
				addAux(key, value, N.getRightChild());
			
		}
	}

	@Override
	public V remove(K key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null.");
		if (isEmpty())
			return null;
		return removeAux(key, root);
	}
	
	private V removeAux(K key, BTNode<K, V> N) {
		if (N == null)
			return null;
		int comparison = comparator.compare(key, N.getKey());
		if (comparison == 0) { // Found it!
			V v = N.getValue();
			removeNode(N);
			currentSize--;
			return v;
		}
		else if (comparison < 0)
			return removeAux(key, N.getLeftChild());
		else
			return removeAux(key, N.getRightChild());
	}
	
	private void removeNode(BTNode<K, V> N) {
		/* Retrieve children for convenience */
		BTNode<K, V> left  = N.getLeftChild();
		BTNode<K, V> right = N.getRightChild();

		/* Three possible scenarios: it's a leaf, it has one child, or it has two children */
		if (left != null && right != null) { // N has two children
			/* We'll "swap" N's predecessor into N's place (also works if we use the successor).
			 * Instead of swapping nodes and setting pointers, we'll just copy the key/value,
			 * that way we don't have to change N's parent (if any) or any child pointers of N. */
			BTNode<K, V> predecessor = left;
			while (predecessor.getRightChild() != null) // Keep descending to the right
				predecessor = predecessor.getRightChild();
			N.setKey(predecessor.getKey());
			N.setValue(predecessor.getValue());
			/* Predecessor's contents have been copied, so we no longer need it. */
			removeNode(predecessor);
		}
		else { // N has 1 or 0 children; we will bypass it
			BTNode<K, V> ntk; // Node to keep (i.e. N's replacement; null if N is a leaf)
			if (left == null)
				ntk = right;
			else
				ntk = left;
			if (N == root) { // If N is the root, then we must find a new root
				root = ntk;
			}
			else {
				/* In this case we'll re-route N's parent so that it skips N.
				 * We'll need to know if N is a left child or a right child. */
				BTNode<K, V> parent = N.getParent();
				if (parent.getLeftChild() == N)
					parent.setLeftChild(ntk);
				else
					parent.setRightChild(ntk);
				if (ntk != null)
					ntk.setParent(parent);
			}
			/* We've bypassed N, so we can now clear its contents */
			N.clear();
		}
		
	}

	@Override
	public V get(K key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null.");
		return getAux(key, root);
	}
	
	private V getAux(K key, BTNode<K, V> N) {
		if (N == null)
			return null;
		else {
			int comparison = comparator.compare(key, N.getKey());
			if (comparison == 0)
				return N.getValue();
			else if (comparison < 0)
				return getAux(key, N.getLeftChild());
			else
				return getAux(key, N.getRightChild());
			
		}
	}

	@Override
	public SortedList<K> getKeys() {
		/* Since we know how many elements there are, use an ArrayList. */
		SortedList<K> keys = new SortedArrayList<K>(size());
		getKeysAux(root, keys);
		return keys;
	}
	
	private void getKeysAux(BTNode<K, V> N, SortedList<K> keys) {
		if (N != null) {
			getKeysAux(N.getLeftChild(), keys);
			keys.add(N.getKey());
			getKeysAux(N.getRightChild(), keys);
		}
	}

	@Override
	public SortedList<V> getValues() {
		/* Since we know how many elements there are, use an ArrayList. */
		SortedArrayList<V> values = new SortedArrayList<V>(size());
		getValuesAux(root, values);
		return values;
	}

	private void getValuesAux(BTNode<K, V> N, SortedList<V> values) {
		if (N != null) {
			getValuesAux(N.getLeftChild(), values);
			values.add(N.getValue());
			getValuesAux(N.getRightChild(), values);
		}
	}

	@Override
	public boolean contains(K key) {
		return get(key) != null;
	}

	@Override
	public int height() {
		if (isEmpty())
			return -1; // Distinguishes an empty tree from a tree with only a root
		else
			return heightAux(root);
	}
	
	private int heightAux(BTNode<K, V> N) {
		if (N.getLeftChild() == null && N.getRightChild() == null)
			return 0;
		else {
			int h1 = 0, h2 = 0;
			if (N.getLeftChild() != null)
				h1 = 1 + heightAux(N.getLeftChild());
			if (N.getRightChild() != null)
				h2 = 1 + heightAux(N.getRightChild());
			return Math.max(h1,  h2);
		}
	}

	@Override
	public int size() {
		return currentSize;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public void clear() {
		/* The following code works, but it's inefficient.
		 * What's the running time?
		while (!isEmpty())
			remove(root.getKey());
		*/

		/* Technically, we could just set root to null and currentSize to 0,
		 * and let the Garbage Collector do the rest, but it's a good idea to
		 * always clean up after ourselves instead of waiting for the GC. */
		clearAux(root);
		root = null;
		currentSize = 0;
	}
	
	private void clearAux(BTNode<K, V> N) {
		/* Analyze the code and determine its running time. */
		if (N != null) {
			clearAux(N.getLeftChild());
			clearAux(N.getRightChild());
			/* At this point, N should be a leaf */
			N.clear();
		}
	}

	/**
	 * Return the root node; necessary for BinaryTreePrinter class
	 * 
	 * @return Root node
	 */
	public BTNode<K, V> root() {
		return root;
	}

}