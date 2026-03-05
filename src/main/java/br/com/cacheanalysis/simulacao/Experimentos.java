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
 * Classe responsável por executar os experimentos de simulação das
 * políticas de cache.
 */
public class Experimentos {

    // escala de base de pacientes
    private static final int[] TOTAL_PACIENTES = {1000, 10000, 50000};

    private static final int FATOR_ACESSOS = 5;

    private static int BASE_ATUAL;
    private static int ACESSOS_ATUAL;

    private static String CENARIO_ATUAL;

    public static void main(String[] args) {

        // Capacidades testadas
        int[] capacidades = {10, 50, 100, 500, 1000, 5000, 10000, 50000};

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_experimentos.csv"))) {

            writer.println("Cenario,BasePacientes,TotalAcessos,Capacidade,Politica,Hits,Misses,AcessosBanco,TempoTotal_ns");

            // Loop variando o tamanho da base
            for (int totalPacientes : TOTAL_PACIENTES) {

                BASE_ATUAL = totalPacientes;
                ACESSOS_ATUAL = totalPacientes * FATOR_ACESSOS;

                System.out.println("\n==================================");
                System.out.println("Base de Pacientes: " + BASE_ATUAL);
                System.out.println("Total de Acessos: " + ACESSOS_ATUAL);
                System.out.println("==================================");

                /*
                 * CENÁRIO A
                 */
                CENARIO_ATUAL = "A";

                List<Integer> acessosA =
                        WorkloadGenerator.gerarCenarioA(BASE_ATUAL, ACESSOS_ATUAL);

                for (int capacidade : capacidades) {

                    System.out.println("\n=== Capacidade: " + capacidade + " ===");

                    executarExperimento(new FIFOCache<>(capacidade), acessosA, "FIFO", capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosA, "LFU", capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosA, "LRU", capacidade, writer);
                }

                /*
                 * CENÁRIO B
                 */
                CENARIO_ATUAL = "B";

                List<Integer> acessosB =
                        WorkloadGenerator.gerarCenarioB(BASE_ATUAL, ACESSOS_ATUAL);

                for (int capacidade : capacidades) {

                    System.out.println("\n=== Capacidade: " + capacidade + " ===");

                    executarExperimento(new FIFOCache<>(capacidade), acessosB, "FIFO", capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosB, "LFU", capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosB, "LRU", capacidade, writer);
                }

                /*
                 * CENÁRIO C
                 */
                CENARIO_ATUAL = "C";

                List<Integer> acessosC =
                        WorkloadGenerator.gerarCenarioC(BASE_ATUAL, ACESSOS_ATUAL);

                for (int capacidade : capacidades) {

                    System.out.println("\n=== Capacidade: " + capacidade + " ===");

                    executarExperimento(new FIFOCache<>(capacidade), acessosC, "FIFO", capacidade, writer);
                    executarExperimento(new LFUCache<>(capacidade), acessosC, "LFU", capacidade, writer);
                    executarExperimento(new LRUCache<>(capacidade), acessosC, "LRU", capacidade, writer);
                }
            }

            System.out.println("\n✅ Experimentos finalizados! Resultados salvos em 'resultados_experimentos.csv'");

        } catch (IOException e) {
            System.err.println("Erro ao salvar CSV: " + e.getMessage());
        }
    }

    private static void executarExperimento(
            CachePolicy<Integer> cache,
            List<Integer> acessos,
            String nomePolitica,
            int capacidade,
            PrintWriter writer
    ) {

        BancoDeDadosSimulado banco = new BancoDeDadosSimulado(BASE_ATUAL);

        long inicio = System.nanoTime();

        for (int id : acessos) {
            if (!cache.access(id)) {
                banco.buscarPaciente(id);
            }
        }

        long fim = System.nanoTime();
        long tempoTotal = fim - inicio;

        System.out.println("Política: " + nomePolitica);
        System.out.println("Hits: " + cache.getHits());
        System.out.println("Misses: " + cache.getMisses());
        System.out.println("Acessos ao banco: " + banco.getTotalBuscas());
        System.out.println("Tempo total (ns): " + tempoTotal);
        System.out.println("-----------------------------------");

        writer.printf("%s,%d,%d,%d,%s,%d,%d,%d,%d%n",
                CENARIO_ATUAL,
                BASE_ATUAL,
                ACESSOS_ATUAL,
                capacidade,
                nomePolitica,
                cache.getHits(),
                cache.getMisses(),
                banco.getTotalBuscas(),
                tempoTotal
        );
    }
}
