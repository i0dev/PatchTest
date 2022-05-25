package com.i0dev.plugin.patchtest.utility;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * A time utility class for formatting times, and everything else.
 *
 * @author Andrew Magnuson
 */
public class TimeUtil {


    /**
     * Formats the period specified.
     * Is accurate to the nearest second
     *
     * @param timePeriod The time period in milliseconds
     * @return A formatted string of the time period.
     */
    public static String formatTimePeriod(long timePeriod) {
        long milliseconds = timePeriod;
        String ret = "";

        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        milliseconds -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);


        ret += days + (days == 1 ? " day " : days > 1 ? " days " : "");
        ret += hours + (hours == 1 ? " hour " : hours > 1 ? " hours " : "");
        ret += minutes + (minutes == 1 ? " minute " : minutes > 1 ? " minutes " : "");
        ret += seconds + (seconds == 1 ? " second " : seconds > 1 ? " seconds " : "");

        return ret.isEmpty() ? "0 seconds" : StringUtils.trim(ret);
    }

}
