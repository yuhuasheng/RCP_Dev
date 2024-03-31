package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

public class MyCCombo extends CCombo{

	public MyCCombo(Composite parent, int style) {
		super(parent, style);
		// 添加鼠标滚轮监听器
		addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// 取消事件传播，阻止CCombo切换选择的项
				event.doit = false;
			}
		});
	}
	
	@Override    
	protected void checkSubclass() {
		// TODO Auto-generated method stub        
	}

}
