package com.desertUo.messaagesystem;

import com.desertUo.DesertUo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.UUID;

public class MessageUtils {
    private final MongoCollection<Document> messageCollection;


    public MessageUtils(DesertUo plugin) {
        this.messageCollection = plugin.getMongoDatabase()
                .getCollection("player_messages");
    }

    public void saveMessage(UUID sender, UUID recipient, String content) {
        PlayerMessage msg = new PlayerMessage(sender, recipient, content);
        messageCollection.insertOne(msg.toDocument());
    }

    public void updateStatus(ObjectId messageId, PlayerMessage.MessageStatus newStatus) {
        messageCollection.updateOne(
                Filters.eq("_id", messageId),
                Updates.set("status", newStatus.name())
        );
    }

    public void markAllAsRead(UUID recipient) {
        messageCollection.updateMany(
                Filters.and(
                        Filters.eq("receiver", recipient.toString()),
                        Filters.eq("status", PlayerMessage.MessageStatus.UNREAD.name())
                ),
                Updates.set("status", PlayerMessage.MessageStatus.READ.name())
        );
    }

    public void deleteMessage(ObjectId messageId) {
        messageCollection.deleteOne(Filters.eq("_id", messageId));
    }
}
