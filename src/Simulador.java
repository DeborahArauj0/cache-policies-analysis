import java.util.Scanner;

public class Simulador {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Capacidade do cache: ");
        int capacity = sc.nextInt();

        FIFOCache<Integer> cache = new FIFOCache<>(capacity);

        System.out.println("\nDigite os acessos (-1 para sair)\n");

        while (true) {

            System.out.print("Acesso: ");
            int value = sc.nextInt();

            if (value == -1)
                break;

            boolean hit = cache.access(value);

            if (hit)
                System.out.println("RESULTADO: HIT");
            else
                System.out.println("RESULTADO: MISS");

            cache.printCache();
            System.out.println("---------------------");
        }

        System.out.println("\n===== RESULTADO FINAL =====");
        System.out.println("Hits: " + cache.getHits());
        System.out.println("Misses: " + cache.getMisses());

        sc.close();
    }
}