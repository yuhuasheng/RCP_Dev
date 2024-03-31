package com.foxconn.electronics.pamatrixbom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.foxconn.electronics.matrixbom.export.DTLenovoCableMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoCoverGasketCableMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoPSUListExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoScrewMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.DTLenovoThermalMatrixExcelWriter;
import com.foxconn.electronics.matrixbom.export.IMatrixBOMExportWriter;
import com.foxconn.electronics.matrixbom.export.MatrixBOMExportBean;
import com.foxconn.electronics.matrixbom.export.PackingMatrixExport20230825;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.tcutils.constant.DatasetEnum;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

import cn.hutool.core.util.ArrayUtil;

/**
 * 002349
 * 001375
 * DCN-000163
 * @author MW00343
 *
 */
public class PAMatrixBOMExportService {
	
	private static PAMatrixBOMService matrixBOMService;
			
	public static void setMatrixBOMService(PAMatrixBOMService service) {
		matrixBOMService = service;
	}
	
	public static void setMatrixBOMService(AbstractAIFUIApplication app) throws TCException {
		matrixBOMService = new PAMatrixBOMService(app);
	}

	static String matrixType = null;
	static Map<String, IMatrixBOMExportWriter> matrixTypeExcelTemplateMap = new HashMap<String, IMatrixBOMExportWriter>() {
		private static final long serialVersionUID = 1L;
		{
			put("DT Lenovo Cable Matrix", new DTLenovoCableMatrixExcelWriter());
			put("DT Lenovo Screw Matrix", new DTLenovoScrewMatrixExcelWriter());
			put("DT Lenovo Cover&Gasket&Cable tie Matrix", new DTLenovoCoverGasketCableMatrixExcelWriter());
			put("DT Lenovo Thermal Matrix", new DTLenovoThermalMatrixExcelWriter());
			put("DT Lenovo PSU List", new DTLenovoPSUListExcelWriter());
			put("MNT Packing Matrix", new PackingMatrixExport20230825());
		}
   };
	
	public static String exportBOMExcel(String uid) throws Exception {
    	TCComponentBOMWindow window = null;
    	String excelPath = null;
    	TCSession session = com.foxconn.tcutils.util.TCUtil.getTCSession();
    	com.foxconn.tcutils.util.TCUtil.setBypass(session);
    	
    	try {
        	TCComponent targetComponent = com.foxconn.tcutils.util.TCUtil.findItemRevistion(uid);
    		TCComponentItemRevision matrixItemRevision = (TCComponentItemRevision)targetComponent;
    		String matrixVer = matrixItemRevision.getProperty("item_revision_id");
    		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
    		
    		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {			
    			return null;
    		}
    		
    		IMatrixBOMExportWriter exportWriter = matrixTypeExcelTemplateMap.get(matrixType);
    		if (exportWriter instanceof PackingMatrixExport20230825) {
    			PackingMatrixExport20230825 packingMatrixExport = (PackingMatrixExport20230825)exportWriter;
				//packingMatrixExport.writerBOM(matrixItemRevision);
				excelPath = packingMatrixExport.exportBOM(matrixItemRevision);
			}
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			com.foxconn.tcutils.util.TCUtil.closeBypass(session);
			if (window != null) {
				window.close();
			}
		}  
    	return excelPath;
	}
	
	public static String createDCNexportDCNExcel(TCComponent targetComponent,String path) throws Exception {
		/**
		 * 注意：假定DCN中只有变体
		 */
		String dcnNo = targetComponent.getProperty("item_id");
		AIFComponentContext[] solutionItems = targetComponent.getRelated("CMHasSolutionItem");
		if(solutionItems.length == 0) {
			throw new Exception("没有任何更改！");
		}
		TCComponentItemRevision matrixItemRevision = null;
		for(AIFComponentContext context:solutionItems) {
			AIFComponentContext[] related = ((TCComponentItemRevision)context.getComponent()).whereReferencedByTypeRelation(new String[] {"D9_ProductLineRevision"},new String[] {"D9_HasVariants_REL"});;
			if(related.length != 0) {
				matrixItemRevision = (TCComponentItemRevision) related[0].getComponent();
			}
		}
		if(matrixItemRevision == null) {
			throw new Exception("没有找到Matrix，请检查");
		}		
		matrixType = matrixItemRevision.getProperty("d9_MatrixType");
		if(!matrixTypeExcelTemplateMap.containsKey(matrixType)) {
			throw new Exception("不支持的Matrix类型");
		}
		List<MatrixBOMExportBean> list = new ArrayList<MatrixBOMExportBean>();
		AIFComponentContext[] impactedItems = targetComponent.getRelated("CMHasImpactedItem");
		for (int i = 0; i < impactedItems.length; i++) {
			TCComponentItemRevision impactedItem = (TCComponentItemRevision) impactedItems[i].getComponent();
			String itemId = impactedItem.getProperty("item_id");
			InterfaceAIFComponent solutionItem = findNextRevision(solutionItems, itemId);
			if(solutionItem==null) {
				continue;
			}
			System.out.println("item_id = "+itemId);
			comparison(impactedItem, (TCComponentItemRevision)solutionItem, list);
		}
		if(list.isEmpty()) {
			throw new Exception("没有任何更改");
		}
		list.sort(new Comparator<MatrixBOMExportBean>() {
			@Override
			public int compare(MatrixBOMExportBean o1, MatrixBOMExportBean o2) {
				return o1.bomItem.compareTo(o2.bomItem);
			}
		});
		
		//写Excel
		File file = new File(path);
		String excelPath = matrixTypeExcelTemplateMap.get(matrixType).createDCNwriterDCN(matrixItemRevision,dcnNo, list,path);
		return excelPath;
	}
	
