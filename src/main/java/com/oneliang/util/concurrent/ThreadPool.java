package com.oneliang.util.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.Constants;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public final class ThreadPool implements Runnable {

    private static final Logger logger = LoggerManager.getLogger(ThreadPool.class);

    private int minThreads = 0;
    private int maxThreads = 1;
    private int totalTaskCount = 0;
    private int currentTaskCount = 0;
    private InnerThread[] allInnerThread = null;
    private Dispatcher<ThreadTask> dispatcher = new DefaultDispatcher<ThreadTask>();
    private DaemonThread daemonThread = null;
    private Thread thread = null;
    private Processor processor = null;

    private void initialPool() {
        this.daemonThread = new DaemonThread();
        this.daemonThread.start();
        this.allInnerThread = new InnerThread[this.maxThreads];
        for (int i = 0; i < this.minThreads; i++) {
            InnerThread innerThread = new InnerThread(this);
            innerThread.start();
            this.allInnerThread[i] = innerThread;
            this.daemonThread.addInnerThread(innerThread);
        }
    }

    public void run() {
        initialPool();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!this.dispatcher.isEmpty()) {
                    int index = 0;
                    boolean hasAllInnerThreadBusy = true;
                    for (InnerThread innerThread : this.allInnerThread) {
                        if (innerThread != null && innerThread.isIdle()) {
                            hasAllInnerThreadBusy = false;
                            if (this.processor != null) {
                                this.processor.beforeRunTaskProcess(this.dispatcher);
                            }
                            ThreadTask threadTask = this.dispatcher.poll();
                            if (threadTask != null) {
                                logger.verbose("second");
                                innerThread.setCurrentThreadTask(threadTask);
                            }
                        } else if (innerThread == null) {
                            hasAllInnerThreadBusy = false;
                            if (this.processor != null) {
                                this.processor.beforeRunTaskProcess(this.dispatcher);
                            }
                            ThreadTask threadTask = this.dispatcher.poll();
                            if (threadTask != null) {
                                logger.verbose("first");
                                innerThread = new InnerThread(this);
                                innerThread.setCurrentThreadTask(threadTask);
                                innerThread.start();
                                this.allInnerThread[index] = innerThread;
                                this.daemonThread.addInnerThread(innerThread);
                            }
                        }
                        index++;
                    }
                    if (hasAllInnerThreadBusy) {
                        synchronized (this) {
                            logger.verbose("All inner thread busy,waiting");
                            this.wait();
                        }
                        logger.verbose("after wait");
                    }
                } else {
                    synchronized (this) {
                        logger.verbose("waiting");
                        this.wait();
                    }
                    logger.verbose("after wait");
                }
            } catch (InterruptedException e) {
                logger.verbose("Thread pool need to interrupt:" + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error(Constants.Base.EXCEPTION, e);
            }
        }
    }

    /**
     * start
     */
    public synchronized void start() {
        if (this.thread == null) {
            this.thread = new Thread(this);
            this.thread.setPriority(Thread.NORM_PRIORITY);
            this.thread.start();
        }
    }

    /**
     * real interrupt
     */
    public void interrupt() {
        if (this.allInnerThread != null) {
            int i = 0;
            for (InnerThread innerThread : this.allInnerThread) {
                if (innerThread != null) {
                    innerThread.interrupt();
                    this.allInnerThread[i] = null;
                }
                i++;
            }
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        if (this.daemonThread != null) {
            this.daemonThread.interrupt();
            this.daemonThread = null;
        }
        this.dispatcher.clear();
    }

    /**
     * set dispatcher
     * 
     * @param dispatcher
     */
    public void setDispatcher(Dispatcher<ThreadTask> dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * @return the minThreads
     */
    public int getMinThreads() {
        return minThreads;
    }

    /**
     * @param minThreads
     *            the minThreads to set
     */
    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    /**
     * @return the maxThreads
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * @param maxThreads
     *            the maxThreads to set
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * @param threadTask
     *            the threadTask to add
     */
    public void addThreadTask(ThreadTask threadTask) {
        if (threadTask != null) {
            this.dispatcher.offer(threadTask);
            synchronized (this) {
                this.totalTaskCount++;
                this.notify();
            }
        }
    }

    /**
     * count current task
     */
    private void countCurrentTask() {
        this.currentTaskCount++;
    }

    /**
     * finalize
     */
    protected void finalize() throws Throwable {
        super.finalize();
        this.interrupt();
    }

    private static class InnerThread implements Runnable {

        private static final Logger logger = LoggerManager.getLogger(InnerThread.class);

        private ThreadPool threadPool = null;
        private long beginTimeMillis = 0;
        private long finishedTimeMillis = 0;
        private ThreadTask currentThreadTask = null;
        private int finishedCount = 0;
        private int executeCount = 0;
        private Thread thread = null;

        private InnerThread(ThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        /**
         * start the thread to run task
         */
        public synchronized void start() {
            if (this.thread == null) {
                this.thread = new Thread(this);
                this.thread.setPriority(Thread.MIN_PRIORITY);
                this.thread.start();
            }
        }

        /**
         * interrupt
         */
        public void interrupt() {
            if (this.thread != null) {
                this.thread.interrupt();
                this.thread = null;
            }
        }

        /**
         * when thread is dead restart the thread
         */
        public void restart() {
            synchronized (this) {
                if (this.thread != null && this.thread.getState() == Thread.State.TERMINATED) {
                    this.thread = new Thread(this);
                    this.thread.setPriority(Thread.MIN_PRIORITY);
                    this.thread.start();
                }
            }
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (this) {
                        if (this.currentThreadTask != null) {
                            logger.verbose(this + "--begin--");
                            this.beginTimeMillis = System.currentTimeMillis();
                            try {
                                this.currentThreadTask.runTask();
                            } finally {
                                this.finishedTimeMillis = System.currentTimeMillis();
                                this.finishedCount++;
                                this.currentThreadTask = null;
                                logger.verbose(this + "--end--cost:" + (this.finishedTimeMillis - this.beginTimeMillis));
                            }
                        }
                        if (this.threadPool != null) {
                            synchronized (this.threadPool) {
                                this.threadPool.countCurrentTask();
                                this.threadPool.notify();
                            }
                        }
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    logger.verbose("Inner thread need to interrupt:" + Thread.currentThread().getName() + ",message:" + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error(Constants.Base.EXCEPTION, e);
                }
            }
        }

        /**
         * @param currentThreadTask
         *            the currentThreadTask to set
         */
        public void setCurrentThreadTask(ThreadTask currentThreadTask) {
            synchronized (this) {
                this.executeCount++;
                this.currentThreadTask = currentThreadTask;
                this.notify();
            }
        }

        /**
         * @return the finishedCount
         */
        public int getFinishedCount() {
            return this.finishedCount;
        }

        /**
         * @return the idle
         */
        public boolean isIdle() {
            return this.currentThreadTask == null ? true : false;
        }

        /**
         * @return the executeCount
         */
        public int getExecuteCount() {
            return executeCount;
        }

        /**
         * @return the thread
         */
        public Thread.State getThreadState() {
            Thread.State state = Thread.State.TERMINATED;
            if (this.thread != null) {
                state = this.thread.getState();
            }
            return state;
        }
    }

    private static class DaemonThread implements Runnable {
        private static final Logger logger = LoggerManager.getLogger(DaemonThread.class);
        private static final long THREAD_WAIT_TIME = 5000;

        private Queue<InnerThread> innerThreadQueue = new ConcurrentLinkedQueue<InnerThread>();
        private Thread thread = null;

        /**
         * start
         */
        public synchronized void start() {
            if (this.thread == null) {
                this.thread = new Thread(this);
                this.thread.setPriority(Thread.NORM_PRIORITY);
                this.thread.start();
            }
        }

        /**
         * interrupt
         */
        public void interrupt() {
            if (this.thread != null) {
                this.thread.interrupt();
                this.thread = null;
            }
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (!innerThreadQueue.isEmpty()) {
                        for (InnerThread innerThread : innerThreadQueue) {
                            Thread.State state = innerThread.getThreadState();
                            if (state == Thread.State.TERMINATED) {
                                innerThread.restart();
                            }
                            logger.verbose(innerThread.toString() + "---" + innerThread.isIdle() + "---execute:" + innerThread.getExecuteCount() + "---finished:" + innerThread.getFinishedCount());
                        }
                        synchronized (this) {
                            this.wait(THREAD_WAIT_TIME);
                        }
                    } else {
                        synchronized (this) {
                            this.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    logger.verbose("Daemon thread need to interrupt:" + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error(Constants.Base.EXCEPTION, e);
                }
            }
        }

        public void addInnerThread(InnerThread innerThread) {
            if (innerThread != null) {
                this.innerThreadQueue.add(innerThread);
                synchronized (this) {
                    this.notify();
                }
            }
        }
    }

    /**
     * @param processor
     *            the processor to set
     */
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public static interface Processor {
        /**
         * before run task
         * 
         * @param dispatcher
         */
        public abstract void beforeRunTaskProcess(Dispatcher<ThreadTask> dispatcher);
    }

    public static interface Dispatcher<T> extends Iterable<T> {
        /**
         * offer
         * 
         * @param e
         * @return boolean
         */
        public abstract boolean offer(T t);

        /**
         * poll, will remove
         * 
         * @return T
         */
        public abstract T poll();

        /**
         * peek, but do not remove
         * 
         * @return T
         */
        public abstract T peek();

        /**
         * isEmpty
         * 
         * @return boolean
         */
        public abstract boolean isEmpty();

        /**
         * size
         * 
         * @return int
         */
        public abstract int size();

        /**
         * clear
         */
        public abstract void clear();
    }
}