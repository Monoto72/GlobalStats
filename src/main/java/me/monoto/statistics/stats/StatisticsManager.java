package me.monoto.statistics.stats;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsManager {
    private static GlobalStatistics globalStatistics;
    public static HashMap<UUID, PlayerStatistics> offlinePlayerStatistics = new HashMap<>();
    public static HashMap<UUID, PlayerStatistics> playerStatistics = new HashMap<>();
    public static HashMap<String, LinkedHashMap<String, Integer>> topThreeStatistics = new HashMap<>();

    public static void setGlobalStatistics(int fished, int mined, int killed, int placed, int traversed, int harvestedCrops) {
        GlobalStatistics stats = new GlobalStatistics();

        stats.setFishedFish(fished);
        stats.setMinedBlocks(mined);
        stats.setMobsKilled(killed);
        stats.setPlacedBlocks(placed);
        stats.setTraversedBlocks(traversed);
        stats.setHarvestedCrops(harvestedCrops);

        globalStatistics = stats;
    }

    public static void setPlayerStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed, int harvestedCrops) {
        setStatistics(uuid, name, fished, mined, killed, placed, traversed, harvestedCrops, playerStatistics);
    }

    public static void setOfflinePlayerStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed, int harvestedCrops) {
        setStatistics(uuid, name, fished, mined, killed, placed, traversed, harvestedCrops, offlinePlayerStatistics);
    }

    private static void setStatistics(UUID uuid, String name, int fished, int mined, int killed, int placed, int traversed, int harvestedCrops, HashMap<UUID, PlayerStatistics> hashmap) {
        PlayerStatistics stats = new PlayerStatistics();

        stats.setPlayerUUID(uuid);
        stats.setPlayerName(name);
        stats.setFishedFish(fished);
        stats.setMinedBlocks(mined);
        stats.setMobsKilled(killed);
        stats.setPlacedBlocks(placed);
        stats.setTraversedBlocks(traversed);
        stats.setHarvestedCrops(harvestedCrops);

        hashmap.put(stats.getPlayerUUID(), stats);
    }

    public static void setTopFiveStatistics(HashMap<String, LinkedHashMap<String, Integer>> topThree) {
        topThreeStatistics = topThree;
    }

    public static GlobalStatistics getGlobalStatistics() {
        return globalStatistics;
    }

    public static HashMap<UUID, PlayerStatistics> getPlayerStatistics() {
        return playerStatistics;
    }

    public static HashMap<UUID, PlayerStatistics> getOfflinePlayerStatistics() {
        return offlinePlayerStatistics;
    }

    public static HashMap<String, LinkedHashMap<String, Integer>> getTopThreeStatistics() {
        return topThreeStatistics;
    }

    public static int getTotalBlocksTraversed(OfflinePlayer player) {
        ArrayList<Statistic> statistics = new ArrayList<>(Arrays.asList(Statistic.WALK_ONE_CM, Statistic.SPRINT_ONE_CM, Statistic.SWIM_ONE_CM, Statistic.WALK_ON_WATER_ONE_CM, Statistic.WALK_UNDER_WATER_ONE_CM, Statistic.CROUCH_ONE_CM, Statistic.BOAT_ONE_CM, Statistic.HORSE_ONE_CM, Statistic.MINECART_ONE_CM, Statistic.PIG_ONE_CM, Statistic.STRIDER_ONE_CM));
        AtomicInteger value = new AtomicInteger();

        statistics.forEach(statistic -> value.addAndGet((int) Math.floor(player.getStatistic(statistic)) / 100));
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

    public static int getHarvestedCrops(UUID playerUUID) {
        PlayerStatistics stats = (PlayerStatistics) getOfflinePlayerStatistics().get(playerUUID);
        if (stats != null) {
            return stats.getHarvestedCrops();
        }
        return 0;
    }

    public static List<Map.Entry<UUID, PlayerStatistics>> getTopStatistics(String type, int limit) {
        return getPlayerStatistics().entrySet().stream()
                .sorted((a, b) -> {
                    PlayerStatistics statA = (PlayerStatistics) a.getValue();
                    PlayerStatistics statB = (PlayerStatistics) b.getValue();

                    int compare;
                    switch (type.toLowerCase()) {
                        case "fishing" -> compare = Integer.compare(statB.getFishedFish(), statA.getFishedFish());
                        case "mining" -> compare = Integer.compare(statB.getMinedBlocks(), statA.getMinedBlocks());
                        case "killing" -> compare = Integer.compare(statB.getMobsKilled(), statA.getMobsKilled());
                        case "travelling" -> compare = Long.compare(statB.getTraversedBlocks(), statA.getTraversedBlocks());
                        case "placing" -> compare = Integer.compare(statB.getPlacedBlocks(), statA.getPlacedBlocks());
                        case "crops" -> compare = Integer.compare(statB.getHarvestedCrops(), statA.getHarvestedCrops());
                        default -> compare = 0;
                    }

                    return compare;
                })
                .limit(limit)
                .toList();
    }
}
