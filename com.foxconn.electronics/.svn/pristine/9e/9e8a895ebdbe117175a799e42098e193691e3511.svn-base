package com.foxconn.electronics.managementebom.copybom;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.tcutils.constant.FolderNameConstant;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.util.ArrayUtil;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;

public class CopyBOMDialog extends Dialog
{
    private CheckboxTreeViewer                  treeViewer;
    private TCComponentBOMLine                  rootLine;
    private Registry                            reg;
    private Map<TCComponent, TCComponentFolder> cacheRelateMap = new HashMap<>();
    TCComponentBOMWindow                        newBomWindow   = null;
    private static String[]                     bomLineAtrr    = { "bl_sequence_no", "bl_occ_d9_Location", "bl_quantity", "bl_occ_d9_PackageType", "bl_occ_d9_Side", "bl_occ_d9_AltGroup", "bl_occ_d9_ReferenceDimension" };

    protected CopyBOMDialog(Shell parentShell)
    {
        super(parentShell);
        TCUtil.centerShell(parentShell);
        reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
        AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        try
        {
            if (app.getBOMWindow() == null)
            {
                MessageDialog.openError(shell, "openError", reg.getString("selectError"));
            }
            TCComponentBOMWindow bomWindow = app.getBOMWindow();
            rootLine = bomWindow.getTopBOMLine();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(null);
        treeViewer = new CheckboxTreeViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setBounds(10, 38, 767, 395);
        tree.setLinesVisible(true);
        tree.setHeaderBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
        tree.setHeaderVisible(true);
        TreeColumn column = new TreeColumn(tree, SWT.LEFT);
        column.setText("Name");
        column.setWidth(300);
        column = new TreeColumn(tree, SWT.LEFT);
        column.setText("Description");
        column.setWidth(450);
        Label projectLabel = new Label(container, SWT.NONE);
        projectLabel.setBounds(10, 10, 61, 17);
        projectLabel.setText("请选择专案：");
        ComboViewer comboViewer = new ComboViewer(container, SWT.NONE);
        Combo combo_1 = comboViewer.getCombo();
        combo_1.setBounds(102, 7, 215, 25);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                String objectName = "";
                if (element instanceof TCComponentProject)
                {
                    try
                    {
                        TCComponentProject project = (TCComponentProject) element;
                        objectName = project.getProjectID() + "-" + project.getProjectName();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                return objectName;
            }
        });
        try
        {
            TCComponentProject[] projects = getProjects();
            comboViewer.setInput(projects);
        }
        catch (TCException e)
        {
            MessageBox.post("Error", "获取专案信息失败!", MessageBox.ERROR);
            e.printStackTrace();
        }
        comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent arg0)
            {
                StructuredSelection structuredSelection = (StructuredSelection) arg0.getSelection();
                Object selectObject = structuredSelection.getFirstElement();
                TCComponentProject project = (TCComponentProject) selectObject;
                String ids = project.getProjectID();
                TCComponentFolder folder = getCustomPNFolder(ids);
                treeViewer.setInput(folder);
                if (folder == null)
                {
                    MessageDialog.openWarning(getParentShell(), "Warning", project.getProjectName() + " 没有找到 自编料号物料协同工作区 !");
                }
            }
        });
        treeViewer.setContentProvider(new FolderTreeContenetProvider());
        treeViewer.setLabelProvider(new FolderTableLableProvider());
        treeViewer.addFilter(new ViewerFilter()
        {
            @Override
            public boolean select(Viewer arg0, Object arg1, Object arg2)
            {
                if (arg1 != null && arg1 instanceof TCComponentFolder)
                {
                    try
                    {
                        AIFComponentContext[] childContexts = ((TCComponentFolder) arg1).getChildren();
                        for (AIFComponentContext childContext : childContexts)
                        {
                            if (childContext.getComponent().equals(rootLine.getItem()))
                            {
                                return false;
                            }
                        }
                    }
                    catch (TCException e)
                    {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        treeViewer.addCheckStateListener(new ICheckStateListener()
        {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event)
            {
                if (event.getChecked())
                {
                    treeViewer.setAllChecked(false);
                    Object o = event.getElement();
                    TreeItem treeItem = (TreeItem) treeViewer.testFindItem(o);
                    if (treeItem != null)
                    {
                        if (o instanceof TCComponentItem)
                        {
                            try
                            {
                                String itemId = ((TCComponentItem) o).getProperty("item_id");
                                if (itemId.startsWith("79"))
                                {
                                    treeItem.setChecked(true);
                                }
                            }
                            catch (TCException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    treeViewer.refresh();
                }
            }
        });
        try
        {
            TCComponentItemRevision itemRev = rootLine.getItemRevision();
            TCComponent[] itemRevProjects = itemRev.getReferenceListProperty("project_list");
            if (itemRevProjects != null && itemRevProjects.length > 0)
            {
                comboViewer.setSelection(new StructuredSelection(itemRevProjects[0]));
                TCComponentFolder folder = getCustomPNFolder(((TCComponentProject) itemRevProjects[0]).getProjectID());
                if (folder != null)
                {
                    treeViewer.setInput(folder);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return container;
    }

    public TCComponentFolder getCustomPNFolder(String ids)
    {
        try
        {
            if (StringUtils.isNotEmpty(ids))
            {
                TCComponent[] queryResult = TCUtil.executeQuery(RACUIUtil.getTCSession(), "__D9_Find_Project_Folder", new String[] { "d9_SPAS_ID" }, new String[] { ids });
                if (queryResult.length > 0)
                {
                    TCComponentFolder projectFolder = (TCComponentFolder) queryResult[0];
                    TCComponentFolder customPNFolder = getFolder(projectFolder, FolderNameConstant.PROJECT_DESIGN_FOLDER, FolderNameConstant.PROJECT_CSUTOM_PN_FOLDER);
                    return customPNFolder;
                }
                else
                {
                    // MessageDialog.openError(shell, "Error", "没有指派专案!!");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public TCComponentFolder getFolder(TCComponentFolder parentFolder, String... folderNames) throws TCException
    {
        AIFComponentContext[] childContexts = parentFolder.getChildren();
        if (childContexts.length > 0)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childFolder = (TCComponent) childContext.getComponent();
                if (childFolder instanceof TCComponentFolder)
                {
                    String folderName = childFolder.getProperty("object_name");
                    if (folderNames[0].equalsIgnoreCase(folderName))
                    {
                        if (folderNames.length == 1)
                        {
                            {
                                return (TCComponentFolder) childFolder;
                            }
                        }
                        else if (folderNames.length > 1)
                        {
                            String[] newFolderNames = ArrayUtil.sub(folderNames, 1, folderNames.length);
                            return getFolder((TCComponentFolder) childFolder, newFolderNames);
                        }
                    }
                }
            }
        }
        return null;
    }

    public void processCopyBOM(TCComponentFolder pcbaFolder) throws Exception
    {
        TCComponentBOMWindow bomWindow = rootLine.getCachedWindow();
        AIFComponentContext[] childContexts = pcbaFolder.getChildren();
        if (getFWBOMLine(rootLine).size() > 0 && childContexts.length > 0)
        {
            TCComponentItemRevision newItemRev = doRevise(rootLine.getItemRevision());
            bomWindow.clearCache();
            bomWindow.setWindowTopLine(null, newItemRev, null, null);
            rootLine = bomWindow.getTopBOMLine();
            bomWindow.postOpen(rootLine);
            bomWindow.refresh();
            Set<TCComponentBOMLine> boms = getFWBOMLine(rootLine);
            for (TCComponentBOMLine bom : boms)
            {
                bom.cut();
            }
        }
        if (childContexts.length > 0)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childCom = (TCComponent) childContext.getComponent();
                if (childCom instanceof TCComponentItem)
                {
                    String itemId = childCom.getProperty("item_id");
                    if (itemId.startsWith("629"))
                    {
                        TCComponentBOMLine childBOMLine = rootLine.add((TCComponentItem) childCom, ((TCComponentItem) childCom).getLatestItemRevision(), null, false);
                    }
                }
            }
        }
        bomWindow.save();
        bomWindow.refresh();
    }

    public void processCopyBOM(TCComponentFolder pcbaFolder, TCComponentItemRevision itemRev) throws Exception
    {
        AIFComponentContext[] childContexts = pcbaFolder.getChildren();
        newBomWindow = PartBOMUtils.createBomWindow(itemRev);
        TCComponentBOMLine newBomLine = newBomWindow.getTopBOMLine();
        if (newBomLine.hasChildren())
        {
            newBomWindow.close();
            throw new RuntimeException(" 已经存在BOM 结构不能再另外BOM ，请处理！");
        }
        newBomWindow.clearCache();
        newBomWindow.close();
        TCComponentItemRevision newItemRev = doRevise(itemRev);
        newBomWindow = PartBOMUtils.createBomWindow(newItemRev);
        newBomLine = newBomWindow.getTopBOMLine();
        rootLine.unpack();
        rootLine.refresh();
        AIFComponentContext[] oldAif = rootLine.getChildren();
        if (oldAif != null && oldAif.length > 0)
        {
            for (AIFComponentContext childContext : oldAif)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) childContext.getComponent();
                if (!bomLine.isSubstitute())
                {
                    TCComponentItemRevision partRev = bomLine.getItemRevision();
                    if (!isFW(partRev))
                    {
                        TCComponentBOMLine newChildbomLine = newBomLine.add(bomLine.getItem(), partRev, null, false);
                        try
                        {
                            String[] bomLineAttrValue = bomLine.getProperties(bomLineAtrr);
                            newChildbomLine.setProperties(bomLineAtrr, bomLineAttrValue);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (childContexts != null && childContexts.length > 0)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childCom = (TCComponent) childContext.getComponent();
                if (childCom instanceof TCComponentItem)
                {
                    TCComponentItemRevision partRev = ((TCComponentItem) childCom).getLatestItemRevision();
                    if (isFW(partRev))
                    {
                        newBomLine.add((TCComponentItem) childCom, partRev, null, false);
                    }
                }
            }
        }
        newBomWindow.save();
        newItemRev.add("D9_HasSourceBOM_REL", rootLine.getItemRevision());
        rootLine.getItemRevision().add("D9_HasDerivedBOM_REL", newItemRev);
    }

    public boolean isFW(TCComponent tccompent) throws TCException
    {
        return "BE761".equalsIgnoreCase(tccompent.getProperty("d9_MaterialGroup"));
    }

    public Set<TCComponentBOMLine> getFWBOMLine(TCComponentBOMLine parentBOMLine) throws TCException
    {
        Set<TCComponentBOMLine> boms = new HashSet<TCComponentBOMLine>();
        if (rootLine.hasChildren())
        {
            AIFComponentContext[] childBOMCX = rootLine.getChildren();
            for (AIFComponentContext cx : childBOMCX)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) cx.getComponent();
                String itemId = bomLine.getItem().getProperty("item_id");
                if (itemId.startsWith("629"))
                {
                    boms.add(bomLine);
                }
            }
        }
        return boms;
    }

    public TCComponentItemRevision doRevise(TCComponentItemRevision itemRev) throws Exception
    {
        String versionRule = "";
        String version = itemRev.getProperty("item_revision_id");
        if (version.matches("[0-9]+"))
        {
            versionRule = "NN";
        }
        else if (version.matches("[a-zA-Z]+"))
        {
            versionRule = "@";
        }
        String newRevId = TCUtil.reviseVersion(RACUIUtil.getTCSession(), versionRule, itemRev.getTypeObject().getName(), itemRev.getUid());
        TCComponentItemRevision newItemRev = TCUtil.doRevise(itemRev, newRevId);
        return newItemRev;
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(reg.getString("CopyBOM.LABEL"));
    }

    @Override
    public boolean close()
    {
        cacheRelateMap.clear();
        return super.close();
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize()
    {
        return new Point(793, 519);
    }

    @Override
    protected void buttonPressed(int buttonId)
    {
        switch (buttonId)
        {
            case IDialogConstants.OK_ID:
                treeViewer.refresh();
                Object[] selectedObjects = treeViewer.getCheckedElements();
                if (selectedObjects.length == 1)
                {
                    Object object = selectedObjects[0];
                    if (object instanceof TCComponentItem)
                    {
                        TCComponentItem item = (TCComponentItem) object;
                        try
                        {
                            TCUtil.setBypass(RACUIUtil.getTCSession());
                            item.refresh();
                            TCComponentFolder pcbaFolder = cacheRelateMap.get(item);
                            processCopyBOM(pcbaFolder, item.getLatestItemRevision());
                            MessageDialog.openWarning(getParentShell(), "Info", "Success!");
                            super.buttonPressed(buttonId);
                        }
                        catch (Exception e)
                        {
                            MessageDialog.openWarning(getParentShell(), "Warning", "程式执行异常，可能的原因:" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        finally
                        {
                            if (newBomWindow != null)
                            {
                                try
                                {
                                    newBomWindow.close();
                                }
                                catch (TCException e)
                                {
                                    System.out.println("newBomWindow close fail :: " + e.getLocalizedMessage());
                                }
                            }
                            TCUtil.closeBypass(RACUIUtil.getTCSession());
                        }
                    }
                    else
                    {
                        MessageDialog.openWarning(getParentShell(), "Warning", "请选择  PCAB");
                    }
                }
                else
                {
                    MessageDialog.openWarning(getParentShell(), "Warning", "请选择一笔 PCAB");
                }
                break;
            case IDialogConstants.CANCEL_ID:
                super.buttonPressed(buttonId);
                break;
            default:
                break;
        }
    }

    private TCComponentProject[] getProjects() throws TCException
    {
        TCSession tcSession = RACUIUtil.getTCSession();
        TCComponentUser user = tcSession.getUser();
        TCComponentProjectType projectType = (TCComponentProjectType) tcSession.getTypeComponent(ITypeName.TC_Project);
        return projectType.extent(user, true);
    }

    class FolderTreeContenetProvider implements ITreeContentProvider
    {
        public boolean filter(TCComponent tcCom)
        {
            if (tcCom != null && tcCom instanceof TCComponentItem)
            {
                try
                {
                    String itemId = tcCom.getProperty("item_id");
                    if (itemId.startsWith("629"))
                    {
                        return false;
                    }
                    if (itemId.startsWith("79"))
                    {
                        String group = ((TCComponentItem) tcCom).getLatestItemRevision().getProperty("d9_MaterialGroup");
                        if ("B8X80".equalsIgnoreCase(group))
                        {
                            return false;
                        }
                    }
                    return true;
                }
                catch (TCException e)
                {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public Object[] getChildren(Object parentElement)
        {
            TCComponent tccomnComponent = (TCComponent) parentElement;
            if (tccomnComponent instanceof TCComponentFolder)
            {
                TCComponentFolder folder = (TCComponentFolder) tccomnComponent;
                try
                {
                    List<TCComponent> list = new ArrayList<>();
                    AIFComponentContext[] cs = folder.getChildren();
                    for (AIFComponentContext con : cs)
                    {
                        TCComponent tcCom = (TCComponent) con.getComponent();
                        if (!filter(tcCom))
                        {
                            cacheRelateMap.put(tcCom, folder);
                            list.add(tcCom);
                        }
                    }
                    return list.toArray();
                }
                catch (TCException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public Object getParent(Object element)
        {
            return null;
        }

        public boolean hasChildren(Object element)
        {
            TCComponent tccomnComponent = (TCComponent) element;
            if (tccomnComponent instanceof TCComponentFolder)
            {
                try
                {
                    return (((TCComponentFolder) tccomnComponent).getChildren()).length > 0;
                }
                catch (TCException e)
                {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public Object[] getElements(Object inputElement)
        {
            TCComponent folder = (TCComponent) inputElement;
            if (folder instanceof TCComponentFolder)
            {
                return getChildren(inputElement);
            }
            else
            {
                return new Object[] { folder };
            }
        }

        public void dispose()
        {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    class FolderTableLableProvider implements ITableLabelProvider
    {
        public Image getColumnImage(Object element, int columnIndex)
        {
            if (columnIndex == 0)
            {
                String pngPath = "";
                if (element instanceof TCComponentFolder)
                {
                    pngPath = "/com/foxconn/electronics/icons/Folder.png";
                }
                else if (element instanceof TCComponentItem)
                {
                    pngPath = "/com/foxconn/electronics/icons/item_16.png";
                }
                if (pngPath.length() > 0)
                {
                    InputStream in = CopyBOMDialog.class.getResourceAsStream(pngPath);
                    return new Image(null, in);
                }
            }
            return null;
        }

        public String getColumnText(Object element, int columnIndex)
        {
            TCComponent tccomnComponent = (TCComponent) element;
            String objectName = "";
            String desc = "";
            try
            {
                objectName = tccomnComponent.getProperty("object_name");
                desc = tccomnComponent.getProperty("d9_EnglishDescriptionComp");
            }
            catch (TCException e)
            {
                // e.printStackTrace();
            }
            switch (columnIndex)
            {
                case 0:
                    return objectName;
                case 1:
                    return desc;
                default:
                    return "";
            }
        }

        public void addListener(ILabelProviderListener listener)
        {
        }

        public void dispose()
        {
        }

        public boolean isLabelProperty(Object element, String property)
        {
            return false;
        }

        public void removeListener(ILabelProviderListener listener)
        {
        }
    }
}
