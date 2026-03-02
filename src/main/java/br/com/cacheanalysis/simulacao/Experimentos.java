package br.com.cacheanalysis.simulacao;

import br.com.cacheanalysis.cache.FIFOCache;
import br.com.cacheanalysis.cache.CachePolicy;
// importar LFU quando estiver pronto

import java.util.List;

public class Experimentos {

    private static final int TOTAL_PACIENTES = 100;  // começar pequeno
    private static final int TOTAL_ACESSOS = 500;    // depois escalar

    public static void main(String[] args) {

        // Gera carga UMA vez
        List<Integer> acessos =
                WorkloadGenerator.gerarCenarioC(TOTAL_PACIENTES, TOTAL_ACESSOS);

        int[] capacidades = {10, 20, 50};

        for (int capacidade : capacidades) {

            System.out.println("\n=== Capacidade: " + capacidade + " ===");

            executarExperimento(
                    new FIFOCache<>(capacidade),
                    acessos,
                    "FIFO"
            );

            // Quando LFU estiver pronto:
            // executarExperimento(new LFUCache<>(capacidade), acessos, "LFU");
        }
    }

    private static void executarExperimento(
            CachePolicy<Integer> cache,
            List<Integer> acessos,
            String nomePolitica
    ) {

        BancoDeDadosSimulado banco =
                new BancoDeDadosSimulado(TOTAL_PACIENTES);

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
    }
}
