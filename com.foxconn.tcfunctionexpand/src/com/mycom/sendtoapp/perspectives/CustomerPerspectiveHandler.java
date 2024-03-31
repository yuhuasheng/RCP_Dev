package com.mycom.sendtoapp.perspectives;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class CustomerPerspectiveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println(123);
		return null;
	}

}
