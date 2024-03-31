package com.teamcenter.ets.custom.util;

import com.teamcenter.ets.soa.SoaHelper;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.workflow.WorkflowService;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.strong.workflow._2007_06.Workflow.SetReleaseStatusResponse;
import com.teamcenter.services.strong.workflow._2014_06.Workflow.PerformActionInputInfo;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.ReleaseStatus;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;
import com.teamcenter.tstk.util.log.ITaskLogger;
import java.util.HashMap;
import java.util.Map;

public class WorkflowUtil extends Util {
	 public static final String TASK_STATE_COMPLETED = "Completed";
	    private WorkflowService workflowService = WorkflowService.getService(SoaHelper.getSoaConnection());

	    public WorkflowUtil(ITaskLogger m_zTaskLogger) {
	        this.m_zTaskLogger = m_zTaskLogger;
	    }

	    public boolean completeWorkflowTask(String taskUID, String comment) {
	        this.m_zTaskLogger.info("#SCHAETTI# WorkflowUtil.completeWorkflowTask()");
	        boolean result = true;
	        if (taskUID != null && !taskUID.equals("")) {
	            try {
	                this.m_zTaskLogger.debug("Get model object from taskUID=" + taskUID);
	                ModelObject modelObject = SoaHelper.getModelObject(taskUID);
	                if (modelObject instanceof EPMTask) {
	                    EPMTask workflowTask = (EPMTask)modelObject;
	                    SoaHelper.refresh(workflowTask);
	                    SoaHelper.getProperties(workflowTask, new String[]{"task_type", "object_name", "task_state", "fnd0Assignee"});
	                    String taskType = workflowTask.get_task_type();
	                    String objectName = workflowTask.get_object_name();
	                    String taskState = workflowTask.get_task_state();
	                    ModelObject fnd0Assignee = workflowTask.get_fnd0Assignee();
	                    User assignee = (User)fnd0Assignee;
	                    String assigneeUserID = null;
	                    if (assignee != null) {
	                        SoaHelper.getProperties(assignee, "user_id");
	                        assigneeUserID = assignee.get_user_id();
	                    }

	                    this.m_zTaskLogger.debug("taskType=" + taskType);
	                    this.m_zTaskLogger.debug("objectName=" + objectName);
	                    this.m_zTaskLogger.debug("taskState=" + taskState);
	                    this.m_zTaskLogger.debug("assigneeUserID=" + assigneeUserID);
	                    String taskTypeNameUID = taskType + " \"" + objectName + "\", taskUID=" + taskUID;
	                    if (!taskState.equals("Completed")) {
	                        User loggedInUser = SoaHelper.getLoggedInUser();
	                        SoaHelper.getProperties(loggedInUser, "user_id");
	                        String loggedInUserID = loggedInUser.get_user_id();
	                        this.m_zTaskLogger.debug("loggedInUserID=" + loggedInUserID);
	                        if (assigneeUserID != null && !assigneeUserID.equals(loggedInUserID)) {
	                            this.m_zTaskLogger.info("Assign user for " + taskTypeNameUID + " from " + assigneeUserID + " to " + loggedInUserID);
	                            result &= this.performWorkflowAction(workflowTask, "SOA_EPM_assign_action", loggedInUser, "");
	                        }

	                        if (result) {
	                            this.m_zTaskLogger.info("Complete " + taskTypeNameUID + ", taskState=" + taskState);
	                            result &= this.performWorkflowAction(workflowTask, "SOA_EPM_complete_action", (User)null, comment);
	                        }
	                    } else {
	                        this.m_zTaskLogger.warn("The " + taskTypeNameUID + " is already in the state " + taskState);
	                    }

	                    SoaHelper.refresh(workflowTask);
	                } else {
	                    this.m_zTaskLogger.error("The object with the UID=" + taskUID + " is not an EPMTask.");
	                    result = false;
	                }
	            } catch (Exception var15) {
	                this.m_zTaskLogger.error("Exception: completeWorkflowTask() failed");
	                this.m_zTaskLogger.error(var15.getMessage());
	                result = false;
	            }
	        } else {
	            this.m_zTaskLogger.error("The taskUID is not set.");
	            result = false;
	        }

	        return result;
	    }

