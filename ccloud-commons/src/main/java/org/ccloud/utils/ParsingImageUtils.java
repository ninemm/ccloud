package org.ccloud.utils;

import java.io.File;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

//读取图片信息
public class ParsingImageUtils {
	
	public  String parsingImage(String imgUrl) throws Exception, Exception{  
		File file = new File(imgUrl);  
		Metadata metadata = ImageMetadataReader.readMetadata(file);  
		String content="";
		for (Directory directory : metadata.getDirectories()) {  
			for (Tag tag : directory.getTags()) {  
				String tagName = tag.getTagName();  //标签名
				String desc = tag.getDescription(); //标签信息
				if (tagName.equals("Date/Time Original")) {  
					content=content+"拍摄时间: "+desc;
				}else if (tagName.equals("GPS Latitude")) {  
					content=content+"  纬度 : "+desc;
				} else if (tagName.equals("GPS Longitude")) {  
					content=content+"  经度 : "+desc;
				}
			}  
		}
		return content;  
	}  
}
