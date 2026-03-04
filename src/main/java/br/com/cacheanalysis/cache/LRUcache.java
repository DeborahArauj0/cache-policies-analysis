package br.com.cacheanalysis.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<T> implements CachePolicy<T> {
    
    private final int capacity;
    private int hits;
    private int misses;
    
    private final Map<T, Node<T>> cache;
    private final Node<T> head;
    private final Node<T> tail;

    private static class Node<T> {
        T key;
        Node<T> prev;
        Node<T> next;
        
        Node(T key) { 
            this.key = key; 
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.cache = new HashMap<>();
        
        this.head = new Node<>(null);
        this.tail = new Node<>(null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public boolean access(T key) {
        Node<T> node = cache.get(key);
        
        if (node != null) {
            hits++;
            moveToHead(node);
            return true;
        }
        
        misses++;
        
        if (cache.size() >= capacity) {
            removeTail();
        }
        
        Node<T> newNode = new Node<>(key);
        cache.put(key, newNode);
        addToHead(newNode);
        
        return false;
    }

    private void addToHead(Node<T> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<T> node) {
        removeNode(node);
        addToHead(node);
    }

    private void removeTail() {
        Node<T> res = tail.prev;
        removeNode(res);
        cache.remove(res.key);
    }

    @Override
    public int getHits() { 
        return hits; 
    }

    @Override
    public int getMisses() { 
        return misses; 
    }
}
