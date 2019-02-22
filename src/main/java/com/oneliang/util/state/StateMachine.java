package com.oneliang.util.state;

import java.util.Set;

import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class StateMachine {

    private static final Logger logger = LoggerManager.getLogger(StateMachine.class);
    private final State startState;
    private State currentState = null;

    public StateMachine(State startState) {
        this.startState = startState;
        this.currentState = this.startState;
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
    public void previousState(int key) throws StateNotFoundException {
        if (currentState == null) {
            logger.error("current state is null");
            return;
        }
        this.currentState = this.currentState.previous(key);
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
    public void nextState(int key) throws StateNotFoundException {
        if (currentState == null) {
            logger.error("current state is null");
            return;
        }
        this.currentState = this.currentState.next(key);
    }

    /**
     * @return the startState
     */
    public State getStartState() {
        return startState;
    }

    /**
     * @return the currentState
     */
    public State getCurrentState() {
        return currentState;
    }
}
