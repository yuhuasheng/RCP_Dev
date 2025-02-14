package com.foxconn.dp.plm.syncdoc.utils;


import cn.hutool.log.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertitesUtil {
	
	public static Properties props;

    static {
        try {
            readPropertiesFile("/config.properties");
        } catch (Exception e) {
            LogFactory.get().error(e);
        }
    }

    public static Properties readPropertiesFile(String filePath){
        try {
        	InputStream resourceAsStream = PropertitesUtil.class.getResourceAsStream(filePath);
            props = new Properties();
            props.load(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
            return props;
        } catch (Exception e) {
            LogFactory.get().error(e);
            return null;
        }
    }
}
