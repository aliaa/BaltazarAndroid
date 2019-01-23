package com.mybaltazar.baltazar2.utils;

import android.content.Context;

import com.mybaltazar.baltazar2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
    private static StringBuilder sb = new StringBuilder();
    private static StringBuilder lsb = new StringBuilder();
    public static String getPersianNumber(int num) {
        sb.setLength(0);
        boolean negative = false;
        if (num == 0) {
            char c = 0x06F0;
            sb.append(c);
        } else {
            if (num < 0) {
                negative = true;
                num = -num;
            }
            while (num > 0) {
                char c = (char) ((num % 10) + 0x06f0);
                sb.append(c);
                num /= 10;
            }
        }
        if (negative)
            sb.append('-');
        return sb.reverse().toString();
    }

    public static String getPersianNumber(String western) {
        sb.setLength(0);
        for (int i = 0; i < western.length(); i++) {
            char c = western.charAt(i);
            if (c >= '0' && c <= '9')
                c += 0x06c0;
            sb.append(c);
        }
        return sb.toString();
    }

    public static String buildReadableTime(int seconds, int zeroId, Context c) {
        int t = seconds;

        if (t < 60)
            return c.getString(zeroId);
        t /= 60;
        if (t < 60)
            return addNumberToWord(t, c.getString(R.string.minute));
        t /= 60;
        if (t < 24)
            return addNumberToWord(t, c.getString(R.string.hour));
        t /= 24;
        if (t < 7)
            return addNumberToWord(t, c.getString(R.string.day));
        if (t < 30)
            return addNumberToWord(t / 7, c.getString(R.string.week));
        if (t < 365) {
            t /= 30;
            if (t == 12)
                t = 11;
            return addNumberToWord(t, c.getString(R.string.month));
        } else {
            t /= 365;
            return addNumberToWord(t, c.getString(R.string.year));
        }
    }
    public static String addNumberToWord(int number, String word) {
        lsb.setLength(0);
        lsb.append(getPersianNumber(number)).append(' ').append(word);
        return lsb.toString();
    }

    public static String getPersianDate(String dateString)
    {
        dateString = dateString.replace("T", " ");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = fmt.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            JalaliCalendar.YearMonthDate cDate = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)));
            return cDate.toPersianString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
