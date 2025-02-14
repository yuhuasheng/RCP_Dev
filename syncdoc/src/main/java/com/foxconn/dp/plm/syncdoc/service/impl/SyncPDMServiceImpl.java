package com.foxconn.dp.plm.syncdoc.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.log.LogFactory;
import com.foxconn.dp.plm.syncdoc.domain.*;
import com.foxconn.dp.plm.syncdoc.mapper.PDMDocMapper;
import com.foxconn.dp.plm.syncdoc.utils.MyBatisUtil;
import com.foxconn.dp.plm.syncdoc.utils.PropertitesUtil;
import org.apache.ibatis.session.SqlSession;
import java.util.*;

public class SyncPDMServiceImpl {


    private static SqlSession sqlSession = null;
    private static PDMDocMapper mapper = null;
    static String creator = "ByPDMSync";
    public static Properties config = PropertitesUtil.readPropertiesFile("/config.properties");
    static List<String> folderType = new ArrayList<>();
    static List<String> itemType = new ArrayList<>();
    static List<String> revType = new ArrayList<>();
    static {
        folderType.add("D9_TECHFolder");
        folderType.add("D9_ISOFolder");
        itemType.add("D9_TECH");
        itemType.add("D9_ISO");
        revType.add("D9_TECHRevision");
        revType.add("D9_ISORevision");
    }

    public static void syncISOFolder() {

        sqlSession = MyBatisUtil.getSqlSession();
        mapper = sqlSession.getMapper(PDMDocMapper.class);
        LogFactory.get().info("=======同步ISO开始=======");
        // 取出要同步的虚拟专案
        String virtualProjects = config.getProperty("VirtualProjects");
        if (virtualProjects == null || virtualProjects.isEmpty()) {
            return;
        }

        for (String vId : virtualProjects.split(",")) {
            syncProject(vId);
        }
        LogFactory.get().info("=======同步ISO结束=======");
    }

    static void syncProject(String vid) {

        // 从TC中查询专案
        PDMDocSyncInfo tcProject = mapper.queryTcProject(vid);
        if (tcProject == null) {
            return;
        }

        List<PDMDocSyncInfo> list = mapper.queryTcProjectContent(tcProject.getUid());
        for (PDMDocSyncInfo info : list) {
            info.setCreator(creator);
            info.setCreatorName(creator);
        }
        List<PDMDocSyncInfo> children = findChild(list, tcProject);
        tcProject.addChildren(children);

        // 移除单独的Rev
        removeSingleRev(tcProject);

        //从FS中查询专案
        PDMDocSyncInfo fsProject = mapper.queryFSProject(vid);
        if (fsProject == null) {
            fsProject = new PDMDocSyncInfo();
            fsProject.setType("Project");
            fsProject.setSpasId(vid);
            fsProject.setName(tcProject.getName());
        }else{
            //查询文件系统专案
            List<FSInfo> fsInfos = mapper.queryFSProjectContent(fsProject.getFsSn());
            findChildForFS(fsInfos, fsProject);
        }

        // 比对
        diff(tcProject, fsProject);
        sync(fsProject);
    }

