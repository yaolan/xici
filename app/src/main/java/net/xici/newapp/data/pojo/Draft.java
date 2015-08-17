package net.xici.newapp.data.pojo;

import java.util.List;

public class Draft {
	

	@Override
	public String toString() {
		return "Draft [deafttype=" + deafttype + ", bid=" + bid + ", title="
				+ title + ", content=" + content + ", tid=" + tid + ", userid="
				+ userid + ", listMediaItems=" + listMediaItems + "]";
	}

	public final static int DRAFTTYPE_MAIL = 0; //飞语
	
	public final static int DRAFTTYPE_FLOOR = 1; //回复

	public final static int DRAFTTYPE_FLOOR_FOLLOW = 2;// 快速回复
	
	public final static int DRAFTTYPE_DOC_PUT = 3;// 发帖
	
	public int deafttype = 0;
	public int bid;
	public String title;
	public String content;
	public int tid;
	public long userid;
	
	public List<AddMediaItem.SimpleMedia> listMediaItems;

}
