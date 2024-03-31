package com.foxconn.electronics.prtL10ebom.ebomexport.controller;

import java.util.ResourceBundle;

import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpResponse;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCSession;


@RequestMapping("/prtEbom")
public class PrtEbomController {
	private PrtEbomService service;

	public PrtEbomController(TCSession session,ResourceBundle reg,TCComponentFolder folder , String type) {
		super();
		this.service = new PrtEbomService(session, reg,folder,type);
	}

	@GetMapping("/export")
	public void export(@RequestParam("itemIds") String itemIds,HttpResponse response) {
		service.export(itemIds,response);
	}
	
	
	@GetMapping("/getItemRev")
	public AjaxResult getItemRev() {
		return service.getItemRev();
	}
}
