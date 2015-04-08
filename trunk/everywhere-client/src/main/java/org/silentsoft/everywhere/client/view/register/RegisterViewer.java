package org.silentsoft.everywhere.client.view.register;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterViewer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterViewer.class);
	
	private Parent registerViewer;
	
	private RegisterViewerController registerViewerController;
	
	public RegisterViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterViewer.fxml"));
			registerViewer = fxmlLoader.load();
			registerViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize register viewer !", e);
		}
	}
	
	public Parent getRegisterViewer() {
		return registerViewer;
	}
	
	private void initialize() {
		registerViewerController.initialize();
	}
}
