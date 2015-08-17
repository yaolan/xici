package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.xici.newapp.support.util.MyAsyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

public class AddMediaItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9019026504220237037L;
	
	public String path;//当前路径，拷贝压缩等处理
	
	public String imageId;
	public String originalUri; //原始路径
	public String originalPath; //原始路径
	public String thumbnailPath; //原始缩略图路径
	public int mOrientation;
	
	public String url;    //网络路径
	
	public boolean sended = false;
	
	public View view;
	
	public Bitmap bitmap;
	
	public boolean isSelected = false;
	
	public MyAsyncTask<?, ?, ?> uploadtask;
	
	public static class AddMediaItemSerializable implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 3537045021731139811L;
		
		public LinkedHashMap<String,AddMediaItem> map;
		
		public AddMediaItemSerializable(){
			map = new LinkedHashMap<String, AddMediaItem>();
		}
		public AddMediaItemSerializable(LinkedHashMap<String, AddMediaItem> map){
			this.map = map;
		}
	}
	
	public static class SimpleMedia{
		


		public String path;//当前路径，拷贝压缩等处理
		
		public String imageId;
		public String originalUri; //原始路径
		public String originalPath; //原始路径
		public String thumbnailPath; //原始缩略图路径
		public int mOrientation;
		
		public String url;    //网络路径
		
		public boolean sended = false;
		

		@Override
		public String toString() {
			return "SimpleMedia [path=" + path + ", imageId=" + imageId
					+ ", originalUri=" + originalUri + ", originalPath="
					+ originalPath + ", thumbnailPath=" + thumbnailPath
					+ ", mOrientation=" + mOrientation + ", url=" + url
					+ ", sended=" + sended + "]";
		}
		
		
	}
	
	public AddMediaItem(){
		
	}
	
    public AddMediaItem(SimpleMedia media){
    	
    	path = media.path ;
    	imageId = media.imageId;
    	originalUri = media.originalUri;
    	originalPath = media.originalPath;
    	thumbnailPath = media.thumbnailPath;

    	mOrientation = media.mOrientation;
    	url = media.url;
    	sended = media.sended;
		
	}
	
	public SimpleMedia getsimpleMedia(){
		
		SimpleMedia media = new SimpleMedia();
		media.path = path;
		media.imageId = imageId;
		media.originalUri = originalUri;
		media.originalPath = originalPath;
		media.thumbnailPath = thumbnailPath;

		media.mOrientation = mOrientation;
		media.url = url;
		media.sended = sended;
		return media;
	}

}
