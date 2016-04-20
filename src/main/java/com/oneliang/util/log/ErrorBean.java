package com.oneliang.util.log;

public class ErrorBean{

	public static final String ERROR="error";
	public static final String ERROR_LEVEL="ERROR";
	public static final String ERROR_OUTPUT="/log/%d/errorOutput.log";
	public static final String ERROR_PATTERN="[%d] [%p] [%c(%f:%l)] [%m] [%i]%n";
	
	private String level=null;
	private String output=null;
	private String pattern=null;
	private String currentOutput=null;
	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}
	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * @return the currentOutput
	 */
	public String getCurrentOutput() {
		return currentOutput;
	}
	/**
	 * @param currentOutput the currentOutput to set
	 */
	public void setCurrentOutput(String currentOutput) {
		this.currentOutput = currentOutput;
	}
}
