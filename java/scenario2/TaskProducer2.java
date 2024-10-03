import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskProducer2 implements Runnable {

    public String id;
    private BlockingQueue<Task2> queue;
    private Long currentId = 0L;
    public List<Task2> tasks = new ArrayList<>();
    private int taskPriority;
    private int interval;


    public TaskProducer2 (BlockingQueue<Task2> queue, String id, int taskPriority, int interval) {
        this.id = id;
        this.queue = queue;
        this.taskPriority = taskPriority;
        this.interval = interval;
    }

    @Override
    public void run() {
        ScheduledExecutorService executorService =
                Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            Task2 task = new Task2(id + "_" + ++currentId, taskPriority);
            tasks.add(task);
            try {
                // adicionar tarefa na fila
                queue.put(task);
            } catch(Exception e) {}

        }, 1, interval, TimeUnit.SECONDS);
    }
}