package com.oneliang.coroutine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.util.common.StringUtil;
import com.oneliang.util.concurrent.ThreadPool;
import com.oneliang.util.logging.AbstractLogger;
import com.oneliang.util.logging.BaseLogger;
import com.oneliang.util.logging.ComplexLogger;
import com.oneliang.util.logging.FileLogger;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TestThreadPool {

    static {
        String projectRealPath = new File(StringUtil.BLANK).getAbsolutePath();
        List<AbstractLogger> loggerList = new ArrayList<AbstractLogger>();
        loggerList.add(new BaseLogger(Logger.Level.VERBOSE));
        loggerList.add(new FileLogger(Logger.Level.VERBOSE, new File(projectRealPath + "/log/default.log")));
        Logger logger = new ComplexLogger(Logger.Level.VERBOSE, loggerList);
        LoggerManager.registerLogger("*", logger);
    }

    private static final Logger logger = LoggerManager.getLogger(TestThreadPool.class);

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool();
        threadPool.setMinThreads(1);
        threadPool.setMaxThreads(1);
        threadPool.start();
        threadPool.addRunnable(new Runnable() {
            public void run() {
                logger.info("aaabbbccc");
            }
        });
    }
}
