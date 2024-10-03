import java.util.Random;

public class Task2 implements Comparable<Task2> {
    String id;
    private Long startingTime;
    private Long finishTime;
    public int priority;

    public Task2(String id, int priority) {
        this.id = id;
        this.startingTime = System.currentTimeMillis();
        this.priority = priority;
    }

    public void execute() {
        try {
            // generating a number between 1000 and 15000
            long execDuration = 1000 + (long) (new Random().nextFloat() * (15000 - 1000));
            Thread.sleep(execDuration);
            this.finishTime = System.currentTimeMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFinished() {
        return finishTime != null;
    }

    public Long getExecutionTime() {
        if (hasFinished()) {
            return finishTime - startingTime;
        }
        return null;
    }

    @Override
    public int compareTo(Task2 arg0) {
        return this.priority - ((Task2) arg0).priority;
    }

}
