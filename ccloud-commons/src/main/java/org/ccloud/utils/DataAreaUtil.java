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

	public static String getUserDeptDataArea(String userDataArea) {
		String deptDataArea = userDataArea.substring(0, userDataArea.length() - 4);
		return deptDataArea;
	}

	public static String getUserDealerDataArea(String userDataArea) {
		if (userDataArea != null && userDataArea.length() >= 10) {
			return userDataArea.substring(0, 9);
		}
		return userDataArea;
	}

}
