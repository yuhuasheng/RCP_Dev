package com.foxconn.electronics.prtL10ebom.ebomexport.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.electronics.prtL10ebom.ebomexport.bean.PrtL5BomLineBean;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.jhttp.HttpResponse;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class PrtEbomService {
	private TCSession session;
	private ResourceBundle reg;
	private TCComponentFolder folder;
	private String type;
	
	public TCSession getSession() {
		return session;
	}

	public void setSession(TCSession session) {
		this.session = session;
	}

	public ResourceBundle getReg() {
		return reg;
	}

	public void setReg(ResourceBundle reg) {
		this.reg = reg;
	}

	public TCComponentFolder getFolder() {
		return folder;
	}

	public void setFolder(TCComponentFolder folder) {
		this.folder = folder;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PrtEbomService(TCSession session, ResourceBundle reg , TCComponentFolder folder , String type) {
		super();
		this.session = session;
		this.reg = reg;
		this.folder = folder;
		this.type = type;
	}

	public void export(String itemIds,HttpResponse response) {
		String fileName = (type.equals("L5") ? "PRT L5 EBOM" : "PRT L10 EBOM") + "導出_" + DateUtil.format(DateUtil.date(), "yyyyMMddhhmmssSSS") + ".xlsx";
		try {
			// 業務邏輯
			List<String> itemIdList = StrSplitter.split(itemIds, ",",0,true,true);
			if(CollUtil.isEmpty(itemIdList)) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (String itemId : itemIdList) {
				sb.append(itemId).append(";");
			}
			Map<String, PrtL5BomLineBean> map = new HashMap<String, PrtL5BomLineBean>();
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件 ID", new String[] { "item_id" },
    				new String[] { sb.substring(0, sb.length() - 1) });
			List<PrtL5BomLineBean> list = null;
    		if (executeQuery != null && executeQuery.length > 0) {
    			if(itemIdList.size() > 1) {
	    			for (int i = 0; i < executeQuery.length; i++) {
	    				TCComponentItem item = (TCComponentItem)executeQuery[i];
	    				TCComponentItemRevision itemRevision = item.getLatestItemRevision();
	    				TCComponent[] relateds = itemRevision.getRelatedComponents("view");
	    				if(ObjectUtil.isNotNull(relateds) && relateds.length > 0) {
	    					List<PrtL5BomLineBean> beans = getAllBomObj(itemRevision,type.equals("L5"));
	    					for (PrtL5BomLineBean bean : beans) {
	    						addBean(map,bean);
	    					}
	    				}
	    			}
	    			// 整理數據
		        	list = map.values().parallelStream().sorted(Comparator.comparing(PrtL5BomLineBean::getLevel)
		        			.thenComparing(Comparator.comparing(PrtL5BomLineBean::getSku))).collect(Collectors.toList());
    			}else {
    				TCComponentItem item = (TCComponentItem)executeQuery[0];
    				TCComponentItemRevision itemRevision = item.getLatestItemRevision();
    				TCComponent[] relateds = itemRevision.getRelatedComponents("view");
    				if(ObjectUtil.isNotNull(relateds) && relateds.length > 0) {
    					list = getAllBomObj(itemRevision,type.equals("L5"));
    				}
    			}
    		}
        	// 導出數據到excel
			InputStream inputStream = PrtEbomService.class.getResourceAsStream("/com/foxconn/electronics/prtL10ebom/ebomexport/template.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFCellStyle cellStyle = workbook.getCellStyleAt(0);
	        cellStyle.setAlignment(HorizontalAlignment.CENTER);
	        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        cellStyle.setWrapText(true);
	        cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(list.get(0).getSku())){
            	XSSFRow row = sheet.getRow(2);
            	row.getCell(10).setCellValue(list.get(0).getSku());
            }else {
            	XSSFRow row = sheet.getRow(2);
            	row.getCell(10).setCellValue("");
            }
            if(CollUtil.isNotEmpty(list)) {
	            for (int i = 0; i < list.size(); i++) {
	            	PrtL5BomLineBean bean = list.get(i);
	            	XSSFRow row = sheet.createRow(i+4);
	            	row.createCell(0).setCellValue(i+1);
	            	row.createCell(1).setCellValue(bean.getLevel());
	            	row.createCell(2).setCellValue(bean.getSku());
	            	row.createCell(3).setCellValue(bean.getFamilyPartNum());
	            	row.createCell(4).setCellValue(bean.getParentDrawingNum());
	            	row.createCell(5).setCellValue(bean.getDrawingNum());
	            	row.createCell(6).setCellValue(bean.getParentPartNum());
	            	row.createCell(7).setCellValue(bean.getPartNum());
	            	row.createCell(8).setCellValue(bean.getEnDesc());
	            	row.createCell(9).setCellValue(bean.getChDesc());
	            	row.createCell(10).setCellValue(bean.getQty());
	            	row.createCell(11).setCellValue(bean.getUm());
	            	row.createCell(12).setCellValue(bean.getUnit());
	            	row.createCell(13).setCellValue(bean.getSubsystem());
	            	row.createCell(14).setCellValue(bean.getCommodityType());
	            	row.createCell(15).setCellValue(bean.getMaterial());
	            	row.createCell(16).setCellValue(bean.getDesignName());
	            	row.createCell(17).setCellValue("");
	            	row.createCell(18).setCellValue(bean.getVersion2D());
	            	row.createCell(19).setCellValue(bean.getVersion3D());
	            	row.createCell(20).setCellValue(ObjectUtil.isNull(bean.getHas2DModel()) ? "" : bean.getHas2DModel() ? "Y" : "N/A");
	            	row.createCell(21).setCellValue(ObjectUtil.isNull(bean.getHas3DModel()) ? "" : bean.getHas3DModel() ? "Y" : "N/A");
	            	row.createCell(22).setCellValue("");
	            	row.createCell(23).setCellValue(bean.getOutPart());
	            	row.createCell(24).setCellValue(bean.getCommonParts());
	            	row.createCell(25).setCellValue(bean.getRemark());
	            	row.createCell(26).setCellValue("");
	            	row.createCell(27).setCellValue("");
	            	row.createCell(28).setCellValue("");
	            	row.createCell(29).setCellValue("");
	            	row.createCell(30).setCellValue(bean.getObjectType());
	            	row.createCell(31).setCellValue("");
	            	row.createCell(32).setCellValue(bean.getCustomerDrawingNum());
				}
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			out.flush();
			response.addHeader("Content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
			response.out(out.toByteArray());
			out.flush();
			IoUtil.close(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public AjaxResult getItemRev() {
		if(!type.equals("L5")  && !type.equals("L10")) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "Param Type should be L5 or L10");
		}
		Set<String> set = new HashSet<String>();
		List<JSONObject> partNumberList = new ArrayList<JSONObject>();
		try {
			TCComponent[] tcComponents = folder.getRelatedComponents("contents");
			for (int j = 0; j < tcComponents.length; j++) {
				TCComponent component = tcComponents[j];
				if(!(component instanceof TCComponentItem)) {
					continue;
				}
				TCComponentItem item = (TCComponentItem) component;
				String objType = item.getTypeObject().getName();
				String itemId = item.getProperty("item_id");
				if(type.equals("L5")) {
					if("D9_MEDesign".equals(objType) && set.add(itemId)) {
						JSONObject jsonObject = JSONUtil.createObj();
						jsonObject.set("id", itemId);
						jsonObject.set("name", item.getProperty("object_name"));
						partNumberList.add(jsonObject);
					}
				}else {
					if("D9_FinishedPart".equals(objType)  && set.add(itemId)) {
						JSONObject jsonObject = JSONUtil.createObj();
						jsonObject.set("id", itemId);
						jsonObject.set("name", item.getProperty("object_name"));
						partNumberList.add(jsonObject);
					}
				}
			}
			return AjaxResult.success(partNumberList);
		} catch (Exception e) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "query error");
		}
		
	}
	
	
	/**
	 * 
	 * @param revision
	 * @param flag L5為true
	 * @return
	 * @throws TCException
	 */
	private List<PrtL5BomLineBean> getAllBomObj(TCComponentItemRevision revision,boolean flag) throws TCException{
		TCComponentBOMWindow bomwindow = null;
		List<PrtL5BomLineBean> list = new ArrayList<PrtL5BomLineBean>();
		try {
			bomwindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomline = bomwindow.setWindowTopLine(revision.getItem(), revision, null, null);
			getALLBOMLine(topBomline,list,null,flag);

		} catch (TCException e) {
			e.printStackTrace();
			System.out.println("11111111111");
		} finally {
			bomwindow.close();
		}
		return list;
	}
	
	/**
	 * 
	 * @param topBomline
	 * @param list
	 * @param partBean
	 * @param flag   L5為true
	 * @throws TCException
	 */
	public void getALLBOMLine(TCComponentBOMLine topBomline,List<PrtL5BomLineBean> list,PrtL5BomLineBean partBean,boolean flag) throws TCException {
		PrtL5BomLineBean bean = getBean(topBomline, partBean,flag);
		if(ObjectUtil.isNull(bean)) {
			return;
		}
		list.add(bean);
		AIFComponentContext[] childrens = topBomline.getChildren();
		if (childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				getALLBOMLine(children, list, bean,flag);
			}
		}
	}

	/**
	 * 
	 * @param topBomline
	 * @param partBean
	 * @param flag  L5為true標識圖號查詢，L10為false標識料號查詢
	 * @return
	 * @throws TCException
	 */
	private PrtL5BomLineBean getBean(TCComponentBOMLine topBomline,PrtL5BomLineBean partBean,boolean flag) throws TCException {
		PrtL5BomLineBean bean = new PrtL5BomLineBean();
		if(ObjectUtil.isNull(partBean)) {
			bean.setLevel(0);
			bean.setParentPartNum("");
			bean.setParentDrawingNum("");
		} else {
			bean.setLevel(partBean.getLevel() +1);
			bean.setParentPartNum(partBean.getPartNum());
			bean.setParentDrawingNum(partBean.getDrawingNum());
			bean.setSku(partBean.getSku());
		}
		TCComponentItemRevision itemRevision = topBomline.getItemRevision();
		String qty = topBomline.getProperty("bl_quantity");
		if(StrUtil.isNotBlank(qty)) {
			bean.setQty(NumberUtil.round(qty, 3).doubleValue());
		}else {
			bean.setQty(1.0D);
		}
		if(flag) {
			if(bean.getLevel() == 0) {
				bean.setSku(itemRevision.getProperty("d9_CustomerPN"));
			}
			bean.setDrawingNum(itemRevision.getProperty("item_id"));
			bean.setVersion2D(itemRevision.getProperty("d9_2DRev"));
			bean.setVersion3D(itemRevision.getProperty("d9_3DRev"));
			bean.setEnDesc(itemRevision.getProperty("d9_EnglishDescription"));
			bean.setChDesc(itemRevision.getProperty("d9_ChineseDescription"));
			TCComponentItemRevision iComponentItemRevision = null;
			// 查詢bom行關聯的物料
			String buPn = itemRevision.getProperty("d9_BUPN");
			if(StrUtil.isBlank(buPn)) {
				return bean;
			}
			TCComponent[] components = itemRevision.getRelatedComponents("representation_for");
			for (TCComponent tcComponent : components) {
				TCComponentItemRevision componentItemRevision = (TCComponentItemRevision) tcComponent;
				String itemId = componentItemRevision.getProperty("item_id");
				if(buPn.equals(itemId)) {
					iComponentItemRevision = componentItemRevision;
					break;
				}
			}
			if(ObjectUtil.isNull(iComponentItemRevision)) {
				return bean;
			}
			itemRevision = iComponentItemRevision;
			bean.setPartNum(itemRevision.getProperty("item_id"));
		}else {
			bean.setPartNum(itemRevision.getProperty("item_id"));
			bean.setVersion2D(itemRevision.getProperty("d9_2DRev"));
			bean.setVersion3D(itemRevision.getProperty("d9_3DRev"));
			// 查詢關聯的圖號
			TCComponent[] components = itemRevision.getRelatedComponents("TC_Is_Represented_By");
			if(components!= null && components.length > 0) {
				TCComponentItemRevision componentItemRevision = (TCComponentItemRevision) components[0];
				bean.setDrawingNum(componentItemRevision.getProperty("item_id"));
			}
			if(bean.getLevel() == 0) {
				bean.setSku(itemRevision.getProperty("d9_CustomerPN"));
			}
		}
		bean.setEnDesc(itemRevision.getProperty("d9_EnglishDescription"));
		bean.setChDesc(itemRevision.getProperty("d9_ChineseDescription"));
		bean.setUm(itemRevision.getProperty("d9_Un"));
		bean.setRemark(itemRevision.getProperty("d9_Remarks"));
		bean.setSubsystem(itemRevision.getProperty("d9_Module"));
		bean.setCommodityType(itemRevision.getProperty("d9_CommodityType"));
		bean.setMaterial(itemRevision.getProperty("d9_Material"));
		bean.setOutPart(itemRevision.getProperty("d9_OuterPart"));
		bean.setObjectType(itemRevision.getProperty("d9_ProcurementMethods"));
		bean.setCommonParts(itemRevision.getProperty("d9_SourcingType"));
		bean.setFamilyPartNum(itemRevision.getProperty("d9_FamilyPartNumber"));
		return bean;
	}
	
	private void addBean(Map<String, PrtL5BomLineBean> map,PrtL5BomLineBean bean) {
		String key = bean.getDrawingNum() +  bean.getPartNum() + bean.getLevel() + bean.getQty();
		if(ObjectUtil.isNull(map.get(key))) {
			map.put(key, bean);
		}else {
			PrtL5BomLineBean itemBean = map.get(key);
			itemBean.setParentPartNum(addItem(itemBean.getParentPartNum() , bean.getParentPartNum()));
			itemBean.setParentDrawingNum(addItem(itemBean.getParentDrawingNum(), bean.getParentDrawingNum()));
			itemBean.setSku(addItem(itemBean.getSku(), bean.getSku()));
		}
	}
	
	private String addItem(String str,String item) {
		if(StrUtil.isBlank(item)) {
			return str;
		}
		if(StrUtil.isBlank(str)) {
			return item;
		}
		List<String> split = StrSplitter.split(str, ",",true,true);
		if(CollUtil.isNotEmpty(split)) {
			Set<String> set = new HashSet<String>(split);
			if(!set.contains(item)) {
				return str +  "," +item;
			}
		}
		return str;
	}

}
