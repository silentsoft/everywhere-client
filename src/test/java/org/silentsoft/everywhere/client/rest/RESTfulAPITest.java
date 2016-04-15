package org.silentsoft.everywhere.client.rest;

import java.io.File;

import org.junit.Test;
import org.silentsoft.everywhere.client.core.AbstractTest;
import org.silentsoft.net.item.StoreItem;
import org.silentsoft.net.pojo.FilePOJO;

public class RESTfulAPITest extends AbstractTest {
	
//	@Test
	public void uploadTest() throws Exception {
		StoreItem storeItem = new StoreItem();
		storeItem.add(new FilePOJO(new File("D:\\FileTest.txt")));

		storeItem = RESTfulAPI.doMultipart("/fx/store/upload", storeItem, StoreItem.class);
		
		System.out.println(String.format("%s %s", "StoreItem's Tag :", storeItem.getTag()));
		for (FilePOJO filePOJO : storeItem) {
			System.out.println(String.format("%s %s", "FilePOJO's Tag :", filePOJO.getTag()));
		}
		
		/**
		 * StoreItem's Tag : 73ab02277fe94c789512ec56055323bf
		 * FilePOJO's Tag : 6a4c132b962148efb12a947a95ae9de1
		 */
	}
	
//	@Test
	public void groupDownloadTest() throws Exception {
		StoreItem storeItem = new StoreItem("73ab02277fe94c789512ec56055323bf");
		
		storeItem = RESTfulAPI.doMultipart("/fx/store/download", storeItem, StoreItem.class);
		
		for (FilePOJO filePOJO : storeItem) {
			filePOJO.store("D:\\tmp\\".concat(filePOJO.getNameWithExtension()));
		}
	}
	
//	@Test
	public void eachDownloadTest() throws Exception {
		StoreItem storeItem = new StoreItem();
		storeItem.add(new FilePOJO("6a4c132b962148efb12a947a95ae9de1"));
		
		storeItem = RESTfulAPI.doMultipart("/fx/store/download", storeItem, StoreItem.class);
		
		for (FilePOJO filePOJO : storeItem) {
			filePOJO.store("D:\\tmp\\".concat(filePOJO.getNameWithExtension()));
		}
	}
}
