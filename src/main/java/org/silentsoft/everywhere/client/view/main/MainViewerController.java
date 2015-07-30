package org.silentsoft.everywhere.client.view.main;

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
import org.silentsoft.everywhere.client.utility.PopupHandler;
import org.silentsoft.everywhere.client.utility.PopupHandler.CloseType;
import org.silentsoft.everywhere.client.view.main.notice.NoticeViewer;
import org.silentsoft.everywhere.client.view.main.upload.UploadViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
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
	private TreeView treeDirectory;
	
	@FXML
	private Button btnUpload;
	
	private TreeItem<String> rootDirectory;
	
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
		PopupHandler.show(new NoticeViewer().getNoticeViewer(), CloseType.FOCUS_BASE, true);
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
		PopupHandler.show(new UploadViewer().getUploadViewer(), CloseType.BUTTON_BASE, true);
	}
}
