package org.ccloud.qiniu;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;

public class QiniuPlugin implements IPlugin {

	private String propFile = "qiniu.properties";
	
	public QiniuPlugin() {}
	
	public QiniuPlugin(String propFile) {
		this.propFile = propFile;
	}
	
	@Override
	public boolean start() {
		Prop prop = PropKit.use(propFile);
		QiniuConfig config = new QiniuConfig(prop.get("ak"), prop.get("sk"));
		QiniuKit.init(config);
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
