package com.hanhe.CustomView;

import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.teamcenter.rac.util.iTextField;

public class CustomViews extends ViewPart {

	public static final String ID = "com.hanhe.CustomView.CustomView";
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		parent.setLayout(new FillLayout());
		Text text = new Text(parent, SWT.BORDER);
		text.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
		Font font = text.getFont();
		FontData[] fontData = font.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(18);
		}
		
		Font newFont = new Font(parent.getDisplay(), fontData);
		text.setFont(newFont);
		text.setText(" 我的第一个视图！！ ");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
