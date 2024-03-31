package com.foxconn.mechanism.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.swing.*;

public class MsgBox {
	
	public static void display(String msg) {
		JOptionPane.showMessageDialog(null,msg);
	}
	public static void display(Exception e) {
		JOptionPane.showMessageDialog(null,printException(e));
	}

	public static void display(JFrame frame,String msg) {
		JOptionPane.showMessageDialog(frame,msg);
	}
	public static void display(JFrame frame, Exception e) {
		JOptionPane.showMessageDialog(frame,printException(e));
	}

	private static String printException(Exception e) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
		e.printStackTrace(printWriter);
		printWriter.flush();
		return byteArrayOutputStream.toString();
	}
}
