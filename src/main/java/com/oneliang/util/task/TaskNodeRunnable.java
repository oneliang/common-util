package com.oneliang.util.task;

import com.oneliang.Constants;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TaskNodeRunnable implements Runnable {

    private static final Logger logger = LoggerManager.getLogger(TaskNodeRunnable.class);

    private TaskEngine taskEngine = null;
    private TaskNode taskNode = null;

    public TaskNodeRunnable(TaskEngine taskEngine, TaskNode taskNode) {
        this.taskEngine = taskEngine;
        this.taskNode = taskNode;
    }

    public void run() {
        long begin = System.currentTimeMillis();
        logger.info(taskNode.getName() + " ready");
        while (!taskNode.isAllParentFinished()) {
            synchronized (taskNode) {
                try {
                    taskNode.wait();
                } catch (InterruptedException e) {
                    logger.error(Constants.Base.EXCEPTION, e);
                }
            }
        }
        long runBegin = System.currentTimeMillis();
        try {
            if (taskNode.getRunnable() != null) {
                logger.info(taskNode.getName() + " start");
                taskNode.getRunnable().run();
            }
        } catch (Exception e) {
            logger.error(Constants.Base.EXCEPTION, e);
            taskEngine.setSuccessful(false);
        } finally {
            taskNode.setFinished(true);
        }
        long runCost = System.currentTimeMillis() - runBegin;
        if (taskNode.getChildTaskNodeList() != null && !taskNode.getChildTaskNodeList().isEmpty()) {
            for (TaskNode childTaskNode : taskNode.getChildTaskNodeList()) {
                // taskEngine.executeTaskNode(childTaskNode);
                synchronized (childTaskNode) {
                    childTaskNode.notify();
                }
            }
        }
        long taskCost = System.currentTimeMillis() - begin;
        logger.info(taskNode.getName() + " end,task cost:" + taskCost + ",run cost:" + runCost + ",waiting:" + (taskCost - runCost));
        this.taskNode.setRunCostTime(runCost);
        if (this.taskEngine.isDefaultMode()) {
            if (this.taskEngine.isAllTaskNodeFinished()) {
                synchronized (this.taskEngine) {
                    this.taskEngine.notify();
                }
            }
        }
    }
}