    static void sync(PDMDocSyncInfo fsInfo) {

        if ("Project".equals(fsInfo.getType())) {
            // 查询专案
            Long exist = mapper.queryProjectTopFolderId(fsInfo.getSpasId());
            if (exist == null) {
                // 创建专案文件夹
                FolderEntity folder = new FolderEntity();
                folder.setFldName(fsInfo.getName());
                folder.setCreator(creator);
                folder.setRefId("/");
                folder.setCreated(new Date());
                mapper.insertFolder(folder);
                fsInfo.setFsSn(folder.getFldSn());
                // 创建专案
                ProjectEntity p = new ProjectEntity();
                p.setProjSpasId(fsInfo.getSpasId());
                p.setProjName(fsInfo.getName());
                p.setFolderId(folder.getFldSn());
                p.setCreated(new Date());
                mapper.insertProject(p);
                sqlSession.commit();
            }
        }

        List<PDMDocSyncInfo> children = fsInfo.getChildren();

        for (PDMDocSyncInfo child : children) {

            if (PDMDocSyncInfo.DIFF_TYPE_NEW == child.getDiffType()) {
                if (folderType.contains(child.getType())) {
                    try {
                        // 创建文件夹
                        FolderEntity folder = new FolderEntity();
                        folder.setFldName(child.getName());
                        folder.setCreator(child.getCreator());
                        folder.setRefId(child.getUid());
                        folder.setCreated(child.getCreated());
                        mapper.insertFolder(folder);
                        child.setFsSn(folder.getFldSn());
                        // 创建文件夹结构
                        FolderStructureEntity fs = new FolderStructureEntity();
                        fs.setFldId(child.getParent().getFsSn());
                        fs.setFldChildId(folder.getFldSn());
                        fs.setCreator(child.getCreator());
                        fs.setCreated(new Date());
                        mapper.insertFolderStructure(fs);
                    } catch (Exception e) {
                        LogFactory.get().error(e);
                    }
                }
                if (itemType.contains(child.getType())) {
                    try {
                        Long docId = mapper.queryFSDocument(child.getItemId());
                        if (docId == null) {
                            DocumentEntity doc = new DocumentEntity();
                            doc.setDocNum(child.getItemId());
                            doc.setDocOrigin(1L);
                            doc.setDocName(child.getName());
                            doc.setCreator(child.getCreator());
                            doc.setCreatorName(child.getCreatorName());
                            doc.setCreated(child.getCreated());
                            doc.setRefId(child.getUid());
                            mapper.insertDocument(doc);
                            child.setFsSn(doc.getDocSn());
                        } else {
                            child.setFsSn(docId);
                        }
                    } catch (Exception e) {
                        LogFactory.get().error(e);
                    }
                }
                if (revType.contains(child.getType())) {
                    try {
                        DocumentRevEntity rev = new DocumentRevEntity();
                        rev.setDocId(child.getParent().getFsSn());
                        rev.setFldId(child.getParent().getParent().getFsSn());
                        rev.setRevName(child.getName());
                        rev.setRevNum(child.getVersion());
                        rev.setRefId(child.getUid());
                        rev.setCreator(child.getParent().getCreator());
                        rev.setCreatorName(child.getParent(). getCreatorName());
                        rev.setStatus(child.getStatus());
                        rev.setCreated(child.getCreated());
                        mapper.insertDocumentRev(rev);
                    } catch (Exception e) {
                        LogFactory.get().error(e);
                    }
                }
            } else if (PDMDocSyncInfo.DIFF_TYPE_DELETE == child.getDiffType()) {
                deleteToFS(child);
            } else if (PDMDocSyncInfo.DIFF_TYPE_UPDATE == child.getDiffType()) {
                updateToFS(child);
            }
            sqlSession.commit();
        }
        for (PDMDocSyncInfo child : children) {
            sync(child);
        }
    }

    public static List<PDMDocSyncInfo> findChild(List<PDMDocSyncInfo> list, PDMDocSyncInfo parent) {
        List<PDMDocSyncInfo> children = new ArrayList<>();
        for (PDMDocSyncInfo child : list) {
            if (parent.getUid().equals(child.getPuid())) {
                child.setParent(parent);
                children.add(child);
                if (itemType.contains(child.getType())) {
                    List<PDMDocSyncInfo> itemRevisionList = mapper.queryItemRevision(child.getUid());
                    for (PDMDocSyncInfo revision : itemRevisionList) {
                        revision.setName(child.getName());
                        revision.setPuid(child.getUid());
                        revision.setParent(child);
                    }
                    child.addChildren(itemRevisionList);
                }
            }
        }
        for (PDMDocSyncInfo child : children) {
            child.addChildren(findChild(list, child));
        }
        return children;
    }

    public static void findChildForFS(List<FSInfo> list, PDMDocSyncInfo parent) {
        for (FSInfo row : list) {

            if (row.getPFId().equals(parent.getFsSn())) {
                if (row.getRevId() != null) {
                    // 检查文件夹是否存在
                    PDMDocSyncInfo folderChild = null;
                    for (PDMDocSyncInfo parentChild : parent.getChildren()) {
                        if (parentChild.getFsSn().equals(row.getFId())) {
                            folderChild = parentChild;
                            break;
                        }
                    }
                    if (folderChild == null) {
                        folderChild = new PDMDocSyncInfo();
                        folderChild.setFsSn(row.getFId());
                        folderChild.setPuid(row.getPFRefId());
                        folderChild.setUid(row.getFRefId());
                        folderChild.setName(row.getFName());
                        folderChild.setParent(parent);
                        folderChild.setType(folderType.get(0));
                        folderChild.setCreator(creator);
                        folderChild.setCreatorName(creator);
                        parent.addChild(folderChild);
                        findChildForFS(list, folderChild);
                    }
                    // 检查文档是否存在
                    PDMDocSyncInfo docChild = null;
                    for (PDMDocSyncInfo parentChild : folderChild.getChildren()) {
                        if (itemType.contains(parentChild.getType())) {
                            if (parentChild.getFsSn().equals(row.getDocId())) {
                                docChild = parentChild;
                                break;
                            }
                        }
                    }
                    if (docChild == null) {
                        //doc
                        docChild = new PDMDocSyncInfo();
                        docChild.setFsSn(row.getDocId());
                        docChild.setPuid(folderChild.getUid());
                        docChild.setUid(row.getDocRefId());
                        docChild.setName(row.getDocName());
                        docChild.setParent(folderChild);
                        docChild.setType(itemType.get(0));
                        docChild.setCreator(creator);
                        docChild.setCreatorName(creator);
                        folderChild.addChild(docChild);
                    }

                    // rev
                    PDMDocSyncInfo pdmChild = new PDMDocSyncInfo();
                    pdmChild.setFsSn(row.getRevId());
                    pdmChild.setPuid(docChild.getUid());
                    pdmChild.setUid(row.getRevRefId());
                    pdmChild.setName(row.getRevName());
                    pdmChild.setParent(docChild);
                    pdmChild.setType(revType.get(0));
                    pdmChild.setCreator(creator);
                    pdmChild.setCreatorName(creator);
                    docChild.addChild(pdmChild);
                } else {
                    // folder
                    PDMDocSyncInfo pdmChild = new PDMDocSyncInfo();
                    pdmChild.setFsSn(row.getFId());
                    pdmChild.setPuid(row.getPFRefId());
                    pdmChild.setUid(row.getFRefId());
                    pdmChild.setName(row.getFName());
                    pdmChild.setParent(parent);
                    pdmChild.setType(folderType.get(0));
                    pdmChild.setCreator(creator);
                    pdmChild.setCreatorName(creator);
                    parent.addChild(pdmChild);
                    findChildForFS(list, pdmChild);
                }
            }
        }
    }

