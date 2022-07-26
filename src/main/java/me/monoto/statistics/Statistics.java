package me.monoto.statistics;

import me.monoto.statistics.database.DatabaseClass;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.listeners.*;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.CommandManager;
import me.monoto.statistics.utils.LanguageManager;
import me.monoto.statistics.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Statistics extends JavaPlugin {
    private static Statistics instance;

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
        saveDefaultConfig();
        int bstatID = 15621;
        int spigotID = 103379;

        this.database = new DatabaseClass(this);
        this.metric = new Metrics(this, bstatID);

        setLanguage(new LanguageManager(this).getFileConfig());

        new RouteEvent(this);
        new StatsEvent(this);

        CommandManager.setupCommandManager(this);

        // Update checker
        (new UpdateChecker(this, spigotID)).getLatestVersion(version -> {
            if (!Objects.equals(getDescription().getVersion(), version)) {
                getLogger().info("GlobalStats v" + version + " is out! Download it at: https://www.spigotmc.org/resources/globalstats.103379/");
                setVersion(version);
            } else {
                getLogger().info("No new version available");
            }
        });

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                getServer().getOnlinePlayers().forEach(player -> DatabaseManager.updatePlayer(player.getUniqueId()));
                DatabaseManager.getAllStatistics();
            }, 1L, 20L * 300);
        }
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
}
