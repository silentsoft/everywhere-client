package org.silentsoft.everywhere.client.view.main;

import java.io.File;
import java.util.regex.Pattern;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
import org.silentsoft.everywhere.client.view.main.notice.NoticeViewer;
import org.silentsoft.everywhere.client.view.main.upload.UploadViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
import org.silentsoft.everywhere.context.fx.main.vo.CloudDirectoryInDVO;
import org.silentsoft.everywhere.context.fx.main.vo.CloudDirectoryOutDVO;
import org.silentsoft.everywhere.context.fx.main.vo.MainSVO;
import org.silentsoft.everywhere.context.fx.main.vo.NoticeInDVO;
import org.silentsoft.everywhere.context.fx.main.vo.NoticeOutDVO;
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
	private Button btnUpload;
	
	@FXML
	private TreeView treeCloudViewer;
	
	@FXML
	private TableView tableCloudViewer;
	
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
		
		ContextMenu contextMenu = new ContextMenu();
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem downloadMenuItem = new MenuItem("Download");
		downloadMenuItem.setOnAction(actionEvent -> {
			for (Object selectedItem : tableCloudViewer.getSelectionModel().getSelectedItems()) {
				if (selectedItem != null && selectedItem instanceof CloudDirectoryOutDVO) {
					CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;
					
				}
			}
		});
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(actionEvent -> {
			for (Object selectedItem : tableCloudViewer.getSelectionModel().getSelectedItems()) {
				if (selectedItem != null && selectedItem instanceof CloudDirectoryOutDVO) {
					CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;
					
				}
			}
		});
		contextMenu.getItems().addAll(downloadMenuItem, separatorMenuItem, deleteMenuItem);
		
		tableCloudViewer.setContextMenu(contextMenu);
		tableCloudViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableCloudViewer.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getClickCount() >= CommonConst.MOUSE_DOUBLE_CLICK) {
				Object selectedItem = tableCloudViewer.getSelectionModel().getSelectedItem();
				if (selectedItem != null && selectedItem instanceof CloudDirectoryOutDVO) {
					CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;
					if (cloudDirectoryOutDVO.getDirectoryYn().equals("Y")) {
						TreeItem<String> item = getCrumb(cloudDirectoryOutDVO.getFileName());
						
						breadCrumbBar.setSelectedCrumb(item);
						displayCloudDirectory();
					}
				}
			}
		});
//		viewCloudDirectory.setOnDragDetected(dragEvent -> {
//			ObservableList<Object> selectedItems = viewCloudDirectory.getSelectionModel().getSelectedItems();
//			if (selectedItems != null) {
//				Dragboard dragboard = viewCloudDirectory.startDragAndDrop(TransferMode.COPY);
//				
//				List<File> files = new ArrayList<File>();
//				for (Object selectedItem : selectedItems) {
//					if (selectedItem instanceof CloudDirectoryOutDVO) {
//						CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;
//						File file = new File(cloudDirectoryOutDVO.getFileName());
//						
//						files.add(file);
//					}
//				}
//				
//				ClipboardContent content = new ClipboardContent();
//				content.putFiles(files);
//				
//				dragboard.setContent(content);
//			}
//		});
		
		crumb = new TreeItem<String>(File.separator);
		breadCrumbBar.setSelectedCrumb(crumb);
		breadCrumbBar.setOnCrumbAction(breadCrumbAction -> {
			displayCloudDirectory(breadCrumbAction.getSelectedCrumb());
		});
	}
	
	private TreeItem<String> getCrumb(String path) {
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
		
		return leafChild;
	}
	
	private void displayNotices() {
		try {
			NoticeInDVO noticeInDVO = new NoticeInDVO();
			noticeInDVO.setLangCode(SysUtil.getLanguage());
			
			getMainSVO().setNoticeInDVO(noticeInDVO);
			
			mainSVO = RESTfulAPI.doPost("/fx/main/notice", getMainSVO(), MainSVO.class);
			
			if (threadNotice != null) {
				threadNotice = null;
			}
			threadNotice = new Thread(() -> {
				while (true) {
					for (NoticeOutDVO noticeOutDVO : getMainSVO().getNoticeOutDVOList()) {
						Platform.runLater(() -> {
							setNotice(noticeOutDVO.getTitle());
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
			
			tableCloudViewer.setItems(FXCollections.observableList(getMainSVO().getCloudDirectoryOutDVOList()));
		} catch (Exception e) {
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

	@Override
	public void onEvent(String event) {
		switch (event) {
		case BizConst.EVENT_REFRESH_CLOUD_DIRECTORY:
			displayCloudDirectory();
			break;
		}
		
	}
}
