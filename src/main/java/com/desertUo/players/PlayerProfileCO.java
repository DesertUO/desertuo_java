package com.desertUo.players;

import com.desertUo.Utils.Utils;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerProfileCO {
    public int kills;
    public int deaths;
    public int level;
    public long xp;

    public int secondsInGame = 0;

    public PlayerProfileCO(Document doc) {
        this.level = doc.getInteger("level", 1);
        this.xp = doc.getLong("level-xp") != null ? doc.getLong("level-xp") : 0L;
        this.kills = doc.getInteger("kills", 0);
        this.deaths = doc.getInteger("deaths", 0);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKills(int amount) {
        this.kills += amount;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public void addXp(Player player, long amount) {
        this.xp += amount;

        while(this.xp >= getXpGoal()) {
            this.xp -= getXpGoal();
            this.level++;

            player.sendMessage(Utils.formatMessage("&a&lLEVEL UP! &fYou are now level &b: " + this.level + " &f(&b" + this.xp + "&7/&3" + getXpGoal() + "&f)"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
        return;
    }

    public static long getXpGoal(int level) {
        if (level <= 10) {
            return 5000 + (1000L * (level - 1));
        }
        else if (level <= 30) {
            return 15000 + (5000L * (level - 11));
        }
        else {
            return 115000 + (15000L * (level - 31));
        }
    }

    public long getXpGoal() {
        return getXpGoal(level);
    }

    public double getProgressPercent() {
        return ((double) this.xp / getXpGoal()) * 100;
    }

    public void tickPlaytime(Player player) {
        secondsInGame++;
        if (secondsInGame >= 60) {
            secondsInGame = 0;
            this.addXp(player, 25L);

            player.sendActionBar(Utils.formatMessage("&a+&225 XP &a(Playtime)"));
        }
    }
}
