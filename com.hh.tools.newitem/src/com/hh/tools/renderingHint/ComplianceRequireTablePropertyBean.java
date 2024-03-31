package com.hh.tools.renderingHint;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class ComplianceRequireTablePropertyBean extends AbstractPropertyBean<Object> {

    public static Table complianceTable = null;

    protected TCProperty tcProperty;

    public ComplianceRequireTablePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite,
                                              boolean paramBoolean, Map<?, ?> paramMap) {
        System.out.println(
                " SetGroupAddMaterialPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,\r\n"
                        + "			Map<?, ?> paramMap)");
        initComposite(paramComposite);
    }

    private void initComposite(Composite parentComposite) {
        Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);

        complianceTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        complianceTable.setHeaderVisible(true);// 设置显示表头
		complianceTable.setLinesVisible(true);// 设置显示表格线/*
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 5);
        gridData.heightHint = 120;
        gridData.widthHint = 880;
        complianceTable.setLayoutData(gridData);

        TableColumn tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("Customer");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("MCD ROHS Status");
        tableColumn.setWidth(170);

        tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("MDD ROHS Status");
        tableColumn.setWidth(170);

        tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("HF Status");
        tableColumn.setWidth(100);

        tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("FMD REACH Status");
        tableColumn.setWidth(170);

        tableColumn = new TableColumn(complianceTable, SWT.NONE);
        tableColumn.setText("Form Name");
        tableColumn.setWidth(170);

        complianceTable.setBackground(parentComposite.getBackground());

        Label spaceLabel = new Label(composite, SWT.NONE);
        spaceLabel.setVisible(false);
        spaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        setControl(composite);
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        System.out.println("======Add isPropertyModified(TCProperty arg0)======");
        return false;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add getPropertyToSave(TCProperty arg0)======");
        return null;
    }

    @Override
    public void load(TCProperty tcProperty) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("======Add load(TCProperty arg0)======");
        this.tcProperty = tcProperty;
        TCComponent[] eplevels = tcProperty.getReferenceValueArray();
        if (eplevels != null && eplevels.length > 0) {
            for (TCComponent tcComponet : eplevels) {
                String[] data = new String[5];
                data[0] = tcComponet.getProperty("fx8_Customer");
                data[1] = tcComponet.getProperty("fx8_MCDRoHSStatus");
                data[2] = tcComponet.getProperty("fx8_MDDRoHSStatus");
                data[3] = tcComponet.getProperty("fx8_IsHFStatus");
                data[4] = tcComponet.getProperty("fx8_FMDREACHStatus");
                String formName = tcComponet.getProperty("fx8_RequireFormName");
                TableItem tableItem = new TableItem(complianceTable, SWT.NONE);
                tableItem.setText(data);
                if (formName != null && !"".equals(formName)) {
                    TableEditor editor = new TableEditor(complianceTable);
                    final Button button = new Button(complianceTable, SWT.NONE);
                    button.setBackground(
                            Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
                    button.setText(formName);
                    button.pack();
                    editor.minimumWidth = button.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(button, tableItem, 5);
                    button.addListener(SWT.Selection, new Listener() {
                        @Override
                        public void handleEvent(Event event) {
                            String[] searchKeys = new String[]{Utils.getTextValue("Type"),
                                    Utils.getTextValue("Name")};
                            String[] searchValues = new String[]{ItemTypeName.COMPREQUIREFORM, button.getText()};
                            List<InterfaceAIFComponent> propertyFormList = Utils.search("General...", searchKeys,
                                    searchValues);

                            if (propertyFormList.size() > 0) {
                                OpenCommand command = new OpenCommand(AIFDesktop.getActiveDesktop(),
                                        new InterfaceAIFComponent[]{(TCComponent) propertyFormList.get(0)});
                                try {
                                    command.executeModal();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

                complianceTable.update();
            }
        }
    }

    @Override
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }
}
