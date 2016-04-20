package com.oneliang.util.upload;

import java.io.Serializable;

public class FileUploadResult implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8411147808240297011L;

	private boolean success=false;
	private String originalFilename=null;
	private String filename=null;
	private String filePath=null;
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * @return the originalFilename
	 */
	public String getOriginalFilename() {
		return originalFilename;
	}
	/**
	 * @param originalFilename the originalFilename to set
	 */
	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
