package com.hh.tools.renderingHint;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.DBUtil;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;

public class CheckSubSystemPropertyBean extends AbstractPropertyBean<Object> {

    private TCComponent component;
    private TCProperty tcProperty;
    private TCPropertyDescriptor tcPropertyDescriptor;
    TCComponentType com = null;
    boolean isModify = false;
    String typename = "";
    TCProperty prop = null;
    private FormToolkit toolkit;
    private Composite composite;
    private boolean isModified = false;
    //	private static LOVComboBox moduleListComBox = null;
    private static Button checkButton = null;
//	private Button upLoadButton = null;


    private TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    public CheckSubSystemPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public CheckSubSystemPropertyBean(FormToolkit paramFormToolkit, Composite parentComposite, boolean paramBoolean,
                                      Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(parentComposite);
    }


    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());

        GridLayout gridLayout = new GridLayout(4, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        checkButton = new Button(this.composite, SWT.CHECK);
//		checkButton.setText("Check SubSystem");
        checkButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        Label moduleListLabel = new Label(this.composite, SWT.NONE);
        moduleListLabel.setText("Module List");
        moduleListLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

//		moduleListComBox = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);	
//		moduleListComBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
//		moduleListComBox.setSize(168, 30);		
//		loadModuleList();


//		upLoadButton = new Button(this.composite, SWT.NONE);
//		upLoadButton.setText("Upload");
//		upLoadButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

//		upLoadButton.addListener(SWT.Selection, new Listener() {
//			
//			@Override
//			public void handleEvent(Event arg0) {
//				String mouldeListStr = moduleListComBox.getSelectedString();
//				TCComponentItemRevision PCBAItemRev = null;
//				if(component==null){
//					PCBAItemRev = (TCComponentItemRevision)AIFUtility.getTargetComponent();
//				}else{
//					try {
//						PCBAItemRev = (TCComponentItemRevision)component.getRelatedComponent("FX8_ReferencePCBARel");
//					} catch (TCException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				if(checkButton.getSelection()&&!Utils.isNull(mouldeListStr)){
//					try {
//						String[] keys = new String[] { Utils.getTextValue("Type"),Utils.getTextValue("Name")};
//						String[] values = new String[] { ItemTypeName.MODULELIST, mouldeListStr};
//						List<InterfaceAIFComponent> moudleList = Utils.search("General...", keys, values);
//						TCComponentItemRevision itemRev = null;
//						if(moudleList!=null&&moudleList.size()>0){
//							for (InterfaceAIFComponent interfaceAIFComponent : moudleList) {
//								itemRev = ((TCComponentItem)interfaceAIFComponent).getLatestItemRevision();
//								break;
//							}
//						}
//						String itemId = itemRev.getProperty("item_id");
//						TCComponentBOMLine topBOMLine = Utils.createBOMLine(session,PCBAItemRev);
//						Map<TCComponentItemRevision,String> map = recursionBOMLine(topBOMLine);					
//						List<String> subSystemList = new ArrayList<>();
//						DBUtil dbUtil = new DBUtil();
//						
//						GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
//						HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session,
//								TCPreferenceService.TC_preference_site, "HH_ModuleList_Info", "=");
//						String ip = (String) dbInfo.get("IP");
//						String username = (String) dbInfo.get("UserName");
//						String password = (String) dbInfo.get("Password");
//						String sid = (String) dbInfo.get("SID");
//						String port = (String) dbInfo.get("Port");
//
//						Connection conn = dbUtil.getConnection(ip, username, password, sid, port);
//						
//						if (conn == null) {
//							Utils.infoMessage("数据库链接失败！");									
//							return;
//						}					
//						Statement stmt = dbUtil.getStatment(conn);						
//						String sql = "SELECT * FROM MODULISTTABLE WHERE MODULELISTID = '"+itemId+"'";					
//						ResultSet rs = dbUtil.getResultSet(stmt, sql);
//						while(rs.next()) {
//							String subSystem = rs.getString(4);
//							if(!Utils.isNull(subSystem)) subSystemList.add(subSystem);
//						}
//						Iterator<Entry<TCComponentItemRevision, String>> it = map.entrySet().iterator();
//						List<TCComponentItemRevision> list = new ArrayList<>();
//						while (it.hasNext()) {
//							Entry<TCComponentItemRevision, String> entery = it.next();
//							TCComponentItemRevision edaItemRev = entery.getKey();
//							String subSystem = entery.getValue();
//							if(!subSystemList.contains(subSystem)){
//								list.add(edaItemRev);
//							}
//							
//						}
//						if(list.size()>0){
//							StringBuffer buffer = new StringBuffer();
//							for (TCComponentItemRevision tcComponentItemRevision : list) {
//								buffer.append(tcComponentItemRevision.toDisplayString()+",");
//							}
//							checkButton.setSelection(false);
//							Utils.infoMessage(buffer.toString().substring(0, buffer.toString().length()-1)+"在Module List中未找到！");
//						}else{
//							Utils.infoMessage("电子料件subsystem都与Module List版本的subsystem匹配！");
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		});
        setControl(this.composite);

    }


//	private void loadModuleList() {
//		String[] moduleList = new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site, "FX_Get_ModuleList_Values");
//		try {
//			for (String moduleListStr : moduleList) {
//				TCComponentItemType itemType = (TCComponentItemType)session.getTypeComponent(ItemTypeName.MODULELIST);
//				TCComponentItem[] items = itemType.findItems(moduleListStr);
//				TCComponentItemRevision itemRev = items[0].getLatestItemRevision();
//				moduleListComBox.addItem(itemRev.getProperty("object_name"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//	}

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getEditableValue() {
        boolean isCheck = checkButton.getSelection();
        return isCheck;
    }

    public static boolean isSelected() {
        boolean isCheck = checkButton.getSelection();
        return isCheck;

    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
//		com = arg0.getTCComponent().getTypeComponent();
//		property = arg0.getPropertyName();
//		descriptor = arg0.getPropertyDescriptor();
//		prop = arg0;
//		component = arg0.getTCComponent();
//		typename = descriptor.getTypeComponent().getTypeName();
//		String value = arg0.getStringValue();
//		if("NONE".equals(value)){
//			noneButton.setSelection(true);
//		}else if("NC-SMD".equals(value)){
//			smdButton.setSelection(true);
//		}else if("ALL".equals(value)){
//			allButton.setSelection(true);
//		}
        isModify = false;
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

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        if (prop != null) {
            try {
                String value = getEditableValue().toString();
                if (!Utils.isNull(value)) {
                    component.setProperty(property, value);
                } else {
                    component.setProperty(property, null);
                }
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        super.dispose();
    }

    public static Map<TCComponentItemRevision, String> recursionBOMLine(TCComponentBOMLine bomLine) {
        Map<TCComponentItemRevision, String> map = new HashMap<>();
        try {
            AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
            if (aifComponentContexts != null && aifComponentContexts.length > 0) {
                for (int i = 0; i < aifComponentContexts.length; i++) {
                    TCComponentBOMLine childrenLine = (TCComponentBOMLine) aifComponentContexts[i].getComponent();
                    String subSystem = childrenLine.getProperty("FX8_SubSystem");
                    TCComponentItemRevision childrenRev = childrenLine.getItemRevision();
                    if ("EDAComp Revision".equals(childrenRev.getType())) {
                        map.put(childrenRev, subSystem);
                    }
                    if (childrenLine.hasChildren()) {
                        recursionBOMLine(childrenLine);
                    }
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return map;
    }

}
