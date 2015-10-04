package org.silentsoft.everywhere.client.view.main.download;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadViewer.class);
	
	private Parent downloadViewer;
	
	private DownloadViewerController downloadViewerController;
	
	public DownloadViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DownloadViewer.fxml"));
			downloadViewer = fxmlLoader.load();
			downloadViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize download viewer !", e);
		}
	}
	
	public Parent getDownloadViewer() {
		return downloadViewer;
	}
	
	private void initialize() {
		downloadViewerController.initialize(downloadViewer);
	}
	
}
