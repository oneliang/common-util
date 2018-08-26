package com.oneliang.util.container;

public interface ModelContainer<MODEL> {

    /**
     * add model
     * 
     * @param model
     * @return boolean
     */
    public abstract boolean add(MODEL model);

    /**
     * remove model
     * 
     * @param model
     * @return boolean
     */
    public abstract boolean remove(MODEL model);

    /**
     * get model
     * 
     * @param index
     * @return MODEL
     */
    public abstract MODEL get(int index);

    /**
     * size
     * 
     * @return int
     */
    public abstract int size();
}
