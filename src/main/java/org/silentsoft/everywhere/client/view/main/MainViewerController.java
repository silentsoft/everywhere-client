package org.silentsoft.everywhere.client.view.main;

import java.io.File;
import java.io.FileInputStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import org.apache.commons.io.FileUtils;
import org.controlsfx.dialog.Dialog;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ByteArrayUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.button.ImageButton;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.model.pojo.FilePOJO;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainViewerController.class);
	
	@FXML
	private Label lblSingleId;
	
	@FXML
	private ImageButton btnManage;
	
	@FXML
	private TreeView treeDirectory;
	
	@FXML
	private Button btnUpload;
	
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
	
	@FXML
	private void upload_OnMouseClick() {
		String file = "E:\\Bruno mars - Marry You.mp3";
		
		FilePOJO filePOJO = new FilePOJO();
		
		try {
			filePOJO.setName(FileUtil.getName(file));
			filePOJO.setExtension(FileUtil.getExtension(file));
			filePOJO.setInputStream(new FileInputStream(file));
		} catch (Exception e) {
			LOGGER.error(e.toString());
			return;
		}
		
		try {
			RESTfulAPI.doMultipart("/fx/main/upload", filePOJO, null);
		} catch (EverywhereException e) {
			LOGGER.error(e.toString());
		}
	}
}
