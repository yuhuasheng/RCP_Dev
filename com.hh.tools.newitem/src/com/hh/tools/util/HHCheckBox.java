package com.hh.tools.util;

import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import com.teamcenter.rac.kernel.TCProperty;

public class HHCheckBox extends JCheckBox {

    private TCProperty property = null;
    private ArrayList<TCProperty> propList = new ArrayList<TCProperty>();
    private String propName = "";

    public HHCheckBox() {
        super();
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(Action a) {
        super(a);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(Icon icon) {
        super(icon);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(String text, boolean selected) {
        super(text, selected);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(String text, Icon icon) {
        super(text, icon);
        // TODO Auto-generated constructor stub
    }

    public HHCheckBox(String text) {
        super(text);
        // TODO Auto-generated constructor stub
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
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

}
