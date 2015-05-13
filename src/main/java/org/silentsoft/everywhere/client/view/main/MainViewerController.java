package org.silentsoft.everywhere.client.view.main;

import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainViewerController {
	
	@FXML
	private Label lblSingleId;
	
	@FXML
	private Button btnManage;
	
	@FXML
	private Label lblLatest;
	
	public void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
//				String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
				String userNm = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NM));
				if (ObjectUtil.isNotEmpty(userNm)) {
					lblSingleId.setText(userNm);
				}
			}
		});
	}
}
