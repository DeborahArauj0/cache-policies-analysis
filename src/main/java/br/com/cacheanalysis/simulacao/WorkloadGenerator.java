package br.com.cacheanalysis.simulacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkloadGenerator {

    private static final int SEED = 42;

    // Cenário A - Acesso Aleatório Uniforme
    public static List<Integer> gerarCenarioA(int totalPacientes, int totalAcessos) {

        Random random = new Random(SEED);
        List<Integer> acessos = new ArrayList<>();

        for (int i = 0; i < totalAcessos; i++) {
            int id = random.nextInt(totalPacientes) + 1;
            acessos.add(id);
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
