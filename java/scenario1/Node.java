import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Node implements Runnable {

  private BlockingQueue<Order> queue;
  private Map<String, Integer> storage;
  AtomicBoolean locked = new AtomicBoolean(false);

  public Node(BlockingQueue<Order> queue, Map<String, Integer> storage) {
    this.queue = queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Order order = queue.take();
        processOrder(order);
      } catch (InterruptedException e) {
      }
    }
  }

  private void processOrder(Order order) {
    Map<Product, Integer> items = order.getSaleItem().getItems();

    while (true) {
      while (locked.get()) {
      }
      if (!locked.getAndSet(true)) {
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
          Product p = entry.getKey();
          Integer quantity = entry.getValue();
    
          int storageQuantity = storage.get(p.getName());
    
          if (storageQuantity < quantity) {
            order.setStatus(OrderStatus.PROCESSING);
          }
        }
        break;
      }
    }

    for (Map.Entry<Product, Integer> entry : items.entrySet()) {
      Product p = entry.getKey();
      Integer quantity = entry.getValue();

      int storageQuantity = storage.get(p.getName());
      storage.put(p.getName(), storageQuantity - quantity);
    }
    
    locked.set(false);
    order.setStatus(OrderStatus.FINISHED);

  }
}
