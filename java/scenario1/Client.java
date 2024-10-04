import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Client implements Runnable {

    private final Object printing;
    private String id;
    private BlockingQueue<Order> queue;
    private Long currentId = 0L;

    private Integer interval = 1 + new Random().nextInt(10);
    private static final List<Product> products =
            IntStream.range(0, Arrays.asList("Banana", "Laranja", "Siriguela", "Cagaita", "Pitomba", "Macaiba").size())
                    .mapToObj(index -> new Product(
                            Arrays.asList("Banana", "Laranja", "Siriguela", "Cagaita", "Pitomba", "Macaiba").get(index),
                            ((index*2)+1)
                    ))
                    .collect(Collectors.toList());

    public Client (BlockingQueue<Order> queue, String id, Object printing) {
        this.id = id;
        this.queue = queue;
        this.printing = printing;
    }

    @Override
    public void run() {
        ScheduledExecutorService executorService =
                Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            Order order = new Order(id + "_" + ++currentId);
            SaleItem saleItem = new SaleItem();
            Map<Product, Integer> items = saleItem.getItems();

            for (Product p: products) {
                Random random = new Random();
                boolean shouldProductBeInOrder = random.nextDouble() < 0.40;
                if (shouldProductBeInOrder) {
                    items.put(p, 5 + random.nextInt(6));
                }
            }
            order.setSaleItem(saleItem);
            order.setClientId(this.id);
            synchronized (printing) {
                System.out.println("[PEDIDO]      Pedido " + order.getId() + " do cliente " + this.id + " foi feito");
            }
            try {
                queue.put(order);
            } catch(Exception e) {}

        }, 1, interval, TimeUnit.SECONDS);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}