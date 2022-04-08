package us.codecraft.webmagic.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br><br>
 * Use {@link java.util.concurrent.ExecutorService} as inner implement. <br><br>
 * New feature: <br><br>
 * 1. Block when thread pool is full to avoid poll many urls without process. <br><br>
 * 2. Count of thread alive for monitor.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class CountableThreadPool {

    private int threadNum;
    
    private ExecutorService executorService;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    /**
     * Creates a CountableThreadPool with a thread number.
     * @param threadNum the thread number
     */
    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }

    /**
     * Creates a CountableThreadPool with a thread number and an executor service
     * @param threadNum the thread number
     * @param executorService the executor service
     */
    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    /**
     * Sets executor service.
     * @param executorService the executor service to set as an attribute
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Gets the thread alive.
     * @return the thread alive.
     */
    public int getThreadAlive() {
        return threadAlive.get();
    }

    /**
     * Gets the thread number.
     * @return the thread number.
     */
    public int getThreadNum() {
        return threadNum;
    }

    /**
     * Executes a thread.
     * @param runnable the unique runnable to run
     */
    public void execute(final Runnable runnable) {


        if (threadAlive.get() >= threadNum) {
            try {
                reentrantLock.lock();
                while (threadAlive.get() >= threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    try {
                        reentrantLock.lock();
                        threadAlive.decrementAndGet();
                        condition.signal();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }
        });
    }

    /**
     * Tells whether the executor service is shut or not.
     * @return true if the executor service is shut, false otherwise.
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    /**
     * Shuts the executor service.
     */
    public void shutdown() {
        executorService.shutdown();
    }


}
