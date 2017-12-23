package org.ccloud.qiniu;

import com.qiniu.util.Auth;

public class QiniuConfig {

	private String ak;
	private String sk;
	
	public QiniuConfig(String ak, String sk) {
		this.ak = ak;
		this.sk = sk;
	}
	
	public String getToken(String bucketName) {
		Auth auth = Auth.create(ak, sk);
		return auth.uploadToken(bucketName);
	}
	
	public Auth getAuth() {
		return Auth.create(ak, sk);
	}

	public String getAk() {
		return ak;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}
	
	
	
	
	
}
