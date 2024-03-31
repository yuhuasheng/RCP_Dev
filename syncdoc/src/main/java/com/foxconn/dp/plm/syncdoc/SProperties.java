package com.foxconn.dp.plm.syncdoc;

import cn.hutool.log.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Properties;

public class SProperties extends Properties {

    private final String  filePath;

    public SProperties(String filePath){
        this.filePath = filePath;
        try {
            Reader in = new BufferedReader(new FileReader(filePath));
            load(in);
            in.close();
        } catch (Exception e) {
            LogFactory.get().error(e);
            System.out.println("Error reading configuration file: "+filePath);
            System.exit(0);
        }
    }

    public void save() throws Exception {
        PrintWriter writer = new PrintWriter(filePath);
        store(writer, "NONE");
        writer.close();
    }

}
