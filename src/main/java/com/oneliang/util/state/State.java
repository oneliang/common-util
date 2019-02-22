package com.oneliang.util.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.oneliang.util.common.StringUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class State {
    private static final Logger logger = LoggerManager.getLogger(StateMachine.class);

    private int key = 0;
    private String name = StringUtil.BLANK;

    public State(int key, String name) {
        this.key = key;
        this.name = name;
    }

    private Map<Integer, State> previousStateMap = new HashMap<>();
    private Map<Integer, State> nextStateMap = new HashMap<>();

    /**
     * add previous state
     * 
     * @param state
     */
    private void addPreviousState(State state) {
        this.previousStateMap.put(state.key, state);
    }

    /**
     * add next state
     * 
     * @param state
     */
    public void addNextState(State state) {
        this.nextStateMap.put(state.key, state);
        state.addPreviousState(this);
    }

    /**
     * has previous
     * 
     * @return boolean
     */
    public boolean hasPrevious() {
        return !this.previousStateMap.isEmpty();
    }

    /**
     * get previous key set
     * 
     * @return Set<Integer>
     */
    public Set<Integer> getPreviousKeySet() {
        return this.previousStateMap.keySet();
    }

    /**
     * previous
     * 
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    public State previous(int key) throws StateNotFoundException {
        if (previousStateMap.containsKey(key)) {
            return previousStateMap.get(key);
        } else {
            logger.error("previous state key:%s is not exist", key);
            throw new StateNotFoundException(String.format("previous state key:%s", key));
        }
    }

    /**
     * has next
     * 
     * @return boolean
     */
    public boolean hasNext() {
        return !this.nextStateMap.isEmpty();
    }

    /**
     * get next key set
     * 
     * @return Set<Integer>
     */
    public Set<Integer> getNextKeySet() {
        return this.nextStateMap.keySet();
    }

    /**
     * next
     * 
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    public State next(int key) throws StateNotFoundException {
        if (nextStateMap.containsKey(key)) {
            return nextStateMap.get(key);
        } else {
            logger.error("next state key:%s is not exist", key);
            throw new StateNotFoundException(String.format("next state key:%s", key));
        }
    }

    /**
     * get key
     * 
     * @return int
     */
    public int getKey() {
        return this.key;
    }

    /**
     * get name
     * 
     * @return name
     */
    public String getName() {
        return this.name;
    }
}
