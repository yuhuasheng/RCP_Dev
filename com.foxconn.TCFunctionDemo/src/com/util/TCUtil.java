package com.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentParticipant;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.Type;

import cn.hutool.core.util.StrUtil;

public class TCUtil
{
    private static TCComponentItemRevisionType itemRevType   = null;
    private static TCComponentDatasetType      datasetType   = null;
    private static TCComponentItemType         itemType;
    private static TCTextService               tcTextService = null;
    private static final String                D9_PREFIX     = "d9_";
    private static final String                POC_STR       = "IR_";

    public static AIFDesktop getDesktop()
    {
        return AIFDesktop.getActiveDesktop();
    }

    public static void centerShell(Shell shell)
    {
        int width = shell.getMonitor().getClientArea().width;
        int height = shell.getMonitor().getClientArea().height;
        int x = shell.getSize().x;
        int y = shell.getSize().y;
        if (x > width)
        {
            shell.getSize().x = width;
        }
        if (y > height)
        {
            shell.getSize().y = height;
        }
        shell.setLocation((width - x) / 2, (height - y) / 2);
    }

    /**
     * 打开文件选择器
     * 
     * @return 文件路径
     */
    public static String openFileChooser(Shell shell)
    {
        FileDialog fileDialog = new FileDialog(shell);
        fileDialog.setFilterPath(getSystemDesktop());
        fileDialog.setFilterNames(new String[] { "Microsoft Excel(*.xlsx)", "Microsoft Excel(*.xls)" });
        fileDialog.setFilterExtensions(new String[] { "*.xlsx", "*.xls" });
        return fileDialog.open();
    }

    
    public static String openCADFileChooser(Shell shell) {
    	FileDialog fileDialog = new FileDialog(shell);
    	fileDialog.setFilterPath(getSystemDesktop());
    	fileDialog.setFilterNames(new String[] {"CAD文件(*.dwg)", "CAD图形文件(*.dxf)"});
    	fileDialog.setFilterExtensions(new String[] {"*.dwg", "*.dxf"});
    	return fileDialog.open();
    }
    
    
    /**
	 * 查找数据集
	 * 
	 * @param datasetName 数据集名称
	 * @return
	 * @throws TCException
	 */
	public static TCComponentDataset findDataset(String datasetName) throws TCException {
		if (null == datasetType) {
			datasetType = (TCComponentDatasetType) getTCSession().getTypeComponent(ITypeName.Dataset);
		}
		TCComponentDataset dataset = datasetType.find(datasetName);
		if (null == dataset) {
			return null;
		}
		return dataset;
	}
    /**
	 * 文件选择器
	 * @param fileName
	 * @return
	 */
	public static File openFileChooser(String fileName) {
		JFileChooser jFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
		jFileChooser.setFileFilter(filter);
		jFileChooser.setSelectedFile(new File(fileName + ".xlsx"));
		int openDialog = jFileChooser.showSaveDialog(null);
		if (openDialog == JFileChooser.APPROVE_OPTION) {
			File file = jFileChooser.getSelectedFile();
			String fname = jFileChooser.getName(file);
			if (fname.indexOf(".xlsx") == -1) {
				file = new File(jFileChooser.getCurrentDirectory(), fname + ".xlsx");
			}
			return file;
		}
		return null;
	}
	
    /**
     * 获取系统桌面路径
     * 
     * @return
     */
    public static String getSystemDesktop()
    {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        return desktopDir.getAbsolutePath();
    }

    public static TCSession getTCSession()
    {
        return RACUIUtil.getTCSession();
    }

