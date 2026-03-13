package br.com.cacheanalysis.simulacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gera cargas de trabalho (workloads) para os experimentos de análise de cache.
 *
 * Cada método representa um cenário diferente de acesso a registros de pacientes:
 *   - Cenário A: acessos completamente aleatórios (sem padrão)
 *   - Cenário B: localidade temporal — pacientes recentes têm maior chance de ser acessados novamente
 *   - Cenário C: concentração em pacientes crônicos — 70% dos acessos em 20% dos pacientes
 *
 * A SEED fixa (42) garante que toda execução produza a mesma sequência aleatória,
 * permitindo reprodutibilidade rigorosa nos experimentos.
 */
public class WorkloadGenerator {

    // Semente fixa usada em todos os geradores para garantir resultados reproduzíveis
    private static final int SEED = 42;

    /**
     * Cenário A — Acesso aleatório uniforme.
     *
     * Gera uma sequência de IDs de pacientes sem nenhum padrão de repetição.
     * Cada acesso sorteia qualquer paciente com igual probabilidade.
     * Representa o pior caso para caches, pois não há localidade de referência.
     *
     * @param totalPacientes número total de pacientes disponíveis
     * @param totalAcessos   quantidade de acessos a gerar
     * @return lista de IDs de pacientes na ordem de acesso
     */
    public static List<Integer> gerarCenarioA(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>(totalAcessos);

        // Gera 'totalAcessos' IDs aleatórios no intervalo [1, totalPacientes]
        for (int i = 0; i < totalAcessos; i++) {
            acessos.add(random.nextInt(totalPacientes) + 1);
        }

        return acessos;
    }

    /**
     * Cenário B — Localidade temporal com janela deslizante.
     *
     * Simula o comportamento real de sistemas hospitalares onde pacientes
     * atendidos recentemente tendem a ser acessados novamente em breve.
     *
     * Regra de acesso:
     *   - 80% de chance: reutiliza um paciente já presente na janela dos 10 mais recentes
     *   - 20% de chance: acessa um paciente novo qualquer
     *
     * A janela deslizante é implementada como um buffer circular de tamanho 10,
     * garantindo inserção e leitura em O(1), sem realocações ou deslocamentos de memória.
     *
     * @param totalPacientes número total de pacientes disponíveis
     * @param totalAcessos   quantidade de acessos a gerar
     * @return lista de IDs de pacientes na ordem de acesso
     */
    public static List<Integer> gerarCenarioB(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>(totalAcessos);

        // Buffer circular para janela deslizante: garante acesso e substituição em O(1) puro
        int[] janela = new int[10];  // armazena os 10 pacientes acessados mais recentemente
        int tamanhoJanela = 0;       // quantos slots da janela já foram preenchidos
        int indiceInsercao = 0;      // ponteiro que indica onde o próximo ID será inserido

        for (int i = 0; i < totalAcessos; i++) {
            int id;

            if (tamanhoJanela > 0 && random.nextDouble() < 0.8) {
                // 80%: escolhe paciente da janela em O(1)
                int idxAleatorio = random.nextInt(tamanhoJanela);
                id = janela[idxAleatorio];
            } else {
                // 20%: paciente novo aleatório fora da janela
                id = random.nextInt(totalPacientes) + 1;
            }

            acessos.add(id);

            // Atualiza janela circular em O(1) sem custo de shift ou alocação
            if (tamanhoJanela < 10) {
                // Janela ainda não está cheia: preenche os slots sequencialmente
                janela[tamanhoJanela] = id;
                tamanhoJanela++;
            } else {
                // Janela cheia: sobrescreve o elemento mais antigo (comportamento FIFO)
                janela[indiceInsercao] = id;
                indiceInsercao = (indiceInsercao + 1) % 10; // avança o ponteiro circularmente
            }
        }

        return acessos;
    }

    /**
     * Cenário C — Concentração em pacientes crônicos (distribuição de Pareto).
     *
     * Simula hospitais onde uma minoria de pacientes crônicos concentra
     * a maior parte dos atendimentos — padrão conhecido como regra 80/20.
     *
     * Os pacientes crônicos correspondem aos primeiros 20% dos IDs (corteCronicos).
     *
     * Regra de acesso:
     *   - 70% de chance: acessa um paciente crônico (primeiros 20% dos IDs)
     *   - 30% de chance: acessa qualquer paciente aleatoriamente
     *
     * É o cenário mais favorável para caches, pois um pequeno conjunto de
     * registros responde pela maioria dos acessos.
     *
     * @param totalPacientes número total de pacientes disponíveis
     * @param totalAcessos   quantidade de acessos a gerar
     * @return lista de IDs de pacientes na ordem de acesso
     */
    public static List<Integer> gerarCenarioC(int totalPacientes, int totalAcessos) {
        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>(totalAcessos);

        // Define o limite superior dos IDs considerados "crônicos" (20% do total, mínimo 1)
        int corteCronicos = Math.max(1, totalPacientes / 5);

        for (int i = 0; i < totalAcessos; i++) {
            int id;

            if (random.nextDouble() < 0.7) {
                // 70% dos acessos nos 20% primeiros IDs (pacientes crônicos)
                id = random.nextInt(corteCronicos) + 1;
            } else {
                // 30%: qualquer paciente no universo completo
                id = random.nextInt(totalPacientes) + 1;
            }

            acessos.add(id);
        }

        return acessos;
    }
}
