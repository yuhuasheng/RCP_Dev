package com.foxconn.electronics.matrixbom.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.foxconn.electronics.matrixbom.service.ProductLineBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;

@RequestMapping("/productLine")
public class ProductLineBOMController {

	private ProductLineBOMService matrixBOMService;

	public ProductLineBOMController(AbstractAIFUIApplication app) {
		matrixBOMService = new ProductLineBOMService(app);
	}

	/**
	 * PA 查询 零件part
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/queryPackingParts")
	public AjaxResult queryPackingParts(@RequestParam("itemids") String itemids) {
		System.out.println("==>> queryPackingPartsData: " + itemids);
		return matrixBOMService.queryPaParts(itemids);
	}
	
	/**
	 * 查询 零件part
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/queryMatrixParts")
	public AjaxResult queryMatrixParts(@RequestParam("itemids") String itemids) {
		System.out.println("==>> queryMatrixPartsData: " + itemids);
		return matrixBOMService.queryMatrixParts(itemids);
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
	 * 创建零件part
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addMatrixParts")
	public AjaxResult addMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> addMatrixPartsData: " + data);
		return matrixBOMService.addMatrixParts(data);
	}

	/**
	 * 保存结构
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addBOMMatrixParts")
	public AjaxResult addBOMMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> addBOMMatrixPartsData: " + data);
		return matrixBOMService.addBOMMatrixParts(data);
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
	 * 保存结构
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
	 * 移除零件part
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/removeMatrixParts")
	public AjaxResult removeMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return matrixBOMService.removeMatrixParts(data);
	}

	/**
	 * 修改物料属性
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/modifyMatrixParts")
	public AjaxResult modifyMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> modifyData: " + data);
		return matrixBOMService.modifyMatrixParts(data);
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
	 * 零件part升版、修订
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/upgradedverMatrixParts")
	public AjaxResult upgradedverMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return matrixBOMService.upgradedverMatrixParts(data);
	}
	
	/**
	 * 零件part 排序
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/moveUpDownMatrixParts")
	public AjaxResult moveUpDownMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return matrixBOMService.moveUpDownMatrixParts(data);
	}
	
	/**
	 * 零件part 新增替代料
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/addSubMatrixParts")
	public AjaxResult addSubMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
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
	 * 零件part 替換
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/replaceMatrixParts")
	public AjaxResult replaceMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> replaceMatrixPartsData: " + data);
		return matrixBOMService.replacePAParts(data);
	}
	
	/**
	 * 零件part 移除替代料
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/cutSubMatrixParts")
	public AjaxResult cutSubMatrixParts(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return matrixBOMService.cutSubMatrixParts(data);
	}
	
	/**
	 * 修改物料图片
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/modifyMatrixImg")
	public AjaxResult modifyMatrixImg(HttpRequest request) {
		String data = request.getBody();
		System.out.println("==>> data: " + data);
		return matrixBOMService.modifyMatrixImg(data);
	}
	
	/**
	 * 发起流程
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/createRelease")
	public AjaxResult createRelease(@RequestParam("itemids") String itemids,@RequestParam("processName") String processName) {
		System.out.println("==>> itemids: " + itemids+",processName:"+processName);
		
		return matrixBOMService.createRelease(itemids,processName);
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
	
	
	
	
	public static byte[] getBytes(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream is = connection.getInputStream();
		int size = -1;
		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while ((size = is.read(buffer)) != -1) {
			bis.write(buffer, 0, size);
		}
		buffer = bis.toByteArray();
		bis.close();
 
        return buffer;
    }
	

}
