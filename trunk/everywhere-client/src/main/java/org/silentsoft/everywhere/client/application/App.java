package org.silentsoft.everywhere.client.application;
	
import org.silentsoft.everywhere.client.view.login.LoginViewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
	
	private static Stage stage;
	
	private Parent app;
	
	private AppController appController;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getStage() {
		return stage;
	}
    
	@Override
	public void start(Stage stage) {
		try {
			this.stage = stage;
			
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("App.fxml"));
			app = fxmlLoader.load();
			appController = fxmlLoader.getController();
			appController.initialize();
			
			setLoginViewToBody();
			
			Scene scene = new Scene(app, 910, 530, Color.TRANSPARENT);
			
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLoginViewToBody() {
		Pane body = (Pane)app.lookup("#body");
		body.getChildren().clear();
		body.getChildren().add(new LoginViewer().getLoginViewer());
	}
}
