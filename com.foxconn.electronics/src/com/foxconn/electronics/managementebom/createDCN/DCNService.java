package com.foxconn.electronics.managementebom.createDCN;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;

public class DCNService {
	
	public static void updateDCN(TCComponentItemRevision dcn, TCComponentTask dcnTask, List<TCComponentItemRevision> list) throws TCException {
		for(TCComponentItemRevision revision : list) {
			TCComponentItemRevision previousRevision = getPreviousRevision(revision);
			try {
				dcn.add("CMHasProblemItem", previousRevision);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				dcn.add("CMHasSolutionItem", revision);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			TCProperty tcProperty = dcnTask.getTCProperty("root_target_attachments");
//			TCComponent[] referenceValueArray = tcProperty.getReferenceValueArray();

//			tcProperty.setReferenceValue(revision);
//			tcProperty.setReferenceValueArray(new TCComponent[] {revision});
			
		}
		
	}
	
	public static TCComponentItemRevision getFirstRevision(TCComponentItem item) throws TCException {
		TCComponent[] revions = item.getRelatedComponents("revision_list");
		return (TCComponentItemRevision) revions[0];
	}
	
	public static TCComponentItemRevision getPreviousRevision(TCComponentItemRevision itemRevision) throws TCException {
		try {
			String myVer = itemRevision.getProperty("item_revision_id");
			TCComponent[] revions = itemRevision.getItem().getRelatedComponents("revision_list");
			if (revions.length == 1) {
				return null;
			}
			
			for (int i = 0; i < revions.length; i++) {
				itemRevision = (TCComponentItemRevision) revions[i];
				String version = itemRevision.getProperty("item_revision_id");
				if (myVer.equals(version)) {
					return (TCComponentItemRevision) revions[i - 1];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
