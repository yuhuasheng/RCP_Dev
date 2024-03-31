package com.teamcenter.rac.commands.cut;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCUtilities;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.kernel.bom.BOMMarkupHelper;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CutCommand extends AbstractAIFCommand {
  private Frame parent;
  
  private ArrayList parentsWithNoAccess = new ArrayList();
  
  public CutCommand() {}
  
  public CutCommand(AIFComponentContext[] paramArrayOfAIFComponentContext, Frame paramFrame, Boolean paramBoolean1, Boolean paramBoolean2) {
    this.parent = paramFrame;
    AIFComponentContext[] arrayOfAIFComponentContext = checkInput(this.parent, paramArrayOfAIFComponentContext);
    if (arrayOfAIFComponentContext != null) {
      boolean bool = showConfirmation(arrayOfAIFComponentContext);
      setRunnable((Runnable)new CutDialog(arrayOfAIFComponentContext, this.parent, bool, paramBoolean2.booleanValue()));
    } 
  }
  
  public CutCommand(AIFComponentContext[] paramArrayOfAIFComponentContext, Boolean paramBoolean1, Boolean paramBoolean2) {
    AIFComponentContext[] arrayOfAIFComponentContext = checkInput((Frame)null, paramArrayOfAIFComponentContext);
    if (arrayOfAIFComponentContext != null) {
      boolean bool = showConfirmation(arrayOfAIFComponentContext);
      setRunnable((Runnable)new CutDialog(arrayOfAIFComponentContext, bool, paramBoolean2.booleanValue()));
    } 
  }
  
  public CutCommand(AIFComponentContext paramAIFComponentContext, Frame paramFrame, Boolean paramBoolean1, Boolean paramBoolean2) {
    this(new AIFComponentContext[] { paramAIFComponentContext }, paramFrame, paramBoolean1, paramBoolean2);
  }
  
  public CutCommand(AIFComponentContext paramAIFComponentContext, Boolean paramBoolean1, Boolean paramBoolean2) {
    this(new AIFComponentContext[] { paramAIFComponentContext }, paramBoolean1, paramBoolean2);
  }
  
  public CutCommand(AIFComponentContext[] paramArrayOfAIFComponentContext, Boolean paramBoolean1, Boolean paramBoolean2, Boolean paramBoolean3) {
    AIFComponentContext[] arrayOfAIFComponentContext = checkInput((Frame)null, paramArrayOfAIFComponentContext);
    if (arrayOfAIFComponentContext != null && paramBoolean1.booleanValue()) {
      boolean bool = showConfirmation(arrayOfAIFComponentContext);
      setRunnable((Runnable)new CutDialog(arrayOfAIFComponentContext, bool, paramBoolean3.booleanValue()));
    } 
  }
  
  public CutCommand(AIFComponentContext paramAIFComponentContext, Boolean paramBoolean1, Boolean paramBoolean2, Boolean paramBoolean3) {
    this(new AIFComponentContext[] { paramAIFComponentContext }, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  protected AIFComponentContext[] checkInput(Frame paramFrame, AIFComponentContext[] paramArrayOfAIFComponentContext) {
	System.out.println("===================cut checkInput=======================");

    Registry registry = Registry.getRegistry(this);
    ArrayList<AIFComponentContext> arrayList = new ArrayList();
    if (paramArrayOfAIFComponentContext == null || paramArrayOfAIFComponentContext.length == 0) {
      MessageBox.post(paramFrame, registry.getString("noSelection"), registry.getString("error.TITLE"), 4);
      return null;
    } 
  
    String gn="";
	try {
		gn =((TCSession)paramArrayOfAIFComponentContext[0].getComponent().getSession()).getCurrentGroup().getFullName();
		 System.out.println("user group "+gn);
	}catch(Exception e) {}
	if(!(gn.toLowerCase().contains("dba"))) {
	      List<String> fs=getArrayPreference((TCSession)paramArrayOfAIFComponentContext[0].getComponent().getSession(), TCPreferenceService.TC_preference_site, "D9_Control_FolderType");
	      for(AIFComponentContext ac:paramArrayOfAIFComponentContext){
		   InterfaceAIFComponent cf=ac.getComponent();		
	       if((cf instanceof TCComponentFolder)) {
			  TCComponentFolder cff= (TCComponentFolder)cf;	
			  String childObjType=cff.getTypeObject().getName();
			  System.out.println("child folder type ===="+childObjType);
			  int f1=0;
			  for(String f:fs) {
				 System.out.println(" fs======"+f);
				 if(f.trim().equalsIgnoreCase(childObjType)) {
					 f1=1;
					 break;
				 }
			    }
			    if(f1==1) {
				  MessageBox.post(paramFrame, "非法操作,此文件夹不能剪切","Warning", 4);
			      return null;
			     }
	           }
	        }
	} 
   
    boolean bool = false;
    InterfaceAIFComponent interfaceAIFComponent = paramArrayOfAIFComponentContext[0].getComponent();
    if (interfaceAIFComponent instanceof TCComponentBOMViewRevision || interfaceAIFComponent instanceof com.teamcenter.rac.kernel.TCComponentBOMView) {
      TCPreferenceService tCPreferenceService = ((TCSession)interfaceAIFComponent.getSession()).getPreferenceService();
      if (tCPreferenceService != null && tCPreferenceService.isDefinitionExistForPreference("PS_allow_cut_in_pseudofolder") && tCPreferenceService.getLogicalValue("PS_allow_cut_in_pseudofolder").booleanValue())
        bool = true; 
    } 
    String str = "User_Inbox";
    int i = paramArrayOfAIFComponentContext.length;
    for (int b = 0; b < i; b++) {
      InterfaceAIFComponent interfaceAIFComponent1 = paramArrayOfAIFComponentContext[b].getParentComponent();
      InterfaceAIFComponent interfaceAIFComponent2 = paramArrayOfAIFComponentContext[b].getComponent();
      if (interfaceAIFComponent2 == null) {
        MessageBox.post(paramFrame, registry.getString("invalidSelection"), registry.getString("error.TITLE"), 4);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof TCComponentBOMViewRevision && (interfaceAIFComponent1 instanceof TCComponentItemRevision || (interfaceAIFComponent1 instanceof com.teamcenter.rac.kernel.TCComponentPseudoFolder && !bool)) && paramArrayOfAIFComponentContext[b].getContext() != null && paramArrayOfAIFComponentContext[b].getContext().toString().equals("structure_revisions")) {
        MessageBox.post(paramFrame, registry.getString("invalidSelectionBOMViewRev"), registry.getString("error.TITLE"), 4);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentBOMView && (interfaceAIFComponent1 instanceof com.teamcenter.rac.kernel.TCComponentItem || (interfaceAIFComponent1 instanceof com.teamcenter.rac.kernel.TCComponentPseudoFolder && !bool)) && paramArrayOfAIFComponentContext[b].getContext() != null && paramArrayOfAIFComponentContext[b].getContext().toString().equals("bom_view_tags")) {
        MessageBox.post(paramFrame, registry.getString("invalidSelectionBOMView"), registry.getString("error.TITLE"), 4);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentPseudoFolder) {
        MessageBox.post(paramFrame, String.valueOf(Utilities.trimString(registry.getString("invalidSelection"), ".")) + " -- " + interfaceAIFComponent2 + ".", registry.getString("command.TITLE"), 4);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentAssemblyArrangement && interfaceAIFComponent1 != null && interfaceAIFComponent1 instanceof TCComponentBOMViewRevision && paramArrayOfAIFComponentContext[b].getContext() != null && (paramArrayOfAIFComponentContext[b].getContext().toString().equals("TC_Arrangement") || paramArrayOfAIFComponentContext[b].getContext().toString().equals("TC_DefaultArrangement") || paramArrayOfAIFComponentContext[b].getContext().toString().equals("Fnd0AsSavedArrangement"))) {
        MessageBox.post(paramFrame, registry.getString("invalidSelectionAssemblyArrangement"), registry.getString("error.TITLE"), 4);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentIDCLine && interfaceAIFComponent1 instanceof com.teamcenter.rac.kernel.TCComponentIDCLine) {
        MessageBox.post(paramFrame, registry.getString("invalidSelection"), registry.getString("command.TITLE"), 1);
        return null;
      } 
      if (interfaceAIFComponent2 instanceof TCComponentBOMLine) {
        if (!BOMMarkupHelper.allowStructureEdit(AIFUtility.getActiveDesktop().getShell(), (TCComponentBOMLine)interfaceAIFComponent2))
          return null; 
        try {
          TCComponentBOMViewRevision tCComponentBOMViewRevision = ((TCComponentBOMLine)interfaceAIFComponent1).getBOMViewRevision();
          if (tCComponentBOMViewRevision != null && !tCComponentBOMViewRevision.okToModify()) {
            if (!this.parentsWithNoAccess.contains(tCComponentBOMViewRevision))
              this.parentsWithNoAccess.add(tCComponentBOMViewRevision); 
            continue;
          } 
        } catch (TCException tCException) {
          tCException.printStackTrace();
        } 
      } else if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentTaskInBox) {
        TCComponent tCComponent = (TCComponent)paramArrayOfAIFComponentContext[b].getParentComponent();
        if (tCComponent instanceof com.teamcenter.rac.kernel.TCComponentTaskInBox || tCComponent.getType().equals(str)) {
          MessageBox.post(paramFrame, String.valueOf(Utilities.trimString(registry.getString("invalidSelection"), ".")) + " -- " + interfaceAIFComponent2 + ".", registry.getString("command.TITLE"), 4);
          return null;
        } 
      } else if (interfaceAIFComponent1 instanceof com.teamcenter.rac.kernel.TCComponentTaskInBox) {
        boolean bool1 = true;
        if (interfaceAIFComponent2 instanceof TCComponentTask)
          try {
            if (((TCComponentTask)interfaceAIFComponent2).getState() == TCTaskState.COMPLETED)
              bool1 = false; 
          } catch (TCException tCException) {} 
        if (bool1) {
          MessageBox.post(paramFrame, String.valueOf(Utilities.trimString(registry.getString("invalidSelection"), ".")) + " -- " + interfaceAIFComponent2 + ".", registry.getString("command.TITLE"), 4);
          return null;
        } 
      } else if (interfaceAIFComponent2.getType().equals(str)) {
        TCComponent tCComponent = (TCComponent)paramArrayOfAIFComponentContext[b].getParentComponent();
        if (tCComponent == null) {
          MessageBox.post(paramFrame, String.valueOf(Utilities.trimString(registry.getString("invalidSelection"), ".")) + " -- " + interfaceAIFComponent2 + ".", registry.getString("command.TITLE"), 4);
          return null;
        } 
      } 
      if (interfaceAIFComponent2 instanceof com.teamcenter.rac.kernel.TCComponentArchitecture && interfaceAIFComponent1 instanceof TCComponentItemRevision && paramArrayOfAIFComponentContext[b].getContext().equals(TCUtilities.ARCHITECTURE_RELATION)) {
        try {
          TCComponentItemRevision tCComponentItemRevision = (TCComponentItemRevision)paramArrayOfAIFComponentContext[b].getParentComponent();
          TCComponentItemRevision[] arrayOfTCComponentItemRevision = TCUtilities.getAllContextRevisions(tCComponentItemRevision, null);
          Utilities.addElements(arrayList, (Object[])AIFComponentContext.getContexts((InterfaceAIFComponent[])arrayOfTCComponentItemRevision, paramArrayOfAIFComponentContext[b].getComponent(), paramArrayOfAIFComponentContext[b].getContext()), true);
        } catch (TCException tCException) {}
      } else {
        arrayList.add(paramArrayOfAIFComponentContext[b]);
      } 
      continue;
    } 
    if (this.parentsWithNoAccess.size() > 0) {
      StringBuffer stringBuffer = new StringBuffer();
      String str1 = ", ";
      for (byte b1 = 0; b1 < this.parentsWithNoAccess.size(); b1++) {
        if (b1 == this.parentsWithNoAccess.size() - 1)
          str1 = ""; 
        stringBuffer.append(this.parentsWithNoAccess.get(b1));
        stringBuffer.append(str1);
      } 
      MessageBox.post(paramFrame, String.valueOf(MessageFormat.format(registry.getString("inaccessibleBOMViewRev"), new Object[] { stringBuffer.toString() })) + " " + registry.getString("acessDenied"), registry.getString("error.TITLE"), 4);
    } 
    return arrayList.isEmpty() ? null : arrayList.<AIFComponentContext>toArray(new AIFComponentContext[arrayList.size()]);
  }
  
  private boolean showConfirmation(AIFComponentContext[] paramArrayOfAIFComponentContext) {
    if (paramArrayOfAIFComponentContext == null)
      return true; 
    Registry registry = Registry.getRegistry(this);
    int i = registry.getInt("confirmationLimit", 10);
    return (paramArrayOfAIFComponentContext.length > i);
  }
  
  
  @SuppressWarnings("deprecation")
  private static List<String> getArrayPreference(TCSession session, int scope, String preferenceName) {
		String[] array = session.getPreferenceService().getStringArray(scope, preferenceName);
		return Arrays.asList(array);
  }
  
}
