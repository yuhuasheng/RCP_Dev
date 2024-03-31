package com.foxconn.tcutils.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ActualUserCombo extends CCombo{

	public ActualUserCombo(Composite parent, int style,TCSession session) {
		super(parent, style);
		try {
			ArrayList<String> userList = com.foxconn.tcutils.util.TCUtil.getLovValues(session, (TCComponentItemRevisionType) session.getTypeComponent("ItemRevision"), "d9_ActualUserID");
			for(String user:userList) {
				add(user);					
			}
			setData(userList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				int keyCode = arg0.keyCode;
				if(keyCode!=13 && keyCode != 16777296) {
					return;
				}
				CCombo source = (CCombo) arg0.getSource();
				String text = source.getText();
				List<String> userList = (List<String>) source.getData();
				source.removeAll();
				for(String user : userList) {
					if(user.toLowerCase().contains(text.toLowerCase())) {
						source.add(user);
					}
				}
				source.setText(text);
				source.setSelection(new Point(999,999));
				if(source.getItemCount()==1) {
					source.setText(source.getItem(0));
				}else {
					source.setListVisible(true);
				}				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override    
	protected void checkSubclass() {
		// 解决 Subclassing not allowed
	}
	
	

}
