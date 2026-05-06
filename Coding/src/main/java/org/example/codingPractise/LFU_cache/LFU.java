package org.example.codingPractise.LFU_cache;

import java.util.HashMap;

public class LFU {
    private final int size;
    private final HashMap<Integer, Node> keyNodeMap;
    private final HashMap<Integer, DoublyLinkedList> freqListMap;
    int minFreq = 0;

    public LFU(int size){
        this.size = size;
        this.keyNodeMap = new HashMap<>();
        this.freqListMap = new HashMap<>();
    }

    public int get(int key){
        if(keyNodeMap.containsKey(key)){
            Node node = keyNodeMap.get(key);
            updateFreq(node);
            return node.value;
        }
        return -1;
    }

    public void put(int key, int value){
        if(size == 0){
            return;
        }
        if(keyNodeMap.containsKey(key)) {
            Node node = keyNodeMap.get(key);
            node.value = value;
            updateFreq(node);
        }else{
            if(keyNodeMap.size() >= size){
                Node node = freqListMap.get(minFreq).tail;
                keyNodeMap.remove(node.key);
                freqListMap.get(minFreq).removeTail();
            }
            Node newNode = new Node(key, value);
            minFreq = 1;
            keyNodeMap.put(key, newNode);
            freqListMap.computeIfAbsent(minFreq,k->new DoublyLinkedList());
            freqListMap.get(minFreq).addNodeHead(newNode);
        }
    }

    public void updateFreq(Node node){
        int freq = node.freq;
        DoublyLinkedList freqList = freqListMap.get(freq);
        freqList.removeNode(node);

        if(freq == minFreq && freqList.isEmpty()){
            minFreq++;
        }
        node.freq++;
        freqListMap.computeIfAbsent(node.freq,k->new DoublyLinkedList());
        freqListMap.get(node.freq).addNodeHead(node);
    }
}
