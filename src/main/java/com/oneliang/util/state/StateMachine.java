package com.oneliang.util.state;

import java.util.Set;

import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class StateMachine<T extends State> {

    private static final Logger logger = LoggerManager.getLogger(StateMachine.class);
    private final StateMap<T> stateMap;
    private T currentState = null;

    public StateMachine(StateMap<T> stateMap) {
        this.stateMap = stateMap;
        this.currentState = this.stateMap.startState;
    }

    /**
     * has previous state
     * 
     * @return boolean
     */
    public boolean hasPreviousState() {
        if (currentState == null) {
            logger.error("current state is null, so dosen't have previous state");
            return false;
        }
        return this.currentState.hasPrevious();
    }

    /**
     * get previous state key set
     * 
     * @return Set<Integer>
     */
    public Set<Integer> getPreviousStateKeySet() {
        if (currentState == null) {
            logger.error("current state is null, so dosen't have previous state key set");
            return null;
        }
        return this.currentState.getPreviousKeySet();
    }

    /**
     * previous state
     * 
     * @param key
     * @throws StateNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void previousState(int key) throws StateNotFoundException {
        if (currentState == null) {
            logger.error("current state is null");
            return;
        }
        this.currentState = (T) this.currentState.previous(key);
    }

    /**
     * has next state
     * 
     * @return boolean
     */
    public boolean hasNextState() {
        if (currentState == null) {
            logger.error("current state is null, so dosen't have next state");
            return false;
        }
        return this.currentState.hasNext();
    }

    /**
     * get next state key set
     * 
     * @return Set<Integer>
     */
    public Set<Integer> getNextStateKeySet() {
        if (currentState == null) {
            logger.error("current state is null, so dosen't have next state key set");
            return null;
        }
        return this.currentState.getNextKeySet();
    }

    /**
     * next state
     * 
     * @param key
     * @throws StateNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void nextState(int key) throws StateNotFoundException {
        if (currentState == null) {
            logger.error("current state is null");
            return;
        }
        this.currentState = (T) this.currentState.next(key);
    }

    /**
     * @return the currentState
     */
    public T getCurrentState() {
        return currentState;
    }
}
