package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.HashMap;

import javax.swing.JPanel;

public class ClassPanel extends JPanel {

	boolean isReference = false;  //�Ƿ�����
	HashMap<String,String> lovMap = null; // LOV��Ϣ
	Component component = null; //�ؼ�
	int ICSType = -1; //ICS������
	int valueId = -1; // ��������id

	public ClassPanel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClassPanel(boolean paramBoolean) {
		super(paramBoolean);
		// TODO Auto-generated constructor stub
	}
	public ClassPanel(LayoutManager paramLayoutManager, boolean paramBoolean) {
		super(paramLayoutManager, paramBoolean);
		// TODO Auto-generated constructor stub
	}
	public ClassPanel(LayoutManager paramLayoutManager) {
		super(paramLayoutManager);
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean isReference() {
		return isReference;
	}
	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}
	public HashMap<String, String> getLovMap() {
		return lovMap;
	}
	public void setLovMap(HashMap<String, String> lovMap) {
		this.lovMap = lovMap;
	}
	public Component getComponent() {
		return component;
	}
	public void setComponent(Component component) {
		this.component = component;
	}
	public int getICSType() {
		return ICSType;
	}
	public void setICSType(int iCSType) {
		ICSType = iCSType;
	}
	public int getValueId() {
		return valueId;
	}
	public void setValueId(int valueId) {
		this.valueId = valueId;
	}
	
	
	
}
