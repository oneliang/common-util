package com.oneliang.coroutine;

import com.oneliang.util.concurrent.DefaultDispatcher;
import com.oneliang.util.concurrent.Dispatcher;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TestRunnable implements Runnable {

    private static final Logger logger = LoggerManager.getLogger(TestRunnable.class);
    private Object lock = new Object();
    private Dispatcher<Job> dispatcher = new DefaultDispatcher<>();

    public void addJob(Job job) {
        this.dispatcher.offer(job);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            while (!this.dispatcher.isEmpty()) {
                Job job = this.dispatcher.poll();
                Job nextJob = job.execute();
                if (nextJob != null) {
                    addJob(nextJob);
                }
            }
        }
    }
}
