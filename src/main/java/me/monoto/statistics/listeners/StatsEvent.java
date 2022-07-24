package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class StatsEvent implements Listener {

    public StatsEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void fishing(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
                stats.setFishedFish(stats.getFishedFish() + 1);

                StatisticsManager.getGlobalStatistics().setFishedFish(StatisticsManager.getGlobalStatistics().getFishedFish() + 1);
            }
        }
    }

    @EventHandler
    public void breaking(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            stats.setMinedBlocks(stats.getMinedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setMinedBlocks(StatisticsManager.getGlobalStatistics().getMinedBlocks() + 1);
        }
    }

    @EventHandler
    public void killing(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
                PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
                stats.setMobsKilled(stats.getMobsKilled() + 1);

                StatisticsManager.getGlobalStatistics().setMobsKilled(StatisticsManager.getGlobalStatistics().getMobsKilled() + 1);
            }
        }
    }

    @EventHandler
    public void moving(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());

            if (stats.getTraversedBlocks() <= StatisticsManager.getTotalBlocksTraversed(player)) {
                stats.setTraversedBlocks(StatisticsManager.getTotalBlocksTraversed(player));
            }
        }
    }

    @EventHandler
    public void placing(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (StatisticsManager.getPlayerStatistics().get(player.getUniqueId()) != null) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            stats.setPlacedBlocks(stats.getPlacedBlocks() + 1);

            StatisticsManager.getGlobalStatistics().setPlacedBlocks(StatisticsManager.getGlobalStatistics().getPlacedBlocks() + 1);
        }
    }
}
