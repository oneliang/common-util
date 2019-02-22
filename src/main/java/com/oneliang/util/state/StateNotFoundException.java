package com.oneliang.util.state;

public class StateNotFoundException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4900285939921445989L;

    public StateNotFoundException() {
        super();
    }

    public StateNotFoundException(String message) {
        super(message);
    }

    public StateNotFoundException(Throwable cause) {
        super(cause);
    }

    public StateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
