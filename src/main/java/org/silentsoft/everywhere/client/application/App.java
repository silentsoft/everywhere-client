package org.silentsoft.everywhere.client.application;
	
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.event.EventListener;
import org.silentsoft.core.tray.TrayIconHandler;
import org.silentsoft.everywhere.client.view.login.LoginViewer;
import org.silentsoft.everywhere.client.view.main.MainViewer;
import org.silentsoft.everywhere.client.view.modify.ModifyViewer;
import org.silentsoft.everywhere.client.view.register.RegisterViewer;
import org.silentsoft.everywhere.context.BizConst;

import com.fxexperience.javafx.animation.BounceInTransition;

public class App extends Application implements EventListener {
	
	private static Parent app;
	
	private static Stage stage;
	
	private AppController appController;
	
	public static void main(String[] args) {
		launch(args);
	}

	protected static Parent getParent() {
		return app;
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
			
//			new FlipTransition(app).play();
//			new FadeInTransition(app).play();
			new BounceInTransition(app).play();
//			new FlipInXTransition(app).play();
			
			stage.setTitle("Everywhere");
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() {
		Platform.setImplicitExit(false);
		
		displayIcon();
		
		EventHandler.addListener(this);
	}
	
	private void displayIcon() {
		// taskbar
		stage.getIcons().add(new Image("/images/icon/everywhere.png"));
		
		// system tray
		TrayIconHandler.registerTrayIcon(Toolkit.getDefaultToolkit().getImage("src/main/resources/images/icon/everywhere.png"), "Everywhere", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(() -> {
					if (stage.isShowing()) {
						stage.setIconified(false);
					} else {
						new BounceInTransition(app).play();
						stage.show();
					}
				});
			}
		});
		
		TrayIconHandler.addItem("About", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(() -> {
					MessageBox.showInformation(App.getStage(), "Everywhere", "author : hs830.lee");
				});
			}
		});
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("Exit", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		TrayIconHandler.displayMessage("Everywhere", "Hello, World !", MessageType.INFO);
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
		case BizConst.EVENT_VIEW_MODIFY:
			setModifyViewToBody();
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
	
	private void setModifyViewToBody() {
		changeBodyToNode(new ModifyViewer().getModifyViewer());
	}
	
	private void changeBodyToNode(Node node) {
		Platform.runLater(() -> {
			Pane body = (Pane)app.lookup("#body");
			body.getChildren().clear();
			body.getChildren().add(node);
		});
	}
}
