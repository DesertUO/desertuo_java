package com.desertUo.websockets;
import com.desertUo.DesertUo;
import com.desertUo.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.net.http.WebSocket;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class ChatWebSocketListener implements WebSocket.Listener {
    private final DesertUo plugin;
    private final Gson gson = new Gson();
    private final StringBuilder msgBuffer = new StringBuilder();

    public ChatWebSocketListener(DesertUo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        plugin.getServer().getLogger().info("Connected to the websocket server");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        msgBuffer.append(data);

        if(last) {
            String rawJson = msgBuffer.toString();
            msgBuffer.setLength(0);

            Bukkit.getScheduler().runTask(this.plugin, () -> {
                try {
                    WSChatMessage packet = gson.fromJson(rawJson, WSChatMessage.class);
                    if(packet == null) return;

                    switch (packet.getAction()) {
                        case "welcome":
                            if("SERVER".equals(packet.getSenderId())) {
                                String serverAssignedID = packet.getTargetId();
                                plugin.setServerSessionID(serverAssignedID);
                                plugin.getLogger().info("Chat WebSocket: Connected with backend with ID: " + serverAssignedID);
                            }
                            break;
                        case "bc":
                            if(plugin.getServerSessionID() != null && Objects.equals(packet.getSenderId(), plugin.getServerSessionID())) {
                                break;
                            }
                            Bukkit.getServer().broadcast(Utils.formatMessage("&7[&l&aCHAT&r&7]&r&f (" + packet.getSenderId() + "): " + packet.getMessage()));
                            break;
                        case "pm":
                            break;
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error while processing JSON: " + e.getMessage());
                }
            });
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        plugin.getLogger().severe("Error in WebSocket client: " + error.getMessage());
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        plugin.getLogger().info("WebSocket Connection closed with status: " + statusCode);

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, plugin::connectChatWebSocket, 100L);

        return null;
    }
}
