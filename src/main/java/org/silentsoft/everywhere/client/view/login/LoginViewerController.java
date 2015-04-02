package org.silentsoft.everywhere.client.view.login;

import org.silentsoft.everywhere.client.application.App;
import org.silentsoft.everywhere.context.rest.RESTfulAPI;
import org.silentsoft.everywhere.context.BizConst;
import org.silentsoft.everywhere.context.host.EverywhereException;
import org.silentsoft.everywhere.context.model.table.TbmSmUserDVO;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.everywhere.context.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginViewerController.class);
	
	@FXML
	private TextField txtSingleId;
	
	@FXML
	private PasswordField txtPassword;
	
	public void initialize() {
		txtSingleId.requestFocus();
	}
	
	@FXML
	private void onActionClick() {
		if (txtSingleId.getText().length() <= BizConst.SIZE_EMPTY) {
			return;
		}
		
		if (txtPassword.getText().length() <= BizConst.SIZE_EMPTY) {
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
			
			TbmSmUserDVO param = new TbmSmUserDVO();
			param.setSingleId(txtSingleId.getText());
			param.setUserPwd(SecurityUtil.encodePassword(txtPassword.getText()));
			
			param = RESTfulAPI.doPost("/fx/login/authentication", param, TbmSmUserDVO.class);
			
			if (param == null) {
				result.append("Login Failed !");
			} else if (ObjectUtil.isEmptyVO(param)) {
				result.append("Login Failed.. Try again !");
			} else {
				result.append("MES ID: " + param.getUserId() + "\r\n");
				result.append("User Name: " + param.getUserNm() + "\r\n");
				result.append("Eng User Name: " + param.getEngUserNm() + "\r\n");
				result.append("Emp NO: " + param.getEmpNo() + "\r\n");
				result.append("Email Addr: " + param.getEmailAddr() + "\r\n");
				result.append("Dept Code: " + param.getDeptCode() + "\r\n");
				result.append("Dept Name: " + param.getDeptNm() + "\r\n");
				result.append("Eng Dept Name: " + param.getEngDeptNm() + "\r\n");
				result.append("Mobile Tel: " + param.getMobileTel());
			}
		} catch (EverywhereException e) {
			result.append("Login Failed.. Try again !!!");
			LOGGER.error("I got catch an error !", new Object[]{e});
		}
		
		MessageBox.showAbout(App.getStage(), "Everywhere", result.toString());
	}
}
