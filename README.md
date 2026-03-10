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
O objetivo central deste trabalho empírico é analisar a eficiência assintótica e prática das políticas FIFO, LRU e LFU sob uma carga de 50.000 acessos. O desempenho é avaliado quantitativamente através das seguintes métricas:
- **Taxa de Acertos (Hits):** Quantidade de vezes que o dado solicitado já estava no cache.
- **Taxa de Falhas (Misses):** Quantidade de vezes que foi necessário recorrer ao banco de dados.
- **Tempo Real de CPU:** Medição do custo de processamento dos algoritmos em nanossegundos/milissegundos.
- **Latência Simulada:** Cálculo matemático do custo de I/O (ex: 1ms por Hit, 10ms por Miss) para refletir o impacto real em um servidor.
------------------------------------------------------------------------------------------------------------------------------------
### 3. Desenvolvimento e Metodologia<br>
O projeto adota o padrão de projeto Strategy via interface genérica CachePolicy<T>, permitindo o intercâmbio dinâmico de algoritmos durante a simulação das 50.000 requisições.

#### 3.1. Análise das Estruturas de Dados Utilizadas
Para garantir que o simulador não sofra gargalos de processamento com 50.000 acessos, os algoritmos foram desenhados para evitar varreduras lineares ($O(n)$).
- **FIFO (First In, First Out):** Implementado utilizando um LinkedHashSet ou a interface Queue. Mantém a ordem cronológica de inserção e garante acesso rápido na verificação de existência do elemento.
- **LRU (Least Recently Used):**  Estruturado com a combinação de um HashMap (para localização instantânea do dado em memória) e uma Lista Duplamente Encadeada (DoublyLinkedList) customizada. Quando um dado é acessado, ele é isolado e movido para o "topo" da lista mediante a simples troca de ponteiros.
- **LFU (Least Frequently Used):**  A política mais complexa, implementada com múltiplos mapas (HashMap). Um mapa associa a chave à sua frequência, enquanto outro mapa associa uma frequência a um LinkedHashSet de chaves. O uso de uma variável sentinela minFrequencia elimina a necessidade de buscar a menor frequência iterativamente quando o cache enche.

#### 3.2. Análise de Complexidade Assintótica
Para garantir que o simulador escale e suporte grandes volumes de dados (ex: cargas de *stress test* com mais de 50.000 requisições), as estruturas foram projetadas para evitar varreduras lineares ($O(n)$).

| Política de Cache | Estrutura Principal | Busca (Hit) | Inserção/Evicção |
| :--- | :--- | :--- | :--- |
| **FIFO** | `LinkedHashSet` | $O(1)$ | $O(1)$ |
| **LRU** | `HashMap` + `DoublyLinkedList` | $O(1)$ | $O(1)$ |
| **LFU** | Duplo `HashMap` + `LinkedHashSet` | $O(1)$ | $O(1)$ |

*(Nota: O LFU atinge complexidade O(1) rastreando ativamente a frequência mínima em uma variável sentinela, eliminando a necessidade de buscar a menor frequência em caso de cache cheio).*

#### 3.3. Modelagem Experimental
Para testar a validade das políticas, foi criado um simulador de Banco de Dados que injeta um atraso artificial de 1ms (`Thread.sleep(1)`) a cada *miss*, representando o custo de I/O em disco.

* **LRU (Least Recently Used):** Implementado utilizando a combinação de um `HashMap` para localização instantânea do dado em memória e uma Lista Duplamente Encadeada (`DoublyLinkedList`) customizada para manter o histórico de temporalidade. Quando um dado é acessado, ele é movido para o início da lista em tempo constante.

