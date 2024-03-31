package com.foxconn.dp.plm.syncdoc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.log.LogFactory;
import com.foxconn.dp.plm.syncdoc.service.SyncDocService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: SyncRunnable
 * @Description:
 * @Author DY
 * @Create 2023/3/16
 */
public class SyncRunnable implements Runnable {
    private String spasId;
    private CountDownLatch countDownLatch;
    private SyncDocService syncDocService;

    public SyncRunnable(String spasId, CountDownLatch countDownLatch, SyncDocService syncDocService) {
        this.spasId = spasId;
        this.countDownLatch = countDownLatch;
        this.syncDocService = syncDocService;
    }

    @Override
    public void run() {
        LogFactory.get().info("开始同步专案 ----->  TC系统专案唯一标识【SPAS_ID】：" +spasId);
        try {
            List<String> spasIdList = CollUtil.newArrayList(spasId);
            syncDocService.syncProjectFolder();
            syncDocService.getProjectFolderStructureDifferenceData(spasIdList);
            syncDocService.getProjectDocStructureDifferenceData(spasIdList);
            syncDocService.getProjectDocRevStructureDifferenceData(spasIdList);
            syncDocService.getProjectDocRevIssueStateDifferenceData(spasIdList);
        }catch (Exception e){
            LogFactory.get().error("----->专案：" +spasId + "同步出错");
            //LogFactory.get().error("----->专案：" +spasId + "同步出错，错误信息",e);
        }finally {
            countDownLatch.countDown();
            LogFactory.get().info("----->专案：" +spasId + "同步完成");
        }
    }

}
