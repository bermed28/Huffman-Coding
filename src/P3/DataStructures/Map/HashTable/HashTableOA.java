package P3.DataStructures.Map.HashTable;

import java.io.PrintStream;

import ciic4020.linkedlist.LinkedList;
import ciic4020.linkedlist.List;
import ciic4020.map.Map;

public class HashTableOA<K, V> implements Map<K, V> {

	private static class Bucket<K, V> {
		private K key;
		private V value;
		private boolean inUse;

		public Bucket(K key, V value, boolean inUse) {
			this.key = key;
			this.value = value;
			this.inUse = inUse;
		}
		public K getKey() {
			return key;
		}
		public V getValue() {
			return value;
		}
		public boolean isInUse() {
			return inUse;
		}
		public void release() {
			key = null;
			value = null;
			inUse = false;
		}
	}
	
	
	// private fields
	private int currentSize;
	private Bucket<K, V>[] buckets;
	private HashFunction<K> hashFunction;

	
	@SuppressWarnings("unchecked")
	public HashTableOA(int initialCapacity, HashFunction<K> hashFunction) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException("Capacity must be at least 1");
		if (hashFunction == null)
			throw new IllegalArgumentException("Hash function cannot be null");

		currentSize = 0;
		this.hashFunction = hashFunction;
		buckets = new Bucket[initialCapacity];
		for (int i = 0; i < initialCapacity; i++)
			buckets[i] = new Bucket<K, V>(null, null, false);
	}

	@Override
	public V get(K key) {
		if (key == null)
			throw new IllegalArgumentException("Parameter cannot be null.");

		/* First we determine the bucket corresponding to this key */
		int targetBucket = hashFunction.hashCode(key) % buckets.length;
		if (buckets[targetBucket].isInUse() && buckets[targetBucket].getKey().equals(key))
			return buckets[targetBucket].getValue();
		else
			return getLinearProbing(key, targetBucket);
	}

	private V getLinearProbing(K key, int start) {
		int index = getBucket(key, start);
		if (index != -1) // Found it!
			return buckets[index].getValue();
		else
			return null;
	}

	/**
	 * Helper method to find the bucket containing a specific key by probing.
	 * 
	 * @param key    The key to search for
	 * @param start  The index where the collision occurred
	 * @return       The index where the key was found (or -1 if not found)
	 */
	private int getBucket(K key, int start) {
		int n = buckets.length;
		/* Keep checking buckets until we find the key,
		 * or we wrap-around to the start position. */
		for (int i = (start + 1) % n; i != start; i = (i + 1) % n) {
			/* If we reach a bucket not in use, then our key will not be found,
			 * because no value has been added here through probing. */
			if (!buckets[i].isInUse())
				return -1;
			else if (buckets[i].getKey().equals(key))
				return i;
		}
		return -1; // Did not find it
	}

	@Override
	public void put(K key, V value) {
		if (key == null || value == null)
			throw new IllegalArgumentException("Parameter cannot be null.");
		if (size() == buckets.length)
			throw new IllegalStateException("Hash table is full.");

		/* Can't have two elements with same key,
		 * so remove existing element with the given key (if any) */
		remove(key);
		/* Determine the bucket corresponding to this key */
		int targetBucket = hashFunction.hashCode(key) % buckets.length;
		/* If no collision, store it there.  Otherwise, apply probing. */
		if (!buckets[targetBucket].isInUse())
			buckets[targetBucket] = new Bucket<K, V>(key, value, true);
		else // Collision
			putLinearProbing(targetBucket, key, value);
		currentSize++;
	}

	/**
	 * Perform linear probing to find an empty bucket where value can be added.
	 * It is assumed that there's room for the new value.
	 * 
	 * @param start  Index where initial collision occurred.
	 * @param key    Key of value to be added.
	 * @param value  Value to be added.
	 */
	private void putLinearProbing(int start, K key, V value) {
		int n = buckets.length;
		for (int i = (start + 1) % n; i != start; i = (i + 1) % n) {
			if (!buckets[i].isInUse()) {
				buckets[i] = new Bucket<K, V>(key, value, true);
				break;
			}
		}
		
	}

	@Override
	public V remove(K key) {
		if (key == null)
			throw new IllegalArgumentException("Parameter cannot be null.");

		/* First we determine the bucket corresponding to this key */
		int targetBucket = hashFunction.hashCode(key) % buckets.length;
		Bucket<K, V> b = buckets[targetBucket];
		if (b.isInUse() && b.getKey().equals(key)) {
			V v = b.getValue();
			b.release();
			currentSize--;
			return v;
		}
		else
			return removeLinearProbing(key, targetBucket);
	}

	private V removeLinearProbing(K key, int start) {
		int index = getBucket(key, start);
		if (index != -1) { // Found it!
			V v = buckets[index].getValue();
			buckets[index].release();
			currentSize--;
			return v;
		}
		else
			return null;
	}

	@Override
	public boolean containsKey(K key) {
		return get(key) != null;
	}

	@Override
	public List<K> getKeys() {
		List<K> result = new LinkedList<K>();
		for (int i = 0; i < buckets.length; i++)
			if (buckets[i].isInUse())
				result.add(0, buckets[i].getKey());
		return result;
	}

	@Override
	public List<V> getValues() {
		List<V> result = new LinkedList<V>();
		for (int i = 0; i < buckets.length; i++)
			if (buckets[i].isInUse())
				result.add(0, buckets[i].getValue());
		return result;
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
		currentSize = 0;
		for (int i = 0; i < buckets.length; i++)
			buckets[i].release();
	}

	@Override
	public void print(PrintStream out) {
		/* For each bucket in use in the hash table, print the elements */
		for (int i = 0; i < buckets.length; i++) {
			Bucket<K, V> b = buckets[i];
			if (b.isInUse())
				out.printf("(%s, %s)\n", b.getKey(), b.getValue());
		}

	}

}