package com.hh.tools.util;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JTextArea;

import com.hh.tools.newitem.FileStreamUtil;
import com.teamcenter.rac.kernel.TCProperty;

public class HHTextArea extends JTextArea {
    private TCProperty property = null;
    private ArrayList<TCProperty> propList = new ArrayList<TCProperty>();
    private FileStreamUtil fileStreamUtil = null;
    private PrintStream printStream = null;
    private String propName = "";

    public HHTextArea(FileStreamUtil fileStreamUtil, PrintStream printStream) {
        // TODO Auto-generated constructor stub
        this.fileStreamUtil = fileStreamUtil;
        this.printStream = printStream;
    }

    public ArrayList<TCProperty> getPropList() {
        return propList;
    }

    public void setPropList(ArrayList<TCProperty> propList) {
        this.propList = propList;
    }

    public TCProperty getProperty() {
        return property;
    }

    public void setProperty(TCProperty property) {
        this.property = property;
        setTextAreaValue(property);
    }

    private void setTextAreaValue(TCProperty property) {
        if (property == null) {
            return;
        }
        fileStreamUtil.writeData(printStream, "设置"+property.getName());
        try {
            String value = "";
            if (property.getLOV() != null) {
            	fileStreamUtil.writeData(printStream, "是LOV");
                value = property.getStringValue();
                fileStreamUtil.writeData(printStream, "realValue == " + value);
                value = property.getLOV().getListOfValues().getDisplayableValue(value);
            } else {
            	fileStreamUtil.writeData(printStream, "不是LOV");
                value = property.getStringValue();
            }
            fileStreamUtil.writeData(printStream, "value == " + value);
            this.setText(value);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(printStream);

        }
        fileStreamUtil.writeData(printStream, "设置TextField完毕");
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }


}
