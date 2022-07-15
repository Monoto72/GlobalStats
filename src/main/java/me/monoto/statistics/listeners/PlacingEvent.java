package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class PlacingEvent implements Listener {

    public PlacingEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId());
            stats.setPlacedBlocks(stats.getPlacedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setPlacedBlocks(StatisticsManager.getGlobalStatistics().getPlacedBlocks() + 1);
        }
    }
}
