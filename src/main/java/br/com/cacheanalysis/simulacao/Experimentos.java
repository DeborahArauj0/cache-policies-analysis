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
 * Classe principal — executa os experimentos de simulação das três políticas de
 * cache.
 *
 * Para cada combinação de (cenário × base de pacientes × capacidade de cache),
 * as três políticas (FIFO, LFU, LRU) são testadas com a mesma sequência de
 * acessos.
 *
 * Métricas coletadas por experimento:
 * - Hits / Misses
 * - Acessos ao banco (contabilizados pelo BancoDeDadosSimulado via misses
 * reais)
 * - Tempo de CPU puro em nanossegundos (System.nanoTime, sem Thread.sleep)
 * - Latência simulada em milissegundos (hits × 1ms + misses × 10ms)
 *
 * O BancoDeDadosSimulado é instanciado sem latência (modo rápido) para não
 * distorcer o TempoTotal_ns. A latência de I/O é calculada matematicamente
 * ao final de cada experimento.
 *
 * Os resultados são gravados em "resultados_experimentos.csv".
 */
public class Experimentos {

        // Tamanhos da base de pacientes testados
        private static final int[] TOTAL_PACIENTES = { 1000, 10000, 50000 };

        // Total de acessos = base × fator
        private static final int FATOR_ACESSOS = 5;

        // Custo de I/O simulado (em milissegundos)
        private static final long CUSTO_HIT_MS = 1; // cache em memória
        private static final long CUSTO_MISS_MS = 10; // acesso ao banco em disco

        public static void main(String[] args) {

                int[] capacidades = { 10, 50, 100, 500, 1000, 5000, 10000, 50000 };

                try (PrintWriter writer = new PrintWriter(new FileWriter("resultados_experimentos.csv"))) {

                        // Cabeçalho do CSV com todas as métricas
                        writer.println("Cenario,BasePacientes,TotalAcessos,Capacidade,Politica," +
                                        "Hits,Misses,AcessosBanco,TempoTotal_ns,LatenciaSimulada_ms");

                        for (int totalPacientes : TOTAL_PACIENTES) {

                                int totalAcessos = totalPacientes * FATOR_ACESSOS;

                                System.out.println("\n==================================");
                                System.out.println("Base de Pacientes: " + totalPacientes);
                                System.out.println("Total de Acessos:  " + totalAcessos);
                                System.out.println("==================================");

                                // Banco instanciado sem latência: não distorce o TempoTotal_ns
                                BancoDeDadosSimulado banco = new BancoDeDadosSimulado(totalPacientes);

                                // Gera as sequências de acesso uma única vez por base
                                List<Integer> acessosA = WorkloadGenerator.gerarCenarioA(totalPacientes, totalAcessos);
                                List<Integer> acessosB = WorkloadGenerator.gerarCenarioB(totalPacientes, totalAcessos);
                                List<Integer> acessosC = WorkloadGenerator.gerarCenarioC(totalPacientes, totalAcessos);

                                for (int capacidade : capacidades) {

                                        System.out.println("\n=== Capacidade: " + capacidade + " ===");

                                        // Cenário A
                                        executarExperimento(new FIFOCache<>(capacidade), acessosA, "A", "FIFO",
                                                        totalPacientes,
                                                        totalAcessos, capacidade, banco, writer);
                                        executarExperimento(new LFUCache<>(capacidade), acessosA, "A", "LFU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);
                                        executarExperimento(new LRUCache<>(capacidade), acessosA, "A", "LRU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);

                                        // Cenário B
                                        executarExperimento(new FIFOCache<>(capacidade), acessosB, "B", "FIFO",
                                                        totalPacientes,
                                                        totalAcessos, capacidade, banco, writer);
                                        executarExperimento(new LFUCache<>(capacidade), acessosB, "B", "LFU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);
                                        executarExperimento(new LRUCache<>(capacidade), acessosB, "B", "LRU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);

                                        // Cenário C
                                        executarExperimento(new FIFOCache<>(capacidade), acessosC, "C", "FIFO",
                                                        totalPacientes,
                                                        totalAcessos, capacidade, banco, writer);
                                        executarExperimento(new LFUCache<>(capacidade), acessosC, "C", "LFU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);
                                        executarExperimento(new LRUCache<>(capacidade), acessosC, "C", "LRU",
                                                        totalPacientes, totalAcessos,
                                                        capacidade, banco, writer);
                                }
                        }

                        System.out.println(
                                        "\n✅ Experimentos finalizados! Resultados salvos em 'resultados_experimentos.csv'");

                } catch (IOException e) {
                        System.err.println("Erro ao salvar CSV: " + e.getMessage());
                }
        }

        /**
         * Executa um único experimento: roda todos os acessos na política informada,
         * consulta o banco nos misses, mede o tempo de CPU puro e calcula a latência
         * simulada separadamente.
         *
         * O banco é instanciado sem latência (comLatencia=false), portanto o
         * Thread.sleep não interfere no TempoTotal_ns.
         *
         * @param cache         instância da política a ser testada
         * @param acessos       sequência de IDs a acessar
         * @param cenario       identificador do cenário (A, B ou C)
         * @param nomePolitica  nome da política (FIFO, LFU ou LRU)
         * @param basePacientes tamanho da base de pacientes
         * @param totalAcessos  total de acessos simulados
         * @param capacidade    capacidade do cache
         * @param banco         banco de dados simulado (contabiliza acessos reais)
         * @param writer        escritor do arquivo CSV
         */
        private static void executarExperimento(
                        CachePolicy<Integer> cache,
                        List<Integer> acessos,
                        String cenario,
                        String nomePolitica,
                        int basePacientes,
                        int totalAcessos,
                        int capacidade,
                        BancoDeDadosSimulado banco,
                        PrintWriter writer) {

                // Reinicia o contador do banco para este experimento
                BancoDeDadosSimulado bancoLocal = new BancoDeDadosSimulado(basePacientes, false);

                // Mede apenas o tempo das estruturas de dados (banco sem Thread.sleep)
                long inicio = System.nanoTime();
                for (int id : acessos) {
                        boolean hit = cache.access(id);
                        if (!hit) {
                                bancoLocal.buscarPaciente(id); // miss → consulta o banco
                        }
                }
                long tempoTotal = System.nanoTime() - inicio;

                int hits = cache.getHits();
                int misses = cache.getMisses();
                int acessosBanco = bancoLocal.getTotalBuscas(); // vem do banco, não de misses

                // Latência simulada: custo matemático de I/O por hit e por miss
                long latenciaSimulada = (hits * CUSTO_HIT_MS) + (misses * CUSTO_MISS_MS);

                System.out.printf(
                                "Política: %-4s | Hits: %6d | Misses: %6d | BancoCalls: %6d | Latência: %,d ms | CPU: %,d ns%n",
                                nomePolitica, hits, misses, acessosBanco, latenciaSimulada, tempoTotal);

                writer.printf("%s,%d,%d,%d,%s,%d,%d,%d,%d,%d%n",
                                cenario, basePacientes, totalAcessos, capacidade, nomePolitica,
                                hits, misses, acessosBanco, tempoTotal, latenciaSimulada);
        }
}
