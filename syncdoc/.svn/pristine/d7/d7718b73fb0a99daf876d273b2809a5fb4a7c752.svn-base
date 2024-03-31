package com.foxconn.dp.plm.syncdoc.mapper;

import com.foxconn.dp.plm.syncdoc.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PDMDocMapper {

    PDMDocSyncInfo queryTcProject(String vId);
    List<PDMDocSyncInfo> queryTcProjectContent(@Param("puid")String puid);
    List<PDMDocSyncInfo> queryItemRevision(@Param("itemId")String itemId);
    List<PDMDocSyncInfo> queryDataset(@Param("revId")String revId);
    PDMDocSyncInfo queryFSProject(String vId);
    List<FSInfo> queryFSProjectContent(Long topFolderId);
    Long queryProjectTopFolderId(String spasId);
    Long queryFSDocument(String docNum);
    Integer insertFolder(FolderEntity folder);
    Integer insertProject(ProjectEntity p);
    Integer insertFolderStructure(FolderStructureEntity fs);
    Integer insertDocument(DocumentEntity doc);
    Integer insertDocumentRev(DocumentRevEntity doc);
    Integer deleteFolder(Long id);
    Integer deleteDocument(Long id);
    Integer deleteDocumentRev(Long id);
    Integer updateFolder(@Param("id")Long id,@Param("name")String name);
    Integer updateDocument(@Param("id")Long id,@Param("name")String name);

}
