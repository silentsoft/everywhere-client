package org.silentsoft.everywhere.client.core;

import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.io.memory.SharedMemory;

public class AbstractTest {

	public AbstractTest() {
		RESTfulAPI.init();
		SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, "JUNIT");
	}
}
