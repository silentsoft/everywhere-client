package org.silentsoft.everywhere.client.view.cloud.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.component.popup.PopupHandler;
import org.silentsoft.everywhere.client.model.FileModel;
import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.BizConst;
//import org.silentsoft.everywhere.context.model.pojo.FilePOJO;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.component.notification.Notification;
import org.silentsoft.ui.component.notification.Notification.NotifyType;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadViewerController extends AbstractViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadViewerController.class);
	
	private Parent uploadViewer;
	
	@FXML
	private TableView tableView;
	
	@FXML
	private TableColumn colFilePath;
	
	@FXML
	private TableColumn colSize;
	
	@FXML
	private Button btnSelect;
	
	@FXML
	private Button btnClose;
	
	@FXML
	private Button btnUpload;
	
	private ObservableList<FileModel> fileModelList;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Parent viewer, Object... parameters) {
		this.uploadViewer = viewer;
		
		colFilePath.setCellFactory(new Callback<TableColumn<FileModel, Object>, TableCell<FileModel, Object>>() {
			@Override
			public TableCell<FileModel, Object> call(TableColumn<FileModel, Object> param) {
				TableCell<FileModel, Object> cell = new TableCell<FileModel, Object>() {
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
		
		colFilePath.setCellValueFactory(new PropertyValueFactory<FileModel, Object>("path"));
		colSize.setCellValueFactory(new PropertyValueFactory<FileModel, Object>("size"));
		
		fileModelList = FXCollections.observableArrayList();
		tableView.setItems(fileModelList);
		
		tableView.setOnDragOver(dragEvent -> {
			Dragboard dragboard = dragEvent.getDragboard();
			if (dragboard.hasFiles()) {
				dragEvent.acceptTransferModes(TransferMode.LINK);
			} else {
				dragEvent.consume();
			}
		});
		
		tableView.setOnDragDropped(dragEvent -> {
			Dragboard dragboard = dragEvent.getDragboard();
			if (dragboard.hasFiles()) {
				addFiles(dragboard.getFiles());
				
				dragEvent.setDropCompleted(true);
				dragEvent.consume();
			}
		});
	}
	
	@FXML
	private void select_OnMouseClick() {
		Platform.runLater(() -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Upload Files");
			
			addFiles(fileChooser.showOpenMultipleDialog(App.getStage()));
		});
	}
	
	private void addFiles(List<File> files) {
		if (files == null) {
			return;
		}
		
		for (File file : files) {
			addFile(file);
		}
	}
	
	private void addFile(File file) {
		if (file == null) {
			return;
		}
		
		String parent = file.getParent();
		
		try {
			Files.walkFileTree(Paths.get(file.getAbsolutePath()), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (dir.toString().length() >= CommonConst.MAX_DIRECTORY_LENGTH) {
						LOGGER.error("The depth is to deep ! it will be skip subtree <{}>", new Object[]{dir});
						return FileVisitResult.SKIP_SUBTREE;
					}
					
					String path = dir.toString();
					path = path.substring(parent.length(), path.length());
					
					FileModel fileModel = new FileModel();
					fileModel.setDirectory(true);
					fileModel.setPath(path);
					
					fileModelList.add(fileModel);
					
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					File visitFile = file.toFile();
					
					String path = file.toString();
					path = path.substring(parent.length(), path.length());
					
					FileModel fileModel = new FileModel();
					fileModel.setDirectory(false);
					fileModel.setPath(path);
					fileModel.setFile(visitFile);
					fileModel.setLength(visitFile.length());
					
					fileModelList.add(fileModel);
					
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					LOGGER.error("Visit file failed ! <{}>", new Object[]{file});
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
	}
	
	@FXML
	private void upload_OnMouseClick() {
		Platform.runLater(() -> {
			long startTime = System.currentTimeMillis();
			String userSeq = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_SEQ));
			if (ObjectUtil.isEmpty(userSeq)) {
				MessageBox.showError(App.getStage(), "Wrong User", "Please contact administrator :(");
				return;
			}
			
			for (FileModel fileModel : fileModelList) {
//				FilePOJO filePOJO = new FilePOJO();
//				try {
//					filePOJO.setDirectory(fileModel.isDirectory());
//					filePOJO.setPath(fileModel.getPath());
//					
//					Path path = Paths.get(filePOJO.getPath());
//					String fileName = path.getFileName().toString(); 
//					
//					if (filePOJO.isDirectory() == false) {
//						filePOJO.setName(FileUtil.getName(fileName));
//						filePOJO.setExtension(FileUtil.getExtension(fileName));
//						filePOJO.setSize(fileModel.getSize());
//						filePOJO.setInputStream(new FileInputStream(fileModel.getFile()));
//					} else {
//						filePOJO.setName(fileName);
//					}
//					
//					RESTfulAPI.doMultipart("/fx/cloud/upload", filePOJO);
//					
//				} catch (Exception e) {
//					LOGGER.error(e.toString());
//					return;
//				}
			}
			long endTime = System.currentTimeMillis();
			
			Notification.show(App.getStage(), "Transfer Complete", fileModelList.size() + " files sending succeed in " + (endTime-startTime) + "ms", NotifyType.INFORMATION);
			
			EventHandler.callEvent(UploadViewerController.class, BizConst.EVENT_REFRESH_CLOUD_DIRECTORY);
			
			PopupHandler.close(uploadViewer);
		});
	}
	
}
