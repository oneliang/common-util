package com.oneliang.util.concurrent;

public interface Dispatcher<T> extends Iterable<T> {
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
