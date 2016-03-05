package com.litedbAdvanced.netio;

/*
 * @author Newnius
 *
 * 
 * */

public class Message {
	public static final int CONNECTION_CREATED = 0;
	public static final int CONNECTION_CLOSED = 1;
	
	
	/*
	 * 
	 * 
	 * */
	private int errno;
	private String msg;
	
	public Message(int errno, String msg){
		this.errno = errno;
		this.msg = msg;
	}
	
	public int getErrno() {
		return errno;
	}
	
	public String getMsg() {
		return msg;
	}

	
}
