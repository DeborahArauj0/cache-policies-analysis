package br.com.cacheanalysis.simulacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe responsável por gerar carga de trabalho (workloads)
 * utilizadas nos experimentos de simulação de políticas de cache.
 *
 * Cada método representa um cenário distinto de padrão de acesso,
 * para avaliar o comportamento das políticas com diferentes
 * distribuições estatísticas.
 *
 * A geração utiliza uma SEED fixa para garantir reprodutibilidade
 * dos experimentos.
 */
public class WorkloadGenerator {

    /**
     * Seed fixa para a geração pseudoaleatória.
     *
     * Garante que todos os experimentos sejam reproduzíveis,
     * produzindo a mesma sequência de acessos a cada execução.
     */
    private static final int SEED = 42;

    /**
     * Cenário A — Acesso Aleatório Uniforme (Baseline).
     *
     * Todos os pacientes possuem a mesma probabilidade de serem acessados.
     * Distribuição uniforme sem padrão de repetição significativo.
     *
     * Este cenário serve como base de comparação empírica (baseline):
     * sem localidade temporal nem frequência concentrada, nenhuma política
     * se destaca de forma consistente.
     *
     * @param totalPacientes número total de pacientes possíveis
     * @param totalAcessos   número total de acessos a serem gerados
     * @return lista com a sequência simulada de acessos
     */
    public static List<Integer> gerarCenarioA(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        for (int i = 0; i < totalAcessos; i++) {
            int id = random.nextInt(totalPacientes) + 1;
            acessos.add(id);
        }

        return acessos;
    }

    /**
     * Cenário B — Temporalidade / LRU Friendly.
     *
     * Simula pacientes que retornam ao posto em curto intervalo de tempo,
     * criando localidade temporal. Uma janela deslizante rastreia os pacientes
     * recentemente acessados:
     * - 80% de chance de repetir um paciente da janela recente
     * - 20% de chance de acessar um paciente novo (aleatório)
     *
     * Favorece políticas baseadas em recência (LRU).
     *
     * Janela implementada com ArrayList de tamanho fixo (máx. 10 elementos):
     * acesso por índice é O(1) sem alocação. O remove(0) custa no máximo
     * O(10) — custo fixo e irrelevante na prática.
     *
     * @param totalPacientes número total de pacientes possíveis
     * @param totalAcessos   número total de acessos a serem gerados
     * @return lista com a sequência simulada de acessos
     */
    public static List<Integer> gerarCenarioB(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        // ArrayDeque: remoção da cabeça é O(1) (correção do ArrayList.remove(0) que era
        // ArrayList com capacidade inicial fixa: acesso por índice é O(1) sem alocação.
        // A janela tem no máximo 10 elementos, então remove(0) custa no máximo O(10).
        List<Integer> janelaRecente = new ArrayList<>(10);
        int tamanhoJanela = 10;

        for (int i = 0; i < totalAcessos; i++) {
            double probabilidade = random.nextDouble();
            int id;

            if (!janelaRecente.isEmpty() && probabilidade < 0.8) {
                // 80%: repete um paciente recente — O(1) direto, sem criar array
                int idx = random.nextInt(janelaRecente.size());
                id = janelaRecente.get(idx);
            } else {
                // 20%: acessa um paciente novo
                id = random.nextInt(totalPacientes) + 1;
            }

            acessos.add(id);

            // Atualiza janela deslizante
            janelaRecente.add(id);
            if (janelaRecente.size() > tamanhoJanela) {
                janelaRecente.remove(0); // O(10) no máximo — custo fixo e mínimo
            }
        }

        return acessos;
    }

    /**
     * Cenário C — Frequência / LFU Friendly (Regra de Pareto).
     *
     * Aplica um viés estatístico inspirado na Regra de Pareto:
     * 20% dos pacientes (casos crônicos) concentram 70% dos acessos.
     *
     * - 70%: acessa um paciente do grupo "crônico" (primeiros 20% dos IDs)
     * - 30%: acessa qualquer paciente aleatoriamente
     *
     * Favorece políticas baseadas em frequência histórica (LFU).
     *
     * @param totalPacientes número total de pacientes possíveis
     * @param totalAcessos   número total de acessos a serem gerados
     * @return lista com a sequência simulada de acessos
     */
    public static List<Integer> gerarCenarioC(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        for (int i = 0; i < totalAcessos; i++) {
            double probabilidade = random.nextDouble();
            int id;

            if (probabilidade < 0.7) {
                // 70% dos acessos vão para os 20% mais frequentes (pacientes crônicos)
                id = random.nextInt(totalPacientes / 5) + 1;
            } else {
                // 30% restantes acessam qualquer paciente
                id = random.nextInt(totalPacientes) + 1;
            }

            acessos.add(id);
        }

        return acessos;
    }
}
