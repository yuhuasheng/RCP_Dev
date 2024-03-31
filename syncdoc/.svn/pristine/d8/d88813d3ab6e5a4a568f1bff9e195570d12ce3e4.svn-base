package com.foxconn.dp.plm.syncdoc.utils;

import java.io.IOException;
import java.io.InputStream;

import cn.hutool.log.LogFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	
	static SqlSessionFactory sqlSessionFactory = null;

	static {
		try {
			String resource = "mybatis-config.xml";
			InputStream inputStream = Resources.getResourceAsStream(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			LogFactory.get().error(e);
		}
	}

	public static SqlSession getSqlSession() {
		return sqlSessionFactory.openSession();
	}

	public static void closeSqlSession(SqlSession sqlSession){
		if (sqlSession != null) {
			sqlSession.close();
		}
	}
}
