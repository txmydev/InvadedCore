package invaded.cc.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static long parseTime(String arg) {
        String f = arg.substring(arg.length() - 1);
        int number;
        try {
            number = Integer.parseInt(arg.substring(0, arg.length() - 1));
        } catch (NumberFormatException asd) {
            number = -1;
            System.out.println("Error trying to parse " + arg.substring(0, arg.length() - 1) + " to a number.");
            asd.printStackTrace();
            return number;
        }

        long time = 0L;

        switch (f) {
            case "s":
                time = TimeUnit.SECONDS.toMillis(number);
                break;
            case "m":
                time = TimeUnit.MINUTES.toMillis(number);
                break;
            case "h":
                time = TimeUnit.HOURS.toMillis(number);
                break;
            case "d":
                time = TimeUnit.DAYS.toMillis(number);
                break;
            case "w":
                time = TimeUnit.DAYS.toMillis(number * 7);
                break;
            case "mo":
                time = TimeUnit.DAYS.toMillis(number * 30);
                break;
            case "y":
                time = TimeUnit.DAYS.toMillis(number * 30 * 365);
                break;
            default:
                time = -1;
                System.out.println("Couldn't define a specified time for " + f);
                break;
        }
        return time;
    }

    public static String formatTime(long time) {
        SimpleDateFormat format;

        if (TimeUnit.MILLISECONDS.toDays(time) > 0) {
            format = new SimpleDateFormat("d 'days' h 'hours' m 'minutes' s 'seconds'");

            return format.format(new Date(time));
        }

        if (TimeUnit.MILLISECONDS.toHours(time) > 0) {
            format = new SimpleDateFormat("h 'hours' m 'minutes' s 'seconds'");
            return format.format(new Date(time));
        }

        if (TimeUnit.MILLISECONDS.toMinutes(time) > 0) {
            format = new SimpleDateFormat("m 'minutes' s 'seconds'");
            return format.format(new Date(time));
        }

        format = new SimpleDateFormat("s 'seconds'");
        return format.format(new Date(time));
    }

}
