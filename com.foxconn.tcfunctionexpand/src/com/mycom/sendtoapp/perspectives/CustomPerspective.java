package com.mycom.sendtoapp.perspectives;

import java.awt.Composite;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.util.OSGIUtil;

public class CustomPerspective implements IPerspectiveFactory {
	
	public static final String ID = "com.mycom.sendtoapp.perspectives.CustomPerspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {		
		layout.setEditorAreaVisible(false);
		String editorArea = layout.getEditorArea();
		IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, -2f, editorArea);
		top.addView("com.hanhe.CustomView.CustomViews");
		System.out.println(123);
	}
	
	
}
