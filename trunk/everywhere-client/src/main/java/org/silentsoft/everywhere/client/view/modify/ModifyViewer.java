package org.silentsoft.everywhere.client.view.modify;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifyViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyViewer.class);
	
	private Parent modifyViewer;
	
	private ModifyViewerController modifyViewerController;
	
	public ModifyViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ModifyViewer.fxml"));
			modifyViewer = fxmlLoader.load();
			modifyViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize modify viewer !", e);
		}
	}
	
	public Parent getModifyViewer() {
		return modifyViewer;
	}
	
	private void initialize() {
		modifyViewerController.initialize();
	}
}
