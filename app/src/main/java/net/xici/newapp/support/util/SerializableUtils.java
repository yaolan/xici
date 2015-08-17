package net.xici.newapp.support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public class SerializableUtils {
	
	public static<T extends Serializable> byte[] toByteArray(T obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutput out = null;
		try {
			bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(obj);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}
	
	public static<T extends Serializable> T fromBytes(byte[] bytes) {
		T t = null;
		ByteArrayInputStream bis = null;
		ObjectInput  in = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(bis);
			t = (T) in.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return t;
	}
}
