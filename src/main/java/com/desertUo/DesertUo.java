package com.desertUo;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.desertUo.Utils.ConfigAccessor;
import com.desertUo.commands.*;
import com.desertUo.customobjects.ScoreboardCO;
import com.desertUo.listeners.*;
import com.desertUo.managers.CustomItemManager;
import com.desertUo.managers.MongoManager;
import com.desertUo.messaagesystem.MessageUtils;
import com.desertUo.players.PlayerProfileCO;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class DesertUo extends JavaPlugin {
    public HashSet<Player> playersToggledFlight = new HashSet<>();

    private LuckPerms lpApi;
    private ProtocolManager protocolManager;

    private Economy econ;

    private CoreProtectAPI coreProtect;

    public LuckPerms getLpApi() {
        return this.lpApi;
    }
    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    private static DesertUo desertUoInstance;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoManager mongoManager;

    private MessageUtils messageUtils;


    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoManager getMongoManager() {
        return this.mongoManager;
    }

    public MessageUtils getMessageUtils() {
        return this.messageUtils;
    }

    private CustomItemManager customItemManager;

    public CustomItemManager getCustomItemManager() {
        return this.customItemManager;
    }

    private BukkitTask updatePlayerScoreboards;

    private ConfigAccessor messagesCA;
    public FileConfiguration getMessagesConfig() {
        return this.messagesCA.getConfig();
    }

    public Economy getEconomy() {
        return econ;
    }

    public CoreProtectAPI getCoreProtect() {
        return this.coreProtect;
    }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        desertUoInstance = this;

        messagesCA = new ConfigAccessor(this, "messages.yml");

        messagesCA.saveDefaultConfig();

        saveDefaultConfig();

        RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(lpProvider != null) {
            lpApi = lpProvider.getProvider();
        }

        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String database_uri = this.getConfig().getString("database-uri", "mongodb://localhost:27017");
        mongoClient = MongoClients.create(database_uri);

        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()) // Crucial for POJOs/Enums
        );

        setupCoreProtect();

        mongoDatabase = mongoClient.getDatabase("desertuo_servers").withCodecRegistry(pojoCodecRegistry);

        this.getLogger().info("Successfully connected to MongoDB! DB: " + mongoDatabase.getName());

        this.mongoManager = new MongoManager(mongoDatabase);
        getLogger().info("MongoDB Manager Ready!");

        this.messageUtils = new MessageUtils(this);

        this.customItemManager = new CustomItemManager(this);

        /* Events listener registration */
        this.getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerAsyncChatListener(), this);

        this.getServer().getPluginManager().registerEvents(new PingListener(), this);

        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryActionsListener(), this);

        this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        this.getServer().getPluginManager().registerEvents(new PlayerBreakBlockListener(), this);

        /* Commands registration */
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(FlyCommand.NAME, FlyCommand.DESCRIPTION, new FlyCommand());
            commands.registrar().register(SpawnCommand.NAME, SpawnCommand.DESCRIPTION, new SpawnCommand());
            commands.registrar().register(TestParticlesCommand.NAME, TestParticlesCommand.DESCRIPTION, TestParticlesCommand.ALIASES, new TestParticlesCommand());
            commands.registrar().register(ProfileCommand.NAME, ProfileCommand.DESCRIPTION, new ProfileCommand());
            commands.registrar().register(BroadcastCommand.NAME, BroadcastCommand.DESCRIPTION, BroadcastCommand.ALIASES, new BroadcastCommand());
            commands.registrar().register(ClearBroadcastCommand.NAME, ClearBroadcastCommand.DESCRIPTION, ClearBroadcastCommand.ALIASES, new ClearBroadcastCommand());
            commands.registrar().register(GiveRandomItemCommand.NAME, GiveRandomItemCommand.DESCRIPTION, GiveRandomItemCommand.ALIASES, new GiveRandomItemCommand());
            commands.registrar().register(HomeCommand.NAME, HomeCommand.DESCRIPTION, new HomeCommand());
            commands.registrar().register(SetHomeCommand.NAME, SetHomeCommand.DESCRIPTION, new SetHomeCommand());
            commands.registrar().register(DelHomeCommand.NAME, DelHomeCommand.DESCRIPTION, new DelHomeCommand());
            commands.registrar().register(HomesCommand.NAME, HomesCommand.DESCRIPTION, new HomesCommand());
            // commands.registrar().register("tpa", new TpaCommand);
            // commands.registrar().register("tpaccept", new TpaccpetCommand);
            // commands.registrar().register("tpacancel", new TpacancelCommand);
            // commands.registrar().register("tpahere", new TpahereCommand);
            commands.registrar().register(GiveCICommand.NAME, GiveCICommand.DESCRIPTION, new GiveCICommand());
            commands.registrar().register(StarterCommand.NAME, StarterCommand.DESCRIPTION, new StarterCommand());

            commands.registrar().register(HelpCommand.NAME, HelpCommand.DESCRIPTION, new HelpCommand());

            commands.registrar().register(MessageCommand.NAME, MessageCommand.DESCRIPTION, MessageCommand.ALIASES, new MessageCommand());
            commands.registrar().register(GiveXpCommand.NAME, GiveXpCommand.DESCRIPTION, GiveXpCommand.ALIASES, new GiveXpCommand());
        });

        this.getLogger().info("Plugin enabled correctly");

        updatePlayerScoreboards = getServer().getScheduler().runTaskTimer(this, ScoreboardCO.getInstance(), 0, 20);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(updatePlayerScoreboards != null && !updatePlayerScoreboards.isCancelled()) {
            updatePlayerScoreboards.cancel();
        }

        Map<UUID, PlayerProfileCO> cache = ScoreboardCO.getInstance().playerCache;
        if (!cache.isEmpty()) {
            getLogger().info("Saving " + cache.size() + " online players...");

            for (Map.Entry<UUID, PlayerProfileCO> entry : cache.entrySet()) {
                UUID uuid = entry.getKey();
                PlayerProfileCO profile = entry.getValue();
                Player player = Bukkit.getPlayer(uuid);

                Document doc = new Document("name", (player != null ? player.getName() : "Unknown"))
                        .append("level", profile.getLevel())
                        .append("level-xp", profile.getXp())
                        .append("kills", profile.getKills())
                        .append("deaths", profile.getDeaths())
                        .append("last_login", System.currentTimeMillis());

                try {
                    getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(uuid, "kills", profile.kills);
                    getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(uuid, "deaths", profile.deaths);
                    getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(uuid, "level", profile.level);
                    getMongoManager().updatePlayerDataFieldOrCreateIfAbsent(uuid, "level-xp", profile.xp);
                } catch (Exception ex) {
                    getLogger().severe("Failed to save data for " + uuid + ": " + ex.getMessage());
                }
            }
            getLogger().info("All player data saved.");
        }

        if(mongoClient != null) {
            mongoClient.close();
            getLogger().info("MongoDB connection closed safely.");
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void setupCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
        if (plugin instanceof CoreProtect && ((CoreProtect) plugin).isEnabled()) {
            this.coreProtect = ((CoreProtect) plugin).getAPI();
            if (this.coreProtect.APIVersion() < 9) {
                getLogger().warning("CoreProtect API version is too low!");
                this.coreProtect = null;
            } else {
                getLogger().info("CoreProtect API Hooked Successfully!");
            }
        }
    }

    public static DesertUo getPlugin() {
        return desertUoInstance;
    }
}
