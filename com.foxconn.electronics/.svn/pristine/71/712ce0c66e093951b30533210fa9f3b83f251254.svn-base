package com.foxconn.electronics.pamatrixbom.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.service.MatrixBOMExportService;
import com.foxconn.electronics.pamatrixbom.domain.PAProductLineBOMBean;
import com.foxconn.electronics.pamatrixbom.service.PAMatrixBOMExportService;
import com.foxconn.electronics.pamatrixbom.service.PAMatrixBOMService;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.commands.namedreferences.ExportFilesOperation;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

//@RequestMapping("/electronics")
@RequestMapping("/matrix")
public class PAMatrixBOMController {

	private PAMatrixBOMService matrixBOMService;
	private TCSession session;
	public PAMatrixBOMController(AbstractAIFUIApplication app) throws TCException {
		this.session = (TCSession) app.getSession();
		matrixBOMService = new PAMatrixBOMService(app);
		PAMatrixBOMExportService.setMatrixBOMService(matrixBOMService);
	}
	
	/**
	 * PA 自动带出对应的匹配值
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/autormaticProp")
	public AjaxResult autormaticProp() {
		String tempPath = System.getProperty("java.io.tmpdir");
		String filePath = null;
		JSONObject data = new JSONObject();
		try {
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
					new String[] { "items_tag.item_id", "item_revision_id" },
					new String[] { "MNT-PA0000001", "01" });
			
			if (executeQuery != null && executeQuery.length > 0) {
				
				TCComponentItemRevision itemRev = (TCComponentItemRevision) executeQuery[0];
				
				TCComponent[] imanSpecifications = itemRev.getRelatedComponents("IMAN_specification");
				if(imanSpecifications!=null && imanSpecifications.length > 0) {
					for (TCComponent imanSpecification:imanSpecifications) {
						String specificationType = imanSpecification.getTypeObject().getName();
						if ("MSExcelX".equals(specificationType)) {
							TCComponentTcFile[] tcfiles = ((TCComponentDataset) imanSpecification).getTcFiles();
							if (tcfiles != null && tcfiles.length > 0) {
								String fileName = tcfiles[0].getProperty("original_file_name");
								System.out.println("fileName = " + fileName);
								
								ExportFilesOperation expTemp = new ExportFilesOperation((TCComponentDataset) imanSpecification, tcfiles, tempPath, null);
								expTemp.executeOperation();
							
								filePath = tempPath + "\\" + fileName;
							}
						}
					}
				}
				if(filePath != null) {
					List<JSONObject> readList = new ArrayList<JSONObject>();
					List<JSONObject> readList1 = new ArrayList<JSONObject>();
					
					ExcelReader reader = ExcelUtil.getReader(filePath, 0);
					List<List<Object>> readLists = reader.read();
					readLists.remove(0);
					for (int i = 0; i < readLists.size(); i++) {
						List<Object> list = readLists.get(i);
						String key = list.get(0).toString();
						String value = list.get(1).toString();
						
						if(key!=null && !"".equals(key) && value!=null && !"".equals(value) ) {
							JSONObject object = new JSONObject();
							object.put("d9_ChineseDescription", key);
							object.put("d9_MaterialGroup", value);
							readList.add(object);
						}
					}
					data.put("MaterialGroup", readList);
					IoUtil.close(reader);

					ExcelReader reader1 = ExcelUtil.getReader(filePath, 1);
					List<List<Object>> readLists1 = reader1.read();
					readLists1.remove(0);
					for (int i = 0; i < readLists1.size(); i++) {
						List<Object> list = readLists1.get(i);
						String key = list.get(0).toString();
						String value = list.get(1).toString();
						
						if(key!=null && !"".equals(key) && value!=null && !"".equals(value) ) {
							JSONObject object = new JSONObject();
							object.put("d9_SupplierZF", key);
							object.put("d9_ManufacturerID", value);
							readList1.add(object);
						}
					}
					data.put("Supplier", readList1);
					IoUtil.close(reader1);
					
				}
			} else {
				return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "未找到MNT-PA0000001/01模板文件！");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e1.getMessage());
		}
		return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", data);
		
	}
	
	/**
	 * PA getActualUserID获取实际用户LOV
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getActualUserID")
	public AjaxResult getActualUserID() {
		
		//获取动态LOV
		ArrayList<String> lovValues = new ArrayList<String>();
		try {
			TCComponentItemRevisionType itemRevType = (TCComponentItemRevisionType) session.getTypeComponent("ItemRevision");
			lovValues = TCUtil.getLovValues(session, itemRevType, "d9_ActualUserID");
//			for (String lovValue :lovValues) {
//				System.out.println("lovValue = "+lovValue);
//			}
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return AjaxResult.success(lovValues);
	}
	
	

	/**
	 * PA 获取MatrixBOM结构
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getMatrixBOMStruct")
	public AjaxResult getMatrixBOMStruct(@RequestParam("uid") String uid) {
		System.out.println("getMatrixBOMStruct = "+uid);
		AjaxResult packingBOMStruct = matrixBOMService.getPackingBOMStruct(uid);
		System.out.println("getMatrixBOMStruct = "+uid);
		return packingBOMStruct;
	}
	
	
	/**
	 * PA 保存MatrixBOM结构
	 * @param request
	 * @return
	 */
	@PostMapping("/savePackingMatrixBOM")
	public AjaxResult savePackingMatrixBOM(HttpRequest request) {
		String data = request.getBody();		
		System.out.println("savePacking==>> data: " + data);
		return matrixBOMService.savePackingMatrixBOM(data);
	}
	
	
	/**
	 * PA 获取PartBOM
	 * 
	 * @param uid
	 * @return
	 */
	@GetMapping("/getPartBOMStruct")
	public AjaxResult getPartBOMStruct(@RequestParam("uids") String uids) {
		try {
			List<PAProductLineBOMBean> bomStruct = new ArrayList<PAProductLineBOMBean>();
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
	
	
	
	@GetMapping("/changeLogInfo")
	public AjaxResult changeLogInfo(@RequestParam("uid") String uid) {
		System.out.println("changeLogInfo = "+uid);
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
