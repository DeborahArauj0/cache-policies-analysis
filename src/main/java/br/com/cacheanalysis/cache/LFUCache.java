package br.com.cacheanalysis.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Implementação da política de cache LFU (Least Frequently Used).
 *
 * Ideia principal:
 * O item menos frequentemente utilizado é removido quando o cache
 * atinge sua capacidade máxima. Em caso de empate de frequência,
 * o item mais antigo (inserido primeiro) é removido — tiebreak FIFO.
 *
 * Estruturas usadas:
 * - cacheMap : HashMap<T, Integer>
 * Mapeia cada chave à sua frequência de acesso atual.
 *
 * - freqMap : HashMap<Integer, LinkedHashSet<T>>
 * Mapeia cada frequência ao conjunto de chaves com aquela frequência.
 * LinkedHashSet preserva a ordem de inserção, garantindo o tiebreak FIFO
 * (o primeiro elemento do set é sempre o mais antigo com aquela frequência).
 *
 * - minFrequencia : int
 * Rastreia a menor frequência ativa no cache. Permite evicção em O(1)
 * sem busca linear.
 *
 * Complexidade de todas as operações: O(1) amortizado.
 */
public class LFUCache<T> implements CachePolicy<T> {

    private final int capacity;
    private int hits;
    private int misses;

    // Mapeia chave → frequência atual
    private final Map<T, Integer> cacheMap;

    // Mapeia frequência → conjunto de chaves (ordem de inserção preservada)
    private final Map<Integer, LinkedHashSet<T>> freqMap;

    // Menor frequência ativa no cache (evita busca linear na evicção)
    private int minFrequencia;

    /**
     * Construtor do cache LFU.
     *
     * @param capacity capacidade máxima do cache (deve ser > 0)
     */
    public LFUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }

        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.minFrequencia = 0;
        this.cacheMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    /**
     * Acessa um elemento no cache.
     *
     * HIT → elemento já existe no cache:
     * 1. Incrementa sua frequência em cacheMap.
     * 2. Remove a chave do conjunto da frequência anterior em freqMap.
     * 3. Se o conjunto anterior ficou vazio E era o mínimo, incrementa
     * minFrequencia.
     * 4. Insere a chave no conjunto da nova frequência em freqMap.
     *
     * MISS → elemento não existe no cache:
     * 1. Se o cache estiver cheio, evicta o elemento mais antigo
     * com frequência igual a minFrequencia (primeiro do LinkedHashSet).
     * 2. Insere a nova chave com frequência 1.
     * 3. Define minFrequencia = 1.
     *
     * @param key chave a ser acessada
     * @return true se houve HIT, false se houve MISS
     */
    @Override
    public boolean access(T key) {

        // ── HIT ──────────────────────────────────────────────────────────────
        if (cacheMap.containsKey(key)) {
            hits++;
            incrementarFrequencia(key);
            return true;
        }

        // ── MISS ─────────────────────────────────────────────────────────────
        misses++;

        // Se o cache estiver cheio, remove o menos frequente (mais antigo no empate)
        if (cacheMap.size() >= capacity) {
            evictar();
        }

        // Insere nova chave com frequência 1
        cacheMap.put(key, 1);
        freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        minFrequencia = 1; // novo elemento sempre tem frequência mínima = 1

        return false;
    }

    /**
     * Incrementa a frequência de uma chave já presente no cache.
     *
     * Remove a chave do conjunto da frequência atual e a insere
     * no conjunto da frequência seguinte. Atualiza minFrequencia
     * se necessário.
     *
     * @param key chave cujo contador de frequência será incrementado
     */
    private void incrementarFrequencia(T key) {
        int freqAtual = cacheMap.get(key);
        int freqNova = freqAtual + 1;

        // Atualiza o contador no cacheMap
        cacheMap.put(key, freqNova);

        // Remove do bucket da frequência atual
        LinkedHashSet<T> bucketAtual = freqMap.get(freqAtual);
        bucketAtual.remove(key);

        // Se o bucket ficou vazio: remove do freqMap (evita memory leak)
        // e avança minFrequencia se necessário
        if (bucketAtual.isEmpty()) {
            freqMap.remove(freqAtual);
            if (freqAtual == minFrequencia) {
                minFrequencia = freqNova;
            }
        }

        // Insere no bucket da nova frequência
        freqMap.computeIfAbsent(freqNova, k -> new LinkedHashSet<>()).add(key);
    }

    /**
     * Remove do cache o elemento com menor frequência.
     * Em caso de empate, remove o mais antigo (primeiro inserido
     * no LinkedHashSet daquela frequência — tiebreak FIFO).
     */
    private void evictar() {
        LinkedHashSet<T> bucketMin = freqMap.get(minFrequencia);

        // O iterator de LinkedHashSet retorna na ordem de inserção:
        // o primeiro elemento é o mais antigo com a menor frequência.
        T evicted = bucketMin.iterator().next();
        bucketMin.remove(evicted);
        cacheMap.remove(evicted);
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

    /**
     * Método auxiliar para visualizar o estado interno do cache.
     * Mostra cada chave e sua frequência atual.
     */
    public void printCache() {
        System.out.println("Estado do Cache (LFU):");
        System.out.println("  minFrequencia = " + minFrequencia);
        System.out.println("  elementos     = " + cacheMap);
    }
}