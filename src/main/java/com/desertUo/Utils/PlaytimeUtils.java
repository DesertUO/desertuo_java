package com.desertUo.Utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeUtils {

    public static int getPlayerPlaytimeTicks(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }

    public static int[] secondsToDHMS(long totalSeconds) {
        int seconds = (int)(totalSeconds % 60);
        int minutes = (int)((totalSeconds / 60) % 60);
        int hours = (int)((totalSeconds / 3600) % 24);
        int days = (int)((totalSeconds / (3600 * 24)));

        return new int[] {days, hours, minutes, seconds};
    }

    public static int[] formatPlaytimeDHMS(int ticks) {
        return secondsToDHMS(ticks / 20);
    }

    public static int[] formatMillisDHMS(long millis) {
        return secondsToDHMS(millis / 1000L);
    }

    public static String buildFormattedDHMSString(int[] formattedTime, String ... format) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < formattedTime.length; i++) {
            if(formattedTime[i] > 0) {
                if(!stringBuilder.isEmpty()) { stringBuilder.append(" "); }
                stringBuilder.append(formattedTime[i]).append(format[i]);
            }
        }
        return !stringBuilder.isEmpty() ? stringBuilder.toString() : "0" + format[formattedTime.length - 1];
    }

    public static String getStringWithFormattedPlayTime(Player player, String ... format) {
        return buildFormattedDHMSString(formatPlaytimeDHMS(getPlayerPlaytimeTicks(player)), format);
    }

    public static String getStringFormattedMillis(long millis, String ... format) {
        return buildFormattedDHMSString(formatMillisDHMS(millis), format);
    }

    public static String getStringFormattedPlaytime(Player player) {
        return getStringWithFormattedPlayTime(player, "d", "h", "m", "s");
    }

    public static String getStringFormattedMillis(long millis) {
        return getStringFormattedMillis(millis, "d", "h", "m", "s");
    }
}
