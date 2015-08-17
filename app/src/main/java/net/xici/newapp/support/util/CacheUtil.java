package net.xici.newapp.support.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.xici.newapp.app.XiciApp;

import android.content.Context;

public class CacheUtil 
{
	public static final int PAGE_SIZE = 25;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	public static boolean isExistDataCache(Context context,String cachefile)
	{
		boolean exist = false;
		File data = context.getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public static boolean isCacheDataFailure(Context context,String cachefile)
	{
		boolean failure = false;
		File data = context.getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	public static int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public static void setDiskCache(Context context,String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = context.openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getDiskCache(Context context,String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = context.openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public static boolean saveObject(Context context,Object obj, String file) {
		FileOutputStream fos = null;
		try{
			fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			FileUtil.saveObject(obj, fos);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Object readObject(Context context,String file){
		if(!isExistDataCache(context,file))
			return null;
		FileInputStream fis = null;
		try{
			fis = context.openFileInput(file);
			 return FileUtil.loadObject(fis);
		}catch(FileNotFoundException e){
		}catch(Exception e){
		}
		return null;
	}
	
	public static String getKeyPre()
	{
		if(XiciApp.account!=null)
		{
			return String.valueOf(XiciApp.account.getUserid())+"_";
		}
		else {
			return "";
		}
	}

}
