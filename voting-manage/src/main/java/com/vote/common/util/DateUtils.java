package com.vote.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期相关工具类
 *
 *
 */
@Slf4j
public class DateUtils {

	/**
	 * 年月日时分秒
	 */
	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 年月日
	 */
	public static final String YMD  = "yyyy-MM-dd";
	/**
	 * 时分
	 */
	public static final String HM = "HH:mm";
	/**
	 * 年月日
	 */
	public static final String YYYYMMDD = "yyyyMMdd";
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getNowDateTime() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(YMDHMS);
		return LocalDateTime.now().format(format);
	}

	/**
	 * 格式化对象
	 * @param fm
	 * @return
	 */
	public static String getNowDateTimeByFormat(String fm){
		DateTimeFormatter format = DateTimeFormatter.ofPattern(fm);
		return LocalDateTime.now().format(format);
	}

	/**
	 * 判断两个时间段是否有交集
	 * @param startDate1
	 * @param endDate1
	 * @param startDate2
	 * @param endDate2
	 * @return
	 */
	public static Boolean isIntersection(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
		if (startDate1.isAfter(endDate2) || startDate2.isAfter(endDate1)) {
			return false;
		}
		return true;
	}

	/**
	 * 对象转时间格式
	 * @param obj
	 * @return
	 */
	public static LocalDateTime getLoalDateTime(Object obj) {

		if(obj==null){
			return LocalDateTime.now();
		}
		if(obj instanceof Long){
			long timestamp = NumberUtils.toLong(String.valueOf(obj));
			Instant instant = Instant.ofEpochMilli(timestamp);
			ZoneId zone = ZoneId.systemDefault();
			return LocalDateTime.ofInstant(instant, zone);
		}
		if(obj instanceof String){
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
			return LocalDateTime.parse(String.valueOf(obj), df);
		}
		return LocalDateTime.now();
	}

	/**
	 * 将字符转换成日期
	 *
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date parse(String dateStr, String format) {
		Date date = null;
		SimpleDateFormat sdateFormat = new SimpleDateFormat(format);
		sdateFormat.setLenient(false);
		try {
			date = sdateFormat.parse(dateStr);

		} catch (Exception e) {
			log.info("DateUtils error {} ", e);
		}
		return date;
	}


	/**
	 * 将字符转换成日期
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date parse(String dateStr) {
		Date date = null;
		SimpleDateFormat sdateFormat = new SimpleDateFormat(YMDHMS);
		sdateFormat.setLenient(false);
		try {
			date = sdateFormat.parse(dateStr);

		} catch (Exception e) {
			log.info("DateUtils error {} ", e);
		}
		return date;
	}

	/**
	 * 获取当月最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		return calendar.getTime();
	}

	/**
	 * 格式化日期,返回格式为 yyyy-MM-dd
	 *
	 * @param date 日期
	 * @return
	 */
	public static String formatAsDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(YMD);
		return df.format(date);
	}

	/**
	 * 计算开始时间
	 *
	 * @param time
	 * @return
	 */
	public static LocalDateTime getStartTime(String time) {
		String startTime = time;
		if (time.matches("^\\d{4}-\\d{1,2}$")) {
			startTime = time + "-01 00:00:00";
		} else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
			startTime = time + " 00:00:00";
		} else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
			startTime = time + ":00";
		} else if (time
				.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
			String str = time.replace("T", " ").substring(0, time.indexOf("."));
			startTime = str;
		}
		return LocalDateTime
				.parse(startTime, DateTimeFormatter.ofPattern(YMDHMS));
	}

	/**
	 * 计算结束时间
	 *
	 * @param time
	 * @return
	 */
	public static LocalDateTime getEndTime(String time) {
		String startTime = time;
		if (time.matches("^\\d{4}-\\d{1,2}$")) {
			Date date = DateUtils.parse(time, "yyyy-MM");
			date = DateUtils.getLastDateOfMonth(date);
			startTime = DateUtils.formatAsDate(date) + " 23:59:59";
		} else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
			startTime = time + " 23:59:59";
		} else if (time.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
			startTime = time + ":59";
		} else if (time
				.matches("^\\d{4}-\\d{1,2}-\\d{1,2}T{1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}Z$")) {
			time = time.replace("T", " ").substring(0, time.indexOf("."));
			if (time.endsWith("00:00:00")) {
				time = time.replace("00:00:00", "23:59:59");
			}
			startTime = time;
		}
		return LocalDateTime
				.parse(startTime, DateTimeFormatter.ofPattern(YMDHMS));
	}
}
