package org.silentsoft.everywhere.client.view.cloud;

import java.io.File;
import java.util.Optional;
import java.util.regex.Pattern;

import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.DateUtil;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.component.button.ImageButton;
import org.silentsoft.everywhere.client.component.popup.PopupHandler;
import org.silentsoft.everywhere.client.component.popup.PopupHandler.CloseType;
import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.client.view.cloud.notice.NoticeViewer;
import org.silentsoft.everywhere.client.view.cloud.upload.UploadViewer;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.fx.cloud.vo.CloudDirectoryInDVO;
import org.silentsoft.everywhere.context.fx.cloud.vo.CloudDirectoryOutDVO;
import org.silentsoft.everywhere.context.fx.cloud.vo.CloudSVO;
import org.silentsoft.everywhere.context.fx.cloud.vo.NoticeInDVO;
import org.silentsoft.everywhere.context.fx.cloud.vo.NoticeOutDVO;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.component.notification.Notification;
import org.silentsoft.ui.component.notification.Notification.NotifyType;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudViewerController extends AbstractViewerController implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudViewerController.class);
	
	private CloudSVO cloudSVO;
	
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
	
	private CloudSVO getCloudSVO() {
		if (cloudSVO == null) {
			cloudSVO = new CloudSVO();
		}
		
		return cloudSVO;
	}
	
	private void setCloudSVO(CloudSVO cloudSVO) {
		this.cloudSVO = cloudSVO;
	}
	
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		EventHandler.addListener(this);
		
		initializeComponents();
		
		displayNotices();
		displayUserInfo();
		displayCloudDirectory();
	}
	
	@Override
	public void terminate() {
		super.terminate();
		
		EventHandler.removeListener(this);
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
							ImageView icon = new ImageView(SystemUtil.getIconFromExtensionFx(FileUtil.getExtension(item.toString())));
							
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
			/**
			 * Everywhere Download Architecture
			 * 
			 * CLIENT >>
			 * 	Post full path of file to server (Must send FilePOJO[])
			 *  and open download Popup(Button base) to showing progress
			 *  then list up FilePOJO[] element info to download popup viewer's grid.
			 *  
			 * SERVER >>
			 *  receive FilePOJO[], and return size of each files.
			 *  
			 * CLIENT >>
			 *  receive FilePOJO[], show each size to grid.
			 *  and make thread to download file each step by step. (if 3 files, then make 3 threads.)
			 *  you know, this is very important : make 1 observer thread to check each file size.
			 *  and showing download progress.
			 *  
			 * SERVER >>
			 *  receive one full path of file, and find real path in server(or NAS),
			 *  return that file's byte[] using IOUtils.
			 *  
			 * CLIENT >>
			 *  at observer thread, check if all files are downloaded,
			 *  then make popup message box or make notification & close.
			 */
			
			ObservableList<Object> selectedItems = tableCloudViewer.getSelectionModel().getSelectedItems();
			for (Object selectedItem : selectedItems) {
//				if (selectedItem != null && selectedItem instanceof CloudDirectoryOutDVO) {
//					CloudDirectoryOutDVO cloudDirectoryOutDVO = (CloudDirectoryOutDVO) selectedItem;
//					
//					String path = getCurrentPath();
//					boolean isDirectory = (cloudDirectoryOutDVO.getDirectoryYn() == "Y" ? true : false);
//					String name = cloudDirectoryOutDVO.getFileName();
//					
//					try {
//						long start = System.currentTimeMillis();
//						
//						FilePOJO filePOJO = new FilePOJO();
//						filePOJO.setPath(path);
//						filePOJO.setDirectory(isDirectory);
//						filePOJO.setName(name);
//						
//						filePOJO = RESTfulAPI.doPost("/fx/cloud/download", filePOJO, FilePOJO.class);
//						
//						String downloadPath = "H:\\incoming";
//						downloadPath = downloadPath.concat((filePOJO.getPath().equals(File.separator) ? filePOJO.getPath() : filePOJO.getPath().concat(File.separator)));
//						File downloadDir = new File(downloadPath);
//						if (!downloadDir.exists()) {
//							downloadDir.mkdirs();
//						}
//						
//						String downloadZipFilePath = downloadPath.concat("~".concat(filePOJO.getName()).concat(".zip"));
//						FileOutputStream fileOutputStream = new FileOutputStream(downloadZipFilePath);
//						IOUtils.write(filePOJO.getBytes(), fileOutputStream);
//						fileOutputStream.close();
//						
//						new ZipUtil().unZip(downloadZipFilePath, downloadPath.concat(filePOJO.getName()));
//						new File(downloadZipFilePath).delete();
//						
//						long end = System.currentTimeMillis();
//						
//						MessageBox.showInformation(App.getStage(), "File download .. ", end-start+"".concat(" ms"));
//					} catch (Exception e) {
//						LOGGER.error(e.toString());
//					}
//				}
			}
			
			//PopupHandler.show("File Download", new DownloadViewer().getDownloadViewer(), CloseType.BUTTON_BASE, true);
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
	
	private String getCurrentPath() {
		String currentPath = breadCrumbBar.getSelectedCrumb().getValue();
		
		TreeItem<String> selectedCrumb = breadCrumbBar.getSelectedCrumb();
		boolean isNotFind = true;
		while (isNotFind) {
			TreeItem<String> parentCrumb = selectedCrumb.getParent();
			if (parentCrumb != null) {
				currentPath = parentCrumb.getValue().equals(File.separator) ? parentCrumb.getValue().concat(currentPath) : parentCrumb.getValue().concat(File.separator.concat(currentPath));
				selectedCrumb = parentCrumb;
			} else {
				isNotFind = false;
			}
		}
		
		return currentPath;
	}
	
	private TreeItem<String> getCrumb(String path) {
		String currentPath = getCurrentPath();
		String[] currentPathArray = currentPath.split(Pattern.quote(File.separator));
		
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
			noticeInDVO.setLangCode(SystemUtil.getLanguage());
			
			getCloudSVO().setNoticeInDVO(noticeInDVO);
			
			cloudSVO = RESTfulAPI.doPost("/fx/cloud/notice", getCloudSVO(), CloudSVO.class);
			
			if (threadNotice != null) {
				threadNotice = null;
			}
			threadNotice = new Thread(() -> {
				while (true) {
					for (NoticeOutDVO noticeOutDVO : getCloudSVO().getNoticeOutDVOList()) {
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
		} catch (Exception e) {
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
			
			getCloudSVO().setCloudDirectoryInDVO(cloudDirectoryInDVO);
			
			cloudSVO = RESTfulAPI.doPost("/fx/cloud/cloudDirectory", getCloudSVO(), CloudSVO.class);
			
			tableCloudViewer.setItems(FXCollections.observableList(getCloudSVO().getCloudDirectoryOutDVOList()));
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
		String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
//		String userNm = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NAME));
		if (ObjectUtil.isNotEmpty(userId)) {
			lblSingleId.setText(userId);
			
			try {
				String latest = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_FNL_ACCS_DT));
				lblLatest.setText(DateUtil.getDateAsStr(latest, DateUtil.DATEFORMAT_YYYYMMDDHHMMSS, DateUtil.DATEFORMAT_YYYYMMDDHHMMSS_MASK));
			} catch (Exception e) {
				;
			}
			
			// TODO : Notification is broken under jdk-1.8.77, controlsfx-8.40.10
			// Notification.show(App.getStage(), userId, "Welcome to Everywhere !", NotifyType.INFORMATION);
		}
	}
	
	@FXML
	private void notice_OnMouseClick() {
		PopupHandler.show("Notice", new NoticeViewer().getViewer(), CloseType.FOCUS_BASE, true);
	}
	
	@FXML
	private void modify_OnMouseClick() {
		EventHandler.callEvent(CloudViewerController.class, BizConst.EVENT_VIEW_MODIFY);
	}
	
	@FXML
	private void logout_OnMouseClick() {
		Optional<ButtonType> result = MessageBox.showConfirm(App.getStage(), "Are you sure to logout ?");
		result.ifPresent(buttonType -> {
			if (buttonType == ButtonType.OK) {
				EventHandler.callEvent(CloudViewerController.class, BizConst.EVENT_VIEW_LOGIN);
			}
		});
	}
	
	@FXML
	private void upload_OnMouseClick() {
		PopupHandler.show("File Upload", new UploadViewer().getViewer(), CloseType.BUTTON_BASE, true);
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
