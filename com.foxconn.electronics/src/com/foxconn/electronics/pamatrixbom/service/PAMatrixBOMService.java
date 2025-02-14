package com.foxconn.electronics.pamatrixbom.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.matrixbom.constant.MatrixBOMConstant;
import com.foxconn.electronics.matrixbom.domain.ChangeLogBean;
import com.foxconn.electronics.matrixbom.domain.SaveChangeLogBean;
import com.foxconn.electronics.pamatrixbom.domain.PAProductLineBOMBean;
import com.foxconn.electronics.pamatrixbom.domain.PAVariableBOMBean;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.ModelObject;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;

public class PAMatrixBOMService {
	public SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式
	private static TCSession session;

	private final int threadNum = 5;

	private ExecutorService es = null;;

	public PAMatrixBOMService() {
	}
	
	public ExecutorService getEs() {
		return es;
	}

	public void setEs(ExecutorService es) {
		this.es = es;
	}

	public PAMatrixBOMService(AbstractAIFUIApplication app) throws TCException {
		session = (TCSession) app.getSession();
		es = Executors.newFixedThreadPool(threadNum); // 创建固定大小的线程池对象
	}
	
	
	/**
	 * PA
	 * 获取MatrixBOM数据
	 * 
	 * @param uid
	 * @return
	 */
	public AjaxResult getPackingBOMStruct(String uid) {
		try {
			TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(uid);
			if (ObjUtil.isEmpty(productLineItemRev)) {
				throw new Exception("获取产品BOM对象版本失败");
			}
			Future<PAVariableBOMBean> future1 = variableSeriesBOM(productLineItemRev);
			Future<PAProductLineBOMBean> future2 = productSeriesBOM(productLineItemRev);

			PAVariableBOMBean rootSeriesBOMBean = future1.get();
			PAProductLineBOMBean rootProductLineBOMBean = future2.get();

			if (StrUtil.isNotEmpty(rootSeriesBOMBean.getErrorMsg())) {
				throw new Exception(rootSeriesBOMBean.getErrorMsg());
			}
			if (StrUtil.isNotEmpty(rootProductLineBOMBean.getErrorMsg())) {
				throw new Exception(rootProductLineBOMBean.getErrorMsg());
			}

			JSONObject data = new JSONObject();
			String matrixType = productLineItemRev.getProperty("d9_MatrixType");
			String object_string = productLineItemRev.getProperty("object_string");
			String object_type = productLineItemRev.getProperty("object_type");
			TCComponentUser tcUser = (TCComponentUser) productLineItemRev.getReferenceProperty("owning_user");
			String owning_user = tcUser.toString();
			Date date1 = productLineItemRev.getTCProperty("creation_date").getDateValue();
			Date date2 = productLineItemRev.getTCProperty("last_mod_date").getDateValue();
			String creation_date = df.format(date1);
			String last_mod_date = df.format(date2);

			String release_status_list = "";
			TCComponent[] relStatus = productLineItemRev.getReferenceListProperty("release_status_list");
			if (relStatus != null && relStatus.length > 0) {
				TCComponent status = relStatus[relStatus.length - 1];
				release_status_list = status.getTCProperty("name").getStringValue();
			}

			data.put("matrixType", matrixType);
			data.put("object_string", object_string);
			data.put("object_type", object_type);
			data.put("owning_user", owning_user);
			data.put("creation_date", creation_date);
			data.put("last_mod_date", last_mod_date);
			data.put("release_status_list", release_status_list);

			data.put("partList", rootProductLineBOMBean);
			data.put("variableBOM", rootSeriesBOMBean);

			//String json = JSONObject.toJSONString(data);
			//System.out.println("data:" + json);

			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", data);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * PA
	 * 处理系列BOM
	 * 
	 * @param productLineItemRev
	 * @return
	 */
	private Future<PAVariableBOMBean> variableSeriesBOM(TCComponentItemRevision productLineItemRev) {
		Future<PAVariableBOMBean> future = es.submit(() -> {
			Thread.currentThread().setName("處理Series BOM Thread: ");
			TCComponentBOMWindow seriesBomWindow = null;
			try {

				TCComponentItemRevision seriesItemRev = getSeriesItemRev(productLineItemRev);
				if (ObjUtil.isEmpty(seriesItemRev)) {
					return new PAVariableBOMBean();
				}

				seriesBomWindow = TCUtil.createBOMWindow(session);
				if (ObjUtil.isEmpty(seriesBomWindow)) {
					throw new Exception("获取BOMWindow失败");
				}

				seriesItemRev.refresh();
				TCComponentBOMLine seriesTopLine = TCUtil.getTopBomline(seriesBomWindow, seriesItemRev);
				if (ObjUtil.isEmpty(seriesTopLine)) {
					throw new Exception("获取Series顶阶BOMLine失败");
				}
				seriesTopLine.refresh();

				return getVariableSeriesBOMStruct(seriesTopLine);
			} catch (Exception e) {
				e.printStackTrace();
				PAVariableBOMBean bean = new PAVariableBOMBean();
				bean.setErrorMsg(e.getLocalizedMessage());
				return bean;
			} finally {
				if (ObjUtil.isNotEmpty(seriesBomWindow)) {
					seriesBomWindow.close();
				}
			}
		});
		return future;
	}
	
	/**
	 * PA
	 * 获取产品系列BOM结构
	 * 
	 * @param rootLine
	 * @return
	 * @throws TCException
	 * @throws InterruptedException
	 */
	private PAVariableBOMBean getVariableSeriesBOMStruct(TCComponentBOMLine rootLine) throws TCException, InterruptedException {
		rootLine.refresh();
		PAVariableBOMBean rootBean = new PAVariableBOMBean(rootLine);
		System.out.println(Thread.currentThread().getName() + " ==>> title: " + rootBean.getItemId());
		AIFComponentContext[] componmentContext = null;
		if (rootLine.hasChildren()) {
			componmentContext = rootLine.getChildren();
		}
		if (componmentContext != null) {

			ExecutorService service = Executors.newCachedThreadPool();
			Semaphore position = new Semaphore(5);
			for (int i = 0; i < componmentContext.length; i++) {
				service.submit(new PAMyExecutorBOMStruct(componmentContext[i], rootBean, position));
			}
			//Thread.sleep(componmentContext.length * 150);
			
			Thread.sleep(500);
			position.acquireUninterruptibly(5);
			position.release(5);
			service.shutdown();
		}
		System.out.println(Thread.currentThread().getName() + " ==>> end title: " + rootBean.getItemId());
		
		return rootBean;
	}
	
	/**
	 * PA 处理产品BOM
	 * 
	 * @param productLineItemRev
	 * @return
	 */
	private Future<PAProductLineBOMBean> productSeriesBOM(TCComponentItemRevision productLineItemRev) {
		Future<PAProductLineBOMBean> future = es.submit(() -> {
			TCComponentBOMWindow productLineBomWindow = null;
			Thread.currentThread().setName("处理ProductLine BOM Thread: ");
			try {
				productLineBomWindow = TCUtil.createBOMWindow(session);
				if (ObjUtil.isEmpty(productLineBomWindow)) {
					throw new Exception("获取BOMWindow失败");
				}
				TCComponentBOMLine productLineTopLine = TCUtil.getTopBomline(productLineBomWindow, productLineItemRev);
				if (ObjUtil.isEmpty(productLineTopLine)) {
					throw new Exception("获取ProductLine顶阶BOMLine失败");
				}

				return getPAProductLineBOMStruct(productLineTopLine);
			} catch (Exception e) {
				e.printStackTrace();
				PAProductLineBOMBean bean = new PAProductLineBOMBean();
				return bean;
			} finally {
				if (ObjUtil.isNotEmpty(productLineBomWindow)) {
					productLineBomWindow.close();
				}
			}
		});

		return future;
	}
	
	/**
	 * PA 获取产品线BOM结构
	 * 
	 * @param rootLine
	 * @return
	 * @throws Exception
	 */
	private PAProductLineBOMBean getPAProductLineBOMStruct(TCComponentBOMLine rootLine) throws Exception {
		PAProductLineBOMBean rootBean = new PAProductLineBOMBean(rootLine, session);
		//System.out.println(Thread.currentThread().getName() + " ==>> title: " + rootBean.getItemId());

		rootLine.refresh();
		AIFComponentContext[] childrens_Packed = rootLine.getChildren();
		for (AIFComponentContext aifchildren : childrens_Packed) {
			TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
			if (children.isPacked()) {
				children.unpack();
				children.refresh();
			}
		}
		rootLine.refresh();
		ArrayList<TCComponentBOMLine> lines = new ArrayList<TCComponentBOMLine>();
		AIFComponentContext[] childrens = rootLine.getChildren();

		if (childrens != null) {
			for (AIFComponentContext aifchildren : childrens) {
				TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				if (!children.isSubstitute()) {
					lines.add(children);
				}
			}

			for (TCComponentBOMLine bl : lines) {
				PAProductLineBOMBean bomBean = new PAProductLineBOMBean(bl, session);
				if (bl.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
					List<PAProductLineBOMBean> subBeanList = new ArrayList<PAProductLineBOMBean>();
					for (TCComponentBOMLine subBomline : listSubstitutes) {
						PAProductLineBOMBean subBean = new PAProductLineBOMBean(subBomline, session);
						subBean.setSub(true); // 设置替代料标识位true
						subBean.setItemRevUid(subBomline.getItemRevision().getUid());
						subBean.setLineId(CommonTools.md5Encode(subBean.getItemId()));
						subBeanList.add(subBean);
						addPAProductSubList(bomBean, subBean);
					}
				}
				addPAProductList(rootBean, bomBean);
			}
		}
		return rootBean;
	}	
	
	/**
	 * PA 获取替代料
	 * 
	 * @param current
	 * @param next
	 */
	private void addPAProductSubList(PAProductLineBOMBean current, PAProductLineBOMBean next) {
		List<PAProductLineBOMBean> subList = current.getSubList();
		if (null == subList) {
			subList = new ArrayList<PAProductLineBOMBean>();
			current.setSubList(subList);
		}

		for (PAProductLineBOMBean subBean : subList) {
			if (subBean.getLineId().equals(next.getLineId())) {
				return;
			}
		}
		subList.add(next);
	}
	
	/**
	 * PA 获取子料
	 * 
	 * @param current
	 * @param next
	 */
	private void addPAProductList(PAProductLineBOMBean current, PAProductLineBOMBean next) {
		List<PAProductLineBOMBean> child = current.getChild();
		if (null == child) {
			child = new ArrayList<PAProductLineBOMBean>();
			current.setChild(child);
		}

		for (PAProductLineBOMBean bean : child) {
			if (bean.getLineId().equalsIgnoreCase(next.getLineId())) {

				return;
			}
		}
		child.add(next);
	}

	/**
	 * PA PartBOM初始化加載BOM结构
	 */
	public List<PAProductLineBOMBean> getPABOMStruct(List<String> uidList) throws Exception {
		List<PAProductLineBOMBean> productBeanList = new ArrayList<PAProductLineBOMBean>();
		for (String uid : uidList) {
			TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine rootLine = TCUtil.getTopBomline(bomWindow, TCUtil.loadObjectByUid(uid));
			rootLine.refresh();

			PAProductLineBOMBean productLineBOMStruct = getPABOM(rootLine);
			rootLine.refresh();
			productBeanList.add(productLineBOMStruct);
		}
		return productBeanList;
	}
	
	/**
	 * PA PartBOM初始化加載BOM结构
	 * 
	 * @param rootLine
	 * @return
	 * @throws Exception
	 */
	private PAProductLineBOMBean getPABOM(TCComponentBOMLine rootLine) throws Exception {
		PAProductLineBOMBean rootBean = new PAProductLineBOMBean(rootLine, session);
		//System.out.println(Thread.currentThread().getName() + " ==>> title: " + rootBean.getItemId());

		rootLine.refresh();
		AIFComponentContext[] childrens_Packed = rootLine.getChildren();
		for (AIFComponentContext aifchildren : childrens_Packed) {
			TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
			if (children.isPacked()) {
				children.unpack();
				children.refresh();
			}
		}
		rootLine.refresh();
		ArrayList<TCComponentBOMLine> lines = new ArrayList<TCComponentBOMLine>();
		AIFComponentContext[] childrens = rootLine.getChildren();

		if (childrens != null) {
			for (AIFComponentContext aifchildren : childrens) {
				TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
				if (!children.isSubstitute()) {
					lines.add(children);
				}
			}

			for (TCComponentBOMLine bl : lines) {
				PAProductLineBOMBean bomBean = getPABOM(bl);
				if (bl.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = bl.listSubstitutes();
					List<PAProductLineBOMBean> subBeanList = new ArrayList<PAProductLineBOMBean>();
					for (TCComponentBOMLine subBomline : listSubstitutes) {
						PAProductLineBOMBean subBean = new PAProductLineBOMBean(subBomline, session);
						subBean.setSub(true); // 设置替代料标识位true
						subBeanList.add(subBean);
						addPAProductSubList(bomBean, subBean);
					}
				}
				addPAProductList(rootBean, bomBean);
			}
		}
		return rootBean;
	}
	
	/**
	 * PA 保存 PA PartBOM结构
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult savePartBOM(String data) {
		Gson gson = new Gson();
		List<PAProductLineBOMBean> rootBeanList = gson.fromJson(data, new TypeToken<List<PAProductLineBOMBean>>() {
		}.getType());
		TCComponentBOMWindow bomwindow = null;
		try {
			if (rootBeanList != null && rootBeanList.size() > 0) {

				String productLineItemUID = rootBeanList.get(0).getProductLineItemUID();
				TCComponentItemRevision roductLineItemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(productLineItemUID);
				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine bomline = TCUtil.getTopBomline(bomwindow, roductLineItemRev);

				// 解包
				bomline.refresh();
				AIFComponentContext[] childrens_Packed = bomline.getChildren();
				for (AIFComponentContext aifchildren : childrens_Packed) {
					TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
					if (children.isPacked()) {
						children.unpack();
						children.refresh();
					}
				}

				AIFComponentContext[] childrens = bomline.getChildren();
				if (childrens.length > 0) {
					for (PAProductLineBOMBean productLineBOMBean : rootBeanList) {
						String bomlineuid = productLineBOMBean.getBomLineUid();
						String itemRevUid = productLineBOMBean.getItemRevUid();
						for (int i = 0; i < childrens.length; i++) {
							TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();

							String childrenuid = children.getProperty("bl_occ_fnd0objectId");
							String itemRevisionUid = children.getItemRevision().getUid();
							if ("".equals(bomlineuid)) {

								if (itemRevUid.equals(itemRevisionUid)) {

									System.out.println("itemRevisionUid = " + itemRevisionUid);
									savePaBOM(children, productLineBOMBean, bomwindow);

								}
							} else {
								if (bomlineuid.equals(childrenuid)) {

									System.out.println("childrenuid = " + childrenuid);
									savePaBOM(children, productLineBOMBean, bomwindow);

								}
							}

						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		} finally {
			if (ObjUtil.isNotEmpty(bomwindow)) {
				try {
					bomwindow.save();
					bomwindow.close();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return AjaxResult.success("保存成功");
	}
	
	/**
	 * PA 保存
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult savePackingMatrixBOM(String data) {
		try {
			Gson gson = new Gson();
			PAVariableBOMBean rootBean = gson.fromJson(data, PAVariableBOMBean.class);
			saveMNTSeriesData(rootBean);
			return AjaxResult.success("保存成功");
		} catch (Exception e) {
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * PA 保存系列BOM数据
	 * 
	 * @param rootBean
	 * @throws TCException
	 */
	private void saveMNTSeriesData(PAVariableBOMBean rootBean) throws TCException {
		TCComponentBOMWindow bomWindow = null;
		try {
			String uid = rootBean.getItemRevUid();
			TCComponentItemRevision seriesItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(uid);

			bomWindow = TCUtil.createBOMWindow(session);
			if (ObjUtil.isEmpty(bomWindow)) {
				throw new Exception("获取BOMWindow失败");
			}

			TCComponentBOMLine topLine = TCUtil.getTopBomline(bomWindow, seriesItemRev);

			if (ObjUtil.isEmpty(topLine)) {
				throw new Exception("获取Series顶阶BOMLine失败");
			}
			TCUtil.setBypass(session);
			Map<String, String> seriesMap = new HashMap<String, String>();
			rootBean.setLevel(0);
			List<PAVariableBOMBean> seriesList = rootBean.getChild();
			for (PAVariableBOMBean s : seriesList) {
				seriesMap.put(s.getItemRevUid(), s.getItemRevUid());
				s.setLevel(1);
				List<PAVariableBOMBean> chList = s.getChild();
				for (PAVariableBOMBean s1 : chList) {
					s1.setLevel(2);
				}
			}

			saveMNTData(topLine, rootBean, seriesMap); // 保存数据
			TCUtil.closeBypass(session);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ObjUtil.isNotEmpty(bomWindow)) {
				bomWindow.save();
				bomWindow.close();
			}
		}
	}
	
	
	public void savePaBOM(TCComponentBOMLine topBomLine, PAProductLineBOMBean paBomBean, TCComponentBOMWindow bomwindow)
			throws Exception {
		List<PAProductLineBOMBean> subList = paBomBean.getSubList();
		// 替代料
		if (topBomLine.hasSubstitutes()) {
			int f_sub = 0;

			TCComponentBOMLine[] listSubstitutes = topBomLine.listSubstitutes();
			for (TCComponentBOMLine subBomline : listSubstitutes) {
				String sub_bl_item_id = subBomline.getProperty("bl_item_item_id");
				if (subList != null && subList.size() > 0) {
					for (PAProductLineBOMBean sub : subList) {
						String itemId_sub = sub.getItemId();

						if (sub_bl_item_id.equals(itemId_sub)) {
							if (sub.getIsModifyItem()) {
								modifyItemPro(subBomline, sub);
								bomwindow.save();
							}
							f_sub = 1;

							break;
						}
					}
				}
				if (f_sub == 0) {
					subBomline.cut();
					bomwindow.save();
				}

			}
		}

		if (subList != null && subList.size() > 0) {
			for (PAProductLineBOMBean sub : subList) {
				int f_sub = 0;
				String itemId_sub = sub.getItemId();

				if (topBomLine.hasSubstitutes()) {
					TCComponentBOMLine[] listSubstitutes = topBomLine.listSubstitutes();
					for (TCComponentBOMLine subBomline : listSubstitutes) {
						String sub_bl_item_id = subBomline.getProperty("bl_item_item_id");
						if (sub_bl_item_id.equals(itemId_sub)) {
							f_sub = 1;
							break;
						}
					}
				}

				if (f_sub == 0) {// 新增
					TCComponentItemRevision itemrevision = getTCItemRevision(sub);

					if (itemrevision != null) {
						TCComponentBOMLine childBomLine = topBomLine.add(itemrevision.getItem(), itemrevision, null,true);
						modifyItemPro(childBomLine, sub);
						bomwindow.save();
					}
				}

			}
		}

		AIFComponentContext[] childrens = null;
		if (topBomLine.hasChildren()) {
			childrens = topBomLine.getChildren();
		}

		if (childrens != null) {

			// 通过BOM循环
			for (int i = 0; i < childrens.length; i++) {
				int f = 0;

				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String bl_itemid = children.getProperty("bl_item_item_id");
				List<PAProductLineBOMBean> childsBean = paBomBean.getChild();
				for (PAProductLineBOMBean childBean : childsBean) {
					String itemId = childBean.getItemId();

					if (bl_itemid.equals(itemId)) {
						f = 1; // 修改
						if (childBean.getIsModifyItem()) {
							modifyBomPro(children, childBean);
							modifyItemPro(children, childBean);
							bomwindow.save();
						}

						savePaBOM(children, childBean, bomwindow);

						break;
					}
				}

				if (f == 0) {// 刪除
					children.cut();
					bomwindow.save();
				}
			}

			// 新增 ：通过传递属性循环 BOM
			List<PAProductLineBOMBean> childsBean = paBomBean.getChild();
			for (PAProductLineBOMBean childBean : childsBean) {
				int f = 0;
				String itemId = childBean.getItemId();
				if (childrens.length > 0) {
					for (int i = 0; i < childrens.length; i++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
						String bl_itemid = children.getProperty("bl_item_item_id");
						if (bl_itemid.equals(itemId)) {
							f = 1;
							savePaBOM(children, childBean, bomwindow);

							break;
						}
					}
				}

				if (f == 0) {// 新增
					TCComponentItemRevision itemrevision = getTCItemRevision(childBean);

					if (itemrevision != null) {
						TCComponentBOMLine childBomLine = topBomLine.add(itemrevision.getItem(), itemrevision, null,
								false);
						modifyBomPro(childBomLine, childBean);
						modifyItemPro(childBomLine, childBean);
						bomwindow.save();

						savePaBOM(childBomLine, childBean, bomwindow);
					}
				}
			}
		} else {
			List<PAProductLineBOMBean> childsBean = paBomBean.getChild();
			if (childsBean != null && childsBean.size() > 0) {
				for (PAProductLineBOMBean childBean : childsBean) {
					
					TCComponentItemRevision itemRev = getTCItemRevision(childBean);

					if (itemRev != null) {
						TCComponentBOMLine childBomLine = topBomLine.add(itemRev.getItem(), itemRev, null, false);
						modifyBomPro(childBomLine, childBean);
						modifyItemPro(childBomLine, childBean);
						bomwindow.save();

						List<PAProductLineBOMBean> subsBeans = childBean.getSubList();
						if (subsBeans != null) {
							for (PAProductLineBOMBean sb : subsBeans) {
								TCComponentItemRevision itemRev_sub = getTCItemRevision(sb);
									if (itemRev_sub != null) {
									TCComponentBOMLine childBomLine_sub = childBomLine.add(itemRev_sub.getItem(), itemRev_sub,
											null, true);
	
									modifyItemPro(childBomLine_sub, sb);
									bomwindow.save();
								}
							}
						}

						List<PAProductLineBOMBean> childsBean_s = childBean.getChild();
						if (childsBean_s != null && childsBean_s.size() > 0) {
							savePaBOM(childBomLine, childBean, bomwindow);
						}
					}
				}
			}

		}
	}

	// 修改bom属性
	public void modifyBomPro(TCComponentBOMLine children, PAProductLineBOMBean rootBean) throws TCException {
		String getQty = rootBean.getQty();
		if(getQty==null || "".equals(getQty)) {
			getQty = "1";
		}
		
		if (getQty.contains(".")) {
			children.setProperty("bl_uom", "Other");
		} else {
			children.setProperty("bl_uom", "");
		}

		String[] bomName = new String[] { "bl_occ_d9_Remark", "bl_quantity", "bl_occ_d9_IsNew" };
		String[] bomValue = new String[] { rootBean.getRemark(), rootBean.getQty(), rootBean.getIsNew() };
		children.setProperties(bomName, bomValue);
		children.setProperty("bl_sequence_no", "" + rootBean.getSequence_no());
	}

	// 修改item属性
	public void modifyItemPro(TCComponentBOMLine children, PAProductLineBOMBean rootBean) throws TCException {
		TCComponentItemRevision itemrev = children.getItemRevision();
		boolean iswrite = TCUtil.checkOwninguserisWrite(session, itemrev);
		if (iswrite) {
			String[] itemPro = new String[] { "d9_DescriptionSAP", "d9_Un", "d9_SupplierZF", "d9_ManufacturerID",
					"d9_ManufacturerPN", "d9_AcknowledgementRev", "d9_EnglishDescription", "d9_MaterialGroup",
					"d9_MaterialType", "d9_ProcurementMethods","d9_SAPRev","d9_ChineseDescription","d9_ActualUserID" };

			if (itemrev != null) {
				// 修改零件属性
				String[] provalue = new String[] { rootBean.getDescriptionSAP(), rootBean.getUn(),
						rootBean.getSupplierZF(), rootBean.getManufacturerID(), rootBean.getManufacturerPN(),
						rootBean.getAcknowledgementRev(), rootBean.getEnglishDescription(), rootBean.getMaterialGroup(),
						rootBean.getMaterialType(), rootBean.getProcurementMethods(), rootBean.getsAPRev(), rootBean.getChineseDescription(), rootBean.getActualUserID() };
				itemrev.setProperties(itemPro, provalue);
			}
			
		}
	}

	public TCComponentItemRevision getTCItemRevision(PAProductLineBOMBean childBean) throws Exception {
		TCComponentItemRevision itemRev = null;
		if(childBean!=null) {
			String itemId = childBean.getItemId();
			String itemRevision = childBean.getItemRevision();
			
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...",
					new String[] { "items_tag.item_id", "item_revision_id" },
					new String[] { itemId, itemRevision });
			if (executeQuery != null && executeQuery.length > 0) {

				for (int i = 0; i < executeQuery.length; i++) {
					if (executeQuery[i] instanceof TCComponentItemRevision) {
						String type = executeQuery[i].getType();
						if("D9_L5_PartRevision".equals(type)) {
							continue;
						}
						
						itemRev = (TCComponentItemRevision) executeQuery[i];
						break;
					}
				}
			} else {
				//创建
				String itemType = "D9_CommonPart";
				TCComponentItem newItem = createItem(session, itemId, itemRevision, "", itemType);
				itemRev = newItem.getLatestItemRevision();
				TCComponentFolder homeFolder = session.getUser().getHomeFolder();
				homeFolder.add("contents", newItem);
			}
		}
		return itemRev;
	}
	

