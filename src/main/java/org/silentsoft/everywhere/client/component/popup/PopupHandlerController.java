package org.silentsoft.everywhere.client.component.popup;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class PopupHandlerController {
	
	@FXML
	private Label title;
	
	@FXML
	private Button closeBtn;
	
	@FXML
	private Pane body;
	
	private Parent popup;
	
	public void initialize(Parent popup) {
		this.popup = popup;
		
		body.getChildren().clear();
		body.getChildren().add(popup);
	}
	
	@FXML
	private void close_OnActionClick() {
		PopupHandler.close(popup);
	}
	
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	public void setCloseButtonVisible(boolean visible) {
		closeBtn.setVisible(visible);
	}
}
