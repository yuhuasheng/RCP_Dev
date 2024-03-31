package com.hh.tools.customerPanel;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FootprintZIPFileFilter extends FileFilter {
    public String getDescription() {
        return "*.ZIP";
    }

    public boolean accept(File file) {
        String name = file.getName();
        return file.isDirectory() || name.toLowerCase().endsWith(".zip");  // 仅显示压缩文件
    }
} 
