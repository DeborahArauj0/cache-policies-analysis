**Avaliação de Políticas de Cache em Sistemas para Saúde (FIFO, LFU e LRU)**<br>
**Disciplina: Laboratório de Estrutura de Dados e Algoritmos (LEDA)**<br>
------------------------------------------------------------------------------------------------------------------------------------
**Integrantes:**
<br>- Bruna Rocha Cavalcanti
<br>- Deborah dos Santos Araujo
<br>- Mikael Renan de Oliveira
<br>- Teones Alex Lira de Farias Filho
------------------------------------------------------------------------------------------------------------------------------------
### 1. Introdução<br>
Este projeto materializa um dilema clássico da Computação através de um simulador de atendimento ambulatorial desenvolvido em Java. O núcleo do estudo consiste na implementação do zero e na análise comparativa de três políticas distintas de substituição de páginas de cache: 
  * **FIFO (First In, First Out):** Algoritmo baseado em ordem de chegada.
  * **LFU (Least Frequently Used):** Estratégia baseada na popularidade/frequência histórica.
  * **LRU (Least Recently Used):** Estratégia baseada na recência de uso.
O intuito é demonstrar, com rigor experimental e passo a passo, como a escolha de uma Estrutura de Dados adequada impacta diretamente o desempenho e o tempo de resposta de um sistema real.
------------------------------------------------------------------------------------------------------------------------------------
### 2. Objetivo<br>
O objetivo central deste trabalho é analisar e comparar a eficiência estrutural das políticas FIFO, LFU e LRU. O desempenho é avaliado através das seguintes métricas:
  - **Taxa de Acertos (Hits):** Quantidade de vezes que o dado solicitado já estava no cache.
  - **Taxa de Falhas (Misses):** Quantidade de vezes que foi necessário recorrer ao banco de dados principal.
  - **Quantidade de Acessos ao Banco:** Reflexo direto dos misses, representando o custo computacional.
  - **Tempo Total de Execução:** Custo de latência, impactado pela complexidade do algoritmo de gerenciamento da estrutura do cache.
------------------------------------------------------------------------------------------------------------------------------------
### 3. Desenvolvimento e Metodologia<br>
O projeto foi desenvolvido em linguagem Java, adotando princípios de Orientação a Objetos (padrão Strategy via interface genérica `CachePolicy<T>`) para permitir a troca de algoritmos de forma polimórfica, sem alterar a lógica principal de simulação.

#### 3.1. Análise das Estruturas de Dados Utilizadas
A eficiência de um cache depende da complexidade de tempo de suas operações. Abaixo, detalhamos a arquitetura de cada política:

- **FIFO (First In, First Out):** Implementado utilizando a interface `Queue` (através de uma `LinkedList`) para manter a ordem cronológica de inserção. Para evitar a busca linear $O(N)$ ao verificar se um paciente está no cache, a fila foi associada a um `HashSet`, garantindo acesso rápido em tempo constante $O(1)$.

- **LRU (Least Recently Used):** A política que remove o item que está há mais tempo sem ser acessado. Foi estruturada combinando um `HashMap` e uma Lista Duplamente Encadeada com nós "falsos" (head e tail). Essa união perfeita garante que tanto a verificação de existência quanto a atualização de prioridade (mover o nó para o topo) ocorram com complexidade $O(1)$.

- **LFU (Least Frequently Used):** A política que remove o item menos acessado historicamente. Foi mapeada utilizando um `HashMap` para buscas rápidas, integrado a uma Lista Duplamente Encadeada (`DoublyLinkedList`) customizada. 
  *Nota de Complexidade:* Sempre que ocorre um *hit*, a frequência do nó é incrementada e ele precisa ser reposicionado. Neste projeto, a reordenação (`sortByFrequency`) percorre a lista para encontrar a nova posição, operando com complexidade de pior caso $O(N)$. Isso ilustra o *trade-off* clássico: ganhamos em inteligência de acertos, mas pagamos um custo computacional maior na manutenção da estrutura.

#### 3.2. Modelagem Experimental
Para testar a validade das políticas, foi criado um simulador de Banco de Dados que injeta um atraso artificial de 1ms (`Thread.sleep(1)`) a cada *miss*, representando o custo de I/O em disco.

