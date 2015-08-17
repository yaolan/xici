package net.xici.newapp.support.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.support.widget.CircleBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("NewApi")
public class ImageUtils implements Constants {

	public static ImageLoader sImageLoader = ImageLoader.getInstance();

	public static final DisplayImageOptions DISYPLAY_OPTION_WEB_IMAGE = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.default_img)
			.showImageForEmptyUri(R.drawable.default_img)
			.showImageOnFail(R.drawable.default_img).cacheInMemory(true)
			.cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_SPLASH_IMAGE = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.color.white)
			.showImageForEmptyUri(R.color.white)
			.showImageOnFail(R.color.white).cacheInMemory(true)
			.cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_WEB_IMAGE_CACHEMEMORY = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.default_img)
			.showImageForEmptyUri(R.drawable.default_img)
			.showImageOnFail(R.drawable.default_img).cacheInMemory(true)
			.cacheOnDisk(false).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_WEB_IMAGE_CACHDISC = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.default_img)
			.showImageForEmptyUri(R.drawable.default_img)
			.showImageOnFail(R.drawable.default_img).cacheInMemory(false)
			.cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.default_avatar)
			.showImageForEmptyUri(R.drawable.default_avatar)
			.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
			.bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR_CIRCLE = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.avatar_default_circle)
			.showImageForEmptyUri(R.drawable.avatar_default_circle)
			.displayer(new CircleBitmapDisplayer())
			.showImageOnFail(R.drawable.avatar_default_circle)
			.cacheInMemory(true).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_AVATAR_ROUND = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.avatar_default_round)
			.showImageForEmptyUri(R.drawable.avatar_default_round)
			.showImageOnFail(R.drawable.avatar_default_round)
			.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
			.bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_BOARDLOGO_ROUND = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bd_logo)
			.showImageForEmptyUri(R.drawable.bd_logo)
			.imageScaleType(ImageScaleType.EXACTLY)
			.displayer(new RoundedBitmapDisplayer(5))
			.showImageOnFail(R.drawable.bd_logo).cacheInMemory(true)
			.cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();

	public static final DisplayImageOptions DISYPLAY_OPTION_BOARDLOGO = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bd_logo)
			.showImageForEmptyUri(R.drawable.bd_logo)
			.imageScaleType(ImageScaleType.EXACTLY)
			.showImageOnFail(R.drawable.bd_logo).cacheInMemory(true)
			.cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();

	public static void displayWebImage(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_WEB_IMAGE);
	}
	
	public static void displaySplashPicImage(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_SPLASH_IMAGE);
	}

	public static void displayWebImageCacheMemory(String url,
			ImageView imageView) {
		sImageLoader.displayImage(url, imageView,
				DISYPLAY_OPTION_WEB_IMAGE_CACHEMEMORY);
	}

	public static void displayWebImageCacheDisc(String url, ImageView imageView) {
		sImageLoader.displayImage(url, imageView,
				DISYPLAY_OPTION_WEB_IMAGE_CACHDISC);
	}

	public static void displayAvatar(long uid, ImageView imageView) {
		sImageLoader.displayImage(getavatarurl(uid), imageView,
				DISYPLAY_OPTION_AVATAR);
	}

//	public static void displayAvatar(String url, ImageView imageView) {
//		sImageLoader.displayImage(url, imageView, DISYPLAY_OPTION_AVATAR);
//	}
//
//	public static void clearMemoryCache() {
//		sImageLoader.getMemoryCache().clear();
//	}
//


	public static void displayAvatarCircle(long uid, ImageView imageView) {
		sImageLoader.displayImage(getavatarurl(uid), imageView,
				DISYPLAY_OPTION_AVATAR_CIRCLE);
	}
