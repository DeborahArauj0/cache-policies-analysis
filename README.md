## **Avaliação de Políticas de Cache em Sistemas para Saúde (FIFO, LFU e LRU)**<br>
## **Disciplina: Laboratório de Estrutura de Dados e Algoritmos (LEDA)**<br>

### **Integrantes:**
- Bruna Rocha Cavalcanti
- Deborah dos Santos Araujo
- Mikael Renan de Oliveira
- Teones Alex Lira de Farias Filho

### **Documento do projeto:**<br>
https://docs.google.com/document/d/1kaUEZov6HEL2luhc6fQw7EsyArWNKysm/edit?usp=sharing&ouid=106442824714548305124&rtpof=true&sd=true
------------------------------------------------------------------------------------------------------------------------------------
### 1. Introdução<br>
Este projeto foca no dilema da Computação através de um simulador de atendimento ambulatorial desenvolvido em Java. O centro de estudo consiste na implementação do zero e na análise comparativa de três políticas distintas de substituição de páginas de cache, o intuito é demonstrar, com rigor experimental e passo a passo, como a escolha de uma Estrutura de Dados adequada afeta diretamente o desempenho e o tempo de resposta de um sistema real:<br>
- **FIFO (First In, First Out):** Algoritmo baseado em ordem cronológica de chegada.
- **LFU (Least Frequently Used):** Estratégia baseada na popularidade e frequência histórica de acessos.
- **LRU (Least Recently Used):** Estratégia baseada na recência de uso, descartando o que não é acessado há mais tempo.
------------------------------------------------------------------------------------------------------------------------------------
### 2. Objetivo<br>
O objetivo central deste trabalho é analisar e comparar a eficiência estrutural das políticas FIFO, LFU e LRU. O desempenho é avaliado através das seguintes métricas:
- **Taxa de Acertos (Hits):** Quantidade de vezes que o dado solicitado já estava no cache.
- **Taxa de Falhas (Misses):** Quantidade de vezes que foi necessário recorrer ao banco de dados principal.
- **Quantidade de Acessos ao Banco:** Reflexo direto dos misses, representando o custo computacional e de I/O.
- **Tempo Total de Execução:** Custo de latência, impactado pela complexidade do algoritmo de gerenciamento da estrutura do cache.
------------------------------------------------------------------------------------------------------------------------------------
### 3. Desenvolvimento e Metodologia<br>
O projeto foi desenvolvido em linguagem Java, adotando princípios de Orientação a Objetos (padrão Strategy via interface genérica `CachePolicy<T>`) para permitir a troca de algoritmos de forma polimórfica, sem alterar a lógica principal de simulação.

#### 3.1. Análise das Estruturas de Dados Utilizadas
A eficiência de um cache depende da complexidade de tempo de suas operações. Abaixo, detalhamos a arquitetura de cada política:
- **FIFO (First In, First Out):** Implementado utilizando a interface `Queue` (através de uma `LinkedList`) para manter a ordem cronológica de inserção. Para evitar a busca linear O(N) ao verificar se um paciente está no cache, a fila foi associada a um `HashSet`, garantindo acesso rápido em tempo constante O(1).
- **LRU (Least Recently Used):** A política que remove o item que está há mais tempo sem ser acessado. Foi estruturada combinando um `HashMap` e uma Lista Duplamente Encadeada com nós "falsos" (`head` e `tail`). Essa união perfeita garante que tanto a verificação de existência quanto a atualização de prioridade (mover o nó para o topo) ocorram com complexidade O(1).
- **LFU (Least Frequently Used):** A política que remove o item menos acessado historicamente. Foi mapeada utilizando um `HashMap` para buscas rápidas, integrado a uma Lista Duplamente Encadeada (`DoublyLinkedList`) customizada.
#### 3.2. Modelagem Experimental
Para testar a validade das políticas, foi criado um simulador de Banco de Dados que injeta um atraso artificial de 1ms (`Thread.sleep(1)`) a cada *miss*, representando o custo de I/O em disco.

A geração de dados (`WorkloadGenerator`) cria cenários de teste controlados:
- **Cenário A (Aleatório/Uniforme):** Acessos distribuídos igualmente entre todos os pacientes, servindo como base de comparação (baseline).

