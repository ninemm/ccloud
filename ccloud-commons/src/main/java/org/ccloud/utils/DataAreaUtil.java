package org.ccloud.utils;

import com.jfinal.kit.StrKit;

public class DataAreaUtil {

	public static String dataAreaSetByDept(Integer level, String parentDataArea) {
		String dataArea = parentDataArea + String.format("%02d", level);
		return dataArea;
	}

	public static String dataAreaSetByUser(String deptDataArea) {
		String dataArea = deptDataArea + StrKit.getRandomUUID().substring(0, 4);
		return dataArea;
	}

	public static String getDeptDataAreaByCurUserDataArea(String dataArea) {
		
		if (StrKit.notBlank(dataArea))
			return dataArea.substring(0, dataArea.length() - 4);
		
		return null;
	}

	public static String getDealerDataAreaByCurUserDataArea(String dataArea) {
		
		if (StrKit.notBlank(dataArea) && dataArea.length() > 9) {
			return dataArea.substring(0, 9);
		}
		
		return dataArea;
	}

}
