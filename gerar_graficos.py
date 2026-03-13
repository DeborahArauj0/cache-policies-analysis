import os
import pandas as pd
import matplotlib.pyplot as plt

def gerar_graficos():
    # Garante que a pasta 'graficos' exista antes de tentar salvar os arquivos
    os.makedirs('graficos', exist_ok=True)

    # 1. Carregar os dados reais gerados pelo seu programa Java
    df = pd.read_csv('resultados_experimentos.csv')
    
    # Filtrar para a base de 50.000 pacientes para focar no cenário de estresse
    df = df[df['BasePacientes'] == 50000].copy()
    
    # 2. Calcular as métricas derivadas
    df['TempoPorAcesso_ns'] = df['TempoTotal_ns'] / df['TotalAcessos']
    df['HitRate_pct'] = (df['Hits'] / df['TotalAcessos']) * 100

    cores = {'FIFO': '#f0ad4e', 'LRU': '#d9534f', 'LFU': '#5cb85c'}

    # =========================================================
    # GRÁFICO 1: Comprovação do Tempo O(1)
    # =========================================================
    plt.clf()
    df_a = df[df['Cenario'] == 'A']
    for pol in ['FIFO', 'LRU', 'LFU']:
        subset = df_a[df_a['Politica'] == pol]
        plt.plot(subset['Capacidade'].astype(str), subset['TempoPorAcesso_ns'], 
                 marker='o', label=pol, color=cores[pol], linewidth=2.5)

    plt.title('Comprovação O(1): Tempo de CPU por Acesso (Base 50k)')
    plt.xlabel('Capacidade do Cache')
    plt.ylabel('Tempo por Acesso (nanossegundos)')
    plt.legend()
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.tight_layout()
    # Caminho atualizado
    plt.savefig('graficos/grafico_tempo_cpu.png')

    # =========================================================
    # GRÁFICOS 2, 3 e 4: Hit Rates nos Cenários A, B e C
    # =========================================================
    cenarios = [
        ('A', 'Uniforme (Sem Padrão)'),
        ('B', 'Temporal (LRU-Friendly)'),
        ('C', 'Frequência / Pareto (LFU-Friendly)')
    ]

    for cenario, desc in cenarios:
        plt.clf()
        df_cen = df[df['Cenario'] == cenario]
        for pol in ['FIFO', 'LRU', 'LFU']:
            subset = df_cen[df_cen['Politica'] == pol]
            plt.plot(subset['Capacidade'].astype(str), subset['HitRate_pct'], 
                     marker='s', label=pol, color=cores[pol], linewidth=2)
        
        plt.title(f'Taxa de Acertos - Cenário {cenario}\n{desc}')
        plt.xlabel('Capacidade do Cache')
        plt.ylabel('Hit Rate (%)')
        plt.ylim(0, 105)
        plt.legend()
        plt.grid(True, linestyle='--', alpha=0.6)
        plt.tight_layout()
        plt.savefig(f'graficos/grafico_hit_rate_cenario_{cenario.lower()}.png')
        
    print("Sucesso: 4 gráficos PNG gerados na pasta 'graficos'!")

if __name__ == '__main__':
    gerar_graficos()