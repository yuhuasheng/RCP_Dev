package com.foxconn.dp.plm.syncdoc.service;

import java.util.List;
import java.util.Map;

import com.foxconn.dp.plm.syncdoc.domain.DocRevInfo;
import com.foxconn.dp.plm.syncdoc.domain.DocRevInfo1;
import com.foxconn.dp.plm.syncdoc.domain.FolderStructure;

public interface SyncDocService {
	
	void syncFolders() throws Exception;
	
	void syncProjectFolder() throws Exception;

	void getProjectFolderStructureDifferenceData(List<String> spasIds) throws Exception;
	
	void syncFoldersToTC(String spasId, String tcProjectId,List<FolderStructure> toTCList) throws Exception;
	
	void syncFoldersFromTC(String spasId, int dmsProjectId,List<FolderStructure> toDocManSysList) throws Exception;
	
	void getProjectDocStructureDifferenceData(List<String> spasIds) throws Exception;
	
	void syncDocumentToTC(String spasId, int DMSProjectFolderId, String TCProjectFolderPUID, List<String> tcToDocManSysList) throws Exception;
	
	void syncDocumentFromTC(int DMSProjectFolderId, String TCProjectFolderPUID, List<String> tcToDocManSysList) throws Exception;
	
	void getProjectDocRevStructureDifferenceData(List<String> spasIds) throws Exception;
	
	void syncDocRevToTC(String spasId, String TCProjectFolderPUID, List<DocRevInfo> differenceData) throws Exception;
	
	void syncDocRevFromTC(int DMSProjectFolderId, List<DocRevInfo> differenceData) throws Exception;
	
	void getProjectDocRevIssueStateDifferenceData(List<String> spasIds) throws Exception;
	
	void syncDocRevIssueStateToTC(List<DocRevInfo1> differenceData) throws Exception;
	
	void syncDocRevIssueStateFromTC(List<DocRevInfo1> differenceData) throws Exception;
	
}
