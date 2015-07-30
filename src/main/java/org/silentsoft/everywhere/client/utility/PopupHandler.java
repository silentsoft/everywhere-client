package org.silentsoft.everywhere.client.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.silentsoft.everywhere.client.application.App;

public class PopupHandler {

	public enum CloseType {
		FOCUS_BASE,
		BUTTON_BASE
	};
	
	private static Map<Parent, Map<Stage, Effect>> popupMap = new HashMap<Parent, Map<Stage, Effect>>();
	
	public static void show(Parent popup, CloseType closeType, boolean showColorAdjustEffect) {
		if (popup == null) {
			return;
		}
		
		Stage stage = new Stage();
		stage.initOwner(App.getStage());
		stage.initStyle(StageStyle.TRANSPARENT);
		
		if (closeType == CloseType.BUTTON_BASE) {
			Map<Stage, Effect> value = new HashMap<Stage, Effect>();
			value.put(stage, App.getBody().getEffect());
			
			popupMap.put(popup, value);
		}
		
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			Effect defaultEffect = App.getBody().getEffect();
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!oldValue && newValue) {
					// if got focus
					setEffect(new ColorAdjust(0, 0, -0.5, 0));
					App.getBody().setDisable(true);
				} else if (oldValue && !newValue) {
					// if lost focus
					if (closeType == CloseType.FOCUS_BASE) {
						stage.close();
						
						setEffect(defaultEffect);
						App.getBody().setDisable(false);
					}
				}
				
			}
			
			private void setEffect(Effect effect) {
				if (showColorAdjustEffect) {
					App.getBody().setEffect(effect);
				}
			}
		});
		
		double prefWidth = popup.prefWidth(0);
		double prefHeight = popup.prefHeight(0);
		Scene scene = new Scene(popup, prefWidth, prefHeight);
		
		stage.setScene(scene);
		stage.setX(App.getStage().getX() + (App.getStage().getWidth()/2) - (scene.getWidth()/2));
		stage.setY(App.getStage().getY() + (App.getStage().getHeight()/2) - (scene.getHeight()/2));
		stage.show();
	}
	
	public static Stage get(Parent popup) {
		for(Entry<Stage, Effect> entrySet : popupMap.get(popup).entrySet()) {
			return entrySet.getKey();
		}
		
		return null;
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
