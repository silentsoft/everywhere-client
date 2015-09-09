package org.silentsoft.everywhere.client.component.tree;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CloudTreeItem extends HBox {

	private String filePath;
	
	private boolean isDirectory;
	
	private String fileName;
	
	private String fileSize;
	
	private boolean isDeleted;
	
	public CloudTreeItem(String filePath, boolean isDirectory, String fileName, String fileSize, boolean isDeleted) {
		super();
		
		setFilePath(filePath);
		setDirectory(isDirectory);
		setFileName(fileName);
		setFileSize(fileSize);
		setDeleted(isDeleted);
		
		getChildren().add((new Label(getFileName())));
		
		if (isDirectory() == false) {
			getChildren().add(new Label(" - ".concat(getFileSize())));
		}
	}

	public String getFilePath() {
		return filePath;
	}

	private void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	private void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public String getFileName() {
		return fileName;
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	private void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	private void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
}
