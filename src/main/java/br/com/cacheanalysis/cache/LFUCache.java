package br.com.cacheanalysis.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Implementação da política de cache LFU (Least Frequently Used) com complexidade O(1).
 *
 * Remove o item menos frequentemente utilizado quando a capacidade máxima é atingida.
 * Em caso de empate, o critério de desempate é o tempo na estrutura (FIFO).
 */
public class LFUCache<T> implements CachePolicy<T> {

    private final int capacity;
    private int hits;
    private int misses;
    private int minFrequencia;

    private final Map<T, Integer> cacheMap;
    private final Map<Integer, LinkedHashSet<T>> freqMap;

    /**
     * Inicializa o cache LFU com a capacidade especificada.
     *
     * @param capacity Capacidade máxima do cache.
     * @throws IllegalArgumentException se a capacidade for menor ou igual a zero.
     */
    public LFUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }
        
        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.minFrequencia = 0;

        // Otimização: capacidade inicial definida para evitar rehashing
        this.cacheMap = new HashMap<>(capacity, 1.0f);
        this.freqMap = new HashMap<>();
    }

    /**
     * Acessa um elemento no cache, atualizando sua frequência de uso.
     *
     * @param key A chave do elemento a ser acessado.
     * @return true se o elemento estava no cache (HIT), false caso contrário (MISS).
     */
    @Override
    public boolean access(T key) {
        if (cacheMap.containsKey(key)) {
            hits++;
            atualizarFrequencia(key);
            return true;
        }

        misses++;

        if (cacheMap.size() >= capacity) {
            removerElementoMenosFrequente();
        }

        cacheMap.put(key, 1);
        freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFrequencia = 1;

        return false;
    }

    /**
     * Incrementa a frequência de acesso de uma chave existente.
     *
     * @param key A chave a ser atualizada.
     */
    private void atualizarFrequencia(T key) {
        int freqAtual = cacheMap.get(key);
        int freqNova = freqAtual + 1;

        cacheMap.put(key, freqNova);

        LinkedHashSet<T> bucketAtual = freqMap.get(freqAtual);
        bucketAtual.remove(key);

        if (bucketAtual.isEmpty()) {
            freqMap.remove(freqAtual);
            if (freqAtual == minFrequencia) {
                minFrequencia = freqNova;
            }
        }

        freqMap.computeIfAbsent(freqNova, k -> new LinkedHashSet<>()).add(key);
    }

    /**
     * Remove o elemento com a menor frequência de acesso do cache.
     * Limpa a estrutura de mapeamento caso a frequência fique vazia.
     */
    private void removerElementoMenosFrequente() {
        LinkedHashSet<T> bucketMin = freqMap.get(minFrequencia);
        T chaveRemovida = bucketMin.iterator().next();

        bucketMin.remove(chaveRemovida);
        cacheMap.remove(chaveRemovida);

        if (bucketMin.isEmpty()) {
            freqMap.remove(minFrequencia);
        }
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
