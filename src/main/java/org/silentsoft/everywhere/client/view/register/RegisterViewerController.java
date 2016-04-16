package org.silentsoft.everywhere.client.view.register;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.model.table.TbmSysUserDVO;
import org.silentsoft.everywhere.context.util.SecurityUtil;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterViewerController extends AbstractViewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterViewerController.class);
	
	@FXML
	TextField txtUserId;
	
	@FXML
	PasswordField txtPassword;
	
	@FXML
	PasswordField txtConfirm;
	
	@FXML
	TextField txtName;
	
	@FXML
	TextField txtEmail;
	
	@FXML
	Button btnDone;
	
	@FXML
	Button btnCancel;
	
	@Override
	protected void initialize(Parent viewer, Object... parameters) {
		txtUserId.requestFocus();
	}
	
	@FXML
	private void btnDone_OnActionClick() {
		if (isNotValidate()) {
			return;
		}
		
		try {
			String encPassword = "";
			try {
				encPassword = SecurityUtil.HASH_SHA256(txtPassword.getText());
			} catch (Exception e) {
				LOGGER.error("I got catch error during encoding the password !", e);
			}
			
			TbmSysUserDVO param = new TbmSysUserDVO();
			param.setUserId(txtUserId.getText());
			param.setUserPwd(SecurityUtil.encodePassword(txtPassword.getText()));
			param.setUserName(txtName.getText());
			param.setEmailAddr(txtEmail.getText());
			
			param = RESTfulAPI.doPost("/fx/register/authentication", param, TbmSysUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				MessageBox.showError(App.getStage(), "Register Failed.. Try again !!!");
			} else {
				saveUserInfoToSharedMemory(param.getUserId());
				MessageBox.showInformation(App.getStage(), "Welcome", "Succeed to register member !");
				btnCancel_OnActionClick();
			}
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "response failure from server :(");
		}
	}
	
	@FXML
	private void btnCancel_OnActionClick() {
		EventHandler.callEvent(RegisterViewerController.class, BizConst.EVENT_VIEW_LOGIN);
	}
	
	private void saveUserInfoToSharedMemory(String userId) {
		SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, userId);
	}
	
	private boolean isValidate() {
		if (txtUserId.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "ID is empty !");
			return false;
		}
		
		if (txtPassword.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "Password is empty !");
			return false;
		}
		
		if (txtConfirm.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "Password is empty !");
			return false;
		}
		
		if (!txtPassword.getText().equals(txtConfirm.getText())) {
			MessageBox.showError(App.getStage(), "Password is not matched !");
			return false;
		} else if (txtPassword.getText().equals(txtUserId.getText())) {
			MessageBox.showError(App.getStage(), "Password cannot be same with ID !");
			return false;
		}
		
//		if (txtName.getText().length() <= BizConst.SIZE_EMPTY) {
//			MessageBox.showError(App.getStage(), "Name is empty !");
//			return false;
//		}
		
		if (txtEmail.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "Email is empty !");
			return false;
		} else if (txtEmail.getText().indexOf(CommonConst.AT) == -1 ||
				txtEmail.getText().indexOf(CommonConst.DOT) == -1) {
			MessageBox.showError(App.getStage(), "Not available email type !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidate() {
		return !isValidate();
	}
	
}
