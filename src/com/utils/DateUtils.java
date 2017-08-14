package com.utils;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * 提供对日期时间操作的几个日常方法.
 */
@SuppressWarnings("deprecation")
public class DateUtils {

	private static final Logger log = Logger.getLogger(DateUtils.class);

	private static final Date FIRST_DATE = new Date(0, 0, 1, 0, 0, 0);
	private static final long ONE_DAY = 24 * 3600000;

	private static String datePattern = "yyyy-MM-dd";

	private static String timePattern = "HH:mm";

	private static String pattern = "HH:mm:ss";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			datePattern);

	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			timePattern);

	// private static SimpleDateFormat datetimeFormat = new SimpleDateFormat(
	// datePattern + " " + timePattern);
	private static SimpleDateFormat datetimeFormat = new SimpleDateFormat(
			datePattern + " " + timePattern);

	private static SimpleDateFormat datetimeAllFormat = new SimpleDateFormat(
			datePattern + " " + pattern);

	/**
	 * 将日期对象转换为字符串，格式为yyyy-MM-dd.
	 * 
	 * @param date
	 *            日期.
	 * @return 日期对应的日期字符串.
	 */
	public static String toDateString(Date date) {
		if (date == null) {
			return null;
		}
		return dateFormat.format(date);
	}

	/**
	 * 将字符串转换为日期对象，字符串必须符合yyyy-MM-dd的格式.
	 * 
	 * @param s
	 *            要转化的字符串.
	 * @return 字符串转换成的日期.如字符串为NULL或空串,返回NULL.
	 */
	public static Date toDate(String s) {
		s = StringUtils.trim(s);
		if (s.length() < 1) {
			return null;
		}

		try {
			if (s.length() <= 10) {
				return dateFormat.parse(s);
			}
			try {
				if (s.length() >= 19) {
					return datetimeAllFormat.parse(s);
				} else {
					return datetimeFormat.parse(s);
				}
			} catch (Exception e) {
				return toDate(Timestamp.valueOf(s));
			}
		} catch (Exception e) {
			log.warn("解析字符串成日期对象时出错", e);
			return null;
		}
	}

	/**
	 * 将日期对象转换为字符串，转换后的格式为yyyy-MM-dd HH:mm:ss.
	 * 
	 * @param date
	 *            要转换的日期对象.
	 * @return 字符串,格式为yyyy-MM-dd HH:mm:ss.
	 */
	public static String toDatetimeString(Date date) {
		if (date == null) {
			return null;
		}
		return datetimeAllFormat.format(date);
	}

	/**
	 * 获取当前月的第一天
	 * 
	 * @return
	 */
	public static String getCurrentMonthFirst() {
		Date cur = now();
		return toDateString(getFutureDateByDay(cur, -(cur.getDate() - 1)));
	}

	public static String getCurrentMonthFirstDT() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		return datetimeFormat.format(cal.getTime());
	}

	public static String getCurrentMonthLastDT() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.SECOND, -1);
		return datetimeFormat.format(cal.getTime());
	}
	
	public static String getCurrentDateEnd(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.SECOND, -1);
		return datetimeFormat.format(cal.getTime());
	}

	/**
	 * 将Timestamp转换为日期.
	 * 
	 * @param timestamp
	 *            时间戳.
	 * @return 日期对象.如时间戳为NULL,返回NULL.
	 */
	public static Date toDate(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new Date(timestamp.getTime());
	}

	/**
	 * 将日期转换为Timestamp.
	 * 
	 * @param date
	 *            日期.
	 * @return 时间戳.如日期为NULL,返回NULL.
	 */
	public static Timestamp toTimestamp(Date date) {
		if (date == null) {
			return null;
		}

		return new Timestamp(date.getTime());
	}

	/**
	 * 将时间戳对象转化成字符串.
	 * 
	 * @param t
	 *            时间戳对象.
	 * @return 时间戳对应的字符串.如时间戳对象为NULL,返回NULL.
	 */
	public static String toDateString(Timestamp t) {
		if (t == null) {
			return null;
		}
		return toDateString(toDate(t));
	}

	/**
	 * 将Timestamp转换为日期时间字符串.
	 * 
	 * @param t
	 *            时间戳对象.
	 * @return Timestamp对应的日期时间字符串.如时间戳对象为NULL,返回NULL.
	 */
	public static String toDatetimeString(Timestamp t) {
		if (t == null) {
			return "";
		}
		return toDatetimeString(toDate(t));
	}

	/**
	 * 将日期字符串转换为Timestamp对象.
	 * 
	 * @param s
	 *            日期字符串.
	 * @return 日期时间字符串对应的Timestamp.如字符串对象为NULL,返回NULL.
	 */

	public static Timestamp toTimestamp(String s) {
		return toTimestamp(toDate(s));
	}

	/**
	 * 返回年份，如2004.
	 */
	public static int getYear(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		return c.get(1);
	}

	/**
	 * 返回月份，为1－－ － 12内.
	 */
	public static int getMonth(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		return c.get(2) + 1;
	}

	public static int getDay(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c.get(5);
	}

	/**
	 * 获得将来的日期.如果timeDiffInMillis > 0,返回将来的时间;否则，返回过去的时间
	 * 
	 * @param currDate
	 *            现在日期.
	 * @param timeDiffInMillis
	 *            毫秒级的时间差.
	 * @return 经过 timeDiffInMillis 毫秒后的日期.
	 */
	public static Date getFutureDateByMillis(Date currDate,
			long timeDiffInMillis) {
		long l = currDate.getTime();

		l += timeDiffInMillis;
		return new Date(l);
	}

	/**
	 * 获得将来的日期.如果timeDiffInMillis > 0,返回将来的时间;否则，返回过去的时间.
	 * 
	 * @param currDate
	 *            现在日期.
	 * @param timeDiffInMillis
	 *            毫秒级的时间差.
	 * @return 经过 timeDiffInMillis 毫秒后的日期.
	 */
	public static Date getFutureDateByMillis(String currDate,
			long timeDiffInMillis) {
		return getFutureDateByMillis(toDate(currDate), timeDiffInMillis);
	}

	/**
	 * 获得将来的日期.如果 days > 0,返回将来的时间;否则，返回过去的时间.
	 * 
	 * @param currDate
	 *            现在日期.
	 * @param days
	 *            经过的天数.
	 * @return 经过days天后的日期.
	 */
	public static Date getFutureDateByDay(Date currDate, int days) {
		long l = currDate.getTime();
		long l1 = (long) days * ONE_DAY;

		l += l1;
		return new Date(l);
	}

	/**
	 * 获得将来的日期.如果 days > 0,返回将来的时间;否则，返回过去的时间.
	 * 
	 * @param currDate
	 *            现在日期,字符型如2005-05-05 [14:32:10].
	 * @param days
	 *            经过的天数.
	 * @return 经过days天后的日期.
	 */
	public static Date getFutureDateByDay(String currDate, int days) {
		return getFutureDateByDay(toDate(currDate), days);
	}

	/**
	 * 检查是否在核算期内.
	 * 
	 * @param currDate
	 *            当前时间.
	 * @param dateRange
	 *            核算期日期范围.
	 * @return 是否在核算期内.
	 */
	public static boolean isDateInRange(String currDate, String[] dateRange) {
		if (currDate == null || dateRange == null || dateRange.length < 2) {
			throw new IllegalArgumentException("传入参数非法");
		}

		currDate = getDatePart(currDate);
		@SuppressWarnings("unused")
		String startDate = getDatePart(dateRange[0]);
		@SuppressWarnings("unused")
		String stopDate = getDatePart(dateRange[1]);

		return (currDate.compareTo(dateRange[0]) >= 0 && currDate
				.compareTo(dateRange[1]) <= 0);
	}

	/**
	 * 只获取日期部分.获取日期时间型的日期部分.
	 * 
	 * @param currDate
	 *            日期[时间]型的字串.
	 * @return 日期部分的字串.
	 */
	public static String getDatePart(String currDate) {
		if (currDate != null && currDate.length() > 10) {
			return currDate.substring(0, 10);
		}

		return currDate;
	}

	/**
	 * 计算两天的相差天数,不足一天按一天算.
	 * 
	 * @param stopDate
	 *            结束日期.
	 * @param startDate
	 *            开始日期.
	 * @return 相差天数 = 结束日期 - 开始日期.
	 */
	public static int getDateDiff(String stopDate, String startDate) {
		long t2 = toDate(stopDate).getTime();
		long t1 = toDate(startDate).getTime();

		int diff = (int) ((t2 - t1) / ONE_DAY); // 相差天数
		// 如有剩余时间，不足一天算一天

		diff += (t2 > (t1 + diff * ONE_DAY) ? 1 : 0);
		return diff;
	}

	/**
	 * 计算两天的相差天数,不足一天按一天算.
	 * 
	 * @param stopDate
	 *            结束日期.
	 * @param startDate
	 *            开始日期.
	 * @return 相差天数 = 结束日期 - 开始日期.
	 */
	public static int getDateDiff(Date stopDate, Date startDate) {
		long t2 = stopDate.getTime();
		long t1 = startDate.getTime();

		int diff = (int) ((t2 - t1) / ONE_DAY); // 相差天数
		// 如有剩余时间，不足一天算一天

		diff += (t2 > (t1 + diff * ONE_DAY) ? 1 : 0);
		return diff;
	}

	/**
	 * 获得系统的当前时间.
	 */
	public static String currDate() {
		return toDatetimeString(now());
	}

	public static String todayBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		Date date = calendar.getTime();
		return datetimeFormat.format(date);
	}

	public static String todayBeginTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		Date date = calendar.getTime();
		return timeFormat.format(date);
	}

	public static String tomorrow() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		Date date = calendar.getTime();
		return datetimeFormat.format(date);
	}

	public static String minDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 1);
		Date date = calendar.getTime();
		return datetimeFormat.format(date);
	}

	public static String todayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		return datetimeFormat.format(calendar.getTime());
	}
	
	public static String todayEndTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		return timeFormat.format(calendar.getTime());
	}

	/**
	 * 获得系统当前日期，无时间信息.
	 */
	public static String currYMD() {
		return toDateString(now());
	}

	public static Date now() {
		return new Date();
	}

	public static Date getFirstDate() {
		return (Date) FIRST_DATE.clone();
	}

	public static Timestamp currTimestamp() {
		return new Timestamp(now().getTime());
	}

	public static int getWeekday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);

	}

	public static int getAge(Date birthDate) {
		Date endDate = new Date();
		if ((birthDate == null) || (birthDate.after(endDate))) {
			return 0;
		}

		int year = getYear(endDate) - getYear(birthDate);
		int month = getMonth(endDate) - getMonth(birthDate);
		int day = getDay(endDate) - getDay(birthDate);
		if (day < 0)
			month--;
		if (month < 0) {
			year--;
		}

		return year;
	}

	public static void main(String[] s) {

	}
}