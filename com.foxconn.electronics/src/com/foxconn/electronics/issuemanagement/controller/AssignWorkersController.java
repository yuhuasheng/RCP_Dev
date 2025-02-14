package com.foxconn.electronics.issuemanagement.controller;

import java.util.ResourceBundle;

import com.foxconn.electronics.issuemanagement.service.AssignWorkersService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;

public class AssignWorkersController {

	private AssignWorkersService service;

	public AssignWorkersController(TCSession session,ResourceBundle reg,TCComponentItemRevision itemRevision,String customer) {
		super();
		this.service = new AssignWorkersService(session,reg,itemRevision,customer);
	}
	
	@GetMapping("/assignWorkers/getWorkers")
	public AjaxResult getWorkers() {
		return service.getWorkers();
	}
	
	@GetMapping("/assignWorkers/getWorkerList")
	public AjaxResult getWorkerList() {
		return service.getWorkerList();
	}
	
	@PostMapping("/assignWorkers/setWorkers")
	public AjaxResult setWorkers(HttpRequest req) {
		return service.setWorkers(req);
	}
	
}
