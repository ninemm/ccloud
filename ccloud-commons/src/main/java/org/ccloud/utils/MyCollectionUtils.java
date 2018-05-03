/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wally
 *
 */
public class MyCollectionUtils {
	public static <T> List<T> removeRepeat(List<T> list) {
		Set<T> set = new  HashSet<T>(); 
        List<T> newList = new ArrayList<T>(); 
        set.addAll(list);
        newList.addAll(set);
        return newList;
	}
}
