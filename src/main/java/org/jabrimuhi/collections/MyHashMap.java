package org.jabrimuhi.collections;

import java.util.*;

public class MyHashMap<K, V> implements MyHashMapInterface<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int TREEIFY_THRESHOLD = 8;
    private static final int UNTREEIFY_THRESHOLD = 6;

    private Object[] array = new Object[DEFAULT_INITIAL_CAPACITY];
    private int size = 0;

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void put(K key, V value) {
        if (size >= (array.length * DEFAULT_LOAD_FACTOR)) {
            increaseArrayCapacity();
        }
        int position = getElementPosition(key, array.length);
        Object bucket = array[position];

        if (bucket == null) {
            array[position] = new Entry<>(key, value, null);
            size++;
        } else if (bucket instanceof Entry) {
            Entry<K, V> existing = (Entry<K, V>) bucket;
            Entry<K, V> current = existing;
            int count = 0;

            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                count++;
                current = current.next;
            }

            array[position] = new Entry<>(key, value, existing);
            size++;

            if (count >= TREEIFY_THRESHOLD) {
                array[position] = treeifyBucket((Entry<K, V>) bucket);
            }
        } else if (bucket instanceof TreeMap) {
            TreeMap<K, V> tree = (TreeMap<K, V>) bucket;
            tree.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K key) {
        int position = getElementPosition(key, array.length);
        Object bucket = array[position];

        if (bucket == null) {
            return null;
        } else if (bucket instanceof Entry) {
            Entry<K, V> current = (Entry<K, V>) bucket;
            while (current != null) {
                if (current.key.equals(key)) {
                    return current.value;
                }
                current = current.next;
            }
        } else if (bucket instanceof TreeMap) {
            TreeMap<K, V> tree = (TreeMap<K, V>) bucket;
            return tree.get(key);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();

        for (Object entry : array) {
            Entry<K, V> existedElement = (Entry<K, V>) entry;

            while(existedElement != null) {
                result.add(existedElement.key);
                existedElement = existedElement.next;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<V> values() {
        List<V> result = new ArrayList<>();

        for (Object entry : array) {
            Entry<K, V> existedElement = (Entry<K, V>) entry;

            while(existedElement != null) {
                result.add(existedElement.value);
                existedElement = existedElement.next;
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(K key) {
        int position = getElementPosition(key, array.length);
        Object bucket = array[position];

        if (bucket == null) {
            return false;
        } else if (bucket instanceof Entry) {
            Entry<K, V> current = (Entry<K, V>) bucket;
            Entry<K, V> prev = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        array[position] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    if (bucketSize((Entry<K, V>) array[position]) <= UNTREEIFY_THRESHOLD) {
                        array[position] = untreeifyBucket((Entry<K, V>) array[position]);
                    }
                    return true;
                }
                prev = current;
                current = current.next;
            }
        } else if (bucket instanceof TreeMap) {
            TreeMap<K, V> tree = (TreeMap<K, V>) bucket;
            if (tree.remove(key) != null) {
                if (tree.size() <= UNTREEIFY_THRESHOLD) {
                    array[position] = untreeifyBucketFromTree(tree);
                }
                size--;
                return true;
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
        array = new Object[DEFAULT_INITIAL_CAPACITY];
        size = 0;
    }

    private TreeMap<K, V> treeifyBucket(Entry<K, V> bucket) {
        TreeMap<K, V> tree = new TreeMap<>();
        Entry<K, V> current = bucket;
        while (current != null) {
            tree.put(current.key, current.value);
            current = current.next;
        }
        return tree;
    }

    private Entry<K, V> untreeifyBucketFromTree(TreeMap<K, V> tree) {
        Entry<K, V> bucket = null;
        for (Map.Entry<K, V> entry : tree.entrySet()) {
            bucket = new Entry<>(entry.getKey(), entry.getValue(), bucket);
        }
        return bucket;
    }

    private Entry<K, V> untreeifyBucket(Entry<K, V> bucket) {
        return bucket; // Просто возвращаем тот же список, так как преобразование не нужно
    }

    private int bucketSize(Entry<K, V> bucket) {
        int count = 0;
        Entry<K, V> current = bucket;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    private int getElementPosition(K key, int arrayLength) {
        return Math.abs(key.hashCode() % arrayLength);
    }

    @SuppressWarnings("unchecked")
    private void increaseArrayCapacity() {
        Object[] newArray = new Object[array.length * 2];

        for (Object bucket : array) {
            if (bucket instanceof Entry) {
                Entry<K, V> current = (Entry<K, V>) bucket;
                while (current != null) {
                    put(current.key, current.value, newArray);
                    current = current.next;
                }
            } else if (bucket instanceof TreeMap) {
                TreeMap<K, V> tree = (TreeMap<K, V>) bucket;
                for (Map.Entry<K, V> entry : tree.entrySet()) {
                    put(entry.getKey(), entry.getValue(), newArray);
                }
            }
        }

        array = newArray;
    }

    @SuppressWarnings("unchecked")
    private void put(K key, V value, Object[] dst) {
        int position = getElementPosition(key, dst.length);
        Object bucket = dst[position];

        if (bucket == null) {
            dst[position] = new Entry<>(key, value, null);
        } else if (bucket instanceof Entry) {
            dst[position] = new Entry<>(key, value, (Entry<K, V>) bucket);
        } else if (bucket instanceof TreeMap) {
            ((TreeMap<K, V>) bucket).put(key, value);
        }
    }
}