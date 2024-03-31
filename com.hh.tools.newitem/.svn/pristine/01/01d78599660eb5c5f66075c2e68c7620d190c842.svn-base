package com.hh.tools.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 选择文件过滤器
 * @author wangsf
 *
 */
public class SelectFileFilter extends FileFilter {

	// 选择文件的格式
    private String fileFormat = null;

    public SelectFileFilter() {
        super();
    }

    public SelectFileFilter(String fileFormat) {
        super();
        this.fileFormat = fileFormat;
    }

    public boolean accept(File paramFile) {
        if (paramFile.isFile()) {

            if (null != this.fileFormat && !"".equals(this.fileFormat)) {
                String name = paramFile.getName();
                String fileType = "";
                if (name.lastIndexOf(".") != -1) {
                    fileType = name.substring(name.lastIndexOf("."), name.length());
                    fileType = fileType.toLowerCase();

                    return fileFormat.contains(fileType);
                } else {
                    return false;
                }
            } else {
                return true;
            }

        } else {
            return paramFile.isDirectory();
        }
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getDescription() {
        return this.fileFormat;
    }
}
