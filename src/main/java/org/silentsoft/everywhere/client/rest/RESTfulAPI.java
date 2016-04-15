package org.silentsoft.everywhere.client.rest;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.silentsoft.core.util.INIUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.util.SecurityUtil;
import org.silentsoft.io.memory.SharedMemory;

public class RESTfulAPI extends org.silentsoft.net.rest.RESTfulAPI {

	static {
		INIUtil meta = new INIUtil(System.getProperty("user.dir") + File.separator + BizConst.PATH_META);
		String uri = meta.getData(BizConst.INI_SECTION_SERVER, BizConst.INI_SERVER_URI);
		String root = meta.getData(BizConst.INI_SECTION_SERVER, BizConst.INI_SERVER_ROOT);
		
		init(uri, root);
	}
	
	public static void init() {
		// DO NOT WRITE CODE HERE.
	}
	
	public static void doGet(String api) throws Exception {
		doGet(api, createHeaders());
	}
	
	public static <T> T doGet(String api, Class<T> returnType) throws Exception {
		return doGet(api, createHeaders(), returnType);
	}
	
	public static void doPost(String api, Object param) throws Exception {
		doPost(api, createHeaders(), param);
	}
	
	public static <T> T doPost(String api, Object param, Class<T> returnType) throws Exception {
		return doPost(api, createHeaders(), param, returnType);
	}
	
	public static void doMultipart(String api, Object param) throws Exception {
		doMultipart(api, createHeaders(), param);
	}
	
	public static <T> T doMultipart(String api, Object param, Class<T> returnType) throws Exception {
		return doMultipart(api, createHeaders(), param, returnType);
	}
	
	private static Header[] createHeaders() {
		ArrayList<Header> headers = new ArrayList<Header>();
		
		String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
		if (ObjectUtil.isNotEmpty(userId)) {
			headers.add(new BasicHeader("user", userId));
			headers.add(new BasicHeader("sequence", SecurityUtil.encodePassword(userId)));
		}
		
		return headers.size() == 0 ? null : headers.toArray(new Header[headers.size()]);
	}
}
