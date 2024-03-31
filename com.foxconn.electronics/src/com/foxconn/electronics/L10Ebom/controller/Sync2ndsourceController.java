package com.foxconn.electronics.L10Ebom.controller;

import com.foxconn.electronics.L10Ebom.service.SyncSecondSourceService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.kernel.TCException;

@RequestMapping("/mntL10Ebom")
public class Sync2ndsourceController {
	
	private SyncSecondSourceService secondSourceService;
	
	
	public Sync2ndsourceController() {
		this.secondSourceService = new SyncSecondSourceService();
	}	
	
	@GetMapping("/getSyncParams")
	public AjaxResult getSync2ndSourceParams(@RequestParam("syncFrom") String syncFrom) {
		return secondSourceService.getSync2ndSourceParams(syncFrom);
	}	
	
	
	@GetMapping("/getSingle2ndSource")
	public AjaxResult getSingle2ndSourceStruct(@RequestParam("uid") String uid) {
		return secondSourceService.getSingle2ndSourceEBOMStruct(uid);
	}
	
	
	@GetMapping("/get2ndSource")
	public AjaxResult get2ndSourceStruct(@RequestParam("uid") String uid) {
		System.out.println("uid = "+uid);
		
		return secondSourceService.get2ndSourceEBOMStruct(uid);
	}
	
	@GetMapping("/getSingleSync2ndSource")
	public AjaxResult getSingleSync2ndSourceStruct(@RequestParam("uid") String uid, @RequestParam("syncFrom") String syncFrom, 
			@RequestParam("projectID") String projectID) {
		return secondSourceService.getSingleSync2ndSourceStruct(uid, syncFrom, projectID);
	}
	@GetMapping("/getSyncInfo")
	public AjaxResult getSync2ndSourceInfo(@RequestParam("uid") String uid, @RequestParam("syncFrom") String syncFrom, 
			@RequestParam("projectID") String projectID) {		
			return secondSourceService.getSync2ndSourceInfo(uid, syncFrom, projectID);		
	}
	
	@PostMapping("/saveSubList")	
	public AjaxResult save2ndSourceSubList(HttpRequest request) throws TCException {
		String data = request.getBody();
		System.out.println("==>> data: " + data);		
		return secondSourceService.save2ndSourceSubList(data);		
	}	

}
