package com.oneliang.state;

import com.oneliang.util.state.State;

public class StateRun extends State {

    public final Runnable runnable;

    public StateRun(int key, String name, Runnable runnable) {
        super(key, name);
        this.runnable = runnable;
    }
}
