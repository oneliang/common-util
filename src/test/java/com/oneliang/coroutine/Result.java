package com.oneliang.coroutine;

public class Result<T> {

    private T value = null;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }
}
