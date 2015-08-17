package net.xici.newapp.support.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.xici.newapp.data.pojo.ThumbnailSize;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitmapUtil {
	
	public static final int BITMAP_SIZE_HIGHT = 960;
	public static final int BITMAP_SIZE_MID = 960;
	public static final int BITMAP_SIZE_LOW = 960;
	
	public static final int BITMAP_QUALITY_HIGHT = 85;
	public static final int BITMAP_QUALITY_MID = 75;
	public static final int BITMAP_QUALITY_LOW = 65;

	public static void compress(File file,int size,int quality) throws Exception {
		// 压缩大小
		
			FileInputStream stream = new FileInputStream(file);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);// 此时返回bm为空
			stream.close();
			opts.inJustDecodeBounds = false;
			// 计算缩放比
			int be = (int) (opts.outWidth / (float) size)+1;
			if (be <= 0)
				be = 1;
			opts.inSampleSize = be;
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
			stream = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(stream, null, opts);
			stream.close();
			
			String orientation = "";
			orientation = getExifOrientation(file.getAbsolutePath(), orientation);
			// Create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// Resize the bitmap
			if ((orientation != null)
					&& (orientation.equals("90") || orientation.equals("180") || orientation
							.equals("270"))) {
				matrix.postRotate(Integer.valueOf(orientation));
			}

			Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			
			// 删除文件
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outstream = new FileOutputStream(file);
			if (rotated.compress(Bitmap.CompressFormat.JPEG, quality, outstream)) {
				outstream.flush();
				outstream.close();
			}
			bitmap.recycle(); // free up memory
	}
	
//	public static void compress2(Activity context,String imgPath) throws IOException {
//		ImageHelper ih = new ImageHelper();
//		Display display = context.getWindowManager().getDefaultDisplay();
//		int width = display.getWidth();
//		int height = display.getHeight();
//		if (width > height)
//			width = height;
//
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inJustDecodeBounds = true;
//		
//		File jpeg = new File(imgPath);
//		byte[] bytes = new byte[(int) jpeg.length()];
//		DataInputStream in = null;
//		try {
//			in = new DataInputStream(
//					new FileInputStream(jpeg));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		try {
//			in.readFully(bytes);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try {
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
//
//		float conversionFactor = 0.25f;
//
//		if (opts.outWidth > opts.outHeight)
//			conversionFactor = 0.40f;
//		
//		String orientation = "";
//		orientation = ih.getExifOrientation(imgPath, orientation);
//		
//		byte[] finalBytes = ih.createThumbnail(bytes,
//				String.valueOf((int) (width * conversionFactor)),
//				orientation, true);
//		
//		if(jpeg.delete()) {
//			if(jpeg.createNewFile()) {
//				FileOutputStream outstream = new FileOutputStream(jpeg);
//				outstream.write(finalBytes, 0, finalBytes.length);
//				outstream.flush();
//				outstream.close();
//			}
//		}
//	}
	
//	public static String saveImage(Context context, Uri uri) throws Exception {
//		try {
//			String filePath = FileUtil.getPhotoPath() + File.separator + FileUtil.getfilename() + ".jpg";
//			InputStream myInput = context.getContentResolver().openInputStream(uri);
//
//			File file = new File(filePath);
//			FileOutputStream myOutput = new FileOutputStream(file);
//
//			FileUtil.copy(myInput, myOutput);
//			//compress
//			BitmapUtil.compress(file,BitmapUtil.BITMAP_SIZE_HIGHT,BitmapUtil.BITMAP_QUALITY);
//			return filePath;
//		} catch (IOException e) {
//			Log.e("BitmapUtil", "saveimage fail", e);
//		}
//		return null;
//	}
	
	public  static String getExifOrientation(String path, String orientation) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			return orientation;
		}
		String exifOrientation = exif
				.getAttribute(ExifInterface.TAG_ORIENTATION);
		if (exifOrientation != null) {
			if (exifOrientation.equals("1")) {
				orientation = "0";
			} else if (exifOrientation.equals("3")) {
				orientation = "180";
			} else if (exifOrientation.equals("6")) {
				orientation = "90";
			} else if (exifOrientation.equals("8")) {
				orientation = "270";
			}
		} else {
			orientation = "0";
		}
		return orientation;
	}
	
	public static ThumbnailSize getSize(String path){
		
		ThumbnailSize size = new ThumbnailSize();
		
		try {
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			
			Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
			
			size.height = opts.outHeight;
			size.width = opts.outWidth;
			
		} catch (Exception e) {}
		
		return size;
		
	}
	
//	public static String compressImage(Activity activity, Uri uri) {
//		try {
//			String filePath = FileUtil.PHOTO + File.separator + FileUtil.getfilename() + ".jpg";
//			InputStream myInput = activity.getContentResolver().openInputStream(uri);
//
//			File file = new File(filePath);
//			FileOutputStream myOutput = new FileOutputStream(file);
//
//			FileUtil.copy(myInput, myOutput);
//			//compress
//			BitmapUtil.compress2(activity, filePath);
//			return filePath;
//		} catch (IOException e) {
//			Log.e("BitmapUtil", "saveimage fail", e);
//		}
//		return null;
//	}
}
