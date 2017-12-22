package org.ccloud.qiniu;

import java.io.ByteArrayInputStream;

import com.google.gson.Gson;
import com.jfinal.kit.LogKit;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

public class QiniuKit {

	static QiniuConfig qConfig;
	
	static void init(QiniuConfig config) {
		qConfig = config;
	}
	
	public static DefaultPutRet put(String bucket, String key, String localFilePath) {
		
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadManager = new UploadManager(cfg);
		String token = qConfig.getToken(bucket);
		
		try {
			Response response = uploadManager.put(localFilePath, key, token);
			//解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		    return putRet;
		} catch (QiniuException e) {
			e.printStackTrace();
			Response r = e.response;
			try {
				LogKit.error(r.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
				return null;
			}
			return null;
		}
	}
	
	public static DefaultPutRet put(String bucket, String key, byte[] uploadBytes) {
		
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadManager = new UploadManager(cfg);
		String token = qConfig.getToken(bucket);
		
		try {
			Response response = uploadManager.put(uploadBytes, key, token);
			//解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		    return putRet;
		} catch (QiniuException e) {
			e.printStackTrace();
			Response r = e.response;
			try {
				LogKit.error(r.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
				return null;
			}
			return null;
		}
	}
	
	public static DefaultPutRet put(String bucket, String key, ByteArrayInputStream byteInputStream) {
		
		Configuration cfg = new Configuration(Zone.zone0());
		UploadManager uploadManager = new UploadManager(cfg);
		String token = qConfig.getToken(bucket);
		
		try {
			Response response = uploadManager.put(byteInputStream, key, token, null, null);
			//解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		    return putRet;
		} catch (QiniuException e) {
			e.printStackTrace();
			Response r = e.response;
			try {
				LogKit.error(r.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
				return null;
			}
			return null;
		}
	}
	
	public String getPublicUrl(String domain, String bucket, String filename) {
		String publicUrl = String.format("%s/%s/%s", domain, bucket, filename);
		Auth auth = qConfig.getAuth();
		long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
		String url = auth.privateDownloadUrl(publicUrl, expireInSeconds);
		return url;
	}
	
	public static Response delete(String bucket, String key) {
		Configuration cfg = new Configuration(Zone.zone0());
		Auth auth = qConfig.getAuth();
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			Response res = bucketManager.delete(bucket, key);
			return res;
		} catch (QiniuException e) {
			return e.response;
		}
	}
	
}
