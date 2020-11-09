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

	public final static String UPLOAD_PATH_PREFIX = "upload/";
	public static final String separator = File.separator;



	public static void main(String[] args) throws Exception {
		File sourcepath = new File("D:\\测试目录");
		File savepath = new File("D:\\保存目录2");
		if(!savepath.exists()) {
			savepath.mkdir();
		}
		changeFileName(sourcepath,savepath);
	}


	public static String createFilePath(String name) {
		String realPath = new String(System.getProperty("user.dir")+"/"+ UPLOAD_PATH_PREFIX);
		File filePath = new File(realPath);
		if (!filePath.isDirectory()) {
			filePath.mkdirs();
		}
		return filePath.getAbsolutePath() + File.separator + name;
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

	public static ResponseEntity<InputStreamResource> download(String fileName) {
		String route = "static/model";
		String path = null;
		ResponseEntity<InputStreamResource> response = null;
		try {
			path = route + separator + fileName;
			ClassPathResource classPathResource = new ClassPathResource(path);
			InputStream inputStream = classPathResource.getInputStream();
			//File file = new File(path);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Content-Disposition",
					"attachment; filename="
							+ fileName);
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			response = ResponseEntity.ok().headers(headers)
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(new InputStreamResource(inputStream));
		} catch (FileNotFoundException e1) {
			log.error("找不到指定的文件", e1);
		} catch (IOException e) {
			log.error("获取不到文件流", e);
		}
		return response;
	}

	/**
	 * 写入txt文件
	 *
	 * @param result
	 * @param fileName
	 * @return
	 */
	public static boolean writeDataHubData(String result, String fileName) {
		long start = System.currentTimeMillis();
		String filePath = "D:";
		StringBuilder content = new StringBuilder();
		boolean flag = false;
		BufferedWriter out = null;
		try {
			if (result != null && !result.isEmpty() && StringUtils.isNotEmpty(fileName)) {
				fileName += "_" + System.currentTimeMillis() + ".ftl";
				File pathFile = new File(filePath);
				if (!pathFile.exists()) {
					pathFile.mkdirs();
				}
				String relFilePath = filePath + File.separator + fileName;
				File file = new File(relFilePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));
				//标题头
//                out.write("curr_time,link_id,travel_time,speed,reliabilitycode,link_len,adcode,time_stamp,state,public_rec_time,ds");
//                out.newLine();

				out.write(result);
				out.newLine();

				flag = true;
//                logger.info("写入文件耗时：*********************************" + (System.currentTimeMillis() - start) + "毫秒");
				System.out.println("写入文件耗时：*********************************" + (System.currentTimeMillis() - start) + "毫秒");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return flag;
		}
	}
}
