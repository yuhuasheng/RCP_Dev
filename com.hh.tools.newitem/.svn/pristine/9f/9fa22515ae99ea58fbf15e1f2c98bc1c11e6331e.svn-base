package com.hh.tools.customerPanel;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ZIPFileFilter extends FileFilter {
    public String getDescription() {
        return "*.ZIP(7Z/RAR)";
    }

    public boolean accept(File file) {
        String name = file.getName();
        return file.isDirectory() || name.toLowerCase().endsWith(".zip")
                || name.toLowerCase().endsWith(".7z")
                || name.toLowerCase().endsWith(".rar");  // 仅显示压缩文件
    }
} 
