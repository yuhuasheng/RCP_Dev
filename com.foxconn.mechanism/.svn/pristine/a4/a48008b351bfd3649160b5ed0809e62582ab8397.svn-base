package com.foxconn.mechanism.integrate.hhpnIntegrate;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableLableProvider implements ITableLabelProvider{

	@Override
	public void addListener(ILabelProviderListener var1) {	
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object var1, String var2) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener var1) {
	}

	@Override
	public Image getColumnImage(Object var1, int var2) {
		return null;
	}

	@Override
	public String getColumnText(Object var1, int var2) {
		HHPNPojo hhpnPojo=(HHPNPojo)var1;
		if(var2==0) {
			return ""+hhpnPojo.getIndex();
		}
		if(var2==1) {
			return hhpnPojo.getItemId();
		}
		if(var2==2) {
			if(hhpnPojo.getDataFrom()==null) {
				return "";
			}
			return hhpnPojo.getDataFrom();
		}
		if(var2==3) {
			if(hhpnPojo.getMfg()==null) {
				return "";
			}
			return hhpnPojo.getMfg();
		}
		if(var2==4) {
			if(hhpnPojo.getMfgPN()==null) {
				return "";
			}
			return hhpnPojo.getMfgPN();
		}
		if(var2==5) {
			if(hhpnPojo.getUnit()==null) {
				return "";
			}
			return hhpnPojo.getUnit();
		}
		if(var2==6) {
			if(hhpnPojo.getMaterialType()==null) {
				return "";
			}
			return hhpnPojo.getMaterialType();
		}
		if(var2==7) {
			if(hhpnPojo.getMaterialGroup()==null) {
				return "";
			}
			return hhpnPojo.getMaterialGroup();
		}
		
		if(var2==8) {
			if(hhpnPojo.getProcurementType()==null) {
				return "";
			}
			return hhpnPojo.getProcurementType();
		}
		
		if(var2==9) {
			if(hhpnPojo.getRev()==null) {
				return "";
			}
			return hhpnPojo.getRev();
		}
		
		if(var2==10) {
			if(hhpnPojo.getDescr()==null) {
				return "";
			}
			
			return hhpnPojo.getDescr();
		}
		return "";
	}
	   
   }