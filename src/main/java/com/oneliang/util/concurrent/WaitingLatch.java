package com.oneliang.util.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WaitingLatch {

    private Queue<Runnable> runnableQueue = new ConcurrentLinkedQueue<Runnable>();
    private Queue<Thread> threadQueue = new ConcurrentLinkedQueue<Thread>();
    private boolean finish = false;

    public void addRunnable(Runnable runnable) {
        runnableQueue.offer(runnable);
    }

    public void startAll() {
        for (Runnable runnable : runnableQueue) {
            Thread thread = new Thread(runnable);
            thread.start();
            threadQueue.add(thread);
        }
    }

    public void waiting() {
        while (!finish) {
            finish = true;
            for (Thread thread : threadQueue) {
                if (thread.isAlive()) {
                    finish = false;
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        WaitingLatch pool = new WaitingLatch();
        pool.addRunnable(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("run 1");
            }
        });
        pool.addRunnable(new Runnable() {
            public void run() {
                System.out.println("run 2");
            }
        });
        pool.addRunnable(new Runnable() {
            public void run() {
                System.out.println("run 3");
            }
        });
        pool.addRunnable(new Runnable() {
            public void run() {
                System.out.println("run 4");
            }
        });
        pool.startAll();
        pool.waiting();
        System.out.println("---all finish---");
    }
}