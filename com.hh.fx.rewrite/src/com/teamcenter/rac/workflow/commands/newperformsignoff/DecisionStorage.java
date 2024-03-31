package com.teamcenter.rac.workflow.commands.newperformsignoff;

public class DecisionStorage {
	public static String value = "";
	public static String voltegaFunction = "";
	public static String package_type = "";
	public static String package_size = "";
	public static String descroption = "";
	public static String mfg = "";
	public static String mfg_part_number = "";
	public static String dataSheet = "";
	public static String part_number = "";
	public static String rosh_status = "";
	public static String fox_part_number = "";
	public static String critical = "";
	public static String ccl = "";
	
	public static boolean approveStatus = false;
	public static boolean rejectStatus = false;
	public static boolean noDecisionStatus = false;
	public static boolean isSave = false; //
	
	public static String comments = "";
	
	public static UserDecisionDialog userDecisionDialog = null;

	public static String getValue() {
		return value;
	}

	public static void setValue(String value) {
		DecisionStorage.value = value;
	}

	public static String getVoltegaFunction() {
		return voltegaFunction;
	}

	public static void setVoltegaFunction(String voltegaFunction) {
		DecisionStorage.voltegaFunction = voltegaFunction;
	}

	public static String getPackage_type() {
		return package_type;
	}

	public static void setPackage_type(String package_type) {
		DecisionStorage.package_type = package_type;
	}

	public static String getPackage_size() {
		return package_size;
	}

	public static void setPackage_size(String package_size) {
		DecisionStorage.package_size = package_size;
	}

	public static String getDescroption() {
		return descroption;
	}

	public static void setDescroption(String descroption) {
		DecisionStorage.descroption = descroption;
	}

	public static String getMfg() {
		return mfg;
	}

	public static void setMfg(String mfg) {
		DecisionStorage.mfg = mfg;
	}

	public static String getMfg_part_number() {
		return mfg_part_number;
	}

	public static void setMfg_part_number(String mfg_part_number) {
		DecisionStorage.mfg_part_number = mfg_part_number;
	}

	public static String getDataSheet() {
		return dataSheet;
	}

	public static void setDataSheet(String dataSheet) {
		DecisionStorage.dataSheet = dataSheet;
	}

	public static String getPart_number() {
		return part_number;
	}

	public static void setPart_number(String part_number) {
		DecisionStorage.part_number = part_number;
	}

	public static String getRosh_status() {
		return rosh_status;
	}

	public static void setRosh_status(String rosh_status) {
		DecisionStorage.rosh_status = rosh_status;
	}

	public static String getFox_part_number() {
		return fox_part_number;
	}

	public static void setFox_part_number(String fox_part_number) {
		DecisionStorage.fox_part_number = fox_part_number;
	}

	public static String getCritical() {
		return critical;
	}

	public static void setCritical(String critical) {
		DecisionStorage.critical = critical;
	}

	public static String getCcl() {
		return ccl;
	}

	public static void setCcl(String ccl) {
		DecisionStorage.ccl = ccl;
	}

	public static boolean isApproveStatus() {
		return approveStatus;
	}

	public static void setApproveStatus(boolean approveStatus) {
		DecisionStorage.approveStatus = approveStatus;
	}

	public static boolean isRejectStatus() {
		return rejectStatus;
	}

	public static void setRejectStatus(boolean rejectStatus) {
		DecisionStorage.rejectStatus = rejectStatus;
	}

	public static boolean isNoDecisionStatus() {
		return noDecisionStatus;
	}

	public static void setNoDecisionStatus(boolean noDecisionStatus) {
		DecisionStorage.noDecisionStatus = noDecisionStatus;
	}

	public static boolean isSave() {
		return isSave;
	}

	public static void setSave(boolean isSave) {
		DecisionStorage.isSave = isSave;
	}

	public static String getComments() {
		return comments;
	}

	public static void setComments(String comments) {
		DecisionStorage.comments = comments;
	}

	public static UserDecisionDialog getUserDecisionDialog() {
		return userDecisionDialog;
	}

	public static void setUserDecisionDialog(UserDecisionDialog userDecisionDialog) {
		DecisionStorage.userDecisionDialog = userDecisionDialog;
	}
	
	

	

}
