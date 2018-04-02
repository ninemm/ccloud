/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfinal.core.JFinal;
import com.jfinal.log.Log;

public class StringUtils {
	private static final Log log = Log.getLog(StringUtils.class);

	public static String urlDecode(String string) {
		try {
			return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			log.error("urlDecode is error", e);
		}
		return string;
	}

	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			log.error("urlEncode is error", e);
		}
		return string;
	}

	public static String urlRedirect(String redirect) {
		try {
			redirect = new String(redirect.getBytes(JFinal.me().getConstants().getEncoding()), "ISO8859_1");
		} catch (UnsupportedEncodingException e) {
			log.error("urlRedirect is error", e);
		}
		return redirect;
	}
	
	public static String urlRedirectToUTF8(String redirect) {
		try {
			redirect = new String(redirect.getBytes("ISO8859_1"), JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			log.error("urlRedirect is error", e);
		}
		return redirect;
	}

	public static boolean areNotEmpty(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.equals("");
	}

	public static boolean areNotBlank(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string.trim())) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String string) {
		return string != null && !string.trim().equals("");
	}

	public static boolean isBlank(String string) {
		return string == null || string.trim().equals("");
	}

	public static long toLong(String value, Long defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Long.parseLong(value.substring(1));
			return Long.parseLong(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static int toInt(String value, int defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Integer.parseInt(value.substring(1));
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static BigInteger toBigInteger(String value, BigInteger defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return new BigInteger(value).negate();
			return new BigInteger(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static boolean match(String string, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	}

	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	public static String escapeHtml(String text) {
		if (isBlank(text))
			return text;
		
		return text.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;").replace("/", "&#x2F;");
	}
	
	public static String getArrayFirst(String [] value) {
		if (value == null || StringUtils.isBlank(value[0])) {
			return null;
		}
		return value[0];
	}

	//截取订单末尾流水号
	public static String substringSN(String salesOrderSn, String orderSn) {
		int length = salesOrderSn.length();
		String SN = orderSn.substring(orderSn.length() - length, orderSn.length());
		return SN;
	}
	
	public static String getRandomCode(int length, int type) {
		StringBuffer buffer = null;
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		r.setSeed(new Date().getTime());
		
		switch (type) {
			case 0:
				buffer = new StringBuffer("0123456789");
				break;
			case 1:
				buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
				break;
			case 2:
				buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
				break;
			case 3:
				buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
				break;
			case 4:
				buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
				break;
			case 5:
				buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
				break;
			case 6:
				buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
				sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
				length -= 1;
				break;
			case 7:
				String uuid = getUUID();
				sb.append(uuid);
				break;
			default:
				break;
		}
		
		if (type != 7) {
			int range = buffer.length();
			for (int i = 0; i < length; i++) {
				sb.append(buffer.charAt(r.nextInt(range)));
			}
		}
		
		return sb.toString();
	}
	
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
	}
	
	/**
     * int型的String进行加减，不够位数的在前面补0
     * @param intStr：int型的字符串
     * @param addNum：需要加减的数字， 减则传负数
     * @param len：保留长度位数字
     * @return
     */
    public static String addIntStrAndFillZeros(String intStr, int addNum, int len) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0     
        // num 代表长度为4     
        // d 代表参数为正数型 
        result = String.format("%0" + len + "d", Integer.parseInt(intStr) + addNum);
        return result;
    }
    
    public static void main(String[] args) {
    	System.out.println(addIntStrAndFillZeros("0005", 5, 4));
	}
	
}
