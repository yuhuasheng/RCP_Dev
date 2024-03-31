package com.foxconn.electronics.login;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.IPerspectiveDefService;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.navigationpane.ITaskPaneSection;
import com.teamcenter.rac.aif.navigationpane.SecondaryPopupShell;
import com.teamcenter.rac.aif.navigationpane.impl.OpenObjectInAppExtPointmanager;
import com.teamcenter.rac.aif.navigationpane.impl.SectionComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.providers.TCComponentContentProvider;
import com.teamcenter.rac.providers.TCComponentLabelProvider;
import com.teamcenter.rac.providers.node.ComponentRootNode;
import com.teamcenter.rac.util.AdapterUtil;
import com.teamcenter.rac.util.PlatformHelper;
import com.teamcenter.rac.util.log.TcLogger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

public class MyHomeSectionComponent extends SectionComponent {
	
    public MyHomeSectionComponent() {
    }

    public void createPartControl(ITaskPaneSection var1) {
        super.createPartControl(var1);
        AbstractAIFSession var2 = Activator.getDefault().getSessionService().getActivePerspectiveSession();
        this.setTCComponent(this.getUserHomeFolder(var2));
    }

    public void executeAction(ActionEvent var1) {
        String var2 = AIFUtility.getCurrentApplication().getApplicationId();
        String var3 = null;
        if (this.getTCComponent() != null) {
            var3 = this.getTCComponent().getType();
        }

        boolean var4 = OpenObjectInAppExtPointmanager.getInstance().tcOpenInCurrentPerspective(var3, var2);
        if (!var4) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                	MyHomeSectionComponent.this.postDefaultPerspective();
                }
            });
        }

        if (this.getTCComponent() == null || this.getTCComponent() != this.getUserHomeFolder(Activator.getDefault().getSessionService().getActivePerspectiveSession())) {
            this.setTCComponent(this.getUserHomeFolder(Activator.getDefault().getSessionService().getActivePerspectiveSession()));
        }

        if (this.getTCComponent() != null) {
            super.openComponent(this.getTCComponent());
        }

    }

    private TCComponent getUserHomeFolder(AbstractAIFSession var1) {
        TCComponent homeComponent = null;
        if (var1 instanceof TCSession && var1.isLoggedIn()) {
            TCSession var3 = (TCSession)var1;
            TCUtil.setBypass(var3);
            try {
                // 添加當前二級賬號的home文件夾
    			OSSUserPojo userPojo = UserLoginSecond.getOSSUserInfo();
    			// 加載二級賬號的uid獲取二級賬號的home文件夾名稱
    			if(ObjUtil.isNotNull(userPojo)) {
    				String item_id = userPojo.getEmp_no();
    				// 查询二级账号
    				TCComponent userComponent = null;
    				TCComponent[] executeQuery = TCUtil.executeQuery(var3, "__D9_Find_Actual_User", new String[] {"item_id"},
    						new String[] {item_id});
    				if (executeQuery != null && executeQuery.length > 0) {
    					userComponent = executeQuery[0];
    				} else {
    					throw new TCException("未查詢到實際工作者對象");
    				}
    				String itemId = userComponent.getProperty("item_id");
					String name = userComponent.getProperty("object_name");
					String folderName= name + "(" + itemId + ")";
					executeQuery = TCUtil.executeQuery(var3, "常规...",
							new String[] {"object_type","object_name"}, new String[] {"D9_UserFolder" , folderName});
					if (executeQuery != null && executeQuery.length > 0) {
						homeComponent = executeQuery[0];
					} else {
						// 創建myHome文件夾並關聯部門文件夾
						homeComponent = TCUtil.createFolder(var3, "D9_UserFolder", folderName);
					}
					// 查詢首選項獲取部門文件夾uid
					List<String> list = TCUtil.getArrayPreference(var3,TCPreferenceService.TC_preference_site,"D9_SU_Function_Folder_Mapping");
					Map<String,String> map = new HashMap<String, String>();
					for (String item : list) {
						List<String> split = StrSplitter.split(item, ":",true,true);
						if(split.size() == 2) {
							map.put(split.get(0).substring(1,split.get(0).length()-1), split.get(1).substring(1,split.get(1).length()-1));
						}
					}
					// 判斷當前部門是否在下面
					if(StrUtil.isNotBlank(map.get(userPojo.getDept()))) {
						Set<String>  set= new HashSet<String>();
						TCComponent[] components = homeComponent.getRelatedComponents("contents");
						if(components.length > 0) {
							for (int i = 0; i < components.length; i++) {
								set.add(components[i].getUid());
							}
						}
						if("DT+RD+ME".equals(userPojo.getDept()) || "DT+RD+ME PM".equals(userPojo.getDept())) {
							if(!set.contains(map.get("DT+RD+ME"))) {
								TCComponent meFolder = TCUtil.loadObjectByUid(map.get("DT+RD+ME"));
								homeComponent.add("contents", meFolder);
							}
							if(!set.contains(map.get("DT+RD+ME PM"))) {
								TCComponent mePmFolder = TCUtil.loadObjectByUid(map.get("DT+RD+ME PM"));
								homeComponent.add("contents", mePmFolder);
							}
						} else {
							if(!set.contains(map.get(userPojo.getDept()))) {
								TCComponent deptFolder = TCUtil.loadObjectByUid(map.get(userPojo.getDept()));
								homeComponent.add("contents", deptFolder);
							}
						}
					}
					TCComponent[] relatedComponents = homeComponent.getRelatedComponents("owning_user");
					if(!relatedComponents[0].getUid().equals(var3.getUser().getUid())) {
						homeComponent.changeOwner(var3.getUser(), var3.getGroup());
					}
    			}else {
    				throw new TCException("未查詢到二級用戶");
    			}
                TcLogger.getLogger(MyHomeSectionComponent.class).info("--------------------------End------------------------");
            } catch (final Exception var5) {
                TcLogger.getLogger(MyHomeSectionComponent.class).error("Unable to get user's home folder", var5);
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", var5.getLocalizedMessage());
                    }
                });
            } finally {
            	TCUtil.closeBypass(var3);
			}
        }

        return homeComponent;
    }

    public SecondaryPopupShell createSecondaryPopupShell(Object var1) {
        SecondaryPopupShell var2 = super.createSecondaryPopupShell(var1);
        return var2;
    }

    public void loadSecondaryPopupShellContent() {
        AbstractAIFSession var1 = Activator.getDefault().getSessionService().getActivePerspectiveSession();
        TCComponent var2 = this.getUserHomeFolder(var1);
        Composite var3 = this.secondaryPopupShell.getContentComposite();
        var3.setLayout(new FillLayout());
//        TreeViewer var4 = new TreeViewer(var3, 768);
//        var4.setLabelProvider(new TCComponentLabelProvider());
//        var4.setContentProvider(new TCComponentContentProvider());
//        var4.addSelectionChangedListener(new ISelectionChangedListener() {
//            public void selectionChanged(SelectionChangedEvent var1) {
//                if (!var1.getSelection().isEmpty()) {
//                    if (var1.getSelection() instanceof IStructuredSelection) {
//                        IStructuredSelection var2 = (IStructuredSelection)var1.getSelection();
//                        TreeViewer var3 = (TreeViewer)var1.getSource();
//                        Tree var4 = var3.getTree();
//                        Object var5 = var4.getItem(0).getData();
//                        Iterator var6 = var2.iterator();
//
//                        while(var6.hasNext()) {
//                            Object var7 = var6.next();
//                            if (var7.equals(var5)) {
//                                return;
//                            }
//
//                            TCComponent var8 = (TCComponent)AdapterUtil.getAdapter(var7, TCComponent.class);
//                            if (var8 == null) {
//                                return;
//                            }
//
//                            MyHomeSectionComponent.this.postDefaultPerspective();
//                            MyHomeSectionComponent.super.openComponent(var8);
//                            MyHomeSectionComponent.this.secondaryPopupShell.close();
//                        }
//                    }
//
//                }
//            }
//        });
//        var4.setInput(new ComponentRootNode(var2));
//        var4.expandToLevel(2);
        this.secondaryPopupShell.reload();
    }

    public void postPerspective(String var1) {
        IPerspectiveDefService var2 = this.getPerspectiveDefService();
        if (!var2.isTypeOfPerspectiveActive(var1)) {
            PlatformHelper.showPerspective(var1);
        }

    }
    
}
