package br.com.cacheanalysis.testes;

import br.com.cacheanalysis.cache.FIFOCache;
import br.com.cacheanalysis.cache.LFUCache;
import br.com.cacheanalysis.cache.LRUCache;

/**
 * Suíte de testes unitários para validação lógica das políticas de cache.
 * Assegura que as regras de evicção de cada algoritmo (FIFO, LRU, LFU)
 * estejam operando estritamente de acordo com suas definições teóricas.
 */
public class TesteEstruturas {

    public static void main(String[] args) {
        System.out.println("Iniciando execução da suíte de testes unitários...\n");

        try {
            testarFIFO();
            System.out.println("✅ FIFOCache: Comportamento de evicção validado com sucesso.");

            testarLRU();
            System.out.println("✅ LRUCache: Comportamento de evicção validado com sucesso.");

            testarLFU();
            System.out.println("✅ LFUCache: Comportamento de evicção validado com sucesso.");

            System.out.println("\n✅ Todos os testes unitários foram concluídos sem erros. As estruturas operam conforme as especificações.");

        } catch (AssertionError e) {
            System.err.println("\n❌ Falha de asserção durante a execução dos testes: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void testarFIFO() {
        FIFOCache<Integer> fifo = new FIFOCache<>(3);
        fifo.access(1); 
        fifo.access(2); 
        fifo.access(3); 
        
        fifo.access(4); // Provoca a evicção do elemento 1. Estado esperado: [2, 3, 4]

        // Validação de Hits (elementos que devem permanecer)
        assertEquals(true, fifo.access(2), "FIFOCache: O elemento 2 deveria estar presente (Hit esperado).");
        assertEquals(true, fifo.access(3), "FIFOCache: O elemento 3 deveria estar presente (Hit esperado).");
        assertEquals(true, fifo.access(4), "FIFOCache: O elemento 4 deveria estar presente (Hit esperado).");
        
        // Validação de Miss (elemento evictado)
        assertEquals(false, fifo.access(1), "FIFOCache: O elemento 1 deveria ter sido evictado (Miss esperado).");
    }

    private static void testarLRU() {
        LRUCache<Integer> lru = new LRUCache<>(3);
        lru.access(1); 
        lru.access(2); 
        lru.access(3); 
        
        lru.access(1); // Atualiza o elemento 1 como o mais recentemente usado
        
        lru.access(4); // Provoca a evicção do elemento 2. Estado esperado: [3, 1, 4]

        // Validação de Hits
        assertEquals(true, lru.access(1), "LRUCache: O elemento 1 foi salvo pela localidade temporal e deveria estar presente.");
        assertEquals(true, lru.access(3), "LRUCache: O elemento 3 deveria estar presente (Hit esperado).");
        assertEquals(true, lru.access(4), "LRUCache: O elemento 4 deveria estar presente (Hit esperado).");
        
        // Validação de Miss
        assertEquals(false, lru.access(2), "LRUCache: O elemento 2 deveria ter sido evictado por ser o menos recentemente usado.");
    }

    private static void testarLFU() {
        LFUCache<Integer> lfu = new LFUCache<>(3);
        lfu.access(1);
        lfu.access(1); // Frequência do elemento 1 passa a ser 2
        lfu.access(2); // Frequência do elemento 2 é 1
        lfu.access(3); // Frequência do elemento 3 é 1
        
        lfu.access(4); // Provoca a evicção. Empate entre 2 e 3 (freq 1). O 2 é mais antigo, logo deve sair.

        // Validação de Hits
        assertEquals(true, lfu.access(3), "LFUCache: O elemento 3 deveria estar presente.");
        assertEquals(true, lfu.access(1), "LFUCache: O elemento 1 (maior frequência) não deveria ter sido evictado.");
        assertEquals(true, lfu.access(4), "LFUCache: O elemento 4 deveria estar presente.");
        
        // Validação de Miss
        assertEquals(false, lfu.access(2), "LFUCache: O elemento 2 deveria ter sido evictado devido ao critério de desempate temporal (FIFO).");
    }

    private static void assertEquals(boolean esperado, boolean obtido, String mensagemErro) {
        if (esperado != obtido) {
            throw new AssertionError(mensagemErro + " (Esperado: " + esperado + ", Obtido: " + obtido + ")");
        }
    }
}