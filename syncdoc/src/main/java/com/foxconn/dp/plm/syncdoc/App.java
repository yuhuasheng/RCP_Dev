package com.foxconn.dp.plm.syncdoc;

import cn.hutool.log.LogFactory;
import com.foxconn.dp.plm.syncdoc.service.impl.SyncPDMServiceImpl;
import org.apache.ibatis.session.SqlSession;

import com.foxconn.dp.plm.syncdoc.mapper.SyncDocMapper;
import com.foxconn.dp.plm.syncdoc.service.impl.SyncDocServiceImpl;
import com.foxconn.dp.plm.syncdoc.teamcenter.clientx.AppXSession;
import com.foxconn.dp.plm.syncdoc.teamcenter.clientx.TCSystem;
import com.foxconn.dp.plm.syncdoc.utils.MyBatisUtil;
import com.foxconn.dp.plm.syncdoc.utils.PropertitesUtil;
import com.foxconn.dp.plm.syncdoc.utils.TCUtils;

public class App {

    public static SqlSession sqlSession = null;
    public static AppXSession tcSession = null;
    public static SyncDocMapper syncDocMapper = null;

    public static void main(String[] args) {

        String host = PropertitesUtil.props.getProperty("prd.host");
        String username = PropertitesUtil.props.getProperty("prd.username");
        String password = PropertitesUtil.props.getProperty("prd.password");

        sqlSession = MyBatisUtil.getSqlSession();
        syncDocMapper = sqlSession.getMapper(SyncDocMapper.class);

        //----------获取文件管理系统，数据来源是TC系统的数据(文档)------------
//		Log.log("开始同步TC文档创建者的工号、名字！");
//		List<ObjDocument> docs = syncDocMapper.getDocs();
//		System.out.println("docs：" + docs.size());
//    	List<ObjDocument> docInfos = new ArrayList<ObjDocument>(); 
//    	for (int i = 0; i < docs.size(); i++) {
//    		ObjDocument doc = docs.get(i);
//    		int docNo = doc.getDocNo();
//    		String docNum = doc.getDocNum();
//    		ObjDocument docInfo = syncDocMapper.getDocCreator(docNum);
//    		if(docInfo != null) {
//    			docInfo.setDocNo(docNo);
//    			docInfo.setDocNum(docNum);
//        		System.out.println("docCreator:" + docInfo);
//        		docInfos.add(docInfo);
//    		}
//		} 	
//    	syncDocMapper.setDocCreator(docInfos);
//		syncDocMapper.setDocRevCreator(docInfos);
//		sqlSession.commit();
//		Log.log("结束同步TC文档创建者的工号、名字！");
//    	if(sqlSession != null) {
//			MyBatisUtil.closeSqlSession(sqlSession);
//		}
        //----------获取文件管理系统，数据来源是TC系统的数据(文档)------------

        LogFactory.get().info("SyncDoc Start!");
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            syncDocMapper = sqlSession.getMapper(SyncDocMapper.class);
            SyncPDMServiceImpl.syncISOFolder();
        } catch (Exception e) {
            LogFactory.get().error(e);
        }

        try {
            tcSession = TCSystem.loginTC(host, username, password);
            SyncDocServiceImpl syncDocServiceImpl = new SyncDocServiceImpl();
            syncDocServiceImpl.syncFolders();
        } catch (Exception e) {
            LogFactory.get().error(e);
            TCUtils.sendExternalEmail("PRD:" + e.getMessage());
        } finally {
            if (sqlSession != null) {
                MyBatisUtil.closeSqlSession(sqlSession);
            }
            if (tcSession != null) {
                TCSystem.logoutTC(tcSession);
            }
        }
        LogFactory.get().info("SyncDoc End!");
    }
}