A geração de dados (`WorkloadGenerator`) cria cenários de teste controlados:
- **Cenário A (Aleatório Uniforme):** Os pacientes são acessados aleatoriamente.
- **Cenário C (Enviesado / LFU-LRU Friendly):** Aplica uma distribuição em que 70% das requisições são concentradas em apenas 20% da base de pacientes (simulando casos crônicos de alta recorrência e validando a inteligência dos caches LFU e LRU).
------------------------------------------------------------------------------------------------------------------------------------
### 4. Estrutura do Projeto
```text
cache-policies-analysis/
├── src/main/java/br/com/cacheanalysis/
│   ├── cache/
│   │   ├── CachePolicy.java      (Interface base)
│   │   ├── FIFOCache.java        (Lógica do First-In, First-Out)
│   │   ├── LFUCache.java         (Lógica do Least Frequently Used)
│   │   └── LRUCache.java         (Lógica do Least Recently Used)
│   │
│   └── simulacao/
│       ├── BancoDeDadosSimulado.java (Mock de latência)
│       ├── WorkloadGenerator.java    (Gerador de matrizes de acesso)
│       ├── Experimentos.java         (Classe Main para testes automatizados)
```
------------------------------------------------------------------------------------------------------------------------------------
### 5. Funcionamento Geral<br>
O fluxo de execução do simulador segue um passo a passo rigoroso para avaliar o comportamento do sistema de forma justa:
  1. **Geração de Carga:** Um conjunto de acessos (simulando requisições de prontuários) é gerado integralmente antes do início do cronômetro.
  2. **Processamento:** Cada acesso é enviado sequencialmente para a política testada (FIFO, LFU ou LRU).
  3. **Verificação de Estado (Hit/Miss):**
     **- Hit:** O dado é retornado da memória cache instantaneamente.
     **- Miss:** O sistema aguarda a latência artificial do banco, insere o dado no cache e aplica a regra de expulsão (se a capacidade máxima tiver sido atingida).
  4. **Coleta de Métricas:** Contadores internos registram acertos, falhas e tempo.
  5. **Resultados:** O desempenho comparativo é exibido no console final.
------------------------------------------------------------------------------------------------------------------------------------
### 6. Como Compilar e Executar<br>
- Passo 1: Compilação: Estando na raiz do projeto (cache-policies-analysis-main), execute o comando abaixo para gerar os binários na pasta out:
<br> `javac -d out src/main/java/br/com/cacheanalysis/cache/*.java` 
<br> `src/main/java/br/com/cacheanalysis/simulacao/*.java`

- Passo 2: Execução: Ainda no diretório raiz, execute a classe principal de experimentos. Ela testará automaticamente caches de capacidades 10, 20 e 50 em uma base de 100 pacientes com 500 acessos simulados:
<br> `java -cp out br.com.cacheanalysis.simulacao.Experimentos`
<br> Assim finalização a execução e simulação.

------------------------------------------------------------------------------------------------------------------------------------
### 7. Conclusão<br>
A implementação e execução deste simulador fornecem uma base empírica sólida para compreender o comportamento prático de estruturas de dados aplicadas ao gerenciamento de memória. Através da comparação direta entre as políticas **FIFO, LFU e LRU**, o projeto permite analisar de forma concreta como diferentes estratégias de substituição de páginas impactam diretamente os seguintes aspectos:

* **Desempenho Geral do Sistema:** Como a escolha da estrutura de dados correta afeta o tempo total de execução e a fluidez do processamento em cenários de alta demanda.
* **Eficiência do Algoritmo:** Observando como a taxa de acertos (*hits*) ou falhas (*misses*) varia de acordo com a "inteligência" da política (ordem de chegada, frequência histórica ou recência de uso) frente a diferentes padrões de acesso aos dados dos pacientes.
* **Custo Computacional (Acesso ao Banco):** Demonstrando na prática a importância fundamental de minimizar as buscas custosas no banco de dados principal (simuladas pela latência de I/O), poupando recursos do servidor.

Em resumo, o projeto comprova o princípio fundamental da disciplina de Estrutura de Dados: não existe um algoritmo universalmente perfeito. A escolha entre uma fila simples (FIFO) ou estruturas complexas de mapas e listas (LFU e LRU) depende intrinsecamente do comportamento da carga de trabalho (*workload*) que o sistema de saúde precisará suportar.
