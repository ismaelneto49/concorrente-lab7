import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Node implements Runnable {

  private BlockingQueue<Order> queue;
  private Map<String, Integer> storage;
  private boolean isFree = true;

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

    if (!canOrder(order, items) && isFree) {
      return;
    }

    synchronized (storage) {
      // acho que essa flag tem que ser global, 
      // passada como parametro do construtor
      isFree = false;
      if (!canOrder(order, items)) {
        return;
      }

      for (Map.Entry<Product, Integer> entry : items.entrySet()) {
        Product p = entry.getKey();
        Integer quantity = entry.getValue();

        int storageQuantity = storage.get(p.getName());
        storage.put(p.getName(), storageQuantity - quantity);
      }
      order.setStatus(OrderStatus.FINISHED);
    }
  }

  private boolean canOrder(Order order, Map<Product, Integer> items) {
    for (Map.Entry<Product, Integer> entry : items.entrySet()) {
      Product p = entry.getKey();
      Integer quantity = entry.getValue();

      int storageQuantity = storage.get(p.getName());
      if (storageQuantity < quantity) {
        order.setStatus(OrderStatus.REJECTED);
        return false;
      }
    }
    return true;
  }
}
