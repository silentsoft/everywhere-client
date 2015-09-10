package org.silentsoft.everywhere.client.view.main;

import java.io.File;
import java.util.regex.Pattern;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.dialog.Dialog;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.component.notification.Notification;
import org.silentsoft.core.component.notification.Notification.NotifyType;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.event.EventListener;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SysUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.component.button.ImageButton;
import org.silentsoft.everywhere.client.component.popup.PopupHandler;
import org.silentsoft.everywhere.client.component.popup.PopupHandler.CloseType;
import org.silentsoft.everywhere.client.component.tree.CloudTreeView;
import org.silentsoft.everywhere.client.view.main.notice.NoticeViewer;
import org.silentsoft.everywhere.client.view.main.upload.UploadViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
import org.silentsoft.everywhere.context.fx.main.vo.CloudDirectoryInDVO;
import org.silentsoft.everywhere.context.fx.main.vo.CloudDirectoryOutDVO;
import org.silentsoft.everywhere.context.fx.main.vo.MainSVO;
import org.silentsoft.everywhere.context.fx.main.vo.Notice001DVO;
import org.silentsoft.everywhere.context.fx.main.vo.Notice002DVO;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainViewerController implements EventListener {
	
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
	private BreadCrumbBar<String> breadCrumbBar;
	
	private TreeItem<String> crumb;
	
	@FXML
	private CloudTreeView treeCloud;
	
	@FXML
	private Button btnUpload;
	
//	private TreeItem<File> rootCloud;
	
	
	@FXML
	private TableView viewCloudDirectory;
	
	@FXML
	private TableColumn colName;
	
	@FXML
	private TableColumn colSize;
	
	@FXML
	private TableColumn colModified;
	
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
		EventHandler.addListener(this);
		
		Platform.runLater(() -> {
			initializeComponents();
			
			displayNotices();
			displayUserInfo();
//			displayClouds();
			displayCloudDirectory();
		});
	}
	
	@SuppressWarnings("unchecked")
	private void initializeComponents() {
		colName.setCellFactory(new Callback<TableColumn<CloudDirectoryOutDVO, Object>, TableCell<CloudDirectoryOutDVO, Object>>() {
			@Override
			public TableCell<CloudDirectoryOutDVO, Object> call(TableColumn<CloudDirectoryOutDVO, Object> param) {
				TableCell<CloudDirectoryOutDVO, Object> cell = new TableCell<CloudDirectoryOutDVO, Object>() {
					@Override
					protected void updateItem(Object item, boolean empty) {
						super.updateItem(item, empty);
						
						if (empty || item == null) {
							setGraphic(null);
						} else {
							HBox hBox = new HBox();
							hBox.setAlignment(Pos.CENTER_LEFT);
							
							Label fileName = new Label(item.toString());
							ImageView icon = new ImageView(SysUtil.getIconFromExtensionFx(FileUtil.getExtension(item.toString())));
							
							hBox.getChildren().addAll(icon, fileName);
							
							setGraphic(hBox);
						}
					}
				};
				return cell;
			}
			
		});
		
		colName.setCellValueFactory(new PropertyValueFactory<CloudDirectoryOutDVO, Object>("fileName"));
		colSize.setCellValueFactory(new PropertyValueFactory<CloudDirectoryOutDVO, Object>("fileSize"));
		colModified.setCellValueFactory(new PropertyValueFactory<CloudDirectoryOutDVO, Object>("fnlUpdDt"));
		
		viewCloudDirectory.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getClickCount() >= CommonConst.MOUSE_DOUBLE_CLICK) {
				Object selectedItem = viewCloudDirectory.getSelectionModel().getSelectedItem();
				if (selectedItem != null && selectedItem instanceof CloudDirectoryOutDVO) {
					CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;//(CloudDirectoryOutDVO) viewCloudDirectory.getSelectionModel().getSelectedItem();
					if (cloudDirectoryOutDVO.getDirectoryYn().equals("Y")) {
						TreeItem<String> item = getCrumb(cloudDirectoryOutDVO.getFileName());
						
						breadCrumbBar.setSelectedCrumb(item);
						displayCloudDirectory();
					}
				}
			}
		});
		
		crumb = new TreeItem<String>(File.separator);
		breadCrumbBar.setSelectedCrumb(crumb);
		breadCrumbBar.setOnCrumbAction(breadCrumbAction -> {
			displayCloudDirectory(breadCrumbAction.getSelectedCrumb());
		});
	}
	
	private TreeItem<String> getCrumb(String path) {
//		String[] currentPathArray = breadCrumbBar.getSelectedCrumb().getValue().split(Pattern.quote(File.separator));
		
		String filePath = breadCrumbBar.getSelectedCrumb().getValue();
		
		TreeItem<String> selectedCrumb = breadCrumbBar.getSelectedCrumb();
		boolean isNotFind = true;
		while (isNotFind) {
			TreeItem<String> parentCrumb = selectedCrumb.getParent();
			if (parentCrumb != null) {
				filePath = parentCrumb.getValue().equals(File.separator) ? parentCrumb.getValue().concat(filePath) : parentCrumb.getValue().concat(File.separator.concat(filePath));
				selectedCrumb = parentCrumb;
			} else {
				isNotFind = false;
			}
		}
		
		String[] currentPathArray = filePath.split(Pattern.quote(File.separator));
		
		
		boolean isFirst = true;
		TreeItem<String> tempParent = null;
		TreeItem<String> leafChild = new TreeItem<String>(path);
		for (int i = currentPathArray.length-1; i>=0; i--) {
			String dir = currentPathArray[i];
			if (dir.equals("") == false) {
				TreeItem<String> parent = new TreeItem<String>(dir);
				if (isFirst) {
					parent.getChildren().add(leafChild);
					isFirst = false;
				} else {
					parent.getChildren().add(tempParent);
				}
				tempParent = parent;
			}
		}
		
		TreeItem<String> root = new TreeItem<String>(File.separator);
		if (tempParent == null) {
			root.getChildren().add(leafChild);
		} else {
			root.getChildren().add(tempParent);
		}
//		TreeItem<String> root = new TreeItem<String>(File.separator);
//		for (String dir : currentPathArray) {
//			if (dir.equals("") == false) {
//				root.getChildren().add(new TreeItem<String>(dir));
//			}
//		}
//		
//		TreeItem<String> item = new TreeItem<String>(path);
//		root.getChildren().add(item);
		
		return leafChild;
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
	
	private void displayCloudDirectory() {
		displayCloudDirectory(breadCrumbBar.getSelectedCrumb());
	}
	
	private void displayCloudDirectory(TreeItem<String> crumbBar) {
		try {
			String filePath = crumbBar.getValue();
			
			TreeItem<String> selectedCrumb = crumbBar;
			boolean isNotFind = true;
			while (isNotFind) {
				TreeItem<String> parentCrumb = selectedCrumb.getParent();
				if (parentCrumb != null) {
					filePath = parentCrumb.getValue().equals(File.separator) ? parentCrumb.getValue().concat(filePath) : parentCrumb.getValue().concat(File.separator.concat(filePath));
					selectedCrumb = parentCrumb;
				} else {
					isNotFind = false;
				}
			}
			
			CloudDirectoryInDVO cloudDirectoryInDVO = new CloudDirectoryInDVO();
			cloudDirectoryInDVO.setFilePath(filePath);
			
			getMainSVO().setCloudDirectoryInDVO(cloudDirectoryInDVO);
			
			mainSVO = RESTfulAPI.doPost("/fx/main/cloudDirectory", getMainSVO(), MainSVO.class);
			
			viewCloudDirectory.setItems(FXCollections.observableList(getMainSVO().getCloudDirectoryOutDVOList()));
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
//	private void displayClouds() {
//		try {
//			String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
//			
//			Cloud001DVO cloud001DVO = new Cloud001DVO();
//			cloud001DVO.setUserId(userId);
//			
//			getMainSVO().setCloud001DVO(cloud001DVO);
//			
//			mainSVO = RESTfulAPI.doPost("/fx/main/cloud", getMainSVO(), MainSVO.class);
//
//			for (Cloud002DVO cloud002DVO : getMainSVO().getCloud002DVOList()) {
//				String filePath = cloud002DVO.getFilePath();
//				boolean isDirectory = "Y".equals(cloud002DVO.getDirectoryYn()) ? true : false;
//				String fileName = cloud002DVO.getFileName();
//				String fileSize = cloud002DVO.getFileSize();
//				boolean isDeleted = "Y".equals(cloud002DVO.getDelYn()) ? true : false;
//				treeCloud.add(new CloudTreeItem(filePath, isDirectory, fileName, fileSize, isDeleted));
//			}
//			treeCloud.synchronization();
//			
////			rootCloud = new TreeItem<FileNode>();
////			rootCloud.setExpanded(true);
//			
////			TreeItem<FileNode> recycleBin = new TreeItem<FileNode>(new FileNode(NodeType.RECYCLE_BIN, "Recycle Bin"));
////			rootCloud.getChildren().add(recycleBin);
//			
//			/**
//			 *  FILE_PATH               ||  DIRECTORY_YN  ||  FILE_NAME
//			 *  "test"					||  "Y"           || "test"
//				"test\test"				||  "Y"           || "test"
//				"test\test\test.txt"    ||  "N"			  || "test.txt"
//				"test\trass.txt"		||  "N"			  || "trass.txt"
//				"test\trass - ���纻.txt" ||  "N"			  || "trass - ���纻.txt"
//			 */
//			
//			/**
//			 * if FILE_PATH doesnt have file.separator, then insert to root.
//			 *   --> if DIRECTORY_YN is "Y" then create Directory to root.
//			 *       or DIRECTORY_YN is "N" then create FILE to root.
//			 * or FILE_PATH have file.separator, then find parent DIRECTORY node and insert to that node.
//			 *   --> if DIRECTORY_YN is "Y" then create Directory to parent DIRECTORY node.
//			 *       or DIRECTORY_YN is "N" then create FILE to parent DIRECTORY node.
//			 */
//			
////			for (Cloud002DVO cloud002DVO : getMainSVO().getCloud002DVOList()) {
////				boolean isRootPath = (cloud002DVO.getFilePath().indexOf(File.separator) == -1 ? true : false);
////				if (isRootPath) {
////					TreeItem<String> item = new TreeItem<String>(cloud002DVO.getFileName());
////					rootCloud.getChildren().add(item);
////				} else {
////					for (int i=0, j=rootCloud.getChildren().size(); i<j; i++) {
////						TreeItem<String> item = rootCloud.getChildren().get(i);
////						if (cloud002DVO.getFileName().equals(item.getValue())) {
////							continue;
////						} else {
////							if (item.getValue().equals(cloud002DVO.getFilePath().substring(0, cloud002DVO.getFilePath().lastIndexOf(File.separator)))) {
////								if (cloud002DVO.getDirectoryYn().equals("Y")) {
////									item.getParent().getChildren().add(new TreeItem<String>(cloud002DVO.getFileName()));
////									break;
////								} else if (cloud002DVO.getDirectoryYn().equals("N")) {
////									item.getChildren().add(new TreeItem<String>(cloud002DVO.getFileName()));
////									break;
////								}
////							}
////						}
////					}
////				}
////			}
//			
////			treeCloud.setRoot(rootCloud);
//		} catch (EverywhereException e) {
//			LOGGER.error(e.toString());
//		}
//	}
	
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

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_REFRESH_CLOUD_DIRECTORY:
			displayCloudDirectory();
			break;
		}
		
	}
}
