package com.foxconn.mechanism.batchChangePhase.window;

import javax.swing.table.AbstractTableModel;

import com.teamcenter.rac.util.Registry;

import java.util.ArrayList;
import java.util.List;

public class BlueprintAdapter extends AbstractTableModel {


	Registry req;
    public BlueprintAdapter(Registry req) {
        this.req = req;
    }

    List<BlueprintBean> list = new ArrayList<>();

    public void add(BlueprintBean o) {
        list.add(o);
        fireTableDataChanged();
    }

    public void addAll(List<BlueprintBean> o) {
        list.addAll(o);
        fireTableDataChanged();
    }


    public List<BlueprintBean> getList() {
        return list;
    }


    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
    	switch (column) {
        case 0:
            return "图纸名称";
        case 1:
            return "所有者";
        case 2:
            return "所属父项ID";
        case 3:
            return "所属父项版本";
    }
        return String.valueOf(column - 1);

    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BlueprintBean o = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return o.getBlueprintName();
            case 1:
                return o.getOwner();
            case 2:
                return o.getParentId();
            case 3:
                return o.getParentVer();
            default:
                return "";
        }
    }


}
