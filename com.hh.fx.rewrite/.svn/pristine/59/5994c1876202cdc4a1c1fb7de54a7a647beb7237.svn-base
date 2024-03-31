package com.teamcenter.rac.workflow.commands.digitalsign;

import com.teamcenter.net.tcserverproxy.client.TSPException;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.kernel.TCCRDecision;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.complete.CompleteOperation;
import com.teamcenter.rac.workflow.commands.failure.FailureOperation;
import com.teamcenter.rac.workflow.commands.newperformsignoff.DecisionDialog;
import com.teamcenter.rac.workflow.commands.newperformsignoff.SignoffDecisionOperation;
import com.teamcenter.soa.client.PKCS7;

import java.awt.Window;
import java.io.IOException;

public class PerformTaskUtil
{
  private TCComponentTask task = null;
  private String commentsStr = null;
  private String passwordStr = null;
  private Window m_window = null;
  private String resultStr = null;
  private String operationType = null;
  TCComponent supportObject = null;
  TCCRDecision decision = null;
  TCSession session = null;
  AbstractAIFOperation m_operation = null;
  TCComponent[] attachments = null;
  DecisionDialog dialog = null;
  public static final String APPLY_DS_HANDLER = "EPM-apply-digital-signature";
  public static final String REQUEST_PKI_AUTHENTICATION_HANDLER = "EPM-request-PKI-authentication";
  public static final String COMPLETE_OPERATION = "COMPLETE_OPERATION";
  public static final String FAILURE_OPERATION = "FAILURE_OPERATION";
  public static final String SIGNOFF_DECISION_OPERATION = "SIGNOFF_DECISION_OPERATION";
  InterfaceAIFOperationListener m_lsnr;
  
  public PerformTaskUtil(Window paramWindow, TCComponentTask paramTCComponentTask, String paramString1, String paramString2, String paramString3)
  {
    m_window = paramWindow;
    task = paramTCComponentTask;
    commentsStr = paramString1;
    passwordStr = paramString2;
    resultStr = paramString3;
    session = task.getSession();
  }
  
  public PerformTaskUtil(Window paramWindow, TCComponentTask paramTCComponentTask, String paramString1, String paramString2, String paramString3, InterfaceAIFOperationListener paramInterfaceAIFOperationListener, String paramString4)
  {
    this(paramWindow, paramTCComponentTask, paramString1, paramString2, paramString3);
    m_lsnr = paramInterfaceAIFOperationListener;
    operationType = paramString4;
  }
  
  public PerformTaskUtil(DecisionDialog paramDecisionDialog, TCComponentTask paramTCComponentTask, TCComponent paramTCComponent, TCCRDecision paramTCCRDecision, String paramString1, String paramString2, InterfaceAIFOperationListener paramInterfaceAIFOperationListener)
  {
    this(paramDecisionDialog, paramTCComponentTask, paramString1, paramString2, paramTCCRDecision.toString(), paramInterfaceAIFOperationListener, "SIGNOFF_DECISION_OPERATION");
    supportObject = paramTCComponent;
    decision = paramTCCRDecision;
    dialog = paramDecisionDialog;
  }
  
  public void executeOperation()
  {
    try
    {
      TCTaskState localTCTaskState = task.getState();
      m_operation = getTaskOperation();
      if (localTCTaskState == TCTaskState.STARTED)
      {
        int i = 4;
        if (task.getTypeComponent().toString().equals("EPMPerformSignoffTask")) {
          i = 100;
        }
        Object localObject1 = null;
        Object localObject2 = null;
        TCComponentActionHandler[] arrayOfTCComponentActionHandler = task.getActionHandlers(i);
        for (int j = 0; j < arrayOfTCComponentActionHandler.length; j++) {
          if (arrayOfTCComponentActionHandler[j].getName().equals("EPM-apply-digital-signature")) {
            localObject1 = arrayOfTCComponentActionHandler[j];
          } else if (arrayOfTCComponentActionHandler[j].getName().equals("EPM-request-PKI-authentication")) {
            localObject2 = arrayOfTCComponentActionHandler[j];
          }
        }
        if (localObject1 != null)
        {
          if ((m_operation instanceof CompleteOperation)) {
            ((CompleteOperation)m_operation).setDigitalSignatureContext(null);
          } else if ((m_operation instanceof SignoffDecisionOperation)) {
            ((SignoffDecisionOperation)m_operation).setDigitalSignatureContext(null);
          }
          session.queueOperation(m_operation);
        }
        else if (localObject2 != null)
        {
          String str = performSmartCardAuthentication();
          if (str != null)
          {
            if ((m_operation instanceof CompleteOperation)) {
              ((CompleteOperation)m_operation).setDigitalSignatureContext(str);
            } else if ((m_operation instanceof FailureOperation)) {
              ((FailureOperation)m_operation).setDigitalSignatureContext(str);
            } else if ((m_operation instanceof SignoffDecisionOperation)) {
              ((SignoffDecisionOperation)m_operation).setDigitalSignatureContext(str);
            }
            session.queueOperation(m_operation);
          }
        }
        else
        {
          session.queueOperation(m_operation);
        }
      }
      else if (localTCTaskState == TCTaskState.COMPLETED)
      {
        session.queueOperation(m_operation);
      }
    }
    catch (TCException localTCException)
    {
      MessageBox.post(localTCException);
    }
  }
  
  private AbstractAIFOperation getTaskOperation()
  {
    AbstractAIFOperation localAbstractAIFOperation = null;
    if ((operationType == null) || (operationType.equals("COMPLETE_OPERATION"))) {
      localAbstractAIFOperation = getCompleteOperation();
    } else if (operationType.equals("SIGNOFF_DECISION_OPERATION")) {
      localAbstractAIFOperation = getSignoffDecisionOperation();
    } else if (operationType.equals("FAILURE_OPERATION")) {
      localAbstractAIFOperation = getFailureOperation();
    }
    if (m_lsnr != null) {
      localAbstractAIFOperation.addOperationListener(m_lsnr);
    }
    return localAbstractAIFOperation;
  }
  
  public AbstractAIFOperation getOperation()
  {
    return m_operation;
  }
  
  private AbstractAIFOperation getSignoffDecisionOperation()
  {
    SignoffDecisionOperation localSignoffDecisionOperation = new SignoffDecisionOperation(session, dialog, task, (TCComponentSignoff)supportObject, decision, commentsStr, passwordStr);
    return localSignoffDecisionOperation;
  }
  
  private AbstractAIFOperation getCompleteOperation()
  {
    CompleteOperation localCompleteOperation = new CompleteOperation(m_window, new TCComponentTask[] { task }, commentsStr, passwordStr, resultStr);
    return localCompleteOperation;
  }
  
  private AbstractAIFOperation getFailureOperation()
  {
    FailureOperation localFailureOperation = new FailureOperation(m_window, new TCComponentTask[] { task }, commentsStr, passwordStr, resultStr);
    return localFailureOperation;
  }
  
  private String performSmartCardAuthentication()
  {
    String str = null;
    try
    {
      str = PKCS7.sign(session.getUserName());
    }
    catch (TSPException localTSPException)
    {
      MessageBox.post(localTSPException);
    }
    catch (IOException localIOException)
    {
      MessageBox.post(localIOException);
    }
    return str;
  }
}

/* Location:           D:\开发工具\开发库\tc11.3\plugins\com.teamcenter.rac.tcapps_11000.2.0.jar
 * Qualified Name:     com.teamcenter.rac.workflow.commands.digitalsign.PerformTaskUtil
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */