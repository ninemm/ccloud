package org.ccloud.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import com.jfinal.kit.StrKit;

public class BarcodeUtils {

	public static final String DEFAULT_IMAGE_FORMAT = "image/png";
	
	/** 生成条码文件 **/
	public static File generateFile(String text, String path) {
		
		File file = new File(path);
		try {
			generate(text, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/** 生成字节  **/
	public static byte[] generate(String msg) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		generate(msg, baos);
		return baos.toByteArray();
	}
	
	public static void generate(String msg, OutputStream os) {
		
		if (StrKit.isBlank(msg) || os == null)
			return ;
		
		Code39Bean bean = new Code39Bean();
		
		// 精度
		int dpi = 150;
		// module 宽度
		double width = UnitConv.in2mm(1.0f / dpi);
		
		bean.setModuleWidth(width);
		bean.setWideFactor(3);
		bean.doQuietZone(false);
		
		try {
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(os, DEFAULT_IMAGE_FORMAT, dpi, 
					BufferedImage.TYPE_BYTE_BINARY,	false, 0);
			
			// 生成条形码
			bean.generateBarcode(canvas, msg);
			
			canvas.finish();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		
		String text = "11111111";
		String path = "D:\\barcode.png";
		generateFile(text, path);
		
	}
	
}
