package org.silentsoft.everywhere.client.view.modify;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.apache.commons.lang.StringUtils;
import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.client.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.model.table.TbmSysUserDVO;
import org.silentsoft.everywhere.context.util.SecurityUtil;
import org.silentsoft.io.memory.SharedMemory;
import org.silentsoft.ui.component.messagebox.MessageBox;
import org.silentsoft.ui.viewer.AbstractViewerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifyViewerController extends AbstractViewerController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyViewerController.class);
	
	@FXML
	TextField txtId;
	
	@FXML
	Button btnDoneInId;
	
	@FXML
	Button btnCancelInId;
	
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
		txtId.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID)));
		txtName.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NAME)));
		txtEmail.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_EMAIL)));
	}
	
	private boolean doModify(TbmSysUserDVO param) {
		try {	
			param = RESTfulAPI.doPost("/fx/modify/update", param, TbmSysUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				MessageBox.showError(App.getStage(), "Modify Failed.. Try again !!!");
			} else {
				saveUserInfoToSharedMemory(param);
				MessageBox.showInformation(App.getStage(), "Complete", "Succeed to change !");
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "response failure from server :(");
		}
		
		return false;
	}
	
	@FXML
	private void btnDoneInId_OnActionClick() {
		if (isNotValidId()) {
			return;
		}
		
		TbmSysUserDVO param = new TbmSysUserDVO();
		param.setUserSeq(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_SEQ)));
		param.setUserId(txtId.getText());
			
		if (doModify(param)) {
			btnCancelInId_OnActionClick();
		}
	}
	
	@FXML
	private void btnCancelInId_OnActionClick() {
		txtId.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID)));
		txtId.setText(CommonConst.NULL_STR);
	}
	
	@FXML
	private void btnDoneInName_OnActionClick() {
		if (isNotValidName()) {
			return;
		}
		
		TbmSysUserDVO param = new TbmSysUserDVO();
		param.setUserSeq(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_SEQ)));
		param.setUserName(txtName.getText());
			
		if (doModify(param)) {
			btnCancelInName_OnActionClick();
		}
	}
	
	@FXML
	private void btnCancelInName_OnActionClick() {
		txtName.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_NAME)));
		txtName.setText(CommonConst.NULL_STR);
	}
	
	@FXML
	private void btnDoneInPassword_OnActionClick() {
		if (isNotValidPassword()) {
			return;
		}
		
		TbmSysUserDVO param = new TbmSysUserDVO();
		param.setUserSeq(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_SEQ)));
		param.setUserPwd(SecurityUtil.encodePassword(txtCurrentPassword.getText()));
		
		try {	
			boolean isPasswordMatch = RESTfulAPI.doPost("/fx/modify/check", param, Boolean.class);
			if (isPasswordMatch) {
				param.setUserPwd(SecurityUtil.encodePassword(txtNewPassword.getText()));
				
				if (doModify(param)) {
					btnCancelInPassword_OnActionClick();
				}
			} else {
				MessageBox.showError(App.getStage(), "Current password is incorrect.");
			}
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "response failure from server :(");
		}
	}
	
	@FXML
	private void btnCancelInPassword_OnActionClick() {
		txtCurrentPassword.setText(CommonConst.NULL_STR);
		txtNewPassword.setText(CommonConst.NULL_STR);
		txtConfirmPassword.setText(CommonConst.NULL_STR);
	}
	
	@FXML
	private void btnDoneInEmail_OnActionClick() {
		if (isNotValidEmail()) {
			return;
		}
		
		TbmSysUserDVO param = new TbmSysUserDVO();
		param.setUserSeq(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_SEQ)));
		param.setEmailAddr(txtEmail.getText());
			
		if (doModify(param)) {
			btnCancelInEmail_OnActionClick();
		}
	}
	
	@FXML
	private void btnCancelInEmail_OnActionClick() {
		txtEmail.setPromptText(ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_EMAIL)));
		txtEmail.setText(CommonConst.NULL_STR);
	}
	
	private boolean isValidId() {
		if (txtId.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "ID is empty !");
			return false;
		}
		
		if (StringUtils.isNumeric(txtId.getText())) {
			MessageBox.showError(App.getStage(), "ID cannot be numeric !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidId() {
		return !isValidId();
	}
	
	private boolean isValidName() {
		if (StringUtils.isNumeric(txtName.getText())) {
			MessageBox.showError(App.getStage(), "Name cannot be numeric !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidName() {
		return !isValidName();
	}
	
	private boolean isValidPassword() {
		String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
		
		if (txtCurrentPassword.getText().equals(userId)) {
			MessageBox.showError(App.getStage(), "Password cannot be same with ID !");
			return false;
		} else if (txtCurrentPassword.getText().equals(txtNewPassword.getText())) {
			MessageBox.showError(App.getStage(), "Cannot be same with current password and new password !");
			return false;
		} else if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
			MessageBox.showError(App.getStage(), "Password is not matched !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidPassword() {
		return !isValidPassword();
	}
	
	private boolean isValidEmail() {
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
	
	private boolean isNotValidEmail() {
		return !isValidEmail();
	}
	
	private void saveUserInfoToSharedMemory(TbmSysUserDVO tbmSysUserDVO) {
		if (ObjectUtil.isNotEmpty(tbmSysUserDVO.getUserId())) {
			SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, tbmSysUserDVO.getUserId());
		}
		
		if (ObjectUtil.isNotEmpty(tbmSysUserDVO.getUserName())) {
			SharedMemory.getDataMap().put(BizConst.KEY_USER_NAME, tbmSysUserDVO.getUserName());
		}
		
		if (ObjectUtil.isNotEmpty(tbmSysUserDVO.getEmailAddr())) {
			SharedMemory.getDataMap().put(BizConst.KEY_USER_EMAIL, tbmSysUserDVO.getEmailAddr());
		}
	}
	
}