    /**
     * 弹出消息框
     * 
     * @return
     */
    public static void infoMsgBox(String info, String title)
    {
        if (StrUtil.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "弹出消息框", MessageBox.INFORMATION);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.INFORMATION);
        }
    }

    public static void warningMsgBox(String info, String title)
    {
        if (StrUtil.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "警告", MessageBox.WARNING);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.WARNING);
        }
    }

    public static void errorMsgBox(String info, String title)
    {
        if (StrUtil.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "错误", MessageBox.ERROR);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.ERROR);
        }
    }

    
    public static void writeInfoLog(String message, StyledText styledText, StyleRange infoStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("INFO")) {
						msg = "【INFO】 " + message;
					} else {
						msg = message;
					}					
					styledText.append(msg + "\n");
					
					infoStyle.start = styledText.getCharCount() - msg.length() - 1;
					infoStyle.length = msg.length();
					styledText.setStyleRange(infoStyle);
				}
			}
		});
    }
    
    
    public static void writeWarnLog(String message, StyledText styledText, StyleRange warnStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("WARN")) {
						msg = "【WARN】 " + message;
					} else {
						msg = message;
					}
					styledText.append(msg + "\n");
					
					warnStyle.start = styledText.getCharCount() - msg.length() - 1;
					warnStyle.length = msg.length();
					styledText.setStyleRange(warnStyle);
				}
			}
		});
    }
    
    
    public static void writeErrorLog(String message, StyledText styledText, StyleRange errorStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("ERROR")) {
						msg = "【ERROR】 " + message;
					} else {
						msg = message;
					}					
					styledText.append(msg + "\n");
					
					errorStyle.start = styledText.getCharCount() - msg.length() - 1;
					errorStyle.length = msg.length();
					styledText.setStyleRange(errorStyle);
				}
			}
		});
    }
    
  

    /**
     * 创建Item
     * 
     * @param session
     * @param itemId
     * @param itemRev
     * @param itemName
     * @param itemTypeName
     * @return
     */
    public static TCComponentItem create2Item(TCSession session, String itemId, String itemRev, String itemName, String itemTypeName)
    {
        TCComponentItem item = null;
        try
        {
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            if (itemId.equals(""))
            {
                itemId = itemType.getNewID();
            }
            if (itemRev.equals(""))
            {
                itemRev = itemType.getNewRev(null);
            }
            item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return item;
    }


    /**
     * @param itemRev 对象版本
     * @return 返回升版之后的对象版本
     * @throws Exception *
     */
    public static TCComponentItemRevision doRevise(TCComponentItemRevision itemRev) throws Exception
    {
        System.out.println("doRevise");
        boolean isModify = false;
        String item_id = itemRev.getProperty("item_id");
        String itemRevId = itemRev.getProperty("item_revision_id");
        String object_name = itemRev.getProperty("object_name");
        System.out.println("itemRevId == " + itemRevId);
        try
        {
            isModify = itemRev.getItem().okToModify();
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        if (isModify)
        {
            DeepCopyInfo copyInfo = new DeepCopyInfo(itemRev, DeepCopyInfo.COPY_AS_OBJECT_ACTION, "", null, true, true, true);
            DeepCopyInfo[] copyInfos = new DeepCopyInfo[1];
            copyInfos[0] = copyInfo;
            TCComponentItemRevisionType itemRevType = (TCComponentItemRevisionType) itemRev.getTypeComponent();
            // String newRevId = itemRevType.getNewRevAlt(itemRev,
            // itemRevId);
            // String newRevId = itemRev.getNewRevAlt(itemRevId);
            String newRevId = itemRevType.getItemType().getNewRev(itemRev.getItem());
            System.out.println("newRevId == " + newRevId);
            TCComponentItemRevision newItemRev = itemRev.saveAs(newRevId, object_name, "", true, copyInfos);
            System.out.println("newItemRev == " + newItemRev);
            return newItemRev;
        }
        else
        {
            throw new Exception("權限不夠");
        }
    }

    /**
     * @param itemRev 对象版本
     * @param newRevId 版本号
     * @return
     * @throws Exception
     * 
     */
    public static TCComponentItemRevision doRevise(TCComponentItemRevision itemRev, String newRevId) throws Exception
    {
        System.out.println("doRevise");
        boolean isModify = false;
        String itemRevId = itemRev.getProperty("item_revision_id");
        String object_name = itemRev.getProperty("object_name");
        System.out.println("itemRevId == " + itemRevId);
        try
        {
            isModify = itemRev.getItem().okToModify();
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        if (isModify)
        {
            DeepCopyInfo copyInfo = new DeepCopyInfo(itemRev, DeepCopyInfo.COPY_AS_OBJECT_ACTION, "", null, true, true, true);
            DeepCopyInfo[] copyInfos = new DeepCopyInfo[1];
            copyInfos[0] = copyInfo;
            System.out.println("newRevId == " + newRevId);
            TCComponentItemRevision newItemRev = itemRev.saveAs(newRevId, object_name, "", true, copyInfos);
            System.out.println("newItemRev == " + newItemRev);
            return newItemRev;
        }
        else
        {
            throw new Exception("權限不夠");
        }
    }

    /**
     * 獲取靜態LOV 根据LOV名称获取值列表
     * 
     * @param lovName
     * @return
     */
    public static Object[] getLovValues(String lovName)
    {
        TCComponentListOfValues lov = TCComponentListOfValuesType.findLOVByName(getTCSession(), lovName);
        if (lov == null)
        {
            return new String[0];
        }
        try
        {
            Object[] objs = lov.getListOfValues().getListOfValues();
            return objs;
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return new String[0];
    }

    /**
     * 获取数组类型首选项值
     * 
     * @param session
     * @param scope 首选项级别
     * @param preferenceName 首选项名称
     * @return
     */
    @SuppressWarnings("deprecation")
    public static List<String> getArrayPreference(TCSession session, int scope, String preferenceName)
    {
        try
        {
            TCPreferenceService tCPreferenceService = session.getPreferenceService();
            tCPreferenceService.refresh();
            String[] array = tCPreferenceService.getStringArray(scope, preferenceName);
            return new ArrayList<String>(Arrays.asList(array));
//            return Arrays.asList(array);
        }
        catch (Exception e)
        {
            System.out.print(e);
        }
        return null;
    }

    /**
     * 获取字符串类型首选项值
     * 
     * @param session
     * @param scope
     * @param preferenceName
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getPreference(TCSession session, int scope, String preferenceName)
    {
        try
        {
            TCPreferenceService tCPreferenceService = session.getPreferenceService();
            tCPreferenceService.refresh();
            return tCPreferenceService.getString(scope, preferenceName);
        }
        catch (Exception e)
        {
            System.out.print(e);
        }
        return null;
    }

    /**
     * 
     * @param session
     * @param selectFilepath
     * @param dsType
     * @param dsName
     * @param ref_type
     * @return TCComponentDataset
     * @throws TCException
     */
    public static TCComponentDataset createDataSet(TCSession session, String selectFilepath, String dsType, String dsName, String ref_type) throws TCException
    {
        TCComponentDataset dataset = null;
        TCTypeService type = session.getTypeService();
        TCComponentDatasetType datasettype = (TCComponentDatasetType) type.getTypeComponent("Dataset");
        dataset = datasettype.create(dsName, "", dsType);
        String p[] = new String[1];
        String n[] = new String[1];
        p[0] = selectFilepath;
        // System.out.println("selectFilepath == " + selectFilepath);
        n[0] = ref_type;
        // System.out.println("ref_type == " + ref_type);
        dataset.setFiles(p, n);
        return dataset;
    }

    
    public static void updateDataset(TCSession session, TCComponent itemComp, String relName, String fileFullPath) throws TCException {
		try {
//			session.enableBypass(true);
			setBypass(session); // 开启旁路
			TCComponentItemRevision itemRev = (TCComponentItemRevision) itemComp;
			TCComponentDataset findDataSet = null;
			
			TCComponent[] datesets = itemRev.getRelatedComponents(relName);
			for (TCComponent tcComponent : datesets) {
				if (tcComponent instanceof TCComponentDataset) {
					findDataSet = (TCComponentDataset) tcComponent;
					break;
				}
			}
			
			//TCComponentDataset findDataSet = TCUtil.findDataSet((TCComponentItemRevision) itemComp, relName, fileFullPath.split("\\\\")[fileFullPath.split("\\\\").length - 1]);
			if (findDataSet != null) {
				updateDataset(session, findDataSet, fileFullPath);
			}
		} finally {
//			session.enableBypass(false);
			closeBypass(session); // 关闭旁路
		}
	}
	
	public static void updateDataset(TCSession session, TCComponentDataset dataset, String filePath) {
		try {
			TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session.getTypeComponent("DatasetType");
			TCComponentDatasetDefinition definition = dsdefType.find(dataset.getType());
			NamedReferenceContext[] contexts = definition.getNamedReferenceContexts();
			String namedReference = contexts[0].getNamedReference();
			dataset.removeNamedReference(namedReference);

			dataset.setFiles(new String[] { filePath }, new String[] { namedReference });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
    /**
     * 
     * @param itemRev
     * @param findFileName
     * @return TCComponentDataset
     * @throws TCException
     */
    public static TCComponentDataset findDataSet(TCComponentItemRevision itemRev, String findFileName) throws TCException
    {
        TCComponent[] datesets = itemRev.getRelatedComponents("IMAN_specification");
        for (TCComponent tcComponent : datesets)
        {
            if (tcComponent instanceof TCComponentDataset)
            {
                TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
                for (String fileName : tcComponentDataset.getFileNames(null))
                {
                    if (fileName.equalsIgnoreCase(findFileName))
                    {
                        return tcComponentDataset;
                    }
                }
            }
        }
        return null;
    }

    
    public static TCComponentDataset findDataSet(TCComponentItemRevision itemRev, String relName, String findFileName)
			throws TCException {
		TCComponent[] datesets = itemRev.getRelatedComponents(relName);
		for (TCComponent tcComponent : datesets) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
				for (String fileName : tcComponentDataset.getFileNames(null)) {
					if (fileName.equalsIgnoreCase(findFileName)) {
						return tcComponentDataset;
					}
				}
			}
		}
		return null;
	}
    
    public static boolean isReleased(TCComponent com)
    {
        if (com == null)
        {
            return false;
        }
        try
        {
            com.refresh();
            TCComponent[] relStatus = com.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                return true;
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return false;
    }
  


    public static String getReleasedName(TCComponent tcc)
    {
        if (tcc == null)
        {
            return null;
        }
        try
        {
            tcc.refresh();
            TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                TCComponent status = relStatus[relStatus.length - 1];
                String name = status.getTCProperty("name").getStringValue();
                return name;
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取异常信息
     * 
     * @param e
     * @return String
     */
    public static String getExceptionMsg(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /*
     * 将对象发送到结构管理器
     * 
     * @param session
     * 
     * @param com
     * 
     * @return TCComponentBOMLine
     */
    public static TCComponentBOMLine openBomWindow(TCSession session, TCComponent com)
    {
        TCComponentBOMLine topBomline = null;
        try
        {
            TCComponentItemRevision rev = null;
            TCComponentItem item = null;
            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
            TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
            if (com instanceof TCComponentItem)
            {
                item = (TCComponentItem) com;
                topBomline = imancomponentbomwindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
            }
            else if (com instanceof TCComponentItemRevision)
            {
                rev = (TCComponentItemRevision) com;
                topBomline = imancomponentbomwindow.setWindowTopLine(rev.getItem(), rev, null, null);
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return topBomline;
    }

    /**
     * 创建BOMWindow
     * 
     * @param session
     * @return
     */
    public static TCComponentBOMWindow createBOMWindow(TCSession session)
    {
        TCComponentBOMWindow window = null;
        try
        {
            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
            window = imancomponentbomwindowtype.create(imancomponentrevisionrule);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return window;
    }

    public static TCComponentBOMLine getTopBomline(TCComponentBOMWindow bomWindow, TCComponent com)
    {
        TCComponentBOMLine topBomline = null;
        try
        {
            if (bomWindow == null)
            {
                return topBomline;
            }
            TCComponentItemRevision rev = null;
            TCComponentItem item = null;
            if (com instanceof TCComponentItem)
            {
                item = (TCComponentItem) com;
                topBomline = bomWindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
            }
            else if (com instanceof TCComponentItemRevision)
            {
                rev = (TCComponentItemRevision) com;
                topBomline = bomWindow.setWindowTopLine(rev.getItem(), rev, null, null);
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return topBomline;
    }

    
    /**
	 * 
	 * @param rootLine
	 * @param lines
	 * @param unpacked 是否解包 true 解包 false 不解包
	 * @return
	 * @throws TCException
	 */
	public static List<TCComponentBOMLine> getTCComponmentBOMLines(TCComponentBOMLine rootLine,
			List<TCComponentBOMLine> lines, boolean unpacked) throws TCException {
		if (lines == null) {
			lines = new ArrayList<TCComponentBOMLine>();
		}
		AIFComponentContext[] componmentContext = rootLine.getChildren();
		if (componmentContext != null) {
			for (int i = 0; i < componmentContext.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
				if (unpacked) {
					if (bomLine.isPacked()) {
						TCComponentBOMLine[] packedLines = bomLine.getPackedLines();
						bomLine.unpack();
						if (packedLines != null && packedLines.length > 0) {
							lines.add(bomLine);
							lines.addAll(Arrays.asList(packedLines));
						} else {
							lines.add(bomLine);
						}
					} else {
						lines.add(bomLine);
					}
				} else {
					lines.add(bomLine);
				}
				getTCComponmentBOMLines(bomLine, lines, unpacked);
			}
		}
		return lines;
	}
	
	
    /**
     * 调用查询构建器查询
     * 
     * @param session
     * @param queryName
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    public static TCComponent[] executeQuery(TCSession session, String queryName, String[] keys, String[] values) throws Exception
    {
        TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
        if (keys.length != values.length)
        {
            throw new Exception("queryAttributies length is not equal queryValues length");
        }
        String[] queryAttributeDisplayNames = new String[keys.length];
        TCQueryClause[] elements = query.describe();
        for (int i = 0; i < keys.length; i++)
        {
            for (TCQueryClause element : elements)
            {
                // System.out.println("queryName: " + element.getAttributeName());
                if (element.getAttributeName().equals(keys[i]))
                {
                    queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
                }
            }
            if (queryAttributeDisplayNames[i] == null || queryAttributeDisplayNames[i].equals(""))
            {
                throw new Exception("queryAttribute\"" + keys[i] + "\"未找到对应的显示名称");
            }
        }
        // System.out.println("queryAttributeDisplayNames:" + Arrays.toString(queryAttributeDisplayNames));
        // System.out.println("queryValues:" + Arrays.toString(values));
        return query.execute(queryAttributeDisplayNames, values);
    }

    /**
     * 开旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void setBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("set_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 关闭旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void closeBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("close_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static TCComponentItem findItem(String itemID) throws TCException
    {
        TCComponentItemType itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
        TCComponentItem item = itemType.find(itemID);
        return item;
    }

    public static TCComponentItemRevision findItemRevistion(String revId) throws TCException
    {
        TCComponent tcComponent = getTCSession().getComponentManager().getTCComponent(revId);
        return (TCComponentItemRevision) tcComponent;
    }

    public static TCComponentProcess createProcess(String workflowTemplateName, String processName, String desc, TCComponent[] att)
    {
        try
        {
            TCComponentTaskTemplateType templateType = (TCComponentTaskTemplateType) getTCSession().getTypeComponent(TCComponentTaskTemplateType.EPM_TASKTEMPLATE_TYPE);
            TCComponentTaskTemplate template = templateType.find(workflowTemplateName, 0);
            if (template == null)
            {
                return null;
            }
            int[] attType = null;
            if (att != null && att.length > 0)
            {
                attType = new int[att.length];
                for (int i = 0; i < att.length; i++)
                {
                    attType[i] = 1;
                }
            }
            TCComponentProcessType processType = (TCComponentProcessType) getTCSession().getTypeComponent("Job");
            TCComponentProcess process = (TCComponentProcess) processType.create(processName, desc == null ? "" : desc, template, att, attType);
            return process;
        }
        catch (TCException e)
        {
            e.printStackTrace();
            return null;
        }
    }

   

    public static boolean checkUserPrivilege(TCSession session, TCComponent com)
    {
        boolean isWrite = false;
        try
        {
            TCAccessControlService service = session.getTCAccessControlService();
            isWrite = service.checkPrivilege(com, TCAccessControlService.WRITE);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return isWrite;
    }
    

    /**
     * 通过uid获取对象
     * 
     * @param uid
     * @return
     * @throws TCException
     */
    public TCComponent getTcComponentByUid(String uid) throws TCException
    {
        return getTCSession().getComponentManager().getTCComponent(uid);
    }

    /**
     * 解包BOM结构
     * 
     * @param designBOMTopLine
     * @throws Exception
     */
    public static void unpackageBOMStructure(TCComponentBOMLine topBOMLine) throws Exception
    {
        AIFComponentContext[] children = topBOMLine.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            if (bomLine.isPacked())
            {
                bomLine.unpack();
            }
            unpackageBOMStructure(bomLine);
        }
    }

    public static String post(String actionUrl, String params) throws IOException
    {
        String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        // connection.setRequestProperty("key", "value");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(params);
        writer.flush();
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((strRead = reader.readLine()) != null)
        {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        connection.disconnect();
        return sbf.toString();
    }

    /**
     * 判断是否有写权限
     * 
     * @param session
     * @param component
     * @return
     */
    public static boolean checkOwninguserisWrite(TCSession session, TCComponent component)
    {
        boolean isWrite = false;
        try
        {
            TCAccessControlService service = session.getTCAccessControlService();
            isWrite = service.checkPrivilege(component, "WRITE");
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return isWrite;
    }

    /**
     * 返回升版规则
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    public static String getVersionRule(TCComponentItemRevision itemRev) throws TCException
    {
        String version = itemRev.getProperty("item_revision_id");
        String versionRule = "";
        if (version.matches("[0-9]+"))
        { // 判断对象版本是否为数字版
            versionRule = "NN";
        }
        else if (version.matches("[a-zA-Z]+"))
        { // 判断对象版本是否为字母版
            versionRule = "@";
        }
        return versionRule;
    }    
  

    /**
     * 返回错误信息
     *
     * @param data
     * @return
     */
    public static String getErrorMsg(ServiceData data)
    {
        String errorMsg = "";
        int errorSize = data.sizeOfPartialErrors();
        if (errorSize > 0)
        {
            for (int i = 0; i < errorSize; i++)
            {
                errorMsg += errorMsg + Arrays.toString(data.getPartialError(i).getMessages());
            }
        }
        return errorMsg;
    }

    public static TCComponentItemRevision getLatestReleased(TCComponentItemRevision itemRevision) throws TCException
    {
        TCComponent[] revions = itemRevision.getItem().getRelatedComponents("revision_list");
        for (int i = revions.length - 1; i >= 0; i--)
        {
            TCComponentItemRevision itemRev = (TCComponentItemRevision) revions[i];
            if (TCUtil.isReleased(itemRev))
            {
                return itemRev;
            }
        }
        return null;
    }    
    
}

