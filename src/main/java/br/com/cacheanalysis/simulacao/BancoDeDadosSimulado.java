package br.com.cacheanalysis.simulacao;

import java.util.HashMap;
import java.util.Map;

/**
 * Simula um banco de dados de prontuários de pacientes.
 *
 * Oferece dois modos de operação:
 * - Com latência artificial (Thread.sleep): para demonstração realista em
 * pequena escala.
 * - Sem latência (modo rápido): para medir o tempo real de CPU das estruturas
 * de dados
 * em experimentos de larga escala, onde o sleep distorceria a medição.
 *
 * A latência de I/O em experimentos de grande escala é calculada
 * matematicamente
 * ao final da simulação (misses * custo por miss).
 */
public class BancoDeDadosSimulado {

    /** Custo simulado de cada acesso ao banco, em milissegundos */
    public static final long CUSTO_MS_POR_ACESSO = 10;

    private final Map<Integer, String> pacientes;
    private int totalBuscas;
    private final boolean comLatencia;

    /**
     * Cria o banco de dados simulado.
     *
     * @param totalPacientes número de registros a popular
     * @param comLatencia    se {@code true}, aplica Thread.sleep(1) por acesso
     */
    public BancoDeDadosSimulado(int totalPacientes, boolean comLatencia) {
        this.pacientes = new HashMap<>();
        this.totalBuscas = 0;
        this.comLatencia = comLatencia;

        for (int i = 1; i <= totalPacientes; i++) {
            pacientes.put(i, "Historico do paciente " + i);
        }
    }

    /**
     * Construtor padrão sem latência (recomendado para experimentos de larga
     * escala).
     */
    public BancoDeDadosSimulado(int totalPacientes) {
        this(totalPacientes, false);
    }

    /**
     * Busca um paciente pelo ID, simulando o custo de I/O em disco.
     *
     * @param id identificador do paciente
     * @return string com o histórico do paciente, ou {@code null} se não encontrado
     */
    public String buscarPaciente(int id) {
        totalBuscas++;

        if (comLatencia) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return pacientes.get(id);
    }

    public int getTotalBuscas() {
        return totalBuscas;
    }
}