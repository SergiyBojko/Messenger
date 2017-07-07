package com.rammstein.messenger.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.local.Chat;
import com.rammstein.messenger.model.local.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.RealmList;

/**
 * Created by user on 03.06.2017.
 */

public class SimpleDateUtils {

    public final static int TODAY = 1;
    public final static int THIS_WEEK = 2;
    public final static int THIS_MONTH = 3;
    public final static int OLDER_THAN_MONTH = 0;

    public static String formatTime(Context context, long time, int flags){

        switch (getTimeGroup(time)){
            case TODAY:{
                flags |= DateUtils.FORMAT_SHOW_TIME;
                break;
            }
            case THIS_WEEK:{
                flags |= DateUtils.FORMAT_SHOW_WEEKDAY|DateUtils.FORMAT_ABBREV_WEEKDAY;
                break;
            }
            case THIS_MONTH:
            case OLDER_THAN_MONTH:{
                flags |= DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_ABBREV_MONTH;
                break;
            }
            default:{

            }
        }

        return DateUtils.formatDateTime(context, time, flags);
    }

    public static String formatTime(Context context, long time){
        return formatTime(context, time, 0);
    }

    public static int getTimeGroup(Chat chat) {
        RealmList<Message> messages = chat.getMessages();
        Message lastMessage;
        long lastMessageTime;
        if (messages.size() > 0){
            lastMessageTime = messages.where().max(Message.TIME_IN_MILLS).longValue();
        } else {
            lastMessageTime = System.currentTimeMillis();
            //TODO get chat creation time
        }

        return getTimeGroup(lastMessageTime);
    }

    public static int getTimeGroup(Message message) {
        long time = message.getTimeInMills();
        return getTimeGroup(time);
    }

    public static int getTimeGroup(long time) {
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar messageTime = new GregorianCalendar();
        messageTime.setTimeInMillis(time);

        if ((now.getTimeInMillis() - messageTime.getTimeInMillis()) < 1000*60*60*24
                && now.get(Calendar.DAY_OF_WEEK) == messageTime.get(Calendar.DAY_OF_WEEK)){

            return TODAY;
        }

        int firstDayOfWeek = now.getFirstDayOfWeek();
        int dayOfWeekNow = now.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = messageTime.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek < firstDayOfWeek){
            dayOfWeek += 7;
        }
        if (dayOfWeekNow < firstDayOfWeek){
            dayOfWeekNow += 7;
        }
        if ((now.getTimeInMillis() - messageTime.getTimeInMillis()) < 1000*60*60*24*7
                &&  dayOfWeekNow > dayOfWeek){
            return THIS_WEEK;
        }

        if (now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH)){
            return THIS_MONTH;
        }
        return OLDER_THAN_MONTH;
    }

    public static String getTimeGroupLabel(Context context, Chat chat) {
        String timeGroupText = "";
        int timeGroup = getTimeGroup(chat);
        switch (timeGroup){
            case TODAY:{
                timeGroupText = context.getResources().getString(R.string.today);
                break;
            }
            case THIS_WEEK:{
                timeGroupText = context.getResources().getString(R.string.this_week);
                break;
            }
            case THIS_MONTH:{
                timeGroupText = context.getResources().getString(R.string.this_month);
                break;
            }
            case OLDER_THAN_MONTH:{
                RealmList<Message> messages = chat.getMessages();
                long lastMessageTime = messages.get(messages.size()-1).getTimeInMills();
                int flags = DateUtils.FORMAT_NO_MONTH_DAY;
                timeGroupText = DateUtils.formatDateTime(context, lastMessageTime, flags);
                break;
            }
        }
        return timeGroupText;
    }

    public static Date parseDateString(String dateTime){
        if (dateTime == null || dateTime.isEmpty()){
            return null;
        }


        Date date = null;
        SimpleDateFormat sdf;
        String formatLong = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String formatShort = "yyyy-MM-dd'T'HH:mm:ss";

        if (dateTime.length() == (formatShort.length() - 2)){
            sdf = new SimpleDateFormat(formatShort);
        } else {
            sdf = new SimpleDateFormat(formatLong);
        }

        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        if (year < 1900){
            return null;
        }

        return date;

    }

    public static String formatDateShort(GregorianCalendar calendar) {
        if (calendar == null){
            return null;
        }
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(calendar.getTime());
        Log.i("formatDateShort", date);
        return date;
    }

    public static String formatDateLong (long mills){
        String formatLong = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(formatLong);
        String date = sdf.format(mills);
        Log.i("formatDateLong", date);
        return date;
    }
}