- **Cenário B (Temporalidade - LRU Friendly):** Simula pacientes que retornam em curto intervalo (localidade temporal), favorecendo algoritmos baseados em recência.

- **Cenário C (Frequência - LFU Friendly):** Aplica a regra de Pareto, onde 20% dos pacientes concentram 70% dos acessos (casos crônicos), testando a inteligência estatística do cache.
------------------------------------------------------------------------------------------------------------------------------------
### 4. Estrutura do Projeto
```text
cache-policies-analysis/
├── src/main/java/br/com/cacheanalysis/
│   ├── cache/
│   │   ├── CachePolicy.java          (Interface base)
│   │   ├── FIFOCache.java            (Lógica do First-In, First-Out)
│   │   ├── LFUCache.java             (Lógica do Least Frequently Used)
│   │   └── LRUCache.java             (Lógica do Least Recently Used)
│   │
│   └── simulacao/
│       ├── BancoDeDadosSimulado.java (Mock de latência)
│       ├── WorkloadGenerator.java    (Gerador de matrizes de acesso)
│       ├── Experimentos.java         (Classe Main para testes automatizados)
```
------------------------------------------------------------------------------------------------------------------------------------
### 5. Funcionamento Geral<br>
O fluxo de execução do simulador segue um passo a passo estruturado para avaliar o comportamento do sistema de forma justa e controlada:<br>
   1. Geração de Carga: Inicialmente, um conjunto de acessos (simulando requisições de prontuários de pacientes) é gerado.<br>
   2. Processamento: Cada acesso desse conjunto é enviado sequencialmente para a política de cache que está sendo testada no momento (FIFO ou LFU).<br>
   3. Verificação de Estado (Hit/Miss): Para cada requisição, o sistema avalia onde o dado foi encontrado:
      - **Hit (Acerto):** O dado solicitado já estava presente e vem diretamente da memória cache instantaneamente, resultando em uma operação rápida.
      - **Miss (Falha):** O dado não é encontrado no cache. Consequentemente, o sistema aguarda a latência artificial do banco, busca o dado (operação custosa) e o insere no cache para acessos futuros, aplicando a regra de expulsão se a capacidade máxima tiver sido atingida.<br>
   4. Coleta de Métricas: Durante todo o ciclo, os contadores internos são atualizados, registrando as métricas essenciais.<br>
   5. Resultados: Ao final da execução de toda a carga de testes, o desempenho total e o comparativo dos algoritmos são exibidos no console.<br>
------------------------------------------------------------------------------------------------------------------------------------
### 6. Como Compilar e Executar<br>
- Passo 1: Compilação: Estando na raiz do projeto (cache-policies-analysis-main), execute o comando abaixo para gerar os binários na pasta out:
<br> `javac -d bin $(find src/main/java -name "*.java")` 

- Passo 2: Execução: Ainda no diretório raiz, execute a classe principal de experimentos. Ela testará automaticamente caches de capacidades 10, 20 e 50 em uma base de 100 pacientes com 500 acessos simulados:
<br> `java -cp bin br.com.cacheanalysis.simulacao.Experimentos`
<br> Assim finalização a execução e simulação.

------------------------------------------------------------------------------------------------------------------------------------
### 7. Conclusão<br>
A execução do simulador demonstra que a eficiência de um sistema de saúde depende diretamente da escolha estratégica de estruturas de dados. A análise comparativa entre **FIFO, LFU e LRU** permite concluir que:
 - **Desempenho Geral:** A política correta reduz o tempo total de execução ao minimizar a latência de I/O do banco de dados, garantindo respostas mais rápidas em sistemas críticos.
 - **Inteligência vs. Custo:** Enquanto o **FIFO** prioriza a simplicidade com complexidade $O(1)$, políticas como **LRU** e **LFU** oferecem maior taxa de acertos (*hits*) em cenários de alta recorrência (pacientes crônicos), compensando sua maior complexidade estrutural.
 - **Otimização de Recursos:** A redução de *misses* preserva a integridade do servidor, evitando sobrecarga no banco de dados principal durante picos de demanda ambulatorial.

Em suma, o projeto confirma que não existe um algoritmo universal; a escolha da melhor política deve ser baseada no padrão de acessos (*workload*) real da instituição de saúde.
