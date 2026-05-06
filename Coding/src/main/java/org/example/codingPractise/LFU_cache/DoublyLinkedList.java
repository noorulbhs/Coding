package org.example.codingPractise.LFU_cache;

public class DoublyLinkedList {
    public Node head, tail;

    public DoublyLinkedList(){
        Node node = new Node(-1, -1);
        head = tail = node;
    }

    public void addNodeHead(Node node){
        head.next = node;
        node.prev = head;
        if(tail == head){
            tail = node;
        }
    }

    public void removeNode(Node node){
        if(node == tail){
            node.prev.next = null;
            tail = node.prev;
            return;
        }
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    public void removeTail(){
        if(tail == head){
            return;
        }
        tail.prev.next = null;
        tail = tail.prev;
    }

    public boolean isEmpty(){
        return head == tail;
    }
}
