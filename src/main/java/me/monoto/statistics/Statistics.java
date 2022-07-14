package me.monoto.statistics;

import me.monoto.statistics.database.DatabaseClass;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.listeners.*;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import me.monoto.statistics.utils.CommandManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.plugin.java.JavaPlugin;

public final class Statistics extends JavaPlugin {

    // public ConfigManager config;
    public DatabaseClass database;
    public Metrics metric;

    @Override
    public void onEnable() {
        int bstatID = 15621;

        // this.config = new ConfigManager(this);
        getDataFolder().mkdirs();
        this.database = new DatabaseClass(this);
        this.metric = new Metrics(this, bstatID);

        new JoinEvent(this);
        new LeaveEvent(this);
        new FishingEvent(this);
        new MiningEvent(this);
        new EntityKilledEvent(this);
        new PlacingEvent(this);
        new MoveEvent(this);

        CommandManager.setupCommandManager(this);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

                    stats.setTraversedBlocks((int) (Math.floor(player.getStatistic(Statistic.WALK_ONE_CM)) / 100));
                });
            }, 2L, 20L * 10);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                getServer().getOnlinePlayers().forEach(player -> {
                    DatabaseManager.updatePlayer(player.getUniqueId());
                });
                DatabaseManager.getAllStatistics();
            }, 5L, 20L * 300);
        }
    }

    @Override
    public void onDisable() {
        StatisticsManager.getPlayerStatistics().forEach((key, value) -> {
            PlayerStatistics stats = (PlayerStatistics) value;
            DatabaseManager.updatePlayer(stats.getPlayerUUID());
        });
    }
}
