package com.foxconn.electronics.pamatrixbom.controller;

import com.foxconn.electronics.pamatrixbom.service.PAProductLineBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;

//@RequestMapping("/productLine")
@RequestMapping("/paproductLine")
public class PAProductLineBOMController {

	private PAProductLineBOMService matrixBOMService;

	public PAProductLineBOMController(AbstractAIFUIApplication app) {
		matrixBOMService = new PAProductLineBOMService(app);
	}

	/**
	 * PA 查询 零件part
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/queryPackingParts")
	public AjaxResult queryPackingParts(@RequestParam("itemids") String itemids,@RequestParam("englishDescription") String englishDescription) {
		System.out.println("==>> queryPackingPartsData: itemids = " + itemids+",englishDescription = "+englishDescription);
		return matrixBOMService.queryPaParts(itemids,englishDescription);
	}
	

	/**
	 * 新建前指派零件part的id和版本
	 * 
	 * @return
	 */
	@GetMapping("/newIDMatrixParts")
	public AjaxResult newIDMatrixParts() {
		return matrixBOMService.newIDMatrixParts();
	}

	/**
	 * PA
	 * 创建零件part
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addPackingParts")
	public AjaxResult addPackingParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> addPackingPartsData: " + data);
		return matrixBOMService.addPaParts(data);
	}

	/**
	 * PA
	 * 添加结构
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addPackingBOM")
	public AjaxResult addPackingBOM(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> addPackingBOMData: " + data);
		return matrixBOMService.addPaBOM(data);
	}

	/**
	 * PA
	 * 移除零件part
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/removeMatrixParts")
	public AjaxResult removeMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> removeMatrixParts: " + data);
		return matrixBOMService.removeMatrixParts(data);
	}
	
	/**
	 * PA
	 * 修改物料属性
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/modifyPackingMatrixParts")
	public AjaxResult modifyPackingMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> modifyPackingData: " + data);
		return matrixBOMService.modifyPAMatrixParts(data);
	}

	/**
	 * PA
	 * 零件part升版、修订
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/upgradedverMatrixParts")
	public AjaxResult upgradedverMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> upgradedverMatrixParts: " + data);
		return matrixBOMService.upgradedverMatrixParts(data);
	}
	
	/**
	 * PA
	 * 零件part 新增替代料
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addSubMatrixParts")
	public AjaxResult addSubMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> addSubMatrixParts: " + data);
		return matrixBOMService.addSubMatrixParts(data);
		
	}

	/**
	 * PA 零件part 替換
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/replacePackingParts")
	public AjaxResult replacePackingParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> replacePackingPartsData: " + data);
		return matrixBOMService.replacePAParts(data);
	}
	
	
	/**
	 * PA
	 * 零件part 移除替代料
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/cutSubMatrixParts")
	public AjaxResult cutSubMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> cutSubMatrixParts: " + data);
		return matrixBOMService.cutSubMatrixParts(data);
	}
	
	/**
	 * 创建DCN
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/createDCN")
	public AjaxResult createDCN(@RequestParam("itemids") String itemids) {
		System.out.println("==>> data: " + itemids);
		return matrixBOMService.createDCN(itemids);
	}
	
	/**
	 * 创建 MNT DCN
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/createDCNForMNT")
	public AjaxResult createDCNForMNT(@RequestParam("itemids") String itemids) {
		System.out.println("==>> data: " + itemids);
		return matrixBOMService.createDCNForMNT(itemids);
	}
	
}
