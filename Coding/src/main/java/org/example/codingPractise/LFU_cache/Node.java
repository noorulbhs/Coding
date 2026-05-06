package org.example.codingPractise.LFU_cache;

public class Node {
    public int key, value, freq;
    public Node prev, next;

    public Node(int key, int value){
        this.key = key;
        this.value = value;
        this.freq = 1;
        this.prev = null;
        this.next = null;
    }

}
