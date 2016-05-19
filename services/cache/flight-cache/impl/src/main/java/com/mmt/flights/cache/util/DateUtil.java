package com.mmt.flights.cache.util;

/**
 * Created by amit on 18/5/16.
 */
/**
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author ankit
 */
public class DateUtil {
    public static String convert_DDMMYY(Date date_ddmmyyyy) {
        SimpleDateFormat dateFormat_DDMMYYYY = new SimpleDateFormat("ddMMyy");
        return dateFormat_DDMMYYYY.format(date_ddmmyyyy);
    }

    public static String formatDate_yyyy_MM_dd_T_HH_mm_ss(Date date) {
        SimpleDateFormat dateFormat_yyyy_MM_dd_T_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateFormat_yyyy_MM_dd_T_HH_mm_ss.format(date);
    }

    public static String formatDate_ddMMYY_HHMM(Date date) {
        SimpleDateFormat dateFormat_yyyy_MM_dd_T_HH_mm_ss = new SimpleDateFormat("ddMMyy HHmm");
        return dateFormat_yyyy_MM_dd_T_HH_mm_ss.format(date);
    }

    public static Date convertDateTime(String dateTimeddMMyy_HHmm) throws ParseException {
        Date returnDate = new Date();
        SimpleDateFormat dateFormat_DD_MM_YY_HH_MM = new SimpleDateFormat("ddMMyy HHmm");
        returnDate = dateFormat_DD_MM_YY_HH_MM.parse(dateTimeddMMyy_HHmm);
        return returnDate;
    }

    public static Date convertDateTime(String dateTimeddMMyy_HHmm, TimeZone timezone) throws ParseException {
        Date returnDate = new Date();
        SimpleDateFormat dateFormat_DD_MM_YY_HH_MM = new SimpleDateFormat("ddMMyy HHmm");
        dateFormat_DD_MM_YY_HH_MM.setTimeZone(timezone);
        returnDate = dateFormat_DD_MM_YY_HH_MM.parse(dateTimeddMMyy_HHmm);
        return returnDate;
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calIST = Calendar.getInstance();
        calIST.setTime(date);
        Calendar calendarGMT = Calendar.getInstance();
        calendarGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendarGMT.set(Calendar.DATE, calIST.get(Calendar.DATE));
        calendarGMT.set(Calendar.MONTH, calIST.get(Calendar.MONTH));
        calendarGMT.set(Calendar.YEAR, calIST.get(Calendar.YEAR));
        return calendarGMT;
    }

    public static long getDuration(Calendar std, Calendar sta) {
        return TimeUnit.MILLISECONDS.toMinutes(Math.abs(std.getTimeInMillis() - sta.getTimeInMillis()));
    }

    public static Date convertCalendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    public static long getDurationInMinutes(Date depDateTimeStr, String depTimeZone,
                                            Date arrDateTimeStr, String arrTimeZone) throws ParseException {


        long diff = 0L;
        Date depDate = null, arrDate = null;
        if ((depTimeZone != null && !"".equals(depTimeZone)) && (arrTimeZone != null && !"".equals(arrTimeZone))) {
            SimpleDateFormat dateFormat_DD_MM_YY_HH_MM = new SimpleDateFormat("ddMMyy HHmm");

            String date1Str = dateFormat_DD_MM_YY_HH_MM.format(depDateTimeStr);
            TimeZone depTZ = TimeZone.getTimeZone(depTimeZone);
            depDate = DateUtil.convertDateTime(date1Str, depTZ);

            TimeZone arrTZ = TimeZone.getTimeZone(arrTimeZone);
            String date2Str = dateFormat_DD_MM_YY_HH_MM.format(arrDateTimeStr);
            arrDate = DateUtil.convertDateTime(date2Str, arrTZ);
        } else {
            depDate = depDateTimeStr;
            arrDate = arrDateTimeStr;
        }
        diff = Math.abs((arrDate.getTime() - depDate.getTime()) / (60 * 1000));
        return diff;
    }
}
