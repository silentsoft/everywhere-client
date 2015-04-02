package org.silentsoft.everywhere.client.view.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginViewer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewer.class);
	
	private Parent loginViewer;
	
	private LoginViewerController loginViewerController;
	
	public LoginViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginViewer.fxml"));
			loginViewer = fxmlLoader.load();
			loginViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize login viewer !", e);
		}
	}
	
	public Parent getLoginViewer() {
		return loginViewer;
	}
	
	private void initialize() {
		loginViewerController.initialize();
	}
}
