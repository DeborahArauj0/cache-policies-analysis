package br.com.cacheanalysis.simulacao;

import br.com.cacheanalysis.cache.CachePolicy;
import br.com.cacheanalysis.cache.FIFOCache;
import br.com.cacheanalysis.cache.LFUCache;
import br.com.cacheanalysis.cache.LRUCache;

import java.util.List;

public class Experimentos {

    private static final int TOTAL_PACIENTES = 100;  // começar pequeno
    private static final int TOTAL_ACESSOS = 500;    // depois escalar

    public static void main(String[] args) {

        // Gera carga UMA vez para que todas as políticas sejam testadas sob o mesmo cenário
        List<Integer> acessos = 
                WorkloadGenerator.gerarCenarioC(TOTAL_PACIENTES, TOTAL_ACESSOS);

        int[] capacidades = {10, 20, 50};

        for (int capacidade : capacidades) {

            System.out.println("\n=== Capacidade: " + capacidade + " ===");

            // Executa o experimento para a política FIFO
            executarExperimento(
                    new FIFOCache<>(capacidade),
                    acessos,
                    "FIFO"
            );

            // Executa o experimento para a política LFU
            executarExperimento(
                    new LFUCache<>(capacidade),
                    acessos,
                    "LFU"
            );

            // Executa o experimento para a política LRU
            executarExperimento(
                    new LRUCache<>(capacidade),
                    acessos,
                    "LRU"
            );
        }
    }

    private static void executarExperimento(
            CachePolicy<Integer> cache,
            List<Integer> acessos,
            String nomePolitica
    ) {

        BancoDeDadosSimulado banco = new BancoDeDadosSimulado(TOTAL_PACIENTES);

        long inicio = System.nanoTime();

        // Processa os acessos simulando a busca em cache vs. busca no banco
        for (int id : acessos) {
            if (!cache.access(id)) {
                banco.buscarPaciente(id);
            }
        }

        long fim = System.nanoTime();
        long tempoTotal = fim - inicio;

        // Impressão dos resultados e métricas de desempenho
        System.out.println("Política: " + nomePolitica);
        System.out.println("Hits: " + cache.getHits());
        System.out.println("Misses: " + cache.getMisses());
        System.out.println("Acessos ao banco: " + banco.getTotalBuscas());
        System.out.println("Tempo total (ns): " + tempoTotal);
        System.out.println("-----------------------------------");
    }
}
