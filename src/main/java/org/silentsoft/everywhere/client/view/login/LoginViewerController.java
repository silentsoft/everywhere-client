package org.silentsoft.everywhere.client.view.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jidefx.animation.AnimationType;
import jidefx.animation.AnimationUtils;

import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.core.util.SystemUtil;
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
	
	@FXML
	private Button btnLogin;
	
	protected void initialize() {
		Platform.runLater(() -> {
			SharedMemory.getDataMap().put(BizConst.KEY_APP_LOGIN_STATUS, false);
			
			String userId = ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_USER_ID));
			if (ObjectUtil.isEmpty(userId)) {
				// Why do i write many code to just request focus on text field.. ?
				new Thread(() -> {
					try {
						Thread.sleep(500);
						
						Platform.runLater(() -> {
							txtSingleId.requestFocus();
						});
					} catch (Exception e) {
						;
					}
				}).start();
			} else {
				txtSingleId.setText(userId);
				
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
			param.setLangCode(SystemUtil.getLanguage());
			param.setFnlAccsIp(SystemUtil.getHostAddress());
			
			param = RESTfulAPI.doPost("/fx/login/authentication", param, TbmSmUserDVO.class);
			
			if (param == null || ObjectUtil.isEmpty(param)) {
				AnimationUtils.createTransition(btnLogin, AnimationType.PANIC_SHAKE).play();
			} else {
				SharedMemory.getDataMap().put(BizConst.KEY_APP_LOGIN_STATUS, true);
				
				SharedMemory.getDataMap().put(BizConst.KEY_USER_ID, param.getSingleId());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_UNIQUE_SEQ, param.getUniqueSeq());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_EMAIL, param.getEmailAddr());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_NM, param.getUserNm());
				SharedMemory.getDataMap().put(BizConst.KEY_USER_FNL_ACCS_DT, param.getFnlAccsDt());
				
				EventHandler.callEvent(LoginViewerController.class, BizConst.EVENT_VIEW_MAIN);
			}
		} catch (EverywhereException e) {
			LOGGER.error("I got catch an error !", new Object[]{e});
			MessageBox.showError(App.getStage(), "Response Failure", "Please contact administrator :(");
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
