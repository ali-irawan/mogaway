package com.wenresearch.mogaway.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String dateTimeNow(){
		Date now = new Date();
		SimpleDateFormat format = (SimpleDateFormat) DateFormat.getInstance();
		format.applyPattern("yyyyMMddHHmmss");
		return format.format(now);
	}
}
