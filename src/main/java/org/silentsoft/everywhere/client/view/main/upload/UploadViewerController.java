package org.silentsoft.everywhere.client.view.main.upload;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import org.silentsoft.core.component.notification.Notification;
import org.silentsoft.core.component.notification.Notification.NotifyType;
import org.silentsoft.core.util.FileUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.model.FileModel;
import org.silentsoft.everywhere.client.utility.PopupHandler;
import org.silentsoft.everywhere.context.model.pojo.FilePOJO;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadViewerController {
	
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
	
	
	protected void initialize(Parent uploadViewer) {
		this.uploadViewer = uploadViewer;
		
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
		
		FileModel fileModel = new FileModel();
		fileModel.setFile(file);
		fileModel.setPath(file.getAbsolutePath());
		fileModel.setLength(file.length());
		
		fileModelList.add(fileModel);
	}
	
	@FXML
	private void close_OnMouseClick() {
		PopupHandler.close(uploadViewer);
	}
	
	@FXML
	private void upload_OnMouseClick() {
		Platform.runLater(() -> {
//			StringBuffer sendingInfo = new StringBuffer();
			
			long startTime = System.currentTimeMillis();
			for (FileModel fileModel : fileModelList) {
				File file = fileModel.getFile();
				
				String fileName = file.getName();
				FilePOJO filePOJO = new FilePOJO();
				try {
					filePOJO.setName(FileUtil.getName(fileName));
					filePOJO.setExtension(FileUtil.getExtension(fileName));
					filePOJO.setInputStream(new FileInputStream(file));
					
//					long fileUploadStartTime = System.currentTimeMillis();
					RESTfulAPI.doMultipart("/fx/main/upload", filePOJO);
//					long fileUploadEndTime = System.currentTimeMillis();
					
//					sendingInfo.append(fileModel.getSize() + " " + fileName + " " + (fileUploadEndTime-fileUploadStartTime) + "ms \r\n");
				} catch (Exception e) {
					LOGGER.error(e.toString());
					return;
				}
			}
			long endTime = System.currentTimeMillis();
			
			Notification.show(App.getStage(), "Transfer Complete", fileModelList.size() + " files sending succeed in " + (endTime-startTime) + "ms", NotifyType.INFORMATION);
//			Notification.show(App.getStage(), "Transfer Complete", fileModelList.size() + " files sending succeed in " + (endTime-startTime) + "ms", NotifyType.INFORMATION, (actionEvent) -> {
//				MessageBox.showInformation(App.getStage(), null, sendingInfo.toString());
//			});
			
			close_OnMouseClick();
		});
	}
}
