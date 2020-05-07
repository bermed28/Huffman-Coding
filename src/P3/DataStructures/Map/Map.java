package P3.DataStructures.Map;

import java.io.PrintStream;

import P3.DataStructures.List.List;


public interface Map<K, V> {

	V get(K key);
	void put(K key, V value);
	V remove(K key);
	boolean containsKey(K key);
	List<K> getKeys();
	List<V> getValues();
	int size();
	boolean isEmpty();
	void clear();
	void print(PrintStream out); /* For debugging purposes */
}