**Avaliação de Políticas de Cache em Sistemas para Saúde (FIFO e LFU)
Disciplina: Laboratório de Estrutura de Dados e Algoritmos (LEDA)**

Integrantes:
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

3.1. Estruturas de Dados Utilizadas
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
5. Funcionamento Geral
Passo a passo da simulação:
    1. Um conjunto de acessos é gerado.
    2. Cada acesso é enviado para a política de cache.
    3. Se ocorrer:
      - Hit → dado vem da cache.
      - Miss → dado é buscado no banco.
    4. Métricas são coletadas.
    5. Ao final, o desempenho é exibido.
------------------------------------------------------------------------------------------------------------------------------------
6. Como Compilar e Executar<br>
Passo 1: Compilação. Estando na raiz do projeto (cache-policies-analysis-main), execute o comando abaixo para gerar os binários na pasta out:
<br> `javac -d out src/main/java/br/com/cacheanalysis/cache/*.java` 
<br> `src/main/java/br/com/cacheanalysis/simulacao/*.java`
<
Passo 2: Execução. Ainda no diretório raiz, execute a classe principal de experimentos. Ela testará automaticamente caches de capacidades 10, 20 e 50 em uma base de 100 pacientes com 500 acessos simulados:
<br> `java -cp out br.com.cacheanalysis.simulacao.Experimentos`
<br>
------------------------------------------------------------------------------------------------------------------------------------
7. Conclusão<br>
Este projeto permite analisar como diferentes políticas de substituição impactam:
<br>- Desempenho
<br>- Eficiência
<br>- Custo no acesso ao banco
