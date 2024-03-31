package com.foxconn.mechanism.batchChangePhase.window;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class MsgBox {
	
	public static void display(String msg) {
		JOptionPane.showMessageDialog(null,msg);
	}
	public static void display(Exception e) {
		JOptionPane.showMessageDialog(null,printException(e));
	}
	private static String printException(Exception e) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
		e.printStackTrace(printWriter);
		printWriter.flush();
		return byteArrayOutputStream.toString();
	}
}
