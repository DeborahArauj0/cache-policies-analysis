package br.com.cacheanalysis.simulacao;

import br.com.cacheanalysis.cache.CachePolicy;
import br.com.cacheanalysis.cache.FIFOCache;
import br.com.cacheanalysis.cache.LFUCache;
import br.com.cacheanalysis.cache.LRUCache;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Classe principal para execução dos experimentos.
 * Inclui warm-up da JVM para garantir medições de tempo precisas.
 */
public class Experimentos {

    private static final int[] TOTAL_PACIENTES = { 1000, 10000, 50000 };
    private static final int FATOR_ACESSOS = 5;
    private static final long CUSTO_HIT_MS = 1;
    private static final long CUSTO_MISS_MS = 10;

    public static void main(String[] args) {
        System.out.println("Aquecendo a JVM (Warm-up)...");
        realizarWarmUp();
        System.out.println("Warm-up concluído. Iniciando experimentos rigorosos...\n");

        int[] capacidades = { 10, 50, 100, 500, 1000, 5000, 10000, 50000 };

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_experimentos.csv"))) {
            writer.println("Cenario,BasePacientes,TotalAcessos,Capacidade,Politica,Hits,Misses,AcessosBanco,TempoTotal_ns,LatenciaSimulada_ms");

            for (int totalPacientes : TOTAL_PACIENTES) {
                int totalAcessos = totalPacientes * FATOR_ACESSOS;
                System.out.println("Processando Base: " + totalPacientes + " pacientes...");

                BancoDeDadosSimulado banco = new BancoDeDadosSimulado(totalPacientes);
                List<Integer> acessosA = WorkloadGenerator.gerarCenarioA(totalPacientes, totalAcessos);
                List<Integer> acessosB = WorkloadGenerator.gerarCenarioB(totalPacientes, totalAcessos);
                List<Integer> acessosC = WorkloadGenerator.gerarCenarioC(totalPacientes, totalAcessos);

                for (int capacidade : capacidades) {
                    // Cenário A
                    executarExperimento(new FIFOCache<>(capacidade), acessosA, "A", "FIFO", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosA, "A", "LFU", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosA, "A", "LRU", totalPacientes, totalAcessos, capacidade, writer);

                    // Cenário B 
                    executarExperimento(new FIFOCache<>(capacidade), acessosB, "B", "FIFO", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosB, "B", "LFU", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosB, "B", "LRU", totalPacientes, totalAcessos, capacidade, writer);

                    // Cenário C
                    executarExperimento(new FIFOCache<>(capacidade), acessosC, "C", "FIFO", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosC, "C", "LFU", totalPacientes, totalAcessos, capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosC, "C", "LRU", totalPacientes, totalAcessos, capacidade, writer);
                }
            }
            System.out.println("\n✅ Experimentos finalizados! Resultados salvos no CSV.");

        } catch (IOException e) {
            System.err.println("Erro ao salvar CSV: " + e.getMessage());
        }
    }

    /**
     * Executa milhares de acessos descartáveis para forçar o JIT compiler
     * a otimizar o código em tempo de execução antes da coleta de métricas.
     */
    private static void realizarWarmUp() {
        int iteracoes = 25000;
        CachePolicy<Integer> lru = new LRUCache<>(1000);
        CachePolicy<Integer> lfu = new LFUCache<>(1000);
        CachePolicy<Integer> fifo = new FIFOCache<>(1000);

        for (int i = 0; i < iteracoes; i++) {
            int key = i % 2000;
            lru.access(key);
            lfu.access(key);
            fifo.access(key);
        }
    }

    private static void executarExperimento(
            CachePolicy<Integer> cache, List<Integer> acessos, String cenario,
            String nomePolitica, int basePacientes, int totalAcessos,
            int capacidade, PrintWriter writer) {

        BancoDeDadosSimulado bancoLocal = new BancoDeDadosSimulado(basePacientes);

        long inicio = System.nanoTime();
        for (int id : acessos) {
            if (!cache.access(id)) {
                bancoLocal.buscarPaciente(id);
            }
        }
        long tempoTotal = System.nanoTime() - inicio;

        int hits = cache.getHits();
        int misses = cache.getMisses();
        int acessosBanco = bancoLocal.getTotalBuscas();
        long latenciaSimulada = (hits * CUSTO_HIT_MS) + (misses * CUSTO_MISS_MS);

        writer.printf("%s,%d,%d,%d,%s,%d,%d,%d,%d,%d%n",
                cenario, basePacientes, totalAcessos, capacidade, nomePolitica,
                hits, misses, acessosBanco, tempoTotal, latenciaSimulada);
    }
}