package com.foxconn.mechanism.batchChangePhase.window;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.teamcenter.rac.util.Registry;

import java.util.ArrayList;
import java.util.List;

public class ComplianceAdapter extends AbstractTableModel {
	
	

    Registry reg;
    
    public ComplianceAdapter(Registry reg) {
        this.reg = reg;
    }

    public ComplianceAdapter(JCheckBox chk_all,Registry reg) {
        this.chk_all = chk_all;
        this.reg = reg;
    }

    JCheckBox chk_all;

    List<PhaseBean> list = new ArrayList<>();

    public void add(PhaseBean o) {
        list.add(o);
        fireTableDataChanged();
    }

    public void addAll(List<PhaseBean> o) {
        list.addAll(o);
        fireTableDataChanged();
    }

    public void selectAll(boolean ft) {
        for (PhaseBean bean : list) {
            bean.setSelected(ft);
        }
        fireTableDataChanged();
    }

    public List<PhaseBean> getList() {
        return list;
    }

    public List<PhaseBean> getSelectedList() {
        ArrayList<PhaseBean> objects = new ArrayList<>();
        for (PhaseBean bean : list) {
            if (bean.getSelected()) {
                objects.add(bean);
            }
        }
        return objects;
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
        if (chk_all != null) {
            switch (column) {
                case 0:
                    return reg.getString("phase.window.complianceAdapter.column0");
                case 1:
                    return "ID";
                case 2:
                    return reg.getString("phase.window.complianceAdapter.column1");
                case 3:
                    return reg.getString("phase.window.complianceAdapter.column2");
                case 4:
                    return reg.getString("phase.window.complianceAdapter.column3");
                case 5:
                    return reg.getString("phase.window.complianceAdapter.column4");            
                default:
                    return String.valueOf(column - 1);
            }

        } else {
            switch (column) {
                case 0:
                    return "ID";
                case 1:
                    return reg.getString("phase.window.complianceAdapter.column1");
                case 2:
                    return reg.getString("phase.window.complianceAdapter.column2");
                case 3:
                    return reg.getString("phase.window.complianceAdapter.column3");
                case 4:
                    return reg.getString("phase.window.complianceAdapter.column4");
                case 5:
                    return reg.getString("phase.window.complianceAdapter.column5");           
                default:
                    return String.valueOf(column - 1);
            }
        }

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (chk_all != null) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (chk_all == null) {
            return false;
        }
        return columnIndex == 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PhaseBean o = list.get(rowIndex);
        if (chk_all != null) {
            switch (columnIndex) {
                case 0:
                    return o.getSelected();
                case 1:
                    return o.getId();
                case 2:
                    return o.getVersion();
                case 3:
                    return o.getName();
                case 4:
                    return o.getCount();
                case 5:
                    return o.getOwner();
                default:
                    return "";
            }
        } else {
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
                    return o.getOwner();
                case 5:
                    return o.getResult();
                default:
                    return "";
            }
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column != 0) {
            return;
        }
        boolean ft = (boolean) aValue;
        list.get(row).setSelected(ft);
        if (ft) {
            boolean all = true;
            int rowCount = getRowCount();
            for (int i = 0; i < rowCount; i++) {
                if (!((boolean) getValueAt(i, 0))) {
                    all = false;
                    break;
                }
            }
            if (all) {
                chk_all.setSelected(all);
            }
        } else {
            chk_all.setSelected(false);
        }
    }
}
