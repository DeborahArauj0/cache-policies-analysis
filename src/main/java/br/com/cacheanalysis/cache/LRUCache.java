package br.com.cacheanalysis.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementação da política de cache LRU (Least Recently Used).
 * 
 * Ideia principal:
 * O item menos recentemente utilizado é removido quando o cache atinge
 * sua capacidade máxima.
 * 
 * Estrutura usada:
 * - HashMap para acesso rápido O(1)
 * - Lista duplamente encadeada para controlar a ordem de uso
 * 
 * Elementos mais usados ficam no início da lista.
 * Elementos menos usados ficam no final da lista.
 */

public class LRUCache<T> implements CachePolicy<T> {

    private final int capacity;
    private int hits;
    private int misses;

    // mapa para acesso rápido aos nós
    private final Map<T, Node<T>> cache;

    // nós sentinela (início e fim da lista)
    private final Node<T> head;
    private final Node<T> tail;

    /**
     * Nó da lista duplamente encadeada.
     */
    private static class Node<T> {
        T key;
        Node<T> prev;
        Node<T> next;

        Node(T key) {
            this.key = key;
        }
    }

    /**
     * Construtor do cache LRU.
     * 
     * @param capacity capacidade máxima do cache
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }

        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.cache = new HashMap<>();

        // criação dos nós sentinela
        this.head = new Node<>(null);
        this.tail = new Node<>(null);

        head.next = tail;
        tail.prev = head;
    }

    /**
     * Acessa um item no cache.
     * 
     * Se existir → HIT
     * Se não existir → MISS
     * 
     * @param key chave a ser acessada
     * @return true se houve hit, false se houve miss
     */
    @Override
    public boolean access(T key) {

        Node<T> node = cache.get(key);

        // HIT
        if (node != null) {
            hits++;
            moveToHead(node);
            return true;
        }

        // MISS
        misses++;

        // se o cache estiver cheio remove o menos usado
        if (cache.size() >= capacity) {
            removeTail();
        }

        Node<T> newNode = new Node<>(key);
        cache.put(key, newNode);
        addToHead(newNode);

        return false;
    }

    /**
     * Adiciona um nó no início da lista
     * (representa o elemento mais recentemente usado).
     */
    private void addToHead(Node<T> node) {
        node.prev = head;
        node.next = head.next;

        head.next.prev = node;
        head.next = node;
    }

    /**
     * Remove um nó da lista.
     */
    private void removeNode(Node<T> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * Move um nó para o início da lista
     * (indica que ele foi recentemente utilizado).
     */
    private void moveToHead(Node<T> node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * Remove o nó menos recentemente usado
     * (último elemento antes do tail).
     */
    private void removeTail() {
        Node<T> node = tail.prev;

        if (node == head) {
            return; // lista vazia
        }

        removeNode(node);
        cache.remove(node.key);
    }

    /**
     * Retorna a quantidade de acertos (cache hits).
     */
    @Override
    public int getHits() {
        return hits;
    }

    /**
     * Retorna a quantidade de falhas (cache misses).
     */
    @Override
    public int getMisses() {
        return misses;
    }
}