	/**
	 * MNT 保存数据
	 * 
	 * @param rootLine
	 * @param bean
	 * @throws TCException
	 */
	private void saveMNTData(TCComponentBOMLine bomLine, PAVariableBOMBean newBean, Map<String, String> seriesMap)
			throws Exception {
		bomLine.refresh();

		AIFComponentContext[] componmentContext = null;
		if (bomLine.hasChildren()) {
			componmentContext = bomLine.getChildren();
		}

		if (componmentContext != null) {
			List<TCComponentBOMLine> lines = new ArrayList<>();
			for (AIFComponentContext af : componmentContext) {
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) af.getComponent();
				if (childBomLine.isSubstitute()) {
					continue;
				}
				if (childBomLine.isPacked()) {
					TCComponentBOMLine[] tmpsBomLines = childBomLine.getPackedLines();
					for (TCComponentBOMLine l : tmpsBomLines) {
						lines.add(l);
					}
					childBomLine.unpack();
					childBomLine.refresh();
					lines.add(childBomLine);
				} else {
					lines.add(childBomLine);
				}
			}

			for (TCComponentBOMLine childBomLine : lines) {
				TCComponentItemRevision childComponentItemRevision = childBomLine.getItemRevision();
				String item_id = childComponentItemRevision.getProperty("item_id");
				String bl_occ_d9_BOMTemp = childBomLine.getProperty("bl_occ_d9_BOMTemp");
				if (childBomLine.isSubstitute()) {
					continue;
				}
				List<PAVariableBOMBean> newChildsBeans = newBean.getChild();
				int f = 0;
				for (PAVariableBOMBean variableBean : newChildsBeans) {
					if (variableBean.getIsSub()) {
						continue;
					}
					String d9_BOMTemp = variableBean.getD9_BOMTemp();
					String itemId = variableBean.getItemId();
					if (itemId.equals(item_id) && d9_BOMTemp.equals(bl_occ_d9_BOMTemp)) {
						if (seriesMap.get(variableBean.getItemRevUid()) != null) {
							f = 1;
							if (variableBean.getLevel().intValue() < 2) {
								saveMNTData(childBomLine, variableBean, seriesMap);
							}
						} else if (variableBean.getQty() != null && (!"".equalsIgnoreCase(variableBean.getQty().trim()))
								&& Float.parseFloat(variableBean.getQty()) > 0) {
							f = 1;
							String getQty = variableBean.getQty();
							if (getQty.contains(".")) {
								childBomLine.setProperty("bl_uom", "Other");
							} else {
								childBomLine.setProperty("bl_uom", "");
							}
							childBomLine.setProperty("bl_quantity", "" + variableBean.getQty());// 修改
							//childBomLine.setProperty("bl_occ_d9_Remark", "" + variableBean.getRemark().trim());// 修改

							if (variableBean.getLevel().intValue() < 2) {
								saveMNTData(childBomLine, variableBean, seriesMap);
							}
						}
					}
				}

				if (f == 0) {// 刪除
					childBomLine.cut();
				}
			}

			List<PAVariableBOMBean> newChildsBeans = newBean.getChild();
			for (PAVariableBOMBean nb : newChildsBeans) {
				if (nb.getIsSub()) {
					continue;
				}
				int f = 0;
				for (TCComponentBOMLine childBomLine : lines) {
					TCComponentItemRevision childComponentItemRevision = childBomLine.getItemRevision();
					String item_id = childComponentItemRevision.getProperty("item_id");
					String bl_occ_d9_BOMTemp = childBomLine.getProperty("bl_occ_d9_BOMTemp");
					
					String itemId = nb.getItemId();
					String d9_BOMTemp = nb.getD9_BOMTemp();
					
					if (item_id.equals(itemId) && bl_occ_d9_BOMTemp.equals(d9_BOMTemp)) {
						f = 1;
						if (itemId.startsWith("7130")) {
							if (childBomLine.hasSubstitutes()) {
								TCComponentBOMLine[] listSubstitutes = childBomLine.listSubstitutes();
								for (TCComponentBOMLine listSubstitute : listSubstitutes) {
									listSubstitute.cut();
								}
							}

							List<PAVariableBOMBean> subsBeans = nb.getSubList();
							if (subsBeans != null) {
								for (PAVariableBOMBean sb : subsBeans) {
									TCComponentItemRevision sbItemRev = (TCComponentItemRevision) session
											.getComponentManager().getTCComponent(sb.getItemRevUid());
									childBomLine.add(sbItemRev.getItem(), sbItemRev, null, true);
								}
							}
						}

						break;
					}
				}
				if (f == 0) {// 新增
					String uid = nb.getItemRevUid();
					TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager()
							.getTCComponent(uid);
					TCComponentBOMLine childBomLine = bomLine.add(itemRev.getItem(), itemRev, null, false);

					String getQty = nb.getQty();
					if (getQty.contains(".")) {
						childBomLine.setProperty("bl_uom", "Other");
					} else {
						childBomLine.setProperty("bl_uom", "");
					}
					childBomLine.setProperty("bl_quantity", "" + nb.getQty());// 修改
					childBomLine.setProperty("bl_occ_d9_BOMTemp", "" + nb.getD9_BOMTemp());// 修改

					List<PAVariableBOMBean> subsBeans = nb.getSubList();
					if (subsBeans != null) {
						for (PAVariableBOMBean sb : subsBeans) {
							TCComponentItemRevision sbItemRev = (TCComponentItemRevision) session.getComponentManager()
									.getTCComponent(sb.getItemRevUid());
							childBomLine.add(sbItemRev.getItem(), sbItemRev, null, true);
						}
					}
					if (nb.getLevel().intValue() < 2) {
						saveMNTData(childBomLine, nb, seriesMap);
					}

				}
			}
		} else {
			List<PAVariableBOMBean> newChildsBeans = newBean.getChild();
			for (PAVariableBOMBean nb : newChildsBeans) {
				String uid = nb.getItemRevUid();
				TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager()
						.getTCComponent(uid);
				TCComponentBOMLine childBomLine = bomLine.add(itemRev.getItem(), itemRev, null, false);

				String getQty = nb.getQty();
				if (getQty.contains(".")) {
					childBomLine.setProperty("bl_uom", "Other");
				} else {
					childBomLine.setProperty("bl_uom", "");
				}
				childBomLine.setProperty("bl_quantity", "" + nb.getQty());//
				childBomLine.setProperty("bl_occ_d9_BOMTemp", "" + nb.getD9_BOMTemp());// 修改

				List<PAVariableBOMBean> subsBeans = nb.getSubList();
				if (subsBeans != null) {
					for (PAVariableBOMBean sb : subsBeans) {
						TCComponentItemRevision sbItemRev = (TCComponentItemRevision) session.getComponentManager()
								.getTCComponent(sb.getItemRevUid());
						childBomLine.add(sbItemRev.getItem(), sbItemRev, null, true);
					}
				}
				if (nb.getLevel().intValue() < 2) {
					saveMNTData(childBomLine, nb, seriesMap);
				}
			}
		}

	}

	public AjaxResult changeLogInfo(String uid) {
		ArrayList<ChangeLogBean> list = new ArrayList<ChangeLogBean>();
		try {
			TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(uid);

			if (productLineItemRev != null) {

				TCComponent[] relatedComponents = productLineItemRev.getRelatedComponents("d9_MatrixChangeTable");
				for (int i = 0; i < relatedComponents.length; i++) {
					TCComponent tcComponent = relatedComponents[i];
					TCComponentFnd0TableRow row = (TCComponentFnd0TableRow) tcComponent;
					
					String[] proName = new String[] {"d9_ChangeDate","d9_ChangeVer","d9_ChangeLog","d9_ChangeECR","d9_ChangeReqDateUser"};
					loadAllProperties(row, proName);
					
					String[] proValue = row.getProperties(proName);
					
					String changeDate = proValue[0];
					String changeVer = proValue[1];
					String changeLog = proValue[2];
					String changeECR = proValue[3];
					String changeReqDateUser = proValue[4];
					
//					String changeDate = row.getProperty("d9_ChangeDate");
//					String changeVer = row.getProperty("d9_ChangeVer");
//					String changeLog = row.getProperty("d9_ChangeLog");
//					String changeECR = row.getProperty("d9_ChangeECR");
//					String changeReqDateUser = row.getProperty("d9_ChangeReqDateUser");

					ChangeLogBean bean = new ChangeLogBean();
					bean.setChangeDate(changeDate);
					bean.setChangeVer(changeVer);
					bean.setChangeLog(changeLog);
					bean.setChangeECR(changeECR);
					bean.setChangeReqDateUser(changeReqDateUser);

					list.add(bean);
				}
			}

		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AjaxResult ajaxResult = new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", list);

		//String json = JSONObject.toJSONString(ajaxResult);
		//System.out.println("changeLogInfo==>>" + json);

		return ajaxResult;
	}

	/**
	 * 保存ChangeLog数据
	 * 
	 * @param data
	 * @return
	 */
	public AjaxResult saveChangeLog(String data) {
		try {
			Gson gson = new Gson();
			SaveChangeLogBean rootBean = gson.fromJson(data, SaveChangeLogBean.class);
			String uid = rootBean.getUid();
			ArrayList<ChangeLogBean> rootList = rootBean.getChangeLogList();

			TCComponentItemRevision productLineItemRev = (TCComponentItemRevision) session.getComponentManager()
					.getTCComponent(uid);

			if (productLineItemRev != null) {
				productLineItemRev.setRelated("d9_MatrixChangeTable", new TCComponent[] {});

				for (int i = 0; i < rootList.size(); i++) {
					ChangeLogBean bean = rootList.get(i);

					String changeDate = bean.getChangeDate();
					changeDate = getStringLength(changeDate, 32);

					String changeVer = bean.getChangeVer();
					changeVer = getStringLength(changeVer, 16);

					String changeLog = bean.getChangeLog();
					changeLog = getStringLength(changeLog, 1024);

					String changeECR = bean.getChangeECR();
					changeECR = getStringLength(changeECR, 64);

					String changeReqDateUser = bean.getChangeReqDateUser();
					changeReqDateUser = getStringLength(changeReqDateUser, 64);

					TCComponent component = createChangeTable(session, "D9_MatrixChangeTableRow", changeDate, changeVer,
							changeLog, changeECR, changeReqDateUser);
					if (component != null) {
						productLineItemRev.add("d9_MatrixChangeTable", component);
					}
				}
			}

			return AjaxResult.success("保存成功");
		} catch (Exception e) {
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
	}

	private TCComponent createChangeTable(TCSession session, String type, String changeDate, String changeVer,
			String changeLog, String changeECR, String changeReqDateUser) throws ServiceException {
		DataManagementService dmService = DataManagementService.getService(session);
		CreateIn[] createIn = new CreateIn[1];
		createIn[0] = new CreateIn();
		createIn[0].data.boName = type;
		createIn[0].data.stringProps.put("d9_ChangeDate", changeDate);
		createIn[0].data.stringProps.put("d9_ChangeVer", changeVer);
		createIn[0].data.stringProps.put("d9_ChangeLog", changeLog);
		createIn[0].data.stringProps.put("d9_ChangeECR", changeECR);
		createIn[0].data.stringProps.put("d9_ChangeReqDateUser", changeReqDateUser);

		CreateResponse createObjects = dmService.createObjects(createIn);
		int sizeOfPartialErrors = createObjects.serviceData.sizeOfPartialErrors();
		for (int i = 0; i < sizeOfPartialErrors; i++) {
			ErrorStack partialError = createObjects.serviceData.getPartialError(i);
			String[] messages = partialError.getMessages();
			if (messages != null && messages.length > 0) {
				StringBuffer sBuffe = new StringBuffer();
				for (String string : messages) {
					sBuffe.append(string);
				}
				throw new ServiceException(sBuffe.toString());
			}

		}
		if (createObjects.output.length > 0) {
			return createObjects.output[0].objects[0];
		}

		return null;
	}

	private String getStringLength(String str, int len) throws UnsupportedEncodingException {
		int length = 0;
		int n = 0;
		if (str != null && str.length() > 0) {
			for (int i = 1; i <= str.length(); i++) {
				String byteString = str.substring(n, i);
				byte[] bytes = byteString.getBytes("UTF-8");
				int bytesl = bytes.length;
				length += bytesl;
				if (length >= len) {
					return str.substring(0, i - 1);
				}
				n = i;
			}
		}
		return str;
	}
	
	
	/**
	 * 获取系列对象版本
	 * 
	 * @param productLineItemRev
	 * @return
	 * @throws TCException
	 */
	private TCComponentItemRevision getSeriesItemRev(TCComponentItemRevision productLineItemRev) throws TCException {
		TCComponent[] relatedComponents = productLineItemRev
				.getRelatedComponents(MatrixBOMConstant.D9_HASVARIANTHOLDER_REL);
		if (ArrayUtil.isEmpty(relatedComponents)) {
			return null;
		}

		List<TCComponent> list = Convert.convert(new TypeReference<List<TCComponent>>() {
		}, relatedComponents);
		Optional<TCComponent> findAny = list.stream().filter(e -> {
			try {
				if (e instanceof TCComponentItemRevision) {
					return true;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return false;
		}).findAny();

		if (findAny.isPresent()) {
			return (TCComponentItemRevision) findAny.get();
		}

		return null;
	}
	
	
	public static void loadAllProperties(TCComponentBOMLine bomLine, TCComponentItemRevision itemRev, String[] partAtrrs, String[] bomAttrs)
			throws TCException, InterruptedException {
		com.teamcenter.services.strong.core.DataManagementService dmService = com.teamcenter.services.strong.core.DataManagementService
				.getService(session.getSoaConnection());
		//Set<TCComponentBOMLine> boms = Collections.synchronizedSet(new HashSet<TCComponentBOMLine>());
		//Set<TCComponentItemRevision> parts = Collections.synchronizedSet(new HashSet<TCComponentItemRevision>());
		if(bomLine!=null && bomAttrs !=null && bomAttrs.length > 0) {
//			Thread bomThread = new Thread(() -> {
				dmService.getProperties(new TCComponentBOMLine[] {bomLine}, bomAttrs);
//			});
//			bomThread.start();
//			bomThread.join();
		}
		
		if(itemRev!=null && partAtrrs !=null && partAtrrs.length > 0) {
//			Thread partThread = new Thread(() -> {
				dmService.getProperties(new TCComponentItemRevision[] {itemRev}, partAtrrs);
//			});
//			partThread.start();
//			partThread.join();
		}
	}
	
	
	public static void loadAllProperties(TCComponentItemRevision itemRev, String[] partAtrrs)
			throws TCException, InterruptedException {
		com.teamcenter.services.strong.core.DataManagementService dmService = com.teamcenter.services.strong.core.DataManagementService
				.getService(session.getSoaConnection());
		//Set<TCComponentItemRevision> parts = Collections.synchronizedSet(new HashSet<TCComponentItemRevision>());
		//getAllPartBom(bomLine, boms, parts);
		
		if(partAtrrs !=null && partAtrrs.length > 0) {
//			Thread partThread = new Thread(() -> {
				dmService.getProperties(new TCComponentItemRevision[] {itemRev}, partAtrrs);
//			});
//			partThread.start();
//			partThread.join();
		}
	}
	
	public static void loadAllProperties(ModelObject object, String[] pro)
			throws TCException, InterruptedException {
		com.teamcenter.services.strong.core.DataManagementService dmService = com.teamcenter.services.strong.core.DataManagementService
				.getService(session.getSoaConnection());
		if(pro !=null && pro.length > 0) {
				dmService.getProperties(new ModelObject[] {object}, pro);
		}
	}
	
	
	public static TCComponentItem createItem(TCSession session, String itemId, String itemRev, String itemName,
			String itemTypeName) throws TCException {
		TCComponentItem item = null;
		TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
		if (itemId.equals("")) {
			itemId = itemType.getNewID();
		}
		if (itemRev == null || itemRev.equals("")) {
			itemRev = itemType.getNewRev(null);
		}
		item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
		return item;
	}
}