	static InterfaceAIFComponent findNextRevision(AIFComponentContext[] solutionItems,String impactItemId) throws Exception {
		for (int i = 0; i < solutionItems.length; i++) {
			InterfaceAIFComponent component = solutionItems[i].getComponent();
			String solutionItemId = component.getProperty("item_id");
			if(solutionItemId.equals(impactItemId)) {
				return component;
			}
		}
		return null;
	}
	static void comparison(TCComponentItemRevision impactRevision,TCComponentItemRevision solutionRevision,List<MatrixBOMExportBean> list) throws Exception {
		TCComponentBOMWindow createBomWindowBySnapshot = PartBOMUtils.createBomWindowBySnapshot(impactRevision);
		if(createBomWindowBySnapshot==null) {
			return;
		}
		TCComponentBOMLine impactBOMLine = createBomWindowBySnapshot.getTopBOMLine();
		TCComponentBOMLine solutionBOMLine = TCUtil.openBomWindow(TCUtil.getTCSession(), solutionRevision);
		allBOMLineUnpackage(impactBOMLine);
		allBOMLineUnpackage(solutionBOMLine);
		// 比对
		doComparison(getChildren(impactBOMLine),getChildren(solutionBOMLine),impactBOMLine,solutionBOMLine,list);
	}
	
static void doComparison(TCComponentBOMLine[] impactList,TCComponentBOMLine[] solutionList,TCComponentBOMLine impactPrimayBOMLine,TCComponentBOMLine solutionPrimayBOMLine,List<MatrixBOMExportBean> list) throws Exception {
		
		if(impactList==null) {
			impactList = new TCComponentBOMLine[0];
		}
		
		if(solutionList==null) {
			solutionList = new TCComponentBOMLine[0];
		}
		
		IMatrixBOMExportWriter iMatrixBOMExportWriter = matrixTypeExcelTemplateMap.get(matrixType);
		
		// 找到所有新增的
		for (TCComponentBOMLine solutionBomLine : solutionList) {
			TCComponentBOMLine  findComponent = findComponent(impactList, solutionBomLine,iMatrixBOMExportWriter);
			if(findComponent == null) {
				// 新增
				MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(null,solutionBomLine, "A");
				if(solutionBomLine.isSubstitute()) {
					primaryBean.bomItem = solutionPrimayBOMLine.getProperty("bl_sequence_no");
					primaryBean.quantity = solutionPrimayBOMLine.getProperty("bl_quantity");
				}
				list.add(primaryBean);
				TCComponentBOMLine[] listSubstitutes = listSubstitutes(solutionBomLine);
				for (TCComponentBOMLine substitutes : listSubstitutes) {
					MatrixBOMExportBean beanSubstitutes = iMatrixBOMExportWriter.assembleMatixChangeBean(null,substitutes, "A");
					beanSubstitutes.bomItem = primaryBean.bomItem;
					beanSubstitutes.quantity = primaryBean.quantity;
					list.add(beanSubstitutes);
				}
			}
		}
		// 找到所有删除和修改的
		for (TCComponentBOMLine  impactBomLine : impactList) {
			TCComponentBOMLine solutionBomLine = findComponent(solutionList, impactBomLine,iMatrixBOMExportWriter);
			if(solutionBomLine == null) {
				// 删除
				MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(impactBomLine, null, "D");
				if(impactBomLine.isSubstitute()) {
					String bl_sequence_no = impactPrimayBOMLine.getProperty("bl_sequence_no");
					String bl_quantity = impactPrimayBOMLine.getProperty("bl_quantity");
					
					primaryBean.bomItem = bl_sequence_no;
					primaryBean.quantity = bl_quantity;
				}
				list.add(primaryBean);
				TCComponentBOMLine[] listSubstitutes = listSubstitutes(impactBomLine);
				for (TCComponentBOMLine substitutes : listSubstitutes) {
					MatrixBOMExportBean beanSubstitutes = iMatrixBOMExportWriter.assembleMatixChangeBean(substitutes, null, "D");
					beanSubstitutes.bomItem = primaryBean.bomItem;
					beanSubstitutes.quantity = primaryBean.quantity;
					list.add(beanSubstitutes);
				}
			}else {
				if(iMatrixBOMExportWriter.isChange(impactBomLine,solutionBomLine)) {
					// 修改
					MatrixBOMExportBean primaryBean = iMatrixBOMExportWriter.assembleMatixChangeBean(impactBomLine,solutionBomLine, "C");
					if(primaryBean!=null) {
						list.add(primaryBean);
					}
				}
			    // 比较替代料
				doComparison(listSubstitutes(impactBomLine),listSubstitutes(solutionBomLine),impactBomLine,solutionBomLine,list);
			}
		}	
	}
	
