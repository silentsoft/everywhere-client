package org.silentsoft.everywhere.client.view.main.notice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import org.controlsfx.control.MasterDetailPane;
import org.silentsoft.core.util.SysUtil;
import org.silentsoft.everywhere.context.fx.main.vo.MainSVO;
import org.silentsoft.everywhere.context.fx.main.vo.NoticeInDVO;
import org.silentsoft.everywhere.context.fx.main.vo.NoticeOutDVO;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoticeViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeViewerController.class);
	
	@FXML
	private MasterDetailPane masterDetailPane;
	
	private TableView tableView;
	
	private TextArea textArea;
	
	private MainSVO mainSVO;
	
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
		initializeComponents();
		
		getNotices();
		displayNotices();
	}
	
	private void initializeComponents() {
		//700x350
		tableView = new TableView();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setOnMouseReleased(mouseEvent -> {
			Object selectedObject = tableView.getSelectionModel().getSelectedItem();
			if (selectedObject != null) {
				NoticeOutDVO selectedNotice = (NoticeOutDVO)selectedObject;
				textArea.setText(selectedNotice.getCont().toString());
			}
		});
		
		TableColumn colDate = new TableColumn("Date");
		colDate.setMinWidth(120);
		colDate.setCellValueFactory(new PropertyValueFactory<NoticeOutDVO, Object>("noticeDt"));
		
		TableColumn colTitle = new TableColumn("Title");
		colTitle.setMinWidth(180);
		colTitle.setCellValueFactory(new PropertyValueFactory<NoticeOutDVO, Object>("title"));
		
		TableColumn colContent = new TableColumn("Content");
		colContent.setMinWidth(360);
		colContent.setCellValueFactory(new PropertyValueFactory<NoticeOutDVO, Object>("cont"));
		
		TableColumn colAuthor = new TableColumn("Author");
		colAuthor.setMinWidth(88);
		colAuthor.setCellValueFactory(new PropertyValueFactory<NoticeOutDVO, Object>("fnlUpderId"));
		
		tableView.getColumns().addAll(colDate, colTitle, colContent, colAuthor);
		
		
		textArea = new TextArea();
		
		masterDetailPane.setMasterNode(tableView);
		masterDetailPane.setDetailNode(textArea);
		masterDetailPane.setDetailSide(Side.BOTTOM);
		masterDetailPane.setShowDetailNode(true);
	}
	
	private void getNotices() {
		try {
			NoticeInDVO noticeInDVO = new NoticeInDVO();
			noticeInDVO.setLangCode(SysUtil.getLanguage());
			
			getMainSVO().setNoticeInDVO(noticeInDVO);
			
			setMainSVO(RESTfulAPI.doPost("/fx/main/notice", getMainSVO(), MainSVO.class));
		} catch (EverywhereException e) {
			LOGGER.error(e.toString());
		}
	}
	
	private void displayNotices() {
		ObservableList<NoticeOutDVO> noticeData = FXCollections.observableArrayList(getMainSVO().getNoticeOutDVOList());
		
		tableView.setItems(noticeData);
	}
}