    static boolean removeSingleRev(PDMDocSyncInfo tc){
        if (revType.contains(tc.getType())) {
            return !itemType.contains(tc.getParent().getType());
        }else {
            List<PDMDocSyncInfo> tcChildren = tc.getChildren();
            tcChildren.removeIf(SyncPDMServiceImpl::removeSingleRev);
        }
        return false;
    }

    static void diff(PDMDocSyncInfo tc, PDMDocSyncInfo fs) {

        List<PDMDocSyncInfo> tcChildren = tc.getChildren();
        List<PDMDocSyncInfo> fsChildren = fs.getChildren();
        List<Pair<PDMDocSyncInfo, PDMDocSyncInfo>> nextList = new ArrayList<>();
        int fsChildSize = 0;
        int deleteSize = 0;
        for (PDMDocSyncInfo fsChild : fsChildren) {
            fsChildSize++;
            PDMDocSyncInfo tcChild = findChildFromChildren(fsChild, tcChildren);
            if (tcChild == null) {
                // 删除
                fsChild.setDiffType(PDMDocSyncInfo.DIFF_TYPE_DELETE);
                deleteSize++;
            }
        }
        if (folderType.contains(fs.getType())) {
            if(fsChildSize!=0&&fsChildSize==deleteSize){
                // 当文件夹下所有文件被删除时，将父文件夹删除
                fs.setDiffType(PDMDocSyncInfo.DIFF_TYPE_DELETE);
            }
        }
        for (PDMDocSyncInfo tcChild : tcChildren) {
            PDMDocSyncInfo fsChild = findChildFromChildren(tcChild, fsChildren);
            if (fsChild == null) {
                // 新增
                setTypeNew(tcChild);
                tcChild.setParent(fs);
                fs.addChild(tcChild);
            } else {
                // 可能修改
                // 属性改变
                if (!fsChild.getName().equals(tcChild.getName())) {
                    fsChild.setName(tcChild.getName());
                    fsChild.setDiffType(PDMDocSyncInfo.DIFF_TYPE_UPDATE);
                }
                // 将需要进入下一层的子节点收集起来
                nextList.add(new Pair<>(tcChild, fsChild));
            }
        }
        // 进入下一层
        for (Pair<PDMDocSyncInfo, PDMDocSyncInfo> next : nextList) {
            diff(next.getKey(), next.getValue());
        }
    }

    private static void setTypeNew(PDMDocSyncInfo add) {
        add.setDiffType(PDMDocSyncInfo.DIFF_TYPE_NEW);
        for (PDMDocSyncInfo child : add.getChildren()) {
            setTypeNew(child);
        }
    }

    private static PDMDocSyncInfo findChildFromChildren(PDMDocSyncInfo child, List<PDMDocSyncInfo> children) {
        for (PDMDocSyncInfo echo : children) {
            if (echo.getUid().equals(child.getUid())) {
                return echo;
            }
        }
        return null;
    }

    static void updateToFS(PDMDocSyncInfo fsInfo) {
        try {
            if (folderType.contains(fsInfo.getType())) {
                mapper.updateFolder(fsInfo.getFsSn(),fsInfo.getName());
            } else if (itemType.contains(fsInfo.getType())) {
                mapper.updateDocument(fsInfo.getFsSn(),fsInfo.getName());
            }
        } catch (Exception e) {
            LogFactory.get().error(e);
        }
    }

    static void deleteToFS(PDMDocSyncInfo fsInfo) {
        try {
            if (folderType.contains(fsInfo.getType())) {
                mapper.deleteFolder(fsInfo.getFsSn());
            } else if (itemType.contains(fsInfo.getType())) {
                mapper.deleteDocument(fsInfo.getFsSn());
            } else {
                mapper.deleteDocumentRev(fsInfo.getFsSn());
            }
        } catch (Exception e) {
            LogFactory.get().error(e);
        }
        sqlSession.commit();

        for (PDMDocSyncInfo child : fsInfo.getChildren()) {
            deleteToFS(child);
        }
    }


}
