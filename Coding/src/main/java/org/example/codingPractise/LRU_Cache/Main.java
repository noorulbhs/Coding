package org.example.codingPractise.LRU_Cache;

public class Main {
    public static void main(String[] args) {
        LRU lru = new LRU(5);
        lru.put(1,1);
        lru.put(2,2);
        lru.put(3,3);
        lru.put(4,4);
        lru.put(5,5);
//        System.out.println(lru.get(4));
//        System.out.println(lru.get(5));
        lru.put(6,6);
        System.out.println(lru.get(6));
        System.out.println(lru.get(1));
        System.out.println(lru.get(2));
        lru.put(7,7);
        System.out.println(lru.get(2));
        System.out.println(lru.get(3));
    }
}
