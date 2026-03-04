# cache-policies-analysis

Projeto EDA/LEDA:
Comparação De Diferentes Políticas De Cache (FIFO E LFU)

Integrantes:
Bruna Rocha Cavalcanti  
Deborah dos Santos Araujo  
Mikael Renan de Oliveira 
Teones Alex Lira de Farias Filho 

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

  ### Para compilar, estando na raiz do projeto (cache-polices-analysis)

  `javac -d out src/main/java/br/com/cacheanalysis/cache/*.java src/main/java/br/com/cacheanalysis/simulacao/*.java`

  ### Ainda na raiz do projeto, executar:

  `java -cp out br.com.cacheanalysis.simulacao.Experimentos`

Conclusão
Este projeto permite analisar como diferentes políticas de substituição impactam:
- Desempenho
- Eficiência
- Custo no acesso ao banco
