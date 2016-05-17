package com.oneliang.util.log;

@Deprecated
public class LoggerBean{

	public static final String TAG_LOGGER="logger";

	private String id=null;
	private String target=null;
	private String method=null;
	private boolean debug=true;
	private LevelBean level=null;
	private OutputBean Output=null;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	/**
	 * @return the level
	 */
	public LevelBean getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(LevelBean level) {
		this.level = level;
	}
	/**
	 * @return the output
	 */
	public OutputBean getOutput() {
		return Output;
	}
	/**
	 * @param output the output to set
	 */
	public void setOutput(OutputBean output) {
		Output = output;
	}
}
