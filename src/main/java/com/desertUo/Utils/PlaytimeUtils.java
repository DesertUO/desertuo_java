package com.desertUo.Utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeUtils {

    public static int getPlayerPlaytimeTicks(Player player) {
        return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
    }

    public static int[] formatPlaytimeDHMS(int ticks) {
        int totalSeconds = ticks / 20;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / 3600) % 24;
        int days = (totalSeconds / (3600 * 24));

        return new int[] {days, hours, minutes, seconds};
    }

    public static String getStringWithFormattedPlayTime(Player player, String ... format) {
        int[] playtimeFormatted = formatPlaytimeDHMS(getPlayerPlaytimeTicks(player));

        String formattedString = "";

        if(getPlayerPlaytimeTicks(player) == 0) {
            formattedString = "0s";
            return formattedString;
        }

        if(playtimeFormatted[0] > 0) {
            formattedString += playtimeFormatted[0] + format[0];
            if(playtimeFormatted[1] > 0 || playtimeFormatted[2] > 0 || playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[1] > 0) {
            formattedString += playtimeFormatted[1] + format[1];
            if(playtimeFormatted[2] > 0 || playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[2] > 0) {
            formattedString += playtimeFormatted[2] + format[2];
            if(playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[3] > 0) {
            formattedString += playtimeFormatted[3] + format[3];
        }

        return formattedString;
    }

    public static int[] formatMillisDHMS(long millis) {
        long totalSeconds = millis / 1000L;
        int seconds = (int)(totalSeconds % 60);
        int minutes = (int)((totalSeconds / 60) % 60);
        int hours = (int)((totalSeconds / 3600) % 24);
        int days = (int)(totalSeconds / (3600 * 24));

        return new int[] {days, hours, minutes, seconds};
    }

    public static String getStringFormattedMillis(long millis, String ... format) {
        int[] playtimeFormatted = formatMillisDHMS(millis);

        String formattedString = "";
        if(millis == 0) {
            formattedString = "0s";
            return formattedString;
        }

        if(playtimeFormatted[0] > 0) {
            formattedString += playtimeFormatted[0] + format[0];
            if(playtimeFormatted[1] > 0 || playtimeFormatted[2] > 0 || playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[1] > 0) {
            formattedString += playtimeFormatted[1] + format[1];
            if(playtimeFormatted[2] > 0 || playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[2] > 0) {
            formattedString += playtimeFormatted[2] + format[2];
            if(playtimeFormatted[3] > 0) {
                formattedString += " ";
            }
        }
        if(playtimeFormatted[3] > 0) {
            formattedString += playtimeFormatted[3] + format[3];
        }

        return formattedString;
    }

    public static String getStringFormattedPlaytime(Player player) {
        return getStringWithFormattedPlayTime(player, "d", "h", "m", "s");
    }

    public static String getStringFormattedMillis(long millis) {
        return getStringFormattedMillis(millis, "d", "h", "m", "s");
    }
}
