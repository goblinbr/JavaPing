package com.sartor.javaping.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarUtil {

    public static Calendar getCalendar(Timestamp timestamp) {
        Calendar cal = null;
        if( timestamp != null ){
            cal = Calendar.getInstance();
            cal.setTimeInMillis( timestamp.getTime() );
        }
        return cal;
    }

}
