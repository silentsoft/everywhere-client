package org.silentsoft.everywhere.client.view.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.core.SharedMemory;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.model.table.TbmSmUserDVO;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginViewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewerController.class);
	
	@FXML
	private TextField txtSingleId;
	
	@FXML
	private PasswordField txtPassword;
	
	public void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
				if (ObjectUtil.isEmpty(userId)) {
					txtSingleId.requestFocus();
				} else {
					txtSingleId.setText(userId);
					txtPassword.requestFocus();
				}
				
				SharedMemory.getDataMap().clear();
			}
		});
	}
	
	@FXML
	private void login_OnActionClick() {
		if (isNotValidate()) {
			return;
		}
		
		StringBuffer result = new StringBuffer();
		try {
			String encPassword = "";
			try {
				encPassword = SecurityUtil.HASH_SHA256(txtPassword.getText());
			} catch (Exception e) {
				LOGGER.error("I got catch error during encoding the password !", e);
			}

			String userId = txtSingleId.getText();
			
			TbmSmUserDVO param = new TbmSmUserDVO();
			param.setUserId(userId);
			param.setSingleId(userId);
			param.setUserPwd(SecurityUtil.encodePassword(txtPassword.getText()));
			
			param = RESTfulAPI.doPost("/fx/login/authentication", param, TbmSmUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				MessageBox.showError(App.getStage(), "Login Failed.. Try again !!!");
			} else {
				result.append("Login Succeed ! \r\n\r\n");
				result.append("MES ID: " + param.getUserId() + "\r\n");
				result.append("User Name: " + param.getUserNm() + "\r\n");
				result.append("Eng User Name: " + param.getEngUserNm() + "\r\n");
				result.append("Emp NO: " + param.getEmpNo() + "\r\n");
				result.append("Email Addr: " + param.getEmailAddr() + "\r\n");
				result.append("Dept Code: " + param.getDeptCode() + "\r\n");
				result.append("Dept Name: " + param.getDeptNm() + "\r\n");
				result.append("Eng Dept Name: " + param.getEngDeptNm() + "\r\n");
				result.append("Mobile Tel: " + param.getMobileTel());
				
				//MessageBox.showAbout(App.getStage(), String.format("Welcome, %s", param.getUserNm()), result.toString());
				
				SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, param.getSingleId());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_EMAIL, param.getEmailAddr());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_NM, param.getUserNm());
				
				EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_MAIN);
			}
		} catch (EverywhereException e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "response failure from server :(");
		}
	}
	
	@FXML
	private void register_OnMouseClick() {
		EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_REGISTER);
	}
	
	private boolean isValidate() {
		if (txtSingleId.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "ID is empty !");
			return false;
		}
		
		if (txtPassword.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "Password is empty !");
			return false;
		}
		
		return true;
	}
	
	private boolean isNotValidate() {
		return !isValidate();
	}
}
