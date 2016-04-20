package com.oneliang.util.json;

public class JsonException  extends RuntimeException {
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -363814153391892339L;

    /**
     * Constructs a JsonException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
