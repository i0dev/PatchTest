package com.i0dev.plugin.patchtest.utility;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {


    public static String formatTimePeriod(long timePeriod) {
        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds ";
        else if (seconds == 1) output += seconds + " second ";

        if (output.isEmpty()) return "0 seconds";

        return StringUtils.trim(output);
    }

}