	    public boolean performWorkflowAction(EPMTask workflowTask, String workflowAction, User assignUser, String comment) {
	        this.m_zTaskLogger.info("#SCHAETTI# WorkflowUtil.performWorkflowAction()");
	        boolean result = false;
	        Map<String, String[]> propertyNameValues = new HashMap();
	        propertyNameValues.put("comments", new String[]{comment});
	        PerformActionInputInfo performActionInputInfo = new PerformActionInputInfo();
	        performActionInputInfo.action = workflowAction;
	        performActionInputInfo.actionableObject = workflowTask;
	        performActionInputInfo.propertyNameValues = propertyNameValues;
	        performActionInputInfo.supportingObject = assignUser;
	        performActionInputInfo.supportingValue = "SOA_EPM_completed";
	        PerformActionInputInfo[] performActionInputInfos = new PerformActionInputInfo[]{performActionInputInfo};
	        ServiceData serviceData = this.workflowService.performAction3(performActionInputInfos);
	        if (serviceData.sizeOfPartialErrors() > 0) {
	            this.displayPartialErrors(serviceData);
	        } else {
	            this.m_zTaskLogger.info("performAction3(): SUCCESS");
	            result = true;
	        }

	        return result;
	    }

	    public boolean setReleaseStatus(Dataset dataset, ModelObject newReleaseStatus) {
	        this.m_zTaskLogger.info("#BSH# WorkflowUtil.setReleaseStatus()");
	        boolean result = false;
	        String releaseStatusName = "";

	        try {
	            ReleaseStatus releaseStatus = (ReleaseStatus)newReleaseStatus;
	            SoaHelper.getProperties(releaseStatus, "name");
	            releaseStatusName = releaseStatus.get_name();
	            this.m_zTaskLogger.debug("releaseStatusName=" + releaseStatusName);
	        } catch (Exception var11) {
	            this.m_zTaskLogger.error("Could not determine the release status name. Error message is: ");
	            this.m_zTaskLogger.error(var11.getMessage());
	            return result;
	        }

	        ReleaseStatusOption releaseStatusOption = new ReleaseStatusOption();
	        releaseStatusOption.newReleaseStatusTypeName = releaseStatusName;
	        releaseStatusOption.operation = "Append";
	        releaseStatusOption.existingreleaseStatusTypeName = null;
	        ReleaseStatusOption[] releaseStatusOptions = new ReleaseStatusOption[]{releaseStatusOption};
	        ReleaseStatusInput releaseStatusInput = new ReleaseStatusInput();
	        releaseStatusInput.operations = releaseStatusOptions;
	        releaseStatusInput.objects = new WorkspaceObject[]{dataset};
	        ReleaseStatusInput[] releaseStatusInputs = new ReleaseStatusInput[]{releaseStatusInput};

	        try {
	            SetReleaseStatusResponse response = this.workflowService.setReleaseStatus(releaseStatusInputs);
	            if (response.serviceData.sizeOfPartialErrors() > 0) {
	                this.displayPartialErrors(response.serviceData);
	            } else {
	                this.m_zTaskLogger.info("setReleaseStatus(): SUCCESS");
	                result = true;
	            }
	        } catch (ServiceException var10) {
	            this.m_zTaskLogger.error("Exception: setReleaseStatus() failed");
	            this.m_zTaskLogger.error(var10.getMessage());
	        }

	        return result;
	    }

	    public String getReleaseStatusFlag(ItemRevision itemRevision) throws Exception {
	        this.m_zTaskLogger.info("#BSH# WorkflowUtil.getReleaseStatusFlag()");
	        String statusFlag = "";
	        SoaHelper.getProperties(itemRevision, "last_release_status");
	        ModelObject lastReleaseStatus = itemRevision.get_last_release_status();
	        if (lastReleaseStatus != null) {
	            ReleaseStatus releaseStatus = (ReleaseStatus)lastReleaseStatus;
	            SoaHelper.getProperties(releaseStatus, "object_name");
	            String releaseStatusName = releaseStatus.get_object_name();
	            this.m_zTaskLogger.debug("releaseStatusName=" + releaseStatusName);
	            if (releaseStatusName.equals("Frozen")) {
	                statusFlag = "F";
	            } else if (releaseStatusName.equals("Quotation")) {
	                statusFlag = "Q";
	            } else if (releaseStatusName.equals("Tool Order")) {
	                statusFlag = "T";
	            } else if (releaseStatusName.equals("Released")) {
	                statusFlag = "R";
	            } else if (releaseStatusName.equals("BS4_Invalid")) {
	                statusFlag = "I";
	            } else if (releaseStatusName.equals("Archiving")) {
	                statusFlag = "A";
	            } else if (releaseStatusName.equals("BS4_Cancelled")) {
	                statusFlag = "C";
	            } else if (releaseStatusName.equals("BS4_IndDesignReleased")) {
	                statusFlag = "D";
	            } else if (releaseStatusName.equals("Externally Managed")) {
	                statusFlag = "";
	            } else {
	                this.m_zTaskLogger.warn("Unknown release status: " + releaseStatusName);
	            }
	        }

	        this.m_zTaskLogger.info("statusFlag=" + statusFlag);
	        return statusFlag;
	    }
}
