package com.oneliang.util.task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiTaskNodeRunnable implements Runnable {

    private List<TaskNodeRunnable> taskNodeRunnableList = new CopyOnWriteArrayList<TaskNodeRunnable>();

    public void run() {
        if (this.taskNodeRunnableList != null) {
            for (TaskNodeRunnable taskNodeRunnable : this.taskNodeRunnableList) {
                taskNodeRunnable.run();
            }
        }
    }

    /**
     * add task node runnable
     * 
     * @param taskNodeRunnable
     * @return boolean
     */
    public boolean addTaskNodeRunnable(TaskNodeRunnable taskNodeRunnable) {
        return this.taskNodeRunnableList.add(taskNodeRunnable);
    }
}
