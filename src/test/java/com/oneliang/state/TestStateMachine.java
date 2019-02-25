package com.oneliang.state;

import java.util.Set;

import com.oneliang.util.logging.BaseLogger;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;
import com.oneliang.util.state.State;
import com.oneliang.util.state.StateMachine;
import com.oneliang.util.state.StateMap;
import com.oneliang.util.state.StateNotFoundException;

public class TestStateMachine {

    static {
        LoggerManager.registerLogger("*", new BaseLogger(Logger.Level.DEBUG));
    }
    private static final Logger logger = LoggerManager.getLogger(TestStateMachine.class);
    static final StateRun startState = new StateRun(0, "start", new Runnable() {
        public void run() {
            logger.info("start");
        }
    });
    static final StateMap<StateRun> stateMap = new StateMap<>(startState);
    static {
        StateRun state1 = new StateRun(1, "1", new Runnable() {
            public void run() {
                logger.info("1");
            }
        });
        StateRun state2 = new StateRun(2, "2", new Runnable() {
            public void run() {
                logger.info("2");
            }
        });
        StateRun state3 = new StateRun(3, "3", new Runnable() {
            public void run() {
                logger.info("3");
            }
        });
        StateRun state4 = new StateRun(4, "4", new Runnable() {
            public void run() {
                logger.info("4");
            }
        });
        StateRun state5 = new StateRun(5, "5", new Runnable() {
            public void run() {
                logger.info("5");
            }
        });
        StateRun state6 = new StateRun(6, "6", new Runnable() {
            public void run() {
                logger.info("6");
            }
        });
        StateRun state7 = new StateRun(7, "7", new Runnable() {
            public void run() {
                logger.info("7");
            }
        });
        StateRun state8 = new StateRun(8, "8", new Runnable() {
            public void run() {
                logger.info("8");
            }
        });
        StateRun state9 = new StateRun(9, "9", new Runnable() {
            public void run() {
                logger.info("9");
            }
        });
        StateRun finishState = new StateRun(-1, "finish", new Runnable() {
            public void run() {
                logger.info("-1");
            }
        });

        stateMap.addNextState(startState.getKey(), state1);
        stateMap.addNextState(startState.getKey(), state2);
        stateMap.addNextState(state1.getKey(), state3);
        stateMap.addNextState(state1.getKey(), state4);
        stateMap.addNextState(state2.getKey(), state5);
        stateMap.addNextState(state5.getKey(), state6);
        stateMap.addNextState(state6.getKey(), state7);
        stateMap.addNextState(state5.getKey(), state8);
        stateMap.addNextState(state8.getKey(), state9);
        stateMap.addNextState(state9.getKey(), finishState);
    }

    public static void main(String[] args) throws Exception {
        stateMap.printState();
        StateMachine<StateRun> stateMachine = new StateMachine<>(stateMap);
        stateMachine.getCurrentState().runnable.run();
        while (stateMachine.hasNextState()) {
            Set<Integer> nextStateKeySet = stateMachine.getNextStateKeySet();
            if (nextStateKeySet != null) {
                for (Integer nextStateKey : nextStateKeySet) {
                    logger.info("next state key:%s", nextStateKey);
                    try {
                        stateMachine.nextState(nextStateKey);
                        stateMachine.getCurrentState().runnable.run();
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
