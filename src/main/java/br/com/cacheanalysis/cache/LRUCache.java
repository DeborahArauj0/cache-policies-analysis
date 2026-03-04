package br.com.cacheanalysis.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<T> implements CachePolicy<T> {
    private final int capacity;
    private int hits;
    private int misses;
    
    //Map garante a busca em O(1)
    private final Map<T, Node> cache;
    // Cabeça e cauda "falsas" para facilitar a manipulação da lista
    private final Node head;
    private final Node tail;

    //Estrutura do nó da Lista Duplamente Encadeada
    private class Node {
        T key;
        Node prev;
        Node next;
        Node(T key) { this.key = key; }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.cache = new HashMap<>();
        
        this.head = new Node(null);
        this.tail = new Node(null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public boolean access(T key) {
        if (cache.containsKey(key)) {
            hits++;
            Node node = cache.get(key);
            moveToHead(node); //Paciente acessado vai pro topo da prioridade
            return true;
        } else {
            misses++;
            if (cache.size() >= capacity) {
                removeTail(); //Posto lotou? Expulsa quem está há mais tempo sem ser chamado
            }
            Node newNode = new Node(key);
            cache.put(key, newNode);
            addToHead(newNode);
            return false;
        }
    }

    //Metodos auxiliares para mexer os ponteiros da lista
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void removeTail() {
        Node res = tail.prev;
        removeNode(res);
        cache.remove(res.key);
    }

    @Override
    public int getHits() { return hits; }

    @Override
    public int getMisses() { return misses; }
}
