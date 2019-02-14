package com.oneliang.coroutine;

import com.oneliang.util.concurrent.ThreadTask;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TestThreadTask implements ThreadTask {

    private static final Logger logger = LoggerManager.getLogger(TestThreadTask.class);
    private Object lock = new Object();

    @Override
    public void runTask() {
    }
}
