package me.monoto.statistics.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.monoto.statistics.stats.PlayerStatistics;
import me.monoto.statistics.stats.StatisticsManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.contains("global")) {
             return switch (params) {
                case "global_fishing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getFishedFish());
                case "global_mining" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMinedBlocks());
                case "global_killing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getMobsKilled());
                case "global_travelling" -> Formatters.getDistanceFormatter(StatisticsManager.getGlobalStatistics().getTraversedBlocks());
                case "global_placing" -> String.valueOf(StatisticsManager.getGlobalStatistics().getPlacedBlocks());
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
                default -> null;
            };
        }
        return null;
    }
}
