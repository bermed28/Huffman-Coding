package P3.DataStructures.Tree;

import P3.DataStructures.SortedList.SortedList;

/**
 * Here we define the Binary Search Tree ADT with all of the methods
 * that must be supported.  Elements to be stored are composed of
 * key/value pairs.  A print method has been included for testing purposes.
 * @author Juan O. Lopez
 *
 * @param <K> Generic data type for the keys of the values being stored
 * @param <V> Generic data type for the values being stored
 */
public interface BinarySearchTree<K extends Comparable<? super K>, 
								  V extends Comparable<? super V>> extends Iterable<V>{

	/**
	 * Add a new element to the tree, with the specified key/value.
	 * 
	 * @param key   Key of the element to be added
	 * @param value Value of the element to be added
	 * @throws IllegalArgumentException If the key or value are null
	 */
	void add(K key, V value) throws IllegalArgumentException;
	
	/**
	 * Removes from the tree the element with the specified key.
	 * 
	 * @param key Key of the element to be removed
	 * @return Value of the element that was removed, or null if not found
	 * @throws IllegalArgumentException If the key is null
	 */
	V remove(K key) throws IllegalArgumentException;
	
	/**
	 * Retrieves the value of the element with the specified key.
	 * 
	 * @param key Key of the element to search for
	 * @return Value of the element with the specified key, or null if not found
	 * @throws IllegalArgumentException If the key is null
	 */
	V get(K key) throws IllegalArgumentException;
	
	/**
	 * Returns a list of the keys of the elements, using in-order traversal.
	 * 
	 * @return List of the keys of the elements, using in-order traversal
	 */
	SortedList<K> getKeys();
	
	/**
	 * Returns a list of the values of the elements, using in-order traversal.
	 * 
	 * @return List of the values of the elements, using in-order traversal
	 */
	SortedList<V> getValues();

	/**
	 * Determines whether the tree contains an element with the specified key.
	 * 
	 * @param key Key of the element to search for
	 * @return True if the tree contains an element with the specified key; false otherwise
	 */
	boolean contains(K key);
	
	/**
	 * Returns the height of the tree, determined as
	 * the length of the longest path from the root to a leaf.
	 * 
	 * @return The height of the tree, or -1 if the tree is empty.
	 */
	int height();

	/**
	 * Determines the number of elements currently in the tree.
	 * 
	 * @return The number of elements currently in the tree
	 */
	int size();
	
	/**
	 * Determines whether the tree is empty.
	 * 
	 * @return True if empty; false otherwise.
	 */
	boolean isEmpty();
	
	/**
	 * Removes all of the elements from the tree.
	 */
	void clear();
}