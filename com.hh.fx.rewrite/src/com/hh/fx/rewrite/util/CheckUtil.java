package com.hh.fx.rewrite.util;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.util.MessageBox;

public class CheckUtil {

	// bom权限判断
	public static boolean checkACL(TCSession session, String preferenceName,
			TCComponentBOMLine bomline) {
		// TODO Auto-generated method stub
		try {
			TCComponentBOMViewRevision bomViewRevision = bomline
					.getBOMViewRevision();
			String owning_user = bomViewRevision.getProperty("owning_user");
			System.out.println("owning_user == " + owning_user);
			String[] strings = owning_user.split(" ");

			for (int i = 0; i < strings.length; i++) {
				System.out.println(" str == " + strings[i]);
			}
			owning_user = owning_user.split(" ")[1].replace(")", "").replace(
					"(", "");
			System.out.println("owning_user == " + owning_user);
			String loginUser = session.getUserName();
			System.out.println("loginUser == " + loginUser);
			if (owning_user.equals(loginUser)) {
				return true;
			}

			GetPreferenceUtil getPreference = new GetPreferenceUtil();
			String[] preference = getPreference.getArrayPreference(session,
					TCPreferenceService.TC_preference_site, preferenceName);
			if (preference == null) {
				// progressBarThread.stopBar();
				MessageBox.post("请配置首选项" + preferenceName, "提示",
						MessageBox.INFORMATION);
				return false;

			} else if (preference.length == 0) {
				// progressBarThread.stopBar();
				MessageBox.post("请配置首选项" + preferenceName + "的值", "提示",
						MessageBox.INFORMATION);
				return false;
			}

			for (int i = 0; i < preference.length; i++) {
				if (preference[i].equals(loginUser)) {
					return true;
				}
			}

			// progressBarThread.stopBar();
			MessageBox.post("你对该BOM无操作权限", "提示", MessageBox.INFORMATION);
			return false;
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	// 判断是否是数字
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	// 判断是否是数字
	public static boolean isDigNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+");
		return pattern.matcher(str).matches();
	}

	// 判断ITEM版本为初始版本
	public static boolean isFirstRev(TCComponentItemRevision itemRev) {
		try {
			TCComponent[] com = itemRev.getItem().getRelatedComponents(
					"revision_list");
			if (com.length == 1) {
				return true;
			} else {
				if (com[0] instanceof TCComponentItemRevision) {
					if ((TCComponentItemRevision) com[0] == itemRev) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// java调C 开关bypass
	public static void setByPass(boolean val, TCSession session)
			throws TCException {
		TCUserService userservice = null;
		if (userservice == null) {
			userservice = session.getUserService();
		}
		Object[] obj = new Object[1];
		obj[0] = "origin";
		if (val) {
			String setByPass = (String) userservice.call("set_bypass",
					obj);
			System.out.println("ORIGIN_set_bypass===" + setByPass);
		} else {
			String setByPass = (String) userservice.call("close_bypass",
					obj);
			System.out.println("ORIGIN_close_bypass===" + setByPass);
		}
	}

	/*
	 * 判断用户对对象具有什么权限
	 */
	public static boolean checkUserPrivilege(TCSession session,
			TCComponent com, String acl) throws TCException {
		TCAccessControlService service = session.getTCAccessControlService();
		if (service.checkUsersPrivilege(session.getUser(), com,
				acl)) {
			return true; //有权限
		} else {
			return false; //无权限
		}
	}
	
	public static boolean isImage(String srcFilePath) {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(srcFilePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if (inputStream == null) {
            return false;
        }
        Image img;
        try {
            img = ImageIO.read(inputStream);
            return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }


}
