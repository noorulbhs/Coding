package org.example.codingPractise.LRU_Cache;

public class DoublyLinkedList {
    public Node head;
    public Node tail;

    public DoublyLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public void addNode(int key, int val){
        Node newNode = new Node(key,val);
        if(tail == null){
            tail = newNode;
            head = newNode;
        }
        tail.next = newNode;
        newNode.prev = tail;
        tail = newNode;
    }

    public void removeNode(Node node){
        if(node == head){
            head.next.prev = null;
            head = head.next;
        } else if(node == tail){
            tail.prev.next = null;
            tail = tail.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public void removeHead(){
        if(head != null){
           removeNode(head);
        }
    }
}
