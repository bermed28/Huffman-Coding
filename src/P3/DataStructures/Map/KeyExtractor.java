package P3.DataStructures.Map;

public interface KeyExtractor<K, V> {

	K getKey(V value);
}