	/**
	 * 解包全部BOMLine
	 * 
	 * @param designBOMTopLine
	 * @throws Exception
	 */
	private static void allBOMLineUnpackage(TCComponentBOMLine designBOMTopLine) throws Exception {
		AIFComponentContext[] children = designBOMTopLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			if (bomLine.isPacked()) {
				bomLine.unpack();
			}
			allBOMLineUnpackage(bomLine);
		}
	}
	
	static TCComponentBOMLine[] getChildren(TCComponentBOMLine bomline) throws TCException {
		AIFComponentContext[] children = bomline.getChildren();
		if(ArrayUtil.isEmpty(children)) {
			return new TCComponentBOMLine[0];
		}
		TCComponentBOMLine[] list = new TCComponentBOMLine[children.length];
		for(int i=0;i<list.length;i++) {
			list[i] = (TCComponentBOMLine) children[i].getComponent();
		}
		return list;
	}
	
	static TCComponentBOMLine[] listSubstitutes(TCComponentBOMLine bomline) throws TCException {
		if(bomline.isSubstitute()||!bomline.hasSubstitutes()) {
			return new TCComponentBOMLine[0];
		}
		return bomline.listSubstitutes();
	}

	static TCComponentBOMLine findComponent(TCComponentBOMLine[] children,TCComponentBOMLine bomline,IMatrixBOMExportWriter iMatrixBOMExportWriter) throws Exception {
		for (TCComponentBOMLine childBomLine : children) {		
			if(iMatrixBOMExportWriter.equals(childBomLine,bomline)) {
				return childBomLine;
			}
		}
		return null;
	}
	
	
	
	public static String generateDocument(TCSession session, TCComponentItemRevision matrixItemRevision,
			String filePath, String matrixVer) throws Exception {
		com.foxconn.tcutils.util.TCUtil.setBypass(session);
		String dsName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		TCComponentItemRevision documentItemRev = checkDocumentExist(matrixItemRevision);
		if (CommonTools.isEmpty(documentItemRev)) {
			documentItemRev = createDocument(session, matrixItemRevision); // 创建document文档
		}

		if (com.foxconn.tcutils.util.TCUtil.isReleased(documentItemRev)) { // 判断是否已经发行
			String versionRule = com.foxconn.tcutils.util.TCUtil.getVersionRule(documentItemRev);
			String newRevId = com.foxconn.tcutils.util.TCUtil.reviseVersion(session, versionRule,
					documentItemRev.getTypeObject().getName(), documentItemRev.getUid()); // 返回指定版本规则的版本号
			TCComponentItemRevision newDocumentItemRev = com.foxconn.tcutils.util.TCUtil.doRevise(documentItemRev,
					newRevId); // 进行升版
			TCComponentDataset dataset = checkDataset(newDocumentItemRev, dsName);
			if (dataset != null) {
				newDocumentItemRev.remove("IMAN_specification", dataset);
			}

			TCComponent[] relatedComponents = matrixItemRevision.getRelatedComponents("IMAN_specification");
			if (CommonTools.isEmpty(relatedComponents)) {
				matrixItemRevision.add("IMAN_specification", newDocumentItemRev);
			} else {
				matrixItemRevision.add("IMAN_specification", newDocumentItemRev, relatedComponents.length + 1);
			}

			documentItemRev = newDocumentItemRev;
			dsName = documentItemRev.getProperty("object_name").replace(" ", "_") + "_" + newRevId + ".xlsx";
		}

		dsName = dsName.replace(matrixVer, documentItemRev.getProperty("item_revision_id"));
		String tempFilePath = filePath.replace(filePath.substring(filePath.lastIndexOf(File.separator) + 1), dsName);
		if (!filePath.equals(tempFilePath))
			CommonTools.reNameFile(filePath, tempFilePath);

		if (checkDataset(documentItemRev, dsName) == null) {
			TCComponentDataset createDataSet = com.foxconn.tcutils.util.TCUtil.createDataSet(session, tempFilePath,
					DatasetEnum.MSExcelX.type(), dsName, DatasetEnum.MSExcelX.refName());
			documentItemRev.add("IMAN_specification", createDataSet);
		} else {
			com.foxconn.tcutils.util.TCUtil.updateDataset(session, documentItemRev, "IMAN_specification", tempFilePath);
		}
		com.foxconn.tcutils.util.TCUtil.closeBypass(session);

		return tempFilePath;
	}
	 
	 
	/**
	 * 校验文档是否存在
	 * 
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	public static TCComponentItemRevision checkDocumentExist(TCComponentItemRevision itemRev) throws TCException {
		// List<TCComponentItemRevision> documentList = new
		// ArrayList<TCComponentItemRevision>();
		itemRev.refresh();
		TCComponent[] imanSpecifications = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isEmpty(imanSpecifications)) {
			return null;
		}

		TCComponentItem document = null;
		for (int i = 0; i < imanSpecifications.length; i++) {
			if (imanSpecifications[i] instanceof TCComponentItemRevision) {
				TCComponentItemRevision documentRev = (TCComponentItemRevision) imanSpecifications[i];
				String objectName = documentRev.getProperty("object_name");
				String objectType = documentRev.getTypeObject().getName();
				if (objectName.equals(itemRev.getProperty("object_name")) && "DocumentRevision".equals(objectType)) {
					document = documentRev.getItem();
					break;
				}
			}
		}
		if (document != null) {
			return document.getLatestItemRevision();
		}

//			Stream.of(imanSpecifications).forEach(component -> {
//				try {
//					String objectType = component.getTypeObject().getName();
//					String objectName = component.getProperty("object_name");
//					if ("DocumentRevision".equals(objectType) && objectName.equals(itemRev.getProperty("object_name"))) {
//						documentList.add((TCComponentItemRevision) component);
//					}				
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			});
//			if (CommonTools.isNotEmpty(documentList)) {
//				return documentList.get(documentList.size() - 1);
//			}
		return null;
	}
	
	/**
	 * 创建文档对象
	 * @param itemRev
	 * @throws Exception 
	 */
	public static TCComponentItemRevision createDocument(TCSession session, TCComponentItemRevision itemRev) throws Exception {
		Map<String, Object> itemMap = new HashMap<String, Object>();
		String systemType = "S";
		String systemName = "D";
		String systemLevel = "3";
		String productCode = "DT";
		String productLine = "CM";
		String customerName = "L";
		String id = com.foxconn.tcutils.util.TCUtil.generateId(session, "Document");
        id = id.replace(systemType + systemName + systemLevel, "");
		String docId = systemType + systemName + systemLevel + productCode + productLine + customerName + id;
		System.out.println("==>> docId: " + docId);
		itemMap.put("object_name", itemRev.getProperty("object_name") == null ? "" : itemRev.getProperty("object_name"));
		itemMap.put("d9_DocumentType", "Other Design Document");
		
		Map<String, Object> revisionMap = new HashMap<String, Object>();
		revisionMap.put("d9_ActualUserID", itemRev.getProperty("d9_ActualUserID") == null ? "" : itemRev.getProperty("d9_ActualUserID"));				
		TCComponentItem document = (TCComponentItem) com.foxconn.tcutils.util.TCUtil.createObject(com.foxconn.tcutils.util.TCUtil.getTCSession(), "Document", docId, itemMap, revisionMap);
		TCComponentItemRevision documentItemRev = document.getLatestItemRevision();
		itemRev.add("IMAN_specification", documentItemRev);
		return documentItemRev;
	}
	
	public static TCComponentDataset checkDataset(TCComponentItemRevision itemRev, String dsName) throws TCException {
		itemRev.refresh();
		TCComponent[] imanSpecifications = itemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isEmpty(imanSpecifications)) {
			return null;
		}
		
		Optional<TCComponent> findAny = Stream.of(imanSpecifications).filter(component -> {
			try {
				String objectType = component.getTypeObject().getName();
				String objectName = component.getProperty("object_name");
				if ("MSExcelX".equals(objectType) && objectName.startsWith(dsName.substring(0, dsName.lastIndexOf("_")))) {
					return true;
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}).findAny();
		
		if (findAny.isPresent()) {
			return (TCComponentDataset) findAny.get();
		}
		return null;	
	}

}
