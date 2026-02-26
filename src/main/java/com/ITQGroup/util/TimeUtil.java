package com.ITQGroup.util;

import java.time.Duration;

public final class TimeUtil {

    private TimeUtil() {
    }

    public static String formatNanos(long nanos) {
        Duration duration = Duration.ofNanos(nanos);

        long ms = duration.toMillis();
        long seconds = duration.toSeconds();
        long minutes = duration.toMinutes();

        if (minutes > 0) {
            return minutes + " min " + (seconds % 60) + " s";
        }
        if (seconds > 0) {
            return seconds + " s " + (ms % 1000) + " ms";
        }
        return ms + " ms";
    }
}
