package com.rammstein.messenger.util;

import android.content.Context;
import android.text.format.DateUtils;

import com.rammstein.messenger.R;
import com.rammstein.messenger.model.Chat;
import com.rammstein.messenger.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
        ArrayList<Message> messages = chat.getMessages();
        Message lastMessage = messages.get(messages.size()-1);

        return getTimeGroup(lastMessage);
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

        if ((now.getTimeInMillis() - messageTime.getTimeInMillis()) < 1000*60*60*24*7
                && now.get(Calendar.DAY_OF_WEEK) > messageTime.get(Calendar.DAY_OF_WEEK)){
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
                ArrayList<Message> messages = chat.getMessages();
                long lastMessageTime = messages.get(messages.size()-1).getTimeInMills();
                int flags = DateUtils.FORMAT_NO_MONTH_DAY;
                timeGroupText = DateUtils.formatDateTime(context, lastMessageTime, flags);
                break;
            }
        }
        return timeGroupText;
    }
}
