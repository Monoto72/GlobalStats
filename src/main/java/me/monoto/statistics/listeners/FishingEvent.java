package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class FishingEvent implements Listener {

    public FishingEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerFishEvent event) {
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) != null) {
            if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId());
                stats.setFishedFish(stats.getFishedFish() + 1);

                StatisticsManager.getGlobalStatistics().setFishedFish(StatisticsManager.getGlobalStatistics().getFishedFish() + 1);
            }
        }
    }
}
