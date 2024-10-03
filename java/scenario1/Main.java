import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static BlockingQueue<Order> queue = new ArrayBlockingQueue<>(10);
    private static Map<String, Integer> storage = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        Client[] clients = new Client[] {
                new Client(queue, "p1"),
                new Client(queue, "p2"),
                new Client(queue, "p3"),
                new Client(queue, "p4"),
                new Client(queue, "p5"),
        };

        Node[] processors = new Node[] {
                new Node(queue, storage),
                new Node(queue, storage),
                new Node(queue, storage),
        };

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

        ScheduledExecutorService monitorSchedule = Executors.newSingleThreadScheduledExecutor();

        monitorSchedule.scheduleAtFixedRate(() -> {
            for (Client cl : clients) {
                System.out.println("Cliente: " + cl.getId());
                for (Order order : cl.getOrders()) {
                    if (order.hasFinished()) {
                        // System.out.println(order.getId() + ": " + order.getExecutionTime() + "ms");
                    }
                }
                System.out.println();
            }
            System.out.println("\n===========================\n");
        }, 1, 5, TimeUnit.SECONDS);
    }
}
