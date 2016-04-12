package org.silentsoft.everywhere.client.update;

import org.silentsoft.everywhere.client.version.BuildVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		LOGGER.info("Welcome. This is UpdateApp (v{}, b{}, j{})", new Object[]{ BuildVersion.VERSION, BuildVersion.BUILD_TIME, System.getProperty("java.version") });
	}

}
