package org.jabrimuhi.collections;

import java.util.List;
import java.util.Set;

public interface MyHashMapInterface<K, V> {
    void put(K key, V value) throws Exception;
    V get(K key) throws Exception;
    Set<K> keySet();
    List<V> values();
    boolean remove(K key) throws Exception;
    int size();
    void clear();
}
