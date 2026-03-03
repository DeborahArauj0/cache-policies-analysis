# cache-policies-analysis

Projeto EDA/LEDA:
Comparação De Diferentes Políticas De Cache (FIFO E LFU)

Integrantes:
Bruna Rocha Cavalcanti – Matrícula: 122210838
Deborah dos Santos Araujo – Matrícula: 20250030337
Mikael Renan de Oliveira – Matrícula: 123210843
Teones Alex Lira de Farias Filho – Matrícula: 122210756

1. Objetivo
Este projeto tem como objetivo analisar e comparar políticas de substituição de cache, medindo:
- Número de hits
- Número de misses
- Quantidade de acessos ao banco
- Tempo total de execução
O sistema simula acessos a um banco de dados de pacientes e utiliza diferentes estratégias de cache, como FIFO e LFU, para reduzir o custo desses acessos.

2. Estrutura do Projeto
  cache/
   ├── CachePolicy.java      → Interface base das políticas
   ├── FIFOCache.java        → Implementação FIFO
  
  simulacao/
   ├── BancoDeDadosSimulado.java
   ├── WorkloadGenerator.java
   ├── Simulador.java
   ├── Experimentos.java

3. Funcionamento Geral
Passo a passo da simulação:
  1. Um conjunto de acessos é gerado.
  2. Cada acesso é enviado para a política de cache.
  3. Se ocorrer:
    Hit → dado vem da cache.
    Miss → dado é buscado no banco.
  4. Métricas são coletadas.
  5. Ao final, o desempenho é exibido.

Conclusão
Este projeto permite analisar como diferentes políticas de substituição impactam:
- Desempenho
- Eficiência
- Custo no acesso ao banco
