package com.oneliang.util.mail;

public class ToAddress{

	private Type type=Type.TO;
	private String address=null;

	public ToAddress(String address){
		this.address=address;
	}

	public ToAddress(Type type,String address) {
		this.type=type;
		this.address=address;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	public static enum Type {
		TO,BCC,CC
	}
}
