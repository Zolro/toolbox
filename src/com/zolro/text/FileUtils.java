package com.zolro.text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 一些文件夹相关的工具代码
 * @author King
 *
 */
public class FileUtils {
	public static void main(String[] args) throws Exception {
		File sourcepath = new File("D:\\测试目录");
		File savepath = new File("D:\\保存目录2");
		if(!savepath.exists()) {
			savepath.mkdir();
		}
		changeFileName(sourcepath,savepath);
	}
	/**
	 * 拷贝文件夹
	 * @param file 原文件夹
	 * @param path 保存目录
	 * @throws Exception
	 */
	public static void changeFileName(File file,File path) throws Exception {
		if(file.isDirectory()) {
			File dire =new File(path,file.getName());
			if(!dire.exists()) {
				dire.mkdir();
			}
			String filePath = file.getPath();
			File[] files = file.listFiles();
			for(File f:files) {
				changeFileName(f,dire);
			}			
		}else {
			File nfile = new File(path,file.getName());
			copyFile(file,nfile);
		}
	}
	/**
	 * 拷贝文件
	 * @param resource 原文件
	 * @param target 拷贝之后的文件
	 * @throws Exception
	 */
	public static void copyFile(File resource, File target) throws Exception {
        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();
        long end = System.currentTimeMillis();
    }
}
