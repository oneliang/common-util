package com.oneliang.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThreadPoolExecutor {

    public static void main(String[] args) throws Exception {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(64);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 6, 120, TimeUnit.SECONDS, workQueue);
        threadPoolExecutor.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread.sleep(200);
        threadPoolExecutor.shutdown();
        System.out.println("111");
        Thread.sleep(2000);
        threadPoolExecutor.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
