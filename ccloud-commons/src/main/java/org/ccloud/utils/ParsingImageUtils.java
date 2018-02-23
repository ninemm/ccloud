package org.ccloud.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

//读取图片信息
public class ParsingImageUtils {
	
	public  String parsingImage(String imgUrl) throws Exception, Exception{  
		byte[] readInputStream = readInputStream(imgUrl);
		InputStream sbs = new ByteArrayInputStream(readInputStream); 
		Metadata metadata = ImageMetadataReader.readMetadata(sbs);
		String content="";
		for (Directory directory : metadata.getDirectories()) {  
			for (Tag tag : directory.getTags()) {  
				String tagName = tag.getTagName();  //标签名
				String desc = tag.getDescription(); //标签信息
				if (tagName.equals("Date/Time Original")) {  
					content=content+"拍摄时间: "+desc;
				}else if (tagName.equals("GPS Latitude")) {  
					content=content+"  纬度 : "+pointToLatlong(desc);
				} else if (tagName.equals("GPS Longitude")) {  
					content=content+"  经度 : "+pointToLatlong(desc);
				}
			}  
		}
		return content;  
	}  
	
	
	/**
	 * 读取远程图片
	 * @param imgurl
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(String imgurl) throws Exception {
		// new一个URL对象
		URL url = new URL(imgurl);
		// 打开链接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置请求方式为"GET"
		conn.setRequestMethod("GET");
		// 超时响应时间为5秒
		conn.setConnectTimeout(5 * 1000);
		// 通过输入流获取图片数据
		InputStream inStream = conn.getInputStream();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 创建一个Buffer字符串
		byte[] buffer = new byte[1024];
		// 每次读取的字符串长度，如果为-1，代表全部读取完毕
		int len = 0;
		// 使用一个输入流从buffer里把数据读取出来
		while ((len = inStream.read(buffer)) != -1) {
			// 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
			outStream.write(buffer, 0, len);
		}
		// 关闭输入流
		inStream.close();
		// 把outStream里的数据写入内存
		return outStream.toByteArray();
	}
	
	
	 /** 
	 * 经纬度格式  转换为  度分秒格式 ,如果需要的话可以调用该方法进行转换
	 * @param point 坐标点 
	 * @return 
	 */ 
	public static String pointToLatlong (String point ) {  
		Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());  
		Double fen = Double.parseDouble(point.substring(point.indexOf("°")+1, point.indexOf("'")).trim());  
		Double miao = Double.parseDouble(point.substring(point.indexOf("'")+1, point.indexOf("\"")).trim());  
		Double duStr = du + fen / 60 + miao / 60 / 60 ;  
		return duStr.toString();  
	}  
}
