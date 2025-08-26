package in.wynk.secret.manager.threads;

import java.util.concurrent.Semaphore;

public class PrintABC {

    private static final int N = 10; // Number of times to repeat

    public static void main(String[] args) {
        Semaphore semA = new Semaphore(1); // Start with A
        Semaphore semB = new Semaphore(0);
        Semaphore semC = new Semaphore(0);

        Thread threadA = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semA.acquire();
                    System.out.print("a ");
                    semB.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semB.acquire();
                    System.out.print("b ");
                    semC.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadC = new Thread(() -> {
            try {
                for (int i = 0; i < N; i++) {
                    semC.acquire();
                    System.out.print("c ");
                    semA.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
        threadC.start();
    }
}
