package net.xici.newapp.data.pojo;

import java.io.Serializable;

public abstract class XiciResponce<T>  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7272200921990105868L;

	public String accesstoken;
	
	public T result;
	
	public int strlen;
	
	public double timespend;
	
}
