package org.silentsoft.everywhere.client.view.index;

import javafx.fxml.FXML;
import javafx.scene.Parent;

import org.silentsoft.everywhere.client.view.login.LoginViewerController;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.ui.viewer.AbstractViewerController;

public class IndexViewerController extends AbstractViewerController {

	@Override
	protected void initialize(Parent viewer, Object... parameters) {
		
	}

	@FXML
	private void cloud_OnActionClick() {
		EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_CLOUD);
	}
	
	@FXML
	private void wiki_OnActionClick() {
		EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_WIKI);
	}
}
