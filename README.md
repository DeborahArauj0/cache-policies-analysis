**Avaliação de Políticas de Cache em Sistemas para Saúde (FIFO e LFU)<br>
Disciplina: Laboratório de Estrutura de Dados e Algoritmos (LEDA)**<br>
------------------------------------------------------------------------------------------------------------------------------------
Integrantes:<br>
<br>- Bruna Rocha Cavalcanti
<br>- Deborah dos Santos Araujo
<br>- Mikael Renan de Oliveira
<br>- Teones Alex Lira de Farias Filho
------------------------------------------------------------------------------------------------------------------------------------
1. Introdução<br>
Este projeto materializa esse dilema clássico da Computação através de um simulador de atendimento ambulatorial desenvolvido em Java. O núcleo do estudo consiste na implementação do zero e na análise comparativa de duas políticas distintas de substituição de páginas: o algoritmo baseado em ordem de chegada FIFO (First In, First Out) e a estratégia baseada em popularidade LFU (Least Frequently Used). O intuito é demonstrar, com rigor experimental, como a escolha de uma Estrutura de Dados adequada impacta diretamente o desempenho de um sistema real.
------------------------------------------------------------------------------------------------------------------------------------
2. Objetivo<br>
O objetivo central deste trabalho é analisar e comparar a eficiência estrutural das políticas FIFO e LFU. O desempenho é avaliado através das seguintes métricas:
  - Taxa de Acertos (Hits): Quantidade de vezes que o dado solicitado já estava no cache.
  - Taxa de Falhas (Misses): Quantidade de vezes que foi necessário recorrer ao banco de dados.
  - Quantidade de Acessos ao Banco: Reflexo direto dos misses.
  - Tempo Total de Execução: Custo de latência, impactado pela complexidade do algoritmo de gerenciamento da estrutura do cache.
------------------------------------------------------------------------------------------------------------------------------------
3. Desenvolvimento e Metodologia<br>
O projeto foi desenvolvido em linguagem Java, adotando princípios de Orientação a Objetos (padrão Strategy via interface genérica CachePolicy<T>) para permitir a troca de algoritmos sem alterar a lógica de simulação.

3.1. Estruturas de Dados Utilizadas<br>
- FIFO (First In, First Out): Implementado utilizando a interface Queue (através de uma LinkedList) para manter a ordem cronológica de inserção, associada a um HashSet para garantir acesso rápido (tempo constante) na verificação de existência do elemento.
- LFU (Least Frequently Used): A política que remove o item menos acessado. Foi estruturada mapeando os elementos em um HashMap para buscas rápidas, integrado a uma Lista Duplamente Encadeada (DoublyLinkedList) customizada que reordena internamente os "Nós" (Node) conforme a variável de frequência é atualizada a cada hit.

3.2. Modelagem Experimental<br>
Para testar a validade das políticas, foi criado um simulador de Banco de Dados que injeta um atraso artificial de 1ms (Thread.sleep(1)) a cada miss, representando o custo de I/O em disco.

A geração de dados (WorkloadGenerator) cria cenários de teste controlados:
- Cenário A (Aleatório Uniforme): Os pacientes são acessados aleatoriamente.
- Cenário C (Enviesado/LFU Friendly): Aplica uma distribuição em que 70% das requisições são concentradas em apenas 20% da base de pacientes (simulando casos crônicos de alta recorrência).
------------------------------------------------------------------------------------------------------------------------------------
4. Estrutura do Projeto<br>
cache-policies-analysis/
<br>├── src/main/java/br/com/cacheanalysis/
<br>│   ├── cache/
<br>│   │   ├── CachePolicy.java      (Interface base)
<br>│   │   ├── FIFOCache.java        (Lógica do First-In, First-Out)
<br>│   │   └── LFUCache.java         (Lógica do Least Frequently Used)
<br>│   │
<br>│   └── simulacao/
<br>│       ├── BancoDeDadosSimulado.java (Mock de latência)
<br>│       ├── WorkloadGenerator.java    (Gerador de matrizes de acesso)
<br>│       ├── Experimentos.java         (Classe Main para testes automatizados)
------------------------------------------------------------------------------------------------------------------------------------
5. Funcionamento Geral<br>
O fluxo de execução do simulador segue um passo a passo estruturado para avaliar o comportamento do sistema de forma justa e controlada:
  1. **Geração de Carga:** Inicialmente, um conjunto de acessos (simulando requisições de prontuários de pacientes) é gerado.
  2. **Processamento:** Cada acesso desse conjunto é enviado sequencialmente para a política de cache que está sendo testada no momento (FIFO ou LFU).
  3. **Verificação de Estado (Hit/Miss):** Para cada requisição, o sistema avalia onde o dado foi encontrado:
    * **Hit (Acerto):** O dado solicitado já estava presente e vem diretamente da memória cache, resultando em uma operação rápida.
    * **Miss (Falha):** O dado não é encontrado no cache. Consequentemente, ele é buscado no banco de dados principal (operação custosa) e inserido no cache para acessos futuros.
  4. **Coleta de Métricas:** Durante todo o ciclo, os contadores internos são atualizados, registrando as métricas essenciais.
  5. **Resultados:** Ao final da execução de toda a carga de testes, o desempenho total e o comparativo dos algoritmos são exibidos no console.
------------------------------------------------------------------------------------------------------------------------------------
6. Como Compilar e Executar<br>
- Passo 1: Compilação: Estando na raiz do projeto (cache-policies-analysis-main), execute o comando abaixo para gerar os binários na pasta out:
<br> `javac -d out src/main/java/br/com/cacheanalysis/cache/*.java` 
<br> `src/main/java/br/com/cacheanalysis/simulacao/*.java`

- Passo 2: Execução: Ainda no diretório raiz, execute a classe principal de experimentos. Ela testará automaticamente caches de capacidades 10, 20 e 50 em uma base de 100 pacientes com 500 acessos simulados:
<br> `java -cp out br.com.cacheanalysis.simulacao.Experimentos`
<br> Assim finalização a execução e simulação.
------------------------------------------------------------------------------------------------------------------------------------
7. Conclusão<br>
A implementação e execução deste simulador fornecem uma base empírica sólida para compreender o comportamento prático de estruturas de dados aplicadas ao gerenciamento de memória. Através da comparação direta entre as políticas FIFO e LFU, o projeto permite analisar de forma concreta como diferentes estratégias de substituição de páginas impactam diretamente os seguintes aspectos:
<br> **- Desempenho Geral do Sistema:** Como a escolha da estrutura de dados correta afeta o tempo total de execução e a fluidez do processamento em cenários de alta demanda.
<br> **- Eficiência do Algoritmo:** Observando como a taxa de acertos (*hits*) ou falhas (*misses*) varia de acordo com a "inteligência" da política (ordem de chegada vs. frequência de uso) frente a diferentes padrões de acesso aos dados dos pacientes.
<br>**- Custo Computacional (Acesso ao Banco):** Demonstrando na prática a importância fundamental de minimizar as buscas custosas no banco de dados principal (simuladas pela latência de I/O), poupando recursos do servidor.

Em resumo, o projeto comprova o princípio fundamental da disciplina de Estrutura de Dados: não existe um algoritmo universalmente perfeito.<br>
A escolha entre uma fila simples (FIFO) ou uma estrutura complexa de mapas e listas (LFU) depende intrinsecamente do comportamento da carga de trabalho (*workload*) que o sistema de saúde precisará suportar.
