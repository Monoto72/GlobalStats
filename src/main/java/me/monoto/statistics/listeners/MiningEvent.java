package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class MiningEvent implements Listener {

    public MiningEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId());
            stats.setMinedBlocks(stats.getMinedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setMinedBlocks(StatisticsManager.getGlobalStatistics().getMinedBlocks() + 1);
        }
    }
}
