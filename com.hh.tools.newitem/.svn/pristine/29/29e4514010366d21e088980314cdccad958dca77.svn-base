package com.hh.tools.util;

import org.eclipse.ui.IStartup;

/**
 *  页面启动时 加载初始化缓存数据
 * @author wangsf
 *
 */
public class InitCacheDataStartUp implements IStartup {

    @Override
    public void earlyStartup() {
        new Thread(new Runnable() {
            public void run() {
            	System.out.println("===================开始加载缓存数据了======================");

            	System.out.println("初始化 cisFileStorage 缓存数据");
				CISFileStorageUtil cisFileStorageUtil = CISFileStorageUtil.getInstance();
				cisFileStorageUtil.loadCISFileStorageData();
				System.out.println("初始化 cisFileStorage 缓存数据完成");
            }
        }).start();
    }

}
