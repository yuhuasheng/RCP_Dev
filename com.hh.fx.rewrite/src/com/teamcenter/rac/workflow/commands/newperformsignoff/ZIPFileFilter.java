package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ZIPFileFilter extends FileFilter {  
    public String getDescription() {  
        return "*.7z;*.zip";  
    }  
  
    public boolean accept(File file) {  
        String name = file.getName();  
        return file.isDirectory() || name.toLowerCase().endsWith(".7z")||name.toLowerCase().endsWith(".zip");  // 仅显示目录和OLB文件
    }  
} 
