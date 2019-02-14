package com.oneliang.util.concurrent;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.util.concurrent.ThreadPool.Dispatcher;

public class DefaultDispatcher<T> implements Dispatcher<T> {

    private Queue<T> queue = new ConcurrentLinkedQueue<>();

    @Override
    public boolean offer(T t) {
        return this.queue.offer(t);
    }

    @Override
    public T poll() {
        return this.queue.poll();
    }

    @Override
    public T peek() {
        return this.queue.peek();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public Iterator<T> iterator() {
        return this.queue.iterator();
    }

    @Override
    public void clear() {
        this.queue.clear();
    }
}
