package org.silentsoft.everywhere.client.view.main.notice;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoticeViewer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeViewer.class);
	
	private Parent noticeViewer;
	
	private NoticeViewerController noticeViewerController;
	
	public NoticeViewer() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NoticeViewer.fxml"));
			noticeViewer = fxmlLoader.load();
			noticeViewerController = fxmlLoader.getController();
			
			initialize();
		} catch (Exception e) {
			LOGGER.error("Failed initialize notice viewer !", e);
		}
	}
	
	public Parent getNoticeViewer() {
		return noticeViewer;
	}
	
	private void initialize() {
		noticeViewerController.initialize();
	}

}
