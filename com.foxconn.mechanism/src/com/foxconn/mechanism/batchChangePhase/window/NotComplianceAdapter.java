package com.foxconn.mechanism.batchChangePhase.window;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.teamcenter.rac.util.Registry;

import java.util.ArrayList;
import java.util.List;

public class NotComplianceAdapter extends AbstractTableModel {

    List<PhaseBean> list = new ArrayList<>();
    Registry reg;
    
    public NotComplianceAdapter(Registry reg) {
    	this.reg = reg;
    }
    

    public void add(PhaseBean o) {
        list.add(o);
        fireTableDataChanged();
    }

    public void addAll(List<PhaseBean> o) {
        list.addAll(o);
        fireTableDataChanged();
    }


    public List<PhaseBean> getList() {
        return list;
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "ID";
            case 1:
                return reg.getString("phase.window.notComplianceAdapter.column0");
            case 2:
                return reg.getString("phase.window.notComplianceAdapter.column1");
            case 3:
                return reg.getString("phase.window.notComplianceAdapter.column2");
            case 4:
                return reg.getString("phase.window.notComplianceAdapter.column3");
            case 5:
                return reg.getString("phase.window.notComplianceAdapter.column4");
            default:
                return String.valueOf(column - 1);
        }

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PhaseBean o = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return o.getId();
            case 1:
                return o.getVersion();
            case 2:
                return o.getName();
            case 3:
                return o.getCount();
            case 4:
                return o.getStatus();
            case 5:
                return o.getOwner();
            default:
                return "";
        }
    }
}
