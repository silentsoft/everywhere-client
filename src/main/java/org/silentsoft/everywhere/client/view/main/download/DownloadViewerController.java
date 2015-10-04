package org.silentsoft.everywhere.client.view.main.download;

import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadViewerController.class);
	
	private Parent downloadViewer;
	
	protected void initialize(Parent downloadViewer) {
		this.downloadViewer = downloadViewer;
	}

}
