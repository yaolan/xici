package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BoardSort implements Serializable{
	
	public int maxindex;
	
	public Map<String, Integer> sortMap;
	
	public BoardSort(){
		sortMap = new HashMap<String, Integer>();
	}

}
