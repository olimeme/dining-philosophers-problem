import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Philosopher implements Runnable {
    private final int id;
    private final Lock leftFork;
    private final Lock rightFork;

    public Philosopher(int id, Lock leftFork, Lock rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        // Simulate thinking time
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating.");
        // Simulate eating time
        Thread.sleep(1000);
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();

                // Acquire left fork
                leftFork.lock();
                try {
                    // Acquire right fork
                    if (rightFork.tryLock()) {
                        try {
                            eat();
                        } finally {
                            // Release right fork
                            rightFork.unlock();
                            System.out.println(
                                    "Philosopher " + id + " released the right fork.");
                        }
                    } else {
                        // If unable to acquire right fork, release left fork
                        System.out.println("Philosopher " + id + " couldn't acquire both forks. Releasing left fork.");
                    }
                } finally {
                    // Release left fork
                    leftFork.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class App {
    public static void main(String[] args) {
        final int numPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Lock[] forks = new ReentrantLock[numPhilosophers];

        // Initialize forks
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new ReentrantLock();
        }

        // Initialize philosophers
        for (int i = 0; i < numPhilosophers; i++) {
            philosophers[i] = new Philosopher(i, forks[i], forks[(i + 1) % numPhilosophers]);
            new Thread(philosophers[i]).start();
        }
    }
}