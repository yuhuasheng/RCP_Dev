package com.hh.tools.customerPanel;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class OLBFileFilter extends FileFilter {
    public String getDescription() {
        return "*.OLB";
    }

    public boolean accept(File file) {
        String name = file.getName();
        return file.isDirectory() || name.toLowerCase().endsWith(".olb");  // 仅显示目录和OLB文件
    }
} 
