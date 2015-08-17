package net.xici.newapp.support.request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.util.Response;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
	public final static String TAG = "HttpUtil";
	public static final String FILE_KEY = "file";
	public static final Integer DEFAULT_REQUEST_TIMEOUT = 20000;
	public static final Integer DEFAULT_GET_REQUEST_TIMEOUT = 20000;
	public static final Integer DEFAULT_POST_REQUEST_TIMEOUT = 20000;
	public static final int SET_CONNECTION_TIMEOUT = 20000;
	public static final int SET_SOCKET_TIMEOUT = 200000;

	private static final String BOUNDARY = "7cd4a6d158c";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String END_MP_BOUNDARY = "--7cd4a6d158c--";
	private static final String MP_BOUNDARY = "--7cd4a6d158c";

	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象
	static {
		client.setTimeout(DEFAULT_REQUEST_TIMEOUT); // 设置链接超时，如果不设置，默认为10s
	}

	public static void cancel(Context context, boolean interrupt) {
		client.cancelRequests(context, interrupt);
	}

	public static void get(Context context, String urlString,
			AsyncHttpResponseHandler handler) // 用一个完整url获取一个string对象
	{
		setheader();
		client.get(context, urlString, handler);
	}

	public static void get(Context context, String urlString,
			RequestParams params, AsyncHttpResponseHandler handler) // url里面带参数
	{
		setheader();
		client.get(context, urlString, params, handler);
	}

	public static void get(Context context, String urlString,
			JsonHttpResponseHandler handler) // 不带参数，获取json对象或者数组
	{
		setheader();
		client.get(context, urlString, handler);
	}

	public static void get(Context context, String urlString,
			RequestParams params, JsonHttpResponseHandler handler) // 带参数，获取json对象或者数组
	{
		setheader();
		client.get(context, urlString, params, handler);
	}

	public static void get(Context context, String uString,
			BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
	{
		setheader();
		client.get(context, uString, bHandler);
	}

	public static void post(Context context, String urlString,
			RequestParams params, AsyncHttpResponseHandler res) // 带参数
	{
		setheader();
		client.post(context, urlString, params, res);
	}

	public static void post(Context context, String urlString,
			RequestParams params, JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
	{
		setheader();
		client.post(context, urlString, params, res);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
	
	public static void setheader(){
		client.removeHeader(Constants.X_Request_Token);
		if(!TextUtils.isEmpty(XiciApp.request_token)){
			client.addHeader(Constants.X_Request_Token, XiciApp.request_token);
		}
		
	}
	
	public static String getRequest(String url) throws HttpException {
		return getRequest(url, new HashMap<String, String>());
	}

	public static String getRequest(String url, Map<String, String> params)
			throws HttpException {
		return getRequest(url, params, new DefaultHttpClient());
	}

	public static String getRequest(String url, Map<String, String> params,
			HttpClient client) throws HttpException {
		String result = null;
		List<NameValuePair> paramList = new LinkedList<NameValuePair>();
		for (Map.Entry<String, String> param : params.entrySet()) {
			paramList.add(new BasicNameValuePair(param.getKey(), param
					.getValue()));
		}

		String paramString = "?" + URLEncodedUtils.format(paramList, "utf-8");

		L.i(TAG, url + paramString);

		HttpGet getMethod = new HttpGet(url + paramString);
		try {
			client.getParams().setIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT,
					DEFAULT_GET_REQUEST_TIMEOUT);
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
					DEFAULT_GET_REQUEST_TIMEOUT);
			HttpResponse httpResponse = client.execute(getMethod);

			result = Response.entityToString(httpResponse.getEntity());
			L.i(TAG, result);
		} catch (Exception e) {
			throw new HttpException("retrive url " + url + " error", e);
		} finally {
			getMethod.abort();
		}

		return result;
	}

	public static String postRequest(String url) throws HttpException,
			IOException {
		return postRequest(url, new HashMap<String, String>());
	}

	public static String postRequest(String url, Map<String, String> params)
			throws HttpException, IOException {
		return postRequest(url, params, new DefaultHttpClient());
	}

	public static String postRequest(String url, Map<String, String> params,
			HttpClient client) throws HttpException, IOException {
		String result = null;
		HttpPost postMethod = new HttpPost(url);
		try {
			if (!params.containsKey(FILE_KEY)) {

				List<NameValuePair> paramList = new LinkedList<NameValuePair>();
				for (Map.Entry<String, String> param : params.entrySet()) {
					paramList.add(new BasicNameValuePair(param.getKey(), param
							.getValue()));
				}
				UrlEncodedFormEntity entity = null;
				try {
					entity = new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("wtf");
				}
				postMethod.setEntity(entity);

			} else {

				String filepath = params.get(FILE_KEY);
				StringBuilder sb = new StringBuilder();
				params.remove(FILE_KEY);
				postMethod.setHeader("Content-Type", MULTIPART_FORM_DATA
						+ "; boundary=" + BOUNDARY);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				sb = new StringBuilder();
				for (Map.Entry<String, String> param : params.entrySet()) {

					sb.append(MP_BOUNDARY).append("\r\n");
					sb.append("content-disposition: form-data; name=\"")
							.append(param.getKey()).append("\"");
					sb.append("\r\n\r\n");
					sb.append(param.getValue()).append("\r\n");
				}
				out.write(sb.toString().getBytes());
				sb = new StringBuilder();
				sb.append(MP_BOUNDARY).append("\r\n");
				sb.append(
						"Content-Disposition: form-data; name=\"" + FILE_KEY
								+ "\"; filename=\"")
						.append(new File(filepath).getName()).append("\"\r\n");
				sb.append("Content-Type: ").append("image/jpeg")
						.append("\r\n\r\n");
				out.write(sb.toString().getBytes());
				out.write(ImageUtils.getbyteFromBitmap(BitmapFactory.decodeFile(filepath), true));
//				out.write(ImageUtils.getbyteFromBitmap(ImageUtils
//						.decodeSampledBitmapFromFile(filepath, 480, 320), true));
				
				//BitmapFactory
				out.write(("\r\n\r\n" + END_MP_BOUNDARY).getBytes());
				ByteArrayEntity byteEntity = new ByteArrayEntity(
						out.toByteArray());
				postMethod.setEntity(byteEntity);
			}

			client.getParams().setIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT,
					DEFAULT_POST_REQUEST_TIMEOUT);
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
					DEFAULT_POST_REQUEST_TIMEOUT);
			HttpResponse httpResponse = client.execute(postMethod);

			result = Response.entityToString(httpResponse.getEntity());
		} catch (Exception e) {
			throw new HttpException("retrive url " + url + " error", e);
		} finally {
			postMethod.abort();
		}

		return result;
	}

	public static HttpClient getNewHttpClient(Context context) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			// Set the default socket timeout (SO_TIMEOUT) // in
			// milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setConnectionTimeout(params,
					SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);

			// WifiManager wifiManager = (WifiManager) context
			// .getSystemService(Context.WIFI_SERVICE);
			// if (!wifiManager.isWifiEnabled()) {
			// // 获取当前正在使用的APN接入点
			// Uri uri = Uri.parse("content://telephony/carriers/preferapn");
			// Cursor mCursor = context.getContentResolver().query(uri, null,
			// null, null, null);
			// if (mCursor != null && mCursor.moveToFirst()) {
			// // 游标移至第一条记录，当然也只有一条
			// String proxyStr = mCursor.getString(mCursor
			// .getColumnIndex("proxy"));
			// if (proxyStr != null && proxyStr.trim().length() > 0) {
			// HttpHost proxy = new HttpHost(proxyStr, 80);
			// client.getParams().setParameter(
			// ConnRouteParams.DEFAULT_PROXY, proxy);
			// }
			// mCursor.close();
			// }
			// }
			return client;
		} catch (Exception e) {
		}
		return null;
	}
	
	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
