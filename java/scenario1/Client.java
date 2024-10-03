import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {

    private String id;
    private BlockingQueue<Order> queue;
    private Long currentId = 0L;
    private List<Order> orders = new ArrayList<>();

    public Client (BlockingQueue<Order> queue, String id) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        ScheduledExecutorService executorService =
                Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            Order order = new Order(id + "_" + ++currentId);
            // add random products and quantities to order SaleItem
            orders.add(order);
            try {
                queue.put(order);
            } catch(Exception e) {}

        }, 1, 5, TimeUnit.SECONDS);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}