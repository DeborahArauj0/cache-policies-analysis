package br.com.cacheanalysis.simulacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Classe responsável por gerar carga de trabalho (workloads)
 * utilizadas nos experimentos de simulação de políticas de cache.
 * 
 * Cada método representa um cenário distinto de padrão de acesso,
 * para avaliar o comportamento das politicas com diferentes 
 * distribuições estatísticas
 * 
 * A geração utiliza uma SEED fixa para garantir reprodutividade
 * dos esperimentosch   
 */
public class WorkloadGenerator {

    /**
     * Seed fixa para a geração pseudoaleatória
     * 
     * Uma maneira de garantir que todos os espetimentos sejam reproduzíveis,
     * produzindo a mesma sequência de acessos a cada execução.
     */
    private static final int SEED = 42;

    /**
     * Cenário A - Acesso Aleatório Uniforme.
     * 
     * Neste cenário, todos os passientes possuem a mesma porbabilidade de
     * serem acessados. Distribuição uniforme.
     * 
     * Esse cenário favorece políticas como FIFO,
     * pois não há padrão de repetição significativo
     * 
     * 
     * @param totalPacientes número total máximo de pacientes possíveis
     * @param totalAcessos número total de acessos a serem gerados 
     * @return lista que contém a sequência simulada de acessos
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

    // Cenário B - Temporalidade (LRU Friendly)
    public static List<Integer> gerarCenarioB(int totalPacientes, int totalAcessos) {

        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        // Janela de pacientes recentemente acessados
        List<Integer> janelaRecente = new ArrayList<>();

        int tamanhoJanela = 10; // controla o grau de temporalidade

        for (int i = 0; i < totalAcessos; i++) {

            double probabilidade = random.nextDouble();
            int id;

            if (!janelaRecente.isEmpty() && probabilidade < 0.8) {
                // 80% de chance de repetir pacientes recentes
                id = janelaRecente.get(random.nextInt(janelaRecente.size()));
            } else {
                // 20% de chance de acessar um paciente novo
                id = random.nextInt(totalPacientes) + 1;
            }

            acessos.add(id);

            // Atualiza janela recente
            janelaRecente.add(id);

            if (janelaRecente.size() > tamanhoJanela) {
                janelaRecente.remove(0);
            }
        }

        return acessos;
    }


    // Cenário C - Frequência (LFU Friendly)
    public static List<Integer> gerarCenarioC(int totalPacientes, int totalAcessos) {

        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        for (int i = 0; i < totalAcessos; i++) {

            double probabilidade = random.nextDouble();

            if (probabilidade < 0.7) {
                // 20% dos pacientes concentram 70% dos acessos
                int id = random.nextInt(totalPacientes / 5) + 1;
                acessos.add(id);
            } else {
                int id = random.nextInt(totalPacientes) + 1;
                acessos.add(id);
            }
        }

        return acessos;
    }
}
