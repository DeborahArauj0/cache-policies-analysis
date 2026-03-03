import java.util.HashMap;
import java.util.Map;

public class LFUCache<T> implements CachePolicy<T> {

    private int capacity;
    private int hits;
    private int misses;
    
    // O Mapa garante a busca em O(1)
    private Map<T, Node<T>> cacheMap;
    // A Lista garante a ordem de quem sai primeiro
    private DoublyLinkedList<T> freqList;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.hits = 0;
        this.misses = 0;
        this.cacheMap = new HashMap<>();
        this.freqList = new DoublyLinkedList<>();
    }

    @Override
    public boolean access(T key) {
        // HIT: O paciente já está no cache
        if (cacheMap.containsKey(key)) {
            this.hits++;
            Node<T> node = cacheMap.get(key);
            node.frequency++;
            freqList.sortByFrequency(node); // Reordena usando a sua lógica imbatível
            return true;
        } 
        // MISS: O paciente não está no cache
        else {
            this.misses++;
            
            // Se estiver cheio, removemos o menos frequente (que fica no Head da lista)
            if (cacheMap.size() == capacity) {
                Node<T> evicted = freqList.removeFirst();
                if (evicted != null) {
                    cacheMap.remove(evicted.value); // Tira do mapa também
                }
            }
            
            // Adiciona o paciente novo
            Node<T> newNode = new Node<>(key);
            freqList.addFirst(newNode);
            cacheMap.put(key, newNode);
            return false;
        }
    }

    @Override
    public int getHits() {
        return this.hits;
    }

    @Override
    public int getMisses() {
        return this.misses;
    }

    // Método exigido pelo Simulador.java da sua equipe
    public void printCache() {
        System.out.println("Estado do Cache (LFU): " + freqList.toString());
    }

    // ==========================================
    // CLASSES INTERNAS (O motor da frequência)
    // ==========================================
    
    private static class Node<T> {
        T value;
        int frequency;
        Node<T> prev;
        Node<T> next;

        Node(T v) {
            this.value = v;
            this.frequency = 1;
        }
    }

    private static class DoublyLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size;

        public DoublyLinkedList() {
            this.head = null;
            this.tail = null;
            this.size = 0;
        }

        public void addFirst(Node<T> node) {
            if (head == null) {
                head = node;
                tail = node;
            } else {
                node.next = head;
                head.prev = node;
                head = node;
            }
            size++;
        }

        public Node<T> removeFirst() {
            if (head == null) return null;
            
            Node<T> removed = head;
            if (head.next == null) {
                head = null;
                tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }
            size--;
            removed.next = null;
            return removed;
        }

        // A sua lógica de ponteiros corrigida!
        public void sortByFrequency(Node<T> node) {
            if (head == null || size == 1 || node.next == null) return;
            if (node.frequency <= node.next.frequency) return;

            Node<T> ref = node.next;

            if (node == head) {
                head = node.next;
                head.prev = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            
            node.next = null;
            node.prev = null;

            while (ref != null && node.frequency > ref.frequency) {
                ref = ref.next;
            }

            if (ref == null) {
                tail.next = node;
                node.prev = tail;
                tail = node;
            } else {
                node.next = ref;
                node.prev = ref.prev;
                if (ref.prev != null) {
                    ref.prev.next = node;
                } else {
                    head = node;
                }
                ref.prev = node;
            }
        }

        public String toString() {
            if (head == null) return "[]";
            StringBuilder sb = new StringBuilder("[");
            Node<T> aux = head;
            while (aux != null) {
                sb.append("<").append(aux.value).append(", f:").append(aux.frequency).append(">");
                if (aux.next != null) sb.append(", ");
                aux = aux.next;
            }
            sb.append("]");
            return sb.toString();
        }
    }
}