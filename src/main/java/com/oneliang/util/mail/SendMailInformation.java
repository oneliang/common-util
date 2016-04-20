package com.oneliang.util.mail;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SendMailInformation{

	private String host=null;
	private String fromAddress=null;
	private String user=null;
	private String password=null;
	private List<ToAddress> toAddressList=new CopyOnWriteArrayList<ToAddress>();
	private String subject=null;
	private String content=null;
	private List<String> accessoryPathList=new CopyOnWriteArrayList<String>();
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}
	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the toAddressList
	 */
	public List<ToAddress> getToAddressList() {
		return toAddressList;
	}
	/**
	 * add to address
	 * @param toAddress
	 * @return boolean
	 */
	public boolean addToAddress(ToAddress toAddress) {
		return this.toAddressList.add(toAddress);
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @param accessoryPath
	 * @return boolean
	 */
	public boolean addAccessoryPath(String accessoryPath){
		return this.accessoryPathList.add(accessoryPath);
	}
	/**
	 * @return the accessoryPathList
	 */
	public List<String> getAccessoryPathList() {
		return this.accessoryPathList;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
}
