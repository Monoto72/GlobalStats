package me.monoto.statistics.stats;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsManager {
    private static GlobalStatistics globalStatistics;
    public static HashMap<UUID, Object> offlinePlayerStatistics = new HashMap<>();
    public static HashMap<UUID, Object> playerStatistics = new HashMap<>();

    public static void setGlobalStatistics(int fished, int mined, int killed, int placed, int traversed) {
        GlobalStatistics stats = new GlobalStatistics();

        stats.setFishedFish(fished);
        stats.setMinedBlocks(mined);
        stats.setMobsKilled(killed);
        stats.setPlacedBlocks(placed);
        stats.setTraversedBlocks(traversed);

        globalStatistics = stats;
    }

    public static void setPlayerStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed) {
        setStatistics(uuid, name, fished, mined, killed, placed, traversed, playerStatistics);
    }

    public static void setOfflinePlayerStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed) {
        setStatistics(uuid, name, fished, mined, killed, placed, traversed, offlinePlayerStatistics);
    }

    private static void setStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed, HashMap<UUID, Object> hashmap) {
        PlayerStatistics stats = new PlayerStatistics();

        stats.setPlayerUUID(uuid);
        stats.setPlayerName(name);
        stats.setFishedFish(fished);
        stats.setMinedBlocks(mined);
        stats.setMobsKilled(killed);
        stats.setPlacedBlocks(placed);
        stats.setTraversedBlocks(traversed);

        hashmap.put(stats.getPlayerUUID(), stats);
    }

    public static GlobalStatistics getGlobalStatistics() {
        return globalStatistics;
    }

    public static HashMap<UUID, Object> getPlayerStatistics() {
        return playerStatistics;
    }

    public static HashMap<UUID, Object> getOfflinePlayerStatistics() {
        return offlinePlayerStatistics;
    }

    public static int getTotalBlocksTraversed(OfflinePlayer player) {
        ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(Statistic.WALK_ONE_CM, Statistic.SPRINT_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.WALK_ON_WATER_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM, Statistic.CROUCH_ONE_CM, Statistic.BOAT_ONE_CM, Statistic.HORSE_ONE_CM, Statistic.MINECART_ONE_CM, Statistic.PIG_ONE_CM, Statistic.STRIDER_ONE_CM));
        AtomicInteger value = new AtomicInteger();

        statistics.forEach(statistic -> {
            value.addAndGet((int) Math.floor(player.getStatistic(statistic)) / 100);
        });
        return value.get();
    }

    public static int getTotalBlocks(OfflinePlayer player, Statistic statistic) {
        int count = 0;
        for (org.bukkit.Material material : Material.values()) {
            if (material.isBlock()) {
                try {
                    count += player.getStatistic(statistic, material);
                } catch (IllegalArgumentException ignored) {
                    // Catch blocks not on the Statistic list if any
                }
            }
        }
        return count;
    }
}
