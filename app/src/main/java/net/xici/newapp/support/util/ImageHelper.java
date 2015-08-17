package net.xici.newapp.support.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.FloatMath;

public class ImageHelper {

	public byte[] createThumbnail(byte[] bytes, String sMaxImageWidth,
			String orientation, boolean tiny) {
		// creates a thumbnail and returns the bytes

		int finalHeight = 0;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

		int width = opts.outWidth;

		int finalWidth = 500; // default to this if there's a problem
		// Change dimensions of thumbnail

		if (tiny) {
			finalWidth = 150;
		}

		byte[] finalBytes;

		if (sMaxImageWidth.equals("Original Size")) {
			finalBytes = bytes;
		} else {
			finalWidth = Integer.parseInt(sMaxImageWidth);
			if (finalWidth > width) {
				// don't resize
				finalBytes = bytes;
			} else {
				int sample = 0;

				float fWidth = width;
				sample = Double.valueOf(FloatMath.ceil(fWidth / 1200))
						.intValue();

				if (sample == 3) {
					sample = 4;
				} else if (sample > 4 && sample < 8) {
					sample = 8;
				}

				opts.inSampleSize = sample;
				opts.inJustDecodeBounds = false;

				try {
					bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
							opts);
				} catch (OutOfMemoryError e) {
					// out of memory
					return null;
				}

				float percentage = (float) finalWidth / bm.getWidth();
				float proportionateHeight = bm.getHeight() * percentage;
				finalHeight = (int) Math.rint(proportionateHeight);

				float scaleWidth = ((float) finalWidth) / bm.getWidth();
				float scaleHeight = ((float) finalHeight) / bm.getHeight();

				float scaleBy = Math.min(scaleWidth, scaleHeight);

				// Create a matrix for the manipulation
				Matrix matrix = new Matrix();
				// Resize the bitmap
				matrix.postScale(scaleBy, scaleBy);
				if ((orientation != null)
						&& (orientation.equals("90")
								|| orientation.equals("180") || orientation
									.equals("270"))) {
					matrix.postRotate(Integer.valueOf(orientation));
				}

				Bitmap resized = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), matrix, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resized.compress(Bitmap.CompressFormat.JPEG, 85, baos);

				bm.recycle(); // free up memory
				resized.recycle();

				finalBytes = baos.toByteArray();
			}
		}

		return finalBytes;

	}

	public String getExifOrientation(String path, String orientation) {
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

	public HashMap<String, Object> getImageBytesForPath(String filePath,
			Context ctx) {
		Uri curStream = null;
		String[] projection;
		HashMap<String, Object> mediaData = new HashMap<String, Object>();
		String title = "", orientation = "";
		byte[] bytes;
		if (filePath != null) {
			if (!filePath.contains("content://"))
				curStream = Uri.parse("content://media" + filePath);
			else
				curStream = Uri.parse(filePath);
		}
		if (curStream != null) {
			if (filePath.contains("video")) {
				int videoID = Integer.parseInt(curStream.getLastPathSegment());
				projection = new String[] { Video.Thumbnails._ID,
						Video.Thumbnails.DATA };
				ContentResolver crThumb = ctx.getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap videoBitmap = Video.Thumbnails.getThumbnail(
						crThumb, videoID,
						Video.Thumbnails.MINI_KIND, options);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					videoBitmap
							.compress(Bitmap.CompressFormat.PNG, 100, stream);
					bytes = stream.toByteArray();
					title = "Video";
					videoBitmap = null;
				} catch (Exception e) {
					return null;
				}

			} else {
				projection = new String[] { Images.Thumbnails._ID,
						Images.Thumbnails.DATA, Images.Media.ORIENTATION };

				String path = "";
				Cursor cur;
				try {
					cur = ctx.getContentResolver().query(curStream, projection,
							null, null, null);
				} catch (Exception e1) {
					return null;
				}
				File jpeg = null;
				if (cur != null) {
					String thumbData = "";

					if (cur.moveToFirst()) {

						int dataColumn, orientationColumn;

						dataColumn = cur.getColumnIndex(Images.Media.DATA);
						thumbData = cur.getString(dataColumn);
						orientationColumn = cur
								.getColumnIndex(Images.Media.ORIENTATION);
						orientation = cur.getString(orientationColumn);
						if (orientation == null)
							orientation = "";
					}

					if (thumbData == null) {
						return null;
					}

					jpeg = new File(thumbData);
					path = thumbData;
				} else {
					path = filePath.toString().replace("file://", "");
					jpeg = new File(path);

				}

				title = jpeg.getName();

				try {
					bytes = new byte[(int) jpeg.length()];
				} catch (Exception e) {
					return null;
				} catch (OutOfMemoryError e) {
					return null;
				}

				DataInputStream in = null;
				try {
					in = new DataInputStream(new FileInputStream(jpeg));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				try {
					in.readFully(bytes);
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				title = jpeg.getName();
				if (orientation == "") {
					orientation = getExifOrientation(path, orientation);
				}
			}

			mediaData.put("bytes", bytes);
			mediaData.put("title", title);
			mediaData.put("orientation", orientation);

			return mediaData;

		} else {
			return null;
		}

	}
	
//	/**
//	 * 压缩图片
//	 * 
//	 * @param bytes
//	 * @param sMaxImageWidth
//	 * @param orientation
//	 * @param file
//	 * @param quality
//	 * @throws IOException
//	 */
//	public static boolean compress(byte[] bytes, int sMaxImageWidth,
//			String orientation, File file, int quality) throws IOException {
//		
//		boolean result = false;
//
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inJustDecodeBounds = true;
//		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
//
//		opts.inJustDecodeBounds = false;
//		// 计算缩放比
//		int be = (int) (opts.outHeight / (float) sMaxImageWidth);
//		if (be <= 0)
//			be = 1;
//		opts.inSampleSize = be;
//		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
//		try {
//			bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
//		} catch (OutOfMemoryError e) {
//			// out of memory
//			return result;
//		}
//
//		// Create a matrix for the manipulation
//		Matrix matrix = new Matrix();
//		// Resize the bitmap
//		if ((orientation != null)
//				&& (orientation.equals("90") || orientation.equals("180") || orientation
//						.equals("270"))) {
//			matrix.postRotate(Integer.valueOf(orientation));
//		}
//
//		Bitmap resized = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
//				bm.getHeight(), matrix, true);
//		bm.recycle(); // free up memory
//
//		FileOutputStream outstream = new FileOutputStream(file);
//
//		result = resized.compress(Bitmap.CompressFormat.JPEG, quality, outstream);
//		outstream.flush();
//		outstream.close();
//
//		resized.recycle();
//		return result;
//	}

}
