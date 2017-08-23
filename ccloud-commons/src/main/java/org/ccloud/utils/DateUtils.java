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

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.jfinal.kit.StrKit;

public class DateUtils {

	/** 缺省日期格式,精确到秒 */
	public static final String DEFAULT_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	/** 缺省日期格式,精确到毫秒  */
    public static final String DEFAULT_DATETIME_FORMAT_MILLI_SEC = "yyyy-MM-dd HH:mm:ss.SSS";
    /** 文件名日期格式, 精确到毫秒  */
    public static final String DEFAULT_FILE_NAME_FORMATTER = "yyyyMMddHHmmssSSS";
    /** 文件目录日期格式  */
    public static final String DEFAULT_DIRECTORY_FORMATTER = "yyyyMM";
    
    public static final String DEFAULT_NORMAL_FORMATTER = "yyyy-MM-dd";
    
    public static final String DEFAULT_UNSECOND_FORMATTER = "yyyy-MM-dd HH:mm";
    
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");

	public static String now() {
		return sdf.format(new Date());
	}

	public static String dateString() {
		return dateSdf.format(new Date());
	}

	public static String format(Date date) {
		if (null == date)
			return null;

		return sdf.format(date);
	}
	
	/**
	 * 功能描述：日期输出为指定格式的字符串
	 * @param format	格式
	 * @param date		日期
	 * @return
	 * 返回类型：String
	 * 创建人：eric
	 * 日期：2017年2月15日
	 */
	public static String format(String format, Date date) {
		
		DateTime dateTime = null;
		if (null == date)
			dateTime = new DateTime();
		else
			dateTime = new DateTime(date.getTime());
		
		return dateTime.toString(format);
		
	}
	
	/**
	 * 功能描述：字符串转日期
	 * @param str		日期字符串
	 * @param format	日期格式
	 * @return
	 * 返回类型：Date
	 * 创建人：eric
	 * 日期：2017年2月16日
	 */
	public static Date strToDate(String str, String format) {
		
		if(StrKit.isBlank(str))
			return null;
		
		if(StrKit.isBlank(format))
			format = DEFAULT_FORMATTER;
		
		return DateTime.parse(str, DateTimeFormat.forPattern(format)).toDate();
		
	}

	/**
	 * 统计两个日期之间包含的天数。
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDayDiff(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new InvalidParameterException("date1 and date2 cannot be null!");
		}
		long millSecondsInOneDay = 24 * 60 * 60 * 1000;
		return (int) ((date1.getTime() - date2.getTime()) / millSecondsInOneDay);
	}

	/**
	 * 功能描述：比较两个日期的大小
	 * @param start	开始时间
	 * @param end	结束时间
	 * @return	true：开始时间小于结束时间，false：开始时间大于结束时间
	 * 返回类型：boolean
	 * 创建人：eric
	 * 日期：2017年2月13日
	 */
	public static boolean compareDays(Date start, Date end) {
		DateTime startDate = new DateTime(start);
		if (end == null)
			return startDate.isBeforeNow();
		return startDate.isBefore(startDate.getMillis());
	}
	
	/**
	 * 功能描述：给指定日期添加天数
	 * @param date	日期
	 * @param day	天数
	 * @return
	 * 返回类型：Date
	 * 创建人：eric
	 * 日期：2017年6月10日
	 */
	public static Date plusDays(Date date, int day) {
		
		DateTime dateTime = new DateTime(date);
		return dateTime.plusDays(day).toDate();
		
	}
	
	public static void main(String[] args) throws ParseException {
		Date date = sdf.parse("2016-7-20 00:00:00");
		System.out.println(getDayDiff(new Date(), date));
		
		DateTime dateTime = DateTime.now();
		
		System.out.println(dateTime.plusDays(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER));
		
		System.out.println(dateTime.plusWeeks(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER));
		
		System.out.println(dateTime.plusMonths(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER));
		
		System.out.println(dateTime.plusYears(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER));
		
		
	}

}
