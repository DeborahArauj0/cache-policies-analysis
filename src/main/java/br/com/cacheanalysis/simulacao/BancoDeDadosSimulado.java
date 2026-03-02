package br.com.cacheanalysis.simulacao;

import java.util.HashMap;
import java.util.Map;

public class BancoDeDadosSimulado {
    //Armazena os pacientes, com id e dados deles
    private Map<Integer, String> pacientes;
    private int totalBuscas;

    public BancoDeDadosSimulado(int totalPacientes) {
        pacientes = new HashMap<>();
        totalBuscas = 0;

        // Popula banco com registros fictícios
        for (int i = 1; i <= totalPacientes; i++) {
            pacientes.put(i, "Historico do paciente " + i);
        }
    }

    public String buscarPaciente(int id) {
        totalBuscas++;

        // Simula custo de acesso ao banco
        try {
            Thread.sleep(1); // 1ms artificial
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return pacientes.get(id);
    }

    public int getTotalBuscas() {
        return totalBuscas;
    }
}
