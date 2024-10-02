package org.jabrimuhi.collections;

import java.util.*;

public class MyHashMap<K, V> implements MyHashMapInterface<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private Entry<K, V>[] array = (Entry<K, V>[]) new Entry[DEFAULT_INITIAL_CAPACITY];
    private int size = 0;



    private static class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;
        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public void put(K key, V value) {
        if (size >= (array.length * DEFAULT_LOAD_FACTOR)) {
            increaseArrayCapacity();
        }
        boolean put = put(key, value, array);
        if (put) {
            size++;
        }
    }

    private boolean put(K key, V value, Entry<K, V>[] dst) {
        int position = getElementPosition(key, dst.length);
        Entry<K, V> existedElement = dst[position];

        if (existedElement == null) {
            Entry<K, V> newElement = new Entry<K, V>(key, value, null);
            dst[position] = newElement;
            return true;
        } else {
            while (true) {
                if (existedElement.key.equals(key)) {
                    existedElement.value = value;
                    return false;
                }

                if (existedElement.next == null) {
                    existedElement.next = new Entry<>(key, value, null);
                    size++;
                    return true;
                }
                existedElement = existedElement.next;
            }
        }
    }



    @Override
    public V get(K key) {
        int position = getElementPosition(key, array.length);
        Entry<K, V> existedElement = array[position];

        while (existedElement != null) {
            if (existedElement.key.equals(key)) {
                return existedElement.value;
            }
            existedElement = existedElement.next;
        }

        return null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();

        for (Entry<K, V> entry : array) {
            Entry<K, V> existedElement = entry;

            while(existedElement != null) {
                result.add(existedElement.key);
                existedElement = existedElement.next;
            }
        }

        return result;
    }

    @Override
    public List<V> values() {
        List<V> result = new ArrayList<>();

        for (Entry<K, V> entry : array) {
            Entry<K, V> existedElement = entry;

            while(existedElement != null) {
                result.add(existedElement.value);
                existedElement = existedElement.next;
            }
        }

        return result;
    }

    @Override
    public boolean remove(K key) {
        int position = getElementPosition(key, array.length);
        Entry<K, V> existedElement = array[position];
        if (existedElement != null && existedElement.key.equals(key)) {
            array[position] = existedElement.next;
            size--;
            return true;
        } else {
            while (existedElement != null) {
                Entry<K, V> nextElement = existedElement.next;
                if (nextElement == null) {
                    return false;
                }

                if (nextElement.key.equals(key)) {
                    existedElement.next = nextElement.next;
                    size--;
                    return true;
                }

                existedElement = existedElement.next;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        array = (Entry<K, V>[]) new Entry[DEFAULT_INITIAL_CAPACITY];
        size = 0;
    }

    private int getElementPosition(K key, int arrayLength) {
        return Math.abs(key.hashCode() % arrayLength);
    }

    private void increaseArrayCapacity() {
        Entry<K, V>[] newArray = (Entry<K, V>[]) new Entry[array.length * 2];

        for (Entry<K, V> entry : array) {
            Entry<K, V> existedElement = entry;

            while(existedElement != null) {
                put(existedElement.key, existedElement.value, newArray);
                existedElement = existedElement.next;
            }
        }

        array = newArray;
    }
}