A geração de dados (`WorkloadGenerator`) cria cenários de teste controlados; Para medir o desempenho real dos algoritmos sem distorções causadas pelo bloqueio do processador, a simulação em larga escala não utiliza atrasos artificiais (como `Thread.sleep()`) para simular o I/O do banco de dados. Em vez disso, o tempo de execução puro das estruturas de dados é cronometrado via `System.nanoTime()`, enquanto a latência de disco é calculada matematicamente ao final do experimento, multiplicando o número de *misses* pelo custo teórico de acesso ao banco:
- **Cenário A (Aleatório/Uniforme):** Acessos distribuídos uniformemente entre todos os pacientes cadastrados. Serve como base de comparação empírica (*baseline*).
- **Cenário B (Temporalidade - LRU Friendly):** Simula pacientes que retornam ao posto em um curto intervalo de tempo (localidade temporal), favorecendo algoritmos baseados em recência.
- **Cenário C (Frequência - LFU Friendly):** Aplica um viés estatístico inspirado na Regra de Pareto, onde uma minoria de pacientes (ex: casos crônicos) concentra a maior parte dos acessos do sistema (ex: 20% dos pacientes geram 70% das requisições). Este cenário testa a resiliência estatística do cache a longo prazo.
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
   2. Processamento: Cada acesso desse conjunto é enviado sequencialmente para a política de cache que está sendo testada no momento (FIFO, LFU ou LRU).<br>
   3. Verificação de Estado (Hit/Miss): Para cada requisição, o sistema avalia onde o dado foi encontrado:
      - **Hit (Acerto):** O dado solicitado já estava presente e vem diretamente da memória cache instantaneamente, resultando em uma operação rápida.
      - **Miss (Falha):** O dado não é encontrado no cache. Consequentemente, o sistema aguarda a latência artificial do banco, busca o dado (operação custosa) e o insere no cache para acessos futuros, aplicando a regra de expulsão se a capacidade máxima tiver sido atingida.<br>
   4. Coleta de Métricas: Durante todo o ciclo, os contadores internos são atualizados, registrando as métricas essenciais.<br>
   5. Resultados: Ao final da execução de toda a carga de testes, o desempenho total e o comparativo dos algoritmos são exibidos no console.<br>
------------------------------------------------------------------------------------------------------------------------------------
### 6. Como Compilar e Executar<br>
- Passo 1: Compilação Estando na raiz do projeto (cache-policies-analysis-main), execute o comando abaixo para gerar os binários na pasta out:
<br> `javac -d bin $(find src/main/java -name "*.java")` 

- Passo 2: Execução Ainda no diretório raiz, execute a classe principal. Ela testará automaticamente caches de diferentes capacidades (ex: 100, 500, 1000 slots) contra a base de 50.000 acessos:
<br> `java -cp bin br.com.cacheanalysis.simulacao.Experimentos`
<br> Assim finalização a execução e simulação.

------------------------------------------------------------------------------------------------------------------------------------
### 7. Conclusão
A execução deste simulador em escala de estresse (50.000 requisições) demonstra que a eficiência de um sistema de saúde não depende apenas de limitações de hardware, mas da escolha estratégica e da otimização de estruturas de dados. Ao garantir operações em tempo constante O(1) e evitar a degradação para O(n) sob alta carga, a análise comparativa entre as políticas **FIFO, LRU e LFU** permite concluir que:

- **Desempenho Geral:** A política correta reduz o tempo total de execução ao minimizar a latência de I/O do banco de dados, garantindo respostas mais rápidas em sistemas críticos.
- **Inteligência vs. Custo Estrutural:** Enquanto o **FIFO** prioriza a extrema simplicidade de implementação, políticas avançadas como **LRU** e **LFU** exigem estruturas combinadas mais complexas (Mapas e Listas Duplamente Encadeadas). Contudo, compensam esse esforço arquitetônico entregando uma maior taxa de acertos (*hits*) em cenários de localidade temporal ou alta recorrência (pacientes crônicos).
- **Otimização de Recursos:** A maximização de *hits* e a consequente redução de *misses* preservam a integridade do servidor, evitando a sobrecarga no banco de dados principal durante picos de demanda ambulatorial.

Em suma, o projeto ratifica o princípio fundamental da disciplina: não existe um algoritmo universalmente perfeito. A escolha da política de substituição de cache depende estritamente do padrão de requisições (*workload*) e da distribuição de acessos da unidade de saúde, exigindo do engenheiro de software uma análise prévia minuciosa do comportamento dos dados.
