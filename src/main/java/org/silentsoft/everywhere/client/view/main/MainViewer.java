package org.silentsoft.everywhere.client.view.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainViewer.class);
	
	private Parent mainViewer;
	
	private MainViewerController mainViewerController;
	
	public MainViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainViewer.fxml"));
			mainViewer = fxmlLoader.load();
			mainViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize main viewer !", e);
		}
	}
	
	public Parent getMainViewer() {
		return mainViewer;
	}
	
	private void initialize() {
		mainViewerController.initialize();
	}
}
