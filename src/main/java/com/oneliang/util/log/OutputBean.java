package com.oneliang.util.log;

@Deprecated
public class OutputBean{

	public static final String OUTPUT="output";
	
	private String id=null;
	private String file=null;
	private String currentFile=null;
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
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	/**
	 * @return the currentFile
	 */
	public String getCurrentFile() {
		return currentFile;
	}
	/**
	 * @param currentFile the currentFile to set
	 */
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}
}
