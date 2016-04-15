package org.silentsoft.everywhere.client.application;
	
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.ImageIcon;

import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.client.version.BuildVersion;
import org.silentsoft.everywhere.client.view.login.LoginViewer;
import org.silentsoft.everywhere.client.view.main.MainViewer;
import org.silentsoft.everywhere.client.view.modify.ModifyViewer;
import org.silentsoft.everywhere.client.view.register.RegisterViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.hotkey.HotkeyHandler;
import org.silentsoft.ui.tray.TrayIconHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App extends Application implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private static Parent app;
	
	private static Stage stage;
	
	private AppController appController;
	
	public static void main(String[] args) {
		LOGGER.info("Welcome. This is ClientApp (v{}, b{}, j{})", new Object[]{ BuildVersion.VERSION, BuildVersion.BUILD_TIME, System.getProperty("java.version") });
		
		launch(args);
	}

	protected static Parent getParent() {
		return app;
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static AnchorPane getBody() {
		return (AnchorPane)getParent().lookup("#body");
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
			
			stage.setTitle("Everywhere");
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setScene(scene);
			
			AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() {
		Platform.setImplicitExit(false);
		
		displayIcon();
		registerHotkey();
		
		RESTfulAPI.init();
		
		EventHandler.addListener(this);
	}

	private void displayIcon() {
		// taskbar
		stage.getIcons().add(new Image("/images/icon/everywhere.png"));
		
		// system tray
		TrayIconHandler.registerTrayIcon(new ImageIcon(getClass().getResource("/images/icon/everywhere.png")).getImage(), "Everywhere", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(() -> {
					if (stage.isShowing()) {
						stage.setIconified(false);
					} else {
						AnimationUtils.createTransition(app, AnimationType.BOUNCE_IN).play();
						stage.show();
					}
				});
			}
		});
		
		TrayIconHandler.addItem("About", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(() -> {
					MessageBox.showInformation(App.getStage(), "Everywhere", "author : silentsoft");
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
	
	private void registerHotkey() {
		HotkeyHandler.getInstance().registerHotkey(KeyCode.H, true, true, true, () -> {
			LOGGER.debug(EventHandler.getHistories().toString());
		});
		
		stage.addEventHandler(KeyEvent.KEY_RELEASED, HotkeyHandler.getInstance());
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
		changeBodyToNode(new RegisterViewer().getViewer());
	}
	
	private void setLoginViewToBody() {
		changeBodyToNode(new LoginViewer().getViewer());
	}
	
	private void setMainViewToBody() {
		changeBodyToNode(new MainViewer().getViewer());
	}
	
	private void setModifyViewToBody() {
		changeBodyToNode(new ModifyViewer().getViewer());
	}
	
	private void changeBodyToNode(Node node) {
		Platform.runLater(() -> {
			getBody().getChildren().clear();
			getBody().getChildren().add(node);
		});
	}
}
