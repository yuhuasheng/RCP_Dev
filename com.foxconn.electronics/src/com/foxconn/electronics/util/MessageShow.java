package com.foxconn.electronics.util;

import com.foxconn.mechanism.util.CommonTools;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.util.MessageBox;

public class MessageShow {
	
	/**
	 * 获取程序桌面
	 * 
	 * @return
	 */
	public static AIFDesktop getDesktop() {
		return AIFDesktop.getActiveDesktop();
	}

	/**
	 * 弹出消息框
	 * 
	 * @return
	 */
	public static void infoMsgBox(String info, String title) {
		if (CommonTools.isEmpty(title)) {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "弹出消息框", MessageBox.INFORMATION);
		} else {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.INFORMATION);
		}		
	}

	public static void warningMsgBox(String info, String title) {
		if (CommonTools.isEmpty(title)) {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "警告", MessageBox.WARNING);
		} else {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.WARNING);
		}		
	}

	public static void errorMsgBox(String info, String title) {
		if (CommonTools.isEmpty(title)) {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "错误", MessageBox.ERROR);
		} else {
			MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.ERROR);
		}		
	}

}
