/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package comparator;

import java.util.Comparator;

import org.ccloud.model.Seller;

/**
 * @author wally
 *
 */
public class SellerDeptComparator implements Comparator<Seller> {

	@Override
	public int compare(Seller o1, Seller o2) {
		if(o1.getDataArea().length() > o2.getDataArea().length())
			return -1;
		else if(o1.getDataArea().length() == o2.getDataArea().length())
			return 0;
		return 1;
	}

}
