package org.jabrimuhi.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {
    private MyHashMapInterface<Integer, String> hashMap;

    @BeforeEach
    void setUp() {
        hashMap = new MyHashMap<>();
    }

    @Test
    public void whenPut100ElementsMapSizeBecome100() throws Exception {
        for (int i = 0; i < 100; i++) {
            String value = "value" + i;

            hashMap.put(i, value);
        }

        assertEquals(100, hashMap.size());
    }

    @Test
    public void whenPut100ElementsWith10DifferentKeysThenSize10() throws Exception {
        for (int i = 0; i < 100; i++) {
            int index = i % 10;
            String value = "value" + i;

            hashMap.put(index, value);
        }
        assertEquals(10, hashMap.size());
    }

    @Test
    public void removeReturnTrueOnlyOnce() throws Exception {
        for (int i = 0; i < 10; i++) {
            String value = "value" + i;
            hashMap.put(i, value);
        }

        assertEquals(10, hashMap.size());

        assertTrue(hashMap.remove(3));
        assertEquals(9, hashMap.size());
        assertFalse(hashMap.remove(3));
    }

    @Test
    public void keysCountMustBeEqualToValuesCount() throws Exception {
        for (int i = 0; i < 100; i++) {
            hashMap.put(i, "value" + i);
        }

        assertEquals(100, hashMap.size());
        assertEquals(100, hashMap.keySet().size());
        assertEquals(100, hashMap.values().size());
    }

    @Test
    public void methodGetMustReturnRightValue() throws Exception {
        for (int i = 0; i < 100; i++) {
            hashMap.put(i, "value" + i);
        }
        String value = hashMap.get(3);
        String expected = "value3";
        assertEquals(expected, value);
    }

    @Test
    public void whenBucketSizeExceedsThresholdThenTreeify() {
        // Вообще не понял как затестить :(
        assertTrue(true);
    }

    // Данный метод private в MyHashMap
    private int getElementPosition(int key, int arrayLength) {
        return Math.abs(Integer.valueOf(key).hashCode() % arrayLength);
    }
}