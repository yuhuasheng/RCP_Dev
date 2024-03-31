package com.origin.custom.handler.newitem;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCSession;


public class NewItemCustomOperation extends AbstractAIFOperation {

	private TCComponent tccomponent = null;
	private TCSession session = null;

	public NewItemCustomOperation(TCSession session, TCComponent tccomponent) {
		this.tccomponent = tccomponent;
		this.session = session;
	}

	public void executeOperation() throws Exception {
		// TODO Auto-generated method stub
		// 业务逻辑实现部分

		TCComponentItemType newItem = (TCComponentItemType) session
				.getTypeComponent("Item");

		TCComponentType uom = session.getTypeComponent("UnitOfMeasure");

		String itemId = newItem.getNewID();
		String itemRev = newItem.getNewRev(null);
		String itemType = "car";
		String itemName = "qqqqq";
		String itemDescription = "qqqqqqq";
		TCComponent[] uoms = uom.extent();

		TCComponentItem item = newItem.create(itemId, itemRev, itemType,
				itemName, itemDescription, uoms[0]);
		
		if (tccomponent instanceof TCComponentFolder) {
			tccomponent.add("contents", item);
		} else {
			session.getUser().getNewStuffFolder().add("contents", item);
		}
	}


}
