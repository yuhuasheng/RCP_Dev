package com.hh.tools.newitem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileStreamUtil {

    private String fileName = "";

    // 打开流处理
    public PrintStream openStream(String path) {
        File f = new File(path);
        BufferedWriter bw = null;
        PrintStream stream = null;
        try {
            if (!f.exists())
                f.createNewFile();
//			FileWriter fw = new FileWriter(f);
//			
//			bw = new BufferedWriter(fw);
            stream = new PrintStream(f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return stream;
    }

    // 写入数据
    public void writeData(PrintStream write, String str) {
        try {
            write.println(str);
            System.out.println(str);
//			write.println(System.getProperty("line.separator"));
//			write.write(str);
//			write.write(System.getProperty("line.separator"));
            write.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 关闭流
    public void close(PrintStream write) {
        try {
            if (write != null) {
                write.flush();
                write.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 得到temp目录下的txt文件
    public String getTempPath(String name) {
        String s = System.getenv("TEMP") + File.separator + name + getNowTime()
                + ".txt";
        // s = "C:\\"+name+getNowTime()+ ".txt";
        fileName = name + getNowTime() + ".txt";
        return s;
    }

    public String getFilename() {
        return this.fileName;

    }

    // 获取系统当前时间
    public String getNowTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(new Date().getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss");
        return dateFormat.format(c.getTime());
    }

    public String getSuffix(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return suffix;
    }


}
