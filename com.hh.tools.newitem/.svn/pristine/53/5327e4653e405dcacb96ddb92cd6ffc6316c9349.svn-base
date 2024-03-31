package com.hh.tools.newitem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;

public class DownloadDataset {

    public static String downloadFile(TCComponentDataset dataset, boolean isCopy) {
        // TODO Auto-generated method stub
        try {
            TCComponentTcFile[] tcfiles = dataset.getTcFiles();
            if (tcfiles == null || tcfiles.length == 0) {
                return "";
            }
            String temppath = System.getProperty("java.io.tmpdir");
            String filename = "";
            File newfile = null;
            File tempfile = null;
            for (int i = 0; i < tcfiles.length; i++) {
                TCComponentTcFile onetcfile = tcfiles[i];
                filename = onetcfile.getProperty("original_file_name");
                tempfile = onetcfile.getFmsFile();
                if (isCopy) {
                    newfile = new File(temppath + File.separator + filename);
                    copyFile(tempfile, newfile);
                }

            }
            if (isCopy) {
                return newfile.getAbsolutePath();
            } else {
                return tempfile.getAbsolutePath();
            }

        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
        	// 新建文件输入流并对它进行缓冲
        	inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
        	// 新建文件输出流并对它进行缓冲
        	outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
        	// 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
         	outBuff.flush();
         } finally {
        	 // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    public static String downloadFile(TCComponentDataset dataset,
                                      boolean isCopy, String folderPath) {
        // TODO Auto-generated method stub
        try {
            TCComponentTcFile[] tcfiles = dataset.getTcFiles();
            if (tcfiles == null || tcfiles.length == 0) {
                return "";
            }
            String temppath = System.getProperty("java.io.tmpdir");
            String filename = "";
            File newfile = null;
            File tempfile = null;
            for (int i = 0; i < tcfiles.length; i++) {
                TCComponentTcFile onetcfile = tcfiles[i];
                filename = onetcfile.getProperty("original_file_name");
                tempfile = onetcfile.getFmsFile();
                if (isCopy) {
                    if (folderPath != null || "".equals(folderPath)) {
                        newfile = new File(folderPath + File.separator + filename);
                        copyFile(tempfile, newfile);
                    } else {
                        newfile = new File(temppath + File.separator + filename);
                        copyFile(tempfile, newfile);
                    }
                }

            }
            if (isCopy) {
                return newfile.getAbsolutePath();
            } else {
                return tempfile.getAbsolutePath();
            }

        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String downloadFile(File file, File newFile) {
        try {
            copyFile(file, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
