public interface CachePolicy<T> {

    boolean access(T key); // retorna true se hit, false se miss

    int getHits();

    int getMisses();

}