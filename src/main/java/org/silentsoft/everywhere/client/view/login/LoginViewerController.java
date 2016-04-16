package org.silentsoft.everywhere.client.view.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.core.CommonConst;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
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

public class LoginViewerController extends AbstractViewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewerController.class);
	
	@FXML
	private TextField txtLogin;
	
	@FXML
	private PasswordField txtPassword;
	
	@FXML
	private Button btnLogin;
	
	@Override
	protected void initialize(Parent viewer, Object... parameters) {
		SharedMemory.getDataMap().put(BizConst.KEY_APP_LOGIN_STATUS, false);
		
		String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
		if (ObjectUtil.isEmpty(userId)) {
			// Why do i write many code to just request focus on text field.. ?
			new Thread(() -> {
				try {
					Thread.sleep(500);
					
					Platform.runLater(() -> {
						txtLogin.requestFocus();
					});
				} catch (Exception e) {
					;
				}
			}).start();
		} else {
			txtLogin.setText(userId);
			
			new Thread(() -> {
				try {
					Thread.sleep(500);
					
					Platform.runLater(()->{
						txtPassword.requestFocus();
					});
				} catch (Exception e) {
					;
				}
			}).start();
		}
		
		SharedMemory.getDataMap().clear();
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

			TbmSysUserDVO param = new TbmSysUserDVO();
			if (txtLogin.getText().indexOf(CommonConst.AT) != -1) {
				param.setEmailAddr(txtLogin.getText());
			} else {
				param.setUserId(txtLogin.getText());
			}
			param.setUserPwd(SecurityUtil.encodePassword(txtPassword.getText()));
			param.setLangCode(SystemUtil.getLanguage());
			param.setFnlAccsIp(SystemUtil.getHostAddress());
			
			param = RESTfulAPI.doPost("/fx/login/authentication", param, TbmSysUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				AnimationUtils.createTransition(btnLogin, AnimationType.PANIC_SHAKE).play();
			} else {
				SharedMemory.getDataMap().put(BizConst.KEY_APP_LOGIN_STATUS, true);
				
				SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, param.getUserId());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_SEQ, param.getUserSeq());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_EMAIL, param.getEmailAddr());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_NAME, param.getUserName());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_FNL_ACCS_DT, param.getFnlAccsDt());
				
				EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_INDEX);
			}
		} catch (Exception e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "Response Failure", "Please contact administrator :(");
		}
	}
	
	@FXML
	private void register_OnMouseClick() {
		EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_REGISTER);
	}
	
	private boolean isValidate() {
		if (txtLogin.getText().length() <= BizConst.SIZE_EMPTY) {
			MessageBox.showError(App.getStage(), "ID or Email is empty !");
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
