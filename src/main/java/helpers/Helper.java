package helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static String formatSongDuration(long time) {
        // Compute Hours, Minutes, Seconds
        long currentHours = TimeUnit.MILLISECONDS.toHours(time);
        long currentMinutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        long currentSeconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;

        // Format and Return
        if (currentHours == 0) {
            return String.format("%02d:%02d", currentMinutes, currentSeconds);
        } else {
            return String.format("%02d:%02d:%02d", currentHours, currentMinutes, currentSeconds);
        }
    }

    public static boolean isUrl(String link) {
        try {
            new URL(link);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isNumber(String number) {
        if (number == null) {
            return false;
        }

        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
