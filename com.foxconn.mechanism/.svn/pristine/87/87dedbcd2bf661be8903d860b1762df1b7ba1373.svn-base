package com.foxconn.mechanism.jurisdiction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class CustTreeNode {
	private static Image IMAGE = SWTResourceManager.getImage(CustTreeNode.class, "/icons/itemrevision_16.png");
	
	private String objStr;
	private String itemRevName;
	private String itemRevId;
	private String owner;
	private String assignUser = "";
	private String mailSubject = "";
	private String personLiable = "";

	private TCComponentBOMLine bomLine;
	
	public List<CustTreeNode> list = new ArrayList<CustTreeNode>();
	
	public CustTreeNode (TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}
	
	public Image getImage() {
		return IMAGE;
	}
	
	
	public boolean hasChildren() {
		
		return bomLine.hasChildren();
	}
	
	public List<CustTreeNode> getChildren() {
		try {
			AIFComponentContext[] aifs = bomLine.getChildren();
			for (AIFComponentContext aif : aifs) {
				TCComponentBOMLine com = (TCComponentBOMLine) aif.getComponent();
				list.add(new CustTreeNode(com));
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public String getObjStr() {
		String value = "";
		try {
			value = bomLine.getTCProperty("bl_indented_title").getStringValue();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setObjStr(String objStr) {
		this.objStr = objStr;
	}
	
	public String getItemRevId() {
		String value = "";
		try {
			value = bomLine.getItemRevision().getProperty("item_id");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setItemRevId(String itemRevId) {
		this.itemRevId = itemRevId;
	}
	
	public String getItemRevName() {
		String value = "";
		try {
			value = bomLine.getItemRevision().getProperty("object_name");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setItemRevName(String itemRevName) {
		this.itemRevName = itemRevName;
	}
	
	public String getOwner() {
		String value = "";
		try {
			value = bomLine.getItemRevision().getProperty("owning_user");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAssignUser() {
		return assignUser;
	}

	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}
	
	public String getMailSubject() {
		String value = "";
		try {
			value = bomLine.getItemRevision().getProperty("d9_RealMail");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	
	public String getPersonLiable() {
		String value = "";
		try {
			value = bomLine.getItemRevision().getProperty("d9_ActualUserID");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}

	public List<CustTreeNode> getList() {
		List<CustTreeNode> tempList = new ArrayList<CustTreeNode>();
		tempList.add(new CustTreeNode(bomLine));
		return tempList;
	}

	public void setList(List<CustTreeNode> list) {
		this.list = list;
	}

	
	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}
	
}
