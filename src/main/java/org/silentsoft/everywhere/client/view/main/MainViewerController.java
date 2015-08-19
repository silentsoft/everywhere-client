package org.silentsoft.everywhere.client.view.main;

import java.io.File;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.controlsfx.dialog.Dialog;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.component.notification.Notification;
import org.silentsoft.core.component.notification.Notification.NotifyType;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SysUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.button.ImageButton;
import org.silentsoft.everywhere.client.popup.PopupHandler;
import org.silentsoft.everywhere.client.popup.PopupHandler.CloseType;
import org.silentsoft.everywhere.client.view.main.notice.NoticeViewer;
import org.silentsoft.everywhere.client.view.main.upload.UploadViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
import org.silentsoft.everywhere.context.fx.main.vo.Cloud001DVO;
import org.silentsoft.everywhere.context.fx.main.vo.Cloud002DVO;
import org.silentsoft.everywhere.context.fx.main.vo.MainSVO;
import org.silentsoft.everywhere.context.fx.main.vo.Notice001DVO;
import org.silentsoft.everywhere.context.fx.main.vo.Notice002DVO;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainViewerController.class);
	
	private MainSVO mainSVO;
	
	@FXML
	private Label lblSingleId;
	
	@FXML
	private Label lblLatest;
	
	@FXML
	private Label lblNotice;
	
	private Thread threadNotice;
	
	@FXML
	private ImageButton btnManage;
	
	@FXML
	private TreeView treeCloud;
	
	@FXML
	private Button btnUpload;
	
	private TreeItem<String> rootCloud;
	
	private MainSVO getMainSVO() {
		if (mainSVO == null) {
			mainSVO = new MainSVO();
		}
		
		return mainSVO;
	}
	
	private void setMainSVO(MainSVO mainSVO) {
		this.mainSVO = mainSVO;
	}
	
	protected void initialize() {
		Platform.runLater(() -> {
			displayNotices();
			displayUserInfo();
			displayClouds();
		});
	}
	
	private void displayNotices() {
		try {
			Notice001DVO notice001DVO = new Notice001DVO();
			notice001DVO.setLangCode(SysUtil.getLanguage());
			
			getMainSVO().setNotice001DVO(notice001DVO);
			
			mainSVO = RESTfulAPI.doPost("/fx/main/notice", getMainSVO(), MainSVO.class);
			
			if (threadNotice != null) {
				threadNotice = null;
			}
			threadNotice = new Thread(() -> {
				while (true) {
					for (Notice002DVO notice002DVO : getMainSVO().getNotice002DVOList()) {
						Platform.runLater(() -> {
							setNotice(notice002DVO.getTitle());
						});
						
						try {
							Thread.sleep(10000);
						} catch (Exception e) {
							;
						}
					}
				}
			});
			threadNotice.start();
		} catch (EverywhereException e) {
			LOGGER.error(e.toString());
		}
	}
	
	private void displayClouds() {
		try {
			String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
			
			Cloud001DVO cloud001DVO = new Cloud001DVO();
			cloud001DVO.setUserId(userId);
			
			getMainSVO().setCloud001DVO(cloud001DVO);
			
			mainSVO = RESTfulAPI.doPost("/fx/main/cloud", getMainSVO(), MainSVO.class);

			
			rootCloud = new TreeItem<String>();
			rootCloud.setExpanded(true);
			
			TreeItem<String> recycleBin = new TreeItem<String>("Recycle Bin");
			recycleBin.getChildren().add(new TreeItem<String>("Not support function"));
			rootCloud.getChildren().add(recycleBin);
			
//			for (Cloud002DVO cloud002DVO : getMainSVO().getCloud002DVOList()) {
//				boolean isRootPath = (cloud002DVO.getFilePath().indexOf(File.separator) == -1 ? true : false);
//				if (isRootPath) {
//					TreeItem<String> item = new TreeItem<String>(cloud002DVO.getFileName());
//					rootCloud.getChildren().add(item);
//				} else {
//					for (int i=0, j=rootCloud.getChildren().size(); i<j; i++) {
//						TreeItem<String> item = rootCloud.getChildren().get(i);
//						if (cloud002DVO.getFileName().equals(item.getValue())) {
//							continue;
//						} else {
//							if (item.getValue().equals(cloud002DVO.getFilePath().substring(0, cloud002DVO.getFilePath().lastIndexOf(File.separator)))) {
//								if (cloud002DVO.getDirectoryYn().equals("Y")) {
//									item.getParent().getChildren().add(new TreeItem<String>(cloud002DVO.getFileName()));
//									break;
//								} else if (cloud002DVO.getDirectoryYn().equals("N")) {
//									item.getChildren().add(new TreeItem<String>(cloud002DVO.getFileName()));
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
			
			// for (...)
			//  1. if root path and directoryYn is "N" then insert into the root.
			//  2. else not root path and directoryYn is "Y" then
			//  2-1. (skip self) if already exists directory then insert into the directory nor create.
			//  3. else not root path and directoryYn is "N" then
			//  3-1. (skip self) if already exists parent directory then insert into the file to directory nor create directory, and put.
			
			
			treeCloud.setRoot(rootCloud);
		} catch (EverywhereException e) {
			LOGGER.error(e.toString());
		}
	}
	
	private void setNotice(String notice) {
		Transition fadeOutAnimation = AnimationUtils.createTransition(lblNotice, AnimationType.FADE_OUT_UP);
		fadeOutAnimation.setRate(7.5);
		fadeOutAnimation.setOnFinished(actionEvent -> {
			lblNotice.setText(notice);
			
			Transition fadeInAnimation = AnimationUtils.createTransition(lblNotice, AnimationType.FADE_IN_UP);
			fadeInAnimation.setRate(7.5);
			fadeInAnimation.play();
		});
		fadeOutAnimation.play();
	}
	
	private void displayUserInfo() {
//		String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
		String userNm = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NM));
		if (ObjectUtil.isNotEmpty(userNm)) {
			lblSingleId.setText(userNm);
			
			try {
				String latest = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_FNL_ACCS_DT));
				lblLatest.setText(DateUtil.getDateAsStr(latest, DateUtil.DATEFORMAT_YYYYMMDDHHMMSS, DateUtil.DATEFORMAT_YYYYMMDDHHMMSS_MASK));
			} catch (Exception e) {
				;
			}
			
			Notification.show(App.getStage(), userNm, "Welcome to Everywhere !", NotifyType.INFORMATION);
		}
	}
	
	@FXML
	private void notice_OnMouseClick() {
		PopupHandler.show("Notice", new NoticeViewer().getNoticeViewer(), CloseType.FOCUS_BASE, true);
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
		PopupHandler.show("File Upload", new UploadViewer().getUploadViewer(), CloseType.BUTTON_BASE, true);
	}
}
