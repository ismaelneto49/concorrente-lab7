import java.util.concurrent.BlockingQueue;

public class Node2 implements Runnable {

    private BlockingQueue<Task2> queue;

    public Node2(BlockingQueue<Task2> queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      while (true) {
        try {
          Task2 task = queue.take();
          task.execute();
        } catch (InterruptedException e) {}
      }
    }
}
