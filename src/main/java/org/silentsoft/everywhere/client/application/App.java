package org.silentsoft.everywhere.client.application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.event.EventListener;
import org.silentsoft.everywhere.client.view.login.LoginViewer;
import org.silentsoft.everywhere.client.view.main.MainViewer;
import org.silentsoft.everywhere.client.view.register.RegisterViewer;
import org.silentsoft.everywhere.context.BizConst;

public class App extends Application implements EventListener {
	
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
			
			initialize();
			
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
	
	private void initialize() {
		EventHandler.addListener(this);
	}
	
	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_VIEW_REGISTER:
			setRegisterViewToBody();
			break;
		case BizConst.EVENT_VIEW_LOGIN:
			setLoginViewToBody();
			break;
		case BizConst.EVENT_VIEW_MAIN:
			setMainViewToBody();
			break;
		}
	}
	
	private void setRegisterViewToBody() {
		changeBodyToNode(new RegisterViewer().getRegisterViewer());
	}
	
	private void setLoginViewToBody() {
		changeBodyToNode(new LoginViewer().getLoginViewer());
	}
	
	private void setMainViewToBody() {
		changeBodyToNode(new MainViewer().getMainViewer());
	}
	
	private void changeBodyToNode(Node node) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Pane body = (Pane)app.lookup("#body");
				body.getChildren().clear();
				body.getChildren().add(node);
			}
		});
	}
}
