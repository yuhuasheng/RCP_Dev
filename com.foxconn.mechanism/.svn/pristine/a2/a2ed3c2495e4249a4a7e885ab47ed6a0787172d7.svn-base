package com.foxconn.mechanism.downloadrename;


import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.extra.compress.extractor.StreamExtractor;

/**
 *  电子原理图下载并重命名
 * @author 范建军
 *
 */
public class DownloadRenameAction extends AbstractAIFAction{
	
	private AbstractAIFApplication app = null;
	
	public DownloadRenameAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
	}

	@Override
	public void run() {
		String prefix = getStringPreference((TCSession)app.getSession(),TCPreferenceService.TC_preference_site,"FX8_prefix");
		StreamExtractor se = null;
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
			Frame frame = AIFDesktop.getActiveDesktop().getFrame();
			JDialog jDialog = new JDialog(frame);
			jDialog.setModal(true);
	        jDialog.setBounds(0,0,100,100);
	        jDialog.setVisible(true);
			InterfaceAIFComponent[] aifCom = app.getTargetComponents();
			// 限制只選一個
			if(aifCom.length != 1){
				MessageBox.post("只能选取一行", "提示", MessageBox.WARNING);
				return;
			}
			InterfaceAIFComponent aif = aifCom[0];
			if(!(aif instanceof TCComponentDataset)){
				MessageBox.post("只能对数据集进行操作", "提示", MessageBox.ERROR);
				return;
			}
			
			TCComponentDataset dataset = (TCComponentDataset) aif;
			AIFComponentContext[] list = dataset.whereReferenced();
			prefix = list[0].getComponent().getProperty("object_name");
			dataset.refresh();
			String fileName = "";
			int compressCount = 0;
			for (String name : dataset.getFileNames(null)) {
				if(name.toUpperCase().endsWith(".zip".toUpperCase())
					|| name.toUpperCase().endsWith(".rar".toUpperCase())
					|| name.toUpperCase().endsWith(".7z".toUpperCase())) {
					compressCount++;
					fileName = name;
				}
				System.out.println(name);
			}
			if(compressCount==0) {
				MessageBox.post("数据集没有压缩文件无法操作", "提示", MessageBox.WARNING);
				return;
			}
			if(compressCount>1) {
				MessageBox.post("数据集内仅有一个压缩文件时才能操作", "提示", MessageBox.WARNING);
				return;
			}
			File compressZip = dataset.getFile(null, fileName);
			System.out.println(compressZip.getAbsolutePath());
			File dir = fileDialogExport();
			System.out.println(dir.getAbsolutePath());
			File dest = new File(dir.getAbsolutePath()+java.io.File.separator+fileName);
			if(dest.exists()) {
				MessageBox.post("请先删除上次下载的文件："+fileName, "提示", MessageBox.ERROR);
				return;
			}
			dest = FileUtil.copy(compressZip, dest, true);
			if(!dest.exists()) {
				MessageBox.post("文件下载失败", "提示", MessageBox.ERROR);
				return;
			}
			uncompress(dest.getAbsolutePath(),dest.getParent()+File.separator+fileName.split("\\.")[0],prefix);
//			uncompress("C:\\Users\\Administrator\\Desktop\\test\\test.rar",dest.getParent(),prefix);
			MessageBox.post("操作完成", "提示", MessageBox.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(se!=null) {
				se.close();
			}
		}
	}
	
	/**
	 * 文件导出路径
	 * 
	 * @return
	 */
	private File fileDialogExport() {
		try {
			JFileChooser jf = new JFileChooser();
			jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = jf.showOpenDialog(jf);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = jf.getSelectedFile();
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void uncompress(String compressFile,String targetDir,String prefix) {
		try {
			File dir = new File(targetDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if(compressFile.toUpperCase().endsWith(".zip".toUpperCase())) {
				unZip(compressFile,targetDir,prefix);
			}
			if(compressFile.toUpperCase().endsWith(".rar".toUpperCase())) {
				MessageBox.displayModalMessageBox(null,"文件已下载，但无法解压RAR格式，只支持zip,7z格式","错误", MessageBox.ERROR);
				return;
			}
			if(compressFile.toUpperCase().endsWith(".7z".toUpperCase())) {
				un7z(compressFile,targetDir,prefix);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("文件解压失败，请检查文件是否被占用", "提示", MessageBox.ERROR);
		}
	}
	
	private void unZip(String zipFile, String targetDir, String prefix) throws Exception {
        ZipFile zip = new ZipFile(zipFile);
        try {
            File dir = new File(targetDir);
            Enumeration<? extends ZipEntry> en = zip.entries();
            while (en.hasMoreElements()) {
                ZipEntry zipEntry = en.nextElement();
                if (zipEntry.isDirectory()) {
                    continue;
                }
                String name = zipEntry.getName();
                List<String> collect = Arrays.stream(name.split(name.contains("/")?"/":"\\\\")).map(item->prefix + "-"+item).collect(Collectors.toList());
                name = String.join(File.separator,collect );
                File destFile = new File(dir,name);
                if (!destFile.exists()) {
                    new File(destFile.getParent()).mkdirs();
                    InputStream inputStream = zip.getInputStream(zipEntry);
                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);
                    IoUtil.copy(inputStream,fileOutputStream);
                    fileOutputStream.close();
                }
                System.out.println(destFile.getAbsolutePath());
            }
        } finally {
            zip.close();
        }
    }

    public void un7z(String inputFile, String destDirPath,String prefix) throws Exception {
        SevenZFile zIn = null;
        try {
            File srcFile = new File(inputFile);//获取当前压缩文件
            //开始解压
            zIn = new SevenZFile(srcFile);
            SevenZArchiveEntry entry;
            File file;
            while ((entry = zIn.getNextEntry()) != null) {
                System.out.println(entry.getName());
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    List<String> collect = Arrays.stream(name.split(name.contains("/")?"/":"\\\\")).map(item->prefix + "-"+item).collect(Collectors.toList());
                    name = String.join(File.separator,collect );
                    file = new File(destDirPath,name);
                    if (!file.exists()) {
                        new File(file.getParent()).mkdirs();//创建此文件的上级目录
                    }
                    FileOutputStream bos = new FileOutputStream(file);
                    int len = -1;
                    byte[] buf = new byte[4096];
                    while ((len = zIn.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    bos.close();
                }
            }
        } finally {
            // 关流顺序，先打开的后关闭
            if (zIn != null) {
                zIn.close();
            }
        }
    }
    
  	public String getStringPreference(TCSession session, int scope,
  			String preferenceName) {
  		TCPreferenceService preferenceService = session.getPreferenceService();
  		String strValue = preferenceService.getString(scope, preferenceName);
  		return strValue;

  	}
}
