package kr.o3selab.homecoco.Models;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;

public class Date {

    public static Date getDateToLong(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        String parseTime = sdf.format(new java.util.Date(time));

        StringTokenizer str = new StringTokenizer(parseTime, ".");
        return new Date(str.nextToken(), str.nextToken(), str.nextToken());
    }

    public static Date getDateWithTimeToString(String time) {
        Date date = new Date();

        int index = 0;

        date.year = Integer.parseInt(time.substring(index, time.indexOf("년")));
        index = time.indexOf("년") + 2;
        date.month = Integer.parseInt(time.substring(index, time.indexOf("월")));
        index = time.indexOf("월") + 2;
        date.date = Integer.parseInt(time.substring(index, time.indexOf("일")));
        index = time.indexOf("일") + 2;
        date.type = time.substring(index, time.length());

        return date;
    }

    public static long getTimeToInt(int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        long time;
        try {
            time = sdf.parse(String.format(Locale.KOREA, "%04d.%02d.%02d", year, month + 1, dayOfMonth)).getTime();
        } catch (Exception e) {
            time = 0;
        }
        return time;
    }

    public static String getStringToLong(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA);
        return sdf.format(new java.util.Date(time));
    }

    private int year;
    private int month;
    private int date;

    private int hour;
    private int minute;

    private String type;

    public Date() {

    }

    public Date(int year, int month, int date, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
    }

    public Date(String year, String month, String date) {
        this.year = Integer.parseInt(year);
        this.month = Integer.parseInt(month);
        this.date = Integer.parseInt(date);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month - 1;
    }

    public int getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return year + "." + month + "." + date;
    }
}
