package com.foxconn.electronics.issuemanagement.controller;

import com.foxconn.electronics.issuemanagement.dialog.IssueUpdatesDialog;
import com.foxconn.electronics.issuemanagement.service.IssueUpdatesService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;

public class IssueUpdatesController {
	private IssueUpdatesService service;

	public IssueUpdatesController(TCSession session,String customer,TCComponentItemRevision itemRevision,IssueUpdatesDialog dialog) {
		super();
		this.service = new IssueUpdatesService(session,customer,itemRevision,dialog);
	}
	
	@GetMapping("/issueUpdates/getBaseData")
	public AjaxResult getBaseData() {
		return service.getBaseData();
	}
	
	
	@PostMapping("/issueUpdates/close")
	public AjaxResult closeDialog() {
		return service.closeDialog();
	}
	
}
