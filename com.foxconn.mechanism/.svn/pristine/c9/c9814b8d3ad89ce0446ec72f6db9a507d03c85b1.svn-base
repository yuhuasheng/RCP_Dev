package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.teamcenter.rac.kernel.TCComponentProject;

public class ProjectCCombo extends MyCCombo{

	public ProjectCCombo(Composite parent, int style) {
		super(parent, style);
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				int keyCode = arg0.keyCode;
				if(keyCode!=13 && keyCode != 16777296) {
					return;
				}
				MyCCombo source = (MyCCombo) arg0.getSource();
				String text = source.getText();
				TCComponentProject[] list =(TCComponentProject[]) source.getData();
				source.removeAll();
				for(TCComponentProject user : list) {
					if(user.getProjectName().toLowerCase().contains(text.toLowerCase())) {
						source.add(user.getProjectName());
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
		// TODO Auto-generated method stub        
	}

}
