package com.foxconn.electronics.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertitesUtil {
	
	public static Properties props;

    static {
        try {
            readPropertiesFile("/jdbc.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties readPropertiesFile(String filePath) throws FileNotFoundException, IOException {
        InputStream inputStream = null;
        try {
        	InputStream resourceAsStream = PropertitesUtil.class.getResourceAsStream(filePath);
            props = new Properties();
            props.load(new InputStreamReader(resourceAsStream, "UTF-8"));
            return props;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