//
//	public static void displayAvatarLarge(long uid, ImageView imageView) {
//		sImageLoader.displayImage(getavatarurlLarge(uid), imageView,
//				DISYPLAY_OPTION_AVATAR);
//	}
//
//	public static void displayAvatarCircleLarge(long uid, ImageView imageView) {
//		sImageLoader.displayImage(getavatarurlLarge(uid), imageView,
//				DISYPLAY_OPTION_AVATAR_CIRCLE);
//	}
//
//	public static void displayLocalAvatarCircle(File file, ImageView imageView) {
//		sImageLoader.displayImage(Uri.fromFile(file).toString(), imageView,
//				DISYPLAY_OPTION_AVATAR_CIRCLE);
//	}
//
//	public static void displayAvatarRound(long uid, ImageView imageView) {
//		sImageLoader.displayImage(getavatarurl(uid), imageView,
//				DISYPLAY_OPTION_AVATAR_ROUND);
//	}
//
	public static void displayboardlogo(int boardid, ImageView imageView) {
		sImageLoader.displayImage(getboardidurl(boardid), imageView,
				DISYPLAY_OPTION_BOARDLOGO);
	}

	public static void displayboardlogoRound(int boardid, ImageView imageView) {
		sImageLoader.displayImage(getboardidurl(boardid), imageView,
				DISYPLAY_OPTION_BOARDLOGO_ROUND);
	}

	public static void resume() {
		sImageLoader.resume();
	}

	/**
	 * 暂停加载
	 */
	public static void pause() {
		sImageLoader.pause();
	}

	/**
	 * 停止加载
	 */
	public static void stop() {
		sImageLoader.stop();
	}

	/**
	 * 销毁加载
	 */
	public static void destroy() {
		sImageLoader.destroy();
	}

	public static void cancelDisplayTask(ImageView imageView) {
		sImageLoader.cancelDisplayTask(imageView);
	}

	public static String getImagePathFromProvider(Context context, Uri uri) {
		String[] projection = new String[] { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);
		int rowNums = cursor.getCount();
		if (rowNums == 0) {
			return null;
		}
		cursor.moveToFirst();
		String filePath = cursor.getString(0);
		cursor.close();
		return filePath;

	}

	public static Bitmap scaleImage(String imagePath, int requestWidth,
			int requestHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		options.inSampleSize = calculateInSampleSize(options, requestWidth,
				requestHeight);

		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

		String orientation = getExifOrientation(imagePath, "0");

		Matrix matrix = new Matrix();
		matrix.postRotate(Float.valueOf(orientation));

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);

		return newBitmap;
	}

	public static String getExifOrientation(String path, String orientation) {
		// get image EXIF orientation if Android 2.0 or higher, using reflection
		// http://developer.android.com/resources/articles/backward-compatibility.html
		Method exif_getAttribute;
		Constructor<ExifInterface> exif_construct;
		String exifOrientation = "";

		int sdk_int = 0;
		try {
			sdk_int = Integer.valueOf(VERSION.SDK);
		} catch (Exception e1) {
			sdk_int = 3; // assume they are on cupcake
		}
		if (sdk_int >= 5) {
			try {
				exif_construct = ExifInterface.class
						.getConstructor(new Class[] { String.class });
				Object exif = exif_construct.newInstance(path);
				exif_getAttribute = ExifInterface.class
						.getMethod("getAttribute", new Class[] { String.class });
				try {
					exifOrientation = (String) exif_getAttribute.invoke(exif,
							ExifInterface.TAG_ORIENTATION);
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
				} catch (InvocationTargetException ite) {
					/* unpack original exception when possible */
					orientation = "0";
				} catch (IllegalAccessException ie) {
					System.err.println("unexpected " + ie);
					orientation = "0";
				}
				/* success, this is a newer device */
			} catch (NoSuchMethodException nsme) {
				orientation = "0";
			} catch (IllegalArgumentException e) {
				orientation = "0";
			} catch (InstantiationException e) {
				orientation = "0";
			} catch (IllegalAccessException e) {
				orientation = "0";
			} catch (InvocationTargetException e) {
				orientation = "0";
			}

		}
		return orientation;
	}

	public static String getPicPathFromUri(Uri uri, Activity activity) {
		String value = uri.getPath();

		if (value.startsWith("/external")) {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else {
			return value;
		}
	}

	private static final int MAX_TEXTURE_SIZE = getOpengl2MaxTextureSize();

	public static int getOpengl2MaxTextureSize() {
		int[] maxTextureSize = new int[1];
		maxTextureSize[0] = 2048;
		android.opengl.GLES20.glGetIntegerv(
				android.opengl.GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		return maxTextureSize[0];
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	@SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
		if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height(inMutable)
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		return decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight,
				false);
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param isMutable
	 *            �ɱ༭
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	@SuppressLint("NewApi")
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight, boolean isMutable) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		if (isMutable && VERSION.SDK_INT >= 11) {
			options.inMutable = true;
		}
		Bitmap result = BitmapFactory.decodeResource(res, resId, options);
		if (isMutable) {
			result = createMutableBitmap(result);
		}
		return result;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filePath,
			int sampledSize) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = sampledSize;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filePath
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height(inmutable)
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filePath,
			int reqWidth, int reqHeight) {
		return decodeSampledBitmapFromFile(filePath, reqWidth, reqHeight,
				false, false);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filePath
	 *            The full path of the file to decode
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @param isMutable
	 *            �ɱ༭
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filePath,
			int reqWidth, int reqHeight, boolean isMutable, boolean region) {
		if (filePath == null) {
			return null;
		}
		if (reqHeight == 0) {
			reqHeight = MAX_TEXTURE_SIZE;
		}
		if (reqWidth == 0) {
			reqWidth = MAX_TEXTURE_SIZE;
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);

		if (options.outWidth == -1 || options.outHeight == -1) {
			return null;
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		if (isMutable && VERSION.SDK_INT >= 11) {
			options.inMutable = true;
		}

		Bitmap result = null;

		if ((options.outWidth > MAX_TEXTURE_SIZE
				|| options.outHeight > MAX_TEXTURE_SIZE || (options.outHeight > options.outWidth * 3))
				&& region) {
			// ��ͼ
			try {
				result = regionDecode(filePath, reqWidth, reqHeight,
						options.outWidth, options.outHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			result = BitmapFactory.decodeFile(filePath, options);
		}

		if (isMutable) {
			result = createMutableBitmap(result);
		}

		return result;
	}

	private static Bitmap regionDecode(String path, int reqWidth,
			int reqHeight, int outWidth, int outHeight) throws IOException {
		BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(
				path, true);
		if (reqWidth > outWidth) {
			reqWidth = outWidth;
		}
		if (reqHeight > outHeight) {
			reqHeight = outHeight;
		}

		return regionDecoder.decodeRegion(new Rect(0, 0, reqWidth, reqHeight),
				null);
	}

	/**
	 * Calculate an inSampleSize for use in a
	 * {@link BitmapFactory.Options} object when decoding
	 * bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates
	 * the closest inSampleSize that will result in the final decoded bitmap
	 * having a width and height equal to or larger than the requested width and
	 * height. This implementation does not ensure a power of 2 is returned for
	 * inSampleSize which can be faster when decoding but results in a larger
	 * bitmap which isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			int widthSampleSize = 0;
			int heightSampleSize = 0;
			if (reqWidth < width) {
				widthSampleSize = Math.round((float) width / (float) reqWidth);
			}
			if (reqHeight < height) {
				heightSampleSize = Math.round((float) height
						/ (float) reqHeight);
			}
			inSampleSize = Math.max(widthSampleSize, heightSampleSize);
			// if (width > height) {
			// inSampleSize = Math.round((float) height / (float) reqHeight);
			// } else {
			// inSampleSize = Math.round((float) width / (float) reqWidth);
			// }

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			// final float totalPixels = width * height;
			//
			// // Anything more than 2x the requested pixels we'll sample down
			// // further.
			// float totalReqPixelsCap = reqWidth * reqHeight * 2;
			// while (totalPixels / (inSampleSize * inSampleSize) >
			// totalReqPixelsCap) {
			// inSampleSize++;
			// }
		}
		return inSampleSize;
	}

	/**
	 * ͨ��srcbitmap ����һ���ɱ༭��bitmap
	 * 
	 * @param src
	 * @return
	 */
	public static Bitmap createMutableBitmap(Bitmap src) {
		Bitmap result = null;
		if (src == null) {
			return null;
		}
		result = src.copy(Config.ARGB_8888, true);

		return result;
	}

	/**
	 * ��subBmpͼ��ϲ���oriBmp��
	 * 
	 * @param oriBmp
	 * @param subBmp
	 * @param oriRect
	 *            subBmp��ȡ����bitmap��Ҫ��䵽oriRect�е�����
	 * @param subRect
	 *            ��subBmp��ȡ��������
	 * @param paint
	 * @return
	 */
	public static Bitmap mergeBitmap(Bitmap oriBmp, Bitmap subBmp,
			final Rect oriRect, final Rect subRect) {
		if (subBmp == null) {
			return oriBmp;
		}

		if (oriBmp == null) {
			return null;
		}

		if (!oriBmp.isMutable()) {
			oriBmp = createMutableBitmap(oriBmp);
		}

		Canvas canvas = new Canvas(oriBmp);
		canvas.drawBitmap(subBmp, subRect, oriRect, null);
		return oriBmp;
	}

	/**
	 * ��subBmpͼ��ϲ���oriBmp��
	 * 
	 * @param oriBmp
	 * @param subBmp
	 * @param paint
	 * @return oriBmp
	 */
	public static Bitmap mergeBitmap(Bitmap oriBmp, Bitmap subBmp) {
		if (subBmp == null) {
			return oriBmp;
		}

		if (oriBmp == null) {
			return null;
		}

		return mergeBitmap(oriBmp, subBmp, new Rect(0, 0, oriBmp.getWidth(),
				oriBmp.getHeight()),
				new Rect(0, 0, subBmp.getWidth(), subBmp.getHeight()));
	}

	private static final PorterDuffXfermode SRC_IN_MODE = new PorterDuffXfermode(
			PorterDuff.Mode.SRC_IN);

	private final static Paint SRC_IN_PAINT = new Paint();

	static {
		SRC_IN_PAINT.setXfermode(SRC_IN_MODE);
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param dstBmp
	 * @param mask
	 * @param paint
	 * @return ���ֺ��ͼƬ
	 */
	public static Bitmap maskBitmap(final Bitmap dstBmp, final Bitmap mask) {
		if (dstBmp == null || mask == null) {
			return dstBmp;
		}
		Bitmap result = Bitmap.createBitmap(dstBmp.getWidth(),
				dstBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		int sc = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
				null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
						| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
						| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		canvas.drawBitmap(mask,
				new Rect(0, 0, mask.getWidth(), mask.getHeight()), new Rect(0,
						0, dstBmp.getWidth(), dstBmp.getHeight()), null);
		canvas.drawBitmap(dstBmp, 0, 0, SRC_IN_PAINT);

		canvas.restoreToCount(sc);
		return result;
	}

	public static Bitmap convertToAlphaMask(Bitmap b) {
		Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(),
				Config.ALPHA_8);
		Canvas c = new Canvas(a);
		c.drawBitmap(b, 0.0f, 0.0f, null);
		return a;
	}

	public static Bitmap decodeBitmapFromDrawableRes(int resId,
			final int width, final int height) {
		Drawable drawable = XiciApp.getContext().getResources()
				.getDrawable(resId);
		drawable.setBounds(0, 0, width, height);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);
		return bitmap;
	}

	public static byte[] getbyteFromBitmap(Bitmap bitmap, boolean recycle)
			throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, out);
		if (recycle) {
			bitmap.recycle();
		}

		return out.toByteArray();
	}

	public static String getavatarurl(long uid) {
		return "http://icons.xici.net/u" + uid + "/files/photo_m.pic";
	}

	public static String getavatarurlLarge(long uid) {
		return "http://icons.xici.net/u" + uid + "/files/photo_l.pic";
	}

	public static String getboardidurl(int boardid) {
		return "http://xiciimgs.xici800.com/board_" + boardid + ".jpg";
	}

	public static String getBigUrl(String url) {
		String bigurl = "";
		if (!TextUtils.isEmpty(url)) {
			if (url.endsWith("tb=mt")) {
				bigurl = url.substring(0, url.lastIndexOf("mt")) + "dm";
			} else {
				bigurl = url;
			}
		}
		return bigurl;
	}

}
