package org.silentsoft.everywhere.client.utility;

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
	
	public static void show(Parent popup, CloseType closeType, boolean showColorAdjustEffect) {
		Stage popupStage = new Stage();
		popupStage.initOwner(App.getStage());
		popupStage.initStyle(StageStyle.TRANSPARENT);
		popupStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			Effect defaultEffect = App.getBody().getEffect();
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!oldValue && newValue) {
					// if got focus
					setEffect(new ColorAdjust(0, 0, -0.5, 0));
				} else if (oldValue && !newValue) {
					// if lost focus
					setEffect(defaultEffect);
					
					if (closeType == CloseType.FOCUS_BASE) {
						popupStage.close();
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
		Scene popupScene = new Scene(popup, prefWidth, prefHeight);
		
		popupStage.setScene(popupScene);
		popupStage.setX(App.getStage().getX() + (App.getStage().getWidth()/2) - (popupScene.getWidth()/2));
		popupStage.setY(App.getStage().getY() + (App.getStage().getHeight()/2) - (popupScene.getHeight()/2));
		popupStage.show();
	}
}
