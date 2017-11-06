package org.ccloud.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.ccloud.Consts;
import org.joda.time.DateTime;

import com.beust.jcommander.internal.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jfinal.kit.PathKit;

public class QRCodeUtils {

	public static final String QRCODE_ABS_PATH = "abs_path";	// 二维码绝对路径
	public static final String QRCODE_REL_PATH = "rel_path";	// 二维码相对路径
	public static final String QRCODE_FILE_NAME = "file_name";	// 二维码文件名
	
	/**
	 * 功能描述：生成二维码
	 * @param contents	二维码内容
	 * @param imagePath	二维码图片地址
	 * 返回类型：void
	 * 创建人：eric
	 * 日期：2016年12月25日
	 */
	public static void genQRCode(String contents, String imagePath, String fileName) {
		
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		// 指定纠错等级
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.CHARACTER_SET, Consts.CHARTSET_UTF8);
		try {
			
			File file = new File(imagePath);
			if(!file.exists())
				file.mkdirs();
			
			BitMatrix bitMatrix = multiFormatWriter.encode(contents, BarcodeFormat.QR_CODE, 400, 400, hints);
			MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(imagePath + "/" + fileName));
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 功能描述：解析二维码
	 * @param fileName	二维码图片的地址
	 * @return
	 * 返回类型：Result
	 * 创建人：eric
	 * 日期：2016年12月25日
	 */
	public static Result parseQRCode(String fileName) {
		
		try {
			MultiFormatReader formatReader = new MultiFormatReader();
			File file = new File(fileName);
			BufferedImage image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
			hints.put(DecodeHintType.CHARACTER_SET, Consts.CHARTSET_UTF8);
			Result result = formatReader.decode(binaryBitmap, hints);
			System.out.println("result = "+ result.toString());
            System.out.println("resultFormat = "+ result.getBarcodeFormat());
            System.out.println("resultText = "+ result.getText());
            return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, String> qrcodeMap() {
		
		Map<String, String> map = Maps.newHashMap();
		String basePath = PathKit.getWebRootPath();
		String directory = DateUtils.dateToStr(DateTime.now().toDate(), DateUtils.DEFAULT_DIRECTORY_FORMATTER);
		String fileName = DateUtils.dateToStr(DateTime.now().toDate(), DateUtils.DEFAULT_FILE_NAME_FORMATTER);
		
		String relativePath = Consts.QRCODE_PATH + directory;
		map.put(QRCODE_REL_PATH, relativePath);
		map.put(QRCODE_FILE_NAME, fileName + ".png");
		map.put(QRCODE_ABS_PATH, basePath + relativePath);
		return map;
		
	}
	
	public static void main(String[] args) {
		String imagePath = "D:\\qrcode\\";
		String contents = "http://weixin.antrace.cn/member/findMemberByMobile?mobile=234";
		genQRCode(contents, imagePath, "1.png");
//		File file = new File(imagePath);
//		imagePath = "E:\\qrcode";
		parseQRCode(imagePath);
	}
}
