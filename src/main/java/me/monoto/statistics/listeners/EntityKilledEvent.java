package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class EntityKilledEvent implements Listener {

    public EntityKilledEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
                stats.setMobsKilled(stats.getMobsKilled() + 1);

                StatisticsManager.getGlobalStatistics().setMobsKilled(StatisticsManager.getGlobalStatistics().getMobsKilled() + 1);
            }
        }
    }
}


