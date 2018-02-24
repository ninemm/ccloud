package org.ccloud.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
//读取图片信息
public class ParsingImageUtils {
	
	public Map<String, String> parsingImage(String imgUrl) throws Exception, Exception{  
		byte[] readInputStream = readInputStream(imgUrl);
		InputStream sbs = new ByteArrayInputStream(readInputStream); 
		Metadata metadata = ImageMetadataReader.readMetadata(sbs);
		Map<String, String>map=new HashMap<>();
		for (Directory directory : metadata.getDirectories()) {  
			for (Tag tag : directory.getTags()) {  
				String tagName = tag.getTagName();  //标签名
				String desc = tag.getDescription(); //标签信息
				if (tagName.equals("Date/Time Original")) {  
					map.put("Time", desc);
				}else if (tagName.equals("GPS Latitude")) {  
					map.put("Latitude", pointToLatlong(desc));
				} else if (tagName.equals("GPS Longitude")) { 
					map.put("Longitude", pointToLatlong(desc));
				}
			}  
		}
		if (map.size()!=3) {
			return null;
		}
		double[] latlng = postBaidu(map.get("Longitude"), map.get("Latitude"));
		map.put("Latitude", String.valueOf(latlng[0]));
		map.put("Longitude", String.valueOf(latlng[1]));
		return map;  
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
	
	 //GPS坐标转百度坐标
	public static double[] postBaidu(String lng, String lat) {
		double[] latlng = null;

		URL url = null;
		URLConnection connection = null;
		try {
			url = new URL("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + lng + "&y="+ lat);
			connection = url.openConnection();
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(1000);
			connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
			out.flush();
			out.close();

			// 服务器的回应的字串，并解析
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				if (!sCurrentLine.equals(""))
					sTotalString += sCurrentLine;
			}
			// System.out.println(sTotalString);
			sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
			// System.out.println(sTotalString);
			String[] results = sTotalString.split("\\,");
			if (results.length == 3) {
				if (results[0].split("\\:")[1].equals("0")) {
					String mapX = results[1].split("\\:")[1];
					String mapY = results[2].split("\\:")[1];
					mapX = mapX.substring(1, mapX.length() - 1);
					mapY = mapY.substring(1, mapY.length() - 1);
					mapX = new String(Base64.decode(mapX));
					mapY = new String(Base64.decode(mapY));
					// System.out.println(mapX);
					// System.out.println(mapY);
					latlng = new double[] { Double.parseDouble(mapX), Double.parseDouble(mapY) };
				} else {
					System.out.println("error != 0");
				}
			} else {
				System.out.println("String invalid!");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("GPS转百度坐标异常！");
		}
		return latlng;
	}
}
