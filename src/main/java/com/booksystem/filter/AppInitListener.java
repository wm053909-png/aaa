package com.booksystem.filter;

import com.booksystem.util.DBUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 应用启动监听器
 * 在 Web 应用启动时自动初始化数据库（建表 + 插入初始数据）
 */
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[AppInit] 正在初始化数据库...");
        DBUtil.initDatabase();
        System.out.println("[AppInit] 数据库初始化完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
