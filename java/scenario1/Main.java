import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  private static BlockingQueue<Order> queue = new ArrayBlockingQueue<>(10);
  private static final Map<String, Integer> storage = new ConcurrentHashMap<>();
  private static final AtomicInteger processedOrders = new AtomicInteger(0);
  private static final AtomicInteger salesTotal = new AtomicInteger(0);
  private static final AtomicInteger rejectedOrders = new AtomicInteger(0);

  private static final Object printing = new Object();

  public static void main(String[] args) {

    Client[] clients = new Client[]{
            new Client(queue, "c1", printing),
            new Client(queue, "c2", printing),
            new Client(queue, "c3", printing),
            new Client(queue, "c4", printing),
            new Client(queue, "c5", printing),
    };

    Node[] processors = new Node[]{
            new Node(queue, storage, processedOrders, salesTotal, rejectedOrders, printing),
            new Node(queue, storage, processedOrders, salesTotal, rejectedOrders, printing),
            new Node(queue, storage, processedOrders, salesTotal, rejectedOrders, printing),
    };

    storage.put("Banana", 30);
    storage.put("Laranja", 30);
    storage.put("Siriguela", 30);
    storage.put("Cagaita", 30);
    storage.put("Pitomba", 30);
    storage.put("Macaiba", 30);

    ExecutorService producerService = Executors.newFixedThreadPool(5);
    producerService.execute(clients[0]);
    producerService.execute(clients[1]);
    producerService.execute(clients[2]);
    producerService.execute(clients[3]);
    producerService.execute(clients[4]);

    ExecutorService consumerService = Executors.newFixedThreadPool(3);
    consumerService.execute(processors[0]);
    consumerService.execute(processors[1]);
    consumerService.execute(processors[2]);

    ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

    schedule.scheduleAtFixedRate(() -> {
      List<String> prints = new ArrayList<>();
      synchronized (storage) {
        prints.add("\n===========================================\n");
        for (Map.Entry<String, Integer> entry : storage.entrySet()) {
          String p = entry.getKey();
          Integer quantity = entry.getValue();
          Random random = new Random();
          int randomNumber = 5 + random.nextInt(6);
          storage.put(p, quantity + randomNumber);
          prints.add("[ESTOQUE]     Estoque abastecido com " + randomNumber + " itens do produto " + p);
        }
        prints.add("\n===========================================\n");
      }

      synchronized (printing) {
        for (String print: prints) {
          System.out.println(print);
        }
      }

    }, 1, 10, TimeUnit.SECONDS);

    schedule.scheduleAtFixedRate(() -> {
      synchronized (printing) {
        System.out.println("\n===========================================\n");
        System.out.println("[RELATORIO]   Relatório de Vendas:");
        System.out.println("[RELATORIO]   Número total de pedidos processados: " + processedOrders.get());
        System.out.println("[RELATORIO]   Valor total das vendas: " + salesTotal.get());
        System.out.println("[RELATORIO]   Número de pedidos rejeitados: " + rejectedOrders.get());
        System.out.println("\n===========================================\n");
      }
    }, 1, 30, TimeUnit.SECONDS);
  }
}
