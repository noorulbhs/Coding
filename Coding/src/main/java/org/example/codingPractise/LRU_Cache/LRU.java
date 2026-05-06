package org.example.codingPractise.LRU_Cache;

import java.util.HashMap;

public class LRU {
    private final int size;
    private final HashMap<Integer,Node> cache;
    private final DoublyLinkedList linkedList;

    public LRU(int size){
        this.size = size;
        this.cache = new HashMap<>();
        linkedList = new DoublyLinkedList();
    }

    public void put(int key, int value){
        if(!cache.containsKey(key)){
            if(cache.size() >= size){
                cache.remove(linkedList.head.key);
                linkedList.removeHead();
            }
        }else{
            Node node = cache.get(key);
            cache.remove(node.key);
            linkedList.removeNode(node);
        }
        linkedList.addNode(key, value);
        cache.put(key,linkedList.tail);
    }

    public int get(int key){
        if(cache.containsKey(key)){
            Node node = cache.get(key);
            cache.remove(node.key);
            linkedList.removeNode(node);
            linkedList.addNode(key, node.val);
            cache.put(key,linkedList.tail);
            return node.val;
        }
        return -1;
    }
}
