package com.oneliang.util.state;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class StateMap<T extends State> {

    private static final Logger logger = LoggerManager.getLogger(StateMap.class);
    private final Map<Integer, T> stateMap = new ConcurrentHashMap<>();
    public final T startState;

    public StateMap(T startState) {
        this.startState = startState;
        this.stateMap.put(this.startState.getKey(), this.startState);
    }

    public void addNextState(int key, T nextState) {
        if (stateMap.containsKey(key)) {
            T previousState = stateMap.get(key);
            previousState.addNextState(nextState);
            stateMap.put(nextState.getKey(), nextState);
        } else {
            logger.error("state key:%s is not exist", key);
        }
    }

    public void printState() {
        try {
            printState(this.startState);
        } catch (Exception e) {
            logger.error("state not found", e);
        }
    }

    private static void printState(State startState) throws Exception {
        logger.info("key:%s, name:%s", startState.getKey(), startState.getName());
        if (startState.hasNext()) {
            Set<Integer> nextStateKeySet = startState.getNextKeySet();
            for (Integer nextKey : nextStateKeySet) {
                State nextState = startState.next(nextKey);
                printState(nextState);
            }
        }
    }
}
