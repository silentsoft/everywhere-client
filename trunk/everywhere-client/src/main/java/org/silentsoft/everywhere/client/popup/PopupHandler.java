package org.silentsoft.everywhere.client.popup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.silentsoft.everywhere.client.application.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopupHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PopupHandler.class);

	public enum CloseType {
		FOCUS_BASE,
		BUTTON_BASE
	};
	
	private static Map<Parent, Map<Stage, Effect>> popupMap = new HashMap<Parent, Map<Stage, Effect>>();
	
	public static void show(String title, Parent popup, CloseType closeType, boolean showColorAdjustEffect) {
		if (popup == null) {
			return;
		}
		
		Parent parent = null;
		PopupHandlerController controller = null;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(PopupHandler.class.getResource("PopupHandler.fxml")); 
			parent = fxmlLoader.load();
			controller = fxmlLoader.getController();
			controller.initialize(popup);
		} catch (IOException e) {
			LOGGER.error("Popup show failure !");
			LOGGER.error(e.toString());
		}
		
		if (parent == null || controller == null) {
			return;
		}
		
		controller.setTitle(title);
		
		Stage stage = new Stage();
		stage.initOwner(App.getStage());
		stage.initStyle(StageStyle.TRANSPARENT);
		
		if (closeType == CloseType.FOCUS_BASE) {
			controller.setCloseButtonVisible(false);
		} else if (closeType == CloseType.BUTTON_BASE) {
			controller.setCloseButtonVisible(true);
		}
		
		Map<Stage, Effect> value = new HashMap<Stage, Effect>();
		value.put(stage, App.getBody().getEffect());
		
		popupMap.put(popup, value);
		
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!oldValue && newValue) {
					// if got focus
					if (showColorAdjustEffect) {
						App.getBody().setEffect(new ColorAdjust(0, 0, -0.5, 0));
					}
					App.getBody().setDisable(true);
				} else if (oldValue && !newValue) {
					// if lost focus
					if (closeType == CloseType.FOCUS_BASE) {
						close(popup);
					}
				}
			}
		});
		
		double prefWidth = parent.prefWidth(0);
		double prefHeight = parent.prefHeight(0);
		Scene scene = new Scene(parent, prefWidth, prefHeight);
		
		stage.setScene(scene);
		stage.setX(App.getStage().getX() + (App.getStage().getWidth()/2) - (scene.getWidth()/2));
		stage.setY(App.getStage().getY() + (App.getStage().getHeight()/2) - (scene.getHeight()/2));
		stage.show();
	}
	
	public static void close(Parent popup) {
		if (popup == null) {
			return;
		}
		
		for(Entry<Stage, Effect> entrySet : popupMap.get(popup).entrySet()) {
			Stage stage = entrySet.getKey();
			if (stage != null) {
				stage.close();
				
				App.getBody().setEffect(entrySet.getValue());
				App.getBody().setDisable(false);
				
				popupMap.remove(popup);
			}
			
			break;
		}
	}
}
