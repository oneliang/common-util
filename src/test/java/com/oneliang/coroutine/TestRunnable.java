package com.oneliang.coroutine;

import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TestRunnable implements Runnable {

    private static final Logger logger = LoggerManager.getLogger(TestRunnable.class);
    private Object lock = new Object();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            
        }
    }
}
