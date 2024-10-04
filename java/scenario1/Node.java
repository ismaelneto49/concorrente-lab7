import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Node implements Runnable {

  private final Object printing;
  private BlockingQueue<Order> queue;
  private Map<String, Integer> storage;
  private final AtomicInteger processedOrders;
  private final AtomicInteger salesTotal;
  private final AtomicInteger rejectedOrders;

  public Node(BlockingQueue<Order> queue, Map<String, Integer> storage, AtomicInteger processedOrders, AtomicInteger salesTotal, AtomicInteger rejectedOrders, Object printing) {
    this.queue = queue;
    this.storage = storage;
    this.processedOrders = processedOrders;
    this.salesTotal = salesTotal;
    this.rejectedOrders = rejectedOrders;
    this.printing = printing;
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
    try {
      Thread.sleep(new Random().nextInt(5) * 1000);
    } catch (InterruptedException e) {}
    Map<Product, Integer> items = order.getSaleItem().getItems();

    if (!canOrder(order, items)) {
      rejectedOrders.incrementAndGet();
      synchronized(printing) {
        System.out.println("[REJEITADO]   Pedido " +  order.getId() + " do cliente " + order.getClientId() + " foi rejeitado");
      }
      return;
    }

    synchronized (storage) {
      if (!canOrder(order, items)) {
        rejectedOrders.incrementAndGet();

        synchronized (printing) {
          System.out.println("[REJEITADO]   Pedido " +  order.getId() + " do cliente " + order.getClientId() + " foi rejeitado");
        }
        return;
      }
      int total = 0;
      for (Map.Entry<Product, Integer> entry : items.entrySet()) {
        Product p = entry.getKey();
        Integer quantity = entry.getValue();
        total += p.getPrice() * quantity;
        int storageQuantity = storage.get(p.getName());
        storage.put(p.getName(), storageQuantity - quantity);
      }
      order.setStatus(OrderStatus.FINISHED);
      processedOrders.incrementAndGet();
      salesTotal.addAndGet(total);

      synchronized (printing) {
        System.out.println("[PROCESSADO]  Pedido " +  order.getId() + " do cliente " + order.getClientId() + " foi processado");
      }
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
