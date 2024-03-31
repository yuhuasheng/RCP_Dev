package com.foxconn.electronics.L10Ebom.action;

import java.awt.Frame;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishMatRowBean;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class L10EBOMApplyFormAction extends AbstractAIFAction {

	private AbstractAIFUIApplication app = null;
	private String source = null;
	
	public L10EBOMApplyFormAction(AbstractAIFUIApplication arg0, Frame arg1, String str) {
		super(arg0, arg1, str);
		this.app = arg0;
		this.source = str;
	}

	@Override
	public void run() {
		List<EBOMApplyRowBean> list = new ArrayList<EBOMApplyRowBean>();
		TCComponentItemRevision itemRev = (TCComponentItemRevision) app.getTargetComponent();
		String propName = source.substring(0, source.indexOf("."));
		try {
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(propName);
			for (TCComponent tcComponent : relatedComponents) {
				TCComponentFnd0TableRow row = (TCComponentFnd0TableRow)tcComponent;				
				list.add(tcPropMapping(new FinishMatRowBean(), row));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public <T> T tcPropMapping(T bean, TCComponent tcObject)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcObject != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String propertyName = tcPropName.tcProperty();
					Object val = null;
					tcObject.refresh();
					if (CommonTools.isNotEmpty(propertyName)) {
						if (tcObject.isValidPropertyName(propertyName)) { // 判断属性是否存在
							if ("cm0AuthoringChangeRevision".equals(propertyName)) {
								TCComponent relatedComponent = tcObject.getRelatedComponent(propertyName);
								if (CommonTools.isNotEmpty(relatedComponent)) {
									val = relatedComponent.getProperty("item_id");
								}								
							} else {
								val = tcObject.getProperty(propertyName);
							}
							
							if (fields[i].getType() == Integer.class) {
								if (val.equals("") || val == null) {
									val = "";
								} else {
									val = Integer.parseInt((String) val);
								}
							}
							fields[i].set(bean, val);
						} else {
							System.out.println(propertyName + " propertyName is not exist ");
							bean = null;
//							break;
							continue;
						}
					}					
				}
			}
		}
		return bean;
	}
}
