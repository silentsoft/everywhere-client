package org.silentsoft.everywhere.client.view.main.upload;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadViewer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadViewer.class);
	
	private Parent uploadViewer;
	
	private UploadViewerController uploadViewerController;
	
	public UploadViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UploadViewer.fxml"));
			uploadViewer = fxmlLoader.load();
			uploadViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize upload viewer !", e);
		}
	}
	
	public Parent getUploadViewer() {
		return uploadViewer;
	}
	
	private void initialize() {
		uploadViewerController.initialize(uploadViewer);
	}

}
