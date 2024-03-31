package com.teamcenter.rac.commands.paste;

import com.teamcenter.rac.aif.AIFClipboard;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.common.AIFXMLTransferable;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.aifrcp.SelectionHelper;
import com.teamcenter.rac.commands.newproxylink.NewProxyLinkCommand;
import com.teamcenter.rac.commands.userpreferences.GeneralUIPanel;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGDE;
import com.teamcenter.rac.kernel.TCComponentGDELine;
import com.teamcenter.rac.kernel.TCComponentGDELink;
import com.teamcenter.rac.kernel.TCComponentGDELinkLine;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentMECfgLine;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskInBox;
import com.teamcenter.rac.kernel.TCComponentTempProxyLink;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.services.IProgressCheckingService;
import com.teamcenter.rac.services.IPropertiesOnRelationService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.log.Debug;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;

public class PasteCommand extends AbstractAIFCommand
  implements PropertyChangeListener, InterfaceAIFOperationListener
{
  protected Frame parent;
  private final PropertyChangeSupport m_propertySupport = new PropertyChangeSupport(this);
  protected PasteDialog dlg;
  public static final String DEFAULT_CONTEXT = "contents";
  public static final String SUBTASK_DEFINITION_CONTEXT = "child_taskdefinition";
  public AIFComponentContext[] source;
  public static final String IMPLEMENTEDBY_CONTEXT = "CMImplementedBy";
  public static final String IMPLEMENTS_CONTEXT = "CMImplements";
  private List m_pendingCutComponents;
  private IProgressCheckingService m_progressCheckingService;

  
  
  private boolean checkParam(AIFComponentContext[] arrayOfAIFComponentContext) {
	  if(arrayOfAIFComponentContext==null||arrayOfAIFComponentContext.length<=0) {
		  return true;
	  }
	  InterfaceAIFComponent cf0=arrayOfAIFComponentContext[0].getComponent();
	  try {
	  String gn=((TCSession)cf0.getSession()).getCurrentGroup().getFullName();
	      System.out.println("user group "+gn);
	     if(gn.toLowerCase().contains("dba")) {
		    return true;
	      }
	  }catch(Exception e) {}
	  
	  List<String> fs=getArrayPreference((TCSession)cf0.getSession(), TCPreferenceService.TC_preference_site, "D9_Control_FolderType");
		
	  for(AIFComponentContext ac:arrayOfAIFComponentContext){
		  InterfaceAIFComponent pf=ac.getParentComponent();
		  InterfaceAIFComponent cf=ac.getComponent();		
	     if((cf instanceof TCComponentFolder) && (pf instanceof TCComponentFolder)) {
			  TCComponentFolder cff= (TCComponentFolder)cf;	
			  String childObjType=cff.getTypeObject().getName();
			  System.out.println("child folder type ===="+childObjType);
			
			  TCComponentFolder pff= (TCComponentFolder)pf;	
			  String parentObjType=pff.getTypeObject().getName(); 
			  System.out.println("parent folder type ===="+childObjType);
			  int f1=0;
			  int f2=0;
			  for(String f:fs) {
				 System.out.println(" fs======"+f);
				 if(f.trim().equalsIgnoreCase(childObjType)) {
					 f1=1;
					 break;
				 }
			  }
			  for(String f:fs) {
			    System.out.println(" fs======"+f);
				 if(f.trim().equalsIgnoreCase(parentObjType)) {
				   f2=1;
				   break;
				 }
			   }			 
			  if(f1==1&&f2==1) {
			      MessageBox messageBox = new MessageBox("非法操作,不能黏贴到此文件夹下", "Warning", 4);
		          messageBox.setModal(true);
		          messageBox.setVisible(true);
			      return false;
			  }
	   }
	  }
	  return true;
  }
  
  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Frame paramFrame, Boolean paramBoolean)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, null);
    if(!checkParam(arrayOfAIFComponentContext)) {
        return;	
    }
    if ((arrayOfAIFComponentContext != null) && (this.parent != null))
    {
      String str = (String)arrayOfAIFComponentContext[0].getContext();
      if (hasVisibleAttributesOnRelation(str))
      {
        setRunnablePropsOnRelation(arrayOfAIFComponentContext);
      }
      else
      {
        if (this.m_progressCheckingService == null)
          this.m_progressCheckingService = ((IProgressCheckingService)OSGIUtil.getService(Activator.getDefault(), IProgressCheckingService.class));
        if ((this.m_progressCheckingService != null) && (this.m_progressCheckingService.isProgressJobRunning()))
          return;
        this.m_progressCheckingService.setProgressing(true);
        this.dlg = new PasteDialog(arrayOfAIFComponentContext, this.parent, paramBoolean.booleanValue());
        this.dlg.addPropertyChangeListener(this);
        this.dlg.addCompletionListener(this);
        setRunnable(this.dlg);
      }
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(Boolean paramBoolean, TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, Frame paramFrame)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = null;
    if (paramBoolean.booleanValue())
      arrayOfAIFComponentContext = buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, paramString);
    else
      arrayOfAIFComponentContext = buildComponentContextFromPreference(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, null);
    if ((arrayOfAIFComponentContext == null) || (this.parent == null))
      return;
    if(!checkParam(arrayOfAIFComponentContext)) {
       return;	
     }
    setRunnable(new IC_InvokePropertiesOnRelationRunnable(arrayOfAIFComponentContext));
  }

  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent)
  {
	  AIFComponentContext[]  tmp=buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, null);
	  if(!checkParam(tmp)) {
	        return;	
	    }
      this.source = tmp;
  }

  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Boolean paramBoolean1, Boolean paramBoolean2)
  {
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, null);
    if(!checkParam(arrayOfAIFComponentContext)) {
        return;	
     }
    if ((arrayOfAIFComponentContext != null) && (paramBoolean1.booleanValue()))
    {
      this.dlg = new PasteDialog(arrayOfAIFComponentContext, paramBoolean2.booleanValue());
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, Boolean paramBoolean, Frame paramFrame)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, paramString);
    if(!checkParam(arrayOfAIFComponentContext)) {
        return;	
     }
    if ((arrayOfAIFComponentContext != null) && (this.parent != null))
    {
      this.dlg = new PasteDialog(arrayOfAIFComponentContext, this.parent, paramBoolean.booleanValue());
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, Boolean paramBoolean1, Boolean paramBoolean2)
  {
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromClipboard(paramArrayOfInterfaceAIFComponent, paramString);
    if(!checkParam(arrayOfAIFComponentContext)) {
        return;	
     }
    if ((arrayOfAIFComponentContext != null) && (paramBoolean2 != null) && (paramBoolean2.booleanValue()))
    {
      this.dlg = new PasteDialog(arrayOfAIFComponentContext, paramBoolean1.booleanValue());
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Frame paramFrame)
  {
    this(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramFrame, Boolean.FALSE);
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Frame paramFrame, Boolean paramBoolean)
  {
    this(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramFrame, null, paramBoolean);
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Frame paramFrame, String paramString, Boolean paramBoolean)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromPreference(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramString);
    if(!checkParam(arrayOfAIFComponentContext)) {
        return;	
     }
    createPasteDialog(paramFrame, paramBoolean, arrayOfAIFComponentContext);
  }

  private void createPasteDialog(Frame paramFrame, Boolean paramBoolean, AIFComponentContext[] paramArrayOfAIFComponentContext)
  {
	 if(!checkParam(paramArrayOfAIFComponentContext)) {
	   return;	
	}
    if ((paramArrayOfAIFComponentContext != null) && (this.parent != null))
    {
      this.dlg = new PasteDialog(paramArrayOfAIFComponentContext, paramFrame, false, paramBoolean.booleanValue());
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (paramArrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(paramArrayOfAIFComponentContext));
    }
  }

  public PasteCommand(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, TCComponent[] paramArrayOfTCComponent, Frame paramFrame, Boolean paramBoolean1, Boolean paramBoolean2)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = null;
    if (paramArrayOfInterfaceAIFComponent != null)
      arrayOfAIFComponentContext = buildComponentParentContext(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent);
    if(!checkParam(arrayOfAIFComponentContext)) {
 	   return;	
 	}
    if ((arrayOfAIFComponentContext != null) && (this.parent != null))
    {
      this.dlg = new PasteDialog(arrayOfAIFComponentContext, paramFrame, false);
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, Frame paramFrame)
  {
    this(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramString, paramFrame, true);
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, Frame paramFrame, boolean paramBoolean)
  {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromPreference(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramString, paramBoolean);
    if(!checkParam(arrayOfAIFComponentContext)) {
  	   return;	
  	}
    if ((arrayOfAIFComponentContext != null) && (this.parent != null))
    {
      if (hasVisibleAttributesOnRelation(paramString))
      {
        setRunnablePropsOnRelation(arrayOfAIFComponentContext);
      }
      else
      {
        this.dlg = new PasteDialog(arrayOfAIFComponentContext, paramFrame, false);
        this.dlg.addPropertyChangeListener(this);
        this.dlg.addCompletionListener(this);
        setRunnable(this.dlg);
      }
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  public PasteCommand(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, Boolean paramBoolean)
  {
    AIFComponentContext[] arrayOfAIFComponentContext = buildComponentContextFromPreference(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, null);
    if(!checkParam(arrayOfAIFComponentContext)) {
  	   return;	
  	}
    if ((arrayOfAIFComponentContext != null) && (paramBoolean != null) && (paramBoolean.booleanValue()))
    {
      this.dlg = new PasteDialog(arrayOfAIFComponentContext, false);
      this.dlg.addPropertyChangeListener(this);
      this.dlg.addCompletionListener(this);
      setRunnable(this.dlg);
    }
    else
    {
      if (arrayOfAIFComponentContext == null)
        return;
      setRunnable(new PasteRunnable(arrayOfAIFComponentContext));
    }
  }

  protected PasteCommand()
  {
  }

  public void setFailBackFlag(boolean paramBoolean)
  {
    if (this.dlg == null)
      return;
    this.dlg.setFailBackFlag(paramBoolean);
  }

  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    this.m_propertySupport.firePropertyChange(paramPropertyChangeEvent);
  }

  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.m_propertySupport.addPropertyChangeListener(paramPropertyChangeListener);
  }

  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    this.m_propertySupport.removePropertyChangeListener(paramPropertyChangeListener);
  }

  public boolean isProgressDialogSuccessful()
  {
    int i = 0;
    if (this.dlg != null)
    {
      List localList = this.dlg.getErrorComponents();
      i = (localList.size() == 0) ? 1 : (localList == null) ? 1 : 0;
    }
    return i==1;
  }

  public boolean hasVisibleAttributesOnRelation(String paramString)
  {
    IPropertiesOnRelationService localIPropertiesOnRelationService = (IPropertiesOnRelationService)OSGIUtil.getService(Activator.getDefault(), IPropertiesOnRelationService.class);
    boolean bool = false;
    if (localIPropertiesOnRelationService != null)
      bool = localIPropertiesOnRelationService.containsVisibleAttributes(paramString);
    return bool;
  }

  public void setRunnablePropsOnRelation(AIFComponentContext[] paramArrayOfAIFComponentContext)
  {
    setRunnable(new IC_InvokePropertiesOnRelationRunnable(paramArrayOfAIFComponentContext));
  }

  public static InterfaceAIFComponent[] getParentsFromPreference(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, TCSession paramTCSession)
  {
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = null;
    try
    {
      TCPreferenceService localTCPreferenceService = (TCPreferenceService)OSGIUtil.getService(Activator.getDefault(), TCPreferenceService.class);
      Integer localInteger;
      TCComponentUser localTCComponentUser;
      TCComponentFolder localTCComponentFolder;
      if ((paramArrayOfInterfaceAIFComponent == null) || (paramArrayOfInterfaceAIFComponent.length == 0))
      {
        localInteger = localTCPreferenceService.getIntegerValue("WsoInsertNoSelectionsPref");
        if (GeneralUIPanel.NOSELECTION_NEWSTUFF_FOLDER.equals(localInteger))
        {
          localTCComponentUser = null;
          localTCComponentFolder = null;
          localTCComponentUser = paramTCSession.getUser();
          localTCComponentFolder = ((TCComponentUser)localTCComponentUser).getNewStuffFolder();
          arrayOfInterfaceAIFComponent = new InterfaceAIFComponent[] { localTCComponentFolder };
        }
        else if (GeneralUIPanel.NOSELECTION_ROOT_FOLDER.equals(localInteger))
        {
          localTCComponentUser = null;
          localTCComponentFolder = null;
          localTCComponentUser = paramTCSession.getUser();
          localTCComponentFolder = ((TCComponentUser)localTCComponentUser).getHomeFolder();
          arrayOfInterfaceAIFComponent = new InterfaceAIFComponent[] { localTCComponentFolder };
        }
      }
      else
      {
        localInteger = localTCPreferenceService.getIntegerValue("WsoInsertSelectionsPref");
        if (GeneralUIPanel.SELECTION_ALL_SELECTED_FOLDER.equals(localInteger))
        {
          arrayOfInterfaceAIFComponent = paramArrayOfInterfaceAIFComponent;
        }
        else if (GeneralUIPanel.SELECTION_NEWSTUFF_FOLDER.equals(localInteger))
        {
          localTCComponentUser = null;
          localTCComponentFolder = null;
          localTCComponentUser = paramTCSession.getUser();
          localTCComponentFolder = ((TCComponentUser)localTCComponentUser).getNewStuffFolder();
          arrayOfInterfaceAIFComponent = new InterfaceAIFComponent[] { localTCComponentFolder };
        }
      }
    }
    catch (TCException localTCException)
    {
    }
    return arrayOfInterfaceAIFComponent;
  }

  protected AIFComponentContext[] buildComponentParentContext(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent)
  {
    AIFComponentContext[] arrayOfAIFComponentContext = null;
    Registry localRegistry = Registry.getRegistry(this);
    if (paramArrayOfTCComponent == null)
    {
      MessageBox localMessageBox1 = new MessageBox(this.parent, localRegistry.getString("noPastingObject"), null, localRegistry.getString("error.TITLE"), 1, true);
      localMessageBox1.setVisible(true);
      return null;
    }
    for (int i = 0; i < paramArrayOfTCComponent.length; ++i)
    {
      if (paramArrayOfTCComponent[i] != null)
        continue;
      MessageBox localMessageBox2 = new MessageBox(this.parent, localRegistry.getString("invalidPastingObject"), null, localRegistry.getString("error.TITLE"), 1, true);
      localMessageBox2.setVisible(true);
      return null;
    }
    if ((paramArrayOfInterfaceAIFComponent != null) && (paramArrayOfInterfaceAIFComponent.length > 0))
      try
      {
        arrayOfAIFComponentContext = processPasteComponents(paramArrayOfInterfaceAIFComponent, paramArrayOfTCComponent, null);
      }
      catch (TCException localTCException)
      {
        MessageBox.post(this.parent, localTCException);
        return null;
      }
    return arrayOfAIFComponentContext;
  }

  protected AIFComponentContext[] buildComponentContextFromClipboard(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString)
  {
    Registry localRegistry = Registry.getRegistry(this);
    if (paramArrayOfInterfaceAIFComponent == null)
    {
      MessageBox.post(this.parent, localRegistry.getString("noPasteTarget"), localRegistry.getString("error.TITLE"), 1);
      return null;
    }
    for (int i = 0; i < paramArrayOfInterfaceAIFComponent.length; ++i)
    {
      if (paramArrayOfInterfaceAIFComponent[i] != null)
        continue;
      MessageBox.post(this.parent, localRegistry.getString("invalidPasteTarget"), localRegistry.getString("error.TITLE"), 1);
      return null;
    }
    Object localObject1 = new ArrayList();
    AbstractAIFSession localAbstractAIFSession = Activator.getDefault().getSessionService().getActivePerspectiveSession();
    List localList = checkSysClipboard(localAbstractAIFSession);
    if ((localList == null) || (localList.isEmpty()))
    {
      ArrayList localArrayList = new ArrayList();
      if (Debug.isOn("action,copy,cut,paste"))
        Debug.println("Get pasting objs from aif clipboard.");
      AIFClipboard localObject2 = (AIFClipboard)OSGIUtil.getService(Activator.getDefault(), AIFClipboard.class);
      ISelection localISelection = ((AIFClipboard)localObject2).getSelectedObjects();
      if (localISelection != null)
        Collections.addAll(localArrayList, SelectionHelper.getTargetComponents(localISelection));
      if ((localArrayList.isEmpty()) && (!((AIFClipboard)localObject2).isEmpty()))
        localArrayList.addAll(((AIFClipboard)localObject2).toVector());
      if (localArrayList.isEmpty())
      {
        MessageBox.post(this.parent, localRegistry.getString("clipboardEmpty"), localRegistry.getString("error.TITLE"), 1);
        return null;
      }
      localObject1 = localArrayList;
    }
    else
    {
      localObject1 = localList;
    }
    int j = ((List)localObject1).size();
    Object localObject2 = new ArrayList();
    Object localObject3;
    AIFComponentContext[] af;
    for (int k = 0; k < j; ++k)
    {
      localObject3 = (InterfaceAIFComponent)((List)localObject1).get(k);
      if (!isValidComponent(localRegistry, (InterfaceAIFComponent)localObject3))
        return null;
      ((List)localObject2).add(localObject3);
    }
    if (((List)localObject2).isEmpty())
      return null;
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = (InterfaceAIFComponent[])((List)localObject2).toArray(new InterfaceAIFComponent[((List)localObject2).size()]);
    try
    {
    	af = processPasteComponents(paramArrayOfInterfaceAIFComponent, arrayOfInterfaceAIFComponent, paramString);
    }
    catch (TCException localTCException)
    {
      MessageBox.post(this.parent, localTCException);
      return null;
    }
    return af;
  }

  protected boolean isValidComponent(Registry paramRegistry, InterfaceAIFComponent paramInterfaceAIFComponent)
  {
    if ((paramInterfaceAIFComponent instanceof TCComponentPseudoFolder) && (!(((TCComponentPseudoFolder)paramInterfaceAIFComponent).getOwningComponent() instanceof TCComponentTask)))
    {
      MessageBox.post(this.parent, Utilities.trimString(paramRegistry.getString("invalidPastingObject"), ".") + " -- " + paramInterfaceAIFComponent + ".", paramRegistry.getString("command.TITLE"), 4, false);
      return false;
    }
    try
    {
      if ((paramInterfaceAIFComponent instanceof TCComponentBOMLine) && (((TCComponentBOMLine)paramInterfaceAIFComponent).getUnderlyingComponent() == null))
      {
        MessageBox.post(this.parent, paramRegistry.getString("invalidPastingObject"), null, paramRegistry.getString("error.TITLE"), 1, true);
        return false;
      }
    }
    catch (TCException localTCException)
    {
      MessageBox.post(this.parent, localTCException);
      return false;
    }
    return true;
  }

  protected List checkSysClipboard(AbstractAIFSession paramAbstractAIFSession)
  {
    if (this.parent == null)
      return null;
    Clipboard localClipboard = this.parent.getToolkit().getSystemClipboard();
    String str1 = null;
    try
    {
    	Transferable localObject = localClipboard.getContents(this);
      str1 = (String)((Transferable)localObject).getTransferData(AIFXMLTransferable.XML_Flavor);
    }
    catch (Exception localException)
    {
      return null;
    }
    if (str1 == null)
      return null;
    Object localObject = new NewProxyLinkCommand(this.parent, str1);
    boolean bool = ((NewProxyLinkCommand)localObject).parseXML();
    if (!bool)
      return null;
    String str2 = ((NewProxyLinkCommand)localObject).appId;
    String[] arrayOfString = ((NewProxyLinkCommand)localObject).objIds;
    if ((str2 == null) || (str2.length() == 0) || (arrayOfString == null) || (arrayOfString.length == 0))
    {
      if (Debug.isOn("action,copy,cut,paste"))
        Debug.println("Found xml on sysclipboard, but no appId or objIds.");
      return null;
    }
    String str3 = paramAbstractAIFSession.getAppGuid();
    if (str2.equals(str3))
    {
      if (Debug.isOn("action,copy,cut,paste"))
        Debug.println("Found xml on sysclipboard, but appId is same as currentAppId");
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfString.length; ++i)
      localArrayList.add(new TCComponentTempProxyLink(paramAbstractAIFSession, str2, arrayOfString[i]));
    if (!localArrayList.isEmpty())
    {
      if (Debug.isOn("paste"))
        Debug.println("Get pasting objs from sys clipboard, size=" + localArrayList.size());
      return localArrayList;
    }
    return (List)null;
  }

  protected AIFComponentContext[] processPasteComponents(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent1, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent2, String paramString)
    throws TCException
  {
    String localObject1 = null;
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent1 = new InterfaceAIFComponent[paramArrayOfInterfaceAIFComponent1.length];
    int i = paramArrayOfInterfaceAIFComponent1.length;
    paramArrayOfInterfaceAIFComponent1 = validateParents(paramArrayOfInterfaceAIFComponent1, arrayOfInterfaceAIFComponent1);
    i -= paramArrayOfInterfaceAIFComponent1.length;
    int j = paramArrayOfInterfaceAIFComponent1.length * paramArrayOfInterfaceAIFComponent2.length;
    AIFComponentContext[] arrayOfAIFComponentContext = new AIFComponentContext[j];
    int k = 0;
    ArrayList localArrayList1 = new ArrayList(paramArrayOfInterfaceAIFComponent1.length);
    ArrayList localArrayList2 = new ArrayList(paramArrayOfInterfaceAIFComponent1.length);
    this.m_pendingCutComponents = new ArrayList();
    int i2;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    String str1;
    for (int l = 0; l < paramArrayOfInterfaceAIFComponent1.length; ++l)
    {
      if (Debug.isOn("action,copy,cut,paste"))
        Debug.println("Pasting " + paramArrayOfInterfaceAIFComponent2.length + " children to parent uid " + paramArrayOfInterfaceAIFComponent1[l].getUid() + ", type name [" + paramArrayOfInterfaceAIFComponent1[l].getType() + "], using default context [" + (String)localObject1 + "]");
      String[] arrayOfString1 = null;
      if (paramString == null)
        arrayOfString1 = ((TCComponent)paramArrayOfInterfaceAIFComponent1[l]).getPreferredPasteRelation(paramArrayOfInterfaceAIFComponent2);
      i2 = 0;
      while (i2 < paramArrayOfInterfaceAIFComponent2.length)
      {
        localObject2 = null;
        if ((paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponent) && (((paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentICO) || ((!(paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentBOMLine)) && (!(paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentMECfgLine))))))
        {
          localObject2 = ((TCComponent)paramArrayOfInterfaceAIFComponent2[i2]).getUnderlyingComponent();
          if ((localObject2 != paramArrayOfInterfaceAIFComponent2[i2]) && (((TCComponent)paramArrayOfInterfaceAIFComponent2[i2]).getPendingCut()))
            this.m_pendingCutComponents.add(paramArrayOfInterfaceAIFComponent2[i2]);
        }
        else
        {
          localObject2 = paramArrayOfInterfaceAIFComponent2[i2];
        }
        if (paramString != null)
          localObject1 = paramString;
        else
          localObject1 = arrayOfString1[i2];
        localObject3 = (TCSession)paramArrayOfInterfaceAIFComponent1[l].getSession();
        localObject4 = ((TCSession)localObject3).getTextService();
        str1 = "interprocess_task_dependencies";
        String str2;
        TCComponentTask localObject5;
        if (localObject2 instanceof TCComponentTask)
        {
          str2 = ((TCTextService)localObject4).getTextValue(str1);
          if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentProcess)
          {
            localObject5 = ((TCComponentProcess)paramArrayOfInterfaceAIFComponent1[l]).getRootTask();
            paramArrayOfInterfaceAIFComponent1[l] = localObject5;
            localObject1 = str1;
          }
          else if ((paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentTask) || ((paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentPseudoFolder) && (((TCComponent)paramArrayOfInterfaceAIFComponent1[l]).toString().equals(str2))))
          {
            localObject1 = str1;
            localArrayList1.add(((TCComponentTask)localObject2).getProcess());
            localArrayList2.add(paramArrayOfInterfaceAIFComponent1[l]);
          }
          else if ((!paramArrayOfInterfaceAIFComponent1[l].toString().equals("Parent Processes")) && (!paramArrayOfInterfaceAIFComponent1[l].toString().equals("!!!parent_processes")));
        }
        TCComponentTask localObject6;
        TCComponentProcess localObject7;
        try
        {
          TCComponent localObject5_1 = ((TCComponentPseudoFolder)paramArrayOfInterfaceAIFComponent1[l]).getOwningComponent();
          TCComponentProcess localTCComponentProcess = ((TCComponentTask)localObject5_1).getProcess();
          localObject6 = (TCComponentTask)localObject2;
          localObject7 = ((TCComponentTask)localObject6).getProcess();
          InterfaceAIFComponent[] arrayOfInterfaceAIFComponent2 = { localTCComponentProcess };
          InterfaceAIFComponent[] arrayOfInterfaceAIFComponent3 = { localObject7 };
          return processPasteComponents(arrayOfInterfaceAIFComponent3, arrayOfInterfaceAIFComponent2, null);
        }
        catch (Exception str3)
        {
          str2 = "epm_subprocesses";
          if (localObject2 instanceof TCComponentProcess)
            if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentProcess)
            {
              TCComponentTask  localObject5_1 = ((TCComponentProcess)paramArrayOfInterfaceAIFComponent1[l]).getRootTask();
              paramArrayOfInterfaceAIFComponent1[l] = localObject5_1;
              localObject1 = str2;
            }
            else if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentTask)
            {
              localObject1 = str2;
            }
          else if ((paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentPseudoFolder) && (((TCTextService)localObject4).getTextValue(str2).equals(((TCComponent)paramArrayOfInterfaceAIFComponent1[l]).toString())))
            return null;
          if ((paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentBOMLine) && (paramArrayOfInterfaceAIFComponent1.length > 1) && (((TCComponentBOMLine)paramArrayOfInterfaceAIFComponent2[i2]).getPendingCut()) && (l != paramArrayOfInterfaceAIFComponent1.length - 1))
            if (localObject1 == null)
              localObject1 = TCComponentBOMLine.DELAYBOMCUT;
            else
              localObject1 = localObject1 + TCComponentBOMLine.DELAYBOMCUT;
          Registry localObject5_2 = Registry.getRegistry(this);
          if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentGDELine)
            if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentGDELinkLine)
            {
              if ((!(paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentGDELink)) && (!(paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentGDELinkLine)))
              {
                MessageBox.post(this.parent, ((Registry)localObject5_2).getString("invalidParentObject"), ((Registry)localObject5_2).getString("error.TITLE"), 1);
                return null;
              }
            }
            else if ((paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentGDELinkLine) || ((!(paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentGDELine)) && (!(paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentGDE))))
            {
              MessageBox.post(this.parent, ((Registry)localObject5_2).getString("invalidParentObject"), ((Registry)localObject5_2).getString("error.TITLE"), 1);
              return null;
            }
          String str3_1 = "CMHasWorkBreakdown";
          if ((localObject2 instanceof TCComponentSchedule) && (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentChangeItemRevision))
            localObject1 = str3_1;
          TCComponentPseudoFolder localObject6_1 = null;
          if (paramArrayOfInterfaceAIFComponent1[l] instanceof TCComponentPseudoFolder)
          {
        	  localObject6_1 = (TCComponentPseudoFolder)paramArrayOfInterfaceAIFComponent1[l];
          }
          else
          {
        	  TCComponent localObject7_1 = ((TCComponent)paramArrayOfInterfaceAIFComponent1[l]).getUnderlyingComponent();
            if ((localObject7_1 != null) && (localObject7_1 instanceof TCComponentPseudoFolder))
            	localObject6_1 = (TCComponentPseudoFolder)localObject7_1;
          }
          if (localObject6_1 != null)
          {
            if ((localObject1 == null) || (((String)localObject1).length() == 0))
              localObject1 = ((TCComponentPseudoFolder)localObject6_1).getDefaultPasteRelation();
            if (((String)localObject1).equals("CMImplementedBy"))
            {
              localObject1 = "CMImplements";
              arrayOfAIFComponentContext[(k++)] = new AIFComponentContext((InterfaceAIFComponent)localObject2, ((TCComponentPseudoFolder)localObject6_1).getOwningComponent(), localObject1);
            }
            else
            {
              arrayOfAIFComponentContext[(k++)] = new AIFComponentContext(((TCComponentPseudoFolder)localObject6_1).getOwningComponent(), (InterfaceAIFComponent)localObject2, localObject1);
            }
          }
          else
          {
            arrayOfAIFComponentContext[(k++)] = new AIFComponentContext(paramArrayOfInterfaceAIFComponent1[l], (InterfaceAIFComponent)localObject2, localObject1);
          }
          if (Debug.isOn("action,copy,cut,paste"))
            if (paramArrayOfInterfaceAIFComponent2[i2] instanceof TCComponentTempProxyLink)
              Debug.println("  Pasted children[" + i2 + "], TempProxyLink" + ", name [" + localObject2.toString() + "], type [" + ((InterfaceAIFComponent)localObject2).getType() + "]");
            else
              Debug.println("  Pasted children[" + i2 + "], uid " + ((InterfaceAIFComponent)localObject2).getUid() + ", name [" + localObject2.toString() + "], type [" + ((InterfaceAIFComponent)localObject2).getType() + "]");
          ++i2;
        }
      }
    }
    int l = localArrayList1.size();
    if (l > 0)
    {
      int i1 = paramArrayOfInterfaceAIFComponent1.length;
      i2 = paramArrayOfInterfaceAIFComponent1.length + l;
      localObject2 = new InterfaceAIFComponent[i1];
      System.arraycopy(paramArrayOfInterfaceAIFComponent1, 0, localObject2, 0, i1);
      paramArrayOfInterfaceAIFComponent1 = new InterfaceAIFComponent[i2];
      System.arraycopy(localObject2, 0, paramArrayOfInterfaceAIFComponent1, 0, i1);
      localObject3 = new InterfaceAIFComponent[i1];
      System.arraycopy(paramArrayOfInterfaceAIFComponent2, 0, localObject3, 0, i1);
      paramArrayOfInterfaceAIFComponent2 = new InterfaceAIFComponent[i2];
      System.arraycopy(localObject3, 0, paramArrayOfInterfaceAIFComponent2, 0, i1);
      localObject4 = new AIFComponentContext[i1];
      System.arraycopy(arrayOfAIFComponentContext, 0, localObject4, 0, i1);
      arrayOfAIFComponentContext = new AIFComponentContext[i2];
      System.arraycopy(localObject4, 0, arrayOfAIFComponentContext, 0, i1);
      str1 = "epm_subprocesses";
      for (int i4 = 0; i4 < l; ++i4)
      {
        int i5 = i4 + i1;
        paramArrayOfInterfaceAIFComponent1[i5] = ((InterfaceAIFComponent)localArrayList2.get(i4));
        paramArrayOfInterfaceAIFComponent2[i5] = ((InterfaceAIFComponent)localArrayList1.get(i4));
        arrayOfAIFComponentContext[i5] = new AIFComponentContext(paramArrayOfInterfaceAIFComponent1[i5], paramArrayOfInterfaceAIFComponent2[i5], str1);
      }
    }
    if (i > 0)
    {
      Registry localRegistry = Registry.getRegistry(this);
      String[] arrayOfString2 = new String[i + 1];
      for (int i3 = 0; i3 < i; ++i3)
        arrayOfString2[(i3 + 1)] = arrayOfInterfaceAIFComponent1[i3].toString();
      arrayOfString2[0] = Utilities.trimString(localRegistry.getString("invalidPasteTarget"), ".");
      TCException localTCException = new TCException(arrayOfString2);
      if (arrayOfAIFComponentContext.length < 1)
        throw localTCException;
      if (i > 1)
        MessageBox.post(this.parent, localTCException);
      else
        MessageBox.post(this.parent, arrayOfString2[0] + " -- " + arrayOfString2[1] + ".", localRegistry.getString("command.TITLE"), 4);
    }
    return arrayOfAIFComponentContext;
  }

  protected InterfaceAIFComponent[] validateParents(InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent1, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent2)
  {
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = new InterfaceAIFComponent[paramArrayOfInterfaceAIFComponent1.length];
    System.arraycopy(paramArrayOfInterfaceAIFComponent1, 0, arrayOfInterfaceAIFComponent, 0, paramArrayOfInterfaceAIFComponent1.length);
    ArrayList localArrayList = new ArrayList(paramArrayOfInterfaceAIFComponent1.length);
    int i = 0;
    for (int j = 0; j < paramArrayOfInterfaceAIFComponent1.length; ++j)
      if ((paramArrayOfInterfaceAIFComponent1[j] instanceof TCComponentTaskInBox) || (paramArrayOfInterfaceAIFComponent1[j].getType().equals("User_Inbox")))
        localArrayList.add(paramArrayOfInterfaceAIFComponent1[j]);
      else
        arrayOfInterfaceAIFComponent[(i++)] = paramArrayOfInterfaceAIFComponent1[j];
    int j = localArrayList.size();
    if (j > 0)
    {
      paramArrayOfInterfaceAIFComponent1 = new InterfaceAIFComponent[i];
      System.arraycopy(arrayOfInterfaceAIFComponent, 0, paramArrayOfInterfaceAIFComponent1, 0, i);
      for (int k = 0; k < j; ++k)
        paramArrayOfInterfaceAIFComponent2[k] = ((InterfaceAIFComponent)localArrayList.get(k));
    }
    return paramArrayOfInterfaceAIFComponent1;
  }

  protected AIFComponentContext[] buildComponentContextFromPreference(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString)
  {
    return buildComponentContextFromPreference(paramArrayOfTCComponent, paramArrayOfInterfaceAIFComponent, paramString, true);
  }

  protected AIFComponentContext[] buildComponentContextFromPreference(TCComponent[] paramArrayOfTCComponent, InterfaceAIFComponent[] paramArrayOfInterfaceAIFComponent, String paramString, boolean paramBoolean)
  {
    AIFComponentContext[] arrayOfAIFComponentContext = null;
    InterfaceAIFComponent[] arrayOfInterfaceAIFComponent = null;
    Registry localRegistry = Registry.getRegistry(this);
    if (paramArrayOfTCComponent == null)
    {
      MessageBox localMessageBox1 = new MessageBox(this.parent, localRegistry.getString("noPastingObject"), null, localRegistry.getString("error.TITLE"), 1, true);
      localMessageBox1.setVisible(true);
      return null;
    }
    for (int i = 0; i < paramArrayOfTCComponent.length; ++i)
    {
      if (paramArrayOfTCComponent[i] != null)
        continue;
      MessageBox localMessageBox2 = new MessageBox(this.parent, localRegistry.getString("invalidPastingObject"), null, localRegistry.getString("error.TITLE"), 1, true);
      localMessageBox2.setVisible(true);
      return null;
    }
    TCSession localTCSession = paramArrayOfTCComponent[0].getSession();
    if (paramBoolean)
      arrayOfInterfaceAIFComponent = getParentsFromPreference(paramArrayOfInterfaceAIFComponent, localTCSession);
    else
      arrayOfInterfaceAIFComponent = paramArrayOfInterfaceAIFComponent;
    if ((arrayOfInterfaceAIFComponent != null) && (arrayOfInterfaceAIFComponent.length > 0))
      try
      {
        arrayOfAIFComponentContext = processPasteComponents(arrayOfInterfaceAIFComponent, paramArrayOfTCComponent, paramString);
      }
      catch (TCException localTCException)
      {
        MessageBox.post(this.parent, localTCException);
        return null;
      }
    return arrayOfAIFComponentContext;
  }

  protected void setPasteDialog(PasteDialog paramPasteDialog)
  {
    this.dlg = paramPasteDialog;
  }

  protected PasteDialog getPasteDialog()
  {
    return this.dlg;
  }

  protected void setParent(Frame paramFrame)
  {
    this.parent = paramFrame;
  }

  protected Frame getParent()
  {
    return this.parent;
  }

  public void startOperation(String paramString)
  {
  }

  public void endOperation()
  {
    if (this.m_pendingCutComponents == null)
      return;
    Iterator localIterator = this.m_pendingCutComponents.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      try
      {
        ((TCComponent)localObject).processPendingCut();
      }
      catch (TCException localTCException)
      {
      }
    }
  }

  private class IC_InvokePropertiesOnRelationRunnable
    implements Runnable
  {
    private AIFComponentContext[] contextArray;

    private IC_InvokePropertiesOnRelationRunnable(AIFComponentContext[] arg2)
    {
  
      this.contextArray = arg2;
    }

    public void run()
    {
      TCSession localTCSession = (TCSession)this.contextArray[0].getComponent().getSession();
      String str = (String)this.contextArray[0].getContext();
      InterfaceAIFComponent[] arrayOfInterfaceAIFComponent1 = new InterfaceAIFComponent[this.contextArray.length];
      InterfaceAIFComponent[] arrayOfInterfaceAIFComponent2 = new InterfaceAIFComponent[this.contextArray.length];
      for (int i = 0; i < this.contextArray.length; ++i)
      {
        arrayOfInterfaceAIFComponent1[i] = this.contextArray[i].getParentComponent();
        arrayOfInterfaceAIFComponent2[i] = this.contextArray[i].getComponent();
        if (!(arrayOfInterfaceAIFComponent2[i] instanceof TCComponentBOMLine))
          continue;
        try
        {
          arrayOfInterfaceAIFComponent2[i] = ((TCComponentBOMLine)arrayOfInterfaceAIFComponent2[i]).getUnderlyingComponent();
        }
        catch (TCException localTCException)
        {
        }
      }
      IPropertiesOnRelationService localIPropertiesOnRelationService = (IPropertiesOnRelationService)OSGIUtil.getService(Activator.getDefault(), IPropertiesOnRelationService.class);
      localIPropertiesOnRelationService.renderPropertiesOnRelation(AIFUtility.getActiveDesktop(), localTCSession, arrayOfInterfaceAIFComponent1, arrayOfInterfaceAIFComponent2, str);
    }
  }

  protected class PasteRunnable
    implements Runnable
  {
    private final PasteOperation m_op;

    public PasteRunnable(AIFComponentContext[] arg2)
    {
      this.m_op = new PasteOperation(AIFUtility.getCurrentApplication(), arg2);
    }

    public void run()
    {
      this.m_op.executeModeless();
    }
  }
  
  @SuppressWarnings("deprecation")
  private static List<String> getArrayPreference(TCSession session, int scope, String preferenceName) {
		String[] array = session.getPreferenceService().getStringArray(scope, preferenceName);
		return Arrays.asList(array);
  }
}