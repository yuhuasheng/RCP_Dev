package com.foxconn.electronics.matrixbom.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.matrixbom.domain.ProductLineBOMBean;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.matrixbom.service.MatrixBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCException;

@RequestMapping("/electronics")
public class MatrixBOMController {

	private MatrixBOMService matrixBOMService;
	
	public MatrixBOMController(AbstractAIFUIApplication app) throws TCException {
		matrixBOMService = new MatrixBOMService(app);
		MatrixBOMExportService.setMatrixBOMService(matrixBOMService);
	}
	
	/**
	 * 查询 零件part
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/queryParts")
	public AjaxResult queryMatrixParts(@RequestParam("itemids") String itemids) {
		System.out.println("==>> data: " + itemids);
		return matrixBOMService.queryMatrixParts(itemids);
	}

	/**
	 * 加载获取MatrixBOM中所有的图片
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getImg")
	public AjaxResult getImg(@RequestParam("uid") String uid) {
		System.out.println("getImg uid = "+uid);
		return matrixBOMService.getImg(uid);
	}

	/**
	 * 获取MatrixBOM结构
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getMatrixBOMStruct")
	public AjaxResult getMatrixBOMStruct(@RequestParam("uid") String uid) {
		System.out.println("getMatrixBOMStruct = "+uid);
		AjaxResult matrixBOMStruct = matrixBOMService.getMatrixBOMStruct(uid);
		System.out.println("getMatrixBOMStruct = "+uid);
		return matrixBOMStruct;
	}
	
	/**
	 * 获取BOM结构
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getBOMStruct")
	public AjaxResult getBOMStruct(@RequestParam("uids") String uids) {
		try {
			String[] uidArray = uids.split(",");
			List<String> uidList = Arrays.asList(uidArray);
			List<EBOMLineBean> bomStruct = matrixBOMService.getBOMStruct(uidList);
			return AjaxResult.success(bomStruct);
		}catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * PA PartBOM 获取BOM结构
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getPartBOMStruct")
	public AjaxResult getPartBOMStruct(@RequestParam("uids") String uids) {
		try {
			List<ProductLineBOMBean> bomStruct = new ArrayList<ProductLineBOMBean>();
			if(!"".equals(uids)) {
				String[] uidArray = uids.split(",");
				List<String> uidList = Arrays.asList(uidArray);
				bomStruct = matrixBOMService.getPABOMStruct(uidList);
			}
			return AjaxResult.success(bomStruct);
		}catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * PA PartBOM 保存BOM结构
	 * 
	 */
	@PostMapping("/savePartBOM")
	public AjaxResult savePartBOMStruct(HttpRequest request) {
		String data = request.getBody();		
		System.out.println("savePartBOM==>> data: " + data);
		return matrixBOMService.savePartBOM(data);
	}
	

	@PostMapping("/savePackingMatrixBOM")
	public AjaxResult savePackingMatrixBOM(HttpRequest request) {
		String data = request.getBody();		
		System.out.println("savePacking==>> data: " + data);
		return matrixBOMService.savePackingMatrixBOM(data);
	}
	
	@PostMapping("/saveMatrixBOM")
	public AjaxResult saveMatrixBOM(HttpRequest request) {
		String data = request.getBody();		
		System.out.println("save==>> data: " + data);
		return matrixBOMService.saveMatrixBOM(data);
	}
	
	
	@GetMapping("/changeLogInfo")
	public AjaxResult changeLogInfo(@RequestParam("uid") String uid) {
		System.out.println("uid = "+uid);
		return matrixBOMService.changeLogInfo(uid);
	}
	
	@PostMapping("/saveChangeLog")
	public AjaxResult saveChangeLog(HttpRequest request) {
		String data = request.getBody();		
		System.out.println("saveChangeLog==>>" + data);
		return matrixBOMService.saveChangeLog(data);
	}
	

	@GetMapping("/exportMatrixBOM")
	public AjaxResult exportMatrixBOM(@RequestParam("uid") String uid){
		try {
			String excelPath = MatrixBOMExportService.exportBOMExcel(uid);
			System.out.println("==>> excelPath: " + excelPath);
			return AjaxResult.success("导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	public ExecutorService getExecutorService() {
		return matrixBOMService.getEs();
	}


}
