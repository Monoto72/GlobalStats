package me.monoto.statistics;

import com.google.gson.Gson;
import me.monoto.statistics.commands.CommandBase;
import me.monoto.statistics.database.DatabaseClass;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.listeners.*;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.BlockCache;
import me.monoto.statistics.utils.LanguageManager;
import me.monoto.statistics.utils.PlaceholderManager;
import me.monoto.statistics.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Statistics extends JavaPlugin {
    private static Statistics instance;
    private StatisticsAPI statisticsAPI;

    private String newVersion;
    private YamlConfiguration langFile;

    public DatabaseClass database;
    public Metrics metric;

    public static Statistics getInstance() {
        return instance;
    }

    public Statistics() {
        instance = this;
    }

    @Override
    public void onEnable() {
        BlockCache.initAsync()
                .thenAccept(cache -> getLogger().info("Cache ready: "
                        + cache.placeables().size() + " placeables loaded"))
                .exceptionally(err -> {
                    getLogger().severe("Failed to build BlockCache: " + err.getMessage());
                    return null;
                });

        this.statisticsAPI = new StatisticsAPIImpl(this);

        saveDefaultConfig();
        int bstatID = 15621;
        int spigotID = 103379;

        // Load the database configuration
        boolean useSQLite = getConfig().getBoolean("useSQLite", true);

        if (useSQLite) {
            // Only initialize SQLite
            this.database = new DatabaseClass(this, true, null, null, null);
        } else {
            // Check if MySQL or other DB configuration is provided
            String dbUrl = getConfig().getString("database.url");
            String dbUser = getConfig().getString("database.username");
            String dbPassword = getConfig().getString("database.password");

            if (dbUrl == null || dbUrl.isEmpty() || dbUser == null || dbUser.isEmpty() || dbPassword == null || dbPassword.isEmpty()) {
                getLogger().severe("Configuration error: Database URL, username, and password must be provided when not using SQLite.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Initialize with MySQL or other DB
            this.database = new DatabaseClass(this, false, dbUrl, dbUser, dbPassword);
        }

        this.metric = new Metrics(this, bstatID);
        setLanguage(new LanguageManager(this).getFileConfig());

        new RouteEvent(this);
        new StatsEvent(this);

        new CommandBase(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PlaceholderManager().register();

        // Update checker
        if (getConfig().getBoolean("check-for-updates", true)) {
            new UpdateChecker(this, spigotID).getLatestVersion(version -> {
                if (!Objects.equals(getDescription().getVersion(), version)) {
                    getLogger().info("GlobalStats v" + version + " is out! Download it at: https://www.spigotmc.org/resources/globalstats.103379/");
                    setVersion(version);
                } else {
                    getLogger().info("No new version available");
                }
            });
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                getServer().getOnlinePlayers().forEach(player -> DatabaseManager.updatePlayer(player.getUniqueId()));
                DatabaseManager.getAllStatistics();
                DatabaseManager.getTopFiveStatistics();
            }
        }, 1L, 20L * 300); // 300 seconds (5 minutes)

    }

    @Override
    public void onDisable() {
        StatisticsManager.getPlayerStatistics().forEach((key, value) -> {
            PlayerStatistics stats = (PlayerStatistics) value;
            DatabaseManager.updatePlayer(stats.getPlayerUUID());
        });
        getLogger().info(" has successfully shut down, and has saved " + StatisticsManager.getPlayerStatistics().size() + " players data.");
    }

    public String getVersion() {
        return this.newVersion;
    }

    public void setVersion(String value) {
        this.newVersion = value;
    }

    public YamlConfiguration getLanguage() {
        return langFile;
    }

    public void setLanguage(YamlConfiguration value) {
        this.langFile = value;
    }

    public StatisticsAPI  getStatisticsAPI() {
        return statisticsAPI;
    }
}
