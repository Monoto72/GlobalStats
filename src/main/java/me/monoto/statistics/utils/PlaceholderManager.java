package me.monoto.statistics.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlaceholderManager extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "globalstats";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Monoto";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.5";
    }

    public boolean canRegister() {
        return true;
    }

    public boolean persist() {
        return true;
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.startsWith("leaderboard_")) {
            String[] split = params.split("_");
            if (split.length == 4) {
                String type = split[1];  // e.g., "fishing"
                int rank;

                try {
                    rank = Integer.parseInt(split[2]) - 1; // 0-based index
                } catch (NumberFormatException e) {
                    return null;
                }

                String field = split[3];
                List<Map.Entry<UUID, PlayerStatistics>> top = StatisticsManager.getTopStatistics(type, 5);

                if (rank >= 0 && rank < top.size()) {
                    Map.Entry<UUID, PlayerStatistics> entry = top.get(rank);
                    PlayerStatistics stats = entry.getValue();

                    return switch (field.toLowerCase()) {
                        case "name" -> stats.getPlayerName();
                        case "value" -> switch (type.toLowerCase()) {
                            case "fishing" -> String.valueOf(stats.getFishedFish());
                            case "mining" -> String.valueOf(stats.getMinedBlocks());
                            case "killing" -> String.valueOf(stats.getMobsKilled());
                            case "travelling" -> Formatters.getDistanceFormatter(stats.getTraversedBlocks());
                            case "placing" -> String.valueOf(stats.getPlacedBlocks());
                            case "crops" -> String.valueOf(stats.getHarvestedCrops());
                            default -> null;
                        };
                        default -> null;
                    };
                } else {
                    return "-";
                }
            }
        }

        // Existing global and player stats
        if (params.contains("global")) {
            return switch (params) {
                case "global_fishing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getFishedFish());
                case "global_mining" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMinedBlocks());
                case "global_killing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMobsKilled());
                case "global_travelling" -> Formatters.getDistanceFormatter(StatisticsManager.getGlobalStatistics().getTraversedBlocks());
                case "global_placing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getPlacedBlocks());
                case "global_crops" -> String.valueOf(StatisticsManager.getGlobalStatistics().getHarvestedCrops());
                default -> null;
            };
        } else if (params.contains("player")) {
            PlayerStatistics stats = (PlayerStatistics) StatisticsManager.getPlayerStatistics().get(player.getUniqueId());
            return switch (params) {
                case "player_fishing" -> String.valueOf(stats.getFishedFish());
                case "player_mining" -> String.valueOf(stats.getMinedBlocks());
                case "player_killing" -> String.valueOf(stats.getMobsKilled());
                case "player_travelling" -> Formatters.getDistanceFormatter(stats.getTraversedBlocks());
                case "player_placing" -> String.valueOf(stats.getPlacedBlocks());
                case "player_crops" -> String.valueOf(stats.getHarvestedCrops());
                default -> null;
            };
        }

        return null;
    }
}
