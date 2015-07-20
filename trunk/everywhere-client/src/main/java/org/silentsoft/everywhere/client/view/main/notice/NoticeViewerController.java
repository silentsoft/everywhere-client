package org.silentsoft.everywhere.client.view.main.notice;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.TableView;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NoticeViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeViewerController.class);
	
	@FXML
	private MasterDetailPane masterDetailPane;
	
	private TableView tableView;
	
	private PropertySheet propertySheet;
	
	protected void initialize() {
//		Platform.runLater(() -> {
//			masterDetailPane.setMasterNode(tableView);
//			masterDetailPane.setDetailNode(propertySheet);
//			masterDetailPane.setDetailSide(Side.BOTTOM);
//			masterDetailPane.setShowDetailNode(true);
//		});
	}
}
