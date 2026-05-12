package com.desertUo.listeners;

import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.List;

public class PingListener implements Listener {
    private final DesertUo plugin = DesertUo.getPlugin();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ServerListPingEvent e) {
        e.motd(this.getMotd());
    }

    public Component getMotd() {
        List<String> motd_lines = plugin.getMessagesConfig().getStringList("motd-lines");
        Component motd;
        Component line1 = Utils.formatMessage("");
        Component line2 = Utils.formatMessage("");
        if(motd_lines.isEmpty()) {
            line1 = Utils.formatMessage("Please add the \"motd-lines\" path to your config.yml");
        } else {
            line1 = Utils.formatMessage(motd_lines.get(0));

            if(motd_lines.size() > 1) {
                line2 = Utils.formatMessage("\n" + motd_lines.get(1));
            }
        }

        motd = line1.append(line2);

        return motd;
    }
}
