package com.foxconn.mechanism.integrate.mdas;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.HttpUtil;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MSplitPane;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.favorites.G4MFavoritesSplitPane;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSHierarchyNodeDescriptor;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSSearchResult;
import com.teamcenter.rac.kernel.ics.ICSTableICOWrapper;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

/**
 * @author wt0010
 *  mdas 集成action 类 
 */
public class SynMDASAction extends AbstractAIFAction {
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;

	public SynMDASAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) this.app.getSession();
		reg = Registry.getRegistry("com.foxconn.mechanism.integrate.mdas.sysnmdas");
	}

	@Override
	public void run() {
		try {
			String bu = "";
			String category = "";
			String type = "";
			//分类属性 subType
			String subType = "";
			//同步状态
			String ecFlag = "";
			//客户
			String vendor="";
			//线圈圈数
			String threadSize="";
			//线圈长度
			String threadLen="";
			JPanel applicationPanel = this.app.getApplicationPanel();
			G4MSplitPane g4MTabbedPane = (G4MSplitPane) applicationPanel.getComponents()[0];
			G4MFavoritesSplitPane l = (G4MFavoritesSplitPane) g4MTabbedPane.getLeftComponent();
			G4MTree t4 = (G4MTree) l.getLeftComponent();
			G4MTreeNode sn = t4.getSelectedNode();
			if (sn == null) {
				throw new Exception(reg.getString("nonselect.Err"));
			}
			ICSHierarchyNodeDescriptor icsd = sn.getICSDescriptor();
			int count = icsd.getClassChildrenCount();
			if (count > 0) {
				throw new Exception(reg.getString("selectleaf.Info"));
			}
			String strClassId = icsd.getId();
			category = icsd.getName();
			//分类属性别名
			String[] aliasNames= icsd.getAliasNames();
			if(aliasNames!=null&&aliasNames.length>0) {
				if(!"".equalsIgnoreCase(aliasNames[0].trim())) {
					category=aliasNames[0];
				}
			}
			TreeNode[] path = sn.getPath();
			G4MTreeNode buNode = (G4MTreeNode) path[2];
			//获取当前选中的分类节点
			ICSHierarchyNodeDescriptor buicsd = buNode.getICSDescriptor();
            //目前只支持DT
			if (buicsd.getName().equalsIgnoreCase("MNT") || buicsd.getName().equalsIgnoreCase("PRT")) {
				throw new Exception(reg.getString("nonSurpportNode.Err"));
			}
			bu = "DT";
			int level = path.length;
			if (level == 5) {
				G4MTreeNode node = (G4MTreeNode) path[3];
				ICSHierarchyNodeDescriptor nodeics = node.getICSDescriptor();
				type = category;
				category = nodeics.getName();
				//分类属性别名
				aliasNames= nodeics.getAliasNames();
				if(aliasNames!=null&&aliasNames.length>0) {
					if(!"".equalsIgnoreCase(aliasNames[0].trim())) {
						category=aliasNames[0];
					}
				}				
			}

			TCClassificationService tccs = session.getClassificationService();
             //查询分类节点里的所有标准件
			ICSProperty[] arrayOfICSProperty = new ICSProperty[1];
			arrayOfICSProperty[0] = new ICSProperty(-630, "0");
			ICSSearchResult[] icssResults = tccs.searchICOs(strClassId, arrayOfICSProperty, 0);
			List<MdasPojo> mdasPojos = new ArrayList<>();

			if ((icssResults == null) || (icssResults.length <= 0)) {
				return;
			}
			for (ICSSearchResult icssResult : icssResults) {
				if (icssResult.getWsoUid().trim().equals("")) {
					continue;
				}
				if (level == 4) {
					type = "";
				}
				subType = "";
				ecFlag = "";
				vendor="";
				threadSize="";
				threadLen="";
				ICSTableICOWrapper[] wps = tccs.loadICOs(new String[] { icssResult.getIcoUid() }, strClassId);
				if (wps == null || wps.length == 0) {
					continue;
				}
				ICSTableICOWrapper wp = wps[0];
				TCComponentICO tcIco = wp.getTCComponentICO();
				if (tcIco == null) {
					continue;
				}
				//获取分类属性
				ICSProperty[] pss = tcIco.getICSProperties(true);
				for (ICSProperty p : pss) {
					int id = p.getId();
					//subType
					if (id == 40064 || id == 40065 || id == 40069 || id == 40070) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							subType = p.getValue();
						}
					}
					//同步状态
					if (id == 40063) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							ecFlag = p.getValue();
							if ("key01".equalsIgnoreCase(ecFlag)) {
								ecFlag = "1";// 删除
							} else if ("key03".equalsIgnoreCase(ecFlag)) {
								ecFlag = "0";// 未同步
							} else if ("key00".equalsIgnoreCase(ecFlag)) {
								ecFlag = "0";// 未同步
							}  else {
								ecFlag = "";
							}
						}
					}
					
					//获取分类属性 type 的值
					if (id == 40051 || id == 40066 || id == 40052 || id == 40067 || id == 40068) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							type = p.getValue();
						}
					}
					//客户分类属性
					if(id==40050) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							 vendor = p.getValue();						 
						}
					}
				
					//线圈圈数
					if(id==40071) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							threadSize = p.getValue();
						}
					}
				
					//线圈长度
					if(id==40072) {
						if (p.getValue() != null && !"".equalsIgnoreCase(p.getValue())) {
							threadLen = p.getValue();
						}
					}
					
				}
				if ("".equalsIgnoreCase(ecFlag)) {
					continue;
				}
				//得到版本对象
				TCComponentItemRevision itemRev = null;
				TCComponent wsoid = tccs.getTCComponent(icssResult.getWsoUid());
				// System.out.print(c.getTypeObject().getName());
				if (wsoid instanceof TCComponentItemRevision) {
					itemRev = (TCComponentItemRevision) wsoid;
				} else {
					TCComponent[] revions = wsoid.getRelatedComponents("revision_list");
					if (revions != null && revions.length > 0) {
						itemRev = (TCComponentItemRevision) revions[revions.length - 1];
					}
				}
				if (itemRev == null) {
					continue;
				}
				String part = "";
				String pic = "";
				String doc = "";
				String file = "";
				
				TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
				for (TCComponent tcComponent : relatedComponents) {
					if (!(tcComponent instanceof TCComponentDataset)) {
						continue;
					}
					TCComponentDataset dataset = (TCComponentDataset) tcComponent;
					dataset.refresh();
					String objectType = dataset.getTypeObject().getName();
					//获取prt dataset
					if ("ProPrt".equalsIgnoreCase(objectType)) {					
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".prt")) {
								part = f.getUid();
							}
						}
					}
                    //获取 bmp dataset
					if ("Bitmap".equalsIgnoreCase(objectType)) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".bmp")) {
								pic = f.getUid();
							}
						}
					}
                     //获取gph dataset
					if ("ProGph".equalsIgnoreCase(objectType)) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".gph")) {
								file = f.getUid();
							}
						}
					}
                      //获取pdf dataset
					if ("PDF".equalsIgnoreCase(objectType)) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".pdf")) {
								doc = f.getUid();
							}
						}
					}
				}
				
				
				relatedComponents = itemRev.getRelatedComponents("IMAN_Rendering");
				for (TCComponent tcComponent : relatedComponents) {
					if (!(tcComponent instanceof TCComponentDataset)) {
						continue;
					}
					TCComponentDataset dataset = (TCComponentDataset) tcComponent;
					dataset.refresh();
					String objectType = dataset.getTypeObject().getName();
					//获取prt dataset
					if ("ProPrt".equalsIgnoreCase(objectType)&&(part==null||"".equalsIgnoreCase(part))) {					
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".prt")) {								
								part = f.getUid();
							}
						}
					}
                    //获取 bmp dataset
					if ("Bitmap".equalsIgnoreCase(objectType)&&(pic==null||"".equalsIgnoreCase(pic))) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".bmp")) {
								pic = f.getUid();
							}
						}
					}
                     //获取gph dataset
					if ("ProGph".equalsIgnoreCase(objectType)&&(file==null||"".equalsIgnoreCase(file))) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".gph")) {
								file = f.getUid();
							}
						}
					}
                      //获取pdf dataset
					if ("PDF".equalsIgnoreCase(objectType)&&(doc==null||"".equalsIgnoreCase(doc))) {
						TCComponentTcFile[] files = dataset.getTcFiles();
						if (files == null || files.length == 0) {
							continue;
						}
						for (TCComponentTcFile f : files) {
							String fileName = f.getProperty("original_file_name");
							if (fileName.toLowerCase().contains(".pdf")) {
								doc = f.getUid();
							}
						}
					}
				}
				
				
				String itemId=itemRev.getProperty("item_id");
				MdasPojo mdasPojo = new MdasPojo();
				mdasPojo.setBu(bu);
				mdasPojo.setCategory(category);
				mdasPojo.setType(type);
				mdasPojo.setSubType(subType);
				mdasPojo.setPart(part);
				mdasPojo.setPic(pic);
				mdasPojo.setDoc(doc);
				mdasPojo.setFile(file);
				mdasPojo.setEcFlag(ecFlag);
				mdasPojo.setItemId(itemId);
				mdasPojo.setVendor(vendor);
				mdasPojo.setThreadSize(threadSize);
				mdasPojo.setThreadLen(threadLen);
				mdasPojos.add(mdasPojo);
			}
			
			String jsons = JSONArray.toJSONString(mdasPojos);
			System.out.println(jsons);
			//读取首选项，获取mdas集成接口url
            String url=TCUtil.getPreference(this.session, TCPreferenceService.TC_preference_site, "D9_Cust_MDAS_URL");
			System.out.print(url);
		    if(url==null||"".equalsIgnoreCase(url)) {
		    	throw new Exception(reg.getString("nonPreference.Err"));
		    }
		    //调用接口，获取返回结果
			String rs=HttpUtil.post(url, jsons);
		    System.out.print(rs);
			JSONObject rsObj=JSON.parseObject(rs);
			if(!(rsObj.getString(AjaxResult.CODE_TAG).equalsIgnoreCase(AjaxResult.STATUS_SUCCESS))) {
				throw new Exception(rsObj.getString("msg"));
			}
			TCUtil.setBypass(this.session);
			//更新状态到分类属性上
			G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(AIFUtility.getCurrentApplication(), strClassId);
			ICSApplicationObject icsAppObj = g4mUserAppContext.getICSApplicationObject();
			icsAppObj.setView(strClassId);
			for (ICSSearchResult icssResult : icssResults) {
				ICSTableICOWrapper[] wps=tccs.loadICOs(new String[] {icssResult.getIcoUid()}, strClassId);
				TCComponentICO ico= wps[0].getTCComponentICO();
				ICSProperty[] ps= ico.getICSProperties(true);
				List<ICSProperty> iccs=	new ArrayList<ICSProperty>();	
				for(ICSProperty p:ps) {
				   //System.out.print(p.getId());
				   if(p.getId()==40063) {
				    	String v=p.getValue();			    	
				    	if("key01".equalsIgnoreCase(v)) {
				    	  p.setValue("key02");//已同步
				    	}
				    }
				   if(p.getId()!=40053) {
				        iccs.add(p);
				   }
				 }
			
				//保存分类属性 			
				 icsAppObj.load(icssResult.getIcoUid());	
				 icsAppObj.setProperties(iccs.toArray(new ICSProperty[iccs.size()]));			
				 icsAppObj.save();
			}
			
			MessageBox.post(AIFUtility.getActiveDesktop(),reg.getString("susccess.Info"), "Success",MessageBox.INFORMATION);
			TCUtil.closeBypass(this.session);
		} catch (Exception e) {
			try {
			   TCUtil.closeBypass(this.session);
			}catch(Exception e0 ) {}
			System.out.print(e.getMessage());
			MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Warning",MessageBox.WARNING);
		}
	}

}
