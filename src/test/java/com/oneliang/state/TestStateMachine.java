package com.oneliang.state;

import java.util.Set;

import com.oneliang.util.logging.BaseLogger;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;
import com.oneliang.util.state.State;
import com.oneliang.util.state.StateMachine;
import com.oneliang.util.state.StateNotFoundException;

public class TestStateMachine {

    static {
        LoggerManager.registerLogger("*", new BaseLogger(Logger.Level.DEBUG));
    }
    private static final Logger logger = LoggerManager.getLogger(TestStateMachine.class);
    static final State startState = new State(0, "start");
    static {
        State state1 = new State(1, "1");
        State state2 = new State(2, "2");
        State state3 = new State(3, "3");
        State state4 = new State(4, "4");
        State state5 = new State(5, "5");
        State state6 = new State(6, "6");
        State state7 = new State(7, "7");
        State state8 = new State(8, "8");
        State state9 = new State(9, "9");
        State finishState = new State(-1, "finish");

        startState.addNextState(state1);
        startState.addNextState(state2);
        state1.addNextState(state3);
        state1.addNextState(state4);
        state2.addNextState(state5);
        state5.addNextState(state6);
        state6.addNextState(state7);
        state5.addNextState(state8);
        state8.addNextState(state9);
        state9.addNextState(finishState);
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

    public static void main(String[] args) throws Exception {
        printState(startState);
        StateMachine stateMachine = new StateMachine(startState);
        while (stateMachine.hasNextState()) {
            Set<Integer> nextStateKeySet = stateMachine.getNextStateKeySet();
            if (nextStateKeySet != null) {
                for (Integer nextStateKey : nextStateKeySet) {
                    logger.info("next state key:%s", nextStateKey);
                    try {
                        stateMachine.nextState(nextStateKey);
                    } catch (StateNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } else {
                logger.error("next state key is null");
            }
        }
    }
}
