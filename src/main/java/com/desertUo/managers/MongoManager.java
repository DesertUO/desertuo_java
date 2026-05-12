package com.desertUo.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoManager {
    private final MongoCollection<Document> playerCollection;
    private final MongoCollection<Document> playerMessagesCollection;
    private final MongoCollection<Document> playerReportsCollection;

    public MongoManager(MongoDatabase db) {
        this.playerCollection = db.getCollection("player_data");
        playerCollection.createIndex(new Document("level", -1));

        this.playerMessagesCollection = db.getCollection("player_messages");

        playerMessagesCollection.createIndex(new Document("receiver", 1).append("sender", 1));
        playerMessagesCollection.createIndex(new Document("timestamp", -1));

        this.playerReportsCollection = db.getCollection("player_reports");

        playerReportsCollection.createIndex(new Document("status", 1).append("timestamp", -1));
    }

    public void insertPlayerData(UUID uuid, String name, int level) {
        Document doc = new Document("uuid", uuid.toString())
                .append("name", name)
                .append("level", level)
                .append("last_login", System.currentTimeMillis());

        playerCollection.insertOne(doc);
    }

    public void savePlayerData(UUID uuid, String name) {
        Document doc = new Document("uuid", uuid.toString())
                .append("name", name)
                .append("level", 1)
                .append("level-xp", 0L)
                .append("kills", 0)
                .append("deaths", 0)
                .append("homes", new ArrayList<Document>())
                .append("last_login", System.currentTimeMillis());

        playerCollection.replaceOne(
                Filters.eq("uuid", uuid.toString()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    public void updatePlayerDataField(UUID playerUUID, String key, Object value) {
        playerCollection.updateOne(
                Filters.eq("uuid", playerUUID.toString()),
                Updates.set(key, value)
        );
    }

    public void updatePlayerDataFieldOrCreateIfAbsent(UUID playerUUID, String key, Object value) {
        playerCollection.updateOne(
                Filters.eq("uuid", playerUUID.toString()),
                Updates.set(key, value),
                new UpdateOptions().upsert(true)
        );
    }

    public Document getPlayerData(UUID uuid) {
        return playerCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void sendMessage(UUID sender, UUID receiver, String message) {
        String safeMessage = StringEscapeUtils.escapeHtml4(message);

        Document msg = new Document("sender", sender.toString())
                .append("receiver", receiver.toString())
                .append("message", safeMessage)
                .append("timestamp", System.currentTimeMillis())
                .append("read", false); // Default to unread

        playerMessagesCollection.insertOne(msg);
    }

    public List<Document> getFullConversation(UUID playerA, UUID playerB) {
        String idA = playerA.toString();
        String idB = playerB.toString();

        // Filter: (Sender A AND Receiver B) OR (Sender B AND Receiver A)
        return playerMessagesCollection.find(
                        Filters.or(
                                Filters.and(Filters.eq("sender", idA), Filters.eq("receiver", idB)),
                                Filters.and(Filters.eq("sender", idB), Filters.eq("receiver", idA))
                        )
                ).sort(Sorts.ascending("timestamp")) // Keeps messages in order
                .into(new ArrayList<>());
    }

    public List<Document> getUnreadMessages(UUID receiver) {
        return playerMessagesCollection .find(Filters.and(
                Filters.eq("receiver", receiver.toString()),
                Filters.eq("read", false)
        )).into(new ArrayList<>());
    }

    // 3. Mark all messages from a specific sender to a receiver as read
    public void markMessagesAsRead(UUID receiver, UUID sender) {
        playerMessagesCollection.updateMany(
                Filters.and(
                        Filters.eq("receiver", receiver.toString()),
                        Filters.eq("sender", sender.toString()),
                        Filters.eq("read", false)
                ),
                Updates.set("read", true)
        );
    }

    public List<String> getRecentChatPartners(UUID playerUUID) {
        String id = playerUUID.toString();

        // Get all unique senders who messaged the player
        List<String> partners = playerMessagesCollection.distinct("sender",
                Filters.eq("receiver", id), String.class).into(new ArrayList<>());

        // Get all unique receivers the player messaged
        List<String> sentTo = playerMessagesCollection.distinct("receiver",
                Filters.eq("sender", id), String.class).into(new ArrayList<>());

        // Combine and remove duplicates
        for (String uuid : sentTo) {
            if (!partners.contains(uuid)) partners.add(uuid);
        }
        return partners;
    }

    // Create a new report
    public void createReport(UUID reporter, UUID reportedPlayer, String reason) {
        Document report = new Document("reporter", reporter.toString())
                .append("reported", reportedPlayer.toString())
                .append("reason", reason)
                .append("timestamp", System.currentTimeMillis())
                .append("status", "OPEN"); // Track if staff has handled it

        playerReportsCollection.insertOne(report);
    }

    // Get all open reports for staff to review
    public List<Document> getOpenReports() {
        return playerReportsCollection.find(Filters.eq("status", "OPEN"))
                .sort(Sorts.descending("timestamp"))
                .into(new ArrayList<>());
    }

    // Close a report once a staff member handles it
    public void resolveReport(UUID reportedPlayer, String staffName) {
        playerReportsCollection.updateMany(
                Filters.and(
                        Filters.eq("reported", reportedPlayer.toString()),
                        Filters.eq("status", "OPEN")
                ),
                Updates.combine(
                        Updates.set("status", "RESOLVED"),
                        Updates.set("resolved_by", staffName),
                        Updates.set("resolved_at", System.currentTimeMillis())
                )
        );
    }

    public void deleteOldMessages(long days) {
        long threshold = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L);
        playerMessagesCollection.deleteMany(Filters.lt("timestamp", threshold));
    }

    public List<Document> getTopTenByLevel() {
        return playerCollection.find()
                .sort(Sorts.descending("level"))
                .limit(10)
                .into(new ArrayList<>());
    }
}
