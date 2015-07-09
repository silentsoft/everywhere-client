package org.silentsoft.everywhere.client.view.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


import org.controlsfx.dialog.Dialog;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.button.ImageButton;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;

public class MainViewerController {
	
	@FXML
	private Label lblSingleId;
	
	@FXML
	private ImageButton btnManage;
	
	@FXML
	private TreeView treeDirectory;
	
	private TreeItem<String> rootDirectory;
	
	public void initialize() {
		Platform.runLater(() -> {
//			String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
			String userNm = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NM));
			if (ObjectUtil.isNotEmpty(userNm)) {
				lblSingleId.setText(userNm);
			}
			
			//TEMP CODING
			rootDirectory = new TreeItem<String>();
			rootDirectory.setExpanded(true);
			
			TreeItem<String> recycleBin = new TreeItem<String>("Recycle Bin");
			recycleBin.getChildren().add(new TreeItem<String>("WTF.avi"));
			
			rootDirectory.getChildren().add(recycleBin);
			
			treeDirectory.setRoot(rootDirectory);
			//
		});
	}
	
	@FXML
	private void modify_OnMouseClick() {
		EventHandler.callEvent(MainViewerController.class, BizConst.EVENT_VIEW_MODIFY);
	}
	
	@FXML
	private void logout_OnMouseClick() {
		if (MessageBox.showConfirm(App.getStage(), "Are you sure to logout ?") == Dialog.ACTION_YES) {
			EventHandler.callEvent(MainViewerController.class, BizConst.EVENT_VIEW_LOGIN);
		}
	}
}
