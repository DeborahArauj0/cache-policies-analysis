package br.com.cacheanalysis.cache;

import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementação da política de cache FIFO (First-In, First-Out) com complexidade O(1).
 * O elemento mais antigo a entrar no cache é o primeiro a ser removido quando cheio.
 */
public class FIFOCache<T> implements CachePolicy<T> {

    private final int capacity;
    private final Queue<T> queue;
    private final Set<T> cacheSet;

    private int hits;
    private int misses;

    /**
     * Inicializa o cache FIFO com a capacidade especificada.
     *
     * @param capacity Capacidade máxima do cache.
     * @throws IllegalArgumentException se a capacidade for menor ou igual a zero.
     */
    public FIFOCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.cacheSet = new HashSet<>(capacity, 1.0f);
        this.hits = 0;
        this.misses = 0;
    }

    @Override
    public boolean access(T key) {

        // HIT
        if (cacheSet.contains(key)) {
            hits++;
            return true;
        }

        // MISS
        misses++;

        // cache cheio → remove mais antigo
        if (queue.size() >= capacity) {
            T removed = queue.poll();
            cacheSet.remove(removed);
            // System.out.println("REMOVEU: " + removed);
        }

        // adiciona novo
        queue.offer(key);
        cacheSet.add(key);

        return false;
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