import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;

public class FIFOCache<T> implements CachePolicy<T> {

    private int capacity;
    private Queue<T> queue;
    private Set<T> cacheSet;

    private int hits;
    private int misses;

    public FIFOCache(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.cacheSet = new HashSet<>();
        this.hits = 0;
        this.misses = 0;
    }

    @Override
    public boolean access(T key) {

        // HIT
        if (cacheSet.contains(key)) {
            hits++;
            return true;
        }

        // MISS
        misses++;

        // cache cheio → remove mais antigo
        if (queue.size() == capacity) {
            T removed = queue.poll();
            cacheSet.remove(removed);
            System.out.println("REMOVEU: " + removed);
        }

        // adiciona novo
        queue.offer(key);
        cacheSet.add(key);

        return false;
    }

    @Override
    public int getHits() {
        return hits;
    }

    @Override
    public int getMisses() {
        return misses;
    }

    public void printCache() {
        System.out.println("CACHE: " + queue);
    }
}