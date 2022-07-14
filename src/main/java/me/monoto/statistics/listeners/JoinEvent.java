package me.monoto.statistics.listeners;

import me.monoto.statistics.Statistics;
import me.monoto.statistics.database.DatabaseManager;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getPluginManager;

public class JoinEvent implements Listener {

    public JoinEvent(Statistics main){
        getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        DatabaseManager.getPlayer(event.getPlayer().getUniqueId());
        if (StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId()) == null) {
            StatisticsManager.setPlayerStatistics(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getName(),
                    event.getPlayer().getStatistic(Statistic.FISH_CAUGHT),
                    getTotalBlocks(event.getPlayer(), Statistic.MINE_BLOCK),
                    event.getPlayer().getStatistic(Statistic.MOB_KILLS),
                    getTotalBlocks(event.getPlayer(), Statistic.USE_ITEM),
                    StatisticsManager.getTotalBlocksTraversed(event.getPlayer())
            );

            DatabaseManager.savePlayer(event.getPlayer().getUniqueId());
        }  else { // Changed name catch
            PlayerStatistics player = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(event.getPlayer().getUniqueId());

            if (player != null) {
                if (!player.getPlayerName().equals(event.getPlayer().getName())) {
                    DatabaseManager.updatePlayerName(player.getPlayerName(), player.getPlayerUUID());
                }
            }
        }
    }

    private int getTotalBlocks(Player player, Statistic statistic) {
        int count = 0;
        for (Material Material : Material.values()) {
            try {
                count += player.getStatistic(statistic, Material);
            } catch (IllegalArgumentException ignored) {
                // Catch blocks not on the Statistic list if any
            }
        }
        return count;
    }
}
