package br.com.cacheanalysis.simulacao;

import br.com.cacheanalysis.cache.CachePolicy;
import br.com.cacheanalysis.cache.FIFOCache;
import br.com.cacheanalysis.cache.LFUCache;
import br.com.cacheanalysis.cache.LRUCache;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Experimentos {

    private static final int TOTAL_PACIENTES = 100;
    private static final int TOTAL_ACESSOS = 500;

    public static void main(String[] args) {

        List<Integer> acessos =
                WorkloadGenerator.gerarCenarioC(TOTAL_PACIENTES, TOTAL_ACESSOS);

        int[] capacidades = {10, 20, 50};

        try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_experimentos.csv"))) {

            writer.println("Capacidade,Politica,Hits,Misses,AcessosBanco,TempoTotal_ns");

            for (int capacidade : capacidades) {

                System.out.println("\n=== Capacidade: " + capacidade + " ===");

                executarExperimento(new FIFOCache<>(capacidade), acessos, "FIFO", capacidade, writer);
                executarExperimento(new LFUCache<>(capacidade), acessos, "LFU", capacidade, writer);
                executarExperimento(new LRUCache<>(capacidade), acessos, "LRU", capacidade, writer);
            }

            System.out.println("\n✅ Experimentos finalizados! Os resultados foram salvos no arquivo 'resultados_experimentos.csv'");

        } catch (IOException e) {
            System.err.println("Erro ao tentar salvar o arquivo CSV: " + e.getMessage());
        }
    }

    private static void executarExperimento(
            CachePolicy<Integer> cache,
            List<Integer> acessos,
            String nomePolitica,
            int capacidade,
            PrintWriter writer
    ) {

        BancoDeDadosSimulado banco = new BancoDeDadosSimulado(TOTAL_PACIENTES);

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

        writer.printf("%d,%s,%d,%d,%d,%d%n",
                capacidade,
                nomePolitica,
                cache.getHits(),
                cache.getMisses(),
                banco.getTotalBuscas(),
                tempoTotal
        );
    }
}
