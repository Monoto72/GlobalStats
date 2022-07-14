package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class LeaveEvent implements Listener {

    public LeaveEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            DatabaseManager.updatePlayer(event.getPlayer().getUniqueId());
            StatisticsManager.getPlayerStatistics().remove(event.getPlayer().getUniqueId());
        }
    }
}
