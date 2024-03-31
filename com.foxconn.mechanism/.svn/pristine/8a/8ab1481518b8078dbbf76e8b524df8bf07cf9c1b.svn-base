package com.foxconn.mechanism.batchDownloadDataset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class BomLineTreeNode {
	private static Image IMAGE = SWTResourceManager.getImage(BomLineTreeNode.class, "/icons/itemrevision_16.png");
	
	private String objStr;
	private String itemRevName;
	private String itemRevId;
	private String owner;
	private String assignUser = "";
	private String mailSubject = "";

	private TCComponentBOMLine bomLine;
	
	public List<BomLineTreeNode> list = new ArrayList<BomLineTreeNode>();
	
	public BomLineTreeNode (TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}
	
	public Image getImage() {
		return IMAGE;
	}
	
	
	public boolean hasChildren() {
		
		return bomLine.hasChildren();
	}
	
	public List<BomLineTreeNode> getChildren() {
		try {
			AIFComponentContext[] aifs = bomLine.getChildren();
			for (AIFComponentContext aif : aifs) {
				TCComponentBOMLine com = (TCComponentBOMLine) aif.getComponent();
				list.add(new BomLineTreeNode(com));
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
			value = "12300";
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
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public List<BomLineTreeNode> getList() {
		List<BomLineTreeNode> tempList = new ArrayList<BomLineTreeNode>();
		tempList.add(new BomLineTreeNode(bomLine));
		return tempList;
	}

	public void setList(List<BomLineTreeNode> list) {
		this.list = list;
	}

	
	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}
	
}
