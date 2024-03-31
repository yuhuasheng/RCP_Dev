package com.hh.tools.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class ConnectPoolUtils {

    public static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";

    static {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
                SAPConstants.SAP_IP);
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
                SAPConstants.SAP_SYSTEMNUMBER);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
                SAPConstants.SAP_SITE);
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,
                SAPConstants.SAP_USERID);
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
                SAPConstants.SAP_USERPASSWD);
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,
                SAPConstants.SAP_LANGUAGE);
        connectProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
                SAPConstants.SAP_CODING);
        // JCO_PEAK_LIMIT - Maximum number of idle connections kept open by the
        connectProperties.setProperty(
                DestinationDataProvider.JCO_POOL_CAPACITY,
                SAPConstants.SAP_ALIVE_CONNECT);
        // JCO_POOL_CAPACITY - Maximum number of active connections that
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,
                SAPConstants.SAP_MAX_CONNECT);
        createDataFile(ABAP_AS_POOLED, "jcoDestination", connectProperties);
    }


    static void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        if (!cfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
                System.out.println("create pool  success !");
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to create the destination file "
                                + cfg.getName(), e);
            }
        }
    }

}
