package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class MoveEvent implements Listener {

    public MoveEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId());

            if (stats.getTraversedBlocks() <= StatisticsManager.getTotalBlocksTraversed(event.getPlayer())) {
                stats.setTraversedBlocks(StatisticsManager.getTotalBlocksTraversed(event.getPlayer()));
            }
        }
    }
}
