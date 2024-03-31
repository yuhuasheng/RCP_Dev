package com.foxconn.mechanism.integrate.hhpnIntegrate;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public class TableViewContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object var1) {
		  if(var1 instanceof List) {
			  return ((List)var1).toArray();
		  }
		 return new Object[0];
	}
	   
   }
