package edu.hue.community;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author 47552
 * @date 2021/09/21
 */
public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue<>(10);
        new Thread(new producer(queue)).start();
        new Thread(new consumer(queue)).start();
        new Thread(new consumer(queue)).start();
        new Thread(new consumer(queue)).start();
    }
}

class producer implements Runnable {
    private BlockingQueue<Integer> blockingQueue;

    public producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(200);
                blockingQueue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + blockingQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class consumer implements Runnable {

    private BlockingQueue<Integer> blockingQueue;

    public consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                blockingQueue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + blockingQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
