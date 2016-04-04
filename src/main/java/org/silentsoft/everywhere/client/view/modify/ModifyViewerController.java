package org.silentsoft.everywhere.client.view.modify;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.apache.commons.lang.StringUtils;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.model.table.TbmSmUserDVO;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifyViewerController extends AbstractViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyViewerController.class);
	
	@FXML
	TextField txtName;
	
	@FXML
	Button btnDoneInName;
	
	@FXML
	Button btnCancelInName;
	
	@FXML
	PasswordField txtCurrentPassword;
	
	@FXML
	PasswordField txtNewPassword;
	
	@FXML
	PasswordField txtConfirmPassword;
	
	@FXML
	Button btnDoneInPassword;
	
	@FXML
	Button btnCancelInPassword;
	
	@FXML
	TextField txtEmail;
	
	@FXML
	Button btnDoneInEmail;
	
	@FXML
	Button btnCancelInEmail;
	
	@Override
	protected void initialize(Parent viewer, Object... parameters) {
		txtName.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NM)));
		txtEmail.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_EMAIL)));
	}
	
	@FXML
	private void btnDoneInName_OnActionClick() {
		if (isNotValidateName()) {
			return;
		}
		
		try {
			TbmSmUserDVO param = new TbmSmUserDVO();
			param.setUserId(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID)));
			param.setUserNm(txtName.getText());
			
			param = RESTfulAPI.doPost("/fx/modify/update", param, TbmSmUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				MessageBox.showError(App.getStage(), "Modify Failed.. Try again !!!");
			} else {
				saveUserInfoToSharedMemory(param);
				btnCancelInName_OnActionClick();
			}
		} catch (EverywhereException e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "response failure from server :(");
		}
	}
	
	@FXML
	private void btnCancelInName_OnActionClick() {
		Platform.runLater(() ->
		{
			txtName.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NM)));
			txtName.setText(CommonConst.NULL_STR);
		});
	}
	
	@FXML
	private void btnDoneInPassword_OnActionClick() {
		
	}
	
	@FXML
	private void btnCancelInPassword_OnActionClick() {
		
	}
	
	@FXML
	private void btnDoneInEmail_OnActionClick() {
		
	}
	
	@FXML
	private void btnCancelInEmail_OnActionClick() {
		
	}
	
	private boolean isValidateName() {
		if (txtName.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "Name is empty !");
			return false;
		}
		
		if (StringUtils.isNumeric(txtName.getText())) {
			MessageBox.showError(App.getStage(), "Name cannot be numeric !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidateName() {
		return !isValidateName();
	}
	
	private void saveUserInfoToSharedMemory(TbmSmUserDVO tbmSmUserDVO) {
		if (ObjectUtil.isNotEmpty(tbmSmUserDVO.getUserNm())) {
			SharedMemory.getDataMap().put(BizConst.KEY_USER_NM, tbmSmUserDVO.getUserNm());
		}
	}
	
}
